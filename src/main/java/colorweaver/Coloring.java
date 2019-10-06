package colorweaver;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectIntMap;

/**
 * Created by Tommy Ettinger on 11/4/2017.
 */
public class Coloring {
    public static final int[] CW_PALETTE = {
            0x00000000, 0x444444ff, 0x000000ff, 0x88ffff00, 0x212121ff, 0x00ff00ff, 0x0000ffff, 0x080808ff,
            0x1f1f1fff, 0x3f3f3fff, 0x5f5f5fff, 0x7f7f7fff, 0x9f9f9fff, 0xbfbfbfff, 0xdfdfdfff, 0xffffffff,
            0xa11616ff, 0x923535ff, 0xbe1111ff, 0xe21414ff, 0xb76363ff, 0xf43b3bff, 0xed8383ff, 0xe4a7a7ff,
            0xa35113ff, 0xa36b41ff, 0xc15b0fff, 0xe56d11ff, 0xcc9b75ff, 0xf68a39ff, 0xf1b283ff, 0xecc7abff,
            0x8a582cff, 0x937358ff, 0xa45e21ff, 0xc37027ff, 0xbea289ff, 0xdf8f47ff, 0xdeba9bff, 0xe2cdbbff,
            0xa8701eff, 0xb18b55ff, 0xc77f17ff, 0xed971bff, 0xddbc8bff, 0xfbaf3fff, 0xf7cd8fff, 0xf3dbb6ff,
            0x9f8810ff, 0xae9f4bff, 0xbca00cff, 0xdfbf0eff, 0xdccf85ff, 0xf2d536ff, 0xf1df81ff, 0xf0e7afff,
            0xa9a915ff, 0xc1c05bff, 0xc9c810ff, 0xeeee13ff, 0xf2f197ff, 0xfcfc3aff, 0xfcfb88ff, 0xfafab7ff,
            0x82a113ff, 0x9db252ff, 0x98be0fff, 0xb5e211ff, 0xcfe28cff, 0xcbf439ff, 0xdbf386ff, 0xe5f2b3ff,
            0x72a10dff, 0x90b049ff, 0x86be0aff, 0x9fe20cff, 0xc2de83ff, 0xb8f435ff, 0xcef27eff, 0xdcf1aeff,
            0x68a62aff, 0x8fb766ff, 0x73c51fff, 0x88ea25ff, 0xc1e59eff, 0xa1f946ff, 0xc9f79cff, 0xdaf5c0ff,
            0x269d16ff, 0x51a347ff, 0x24ba11ff, 0x2bdd14ff, 0x85cd7cff, 0x4ff13aff, 0x92ee86ff, 0xb6ebafff,
            0x269940ff, 0x56a167ff, 0x1cb53fff, 0x21d74bff, 0x8acd9aff, 0x43ec6bff, 0x95eaa9ff, 0xb8eac4ff,
            0x1ea574ff, 0x54af8eff, 0x16c385ff, 0x1ae89eff, 0x8adbbeff, 0x3ff8b6ff, 0x8ff4d0ff, 0xb6f1dcff,
            0x0d9d99ff, 0x41a6a3ff, 0x09bab6ff, 0x0bddd8ff, 0x78d1cfff, 0x34f1ecff, 0x7deeebff, 0xabedebff,
            0x1b819aff, 0x4a8e9eff, 0x1596b6ff, 0x18b1d9ff, 0x7eb9c9ff, 0x3dcaedff, 0x8bd7ebff, 0xb1dee9ff,
            0x16519dff, 0x386094ff, 0x115bbaff, 0x146cddff, 0x688cb9ff, 0x3a8bf1ff, 0x83b0ebff, 0xa9c3e5ff,
            0x131b9cff, 0x262c84ff, 0x0e19b9ff, 0x111edbff, 0x5156a4ff, 0x3844efff, 0x7d83e7ff, 0xa0a3deff,
            0x360a97ff, 0x3b1d7eff, 0x3d08b2ff, 0x4909d4ff, 0x64489fff, 0x6c33eaff, 0x9774e2ff, 0xaf9bdbff,
            0x5e179cff, 0x63328dff, 0x6b11b9ff, 0x8015dbff, 0x8c61b1ff, 0x9c3befff, 0xb983e9ff, 0xc6a7e2ff,
            0x871c9dff, 0x863f95ff, 0x9e15baff, 0xbb19ddff, 0xae6ebbff, 0xd13ef1ff, 0xda89ebff, 0xdcade5ff,
            0x9c117eff, 0x92327dff, 0xb90d94ff, 0xdb0fb0ff, 0xb762a5ff, 0xef37c8ff, 0xea7ed2ff, 0xe4a5d6ff,
            0x9a0e4aff, 0x8c2b55ff, 0xb60b54ff, 0xd90d64ff, 0xb05a7fff, 0xed3585ff, 0xe77aa9ff, 0xe1a2bdff,
            0x000000ff, 0x101010ff, 0x202020ff, 0x303030ff, 0x404040ff, 0x505050ff, 0x606060ff, 0x707070ff,
            0x808080ff, 0x909090ff, 0xa0a0a0ff, 0xb0b0b0ff, 0xc0c0c0ff, 0xd0d0d0ff, 0xe0e0e0ff, 0xf0f0f0ff,
            0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff,
            0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff,
            0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff,
            0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff,
            0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff,
            0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff,
            0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff, 0x000000ff,
    };
    /**
     * Starts with 8 important colors (clear, shadow, hard outline, clear bubble, 4 placeholders).
     * Then 8 invisible marker colors meant for voxels that have some non-visual meaning.
     * Then 16 grayscale colors from white to black.
     * Then 16 groups of 6 colors each, going from light to dark, with groups alternating saturated then
     * desaturated, and cycling hue from red to orange to yellow (briefly to skin colors) to green to cyan
     * to blue to violet to pink. Yellow doesn't have a desaturated block since it is replaced with various
     * skin tones. Cyan doesn't have a saturated block; it is replaced with more variants on deep green.
     * Some colors are slightly different on hue or saturation when their saturated and desaturated blocks
     * are compared, such as desaturated red being a tad closer to rich brown than simply grayish-red.
     * Yellow also doesn't get quite as dark because very dark yellow looks like an unpleasant brown, and
     * there's more need for light yellows that are more "cream-colored" than there is a need for ugly
     * yellow-gray-brown.
     * <p>
     * All 128 of these colors are repeated later to allow priority to be given to some voxels for shading.
     */
    public static final int[] ALT_PALETTE = {
            0x00000000, 0x444444ff, 0x000000ff, 0x88ffff00, 0x212121ff, 0x00ff00ff, 0x0000ffff, 0x080808ff,
            0xff574600, 0xffb14600, 0xfffd4600, 0x4bff4600, 0x51bf6c00, 0x4697ff00, 0x9146ff00, 0xff46ae00,
            0xffffffff, 0xeeeeeeff, 0xddddddff, 0xccccccff, 0xbbbbbbff, 0xaaaaaaff, 0x999999ff, 0x888888ff,
            0x777777ff, 0x666666ff, 0x555555ff, 0x444444ff, 0x333333ff, 0x222222ff, 0x111111ff, 0x000000ff,
            0xff8b7fff, 0xff5746ff, 0xeb3623ff, 0xcc1c0aff, 0xa50f00ff, 0x720a00ff, 0xffcbb2ff, 0xebb093ff,
            0xcc8a6bff, 0xa5684aff, 0x79462dff, 0x4c2816ff, 0xffc87fff, 0xffaf46ff, 0xeb9623ff, 0xcc790aff,
            0xa55e00ff, 0x724100ff, 0xffe3bfff, 0xebcb9fff, 0xcca675ff, 0xa58252ff, 0x795b33ff, 0x4c371aff,
            0xfffeacff, 0xfffd59ff, 0xf2f136ff, 0xdfdd1bff, 0xc5c409ff, 0xacab00ff, 0xf8e7cdff, 0xf2d09dff,
            0xdfb26fff, 0xbf8f47ff, 0x996c2aff, 0x6c4915ff, 0x83ff7fff, 0x4bff46ff, 0x29eb23ff, 0x10cc0aff,
            0x05a500ff, 0x037200ff, 0xc1ffbfff, 0xa1eb9fff, 0x78cc75ff, 0x55a552ff, 0x357933ff, 0x1c4c1aff,
            0x8ecc9eff, 0x51bf6cff, 0x33ac51ff, 0x1d923aff, 0x0f7929ff, 0x06591bff, 0xbffffdff, 0x9febe9ff,
            0x75ccc9ff, 0x52a5a3ff, 0x337977ff, 0x1a4c4bff, 0x7fb7ffff, 0x4697ffff, 0x237bebff, 0x0a5eccff,
            0x0048a5ff, 0x003272ff, 0xbfdbffff, 0x9fc0ebff, 0x759bccff, 0x5277a5ff, 0x335179ff, 0x1a304cff,
            0xb37fffff, 0x9146ffff, 0x7423ebff, 0x580accff, 0x4300a5ff, 0x2e0072ff, 0xd9bfffff, 0xbe9febff,
            0x9875ccff, 0x7452a5ff, 0x4f3379ff, 0x2e1a4cff, 0xff7fc7ff, 0xff46aeff, 0xeb2394ff, 0xcc0a77ff,
            0xa5005dff, 0x720040ff, 0xffbfe3ff, 0xeb9fcaff, 0xcc75a6ff, 0xa55281ff, 0x79335aff, 0x4c1a36ff,
            0x00000000, 0x444444ff, 0x000000ff, 0x88ffff00, 0x212121ff, 0x00ff00ff, 0x0000ffff, 0x080808ff,
            0xff574600, 0xffb14600, 0xfffd4600, 0x4bff4600, 0x51bf6c00, 0x4697ff00, 0x9146ff00, 0xff46ae00,
            0xffffffff, 0xeeeeeeff, 0xddddddff, 0xccccccff, 0xbbbbbbff, 0xaaaaaaff, 0x999999ff, 0x888888ff,
            0x777777ff, 0x666666ff, 0x555555ff, 0x444444ff, 0x333333ff, 0x222222ff, 0x111111ff, 0x000000ff,
            0xff8b7fff, 0xff5746ff, 0xeb3623ff, 0xcc1c0aff, 0xa50f00ff, 0x720a00ff, 0xffcbb2ff, 0xebb093ff,
            0xcc8a6bff, 0xa5684aff, 0x79462dff, 0x4c2816ff, 0xffc87fff, 0xffaf46ff, 0xeb9623ff, 0xcc790aff,
            0xa55e00ff, 0x724100ff, 0xffe3bfff, 0xebcb9fff, 0xcca675ff, 0xa58252ff, 0x795b33ff, 0x4c371aff,
            0xfffeacff, 0xfffd59ff, 0xf2f136ff, 0xdfdd1bff, 0xc5c409ff, 0xacab00ff, 0xf8e7cdff, 0xf2d09dff,
            0xdfb26fff, 0xbf8f47ff, 0x996c2aff, 0x6c4915ff, 0x83ff7fff, 0x4bff46ff, 0x29eb23ff, 0x10cc0aff,
            0x05a500ff, 0x037200ff, 0xc1ffbfff, 0xa1eb9fff, 0x78cc75ff, 0x55a552ff, 0x357933ff, 0x1c4c1aff,
            0x8ecc9eff, 0x51bf6cff, 0x33ac51ff, 0x1d923aff, 0x0f7929ff, 0x06591bff, 0xbffffdff, 0x9febe9ff,
            0x75ccc9ff, 0x52a5a3ff, 0x337977ff, 0x1a4c4bff, 0x7fb7ffff, 0x4697ffff, 0x237bebff, 0x0a5eccff,
            0x0048a5ff, 0x003272ff, 0xbfdbffff, 0x9fc0ebff, 0x759bccff, 0x5277a5ff, 0x335179ff, 0x1a304cff,
            0xb37fffff, 0x9146ffff, 0x7423ebff, 0x580accff, 0x4300a5ff, 0x2e0072ff, 0xd9bfffff, 0xbe9febff,
            0x9875ccff, 0x7452a5ff, 0x4f3379ff, 0x2e1a4cff, 0xff7fc7ff, 0xff46aeff, 0xeb2394ff, 0xcc0a77ff,
            0xa5005dff, 0x720040ff, 0xffbfe3ff, 0xeb9fcaff, 0xcc75a6ff, 0xa55281ff, 0x79335aff, 0x4c1a36ff,
    };
    
    public static final int[] ALT_PALETTE_OLD = { // change first item to, say, 0x00FF00FF to make all backgrounds green
            0x00000000, 0x444444FF, 0x000000FF, 0x88FFFF00, 0x212121FF, 0x00FF00FF, 0x0000FFFF, 0x0F0F0FFF,
            0x2B2B2BFF, 0x474747FF, 0x636363FF, 0x7F7F7FFF, 0x9B9B9BFF, 0xB7B7B7FF, 0xD3D3D3FF, 0xEFEFEFFF,
            0x330000FF, 0x4C0000FF, 0x6B0000FF, 0x930000FF, 0xBC0B0BFF, 0xDB3838FF, 0xF47878FF, 0xFFC2C2FF,
            0x330A00FF, 0x4C1200FF, 0x6B1F00FF, 0x933600FF, 0xBC5609FF, 0xDB7D36FF, 0xF4AC76FF, 0xFFDBC0FF,
            0x331000FF, 0x4C1A00FF, 0x6B2A00FF, 0x934400FF, 0xBC671AFF, 0xDB8E49FF, 0xF4BD8BFF, 0xFFE9D6FF,
            0x331600FF, 0x4C2400FF, 0x6B3600FF, 0x935200FF, 0xBC750DFF, 0xDB9A3AFF, 0xF4C37BFF, 0xFFE7C5FF,
            0x332800FF, 0x4C3C00FF, 0x6B5600FF, 0x937A00FF, 0xBCA008FF, 0xDBC134FF, 0xF4E074FF, 0xFFF4BEFF,
            0x333300FF, 0x4C4C00FF, 0x6B6B00FF, 0x939300FF, 0xBCBC09FF, 0xDBDB36FF, 0xF4F476FF, 0xFFFFBFFF,
            0x233300FF, 0x364C00FF, 0x4E6B00FF, 0x6F9300FF, 0x95BC0AFF, 0xB7DB37FF, 0xD9F476FF, 0xF1FFC0FF,
            0x1C3300FF, 0x2C4C00FF, 0x416B00FF, 0x5F9300FF, 0x83BC07FF, 0xA6DB33FF, 0xCBF472FF, 0xEAFFBCFF,
            0x113300FF, 0x1C4C00FF, 0x2C6B00FF, 0x469300FF, 0x69BC13FF, 0x8FDB41FF, 0xBCF482FF, 0xE6FFCCFF,
            0x003300FF, 0x004C00FF, 0x006B00FF, 0x039300FF, 0x1FBC0BFF, 0x4BDB38FF, 0x86F478FF, 0xC9FFC2FF,
            0x003300FF, 0x004C02FF, 0x006B0AFF, 0x00931CFF, 0x13BC3BFF, 0x42DB66FF, 0x83F49EFF, 0xCDFFD9FF,
            0x00331AFF, 0x004C28FF, 0x006B3CFF, 0x00935AFF, 0x0DBC7EFF, 0x3BDBA2FF, 0x7BF4C9FF, 0xC5FFEAFF,
            0x003331FF, 0x004C49FF, 0x006B67FF, 0x00938FFF, 0x06BCB7FF, 0x33DBD6FF, 0x72F4F1FF, 0xBCFFFDFF,
            0x002533FF, 0x00384CFF, 0x00516BFF, 0x007393FF, 0x0E9ABCFF, 0x3CBBDBFF, 0x7CDCF4FF, 0xC6F3FFFF,
            0x000C33FF, 0x00144CFF, 0x00216BFF, 0x003993FF, 0x0B59BCFF, 0x3880DBFF, 0x78AFF4FF, 0xC2DDFFFF,
            0x000033FF, 0x00004CFF, 0x00006BFF, 0x000093FF, 0x0A15BCFF, 0x3742DBFF, 0x777FF4FF, 0xC0C4FFFF,
            0x020033FF, 0x05004CFF, 0x0E006BFF, 0x210093FF, 0x3F07BCFF, 0x6733DBFF, 0x9B73F4FF, 0xD1BCFFFF,
            0x120033FF, 0x1E004CFF, 0x2E006BFF, 0x490093FF, 0x6B0CBCFF, 0x903ADBFF, 0xBC7AF4FF, 0xE3C4FFFF,
            0x260033FF, 0x3A004CFF, 0x53006BFF, 0x770093FF, 0x9D0EBCFF, 0xBE3CDBFF, 0xDF7CF4FF, 0xF4C6FFFF,
            0x330024FF, 0x4C0036FF, 0x6B004EFF, 0x930071FF, 0xBC0996FF, 0xDB36B8FF, 0xF476D9FF, 0xFFBFF1FF,
            0x33000AFF, 0x4C0012FF, 0x6B001FFF, 0x930036FF, 0xBC0856FF, 0xDB347DFF, 0xF474ACFF, 0xFFBEDAFF,
            0x000000FF, 0x101010FF, 0x202020FF, 0x303030FF, 0x404040FF, 0x505050FF, 0x606060FF, 0x707070FF,
            0x808080FF, 0x909090FF, 0xA0A0A0FF, 0xB0B0B0FF, 0xC0C0C0FF, 0xD0D0D0FF, 0xE0E0E0FF, 0xF0F0F0FF,
            0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000,
            0x332800FF, 0x4C3C00FF, 0x6B5600FF, 0x937A00FF, 0xBCA008FF, 0xDBC134FF, 0xF4E074FF, 0xFFF4BEFF,
            0x330A00FF, 0x4C1200FF, 0x6B1F00FF, 0x933600FF, 0xBC5609FF, 0xDB7D36FF, 0xF4AC76FF, 0xFFDBC0FF,
            0x331000FF, 0x4C1A00FF, 0x6B2A00FF, 0x934400FF, 0xBC671AFF, 0xDB8E49FF, 0xF4BD8BFF, 0xFFE9D6FF,
            0x330000FF, 0x4C0000FF, 0x6B0000FF, 0x930000FF, 0xBC0B0BFF, 0xDB3838FF, 0xF47878FF, 0xFFC2C2FF,
            0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000,
            0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000, 0x00000000,
    };
    /**
     * DawnBringer's 256-color Aurora palette, modified slightly to fit one transparent color by removing one gray.
     * Aurora is available in <a href="http://pixeljoint.com/forum/forum_posts.asp?TID=26080&KW=">this set of tools</a>
     * for a pixel art editor, but it is usable for lots of high-color purposes.
     */
    public static final int[] AURORA = {
            0x00000000, 0x010101FF, 0x131313FF, 0x252525FF, 0x373737FF, 0x494949FF, 0x5B5B5BFF, 0x6E6E6EFF,
            0x808080FF, 0x929292FF, 0xA4A4A4FF, 0xB6B6B6FF, 0xC9C9C9FF, 0xDBDBDBFF, 0xEDEDEDFF, 0xFFFFFFFF,
            0x007F7FFF, 0x3FBFBFFF, 0x00FFFFFF, 0xBFFFFFFF, 0x8181FFFF, 0x0000FFFF, 0x3F3FBFFF, 0x00007FFF,
            0x0F0F50FF, 0x7F007FFF, 0xBF3FBFFF, 0xF500F5FF, 0xFD81FFFF, 0xFFC0CBFF, 0xFF8181FF, 0xFF0000FF,
            0xBF3F3FFF, 0x7F0000FF, 0x551414FF, 0x7F3F00FF, 0xBF7F3FFF, 0xFF7F00FF, 0xFFBF81FF, 0xFFFFBFFF,
            0xFFFF00FF, 0xBFBF3FFF, 0x7F7F00FF, 0x007F00FF, 0x3FBF3FFF, 0x00FF00FF, 0xAFFFAFFF, 0xBCAFC0FF,
            0xCBAA89FF, 0xA6A090FF, 0x7E9494FF, 0x6E8287FF, 0x7E6E60FF, 0xA0695FFF, 0xC07872FF, 0xD08A74FF,
            0xE19B7DFF, 0xEBAA8CFF, 0xF5B99BFF, 0xF6C8AFFF, 0xF5E1D2FF, 0x573B3BFF, 0x73413CFF, 0x8E5555FF,
            0xAB7373FF, 0xC78F8FFF, 0xE3ABABFF, 0xF8D2DAFF, 0xE3C7ABFF, 0xC49E73FF, 0x8F7357FF, 0x73573BFF,
            0x3B2D1FFF, 0x414123FF, 0x73733BFF, 0x8F8F57FF, 0xA2A255FF, 0xB5B572FF, 0xC7C78FFF, 0xDADAABFF,
            0xEDEDC7FF, 0xC7E3ABFF, 0xABC78FFF, 0x8EBE55FF, 0x738F57FF, 0x587D3EFF, 0x465032FF, 0x191E0FFF,
            0x235037FF, 0x3B573BFF, 0x506450FF, 0x3B7349FF, 0x578F57FF, 0x73AB73FF, 0x64C082FF, 0x8FC78FFF,
            0xA2D8A2FF, 0xE1F8FAFF, 0xB4EECAFF, 0xABE3C5FF, 0x87B48EFF, 0x507D5FFF, 0x0F6946FF, 0x1E2D23FF,
            0x234146FF, 0x3B7373FF, 0x64ABABFF, 0x8FC7C7FF, 0xABE3E3FF, 0xC7F1F1FF, 0xBED2F0FF, 0xABC7E3FF,
            0xA8B9DCFF, 0x8FABC7FF, 0x578FC7FF, 0x57738FFF, 0x3B5773FF, 0x0F192DFF, 0x1F1F3BFF, 0x3B3B57FF,
            0x494973FF, 0x57578FFF, 0x736EAAFF, 0x7676CAFF, 0x8F8FC7FF, 0xABABE3FF, 0xD0DAF8FF, 0xE3E3FFFF,
            0xAB8FC7FF, 0x8F57C7FF, 0x73578FFF, 0x573B73FF, 0x3C233CFF, 0x463246FF, 0x724072FF, 0x8F578FFF,
            0xAB57ABFF, 0xAB73ABFF, 0xEBACE1FF, 0xFFDCF5FF, 0xE3C7E3FF, 0xE1B9D2FF, 0xD7A0BEFF, 0xC78FB9FF,
            0xC87DA0FF, 0xC35A91FF, 0x4B2837FF, 0x321623FF, 0x280A1EFF, 0x401811FF, 0x621800FF, 0xA5140AFF,
            0xDA2010FF, 0xD5524AFF, 0xFF3C0AFF, 0xF55A32FF, 0xFF6262FF, 0xF6BD31FF, 0xFFA53CFF, 0xD79B0FFF,
            0xDA6E0AFF, 0xB45A00FF, 0xA04B05FF, 0x5F3214FF, 0x53500AFF, 0x626200FF, 0x8C805AFF, 0xAC9400FF,
            0xB1B10AFF, 0xE6D55AFF, 0xFFD510FF, 0xFFEA4AFF, 0xC8FF41FF, 0x9BF046FF, 0x96DC19FF, 0x73C805FF,
            0x6AA805FF, 0x3C6E14FF, 0x283405FF, 0x204608FF, 0x0C5C0CFF, 0x149605FF, 0x0AD70AFF, 0x14E60AFF,
            0x7DFF73FF, 0x4BF05AFF, 0x00C514FF, 0x05B450FF, 0x1C8C4EFF, 0x123832FF, 0x129880FF, 0x06C491FF,
            0x00DE6AFF, 0x2DEBA8FF, 0x3CFEA5FF, 0x6AFFCDFF, 0x91EBFFFF, 0x55E6FFFF, 0x7DD7F0FF, 0x08DED5FF,
            0x109CDEFF, 0x055A5CFF, 0x162C52FF, 0x0F377DFF, 0x004A9CFF, 0x326496FF, 0x0052F6FF, 0x186ABDFF,
            0x2378DCFF, 0x699DC3FF, 0x4AA4FFFF, 0x90B0FFFF, 0x5AC5FFFF, 0xBEB9FAFF, 0x00BFFFFF, 0x007FFFFF,
            0x4B7DC8FF, 0x786EF0FF, 0x4A5AFFFF, 0x6241F6FF, 0x3C3CF5FF, 0x101CDAFF, 0x0010BDFF, 0x231094FF,
            0x0C2148FF, 0x5010B0FF, 0x6010D0FF, 0x8732D2FF, 0x9C41FFFF, 0x7F00FFFF, 0xBD62FFFF, 0xB991FFFF,
            0xD7A5FFFF, 0xD7C3FAFF, 0xF8C6FCFF, 0xE673FFFF, 0xFF52FFFF, 0xDA20E0FF, 0xBD29FFFF, 0xBD10C5FF,
            0x8C14BEFF, 0x5A187BFF, 0x641464FF, 0x410062FF, 0x320A46FF, 0x551937FF, 0xA01982FF, 0xC80078FF,
            0xFF50BFFF, 0xFF6AC5FF, 0xFAA0B9FF, 0xFC3A8CFF, 0xE61E78FF, 0xBD1039FF, 0x98344DFF, 0x911437FF,
    };
    /**
     * Organized into chunks of 4 colors after the first 16 (those are the same as {@link #ALT_PALETTE}).
     * Lots of unassigned space; only 80 colors are used.
     */
    public static final int[] UNSEVEN = {
            0x00000000,
            0xfcfcfcff, 0xc3cbdbff, 0xa096d1ff, 0x62507eff, 0x424556ff, 0x252a32ff, 0x14161fff, 0x0a0b0fff,
            0x888c78ff, 0x585651ff, 0x453c3cff, 0x32222eff, 0xff8f8fff, 0xff2245ff, 0xd50964ff, 0x9c0565ff,
            0xffd800ff, 0xff9000ff, 0xe93100ff, 0xbf0000ff, 0xe5ff05ff, 0xa7ed00ff, 0x4ab907ff, 0x0a5d45ff,
            0x00fff0ff, 0x00b9ffff, 0x008df0ff, 0x1664c5ff, 0xffe822ff, 0xffa939ff, 0xe56335ff, 0xe5233eff,
            0xfffc00ff, 0xebb70aff, 0xbe8420ff, 0x915816ff, 0xffb35bff, 0xd77e4bff, 0xb15c51ff, 0x793d4eff,
            0xff70dfff, 0xff22a9ff, 0x611381ff, 0x45064bff, 0xccfff5ff, 0x6df7b1ff, 0x00c19aff, 0x017687ff,
            0x7bd5f3ff, 0x6c88ffff, 0x6440d8ff, 0x3d2e93ff, 0x85a3c7ff, 0x676cadff, 0x683395ff, 0x323751ff,
            0xff59beff, 0xc51aeaff, 0x6e10abff, 0x331685ff, 0xfb9585ff, 0xe97461ff, 0xb53772ff, 0x93278fff,
    };
    
    /**
     * This palette was given along with the Unseven palette
     * <a href="https://www.deviantart.com/foguinhos/art/Unseven-Full-541514728">in this set of swatches</a>, but it's
     * unclear if Unseven made it, or if this palette was published in some other medium. It's a nice palette, with 8
     * levels of lightness ramp for 30 ramps with different hues. It seems meant for pixel art that includes human
     * characters, and doesn't lack for skin tones like Unseven does. It has a generally good selection of light brown
     * colors, and has been adjusted to add some dark brown colors, as well as vividly saturated purple. Many ramps also
     * become more purple as they go into darker shades.
     * <p>
     * This is organized so the colors from index 1 to index 232 inclusive are sorted by hue, from red to orange to
     * yellow to green to blue to purple, while still being organized in blocks of 8 colors at a time from bright to
     * dark. Some almost-grayscale blocks are jumbled in the middle, but they do have a hue and it is always at the
     * point where they are in the sort. A block of colors that are practically true grayscale are at indices 16-23,
     * inclusive.
     */
    public static final int[] RINSED = {
            0x00000000,
            0xF8F9FAFF, 0xC4C3C5FF, 0x9C9C9DFF, 0x757676FF, 0x616262FF, 0x4C484AFF, 0x252626FF, 0x090304FF,
            0xD89789FF, 0xC4877AFF, 0xB47B76FF, 0xA36C72FF, 0x905861FF, 0x76454CFF, 0x5F3234FF, 0x452327FF,
            0xF9DCB8FF, 0xCEB29AFF, 0xB29891FF, 0x8F797FFF, 0x75636FFF, 0x554B67FF, 0x3E3552FF, 0x272340FF,
            0xEAA18DFF, 0xCF9180FF, 0xB87C6BFF, 0xA06A60FF, 0x905C59FF, 0x73474BFF, 0x52383EFF, 0x35242AFF,
            0xBEAE97FF, 0xB0968AFF, 0x89756EFF, 0x6E5A54FF, 0x4F413CFF, 0x413534FF, 0x2F2525FF, 0x1C1415FF,
            0xEED8A1FF, 0xE7B38CFF, 0xCC967FFF, 0xB6776DFF, 0x995A55FF, 0x803D49FF, 0x662139FF, 0x500328FF,
            0xFDFE9CFF, 0xFDD7AAFF, 0xE9BBA4FF, 0xC9A09DFF, 0xB7889AFF, 0x957088FF, 0x755B7BFF, 0x514265FF,
            0xFDF067FF, 0xFDBF60FF, 0xEF995AFF, 0xCC7148FF, 0xB65549FF, 0xA34547FF, 0x7D303FFF, 0x61242FFF,
            0xDDBBA4FF, 0xC0A68FFF, 0x9F8871FF, 0x7F6B5CFF, 0x6B5755FF, 0x5D464CFF, 0x482F3DFF, 0x30232DFF,
            0xFEF5E1FF, 0xE9DFD3FF, 0xCFC5BAFF, 0xBAAFABFF, 0xAAA291FF, 0x9A877BFF, 0x816F69FF, 0x615D56FF,
            0xFEF1A8FF, 0xE4CE85FF, 0xC9AD77FF, 0xB19169FF, 0x957859FF, 0x7B604CFF, 0x60463BFF, 0x472F2AFF,
            0xFEFC74FF, 0xE8D861FF, 0xCDAD53FF, 0xB2893EFF, 0x91672FFF, 0x7D4F21FF, 0x693C12FF, 0x562810FF,
            0xFDFCB7FF, 0xFCFA3CFF, 0xFAD725FF, 0xF5B325FF, 0xD7853CFF, 0xB25345FF, 0x8A2B2BFF, 0x67160AFF,
            0xCBD350FF, 0xB3B24BFF, 0x9A9E3AFF, 0x808B30FF, 0x647717FF, 0x4B6309FF, 0x305413FF, 0x272A07FF,
            0x8DC655FF, 0x7BA838FF, 0x6C8A37FF, 0x5D733AFF, 0x4F633CFF, 0x3F5244FF, 0x323D4AFF, 0x232A45FF,
            0xADD54BFF, 0x80B040FF, 0x599135FF, 0x35761AFF, 0x2A621FFF, 0x1E5220FF, 0x063824FF, 0x012B1DFF,
            0xE8FFEFFF, 0xA9DDC0FF, 0x95C89CFF, 0x91B48EFF, 0x759983FF, 0x627F72FF, 0x4C655CFF, 0x36514AFF,
            0x91E49DFF, 0x69C085FF, 0x4F8F62FF, 0x4A7855FF, 0x396044FF, 0x385240FF, 0x31413DFF, 0x233631FF,
            0x09EFD0FF, 0x07CCA2FF, 0x03AA83FF, 0x038D75FF, 0x04726DFF, 0x01585AFF, 0x05454EFF, 0x083142FF,
            0x97D6F9FF, 0x3EB0CAFF, 0x3C919FFF, 0x0A737CFF, 0x226171FF, 0x0B505FFF, 0x0D3948FF, 0x052935FF,
            0x91FCFCFF, 0x68DBFEFF, 0x5CB1D5FF, 0x4C8CAAFF, 0x406883FF, 0x2B4965FF, 0x29324DFF, 0x1C1E34FF,
            0x80D1FBFF, 0x62B2E7FF, 0x4D96DBFF, 0x267DB9FF, 0x195F97FF, 0x114776FF, 0x0B355AFF, 0x031D41FF,
            0xCEEEFDFF, 0xCDD7FEFF, 0xA1AED7FF, 0x898CAEFF, 0x7C7196FF, 0x5E597CFF, 0x404163FF, 0x26294CFF,
            0x8391C1FF, 0x7181CAFF, 0x5E71BEFF, 0x555FA2FF, 0x424C84FF, 0x323B6DFF, 0x2B325CFF, 0x292349FF,
            0xE3D1FDFF, 0xBAABFAFF, 0x9F94E2FF, 0x9588D7FF, 0x7B71B3FF, 0x675E9CFF, 0x4F4D7CFF, 0x333158FF,
            0xA570FFFF, 0x9462FFFF, 0x814EFFFF, 0x6C39FCFF, 0x582DC1FF, 0x472195FF, 0x412160FF, 0x2E1F38FF,
            0xF7C1E7FF, 0xD791C6FF, 0xBB6FAAFF, 0xAF6190FF, 0x924B76FF, 0x623155FF, 0x47253FFF, 0x2F0E25FF,
            0xFDC7FBFF, 0xFC9FC5FF, 0xFB71A9FF, 0xE6497EFF, 0xC33C6BFF, 0x933255FF, 0x68243FFF, 0x3F122AFF,
            0xFDDDDCFF, 0xD1ABB1FF, 0xB48C9AFF, 0x9D7482FF, 0x8B5D6EFF, 0x705057FF, 0x583C4BFF, 0x421E29FF,
            0xFCD9FBFF, 0xFDB8C7FF, 0xFD97AAFF, 0xF46E7EFF, 0xC65365FF, 0x9E303CFF, 0x741B28FF, 0x50071AFF,
    };

