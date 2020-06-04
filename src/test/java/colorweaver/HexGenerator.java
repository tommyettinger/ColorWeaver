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
    public static final String NAME = "joel-16";
    
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
    public static float lerpHue (float from, float to, float progress) {
        to += 1.5f - from;
        to -= 0.5f + (int)to;
        from += to * progress + 1f;
        return from - (int)from;
        /*
        fract((fract(to + 0.5 - from) - 0.5) * progress + from)
         */
    }

    @Override
    public void create() {
//        palette = new int[64];
//        float[] outer = {
//            0.96f,//NamedColor.AURORA_CARMINE.hue(),
//            0.015f,//NamedColor.AURORA_LIGHT_SKIN_6.hue(),
//            0.013f,//NamedColor.ORANGE.hue(),
//            NamedColor.CW_LIGHT_YELLOW.hue(),
//            NamedColor.CW_GREEN.hue(),
//            NamedColor.CW_LIGHT_CYAN.hue(),
//            NamedColor.CW_SAPPHIRE.hue(),
//            NamedColor.CW_PURPLE.hue()
//        };
//
//        for (int i = 0; i < 7; i++) {
//            palette[i+1] = getInt(floatGetHSV(0.078f, 0.05f, (i+0.5f)/7f, 1f));
//        }
//        for (int i = 0; i < 8; i++) {
//            float sm = 1.0625f, vm = (9.5f + ((i & 3))) * 0.1f;
//            if(i <= 1 || i == 4) sm -= 0.09375f;
//            if(i == 7 || i == 5) sm -= 0.125f;
//            if(i == 6) vm = 0.9875f;
//            if(i == 7) vm = 0.9375f;
//            palette[i+8]    = getInt(floatGetHSV(outer[i], sm * 0.375f, vm * 0.25f, 1f));
//            palette[i+8+8]  = getInt(floatGetHSV(outer[i], sm * 0.3f, vm * 0.4f, 1f));
//            palette[i+8+16] = getInt(floatGetHSV(outer[i], sm * 0.45f, vm * 0.55f, 1f));
//            palette[i+8+24] = getInt(floatGetHSV(outer[i], sm * 0.55f, vm * 0.75f, 1f));
//            palette[i+8+32] = getInt(floatGetHSV(outer[i], sm * 0.4f, vm * 0.9f, 1f));
//            palette[i+8+40] = getInt(floatGetHSV(lerpHue(outer[i], outer[i+1 & 7], 0.4f), sm * 0.5f, vm * 0.475f, 1f));
//            palette[i+8+48] = getInt(floatGetHSV(lerpHue(outer[i], outer[i-1 & 7], 0.4f), sm * 0.35f, vm * 0.65f, 1f));
//        }
        
//        palette = new int[217];
//        for (int r = 0, i = 1; r < 6; r++) {
//            for (int g = 0; g < 6; g++) {
//                for (int b = 0; b < 6; b++) {
//                    palette[i++] = (r * 0x330000 | g * 0x3300 | b * 0x33) << 8 | 0xFF;
//                }
//            }
//        }
//        palette = Coloring.MANOS64;
        palette = new int[] {0x080000FF,0x201A0BFF,0x432817FF,0x492910FF,
                0x234309FF,0x5D4F1EFF,0x9C6B20FF,0xA9220FFF,
                0x2B347CFF,0x2B7409FF,0xD0CA40FF,0xE8A077FF,
                0x6A94ABFF,0xD5C4B3FF,0xFCE76EFF,0xFCFAE2FF };
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
        Gdx.files.local("palettes/hex/"+HexGenerator.NAME+".hex").writeString(sb.toString(), false);
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
