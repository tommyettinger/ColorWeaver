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
public class PalettizerOneDither extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Initiate! Palette! Reduction!");
        config.setWindowedMode(640, 320);
        config.setIdleFPS(10);
        config.setResizable(true);
        new Lwjgl3Application(new PalettizerOneDither(), config);
    }
    
    private int[] PALETTE;
//    private final double[][] lab15 = CIELABConverter.makeLAB15();
//    private final PaletteReducer.ColorMetric cm = new PaletteReducer.ColorMetric(){
//        @Override
//        public double difference(int color1, int color2) {
//            if(((color1 ^ color2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
//            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, color2 >>> 24, color2 >>> 16 & 0xFF, color2 >>> 8 & 0xFF);
//        }
//
//        @Override
//        public double difference(int color1, int r2, int g2, int b2) {
//            if((color1 & 0x80) == 0) return Double.POSITIVE_INFINITY;
//            return difference(color1 >>> 24, color1 >>> 16 & 0xFF, color1 >>> 8 & 0xFF, r2, g2, b2);
//        }
//
//        @Override
//        public double difference(int r1, int g1, int b1, int r2, int g2, int b2) {
//            int indexA = (r1 << 7 & 0x7C00) | (g1 << 2 & 0x3E0) | (b1 >>> 3),
//                indexB = (r2 << 7 & 0x7C00) | (g2 << 2 & 0x3E0) | (b2 >>> 3);
//            final double
//                L = lab15[0][indexA] - lab15[0][indexB],
//                A = lab15[1][indexA] - lab15[1][indexB],
//                B = lab15[2][indexA] - lab15[2][indexB];
//            return L * L * 11.0 + A * A * 1.6 + B * B;
//        }
//    };
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
//        final String targetDir = "samples/reducedGood/"; //
//        final String targetDir = "samples/LowColorGoodDithers/"; //
//        final String targetDir = "samples/reducedRgbTricky/"; //
//        final String targetDir = "samples/reducedRgbSlippery/"; //
//        final String targetDir = "samples/reducedOkReadjusted/"; //

//        final String targetDir = "samples/reducedRgbStupider/"; //
//        final String targetDir = "samples/reducedEmpty"+HexGenerator.SPACE+"/"; //
//        final String targetDir = "samples/reducedDiffusion"+HexGenerator.SPACE+"/"; //
        final String targetDir = "samples/reducedExperiment"+HexGenerator.SPACE+"/"; //
//        final String targetDir = "samples/reducedOkOnce/"; //
//        final String targetDir = "samples/reducedRgbSqrt/"; //

//        final String targetDir = "samples/reduced"+HexGenerator.SPACE+"/"; //

        FileHandle[] hexes = Gdx.files.local("palettes/hex/").list(".hex");
//        FileHandle[] samples = {Gdx.files.local("samples/Mona_Lisa.jpg")
//                , Gdx.files.local("samples/Painting_by_Henri_Biva.jpg")
//                , Gdx.files.local("samples/Among_the_Sierra_Nevada_by_Albert_Bierstadt.jpg")
//                , Gdx.files.local("samples/Girl_with_a_Pearl_Earring.jpg")
//        };

//        FileHandle[] samples = {
//                Gdx.files.local("samples/Rome-Buildings.jpg"),
//                Gdx.files.local("samples/Rome-Seagull.jpg"),
//        };

//        FileHandle[] samples = {Gdx.files.local("samples/Pepper.png")};
//        FileHandle[] samples = {Gdx.files.local("samples/Watching.png")};
//        FileHandle[] samples = {Gdx.files.local("samples/Rooster.png")};
//        FileHandle[] samples = {Gdx.files.local("samples/Mona_Lisa.jpg")};
//        FileHandle[] samples = {Gdx.files.local("samples/Girl_with_a_Pearl_Earring.jpg")};
//        FileHandle[] samples = {Gdx.files.local("samples/Cat_Posing.jpg")};
//        FileHandle[] samples = {Gdx.files.local("samples/ignored/Vandalism.jpg")};
//        FileHandle[] samples = {Gdx.files.local("samples/ignored/Big_Pepper.png")};
//        FileHandle[] samples = {Gdx.files.local("samples/Judgment_Cat.jpg"), Gdx.files.local("samples/Purrito.jpg")};
//        FileHandle[] samples = {
//                Gdx.files.local("samples/GoStones.png"),
//                Gdx.files.local("samples/GoChips.png"),
//        };

        //// USE THIS TO RENDER ALL SAMPLES
        FileHandle[] samples = Gdx.files.local("samples/").list(pathname -> !pathname.isDirectory());

        PNG8 png8 = new PNG8();
        png8.setCompression(2);
        png8.setFlipY(false);
        long startTime = System.currentTimeMillis();
        PaletteReducer reducer = new PaletteReducer();

        // do everything