    /**
     * @param name Name of a color
     * @return Retrieves color as an int from RINSED_MAP, or 0 if the name isn't found
     */
    public static int rinsed(String name) {
        return RINSED_MAP.get(name, 0);
    }

    public static final String[] RINSED_NAMES = {
            "Transparent",
            //0
            "Gray 0", "Gray 1", "Gray 2", "Gray 3",
            "Gray 4", "Gray 5", "Gray 6", "Gray 7",
            //1
            "Blush Skin 0", "Blush Skin 1", "Blush Skin 2", "Blush Skin 3",
            "Blush Skin 4", "Blush Skin 5", "Blush Skin 6", "Blush Skin 7",
            //2
            "Dark Deepening Skin 0", "Dark Deepening Skin 1", "Dark Deepening Skin 2", "Dark Deepening Skin 3",
            "Dark Deepening Skin 4", "Dark Deepening Skin 5", "Dark Deepening Skin 6", "Dark Deepening Skin 7",
            //3
            "Warm Skin 0", "Warm Skin 1", "Warm Skin 2", "Warm Skin 3",
            "Warm Skin 4", "Warm Skin 5", "Warm Skin 6", "Warm Skin 7",
            //4
            "Dark Skin 0", "Dark Skin 1", "Dark Skin 2", "Dark Skin 3",
            "Dark Skin 4", "Dark Skin 5", "Dark Skin 6", "Dark Skin 7",
            //5
            "Bold Skin 0", "Bold Skin 1", "Bold Skin 2", "Bold Skin 3",
            "Bold Skin 4", "Bold Skin 5", "Bold Skin 6", "Bold Skin 7",
            //6
            "Light Deepening Skin 0", "Light Deepening Skin 1", "Light Deepening Skin 2", "Light Deepening Skin 3",
            "Light Deepening Skin 4", "Light Deepening Skin 5", "Light Deepening Skin 6", "Light Deepening Skin 7",
            //7
            "Yellow Orange 0", "Yellow Orange 1", "Yellow Orange 2", "Yellow Orange 3",
            "Yellow Orange 4", "Yellow Orange 5", "Yellow Orange 6", "Yellow Orange 7",
            //8
            "Wood 0", "Wood 1", "Wood 2", "Wood 3",
            "Wood 4", "Wood 5", "Wood 6", "Wood 7",
            //9
            "Discolored Gray 0", "Discolored Gray 1", "Discolored Gray 2", "Discolored Gray 3",
            "Discolored Gray 4", "Discolored Gray 5", "Discolored Gray 6", "Discolored Gray 7",
            //10
            "Bronze Skin 0", "Bronze Skin 1", "Bronze Skin 2", "Bronze Skin 3",
            "Bronze Skin 4", "Bronze Skin 5", "Bronze Skin 6", "Bronze Skin 7",
            //11
            "Gold Fur 0", "Gold Fur 1", "Gold Fur 2", "Gold Fur 3",
            "Gold Fur 4", "Gold Fur 5", "Gold Fur 6", "Gold Fur 7",
            //12
            "Fire 0", "Fire 1", "Fire 2", "Fire 3",
            "Fire 4", "Fire 5", "Fire 6", "Fire 7",
            //13
            "Avocado 0", "Avocado 1", "Avocado 2", "Avocado 3",
            "Avocado 4", "Avocado 5", "Avocado 6", "Avocado 7",
            //14
            "Dull Green 0", "Dull Green 1", "Dull Green 2", "Dull Green 3",
            "Dull Green 4", "Dull Green 5", "Dull Green 6", "Dull Green 7",
            //15
            "Vivid Green 0", "Vivid Green 1", "Vivid Green 2", "Vivid Green 3",
            "Vivid Green 4", "Vivid Green 5", "Vivid Green 6", "Vivid Green 7",
            //16
            "Gray Green 0", "Gray Green 1", "Gray Green 2", "Gray Green 3",
            "Gray Green 4", "Gray Green 5", "Gray Green 6", "Gray Green 7",
            //17
            "Cold Forest 0", "Cold Forest 1", "Cold Forest 2", "Cold Forest 3",
            "Cold Forest 4", "Cold Forest 5", "Cold Forest 6", "Cold Forest 7",
            //18
            "Turquoise 0", "Turquoise 1", "Turquoise 2", "Turquoise 3",
            "Turquoise 4", "Turquoise 5", "Turquoise 6", "Turquoise 7",
            //19
            "Coastal Water 0", "Coastal Water 1", "Coastal Water 2", "Coastal Water 3",
            "Coastal Water 4", "Coastal Water 5", "Coastal Water 6", "Coastal Water 7",
            //20
            "Ice 0", "Ice 1", "Ice 2", "Ice 3",
            "Ice 4", "Ice 5", "Ice 6", "Ice 7",
            //21
            "Powder Blue 0", "Powder Blue 1", "Powder Blue 2", "Powder Blue 3",
            "Powder Blue 4", "Powder Blue 5", "Powder Blue 6", "Powder Blue 7",
            //22
            "Dusty Gray 0", "Dusty Gray 1", "Dusty Gray 2", "Dusty Gray 3",
            "Dusty Gray 4", "Dusty Gray 5", "Dusty Gray 6", "Dusty Gray 7",
            //23
            "Blue Steel 0", "Blue Steel 1", "Blue Steel 2", "Blue Steel 3",
            "Blue Steel 4", "Blue Steel 5", "Blue Steel 6", "Blue Steel 7",
            //24
            "Lavender 0", "Lavender 1", "Lavender 2", "Lavender 3",
            "Lavender 4", "Lavender 5", "Lavender 6", "Lavender 7",
            //25
            "Heliotrope 0", "Heliotrope 1", "Heliotrope 2", "Heliotrope 3",
            "Heliotrope 4", "Heliotrope 5", "Heliotrope 6", "Heliotrope 7",
            //26
            "Purple 0", "Purple 1", "Purple 2", "Purple 3",
            "Purple 4", "Purple 5", "Purple 6", "Purple 7",
            //27
            "Hot Pink 0", "Hot Pink 1", "Hot Pink 2", "Hot Pink 3",
            "Hot Pink 4", "Hot Pink 5", "Hot Pink 6", "Hot Pink 7",
            //28
            "Withered Plum 0", "Withered Plum 1", "Withered Plum 2", "Withered Plum 3",
            "Withered Plum 4", "Withered Plum 5", "Withered Plum 6", "Withered Plum 7",
            //29
            "Red 0", "Red 1", "Red 2", "Red 3",
            "Red 4", "Red 5", "Red 6", "Red 7",
};    
    public static final int[] PURE = {
            0x00000000, 
            0x000000FF, 0x202020FF, 0x404040FF, 0x606060FF, 0x808080FF, 0xA0A0A0FF, 0xC0C0C0FF, 0xE0E0E0FF, 0xFFFFFFFF, //Gray
            0xBEAE97FF, 0xB0968AFF, 0x89756EFF, 0x6E5A54FF, 0x4F413CFF, 0x413534FF, 0x2F2525FF, 0x1C1415FF, //Dark Skin
            0xFDFE9CFF, 0xFDD7AAFF, 0xE9BBA4FF, 0xC9A09DFF, 0xB7889AFF, 0x957088FF, 0x755B7BFF, 0x514265FF, //Light Deepening Skin
            0xDDBBA4FF, 0xC0A68FFF, 0x9F8871FF, 0x7F6B5CFF, 0x6B5755FF, 0x5D464CFF, 0x482F3DFF, 0x30232DFF, //Wood
            0xFDFCB7FF, 0xFCFA3CFF, 0xFAD725FF, 0xF5B325FF, 0xD7853CFF, 0xB25345FF, 0x8A2B2BFF, 0x67160AFF, //Fire
            //0x8DC655FF, 0x7BA838FF, 0x6C8A37FF, 0x5D733AFF, 0x4F633CFF, 0x3F5244FF, 0x323D4AFF, 0x232A45FF, //Dull Green
            0xADD54BFF, 0x80B040FF, 0x599135FF, 0x35761AFF, 0x2A621FFF, 0x1E5220FF, 0x063824FF, 0x012B1DFF, //Vivid Green
            0x97D6F9FF, 0x3EB0CAFF, 0x3C919FFF, 0x0A737CFF, 0x226171FF, 0x0B505FFF, 0x0D3948FF, 0x052935FF, //Coastal Water
            0x8391C1FF, 0x7181CAFF, 0x5E71BEFF, 0x555FA2FF, 0x424C84FF, 0x323B6DFF, 0x2B325CFF, 0x292349FF, //Blue Steel
            0xE3D1FDFF, 0xBAABFAFF, 0x9F94E2FF, 0x9588D7FF, 0x7B71B3FF, 0x675E9CFF, 0x4F4D7CFF, 0x333158FF, //Lavender
            //0xF7C1E7FF, 0xD791C6FF, 0xBB6FAAFF, 0xAF6190FF, 0x924B76FF, 0x623155FF, 0x47253FFF, 0x2F0E25FF, //Purple
            0xFCD9FBFF, 0xFDB8C7FF, 0xFD97AAFF, 0xF46E7EFF, 0xC65365FF, 0x9E303CFF, 0x741B28FF, 0x50071AFF, //Red
    };
    /**
     * Big ObjectIntMap of a name for each color in {@link #RINSED}, mapping String keys to RGBA8888 color ints.
     * Colors with numbers after the names have 0 mean the lightest color in a ramp and 7 mean the darkest.
     * If you're reading the source, there's a comment above each ramp saying which index that ramp would have out of
     * the full list of 30 ramps (each with 8 colors). The first ramp, which goes from white to black, takes up index 1
     * to index 8.
     */
    public static final ObjectIntMap<String> RINSED_MAP = new ObjectIntMap<>(256);
    static {
        for (int i = 0; i < RINSED.length; i++) {
            RINSED_MAP.put(RINSED_NAMES[i], RINSED[i]);
        }
    }

    /**
     * DawnBringer16 palette, plus transparent first. Has slight changes to match the palette used in DawnLike.
     */
    public static final int[] DB16 = {
            0x00000000,
            0x140C1CFF,
            0x452434FF,
            0x30346DFF,
            0x4D494DFF,
            0x864D30FF,
            0x346524FF,
            0xD34549FF,
            0x757161FF,
            0x597DCFFF,
            0xD37D2CFF,
            0x8696A2FF,
            0x6DAA2CFF,
            0xD3AA9AFF,
            0x6DC3CBFF,
            0xDBD75DFF,
            0xDFEFD7FF,
    };


    /**
     * DawnBringer32 palette, plus transparent first.
     */
    public static final int[] DB32 = {
            0x00000000,
            0x000000FF, 0x222034FF, 0x45283CFF, 0x663931FF, 0x8F563BFF, 0xDF7126FF, 0xD9A066FF, 0xEEC39AFF,
            0xFBF236FF, 0x99E550FF, 0x6ABE30FF, 0x37946EFF, 0x4B692FFF, 0x524B24FF, 0x323C39FF, 0x3F3F74FF,
            0x306082FF, 0x5B6EE1FF, 0x639BFFFF, 0x5FCDE4FF, 0xCBDBFCFF, 0xFFFFFFFF, 0x9BADB7FF, 0x847E87FF,
            0x696A6AFF, 0x595652FF, 0x76428AFF, 0xAC3232FF, 0xD95763FF, 0xD77BBAFF, 0x8F974AFF, 0x8A6F30FF,
    };

    public static final int[] GB = {
            //0x00000000, 0x000000FF, 0x5B5B5BFF, 0xA4A4A4FF, 0xFFFFFFFF,
            0x00000000, 0x252525FF, 0x6E6E6EFF, 0xB6B6B6FF, 0xFFFFFFFF,
    };

    public static final int[] GB_GREEN = {
            0x00000000, 0x081820FF, 0x346856FF, 0x88C070FF, 0xE0F8D0FF
    };
    
    public static final int[] GB_GREEN16 = {
            0x00000000, 0x000000FF, 0x081820FF, 0x132C2DFF, 0x1E403BFF, 0x295447FF, 0x346856FF, 0x497E5BFF,
            0x5E9463FF, 0x73AA69FF, 0x88C070FF, 0x9ECE88FF, 0xB4DCA0FF, 0xCAEAB8FF, 0xE0F8D0FF, 0xEFFBE7FF,
            0xFFFFFFFF,
    };

    public static final int[] BLUE16 = new int[16];
    public static final int[] ORANGE16 = new int[16];
    public static final int[] ROLLER = new int[64];
    public static final int[] BIG_ROLLER = new int[256];
    
    static {
        for (int i = 1; i < 16; i++) {
            BLUE16[i] = Color.rgba8888(NamedColor.ycwcm((i - 0.75f) / 13.25f, -1f, -1f + i * 0x1p-5f, 1f)) | 1;
        }
        final NamedColor low = NamedColor.BLACK, mid = NamedColor.CW_ORANGE, high = NamedColor.WHITE;
        ORANGE16[7] = Color.rgba8888(mid);
        for (int i = 1; i < 7; i++) {
            final float a = (i - 1) / 6f;
            ORANGE16[i] = Color.rgba8888(NamedColor.ycwcmLerp(low, mid, a));
        }
        for (int i = 8; i < 16; i++) {
            final float a = (i - 7) / 8f;
            ORANGE16[i] = Color.rgba8888(NamedColor.ycwcmLerp(mid, high, a));
        }
        for (int i = 0; i < 8; i++) {
            ROLLER[i+1] = Color.rgba8888(NamedColor.lerp(NamedColor.BLACK, NamedColor.WHITE, i / 7f));
        }
        int c = 8;
        NamedColor[] colors = {
                NamedColor.CW_LIGHT_RED, NamedColor.CW_APRICOT, NamedColor.CW_DRAB_BROWN, NamedColor.CW_LIGHT_YELLOW,
                NamedColor.CW_FLUSH_HONEYDEW, NamedColor.CW_GREEN, NamedColor.CW_DRAB_SEAFOAM, NamedColor.CW_RICH_BLUE,
                NamedColor.CW_LIGHT_SAPPHIRE, NamedColor.CW_FADED_VIOLET, NamedColor.CW_ROSE
        };
        for(NamedColor nc : colors) {
            ROLLER[1 + c] = Color.rgba8888(NamedColor.ycwcmLerp(NamedColor.BLACK, nc, 0.35f));
            ROLLER[2 + c] = Color.rgba8888(NamedColor.ycwcmLerp(NamedColor.BLACK, nc, 0.75f));
            ROLLER[3 + c] = Color.rgba8888(nc);
            ROLLER[4 + c] = Color.rgba8888(NamedColor.ycwcmLerp(nc, NamedColor.WHITE, 0.25f));
            ROLLER[5 + c] = Color.rgba8888(NamedColor.ycwcmLerp(nc, NamedColor.WHITE, 0.6f));
            c += 5;
        }
        for (int i = 0; i < 17; i++) {
            BIG_ROLLER[i+1] = Color.rgba8888(NamedColor.lerp(NamedColor.BLACK, NamedColor.WHITE, i / 16f));
        }
        c = 17;
        NamedColor[] colorsBig = {
                NamedColor.CW_LIGHT_RED, NamedColor.CW_APRICOT, NamedColor.CW_FLUSH_ORANGE, NamedColor.CW_DRAB_BROWN,
                NamedColor.CW_BRIGHT_BROWN, NamedColor.CW_BRIGHT_YELLOW, NamedColor.CW_FLUSH_HONEYDEW, NamedColor.CW_GREEN,
                NamedColor.CW_PALE_JADE, NamedColor.CW_DARK_SEAFOAM, NamedColor.CW_CYAN, NamedColor.CW_LIGHT_AZURE,
                NamedColor.CW_BRIGHT_BLUE, NamedColor.CW_SAPPHIRE, NamedColor.CW_FADED_VIOLET,
                NamedColor.CW_RICH_PURPLE, NamedColor.CW_ROSE
        };
        for(NamedColor nc : colorsBig) {
            float y = NamedColor.lumaYCwCm(nc), cw = NamedColor.chromaWarm(nc), cm = NamedColor.chromaMild(nc);
            BIG_ROLLER[1 + c] = Color.rgba8888(NamedColor.ycwcmLerp(NamedColor.BLACK, nc, 0.25f));
            BIG_ROLLER[2 + c] = Color.rgba8888(NamedColor.ycwcmLerp(NamedColor.BLACK, nc, 0.45f));
            BIG_ROLLER[3 + c] = Color.rgba8888(NamedColor.ycwcmLerp(NamedColor.BLACK, nc, 0.625f));
            BIG_ROLLER[4 + c] = Color.rgba8888(NamedColor.ycwcmLerp(NamedColor.BLACK, nc, 0.8f));
            BIG_ROLLER[5 + c] = Color.rgba8888(nc);
            BIG_ROLLER[6 + c] = Color.rgba8888(NamedColor.ycwcmLerp(nc, NamedColor.WHITE, 0.15f));
            BIG_ROLLER[7 + c] = Color.rgba8888(NamedColor.ycwcmLerp(nc, NamedColor.WHITE, 0.3f));
            BIG_ROLLER[8 + c] = Color.rgba8888(NamedColor.ycwcmLerp(nc, NamedColor.WHITE, 0.425f));
            BIG_ROLLER[9 + c] = Color.rgba8888(NamedColor.ycwcmLerp(nc, NamedColor.WHITE, 0.55f));
            BIG_ROLLER[10 + c] = Color.rgba8888(NamedColor.ycwcmLerp(nc, NamedColor.WHITE, 0.7f));
            BIG_ROLLER[11 + c] = Color.rgba8888(NamedColor.ycwcm(y - 0.1f, cw * 0.375f, cm * 0.375f, 1f));
            BIG_ROLLER[12 + c] = Color.rgba8888(NamedColor.ycwcm(y - 0.15f, cw * 0.25f, cm * 0.25f, 1f));
            BIG_ROLLER[13 + c] = Color.rgba8888(NamedColor.ycwcm(y - 0.2f, cw * 0.5f, cm * 0.5f, 1f));
            BIG_ROLLER[14 + c] = Color.rgba8888(NamedColor.ycwcm(y - 0.25f, cw * 0.75f, cm * 0.75f, 1f));
            c += 14;
        }
    }

    /**
     * A 64-color palette that started with specific colors from NamedColor's Color Wheel palette, then lightened and
     * darkened to get 5 variants on all colors (except 8 for grayscale), and finally ran the whole set through Lloyd
     * relaxation to improve the worst similarity between any two colors. It is very good for a 64-color palette, and
     * its only real weakness is a somewhat poor coverage of low-saturation colors.
     */
    public static final int[] RELAXED_ROLL = {
            0x00000000, 0x100818ff, 0x181818ff, 0x314a6bff, 0x396b7bff, 0x4a9494ff, 0xa5b5adff, 0xb5e7e7ff,
            0xf7efefff, 0x6b1831ff, 0xbd5242ff, 0xef6b4aff, 0xef9c9cff, 0xf7c6deff, 0x6b3921ff, 0xbd8421ff,
            0xefa531ff, 0xe7ce42ff, 0xefd6a5ff, 0x292921ff, 0x7b5231ff, 0x8c7339ff, 0xb59473ff, 0xcec6a5ff,
            0x316b31ff, 0xadbd42ff, 0xefef39ff, 0xeff79cff, 0xe7f7deff, 0x215a21ff, 0x52bd39ff, 0x84e731ff,
            0xb5ef42ff, 0xbdef9cff, 0x295221ff, 0x29ad29ff, 0x31e729ff, 0x39ef7bff, 0x52f7b5ff, 0x214221ff,
            0x318439ff, 0x42ad84ff, 0x4aceadff, 0x5ae7e7ff, 0x180842ff, 0x3118a5ff, 0x3921deff, 0x428cc6ff,
            0x42bde7ff, 0x293163ff, 0x4a63b5ff, 0x5a84efff, 0x9ca5e7ff, 0xced6efff, 0x211073ff, 0x5a3194ff,
            0x8431d6ff, 0xb573b5ff, 0xc6bde7ff, 0x421039ff, 0xa5214aff, 0xde2152ff, 0xde31ceff, 0xe784deff,
    };
    
    public static final int[] LAZY_ROLL = {
            0x00000000, 0x000000ff, 0x100810ff, 0x081821ff, 0x292929ff, 0x394242ff, 0x4a4a4aff, 0x5a635aff,
            0x6b736bff, 0x737b7bff, 0x8c8c84ff, 0x9c9494ff, 0xada5adff, 0xbdb5b5ff, 0xc6cebdff, 0xdeded6ff,
            0xefe7e7ff, 0xfff7f7ff, 0x421829ff, 0x6b3931ff, 0x944a5aff, 0xce5273ff, 0xef5a7bff, 0xef8c94ff,
            0xefa5adff, 0xf7b5b5ff, 0xf7c6ceff, 0xf7d6efff, 0xad6b9cff, 0x8c6b8cff, 0x9c4a94ff, 0xad3173ff,
            0x422908ff, 0x735210ff, 0x947310ff, 0xc68c18ff, 0xf7ad10ff, 0xf7bd31ff, 0xf7ce52ff, 0xf7d673ff,
            0xf7de94ff, 0xffe7ceff, 0xbd9463ff, 0xa59c63ff, 0xa58c39ff, 0xad7310ff, 0x421808ff, 0x6b2910ff,
            0x943910ff, 0xbd4218ff, 0xe74221ff, 0xef7321ff, 0xef8c4aff, 0xf7a573ff, 0xf7bd8cff, 0xf7d6b5ff,
            0x9c6342ff, 0x7b634aff, 0x843921ff, 0xa52118ff, 0x211808ff, 0x423129ff, 0x5a5a18ff, 0x735242ff,
            0x947b4aff, 0xa5736bff, 0xb59c8cff, 0xce8c9cff, 0xcebda5ff, 0xded6b5ff, 0x6b634aff, 0x5a5a42ff,
            0x524a31ff, 0x523118ff, 0x312910ff, 0x634210ff, 0x8c5a29ff, 0xb56b42ff, 0xd68442ff, 0xdea54aff,
            0xdeb57bff, 0xe7c694ff, 0xe7de94ff, 0xefe7bdff, 0x9c846bff, 0x847b63ff, 0x847b29ff, 0x9c5218ff,
            0x394a10ff, 0x6b7318ff, 0x949c18ff, 0xcece21ff, 0xe7ef18ff, 0xefef42ff, 0xeff763ff, 0xf7f784ff,
            0xf7f7a5ff, 0xf7ffc6ff, 0xced66bff, 0xbdc684ff, 0xb5bd52ff, 0xadb518ff, 0x214208ff, 0x426b10ff,
            0x5a8c18ff, 0x6bc621ff, 0x73e718ff, 0xa5ef21ff, 0xb5ef52ff, 0xc6ef73ff, 0xcef78cff, 0xdef7adff,
            0x84b54aff, 0x84945aff, 0x739c31ff, 0x52ad18ff, 0x083908ff, 0x187310ff, 0x219c21ff, 0x21c629ff,
            0x21ef29ff, 0x31e76bff, 0x63ef73ff, 0x8cef84ff, 0xa5f7a5ff, 0xbdffbdff, 0x4a9c52ff, 0x527b4aff,
            0x318429ff, 0x089410ff, 0x294229ff, 0x526b52ff, 0x6b947bff, 0x8cce6bff, 0xade7a5ff, 0xc6e7adff,
            0xceefceff, 0xcef7deff, 0xdef7d6ff, 0xeff7efff, 0xa5c6b5ff, 0xa5b59cff, 0x94ad84ff, 0x6bad84ff,
            0x082918ff, 0x104a29ff, 0x106b39ff, 0x18845aff, 0x10ad63ff, 0x39b56bff, 0x4ac69cff, 0x73ceadff,
            0x94d6b5ff, 0xbdd6d6ff, 0x316b4aff, 0x315242ff, 0x10524aff, 0x105a18ff, 0x083939ff, 0x106b6bff,
            0x107b8cff, 0x18bdadff, 0x10e7c6ff, 0x31e7deff, 0x52efceff, 0x73f7d6ff, 0x94f7e7ff, 0xb5f7efff,
            0x429c9cff, 0x4a8473ff, 0x298c84ff, 0x10949cff, 0x213139ff, 0x39636bff, 0x5a8494ff, 0x5aa5c6ff,
            0x73d6e7ff, 0x9cded6ff, 0xade7efff, 0xbde7deff, 0xceeff7ff, 0xdeeff7ff, 0x8cadbdff, 0x8c94a5ff,
            0x7394a5ff, 0x527bb5ff, 0x102142ff, 0x10426bff, 0x215aadff, 0x2173ceff, 0x188cefff, 0x42a5efff,
            0x6bb5efff, 0x8cbdefff, 0xa5c6f7ff, 0xc6d6f7ff, 0x4a6394ff, 0x425273ff, 0x215284ff, 0x084294ff,
            0x100842ff, 0x18216bff, 0x392194ff, 0x2921bdff, 0x2929efff, 0x6342e7ff, 0x7b6befff, 0x948cefff,
            0xada5efff, 0xc6bdf7ff, 0x313173ff, 0x182952ff, 0x10086bff, 0x101094ff, 0x211831ff, 0x393152ff,
            0x5a3984ff, 0x734a94ff, 0x7b4ab5ff, 0x8c73c6ff, 0xad8cc6ff, 0xb5a5ceff, 0xceb5d6ff, 0xdecee7ff,
            0x6b5273ff, 0x5a4a63ff, 0x4a3963ff, 0x422163ff, 0x290821ff, 0x31084aff, 0x6b107bff, 0x7b10a5ff,
            0x8c10deff, 0xbd18bdff, 0xbd4ad6ff, 0xc673deff, 0xd694ceff, 0xd69cefff, 0x632163ff, 0x4a2142ff,
            0x52084aff, 0x420873ff, 0x420818ff, 0x630821ff, 0x94105aff, 0xbd104aff, 0xe7104aff, 0xef18c6ff,
            0xef4ad6ff, 0xef73d6ff, 0xf794deff, 0xf7b5efff, 0x84316bff, 0x6b314aff, 0x7b1039ff, 0x8c0818ff,
    };
    
    public static final int[] GRAY = {
            0x00000000, 0x010101FF, 0x131313FF, 0x252525FF, 0x373737FF, 0x494949FF, 0x5B5B5BFF, 0x6E6E6EFF,
            0x808080FF, 0x929292FF, 0xA4A4A4FF, 0xB6B6B6FF, 0xC9C9C9FF, 0xDBDBDBFF, 0xEDEDEDFF, 0xFFFFFFFF,
    };
    public static final int[] GRAY8 = {
            0x00000000, 0x131313FF, 0x373737FF, 0x5B5B5BFF, 
            0x808080FF, 0xA4A4A4FF, 0xC9C9C9FF, 0xEDEDEDFF,
    };
    public static final int[] DB8 = {
            0x00000000,
            0x000000FF, 0x55415FFF, 0x646964FF, 0xD77355FF, 0x508CD7FF, 0x64B964FF, 0xE6C86EFF, 0xDCF5FFFF,
    };
    public static final int[] FADED16 = {
//            0x00000000,
//            0x262626FF, 0x666666FF, 0xA5A5A5FF, 0xE5E5E5FF, 0x60193CFF, 0x474705FF, 0x1E6542FF, 0x515193FF,
//            0x96589FFF, 0xA27652FF, 0x68A65FFF, 0x75A2C5FF, 0xB9A4E7FF, 0xE9ABB6FF, 0xC4DA96FF, 0xAEECE1FF,
            0x00000000,
            0x262626FF, 0x666666FF, 0xA5A5A5FF, 0xE5E5E5FF, 0x591D3CFF, 0x46450EFF, 0x256143FF, 0x52538AFF,
            0x8D5C9CFF, 0xA0755DFF, 0x71A262FF, 0x77A3BBFF, 0xB1A9DFFF, 0xE2ACBFFF, 0xCCD49EFF, 0xB5EBD8FF,
//            0x00000000,
//            0x262626FF, 0x666666FF, 0xA5A5A5FF, 0xE5E5E5FF, 0x67153EFF, 0x484800FF, 0x176941FF, 0x50509CFF,
//            0xA54CB2FF, 0xB2723FFF, 0x59B24CFF, 0x66A6D8FF, 0xBBA0F5FF, 0xF4A6B4FF, 0xC3DD89FF, 0xA3F1E3FF,
    };
    public static final int[] BOLD16 = {
            0x00000000,
            0x262626FF, 0x666666FF, 0xA5A5A5FF, 0xE5E5E5FF, 0x8F0331FF, 0x3E5700FF, 0x007B4EFF, 0x5A41CEFF,
            0xC537C8FF, 0xC37116FF, 0x39C736FF, 0x55A7FFFF, 0xC098FFFF, 0xFF9BADFF, 0xBDE66AFF, 0x8BFCEAFF,
//            0x00000000,
//            0x262626FF, 0x666666FF, 0xA5A5A5FF, 0xE5E5E5FF, 0x8E0048FF, 0x504D00FF, 0x007F36FF, 0x484BCCFF,
//            0xB63BDBFF, 0xD26529FF, 0x48C323FF, 0x45B2EEFF, 0xB19FFFFF, 0xFF96C0FF, 0xCDDE68FF, 0x8AFFD7FF,
//            0x00000000,
//            0x262626FF, 0x666666FF, 0xA5A5A5FF, 0xE5E5E5FF, 0x820743FF, 0x4D4C00FF, 0x00783BFF, 0x4B4CBDFF,
//            0xBE39D2FF, 0xCB6B1FFF, 0x40C52CFF, 0x4DADF8FF, 0xBC9EFFFF, 0xFCA2B2FF, 0xC1E07EFF, 0x9BF5E5FF,
    };
    
