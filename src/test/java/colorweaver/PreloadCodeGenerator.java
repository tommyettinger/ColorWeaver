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
        int[] joker255 = new int[] {
                0x00000000, 0x000000FF, 0x050403FF, 0x0F0D0CFF, 0x1A1817FF, 0x292625FF, 0x393534FF, 0x4A4645FF,
                0x5C5957FF, 0x726E6CFF, 0x878381FF, 0x9D9997FF, 0xB6B2B0FF, 0xCFCAC8FF, 0xE9E4E2FF, 0xFBFFFFFF,
                0x3F0C10FF, 0x5C393EFF, 0x956C75FF, 0xDBA3B1FF, 0x3B1C0CFF, 0x504D3CFF, 0x8A7F6CFF, 0xBDC1AEFF,
                0x251F12FF, 0x614C48FF, 0xA77E77FF, 0xEDB8B6FF, 0x452515FF, 0x5F5643FF, 0x9C8C86FF, 0xEBCABBFF,
                0x423817FF, 0x6E6552FF, 0xA69E87FF, 0xECE0C0FF, 0x1A421BFF, 0x4F6951FF, 0x7B9B80FF, 0xB6ECCFFF,
                0x142B13FF, 0x2C5527FF, 0x6A8369FF, 0x9BBAA1FF, 0x153144FF, 0x4D6272FF, 0x829AAEFF, 0xB5E6F1FF,
                0x09123EFF, 0x1B4454FF, 0x5E798DFF, 0x95B7D6FF, 0x1C0A34FF, 0x42375FFF, 0x746693FF, 0xAFA1DAFF,
                0x2C133FFF, 0x4D4569FF, 0x8178A3FF, 0xC3B0E7FF, 0x461234FF, 0x6D4767FF, 0xA7799DFF, 0xECB4E7FF,
                0x731B11FF, 0x93523DFF, 0xC58374FF, 0x623819FF, 0x90654DFF, 0xD39486FF, 0x833619FF, 0xAF695CFF,
                0xF49892FF, 0x80403DFF, 0x99745FFF, 0xC1A588FF, 0x6A4715FF, 0x8F7846FF, 0xD1AC7FFF, 0x7D4F1BFF,
                0x9E8253FF, 0xF0B18AFF, 0x695B1FFF, 0x998F5DFF, 0xD9C590FF, 0x816925FF, 0xB09D63FF, 0xEDD695FF,
                0x648325FF, 0xA0B06AFF, 0xDDF6B5FF, 0x5E7035FF, 0x7FAB72FF, 0xA3F4B5FF, 0x4B6D25FF, 0x7AA159FF,
                0xACD99CFF, 0x227922FF, 0x6C9933FF, 0x80E69DFF, 0x257354FF, 0x3C9063FF, 0x7BC493FF, 0x20625BFF,
                0x2A8783FF, 0x67AEA6FF, 0x236A86FF, 0x5D93A8FF, 0x92D1E1FF, 0x1D4B77FF, 0x4473A2FF, 0x7FAAE6FF,
                0x131A75FF, 0x2B559EFF, 0x6378DAFF, 0x311D6DFF, 0x544B97FF, 0x7E86E2FF, 0x481273FF, 0x6851A7FF,
                0xA174D6FF, 0x4E2259FF, 0x75599BFF, 0xA08EDAFF, 0x5D1770FF, 0x894794FF, 0xBF81E0FF, 0x72237CFF,
                0x9D5EAEFF, 0xE38EEBFF, 0x811C56FF, 0xAA5280FF, 0xE981B5FF, 0x72162DFF, 0x974B5EFF, 0xD9788CFF,
                0xAE1D20FF, 0xD54B45FF, 0xBE3B26FF, 0xE16457FF, 0xA84D19FF, 0xD8744AFF, 0xBD583AFF, 0xF17B67FF,
                0xA75E15FF, 0xD8802BFF, 0xCA6525FF, 0xCF8E40FF, 0xAF672EFF, 0xF68D62FF, 0xA0711EFF, 0xE0985CFF,
                0xBA7924FF, 0xE4A253FF, 0xB3862EFF, 0xDAB161FF, 0xA39324FF, 0xE0C166FF, 0xBFA52BFF, 0xE4D668FF,
                0xBDC630FF, 0xEEF481FF, 0xA2C362FF, 0xBDF789FF, 0x97B333FF, 0xC4E159FF, 0x83CB35FF, 0x9DF376FF,
                0x70B230FF, 0x9FDF3CFF, 0x32C129FF, 0x66EF75FF, 0x2FA42CFF, 0x3DD24DFF, 0x27A265FF, 0x38BC7DFF,
                0x2DA89BFF, 0x3BC9B0FF, 0x36BAC9FF, 0x79F2E8FF, 0x289CC1FF, 0x60C9E6FF, 0x2580BCFF, 0x57A2E3FF,
                0x1F43B4FF, 0x2F66DBFF, 0x201DAEFF, 0x334DE7FF, 0x4A21B0FF, 0x664FE4FF, 0x6519B2FF, 0x8051E3FF,
                0x6E2EA4FF, 0x8F57DDFF, 0x7924BAFF, 0x9D5AEAFF, 0x8B20BDFF, 0xB15CEEFF, 0x951C95FF, 0xC14CCDFF,
                0xA32FB5FF, 0xCD5FE5FF, 0xC324B6FF, 0xEF63E5FF, 0xB82480FF, 0xDF58A8FF, 0xB52152FF, 0xE45377FF,
                0xEA211AFF, 0xF24727FF, 0xF15B25FF, 0xFA682FFF, 0xEB7124FF, 0xFE7831FF, 0xE47815FF, 0xFD7F1CFF,
                0xEE8729FF, 0xF19222FF, 0xEF9D25FF, 0xF3A730FF, 0xE9B42AFF, 0xEDC729FF, 0xEADE2EFF, 0xF5F444FF,
                0xF4FD27FF, 0xD9FA3BFF, 0xC7F332FF, 0xAFF733FF, 0x94F734FF, 0x73F63CFF, 0x4BFB26FF, 0x2CF02EFF,
                0x37EA5AFF, 0x39E395FF, 0x40DEBEFF, 0x3CF7D1FF, 0x36E8F4FF, 0x28CAF1FF, 0x29A5F2FF, 0x2F82F1FF,
                0x1921E8FF, 0x3D25EAFF, 0x5A22EAFF, 0x7322EAFF, 0x881EECFF, 0x9825E7FF, 0xA61CEDFF, 0xB01DE4FF,
                0xB92DECFF, 0xCA23EAFF, 0xDB20E9FF, 0xF12BF6FF, 0xF027D1FF, 0xEB26A4FF, 0xEB2372FF, 0xEA2144FF,
        };

        PaletteReducer reducer = new PaletteReducer(joker255, PaletteReducer.oklabCarefulMetric);
        generatePreloadCode(reducer.paletteMapping, "TaterPreload.txt");
        Gdx.files.local("TaterPreload.dat").writeBytes(reducer.paletteMapping, false);
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
                        if(Character.isISOControl(b))
                            sb.append(String.format("\\%03o", b));
                        else
                            sb.append((char) (b&0xFF));
                        break;
                }
            } 
        }
        sb.append("\".getBytes(StandardCharsets.ISO_8859_1);\n");
        Gdx.files.local(filename).writeString(sb.toString(), true, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
    }
}
