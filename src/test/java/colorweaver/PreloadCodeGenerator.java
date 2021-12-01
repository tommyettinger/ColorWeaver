package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class PreloadCodeGenerator extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Preload Code Generator");
        config.setWindowedMode(320, 320);
        config.setIdleFPS(1);
        config.setResizable(false);
        new Lwjgl3Application(new PreloadCodeGenerator(), config);
    }

    public void create() {
        int[] yam2 = new int[] {
                0x00000000, 0x000000FF, 0x0B0909FF, 0x1C1A19FF, 0x302D2CFF, 0x464241FF, 0x5A5755FF, 0x6F6B6AFF,
                0x847F7EFF, 0x999492FF, 0xACA7A5FF, 0xBEB9B7FF, 0xD1CCCAFF, 0xE2DDDBFF, 0xF3EEEBFF, 0xFFFDFBFF,
                0x3D1913FF, 0x7C4C43FF, 0xB98076FF, 0xEFB0A4FF, 0x342620FF, 0x6F5C54FF, 0xA89289FF, 0xDCC4BAFF,
                0x3B2414FF, 0x7B5D48FF, 0xB8957DFF, 0xEEC8ADFF, 0x392E19FF, 0x776A4FFF, 0xB1A183FF, 0xE6D5B4FF,
                0x353914FF, 0x73794DFF, 0xADB482FF, 0xE2EBB4FF, 0x27391DFF, 0x627955FF, 0x9AB58CFF, 0xCDEABDFF,
                0x14391AFF, 0x457049FF, 0x73A478FF, 0x9FD4A4FF, 0x173B3FFF, 0x50797EFF, 0x87B5BBFF, 0xB8EAF0FF,
                0x0A1A42FF, 0x364F82FF, 0x6583BDFF, 0x92B3F4FF, 0x271B3EFF, 0x5D4F7DFF, 0x9182B7FF, 0xC3B2EDFF,
                0x321941FF, 0x6D4E81FF, 0xA683BEFF, 0xDAB3F4FF, 0x3A1B3EFF, 0x78517DFF, 0xB486B9FF, 0xE9B7EFFF,
                0x79261AFF, 0xB55545FF, 0xEB816DFF, 0x74351EFF, 0xAE654AFF, 0xE49274FF, 0x6A4434FF, 0xA17461FF,
                0xD4A28DFF, 0x604C40FF, 0x978072FF, 0xC8AF9FFF, 0x7A491DFF, 0xB67D4DFF, 0xEBAD78FF, 0x725328FF,
                0xAF8A5AFF, 0xE5BD88FF, 0x765C20FF, 0xB29554FF, 0xE9CA83FF, 0x706E2AFF, 0xAEAC62FF, 0xE5E393FF,
                0x65761BFF, 0x9FB455FF, 0xD4EB85FF, 0x58742AFF, 0x90B15FFF, 0xC4E98FFF, 0x3F741CFF, 0x76B254FF,
                0xA8EA84FF, 0x28762CFF, 0x5EB25FFF, 0x8FE98EFF, 0x14754EFF, 0x47A679FF, 0x70D3A2FF, 0x156B65FF,
                0x459992FF, 0x6CC4BBFF, 0x00697CFF, 0x4AA5BAFF, 0x7CDBF2FF, 0x0F4A77FF, 0x4380B4FF, 0x71B3EDFF,
                0x1D217DFF, 0x4052BBFF, 0x6680F3FF, 0x362479FF, 0x6154B4FF, 0x8E81EDFF, 0x4B207FFF, 0x7C50BCFF,
                0xAC7EF4FF, 0x552779FF, 0x8855B3FF, 0xB982E9FF, 0x63237EFF, 0x9B55BAFF, 0xCE82F1FF, 0x702978FF,
                0xAA5AB3FF, 0xE089EAFF, 0x7A2061FF, 0xB64F95FF, 0xEC7BC6FF, 0x722431FF, 0xAE535EFF, 0xE47E88FF,
                0xBD2520FF, 0xEB4C40FF, 0xBA3D27FF, 0xE55F46FF, 0xBE4424FF, 0xEC6845FF, 0xA1614AFF, 0xCB846BFF,
                0xA76240FF, 0xD48963FF, 0xBA6130FF, 0xE78653FF, 0xBB6420FF, 0xEB8C48FF, 0xB67230FF, 0xE59B56FF,
                0xBB7721FF, 0xECA24CFF, 0xB58533FF, 0xE3B05AFF, 0xBE9923FF, 0xEAC24DFF, 0xBDAD3DFF, 0xEADA66FF,
                0xAFBA1DFF, 0xDFEC54FF, 0xA4BB37FF, 0xD1EB63FF, 0x8ABD26FF, 0xB6EE56FF, 0x78B937FF, 0xA4EB63FF,
                0x5FBA17FF, 0x8AEC4DFF, 0x41BA30FF, 0x6CEA5BFF, 0x00BC40FF, 0x25DB5AFF, 0x18B27FFF, 0x37C993FF,
                0x00B8A1FF, 0x25D6BDFF, 0x25BBBFFF, 0x5AEBEFFF, 0x009EC0FF, 0x41CBF0FF, 0x097ABAFF, 0x40A6ECFF,
                0x0024C0FF, 0x164CEFFF, 0x332DB7FF, 0x4C51E3FF, 0x4E22C1FF, 0x6E4CF3FF, 0x672DBBFF, 0x8B53EAFF,
                0x7823C5FF, 0x9E4BF4FF, 0x8130BFFF, 0xA755ECFF, 0x9123C7FF, 0xBB4EF7FF, 0x9B31BBFF, 0xC759EAFF,
                0xAF27C8FF, 0xDC51F6FF, 0xB435B9FF, 0xE35CE7FF, 0xBE2285FF, 0xEB49AAFF, 0xB82E56FF, 0xE45076FF,
                0xFF1B00FF, 0xF34428FF, 0xFC4400FF, 0xCD7758FF, 0xD7764CFF, 0xED7432FF, 0xC59171FF, 0xF87E26FF,
                0xF98000FF, 0xF49023FF, 0xFA9600FF, 0xF2A733FF, 0xF6AF00FF, 0xF9C834FF, 0xFEDD00FF, 0xFFF932FF,
                0xEDFF00FF, 0xD7FB38FF, 0xC1FE00FF, 0xACF829FF, 0x8CFF00FF, 0x72FC32FF, 0x2EFE00FF, 0x26FC45FF,
                0x00FF7EFF, 0x28F7B1FF, 0x00FBD4FF, 0x07FFF5FF, 0x00E7FEFF, 0x00C3F8FF, 0x00A6FFFF, 0x0070F2FF,
                0x2300FFFF, 0x3F25F1FF, 0x5F00FFFF, 0x7824F7FF, 0x9500FFFF, 0x9625FAFF, 0xAD00FFFF, 0xB32AFFFF,
                0xCA00FFFF, 0xC72BF8FF, 0xEB03FFFF, 0xEF33FFFF, 0xFE00E2FF, 0xF02D9CFF, 0xFF0070FF, 0xF62941FF,
        };

        PaletteReducer reducer = new PaletteReducer(yam2, PaletteReducer.oklabCarefulMetric);
        generatePreloadCode(reducer.paletteMapping, "Yam2Preload.txt");
        Gdx.files.local("Yam2Preload.dat").writeBytes(reducer.paletteMapping, false);
        Gdx.app.exit();
    }
    /**
     * Given a byte array, this writes a file containing a code snippet that can be pasted into Java code as the preload
     * data used by {@link PaletteReducer#exact(int[], byte[])}; this is almost never needed by external code. When 
     * using this for preload data, the byte array should be {@link PaletteReducer#paletteMapping}.
     * @param data the bytes to use as preload data, usually {@link PaletteReducer#paletteMapping}
     */
    public static void generatePreloadCode(final byte[] data) {
        generatePreloadCode(data, "bytes_" + TimeUtils.millis() + ".txt");
    }
    /**
     * Given a byte array, this appends to a file called {@code filename} containing a code snippet that can be pasted
     * into Java code as the preload data used by {@link PaletteReducer#exact(int[], byte[])}; this is almost never
     * needed by external code. When using this for preload data, the byte array should be
     * {@link PaletteReducer#paletteMapping}.
     * @param data the bytes to use as preload data, usually {@link PaletteReducer#paletteMapping}
     * @param filename the name of the text file to append to
     */
    public static void generatePreloadCode(final byte[] data, String filename){
        StringBuilder sb = new StringBuilder(data.length + 400);
        sb.append('"');
        for (int i = 0; i < data.length;) {
            for (int j = 0; j < 0x80 && i < data.length; j++) {
                byte b = data[i++];
                switch (b)
                {
                    case '\t': sb.append("\\t");
                        break;
                    case '\b': sb.append("\\b");
                        break;
                    case '\n': sb.append("\\n");
                        break;
                    case '\r': sb.append("\\r");
                        break;
                    case '\f': sb.append("\\f");
                        break;
                    case '\"': sb.append("\\\"");
                        break;
                    case '\\': sb.append("\\\\");
                        break;
                    default:
//                        if(Character.isISOControl(b))
//                            sb.append(String.format("\\%03o", b));
//                        else
                            sb.append((char) (b&0xFF));
                        break;
                }
            } 
        }
        sb.append("\".getBytes(\"ISO-8859-1\");\n");
//        sb.append("\".getBytes(StandardCharsets.ISO_8859_1);\n");
        Gdx.files.local(filename).writeString(sb.toString(), true, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
    }
}