    public static final int[]  BLK36 = {
            0x00000000,
            0x000000FF, 0x12173DFF, 0x293268FF, 0x464B8CFF, 0x6B74B2FF, 0x909EDDFF, 0xC1D9F2FF, 0xFFFFFFFF,
            0xFFCCD0FF, 0xE5959FFF, 0xC16A7DFF, 0x8C4B63FF, 0x66334BFF, 0x3F233CFF, 0x29174CFF, 0x412866FF,
            0x643499FF, 0x8C51CCFF, 0xB991F2FF, 0xA5E6FFFF, 0x5AB9E5FF, 0x4185D8FF, 0x354AB2FF, 0x36277FFF,
            0x0A2A33FF, 0x0F4A4CFF, 0x14665BFF, 0x22896EFF, 0x42BC7FFF, 0x8CFF9BFF, 0xFFE091FF, 0xFF965FFF,
            0xCC5250FF, 0x872A38FF, 0xD83843FF, 0xFF6866FF,
    };
    
    public static final int[] SHELTZY32 = {
            0x00000000,
            0x8CFFDEFF, 0x45B8B3FF, 0x839740FF, 0xC9EC85FF, 0x46C657FF, 0x158968FF, 0x2C5B6DFF, 0x222A5CFF,
            0x566A89FF, 0x8BABBFFF, 0xCCE2E1FF, 0xFFDBA5FF, 0xCCAC68FF, 0xA36D3EFF, 0x683C34FF, 0x000000FF,
            0x38002CFF, 0x663B93FF, 0x8B72DEFF, 0x9CD8FCFF, 0x5E96DDFF, 0x3953C0FF, 0x800C53FF, 0xC34B91FF,
            0xFF94B3FF, 0xBD1F3FFF, 0xEC614AFF, 0xFFA468FF, 0xFFF6AEFF, 0xFFDA70FF, 0xF4B03CFF, 0xFFFFFFFF
    };

    /**
     * A 64-color mix of <a href="https://lospec.com/palette-list/fleja-master-palette">Fleja's Master Palette</a> with
     * <a href="https://lospec.com/palette-list/resurrect-32">Resurrect 32 by Kerrie Lake</a>. Some very similar colors
     * have been removed from the overlap, and the range of green and purple coverage has been expanded. I'd say this is
     * a good option if we want to use less total colors relative to Rinsed or Aurora. The color count is low, so the
     * Colorizer objects that use this have the shade and wave bits set, enabling extra animation options.
     * <p>
     * This is sorted so the first element is transparent, then indices 1 to 9 are grayscale (or close to it), 10 to 13
     * are brownish-gray and so don't have an especially useful hue, and the rest are sorted by hue (red-green-blue).
     */
    public static final int[] FLESURRECT = {
            0x00000000, 0x1F1833FF, 0x2B2E42FF, 0x3E3546FF,
            0x414859FF, 0x68717AFF, 0x90A1A8FF, 0xB6CBCFFF,
            0xD3E5EDFF, 0xFFFFFFFF, 0x5C3A41FF, 0x826481FF,
            0x966C6CFF, 0x715A56FF, 0xAB947AFF, 0xF68181FF,
            0xF53333FF, 0x5A0A07FF, 0xAE4539FF, 0x8A503EFF,
            0xCD683DFF, 0xFBA458FF, 0xFB6B1DFF, 0xDDBBA4FF,
            0xFDD7AAFF, 0xFFA514FF, 0xC29162FF, 0xE8B710FF,
            0xFBE626FF, 0xC0B510FF, 0xFBFF86FF, 0xB4D645FF,
            0x729446FF, 0xC8E4BEFF, 0x45F520FF, 0x51C43FFF,
            0x0E4904FF, 0x55F084FF, 0x1EBC73FF, 0x30E1B9FF,
            0x7FE0C2FF, 0xB8FDFFFF, 0x039F78FF, 0x63C2C9FF,
            0x216981FF, 0x7FE8F2FF, 0x5369EFFF, 0x4D9BE6FF,
            0x28306FFF, 0x5C76BFFF, 0x4D44C0FF, 0x180FCFFF,
            0x53207DFF, 0x8657CCFF, 0xA884F3FF, 0x630867FF,
            0xA03EB2FF, 0x881AC4FF, 0xE4A8FAFF, 0xB53D86FF,
            0xF34FE9FF, 0x7A3045FF, 0xF04F78FF, 0xC93038FF,
    };
    
