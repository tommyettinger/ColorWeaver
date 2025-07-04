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
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class PaletteCubeGenerator extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("TRANSFOOOORM!");
        config.setWindowedMode(640, 320);
        config.setIdleFPS(10);
        config.setResizable(true);
        new Lwjgl3Application(new PaletteCubeGenerator(), config);
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
        FileHandle[] hexes = Gdx.files.local("palettes/hex/").list(".hex");
        Gdx.files.local("palettes/gen/txt/").mkdirs();
//        Gdx.files.local("palettes/genOkOnce/").mkdirs();
        Gdx.files.local("palettes/genOkCareful/").mkdirs();
//        Gdx.files.local("palettes/genRgbTricky/").mkdirs();
//        Gdx.files.local("palettes/genRgbStupider/").mkdirs();
//        Gdx.files.local("palettes/genRgbSlippery/").mkdirs();
//        for(FileHandle hex : hexes) {
        FileHandle hex = Gdx.files.local("palettes/hex/"+HexGenerator.NAME+".hex");{

//        for(FileHandle hex : new FileHandle[]{
//                Gdx.files.local("lospec2000-182.hex"),
//                Gdx.files.local("zenit-241.hex"),
//                Gdx.files.local("project-ruzikb-86.hex"),
//        }) {

//        FileHandle[] snugglies = new FileHandle[7];
//        for (int i = 8, idx = 0; i < 15; i++) {
//            snugglies[idx++] = Gdx.files.local("snuggly-"+i+".hex");
//        }
//        FileHandle[] hugglies = new FileHandle[6];
//        for (int i = 8, idx = 0; i <= 256; i <<= 1) {
//            hugglies[idx++] = Gdx.files.local("huggly-"+(i-1)+".hex");
//        }

//        for(FileHandle hex : hugglies) {
//        FileHandle hex = Gdx.files.local("palettes/hex/bw-2.hex");{
//        FileHandle hex = Gdx.files.local("palettes/hex/blknx-64.hex");{
            String name = hex.nameWithoutExtension().toLowerCase();
            loadPalette(name);
            StringBuilder sb = new StringBuilder((1 + 12 * 8) * (PALETTE.length + 7 >>> 3));
            for (int i = 0; i < (PALETTE.length + 7 >>> 3); i++) {
                for (int j = 0; j < 8 && (i << 3 | j) < PALETTE.length; j++) {
                    sb.append("0x").append(StringKit.hex(PALETTE[i << 3 | j]).toUpperCase()).append(", ");
                }
                sb.append('\n');
            }
            Gdx.files.local("palettes/gen/txt/" + name + ".txt").writeString(sb.toString(), false);
            sb.setLength(0);

            A8PNG8 png8 = new A8PNG8();
            png8.setCompression(7);
            png8.palette = new A8PaletteReducer(PALETTE);
            Pixmap pix = new Pixmap(256, 1, Pixmap.Format.RGBA8888);
            for (int i = 1; i < PALETTE.length; i++) {
                pix.drawPixel(i - 1, 0, PALETTE[i]);
            }
            pix.drawPixel(255, 0, 0);

//                png8.writePrecisely(Gdx.files.local("palettes/genOkOnce/" + name + ".png"), pix, false);
                png8.writePrecisely(Gdx.files.local("palettes/genOkCareful/" + name + ".png"), pix, false);
//                png8.writePrecisely(Gdx.files.local("palettes/genRgbTricky/" + name + ".png"), pix, false);
//                png8.writePrecisely(Gdx.files.local("palettes/genRgbStupider/" + name + ".png"), pix, false);
//                png8.writePrecisely(Gdx.files.local("palettes/genRgbSlippery/" + name + ".png"), pix, false);

            Pixmap p2 = new Pixmap(1024, 32, Pixmap.Format.RGBA8888);
            for (int r = 0; r < 32; r++) {
                for (int b = 0; b < 32; b++) {
                    for (int g = 0; g < 32; g++) {
                        p2.drawPixel(r << 5 | b, g, PALETTE[png8.palette.paletteMapping[((r << 10) & 0x7C00) | ((g << 5) & 0x3E0) | b] & 0xFF]);
                    }
                }
            }

//                png8.writePrecisely(Gdx.files.local("palettes/genOkOnce/" + name + "_GLSL.png"), p2, false);
                png8.writePrecisely(Gdx.files.local("palettes/genOkCareful/" + name + "_GLSL.png"), p2, false);
//                png8.writePrecisely(Gdx.files.local("palettes/genRgbTricky/" + name + "_GLSL.png"), p2, false);
//                png8.writePrecisely(Gdx.files.local("palettes/genRgbStupider/" + name + "_GLSL.png"), p2, false);
//                png8.writePrecisely(Gdx.files.local("palettes/genRgbSlippery/" + name + "_GLSL.png"), p2, false);

        }
        Gdx.app.exit();
    }
}
