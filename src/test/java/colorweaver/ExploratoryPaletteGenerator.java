package colorweaver;

import colorweaver.tools.StringKit;
import colorweaver.tools.TrigTools;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.IntArray;

import java.io.IOException;

import static colorweaver.PaletteReducer.labRoughMetric;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class ExploratoryPaletteGenerator extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("EXTREME Palette Stuff");
        config.setWindowedMode(1000, 600);
        config.setIdleFPS(10);
        config.setResizable(false);
        new Lwjgl3Application(new ExploratoryPaletteGenerator(), config);
    }

    private static float hue(int rgba) {
        final float r = (rgba >>> 24 & 255) * 0.003921569f, g = (rgba >>> 16 & 255) * 0.003921569f,
                b = (rgba >>> 8 & 255) * 0.003921569f;//, a = (e >>> 24 & 254) / 254f;
        final float min = Math.min(Math.min(r, g), b);   //Min. value of RGB
        final float max = Math.max(Math.max(r, g), b);   //Max value of RGB
        final float delta = max - min;                           //Delta RGB value

        if (delta < 0.0001f)                     //This is a gray, no chroma...
        {
            return 0f;
        } else                                    //Chromatic data...
        {
            final float rDelta = (((max - r) / 6f) + (delta * 0.5f)) / delta;
            final float gDelta = (((max - g) / 6f) + (delta * 0.5f)) / delta;
            final float bDelta = (((max - b) / 6f) + (delta * 0.5f)) / delta;

            if (r == max) return (1f + bDelta - gDelta) % 1f;
            else if (g == max) return ((4f / 3f) + rDelta - bDelta) % 1f;
            else return ((5f / 3f) + gDelta - rDelta) % 1f;
        }
    }

    public static int difference(final int color1, final int color2) {
        // if one color is transparent and the other isn't, then this is max-different
        if (((color1 ^ color2) & 0x80) == 0x80) return 0x70000000;
        final int r1 = (color1 >>> 24), g1 = (color1 >>> 16 & 0xFF), b1 = (color1 >>> 8 & 0xFF),
                r2 = (color2 >>> 24), g2 = (color2 >>> 16 & 0xFF), b2 = (color2 >>> 8 & 0xFF),
                rmean = (r1 + r2) * 53 >> 5,
                r = r1 - r2,
                g = g1 - g2,
                b = b1 - b2,
                y = Math.max(r1, Math.max(g1, b1)) - Math.max(r2, Math.max(g2, b2));
//        return (((512 + rmean) * r * r) >> 8) + g * g + (((767 - rmean) * b * b) >> 8);
//        return (((0x580 + rmean) * r * r) >> 7) + g * g * 12 + (((0x5FF - rmean) * b * b) >> 8) + y * y * 8;
        return (((1024 + rmean) * r * r) >> 7) + g * g * 13 + (((1534 - rmean) * b * b) >> 8) + y * y * 12;
//        return (((1024 + rmean) * r * r) >> 7) + g * g * 12 + (((1534 - rmean) * b * b) >> 8) + y * y * 14;
    }

    public final double fastGaussian() {
        long a = (state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L,
                b = (state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L;
        a = (a & 0x0003FF003FF003FFL) + ((a & 0x0FFC00FFC00FFC00L) >>> 10);
        b = (b & 0x0003FF003FF003FFL) + ((b & 0x0FFC00FFC00FFC00L) >>> 10);
        a = (a & 0x000000007FF007FFL) + ((a & 0x0007FF0000000000L) >>> 40);
        b = (b & 0x000000007FF007FFL) + ((b & 0x0007FF0000000000L) >>> 40);
        return ((((a & 0x0000000000000FFFL) + ((a & 0x000000007FF00000L) >>> 20))
                - ((b & 0x0000000000000FFFL) + ((b & 0x000000007FF00000L) >>> 20))) * 0x1p-10);
    }

    public double smooth (double a) {
        return Math.sqrt(a) * a * (3 - 2 * a);
    }


    private long state = 9005L;
    
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
    
    public void create() {
        IntArray base = new IntArray(900);
        double depth;
        for (int ct = 0, i = 1, cie; ct < 900; i++, ct++) {
            while (0 == (cie = CIELABConverter.rgba8888((ct + i * 0.03) % 100.0,
                    TrigTools.sin(i * 2.6180339887498949) * (depth = TrigTools.cos(i * 10.101) * 145),
                    TrigTools.cos(i * 3.6180339887498949) * depth))) {
                i++;
            }
            base.add(cie);
        }
//        base.addAll(
//                0x000000FF,
//                0x1F1F1FFF,
//                0x3F3F3FFF,
//                0x5F5F5FFF,
//                0x7F7F7FFF,
//                0x9F9F9FFF,
//                0xBFBFBFFF,
//                0xDFDFDFFF,
//                0xFFFFFFFF);
//
//        for (int i = 20, ct = 0, cie; ct < 1000; i++, ct++) {
//            while (0 == (cie = CIELABConverter.rgba8888(vdc(5, i) * 100.0,
//                    vdc(3, i) * 256 - 128,
//                    vdc(7, i) * 256 - 128))) {
//                i++;
//            }
//            base.add(cie);
//        }


//            rr = (int)(i * i * 0xD1B54A32D192ED03L + i * 0xC13FA9A902A6328FL >>> 56);
//            gg = (int)(i * i * 0x9E3779B97F4A7C15L + i * 0xD1B54A32D192ED03L >>> 56);
//            bb = (int)(i * i * 0xC13FA9A902A6328FL + i * 0x9E3779B97F4A7C15L >>> 56);
//            rr = (int)(Math.pow((i * 0xD1B54A32D192ED03L >>> 12) * 0x1p-52, DiverRNG.randomizeDouble(i * 17L) + 0.25) * 256.0);
//            gg = (int)(Math.pow((i * 0xABC98388FB8FAC03L >>> 12) * 0x1p-52, DiverRNG.randomizeDouble(i * 37L) + 0.25) * 256.0);
//            bb = (int)(Math.pow((i * 0x8CB92BA72F3D8DD7L >>> 12) * 0x1p-52, DiverRNG.randomizeDouble(i * 47L) + 0.25) * 256.0);
//            rr = (int)(Math.pow(DiverRNG.randomizeDouble(i * 0xD1B54A32D192ED03L), DiverRNG.randomizeDouble(i * 17L) + 0.2) * 256.0);
//            gg = (int)(Math.pow(DiverRNG.randomizeDouble(i * 0xABC98388FB8FAC03L), DiverRNG.randomizeDouble(i * 37L) + 0.2) * 256.0);
//            bb = (int)(Math.pow(DiverRNG.randomizeDouble(i * 0x8CB92BA72F3D8DD7L), DiverRNG.randomizeDouble(i * 47L) + 0.2) * 256.0);

//            base.add((int)DiverRNG.randomize(i) | 0xFF);
//            rr = (int)(i * 0xD1B54A32D192ED03L >>> 32 & 0xFF000000L);
//            gg = (int)(i * 0xABC98388FB8FAC03L >>> 40 & 0x00FF0000L);
//            bb = (int)(i * 0x8CB92BA72F3D8DD7L >>> 48 & 0x0000FF00L);
//            base.add(rr | gg | bb | 0xFF);
            
//        for (int r = 0, rr = 0; r < 10; r++) {
//            rr = r * 32 - (r >>> 3) << 24;
//            for (int g = 0, gg = 0; g < 10; g++) {
//                gg = g * 32 - (g >>> 3) << 16;
//                for (int b = 0, bb = 0; b < 10; b++) {
//                    bb = b * 32 - (b >>> 3) << 8;
//                    base.add(rr | gg | bb | 0xFF);
//                }
//            }
//        }
        int[] BIG_PALETTE = new int[256];
//        int[] BIG_PALETTE = Coloring.TWIRL256;
//        while (base.size < -1) {
        while (base.size > 63) {
            System.out.println(base.size);
            if(base.size == 255)
                System.arraycopy(base.items, 0, BIG_PALETTE, 1, 255);
            int ca = 0, cb = 1, cc, idx, color1, color2;
//            int t, d = 0xFFFFFFF;
            double t, d = 0x1p500;
            OUTER:
            for (int i = 0; i < base.size; i++) {
                color1 = base.get(i);
//                lab1.fromRGBA(base.get(i));
                for (int j = i + 1; j < base.size; j++) {
                    color2 = base.get(j);
//                    lab2.fromRGBA(base.get(j));
//                    if ((t = difference(color1, color2)) < d) {
                    if ((t = labRoughMetric.difference(color1, color2)) < d) {
                        d = t;
                        ca = i;
                        cb = j;
                        if(d <= 0)
                            break OUTER;
                    }
                }
            }
            idx = cb;
            cc = base.get(ca);
            cb = base.get(cb);
            int ra = (cc >>> 24), ga = (cc >>> 16 & 0xFF), ba = (cc >>> 8 & 0xFF),
                    rb = (cb >>> 24), gb = (cb >>> 16 & 0xFF), bb = (cb >>> 8 & 0xFF);

            //base.set(ca, (ra * ra + ga * ga + ba * ba >= rb * rb + gb * gb + bb * bb)
            //        ? ra << 24 | ga << 16 | ba << 8 | 0xFF
            //        : rb << 24 | gb << 16 | bb << 8 | 0xFF);
            
            //base.set(ca, 
            //          (Math.max(ra, rb) + 1 >> 1) + (ra + rb >> 2) << 24 
            //        | (Math.max(ga, gb) + 1 >> 1) + (ga + gb >> 2) << 16 
            //        | (Math.max(ba, bb) + 1 >> 1) + (ba + bb >> 2) << 8 
            //        | 0xFF);

//            base.set(ca, Math.max(ra, rb) << 24 | Math.max(ga, gb) << 16 | Math.max(ba, bb) << 8 | 0xFF);

            base.set(ca,
                    (ra + rb + 1 << 23 & 0xFF000000)
                            | (ga + gb + 1 << 15 & 0xFF0000)
                            | (ba + bb + 1 << 7 & 0xFF00)
                            | 0xFF);
            base.removeIndex(idx);
        }
        base.insert(0, 0);
        int[] PALETTE = base.toArray();

        // used for NonUniform256
//        int[] PALETTE = new int[256];
//        PALETTE[1] = 0x000000FF;
//        PALETTE[2] = 0x1F1F1FFF;
//        PALETTE[3] = 0x3F3F3FFF;
//        PALETTE[4] = 0x5F5F5FFF;
//        PALETTE[5] = 0x7F7F7FFF;
//        PALETTE[6] = 0x9F9F9FFF;
//        PALETTE[7] = 0xBFBFBFFF;
//        PALETTE[8] = 0xDFDFDFFF;
//        PALETTE[9] = 0xFFFFFFFF;
//        int idx = 10;
//        for (int n = 21; idx < 256; n++, idx++) {
//            while(0 == (PALETTE[idx] = CIELABConverter.rgba8888(vdc(5, n) * 100.0,
//                    vdc(7, n) * 254 - 127,
//                    vdc(3, n) * 254 - 127))){
//                n++;
//            }
//        }

//        int[] PALETTE = 
//                new int[64];
//        {
//            int i = 1;
//            for (int j = 0; j < 9; j++) {
//                int v = 256 - (8-j) * (8-j) * 4 + (-j >> 31);
//                PALETTE[i++] = v << 24 | v << 16 | v << 8 | 0xFF;
//            }
//            int[] rgb = {0, 0, 0};
//            for (int sel = 0; sel < 3; sel++) {
//                int o1 = (sel + 1) % 3, o2 = (sel + 2) % 3;
//                for (int j = 0; j < 9; j++) {
//                    if((j & 1) == 0)
//                    {
//                        rgb[sel] = MathUtils.clamp(j * 60 + 50, 0, 255);
//                        rgb[o1] = rgb[o2] = MathUtils.clamp(-100 + j * 40, 0, 240);
//                    }
//                    else
//                    {
//                        rgb[sel] = MathUtils.clamp(j * 44 + 56, 0, 255);
//                        rgb[o1] = rgb[o2] = MathUtils.clamp(-16 + j * 26, 0, 220);
//                    }
//                    PALETTE[i++] = rgb[0] << 24 | rgb[1] << 16 | rgb[2] << 8 | 0xFF;
//                }
//                for (int j = 0; j < 9; j++) {
//                    if((j & 1) == 1)
//                    {
//                        rgb[o1] = rgb[o2] = MathUtils.clamp(j * 56 + 50, 0, 255);
//                        rgb[sel] = MathUtils.clamp(-100 + j * 42, 0, 240);
//                    }
//                    else
//                    {
//                        rgb[o1] = rgb[o2] = MathUtils.clamp(j * 38 + 56, 0, 255);
//                        rgb[sel] = MathUtils.clamp(-16 + j * 32, 0, 220);
//                    }
//                    PALETTE[i++] = rgb[0] << 24 | rgb[1] << 16 | rgb[2] << 8 | 0xFF;
//                }
//            }
//            
////            int i = 1, r, g, b;
////            for (int cw : new int[] {0, -1, 1}) {
////                for (int cm : new int[]{0, -1, 1}) {
////                    for (int lu = 0; lu <= 6; lu++) {
////                        double luma, warm, mild;
////                        if ((cm | cw) == 0)
////                            luma = lu / 6.0;
////                        else
////                            luma = 0.9 - Math.pow((6.0 - lu) / 6.0, 1.125) * 0.8;
////                        if ((cm & cw) == 0) {
////                            if (cw == 1) {
////                                warm = 0.7;
////                                mild = 0.0625;
////                            } else if(cm == -1){
////                                warm = 0.25;
////                                mild = -0.65;
////                            }else {
////                                warm = cw * 0.5;
////                                mild = cm * 0.5;
////                            }
////                        } else if(cw == 1 && cm == 1) {
////                            warm = 1.0;
////                            mild = 0.75 + luma * 0.3;
////                            luma = Math.pow(luma, 0.6);
////                        }
////                        else {
////                            warm = cw;
////                            mild = cm;
////                        }
////                        if((lu & 1) == 1)
////                        {
////                            warm *= 0.7;
////                            mild *= 0.7;
////                        }
////                        r = MathUtils.clamp((int) ((luma + warm * 0.625 - mild * 0.5) * 255.5), 0, 255);
////                        g = MathUtils.clamp((int) ((luma + mild * 0.5 - warm * 0.375) * 255.5), 0, 255);
////                        b = MathUtils.clamp((int) ((luma - warm * 0.375 - mild * 0.5) * 255.5), 0, 255);
////                        PALETTE[i++] = r << 24 | g << 16 | b << 8 | 0xFF;
////
////                    }
////                }
////            }
//        }
        double luma, warm, mild, hue;
        double[] lumas = new double[PALETTE.length], warms = new double[PALETTE.length], milds = new double[PALETTE.length];
        int ctr = 1;
        int r, g, b;
        int pal;
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
                    {
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
                    mild = (g - b) / 255.0; 
                    warm = (r - b) / 255.0;                     
                    luma = (0.375 * r + 0.5 * g + 0.125 * b) / 255.0;
//                    lumas[i] = luma = MathUtils.clamp(((0.375 * r + 0.5 * g + 0.125 * b) / 255.0) 
//                            * (1.0 + (nextDouble() + nextDouble() - nextDouble() - nextDouble()) * 0.2), 0.05, 0.95);
//                    lumas[i] = luma = (curvedDouble() + curvedDouble() + curvedDouble() + curvedDouble()) * 0.25;

//                color[0] = i * (360.0 * 1.6180339887498949);
//                color[1] = Math.sqrt(1.0 - nextDouble() * nextDouble()) * 100.0;
//                color[2] = curvedDouble() * 100.0;
//                color[2] = i * (94.0 / 255.0) + 3.0;
//                System.out.println(StringKit.join(", ", color) + "  -> " + StringKit.join(", ", HSLUVConverter.hsluvToRgb(color)));

////normally this next section is used
                  r = MathUtils.clamp((int) ((luma + warm * 0.625 - mild * 0.5) * 255.5), 0, 255);
                  g = MathUtils.clamp((int) ((luma - warm * 0.375 + mild * 0.5) * 255.5), 0, 255);
                  b = MathUtils.clamp((int) ((luma - warm * 0.375 - mild * 0.5) * 255.5), 0, 255);
                  ////PALETTE[i] = r << 24 | g << 16 | b << 8 | 0xFF;
                  milds[i] = (g - b) / 255.0;
                  warms[i] = (r - b) / 255.0;
                  lumas[i] = (0.375 * r + 0.5 * g + 0.125 * b) / 255.0;
//                }//while (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255);
//                PALETTE[i++] = r << 24 |
//                        g << 16 |
//                        b << 8 | 0xFF;

//                PALETTE[i++] = (int) (MathUtils.clamp(color[0], 0.0, 1.0) * 255.5) << 24 |
//                        (int) (MathUtils.clamp(color[1], 0.0, 1.0) * 255.5) << 16 |
//                        (int) (MathUtils.clamp(color[2], 0.0, 1.0) * 255.5) << 8 | 0xFF;
            }
        }
        final double THRESHOLD = 0.011; // threshold controls the "stark-ness" of color changes; must not be negative.
        byte[] paletteMapping = new byte[1 << 16];
        int[] reverse = new int[PALETTE.length];
        byte[][] ramps = new byte[PALETTE.length][4];
        final int yLim = 63, cwLim = 31, cmLim = 31, shift1 = 6, shift2 = 11;
        for (int i = 1; i < PALETTE.length; i++) {
            reverse[i] =
                    (int) ((lumas[i]) * yLim)
                            | (int) ((warms[i] * 0.5 + 0.5) * cwLim) << shift1
                            | (int) ((milds[i] * 0.5 + 0.5) * cmLim) << shift2;
            if(paletteMapping[reverse[i]] != 0)
                System.out.println("color at index " + i + " overlaps an existing color that has index " + reverse[i] + "!");
            paletteMapping[reverse[i]] = (byte) i;
        }
        double wf, mf, yf;
        for (int cr = 0; cr <= cmLim; cr++) {
            wf = (double) cr / cmLim - 0.5;
            for (int cb = 0; cb <= cwLim; cb++) {
                mf = (double) cb / cwLim - 0.5;
                for (int y = 0; y <= yLim; y++) {
                    final int c2 = cr << shift2 | cb << shift1 | y;
                    if (paletteMapping[c2] == 0) {
                        yf = (double) y / yLim;
                        double dist = Double.POSITIVE_INFINITY;
                        for (int i = 1; i < PALETTE.length; i++) {
                            if (Math.abs(lumas[i] - yf) < 0.2f && dist > (dist = Math.min(dist, difference(lumas[i], warms[i], milds[i], yf, wf, mf))))
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
            yf = lumas[i];
            warm = warms[i];
            mild = milds[i];
            ramps[i][1] = (byte)i;//Color.rgba8888(DAWNBRINGER_AURORA[i]);
            ramps[i][0] = 9;//15;  //0xFFFFFFFF, white
            ramps[i][2] = 1;//0x010101FF, black
            ramps[i][3] = 1;//0x010101FF, black
            for (int yy = y + 2, rr = rev + 2; yy <= yLim; yy++, rr++) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], warms[idx2], milds[idx2], yf, warm, mild) > THRESHOLD) {
                    ramps[i][0] = paletteMapping[rr];
                    break;
                }
                adj = 1.0 + ((yLim + 1 >>> 1) - yy) * 0x1p-10;
                mild = MathUtils.clamp(mild * adj, -0.5, 0.5);
                warm = MathUtils.clamp(warm * adj + 0x1.8p-10, -0.5, 0.5);

//                cof = (cof + 0.5f) * 0.984375f - 0.5f;
//                cgf = (cgf - 0.5f) * 0.96875f + 0.5f;
                rr = yy
                        | (int) ((warm + 0.5) * cwLim) << shift1
                        | (int) ((mild + 0.5) * cmLim) << shift2;
            }
            warm = warms[i];
            mild = milds[i];
            for (int yy = y - 2, rr = rev - 2; yy > 0; rr--) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], warms[idx2], milds[idx2], yf, warm, mild) > THRESHOLD) {
                    ramps[i][2] = paletteMapping[rr];
                    rev = rr;
                    y = yy;
                    match = paletteMapping[rr] & 255;
                    break;
                }
                adj = 1.0 + (yy - (yLim + 1 >>> 1)) * 0x1p-10;
                mild = MathUtils.clamp(mild * adj, -0.5, 0.5);
                warm = MathUtils.clamp(warm * adj - 0x1.8p-10, -0.5, 0.5);
                rr = yy
                        | (int) ((warm + 0.5) * cwLim) << shift1
                        | (int) ((mild + 0.5) * cmLim) << shift2;

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
                    if ((idx2 = paletteMapping[rr] & 255) != match && difference(lumas[idx2], warms[idx2], milds[idx2], yf, warm, mild) > THRESHOLD) {
                        ramps[i][3] = paletteMapping[rr];
                        break;
                    }
                    adj = 1.0 + (yy - (yLim + 1 >>> 1)) * 0x1p-10;
                    mild = MathUtils.clamp(mild * adj, -0.5, 0.5);
                    warm = MathUtils.clamp(warm * adj - 0x1.8p-10, -0.5, 0.5);
                    rr = yy
                            | (int) ((warm + 0.5) * cwLim) << shift1
                            | (int) ((mild + 0.5) * cmLim) << shift2;
                }
            }
        }

        System.out.println("public static final byte[][] TWIRL_RAMPS = new byte[][]{");
        for (int i = 0; i < PALETTE.length; i++) {
            System.out.println(
                    "{ " + ramps[i][3]
                            + ", " + ramps[i][2]
                            + ", " + ramps[i][1]
                            + ", " + ramps[i][0] + " },"
            );
        }
        System.out.println("};");

        System.out.println("public static final int[][] TWIRL_RAMP_VALUES = new int[][]{");
        for (int i = 0; i < PALETTE.length; i++) {
            System.out.println("{ 0x" + StringKit.hex(PALETTE[ramps[i][3] & 255])
                    + ", 0x" + StringKit.hex(PALETTE[ramps[i][2] & 255])
                    + ", 0x" + StringKit.hex(PALETTE[ramps[i][1] & 255])
                    + ", 0x" + StringKit.hex(PALETTE[ramps[i][0] & 255]) + " },"
            );
        }
        System.out.println("};");