    public static final PaletteReducer FLESURRECT_REDUCER = new PaletteReducer(FLESURRECT,
            "\001\001\001\001\001\001\001\001\001\001\001000000043333333333333\001\001\001\001\001\001\001\001\001\001\001000000003333333333333\001\001\001\001\001\001\001\001\001\001\002000000003333333333333\001\001\001\001\001\001\001\001\001\001\002000000000333333333333"+
                    "\001\001\001\001\001\001\001\001\001\002\002000000000333333333333$\001\001\001\001\001\001\001\002\002\002000000000333333333333$$$\001\001\001\001\002\002\002\002000000000333333333333$$$$$\002\002\002\002\002\0020000000,,22333333333."+
                    "$$$$$$\002\002\002\002\002000000,,,2222333333..$$$$$$$\002\002\002\002\004000,,,,,,2222233....$$$$$$$$\002\004\004\00400,,,,,,,222222.....$$$$$$$$\004\004\004\004,,,,,,,,,222222....."+
                    "$$$$$$$$\004\004\004,,,,,,,,,,,2222......$$$$$$$\004,,,,,,,,,,,,,,1222......$$$$$$$,,,,,,,,,,,,,,*1111......$$$$$,,,,,,,,,,,,,,****111//...."+
                    "$$$$********,,,,,******11///////$ **********************1/////// ***********************////////##**********************////////"+
                    "####********************////////#####&&****************&'///////######&&&&&&&&&&&&&&&&&&'''/////######&&&&&&&&&&&&&&&&&''''''///"+
                    "#######&&&&&&&&&&&&&&''''''''''/#######&&&&&&&&&&&&&''''''''''''\"\"\"\"\"###&&&&&&&&&&''''''''''''''\"\"\"\"\"\"\"\"\"&&&&&&&''''''''''''''''"+
                    "\"\"\"\"\"\"\"\"\"\"\"&&%%'''''''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%''''''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%%'''''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%%%''''''''''''''"+
                    "\001\001\001\001\001\001\001\001\001\001\001000000043333333333333\001\001\001\001\001\001\001\001\001\001\001000000043333333333333\001\001\001\001\001\001\001\001\001\001\002000000004333333333333\001\001\001\001\001\001\001\001\001\002\002000000004333333333333"+
                    "\001\001\001\001\001\001\001\001\001\002\002000000000333333333333$\001\001\001\001\001\001\001\002\002\002000000000333333333333$$$\001\001\001\001\002\002\002\002000000000233333333333$$$$$\002\002\002\002\002\00200000000,22233333333."+
                    "$$$$$$\002\002\002\002\002000000,,,222223333...$$$$$$$\002\002\002\004\004000,,,,,,2222223....$$$$$$$$\002\004\004\00400,,,,,,,222222.....$$$$$$$$\004\004\004\004\004,,,,,,,,222222....."+
                    "$$$$$$$\004\004\004\004\004,,,,,,,,,,2222......$$$$$$$\004\004,,,,,,,,,,,,,1111......$$$$$$,,,,,,,,,,,,,,,,1111......$$$$$,,,,,,,,,,,,,,,**1111//...."+
                    "$$$$******,,,,,,,,*****11//////.   ********************11///////   ********************1////////###*********************////////"+
                    "#####*******************////////######&&&&*************&'///////#######&&&&&&&&&&&&&&&&&'''/////#######&&&&&&&&&&&&&&&&''''''///"+
                    "########&&&&&&&&&&&&&''''''''''/########&&&&&&&&&&&&''''''''''''\"\"\"\"\"###&&&&&&&&&&''''''''''''''\"\"\"\"\"\"\"\"\"&&&&&&&''''''''''''''''"+
                    "\"\"\"\"\"\"\"\"\"\"\"%%%%%''''''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%%'''''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%%%''''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%%%''''''''''''''"+
                    "\001\001\001\001\001\001\001\001\001\001\001000000444333333333333\001\001\001\001\001\001\001\001\001\001\001000000444333333333333\001\001\001\001\001\001\001\001\001\001\002000000044333333333333\001\001\001\001\001\001\001\001\001\002\002000000044333333333333"+
                    "\001\001\001\001\001\001\001\001\002\002\002000000004333333333333$\001\001\001\001\001\001\001\002\002\002000000000233333333333$$$\001\001\001\001\002\002\002\002000000000223333333333$$$$$\002\002\002\002\002\00200000000,2222333333.."+
                    "$$$$$$\002\002\002\002\002\00400000,,,222222333...$$$$$$\002\002\002\002\004\004000,,,,,22222222....$$$$$$$\002\004\004\004\004\0040,,,,,,,222222.....$$$$$$$\004\004\004\004\004\004,,,,,,,,222222....."+
                    "$$$$$$$\004\004\004\004\004,,,,,,,,,22222......$$$$$$\004\004\004,,,,,,,,,,,,11111......$$$$$$\004,,,,,,,,,,,,,,11111......$$$$$,,,,,,,,,,,,,,,**1111//...."+
                    "$$$  **,,,,,,,,,,,****111//////.     ******************11///////    *******************1////////###********************1////////"+
                    "#####******************&////////#######&&&&&&*********&&'///////#######&&&&&&&&&&&&&&&&'''//////########&&&&&&&&&&&&&&&'''''////"+
                    "########&&&&&&&&&&&&&'''''''''//#########&&&&&&&&&&'''''''''''''\"\"\"\"#####&&&&&&&&&''''''''''''''\"\"\"\"\"\"\"\"\"#&&&&&&''''''''''''''''"+
                    "\"\"\"\"\"\"\"\"\"\"\"%%%%%''''''''''''''''\"\"\"\"\"\"\"\"\"\"\"%%%%%%'''''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%%%''''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%%%%'''''''''''''"+
                    "\001\001\001\001\001\001\001\001\001\001\001000004444333333333333\001\001\001\001\001\001\001\001\001\001\002000004444333333333333\001\001\001\001\001\001\001\001\001\001\002000000444333333333333\001\001\001\001\001\001\001\001\001\002\002000000044333333333333"+
                    "\001\001\001\001\001\001\001\001\002\002\002000000044333333333333\001\001\001\001\001\001\001\002\002\002\002000000004233333333333$$$\001\001\001\001\002\002\002\00200000000422233333333.$$$$\002\002\002\002\002\002\00200000000,2222233333.."+
                    "$$$$$\002\002\002\002\002\004\00400000,,,22222223....$$$$$$\002\002\002\002\004\004000,,,,,22222222....$$$$$$$\004\004\004\004\004\0040,,,,,,,222222.....$$$$$$$\004\004\004\004\004\004,,,,,,,,222222....."+
                    "$$$$$$\004\004\004\004\004\004\004,,,,,,,,22222......$$$$$$\004\004\004\004,,,,,,,,,,,11111......$$$$$$\004,,,,,,,,,,,,,,11111......$$$$$,,,,,,,,,,,,,,,,11111//...."+
                    "$     ,,,,,,,,,,,,,***111//////.      ****************111///////     *****************111///////     ******************1////////"+
                    "######*****************+////////#######&&&&&&&&&&*****&++///////########&&&&&&&&&&&&&&&+''//////########&&&&&&&&&&&&&&&'''''////"+
                    "#########&&&&&&&&&&&&'''''''''//#########&&&&&&&&&&'''''''''''''\"\"\"\"######&&&&&&&&''''''''''''''\"\"\"\"\"\"\"\"\"#&&&&&&''''''''''''''''"+
                    "\"\"\"\"\"\"\"\"\"\"\"%%%%%%'''''''''''''''\"\"\"\"\"\"\"\"\"\"\"%%%%%%%''''''''''''''\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%'''''''''''''\"\"\"\"\"\"\"\"\"\"\"\"%%%%%%%'''''''''''''"+
                    "\001\001\001\001\001\001\001\001\001\001\001770044444333333333333\001\001\001\001\001\001\001\001\001\001\002000004444333333333333\001\001\001\001\001\001\001\001\001\002\002000004444333333333333\001\001\001\001\001\001\001\001\001\002\002000000444333333333333"+
                    "\001\001\001\001\001\001\001\001\002\002\002000000444233333333333\001\001\001\001\001\001\001\002\002\002\002000000044222333333333$$\001\001\001\001\002\002\002\002\00200000004422223333333.$$$$\002\002\002\002\002\002\002\00400000002222222333..."+
                    "$$$$$\002\002\002\002\002\004\00400000,,,22222222....$$$$$$\002\002\002\003\004\004\004000,,,,22222222....$$$$$$\003\004\004\004\004\004\0040,,,,,,2222222.....$$$$$$\004\004\004\004\004\004\004,,,,,,,,222222....."+
                    "$$$$$$\004\004\004\004\004\004\004,,,,,,,,22222......$$$$$$\004\004\004\004,,,,,,,,,,,11111......$$$$$\004\004\004,,,,,,,,,,,,,11111......$$$$  ,,,,,,,,,,,,,,,11111//...."+
                    "        ,,,,,,,,,,,**11111/////.        ********,,***1111///////       ***************111///////      ****************11////////"+
                    "#######***************&+////////########&&&&&&&&&&&&&&&++///////#########&&&&&&&&&&&&&&+++//////#########&&&&&&&&&&&&&++'''/////"+
                    "##########&&&&&&&&&&&''''''''///##########&&&&&&&&&''''''''''''/\"\"\"\"#######&&&&&&'''''''''''''''\"\"\"\"\"\"\"\"\"##&&&%%''''''''''''''''"+
                    "\"\"\"\"\"\"\"\"\"\"\"%%%%%%'''''''''''''''\"\"\"\"\"\"\"\"\"\"\"%%%%%%%''''''''''''''\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%'''''''''''''\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%''''''''''''"+
                    "\001\001\001\001\001\001\001\001\001\001\001777444444333333333333\001\001\001\001\001\001\001\001\001\001\002700044444333333333333\001\001\001\001\001\001\001\001\001\002\002000044444333333333333\001\001\001\001\001\001\001\001\002\002\002000004444333333333333"+
                    "\001\001\001\001\001\001\001\001\002\002\002000004444223333333333\001\001\001\001\001\001\001\002\002\002\002000000444222333333333$$\001\001\001\001\002\002\002\002\002\004000000442222233333..$$$$\002\002\002\002\002\002\003\00400000042222222233..."+
                    "$$$$$\002\002\002\002\003\004\004\0040000,,,22222222....$$$$$\003\003\003\003\003\004\004\004000,,,,22222222....$$$$$$\003\004\004\004\004\004\004\004,,,,,,2222222.....$$$$$$\004\004\004\004\004\004\004,,,,,,,2222222....."+
                    "$$$$$\004\004\004\004\004\004\004\004,,,,,,,,22222......$$$$$\004\004\004\004\004\004,,,,,,,,,,11111......$$$$$\004\004\004,,,,,,,,,,,,,11111......$$$    ,,,,,,,,,,,,,111111//...."+
                    "        ,,,,,,,,,,,,111111/////.         *****,,,,***1111///////         ************1111///////        **************11////////"+
                    "########**************+++///////#########&&&&&&&&&&&&&+++///////#########&&&&&&&&&&&&&++++//////##########&&&&&&&&&&&&++++'/////"+
                    "##########&&&&&&&&&&'''''''''///###########&&&&&&&&''''''''''''/\"\"\"\"#######&&&&&&'''''''''''''''\"\"\"\"\"\"\"\"####%%%%%'''''''''''''''"+
                    "\"\"\"\"\"\"\"\"\"\"\"%%%%%%%'''''''''''''-\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%''''''''''''-\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%'''''''''''-\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%'''''''''''-"+
                    "\001\001\001\001\001\001\001\001\001\0017777744444333333333333\001\001\001\001\001\001\001\001\001\001\002777444444333333333333\001\001\001\001\001\001\001\001\001\002\002700044444333333333333\001\001\001\001\001\001\001\001\002\002\002000044444233333333333"+
                    "\001\001\001\001\001\001\001\001\002\002\002000004444222333333333\001\001\001\001\001\001\001\002\002\002\00300000444422223333333.$$\001\001\001\002\002\002\002\003\003\004000004442222223333..$$$\002\002\002\002\002\002\003\003\0040000044222222222...."+
                    "$$$$\002\002\003\003\003\003\004\004\0040000,,222222222....$$$$$\003\003\003\003\003\004\004\004000,,,,2222222.....$$$$$\003\004\004\004\004\004\004\004\004,,,,,,2222222.....$$$$$\004\004\004\004\004\004\004\004\004,,,,,,2222222....."+
                    "$$$$$\004\004\004\004\004\004\004\004,,,,,,,,11222......$$$$$\004\004\004\004\004\004\004,,,,,,,,111111......$$$$\r\r\r\004\004,,,,,,,,,,,111111......$$      ,,,,,,,,,,,,111111/....."+
                    "         ,,,,,,,,,,,111111////..           *,,,,,,,*11111///////          ***********1111///////          ***********111////////"+
                    "#########&&&*********&+++///////##########&&&&&&&&&&&&+++///////##########&&&&&&&&&&&+++++//////###########&&&&&&&&&&++++++/////"+
                    "###########&&&&&&&&&'''++''''///############&&&&&&&'''''''''''//\"\"\"#########&&&&&''''''''''''''-\"\"\"\"\"\"\"\"####%%%%%''''''''''''''-"+
                    "\"\"\"\"\"\"\"\"\"\"\"%%%%%%%'''''''''''''-\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%''''''''''''-\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%'''''''''''-\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%'''''''''--"+
                    "\021\021\001\001\001\001\001\001\001\0017777744444333333333333\021\001\001\001\001\001\001\001\001\0017777444444433333333333\021\001\001\001\001\001\001\001\001\002\002777444444433333333333\021\001\001\001\001\001\001\001\002\002\002000444444423333333333"+
                    "\001\001\001\001\001\001\001\002\002\002\003\00300044444222233333333$\001\001\001\001\002\002\002\002\003\003\0030004444422222333333.$$\001\002\002\002\002\002\003\003\003\00400004444222222233...$$$\002\002\002\002\003\003\003\003\004\004000044222222222...."+
                    "$$$$\003\003\003\003\003\003\004\004\00400004,222222222....$$$$\003\003\003\003\003\004\004\004\004\00400,,,,2222222.....$$$$$\003\004\004\004\004\004\004\004\004,,,,,,2222222.....$$$$$\004\004\004\004\004\004\004\004\004,,,,,,2222222....."+
                    "$$$$\004\004\004\004\004\004\004\004\004,,,,,,,111111......$$$$\004\004\004\004\004\004\004\004,,,,,,,,111111......$$$\r\r\r\r\r\r\r,,,,,,,,,,111111......        \r,,,,,,,,,,,111111/....."+
                    "           ,,,,,,,,,111111////..            ,,,,,,,*11111///////            ********11111///////           **********111////////"+
                    "######### &&&&&&*****++++///////##########&&&&&&&&&&&+++++//////###########&&&&&&&&&&+++++//////###########&&&&&&&&&+++++++/////"+
                    "############&&&&&&&&+++++++'////############&&&&&&''''''''''''/-\"\"\"##########&&&%'''''''''''''--\"\"\"\"\"\"\"\"####%%%%%%''''''''''''--"+
                    "\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%'''''''''''--\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%''''''''''--\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%''''''''---\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%'''''''---"+
                    "\021\021\021\021\021\001\001\001\001\0017777744444433333333333\021\021\021\021\001\001\001\001\001\0027777744444433333333333\021\021\021\021\001\001\001\001\001\0027777444444433333333333\021\021\021\021\001\001\001\002\002\003\003774444444422333333333"+
                    "\021\021\021\001\002\002\002\002\003\003\003\00300444444222223333333\021\021\002\002\002\003\003\003\003\003\003\003004444442222222333..$$\002\003\003\003\003\003\003\003\003\00400044444222222223...$$$\003\003\003\003\003\003\003\003\004\004000444422222222...."+
                    "$$$\003\003\003\003\003\003\003\004\004\004\00400444222222222....$$$$\003\003\003\003\003\004\004\004\004\00400,,,22222222.....$$$$\n\n\004\004\004\004\004\004\004\004\004,,,,,2222222.....$$$$\n\n\004\004\004\004\004\004\004\004,,,,,,2222222....."+
                    "$$$\n\n\004\004\004\004\004\004\004\004\004,,,,,,111111......$$$\r\r\r\r\r\r\r\r\004\005\005\005,,,,,111111......$$\r\r\r\r\r\r\r\r\r\005\005\005\005,,,,,1111111.....         \005\005\005\005\005\005\005,,,,1111111....."+
                    "           \005\005\005\005\005,,,,111111////..             \005\005\005,,,1111111//////              ******11111///////             *******11111///////"+
                    "#####       &&&&&&&&*++++///////###########&&&&&&&&&++++++//////############&&&&&&&&+++++++/////############&&&&&&&&+++++++/////"+
                    "#############&&&&&&+++++++++////#############&&&&&'''''++++'''--\"\"\"##########%%%%%'''''''''''---\"\"\"\"\"\"\"\"###%%%%%%%%''''''''''---"+
                    "\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%'''''''''---\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%'''''''''---\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%'''''''----\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%''''''----"+
                    "\021\021\021\021\021\021\021\021777777744444433333333333\021\021\021\021\021\021\021\001\00277777744444493333333333\021\021\021\021\021\021\021\002\003\0037777444444423333333333\021\021\021\021\021\021\003\003\003\003\003777444444422233333333"+
                    "\021\021\021\021\003\003\003\003\003\003\003\n7444444422222233333.\021\021\021\021\003\003\003\003\003\003\003\n004444442222222233..\021\021\021\003\003\003\003\003\003\003\003\004\0040444444222222222...$$\003\003\003\003\003\003\003\003\004\004\004004444422222222...."+
                    "$$$\003\003\003\003\003\003\003\004\004\004\00404444222222222....$$$\n\003\003\003\003\003\004\004\004\004\004004,,22222222.....$$$\n\n\n\n\004\004\004\004\004\004\004\004\005,,,22222222.....$$$\n\n\n\n\004\004\004\004\004\004\004\005\005,,,,2222222....."+
                    "$$\n\n\n\n\004\004\004\004\004\004\004\005\005\005\005,,,111111......$$\r\r\r\r\r\r\r\r\r\r\005\005\005\005\005,,,111111......$\r\r\r\r\r\r\r\r\r\r\005\005\005\005\005\005\005,,1111111.....         \005\005\005\005\005\005\005\005\005\00511111111....."+
                    "           \005\005\005\005\005\005\005\0051111111////..             \005\005\005\005\005\0051111111//////               \005\005\005\005\00611111///////               ****\00611111///////"+
                    "             &&&&&&\006\006++++///////############&&&&&&&&++++++//////############&&&&&&&&+++++++/////#############&&&&&&+++++++++////"+
                    "#############&&&&&&+++++++++////##############&&&&''+++++++++---\"\"###########%%%%%''''''''+''---\"\"\"\"\"\"\"####%%%%%%%%''''''''''---"+
                    "\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%''''''''----\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%'''''''----\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%'''''-----\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%'''------"+
                    "\021\021\021\021\021\021\021\021777777774444499933333333\021\021\021\021\021\021\021\021777777744444499933333333\021\021\021\021\021\021\021\021777777444444499993333333\021\021\021\021\021\021\021\003\003\0037777444444422993333333"+
                    "\021\021\021\021\021\n\n\n\n\003\003\n7444444422222223333.\021\021\021\021\n\n\n\n\n\n\003\n\n4444444222222223...\021\021\021\n\n\n\n\n\n\n\n\n\004444444422222222....\021\021\n\n\n\n\n\n\n\n\n\004\004\00444444422222222...."+
                    "$\n\n\n\n\n\n\n\n\n\004\004\004\00404444222222222....$\n\n\n\n\n\n\n\n\004\004\004\004\004\r444\01322222222.....$$\n\n\n\n\n\n\004\004\004\004\004\004\r\005\005\005\01322222222.....$$\n\n\n\n\n\n\004\004\004\004\004\r\005\005\005\005\005\013222222......"+
                    "$\n\n\n\n\n\r\r\r\r\r\r\r\005\005\005\005\005\005\005111111......$\r\r\r\r\r\r\r\r\r\r\r\005\005\005\005\005\005\00511111111.....\r\r\r\r\r\r\r\r\r\r\r\005\005\005\005\005\005\005\00511111111.....         \r\005\005\005\005\005\005\005\005\00511111111....."+
                    "           \005\005\005\005\005\005\005\0051111111////..             \005\005\005\005\005\0051111111//////               \005\005\005\005\00611111///////                 \006\006\006\0061111///////"+
                    "               &&\006\006\006\006\006++++//////#############&&&&&&\006++++++//////#############&&&&&&++++++++/////##############&&&&++++++++++////"+
                    "##############&&&&++++++++++///-###############&&%+++++++++++---\"\"###########%%%%%%''''+++++----\"\"\"\"\"\"\"####%%%%%%%%%''''''''----"+
                    "\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%''''''-----\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%'''''-----\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%%'''------\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%%'-------"+
                    "\021\021\021\021\021\021\0217777777774444999999933333\021\021\021\021\021\021\0217777777744444999999993333\021\021\021\021\021\021\021\021777777744444999999993333\021\021\021\021\021\021\n\n\n77777444444999999999333"+
                    "\021\021\021\021\021\n\n\n\n\n\n7744444442222999993..\021\021\021\021\n\n\n\n\n\n\n\n\n4444444222222222...\021\021\021\n\n\n\n\n\n\n\n\n\n444444422222222....\021\n\n\n\n\n\n\n\n\n\n\n\004\00444444422222222...."+
                    "\n\n\n\n\n\n\n\n\n\n\n\004\004\00444444222222222....\n\n\n\n\n\n\n\n\n\n\004\004\004\r\r\r\013\013\01322222222.....\n\n\n\n\n\n\n\n\n\004\004\004\004\r\r\005\005\013\013\0132222222.....\n\n\n\n\n\n\n\n\n\004\004\004\r\r\005\005\005\005\013\0132222225....."+
                    "\n\n\n\n\n\r\r\r\r\r\r\r\r\r\005\005\005\005\005\013111111......\r\r\r\r\r\r\r\r\r\r\r\r\r\005\005\005\005\005\005\0131111111.....\023\r\r\r\r\r\r\r\r\r\r\005\005\005\005\005\005\005\005\0051111111.....         \r\005\005\005\005\005\005\005\005\00511111111....."+
                    "            \005\005\005\005\005\005\0051111111///...              \005\005\005\005\005\006111111//////                \005\005\005\006\0061111///////                \006\006\006\006\006\00611+///////"+
                    "                \006\006\006\006\006\006++++//////##############&&&\006\006\006\006\006+++++/////##############&&&&+++++++++/////###############&&&++++++++++////"+
                    "###############&&++++++++++++---################%%++++++++++----\"############%%%%%%'++++++++----\"\"\"\"\"\"\"####%%%%%%%%%''((((((----"+
                    "\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%'(((((-----\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%((((------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%%((-------\"\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%%--------"+
                    "\021\021\021\021\021\021\0217777777774444999999999993\021\021\021\021\021\021\0217777777744444999999999993\021\021\021\021\021\021\0217777777744444999999999999\021\021\021\021\021\021\n\n77777744444499999999999."+
                    "\021\021\021\021\021\n\n\n\n\n\n7774444449999999999..\021\021\021\021\n\n\n\n\n\n\n\n74444444222299999...\021\021\021\n\n\n\n\n\n\n\n\n\n444444422222222....\021\n\n\n\n\n\n\n\n\n\n\n\n444444422222222...."+
                    "\n\n\n\n\n\n\n\n\n\n\n\n\004\r\r4444222222225....\n\n\n\n\n\n\n\n\n\n\n\r\r\r\r\013\013\013\013\01322222255....\n\n\n\n\n\n\n\n\n\r\r\r\r\r\r\005\013\013\013\0132222255.....\n\n\n\n\n\n\r\r\r\r\r\r\r\r\005\005\013\013\013\0131122555....."+
                    "\n\n\n\n\r\r\r\r\r\r\r\r\r\r\005\005\005\013\013\0131111115.....\r\r\r\r\r\r\r\r\r\r\r\r\r\005\005\005\005\005\013\0131111111.....\023\023\r\r\r\r\r\r\r\r\r\005\005\005\005\005\005\005\005\0131111111.....         \r\005\005\005\005\005\005\005\005\005\0051111111....."+
                    "            \005\005\005\005\005\005\005\0061111111//...              \005\005\005\005\005\006\00611111//////                \005\005\006\006\006\0061111//////                \006\006\006\006\006\006\0061+///////"+
                    "               \006\006\006\006\006\006\006\006+++//////###########    \006\006\006\006\006\006\006+++++/////###############&\006\006\006\006++++++++////################&+++++++++++////"+
                    "################&++++++++++++---###############%%+++++++++++----\"############%%%%%%((((++++(----\"\"\"\"\"\"#####%%%%%%%%%((((((((----"+
                    "\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%((((((-----\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%((((------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%(((-------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%%(--------"+
                    "\021\021\021\021\021\021\0217777777774444999999999999\021\021\021\021\021\021\0217777777774444999999999999\021\021\021\021\021\021=7777777744444999999999999\021\021\021\021\021\021===77777744444999999999999"+
                    "\021\021\021\021=======77744444499999999999.\021\021\021\021=========44444448999999999..\021\021\n\n\n\n\n======4444444822229995...\021\n\n\n\n\n\n\n\n\n\n===444448222222555..."+
                    "\n\n\n\n\n\n\n\n\n\n\r\r\r\r\r4\013\013\013822222555....\n\n\n\n\n\n\n\n\r\r\r\r\r\r\r\013\013\013\013\01322222555....\n\n\n\n\n\n\r\r\r\r\r\r\r\r\r\013\013\013\013\01322225555....\n\n\n\r\r\r\r\r\r\r\r\r\r\r\r\013\013\013\013\01311155555...."+
                    "\023\023\r\r\r\r\r\r\r\r\r\r\r\r\005\005\013\013\013\0131111155.....\023\r\r\r\r\r\r\r\r\r\r\r\r\005\005\005\005\013\013\0131111115.....\023\023\r\r\r\r\r\r\r\r\r\r\005\005\005\005\005\005\013\0131111111.....         \r\005\005\005\005\005\005\005\005\005\0131111111....."+
                    "            \005\005\005\005\005\005\005\0061111111//...              \005\005\005\005\005\006\00611111//////                \005\006\006\006\006\0061111//////                \006\006\006\006\006\006\0061++//////"+
                    "               \006\006\006\006\006\006\006\006+++//////######         \006\006\006\006\006\006\006+++++/////###############\006\006\006\006\006\006+++++++////################\006+++++++++++//--"+
                    "################++++++++++++----###############%%+++++++++++----\"############%%%%%((((((((((----\"\"\"\"\"\"#####%%%%%%%%(((((((((----"+
                    "\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%(((((((-----\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%(((((------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%(((-------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%%(--------"+
                    "\021\021\021\021\021\02177777777774444999999999999\021\021\021\021\021\021=7777777774444999999999999\021\021\021\021\021====77777744444999999999999\021\021\021\021=======777744444999999999999"+
                    "\021\021\021==========444444499999999999.\021\021===========44444448999999999..\021=============444448889999995...==============444448882225555..."+
                    "===========\r\r\r\r\013\013\013\0138882255555...\023\023\023\023\023==\r\r\r\r\r\r\r\r\013\013\013\013\01388255555....\023\023\023\023\023\023\r\r\r\r\r\r\r\r\r\013\013\013\013\01388555555....\023\023\023\023\023\r\r\r\r\r\r\r\r\r\r\013\013\013\013\01381555555...."+
                    "\023\023\023\023\023\r\r\r\r\r\r\r\r\r\005\013\013\013\013\01311115555....\023\023\023\023\r\r\r\r\r\r\r\r\r\005\005\005\013\013\013\0131111155.....\023\023\023\r\r\r\r\r\r\r\r\r\005\005\005\005\005\013\013\0131111115.....         \r\005\005\005\005\005\005\005\005\013\0131111111....."+
                    "            \005\005\005\005\005\005\005\006\006111111//...              \005\005\005\005\006\006\006\0061111/////6                \006\006\006\006\006\006\006111//////                \006\006\006\006\006\006\006\006++//////"+
                    "               \006\006\006\006\006\006\006\006++++/////##            \006\006\006\006\006\006\006\006\006++++/////##############\006\006\006\006\006\006\006\006++++++////###############\006\006\006\006++++++++++---"+
                    "################++++++++++++----###############%%(+++++++++(----#############%%%%%((((((((((----\"\"\"\"\"\"#####%%%%%%%((((((((((----"+
                    "\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%(((((((-----\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%((((((------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%((((-------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%%((--------"+
                    "\021\021\021\021\021==7777777777449999999999999\021\021\021\021=====77777774444999999999999\021\021\021\021=======777774444999999999999\021\021\021=========77444444999999999999"+
                    "\021\021============44444899999999999.\021=============4444488999999999..==============4444488899999955..===============444\0138888855555..."+
                    "\023\023\023\023\023=======\r\r\r\013\013\013\0138888555555...\023\023\023\023\023\023\023\023\023\r\r\r\r\r\013\013\013\013\0138888555555...\023\023\023\023\023\023\023\023\023\r\r\r\r\r\013\013\013\013\013\013885555555...\023\023\023\023\023\023\023\023\r\r\r\r\r\r\013\013\013\013\013\01388555555...."+
                    "\023\023\023\023\023\023\023\023\r\r\r\r\r\013\013\013\013\013\013\01311155555....\023\023\023\023\023\023\023\r\r\r\r\r\r\005\013\013\013\013\013\01311115555....\023\023\023\023\023\023\r\r\r\r\r\r\005\005\005\005\013\013\013\01311111555....         \r\r\005\005\005\005\005\005\013\013\013\006111115....6"+
                    "            \005\005\005\005\005\005\013\006\006\00611111/6666              \005\005\005\005\006\006\006\006\006111///666                \006\006\006\006\006\006\006\00611////66               \006\006\006\006\006\006\006\006\006++/////6"+
                    "              \016\006\006\006\006\006\006\006\006\006+++/////\035\035           \016\016\006\006\006\006\006\006\006\006+++++////\035\035############\006\006\006\006\006\006\006\006\006+++++///-\035##############\006\006\006\006\006+++++++++---"+
                    "\035###############+++++++++++(----###############%((((((++++((----#############%%%%(((((((((((----\"\"\"\"\"\037\037\037\037\037\037\037%%%%%%((((((((((----"+
                    "\"\"\"\"\"\"\"\"\037\037%%%%%%%%%((((((((-----\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%((((((------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%((((-------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%(((--------"+
                    "\021\021\021\021=====77777777449999999999999\021\021\021========777774449999999999999\021\021===========7444449999999999999\021=============444448999999999999"+
                    "==============444448899999999999===============4444888999999999.===============444888889999955..\023\023\023\023===========\013\013\013888888555555.."+
                    "\023\023\023\023\023\023\023\023\023\023\023\023==\013\013\013\013\01388888555555..\023\023\023\023\023\023\023\023\023\023\023\023\023\013\013\013\013\013\0138888555555...\023\023\023\023\023\023\023\023\023\023\023\023\r\013\013\013\013\013\013\013888555555...\023\023\023\023\023\023\023\023\023\023\023\023\013\013\013\013\013\013\013\013885555555..."+
                    "\023\023\023\023\023\023\023\023\023\023\023\r\013\013\013\013\013\013\013\013885555555...\023\023\023\023\023\023\023\023\023\023\r\f\013\013\013\013\013\013\013\01311155555..66\023\023\023\023\023\023\023\023\023\f\f\f\f\013\013\013\013\013\013\013\00611155556666           \f\f\013\013\013\013\013\013\006\006\0061115566666"+
                    "            \f\005\005\005\005\013\013\006\006\006\006111166666              \f\f\005\013\006\006\006\006\006\00611166666               \016\006\006\006\006\006\006\006\006\0061/66666              \016\016\006\006\006\006\006\006\006\006\006+//6666"+
                    "\035            \016\016\016\006\006\006\006\006\006\006\006+++//666\035\035\035\035        \016\016\016\006\006\006\006\006\006\006\006\006++++//66\035\035\035\035########\016\016\016\006\006\006\006\006\006\006\006+++++/---\035\035\035\035##########\016\006\006\006\006\006\006+++++++----"+
                    "\035\035\035############\037\006++++++++++(----\035\035####\037\037\037\037\037\037\037\037\037\037((((((((((((----\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037%((((((((((((----\"\037\037\037\037\037\037\037\037\037\037\037\037%%%%(((((((((((----"+
                    "\"\"\"\"\"\037\037\037\037\037\037\037%%%%%%(((((((((-----\"\"\"\"\"\"\"\"\037\037%%%%%%%%%(((((((------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%(((((-------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%%(((--------"+
                    "\021\021=========777774449999999999999\021===========77744449999999999999==============444449999999999999==============444448999999999999"+
                    "===============44488899999999999===============4448888999999999.\023\023\023\023============448888899999955.\023\023\023\023\023\023\023\023\023\023\023====\013\013\0138888889555555."+
                    "\023\023\023\023\023\023\023\023\023\023\023\023\023\023\013\013\013\013\01388888555555..\023\023\023\023\023\023\023\023\023\023\023\023\023\013\013\013\013\013\01388885555555..\023\023\023\023\023\023\023\023\023\023\023\023\023\013\013\013\013\013\01388885555555..\023\023\023\023\023\023\023\023\023\023\023\023\f\f\013\013\013\013\013\013885555555.66"+
                    "\023\023\023\023\023\023\023\023\023\023\023\f\f\f\f\013\013\013\013\013885555555666\023\023\023\023\023\023\023\023\023\023\f\f\f\f\f\013\013\013\013\013\f15555556666\023\023\023\023\023\023\023\023\023\f\f\f\f\f\f\f\013\013\013\f\00611555556666         \f\f\f\f\f\f\f\013\013\013\006\006\0061155566666"+
                    "           \f\f\f\f\f\f\013\006\006\006\006\006111666666             \f\f\f\f\f\006\006\006\006\006\00611666666              \016\016\016\006\006\006\006\006\006\006\0061666666             \016\016\016\006\006\006\006\006\006\006\006\006+666666"+
                    "\035\035          \016\016\016\016\006\006\006\006\006\006\006\006\006++66666\035\035\035\035\035      \016\016\016\016\016\006\006\006\006\006\006\006\006++++6666\035\035\035\035\035\035\035###\016\016\016\016\016\016\006\006\006\006\006\006\006\006++++\007---\035\035\035\035\035\035######\037\016\016\006\006\006\006\006\006\006\006++++\007----"+
                    "\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\006\006\006+++++++((----\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037((((((((((((----\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037((((((((((((----\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037%((((((((((((----"+
                    "\"\"\037\037\037\037\037\037\037\037\037\037\037%%%%((((((((((-----\"\"\"\"\"\037\037\037\037\037\037\037%%%%%%((((((((------\"\"\"\"\"\"\"\"\037\037%%%%%%%%%((((((-------\"\"\"\"\"\"\"\"\"\"%%%%%%%%%%((((--------"+
                    "\021===========77744449999999999999==============444449999999999999==============444448999999999999===============44488899999999999"+
                    "===============;;;88889999999999\022\023\023============;;;88888999999999\023\023\023\023\023\023\023\023\023\023====;;;;8888889999955.\023\023\023\023\023\023\023\023\023\023\023\023\023\023;;;88888888555555."+
                    "\023\023\023\023\023\023\023\023\023\023\023\023\023\023\f\013\013\0138888885555555.\023\023\023\023\023\023\023\023\023\023\023\023\023\f\f\f\013\013\0138888855555566\023\023\023\023\023\023\023\023\023\023\023\023\f\f\f\f\013\013\0138888555555566\023\023\023\023\023\023\023\023\023\023\023\f\f\f\f\f\f\013\013\013888555555666"+
                    "\023\023\023\023\023\023\023\023\023\023\f\f\f\f\f\f\f\013\013\013885555556666\023\023\023\023\023\023\023\023\023\f\f\f\f\f\f\f\f\f\013\f\f85555556666\023\023\023\023\023\023\023\023\f\f\f\f\f\f\f\f\f\f\f\f\006\0065555566666\023       \f\f\f\f\f\f\f\f\f\f\f\f\006\006\006555566666"+
                    "          \f\f\f\f\f\f\f\f\f\006\006\006\006\00655666666            \f\f\f\016\016\016\006\006\006\006\006\006\0061666666             \016\016\016\016\006\006\006\006\006\006\006\006\006666666\035           \016\016\016\016\016\006\006\006\006\006\006\006\006\006666666"+
                    "\035\035\035\035       \016\016\016\016\016\016\006\006\006\006\006\006\006\006\007\00766666\035\035\035\035\035\035    \016\016\016\016\016\016\006\006\006\006\006\006\006\006\007\007\007\0076666\035\035\035\035\035\035\035\035\035\016\016\016\016\016\016\016\006\006\006\006\006\006\006\006\007\007\007\007\007666\035\035\035\035\035\035\035\037\037\037\037\037\037\016\016\016\006\006\006\006\006\006\006\007\007\007\007\007\007---"+
                    "\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\006\006\006\007\007\007\007\007\007\007\007\007----\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037((((((\007\007\007\007(----\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037((((((((((((----\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037((((((((((((----"+
                    "\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037%%((((((((((-----\"\"\037\037\037\037\037\037\037\037\037\037\037%%%%%((((((((------\"\"\"\"\"\037\037\037\037\037\037\037%%%%%%%((((((-------\"\"\"\"\"\"\"\"\037\037%%%%%%%%%%((((-----)))"+
                    "\022============;;;;999999999999999\022\022\022\022=========;;;;;99999999999999\022\022\022\022\022\022=======;;;;;88999999999999\022\022\022\022\022\022\022\022\022===;;;;;;88899999999999"+
                    "\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;88889999999999\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;88888999999999\022\022\022\022\022\022\022\022\022\022\022\022\022;;;;;88888899999555\022\022\022\022\022\022\022\022\022\022\022\022\022;;;;;88888885555556"+
                    "\022\022\022\022\022\022\022\022\022\022\022\022\023;;;;;88888885555556\022\022\022\022\022\022\022\022\023\023\023\023\f\f\f\f;;88888855555566\022\022\022\022\022\023\023\023\023\023\023\f\f\f\f\f\f\f88888855555566\022\022\023\023\023\023\023\023\023\023\f\f\f\f\f\f\f\f\f8888555555666"+
                    "\023\023\023\023\023\023\023\023\023\f\f\f\f\f\f\f\f\f\f\f888555556666\023\023\023\023\023\023\023\023\f\f\f\f\f\f\f\f\f\f\f\f\f85555556666\023\023\023\023\023\023\023\f\f\f\f\f\f\f\f\f\f\f\f\f\006\0065555566666\023      \f\f\f\f\f\f\f\f\f\f\f\f\f\006\006\006555666666"+
                    "         \f\f\f\f\f\f\016\016\016\016\006\006\006\006\00655666666           \016\016\016\016\016\016\016\016\006\006\006\006\006\0066666666\035          \016\016\016\016\016\016\016\006\006\006\006\006\006\0066666666\035\035         \016\016\016\016\016\016\016\006\006\006\006\006\006\006\007666666"+
                    "\035\035\035\035\035     \016\016\016\016\016\016\016\006\006\006\006\006\006\006\006\007\00766666\035\035\035\035\035\035\035\035 \016\016\016\016\016\016\016\016\006\006\006\006\006\006\006\007\007\007\0076666\035\035\035\035\035\035\035\035\035\016\016\016\016\016\016\016\016\006\006\006\006\006\006\006\007\007\007\007\007666\035\035\035\035\035\035\035\037\037\037\037\037\037\016\016\016\006\006\006\006\006\006\007\007\007\007\007\007\007---"+
                    "\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\006\007\007\007\007\007\007\007\007\007\007\007---\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037(\007\007\007\007\007\007\007\007\007\007----\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037(((((((\007\007\007\007----\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037(((((((((((----"+
                    "\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037(((((((((((-----\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037%%(((((((((-----)\"\"\037\037\037\037\037\037\037\037\037\037\037%%%%%(((((((----)))\"\"\"\"\"\037\037\037\037\037\037\037%%%%%%%((((())))))))"+
                    "???????????;;;;;;;99999999999999\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;9999999999999\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;8999999999999\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;8889999999999"+
                    "\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;888899999999<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;888889999999<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;88888899995<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;88888885555<<"+
                    "\022\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;8888885555566\022\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;8888855555566\022\022\022\022\022\022\022\022\022\022\022\022\f\f\f;;;;8888855555666\022\022\022\022\022\022\022\022\022\022\022\f\f\f\f\f\f;;8888855555666"+
                    "\022\022\022\022\022\022\022\022\022\f\f\f\f\f\f\f\f\f\f\f888555556666\022\022\022\022\022\022\022\022\f\f\f\f\f\f\f\f\f\f\f\f885555566666\022\022\022\022\022\022\022\f\f\f\f\f\f\f\f\f\f\f\f\016\016\0065555566666\035\022\022\022\022 \f\f\f\f\f\f\f\f\f\016\016\016\016\016\006\006\006555666666"+
                    "\035\035\035     \f\016\016\016\016\016\016\016\016\016\016\016\006\006\006\00656666666\035\035\035\035    \016\016\016\016\016\016\016\016\016\016\016\016\006\006\006\006\0066666666\035\035\035\035\035   \016\016\016\016\016\016\016\016\016\016\016\006\006\006\006\006\0066666666\035\035\035\035\035\035  \016\016\016\016\016\016\016\016\016\016\016\006\006\006\006\006\006\007666666"+
                    "\035\035\035\035\035\035\035 \016\016\016\016\016\016\016\016\016\016\006\006\006\006\006\006\007\007\00766666\035\035\035\035\035\035\035\035\035\016\016\016\016\016\016\016\016\016\006\006\006\006\006\006\007\007\007\0076666\035\035\035\035\035\035\035\035\035\032\016\016\016\016\016\016\016\006\006\006\006\006\006\007\007\007\007\007\007666\035\035\035\035\035\035\035\035\037\037\037\037\037\016\016\016\016\006\006\006\006\007\007\007\007\007\007\007\007\007--"+
                    "\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\007\007\007\007\007\007\007\007\007\007\007\007\007--\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\007\007\007\007\007\007\007\007\007\007\007\007---\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!\007\007\007\007\007\007\007\007\007\007---\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!\007\007\007\007---)"+
                    "\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!---))\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!--)))\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037%!!!!!!!!!)))))))\"\037\037\037\037\037\037\037\037\037\037\037\037\037%%!!!!!!!!))))))))"+
                    "????????????;;;;;;99999999999999????????????;;;;;;;9999999999999????????????;;;;;;;;999999999999???\022\022\022\022\022\022\022\022;;;;;;;;;88999999999<"+
                    "\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;8889999999<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;8888999999<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;888889999<<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;888888555<<<"+
                    "\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;888885555<<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;8888885555666\022\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;8888855555666\022\022\022\022\022\022\022\022\022\022\022\022\f\f;;;;;8888855556666"+
                    "\022\022\022\022\022\022\022\022\022\022\022\f\f\f\f\f\f;;8888555556666\022\022\022\022\022\022\022\022\022\022\f\f\f\f\f\f\f\f\f\016888555566666\022\022\022\022\022\022\022\024\f\f\f\f\f\f\f\016\016\016\016\016\016\0165555666666\035\024\024\024\024\024\024\032\032\032\032\016\016\016\016\016\016\016\016\016\016\006\006555666666"+
                    "\035\035\035\035\024\032\032\032\032\032\032\016\016\016\016\016\016\016\016\016\016\006\006\006\0066666666\035\035\035\035\035\032\032\032\032\032\016\016\016\016\016\016\016\016\016\016\006\006\006\006\0066666666\035\035\035\035\035\035\032\032\032\032\016\016\016\016\016\016\016\016\016\016\006\006\006\006\0066666666\035\035\035\035\035\035\035\032\032\032\016\016\016\016\016\016\016\016\016\006\006\006\006\006\006\007666666"+
                    "\035\035\035\035\035\035\035\035\032\032\016\016\016\016\016\016\016\016\016\006\006\006\006\006\007\007\007\0076666\035\035\035\035\035\035\035\035\035\032\016\016\016\016\016\016\016\016\006\006\006\006\006\006\007\007\007\0076666\035\035\035\035\035\035\035\035\035\032\032\016\016\016\016\016\016\016\006\006\006\006\006\007\007\007\007\007\007\007::\035\035\035\035\035\035\035\035\037\037\037\037\037\016\016\016\016\016\006\006\007\007\007\007\007\007\007\007\007\007::"+
                    "\035\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\027\007\007\007\007\007\007\007\007\007\007\007\007\b\b\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\027\007\007\007\007\007\007\007\007\007\007\007\b\b\b\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!\007\007\007\007\007\007\007\007\007\b\b)\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!\007\007\007\b\b))"+
                    "\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!!\b\b))\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!)))))\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!))))))\034\034\034\037\037\037\037\037\037\037\037\037\037\037\036\036!!!!!!!!))))))))"+
                    "?????????????;;;;;99999999999999?????????????;;;;;;9999999999999????????????;;;;;;;;99999999999<????????????;;;;;;;;8899999999<<"+
                    "????????????;;;;;;;;8888999999<<????????????;;;;;;;;888889999<<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;88888899<<<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;88888855<<<<"+
                    "\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;88888855<<<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;88888555<<<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;888885555666\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;888855556666"+
                    "\022\022\022\022\022\022\022\022\022\022\022\024\024;;;;;;;888855566666\024\024\024\024\024\024\024\024\024\024\024\024\032\032\f\016\016\016;;888555566666\024\024\024\024\024\024\024\024\024\024\032\032\032\032\016\016\016\016\016\016\016\0168555666666\024\024\024\024\024\024\024\024\032\032\032\032\032\032\016\016\016\016\016\016\016\016\006556666666"+
                    "\035\035\035\024\024\024\032\032\032\032\032\032\032\032\016\016\016\016\016\016\016\016\006\006\0066666666\035\035\035\035\035\032\032\032\032\032\032\032\032\016\016\016\016\016\016\016\016\006\006\006\0066666666\035\035\035\035\035\035\032\032\032\032\032\032\032\016\016\016\016\016\016\016\016\006\006\006\0066666666\035\035\035\035\035\035\035\032\032\032\032\032\032\016\016\016\016\016\016\016\006\006\006\006\007\007666666"+
                    "\035\035\035\035\035\035\035\035\032\032\032\032\032\016\016\016\016\016\016\016\006\006\006\006\007\007\007\0076666\035\035\035\035\035\035\035\035\032\032\032\032\032\016\016\016\016\016\016\006\006\006\006\007\007\007\007\007\007:::\035\035\035\035\035\035\035\035\035\032\032\032\016\016\016\016\016\016\016\006\006\006\006\007\007\007\007\007\007:::\035\035\035\035\035\035\035\035\037\037\037\037\037\032\016\016\016\016\006\007\007\007\007\007\007\007\007\007\007\007::"+
                    "\035\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\027\027\027\007\007\007\007\007\007\007\007\007\007\b\b\b\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\027\027\027\007\007\007\007\007\007\007\007\007\007\b\b\b\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!\007\007\007\007\007\007\007\b\b\b\b\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!\007\b\b\b)"+
                    "\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!\b\b\b))\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!!\b\b)))\034\034\034\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!))))))\034\034\034\034\034\034\037\037\037\037\037\037\036\036\036\036\036!!!!!!!))))))))"+
                    "??????????????;;;;9999999999999<??????????????;;;;;999999999999<?????????????;;;;;;;9999999999<<?????????????;;;;;;;;89999999<<<"+
                    "?????????????;;;;;;;;88899999<<<????????????;;;;;;;;;8888999<<<<????????????;;;;;;;;;888889<<<<<????????????;;;;;;;;;888888<<<<<"+
                    "\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;;888885<<<<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;;888885<<<<<\022\022\022\022\022\022\022\022\022\022\022\022;;;;;;;;;8888555<<<<\022\022\024\024\024\024\024\024\024\024\024\024\024;;;;;;;;88855556666"+
                    "\024\024\024\024\024\024\024\024\024\024\024\024\024\024;;;;;;;88855566666\024\024\024\024\024\024\024\024\024\024\024\024\024\032\032\032\032;;;;88555666666\024\024\024\024\024\024\024\024\024\024\024\032\032\032\032\032\032\016\016\016\016\0165555666666\024\024\024\024\024\024\024\024\024\032\032\032\032\032\032\032\032\016\016\016\016\016\016\00656666666"+
                    "\035\035\024\024\024\024\024\032\032\032\032\032\032\032\032\032\032\016\016\016\016\016\006\006\0066666666\035\035\035\035\035\032\032\032\032\032\032\032\032\032\032\032\032\016\016\016\016\016\006\006\0066666666\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\032\016\016\016\016\016\006\006\006\0066666666\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\016\016\016\016\016\006\006\006\007\007\00766666"+
                    "\035\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\016\016\016\016\006\006\006\007\007\007\007\0076:::\035\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\016\016\016\016\006\007\007\007\007\007\007\007::::\035\035\035\035\035\035\035\035\035\032\032\032\032\032\032\032\016\016\016\027\007\007\007\007\007\007\007\007\007:::\035\035\035\035\035\035\035\035\037\037\037\037\037\032\032\016\016\027\027\027\007\007\007\007\007\007\007\007\007:::"+
                    "\035\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\027\027\027\027\007\007\007\007\007\007\007\007\007\b\b\b\035\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\027\027\027\027\007\007\007\007\007\007\007\007\b\b\b\b\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\037\027\027!!!!!\007\007\007\007\007\b\b\b\b\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!!\b\b\b\b"+
                    "\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!\b\b\b\b)\034\034\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!!\b\b\b))\034\034\034\034\034\037\037\037\037\037\037\037\037\036\036\036!!!!!!!!!!\b)))))\034\034\034\034\034\034\034\034\034\037\037\036\036\036\036\036\036\036!!!!!!!)))))))"+
                    "???????????????;;;;99999999999<<???????????????;;;;;9999999999<<??????????????;;;;;;;99999999<<<??????????????;;;;;;;;999999<<<<"+
                    "??????????????;;;;;;;;888999<<<<?????????????;;;;;;;;;88889<<<<<?????????????;;;;;;;;;88888<<<<<?????????????;;;;;;;;;8888<<<<<<"+
                    "????????????;;;;;;;;;;8888<<<<<<????????????;;;;;;;;;;8888<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024;;;;;;;;;8885<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024\024;;;;;;;;8885<<<<<<"+
                    "\024\024\024\024\024\024\024\024\024\024\024\024\024\024\024;;;;;;888555<6666\024\024\024\024\024\024\024\024\024\024\024\024\024\024\032\032\032\032;;;85555666666\024\024\024\024\024\024\024\024\024\024\024\024\032\032\032\032\032\032\032\032\032\0165556666666\024\024\024\024\024\024\024\024\024\024\032\032\032\032\032\032\032\032\032\032\032\016\016556666666"+
                    "\035\024\024\024\024\024\024\024\032\032\032\032\032\032\032\032\032\032\032\032\032\016\016\00666666666\035\035\035\035\024\024\032\032\032\032\032\032\032\032\032\032\032\032\032\032\016\016\027\027\0276666666\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\032\032\032\032\032\016\027\027\027\027\02766666:\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\032\032\032\032\027\027\027\027\007\007\0076::::"+
                    "\035\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\032\027\027\027\027\027\027\007\007\007\007::::\035\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\027\027\027\027\027\027\007\007\007\007\007::::\035\035\035\035\035\035\035\035\035\037\032\032\032\032\032\032\027\027\027\027\027\027\007\007\007\007\007\007::::\035\035\035\035\035\035\035\035\037\037\037\037\037\037\032\027\027\027\027\027\027\027\007\007\007\007\007\007\007:::"+
                    "\035\035\035\035\035\035\035\037\037\037\037\037\037\037\037\027\027\027\027\027\027\007\007\007\007\007\007\007\b\b\b:\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\027\027\027\027\027\027\007\007\007\007\007\007\007\b\b\b\b\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\037\027\027\027!!!!!\007\007\007\007\b\b\b\b\033\037\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!\b\b\b\b\b"+
                    "\033\037\037\037\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!!\b\b\b\b)\034\034\034\034\034\037\037\037\037\037\037\037\037\037\036\036!!!!!!!!!!\b\b\b\b\b)\034\034\034\034\034\034\034\034\037\037\037\037\036\036\036\036\036\036!!!!!!!!\b\b))))\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036!!!!!)))))))"+
                    "????????????????;;;9999999999<<<???????????????;;;;;999999999<<<???????????????;;;;;;9999999<<<<???????????????;;;;;;;999999<<<<"+
                    "???????????????;;;;;;;;8999<<<<<??????????????;;;;;;;;;888<<<<<<??????????????;;;;;;;;8888<<<<<<??????????????;;;;;;;;8888<<<<<<"+
                    "?????????????;;;;;;;;;8888<<<<<<????????????\024;;;;;;;;;8888<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024\024;;;;;;;;888<<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024\024\024;;;;;;;888<<<<<<<"+
                    "\024\024\024\024\024\024\024\024\024\024\024\024\024\024\024\024;;;;;;855<<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024\024\024\032\032\032\032\032;;5555<<6666\024\024\024\024\024\024\024\024\024\024\024\024\024\032\032\032\032\032\032\032\032\0325566666666\024\024\024\024\024\024\024\024\024\024\024\032\032\032\032\032\032\032\032\032\032\032\032666666666"+
                    "\024\024\024\024\024\024\024\024\024\032\032\032\032\032\032\032\032\032\032\032\032\027\027\02766666666\035\035\035\024\024\024\024\032\032\032\032\032\032\032\032\032\032\032\032\032\027\027\027\027\027666666:\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\032\032\032\032\027\027\027\027\027\027\02766::::\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\032\032\027\027\027\027\027\027\027\027\027:::::"+
                    "\035\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\032\027\027\027\027\027\027\027\027\007\007:::::\035\035\035\035\035\035\035\035\032\032\032\032\032\032\032\032\027\027\027\027\027\027\027\027\027\007\007\007::::\035\035\035\035\035\035\035\035\037\037\032\032\032\032\032\027\027\027\027\027\027\027\027\027\007\007\007\007::::\035\035\035\035\035\035\035\037\037\037\037\037\037\037\027\027\027\027\027\027\027\027\027\007\007\007\007\007::::"+
                    "\035\035\035\035\035\035\037\037\037\037\037\037\037\037\027\027\027\027\027\027\027\027\027\007\007\007\007\007\b\b\b:\035\035\035\035\035\037\037\037\037\037\037\037\037\037\037\027\027\027\027\027\027\027\007\007\007\007\007\007\b\b\b\b\035\035\035\037\037\037\037\037\037\037\037\037\037\037\037\027\027\027\027\027!!!!!\007\007\b\b\b\b\b\033\033\033\037\037\037\037\037\037\037\037\037\037\037\037\037\027!!!!!!!!!!\b\b\b\b\b"+
                    "\034\034\034\034\037\037\037\037\037\037\037\037\037\037\037!!!!!!!!!!!\b\b\b\b\b\b\034\034\034\034\034\034\034\034\037\037\037\037\037\036\036\036\036!!!!!!!!!\b\b\b\b\b)\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036!!!!!!!\b\b\b\b))\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036!!!!\b))))))"+
                    "?????????????????;;9999999999<<<????????????????;;;;99999999<<<<????????????????;;;;;999999<<<<<????????????????;;;;;;99999<<<<<"+
                    "???????????????;;;;;;;;8<<<<<<<<???????????????;;;;;;;;8<<<<<<<<???????????????>;;;;;;;<<<<<<<<<??????????????>>>>>;;;;<<<<<<<<<"+
                    "?????????????>>>>>>>>><<<<<<<<<<?????????\024\024\024>>>>>>>>>><<<<<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024>>>>>>>>><<<<<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024\024>>>>>>>><<<<<<<<<<"+
                    "\024\024\024\024\024\024\024\024\024\024\024\024\024\024>>>>>>>><<<<<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024\024\024>>>>>>\017<<<<<<<<<<\024\024\024\024\024\024\024\024\024\024\024\024\024\024\032\032\017\017\017\017\017\017\017<<<<<6666\024\024\024\024\024\024\024\024\024\024\024\024\032\032\032\032\032\017\017\017\017\017\017\01766666666"+
                    "\024\024\024\024\024\024\024\024\024\024\032\032\032\032\032\032\032\032\017\017\017\017\027\027666666::\033\033\033\024\024\024\024\024\032\032\032\032\032\032\032\032\032\032\032\027\027\027\027\027\027\02766::::\033\033\033\033\033\033\032\032\032\032\032\032\032\032\032\032\032\032\027\027\027\027\027\027\027\027::::::\033\033\033\033\033\033\033\032\032\032\032\032\032\032\032\032\032\027\027\027\027\027\027\027\027\027\027:::::"+
                    "\033\033\033\033\033\033\033\033\032\032\032\032\032\032\032\027\027\027\027\027\027\027\027\027\027\027\027:::::\033\033\033\033\033\033\033\033\033\032\032\032\032\032\027\027\027\027\027\027\027\027\027\027\027\027\027:::::\033\033\033\033\033\033\033\033\037\037\037\032\032\027\027\027\027\027\027\027\027\027\027\027\027\027\007\007::::\033\033\033\033\033\033\033\037\037\037\037\037\037\027\027\027\027\027\027\027\027\027\027\027\027\007\007\007::::"+
                    "\033\033\033\033\033\033\037\037\037\037\037\037\037\037\027\027\027\027\027\027\027\027\027\027\027\007\007\007\b\b::\033\033\033\033\033\037\037\037\037\037\037\037\037\037\027\027\027\027\027\027\027\027\027\027\007\007\007\b\b\b\b\b\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037\027\027\027\027\027\027!!!!!!\b\b\b\b\b\033\033\033\033\037\037\037\037\037\037\037\037\037\037\037\027\027\027!!!!!!!!!\b\b\b\b\b"+
                    "\034\034\034\034\034\034\034\037\037\037\037\037\037\037\036\036!!!!!!!!!!\b\b\b\b\b\b\034\034\034\034\034\034\034\034\034\034\034\037\036\036\036\036\036\036!!!!!!!!\b\b\b\b\b\b\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036!!!!!\b\b\b\b\b\b)\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\036!!!\b\b\b))))"+
                    "??????????????????;;99999999<<<<?????????????????>>;;99999<<<<<<????????????????>>>>>>9<<<<<<<<<\020??????????????>>>>>>><<<<<<<<<<"+
                    "\020\020????????????>>>>>>>><<<<<<<<<<\020\020???????????>>>>>>>>><<<<<<<<<<\020\020\020?????????>>>>>>>>>><<<<<<<<<<\020\020\020\020????????>>>>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020??????>>>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\024\024\024\024>>>>>>>>>>>><<<<<<<<<<\026\026\024\024\024\024\024\024\024\024\024>>>>>>>>>>><<<<<<<<<<\026\026\024\024\024\024\024\024\024\024\024\024>>>>>>>>>><<<<<<<<<<"+
                    "\026\026\024\024\024\024\024\024\024\024\024\024>>>>>>>>>><<<<<<<<<<\026\026\024\024\024\024\024\024\024\024\024\024\024>>>>>\017\017\017\017\017<<<<<<<<<\026\026\024\024\024\024\024\024\024\024\024\024\024\017\017\017\017\017\017\017\017\017\017<<<<<<<<<\026\026\026\024\024\024\024\024\024\024\024\024\017\017\017\017\017\017\017\017\017\017\017\017<<<<66::"+
                    "\026\026\026\024\024\024\024\024\024\024\024\032\017\017\017\017\017\017\017\017\017\017\017\017\017:::::::\033\033\033\033\033\024\024\024\024\032\032\032\032\017\017\017\017\017\017\017\017\027\027\027\027:::::::\033\033\033\033\033\033\033\033\032\032\032\032\032\032\017\017\017\017\027\027\027\027\027\027\027:::::::\033\033\033\033\033\033\033\033\025\025\025\032\032\032\032\027\027\027\027\027\027\027\027\027\027\027::::::"+
                    "\033\033\033\033\033\033\033\033\033\025\025\025\025\025\027\027\027\027\027\027\027\027\027\027\027\027::::::\033\033\033\033\033\033\033\033\033\025\025\025\025\027\027\027\027\027\027\027\027\027\027\027\027\027\027:::::\033\033\033\033\033\033\033\033\033\033\025\025\025\027\027\027\027\027\027\027\027\027\027\027\027\027\027:::::\033\033\033\033\033\033\033\033\033\037\037\037\027\027\027\027\027\027\027\027\027\027\027\027\027\027\027\007::::"+
                    "\033\033\033\033\033\033\033\033\037\037\037\037\037\027\027\027\027\027\027\027\027\027\027\027\027\027\007\b\b\b::\033\033\033\033\033\033\033\037\037\037\037\037\037\027\027\027\027\027\027\027\027\027\027\027!!\b\b\b\b\b\b\033\033\033\033\033\033\037\037\037\037\037\037\037\037\027\027\027\027\027\027\027!!!!!\b\b\b\b\b\b\033\033\033\033\034\034\037\037\037\037\037\037\037\037\027\027\027\027\027!!!!!!!\b\b\b\b\b\b"+
                    "\034\034\034\034\034\034\034\034\034\034\037\037\037\036\036\036\036!!!!!!!!!\b\b\b\b\b\b\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036!!!!!!\b\b\b\b\b\b\b\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036!!!!\b\b\b\b\b\b\t\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\036\036!\b\b\b\b\t\t\t\t"+
                    "\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020\020\020\020\020\020>>>>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\020\020\020\020>>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\026\026\024>>>>>>>>>>>>><<<<<<<<<<"+
                    "\026\026\026\026\026\026\026\026\024\024>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\026\026\024\024\024>>>>\017\017\017\017\017\017\017\017<<<<<<<<<\026\026\026\026\026\026\026\026\024\024\024\017\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<<<\026\026\026\026\026\026\026\026\024\024\017\017\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<::"+
                    "\026\026\026\026\026\026\026\026\025\025\017\017\017\017\017\017\017\017\017\017\017\017\017\017\017:::::::\031\031\031\031\031\026\026\025\025\025\025\017\017\017\017\017\017\017\017\017\017\017\017\017::::::::\033\033\033\033\033\033\033\025\025\025\025\025\025\017\017\017\017\017\017\017\017\027\027\027::::::::\033\033\033\033\033\033\033\025\025\025\025\025\025\025\025\017\017\017\027\027\027\027\027\027\027:::::::"+
                    "\033\033\033\033\033\033\033\033\025\025\025\025\025\025\025\025\027\027\027\027\027\027\027\027\027:::::::\033\033\033\033\033\033\033\033\025\025\025\025\025\025\025\027\027\027\027\027\027\027\027\027\027\027::::::\033\033\033\033\033\033\033\033\033\025\025\025\025\025\025\027\027\027\027\027\027\027\027\027\027\027::::::\033\033\033\033\033\033\033\033\033\025\025\025\025\025\027\027\027\027\027\027\027\027\027\027\027\027\027:::::"+
                    "\033\033\033\033\033\033\033\033\033\033\025\025\025\027\027\027\027\027\027\027\027\027\027\027\027\027\b\b\b:::\033\033\033\033\033\033\033\033\034\034\037\037\025\027\027\027\027\027\027\027\027\027\027\027\027!\b\b\b\b\b\b\033\033\033\033\034\034\034\034\034\034\034\037\037\027\027\027\027\027\027\027\027\030!!!!\b\b\b\b\b\b\034\034\034\034\034\034\034\034\034\034\034\034\036\036\036\030\030\030\030\030\030!!!!!\b\b\b\b\b\b"+
                    "\034\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\030\030!!!!!!\b\b\b\b\b\b\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\030!!!!\b\b\b\b\b\b\t\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\030!!\b\b\b\b\t\t\t\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\036\036\036\t\t\t\t\t\t\t\t"+
                    "\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>>><<<<<<<<<<\026\020\020\020\020\020\020\020\020\020>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\020\020\020>>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\026\026\026>>>>>>>>>>>>><<<<<<<<<<"+
                    "\026\026\026\026\026\026\026\026\026\026>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\026\026\026\026>>>\017\017\017\017\017\017\017\017\017\017<<<<<<<<<\026\026\026\026\026\026\026\026\026\026\017\017\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<<<\026\026\026\026\026\026\026\026\026\026\017\017\017\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<:"+
                    "\026\026\026\026\026\026\026\026\025\025\017\017\017\017\017\017\017\017\017\017\017\017\017\017\017:::::::\031\031\031\031\031\031\031\025\025\025\025\025\017\017\017\017\017\017\017\017\017\017\017\017::::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\017\017\017\017\017\017\017\017\017\017\017::::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\017\017\017\017\017\017\027\027\027::::::::"+
                    "\033\033\033\033\033\031\031\025\025\025\025\025\025\025\025\025\025\027\027\027\027\027\027\027::::::::\033\033\033\033\033\033\033\025\025\025\025\025\025\025\025\025\025\027\027\027\027\027\027\027\027:::::::\033\033\033\033\033\033\033\033\025\025\025\025\025\025\025\025\027\027\027\027\027\027\027\027\027:::::::\033\033\033\033\033\033\033\033\025\025\025\025\025\025\025\027\027\027\027\027\027\027\027\027\027\027::::::"+
                    "\033\033\033\033\033\033\033\033\033\025\025\025\025\025\025\027\027\027\027\030\030\030\030\030\030\030\030:::::\033\033\033\033\033\034\034\034\034\034\034\025\025\025\030\030\030\030\030\030\030\030\030\030\030\030\b\b\b\b\b:\033\034\034\034\034\034\034\034\034\034\034\034\034\030\030\030\030\030\030\030\030\030\030\030\030\030\b\b\b\b\b\b\034\034\034\034\034\034\034\034\034\034\034\034\036\036\036\030\030\030\030\030\030\030\030\030\030\030\b\b\b\b\b\b"+
                    "\034\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\030\030\030\030\030\030\030\b\b\b\b\b\b\t\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\030\030\030\030\030\b\b\b\b\b\t\t\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\030\030\030\b\t\t\t\t\t\t\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\036\036\036\t\t\t\t\t\t\t\t"+
                    "\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>>><<<<<<<<<<\026\026\020\020\020\020\020\020\020\020>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\026\020\020>>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\026\026\026>>>>>>>>>>>>><<<<<<<<<<"+
                    "\026\026\026\026\026\026\026\026\026\026>>>>>>>>>>>\017<<<<<<<<<<\026\026\026\026\026\026\026\026\026\026\026\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<<<<\026\026\026\026\026\026\026\026\026\026\017\017\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<<<\026\026\026\026\026\026\026\026\026\026\017\017\017\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<:"+
                    "\026\026\026\026\026\026\026\026\025\025\025\017\017\017\017\017\017\017\017\017\017\017\017\017\017:::::::\031\031\031\031\031\031\031\025\025\025\025\025\017\017\017\017\017\017\017\017\017\017\017\017::::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\017\017\017\017\017\017\017\017\017\017::::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\017\017\017\017\017\017\017:::::::::"+
                    "\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\025\017\017\017\027\027\027\027::::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\025\025\027\027\027\027\027\027::::::::\033\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\025\027\030\030\030\030\030\030\030:::::::\033\033\033\033\033\033\033\025\025\025\025\025\025\025\025\025\030\030\030\030\030\030\030\030\030\030::::::"+
                    "\033\033\033\033\033\033\034\034\025\025\025\025\025\025\025\030\030\030\030\030\030\030\030\030\030\030\030:::::\033\033\034\034\034\034\034\034\034\034\034\025\025\025\030\030\030\030\030\030\030\030\030\030\030\030\030\030\b\b\b:\034\034\034\034\034\034\034\034\034\034\034\034\034\030\030\030\030\030\030\030\030\030\030\030\030\030\030\b\b\b\b\t\034\034\034\034\034\034\034\034\034\034\034\034\036\036\036\030\030\030\030\030\030\030\030\030\030\030\030\b\b\b\b\t"+
                    "\034\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\030\030\030\030\030\030\030\030\030\b\t\t\t\t\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\030\030\030\030\030\030\t\t\t\t\t\t\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\030\030\030\t\t\t\t\t\t\t\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\036\036\036\t\t\t\t\t\t\t\t"+
                    "\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>><<<<<<<<<<\020\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>><<<<<<<<<<"+
                    "\020\020\020\020\020\020\020\020\020\020\020>>>>>>>>>>><<<<<<<<<<\026\026\026\020\020\020\020\020\020\020>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\026\026\020\020>>>>>>>>>>>><<<<<<<<<<\026\026\026\026\026\026\026\026\026\026>>>>>>>>>>>><<<<<<<<<<"+
                    "\026\026\026\026\026\026\026\026\026\026\026>>>>>>>\017\017\017\017<<<<<<<<<<\026\026\026\026\026\026\026\026\026\026\026\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<<<<\026\026\026\026\026\026\026\026\026\026\017\017\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<<<\026\026\026\026\026\026\026\026\026\026\017\017\017\017\017\017\017\017\017\017\017\017\017\017\017<<<<<<<"+
                    "\026\026\026\026\026\026\026\026\025\025\025\017\017\017\017\017\017\017\017\017\017\017\017\017\017:::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\017\017\017\017\017\017\017\017\017\017\017\017:::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\017\017\017\017\017\017\017\017\017\017::::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\017\017\017\017\017\017\017:::::::::"+
                    "\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\025\025\017\017\017\017\030:::::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\025\025\025\030\030\030\030\030::::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\025\030\030\030\030\030\030\030\030:::::::\031\031\031\031\031\031\031\025\025\025\025\025\025\025\025\025\030\030\030\030\030\030\030\030\030\030\030:::::"+
                    "\031\031\031\031\034\034\034\034\025\025\025\025\025\025\025\030\030\030\030\030\030\030\030\030\030\030\030\030::::\034\034\034\034\034\034\034\034\034\034\034\025\025\025\030\030\030\030\030\030\030\030\030\030\030\030\030\030\030:::\034\034\034\034\034\034\034\034\034\034\034\034\034\030\030\030\030\030\030\030\030\030\030\030\030\030\030\030\030\t\t\t\034\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\030\030\030\030\030\030\030\030\030\030\030\030\t\t\t\t"+
                    "\034\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\030\030\030\030\030\030\030\030\030\t\t\t\t\t\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\030\030\030\030\030\030\t\t\t\t\t\t\034\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\030\030\030\t\t\t\t\t\t\t\034\034\034\034\034\034\034\034\034\034\036\036\036\036\036\036\036\036\036\036\036\036\036\036\t\t\t\t\t\t\t\t"
    );
    /**
     * The default 256-color VGA palette, upscaled to RGBA8888 as per
     * <a href="https://commons.wikimedia.org/wiki/File:VGA_palette_with_black_borders.svg">this image and the .sh
     * script that generated it</a>. The first index is transparent rather than black, but the last 8 indices are all
     * identical black colors.
     */
    public static final int[] VGA256 = {
            0x00000000, 0x0000AAFF, 0x00AA00FF, 0x00AAAAFF, 0xAA0000FF, 0xAA00AAFF, 0xAA5500FF, 0xAAAAAAFF,
            0x555555FF, 0x5555FFFF, 0x55FF55FF, 0x55FFFFFF, 0xFF5555FF, 0xFF55FFFF, 0xFFFF55FF, 0xFFFFFFFF,
            0x000000FF, 0x101010FF, 0x202020FF, 0x353535FF, 0x454545FF, 0x555555FF, 0x656565FF, 0x757575FF,
            0x8A8A8AFF, 0x9A9A9AFF, 0xAAAAAAFF, 0xBABABAFF, 0xCACACAFF, 0xDFDFDFFF, 0xEFEFEFFF, 0xFFFFFFFF,
            0x0000FFFF, 0x4100FFFF, 0x8200FFFF, 0xBE00FFFF, 0xFF00FFFF, 0xFF00BEFF, 0xFF0082FF, 0xFF0041FF,
            0xFF0000FF, 0xFF4100FF, 0xFF8200FF, 0xFFBE00FF, 0xFFFF00FF, 0xBEFF00FF, 0x82FF00FF, 0x41FF00FF,
            0x00FF00FF, 0x00FF41FF, 0x00FF82FF, 0x00FFBEFF, 0x00FFFFFF, 0x00BEFFFF, 0x0082FFFF, 0x0041FFFF,
            0x8282FFFF, 0x9E82FFFF, 0xBE82FFFF, 0xDF82FFFF, 0xFF82FFFF, 0xFF82DFFF, 0xFF82BEFF, 0xFF829EFF,
            0xFF8282FF, 0xFF9E82FF, 0xFFBE82FF, 0xFFDF82FF, 0xFFFF82FF, 0xDFFF82FF, 0xBEFF82FF, 0x9EFF82FF,
            0x82FF82FF, 0x82FF9EFF, 0x82FFBEFF, 0x82FFDFFF, 0x82FFFFFF, 0x82DFFFFF, 0x82BEFFFF, 0x829EFFFF,
            0xBABAFFFF, 0xCABAFFFF, 0xDFBAFFFF, 0xEFBAFFFF, 0xFFBAFFFF, 0xFFBAEFFF, 0xFFBADFFF, 0xFFBACAFF,
            0xFFBABAFF, 0xFFCABAFF, 0xFFDFBAFF, 0xFFEFBAFF, 0xFFFFBAFF, 0xEFFFBAFF, 0xDFFFBAFF, 0xCAFFBAFF,
            0xBAFFBAFF, 0xBAFFCAFF, 0xBAFFDFFF, 0xBAFFEFFF, 0xBAFFFFFF, 0xBAEFFFFF, 0xBADFFFFF, 0xBACAFFFF,
            0x000071FF, 0x1C0071FF, 0x390071FF, 0x550071FF, 0x710071FF, 0x710055FF, 0x710039FF, 0x71001CFF,
            0x710000FF, 0x711C00FF, 0x713900FF, 0x715500FF, 0x717100FF, 0x557100FF, 0x397100FF, 0x1C7100FF,
            0x007100FF, 0x00711CFF, 0x007139FF, 0x007155FF, 0x007171FF, 0x005571FF, 0x003971FF, 0x001C71FF,
            0x393971FF, 0x453971FF, 0x553971FF, 0x613971FF, 0x713971FF, 0x713961FF, 0x713955FF, 0x713945FF,
            0x713939FF, 0x714539FF, 0x715539FF, 0x716139FF, 0x717139FF, 0x617139FF, 0x557139FF, 0x457139FF,
            0x397139FF, 0x397145FF, 0x397155FF, 0x397161FF, 0x397171FF, 0x396171FF, 0x395571FF, 0x394571FF,
            0x515171FF, 0x595171FF, 0x615171FF, 0x695171FF, 0x715171FF, 0x715169FF, 0x715161FF, 0x715159FF,
            0x715151FF, 0x715951FF, 0x716151FF, 0x716951FF, 0x717151FF, 0x697151FF, 0x617151FF, 0x597151FF,
            0x517151FF, 0x517159FF, 0x517161FF, 0x517169FF, 0x517171FF, 0x516971FF, 0x516171FF, 0x515971FF,
            0x000041FF, 0x100041FF, 0x200041FF, 0x310041FF, 0x410041FF, 0x410031FF, 0x410020FF, 0x410010FF,
            0x410000FF, 0x411000FF, 0x412000FF, 0x413100FF, 0x414100FF, 0x314100FF, 0x204100FF, 0x104100FF,
            0x004100FF, 0x004110FF, 0x004120FF, 0x004131FF, 0x004141FF, 0x003141FF, 0x002041FF, 0x001041FF,
            0x202041FF, 0x282041FF, 0x312041FF, 0x392041FF, 0x412041FF, 0x412039FF, 0x412031FF, 0x412028FF,
            0x412020FF, 0x412820FF, 0x413120FF, 0x413920FF, 0x414120FF, 0x394120FF, 0x314120FF, 0x284120FF,
            0x204120FF, 0x204128FF, 0x204131FF, 0x204139FF, 0x204141FF, 0x203941FF, 0x203141FF, 0x202841FF,
            0x2D2D41FF, 0x312D41FF, 0x352D41FF, 0x3D2D41FF, 0x412D41FF, 0x412D3DFF, 0x412D35FF, 0x412D31FF,
            0x412D2DFF, 0x41312DFF, 0x41352DFF, 0x413D2DFF, 0x41412DFF, 0x3D412DFF, 0x35412DFF, 0x31412DFF,
            0x2D412DFF, 0x2D4131FF, 0x2D4135FF, 0x2D413DFF, 0x2D4141FF, 0x2D3D41FF, 0x2D3541FF, 0x2D3141FF,
            0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF
    };

