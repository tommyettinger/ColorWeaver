package colorweaver;

import colorweaver.tools.StringKit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class BigPaletteTransformer extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("TRANSFOOOORM!");
        config.setWindowedMode(640, 320);
        config.setIdleFPS(10);
        config.setResizable(true);
        new Lwjgl3Application(new BigPaletteTransformer(), config);
    }

    private int[] PALETTE = new int[]{
            0x00000000, 0x000000FF, 0xFFFFFFFF, 0x888888FF, 0x444444FF, 0xCCCCCCFF, 0x222222FF, 0xAAAAAAFF,
            0x666666FF, 0xEEEEEEFF, 0x111111FF, 0x999999FF, 0x555555FF, 0xDDDDDDFF, 0x333333FF, 0xBBBBBBFF,
            0x777777FF, 0x006093FF, 0x350035FF, 0x00C78FFF, 0x0C0B04FF, 0x994CFFFF, 0x074150FF, 0xE7BFFFFF,
            0x070000FF, 0x0D841CFF, 0x082C51FF, 0xDB9FFFFF, 0xA99112FF, 0x783300FF, 0xBC1056FF, 0x002E15FF,
            0xC5B41FFF, 0x140123FF, 0x1A9BA6FF, 0x5C3900FF, 0x060700FF, 0x6D52FFFF, 0x550856FF, 0x00E7DFFF,
            0x061F0AFF, 0xEB7D08FF, 0x0D5A78FF, 0x007335FF, 0xD784FDFF, 0x6C240BFF, 0x92F924FF, 0x00020FFF,
            0xC300B3FF, 0x08382EFF, 0xFEAE61FF, 0x260E06FF, 0x00A9D1FF, 0x806213FF, 0x3A015CFF, 0x60C2FFFF,
            0xFF3D63FF, 0x00551EFF, 0x0B0213FF, 0x008E83FF, 0x3E370DFF, 0xFFB995FF, 0x001E2DFF, 0xA18EFEFF,
            0xA20C53FF, 0x7A0FDAFF, 0xC1A475FF, 0x7A8B04FF, 0x43069FFF, 0x44250BFF, 0x12023EFF, 0xFF3DAFFF,
            0x0E737BFF, 0xFE966AFF, 0x4885FFFF, 0x71007AFF, 0x5DFFE0FF, 0xC75000FF, 0x85D6FCFF, 0x340611FF,
            0x13C234FF, 0x655112FF, 0xAD3400FF, 0x440E07FF, 0x78C91DFF, 0xBC40FCFF, 0xFFD187FF, 0x1678C4FF,
            0x410077FF, 0xFEA6C3FF, 0x9CA207FF, 0x6000CDFF, 0xC33100FF, 0x262E08FF, 0xACB0FFFF, 0x20051BFF,
            0x00A98AFF, 0x444D0FFF, 0xFFDFC1FF, 0x446EFFFF, 0x6D0A23FF, 0x281B08FF, 0x16085FFF, 0xF169FCFF,
            0xCC0277FF, 0xE2B91EFF, 0x92072EFF, 0x1763BFFF, 0x420040FF, 0x06D3A1FF, 0xDA6600FF, 0x074672FF,
            0xA40425FF, 0xEEFFA2FF, 0xA8098EFF, 0xFFC7D3FF, 0x956812FF, 0x830055FF, 0x99FFB6FF, 0xB421FDFF,
            0x695E13FF, 0x27BAFDFF, 0xFC0F5EFF, 0x0E4A1CFF, 0xEDDA1EFF, 0x187B99FF, 0x1B2076FF, 0x8C82FFFF,
            0x840978FF, 0xAEFFF2FF, 0x9E4E0FFF, 0x60A104FF, 0xFDCFFFFF, 0x56260BFF, 0x99E723FF, 0xFF52D1FF,
            0x0F6541FF, 0x042641FF, 0xFF7ACEFF, 0xA88012FF, 0x5E0090FF, 0xE60035FF, 0x183C09FF, 0x29002DFF,
            0x10B69AFF, 0xA700D3FF, 0x530323FF, 0x00E36FFF, 0x105354FF, 0xCEFE27FF, 0x8A440AFF, 0xE59300FF,
            0x4650FEFF, 0xFD580BFF, 0xBF9BFFFF, 0x820B27FF, 0x7DFF86FF, 0x00A6FFFF, 0xFE2700FF, 0x3F4300FF,
            0x13846AFF, 0x6193FFFF, 0x052F36FF, 0x1DD0DEFF, 0x280EDAFF, 0xFF6B88FF, 0x1A6A0CFF, 0xFF7F6CFF,
            0x1082EFFF, 0x22F6EDFF, 0xF902C2FF, 0x2E099BFF, 0x8300AEFF, 0x87ACFFFF, 0x5F0A2AFF, 0x00F14FFF,
            0xCC64FFFF, 0xE60095FF, 0x5314FFFF, 0xC26500FF, 0x06181AFF, 0x200500FF, 0x06715AFF, 0x8937FDFF,
            0x03112AFF, 0xFF348AFF, 0x109C5BFF, 0xFDEEA7FF, 0xFD90A8FF, 0xDA15F7FF, 0x478719FF, 0xFFA0F2FF,
            0x710097FF, 0x538E5DFF, 0x010100FF, 0xD7FFD8FF, 0x0B3984FF, 0x936FFFFF, 0x040525FF, 0x00F1A5FF,
            0xA8E4FFFF, 0xFF65B6FF, 0x5E0074FF, 0x70720CFF, 0x3D00FAFF, 0x00AD4DFF, 0x790EFDFF, 0x75BFC6FF,
            0x3E00C0FF, 0x0F0650FF, 0x6772FFFF, 0x0DB85DFF, 0xB80E22FF, 0x150311FF, 0xE146FFFF, 0x13017FFF,
            0xFDBF2EFF, 0xC5B8FFFF, 0xB6C519FF, 0xFF7E29FF, 0x39B400FF, 0x02479FFF, 0x07A423FF, 0x00CF67FF,
            0xF0A60AFF, 0x2F230AFF, 0xC4D218FF, 0x0E0EB9FF, 0x11345AFF, 0x0A009BFF, 0x55358FFF, 0x9CB01EFF,
            0xAB8071FF, 0x947B15FF, 0xFF7CF7FF, 0x18B8C2FF, 0xE060B7FF, 0x1094E1FF, 0xFF8F28FF, 0x0087C7FF,
            0x5DA8FFFF, 0x7D6B46FF, 0xFFEF3AFF, 0x00D61DFF, 0x8E2C0AFF, 0x0052A6FF, 0xFD0886FF, 0xD800BBFF,
            0x7947D2FF, 0x00270BFF, 0x16C9BEFF, 0x13FF95FF, 0xA1D363FF, 0x4900DDFF, 0x0050D6FF, 0x598BB6FF,
            0xFE7F9EFF, 0xFF5678FF, 0x005F25FF, 0x0F6670FF, 0x3143FFFF, 0xFEA48EFF, 0xBB5AFFFF, 0x500041FF,
            0x11DDC3FF, 0xD37C12FF, 0x6D0A53FF, 0x8AC5FFFF, 0x1031ACFF, 0xFF6945FF, 0x9D0077FF, 0x5D0300FF,
            0x78410DFF, 0xB96079FF, 0x9A00BDFF, 0x1C66FFFF, 0x0C31FFFF, 0x85628FFF, 0x0E4A43FF, 0xDA0059FF,
            0x566D2CFF, 0x270045FF, 0x634D6EFF, 0x68DE20FF, 0xF413F9FF, 0x37D5FFFF, 0x0C4132FF, 0xDAA87FFF,
            0xFFB6CFFF, 0x0E205CFF, 0xFFE15EFF, 0xFF90D5FF, 0xAE540DFF, 0x7C5467FF, 0x00FF2EFF, 0xAA54A9FF,
            0x455C00FF, 0xC29308FF, 0x81334BFF, 0x00992DFF, 0xFF46FEFF, 0xBBFF88FF, 0x1FFFC5FF, 0x4966A2FF,
            0xE23A0BFF, 0x115AE8FF, 0x099158FF, 0xAF5C5DFF, 0x9A3D74FF, 0x410626FF, 0xE2C778FF, 0xAB77BDFF,
            0xE54E00FF, 0x5503B1FF, 0xB8CEFFFF, 0x5EB374FF, 0x6A563AFF, 0x88D2BFFF, 0xCA0036FF, 0xC184B0FF,
            0xBA58CAFF, 0x5D38FEFF, 0x2C0076FF, 0xBE00DBFF, 0x9CB88EFF, 0x9C9EC5FF, 0x2D2236FF, 0x753E9FFF,
            0xC979FFFF, 0xFBFF25FF, 0x6B2E49FF, 0xD82A00FF, 0xD7675FFF, 0x5856B4FF, 0x613DD9FF, 0x190CF9FF,
            0xAD3392FF, 0xA800F5FF, 0x69BA94FF, 0x007F4AFF, 0xA84F74FF, 0xC9BA62FF, 0x9B8BC1FF, 0x7E975DFF,
            0x2D0755FF, 0x692FBEFF, 0xCCDCBAFF, 0xA2E2A1FF, 0x6D88D6FF, 0x173BCDFF, 0x543B60FF, 0x930050FF,
            0x006C99FF, 0xB8F0FFFF, 0x8965AFFF, 0x9106E0FF, 0x46538FFF, 0x70B10FFF, 0x818552FF, 0xFF4336FF,
            0x5D5A86FF, 0xD6EE11FF, 0x001B46FF, 0x614500FF, 0x5C2044FF, 0x82C773FF, 0x777D07FF, 0xDC94BFFF,
            0x6F5D99FF, 0x4C3137FF, 0x0D2D82FF, 0x422753FF, 0x3F4A61FF, 0xC24B71FF, 0x532974FF, 0xC68B6EFF,
            0xDF4E8FFF, 0xFF28D3FF, 0x5F68E0FF, 0xA68C52FF, 0x871900FF, 0x4A9186FF, 0xA34A55FF, 0xB85DA4FF,
            0x636CAEFF, 0xC079D2FF, 0xB1EA6EFF, 0x983542FF, 0xE5126FFF, 0x9270A9FF, 0x43E6FFFF, 0x3F31ACFF,
            0xD788AEFF, 0x9BC98EFF, 0x673C63FF, 0x9BB4D7FF, 0x852A7FFF, 0xB103A9FF, 0xB4748FFF, 0x7A4950FF,
            0x7373D7FF, 0x89931AFF, 0x40745CFF, 0xD30B96FF, 0xB145D2FF, 0x9146A0FF, 0x5844A2FF, 0x144E7CFF,
            0xE5717BFF, 0x893E68FF, 0xE44856FF, 0xFEFFD2FF, 0x5133B5FF, 0x231B3DFF, 0x061703FF, 0xC28DE2FF,
            0x82DC97FF, 0xF80836FF, 0x7A5AC3FF, 0xA17668FF, 0xFF55A2FF, 0x7F4A8AFF, 0x393767FF, 0xA12700FF,
            0x61AAB0FF, 0x5E7B61FF, 0x72AFC4FF, 0x6D96BFFF, 0x3D46C0FF, 0x6304E6FF, 0x006BDAFF, 0xD741A8FF,
            0x97A55FFF, 0xC7ABD5FF, 0x332C7FFF, 0x684447FF, 0x9B6CCCFF, 0xA043C2FF, 0x071634FF, 0x331E58FF,
            0xBA4290FF, 0xBFC99CFF, 0x91EADAFF, 0x6DF6FFFF, 0x8E5CDAFF, 0xE4E59BFF, 0x647CA6FF, 0x9DA78FFF,
            0x4D467AFF, 0xC84243FF, 0x82BA63FF, 0xD3D655FF, 0x4E6553FF, 0x82764FFF, 0x52F661FF, 0xD5938AFF,
            0x82666BFF, 0x3A4084FF, 0x68A583FF, 0xB0B056FF, 0x88F4A4FF, 0x682769FF, 0x5F6D8DFF, 0xE14DD0FF,
            0x9431B7FF, 0x8995E3FF, 0x40202CFF, 0x231629FF, 0x68A651FF, 0x3F2D8FFF, 0x663B85FF, 0xBBF1D4FF,
            0x4B5230FF, 0xBB3465FF, 0x471D70FF, 0xE1724DFF, 0xDF75DCFF, 0x61E677FF, 0xD038D6FF, 0xC8634BFF,
            0xB2878FFF, 0x603A35FF, 0x5461C9FF, 0xD89154FF, 0x825527FF, 0x86C5DBFF, 0xBB783DFF, 0x533053FF,
            0x393F37FF, 0x64A3D5FF, 0x985943FF, 0xB2A2DBFF, 0xC164D6FF, 0x934D40FF, 0x342A42FF, 0xDE638FFF,
            0x3D469CFF, 0x8D259CFF, 0xA46B37FF, 0x986D81FF, 0x529E55FF, 0x732598FF, 0xAD3344FF, 0xDCDC7DFF,
            0xE0B8ACFF, 0x295B54FF, 0x8D61FEFF, 0x0AE49AFF, 0xD8D2FFFF, 0x0E8EA6FF, 0xFFDEFFFF, 0xBC0078FF,
    };
    private short[] paletteMapping = new short[0x40000];

    public void exact() {
        Arrays.fill(paletteMapping, (short) 0);
        final int plen = PALETTE.length;
        int colorCount = plen;
        int color, c2;
        double dist;
        for (int i = 0; i < plen; i++) {
            color = PALETTE[i];
            if ((color & 0x80) != 0) {
                paletteMapping[(color >>> 14 & 0x3F000) | (color >>> 12 & 0xFC0) | (color >>> 10 & 0x3F)] = (short) i;
            }
        }
        int rr, gg, bb;
        for (int r = 0; r < 64; r++) {
            rr = (r << 2 | r >>> 4);
            for (int g = 0; g < 64; g++) {
                gg = (g << 2 | g >>> 4);
                for (int b = 0; b < 64; b++) {
                    c2 = r << 12 | g << 6 | b;
                    if (paletteMapping[c2] == 0) {
                        bb = (b << 2 | b >>> 4);
                        dist = 0x1e256;
                        for (int i = 1; i < plen; i++) {
                            if (dist > (dist = Math.min(dist, PaletteReducer.oklabCarefulMetric.difference(PALETTE[i], rr, gg, bb))))
                                paletteMapping[c2] = (short) i;
                        }
                    }
                }
            }
        }
    }

    public void create() {
        Gdx.files.local("palettes/gen/txt/").mkdirs();
        Gdx.files.local("palettes/genBig/").mkdirs();
//        for(FileHandle hex : hexes) {
//        FileHandle hex = Gdx.files.local("palettes/hex/"+HexGenerator.NAME+".hex");{
//            String name = hex.nameWithoutExtension().toLowerCase();
        String name = "halexander-512";
        StringBuilder sb = new StringBuilder((1 + 12 * 8) * (PALETTE.length + 7 >>> 3));
        for (int i = 0; i < (PALETTE.length + 7 >>> 3); i++) {
            for (int j = 0; j < 8 && (i << 3 | j) < PALETTE.length; j++) {
                sb.append("0x").append(StringKit.hex(PALETTE[i << 3 | j]).toUpperCase()).append(", ");
            }
            sb.append('\n');
        }
        Gdx.files.local("palettes/gen/txt/" + name + ".txt").writeString(sb.toString(), false);
        sb.setLength(0);

        PixmapIO.PNG png = new PixmapIO.PNG();
        exact();
        Pixmap pix = new Pixmap(512, 1, Pixmap.Format.RGBA8888);
        for (int i = 1; i < PALETTE.length; i++) {
            pix.drawPixel(i - 1, 0, PALETTE[i]);
        }
        pix.drawPixel(511, 0, 0);
        try {
            png.write(Gdx.files.local("palettes/genBig/" + name + ".png"), pix);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Pixmap p2 = new Pixmap(4096, 64, Pixmap.Format.RGBA8888);
        for (int r = 0; r < 64; r++) {
            for (int b = 0; b < 64; b++) {
                for (int g = 0; g < 64; g++) {
                    p2.drawPixel(r << 6 | b, g, PALETTE[paletteMapping[((r << 12) & 0x3F000) | ((g << 6) & 0xFC0) | b] & 511]);
                }
            }
        }
        try {
            png.write(Gdx.files.local("palettes/genBig/" + name + "_GLSL.png"), p2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gdx.app.exit();
    }
}
