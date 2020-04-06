package colorweaver;

import colorweaver.tools.StringKit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.IOException;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class AutomaticPaletteTransformer extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("TRANSFOOOORM!");
        config.setWindowedMode(640, 320);
        config.setIdleFPS(10);
        config.setResizable(true);
        new Lwjgl3Application(new AutomaticPaletteTransformer(), config);
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
    
    private int[] PALETTE;
    private final double[][] lab15 = CIELABConverter.makeLAB15();
    private final PaletteReducer.ColorMetric cm = new PaletteReducer.ColorMetric(){
        @Override
        public double difference(int color1, int color2) {
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
        }

        @Override
        public double difference(int color1, int r2, int g2, int b2) {
            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
        }

        @Override
        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
            int indexA = (r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3),
                indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
            final double
                L = lab15[0][indexA] - lab15[0][indexB],
                A = lab15[1][indexA] - lab15[1][indexB],
                B = lab15[2][indexA] - lab15[2][indexB];
            return L * L * 11.0 + A * A * 1.6 + B * B;
        }
    };
    public void loadPalette(String name) {
        try {
            String text = Gdx.files.local("palettes/hex/" + name + ".hex").readString();
            int start = 0, end = 6, len = text.length();
            int gap = (text.charAt(7) == '\n') ? 8 : 7;
            int sz = ((len + 2) / gap);
            PALETTE = new int[sz + 1];
            for (int i = 1; i <= sz; i++) {
                PALETTE[i] = StringKit.intFromHex(text, start, end) << 8 | 0xFF;
                start += gap;
                end += gap;
            }
        } catch (GdxRuntimeException e) {
            e.printStackTrace();
        }
    }

    public void create() {
        FileHandle[] hexes = Gdx.files.local("palettes/hex/").list(".hex");
        Gdx.files.local("palettes/gen/").mkdirs();
        Gdx.files.local("palettes/gen/hex/").mkdirs();
//        for(FileHandle hex : hexes) {
        FileHandle hex = Gdx.files.local("palettes/hex/gb-16.hex");{
            String name = hex.nameWithoutExtension().toLowerCase();
            loadPalette(name);
            StringBuilder sb = new StringBuilder((1 + 12 * 8) * (PALETTE.length + 7 >>> 3));
            for (int i = 0; i < (PALETTE.length + 7 >>> 3); i++) {
                for (int j = 0; j < 8 && (i << 3 | j) < PALETTE.length; j++) {
                    sb.append("0x").append(StringKit.hex(PALETTE[i << 3 | j]).toUpperCase()).append(", ");
                }
                sb.append('\n');
            }
            Gdx.files.local("palettes/gen/hex/" + name + ".txt").writeString(sb.toString(), false);
            sb.setLength(0);

            PNG8 png8 = new PNG8();
            png8.palette = new PaletteReducer(PALETTE, cm);
            Pixmap pix = new Pixmap(256, 1, Pixmap.Format.RGBA8888);
            for (int i = 1; i < PALETTE.length; i++) {
                pix.drawPixel(i - 1, 0, PALETTE[i]);
            }
            pix.drawPixel(255, 0, 0);
            try {
                png8.writePrecisely(Gdx.files.local("palettes/gen/" + name + ".png"), pix, false);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Pixmap p2 = new Pixmap(1024, 32, Pixmap.Format.RGBA8888);
            for (int r = 0; r < 32; r++) {
                for (int b = 0; b < 32; b++) {
                    for (int g = 0; g < 32; g++) {
                        p2.drawPixel(r << 5 | b, g, PALETTE[png8.palette.paletteMapping[((r << 10) & 0x7C00) | ((g << 5) & 0x3E0) | b] & 0xFF]);
                    }
                }
            }
            try {
                png8.writePrecisely(Gdx.files.local("palettes/gen/" + name + "_GLSL.png"), p2, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Gdx.app.exit();
    }
}