    /**
     * An auto-generated palette made by churning out over a thousand random colors and repeatedly merging the most
     * similar pair of colors, until it reached 63 colors (plus transparent, for 64 total); this uses an earlier
     * version of the algorithm used by {@link #QUORUM64} and {@link #QUORUM128}.
     */
    public static final int[] QUORUM64_ALPHA = {
            0x00000000, 0x010101FF, 0x1B2933FF, 0x4E5762FF, 0x7B7B7BFF, 0xA29B8BFF, 0xC1C1C1FF, 0xD2CEE9FF,
            0xF7F8FFFF, 0x70101CFF, 0x293300FF, 0x293383FF, 0x003A5AFF, 0x0A5B35FF, 0x573E0AFF, 0x007A15FF,
            0x049244FF, 0x9A0A32FF, 0x5F0E4AFF, 0xB31D6CFF, 0x1B6101FF, 0x501E95FF, 0x007562FF, 0x7B5C0EFF,
            0x1EBE6BFF, 0x595EAAFF, 0xE61E6FFF, 0x8A2E6DFF, 0x439327FF, 0x339087FF, 0x86634AFF, 0xA86232FF,
            0x6A77D9FF, 0xAF41A0FF, 0x20E778FF, 0x528ABFFF, 0x34A9AEFF, 0xAC586CFF, 0xDB488BFF, 0x69A64EFF,
            0x9F729EFF, 0xFA6DA7FF, 0xA88E4BFF, 0x9A85C0FF, 0x6DB58BFF, 0xA6B657FF, 0xD08659FF, 0x87ABEBFF,
            0xB475DDFF, 0x33FBA8FF, 0xD87E9AFF, 0x55DEB9FF, 0x86D77FFF, 0x71E1EBFF, 0x71F897FF, 0xEAB66CFF,
            0xFFB09FFF, 0xE1A8BAFF, 0xCADE8EFF, 0xF298F5FF, 0xF8DF7AFF, 0xAEFBE0FF, 0xF2F8BAFF, 0xC2FFA9FF,
    };
    /**
     * An auto-generated palette made by churning out over a thousand random colors and repeatedly merging the most
     * similar pair of colors, until it reached 63 colors (plus transparent, for 64 total). {@link #QUORUM128} and
     * {@link #QUORUM256} are similar palettes that use 128 and 256 total colors, respectively.
     */
    public static final int[] QUORUM64 = {
            0x00000000, 0x010101FF, 0x212121FF, 0x414141FF, 0x616161FF, 0x818181FF, 0xA1A1A1FF, 0xC1C1C1FF,
            0xE1E1E1FF, 0xFFFFFFFF, 0x057B4BFF, 0x750000FF, 0x405816FF, 0x81247BFF, 0x19529EFF, 0x2A6457FF,
            0x008A92FF, 0x55454BFF, 0x9C002EFF, 0xB60058FF, 0x494273FF, 0x40824EFF, 0x7649A1FF, 0xAA2F47FF,
            0x4F5EB7FF, 0xCC1C5EFF, 0x57778BFF, 0x43A26EFF, 0x6B6843FF, 0x885A6FFF, 0x30949FFF, 0x728A55FF,
            0x7D4604FF, 0xD0484AFF, 0x42B79EFF, 0x9677AAFF, 0x6BA09AFF, 0xA1906AFF, 0xE43A96FF, 0x7D9FCAFF,
            0x86B068FF, 0xD162A2FF, 0xBB7C54FF, 0xB6A550FF, 0x79BBA4FF, 0xAF8FC2FF, 0x9E9EDFFF, 0x99A3A8FF,
            0x60C5CCFF, 0xD4A149FF, 0x92CF69FF, 0xD2AAFBFF, 0x96D1D1FF, 0xD29FB0FF, 0xB6BCA1FF, 0x84D8F8FF,
            0xBABCE1FF, 0xCFE29FFF, 0xEF9686FF, 0xFB6B97FF, 0xFCF5ABFF, 0xBBF8D9FF, 0xF9B4DCFF, 0xE0D9D9FF,
    };
    /**
     * An auto-generated palette made by churning out over a thousand random colors and repeatedly merging the most
     * similar pair of colors, until it reached 127 colors (plus transparent, for 128 total). {@link #QUORUM64} and
     * {@link #QUORUM256} are similar palettes that use 64 and 256 total colors, respectively.
     */
    public static final int[] QUORUM128 = {
            0x00000000, 0x010101FF, 0x171717FF, 0x2D2D2DFF, 0x555555FF, 0x686868FF, 0x7B7B7BFF, 0x8D8D8DFF,
            0x9F9F9FFF, 0xB0B0B0FF, 0xC1C1C1FF, 0xD1D1D1FF, 0xE1E1E1FF, 0xF0F0F0FF, 0xFFFFFFFF, 0x104B89FF,
            0x812400FF, 0x065B5AFF, 0x5B2A1DFF, 0x6E0591FF, 0x9C0C08FF, 0x640F59FF, 0x027C25FF, 0x2A7303FF,
            0xB90342FF, 0x009E0BFF, 0x5B36AEFF, 0xDC0029FF, 0x326551FF, 0x534D33FF, 0x278046FF, 0x93105DFF,
            0x0787CDFF, 0x157B89FF, 0xBD039CFF, 0x33920FFF, 0x7D3583FF, 0x7E4437FF, 0x129955FF, 0x921CA5FF,
            0x51578CFF, 0x78780BFF, 0xCF3508FF, 0x1CC700FF, 0x07A58CFF, 0x54832AFF, 0x8A6749FF, 0xF60147FF,
            0xA9581AFF, 0xB63250FF, 0x347EAEFF, 0xD42D4BFF, 0x6D9802FF, 0x6A6F4DFF, 0xCA1EBFFF, 0x03DC4AFF,
            0x11BD5EFF, 0x7C54DBFF, 0x8D52BDFF, 0x379A95FF, 0x44A547FF, 0x7368F7FF, 0x759144FF, 0x4F847AFF,
            0xBF5D52FF, 0x6771ADFF, 0xDE4486FF, 0x9C5980FF, 0xC645BCFF, 0x16D8ACFF, 0x60AA73FF, 0x9578BEFF,
            0x5A9EC0FF, 0x7C8183FF, 0x3AB8C0FF, 0x9A8B90FF, 0x6095DEFF, 0x35C9F7FF, 0xBA8938FF, 0xD77444FF,
            0x6DBF3AFF, 0xA17BDFFF, 0xFE318DFF, 0x7AAF9FFF, 0x5FD287FF, 0xE064A9FF, 0xC27890FF, 0xBF70F5FF,
            0x65C6DCFF, 0xA09ECEFF, 0x4DEABEFF, 0xA2A952FF, 0xFF6071FF, 0x9CA1F6FF, 0x79BAFFFF, 0x8BC5D3FF,
            0xD988DDFF, 0x9BC797FF, 0xB7ACB3FF, 0xBFA7E2FF, 0xFC5ABAFF, 0xA3EF5EFF, 0xE088FDFF, 0xF5905AFF,
            0xD4A294FF, 0x87E297FF, 0xD3D83BFF, 0xBCD386FF, 0xEBA4BFFF, 0xC7CDCCFF, 0x28FF8FFF, 0x9AE0E6FF,
            0x58FC8EFF, 0x4EFFE2FF, 0xFB6EF7FF, 0xFE83B3FF, 0xDFE0A4FF, 0xD0C3FCFF, 0xF4CA8AFF, 0xC7FF92FF,
            0xFFFF60FF, 0x81FEA7FF, 0x7DF2FEFF, 0xEFCAEAFF, 0xFFA7F3FF, 0xA2FDFBFF, 0xE4EFD9FF, 0xAEF4B9FF,
    };
    /**
     * An auto-generated palette made by churning out over a thousand random colors and repeatedly merging the most
     * similar pair of colors, until it reached 255 colors (plus transparent, for 256 total). {@link #QUORUM64} and
     * {@link #QUORUM128} are similar palettes that use 64 and 128 total colors, respectively.
     */
    public static final int[] QUORUM256 = {
            0x00000000, 0x010101FF, 0x171717FF, 0x2D2D2DFF, 0x555555FF, 0x686868FF, 0x7B7B7BFF, 0x8D8D8DFF,
            0x9F9F9FFF, 0xB0B0B0FF, 0xC1C1C1FF, 0xD1D1D1FF, 0xE1E1E1FF, 0xF0F0F0FF, 0xFFFFFFFF, 0x300687FF,
            0x4A014EFF, 0x024A4CFF, 0x292160FF, 0x510916FF, 0x3C2339FF, 0x0D5209FF, 0x3D2654FF, 0x5B2807FF,
            0x223250FF, 0x312972FF, 0x45260DFF, 0x0B5B2EFF, 0x541B41FF, 0x2F3728FF, 0x720877FF, 0x004A6FFF,
            0x6F0033FF, 0x85001CFF, 0x9F0068FF, 0x0D4392FF, 0x35470CFF, 0x6A2230FF, 0x2B6008FF, 0xC7002AFF,
            0x006D6CFF, 0x42218AFF, 0x5A2580FF, 0x0759ADFF, 0xB10052FF, 0xA7081CFF, 0x5D1762FF, 0x3D4462FF,
            0x413E99FF, 0x524A20FF, 0x1A6F4AFF, 0x4D30A7FF, 0x670D00FF, 0x8C1833FF, 0x693922FF, 0x8C2807FF,
            0x24524AFF, 0x1F6273FF, 0x65414AFF, 0x00856DFF, 0x3F5D25FF, 0x742B59FF, 0xA24400FF, 0x106792FF,
            0x85256FFF, 0x51534DFF, 0x793836FF, 0x8D004BFF, 0x3C507EFF, 0x544B75FF, 0x696000FF, 0x7A2E0AFF,
            0x6B3D74FF, 0x875000FF, 0xAF2F0DFF, 0xC50065FF, 0xB51A43FF, 0x3F7930FF, 0x5B538EFF, 0x5B4BCAFF,
            0x755750FF, 0x7B6900FF, 0x9E2F43FF, 0x49693BFF, 0xB71674FF, 0x6A6F22FF, 0x665F3AFF, 0x3F7263FF,
            0x87418BFF, 0x894644FF, 0x398B61FF, 0xBF2B5FFF, 0x0C8C8DFF, 0x6B6281FF, 0x627900FF, 0xB22F90FF,
            0x946018FF, 0x1B78A0FF, 0x50709DFF, 0x875872FF, 0x9F4C75FF, 0x63675DFF, 0x408D95FF, 0x4B75BBFF,
            0xB13A5EFF, 0x9C43B2FF, 0x39A262FF, 0x547B81FF, 0xD12F2CFF, 0x5B804BFF, 0xC54C27FF, 0xC02FB0FF,
            0x1DAA9DFF, 0x4FA246FF, 0xD92390FF, 0x835BAAFF, 0x7E7C29FF, 0xAF4C25FF, 0x7C7572FF, 0xA47A00FF,
            0x839308FF, 0x36AF88FF, 0x846A8EFF, 0x539979FF, 0x698989FF, 0x916C5AFF, 0x76829BFF, 0xBA644FFF,
            0x718F5BFF, 0xFF2175FF, 0x4C98A5FF, 0x7C77C3FF, 0xCA4299FF, 0x99802FFF, 0xB64D73FF, 0x8B904CFF,
            0x7A7DDDFF, 0x37AAC2FF, 0x55B48AFF, 0xB47F0FFF, 0xAA7361FF, 0x55A4C2FF, 0xAA7F30FF, 0x7E8CB7FF,
            0x70B044FF, 0xB85EB7FF, 0x89996EFF, 0xE34B79FF, 0xC9725CFF, 0xCA3F5BFF, 0x83A95AFF, 0x5CA8ADFF,
            0xFF2FBDFF, 0xF83B95FF, 0x3CBEACFF, 0x709D9AFF, 0x6F98CAFF, 0x77A57BFF, 0x9E7598FF, 0x9E7EC9FF,
            0xA2926CFF, 0x8DA835FF, 0x8B8C93FF, 0xAAA24CFF, 0xADA818FF, 0xB88B49FF, 0xD36781FF, 0xC762FFFF,
            0x81BF69FF, 0x6EC18BFF, 0xBD7EA8FF, 0xD274AEFF, 0x9899BCFF, 0xC59C3BFF, 0x7CACE4FF, 0xE9726CFF,
            0x9994D0FF, 0x8AB384FF, 0x7CAEB5FF, 0xCB8B77FF, 0xA8A48AFF, 0x7ED362FF, 0xFC586EFF, 0x6CD4BAFF,
            0xC28FD5FF, 0x5BE2D4FF, 0xC595A8FF, 0xA8AFB6FF, 0xA59EEEFF, 0x8AC0D6FF, 0xB197FFFF, 0x9ABF94FF,
            0x7FD19EFF, 0x98CC75FF, 0xFF4BBDFF, 0xEC699FFF, 0xE18E9AFF, 0xB7B773FF, 0xCFAB64FF, 0x95C1BDFF,
            0xEE85DDFF, 0x87DABFFF, 0xFF67DCFF, 0x9EE469FF, 0x7DDFE2FF, 0x8AF374FF, 0x76EDC9FF, 0x81B9FFFF,
            0xC9B39CFF, 0xC1A1EAFF, 0x9CDB9CFF, 0xFF62ABFF, 0xB5BAECFF, 0xAFBAD7FF, 0xFF3EEEFF, 0xB8C9B3FF,
            0xD696FEFF, 0x6AFDFFFF, 0xE0A57BFF, 0xAAD7C3FF, 0xD6B4C4FF, 0xBEBAFFFF, 0xAAD6FFFF, 0x9ED8EBFF,
            0xD6BAFCFF, 0xDAC194FF, 0xB7E2A0FF, 0xA5EED5FF, 0xDBDC97FF, 0xC8EFAFFF, 0xCAD6C5FF, 0xF18BA9FF,
            0xFF98D5FF, 0xFFE645FF, 0xE3CBD7FF, 0xFFD363FF, 0xEFDAAFFF, 0x87EEFFFF, 0xFF7BB3FF, 0xF3A9C0FF,
            0xCFD3FFFF, 0xE4F79DFF, 0xFFCD97FF, 0xC0FAD2FF, 0x95FDECFF, 0xCDFE96FF, 0xFFB494FF, 0xFFF567FF,
            0xFDD0CBFF, 0xFF94FFFF, 0xE0FFC7FF, 0xB7F5F9FF, 0xE0FCF9FF, 0xF4B8F3FF, 0xE7E5EDFF, 0xFDF5CBFF,
    };
    /**
     * An auto-generated palette like {@link #QUORUM256}, but using different calculations that give it better coverage
     * of orange and brown but worse coverage of green.
     */
    public static final int[] MASH256 = {
            0x00000000, 0x010101FF, 0x171717FF, 0x2D2D2DFF, 0x555555FF, 0x686868FF, 0x7B7B7BFF, 0x8D8D8DFF,
            0x9F9F9FFF, 0xB0B0B0FF, 0xC1C1C1FF, 0xD1D1D1FF, 0xE1E1E1FF, 0xF0F0F0FF, 0xFFFFFFFF, 0x340466FF,
            0x4A054DFF, 0x791D08FF, 0x7E061CFF, 0x05344CFF, 0x1404ACFF, 0x1F2755FF, 0x393501FF, 0x3A068AFF,
            0x003472FF, 0x6A0F45FF, 0x055B12FF, 0x026C09FF, 0x1A4A0DFF, 0x860252FF, 0x1F2374FF, 0x283E37FF,
            0x850087FF, 0x670289FF, 0x432946FF, 0x141FA1FF, 0x3B5903FF, 0x4D3A2BFF, 0x432162FF, 0x540B9AFF,
            0x621868FF, 0x604E00FF, 0x0B4F4AFF, 0x1F7401FF, 0x683710FF, 0x933513FF, 0x076542FF, 0x3E2486FF,
            0x047F11FF, 0x1C3C94FF, 0x2E2EA1FF, 0x2D6922FF, 0x384451FF, 0x784C14FF, 0x822744FF, 0x61384EFF,
            0x008343FF, 0x1B7739FF, 0xB4470FFF, 0x4F542DFF, 0x295275FF, 0x478800FF, 0x663577FF, 0x0D6173FF,
            0x7B2284FF, 0x288A19FF, 0x497715FF, 0x07962DFF, 0xC7225AFF, 0x90327AFF, 0x44437CFF, 0x6B8400FF,
            0x694C65FF, 0xFE174AFF, 0x625E48FF, 0x3F6A51FF, 0x4447B6FF, 0x296192FF, 0x35687CFF, 0xC32484FF,
            0x794F4FFF, 0x4E458FFF, 0x935237FF, 0x05B91CFF, 0x50951AFF, 0x656D2DFF, 0x267F64FF, 0xC94F36FF,
            0x874881FF, 0x902E9DFF, 0x17946AFF, 0xB23B6FFF, 0x7835B6FF, 0xA52FB7FF, 0x6F8826FF, 0x54883FFF,
            0x9E4865FF, 0x6439EBFF, 0x819714FF, 0x8B6068FF, 0x686282FF, 0x85723EFF, 0x4169AAFF, 0x3D9159FF,
            0x00C262FF, 0x2FC11FFF, 0x5E50D3FF, 0x9140C5FF, 0x67706BFF, 0xAA6D3DFF, 0x6452BAFF, 0x8E5298FF,
            0x478278FF, 0x706198FF, 0x1EC248FF, 0x6D9A3AFF, 0x467B9AFF, 0xEB5167FF, 0x41A840FF, 0x9A8838FF,
            0x34988AFF, 0x2095B5FF, 0x9F795FFF, 0xCD32D0FF, 0xD0556EFF, 0x9350B3FF, 0x5A7AB3FF, 0x814CFCFF,
            0xB4667FFF, 0x6C5EF3FF, 0x7B7D8CFF, 0xF87737FF, 0xC449A3FF, 0x4480C8FF, 0x7875ACFF, 0xE53BCCFF,
            0x36B56FFF, 0xA451E3FF, 0x8265DEFF, 0xD97755FF, 0x7870C3FF, 0x8A9352FF, 0xBD8A4FFF, 0x82B33DFF,
            0x51A973FF, 0x947387FF, 0x6D8B6EFF, 0x4F9FA5FF, 0xAC57ABFF, 0x5DCD4AFF, 0x369FC5FF, 0xDF9E2BFF,
            0xA98E76FF, 0x73929DFF, 0x7EAE6BFF, 0x41CF5CFF, 0xBA799EFF, 0x5BBF6BFF, 0x2FBDA4FF, 0x8E9A8DFF,
            0xD2768FFF, 0xAAA551FF, 0xA078B8FF, 0x6CA9A1FF, 0x6F94BAFF, 0x37DB88FF, 0xA868FFFF, 0xBD67DEFF,
            0x9675D4FF, 0x918FACFF, 0x5DA8C8FF, 0xD19C59FF, 0x52C692FF, 0x7390D6FF, 0xD067C7FF, 0xC0A764FF,
            0xE963DAFF, 0xFD50DCFF, 0x99D848FF, 0xCC8E96FF, 0xA9BD6CFF, 0x84C472FF, 0x52E070FF, 0x9099C2FF,
            0xA88DD8FF, 0xE2889AFF, 0xF9A559FF, 0xCA8CCBFF, 0x6BBAD6FF, 0xC1C66EFF, 0xB4D859FF, 0xACA0A4FF,
            0x65CABEFF, 0xB69FCDFF, 0x9AB196FF, 0x77D88FFF, 0x9EE961FF, 0xD5B487FF, 0xA49EE8FF, 0xD875FEFF,
            0xFF73D9FF, 0x61F69AFF, 0xC3B393FF, 0xA1CF85FF, 0x99AED2FF, 0xF890ADFF, 0xF6BE7AFF, 0xBDBBBAFF,
            0x9DA2FFFF, 0xBBA0FEFF, 0xE395D5FF, 0xBC87F6FF, 0xBFA6E0FF, 0xCFAAC1FF, 0x8AB6F4FF, 0x91E7ACFF,
            0xC9FB5DFF, 0xB0D4AEFF, 0x8ECAC3FF, 0xD7E485FF, 0xF2EF57FF, 0xAEE790FF, 0xE195FAFF, 0xF1B1A7FF,
            0xADCDE6FF, 0xD4D0A5FF, 0xFDD195FF, 0xCAC6CFFF, 0xE4BFC7FF, 0x64FCD9FF, 0xEADEAEFF, 0xB2BBFDFF,
            0x84E1FFFF, 0x89FDBEFF, 0xF9CCC1FF, 0xCCCCE8FF, 0xD2EBB8FF, 0xF780FEFF, 0xB5EDD8FF, 0x9FD8FFFF,
            0xFEFF73FF, 0xD6BFFCFF, 0xE2E4E0FF, 0xAEFAA1FF, 0xDEF889FF, 0xFE9FEBFF, 0x9BF9DCFF, 0xFDFEABFF,
            0xFCE7C7FF, 0xAEF3FAFF, 0xD4FEBAFF, 0x82FFFDFF, 0xC7E0FCFF, 0xF5D5F0FF, 0xF7B8F8FF, 0xF4FCEEFF,
    };

