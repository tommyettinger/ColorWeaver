package colorweaver;

import colorweaver.a8.A8PNG;
import colorweaver.a8.A8PNG8;
import colorweaver.a8.A8PaletteReducer;
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
 * Created by Tommy Ettinger on 2/8/2025.
 */
public class OkCarefulPalettizerAll extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Initiate! Palette! Reduction!");
        config.setWindowedMode(640, 320);
        config.setIdleFPS(10);
        config.setResizable(true);
        new Lwjgl3Application(new OkCarefulPalettizerAll(), config);
    }
    
    private int[] PALETTE;

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
        final String targetDir = "samples/reducedOkCareful/"; //

//        FileHandle[] hexes = Gdx.files.local("palettes/hex/").list(".hex");
//        FileHandle[] samples = {Gdx.files.local("samples/Mona_Lisa.jpg")
//                , Gdx.files.local("samples/Painting_by_Henri_Biva.jpg")
//                , Gdx.files.local("samples/Among_the_Sierra_Nevada_by_Albert_Bierstadt.jpg")
//                , Gdx.files.local("samples/Girl_with_a_Pearl_Earring.jpg")
//        };

//        FileHandle[] samples = {
//                Gdx.files.local("samples/Rome-Buildings.jpg"),
//                Gdx.files.local("samples/Rome-Seagull.jpg"),
//        };

//        FileHandle[] samples = {Gdx.files.local("samples/other/crispy-pixels.png")};
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

        A8PNG8 a8png8 = new A8PNG8();
        a8png8.setCompression(2);
        a8png8.setFlipY(false);

        A8PNG a8png = new A8PNG();
        a8png.setFlipY(false);

        long startTime = System.currentTimeMillis();
        OkCarefulReducer reducer = new OkCarefulReducer();
//        reducer.analyze(new Pixmap(Gdx.files.local("samples/other/IsometricTRPG.png")));

        // do everything
//        for(FileHandle hex : hexes) {

        // just do the one in HexGenerator
