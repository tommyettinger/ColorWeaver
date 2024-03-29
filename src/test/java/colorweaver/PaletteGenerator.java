package colorweaver;

import colorweaver.tools.StringKit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.io.IOException;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class PaletteGenerator extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Palette Stuff");
        config.setWindowedMode(1000, 600);
        config.setIdleFPS(10);
        config.setResizable(false);
        new Lwjgl3Application(new PaletteGenerator(), config);
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
                rmean = r1 + r2,
                r = r1 - r2,
                g = g1 - g2,
                b = b1 - b2,
                y = Math.max(r1, Math.max(g1, b1)) - Math.max(r2, Math.max(g2, b2));
//        return (((512 + rmean) * r * r) >> 8) + g * g + (((767 - rmean) * b * b) >> 8);
//        return (((0x580 + rmean) * r * r) >> 7) + g * g * 12 + (((0x5FF - rmean) * b * b) >> 8) + y * y * 8;
        return (((1024 + rmean) * r * r) >> 7) + g * g * 12 + (((1534 - rmean) * b * b) >> 8) + y * y * 14;
    }

//    public final double fastGaussian(RandomnessSource random) {
//        long a = random.nextLong(), b = random.nextLong();
//        a = (a & 0x0003FF003FF003FFL) + ((a & 0x0FFC00FFC00FFC00L) >>> 10);
//        b = (b & 0x0003FF003FF003FFL) + ((b & 0x0FFC00FFC00FFC00L) >>> 10);
//        a = (a & 0x000000007FF007FFL) + ((a & 0x0007FF0000000000L) >>> 40);
//        b = (b & 0x000000007FF007FFL) + ((b & 0x0007FF0000000000L) >>> 40);
//        return ((((a & 0x0000000000000FFFL) + ((a & 0x000000007FF00000L) >>> 20))
//                - ((b & 0x0000000000000FFFL) + ((b & 0x000000007FF00000L) >>> 20))) * 0x1p-10);
//    }
    
    private long state = 64L;
    
    private double nextDouble()
    {
        return ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L & 0x1FFFFFFFFFFFFFL) * 0x1p-53;
    }
    
    public void create() {
//        final float[] hues = {0.0f, 0.07179487f, 0.07749468f, 0.098445594f, 0.09782606f, 0.14184391f, 0.16522992f,
//                0.20281118f, 0.20285714f, 0.21867621f, 0.25163394f, 0.3141666f, 0.3715499f, 0.37061405f, 0.44054055f,
//                0.49561405f, 0.53289473f, 0.53312635f, 0.5931374f, 0.6494253f, 0.7192119f, 0.7562056f, 0.7564103f,
//                0.8037036f, 0.8703703f, 0.9282946f, 0.92884994f};
//        final float[] sats = {0.863354f, 0.2742616f, 0.8051282f, 0.7751004f, 0.519774f, 0.7768595f, 0.46031743f, 0.36086953f,
//                        0.9067358f, 0.94630873f, 0.20731705f, 0.91324204f, 0.6946903f, 0.4691358f, 0.74596775f,
//                        0.47698745f, 0.38f, 0.8846154f, 0.86624205f, 0.25777775f, 0.9575472f, 0.81385285f, 0.6453901f,
//                        0.746888f, 0.46153846f, 0.48863637f, 0.9395605f};
//        int[] PALETTE = new int[64];
        int[] PALETTE;

//        IntArray base = new IntArray(0x912);
//        base.addAll(Coloring.AURORA, 1, 255);
//        base.addAll(0x010101FF, 0x2D2D2DFF, 0x555555FF, 0x7B7B7BFF,
//                0x9F9F9FFF, 0xC1C1C1FF, 0xE1E1E1FF, 0xFFFFFFFF);
        /*
        int[] grayscale = {0x010101FF, 0x171717FF, 0x2D2D2DFF, 0x555555FF, 0x686868FF, 0x7B7B7BFF, 0x8D8D8DFF,
                0x9F9F9FFF, 0xB0B0B0FF, 0xC1C1C1FF, 0xD1D1D1FF, 0xE1E1E1FF, 0xF0F0F0FF, 0xFFFFFFFF};
//        int[] grayscale = {0x010101FF, 0x212121FF, 0x414141FF, 0x616161FF,
//                0x818181FF, 0xA1A1A1FF, 0xC1C1C1FF, 0xE1E1E1FF, 0xFFFFFFFF};
//        int[] grayscale = {0x010101FF, 0x414141FF,
//                0x818181FF, 0xC1C1C1FF, 0xFFFFFFFF};
//        int[] grayscale = {0x010101FF, 0x2D2D2DFF, 0x555555FF, 0x7B7B7BFF,
//                0x9F9F9FFF, 0xC1C1C1FF, 0xE1E1E1FF, 0xFFFFFFFF};
        base.addAll(grayscale);
//        DiverRNG rng = new DiverRNG("sixty-four");
//        MiniMover64RNG rng = new MiniMover64RNG(64);
        for (int i = 1; i <= 2240; i++) {
//            double luma = Math.pow(i * 0x1.c7p-11, 0.875), // 0 to 1, more often near 1 than near 0
            double luma = i / 2240.0;//, mild = 0.0, warm = 0.0;// 0.0 to 1.0
            luma = (Math.sqrt(luma) + luma) * 128.0;
            //0xC13FA9A902A6328FL * i
            //0x91E10DA5C79E7B1DL * i
//                    mild = ((DiverRNG.determineDouble(i) + DiverRNG.randomizeDouble(-i) - DiverRNG.randomizeDouble(123456789L - i) - DiverRNG.determineDouble(987654321L + i) + 0.5 - DiverRNG.randomizeDouble(123456789L + i)) * 0.4), // -1 to 1, curved random
//                    warm = ((DiverRNG.determineDouble(-i) + DiverRNG.randomizeDouble((i^12345L)*i) - DiverRNG.randomizeDouble((i^99999L)*i) - DiverRNG.determineDouble((987654321L - i)*i) + 0.5  - DiverRNG.randomizeDouble((123456789L - i)*i)) * 0.4); // -1 to 1, curved random
//                    mild = ((DiverRNG.determineDouble(i) + DiverRNG.randomizeDouble(-i) + DiverRNG.randomizeDouble(987654321L - i) - DiverRNG.randomizeDouble(123456789L - i) - DiverRNG.randomizeDouble(987654321L + i) - DiverRNG.determineDouble(1234567890L + i)) / 3.0), // -1 to 1, curved random
//                    warm = ((DiverRNG.determineDouble(-i) + DiverRNG.randomizeDouble((i^12345L)*i) + DiverRNG.randomizeDouble((i^54321L)*i) - DiverRNG.randomizeDouble((i^99999L)*i) - DiverRNG.randomizeDouble((987654321L - i)*i) - DiverRNG.determineDouble((1234567890L - i)*i)) / 3.0); // -1 to 1, curved random

//            final double v1 = fastGaussian(rng), v2 = fastGaussian(rng), v3 = fastGaussian(rng);
//            double mag = v1 * v1 + v2 * v2 + v3 * v3 + 1.0 / (1.0 - ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53) * ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53) * ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53)) - 1.0;
//            double mag = v1 * v1 + v2 * v2 + v3 * v3 + 1.0 / (1.0 - ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53) * ((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53)) - 0.5;
//            double mag = v1 * v1 + v2 * v2 + v3 * v3 - 2.0 * Math.log(((rng.nextLong() & 0x1FFFFFFFFFFFFFL) * 0x1p-53));
//            final long t = rng.nextLong(), s = rng.nextLong(), angle = t >>> 48;
//            float mag = (((t & 0xFFFFFFL)) * 0x0.7p-24f + (0x1.9p0f - ((s & 0xFFFFFFL) * 0x1.4p-24f) * ((s >>> 40) * 0x1.4p-24f))) * 0.555555f;
//            mild = MathUtils.sin(angle) * mag;
//            warm = MathUtils.cos(angle) * mag;
//            double mag = ((t & 0xFFFFFFL) + (t >>> 40) + (s & 0xFFFFFFL) + (s >>> 40)) * 0x1p-26;
//            if (mag != 0.0) {
//                mag = 1.0 / Math.sqrt(mag);
//                mild = v1 * mag;
//                warm = v2 * mag;
//            }

//            double mild = (nextDouble() + nextDouble() + nextDouble() + nextDouble() + nextDouble() + nextDouble()
//                    - nextDouble() - nextDouble() - nextDouble() - nextDouble() - nextDouble() - nextDouble()) * 0.17 % 1.0, // -1 to 1, curved random
//                    warm = (nextDouble() + nextDouble() + nextDouble() + nextDouble() + nextDouble() + nextDouble()
//                            - nextDouble() - nextDouble()- nextDouble() - nextDouble() - nextDouble() - nextDouble()) * 0.17 % 1.0; // -1 to 1, curved random
            double co = (nextDouble() + nextDouble() + nextDouble() + nextDouble() * nextDouble() + nextDouble() * nextDouble()
                    - nextDouble() - nextDouble() - nextDouble() - nextDouble() * nextDouble() - nextDouble() * nextDouble()) * 32.0 % 128.0, // -256.0 to 256.0, curved random
                    cg = (nextDouble() + nextDouble() + nextDouble() + nextDouble() * nextDouble() + nextDouble() * nextDouble()
                            - nextDouble() - nextDouble()- nextDouble() - nextDouble() * nextDouble() - nextDouble() * nextDouble()) * 32.0 % 128.0; // -256.0 to 256.0, curved random
//            mild = Math.signum(mild) * Math.pow(Math.abs(mild), 1.05);
//            warm = Math.signum(warm) * Math.pow(Math.abs(warm), 0.8);
//            if (mild > 0 && warm < 0) warm += mild * 1.666;
//            else if (mild < -0.6) warm *= 0.4 - mild;
            final double t = luma - cg;

//            int g = (int) ((luma + mild * 0.5 - warm * 0.375) * 255);
//            int b = (int) ((luma - warm * 0.375 - mild * 0.5) * 255);
//            int r = (int) ((luma + warm * 0.625 - mild * 0.5) * 255);
            base.add(
                    (int) MathUtils.clamp(t + co, 0.0, 255.0) << 24 |
                            (int) MathUtils.clamp(luma + cg, 0.0, 255.0) << 16 |
                            (int) MathUtils.clamp(t - co, 0.0, 255.0) << 8 | 0xFF);
        }

//        base.addAll(Coloring.AURORA);
//        base.addAll(Colorizer.FlesurrectBonusPalette);
//        base.addAll(Coloring.VGA256);
//        base.addAll(Coloring.RINSED);
        
//        for (int r = 0, rr = 0; r < 16; r++, rr += 0x11000000) {
//            for (int g = 0, gg = 0; g < 16; g++, gg += 0x110000) {
//                for (int b = 0, bb = 0; b < 16; b++, bb += 0x1100) {
//                    base.add(rr | gg | bb | 0xFF);
//                }
//            }
//        }
        while (base.size >= 256) {
            int t, ca = 0, cb = 1, d = 0xFFFFFF;
            for (int i = 0; i < base.size; i++) {
                int a = base.get(i);
                for (int j = i + 1; j < base.size; j++) {
                    int b = base.get(j);
                    if ((t = difference(a, b)) < d) {
                        d = t;
                        ca = i;
                        cb = j;
                    }
                }
            }
            d = ca;
            ca = base.get(ca);
            t = base.get(cb);
            int ra = (ca >>> 24), ga = (ca >>> 16 & 0xFF), ba = (ca >>> 8 & 0xFF),
                    rb = (t >>> 24), gb = (t >>> 16 & 0xFF), bb = (t >>> 8 & 0xFF);
//                    maxa = Math.max(ra, Math.max(ga, ba)), mina = Math.min(ra, Math.min(ga, ba)),
//                    maxb = Math.max(rb, Math.max(gb, bb)), minb = Math.min(rb, Math.min(gb, bb));
//            if (maxa - mina > 100)
//                base.set(cb, ca);
//            else if (maxb - minb > 100)
//                base.set(cb, t);
//            else
                base.set(cb,
                        (ra + rb + 1 << 23 & 0xFF000000)
                                | (ga + gb + 1 << 15 & 0xFF0000)
                                | (ba + bb + 1 << 7 & 0xFF00)
                                | 0xFF);
            base.removeIndex(d);
        }
        base.insert(0, 0);
        System.arraycopy(grayscale, 0, base.items, 1, grayscale.length);
        PALETTE = base.toArray();
        */
        //// used for Uniform216, SemiUniform256, and Uniform65
//        PALETTE = new int[65];
//        int idx = 1;
////        for (int r = 0; r < 6; r++) {
////            for (int g = 0; g < 6; g++) {
////                for (int b = 0; b < 6; b++) {
////                    PALETTE[idx++] = r * 51 << 24 | g * 51 << 16 | b * 51 << 8 | 0xFF;
////                }
////            }
////        }
//        for (int r = 0; r < 4; r++) {
//            for (int g = 0; g < 4; g++) {
//                for (int b = 0; b < 4; b++) {
////                    PALETTE[idx++] = r * 60 + (1 << r) - 1 << 24 | g * 60 + (1 << g) - 1 << 16 | b * 60 + (1 << b) - 1 << 8 | 0xFF;
//                    PALETTE[idx++] = r * 85 << 24 | g * 85 << 16 | b * 85 << 8 | 0xFF;
//                }
//            }
//        }
//        IntSet is = new IntSet(256);
//        RNG rng = new RNG(new MiniMover64RNG(123456789));
//        while (idx < 256)
//        {
//            int pt = rng.next(9);
//            if(is.add(pt))
//            {
//                int r = pt & 7, g = (pt >>> 3) & 7, b = pt >>> 6;
////                int r = pt % 5, g = (pt / 5) % 5, b = pt / 25;
////                PALETTE[idx++] = r * 51 + 25 << 24 | g * 51 + 25 << 16 | b * 51 + 25 << 8 | 0xFF;
//                PALETTE[idx++] = r * 32 + 15 << 24 | g * 32 + 15 << 16 | b * 32 + 15 << 8 | 0xFF;
//            }
//        }
        
//        IntIntOrderedMap iiom = new IntIntOrderedMap(initial, initial);
//        initial = iiom.keysAsArray();
        //uncomment next line to actually use unseven full, with more colors
//        System.arraycopy(initial, 0, PALETTE, 16, initial.length);

        //System.arraycopy(initial, 0, PALETTE, 128, 128);
//        System.arraycopy(Coloring.AURORA, 0, PALETTE, 0, 256);
//        IntIntOrderedMap hueToIndex = new IntIntOrderedMap(32);
//        for (int i = 18, s = 16; i < 256; i += 8, s += 8) {
//            hueToIndex.put((int)(hue(Coloring.RINSED[i]) * 1024), s);
//        }
//        TreeSet<IntIntOrderedMap.MapEntry> sorted = new TreeSet<>(new Comparator<IntIntOrderedMap.MapEntry>(){
//            /**
//             * Compares its two arguments for order.  Returns a negative integer,
//             * zero, or a positive integer as the first argument is less than, equal
//             * to, or greater than the second.<p>
//             */
//            @Override
//            public int compare(IntIntOrderedMap.MapEntry o1, IntIntOrderedMap.MapEntry o2) {
//                return o1.getKey() - o2.getKey();
//            }
//        });
//        sorted.addAll(hueToIndex.entrySet());
//        System.out.println(sorted);
//        int idx = 16;
//        for (IntIntOrderedMap.MapEntry ent : sorted) {
//            System.arraycopy(Coloring.RINSED, ent.getValue(), PALETTE, idx, 8);
//            idx += 8;
//        }

//        StringBuilder sb = new StringBuilder((1 + 12 * 8) * 32);
//        for (int i = 0; i < 32; i++) {
//            for (int j = 0; j < 8; j++) {
//                sb.append(StringKit.hex(PALETTE[i << 3 | j]), 0, 6).append('\n');
//            }
//        }
//        String sbs = sb.toString();
//        System.out.println(sbs);
//        Gdx.files.local("DawnBringer_Aurora_Official.hex").writeString(sbs, false);
//        sb.setLength(0);

//        PALETTE = Coloring.SMASH256;
//        PaletteReducer pr = new PaletteReducer(PALETTE, PaletteReducer.labRoughMetric);
//        for (int i = 1; i < Coloring.GRAY8.length; i++) {
//            int color = Coloring.GRAY8[i];
//            PALETTE[pr.reduceIndex(color) & 0xFF] = color;
//        }
//        for (int i = 1; i < Coloring.DB16.length; i++) {
//            int color = Coloring.DB16[i];
//            PALETTE[pr.reduceIndex(color) & 0xFF] = color;
//        }
        String NAME = "Betts64";
        PALETTE = Coloring.BETTS64;
//        PALETTE = Coloring.ZIGGURAT64;
//        PALETTE = Coloring.AZURESTAR33;
//        PALETTE = Coloring.SHELTZY32;
        
//        PALETTE = new int[17];
//        PALETTE[1] = 0x000000FF;
//        PALETTE[2] = Coloring.GB_GREEN[1];
//        PALETTE[6] = Coloring.GB_GREEN[2];
//        PALETTE[10] = Coloring.GB_GREEN[3];
//        PALETTE[14] = Coloring.GB_GREEN[4];
//        PALETTE[15] = Coloring.mixEvenly(Coloring.GB_GREEN[4], 0xFFFFFFFF);
//        PALETTE[16] = 0xFFFFFFFF;
//        for (int i = 3; i < 14; i+=4) {
//            PALETTE[i] = Coloring.mixLightly(PALETTE[i-1], PALETTE[i+3]);
//            PALETTE[i+1] = Coloring.mixEvenly(PALETTE[i-1], PALETTE[i+3]);
//            PALETTE[i+2] = Coloring.mixHeavily(PALETTE[i-1], PALETTE[i+3]);
//        }
        StringBuilder sb = new StringBuilder((1 + 12 * 8) * (PALETTE.length + 7 >>> 3));
        for (int i = 0; i < (PALETTE.length + 7 >>> 3); i++) {
            for (int j = 0; j < 8 && (i << 3 | j) < PALETTE.length; j++) {
                sb.append("0x").append(StringKit.hex(PALETTE[i << 3 | j]).toUpperCase()).append(", ");
            }
            sb.append('\n');
        }
        String sbs = sb.toString();
        System.out.println(sbs);
        //Gdx.files.local("GeneratedPalette.txt").writeString(sbs, false);
        sb.setLength(0);

//        for (int i = 7; i < 120; i++) {
//            final int p = PALETTE[i];
//            int diff = Integer.MAX_VALUE;
//            for (int j = 7; j < 120; j++) {
//                if(i == j) continue;
//                diff = Math.min(difference2(p, PALETTE[j]), diff);
//            }
//            sb.append("0x").append(StringKit.hex(p)).append(' ').append(diff).append('\n');
//        }
//        Gdx.files.local("PaletteDifferences.txt").writeString(sb.toString(), false);
//        Pixmap pix = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
//        for (int i = 0; i < 256; i++) {
//            pix.setColor(PALETTE[i]);
//            pix.fillRectangle((i & 15) << 3, (i & -16) >>> 1, 8, 8);
//        }

        PNG8 png8 = new PNG8();
        png8.palette = new PaletteReducer(PALETTE, HexGenerator.METRIC);
        Pixmap pix = new Pixmap(256, 1, Pixmap.Format.RGBA8888);

        //// for palettes that are fairly small (64 or less) and don't have bonus info.
        //// Meant initially for Azurestar33, which is very desaturated, so saturation may need tweaks for other palettes.
//        Color color = new Color();
//        for (int i = 1; i < 33 && i < PALETTE.length; i++) {
//            Color.rgba8888ToColor(color, PALETTE[i]);
//            float hue = NamedColor.hue(color) * 360f;
//            float sat = NamedColor.saturation(color);
//            float val = NamedColor.value(color);
//            pix.drawPixel(i-1, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.75f, val)));
//            pix.drawPixel(i+62, 0, PALETTE[i]);
//            pix.drawPixel(i+126, 0, Color.rgba8888(color.fromHsv(hue, sat * 0.875f, val * 1.25f)));
//            pix.drawPixel(i+158, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.25f, val * 1.25f)));
//            pix.drawPixel(i+190, 0, Color.rgba8888(color.fromHsv(hue, sat * 0.925f, val * 0.75f)));
//            pix.drawPixel(i+222, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.3f, val * 0.75f)));
//        }
//        pix.drawPixel(255, 0, 0);
        Color color = new Color();
        for (int i = 1; i < PALETTE.length; i++) {
            Color.rgba8888ToColor(color, PALETTE[i]);
            float hue = NamedColor.hue(color) * 360f;
            float sat = NamedColor.saturation(color);
            float val = NamedColor.value(color);
            pix.drawPixel(i-1, 0, PALETTE[i]);
            pix.drawPixel(i+63, 0, Color.rgba8888(color.fromHsv(hue, sat * 0.875f, val * 0.875f)));
            pix.drawPixel(i+127, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.25f, val * 1.25f)));
            pix.drawPixel(i+191, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.125f, val * 0.75f)));
        }
        pix.drawPixel(255, 0, 0);