    /**
     * Another one of DawnBringer's palettes, winner of PixelJoint's 2017 22-color palette competition.
     * This has transparent at the start so it has 23 items.
     */
    public static final int[] DB_ISO22 = {
            0x00000000, 0x0C0816FF, 0x4C4138FF, 0x70503AFF,
            0xBC5F4EFF, 0xCE9148FF, 0xE4DA6CFF, 0x90C446FF,
            0x698E34FF, 0x4D613CFF, 0x26323CFF, 0x2C4B73FF,
            0x3C7373FF, 0x558DDEFF, 0x74BAEAFF, 0xF0FAFFFF,
            0xCFB690FF, 0xB67C74FF, 0x845A78FF, 0x555461FF,
            0x746658FF, 0x6B7B89FF, 0x939388FF
    };
    
    /**
     * Not very usable on its own because the brightnesses here are artificially close together; meant for use as part
     * of a Bonus Colorizer.
     */
    public static final int[] JUDGE64 = {
            0x00000000, 0x0F0813FF, 0x31383BFF, 0x64666FFF, 0x888F94FF, 0xD2DEE2FF, 0x9EAAB2FF, 0xD5D5D5FF,
            0x58363DFF, 0x896B88FF, 0x956B6BFF, 0x806965FF, 0xB29B81FF, 0xFF9B9BFF, 0xFF3D3DFF, 0x540300FF,
            0xAC4337FF, 0x894F3DFF, 0xCD673CFF, 0xF9A256FF, 0xC77930FF, 0xD0AE97FF, 0xE2BC8FFF, 0xF39908FF,
            0xAE7D4EFF, 0xF2C11AFF, 0xFFEF2EFF, 0xC0FFA6FF, 0x73F60EFF, 0x9BBD2CFF, 0x146D03FF, 0x819E77FF,
            0x4DFD28FF, 0x44B732FF, 0x8BD1BAFF, 0x52ED81FF, 0x23C178FF, 0x2EDFB7FF, 0x8DEED0FF, 0xC7FFFFFF,
            0x008F68FF, 0x7BDAE1FF, 0x196179FF, 0x68D1DBFF, 0x5D74FAFF, 0x4D9BE6FF, 0x2A3271FF, 0x4F69B2FF,
            0x574ECAFF, 0x140BCBFF, 0x5A2784FF, 0x8354C9FF, 0x9B77E6FF, 0x6A0F6EFF, 0xAF4DC1FF, 0x8113BDFF,
            0xFFC7FFFF, 0xAB337CFF, 0xFF5FFAFF, 0x6F243AFF, 0xE94771FF, 0xD0363EFF, 0xC3A5F4FF, 0x5A00BBFF,
    };

    /**
     * An interesting and potentially-useful palette that behaves better as part of a Bonus Colorizer. Made by drawing a
     * straight line from black to white through the grayscale section of an RGB cube, then drawing curved zig-zagging
     * lines from close-to-black to close-to-white that go through red, green, blue, cyan, magenta, and yellow. The
     * zig-zag moves every-other color into a desaturated area. The normal way of getting a Bonus Colorizer will (almost
     * accidentally) add orange/brown where the desaturated yellow or red would be, and tends to have good coverage.
     */
    public static final int[] CUBICLE64 = {
            0x00000000, 0x000000FF, 0x3B3B3BFF, 0x6F6F6FFF, 0x9B9B9BFF, 0xBFBFBFFF, 0xDBDBDBFF, 0xEFEFEFFF,
            0xFBFBFBFF, 0xFFFFFFFF, 0x320000FF, 0x640A0AFF, 0xAA0000FF, 0xBC3E3EFF, 0xFF3C3CFF, 0xFF7272FF,
            0xFF8C8CFF, 0xFFA6A6FF, 0xFFDCDCFF, 0x003838FF, 0x006A6AFF, 0x308484FF, 0x1ADADAFF, 0x70D0D0FF,
            0x6EFFFFFF, 0xB0FFFFFF, 0xC2FFFFFF, 0xDCFFFFFF, 0x003200FF, 0x0A640AFF, 0x00AA00FF, 0x3EBC3EFF,
            0x3CFF3CFF, 0x72FF72FF, 0x8CFF8CFF, 0xA6FFA6FF, 0xDCFFDCFF, 0x380038FF, 0x6A006AFF, 0x843084FF,
            0xDA1ADAFF, 0xD070D0FF, 0xFF6EFFFF, 0xFFB0FFFF, 0xFFC2FFFF, 0xFFDCFFFF, 0x000032FF, 0x0A0A64FF,
            0x0000AAFF, 0x3E3EBCFF, 0x3C3CFFFF, 0x7272FFFF, 0x8C8CFFFF, 0xA6A6FFFF, 0xDCDCFFFF, 0x383800FF,
            0x6A6A00FF, 0x848430FF, 0xDADA1AFF, 0xD0D070FF, 0xFFFF6EFF, 0xFFFFB0FF, 0xFFFFC2FF, 0xFFFFDCFF,
    };
    
    public static final int[] LABRADOR256 = {
            0x000000FF, 0x000020FF, 0x100050FF, 0x2F0E8EFF, 0x2810F0FF, 0x002000FF, 0x002020FF, 0x002040FF,
            0x102070FF, 0x104000FF, 0x104020FF, 0x104040FF, 0x104060FF, 0x104090FF, 0x3440B0FF, 0x2840F0FF,
            0x106010FF, 0x106040FF, 0x106060FF, 0x106080FF, 0x316095FF, 0x2860FFFF, 0x288010FF, 0x288040FF,
            0x288060FF, 0x288080FF, 0x2880A0FF, 0x1C80C8FF, 0x4A80F4FF, 0x44A028FF, 0x44A060FF, 0x44A080FF,
            0x44A0A0FF, 0x44A0C0FF, 0x36A0ECFF, 0x5AC028FF, 0x44C070FF, 0x44C0A0FF, 0x62C0C0FF, 0x62C0E0FF,
            0x44C0FFFF, 0x5AE028FF, 0x44E070FF, 0x44E0A0FF, 0x44E0C0FF, 0x6AE0E0FF, 0x44E0FFFF, 0x62FF4CFF,
            0x6AFFB0FF, 0x6AFFE0FF, 0x6AFFFFFF, 0x200000FF, 0x200020FF, 0x202000FF, 0x202020FF, 0x202040FF,
            0x400000FF, 0x400020FF, 0x400040FF, 0x401060FF, 0x402000FF, 0x402020FF, 0x402040FF, 0x404000FF,
            0x404020FF, 0x404040FF, 0x404060FF, 0x404080FF, 0x406010FF, 0x406040FF, 0x406060FF, 0x600000FF,
            0x600020FF, 0x601040FF, 0x601060FF, 0x601080FF, 0x7010B0FF, 0x7028F0FF, 0x602000FF, 0x602020FF,
            0x604000FF, 0x604020FF, 0x604040FF, 0x604060FF, 0x604080FF, 0x7040B0FF, 0x606010FF, 0x606040FF,
            0x606060FF, 0x606080FF, 0x6060B0FF, 0x7060F0FF, 0x608010FF, 0x608040FF, 0x608060FF, 0x608080FF,
            0x6080B0FF, 0x901000FF, 0x801020FF, 0x801040FF, 0x801060FF, 0x801080FF, 0x804000FF, 0x804020FF,
            0x804040FF, 0x804060FF, 0x804080FF, 0x806010FF, 0x806040FF, 0x806060FF, 0x806080FF, 0x8060A0FF,
            0x8060C0FF, 0x808028FF, 0x808060FF, 0x808080FF, 0x8080A0FF, 0x8080C0FF, 0x8080F0FF, 0x80A028FF,
            0x80A060FF, 0x80A080FF, 0x80A0A0FF, 0x80A0D8FF, 0x8CC078FF, 0x80C0A0FF, 0x90C0F8FF, 0x90E070FF,
            0x90E0A0FF, 0x90E0C0FF, 0x90E0FFFF, 0xA01020FF, 0xA01040FF, 0xA01060FF, 0xA81C80FF, 0xA425B2FF,
            0xA822F0FF, 0xA04010FF, 0xA04040FF, 0xA04060FF, 0xA06010FF, 0xA06040FF, 0xA06060FF, 0xA06080FF,
            0xA060A0FF, 0xA060C0FF, 0xB060F0FF, 0xA08028FF, 0xA08060FF, 0xA08080FF, 0xA080A0FF, 0xA080C0FF,
            0xB080F0FF, 0xA0A028FF, 0xA0A060FF, 0xA0A080FF, 0xA0A0A0FF, 0xA0A0C0FF, 0xA0A0F0FF, 0xA0C028FF,
            0xA0C0A0FF, 0xA0C0C0FF, 0xB0E028FF, 0xB8FF4CFF, 0xC01010FF, 0xC81C40FF, 0xC81C60FF, 0xC04010FF,
            0xD03480FF, 0xC06010FF, 0xC06040FF, 0xC06060FF, 0xC06080FF, 0xC060A0FF, 0xC060C0FF, 0xC08010FF,
            0xC08040FF, 0xC08060FF, 0xC08080FF, 0xC080A0FF, 0xC080C0FF, 0xC0A028FF, 0xC0A060FF, 0xC0A080FF,
            0xC0A0A0FF, 0xC0A0C0FF, 0xC0A0F0FF, 0xC0C028FF, 0xC0C070FF, 0xC0C0A0FF, 0xC0C0C0FF, 0xC0C0E0FF,
            0xC0C0FFFF, 0xC0E070FF, 0xC0E0A0FF, 0xC0E0C0FF, 0xC0E0E0FF, 0xC0E0FFFF, 0xCCFFB8FF, 0xC0FFE0FF,
            0xC0FFFFFF, 0xE82210FF, 0xE028B0FF, 0xEC36F0FF, 0xF03440FF, 0xF03460FF, 0xF06010FF, 0xF06040FF,
            0xF06060FF, 0xF06080FF, 0xF060A0FF, 0xF844B8FF, 0xE08010FF, 0xF88028FF, 0xF08060FF, 0xF08080FF,
            0xF080A0FF, 0xF880D0FF, 0xE880F0FF, 0xE0A028FF, 0xE0A060FF, 0xF0A080FF, 0xF0A0A0FF, 0xF0A0C0FF,
            0xE8A0F0FF, 0xE0C028FF, 0xE0C070FF, 0xE0C0A0FF, 0xE0C0C0FF, 0xE0C0E0FF, 0xE0C0FFFF, 0xE0E04CFF,
            0xE0E0A0FF, 0xE0E0C0FF, 0xF0F0F0FF, 0xE0E0FFFF, 0xF0FF4CFF, 0xE0FFE0FF, 0xE0FFFFFF, 0xFF2880FF,
            0xFFA028FF, 0xFFA060FF, 0xFFA0E0FF, 0xFFC028FF, 0xFFC070FF, 0xFFC0A0FF, 0xFFC0C0FF, 0xFFC0E0FF,
            0xFFC0FFFF, 0xFFE04CFF, 0xFFE0A0FF, 0xFFE0C0FF, 0xFFE0E0FF, 0xFFE0FFFF, 0xFFFFB0FF, 0xFFFFE0FF,
    };
    /**
     * Fairly good at 256 colors; does not include an entry for transparent.
     */
    public static final int[] LAVA256 = {
            0x54C434FF, 0x8D1E4BFF, 0x68808EFF, 0xE8DAC2FF, 0x444BECFF, 0x98A102FF, 0x49F744FF, 0xDD2060FF,
            0x215EC2FF, 0xC1B2D4FF, 0x861514FF, 0xF66B2FFF, 0x1CC25CFF, 0x862D95FF, 0x3598E1FF, 0xBFE433FF,
            0x251955FF, 0xAA5D77FF, 0x6AB2AAFF, 0xC71AD5FF, 0x1E731CFF, 0x86DB30FF, 0x544587FF, 0xD49ABAFF,
            0x3AEAE2FF, 0xB6221FFF, 0x797268FF, 0xF7BD8BFF, 0x32399AFF, 0x8286F3FF, 0xCB4C29FF, 0x22A272FF,
            0xABF993FF, 0x6315AAFF, 0xA61587FF, 0x5F76BAFF, 0xEAD8E2FF, 0x45441DFF, 0xCA963CFF, 0x82DC7EFF,
            0xF134A5FF, 0x8DBA1EFF, 0x452750FF, 0xC57D83FF, 0x1AD4B3FF, 0x9532F2FF, 0x3DA427FF, 0xDEF676FF,
            0x0E1180FF, 0x9B6DC5FF, 0x3DB6E9FF, 0x208D28FF, 0xBFDB74FF, 0x74589CFF, 0xF79FCEFF, 0x4A5570FF,
            0xD5B4A9FF, 0x212CE3FF, 0xA56D13FF, 0xDE3083FF, 0x318BABFF, 0x8CE8F0FF, 0x5A1227FF, 0xF1394EFF,
            0x35BC8EFF, 0xE6C926FF, 0x093F5CFF, 0x869895FF, 0x3CF3CBFF, 0x643874FF, 0xE48DA7FF, 0xA05211FF,
            0x2B0FB4FF, 0xB462E7FF, 0x1B717CFF, 0x95D2C0FF, 0x2DE062FF, 0x655EBEFF, 0xDFB0F2FF, 0x18252BFF,
            0x987B5EFF, 0x2CD78FFF, 0xDB33C8FF, 0x790E2FFF, 0xFC7670FF, 0x723FBEFF, 0x468409FF, 0x1F4C79FF,
            0xA1A5A4FF, 0xE02323FF, 0x125746FF, 0x92AC7AFF, 0xD681E0FF, 0xBF2E4BFF, 0x719585FF, 0xDBE5AAFF,
            0x866127FF, 0xB73383FF, 0x287EDAFF, 0xA8D3F5FF, 0x694902FF, 0xF4A443FF, 0x38323AFF, 0xB8876DFF,
            0x84EAAEFF, 0xF33FE9FF, 0x491A74FF, 0xBF66A9FF, 0x5C6F52FF, 0xDCC485FF, 0xB5B6C5FF, 0x6D17F1FF,
            0xF28717FF, 0x8F3B69FF, 0x29A0A2FF, 0xD3F5DBFF, 0x271008FF, 0xA7663CFF, 0x64B266FF, 0x574946FF,
            0xDF9A6FFF, 0x3FF693FF, 0x7F74B0FF, 0x36D1EDFF, 0x168D5AFF, 0x973336FF, 0x488263FF, 0xBAAD23FF,
            0x166AADFF, 0x86C3DAFF, 0x4F3716FF, 0xC9812DFF, 0x9E50B1FF, 0xF0FA34FF, 0x9A5685FF, 0x28ADC1FF,
            0x1FE3EFFF, 0x4D6B34FF, 0xCDC168FF, 0xAE87D5FF, 0xDC6E10FF, 0x778DB6FF, 0x235C2EFF, 0x9CAE60FF,
            0x7A1069FF, 0xE9618BFF, 0x58C8BEFF, 0x709712FF, 0xD6D826FF, 0xFD3014FF, 0x237746FF, 0xBE93CAFF,
            0x60687BFF, 0x9A850FFF, 0xB0F6ECFF, 0x27B19BFF, 0x9114CAFF, 0x2F70F8FF, 0x283C36FF, 0xB39872FF,
            0xE379ADFF, 0x739449FF, 0xF3E97CFF, 0x6767F1FF, 0x238E83FF, 0xB2EEC0FF, 0xE7B02FFF, 0x342304FF,
            0xA5F645FF, 0x560E4BFF, 0x7D8839FF, 0x9C9EB2FF, 0x3C6E56FF, 0xB9C798FF, 0xF797F2FF, 0x7C4C5EFF,
            0x107AA9FF, 0xA924ACFF, 0x122F66FF, 0x8A7C81FF, 0xEC73CDFF, 0xAF9DEDFF, 0x0D6078FF, 0x8DB6ABFF,
            0x6D99B5FF, 0xEDEFE9FF, 0x160E1DFF, 0x966350FF, 0xF69C8EFF, 0xD760C9FF, 0xA1BBE2FF, 0x612D1FFF,
            0xDB855FFF, 0xA9738BFF, 0x6A563EFF, 0xD52D40FF, 0xC5D3EEFF, 0x25442CFF, 0xA59A5FFF, 0x553138FF,
            0xA7CA81FF, 0x5D648AFF, 0xDDB9BDFF, 0x299FCAFF, 0x431807FF, 0xC36D3AFF, 0x6187D6FF, 0x325714FF,
            0x8B4126FF, 0x3D9C53FF, 0x1B8496FF, 0xF9BCD1FF, 0x113B0EFF, 0x0F5F65FF, 0x84B08FFF, 0xCF7BFFFF,
            0x6F9868FF, 0xBFDAD8FF, 0x7F4C16FF, 0x93A3DDFF, 0xA8C1A7FF, 0x380722FF, 0xB95653FF, 0x443F5FFF,
            0xC49593FF, 0x6B680EFF, 0x94829FFF, 0xDC7361FF, 0xFCE507FF, 0x020B3AFF, 0x82606EFF, 0xEACCFDFF,
            0x999537FF, 0xD9119DFF, 0x3E4A47FF, 0x3B7169FF, 0x292816FF, 0xA97E49FF, 0x85A3C4FF, 0x559474FF,
            0xB56418FF, 0xA4939BFF, 0x0B1C2FFF, 0x856A5AFF, 0x5B5512FF, 0xE7D0C0FF, 0xB7BDFEFF, 0x772F00FF,
            0x2F033DFF, 0xD0DDCFFF, 0x94FCDBFF, 0x8C8F89FF, 0x664031FF, 0xBEACB9FF, 0xC1E5F6FF, 0x8986B1FF,
    };
    
    public static final int[] LAVA64 = {
            0x69B62CFF, 0x8E2D5AFF, 0x42819AFF, 0xEBE2D5FF, 0x5D33E8FF, 0xA29518FF, 0x8EF56AFF, 0xCE4352FF,
            0x4D6AD0FF, 0xB5ADC2FF, 0xA32322FF, 0xEF7322FF, 0x9F1F94FF, 0x45A3D5FF, 0xDADF72FF, 0x220B43FF,
            0xC448B2FF, 0x52C7B0FF, 0x356A4CFF, 0x684C8DFF, 0xE7749FFF, 0x48E8E9FF, 0x797268FF, 0xF7BD8BFF,
            0x362798FF, 0x8286F3FF, 0xEA341DFF, 0x42927DFF, 0xEEC2CEFF, 0x3D3214FF, 0xE3A436FF, 0x5FEAA7FF,
            0x453358FF, 0xDB8984FF, 0x548E22FF, 0x555E7AFF, 0xA86519FF, 0x4C142AFF, 0x133B69FF, 0x959D9AFF,
            0x116475FF, 0xE5BEF8FF, 0x12212DFF, 0xA78B5FFF, 0x9DBA8CFF, 0xCD8FE7FF, 0x74562CFF, 0xBDD8F2FF,
            0x483E40FF, 0xCB8C6AFF, 0x44B9CBFF, 0xBFF0E2FF, 0x351408FF, 0x8C80A8FF, 0x680F5AFF, 0x33433FFF,
            0x7F5666FF, 0x97888EFF, 0x160E1DFF, 0x8E6755FF, 0xA0B0E7FF, 0x73381EFF, 0x1B401DFF, 0x635F10FF,
    };
    
