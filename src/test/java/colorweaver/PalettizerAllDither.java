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
public class PalettizerAllDither extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Initiate! Palette! Reduction!");
        config.setWindowedMode(640, 320);
        config.setIdleFPS(10);
        config.setResizable(true);
        new Lwjgl3Application(new PalettizerAllDither(), config);
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

        // auto-generated Snorgly palettes
        FileHandle[] snorglies = new FileHandle[6];
        for (int i = 8, idx = 0; i <= 256; i <<= 1) {
            snorglies[idx++] = Gdx.files.local("snorgly-"+(i-1)+".hex");
        }
        for(FileHandle hex : snorglies) {

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/sm-septembit-4.hex"),
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
                    String subname = targetDir + name + "/" + sample.nameWithoutExtension();
////lousy but important
                    pm.drawPixmap(sam, 0, 0);
                    pm = reducer.reduceSolid(pm);
                    png8.writePrecisely(Gdx.files.local(subname + "_Solid" + suffix + ".png"), pm, PALETTE, false, 0);

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

//                    reducer.setDitherStrength(0.25f);
//                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_quarter";
//
//                    drawPart(pm, sam, reducer, png8, subname, suffix);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Took " + (System.currentTimeMillis() - startTime) + " ms");

        Gdx.app.exit();
    }

    private void drawPart(Pixmap pm, Pixmap sam, PaletteReducer reducer, PNG8 png8, String subname, String suffix) throws IOException {

        // main block

//////////very good
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceIGN(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_IGN" + suffix + ".png"), pm, PALETTE, false, 0);
//////////good enough
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceSierraLite(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_SierraLite" + suffix + ".png"), pm, PALETTE, false, 0);
//////////rather good
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceFloydSteinberg(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg" + suffix + ".png"), pm, PALETTE, false, 0);
//////ok
//        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceTrueBlue3(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_BlueNewer3" + suffix + ".png"), pm, PALETTE, false, 0);
//////great
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceBluish(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Neuter" + suffix + ".png"), pm, PALETTE, false, 0);
////////very good
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceScatter(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Scatter" + suffix + ".png"), pm, PALETTE, false, 0);
////////YAY YIPPEE WOO NO BANDING
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceNeue(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Neue" + suffix + ".png"), pm, PALETTE, false, 0);
////////////incredible
        //Took 106929 ms
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceKnoll(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Knoll" + suffix + ".png"), pm, PALETTE, false, 0);
////////////
        //Took 98748 ms
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceKnoll2(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Knoll2" + suffix + ".png"), pm, PALETTE, false, 0);
//////////??? error diffusion with IGN
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceIgneous(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Igneous2" + suffix + ".png"), pm, PALETTE, false, 0);
//////////very good, error-diffusion, per-channel color
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceWeave(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Weave2" + suffix + ".png"), pm, PALETTE, false, 0);
//////////fairly good, low banding, some other artifacts, per-channel color
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceRobertsEdit(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Roberts14" + suffix + ".png"), pm, PALETTE, false, 0);
////very good!
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceDodgy(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Dodgy4" + suffix + ".png"), pm, PALETTE, false, 0);
//////retro, doesn't have to be classically good
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceLoaf(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Loaf" + suffix + ".png"), pm, PALETTE, false, 0);
////retro, doesn't have to be classically good
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceLoaf2(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Loaf2" + suffix + ".png"), pm, PALETTE, false, 0);
//////retro, doesn't have to be classically good
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceLoaf3(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Loaf3" + suffix + ".png"), pm, PALETTE, false, 0);
////retro, doesn't have to be classically good
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceLeaf(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Leaf" + suffix + ".png"), pm, PALETTE, false, 0);
////retro, doesn't have to be classically good
        // 27994 ms for just this
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceGourd(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Gourd" + suffix + ".png"), pm, PALETTE, false, 0);
////experimenting with LUTs to allow proper gamma correction
        // 26523 ms for just this
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceGourdLUT(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_GourdLUT" + suffix + ".png"), pm, PALETTE, false, 0);
////experimenting to see what this looks like without gamma correction
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceGourdNoGamma(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_GourdNoGamma" + suffix + ".png"), pm, PALETTE, false, 0);
//////stylistic, not a traditional dither
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceSchmidt(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Kufic" + suffix + ".png"), pm, PALETTE, false, 0);
////yay!
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceOverboard2(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Overboard3" + suffix + ".png"), pm, PALETTE, false, 0);
//////I'm in love
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceWean(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Wean" + suffix + ".png"), pm, PALETTE, false, 0);
////////more love!
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceBlubber(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Blubber" + suffix + ".png"), pm, PALETTE, false, 0);
/////////has some issues
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceBurkes(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Burkes" + suffix + ".png"), pm, PALETTE, false, 0);
/////////great, especially for error diffusion
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceBurkes0(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Burkes0" + suffix + ".png"), pm, PALETTE, false, 0);
////////even better!
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceBurkes2(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Burkes2" + suffix + ".png"), pm, PALETTE, false, 0);
/////great!
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceOceanic(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Oceanic" + suffix + ".png"), pm, PALETTE, false, 0);
///// basically the same as Oceanic, but without the Burkes constants
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceSeaside(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Seaside" + suffix + ".png"), pm, PALETTE, false, 0);
//// oceanic meets pattern dither
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceCoastal(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Coastal" + suffix + ".png"), pm, PALETTE, false, 0);
//// nice and griddy, for things that can use that
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reducePatternish(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Patternish" + suffix + ".png"), pm, PALETTE, false, 0);

////////better?
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceTrueBlue4(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_BlueNewer4" + suffix + ".png"), pm, PALETTE, false, 0);
//////////better?
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceTrueBlue5(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_BlueNewer5" + suffix + ".png"), pm, PALETTE, false, 0);
//////pretty bad
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceChaoticNoise(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Chaotic" + suffix + ".png"), pm, PALETTE, false, 0);
/////////meh...
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceCyanic(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Cyanic" + suffix + ".png"), pm, PALETTE, false, 0);
////////???
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceShuffle(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Shuffle" + suffix + ".png"), pm, PALETTE, false, 0);
////////not ideal...
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceKnollRoberts(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_KR_G" + suffix + ".png"), pm, PALETTE, false, 0);
//////meh
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceKnollBlue(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_KB2" + suffix + ".png"), pm, PALETTE, false, 0);
////???
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reducePlexus(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Plexus" + suffix + ".png"), pm, PALETTE, false, 0);
////////BAD
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceRobertsLAB(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_RobertsLAB" + suffix + ".png"), pm, PALETTE, false, 0);
////???
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceRing(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Ring2" + suffix + ".png"), pm, PALETTE, false, 0);
//////???
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlob(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Blob" + suffix + ".png"), pm, PALETTE, false, 0);
/////// really bad.
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceCuatro(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Cuatro" + suffix + ".png"), pm, PALETTE, false, 0);
//////hot rat-infested garbage
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceGarbage(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Garbage.png"), pm, PALETTE, false, 0);
    }
}