//        FileHandle hex = Gdx.files.local("palettes/hex/"+HexGenerator.NAME+".hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/IsometricTRPG.hex");{

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
                Gdx.files.local("palettes/hex/huggly-15.hex"),
                Gdx.files.local("palettes/hex/huggly-31.hex"),
                Gdx.files.local("palettes/hex/huggly-63.hex"),
                Gdx.files.local("palettes/hex/huggly-255.hex"),
        }) {


        // auto-generated Huggly palettes
//        FileHandle[] hugglies = new FileHandle[6];
//        for (int i = 8, idx = 0; i <= 256; i <<= 1) {
//            hugglies[idx++] = Gdx.files.local("huggly-"+(i-1)+".hex");
//        }
//        for(FileHandle hex : hugglies) {


//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/sm-septembit-4.hex"),
//        }) {

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/meadowvale-70.hex"),
//                Gdx.files.local("palettes/hex/eighexplore-32.hex"),
//        }) {

            String name = hex.nameWithoutExtension().toLowerCase(), suffix = "_" + name;

            System.out.println(name);
            loadPalette(name);

            Gdx.files.local(targetDir).mkdirs();
            reducer.exact(PALETTE);
            a8png8.palette = reducer;
            try {
                for(FileHandle sample : samples) {

                    Pixmap pm, sam = new Pixmap(sample);
                    pm = new Pixmap(sam.getWidth(), sam.getHeight(), sam.getFormat());
                    reducer.setDitherStrength(1f);
                    String subname = targetDir + name + "/" + sample.nameWithoutExtension();
////lousy but important
                    pm.drawPixmap(sam, 0, 0);
                    pm = reducer.reduceSolid(pm);
                    a8png8.writePrecisely(Gdx.files.local(subname + "_Solid" + suffix + ".png"), pm, PALETTE, false, 0);

                    reducer.setDitherStrength(1f);
                    drawPart(pm, sam, reducer, a8png8, subname, suffix);


                    reducer.setDitherStrength(0.5f);
                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_half";

                    drawPart(pm, sam, reducer, a8png8, subname, suffix);

                    reducer.setDitherStrength(1.5f);
                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_bonus";

                    drawPart(pm, sam, reducer, a8png8, subname, suffix);

                    reducer.setDitherStrength(2f);
                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_heavy";

                    drawPart(pm, sam, reducer, a8png8, subname, suffix);

//                    reducer.setDitherStrength(0.25f);
//                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_quarter";
//
//                    drawPart(pm, sam, reducer, a8png8, subname, suffix);

                }
//                for(FileHandle sample : samples) {
//
//                    Pixmap pm, sam = new Pixmap(sample);
//                    pm = new Pixmap(sam.getWidth(), sam.getHeight(), sam.getFormat());
//                    reducer.setDitherStrength(1f);
//                    String subname = targetDir + name + "/" + sample.nameWithoutExtension();
//////lousy but important
////                    pm.drawPixmap(sam, 0, 0);
////                    pm = reducer.reduceSolid(pm);
////                    a8png.write(Gdx.files.local(subname + "_Solid" + suffix + ".png"), pm);
//
//                    reducer.setDitherStrength(1f);
//                    drawPartFullColor(pm, sam, reducer, a8png, subname, suffix);
//
//
//                    reducer.setDitherStrength(0.5f);
//                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_half";
//
//                    drawPartFullColor(pm, sam, reducer, a8png, subname, suffix);
//
//                    reducer.setDitherStrength(1.5f);
//                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_bonus";
//
//                    drawPartFullColor(pm, sam, reducer, a8png, subname, suffix);
//
//                    reducer.setDitherStrength(2f);
//                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_heavy";
//
//                    drawPartFullColor(pm, sam, reducer, a8png, subname, suffix);
//
////                    reducer.setDitherStrength(0.25f);
////                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_quarter";
////
////                    drawPart(pm, sam, reducer, a8png8, subname, suffix);
//
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Took " + (System.currentTimeMillis() - startTime) + " ms");

        Gdx.app.exit();
    }

    private void drawPart(Pixmap pm, Pixmap sam, A8PaletteReducer reducer, A8PNG8 a8png8, String subname, String suffix) throws IOException {

        // main block

////////////very good
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceJimenez(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_GradientNoise" + suffix + ".png"), pm, PALETTE, false, 0);
////////////good enough
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceSierraLite(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_SierraLite" + suffix + ".png"), pm, PALETTE, false, 0);
////////////rather good
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg" + suffix + ".png"), pm, PALETTE, false, 0);
////////improved from before! looks pretty good, ordered.
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoise(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoise" + suffix + ".png"), pm, PALETTE, false, 0);
////////definitely good, close to BlueNoise; ordered.
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueCrab(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueCrab" + suffix + ".png"), pm, PALETTE, false, 0);
////////very close to crab now...
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNit(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNit" + suffix + ".png"), pm, PALETTE, false, 0);
////////quite nice!
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueOct(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueOct" + suffix + ".png"), pm, PALETTE, false, 0);
////////great
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBluish(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Neuter" + suffix + ".png"), pm, PALETTE, false, 0);
//////////very good
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceScatter(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Scatter" + suffix + ".png"), pm, PALETTE, false, 0);
//////////YAY YIPPEE WOO NO BANDING
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceNeue(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Neue" + suffix + ".png"), pm, PALETTE, false, 0);
//////////////incredible
//        //Took 106929 ms
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceKnoll(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Pattern" + suffix + ".png"), pm, PALETTE, false, 0);
////////////////
////        //Took 98748 ms
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceKnoll2(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Knoll2" + suffix + ".png"), pm, PALETTE, false, 0);
////////////??? error diffusion with IGN
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceIgneous(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Igneous" + suffix + ".png"), pm, PALETTE, false, 0);
////////////very good, error-diffusion, per-channel color
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceWoven(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Woven" + suffix + ".png"), pm, PALETTE, false, 0);
////////////fairly good, low banding, some other artifacts, per-channel color
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceRoberts(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Roberts" + suffix + ".png"), pm, PALETTE, false, 0);
////////very good!
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceDodgy(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Dodgy" + suffix + ".png"), pm, PALETTE, false, 0);
////////retro, doesn't have to be classically good
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceLoaf(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Loaf" + suffix + ".png"), pm, PALETTE, false, 0);
////////retro, doesn't have to be classically good
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceLoaf2(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Loaf2" + suffix + ".png"), pm, PALETTE, false, 0);
//////////retro, doesn't have to be classically good
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceLoaf3(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Loaf3" + suffix + ".png"), pm, PALETTE, false, 0);
////////retro, doesn't have to be classically good, but this sucks
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceLeaf(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Leaf" + suffix + ".png"), pm, PALETTE, false, 0);
//////retro, doesn't have to be classically good
//        // 27994 ms for just this
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceGourd(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Gourd" + suffix + ".png"), pm, PALETTE, false, 0);
////////experimenting with LUTs to allow proper gamma correction
////        // 26523 ms for just this
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceGourdLUT(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_GourdLUT" + suffix + ".png"), pm, PALETTE, false, 0);
////////experimenting to see what this looks like without gamma correction
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceGourdNoGamma(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_GourdNoGamma" + suffix + ".png"), pm, PALETTE, false, 0);
////////stylistic, not a traditional dither
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceSchmidt(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Kufic" + suffix + ".png"), pm, PALETTE, false, 0);
////////yay!
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceOverboard(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Overboard" + suffix + ".png"), pm, PALETTE, false, 0);
////////I'm in love
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceWren(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Wren" + suffix + ".png"), pm, PALETTE, false, 0);
////////////more love!
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceBlubber(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Blubber" + suffix + ".png"), pm, PALETTE, false, 0);
///////////has some issues, maybe?
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBurkes(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Burkes" + suffix + ".png"), pm, PALETTE, false, 0);
/////////////great, especially for error diffusion
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceBurkes0(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Burkes0" + suffix + ".png"), pm, PALETTE, false, 0);
////////////even better!
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceBurkes2(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Burkes2" + suffix + ".png"), pm, PALETTE, false, 0);
/////////great!
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceOceanic(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Oceanic" + suffix + ".png"), pm, PALETTE, false, 0);
/////// basically the same as Oceanic, but without the Burkes constants
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceSeaside(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Seaside" + suffix + ".png"), pm, PALETTE, false, 0);
//////// oceanic meets pattern dither
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceCoastal(pm);
////        a8png8.writePrecisely(Gdx.files.local(subname + "_Coastal" + suffix + ".png"), pm, PALETTE, false, 0);
////// nice and griddy, for things that can use that
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reducePatternish(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Patternish" + suffix + ".png"), pm, PALETTE, false, 0);
////// Absolutely terrible!
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceSkitter(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Skitter" + suffix + ".png"), pm, PALETTE, false, 0);
////// ???
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceBerry(pm);
        a8png8.writePrecisely(Gdx.files.local(subname + "_Berry" + suffix + ".png"), pm, PALETTE, false, 0);

    }
    private void drawPartFullColor(Pixmap pm, Pixmap sam, A8PaletteReducer reducer, A8PNG a8png8, String subname, String suffix) throws IOException {

        // main block

////////////very good
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceJimenez(pm);
//        a8png8.write(Gdx.files.local(subname + "_GradientNoise" + suffix + ".png"), pm);
////////////good enough
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceSierraLite(pm);
//        a8png8.write(Gdx.files.local(subname + "_SierraLite" + suffix + ".png"), pm);
////////////rather good
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        a8png8.write(Gdx.files.local(subname + "_FloydSteinberg" + suffix + ".png"), pm);
////////ok
////        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoise(pm);
//        a8png8.write(Gdx.files.local(subname + "_BlueNoise" + suffix + ".png"), pm);
//////////great
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceBluish(pm);
////        a8png8.write(Gdx.files.local(subname + "_Neuter" + suffix + ".png"), pm);
//////////very good
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceScatter(pm);
//        a8png8.write(Gdx.files.local(subname + "_Scatter" + suffix + ".png"), pm);
//////////YAY YIPPEE WOO NO BANDING
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceNeue(pm);
//        a8png8.write(Gdx.files.local(subname + "_Neue" + suffix + ".png"), pm);
//////////////incredible
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceKnoll(pm);
//        a8png8.write(Gdx.files.local(subname + "_Pattern" + suffix + ".png"), pm);
////////////////
////        //Took 98748 ms
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceKnollFull(pm);
//        a8png8.write(Gdx.files.local(subname + "_PatternFull" + suffix + ".png"), pm);
////////////??? error diffusion with IGN
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceIgneous(pm);
//        a8png8.write(Gdx.files.local(subname + "_Igneous" + suffix + ".png"), pm);
////////////very good, error-diffusion, per-channel color
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceWoven(pm);
//        a8png8.write(Gdx.files.local(subname + "_Woven" + suffix + ".png"), pm);
////////////fairly good, low banding, some other artifacts, per-channel color
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceRoberts(pm);
//        a8png8.write(Gdx.files.local(subname + "_Roberts" + suffix + ".png"), pm);
//////very good!
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceDodgy(pm);
//        a8png8.write(Gdx.files.local(subname + "_Dodgy" + suffix + ".png"), pm);
////////retro, doesn't have to be classically good
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceLoaf(pm);
//        a8png8.write(Gdx.files.local(subname + "_Loaf" + suffix + ".png"), pm);
////////retro, doesn't have to be classically good
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceLoaf2(pm);
////        a8png8.write(Gdx.files.local(subname + "_Loaf2" + suffix + ".png"), pm);
//////////retro, doesn't have to be classically good
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceLoaf3(pm);
////        a8png8.write(Gdx.files.local(subname + "_Loaf3" + suffix + ".png"), pm);
////////retro, doesn't have to be classically good, but this sucks
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceLeaf(pm);
////        a8png8.write(Gdx.files.local(subname + "_Leaf" + suffix + ".png"), pm);
//////retro, doesn't have to be classically good
//        // 27994 ms for just this
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceGourd(pm);
//        a8png8.write(Gdx.files.local(subname + "_Gourd" + suffix + ".png"), pm);
////////experimenting with LUTs to allow proper gamma correction
////        // 26523 ms for just this
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceGourdLUT(pm);
////        a8png8.write(Gdx.files.local(subname + "_GourdLUT" + suffix + ".png"), pm);
////////experimenting to see what this looks like without gamma correction
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceGourdNoGamma(pm);
////        a8png8.write(Gdx.files.local(subname + "_GourdNoGamma" + suffix + ".png"), pm);
////////stylistic, not a traditional dither
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceSchmidt(pm);
////        a8png8.write(Gdx.files.local(subname + "_Kufic" + suffix + ".png"), pm);
//////yay!
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceOverboard(pm);
//        a8png8.write(Gdx.files.local(subname + "_Overboard" + suffix + ".png"), pm);
////////I'm in love
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceWren(pm);
//        a8png8.write(Gdx.files.local(subname + "_Wren" + suffix + ".png"), pm);
////////////more love!
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceBlubber(pm);
////        a8png8.write(Gdx.files.local(subname + "_Blubber" + suffix + ".png"), pm);
///////////has some issues, maybe?
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBurkes(pm);
//        a8png8.write(Gdx.files.local(subname + "_Burkes" + suffix + ".png"), pm);
/////////////great, especially for error diffusion
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceBurkes0(pm);
////        a8png8.write(Gdx.files.local(subname + "_Burkes0" + suffix + ".png"), pm);
////////////even better!
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceBurkes2(pm);
////        a8png8.write(Gdx.files.local(subname + "_Burkes2" + suffix + ".png"), pm);
///////great!
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceOceanic(pm);
//        a8png8.write(Gdx.files.local(subname + "_Oceanic" + suffix + ".png"), pm);
/////// basically the same as Oceanic, but without the Burkes constants
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceSeaside(pm);
//        a8png8.write(Gdx.files.local(subname + "_Seaside" + suffix + ".png"), pm);
//////// oceanic meets pattern dither
////        pm.drawPixmap(sam, 0, 0);
////        pm = reducer.reduceCoastal(pm);
////        a8png8.write(Gdx.files.local(subname + "_Coastal" + suffix + ".png"), pm);
////// nice and griddy, for things that can use that
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reducePatternish(pm);
//        a8png8.write(Gdx.files.local(subname + "_Patternish" + suffix + ".png"), pm);

    }
}