//        IntVLA base = new IntVLA(52 * 52 * 52);
//        base.addAll(PALETTE, 1, PALETTE.length - 1);
////        base.addAll(Coloring.AURORA, 1, 255);
////        base.addAll(0x010101FF, 0x2D2D2DFF, 0x555555FF, 0x7B7B7BFF,
////                0x9F9F9FFF, 0xC1C1C1FF, 0xE1E1E1FF, 0xFFFFFFFF);
//        
//        int[] grayscale = {0x010101FF, 0x171717FF, 0x2D2D2DFF, 0x555555FF, 0x686868FF, 0x7B7B7BFF, 0x8D8D8DFF,
//                0x9F9F9FFF, 0xB0B0B0FF, 0xC1C1C1FF, 0xD1D1D1FF, 0xE1E1E1FF, 0xF0F0F0FF, 0xFFFFFFFF};
////        int[] grayscale = {0x010101FF, 0x212121FF, 0x414141FF, 0x616161FF,
////                0x818181FF, 0xA1A1A1FF, 0xC1C1C1FF, 0xE1E1E1FF, 0xFFFFFFFF};
////        int[] grayscale = {0x010101FF, 0x414141FF,
////                0x818181FF, 0xC1C1C1FF, 0xFFFFFFFF};
////        int[] grayscale = {0x010101FF, 0x2D2D2DFF, 0x555555FF, 0x7B7B7BFF,
////                0x9F9F9FFF, 0xC1C1C1FF, 0xE1E1E1FF, 0xFFFFFFFF};
//        base.addAll(grayscale);
////        DiverRNG rng = new DiverRNG("sixty-four");
////        MiniMover64RNG rng = new MiniMover64RNG(64);
//        for (int i = 1; i <= 2240; i++) {
////            double luma = Math.pow(i * 0x1.c7p-11, 0.875), // 0 to 1, more often near 1 than near 0
//            double luma = i / 2240.0;//, mild = 0.0, warm = 0.0;// 0.0 to 1.0
//            luma = (Math.sqrt(luma) + luma) * 128.0;
//            //0xC13FA9A902A6328FL * i
//            //0x91E10DA5C79E7B1DL * i
////                    mild = ((DiverRNG.determineDouble(i) + DiverRNG.randomizeDouble(-i) - DiverRNG.randomizeDouble(123456789L - i) - DiverRNG.determineDouble(987654321L + i) + 0.5 - DiverRNG.randomizeDouble(123456789L + i)) * 0.4), // -1 to 1, curved random
////                    warm = ((DiverRNG.determineDouble(-i) + DiverRNG.randomizeDouble((i^12345L)*i) - DiverRNG.randomizeDouble((i^99999L)*i) - DiverRNG.determineDouble((987654321L - i)*i) + 0.5  - DiverRNG.randomizeDouble((123456789L - i)*i)) * 0.4); // -1 to 1, curved random
////                    mild = ((DiverRNG.determineDouble(i) + DiverRNG.randomizeDouble(-i) + DiverRNG.randomizeDouble(987654321L - i) - DiverRNG.randomizeDouble(123456789L - i) - DiverRNG.randomizeDouble(987654321L + i) - DiverRNG.determineDouble(1234567890L + i)) / 3.0), // -1 to 1, curved random
////                    warm = ((DiverRNG.determineDouble(-i) + DiverRNG.randomizeDouble((i^12345L)*i) + DiverRNG.randomizeDouble((i^54321L)*i) - DiverRNG.randomizeDouble((i^99999L)*i) - DiverRNG.randomizeDouble((987654321L - i)*i) - DiverRNG.determineDouble((1234567890L - i)*i)) / 3.0); // -1 to 1, curved random
//
////            final double v1 = fastGaussian(rng), v2 = fastGaussian(rng), v3 = fastGaussian(rng);
////            double mag = v1 * v1 + v2 * v2 + v3 * v3 + 1.0 / (1.0 - ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53) * ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53) * ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53)) - 1.0;
////            double mag = v1 * v1 + v2 * v2 + v3 * v3 + 1.0 / (1.0 - ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53) * ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53)) - 0.5;
////            double mag = v1 * v1 + v2 * v2 + v3 * v3 - 2.0 * Math.log(((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53));
////            final long t = rng.nextLong(), s = rng.nextLong(), angle = t >>> 48;
////            float mag = (((t & 0xFFFFFFL)) * 0x0.7p-24f + (0x1.9p0f - ((s & 0xFFFFFFL) * 0x1.4p-24f) * ((s >>> 40) * 0x1.4p-24f))) * 0.555555f;
////            mild = MathUtils.sin(angle) * mag;
////            warm = MathUtils.cos(angle) * mag;
////            double mag = ((t & 0xFFFFFFL) + (t >>> 40) + (s & 0xFFFFFFL) + (s >>> 40)) * 0x1p-26;
////            if (mag != 0.0) {
////                mag = 1.0 / Math.sqrt(mag);
////                mild = v1 * mag;
////                warm = v2 * mag;
////            }
//
////            double mild = (nextDouble() + nextDouble() + nextDouble() + nextDouble() + nextDouble() + nextDouble()
////                    - nextDouble() - nextDouble() - nextDouble() - nextDouble() - nextDouble() - nextDouble()) * 0.17 % 1.0, // -1 to 1, curved random
////                    warm = (nextDouble() + nextDouble() + nextDouble() + nextDouble() + nextDouble() + nextDouble()
////                            - nextDouble() - nextDouble()- nextDouble() - nextDouble() - nextDouble() - nextDouble()) * 0.17 % 1.0; // -1 to 1, curved random
//            double co = (nextDouble() + nextDouble() + nextDouble() + nextDouble() * nextDouble() + nextDouble() * nextDouble()
//                    - nextDouble() - nextDouble() - nextDouble() - nextDouble() * nextDouble() - nextDouble() * nextDouble()) * 32.0 % 128.0, // -256.0 to 256.0, curved random
//                    cg = (nextDouble() + nextDouble() + nextDouble() + nextDouble() * nextDouble() + nextDouble() * nextDouble()
//                            - nextDouble() - nextDouble()- nextDouble() - nextDouble() * nextDouble() - nextDouble() * nextDouble()) * 32.0 % 128.0; // -256.0 to 256.0, curved random
////            mild = Math.signum(mild) * Math.pow(Math.abs(mild), 1.05);
////            warm = Math.signum(warm) * Math.pow(Math.abs(warm), 0.8);
////            if (mild > 0 && warm < 0) warm += mild * 1.666;
////            else if (mild < -0.6) warm *= 0.4 - mild;
//            final double t = luma - cg;
//
////            int g = (int) ((luma + mild * 0.5) * 255);
////            int b = (int) ((luma - (warm + mild) * 0.25) * 255);
////            int r = (int) ((luma + warm * 0.5) * 255);
//            base.add(
//                    (int) MathUtils.clamp(t + co, 0.0, 255.0) << 24 |
//                            (int) MathUtils.clamp(luma + cg, 0.0, 255.0) << 16 |
//                            (int) MathUtils.clamp(t - co, 0.0, 255.0) << 8 | 0xFF);
//        }
//
////        base.addAll(Coloring.AURORA);
////        base.addAll(Colorizer.FlesurrectBonusPalette);
////        base.addAll(Coloring.VGA256);
////        base.addAll(Coloring.RINSED);
//        
//        for (int r = 0, rr = 0; r < 29; r++, rr += 0x05000000) {
//            for (int g = 0, gg = 0; g < 29; g++, gg += 0x050000) {
//                for (int b = 0, bb = 0; b < 29; b++, bb += 0x0500) {

