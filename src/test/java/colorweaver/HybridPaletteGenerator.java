package colorweaver;

import colorweaver.tools.StringKit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;

import java.io.IOException;

import static colorweaver.PaletteReducer.labMetric;

/**
 * Uses the approach from <a href="http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.65.2790">this paper</a>,
 * "Colour displays for categorical images" by C.A. Glasbey et al.
 * <br>
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class HybridPaletteGenerator extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("HYBRID Palette Stuff");
        config.setWindowedMode(1000, 600);
        config.setIdleFPS(10);
        config.setResizable(false);
        new Lwjgl3Application(new HybridPaletteGenerator(), config);
    }

    private long state = 99005L;

    private int next15()
    {
        return (int) ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L & 0x7FFFL);
    }

    private int nextUpDown()
    {
        return (int)((41 * (((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L) & 0xFFFFFFFFL)) >> 32) - 20;
    }

    private int nextColor()
    {
        return (int) ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L) | 0xFF;
    }
    private double nextDouble()
    {
        return ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
    }
    private double curvedDouble()
    {
        // averages about 0.6
//        return 0.1 * (nextDouble() + nextDouble() + nextDouble()
//                + nextDouble() + nextDouble() + nextDouble())
//                + 0.2 * ((1.0 - nextDouble() * nextDouble()) + (1.0 - nextDouble() * nextDouble()));
        // averages about 0.685
        return 0.25 * (0.5 * (nextDouble() + nextDouble() + nextDouble() + nextDouble()) +
                (3.0 - nextDouble() * nextDouble() - nextDouble() * nextDouble() - nextDouble() * nextDouble()));

    }
    private static double difference(double y1, double w1, double m1, double y2, double w2, double m2) {
        return (y1 - y2) * (y1 - y2) + ((w1 - w2) * (w1 - w2) + (m1 - m2) * (m1 - m2)) * 0.1625;
    }

    public final int randomSign() {
        return (int)(((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L >> 63 | 1L) & -(state & 1L));
    }

    public int[] lloyd(int[] palette, final int iterations) {
        PaletteReducer pr = new PaletteReducer(palette, labMetric);
        int[][] centroids = new int[4][palette.length];
        byte[] pm = pr.paletteMapping;
        int index, mix;
        float count;
        for (int iter = 0; iter < iterations; iter++) {
            System.out.println("Relaxation iteration #" + (iter + 1));
            for (int i = 0; i < 0x8000; i++) {
                index = pm[i] & 0xFF;
                centroids[0][index] += MathUtils.clamp((i >>> 10) + randomSign(), 0, 31);
                centroids[1][index] += MathUtils.clamp((i >>> 5 & 0x1F) + randomSign(), 0, 31);
                centroids[2][index] += MathUtils.clamp((i & 0x1F) + randomSign(), 0, 31);
                centroids[3][index]++;
            }
            for (int i = 1; i < palette.length; i++) {
                count = centroids[3][i];
                mix = MathUtils.clamp((int)(centroids[0][i] / count + 0.5f), 0, 31) << 10 |
                        MathUtils.clamp((int)(centroids[1][i] / count + 0.5f), 0, 31) << 5 |
                        MathUtils.clamp((int)(centroids[2][i] / count + 0.5f), 0, 31);
                palette[i] = CIELABConverter.puff(mix);
            }
            pr.exact(palette, labMetric);
        }
        return palette;
    }



    public int[] anneal(final double[][] lab15, int[] palette, final int iterations) {
        double temperature = 15.0;
        for (int iter = 0; iter < iterations; iter++) {
            System.out.println("Annealing iteration #" + (iter + 1));
            int successes = 0;
            for (int att = 0; att < 12000; att++) {
                int ca = 0, cb = 1, cc, idx, color1, color2;
                double t, d = 0x1p500;
                OUTER:
                for (int i = 1; i < palette.length; i++) {
                    color1 = palette[i];
//                lab1.fromRGBA(base.get(i));
                    for (int j = i + 1; j < palette.length; j++) {
                        color2 = palette[j];
                        if ((t = CIELABConverter.difference15(lab15, (color1 >>> 17 & 0x7C00) | (color1 >>> 14 & 0x3E0) | (color1 >>> 11 & 0x1F),
                                (color2 >>> 17 & 0x7C00) | (color2 >>> 14 & 0x3E0) | (color2 >>> 11 & 0x1F))) < d) {
                            d = t;
                            ca = i;
                            cb = j;
                            if (d <= 0)
                                break OUTER;
                        }
                    }
                }
                idx = cb;
                cc = palette[ca];
//                cb = palette[cb];
//            int ra = (cc >>> 24), ga = (cc >>> 16 & 0xFF), ba = (cc >>> 8 & 0xFF),
//                    rb = (cb >>> 24), gb = (cb >>> 16 & 0xFF), bb = (cb >>> 8 & 0xFF);

                //base.set(ca, (ra * ra + ga * ga + ba * ba >= rb * rb + gb * gb + bb * bb)
                //        ? ra << 24 | ga << 16 | ba << 8 | 0xFF
                //        : rb << 24 | gb << 16 | bb << 8 | 0xFF);

                //base.set(ca, 
                //          (Math.max(ra, rb) + 1 >> 1) + (ra + rb >> 2) << 24 
                //        | (Math.max(ga, gb) + 1 >> 1) + (ga + gb >> 2) << 16 
                //        | (Math.max(ba, bb) + 1 >> 1) + (ba + bb >> 2) << 8 
                //        | 0xFF);

//            base.set(ca, Math.max(ra, rb) << 24 | Math.max(ga, gb) << 16 | Math.max(ba, bb) << 8 | 0xFF);

                cb = (next15() < iter << 9) ? nextColor() :
                        MathUtils.clamp((cc >>> 24) + nextUpDown(), 0, 255) << 24 |
                                MathUtils.clamp((cc >>> 16 & 0xFF) + nextUpDown(), 0, 255) << 16 |
                                MathUtils.clamp((cc >>> 8 & 0xFF) + nextUpDown(), 0, 255) << 8 | 0xFF;
                double dn = 0x1p500;
                for (int j = 1; j < palette.length; j++) {
                    color2 = palette[j];
                    if ((t = CIELABConverter.difference15(lab15, (cb >>> 17 & 0x7C00) | (cb >>> 14 & 0x3E0) | (cb >>> 11 & 0x1F),
                            (color2 >>> 17 & 0x7C00) | (color2 >>> 14 & 0x3E0) | (color2 >>> 11 & 0x1F))) < dn) {
                        dn = t;
                        if (dn <= 0)
                            break;
                    }
                }
                if (nextDouble() < Math.exp((Math.sqrt(dn) - Math.sqrt(d)) / temperature))
                {
                    palette[(next15() & 1) == 0 ? ca : idx] = cb;
                    successes++;
                    if(successes > 1200)
                        break;
                }
//            base.set(ca,
//                    (ra + rb + 1 << 23 & 0xFF000000)
//                            | (ga + gb + 1 << 15 & 0xFF0000)
//                            | (ba + bb + 1 << 7 & 0xFF00)
//                            | 0xFF);
//            base.removeIndex(idx);
            }
            if(successes == 0) break;
            temperature *= 0.9;
        }
        return palette;
    }
    
    public static int xyzToColor(double x, double y, double z)
    {
        double r, g, b;
        x = 0.95047 * ((x > 0.2068930344229638) ? x * x * x : (x - 16.0 / 116.0) / 7.787);
        y = 1.00000 * ((y > 0.2068930344229638) ? y * y * y : (y - 16.0 / 116.0) / 7.787);
        z = 1.08883 * ((z > 0.2068930344229638) ? z * z * z : (z - 16.0 / 116.0) / 7.787);

        r = x *  3.2406 + y * -1.5372 + z * -0.4986;
        g = x * -0.9689 + y *  1.8758 + z *  0.0415;
        b = x *  0.0557 + y * -0.2040 + z *  1.0570;

        r = (r > 0.0031308) ? (1.055 * Math.pow(r, 1.0 / 2.4) - 0.055) : 12.92 * r;
        g = (g > 0.0031308) ? (1.055 * Math.pow(g, 1.0 / 2.4) - 0.055) : 12.92 * g;
        b = (b > 0.0031308) ? (1.055 * Math.pow(b, 1.0 / 2.4) - 0.055) : 12.92 * b;
        return MathUtils.clamp((int) (r * 255 + 0.5), 0, 255) << 24
                | MathUtils.clamp((int) (g * 255 + 0.5), 0, 255) << 16
                | MathUtils.clamp((int) (b * 255 + 0.5), 0, 255) << 8 | 0xFF;
    }
    
    public void create() {
        final double[][] lab15 =  CIELABConverter.makeLAB15();
//        IntSet distinct = IntSet.with(0x001000FF, 0x000018FF, 0x000029FF, 0x000042FF, 0x000052FF,
//                0x08C600FF, 0x080018FF, 0x080029FF, 0x080042FF, 0x000052FF,
//                0x108400FF, 0x100018FF, 0x080029FF, 0x080042FF, 0x000052FF,
//                0x184200FF, 0x180018FF, 0x100029FF, 0x080042FF, 0x000052FF,
//                0x181000FF, 0x184218FF, 0x108429FF, 0x08C642FF, 0x000052FF,
//
//
//                0x003100FF, 0x000029FF, 0x00004AFF, 0x000073FF, 0x000094FF,
//                0x18C600FF, 0x180029FF, 0x10004AFF, 0x108473FF, 0x084294FF,
//                0x318400FF, 0x290029FF, 0x21844AFF, 0x188473FF, 0x088494FF,
//                0x4A4200FF, 0x390029FF, 0x29004AFF, 0x180073FF, 0x08C694FF,
//                0x5A0000FF, 0x4A8429FF, 0x31004AFF, 0x218473FF, 0x080094FF,
//
//
//                0x004A00FF, 0x000039FF, 0x000073FF, 0x0000ADFF, 0x0000DEFF,
//                0x210000FF, 0x210039FF, 0x180073FF, 0x1000ADFF, 0x0842DEFF,
//                0x420000FF, 0x390039FF, 0x290073FF, 0x1800ADFF, 0x0884DEFF,
//                0x630000FF, 0x520039FF, 0x390073FF, 0x2100ADFF, 0x08C6DEFF,
//                0x841000FF, 0x6B4239FF, 0x4A8473FF, 0x29C6ADFF, 0x0800DEFF,
//
//
//                0x006B00FF, 0x000042FF, 0x000084FF, 0x0000C6FF, 0x0000FFFF,
//                0x318400FF, 0x318442FF, 0x290084FF, 0x2984C6FF, 0x2142FFFF,
//                0x5A0000FF, 0x520042FF, 0x4A0084FF, 0x4200C6FF, 0x3984FFFF,
//                0x8C8400FF, 0x848442FF, 0x730084FF, 0x6300C6FF, 0x52C6FFFF,
//                0xB51800FF, 0xA5C642FF, 0x948484FF, 0x8442C6FF, 0x6B00FFFF,
//
//
//                0x008400FF, 0x100042FF, 0x210084FF, 0x3100C6FF, 0x4231FFFF,
//                0x424200FF, 0x4A0042FF, 0x520084FF, 0x5A00C6FF, 0x63C6FFFF,
//                0x7B8400FF, 0x848442FF, 0x848484FF, 0x8400C6FF, 0x8484FFFF,
//                0xB5C600FF, 0xB50042FF, 0xAD0084FF, 0xAD00C6FF, 0xA542FFFF,
//                0xEF0000FF, 0xE78442FF, 0xD60084FF, 0xCE84C6FF, 0xBD00FFFF,
//
//
//                0x00A500FF, 0x298442FF, 0x4A0084FF, 0x7384C6FF, 0x944AFFFF,
//                0x42C600FF, 0x630042FF, 0x7B0084FF, 0x9C00C6FF, 0xB542FFFF,
//                0x848400FF, 0x9C8442FF, 0xAD8484FF, 0xBD00C6FF, 0xCE84FFFF,
//                0xC64200FF, 0xCE0042FF, 0xD60084FF, 0xDE00C6FF, 0xE7C6FFFF,
//                0xFF5A00FF, 0xFF0042FF, 0xFF0084FF, 0xFF00C6FF, 0xFF00FFFF,
//
//
//                0x00CE00FF, 0x100042FF, 0x210084FF, 0x3100C6FF, 0x4294FFFF,
//                0x42C600FF, 0x520042FF, 0x5A0084FF, 0x6B00C6FF, 0x73C6FFFF,
//                0x848400FF, 0x8C0042FF, 0x940084FF, 0x9C00C6FF, 0xA584FFFF,
//                0xC64200FF, 0xCE8442FF, 0xCE0084FF, 0xD684C6FF, 0xD642FFFF,
//                0xFF9400FF, 0xFF0042FF, 0xFF0084FF, 0xFF00C6FF, 0xFF21FFFF,
//
//
//                0x00EF00FF, 0x080042FF, 0x100084FF, 0x1800C6FF, 0x21BDFFFF,
//                0x42C600FF, 0x4A0042FF, 0x520084FF, 0x5A84C6FF, 0x5AC6FFFF,
//                0x848400FF, 0x8C8442FF, 0x8C0084FF, 0x9484C6FF, 0x9484FFFF,
//                0xC64200FF, 0xCE8442FF, 0xCE8484FF, 0xCE84C6FF, 0xCE42FFFF,
//                0xFFC600FF, 0xFF0042FF, 0xFF0084FF, 0xFF00C6FF, 0xFF84FFFF,
//
//
//                0x00FF00FF, 0x218442FF, 0x390084FF, 0x5A84C6FF, 0x73DEFFFF,
//                0x428400FF, 0x5A0042FF, 0x738484FF, 0x8C84C6FF, 0x9C42FFFF,
//                0x7B0000FF, 0x948442FF, 0x9C0084FF, 0xAD00C6FF, 0xBD84FFFF,
//                0xBD8400FF, 0xCE8442FF, 0xCE0084FF, 0xD600C6FF, 0xDEC6FFFF,
//                0xF7F700FF, 0xFF4242FF, 0xFF8484FF, 0xFFC6C6FF, 0xFFBDFFFF,
//
//
//                0xD6FF52FF, 0xCE8484FF, 0xBD00ADFF, 0xB584D6FF, 0xA5FFFFFF,
//                0xE74242FF, 0xDE0073FF, 0xD684A5FF, 0xCE00D6FF, 0xBDC6FFFF,
//                0xEF8429FF, 0xEF8463FF, 0xE7849CFF, 0xDE00CEFF, 0xD684FFFF,
//                0xF7C618FF, 0xF70052FF, 0xF78494FF, 0xF784CEFF, 0xEF42FFFF,
//                0xFFFF00FF, 0xFF0042FF, 0xFF0084FF, 0xFF00C6FF, 0xFFEFFFFF);
//        IntArray ia = distinct.iterator().toArray();
//        ia.insert(0, 0);
//        int baseLen = ia.size;
//        System.out.println("baseLen is " + baseLen);
//        int[] items = new int[256];
//        System.arraycopy(ia.items, 0, items, 0, baseLen);
//        System.out.println("items int[] has length " + items.length);
//        for (int i = baseLen; i < 256; i++) {
//            items[i] = Coloring.LAVA256[i];
//        }
        int[] PALETTE = Coloring.FLESURRECT;
        anneal(lab15, PALETTE, 50);
        lloyd(PALETTE, 20);
        double luma, xlab, zlab;
        double[] lumas = new double[PALETTE.length], xlabs = new double[PALETTE.length], zlabs = new double[PALETTE.length];
        int r, g, b;
        int pal, index;
        for (int i = 1; i < PALETTE.length; i++) {
            //if ((i & 7) == 7)
//            {
//                int ch = i << 2 | i >>> 3;
//                PALETTE[i] = ch << 24 | ch << 16 | ch << 8 | 0xFF;
//                milds[i] = warms[i] = 0.0;
//                lumas[i] = ch / 255.0;
//                ctr++;
//                i++;
            //} else {
            //do
//                hue = i * (Math.PI * 1.6180339887498949);
//                    hue = (ctr) * (Math.PI * 2.0 / 53.0);
//                    milds[i] = mild = (NumberTools.sin(hue) * (NumberTools.zigzag(ctr * 1.543) * 0.5 + 0.8));
//                    warms[i] = warm = (NumberTools.cos(hue) * (NumberTools.zigzag(0.4 + ctr * 1.611) * 0.5 + 0.8));
//                    lumas[i] = luma = curvedDouble();
            //ctr++;
            pal = PALETTE[i];//Coloring.FLESURRECT_ALT[i];
            r = pal >>> 24;
            g = pal >>> 16 & 0xFF;
            b = pal >>> 8 & 0xFF;
            index = (r >>> 3) << 10 | (g >>> 3) << 5 | b >>> 3;
            // yes, 4 3 5 ordering is correct
            lumas[i] = lab15[4][index];
            xlabs[i] = lab15[3][index];
            zlabs[i] = lab15[5][index];
            
//                    lumas[i] = luma = MathUtils.clamp(((0.375 * r + 0.5 * g + 0.125 * b) / 255.0) 
//                            * (1.0 + (nextDouble() + nextDouble() - nextDouble() - nextDouble()) * 0.2), 0.05, 0.95);
//                    lumas[i] = luma = (curvedDouble() + curvedDouble() + curvedDouble() + curvedDouble()) * 0.25;

//                color[0] = i * (360.0 * 1.6180339887498949);
//                color[1] = Math.sqrt(1.0 - nextDouble() * nextDouble()) * 100.0;
//                color[2] = curvedDouble() * 100.0;
//                color[2] = i * (94.0 / 255.0) + 3.0;
//                System.out.println(StringKit.join(", ", color) + "  -> " + StringKit.join(", ", HSLUVConverter.hsluvToRgb(color)));

////normally this next section is used
//            r = MathUtils.clamp((int) ((luma + alab * 0.625 - blab * 0.5) * 255.5), 0, 255);
//            g = MathUtils.clamp((int) ((luma - alab * 0.375 + blab * 0.5) * 255.5), 0, 255);
//            b = MathUtils.clamp((int) ((luma - alab * 0.375 - blab * 0.5) * 255.5), 0, 255);
            ////PALETTE[i] = r << 24 | g << 16 | b << 8 | 0xFF;
            //blabs[i] = (g - b) / 255.0;
            //alabs[i] = (r - b) / 255.0;
            //lumas[i] = (0.375 * r + 0.5 * g + 0.125 * b) / 255.0;
//                }//while (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255);
//                PALETTE[i++] = r << 24 |
//                        g << 16 |
//                        b << 8 | 0xFF;


        }
        final double THRESHOLD = 0.01; // threshold controls the "stark-ness" of color changes; must not be negative.
        byte[] paletteMapping = new byte[1 << 16];
        int[] reverse = new int[PALETTE.length];
        byte[][] ramps = new byte[PALETTE.length][4];
        final int yLim = 63, xLim = 31, zLim = 31, shift1 = 6, shift2 = 11;
        for (int i = 1; i < PALETTE.length; i++) {
            reverse[i] =
                    (int) ((lumas[i]) * yLim)
                            | (int) ((xlabs[i]) * xLim) << shift1
                            | (int) ((zlabs[i]) * zLim) << shift2;
            if(paletteMapping[reverse[i]] != 0)
                System.out.println("color at index " + i + " overlaps an existing color that has index " + reverse[i] + "!");
            paletteMapping[reverse[i]] = (byte) i;
        }
        double zd, xd, yd;
        for (int iz = 0; iz <= zLim; iz++) {
            zd = (double) iz / zLim - 0.5;
            for (int ix = 0; ix <= xLim; ix++) {
                xd = (double) ix / xLim - 0.5;
                for (int y = 0; y <= yLim; y++) {
                    final int c2 = iz << shift2 | ix << shift1 | y;
                    if (paletteMapping[c2] == 0) {
                        yd = (double) y / yLim;
                        double dist = Double.POSITIVE_INFINITY;
                        for (int i = 1; i < PALETTE.length; i++) {
                            if (Math.abs(lumas[i] - yd) < 0.2f && dist > (dist = Math.min(dist, difference(lumas[i], xlabs[i], zlabs[i], yd, xd, zd))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }

        double adj;
        int idx2;
        for (int i = 1; i < PALETTE.length; i++) {
            int rev = reverse[i], y = rev & yLim, match = i;
            yd = lumas[i];
            xlab = xlabs[i] - 0.5;
            zlab = zlabs[i] - 0.5;
            ramps[i][1] = (byte)i;//Color.rgba8888(DAWNBRINGER_AURORA[i]);
            ramps[i][0] = paletteMapping[0x843F];//15;  //0xFFFFFFFF, white
            ramps[i][2] = paletteMapping[0x8400];//0x010101FF, black
            ramps[i][3] = paletteMapping[0x8400];//0x010101FF, black
            for (int yy = y + 2, rr = rev + 2; yy <= yLim; yy++, rr++) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], xlabs[idx2], zlabs[idx2], yd, xlab, zlab) > THRESHOLD) {
                    ramps[i][0] = paletteMapping[rr];
                    break;
                }
                adj = 1.0 + ((yLim + 1 >>> 1) - yy) * 0x1p-10;
                zlab = MathUtils.clamp(zlab * adj, -0.5, 0.5);
                xlab = MathUtils.clamp(xlab * adj + 0x1.8p-10, -0.5, 0.5);

//                cof = (cof + 0.5f) * 0.984375f - 0.5f;
//                cgf = (cgf - 0.5f) * 0.96875f + 0.5f;
                rr = yy
                        | (int) ((xlab + 0.5) * xLim) << shift1
                        | (int) ((zlab + 0.5) * zLim) << shift2;
            }
            xlab = xlabs[i] - 0.5;
            zlab = zlabs[i] - 0.5;
            for (int yy = y - 2, rr = rev - 2; yy > 0; rr--) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], xlabs[idx2], zlabs[idx2], yd, xlab, zlab) > THRESHOLD) {
                    ramps[i][2] = paletteMapping[rr];
                    rev = rr;
                    y = yy;
                    match = paletteMapping[rr] & 255;
                    break;
                }
                adj = 1.0 + (yy - (yLim + 1 >>> 1)) * 0x1p-10;
                zlab = MathUtils.clamp(zlab * adj, -0.5, 0.5);
                xlab = MathUtils.clamp(xlab * adj, -0.5, 0.5);
//                xlab = MathUtils.clamp(xlab * adj - 0x1.8p-10, -0.5, 0.5);
                rr = yy
                        | (int) ((xlab + 0.5) * xLim) << shift1
                        | (int) ((zlab + 0.5) * zLim) << shift2;

//                cof = MathUtils.clamp(cof * 0.9375f, -0.5f, 0.5f);
//                cgf = MathUtils.clamp(cgf * 0.9375f, -0.5f, 0.5f);
//                rr = yy
//                        | (int) ((cof + 0.5f) * 63) << 7
//                        | (int) ((cgf + 0.5f) * 63) << 13;
                if (--yy == 0) {
                    match = -1;
                }
            }
            if (match >= 0) {
                for (int yy = y - 3, rr = rev - 3; yy > 0; yy--, rr--) {
                    if ((idx2 = paletteMapping[rr] & 255) != match && difference(lumas[idx2], xlabs[idx2], zlabs[idx2], yd, xlab, zlab) > THRESHOLD) {
                        ramps[i][3] = paletteMapping[rr];
                        break;
                    }
                    adj = 1.0 + (yy - (yLim + 1 >>> 1)) * 0x1p-10;
                    zlab = MathUtils.clamp(zlab * adj, -0.5, 0.5);
                    xlab = MathUtils.clamp(xlab * adj - 0x1.8p-10, -0.5, 0.5);
                    rr = yy
                            | (int) ((xlab + 0.5) * xLim) << shift1
                            | (int) ((zlab + 0.5) * zLim) << shift2;
                }
            }
        }

        System.out.println("public static final byte[][] WARD_RAMPS = new byte[][]{");
        for (int i = 0; i < PALETTE.length; i++) {
            System.out.println(
                    "{ " + ramps[i][3]
                            + ", " + ramps[i][2]
                            + ", " + ramps[i][1]
                            + ", " + ramps[i][0] + " },"
            );
        }
        System.out.println("};");

        System.out.println("public static final int[][] WARD_RAMP_VALUES = new int[][]{");
        for (int i = 0; i < PALETTE.length; i++) {
            System.out.println("{ 0x" + StringKit.hex(PALETTE[ramps[i][3] & 255])
                    + ", 0x" + StringKit.hex(PALETTE[ramps[i][2] & 255])
                    + ", 0x" + StringKit.hex(PALETTE[ramps[i][1] & 255])
                    + ", 0x" + StringKit.hex(PALETTE[ramps[i][0] & 255]) + " },"
            );
        }
        System.out.println("};");
        
        System.out.println(PALETTE.length+"-color: ");
        StringBuilder sb = new StringBuilder((1 + 12 * 8) * (PALETTE.length >>> 3));
        for (int i = 0; i < (PALETTE.length + 7 >>> 3); i++) {
            for (int j = 0; j < 8 && (i << 3 | j) < PALETTE.length; j++) {
                sb.append("0x").append(StringKit.hex(PALETTE[i << 3 | j])).append(", ");
            }
            sb.append('\n');
        }
        System.out.println(sb);
        sb.setLength(0);

        Pixmap pix = new Pixmap(256, 1, Pixmap.Format.RGBA8888);
        for (int i = 0; i < PALETTE.length; i++) {
            pix.drawPixel(i, 0, PALETTE[i]);
        }
//        for (int i = 0; i < PALETTE.length - 1; i++) {
//            pix.drawPixel(i, 0, PALETTE[i + 1]);
//        }
        //pix.drawPixel(255, 0, 0);
        PNG8 png8 = new PNG8();
        png8.palette = new PaletteReducer(PALETTE, labMetric);
        try {
            png8.writePrecisely(Gdx.files.local("Ward.png"), pix, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pixmap p2 = new Pixmap(1024, 32, Pixmap.Format.RGBA8888);
        for (int red = 0; red < 32; red++) {
            for (int blu = 0; blu < 32; blu++) {
                for (int gre = 0; gre < 32; gre++) {
                    p2.drawPixel(red << 5 | blu, gre, PALETTE[png8.palette.paletteMapping[
                            ((red << 10) & 0x7C00)
                                    | ((gre << 5) & 0x3E0)
                                    | blu] & 0xFF]);
                }
            }
        }

        try {
            png8.writePrecisely(Gdx.files.local("Ward_GLSL.png"), p2, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Gdx.app.exit();


        int[][] WARD_BONUS_RAMP_VALUES = new int[256][4];
        for (int i = 1; i < PALETTE.length; i++) {
            int color = WARD_BONUS_RAMP_VALUES[i | 128][2] = WARD_BONUS_RAMP_VALUES[i][2] =
                    PALETTE[i];
//            r = (color >>> 24);
//            g = (color >>> 16 & 0xFF);
//            b = (color >>> 8 & 0xFF);
            luma = lumas[i];
            xlab = xlabs[i];
            zlab = zlabs[i];
            WARD_BONUS_RAMP_VALUES[i | 64][1] = WARD_BONUS_RAMP_VALUES[i | 64][2] =
                    WARD_BONUS_RAMP_VALUES[i | 64][3] = color;
            WARD_BONUS_RAMP_VALUES[i | 192][0] = WARD_BONUS_RAMP_VALUES[i | 192][2] = color;
//            int co = r - b, t = b + (co >> 1), cg = g - t, y = t + (cg >> 1),
//                    yBright = y * 21 >> 4, yDim = y * 11 >> 4, yDark = y * 6 >> 4, chromO, chromG;
//            chromO = (co * 3) >> 2;
//            chromG = (cg * 3) >> 2;
//            t = yDim - (chromG >> 1);
//            g = chromG + t;
//            b = t - (chromO >> 1);
//            r = b + chromO;
//            r = MathUtils.clamp((int) ((luma * 0.83f + (xlab *  0.625f - zlab * 0.5f) * 0.7f) * 256f), 0, 255);
//            g = MathUtils.clamp((int) ((luma * 0.83f + (xlab * -0.375f + zlab * 0.5f) * 0.7f) * 256f), 0, 255);
//            b = MathUtils.clamp((int) ((luma * 0.83f + (xlab * -0.375f - zlab * 0.5f) * 0.7f) * 256f), 0, 255);
            WARD_BONUS_RAMP_VALUES[i | 192][1] = WARD_BONUS_RAMP_VALUES[i | 128][1] =
                    WARD_BONUS_RAMP_VALUES[i | 64][0] = WARD_BONUS_RAMP_VALUES[i][1] =
                            xyzToColor(xlab * 0.8, luma * 0.83, zlab * 0.8);
//            r = MathUtils.clamp((int) ((luma * 1.35f + (xlab *  0.625f - zlab * 0.5f) * 0.65f) * 256f), 0, 255);
//            g = MathUtils.clamp((int) ((luma * 1.35f + (xlab * -0.375f + zlab * 0.5f) * 0.65f) * 256f), 0, 255);
//            b = MathUtils.clamp((int) ((luma * 1.35f + (xlab * -0.375f - zlab * 0.5f) * 0.65f) * 256f), 0, 255);
            WARD_BONUS_RAMP_VALUES[i | 192][3] = WARD_BONUS_RAMP_VALUES[i | 128][3] =
                    WARD_BONUS_RAMP_VALUES[i][3] =
                            xyzToColor(xlab * 0.7, luma * 1.35, zlab * 0.7);
//            r = MathUtils.clamp((int) ((luma * 0.65f + (xlab *  0.625f - zlab * 0.5f) * 0.8f) * 256f), 0, 255);
//            g = MathUtils.clamp((int) ((luma * 0.65f + (xlab * -0.375f + zlab * 0.5f) * 0.8f) * 256f), 0, 255);
//            b = MathUtils.clamp((int) ((luma * 0.65f + (xlab * -0.375f - zlab * 0.5f) * 0.8f) * 256f), 0, 255);
            WARD_BONUS_RAMP_VALUES[i | 128][0] = WARD_BONUS_RAMP_VALUES[i][0] =
                    xyzToColor(xlab * 0.8, luma * 0.65, zlab * 0.8);
        }
        sb.setLength(0);
        sb.ensureCapacity(2800);
        sb.append("private static final int[][] WARD_BONUS_RAMP_VALUES = new int[][] {\n");
        for (int i = 0; i < 256; i++) {
            sb.append("{ 0x");
            StringKit.appendHex(sb, WARD_BONUS_RAMP_VALUES[i][0]);
            StringKit.appendHex(sb.append(", 0x"), WARD_BONUS_RAMP_VALUES[i][1]);
            StringKit.appendHex(sb.append(", 0x"), WARD_BONUS_RAMP_VALUES[i][2]);
            StringKit.appendHex(sb.append(", 0x"), WARD_BONUS_RAMP_VALUES[i][3]);
            sb.append(" },\n");

        }
        System.out.println(sb.append("};"));
        PALETTE = new int[256];
        for (int i = 0; i < 64; i++) {
            System.arraycopy(WARD_BONUS_RAMP_VALUES[i], 0, PALETTE, i << 2, 4);
        }
        sb.setLength(0);
        sb.ensureCapacity((1 + 12 * 8) * (PALETTE.length >>> 3));
        for (int i = 0; i < (PALETTE.length >>> 3); i++) {
            for (int j = 0; j < 8; j++) {
                sb.append("0x").append(StringKit.hex(PALETTE[i << 3 | j]).toUpperCase()).append(", ");
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
        sb.setLength(0);

        pix = new Pixmap(256, 1, Pixmap.Format.RGBA8888);
        for (int i = 0; i < PALETTE.length - 1; i++) {
            pix.drawPixel(i, 0, PALETTE[i + 1]);
        }
        //pix.drawPixel(255, 0, 0);
        png8.palette = new PaletteReducer(PALETTE, labMetric);
        try {
            png8.writePrecisely(Gdx.files.local("WardBonus.png"), pix, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        p2 = new Pixmap(1024, 32, Pixmap.Format.RGBA8888);
        for (int red = 0; red < 32; red++) {
            for (int blu = 0; blu < 32; blu++) {
                for (int gre = 0; gre < 32; gre++) {
                    p2.drawPixel(red << 5 | blu, gre, PALETTE[png8.palette.paletteMapping[
                            ((red << 10) & 0x7C00)
                                    | ((gre << 5) & 0x3E0)
                                    | blu] & 0xFF]);
                }
            }
        }
        try {
            png8.writePrecisely(Gdx.files.local("WardBonus_GLSL.png"), p2, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pix = new Pixmap(256, 1, Pixmap.Format.RGBA8888);
        for (int i = 1; i < 64; i++) {
//            pix.drawPixel(i-1, 0, PALETTE[i]);
            pix.drawPixel(i-1, 0, PALETTE[i << 2 | 2]);
            pix.drawPixel(i+63, 0, PALETTE[i << 2]);
            pix.drawPixel(i+127, 0, PALETTE[i << 2 | 1]);
            pix.drawPixel(i+191, 0, PALETTE[i << 2 | 3]);
        }
        png8.palette = new PaletteReducer(PALETTE, labMetric);
        try {
            png8.writePrecisely(Gdx.files.local("WardBonusMagicaVoxel.png"), pix, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        
    }

    /**
     * Van der Corput sequence for a given base; s must be greater than 0.
     *
     * @param base any prime number, with some optimizations used when base == 2
     * @param s    any int greater than 0
     * @return a sub-random float between 0f inclusive and 1f exclusive
     */
    public static float vdc(int base, int s) {
        if (base <= 2) {
            final int leading = Integer.numberOfLeadingZeros(s);
            return (Integer.reverse(s) >>> leading) / (float) (1 << (32 - leading));
        }
        int num = s % base, den = base;
        while (den <= s) {
            num *= base;
            num += (s % (den * base)) / den;
            den *= base;
        }
        return num / (float) den;
    }

    public static float vdc2_scrambled(int index) {
        int s = ((++index ^ index << 1 ^ index >> 1) & 0x7fffffff), leading = Integer.numberOfLeadingZeros(s);
        return (Integer.reverse(s) >>> leading) / (float) (1 << (32 - leading));
    }

    public static int difference2(int color1, int color2) {
        int rmean = ((color1 >>> 24 & 0xFF) + (color2 >>> 24 & 0xFF)) >> 1;
        int b = (color1 >>> 8 & 0xFF) - (color2 >>> 8 & 0xFF);
        int g = (color1 >>> 16 & 0xFF) - (color2 >>> 16 & 0xFF);
        int r = (color1 >>> 24 & 0xFF) - (color2 >>> 24 & 0xFF);
        return (((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8);
    }

    // goes in squidlib, needs squidlib display/SColor
    /*
            final float pi = 3.14159265358979323846f, offset = 0.1f,
                pi1 = pi * 0.25f + offset, pi2 = pi * 0.75f + offset, pi3 = pi * -0.75f + offset, pi4 = pi * -0.25f + offset, ph = 0.7f,//0.6180339887498948482f,
                dark = 0.26f, mid = 0.3f, light = 0.22f;
//        dark = 0.11f, mid = 0.12f, light = 0.1f;
//        System.out.printf("0x%08X, ", NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0f, 0f, 0f, 0f)));
        System.out.println("0x00000000, ");
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.15f, 0f, 0f, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.4f, 0f, 0f, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.65f, 0f, 0f, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.9f, 0f, 0f, 1f)));

        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.2f, NumberTools.cos(pi1+ph) * dark, NumberTools.sin(pi1+ph) * dark, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.25f, NumberTools.cos(pi2+ph) * dark, NumberTools.sin(pi2+ph) * dark, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.3f, NumberTools.cos(pi3+ph) * dark, NumberTools.sin(pi3+ph) * dark, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.35f, NumberTools.cos(pi4+ph) * dark, NumberTools.sin(pi4+ph) * dark, 1f)));
        System.out.println();
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.45f, NumberTools.cos(pi1) * mid, NumberTools.sin(pi1) * mid, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.5f, NumberTools.cos(pi2) * mid, NumberTools.sin(pi2) * mid, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.55f, NumberTools.cos(pi3) * mid, NumberTools.sin(pi3) * mid, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.6f, NumberTools.cos(pi4) * mid, NumberTools.sin(pi4) * mid, 1f)));

        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.7f, NumberTools.cos(pi1-ph) * light, NumberTools.sin(pi1-ph) * light, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.75f, NumberTools.cos(pi2-ph) * light, NumberTools.sin(pi2-ph) * light, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.8f, NumberTools.cos(pi3-ph) * light, NumberTools.sin(pi3-ph) * light, 1f)));
        System.out.printf("0x%08X, ", 1|NumberTools.floatToReversedIntBits(SColor.floatGetYCbCr(0.85f, NumberTools.cos(pi4-ph) * light, NumberTools.sin(pi4-ph) * light, 1f)));

     */
}