//        
//        //// meant for Splay32
//        Color color = new Color();
//        for (int i = 1; i < 32 && i < PALETTE.length; i++) {
//            Color.rgba8888ToColor(color, PALETTE[i]);
//            float hue = NamedColor.hue(color) * 360f;
//            float sat = NamedColor.saturation(color);
//            float val = NamedColor.value(color);
//            pix.drawPixel(i-1, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.75f, val)));
//            pix.drawPixel(i-1+32, 0, PALETTE[i]);
//            pix.drawPixel(i-1+64, 0, Color.rgba8888(color.fromHsv(hue, sat * 0.875f, val * 1.25f)));
//            pix.drawPixel(i-1+96, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.25f, val * 1.25f)));
//            pix.drawPixel(i-1+128, 0, Color.rgba8888(color.fromHsv(hue, sat * 0.925f, val * 0.75f)));
//            pix.drawPixel(i-1+160, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.3f, val * 0.75f)));
//            pix.drawPixel(i-1+192, 0, Color.rgba8888(color.fromHsv(hue, sat * 0.75f, val * 0.6f)));
//            pix.drawPixel(i-1+224, 0, Color.rgba8888(color.fromHsv(hue, sat * 1.45f, val * 1.35f)));
//        }
//        pix.drawPixel(255, 0, 0);

