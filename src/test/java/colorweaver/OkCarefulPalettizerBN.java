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
public class OkCarefulPalettizerBN extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Initiate! Palette! Reduction!");
        config.setWindowedMode(640, 320);
        config.setIdleFPS(10);
        config.setResizable(true);
        new Lwjgl3Application(new OkCarefulPalettizerBN(), config);
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
        final String targetDir = "samples/reducedOkBN/";

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

                    reducer.setDitherStrength(0.25f);
                    subname = targetDir + name + "/" + sample.nameWithoutExtension() + "_quarter";
                    drawPart(pm, sam, reducer, a8png8, subname, suffix);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Took " + (System.currentTimeMillis() - startTime) + " ms");

        Gdx.app.exit();
    }

    private void drawPart(Pixmap pm, Pixmap sam, A8PaletteReducer reducer, A8PNG8 a8png8, String subname, String suffix) throws IOException {

//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoise(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoise" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoiseOmni64(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoiseOmni64" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoiseOmni128(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoiseOmni128" + suffix + ".png"), pm, PALETTE, false, 0);

        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceBlueNoiseDuel128(pm);
        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoiseDuel128" + suffix + ".png"), pm, PALETTE, false, 0);

//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoiseBrawl128(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoiseBrawl128" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoisePawn128(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoisePawn128" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoiseBishop128(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoiseBishop128" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoiseKnight128(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoiseKnight128" + suffix + ".png"), pm, PALETTE, false, 0);
//
//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reduceBlueNoiseRook128(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoiseRook128" + suffix + ".png"), pm, PALETTE, false, 0);

        pm.drawPixmap(sam, 0, 0);
        pm = reducer.reduceBlueCrab(pm);
        a8png8.writePrecisely(Gdx.files.local(subname + "_BlueNoisePrawn128" + suffix + ".png"), pm, PALETTE, false, 0);

//        pm.drawPixmap(sam, 0, 0);
//        pm = reducer.reducePatternish(pm);
//        a8png8.writePrecisely(Gdx.files.local(subname + "_Patternish" + suffix + ".png"), pm, PALETTE, false, 0);

    }
}
