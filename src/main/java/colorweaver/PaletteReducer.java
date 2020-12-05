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
 * <br>
 * This class does a not-insignificant amount of work in static initializers, so it may penalize startup time slightly.
 * The {@link #labs} array needs close to a hundred-thousand calls to {@link Math#cbrt(double)}, for instance.
 * <br>
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
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            int indexA = (r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3),
                    indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
            final double
                    L = labs[0][indexA] - labs[0][indexB],
                    A = labs[1][indexA] - labs[1][indexB],
                    B = labs[2][indexA] - labs[2][indexB];
            return L * L * 11.0 + A * A * 1.6 + B * B;
        }
    };
    
    public static final double[][] ipts = new double[3][0x8000];
    static {
        double l, m, s;
        int idx = 0;
        for (int ri = 0; ri < 32; ri++) {
            for (int gi = 0; gi < 32; gi++) {
                for (int bi = 0; bi < 32; bi++) {
                    l = (0.010126483870967743) * ri + (0.020628) * gi + (0.0015031290322580645) * bi;
                    m = (0.004893322580645161) * ri + (0.024135774193548388) * gi + (0.003225948387096774) * bi;
                    s = (5.726774193548388E-4) * ri + (0.0035312258064516128) * gi + (0.028160290322580644) * bi;

                    ipts[0][idx] = 0.4000 * l + 0.4000 * m + 0.2000 * s;
                    ipts[1][idx] = 4.4550 * l - 4.8510 * m + 0.3960 * s;
                    ipts[2][idx] = 0.8056 * l + 0.3572 * m - 1.1628 * s;


                    idx++;
                }
            }
        }
    }

    public static final ColorMetric iptQuickMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            int indexA = (r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3),
                    indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
            final double
                    i = ipts[0][indexA] - ipts[0][indexB],
                    p = ipts[1][indexA] - ipts[1][indexB],
                    t = ipts[2][indexA] - ipts[2][indexB];
            return (i * i * 3.0 + p * p + t * t) * 0x1p13;