    public static final int[] LARVA256 = {
            0xD1AB8CFF, 0xA35719FF, 0x4216C2FF, 0x46AF32FF, 0x35538FFF, 0xEA1D48FF, 0xA5A7C9FF, 0x8D4E59FF,
            0x8427E5FF, 0x5DBC92FF, 0x0A7723FF, 0xD52394FF, 0xA5A919FF, 0x7F64CEFF, 0x751752FF, 0x55BFC6FF,
            0xF47D70FF, 0xAF08F1FF, 0x854DEDFF, 0x6E1A86FF, 0x14C14FFF, 0xC16494FF, 0xBF3C3DFF, 0x64BBAEFF,
            0x4B633EFF, 0x251DF3FF, 0xD2C288FF, 0xB267ADFF, 0x7C166EFF, 0x55C116FF, 0x206E87FF, 0x082423FF,
            0xDAD0B0FF, 0xBA7825FF, 0xA0E4B1FF, 0x207FE3FF, 0xF3426CFF, 0xAECCEDFF, 0x968389FF, 0x672F16FF,
            0x2ACFAFFF, 0x378D43FF, 0xC627ACFF, 0xBDDC81FF, 0x8789F1FF, 0x3B2A53FF, 0x23E1EFFF, 0xAA5635FF,
            0x6A8116FF, 0x1735B7FF, 0x15E757FF, 0xF58FCCFF, 0x9B3445FF, 0x98E7E5FF, 0x809D81FF, 0xF8EF88FF,
            0xC98B08FF, 0xA245BDFF, 0xAFF445FF, 0x6396A6FF, 0x103A3BFF, 0xE2F5D4FF, 0xB4A161FF, 0xCE3A74FF,
            0xCCED14FF, 0x9EA8ADFF, 0x70543AFF, 0x06EDBFFF, 0x1AAA6FFF, 0xD64BECFF, 0xBE0289FF, 0x88AEF9FF,
            0x5A5A86FF, 0x2C0613FF, 0xD19C80FF, 0xDE5A14FF, 0x115DF3FF, 0x2C0863FF, 0xD1AEDCFF, 0xB96479FF,
            0xA23111FF, 0x47C3DEFF, 0x45757EFF, 0xBCC685FF, 0x8E7211FF, 0x11719FFF, 0x90C2C1FF, 0x78795EFF,
            0x0CC483FF, 0xF47B20FF, 0xAECE3DFF, 0x5374B6FF, 0x342B37FF, 0xC17840FF, 0x6282FAFF, 0x0F268FFF,
            0xFEDC48FF, 0xC2899CFF, 0x933529FF, 0xDAE45CFF, 0xC28CECFF, 0xAA4289FF, 0x4FE802FF, 0x21938FFF,
            0xF21A14FF, 0xAEE099FF, 0x878B41FF, 0x6843C2FF, 0x5C99DAFF, 0x52490EFF, 0xF5A128FF, 0xDD48B8FF,
            0x3C505BFF, 0x49FFE2FF, 0xE0A874FF, 0xE31D2CFF, 0xB4A3B1FF, 0xB15651FF, 0x9C5C9DFF, 0x580D26FF,
            0xFB6440FF, 0xE30CD0FF, 0x9EBC59FF, 0x14BFFFFF, 0xE56B8CFF, 0x89C2A5FF, 0x5A6E32FF, 0xFEC64CFF,
            0x73C9F2FF, 0x16210BFF, 0xD2D3E4FF, 0xA47F71FF, 0x3D7A03FF, 0x8E86BDFF, 0x60324AFF, 0x1BD2C7FF,
            0x038963FF, 0x60349AFF, 0xEE90B0FF, 0x91E7C9FF, 0x639356FF, 0x4D9AA2FF, 0x1F462FFF, 0xF0F2BCFF,
            0x094C7BFF, 0xACA495FF, 0x7E5022FF, 0xD4FE8CFF, 0x68576EFF, 0x38B59BFF, 0x9A2099FF, 0x7755A6FF,
            0xF6B5D4FF, 0xDE5C64FF, 0x81B47DFF, 0x841339FF, 0x276B53FF, 0x9C6EF9FF, 0x9FBEA9FF, 0x867545FF,
            0xFC78ECFF, 0x717C92FF, 0x42271EFF, 0x432A6EFF, 0xFEDAF8FF, 0xE68188FF, 0xDAD300FF, 0x8C385DFF,
            0x013B03FF, 0xA5931DFF, 0x8F9A69FF, 0x4B4C42FF, 0xC2A098FF, 0xACA6E5FF, 0x945D81FF, 0x7C0412FF,
            0xC61300FF, 0x706A36FF, 0xC61550FF, 0x537166FF, 0xF7C980FF, 0x0F243FFF, 0xE1D0CCFF, 0xB37B59FF,
            0x9D82A5FF, 0x6F2E32FF, 0xE693E4FF, 0x8AEAFDFF, 0x5C968AFF, 0x2D4217FF, 0xD3EAE0FF, 0xBBA07DFF,
            0x92FEC5FF, 0x775356FF, 0x33052FFF, 0x1AADBFFF, 0xD65D48FF, 0xC3C5A0FF, 0x7F787AFF, 0xB37EA9FF,
            0x548862FF, 0xFA3E38FF, 0x889D9DFF, 0x59492AFF, 0x2D4467FF, 0xA35969FF, 0x776702FF, 0x49138EFF,
            0x626E4EFF, 0x1E2027FF, 0xEFCCB4FF, 0x673166FF, 0xF8F1D8FF, 0x854F3DFF, 0x70568AFF, 0xFDB4F0FF,
            0xAB6D31FF, 0xBCC8D5FF, 0x8E7461FF, 0xED7E54FF, 0xC4EDF8FF, 0x969985FF, 0x684512FF, 0x524C5EFF,
            0x50FEFEFF, 0xF6A378FF, 0xCAB110FF, 0x5A7182FF, 0x2C1C0FFF, 0xFEC89CFF, 0xBA7B75FF, 0x3E8EAFFF,
            0x354133FF, 0x944B25FF, 0x7E5272FF, 0x53600AFF, 0xCBC5BCFF, 0x877795FF, 0xA5956DFF, 0x8F9CB9FF,
            0x614846FF, 0xEEA6ACFF, 0xD9ADF8FF, 0x696D6AFF, 0xF7CBD0FF, 0xC8775CFF, 0x12889BFF, 0x71928EFF,
            0x433E1AFF, 0xFFF0F4FF, 0x071517FF, 0xD9C1A4FF, 0xC3C8F0FF, 0x95737DFF, 0x9D98A1FF, 0x6F442EFF,
    };

    /**
     * A 64-color palette that includes transparent and uses colors distributed slightly non-uniformly through space and
     * compared for similarity with a rough LAB metric. The slight non-uniformity biases it away from desaturated
     * colors, but not so strongly that they are missing.
     */
    public static final int[] LAWN64 = {
            0x00000000, 0x31E635FF, 0x0F368AFF, 0x1CBDB2FF, 0x341DE9FF, 0xA56D07FF, 0xD9EA22FF, 0xE41DA2FF,
            0x21107CFF, 0x3D81BCFF, 0x901720FF, 0xBCB71EFF, 0xE4F7A9FF, 0xEB6AF3FF, 0x1A0C1CFF, 0x9F9BB0FF,
            0xB9EFE8FF, 0xEA1D21FF, 0x1EBC51FF, 0x3050CAFF, 0x32DCECFF, 0xB81D71FF, 0xDE9581FF, 0x45F7D8FF,
            0x645F1BFF, 0xA7EA8EFF, 0xA220EBFF, 0x12703DFF, 0x251FB2FF, 0x35A6E6FF, 0xC65028FF, 0xEED483FF,
            0x328F31FF, 0x90F521FF, 0xD9BEDDFF, 0xEF16E6FF, 0x33510EFF, 0xB29FFBFF, 0x3C80F3FF, 0x0C6F96FF,
            0x07210CFF, 0x348D8AFF, 0x49435EFF, 0x791781FF, 0xC88536FF, 0xFBFC37FF, 0x99CF37FF, 0x1F391BFF,
            0x8FB079FF, 0xEEB30BFF, 0x56100EFF, 0xB2CFA6FF, 0xEF7215FF, 0xF6B093FF, 0x3FDD8CFF, 0xEF66B2FF,
            0xF3F3E1FF, 0x8F6890FF, 0xF56F71FF, 0xF5A2E6FF, 0x8F9E1CFF, 0xA472DBFF, 0x8CC8FCFF, 0x934F46FF
    };

    /**
     * A 256-color palette that includes transparent and uses colors distributed slightly non-uniformly through space
     * and compared for similarity with a rough LAB metric. The slight non-uniformity biases it away from desaturated
     * colors, but not so strongly that they are missing.
     */
    public static final int[] LAWN256 = {
            0x00000000, 0x25EF13FF, 0x0F368AFF, 0x4DB6C0FF, 0x7305F8FF, 0xB16304FF, 0xE0E711FF, 0xF91459FF,
            0x11098DFF, 0x107FDEFF, 0x7A2A07FF, 0xB7AD51FF, 0xEBFE9FFF, 0xFC4FDDFF, 0x2AD613FF, 0x11061DFF,
            0x520968FF, 0x898EB8FF, 0xAFFAF8FF, 0xE93D09FF, 0x26A90DFF, 0x110C4EFF, 0x3D5BB8FF, 0x33D7F9FF,
            0xA00918FF, 0xC01E46FF, 0xE39C8DFF, 0x2CF6CEFF, 0x183AF3FF, 0x17C239FF, 0x34060DFF, 0x606841FF,
            0x9BE194FF, 0xD015E5FF, 0xF80E0AFF, 0x13731DFF, 0x11F76FFF, 0x47189BFF, 0x6DA4D9FF, 0xD35427FF,
            0xF7D466FF, 0x2206B3FF, 0x2514F0FF, 0x0D9425FF, 0x7BFC1AFF, 0xA6377CFF, 0xD7BDC8FF, 0xF20AF3FF,
            0x1D5D06FF, 0x7617DAFF, 0xBC97F7FF, 0xD7FA0FFF, 0xFA4505FF, 0x2D6FE4FF, 0x87EC08FF, 0xB60B08FF,
            0xDF0F53FF, 0x1F76A3FF, 0x09E6EBFF, 0x07210CFF, 0x93FB57FF, 0xC83FA9FF, 0xEFC3E8FF, 0x208381FF,
            0xA307DDFF, 0xD4B024FF, 0x045286FF, 0x0AD6D9FF, 0x619304FF, 0x0ADC64FF, 0x6D05B5FF, 0xD27E06FF,
            0xF9F911FF, 0x0E246EFF, 0x05A9C3FF, 0x38F7F3FF, 0x6E5005FF, 0xA3D716FF, 0xDD0684FF, 0xEC21D9FF,
            0x048EFCFF, 0x3F2D38FF, 0x7BB48BFF, 0xEA60FFFF, 0xF2DD21FF, 0x3F9E8BFF, 0x79FECEFF, 0xB6460AFF,
            0xE0C806FF, 0x056AA9FF, 0x4C0907FF, 0x81061FFF, 0xBE8366FF, 0xE8F1B5FF, 0x545576FF, 0x90D4C3FF,
            0x152EBCFF, 0x39B2F6FF, 0x67081DFF, 0x946301FF, 0xC7E534FF, 0xF11387FF, 0x1B17D5FF, 0x6B414FFF,
            0xA5C5A2FF, 0xF37D28FF, 0x5575F4FF, 0xABF008FF, 0xE50720FF, 0xFBA371FF, 0x0FFBB1FF, 0xB20688FF,
            0xE87DC4FF, 0xFEF2FCFF, 0x0C330CFF, 0x41043FFF, 0x845795FF, 0xB6D9D5FF, 0x061004FF, 0x04945EFF,
            0x7FBF16FF, 0xD2211CFF, 0xEB6663FF, 0x2CE2B3FF, 0x508513FF, 0x91EF7DFF, 0xC31AC4FF, 0xF6AFF8FF,
            0x0F521DFF, 0x27D389FF, 0x9E8B02FF, 0x09BA9CFF, 0x73D614FF, 0xA70566FF, 0xDF12B0FF, 0xF092E4FF,
            0x1C4010FF, 0x13BF71FF, 0xB863E6FF, 0x1385C7FF, 0x819451FF, 0xB8F9A3FF, 0x1A665EFF, 0xE99A0FFF,
            0x0546B3FF, 0x21BFE3FF, 0x917711FF, 0xCAE77BFF, 0xF6BF04FF, 0x9E3509FF, 0xF922ACFF, 0x4458E8FF,
            0x710E6AFF, 0xAF92AEFF, 0xDEF0EDFF, 0x6AE0F8FF, 0xFF787FFF, 0xB95148FF, 0xEFD396FF, 0x8B33AAFF,
            0xC1B9E9FF, 0xA49D29FF, 0xED4981FF, 0x04C8BAFF, 0xD27DFCFF, 0x04B150FF, 0x0D93A9FF, 0x393303FF,
            0xDB64C9FF, 0xF0DFF6FF, 0x0B1B36FF, 0xAE46F8FF, 0x056E85FF, 0xC56E06FF, 0x0EA1E2FF, 0x4F4909FF,
            0x90CA57FF, 0xC8089FFF, 0x5D3539FF, 0x98BB8CFF, 0x677FA4FF, 0xABEAE6FF, 0xF6AA10FF, 0xD98E74FF,
            0xFBF9CEFF, 0x786084FF, 0x3A44D2FF, 0x8CC8FCFF, 0xEA6702FF, 0xE9AFA8FF, 0x910FB6FF, 0xF897C1FF,
            0xA16B87FF, 0xD3E3D0FF, 0x057459FF, 0x17ED99FF, 0xFA4861FF, 0x788913FF, 0xB2F272FF, 0xB811FAFF,
            0x05384DFF, 0x56230BFF, 0x93A865FF, 0xD5FB9AFF, 0x72E4A9FF, 0xE1D379FF, 0x833489FF, 0xBABAD2FF,
            0x8C9FE0FF, 0xBBFF0AFF, 0xC8006CFF, 0x302900FF, 0x5EB121FF, 0xAD36A0FF, 0xDCBCE3FF, 0xB47906FF,
            0x8D0394FF, 0xC5C40BFF, 0x647E73FF, 0x8BEECDFF, 0xD09003FF, 0x755F0BFF, 0xAEDB55FF, 0x2F426FFF,
            0x69C6BDFF, 0x930645FF, 0xC3859AFF, 0x96D5E9FF, 0xA7A7FFFF, 0xFCB5ADFF, 0x876BFAFF, 0x89A703FF,
            0x5C8F4BFF, 0x97F69EFF, 0xDBFA62FF, 0x7BE273FF, 0x934F46FF, 0xC8D098FF, 0x700598FF, 0xAA81DDFF,
            0xB862C4FF, 0x8943FEFF, 0xFDFF5DFF, 0x9CA9B1FF, 0x6A8CF5FF, 0xF7B782FF, 0xAC1642FF, 0x7B6DA5FF,
            0xF47AF1FF, 0xADB404FF, 0xFB5A9CFF, 0xD1C55EFF, 0xA676BDFF, 0xB68F0CFF, 0x4B6119FF, 0xCBA049FF,
    };

    /**
     * A surprisingly-really-good auto-generated 64-color palette. The generation was done in the CIE L*A*B* color space
     * but using a simpler, seemingly-better distance metric. Like several other palettes here, many colors were
     * produced in a sub-random way and the closest pair of colors (using that simpler distance metric) repeatedly
     * merged until a threshold was reached. Here, that threshold is 63 opaque colors; this also has one fully
     * transparent color. The sub-random way of getting colors generated L* almost uniformly, and A* and B* were
     * produced by a sine wave and a cosine wave at different frequencies, allowing a non-circular span to be reachable
     * by some inputs. If an imaginary color would have been produced, all of the inputs would change slightly and it
     * would regenerate that color.
     */
    public static final int[] TWIRL64 = {
            0x00000000, 0x13071dff, 0x2e1d3fff, 0x304024ff, 0x5f1255ff, 0x491aa3ff, 0x6d3e41ff, 0x535d26ff,
            0x93150cff, 0x4e6d80ff, 0x5e4dbbff, 0x895769ff, 0x733ee4ff, 0xae4726ff, 0x9a3390ff, 0x8d7e3cff,
            0x648c9dff, 0xe46c7aff, 0x8ba5a9ff, 0xfd3dedff, 0xcf9894ff, 0x99a1deff, 0x88c262ff, 0xbaa150ff,
            0xdf8fc3ff, 0xb2bea1ff, 0xe7bdf4ff, 0xf2b57dff, 0x7befb4ff, 0xcdc8c5ff, 0x6eea3cff, 0xc6ecfaff,
            0x9fede3ff, 0xf3e8b4ff, 0xbdef9cff, 0xfaf3f7ff, 0x213366ff, 0x4e3982ff, 0x337c28ff, 0xdf3527ff,
            0xb435c0ff, 0xaf597dff, 0x9064ceff, 0xc77e37ff, 0xcd66c6ff, 0x4db9dcff, 0x98c2eeff, 0x8e0f5cff,
            0x23bf27ff, 0x9474a7ff, 0xd368faff, 0x4cb082ff, 0xfa99e6ff, 0xa8887bff, 0x32dc97ff, 0xeee037ff,
            0x4c7fdeff, 0xc0c934ff, 0x2fa632ff, 0x9c9504ff, 0xf19318ff, 0xaafb1aff, 0xfafc56ff, 0x6fd4cbff,
    };

    /**
     * A surprisingly-really-good auto-generated 256-color palette. The generation was done in the CIE L*A*B* color
     * space but using a simpler, seemingly-better distance metric. Like several other palettes here, many colors were
     * produced in a sub-random way and the closest pair of colors (using that simpler distance metric) repeatedly
     * merged until a threshold was reached. Here, that threshold is 255 opaque colors; this also has one fully
     * transparent color. The sub-random way of getting colors generated L* almost uniformly, and A* and B* were
     * produced by a sine wave and a cosine wave at different frequencies, allowing a non-circular span to be reachable
     * by some inputs. If an imaginary color would have been produced, all of the inputs would change slightly and it
     * would regenerate that color.
     */

    public static final int[] TWIRL256 = {
            0x00000000, 0x0b040fff, 0x200c1cff, 0x151811ff, 0x1f2125ff, 0x3e101dff, 0x19156bff, 0x361f56ff,
            0x3c2526ff, 0x1a3029ff, 0x1c3915ff, 0x5f1255ff, 0x233f45ff, 0x21451eff, 0x692282ff, 0x2526bbff,
            0x484141ff, 0x733153ff, 0x3b4d4bff, 0x93150cff, 0x624866ff, 0x2f5766ff, 0x466125ff, 0x58625bff,
            0x5445c6ff, 0x8d4f5eff, 0x8021e6ff, 0xb1330cff, 0x8e4494ff, 0x5c697bff, 0xc0363eff, 0x56745eff,
            0xae5340ff, 0x827122ff, 0xc54a03ff, 0x707f86ff, 0x348b9aff, 0x947c52ff, 0x8d8180ff, 0xd76553ff,
            0x86917cff, 0xf4588cff, 0x8996a4ff, 0xe86c59ff, 0xfd3dedff, 0xaf979aff, 0x7da77bff, 0x4da1e4ff,
            0x8fac36ff, 0x95b0b3ff, 0xc9a43eff, 0xcd9bbeff, 0x8aba6bff, 0xc2af97ff, 0xa9bbc4ff, 0xd5a9edff,
            0x96cd70ff, 0xe0c479ff, 0xb9c6bcff, 0xeec543ff, 0x92dcb2ff, 0xd1ceaeff, 0x54ee38ff, 0xc8e0f7ff,
            0x62f8c2ff, 0x9ce9f3ff, 0xf9d7beff, 0xf9e399ff, 0xf5e0deff, 0xaafbb0ff, 0xe2efe9ff, 0xfaf3f7ff,
            0x261348ff, 0x55081bff, 0x551e1aff, 0x583220ff, 0x1a395dff, 0x543772ff, 0x6c4123ff, 0x722da1ff,
            0x5b5545ff, 0x7a2cceff, 0x2c7341ff, 0x845e73ff, 0x6f6d8aff, 0xdd3040ff, 0xbf27c0ff, 0x678042ff,
            0xdc4718ff, 0xb3617fff, 0x6c89afff, 0x4a9873ff, 0x9579d9ff, 0xce7551ff, 0xdb65caff, 0xde8035ff,
            0xb09c4dff, 0xa49f76ff, 0xce9080ff, 0x66c0d6ff, 0xd3a6a2ff, 0xa5bb8cff, 0x85c7edff, 0xc8b5c4ff,
            0xb3cc8bff, 0x99d8d8ff, 0xd4c3d2ff, 0x8ee358ff, 0xecd0a6ff, 0xd1dfa8ff, 0xceec9dff, 0xc4f2ddff,
            0xe4eec5ff, 0x8e0f5cff, 0x775140ff, 0xa32a75ff, 0x3d66a1ff, 0x487a9eff, 0x8d746dff, 0x6f9b76ff,
            0xbb8da3ff, 0x8aaa96ff, 0x989df5ff, 0x80b942ff, 0x1bc83bff, 0xb4abbeff, 0x76cb6fff, 0xabbdefff,
            0xfac5aeff, 0x99ef2fff, 0xc3f8fdff, 0x4b4216ff, 0x5f3244ff, 0x892f45ff, 0x893f13ff, 0x6b5f18ff,
            0x4656b9ff, 0x982fd2ff, 0x5b50f2ff, 0x745aacff, 0x736f61ff, 0x39840eff, 0x816ae0ff, 0xb5789eff,
            0xd652f3ff, 0xe66d9bff, 0x41ab78ff, 0x82a4b6ff, 0x74a3eaff, 0xee86bbff, 0xe5978bff, 0x9daae9ff,
            0xfda1c6ff, 0xa4ccb0ff, 0x83e719ff, 0x7de5acff, 0xe6d5eeff, 0xbcfcc9ff, 0x062815ff, 0x4000a3ff,
            0x3e2096ff, 0x76029bff, 0x315848ff, 0x55521fff, 0x6e49aeff, 0xe52a04ff, 0xc26f37ff, 0xd063adff,
            0xa8887bff, 0xbd69f7ff, 0x659d9dff, 0x9f8fcbff, 0x57b58cff, 0x3ed08fff, 0x6fdf59ff, 0xb9dfbaff,
            0xfbe037ff, 0xa23843ff, 0xa22b9aff, 0xa843bfff, 0x9657b9ff, 0x887f29ff, 0x6e8e4bff, 0x4487daff,
            0x4497beff, 0x33b1e2ff, 0xdcab6bff, 0xf59be2ff, 0xb9d160ff, 0x26e89eff, 0xbbdb9cff, 0xf4cdf8ff,
            0x162647ff, 0x1a5d22ff, 0x9a6316ff, 0x876a92ff, 0x7a70b2ff, 0x788362ff, 0xc156c4ff, 0xba52f4ff,
            0xd0688aff, 0x01a62eff, 0x9c9504ff, 0x5faa21ff, 0x829acdff, 0x95a6cdff, 0xf19318ff, 0xf7b397ff,
            0xfdbf63ff, 0xfcbbfaff, 0xaafa89ff, 0xf7f6d2ff, 0x18013fff, 0x462fc2ff, 0xaa507aff, 0x518775ff,
            0xb06e4eff, 0xb57f2dff, 0x58a048ff, 0xdc77feff, 0xe8a53cff, 0xfa93f7ff, 0xf5ab69ff, 0xa1cc30ff,
            0xddbef2ff, 0xe0df36ff, 0x9ee99aff, 0xaafb1aff, 0xfcfd39ff, 0xf5ffb7ff, 0x412b4bff, 0x143377ff,
            0x246d7bff, 0xb55609ff, 0x5376e2ff, 0x967cb9ff, 0xc578d9ff, 0x2bb613ff, 0xb19cb6ff, 0xd393d8ff,
            0xcdc12bff, 0x84fbebff, 0xf8fb73ff, 0x77322dff, 0x483b91ff, 0x883576ff, 0xef417cff, 0x8f74f1ff,
            0x948c50ff, 0xcc8830ff, 0xf17e77ff, 0x6fd4cbff, 0xb8d304ff, 0xc4d7cfff, 0xc9f46bff, 0x975439ff,
    };
    
    public static final int[] TINCTURE64 = new int[] {
            0x00000000, 0x180d13ff, 0x3c1c24ff, 0x453935ff, 0x36584eff, 0x68705fff, 0x688795ff, 0xbfbc85ff,
            0xc9d7dfff, 0xdcfdecff, 0x34875dff, 0x44bfd7ff, 0x22fee7ff, 0xa087f6ff, 0x0000ffff, 0x3d23c3ff,
            0x250863ff, 0x8e168eff, 0xb833d4ff, 0xf529f8ff, 0xef73e1ff, 0xe3b5caff, 0xf68499ff, 0xed1f0bff,
            0xb75028ff, 0x7c230dff, 0xe26f41ff, 0xff7f00ff, 0xf7c78aff, 0xffff00ff, 0xb4ba29ff, 0x858138ff,
            0x0a8b03ff, 0x1abb3dff, 0x00ff00ff, 0xbdeb68ff, 0xaa6567ff, 0xbe97b6ff, 0xeba281ff, 0x43be84ff,
            0x65dfdbff, 0x1e4317ff, 0x1b692fff, 0x68a1a2ff, 0xa1b4e1ff, 0x5f81cfff, 0x50639eff, 0x4e2b72ff,
            0x6053dbff, 0xa362ebff, 0xe94889ff, 0xb51e2aff, 0xefb02aff, 0x626200ff, 0xa7960cff, 0xf7dd4cff,
            0x53da48ff, 0x74a729ff, 0x43f69eff, 0x2da0efff, 0x7f00ffff, 0xd7a5ffff, 0xcc197bff, 0x91264bff,
    };
    public static final int[] TINCTURE256 = new int[] {
            0x00000000, 0x0e090aff, 0x121e14ff, 0x232e2aff, 0x403e25ff, 0x484a47ff, 0x6b5945ff, 0x676966ff,
            0x7a7f90ff, 0x87918dff, 0xad9f91ff, 0xb6b3a2ff, 0xc2cabcff, 0xe5d4c7ff, 0xf7f7eeff, 0x007f7fff,
            0x24c2ceff, 0x00ffffff, 0xbfffffff, 0x8a81fbff, 0x0000ffff, 0x3f3fbfff, 0x00007fff, 0x1d0c52ff,
            0x80098eff, 0xbf3fbfff, 0xf500f5ff, 0xfd81ffff, 0xffc0cbff, 0xff8181ff, 0xff1e05ff, 0xb24432ff,
            0x7c0e02ff, 0x541728ff, 0x7f3f00ff, 0xbe8452ff, 0xff7f00ff, 0xf7c090ff, 0xf8fdc4ff, 0xffff00ff,
            0xc7bd3cff, 0x81790aff, 0x007f00ff, 0x2ec12aff, 0x00ff00ff, 0xc1f695ff, 0xc8a37cff, 0x797566ff,
            0xa0695fff, 0xb5726bff, 0xd18b8eff, 0xea9879ff, 0xecab89ff, 0xeae4d3ff, 0x503846ff, 0x6c484dff,
            0x905252ff, 0xd9b3b8ff, 0xe1d9f5ff, 0x71722cff, 0x84925bff, 0x95a666ff, 0xb5b572ff, 0xd0c68aff,
            0xcee0b2ff, 0xabc78fff, 0x84c15bff, 0x697f3cff, 0x445432ff, 0x144d25ff, 0x546945ff, 0x578b5cff,
            0x6dad6fff, 0x64bc74ff, 0x89c287ff, 0x97dfa8ff, 0xacf2cfff, 0x8bb491ff, 0x507d5fff, 0x0f6946ff,
            0x293f4dff, 0x46706fff, 0x64ababff, 0x9dc7c5ff, 0xa1e8f0ff, 0xd0eef8ff, 0xb4d0f5ff, 0xa1b4e1ff,
            0x8ba7afff, 0x588fd5ff, 0x507193ff, 0x465764ff, 0x22111bff, 0x152648ff, 0x444574ff, 0x655682ff,
            0x636facff, 0x7a77c0ff, 0x8a8dd0ff, 0xbf83a9ff, 0x8450c2ff, 0x785da1ff, 0x613e76ff, 0x3b293bff,
            0xaf5db4ff, 0x9c7dc0ff, 0xf1abd0ff, 0xd2bfebff, 0xcea3bfff, 0xba9acfff, 0xd45896ff, 0x2e1b27ff,
            0x44220dff, 0x6b1717ff, 0xab1b10ff, 0xda2010ff, 0xd7533aff, 0xef6132ff, 0xf26665ff, 0xf6bd31ff,
            0xfba227ff, 0xdb9b28ff, 0xd87114ff, 0xb45a00ff, 0xa14b17ff, 0x592e1bff, 0x53500aff, 0x626200ff,
            0x838568ff, 0xa7960cff, 0xbcb013ff, 0xefda6bff, 0xffd510ff, 0xffea4aff, 0xc1fd49ff, 0x9ff135ff,
            0x89db26ff, 0x73c805ff, 0x62a628ff, 0x3d6d1eff, 0x283909ff, 0x116510ff, 0x149605ff, 0x0fde0fff,
            0x8bfd79ff, 0x47ea54ff, 0x05b450ff, 0x1c8c4eff, 0x389871ff, 0x06c491ff, 0x17da69ff, 0x3fee9bff,
            0x46fea0ff, 0x44fdcfff, 0x55e6ffff, 0x8fd9e7ff, 0x3bdcd1ff, 0x109cdeff, 0x175e61ff, 0x0f377dff,
            0x004a9cff, 0x406495ff, 0x2e50e6ff, 0x186abdff, 0x2378dcff, 0x48a6caff, 0x4aa4ffff, 0x63bce0ff,
            0x007fffff, 0x3f82c1ff, 0x786ef0ff, 0x4a5affff, 0x4f3ff6ff, 0x3114b8ff, 0x2c0c8aff, 0x6219daff,
            0x8b29d6ff, 0xb23afcff, 0x7f00ffff, 0xb35cfaff, 0xb991ffff, 0xd7a5ffff, 0xf2c5feff, 0xe673ffff,
            0xf452faff, 0xda20e0ff, 0xbd10c5ff, 0x8216bbff, 0x5f1670ff, 0x410062ff, 0x9f1774ff, 0xc80078ff,
            0xff50bfff, 0xff6ac5ff, 0xfd387bff, 0xdf1e6cff, 0xbe2043ff, 0x98344dff, 0x911437ff, 0x370a2cff,
            0x49305eff, 0x88260cff, 0x6e37c7ff, 0xa5536bff, 0x498e2bff, 0xb86140ff, 0xc16576ff, 0x99838aff,
            0xb187e1ff, 0xa794abff, 0xd1b26dff, 0xf6ce83ff, 0x4b164eff, 0x654c1fff, 0x8f3925ff, 0xa0853eff,
            0x599595ff, 0x88a129ff, 0xda75d7ff, 0x96cf5aff, 0x9def6aff, 0x2d2008ff, 0x741457ff, 0x972ea7ff,
            0x886355ff, 0xed87b1ff, 0xc4de59ff, 0x0f3766ff, 0x531f94ff, 0xbd3e8eff, 0x9c4edaff, 0x867092ff,
            0xdd805dff, 0x80df80ff, 0x1d296aff, 0x742e01ff, 0x0437d4ff, 0x873a6eff, 0x348cacff, 0x81af29ff,
            0xc7491dff, 0xf76796ff, 0xa5bc29ff, 0x057860ff, 0xc83e68ff, 0x577e8bff, 0xbc691dff, 0xe8ad14ff,
            0x3f1313ff, 0x5e378eff, 0x23d6f0ff, 0x652a57ff, 0xad63dcff, 0x639f86ff, 0xd3cf6dff, 0xdb2e95ff,
    };

