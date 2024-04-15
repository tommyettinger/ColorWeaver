package colorweaver;

import colorweaver.tools.StringKit;
import colorweaver.tools.TrigTools;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.github.tommyettinger.digital.AlternateRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import static colorweaver.PaletteReducer.forwardLight;
import static colorweaver.PaletteReducer.shrink;
import static colorweaver.tools.TrigTools.cos_;
import static colorweaver.tools.TrigTools.sin_;

public class PaletteExpander extends ApplicationAdapter {
    public int[] palette;
    public static final long SEED = 1L;
    public static final int LIMIT = 1023;
    public static final String NAME = "bigdawn";
    static final IntArray RGBA = new IntArray(LIMIT+1);

    static final int[] DB8 = new int[]{
            0x000000FF, 0x55415FFF, 0x646964FF, 0xD77355FF, 0x508CD7FF, 0x64B964FF, 0xE6C86EFF, 0xDCF5FFFF};

    static final int[] PROSPECAL = new int[]{
            0x6DB5BAFF, 0x26544CFF, 0x76AA3AFF, 0xFBFDBEFF, 0xD23C4FFF, 0x2B1328FF, 0x753D38FF, 0xEFAD5FFF};

    static final int[] HYPER8 = new int[]{
            0x000000FF, 0xFFFFFFFF, 0xFF0000FF, 0x00FF00FF, 0x0000FFFF, 0x00FFFFFF, 0xFF00FFFF, 0xFFFF00FF};

    static final int[] JAPANESE_WOODBLOCK = new int[]{
            0x2B2821FF,
            0x624C3CFF,
            0xD9AC8BFF,
            0xE3CFB4FF,
            0x243D5CFF,
            0x5D7275FF,
            0x5C8B93FF,
            0xB1A58DFF,
            0xB03A48FF,
            0xD4804DFF,
            0xE0C872FF,
            0x3E6958FF,
    };

    public static Vector3 fromRGBA8888(Vector3 filling, int rgba) {
        double r = ((rgba >>> 24) * 0x1.010101010101p-8);        r *= r;
        double g = ((rgba >>> 16 & 0xFF) * 0x1.010101010101p-8); g *= g;
        double b = ((rgba >>> 8 & 0xFF) * 0x1.010101010101p-8);  b *= b;
        double l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
        double m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
        double s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);
        filling.set(
                (float)Math.min(Math.max((forwardLight(0.2104542553f * l + 0.7936177850f * m - 0.0040720468f * s)), 0f), 1f),
                (float)Math.min(Math.max(((1.9779984951f * l - 2.4285922050f * m + 0.4505937099f * s)), -1f), 1f),
                (float)Math.min(Math.max(((0.0259040371f * l + 0.7827717662f * m - 0.8086757660f * s)), -1f), 1f)
        );
        return filling;
    }

    /**
     * Returns true if the given Oklab values are valid to convert losslessly back to RGBA.
     * @param L lightness channel, as a double from 0 to 1
     * @param A green-to-red chromatic channel, as a double from 0 to 1
     * @param B blue-to-yellow chromatic channel, as a double from 0 to 1
     * @return true if the given Oklab channels can be converted back and forth to RGBA
     */
    public static boolean inGamut(double L, double A, double B)
    {
        L = Math.pow(L, 2.0/3.0);

        double l = (L + +0.3963377774 * A + +0.2158037573 * B);
        l *= l * l;
        double m = (L + -0.1055613458 * A + -0.0638541728 * B);
        m *= m * m;
        double s = (L + -0.0894841775 * A + -1.2914855480 * B);
        s *= s * s;

//        final double r = +4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s;
//        if(r < -0x1p-8 || r > 0x101p-8) return false;
//        final double g = -1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s;
//        if(g < -0x1p-8 || g > 0x101p-8) return false;
//        final double b = -0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s;
//        return (b >= -0x1p-8 && b <= 0x101p-8);

        double dr = Math.sqrt(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s)*255f;
        final int r = (int)dr;
        if(Double.isNaN(dr) || r < 0 || r > 255)
            return false;
        double dg = Math.sqrt(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s)*255f;
        final int g = (int)dg;
        if(Double.isNaN(dg) || g < 0 || g > 255)
            return false;
        double db = Math.sqrt(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s)*255f;
        final int b = (int)db;
        if(!Double.isNaN(db) && b >= 0 && b <= 255)
            return true;
        return false;
    }


    public static Vector3 randomChangeIfValid(Vector3 filling, Vector3 original, float change, Random random){
        if(random == null)
            random = MathUtils.random;
        // set to random direction, using random
        float u = random.nextFloat();
        float v = random.nextFloat();
        float theta = MathUtils.PI2 * u;
        float phi = MathUtils.acos(v + v - 1f);
        filling.setFromSpherical(theta, phi);
        // scale the translation to match change, add it with original
        filling.scl(change).add(original);
        // if the modified color is in-gamut, return it
        if(inGamut(filling.x, filling.y, filling.z))
            return filling;
        return null;
    }

    private static float cube(final float x) {
        return x * x * x;
    }

    public static int toRGBA8888(final Vector3 oklab)
    {
        final float L = (float) Math.pow(oklab.x, 2.0/3.0);
        final float A = oklab.y;
        final float B = oklab.z;
        final float l = cube(L + 0.3963377774f * A + 0.2158037573f * B);
        final float m = cube(L - 0.1055613458f * A - 0.0638541728f * B);
        final float s = cube(L - 0.0894841775f * A - 1.2914855480f * B);
        final int r = (int)(Math.sqrt(Math.min(Math.max(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s, 0.0), 1.0)) * 255.9999);
        final int g = (int)(Math.sqrt(Math.min(Math.max(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s, 0.0), 1.0)) * 255.9999);
        final int b = (int)(Math.sqrt(Math.min(Math.max(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s, 0.0), 1.0)) * 255.9999);
        return r << 24 | g << 16 | b << 8 | 0xFF;
    }


    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(".hex Palette Generator/Expander");
        config.setWindowedMode(640, 480);
        config.setIdleFPS(10);
        config.useVsync(true);
        config.setResizable(false);
        new Lwjgl3Application(new PaletteExpander(), config);
