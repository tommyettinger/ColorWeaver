package colorweaver;

import colorweaver.tools.OtherMath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Collections;

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
     * Converts an RGB555 int color to an approximation of the closest RGBA8888 color. For each 5-bit channel in
     * {@code color}, this gets an 8-bit value by keeping the original 5 in the most significant 5 places, then copying
     * the most significant 3 bits of the RGB555 color into the least significant 3 bits of the 8-bit value. In
     * practice, this means the lowest 5-bit value produces the lowest 8-bit value (00000 to 00000000), and the highest
     * 5-bit value produces the highest 8-bit value (11111 to 11111111). This always assigns a fully-opaque value to
     * alpha (255, or 0xFF).
     * @param color an RGB555 color
     * @return an approximation of the closest RGBA8888 color; alpha is always fully opaque
     */
    public static int stretch(final int color)
    {
        return (color << 17 & 0xF8000000) | (color << 12 & 0x07000000) | (color << 14 & 0xF80000) | (color << 9 & 0x070000) | (color << 11 & 0xF800) | (color << 6 & 0x0700) | 0xFF;
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

    public static final double[][] IPT = new double[3][0x8000];
    public static final double[][] IPT_FLAT = new double[3][0x8000];
    public static final double[][] OKLAB = new double[3][0x8000];
    static {
        double r, g, b, l, m, s;
        int idx = 0;
        for (int ri = 0; ri < 32; ri++) {
            r = ri * ri * 0.0010405827263267429; // 1.0 / 31.0 / 31.0
            for (int gi = 0; gi < 32; gi++) {
                g = gi * gi * 0.0010405827263267429; // 1.0 / 31.0 / 31.0
                for (int bi = 0; bi < 32; bi++) {
                    b = bi * bi * 0.0010405827263267429; // 1.0 / 31.0 / 31.0

                    l = Math.pow(0.313921 * r + 0.639468 * g + 0.0465970 * b, 0.43);
                    m = Math.pow(0.151693 * r + 0.748209 * g + 0.1000044 * b, 0.43);
                    s = Math.pow(0.017753 * r + 0.109468 * g + 0.8729690 * b, 0.43);

                    IPT[0][idx] = 0.4000 * l + 0.4000 * m + 0.2000 * s;
                    IPT[1][idx] = 4.4550 * l - 4.8510 * m + 0.3960 * s;
                    IPT[2][idx] = 0.8056 * l + 0.3572 * m - 1.1628 * s;

                    l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
                    m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
                    s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

                    OKLAB[0][idx] = forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
                    OKLAB[1][idx] = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
                    OKLAB[2][idx] = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;

                    idx++;
                }
            }
        }

        idx = 0;
        for (int ri = 0; ri < 32; ri++) {
            for (int gi = 0; gi < 32; gi++) {
                for (int bi = 0; bi < 32; bi++) {
                    l = (0.010126483870967743) * ri + (0.020628) * gi + (0.0015031290322580645) * bi;
                    m = (0.004893322580645161) * ri + (0.024135774193548388) * gi + (0.003225948387096774) * bi;
                    s = (5.726774193548388E-4) * ri + (0.0035312258064516128) * gi + (0.028160290322580644) * bi;

                    IPT_FLAT[0][idx] = 0.4000 * l + 0.4000 * m + 0.2000 * s;
                    IPT_FLAT[1][idx] = 4.4550 * l - 4.8510 * m + 0.3960 * s;
                    IPT_FLAT[2][idx] = 0.8056 * l + 0.3572 * m - 1.1628 * s;

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
                    i = IPT_FLAT[0][indexA] - IPT_FLAT[0][indexB],
                    p = IPT_FLAT[1][indexA] - IPT_FLAT[1][indexB],
                    t = IPT_FLAT[2][indexA] - IPT_FLAT[2][indexB];
            return (i * i * 3.0 + p * p + t * t) * 0x1p13;
//            return i * i * 16.0 + p * p * 9.0 + t * t * 9.0;
        }
    };
    
    public static final ColorMetric iptGoodMetric = new ColorMetric(){
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
                    i = IPT[0][indexA] - IPT[0][indexB],
                    p = IPT[1][indexA] - IPT[1][indexB],
                    t = IPT[2][indexA] - IPT[2][indexB];
            return (i * i + p * p + t * t) * 0x1p13;
        }
    };

    /**
     * Changes the curve of a requested L value so that it matches the internally-used curve. This takes a curve with a
     * very-dark area similar to sRGB (a very small one), and makes it significantly larger. This is typically used on
     * "to Oklab" conversions.
     * <br>
     * Internally, this is just {@code Math.pow(L, 1.5)}. At one point it used a modified "Barron spline" to get its
     * curvature mostly right, but this now seems nearly indistinguishable from an ideal curve.
     * @param L lightness, from 0 to 1 inclusive
     * @return an adjusted L value that can be used internally
     */
    public static double forwardLight(final double L) {
        return Math.pow(L, 1.5);
    }
    public static double forwardGamma(final double L) {
        return Math.pow(L, 2.2);
    }
//    public static double forwardLight(final double L) {
//        final double shape = 0.64516133, turning = 0.95;
//        final double d = turning - L;
//        double r;
//        if(d < 0)
//            r = ((1.0 - turning) * (L - 1.0)) / (1.0 - (L + shape * d)) + 1.0;
//        else
//            r = (turning * L) / (1e-50 + (L + shape * d));
//        return r * r;
//    }

//	public static float forwardLight(final float L) {
//		return (L - 1.004f) / (1f - L * 0.4285714f) + 1.004f;
//	}

    /**
     * Changes the curve of the internally-used lightness when it is output to another format. This makes the very-dark
     * area smaller, matching (closely) the curve that the standard sRGB lightness uses. This is typically used on "from
     * Oklab" conversions.
     * <br>
     * Internally, this is just {@code Math.pow(L, 2.0/3.0)}. At one point it used a modified "Barron spline" to get its
     * curvature mostly right, but this now seems nearly indistinguishable from an ideal curve.
     * @param L lightness, from 0 to 1 inclusive
     * @return an adjusted L value that can be fed into a conversion to RGBA or something similar
     */
    public static double reverseLight(double L) {
        return Math.pow(L, 2.0 / 3.0);
    }

    public static double reverseGamma(double L) {
        return Math.pow(L, 1.0 / 2.2);
    }
//    public static double reverseLight(double L) {
//        L = Math.sqrt(L);
//        final double shape = 1.55, turning = 0.95;
//        final double d = turning - L;
//        double r;
//        if(d < 0)
//            r = ((1.0 - turning) * (L - 1.0)) / (1.0 - (L + shape * d)) + 1.0;
//        else
//            r = (turning * L) / (1e-50 + (L + shape * d));
//        return r;
//    }

//	public static float reverseLight(final float L) {
//		return (L - 0.993f) / (1f + L * 0.75f) + 0.993f;
//	}

    public static final ColorMetric oklabMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            final int indexA = (r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3),
                    indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
            double
                    L = OKLAB[0][indexA] - OKLAB[0][indexB],
                    A = OKLAB[1][indexA] - OKLAB[1][indexB],
                    B = OKLAB[2][indexA] - OKLAB[2][indexB];
            L *= L;
            A *= A;
            B *= B;
            return (L * L + A * A + B * B) * 0x1.2p+22;
        }
    };

    public static final ColorMetric oklabCarefulMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double r = r1 * 0.00392156862745098; r *= r;
            double g = g1 * 0.00392156862745098; g *= g;
            double b = b1 * 0.00392156862745098; b *= b;

            double l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
            double m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
            double s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

            double L1 = forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
            double A1 = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
            double B1 = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;
////LR alternate lightness estimate
//            final double k1 = 0.206, k2 = 0.03, k3 = 1.17087;
//            double t = (k3 * L1 - k1);
//            L1 = (t + Math.sqrt(t * t + 0.1405044 * L1)) * 0.5;

            r = r2 * 0.00392156862745098; r *= r;
            g = g2 * 0.00392156862745098; g *= g;
            b = b2 * 0.00392156862745098; b *= b;

            l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
            m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
            s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

            double L2 = forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
            double A2 = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
            double B2 = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;
//            t = (k3 * L2 - k1);
//            L2 = (t + Math.sqrt(t * t + 0.1405044 * L2)) * 0.5;

            double L = (L1 - L2);
            double A = (A1 - A2);
            double B = (B1 - B2);

            return (L * L + A * A + B * B) * 0x1p+21;
        }
    };

    /**
     * For colors stored as ints with 8 bits each of L, A, B, and alpha, in that order.
     */
    public static final ColorMetric oklabLABMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int l1, int a1, int b1, int l2, int a2, int b2) {
            double L1 = l1 * 0.00392156862745098;
            double A1 = a1 * 0.00392156862745098;
            double B1 = b1 * 0.00392156862745098;

            double L2 = l2 * 0.00392156862745098;
            double A2 = a2 * 0.00392156862745098;
            double B2 = b2 * 0.00392156862745098;

            double L = (L1 - L2);
            double A = (A1 - A2);
            double B = (B1 - B2);

            return (L * L + A * A + B * B) * 0x1p+21;
        }
    };


    public static final ColorMetric oklabGammaMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double r = r1 * 0.00392156862745098; r *= r;
            double g = g1 * 0.00392156862745098; g *= g;
            double b = b1 * 0.00392156862745098; b *= b;

//            double l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
//            double m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
//            double s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

            double l = Math.pow(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b, 1.0/4.4);
            double m = Math.pow(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b, 1.0/4.4);
            double s = Math.pow(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b, 1.0/4.4);

            double L1 = forwardGamma(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
            double A1 = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
            double B1 = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;
////LR alternate lightness estimate
//            final double k1 = 0.206, k2 = 0.03, k3 = 1.17087;
//            double t = (k3 * L1 - k1);
//            L1 = (t + Math.sqrt(t * t + 0.1405044 * L1)) * 0.5;

            r = r2 * 0.00392156862745098; r *= r;
            g = g2 * 0.00392156862745098; g *= g;
            b = b2 * 0.00392156862745098; b *= b;

//            l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
//            m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
//            s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

            l = Math.pow(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b, 1.0/4.4);
            m = Math.pow(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b, 1.0/4.4);
            s = Math.pow(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b, 1.0/4.4);

            double L2 = forwardGamma(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
            double A2 = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
            double B2 = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;
//            t = (k3 * L2 - k1);
//            L2 = (t + Math.sqrt(t * t + 0.1405044 * L2)) * 0.5;

            double L = (L1 - L2);
            double A = (A1 - A2);
            double B = (B1 - B2);

            return (L * L + A * A + B * B) * 0x1p+21;
        }
    };

    public static final ColorMetric oklabAlternateMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double r = r1 * 0.00392156862745098; r *= r;
            double g = g1 * 0.00392156862745098; g *= g;
            double b = b1 * 0.00392156862745098; b *= b;

            double l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
            double m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
            double s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

            double L1 = 0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s;
            double A1 = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
            double B1 = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;
////LR alternate lightness estimate
            final double k1 = 0.206, k3 = 1.17087; // k2 = 0.03, but that is already included.
            double t = (k3 * L1 - k1);
            L1 = (t + Math.sqrt(t * t + 0.1405044 * L1)) * 0.5;

            r = r2 * 0.00392156862745098; r *= r;
            g = g2 * 0.00392156862745098; g *= g;
            b = b2 * 0.00392156862745098; b *= b;

            l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
            m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
            s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

            double L2 = 0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s;
            double A2 = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
            double B2 = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;
            t = (k3 * L2 - k1);
            L2 = (t + Math.sqrt(t * t + 0.1405044 * L2)) * 0.5;

            double L = (L1 - L2);
            double A = (A1 - A2);
            double B = (B1 - B2);

            return (L * L + A * A + B * B) * 0x1p+21;
        }
    };

    public static final ColorMetric oklabOnceMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double r = (r1 - r2) * 0.00392156862745098; r *= r;
            double g = (g1 - g2) * 0.00392156862745098; g *= g;
            double b = (b1 - b2) * 0.00392156862745098; b *= b;

            double l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
            double m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
            double s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

            double L = forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
            double A = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
            double B = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;

            return (L * L + A * A + B * B) * 0x1p+21;//300000;
        }
    };

    /**
     * This metric is currently identical to {@link #oklabCarefulMetric}. For the purpose of comparison,
     * the term "OkCareful" should probably refer to the older, very-slightly-different oklabCarefulMetric,
     * and "OkSmooth" should refer to the current metric (such as this one).
     */
    public static final ColorMetric oklabSmoothMetric = oklabCarefulMetric;

    /**
     * Returns true if the given Oklab values are valid to convert losslessly back to RGBA.
     * @param L lightness channel, as a double from 0 to 1
     * @param A green-to-red chromatic channel, as a double from 0 to 1
     * @param B blue-to-yellow chromatic channel, as a double from 0 to 1
     * @return true if the given Oklab channels can be converted back and forth to RGBA
     */
    public static boolean inGamut(double L, double A, double B)
    {
        L = reverseLight(L);

        double l = (L + +0.3963377774 * A + +0.2158037573 * B);
        l *= l * l;
        double m = (L + -0.1055613458 * A + -0.0638541728 * B);
        m *= m * m;
        double s = (L + -0.0894841775 * A + -1.2914855480 * B);
        s *= s * s;

        double dr = Math.sqrt(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s)*255.0;
        final int r = (int)dr;
        if(Double.isNaN(dr) || r < 0 || r > 255) return false;
        double dg = Math.sqrt(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s)*255.0;
        final int g = (int)dg;
        if(Double.isNaN(dg) || g < 0 || g > 255) return false;
        double db = Math.sqrt(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s)*255.0;
        final int b = (int)db;
        return (!Double.isNaN(db) && b >= 0 && b <= 255);
    }

    public static boolean fillOklab(double[] toFill, double r, double g, double b){
        r *= r;
        g *= g;
        b *= b;

        double l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
        double m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
        double s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

        toFill[0] = forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
        toFill[1] = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
        toFill[2] = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;

        return inGamut(toFill[0], toFill[1], toFill[2]);
    }

    public static int oklabToRGB(double L, double A, double B)
    {
        L = reverseLight(L);
        double l = (L + 0.3963377774 * A + 0.2158037573 * B);
        double m = (L - 0.1055613458 * A - 0.0638541728 * B);
        double s = (L - 0.0894841775 * A - 1.2914855480 * B);
        l *= l * l;
        m *= m * m;
        s *= s * s;
        final int r = (int)(Math.sqrt(Math.min(Math.max(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s, 0.0), 1.0)) * 255.9999);
        final int g = (int)(Math.sqrt(Math.min(Math.max(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s, 0.0), 1.0)) * 255.9999);
        final int b = (int)(Math.sqrt(Math.min(Math.max(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s, 0.0), 1.0)) * 255.9999);
        return r << 24 | g << 16 | b << 8 | 255;
    }


    private static final double[] FORWARD_LOOKUP = new double[256];
    private static final double[] RGB_POWERS = new double[3 << 8];
    static {
        for (int i = 1; i < 256; i++) {
            RGB_POWERS[i]     = Math.pow(i, 3.7);
            RGB_POWERS[i+256] = Math.pow(i, 4.0);
            RGB_POWERS[i+512] = Math.pow(i, 3.1);
//            FORWARD_LOOKUP[i] = Math.pow(i / 255.0, 2.0);
            FORWARD_LOOKUP[i] = OtherMath.barronSpline(i / 255f, 4f, 0.5f);
//            FORWARD_LOOKUP[i] = Math.pow(OtherMath.barronSpline(i / 255f, 4f, 0.5f), 2.0);
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

    public static final ColorMetric rgbStupidMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.MAX_VALUE;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.MAX_VALUE;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double rf = (FORWARD_LOOKUP[r1] - FORWARD_LOOKUP[r2]) * 1.55;// rf *= rf;
            double gf = (FORWARD_LOOKUP[g1] - FORWARD_LOOKUP[g2]) * 2.05;// gf *= gf;
            double bf = (FORWARD_LOOKUP[b1] - FORWARD_LOOKUP[b2]) * 0.90;// bf *= bf;

            double d2 = (rf * rf + gf * gf + bf * bf);
            return d2 * d2 * 0x1.8p17;
        }
    };

    public static final ColorMetric rgbStupiderMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.MAX_VALUE;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.MAX_VALUE;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double rf = (r1 - r2);
            double gf = (g1 - g2);
            double bf = (b1 - b2);
            return (rf * rf + gf * gf + bf * bf);
        }
    };

    public static final ColorMetric rgbSqrtMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.MAX_VALUE;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.MAX_VALUE;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double rf = (r1 - r2);
            double gf = (g1 - g2);
            double bf = (b1 - b2);
            return Math.sqrt(rf * rf + gf * gf + bf * bf);
        }
    };
