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
        int[] yam3 = {
                0x00000000, 0x000000FF, 0x0B0909FF, 0x1C1A19FF, 0x302D2CFF, 0x464241FF, 0x5A5755FF, 0x6F6B6AFF,
                0x847F7EFF, 0x999492FF, 0xACA7A5FF, 0xBEB9B7FF, 0xD1CCCAFF, 0xE2DDDBFF, 0xF3EEEBFF, 0xFFFDFBFF,
                0x62322CFF, 0x824C45FF, 0xA36760FF, 0xC7867DFF, 0xEBA69DFF, 0x644D47FF, 0x856B65FF, 0xA1867EFF,
                0xBC9F97FF, 0xDCBDB5FF, 0x664A37FF, 0x83644FFF, 0xA2816AFF, 0xC4A087FF, 0xE7C1A7FF, 0x6E5B43FF,
                0x937E64FF, 0xB19B7FFF, 0xCCB597FF, 0xE7CFB0FF, 0x66663DFF, 0x87875BFF, 0xA2A374FF, 0xC0C18FFF,
                0xE2E3AFFF, 0x4B5E38FF, 0x677D53FF, 0x869D70FF, 0xA6C090FF, 0xC9E4B1FF, 0x436E48FF, 0x639267FF,
                0x7EAF82FF, 0x95C899FF, 0xB1E7B5FF, 0x416766FF, 0x5D8585FF, 0x77A2A2FF, 0x95C2C1FF, 0xB4E4E3FF,
                0x2D4475FF, 0x4A659BFF, 0x6380BBFF, 0x7999D6FF, 0x8FB0F0FF, 0x4F416DFF, 0x6C5D8DFF, 0x8576A9FF,
                0x9F8FC6FF, 0xBEADE7FF, 0x573A6AFF, 0x735388FF, 0x906FA7FF, 0xB18DCAFF, 0xD4ADEEFF, 0x6D466CFF,
                0x916690FF, 0xAE80ADFF, 0xC898C7FF, 0xE5B2E4FF, 0x953229FF, 0xBC5044FF, 0xE77264FF, 0x885E4CFF,
                0xAA7D69FF, 0xCD9C87FF, 0x935C36FF, 0xB87B53FF, 0xE2A176FF, 0x9E7844FF, 0xC59D65FF, 0xE6BB80FF,
                0x90963DFF, 0xB4BB5EFF, 0xDAE280FF, 0x688D3FFF, 0x8BB460FF, 0xB4E086FF, 0x449F4DFF, 0x64C46CFF,
                0x83E68AFF, 0x489595FF, 0x69B9B8FF, 0x8CE1DFFF, 0x1D42A1FF, 0x3662C9FF, 0x4D7DEAFF, 0x654099FF,
                0x835CBCFF, 0xA37AE1FF, 0x7A3896FF, 0x9C56BCFF, 0xC479E6FF, 0x9A4799FF, 0xC167BFFF, 0xE182DFFF,
                0xC6221BFF, 0xDB362AFF, 0xF3493BFF, 0xB7503CFF, 0xC85D48FF, 0xDB6C56FF, 0xAF5B45FF, 0xC16A53FF,
                0xD77C63FF, 0xAC6A52FF, 0xC07B62FF, 0xD0886FFF, 0xB26B48FF, 0xC37A56FF, 0xD78B65FF, 0xB46C43FF,
                0xC87C52FF, 0xDD8E63FF, 0xC76D2AFF, 0xDD7F3CFF, 0xF0904BFF, 0xC5762FFF, 0xD8873FFF, 0xEF9B51FF,
                0xCB8223FF, 0xE19639FF, 0xF4A748FF, 0xC58C33FF, 0xD99E44FF, 0xEEB156FF, 0xC99D2AFF, 0xDCAF3DFF,
                0xF2C350FF, 0xCFB63EFF, 0xE4CA51FF, 0xF6DC61FF, 0xBEC22BFF, 0xD2D741FF, 0xE8ED56FF, 0xA9C13CFF,
                0xBCD650FF, 0xD3EE66FF, 0x9EC725FF, 0xB1DC3CFF, 0xC3F050FF, 0x89BF34FF, 0x9CD448FF, 0xB1EB5DFF,
                0x6AC829FF, 0x7EDE41FF, 0x8EF152FF, 0x54C339FF, 0x65D64BFF, 0x78EB5DFF, 0x29C742FF, 0x3FDA53FF,
                0x55F166FF, 0x49C88EFF, 0x5CDCA0FF, 0x6DEEB0FF, 0x2FC8ACFF, 0x44DBBDFF, 0x5AF0D2FF, 0x48C7C6FF,
                0x5DDDDCFF, 0x6EF0EEFF, 0x27A7CAFF, 0x3DBADEFF, 0x51CEF4FF, 0x2A81BDFF, 0x3B92D1FF, 0x50A8EAFF,
                0x002FCDFF, 0x0D41E1FF, 0x1950F4FF, 0x3834C0FF, 0x4143D2FF, 0x4E53E6FF, 0x5127C6FF, 0x603BDDFF,
                0x704FF6FF, 0x7536C2FF, 0x8446D6FF, 0x9455E9FF, 0x7E2ACDFF, 0x8E3CE0FF, 0xA04EF7FF, 0x8F39C5FF,
                0xA149DAFF, 0xB158ECFF, 0x9E2BCCFF, 0xB03DE0FF, 0xC44FF5FF, 0xA238C2FF, 0xB448D6FF, 0xCA5BEDFF,
                0xBE2FCEFF, 0xD443E4FF, 0xE653F6FF, 0xC139C2FF, 0xD449D4FF, 0xEA5CEAFF, 0xC72183FF, 0xDC3494FF,
                0xF347A7FF, 0xC43659FF, 0xD74567FF, 0xE95374FF, 0xFE000FFF, 0xE65239FF, 0xDA6746FF, 0xCF7859FF,
                0xE17C4BFF, 0xE58349FF, 0xF87C13FF, 0xF48A1EFF, 0xFC9500FF, 0xF6A727FF, 0xFFC400FF, 0xFFE12EFF,
                0xF3F900FF, 0xE0F826FF, 0xC4FD00FF, 0xA3F827FF, 0x75FC00FF, 0x49FA2AFF, 0x00FF3BFF, 0x31F7ABFF,
                0x00FFDAFF, 0x39F9F8FF, 0x00CCFEFF, 0x179BEFFF, 0x0000FFFF, 0x3F25F1FF, 0x6400FEFF, 0x8824F1FF,
                0x9D00FFFF, 0xAA28FAFF, 0xBF00FFFF, 0xCB25F8FF, 0xEC0FFFFF, 0xF42FFAFF, 0xFF00A9FF, 0xF52664FF,
        };

        int[] corn4 = {
                0x00000000, 0x000000FF, 0xFFFFFFFF, 0x858585FF, 0x434343FF, 0xCECECEFF, 0x242424FF, 0xA8A8A8FF,
                0x636363FF, 0xF5F5F4FF, 0x141414FF, 0x969696FF, 0x535353FF, 0xE2E2E2FF, 0x343434FF, 0xBBBBBBFF,
                0x747474FF, 0x4616A3FF, 0x7C5AF2FF, 0x5A33C2FF, 0xE16E00FF, 0xC850A0FF, 0x9A2C78FF, 0xE467B9FF,
                0xB74391FF, 0x598329FF, 0x94C563FF, 0x385C00FF, 0x6E9B3EFF, 0xACE07AFF, 0x4C741AFF, 0x5B7CB8FF,
                0x081D47FF, 0x96BCFFFF, 0x3A588EFF, 0x7194D3FF, 0x1B3463FF, 0xFFAE9EFF, 0x8D4C41FF, 0xD38678FF,
                0x632A22FF, 0xA86155FF, 0xEF9D8FFF, 0x9637BEFF, 0x6C008FFF, 0xB04EDBFF, 0x76D6BCFF, 0x076C58FF,
                0x4FAB93FF, 0x8DF0D5FF, 0x29836EFF, 0x66C5ACFF, 0xFA2B87FF, 0x001783FF, 0x295AD4FF, 0x0932A2FF,
                0x3C72F2FF, 0xC89B0EFF, 0xE2B331FF, 0xC996C9FF, 0x603960FF, 0x9F71A0FF, 0x371739FF, 0xE4AFE4FF,
                0x784E78FF, 0x00BF48FF, 0x2AD95DFF, 0x952FFFFF, 0x8617F4FF, 0xF15342FF, 0xC02B20FF, 0xDE4435FF,
                0x00B9E3FF, 0x29D3FFFF, 0xF545D3FF, 0xC312A7FF, 0x0024EBFF, 0xF7F6C5FF, 0x8B8960FF, 0x2D2B0CFF,
                0xCBC99CFF, 0x66643FFF, 0xA3A176FF, 0x5740A2FF, 0x917BEBFF, 0x351A72FF, 0x6C56BCFF, 0x4A328FFF,
                0x6F0937FF, 0xB9496FFF, 0x8B244BFF, 0xD65F86FF, 0xB2E03BFF, 0x8BB400FF, 0xCBFA55FF, 0xA2CE27FF,
                0xC211F0FF, 0x1B3948FF, 0xB1DBF3FF, 0x507386FF, 0x8AB2C7FF, 0x2E4E5FFF, 0x658A9EFF, 0x3400B4FF,
                0x442BD5FF, 0xF4A061FF, 0xC7793EFF, 0x9C551AFF, 0xE39154FF, 0xB158B7FF, 0xF895FFFF, 0x85348CFF,
                0xCA6ED1FF, 0x5B0C62FF, 0x9F4AA6FF, 0x6CC781FF, 0x005E28FF, 0x489D5DFF, 0x84E198FF, 0x23763CFF,
                0x5EB773FF, 0xE91C55FF, 0x3A7CCFFF, 0x1655A1FF, 0x4F93EAFF, 0x2D6DBDFF, 0x7A5158FF, 0xBB8B93FF,
                0x512F35FF, 0xFFCCD4FF, 0x91666DFF, 0x280E14FF, 0x58F631FF, 0x2BC900FF, 0x8037D2FF, 0x5B00A2FF,
                0x984FF0FF, 0x7227C0FF, 0x51F4F2FF, 0x18C8C7FF, 0xE23E9EFF, 0xB20E77FF, 0xFF57B7FF, 0xEBE583FF,
                0x817B22FF, 0xC0BB5DFF, 0xFFFE99FF, 0x989339FF, 0xDAD574FF, 0xAA9AE2FF, 0x493B71FF, 0x8374B5FF,
                0x271846FF, 0xC2B2FCFF, 0x5E508BFF, 0xD96160FF, 0xA93C3EFF, 0xF57775FF, 0x7B171FFF, 0xC65252FF,
                0xDF49E9FF, 0xB11DBCFF, 0xFC62FFFF, 0xCD3AD8FF, 0x6A8E7BFF, 0x112D20FF, 0xA6D0B9FF, 0x466856FF,
                0x7FA591FF, 0x264435FF, 0x737CFFFF, 0x251484FF, 0x5356D3FF, 0x3530A3FF, 0x676DF0FF, 0xE79000FF,
                0xFFA928FF, 0xA15084FF, 0xE88BC5FF, 0x762E5EFF, 0xBB669DFF, 0x4A0939FF, 0xFFA3E0FF, 0x2E7700FF,
                0x67B842FF, 0xA5FF7FFF, 0x428E1AFF, 0x7ED359FF, 0xAF1DFFFF, 0x2C739BFF, 0x66B2E0FF, 0x004E72FF,
                0x428AB5FF, 0x7ECCFCFF, 0x1E658BFF, 0x2F1EE9FF, 0xD6A982FF, 0x6A4728FF, 0xAB815DFF, 0x432606FF,
                0xF0C198FF, 0x835D3CFF, 0x7138A1FF, 0xB173EAFF, 0x4A1072FF, 0x894EBCFF, 0xCB8BFFFF, 0x27CB9EFF,
                0x46E5B6FF, 0x00BA8EFF, 0xD2356CFF, 0xF04D83FF, 0x0092FFFF, 0xE1D632FF, 0xFCF14FFF, 0x9D90ABFF,
                0x3D3447FF, 0xE0D2F0FF, 0x776C84FF, 0x18111FFF, 0xB6A9C5FF, 0x7035E3FF, 0x854EFFFF, 0x6223CFFF,
                0xC95221FF, 0xE66938FF, 0xB74510FF, 0xCF47B5FF, 0xA01E8BFF, 0xEC5ED0FF, 0xC4EDA5FF, 0x608045FF,
                0x9BC07DFF, 0x3E5B25FF, 0x76985AFF, 0x1E3600FF, 0x323B84FF, 0x6676CDFF, 0x151454FF, 0x44509FFF,
                0x7B8EE9FF, 0x262D72FF, 0x4D1021FF, 0x934856FF, 0xDA8190FF, 0x672534FF, 0xAE5D6BFF, 0xF799A8FF,
                0x7ED400FF, 0x95EF24FF, 0xC951FCFF, 0x9D26CCFF, 0xB741E9FF, 0x79D2D3FF, 0x146A6BFF, 0x54A9AAFF,
        };


        PaletteReducer reducer = new PaletteReducer(corn4, PaletteReducer.oklabCarefulMetric);
        generatePreloadCode(reducer.paletteMapping, "Corn4OklabPreload.txt");
        Gdx.files.local("Corn4OklabPreload.dat").writeBytes(reducer.paletteMapping, false);
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