//        for(FileHandle hex : hexes) {

        // just do the one in HexGenerator
//        FileHandle hex = Gdx.files.local("palettes/hex/"+HexGenerator.NAME+".hex");{

        // the default
        for(FileHandle hex : new FileHandle[]{
                Gdx.files.local("palettes/hex/bw-2.hex"),
                Gdx.files.local("palettes/hex/dawnbringer-8.hex"),
                Gdx.files.local("palettes/hex/dawnbringer-16.hex"),
                Gdx.files.local("palettes/hex/dawnbringer-32.hex"),
                Gdx.files.local("palettes/hex/db-aurora-255.hex"),
                Gdx.files.local("palettes/hex/gb-4.hex"),
                Gdx.files.local("palettes/hex/prospecal-8.hex"),
                Gdx.files.local("palettes/hex/septembit23-6.hex"),
                Gdx.files.local("palettes/hex/gb-16.hex"),
                Gdx.files.local("palettes/hex/japanese-woodblock-12.hex"),
                Gdx.files.local("palettes/hex/azurestar-32.hex"),
                Gdx.files.local("palettes/hex/americana-4.hex"),
                Gdx.files.local("palettes/hex/ayy-4.hex"),
                Gdx.files.local("palettes/hex/gray-15.hex"),
                Gdx.files.local("palettes/hex/vinik-24.hex"),
                Gdx.files.local("palettes/hex/hyper-8.hex"),
                Gdx.files.local("palettes/hex/snuggly-15.hex"),
                Gdx.files.local("palettes/hex/snuggly-31.hex"),
                Gdx.files.local("palettes/hex/snuggly-63.hex"),
                Gdx.files.local("palettes/hex/snuggly-255.hex"),
                Gdx.files.local("palettes/hex/snorgly-15.hex"),
                Gdx.files.local("palettes/hex/snorgly-31.hex"),
                Gdx.files.local("palettes/hex/snorgly-63.hex"),
                Gdx.files.local("palettes/hex/snorgly-255.hex"),
                Gdx.files.local("palettes/hex/headpat-15.hex"),
                Gdx.files.local("palettes/hex/headpat-31.hex"),
                Gdx.files.local("palettes/hex/headpat-63.hex"),
                Gdx.files.local("palettes/hex/headpat-255.hex"),
                Gdx.files.local("palettes/hex/hug-15.hex"),
                Gdx.files.local("palettes/hex/hug-31.hex"),
                Gdx.files.local("palettes/hex/hug-63.hex"),
                Gdx.files.local("palettes/hex/hug-255.hex"),
        }) {

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/sm-septembit-4.hex"),
//        }) {

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/bw-2.hex"),
//                Gdx.files.local("palettes/hex/gray-16.hex"),
//                Gdx.files.local("palettes/hex/gray-15.hex"),
//                Gdx.files.local("palettes/hex/grayfull-256.hex"),
//        }) {

            String name = hex.nameWithoutExtension().toLowerCase(), suffix = "_" + name;

            System.out.println(name);
            loadPalette(name);

            Gdx.files.local(targetDir).mkdirs();
            reducer.exact(PALETTE, HexGenerator.METRIC);
            png8.palette = reducer;
            try {
                for(FileHandle sample : samples) {

                    Pixmap pm, sam = new Pixmap(sample);
                    pm = new Pixmap(sam.getWidth(), sam.getHeight(), sam.getFormat());
                    reducer.setDitherStrength(1f);
//                    String subname = targetDir + "/" + sample.nameWithoutExtension();
                    String subname = targetDir + name + "/" + sample.nameWithoutExtension();

                    reducer.setDitherStrength(1f);
                    drawPart(pm, sam, reducer, png8, subname, suffix);

                    reducer.setDitherStrength(0.5f);
                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_half";

                    drawPart(pm, sam, reducer, png8, subname, suffix);

                    reducer.setDitherStrength(1.5f);
                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_bonus";

                    drawPart(pm, sam, reducer, png8, subname, suffix);

                    reducer.setDitherStrength(2f);
                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_heavy";

                    drawPart(pm, sam, reducer, png8, subname, suffix);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Took " + (System.currentTimeMillis() - startTime) + " ms");

        Gdx.app.exit();
    }

    private void drawPart(Pixmap pm, Pixmap sam, PaletteReducer reducer, PNG8 png8, String subname, String suffix) throws IOException {
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceSchmidt(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Kufic" + suffix + ".png"), pm, PALETTE, false, 0);
    }
}