//            return i * i * 16.0 + p * p * 9.0 + t * t * 9.0;
        }
    };
    
    private static final double[] RGB_POWERS = new double[3 << 8];
    static {
        for (int i = 1; i < 256; i++) {
            RGB_POWERS[i]     = Math.pow(i, 3.7);
            RGB_POWERS[i+256] = Math.pow(i, 4.0);
            RGB_POWERS[i+512] = Math.pow(i, 3.3);
        }
    }

    public static final ColorMetric rgbEasyMetric = new ColorMetric(){
        /**
         * Color difference metric; returns large numbers even for smallish differences.
         * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
         *
         * @param color1 an RGBA8888 color as an int
         * @param color2 an RGBA8888 color as an int
         * @return the difference between the given colors, as a positive double
         */
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return (RGB_POWERS[Math.abs((color1 >>> 24) - (color2 >>> 24))]
                    + RGB_POWERS[256+Math.abs((color1 >>> 16 & 0xFF) - (color2 >>> 16 & 0xFF))]
                    + RGB_POWERS[512+Math.abs((color1 >>> 8 & 0xFF) - (color2 >>> 8 & 0xFF))]) * 0x1p-10;
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
        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return (RGB_POWERS[Math.abs((color1 >>> 24) - r2)]
                    + RGB_POWERS[256+Math.abs((color1 >>> 16 & 0xFF) - g2)]
                    + RGB_POWERS[512+Math.abs((color1 >>> 8 & 0xFF) - b2)]) * 0x1p-10;
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
            return (RGB_POWERS[Math.abs(r1 - r2)]
                    + RGB_POWERS[256+Math.abs(g1 - g2)]
                    + RGB_POWERS[512+Math.abs(b1 - b2)]) * 0x1p-10;
        }
    };

    public static final BasicColorMetric basicMetric = new BasicColorMetric(); // has no state, should be fine static
    public static final LABEuclideanColorMetric labMetric = new LABEuclideanColorMetric();
    public static final LABRoughColorMetric labRoughMetric = new LABRoughColorMetric();
    public byte[] paletteMapping;
    public final int[] paletteArray = new int[256];
    final int[] gammaArray = new int[256];
    ByteArray curErrorRedBytes, nextErrorRedBytes, curErrorGreenBytes, nextErrorGreenBytes, curErrorBlueBytes, nextErrorBlueBytes;
    public int colorCount;
    double ditherStrength = 0.5f, populationBias = 0.5;

    /**
     * This stores a preload code for a PaletteReducer using {@link Coloring#AURORA} with {@link #rgbEasyMetric}. Using
     * a preload code in the constructor {@link #PaletteReducer(int[], byte[])} eliminates the time needed to fill 32 KB
     * of palette mapping in a somewhat-intricate way that only gets more intricate with better metrics, and replaces it
     * with a straightforward load from a String into a 32KB byte array. This load is a simple getBytes(), but does use
     * {@link StandardCharsets}, which requires Android API Level 19 or higher (Android OS 4.4 KitKat, present on over
     * 98% of devices) and is otherwise omnipresent. StandardCharsets is available on GWT and supports the charset used
     * here, ISO 8859-1, but doesn't support many other charsets (just UTF-8).
     */
    private static final byte[] ENCODED_AURORA = "\001\001\001\001\001\001\030\030\030\030\030\030\027\027\027\027\027\027\027\027ÞÞÞÞÞÞÞ\025\025\025\025\025\001\001\001\001\002\002u\030\030\030\030\030\030\027\027\027\027\027\027\027ÞÞÞÞÞÞÞ\025\025\025\025\025\001\001\002uuuuu\030\030\030\030\030\030\027\027\027\027\027ÞÞÞÞÞÞÞÞÝÝÝ\025\025\002\002\002uuuuààààà\030\030\030\030\027\027ÞÞÞÞÞÞÞÞÝÝÝÝÝÝ\002\002\002uuuuàààààààÊËËËËÞÞÞÞÞÝÝÝÝÝÝÝÝWWuuuuàààààààËËËËËËËËÝÝÝÝÝÝÝÝÝÝÝW½½½½½½½½àààËËËËËËËËËËÝÝÝÝÝÝÝÝÝÝ³½½½½½½½½½ÊÊËËËËËËËÌÌÌÌÌÌÌÎÎÎÎÎÎ³½½½½½½½½½½ËËËËËËÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´½½½ÉÉÉÉÉÉÉÌÌÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´´´ÉÉÉÉÉÉÉÉÉÌÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´´ffÉÉÉÉÉÉÉÉÌÌÌÌÌÌÌÌÏÎÎÎÎÎÎÎ´´´´´´fffÉÉÉÉÉÉÉÉÉÏÏÏÏÏÏÏÏÏÎÎÎÎÎ´´´´´fffffffÉÉÉ\020\020\020\020\020ÏÏÏÏÏÏÏÏ××××+++++fffffff\020\020\020\020\020\020\020\020ÏÏÏÏÏÏÏ×××××+++++fffffff\020\020\020\020\020\020\020\020\020ÏÏÏÏÏ××××××++++++¼¼¼¼¼\020\020\020\020\020\020\020\020\020\020\020\020ÈÈÈ××××××+++++µ¼¼¼¼¼\020\020\020\020\020\020\020\020\020\020\020ÈÈÈÈÈÈ××××µµµµµµ¼¼¼¼¼¾¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµµ¼¼¼¼¾¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµ»»»»»»»»¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµººº»»»»»»»»»»»¾¾¾¾¿¿¿ÈÈÈÈÈÈÈÖÖÖºººººº»»»»»»»»»¿¿¿¿¿¿¿¿¿ÈÖÖÖÖÖÖÖººººººº»»»»»»»¿¿¿¿¿¿¿¿¿¿¿ÖÖÖÖÖÖÖººººººº»»»»»»»¿¿¿¿¿¿¿¿¿¿ÇÇÖÖÖÖÖÖ¶ºººººººÀÀÀÀÀÀÀ¿¿¿¿¿¿¿¿ÇÇÇÇÇÇÖÖÖ¶¶¶¶¶¶ººÀÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÇÇÇ¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÀÀÀÇÇÇÇÇÇÇÇÇÇÇÇ·······ÀÀÀÀÀÀÀÀÀÀÀÀÀÇÇÇÇÇÇÇÇÇÇÇÇ--······ÀÀÀÀÀÀÀÀÀÀÀÀÇÇÇÇÇÇÇÇÇ\022\022\022------··ÀÀÀÀÀÀÀÀÀÀÀÀÇÇÇÇÇ\022\022\022\022\022\022\022---------ÀÀÀÀÀÀÀÀÀÀÁÁÇÇ\022\022\022\022\022\022\022\022\022\001\001\001\001\001\002\030\030\030\030\030\030\027\027\027\027\027\027\027\027ÞÞÞÞÞÞÞ\025\025\025\025\025\001\001\001\002\002\002u\030\030\030\030\030\030\027\027\027\027\027\027\027ÞÞÞÞÞÞÞ\025\025\025\025\025\002\002\002\002uuuu\030\030\030\030\030\030\027\027\027\027\027ÞÞÞÞÞÞÞÝÝÝÝ\025\025\002\002\002\002uuuuàà\030\030\030\030\030ßßßßÞÞÞÞÞÞÝÝÝÝÝÝÝWWWuuuuàààààÊÊÊËËËËÞÞÞÞÝÝÝÝÝÝÝÝÝWWWWW½ààààààÊËËËËËËËËÝÝÝÝÝÝÝÝÝÝÝWgg½½½½½½ÊÊÊÊËËËËËËËËËÝÝÝÝÝÝÝÝÝÝ³³½½½½½½½½ÊÊËËËËËËËËÌÌÌÌÌÌÎÎÎÎÎÎ³³½½½½½½½½½ËËËËËËÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´½½½½ÉÉÉÉÉÉÉÌÌÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´´´ÉÉÉÉÉÉÉÉÉÌÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´´ffÉÉÉÉÉÉÉÉÌÌÌÌÌÌÏÏÏÏÎÎÎÎÎÎ´´´´´´fffffÉÉÉÉÉÉÉÏÏÏÏÏÏÏÏÏÎÎÎÎÎ´´´´´ffffffffÉÉ\020\020\020\020ÏÏÏÏÏÏÏÏÏ××××+++++ffffffff\020\020\020\020\020\020\020ÏÏÏÏÏÏÏ×××××+++++fffffff\020\020\020\020\020\020\020\020ÏÏÏÏÏÏÏ×××××++++++¼¼¼¼¼¼\020\020\020\020\020\020\020\020\020\020ÐÐÐÐ××××××µµµµµµ¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈ××××µµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ»»»»»»»¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµ»»»»»»»»»»»¾¾¾¾¾¿¿ÈÈÈÈÈÈÈÈÖÖºººººº»»»»»»»»»¿¿¿¿¿¿¿¿¿ÈÖÖÖÖÖÖÖººººººº»»»»»»»¿¿¿¿¿¿¿¿¿¿¿ÖÖÖÖÖÖÖººººººº»»»»»»»¿¿¿¿¿¿¿¿¿¿ÇÇÖÖÖÖÖÖ¶¶¶ºººººÀÀÀÀÀÀÀ¿¿¿¿¿¿¿¿ÇÇÇÇÇÇÖÖÖ¶¶¶¶¶¶ººÀÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÇÇÇ¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÀÀÀÇÇÇÇÇÇÇÇÇÇÇÇ········ÀÀÀÀÀÀÀÀÀÀÀÀÇÇÇÇÇÇÇÇÇÇÇÇ········ÀÀÀÀÀÀÀÀÀÀÀÀÇÇÇÇÇÇÇÇÇ\022\022\022-----···ÀÀÀÀÀÀÀÀÀÀÀÁÁÇÇÇÇ\022\022\022\022\022\022\022--------·ÀÀÀÀÀÀÁÁÁÁÁÁÁÁÁ\022\022\022\022\022\022\022\022\001\001\001\001\001\030\030\030\030\030\030\030\027\027\027\027\027\027\027ÞÞÞÞÞÞÞ\025\025\025\025\025\001\002\002\002\002\002u\030\030\030\030\030\030\027\027\027\027\027\027ßÞÞÞÞÞÞÞ\025\025\025\025\025\002\002\002\002\002uuu\030\030\030\030\030\030\027\027ßßßßÞÞÞÞÞÝÝÝÝÝÝ\025W\002\002\002uuuuàà\030\030\030\030\030ßßßßßÞÞÞÞÝÝÝÝÝÝÝÝWWWWuuuuààààÊÊÊßßßßßÞÞÞÝÝÝÝÝÝÝÝÝWWWWgggààÊÊÊÊÊËËËËËËËÝÝÝÝÝÝÝÝÝÝÝ²ggg½½½½ÊÊÊÊÊËËËËËËËËËÝÝÝÝÝÝÝÝÝÝ³³gg½½½½½ÊÊÊÊËËËËËËËÌÌÌÌÌÌÎÎÎÎÎÎ³³³³½½½½½½hhËËËËËËÌÌÌÌÌÌÌÎÎÎÎÎÎÎ³³³³³XXXXhhÉÉÉÉËÌÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´XXXXÉÉÉÉÉÉÉÌÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´´XffÉÉÉÉÉÉÉÌÌÌÌÌÏÏÏÏÏÏÎÎÎÎÎ´´´´´ffffffÉÉÉÉÉÉÉÏÏÏÏÏÏÏÏÏÏÎÎÎÎ´´´´´ffffffffÉÉÉ\020\020\020ÏÏÏÏÏÏÏÏÐÐÐÐ×+++++ffffffff\020\020\020\020\020\020ÏÏÏÏÏÏÏÏÐÐ×××+++++fff¼¼¼¼\020\020\020\020\020\020\020\020ÏÏÏÏÐÐÐÐÐ×××+++++¼¼¼¼¼¼¼¼\020\020\020\020\020\020\020\020\020ÐÐÐÐÐÐ××××µµµµµµ¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈ×××µµµµµµ¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ»»»»»»»¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµ»»»»»»»»»»»¾¾¾¾¾¿¿ÈÈÈÈÈÈÈÈÈÖºººººº»»»»»»»»¿¿¿¿¿¿¿¿¿¿ÈÈÖÖÖÖÖÖºººººº»»»»»»»»¿¿¿¿¿¿¿¿¿¿¿ÖÖÖÖÖÖÖººººººº»»»»»»»¿¿¿¿¿¿¿¿¿¿ÇÇÖÖÖÖÖÖ¶¶¶¶¶ºººÀÀÀÀÀÀ¿¿¿¿¿¿¿¿¿ÇÇÇÇÇÇÖÖÖ¶¶¶¶¶¶¶ºÀÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÇÇÇ¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÀÀÁÁÇÇÇÇÇÇÇÇÇÇÇ········ÀÀÀÀÀÀÀÀÀÀÀÁÁÇÇÇÇÇÇÇÇÇÇÇ········ÀÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇ\022\022\022----····ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÁÁÁÇ\022\022\022\022\022\022--------··ÀÀÀÁÁÁÁÁÁÁÁÁÁÁÁ\022\022\022\022\022\022\022\001\030\030\030\030\030\030\030\027ßßßßßßßßÞÞÞÞ\025\025\025\025\025\002\002\002\002\002\030\030\030\030\030\030\030ßßßßßßßßßÞÞÞÝÝÝ\025\025\025\002\002\002\002\002uuu\030\030\030\030\030\030ßßßßßßßßßÝÝÝÝÝÝÝÝÝWW\002\002uuuvv\030\030\030\030\030ßßßßßßßßßÝÝÝÝÝÝÝÝÝWWWWuuvvvvàÊÊÊÊßßßßßßßÝÝÝÝÝÝÝÝÝÝWWWggggvÊÊÊÊÊÊËËËËËËËÝÝÝÝÝÝÝÝÝÝÝ²²gggg½½ÊÊÊÊÊËËËËËËËËËÝÝÝÝÝÝÝÝÝÝ²²³g½½½½½hhÊÊËËËËËËËËÌÌÌÌÌÎÎÎÎÎÎ³³³³½½½½hhhhËËËËËËËËÌÌÌÌÌÎÎÎÎÎÎÎ³³³³XXXXXhhhhÉËËËÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´XXXXXXÉÉÉÉÉÉÌÌÌÌÌÌÌÌÏÎÎÎÎÎÎÎ´´´´´XXXXXÉÉÉÉÉÉÍÍÍÏÏÏÏÏÏÏÏÎÎÎÎÎ´´´´´fffffffÉÉÉÉÍÍÍÏÏÏÏÏÏÏÏÏÐÎÎÎ´´´´´ffffffffÉÉÉÍÍÍÏÏÏÏÏÏÏÐÐÐÐÐÐ++++ffffffffff\020\020\020\020\020ÏÏÏÏÏÏÐÐÐÐÐÐÐ+++++ff¼¼¼¼¼¼\020\020\020\020\020\020ÏÏÏÏÏÐÐÐÐÐÐÐ×µµµµµ¼¼¼¼¼¼¼¼¼\020\020\020\020\020\020\020ÐÐÐÐÐÐÐÐÐÐ×µµµµµµ¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈ×µµµµµµ¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ»»»»»»¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµ»»»»»»»»»»¾¾¾¾¾¿¿ÈÈÈÈÈÈÈÈÈÈºººººº»»»»»»»»¿¿¿¿¿¿¿¿¿¿ÈÈÖÖÖÖÖÖºººººº»»»»»»»»¿¿¿¿¿¿¿¿¿¿¿ÖÖÖÖÖÖÖ¶¶¶¶¶ºº»»»»»»¿¿¿¿¿¿¿¿¿¿¿ÇÇÖÖÖÖÖÖ¶¶¶¶¶¶ººÀÀÀÀÀÀ¿¿¿¿¿¿¿¿ÇÇÇÇÇÇÇÇÖÖ¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿¿ÇÇÇÇÇÇÇÇÇÇ········ÀÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÇÇÇ········ÀÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÇÇÇ········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÁÁÇÇÇÇÇÇ\022\022·········ÀÀÀÀÀÀÁÁÁÁÁÁÁÁÁÁÁ\022\022\022\022\022\022-------···ÀÀÀÁÁÁÁÁÁÁÁÁÁÁÁÁ\022\022\022\022\022\022ôôôôô\030\030ßßßßßßßßßßßÞÞ\025\025\025\025\025\002ôôôô\030\030\030ßßßßßßßßßßÝÝÝÝÝÝÝÝ\002\002ôôôô\030\030\030ßßßßßßßßßÝÝÝÝÝÝÝÝÝWWWWWvvvvvv\030\030\030ßßßßßßßßßÝÝÝÝÝÝÝÝÝWWWW\003\003vvvvÊÊÊÊÊßßßßßßßÝÝÝÝÝÝÝÝÝÝ²²Wgg\003\003vvÊÊÊÊÊËËËËËËËÝÝÝÝÝÝÝÝÝÝÝ²²²gggg½ÊÊÊÊÊÊËËËËËËË\026\026ÝÝÝÝÝÜÜÜÜ²²²²gg½hhhhhÊËËËËËËËË\026\026\026\026\026ÜÜÜÜÜÜ³³³³³½hhhhhhhËËËËËËËË\026\026\026\026\026ÜÜÜÜÜÜ³³³³XXXXXhhhhhËËËËËË\026\026\026\026\026\026ÜÜÜÜÜÜ³³³³XXXXXXXhhtttÍÍÍÍÍÍÍÍÏÏÏÏÎÎÎÎ´´´´XXXXXXXfftÍÍÍÍÍÍÍÍÏÏÏÏÏÏÏÎÎÎ´´´´´fffffffftÍÍÍÍÍÍÏÏÏÏÏÏÏÏÐÐÐÐ´´´´´ffffffffiÍÍÍÍÍÍÏÏÏÏÏÏÐÐÐÐÐÐ±±±±±ffffffffiiÍÍÍÍÍÏÏÏÏÏÐÐÐÐÐÐÐ±±±±±f¼¼¼¼¼¼¼¼iiiiiÏÏÏÏÐÐÐÐÐÐÐÐÐµµµµµ¼¼¼¼¼¼¼¼¼¼¾¾¾¾¾¾ÐÐÐÐÐÐÐÐÐÐÐµµµµµ¼¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµµ»»»»¾¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµ»»»»»»»»¾¾¾¾¾¾¾¾\021ÈÈÈÈÈÈÈÈÈÈººº,,,,»»»»»»»¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÈÖÖÖºººº,,,»»»»»»»¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖ¶¶¶¶¶¶,,,,,»»¿¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖ¶¶¶¶¶¶¶,,,,,,¿¿¿¿¿¿¿¿¿ÇÇÇÇÇÇÇÇÇÇ········,ÀÀÀÀÀ¿¿¿ÁÁÁÁÁÁÇÇÇÇÇÇÇÇÇ·········ÀÀÀÀÀÀÁÁÁÁÁÁÁÁÁÁÇÇÇÇÇÇÇ·········ÀÀÀÀÀÀÁÁÁÁÁÁÁÁÁÁÇÇÇÇÇÇÇ·········ÀÀÀÀÀÁÁÁÁÁÁÁÁÁÁÁÁÇÇÇÇÇÇ·········ÀÀÀÀÀÁÁÁÁÁÁÁÁÁÁÁÁÁ\022\022\022\022\022·········¹¹¹¹ÂÂÂÂÁÁÁÁÁÁÁÁÁÁ\022\022\022\022\022ôôôôôôóóßßßßßßßßßßßÝÝÝÝÝÝôôôôôôóßßßßßßßßßßÝÝÝÝÝÝÝÝWôôôôôôôßßßßßßßßßßÝÝÝÝÝÝÝÝWWvvvvvôôôßßßßßßßßßßÝÝÝÝÝÝÝÝWW\003\003\003\003\003vvvvÊÊÊÊßßßßßßßßÝÝÝÝÝÝÝÝÝ²²²\003\003\003\003vvvvÊÊÊÊËËËËË\026\026ÝÝÝÝÝÝÝÜÜÜ²²²gggg\004\004\004wwwwwËËËË\026\026\026\026\026\026\026ÜÜÜÜÜÜ²²²²g\004\004hhhhhwwwËËËË\026\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³³\004hhhhhhwwwwËËË\026\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³XXXXXhhhhtttttt\026\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³XXXXXXXtttttttÍÍÍÍÍ\026\026\026\026\026ÜÜÜÜ³³³XXXXXXXXtttttÍÍÍÍÍÍÍÏÏÏÏÏÏÚÚÚ±±±±±YYYYYYttttÍÍÍÍÍÍÍÏÏÏÏÏÏÐÐÐÐ±±±±±±[[[[[[iiiiÍÍÍÍÍÍÏÏÏÐÐÐÐÐÐÐ±±±±±±[[[[[[iiiiiÍÍÍÍÏÏÏÐÐÐÐÐÐÐÐ±±±±±±[[[[[[iiiiiiiÏÏÏÏÐÐÐÐÐÐÐÐÐµµµµµ¼¼¼¼¼¼¼¼¼iiiii¾¾ÐÐÐÐÐÐÐÐÐÐÐµµµµµ¼¼¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾ÐÐÐÐÐÐÐÐÐÐµµµµµ¼¼¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµ¼¼¼¼¼¼¼¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµµµµ¼¼¼¼¾¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÈµµµµ,,,,,,,,,¾¾¾¾¾¾\021\021\021\021\021\021\021\021ÈÈÈÈÈ,,,,,,,,,,,,,,,¿¿\021\021\021\021\021\021\021\021\021\021\021\021\021ÒÒ¶,,,,,,,,,,,,,,¿¿¿\021\021\021\021\021\021\021\021\021\021\021\021\021Ò¶¶¶,,,,,,,,,,,,¿¿¿\021\021\021\021\021\021\021\021\021\021\021\021\021Ö¶¶¶,,,,,,,,,,,¿¿¿¿\021\021\021\021\021\021\021\021\021\021\021\021ÇÇ·······,,,,,,,ÁÁÁÁÁÁÁÁÁÁÁÁ\021ÇÇÇÇÇ········¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÁÁÇÇÇÇÇ········¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÁÁÇÇÇÇÇ········¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÁÁÇÇÇÇÇ········¹¹¹¹¹¹ÂÂÁÁÁÁÁÁÁÁÁÁÁÁÇÇÇ\022·······¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÂÂÂÂÂ\022\022\022\022ôôôôóóóóóßßßßßßßßáááááÝÝÝôôôôôóóóßßßßßßßßßáááÝÝÝÝÝôôôôôôóóßßßßßßßßßááÝÝÝÝÝÝôôôôßßßßßßßßßßááÝÝÝÝÝÝ\003\003\003ÊßßßßßßßßßßÝÝÝÝÝÝÝÜ²²HHHHwwwwwßß\026\026\026\026\026\026\026ÜÜÜÜÜÜ²²²HHH\004\004\004\004wwwwwwwË\026\026\026\026\026\026\026\026\026ÜÜÜÜÜ²²²²H\004\004\004\004wwwwwwwxx\026\026\026\026\026\026\026\026\026ÜÜÜÜÜ³³³II\004\004\004hwwwwwwxxxx\026\026\026\026\026\026\026\026ÜÜÜÜÜ³³³IIXXXXhhhttttttt\026\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³XYYYYYYtttttttÍÍÍÍ\026\026\026\026\026\026ÚÚÚÚ±±±XYYYYYYYttttttÍÍÍÍÍÍÍÏÏÏÚÚÚÚÚ±±±±±YYYYYYtttttÍÍÍÍÍÍÍÏÏÏÏÐÚÚÚÚ±±±±±±[[[[[[iiiiÍÍÍÍÍÍÍÐÐÐÐÐÐÐÐÐ±±±±±±[[[[[[iiiiiiÍÍÍÍÍÐÐÐÐÐÐÐÐÐ±±±±±±[[[[[[iiiiiiiiÏØØÐÐÐÐÐÐÐÐÐ±±±±±[[[¼¼¼¼iiiiiiiiØØØÐÐÐÐÐÐÐÐÐµµµµµ¼¼¼¼¼¼¼¼¼¼¾¾¾¾¾ØØØØÐÐÐÐÐÐÐÐµµµµµ¼¼¼¼¼¼¼¼¼¼¾¾¾¾¾¾ØØØØÈÈÒÒÒÒÒµµµµµ¼¼¼¼¼¼¼¼¼¼¾¾¾¾¾¾¾ÈÈÈÈÒÒÒÒÒÒµµµµµµ,,¼¼¼¼¾¾¾¾¾¾¾¾¾\021\021\021ÈÈÒÒÒÒÒÒµµ,,,,,,,,,,,,¾¾¾¾\021\021\021\021\021\021\021\021\021\021ÒÒÒÒ,,,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021\021\021ÒÒÒ,,,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021\021\021\021ÒÒ¶,,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021\021\021\021\021Ô··,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021\021\021\021\021Å·····,,,,,,,,,¹ÁÁÁÁÁÁÁÁÁÁ\021\021\021\021ÅÅÅ·······¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÁÁÅÅÅÅ······¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÁÅÅÅÅ······¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÁÅÅÅÅ······¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÁÁÁÁÁÅÅÅÅ·····¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÂÂÂÂÅÅÅÅôôôôóóóóóóóßßßßááááááááâââôôôôôóóóóóßßßßßááááááááâââôôôôôóóóóßßßßßááááááááââÝôôôßßßßßßßááááááááââÝßßßßßßßáááááááÜÜÜÜ²HHHHHwwww\026\026\026\026\026\026\026\026ÜÜÜÜÜÜ²HHHHH\004\004\004wwwwwww\026\026\026\026\026\026\026\026\026ÜÜÜÜÜ²²III\004\004\004\004wwwwwwxxxx\026\026\026\026\026\026\026\026ÜÜÜÜÜ²IIIIII\004\004wwwwwxxxxx\026\026\026\026\026\026\026\026ÜÜÜÜÜIIIIIIVVV\005\005\005xxxxxxx\026\026\026\026\026\026\026\026ÜÜÜÜÜ³IIVVYYYYYYttttttttÍÍÍ\026\026\026\026\026ÚÚÚÚÚ±±VVVYYYYYYttttttÍÍÍÍÍÍÍÍÚÚÚÚÚÚÚ±±±±±YYYYYYtttttÍÍÍÍÍÍÍÍÐÐÚÚÚÚÚÚ±±±±±±[[[[[[iiiiiÍÍÍÍÍÍØÐÐÐÐÐÚÚÚ±±±±±±[[[[[[iiiiiiÍÍÍÍØØØØØØÐÐÐÐ±±±±±±[[[[[[iiiiiiiiØØØØØØØØÐÐÐÐ±±±±±[[[[[[[iiiiiiiiØØØØØØØØÐÐÐÐµµµUUU¼¼eeeeeeeeeiiØØØØØØØØØØÐÐÒµµµµ¼¼¼¼¼¼¼¼¼\\\\\\eerrrrrrrrrÒÒÒÒÒµµµµ¼¼¼¼¼¼\\\\\\\\\\\\\\¾rrrrrrrrÒÒÒÒÒÒµµµ,,,,,,,,,\\\\\\\\¾¾\021\021\021\021\021\021\021ÒÒÒÒÒÒÒµ,,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021ÒÒÒÒÒ,,,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021\021ÒÒÒÒ,,,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021\021\021\021ÔÔ,,,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021\021\021\021ÔÔ,,,,,,,,,,,,,,,,\021\021\021\021\021\021\021\021\021\021\021\021\021\021ÔÔ··,,,,,,,,,,,¹¹ÁÁÁÁÁÁÁÁÁÁ\021\021\021ÅÅÅÅ·····¹¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÅÅÅÅÅ·····¹¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÅÅÅÅÅ····¹¹¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÁÅÅÅÅÅ····¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÁÁÅÅÅÅÅ····¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÂÂÂÅÅÅÅ\"ôôôôóóóóóóóóááááááááááâââââôôôôóóóóóóóááááááááááâââââôôôôôóóóóññáááááááááâââââôôñññññáááááááááâââââñññññáááááááááâââÜÜHHHHHHwwñññ\026\026\026\026\026\026ÜÜÜÜÜÜHHHHHH\004wwww\026\026\026\026\026\026\026\026ÜÜÜÜÜHHIIII\004\004wwwwwxxxx\026\026\026\026\026\026\026\026ÜÜÜÜÜ¤IIIIII\005\005\005wwwxxxxxx\026\026\026\026\026\026\026\026ÜÜÜÜÜ¤¤IIIVVV\005\005\005\005xxxxxxxy\026\026\026\026\026\026\026ÜÜÜÜÜ¤¤¤¤VVVVY\005\005\005ttttttyyy\026\026\026\026\026\026ÚÚÚÚÚ¤¤¤¤VVYYYYZZtttttÍÍÍÍÍÍÍÍÚÚÚÚÚÚÚ±±±±±YYYYZZZtttttÍÍÍÍÍÍÍÍÚÚÚÚÚÚÚ±±±±±±[[[[[[iiiiiÍÍÍÍÍÍØØØØØÚÚÚÚ±±±±±±[[[[[[iiiiiisÍÍØØØØØØØØØØÚ±±±±±±[[[[[[iiiiiisssØØØØØØØØØØÐ±±±±UUU[[eeeeeeeiissØØØØØØØØØØØÐ±UUUUUUU\\\\eeeeeeeesØØØØØØØØØØØÒÒ°UUUU\\\\\\\\\\\\\\\\\\\\\\\\\\rrrrrrrrrrÒÒÒÒ°°°°\\\\\\\\\\\\\\\\\\\\\\\\\\\\rrrrrrrrrÒÒÒÒÒ°°,,,,,\\\\\\\\\\\\\\\\\\\\jjjrrrrrrÒÒÒÒÒÒ°,,,,,,,,,,,,,,^^\021\021\021\021\021\021\021\021\021\021ÒÒÒÒÒ°,,,,,,,,,,,,,,^^\021\021\021\021\021\021\021\021\021\021\021ÒÒÒÒ,,,,,,,,,,,,,,,^^\021\021\021\021\021\021\021\021\021\021\021ÔÔÔÔ,,,,,,,,,,,,,,,^^\021\021\021\021\021\021\021\021\021\021\021ÔÔÔÔ,,,,,,,,,,,,,,^^^\021\021\021\021\021\021\021\021\021\021\021ÔÔÔÔ,,,,,,,,,,,,,¹¹ÁÁÁÁÁÁÁÁ\021\021\021\021ÅÅÅÅÅ··,,¹¹¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÅÅÅÅÅÅ···¹¹¹¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÁÁÅÅÅÅÅÅ···¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÁÁÅÅÅÅÅÅ··¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÂÅÅÅÅÅÅ··¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÂÂÅÅÅÅÅ\"\"\"\"\"\"ôôóóóóóóóóóáááááááááââââââ\"\"\"\"\"\"õõõóóóóóóóññááááááááââââââ\"\"\"õõõõõõóññññññááááááááââââââ\"õõõõõññññññááááááááââââââñññññññáááááááââââââHHHHññññññ\026\026\026\026\026\026ÜÜÜÜÜÜ£HHHH\026\026\026\026\026\026\026\026ÜÜÜÜÜ£IIIIII\026\026\026\026\026\026\026\026ÜÜÜÜÜ¤¤IIIII\005\005\005\005\005xxxxxxx\026\026\026\026\026\026\026\026ÜÜÜÜÜ¤¤¤¤IVVV\005\005\005\005xxxxxxyy\026\026\026\026\026\026\026ÜÜÜÜÜ¤¤¤¤VVVV\005\005\005\005xxxxyyyyyy\026\026\026\026\026ÚÚÚÚÚ¤¤¤¤VVVVZZZZZtttyyyyyyyÍÍÚÚÚÚÚÚÚ±±±±±±YZZZZZZtttyyyyyyyÍØÚÚÚÚÚÚÚ±±±±±±[ZZZZZZiiissssssØØØØØØÚÚÚÚ±±±±±±UU[[eeeeiisssssØØØØØØØØØØÚ±±±±±UUUUUeeeeeisssssØØØØØØØØØØØ±±±UUUUUUUeeeeeesssssØØØØØØØØØØØ±UUUUUUU\\\\\\\\\\\\eeeesrrrrrrrrrrrÒÒ°°°UUU\\\\\\\\\\\\\\\\\\\\\\\\rrrrrrrrrrÒÒÒÒ°°°°°\\\\\\\\\\\\\\\\\\\\\\\\jjjrrrrrrrrÒÒÒÒ°°°°°,\\\\\\\\\\\\\\\\\\\\jjjjjjjjjrÒÒÒÒÒÒ°°°,,,,,,,,,,^^^^jjj\021\021\021\021\021\021\021ÒÒÒÒÒ°°,,,,,,,,,,,^^^^^^\021\021\021\021\021\021\021\021\021ÒÒÒÒ°,,,,,,,,,,,,^^^^^^\021\021\021\021\021\021\021\021\021ÔÔÔÔ,,,,,,,,,,,,,^^^^^^\021\021\021\021\021\021\021\021\021ÔÔÔÔ,,,,,,,,,,,,,^^^^^^\021\021\021\021\021\021\021\021ÔÔÔÔÔ¯,,,,,,,,,,,,^^^^^^\021\021\021\021\021\021\021ÅÅÅÅÅÅ¯¯¯,¹¹¹¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÅÅÅÅÅÅÅ¯¯¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÁÁÅÅÅÅÅÅÅ·¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÂÅÅÅÅÅÅ·¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÂÅÅÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÂÂÃÃÃÅÅÅ\"\"\"\"\"\"\"óóóóóóóóóóáááááááââââââââ\"\"\"\"\"\"õõõóóóóóóñññááááááââââââââ\"\"\"\"\"õõõõõòòñññññññáááááââââââââ\"\"\"\"\"õõõõõõòñññññññáááááââââââââ\"\"\"òñññññññáááááââââââââ£££££ññññññññ\026\026\026\026\026ÜÜÜÜÜÜ£££££\026\026\026\026\026\026\026\026ÛÛÛÛÛ£££££=====\026\026\026\026\026\026\026\026ÛÛÛÛÛ¤¤¤II====\005\005\005\026\026\026\026\026\026\026\026ÛÛÛÛÛ¤¤¤¤VVVV\005\005\005\005xxxxxyyyy\026\026\026\026\026\026ÛÛÛÛÛ¤¤¤¤VVVV\005\005\005\006\006xxxyyyyyyy\026\026\026ÛÚÚÚÚÚ¤¤¤¤VVVVZZ\006\006\006\006\006yyyyyyyyyyÚÚÚÚÚÚÚ¥¥¥¥±±ZZZZZZ\006\006\006yyyyyyyyyØÚÚÚÚÚÚÚ¥¥¥¥±±ZZZZZZZZssssssssØØØØØØÚÚÚÚ¥±±±UUUUUUeeeeessssssØØØØØØØØØØÚ±±±±UUUUUUeeeeessssssØØØØØØØØØØØ±±UUUUUUUUeeeeeesssssØØØØØØØØØØØ°UUUUUUU\\\\\\\\\\\\\\ee33rrrrrrrrrrrÒÒ°°°°°U\\\\\\\\\\\\\\\\\\\\\\33rrrrrrrrrrÒÒÒ°°°°°°\\\\\\\\\\\\\\\\\\\\jjjjjrrrrrrrÒÒÒÒ°°°°°°°\\\\\\\\\\\\\\\\\\jjjjjjjjjÑÑÒÒÒÒÒ°°°°°,,,,,,,^^^^^jjjjjjjj\021\021ÒÒÒÒÒ°°°°,,,,,,,,^^^^^^^jjjj\021\021\021\021\021ÔÔÔÔ°°°,,,,,,,,,^^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔÔ¯¯,,,,,,,,,,^^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔÔ¯¯¯,,,,,,,,,^^^^^^^^\021\021\021\021\021\021\021ÔÔÔÔÔ¯¯¯¯,,,,,,,,^^^^^^^^\021\021\021\021\021\021ÅÅÅÅÅÅ¯¯¯¯¯¹¹¹¹¹¹¹¹¹¹¹¹¹ÁÁÁÁÁÁÅÅÅÅÅÅÅÅ¯¯¯¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÃÅÅÅÅÅÅÅ¯¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÅÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÃÃÃÃÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÃÃÃÃÃÃÃÅ\"\"\"\"\"\"\"\"óóóóóóóóòáááááááââââââââ\"\"\"\"\"\"õõõòòòòòòòññááááááââââââââ\"\"\"\"õõõõõòòòòñññññáááááââââââââ\"\"\"õõõõõòòòòñññññáááááââââââââ\"õõõõõõòòòññññññáááááââââââââ£££££ññññññññáááâââÛÛÛÛÛ££££££=====\026\026\026\026\026\026ÛÛÛÛÛÛ£££££======\026\026\026\026\026ÛÛÛÛÛÛÛ¤¤¤¤=======yy\026\026\026\026ÛÛÛÛÛÛÛ¤¤¤¤¤==\005\005\005\005\005xxxxyyyyyyy\026\026ÛÛÛÛÛÛÛ¤¤¤¤¤VV\005\005\006\006\006\006\006xyyyyyyyyy\026ÛÛÛÚÚÚÚ¥¥¤¤¤VVZZ\006\006\006\006\006\006yyyyyyyyyzÚÚÚÚÚÚÚ¥¥¥¥¥ZZZZZZ\006\006\006\006yyyyyyyzzzzÚÚÚÚÚÚ¥¥¥¥¥UZZZZZZZZssssssssØØØØØØÚÚÚÚ¥¥¥¥UUUUUUeeeeesssssssØØØØØØØØÙÙ±±UUUUUUUUeeeeessssssØØØØØØØØØÙÙ±UUUUUUUUUeeeeee3333srrrrrrrrØØÙ°UUUUUUU\\\\\\\\\\\\\\33333rrrrrrrrrrrÒ°°°°°U\\\\\\\\\\\\\\\\\\\\3333ÑrrrrrrrrÒÒÒ°°°°°°\\\\\\\\\\\\\\\\\\\\]jjjjÑÑÑÑÑrrÒÒÒÒ°°°°°°°\\\\\\\\]]]]]]jjjjjjjÑÑÑÑÒÒÒÒ°°°°°°°,,,]]]]^^^jjjjjjjjÑÑÒÒÒÒÒ°°°°°°,,,,,]^^^^^^^jjjjjjj\021ÔÔÔÔÔ¯¯¯¯¯,,,,,,^^^^^^^^^jjjjj\021ÔÔÔÔÔÔ¯¯¯¯¯,,,,,,^^^^^^^^^^\021\021\021\021\021ÔÔÔÔÔÔ¯¯¯¯¯,,,,,,^^^^^^^^^^\021\021\021\021\021ÔÔÔÔÔÔ¯¯¯¯¯¯¯,,,¹^^^^^^^^^^^\021\021\021ÅÅÅÅÅÅÅ¯¯¯¯¯¹¹¹¹¹¹¹¹¹¹¹¹¹¹^^^\021ÆÅÅÅÅÅÅÅÅ¯¯¯¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÃÃÃÃÃÃÅÅÅÅÅÅ¯¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÃÃÃÃÃÃÃÃÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÃÃÃÃÃÃÃÃÃÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÂÃÃÃÃÃÃÃÃÃÃÃÅ\"\"\"\"\"\"\"\"òòòòòòòò\031\031ááááââââââââåå\"\"\"\"\"\"õõõòòòòòòòñññáááâââââââââå\"\"õõõõõòòòòòññññáááâââââââââå\"\"õõõõõòòòòñññññáááââââââââââõõõõõõòòòñññññññááââââââââââ£££££££õõõõñññññññññáâââââÛÛÛÛÛ££££££=====\026\026ãÛÛÛÛÛÛÛÛ£££££======\026\026\026ÛÛÛÛÛÛÛÛ¤¤££=======yyyyyÛÛÛÛÛÛÛÛ¤¤¤¤¤>>>>>>yyyyyyyyÛÛÛÛÛÛÛÛ¤¤¤¤¤GGGG\006\006\006\006\006\006yyyyyyyyyÛÛÛÛÛÛÛÛ¥¥¥¥¤GGGG\006\006\006\006\006\006yyyyyyyyzzzÙÚÚÚÚÚ¥¥¥¥GGGG\006\006\006\006\006\006\007\007yyyyzzzzzzÙÙÙÙÙÙ¥¥¥¥JJJJJJZ\007\007\007\007\007sssszzzz{{{{ÙÙÙÙ¥¥¥¥JUUUUUU\007\007\007\007\007sssszzzz{{{{ÙÙÙÙ***JUUUUUUUe\007\0073333ssszz{{{{{ÙÙÙÙ***UUUUUUU\\\\\\\\3333333rrrrrrrrr\024\024***UUUUU\\\\\\\\\\\\3333333rrrrrrrrr\024\024°°°°°°\\\\\\\\\\\\\\\\\\33333ÑÑÑÑÑÑrrrrÒÒ°°°°°°\\\\\\\\\\\\\\\\]]]]jjjÑÑÑÑÑÑÑrÒÒÒ°°°°°°°\\\\]]]]]]]]]jjjjjÑÑÑÑÑÑÒÒÒ°°°°°°°°]]]]]]]]]]jjjjjjjÑÑÑÒÒÒÒ°°°°°°°°]]]]]]^^^^^jjjjjjjjÔÔÔÔÔ¯¯¯¯¯¯¯¯]]]]^^^^^^^^jjjjjjÔÔÔÔÔÔ¯¯¯¯¯¯¯¯,,^^^^^^^^^^^^^jÆÆÔÔÔÔÔÔ¯¯¯¯¯¯¯¯,^^^^^^^^^^^^^^ÆÆÆÔÔÔÔÔÔ¯¯¯¯¯¯¯¯¹¹¹^^^^^^^^^^^^ÆÆÆÆÅÅÅÅÅ¯¯¯¯¯¯¹¹¹¹¹¹¹¹¹¹¹¹^^^^ÆÆÆÆÆÅÅÅÅÅ¯¯¯¯¹¹¹¹¹¹¹¹¹¹¹¹¹¹¸ÃÃÃÃÃÃÃÅÅÅÅÅÅ®¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¸¸¸ÃÃÃÃÃÃÃÃÃÅÅÅÅ®¹¹¹¹¹¹¹¹¹¹¹¹¹¹¸¸¸¸ÃÃÃÃÃÃÃÃÃÃÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÃÃÃÃ!!!!!!\"\"òòòò\031\031\031\031\031\031\031\031\031âââââââåååå\"\"õõòòòòòòòò\031\031\031\031áââââââââååå\"õõõõòòòòòòòññññáâââââââââåå\"õõõõòòòòòòòññññáâââââââââââ£õõõõòòòòòòññññññâââââââââââ£££££££õõõ=òñññññññññãããããããããÛÛ££££££>>>>>ãããããÛÛÛÛÛÛ£££££>>>>>>ããããÛÛÛÛÛÛÛ###£>>>>>>>ãããÛÛÛÛÛÛÛ####>>>>>>>ÛÛÛÛÛÛÛÛ¥¥¥¥GGGGGGG\006\006\006ÛÛÛÛÛÛÛÛ¥¥¥¥GGGGGG\006\006\006\006zzzÙÙÙÙÙÙÙ¥¥¥¥GGGGGG\006\006\007\007\007\007zzzzzz{ÙÙÙÙÙÙ¥¥¥¥JJJJJJJ\007\007\007\007\007\007szzzzzz{{{ÙÙÙÙÙ****JJJJJJJ\007\007\007\007\007\0073zzzzz{{{{{ÙÙÙÙ****JJJJJJJ\007\007\007333333zz{{{{{{ÙÙÙÙ****UUUUTTTTTT3333333{{{{{{{{\024\024\024****UUUTTTTTTT33333322rrrrrr\024\024\024\024°°°°°°TTTTTTTTT222222ÑÑÑÑÑÑÑÑ\024\024\024°°°°°°TTTTTTTT]]]222jÑÑÑÑÑÑÑÑÑÒÒ°°°°°°°TT]]]]]]]]]jjjjjÑÑÑÑÑÑÑÒÒ°°°°°°°°]]]]]]]]]]jjjjjjjÑÑÑÑÑÔÔ°°°°°°°°]]]]]]]]^^^jjjjjjjjÔÔÔÔÔ¯¯¯¯¯¯¯¯]]]]^^^^^^^^jjjjjjÔÔÔÔÔÔ¯¯¯¯¯¯¯¯SS^^^^^^^^^^^^^jÆÆÆÔÔÔÔÔ¯¯¯¯¯¯¯¯SS^^^^^^^^^^^^^ÆÆÆÆÆÆÆÔÔ¯¯¯¯¯¯¯¯¯^^^^^^^^^^^^^kÆÆÆÆÆÆÆÆÆ¯¯¯¯¯¯¯¹¹¹¹¹¹¹¹¹¹^^__kÆÆÆÆÆÆÆÆÆÆ¯¯¯¯¯¹¹¹¹¹¹¹¹¸¸¸¸¸¸¸ÃÃÃÃÃÆÆÆÆÅÅÅ®®®®¹¹¹¹¹¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÅÅÅ®®®¹¹¹¹¹¹¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÃÅÅ®¹¹¹¹¹¹¹¸¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÃÃÃ!!!!!!!òòòò\031\031\031\031\031\031\031\031\031\031ââââââååååå!!!!!!ÿòòòòòò\031\031\031\031\031\031\031\031ðââââââååååÿòòòòòòòòò\031\031\031\031ððâââââââåååõòòòòòòòòòòñññððâââââââââå££õòòòòòòòòòñññðððââââââããã££££££££òòòòòòòñññññãããããããããããã###££>>>>>>ãããããããããÛÛÛ####>>>>>>>ããããããããÛÛÛÛ####>>>>>>>ããããÛÛÛÛÛÛ####>>>>>>>ÛÛÛÛÛÛÛ¥¥¥GGGGGGGGGÛÛÛÛÛÛ¥¥¥¥GGGGGGGG\006zzzÙÙÙÙÙÙ¥¥¥¥GGGGGG44\007\007\007\007zzzzzzÙÙÙÙÙÙÙ¥¥¥¥JJJJJJ44\007\007\007\007\007zzzzzzz{{{ÙÙÙÙÙ****JJJJJJ44\007\007\007\007\007\bzzzzz{{{{ÙÙÙÙÙ****JJJJJJJ4\007\007\b33333zz{{{{{{Ù\024\024\024*****JJJTTTTTT\b333333{{{{{{{\024\024\024\024******TTTTTTTT33332222ÑÑÑÑÑ\024\024\024\024\024°°°°°°TTTTTTTTT222222ÑÑÑÑÑÑÑÑ\024\024\024°°°°°°TTTTTTTT]]22222ÑÑÑÑÑÑÑÑÑÓÓ°°°°°°°TT]]]]]]]]]jjjjÑÑÑÑÑÑÑÑÓÓ°°°°°°°SS]]]]]]]]]jjjjjjÑÑÑÑÑÓÓÓ°°°°°°°SSS]]]]]]]^^jjjjjjjqqÔÔÔÔ¯¯¯¯¯¯¯SSSS]^^^^^^^^djjjjkkÔÔÔÔÔ¯¯¯¯¯¯¯SSSSS^^^^^^^^^dkkkÆÆÆÆÆÆÆ¯¯¯¯¯¯¯SSSS^^^^^^^^^^kkkkÆÆÆÆÆÆÆ¯¯¯¯¯¯¯¯SSS^^^^^^^^__kkkÆÆÆÆÆÆÆÆ¯¯¯¯¯¯¯®®®¹¹_________kkÆÆÆÆÆÆÆÆÆ®®®®®®®®¸¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÆÆÆÆÆÆÆÆ®®®®®®®¸¸¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÃÄÄ®®®®­­­¸¸¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÃÃÄ®­­­­­­¸¸¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÃÃÃ!!!!!!ÿÿÿÿò\031\031\031\031\031\031\031\031\031\031ðððððåååååå!!!!!ÿÿÿÿÿòò\031\031\031\031\031\031\031\031ðððððððååååå!!!!ÿÿÿÿÿÿòòò\031\031\031\031\031\031\031ððððððððååååÿÿÿÿÿÿÿòòòòòòòòððððððððððââååÿÿÿÿÿÿòòòòòòòòððððððððãããããã££££ÿÿþþþþððããããããããããã####>>>>>>>ãããããããããããÛ####>>>>>>>ããããããããããÛÛ####>>>>>>>ãããããããããÛÛ####>>>>>>>ÛÛÛÛ###GGGGGGGG???ÛÛÛ¥¥¥GGGGGGGG???ÙÙÙ¥¥¥GGGGGGG4444\007\007zzzzzzÙÙÙÙÙÙ¥¥¥JJJJJJJ4444\007\007\007zzzzzzz{{{ÙÙÙÙÙ****JJJJJJ4444\007\007\b\bzzzzz{{{{ÙÙÙÙÙ****JJJJJJ444\b\b\b\b\b3zzz{{{{{{Ù\024\024\024*****JJJTTTTT\b\b\b\b\b332{{{{{{{\024\024\024\024******TTTTTTTT\b\b222222||||||\024\024\024\024**°°°TTTTTTTTTT2222222ÑÑÑÑÑ||\024\024\024°°°°°°TTTTTTTT]2222222ÑÑÑÑÑÑÑÓÓÓ°°°°°°°TT]]]]]]]]]dddÑÑÑÑÑÑqÓÓÓÓ°°°°°°SSSS]]]]]]]dddddqqqqqqÓÓÓÓ°°°°°°SSSSS]]]]]ddddddqqqqqqÓÓÓÓ¯¯¯¯¯¯SSSSSSSSddddddddkkkkkkÓÓÓÓ¯¯¯¯¯¯SSSSSSSS^______kkkkkkkÆÆÆÆ¯¯¯¯¯¯SSSSSSSS_______kkkkkÆÆÆÆÆÆ¯¯¯¯¯¯®®SSSS_________kkkkÆÆÆÆÆÆÆ¯®®®®®®®®­­­­________kkkÆÆÆÆÆÆÆÆ®®®®®®®®­­­¸¸¸¸¸¸¸¸¸ÃÃÃÃÆÆÆÆÆÆÆÆ®®®®®®®­­¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÄÄÄÄ®®®­­­­­­¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÄÄÄ­­­­­­­­¸¸¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÃÃÄÄ!!!!!ÿÿÿÿÿÿ\031\031\031\031\031\031\031\031\031\031ððððððååååå!!!!ÿÿÿÿÿÿÿ\031\031\031\031\031\031\031\031\031ðððððððååååå!!!ÿÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031ðððððððððåååå!!ÿÿÿÿÿÿÿÿÿÿÿöööööðððððððððððåååÿÿÿÿÿÿÿÿÿÿÿþööööööððððððððããããã####ÿÿþþþþþþþþþööööððããããããããããã#####>>þþþþþþãããããããããããä####>>>>>>þþããããããããããää####>>>>>>>ãããããããããää####>>>>??????äää###GGGGG??????ää¢¢¢GGGGG??????ÙÙÙ¥¥GGGGGGG444444zzÙÙÙÙÙ***JJJJJFF444444\007zzzzzz{{{{ÙÙÙÙÙ****JJJJFFF4444\b\b\b\bzzzz{{{{ÙÙÙÙÙ****JJJJF¦¦¦¦¦\b\b\b\b\b\bzz{{{{{{\024\024\024\024*****JJK¦¦¦¦¦¦\b\b\b\b\b\t\t|||||||\024\024\024\024******KKKKKKKK\b\b222222||||||\024\024\024\024*****KKKKKKKKKK2222222|||||||\024\024\024°°°°°°KKKKKKKK]2222222|||||||ÓÓÓ°°°°°°KKKK]]]]]]]ddddqqqqqqqÓÓÓÓ°°°°°°SSSSS]]]]ddddddqqqqqqqÓÓÓÓ¯¯¯¯¯SSSSSSSSS]ddddddqqqqqqqÓÓÓÓ¯¯¯¯¯¯SSSSSSSSdddddddkkkkkkkÓÓÓÓ¯¯¯¯¯¯SSSSSSSS_______kkkkkkkÆÆÆÆ¯¯¯¯®®®SSSSSSS_______kkkkkkÆÆÆÆÆ¯®®®®®®®®SSS_________kkkkkÆÆÆÆÆÆ®®®®®®®®­­­­­­______``kkÆÆÆÆÆÆÆÆ®®®®®®®­­­­­­­¸¸¸```````ÆÆÆÆÄÄÄÄ®®®®®­­­­­­¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÄÄÄÄÄÄ®®®­­­­­­­¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÄÄÄÄÄ­­­­­­­­­­¸¸¸¸¸¸¸¸¸¸ÃÃÃÃÃÃÃÃÄÄÄÄ!!!!!ÿÿÿÿÿÿ\031\031\031\031\031\031\031\031\031\031ððððððååååå!!!!ÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ððððððððåååå!!!ÿÿÿÿÿÿÿÿÿ\031\031öööööðððððððððååååÿÿÿÿÿÿÿÿÿööööööööðððððððððåååÿÿÿÿÿÿÿÿþööööööööðððððððããããã###ÿÿþþþþþþþþþööööööðããããããããããã#####þþþþþþþþþþöããããããããããää#####þþþþþþþþþþãããããããããäää#####>þþþþþþþþãããããããääää####¢>????????ääää¢¢¢¢¢G????????äää¢¢¢¢¢G????????ÙÙ¢¢¢GGGG???44444ÙÙÙÙ****JJJFFFFFF4444zzz{{{{{ÙÙÙÙÙ*****JJFFFFFFF4\b\b\b\b\bzz{{{{{ÙÙ\024\024\024*****JJFFF¦¦¦¦\b\b\b\b\b\b\b{{{{{{Ù\024\024\024\024******KK¦¦¦¦¦¦\b\b\b\b\b\t\t|||||||\024\024\024\024******KKKKKKKK\b\b\t\t\t\t\t|||||||\024\024\024\024*****KKKKKKKKKK\t\t\t\t\t\t||||||||\024\024\024§§§§§KKKKKKKKKK\t\t\t\t\t\t2|||||||ÓÓÓ§§§°°LLLLLLLLLdddddddqqqqqqqÓÓÓÓ°°°°°SSSSSSSSSdddddddqqqqqqqÓÓÓÓ¯¯¯¯¯SSSSSSSSSdddddddqqqqqqqÓÓÓÓ¯¯¯¯¯SSSSSSSSSdddddd_kkkkkkkÓÓÓÓ¯¯¯¯¯®SSSSSSSS_______kkkkkkkkÆÆÆ®®®®®®®SSSSSSS_______kkkkkkkÆÆÆÆ®®®®®®®®®SSS_________`kkkkkÆÆÆÆÆ®®®®®®®®­­­­­­_``````````ÆÆÆÆÆÆÄ®®®®®®­­­­­­­­­``````````ÆÆÆÄÄÄÄ®®®®®­­­­­­­­¸¸¸¸¸¸¸``ccccÄÄÄÄÄÄ®®®­­­­­­­­­¸¸¸¸¸¸¸¸¸.cÃÃÄÄÄÄÄÄÄ­­­­­­­­­­­¸¸¸¸¸¸¸¸¸¸.ÃÃÃÃÄÄÄÄÄÄ!!!!!ÿÿÿÿÿÿ\031\031\031\031\031\031\031\031\031ðððððððåååååÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031ðððððððððååååÿÿÿÿÿÿÿÿööööööööððððððððååååÿÿÿÿÿÿÿÿööööööööððððððððððååÿÿÿÿÿÿÿþööööööööðððððððãããããÿþþþþþþþþþöööööööããããããããããã#####þþþþþþþþþþþööööãããããããããäää####¢þþþþþþþþþþþããããããããããäää¢¢¢¢¢¢þþþþþþþþþãããããããääää¢¢¢¢¢¢????????ääää¢¢¢¢¢¢????????äää¢¢¢¢¢¢????????ä¢¢¢¢¢????555555ÙÙ*****FFFFFFFF555@@ÙÙÙÙ*****FFFFFFFFF55\b\b\bÙ\024\024\024\024\024******FFFF¦¦¦¦¦\b\b\b\b|||\024\024\024\024\024******KKK¦¦¦¦¦¦\b\t\t\t\t\t|||||||\024\024\024\024*****KKKKKKKKKK\t\t\t\t\t\t||||||||\024\024\024§§§§§KKKKKKKKKK\t\t\t\t\t\t||||||||\024\024\024§§§§§LLLLLLLLLK\t\t\t\t\t\t\n|||||||}ÓÓ§§§§¨LLLLLLLLLL111\n\n\n\nqqqqqqÓÓÓÓ¨¨¨¨¨LLLLLLLLLdddddd\n\nqqqqqqÓÓÓÓ¨¨¨¨¨SSSSSSSSSdddddddqqqqqqqÓÓÓÓ¨¨¨¨¨SSSSSSSSS_______kkkkkkkÓÓÓÓ®®®®®®SSSSSSSS_______kkkkkkkkpoo®®®®®®®SSSSSSS______``kkkkkkkoÆÆ®®®®®®®®®SSS_____``````kkkkkÆÆÆÆ®®®®®®®®­­­­­­_`````````ccllÄÄÄÄ®®®®®®­­­­­­­­­````````ccclÄÄÄÄÄ®®®®®­­­­­­­­­¸¸¸¸¸.cccccclÄÄÄÄÄ®®­­­­­­­­­­­¸¸¸¸¸¸......cÄÄÄÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸¸.......ÄÄÄÄÄÄÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031ðððððððððååååÿÿÿÿÿÿÿÿööööööööðððððððððåååÿÿÿÿÿÿÿÿööööööööðððððððððåååÿÿÿÿÿÿÿÿööööööööððððððððððîîÿÿÿÿÿÿÿþööööööööððððððððãããîÿþþþþþþþþþöööööööããããããããããä####þþþþþþþþþþþþööööãããããããããäää¢¢¢¢¢þþþþþþþþþþþþ\032ãããããããããääää¢¢¢¢¢¢þþþþþþþþþãããããäääää¢¢¢¢¢¢????????ääää¢¢¢¢¢¢????????äää¢¢¢¢¢¢????????5ä¡¡¡¡¡???5555555æ¡¡¡¡¡FFFFF55555@@@ææ*****FFFFFF555@@@@\024\024\024\024\024*****FFFFFFFF@@@@@||\024\024\024\024§§§§§§KKKKKK¦¦¦\t\t\t\t\t\t||||||||\024\024\024§§§§§§KKKKKKKKK\t\t\t\t\t\t\t||||||||\024\024§§§§§§LKKKKKKKK\t\t\t\t\t\t\t||||||||çç§§§§§§LLLLLLLL11111\n\n\n\n|||||}}}}¨¨¨¨¨¨LLLLLLLL11111\n\n\n\nqqqq}}}ÓÓ¨¨¨¨¨¨LLLLLLLLL111\n\n\n\n\nqqqq}}ÓÓÓ¨¨¨¨¨¨SSSSSSSSSddddd\nqqqqppppÓÓÓ¨¨¨¨¨¨SSSSSSSS_______kkkkpppppÓÓ¨®®®®®SSSSSSSS_______kkkkkkooooo®®®®®®®®SSSSSS_____````kkkkooooo®®®®®®®®®SSS_____``````cccllllll®®®®®®®®­­­­­­`````````cccllllÄÄ®®®®®®­­­­­­­­­```````cccclllÄÄÄ®®®®®­­­­­­­­­­¸`....cccccllÄÄÄÄ®®­­­­­­­­­­­­­¸.........bbÄÄÄÄÄ­­­­­­­­­­­­­­¸¸..........bbÄÄÄÄýýýýýýýýööööööïïïïïïïïïïïïåÿÿÿÿÿÿÿöööööööööïïïïïïïïïïåÿÿÿÿÿÿÿöööööööööðïïïïïïïïîîÿÿÿÿÿÿÿöööööööööðïïïïïïïîîîÿÿÿÿÿþþöööööööööððððððîîîîîþþþþþþþþöööööööööðããããîîîîî¢¢¢ þþþþþþþþþþþþööö\032\032\032\032\032\032\032\032äääää¢¢¢¢¢þþþþþþþþþþþþ\032\032\032\032\032\032\032\032\032\032äääää¢¢¢¢¢¢þþþþþþþþþ\032\032\032\032\032\032äääää¢¢¢¢¢¢ ???????äääää¢¢¢¢¢¢????????ääää¡¡¡¡¢¢??55555555ææææ¡¡¡¡¡¡?555555555@ææææ¡¡¡¡¡¡F5555555@@@@æææææ¡¡¡$$$$F5555@@@@@@æææææ§§§$$$$FFFF@@@@@@@ççç§§§§§$$$KKKK@@@@@\t\tççç§§§§§§$KKKKKKK\t\t\t\t\t\t\tççç§§§§§§LLLLLLLL1111111ççç§§§§§§LLLLLLLL111111\n\n\n}}}}ç¨¨¨¨¨¨LLLLLLLL11111\n\n\n\n\n}}}}}}}}¨¨¨¨¨¨LLLLLLLMMM11\n\n\n\n\n\013p}}}}}}}¨¨¨¨¨¨)SSSSMMMMMMM\n\013\013\013\013\013ppppp}}}¨¨¨¨¨¨)SSSSMMMMRRRRRR\013\013\013pppppooo¨¨¨¨¨))SSSSSSMRRRRRRRR\013kpooooooo®®®®®®®®SSSSSRRRRRRR````oooooooo®®®®®®®®®®SSRRRRR``````cccllllll®®®®®®®­­­­­­­````````ccccllllll®®®®®®­­­­­­­­­``````cccccllllll®®®®®­­­­­­­­­­`.....ccccbbllllÄ®®­­­­­­­­­­­­­.........bbbbbÄÄÄ­­­­­­­­­­­­­­­...........bbb\023\023\023ýýýýýýýý÷÷÷÷÷÷÷ïïïïïïïïïïïïïýýýýýýýööööööööïïïïïïïïïïïïýýýýýýýööööööööïïïïïïïïïïîîýýýýýýýööööööööïïïïïïïïïîîîýýýýýööööööööööïïïïïïîîîîîþþþþþþþþöööööööö\032\032\032\032\032îîîîîî¢¢      þþþþþþþööö\032\032\032\032\032\032\032\032\032îîîîî¢¢¢¢       þþþþþþ\032\032\032\032\032\032\032\032\032\032\032ääää¢¢¢¢¢       þþþþ\032\032\032\032\032\032\032\032\032ääää¢¢¢¢¢        \032\032\032\032ääää¡¡¡¢¢¢      5\032æææææ¡¡¡¡¡¢¢55555555ææææææ¡¡¡¡¡¡555555555@@ææææææ¡¡¡¡¡$$555555@@@@@ææææææ¡¡$$$$$$$555@@@@@@æææææ§§$$$$$$$$$@@@@@@@ççç§§§§$$$$$$$$666666çççç§§§§§$$$$$$$$6611111çççç§§§§§§LLLLLLLL1111111çççç§§§§§§LLLLLLLL111111\n\n\n}}ççç¨¨¨¨¨¨LLLLLLLLM1111\n\n\n\n//}}}}}}}¨¨¨¨¨¨)LLLLLMMMMM1\n\n\n\n\013\013\013}}}}}}Õ¨¨¨¨¨))))))MMMMMMM\n\013\013\013\013\013\013pppp}}Õ¨¨¨¨¨))))))MMMMMRRRR\013\013\013\013\013ppppooÕ¨¨¨¨))))))))MMRRRRRRR\013\013\013pooooooo®®®®®))))))))RRRRRRR````ooooooon®®®®®®®®®)))RRRRR``````cccllllnn®®®®®®®­­­­­­­````````ccccllllll®®®®®­­­­­­­­­­``````cccccllllll®®®®­­­­­­­­­­­`.....cbbbbblllll®®­­­­­­­­­­­­­.........bbbbb\023\023\023¬¬¬­­­­­­­­­­­­...........bb\023\023\023\023ýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïïïïïýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïïïïïïïïýýýýýýýý÷öööööööïïïïïïïïïïîîýýýýýýýýööööööööïïïïïïïïïîîîýýýýýýýööööööööïïïïïïïîîîîî      þööööööööö\032\032\032\032\032îîîîîî¢¢            öööö\032\032\032\032\032\032\032\032\032îîîîî¢¢¢           þþ\032\032\032\032\032\032\032\032\032\032\032\032îîîî¢¢¢¢          \032\032\032\032\032\032\032\032\032ääää¡¡¡¢         \032\032\032\032\032\032\032ääää¡¡¡¡¡        \032æææææ¡¡¡¡¡¡¡5555555ææææææ¡¡¡¡¡¡¡555555@@ææææææ¡¡¡¡¡$$$555@@@@@@@ææææææ¡¡$$$$$$$$$6@@@@@@æææææ§§$$$$$$$$$66666@@ççç§§§§$$$$$$$$66666AAçççç§§§§$$$$$$$$EEAAAAAAçççç§§§§§§$LLLLEEEEEE111çççç§§§§§§LLLLLLEEEE1111\n\n}çççç¨¨¨¨¨¨LLLLLLEEEE1111\n/////}}}}}}¨¨¨¨¨¨))LLLMMMMMMM1\n\013\013////}}}}ÕÕ¨¨¨¨¨))))))MMMMMMM0\013\013\013\013\013//pp}ÕÕÕ¨¨¨¨¨))))))MMMMMRRRR\013\013\013\013\013pppoÕÕÕ¨¨¨)))))))))MMRRRRRRR\013\013\013\foooooon®®®))))))))))RRRRRRRR``\f\fooonnnn®®®®®)))))))RRRRRRR```cccclnnnnn®®®®®®­­­­­­­RR````QQcccccllllnn®®®®­­­­­­­­­­```QQQQcccbbbllllm®®®¬¬¬¬¬¬¬¬¬¬­­QQQQQQcbbbbbbllmm®¬¬¬¬¬¬¬¬¬¬¬¬¬­.........bbbb\023\023\023\023¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬..........bbm\023\023\023\023ýýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïïïïïýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïïïïïýýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïïïïïïíîýýýýýýýýýýööööööïïïïïïïïïïîîîýýýýýýýýýööööööïïïïïïïïîîîîî        öööööö\032\032\032\032\032\032\032îîîîîî            öö\032\032\032\032\032\032\032\032\032\032îîîîî¢¢              \032\032\032\032\032\032\032\032\032\032\032\032îîîî¢¢¢           \032\032\032\032\032\032\032\032\032\032\032\032îî¡¡¡¡         \032\032\032\032\032\032\032\032\032æææ¡¡¡¡¡   \032\032æææææ¡¡¡¡¡¡ææææææ¡¡¡¡¡¡@@@@ææææææ¡¡¡¡¡$$$$$66666666ææææææ   $$$$$$$$666666æææææ   $$$$$$$$666666çççç§§§§$$$$$$$$6666AAçççç§§§§$$$$$$$$777AAAAAçççç§§§§§$$$$EEEEEEAAAAçççç§§§§§LLLEEEEEEEAAA}çççç¨¨¨¨¨¨¨LLLEEEEEEE000///////}}}}}¨¨¨¨¨¨))))MMMMMM0000\013\013/////}ÕÕÕÕ¨¨¨¨¨))))))MMMMMM00\013\013\013\013////}ÕÕÕÕ¨¨¨¨¨))))))MMMMMNNNN\013\013\013\013\013\f\f\fÕÕÕÕ¨¨¨)))))))))MMNNNNNNN\013\f\f\f\f\f\fnnnn¨))))))))))))NNNNNNNN\f\f\f\f\f\fnnnnn®®))))))))))NNNNNNNQQQQ\f\f\f\fnnnnn®®®®­­­­­­­­NNNNQQQQQQQQcclnnnnn®®¬¬¬¬¬¬¬¬¬¬¬¬`QQQQQQQQbbbbbmmmm®¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQQQQQQbbbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬........bbbbmmm\023\023¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬..........bbm\023\023\023\023ýýýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïïïïïýýýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïïïïíýýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïïïïïíííýýýýýýýýý÷÷÷÷÷ïïïïïïïïïïííîîýýýýýýýýüüüüüüïïïïïïíííîîîî      üüüüüü\032\032\032\032íííííîîîî           üü\032\032\032\032\032\032\032\032\032\032îîîîî              \032\032\032\032\032\032\032\032\032\032\032\032îîîî¡¡          \032\032\032\032\032\032\032\032\032\032\032\032îî¡¡¡¡      \032\032\032\032\032\032\032\032\032æææ¡¡¡¡¡\032\032\032\032\032æææææ¡¡¡¡¡ææææææ¡¡¡   ææææææ      $$$$66666666ææææææ     $$$$$$666666ææææææ    $$$$$$$666666çççç§§§$$$$$$$$777777Aççççç$$$$$$77777AAAAççççç$$$EEEEEEAAAAçççççEEEEEEEEEAAAççççEEEEEEEE00000///////èèèè¨¨¨¨¨))))))MMM000000\013///////ÕÕÕÕ¨¨¨¨))))))))MMM00000\013//////ÕÕÕÕÕ¨¨¨¨))))))))MMMNNNNNN\013\013\f\f\f\f\fÕÕÕÕ¨¨¨)))))))))MMNNNNNNN\f\f\f\f\f\f\fnnnn¨))))))))))))NNNNNNNN\f\f\f\f\f\fnnnnn))))))))))))NNNNNNNQQQQ\f\f\f\fnnnn~®))))©©©©©©©©©NNQQQQQQQQQ\rnn~~~~¬¬¬¬¬¬¬¬¬¬¬¬¬©©QQQQQQQQQbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQQQQQQQQbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQ.....bbbbmmmmm\023¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.........bbmm\023\023\023\023ýýýýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïïïïïýýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïïïííýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïïïíííííýýýýýýýüüüüüüüïïïïïïíííííííýýýýüüüüüüüüüïïïïííííííîî   üüüüüüüüü\032\032\032íííííííîî        üüüüü\032\032\032\032\032\032\032\032\032íîîîî             üü\032\032\032\032\032\032\032\032\032\032\032îîîî¡¡          \032\032\032\032\032\032\032\032\032\032\032\032îî¡¡¡    \032\032\032\032\032\032\032\032\032æææ¡¡¡¡\032\032\032\032æææææ     æææææææ      æææææææ       $$$6666666ææææææ     $$$$$$666666ææææææ     $$$$$$666666ëëëëë   $$$$$$$$777777Açççç$$$$$$77777AAAAçççç$$$EE7777AAAAççççEEEEEEEEEAAAèèèèEEEEEEE00000èèèè))))))EEE0000000èèèè¨¨¨)))))))))M00000000/////ÕÕÕÕ¨¨¨))))))))))NNNNNNNN\f\f\f\f\f\f\féééé¨¨)))))))))))NNNNNNNN\f\f\f\f\f\f\féééé))))))))))))©NNNNNNNNO\f\f\f\f\f\fnééé)))))))©©©©©©©NNNNOOOOOO\f\r\r\r~~~~ª)))©©©©©©©©©©©©QQQQQQQQ\r\r\r\r~~~~¬¬¬¬¬¬¬¬©©©©©©©©QQQQQQQQQ\r\rmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQQQQQQQQQmmmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQQQQQQQQbmmmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.....bbbbmmmm\023\023\023ýýýýýýýýýý÷÷÷÷÷÷÷÷÷÷÷ïïïïïïïï\033\033\033ýýýýýýý÷÷÷÷÷÷÷÷÷÷ïïïïïïïïï\033\033\033ýýýýýüüüüüüüüüïïïïííííííííýýýüüüüüüüüüüïïïíííííííííýýüüüüüüüüüüüïïííííííííí  üüüüüüüüüüü\032íííííííííí      üüüüüüü\032\032\032\032\032íííííííî           üüü\032\032\032\032\032\032\032\032\032\032\032îîîî¡     \032\032\032\032\032\032\032\032\032\032\032\032îî¡¡   \032\032\032\032\032\032\032\032\032ææ¡¡\032\032\032\032æææææ     æææææææ      ëëëëëëë       $$$6666666ëëëëëëë      $$$$$66666ëëëëëë      $$$$$777777ëëëëëë    $$$$$$7777777Aëëëë$$$$7777777AAAçççç$777777888AAèèèèEEEEE888888AèèèèEEEEE880000BBèèèè)))EEE000000BBBBBèèèè))))))))))0000000BBBBééé¨)))))))))))NNNNNNDDDDéééé)))))))©©©©©NNNNNDDDD\f\f\fééé)))))©©©©©©©©NNNNDDDD\f\f\f\féééªª)))©©©©©©©©©©NNNOOOOOO\r\r\r\r\r~~~ªªªª©©©©©©©©©©©©OOOOOOOO\r\r\r\r\r~~~ª¬¬¬¬¬©©©©©©©©©©QQQQQQQQ\r\r\r\r\r\177\177\177(¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQQQQQQQQQPmmmaaa(¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQQQQQPPPPPmmaaaa(¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬QQPPPPPPPmmaaaaa\037\037\037\037ýýýýý÷÷÷÷÷÷÷÷÷÷÷÷ïïï\033\033\033\033\033\033\033\033ýýý÷÷÷÷÷÷÷÷÷÷÷÷ïïïí\033\033\033\033\033\033\033ýüüüüüüüüüüüüïííííííííííüüüüüüüüüüüüíííííííííííüüüüüüüüüüüüíííííííííííüüüüüüüüüüüüííííííííííí  üüüüüüüüüüû\032íííííííííí       ûûûûûûûû\032\032\032\032\032\032íííííûûûûûû\032\032\032\032\032\032\032ìììì\032\032\032\032\032\032ììììøøøøøìììì   øøøøëëëëë      ùùùëëëëëë       6666ëëëëëëë        $$$6666ëëëëëëë       $$777777777ëëëëëë      $$$777777777ëëëëë$7777777778A\034\034\034777778888888èèèè7788888888BèèèèE888888899BBBèèè88999999BBBBBèèè0999999BBBBéé©©9NNNDDDDDDéééª©©©©©©©NNDDDDDDDééªª©©©©©©©©©NDDDDDDD\rééªªªªª©©©©©©©©©©©NOOOOOOO\r\r\r\r\r~~~ªªªªª©©©©©©©©©©©OOOOOOOO\r\r\r\r\r\177\177\177ªªªª¬¬©©©©©©©©©©OOOOOOOPPP\r\r\016\177\177\177(((¬¬¬¬¬¬¬¬¬¬¬©QQQQQQPPPPPP\016\016aaa(((¬¬¬¬¬¬¬¬¬¬¬¬QQQQQPPPPPPP\016aaaa(((¬¬¬¬¬¬¬¬¬¬¬¬¬QQPPPPPPPPPaaaaa\037\037\037\037\037\037\037\037÷÷÷÷÷÷÷÷÷÷÷÷÷÷\033\033\033\033\033\033\033\033\033\033\037\037\037\037\037\037üüüüüüüüüüüüüíí\033\033\033\033\033\033\033\033üüüüüüüüüüüüíííííííí\033\033\033üüüüüüüüüüüüíííííííííííüüüüüüüüüüüüíííííííííííüüüüüüüüüüüûûííííííííííüüüüüûûûûûûûûûíííííííííûûûûûûûûûûûøøøííííííûûûûûûûûøøøøøøøììììûûûûûûøøøøøøøøììììûøøøøøøøøøøìììì  øøøøøøøøøøìììì    øùùùùùùùùëëëëë      \036ùùùùùùùùëëëëë        \036\036\036\036\036\036\036ùùùùùùùëëëëë        77777\036\036\036\036\036\036\036\036ùùùùùëëëëëë%%%%% 777777777\036\036\036\036ëëëëëë77788888888\034\034\034\034\0347888888888èèèè888888889Bèèè88888999BBBBBèè8999999BBBBBè&999999:BBBBé&&&:::::DDDDéªª©©©©©©&::DDDDDDéªªªª©©©©©©©©©&DDDDDDD;êªªªªªª©©©©©©©©©©OOOOOOOO\r\r\r\r\r\r\177\177ªªªªªª«©©©©©©©©©OOOOOOOO<\r\r\r\r\177\177\177ªªªªª««««©©©©©©©OOOOOOPPPPP\016\016\016\177\177(((((«««««««««««OOOOPPPPPPP\016\016\016\016a(((((«««««««««««Q''PPPPPPPP\016\016aaa((((((¬¬««««««««''''''PPPPPaaaaa\037\037\037\037\037\037\037\037\037÷÷÷÷÷÷÷÷÷÷÷÷÷\033\033\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037üüüüüüüüüüüüüüíí\033\033\033\033\033\033\033\033\037\037\037\037üüüüüüüüüüüüüííííí\033\033\033\033\033üüüüüüüüüüüüüííííííííííüüüüüüüüüüüüüííííííííííüüüüüüüüüüûûûûíííííííííüüüüûûûûûûûûûûíííííííííûûûûûûûûûûûûøøøøøììììûûûûûûûûûøøøøøøøììììûûûûûûûøøøøøøøøììììøøøøøøøøøøìììì øøøøøøøøøøìììì   øùùùùùùùùùëëëë    \036ùùùùùùùùùëëëë%%%    \036\036\036\036\036\036\036ùùùùùùùùëëëë%%%%%   \036\036\036\036\036\036\036\036\036\036\036ùùùùùùëëëëë%%%%%%%77\036\036\036\036\036\036\036\036\036\036\036\036\036ùù\034\034\034\034\034\034\034%%%%%888\036\036\036\036\036\036\036\036úúúúúú\034\034\034\034\03488888888úúúúúúúúú\034\034\034\03488888899úúúúúúè88899999BBúúú89999999BBBBú&&&99:::::BBª&&&&&&:::::;;êêêªªª©©©&&&&&:D;;;;\035Cêêêªªªªªª©©©©©©©&&&D;;;;;CCCCCêêêªªªªªªª©©©©©©©©©©DD;;;;<<CCCCCªªªªªª««©©©©©©©©©OOOOOP<<<<<<\177\177\177ªªªªª««««««««©©©OOOOOPPPPP<<\016\016\016\177(((((«««««««««««OOO'PPPPPPP\016\016\016\016\016(((((««««««««««««'''''PPPPP\016\016\016\016a((((((««««««««««'''''''''''aaaaa\037\037\037\037\037\037\037\037\037üüüüüüüüüüüüü\033\033\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037üüüüüüüüüüüüüüí\033\033\033\033\033\033\033\033\033\037\037\037\037\037\037üüüüüüüüüüüüüüííí\033\033\033\033\033\033\033üüüüüüüüüüüüüüííííííííí\033üüüüüüüüüüüüüûííííííííííüüüüüüüüüüûûûûûíííííííííüüüüûûûûûûûûûûûííííííííûûûûûûûûûûûûûøøøøøììììûûûûûûûûûûøøøøøøøììììûûûûûûûûøøøøøøøøììììøøøøøøøøøøììììøøøøøøøøøøìììì øùùùùùùùùùùììì%%%\036ùùùùùùùùùëëëë%%%%%%\036\036\036\036\036\036\036ùùùùùùùùëëëë%%%%%%%\036\036\036\036\036\036\036\036\036\036\036ùùùùùù\034\034\034\034\034%%%%%%%%\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036ùù\034\034\034\034\034\034\034%%%%%%\036\036\036\036\036\036\036\036\036\036\036úúúúú\034\034\034\034\034\034%%%8888888úúúúúúúúú\034\034\034\03488999999úúúúúúú89999999úúúúúú&9999999:úúúú&&&&&:::::\035\035\035\035êª&&&&&&&::::;\035\035\035\035\035êêêêªªª©©&&&&&&&;;;;;\035\035\035\035êêêêªªªªªª©©©©©©&&&&;;;;;;CCCCCêêêªªªªªªª©©©©©©©©©©;;;;;;<<CCCCªªªªªª««««««©©©©©O;;PP<<<<<<ªªªªª«««««««««©©©OOPPPPP<<<<\016\016\016(((((«««««««««««O''''PPPPPP\016\016\016\016\016(((((««««««««««««'''''''''P\016\016\016\017\017((((((««««««««««''''''''''''\017\017\017\017\037\037\037\037\037\037\037\037\037üüüüüüüüüüüü\033\033\033\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037üüüüüüüüüüüüüü\033\033\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037üüüüüüüüüüüüüüí\033\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037üüüüüüüüüüüüüüíí\033\033\033\033\033\033\033\033üüüüüüüüüüüüûûûíííííííííüüüüüüûûûûûûûûûûííííííííüüüûûûûûûûûûûûûûíííííííûûûûûûûûûûûûûøøøøøììììûûûûûûûûûûøøøøøøøììììûûûûûûûûøøøøøøøøììììøøøøøøøøøøììììøøøøøøøøøøììììøùùùùùùùùùùììì%%%\036ùùùùùùùùùùëëë%%%%%%\036\036\036\036\036\036\036ùùùùùùùù\034\034\034\034%%%%%%%\036\036\036\036\036\036\036\036\036\036\036ùùùùùù\034\034\034\034\034%%%%%%%%\036\036\036\036\036\036\036\036\036\036\036\036\036\036\036ùù\034\034\034\034\034\034\034%%%%%%\036\036\036\036\036\036\036\036\036\036\036úúúúú\034\034\034\034\034\034%%%\036\036\036\036\036\036\036úúúúúúúúú\034\034\034\034%999999úúúúúúúú999999úúúúúúúú&&99:::::úúúúú&&&&&&::::\035\035\035\035\035\035êêª&&&&&&&:::;;\035\035\035\035\035êêêêªªª&&&&&&&&;;;;\035\035\035\035\035êêêêªªªªªª©©&&&&&&;;;;;;CCCCCêêêªªªªªªª««©©©©©©©&;;;;;;<CCCCCªªªªªª««««««««©©©;;;PP<<<<<<ªªªªª««««««««««©©©PPPPP<<<<<(((((««««««««««««'''''PPP<<<\016\016\016\016(((((««««««««««««''''''''''\016\017\017\017\017((((((««««««««««''''''''''''\017\017\017\017".getBytes(StandardCharsets.ISO_8859_1)
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
        colorCount = plen;
        populationBias = Math.exp(-1.375/colorCount);
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
        colorCount = palette.length;
        populationBias = Math.exp(-1.375/colorCount);
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
        colorCount = plen;
        populationBias = Math.exp(-1.375/colorCount);
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
            colorCount = i;
            populationBias = Math.exp(-1.375/colorCount);
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
            colorCount = i;
            populationBias = Math.exp(-1.375/colorCount);
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
        float halfDitherStrength = (float) (ditherStrength * this.populationBias);
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
        float xi1, xi2, w1 = (float) (ditherStrength * populationBias * 0.25), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f;
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
        double ditherStrength = this.ditherStrength * this.populationBias, halfDitherStrength = ditherStrength * 0.5;
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
        float w1 = (float)(ditherStrength * populationBias * 0.125), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f;
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
        float adj, str = (float) (ditherStrength * populationBias * 0x2.5p-27);
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
        float adj, str = (float) (-0x3.Fp-20 * ditherStrength * populationBias);
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
        float pos, adj;
        final float strength = (float) (ditherStrength * populationBias * 3.333);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    color |= (color >>> 5 & 0x07070700) | 0xFF;
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF];
                    pos = (px * 0.06711056f + y * 0.00583715f);
                    pos -= (int) pos;
                    pos *= 52.9829189f;
                    pos -= (int) pos;
                    adj = (pos * pos - 0.3f) * strength;
                    rr = MathUtils.clamp((int) (rr + (adj * (rr - (used >>> 24       )))), 0, 0xFF);
                    gg = MathUtils.clamp((int) (gg + (adj * (gg - (used >>> 16 & 0xFF)))), 0, 0xFF);
                    bb = MathUtils.clamp((int) (bb + (adj * (bb - (used >>> 8  & 0xFF)))), 0, 0xFF);
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

    
    public static final byte[] RAW_BLUE_NOISE = "\021ñDø\030¶àö%3\nò\020Õ°å2Ù)P\024þÄõ9\nXåÁ4Ì$ÿ\021±ÚZÊò»\034YÅé<Ôþä³\002\025òüDÈ&\bù½\000àõÒ:Ù_¬ä5\bG§\000Y\030À9K\007ð¼\016Êøâ\\,ºßEÔ\001\036\016êP\006!åý\024MØù!\r'\t.ËR4Á\f¬\024î\032ÏZ\025¡'\032Â\001\013%ÌÿÖ\037\rÑä±ê#Ëú\025c\002D\035Ò\004\035\017É!ó¯ùÜCºÈô\030F\005$á/¤ÐKûÛB\035÷Úç%d×öN³Ü+7ï´Rù4ì\024Wéò;Æ*ùA\0020Û%§è5\021ñ@èqü.\025;[-Õüp+ÀÒë´\0079ï±ã\025Áa¨\021ÿ\007á.\001;ç\013Ç\005B\016ã¹ÔKô)¾P\023\006k×\034âWìÃöÖ\b·$ù­Ø\007ì¾á\004ï\032\0138ìÝ\r>õ]\020\000\031n\0044òê\006ÑKî?\035Ã\016ô!üÚêÌ#\006<\034Ý\017\0040üÜï¢\rô´\021\b;\033ÿiÞJÌ\0307 RÒ\f)Ëÿä \024²ú2\036ÈÛè*Ê\017µ\";,\032»ùÎë$aÔI­o.¥\027ïÄý¸uÊä\035·$6ÍO-ýÐI³+Çí0\rã\001&öD ªb?Â'O\002Ë\030ãLü½?÷×SàüÄÜ\r2U¦Þþ\024¿\034ò\021ÿXÚ-Fèø6\nÓEé\026\004Áæ ßð\013\027û¾að¶\022ÄèûÚ\021÷\034Ôðc\tÕ,ò\024\"\bG\034\001¢\024ôv®\001\026\tD+î\005>ßÐ9÷\t\023Ñ\032 ò]\000­øÝ>òf\026ù6Yå\004 CÑ\0332Þ\000\0304ð·Iè\007:£#êµ\004eÑî°æ1ÍD\005é'âòÓø¸Éå\f\037¾æf%­\003A'¿\017-ÈK(\016¹Ø\005%Ìª×OóÜ\007ëYÕM\t\"Î«,àÄþE\016¦1áþ)\f_ï%ÕMÇ5¼\0328Z#ùR\003*\016úñàUìÙûà\031ð\b¤\033ÿ8Ãè\020\000\032)°\021-?ù\016\005¼+ßi\001ù\017Tô\0256ÛùÇI\021ÀÛù¸\t\033<\020ý eç\017Ùª3ÔëAÉ×8»\026È\007;PÖoåÏíS/Eî¿9ËéÿÄ«'Ë=õ\023ì7\032Ú¹ çÎX\032ñ Q\031È8ã½÷íØ\n\002ïü\027Ãó\023°\033N \000ö1ª\036ê\0023\"ûD)\024ö\tÜ_û\013õT\026ãtñ\036æýÓÃDË3\n\005½ä\t8Õ\002ô,\022ÿ[Ì-²Cà0ÌE\007'cþá\006éÐ]&\rÐ·÷\020Ã°\fÚÉã\036¸Ñ\"àBÔ\"\tÛJ¡\031V)\006åòÿs'ú@+ªûé?ßmë \004N\024ò$ÀU£ÞèºI /Â\n\022ãÚòD`\027Þ>ó\004§eþ=\003¹\0242\001\034´ú/º\020\0028±\fø\\\027¬ëÒ\022ïØe\025Ê$¬\006ÑDÚ¦äûÑ\006\030÷\020<\036Ï\f\030ô})úIÁ\004æþÊ,ëY\0354\027¾ñN&åjÂî\0057Îì÷ÆØïá#»/Ý:ÄL\"·\0032¾\016ï\035ú\f(\027Á:q«é*Óñ\0025ùÙ>\002î±\033:#6¨\t¶Òßøé,Ö\nìøÍ\017ÛW\024C^!-O\022ÊÖ\003\017\035õâ¡\röáTýIº5Æò2ë\n\036Ü6ý´hÅ&å©ÊÞ1Ó\bö\022Ö\034ùE&\016GÌ\0218²E.ô#çÿÞ«\005æý<LùG\bþ^\031Îì)Õ\026æÝO®ýVõÉ\003Nä\027\tìY\022!O\026üUëÈmîáQ\001ðÄ\006!ÿWÞ\034ü\007£\027<Å\r*\027Ñ½\034ó\bã%îÎº,Ú4A²\005;ö#\003\020ØÎ&\022B½\r\"Ø/Cý»\0077æ\016´(>¼\016Í\025<yã¹õ)ÇP×â\001ºÕhõí?\0135_³\030Âi6ç\020ðÃú \021hÉ\n¤`\034çG\000áï-¤ùÊô¬àÒõïÀ+\001\036Üû\0043#è-®Øï7\027\004ê\016 +îKû\036·¢TúÚÍë/þ\023Ö!\002S\tåÜñþ,Òî»/\b³\030]Óé@U\005\033(\rG\030ÙaÎF¢\031ÑóWÝþ÷\035\rÐ¢d½óª\\É\n3Ü\0070ãÄ\021)\004Þ¥\nö¯C\030Õ'¨G5´\030â>úÅó<û\007\035\023ÝÀ9é \001ã!\tøñã^\nÄ«\022B¿\0071KüÜ.@Òø\022æB\025\001$ò\036BðM9âZüÉ1õ\001Ï\035éL\004\022&Þj Ú5Ã)´\000ð\021cË0ü¥?±4\023*êG\002\037Ô`¦êÅ\"\023ç\007\031:°'óËêÓJ¯þ|\rÒ\032È¶*\tÞês\025À\013ùÇX¯\f\002Íí\017äôr#ÖøµQÔí\020Ê\006½Ùù/ãï\016(à\002öEÃýß\004p\033ûV¾\nà8è¼ú#\003ëó\036\017¸@à\\0Ûò\037Ôç.\025¹S\004=Ð\013Jå5\t\025Â.iæ$ý<\034\fË´=úÎP\033¸Õ\r'Rï»Ö\017©*4\023÷É\030Ù3©`\025ÐQ<ý\"î\006¡%\020A\0008ø¢Aý%¨ß-í\031Ä\003'ñH\000Û\032RïÏLóp\"\005\0279í\bl7êÌ\0362Gä\000ÜîN(\005Tôã\013.Ü¾\004Ç×,öÒý¹ìË\030ÁMð×\033Çù\024þ«?ß¯\035¡\013õ¶\003à\024­ÚéÁVÛÿ¼$âô\001³\024÷\bÄ!b\006\035Öëµ\021@Åþ÷4\031ãY°:\034Nâm\n*ß\005\016åb\tI½ØXöÎ^è;)ÐC\0174Ã*ÿ6÷\013,ò\024/È\021¦Ù-^Ñ@õ¯ÎúA\000\000\037îÓ'E\020ìJó\n\022éÄ\007\026õ±Z#Ì2óé8\037\0130\022ü\005Åâ\025úêd\bæ\035HÐªäDÓøT\034H\005àí\020)ç\0266Á\016kß.\006»gå\001¶¤)Îÿ¨FÙ'\0024Öêû<\027¶(Ò¦\002äí#×qð2¿\037×ö¸\\\021\003&\031h\006°ê4\nÁú$9ý¥P\nÞ,æõÌ;û\022\034ÙÍ$\025Ýn4 úòÍç@\036\022¼\001Üø\005\020\\ò)ÉOº\030\bO\001ª&ÍAðÆØíüÃ\017!ÿäðÍ´\032ØÂ\001ñ]¸\"\031¨Qè¬ó6\013Tù\006¹ïá\017/b¿\f©Kñ,{AÆ\033àÀ=ÿ\r5öý!Úç\023Uý\f-â<¹2ßô:Ù`&\026B\013mäE\035Ó¸ü\004×\f&ÝÈIþê?Å\033ÕQµ\027þöáÈ\bÓæ­í&Gú\026ÓÝè±ÏF\fÈ0íÞ\030ø\007W\035OÊ\022¼\003Ó1¬õ\004/ê%\024;H¿ï\001\0260\007!¿à*ô8\003%\tíGÓ+W\0317 \r\000×\bµe\035J+á7ò\032¶\004:À#K­\022Ôþ\ní.øJàþì\023Ê\r½÷Þé2VödÔì\031^Ó\r£åüË5Ý\036\002ëú\023Põ7_å.ëô\001¨\023¾\004gøH\020Õ\000éÉð%åD®\032è Å[(Û7X\000MÉ\017\034Ñ+Ãâ¯:ø\022\000²J\025fÀ\021ô\006=ÂÙ%¹ßÍÀ\031\020Ï\"3Ç\nï'ûØ ãÌ(óyÜ1\003^À*\006Ðq\020\t:§\033üñÑâ)ô±\büB\020\002&ÉCï\037Ú÷)éZ±æ\016añC\0040ýï³\004LÚ;\\æ\025?¬\013ë³\027\037\r©ö\025Ùú=½ñØöæ\005G¸ \t\0265\003wÛç\037ñL\tç3Þ\006/Ï´=¡ü#Ï\026/¤\né\037V:øá\025þ\037Ð\007SÇ.ÿ[3\006û=ÑIâ9!ã\0012#SÐ¿\017êg¨ù×ì$<\025³ÌÙ½lõ\031ÁUì\013\003\032×1L÷ÿÉÖm\024Ý&Ó\fÁtêöµ¤ðß\021õÔCÅäî¼\032ýÈ\017ïVË\023³ÿ\027>1Ø\002Æ=S¼Ëö](û\023 Ôÿ®\021û$âHÅ\bï¶á\033>®÷Æ\001è\032@­+\016E2\002%¼\035¥Ý$\023V,\007êe/¸\013F¥ë*âø$\023ð+å\032\017\0000\006à7ì I(æ@x½ò\023Ý!S\003(î3\tF¥0ò\006×É\033øÑ9èM\003ð\f÷Ú¸&Ó\001öÛ\034ûÕh\fòËPà®þ\b¡KÔèÂ\016Fµ\003\fÈñ3ÜÒ\0309\000`4\féÂû\021ãºSüÌÞ\"ÿXã\024nüÉ\026-gÏ8\002K\022·\031Bè)\0067Ä\037I\006º\031aÏ\"Ý-ðTù\032Ïô-á[\036\001\bø,©æùÊ\026;Ú\\Ð!\026ì\017`5îº!\007Û\016@²âû¾\034åÆóà5¿ÎNðµ\000Ú.ë\f4õFú\023¸:¦$Ûd\027üÖ¨\025ÄMì\r¹(Ôô°,\bDó\002×*·ö\027Ã\rLó,®íö#Ö\bFì$\fYû\tw\024øÝ\017\031ç¡úCÖè±Æ\004\037â\n\001êÂ6î»?ä!Í[\033\007H\001kì\033ßÃ6ä:\007EçýÕÇå7\035ÂW0¬\023ÿ1¹\036Öê\003#/Ê^>%\022Â!\001\026;[ìÑo¿+?\022\006\"J\013.õ\003Ù@â£\"Ç\022ü³&\013oþ\036ÐÜ'¦=\021\001_Ð\013\002èóRÒÝBÍï*@¶äþ©ô\005Îröß\t&Ú\0165ôüÌNß÷ÐèþÛf\022+óýÁð\016åÖP?ø\024íÆJ¬ð\003\033RÞ\027ûÜ=È\033(÷\005\026\000MÆ\020ñQ\tÓ3Ýí¸0OËñ¶ÿ\032\021«ð\035µ[ \031È\007¶é­1\027W8+\005Ïç§Ù/ú\rX1¾÷$ï·A\037*\021á\t¼cç¥6â÷\033Ù8\037À\024T\f\033æü¤\036.RÞFç×3\000\r):ñ%OÔ: \fÎÛú»\034ò1\"Z\b\033»â\024Ìé\nÒ/Mô¿ëlúª4ð\020\"µ\013aÐ\003²êú(ÿH¾\024@Ô\004öÂ*!\005VÅæÕ\003¿áù\025\000ßI\bë\000e\017áÀ\000ó<Ó*ÿ¥8u²\020ä\007ÿÍ1×\004NÇÔJÜü&§-\021làAòÉ×)\006ícã\027\nÏ£ø<¯\027õs\0173BîÄlö(=ÇØC®\027ÌKêdõ\030Ú\004ýÈ:ØU\f \030$äÿ\035õ=ÀîãDôË\007¯\036ä9ö®\020Æ;ëþi\023íß-E¨ë\035Ð\013¹\036\003å\025µ\037\néú7'Þ\020\005!ÄJî\037ç+«\031#¹íõB®\r.\007\026S\fÿ\031Ø1\r\002f\031Ý3û\"L²2ÜÀ\013\036üÌ\bÛÿQó-Ù\020©ÔU.õQ\002î·ù5­à\f>Z\025ñùáH5\022ÜÁìpÍé3ÖÈ\0379YüîO½ëÎT\002ñØ\016\032óGÒU#¼>(å«]û4CñþàÏ\023 Õ\r_\032ÑEü(Î÷Û\006cÅ\004Òþ](ú\027ß\002©%øºßèÂ$Ò\023ø'\b\035â·+É\005å%5\002çð\021÷Æ\030\005Í#ëÈ\033\006²x4Ãã§+Çæð\023R\003»\rBÐ'\020è\035Ê\b;²M\016Añg\024\t,\004¤@á.HÂ§D\024dùP¯ø\026¶Ö/b\rFá»\013J%\016íø\005Ló>\t\0000´×\0322å\036îý;¸-Lòä\037Õ÷¾\034ä\000ÎLõ\030\013ñÿØ\017ôþÒç6 Î\tÝjA\034\003Þì6üò\0319øæ×Á=¬&\031ýÚº\037ZëôÅ\000°Q¿Üõ\013Ö\025ª\0031î*ËÙ<'í6Û®kÊ³Yé$=\013¾ï\000\021,ìÅ\f¢úÍ!´Õ'hÜ\002T+ý\025ÜéÏi\023÷ß\007%9j\024Ö,\b\027z\000ë5XÅ\021a\t°R\006\020·\035ýå\";\005\0261Þ¬\033BÙ\\ºôþ9äM2\023W\tÆ\022°Ïñ_\bH/\fí6ÁÑ?\017ú¢#ó8æÊ£E¼!ûÝæý\031ó§ùÕUÆ\0161íÔ\036ùÇu\003(ö¦ã3\036Ô&²\007ó¾æ\000Ií0\007\0307 âÆú´\"\001ä)Mè¶ÌáN\002«ú\034&âÏ\016ôF$Ò7HÞ0è\026A\003ø¾âP\nòæÑ\017ÄK\026\005S\017îÇ\037Ø>ö\034áûCèº\000\017AðØQÊ¦\027û\004\034F\013ÀÑ[\017?ñ\0060­ç\005\025»ì\rÅ#t½àñ(b\021\001?·!Gý9ì\nûÍèAÝ\030vü*\016°Î%¤Øn÷Ì,©\033\0228ô\n]¯òÛ.î\0262ÝìÕýQ\032>Êj-÷\003Ðþ\013\0377ÏÚ\031¨Í-Û\030µ+Öf\034¿÷\001ªÒCß3b\004\020Ã(\nÞXê\005¼þÞ Õ2Æ\"ý± \005Æ*g\022ÂíÙ þà V\030@ô£ë²\bHê%ûï\023\006`õá¢<ð0\f'6ç\n\002ð\026¼óN\032î=\037Ó1är>ê\020H\007kç".getBytes(StandardCharsets.ISO_8859_1);

    public static final byte[] TRI_BLUE_NOISE = "\021ñDø\030¶àö%3\nò\020Õ°å2Ù)P\024þÄõ9\nXåÁ4Ì$ÿ\021±ÚZÊò»\034YÅé<Ôþä³\002\025òüDÈ&\bù½\000àõÒ:Ù_¬ä5\bG§\000Y\030À9K\007ð¼\016Êøâ\\,ºßEÔ\001\036\016êP\006!åý\024MØù!\r'\t.ËR4Á\f¬\024î\032ÏZ\025¡'\032Â\001\013%ÌÿÖ\037\rÑä±ê#Ëú\025c\002D\035Ò\004\035\017É!ó¯ùÜCºÈô\030F\005$á/¤ÐKûÛB\035÷Úç%d×öN³Ü+7ï´Rù4ì\024Wéò;Æ*ùA\0020Û%§è5\021ñ@èqü.\025;[-Õüp+ÀÒë´\0079ï±ã\025Áa¨\021ÿ\007á.\001;ç\013Ç\005B\016ã¹ÔKô)¾P\023\006k×\034âWìÃöÖ\b·$ù­Ø\007ì¾á\004ï\032\0138ìÝ\r>õ]\020\000\031n\0044òê\006ÑKî?\035Ã\016ô!üÚêÌ#\006<\034Ý\017\0040üÜï¢\rô´\021\b;\033ÿiÞJÌ\0307 RÒ\f)Ëÿä \024²ú2\036ÈÛè*Ê\017µ\";,\032»ùÎë$aÔI­o.¥\027ïÄý¸uÊä\035·$6ÍO-ýÐI³+Çí0\rã\001&öD ªb?Â'O\002Ë\030ãLü½?÷×SàüÄÜ\r2U¦Þþ\024¿\034ò\021ÿXÚ-Fèø6\nÓEé\026\004Áæ ßð\013\027û¾að¶\022ÄèûÚ\021÷\034Ôðc\tÕ,ò\024\"\bG\034\001¢\024ôv®\001\026\tD+î\005>ßÐ9÷\t\023Ñ\032 ò]\000­øÝ>òf\026ù6Yå\004 CÑ\0332Þ\000\0304ð·Iè\007:£#êµ\004eÑî°æ1ÍD\005é'âòÓø¸Éå\f\037¾æf%­\003A'¿\017-ÈK(\016¹Ø\005%Ìª×OóÜ\007ëYÕM\t\"Î«,àÄþE\016¦1áþ)\f_ï%ÕMÇ5¼\0328Z#ùR\003*\016úñàUìÙûà\031ð\b¤\033ÿ8Ãè\020\000\032)°\021-?ù\016\005¼+ßi\001ù\017Tô\0256ÛùÇI\021ÀÛù¸\t\033<\020ý eç\017Ùª3ÔëAÉ×8»\026È\007;PÖoåÏíS/Eî¿9ËéÿÄ«'Ë=õ\023ì7\032Ú¹ çÎX\032ñ Q\031È8ã½÷íØ\n\002ïü\027Ãó\023°\033N \000ö1ª\036ê\0023\"ûD)\024ö\tÜ_û\013õT\026ãtñ\036æýÓÃDË3\n\005½ä\t8Õ\002ô,\022ÿ[Ì-²Cà0ÌE\007'cþá\006éÐ]&\rÐ·÷\020Ã°\fÚÉã\036¸Ñ\"àBÔ\"\tÛJ¡\031V)\006åòÿs'ú@+ªûé?ßmë \004N\024ò$ÀU£ÞèºI /Â\n\022ãÚòD`\027Þ>ó\004§eþ=\003¹\0242\001\034´ú/º\020\0028±\fø\\\027¬ëÒ\022ïØe\025Ê$¬\006ÑDÚ¦äûÑ\006\030÷\020<\036Ï\f\030ô})úIÁ\004æþÊ,ëY\0354\027¾ñN&åjÂî\0057Îì÷ÆØïá#»/Ý:ÄL\"·\0032¾\016ï\035ú\f(\027Á:q«é*Óñ\0025ùÙ>\002î±\033:#6¨\t¶Òßøé,Ö\nìøÍ\017ÛW\024C^!-O\022ÊÖ\003\017\035õâ¡\röáTýIº5Æò2ë\n\036Ü6ý´hÅ&å©ÊÞ1Ó\bö\022Ö\034ùE&\016GÌ\0218²E.ô#çÿÞ«\005æý<LùG\bþ^\031Îì)Õ\026æÝO®ýVõÉ\003Nä\027\tìY\022!O\026üUëÈmîáQ\001ðÄ\006!ÿWÞ\034ü\007£\027<Å\r*\027Ñ½\034ó\bã%îÎº,Ú4A²\005;ö#\003\020ØÎ&\022B½\r\"Ø/Cý»\0077æ\016´(>¼\016Í\025<yã¹õ)ÇP×â\001ºÕhõí?\0135_³\030Âi6ç\020ðÃú \021hÉ\n¤`\034çG\000áï-¤ùÊô¬àÒõïÀ+\001\036Üû\0043#è-®Øï7\027\004ê\016 +îKû\036·¢TúÚÍë/þ\023Ö!\002S\tåÜñþ,Òî»/\b³\030]Óé@U\005\033(\rG\030ÙaÎF¢\031ÑóWÝþ÷\035\rÐ¢d½óª\\É\n3Ü\0070ãÄ\021)\004Þ¥\nö¯C\030Õ'¨G5´\030â>úÅó<û\007\035\023ÝÀ9é \001ã!\tøñã^\nÄ«\022B¿\0071KüÜ.@Òø\022æB\025\001$ò\036BðM9âZüÉ1õ\001Ï\035éL\004\022&Þj Ú5Ã)´\000ð\021cË0ü¥?±4\023*êG\002\037Ô`¦êÅ\"\023ç\007\031:°'óËêÓJ¯þ|\rÒ\032È¶*\tÞês\025À\013ùÇX¯\f\002Íí\017äôr#ÖøµQÔí\020Ê\006½Ùù/ãï\016(à\002öEÃýß\004p\033ûV¾\nà8è¼ú#\003ëó\036\017¸@à\\0Ûò\037Ôç.\025¹S\004=Ð\013Jå5\t\025Â.iæ$ý<\034\fË´=úÎP\033¸Õ\r'Rï»Ö\017©*4\023÷É\030Ù3©`\025ÐQ<ý\"î\006¡%\020A\0008ø¢Aý%¨ß-í\031Ä\003'ñH\000Û\032RïÏLóp\"\005\0279í\bl7êÌ\0362Gä\000ÜîN(\005Tôã\013.Ü¾\004Ç×,öÒý¹ìË\030ÁMð×\033Çù\024þ«?ß¯\035¡\013õ¶\003à\024­ÚéÁVÛÿ¼$âô\001³\024÷\bÄ!b\006\035Öëµ\021@Åþ÷4\031ãY°:\034Nâm\n*ß\005\016åb\tI½ØXöÎ^è;)ÐC\0174Ã*ÿ6÷\013,ò\024/È\021¦Ù-^Ñ@õ¯ÎúA\000\000\037îÓ'E\020ìJó\n\022éÄ\007\026õ±Z#Ì2óé8\037\0130\022ü\005Åâ\025úêd\bæ\035HÐªäDÓøT\034H\005àí\020)ç\0266Á\016kß.\006»gå\001¶¤)Îÿ¨FÙ'\0024Öêû<\027¶(Ò¦\002äí#×qð2¿\037×ö¸\\\021\003&\031h\006°ê4\nÁú$9ý¥P\nÞ,æõÌ;û\022\034ÙÍ$\025Ýn4 úòÍç@\036\022¼\001Üø\005\020\\ò)ÉOº\030\bO\001ª&ÍAðÆØíüÃ\017!ÿäðÍ´\032ØÂ\001ñ]¸\"\031¨Qè¬ó6\013Tù\006¹ïá\017/b¿\f©Kñ,{AÆ\033àÀ=ÿ\r5öý!Úç\023Uý\f-â<¹2ßô:Ù`&\026B\013mäE\035Ó¸ü\004×\f&ÝÈIþê?Å\033ÕQµ\027þöáÈ\bÓæ­í&Gú\026ÓÝè±ÏF\fÈ0íÞ\030ø\007W\035OÊ\022¼\003Ó1¬õ\004/ê%\024;H¿ï\001\0260\007!¿à*ô8\003%\tíGÓ+W\0317 \r\000×\bµe\035J+á7ò\032¶\004:À#K­\022Ôþ\ní.øJàþì\023Ê\r½÷Þé2VödÔì\031^Ó\r£åüË5Ý\036\002ëú\023Põ7_å.ëô\001¨\023¾\004gøH\020Õ\000éÉð%åD®\032è Å[(Û7X\000MÉ\017\034Ñ+Ãâ¯:ø\022\000²J\025fÀ\021ô\006=ÂÙ%¹ßÍÀ\031\020Ï\"3Ç\nï'ûØ ãÌ(óyÜ1\003^À*\006Ðq\020\t:§\033üñÑâ)ô±\büB\020\002&ÉCï\037Ú÷)éZ±æ\016añC\0040ýï³\004LÚ;\\æ\025?¬\013ë³\027\037\r©ö\025Ùú=½ñØöæ\005G¸ \t\0265\003wÛç\037ñL\tç3Þ\006/Ï´=¡ü#Ï\026/¤\né\037V:øá\025þ\037Ð\007SÇ.ÿ[3\006û=ÑIâ9!ã\0012#SÐ¿\017êg¨ù×ì$<\025³ÌÙ½lõ\031ÁUì\013\003\032×1L÷ÿÉÖm\024Ý&Ó\fÁtêöµ¤ðß\021õÔCÅäî¼\032ýÈ\017ïVË\023³ÿ\027>1Ø\002Æ=S¼Ëö](û\023 Ôÿ®\021û$âHÅ\bï¶á\033>®÷Æ\001è\032@­+\016E2\002%¼\035¥Ý$\023V,\007êe/¸\013F¥ë*âø$\023ð+å\032\017\0000\006à7ì I(æ@x½ò\023Ý!S\003(î3\tF¥0ò\006×É\033øÑ9èM\003ð\f÷Ú¸&Ó\001öÛ\034ûÕh\fòËPà®þ\b¡KÔèÂ\016Fµ\003\fÈñ3ÜÒ\0309\000`4\féÂû\021ãºSüÌÞ\"ÿXã\024nüÉ\026-gÏ8\002K\022·\031Bè)\0067Ä\037I\006º\031aÏ\"Ý-ðTù\032Ïô-á[\036\001\bø,©æùÊ\026;Ú\\Ð!\026ì\017`5îº!\007Û\016@²âû¾\034åÆóà5¿ÎNðµ\000Ú.ë\f4õFú\023¸:¦$Ûd\027üÖ¨\025ÄMì\r¹(Ôô°,\bDó\002×*·ö\027Ã\rLó,®íö#Ö\bFì$\fYû\tw\024øÝ\017\031ç¡úCÖè±Æ\004\037â\n\001êÂ6î»?ä!Í[\033\007H\001kì\033ßÃ6ä:\007EçýÕÇå7\035ÂW0¬\023ÿ1¹\036Öê\003#/Ê^>%\022Â!\001\026;[ìÑo¿+?\022\006\"J\013.õ\003Ù@â£\"Ç\022ü³&\013oþ\036ÐÜ'¦=\021\001_Ð\013\002èóRÒÝBÍï*@¶äþ©ô\005Îröß\t&Ú\0165ôüÌNß÷ÐèþÛf\022+óýÁð\016åÖP?ø\024íÆJ¬ð\003\033RÞ\027ûÜ=È\033(÷\005\026\000MÆ\020ñQ\tÓ3Ýí¸0OËñ¶ÿ\032\021«ð\035µ[ \031È\007¶é­1\027W8+\005Ïç§Ù/ú\rX1¾÷$ï·A\037*\021á\t¼cç¥6â÷\033Ù8\037À\024T\f\033æü¤\036.RÞFç×3\000\r):ñ%OÔ: \fÎÛú»\034ò1\"Z\b\033»â\024Ìé\nÒ/Mô¿ëlúª4ð\020\"µ\013aÐ\003²êú(ÿH¾\024@Ô\004öÂ*!\005VÅæÕ\003¿áù\025\000ßI\bë\000e\017áÀ\000ó<Ó*ÿ¥8u²\020ä\007ÿÍ1×\004NÇÔJÜü&§-\021làAòÉ×)\006ícã\027\nÏ£ø<¯\027õs\0173BîÄlö(=ÇØC®\027ÌKêdõ\030Ú\004ýÈ:ØU\f \030$äÿ\035õ=ÀîãDôË\007¯\036ä9ö®\020Æ;ëþi\023íß-E¨ë\035Ð\013¹\036\003å\025µ\037\néú7'Þ\020\005!ÄJî\037ç+«\031#¹íõB®\r.\007\026S\fÿ\031Ø1\r\002f\031Ý3û\"L²2ÜÀ\013\036üÌ\bÛÿQó-Ù\020©ÔU.õQ\002î·ù5­à\f>Z\025ñùáH5\022ÜÁìpÍé3ÖÈ\0379YüîO½ëÎT\002ñØ\016\032óGÒU#¼>(å«]û4CñþàÏ\023 Õ\r_\032ÑEü(Î÷Û\006cÅ\004Òþ](ú\027ß\002©%øºßèÂ$Ò\023ø'\b\035â·+É\005å%5\002çð\021÷Æ\030\005Í#ëÈ\033\006²x4Ãã§+Çæð\023R\003»\rBÐ'\020è\035Ê\b;²M\016Añg\024\t,\004¤@á.HÂ§D\024dùP¯ø\026¶Ö/b\rFá»\013J%\016íø\005Ló>\t\0000´×\0322å\036îý;¸-Lòä\037Õ÷¾\034ä\000ÎLõ\030\013ñÿØ\017ôþÒç6 Î\tÝjA\034\003Þì6üò\0319øæ×Á=¬&\031ýÚº\037ZëôÅ\000°Q¿Üõ\013Ö\025ª\0031î*ËÙ<'í6Û®kÊ³Yé$=\013¾ï\000\021,ìÅ\f¢úÍ!´Õ'hÜ\002T+ý\025ÜéÏi\023÷ß\007%9j\024Ö,\b\027z\000ë5XÅ\021a\t°R\006\020·\035ýå\";\005\0261Þ¬\033BÙ\\ºôþ9äM2\023W\tÆ\022°Ïñ_\bH/\fí6ÁÑ?\017ú¢#ó8æÊ£E¼!ûÝæý\031ó§ùÕUÆ\0161íÔ\036ùÇu\003(ö¦ã3\036Ô&²\007ó¾æ\000Ií0\007\0307 âÆú´\"\001ä)Mè¶ÌáN\002«ú\034&âÏ\016ôF$Ò7HÞ0è\026A\003ø¾âP\nòæÑ\017ÄK\026\005S\017îÇ\037Ø>ö\034áûCèº\000\017AðØQÊ¦\027û\004\034F\013ÀÑ[\017?ñ\0060­ç\005\025»ì\rÅ#t½àñ(b\021\001?·!Gý9ì\nûÍèAÝ\030vü*\016°Î%¤Øn÷Ì,©\033\0228ô\n]¯òÛ.î\0262ÝìÕýQ\032>Êj-÷\003Ðþ\013\0377ÏÚ\031¨Í-Û\030µ+Öf\034¿÷\001ªÒCß3b\004\020Ã(\nÞXê\005¼þÞ Õ2Æ\"ý± \005Æ*g\022ÂíÙ þà V\030@ô£ë²\bHê%ûï\023\006`õá¢<ð0\f'6ç\n\002ð\026¼óN\032î=\037Ó1är>ê\020H\007kç".getBytes(StandardCharsets.ISO_8859_1);
    public static final double[] TRI_BLUE_NOISE_MULTIPLIERS = new double[4096];
    static {
        for (int i = 0; i < 4096; i++) {
            TRI_BLUE_NOISE_MULTIPLIERS[i] = Math.exp((PaletteReducer.TRI_BLUE_NOISE[i] + 0.5f) * 0x1p-7f);
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
        int color;
        float adj, strength = (float) (ditherStrength * populationBias * 128);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    color |= (color >>> 5 & 0x07070700) | 0xFE;
//                    adj = ((PaletteReducer.RAW_BLUE_NOISE[(px & 63) | (y & 63) << 6] + 0.5f) * 0.007f); // slightly inside -1 to 1 range, should be +/- 0.8925
                    adj = ((BlueNoise.get(px, y) + 0.5f) * 0.007f); // slightly inside -1 to 1 range, should be +/- 0.8925
                    adj *= adj * adj * strength;
                    int rr = MathUtils.clamp((int) (adj + ((color >>> 24)       )), 0, 255);
                    int gg = MathUtils.clamp((int) (adj + ((color >>> 16) & 0xFF)), 0, 255);
                    int bb = MathUtils.clamp((int) (adj + ((color >>> 8)  & 0xFF)), 0, 255);
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }
//    /**
//     * A blue-noise-based dither that uses a tiling 64x64 noise texture to add error to an image;
//     * this does use an approximation of arccosine to bias results toward the original color.
//     * <br>
//     * There are times to use {@link #reduceBluish(Pixmap)} and times to use this; each palette and
//     * source image will have different qualities of result.
//     * @param pixmap will be modified in-place and returned
//     * @return pixmap, after modifications
//     */
//    public Pixmap reduceTrueBlue (Pixmap pixmap) {
//        boolean hasTransparent = (paletteArray[0] == 0);
//        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
//        Pixmap.Blending blending = pixmap.getBlending();
//        pixmap.setBlending(Pixmap.Blending.None);
//        int color, used;
//        float adj, strength = ditherStrength * 8;
//        for (int y = 0; y < h; y++) {
//            for (int px = 0; px < lineLen; px++) {
//                color = pixmap.getPixel(px, y) & 0xF8F8F880;
//                if ((color & 0x80) == 0 && hasTransparent)
//                    pixmap.drawPixel(px, y, 0);
//                else {
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
//                    int rr = ((color >>> 24)       );
//                    int gg = ((color >>> 16) & 0xFF);
//                    int bb = ((color >>> 8)  & 0xFF);
//                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                        | ((gg << 2) & 0x3E0)
//                        | ((bb >>> 3))] & 0xFF];
//                    adj = (acos_((BlueNoise.get(px, y) + 0.5f) * 0.00784313725490196f) - 0.25f) * strength;
//                    rr = MathUtils.clamp((int) (rr + (adj * ((rr - (used >>> 24))))), 0, 0xFF);
//                    gg = MathUtils.clamp((int) (gg + (adj * ((gg - (used >>> 16 & 0xFF))))), 0, 0xFF);
//                    bb = MathUtils.clamp((int) (bb + (adj * ((bb - (used >>> 8 & 0xFF))))), 0, 0xFF);
//                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                        | ((gg << 2) & 0x3E0)
//                        | ((bb >>> 3))] & 0xFF]);
//                }
//            }
//
//        }
//        pixmap.setBlending(blending);
//        return pixmap;
//    }

    /**
     * A different kind of blue-noise-based dither; does not diffuse error, and uses a non-repeating blue noise pattern
     * (the same type as what {@link #reduceTrueBlue(Pixmap)} uses) as well as a checkerboard pattern, but only applies
     * these noisy patterns when there's error matching a color from the image to a color in the palette.
     * <br>
     * There are times to use {@link #reduceTrueBlue(Pixmap)} and times to use this; each palette and
     * source image will have different qualities of result. {@link #reduceTrueBlue(Pixmap)} will add splotches of
     * different lightness even in areas where a color would be matched exactly; this method shouldn't do that.
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceBluish (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float adj, strength = (float) (ditherStrength * populationBias * 1.5);
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
//                    adj = ((PaletteReducer.RAW_BLUE_NOISE[bn = (px & 63) | (y & 63) << 6] + 0.5f) * 0.01f); // 0.007843138f is 1f / 127.5f
//                    adj += ((px + y & 1) - 0.5f) * (0.5f + PaletteReducer.RAW_BLUE_NOISE[bn * 0xDAB & 4095]) * -0x1p-9f;
                    adj = ((BlueNoise.get(px, y) + 0.5f) * 0.007843138f); // 0.007843138f is 1f / 127.5f
                    adj += ((px + y & 1) - 0.5f) * (0.5f + BlueNoise.get(px * 19, y * 23)) * -0x1.6p-10f;
                    adj *= strength;
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
//    /**
//     * A different kind of blue-noise-based dither; does not diffuse error, and uses a non-repeating blue noise pattern
//     * (that isn't quite as strongly measurable as blue noise as what {@link #reduceTrueBlue(Pixmap)} uses). This pattern
//     * can be seeded to produce different dithers for otherwise identical inputs; the seed can be any int.
//     * <br>
//     * There are times to use {@link #reduceTrueBlue(Pixmap)} and times to use this; each palette and
//     * source image will have different qualities of result.
//     * @param pixmap will be modified in-place and returned
//     * @param seed any int; will be used to change the dither pattern
//     * @return pixmap, after modifications
//     */
//    public Pixmap reduceBluish (Pixmap pixmap, int seed) {
//        boolean hasTransparent = (paletteArray[0] == 0);
//        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
//        Pixmap.Blending blending = pixmap.getBlending();
//        pixmap.setBlending(Pixmap.Blending.None);
//        int color, used;
//        double adj, strength = ditherStrength;
//        for (int y = 0; y < h; y++) {
//            for (int px = 0; px < lineLen; px++) {
//                color = pixmap.getPixel(px, y) & 0xF8F8F880;
//                if ((color & 0x80) == 0 && hasTransparent)
//                    pixmap.drawPixel(px, y, 0);
//                else {
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
//                    int rr = ((color >>> 24)       );
//                    int gg = ((color >>> 16) & 0xFF);
//                    int bb = ((color >>> 8)  & 0xFF);
//                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                        | ((gg << 2) & 0x3E0)
//                        | ((bb >>> 3))] & 0xFF];
//                    adj = Math.cbrt((BlueNoise.get(px, y, BlueNoise.ALT_NOISE[1]) + 0.5f) * 0.00784313725490196f) * strength;
////                    adj = (BlueNoise.getSeeded(px, y, 1111111) + ((px + y & 1) - 0.3125f) * 32f) * strength;
//                    rr = MathUtils.clamp((int) (rr + (adj * ((rr - (used >>> 24))))), 0, 0xFF);
//                    gg = MathUtils.clamp((int) (gg + (adj * ((gg - (used >>> 16 & 0xFF))))), 0, 0xFF);
//                    bb = MathUtils.clamp((int) (bb + (adj * ((bb - (used >>> 8 & 0xFF))))), 0, 0xFF);
//                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                        | ((gg << 2) & 0x3E0)
//                        | ((bb >>> 3))] & 0xFF]);
//                }
//            }
//
//        }
//        pixmap.setBlending(blending);
//        return pixmap;
//    }

    /**
     * A white-noise-based dither; uses the colors encountered so far during dithering as a sort of state for basic
     * pseudo-random number generation, while also using some blue noise from a tiling texture to offset clumping.
     * This tends to be less "flat" than {@link #reduceBluish(Pixmap)}, permitting more pixels to be different from
     * what {@link #reduceSolid(Pixmap)} would produce, but this generally looks good, especially with larger palettes.
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceChaoticNoise (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        double adj, strength = ditherStrength * populationBias * 1.5;
        long s = 0xC13FA9A902A6328FL;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y) & 0xF8F8F880;
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    color |= (color >>> 5 & 0x07070700) | 0xFF;
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF];
                    adj = ((PaletteReducer.RAW_BLUE_NOISE[(px & 63) | (y & 63) << 6] + 0.5f) * 0.007843138f);
                    adj *= adj * adj;
                    //// Complicated... This starts with a checkerboard of -0.5 and 0.5, times a tiny fraction.
                    //// The next 3 lines generate 3 low-quality-random numbers based on s, which should be
                    ////   different as long as the colors encountered so far were different. The numbers can
                    ////   each be positive or negative, and are reduced to a manageable size, summed, and
                    ////   multiplied by the earlier tiny fraction. Summing 3 random values gives us a curved
                    ////   distribution, centered on about 0.0 and weighted so most results are close to 0.
                    ////   Two of the random numbers use an XLCG, and the last uses an LCG.
                    adj += ((px + y & 1) - 0.5f) * 0x1.8p-49 * strength *
                            (((s ^ 0x9E3779B97F4A7C15L) * 0xC6BC279692B5CC83L >> 15) +
                                    ((~s ^ 0xDB4F0B9175AE2165L) * 0xD1B54A32D192ED03L >> 15) +
                                    ((s = (s ^ color) * 0xD1342543DE82EF95L + 0x91E10DA5C79E7B1DL) >> 15));
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
     * Modifies the given Pixmap so it only uses colors present in this PaletteReducer, using Floyd-Steinberg to dither
     * but modifying patterns slightly by introducing triangular-distributed blue noise. If you want to reduce the
     * colors in a Pixmap based on what it currently contains, call {@link #analyze(Pixmap)} with {@code pixmap} as its
     * argument, then call this method with the same Pixmap. You may instead want to use a known palette instead of one
     * computed from a Pixmap; {@link #exact(int[])} is the tool for that job.
     * @param pixmap a Pixmap that will be modified in place
     * @return the given Pixmap, for chaining
     */
    public Pixmap reduceScatter (Pixmap pixmap) {
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
        float w1 = (float)(ditherStrength * populationBias * 0.09375), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f;
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
                    double tbn = PaletteReducer.TRI_BLUE_NOISE_MULTIPLIERS[(px & 63) | ((y << 6) & 0xFC0)];
                    er = (byte) (curErrorRed[px] * tbn);
                    eg = (byte) (curErrorGreen[px] * tbn);
                    eb = (byte) (curErrorBlue[px] * tbn);
                    color |= (color >>> 5 & 0x07070700) | 0xFF;
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
        final float errorMul = (float) (ditherStrength * populationBias);
        computePaletteGamma(1.8 - ditherStrength * 1.8);
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
        final float errorMul = (float) (ditherStrength * populationBias * 0.6);
        computePaletteGamma(1.8 - ditherStrength * 1.8);
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
    }}
