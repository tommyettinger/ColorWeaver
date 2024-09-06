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
        final String targetDir = "samples/reducedLow"+HexGenerator.SPACE+"/"; //
//        final String targetDir = "samples/reducedOkOnce/"; //
//        final String targetDir = "samples/reducedRgbSqrt/"; //


//        final String targetDir = "samples/reducedOkSigmoid/"; //
//        final String targetDir = "samples/reducedOkStraight/"; //
//        final String targetDir = "samples/ignored/youeye1/"; //
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
        PaletteReducer reducer = new PaletteReducer();

        // do everything
//        for(FileHandle hex : hexes) {

        // just do the one in HexGenerator
        FileHandle hex = Gdx.files.local("palettes/hex/"+HexGenerator.NAME+".hex");{

        // the default
//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/bw-2.hex"),
//                Gdx.files.local("palettes/hex/dawnbringer-8.hex"),
//                Gdx.files.local("palettes/hex/dawnbringer-16.hex"),
//                Gdx.files.local("palettes/hex/dawnbringer-32.hex"),
//                Gdx.files.local("palettes/hex/db-aurora-255.hex"),
//                Gdx.files.local("palettes/hex/gb-4.hex"),
//                Gdx.files.local("palettes/hex/prospecal-8.hex"),
//                Gdx.files.local("palettes/hex/septembit23-6.hex"),
//                Gdx.files.local("palettes/hex/gb-16.hex"),
//                Gdx.files.local("palettes/hex/japanese-woodblock-12.hex"),
//                Gdx.files.local("palettes/hex/azurestar-32.hex"),
//                Gdx.files.local("palettes/hex/americana-4.hex"),
//                Gdx.files.local("palettes/hex/ayy-4.hex"),
//                Gdx.files.local("palettes/hex/gray-15.hex"),
//                Gdx.files.local("palettes/hex/vinik-24.hex"),
//                Gdx.files.local("palettes/hex/hyper-8.hex"),
//                Gdx.files.local("palettes/hex/snuggly-15.hex"),
//                Gdx.files.local("palettes/hex/snuggly-31.hex"),
//                Gdx.files.local("palettes/hex/snuggly-63.hex"),
//                Gdx.files.local("palettes/hex/snuggly-255.hex"),
//        }) {

        // auto-generated Snuggly palettes
//        FileHandle[] snugglies = new FileHandle[7];
//        for (int i = 8, idx = 0; i < 15; i++) {
//            snugglies[idx++] = Gdx.files.local("snuggly-"+i+".hex");
//        }
//        for(FileHandle hex : snugglies) {


//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/uncured-official-112.hex"),
//                Gdx.files.local("palettes/hex/sendhelp-50.hex"),
//                Gdx.files.local("palettes/hex/cormorant-14.hex"),
//                Gdx.files.local("palettes/hex/sanguepear-24.hex"),
//                Gdx.files.local("palettes/hex/sage-57.hex"),
//                Gdx.files.local("palettes/hex/nanner2022-29.hex"),
//                Gdx.files.local("palettes/hex/nostalgic-dreams-8.hex"),
//                Gdx.files.local("palettes/hex/the-crow-67.hex"),
//                Gdx.files.local("palettes/hex/jehkoba-32.hex"),
//                Gdx.files.local("palettes/hex/aren-32.hex"),
//                Gdx.files.local("palettes/hex/archimedes-64.hex"),
//                Gdx.files.local("palettes/hex/minty-steel-4.hex"),
//                Gdx.files.local("palettes/hex/rgr-proto-16.hex"),
//                Gdx.files.local("palettes/hex/nostalgic-memories-12.hex"),
//                Gdx.files.local("palettes/hex/kiwami-v1-64.hex"),
//                Gdx.files.local("palettes/hex/rgr-papercut-4.hex"),
//                Gdx.files.local("palettes/hex/eris-18.hex"),
//                Gdx.files.local("palettes/hex/woodspark-16.hex"),
//                Gdx.files.local("palettes/hex/absolutley-18.hex"),
//                Gdx.files.local("palettes/hex/viewline-64.hex"),
//                Gdx.files.local("palettes/hex/greyteen-18.hex"),
//                Gdx.files.local("palettes/hex/asympix-18.hex"),
//                Gdx.files.local("palettes/hex/glomzy-6.hex"),
//                Gdx.files.local("palettes/hex/miyazaki-16.hex"),
//                Gdx.files.local("palettes/hex/atropoeia-48.hex"),
//                Gdx.files.local("palettes/hex/ludpiratepalette-128.hex"),
//                Gdx.files.local("palettes/hex/pixel-lands-38.hex"),
//                Gdx.files.local("palettes/hex/paulette-56.hex"),
//        }) {

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/septembit23-6.hex"),
//                Gdx.files.local("palettes/hex/arjibi-8.hex"),
//                Gdx.files.local("palettes/hex/desatur-8.hex"),
//                Gdx.files.local("palettes/hex/fairydust-8.hex"),
//                Gdx.files.local("palettes/hex/nostalgic-dreams-8.hex"),
//                Gdx.files.local("palettes/hex/prospecal-8.hex"),
//                Gdx.files.local("palettes/hex/rosemoss-8.hex"),
//                Gdx.files.local("palettes/hex/viewline-64.hex"),
//        }) {

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/dharm-32.hex"),
//                Gdx.files.local("palettes/hex/bjg-sw-poster-7.hex"),
//                Gdx.files.local("palettes/hex/sunlit-days-22.hex"),
//                Gdx.files.local("palettes/hex/zee-ze-ze-ze-ze-14.hex"),
//                Gdx.files.local("palettes/hex/soft-thirty-30.hex"),
//        }) {

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/sm-septembit-4.hex"),
//        }) {

//                Gdx.files.local("palettes/hex/retrobubble-16.hex"),
//                Gdx.files.local("palettes/hex/fruitpunch-24.hex"),
//                Gdx.files.local("palettes/hex/fractals-die-die-die-32.hex"),

//        FileHandle hex = Gdx.files.local("palettes/hex/websafe-216.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/bw-2.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/db-iso-22.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/azurestar-32.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/manos-64.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/hyper-8.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/tzi-24.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/ziggurat-63.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/dawnvinja-63.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/betts-63.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/tater-255.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/yam2-255.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/dbpaip-18.hex");{

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/bw-2.hex"),
//                Gdx.files.local("palettes/hex/gray-16.hex"),
//                Gdx.files.local("palettes/hex/gray-15.hex"),
//                Gdx.files.local("palettes/hex/grayfull-256.hex"),
//        }) {

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("palettes/hex/snuggly-15.hex"),
//                Gdx.files.local("palettes/hex/snuggly-31.hex"),
//                Gdx.files.local("palettes/hex/snuggly-63.hex"),
//                Gdx.files.local("palettes/hex/snuggly-255.hex"),
//        }) {

//            if(i++ > 12) break;

            String name = hex.nameWithoutExtension().toLowerCase(), suffix = "_" + name;

//            if(name.compareToIgnoreCase("t") < 0) continue;

            System.out.println(name);
//            if(name.compareToIgnoreCase("nameOfPaletteThatFailed") < 0) continue;
            loadPalette(name);
//            if(PALETTE.length != 5)
//                continue;

            Gdx.files.local(targetDir).mkdirs();
            reducer.exact(PALETTE, HexGenerator.METRIC);
            png8.palette = reducer;
            try {
                for(FileHandle sample : samples) {
                    long startTime = System.currentTimeMillis();
                    Pixmap pm, sam = new Pixmap(sample);
                    pm = new Pixmap(sam.getWidth(), sam.getHeight(), sam.getFormat());
                    reducer.setDitherStrength(1f);
//                    String subname = targetDir + "/" + sample.nameWithoutExtension();
                    String subname = targetDir + name + "/" + sample.nameWithoutExtension();
////lousy but important
                    pm.drawPixmap(sam, 0, 0);
                    pm = reducer.reduceSolid(pm);
                    png8.writePrecisely(Gdx.files.local(subname + "_Solid" + suffix + ".png"), pm, PALETTE, false, 0);

                    reducer.setDitherStrength(1f);
                    drawPart(pm, sam, reducer, png8, subname, suffix);
//
//                    System.out.println("Took " + (System.currentTimeMillis() - startTime) + " ms");

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

        Gdx.app.exit();
    }

    private void drawPart(Pixmap pm, Pixmap sam, PaletteReducer reducer, PNG8 png8, String subname, String suffix) throws IOException {

        // Error diffusion dithers, with configurable strengths

//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceSierraLite(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_SierraLite" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinbergCurvy(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinbergCurvy" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.FS_MULTIPLIER = 0.04;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg_04" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.FS_MULTIPLIER = 0.03;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg_03" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.FS_MULTIPLIER = 0.02;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg_02" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.FS_MULTIPLIER = 0.015;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg_015" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.FS_MULTIPLIER = 0.0125;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg_0125" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.FS_MULTIPLIER = 0.01;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg_01" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.FS_MULTIPLIER = 0.005;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceFloydSteinberg(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinberg_005" + suffix + ".png"), pm, PALETTE, false, 0);
//
//
//        PaletteReducer.BURKES_MULTIPLIER = 0.2;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBurkes(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Burkes_2" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.BURKES_MULTIPLIER = 0.125;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBurkes(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Burkes_125" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.BURKES_MULTIPLIER = 0.1;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBurkes(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Burkes_1" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.BURKES_MULTIPLIER = 0.05;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBurkes(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Burkes_05" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.BURKES_MULTIPLIER = 0.025;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBurkes(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Burkes_025" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        PaletteReducer.BURKES_MULTIPLIER = 0.01;
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBurkes(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Burkes_01" + suffix + ".png"), pm, PALETTE, false, 0);

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
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceKnoll(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Knoll_H" + suffix + ".png"), pm, PALETTE, false, 0);
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
///////great!
        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceOceanic(pm);
        png8.writePrecisely(Gdx.files.local(subname + "_Oceanic" + suffix + ".png"), pm, PALETTE, false, 0);

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
//////hot rat-infested garbage
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceGarbage(pm);
//        png8.writePrecisely(Gdx.files.local(subname + "_Garbage.png"), pm, PALETTE, false, 0);

    }
}