//            double ra = FORWARD_LOOKUP[r1];// ra *= ra;
//            double ga = FORWARD_LOOKUP[g1];// ga *= ga;
//            double ba = FORWARD_LOOKUP[b1];// ba *= ba;
//            double rb = FORWARD_LOOKUP[r2];// rb *= rb;
//            double gb = FORWARD_LOOKUP[g2];// gb *= gb;
//            double bb = FORWARD_LOOKUP[b2];// bb *= bb;
//
//            double rf = (ra - rb);
//            double gf = (ga - gb);
//            double bf = (ba - bb);
//
//            return (rf * rf + gf * gf + bf * bf) * 0x1p+21;

    public static final ColorMetric rgbSimpleMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double rf = (r1 - r2); rf *= rf;
            double gf = (g1 - g2); gf *= gf;
            double bf = (b1 - b2); bf *= bf;

            return (rf * rf + gf * gf + bf * bf) * 0x1p-10;
        }
    };

    public static final ColorMetric rgbTrickyMetric = new ColorMetric(){
        public double difference(int color1, int color2) {
            if(((color1 ^ color2) & 0x80) == 0x80) return Double.MAX_VALUE;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        public double difference(int color1, int r2, int g2, int b2) {
            if((color1 & 0x80) == 0) return Double.MAX_VALUE;
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            double rf = (r1 - r2);
            double gf = (g1 - g2);
            double bf = (b1 - b2);
            double lf = 100000 * Math.abs(OKLAB[0][(r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3)] - OKLAB[0][(r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3)]);
            return (rf * rf + gf * gf + bf * bf + lf);
        }
    };
    public static final BasicColorMetric basicMetric = new BasicColorMetric(); // has no state, should be fine static
    public static final LABEuclideanColorMetric labMetric = new LABEuclideanColorMetric();
    public static final LABRoughColorMetric labRoughMetric = new LABRoughColorMetric();
    public byte[] paletteMapping;
    public final int[] paletteArray = new int[256];
    FloatArray curErrorRedFloats, nextErrorRedFloats, curErrorGreenFloats, nextErrorGreenFloats, curErrorBlueFloats, nextErrorBlueFloats;
    public int colorCount;
    double ditherStrength = 1.0, populationBias = 0.5;

    /**
     * This stores a preload code for a PaletteReducer using {@link Coloring#AURORA} with {@link #oklabMetric}. Using
     * a preload code in the constructor {@link #PaletteReducer(int[], byte[])} eliminates the time needed to fill 32 KB
     * of palette mapping in a somewhat-intricate way that only gets more intricate with better metrics, and replaces it
     * with a straightforward load from a String into a 32KB byte array. This load is a simple getBytes(), but does use
     * {@link StandardCharsets}, which requires Android API Level 19 or higher (Android OS 4.4 KitKat, present on over
     * 98% of devices) and is otherwise omnipresent. StandardCharsets is available on GWT and supports the charset used
     * here, ISO 8859-1, but doesn't support many other charsets (just UTF-8).
     */
    private static final byte[] ENCODED_AURORA = "\001\001\001\002\002u\030\030\030\030\030\030\030\027\027\027\027\027ßßÞÞÞÞÞÞÝÝÝ\025\025\025\002\002\002\002\002uu\030\030\030\030\030\030\027\027\027\027\027ßßÞÞÞÞÞÞÝÝÝ\025\025\025\002\002\002\002uuu\030\030\030\030\030\030\027\027\027\027\027ßßÞÞÞÞÞÞÝÝÝ\025\025\025WWWuuuuvàààà\027\027\027\027\027ßßßÞÞÞÞÞÝÝÝÝ\025\025\025WWWW\003uuàààààÊ\027\027ËßßßßÞÞÞÞÞÝÝÝÝ\025\025\025gggggggàÊÊÊÊÊËËËËËËßÞÞÞÞÝÝÝÝÝ\025\025\025²²²²gg½½ÊÊÊÊÊËËËËËËËÞÞÞÞÝÝÝÝÝ\025\025\025³³³²½½½½½hhÊËËËËËËËÌÌÌ\026\026\026ÝÝÝÜÜÜÜ³³³³³½½hhhhhhËËËÌÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÎ³³³³³XXXhhhhhttÌÌÌÌÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´XXXXXÉÉÉÉÉtttÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´´XXXÉÉÉÉÉÉttÍÍÍÍÌÏÏÏÏÎÎÎÎÎÎ´´´´´´ffffÉÉÉÉÉÍÍÍÍÍÍÏÏÏÏÏÏÎÎÎÎÎ±±±±±fffffffiiiiÍÍÍÍÍÏÏÏÏÏÏÏÐ×××+++++±±ffff[\020\020\020\020\020\020ÍÍÏÏÏÏÏÏÐÐÐ×××++++++++[[[[\020\020\020\020\020\020\020\020sÏÏÏÐÐÐÐÐ×××+++++++¼¼¼¼¼¼\020\020\020\020\020\020\020\020rrØÐÐÐÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¼¾¾\020\020\020\020\020rrrrÈÈÐÐÐ××µµµµµµµµ¼¼¼¼¼¾¾¾¾¾¾¾¾rÈÈÈÈÈÈÈÈÈÈµµµµµµµµµ¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÒµµµµµµµ»»»»»»¾¾¾¾¾¾¾¾¾jÈÈÈÈÈÈÈÒÒ»»»»»»»»»»»»»»»¾¾¾¾¾¾\021\021\021\021ÈÈÈÈÖÒÒº,,,,,,»»»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»»¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖÖºººººººº,,,,,»¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖ¶¶ºººººººº,ÀÀÀÀ¿¿¿¿¿¿¿¿\021\021ÇÇÇÇÔÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÇÇÔ·¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÇÅÅ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÅÅ··········¹¹¹¹ÀÀÀÁÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ--------¹¹¹¹¹¹¹¹¹ÂÂÂÂÁÁÁÁÁÁ\022\022\022\022\022----------¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÃÃÃ\022\022\022\022\001\001\002\002\002u\030\030\030\030\030\030\030\027\027\027\027\027ßßÞÞÞÞÞÞÝÝÝ\025\025\025\002\002\002\002\002uu\030\030\030\030\030\030\027\027\027\027\027ßßÞÞÞÞÞÞÝÝÝ\025\025\025\002\002\002\002uuuv\030\030\030\030\030\027\027\027\027\027ßßÞÞÞÞÞÞÝÝÝ\025\025\025WWWuuuuvvààà\027\027\027\027\027ßßßÞÞÞÞÞÝÝÝÝ\025\025\025WWWW\003uuàààààÊÊ\027ËßßßßÞÞÞÞÞÝÝÝÝ\025\025\025gggggggàÊÊÊÊÊËËËËËËßÞÞÞÞÝÝÝÝÝ\025\025\025²²²²gg½½ÊÊÊÊÊËËËËËËËÞÞÞÝÝÝÝÝÝ\025\025\025³³³²½½½½½hhÊËËËËËËËÌÌÌ\026\026\026\026ÝÝÜÜÜÜ³³³³³½½hhhhhhËËËÌÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÎ³³³³³XXXhhhhhttÌÌÌÌÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´XXXXXÉÉÉÉÉtttÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´´XXXÉÉÉÉÉÉttÍÍÍÍÏÏÏÏÏÎÎÎÎÎÎ´´´´´´ffffÉÉÉÉÉÍÍÍÍÍÍÏÏÏÏÏÏÎÎÎÎÎ±±±±±fffffffiiiiÍÍÍÍÍÏÏÏÏÏÏÏÐ×××+++++±±ffff[\020\020\020\020\020\020ÍÍÏÏÏÏÏÏÐÐÐ×××++++++++[[[[\020\020\020\020\020\020\020\020sÏÏÏÐÐÐÐÐ×××++++++¼¼¼¼¼¼¼\020\020\020\020\020\020\020\020rrØÐÐÐÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¼¾¾\020\020\020\020\020rrrrrÈÐÐÐ××µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾¾rÈÈÈÈÈÈÈÈÈÈµµµµµµµµµ¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÒµµµµµµµ»»»»»»¾¾¾¾¾¾¾¾¾jÈÈÈÈÈÈÈÒÒ»»»»»»»»»»»»»»»¾¾¾¾¾¾\021\021\021\021ÈÈÈÈÖÒÒ,,,,,,,»»»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»»¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖÖºººººººº,,,,,»¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖ¶¶¶ººººººº,ÀÀÀÀ¿¿¿¿¿¿¿¿\021\021ÇÇÇÇÔÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÇÇÔ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÇÅÅ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÅÅ··········¹¹¹¹ÀÀÀÁÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ--------¹¹¹¹¹¹¹¹¹ÂÂÂÂÁÁÁÁÁÁ\022\022\022\022\022----------¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÃÃÃ\022\022\022\022\002\002\002\002\002uu\030\030\030\030\030\030\027\027\027\027\027ßßßÞÞÞÞÞÝÝÝÝ\025\025\002\002\002\002\002uu\030\030\030\030\030\030\027\027\027\027\027ßßßÞÞÞÞÞÝÝÝÝ\025\025\002\002\002\002uuuv\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÞÝÝÝÝ\025\025WWWWuuuvvààà\027\027\027\027ßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWW\003\003\003uvààààÊÊËËßßßßÞÞÞÞÞÝÝÝÝ\025\025\025gggggggÊÊÊÊÊÊËËËËËßßÞÞÞÞÝÝÝÝÝ\025\025\025²²²²g½½½ÊÊÊÊÊËËËËËËËÞÞÞÝÝÝÝÝÝ\025\025\025³³³²½½½½½hhwËËËËËËËÌÌÌ\026\026\026\026ÝÝÜÜÜÜ³³³³³½½hhhhhwwËËÌÌÌÌÌÌÌ\026\026\026\026ÜÎÎÎÎ³³³³³XXXhhhhhttÌÌÌÌÌÌÌÌÌ\026\026ÎÎÎÎÎÎ´´´´XXXXXÉÉÉÉttttÌÌÌÌÌÌÌÌÎÎÎÎÎÎÎ´´´´´´XXXÉÉÉÉÉtttÍÍÍÍÏÏÏÏÏÎÎÎÎÎÎ´´´´´´ffffÉÉÉÉÉÍÍÍÍÍÍÏÏÏÏÏÏÎÎÎÎÎ±±±±±±ffffffiiiiÍÍÍÍÍÏÏÏÏÏÏÏÐ×××+++++±±fff[[\020\020\020\020\020\020ÍÍÏÏÏÏÏÏÐÐÐ×××++++++++[[[[\020\020\020\020\020\020\020\020sÏÏÏÐÐÐÐÐ×××++++++¼¼¼¼¼¼¼\020\020\020\020\020\020\020\020rrØÐÐÐÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¼¾¾¾\020\020\020\020rrrrrÈÐÐÐ××µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾¾rrÈÈÈÈÈÈÈÈÈµµµµµµµµµ¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÒµµµµµµµ»»»»»»¾¾¾¾¾¾¾¾¾jÈÈÈÈÈÈÈÒÒ»»»»»»»»»»»»»»»¾¾¾¾¾j\021\021\021\021ÈÈÈÈÖÒÒ,,,,,,,»»»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»»¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖÖººººººº,,,,,,»¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖ¶¶¶ºººººº,,ÀÀÀÀ¿¿¿¿¿¿¿¿\021\021ÇÇÇÇÔÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÇÅÔ··¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÇÅÅ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÅÅ·········¹¹¹¹¹¹ÀÀÁÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ--------¹¹¹¹¹¹¹¹¹ÂÂÂÂÁÁÁÁÁÃ\022\022\022\022\022----------¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÃÃÃ\022\022\022\022\002\002\002vv\030\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÞÝÝÝÝ\025\025\002\002\002vvvv\030\030\030\030\027\027\027\027ßßßßÞÞÞÞÞÝÝÝÝ\025\025\002\002\002\002uuvvvv\030\030\030\027\027\027ßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWWWuuvvvàààà\027\027ßßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWW\003\003\003\003vàààÊÊÊËËßßßßßÞÞÞÞÝÝÝÝÝ\025\025²ggggggÊÊÊÊÊÊËËËËËßßÞÞÞÞÝÝÝÝÝÝ\025\025²²²²g½½½\004ÊÊÊÊËËËËËËË\026\026\026\026ÝÝÝÝÝÜÜÜ³³²²½½½½hhwwËËËËËËÌÌÌ\026\026\026\026\026\026ÜÜÜÜÜ³³³³³½½hhhhhwwËÌÌÌÌÌÌÌ\026\026\026\026\026ÜÜÜÜÎ³³³³³XXXhhhhttttÌÌÌÌÌÌÌ\026\026\026ÎÎÎÎÎÎ´´´´XXXXXÉÉÉÉttttÌÌÌÌÌÌÌ\026ÎÎÎÎÎÎÎ´´´´´XXXXÉÉÉÉÉtttÍÍÍÍÏÏÏÏÏÎÎÎÎÎÎ´´´´´fffffÉÉÉÉÉÍÍÍÍÍÍÏÏÏÏÏÏÏÎÎÎÎ±±±±±±ffffffiiiiÍÍÍÍÍÏÏÏÏÏÏÐÐ×××++++±±±ff[[[\020\020\020\020\020\020ÍÍÏÏÏÏÏÏÐÐÐ×××++++++++[[[[\020\020\020\020\020\020\020ssÏÏÏÐÐÐÐÐ×××++++++¼¼¼¼¼¼e\020\020\020\020\020\020\020srrØØÐÐÐÐÐ××µµµµµµ¼¼¼¼¼¼¼¼¾¾¾\020\020\020rrrrrrÈÐÐÐ××µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾¾rrÈÈÈÈÈÈÈÈÈµµµµµµµµµ¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÒµµµµµµ»»»»»»»¾¾¾¾¾¾¾¾¾jÈÈÈÈÈÈÈÒÒ»»»»»»»»»»»»»»»¾¾¾¾¾jj\021\021\021ÈÈÈÈÒÒÒ,,,,,,,»»»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖÖººººººº,,,,,,»¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖ¶¶¶¶ººººº,,ÀÀÀÀ¿¿¿¿¿¿¿¿\021\021ÇÇÇÇÔÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÇÅÔ···¶¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÇÅÅ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÇÅÅ·········¹¹¹¹¹¹ÀÀÁÁÁÁÁÁÁÁÁÇÇÇ\022ÅÅ--------¹¹¹¹¹¹¹¹¹ÂÂÂÂÁÁÁÁÁÃ\022\022\022\022\022----------¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÃÃÃ\022\022\022\022ôôôô\030\030\030\027\027ßßßßßßÞÞÞÞÝÝÝÝÝ\025\025vôôô\030\030\030\027\027ßßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWvvvvv\030\030\027\027ßßßßßßÞÞÞÞÝÝÝÝÝ\025\025WWW\003\003vvvvvààÊ\027ßßßßßßßÞÞÞÞÝÝÝÝÝ\025\025WW\003\003\003\003\003vvÊÊÊÊËËßßßßßßÞÞÞÝÝÝÝÝÝ\025\025²²gggggÊÊÊÊÊÊËËËËËßßßÞÞÞÝÝÝÝÝÝ\025\025²²²²½½\004\004\004ÊÊÊËËËËËËËË\026\026\026\026\026\026ÝÝÜÜÜÜ³³²²½½½½hhwwwËËËËËÌÌÌ\026\026\026\026\026\026ÜÜÜÜÜ³³³³³³hhhhhwwwËÌÌÌÌÌÌÌ\026\026\026\026\026ÜÜÜÜÜ³³³³XXXXhhhhttttÌÌÌÌÌÌÌ\026\026\026ÎÎÎÎÎÎ´´´´XXXXXÉÉÉÉttttÌÌÌÌÌÌÌ\026ÎÎÎÎÎÎÎ´´´´´XXXXÉÉÉÉÉtttÍÍÍÍÏÏÏÏÏÎÎÎÎÎÎ´´´´´fffffÉÉÉÉÉÍÍÍÍÍÍÏÏÏÏÏÏÏÎÎÚÚ±±±±±±ffffffiiiiÍÍÍÍÍÏÏÏÏÏÏÐÐ×××+++±±±±f[[[[iiiiiisÍÏÏÏÏÏÐÐÐÐ×××+++++++[[[[[[\020\020\020\020\020\020ssÏÏØÐÐÐÐÐÐ××++++++¼¼¼¼¼¼e\020\020\020\020\020\020\020srrØØÐÐÐÐÐ××µµµµµµ¼¼¼¼¼¼¼\\¾¾¾\020\020\020rrrrrrrÈÐÐ××µµµµµµµ¼¼¼¼¼¼¾¾¾¾¾¾¾¾rrrÈÈÈÈÈÈÈÈµµµµµµµµµ¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÈÒµµµµµµ»»»»»»»¾¾¾¾¾¾¾¾jjÈÈÈÈÈÈÈÒÒ,,»»»»»»»»»»»»»¾¾¾¾¾jjj\021\021ÈÈÈÈÒÒÒ,,,,,,,»»»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,»»»»¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖÖººººººº,,,,,,»¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖ¶¶¶¶ººººº,,ÀÀÀÀ¿¿¿¿¿¿¿¿\021\021ÇÇÇÇÔÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ¿¿¿¿¿ÇÇÇÇÇÇÇÇÅÔ····¶¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÇÅÅ··········ÀÀÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÅÅÅ·········¹¹¹¹¹¹ÀÀÁÁÁÁÁÁÁÁÁÇÇ\022\022ÅÅ--------¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÁÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôôôôôôóóßßßßßßßßÞÞÞÝÝÝÝÝ\025\025ôôôôôôóóßßßßßßßßÞÞÞÝÝÝÝÝ\025\025WôôôôôôóóßßßßßßßßÞÞÞÝÝÝÝÝ\025\025WW\003\003vvôôôôóóßßßßßßßßÞÞÝÝÝÝÝÝ\025\025\003\003\003\003\003\003ÊÊÊÊËËßßßßßßßÞÞÝÝÝÝÝÝ\025\025²²²gg\004\004ÊÊÊÊËËËËËßßß\026\026\026\026ÝÝÝÝÜÜÜ²²²²\004\004\004\004\004wwwËËËËËËË\026\026\026\026\026\026\026\026ÜÜÜÜÜ³²²²½½½hhwwwwwËËËÌÌÌ\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³³IhhhhhwwwwÌÌÌÌÌÌ\026\026\026\026\026\026ÜÜÜÜÜ³³³³XXXXhhhhttttÌÌÌÌÌÌ\026\026\026\026ÎÎÎÎÎÎ´´´XXXXXYYÉÉttttttÌÌÌÌÌ\026\026ÎÎÎÎÎÎÎ´´´´´XXXYÉÉÉÉttttÍÍÍÍÏÏÏÏÏÎÎÎÎÎÎ±±±±±fffffÉÉÉÉÉÍÍÍÍÍÍÏÏÏÏÏÏÏÚÚÚÚ±±±±±±fffff[iiiiÍÍÍÍÍÏÏÏÏÏÏÐÐ×××++±±±±±[[[[[iiiiiissÏÏÏÏÏÐÐÐÐ×××+++++++[[[[[[\020\020\020\020\020ssssØØØÐÐÐÐÐ××+++++¼¼¼¼¼¼¼ee\020\020\020\020\020ssrrØØØÐÐÐÐ××µµµµµµ¼¼¼¼¼¼¼\\¾¾¾¾\020\020rrrrrrrÈÐÐ××µµµµµµµ¼¼¼¼¼\\¾¾¾¾¾¾¾¾rrrÈÈÈÈÈÈÈÒµµµµµµµµµ¼¼¼¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÒÒµµµµµ»»»»»»»»¾¾¾¾¾¾¾¾jjÈÈÈÈÈÈÈÒÒ,,,»»»»»»»»»»»»¾¾¾¾jjjjjjÈÈÈÖÒÒÒ,,,,,,,,»»»»»»»¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖºººº,,,,,,,»»»¿¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖººººººº,,,,,,,¿¿¿¿¿¿¿¿\021\021\021\021\021\021ÖÖÖÖ¶¶¶¶¶ºººº,,ÀÀÀÀ¿¿¿¿¿¿¿¿\021ÇÇÇÇÔÔÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ^¿¿¿¿ÇÇÇÇÇÇÇÇÅÔ·····¶¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÇÅÅ··········ÀÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ·········¹¹¹¹¹¹ÀÀÁÁÁÁÁÁÁÁÁÇÇ\022ÅÅÅ--------¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÁÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôôôôôóóóóßßßßßßßÞÞÞÝÝÝÝÝ\025\025ôôôôôóóóóßßßßßßßÞÞÞÝÝÝÝÝ\025\025ôôôôóóóóßßßßßßßÞÞÝÝÝÝÝÝ\025\025ôôôóóóóßßßßßßßÞÞÝÝÝÝÝÝ\025\025HHHH\003óóóËßßßßßßß\026\026\026ÝÝÝÝÜÜÜ²HHHH\004\004ÊÊËËËËËßß\026\026\026\026\026\026\026ÝÜÜÜÜ²²²²H\004\004\004\004wwwwËËËËËË\026\026\026\026\026\026\026\026ÜÜÜÜÜ²²²²I\004\004\004wwwwwwwËËÌÌ\026\026\026\026\026\026\026\026ÜÜÜÜÜ³³³³IIhhhhwwwwxxÌÌÌÌÌ\026\026\026\026\026\026ÜÜÜÜÜ³³³XXXYYY\005\005\005tttttÌÌÌÌÌ\026\026\026\026\026ÎÎÎÎÎ´´´XXXXYYYÉÉttttttÌÌÌÌÌ\026\026ÎÎÎÎÎÎÎ´´´´´XVYYYÉÉÉtttÍÍÍÍÍÏÏÏÏÏÎÎÎÚÚÚ±±±±±fffffZZÉÉtÍÍÍÍÍÍÏÏÏÏÏÏÚÚÚÚÚ±±±±±±fff[[[iiiiÍÍÍÍÍÏÏÏÏÏÏÐÐ×××±±±±±±±[[[[[iiiiissssÏÏÏÏÐÐÐÐ×××+++++++[[[[[e\020\020\020\020\020ssssØØØØÐÐÐÐ××+++++¼¼¼¼¼¼eee\020\020\020\020\020ssrrØØØØÐÐÐ××µµµµµ¼¼¼¼¼¼¼\\\\¾¾¾¾33rrrrrrrrÐÐ××µµµµµµµ¼¼¼¼\\\\¾¾¾¾¾¾¾¾rrrrÈÈÈÈÈÈÒµµµµµµµµµ¼¼\\¾¾¾¾¾¾¾¾¾¾ÈÈÈÈÈÈÈÈÒÒµµµµµ»»»»»»»»¾¾¾¾¾¾¾jjjjÈÈÈÈÈÒÒÒ,,,,»»»»»»»»»»»¾¾¾¾jjjjjjjÈÈÖÒÒÒ,,,,,,,,»»»»»»»¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖººº,,,,,,,,»»»¿¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖºººººº,,,,,,,,¿¿¿¿¿¿¿\021\021\021\021\021\021\021ÔÔÔÔ¶¶¶¶¶¶ººº,ÀÀÀÀÀ^^¿¿¿¿¿\021\021ÇÇÇÇÔÔÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ^^¿¿ÇÇÇÇÇÇÇÇÇÅÔ······¶¶¶ÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÅÅÅ··········ÀÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ········¹¹¹¹¹¹¹¹ÀÁÁÁÁÁÁÁÁÁÇÇ\022ÅÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôôôôóóóóóóóßßßßááááÝÝÝÝÝÜÜôôôôóóóóóóßßßßßááááÝÝÝÝÝÜÜôôôóóóóóóßßßßßááááÝÝÝÝÜÜÜóóóóóóßßßßáááá\026\026ÝÝÝÜÜÜHHHóóóóóßßßáááá\026\026\026\026ÜÜÜÜÜHHHHH\004\004wwËËËËËá\026\026\026\026\026\026\026\026ÜÜÜÜÜ²²HHH\004\004wwww\026\026\026\026\026\026\026\026\026ÜÜÜÜÜIIIIII\004\004wwwwwwÌ\026\026\026\026\026\026\026\026ÜÜÜÜÜIIIIIIIhhwwwwxxxxÌÌÌ\026\026\026\026\026\026\026ÜÜÜÜÜIIIIIIYY\005\005\005\005ttxxxxÌÌÌ\026\026\026\026\026\026ÜÜÜÜÜ¤¤¤¤VVVYYY\005\005tttttyyyyÌ\026\026\026\026ÎÎÎÎÚÚ´´¤¤¤VVVYYÉÉttttÍÍÍÍÍÍÏÏÏÏÚÚÚÚÚÚ±±±±±±fffZZZZZiÍÍÍÍÍÍÏÏÏÏÏÏÚÚÚÚÚ±±±±±±±[[[[ZiiiiiÍÍÍÍÏÏÏÏÏÐÐÐ×ÚÚ±±±±±±[[[[[[iiiiissssÏÏØØØÐÐÐ×××++++++UU[[[eeiiiisssssØØØØØÐÐÐ××++++¼¼¼¼¼¼¼eeee\020\020\020sssrrrØØØØÐÐ××µµµµµ¼¼¼¼¼\\\\\\\\¾¾¾333rrrrrrrrrÐ××µµµµµµµ¼¼¼\\\\\\¾¾¾¾¾¾¾rrrrrrÈÈÈÈÒÒµµµµµµµµ¼\\\\\\\\¾¾¾¾¾¾¾¾ÑÑÈÈÈÈÈÈÈÒÒ°°°°»»»»»»»»»¾¾¾¾¾¾¾jjjjÑÈÈÈÈÒÒÒ°°°°»»»»»»»»»»]]]¾jjjjjjjjÑÖÒÒÒÒ,,,,,,,,»»»»»»¿¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖººº,,,,,,,,»»»¿¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖºººººº,,,,,,,^^¿¿¿¿¿¿\021\021\021\021\021\021\021ÔÔÔÔ¶¶¶¶¶¶¶º,,ÀÀÀÀ^^^^¿¿¿¿\021ÇÇÇÇÇÔÔÔÔ¶¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ^^^^ÇÇÇÇÇÇÇÇÇÅÔ·······¶ÀÀÀÀÀÀÀÀÀÀÁÁÁÁÇÇÇÇÇÇÇÅÅÅ··········ÀÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÇÅÅÅ········¹¹¹¹¹¹¹¹ÂÁÁÁÁÁÁÁÁÁÇÇ\022ÅÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÁÃÃ\022\022\022\022\022---------¹¹¹¹¹¹¹ÂÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022ôôôóóóóóóóñáááááááááÝÜÜÜÜôôôóóóóóóóñáááááááááÝÜÜÜÜôóóóóóóóóñáááááááááÜÜÜÜÜóóóóóóññáááááááá\026ÜÜÜÜÜHóóóññññáááááá\026\026\026ÜÜÜÜÜHHHHHñññáááá\026\026\026\026\026ÜÜÜÜÜHHHHH\004www\026\026\026\026\026\026\026\026\026ÜÜÜÜÜIIIIII==wwww\026\026\026\026\026\026\026\026ÜÜÜÜÜIIIIIII\005\005\005wwxxxx\026\026\026\026\026\026\026\026ÜÜÜÜÜ¤¤IIIVYY\005\005\005\005xxxxxyyyy\026\026\026\026\026\026ÜÜÜÜÜ¤¤¤¤VVVYY\005\005\005ttttyyyyyyy\026\026\026ÚÚÚÚÚÚ¤¤¤¤¤VVVYYZ\006\006tttÍÍÍÍÍÍÏÏÏÏÚÚÚÚÚÚ±±±±±±ffZZZZZ\006\006ÍÍÍÍÍÍÏÏÏÏÏÏÚÚÚÚÚ±±±±±±[[[[ZZiiiissÍÍÍÏÏÏÏÏÐÐÐÚÚÚ±±±±±±[[[[[[iiiisssssØØØØØØÐÐ×××++++UUUUU[eeeeiiisssssØØØØØØÐÐ××+++¼¼¼¼¼¼\\\\eeeee33sssrrrØØØØØÐ××µµµµµ¼¼¼¼\\\\\\\\\\¾¾3333rrrrrrrrrØ××µµµµµµµ¼\\\\\\\\\\\\¾¾¾¾¾¾rrrrrrrÈÈÈÒÒµµµµµµµµ\\\\\\\\\\¾¾¾¾¾¾¾jÑÑÑÈÈÈÈÈÒÒÒ°°°°°°»»»»»»»¾¾¾¾¾¾jjjjÑÑÑÈÈÒÒÒÒ°°°°°°»»»»»»»]]]]]jjjjjjjjÑÖÒÒÒÒ,,,,,,,,,»»»»»]¿¿¿¿\021\021\021\021\021\021\021\021ÖÖÖÖÖ,,,,,,,,,,,,»»¿¿¿¿¿¿\021\021\021\021\021\021\021ÖÖÖÖÖººººº,,,,,,,,^^^^¿¿¿¿\021\021\021\021\021\021\021ÔÔÔÔ¶¶¶¶¶¶¶¯¯,ÀÀÀÀ^^^^^¿¿¿ÇÇÇÇÇÇÔÔÔÔ¶¶¶¶¶¶¶¶ÀÀÀÀÀÀÀÀ^^^^^ÇÇÇÇÇÇÇÇÅÅÔ········ÀÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹ÀÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÅÅÅÅ·······¹¹¹¹¹¹¹¹¹ÂÁÁÁÁÁÁÁÁÁÇ\022\022ÅÅÅ-------¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÁÁÁÃÃÃ\022\022\022\022\022--------¹¹¹¹¹¹¹¹¸ÂÂÂÂÂÂÂÃÃÃÃ\022\022\022\022\"\"\"\"\"õõõõóóóóóóóññááááááááâââÜÜÜ\"\"\"\"\"õõõõóóóóóóñññááááááááâââÜÜÜ\"\"\"\"õõõõóóóóóóñññááááááááâââÜÜÜ\"\"õõõõõóóóóññññááááááááâââÜÜÜõõõóññññññááááááááââÜÜÜÜHHHHññññáááááá\026\026\026ÜÜÜÜÜ£££££w\026\026\026\026\026\026\026\026ÜÜÜÜÜIIIII=====ww\026\026\026\026\026\026\026\026ÜÜÜÜÜIIIIIII=\005\005\005xxxxyy\026\026\026\026\026\026\026ÜÜÜÜÜ¤¤¤¤VVV\005\005\005\005\005xxxxxyyyy\026\026\026\026\026ÛÛÛÛÚÚ¤¤¤¤¤VVVY\005\005\005tttxyyyyyyy\026\026ÚÚÚÚÚÚÚ¤¤¤¤¤¤VVZZZ\006\006\006ttyyyyyyÏÏÏÏÚÚÚÚÚÚ±±±±¥¥¥ZZZZZZ\006\006ÍÍÍÍÍÍÏÏÏÏÏÚÚÚÚÚÚ±±±±±±[[[[ZZZiisssssÍÏÏÏØØØÐÚÚÚÚ±±±±±UU[[[[eiiiisssssØØØØØØØÐ×××++UUUUUUUUeeeeiisssssØØØØØØØØÐ××µµ¼UUUUUU\\\\eeee3333srrrrrØØØØØ××µµµµµ¼¼\\\\\\\\\\\\\\\\33333rrrrrrrrrrÒÒµµµµµµµ\\\\\\\\\\\\\\¾¾¾¾¾32rrrrrrrÈÒÒÒµµµµµµµµ\\\\\\\\\\¾¾¾¾¾¾¾jÑÑÑÑÑÈÈÒÒÒÒ°°°°°°°»»»»»]]]¾¾¾jjjjjÑÑÑÑÈÒÒÒÒ°°°°°°°»»»»»»]]]]]jjjjjjjÑÑÑÒÒÒÒ,,,,,,,,,»»»»]]]]]¿\021\021\021\021\021\021\021\021ÖÖÖÖÖ,,,,,,,,,,,,»^^^¿¿¿¿\021\021\021\021\021\021\021ÔÔÔÔÔººººº,,,,,,,,^^^^^^¿¿\021\021\021\021\021\021ÔÔÔÔÔ¶¶¶¶¯¯¯¯¯¯ÀÀÀÀ^^^^^^^¿ÇÇÇÇÇÇÔÔÔÔ¶¶¶¶¶¶¯¯¯ÀÀÀÀÀÀÀ^^^^^ÇÇÇÇÇÇÇÇÆÆÆ········ÀÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÇÅÅÅ·········¹¹ÀÀÀÀÀÀÁÁÁÁÁÁÁÇÇÇÇÅÅÅÅ······¹¹¹¹¹¹¹¹¹¹ÂÁÁÁÁÁÁÁÁÁÇ\022\022ÅÅÅ------¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÁÁÃÃÃÃ\022\022\022\022--------¹¹¹¹¹¹¹¸¸ÂÂÂÂÂÂÃÃÃÃÃ\022\022\022\022\"\"\"\"\"õõõõòòòóóñññññáááááááâââââÜ\"\"\"\"\"õõõõòòòòññññññáááááááâââââÜ\"\"\"\"\"õõõõõòòòññññññááááááâââââÜÜ\"\"\"\"\"õõõõõòòòññññññááááááâââââÜÜ\"õõõõòòòññññññááááááââââÜÜÜ££££ñññññáááááâââââÜÜÜ£££££===ááá\026\026\026\026ââÜÜÜÜ£££££======\026\026\026\026\026\026\026\026ÛÛÛÛÛIIIIII===\005\005xxxyy\026\026\026\026\026\026ÛÛÛÛÛÛ¤¤¤¤VVV\005\005\005\005\005xxxxyyyyyy\026\026\026ÛÛÛÛÛÛÛ¤¤¤¤¤VVV\005\005\006\006\006xxyyyyyyyyy\026ÛÚÚÚÚÚÚ¤¤¤¤¤¤VZZZ\006\006\006\006\006yyyyyyyyÏÏÚÚÚÚÚÚÚ¥¥¥¥¥¥¥ZZZZZ\006\006\006sÍÍÍÍÍzÏÏÏÏÚÚÚÚÚÚ±±¥¥¥¥¥[[ZZZZiissssszzzØØØØØÚÚÚÚ±±±UUUUU[eeeeiissssssØØØØØØØØÐ××UUUUUUUUUUeeeeessssssØØØØØØØØØ××UUUUUUUUU\\\\eeee33333rrrrrØØØØØ××µµµµ¼\\\\\\\\\\\\\\\\\\\\33333rrrrrrrrrrÒ\024µµµµµµ\\\\\\\\\\\\\\\\¾¾¾3222rrrrrrrrÒÒÒµ°°°°°°\\\\\\\\\\\\\\¾¾¾¾222ÑÑÑÑÑÑÈÒÒÒÒ°°°°°°°°»»»]]]]]]jjjjjjÑÑÑÑÑÒÒÒÒ°°°°°°°°»»»»]]]]]]jjjjjjjÑÑÑÒÒÒÒ,,,,,,,,,,»»»]]]]]]jjjj\021\021\021\021ÖÖÖÖÖ,,,,,,,,,,,,^^^^^^¿¿\021\021\021\021\021\021\021ÔÔÔÔÔºººº,,,,,,,,^^^^^^^^^\021\021\021\021\021\021ÔÔÔÔÔ¯¯¯¯¯¯¯¯¯¯ÀÀÀ^^^^^^^^^ÇÇÇÇÇÇÔÔÔÔ¶¶¯¯¯¯¯¯¯ÀÀÀÀÀÀÀ^^^^^ÇÇÇÇÇÇÇÆÆÆÆ·······¯ÀÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÅÅÅÅ········¹¹¹¹ÀÀÀÀÁÁÁÁÁÁÁÁÇÇÇÇÅÅÅÅ·····¹¹¹¹¹¹¹¹¹¹¹ÂÂÁÁÁÁÁÁÁÃÃ\022ÅÅÅÅ------¹¹¹¹¹¹¹¹¹¹ÂÂÂÂÂÂÁÃÃÃÃÃ\022\022\022\022--------¹¹¹¹¹¹¸¸¸ÂÂÂÂÂÂÃÃÃÃÃÃ\022\022\022\"\"\"\"õõõõõòòòòòñññññááááááâââââââ\"\"\"\"\"õõõõòòòòòñññññááááááâââââââ\"\"\"\"\"õõõõòòòòòñññññáááááââââââââ\"õõõõòòòòòñññññáááááâââââââÛõõõõõòòòññññññáááááââââââÛÛ£££££òòòñññññáááááâââââÛÛÛ£££££=====ááááâââââÛÛÛÛ£££££======\026\026\026\026\026âÛÛÛÛÛÛIIIII====\005\005xxxyyy\026\026\026\026ÛÛÛÛÛÛÛ¤¤¤¤¤VV\005\005\005\005\005xxxxyyyyyyy\026ÛÛÛÛÛÛÛÛ¤¤¤¤¤¤VG\006\006\006\006\006\006xyyyyyyyyyÛÛÛÛÛÚÚÚ¥¥¤¤¤¤¤ZZZ\006\006\006\006\006yyyyyyyyzzÚÚÚÚÚÚÚ¥¥¥¥¥¥¥ZZZZZ\006\006\006sssyzzzzzz{{ÚÚÚÚÚ¥¥¥¥¥¥¥[ZZZZ\007\007\007ssssszzzz{{{{ÙÙÙÚUUUUUUUUUeeee\007\007sssssszzØØØØØ{ÙÙÙUUUUUUUUUUeeee33sssssrØØØØØØØØÙÙ***UUUUUU\\\\eee333333rrrrrrØØØØ\024\024µµµ*\\\\\\\\\\\\\\\\\\\\333333rrrrrrrrrr\024\024µµµµµ\\\\\\\\\\\\\\\\\\T¾32222ÑÑrrrrrÒÒÒÒ°°°°°°°°\\\\\\\\TT¾¾¾2222ÑÑÑÑÑÑÑÒÒÒÒ°°°°°°°°°»]]]]]]]jjjjjjÑÑÑÑÑÒÒÒÒ°°°°°°°°°°»]]]]]]]jjjjjjjÑÑÑÒÒÒÒ°°°°°°°°,,»»]]]]]]djjjjjjj\021ÔÔÖÖÖ,,,,,,,,,,,,^^^^^^^d\021\021\021\021\021\021\021ÔÔÔÔÔ¯¯¯¯¯¯,,,,,,^^^^^^^^^\021\021\021\021\021\021ÔÔÔÔÔ¯¯¯¯¯¯¯¯¯¯¯ÀÀ^^^^^^^^^ÇÇÇkkÆÆÔÔÔ¯¯¯¯¯¯¯¯¯¯ÀÀÀÀÀ^^^^^^ÇÇÇÇÇÇÇÆÆÆÆ·····¯¯¯¯ÀÀÀÀÀÀÀÀÁÁÁÁÁÇÇÇÇÇÇÅÅÅÅ·······¹¹¹¹¹¹ÀÀÀÁÁÁÁÁÁÁÁÇÇÇÅÅÅÅÅ···¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÁÁÁÁÁÁÃÃÃ\022ÅÅÅÅ-----¹¹¹¹¹¹¹¹¹¹¸ÂÂÂÂÂÂÁÃÃÃÃÃ\022\022\022\022-------¹¹¹¹¹¹¹¸¸¸ÂÂÂÂÂÂÃÃÃÃÃÃ\022\022\022\"õõõõõòòòòòññññññááááâââââââåõõõõõòòòòòññññññááááâââââââåõõõõòòòòòññññññáááââââââââåõõõõòòòòòññññññáááâââââââååõõõòòòòòññññññáááâââââââåå£££££õòòòòññññññáááââââââÛÛÛ££££££====ñááââââââÛÛÛÛ££££££=====\026\026ââââÛÛÛÛÛÛ#####>>>>>>yyyy\026ããÛÛÛÛÛÛÛ¤¤¤¤¤GG>>>>\006yyyyyyyÛÛÛÛÛÛÛÛ¤¤¤¤¤GGGG\006\006\006\006\006yyyyyÛÛÛÛÛÛÛ¥¥¥¥¥¥GGG\006\006\006\006\006\006zzzÚÚÚÚÚÚ¥¥¥¥¥¥¥ZZZZZ\007\007\007\007sszzzzzz{{{ÙÙÙÚÚ¥¥¥¥¥¥¥JJZZ\007\007\007\007\007ssszzzzz{{{{ÙÙÙÙUUUUUUUJJJee\007\007\007\007sssszzz{{{{{{ÙÙÙ***UUUUUUUeeee3333ssszØØØ{{{{{ÙÙ*****UUUU\\\\eee333333rrrrrrrØØ\024\024\024*****\\\\\\\\\\\\TTT3333333rrrrrrrr\024\024\024µµµµ\\\\\\\\\\\\TTTTT222222ÑÑÑÑÑrrÒÒÒÒ°°°°°°°°TTTTTT]]22222ÑÑÑÑÑÑÑÒÒÒÒ°°°°°°°°°°]]]]]]]jjjjjjÑÑÑÑÑÒÒÒÒ°°°°°°°°°°]]]]]]]]jjjjjjjÑÑÑÒÒÒÒ°°°°°°°°°°°]]]]]]]djjjjjjjqqÔÔÔÔ,,,,,,,,,,SS^^^^^^^dd\021\021\021\021\021\021ÔÔÔÔÔ¯¯¯¯¯¯¯¯,,SS^^^^^^^^^\021\021\021kkkÔÔÔÔÔ¯¯¯¯¯¯¯¯¯¯¯ÀS^^^^^^^^_kkkkkÆÆÆÔÔ¯¯¯¯¯¯¯¯¯¯ÀÀÀÀÀ^^^^^^_ÇÇÇÇÇÆÆÆÆÆ¯¯¯¯¯¯¯¯¯¯ÀÀÀÀÀÀÁÁÁÁÁÁÇÇÇÇÇÅÅÅÅÅ·····¹¹¹¹¹¹¹¹ÀÀÀÁÁÁÁÁÁÁÁÇÇÇÅÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹ÂÂÁÁÁÁÁÁÃÃÃ\022ÅÅÅÅ----¹¹¹¹¹¹¹¹¹¹¸¸¸ÂÂÂÂÂÃÃÃÃÃÃ\022\022\022\022------¹¹¹¹¹¹¸¸¸¸¸¸ÂÂÂÂÃÃÃÃÃÃÃ\022\022\022!õõõòòòòòòò\031ññññáááâââââââåå!õõõòòòòòòò\031ññññáááâââââââåå!õõõòòòòòò\031ññññááâââââââååå!õõõòòòòòò\031ññññááâââââââååå!õõòòòòòòñññññááâââââââååå££££====òòòò\031ñññññááâââââââååå££££££>===ñðãããããâÛÛÛÛÛ#####>>>>>ãããããããÛÛÛÛÛ#####>>>>>>ããããÛÛÛÛÛÛ¤¤¤GGGG>>>>>ÛÛÛÛÛÛÛ¤¤¤GGGGGGG\006\006\006\006ÛÛÛÛÛÛ¥¥¥¥¥GGGGG\006\006\006\006\006zzzÙÙÙÚÚ¥¥¥¥¥¥¥ZZZ\007\007\007\007\007\007\007zzzzzz{{{ÙÙÙÙÙ¥¥¥¥¥JJJJJ4\007\007\007\007\007sszzzzzz{{{{ÙÙÙÙ***UJJJJJJJe\007\007\007\007ssszzzz{{{{{{ÙÙÙ*****UUUJJeee333333szzz{{{{{{\024\024\024*******UTTTTT33333333rrrrr{{\024\024\024\024******TTTTTTTT3333332rrrrrrr\024\024\024\024°°°*TTTTTTTTTTT222222ÑÑÑÑÑÑÑÒÒÒ\024°°°°°°°°TTTTT]]]22222ÑÑÑÑÑÑÑÒÒÒÒ°°°°°°°°°]]]]]]]]jjjjjjÑÑÑÑÑÒÒÒÒ°°°°°°°°°°]]]]]]]]jjjjjjjqqqÒÒÒÒ°°°°°°°°°°SS]]]]]dddjjjjjqqqÔÔÔÔ,,,,,,,,SSSSS^^^^^ddd\021\021\021\021kkÔÔÔÔÔ¯¯¯¯¯¯¯¯¯SSSS^^^^^^^__kkkkkkÔÔÔÔ¯¯¯¯¯¯¯¯¯¯¯SS^^^^^^___kkkkkÆÆÆÆÔ¯¯¯¯¯¯¯¯¯¯¯ÀÀÀ^^^^^___ÇÇÇÇkÆÆÆÆÆ¯¯¯¯¯®®®®®®ÀÀÀÀÀÁÁÁÁÁÁ`ÇÇÇÇÅÅÅÆÆ®®®®®®®®®¹¹¹¹¹ÀÁÁÁÁÁÁÁÁÁÇÇÅÅÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¹¹¸ÂÂÂÁÁÁÁÃÃÃÃÃÅÅÅÅ--¹¹¹¹¹¹¹¹¹¹¹¸¸¸¸ÂÂÂÂÂÃÃÃÃÃÃÃ\022\022Ä------¹¹¹¹¹¸¸¸¸¸¸¸ÂÂÂÂÃÃÃÃÃÃÃ\022\022\022!!!!!!!õòòòòò\031\031\031\031\031\031\031ððððââââåååå!!!!!!!õòòòòò\031\031\031\031\031\031\031ððððââââåååå!!!!!!!õòòòòò\031\031\031\031\031\031\031ððððââââåååå!!!!!!!ÿÿòòòò\031\031\031\031\031\031\031ððððââââåååå!!!!!!!ÿÿòòòò\031\031\031\031\031\031\031ððððââââåååå###!!!!ÿÿÿ\031\031\031\031\031\031\031ðððãããããåååå#####>>>>>\031ðððãããããããÛÛÛ#####>>>>>ããããããããÛÛÛÛ#####>>>>>>ããããããÛÛÛÛ###GGGG>>>>>ãÛÛÛÛÛGGGGGGGGGGG??ÛÛÛÛ¥¥¥¥¥GGGGGG\006\006\007zzÙÙÙÙÙ¥¥¥¥¥¥JJG444\007\007\007\007zzzzzz{{{ÙÙÙÙÙ¥¥¥JJJJJJJ444\007\007\007\007zzzzzzz{{{{ÙÙÙÙ****JJJJJJJ444\007\007\007sszzzz{{{{{ÙÙÙÙ******JJJJJFF3333333zzz{{{{{{\024\024\024*******TTTTT¦33333333rrr|{{{\024\024\024\024******TTTTTTTT3332222Ñ||||||\024\024\024\024°°°*TTTTTTTTTTT222222ÑÑÑÑÑÑ||\024\024\024°°°°°°°°TTTT]]]2222222ÑÑÑÑÑÑÑÒÒÒ°°°°°°°°°]]]]]]]]jjjjjjÑÑÑÑÑÑÒÒÒ°°°°°°°°°°]]]]]]]ddjjjjjqqqqqÓÓÓ°°°°°°°°°SSS]]]]ddddjjjjqqqqÔÔÓÓ¯¯¯¯°°SSSSSSS^^^^dddddkkkkkÔÔÔÔÔ¯¯¯¯¯¯¯¯SSSSS^^^^^___kkkkkkkÔÔÔÔ¯¯¯¯¯¯¯¯¯¯¯SSS^^^^____kkkkkÆÆÆÆÆ¯¯¯¯¯¯¯¯¯¯¯¯ÀÀ^^^^_____kkkkÆÆÆÆÆ®®®®®®®®®®®®ÀÀÀÀÁÁÁÁ````ÇÇÅÅÆÆÆÆ®®®®®®®®®®®®¹¹¹ÁÁÁÁÁÁÁ```cÅÅÅÅÅÅ¹¹¹¹¹¹¹¹¹¹¹¹¹¸¸¸ÂÂÂÁÁÁÃÃÃÃÃÃÄÄÄÄ¹¹¹¹¹¹¹¹¹¹¹¹¸¸¸¸¸ÂÂÂÂÃÃÃÃÃÃÃÃÄÄÄ----­­­­­­¸¸¸¸¸¸¸¸¸ÂÂÂÃÃÃÃÃÃÃÃ\022\022!!!!!!ÿÿÿòò\031\031\031\031\031\031\031\031\031ððððððãååååå!!!!!!ÿÿÿòò\031\031\031\031\031\031\031\031\031ððððððãååååå!!!!!!ÿÿÿÿò\031\031\031\031\031\031\031\031\031ððððððããåååå!!!!!!ÿÿÿÿò\031\031\031\031\031\031\031\031\031ðððððãããåååå!!!!!!ÿÿÿÿÿ\031\031\031\031\031\031\031\031ððððððãããåååå####!!ÿÿÿÿÿ\031\031\031\031\031\031ðððððãããããååå#####>>>>>\031ðððããããããããåå#####>>>>>ðãããããããããÛÛ#####>>>>>>ããããããÛÛÛ###GGGGG>>??ããÛÛÛGGGGGGGGGG????ÙÙÙ¥¥¥¥GGGGGG????zÙÙÙÙ¥¥¥¥JJJJ444444\007\007zzzzzzz{{{ÙÙÙÙÙJJJJJJJJJ444444\007\007zzzzzzz{{{ÙÙÙÙÙ****JJJJJJFF44444\bzzzzz{{{{{ÙÙÙÙ*******JJJFFF3\b\b\b\b\b\bzzz{{{{{\024\024\024\024*******TTTT¦¦¦\b\b\b\b\b\b\b||||||\024\024\024\024\024******TTTTTTT¦¦\b\b2222|||||||\024\024\024\024°°**TTTTTTTTKKK2222222ÑÑÑ||||\024\024\024°°°°°°°TTTT]]]]2222222ÑÑÑÑÑÑÑÒÒÒ°°°°°°°°°]]]]]]]]jjjjjqqÑÑÑÑÑÒÒÒ°°°°°°°°°S]]]]]]dddjjjjqqqqqqÓÓÓ°°°°°°°SSSSS]]]]dddddjjqqqqqqÓÓÓ¯¯¯¯¯SSSSSSSSS^^dddddkkkkkkqÔÔÔÓ¯¯¯¯¯¯¯¯SSSSSS^^_____kkkkkkkÆÔÔÔ¯¯¯¯¯¯¯¯¯¯SSSS^^______kkkkkÆÆÆÆÆ¯¯¯¯¯¯¯¯¯¯¯¯SS^^______`kkkkÆÆÆÆÆ®®®®®®®®®®®®®®ÀÁÁ````````cÆÆÆÆÆÆ®®®®®®®®®®®®®®¸¸ÁÁÁ`````cccÅÅÅÅÅ®®®®®¹¹¹¹¹¹¹¸¸¸¸¸ÂÂÁÁÃÃÃÃÃÃcÄÄÄÄ­­­­­­­­­­­¸¸¸¸¸¸¸ÂÂÂÃÃÃÃÃÃÃÃÄÄÄ­­­­­­­­­­­¸¸¸¸¸¸¸¸ÂÂÃÃÃÃÃÃÃÃÃÄÄ!!!!!ÿÿÿÿÿ\031\031\031\031\031\031\031\031\031ððððððððãåååå!!!!!ÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ððððððððãåååå!!!!!ÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ðððððððããåååå!!!!!ÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ðððððððãããååå!!!!!ÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ððððððããããååå#####ÿÿÿÿÿþþ\031\031\031\031\031ðððððããããããåå#####>>>þþþöððððãããããããåå#####>>>>þþðããããããããããä#####>>>>>?ããããããää###GGGGG?????ãäääGGGGGGGG??????äää¥¥¥GGGGGG??????ÙÙÙÙJJJJJJJF44444444zzzzzÙÙÙÙÙJJJJJJJJFF4444444zzzzzz{{{{ÙÙÙÙÙ****JJJJFFFFF444\b\bzzzzz{{{{{ÙÙÙÙ*******JJ¦FFFF\b\b\b\b\b\b|||{{{{{\024\024\024\024*******TTK¦¦¦¦\b\b\b\b\b\b\b||||||\024\024\024\024\024*****TTTKKKK¦¦¦\b\b\b222|||||||\024\024\024\024§§§§§§KKKKKKKKK2222222Ñ||||||\024\024\024°°°°°§§§LKKKK]]222222\tÑÑÑÑÑ|||ÒÒ°°°°°°°°LL]]]]]]dd1jjqqqqqqqÓÓÓÓ°°°°°°°°SS]]]]]]ddddjjqqqqqqÓÓÓÓ°°°°°SSSSSSSS]]ddddddjqqqqqqÓÓÓÓ¯¯¯¯SSSSSSSSSS^__ddddkkkkkkqÔÓÓÓ¯¯¯¯¯¯¯SSSSSSS_______kkkkkkkÆÆÔÔ¯¯¯¯¯¯¯¯¯¯SSSS________kkkkkÆÆÆÆÆ¯¯¯®®®®®®®®®SS______```kkkkÆÆÆÆÆ®®®®®®®®®®®®®®®`````````cclÆÆÆÆÆ®®®®®®®®®®®®®®¸¸````````ccclÄÄÄÄ®®­­­­­­­­­­¸¸¸¸¸ÂÂ``ÃÃÃÃcccÄÄÄÄ­­­­­­­­­­­¸¸¸¸¸¸¸ÂÂÃÃÃÃÃÃÃÃÄÄÄÄ­­­­­­­­­­­¸¸¸¸¸¸¸¸...ÃÃÃÃÃÃÃÃÄÄ!!!ÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ððððððððããååå!!!ÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ððððððððããååå!!!ÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031\031ððððððððããååå!!!ÿÿÿÿÿÿÿÿÿ\031\031\031\031\031\031\031ðððððððãããåååÿÿÿÿÿÿÿþöööööööðððððððããããåå###ÿÿÿÿþþþþööööööððððððãããããåå######þþþþþþþöööööðððãããããããää######>þþþþþþãããããããäää######>>??þþþããããäää¢¢¢¢¢GG??????ääää¢¢¢¢¢¢G???????äää¢¢¢¢¢¢G????????ÙÙÙÙJJJJJJFFF4444444zzzÙÙÙÙÙJJJJJJFFFFF444444zzzzzz{{{{ÙÙÙÙÙ*****JJFFFFFFF4\b\b\b\bzzzz{{{{{ÙÙÙÙ*******¦¦¦¦FFF\b\b\b\b\b\b|||||{{\024\024\024\024\024******KKKK¦¦¦¦\b\b\b\b\b\b||||||||\024\024\024\024****KKKKKKKK¦¦¦\b\b\t\t\t\t|||||||\024\024\024\024§§§§§§§KKKKKKKK\t\t\t\t\t\t\t|||||||\024\024\024§§§§§§§§LLLKKKK\t\t\t\t\t\t\tqÑÑ|||||Ó\024°°°°°°LLLLLL]]]]d111\n\nqqqqqqÓÓÓÓ°°°°°°SSSSLL]]]dddd11qqqqqqqÓÓÓÓ°°SSSSSSSSSSS]dddddddkqqqqqqÓÓÓÓ¯¯SSSSSSSSSSSS____dddkkkkkkppÓÓÓ¯¯¯¯¯¯SSSSSSSS_______kkkkkkkoooÓ¯¯¯¯¯¯¯¯¯SSSSS________kkkkkkÆÆÆÆ®®®®®®®®®®®®SS____`````kkkkÆÆÆÆÆ®®®®®®®®®®®®®®``````````ccllÆÆÆÆ®®®®®®®®®®®®®®¸¸````````cccllÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸`````cccccÄÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸....ÃÃÃÃÃbÄÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸¸....ÃÃÃÃÃÃÄÄÄÿÿÿÿÿÿÿÿööööööööðððððððððããååÿÿÿÿÿÿÿÿööööööööðððððððððããååÿÿÿÿÿÿÿÿÿöööööööððððððððãããååÿÿÿÿÿÿÿþööööööööðððððððãããååÿÿÿÿþþþööööööööððððððããããääÿþþþþþþöööööööðððððãããããää##þþþþþþþþööööööððððãããããäää#####þþþþþþþþþööööãããããääää¢¢¢¢¢¢???þþþþþäääää¢¢¢¢¢¢¢??????ääää¢¢¢¢¢¢¢???????ääää¢¢¢¢¢¢¢????????Ùää¡¡¡¡¡FFFF555554ÙÙÙÙJJJJJFFFFFF5555@@{{{{ÙÙÙÙÙ*****FFFFFFFF5@@@@{{{{\024\024\024\024******¦¦¦¦¦¦¦\b\b\b\b\b\b||||\024\024\024\024\024****KKKKKK¦¦¦¦\b\b\b\b\b\b||||||||\024\024\024\024§§§§§KKKKKKKK¦¦\t\t\t\t\t\t||||||||\024\024\024§§§§§§§§KKKKKKK\t\t\t\t\t\t\t|||||||\024\024\024§§§§§§§§LLLLLL111\t\t\t\t\tqqq|||}}çç§§§§§§LLLLLLLLL11111\n\nqqqqq}}ÓÓÓ°¨¨¨SSSSLLLLLLdddd11\n\nqqqqq}}ÓÓÓ¨¨¨SSSSSSSSSSSddddddd\nqqqqqqÓÓÓÓ¨¨¨SSSSSSSSSSS_____dkkkkkkpppÓÓÓ¯¯¯¯¯SSSSSSSSS_______kkkkkkooooo¯¯¯¯¯¯¯¯SSSSSS_______RkkkkkkÆÆÆÆ®®®®®®®®®®®®SS__````````kkkÆÆÆÆÆ®®®®®®®®®®®®®®``````````cclllÆÆÆ®®®®®®®®®®®®®¸¸````````ccccllÄÄÄ­­­­­­­­­­­­­¸¸¸¸..````ccccclÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸.....ÃÃbbbbÄÄÄ­­­­­­­­­­­­¸¸¸¸¸¸......ÃÃÃÃb\023\023\023ÿÿÿÿÿÿÿöööööööööððððððððããääÿÿÿÿÿÿÿöööööööööððððððððããääÿÿÿÿÿÿþööööööööððððððððããääÿÿÿÿÿþþööööööööðððððððãããääÿÿþþþþöööööööööðððððãããäääþþþþþþþööööööööððððããããäääþþþþþþþþöööööööðððããããääää¢¢¢¢¢¢þþþþþþþþþööööö\032ããäääää¢¢¢¢¢¢¢?þþþþþþäääää¢¢¢¢¢¢¢??????ääää¢¢¢¢¢¢¢¢??????ääää¢¢¢¢¢¢¢¢??????äää¡¡¡¡¡¡¡55555555@ÙÙÙæ¡¡¡¡¡FFFF55555@@@{ÙÙÙÙÙ****FFFFFFF555@@@@\024\024\024\024\024****KK¦¦¦¦¦¦¦@@@@@@|\024\024\024\024\024§§§§KKKKKK¦¦¦¦\b\b\b\b\t\t||||||||\024\024\024\024§§§§§§KKKKKKK¦\t\t\t\t\t\t\t||||\024\024\024§§§§§§§§KKKKKK\t\t\t\t\t\t\t\tççç§§§§§§§LLLLLLL11111\n\n\n\nqq}}çç§§§§§LLLLLLLLLL11111\n\n\nqqq}}}}ÓÓ¨¨¨¨¨¨SLLLLLLLLdd111\n\n\nqqq}}}}ÓÓ¨¨¨¨¨¨SSSSSSSMMMdddd\013\013\013\013qqpp}ÓÓÓ¨¨¨¨¨¨SSSSSSSS_____RkkkkkpppppÓÓ¯¯¯¨¨¨SSSSSSSS____RRRkkkkkkooooo®®®®®®®®)SSSSS__RRRRRRkkkkkooooo®®®®®®®®®®®®))``````````cklÆÆÆÆÆ®®®®®®®®®®®®®®`````````cccllllln®®®®®®®®®®®®®¸¸````````cccclllÄÄ­­­­­­­­­­­­­¸¸¸¸...``cccccllÄÄÄ­­­­­­­­­­­­­¸¸¸¸.......bbbbbÄÄÄ­­­­­­­­­­­­­¸¸¸¸¸........bbb\023\023\023ÿÿÿÿÿþööööööööööðððððïïîîîîÿÿÿÿþööööööööööðððïïïïîîîîÿÿÿþþþöööööööööïïïïïïïîîîîÿÿþþþþöööööööööïïïïïïïîîîîþþþþþöööööööööïïïïïïïîîääþþþþþþþööööööööïïïïïïîääääþþþþþþþþööööööö\032\032\032ïïïäääää¢¢¢¢¢  þþþþþþþþöööö\032\032\032\032\032\032\032äääää¢¢¢¢¢¢¢  þþþþþ\032\032\032äääää¢¢¢¢¢¢¢¢?????äääää¢¢¢¢¢¢¢¢??????ääää¡¡¡¡¡¡¡¡5555555ææææ¡¡¡¡¡¡¡5555555@@@æææææ¡¡¡¡¡¡¡5555555@@@æææææ$$$$$$$$F5555@@@@@\024ææææ$$$$$$$$$¦¦¦@@@@@@@\024\024\024\024§§§§§KKKKK¦¦¦¦@@@\t\t\t\024\024\024§§§§§§§KKKKKK¦\t\t\t\t\t\t\tççç§§§§§§§§§LLKKK\t\t\t\t\t\t\t\tççç§§§§§§§LLLLLLL11111\n\n\n\n\n}}ççç§§§§§LLLLLLLLLL11111\n\n\n\nq}}}}}}Ó¨¨¨¨¨¨¨LLLLLLLMM1111\n\n\nqq/}}}}}Ó¨¨¨¨¨¨¨¨SSSMMMMMMMd1\013\013\013\013\013ppp}}ÓÓ¨¨¨¨¨¨¨¨SSSSSMMMRRRR\013\013\013\013\013ppppppÓ¨¨¨¨¨¨))))))SRRRRRRRRkkkkkoooooo®®®®®®®))))))RRRRRRRRRkkkkoooooo®®®®®®®®®®®)))`````````cclllnnnn®®®®®®®®®®®®®``````````cccllllnn®®®®®®®®®®­­­­¸````````ccccllllÄ­­­­­­­­­­­­­­¸¸.....`cccbbbllÄÄ­­­­­­­­­­­­­¸¸¸........bbbbbÄÄÄ­­­­­­­­­­­­­¸¸¸¸.........bbb\023\023\023ýýýýýþöööööööööïïïïïïïîîîîýýýýýþöööööööööïïïïïïïîîîîýýýýýþöööööööööïïïïïïïîîîîýýýýþþöööööööööïïïïïïïîîîîýýýþþþþööööööööïïïïïïïîîîîýýþþþþþööööööö\032\032ïïïïïïîîîî¢¢     þþþþþþöööööö\032\032\032\032\032ïïîîîîî¢¢¢¢      þþþþþööö\032\032\032\032\032\032\032îîîää¢¢¢¢¢¢      þþ\032\032\032\032\032äääää¢¢¢¢¢¢¢       \032\032\032äääää¡¡¢¢¢¢¢¢   ???ææææä¡¡¡¡¡¡¡¡555555æææææ¡¡¡¡¡¡¡¡555555@@ææææææ¡¡¡¡¡¡¡555555@@@@ææææææ$$$$$$$$$555@@@@@@æææææ$$$$$$$$$$$$@@@@@@@\024\024ææ§§§§$$$$$$$$$666@AAAçççç§§§§§§§§$$$$$E\t\tAAAAçççç§§§§§§§§§LLLEEE1\t\t\t\t\nçççç§§§§§§§LLLLLLEE1111\n\n\n\n}}}ççç§§§§§LLLLLLLLLE1111\n\n\n\n//}}}}}}}¨¨¨¨¨¨¨¨LLLLMMMMM111\n\013\013///}}}}}}¨¨¨¨¨¨¨¨¨))MMMMMMMM\013\013\013\013\013\013ppp}}}Ó¨¨¨¨¨¨¨)))))MMMMRRRR\013\013\013\013\013pppppÕÕ¨¨¨))))))))))RRRRRRRRRkkkooooooÕ®®®®®))))))))RRRRRRRRRRk\f\foooooo®®®®®®®®®®))))`````````cc\fllnnnn®®®®®®®®®®®®®`````````ccccllllnn®®®®®®®­­­­­­­````````cccccllllÄ­­­­­­­­­­­­­­¸.....QQQbbbbblllÄ­­­­­­­­­­­­­­¸¸........bbbbbmmm­­­­­­­­­­­­­¬¸¸..........bb\023\023\023\023ýýýýýýýý÷öööööööïïïïïïïïîîîîýýýýýýýý÷÷ööööööïïïïïïïïîîîîýýýýýýýý÷÷ööööööïïïïïïïïîîîîýýýýýýýý÷÷ööööööïïïïïïïïîîîîýýýýýýýý÷÷÷öööööïïïïïïïïîîîî     ýýýýýýþ÷÷÷öööö\032\032\032ïïïïïîîîî          ýþþ÷÷÷÷öö\032\032\032\032\032\032\032ïîîîîî¢¢¢         þþ÷÷÷\032\032\032\032\032\032\032\032îîîîî¢¢¢¢          \032\032\032\032\032\032\032îîîî¢¢¢¢¢¢        \032\032\032\032\032æîîä¡¡¡¡¡¡¡¡     \032æææææ¡¡¡¡¡¡¡¡55555@ææææææ¡¡¡¡¡¡¡¡55555@@@ææææææ¡¡¡¡¡¡¡$5555@@@@@ææææææ$$$$$$$$$$55@@@@@@æææææ$$$$$$$$$$$$666@@@ççææ§§§$$$$$$$$$66666AAçççç§§§§§§§$$$$$EEAAAAAAAçççç§§§§§§§§§LLEEEEEAAAAAçççç§§§§§§§LLLLLEEEEE11\n\n\n\n/}}}ççç¨¨¨¨§LLLLLLLLEEE111\n\n\n////}}}}}ç¨¨¨¨¨¨¨¨LLLMMMMMM01\013\013\013\013///}}}}}}¨¨¨¨¨¨¨))))MMMMMMM0\013\013\013\013\013\013/pp}}ÕÕ¨¨¨¨¨)))))))MMMMRRRR\013\013\013\013\013ppppÕÕÕ)))))))))))))RRRRRRRRN\013\013\fooooooÕ®®®)))))))))))RRRRRNNN\f\f\f\f\foonnn®®®®®®®®®))))©````````cc\f\f\fnnnnn®®®®®®®®®®®©©©````````ccccllllnn­­­­­­­­­­­­­©©````QQQQcccllllll­­­­­­­­­­­­­­......QQQbbbbblllm­­­­­­­­­­­­­¬¬........bbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.........bbb\023\023\023\023ýýýýýýýýýý÷÷÷÷÷÷ööïïïïïïïïîîîîýýýýýýýýýý÷÷÷÷÷÷ööïïïïïïïïîîîîýýýýýýýýýý÷÷÷÷÷÷ööïïïïïïïïîîîîýýýýýýýýýý÷÷÷÷÷÷÷öïïïïïïïïîîîî    ýýýýýýýý÷÷÷÷÷÷÷\032\032ïïïïïïïîîîî       ýýýýý÷÷÷÷÷÷÷\032\032\032\032\032ïïïïîîîî           ýý÷÷÷÷÷÷\032\032\032\032\032\032\032\032îîîîî             ÷÷÷÷÷\032\032\032\032\032\032\032\032\032îîîî¢¢¢           \032\032\032\032\032\032\032îîîî¡¡¡¡¡        \032\032\032\032\032ææææ¡¡¡¡¡¡¡\032\032æææææ¡¡¡¡¡¡¡¡ææææææ¡¡¡¡¡¡¡¡5555@@@ææææææ$$$$$$$$$55@@@@@@ææææææ$$$$$$$$$$$6666@@æææææ$$$$$$$$$$$6666666çççæ$$$$$$$$$$$$6666AAAçççç§§§§§§§$$$$$E7AAAAAAAçççç§§§§§§§§LLEEEEEEAAAAAççççç§§§§§LLLLEEEEEEE1\n\n\n//}}ççççLLLLLEEEEEE1\n\n/////}}}}èè¨¨¨¨¨¨¨¨)LMMMMMM0000\013\013/////}}}}è¨¨¨¨¨¨)))))MMMMMM000\013\013\013\013///p}ÕÕÕ))))))))))))MMMMNNNN\013\013\013\013\013ppppÕÕÕ)))))))))))))RNNNNNNNN\f\f\f\fooooÕÕ)))))))))))))NNNNNNNNN\f\f\f\f\f\fnnnn®®®®®®®®)©©©©©©```````c\f\f\f\f\fnnnn®®®®®®®®®©©©©©©````QQQcccclllnnn­­­­­­­­­­­­©©©©QQQQQQQccblllll~­­­­­­­­­­­­­¬.....QQQQbbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬........bbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.........bb\023\023\023\023\023ýýýýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïîîîîýýýýýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïïîîîîýýýýýýýýýýý÷÷÷÷÷÷÷÷ïïïïïïïïîîîîýýýýýýýýý÷÷÷÷÷÷÷÷\032ïïïïïïïîîîî    ýýýýýý÷÷÷÷÷÷÷÷\032\032\032ïïïïïîîîî         ýýý÷÷÷÷÷÷÷\032\032\032\032\032\032ïïïîîîî            ÷÷÷÷÷÷÷\032\032\032\032\032\032\032\032íîîîî             ÷÷÷÷÷÷\032\032\032\032\032\032\032\032\032îîîî             \032\032\032\032\032\032\032\032íîîî¡¡¡¡¡ \032\032\032\032\032\032ææææ¡¡¡¡¡¡\032\032\032æææææ¡¡¡¡¡¡¡ææææææ¡¡¡¡¡¡¡¡@æææææææ   $$$$$$$66666@ææææææ$$$$$$$$$$6666666æææææ$$$$$$$$$$$6666666çççëë$$$$$$$$$$$$6666AAAççççç§§§§§§$$$$$7777AAAAAçççççEEEEEEEAAAAAççççEEEEEEEEAA}èçççLEEEEEE000//////}}èèè¨¨¨¨¨¨))MMMMMM00000\013\013/////}}}èè¨¨¨))))))))MMMMM0000\013\013\013\013///pÕÕÕÕ))))))))))))MMNNNNNN\013\013\013\013\013pppÕÕÕÕ)))))))))))))NNNNNNNNN\f\f\f\f\fooééé))))))))))))©NNNNNNNNN\f\f\f\f\f\fnnnn®®®®®®©©©©©©©©©©NNNQQOO\f\f\f\f\fnnnn®®®®®®®©©©©©©©©©QQQQQQQccll\r\rn~~­­­­­­­­­­©©©©©©QQQQQQQQbbblll~~¬¬¬¬¬¬¬¬¬¬¬¬¬¬....QQQQQQbbbbmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬........bbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.........bb\023\023\023\023\023ýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïíííîîýýýýýýý÷÷÷÷÷÷÷÷÷ïïïïïïïíííîîýýýýýý÷÷÷÷÷÷÷÷÷\032ïïïïïïíííîîýýýýý÷÷÷÷÷÷÷÷÷\032\032ïïïïííííîî ýýýýý÷÷÷÷÷÷÷÷\032\032\032\032ïïííííîî    ýý÷÷÷÷÷÷÷÷\032\032\032\032\032\032ííííîî      üüü÷÷÷÷÷\032\032\032\032\032\032ííííîî        üüüüü÷\032\032\032\032\032\032\032ííííîî    \032\032\032\032\032\032\032ííííî¡¡¡\032\032\032\032\032\032ííæææ¡¡¡¡\032\032\032æææææ¡¡¡¡¡æææææææ       æææææææ         6666666ëæææææ      $$$$6666666ëëëëëë$$$$$$$$$$$666666ëëëëë$$$$$$$$$$$77776AAAççççç$$$777777AAAAççççEEEEE777AAAAççççEEEEEEEEAAèèèèèEEEE00000èèèè¨¨¨MMMM0000000B//èèèè)))))))))))MMMM000000\013\013/////ÕÕÕÕ))))))))))))NNNNNNNNDD\013\f\fééÕÕ))))))))))))NNNNNNNNND\f\f\f\f\f\fééé))))))))©©©©©©NNNNNNND\f\f\f\f\f\fnnné©©©©©©©©©©©©©©©©NNOOOOO\f\f\f\f\fnnn~ªªªª©©©©©©©©©©©©©QQQQOOOO\r\r\r\r~~~¬¬¬¬¬¬¬¬¬©©©©©©©QQQQQQQQbb\r\r\r\r~~¬¬¬¬¬¬¬¬¬¬¬¬¬¬©..QQQQQQQbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.......QQbbbmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.........PP\023\023\023\023\023ýýýýü÷÷÷÷÷÷÷÷÷ïïïïíííííííýýýýü÷÷÷÷÷÷÷÷÷ïïïïíííííííýýýýüü÷÷÷÷÷÷÷÷\032ïïïíííííííýýýüüü÷÷÷÷÷÷÷\032\032ïííííííííýýüüüüü÷÷÷÷\032\032\032\032íííííííí  üüüüüüü÷÷\032\032\032\032\032ííííííí   üüüüüüüüü\032\032\032\032\032ííííííí üüüüüüüü\032\032\032\032\032íííííííüüü\032\032\032\032\032íííííí\032\032\032\032íííííæ¡ø\032\032ææææææ     øøææææææ        øøëëëæææ          666666ùùëëëëëë          6666666ùëëëëëë       $$$7666666ëëëëë$$$$$$$$$7777777AAAçëëëE77777777AAAççççEEEE77777AAèèèççEEEEE8888èèèèèEEE00000BBèèèèMMM0000000BBBèèè)))))))))))MMMN00000BBBBÕÕ))))))))))))NNNNNNNDDDD\fééé)))))))))))©NNNNNNNNDDD\f\f\f\fééé©©©©©©©©©©©©©©©NNNNNDDD\f\f\f\f\fééªªª©©©©©©©©©©©©©©OOOOOOO\f\f\r\r\r~~~ªªªªªª©©©©©©©©©©©QQOOOOOO\r\r\r\r~~~¬¬¬¬¬¬¬¬©©©©©©©©©QQQQQOOO\r\r\r\r\r\177\177¬¬¬¬¬¬¬¬¬¬¬¬«««©.QQQQQQPPPPmmmm\177¬¬¬¬¬¬¬¬¬¬¬¬¬¬««.....QPPPPPmmmmm¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬.......'PPPPmaaaýüüüüüüüü÷÷÷\032ïíííííííííýüüüüüüüü÷÷÷\032\032íííííííííýüüüüüüüüü÷÷\032\032íííííííííüüüüüüüüüüü\032\032íííííííííüüüüüüüüüüü\032\032íííííííííüüüüüüüüüüü\032\032\032ííííííííüüüüüüüüüü\032\032\032ííííííííüüüüüüüüü\032\032\032ííííííííüüüüüüûûø\032\032\032íííííííøøøøøíííííì øøøøøììììì     øøøøøëëëëë       øøøøëëëëëë         66666ùùùëëëëëë          666666ùùùëëëëë          7777666ùùëëëëë%%%%%%%%777777777AAëëëëë%777777777AAççççEE8888877AAèèèèèE88888888èèèèè88888888BBèèè0000009BBBBBèèNNN09999BBBBNNNNNDDDDDééé©NNNNNDDDDDD\féé©©©©©©©©©©©©©©©NNNNDDDDD\f\f\féªªªªª©©©©©©©©©©©©OOOOOOO\r\r\r\r\r~~~ªªªªªªª©©©©©©©©©©OOOOOOOO\r\r\r\r\r~~¬¬ªªªª«««©©©©©©©©QQQOOOOPP\r\r\r\r\177\177¬¬¬¬¬¬¬¬¬«««««««©QQQQQPPPPP\016\016\016\016\177¬¬¬¬¬¬¬¬¬¬¬¬«««««...'PPPPPPP\016\016aa¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬««...'''''PPPaaaaüüüüüüüüüüüüííííííííí\033üüüüüüüüüüüüííííííííí\033üüüüüüüüüüüüííííííííí\033üüüüüüüüüüüüííííííííí\033üüüüüüüüüüüüííííííííí\033üüüüüüüüüüüü\032íííííííí\033üüüüüüüüüüü\032íííííííí\033üüüüüüüûûøøøííííííí\033ûûûûûûûûøøøøíííííí\033ûûûûûûûøøøøøøìììììûûûûûøøøøøøøììììì  øøøøøøøììììì      ùùùøøøøëëëëë        ùùùùùùëëëëë          ùùùùùëëëëë%%%%%%%% 777777\036\036\036ùùùùëëëëë%%%%%%%%%7777777\036\036\036ù\034\034\034\034ë%%%%%%7777777\036\036\036\036\034\034\034\034\0348888888888\036èèèèè888888888Bèèèè8888889BBBèè999999BBBBBè&999999BBBB&&&&:::DDDéé©&&&&DDDDDDDéªªªª©©©©©©©©©©©©&O;;;DDDD\féªªªªªª©©©©©©©©©©©OOOOO;;\r\r\r\r\r~~~ªªªªªªªª©©©©©©©©©OOOOOOO\r\r\r\r\r\177\177\177ªªªª«««««««©©©©©©OOOOOOPP<<<\016\177\177\177¬¬¬¬¬««««««««««««QQQPPPPPPP\016\016\016\016\177¬¬¬¬¬¬¬¬¬«««««««««''''PPPPPP\016\016aa(((((¬¬¬¬¬¬¬««««««''''''''PPaaaa\037\037\037\037\037\037üüüüüüüüüüüüíííííí\033\033\033\033\037\037\037\037\037\037\037üüüüüüüüüüüüíííííí\033\033\033\033\037\037\037\037\037\037\037\037üüüüüüüüüüüüíííííí\033\033\033\033\037\037\037\037\037\037\037\037\037\037üüüüüüüüüüüüíííííí\033\033\033\033\037\037\037\037\037\037\037\037\037\037üüüüüüüüüüüüíííííí\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037üüüüüüüüüüûøííííí\033\033\033\033\037\037\037\037\037\037üüüüüüûûûûûøøííííí\033\033\033ûûûûûûûûûøøøøíííí\033\033\033ûûûûûûûûøøøøøøìììììûûûûûûûøøøøøøøìììììûûûûûøøøøøøøìììììûûûøøøøøøøøìììì   ùùùùùùùùëëëëë      ùùùùùùùùëëëë%%%%    \036\036ùùùùùùùëëëë%%%%%%%%%%777\036\036\036\036\036\036\036ùùùùùù\034ëëë%%%%%%%%%%7777\036\036\036\036\036\036\036ùùùù\034\034\034\034\034%%%%%%%%%%88888\036\036\036\036\036\036\036\034\034\034\034\034\03488888888\036\036\036úúúúèè\034\03488888888BBúúúúèèè8889999BBBúúúúè9999999BBBBúú&&&9999:BBB&&&&&:::DDêê&&&&&::DDDD\035\035êªªªªªª©©©©©©©©©©&&;;;;;;;\035\035êªªªªªªªª©©©©©©©©©OOOO;;;;\rCCCCªªªªªªªªª©©©©©©©©OOOOOOO<<\r\r\r\177\177\177«««««««««««««©©©©OOOOPPPP<<<\016\016\177\177««««««««««««««««««'PPPPPPPP<\016\016\016\016(((((((«««««««««««'''''PPPPP\016\016\016a(((((((((((«««««««'''''''''aaaaa\037\037\037\037\037\037\037\037\037\037üüüüüüüüüüüüííí\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037üüüüüüüüüüüüííí\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037üüüüüüüüüüüûøíí\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037üüüüüüüüüûûøííí\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037üüüüüüûûûûûøøíí\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037üûûûûûûûûûûøøøí\033\033\033\033\033\033\037\037\037\037\037ûûûûûûûûûûøøøøø\033\033\033\033\033\033\037ûûûûûûûûûûøøøøøø\033\033\033\033\033ûûûûûûûûûøøøøøøìììììûûûûûûøøøøøøøìììììûûûûûøøøøøøøìììììûûûøøøøøøøøììììùùùùùùùùùùëììì%%%%ùùùùùùùùùëëëë%%%%%%%%%\036\036\036\036ùùùùùùùùëëëë%%%%%%%%%%%\036\036\036\036\036\036\036\036\036\036ùùùùùùù\034\034\034\034%%%%%%%%%%%7\036\036\036\036\036\036\036\036\036\036ùùùùù\034\034\034\034\034%%%%%%%%%%%888\036\036\036\036\036\036\036\036úúúù\034\034\034\034\034\0348888888\036\036\036úúúúúú\034\034\034\0348888888Búúúúúúèè999999BBúúúúú9999999BBBúúú&&&&&::::BBB&&&&&&:::::\035\035\035êêê&&&&&&:;;D\035\035\035\035\035êêêªªªªªªª©©©©©©©©&&&;;;;;;\035\035\035CCêêªªªªªªªªª©©©©©©©OOO;;;;;;CCCCCªªªªªªªªªª©©©©©©©OOOOOO<<<<CC«««««««««««««««©©OOOPPPP<<<<\016\016«««««««««««««««««'''PPPPPP<<\016\016\016\016(((((((««««««««««'''''''PPPP\016\016\017\017(((((((((((««««««'''''''''''\017\017\017\017\037\037\037\037\037\037\037\037\037\037\037üüüüüûûûûûûøø\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037üüüüüûûûûûûøø\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037üüüûûûûûûûûøø\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037ûûûûûûûûûûûøø\033\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037ûûûûûûûûûûûøøø\033\033\033\033\033\033\033\037\037\037\037\037\037\037\037ûûûûûûûûûûûøøø\033\033\033\033\033\033\033\037\037\037ûûûûûûûûûûøøøøø\033\033\033\033\033\033ûûûûûûûûûûøøøøøøìììììûûûûûûûûøøøøøøìììììûûûûûûøøøøøøøìììììûûûûûøøøøøøøìììììûûøøøøøøøøøììììùùùùùùùùùùìììì%%%%%ùùùùùùùùùùëëë%%%%%%%%%%\036\036\036\036\036ùùùùùùùù\034\034\034\034%%%%%%%%%%%\036\036\036\036\036\036\036\036\036\036ùùùùùùù\034\034\034\034%%%%%%%%%%%\036\036\036\036\036\036\036\036\036\036\036ùùùùù\034\034\034\034\034%%%%%%%%%%%88\036\036\036\036\036\036\036\036\036úúúú\034\034\034\034\034\03488888\036\036\036\036úúúúúú\034\034\034\0348888889úúúúúúú\034999999Búúúúúú&999999Búúúúú&&&&&:::::\035\035úê&&&&&&::::\035\035\035\035\035\035êêêê&&&&&&;;;;\035\035\035\035\035êêêêªªªªªªªªª©©©©©&&&&;;;;;;\035\035CCCêêêªªªªªªªªªª©©©©©©OO;;;;;;CCCCCªªªªªªªªªªª©©©©©©OOOOP<<<<<CC«««««««««««««««««OPPPPPP<<<<<«««««««««««««««««''''PPPPP<<\016\016\016\016(((((((««««««««««''''''''PP\016\017\017\017\017(((((((((((««««««'''''''''''\017\017\017\017"
            .getBytes(StandardCharsets.ISO_8859_1);

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
     * @param metric      should usually be {@link #oklabMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which may handle gradients better
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

//    /**
//     * Color difference metric; returns large numbers even for smallish differences.
//     * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
//     *
//     * @param color1 an RGBA8888 color as an int
//     * @param color2 an RGBA8888 color as an int
//     * @return the difference between the given colors, as a positive double
//     */
//    public static double difference(int color1, int color2) {
//        int indexA = (color1 >>> 17 & 0x7C00) | (color1 >>> 14 & 0x3E0) | (color1 >>> 11 & 0x1F),
//                indexB = (color2 >>> 17 & 0x7C00) | (color2 >>> 14 & 0x3E0) | (color2 >>> 11 & 0x1F);
//        final double
//                L = labs[0][indexA] - labs[0][indexB],
//                A = labs[1][indexA] - labs[1][indexB],
//                B = labs[2][indexA] - labs[2][indexB];
//        return (L * L * 7 + A * A + B * B);
//    }
//
//
//    /**
//     * Color difference metric; returns large numbers even for smallish differences.
//     * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
//     *
//     * @param color1 an RGBA8888 color as an int
//     * @param r2     red value from 0 to 255, inclusive
//     * @param g2     green value from 0 to 255, inclusive
//     * @param b2     blue value from 0 to 255, inclusive
//     * @return the difference between the given colors, as a positive double
//     */
//    public static double difference(int color1, int r2, int g2, int b2) {
//        int indexA = (color1 >>> 17 & 0x7C00) | (color1 >>> 14 & 0x3E0) | (color1 >>> 11 & 0x1F),
//                indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
//        final double
//                L = labs[0][indexA] - labs[0][indexB],
//                A = labs[1][indexA] - labs[1][indexB],
//                B = labs[2][indexA] - labs[2][indexB];
//        return (L * L * 7 + A * A + B * B);
//    }
//
//    /**
//     * Color difference metric; returns large numbers even for smallish differences.
//     * If this returns 250 or more, the colors may be perceptibly different; 500 or more almost guarantees it.
//     *
//     * @param r1 red value from 0 to 255, inclusive
//     * @param g1 green value from 0 to 255, inclusive
//     * @param b1 blue value from 0 to 255, inclusive
//     * @param r2 red value from 0 to 255, inclusive
//     * @param g2 green value from 0 to 255, inclusive
//     * @param b2 blue value from 0 to 255, inclusive
//     * @return the difference between the given colors, as a positive double
//     */
//    public static double difference(final int r1, final int g1, final int b1, final int r2, final int g2, final int b2) {
//        int indexA = (r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3),
//                indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
//        final double
//                L = labs[0][indexA] - labs[0][indexB],
//                A = labs[1][indexA] - labs[1][indexB],
//                B = labs[2][indexA] - labs[2][indexB];
//        return (L * L * 7 + A * A + B * B);
//    }

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
        exact(rgbaPalette, oklabMetric);
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
        exact(rgbaPalette, limit, oklabMetric);
    }
    /**
     * Builds the palette information this PNG8 stores from the RGBA8888 ints in {@code rgbaPalette}, up to 256 colors.
     * Alpha is not preserved except for the first item in rgbaPalette, and only if it is {@code 0} (fully transparent
     * black); otherwise all items are treated as opaque. If rgbaPalette is null, empty, or only has one color, then
     * this defaults to DawnBringer's Aurora palette with 256 hand-chosen colors (including transparent).
     *
     * @param rgbaPalette an array of RGBA8888 ints; all will be used up to 256 items or the length of the array
     * @param metric      should usually be {@link #oklabMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which handles gradients better
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
    }

    public void exactLAB(int[] rgbaPalette, int limit) {
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
        int[] Ls = new int[plen], As = new int[plen], Bs = new int[plen];
        for (int i = 0; i < plen; i++) {
            color = rgbaPalette[i];
            if ((color & 0x80) != 0) {
                paletteArray[i] = color;
                paletteMapping[(color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F)] = (byte) i;
            }
            paletteArray[i] = color;
            color = shrink(color);
            Ls[i] = Math.min(Math.max((int)(OKLAB[0][color] * 255.999), 0), 255);
            As[i] = Math.min(Math.max((int)((OKLAB[1][color] + 0.5) * 255.999), 0), 255);
            Bs[i] = Math.min(Math.max((int)((OKLAB[2][color] + 0.5) * 255.999), 0), 255);
            color = (Ls[i] << 7 & 0x7C00) | (As[i] << 2 & 0x3E0) | (Bs[i] >>> 3);
            paletteMapping[color] = (byte) i;
        }
        for (int L = 0; L < 32; L++) {
            int LL = (L << 3 | L >>> 2);
            for (int A = 0; A < 32; A++) {
                int AA = (A << 3 | A >>> 2);
                for (int B = 0; B < 32; B++) {
                    c2 = L << 10 | A << 5 | B;
                    if (paletteMapping[c2] == 0) {
                        int BB = (B << 3 | B >>> 2);
                        dist = Double.MAX_VALUE;
                        for (int i = 1; i < plen; i++) {
                            if (dist > (dist = Math.min(dist, oklabLABMetric.difference(Ls[i], As[i], Bs[i], LL, AA, BB))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }
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
        exact(colorPalette, 256, oklabMetric);
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
     * @param metric      should usually be {@link #oklabMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which handles gradients better
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
        exact(colorPalette, limit, oklabMetric);
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
     * @param metric      should usually be {@link #oklabMetric}, which is usually high-quality, or {@link #rgbEasyMetric}, which handles gradients better
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
        populationBias = Math.exp(-1.125/colorCount);
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
    public void exactLAB(Color[] colorPalette, int limit) {
        if (colorPalette == null || colorPalette.length < 2 || limit < 2) {
            exact(Coloring.AURORA, ENCODED_AURORA);
            return;
        }
        Arrays.fill(paletteArray, 0);
        Arrays.fill(paletteMapping, (byte) 0);
        final int plen = Math.min(Math.min(256, colorPalette.length), limit);
        colorCount = plen;
        populationBias = Math.exp(-1.125/colorCount);
        int color, c2;
        double dist;
        int[] Ls = new int[plen], As = new int[plen], Bs = new int[plen];
        for (int i = 0; i < plen; i++) {
            color = Color.rgba8888(colorPalette[i]);
            paletteArray[i] = color;
            color = shrink(color);
            Ls[i] = Math.min(Math.max((int)(OKLAB[0][color] * 255.999), 0), 255);
            As[i] = Math.min(Math.max((int)((OKLAB[1][color] + 0.5) * 255.999), 0), 255);
            Bs[i] = Math.min(Math.max((int)((OKLAB[2][color] + 0.5) * 255.999), 0), 255);
            color = (Ls[i] << 7 & 0x7C00) | (As[i] << 2 & 0x3E0) | (Bs[i] >>> 3);
            paletteMapping[color] = (byte) i;
        }
        for (int L = 0; L < 32; L++) {
            int LL = (L << 3 | L >>> 2);
            for (int A = 0; A < 32; A++) {
                int AA = (A << 3 | A >>> 2);
                for (int B = 0; B < 32; B++) {
                    c2 = L << 10 | A << 5 | B;
                    if (paletteMapping[c2] == 0) {
                        int BB = (B << 3 | B >>> 2);
                        dist = Double.MAX_VALUE;
                        for (int i = 1; i < plen; i++) {
                            if (dist > (dist = Math.min(dist, oklabLABMetric.difference(Ls[i], As[i], Bs[i], LL, AA, BB))))
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
     * second color is different enough (as measured by {@link ColorMetric#difference(int, int)}) by a value of at least 400, it is
     * allowed in the palette, otherwise it is kept out for being too similar to existing colors. This doesn't return a
     * value but instead stores the palette info in this object; a PaletteReducer can be assigned to the
     * {@link PNG8#palette} field or can be used directly to {@link #reduce(Pixmap)} a Pixmap.
     *
     * @param pixmap a Pixmap to analyze, making a palette which can be used by this to {@link #reduce(Pixmap)} or by PNG8
     */
    public void analyze(Pixmap pixmap) {
        analyze(pixmap, 150, 256, oklabCarefulMetric);
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
     * second color is different enough (as measured by {@link ColorMetric#difference(int, int)}) by a value of at least
     * {@code threshold}, it is allowed in the palette, otherwise it is kept out for being too similar to existing
     * colors. The threshold is usually between 250 and 1000, and 400 is a good default. This doesn't return a value but
     * instead stores the palette info in this object; a PaletteReducer can be assigned to the {@link PNG8#palette}
     * field or can be used directly to {@link #reduce(Pixmap)} a Pixmap.
     *
     * @param pixmap    a Pixmap to analyze, making a palette which can be used by this to {@link #reduce(Pixmap)} or by PNG8
     * @param threshold a minimum color difference as produced by {@link ColorMetric#difference(int, int)}; usually between 250 and 1000, 400 is a good default
     */
    public void analyze(Pixmap pixmap, int threshold) {
        analyze(pixmap, threshold, 256, rgbStupidMetric);
    }
    /**
     * Analyzes {@code pixmap} for color count and frequency, building a palette with at most 256 colors if there are
     * too many colors to store in a PNG-8 palette. If there are 256 or less colors, this uses the exact colors
     * (although with at most one transparent color, and no alpha for other colors); if there are more than 256 colors
     * or any colors have 50% or less alpha, it will reserve a palette entry for transparent (even if the image has no
     * transparency). Because calling {@link #reduce(Pixmap)} (or any of PNG8's write methods) will dither colors that
     * aren't exact, and dithering works better when the palette can choose colors that are sufficiently different, this
     * takes a threshold value to determine whether it should permit a less-common color into the palette, and if the
     * second color is different enough (as measured by {@link ColorMetric#difference(int, int)}) by a value of at least
     * {@code threshold}, it is allowed in the palette, otherwise it is kept out for being too similar to existing
     * colors. The threshold is usually between 250 and 1000, and 400 is a good default. This doesn't return a value but
     * instead stores the palette info in this object; a PaletteReducer can be assigned to the {@link PNG8#palette}
     * field or can be used directly to {@link #reduce(Pixmap)} a Pixmap.
     *
     * @param pixmap    a Pixmap to analyze, making a palette which can be used by this to {@link #reduce(Pixmap)} or by PNG8
     * @param threshold a minimum color difference as produced by {@link ColorMetric#difference(int, int)}; usually between 250 and 1000, 400 is a good default
     */
    public void analyze(Pixmap pixmap, int threshold, int limit, ColorMetric metric) {
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
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
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
                    if (metric.difference(color, paletteArray[j]) < threshold)
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
            int rr = (r << 3 | r >>> 2);
            for (int g = 0; g < 32; g++) {
                int gg = (g << 3 | g >>> 2);
                for (int b = 0; b < 32; b++) {
                    c2 = r << 10 | g << 5 | b;
                    if (paletteMapping[c2] == 0) {
                        int bb = (b << 3 | b >>> 2);
                        dist = Double.POSITIVE_INFINITY;
                        for (int i = 1; i < limit; i++) {
                            if (dist > (dist = Math.min(dist, metric.difference(reds[i], greens[i], blues[i], rr, gg, bb))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }
    }

    public void analyzeLAB(Pixmap pixmap, int threshold, int limit) {
        Arrays.fill(paletteArray, 0);
        Arrays.fill(paletteMapping, (byte) 0);
        int color;
        final int width = pixmap.getWidth(), height = pixmap.getHeight();
        IntIntMap counts = new IntIntMap(limit);
        int hasTransparent = 0;
        int[] Ls = new int[limit], As = new int[limit], Bs = new int[limit];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                color = pixmap.getPixel(x, y);
                if ((color & 0x80) != 0) {
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
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
                Ls[i] = Math.min(Math.max((int)(OKLAB[0][color] * 255.999), 0), 255);
                As[i] = Math.min(Math.max((int)((OKLAB[1][color] + 0.5) * 255.999), 0), 255);
                Bs[i] = Math.min(Math.max((int)((OKLAB[2][color] + 0.5) * 255.999), 0), 255);
                color = (Ls[i] << 7 & 0x7C00) | (As[i] << 2 & 0x3E0) | (Bs[i] >>> 3);
                paletteMapping[color] = (byte) i;
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
                    if (oklabCarefulMetric.difference(color, paletteArray[j]) < threshold)
                        continue PER_BEST;
                }
                paletteArray[i] = color;
                color = (color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F);
                Ls[i] = Math.min(Math.max((int)(OKLAB[0][color] * 255.999), 0), 255);
                As[i] = Math.min(Math.max((int)((OKLAB[1][color] + 0.5) * 255.999), 0), 255);
                Bs[i] = Math.min(Math.max((int)((OKLAB[2][color] + 0.5) * 255.999), 0), 255);
                color = (Ls[i] << 7 & 0x7C00) | (As[i] << 2 & 0x3E0) | (Bs[i] >>> 3);
                paletteMapping[color] = (byte) i;

                i++;
            }
            colorCount = i;
            populationBias = Math.exp(-1.375/colorCount);
        }
        int c2;
        double dist;
        for (int L = 0; L < 32; L++) {
            int LL = (L << 3 | L >>> 2);
            for (int A = 0; A < 32; A++) {
                int AA = (A << 3 | A >>> 2);
                for (int B = 0; B < 32; B++) {
                    c2 = L << 10 | A << 5 | B;
                    if (paletteMapping[c2] == 0) {
                        int BB = (B << 3 | B >>> 2);
                        dist = Double.POSITIVE_INFINITY;
                        for (int i = 1; i < limit; i++) {
                            if (dist > (dist = Math.min(dist, oklabLABMetric.difference(Ls[i], As[i], Bs[i], LL, AA, BB))))
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
        this.ditherStrength = ditherStrength;
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
    public static double BURKES_MULTIPLIER = 0.1;

    public Pixmap reduceBurkes0 (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float r4, r2, r1, g4, g2, g1, b4, b2, b1;
        float strength = (float) (BURKES_MULTIPLIER * ditherStrength * (populationBias * populationBias));
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        for (int y = 0; y < h; y++) {
            int ny = y + 1;

            System.arraycopy(nextErrorRed, 0, curErrorRed, 0, lineLen);
            System.arraycopy(nextErrorGreen, 0, curErrorGreen, 0, lineLen);
            System.arraycopy(nextErrorBlue, 0, curErrorBlue, 0, lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);

            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + er + 0.5f), 0), 0xFF);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0), 0xFF);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0), 0xFF);

                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = (color>>>24)-    (used>>>24);
                    gdiff = (color>>>16&255)-(used>>>16&255);
                    bdiff = (color>>>8&255)- (used>>>8&255);
                    r1 = rdiff * strength;
                    g1 = gdiff * strength;
                    b1 = bdiff * strength;
                    r2 = r1 + r1;
                    g2 = g1 + g1;
                    b2 = b1 + b1;
                    r4 = r2 + r2;
                    g4 = g2 + g2;
                    b4 = b2 + b2;
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

    public Pixmap reduceBurkes0LAB (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float r4, r2, r1, g4, g2, g1, b4, b2, b1;
        float strength = (float) (BURKES_MULTIPLIER * ditherStrength * (populationBias * populationBias));
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        for (int y = 0; y < h; y++) {
            int ny = y + 1;

            System.arraycopy(nextErrorRed, 0, curErrorRed, 0, lineLen);
            System.arraycopy(nextErrorGreen, 0, curErrorGreen, 0, lineLen);
            System.arraycopy(nextErrorBlue, 0, curErrorBlue, 0, lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);

            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
                    int shrunk = shrink(color);
                    int L = Math.min(Math.max((int)(OKLAB[0][shrunk] * 256), 0), 255);
                    int A = Math.min(Math.max((int)(OKLAB[1][shrunk] * 256 + 128), 0), 255);
                    int B = Math.min(Math.max((int)(OKLAB[2][shrunk] * 256 + 128), 0), 255);
                    int rr = Math.min(Math.max((int)(L + er + 0.5f), 0), 0xFF);
                    int gg = Math.min(Math.max((int)(A + eg + 0.5f), 0), 0xFF);
                    int bb = Math.min(Math.max((int)(B + eb + 0.5f), 0), 0xFF);

                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    shrunk = shrink(used);
                    int Lu = Math.min(Math.max((int)(OKLAB[0][shrunk] * 256), 0), 255);
                    int Au = Math.min(Math.max((int)(OKLAB[1][shrunk] * 256 + 128), 0), 255);
                    int Bu = Math.min(Math.max((int)(OKLAB[2][shrunk] * 256 + 128), 0), 255);
                    rdiff = L - Lu;
                    gdiff = A - Au;
                    bdiff = B - Bu;
                    r1 = rdiff * strength;
                    g1 = gdiff * strength;
                    b1 = bdiff * strength;
                    r2 = r1 + r1;
                    g2 = g1 + g1;
                    b2 = b1 + b1;
                    r4 = r2 + r2;
                    g4 = g2 + g2;
                    b4 = b2 + b2;
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
//        float strength = (float) (BURKES_MULTIPLIER * ditherStrength * (populationBias * populationBias));
        float strength = 0.14f * (float) Math.tanh(ditherStrength / (populationBias * populationBias));
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        for (int py = 0; py < h; py++) {
            int ny = py + 1;

            System.arraycopy(nextErrorRed, 0, curErrorRed, 0, lineLen);
            System.arraycopy(nextErrorGreen, 0, curErrorGreen, 0, lineLen);
            System.arraycopy(nextErrorBlue, 0, curErrorBlue, 0, lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);

            for (int px = 0; px < lineLen; px++) {
                int color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {
                    float er = curErrorRed[px];
                    float eg = curErrorGreen[px];
                    float eb = curErrorBlue[px];
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + er + 0.5f), 0), 0xFF);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0), 0xFF);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0), 0xFF);
                    byte paletteIndex = paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))];
                    int used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    int rdiff = (color >>> 24) - (used >>> 24);
                    int gdiff = (color >>> 16 & 255) - (used >>> 16 & 255);
                    int bdiff = (color >>> 8 & 255) - (used >>> 8 & 255);
                    r1 = rdiff * strength;
                    g1 = gdiff * strength;
                    b1 = bdiff * strength;
                    r2 = r1 + r1;
                    g2 = g1 + g1;
                    b2 = b1 + b1;
                    r4 = r2 + r2;
                    g4 = g2 + g2;
                    b4 = b2 + b2;
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

    public Pixmap reduceBurkes2 (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float r4, r2, r1, g4, g2, g1, b4, b2, b1;
        final float s = (float) (0.175 * ditherStrength * (populationBias * populationBias * populationBias)),
                strength = s * 0.29f / (0.19f + s);
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        for (int py = 0; py < h; py++) {
            int ny = py + 1;

            System.arraycopy(nextErrorRed, 0, curErrorRed, 0, lineLen);
            System.arraycopy(nextErrorGreen, 0, curErrorGreen, 0, lineLen);
            System.arraycopy(nextErrorBlue, 0, curErrorBlue, 0, lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);

            for (int px = 0; px < lineLen; px++) {
                int color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {
                    float er = curErrorRed[px];
                    float eg = curErrorGreen[px];
                    float eb = curErrorBlue[px];
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + er + 0.5f), 0), 0xFF);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0), 0xFF);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0), 0xFF);
                    byte paletteIndex = paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))];
                    int used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    int rdiff = (color >>> 24) - (used >>> 24);
                    int gdiff = (color >>> 16 & 255) - (used >>> 16 & 255);
                    int bdiff = (color >>> 8 & 255) - (used >>> 8 & 255);
                    r1 = rdiff * strength;
                    g1 = gdiff * strength;
                    b1 = bdiff * strength;
                    r2 = r1 + r1;
                    g2 = g1 + g1;
                    b2 = b1 + b1;
                    r4 = r2 + r2;
                    g4 = g2 + g2;
                    b4 = b2 + b2;
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

    public Pixmap reduceCyanic (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float r1, g1, b1;
//        float strength = (1f / 512f) * (float) Math.tanh(ditherStrength / (populationBias * populationBias));
        float strength = (float) ((2f / 1024f) * ditherStrength * (populationBias * populationBias));

        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        for (int py = 0; py < h; py++) {
            int ny = py + 1;

            System.arraycopy(nextErrorRed, 0, curErrorRed, 0, lineLen);
            System.arraycopy(nextErrorGreen, 0, curErrorGreen, 0, lineLen);
            System.arraycopy(nextErrorBlue, 0, curErrorBlue, 0, lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);

            for (int px = 0; px < lineLen; px++) {
                int color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {
                    float er = curErrorRed[px];
                    float eg = curErrorGreen[px];
                    float eb = curErrorBlue[px];
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + er + 0.5f), 0), 0xFF);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0), 0xFF);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0), 0xFF);
                    byte paletteIndex = paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))];
                    int used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    int rdiff = (color >>> 24) - (used >>> 24);
                    int gdiff = (color >>> 16 & 255) - (used >>> 16 & 255);
                    int bdiff = (color >>> 8 & 255) - (used >>> 8 & 255);
                    int idx = (px & 63) | ((py << 6) & 0xFC0);
                    r1 = (rdiff + (BlueNoise.TILE_TRI_NOISE[1][idx] + 0.5f) * 0x1p-7f) * strength;
                    g1 = (gdiff + (BlueNoise.TILE_TRI_NOISE[2][idx] + 0.5f) * 0x1p-7f) * strength;
                    b1 = (bdiff + (BlueNoise.TILE_TRI_NOISE[3][idx] + 0.5f) * 0x1p-7f) * strength;
                    float modifier;
                    if(px < lineLen - 1)
                    {
                        modifier = 255 & TRI_BLUE_NOISE[(px + 1 & 63) | ((py << 6) & 0xFC0)];
                        curErrorRed[px+1]   += r1 * modifier;
                        curErrorGreen[px+1] += g1 * modifier;
                        curErrorBlue[px+1]  += b1 * modifier;
//                        if(px < lineLen - 2)
//                        {
//                            modifier = TRI_BLUE_NOISE_MULTIPLIERS[(px + 2 & 63) | ((py << 6) & 0xFC0)];
//                            curErrorRed[px+2]   += r1 * modifier;
//                            curErrorGreen[px+2] += g1 * modifier;
//                            curErrorBlue[px+2]  += b1 * modifier;
//                        }
                    }
                    if(ny < h)
                    {
                        if(px > 0)
                        {
                            modifier = 255 & TRI_BLUE_NOISE[(px - 1 & 63) | ((ny << 6) & 0xFC0)];
                            nextErrorRed[px-1]   += r1 * modifier;
                            nextErrorGreen[px-1] += g1 * modifier;
                            nextErrorBlue[px-1]  += b1 * modifier;
//                            if(px > 1)
//                            {
//                                modifier = TRI_BLUE_NOISE_MULTIPLIERS[(px - 2 & 63) | ((ny << 6) & 0xFC0)];
//                                nextErrorRed[px-2]   += r1 * modifier;
//                                nextErrorGreen[px-2] += g1 * modifier;
//                                nextErrorBlue[px-2]  += b1 * modifier;
//                            }
                        }
                        modifier = 255 & TRI_BLUE_NOISE[(px & 63) | ((ny << 6) & 0xFC0)];
                        nextErrorRed[px]   += r1 * modifier;
                        nextErrorGreen[px] += g1 * modifier;
                        nextErrorBlue[px]  += b1 * modifier;
                        if(px < lineLen - 1)
                        {
                            modifier = 255 & TRI_BLUE_NOISE[(px + 1 & 63) | ((ny << 6) & 0xFC0)];
                            nextErrorRed[px+1]   += r1 * modifier;
                            nextErrorGreen[px+1] += g1 * modifier;
                            nextErrorBlue[px+1]  += b1 * modifier;
//                            if(px < lineLen - 2)
//                            {
//                                modifier = TRI_BLUE_NOISE_MULTIPLIERS[(px + 2 & 63) | ((ny << 6) & 0xFC0)];
//                                nextErrorRed[px+2]   += r1 * modifier;
//                                nextErrorGreen[px+2] += g1 * modifier;
//                                nextErrorBlue[px+2]  += b1 * modifier;
//                            }
                        }
                    }
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    public Pixmap reduceOceanic (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        final float[] noise = TRI_BLUE_NOISE_MULTIPLIERS;
        float r4, r2, r1, g4, g2, g1, b4, b2, b1;
        final float s = (float) (0.175 * ditherStrength * (populationBias * populationBias * populationBias)),
                strength = s * 0.29f / (0.19f + s);
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        for (int py = 0; py < h; py++) {
            int ny = py + 1;

            System.arraycopy(nextErrorRed, 0, curErrorRed, 0, lineLen);
            System.arraycopy(nextErrorGreen, 0, curErrorGreen, 0, lineLen);
            System.arraycopy(nextErrorBlue, 0, curErrorBlue, 0, lineLen);

            Arrays.fill(nextErrorRed, 0, lineLen, 0);
            Arrays.fill(nextErrorGreen, 0, lineLen, 0);
            Arrays.fill(nextErrorBlue, 0, lineLen, 0);

            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + er + 0.5f), 0), 0xFF);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0), 0xFF);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0), 0xFF);

                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    rdiff = (color>>>24)-    (used>>>24);
                    gdiff = (color>>>16&255)-(used>>>16&255);
                    bdiff = (color>>>8&255)- (used>>>8&255);
                    r1 = rdiff * strength;
                    g1 = gdiff * strength;
                    b1 = bdiff * strength;
                    r2 = r1 + r1;
                    g2 = g1 + g1;
                    b2 = b1 + b1;
                    r4 = r2 + r2;
                    g4 = g2 + g2;
                    b4 = b2 + b2;
                    float modifier;
                    if(px < lineLen - 1)
                    {
                        modifier = noise[(px + 1 & 63) | ((py << 6) & 0xFC0)];
                        curErrorRed[px+1]   += r4 * modifier;
                        curErrorGreen[px+1] += g4 * modifier;
                        curErrorBlue[px+1]  += b4 * modifier;
                        if(px < lineLen - 2)
                        {
                            modifier = noise[(px + 2 & 63) | ((py << 6) & 0xFC0)];
                            curErrorRed[px+2]   += r2 * modifier;
                            curErrorGreen[px+2] += g2 * modifier;
                            curErrorBlue[px+2]  += b2 * modifier;
                        }
                    }
                    if(ny < h)
                    {
                        if(px > 0)
                        {
                            modifier = noise[(px - 1 & 63) | ((ny << 6) & 0xFC0)];
                            nextErrorRed[px-1]   += r2 * modifier;
                            nextErrorGreen[px-1] += g2 * modifier;
                            nextErrorBlue[px-1]  += b2 * modifier;
                            if(px > 1)
                            {
                                modifier = noise[(px - 2 & 63) | ((ny << 6) & 0xFC0)];
                                nextErrorRed[px-2]   += r1 * modifier;
                                nextErrorGreen[px-2] += g1 * modifier;
                                nextErrorBlue[px-2]  += b1 * modifier;
                            }
                        }
                        modifier = noise[(px & 63) | ((ny << 6) & 0xFC0)];
                        nextErrorRed[px]   += r4 * modifier;
                        nextErrorGreen[px] += g4 * modifier;
                        nextErrorBlue[px]  += b4 * modifier;
                        if(px < lineLen - 1)
                        {
                            modifier = noise[(px + 1 & 63) | ((ny << 6) & 0xFC0)];
                            nextErrorRed[px+1]   += r2 * modifier;
                            nextErrorGreen[px+1] += g2 * modifier;
                            nextErrorBlue[px+1]  += b2 * modifier;
                            if(px < lineLen - 2)
                            {
                                modifier = noise[(px + 2 & 63) | ((ny << 6) & 0xFC0)];
                                nextErrorRed[px+2]   += r1 * modifier;
                                nextErrorGreen[px+2] += g1 * modifier;
                                nextErrorBlue[px+2]  += b1 * modifier;
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
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, rdiff, gdiff, bdiff, state = 0xFEEDBEEF;
        float er, eg, eb;
        byte paletteIndex;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
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
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float ditherStrength = (float)(this.ditherStrength * 0.2), halfDitherStrength = ditherStrength * 0.5f;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);

                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = (color>>>24)-    (used>>>24)    ;
                    gdiff = (color>>>16&255)-(used>>>16&255);
                    bdiff = (color>>>8&255)- (used>>>8&255) ;
//                    rdiff = OtherMath.cbrtShape(0x2.4p-8f * ((color>>>24)-    (used>>>24))    );
//                    gdiff = OtherMath.cbrtShape(0x2.4p-8f * ((color>>>16&255)-(used>>>16&255)));
//                    bdiff = OtherMath.cbrtShape(0x2.4p-8f * ((color>>>8&255)- (used>>>8&255)) );
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

    public static double FS_MULTIPLIER = 0.03;

    /**
     * Some aspects of how this dithers can be changed by adjusting {@link #FS_MULTIPLIER}.
     * @param pixmap
     * @return
     */
    public Pixmap reduceFloydSteinbergAdjustable (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float)(ditherStrength * FS_MULTIPLIER), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = ((color>>>24)-    (used>>>24))    ;
                    gdiff = ((color>>>16&255)-(used>>>16&255));
                    bdiff = ((color>>>8&255)- (used>>>8&255)) ;
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

    public Pixmap reduceFloydSteinberg (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        byte paletteIndex;
        float w1 = (float)(ditherStrength * 4), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + curErrorRed[px]   + 0.5f), 0), 0xFF);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + curErrorGreen[px] + 0.5f), 0), 0xFF);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + curErrorBlue[px]  + 0.5f), 0), 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
