package colorweaver;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ByteArray;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.NumberUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Data that can be used to limit the colors present in a Pixmap or other image, here with the goal of using 256 or less
 * colors in the image (for saving indexed-mode images).
 * <p>
 * Created by Tommy Ettinger on 6/23/2018.
 */
public class PaletteReducer {

    public interface ColorMetric{
        double difference(final int color1, int color2);
        double difference(final int color1, int r2, int g2, int b2);
        double difference(final int r1, final int g1, final int b1, final int r2, final int g2, final int b2);
    }
    public static class BasicColorMetric implements ColorMetric{
        /**
         * Color difference metric; returns large numbers even for smallish differences.
         * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
         *
         * @param color1 an RGBA8888 color as an int
         * @param color2 an RGBA8888 color as an int
         * @return the difference between the given colors, as a positive double
         */
        public double difference(final int color1, final int color2) {
            // if one color is transparent and the other isn't, then this is max-different
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            final int r1 = (color1 >>> 24), g1 = (color1 >>> 16 & 0xFF), b1 = (color1 >>> 8 & 0xFF),
                    r2 = (color2 >>> 24), g2 = (color2 >>> 16 & 0xFF), b2 = (color2 >>> 8 & 0xFF),
                    rmean = r1 + r2,
                    r = r1 - r2,
                    g = g1 - g2,
                    b = b1 - b2,
                    y = Math.max(r1, Math.max(g1, b1)) - Math.max(r2, Math.max(g2, b2));
            return (((1024 + rmean) * r * r) >> 7) + g * g * 12 + (((1534 - rmean) * b * b) >> 8) + y * y * 14;
        }
        /**
         * Color difference metric; returns large numbers even for smallish differences.
         * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
         *
         * @param color1 an RGBA8888 color as an int
         * @param r2     red value from 0 to 255, inclusive
         * @param g2     green value from 0 to 255, inclusive
         * @param b2     blue value from 0 to 255, inclusive
         * @return the difference between the given colors, as a positive double
         */
        public double difference(final int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY; // if a transparent color is being compared, it is always different
            final int
                    r1 = (color1 >>> 24),
                    g1 = (color1 >>> 16 & 0xFF),
                    b1 = (color1 >>> 8 & 0xFF),
                    rmean = (r1 + r2),
                    r = r1 - r2,
                    g = g1 - g2,
                    b = b1 - b2,
                    y = Math.max(r1, Math.max(g1, b1)) - Math.max(r2, Math.max(g2, b2));
            return (((1024 + rmean) * r * r) >> 7) + g * g * 12 + (((1534 - rmean) * b * b) >> 8) + y * y * 14;
        }
        /**
         * Color difference metric; returns large numbers even for smallish differences.
         * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
         *
         * @param r1 red value from 0 to 255, inclusive
         * @param g1 green value from 0 to 255, inclusive
         * @param b1 blue value from 0 to 255, inclusive
         * @param r2 red value from 0 to 255, inclusive
         * @param g2 green value from 0 to 255, inclusive
         * @param b2 blue value from 0 to 255, inclusive
         * @return the difference between the given colors, as a positive double
         */
        public double difference(final int r1, final int g1, final int b1, final int r2, final int g2, final int b2) {
            final int rmean = (r1 + r2),
                    r = r1 - r2,
                    g = g1 - g2 << 1,
                    b = b1 - b2,
                    y = Math.max(r1, Math.max(g1, b1)) - Math.max(r2, Math.max(g2, b2));
            return (((1024 + rmean) * r * r) >> 7) + g * g * 12 + (((1534 - rmean) * b * b) >> 8) + y * y * 14;
        }
    }

    public static class LABEuclideanColorMetric implements ColorMetric {
        
        public static final double[] LUT = new double[256];
        static {
            for (int i = 0; i < 256; i++) {
                final double r = i / 255.0;
                LUT[i] = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
            }
        }
        
        /**
         * Color difference metric (squared) using L*A*B color space; returns large numbers even for smallish differences.
         * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
         *
         * @param rgba1 an RGBA8888 color as an int
         * @param rgba2 an RGBA8888 color as an int
         * @return the difference between the given colors, as a positive double
         */
        @Override
        public double difference(final int rgba1, final int rgba2)
        {
            if(((rgba1 ^ rgba2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            double x, y, z, r, g, b;

            r = LUT[(rgba1 >>> 24)];
            g = LUT[(rgba1 >>> 16 & 0xFF)];
            b = LUT[(rgba1 >>> 8 & 0xFF)];

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;

            x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
            y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
            z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

            double L = (116.0 * y) - 16.0;
            double A = 500.0 * (x - y);
            double B = 200.0 * (y - z);

            r = LUT[(rgba2 >>> 24)];
            g = LUT[(rgba2 >>> 16 & 0xFF)];
            b = LUT[(rgba2 >>> 8 & 0xFF)];
            
            x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;

            x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
            y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
            z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

            L -= 116.0 * y - 16.0;
            A -= 500.0 * (x - y);
            B -= 200.0 * (y - z);

//            return L * L * 400.0 + A * A * 25.0 + B * B * 10.0;
            return (L * L * 11.0 + A * A * 1.6 + B * B);
        }
        @Override
        public double difference(final int rgba1, final int r2, final int g2, final int b2)
        {
            if((rgba1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            double x, y, z, r, g, b;

            r = LUT[(rgba1 >>> 24)];
            g = LUT[(rgba1 >>> 16 & 0xFF)];
            b = LUT[(rgba1 >>> 8 & 0xFF)];

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;

            x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
            y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
            z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

            double L = (116.0 * y) - 16.0;
            double A = 500.0 * (x - y);
            double B = 200.0 * (y - z);

            r = LUT[r2];
            g = LUT[g2];
            b = LUT[b2];
            
            x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;

            x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
            y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
            z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

            L -= 116.0 * y - 16.0;
            A -= 500.0 * (x - y);
            B -= 200.0 * (y - z);

//            return L * L * 190.0 + A * A * 25.0 + B * B * 10.0;
            return (L * L * 11.0 + A * A * 1.6 + B * B);
        }
        @Override
        public double difference(final int r1, final int g1, final int b1, final int r2, final int g2, final int b2) {
            double x, y, z, r, g, b;

            r = LUT[r1];
            g = LUT[g1];
            b = LUT[b1];

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;

            x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
            y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
            z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

            double L = (116.0 * y) - 16.0;
            double A = 500.0 * (x - y);
            double B = 200.0 * (y - z);

            r = LUT[r2];
            g = LUT[g2];
            b = LUT[b2];

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;

            x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
            y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
            z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

            L -= 116.0 * y - 16.0;
            A -= 500.0 * (x - y);
            B -= 200.0 * (y - z);

            //return L * L * 190 + A * A * 25 + B * B * 10;
//            return L * L * 190.0 + A * A * 25.0 + B * B * 10.0;
            return (L * L * 11.0 + A * A * 1.6 + B * B);
        }

    }

    public static class LABRoughColorMetric implements ColorMetric {
        /**
         * Color difference metric (squared) using L*A*B color space; returns large numbers even for smallish differences.
         * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
         *
         * @param rgba1 an RGBA8888 color as an int
         * @param rgba2 an RGBA8888 color as an int
         * @return the difference between the given colors, as a positive double
         */
        @Override
        public double difference(final int rgba1, final int rgba2)
        {
            if(((rgba1 ^ rgba2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            double x, y, z, r, g, b;

            r = (rgba1 >>> 24) / 255.0;
            g = (rgba1 >>> 16 & 0xFF) / 255.0;
            b = (rgba1 >>> 8 & 0xFF) / 255.0;

            r = Math.pow((r + 0.055) / 1.055, 2.4);
            g = Math.pow((g + 0.055) / 1.055, 2.4);
            b = Math.pow((b + 0.055) / 1.055, 2.4);

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805);
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722);
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505);

            x = Math.pow(x, 0.3125);
            y = Math.pow(y, 0.3125);
            z = Math.pow(z, 0.3125);

            double L = 100.0 * y;
            double A = 500.0 * (x - y);
            double B = 200.0 * (y - z);

            r = (rgba2 >>> 24) / 255.0;
            g = (rgba2 >>> 16 & 0xFF) / 255.0;
            b = (rgba2 >>> 8 & 0xFF) / 255.0;

            r = Math.pow((r + 0.055) / 1.055, 2.4);
            g = Math.pow((g + 0.055) / 1.055, 2.4);
            b = Math.pow((b + 0.055) / 1.055, 2.4);

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805);
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722);
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505);

            x = Math.pow(x, 0.3125);
            y = Math.pow(y, 0.3125);
            z = Math.pow(z, 0.3125);

            L -= 100.0 * y;
            A -= 500.0 * (x - y);
            B -= 200.0 * (y - z);

            return L * L * Math.abs(L) * 350 + A * A * 25.0 + B * B * 15.0;
        }
        @Override
        public double difference(final int rgba1, final int r2, final int g2, final int b2)
        {
            if((rgba1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            double x, y, z, r, g, b;

            r = (rgba1 >>> 24) / 255.0;
            g = (rgba1 >>> 16 & 0xFF) / 255.0;
            b = (rgba1 >>> 8 & 0xFF) / 255.0;

            r = Math.pow((r + 0.055) / 1.055, 2.4);
            g = Math.pow((g + 0.055) / 1.055, 2.4);
            b = Math.pow((b + 0.055) / 1.055, 2.4);

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805);
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722);
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505);

            x = Math.pow(x, 0.3125);
            y = Math.pow(y, 0.3125);
            z = Math.pow(z, 0.3125);

            double L = 100 * y;
            double A = 500.0 * (x - y);
            double B = 200.0 * (y - z);

            r = r2 / 255.0;
            g = g2 / 255.0;
            b = b2 / 255.0;

            r = Math.pow((r + 0.055) / 1.055, 2.4);
            g = Math.pow((g + 0.055) / 1.055, 2.4);
            b = Math.pow((b + 0.055) / 1.055, 2.4);

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805);
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722);
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505);

            x = Math.pow(x, 0.3125);
            y = Math.pow(y, 0.3125);
            z = Math.pow(z, 0.3125);

            L -= 100.0 * y;
            A -= 500.0 * (x - y);
            B -= 200.0 * (y - z);