//        AutomaticPaletteTransformer.main(arg);
//        AutomaticPalettizer.main(arg);
    }

    public void loadPalette(String name) {
        try {
            String text = Gdx.files.local("palettes/hex/" + name + ".hex").readString();
            int start = 0, end = 6, len = text.length();
            int gap = (text.charAt(7) == '\n') ? 8 : 7;
            int sz = ((len + 2) / gap);
            palette = new int[sz + 1];
            for (int i = 1; i <= sz; i++) {
                palette[i] = StringKit.intFromHex(text, start, end) << 8 | 0xFF;
                start += gap;
                end += gap;
            }
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void create() {
        loadPalette("db-aurora-255");
//        palette = JAPANESE_WOODBLOCK;
        HexGenerator.NAME = NAME + "-" + LIMIT;

        AlternateRandom random = new AlternateRandom(SEED);
        ArrayList<Vector3> vs = new ArrayList<>(LIMIT);
        HashSet<Integer> unique = new HashSet<>(1024);
        int[] base = palette;
//        unique.add(-1);
//        vs.add(fromRGBA8888(new Vector3(), base[0]));
        for (int i = 1; i < base.length; i++) {
            if(unique.add(shrink(base[i])))
                vs.add(fromRGBA8888(new Vector3(), base[i]));
        }
        final float threshold = 1f / LIMIT, lowThreshold = 1.5f / LIMIT, move = (float)Math.sqrt(1f/palette.length);

        ITERS:
        for (int iter = vs.size(); iter < LIMIT; iter++) {
            int triesLeft = 100;
            TRIALS:
            while (triesLeft-- > 0){
                int choice = random.nextInt(iter);
                Vector3 initial = vs.get(choice), next = new Vector3();
                if(randomChangeIfValid(next, initial, move, random) == null) continue;
                for (int i = 0; i < iter; i++) {
                    if(i == choice) continue;
                    Vector3 other = vs.get(i);
                    if(other.dst(next) < lowThreshold) continue TRIALS;
                }
                if(unique.add(shrink(toRGBA8888(next)))) {
                    vs.add(next);
                    continue ITERS;
                }
            }
            // failed to find a valid color in 100 tries
            System.err.println("OH NO, FAILED ON ITERATION " + iter);
            throw new RuntimeException("SAD FACE :(");
        }
        final float GRAY_LIMIT = 1.2f / LIMIT;
//        for (int i = 0; i < vs.size(); i++) {
//            Vector3 v = vs.get(i);
//            if(v.y * v.y + v.z * v.z <= GRAY_LIMIT){
//                System.out.println("BRINGING " + v + " TOWARD GRAYSCALE");
//                v.y *= 0.125f;
//                v.z *= 0.125f;
//            }
//        }
        Collections.sort(vs, (c1, c2) -> {
//                if (ColorTools.alphaInt(c1.value) < 128) return -10000;
//                else if (ColorTools.alphaInt(c2.value) < 128) return 10000;
            float s1 = (c1.y * c1.y + c1.z * c1.z), s2 = (c2.y * c2.y + c2.z * c2.z);

            if(s1 <= GRAY_LIMIT && s2 > GRAY_LIMIT)
                return -1000;
            else if(s1 > GRAY_LIMIT && s2 <= GRAY_LIMIT)
                return 1000;
            else if(s1 <= GRAY_LIMIT && s2 <= GRAY_LIMIT)
                return (int)Math.signum(c1.x - c2.x);
            else
                return 2 * (int)Math.signum(MathUtils.atan2Deg360(c1.z, c1.y) - MathUtils.atan2Deg360(c2.z, c2.y))
                        + (int)Math.signum(c1.x - c2.x);
        });

        unique.add(-1);
        RGBA.add(0);
        for(Vector3 v : vs) {
            RGBA.add(toRGBA8888(v));
        }

        palette = RGBA.toArray();

        ArrayList<Integer> mixingPalette = new ArrayList<>(256);
        for (int i = 0; i < palette.length; i++) {
            mixingPalette.add(palette[i]);
        }
        StringBuilder sb = new StringBuilder(mixingPalette.size() * 7);
        for (int i = 1; i < mixingPalette.size(); i++) {
            sb.append(String.format("%06x\n", mixingPalette.get(i) >>> 8));
        }
        Gdx.files.local("palettes/hex/"+ HexGenerator.NAME+".hex").writeString(sb.toString(), false);
        System.out.println("new int[] {");
        for (int i = 0; i < mixingPalette.size(); i++) {
            System.out.print("0x" + StringKit.hex(mixingPalette.get(i)) + ", ");
            if((i & 7) == 7)
                System.out.println();
        }
        System.out.println("};");
        System.out.println("Number of unique colors: " + unique.size());
        Gdx.app.exit();
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
