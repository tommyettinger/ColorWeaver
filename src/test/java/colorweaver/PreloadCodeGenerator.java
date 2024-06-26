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
//        int[] yam3_old = {
//                0x00000000, 0x000000FF, 0x0B0909FF, 0x1C1A19FF, 0x302D2CFF, 0x464241FF, 0x5A5755FF, 0x6F6B6AFF,
//                0x847F7EFF, 0x999492FF, 0xACA7A5FF, 0xBEB9B7FF, 0xD1CCCAFF, 0xE2DDDBFF, 0xF3EEEBFF, 0xFFFDFBFF,
//                0x62322CFF, 0x824C45FF, 0xA36760FF, 0xC7867DFF, 0xEBA69DFF, 0x644D47FF, 0x856B65FF, 0xA1867EFF,
//                0xBC9F97FF, 0xDCBDB5FF, 0x664A37FF, 0x83644FFF, 0xA2816AFF, 0xC4A087FF, 0xE7C1A7FF, 0x6E5B43FF,
//                0x937E64FF, 0xB19B7FFF, 0xCCB597FF, 0xE7CFB0FF, 0x66663DFF, 0x87875BFF, 0xA2A374FF, 0xC0C18FFF,
//                0xE2E3AFFF, 0x4B5E38FF, 0x677D53FF, 0x869D70FF, 0xA6C090FF, 0xC9E4B1FF, 0x436E48FF, 0x639267FF,
//                0x7EAF82FF, 0x95C899FF, 0xB1E7B5FF, 0x416766FF, 0x5D8585FF, 0x77A2A2FF, 0x95C2C1FF, 0xB4E4E3FF,
//                0x2D4475FF, 0x4A659BFF, 0x6380BBFF, 0x7999D6FF, 0x8FB0F0FF, 0x4F416DFF, 0x6C5D8DFF, 0x8576A9FF,
//                0x9F8FC6FF, 0xBEADE7FF, 0x573A6AFF, 0x735388FF, 0x906FA7FF, 0xB18DCAFF, 0xD4ADEEFF, 0x6D466CFF,
//                0x916690FF, 0xAE80ADFF, 0xC898C7FF, 0xE5B2E4FF, 0x953229FF, 0xBC5044FF, 0xE77264FF, 0x885E4CFF,
//                0xAA7D69FF, 0xCD9C87FF, 0x935C36FF, 0xB87B53FF, 0xE2A176FF, 0x9E7844FF, 0xC59D65FF, 0xE6BB80FF,
//                0x90963DFF, 0xB4BB5EFF, 0xDAE280FF, 0x688D3FFF, 0x8BB460FF, 0xB4E086FF, 0x449F4DFF, 0x64C46CFF,
//                0x83E68AFF, 0x489595FF, 0x69B9B8FF, 0x8CE1DFFF, 0x1D42A1FF, 0x3662C9FF, 0x4D7DEAFF, 0x654099FF,
//                0x835CBCFF, 0xA37AE1FF, 0x7A3896FF, 0x9C56BCFF, 0xC479E6FF, 0x9A4799FF, 0xC167BFFF, 0xE182DFFF,
//                0xC6221BFF, 0xDB362AFF, 0xF3493BFF, 0xB7503CFF, 0xC85D48FF, 0xDB6C56FF, 0xAF5B45FF, 0xC16A53FF,
//                0xD77C63FF, 0xAC6A52FF, 0xC07B62FF, 0xD0886FFF, 0xB26B48FF, 0xC37A56FF, 0xD78B65FF, 0xB46C43FF,
//                0xC87C52FF, 0xDD8E63FF, 0xC76D2AFF, 0xDD7F3CFF, 0xF0904BFF, 0xC5762FFF, 0xD8873FFF, 0xEF9B51FF,
//                0xCB8223FF, 0xE19639FF, 0xF4A748FF, 0xC58C33FF, 0xD99E44FF, 0xEEB156FF, 0xC99D2AFF, 0xDCAF3DFF,
//                0xF2C350FF, 0xCFB63EFF, 0xE4CA51FF, 0xF6DC61FF, 0xBEC22BFF, 0xD2D741FF, 0xE8ED56FF, 0xA9C13CFF,
//                0xBCD650FF, 0xD3EE66FF, 0x9EC725FF, 0xB1DC3CFF, 0xC3F050FF, 0x89BF34FF, 0x9CD448FF, 0xB1EB5DFF,
//                0x6AC829FF, 0x7EDE41FF, 0x8EF152FF, 0x54C339FF, 0x65D64BFF, 0x78EB5DFF, 0x29C742FF, 0x3FDA53FF,
//                0x55F166FF, 0x49C88EFF, 0x5CDCA0FF, 0x6DEEB0FF, 0x2FC8ACFF, 0x44DBBDFF, 0x5AF0D2FF, 0x48C7C6FF,
//                0x5DDDDCFF, 0x6EF0EEFF, 0x27A7CAFF, 0x3DBADEFF, 0x51CEF4FF, 0x2A81BDFF, 0x3B92D1FF, 0x50A8EAFF,
//                0x002FCDFF, 0x0D41E1FF, 0x1950F4FF, 0x3834C0FF, 0x4143D2FF, 0x4E53E6FF, 0x5127C6FF, 0x603BDDFF,
//                0x704FF6FF, 0x7536C2FF, 0x8446D6FF, 0x9455E9FF, 0x7E2ACDFF, 0x8E3CE0FF, 0xA04EF7FF, 0x8F39C5FF,
//                0xA149DAFF, 0xB158ECFF, 0x9E2BCCFF, 0xB03DE0FF, 0xC44FF5FF, 0xA238C2FF, 0xB448D6FF, 0xCA5BEDFF,
//                0xBE2FCEFF, 0xD443E4FF, 0xE653F6FF, 0xC139C2FF, 0xD449D4FF, 0xEA5CEAFF, 0xC72183FF, 0xDC3494FF,
//                0xF347A7FF, 0xC43659FF, 0xD74567FF, 0xE95374FF, 0xFE000FFF, 0xE65239FF, 0xDA6746FF, 0xCF7859FF,
//                0xE17C4BFF, 0xE58349FF, 0xF87C13FF, 0xF48A1EFF, 0xFC9500FF, 0xF6A727FF, 0xFFC400FF, 0xFFE12EFF,
//                0xF3F900FF, 0xE0F826FF, 0xC4FD00FF, 0xA3F827FF, 0x75FC00FF, 0x49FA2AFF, 0x00FF3BFF, 0x31F7ABFF,
//                0x00FFDAFF, 0x39F9F8FF, 0x00CCFEFF, 0x179BEFFF, 0x0000FFFF, 0x3F25F1FF, 0x6400FEFF, 0x8824F1FF,
//                0x9D00FFFF, 0xAA28FAFF, 0xBF00FFFF, 0xCB25F8FF, 0xEC0FFFFF, 0xF42FFAFF, 0xFF00A9FF, 0xF52664FF,
//        };
//
//        int[] munsellish255 = {
//                0x00000000, 0x000000FF, 0x404040FF, 0x808080FF, 0xABABABFF, 0xD5D5D5FF, 0xFFFFFFFF, 0xFF94A8FF,
//                0xFDA4B1FF, 0xF67E92FF, 0xFED1DDFF, 0xEFB3BDFF, 0xD9929AFF, 0xFADBE3FF, 0xDFBAC4FF, 0xF4E7F1FF,
//                0xD9C3C9FF, 0xFF003EFF, 0xEC2E54FF, 0xB30039FF, 0x810031FF, 0xDD4A62FF, 0x9C243FFF, 0x671135FF,
//                0xCA99A0FF, 0xC16570FF, 0x724251FF, 0x4B2B35FF, 0xAB9198FF, 0x7E5C65FF, 0xFF9576FF, 0xFDA397FF,
//                0xFA7C7FFF, 0xFFD0C1FF, 0xF2B5A5FF, 0xDD918EFF, 0xFDDCDEFF, 0xDEB8BCFF, 0xDEBCB8FF, 0xFF512EFF,
//                0xF16F4EFF, 0xDC320AFF, 0xE37C64FF, 0xCD4525FF, 0xD28477FF, 0xB51C01FF, 0x890014FF, 0xC8897DFF,
//                0xAA4742FF, 0x662129FF, 0xB78F88FF, 0x925854FF, 0x603434FF, 0xFF8C38FF, 0xFF8F2FFF, 0xE86A00FF,
//                0xFFBE9BFF, 0xF09C73FF, 0xD7712DFF, 0xAE5000FF, 0x79200AFF, 0xD5855EFF, 0x995538FF, 0x63290EFF,
//                0xB88D7AFF, 0x8D5C4FFF, 0x5B372FFF, 0xAB938DFF, 0x7D625FFF, 0xFFCE71FF, 0xFFAF2AFF, 0xEE9300FF,
//                0xFFD198FF, 0xF7B56EFF, 0xE39331FF, 0xF7D2B3FF, 0xBC7700FF, 0x854E00FF, 0xE0A775FF, 0xA57748FF,
//                0x6C421FFF, 0xCDA899FF, 0xA07765FF, 0x664D3BFF, 0xFFCC00FF, 0xFFD95DFF, 0xF0BB1AFF, 0xD29F00FF,
//                0xFADF98FF, 0xEABB56FF, 0xCB9F3CFF, 0xF7E1BDFF, 0xDEBF84FF, 0xC1A162FF, 0xF9E7E4FF, 0xD5C7B7FF,
//                0xB1892FFF, 0x8A6713FF, 0x583A14FF, 0xF7E800FF, 0xF5E736FF, 0xD3C81CFF, 0xB6AB00FF, 0xFAE686FF,
//                0xD0C556FF, 0xB4A730FF, 0xF5E6ABFF, 0xD6C37DFF, 0xA78F25FF, 0x7A6E14FF, 0x4B3D00FF, 0xA79871FF,
//                0x786840FF, 0x484121FF, 0xAC9684FF, 0x766352FF, 0xB0ED00FF, 0xCBED54FF, 0x91B615FF, 0x558400FF,
//                0xD7E997FF, 0x9BAF5CFF, 0xE3E4BBFF, 0xAAAD8DFF, 0x336D00FF, 0x57721AFF, 0x294705FF, 0x627052FF,
//                0x3E452CFF, 0x20ED30FF, 0x80ED83FF, 0x20B14DFF, 0x1A8313FF, 0x005807FF, 0xD1F5CAFF, 0x99DC98FF,
//                0x61AA70FF, 0x327643FF, 0x124A1FFF, 0x00FCAAFF, 0x32E4B3FF, 0x00A375FF, 0xAAEFD2FF, 0x6BC49FFF,
//                0x31906EFF, 0x0D6649FF, 0x0F4034FF, 0xCEE6E0FF, 0x9CBAB3FF, 0x609A7AFF, 0x3C6C4FFF, 0x1F4A37FF,
//                0xE0FBE6FF, 0xC1DCCAFF, 0x87AD92FF, 0x597F64FF, 0x65FFDEFF, 0x1EE9DEFF, 0x00A796FF, 0x8BF2F1FF,
//                0x5CC0C6FF, 0x209296FF, 0x176063FF, 0x004043FF, 0x00D1FCFF, 0x009BD2FF, 0x9CE3FCFF, 0x5EBDE3FF,
//                0x1E8DB4FF, 0x005E7FFF, 0x003D53FF, 0x3272EAFF, 0x63AFF7FF, 0x2C7FE4FF, 0x0D38B0FF, 0xA8D3FFFF,
//                0x84B7EBFF, 0x267EC9FF, 0x16457FFF, 0xCBDEFBFF, 0x9FB2D3FF, 0x5A7DA2FF, 0x3B516EFF, 0x21354EFF,
//                0xECE9F8FF, 0xC6C5D9FF, 0x9499AAFF, 0x61687BFF, 0x534DEDFF, 0x909BF5FF, 0x626DD0FF, 0x352FA4FF,
//                0xC4CDFFFF, 0x98A4DFFF, 0x5960AFFF, 0x2F2A73FF, 0xD1D6FDFF, 0x9EA5CEFF, 0x545F9BFF, 0x9240F8FF,
//                0xAB91EEFF, 0x7C47D0FF, 0x570D97FF, 0xD6C8FFFF, 0xAE9DDFFF, 0x7552B8FF, 0x482B74FF, 0xDED1F6FF,
//                0xAFA0CFFF, 0x7359A0FF, 0xDA32FDFF, 0xDB9FEFFF, 0xBB51D6FF, 0x86209EFF, 0x620079FF, 0xF0C7FAFF,
//                0xCCA2E0FF, 0xAC64BEFF, 0x743E86FF, 0x4F1E63FF, 0xECD6F7FF, 0xC0A6CCFF, 0x9B729FFF, 0x6B4C73FF,
//                0x462B48FF, 0xF2E7F7FF, 0xD3C3DBFF, 0xA395AAFF, 0x73627CFF, 0xFD1BD7FF, 0xD147B7FF, 0x9B1786FF,
//                0x6C0066FF, 0xD78EC2FF, 0xBF5BAEFF, 0x823973FF, 0x581C58FF, 0xC696BCFF, 0xAD6A9AFF, 0xFFB4F1FF,
//                0xFB9AD7FF, 0xEB81C1FF, 0xFDC0E5FF, 0xEBA9CFFF, 0xDC8EB1FF, 0xF5D3E9FF, 0xDDAFC8FF, 0xFF2A9AFF,
//                0xE4338AFF, 0xB00070FF, 0x730052FF, 0xD74B8EFF, 0x922D61FF, 0x631244FF, 0xCB97ABFF, 0xBD638AFF,
//        };

        PaletteReducer reducer = new PaletteReducer(Coloring.SNUGGLY255, PaletteReducer.oklabCarefulMetric);
        generatePreloadCode(reducer.paletteMapping, "SnugglyOklabPreload.txt");
        Gdx.files.local("SnugglyOklabPreload.dat").writeBytes(reducer.paletteMapping, false);
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
        Gdx.files.local(filename).writeString(sb.toString(), false, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
    }
}