//                    rdiff = OtherMath.cbrtShape(0x1.8p-8f * ((color>>>24)-    (used>>>24))    );
//                    gdiff = OtherMath.cbrtShape(0x1.8p-8f * ((color>>>16&255)-(used>>>16&255)));
//                    bdiff = OtherMath.cbrtShape(0x1.8p-8f * ((color>>>8&255)- (used>>>8&255)) );
                    rdiff = (0x1.8p-8f * ((color>>>24)-    (used>>>24))    );
                    gdiff = (0x1.8p-8f * ((color>>>16&255)-(used>>>16&255)));
                    bdiff = (0x1.8p-8f * ((color>>>8&255)- (used>>>8&255)) );
                    rdiff *= 1.25f / (0.25f + Math.abs(rdiff));
                    gdiff *= 1.25f / (0.25f + Math.abs(gdiff));
                    bdiff *= 1.25f / (0.25f + Math.abs(bdiff));
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    adj = (int)((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >> 57) * ditherStrength);
                    adj ^= adj >> 31;
                    //adj = (-(adj >>> 4 & 1) ^ adj) & 7;
                    adj -= 32 * ditherStrength;
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
//                    adj = (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >> 40) * 0x1.Fp-26f) * ditherStrength) + 1f;
////                    color |= (color >>> 5 & 0x07070700) | 0xFE;
//                    int rr = MathUtils.clamp((int) (((color >>> 24)       ) * adj), 0, 0xFF);
//                    int gg = MathUtils.clamp((int) (((color >>> 16) & 0xFF) * adj), 0, 0xFF);
//                    int bb = MathUtils.clamp((int) (((color >>> 8)  & 0xFF) * adj), 0, 0xFF);
                    //0xD1B54A32D192ED03L, 0xABC98388FB8FAC03L, 0x8CB92BA72F3D8DD7L
