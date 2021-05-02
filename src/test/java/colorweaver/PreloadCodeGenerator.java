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
        PaletteReducer reducer = new PaletteReducer(new int[]{
                0x00000000, 0x19092DFF, 0x213118FF, 0x314A29FF, 0x8C847BFF, 0x6E868EFF, 0x9CA59CFF, 0xAFC7CFFF,
                0xD6F7D6FF, 0xFBD7EBFF, 0xFDFBE3FF, 0xE73129FF, 0x7B2921FF, 0xE79C94FF, 0xBF4529FF, 0xE35A00FF,
                0xAD6329FF, 0xE78431FF, 0x4A2D11FF, 0xD39A5EFF, 0xFFAA4DFF, 0xF7CF9EFF, 0xA58C29FF, 0xFBE76AFF,
                0xBDB573FF, 0x6B7321FF, 0x8CAD29FF, 0xC7FF2DFF, 0x96DF1DFF, 0xBFEF94FF, 0x296318FF, 0x62FF39FF,
                0x39C621FF, 0x319421FF, 0x4AEF31FF, 0x39AD5AFF, 0x49FF8AFF, 0x319E7AFF, 0x296B5AFF, 0x49B39AFF,
                0x52F7DEFF, 0xA5DEDEFF, 0x39BDC6FF, 0x52CEEFFF, 0x42A5C6FF, 0x396B9CFF, 0x29426BFF, 0x394ABDFF,
                0x2910DEFF, 0x29189CFF, 0x21105AFF, 0x6329E7FF, 0x9C84CEFF, 0x8A49DBFF, 0xCEADE7FF, 0x9C29B5FF,
                0x6B1873FF, 0xD631DEFF, 0xE773D6FF, 0xA52973FF, 0xE7298CFF, 0xCF1562FF, 0x845A6BFF, 0xD66B7BFF,
                0x16210BFF, 0x232323FF, 0x082423FF, 0x2C0613FF, 0x353535FF, 0x474747FF, 0x595959FF, 0x3C505BFF,
                0x6B6B6BFF, 0x537166FF, 0x4C724AFF, 0x78795EFF, 0x809D81FF, 0xB3B3B3FF, 0xD7D7D7FF, 0xBBB2D9FF,
                0xE9E9E9FF, 0xC86160FF, 0xD08684FF, 0xD8ABA8FF, 0x8B1005FF, 0xC61300FF, 0xED6858FF, 0x933529FF,
                0xF58D7CFF, 0x9C5A4DFF, 0xFB6440FF, 0xFDB2A0FF, 0xA23111FF, 0xC73908FF, 0xA47F71FF, 0xAA5635FF,
                0xCF5E2CFF, 0x672F16FF, 0xD78350FF, 0xD1AB8CFF, 0xB15301FF, 0x70543AFF, 0xE0A874FF, 0xEEA45CFF,
                0x7E5022FF, 0xBA7825FF, 0xAB7C3DFF, 0xF5A128FF, 0xFEC64CFF, 0xC29D48FF, 0xEFCA64FF, 0x867545FF,
                0xC99A14FF, 0xDAD0B0FF, 0xB4A161FF, 0x8E7211FF, 0x52490EFF, 0xD2BF38FF, 0xCBC26CFF, 0xF0DE10FF,
                0x969735FF, 0xF0F2BCFF, 0xDAE45CFF, 0xA6B925FF, 0xBCC685FF, 0x9FD005FF, 0xAEDE49FF, 0x6A9022FF,
                0x63A702FF, 0x99F841FF, 0x7BEE16FF, 0x64CD0AFF, 0x90BF71FF, 0x72B546FF, 0x45892AFF, 0x29A407FF,
                0x639356FF, 0x97BF8DFF, 0x46AF32FF, 0x7BDA6AFF, 0x98E495FF, 0x66F562FF, 0x013B03FF, 0x4FD456FF,
                0x07FF1BFF, 0x83FF8DFF, 0x378D43FF, 0x6BB87AFF, 0x0B862FFF, 0x06EB6FFF, 0x1BD077FF, 0x24F59BFF,
                0x31B57FFF, 0x65E1B6FF, 0x39DAA3FF, 0x41FFC6FF, 0x038963FF, 0x2F9077FF, 0x7AC6BEFF, 0x83EBE1FF,
                0x06D7C3FF, 0x104947FF, 0x21938FFF, 0xAFF2F5FF, 0x23E1EFFF, 0x4D9AA2FF, 0x81C5D9FF, 0x14BFFFFF,
                0x367997FF, 0x0B9ADBFF, 0x6BA4CEFF, 0x3D78B3FF, 0x207FE3FF, 0x012757FF, 0x0F243FFF, 0x627FAAFF,
                0x185ABFFF, 0x2757A7FF, 0x4C86F6FF, 0xC4D7FCFF, 0x88AEF9FF, 0x115DF3FF, 0x0838CFFF, 0x4461D2FF,
                0x1E3283FF, 0x1735B7FF, 0x2643FBFF, 0x626BFEFF, 0x353FE3FF, 0x5A5A86FF, 0x2C1ABFFF, 0x8E86BDFF,
                0x4A3896FF, 0x7765B2FF, 0x7F64CEFF, 0x6843C2FF, 0xAC90E9FF, 0x9C6EF9FF, 0x762BFEFF, 0x5F0AF2FF,
                0x421372FF, 0x5F1E9EFF, 0x7D28CAFF, 0xC175F0FF, 0x513562FF, 0xA36BC5FF, 0xD897FCFF, 0x7503A6FF,
                0xC050E8FF, 0xBE14E5FF, 0x50105AFF, 0xB82BC5FF, 0xE8B9ECFF, 0x9B46A1FF, 0xEA1AF8FF, 0x9A2099FF,
                0xE557E0FF, 0xAF06A1FF, 0xF917E0FF, 0x7E3C76FF, 0xFC78ECFF, 0xC74DB4FF, 0xDC32BCFF, 0x49103EFF,
                0x93217DFF, 0xF353C8FF, 0xD40D98FF, 0xBF2891FF, 0xDF94C8FF, 0xAA4289FF, 0x945D81FF, 0x751752FF,
                0xB6036DFF, 0xD6499CFF, 0x60324AFF, 0xD76FA4FF, 0x8C385DFF, 0xFA5094FF, 0xF22B70FF, 0xEE90B0FF,
                0xB01A4DFF, 0x841339FF, 0xC2899CFF, 0x580D26FF, 0xEA064CFF, 0xB96479FF, 0xDD4668FF, 0xF80334FF,
                0xD52144FF, 0xB13F55FF, 0xA81A31FF, 0xF46774FF, 0xEB4250FF, 0x66090EFF, 0xB71719FF, 0xBF3C3DFF,
        }, PaletteReducer.oklabMetric);
        generatePreloadCode(reducer.paletteMapping, "ManossusPreload.txt");
//        generatePreloadCode(Gdx.files.internal("OklabGamut.dat").readBytes(), "OklabGamut.txt");
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