//        //// for Bonus palettes that store edits to their typically 64 colors in 4 chunks.
//        for (int i = 1; i < 64 && i < PALETTE.length; i++) {
//            pix.drawPixel(i-1, 0, PALETTE[i << 2 | 2]);
//            pix.drawPixel(i+63, 0, PALETTE[i << 2]);
//            pix.drawPixel(i+127, 0, PALETTE[i << 2 | 1]);
//            pix.drawPixel(i+191, 0, PALETTE[i << 2 | 3]);
//        }
//        pix.drawPixel(255, 0, 0);
        
        //// Used for either of the above.
        try {
            png8.writePrecisely(Gdx.files.local(NAME+"_MV.png"), pix, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pix.setColor(0);
        pix.fill();
        for (int i = 1; i < PALETTE.length; i++) {
            pix.drawPixel(i-1, 0, PALETTE[i]);
        }
        pix.drawPixel(255, 0, 0);
        try {
            png8.writePrecisely(Gdx.files.local(NAME+".png"), pix, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pixmap p2 = new Pixmap(1024, 32, Pixmap.Format.RGBA8888);
        for (int r = 0; r < 32; r++) {
            for (int b = 0; b < 32; b++) {
                for (int g = 0; g < 32; g++) {
                    p2.drawPixel(r << 5 | b, g, PALETTE[png8.palette.paletteMapping[
                            ((r << 10) & 0x7C00)
                                    | ((g << 5) & 0x3E0)
                                    | b] & 0xFF]);
                }
            }
        }
        try {
            png8.writePrecisely(Gdx.files.local(NAME+"_GLSL.png"), p2, false);
//            png8.writePrecisely(Gdx.files.local("Uniform"+PALETTE.length+"_GLSL.png"), p2, false);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Pixmap pm = new Pixmap(Gdx.files.internal("BlueNoiseBW64.png"));
//        final byte[] data = new byte[64 * 64];
//        int n = 0;
//        for (int x = 0; x < 64; x++) {
//            for (int y = 0; y < 64; y++) {
//                data[n++] = (byte)(pm.getPixel(x, y));
//            }
//        }
//        PaletteReducer.print(data);
        Gdx.app.exit();
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
