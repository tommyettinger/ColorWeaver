package colorweaver;

import colorweaver.tools.StringKit;
import colorweaver.tools.TrigTools;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class HexGenerator extends ApplicationAdapter {
    private int[] palette;

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(".hex Palette Generator");
        config.setWindowedMode(640, 480);
        config.setIdleFPS(10);
        config.useVsync(true);
        config.setResizable(false);
        new Lwjgl3Application(new HexGenerator(), config);
        AutomaticPaletteTransformer.main(arg);
        AutomaticPalettizer.main(arg);
    }

    @Override
    public void create() {
        palette = new int[32];
        float hueAngle = 0.2f, sat, warm = 0.5f, mild = 0.5f;
        //0.7548776662466927, 0.5698402909980532,   0.6180339887498949
        for (int i = 0; i < 6; i++) {
            sat = TrigTools.sin_((i / 5f) * 0.5f) * 0.09f;
//            sat = TrigTools.sin_((i / 5.0) * 0.5) * 12.0;
//            palette[1 + i] = CIELABConverter.rgba8888((i / 5.0) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
            palette[1 + i] = Color.rgba8888(NamedColor.ycbcr((i / 5f), TrigTools.cos(hueAngle) * sat, TrigTools.sin(hueAngle) * sat, 1f));
            hueAngle += 1.7548776662466927;
        }
        for (int i = 1; i < 7; i++) {
            sat = TrigTools.sin_(((i + 3f) / 13f) * 0.5f) * 0.17f;
//            sat = TrigTools.sin_(((i + 2.0) / 11.0) * 0.5) * 28.0;
//            palette[6 + i] = CIELABConverter.rgba8888((i / 7.0) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
            palette[6 + i] = Color.rgba8888(NamedColor.ycbcr(((i+2f) / 11f), TrigTools.cos(hueAngle) * sat + 0.04f, TrigTools.sin(hueAngle) * sat - 0.03f, 1f));
            hueAngle += 1.7548776662466927;
        }
        for (int i = 1; i < 20; i++) {
            sat = TrigTools.sin_(((i + 6f) / 32f) * 0.5f) * 0.25f;
//            sat = TrigTools.sin_(((i + 5) / 30.0) * 0.5) * (44.0 + 10.0 * TrigTools.cos(i * Math.E));
//            palette[12 + i] = CIELABConverter.rgba8888(Math.pow((i+5) / 30.0, 0.75) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
            palette[12 + i] = Color.rgba8888(NamedColor.ycbcr((float)Math.pow((i+4f) / 28.0f, 0.75), TrigTools.cos(hueAngle) * sat - (i * 0.05f * 0.17548776662466927f) + 0.07f, TrigTools.sin(hueAngle) * sat + (i * 0.05f * 0.15698402909980532f) - 0.06f, 1f));
            hueAngle += 1.7548776662466927;
        }
        StringBuilder sb = new StringBuilder(palette.length * 7);
        for (int i = 1; i < palette.length; i++) {
            sb.append(String.format("%06x\n", palette[i] >>> 8));
        }
        Gdx.files.local("palettes/hex/splat-31.hex").writeString(sb.toString(), false);
        System.out.println("new int[] {");
        for (int i = 0; i < palette.length; i++) {
            System.out.print("0x" + StringKit.hex(palette[i]) + ", ");
            if((i & 7) == 7)
                System.out.println();
        }
        System.out.println("};");
        Gdx.app.exit();
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