    /**
     * Made by taking the colors from {@link #TWIRL256} and running lots of simulated annealing on them; this is a
     * rather good palette.
     */
    public static final int[] KNEE256 = new int[]{
            0x00000000, 0xc2b069ff, 0x34626bff, 0xb8be05ff, 0x7acb91ff, 0x05194fff, 0x176bd8ff, 0x30343fff,
            0x6f4f0eff, 0x34dbafff, 0xd4d0fcff, 0x411d38ff, 0xd50f41ff, 0x675b75ff, 0xa75ef3ff, 0x6c46d5ff,
            0x549148ff, 0xe070deff, 0xd0f9b8ff, 0x760796ff, 0xb1c395ff, 0xfe89f5ff, 0x0b0105ff, 0x69327bff,
            0x5e6afdff, 0xfae184ff, 0x104d63ff, 0x89ab39ff, 0x074142ff, 0xe62dfdff, 0x7e9807ff, 0xe1e3faff,
            0x2391e0ff, 0x50037fff, 0x0864a9ff, 0xfbaa9eff, 0xb2ac0bff, 0x2d4307ff, 0x2012d4ff, 0x8bfcadff,
            0x164bcbff, 0xfcbad0ff, 0x233e9aff, 0x6b02fcff, 0xbc2e9aff, 0x414073ff, 0x54cc00ff, 0xa978fdff,
            0xcb8bc5ff, 0x3e2100ff, 0x9e496fff, 0x9176e4ff, 0x9ec911ff, 0xe05b06ff, 0xdf5955ff, 0x787833ff,
            0xc3f76fff, 0x63af07ff, 0x4ce1daff, 0x075d02ff, 0x826b8bff, 0x2c0502ff, 0x170394ff, 0x065431ff,
            0xbcccdcff, 0xebfc25ff, 0x1b02f9ff, 0x02de13ff, 0xda4bc5ff, 0xb84557ff, 0x112d83ff, 0xe4b746ff,
            0x0b112bff, 0x31e8abff, 0xb04d8aff, 0xc56410ff, 0xd2498fff, 0x997f03ff, 0xff6b27ff, 0x7cdd04ff,
            0x520454ff, 0x50776bff, 0xf02180ff, 0x112a36ff, 0x9ab8ccff, 0xa00799ff, 0xccdcd5ff, 0x4bbf82ff,
            0x029b07ff, 0x0fc5ffff, 0x4f5deeff, 0x023a14ff, 0x2b1a1cff, 0xa9a25cff, 0x989f8dff, 0x64798dff,
            0x925423ff, 0xb8fd02ff, 0xfd74fcff, 0x4e4023ff, 0x57210eff, 0xf27667ff, 0xd69c0bff, 0xb8060fff,
            0xf968a6ff, 0x0e8e40ff, 0xaaa1c0ff, 0xc79094ff, 0x9a3f8eff, 0x92d4b9ff, 0xfd0ec4ff, 0xfefdc8ff,
            0xa37a5dff, 0x2ca04fff, 0xfc184aff, 0x856904ff, 0xc97a5cff, 0xf7a0ffff, 0x522c4dff, 0x510208ff,
            0x0f588fff, 0x83ff30ff, 0xf48b87ff, 0xfcf3faff, 0x9c81b9ff, 0x7fdaf0ff, 0x1031bdff, 0x7a4187ff,
            0x45cfd0ff, 0x5b871dff, 0x775ca9ff, 0xf89906ff, 0xbd30feff, 0x3188adff, 0x70b2fbff, 0xc94c33ff,
            0x924fb9ff, 0x775f02ff, 0x7a56feff, 0x83290eff, 0x06aaf8ff, 0x0e54afff, 0x029086ff, 0x866854ff,
            0x71224eff, 0xf4c95eff, 0xa0073aff, 0x8d8985ff, 0xb22151ff, 0x812497ff, 0xb08b52ff, 0xeecaa0ff,
            0xac9e0aff, 0x10ab9fff, 0xc86fb5ff, 0x8c4204ff, 0x8198e6ff, 0xa16f3dff, 0x7d8e10ff, 0xb8758aff,
            0x465b0dff, 0x72987cff, 0x3174b8ff, 0xfbf88fff, 0x023d74ff, 0x3b3207ff, 0x197b07ff, 0xf9350aff,
            0x001907ff, 0xae6396ff, 0x3a0424ff, 0x2b8479ff, 0x53703aff, 0xd0ac93ff, 0x0c9baaff, 0x1bffa7ff,
            0x91ebccff, 0xc6bfedff, 0x728961ff, 0xdb7f93ff, 0xd5e578ff, 0x0b623bff, 0x300254ff, 0xa3332cff,
            0xdf8a45ff, 0xa89bffff, 0xe95393ff, 0x6b0509ff, 0xd80d73ff, 0x2a83f5ff, 0x051b79ff, 0x70d660ff,
            0x592674ff, 0x6c4d49ff, 0x47554eff, 0x7105cdff, 0x78bb02ff, 0x16ec08ff, 0xf9564eff, 0x2f6b0aff,
            0xa20d06ff, 0xa3ae96ff, 0x2fbe01ff, 0x300facff, 0xea9859ff, 0xe8a0c5ff, 0xd7d908ff, 0x2bca85ff,
            0x9bbd56ff, 0xdac405ff, 0x833149ff, 0xa5ec04ff, 0xb150b8ff, 0x1fbbbeff, 0xf0bc76ff, 0xff8113ff,
            0xe958faff, 0xbf7b22ff, 0xfcdc2eff, 0xd920abff, 0xfe4fc0ff, 0x1d063fff, 0x636641ff, 0xa38812ff,
            0x7a76c0ff, 0xa3e781ff, 0xb24b0eff, 0x04e273ff, 0x5f3010ff, 0xa55a50ff, 0x7eac7bff, 0xc7d062ff,
            0xe00308ff, 0x94fdefff, 0xa134d1ff, 0x6d8afdff, 0x0df56fff, 0x603a3aff, 0x64aab2ff, 0xf585c7ff,
            0xbd6558ff, 0x88032dff, 0xf8e3c6ff, 0x9febfeff, 0x8b0766ff, 0x223af2ff, 0x0efcefff, 0x769bbbff,
            0x6f003eff, 0x00b355ff, 0x6e82bbff, 0x002d5eff, 0x017361ff, 0xa50e6dff, 0x1c2a07ff, 0xad05ffff,
    };

    /**
     * Generated by simulated annealing run on mostly-uniform sampling of CIE LAB color space, filling out the remaining
     * initial colors with ones from {@link #LAVA256}.
     */
    public static final int[] SMASH256 = {
            0x00000000, 0xe3b72eff, 0x054d41ff, 0x3f136bff, 0x80e856ff, 0x391e19ff, 0x3b5cfbff, 0x233bd4ff,
            0x627846ff, 0x810208ff, 0x2c3ba1ff, 0x1e3d45ff, 0x9de5caff, 0x71a928ff, 0x980f99ff, 0x60879bff,
            0xe30c1bff, 0xcad985ff, 0x08c881ff, 0x9242efff, 0xfdb56dff, 0x3979d4ff, 0x107a05ff, 0xc56045ff,
            0xfd0c0cff, 0x7894f2ff, 0x72154eff, 0xbd8d96ff, 0xf7144dff, 0xd3dbceff, 0xf97d52ff, 0x1a140eff,
            0x0c2cc5ff, 0xf750cdff, 0x082e9dff, 0xcdbeffff, 0xaf7a67ff, 0x9bd5e1ff, 0x2ba050ff, 0xd39270ff,
            0xb833f7ff, 0x86aaf8ff, 0x689e1aff, 0xf7e95fff, 0x75cd50ff, 0x8de89bff, 0x745909ff, 0xfc5f68ff,
            0x79a7a7ff, 0xff907fff, 0x499ec9ff, 0x8c03d4ff, 0xe8cc1fff, 0xeba55fff, 0x96540cff, 0x093c06ff,
            0x09b583ff, 0x935af8ff, 0xf870c9ff, 0xbbcf7dff, 0x9e6644ff, 0x814b33ff, 0x00406bff, 0xa9fbc5ff,
            0x188257ff, 0xfd4a08ff, 0x5981f9ff, 0xd4bac2ff, 0x2a05beff, 0x48da89ff, 0x85bf60ff, 0x0eb0f7ff,
            0x644d59ff, 0x94fe8aff, 0xfc89cfff, 0xf16b14ff, 0xa0a10cff, 0xbc72a8ff, 0x9b17ffff, 0x9f50bcff,
            0x665e50ff, 0xe5eda8ff, 0x386330ff, 0x8a3fc4ff, 0x5a959fff, 0x8bbaecff, 0xaaae89ff, 0xb3c03fff,
            0x06c6baff, 0x61884fff, 0x61410dff, 0xfcbbf9ff, 0xfcd7ffff, 0xeb75a4ff, 0xc54749ff, 0x01a601ff,
            0x5a6f05ff, 0xd25ab6ff, 0x420cffff, 0x10cf0eff, 0x048d93ff, 0xc542c3ff, 0x170c4aff, 0xbfea12ff,
            0x066c03ff, 0xc2a9ddff, 0x78779cff, 0xfff923ff, 0x0955e1ff, 0x1c3884ff, 0xfb3e86ff, 0x891878ff,
            0x9b7afdff, 0x681c85ff, 0xa34a4aff, 0xab8950ff, 0xb50cc6ff, 0xe8ce7eff, 0xcf6971ff, 0x06d3bfff,
            0x21fddaff, 0xd5ac9bff, 0x1e2b76ff, 0x00ddf8ff, 0x5b0576ff, 0x31290dff, 0x883e6eff, 0x2f2a3eff,
            0x404213ff, 0x1204e4ff, 0xf3c1b3ff, 0x0f2b00ff, 0x34df4dff, 0x672501ff, 0xb4d002ff, 0xbb5e9aff,
            0x4d2d45ff, 0xc1982dff, 0x07ed15ff, 0x6b3020ff, 0x839c6bff, 0x0af7abff, 0xf93bffff, 0xaa104fff,
            0x7614aeff, 0x8aca81ff, 0xd1ba7bff, 0x960542ff, 0xab4380ff, 0x17b103ff, 0xcc387dff, 0xb5740bff,
            0x082126ff, 0xf2ece7ff, 0x0c0635ff, 0x1754b0ff, 0xfbaba9ff, 0x03a6afff, 0x5a0705ff, 0x643654ff,
            0x9c0612ff, 0xc7d1faff, 0x4efd0eff, 0x49320bff, 0xa2607dff, 0xacb101ff, 0x2a606bff, 0x025806ff,
            0x000612ff, 0xffd800ff, 0xe226f0ff, 0x7ab1bcff, 0xc48202ff, 0x245674ff, 0xb29fc1ff, 0x58174cff,
            0x878260ff, 0x4e6eedff, 0x23e3c7ff, 0xff66fdff, 0xc21b43ff, 0x3b8bdeff, 0xed0778ff, 0x07eff6ff,
            0xbe0f02ff, 0xef34b5ff, 0xf9a5ddff, 0x2dfa78ff, 0x10915eff, 0x548204ff, 0x3c5889ff, 0x4f9c76ff,
            0xc05ef8ff, 0xd57949ff, 0xd88830ff, 0x244b07ff, 0xd96401ff, 0x4a4140ff, 0x054b95ff, 0xa06cd3ff,
            0xa4db68ff, 0xda8bb8ff, 0x527b78ff, 0x516d7eff, 0x8bad68ff, 0x654786ff, 0x8664aeff, 0x6f4faeff,
            0xb9fafaff, 0x12077bff, 0x86de18ff, 0xd1fe8eff, 0xcafa14ff, 0xfd8f25ff, 0x917610ff, 0x77517eff,
            0x00cbeeff, 0x2561cdff, 0x858700ff, 0x726941ff, 0x2073b5ff, 0x9d332aff, 0x0eb2b6ff, 0x92f935ff,
            0x806176ff, 0xb14f1dff, 0xffffd1ff, 0x092359ff, 0x9e9623ff, 0xd84d27ff, 0x0a7740ff, 0x093dffff,
            0x097472ff, 0x823b05ff, 0x72bd1dff, 0xe64c63ff, 0x91d1b0ff, 0x209415ff, 0xcfcfaaff, 0x8e7fb9ff,
            0x0c069fff, 0xfbdd7dff, 0x908c97ff, 0x34081aff, 0x4e5431ff, 0x89f7f6ff, 0x4e0536ff, 0x131a38ff,
            0xc69ff5ff, 0xfe84feff, 0x14a0ffff, 0xcc7bf6ff, 0xa2c1bbff, 0xa8bd77ff, 0xbd0b81ff, 0xd3019dff,
    };

    /**
     * DawnBringer's Aurora palette run through some Lloyd relaxation to increase the difference between colors.
     */
    public static final int[] AURORA_LLOYD = new int[]{
            0x00000000, 0x000008ff, 0x080808ff, 0x102118ff, 0x183139ff, 0x29424aff, 0x525252ff, 0x63636bff,
            0x737384ff, 0x8c848cff, 0x949c9cff, 0xa5b5a5ff, 0xb5c6bdff, 0xced6c6ff, 0xefe7e7ff, 0xeff7e7ff,
            0x187b7bff, 0x29b5b5ff, 0x39f7e7ff, 0xadf7e7ff, 0x847be7ff, 0x2108efff, 0x3131b5ff, 0x080073ff,
            0x080042ff, 0x6b1084ff, 0xbd29adff, 0xe718efff, 0xef7be7ff, 0xefadbdff, 0xef737bff, 0xef1029ff,
            0xb53931ff, 0x730810ff, 0x4a0810ff, 0x7b3110ff, 0xad7329ff, 0xef7321ff, 0xf7bd63ff, 0xefef94ff,
            0xe7f729ff, 0xb5bd29ff, 0x7b7b10ff, 0x187b10ff, 0x31b539ff, 0x31f721ff, 0xa5f78cff, 0xb5a5adff,
            0xc6a56bff, 0x949473ff, 0x52948cff, 0x318494ff, 0x7b634aff, 0x9c5a52ff, 0xbd6b63ff, 0xce7b5aff,
            0xde8c63ff, 0xe79c6bff, 0xf7a58cff, 0xefc69cff, 0xefdeb5ff, 0x523131ff, 0x6b3939ff, 0x844a52ff,
            0x9c6b73ff, 0xb5848cff, 0xd69c9cff, 0xefceceff, 0xd6b594ff, 0xb58c52ff, 0x946321ff, 0x735218ff,
            0x312110ff, 0x423110ff, 0x6b6b21ff, 0x8c8c39ff, 0x8c9c31ff, 0xadad63ff, 0xc6c67bff, 0xded68cff,
            0xd6efb5ff, 0xbdde8cff, 0xa5c673ff, 0x84b531ff, 0x6b8c39ff, 0x4a7318ff, 0x424221ff, 0x101808ff,
            0x104a18ff, 0x215229ff, 0x426342ff, 0x187342ff, 0x4a8c31ff, 0x39a56bff, 0x5abd73ff, 0x6bc68cff,
            0x8cd68cff, 0xd6f7e7ff, 0x9cefc6ff, 0x8cd6bdff, 0x7bad84ff, 0x427b5aff, 0x106331ff, 0x102918ff,
            0x104242ff, 0x216b6bff, 0x29a5a5ff, 0x63bdc6ff, 0x94d6deff, 0xb5e7e7ff, 0xa5ceefff, 0x8cc6e7ff,
            0x8cb5deff, 0x73a5c6ff, 0x3184bdff, 0x297394ff, 0x21526bff, 0x100821ff, 0x101031ff, 0x18315aff,
            0x294273ff, 0x31528cff, 0x6b639cff, 0x6b73c6ff, 0x7b84bdff, 0xa59cd6ff, 0xc6d6efff, 0xd6d6efff,
            0x9c84ceff, 0x7b4abdff, 0x634a94ff, 0x4a2973ff, 0x291831ff, 0x4a294aff, 0x73316bff, 0x944a84ff,
            0xa54aa5ff, 0xa56badff, 0xef9cdeff, 0xefceefff, 0xd6bdd6ff, 0xceadc6ff, 0xce94bdff, 0xc684adff,
            0xce639cff, 0xc64a84ff, 0x4a2129ff, 0x290810ff, 0x100018ff, 0x390808ff, 0x5a0810ff, 0x9c1010ff,
            0xce1818ff, 0xce4a31ff, 0xf72921ff, 0xef5221ff, 0xef5263ff, 0xe7ad21ff, 0xef9429ff, 0xce9418ff,
            0xce6b18ff, 0xad5218ff, 0x944210ff, 0x632910ff, 0x524a10ff, 0x525a10ff, 0x847b4aff, 0xa58c18ff,
            0xa5a518ff, 0xced631ff, 0xefce29ff, 0xefe742ff, 0xbdef31ff, 0x8cef29ff, 0x8cd629ff, 0x63c621ff,
            0x63a521ff, 0x186b10ff, 0x103108ff, 0x104210ff, 0x105210ff, 0x189418ff, 0x21ce18ff, 0x29de18ff,
            0x73f763ff, 0x39e74aff, 0x18b510ff, 0x21a531ff, 0x188442ff, 0x103918ff, 0x219473ff, 0x18bd84ff,
            0x29ce63ff, 0x31d694ff, 0x31f794ff, 0x42efbdff, 0x6befefff, 0x31dee7ff, 0x42ceefff, 0x31d6bdff,
            0x2194d6ff, 0x185252ff, 0x102152ff, 0x182973ff, 0x214294ff, 0x21638cff, 0x294ad6ff, 0x215ab5ff,
            0x2963ceff, 0x399cbdff, 0x3994efff, 0x739cefff, 0x39bdefff, 0xadadefff, 0x29ade7ff, 0x297befff,
            0x2973bdff, 0x6b63e7ff, 0x3952efff, 0x4a39efff, 0x2929e7ff, 0x1808ceff, 0x1008adff, 0x18088cff,
            0x181052ff, 0x3110adff, 0x4210ceff, 0x8418c6ff, 0x9429efff, 0x6b10efff, 0xb552e7ff, 0xb57be7ff,
            0xce94efff, 0xceb5efff, 0xefb5efff, 0xd663e7ff, 0xef42e7ff, 0xce18ceff, 0xb518efff, 0xad10bdff,
            0x7310adff, 0x311084ff, 0x630852ff, 0x210863ff, 0x210842ff, 0x4a0839ff, 0x941884ff, 0xb51873ff,
            0xef29b5ff, 0xef63b5ff, 0xef8cadff, 0xef2184ff, 0xd6186bff, 0xb51029ff, 0x94214aff, 0x841031ff,
    };

    /**
     * DawnBringer's Aurora palette run through some Lloyd relaxation to increase the difference between colors, but
     * some quirks in the process make lightness more even and saturation less erratic than in {@link #AURORA_LLOYD}.
     */
    public static final int[] AURORA_LLOYD_FLAT = new int[]{
            0x00000000, 0x000010ff, 0x101010ff, 0x103110ff, 0x313931ff, 0x4a4a4aff, 0x5a5a5aff, 0x8c637bff,
            0x7b7b7bff, 0x949494ff, 0xa5ada5ff, 0xbdadbdff, 0xadc6adff, 0xcedeceff, 0xefe7efff, 0xe7f7e7ff,
            0x108494ff, 0x39c6c6ff, 0x18efefff, 0x94f7efff, 0x7b8cefff, 0x0808e7ff, 0x314ab5ff, 0x080873ff,
            0x08084aff, 0x7b187bff, 0xd629d6ff, 0xd621c6ff, 0xde7be7ff, 0xf7adc6ff, 0xef7384ff, 0xef1031ff,
            0xce3139ff, 0x940818ff, 0x5a0818ff, 0x8c3910ff, 0xbd8439ff, 0xf78c08ff, 0xefe77bff, 0xe7f79cff,
            0xf7f710ff, 0xc6b54aff, 0x8c7b18ff, 0x108410ff, 0x42c642ff, 0x21ef21ff, 0xbdf7bdff, 0xada5bdff,
            0xc6a58cff, 0x94a584ff, 0x7bad94ff, 0x73738cff, 0x737b5aff, 0xa56342ff, 0xb57b6bff, 0xce846bff,
            0xef946bff, 0xe7ad84ff, 0xe7ce8cff, 0xefceadff, 0xdee7ceff, 0x634242ff, 0x733931ff, 0x94526bff,
            0xbd5a73ff, 0xb58c8cff, 0xefadadff, 0xefced6ff, 0xcebdadff, 0xbda56bff, 0x847352ff, 0x735a39ff,
            0x393121ff, 0x394a10ff, 0x5a8c18ff, 0x5ab521ff, 0x94ad42ff, 0x9cce5aff, 0xade773ff, 0xc6e794ff,
            0xefefc6ff, 0xb5e7a5ff, 0x84de63ff, 0x63e729ff, 0x52ce31ff, 0x429c21ff, 0x396329ff, 0x182108ff,
            0x185a29ff, 0x316331ff, 0x397b39ff, 0x218431ff, 0x528c52ff, 0x73ad73ff, 0x39d663ff, 0x63e763ff,
            0x94e794ff, 0xdee7efff, 0xb5efc6ff, 0x7be7a5ff, 0x84d68cff, 0x528c63ff, 0x106b4aff, 0x083918ff,
            0x214252ff, 0x397373ff, 0x52ad9cff, 0x84c6c6ff, 0x8cefceff, 0xbdefdeff, 0xbdd6efff, 0xa5d6e7ff,
            0xa5b5ceff, 0x84adc6ff, 0x399cc6ff, 0x527b94ff, 0x395273ff, 0x101029ff, 0x212139ff, 0x52295aff,
            0x5a2194ff, 0x5a5294ff, 0x7373adff, 0x7b73d6ff, 0x9484c6ff, 0xb594e7ff, 0xcedeefff, 0xdeceefff,
            0xad73c6ff, 0xbd39deff, 0x9439b5ff, 0x732984ff, 0x391039ff, 0x4a314aff, 0x9c219cff, 0x944a94ff,
            0xce39ceff, 0xb57bb5ff, 0xe78cd6ff, 0xefc6e7ff, 0xefb5efff, 0xe7adceff, 0xe784c6ff, 0xd65aceff,
            0xce63adff, 0xd629adff, 0x732163ff, 0x4a1031ff, 0x290810ff, 0x391808ff, 0x631808ff, 0xa51810ff,
            0xd62910ff, 0xce5242ff, 0xef4210ff, 0xef6339ff, 0xef5a5aff, 0xced618ff, 0xefa539ff, 0xde9c18ff,
            0xde6b10ff, 0xbd5a10ff, 0xa54a10ff, 0x5a3110ff, 0x525210ff, 0x6b6310ff, 0x949463ff, 0xad8c10ff,
            0xb5ad18ff, 0xe7ce52ff, 0xefd610ff, 0xefef39ff, 0xcef74aff, 0xa5e74aff, 0x94e718ff, 0x7bc610ff,
            0x6ba518ff, 0x397318ff, 0x293108ff, 0x184a10ff, 0x105210ff, 0x299421ff, 0x18bd18ff, 0x39ce29ff,
            0x7be773ff, 0x4ae75aff, 0x18c629ff, 0x18b55aff, 0x21944aff, 0x084231ff, 0x10947bff, 0x10c69cff,
            0x18d673ff, 0x29e7adff, 0x42ef94ff, 0x63efceff, 0x8ce7f7ff, 0x4ae7f7ff, 0x7bcee7ff, 0x18efd6ff,
            0x189cdeff, 0x105273ff, 0x212173ff, 0x212994ff, 0x2929c6ff, 0x396394ff, 0x1839efff, 0x106bb5ff,
            0x217bceff, 0x63a5c6ff, 0x42a5efff, 0x84adefff, 0x4ac6efff, 0xb5bdf7ff, 0x10c6efff, 0x1073efff,
            0x427bbdff, 0x6b6befff, 0x4263f7ff, 0x6342e7ff, 0x3142e7ff, 0x1818deff, 0x0808b5ff, 0x181094ff,
            0x18185aff, 0x4a18a5ff, 0x5a18ceff, 0x8429d6ff, 0x8c4ae7ff, 0x7318e7ff, 0xb563efff, 0xa594efff,
            0xce94efff, 0xd6adefff, 0xe7bdefff, 0xde6befff, 0xe76be7ff, 0xce39d6ff, 0xad29e7ff, 0xa521b5ff,
            0x8c18bdff, 0x5a107bff, 0x631063ff, 0x391063ff, 0x39104aff, 0x73104aff, 0xa5218cff, 0xc6107bff,
            0xef42a5ff, 0xef73b5ff, 0xef94adff, 0xef4a73ff, 0xe72173ff, 0xce1039ff, 0x9c3152ff, 0x941042ff, 
    };

    /**
     * DawnBringer's Aurora palette run through more Lloyd relaxation to increase the difference between colors, with
     * better definition on edges than {@link #AURORA_LLOYD_FLAT} and hopefully better color distribution than
     * {@link #AURORA_LLOYD} (almost certainly better mathematically than {@link #AURORA}, though it's debatable whether
     * this is an aesthetic improvement on a particular image).
     */
    public static final int[] AURORA_RELAXED = new int[]{
            0x00000000, 0x000010ff, 0x101008ff, 0x421839ff, 0x313931ff, 0x4a4a5aff, 0x6b525aff, 0x736b6bff,
            0x9c738cff, 0x94948cff, 0xa5a5a5ff, 0xb5b5b5ff, 0xadcec6ff, 0xced6c6ff, 0xe7f7e7ff, 0xf7f7f7ff,
            0x087b7bff, 0x39c6c6ff, 0x18e7efff, 0xa5f7efff, 0x7b8cefff, 0x0808deff, 0x4242bdff, 0x08084aff,
            0x100863ff, 0x841084ff, 0xde29ceff, 0xe710bdff, 0xe78cefff, 0xf7b5c6ff, 0xf77b8cff, 0xef0829ff,
            0xde314aff, 0x840818ff, 0x5a0818ff, 0x8c3910ff, 0xbd8439ff, 0xf78408ff, 0xe7ce73ff, 0xeff794ff,
            0xf7f710ff, 0xc6b542ff, 0x847b10ff, 0x087b21ff, 0x39ce42ff, 0x10e731ff, 0xb5f7b5ff, 0xbdadc6ff,
            0xc6ad8cff, 0x9cad84ff, 0x7b9494ff, 0x737b8cff, 0x737352ff, 0xa55a4aff, 0xbd736bff, 0xde7b63ff,
            0xe7946bff, 0xe7a58cff, 0xefbd94ff, 0xf7d6adff, 0xefe7d6ff, 0x7b294aff, 0x734239ff, 0xa54a63ff,
            0xbd6373ff, 0xce8c8cff, 0xdeadadff, 0xf7ced6ff, 0xdec6adff, 0xb59c6bff, 0x947342ff, 0x735231ff,
            0x423121ff, 0x315210ff, 0x5a8c21ff, 0x849c4aff, 0x9ca542ff, 0xadbd63ff, 0xb5de7bff, 0xceef94ff,
            0xe7f7c6ff, 0xb5efa5ff, 0xa5ce8cff, 0x6bde39ff, 0x63ad4aff, 0x429429ff, 0x425231ff, 0x182108ff,
            0x185a31ff, 0x217321ff, 0x4a6b4aff, 0x218c31ff, 0x529452ff, 0x6bb56bff, 0x52c67bff, 0x63e763ff,
            0x94e79cff, 0xdef7efff, 0xb5f7ceff, 0x7befa5ff, 0x84bd94ff, 0x4a8c63ff, 0x106b4aff, 0x083918ff,
            0x213952ff, 0x397373ff, 0x52ad9cff, 0x73cebdff, 0x94efd6ff, 0xc6efefff, 0xbdd6efff, 0x9ccedeff,
            0xadb5d6ff, 0x8cadc6ff, 0x399cc6ff, 0x4a7b94ff, 0x315a73ff, 0x101029ff, 0x182139ff, 0x39395aff,
            0x4a3994ff, 0x5a529cff, 0x7373adff, 0x7b73ceff, 0x9484c6ff, 0xada5e7ff, 0xcedef7ff, 0xdedef7ff,
            0xad84c6ff, 0x9c52ceff, 0x7b5294ff, 0x73318cff, 0x4a1852ff, 0x4a314aff, 0x843173ff, 0x9c4a8cff,
            0xad52adff, 0xb573b5ff, 0xf78ce7ff, 0xf7d6efff, 0xdec6deff, 0xe7b5d6ff, 0xde9cbdff, 0xce7bbdff,
            0xce739cff, 0xc6429cff, 0x7b1063ff, 0x420829ff, 0x310818ff, 0x421008ff, 0x631808ff, 0xa51810ff,
            0xd62110ff, 0xd65242ff, 0xef4210ff, 0xef6b39ff, 0xef5263ff, 0xd6ce18ff, 0xefa542ff, 0xde9c18ff,
            0xde6b10ff, 0xbd5a10ff, 0xa54a18ff, 0x6b3110ff, 0x525210ff, 0x636b10ff, 0x8c8463ff, 0xa59410ff,
            0xadb510ff, 0xe7e763ff, 0xe7de10ff, 0xefef42ff, 0xc6ef39ff, 0x9cef4aff, 0x94e718ff, 0x6bd610ff,
            0x63ad10ff, 0x397310ff, 0x293108ff, 0x214a08ff, 0x105a10ff, 0x219410ff, 0x18c610ff, 0x31de18ff,
            0x7bef63ff, 0x42ef5aff, 0x10bd29ff, 0x18b55aff, 0x188c4aff, 0x084231ff, 0x189c84ff, 0x18bd94ff,
            0x18de6bff, 0x29e7adff, 0x4aef94ff, 0x5af7ceff, 0x8ceff7ff, 0x4ae7f7ff, 0x7bd6e7ff, 0x10e7ceff,
            0x189cdeff, 0x105273ff, 0x18295aff, 0x21298cff, 0x1042adff, 0x21739cff, 0x2131e7ff, 0x106bbdff,
            0x217bceff, 0x6ba5c6ff, 0x42adefff, 0x84adefff, 0x52c6efff, 0xadbdf7ff, 0x10bdefff, 0x1073efff,
            0x427bc6ff, 0x6b6befff, 0x426bf7ff, 0x6342e7ff, 0x3142efff, 0x1818deff, 0x0808a5ff, 0x21109cff,
            0x181863ff, 0x4a18adff, 0x5218d6ff, 0x8c29deff, 0x944aefff, 0x7310e7ff, 0xb563efff, 0xb58cf7ff,
            0xd69cf7ff, 0xd6bdefff, 0xefbdf7ff, 0xde6befff, 0xef52e7ff, 0xd631e7ff, 0xbd21efff, 0xb518bdff,
            0x8c18bdff, 0x5a188cff, 0x631063ff, 0x42086bff, 0x310842ff, 0x6b1042ff, 0xad1094ff, 0xc6107bff,
            0xef42bdff, 0xf76bbdff, 0xf794b5ff, 0xef4284ff, 0xe7187bff, 0xc61039ff, 0x9c3152ff, 0x9c1039ff,
    };

    public static int mixLightly(int baseColor, int mixColor)
    {
        final int
                r = (baseColor >>> 26) * 3 + (mixColor >>> 26),
                g = (baseColor >>> 18 & 0x3F) * 3 + (mixColor >>> 18 & 0x3F),
                b = (baseColor >>> 10 & 0x3F) * 3 + (mixColor >>> 10 & 0x3F);
        return r << 24 | g << 16 | b << 8 | 0xFF;
    }
    public static int mixThird(int baseColor, int mixColor)
    {
        final int
                r = ((baseColor >>> 23 & 0x1FE) + (mixColor >>> 24)) / 3,
                g = ((baseColor >>> 15 & 0x1FE) + (mixColor >>> 16 & 0xFF)) / 3,
                b = ((baseColor >>> 7 & 0x1FE) + (mixColor >>> 8 & 0xFF)) / 3;
        return r << 24 | g << 16 | b << 8 | 0xFF;
    }

    public static int mixEvenly(int baseColor, int mixColor)
    {
        final int
                r = (baseColor >>> 25) + (mixColor >>> 25),
                g = (baseColor >>> 17 & 0x7F) + (mixColor >>> 17 & 0x7F),
                b = (baseColor >>> 9 & 0x7F) + (mixColor >>> 9 & 0x7F);
        return r << 24 | g << 16 | b << 8 | 0xFF;
    }

    public static int mixHeavily(int baseColor, int mixColor)
    {
        final int
                r = (mixColor >>> 26) * 3 + (baseColor >>> 26),
                g = (mixColor >>> 18 & 0x3F) * 3 + (baseColor >>> 18 & 0x3F),
                b = (mixColor >>> 10 & 0x3F) * 3 + (baseColor >>> 10 & 0x3F);
        return r << 24 | g << 16 | b << 8 | 0xFF;
    }


}
