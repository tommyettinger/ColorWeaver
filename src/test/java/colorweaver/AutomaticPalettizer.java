package colorweaver;

import colorweaver.tools.StringKit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class AutomaticPalettizer extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Initiate! Palette! Reduction!");
        config.setWindowedMode(640, 320);
        config.setIdleFPS(10);
        config.setResizable(true);
        new Lwjgl3Application(new AutomaticPalettizer(), config);
    }
    
    private int[] PALETTE;
    private final double[][] lab15 = CIELABConverter.makeLAB15();
    private final PaletteReducer.ColorMetric cm = new PaletteReducer.ColorMetric(){
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
        FileHandle[] samples = Gdx.files.local("samples/").list(new FileFilter() {
            @Override
            public boolean accept (File pathname) {
                return !pathname.isDirectory();
            }
        });
        Gdx.files.local("samples/reduced/").mkdirs();
        for(FileHandle hex : hexes) {
//        FileHandle hex = Gdx.files.local("palettes/hex/bw-2.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/gb-4.hex");{
            String name = hex.nameWithoutExtension().toLowerCase(), suffix = '_' + name;
            loadPalette(name);
            Gdx.files.local("samples/reduced/" + name).mkdirs();
            PNG8 png8 = new PNG8();
            png8.setFlipY(false);
            PaletteReducer reducer = new PaletteReducer(PALETTE, cm);
            png8.palette = reducer;
            reducer.setDitherStrength(0.5f);
            try {
                Pixmap pm;
//                FileHandle sample = Gdx.files.local("samples/Portal_Companion_Cube.jpg"); {
                for(FileHandle sample : samples) {
                    String subname = "samples/reduced/" + name + "/" + sample.nameWithoutExtension() + "_half";
//            pm = (reducer.reduceWithNoise(new Pixmap(sample)));
//            png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinbergHu"+suffix+".png"), pm, false);

//            pm = reducer.reduceBurkes(new Pixmap(sample));
//            png8.writePrecisely(Gdx.files.local(subname + "_Burkes"+suffix+".png"), pm, false);
////good enough
                    pm = (reducer.reduceSierraLite(new Pixmap(sample)));
                    png8.writePrecisely(Gdx.files.local(subname + "_SierraLite" + suffix + ".png"), pm, PALETTE, false, 0);
////good
//                    pm = reducer.reduceSolid(new Pixmap(sample));
//                    png8.writePrecisely(Gdx.files.local(subname + "_Solid" + suffix + ".png"), pm, PALETTE, false, 0);

//            pm = reducer.reduceWithRoberts(new Pixmap(sample));
//            png8.writePrecisely(Gdx.files.local(subname + "_Roberts"+suffix+".png"), pm, false);

//            pm = reducer.reduceRobertsMul(new Pixmap(sample));
//            png8.writePrecisely(Gdx.files.local(subname + "_RobertsMul"+suffix+".png"), pm, false);

//            pm = reducer.reduceRobertsEdit(new Pixmap(sample));
//            png8.writePrecisely(Gdx.files.local(subname + "_RobertsEdit"+suffix+".png"), pm, false);
////good
                    pm = reducer.reduceShaderMimic(new Pixmap(sample));
                    png8.writePrecisely(Gdx.files.local(subname + "_ShaderMimic" + suffix + ".png"), pm, PALETTE, false, 0);
////good
                    pm = (reducer.reduceFloydSteinberg(new Pixmap(sample)));
                    png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg" + suffix + ".png"), pm, PALETTE, false, 0);
////good?
                    pm = (reducer.reduceTrueBlue(new Pixmap(sample)));
                    png8.writePrecisely(Gdx.files.local(subname + "_Blue" + suffix + ".png"), pm, PALETTE, false, 0);
////good?
                    pm = (reducer.reduceChosenBlue(new Pixmap(sample)));
                    png8.writePrecisely(Gdx.files.local(subname + "_Bluish" + suffix + ".png"), pm, PALETTE, false, 0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Gdx.app.exit();
    }
}