//                    adj = (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 40) * str);

//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
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

    /**
     * Roberts3 used sign-preserving square root on an adjustment in the -1 to 1 range, with 24 str.
     * Roberts4 used an adjustment in the -0.75 to 0.75 range, with 32 str.
     * @param pixmap
     * @return
     */
    public Pixmap reduceRobertsEdit (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
//        float str = (float) (64 * ditherStrength / Math.log(colorCount * 0.3 + 1.5));
//        float str = (float) (40.0 * ditherStrength / (populationBias * populationBias));
//        float str = (float) (25.0 * ditherStrength / (populationBias));
//        float str = (float) (25.0 * ditherStrength / (populationBias * populationBias * populationBias * populationBias));
        float str = (float) (32 * ditherStrength / (populationBias * populationBias * populationBias * populationBias));
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
//                    adj = (px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >>> 41) * 0x1.8p-23f - 0x1.8p-1f;
//                    adj = adj * str + 0.5f;
//                    adj *= Math.abs(adj); // sign-preserving square
//                    adj = Math.copySign((float) Math.sqrt(Math.abs(adj)), adj); // sign-preserving square root

//                    pos = px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >>> 41;
//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + ((pos ^ 0x555555) * 0x1p-23f - 0.5f) * str + 0.5f), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + ((pos ^ 0x2AAAAA) * 0x1p-23f - 0.5f) * str + 0.5f), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + ((pos           ) * 0x1p-23f - 0.5f) * str + 0.5f), 0), 255);

                    // roberts5, roberts9
//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + ((((px-1) * 0xC13FA9A902A6328FL + (y+2) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + ((((px+3) * 0xC13FA9A902A6328FL + (y-1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + ((((px+2) * 0xC13FA9A902A6328FL + (y+3) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
                    // roberts10
                    // 0xD1B54A32D192ED03L
                    // 0xABC98388FB8FAC03L
                    // 0x8CB92BA72F3D8DD7L
                    // , 0xD1B54A32D192ED03L, 0xABC98388FB8FAC03L, 0x8CB92BA72F3D8DD7L
                    // px * 0xE60E2B722B53AEEBL + y * 0xA6F5777F6F88983FL
                    // px * 0xCEBD76D9EDB6A8EFL + y * 0x9609C71EB7D03F7BL
                    // px * 0xB9C9AA3A51D00B65L + y * 0x86D516E50B04AB1BL
//
//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL - 0xD1B54A32D192ED03L) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL - 0xABC98388FB8FAC03L) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + (((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL - 0x8CB92BA72F3D8DD7L) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
                    // roberts11
                    // 0xD1B54A32D192ED03L
                    // 0xABC98388FB8FAC03L
                    // 0x8CB92BA72F3D8DD7L
                    // , 0xD1B54A32D192ED03L, 0xABC98388FB8FAC03L, 0x8CB92BA72F3D8DD7L
                    // px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL - 0xE60E2B722B53AEEBL
                    // px * 0xD1B54A32D192ED03L + y * 0xABC98388FB8FAC03L - 0xCEBD76D9EDB6A8EFL
                    // px * 0xDB4F0B9175AE2165L + y * 0xBBE0563303A4615FL - 0xB9C9AA3A51D00B65L
////                    int ax = px ^ px >>> 1, ay = y ^ y >>> 1;
//                    long light = (color >>> 8 ^ color >>> 20 ^ px ^ y) * 0x9E3779B97F4A7C15L >> 4;//(px + y ^ px + y >>> 1) * 0x9E3779B97F4A7C15L >>> 4;//((color >>> 24) + (color >>> 16 & 255) + (color >>> 8 & 255)) * 0x9E3779B97F4A7C15L >> 2;
//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + ((((px + 3) * 0xC13FA9A902A6328FL + (y + 2) * 0x91E10DA5C79E7B1DL + light) >>> 41) * 0x1.3p-22f - 0x1.3p0f) * str + 0.5f), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + ((((px + 2) * 0xC13FA9A902A6328FL + (y + 1) * 0x91E10DA5C79E7B1DL + light) >>> 41) * 0x1.3p-22f - 0x1.3p0f) * str + 0.5f), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + ((((px + 1) * 0xC13FA9A902A6328FL + (y + 3) * 0x91E10DA5C79E7B1DL + light) >>> 41) * 0x1.3p-22f - 0x1.3p0f) * str + 0.5f), 0), 255);
                    //roberts6
//                    float adj = (px * 0.06711056f + y * 0.00583715f);
//                    adj -= (int) adj;
//                    adj *= 52.9829189f;
//                    adj -= (int) adj;
//                    adj -= 0.5f;
//                    adj *= adj * adj * 4f;
//                    adj += 0.5f;
//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + ((((px-2) * 0xC13FA9A902A6328FL + (y+2) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.0p-23f - adj) * str + 0.5f), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + ((((px+3) * 0xC13FA9A902A6328FL + (y+1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.0p-23f - adj) * str + 0.5f), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + ((((px+1) * 0xC13FA9A902A6328FL + (y-3) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.0p-23f - adj) * str + 0.5f), 0), 255);
                    //roberts7 (used in anim8-gdx 0.3.10)
//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + ((((px-2) * 0xC13FA9A902A6328FL + (y+2) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + ((((px+3) * 0xC13FA9A902A6328FL + (y+1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + ((((px+1) * 0xC13FA9A902A6328FL + (y-3) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);

//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + adj), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + adj), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + adj), 0), 255);

//                    adj = (pos * 0x1p-23f - 0.5f) * str;
//                    int rr = ((color >>> 24)       );//MathUtils.clamp((int) (rr * (1f + adj)), 0, 0xFF);
//                    int gg = ((color >>> 16) & 0xFF);//MathUtils.clamp((int) (gg * (1f + adj)), 0, 0xFF);
//                    int bb = ((color >>> 8)  & 0xFF);//MathUtils.clamp((int) (bb * (1f + adj)), 0, 0xFF);
//                    int used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                            | ((gg << 2) & 0x3E0)
//                            | ((bb >>> 3))] & 0xFF];
//                    rr = MathUtils.clamp((int) (rr + (adj * (((used >>> 24) - rr)))), 0, 0xFF);
//                    gg = MathUtils.clamp((int) (gg + (adj * (((used >>> 16 & 0xFF) - gg)))), 0, 0xFF);
//                    bb = MathUtils.clamp((int) (bb + (adj * (((used >>> 8 & 0xFF) - bb)))), 0, 0xFF);

                    //roberts8
//                    float ll = ((((px-0) * 0xC13FA9A902A6328FL + (y-2) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.1p-22f - 0x1.1p0f);
//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + ((((px-2) * 0xC13FA9A902A6328FL + (y+2) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.6p-22f - 0x1.6p0f + ll) * str + 0.5f), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + ((((px+3) * 0xC13FA9A902A6328FL + (y+1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.6p-22f - 0x1.6p0f + ll) * str + 0.5f), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + ((((px+1) * 0xC13FA9A902A6328FL + (y-3) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.6p-22f - 0x1.6p0f + ll) * str + 0.5f), 0), 255);
                    //roberts13 (used in anim8-gdx 0.3.13)
//                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + ((((px-1) * 0xC13FA9A902A6328FL + (y+1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
//                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + ((((px+3) * 0xC13FA9A902A6328FL + (y-1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);
//                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + ((((px-4) * 0xC13FA9A902A6328FL + (y+2) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-22f - 0x1.4p0f) * str + 0.5f), 0), 255);

                    //roberts14; uses R2 to get an angle, then gives RGB different rotations of that angle to cos().
                    //3D Roberts 0xD1B54A32D192ED03L, 0xABC98388FB8FAC03L, 0x8CB92BA72F3D8DD7L
                    //3D Roberts 0.8191725133961645f, 0.6710436067037893f, 0.5497004779019703f
                    //6D Roberts 0xE60E2B722B53AEEBL, 0xCEBD76D9EDB6A8EFL, 0xB9C9AA3A51D00B65L, 0xA6F5777F6F88983FL, 0x9609C71EB7D03F7BL, 0x86D516E50B04AB1BL
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    final float theta = ((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >>> 41) * 0x1.921fb6p-21f); //0x1.921fb6p-21f is 0x1p-23f * MathUtils.PI2
                    rr = Math.min(Math.max((int)(rr + MathUtils.cos(theta        ) * str + 0.5f), 0), 255);
                    gg = Math.min(Math.max((int)(gg + MathUtils.cos(theta + 1.04f) * str + 0.5f), 0), 255);
                    bb = Math.min(Math.max((int)(bb + MathUtils.cos(theta + 2.09f) * str + 0.5f), 0), 255);

                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    public Pixmap reduceRobertsLAB (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
//        float str = (float) (64 * ditherStrength / Math.log(colorCount * 0.3 + 1.5));
        float str = (float) (0.1 * ditherStrength / (populationBias * populationBias));
//        float str = (float) (25.0 * ditherStrength / (populationBias));
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    int shrunk = ((rr << 7) & 0x7C00) | ((gg << 2) & 0x3E0) | ((bb >>> 3));
                    double L = OKLAB[0][shrunk];
                    double A = OKLAB[1][shrunk];
                    double B = OKLAB[2][shrunk];
//                    long rob = ((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >>> 41);
//                    L = Math.min(Math.max((L + (rob * 0x1.1p-22f - 0x1.1p0f) * str), 0), 1f);
//                    A = Math.min(Math.max((A + (rob * 0x0.9p-22f - 0x0.9p0f) * str), -1f), 1f);
//                    B = Math.min(Math.max((B + (rob * 0x0.9p-22f - 0x0.9p0f) * str), -1f), 1f);
//                    double LL = ((((px + 3) * 0xC13FA9A902A6328FL + (y + 2) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.1p-23);
                    L = Math.min(Math.max((L + ((((px + 3) * 0xC13FA9A902A6328FL + (y + 2) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.0p-22 - 0x1.0p0) * str), 0), 1f);
                    A = Math.min(Math.max((A + ((((px + 2) * 0xC13FA9A902A6328FL + (y + 1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x0.Ap-22 - 0x0.Ap0) * str), -1f), 1f);
                    B = Math.min(Math.max((B + ((((px + 1) * 0xC13FA9A902A6328FL + (y + 3) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x0.Ap-22 - 0x0.Ap0) * str), -1f), 1f);
                    L = reverseLight(L);
                    double l = (L + 0.3963377774 * A + 0.2158037573 * B);
                    double m = (L - 0.1055613458 * A - 0.0638541728 * B);
                    double s = (L - 0.0894841775 * A - 1.2914855480 * B);
                    l *= l * l;
                    m *= m * m;
                    s *= s * s;
                    rr = (int)(Math.sqrt(Math.min(Math.max(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s, 0.0), 1.0)) * 255.9999);
                    gg = (int)(Math.sqrt(Math.min(Math.max(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s, 0.0), 1.0)) * 255.9999);
                    bb = (int)(Math.sqrt(Math.min(Math.max(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s, 0.0), 1.0)) * 255.9999);

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
     * An intentionally low-fidelity dither, meant for pixel art.
     * @param pixmap
     * @return
     */
    public Pixmap reduceLoaf(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        final int strength = (int) (11 * ditherStrength / (populationBias * populationBias) + 0.5);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int adj = ((px & 1) + (y & 1) - 1) * strength * (2 + (((px ^ y) & 2) - 1));
                    int rr = Math.min(Math.max(((color >>> 24)       ) + adj, 0), 255);
                    int gg = Math.min(Math.max(((color >>> 16) & 0xFF) + adj, 0), 255);
                    int bb = Math.min(Math.max(((color >>> 8)  & 0xFF) + adj, 0), 255);
                    int rgb555 = ((rr << 7) & 0x7C00) | ((gg << 2) & 0x3E0) | ((bb >>> 3));
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[rgb555] & 0xFF]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }


    /**
     * An intentionally low-fidelity dither, meant for pixel art.
     * @param pixmap
     * @return
     */
    public Pixmap reduceLoaf2(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        final double strength = ditherStrength * populationBias;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int adj = (int)((((px + y & 1) << 5) - 16) * strength);
                    int rr = Math.min(Math.max(((color >>> 24)       ) + adj, 0), 255);
                    int gg = Math.min(Math.max(((color >>> 16) & 0xFF) + adj, 0), 255);
                    int bb = Math.min(Math.max(((color >>> 8)  & 0xFF) + adj, 0), 255);
                    int rgb555 = ((rr << 7) & 0x7C00) | ((gg << 2) & 0x3E0) | ((bb >>> 3));
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[rgb555] & 0xFF]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }
    /**
     * An intentionally low-fidelity dither, meant for pixel art.
     * @param pixmap
     * @return
     */
    public Pixmap reduceLoaf3(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        final int strength = (int) (8 * ditherStrength / (populationBias * populationBias) + 0.5);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int adj = ((px + y & 1) - (px & y & 1)) * (strength + (((px + y ^ px + y >>> 1) & 3)) * (1 + ((px * 5 ^ y * 3) >>> 1 & 3)));
                    int rr = Math.min(Math.max(((color >>> 24)       ) + adj, 0), 255);
                    int gg = Math.min(Math.max(((color >>> 16) & 0xFF) + adj, 0), 255);
                    int bb = Math.min(Math.max(((color >>> 8)  & 0xFF) + adj, 0), 255);
                    int rgb555 = ((rr << 7) & 0x7C00) | ((gg << 2) & 0x3E0) | ((bb >>> 3));
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[rgb555] & 0xFF]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    /**
     * An intentionally low-fidelity dither, meant for pixel art. Like Loaf, but with diagonal components.
     * @param pixmap
     * @return
     */
    public Pixmap reduceLeaf(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        final double strength = (7.5f * ditherStrength / (populationBias * populationBias) + 0.5f);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    float pos = (y * 6.711056f + px * 0.583715f);
                    pos -= (int) pos;
                    pos *= 52.9829189f;
                    pos -= (int) pos;
                    int adj = (int)(((px & y & 1) - (px + y << 1 & 2) - (px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >>> 41) * 0x1p-23 + pos)
                                    * (strength + (((px + y ^ px + y >>> 1) & 3))));
                    int rr = Math.min(Math.max(((color >>> 24)       ) + adj, 0), 255);
                    int gg = Math.min(Math.max(((color >>> 16) & 0xFF) + adj, 0), 255);
                    int bb = Math.min(Math.max(((color >>> 8)  & 0xFF) + adj, 0), 255);
                    int rgb555 = ((rr << 7) & 0x7C00) | ((gg << 2) & 0x3E0) | ((bb >>> 3));
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[rgb555] & 0xFF]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }
//                    int adj = (int)(((px + y & 1) - (px & y & 1) - pos * 0.5f)
//                                    * (strength + (((px + y ^ px + y >>> 1) & 3))));
//                    int adj = (int)(((px + y & 1) + (px & y & 1) - (px * 0xC13FA9A902A6328FL - y * 0x91E10DA5C79E7B1DL >>> 41) * 0x1.8p-23)
//                                    * (strength + (((px + y ^ px + y >>> 1) & 3))));


//                    int adj = (int)((px + y & 1) << 3);
// ^ (RAW_BLUE_NOISE[(px & 63) | (y & 63) << 6] & 255) >>> 4

    /**
     * Uses Interleaved Gradient Noise, by Jorge Jimenez.
     * @param pixmap
     * @return
     */
    public Pixmap reduceIGN(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        float pos;
        final float strength = (float) (40 * ditherStrength / (populationBias * populationBias * populationBias));
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    pos = (px * 0.06711056f + y * 0.00583715f);
                    pos -= (int) pos;
                    pos *= 52.9829189f;
                    pos -= (int) pos;
                    pos = (pos - 0.5f) * strength;
//                    pos = OtherMath.cbrt(pos) * strength + 0.5f;
//                    pos = pos * pos * pos * strength + 0.5f;
//                    pos = (((pos > 0.5f) ? 1f - (float)Math.sqrt(2f - pos - pos) : (float)Math.sqrt(pos + pos) - 1f)) * strength + 0.5f;
//                    pos = (pos-0.5f) * strength + 0.5f;

//                    pos = pos * pos * pos * strength + ((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL >>> 41) * 0x1p-20f - 3f);

                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + pos), 0), 255);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + pos), 0), 255);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + pos), 0), 255);
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    public Pixmap reduceIgneous(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float) (6.0 * ditherStrength * populationBias * populationBias), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f,
                strength = (float) (60.0 * ditherStrength / (populationBias * populationBias)),
                adj, dmul = (float) (0x4p-10 / populationBias);

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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    adj = (px * 0.06711056f + y * 0.00583715f);
                    adj -= (int) adj;
                    adj *= 52.9829189f;
                    adj -= (int) adj;
                    adj -= 0.5f;
                    adj *= strength;

                    er = adj + (curErrorRed[px]);
                    eg = adj + (curErrorGreen[px]);
                    eb = adj + (curErrorBlue[px]);

                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = (dmul * ((color>>>24)-    (used>>>24))    );
                    gdiff = (dmul * ((color>>>16&255)-(used>>>16&255)));
                    bdiff = (dmul * ((color>>>8&255)- (used>>>8&255)) );