//            idx = cb;
//            cc = base.get(DiverRNG.determine(ca * 0xC13FA9A902A6328FL + cb * 0x91E10DA5C79E7B1DL) < 0L ? ca : cb);
//            int ra = (cc >>> 24), ga = (cc >>> 16 & 0xFF), ba = (cc >>> 8 & 0xFF);
////                    maxa = Math.max(ra, Math.max(ga, ba)), mina = Math.min(ra, Math.min(ga, ba)),
////                    maxb = Math.max(rb, Math.max(gb, bb)), minb = Math.min(rb, Math.min(gb, bb));
////            if (maxa - mina > 100)
////                base.set(cb, ca);
////            else if (maxb - minb > 100)
////                base.set(cb, t);
////            else
//            base.set(ca,
//                    (ra << 24 & 0xFF000000)
//                            | (ga << 16 & 0xFF0000)
//                            | (ba << 8 & 0xFF00)
//                            | 0xFF);
//
//        base.insert(0, 0);
////        System.arraycopy(grayscale, 0, base.items, 1, grayscale.length);
//        int[] PALETTE = base.toArray();
//        
//        //// used for Uniform216 and SemiUniform256
//        // used for NonUniform256
//        PALETTE = new int[256];
//        PALETTE[1] = 0x3F3F3FFF;
//        PALETTE[2] = 0x7F7F7FFF;
//        PALETTE[3] = 0xBFBFBFFF;
//        int idx = 4;
//        for (int rr = 0; rr < 7; rr++) {
//            for (int gg = 0; gg < 9; gg++) {
//                for (int bb = 0; bb < 4; bb++) {
//                    PALETTE[idx++] = rr * 42 + (rr >> 1) << 24 | gg * 32 - (gg >> 3) << 16 | bb * 85 << 8 | 0xFF;
//                }
//            }
//        }
//////        for (int r = 0; r < 5; r++) {
//////            for (int g = 0; g < 5; g++) {
//////                for (int b = 0; b < 5; b++) {
//////                    PALETTE[idx++] = r * 60 + (1 << r) - 1 << 24 | g * 60 + (1 << g) - 1 << 16 | b * 60 + (1 << b) - 1 << 8 | 0xFF;
//////                }
//////            }
//////        }
////        IntSet is = new IntSet(256);
////        RNG rng = new RNG(new MiniMover64RNG(123456789));
////        while (idx < 256)
////        {
////            int pt = rng.next(9);
////            if(is.add(pt))
////            {
////                int r = pt & 7, g = (pt >>> 3) & 7, b = pt >>> 6;
//////                int r = pt % 5, g = (pt / 5) % 5, b = pt / 25;
//////                PALETTE[idx++] = r * 51 + 25 << 24 | g * 51 + 25 << 16 | b * 51 + 25 << 8 | 0xFF;
////                PALETTE[idx++] = r * 32 + 15 << 24 | g * 32 + 15 << 16 | b * 32 + 15 << 8 | 0xFF;
////            }
////        }
//        
//
        System.out.println("64-color: ");
        StringBuilder sb = new StringBuilder((1 + 12 * 8) * (BIG_PALETTE.length >>> 3));
        for (int i = 0; i < (PALETTE.length + 7 >>> 3); i++) {
            for (int j = 0; j < 8 && (i << 3 | j) < PALETTE.length; j++) {
                sb.append("0x").append(StringKit.hex(PALETTE[i << 3 | j])).append(", ");
            }
            sb.append('\n');
        }
        System.out.println(sb);
        sb.setLength(0);
        System.out.println("256-color: ");
        for (int i = 0; i < (BIG_PALETTE.length + 7 >>> 3); i++) {
            for (int j = 0; j < 8 && (i << 3 | j) < BIG_PALETTE.length; j++) {
                sb.append("0x").append(StringKit.hex(BIG_PALETTE[i << 3 | j])).append(", ");
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
        png8.palette = new PaletteReducer(PALETTE, labRoughMetric);
        try {
            png8.writePrecisely(Gdx.files.local("Twirl64.png"), pix, false);
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
            png8.writePrecisely(Gdx.files.local("Twirl64_GLSL.png"), p2, false);
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < BIG_PALETTE.length; i++) {
            pix.drawPixel(i, 0, BIG_PALETTE[i]);
        }
        png8.palette.exact(BIG_PALETTE, labRoughMetric);
        try {
            png8.writePrecisely(Gdx.files.local("Twirl256.png"), pix, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int red = 0; red < 32; red++) {
            for (int blu = 0; blu < 32; blu++) {
                for (int gre = 0; gre < 32; gre++) {
                    p2.drawPixel(red << 5 | blu, gre, BIG_PALETTE[png8.palette.paletteMapping[
                            ((red << 10) & 0x7C00)
                                    | ((gre << 5) & 0x3E0)
                                    | blu] & 0xFF]);
                }
            }
        }

        try {
            png8.writePrecisely(Gdx.files.local("Twirl256_GLSL.png"), p2, false);
        } catch (IOException e) {
            e.printStackTrace();
        }


//		Pixmap p2 = new Pixmap(1024, 32, Pixmap.Format.RGBA8888);
//        
//        byte[] paletteMapping = new byte[0x8000];
//		Arrays.fill(paletteMapping, (byte) 0);
//		final int plen = Math.min(256, PALETTE.length);
//		int color, c2;
//		double dist;
////		int dist;
//		for (int i = 0; i < plen; i++) {
//			color = PALETTE[i];
//			if ((color & 0x80) != 0) {
//				paletteMapping[(color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F)] = (byte) i;
//				p2.drawPixel((color >>> 22 & 0x3E0) | (color >>> 11 & 0x1F), color >>> 19 & 0x1F, color);
//
//			}
//		}
//		int rr, gg, bb, idx = 0;
//		for (int r = 0; r < 32; r++) {
//			rr = (r << 3 | r >>> 2);
//			for (int g = 0; g < 32; g++) {
//				gg = (g << 3 | g >>> 2);
//				for (int b = 0; b < 32; b++) {
//					c2 = r << 10 | g << 5 | b;
//					if (paletteMapping[c2] == 0) {
//						bb = (b << 3 | b >>> 2);
//						dist = 0x1p500;
////						dist = 0x7FFFFFFF;
//						lab1.fromRGBA(rr << 24 | gg << 16 | bb << 8 | 0xFF);
//						for (int i = 1; i < plen; i++) {
//							lab2.fromRGBA(PALETTE[i]);
//							if (dist > (dist = Math.min(dist, cielab.delta(lab1, lab2))))
//								paletteMapping[c2] = (byte) i;
//						}
//						p2.drawPixel(r << 5 | b, g, PALETTE[paletteMapping[c2] & 0xFF]);
//						System.out.println("Finished " + ++idx + " colors.");
//					}
//				}
//			}
//		}
//        StringBuilder sb = new StringBuilder(13 * 256);
//        Pixmap pix;         
//        Pixmap p2;
//
//        PNG8 png8 = new PNG8();
        png8.palette = new PaletteReducer(PALETTE, labRoughMetric);        
        int[][] TWIRL_BONUS_RAMP_VALUES = new int[256][4];
        for (int i = 1; i < PALETTE.length; i++) {
            int color = TWIRL_BONUS_RAMP_VALUES[i | 128][2] = TWIRL_BONUS_RAMP_VALUES[i][2] =
                    PALETTE[i];             
//            r = (color >>> 24);
//            g = (color >>> 16 & 0xFF);
//            b = (color >>> 8 & 0xFF);
            luma = lumas[i];
            warm = warms[i];
            mild = milds[i];
            TWIRL_BONUS_RAMP_VALUES[i | 64][1] = TWIRL_BONUS_RAMP_VALUES[i | 64][2] =
                    TWIRL_BONUS_RAMP_VALUES[i | 64][3] = color;
            TWIRL_BONUS_RAMP_VALUES[i | 192][0] = TWIRL_BONUS_RAMP_VALUES[i | 192][2] = color;
//            int co = r - b, t = b + (co >> 1), cg = g - t, y = t + (cg >> 1),
//                    yBright = y * 21 >> 4, yDim = y * 11 >> 4, yDark = y * 6 >> 4, chromO, chromG;
//            chromO = (co * 3) >> 2;
//            chromG = (cg * 3) >> 2;
//            t = yDim - (chromG >> 1);
//            g = chromG + t;
//            b = t - (chromO >> 1);
//            r = b + chromO;
            r = MathUtils.clamp((int) ((luma * 0.83f + (warm *  0.625f - mild * 0.5f) * 0.7f) * 256f), 0, 255);
            g = MathUtils.clamp((int) ((luma * 0.83f + (warm * -0.375f + mild * 0.5f) * 0.7f) * 256f), 0, 255);
            b = MathUtils.clamp((int) ((luma * 0.83f + (warm * -0.375f - mild * 0.5f) * 0.7f) * 256f), 0, 255);
            TWIRL_BONUS_RAMP_VALUES[i | 192][1] = TWIRL_BONUS_RAMP_VALUES[i | 128][1] =
                    TWIRL_BONUS_RAMP_VALUES[i | 64][0] = TWIRL_BONUS_RAMP_VALUES[i][1] =
                            MathUtils.clamp(r, 0, 255) << 24 |
                                    MathUtils.clamp(g, 0, 255) << 16 |
                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
            r = MathUtils.clamp((int) ((luma * 1.35f + (warm *  0.625f - mild * 0.5f) * 0.65f) * 256f), 0, 255);
            g = MathUtils.clamp((int) ((luma * 1.35f + (warm * -0.375f + mild * 0.5f) * 0.65f) * 256f), 0, 255);
            b = MathUtils.clamp((int) ((luma * 1.35f + (warm * -0.375f - mild * 0.5f) * 0.65f) * 256f), 0, 255);
            TWIRL_BONUS_RAMP_VALUES[i | 192][3] = TWIRL_BONUS_RAMP_VALUES[i | 128][3] =
                    TWIRL_BONUS_RAMP_VALUES[i][3] =
                            MathUtils.clamp(r, 0, 255) << 24 |
                                    MathUtils.clamp(g, 0, 255) << 16 |
                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
            r = MathUtils.clamp((int) ((luma * 0.65f + (warm *  0.625f - mild * 0.5f) * 0.8f) * 256f), 0, 255);
            g = MathUtils.clamp((int) ((luma * 0.65f + (warm * -0.375f + mild * 0.5f) * 0.8f) * 256f), 0, 255);
            b = MathUtils.clamp((int) ((luma * 0.65f + (warm * -0.375f - mild * 0.5f) * 0.8f) * 256f), 0, 255);
            TWIRL_BONUS_RAMP_VALUES[i | 128][0] = TWIRL_BONUS_RAMP_VALUES[i][0] =
                    MathUtils.clamp(r, 0, 255) << 24 |
                            MathUtils.clamp(g, 0, 255) << 16 |
                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
        }
        sb.setLength(0);
        sb.ensureCapacity(2800);
        sb.append("private static final int[][] TWIRL_BONUS_RAMP_VALUES = new int[][] {\n");
        for (int i = 0; i < 256; i++) {
            sb.append("{ 0x");
            StringKit.appendHex(sb, TWIRL_BONUS_RAMP_VALUES[i][0]);
            StringKit.appendHex(sb.append(", 0x"), TWIRL_BONUS_RAMP_VALUES[i][1]);
            StringKit.appendHex(sb.append(", 0x"), TWIRL_BONUS_RAMP_VALUES[i][2]);
            StringKit.appendHex(sb.append(", 0x"), TWIRL_BONUS_RAMP_VALUES[i][3]);
            sb.append(" },\n");

        }
        System.out.println(sb.append("};"));
        PALETTE = new int[256];
        for (int i = 0; i < 64; i++) {
            System.arraycopy(TWIRL_BONUS_RAMP_VALUES[i], 0, PALETTE, i << 2, 4);
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
        png8.palette = new PaletteReducer(PALETTE, labRoughMetric);
        try {
            png8.writePrecisely(Gdx.files.local("TwirlBonus.png"), pix, false);
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
            png8.writePrecisely(Gdx.files.local("TwirlBonus_GLSL.png"), p2, false);
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
        png8.palette = new PaletteReducer(PALETTE);
        try {
            png8.writePrecisely(Gdx.files.local("TwirlBonusMagicaVoxel.png"), pix, false);
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
