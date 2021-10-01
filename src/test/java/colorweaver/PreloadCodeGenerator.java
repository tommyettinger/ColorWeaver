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
        int[] tetra255 = new int[] {
                0x00000000, 0x000000FF, 0x282828FF, 0x585858FF, 0x868686FF, 0xA2A2A2FF, 0xCBCBCBFF, 0xFFFFFFFF,
                0xA6D755FF, 0x8ABF5EFF, 0x7AA221FF, 0x6E8A31FF, 0x0A4111FF, 0x0F661DFF, 0x147E25FF, 0x229E35FF,
                0x42C372FF, 0x8CE7A6FF, 0x78FAE6FF, 0x00C7A5FF, 0x009282FF, 0x163135FF, 0x21526BFF, 0x3B768FFF,
                0x53A1ADFF, 0x8CDAFFFF, 0x50AAF7FF, 0x3E83EBFF, 0x354AD7FF, 0x1D2DAAFF, 0x152135FF, 0x66397EFF,
                0x7E5596FF, 0xA68ABFFF, 0xDFBFEFFF, 0xC772FFFF, 0x9245E7FF, 0x6E05C3FF, 0x350082FF, 0x721C2FFF,
                0xB22E69FF, 0xE54286FF, 0xFF6EAFFF, 0xFF9AC7FF, 0xFFD7F3FF, 0xE3B3C3FF, 0xCB96A2FF, 0xAA768AFF,
                0xCF4929FF, 0xF3820DFF, 0xFFAA0DFF, 0xFFD3A6FF, 0xFFBB8AFF, 0xE7A67AFF, 0xBB7251FF, 0x82491DFF,
                0x513115FF, 0xB7515AFF, 0x6E5A51FF, 0x8A7A5AFF, 0xB79E5EFF, 0xDFC721FF, 0xFFDF00FF, 0xFFF3B3FF,
                0x050403FF, 0x0F0D0CFF, 0x1A1817FF, 0x1D182CFF, 0x251B2EFF, 0x2B1C1BFF, 0x2C1D2EFF, 0x2D231CFF,
                0x2D241DFF, 0x1D2F21FF, 0x2E2820FF, 0x323425FF, 0x393534FF, 0x334459FF, 0x4A4645FF, 0x45405AFF,
                0x574241FF, 0x4F435BFF, 0x5A475CFF, 0x405645FF, 0x594D44FF, 0x5B4F46FF, 0x5C554BFF, 0x56644FFF,
                0x4A6264FF, 0x726E6CFF, 0x637690FF, 0x777090FF, 0x8C7472FF, 0x837491FF, 0x6A836FFF, 0x8F8176FF,
                0x908277FF, 0x938B7EFF, 0x8C9C83FF, 0x809B9EFF, 0x9A9D87FF, 0x9BB6A0FF, 0x9BB1CEFF, 0xB2AACFFF,
                0xB6B2B0FF, 0xC9ADAAFF, 0xCCBCB0FF, 0xD3CABCFF, 0xBDDCDFFF, 0xCADCC0FF, 0xD9DCC4FF, 0xB0923AFF,
                0xDEBE62FF, 0xB4A740FF, 0xE1D468FF, 0xFBF42CFF, 0x6E6C3DFF, 0xEDFF21FF, 0xDDEC6FFF, 0xB4C048FF,
                0xDAFF2FFF, 0xD0EA73FF, 0xA7BD4CFF, 0x98A868FF, 0xC9FF29FF, 0xC2DF9EFF, 0xBFE86FFF, 0x8BA56AFF,
                0x59703BFF, 0xAFFF21FF, 0x82BE46FF, 0xA8E96BFF, 0x94FF19FF, 0xB0DD99FF, 0x7BA366FF, 0x7AFF27FF,
                0x49FF21FF, 0x50BD47FF, 0x76E86BFF, 0x64A362FF, 0x00FF3CFF, 0x00EE6EFF, 0x346C4FFF, 0x569271FF,
                0x7CBC98FF, 0x30B482FF, 0x23A979FF, 0x00DF9BFF, 0x00DFBBFF, 0x2B665DFF, 0x2BAD9AFF, 0x69AAA0FF,
                0x00EAE0FF, 0x38BDC1FF, 0x00EBFFFF, 0x00CEFFFF, 0x319DBCFF, 0x326672FF, 0x5AC7E8FF, 0x2D80B7FF,
                0x00ADFFFF, 0x81ACD7FF, 0x1D44B0FF, 0x3967DCFF, 0x2200FFFF, 0x667ACEFF, 0x1D2565FF, 0x3E4D97FF,
                0x4300FFFF, 0x5F00FFFF, 0x4B2BB2FF, 0x877ECEFF, 0x684DDDFF, 0x584E95FF, 0x7900FFFF, 0x6C5199FF,
                0x9D7ED0FF, 0x8F05FFFF, 0x6D2FB3FF, 0x422866FF, 0x9052DFFF, 0x765399FF, 0x9B13FEFF, 0x7A31B5FF,
                0xA600FFFF, 0xA155E2FF, 0x492A66FF, 0xAE56E2FF, 0x8631B5FF, 0xB510FFFF, 0xBD15FFFF, 0xBA85D3FF,
                0x552B67FF, 0x9431B1FF, 0xCA11FFFF, 0xBE56DEFF, 0xC789D4FF, 0x9F35B3FF, 0x90599CFF, 0xE10AFFFF,
                0xED16FCFF, 0xDB5CE3FF, 0xAE36B6FF, 0xFD15DFFF, 0x9B5382FF, 0x692B54FF, 0xB33581FF, 0xDD56A4FF,
                0xFD1BA4FF, 0xFF1271FF, 0xCF8088FF, 0xFE173BFF, 0xCE8579FF, 0xB0342EFF, 0xFD2918FF, 0xDC654CFF,
                0xB1432EFF, 0xFA4411FF, 0x98584EFF, 0x986650FF, 0xFD580BFF, 0xDD754EFF, 0x663B28FF, 0xB0512CFF,
                0xB25D33FF, 0xFC6A1AFF, 0x9A6C54FF, 0xF8721EFF, 0xDE8155FF, 0xDF8650FF, 0xFD740EFF, 0xF9770FFF,
                0xB36731FF, 0x68412CFF, 0x68452FFF, 0x9A7258FF, 0xE08C54FF, 0xFA7E17FF, 0xB3612EFF, 0xD3A085FF,
                0xD0A386FF, 0xDE8B53FF, 0xB56832FF, 0xDF975AFF, 0xFD8F14FF, 0x6A4A2DFF, 0x9E7F5BFF, 0xFB9A1EFF,
                0xE0A259FF, 0xB47B35FF, 0xD7B58DFF, 0xB4863CFF, 0xDEAD5FFF, 0xF7B321FF, 0x6A5A36FF, 0x9E8B63FF,
        };

        PaletteReducer reducer = new PaletteReducer(tetra255, PaletteReducer.oklabCarefulMetric);
        generatePreloadCode(reducer.paletteMapping, "TetraPreload.txt");
        Gdx.files.local("TetraPreload.dat").writeBytes(reducer.paletteMapping, false);
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