            return L * L * Math.abs(L) * 350 + A * A * 25.0 + B * B * 15.0;
        }
        @Override
        public double difference(final int r1, final int g1, final int b1, final int r2, final int g2, final int b2) {
            double x, y, z, r, g, b;

            r = r1 / 255.0;
            g = g1 / 255.0;
            b = b1 / 255.0;

            r = Math.pow((r + 0.055) / 1.055, 2.4);
            g = Math.pow((g + 0.055) / 1.055, 2.4);
            b = Math.pow((b + 0.055) / 1.055, 2.4);

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805);
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722);
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505);

            x = Math.pow(x, 0.3125);
            y = Math.pow(y, 0.3125);
            z = Math.pow(z, 0.3125);

            double L = 100 * y;
            double A = 500.0 * (x - y);
            double B = 200.0 * (y - z);

            r = r2 / 255.0;
            g = g2 / 255.0;
            b = b2 / 255.0;

            r = Math.pow((r + 0.055) / 1.055, 2.4);
            g = Math.pow((g + 0.055) / 1.055, 2.4);
            b = Math.pow((b + 0.055) / 1.055, 2.4);

            x = (r * 0.4124 + g * 0.3576 + b * 0.1805);
            y = (r * 0.2126 + g * 0.7152 + b * 0.0722);
            z = (r * 0.0193 + g * 0.1192 + b * 0.9505);

            x = Math.pow(x, 0.3125);
            y = Math.pow(y, 0.3125);
            z = Math.pow(z, 0.3125);

            L -= 100.0 * y;
            A -= 500.0 * (x - y);
            B -= 200.0 * (y - z);

            return L * L * Math.abs(L) * 350 + A * A * 25.0 + B * B * 15.0;
        }

    }

    /**
     * Converts an RGBA8888 int color to the RGB555 format used by {@link #labs} to look up colors.
     * @param color an RGBA8888 int color
     * @return an RGB555 int color
     */
    public static int shrink(final int color)
    {
        return (color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F);
    }

    /**
     * Stores CIE L*A*B* components for 32768 colors (the full RGB555 range). The first sub-array stores luma (ranging
     * from 0 to 100), the second sub-array stores A chroma (representing red for positive values and green for
     * negative, very roughly, with a strange range of about -120 to 120), and the third sub-array stores B chroma
     * (representing yellow for positive values and blue for negative values, very roughly, with a strange range of
     * about -120 to 120). Inside each array are 32768 double values, storing the L, A, or B for the RGB555 color at
     * that index. You can convert a (normal) RGBA8888 color to RGB555 with {@link #shrink(int)}.
     */
    public static final double[][] labs = new double[3][0x8000];
    static {
        double r, g, b, x, y, z;
        int idx = 0;
        for (int ri = 0; ri < 32; ri++) {
            r = ri / 31.0;
            r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
            for (int gi = 0; gi < 32; gi++) {
                g = gi / 31.0;
                g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
                for (int bi = 0; bi < 32; bi++) {
                    b = bi / 31.0;
                    b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);

                    x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
                    y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
                    z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;

                    x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
                    y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
                    z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

                    labs[0][idx] = (116.0 * y) - 16.0;
                    labs[1][idx] = 500.0 * (x - y);
                    labs[2][idx] = 200.0 * (y - z);
                    idx++;
                }
            }
        }
    }


    public static final ColorMetric labQuickMetric = new ColorMetric(){
        @Override
        public double difference(int color1, int color2) {
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        @Override
        public double difference(int color1, int r2, int g2, int b2) {
            int indexA = (color1 >>> 17 & 0x7C00) | (color1 >>> 14 & 0x3E0) | (color1 >>> 11 & 0x1F),
                    indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
            final double
                    L = labs[0][indexA] - labs[0][indexB],
                    A = labs[1][indexA] - labs[1][indexB],
                    B = labs[2][indexA] - labs[2][indexB];
            return L * L * 7 + A * A + B * B;
        }

        @Override
        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            int indexA = (r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3),
                    indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
            final double
                    L = labs[0][indexA] - labs[0][indexB],
                    A = labs[1][indexA] - labs[1][indexB],
                    B = labs[2][indexA] - labs[2][indexB];
            return L * L * 7 + A * A + B * B;
//            return L * L * 11.0 + A * A * 0.625 + B * B;
        }
    };

    public static final ColorMetric rgbEasyMetric = new ColorMetric(){
        @Override
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        @Override
        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        @Override
        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            //return Math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2));
            return Math.sqrt(Math.pow(Math.abs(r1 - r2), 3.6) + Math.pow(Math.abs(g1 - g2), 4.0) + Math.pow(Math.abs(b1 - b2), 3.2));

        }
    };

    public static final BasicColorMetric basicMetric = new BasicColorMetric(); // has no state, should be fine static
    public static final LABEuclideanColorMetric labMetric = new LABEuclideanColorMetric();
    public static final LABRoughColorMetric labRoughMetric = new LABRoughColorMetric();
    public byte[] paletteMapping;
    public final int[] paletteArray = new int[256];
    final int[] gammaArray = new int[256];
    ByteArray curErrorRedBytes, nextErrorRedBytes, curErrorGreenBytes, nextErrorGreenBytes, curErrorBlueBytes, nextErrorBlueBytes;
    float ditherStrength = 0.5f, halfDitherStrength = 0.25f;

    /**
     * This stores a preload code for a PaletteReducer using {@link Coloring#AURORA} with {@link #labQuickMetric}. Using
     * a preload code in the constructor {@link #PaletteReducer(int[], byte[])} eliminates the time needed to fill 32 KB
     * of palette mapping in a somewhat-intricate way that only gets more intricate with better metrics, and replaces it
     * with a straightforward load from a String into a 32KB byte array.
     * <br>
     * Earlier versions of this constant used {@link #basicMetric}, but dithering gets much smoother using
     * {@link #labQuickMetric} instead.
     */
    public static final byte[] ENCODED_AURORA = "\001\001\001\001\001uv\030\030\030\030\030\030\027\027\027\027\027ßßßÞÞÞÞÞÝÝÝ\025\025\025\001\001\001\001uuvv\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÞÝÝÝ\025\025\025\002\002\002\002uuvvv\030\030\030\030\030\027\027\027ßßßßÞÞÞÞÞÝÝÝÝ\025\025WWW\002uuuvvàà\030\030\030ó\027ßßßßßÞÞÞÞÞÝÝÝÝ\025\025WWWg\003uuuàààààËËËßßßßßßÞÞÞÝÝÝÝÝ\025\025²gggggg\003ÊÊÊÊÊËËËËËßßßßááááÝÝÝÝÝ\025²²²½½½½½\004ÊÊÊÊËËËËËËË\026\026\026\026\026\026ÝÝÜÜÜÜ³³³³½½½½½hwwwËËËËËËÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³³½½½hhhwwwwËÌÌÌÌÌÌ\026\026\026\026\026\026ÜÜÜÜ³³³³XXXXhhhhttttÌÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÎ´´´´XXXXXÉÉÉÉtttttÌÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´´XXXXÉÉÉÉÉtttÍÍÍyÌÏÏÏÏÎÎÎÎÎÎ´´´´´fffffÉÉÉÉÉtÍÍÍÍÍÏÏÏÏÏÏ×ÚÚÚÚ±±±±±fffffffiiiiiÍÍÍÍÏÏÏÏÏÏÐ×××Ú++++±±fffff[\020\020\020iisssÍÍÏÏÏÏÐÐÐ×××+++++++¼[[[[[\020\020\020\020\020ssssØØØØÐÐÐÐ××++++++¼¼¼¼¼¼e\020\020\020\020\020\020\020ssrrrØØÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¾¾¾\020\020\020\020\020rrrrrrrØÐÐ×µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾3ÑrrrÈÈÈÈÈÒÒµµµµµµµµ¼¼¼¼¼¾¾¾¾¾¾¾¾jÑÑÈÈÈÈÈÈÒÒµµµµµµµ»»»»»»¾¾¾¾¾¾¾¾jjjÑÑÈÈÈÈÒÒ,,,,,,»»»»»»»»»¾¾¾¾¾jjjjjjÑÈÈÖÒÒº,,,,,,,»»»»»»»¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»»¿¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖººººººº,,,,,»»»¿¿¿¿¿¿¿\021\021\021\021\021\021ÔÔÔÖ¶¶ººººººº,ÀÀÀÀÀ^¿¿¿¿¿¿¿ÇÇ\021\021\021\021ÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÅÆÆ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÅÅÅ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÅÅ········¹¹¹¹¹¹ÀÀÀÂÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ--------¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022\001\001\001\001\001vv\030\030\030\030\030\030\027\027\027\027\027ßßßÞÞÞÞÞÝÝÝ\025\025\025\001\001\002\002uuvv\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÞÝÝÝ\025\025\025\002\002\002\002uuvvv\030\030\030\030\030\027\027\027ßßßßÞÞÞÞÝÝÝÝÝ\025\025WWW\002uuuvvàà\030\030óó\027ßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWWg\003\003uvàààààËËËßßßßßßÞÞÞÝÝÝÝÝ\025\025²gggggg\003ÊÊÊÊÊËËËËËßßßßááááÝÝÝÝÜ\025²²²g½½½½\004ÊÊÊÊËËËËËËË\026\026\026\026\026\026ÝÝÜÜÜÜ³³³³½½½½½hwwwËËËËËËÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³³½½½hhhwwwwÌÌÌÌÌÌÌ\026\026\026\026\026ÎÜÜÜÜ³³³³XXXXhhhhtttxxÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÎ´´´´XXXXXÉÉÉÉtttttÌÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´´XXXXÉÉÉÉÉtttÍÍÍyÏÏÏÏÏÎÎÎÎÎÚ´´´´´fffffÉÉÉÉÉtÍÍÍÍÍÏÏÏÏÏÏ×ÚÚÚÚ±±±±±fffffffiiiiiÍÍÍÍÏÏÏÏÏÏÐ×××Ú++++±±ffff[[\020\020\020iisssÍÍÏÏÏÏÐÐÐ×××++++++¼¼[[[[[\020\020\020\020\020ssssØØØØÐÐÐÐ××++++++¼¼¼¼¼¼e\020\020\020\020\020\020\020ssrrrØØÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¾¾¾\020\020\020\0203rrrrrrrØÐÐ×µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾3ÑrrrÈÈÈÈÈÒÒµµµµµµµµ¼¼¼¼¼¾¾¾¾¾¾¾¾jÑÑÈÈÈÈÈÈÒÒµµµµµµµ»»»»»»¾¾¾¾¾¾¾¾jjjÑÑÈÈÈÈÒÒ,,,,,,»»»»»»»»]¾¾¾¾¾jjjjjjÑÈÈÖÒÒº,,,,,,,»»»»»»»¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»»¿¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖººººººº,,,,,»»»¿¿¿¿¿¿¿\021\021\021\021\021\021ÔÔÔÔ¶¶ººººººº,ÀÀÀÀÀ^¿¿¿¿¿¿¿ÇÇ\021\021\021ÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÅÆÆ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÅÅ········¹¹¹¹¹¹ÀÀÀÂÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ--------¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022\001\001\001\001vv\030\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÝÝÝÝ\025\025\025\002\002\002\002uvv\030\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÝÝÝÝ\025\025\025\002\002\002\002uuvvv\030\030\030\030ó\027\027ßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWW\002uuvvvàà\030\030óóóßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWWg\003\003uvàààààËËËßßßßßßÞÞÞÝÝÝÝÝ\025\025²gggggg\003ÊÊÊÊÊËËËËËßßßáááááÝÝÝÝÜ\025²²²gg½½\004\004ÊÊÊÊËËËËËËË\026\026\026\026\026\026ÝÝÜÜÜÜ³³³³½½½½hhwwwwËËËËËÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³³½½hhhhwwwwÌÌÌÌÌ\026\026\026\026\026\026ÎÎÜÜÜ³³³³XXXXhhhhtttxxÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÎ´´´´XXXXXÉÉÉÉtttttÌÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´´XXXXÉÉÉÉÉtttÍÍÍyÏÏÏÏÏÎÎÎÎÎÚ´´´´´fffffÉÉÉÉÉtÍÍÍÍÍÏÏÏÏÏÏ×ÚÚÚÚ±±±±±fffffffiiiiiÍÍÍÍÏÏÏÏÏÏÐ×××Ú++++±±ffff[[\020\020iiisssÍÍÏÏÏÏÐÐÐ×××++++++¼¼[[[[[\020\020\020\020\020ssssØØØØÐÐÐÐ××++++++¼¼¼¼¼¼e\020\020\020\020\020\020sssrrrØØÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¾¾¾\020\020\020\0203rrrrrrrØÐÐ×µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾3ÑrrrÈÈÈÈÈÒÒµµµµµµµµ¼¼¼¼¼¾¾¾¾¾¾¾¾jÑÑÈÈÈÈÈÈÒÒµµµµµµ»»»»»»»¾¾¾¾¾¾¾jjjjÑÑÈÈÈÈÒÒ,,,,,,»»»»»»»»]¾¾¾¾¾jjjjjjÑÈÈÖÒÒº,,,,,,,»»»»»»»¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»»¿¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖººººººº,,,,,»»^¿¿¿¿¿¿¿\021\021\021\021\021\021ÔÔÔÔ¶¶ººººººº,ÀÀÀÀÀ^¿¿¿¿¿¿¿ÇÇ\021\021\021ÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÅÆÆ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹¹ÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÅÅ········¹¹¹¹¹¹ÀÀÀÂÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022\001\001\001ô\030\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÝÝÝÝ\025\025\025\002\002\002\002vvô\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÝÝÝÝ\025\025\025\002\002\002\002uvvvôô\030\030\030ó\027\027ßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWWWuuvvvàôôóóóóßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWW\003\003\003\003vààààËËËËßßßßßßÞÞÞÝÝÝÝÝ\025\025²²gggg\003\003ÊÊÊÊÊËËËËËßßßáááááÝÝÝÝÜÜ²²²gg½½\004\004ÊÊÊÊËËËËËË\026\026\026\026\026\026\026ââÜÜÜÜ³³³²½½½½hhwwwwËËËËËÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³³½½hhhhwwwxÌÌÌÌÌ\026\026\026\026\026\026ÎÎÜÜÜ³³³³XXXXhhhhtttxxÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÎ´´´´XXXXXÉÉÉÉttttyÌÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´´XXXXÉÉÉÉÉtttÍÍÍyÏÏÏÏÏÎÎÎÎÎÚ´´´´´fffffÉÉÉÉÉtÍÍÍÍÍÏÏÏÏÏÏ×ÚÚÚÚ±±±±±±ffffffiiiisÍÍÍÍÏÏÏÏÏÏÐ×××Ú+++±±±fff[[[\020\020iiisssÍÍÏÏÏÏÐÐÐ×××++++++¼¼[[[[[\020\020\020\020\020ssssØØØØÐÐÐ×××++++++¼¼¼¼¼¼ee\020\020\020\020\020sssrrrØØÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¾¾¾\020\020\020\0203rrrrrrrØÐÐ×µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾3ÑrrrÈÈÈÈÈÒÒµµµµµµµµ¼¼¼¼¼¾¾¾¾¾¾¾¾jÑÑÈÈÈÈÈÈÒÒµµµµµµ»»»»»»»¾¾¾¾¾¾¾jjjjÑÑÈÈÈÈÒÒ,,,,,,»»»»»»»»]¾¾¾¾¾jjjjjjÑÈÈÖÒÒ,,,,,,,,»»»»»»»¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»»¿¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖººººººº,,,,,»»^¿¿¿¿¿¿¿\021\021\021\021\021\021ÔÔÔÔ¶¶ººººººº,ÀÀÀÀÀ^¿¿¿¿¿¿¿ÇÇ\021\021\021ÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÅÆÆ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹¹ÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÅÅÅ········¹¹¹¹¹¹ÀÀÀÂÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¸ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôô\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÝÝÝÝ\025\025\025\002vôô\030\030\030óó\027\027ßßßßßÞÞÞÞÝÝÝÝ\025\025\025W\002\002vvvôôô\030óóó\027ßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWW\003\003uvvvàôôóóóóßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWW\003\003\003\003vàààÊËËËËßßßßßááááÝÝÝÝÝ\025\025²²gggg\003ÊÊÊÊÊËËËËËßßßáááááÝÝÝÝÜÜ²²²²gg\004\004\004wÊÊÊËËËËËË\026\026\026\026\026\026\026ââÜÜÜÜ³³²²½½½½hwwwwwËËËËÌÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³³½hhhhhwwwxÌÌÌÌÌ\026\026\026\026\026\026ÎÎÜÜÜ³³³³XXXXhhhhtttxxÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÛ´´´XXXXXYÉÉÉtttttyÌÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´´XXXXÉÉÉÉÉtttÍÍÍyÏÏÏÏÏÎÎÎÎÎÚ´´´´´fffffÉÉÉÉÉtÍÍÍÍÍÏÏÏÏÏÏ×ÚÚÚÚ±±±±±±fffffiiiiisÍÍÍÍÏÏÏÏÏÐÐ×××Ú+++±±±±f[[[[iiiiisssÍÍÏÏÏÐÐÐÐ×××++++++¼¼[[[[[\020\020\020\020\020ssssØØØØÐÐÐ×××++++++¼¼¼¼¼¼ee\020\020\020\020\020ssrrrrØØÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¾¾¾\020\020\02033rrrrrrrØÐÐ×µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾3ÑrrrÈÈÈÈÈÒÒµµµµµµµµ¼¼¼¼¼¾¾¾¾¾¾¾¾jÑÑÑÈÈÈÈÈÒÒµµµµµµ»»»»»»»¾¾¾¾¾¾¾jjjjÑÑÈÈÈÈÒÒ,,,,,,»»»»»»»»]¾¾¾¾¾jjjjjjÑÈÖÖÒÒ,,,,,,,,»»»»»»»¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖºººº,,,,,,»»»»¿¿¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖººººººº,,,,,»»^¿¿¿¿¿¿¿\021\021\021\021\021\021ÔÔÔÔ¶¶ººººººº,ÀÀÀÀÀ^¿¿¿¿¿¿¿ÇÇ\021\021\021ÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ^¿¿¿¿ÇÇÇÇÇÇÇÅÆÆ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹¹ÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÅÅÅ········¹¹¹¹¹¹ÀÀÀÂÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¸ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôôô\030\030\030ó\027\027\027ßßßßÞÞÞÞÞÝÝÝÝ\025\025\025ôôôô\030\030óóó\027ßßßßßÞÞÞÞÝÝÝÝ\025\025\025WvôôôôóóóóóßßßßßÞÞÞÞÝÝÝÝ\025\025\025WWW\003vvvôôôóóóóßßßßßÞÞÞÞÝÝÝÝÝ\025\025HH\003\003\003\003ÊÊÊËËËËßßßßßááááÝÝÝÝÝ\025\025²²Hgg\004\004ÊÊÊËËËËËññßß\026ááááÝÝÝÜÜÜ²²²²\004\004\004\004\004wwÊÊËËËËËË\026\026\026\026\026\026\026ââÜÜÜÜ²²²²½½½hhwwwwwËËËËÌÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³IIhhhhwwwwxÌÌÌÌÌ\026\026\026\026\026\026ÎÎÜÜÜ³³³³XXXYhhhhtttxxÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÛ´´´XXXXXYÉÉÉtttttyyÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´´XXXYÉÉÉÉÉtttÍÍyyÏÏÏÏÏÎÎÎÎÚÚ±±±±´fffffÉÉÉÉÉÍÍÍÍÍÍÏÏÏÏÏÏ×ÚÚÚÚ±±±±±±fffffiiiiisÍÍÍÍÏÏÏÏÏÐÐ×××Ú+±±±±±±[[[[[iiiiisssÍÏÏÏÏÐÐÐÐ×××++++++¼¼[[[[e\020\020\020\020\020ssssØØØØÐÐÐ×××++++++¼¼¼¼¼eee\020\020\020\020\020ssrrrrØØÐÐÐ××µµµµµ¼¼¼¼¼¼¼\\¾¾¾\020\020\02033rrrrrrrØÐÐ×µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾2ÑÑrrÈÈÈÈÒÒÒµµµµµµµµ¼¼¼¼¼¾¾¾¾¾¾¾jjÑÑÑÈÈÈÈÈÒÒµµµµµµ»»»»»»»¾¾¾¾¾¾¾jjjjÑÑÈÈÈÈÒÒ,,,,,,»»»»»»»»]]¾¾¾¾jjjjjÑÑÈÖÖÒÒ,,,,,,,,»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖºººº,,,,,,»»»»¿¿¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖººººººº,,,,,»»^^¿¿¿¿¿¿\021\021\021\021\021\021ÔÔÔÔ¶¶ººººººº,ÀÀÀÀÀ^^¿¿¿¿¿¿ÇÇ\021\021\021ÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ^¿¿¿¿ÇÇÇÇÇÇÇÆÆÆ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹¹ÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ········¹¹¹¹¹¹ÀÀÀÂÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¸ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôôôôôôóóó\027\027ßßßßÞÞÞÞÞÝÝÝÝ\025\025\025ôôôôôóóóóßßßßßßÞÞÞÞÝÝÝÝ\025\025\025ôôôôóóóóóßßßßßÞÞÞÞÝÝÝÝ\025\025\025ôôôôóóóóßßßßßááááÝÝÝÝÝ\025\025HHHH\003\003ÊÊËËËññßßßßááááÝÝÝÝÝ\025\025²HHHH\004\004ÊÊËËËËËññß\026\026ááááâââÜÜÜ²²²HH\004\004\004wwËËËËËËñ\026\026\026\026\026\026\026ââÜÜÜÜ²²²II\004\004\004\004wwwwËÌÌÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³³IIIhhhhwwwxxÌÌÌÌÌ\026\026\026\026\026\026ÎÎÜÜÜ³³³³XXYYhh\005\005ttxxxÌÌÌÌÌ\026\026\026\026\026ÎÎÎÎÛ´´´XXXXYYYÉ\005tttttyyÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´´XXYYYÉÉÉttttÍÍyyÏÏÏÏÏÎÎÎÚÚÚ±±±±±ffffZZZÉÉ\006ÍÍÍÍÍÍÏÏÏÏÏÏ×ÚÚÚÚ±±±±±±fff[[ZiiiisÍÍÍÍÏÏÏÏÏÐÐ×××Ú±±±±±±±[[[[[iiiiissssÏÏÏÏÐÐÐ××××++++++UU[[[[e\020\020\020\020sssssØØØØÐÐÐ×××++++++¼¼¼¼¼eee\020\020\020\020\020ssrrrrØØØÐÐ××µµµµµ¼¼¼¼¼¼\\\\\\¾¾\020\020333rrrrrrrØÐÐ×µµµµµµµ¼¼¼¼¼\\¾¾¾¾¾¾32ÑÑrrrÈÈÈÒÒÒµµµµµµµµ¼¼¼¼\\¾¾¾¾¾¾¾jjÑÑÑÈÈÈÈÒÒÒµµµµµµ»»»»»»]¾¾¾¾¾¾¾jjjjÑÑÈÈÈÈÒÒ°,,,,,»»»»»»»»]]¾¾¾jjjjjjÑÑÑÖÖÒÒ,,,,,,,,»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖºººº,,,,,,»»»»^¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖººººººº,,,,,»»^^¿¿¿¿¿¿\021\021\021\021\021\021ÔÔÔÔ¶¶¶ºººººº,,ÀÀÀ^^^¿¿¿¿¿ÇÇÇ\021\021\021ÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ^¿¿¿¿ÇÇÇÇÇÇÇÆÆÆ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹¹ÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ········¹¹¹¹¹¹ÀÀÀÂÁÁÁÁÁÁÁÃÇÇ\022\022ÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÃÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¸ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôôôôóóóóóßßßßßÞÞÞÞÞÝÝÝÝ\025\025\025ôôôôôóóóóßßßßßßÞÞÞÞÝÝÝÝ\025\025\025ôôôôóóóóóßßßßßáááÞÝÝÝÝ\025\025\025ôôóóóóóßßßßáááááÝÝÝÝÝ\025\025HHHHHÊÊËËËñññßßááááááÝÝÝÝ\025\025HHHHH\004ÊËËËËññññ\026\026ááááâââÜÜÜ²²²HH\004\004wwËËËËËñ\026\026\026\026\026\026\026ââÜÜÜÜ²²IIII\004\004wwwwÌÌÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³IIIIIhhhwwwxxÌÌÌÌ\026\026\026\026\026\026ÎÎÜÜÜ¤³³IIVYY\005\005\005\005ttxxxÌÌÌÌÌ\026\026\026\026ÎÎÎÎÎÛ¤¤¤VVVVYYY\005\005ttttxyyyÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´VVVVYYÉÉ\006tttÍÍÍyyÏÏÏÏÎÎÚÚÚ±±±±±±fffZZZZ\006\006ÍÍÍÍÍÍÏÏÏÏÏÏ×ÚÚÚÚ±±±±±±[[[[[ZiiiissÍÍÍÏÏÏÏÏÐÐ×××Ù±±±±±±±[[[[[iiiisssssÏÏÏØÐÐÐ××××+++++UUU[[[ee\020\020iisssssØØØØÐÐÐ×××+++++¼¼¼¼¼\\eee\020\020\020\0203ssrrrrØØØÐÐ××µµµµµ¼¼¼¼¼\\\\\\\\¾¾33333rrrrrrrØÐÐ\024µµµµµµµ¼¼¼¼\\\\¾¾¾¾¾¾22ÑÑrrrÈÈÈÒÒÒµµµµµµµµ¼¼¼¼\\¾¾¾¾¾¾¾jjÑÑÑÈÈÈÈÒÒÒ°µµµµµ»»»»»»]]¾¾¾¾¾jjjjjÑÑÑÈÈÒÒÒ°°°,,,»»»»»»»]]]]¾¾jjjjjjÑÑÑÖÖÒÒ,,,,,,,,»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021qÖÖÖÖÖººº,,,,,,,»»»»^¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖºººººº,,,,,,»^^^^¿¿¿¿\021\021\021\021\021\021\021ÔÔÔÔ¶¶¶ººººº,,,ÀÀÀ^^^^¿¿¿¿ÇÇÇ\021\021\021ÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀ^^^¿¿¿ÇÇÇÇÇÇÇÆÆÆ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹¹ÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ········¹¹¹¹¹¹ÀÀÀÂÁÁÁÁÁÁÁÃÇÇ\022\022ÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÃÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¸¸ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôôôôóóóóóßßßßßáÞÞÞÞÝÝÝÝ\025\025\025ôôôóóóóóóßßßßáááááÝÝÝÝ\025\025\025ôôôóóóóóßßßßáááááÝÝÝÝ\025\025\025õôóóóñññßßßáááááÝÝÝÝÝ\025\025òòËñññññßáááááâââââÜåHHHHHÊËËËñññññ\026\026\026ááââââÜÜÜ²²HHH\004\004wwËññ\026\026\026\026\026\026\026ââÜÜÜÜIIIIII==wwwwÌÌ\026\026\026\026\026\026\026ÜÜÜÜÜIIIIIII\005\005\005wwxxxÌÌÌÌ\026\026\026\026\026\026ÎÎÜÜÜ¤¤¤IIVYY\005\005\005\005txxxxyÌÌÌÌ\026\026\026\026ÎÎÎÎÛÛ¤¤¤¤VVVYY\005\005\005ttttyyyyyÌÌÌ\026\026ÎÎÎÎÎÚ¤¤¤¤¤VVVYYZ\006\006tttÍÍyyyÏÏÏÏÎÚÚÚÚ±±±±±±ffZZZZZ\006\006ÍÍÍÍÍÍÏÏÏÏÏ×ÚÚÚÚ±±±±±±[[[[ZZiiisssÍÍÍÏÏÏÏÏÐ×××ÙÙ±±±±±±U[[[[eiiiisssssÏÏØØÐÐÐ××××++++UUUUU[eeeeiiisssssØØØØØÐÐ×××+++++¼¼¼\\\\\\eeee\020\02033ssrrrrØØØÐÐ××µµµµµ¼¼¼¼\\\\\\\\\\¾¾33333rrrrrrrØÐÐ\024µµµµµµµ¼¼¼\\\\\\\\¾¾¾¾222ÑÑrrrÈÈÈÒÒÒµµµµµµµµ¼¼¼\\\\¾¾¾¾¾¾jjjÑÑÑÈÈÈÈÒÒÒ°°°°°µ»»»»»]]]¾¾¾¾¾jjjjjÑÑÑÈÈÒÒÒ°°°°°,,»»»»»»]]]]¾¾jjjjjjÑÑÑÖÖÒÒ,,,,,,,,,»»»»»]]¿¿¿\021\021\021\021\021\021\021qÖÖÖÖÖººº,,,,,,,,»»»^^¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖºººººº,,,,,,»^^^^¿¿¿¿\021\021\021\021\021\021\021ÔÔÔÔ¶¶¶ººººº,,,ÀÀÀ^^^^¿¿¿¿ÇÇÇ\021\021kÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀ^^^^¿¿ÇÇÇÇÇÇÅÆÆÆ···¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹¹ÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ·······¹¹¹¹¹¹¹¹ÀÀÂÁÁÁÁÁÁÁÃÇÇ\022\022ÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÃÃÃÃ\022\022\022\022---------¹¹¹¹¹¹¸¸ÂÂÂÂÂÂÃÃÃÃÃ\022\022\022\022\"\"\"\"õõõôôôóóóóóóßßßßáááááÝÝÝÝ\025\025\025\"\"\"\"õõõõôôóóóóóñßßßßáááááÝÝÝÝ\025\025\025\"\"\"\"õõõõõôóóóóñññßßááááááââââ\025\025\025\"õõõõòòòññññññááááááâââââ\025åõõòòòññññññáááááâââââÜå££HHññññññ\026\026\026ááââââÜÜÜ££££H=wwñññ\026\026\026\026\026\026\026âÜÜÜÜÜ£IIII====wwwÌ\026\026\026\026\026\026\026\026ÜÜÜÜÜIIIIIII=\005\005wwxxÌÌÌ\026\026\026\026\026\026ÎÎÛÛÛ¤¤¤IIIV\005\005\005\005\005xxxxyyyÌÌÌ\026\026\026\026ÎÎÎÎÛÛ¤¤¤¤VVVVY\005\005\005tttxyyyyyÌÌÎÎÎÎÚÚ¤¤¤¤¤VVVZZ\006\006\006\006ttÍyyyyÏÏÏÏÚÚÚÚÚ±±±±±±[ZZZZZZ\006\006ÍÍÍÍÍÏÏÏÏÏÏÙÚÚÚ±±±±±±[[[[ZZZiisssÍÍÍÏÏÏÏÐÐ×××ÙÙ±±±±±UU[[[[eiiiissssszØØØØÐÐ××××+++UUUUUUeeeeeiiisssssØØØØØÐÐ×××++++UUUU\\\\\\eeee3333ssrrrrØØØÐÐ××µµµµµ¼¼\\\\\\\\\\\\\\e333333rrrrrrrØÐ\024\024µµµµµµµ¼\\\\\\\\\\\\¾¾¾¾222ÑÑÑrrrÈÈÒÒÒµµµµµµµµ¼\\\\\\\\¾¾¾¾¾¾222ÑÑÑÑÈÈÈÒÒÒ°°°°°°»»»»»]]]]¾¾¾¾jjjjÑÑÑÑÈÈÒÒÒ°°°°°°,»»»»»]]]]]]jjjjjjjÑÑÑÖÖÒÒ°,,,,,,,,»»»»»]]]]d\021\021\021\021\021jjqÖÖÖÖÖºº,,,,,,,,,»»^^^^¿¿¿\021\021\021\021\021\021\021ÔÖÖÖÖºººººº,,,,,,,^^^^^¿¿¿\021\021\021\021\021\021\021ÔÔÔÔ¶¶¶ººººº,,,ÀÀÀ^^^^^¿¿¿ÇÇÇ\021\021kÆÆÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀ^^^^¿¿ÇÇÇÇÇÇÆÆÆÆ···¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÇÅÅÅ········¹¹¹ÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ·······¹¹¹¹¹¹¹¹ÀÂÂÁÁÁÁÁÁÁÃÇÇ\022\022ÅÅ-------¹¹¹¹¹¹¹¹¸ÂÂÂÂÂÁÁÁÃÃÃÃ\022\022\022\022--------¹¹¹¹¹¹¹¸¸ÂÂÂÂÂÂÃÃÃÃÃ\022\022\022\022\"\"\"\"õõõõõòóóóóñññßßááááááââââ\025\025\025\"\"\"\"õõõõõòòòóóññññßááááááââââ\025\025\025\"\"\"\"õõõõõòòòòññññññááááááâââââåå\"\"\"\"\"õõõõõòòòòñññññááááááâââââåå\"\"õõõõòòòòññññññáááááââââÜÜå££££ñññññ\026\026\026\026\026áâââÜÜÜÜ£££££===ññ\026\026\026\026\026\026\026âÜÜÜÜÜ£££££=====wwÌ\026\026\026\026\026\026\026ãÜÜÜÜÜ¤IIIII===\005\005xxxÌÌÌ\026\026\026\026\026ÎÎÛÛÛÛ¤¤¤¤IIV\005\005\005\005\005xxxxyyyyÌÌ\026\026\026\026ÎÎÎÎÛÛ¤¤¤¤¤VVV\005\005\006\006\006ttxyyyyyyÌÎÎÎÚÚÚ¤¤¤¤¤¤VVZZ\006\006\006\006ttÍyyyyÏÏÏÚÚÚÚÚ¥¥¥¥¥¥ZZZZZZ\006\006\006sÍÍÍÍÏÏÏÏÏ{ÙÚÚÚ±±±±±±[[[ZZZZ\007\007ssssÍzzÏÏÏ{{××ÙÙÙ±±±±UUUU[[eeiiisssssszzØØØÐÐ×××Ù*UUUUUUUUUeeeeiisssssrØØØØØÐÐ×××+++UUUUUU\\\\eeee3333ssrrrrØØØÐÐ××µµµµµ\\\\\\\\\\\\\\\\\\e333333rrrrrrrØÐ\024\024µµµµµµµ\\\\\\\\\\\\\\¾¾¾2222ÑÑÑrrrÈÈÒÒÒµµµµµµµµ\\\\\\\\\\\\¾¾¾¾2222ÑÑÑÑÈÈÈÒÒÒ°°°°°°°»»»]]]]]]¾¾jjjjjÑÑÑÑÈÈÒÒÒ°°°°°°°»»»»»]]]]]]jjjjjjjÑÑÑÖÖÒÒ°°°°,,,,,»»»»]]]]]d\021\021\021jjjjqÖÖÖÖÖº,,,,,,,,,,»»^^^^^¿¿\021\021\021\021\021\021\021ÔÔÔÖÖººººº,,,,,,,^^^^^^^¿¿\021\021\021\021\021\021kÔÔÔÔ¯¯¯¯¯¯¯¯,,,ÀÀÀ^^^^^^¿¿ÇÇÇ\021kkÆÆÔÔ¶¶¶¶¶¶¶¶¯ÀÀÀÀÀÀÀ^^^^^ÇÇÇÇÇÇÇÆÆÆÆ···¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÅÅÅÅ········¹¹¹ÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ·······¹¹¹¹¹¹¹¹ÀÂÂÁÁÁÁÁÁÃÃÇÇ\022\022ÅÅ------¹¹¹¹¹¹¹¹¹¸ÂÂÂÂÂÁÁÃÃÃÃÃ\022\022\022\022--------¹¹¹¹¹¹¸¸¸ÂÂÂÂÂÂÃÃÃÃÃÃ\022\022\022\"\"\"\"\"õõõõòòòòñññññááááááââââââåå\"\"\"\"\"õõõõòòòòòñññññáááááââââââåå\"\"\"\"õõõõòòòòòñññññáááááââââââåå\"\"\"õõõõõòòòòñññññááááááâââââåå\"\"õõõõõòòòòññññññáááááââââÜåå£££££òññññ\026\026\026\026\026áâââÜÜÜå£££££====ññ\026\026\026\026\026\026\026âÜÜÜÜÜ£££££======\026\026\026\026\026\026\026ããÛÛÛÛ¤£££II====\005xxxÌÌ\026\026\026\026\026\026ããÛÛÛÛ¤¤¤¤¤VG\005\005\005\005\005xxxxyyyyÌÌ\026\026\026\026ÎÎÎÛÛÛ¤¤¤¤¤VVV\006\006\006\006\006txyyyyyyÚÚÚÚÚ¥¤¤¤¤¤VZZZ\006\006\006\006\006ÍyyyyyÏÏÚÚÚÚ¥¥¥¥¥¥ZZZZZZ\006\006\006sÍÍÍÍzzÏÏ{{ÙÙÚÚ¥¥¥¥¥JJJ[ZZZ\007\007\007sssszzzzz{{{{ÙÙÙÙ±±±UUUUUUeeee\007\007ssssszzzØØ{{{×××Ù**UUUUUUUUeeee333ssssrØØØØØ{{×××****UUUUU\\\\eee333333rrrrrØØØÐÐ\024\024µµµµU\\\\\\\\\\\\\\\\\\e333333rrrrrrrØ\024\024\024µµµµµµ\\\\\\\\\\\\\\\\¾¾22222ÑÑÑrrrÈÒÒÒÒ°°°°°µµµ\\\\\\\\\\\\¾¾¾¾2222ÑÑÑÑÑÈÒÒÒÒ°°°°°°°°»»]]]]]]¾¾jjjjjÑÑÑÑÑÈÒÒÒ°°°°°°°°»»»]]]]]]]jjjjjjjqÑÑÖÖÒÒ°°°°°°,,,»»»]]]]]dddjjjjjqqÖÖÖÖÖ,,,,,,,,,,,»^^^^^^¿d\021\021\021\021\021\021\021ÔÔÔÔÔ¯¯¯¯¯,,,,,,,^^^^^^^^¿\021\021\021\021\021kkÔÔÔÔ¯¯¯¯¯¯¯¯,,,ÀÀ^^^^^^^^¿ÇÇÇkkkÆÆÆÔ¯¯¯¯¯¯¯¯¯ÀÀÀÀÀÀÀ^^^^^ÇÇÇÇÇÇÇÆÆÆÆ···¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÅÅÅÅ········¹¹¹¹ÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÅÅÅÅ·······¹¹¹¹¹¹¹¹ÀÂÂÁÁÁÁÁÁÃÃÇ\022\022\022ÅÅ------¹¹¹¹¹¹¹¹¸¸¸ÂÂÂÂÁÁÃÃÃÃÃ\022\022\022\022--------¹¹¹¹¹¹¸¸¸ÂÂÂÂÂÂÃÃÃÃÃÃ\022\022\022\"\"\"õõõõòòòòòññññááááááâââââååå\"\"\"õõõõòòòòòñññññáááááâââââååå\"\"õõõõòòòòòñññññáááááâââââååå\"õõõõòòòòòñññññáááááââââââååõõõõõòòòòññññññááááâââââÜåå£££££õòññññ\026\026\026\026\026ðâââÜÜÜå££££££====ññ\026\026\026\026\026\026ãããÜÜÜÜ£££££>=====\026\026\026\026\026\026ãããÛÛÛÛ##£££>>>>==xyÌ\026\026\026\026\026ãããÛÛÛÛ¤¤¤¤GGG>>\005\005\005xxyyyãÎÎÛÛÛ¤¤¤¤¤GGGG\006\006\006\006xxyyyÚÚÚÚÚ¥¥¥¤¤¤GGG\006\006\006\006\006\006yÏÚÚÚÚ¥¥¥¥¥¥JZZZZZ\007\007\007ssÍzzzzz{ÙÙÙÙÚ¥¥¥¥¥JJJJZZ\007\007\007\007\007ssszzzzz{{{{ÙÙÙÙ***UUUUJJeee\007\007\007\007sssszzzz{{{{××ÙÙ****UUUUUUeeee333ssssrØØØØ{{{×××*****UUUUUTeee333333rrrrrØØØ{\024\024\024*****\\\\\\\\\\\\\\TT3333333rrrrrrrØ\024\024\024µµµµµµ\\\\\\\\\\\\TTT222222ÑÑÑÑrrrÒÒÒÒ°°°°°°°\\\\\\\\\\]]]¾¾22222ÑÑÑÑÑÈÒÒÒÒ°°°°°°°°°]]]]]]]]jjjjjjÑÑÑÑÑÒÒÒÒ°°°°°°°°°»]]]]]]]djjjjjjqqqÑÖÒÒÒ°°°°°°°,,SSS]]]]]dddjjjjjqqqÖÖÖÓ¯°,,,,,,,,SS^^^^^^dd\021\021\021\021\021\021qÔÔÔÔÔ¯¯¯¯¯¯,,,,,SS^^^^^^^_\021\021\021\021kkkÔÔÔÔ¯¯¯¯¯¯¯¯¯,,ÀÀ^^^^^^^^_ÇÇkkkkÆÆÆÔ¯¯¯¯¯¯¯¯¯¯ÀÀÀÀÀ^^^^^^ÇÇÇÇÇÇÇÆÆÆÆ····¯¯¯¯¯ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÅÅÅÆ·······¹¹¹¹¹ÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÅÅÅÅ······¹¹¹¹¹¹¹¹¸¸ÂÂÁÁÁÁÁÁÃÃÃ\022\022ÅÅÅ------¹¹¹¹¹¹¹¹¸¸¸ÂÂÂÂÁÁÃÃÃÃÃ\022\022\022\022--------¹¹¹¹¹¸¸¸¸¸ÂÂÂÂÂÃÃÃÃÃÃ\022\022\022\"õõõõòòòòòññ\031\031\031áááááâââââååå\"õõõõòòòòòññ\031\031\031áááááâââââååå\"õõõõòòòòòññ\031\031\031áááááâââââåååõõõõòòòòòññ\031\031\031\031ááááâââââåååõõõõòòòòò\031\031\031\031\031\026ááðâââââÜåå££££===õò\031\031\031\031\031\026\026\026ððãââÜÜÜå#££££>>===\031\031\026\026\026\026\026ããããÛÛÛÛ##£££>>>>>\026\026\026\026\026ããããÛÛÛÛ####>>>>>>>\026\026\026\026ããããÛÛÛÛ¤¤¤GGGG>>>>\006ããÛÛÛÛ¤¤¤¤GGGGGG\006\006\006\006ÚÚÚÚÚ¥¥¥¥¥GGGGG\006\006\006\006\006ÚÚÚÚ¥¥¥¥¥¥JJZZZ\007\007\007\007\007szzzzz{{ÙÙÙÙÙ¥¥¥¥¥JJJJJ44\007\007\007\007sszzzzzz{{{{ÙÙÙÙ****JJJJJJee4\007\007\007sssszzzz{{{{{ÙÙÙ*****UUUUJeee33333sssrzØØ{{{{\024\024\024*****UUUTTTTee333333rrrrrØØ{{\024\024\024******TTTTTTTT3333322rrrrr|||\024\024\024°°°**TTTTTTTTTT222222ÑÑÑÑr||ÒÒÒ\024°°°°°°°TTTTT]]]]222222ÑÑÑÑÑÈÒÒÒÒ°°°°°°°°°]]]]]]]]jjjjjjÑÑÑÑÑÒÒÒÒ°°°°°°°°°S]]]]]]]ddjjjjjqqqqÖÒÒÒ°°°°°°°°SSSS]]]]]dddjjjjjqqqÖÖÖÓ¯°°°°,,,,SSSS^^^^^ddd\021\021\021\021kqÔÔÔÔÔ¯¯¯¯¯¯¯,,,SSS^^^^^^__\021\021kkkkkÔÔÔÔ¯¯¯¯¯¯¯¯¯¯,SS^^^^^^^__kkkkkkÆÆÆÔ¯¯¯¯¯¯¯¯¯¯ÀÀÀÀÀ^^^^^__ÇÇÇÇÇÆÆÆÆÆ®®®®®®®¯¯¯ÀÀÀÀÀÀÀÁÁÁÁ``ÇÇÇÇÇÅÅÅÆ·····®®®¹¹¹¹ÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÅÅÅÅ······¹¹¹¹¹¹¹¹¸¸ÂÂÁÁÁÁÁÃÃÃÃ\022\022ÅÅÅ-----­¹¹¹¹¹¹¹¸¸¸¸ÂÂÂÂÁÃÃÃÃÃÃ\022\022\022Ä--------¹¹¹¹¸¸¸¸¸¸ÂÂÂÂÃÃÃÃÃÃÃ\022\022\022!!!!!õõõõòòòò\031\031\031\031\031\031ááááââââââååå!!!!ÿõõõòòòòò\031\031\031\031\031ááááðâââââååå!!!!ÿõõõòòòòò\031\031\031\031\031\031ááððâââââååå!!!ÿÿõõòòòòò\031\031\031\031\031\031ðððððââââååå!!ÿÿÿõõòòòò\031\031\031\031\031\031ððððððâââååå##££>>>>\031\031\031\031\031\031\026ððððãããÛÛåå###£>>>>>>\031\031\031\031\026\026ððããããÛÛÛÛ####>>>>>>\031\026\026\026ãããããÛÛÛÛ####>>>>>>>ããããÛÛÛÛ####GGG>>>>>ããÚÛÛÛ¥¤¤GGGGGGG?\006\006ÚÚÚÚ¥¥¥¥¥GGGGGG\006\006\006\006zÙÚÚÚ¥¥¥¥¥JJGG444\007\007\007\007zzzzz{{ÙÙÙÙÙ¥¥¥¥JJJJJJ444\007\007\007sszzzzz{{{{ÙÙÙÙÙ****JJJJJJF444\007\007ssszzzzz{{{{{ÙÙÙ******JJJJJee33333ssrzzØ{{{{{\024\024\024******UTTTTT¦333333\brrrr||{{{\024\024\024******TTTTTTTT3332222rrr|||||\024\024\024°****TTTTTTTTTT222222ÑÑÑÑ|||ÒÒÒ\024°°°°°°°TTTTT]]]]222222ÑÑÑÑÑÑÒÒÒÒ°°°°°°°°]]]]]]]]]jjjjjjÑÑÑÑÑÒÒÒÒ°°°°°°°°SS]]]]]]]ddjjjjqqqqqÓÓÓÒ°°°°°°°°SSSS]]]]ddddjjjjqqqqÔÖÓÓ¯°°°°°°SSSSSS^^^^dddd\021\021kkkqÔÔÔÔÔ¯¯¯¯¯¯¯¯SSSSS^^^^^___kkkkkkkÔÔÔÔ¯¯¯¯¯¯¯¯¯¯SSSS^^^^^___kkkkkkÆÆÆÔ¯¯¯¯¯¯¯¯¯¯ÀÀÀÀÀ^^^^___ÇÇÇÇkÆÆÆÆÆ®®®®®®®®®®ÀÀÀÀÀÀÁÁÁ````ÇÇÇÇÅÅÅÆÆ®®®®®®®®®¹¹¹ÀÀÀÀÁÁÁÁÁÁ``cÇÇÇÅÅÅÅ­­­­­­­¹¹¹¹¹¹¸¸¸ÂÂÁÁÁÁÁÃÃÃc\022\022ÄÄÄ-­­­­­­­¹¹¹¹¸¸¸¸¸ÂÂÂÂÁÃÃÃÃÃÃ\022\022\022Ä------­­­¹¹¸¸¸¸¸¸¸ÂÂÂÂÃÃÃÃÃÃÃ\022\022\022!!!!!ÿÿÿÿòòòò\031\031\031\031\031\031\031ððððââââåååå!!!!!ÿÿÿÿòòòò\031\031\031\031\031\031\031ðððððâââåååå!!!!!ÿÿÿÿòòòò\031\031\031\031\031\031\031ðððððâââåååå!!!!ÿÿÿÿÿÿòòò\031\031\031\031\031\031\031ððððððâââååå!!!!ÿÿÿÿÿÿòò\031\031\031\031\031\031\031ððððððãããååå####ÿÿÿÿÿþ\031\031\031\031\031\031ðððððããããÛåå####>>>>>>\031\031\031ððððãããããÛÛÛ#####>>>>>öðããããããÛÛÛ#####>>>>>>ãããããÛÛÛ####GGGG>>??ããÛÛä###GGGGGG?????ÚÚÚÚ¥¥¥¥GGGGGG?????zÙÙÚÚ¥¥¥¥¥JJGG44444\007\007zzzzz{{ÙÙÙÙÙ¥¥¥¥JJJJJF4444\007\007\007szzzzz{{{{ÙÙÙÙÙ****JJJJJFFF444\007\007sszzzz{{{{{ÙÙÙÙ******JJJJ¦FF\b\b\b\b\b\bsrzz{{{{{\024\024\024\024*******TTT¦¦¦¦\b\b\b\b\b\br|||||{{\024\024\024\024******TTTTTTT¦¦\b\b2222Ñ|||||||\024\024\024******TTTTTTTTT222222ÑÑÑÑ||||ÒÒ\024°°°°°°LLLTTT]]]2222222ÑÑÑÑÑ|ÒÒÒÒ°°°°°°°°LL]]]]]]ddjjjjqqÑÑÑÑÒÒÒÒ°°°°°°°°SSS]]]]]dddjjjjqqqqqÓÓÓÓ°°°°°°°SSSSS]]]]ddddjjjjqqqqÔÓÓÓ¯¯°°°°SSSSSSSS^^_ddddkkkkkqqÔÔÔÓ¯¯¯¯¯¯¯SSSSSSS^^_____kkkkkkkÔÔÔÔ¯¯¯¯¯¯¯¯¯¯SSSS^^^_____kkkkkkÆÆÆÔ¯¯¯¯¯¯¯¯¯¯¯ÀÀÀ^^^^__```ÇÇkkÆÆÆÆÆ®®®®®®®®®®®ÀÀÀÀÀ````````ÇÇÇÅÅÅÆÆ®®®®®®®®®®®¹¸ÀÀÀÁÁÁÁ````ccclÅÅÅÅ­­­­­­­­­­¹¹¸¸¸¸ÂÂÁÁÁÁÃÃÃcccÄÄÄÄ­­­­­­­­­­¹¸¸¸¸¸¸ÂÂÂÂÃÃÃÃÃÃÃ\022\022ÄÄ---­­­­­­­­¸¸¸¸¸¸¸ÂÂÂÂÃÃÃÃÃÃÃ\022\022\022!!!!ÿÿÿÿÿÿò\031\031\031\031\031\031\031\031ðððððððââåååå!!!!ÿÿÿÿÿÿòò\031\031\031\031\031\031\031ðððððððââåååå!!!!ÿÿÿÿÿÿòò\031\031\031\031\031\031\031\031ððððððãâåååå!!!!ÿÿÿÿÿÿÿò\031\031\031\031\031\031\031\031ððððððããåååå!!!!ÿÿÿÿÿÿþ\031\031\031\031\031\031\031ðððððããããååå####ÿÿÿÿÿþþ\031\031\031\031\031ðððððããããÛÛå#####>>>þþþööööððððãããããÛÛÛ#####>>>>þþööããããããÛÛÛ#####>>>>>?ããããÛÛä#####GGG?????ããäää¢¢#GGGGGG?????ÚÚÚä¥¥¥¥GGGGG??????zÙÙÙÚ¥¥¥¥JJJFF444444\007zzzzz{ÙÙÙÙÙ***JJJJJFFF44444\007zzzzzz{{{{ÙÙÙÙÙ*****JJJJFFF4444\b\bzzzzz{{{{{ÙÙÙÙ******JJJ¦¦FF\b\b\b\b\b\b\b||||{{{{\024\024\024\024*******KKK¦¦¦¦\b\b\b\b\b\b|||||||{\024\024\024\024******KKKKKK¦¦¦\b\b\b222||||||||\024\024\024§§§§§*KKKKKKKK¦222222ÑÑÑ|||||Ò\024\024°°°°°LLLLLKKK]]222222\tÑÑÑÑÑ|ÒÒÒÒ°°°°°°°LLLL]]]]]d11jjqqqqqÑÑ}ÒÒÒ°°°°°°°SSSS]]]]]dddjjjqqqqqq}ÓÓÓ°°°°°°SSSSSSS]]ddddddjjqqqqqqÓÓÓ¯¯°°°SSSSSSSSS___ddddkkkkkqpÔÔÔÓ¯¯¯¯¯¯¯SSSSSSS^______kkkkkkkoÔÔÔ¯¯¯¯¯¯¯¯¯SSSSS^^______kkkkkÆÆÆÆÆ®¯¯¯¯¯¯¯¯¯¯SSSS^___````kkkkÆÆÆÆÆ®®®®®®®®®®®®ÀÀÀÀ```````cccllÅÆÆÆ®®®®®®®®®®®®¸¸¸ÀÁÁ`````ccccllÄÄÄ­­­­­­­­­­­¸¸¸¸¸¸ÂÁÁÁÁÃÃÃccclÄÄÄ­­­­­­­­­­­¸¸¸¸¸¸¸ÂÂ..ÃÃÃÃÃb\022\022ÄÄ­­­­­­­­­­­¸¸¸¸¸¸¸ÂÂ...ÃÃÃÃÃÃ\022\022\022!!!!ÿÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ððððððððãåååå!!!!ÿÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ðððððððããåååå!!!ÿÿÿÿÿÿÿÿö\031\031\031\031\031\031\031ðððððððããåååå!!ÿÿÿÿÿÿÿþþööö\031\031\031\031ðððððððãããåååÿÿÿÿÿÿþþþööööööööðððððããããÛåå####ÿÿÿÿþþþþööööööððððãããããÛÛå#####ÿÿþþþþþööööööðððãããããÛää#####>>þþþþþöööããããããäää######>>??þþããããäää¢¢###GG??????ãääää¢¢¢¢¢GG???????Ùäää¢¢¢¢¢GGG???????ÙÙÙä¥¥¥¥JJFFFF444444zzzz{ÙÙÙÙÙ****JJJFFFF444444zzzzzz{{{{ÙÙÙÙÙ*****JJJFFFFF44\b\b\bzzzz{{{{{\024ÙÙÙ******JJ¦¦¦FFF\b\b\b\b\b\b||||{{{{\024\024\024\024******KKKK¦¦¦¦\b\b\b\b\b\b|||||||\024\024\024\024\024******KKKKKK¦¦¦\b\b\t\t\t\t||||||||\024\024\024§§§§§§LKKKKKKK¦\t\t\t\t\t\t\tÑ||||||Ò\024\024§§§§§LLLLLLKKKK11\t\t\t\t\tÑÑÑÑ||ÒÒÒÒ°°°°°°LLLLLL]]]d1111\n\nqqqqq}}ÓÓÒ°°°°°°SSSSLL]]]dddd1\n\nqqqqq}}ÓÓÓ°°°°°SSSSSSSS]]ddddddkkqqqqq}ÓÓÓ¯¯¯°SSSSSSSSSS____dddkkkkkpppÔÓÓ¯¯¯¯¯¯¯SSSSSSS_______kkkkkkoooÔÔ¯¯¯¯¯¯¯¯¯SSSSSS_______kkkkkÆÆÆÆÆ®®®®®®®®®®®SSSS_```````ckkkÆÆÆÆÆ®®®®®®®®®®®®®ÀÀ````````ccclllÆÆÆ®®®®®®®®®®®®¸¸¸¸```````ccccllÄÄÄ­­­­­­­­­­­­¸¸¸¸¸....``ÃcccclÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸....ÃÃÃÃÃbbÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸¸....ÃÃÃÃÃÃ\022\023\023ÿÿÿÿÿÿÿÿööööööööðððððððããååååÿÿÿÿÿÿÿÿööööööööðððððððããååååÿÿÿÿÿÿÿþþöööööööðððððððãããåååÿÿÿÿÿÿþþööööööööðððððããããåååÿÿÿÿÿþþþööööööööðððððããããäååÿÿÿÿþþþþþööööööööðððãããããäääÿþþþþþþþöööööööððããããããäää#####þþþþþþþþööööãããããäää¢¢¢¢##??þþþþþããääää¢¢¢¢¢¢???????ääää¢¢¢¢¢¢????????Ùäää¢¢¢¢¢¢G????????ÙÙÙä¡¡¡¡¢FFF5555544zzÙÙÙÙÙ****JFFFFFF554@@@zz{{{{ÙÙÙÙÙ*****JFFFFFFF5@@@@{{{{\024\024ÙÙ******K¦¦¦¦FFF\b\b\b\b\b{{\024\024\024\024\024******KKKK¦¦¦¦\b\b\b\b\b\b|||||||\024\024\024\024\024§§§§§§KKKKKK¦¦¦\t\t\t\t\t\t|||||||\024\024\024\024§§§§§§LLKKKKKK¦\t\t\t\t\t\t\t||||||ççç§§§§§§LLLLLLKK1111\t\t\t\tqÑÑ|||Òçç°°§§§LLLLLLLLL]11111\n\nqqqq}}}}ÓÓ¨¨¨¨¨SSSLLLLL]]ddd11\n\nqqqqq}}ÓÓÓ¨¨¨¨¨SSSSSSSSMddddddd\013\013qqqqp}ÓÓÓ¨¨¨¨¨SSSSSSSSS____dddkkkkkppppÓÓ¯¯¯¯¯¯SSSSSSSS_______kkkkkkooooÔ¯¯¯¯¯¯¯¯SSSSSSS_______kkkkkÆÆÆoo®®®®®®®®®®®SSSS````````ckkkÆÆÆÆÆ®®®®®®®®®®®®®®`````````ccclllÆÆÆ®®®®®®®®®®®®®¸¸¸```````cccclllÄÄ­­­­­­­­­­­­¸¸¸¸.....``ccccblÄÄÄ­­­­­­­­­­­­¸¸¸¸¸......ÃÃÃbbbÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸......ÃÃÃÃb\023\023\023ÿÿÿÿÿÿþöööööööööððððððããååååÿÿÿÿÿÿþþööööööööððððððãããåååÿÿÿÿÿÿþþööööööööðððððããããåååÿÿÿÿþþþööööööööðððððããããääåÿÿÿþþþþöööööööööððððããããäääÿÿþþþþþþööööööööðððãããããäääþþþþþþþþþöööööööïããããääää¢¢¢¢ þþþþþþþþööööööãããääää¢¢¢¢¢  þþþþþþþããääää¢¢¢¢¢¢ ???????ääää¢¢¢¢¢¢¢???????Ùäää¢¢¢¢¢¢¢555?????ÙÙÙÙæ¡¡¡¡¡¡55555555@@ÙÙÙÙæ¡¡¡¡¡$FFF55555@@@{{{{ÙÙÙÙÙ***$$$$FFFF55@@@@@{{\024\024\024\024Ù*****$$$¦¦¦FF@@@@@@{\024\024\024\024\024§§§***KKKK¦¦¦¦\b\b\b\b\b\b||||||\024\024\024\024\024§§§§§§KKKKKK¦¦¦\t\t\t\t\t\t||||\024\024\024§§§§§§§LLKKKKK¦\t\t\t\t\t\t\t||ççç§§§§§§LLLLLLLK11111\n\n\nqq}ççç§§§§§LLLLLLLLLL11111\n\n\nqqq}}}}ÓÓ¨¨¨¨¨¨¨LLLLLLLMdd111\n\n\nqqq}}}}ÓÓ¨¨¨¨¨¨SSSSSSMMMMdddd\013\013\013\013qqpp}}ÓÓ¨¨¨¨¨¨SSSSSSSS___RddkkkkkpppppÓÓ¯¯¯¯¨))SSSSSSS____RRRkkkkkkooooÕ®®®®®®®¯SSSSSSS____RRRkkkkkooooo®®®®®®®®®®®SSSS````````ccklÆÆÆÆn®®®®®®®®®®®®®®`````````cccllllÆn®®®®®®®®®®®®®¸¸````````cccclllÄÄ­­­­­­­­­­­­­¸¸¸.....``ccbbbllÄÄ­­­­­­­­­­­­­¸¸¸¸......Ãbbbbb\023ÄÄ­­­­­­­­­­­­­¸¸¸¸¸......ÃÃÃbb\023\023\023ÿÿÿÿþþþööööööööðððððãããäîîåÿÿÿÿþþþööööööööðððððãããääîîÿÿÿÿþþþöööööööööððððãããääîîÿÿÿþþþþöööööööööïïïïïããääîîýÿþþþþþþööööööööïïïïïããäääîýýþþþþþþöööööööö\032ïïïïïãääää  þþþþþþþööööööö\032\032ïïïïïääää¢¢¢¢   þþþþþþþööööö\032\032\032\032ïïäääää¢¢¢¢¢   þþþþþþ\032\032\032\032\032äääää¢¢¢¢¢¢   ????þ\032\032ääää¢¢¢¢¢¢¢ ??????Ùäää¡¡¡¡¢¢¢55555???ÙÙæææ¡¡¡¡¡¡55555555@@ÙÙæææ¡¡¡¡¡$$555555@@@@{{ÙÙÙææ$$$$$$$$F5555@@@@@{\024\024\024\024æ$$$$$$$$$¦¦F5@@@@@@\024\024\024\024\024§§§§§$$$$K¦¦¦¦@@@@\t\t\024\024\024\024§§§§§§§KKKKK¦¦¦\t\t\t\t\t\tçççç§§§§§§§LLLKKKK1\t\t\t\t\t\t\tççç§§§§§§§LLLLLLE11111\n\n\n\nq}}ççç§§§§§§LLLLLLLLL11111\n\n\nqq}}}}}ÓÓ¨¨¨¨¨¨¨LLLLLLMMM1111\n\n\nqq/}}}}ÓÓ¨¨¨¨¨¨¨)SSSMMMMMMdd1\013\013\013\013\013ppp}}ÓÓ¨¨¨¨¨¨)))SSSSMMRRRRR\013\013\013\013\013pppppÕÕ¨¨¨¨)))))SSSSSRRRRRRRkkkkkoooooÕ®®®®®®®)))SSSSRRRRRRRRkkkkkooooo®®®®®®®®®®®SSS`````````cclllÆnnn®®®®®®®®®®®®®``````````cccllllnn®®®®®®®®®®®®®¸¸````````cccclllÄÄ­­­­­­­­­­­­­¸¸¸.....``bbbbbllÄÄ­­­­­­­­­­­­­¸¸¸........bbbbbmmÄ­­­­­­­­­­­­­¸¸¸¸........Ãbbb\023\023\023ýýýýþþþööööööööïïïïïïïïîîîîýýýýþþþööööööööïïïïïïïïîîîîýýýýþþþöööööööööïïïïïïïîîîîýýýýþþþþööööööööïïïïïïïîîîîýýýýþþþþööööööööïïïïïïïäîîîýýýþþþþþööööööö\032\032ïïïïïïääîî¢    þþþþþþöööööö\032\032\032\032ïïïïäääî¢¢¢¢     þþþþþ÷öööö\032\032\032\032ïïïääää¢¢¢¢¢     þþþþ\032\032\032\032\032ïääää¢¢¢¢¢¢     ?þþ\032\032\032\032\032ääää¡¢¢¢¢¢¢   ????\032\032\032æææä¡¡¡¡¡¡¡5555555æææææ¡¡¡¡¡¡¡555555@@@æææææ¡¡¡¡¡$$$55555@@@@Ùææææ$$$$$$$$$5555@@@@@\024\024\024\024ææ$$$$$$$$$$$5@@@@@@@\024\024\024\024\024§§§§$$$$$$$¦¦66@@AAç\024\024\024§§§§§§§$$$KK¦¦AAAAAAçççç§§§§§§§§LLLEEEE1\t\t\t\t\tçççç§§§§§§§LLLLLLEE1111\n\n\n\n/}}ççç¨§§§§§LLLLLLLLE1111\n\n\n\n//}}}}}}ç¨¨¨¨¨¨¨LLLLLLMMMM111\n\013\013///}}}}}Ó¨¨¨¨¨¨))))LMMMMMMM1\013\013\013\013\013\013ppp}}}Ó¨¨¨¨¨))))))SMMMMRRRR\013\013\013\013\013pppppÕÕ¨¨¨¨)))))))SSRRRRRRRRkkkkkoooooÕ®®®®®)))))))SSRRRRRRRRkkk\f\fooooo®®®®®®®®®®)))S````````ccclllnnnn®®®®®®®®®®®®®©````````ccccllllnn®®®®®®®®®®®®­­.```````cccccllllÄ­­­­­­­­­­­­­­¸......QQbbbbbllÄÄ­­­­­­­­­­­­­­¸¸........bbbbbmmm­­­­­­­­­­­­­­¸¸¸........bbbb\023\023\023ýýýýýý÷÷÷ööööööïïïïïïïïîîîîýýýýýþþ÷÷ööööööïïïïïïïïîîîîýýýýýþþ÷÷÷öööööïïïïïïïïîîîîýýýýýþþ÷÷÷ööööö\032ïïïïïïïîîîîýýýýýþþ÷÷÷ööööö\032\032ïïïïïïîîîî  ýýýýþþþ÷÷÷öööö\032\032\032ïïïïïîîîî¢¢¢      ýþþþ÷÷÷÷öö\032\032\032\032\032ïïïïäîîî¢¢¢¢      þþþþ÷÷÷÷\032\032\032\032\032\032ïïääîî¢¢¢¢       þþþ\032\032\032\032\032\032äääî¢¢¢¢¢       þ\032\032\032\032\032æäää¡¡¡¡¡¢      5\032\032\032\032ææææ¡¡¡¡¡¡¡55555@\032æææææ¡¡¡¡¡¡¡555555@@@æææææ¡¡¡¡¡$$$$5555@@@@æææææ$$$$$$$$$$55@@@@@@\024\024æææ$$$$$$$$$$$6666@@@\024\024\024\024\024§§§$$$$$$$$$6666AAAççççç§§§§§§$$$$$EEEAAAAAAçççç§§§§§§§§LLEEEEEEAAAAAçççç§§§§§§§LLLLLEEEE111\n\n\n//}}}ççç¨¨§§§§§LLLLLLEEE111\n\n\n////}}}}èè¨¨¨¨¨¨¨)LLLLMMMM0011\013\013\013///}}}}}Ó¨¨¨¨¨¨)))))MMMMMMM0\013\013\013\013\013//pp}}ÕÕ¨¨¨¨¨)))))))MMMMRRRR\013\013\013\013\013ppppÕÕÕ¨¨¨))))))))))RRRRRRRRR\013\f\f\foooooÕ®®)))))))))))RRRRRRRRR\f\f\f\f\foonnn®®®®®®®®®)))©©````````cc\f\f\f\fnnnn®®®®®®®®®®®©©©````````cccclllnnn®®®®®®®®®®®­­©©.``QQQQQccclllllÄ­­­­­­­­­­­­­­¸.....QQQbbbbblllÄ¬¬¬¬­­­­­­­­­­¸........bbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬­­­¸¸.........bbb\023\023\023\023ýýýýýýý÷÷÷÷÷÷öööïïïïïïïïîîîîýýýýýýý÷÷÷÷÷÷ööö\032ïïïïïïïîîîîýýýýýýý÷÷÷÷÷÷ööö\032ïïïïïïïîîîîýýýýýýýý÷÷÷÷÷÷ö\032\032\032ïïïïïïîîîî ýýýýýýý÷÷÷÷÷÷ö\032\032\032\032ïïïïïîîîî    ýýýýý÷÷÷÷÷÷÷\032\032\032\032\032ïïïïîîîî        ýý÷÷÷÷÷÷÷\032\032\032\032\032\032ïïïîîîî¢¢¢         þ÷÷÷÷÷\032\032\032\032\032\032\032ïíîîî¢¢¢¢         \032\032\032\032\032\032\032ííîî¡¡¡¡¢        \032\032\032\032\032æææî¡¡¡¡¡¡\032\032\032æææææ¡¡¡¡¡¡¡5@\032æææææ¡¡¡¡¡¡¡555@@ææææææ¡¡¡¡¡$$$$555@@@@@æææææ$$$$$$$$$$$6666@@\024ææææ$$$$$$$$$$$6666666çççëë§§$$$$$$$$$$6666AAAççççç§§§§§§$$$$$EE77AAAAAçççç§§§§§§§§LEEEEEEEAAAAAçççç§§§§§§§§LLLEEEEEE11\n\n/}}}ççç§§§LLLLLEEEEE01\n\n/////}}}}èè¨¨¨¨¨¨¨))LLMMMMM0000\013\013\013////}}}}è¨¨¨¨¨¨)))))MMMMMM000\013\013\013\013///p}ÕÕÕ¨¨¨¨))))))))MMMMNNNN\013\013\013\013\013ppppÕÕÕ¨¨)))))))))))NNNNNNNN\f\f\f\f\fooooÕÕ))))))))))))))NNNNNNNN\f\f\f\f\f\fnnnn®®®®®®®®©©©©©©©N``````cc\f\f\f\fnnnn®®®®®®®®®®©©©©©```QQQQcccllllnnn®®®®®®®®®®®­©©©QQQQQQQQccclllll~¬¬¬¬¬¬¬¬­­­­­­.....QQQQbbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬­­........bbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¸.........bbb\023\023\023\023ýýýýýýý÷÷÷÷÷÷÷÷\032\032ïïïïïïïîîîîýýýýýýý÷÷÷÷÷÷÷÷\032\032\032ïïïïïïîîîîýýýýýýýý÷÷÷÷÷÷÷\032\032\032ïïïïïïîîîî ýýýýýýý÷÷÷÷÷÷÷\032\032\032\032ïïïïïîîîî   ýýýýý÷÷÷÷÷÷÷\032\032\032\032\032ïïïïîîîî     ýýý÷÷÷÷÷÷÷\032\032\032\032\032\032ïïííîîî       ý÷÷÷÷÷÷÷\032\032\032\032\032\032\032ïííîîî        ü÷÷÷÷÷÷\032\032\032\032\032\032\032íííîî         \032\032\032\032\032\032ííííî¡¡¡¡¡   \032\032\032\032\032ææææ¡¡¡¡¡¡\032\032\032æææææ¡¡¡¡¡¡\032ææææææ¡¡¡¡¡¡¡@@ææææææ    $$$$$$66666@@ææææææ   $$$$$$$$666666ëëëëæ $$$$$$$$$$6666666ççççëë§§$$$$$$$$$77666AAAççççç§§§§§§$$$$$7777AAAAAççççç§§§$EEEEEEEAAAAAççççLLEEEEEEEEAA}èèèçLLLLEEEE0000\n///}}}èèè¨¨¨¨¨¨))))MMMMM00000\013\013/////}}}èè¨¨¨¨¨))))))MMMMM0000\013\013\013\013///pÕÕÕÕ¨¨¨¨))))))))MMMNNNNN0\013\013\013\013ppppÕÕÕ¨))))))))))))NNNNNNNND\f\f\f\f\foooéé))))))))))))©©NNNNNNNN\f\f\f\f\f\fnnné®®®®®©©©©©©©©©©NNNNOOOO\f\f\f\f\fnnnn®®®®®®®®©©©©©©©©QQQQQQQccl\r\r\rn~~¬¬¬¬¬¬¬¬¬¬©©©©©©QQQQQQQQbbllll~~¬¬¬¬¬¬¬¬¬¬¬¬¬¬©...QQQQQQbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.......QQbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.........bbb\023\023\023\023ýýýýýý÷÷÷÷÷÷÷÷÷\032\032ïïïïïííîîîýýýýýýý÷÷÷÷÷÷÷÷\032\032\032ïïïïííîîîýýýýýýý÷÷÷÷÷÷÷÷\032\032\032ïïïïííîîî ýýýýýý÷÷÷÷÷÷÷÷\032\032\032\032ïïíííîîî   ýýýý÷÷÷÷÷÷÷÷\032\032\032\032ïïííííîî     ýýü÷÷÷÷÷÷÷\032\032\032\032\032ïííííîî       üüü÷÷÷÷÷\032\032\032\032\032\032íííííî       üüüü÷÷÷÷\032\032\032\032\032\032íííííîüü\032\032\032\032\032\032ííííí¡¡¡¡\032\032\032\032\032ííæææ¡¡¡¡¡\032\032\032æææææ¡¡¡¡¡¡\032ææææææ     ¡66ææææææ      $$$66666ëëëëææ     $$$$$$666666ëëëëëë    $$$$$$$666666çççëëë  $$$$$$$$77776AAAççççç$$$$$77777AAAAAçççççEEEEE777AAAAççççEEEEEEEEEAAèèèèèLEEEEE0000}èèèè¨¨)))MMMM000000BB///}}Õèè¨¨¨¨¨))))))MMMMM00000\013\013\013////ÕÕÕÕ¨¨¨)))))))))MMNNNNN0DD\013\013\fééÕÕ)))))))))))))NNNNNNNDD\f\f\f\f\fééé)))))))©©©©©©©NNNNNNNDD\f\f\f\f\fnnnéªª©©©©©©©©©©©©©©NNOOOOO\f\f\f\f\fnnnn®®®®®©©©©©©©©©©©QQQQQOOOO\r\r\r\r~~~¬¬¬¬¬¬¬¬¬©©©©©©©QQQQQQQQbb\r\r\r\r\177~¬¬¬¬¬¬¬¬¬¬¬¬¬©©©..QQQQQQbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.......QQbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.........bbb\023\023\023\023ýýýýýüü÷÷÷÷÷÷÷\032\032\032ïïíííííîîýýýýýüüü÷÷÷÷÷÷\032\032\032ïïíííííîî ýýýýüüü÷÷÷÷÷÷\032\032\032\032ïíííííîî  ýýýüüüü÷÷÷÷÷\032\032\032\032ïííííííî   ýýüüüü÷÷÷÷÷\032\032\032\032\032ííííííî     üüüüü÷÷÷÷\032\032\032\032\032ííííííî     üüüüüüü÷÷\032\032\032\032\032\032ííííííüüüüüüü÷\032\032\032\032\032\032ííííííüüüü\032\032\032\032\032íííííí\032\032\032\032\032ííííí    ø\032\032ææææææ     øøøææææææ       6øøøëëëæææ       $6666ùùùëëëëëë       $$$$666666ùùùëëëëë      $$$$$776666ùçëëëë     $$$$$777777AAAççççë$$$7777777AAAçççççEEEE7777AAAAèèèççEEEEE888ABèèèèèEEEEE0000BBèèèèMMM0000000BBBèè¨¨¨¨)))))))MMMM000000BBBÕÕÕ¨¨)))))))))))NNNNNNDDDD\fééé)))))))))))©©NNNNNNNDDD\f\f\fééªªª©©©©©©©©©©©©NNNNNDDD\f\f\f\f\féªªªª©©©©©©©©©©©©NOOOOOOO\f\f\r\r\r~~~ªªªªª©©©©©©©©©©©©QOOOOOOO\r\r\r\r~~~¬¬¬««««««©©©©©©©©QQQQQOOOP\r\r\r\r\177\177¬¬¬¬¬¬¬¬¬¬¬¬«©©©©QQQQQQQPPPmmmm\177¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬......QQPPPPmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.......''PPP\023aaaýýýýüüüüüü÷÷÷\032\032\032\032íííííííîýýýýüüüüüü÷÷÷\032\032\032\032ííííííí\033ýýýüüüüüü÷÷÷\032\032\032\032ííííííí\033 ýýüüüüüüü÷÷\032\032\032\032ííííííí\033   üüüüüüüü÷\032\032\032\032ííííííí\033üüüüüüüüü\032\032\032\032\032íííííí\033üüüüüüüüüü\032\032\032\032íííííííüüüüüüüüü\032\032\032\032íííííííüüüüüüø\032\032\032\032ííííííüüøøøø\032íííííí   øøøøøææææææ    øøøøøëëæææ      6øøøøëëëëëë        666ùùùùùëëëëë        $$666666ùùùùëëëëë        $$7777666ùùùùëëëë       $$$7777777AAçççëë$$7777777AAAççççç888888777AAèèèèç888888888Bèèèè8888888BBBBèèè9999900BBBBèè)&&&&99900BBBB))&&NNNNDDDDDééé©©©©©©NNNNNDDDDD\f\fééªªªªª©©©©©©©©©©NNNNODDDD\f\f\féªªªªª©©©©©©©©©©©OOOOOOOO\r\r\r\r\r~~~ªªªªªª©©©©©©©©©©©OOOOOOOO\r\r\r\r\r~~««««««««««©©©©©©©QQQOOOOP<\r\r\r\r\177\177¬¬¬¬¬¬¬«««««««©©©QQQQQPPPPP\016\016\016\016\177¬¬¬¬¬¬¬¬¬¬¬¬¬«««...''''PPPPPmaaa¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬...'''''PPPaaaaýüüüüüüüüüü÷\032\032ííííííí\033\033ýüüüüüüüüüüü\032\032ííííííí\033\033üüüüüüüüüüü\032\032\032íííííí\033\033üüüüüüüüüüü\032\032\032íííííí\033\033üüüüüüüüüü\032\032\032íííííí\033\033üüüüüüüüüü\032\032\032íííííí\033\033üüüüüüüüüø\032\032íííííí\033\033üüüüüüüüøøøø\032ííííí\033\033üüüüûûûøøøøøííííí\033\033ûûûûûûøøøøøøííììììûûûûûûøøøøøøøììììì   ûûûûøøøøøøøëëììì     øøøøøøëëëëë       ùùùùùùëëëëë        \036\036ùùùùùëëëëë         $7777\036\036\036\036ùùùùùëëëë%%%%%%   7777777\036\036\036ùùù\034\034\034\03488777777\036\036Aèè\034\034\034888888888Aèèèèè888888888BBèèèè88888899BBBèè&999999BBBBBè&&&&99999BBBB&&&&&:::DDDééé©©©©&&&&:DDDDD\035ééªªªªªª©©©©©©©©©©NN;;;DDD\f\f\féªªªªªªª©©©©©©©©©©OOOOO;;\r\r\r\r\r~~~ªªªªªªª©©©©©©©©©©OOOOOOO<\r\r\r\r\177\177~«««««««««««©©©©©©OOOOOOPP<<<\016\016\177\177(«««««««««««««©©©'QQQPPPPPP\016\016\016\016\177(((((¬¬¬¬««««««««''''''PPPPP\016\016aa((((((¬¬¬¬¬¬¬¬¬««'''''''''PPaaaa\037üüüüüüüüüüüø\032íííííí\033\033\033\037üüüüüüüüüüüø\032íííííí\033\033\033\037\037üüüüüüüüüüüø\032íííííí\033\033\033\037\037üüüüüüüüüüøøíííííí\033\033\033\037\037üüüüüüüüüüøøøííííí\033\033\033\037\037üüüüüüüüüüøøøííííí\033\033\033üüüüüüüûøøøøøíííí\033\033\033ûûûûûûûøøøøøøííí\033\033\033ûûûûûûûøøøøøøììììì\033ûûûûûûûøøøøøøøìììììûûûûûûøøøøøøøììììì ûûûûûøøøøøøøëëììì    ûûûùùùøøøøëëëëì      ùùùùùùùëëëëë%      \036\036\036ùùùùùùùëëëë%%%%%%   \036\036\036\036\036\036\036ùùùùùù\034\034ëë%%%%%%%%%7777\036\036\036\036\036\036\036ùùùùù\034\034\034\034%%%%%%%%888887\036\036\036\036\036\036úùè\034\034\034\03488888888\036\036úúúúúèèèè\03488888888BBúúúúèèè8888999BBBúúúèè&&999999BBBBúú&&&&&999::BBB&&&&&::::DD\035êê©©&&&&&::DDDD\035\035êêªªªªªªª©©©©©©©©&&&;;;;;DD\035\035Cêªªªªªªªª©©©©©©©©©OOOO;;;;\rCCC~ªªªªªªªª©©©©©©©©©OOOOOOO<<\r\r\r««««««««««««©©©©©©OOOOPPP<<<\016\016\177\177(««««««««««««««©©''''PPPPP<<\016\016\016\177(((((((««««««««««''''''PPPPP\016\016\016a(((((((((¬¬««««««'''''''''PPaaaa\037\037\037\037\037üüüüüüüüüüüøøíííí\033\033\033\033\033\037\037\037\037\037üüüüüüüüüüüøøøííí\033\033\033\033\033\037\037\037\037\037üüüüüüüüüüøøøííí\033\033\033\033\033\037\037\037\037\037üüüüüüüüûûøøøííí\033\033\033\033\033\037\037\037\037\037üüüüûûûûûøøøøøíí\033\033\033\033\033\037\037\037\037\037\037ûûûûûûûûøøøøøíí\033\033\033\033\033ûûûûûûûûøøøøøøíí\033\033\033\033ûûûûûûûûøøøøøøìììì\033\033ûûûûûûûøøøøøøøìììììûûûûûûøøøøøøøìììììûûûûûøøøøøøøìììììûûûûøøøøøøøøìììì  ûûûùùùùùùøøëëëì%%% \036ùùùùùùùùùëëëë%%%%%%\036\036\036\036ùùùùùùùù\034ëëë%%%%%%%%\036\036\036\036\036\036\036\036ùùùùùùù\034\034\034\034%%%%%%%%%%\036\036\036\036\036\036\036\036\036\036\036ùùùùùù\034\034\034\034%%%%%%%%%8888\036\036\036\036\036\036\036úúúùùù\034\034\034\034\034888888\036\036\036úúúúúèè\034\0348888888Búúúúúúèèè899999BBúúúúúè&&&99999BBBúúú&&&&&9:::BBB&&&&&&::::\035\035\035\035êêê©&&&&&&::;D\035\035\035\035êêªªªªªªª©©©©©©&&&;;;;;;\035\035\035Cêêªªªªªªªªª©©©©©©©©OO;;;;;;CCCCªªªªªªªªª©©©©©©©©OOOOOO<<<<CC«««««««««««««©©©©©OOOPPPP<<<\016\016\177\177(«««««««««««««««©''''PPPPP<<\016\016\016\016(((((((««««««««««'''''''PPPP\016\016\017\017((((((((((«««««««''''''''''P\017\017\017\017\037\037\037\037\037\037\037\037ûûûûûûûûûûøøøøí\033\033\033\033\033\033\037\037\037\037\037\037\037\037ûûûûûûûûûûøøøøí\033\033\033\033\033\033\037\037\037\037\037\037\037\037ûûûûûûûûûûøøøøí\033\033\033\033\033\033\037\037\037\037\037\037\037\037ûûûûûûûûûûøøøøí\033\033\033\033\033\033\037\037\037\037\037\037\037\037ûûûûûûûûûûøøøøø\033\033\033\033\033\033ûûûûûûûûûøøøøøì\033\033\033\033\033ûûûûûûûûûøøøøøìììì\033\033ûûûûûûûûøøøøøììììììûûûûûûøøøøøøøìììììûûûûûûøøøøøøøìììììûûûûûøøøøøøøìììììûûûûøøøøøøøøìììì%%ûûûùùùùùùùùëëìì%%%%%\036\036ùùùùùùùùù\034ëëë%%%%%%%\036\036\036\036\036ùùùùùùùù\034\034\034\034%%%%%%%%%\036\036\036\036\036\036\036\036\036ùùùùùùù\034\034\034\034%%%%%%%%%%\036\036\036\036\036\036\036\036\036\036\036\036ùùùùùù\034\034\034\034%%%%%%%%%88\036\036\036\036\036\036\036\036úúúúùù\034\034\034\034\03488888\036\036\036\036úúúúúú\034\034\0348888888úúúúúúúèè9999999úúúúúúè&&&99999BBúúúú&&&&&&::::B\035úê&&&&&&::::\035\035\035\035\035êêêê&&&&&&::;;\035\035\035\035\035êêêªªªªªªªª©©©©&&&&&;;;;;\035\035\035CCêêªªªªªªªªª©©©©©©©©&;;;;;;;CCCCªªªªªªªªªª©©©©©©©OOOOO;<<<<CCª«««««««««««««©©©©OOPPPP<<<<\016\016(««««««««««««««««'''''PPPP<<\016\016\016\016(((((((««««««««««''''''''PPP\017\017\017\017((((((((((«««««««''''''''''P\017\017\017\017".getBytes(StandardCharsets.ISO_8859_1)
            ;

    /**
     * Constructs a default PaletteReducer that uses the DawnBringer Aurora palette.
     */
    public PaletteReducer() {
        //this(Coloring.AURORA);
        exact(Coloring.AURORA, ENCODED_AURORA);
    }

    /**
     * Constructs a PaletteReducer that uses the given array of RGBA8888 ints as a palette (see {@link #exact(int[])}
     * for more info).
     *
     * @param rgbaPalette an array of RGBA8888 ints to use as a palette
     */
    public PaletteReducer(int[] rgbaPalette) {
        paletteMapping = new byte[0x8000];
        exact(rgbaPalette);
    }

    /**
     * Constructs a PaletteReducer that uses the given array of RGBA8888 ints as a palette (see {@link #exact(int[])}
     * for more info).
     *
     * @param rgbaPalette an array of RGBA8888 ints to use as a palette
     * @param metric      should usually be {@link #labQuickMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which handles gradients better
     */
    public PaletteReducer(int[] rgbaPalette, ColorMetric metric) {
        paletteMapping = new byte[0x8000];
        exact(rgbaPalette, metric);
    }

    /**
     * Constructs a PaletteReducer that uses the given array of Color objects as a palette (see {@link #exact(Color[])}
     * for more info).
     *
     * @param colorPalette an array of Color objects to use as a palette
     */
    public PaletteReducer(Color[] colorPalette) {
        paletteMapping = new byte[0x8000];
        exact(colorPalette);
    }

    /**
     * Constructs a PaletteReducer that uses the given Array of Color objects as a palette (see {@link #exact(Color[])}
     * for more info).
     *
     * @param colorPalette an array of Color objects to use as a palette
     */
    public PaletteReducer(Array<Color> colorPalette) {
        paletteMapping = new byte[0x8000];
        if (colorPalette != null)
            exact(colorPalette.items, colorPalette.size);
        else
            exact(Coloring.AURORA, ENCODED_AURORA);
    }

    /**
     * Constructs a PaletteReducer that analyzes the given Pixmap for color count and frequency to generate a palette
     * (see {@link #analyze(Pixmap)} for more info).
     *
     * @param pixmap a Pixmap to analyze in detail to produce a palette
     */
    public PaletteReducer(Pixmap pixmap) {
        paletteMapping = new byte[0x8000];
        analyze(pixmap);
    }
    /**
     * Constructs a PaletteReducer that uses the given array of RGBA8888 ints as a palette (see {@link #exact(int[])}
     * for more info) and an encoded String to use to look up pre-loaded color data.
     *
     * @param palette an array of RGBA8888 ints to use as a palette
     * @param preload an ISO-8859-1-encoded String containing preload data
     */
    public PaletteReducer(int[] palette, byte[] preload)
    {
        exact(palette, preload);
    }
    /**
     * Constructs a PaletteReducer that analyzes the given Pixmap for color count and frequency to generate a palette
     * (see {@link #analyze(Pixmap, int)} for more info).
     *
     * @param pixmap    a Pixmap to analyze in detail to produce a palette
     * @param threshold the minimum difference between colors required to put them in the palette (default 400)
     */
    public PaletteReducer(Pixmap pixmap, int threshold) {
        paletteMapping = new byte[0x8000];
        analyze(pixmap, threshold);
    }

    /**
     * Color difference metric; returns large numbers even for smallish differences.
     * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
     *
     * @param color1 an RGBA8888 color as an int
     * @param color2 an RGBA8888 color as an int
     * @return the difference between the given colors, as a positive double
     */
    public static double difference(int color1, int color2) {
        int indexA = (color1 >>> 17 & 0x7C00) | (color1 >>> 14 & 0x3E0) | (color1 >>> 11 & 0x1F),
                indexB = (color2 >>> 17 & 0x7C00) | (color2 >>> 14 & 0x3E0) | (color2 >>> 11 & 0x1F);
        final double
                L = labs[0][indexA] - labs[0][indexB],
                A = labs[1][indexA] - labs[1][indexB],
                B = labs[2][indexA] - labs[2][indexB];
        return (L * L * 7 + A * A + B * B);
    }


    /**
     * Color difference metric; returns large numbers even for smallish differences.
     * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
     *
     * @param color1 an RGBA8888 color as an int
     * @param r2     red value from 0 to 255, inclusive
     * @param g2     green value from 0 to 255, inclusive
     * @param b2     blue value from 0 to 255, inclusive
     * @return the difference between the given colors, as a positive double
     */
    public static double difference(int color1, int r2, int g2, int b2) {
        int indexA = (color1 >>> 17 & 0x7C00) | (color1 >>> 14 & 0x3E0) | (color1 >>> 11 & 0x1F),
                indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
        final double
                L = labs[0][indexA] - labs[0][indexB],
                A = labs[1][indexA] - labs[1][indexB],
                B = labs[2][indexA] - labs[2][indexB];
        return (L * L * 7 + A * A + B * B);
    }

    /**
     * Color difference metric; returns large numbers even for smallish differences.
     * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
     *
     * @param r1 red value from 0 to 255, inclusive
     * @param g1 green value from 0 to 255, inclusive
     * @param b1 blue value from 0 to 255, inclusive
     * @param r2 red value from 0 to 255, inclusive
     * @param g2 green value from 0 to 255, inclusive
     * @param b2 blue value from 0 to 255, inclusive
     * @return the difference between the given colors, as a positive double
     */
    public static double difference(final int r1, final int g1, final int b1, final int r2, final int g2, final int b2) {
            int indexA = (r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3),
                    indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
            final double
                    L = labs[0][indexA] - labs[0][indexB],
                    A = labs[1][indexA] - labs[1][indexB],
                    B = labs[2][indexA] - labs[2][indexB];
            return (L * L * 7 + A * A + B * B);
        }

    /**
     * Gets a pseudo-random float between -0.65625f and 0.65625f, determined by the upper 23 bits of seed.
     * This currently uses a uniform distribution for its output, but earlier versions intentionally used a non-uniform
     * one; a non-uniform distribution can sometimes work well but is very dependent on how error propagates through a
     * dithered image, and in bad cases can produce bands of bright mistakenly-error-adjusted colors.
     * @param seed any int, but only the most-significant 23 bits will be used
     * @return a float between -0.65625f and 0.65625f, with fairly uniform distribution as long as seed is uniform
     */
    static float randomXi(int seed)
    {
        return ((seed >> 9) * 0x1.5p-23f);
//        return NumberUtils.intBitsToFloat((seed & 0x7FFFFF & ((seed >>> 11 & 0x400000)|0x3FFFFF)) | 0x3f800000) - 1.4f;
//        return NumberUtils.intBitsToFloat((seed & 0x7FFFFF & ((seed >>> 11 & 0x600000)|0x1FFFFF)) | 0x3f800000) - 1.3f;
    }

    /**
     * Builds the palette information this PNG8 stores from the RGBA8888 ints in {@code rgbaPalette}, up to 256 colors.
     * Alpha is not preserved except for the first item in rgbaPalette, and only if it is {@code 0} (fully transparent
     * black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, or only has one color, then
     * this defaults to DawnBringer's Aurora palette with 256 hand-chosen colors (including transparent).
     *
     * @param rgbaPalette an array of RGBA8888 ints; all will be used up to 256 items or the length of the array
     */
    public void exact(int[] rgbaPalette) {
        exact(rgbaPalette, rgbEasyMetric);
    }

    /**
     * Builds the palette information this PNG8 stores from the RGBA8888 ints in {@code rgbaPalette}, up to 256 colors.
     * Alpha is not preserved except for the first item in rgbaPalette, and only if it is {@code 0} (fully transparent
     * black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, or only has one color, then
     * this defaults to DawnBringer's Aurora palette with 256 hand-chosen colors (including transparent).
     *
     * @param rgbaPalette an array of RGBA8888 ints; all will be used up to 256 items or the length of the array
     */
    public void exact(int[] rgbaPalette, int limit) {
        exact(rgbaPalette, limit, rgbEasyMetric);
    }
    /**
     * Builds the palette information this PNG8 stores from the RGBA8888 ints in {@code rgbaPalette}, up to 256 colors.
     * Alpha is not preserved except for the first item in rgbaPalette, and only if it is {@code 0} (fully transparent
     * black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, or only has one color, then
     * this defaults to DawnBringer's Aurora palette with 256 hand-chosen colors (including transparent).
     *
     * @param rgbaPalette an array of RGBA8888 ints; all will be used up to 256 items or the length of the array
     * @param metric      should usually be {@link #labQuickMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which handles gradients better
     */
    public void exact(int[] rgbaPalette, ColorMetric metric) {
        exact(rgbaPalette, 256, metric);
    }
    /**
     * Builds the palette information this PNG8 stores from the RGBA8888 ints in {@code rgbaPalette}, up to 256 colors.
     * Alpha is not preserved except for the first item in rgbaPalette, and only if it is {@code 0} (fully transparent
     * black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, or only has one color, then
     * this defaults to DawnBringer's Aurora palette with 256 hand-chosen colors (including transparent).
     *
     * @param rgbaPalette an array of RGBA8888 ints; all will be used up to 256 items or the length of the array
     * @param metric      should usually be {@link #labQuickMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which handles gradients better
     */
    public void exact(int[] rgbaPalette, int limit, ColorMetric metric) {
        if (rgbaPalette == null || rgbaPalette.length < 2 || limit < 2) {
            exact(Coloring.AURORA, ENCODED_AURORA);
            return;
        }
        Arrays.fill(paletteArray, 0);
        Arrays.fill(paletteMapping, (byte) 0);
        final int plen = Math.min(Math.min(256, limit), rgbaPalette.length);
        int color, c2;
        double dist;
        for (int i = 0; i < plen; i++) {
            color = rgbaPalette[i];
            if ((color & 0x80) != 0) {
                paletteArray[i] = color;
                paletteMapping[(color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F)] = (byte) i;
            }
        }
        int rr, gg, bb;
        for (int r = 0; r < 32; r++) {
            rr = (r << 3 | r >>> 2);
            for (int g = 0; g < 32; g++) {
                gg = (g << 3 | g >>> 2);
                for (int b = 0; b < 32; b++) {
                    c2 = r << 10 | g << 5 | b;
                    if (paletteMapping[c2] == 0) {
                        bb = (b << 3 | b >>> 2);
                        dist = 0x7FFFFFFF;
                        for (int i = 1; i < plen; i++) {
                            if (dist > (dist = Math.min(dist, metric.difference(paletteArray[i], rr, gg, bb))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }
//        generatePreloadCode(paletteMapping);
    }

    /**
     * Builds the palette information this PaletteReducer stores from the given array of RGBA8888 ints as a palette (see
     * {@link #exact(int[])} for more info) and an encoded String to use to look up pre-loaded color data. The encoded
     * string is going to be hard to produce if you intend to do this from outside ColorWeaver, but there is a
     * generatePreloadCode() method in ColorWeaver's tests. For external code, there's slightly
     * more startup time spent when initially calling {@link #exact(int[])}, but it will produce the same result. 
     *
     * @param palette an array of RGBA8888 ints to use as a palette
     * @param preload an ISO-8859-1-encoded String containing preload data
     */
    public void exact(int[] palette, byte[] preload)
    {
        for (int i = 0; i < 256 & i < palette.length; i++) {
            int color = palette[i];
            if((color & 0x80) != 0)
                paletteArray[i] = color;
        }         
        paletteMapping = preload;
    }

    /**
     * Builds the palette information this PaletteReducer stores from the Color objects in {@code colorPalette}, up to
     * 256 colors.
     * Alpha is not preserved except for the first item in colorPalette, and only if its r, g, b, and a values are all
     * 0f (fully transparent black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, or only
     * has one color, then this defaults to DawnBringer's Aurora palette with 256 hand-chosen colors (including
     * transparent).
     *
     * @param colorPalette an array of Color objects; all will be used up to 256 items or the length of the array
     */
    public void exact(Color[] colorPalette) {
        exact(colorPalette, 256, rgbEasyMetric);
    }

    /**
     * Builds the palette information this PaletteReducer stores from the Color objects in {@code colorPalette}, up to
     * 256 colors.
     * Alpha is not preserved except for the first item in colorPalette, and only if its r, g, b, and a values are all
     * 0f (fully transparent black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, or only
     * has one color, then this defaults to DawnBringer's Aurora palette with 256 hand-chosen colors (including
     * transparent).
     *
     * @param colorPalette an array of Color objects; all will be used up to 256 items or the length of the array
     * @param metric      should usually be {@link #labQuickMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which handles gradients better
     */
    public void exact(Color[] colorPalette, ColorMetric metric) {
        exact(colorPalette, 256, metric);
    }

    /**
     * Builds the palette information this PaletteReducer stores from the Color objects in {@code colorPalette}, up to
     * 256 colors.
     * Alpha is not preserved except for the first item in colorPalette, and only if its r, g, b, and a values are all
     * 0f (fully transparent black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, only has
     * one color, or limit is less than 2, then this defaults to DawnBringer's Aurora palette with 256 hand-chosen
     * colors (including transparent).
     *
     * @param colorPalette an array of Color objects; all will be used up to 256 items, limit, or the length of the array
     * @param limit        a limit on how many Color items to use from colorPalette; useful if colorPalette is from an Array
     */
    public void exact(Color[] colorPalette, int limit) {
        exact(colorPalette, limit, rgbEasyMetric);
    }

    /**
     * Builds the palette information this PaletteReducer stores from the Color objects in {@code colorPalette}, up to
     * 256 colors.
     * Alpha is not preserved except for the first item in colorPalette, and only if its r, g, b, and a values are all
     * 0f (fully transparent black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, only has
     * one color, or limit is less than 2, then this defaults to DawnBringer's Aurora palette with 256 hand-chosen
     * colors (including transparent).
     *
     * @param colorPalette an array of Color objects; all will be used up to 256 items, limit, or the length of the array
     * @param limit        a limit on how many Color items to use from colorPalette; useful if colorPalette is from an Array
     * @param metric      should usually be {@link #labQuickMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which handles gradients better
     */
    public void exact(Color[] colorPalette, int limit, ColorMetric metric) {
        if (colorPalette == null || colorPalette.length < 2 || limit < 2) {
            exact(Coloring.AURORA, ENCODED_AURORA);
            return;
        }
        Arrays.fill(paletteArray, 0);
        Arrays.fill(paletteMapping, (byte) 0);
        final int plen = Math.min(Math.min(256, colorPalette.length), limit);
        int color, c2;
        double dist;
        for (int i = 0; i < plen; i++) {
            color = Color.rgba8888(colorPalette[i]);
            paletteArray[i] = color;
            paletteMapping[(color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F)] = (byte) i;
        }
        int rr, gg, bb;
        for (int r = 0; r < 32; r++) {
            rr = (r << 3 | r >>> 2);
            for (int g = 0; g < 32; g++) {
                gg = (g << 3 | g >>> 2);
                for (int b = 0; b < 32; b++) {
                    c2 = r << 10 | g << 5 | b;
                    if (paletteMapping[c2] == 0) {
                        bb = (b << 3 | b >>> 2);
                        dist = 0x7FFFFFFF;
                        for (int i = 1; i < plen; i++) {
                            if (dist > (dist = Math.min(dist, metric.difference(paletteArray[i], rr, gg, bb))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }
    }
    /**
     * Analyzes {@code pixmap} for color count and frequency, building a palette with at most 256 colors if there are
     * too many colors to store in a PNG-8 palette. If there are 256 or less colors, this uses the exact colors
     * (although with at most one transparent color, and no alpha for other colors); if there are more than 256 colors
     * or any colors have 50% or less alpha, it will reserve a palette entry for transparent (even if the image has no
     * transparency). Because calling {@link #reduce(Pixmap)} (or any of PNG8's write methods) will dither colors that
     * aren't exact, and dithering works better when the palette can choose colors that are sufficiently different, this
     * uses a threshold value to determine whether it should permit a less-common color into the palette, and if the
     * second color is different enough (as measured by {@link #difference(int, int)}) by a value of at least 400, it is
     * allowed in the palette, otherwise it is kept out for being too similar to existing colors. This doesn't return a
     * value but instead stores the palette info in this object; a PaletteReducer can be assigned to the
     * {@link PNG8#palette} field or can be used directly to {@link #reduce(Pixmap)} a Pixmap.
     *
     * @param pixmap a Pixmap to analyze, making a palette which can be used by this to {@link #reduce(Pixmap)} or by PNG8
     */
    public void analyze(Pixmap pixmap) {
        analyze(pixmap, 400);
    }

    private static final Comparator<IntIntMap.Entry> entryComparator = new Comparator<IntIntMap.Entry>() {
        @Override
        public int compare(IntIntMap.Entry o1, IntIntMap.Entry o2) {
            return o2.value - o1.value;
        }
    };


    /**
     * Analyzes {@code pixmap} for color count and frequency, building a palette with at most 256 colors if there are
     * too many colors to store in a PNG-8 palette. If there are 256 or less colors, this uses the exact colors
     * (although with at most one transparent color, and no alpha for other colors); if there are more than 256 colors
     * or any colors have 50% or less alpha, it will reserve a palette entry for transparent (even if the image has no
     * transparency). Because calling {@link #reduce(Pixmap)} (or any of PNG8's write methods) will dither colors that
     * aren't exact, and dithering works better when the palette can choose colors that are sufficiently different, this
     * takes a threshold value to determine whether it should permit a less-common color into the palette, and if the
     * second color is different enough (as measured by {@link #difference(int, int)}) by a value of at least
     * {@code threshold}, it is allowed in the palette, otherwise it is kept out for being too similar to existing
     * colors. The threshold is usually between 250 and 1000, and 400 is a good default. This doesn't return a value but
     * instead stores the palette info in this object; a PaletteReducer can be assigned to the {@link PNG8#palette}
     * field or can be used directly to {@link #reduce(Pixmap)} a Pixmap.
     *
     * @param pixmap    a Pixmap to analyze, making a palette which can be used by this to {@link #reduce(Pixmap)} or by PNG8
     * @param threshold a minimum color difference as produced by {@link #difference(int, int)}; usually between 250 and 1000, 400 is a good default
     */
    public void analyze(Pixmap pixmap, int threshold) {
        analyze(pixmap, threshold, 256);
    }
    /**
     * Analyzes {@code pixmap} for color count and frequency, building a palette with at most 256 colors if there are
     * too many colors to store in a PNG-8 palette. If there are 256 or less colors, this uses the exact colors
     * (although with at most one transparent color, and no alpha for other colors); if there are more than 256 colors
     * or any colors have 50% or less alpha, it will reserve a palette entry for transparent (even if the image has no
     * transparency). Because calling {@link #reduce(Pixmap)} (or any of PNG8's write methods) will dither colors that
     * aren't exact, and dithering works better when the palette can choose colors that are sufficiently different, this
     * takes a threshold value to determine whether it should permit a less-common color into the palette, and if the
     * second color is different enough (as measured by {@link #difference(int, int)}) by a value of at least
     * {@code threshold}, it is allowed in the palette, otherwise it is kept out for being too similar to existing
     * colors. The threshold is usually between 250 and 1000, and 400 is a good default. This doesn't return a value but
     * instead stores the palette info in this object; a PaletteReducer can be assigned to the {@link PNG8#palette}
     * field or can be used directly to {@link #reduce(Pixmap)} a Pixmap.
     *
     * @param pixmap    a Pixmap to analyze, making a palette which can be used by this to {@link #reduce(Pixmap)} or by PNG8
     * @param threshold a minimum color difference as produced by {@link #difference(int, int)}; usually between 250 and 1000, 400 is a good default
     */
    public void analyze(Pixmap pixmap, int threshold, int limit) {
        Arrays.fill(paletteArray, 0);
        Arrays.fill(paletteMapping, (byte) 0);
        int color;
        final int width = pixmap.getWidth(), height = pixmap.getHeight();
        IntIntMap counts = new IntIntMap(limit);
        int hasTransparent = 0;
        int[] reds = new int[limit], greens = new int[limit], blues = new int[limit];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                color = pixmap.getPixel(x, y);
                if ((color & 0x80) != 0) {
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    counts.getAndIncrement(color, 0, 1);
                } else {
                    hasTransparent = 1;
                }
            }
        }
        final int cs = counts.size;
        ArrayList<IntIntMap.Entry> es = new ArrayList<>(cs);
        for(IntIntMap.Entry e : counts)
        {
            IntIntMap.Entry e2 = new IntIntMap.Entry();
            e2.key = e.key;
            e2.value = e.value;
            es.add(e2);
        }
        Collections.sort(es, entryComparator);
        if (cs + hasTransparent <= limit) {
            int i = hasTransparent;
            for(IntIntMap.Entry e : es) {
                color = e.key;
                paletteArray[i] = color;
                color = (color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F);
                paletteMapping[color] = (byte) i;
                reds[i] = color >>> 10;
                greens[i] = color >>> 5 & 31;
                blues[i] = color & 31;
                i++;
            }
        } else // reduce color count
        {
            int i = 1, c = 0;
            PER_BEST:
            for (; i < limit && c < cs;) {
                color = es.get(c++).key;
                for (int j = 1; j < i; j++) {
                    if (difference(color, paletteArray[j]) < threshold)
                        continue PER_BEST;
                }
                paletteArray[i] = color;
                color = (color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F);
                paletteMapping[color] = (byte) i;
                reds[i] = color >>> 10;
                greens[i] = color >>> 5 & 31;
                blues[i] = color & 31;
                i++;
            }
        }
        int c2;
        double dist;
        for (int r = 0; r < 32; r++) {
            for (int g = 0; g < 32; g++) {
                for (int b = 0; b < 32; b++) {
                    c2 = r << 10 | g << 5 | b;
                    if (paletteMapping[c2] == 0) {
                        dist = Double.POSITIVE_INFINITY;
                        for (int i = 1; i < limit; i++) {
                            if (dist > (dist = Math.min(dist, difference(reds[i], greens[i], blues[i], r, g, b))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }
    }

    /**
     * Changes the "strength" of the dither effect applied during {@link #reduce(Pixmap)} calls. The default is 1f,
     * and while both values higher than 1f and lower than 1f are valid, they should not be negative. If you want dither
     * to be eliminated, don't set dither strength to 0; use {@link #reduceSolid(Pixmap)} instead of reduce().
     * @param ditherStrength dither strength as a non-negative float that should be close to 1f
     */
    public void setDitherStrength(float ditherStrength) {
        this.ditherStrength = 0.5f * ditherStrength;
        this.halfDitherStrength = 0.25f * ditherStrength;
    }

    /**
     * Modifies the given Pixmap so it only uses colors present in this PaletteReducer, dithering when it can
     * using Floyd-Steinberg (this merely delegates to {@link #reduceFloydSteinberg(Pixmap)}).
     * If you want to reduce the colors in a Pixmap based on what it currently contains, call
     * {@link #analyze(Pixmap)} with {@code pixmap} as its argument, then call this method with the same
     * Pixmap. You may instead want to use a known palette instead of one computed from a Pixmap;
     * {@link #exact(int[])} is the tool for that job.
     * <p>
     * This method is not incredibly fast because of the extra calculations it has to do for dithering, but if you can
     * compute the PaletteReducer once and reuse it, that will save some time.
     * @param pixmap a Pixmap that will be modified in place
     * @return the given Pixmap, for chaining
     */
    public Pixmap reduce (Pixmap pixmap) {
        return reduceFloydSteinberg(pixmap);
    }

    /**
     * Modifies the given Pixmap so it only uses colors present in this PaletteReducer, without dithering. This produces
     * blocky solid sections of color in most images where the palette isn't exact, instead of checkerboard-like
     * dithering patterns. If you want to reduce the colors in a Pixmap based on what it currently contains, call
     * {@link #analyze(Pixmap)} with {@code pixmap} as its argument, then call this method with the same
     * Pixmap. You may instead want to use a known palette instead of one computed from a Pixmap;
     * {@link #exact(int[])} is the tool for that job.
     * @param pixmap a Pixmap that will be modified in place
     * @return the given Pixmap, for chaining
     */
    public Pixmap reduceSolid (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    pixmap.drawPixel(px, y, paletteArray[
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    /**
     * Modifies the given Pixmap so it only uses colors present in this PaletteReducer, dithering when it can using
     * Burkes dithering instead of the Floyd-Steinberg dithering that {@link #reduce(Pixmap)} uses.
     * If you want to reduce the colors in a Pixmap based on what it currently contains, call
     * {@link #analyze(Pixmap)} with {@code pixmap} as its argument, then call this method with the same
     * Pixmap. You may instead want to use a known palette instead of one computed from a Pixmap;
     * {@link #exact(int[])} is the tool for that job.
     * <p>
     * This method is not incredibly fast because of the extra calculations it has to do for dithering, but if you can
     * compute the PaletteReducer once and reuse it, that will save some time. Burkes dithering probably takes about as
     * much time as Floyd-Steinberg, and has pretty close quality.
     * @param pixmap a Pixmap that will be modified in place
     * @return the given Pixmap, for chaining
     */
    public Pixmap reduceBurkes (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float r4, r2, r1, g4, g2, g1, b4, b2, b1;
        byte[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedBytes == null) {
            curErrorRed = (curErrorRedBytes = new ByteArray(lineLen)).items;
            nextErrorRed = (nextErrorRedBytes = new ByteArray(lineLen)).items;
            curErrorGreen = (curErrorGreenBytes = new ByteArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenBytes = new ByteArray(lineLen)).items;
            curErrorBlue = (curErrorBlueBytes = new ByteArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueBytes = new ByteArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedBytes.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedBytes.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenBytes.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenBytes.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueBytes.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueBytes.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }

        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, rdiff, gdiff, bdiff;
        byte er, eg, eb, paletteIndex;
        for (int y = 0; y < h; y++) {
            int ny = y + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp(((color >>> 24)       ) + (er), 0, 0xFF);
                    int gg = MathUtils.clamp(((color >>> 16) & 0xFF) + (eg), 0, 0xFF);
                    int bb = MathUtils.clamp(((color >>> 8)  & 0xFF) + (eb), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = (color>>>24)-    (used>>>24);
                    gdiff = (color>>>16&255)-(used>>>16&255);
                    bdiff = (color>>>8&255)- (used>>>8&255);
                    r4 = rdiff * halfDitherStrength;
                    g4 = gdiff * halfDitherStrength;
                    b4 = bdiff * halfDitherStrength;
                    r2 = r4 * 0.5f;
                    g2 = g4 * 0.5f;
                    b2 = b4 * 0.5f;
                    r1 = r4 * 0.25f;
                    g1 = g4 * 0.25f;
                    b1 = b4 * 0.25f;
                    if(px < lineLen - 1)
                    {
                        curErrorRed[px+1]   += r4;
                        curErrorGreen[px+1] += g4;
                        curErrorBlue[px+1]  += b4;
                        if(px < lineLen - 2)
                        {

                            curErrorRed[px+2]   += r2;
                            curErrorGreen[px+2] += g2;
                            curErrorBlue[px+2]  += b2;
                        }
                    }
                    if(ny < h)
                    {
                        if(px > 0)
                        {
                            nextErrorRed[px-1]   += r2;
                            nextErrorGreen[px-1] += g2;
                            nextErrorBlue[px-1]  += b2;
                            if(px > 1)
                            {
                                nextErrorRed[px-2]   += r1;
                                nextErrorGreen[px-2] += g1;
                                nextErrorBlue[px-2]  += b1;
                            }
                        }
                        nextErrorRed[px]   += r4;
                        nextErrorGreen[px] += g4;
                        nextErrorBlue[px]  += b4;
                        if(px < lineLen - 1)
                        {
                            nextErrorRed[px+1]   += r2;
                            nextErrorGreen[px+1] += g2;
                            nextErrorBlue[px+1]  += b2;
                            if(px < lineLen - 2)
                            {

                                nextErrorRed[px+2]   += r1;
                                nextErrorGreen[px+2] += g1;
                                nextErrorBlue[px+2]  += b1;
                            }
                        }
                    }
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    /**
     * Modifies the given Pixmap so it only uses colors present in this PaletteReducer, dithering when it can using a
     * modified version of the algorithm presented in "Simple gradient-based error-diffusion method" by Xaingyu Y. Hu in
     * the Journal of Electronic Imaging, 2016. This algorithm uses pseudo-randomly-generated noise to adjust
     * Floyd-Steinberg dithering, with input for the pseudo-random state obtained by the non-transparent color values as
     * they are encountered. Very oddly, this tends to produce less random-seeming dither than
     * {@link #reduceBurkes(Pixmap)}, with this method often returning regular checkerboards where Burkes may produce
     * splotches of color. If you want to reduce the colors in a Pixmap based on what it currently contains, call
     * {@link #analyze(Pixmap)} with {@code pixmap} as its argument, then call this method with the same
     * Pixmap. You may instead want to use a known palette instead of one computed from a Pixmap;
     * {@link #exact(int[])} is the tool for that job.
     * <p>
     * This method is not incredibly fast because of the extra calculations it has to do for dithering, but if you can
     * compute the PaletteReducer once and reuse it, that will save some time. This method is probably slower than
     * {@link #reduceBurkes(Pixmap)} even though Burkes propagates error to more pixels, because this method also has to
     * generate two random values per non-transparent pixel. The random number "algorithm" this uses isn't very good
     * because it doesn't have to be good, it should just be fast and avoid clear artifacts; it's similar to one of
     * <a href="http://www.drdobbs.com/tools/fast-high-quality-parallel-random-number/231000484?pgno=2">Mark Overton's
     * subcycle generators</a> (which are usually paired, but that isn't the case here), but because it's
     * constantly being adjusted by additional colors as input, it may be more comparable to a rolling hash. This uses
     * {@link #randomXi(int)} to get the parameter in Hu's paper that's marked as {@code aξ}, but our randomXi() is
     * adjusted so it has half the range (from -0.5 to 0.5 instead of -1 to 1). That quirk ends up getting rather high
     * quality for this method, though it may have some grainy appearance in certain zones with mid-level intensity (an
     * acknowledged issue with the type of noise-based approach Hu uses, and not a very severe problem).
     * @param pixmap a Pixmap that will be modified in place
     * @return the given Pixmap, for chaining
     */
    public Pixmap reduceWithNoise (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        byte[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedBytes == null) {
            curErrorRed = (curErrorRedBytes = new ByteArray(lineLen)).items;
            nextErrorRed = (nextErrorRedBytes = new ByteArray(lineLen)).items;
            curErrorGreen = (curErrorGreenBytes = new ByteArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenBytes = new ByteArray(lineLen)).items;
            curErrorBlue = (curErrorBlueBytes = new ByteArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueBytes = new ByteArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedBytes.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedBytes.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenBytes.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenBytes.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueBytes.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueBytes.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }

        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, rdiff, gdiff, bdiff, state = 0xFEEDBEEF;
        byte er, eg, eb, paletteIndex;
        //float xir1, xir2, xig1, xig2, xib1, xib2, // would be used if random factors were per-channel
        // used now, where random factors are determined by whole colors as ints
        float xi1, xi2, w1 = ditherStrength * 0.125f, w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f;
        for (int y = 0; y < h; y++) {
            int ny = y + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp(((color >>> 24)       ) + (er), 0, 0xFF);
                    int gg = MathUtils.clamp(((color >>> 16) & 0xFF) + (eg), 0, 0xFF);
                    int bb = MathUtils.clamp(((color >>> 8)  & 0xFF) + (eb), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = (color>>>24)-    (used>>>24);
                    gdiff = (color>>>16&255)-(used>>>16&255);
                    bdiff = (color>>>8&255)- (used>>>8&255);
                    state += (color + 0x41C64E6D) ^ color >>> 7;
                    state = (state << 21 | state >>> 11);
                    xi1 = randomXi(state);
                    state ^= (state << 5 | state >>> 27) + 0x9E3779B9;
                    xi2 = randomXi(state);

//                    state += rdiff ^ rdiff << 9;
//                    state = (state << 21 | state >>> 11);
//                    xir1 = randomXi(state);
//                    state = (state << 21 | state >>> 11);
//                    xir2 = randomXi(state);
//                    state += gdiff ^ gdiff << 9;
//                    state = (state << 21 | state >>> 11);
//                    xig1 = randomXi(state);
//                    state = (state << 21 | state >>> 11);
//                    xig2 = randomXi(state);
//                    state += bdiff ^ bdiff << 9;
//                    state = (state << 21 | state >>> 11);
//                    xib1 = randomXi(state);
//                    state = (state << 21 | state >>> 11);
//                    xib2 = randomXi(state);
                    if(px < lineLen - 1)
                    {
                        curErrorRed[px+1]   += rdiff * w7 * (1f + xi1);
                        curErrorGreen[px+1] += gdiff * w7 * (1f + xi1);
                        curErrorBlue[px+1]  += bdiff * w7 * (1f + xi1);
                    }
                    if(ny < h)
                    {
                        if(px > 0)
                        {
                            nextErrorRed[px-1]   += rdiff * w3 * (1f + xi2);
                            nextErrorGreen[px-1] += gdiff * w3 * (1f + xi2);
                            nextErrorBlue[px-1]  += bdiff * w3 * (1f + xi2);
                        }
                        if(px < lineLen - 1)
                        {
                            nextErrorRed[px+1]   += rdiff * w1 * (1f - xi2);
                            nextErrorGreen[px+1] += gdiff * w1 * (1f - xi2);
                            nextErrorBlue[px+1]  += bdiff * w1 * (1f - xi2);
                        }
                        nextErrorRed[px]   += rdiff * w5 * (1f - xi1);
                        nextErrorGreen[px] += gdiff * w5 * (1f - xi1);
                        nextErrorBlue[px]  += bdiff * w5 * (1f - xi1);
                    }
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }
    /**
     * Modifies the given Pixmap so it only uses colors present in this PaletteReducer, dithering when it can using
     * Sierra Lite dithering instead of the Floyd-Steinberg dithering that {@link #reduce(Pixmap)} uses.
     * If you want to reduce the colors in a Pixmap based on what it currently contains, call
     * {@link #analyze(Pixmap)} with {@code pixmap} as its argument, then call this method with the same
     * Pixmap. You may instead want to use a known palette instead of one computed from a Pixmap;
     * {@link #exact(int[])} is the tool for that job.
     * <p>
     * This method is meant to be a little faster than Floyd-Steinberg, but the quality isn't quite as good sometimes.
     * @param pixmap a Pixmap that will be modified in place
     * @return the given Pixmap, for chaining
     */
    public Pixmap reduceSierraLite (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        byte[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedBytes == null) {
            curErrorRed = (curErrorRedBytes = new ByteArray(lineLen)).items;
            nextErrorRed = (nextErrorRedBytes = new ByteArray(lineLen)).items;
            curErrorGreen = (curErrorGreenBytes = new ByteArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenBytes = new ByteArray(lineLen)).items;
            curErrorBlue = (curErrorBlueBytes = new ByteArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueBytes = new ByteArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedBytes.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedBytes.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenBytes.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenBytes.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueBytes.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueBytes.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }

        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, rdiff, gdiff, bdiff;
        byte er, eg, eb, paletteIndex;
        for (int y = 0; y < h; y++) {
            int ny = y + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp(((color >>> 24)       ) + (er), 0, 0xFF);
                    int gg = MathUtils.clamp(((color >>> 16) & 0xFF) + (eg), 0, 0xFF);
                    int bb = MathUtils.clamp(((color >>> 8)  & 0xFF) + (eb), 0, 0xFF);
                    paletteIndex =
                        paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = (color>>>24)-    (used>>>24);
                    gdiff = (color>>>16&255)-(used>>>16&255);
                    bdiff = (color>>>8&255)- (used>>>8&255);
                    if(px < lineLen - 1)
                    {
                        curErrorRed[px+1]   += rdiff * ditherStrength;
                        curErrorGreen[px+1] += gdiff * ditherStrength;
                        curErrorBlue[px+1]  += bdiff * ditherStrength;
                    }
                    if(ny < h)
                    {
                        if(px > 0)
                        {
                            nextErrorRed[px-1]   += rdiff * halfDitherStrength;
                            nextErrorGreen[px-1] += gdiff * halfDitherStrength;
                            nextErrorBlue[px-1]  += bdiff * halfDitherStrength;
                        }
                        nextErrorRed[px]   += rdiff * halfDitherStrength;
                        nextErrorGreen[px] += gdiff * halfDitherStrength;
                        nextErrorBlue[px]  += bdiff * halfDitherStrength;
                    }
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    /**
     * Modifies the given Pixmap so it only uses colors present in this PaletteReducer, dithering when it can using the
     * commonly-used Floyd-Steinberg dithering. If you want to reduce the colors in a Pixmap based on what it currently
     * contains, call {@link #analyze(Pixmap)} with {@code pixmap} as its argument, then call this method with the same
     * Pixmap. You may instead want to use a known palette instead of one computed from a Pixmap;
     * {@link #exact(int[])} is the tool for that job.
     * <p>
     * This method is not incredibly fast because of the extra calculations it has to do for dithering, but if you can
     * compute the PaletteReducer once and reuse it, that will save some time. This method is probably about the same
     * speed as {@link #reduceBurkes(Pixmap)}.
     * @param pixmap a Pixmap that will be modified in place
     * @return the given Pixmap, for chaining
     */
    public Pixmap reduceFloydSteinberg (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        byte[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedBytes == null) {
            curErrorRed = (curErrorRedBytes = new ByteArray(lineLen)).items;
            nextErrorRed = (nextErrorRedBytes = new ByteArray(lineLen)).items;
            curErrorGreen = (curErrorGreenBytes = new ByteArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenBytes = new ByteArray(lineLen)).items;
            curErrorBlue = (curErrorBlueBytes = new ByteArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueBytes = new ByteArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedBytes.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedBytes.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenBytes.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenBytes.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueBytes.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueBytes.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }

        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, rdiff, gdiff, bdiff;
        byte er, eg, eb, paletteIndex;
        float w1 = ditherStrength * 0.125f, w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f;
        for (int y = 0; y < h; y++) {
            int ny = y + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp(((color >>> 24)       ) + (er), 0, 0xFF);
                    int gg = MathUtils.clamp(((color >>> 16) & 0xFF) + (eg), 0, 0xFF);
                    int bb = MathUtils.clamp(((color >>> 8)  & 0xFF) + (eb), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = (color>>>24)-    (used>>>24);
                    gdiff = (color>>>16&255)-(used>>>16&255);
                    bdiff = (color>>>8&255)- (used>>>8&255);
                    if(px < lineLen - 1)
                    {
                        curErrorRed[px+1]   += rdiff * w7;
                        curErrorGreen[px+1] += gdiff * w7;
                        curErrorBlue[px+1]  += bdiff * w7;
                    }
                    if(ny < h)
                    {
                        if(px > 0)
                        {
                            nextErrorRed[px-1]   += rdiff * w3;
                            nextErrorGreen[px-1] += gdiff * w3;
                            nextErrorBlue[px-1]  += bdiff * w3;
                        }
                        if(px < lineLen - 1)
                        {
                            nextErrorRed[px+1]   += rdiff * w1;
                            nextErrorGreen[px+1] += gdiff * w1;
                            nextErrorBlue[px+1]  += bdiff * w1;
                        }
                        nextErrorRed[px]   += rdiff * w5;
                        nextErrorGreen[px] += gdiff * w5;
                        nextErrorBlue[px]  += bdiff * w5;
                    }
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    public Pixmap reduceWithRoberts (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, adj;
        byte paletteIndex;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    adj = (int)((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >> 57) * ditherStrength);
                    adj ^= adj >> 31;
                    //adj = (-(adj >>> 4 & 1) ^ adj) & 7;
                    adj -= 32 * ditherStrength;
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp(((color >>> 24)       ) + (adj), 0, 0xFF);
                    int gg = MathUtils.clamp(((color >>> 16) & 0xFF) + (adj), 0, 0xFF);
                    int bb = MathUtils.clamp(((color >>> 8)  & 0xFF) + (adj), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    public Pixmap reduceRobertsMul (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float adj, str = ditherStrength * (256f / paletteArray.length) * 0x2.5p-27f;
        long pos;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
//                    adj = (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >> 40) * 0x1.Fp-26f) * ditherStrength) + 1f;
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
//                    int rr = MathUtils.clamp((int) (((color >>> 24)       ) * adj), 0, 0xFF);
//                    int gg = MathUtils.clamp((int) (((color >>> 16) & 0xFF) * adj), 0, 0xFF);
//                    int bb = MathUtils.clamp((int) (((color >>> 8)  & 0xFF) * adj), 0, 0xFF);
                    //0xD1B54A32D192ED03L, 0xABC98388FB8FAC03L, 0x8CB92BA72F3D8DD7L
//                    adj = (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 40) * str);
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = ((color >>> 24)       );//MathUtils.clamp((int) (rr * (1f + adj)), 0, 0xFF);
                    int gg = ((color >>> 16) & 0xFF);//MathUtils.clamp((int) (gg * (1f + adj)), 0, 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);//MathUtils.clamp((int) (bb * (1f + adj)), 0, 0xFF);
                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF];
                    pos = (px * 0xC13FA9A902A6328FL - y * 0x91E10DA5C79E7B1DL);
                    pos ^= pos >>> 1;
                    adj = ((pos >> 40) * str);
                    rr = MathUtils.clamp((int) (rr * (1f + adj * ((used >>> 24) - rr >> 3))), 0, 0xFF);
                    gg = MathUtils.clamp((int) (gg * (1f + adj * ((used >>> 16 & 0xFF) - gg >> 3))), 0, 0xFF);
                    bb = MathUtils.clamp((int) (bb * (1f + adj * ((used >>> 8 & 0xFF) - bb >> 3))), 0, 0xFF);
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    public Pixmap reduceRobertsEdit (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        int pos;
        float adj, str = -0x3.Fp-20f * ditherStrength;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
//                    adj = (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >> 40) * 0x1.Fp-26f) * ditherStrength) + 1f;
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
//                    int rr = MathUtils.clamp((int) (((color >>> 24)       ) * adj), 0, 0xFF);
//                    int gg = MathUtils.clamp((int) (((color >>> 16) & 0xFF) * adj), 0, 0xFF);
//                    int bb = MathUtils.clamp((int) (((color >>> 8)  & 0xFF) * adj), 0, 0xFF);
                    //0xD1B54A32D192ED03L, 0xABC98388FB8FAC03L, 0x8CB92BA72F3D8DD7L
//                    adj = (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 40) * str);
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = ((color >>> 24)       );//MathUtils.clamp((int) (rr * (1f + adj)), 0, 0xFF);
                    int gg = ((color >>> 16) & 0xFF);//MathUtils.clamp((int) (gg * (1f + adj)), 0, 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);//MathUtils.clamp((int) (bb * (1f + adj)), 0, 0xFF);
                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF];
                    pos = (px * (0xC13FA9A9 + y) + y * (0x91E10DA5 + px));
                    pos += pos >>> 1 ^ pos >>> 3 ^ pos >>> 4;
                    //0xE60E2B722B53AEEBL, 0xCEBD76D9EDB6A8EFL, 0xB9C9AA3A51D00B65L, 0xA6F5777F6F88983FL, 0x9609C71EB7D03F7BL, 
                    //0x86D516E50B04AB1BL
//                    long pr = (px * 0xE60E2B722B53AEEBL - y * 0x86D516E50B04AB1BL),
//                         pg = (px * 0xCEBD76D9EDB6A8EFL + y * 0x9609C71EB7D03F7BL),
//                         pb = (y * 0xB9C9AA3A51D00B65L - px * 0xA6F5777F6F88983FL);
//                    str * ((pr ^ pr >>> 1 ^ pr >>> 3 ^ pr >>> 4) >> 40)
//                    str * ((pg ^ pg >>> 1 ^ pg >>> 3 ^ pg >>> 4) >> 40)
//                    str * ((pb ^ pb >>> 1 ^ pb >>> 3 ^ pb >>> 4) >> 40)
                    //(px + y) * 1.6180339887498949f
                    adj = (pos >> 12) * str;
                    //adj = adj * ditherStrength; //(adj * adj * adj + 0x5p-6f)
                    // + NumberTools.sway(y * 0.7548776662466927f + px * 0.5698402909980532f) * 0.0625f;
                    rr = MathUtils.clamp((int) (rr + (adj * (((used >>> 24) - rr)))), 0, 0xFF); //  * 17 >> 4
                    gg = MathUtils.clamp((int) (gg + (adj * (((used >>> 16 & 0xFF) - gg)))), 0, 0xFF); //  * 23 >> 4
                    bb = MathUtils.clamp((int) (bb + (adj * (((used >>> 8 & 0xFF) - bb)))), 0, 0xFF); // * 5 >> 4
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }
    public Pixmap reduceShaderMimic (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float pos;
        double adj;
//        final float strength = 0x1.4p-10f * ditherStrength;
        final float strength = ditherStrength * 3.25f;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                        | ((gg << 2) & 0x3E0)
                        | ((bb >>> 3))] & 0xFF];
                    //float len = (rr * 5 + gg * 9 + bb * 2) * strength + 1f;
                    //adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) * len - len * 0.5;
                    //adj = asin(fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) * 0.875 
                    //         - fract(dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy)) * 0.5);
                    //adj = 2.0 * sin(fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) * 1.44 - 0.72);
                    pos = (px * 0.06711056f + y * 0.00583715f);
                    pos -= (int)pos;
                    pos *= 52.9829189f;
                    pos -= (int)pos;
                    adj = (Math.sqrt(pos) * pos - 0.3125) * strength;
//                    adj = TrigTools.sin(pos * 1.44f - 0.72f) * strength;
                    
//                    pos *= 0.875f;
//                    adj = (px * 0.7548776662466927f + y * 0.5698402909980532f);
//                    adj -= (int)adj;
//                    adj = TrigTools.asin((pos - adj * 0.3125f) * strength) * 1.25f;
                    rr = MathUtils.clamp((int) (rr + (adj * ((rr - (used >>> 24))))), 0, 0xFF);
                    gg = MathUtils.clamp((int) (gg + (adj * ((gg - (used >>> 16 & 0xFF))))), 0, 0xFF);
                    bb = MathUtils.clamp((int) (bb + (adj * ((bb - (used >>> 8 & 0xFF))))), 0, 0xFF);
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }
    /**
     * Inverse cosine function (arccos) but with output measured in turns instead of radians. Possible results for this
     * range from 0.0f (inclusive) to 0.5f (inclusive).
     * <br>
     * This method is extremely similar to the non-turn approximation.
     * @param n a float from -1.0f to 1.0f (both inclusive), usually the output of sin_() or cos_()
     * @return one of the values that would produce {@code n} if it were adjusted to 1/2pi range and passed to cos() 
     */
    private static float acos_(final float n)
    {
        final float ax = Math.abs(n), ay = (float) Math.sqrt(1f - n * n);
        if(ax < ay)
        {
            final float a = ax / ay, s = a * a,
                    r = 0.25f - (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a) * 0.15915494309189535f;
            return (n < 0.0f) ? 0.5f - r : r;
        }
        else {
            final float a = ay / ax, s = a * a,
                    r = (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a) * 0.15915494309189535f;
            return (n < 0.0f) ? 0.5f - r : r;
        }
    }

    /**
     * A blue-noise-based dither that uses a tiling 64x64 noise texture to add error to an image;
     * this does use an approximation of arccosine to bias results toward the original color.
     * <br>
     * There are times to use {@link #reduceBluish(Pixmap)} and times to use this; each palette and
     * source image will have different qualities of result.
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceTrueBlue (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float adj, strength = ditherStrength * 8;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                        | ((gg << 2) & 0x3E0)
                        | ((bb >>> 3))] & 0xFF];
                    adj = (acos_((BlueNoise.get(px, y) + 0.5f) * 0.00784313725490196f) - 0.25f) * strength;
                    rr = MathUtils.clamp((int) (rr + (adj * ((rr - (used >>> 24))))), 0, 0xFF);
                    gg = MathUtils.clamp((int) (gg + (adj * ((gg - (used >>> 16 & 0xFF))))), 0, 0xFF);
                    bb = MathUtils.clamp((int) (bb + (adj * ((bb - (used >>> 8 & 0xFF))))), 0, 0xFF);
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                        | ((gg << 2) & 0x3E0)
                        | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    /**
     * A different kind of blue-noise-based dither; does not diffuse error, and uses a non-repeating blue noise pattern
     * (that isn't quite as strongly measurable as blue noise as what {@link #reduceTrueBlue(Pixmap)} uses). This pattern
     * can be seeded to produce different dithers for otherwise identical inputs; see {@link #reduceBluish(Pixmap, int)}.
     * <br>
     * There are times to use {@link #reduceTrueBlue(Pixmap)} and times to use this; each palette and
     * source image will have different qualities of result.
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceBluish (Pixmap pixmap) {
        return reduceBluish(pixmap, 1111111);
    }
    /**
     * A different kind of blue-noise-based dither; does not diffuse error, and uses a non-repeating blue noise pattern
     * (that isn't quite as strongly measurable as blue noise as what {@link #reduceTrueBlue(Pixmap)} uses). This pattern
     * can be seeded to produce different dithers for otherwise identical inputs; the seed can be any int.
     * <br>
     * There are times to use {@link #reduceTrueBlue(Pixmap)} and times to use this; each palette and
     * source image will have different qualities of result.
     * @param pixmap will be modified in-place and returned
     * @param seed any int; will be used to change the dither pattern
     * @return pixmap, after modifications
     */
    public Pixmap reduceBluish (Pixmap pixmap, int seed) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        double adj, strength = ditherStrength;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                        | ((gg << 2) & 0x3E0)
                        | ((bb >>> 3))] & 0xFF];
                    adj = Math.cbrt((BlueNoise.get(px, y, BlueNoise.ALT_NOISE[1]) + 0.5f) * 0.00784313725490196f) * strength;
//                    adj = (BlueNoise.getSeeded(px, y, 1111111) + ((px + y & 1) - 0.3125f) * 32f) * strength;
                    rr = MathUtils.clamp((int) (rr + (adj * ((rr - (used >>> 24))))), 0, 0xFF);
                    gg = MathUtils.clamp((int) (gg + (adj * ((gg - (used >>> 16 & 0xFF))))), 0, 0xFF);
                    bb = MathUtils.clamp((int) (bb + (adj * ((bb - (used >>> 8 & 0xFF))))), 0, 0xFF);
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                        | ((gg << 2) & 0x3E0)
                        | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }
    
    void computePaletteGamma(double gamma){
        for (int i = 0; i < paletteArray.length; i++) {
            int color = paletteArray[i];
            double r = Math.pow((color >>> 24) / 255.0, gamma);
            double g = Math.pow((color >>> 16 & 0xFF) / 255.0, gamma);
            double b = Math.pow((color >>>  8 & 0xFF) / 255.0, gamma);
            int a = color & 0xFF;
            gammaArray[i] = (int)(r * 255.999) << 24 | (int)(g * 255.999) << 16 | (int)(b * 255.999) << 8 | a;
        }
    }

    /**
     * Given by Joel Yliluoma in <a href="https://bisqwit.iki.fi/story/howto/dither/jy/">a dithering article</a>.
     */
    private static final int[] thresholdMatrix = {
            0,  12,   3,  15,
            8,   4,  11,   7,
            2,  14,   1,  13,
            10,  6,   9,   5,
    };
    
    private final int[] candidates = new int[16];

    /**
     * Compares items in ints by their luma, looking up items by the indices a and b, and swaps the two given indices if
     * the item at a has higher luma than the item at b. This is protected rather than private because it's more likely
     * that this would be desirable to override than a method that uses it, like {@link #reduceKnoll(Pixmap)}. Uses
     * {@link #labs} to look up fairly-accurate luma for the given colors in {@code ints} (that contains RGBA8888 colors
     * while labs uses RGB555, so {@link #shrink(int)} is used to convert).
     * @param ints an int array than must be able to take a and b as indices; may be modified in place
     * @param a an index into ints
     * @param b an index into ints
     */
    protected void compareSwap(final int[] ints, final int a, final int b) {
        if(labs[0][shrink(ints[a])] > labs[0][shrink(ints[b])]) {
            final int t = ints[a];
            ints[a] = ints[b];
            ints[b] = t;
        }
    }

    /**
     * Sorting network, found by http://pages.ripco.net/~jgamble/nw.html , considered the best known for length 16.
     * @param i16 a 16-element array that will be sorted in-place by {@link #compareSwap(int[], int, int)}
     */
    private void sort16(final int[] i16)
    {
        compareSwap(i16, 0, 1);
        compareSwap(i16, 2, 3);
        compareSwap(i16, 4, 5);
        compareSwap(i16, 6, 7);
        compareSwap(i16, 8, 9);
        compareSwap(i16, 10, 11);
        compareSwap(i16, 12, 13);
        compareSwap(i16, 14, 15);
        compareSwap(i16, 0, 2);
        compareSwap(i16, 4, 6);
        compareSwap(i16, 8, 10);
        compareSwap(i16, 12, 14);
        compareSwap(i16, 1, 3);
        compareSwap(i16, 5, 7);
        compareSwap(i16, 9, 11);
        compareSwap(i16, 13, 15);
        compareSwap(i16, 0, 4);
        compareSwap(i16, 8, 12);
        compareSwap(i16, 1, 5);
        compareSwap(i16, 9, 13);
        compareSwap(i16, 2, 6);
        compareSwap(i16, 10, 14);
        compareSwap(i16, 3, 7);
        compareSwap(i16, 11, 15);
        compareSwap(i16, 0, 8);
        compareSwap(i16, 1, 9);
        compareSwap(i16, 2, 10);
        compareSwap(i16, 3, 11);
        compareSwap(i16, 4, 12);
        compareSwap(i16, 5, 13);
        compareSwap(i16, 6, 14);
        compareSwap(i16, 7, 15);
        compareSwap(i16, 5, 10);
        compareSwap(i16, 6, 9);
        compareSwap(i16, 3, 12);
        compareSwap(i16, 13, 14);
        compareSwap(i16, 7, 11);
        compareSwap(i16, 1, 2);
        compareSwap(i16, 4, 8);
        compareSwap(i16, 1, 4);
        compareSwap(i16, 7, 13);
        compareSwap(i16, 2, 8);
        compareSwap(i16, 11, 14);
        compareSwap(i16, 2, 4);
        compareSwap(i16, 5, 6);
        compareSwap(i16, 9, 10);
        compareSwap(i16, 11, 13);
        compareSwap(i16, 3, 8);
        compareSwap(i16, 7, 12);
        compareSwap(i16, 6, 8);
        compareSwap(i16, 10, 12);
        compareSwap(i16, 3, 5);
        compareSwap(i16, 7, 9);
        compareSwap(i16, 3, 4);
        compareSwap(i16, 5, 6);
        compareSwap(i16, 7, 8);
        compareSwap(i16, 9, 10);
        compareSwap(i16, 11, 12);
        compareSwap(i16, 6, 7);
        compareSwap(i16, 8, 9);
    }

    /**
     * Reduces a Pixmap to the palette this knows by using Thomas Knoll's pattern dither, which is out-of-patent since
     * late 2019. The output this produces is very dependent on the palette and this PaletteReducer's dither strength,
     * which can be set with {@link #setDitherStrength(float)}. At close-up zooms, a strong grid pattern will be visible
     * on most dithered output (like needlepoint). The algorithm was described in detail by Joel Yliluoma in
     * <a href="https://bisqwit.iki.fi/story/howto/dither/jy/">this dithering article</a>; Yliluoma used an 8x8
     * threshold matrix because at the time 4x4 was still covered by the patent, but using 4x4 allows a much faster
     * sorting step (this uses a sorting network, which works well for small input sizes like 16 items).
     * @see #reduceKnollRoberts(Pixmap) An alternative that uses a similar pattern but skews it to obscure the grid
     * @param pixmap a Pixmap that will be modified
     * @return {@code pixmap}, after modifications
     */
    public Pixmap reduceKnoll (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, cr, cg, cb, usedIndex;
        final float errorMul = ditherStrength * 0.5f;
        computePaletteGamma(2.0 - ditherStrength * 1.666);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int er = 0, eg = 0, eb = 0;
                    cr = (color >>> 24);
                    cg = (color >>> 16 & 0xFF);
                    cb = (color >>> 8 & 0xFF);
                    for (int i = 0; i < candidates.length; i++) {
                        int rr = MathUtils.clamp((int) (cr + er * errorMul), 0, 255);
                        int gg = MathUtils.clamp((int) (cg + eg * errorMul), 0, 255);
                        int bb = MathUtils.clamp((int) (cb + eb * errorMul), 0, 255);
                        usedIndex = paletteMapping[((rr << 7) & 0x7C00)
                                | ((gg << 2) & 0x3E0)
                                | ((bb >>> 3))] & 0xFF;
                        candidates[i] = paletteArray[usedIndex];
                        used = gammaArray[usedIndex];
                        er += cr - (used >>> 24);
                        eg += cg - (used >>> 16 & 0xFF);
                        eb += cb - (used >>> 8 & 0xFF);
                    }
                    sort16(candidates);
                    pixmap.drawPixel(px, y, candidates[thresholdMatrix[((px & 3) | (y & 3) << 2)]]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    /**
     * Reduces a Pixmap to the palette this knows by using a skewed version of Thomas Knoll's pattern dither, which is
     * out-of-patent since late 2019, using the harmonious numbers rediscovered by Martin Roberts to handle the skew.
     * The output this produces is very dependent on the palette and this PaletteReducer's dither strength, which can be
     * set with {@link #setDitherStrength(float)}. A diagonal striping can be visible on many outputs this produces;
     * this artifact can be mitigated by changing dither strength. The algorithm was described in detail by Joel
     * Yliluoma in <a href="https://bisqwit.iki.fi/story/howto/dither/jy/">this dithering article</a>; Yliluoma used an
     * 8x8 threshold matrix because at the time 4x4 was still covered by the patent, but using 4x4 allows a much faster
     * sorting step (this uses a sorting network, which works well for small input sizes like 16 items).
     * @see #reduceKnoll(Pixmap) An alternative that uses a similar pattern but has a more obvious grid
     * @param pixmap a Pixmap that will be modified
     * @return {@code pixmap}, after modifications
     */
    public Pixmap reduceKnollRoberts (Pixmap pixmap) { 
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, cr, cg, cb, usedIndex;
        final float errorMul = ditherStrength * 0.3f;
        computePaletteGamma(2.0 - ditherStrength * 1.666);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int er = 0, eg = 0, eb = 0;
                    cr = (color >>> 24);
                    cg = (color >>> 16 & 0xFF);
                    cb = (color >>> 8 & 0xFF);
                    for (int i = 0; i < candidates.length; i++) {
                        int rr = MathUtils.clamp((int) (cr + er * errorMul), 0, 255);
                        int gg = MathUtils.clamp((int) (cg + eg * errorMul), 0, 255);
                        int bb = MathUtils.clamp((int) (cb + eb * errorMul), 0, 255);
                        usedIndex = paletteMapping[((rr << 7) & 0x7C00)
                                | ((gg << 2) & 0x3E0)
                                | ((bb >>> 3))] & 0xFF;
                        candidates[i] = paletteArray[usedIndex];
                        used = gammaArray[usedIndex];
                        er += cr - (used >>> 24);
                        eg += cg - (used >>> 16 & 0xFF);
                        eb += cb - (used >>> 8 & 0xFF);
                    }
                    sort16(candidates);
                    pixmap.drawPixel(px, y, candidates[thresholdMatrix[
                            ((int) (px * 0x0.C13FA9A902A6328Fp3 + y * 0x1.9E3779B97F4A7C15p2) & 3) ^
                                    ((px & 3) | (y & 3) << 2)
                            ]]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }
    
//    /**
//     * Reduces a Pixmap to the palette this knows by using a skewed version of Thomas Knoll's pattern dither, which is
//     * out-of-patent since late 2019, using golden-ratio-based values to handle the skew.
//     * The output this produces is very dependent on the palette and this PaletteReducer's dither strength, which can be
//     * set with {@link #setDitherStrength(float)}.
//     * The algorithm was described in detail by Joel
//     * Yliluoma in <a href="https://bisqwit.iki.fi/story/howto/dither/jy/">this dithering article</a>; Yliluoma used an
//     * 8x8 threshold matrix because at the time 4x4 was still covered by the patent, but using 4x4 allows a much faster
//     * sorting step (this uses a sorting network, which works well for small input sizes like 16 items).
//     * @see #reduceKnoll(Pixmap) An alternative that uses a similar pattern but has a more obvious grid
//     * @param pixmap a Pixmap that will be modified
//     * @return {@code pixmap}, after modifications
//     */
//    public Pixmap reduceKnollGolden (Pixmap pixmap) { 
//        boolean hasTransparent = (paletteArray[0] == 0);
//        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
//        Pixmap.Blending blending = pixmap.getBlending();
//        pixmap.setBlending(Pixmap.Blending.None);
//        int color, used, cr, cg, cb, usedIndex;
//        final float errorMul = ditherStrength * 0.6180339887498949f * 0.5f;
//        computePaletteGamma(2.0 - ditherStrength * 1.666);
//        for (int y = 0; y < h; y++) {
//            for (int px = 0; px < lineLen; px++) {
//                color = pixmap.getPixel(px, y);
//                if ((color & 0x80) == 0 && hasTransparent)
//                    pixmap.drawPixel(px, y, 0);
//                else {
//                    int er = 0, eg = 0, eb = 0;
//                    cr = (color >>> 24);
//                    cg = (color >>> 16 & 0xFF);
//                    cb = (color >>> 8 & 0xFF);
//                    for (int i = 0; i < candidates.length; i++) {
//                        int rr = MathUtils.clamp((int) (cr + er * errorMul), 0, 255);
//                        int gg = MathUtils.clamp((int) (cg + eg * errorMul), 0, 255);
//                        int bb = MathUtils.clamp((int) (cb + eb * errorMul), 0, 255);
//                        usedIndex = paletteMapping[((rr << 7) & 0x7C00)
//                                | ((gg << 2) & 0x3E0)
//                                | ((bb >>> 3))] & 0xFF;
//                        candidates[i] = paletteArray[usedIndex];
//                        used = gammaArray[usedIndex];
//                        er += cr - (used >>> 24);
//                        eg += cg - (used >>> 16 & 0xFF);
//                        eb += cb - (used >>> 8 & 0xFF);
//                    }
//                    sort16(candidates);
////                    pixmap.drawPixel(px, y, candidates[thresholdMatrix[
////                            ((int) (px * 6.180339887498949 + y * 0.6180339887498949 * 6.180339887498949) & 3) ^
////                                    ((px & 3) | (y & 3) << 2)
////                            ]]);
////                    pixmap.drawPixel(px, y, candidates[thresholdMatrix[
////                            ((int) (TrigTools.cos(px + y * 0.6180339887498949) * 4.0) & 3) ^
////                                    ((px & 3) | (y & 3) << 2)
////                            ]]);
//                    pixmap.drawPixel(px, y, candidates[thresholdMatrix[
//                            ((int) (px * 0x0.C13FA9A902A6328Fp3 + y * 0x1.9E3779B97F4A7C15p2) & 3) ^
//                                    ((px & 3) | (y & 3) << 2)
//                            ]]);
//                }
//            }
//        }
//        pixmap.setBlending(blending);
//        return pixmap;
//    }

    /**
     * Retrieves a random non-0 color index for the palette this would reduce to, with a higher likelihood for colors
     * that are used more often in reductions (those with few similar colors). The index is returned as a byte that,
     * when masked with 255 as with {@code (palette.randomColorIndex(random) & 255)}, can be used as an index into a
     * palette array with 256 or less elements that should have been used with {@link #exact(int[])} before to set the
     * palette this uses.
     * @param random a Random instance, which may be seeded
     * @return a randomly selected color index from this palette with a non-uniform distribution, can be any byte but 0
     */
    public byte randomColorIndex(Random random)
    {
        return paletteMapping[random.nextInt() >>> 17];
    }

    /**
     * Retrieves a random non-transparent color from the palette this would reduce to, with a higher likelihood for
     * colors that are used more often in reductions (those with few similar colors). The color is returned as an
     * RGBA8888 int; you can assign one of these into a Color with {@link Color#rgba8888ToColor(Color, int)} or
     * {@link Color#set(int)}.
     * @param random a Random instance, which may be seeded
     * @return a randomly selected color from this palette with a non-uniform distribution
     */
    public int randomColor(Random random)
    {
        return paletteArray[paletteMapping[random.nextInt() >>> 17] & 255];
    }

    /**
     * Looks up {@code color} as if it was part of an image being color-reduced and finds the closest color to it in the
     * palette this holds. Both the parameter and the returned color are RGBA8888 ints.
     * @param color an RGBA8888 int that represents a color this should try to find a similar color for in its palette
     * @return an RGBA8888 int representing a color from this palette, or 0 if color is mostly transparent
     * (0 is often but not always in the palette)
     */
    public int reduceSingle(int color)
    {
        if((color & 0x80) == 0) // less visible than half-transparent
            return 0; // transparent
        return paletteArray[paletteMapping[
                (color >>> 17 & 0x7C00)
                        | (color >>> 14 & 0x3E0)
                        | (color >>> 11 & 0x1F)] & 0xFF];
    }

    /**
     * Looks up {@code color} as if it was part of an image being color-reduced and finds the closest color to it in the
     * palette this holds. The parameter is a RGBA8888 int, the returned color is a byte index into the
     * {@link #paletteArray} (mask it like: {@code paletteArray[reduceIndex(color) & 0xFF]}).
     * @param color an RGBA8888 int that represents a color this should try to find a similar color for in its palette
     * @return a byte index that can be used to look up a color from the {@link #paletteArray}
     */
    public byte reduceIndex(int color)
    {
        if((color & 0x80) == 0) // less visible than half-transparent
            return 0; // transparent
        return paletteMapping[
                (color >>> 17 & 0x7C00)
                        | (color >>> 14 & 0x3E0)
                        | (color >>> 11 & 0x1F)];
    }

    /**
     * Looks up {@code color} as if it was part of an image being color-reduced and finds the closest color to it in the
     * palette this holds. Both the parameter and the returned color are packed float colors, as produced by
     * {@link Color#toFloatBits()} or many methods in SColor.
     * @param packedColor a packed float color this should try to find a similar color for in its palette
     * @return a packed float color from this palette, or 0f if color is mostly transparent
     * (0f is often but not always in the palette)
     */
    public float reduceFloat(float packedColor)
    {
        final int color = NumberUtils.floatToIntBits(packedColor);
        if(color >= 0) // if color is non-negative, then alpha is less than half of opaque
            return 0f;
        return NumberUtils.intBitsToFloat(Integer.reverseBytes(paletteArray[paletteMapping[
                (color << 7 & 0x7C00)
                        | (color >>> 6 & 0x3E0)
                        | (color >>> 19)] & 0xFF] & 0xFFFFFFFE));

    }

    /**
     * Modifies {@code color} so its RGB values will match the closest color in this PaletteReducer's palette. If color
     * has {@link Color#a} less than 0.5f, this will simply set color to be fully transparent, with rgba all 0.
     * @param color a libGDX Color that will be modified in-place; do not use a Color constant, use {@link Color#cpy()}
     *              or a temporary Color
     * @return color, after modifications.
     */
    public Color reduceInPlace(Color color)
    {
        if(color.a < 0.5f)
            return color.set(0);
        return color.set(paletteArray[paletteMapping[
                ((int) (color.r * 0x1f.8p+10) & 0x7C00)
                        | ((int) (color.g * 0x1f.8p+5) & 0x3E0)
                        | ((int) (color.r * 0x1f.8p+0))] & 0xFF]);
    }
    public static int hueShift(int rgba)
    {
        final int a = rgba & 0xFF;
        final float r = (rgba >>> 24) / 255f, g = (rgba >>> 16 & 0xFF) / 255f, b = (rgba >>> 8 & 0xFF) / 255f;
        final float luma = (float)Math.pow(r * 0.375f + g * 0.5f + b * 0.125f, 1.1875);
        final float adj = MathUtils.sin((luma - 0.5f) * Math.abs(luma - 0.5f) * 13.5f) * 0.09f;//(1.875f * 6.283185307179586f)
        final float warm = adj + r - b, mild = 0.5f * (adj + g - b);
        return (MathUtils.clamp((int) ((luma + 0.625f * warm - mild) * 256f), 0, 255)<<24|
                MathUtils.clamp((int) ((luma - 0.375f * warm + mild) * 256f), 0, 255)<<16|
                MathUtils.clamp((int) ((luma - 0.375f * warm - mild) * 256f), 0, 255)<<8|
                a);
    }
    public static void hueShiftPalette(int[] palette)
    {
        for (int i = 0; i < palette.length; i++) {
            palette[i] = hueShift(palette[i]);
        }
    }
    public void hueShift()
    {
        hueShiftPalette(paletteArray);
    }

}
