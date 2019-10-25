package colorweaver;

import colorweaver.tools.StringKit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.io.IOException;

import static colorweaver.PaletteReducer.basicMetric;
import static colorweaver.PaletteReducer.labRoughMetric;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class GeometricPaletteGenerator extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("GEOMETRIC Palette Stuff");
        config.setWindowedMode(1000, 600);
        config.setIdleFPS(10);
        config.setResizable(false);
        new Lwjgl3Application(new GeometricPaletteGenerator(), config);
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

    public int[] lloyd(int[] palette) {
        PaletteReducer pr = new PaletteReducer(palette, basicMetric);
        int[][] centroids = new int[4][palette.length];
        byte[] pm = pr.paletteMapping;
        int index, mix;
        float count;
        for (int iter = 0; iter < 50; iter++) {
            System.out.println("Relaxation iteration #" + (iter + 1));
            for (int i = 0; i < 0x8000; i++) {
                index = pm[i] & 0xFF;
                centroids[0][index] += i >>> 10;
                centroids[1][index] += i >>> 5 & 0x1F;
                centroids[2][index] += i & 0x1F;
                centroids[3][index]++;
            }
            for (int i = 1; i < palette.length; i++) {
                count = centroids[3][i];
                mix = MathUtils.clamp((int)(centroids[0][i] / count + 0.5f), 0, 31) << 10 |
                        MathUtils.clamp((int)(centroids[1][i] / count + 0.5f), 0, 31) << 5 |
                        MathUtils.clamp((int)(centroids[2][i] / count + 0.5f), 0, 31);
                palette[i] = CIELABConverter.puff(mix);
            }
            pr.exact(palette, basicMetric);
        }
        return palette;
    }


    public void create() {
//        int[] PALETTE = lloyd(Coloring.AURORA);
//        int[] PALETTE = lloyd(Coloring.DB_ISO22);
        int[] PALETTE = Coloring.BIG_ROLLER;
//        int[] PALETTE = lloyd(Coloring.BIG_ROLLER);
        
        
//        final int[] points = {0, 75, 140, 210, 255};
//        int ctr = 1;
//        for (int r = 0; r < 5; r++) {
//            for (int g = 0; g < 5; g++) {
//                for (int b = 0; b < 5; b++) {
//                    if(((r ^ g ^ b) & 1) == 0) 
//                        PALETTE[ctr++] = points[r] << 24 | points[g] << 16 | points[b] << 8 | 0xFF;
//                }
//            }
//        }
        double luma, warm, mild;
        double[] lumas = new double[PALETTE.length], warms = new double[PALETTE.length], milds = new double[PALETTE.length];
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
                System.out.println("color at index " + i + " overlaps an existing color that has index " + paletteMapping[reverse[i]] +
                        "! Luma is " + lumas[i] + ", warm is " + warms[i] + ",  mild is " + milds[i]);
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

        System.out.println("public static final byte[][] BIG_ROLLER_RAMPS = new byte[][]{");
        for (int i = 0; i < PALETTE.length; i++) {
            System.out.println(
                    "{ " + ramps[i][3]
                            + ", " + ramps[i][2]
                            + ", " + ramps[i][1]
                            + ", " + ramps[i][0] + " },"
            );
        }
        System.out.println("};");

        System.out.println("public static final int[][] BIG_ROLLER_RAMP_VALUES = new int[][]{");
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

//        System.out.println("64-color: ");
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
        png8.palette = new PaletteReducer(PALETTE, labRoughMetric);
        try {
            png8.writePrecisely(Gdx.files.local("BigRoller.png"), pix, false);
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
            png8.writePrecisely(Gdx.files.local("BigRoller_GLSL.png"), p2, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        generatePreloadCode(png8.palette.paletteMapping);
//        
//        PaletteReducer pr2 = new PaletteReducer(PALETTE);
//        final String preloadRelaxed = "\001\001\001\001\001\001\001\027\027\027\027\027\030\030\030\030\030ßÞÞÞÞÞÞ\025\025\025\025\025\025\025\025\001\001\001\001uu\027\027\027\027\027\030\030\030\030\030óßßÞÞÞÞÞ\025\025\025\025\025\025\025\025\002\002\002\002uuuu\027\027\027\030\030\030\030óóßßßÞÞÞÞ\025\025\025\025\025ÝÝÝ\002\002\002\002uuuuôôàààààóóßßßßßÞááÝÝÝÝÝÝå"+
//                "WWWWWvvvvvvààààóËËËßßááááÝÝÝÝÝÎåWWWWWWvvvÊÊÊÊÊÊËËËËËËááááÝÝÝÎÎÎÎ²²²²²²²²hhÊÊÊÊÊËËËËËËááááÎÎÎÎÎÎÎggggggg\004\004hhhhwwwËËxxÌÌÌÌ\026\026ÎÎÎÎÎÜ"+
//                "³½½½½½½½½½hhhwwwwxxÌÌÌÌ\026\026\026\026ÜÜÜÜÜ³³³³³³½½½½½ÉÉÉÉÉÉÉÌÌÌÌÌ\026\026\026\026ÜÜÜÜÜ´´IIIIXXXVVÉÉÉÉÉÉÉÉyyyy\026\026\026\026\026ÜÜÜÜ´´´´´XXXXXXXtttttttyyyyyy\026\026\026ÚÚÚÛ"+
//                "´´´´´´XXfffffttttttÍÏÏÏÏÏÏÏ××ÚÚÚYYYYYYfffffffiiiiÍÍÍÍÏÏÏÏÏÏ×××ÚÚYYYYYYYYYffff\020iiiÍÍÍÍÍÏÏÏÐÐ××××Ú++++++++++Y\020\020\020\020\020\020\020sÍÍÍÍÐÐÐÐÐ××××"+
//                "+++++[[[¼¼¼¼¼\020\020\020\020\020\020ssssØØÐÐÐÐ×××µ[[[[[[[¼¼¼¼¼eeeee\020ssssrrØÈÈÈÈ\024\024µµµµµµµµ[¼¼¼¼e¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024µµµµµµµµUUU\\\\¾¾¾¾¾¾¾¾rrrrrÈÈÈÈÈÈ"+
//                "µµµµµµµµ»»»»»¾¾¾¾¾¾¾jjjrrrrÈÈÒÒÒººººº»»»»»»»»»»»¿¿jjjjjjrrÑÑÒÒÒÒºººººººº»»»»»»»»¿¿¿¿jjjj\021\021ÖÖÖÖÒÒºººººººººº»»»»»¿¿¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÖ"+
//                "¶¶¶¶¶¶¶¶¶,,,,^^^^^¿¿¿¿\021\021\021\021\021\021ÖÖÔÔ¶¶¶¶¶¶,,,,,,,,^^^^^^^¿\021\021\021\021\021\021ÔÔÔÔ·······,,,,,ÀÀÀÀÀ^^^ÁÁÁÇ\021\021\021\021\022\022ÔÔ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇ\022\022\022\022Å"+
//                "------------ÀÀÀÀÀÂÂÁÁÁÁÁÇÇÇÇ\022\022\022Å----------¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÇÇÇÇ\022\022\022Å-------¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÁÃÃÃÃÃ\022\022Å-¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÃÃÃÃÃÃÃÃÅ"+
//                "\001\001\001\001\001\001\027\027\027\027\027\027\030\030\030\030\030ßÞÞÞÞÞÞ\025\025\025\025\025\025\025\025\002\002\002\002uu\027\027\027\027\027\030\030\030\030\030óßßÞÞÞÞÞ\025\025\025\025\025\025\025Ý\002\002\002\002uuuu\027\027ô\030\030\030\030óóßßßÞÞÞá\025\025\025\025\025ÝÝÝ\002\002\002\002uuuuôôàààààóóßßßßßáááÝÝÝÝÝÝå"+
//                "WWWWWvvvvvvààààóËËËßßááááÝÝÝÝÝÎåWWWWWWvvvÊÊÊÊÊÊËËËËËËááááÝÝÝÎÎÎÎ²²²²²²²²hhÊÊÊÊÊËËËËËËááááÎÎÎÎÎÎÎggggggg\004\004hhhhwwwËËxxÌÌÌ\026\026\026ÎÎÎÎÜÜ"+
//                "³½½½½½½½½½hhhwwwwxxÌÌÌÌ\026\026\026\026ÜÜÜÜÜ³³³³³³½½½½½ÉÉÉÉÉÉÉÌÌÌÌÌ\026\026\026\026ÜÜÜÜÜ´IIIIIXXXVVÉÉÉÉÉÉÉÉyyyy\026\026\026\026\026ÜÜÜÜ´´´´´XXXXXXXtttttttyyyyyy\026\026\026ÚÚÚÛ"+
//                "´´´´´´XffffffttttttÍÏÏÏÏÏÏÏ××ÚÚÚYYYYYYfffffffiiiiÍÍÍÍÏÏÏÏÏÏ×××ÚÚYYYYYYYYYffff\020iiiÍÍÍÍÍÏÏÏÐÐ××××Ú++++++++++Y\020\020\020\020\020\020\020sÍÍÍÍÐÐÐÐÐ××××"+
//                "+++[[[[[¼¼¼¼¼\020\020\020\020\020\020ssssØØÐÐÐÐ×××µ[[[[[[[¼¼¼¼¼eeeee\020ssssrrØÈÈÈÈ\024\024µµµµµµµ[[¼¼¼¼e¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024µµµµµµµµUUU\\\\¾¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024"+
//                "µµµµµµµµ»»»»»¾¾¾¾¾¾¾jjjrrrrÈÈÒÒÒººººº»»»»»»»»»»»¿¿jjjjjjrrÑÑÒÒÒÒºººººººº»»»»»»»»¿¿¿¿jjjj\021\021ÖÖÖÖÒÒºººººººººº»»»»»¿¿¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÖ"+
//                "¶¶¶¶¶¶¶¶¶,,,,^^^^^¿¿¿¿\021\021\021\021\021\021ÖÖÔÔ¶¶¶¶¶¶,,,,,,,,^^^^^^^¿\021\021\021\021\021\021ÔÔÔÔ·······,,,,,ÀÀÀÀÀ^^^ÁÁÁÇ\021\021\021\021\022\022ÔÔ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇ\022\022\022\022Å"+
//                "------------ÀÀÀÀÀÂÂÁÁÁÁÁÇÇÇÇ\022\022\022Å----------¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÇÇÇÇ\022\022\022Å-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÁÃÃÃÃÃ\022\022Å¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÃÃÃÃÃÃÃÃÅ"+
//                "\001\001\001\001\001\001\027\027\027\027\027\030\030\030\030\030óßÞÞÞÞÞÞ\025\025\025\025\025\025\025\025\002\002\002uuuu\027\027\027\027\030\030\030\030\030óßßÞÞÞÞÞ\025\025\025\025\025\025\025Ý\002\002\002\002uuuu\027ôô\030\030\030àóóßßßÞÞÞá\025\025\025\025ÝÝÝå\002\002\002\002uuuuôôàààààóóßßßßßáááÝÝÝÝÝÝå"+
//                "WWWWWvvvvvvÊàààóËËËßßááááÝÝÝÝÝÎåWWWWWWvvvÊÊÊÊÊÊËËËËËËááááÝÝÎÎÎÎÎ²²²²²²²²hhÊÊÊÊÊËËËËËËááááÎÎÎÎÎÎÎggggggg\004\004hhhhwwwËxxxÌÌÌ\026\026\026ÎÎÎÎÜÜ"+
//                "³³½½½½½½½½hhhwwwwxxÌÌÌÌ\026\026\026\026ÜÜÜÜÜ³³³³³³³½½½½ÉÉÉÉÉÉÉÌÌÌÌÌ\026\026\026\026ÜÜÜÜÜIIIIIIXXVVVÉÉÉÉÉÉÉÉyyyy\026\026\026\026\026ÜÜÜÜ´´´´´XXXXXXttttttttyyyyyy\026\026\026ÚÚÚÛ"+
//                "´´´´´´XffffffttttttÍÏÏÏÏÏÏÏ××ÚÚÚYYYYYYfffffffiiiiÍÍÍÍÏÏÏÏÏÏ×××ÚÚYYYYYYYYYffffiiiiÍÍÍÍÍÏÏÏÐÐ××××Ú++++++++++Y\020\020\020\020\020\020\020sÍÍÍÍØÐÐÐÐ××××"+
//                "[[[[[[[¼¼¼¼¼¼\020\020\020\020\020sssssØØØÐÐÐ×××µ[[[[[[[¼¼¼¼¼eeeee\020ssssrrØÈÈÈÈ\024\024µµµµµµµ[[¼¼¼¼e¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024µµµµµµµµUUU\\\\¾¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024"+
//                "µµµµµµµµ»»»»»¾¾¾¾¾¾¾jjjrrrrÈÈÒÒÒººººº»»»»»»»»»»»¿¿jjjjjjrrÑÑÒÒÒÒºººººººº»»»»»»»¿¿¿¿¿jjjj\021\021ÖÖÖÖÒÒºººººººººº»»»»»¿¿¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÖ"+
//                "¶¶¶¶¶¶¶¶¶,,,,^^^^^¿¿¿¿\021\021\021\021\021\021ÖÖÔÔ¶¶¶¶¶¶,,,,,,,,^^^^^^^¿\021\021\021\021\021\021ÔÔÔÔ·······,,,,,ÀÀÀÀÀ^^^ÁÁÁÇ\021\021\021\021\022\022ÔÔ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇ\022\022\022\022Å"+
//                "------------ÀÀÀÀÀÂÂÁÁÁÁÁÇÇÇÇ\022\022\022Å---------¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÇÇÇÇ\022\022\022Å-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÁÃÃÃÃÃ\022\022Å¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÃÃÃÃÃÃÃÃÅ"+
//                "\001\001\001\001u\027\027\027\027\027\027\030\030\030\030\030óßÞÞÞÞÞÞ\025\025\025\025\025\025\025\025\002\002\002uuuu\027\027\027\027\030\030\030\030óóßßÞÞÞÞÞ\025\025\025\025\025\025Ýå\002\002\002uuuuuôôô\030\030ààóóßßßßÞÞá\025\025\025ÝÝÝÝåWW\002\002uuvvôôàààààóóßßßßßáááÝÝÝÝÝÝå"+
//                "WWWWWvvvvvÊÊàààËËËËßßááááÝÝÝÝÎÎåWWWWWWvvvÊÊÊÊÊÊËËËËËËááááÝÝÎÎÎÎå²²²²²²²hhhhÊÊÊÊËËËËËËááááÎÎÎÎÎÎÎgggggg\004\004\004hhhhwwwËxxxÌÌÌ\026\026\026ÎÎÎÎÜÜ"+
//                "³³½½½½½½½½hhwwwwwxxÌÌÌÌ\026\026\026\026ÜÜÜÜÜ³³³³³³³½½½ÉÉÉÉÉÉÉÉÌÌÌÌÌ\026\026\026\026ÜÜÜÜÜIIIIIIIXVVVÉÉÉÉÉÉÉÉyyyy\026\026\026\026\026ÜÜÜÜ´´´´´XXXXXXttttttttyyyyyy\026\026\026ÚÚÚÛ"+
//                "´´´´´´XffffftttttttÍÏÏÏÏÏÏ×××ÚÚÚYYYYYYfffffffiiiiÍÍÍÍÏÏÏÏÏ××××ÚÚYYYYYYYYYfff\020iiiiÍÍÍÍÍÏÏÐÐÐ××××Ú++++++++++Y\020\020\020\020\020\020\020ssÍÍÍØÐÐÐÐ××××"+
//                "[[[[[[[¼¼¼¼¼e\020\020\020\020\020sssssØØØÐÐÐ×××[[[[[[[[¼¼¼¼¼eeeeesssssrrØÈÈÈÈ\024\024µµµµµµµ[[¼¼¼¼e¾¾¾¾¾¾¾rrrrrÈÈÈÈ\024\024µµµµµµµµUUU\\\\¾¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024"+
//                "µµµµµµµ»»»»»»¾¾¾¾¾¾¾jjjrrrrÈÈÒÒÒººººº»»»»»»»»»»»¿¿jjjjjjrÑÑÑÒÒÒÒºººººººº»»»»»»»¿¿¿¿¿jjjj\021\021ÖÖÖÖÒÒºººººººººº»»»»»¿¿¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÖ"+
//                "¶¶¶¶¶¶¶¶¶,,,,^^^^^¿¿¿¿\021\021\021\021\021\021ÖÔÔÔ¶¶¶¶¶¶,,,,,,,,^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔ·······,,,,,ÀÀÀÀÀ^^^ÁÁÁÇ\021\021\021k\022\022ÔÔ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇ\022\022\022\022Å"+
//                "-----------¹ÀÀÀÀÀÂÂÁÁÁÁÁÇÇÇÇ\022\022\022Å---------¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÇÇÇÇ\022\022\022Å------¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÁÃÃÃÃÃ\022\022Å¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÃÃÃÃÃÃÃÃÅ"+
//                "\002\002uuuu\027\027\027\027\030\030\030\030\030óóßßÞÞÞÞÞ\025\025\025\025\025\025\025å\002\002\002uuuu\027\027ô\030\030\030\030\030óóßßßÞÞÞá\025\025\025\025\025ÝÝå\002\002uuuôôôôàààóóóßßßßÞáá\025\025ÝÝÝÝÝåWWWvvvvôôààààóóóßßßßßáááÝÝÝÝÝÝå"+
//                "WWWWWvvvvvÊÊÊàóËËËËßßááááÝÝÝÝÎÎå²²²WWvvvÊÊÊÊÊÊËËËËËËáááááÝâÎÎÎÎå²²²²²²²hhhhÊÊÊÊËËËËËËááááÎÎÎÎÎÎÎggggg\004\004\004\004hhhwwwwxxxxÌÌÌ\026\026\026ÎÎÎÎÜÜ"+
//                "³³½½½½½½½\004hhwwwwwxxÌÌÌÌ\026\026\026\026ÜÜÜÜÜ³³³³³³³½½½\005ÉÉÉÉÉÉÉÌÌÌÌÌ\026\026\026\026ÜÜÜÜÜIIIIIIIVVVVÉÉÉÉÉÉÉyyyyy\026\026\026\026\026ÜÜÜÛ´´´´XXXXXXVttttttttyyyyyy\026\026ÚÚÚÚÛ"+
//                "´´´´´XfffffftttttttÍÏÏÏÏÏÏ×××ÚÚÚYYYYYYffffffZiiiiÍÍÍÍÏÏÏÏÏ××××ÚÚYYYYYYYYYfffiiiiiÍÍÍÍÍÏÏÐÐÐ××××Ú+++++++++YY\020\020\020\020\020\020\020sssÍØØÐÐÐÐ××××"+
//                "[[[[[[[¼¼¼¼¼ee\020\020\020\020sssssØØØÐÐÐ×××[[[[[[[[¼¼¼¼eeeeeessssrrrÈÈÈÈ\024\024\024µµµµµµµUU¼¼¼\\e¾¾¾¾¾¾rrrrrrÈÈÈÈ\024\024µµµµµµµUUUU\\\\¾¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024"+
//                "µµµµµµµ»»»»»»¾¾¾¾¾¾jjjjrrrrÈÈÒÒÒººººº»»»»»»»»»»»¿jjjjjjjrÑÑÑÒÒÒÒºººººººº»»»»»»»¿¿¿¿¿jjjj\021\021ÖÖÖÖÒÒºººººººººº»»»»»¿¿¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÖ"+
//                "¶¶¶¶¶¶¶¶,,,,,^^^^^¿¿¿¿\021\021\021\021\021\021ÖÔÔÔ¶¶¶¶¶,,,,,,,,,^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔ·······,,,,ÀÀÀÀÀÀ^^^ÁÁÁÇ\021\021kk\022\022ÔÔ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇ\022\022\022ÅÅ"+
//                "-----------¹ÀÀÀÀÀÂÂÁÁÁÁÁÇÇÇ\022\022\022\022Å---------¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÇÇÇÇ\022\022\022Å------¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÁÃÃÃÃÃ\022\022Å¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃÃÃÃÃÅ"+
//                "uu\027\027\027ô\030\030\030\030óóóßßÞÞÞÞá\025\025\025\025\025\025Ýåôôôô\030\030\030óóóßßßÞÞÞá\025\025\025\025ÝÝÝåôôôôôàààóóóßßßßÞáá\025ÝÝÝÝÝÝåWWWvvvvôàààààóóóßßßßááááÝÝÝÝÝåå"+
//                "WWWWvvvvvÊÊÊÊóËËËËËáááááÝÝÝâÎÎå²²²²²vvÊÊÊÊÊÊÊËËËËËËáááááââÎÎÎÎå²²²²²²HhhhhhwwËËËËËËxááááÎÎÎÎÎÎågggg\004\004\004\004\004hhhwwwwxxxxÌÌÌ\026\026\026ÎÎÎÜÜÜ"+
//                "³³½½½½½½\004\004hwwwwwxxxÌÌÌÌ\026\026\026\026ÜÜÜÜÜ³³³³³³³½½\005\005\005ÉÉÉÉÉÉxÌÌÌÌ\026\026\026\026ÜÜÜÜÜIIIIIIIVVVVÉÉÉÉÉÉÉyyyyy\026\026\026\026ÜÜÜÛÛ´´´´XXXXXXVttttttttyyyyyy\026\026ÚÚÚÚÛ"+
//                "´´´´´ffffffftttttttÏÏÏÏÏÏÏ××ÚÚÚÚYYYYYYffffffZiiiÍÍÍÍÍÏÏÏÏÏ××××ÚÚYYYYYYYYYffZiiiiiÍÍÍÍÍÏÏÐÐÐ××××Ú+++++++++Y\020\020\020\020\020\020\020sssssØØÐÐÐÐ××××"+
//                "[[[[[[[¼¼¼¼¼ee\020\020\020\020sssssØØØÐÐÐ×××[[[[[[[[¼¼¼¼eeeeeessssrrrÈÈÈÈ\024\024\024µµµµµµUUU¼¼\\\\e¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024\024µµµµµµUUUUU\\\\¾¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024"+
//                "µµµµµµU»»»»»»¾¾¾¾¾¾jjjjrrrrÈÈÒÒÒººººº»»»»»»»»»»¿¿jjjjjjjrÑÑÒÒÒÒÒºººººººº»»»»»»»¿¿¿¿¿jjjj\021\021ÖÖÖÖÒÒºººººººººº»»»»»¿¿¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÖ"+
//                "¶¶¶¶¶¶¶¶,,,,,^^^^^¿¿¿¿\021\021\021\021\021\021ÔÔÔÔ¶¶¶¶,,,,,,,,,,^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔ·······,,,,ÀÀÀÀÀÀ^^ÁÁÁÁÇ\021kkk\022\022ÔÔ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇ\022\022\022ÅÅ"+
//                "-----------¹¹ÀÀÀÂÂÂÁÁÁÁÁÇÇÇ\022\022\022ÅÅ--------¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÇÇÇÇ\022\022ÅÅ-----¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃÃÃ\022ÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃÃÃÃÃÅ"+
//                "ôôôôô\030\030óóóßßßßÞÞáá\025\025\025\025ÝÝÝåôôôôôààóóóóßßßÞÞáá\025\025\025ÝÝÝÝåôôôàààóóóóßßßßáááÝÝÝÝÝÝååvv\003\003\003àààóóóóßßßßááááÝÝÝÝÝåå"+
//                "WWWWvvv\003\003ÊÊÊËËËËËñáááááÝÝââÎåå²²²²²HHÊÊÊÊÊÊÊËËËËËËáááááââÎÎÎÎå²²²²²HHHhhhwwËËËËËxxááááÎÎÎÎÎÎå²g\004\004\004\004\004\004\004hhwwwwwxxxxÌÌ\026\026\026\026ÎÎÎÜÜÜ"+
//                "³³³½½½½\004\004\004wwwwwwxxxxÌÌ\026\026\026\026\026ÜÜÜÜÜ³³³³³³VVV\005\005\005\005\005ÉÉÉxxÌÌÌ\026\026\026\026\026ÜÜÜÜÜIIIIIIVVVVVÉÉÉÉÉÉÉyyyyy\026\026\026\026ÜÛÛÛÛ´´´XXXXXXVVtttttttyyyyyyy\026\026ÚÚÚÚÚ"+
//                "´´´´fffffffZttttttÍÏÏÏÏÏÏÏ××ÚÚÚÚ±±YYYYffffZZZiiiÍÍÍÍÍÏÏÏÏÏ×××ÚÚÚ±±±±±YYYffZZiiiiiÍÍÍÍÍÏÏÐÐÐ××××Ú++++++++YY\020\020\020\020\020\020\020sssssØØØÐÐÐ××××"+
//                "[[[[[[[¼¼¼¼eee\020\020\020ssssssØØØØÐÐ×××[[[[[[[[¼¼¼¼eeeeeessssrrrÈÈÈÈ\024\024\024µµµµµUUUUU\\\\\\e¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024\024µµµµµUUUUU\\\\\\¾¾¾¾¾¾¾¾rrrrrÈÈÈÈÈ\024"+
//                "°°°°°UU»»»»»»¾¾¾¾¾¾jjjrrrrrÈÒÒÒÒ°°°°°»»»»»»»»»»¿¿jjjjjjjÑÑÑÒÒÒÒÒººººººº»»»»»»»»¿¿¿¿jjjjj\021ÖÖÖÖÒÒÒºººººººººº»»»»^¿¿¿¿¿¿¿\021\021\021\021ÖÖÖÖÖÖ"+
//                "¶¶¶¶¶¶¶,,,,,,^^^^^¿¿¿\021\021\021\021\021\021\021ÔÔÔÔ¶¶¶,,,,,,,,,,,^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔ·······,,,,ÀÀÀÀÀÀ^^ÁÁÁÁÇkkkk\022ÆÆÔ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇ\022\022\022ÅÅ"+
//                "----------¹¹¹ÀÀÀÂÂÂÁÁÁÁÇÇÇÇ\022\022\022ÅÅ--------¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÇÇÇÃ\022\022ÅÅ----¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃÃÃ\022ÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃÃÃÃÃÅ"+
//                "ôôôôôàóóóóßßßßÞÞáá\025\025ÝÝÝÝååôôôôàóóóóßßßßßááá\025ÝÝÝÝÝååôààóóóóóßßßßáááÝÝÝÝÝÝåå\003\003\003\003\003óóóóËßßßááááÝÝÝÝââåå"+
//                "\003\003\003\003\003\003ËËËËËñáááááââââÎåå²²²HHHHÊÊÊÊÊÊËËËËËñáááááââÎÎÎåå²²HHHHHHwwwËËËxxxááá\026ÎÎÎÎÎÎå\004\004\004\004\004\004\004\004\004hwwwwwwxxxxÌÌ\026\026\026\026ÎÎÜÜÜÜ"+
//                "³³³½½½\004\004\004\005wwwwwwxxxxÌÌ\026\026\026\026\026ÜÜÜÜÜ³³³³³VVVV\005\005\005\005\005\005\005ÉyyÌÌÌ\026\026\026\026\026ÜÜÜÜÛIIIIIVVVVVV\005ÉÉÉÉÉyyyyyy\026\026\026\026ÛÛÛÛÛ´IIXXXXVVVVtttttttyyyyyyy\026\026ÚÚÚÚÚ"+
//                "´´´´fffffZZZttttttÍÏÏÏÏÏÏÏ××ÚÚÚÚ±±±±±±ffZZZZZiiiÍÍÍÍÏÏÏÏÏÏ×××ÚÚÚ±±±±±±±±ZZZZiiiiiÍÍÍÍÍÏØÐÐÐ×××ÚÚ++++±±±±±±\020\020\020\020\020\020\020sssssØØØÐÐÐ×××Ú"+
//                "[[[[[[[¼¼¼¼eeee\020\020ssssssØØØØÐÐ××\024[[[[[[[[¼¼¼eeeeeeessssrrrÈÈÈÈ\024\024\024µµµUUUUUU\\\\\\\\ee¾¾¾¾¾rrrrrÈÈÈÈÈ\024\024µµµUUUUUUU\\\\\\¾¾¾¾¾¾¾rrrrrrÈÈÈÈÒ\024"+
//                "°°°°°°°»»»TTT¾¾¾¾¾jjjjrrrrÑÈÒÒÒÒ°°°°°°»»»»»»»»T¿¿jjjjjjjÑÑÑÒÒÒÒÒººººººº»»»»»»»»¿¿¿¿jjjjj\021ÖÖÖÖÒÒÒººººººººº»»»»»^¿¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÖÖ"+
//                "¶¶¶¶¶¶¶,,,,,^^^^^^^¿¿\021\021\021\021\021\021ÔÔÔÔÔ¶,,,,,,,,,,,,^^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔ·······,,,,ÀÀÀÀÀ^^^ÁÁÁÇÇkkkk\022ÆÆÆ·········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇ\022\022\022ÅÅ"+
//                "----------¹¹¹___ÂÂÂÁÁÁÁÇÇÇÇ\022\022\022ÅÅ-------¹¹¹¹¹¹¹¹_ÂÂÂÂÂÁÁÁÇÇÇÃ\022\022ÅÅ---¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂcÃÃÃÃÃÃ\022ÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂcÃÃÃÃÃÃÃÃÅ"+
//                "ôôóóóóóóßßßßááááÝÝÝÝÝÝååôóóóóóóßßßßááááÝÝÝÝÝâåå\003\003\003óóóóóßßßßááááÝÝÝÝââåå\003\003\003\003\003óóóËñññááááÝâââââåå"+
//                "\003\003\003\003\003\003ËËËËññááááâââââÎååHHHHHHHËËËËËññááááâââÎÎÎååHHHHHHHHwwËxxxxxá\026\026\026ÎÎÎÎÎååHH\004\004\004\004\004\004wwwwwwwxxxxxxÌ\026\026\026\026ÎÜÜÜÜÜ"+
//                "³³³³\004\004\004\004\005\005\005\005\005\005wxxxxxÌÌ\026\026\026\026ÜÜÜÜÜÜIIVVVVVV\005\005\005\005\005\005\005\005\005yyyy\026\026\026\026\026\026ÜÜÛÛÛIIIIVVVVVV\005\005\005ttttyyyyyy\026\026\026\026ÛÛÛÛÛ¤¤¤¤¤VVVVVVtttttttyyyyyyy\026ÚÚÚÚÚÚ"+
//                "´´fffZZZZZZZtttttÍÍÏÏÏÏÏÏÏ××ÚÚÚÚ±±±±±±ZZZZZZiiiiÍÍÍÍÏÏÏÏÏÏ×××ÚÚÚ±±±±±±±±ZZZZiiiiiÍÍÍÍÏØØÐÐÐ×××ÚÚ±±±±±±±±±±\020\020\020\020\020issssssØØØØÐÐ×××Ù"+
//                "[[[[[[[¼¼¼eeeee\020ssssssØØØØØØ{×\024\024UUU[[[[¼¼¼eeeeeeeesssrrrrÈÈÈ\024\024\024\024UUUUUUUUU\\\\\\\\ee¾¾¾¾2rrrrrÈÈÈÈ\024\024\024UUUUUUUUU\\\\\\\\¾¾¾¾¾¾¾rrrrrrÈÈÈÈÒ\024"+
//                "°°°°°°°°TTTTT¾¾¾¾jjjjjrrrÑÑÈÒÒÒÒ°°°°°°°»»»»TTTT]jjjjjjjjÑÑÑÒÒÒÒÒººººººº»»»»»»»]¿¿¿¿jjjjj\021ÖÖÖÖÒÒÒººººººººº»»»»^^^¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÖÔ"+
//                "¶¶¶¶¶,,,,,,,^^^^^^^¿¿\021\021\021\021\021\021ÔÔÔÔÔ,,,,,,,,,,,,,^^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔ······,,,,,ÀÀÀÀÀ^^^ÁÁÁÇkkkkkÆÆÆÆ·········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇ\022\022\022ÅÅ"+
//                "---------¹¹¹¹___ÂÂÂÁÁÁÁÇÇÇÇ\022\022\022ÅÅ------¹¹¹¹¹¹¹¹¹_ÂÂÂÂÂÁÁÇÇÃÃÃ\022\022ÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂcÃÃÃÃÃÃ\022ÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂcÃÃÃÃÃÃÃÃÄ"+
//                "óóóóóóßßßßááááÝÝÝââååå\003\003óóóóóßßßáááááÝÝâââååå\003\003\003\003óóóóñññáááááâââââååå\"\"\003\003\003\003\003òËññññááááâââââååå"+
//                "\"\"\003\003\003òòËññññááááââââÎåååHHHHHHHòËËËñññááááâââÎÎåååHHHHHHHxxxxxxx\026\026\026\026ÎÎÎÎÎååHHHHHH\004\004wwwwwwxxxxxx\026\026\026\026\026ÜÜÜÜÜÜ"+
//                "³³VV\004\004\005\005\005\005\005\005\005\005\005xxxxxÌ\026\026\026\026\026ÜÜÜÜÛÛ¤¤¤VVVVV\005\005\005\005\005\005\005\005yyyyy\026\026\026\026\026\026ÛÛÛÛÛ¤¤¤¤¤VVVVV\005\005\005tttyyyyyyy\026\026\026\026ÛÛÛÛÛ¤¤¤¤¤¤VVVVtttttttyyyyyyyy\026ÚÚÚÚÚÚ"+
//                "¥¥¥¥ZZZZZZZZtttttÍÍÏÏÏÏÏÏ×××ÚÚÚÚ±±±±¥ZZZZZZZiiiiÍÍÍÍÏÏÏÏÏ××××ÚÚÚ±±±±±±±ZZZZZiiiissÍÍÍØØØØÐ××××ÚÚ±±±±±±±±±Z\020\020\020iiisssssØØØØØØ{×××Ù"+
//                "JJJJJJJJ¼eeeeeeessssssØØØØØØ{\024\024\024JJJJJJJJJ\\eeeeeeeesssrrrÈÈÈÈ\024\024\024\024UUUUUUUU\\\\\\\\\\ee¾¾¾¾22rrrrÈÈÈÈ\024\024\024UUUUUUUUU\\\\\\\\\\¾¾¾¾¾22rrrrrÈÈÈÈ\024\024"+
//                "°°°°°°°TTTTTTT¾¾¾jjjjjrrÑÑÑÒÒÒÒÒ°°°°°°°°TTTTTT]]jjjjjjjÑÑÑÑÒÒÒÒÒºº°°°°°°»»»»T]]]¿¿jjjjj\021\021ÑÖÖÖÒÒÒººººººººº»»»^^^^¿¿¿¿¿\021\021\021\021\021ÖÖÖÖÔÔ"+
//                "¶¶¶¶,,,,,,,,^^^^^^^¿\021\021\021\021\021\021\021ÔÔÔÔÔ,,,,,,,,,,,,,^^^^^^^^\021\021\021\021\021kÔÔÔÔÔ······,,,,ÀÀÀÀÀÀ^^^ÁÁÁkkkkkkÆÆÆÆ········SSÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇ\022\022\022ÅÅÅ"+
//                "--------¹¹¹¹____ÂÂÂÁÁÁÁÇÇÇÇ\022\022ÅÅÅ-----¹¹¹¹¹¹¹¹¹__ÂÂÂÂÂÁÁÇÃÃÃÃ\022ÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂcÃÃÃÃÃÃÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¸ÂÂÂÂÂccÃÃÃÃÃÃÃlÄ"+
//                "\"\"\"\"\"\"\003\003óóóóóñññáááááâââââååå\"\"\"\"\"\"\003\003óóóóñññáááááâââââååå\"\"\"\"\"\"\003\003òòññññáááááâââââååå\"\"\"\"\"\"\003\003òòòñññññááááâââââååå"+
//                "òòòòññññááááââââÎåååHHHHHHòòòòññññááááâââÎÎåååHHHHHHxxxxxxx\026\026\026\026ÎÎÎÎÜåå££HHHHHwwwwxxxxxxx\026\026\026\026\026ÜÜÜÜÜÛ"+
//                "¤¤¤VV\005\005\005\005\005\005\005\005\005\005xxxxx\026\026\026\026\026\026ÜÜÛÛÛÛ¤¤¤¤¤VVV\005\005\005\005\005\005\005\005yyyyy\026\026\026\026\026ÛÛÛÛÛÛ¤¤¤¤¤¤VVV\005\005\005\005\006\006\006yyyyyyy\026\026\026ÛÛÛÛÛÛ¤¤¤¤¤¤¤VVVtttttttyyyyyyyy×ÚÚÚÚÚÚ"+
//                "¥¥¥¥¥ZZZZZZZttttÍÍÏÏÏÏÏÏÏ××ÚÚÚÚÚ¥¥¥¥¥¥ZZZZZZiii\007ÍÍÍÍÏÏÏÏÏ×××ÚÚÚÚ±±±±±¥¥ZZZZiiiiissssÍzØØØØ{××ÙÙÙ±±±±±±±±±e4iiiissssssØØØØØØ{××ÙÙ"+
//                "JJJJJJJJeeeeeeeesssss3ØØØØØ{{\024\024\024JJJJJJJJJ\\eeeeeeess22rrrÈÈÈÈ\024\024\024\024UUUUUUUU\\\\\\\\\\ee¾¾¾222rrrrÈÈÈÈ\024\024\024UUUUUUUU\\\\\\\\\\\\¾¾¾¾j22rrrrÈÈÈÈÒ\024\024"+
//                "°°°°°°°TTTTTTT¾¾jjjjjjrÑÑÑÑÒÒÒÒÒ°°°°°°°°TTTTTT]]jjjjjjjÑÑÑÑÒÒÒÒÒ°°°°°°°°TTTTT]]]]¿jjjjj\021ÑÑÖÖÖÒÒÓººººººººº»»^^^^^¿¿¿¿j\021\021\021\021\021ÖÖÖÔÔÔ"+
//                "¶,,,,,,,,,,^^^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔÔ,,,,,,,,,,,,^^^^^^^^\021\021\021kkkkÔÔÔÔÔ¯¯¯¯¯¯¯¯¯,ÀÀÀÀÀÀ^^ÁÁÁÁkkkkkkÆÆÆÆ······SSSSSÀÀÀÀ_ÂÁÁÁÁÁÇÇÇÇ\022\022\022ÅÅÅ"+
//                "-------¹¹¹¹_____ÂÂÂÁÁÁÁÇÇÇÇ\022\022ÅÅÅ---¹¹¹¹¹¹¹¹¹¹___ÂÂÂÂÂccÃÃÃÃÃ\022ÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¸¸ÂÂÂÂÂccÃÃÃÃÃÃÅÅÄ¹¹¹¹¹¹¹¹¹¹¹¹¹¸¸¸¸ÂÂÂÂccÃÃÃÃÃÃÃlÄ"+
//                "\"\"\"\"\"\"\"òòòòññññáááááââââåååå\"\"\"\"\"\"\"òòòòññññáááááââââåååå\"\"\"\"\"\"\"òòòòññññáááááââââåååå\"\"\"õõòòòòñññññááááââââåååå"+
//                "õõõõòòòòñññññááááââââååååòòòxññññáááââââÎåååå££££££xxxxxxx\026\026\026\026ÎÎÎÜååå££££££££wwwxxxxxx\026\026\026\026\026ÜÜÜÜÛÛÛ"+
//                "¤¤££>>>\005\005\005\005\005\005\005\005xxxxx\026\026\026\026\026\026ÛÛÛÛÛÛ¤¤¤¤¤¤V\005\005\005\005\005\005\005\005yyyyyyy\026\026\026\026ÛÛÛÛÛÛ¤¤¤¤¤¤¤VG\006\006\006\006\006\006\006yyyyyyy\026\026\026ÛÛÛÛÛÛ¤¤¤¤¤¤¤¤GG\006\006\006\006\006\006yyyyyyyyÚÚÚÚÚÚä"+
//                "¥¥¥¥¥¥ZZZZZZ\007\007\007\007\007ÍÏÏÏÏÏÏÏ××ÚÚÚÚÚ¥¥¥¥¥¥¥ZZZZZi\007\007\007\007ÍÍÍÏÏÏzØ{××ÚÚÙÙ¥¥¥¥¥¥¥¥ZZZ4iiissssszzzØØ{{{×ÙÙÙ±±±±±¥¥¥444444isssss3zØØØØ{{{×ÙÙ"+
//                "JJJJJJJJeeeeeeessss333ØØØØØ{\024\024\024\024JJJJJJJJ\\\\eeeeeee2222rrrÈÈÈ|\024\024\024\024UUUUJJJ\\\\\\\\\\\\ee¾¾2222rrrrÈÈÈ\024\024\024\024UUUUUUU\\\\\\\\\\\\\\¾¾¾j222rrrrÈÈÈÈÒ\024\024"+
//                "°°°°°°°TTTTTTT¾jjjjjjjÑÑÑÑÑÒÒÒÒÓ°°°°°°°TTTTTT]]]jjjjjjjÑÑÑÑÒÒÒÒÓ°°°°°°°°TTTT]]]]]jjjjjj\021ÑÑÖÖÒÒÒÓºººººº°°°TT^^^^^]¿¿dd\021\021\021\021\021ÖÖÔÔÔÔ"+
//                ",,,,,,,,,,,^^^^^^^^dd\021\021\021\021\021ÔÔÔÔÔÔ¯¯¯,,,,,,,,,^^^^^^^^kkkkkkkÔÔÔÔÔ¯¯¯¯¯¯¯¯¯¯SÀÀÀÀ^^^ÁÁÁkkkkkkÆÆÆÆÆ¯¯¯¯SSSSSSSS____ÂÁÁÁÁÁÇÇÇÇ\022\022ÅÅÅÅ"+
//                "---SSSSSS¹______ÂÂÂÁÁÁÁÇÇÇ\022\022\022ÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹__ÂÂÂÂÂÂccÃÃÃÃÃ\022ÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¸¸¸ÂÂÂÂcccÃÃÃÃÃÃlÅÄ¹¹¹¹¹¹¹¹¹¹¹¹¸¸¸¸¸ÂÂÂcccÃÃÃÃÃÃllÄ"+
//                "\"\"\"\"\"\"\"õõõòòòòòññññááááâââââåååå\"\"\"\"õõõõòòòòòññññááááâââââåååå\"õõõõòòòòòññññááááâââââååååõõõõõòòòòññññááááâââââåååå"+
//                "õõõõõòòòòñññññáááââââååååå££££££õõxx\026\026ââââååååå££££££££====xxx\026\026\026ððÜÜÛååå££££££££>>====xxx\026\026\026\026ÛÛÛÛÛÛã"+
//                "££>>>>>>>>>\005\005\005y\026\026\026\026\026ÛÛÛÛÛÛÛ¤¤¤¤¤G>>>>\006\006\006\006\006yyyyyyy\026\026\026ÛÛÛÛÛÛÛ¤¤¤¤¤GGGG\006\006\006\006\006\006\006yyyyyyy\026ÛÛÛÛÛÛä¤¤¤¤¤GGGGG\006\006\006\006\006\006yyyyyÚÚÚÚÚÚä"+
//                "¥¥¥¥¥¥¥ZZZZ\007\007\007\007\007\007\007ÏÏÏÏ{×ÚÚÚÙÙÙ¥¥¥¥¥¥¥¥ZZ44\007\007\007\007\007\007Ízzzzz{{{ÙÙÙÙÙ¥¥¥¥¥¥¥¥444444\007\007ss33zzzzØ{{{ÙÙÙÙ¥¥¥¥¥¥¥44444444ss3333zzØØ{{{{ÙÙÙ"+
//                "JJJJJJJJeeeeeee3333333ØØØØ{{\024\024\024\024JJJJJJJJ\\\\eeeeee222222rrÈÈ|\024\024\024\024\024JJJJJJJ\\\\\\\\\\\\eee222222rrÈÈÈÈ\024\024\024\024UUUUUU\\\\\\\\\\\\\\\\¾¾j22222rrÑÑÈÈÒ\024\024\024"+
//                "°°°°°°TTTTTTTTjjjjjjjÑÑÑÑÑÑÒÒÒÓÓ°°°°°°°TTTTTT]]]jjjjjjÑÑÑÑÑÒÒÒÓÓ°°°°°°°°TTT]]]]]]jjjjj\021\021ÑÑÖÖÒÒÓÓ°°°°°°°°TT]]]]]]]]ddd\021\021\021\021\021ÔÔÔÔÔÓ"+
//                ",,,,,,,,,,^^^^^^^^^dd\021\021\021\021kÔÔÔÔÔÔ¯¯¯¯¯¯¯¯,,,^^^^^^^^dkkkkkkkÔÔÔÔÔ¯¯¯¯¯¯¯¯¯SSSÀÀÀ^^ÁÁÁÁkkkkkkÆÆÆÆÆ¯¯SSSSSSSSS_____ÂÁÁÁÁÁÇÇÇk\022\022ÅÅÆÆ"+
//                "SSSSSSSSS_______ÂÂÂÁÁccÇÇÇ\022\022ÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¹___¸¸ÂÂÂÂcccÃÃÃÃÃÅÅÅÄ¹¹¹¹¹¹¹¹¹¹¹¸¸¸¸¸ÂÂÂÂcccÃÃÃÃÃllÄÄ¹¹¹¹¹¹¹¹¹¹¸¸¸¸¸¸¸ÂÂccccÃÃÃÃÃÃlÄÄ"+
//                "õõõõõòòòòòññññááááââââåååååõõõõõòòòòòññññááááââââåååååõõõõõõòòòòññññááááââââåååååõõõõõõòòòññññááááââââååååå"+
//                "õõõõõñ\031\031\031\031áðââââååååå£££££££=====\026\026ððððååååå££££££££======\026\026ðððÛÛÛãåå£££££££>>>====\026\026\026ðÛÛÛÛÛãã"+
//                ">>>>>>>>>>>>>\026\026\026\026\026ÛÛÛÛÛÛÛGGGGGG>>>>\006\006\006\006\006yyyyyyy\026\026\026ÛÛÛÛÛÛäGGGGGGGGG\006\006\006\006\006\006\006yyy\026ÛÛÛÛÛääGGGGGGGGGG\006\006\006\006\006\006ÚÚÚÚÙää"+
//                "¥¥¥¥¥¥¥¥Z\007\007\007\007\007\007\007\007\007z{{{ÙÙÙÙÙÙ¥¥¥¥¥¥¥¥444\007\007\007\007\007\007\007zzzzzz{{{ÙÙÙÙÙ¥¥¥¥¥¥¥444444\007\007\007333zzzzz{{{{ÙÙÙÙ*******4444444433333zzzzØ{{{{ÙÙÙ"+
//                "JJJJJJJJeeeee¦¦3333333zØØ||{\024\024\024\024JJJJJJJJ\\\\eeee¦¦222222rrÈ||\024\024\024\024\024JJJJJJJ\\\\\\\\\\\\\\e2222222rrÈÈÈ\024\024\024\024\024°°°°TKKKKKKKKKKj222222ÑÑÑÑÑÒÒ\024\024\024"+
//                "°°°°°°TTTTTTKKKjjjjjjÑÑÑÑÑÑÒÒÓÓÓ°°°°°°TTTTTT]]]]jjjjjjÑÑÑÑÑÒÒÓÓÓ°°°°°°°TTTT]]]]]]jjjjj\021ÑqqqqÒÓÓÓ°°°°°°°°T]]]]]]]]ddddd\021\021\021ÔÔÔÔÔÔÓ"+
//                "¯¯¯,,,,,,,^^^^^^^^dddkkkkkÔÔÔÔÔÔ¯¯¯¯¯¯¯¯¯¯¯^^^^^^^^dkkkkkkkÆÆÆÔÔ¯¯¯¯¯¯¯¯SSSSS__^^ÁÁÁkkkkkkkÆÆÆÆÆSSSSSSSSSSS_____ÂÁÁÁÁÁÇÇkk\022ÆÆÆÆÆ"+
//                "SSSSSSSSS_______ÂÂÂccccÇÇÃ\022\022ÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¸¸¸¸¸¸ÂÂÂccccÃÃÃÃllÅÄÄ¹¹¹¹¹¹¹¹¸¸¸¸¸¸¸¸¸ÂÂccccÃÃÃÃÃllÄÄ¹¹¹¹¹¹¹¸¸¸¸¸¸¸¸¸¸¸ÂccccÃÃÃÃÃllÄÄ"+
//                "õõõõõõõòñññ\031áááââââååååååõõõõõõññ\031\031\031ááââââååååååõõõõõõ\031\031\031\031ááââââåååååå!!!!õõõõ\031\031\031\031\031áðððâåååååå"+
//                "£££!!!=====\031\031\031\031\031ðððððåååååå££££££=======ððððððååååå£££££££======\026ððððÛÛãããã££££>>>>>>===\026\026ððÛÛÛÛããã"+
//                ">>>>>>>>>>>>>\026\026\026ÛÛÛÛÛÛÛäGGGGGGG>>\006\006\006\006\006\006y\026ÛÛÛÛÛÛääGGGGGGGGG\006\006\006\006\006\006ÛÛäääGGGGGGGGGG\006\006\006\006\006\006ÙÙÙÙää"+
//                "¥¥¥¥¥¥¥44\007\007\007\007\007\007\007\007zzz{{{ÙÙÙÙÙÙ¥¥¥¥¥¥44444\007\007\007\007\007\007\007zzzzz{{{{ÙÙÙÙÙ¥¥¥¥¥¥4444444\007\007\007333zzzzz{{{{ÙÙÙÙ********444444333333zzzz{{{{{ÙÙÙ"+
//                "**********¦¦¦¦¦¦33333zzz|||\024\024\024\024\024JJJJJJJJ\\\\ee¦¦¦¦222222r||||\024\024\024\024\024JJJJJJKKKKKKKK22222222rÑÈÈ|\024\024\024\024\024°°KKKKKKKKKKKKK2222222ÑÑÑÑÑÒ\024\024\024\024"+
//                "°°°°°TTTTTKKKKKjjjjj2ÑÑÑÑÑÑÒÓÓÓÓ°°°°°°TTTTT]]]]]jjjjjÑÑÑÑqqÒÓÓÓÓ°°°°°°°TTT]]]]]]]ddddddqqqqqÓÓÓÓ°°°°°°°T]]]]]]]]dddddd\021\021\021ÔÔÔÔÔÓÓ"+
//                "¯¯¯¯¯¯¯¯,^^^^^^^^ddddkkkkkÔÔÔÔÔÔ¯¯¯¯¯¯¯¯¯¯¯^^^^^^^ddkkkkkkÆÆÆÆÆÆ¯¯¯¯¯¯¯SSSSS___^^ÁÁÁkkkkkkÆÆÆÆÆÆSSSSSSSSSSS_____ÂÁÁÁ``ÇkkkÆÆÆÆÆÆ"+
//                "SSSSSSSSS_______ÂÂcccccÃÃÃ\022ÅÅÅÅÄ®®®®®®®¸¸¸¸¸¸¸¸¸ÂÂcccccÃÃÃlllÄÄÄ®®®®®¸¸¸¸¸¸¸¸¸¸¸¸ÂcccccÃÃÃÃlllÄÄ®®®¸¸¸¸¸¸¸¸¸¸¸¸¸¸¸cccccÃÃÃÃÃllÄÄ"+
//                "!!!!!!õõõ\031\031\031\031\031\031ððððååååååå!!!!!!!õõ\031\031\031\031\031\031ðððððåååååå!!!!!!!õõ\031\031\031\031\031\031ðððððåååååå!!!!!!!===\031\031\031\031\031\031ðððððåååååå"+
//                "!!!!!!======\031\031\031\031\031\031ðððððåååååå£££££========ðððððãããããã££££££======ðððððÛããããã###>>>>>>>==\026ðððÛÛÛãããã"+
//                "###>>>>>>>>>\026\026ðÛÛÛÛÛÛääGGGGGGGG>\006\006\006\006\006ÛÛÛÛÛäääGGGGGGGGG\006\006\006\006\006ääääGGGGGGGGGG\006\006\006\006\006Ùäää"+
//                "¥¥¥¥¥¥44\007\007\007\007\007\007\007\007\007zzzz{{{ÙÙÙÙÙÙ¥¥¥¥¥444444\007\007\007\007\007\007zzzzzz{{{{ÙÙÙÙÙ******4444444\007\0073333zzzz{{{{{ÙÙÙÙ********444444333333zzzz{{{{{\024ÙÙ"+
//                "*********¦¦¦¦¦¦¦33333zz||||\024\024\024\024\024JJJJJJJKKK¦¦¦¦¦¦222222|||||\024\024\024\024\024KKKKKKKKKKKKKK22222222ÑÑÑ||\024\024\024\024\024KKKKKKKKKKKKKKK222222ÑÑÑÑÑÑÓÓ\024\024\024"+
//                "°°°°TTTTKKKKKKKjjjj2ÑÑÑÑÑÑqÓÓÓÓÓ°°°°°TTTTT]]]]]]jjjj1ÑÑqqqqqÓÓÓÓ°°°°°°TTT]]]]]]]ddddddqqqqqqÓÓÓÓ°°°°°°°]]]]]]]]]dddddddkqqÔÔÔÔÓÓ"+
//                "¯¯¯¯¯¯¯¯¯^^^^^^^dddddkkkkkÔÔÔÔÔÔ¯¯¯¯¯¯¯¯¯¯¯^^^^^^ddkkkkkkkÆÆÆÆÆo¯¯¯¯¯¯SSSSSS____ÁRRRkkkkkkÆÆÆÆÆÆSSSSSSSSSSS_____Â``````kkkÆÆÆÆÆÆ"+
//                "®®®®®®®®__¸¸¸¸¸¸Âcccccc`ÃlllÅÅÄÄ®®®®®®®¸¸¸¸¸¸¸¸¸¸ccccccÃÃÃlllÄÄÄ®®®®®®¸¸¸¸¸¸¸¸¸¸¸ccccccÃÃÃllllÄÄ®®®®­¸¸¸¸¸¸¸¸¸¸¸¸¸cccccÃÃÃÃlll\023\023"+
//                "!!!!!!!!\031\031\031\031\031\031ððððððåååååå!!!!!!!!=\031\031\031\031\031\031ððððððåååååå!!!!!!!!=\031\031\031\031\031\031ððððððåååååå!!!!!!!====\031\031\031\031\031\031ððððððãååååå"+
//                "!!!!!!======\031\031\031\031\031ððððððãããããã####!=======ððððððãããããã#######====ðððððãããããã#######>>>=ððððÛãããããä"+
//                "#######>>>>þððÛÛÛÛääääGGGGGGGG>\006\006\006\006ääääGGGGGGGGG\006\006\006\006\006ääääGGGGGGGGGG\006\007\007\007äää"+
//                "¥¥¥¥4444\007\007\007\007\007\007\007\007\007zzzzz{{{{ÙÙÙÙÙä****FF4444\007\007\007\007\007\0073zzzzz{{{{{ÙÙÙÙÙ*******FF444\007\0073333zzzzz{{{{{ÙÙÙÙ********FFF¦¦¦33333\bzzz{{{{{\024\024\024Ù"+
//                "*********¦¦¦¦¦¦¦3333\b||||||\024\024\024\024\024*****KKKK¦¦¦¦¦¦¦222222|||||\024\024\024\024\024KKKKKKKKKKKKK¦2222222\tÑÑ|||\024\024\024\024\024KKKKKKKKKKKKKKK2222\t\tÑÑÑÑÑÓÓÓ\024\024\024"+
//                "°°°KKKKKKKKKKKK1111\tÑÑÑÑqqqÓÓÓÓÓ°°°°TTTTT]]]]]]111111qqqqqqÓÓÓÓÓ°°°°°TT]]]]]]]]dddddddqqqqqqÓÓÓÓ°°°°°]]]]]]]]]ddddddddkkqqÔÔÔÓÓÓ"+
//                "¯¯¯¯¯¯¯¯¯^^^^^^ddddddkkkkkÔÔoooÕ¯¯¯¯¯¯¯¯¯¯S^^^^RRRRRkkkkkÆÆÆÆooo¯¯¯¯¯SSSSSSS___RRRRRkkkkkÆÆÆÆÆÆoSSSSSSSSSS_____````````kkÆÆÆÆÆÆÆ"+
//                "®®®®®®®®®¸¸¸¸¸¸¸ccc`````llllÄÄÄÄ®®®®®®®®¸¸¸¸¸¸¸¸cccccccÃÃllllÄÄÄ®®®®®®®­¸¸¸¸¸¸¸¸¸ccccccÃÃllllÄÄÄ­­­­­­­­­­­­¸¸¸¸¸cccccÃÃÃÃlll\023\023\023"+
//                "!!!!!!!!ÿ\031\031\031\031\031\031\031ððððððåååååå!!!!!!!!ÿ\031\031\031\031\031\031\031ððððððããåååå!!!!!!!ÿÿ=\031\031\031\031\031\031\031ððððððãããããã!!!!!!!ÿÿ==\031\031\031\031\031\031ððððððãããããã"+
//                "!!!!!ÿÿÿ===\031\031\031\031\031ðððððããããããã######ÿÿ===\031\031ðððððããããããã########þþþþðððððããããããä########þþþþþöððððãããããää"+
//                "#########þþþþþÛäääää#########\006????äääää¢¢GGGGGGG55????ääää¢GGGGGG555555??äää"+
//                "FFFFFFFFF\007\007\007\007\007\007\007\bzzzz{{{{ÙÙÙääFFFFFFFFFFFF\007\007\007\b\b\b\bzzz{{{{{ÙÙÙÙæ*****FFFFFFFFF33\b\b\b\bzz{{{{{{ÙÙÙæ*******FFFF¦¦¦¦33\b\b\b\b\b||||{\024\024\024\024æ"+
//                "********¦¦¦¦¦¦¦¦¦3\b\b\b||||||\024\024\024\024\024§§§§KKKKK¦¦¦¦¦¦¦\t\t\t\t\t||||||\024\024\024\024\024KKKKKKKKKKKKK¦\t\t\t\t\t\t\t\tÑ||||\024\024\024\024\024KKKKKKKKKKKKKK\t\t\t\t\t\t\tÑÑÑÑqÓÓÓÓ\024}"+
//                "LLLLLLLLLLLLLL111111\n\nqqqqÓÓÓÓÓ}LLLLLLLLLLLLLL1111111\nqqqqqÓÓÓÓÓ¨¨¨¨¨¨¨LL]]]]]dddddddqqqqqqÓÓÓÓÓ¨¨¨¨¨¨¨¨¨]]]]dddddddddkqqqqpppÕÕ"+
//                "¯¯¯¯¯¯¯¯¨¨MMMMddddddkkkkkooooooÕ¯¯¯¯¯¯¯¯¯SSSMRRRRRRRkkkkkÆÆooooo¯¯¯SSSSSSSSS__RRRRRRRkkkkÆÆÆÆooo®®®®®®®®®®®¸¸¸¸`````````lÆÆÆÆÆÆÄ"+
//                "®®®®®®®®®®¸¸¸¸¸¸````````llllÄÄÄÄ®®®®®®®®­­­¸¸¸¸¸ccccc``ÃlllllÄÄÄ®®®®­­­­­­­­­­¸¸cccccccÃlllllÄÄÄ­­­­­­­­­­­­­­­­­cccccÃÃ.llll\023\023\023"+
//                "!!!!ÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031ððððððããããããî!!!ÿÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031ððððððããããããîÿÿÿÿÿÿÿÿÿÿ\031\031\031\031\031\031ððððððããããããîÿÿÿÿÿÿÿÿÿ\031\031\031\031\031öðððððããããããî"+
//                "ÿÿÿÿÿÿÿþ\031\031ööðððððããããããî#####ÿÿÿÿþþþööööððððããããããî#######þþþþþþööööðððãããããäî#######þþþþþþþööððããããääää"+
//                "########þþþþþþäääää¢¢¢¢¢¢¢¢¢??????äääää¢¢¢¢¢¢¢¢555?????ääää¢¢¢¢¢¢5555555???äää"+
//                "FFFFFFFF5555555\b\b\b\bzz{{{{ÙæææFFFFFFFFFFFFF\b\b\b\b\b\b\b\b{{{{{{ÙÙÙææ**FFFFFFFFFFFF\b\b\b\b\b\b\b\b{{{{{{ÙÙææ******FFFFF¦¦¦¦\b\b\b\b\b\b||||||\024\024\024\024æ"+
//                "*******¦¦¦¦¦¦¦¦¦\t\t\b\b\b||||||\024\024\024\024ç§§§§§§§§¦¦¦¦¦¦¦\t\t\t\t\t\t||||||\024\024\024\024ç§§§§§§§KKKKK¦\t\t\t\t\t\t\t\t\t|||||\024\024\024\024ç§§§§LKKKKKKKKK\t\t\t\t\t\t\t\n\nqqqÓÓ}}}}"+
//                "LLLLLLLLLLLLL111111\n\n\nqqqqÓÓÓÓ}}LLLLLLLLLLLLL1111111\n\nqqqqqÓÓÓÓ}¨¨¨¨¨¨¨¨LLLLL11111111\nqqqqqpÓÓÓÕ¨¨¨¨¨¨¨¨¨¨MMMMMddddddd\013\013\013qppppÕÕ"+
//                "¨¨¨¨¨¨¨¨¨MMMMMMMRRddkkkk\foooooÕÕ¯¯¯¯¯¯¯¯SSMMMRRRRRRRRkkk\f\foooooÕ®®®®®®®®®®®®RRRRRRRRRRkk\f\fÆoooon®®®®®®®®®®®¸¸¸`````````lllÆÆÆoÄn"+
//                "®®®®®®®®®­­­­­¸````````lllllÄÄÄÄ®®®®®®­­­­­­­­­­```````lllllÄÄÄÄ­­­­­­­­­­­­­­­­ccccccQ.llll\023\023\023\023­­­­­­­­­­­­­­­­­cccc.....bb\023\023\023\023"+
//                "ÿÿÿÿÿÿÿÿÿÿ\031\031\031\031öööðððððãããããîîÿÿÿÿÿÿÿÿÿ\031\031\031\031öööðððððãããããîîÿÿÿÿÿÿÿÿÿ\031\031ööööðððððãããããîîÿÿÿÿÿÿÿþ\031öööööðððããããããîî"+
//                "ÿÿÿÿÿþþþöööööðððããããããîîÿþþþþþþþööööööððããããããîî####þþþþþþþþþööööööïïããããäîî######þþþþþþþþþöööïïïãääääî"+
//                "¢¢¢¢¢¢¢¢þþ????ääääää¢¢¢¢¢¢¢¢¢??????äääää¢¢¢¢¢¢¢5555?????ääää¢¢¢¢¢55555555???ääää"+
//                "FFFFFF55555555\b\b\b\b\b\bææææFFFFFFFFFFFF5\b\b\b\b\b\b\b\b{{{{{ÙÙææææFFFFFFFFFFFFF\b\b\b\b\b\b\b\b|ææææ***FFFFFFFF¦¦¦\b\b\b\b\b\b\b|||||\024\024\024ææ"+
//                "§§§§§§¦¦¦¦¦¦¦¦¦\t\t\t\b\b||||||\024\024çç§§§§§§§§§¦¦¦¦¦\t\t\t\t\t\t\t||||\024\024çç§§§§§§§§§§§K\t\t\t\t\t\t\t\t\t\n|||\024\024çç§§§§§§LLLLLLLE\t\t\t\t\t\n\n\n\nqqÓÓ}}}}ç"+
//                "LLLLLLLLLLLLL11111\n\n\n\n\nqqqÓÓ}}}}¨¨¨LLLLLLLLLL1111111\n\n\nqqqpÓÓ}}}¨¨¨¨¨¨¨¨¨¨MMMM111111\n\nqqqpppppÕÕ¨¨¨¨¨¨¨¨¨¨MMMMMMddddd\013\013\013\013\013pppÕÕÕ"+
//                "¨¨¨¨¨¨¨¨¨MMMMMMRRRRRRk\f\f\foooooÕÕ®®®®®®¨¨MMMMMRRRRRRRRR\f\f\f\foooooÕ®®®®®®®®®®®NNNRRRRRRRR\f\f\f\foooonn®®®®®®®®®®­­­NNN```````lll\fooÄnn"+
//                "®®®®®®®®­­­­­­`````````lllllÄÄÄÄ®®®®­­­­­­­­­­­­`````QQlllllÄÄÄÄ­­­­­­­­­­­­­­­­cc``QQQ..lll\023\023\023\023­­­­­­­­­­­­­­­­­ccQQ.....bb\023\023\023\023"+
//                "ÿÿÿÿÿÿÿþöööööööööðïïããããîîîÿÿÿÿÿÿþþööööööööðïïããããîîîÿÿÿÿÿþþþööööööööïïïããããîîîÿÿÿÿþþþþööööööööïïïïãããîîî"+
//                "ÿþþþþþþööööööööïïïïãããîîîþþþþþþþþöööööööïïïïïãäîîîþþþþþþþþöööööïïïïïääîîî¢¢¢¢¢¢¢þþþþþþ?öööïïïïääääîî"+
//                "¢¢¢¢¢¢¢¢???????äääääî¢¢¢¢¢¢¢¢¢??????äääää¢¢¢¢¢¢¢5555?????ääää¢¢¢¢555555555???ææææ"+
//                "¡¡¡55555555555\b\b\b\b\bæææææFFFFFFFFFF555\b\b\b\b\b\b\bæææææFFFFFFFFFFFF6\b\b\b\b\b\b\bææææFFFFFFFFFFF¦¦6\b\b\b\b\b\b|||çççæ"+
//                "§§§§§§$$$¦¦¦¦¦\t\t\t\b\b\b|||çççç§§§§§§§§§§$¦\t\t\t\t\t\t\t\t|||çççç§§§§§§§§§§§EEE\t\t\t\t\t\t\n\n\n}ççç§§§§§§§LLLLEEEEEE\t\n\n\n\n\n\nq}}}}}}ç"+
//                "LLLLLLLLLLLL111111\n\n\n\n\nqq/}}}}}}¨¨¨¨¨¨LLLLLL1111111\n\n\n\nqqppp}}}}¨¨¨¨¨¨¨¨¨¨MMMMM11111\013\013\013\013\013ppppÕÕÕ¨¨¨¨¨¨¨¨¨MMMMMMMMdd1\013\013\013\013\013ppppÕÕÕ"+
//                "¨¨¨¨¨¨¨¨MMMMMMMRRRRRR\f\f\f\fooooÕÕÕ¨¨¨¨¨¨¨¨MMMMMRRRRRRRR\f\f\f\f\foooonÕ®®®®®®®®®NNNNNNRRRRRRR\f\f\f\f\foonnn®®®®®®®®­­­NNNNNNN````ll\f\f\foÄnnn"+
//                "®®®®®­­­­­­­­NNNN````QQllllÄÄÄÄÄ­­­­­­­­­­­­­­­````QQQQQllll\023\023\023\023­­­­­­­­­­­­­­­­`QQQQQ...bbb\023\023\023\023­­­­­­­­­­­­­­­­­QQQ.....bbb\023\023\023\023"+
//                "ÿÿÿÿþþööööööööïïïïïïãîîîîÿÿÿþþþööööööööïïïïïïãîîîîÿÿÿþþþööööööööïïïïïïîîîîîþþþþþööööööööïïïïïïîîîîî"+
//                "þþþþþþöööööööïïïïïïîîîîîþþþþþþþ÷ööööööïïïïïïîîîîîþþþþþþþöööööïïïïïïäîîîî¢¢¢¢¢¢¢¢??????öïïïïäääîîî"+
//                "¢¢¢¢¢¢¢¢???????ääääîî¢¢¢¢¢¢¢¢5??????ääääî¢¢¢¢¢¢55555?????äææææ¡¡¡¡¡55555555?@@@@æææææ"+
//                "¡¡¡¡¡¡5555555@@@@@@ææææææ¡¡¡¡¡¡FF555666@@\b\b\bææææææFFFFFFFFFF66666\b\b\b\b\bæææææ$$$$$$$$$$$666666\b\b\bçççç"+
//                "§$$$$$$$$$$$$66666\b\b|çççç§§§§§§§$$$$$EE\t\t\t\t\tAçççç§§§§§§§§§EEEEEEE\t\t\n\n\n\n}çççç§§§§§§§§LEEEEEEEE\n\n\n\n\n\n\n}}}}}}}ç"+
//                "LLLLLLLLLLLEEEEEE\n\n\n\n\n\n///}}}}}}¨¨¨¨¨¨¨¨LLLM111111\n\n\n\n\013\013/ppp}}}}¨¨¨¨¨¨¨¨¨MMMMMM1111\013\013\013\013\013\013ppppÕÕÕ¨¨¨¨¨¨¨¨MMMMMMMMMRR\013\013\013\013\013\013pppÕÕÕÕ"+
//                "¨¨¨¨¨¨¨¨MMMMMMMRRRRR\f\f\f\f\f\fooÕÕÕÕ¨¨¨¨¨¨¨MMMMMRRRRRRRRR\f\f\f\f\fooonnn®®®®®®®®NNNNNNNNRRRRR\f\f\f\f\f\fonnnn®®®®®®®­­­NNNNNNNNNNQQl\f\f\f\f\rnnnn"+
//                "­­­­­­­­­­­­NNNNNNNQQQQllllÄÄÄmn­­­­­­­­­­­­­­NNNQQQQQQ.bbb\023\023\023\023m­­­­­­­­­­­­­­¬¬QQQQQ...bbbb\023\023\023m­­­­­­­­­­¬¬¬¬¬¬QQQQ.....bbb\023\023\023\023"+
//                "ýýýýýý÷÷÷ööööööïïïïïïîîîîîýýýýýý÷÷÷ööööööïïïïïïîîîîîýýýýýý÷÷÷ööööööïïïïïïîîîîîýýýýýý÷÷÷÷öööööïïïïïïîîîîî"+
//                "ýýýýýýý÷÷÷÷öööööïïïïïïîîîîîýýýýýýýý÷÷÷÷÷ööööïïïïïïîîîîî¢¢¢¢ýýýýýýý??÷÷÷÷÷öööïïïïïïîîîîî¢¢¢¢¢¢¢ý??????÷÷÷ïïïïääîîîî"+
//                "¢¢¢¢¢¢¢¢???????ääîîîî¢¢¢¢¢¢¢¢5??????äææîî¡¡¡¡¡¡¡5555???@@@ææææææ¡¡¡¡¡¡¡¡5555@@@@@@ææææææ"+
//                "¡¡¡¡¡¡¡¡¡55@@@@@@@@æææææææ¡¡¡¡¡¡¡¡¡66666@@@@@ææææææ$$$$$$$$$666666666@æææææ$$$$$$$$$$$66666666ççççç"+
//                "$$$$$$$$$$$$$66666AAççççç§§§$$$$$$$$$EEEEAAAAAççççç§§§§§§§§EEEEEEEEE\n\nAAA}}çççç§§§§§§§§EEEEEEEEE\n\n\n\n\n///}}}}}çç"+
//                "¨¨¨¨¨LLLLEEEEEEEE\n\n\n\n\n////}}}}}}¨¨¨¨¨¨¨¨)))MM1110000\013\013\013////}}}}}¨¨¨¨¨¨¨¨)MMMMMMM0000\013\013\013\013ppppÕÕÕÕ¨¨¨¨¨¨¨¨MMMMMMMMM00\013\013\013\013\013\013pppÕÕÕÕ"+
//                "¨¨¨¨¨¨¨MMMMMMMMRRRRR\f\f\f\f\f\fooÕÕÕÕMMMNNRRRRRRR\f\f\f\f\f\foonnnn®®NNNNNNNNNNNRRR\f\f\f\f\f\fnnnnn®®®®­­­­NNNNNNNNNNNNQQ\f\f\f\r\r\rnnnn"+
//                "¬¬¬¬¬¬¬¬¬¬¬¬NNNNNNQQQQQllb\023\023mmm~¬¬¬¬¬¬¬¬¬¬¬¬¬¬NNQQQQQQ..bbb\023\023mmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQQQQ...bbbb\023\023mm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQQ.....bbbb\023\023\023m"+
//                "ýýýýýýýýýýýý÷÷÷÷÷÷ööïïïïïïîîîîîîýýýýýýýýýýýý÷÷÷÷÷÷ööïïïïïïîîîîîîýýýýýýýýýýýý÷÷÷÷÷÷ööïïïïïïîîîîîîýýýýýýýýýýýý÷÷÷÷÷÷÷öïïïïïïîîîîîî"+
//                "ýýýýýýýýýýýý÷÷÷÷÷÷÷öïïïïïïîîîîîîýýýýýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïïîîîîîîýýýýýýýýýýýý÷÷÷÷÷÷ïïïïïîîîîîî¢¢¢ýýýýýýýýý?÷÷÷÷ïïïîîîîîî"+
//                "¡¡¡¡¢¢¢ýý??????äîîîîî¡¡¡¡¡¡¡¡5?????@æææííí¡¡¡¡¡¡¡¡¡5@@@@@@@ææææææí¡¡¡¡¡¡¡¡¡¡@@@@@@@@æææææææ"+
//                "¡¡¡¡¡¡¡¡¡¡@@@@@@@@@æææææææ¡¡¡¡¡¡¡¡666666@@@@ææææææ$$$$$$$$6666666666æææææ$$$$$$$$$$$6666666ççççç"+
//                "$$$$$$$$$$$$$666AAAAççççç$$$$$$$$$$$$EEEAAAAAAAççççç§§§§$$$$$EEEEEEEAAAAAAA}}}çççç§§§§§§EEEEEEEEEEE\n\n\n\n////}}}}}çè"+
//                "¨¨¨))))))EEEEEEE0000\013/////}}}}}è¨¨¨¨)))))))))0000000\013\013\013////}}}Õè¨¨¨¨)))))))))MM00000\013\013\013\013//ppÕÕÕÕ¨¨¨¨)))))))MMMMM0000\013\013\013\013\013ppÕÕÕÕÕ"+
//                "))))MMMMMRRRRR\f\f\f\f\f\f\fonÕÕÕéNNNNNRRRRR\f\f\f\f\f\f\rnnnnnNNNNNNNNNNR\f\f\f\f\r\r\rnnnnn¬¬¬¬¬¬¬¬¬NNNNNNNNNNQQQ\r\r\r\r\r\rnnn~"+
//                "¬¬¬¬¬¬¬¬¬¬¬¬NNNNNQQQQQQbbb\rmmmm~¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬NQQQQQQ.bbbb\023mmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬OOOQQ....bbb\023\023mmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬OOO.....bbbb\023\023mm"+
//                "ýýýýýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïîîîîîîîýýýýýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïîîîîîîîýýýýýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïîîîîîîîýýýýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïïîîîîîî"+
//                "ýýýýýýýýý÷÷÷÷÷÷÷ïïïïïïîîîîîîýýýýýýýý÷÷÷÷÷÷ïïïï\032îîîîîîýýýýýýý÷÷÷÷÷ïïï\032\032îîîîîýýýýýý÷÷÷÷\032\032\032ííííí"+
//                "¡¡¡    \032\032\032ííííí¡¡¡¡¡¡@@@æææíííí¡¡¡¡¡¡¡@@@@@ææææææíí¡¡¡¡¡¡¡¡@@@@@@@ææææææææ"+
//                "¡¡¡¡¡¡¡¡¡@@@@@@@@ææææææë¡¡¡¡¡¡¡¡666666@@@ææææëë$$$$$$$$666666666ææëëë$$$$$$$$$$6666666çççëë"+
//                "$$$$$$$$$$$$$6AAAAAAççççç$$$$$$$$$$$$$AAAAAAAAAççççç$$$$$$$$$EEEEEEAAAAAAAA}}çççèèEEEEEEEE0AAAA///}}}}èèè"+
//                ")))))))))))EE0000000//////}}}èèè)))))))))))))0000000\013\013/////}}Õèè))))))))))))))000000\013\013\013////ÕÕééé)))))))))))))))00000\013\013\013\013/ppÕÕééé"+
//                ")))))RRRR0\f\f\f\f\f\r\rnnéééNNNNRRR\f\f\f\r\r\r\r\rnnnnéNNNNNNNNN\r\r\r\r\r\r\rnnnn~¬¬¬¬¬¬¬¬¬¬NNNNNNNNQQQ\r\r\r\r\r\r\rn~~~"+
//                "¬¬¬¬¬¬¬¬¬¬¬¬¬NNNOOOOQQbbb\rmmmm~~¬¬¬¬¬¬¬¬¬¬¬¬¬¬OOOOOOO..bbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬OOOOOO..bbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬OOOOO...bbbbbmmaa"+
//                "ýýýýýýýý÷÷÷÷÷÷÷÷ïïïï\032\032îîîîîîýýýýýýýý÷÷÷÷÷÷÷÷ïïïï\032\032îîîîîîýýýýýýý÷÷÷÷÷÷÷ïïïï\032\032îîîîîîýýýýýýý÷÷÷÷÷÷÷ïïï\032\032\032îîîîí"+
//                "ýýýýýý÷÷÷÷÷÷ïï\032\032\032\032íííííýýýý ÷÷÷÷÷ï\032\032\032\032ííííí     ÷÷÷\032\032\032\032ííííí      ÷\032\032\032\032ííííí"+
//                "    \032\032\032\032ííííí¡  \032\032\032\032íííí¡¡¡@@@æææææííí¡¡¡¡@@@@ææææææëë"+
//                "      @@@@ææææëëë         66666@æëëëëë         6666666ëëëëë$$$$$$$$$7777777Aççëëë"+
//                "$$$$$$$$$$$777AAAAAAçççç\034$$$$$$$$$$$$AAAAAAAAAAçççè\034EEEAAAAAAAAAèèèèèEEE000AAAAèèèè"+
//                "))00000000B///èèèè)))))))))))))0000000BBB////éééèè))))))))))))))000000\013BBBB/ééééé)))))))))))))))00000DDDDBééééé"+
//                "))&&&&&DDDDDDD\rééé&&&&&&DDD\r\r\r\r\rnnéNNNN&&&&\r\r\r\r\r\r\rnn~~~NNNNNOOOOO\r\r\r\r\r\r\r~~~~"+
//                "¬¬¬¬¬¬¬¬¬¬¬¬¬©OOOOOOOObb\r\rmmmm~~¬¬¬¬¬¬¬¬¬¬¬¬¬OOOOOOOOObbbbmmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬OOOOOOOO.bbbPmmmaa¬¬¬¬¬¬¬¬¬¬¬¬¬¬OOOOOOO..bbPPPaaaa"+
//                "ýýý  ÷÷÷÷÷÷ü\033\033\033\033\032\032\032íííííýý   ÷÷÷÷÷÷ü\033\033\033\033\032\032\032ííííí    ÷÷÷÷÷üü\033\033\033\033\032\032\032ííííí     ÷÷÷üüü\033\033\033\032\032\032ííííí"+
//                "      ÷üüüü\033\033\033\032\032\032ííííí       üüüü\033\033\032\032\032\032ííííí       üüü\033\032\032\032\032ííííí        üü\033\032\032\032\032ííííí"+
//                "      \032\032\032\032ííííí   ø\032\032\032\032ííííûøøøø\032\032ííìì   @@øøøøøëëëëì"+
//                "       @ëëëëëëë          77777ëëëëëë         7777777ëëëëë        777777777ëëëëë"+
//                "$$$$$$$$7777777AAAAAç\034\034\034\0347777AAAAAAAAè\034\034\034\03488888AAAAAAèèèèè888888999èèèèè"+
//                "00000999BBBèèèè))))))))))))0000000BBBBBBééèè)))))))))))))000000BBBBBBéééé)))))))))&&00DDDDDDDBéééé"+
//                "&&&&&&&DDDDDDDééé&&&&&&&DDDD\r\r\ré&&&&&&&&\r\r\r\r\r\r\r~~~~\177ªªªªªªªªªªª©©©©OOOOO\r\r\r\r\r\r\r~~~~\177"+
//                "¬¬¬¬¬¬¬¬¬¬©©©©©OOOOOOO\r\r\rmmmm~\177\177¬¬¬¬¬¬¬¬¬¬¬©©©©OOOOOOOPPPPmmmaaa¬¬¬¬¬¬¬¬¬¬¬¬©©OOOOOOOOPPPPPaaaaa¬¬¬¬¬¬¬¬¬¬¬«««OOOOOO'''PPPPPaaaa"+
//                "     üüüüüüüü\033\033\033\033\032\032\032ííííí     üüüüüüüü\033\033\033\033\032\032\032ííííí     üüüüüüüü\033\033\033\033\032\032\032ííííí     üüüüüüüü\033\033\033\033\032\032\032ííííí"+
//                "       üüüüüüü\033\033\033\033\032\032\032ííííí       üüüüüüü\033\033\033\033\032\032\032ííííí       üüüüüüü\033\033\033\033\032\032\032ííííí         üüüüüü\033\033\033\032\032\032ííííí"+
//                "      ûûûûûûûøøø\032\032\032ííííí  ûûûûûûøøøøø\032\032ììììûûûûûøøøøøøììììì     ûûûûûûøøøøëëëììì"+
//                "        ùøëëëëëëë         777777ùùëëëëëë        77777777ùëëëëëë        777777777ùëëëëë"+
//                "      7777777777AAAA\034\034\034\034\034\03488888888AAAA\034\034\034\034\034\0348888888AAèèèè\034\034888889999èèèèè"+
//                "8999999BBBèèèè99999BBBBBBéèè))))))))))00999BBBBBéééê))&&&&&&:::DDDDDéééê"+
//                "&&&&&&&&DDDDDDDééê&&&&&&&&&DDDDD\r\177\177&&&&&&&&&D\r\r\r\r\r~\177\177\177\177ªªªªªªªªªª©©©©©©&&&&\r\r\r\r\r\r<~\177\177\177\177"+
//                "ªªªªªªªªª©©©©©©©OOOOOPPPP<<<<\177\177\177ª«««««««««©©©©©©OOOO'PPPPPPaaaaa«««««««««««««©©©O'''''PPPPP\016aaaa((((((((«««««««'''''''PPPPPP\016aaa"+
//                "\037\037\037\037\037\037\037\037\037  üüüüüüüüü\033\033\033\033\033\032\032ííííí\037\037\037\037\037\037\037\037    üüüüüüüü\033\033\033\033\033\032\032ííííí\037\037\037\037\037\037\037\037    üüüüüüüü\033\033\033\033\033\032\032ííííí\037\037\037\037\037\037\037\037    üüüüüüüü\033\033\033\033\033\032\032ííííí"+
//                "\037\037\037\037\037\037\037\037    üüüüüüüü\033\033\033\033\033\032\032ííííí\037\037\037\037\037\037\037     üüüüüüüü\033\033\033\033\033\032\032ííííí\037\037\037      üüüüüüüø\033\033\033\032\032\032ííííí      ûûûûûûûøøøøø\032\032íííìì"+
//                "   ûûûûûûøøøøøøøìììììûûûûûûøøøøøøìììììûûûûûøøøøøìììììì     ûûûûûøøøøøëììììì"+
//                "     ûûùùùùùùëëëëìì     77\036\036ùùùùùëëëëëë     777777\036\036\036\036\036ùùùùùëëëëë     7777777\036\036\036\036\036ùùùù\034\034\034ëë"+
//                "%%%%%%%%77777777\036\036\036\036\036\036úúù\034\034\034\034\034\034\034%%%%%%%%8888888888\036\036úúú\034\034\034\034\034\03488888888889úè\034\034\034\034\034888889999úèèèè\034"+
//                "9999999BBèèèè99999BBBBBêêê::::::BBBBêêê&&&&&&&::::::DDDéêêê"+
//                "&&&&&&&&&::DDDDDêêê&&&&&&&&&&DDDDDêªªªªªªªªªª&&&&&&&&&;;;;;;CCC\177\177\177ªªªªªªªªª©©©©©©©&&&;;;;;<<<<\177\177\177\177"+
//                "ªªªªªªªª«©©©©©©©©''''PPP<<<<<\177\177\177««««««««««««©©©©'''''PPPPP<<\016aa\017(((((«««««««««©'''''''PPPPP\016\016\016a\017((((((((((««««''''''''PPPPP\016\016\016\016\017"+
//                "\037\037\037\037\037\037\037\037\037\037\037üüüüüüüü\033\033\033\033\033\033\033\032ííííí\037\037\037\037\037\037\037\037\037\037\037üüüüüüüü\033\033\033\033\033\033\033\032ííííí\037\037\037\037\037\037\037\037\037\037\037üüüüüüüü\033\033\033\033\033\033\033\032ííííí\037\037\037\037\037\037\037\037\037\037\037üüüüüüüü\033\033\033\033\033\033\032\032ííííí"+
//                "\037\037\037\037\037\037\037\037\037\037\037 üüüüüüüü\033\033\033\033\033\032\032íííìì\037\037\037\037\037\037\037\037  üüüüüûûûøøø\033\033\032\032íìììì\037\037\037\037\037 ûûûûûûûûøøøøøø\032ììììì\037ûûûûûûûøøøøøøìììììì"+
//                "ûûûûûûøøøøøøììììììûûûûûøøøøøøììììììûûûûûûøøøøøììììììûûûûùùùøøøìììììì"+
//                "ûûùùùùùùùëëìììì\036\036\036\036\036ùùùùùùùëëëëì77\036\036\036\036\036\036\036ùùùùùùëëëëë%%%%%%777\036\036\036\036\036\036\036\036ùùùùù\034\034\034\034\034"+
//                "%%%%%%%%%%88888\036\036\036\036\036\036úúúù\034\034\034\034\034\034\034%%%%%%%%%8888888\036\036\036\036úúúúúú\034\034\034\034%%%%%%8888888899úúúúúúú888899999úúúúúúú"+
//                "9999999BBúèèè999999BB\035\035êêêê::::::::B\035\035\035êêêê&&&&&&&::::::::\035\035\035\035\035êêêê"+
//                "&&&&&&&&&&:::::DDCCCCCCêêêªªªªªªªª&&&&&&&&&&:;;;;;CCCCCªªªªªªªªªª&&&&&&&&;;;;;;;CCCªªªªªªªªª©©©©©©©&&;;;;;;<<<<\177"+
//                "ª«««««««««©©©©©©'''';;P<<<<<<\177\177««««««««««««©©©''''''PPPP<<<\016\017\017\017((((((((««««««''''''''PPPP\016\016\016\016\017\017((((((((((((««''''''''PPPP\016\016\016\016\017\017"+
//                "\037\037\037\037\037\037\037\037\037\037\037üüüüüüüü\033\033\033\033\033\033\033\032ììììì\037\037\037\037\037\037\037\037\037\037\037üüüüüüüü\033\033\033\033\033\033\033\032ììììì\037\037\037\037\037\037\037\037\037\037\037üüüüüüüûøø\033\033\033\033\033ìììììì\037\037\037\037\037\037\037\037\037\037\037\037üüüüûûûøøøø\033\033\033ìììììì"+
//                "\037\037\037\037\037\037\037\037\037\037\037\037ûûûûûûûøøøøøøøìììììì\037\037\037\037\037\037\037ûûûûûûûøøøøøøøìììììì\037\037\037ûûûûûûûøøøøøøøììììììûûûûûûûøøøøøøìììììì"+
//                "ûûûûûûøøøøøøììììììûûûûûøøøøøìììììììûûûûûøøøøøìììììììûûûùùùùùùììììììì"+
//                "\036\036\036ùùùùùùùùììììì\036\036\036\036\036\036\036ùùùùùùùëëììì%%%\036\036\036\036\036\036\036\036ùùùùùùëëë%%%%%%%%\036\036\036\036\036\036\036\036\036ùùùùù"+
//                "%%%%%%%%%%%88\036\036\036\036\036\036\036\036úúúù%%%%%%%%%%88888\036\036\036\036úúúúúú%%%%%%%%88888899úúúúúúúú8899999úúúúúúúú"+
//                "9999999úúú\035\035\035ê::::::\035\035\035\035\035\035\035êêêêê::::::::\035\035\035\035\035\035êêêêê&&&&&:::::::::\035\035\035\035\035êêêêê"+
//                "ªªªª&&&&&&&&&&&::::::;;CCCCCCêêêªªªªªªª&&&&&&&&&&;;;;;;;CCCCªªªªªªªªª©&&&&&&&;;;;;;;CCCCªªªªªªªª©©©©©©©©&;;;;;;;<<<<"+
//                "«««««««««««©©©©''''';;;<<<<<<\017\017(((((««««««««©'''''''PPP<<<<\017\017\017\017((((((((((«««''''''''PPPPP\016\016\017\017\017\017((((((((((((('''''''''PPPP\016\016\017\017\017\017"+
//                "\037\037\037\037\037\037\037\037\037\037\037\037ûûûûûûûøøøøøøììììììì\037\037\037\037\037\037\037\037\037\037\037\037ûûûûûûûøøøøøøììììììì\037\037\037\037\037\037\037\037\037\037\037ûûûûûûûûøøøøøøììììììì\037\037\037\037\037\037\037\037\037\037\037ûûûûûûûûøøøøøøììììììì"+
//                "\037\037\037\037\037\037\037\037ûûûûûûûûøøøøøøììììììì\037\037\037\037ûûûûûûûøøøøøøììììììì\037ûûûûûûûøøøøøøìììììììûûûûûûøøøøøøììììììì"+
//                "ûûûûûøøøøøøìììììììûûûûûûøøøøøìììììììûûûûùùùùùøìììììììûûùùùùùùùùìììììì"+
//                "\036\036\036\036\036ùùùùùùùùììììì\036\036\036\036\036\036\036ùùùùùùùùììììì%%%%%%\036\036\036\036\036\036\036\036ùùùùùùù%%%%%%%%%%\036\036\036\036\036\036\036\036\036ùùùù"+
//                "%%%%%%%%%%%%\036\036\036\036\036\036\036\036úúúú%%%%%%%%%%%888\036\036\036\036\036úúúúúú%%%%%%%%%%888888\036úúúúúúúú89999úúúúúúúú"+
//                "99999\035\035\035\035\035\035\035\035êê::::::\035\035\035\035\035\035\035\035êêêêê::::::::\035\035\035\035\035\035\035êêêêê&&&&:::::::::\035\035\035\035\035\035êêêêê"+
//                "ªªª&&&&&&&&&&&&::::;;;CCCCCCêêêªªªªªªª&&&&&&&&&;;;;;;;CCCCCªªªªªªªª©©&&&&&&;;;;;;;;CCCªªª««««««©©©©©©';;;;;;;;<<<"+
//                "««««««««««««©©''''';;;;<<<<<\017\017\017\017((((((((«««««''''''''PPP<<<\017\017\017\017\017((((((((((((«''''''''PPPP\016\016\017\017\017\017\017((((((((((((('''''''''PPP\016\016\017\017\017\017\017"
//        ;
        
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
//        png8.palette = new PaletteReducer(PALETTE, labRoughMetric);        
//        int[][] BIG_ROLLER_BONUS_RAMP_VALUES = new int[256][4];
//        for (int i = 1; i < PALETTE.length; i++) {
//            int color = BIG_ROLLER_BONUS_RAMP_VALUES[i | 128][2] = BIG_ROLLER_BONUS_RAMP_VALUES[i][2] =
//                    PALETTE[i];             
////            r = (color >>> 24);
////            g = (color >>> 16 & 0xFF);
////            b = (color >>> 8 & 0xFF);
//            luma = lumas[i];
//            warm = warms[i];
//            mild = milds[i];
//            BIG_ROLLER_BONUS_RAMP_VALUES[i | 64][1] = BIG_ROLLER_BONUS_RAMP_VALUES[i | 64][2] =
//                    BIG_ROLLER_BONUS_RAMP_VALUES[i | 64][3] = color;
//            BIG_ROLLER_BONUS_RAMP_VALUES[i | 192][0] = BIG_ROLLER_BONUS_RAMP_VALUES[i | 192][2] = color;
////            int co = r - b, t = b + (co >> 1), cg = g - t, y = t + (cg >> 1),
////                    yBright = y * 21 >> 4, yDim = y * 11 >> 4, yDark = y * 6 >> 4, chromO, chromG;
////            chromO = (co * 3) >> 2;
////            chromG = (cg * 3) >> 2;
////            t = yDim - (chromG >> 1);
////            g = chromG + t;
////            b = t - (chromO >> 1);
////            r = b + chromO;
//            r = MathUtils.clamp((int) ((luma * 0.83f + (warm *  0.625f - mild * 0.5f) * 0.7f) * 256f), 0, 255);
//            g = MathUtils.clamp((int) ((luma * 0.83f + (warm * -0.375f + mild * 0.5f) * 0.7f) * 256f), 0, 255);
//            b = MathUtils.clamp((int) ((luma * 0.83f + (warm * -0.375f - mild * 0.5f) * 0.7f) * 256f), 0, 255);
//            BIG_ROLLER_BONUS_RAMP_VALUES[i | 192][1] = BIG_ROLLER_BONUS_RAMP_VALUES[i | 128][1] =
//                    BIG_ROLLER_BONUS_RAMP_VALUES[i | 64][0] = BIG_ROLLER_BONUS_RAMP_VALUES[i][1] =
//                            MathUtils.clamp(r, 0, 255) << 24 |
//                                    MathUtils.clamp(g, 0, 255) << 16 |
//                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//            r = MathUtils.clamp((int) ((luma * 1.35f + (warm *  0.625f - mild * 0.5f) * 0.65f) * 256f), 0, 255);
//            g = MathUtils.clamp((int) ((luma * 1.35f + (warm * -0.375f + mild * 0.5f) * 0.65f) * 256f), 0, 255);
//            b = MathUtils.clamp((int) ((luma * 1.35f + (warm * -0.375f - mild * 0.5f) * 0.65f) * 256f), 0, 255);
//            BIG_ROLLER_BONUS_RAMP_VALUES[i | 192][3] = BIG_ROLLER_BONUS_RAMP_VALUES[i | 128][3] =
//                    BIG_ROLLER_BONUS_RAMP_VALUES[i][3] =
//                            MathUtils.clamp(r, 0, 255) << 24 |
//                                    MathUtils.clamp(g, 0, 255) << 16 |
//                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//            r = MathUtils.clamp((int) ((luma * 0.65f + (warm *  0.625f - mild * 0.5f) * 0.8f) * 256f), 0, 255);
//            g = MathUtils.clamp((int) ((luma * 0.65f + (warm * -0.375f + mild * 0.5f) * 0.8f) * 256f), 0, 255);
//            b = MathUtils.clamp((int) ((luma * 0.65f + (warm * -0.375f - mild * 0.5f) * 0.8f) * 256f), 0, 255);
//            BIG_ROLLER_BONUS_RAMP_VALUES[i | 128][0] = BIG_ROLLER_BONUS_RAMP_VALUES[i][0] =
//                    MathUtils.clamp(r, 0, 255) << 24 |
//                            MathUtils.clamp(g, 0, 255) << 16 |
//                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//        }
//        sb.setLength(0);
//        sb.ensureCapacity(2800);
//        sb.append("private static final int[][] BIG_ROLLER_BONUS_RAMP_VALUES = new int[][] {\n");
//        for (int i = 0; i < 256; i++) {
//            sb.append("{ 0x");
//            StringKit.appendHex(sb, BIG_ROLLER_BONUS_RAMP_VALUES[i][0]);
//            StringKit.appendHex(sb.append(", 0x"), BIG_ROLLER_BONUS_RAMP_VALUES[i][1]);
//            StringKit.appendHex(sb.append(", 0x"), BIG_ROLLER_BONUS_RAMP_VALUES[i][2]);
//            StringKit.appendHex(sb.append(", 0x"), BIG_ROLLER_BONUS_RAMP_VALUES[i][3]);
//            sb.append(" },\n");
//
//        }
//        System.out.println(sb.append("};"));
//        PALETTE = new int[256];
//        for (int i = 0; i < 64; i++) {
//            System.arraycopy(BIG_ROLLER_BONUS_RAMP_VALUES[i], 0, PALETTE, i << 2, 4);
//        }
//        sb.setLength(0);
//        sb.ensureCapacity((1 + 12 * 8) * (PALETTE.length >>> 3));
//        for (int i = 0; i < (PALETTE.length >>> 3); i++) {
//            for (int j = 0; j < 8; j++) {
//                sb.append("0x").append(StringKit.hex(PALETTE[i << 3 | j]).toUpperCase()).append(", ");
//            }
//            sb.append('\n');
//        }
//        System.out.println(sb.toString());
//        sb.setLength(0);
//
//        pix = new Pixmap(256, 1, Pixmap.Format.RGBA8888);
//        for (int i = 0; i < PALETTE.length - 1; i++) {
//            pix.drawPixel(i, 0, PALETTE[i + 1]);
//        }
//        //pix.drawPixel(255, 0, 0);
//        png8.palette = new PaletteReducer(PALETTE, labRoughMetric);
//        try {
//            png8.writePrecisely(Gdx.files.local("BigRollerBonus.png"), pix, false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        p2 = new Pixmap(1024, 32, Pixmap.Format.RGBA8888);
//        for (int red = 0; red < 32; red++) {
//            for (int blu = 0; blu < 32; blu++) {
//                for (int gre = 0; gre < 32; gre++) {
//                    p2.drawPixel(red << 5 | blu, gre, PALETTE[png8.palette.paletteMapping[
//                            ((red << 10) & 0x7C00)
//                                    | ((gre << 5) & 0x3E0)
//                                    | blu] & 0xFF]);
//                }
//            }
//        }
//        try {
//            png8.writePrecisely(Gdx.files.local("BigRollerBonus_GLSL.png"), p2, false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        pix = new Pixmap(256, 1, Pixmap.Format.RGBA8888);
//        for (int i = 1; i < 64; i++) {
////            pix.drawPixel(i-1, 0, PALETTE[i]);
//            pix.drawPixel(i-1, 0, PALETTE[i << 2 | 2]);
//            pix.drawPixel(i+63, 0, PALETTE[i << 2]);
//            pix.drawPixel(i+127, 0, PALETTE[i << 2 | 1]);
//            pix.drawPixel(i+191, 0, PALETTE[i << 2 | 3]);
//        }
//        png8.palette = new PaletteReducer(PALETTE);
//        try {
//            png8.writePrecisely(Gdx.files.local("BigRollerBonusMagicaVoxel.png"), pix, false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        
        
    }



    /**
     * Given a byte array, this writes a file containing a code snippet that can be pasted into Java code as the preload
     * data used by {@link PaletteReducer#exact(int[], String)}; this is almost never needed by external code. When 
     * using this for preload data, the byte array should be {@link PaletteReducer#paletteMapping}.
     * @param data the bytes to use as preload data, usually {@link PaletteReducer#paletteMapping}
     */
    public static void generatePreloadCode(final byte[] data){
        StringBuilder sb = new StringBuilder(data.length);
        for (int i = 0; i < data.length;) {
            sb.append('"');
            for (int j = 0; j < 0x80 && i < data.length; j++) {
                byte b = data[i++];
                switch (b)
                {
                    case '\t': sb.append("\\t");
                        break;
                    case '\b': sb.append("\\b");
                        break;
                    case '\n': sb.append("\\n");
                        break;
                    case '\r': sb.append("\\r");
                        break;
                    case '\f': sb.append("\\f");
                        break;
                    case '\"': sb.append("\\\"");
                        break;
                    case '\\': sb.append("\\\\");
                        break;
                    default:
                        if(Character.isISOControl(b))
                            sb.append(String.format("\\%03o", b));
                        else
                            sb.append((char)(b&0xFF));
                        break;
                }
            }
            sb.append('"');
            if(i != data.length)
                sb.append('+');
            sb.append('\n');
        }
        String filename = "bytes_" + TimeUtils.millis() + ".txt";
        Gdx.files.local(filename).writeString(sb.toString(), false, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
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
