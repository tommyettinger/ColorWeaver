package colorweaver;

import colorweaver.tools.StringKit;
import colorweaver.tools.TrigTools;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import static colorweaver.tools.TrigTools.cos_;
import static colorweaver.tools.TrigTools.sin_;

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

    private static float cosMaybe(float angle){
        return cos_(angle * 0.25f) * 0.375f + TrigTools.cosq(angle - 0.5f) * 0.625f;
    }
//3.141592653589793f
    private static float sinMaybe(float angle){
        return sin_(angle * 0.25f) * 0.375f + TrigTools.sinq(angle - 0.5f) * 0.625f;
    }
    
    private static int getInt(float color){
        final int c = FloatColorTools.floatToInt(color);
        return c | (c >>> 7 & 1);
    }

    @Override
    public void create() {
        palette = new int[32];
        NamedColor[] outer = new NamedColor[]{
            NamedColor.RED_PIGMENT,
            NamedColor.AURORA_LIGHT_SKIN_7,
            NamedColor.AURORA_PENCIL_YELLOW,
            NamedColor.AURORA_APPLE_GREEN,
            NamedColor.CW_FLUSH_SAPPHIRE,
            NamedColor.AURORA_TYRIAN_PURPLE
        };

        for (int i = 0; i < 7; i++) {
            palette[i+1] = getInt(FloatColorTools.floatGetHSV(0.5f, (float)Math.sqrt(i / 6f) * 0.2f * (i + 1 & 1), (float)Math.pow((i+1f) / 7.5f, 0.75), 1f));
        }
        for (int i = 0; i < 6; i++) {
            palette[i+8]    = getInt(FloatColorTools.floatGetHSV(outer[i].hue() /* + 0.06f */, 0.35f, 0.4f, 1f));
            palette[i+8+6]  = getInt(FloatColorTools.floatGetHSV(outer[i].hue() /* + 0.02f */, 0.2f, 0.6f, 1f));
            palette[i+8+12] = getInt(FloatColorTools.floatGetHSV(outer[i].hue() /* - 0.02f */, 0.6f, 0.75f, 1f));
            palette[i+8+18] = getInt(FloatColorTools.floatGetHSV(outer[i].hue() /* - 0.06f */, 0.45f, 0.9f, 1f));
        }
        
//        float hueAngle = 0.1f, sat;
//        //0.7548776662466927, 0.5698402909980532,   0.6180339887498949
//        for (int i = 0; i < 6; i++) {
//            sat = sin_((i / 5f) * 0.5f) * 0.05f * 2f;
////            sat = TrigTools.sin_((i / 5.0) * 0.5) * 12.0;
////            palette[1 + i] = CIELABConverter.rgba8888((i / 5.0) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
////            palette[1 + i] = Color.rgba8888(NamedColor.ycwcm((i / 5f), TrigTools.zigzag(hueAngle) * sat, TrigTools.zigzag(0.5f + hueAngle) * sat, 1f));
//            palette[1 + i] = Color.rgba8888(NamedColor.ycwcm((i / 5f), cosMaybe(hueAngle) * sat, sinMaybe(hueAngle) * sat, 1f));
//            hueAngle += 0.6180339887498949;
//        }
//        for (int i = 1; i < 7; i++) {
//            sat = sin_(((i + 3f) / 13f) * 0.5f) * 0.125f * 2f;
////            sat = TrigTools.sin_(((i + 2.0) / 11.0) * 0.5) * 28.0;
////            palette[6 + i] = CIELABConverter.rgba8888((i / 7.0) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
////            palette[6 + i] = Color.rgba8888(NamedColor.ycwcm(((i+2f) / 11f), TrigTools.zigzag(hueAngle) * sat, TrigTools.zigzag(0.5f + hueAngle) * sat, 1f));
//            palette[6 + i] = Color.rgba8888(NamedColor.ycwcm(((i+2f) / 11f), cosMaybe(hueAngle) * sat, sinMaybe(hueAngle) * sat, 1f));
//            hueAngle += 0.6180339887498949;
//        }
//        for (int i = 1; i < 20; i++) {
//            sat = sin_(((i + 6f) / 32f) * 0.5f) * 0.2f * 2f;
////            sat = TrigTools.sin_(((i + 5) / 30.0) * 0.5) * (44.0 + 10.0 * TrigTools.cos(i * Math.E));
////            palette[12 + i] = CIELABConverter.rgba8888(Math.pow((i+5) / 30.0, 0.75) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
////            palette[12 + i] = Color.rgba8888(NamedColor.ycwcm((float)Math.pow((i+4f) / 28.0f, 0.75), TrigTools.zigzag(hueAngle) * sat, TrigTools.zigzag(0.5f + hueAngle) * sat, 1f));
//            palette[12 + i] = Color.rgba8888(NamedColor.ycwcm((float)Math.pow((i+4f) / 28.0f, 0.625), cosMaybe(hueAngle) * sat, sinMaybe(hueAngle) * sat, 1f));
//            hueAngle += 0.6180339887498949;
//        }
        StringBuilder sb = new StringBuilder(palette.length * 7);
        for (int i = 1; i < palette.length; i++) {
            sb.append(String.format("%06x\n", palette[i] >>> 8));
        }
        Gdx.files.local("palettes/hex/splay-31.hex").writeString(sb.toString(), false);
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