//                    rdiff = Math.min(Math.max(dmul * ((color>>>24)-    (used>>>24))    , -0.25f), 0.25f);
//                    gdiff = Math.min(Math.max(dmul * ((color>>>16&255)-(used>>>16&255)), -0.25f), 0.25f);
//                    bdiff = Math.min(Math.max(dmul * ((color>>>8&255)- (used>>>8&255)) , -0.25f), 0.25f);
//                    rdiff = (0x3p-10f * ((color>>>24)-    (used>>>24))    );
//                    gdiff = (0x3p-10f * ((color>>>16&255)-(used>>>16&255)));
//                    bdiff = (0x3p-10f * ((color>>>8&255)- (used>>>8&255)) );

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


    /**
     * Forms big, smooth blobs at regular intervals.
     * @param pixmap
     * @return
     */
    public Pixmap reduceBlob(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        float pos;
        final float strength = (float) (10 * ditherStrength / (populationBias * populationBias * populationBias));
        for (int y = 0; y < h; y++) {
//            sy = MathUtils.sinDeg(y * 90f);
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    pos = (MathUtils.sinDeg((px + y + y) * 40f) + MathUtils.cosDeg((px + px - y) * 40f)) * strength;
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + pos), 0), 255);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + pos), 0), 255);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + pos), 0), 255);
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    public static final byte[] RAW_BLUE_NOISE = "ÁwK1¶\025à\007ú¾íNY\030çzÎúdÓi ­rì¨ýÝI£g;~O\023×\006vE1`»Ü\004)±7\fº%LÓD\0377ÜE*\fÿí\177£RÏA2\r(Å\0026\023¯?*Â;ÌE!Â\022,è\006ºá6h\"ó¢Én\"<sZÅAt×\022\002x,aèkZõl±×\033dÅ&k°Ö÷nCÚ]%é\177ø\022S\001Øl´uÉ\036þ«À>Zß\000O®ñ\021Õæe÷¨ê^Â±\030þ®\021¹?èUªE6è\023_|¼¢!­t½P\005ÙG¥¸u.\030ò>Tÿ3nXCvíp*³\033ìÑyC¼/\031P1;òSÝÈ2KÒ\"È3r Óø·V\000\034ä4\bVê\020õgÇ\0331êÞ`¯ÅeãÓ­ò×\rÈ\034KÏ\013h5\tÃ\037T\002~Í´ kÐq@~ïc\003x\023ó»\005OxÛÃJÎeIÒ7´p]\013#J\006 $`F¿¡*³`åôS½F¤bùÝl¦Há\rû¡æ\013%º\005\035à©G[âc\020§=,mñµ=þÃ-\034å\ròM¿?Ïöq9¹\017xæ\032eù2¦\026:~Ùå-:¶ð'Ww¿KcªÕ\\¢OÀ-Ð³:¥+Éî!\\Ñ\f$qß}¦WB*«Õýz¨\025ìPÌ\0027|ÞRq\001Ä¬%ÿr¯\030Ò\016_Ç3Ö=\0260úè8\roøa\007Ù}ýAs¼áû¬Tè\024²_\007øÊxe\036µ1VØ(ª@ÚUÊ\007»Óaî\021WÆM{B\033s\005®óÉyiÍ¯\032ê%M\030±Nh\0267{Â¢K9Ö¹\026à:\tjæ¿~]÷h.µ\024J\"óC-\032KkÏ=ò\003é«Ûö»b\"ßU\b·B#ÞTpÀhèÔ2\tÊFÙ\003+Íñ lGa\000ÁQìË¢\033D\004\035Ãð¤pé®\\ Ýµ2º¡b)¿6kNëFl§\035Mÿ|1È?úª\017GZ÷£ì¶\037p[\017ä1¤&s-`û7±Òt\rYÑ9z\016Éêvü\tã\034pÖJ\007£*\017Å6×íÂ\023óµ\026.]Ì$q¹\034x-bVãø¼«wÃî³\020ÙH¸vÞP\022é3MÞ>\000Á*úeA\"ZD®û\037ÉYÔ\177µ\002t.f«\\JÖuÝ\003¡òß>Ô\f¨\0223B\002RÐ?[÷©\013#Æo[ü¹\"¬d\030á¸Q\0344ÂªÕ}Ç\017ç`xñ2¬ü`è\026XÑ9å\tïR´e4U­\003l¿<NÑhÝ#ù}\030Æm;ÐWô«)¢Í}ñoG¦Ó\003hð'V<µà\024D!Ë=÷º\037jÃ9\036AÁw\020ÈLúé\177\036´_\r¨ÜO,æ|\016?ÛjE\0076×S\nôxâT\022I6»\003Ò\031Oq¿Ûn Mà\020zFþ)§~â\013ùÖ*ë Ü7(Æ¡õ.ê¾i7·\004ë\036fF»\000Àï\027^µ&Ê1?¾,´ùÜn¦v+Å¢\0008õ±(Ã^¢Ø³VÎ¹Ni¨]z¶d\030F\005WuËL)åt¬ÂüØ4b\035T/¯æÄÿvê®b\036\brÍ\033éFöa\016ée\031W\nv\0020eô\024\001-Ë\031G»õ¢\nÐ«r×:\025\000Ñ\\B\fU\024±Ñ ygM\033\023Øí[©:dP\n±>Õ'¸Ðå;ªïÊ\034çpCÚaït2ÿn>RäZð%¸â£°y\034ò¢1Îz&îqãGû\017Ø,§BYý\177LBà\000½$Íjá»TªsI/f\026R@½4Å£\020²ãØÆ\027-ýÁ5\fHh÷V?ÄláH§[8\n·È;óÏlãÂ5Ï¹&\024{ò0\025}\005ÇòþÀØw\016\\­Py\036=X\t$¯Ak^Æ#\016¼Û(\022ù¹\005Á÷f!Uqº\t1³ ¡\006pöÇXÚ=] ù\"8Þb\035|L&µâúÒ$\006öÒ¿J}9dívÛ\022°ç\000ÔrìcI­X;n\032ÚO.¬ß¤\031_÷R^Ü/²E\013s­\003ÆêL\020C¯ê\tY£-gßl/`ýç§ÌW\004¹IÍ\037}Q/<¥\004~Õë$wÌ\023ë|\004JíyØA\025ë¨gSé\032&m³Òv½Ö3Ípð9ÄA·ì«'\024ö\032(¦ù2¾\032ß_¶Ì\0310Æ^³\000>ZÑ)Á8Ë&­iÇ:\036Ìü5¾ÔAW/¤[\002m\025¨@\003y\031M\017UÉFsÕf3ÁàQp[ïC«\007õPåüi\rIåg¼õC°ýNå\013ÿu¶\râw£cPàö\t\031äò%O_¼!Ú¯èÏ\177\034\007¹L°x;\bÅÚ\017iÏx$s8»B¢ò,\027¨4\034k`\022t£\\¾.ÖóN?)´\016{Ài>±Åúæ\016Hkaþ4Ýøá;ï\t\"Îèb®%7KÂ\021ÓY¬\037Ý|ÁPÕs\fãÙ¹ ñGV#Â]\001ð 4¬FÌ\177Ü/uÓô(<½¤%²_-Ziý\027G{¹æý+°î\006nÎ\004büÈM+ó?Ð4Ý{\027¥l®Þ\024ÏmIÇÚ]þ\035\bg;¥R¶ÇX\023gÅApÌ\023¼ÚA¥·1ò\003V\035`ÜoDeâN÷1W±à&E\177¬\007oX\003²çÍû2~;§çr\023é+Qºï\"ü\026|\006ãNð\r¡Oÿ|)ÈTuÜÒÇ§\0265'Å\017\033<£\022·í\\Á\030§Æi=c\bDí!W»*\004=¶¡Òc¨\021ÊCÝ2ªÓvýÙë\035­äù\036Kk\021<tPôÎ½\001®y@¹évôk2Ò\0369âJú-\021½&M·Õù\fgRöyHâvW²j\\ëG\036¸/©Tk1Hc9ê\006Á¬'ì¶/\f\177\\Úñ¤gÓÆHþfÔzëUóÚ\034È_r Ë­×\0360Ä\005>(ô\004¡Àqb<\024½Ò\boµ\025Ò\\ãbþAä L9\024Qÿ,[\bÝr\017´V$¯E Îw©k\020æ-M7\030ãnXì\027Ö½5\032Î+\017ôßË']@óÄ%¨}0DÌ\032m×¬f\bÌo$ß¯\035½«N5Çö\fÞ`3\0048Hþ°ÀyðCÿ¸jª[oOzÛ=S\006}çù°x6ßY\001@ºõ\nJ¼)XÄí´úÀCc7æzñ'çt@Àm\026¶\"áÃ¢#Ú\006(Å\0166IÎü\"\016è§ûi±Â \020Ú ¤MíhÜ\037uïÞ\0027\031w0^\rÊ\005T\023Ñ^¦.üïQù]}îVeµ\\¥~Ý+ä²Ç@_¸Jí\"6FnQcÀ\fs\031Ê,¡T±3[¦z\020MÔS¬ê~öiÖ?ÃlE\005\034ÖYÊuB¬Ô+\017<Í\026õKÑ\037øTwh\006(\024ÊuÓµ*þÔ²Iq\004ÕÅ\025?Íõ£#à\0265¸'¨\033ú°ßº~K®9'£\t\032¸kã¨v2æ=d¼\007:\032ñ1Ód©Ý\032\0024ÉâDïf8û¾\022ê<gû%¶ç_¿r\001FÏK\n]5$òä\020j½êÛb5È\003R¼\np¯ë\024Ë¦ÙQ¾\177ãøVNò¥_\023~«&á^O©áSl,\f=´f YÜîoÉçwV\reÈ\002OzðF\"ùBÛ\034ÄWHmaÿ®F :\b-¿x >º\007Y\027¡xÏ.\035~ÁD\002ª×íÅûv¼,E\003À£<*X\033qÏ,²\026^rÐ+b¢\001$Ø/ûå(\016!oÆ²jèÜøÏ.GÙÆö?\b·ó\rÑu\0338øR|1\017®9\025¦Ù\037MÒý¬Ùõ¶A\022üT×ç¬\r³|ð;xªÃCsÐì\027×D\f®Shêu±hÞI^5ëYÈi\025§ÕåcÑSeµ3ðmEz3â§m¿(9ÄQäHÎ^·\013\033Yº0MZþ%aÄs\033\000¥Q7\032T)Én­ú#·¢á»%@L\006$êù\017t\031â\013Â `LÉ;\nyîd\002\030.¿\020÷Lï7Üùfâ|ï6\021ã´È(\013¼å\000î\025ÙfA\021/K\001Zôm¶Ä\177>Ê¨Y¸d#ñ\005\037ë[øI\034¤×g«s ÙjÊ{\025©\b@Î¸¦L+B_ñcÐ©{:»,RÄ\004wÐîo¯Ë\035Û¤G0^Þ)\0018Îp·~.Û²Ì2ºtú@æ£=+\004³I'Às4\035\002Òõ|Ø2r!H\\\007rçª\\7\ny.ÿ\025ä¯\t¾PéI­0TÕ¤\024f\004àY;ÇS\007(ZýÀRâaÔé¡÷oXh\006»¢\020NßøÃ\021Óã¢\034Jõ'¾\033ÝÀëU:qÑg ÷{Õi\021üà\030CôÂO!n\016$®îÎ´z\022§p ôRe\fDÞÂ«8\"WÍ²> j4°ý=Í\177\rØlRúG\026a¸NïD\030¥Ä\\s\n:©ëEØ\177\0274fÞEìÌ9º\021,È³%ë\023Häuÿ\032í&Yh·5>³gªÓø\016Æ)¦6Í¶1ò@y»7ËaäþÓx·öa¾Jö\0350]\013C}«Û8wOýÇmó.¼]}ØGÂð-\000`¤îÎ\0060æ>#|ß[ÿu\013WmÛ&\004ì\035²X)\027É0O\001åp\tÄÔ°lÞûX\004\031òÑ`Ü\027­aB\nË§\001\035\020«ßÈo\022%{¾m J°\031¾ã!\000°ÐU¤h0H¿d\t¤Ñ)­9wS\001KÄ&èg¾¥3\177¸Q(ÖåS6¶kæuVC\036JàU\032Ê\005ì<×cFî Kd\024>Üó}\021áAí8v³Z@\024dè$½\027/u²Ï=o\013!@\002¡Ãyú\024NÏ )ø¿[­þBñÚ^2Ãj-\025³~ÇætÁ\013Ð¥\003¯nÝ\037ðÊûÙ¡óc¥î\017P\036ù×Vèdð0j\016\"FfÛ,õ<\005²×y\013.Ôq´*¦v\017õN©ûÔ'\n6øª*E\034lQ#ÆU\022¼fR¸3NË?Ø6`¹G­É\025Òá±ïº©wÅ\\\027ëH£ê5b\023Oü¶!Ìm=^ºPÖ`þ´5õÕ,üI\b}'j\003\023z¯\bl\000Þ{+\005K·Z<qË4]\036\fáo½4dËj\033»\006Èå:ÕUáz\b¾àñq\030yÞÃèbCz ç«;ÁäªÕ äEÉ(§ñÂ8k$ôþ\027QÔûB#ý©:öP}#nÁfB3ìW\037J¥/\005É9ïY'\n¸\031_Î!\026G÷8^¾T÷t\021Zä£\n|Å+¨ç\007|¡V°ðLÝ\021'á°AÜô®\f\033¥\001±\022Êÿ´ÚgS!¬\023Ih©Û\0013MtôÙ[rÇén¤0\031µLÍ?\035rÖW@ÚdJ*Æ6kÐ~UÁs]\rÍ+ZEé+bÙq¡e4xé¼Ô\002 Ì:nîÄ²\013.\007µ)\006Øeé\003³ü½3ì°\020tÀó³\021å\001\032¹/Ô\000\037í¹tÿÐ{KÆö%?æÃ&D\016r>{ø\036X&âj½<¦Q\027Dñ\177Ã:Û-cHj\037Sú#ÖD[a<\bóD¤m8\030¨5»\026·Ð\tQ\031]ü¦ñaä/¿PÓ\177B\030LûÑ{ßÌ¯Z\020#pS©î\027Ï§âË_;oª!wÊß©i´*L¿àU\007ðPà\n_2X{ô±ÖË*µU\025®ç\016÷¥Écì\037Xýi5ã¦öº\tÇ{\005".getBytes(StandardCharsets.ISO_8859_1);

    public static final byte[] TRI_BLUE_NOISE = "íøN\fÒ&^\001C0ûÍ\002*\033\f\000àÊ\005-ò;ÒKõÛ ´8\024R(\035ZÐ\0211éô\"7-\030Ûo\0340Â\r\004²ð\017È>óÚ,\031\020#À²:ðÅ\bô\016Õ\034\t<ëPÈ¤óTBûæ\013á\023§éBÄíøÍ\006Àí<$øVÝ\031eáñ\006·ÿÌßSìÐ\031üL!.\006\024éú¢Í4Ø\004\024¤G1äS¯àhÛ\017ú9,®\026Ö!g¹\0034$ÿ\017\b`Ü°3à\026\002ØÅÿ\r²\003Î»Oé*\nú#¢@+©ßºÑþ_Ä;L\001ôU\037èûÚ\031¼ù*Æ\023ö$¹å×\007ì¾6\rÍø\031íÈYÔ\030#J\017ûyµ\033ì+Hú\020\0369Õ¸\0255ôÜ\007jø\026ïä®\037Õ\bã\016Éd,\002Î>ê \000î4\005D\035o\022%ã\001ðO/Ø?­úä-òÿèÕCó*å6PÒ\025<æ'ÅõXïä¾[\021Çé4\tTD'\fñ´\030ý=á­õ#\021\006IÏªZÓÂþ·Î÷Z\035°è¿\nM\037\022<É©0Â\037\tÍ\020\003õ½\tðÙ©\001\f\033GýÒ\037\001%ØÊ£\000Þ3w(½î\bÕK·ïqÜ\024.â\f\027éò1\003<ÆÜD\023þ(ßó\005»Ûg\r\003W±ê?Ü¦&àý2n\027Cà.Ê\004\r®/ñAÀ\017õ7\027øÅèÒF.\022\0329ä\0133Áü´ó=(LÞ¸\027\013+ô\007Îl\030Ñ&ìù\033áõ\025þcÄ\032;[É\037¶Ïêû`Ú\":êÝ\027Oä\003\035êº@\020\004 Ü\001ùÄ\000Ðö\032çU \002Ùú\017!íQçýÕ¨8î\0163B\000O 5Í)×#øð\006é\r÷\005+¥\023Àô¡vùËý¯e+ÐWÚNõÌbê'Y\037¥N&Ö\nÊ0¼bÈ\bÓ6½\\#äÇ³üçÃ\024\t·FîQ\0074\017³Ò\024/@ãU\034ð=\001æ)\021»5#\013Ý ð\bü,\026¬\035:µ×ñ\017ß\003?þîF\026æ¦Aø&\020ñ\032\fF÷ XÜ\034ôÕä \020ÀàÌåGVÛ\000óÃ×\013ºÎP\nÓE\004îÕ;÷F\022$Éåî\013á÷\024\006E¯6ë¿\021i®Üö\0052\033Ûs\002Íà\003Ö/\007)z;\004ý§÷>\000\033(ú©%\031®H&Þ1ù\037âÅ\034\025éÅ\033\000°8j½3þRÆæ.üÉ\026ó-ã\035\f'ÐðÿÀå­1@ûS¹\023ðÎ\r«ìÄ1^\025,ï¾\fÊìh\020üé\003\024µ]þôT,\007¶^Óàö\031\004Ô¸\n&\033Õ\\\"\007Îú7Ã\\µ:\023N\n#í\026Â(ê:â\001Fú\"Þ\013ÑéØ\tpÞD\0048àÐ4§ZóAí*\rÚ¯?âû0íA\rM\"DëÑ}ô\fè²HÚR\000ë áÌõGÔ\007Ýô\036È\\´4Ô\026O°ó\034K²ö.\026\037ö½\b!ÇÖ\032\007Î5ç\"\001Í\020\037©\002)ÍÝòÂ\027=\001Þ¾0\000£\020ñ\031\t×C\016ü-\031¶ÿªb\020¢ü\t\032çö¾\005'AÆ\002\"ÏýçÕP\000-îE\022å8Àü¡\024e½ðIÙRÀçü\0210ãú­\020%Lø=á)Èå,ö­SîØgç2\034;Ð.IÙ%\017bâþ\022ä5\rÂ<ò\016¬^Úù\002pÞ\036OöÖ\013\032÷\b\0267b¤\007VØ4ïÍ\024í\035\004Dm½5Ï\006'¿\004\020Éðùä\002íÀóC.Êï:Ùø`î¥W$¶\033Èã\030%»\fô'³\006ìÇ:%æ/Éï#Öõ\034)¶\005\036æd\bÂ¸Õü¨\017þ\035ä\026¢=Þ\"P\r¸\030k\006ß\000\036§\030-\007»Ö\025\001Û\bIë\003;ÑR1êÓ?ã-Hú´\002xà®\f»=Ë\rýJÓ8þÛ.X\"ó\026Þí]ÇøKñü\\\bÃØ)8Î\023¸ÕN\néÌI\035)Bôä3ù*\021£ðþÆ\027«\017ÿ\030Þ\022Ò8\035þEä\002ìláÄ\021ò¯\031÷\rçÌ>&9Ú/\013®Ò*éB\037ô¨æû\">øºsó¸áüÈcÎ¨×Ái\nÝ GùaÁÐ\t\\'ñ\nöY\022/\031ö\"¡(@ÉH3\006TÃ\001\b¶ ç\0247º\003\022àþG\t3îÅå\024%\001\0164ì\006\023 \fÿA#ö4ç\004Øï#3óè¹ÆPÜ+ÐÅòÛ«5\004]ê\nâ\001ïÙ±ùä\022IôÎn\001Û\033öÏaÁ\033Y*\005Û7Ó¿\032K°,ç¼Rìá\031±M\020(9æ¦Nü\032C\000\024ê¸\033\005%C\nçÍÙû\026¾!|\024)\034ðÕ-\031ýCïÈW:%ì\020ÚöÒ\r®DúYÝ÷Ñ<Ûó/\024\005ÈÿÔò¹Ì\025\013Ü²\006Ö1ø>\râûO¿\026ý>¹/QÓ6¦ýÍE»d§ëß%À\r0ûã\007°/\002Kã\031ÿñ\036è)\nk\002û\034²Ò:W\0360ú{\002@\037É*ã¡\"¿g6·ëÔóT\035\017ñ¸÷\007ê\017â2\n\000É\0176Q\004å!¶Ó\030ò=É#ºh0ÃS\006Êî\027$\020ÄI\tfúï\nÜÃ\bâöë_\021GðÎ\003Ùñ \0200ÝÅá\005#HÝÁWôÚ!<ò¹Ö÷\025¢\tGuý¤è\024ûìÏß\021¦@3½âëª7Ùæ ,¶é\027C\"×5¿þ¬÷\tU\026þÊ_¸&\b,kèÊ\022+\034>\002\030úPä\006\034,ÌY7ÚëÁ)Þ\fB\b:ø\033\001ÖõþCÐ*ñ\027\000ÌD\017 Yî±\016)\027à:Ô\034è3*\013ã;\002éGúÔ7\000òÖ¯Ë(íÂ¬aüíÿ$ô\022\003\036Ð\\ó-Ø$¬Fæ!³Z\r\005Uø£'¾áõ1üÑ\000NòÌ\003#O·ÅÞøE¼\031öÏ\025¬î\027ºVü\ffå7\021Ó0\026Ü\017?á¨ÇAµø6¯\004ÈO\017ðÀq\t,Ü\036àÉ\021L\005bÖ\033\006Å8æd\013¥î\023\001u®ì$Ø\f9Â \013Dà1ì!\005ºJ\t÷èFÄ\037\005d\r.åNî\025ã\034·ÿÞ\027ÍúìÆ\026û2¹<èû\025°>'Þ\023\036ùÚ0åû(9\bÑ\022M\003Xßþ^Úõ'ÏÂ\024¢Ùýð$Í\002³2ÕêûÏ\031\nÔ'ûëX.7¡\021<OòÓk\tïÐ\"òé¼l¸\b@¾\032GÉØô üÆñ)¸ì0\005ç\002>ö+O\036<ßm\032ïW÷\024­K$Ü\000i½2C\013Ó\004ó&â±#\003å¡*\016Ü5P\000\016÷ì*Ññ\021\001±Y\027à?æ2¥\034÷\023ÍHµ\035w\t³ãÌ\016¾ù-§\rä(¿9ñ\007Ä=õ\020ÛòÊ!ç»^×ÿ\0130ÀF\034þ[Á\007Ë ×EÈþ[á3ê\f,ÁS\016Ú\007BÄå#û\0176îÔ\032\000_ê\027Õ\001Ç?Ù\bþ\035â£-ë\037§\004`ý\023Jù\035Ç@ê\022÷×­Aõ\027ä/\0315ª$\006M ùÏ\004î©ø\030Ðm\002V×áÆø¨F3ò\0076ç\023óMÑr\017øW\024Ïä6&´á¤+ì\006\030ÝdË8\006áì&¶û]ð\004Þè\025ÃõÛ?µi$ÿ8¹í,\033;ñ\n)Q\023&Ý\fÆ)D·]!°/\027ìÅA×¹ýM\fÃî\030>Ù\0168²Jüñ!´T\032Î\003=Õ\013¾M\017ù;Ó\f¦\031ã\022×Ê\036Lß\rÔ±\025¾ÿ¡è\004ºüÓ\036öÝüÏ\004àù!\0022\n%ß­CøÓ\007S\000Íõã\021Ò(\001æ\f1\000r\021¦ê&Í-·oï(Tý2óEè\007ôý%cê/Ð>\033\\îJ\024å©\021Jî\017;S´æïË\031\001+\0340êÀ#u/¾Q£\025ÄúñÜ\0374÷\032ÿÚ\033\002ºêÇ\t½*\021[1Á\005ÝùI\007ôØÉ.9o\0062¾'ÇÚ+\022aö7XçòØgû¸\026\tîú\036\006Ü6îÕJ)þæÆJáZó\tEÞ/\021Ö\"Q\003ùÜÎæBï\016!ä³\017\"øàÿìËØ\000æh\tòþÒ\036Ü\007Á¥\nÉ\020ä'AÜ4ÆEê\016øc\037»\t«B\004µ\023¨9$çËö_9â\030í<±!\023«\032Ì6Ã\027f9\002À\030\013µ%\034?\024ø\0335¿\f>¯ü\023?#J5\004ôÑ¨\022\002Õ­\032Ë\003à>\020Ð\030Ù.\013îÔÂý\026\005·ûÄÓj\nòýV+\002PÛýïçSÑ,LùñÒ¸ßQ¦íLä/Ñì÷Þ Ã\\\032Kæ*`ó:´,èõhíúVÉ\001\034e1\rI\037ñ+\017¢\0332Ç9×»ëö\f.Ô%\t@®òÛ\020a\b*DÌ$\005\027È&\003t\030³\000 ï\f²þ÷\"ßL\n\025ý$5¿\035%èF÷°ÝíÕ­åN@\000éß\005äx&§E±\024ÅõÞ\036\0033Æã¼ë\001\016çúÚöð¼Ø,R\022Ö.ß9Ê\017\005ÁûÑïZÙÊ\b\0018ß\016¤*\007¾7\000&Í\bÛ÷¶$M\020ú\bÑ\035\003æU5ýl\022¡éþ\031S4 e-;W\013\036A\007çËú@ê\003\035V×ë\0271åº@¬\022ãP¶ôÖ;\024ãV\032ös\023¿\036X.Ïó?\030ðá<ÈòÝ\034»ìÌ;H%Ó«úÇóÖ³Á\021Ïàû3\016&¬lÂ¸ð(D¤q%\004\033÷1ë\"Î\027cÂþñÐ\013 é;üí\025\002ªÚÂV\016û*\007\016-Ø\n¶ó\007\021ÝG\026\t\035þé(J¯Å_ó\031Ò\nö\0242\b±ÿôÈÝ\rUÿ£\nDü\006ê!ª?,Çß\0051Ö\rÆbè 2ÿ%\025I¿cÒÿMö!â^.í=\000ãNî7\005¢õ\030ëÚ\0047â!JÚüãÐ\036\021KñÒ½+Ûï2(KÙ\027ùN\"²ðH'â6ø\013µöéÙ¬í!>å¨\030ÃüË\032½ 'Ë«ÝjÔ:ÿ#\021L½î\000:Æ]=êØ6'è=\025mÇ\036´âÌ\016\001»ë\bÒ`\026÷·\006£\032DÓ\007`Ë1ùß\021ò4\004T\0249Ù\003yö\f2ú\"\022Ç\013U¶Ð÷-¤Ì\017è$\031\013ºù\001°\006\037âõ\003\021Xø8òkà4\022ÿå Î=þÜÇ+ \030:\f\003\032É¼&Ñèð\f)ä!êÖY\002½æ.ðà@æ\nf\027óT\002­ð-gÄ\023OÌüAèÔ\030\bÃ\033®Eô*ÛÁP\016êp\023ïL\001ÞóµUG\bnÝ¬C¹ùKÅ\022´\031òB\033ù3\005\025(ûÝ³0ÔøßIÎ\034áô*ª\0164$ºþMç%ÖýÉ\035?\0043Õô$»å Â(èÒ-÷ëý\020/\002\035Ò¤\0068HáÏ'\bØa¼ ÖÃM\035\006CÀ)\025\006\0164ëÔ[ÞïÄ\n)Û0B\003\020Wê\fªïù\032,\t¬4\r=új\022ÿ\036Ø9¤\031Ëô`ß+þðû\016RëÿÌDé\001ñ¦Ïìã\r©:íÚúG¼\nÿ\033FÑfô²îÎø¦.ºâf\024ÌáTÄüZÐØ\t©Aâ\r·$MåÀ\013ëR\025×. Ä«7\026°\017ö5Y\022<÷\"rýÊQ&\002¨!?ö´\022\003â\037\025\013_ß\027ðH\007Ô%´E\002èÛ\026ñéI\032ËíÁcòÐù4\023<©\"Ê\013åe\003Ûõ,â$Ü\nµ+\004Ùò\035æÂa\026ãÈ-è2ùª:\001À!9Èþ\037ô8ü\020\035A/\004¿+õ6\006(\025\000Þ\007×\004ú¼õE·í;\034\n¾nýH\032ÑçüÇK\025\t2±\020õÓï\005tÙ#QëÊÖ*æ\005Ú+ Â[Ùì(õË°\020\000]ß´úR<-[\037ð(pÜ3\001\031¥úËNéÔ\006Åï0d\036Þ7ºÑÿEÝ/<\r\031ýÁ\b@\016ZûIõ\021té\r\032å\0052¼\bmã#Ô¥\023\037ÏæØ\fÆé¹FÈ\030é\020ÏTà)\022ò =\023ª\000¼ô\017ë%_î\bú\037R¹5Îó\025Ý ï\030³&ÑA¶ùLÏ«\027SÕû8ðP\013ìC\003%ð±\033÷\021áÿB°#ñ\tÖ\000B/²÷ÞQ&×B\007®ùá\026!ÎçÃ\001ìàH+µ\036ÿ3Åà\ní\0001Ý#ò@ÿß\016\025Ä\035üÚ1¿\017IýU\0057Ò\013ô-\006üÄ5i»ä\030\004Ì6\tå\027¢X,Æ\003K¼+i\023×'ø§\005äUÒ$\005]9¨Ê\026\007\021)Æí\"Fæ0\006Êqõ¬6ÍÜ'¥a!LØä=\035ëø\016Ù]ì\020ûËñ\034Ô?\016Ù5ýñ¯\034>\f[\023í;ø¹è\023×\037öX¿êhù³5Ð\001õ¯A)å\030ÿà\036ë\025Áñç²\024½ï^Ó\023'«JÃ\034þ(¾v1\005âþèõª\tâG\004Ê¼ÔýÈ\rBòýHã'8×\002â\033Q\013¼eÞ".getBytes(StandardCharsets.ISO_8859_1);
    public static final float[] TRI_BLUE_NOISE_MULTIPLIERS = new float[4096];
    static {
        for (int i = 0; i < 4096; i++) {
            //double phi = 1.6180339887498949;
            double gauss = OtherMath.probit((PaletteReducer.TRI_BLUE_NOISE[i] + 128.5) * 0x1p-8);
            TRI_BLUE_NOISE_MULTIPLIERS[i] = (float)Math.exp(gauss * 0.5);
        }
    }

    /**
     * A blue-noise-based dither that uses a tiling 64x64 noise texture to add error to an image.
     * <br>
     * There are times to use {@link #reduceBluish(Pixmap)} and times to use this; each palette and
     * source image will have different qualities of result.
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceTrueBlue3(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
//        float adj, strength = (float) (48.0 * ditherStrength / populationBias), pos;
        float adj, strength = (float) (0.1375 * ditherStrength / populationBias);
//        float adj, strength = (float) (36.0 * ditherStrength / populationBias);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    float pos = (PaletteReducer.thresholdMatrix64[(px & 7) | (y & 7) << 3] - 31.5f) * 0.2f;
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[1][(px & 63) | (y & 63) << 6] + 0.5f) * strength) + pos;
                    int rr = MathUtils.clamp((int) (adj + ((color >>> 24)       )), 0, 255);
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[2][(px & 63) | (y & 63) << 6] + 0.5f) * strength) + pos;
                    int gg = MathUtils.clamp((int) (adj + ((color >>> 16) & 0xFF)), 0, 255);
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[3][(px & 63) | (y & 63) << 6] + 0.5f) * strength) + pos;
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
    /**
     * A blue-noise-based dither that uses a tiling 64x64 noise texture to add error to an image.
     * <br>
     * There are times to use {@link #reduceBluish(Pixmap)} and times to use this; each palette and
     * source image will have different qualities of result.
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceTrueBlue4 (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        float adj, strength = (float) (40.0 * ditherStrength / (populationBias * populationBias));
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    int ti = (px & 63) | (y & 63) << 6;
                    float variation = (strength + 0x1p-5f * (BlueNoise.TRIANGULAR_BLUE_NOISE[0][ti] + 0.5f)) * 0.007f;
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[1][ti] + 0.5f) * variation);
                    int rr = MathUtils.clamp((int) (adj + ((color >>> 24)       )), 0, 255);
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[2][ti] + 0.5f) * variation);
                    int gg = MathUtils.clamp((int) (adj + ((color >>> 16) & 0xFF)), 0, 255);
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[3][ti] + 0.5f) * variation);
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

    /**
     * A blue-noise-based dither that uses a tiling 64x64 noise texture to add error to an image.
     *
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceTrueBlue5(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
//        float adj, strength = (float) (48.0 * ditherStrength / populationBias), pos;
//        float adj, strength = (float) (0.1375 * ditherStrength / populationBias);
//        float adj, strength = (float) (36.0 * ditherStrength / populationBias);
        float adj, strength = (float) (32 * ditherStrength / (populationBias));
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
//                    float pos = (PaletteReducer.thresholdMatrix64[(px & 7) | (y & 7) << 3] - 31.5f) * 0.2f + 0.5f;
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[1][(px & 63) | (y & 63) << 6] + 0.5f));
                    adj = adj * strength / (12f + Math.abs(adj));
                    int rr = Math.min(Math.max((int) (adj + ((color >>> 24)       ) + 0.5f), 0), 255);
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[2][(px & 63) | (y & 63) << 6] + 0.5f));
                    adj = adj * strength / (12f + Math.abs(adj));
                    int gg = Math.min(Math.max((int) (adj + ((color >>> 16) & 0xFF) + 0.5f), 0), 255);
                    adj = ((BlueNoise.TRIANGULAR_BLUE_NOISE[3][(px & 63) | (y & 63) << 6] + 0.5f));
                    adj = adj * strength / (12f + Math.abs(adj));
                    int bb = Math.min(Math.max((int) (adj + ((color >>> 8)  & 0xFF) + 0.5f), 0), 255);
                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF]);
                }
            }

        }
        pixmap.setBlending(blending);
        return pixmap;
    }
    public Pixmap reduceRing (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color;
        float str = (float) ((120.0 * 0.1875) * ditherStrength / Math.sqrt(Math.sqrt(colorCount))),
                limit = (float) Math.pow(5 * str, 1.635 - populationBias);
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
//                    float pos = (PaletteReducer.thresholdMatrix64[(px & 7) | (y & 7) << 3] - 31.5f) * 1.5f + 0.5f;
//                    float pos = (px * 0.06711056f + y * 0.00583715f);
//                    pos -= (int) pos;
//                    pos *= 52.9829189f;
//                    pos -= (int) pos;
//                    pos = (pos - 0.5f) * 96f + 0.5f;
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + Math.min(Math.max(OtherMath.cbrtApprox(BlueNoise.TRIANGULAR_BLUE_NOISE[1][(px & 63) | (y & 63) << 6] + 0.5f) * str, -limit), limit) + 0.5f), 0), 255);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + Math.min(Math.max(OtherMath.cbrtApprox(BlueNoise.TRIANGULAR_BLUE_NOISE[2][(px & 63) | (y & 63) << 6] + 0.5f) * str, -limit), limit) + 0.5f), 0), 255);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + Math.min(Math.max(OtherMath.cbrtApprox(BlueNoise.TRIANGULAR_BLUE_NOISE[0][(px & 63) | (y & 63) << 6] + 0.5f) * str, -limit), limit) + 0.5f), 0), 255);

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
//                color = pixmap.getPixel(px, y);
//                if ((color & 0x80) == 0 && hasTransparent)
//                    pixmap.drawPixel(px, y, 0);
//                else {
////                    color |= (color >>> 5 & 0x07070700) | 0xFE;
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
     * A different kind of blue-noise-based dither; combines error-diffusion with (tri-mapped, made into multipliers
     * using {@link Math#exp(double)}) blue noise like {@link #reduceScatter(Pixmap)}, but also a tri-mapped additive
     * blue noise pattern (the same type as what {@link #reduceTrueBlue3(Pixmap)} uses) and a checkerboard pattern (also
     * just like in TrueBlue) to adjust error further (additively, again). This adds its error in first before sending
     * it through error-diffusion, where the accumulated error is multiplied by blue noise again. This uses the same
     * blue noise texture for both the additive and multiplicative steps, but offsets them so that they don't apply
     * twice to the same pixel.
     * <br>
     * This can be seen as a mid-way point between {@link #reduceTrueBlue3(Pixmap)} and {@link #reduceScatter(Pixmap)}.
     * This performs almost as well on gradients as TrueBlue, and almost as well on color preservation as Scatter.
     * Similarly, it does much better on gradients when compared to Scatter, and on color preservation when compared to
     * TrueBlue. Some palettes and some images will look significantly better with one or two of these three, but not
     * the other(s); you may need to experiment.
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceBluish (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float)(ditherStrength * 3.5), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f,
                adj, strength = (float) (32.0 * ditherStrength / populationBias);
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    adj = ((TRI_BLUE_NOISE[(px + 32 & 63) | (y + 32 & 63) << 6] + 0.5f) * 0.007f); // slightly inside -1 to 1 range, should be +/- 0.8925
                    adj = Math.min(Math.max(adj * strength + ((px + y << 4 & 16) - 8f), -16f), 16f);
//                    adj = Math.min(Math.max(adj * strength + (thresholdMatrix[((px & 3) | (y & 3) << 2)] - 7.5f), -16f), 16f);

//                    adj = Math.min(Math.max(adj * strength + (px + y << 4 & 16) - 8f, -24f), 24f);
                    //                    int rr = MathUtils.clamp((int) (adj + ((color >>> 24)       )), 0, 255);
                    //                    int gg = MathUtils.clamp((int) (adj + ((color >>> 16) & 0xFF)), 0, 255);
                    //                    int bb = MathUtils.clamp((int) (adj + ((color >>> 8)  & 0xFF)), 0, 255);
                    float tbn = TRI_BLUE_NOISE_MULTIPLIERS[(px & 63) | ((y << 6) & 0xFC0)];
                    er = adj + (curErrorRed[px] * tbn);
                    eg = adj + (curErrorGreen[px] * tbn);
                    eb = adj + (curErrorBlue[px] * tbn);
//                    color |= (color >>> 5 & 0x07070700) | 0xFF;
                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>24)-    (used>>>24))    );
                    gdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>16&255)-(used>>>16&255)));
                    bdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>8&255)- (used>>>8&255)) );
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

    /**
     * An error-diffusion dither based on {@link #reduceFloydSteinberg(Pixmap)}, but adding in triangular-mapped blue
     * noise before diffusing, like {@link #reduceTrueBlue3(Pixmap)}. This looks like {@link #reduceScatter(Pixmap)} in
     * many cases, but smooth gradients are much smoother with Neue than Scatter. Scatter multiplies error by a blue
     * noise value, where this adds blue noise regardless of error. This also preserves color better than TrueBlue,
     * while keeping similar gradient smoothness.
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceNeue(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float) ditherStrength * 7f, w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f,
                adj, strength = (float) (32.0 * 0.005f * ditherStrength / (populationBias * populationBias)),
                limit = (float) Math.pow(80, 1.635 - populationBias);

        for (int py = 0; py < h; py++) {
            int ny = py + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {
                    adj = ((TRI_BLUE_NOISE[(px & 63) | (py & 63) << 6] + 0.5f) * strength); // plus or minus 255/400
                    adj = Math.min(Math.max(adj, -limit), limit);
                    er = adj + (curErrorRed[px]);
                    eg = adj + (curErrorGreen[px]);
                    eb = adj + (curErrorBlue[px]);

                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    rdiff = OtherMath.cbrtShape(0x1.7p-10f * ((color>>>24)-    (used>>>24))    );
                    gdiff = OtherMath.cbrtShape(0x1.7p-10f * ((color>>>16&255)-(used>>>16&255)));
                    bdiff = OtherMath.cbrtShape(0x1.7p-10f * ((color>>>8&255)- (used>>>8&255)) );
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

    public Pixmap reduceDodgy(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float) (25.0 * ditherStrength * populationBias * populationBias),
                w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f,
                strength = (float) (0.25 * ditherStrength / (populationBias * populationBias)),
                limit = 5f + 90f / (float)Math.sqrt(colorCount+1.5),
//                dmul = (float)(0x1p-8 / populationBias);
                dmul = 0x1.8p-9f;

        for (int py = 0; py < h; py++) {
            int ny = py + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {
                    er = Math.min(Math.max(((BlueNoise.TILE_TRI_NOISE[0][(px & 63) | (py & 63) << 6] + 0.5f) * strength), -limit), limit) + (curErrorRed[px]);
                    eg = Math.min(Math.max(((BlueNoise.TILE_TRI_NOISE[1][(px & 63) | (py & 63) << 6] + 0.5f) * strength), -limit), limit) + (curErrorGreen[px]);
                    eb = Math.min(Math.max(((BlueNoise.TILE_TRI_NOISE[2][(px & 63) | (py & 63) << 6] + 0.5f) * strength), -limit), limit) + (curErrorBlue[px]);
//                    double mag = 0.5/Math.sqrt(er * er + eg * eg + eb * eb);
                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    rdiff = (dmul * ((color>>>24)-    (used>>>24))    );
                    gdiff = (dmul * ((color>>>16&255)-(used>>>16&255)));
                    bdiff = (dmul * ((color>>>8&255)- (used>>>8&255)) );
//                    rdiff /= (0.2f + Math.abs(rdiff));
//                    gdiff /= (0.2f + Math.abs(gdiff));
//                    bdiff /= (0.2f + Math.abs(bdiff));

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


    public Pixmap reducePlexus(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float) ditherStrength * 2.75f, w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f,
                adj, strength = (float) (24 * ditherStrength * populationBias) * 0.005f;
        for (int py = 0; py < h; py++) {
            int ny = py + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {
                    adj = ((TRI_BLUE_NOISE[(px & 63) | (py & 63) << 6] + 0.5f) * strength); // plus or minus 255/400
                    adj = Math.min(Math.max(adj, -16f), 16f);
                    er = adj + (curErrorRed[px]);
                    eg = adj + (curErrorGreen[px]);
                    eb = adj + (curErrorBlue[px]);

                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    rdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>24)-    (used>>>24))    );
                    gdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>16&255)-(used>>>16&255)));
                    bdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>8&255)- (used>>>8&255)) );
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
    public Pixmap reduceWeave(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float) (20.0 * Math.sqrt(ditherStrength) * populationBias * populationBias * populationBias * populationBias), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f,
                strength = (float) (48.0 * ditherStrength / (populationBias * populationBias * populationBias * populationBias)),
                limit = 5f + 130f / (float)Math.sqrt(colorCount+1.5), dmul = (float) (0x4p-10 / populationBias);

        for (int py = 0; py < h; py++) {
            int ny = py + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {
                    er = Math.min(Math.max(((((px+1) * 0xC13FA9A902A6328FL + (py+1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-23f - 0x1.4p-1f) * strength, -limit), limit) + (curErrorRed[px]);
                    eg = Math.min(Math.max(((((px+3) * 0xC13FA9A902A6328FL + (py-1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-23f - 0x1.4p-1f) * strength, -limit), limit) + (curErrorGreen[px]);
                    eb = Math.min(Math.max(((((px+2) * 0xC13FA9A902A6328FL + (py-4) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-23f - 0x1.4p-1f) * strength, -limit), limit) + (curErrorBlue[px]);

                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    rdiff = (dmul * ((color>>>24)-    (used>>>24))    );
                    gdiff = (dmul * ((color>>>16&255)-(used>>>16&255)));
                    bdiff = (dmul * ((color>>>8&255)- (used>>>8&255)) );
//                    rdiff = Math.min(Math.max(dmul * ((color>>>24)-    (used>>>24))    , -0.25f), 0.25f);
//                    gdiff = Math.min(Math.max(dmul * ((color>>>16&255)-(used>>>16&255)), -0.25f), 0.25f);
//                    bdiff = Math.min(Math.max(dmul * ((color>>>8&255)- (used>>>8&255)) , -0.25f), 0.25f);

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

//        boolean hasTransparent = (paletteArray[0] == 0);
//        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight(), seed = (lineLen + 1) * h;
//        Pixmap.Blending blending = pixmap.getBlending();
//        pixmap.setBlending(Pixmap.Blending.None);
//        int color, used;
//
//        float adj, strength = (float) (ditherStrength * populationBias) * 0x3p-7f;
//        for (int y = 0; y < h; y++) {
//            for (int px = 0; px < lineLen; px++) {
//                color = pixmap.getPixel(px, y);
//                if ((color & 0x80) == 0 && hasTransparent)
//                    pixmap.drawPixel(px, y, 0);
//                else {
//                    int rr = ((color >>> 24)       );
//                    int gg = ((color >>> 16) & 0xFF);
//                    int bb = ((color >>> 8)  & 0xFF);
//                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                            | ((gg << 2) & 0x3E0)
//                            | ((bb >>> 3))] & 0xFF];
//                    adj = (BlueNoise.getSeededTriOmniTiling(px, y, seed) + 0.5f) * strength;
////                    adj = (PaletteReducer.TRI_BLUE_NOISE[(px & 63) | (y & 63) << 6] + 0.5f) * strength;
////                    adj = TRI_BLUE_NOISE_MULTIPLIERS[(px & 63) | ((y << 6) & 0xFC0)];
////                    adj *= adj * strength;
//
//                    rr = Math.min(Math.max((int) (rr + (adj * ((rr - (used >>> 24))))), 0), 0xFF);
//                    gg = Math.min(Math.max((int) (gg + (adj * ((gg - (used >>> 16 & 0xFF))))), 0), 0xFF);
//                    bb = Math.min(Math.max((int) (bb + (adj * ((bb - (used >>> 8 & 0xFF))))), 0), 0xFF);
//
//                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                            | ((gg << 2) & 0x3E0)
//                            | ((bb >>> 3))] & 0xFF]);
//                }
//            }
//        }

//        float adj, strength = (float) (ditherStrength * populationBias) * 0x3p-7f;
//        for (int y = 0; y < h; y++) {
//            for (int px = 0; px < lineLen; px++) {
//                color = pixmap.getPixel(px, y);
//                if ((color & 0x80) == 0 && hasTransparent)
//                    pixmap.drawPixel(px, y, 0);
//                else {
//                    adj = ((BlueNoise.get(px + 29, y + 31, BlueNoise.TILE_TRI_NOISE[0]) + 0.5f)) * 0x1p-4f;
//                    int rr = MathUtils.clamp((int) (adj + ((color >>> 24)       )), 0, 255);
//                    int gg = MathUtils.clamp((int) (adj + ((color >>> 16) & 0xFF)), 0, 255);
//                    int bb = MathUtils.clamp((int) (adj + ((color >>> 8)  & 0xFF)), 0, 255);
//                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                            | ((gg << 2) & 0x3E0)
//                            | ((bb >>> 3))] & 0xFF];
//
//                    adj = ((BlueNoise.get(px, y, BlueNoise.TILE_TRI_NOISE[0]) + 0.5f)) * strength;
//
//
////                    adj = ((PaletteReducer.RAW_BLUE_NOISE[bn = (px & 63) | (y & 63) << 6] + 0.5f) * 0.01f); // 0.007843138f is 1f / 127.5f
////                    adj += ((px + y & 1) - 0.5f) * (0.5f + PaletteReducer.RAW_BLUE_NOISE[bn * 0xDAB & 4095]) * -0x1p-9f;
////                    adj += ((px + y & 1) - 0.5f);// * (0.5f + BlueNoise.get(px * 19 + 29, y * 23 + 17, BlueNoise.TILE_TRI_NOISE[0])) * -0x1.6p-10f;
//                    rr = MathUtils.clamp((int) (rr + (adj * ((rr - (used >>> 24))))), 0, 0xFF);
//                    gg = MathUtils.clamp((int) (gg + (adj * ((gg - (used >>> 16 & 0xFF))))), 0, 0xFF);
//                    bb = MathUtils.clamp((int) (bb + (adj * ((bb - (used >>> 8 & 0xFF))))), 0, 0xFF);
//                    pixmap.drawPixel(px, y, paletteArray[paletteMapping[((rr << 7) & 0x7C00)
//                            | ((gg << 2) & 0x3E0)
//                            | ((bb >>> 3))] & 0xFF]);
//                }
//            }
//        }


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
//                color = pixmap.getPixel(px, y);
//                if ((color & 0x80) == 0 && hasTransparent)
//                    pixmap.drawPixel(px, y, 0);
//                else {
////                    color |= (color >>> 5 & 0x07070700) | 0xFE;
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
     * Weaning myself off highly-structured artifacts.
     * This is going to be called Wren in other code I port it to, because the story won't make sense.
     * @param pixmap
     * @return
     */
    public Pixmap reduceWean(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float) (32.0 * ditherStrength * (populationBias * populationBias)), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f,
                strength = (float) (0.2 * ditherStrength / (populationBias * populationBias * populationBias * populationBias)),
                limit = 5f + 125f / (float)Math.sqrt(colorCount+1.5),
                dmul = 0x1p-8f;

        for (int py = 0; py < h; py++) {
            int ny = py + 1;
            for (int i = 0; i < lineLen; i++) {
                curErrorRed[i] = nextErrorRed[i];
                curErrorGreen[i] = nextErrorGreen[i];
                curErrorBlue[i] = nextErrorBlue[i];
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, py);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, py, 0);
                else {

                    er = Math.min(Math.max(( ( (BlueNoise.TILE_TRI_NOISE[0][(px & 63) | (py & 63) << 6] + 0.5f) + ((((px+1) * 0xC13FA9A902A6328FL + (py+1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1p-16f - 0x1p+6f)) * strength) + (curErrorRed[px]), -limit), limit);
                    eg = Math.min(Math.max(( ( (BlueNoise.TILE_TRI_NOISE[1][(px & 63) | (py & 63) << 6] + 0.5f) + ((((px+3) * 0xC13FA9A902A6328FL + (py-1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1p-16f - 0x1p+6f)) * strength) + (curErrorGreen[px]), -limit), limit);
                    eb = Math.min(Math.max(( ( (BlueNoise.TILE_TRI_NOISE[2][(px & 63) | (py & 63) << 6] + 0.5f) + ((((px+2) * 0xC13FA9A902A6328FL + (py-4) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1p-16f - 0x1p+6f)) * strength) + (curErrorBlue[px]), -limit), limit);

                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, py, used);
                    rdiff = dmul * ((color>>>24)-    (used>>>24))    ;
                    gdiff = dmul * ((color>>>16&255)-(used>>>16&255));
                    bdiff = dmul * ((color>>>8&255)- (used>>>8&255)) ;

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

    /**
     * Blue noise + Burkes error diffusion (and the R2 sequence).
     * @param pixmap
     * @return
     */
    public Pixmap reduceBlubber(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float partialDitherStrength = (float) (0.4 * ditherStrength * (populationBias * populationBias)),
                strength = (float) (40.0 * ditherStrength / (populationBias * populationBias)),
                blueStrength = (float) (0.15 * ditherStrength / (populationBias * populationBias)),
                limit = 5f + 125f / (float)Math.sqrt(colorCount+1.5),
                r1, g1, b1, r2, g2, b2, r4, g4, b4;

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
            for (int x = 0; x < lineLen; x++) {
                color = pixmap.getPixel(x, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(x, y, 0);
                else {
                    er = Math.min(Math.max(( ( (BlueNoise.TILE_TRI_NOISE[0][(x & 63) | (y & 63) << 6] + 0.5f) * blueStrength + ((((x+1) * 0xC13FA9A902A6328FL + (y+1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-24f - 0x1.4p-2f) * strength)), -limit), limit) + (curErrorRed[x]);
                    eg = Math.min(Math.max(( ( (BlueNoise.TILE_TRI_NOISE[1][(x & 63) | (y & 63) << 6] + 0.5f) * blueStrength + ((((x+3) * 0xC13FA9A902A6328FL + (y-1) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-24f - 0x1.4p-2f) * strength)), -limit), limit) + (curErrorGreen[x]);
                    eb = Math.min(Math.max(( ( (BlueNoise.TILE_TRI_NOISE[2][(x & 63) | (y & 63) << 6] + 0.5f) * blueStrength + ((((x+2) * 0xC13FA9A902A6328FL + (y-4) * 0x91E10DA5C79E7B1DL) >>> 41) * 0x1.4p-24f - 0x1.4p-2f) * strength)), -limit), limit) + (curErrorBlue[x]);

                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(x, y, used);
                    rdiff = ((color>>>24)-    (used>>>24))     * partialDitherStrength;
                    gdiff = ((color>>>16&255)-(used>>>16&255)) * partialDitherStrength;
                    bdiff = ((color>>>8&255)- (used>>>8&255))  * partialDitherStrength;

                    r1 = rdiff * 16f / (float)Math.sqrt(2048f + rdiff * rdiff);
                    g1 = gdiff * 16f / (float)Math.sqrt(2048f + gdiff * gdiff);
                    b1 = bdiff * 16f / (float)Math.sqrt(2048f + bdiff * bdiff);
                    r2 = r1 + r1;
                    g2 = g1 + g1;
                    b2 = b1 + b1;
                    r4 = r2 + r2;
                    g4 = g2 + g2;
                    b4 = b2 + b2;
                    if(x < lineLen - 1)
                    {
                        curErrorRed[x+1]   += r4;
                        curErrorGreen[x+1] += g4;
                        curErrorBlue[x+1]  += b4;
                        if(x < lineLen - 2)
                        {

                            curErrorRed[x+2]   += r2;
                            curErrorGreen[x+2] += g2;
                            curErrorBlue[x+2]  += b2;
                        }
                    }
                    if(ny < h)
                    {
                        if(x > 0)
                        {
                            nextErrorRed[x-1]   += r2;
                            nextErrorGreen[x-1] += g2;
                            nextErrorBlue[x-1]  += b2;
                            if(x > 1)
                            {
                                nextErrorRed[x-2]   += r1;
                                nextErrorGreen[x-2] += g1;
                                nextErrorBlue[x-2]  += b1;
                            }
                        }
                        nextErrorRed[x]   += r4;
                        nextErrorGreen[x] += g4;
                        nextErrorBlue[x]  += b4;
                        if(x < lineLen - 1)
                        {
                            nextErrorRed[x+1]   += r2;
                            nextErrorGreen[x+1] += g2;
                            nextErrorBlue[x+1]  += b2;
                            if(x < lineLen - 2)
                            {

                                nextErrorRed[x+2]   += r1;
                                nextErrorGreen[x+2] += g1;
                                nextErrorBlue[x+2]  += b1;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
//                    color |= (color >>> 5 & 0x07070700) | 0xFF;
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

    public Pixmap reduceGarbage (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        double adj, strength = ditherStrength * populationBias * 1.5;
        long s = 0xC13FA9A902A6328FL;
        for (int y = 0; y < h; y++) {
            for (int px = 0; px < lineLen; px++) {
                color = pixmap.getPixel(px, y);
                byte bn = PaletteReducer.RAW_BLUE_NOISE[(px & 63) | (y & 63) << 6];
                if (((color & 0x80) == 0 || bn < -115) && hasTransparent)
                {
                    if(bn > 125)
                        pixmap.drawPixel(px, y, paletteArray[(int)((s = s * 0xD1342543DE82EF95L + 0x91E10DA5C79E7B1DL) >>> 33) % colorCount]);
                    else
                        pixmap.drawPixel(px, y, 0);
                }
                else {
//                    color |= (color >>> 5 & 0x07070700) | 0xFF;
                    int rr = ((color >>> 24)       );
                    int gg = ((color >>> 16) & 0xFF);
                    int bb = ((color >>> 8)  & 0xFF);
                    used = paletteArray[paletteMapping[((rr << 7) & 0x7C00)
                            | ((gg << 2) & 0x3E0)
                            | ((bb >>> 3))] & 0xFF];
                    adj = ((bn + 0.5f) * 0.007843138f);
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
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float)(ditherStrength * 3.5), w3 = w1 * 3f, w5 = w1 * 5f, w7 = w1 * 7f;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    //                    adj = ((TRI_BLUE_NOISE[(px & 63) | (y & 63) << 6] + 0.5f) * 0.007f); // slightly inside -1 to 1 range, should be +/- 0.8925
                    //                    adj = Math.min(Math.max(adj * strength + (px + y << 4 & 16) - 8f, -20f), 20f);
                    //                    int rr = MathUtils.clamp((int) (adj + ((color >>> 24)       )), 0, 255);
                    //                    int gg = MathUtils.clamp((int) (adj + ((color >>> 16) & 0xFF)), 0, 255);
                    //                    int bb = MathUtils.clamp((int) (adj + ((color >>> 8)  & 0xFF)), 0, 255);
                    float tbn = TRI_BLUE_NOISE_MULTIPLIERS[(px & 63) | ((y << 6) & 0xFC0)];
                    er = (curErrorRed[px] * tbn);
                    eg = (curErrorGreen[px] * tbn);
                    eb = (curErrorBlue[px] * tbn);
//                    color |= (color >>> 5 & 0x07070700) | 0xFF;
                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>24)-    (used>>>24))    );
                    gdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>16&255)-(used>>>16&255)));
                    bdiff = OtherMath.cbrtShape(0x2.Ep-8f * ((color>>>8&255)- (used>>>8&255)) );
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

    void computePaletteGamma(){
        //// unused now, this was the only line before
//        System.arraycopy(paletteArray, 0, gammaArray, 0, paletteArray.length);


//        double gamma = 1.0;
//        double gamma = 1.8 - ditherStrength * 1.8;
//        for (int i = 0; i < paletteArray.length; i++) {
//            int color = paletteArray[i];
//            double r = Math.pow((color >>> 24) / 255.0, gamma);
//            double g = Math.pow((color >>> 16 & 0xFF) / 255.0, gamma);
//            double b = Math.pow((color >>>  8 & 0xFF) / 255.0, gamma);
//            int a = color & 0xFF;
//            gammaArray[i] = (int)(r * 255.999) << 24 | (int)(g * 255.999) << 16 | (int)(b * 255.999) << 8 | a;
//        }
    }

    /**
     * Given by Joel Yliluoma in <a href="https://bisqwit.iki.fi/story/howto/dither/jy/">a dithering article</a>.
     */
    static final int[] thresholdMatrix8 = {
            0, 4, 2, 6,
            3, 7, 1, 5,
    };

    /**
     * Given by Joel Yliluoma in <a href="https://bisqwit.iki.fi/story/howto/dither/jy/">a dithering article</a>.
     */
    static final int[] thresholdMatrix64 = {
              0,  48,  12,  60,   3,  51,  15,  63,
             32,  16,  44,  28,  35,  19,  47,  31,
              8,  56,   4,  52,  11,  59,   7,  55,
             40,  24,  36,  20,  43,  27,  39,  23,
              2,  50,  14,  62,   1,  49,  13,  61,
             34,  18,  46,  30,  33,  17,  45,  29,
             10,  58,   6,  54,   9,  57,   5,  53,
             42,  26,  38,  22,  41,  25,  37,  21
    };

    /**
     * Given by Joel Yliluoma in <a href="https://bisqwit.iki.fi/story/howto/dither/jy/">a dithering article</a>.
     */
    private static final int[] thresholdMatrix = {
            0,  12,   3,  15,
            8,   4,  11,   7,
            2,  14,   1,  13,
            10,  6,   9,   5,
    };

    static final int[] thresholdVector9 = {
            8, 2, 6, 5, 0, 4, 1, 7, 3
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
     * Sorting network, found by http://pages.ripco.net/~jgamble/nw.html , considered the best known for length 8.
     * @param i8 an 8-or-more-element array that will be sorted in-place by {@link #compareSwap(int[], int, int)}
     */
    void sort8(final int[] i8) {
        compareSwap(i8, 0, 1);
        compareSwap(i8, 2, 3);
        compareSwap(i8, 0, 2);
        compareSwap(i8, 1, 3);
        compareSwap(i8, 1, 2);
        compareSwap(i8, 4, 5);
        compareSwap(i8, 6, 7);
        compareSwap(i8, 4, 6);
        compareSwap(i8, 5, 7);
        compareSwap(i8, 5, 6);
        compareSwap(i8, 0, 4);
        compareSwap(i8, 1, 5);
        compareSwap(i8, 1, 4);
        compareSwap(i8, 2, 6);
        compareSwap(i8, 3, 7);
        compareSwap(i8, 3, 6);
        compareSwap(i8, 2, 4);
        compareSwap(i8, 3, 5);
        compareSwap(i8, 3, 4);
    }

    /**
     * Sorting network, found by http://pages.ripco.net/~jgamble/nw.html , considered the best known for length 9.
     * @param i9 a 9-or-more-element array that will be sorted in-place by {@link #compareSwap(int[], int, int)}
     */
    void sort9(final int[] i9) {
        compareSwap(i9, 0, 1);
        compareSwap(i9, 3, 4);
        compareSwap(i9, 6, 7);
        compareSwap(i9, 1, 2);
        compareSwap(i9, 4, 5);
        compareSwap(i9, 7, 8);
        compareSwap(i9, 0, 1);
        compareSwap(i9, 3, 4);
        compareSwap(i9, 6, 7);
        compareSwap(i9, 0, 3);
        compareSwap(i9, 3, 6);
        compareSwap(i9, 0, 3);
        compareSwap(i9, 1, 4);
        compareSwap(i9, 4, 7);
        compareSwap(i9, 1, 4);
        compareSwap(i9, 2, 5);
        compareSwap(i9, 5, 8);
        compareSwap(i9, 2, 5);
        compareSwap(i9, 1, 3);
        compareSwap(i9, 5, 7);
        compareSwap(i9, 2, 6);
        compareSwap(i9, 4, 6);
        compareSwap(i9, 2, 4);
        compareSwap(i9, 2, 3);
        compareSwap(i9, 5, 6);
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
        final float errorMul = (float) (ditherStrength * 0.5 / populationBias);
        computePaletteGamma();
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
                    for (int i = 0; i < 16; i++) {
                        int rr = MathUtils.clamp((int) (cr + er * errorMul), 0, 255);
                        int gg = MathUtils.clamp((int) (cg + eg * errorMul), 0, 255);
                        int bb = MathUtils.clamp((int) (cb + eb * errorMul), 0, 255);
                        usedIndex = paletteMapping[((rr << 7) & 0x7C00)
                                | ((gg << 2) & 0x3E0)
                                | ((bb >>> 3))] & 0xFF;
                        used = candidates[i] = paletteArray[usedIndex];
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
        computePaletteGamma();
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
                    for (int i = 0; i < 16; i++) {
                        int rr = MathUtils.clamp((int) (cr + er * errorMul), 0, 255);
                        int gg = MathUtils.clamp((int) (cg + eg * errorMul), 0, 255);
                        int bb = MathUtils.clamp((int) (cb + eb * errorMul), 0, 255);
                        usedIndex = paletteMapping[((rr << 7) & 0x7C00)
                                | ((gg << 2) & 0x3E0)
                                | ((bb >>> 3))] & 0xFF;
                        used = candidates[i] = paletteArray[usedIndex];
                        er += cr - (used >>> 24);
                        eg += cg - (used >>> 16 & 0xFF);
                        eb += cb - (used >>> 8 & 0xFF);
                    }
                    sort16(candidates);
                    pixmap.drawPixel(px, y, candidates[thresholdMatrix[
                            ((int) (px * 0x1.C13FA9A902A6328Fp3 + y * 0x1.9E3779B97F4A7C15p-2) & 3) ^
                                    ((px & 3) | (y & 3) << 2)
                            ]]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    public Pixmap reduceKnollBlue(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, cr, cg, cb, usedIndex;
        final float errorMul = (float) (ditherStrength * populationBias);
        computePaletteGamma();
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
                    for (int i = 0; i < 16; i++) {
                        int rr = MathUtils.clamp((int) (cr + er * errorMul), 0, 255);
                        int gg = MathUtils.clamp((int) (cg + eg * errorMul), 0, 255);
                        int bb = MathUtils.clamp((int) (cb + eb * errorMul), 0, 255);
//                        int rr = MathUtils.clamp((int) (cr - er * errorMul), 0, 255);
//                        int gg = MathUtils.clamp((int) (cg - eg * errorMul), 0, 255);
//                        int bb = MathUtils.clamp((int) (cb - eb * errorMul), 0, 255);
                        usedIndex = paletteMapping[((rr << 7) & 0x7C00)
                                | ((gg << 2) & 0x3E0)
                                | ((bb >>> 3))] & 0xFF;
                        used = candidates[i] = paletteArray[usedIndex];
                        er += cr - (used >>> 24);
                        eg += cg - (used >>> 16 & 0xFF);
                        eb += cb - (used >>> 8 & 0xFF);
                    }
                    sort16(candidates);
//                    pixmap.drawPixel(px, y, candidates[thresholdMatrix[RAW_BLUE_NOISE[(px & 63) | (y & 63) << 6] + 128 >>> 4]]);
//                    pixmap.drawPixel(px, y, candidates[thresholdMatrix[((px & 3) | (y & 3) << 2)]]);
                    pixmap.drawPixel(px, y, candidates[TRI_BLUE_NOISE[(px & 63) | (y & 63) << 6] + 128 >>> 4]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;

//        boolean hasTransparent = (paletteArray[0] == 0);
//        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
//        Pixmap.Blending blending = pixmap.getBlending();
//        pixmap.setBlending(Pixmap.Blending.None);
//        int color, used, usedIndex;
//        float cr, cg, cb;
//        final float errorMul = (float) (ditherStrength * populationBias);
//        computePaletteGamma();
//        for (int y = 0; y < h; y++) {
//            for (int px = 0; px < lineLen; px++) {
//                color = pixmap.getPixel(px, y);
//                if ((color & 0x80) == 0 && hasTransparent)
//                    pixmap.drawPixel(px, y, 0);
//                else {
//                    int er = 0, eg = 0, eb = 0;
//                    int checker = (px & 1) + (y & 1);
////                    int checker = (px + y & 1) << 2;
//                    cr = (color >>> 24);
//                    cg = (color >>> 16 & 0xFF);
//                    cb = (color >>> 8 & 0xFF);
//                    for (int i = 0; i < 8; i++) {
//                        int rr = MathUtils.clamp((int) (cr + er * errorMul), 0, 255);
//                        int gg = MathUtils.clamp((int) (cg + eg * errorMul), 0, 255);
//                        int bb = MathUtils.clamp((int) (cb + eb * errorMul), 0, 255);
//                        usedIndex = paletteMapping[((rr << 7) & 0x7C00)
//                                | ((gg << 2) & 0x3E0)
//                                | ((bb >>> 3))] & 0xFF;
//                        used = candidates[i ^ checker] = paletteArray[usedIndex];
//                        er += cr - (used >>> 24);
//                        eg += cg - (used >>> 16 & 0xFF);
//                        eb += cb - (used >>> 8 & 0xFF);
//                    }
//                    sort8(candidates);
////                    pixmap.drawPixel(px, y, candidates[RAW_BLUE_NOISE[(px & 63) | (y & 63) << 6] + 128 >>> 5]);
////                    pixmap.drawPixel(px, y, candidates[(int)Math.sqrt(RAW_BLUE_NOISE[(px & 63) | (y & 63) << 6] + 128)]);
////                    pixmap.drawPixel(px, y, candidates[bn >>> 5 ^ (px + y & 1)]);
////                    int bn = BlueNoise.getSeededTriOmniTiling(px, y, 123) >>> 4 & 14;
//                    int bn = BlueNoise.getSeededOmniTiling(px, y, 123) + 128 >>> 5;
////                    int rawY = BlueNoise.getSeededTriOmniTiling(y + 35, px + 29, 123456) + 128 >>> 6;
//                    pixmap.drawPixel(px, y, candidates[(bn)]);
//                }
//            }
//        }
//        pixmap.setBlending(blending);
//        return pixmap;
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


    public Pixmap reduceShuffle (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used, cr, cg, cb, usedIndex;
        final float errorMul = (float) (ditherStrength * 0.25 / (populationBias * populationBias));
        computePaletteGamma();
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
                    for (int i = 0; i < 16; i++) {
                        int rr = Math.min(Math.max((int) (cr + er * errorMul), 0), 255);
                        int gg = Math.min(Math.max((int) (cg + eg * errorMul), 0), 255);
                        int bb = Math.min(Math.max((int) (cb + eb * errorMul), 0), 255);
                        usedIndex = paletteMapping[((rr << 7) & 0x7C00)
                                | ((gg << 2) & 0x3E0)
                                | ((bb >>> 3))] & 0xFF;
                        used = candidates[i] = paletteArray[usedIndex];
                        er += cr - (used >>> 24);
                        eg += cg - (used >>> 16 & 0xFF);
                        eb += cb - (used >>> 8 & 0xFF);
                    }
                    sort16(candidates);
//                    int shuf = ((px & 3) | (y & 3) << 2);
//                    shuf ^= shuf >>> 3 ^ (px ^ y) >>> 2 & 15;
//                    shuf = shuf * ((px >>> 2) + (y >>> 5) << 1 ^ 13) & 15;
//                    shuf ^= shuf >>> 1 ^ (px ^ y) >>> 6 & 15;
//                    shuf = shuf * ((px >>> 5) - (y >>> 2) << 1 ^ 5) & 15;
//                    shuf ^= shuf >>> 2 ^ (px ^ y) >>> 14 & 15;
//                    shuf = shuf * ((px >>> 8) - (y >>> 8) << 1 ^ 11) & 15;
//                    shuf ^= shuf >>> 2 ^ (px ^ y) >>> 10 & 15;
//                    int shuf = ((px & 0xFFFF) | (y & 0xFFFF) << 16);
//                    shuf = shuf * 0xB0553 ^ 0x9E3779BD;
//                    shuf ^= shuf >>> 15;
//                    shuf = shuf * 0xB0553 ^ 0x9E3779BD;
//                    shuf ^= shuf >>> 15;
//                    shuf = shuf * 0xB0553 ^ 0x9E3779BD;
                    pixmap.drawPixel(px, y, candidates[
//                            Integer.numberOfTrailingZeros(shuf >>> 17 | 0x8000)
//                            thresholdMatrix[((px & 3) | (y & 3) << 2)]
//                            (((px+842 ^ px >>> 1) + (y+842 ^ y >>> 1)) * 0xDE4D >>> 4 & 15)
                            thresholdMatrix[((((px & 3) | (y & 3) << 2)) ^ (px >>> 2) * 5 ^ (y >>> 2) * 7) & 15]
//                            thresholdMatrix[(((px & 3) | (y & 3) << 2))] ^ ((px >>> 2) * 5 ^ (y >>> 2) * 7) & 15
//                            ((px ^ px >>> 1) * 5 + (y ^ y >>> 1) * 7) % 9
//                            ((((px & 3) | (y & 3) << 2)) ^ px * 5 ^ y * 7) & 15
                            ]);
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }

    /**
     * <a href="https://twitter.com/lorenschmidt/status/1703267207268983293">"Kufic dither" by Loren Schmidt</a>.
     * @param pixmap
     * @return
     */
    public Pixmap reduceSchmidt (Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        float er, eg, eb;
        byte paletteIndex;
        float w1 = (float)(ditherStrength * 0.125),
                xorStrength = (float)(10 / (populationBias * populationBias));
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
//                    color |= (color >>> 5 & 0x07070700) | 0xFE;
                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                    | ((gg << 2) & 0x3E0)
                                    | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    float xp = ((px ^ y) % 9 - 4) * xorStrength; // my only change
                    rdiff = ((color>>>24)-    (used>>>24))     + xp;
                    gdiff = ((color>>>16&255)-(used>>>16&255)) + xp;
                    bdiff = ((color>>>8&255)- (used>>>8&255))  + xp;
                    if(ny < h)
                    {
                        if(px > 0)
                        {
                            nextErrorRed[px-1]   += rdiff * w1;
                            nextErrorGreen[px-1] += gdiff * w1;
                            nextErrorBlue[px-1]  += bdiff * w1;
                        }
                        if(px < lineLen - 1)
                        {
                            nextErrorRed[px+1]   += rdiff * w1;
                            nextErrorGreen[px+1] += gdiff * w1;
                            nextErrorBlue[px+1]  += bdiff * w1;
                        }
                    }
                }
            }
        }
        pixmap.setBlending(blending);
        return pixmap;
    }


    /**
     * Burkes dither with some extra error added in, selecting different types of error pattern in an ordered way.
     * @param pixmap
     * @return
     */
    public Pixmap reduceOverboard0(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float r4, r2, r1, g4, g2, g1, b4, b2, b1;
        float strength = (float)(ditherStrength * 0.05),
                noiseStrength = (float)(2.5f / (populationBias * populationBias));

        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float er, eg, eb, rdiff, gdiff, bdiff;
        byte paletteIndex;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    er = curErrorRed[px];
                    eg = curErrorGreen[px];
                    eb = curErrorBlue[px];
                    int rr = MathUtils.clamp((int)(((color >>> 24)       ) + er + 0.5f), 0, 0xFF);
                    int gg = MathUtils.clamp((int)(((color >>> 16) & 0xFF) + eg + 0.5f), 0, 0xFF);
                    int bb = MathUtils.clamp((int)(((color >>> 8)  & 0xFF) + eb + 0.5f), 0, 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                           | ((gg << 2) & 0x3E0)
                                           | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    float noise = 0f;
                    switch ((px << 1 & 2) | (y & 1)){
                        case 0:
                            noise += ((px ^ y) % 9 - 4);
                            noise += ((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 41) * 0x1p-20f;
                            break;
                        case 1:
                            noise += (BlueNoise.TILE_TRI_NOISE[0][(px & 63) | (y & 63) << 6] + 0.5f) * 0x1p-5f;
                            noise += ((px * -0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 41) * 0x1p-20f;
                            break;
                        case 2:
                            noise += (BlueNoise.TILE_TRI_NOISE[0][(px & 63) | (y & 63) << 6] + 0.5f) * 0x1p-6f;
                            noise += ((y * 0xC13FA9A902A6328FL + px * -0x91E10DA5C79E7B1DL) >> 41) * 0x1.8p-20f;
                            break;
                        default: // case 3:
                            noise += ((px ^ y) % 11 - 5);
                            noise += ((y * -0xC13FA9A902A6328FL + px * -0x91E10DA5C79E7B1DL) >> 41) * 0x1.8p-21f;
                            break;
                    }
                    noise *= noiseStrength;
                    rdiff = (color>>>24)-    (used>>>24)     + noise;
                    gdiff = (color>>>16&255)-(used>>>16&255) + noise;
                    bdiff = (color>>>8&255)- (used>>>8&255)  + noise;
                    r1 = rdiff * strength;
                    g1 = gdiff * strength;
                    b1 = bdiff * strength;
                    r2 = r1 + r1;
                    g2 = g1 + g1;
                    b2 = b1 + b1;
                    r4 = r2 + r2;
                    g4 = g2 + g2;
                    b4 = b2 + b2;
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
     * Burkes dither with some extra error added in, selecting different types of error pattern in an ordered way.
     * This incorporates two types of extra error to each channel of each pixel, selecting based on a grid of 2x2 pixel
     * squares. Error applies differently to each RGB channel. The types of extra error are:
     * <ul>
     * <li>An R2 dither value (as used by {@link #reduceRobertsEdit(Pixmap)}) is used for each pixel, but the four
     * corners of the 2x2 square each use a different angle for the artifacts.</li>
     * <li>Blue noise from three noise textures gets incorporated into two corners, with different strength.</li>
     * <li>XOR-Mod patterns are incorporated when blue noise isn't. These are:
     * <ul>
     *     <li>{@code ((px ^ y) % 9 - 4)}</li>
     *     <li>{@code ((px ^ y) % 11 - 5)}</li>
     * </ul>
     * </li>
     * </ul>
     * <br>
     * This is called Overboard because it is probably going overboard with the different types of extra error. Just
     * Burkes dither on its own is probably good enough. The results can look quite good, though, especially when
     * dither strength is higher than 1.0 (for other dithers, such as WREN, higher strength creates artifacts). This
     * tends to look a little smoother than WREN, and introduces less of a "rough canvas texture" to the image.
     *
     * @param pixmap will be modified in-place and returned
     * @return pixmap, after modifications
     */
    public Pixmap reduceOverboard2(Pixmap pixmap) {
        boolean hasTransparent = (paletteArray[0] == 0);
        final int lineLen = pixmap.getWidth(), h = pixmap.getHeight();
        float r4, r2, r1, g4, g2, g1, b4, b2, b1;
        final float strength = (float) (ditherStrength * 0.5 * (populationBias * populationBias)),
                noiseStrength = (float) (2.0 / (populationBias)),
                limit = 5f + 125f / (float)Math.sqrt(colorCount+1.5f);

        float[] curErrorRed, nextErrorRed, curErrorGreen, nextErrorGreen, curErrorBlue, nextErrorBlue;
        if (curErrorRedFloats == null) {
            curErrorRed = (curErrorRedFloats = new FloatArray(lineLen)).items;
            nextErrorRed = (nextErrorRedFloats = new FloatArray(lineLen)).items;
            curErrorGreen = (curErrorGreenFloats = new FloatArray(lineLen)).items;
            nextErrorGreen = (nextErrorGreenFloats = new FloatArray(lineLen)).items;
            curErrorBlue = (curErrorBlueFloats = new FloatArray(lineLen)).items;
            nextErrorBlue = (nextErrorBlueFloats = new FloatArray(lineLen)).items;
        } else {
            curErrorRed = curErrorRedFloats.ensureCapacity(lineLen);
            nextErrorRed = nextErrorRedFloats.ensureCapacity(lineLen);
            curErrorGreen = curErrorGreenFloats.ensureCapacity(lineLen);
            nextErrorGreen = nextErrorGreenFloats.ensureCapacity(lineLen);
            curErrorBlue = curErrorBlueFloats.ensureCapacity(lineLen);
            nextErrorBlue = nextErrorBlueFloats.ensureCapacity(lineLen);
            for (int i = 0; i < lineLen; i++) {
                nextErrorRed[i] = 0;
                nextErrorGreen[i] = 0;
                nextErrorBlue[i] = 0;
            }
        }
        Pixmap.Blending blending = pixmap.getBlending();
        pixmap.setBlending(Pixmap.Blending.None);
        int color, used;
        float rdiff, gdiff, bdiff;
        byte paletteIndex;
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
                color = pixmap.getPixel(px, y);
                if ((color & 0x80) == 0 && hasTransparent)
                    pixmap.drawPixel(px, y, 0);
                else {
                    float er = 0f;
                    float eg = 0f;
                    float eb = 0f;
                    switch ((px << 1 & 2) | (y & 1)){
                        case 0:
                            er += ((px ^ y) % 9 - 4);
                            er += ((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 41) * 0x1p-20f;
                            eg += (BlueNoise.TILE_TRI_NOISE[1][(px & 63) | (y & 63) << 6] + 0.5f) * 0x1p-5f;
                            eg += ((px * -0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 41) * 0x1p-20f;
                            eb += (BlueNoise.TILE_TRI_NOISE[2][(px & 63) | (y & 63) << 6] + 0.5f) * 0x1p-6f;
                            eb += ((y * 0xC13FA9A902A6328FL + px * -0x91E10DA5C79E7B1DL) >> 41) * 0x1.8p-20f;
                            break;
                        case 1:
                            er += (BlueNoise.TILE_TRI_NOISE[0][(px & 63) | (y & 63) << 6] + 0.5f) * 0x1p-5f;
                            er += ((px * -0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 41) * 0x1p-20f;
                            eg += (BlueNoise.TILE_TRI_NOISE[1][(px & 63) | (y & 63) << 6] + 0.5f) * 0x1p-6f;
                            eg += ((y * 0xC13FA9A902A6328FL + px * -0x91E10DA5C79E7B1DL) >> 41) * 0x1.8p-20f;
                            eb += ((px ^ y) % 11 - 5);
                            eb += ((y * -0xC13FA9A902A6328FL + px * -0x91E10DA5C79E7B1DL) >> 41) * 0x1.8p-21f;
                            break;
                        case 2:
                            er += (BlueNoise.TILE_TRI_NOISE[0][(px & 63) | (y & 63) << 6] + 0.5f) * 0x1p-6f;
                            er += ((y * 0xC13FA9A902A6328FL + px * -0x91E10DA5C79E7B1DL) >> 41) * 0x1.8p-20f;
                            eg += ((px ^ y) % 11 - 5);
                            eg += ((y * -0xC13FA9A902A6328FL + px * -0x91E10DA5C79E7B1DL) >> 41) * 0x1.8p-21f;
                            eb += ((px ^ y) % 9 - 4);
                            eb += ((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 41) * 0x1p-20f;
                            break;
                        default: // case 3:
                            er += ((px ^ y) % 11 - 5);
                            er += ((y * -0xC13FA9A902A6328FL + px * -0x91E10DA5C79E7B1DL) >> 41) * 0x1.8p-21f;
                            eg += ((px ^ y) % 9 - 4);
                            eg += ((px * 0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 41) * 0x1p-20f;
                            eb += (BlueNoise.TILE_TRI_NOISE[2][(px & 63) | (y & 63) << 6] + 0.5f) * 0x1p-5f;
                            eb += ((px * -0xC13FA9A902A6328FL + y * 0x91E10DA5C79E7B1DL) >> 41) * 0x1p-20f;
                            break;
                    }
                    er = er * noiseStrength + curErrorRed[px];
                    eg = eg * noiseStrength + curErrorGreen[px];
                    eb = eb * noiseStrength + curErrorBlue[px];
                    int rr = Math.min(Math.max((int)(((color >>> 24)       ) + Math.min(Math.max(er, -limit), limit) + 0.5f), 0), 0xFF);
                    int gg = Math.min(Math.max((int)(((color >>> 16) & 0xFF) + Math.min(Math.max(eg, -limit), limit) + 0.5f), 0), 0xFF);
                    int bb = Math.min(Math.max((int)(((color >>> 8)  & 0xFF) + Math.min(Math.max(eb, -limit), limit) + 0.5f), 0), 0xFF);
                    paletteIndex =
                            paletteMapping[((rr << 7) & 0x7C00)
                                           | ((gg << 2) & 0x3E0)
                                           | ((bb >>> 3))];
                    used = paletteArray[paletteIndex & 0xFF];
                    pixmap.drawPixel(px, y, used);
                    rdiff = ((color>>>24)-    (used>>>24)    ) * strength;
                    gdiff = ((color>>>16&255)-(used>>>16&255)) * strength;
                    bdiff = ((color>>>8&255)- (used>>>8&255) ) * strength;
                    r1 = rdiff * 16f / (45f + Math.abs(rdiff));
                    g1 = gdiff * 16f / (45f + Math.abs(gdiff));
                    b1 = bdiff * 16f / (45f + Math.abs(bdiff));
//                    r1 = rdiff * 16f / (float)Math.sqrt(2048f + rdiff * rdiff);
//                    g1 = gdiff * 16f / (float)Math.sqrt(2048f + gdiff * gdiff);
//                    b1 = bdiff * 16f / (float)Math.sqrt(2048f + bdiff * bdiff);
                    r2 = r1 + r1;
                    g2 = g1 + g1;
                    b2 = b1 + b1;
                    r4 = r2 + r2;
                    g4 = g2 + g2;
                    b4 = b2 + b2;
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
