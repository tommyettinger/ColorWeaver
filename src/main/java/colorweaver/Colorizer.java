package colorweaver;

import com.badlogic.gdx.math.MathUtils;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by Tommy Ettinger on 1/13/2019.
 */
public abstract class Colorizer extends Dimmer implements IColorizer {
    /**
     * Color values as RGBA8888 ints to use when shading an Aurora-palette model.
     * The color in index 1 of each 4-element sub-array is the "dimmer" color and the one that will match the voxel
     * color used in a model. The color at index 0 is "bright", index 2 is "dim", and index 3 is "dark".
     * To visualize this, <a href="https://i.imgur.com/rQDrPE7.png">use this image</a>, with the first 32 items in
     * AURORA_RAMPS corresponding to the first column from top to bottom, the next 32 items in the second column, etc.
     */
    public static final int[] AURORA_RAMP_VALUES = new int[]{
            0x00000000, 0x00000000, 0x00000000, 0x00000000,
            0x252525FF, 0x010101FF, 0x010101FF, 0x010101FF,
            0x3C233CFF, 0x131313FF, 0x010101FF, 0x010101FF,
            0x3B3B57FF, 0x252525FF, 0x010101FF, 0x010101FF,
            0x5B5B5BFF, 0x373737FF, 0x0F192DFF, 0x131313FF,
            0x6E6E6EFF, 0x494949FF, 0x3C233CFF, 0x252525FF,
            0x6E8287FF, 0x5B5B5BFF, 0x3B3B57FF, 0x373737FF,
            0x7E9494FF, 0x6E6E6EFF, 0x494949FF, 0x3B3B57FF,
            0xA4A4A4FF, 0x808080FF, 0x5B5B5BFF, 0x494949FF,
            0xBCAFC0FF, 0x929292FF, 0x6E6E6EFF, 0x5B5B5BFF,
            0xC9C9C9FF, 0xA4A4A4FF, 0x808080FF, 0x6E8287FF,
            0xE3C7E3FF, 0xB6B6B6FF, 0x929292FF, 0x7E9494FF,
            0xE3E3FFFF, 0xC9C9C9FF, 0xA4A4A4FF, 0x929292FF,
            0xFFFFFFFF, 0xDBDBDBFF, 0xB6B6B6FF, 0xBCAFC0FF,
            0xFFFFFFFF, 0xEDEDEDFF, 0xC9C9C9FF, 0xB6B6B6FF,
            0xFFFFFFFF, 0xFFFFFFFF, 0xDBDBDBFF, 0xE3C7E3FF,
            0x06C491FF, 0x007F7FFF, 0x0F377DFF, 0x0C2148FF,
            0x5AC5FFFF, 0x3FBFBFFF, 0x06C491FF, 0x129880FF,
            0x55E6FFFF, 0x00FFFFFF, 0x00BFFFFF, 0x109CDEFF,
            0xFFFFFFFF, 0xBFFFFFFF, 0xABC7E3FF, 0x8FC7C7FF,
            0xB991FFFF, 0x8181FFFF, 0x4A5AFFFF, 0x6241F6FF,
            0x3C3CF5FF, 0x0000FFFF, 0x00007FFF, 0x010101FF,
            0x4A5AFFFF, 0x3F3FBFFF, 0x5010B0FF, 0x231094FF,
            0x101CDAFF, 0x00007FFF, 0x010101FF, 0x010101FF,
            0x0F377DFF, 0x0F0F50FF, 0x010101FF, 0x010101FF,
            0x8C14BEFF, 0x7F007FFF, 0x320A46FF, 0x010101FF,
            0xFF52FFFF, 0xBF3FBFFF, 0xBD10C5FF, 0x8C14BEFF,
            0xFF52FFFF, 0xF500F5FF, 0x8C14BEFF, 0x7F007FFF,
            0xF8C6FCFF, 0xFD81FFFF, 0xFF52FFFF, 0xBF3FBFFF,
            0xFFFFFFFF, 0xFFC0CBFF, 0xD7A0BEFF, 0xC78FB9FF,
            0xFAA0B9FF, 0xFF8181FF, 0xD5524AFF, 0xBF3F3FFF,
            0xFF3C0AFF, 0xFF0000FF, 0xA5140AFF, 0x7F0000FF,
            0xFF6262FF, 0xBF3F3FFF, 0xBD1039FF, 0x911437FF,
            0x911437FF, 0x7F0000FF, 0x280A1EFF, 0x010101FF,
            0x573B3BFF, 0x551414FF, 0x010101FF, 0x010101FF,
            0xBF3F3FFF, 0x7F3F00FF, 0x621800FF, 0x401811FF,
            0xE19B7DFF, 0xBF7F3FFF, 0xBF3F3FFF, 0xA04B05FF,
            0xFFA53CFF, 0xFF7F00FF, 0xB45A00FF, 0xDA2010FF,
            0xFFFFBFFF, 0xFFBF81FF, 0xE19B7DFF, 0xD08A74FF,
            0xFFFFFFFF, 0xFFFFBFFF, 0xDADAABFF, 0xE3C7ABFF,
            0xFFFFBFFF, 0xFFFF00FF, 0xB1B10AFF, 0xAC9400FF,
            0xE6D55AFF, 0xBFBF3FFF, 0xAC9400FF, 0x7F7F00FF,
            0xB1B10AFF, 0x7F7F00FF, 0x53500AFF, 0x5F3214FF,
            0x00C514FF, 0x007F00FF, 0x191E0FFF, 0x010101FF,
            0x4BF05AFF, 0x3FBF3FFF, 0x1C8C4EFF, 0x149605FF,
            0x4BF05AFF, 0x00FF00FF, 0x00C514FF, 0x149605FF,
            0xE1F8FAFF, 0xAFFFAFFF, 0x8FC78FFF, 0x87B48EFF,
            0xE3C7E3FF, 0xBCAFC0FF, 0x929292FF, 0xAB73ABFF,
            0xE3C7ABFF, 0xCBAA89FF, 0xC07872FF, 0xAB7373FF,
            0xC9C9C9FF, 0xA6A090FF, 0x808080FF, 0x6E6E6EFF,
            0xB6B6B6FF, 0x7E9494FF, 0x6E6E6EFF, 0x73578FFF,
            0xA4A4A4FF, 0x6E8287FF, 0x5B5B5BFF, 0x494973FF,
            0x929292FF, 0x7E6E60FF, 0x494949FF, 0x573B3BFF,
            0xC78F8FFF, 0xA0695FFF, 0x98344DFF, 0x73413CFF,
            0xE19B7DFF, 0xC07872FF, 0x8E5555FF, 0x98344DFF,
            0xEBAA8CFF, 0xD08A74FF, 0xA0695FFF, 0x8E5555FF,
            0xF6C8AFFF, 0xE19B7DFF, 0xC07872FF, 0xA0695FFF,
            0xFFC0CBFF, 0xEBAA8CFF, 0xD08A74FF, 0xC07872FF,
            0xF8D2DAFF, 0xF5B99BFF, 0xD08A74FF, 0xC07872FF,
            0xFFFFBFFF, 0xF6C8AFFF, 0xCBAA89FF, 0xC78F8FFF,
            0xFFFFFFFF, 0xF5E1D2FF, 0xC9C9C9FF, 0xE1B9D2FF,
            0x8E5555FF, 0x573B3BFF, 0x321623FF, 0x280A1EFF,
            0x7E6E60FF, 0x73413CFF, 0x551937FF, 0x401811FF,
            0xAB7373FF, 0x8E5555FF, 0x573B3BFF, 0x551937FF,
            0xC78F8FFF, 0xAB7373FF, 0x8E5555FF, 0x73413CFF,
            0xE3ABABFF, 0xC78F8FFF, 0xAB7373FF, 0xA0695FFF,
            0xF8D2DAFF, 0xE3ABABFF, 0xC78F8FFF, 0xC87DA0FF,
            0xFFFFFFFF, 0xF8D2DAFF, 0xD7A0BEFF, 0xC78FB9FF,
            0xEDEDEDFF, 0xE3C7ABFF, 0xCBAA89FF, 0xC78F8FFF,
            0xE3ABABFF, 0xC49E73FF, 0xAB7373FF, 0xA0695FFF,
            0x929292FF, 0x8F7357FF, 0x73573BFF, 0x73413CFF,
            0x8F7357FF, 0x73573BFF, 0x414123FF, 0x3B2D1FFF,
            0x494949FF, 0x3B2D1FFF, 0x010101FF, 0x010101FF,
            0x73573BFF, 0x414123FF, 0x191E0FFF, 0x131313FF,
            0x8F8F57FF, 0x73733BFF, 0x465032FF, 0x414123FF,
            0xA6A090FF, 0x8F8F57FF, 0x73733BFF, 0x73573BFF,
            0xCBAA89FF, 0xA2A255FF, 0x8F7357FF, 0x73733BFF,
            0xE3C7ABFF, 0xB5B572FF, 0x8F8F57FF, 0x8C805AFF,
            0xEDEDC7FF, 0xC7C78FFF, 0xC49E73FF, 0xA2A255FF,
            0xEDEDEDFF, 0xDADAABFF, 0xCBAA89FF, 0xA6A090FF,
            0xFFFFFFFF, 0xEDEDC7FF, 0xC9C9C9FF, 0xB6B6B6FF,
            0xEDEDEDFF, 0xC7E3ABFF, 0xABC78FFF, 0x8FC78FFF,
            0xC7E3ABFF, 0xABC78FFF, 0x929292FF, 0x8F8F57FF,
            0xABC78FFF, 0x8EBE55FF, 0x738F57FF, 0x587D3EFF,
            0x87B48EFF, 0x738F57FF, 0x506450FF, 0x465032FF,
            0x73AB73FF, 0x587D3EFF, 0x3B573BFF, 0x465032FF,
            0x507D5FFF, 0x465032FF, 0x1E2D23FF, 0x191E0FFF,
            0x373737FF, 0x191E0FFF, 0x010101FF, 0x010101FF,
            0x3B7349FF, 0x235037FF, 0x0F192DFF, 0x131313FF,
            0x507D5FFF, 0x3B573BFF, 0x1E2D23FF, 0x252525FF,
            0x6E8287FF, 0x506450FF, 0x373737FF, 0x1E2D23FF,
            0x6E8287FF, 0x3B7349FF, 0x235037FF, 0x123832FF,
            0x73AB73FF, 0x578F57FF, 0x3B573BFF, 0x235037FF,
            0x8FC78FFF, 0x73AB73FF, 0x578F57FF, 0x507D5FFF,
            0x8FC7C7FF, 0x64C082FF, 0x578F57FF, 0x507D5FFF,
            0xABE3C5FF, 0x8FC78FFF, 0x73AB73FF, 0x6E8287FF,
            0xC7F1F1FF, 0xA2D8A2FF, 0x87B48EFF, 0x73AB73FF,
            0xFFFFFFFF, 0xE1F8FAFF, 0xBED2F0FF, 0xABC7E3FF,
            0xE1F8FAFF, 0xB4EECAFF, 0x8FC7C7FF, 0x8FC78FFF,
            0xE1F8FAFF, 0xABE3C5FF, 0x87B48EFF, 0x64ABABFF,
            0xA2D8A2FF, 0x87B48EFF, 0x808080FF, 0x6E8287FF,
            0x7E9494FF, 0x507D5FFF, 0x3B573BFF, 0x235037FF,
            0x3B7373FF, 0x0F6946FF, 0x123832FF, 0x0C2148FF,
            0x494949FF, 0x1E2D23FF, 0x010101FF, 0x010101FF,
            0x3B7373FF, 0x234146FF, 0x0F192DFF, 0x010101FF,
            0x6E8287FF, 0x3B7373FF, 0x3B3B57FF, 0x234146FF,
            0x8FC7C7FF, 0x64ABABFF, 0x57738FFF, 0x3B7373FF,
            0xABE3E3FF, 0x8FC7C7FF, 0x64ABABFF, 0x578FC7FF,
            0xE1F8FAFF, 0xABE3E3FF, 0x8FC7C7FF, 0x8FABC7FF,
            0xFFFFFFFF, 0xC7F1F1FF, 0xABC7E3FF, 0xA8B9DCFF,
            0xE1F8FAFF, 0xBED2F0FF, 0xABABE3FF, 0x8FABC7FF,
            0xE3E3FFFF, 0xABC7E3FF, 0x8FABC7FF, 0x8F8FC7FF,
            0xD0DAF8FF, 0xA8B9DCFF, 0x8F8FC7FF, 0x7676CAFF,
            0xABC7E3FF, 0x8FABC7FF, 0x7E9494FF, 0x736EAAFF,
            0x90B0FFFF, 0x578FC7FF, 0x326496FF, 0x0F377DFF,
            0x7E9494FF, 0x57738FFF, 0x3B5773FF, 0x494973FF,
            0x57738FFF, 0x3B5773FF, 0x162C52FF, 0x0C2148FF,
            0x234146FF, 0x0F192DFF, 0x010101FF, 0x010101FF,
            0x3B3B57FF, 0x1F1F3BFF, 0x010101FF, 0x010101FF,
            0x57578FFF, 0x3B3B57FF, 0x1F1F3BFF, 0x0F192DFF,
            0x57738FFF, 0x494973FF, 0x162C52FF, 0x1F1F3BFF,
            0x7676CAFF, 0x57578FFF, 0x3B3B57FF, 0x162C52FF,
            0x8F8FC7FF, 0x736EAAFF, 0x494973FF, 0x573B73FF,
            0x90B0FFFF, 0x7676CAFF, 0x57578FFF, 0x3F3FBFFF,
            0xABABE3FF, 0x8F8FC7FF, 0x736EAAFF, 0x73578FFF,
            0xBED2F0FF, 0xABABE3FF, 0x8F8FC7FF, 0x7676CAFF,
            0xFFFFFFFF, 0xD0DAF8FF, 0xA8B9DCFF, 0xABABE3FF,
            0xFFFFFFFF, 0xE3E3FFFF, 0xBEB9FAFF, 0xA8B9DCFF,
            0xBEB9FAFF, 0xAB8FC7FF, 0x736EAAFF, 0x73578FFF,
            0xBD62FFFF, 0x8F57C7FF, 0x8C14BEFF, 0x5A187BFF,
            0xAB73ABFF, 0x73578FFF, 0x573B73FF, 0x5A187BFF,
            0x73578FFF, 0x573B73FF, 0x410062FF, 0x320A46FF,
            0x494949FF, 0x3C233CFF, 0x010101FF, 0x010101FF,
            0x724072FF, 0x463246FF, 0x280A1EFF, 0x010101FF,
            0xAB57ABFF, 0x724072FF, 0x641464FF, 0x3C233CFF,
            0xAB73ABFF, 0x8F578FFF, 0x573B73FF, 0x641464FF,
            0xAB8FC7FF, 0xAB57ABFF, 0x724072FF, 0xA01982FF,
            0xD7A0BEFF, 0xAB73ABFF, 0x8F578FFF, 0x73578FFF,
            0xFFDCF5FF, 0xEBACE1FF, 0xC78FB9FF, 0xC87DA0FF,
            0xFFFFFFFF, 0xFFDCF5FF, 0xE1B9D2FF, 0xEBACE1FF,
            0xFFFFFFFF, 0xE3C7E3FF, 0xBCAFC0FF, 0xC78FB9FF,
            0xFFDCF5FF, 0xE1B9D2FF, 0xC78FB9FF, 0xAB73ABFF,
            0xE3C7E3FF, 0xD7A0BEFF, 0xC87DA0FF, 0xAB73ABFF,
            0xEBACE1FF, 0xC78FB9FF, 0xAB57ABFF, 0x8F578FFF,
            0xD7A0BEFF, 0xC87DA0FF, 0xAB57ABFF, 0x8F578FFF,
            0xC78FB9FF, 0xC35A91FF, 0x8E5555FF, 0xA01982FF,
            0x724072FF, 0x4B2837FF, 0x280A1EFF, 0x010101FF,
            0x463246FF, 0x321623FF, 0x010101FF, 0x010101FF,
            0x4B2837FF, 0x280A1EFF, 0x010101FF, 0x010101FF,
            0x573B3BFF, 0x401811FF, 0x010101FF, 0x010101FF,
            0x7F3F00FF, 0x621800FF, 0x280A1EFF, 0x010101FF,
            0xBF3F3FFF, 0xA5140AFF, 0x280A1EFF, 0x010101FF,
            0xF55A32FF, 0xDA2010FF, 0x7F0000FF, 0x280A1EFF,
            0xFF8181FF, 0xD5524AFF, 0xBD1039FF, 0x911437FF,
            0xF55A32FF, 0xFF3C0AFF, 0xA5140AFF, 0x7F0000FF,
            0xFF8181FF, 0xF55A32FF, 0xFF3C0AFF, 0xDA2010FF,
            0xEBAA8CFF, 0xFF6262FF, 0xBF3F3FFF, 0xBD1039FF,
            0xFFEA4AFF, 0xF6BD31FF, 0xD79B0FFF, 0xAC9400FF,
            0xFFBF81FF, 0xFFA53CFF, 0xD79B0FFF, 0xDA6E0AFF,
            0xFFA53CFF, 0xD79B0FFF, 0xB45A00FF, 0xA04B05FF,
            0xFFA53CFF, 0xDA6E0AFF, 0xA04B05FF, 0xA5140AFF,
            0xBF7F3FFF, 0xB45A00FF, 0x7F3F00FF, 0xA5140AFF,
            0xDA6E0AFF, 0xA04B05FF, 0xA5140AFF, 0x621800FF,
            0x73573BFF, 0x5F3214FF, 0x280A1EFF, 0x010101FF,
            0x73733BFF, 0x53500AFF, 0x283405FF, 0x191E0FFF,
            0x73733BFF, 0x626200FF, 0x283405FF, 0x401811FF,
            0x929292FF, 0x8C805AFF, 0x73573BFF, 0x465032FF,
            0xBFBF3FFF, 0xAC9400FF, 0xA04B05FF, 0x7F3F00FF,
            0xE6D55AFF, 0xB1B10AFF, 0x7F7F00FF, 0x626200FF,
            0xFFFFBFFF, 0xE6D55AFF, 0xBFBF3FFF, 0xB1B10AFF,
            0xFFEA4AFF, 0xFFD510FF, 0xD79B0FFF, 0xAC9400FF,
            0xFFFFBFFF, 0xFFEA4AFF, 0xF6BD31FF, 0xD79B0FFF,
            0xFFFFBFFF, 0xC8FF41FF, 0x96DC19FF, 0x73C805FF,
            0xAFFFAFFF, 0x9BF046FF, 0x8EBE55FF, 0x73C805FF,
            0xC8FF41FF, 0x96DC19FF, 0x6AA805FF, 0x7F7F00FF,
            0x9BF046FF, 0x73C805FF, 0x7F7F00FF, 0x3C6E14FF,
            0x8EBE55FF, 0x6AA805FF, 0x626200FF, 0x53500AFF,
            0x578F57FF, 0x3C6E14FF, 0x204608FF, 0x283405FF,
            0x465032FF, 0x283405FF, 0x010101FF, 0x010101FF,
            0x465032FF, 0x204608FF, 0x131313FF, 0x010101FF,
            0x3B573BFF, 0x0C5C0CFF, 0x191E0FFF, 0x131313FF,
            0x3FBF3FFF, 0x149605FF, 0x0C5C0CFF, 0x204608FF,
            0x4BF05AFF, 0x0AD70AFF, 0x149605FF, 0x007F00FF,
            0x4BF05AFF, 0x14E60AFF, 0x149605FF, 0x007F00FF,
            0xAFFFAFFF, 0x7DFF73FF, 0x4BF05AFF, 0x3FBF3FFF,
            0x7DFF73FF, 0x4BF05AFF, 0x3FBF3FFF, 0x05B450FF,
            0x4BF05AFF, 0x00C514FF, 0x007F00FF, 0x0C5C0CFF,
            0x06C491FF, 0x05B450FF, 0x0F6946FF, 0x0C5C0CFF,
            0x578F57FF, 0x1C8C4EFF, 0x123832FF, 0x0F192DFF,
            0x3B5773FF, 0x123832FF, 0x010101FF, 0x010101FF,
            0x3FBFBFFF, 0x129880FF, 0x055A5CFF, 0x0C2148FF,
            0x3FBFBFFF, 0x06C491FF, 0x007F7FFF, 0x055A5CFF,
            0x2DEBA8FF, 0x00DE6AFF, 0x007F7FFF, 0x0F6946FF,
            0x6AFFCDFF, 0x2DEBA8FF, 0x06C491FF, 0x129880FF,
            0xBFFFFFFF, 0x3CFEA5FF, 0x00DE6AFF, 0x06C491FF,
            0xBFFFFFFF, 0x6AFFCDFF, 0x2DEBA8FF, 0x3FBFBFFF,
            0xBFFFFFFF, 0x91EBFFFF, 0x5AC5FFFF, 0x3FBFBFFF,
            0xBFFFFFFF, 0x55E6FFFF, 0x08DED5FF, 0x4AA4FFFF,
            0xBFFFFFFF, 0x7DD7F0FF, 0x3FBFBFFF, 0x699DC3FF,
            0x00FFFFFF, 0x08DED5FF, 0x109CDEFF, 0x007F7FFF,
            0x4AA4FFFF, 0x109CDEFF, 0x186ABDFF, 0x004A9CFF,
            0x129880FF, 0x055A5CFF, 0x0C2148FF, 0x0F0F50FF,
            0x3B5773FF, 0x162C52FF, 0x010101FF, 0x010101FF,
            0x3F3FBFFF, 0x0F377DFF, 0x0F0F50FF, 0x00007FFF,
            0x186ABDFF, 0x004A9CFF, 0x00007FFF, 0x010101FF,
            0x4B7DC8FF, 0x326496FF, 0x0F377DFF, 0x162C52FF,
            0x007FFFFF, 0x0052F6FF, 0x101CDAFF, 0x0010BDFF,
            0x4B7DC8FF, 0x186ABDFF, 0x004A9CFF, 0x0F377DFF,
            0x4AA4FFFF, 0x2378DCFF, 0x004A9CFF, 0x0010BDFF,
            0x90B0FFFF, 0x699DC3FF, 0x4B7DC8FF, 0x57738FFF,
            0x55E6FFFF, 0x4AA4FFFF, 0x109CDEFF, 0x2378DCFF,
            0x91EBFFFF, 0x90B0FFFF, 0x8181FFFF, 0x786EF0FF,
            0x91EBFFFF, 0x5AC5FFFF, 0x109CDEFF, 0x2378DCFF,
            0xE3E3FFFF, 0xBEB9FAFF, 0x8F8FC7FF, 0x7676CAFF,
            0x00FFFFFF, 0x00BFFFFF, 0x007FFFFF, 0x0052F6FF,
            0x00BFFFFF, 0x007FFFFF, 0x0052F6FF, 0x004A9CFF,
            0x8181FFFF, 0x4B7DC8FF, 0x186ABDFF, 0x326496FF,
            0xB991FFFF, 0x786EF0FF, 0x6241F6FF, 0x3F3FBFFF,
            0x786EF0FF, 0x4A5AFFFF, 0x101CDAFF, 0x0010BDFF,
            0x786EF0FF, 0x6241F6FF, 0x6010D0FF, 0x101CDAFF,
            0x8181FFFF, 0x3C3CF5FF, 0x101CDAFF, 0x0010BDFF,
            0x3C3CF5FF, 0x101CDAFF, 0x00007FFF, 0x010101FF,
            0x3C3CF5FF, 0x0010BDFF, 0x010101FF, 0x010101FF,
            0x3F3FBFFF, 0x231094FF, 0x010101FF, 0x010101FF,
            0x3B3B57FF, 0x0C2148FF, 0x010101FF, 0x010101FF,
            0x8732D2FF, 0x5010B0FF, 0x00007FFF, 0x010101FF,
            0x6241F6FF, 0x6010D0FF, 0x231094FF, 0x00007FFF,
            0xBD62FFFF, 0x8732D2FF, 0x5010B0FF, 0x410062FF,
            0xBD62FFFF, 0x9C41FFFF, 0x7F00FFFF, 0x6010D0FF,
            0xBD29FFFF, 0x7F00FFFF, 0x231094FF, 0x00007FFF,
            0xB991FFFF, 0xBD62FFFF, 0x9C41FFFF, 0x8732D2FF,
            0xD7C3FAFF, 0xB991FFFF, 0xBD62FFFF, 0x8F57C7FF,
            0xF8C6FCFF, 0xD7A5FFFF, 0xBD62FFFF, 0x8F57C7FF,
            0xFFFFFFFF, 0xD7C3FAFF, 0xABABE3FF, 0xAB8FC7FF,
            0xFFFFFFFF, 0xF8C6FCFF, 0xD7A5FFFF, 0xC78FB9FF,
            0xD7A5FFFF, 0xE673FFFF, 0xBF3FBFFF, 0x8732D2FF,
            0xFD81FFFF, 0xFF52FFFF, 0xDA20E0FF, 0xBD10C5FF,
            0xFF52FFFF, 0xDA20E0FF, 0x8C14BEFF, 0x7F007FFF,
            0xBD62FFFF, 0xBD29FFFF, 0x8C14BEFF, 0x7F00FFFF,
            0xFF52FFFF, 0xBD10C5FF, 0x7F007FFF, 0x410062FF,
            0xBD29FFFF, 0x8C14BEFF, 0x5010B0FF, 0x7F007FFF,
            0x724072FF, 0x5A187BFF, 0x0F0F50FF, 0x010101FF,
            0xA01982FF, 0x641464FF, 0x280A1EFF, 0x010101FF,
            0x573B73FF, 0x410062FF, 0x010101FF, 0x010101FF,
            0x5A187BFF, 0x320A46FF, 0x010101FF, 0x010101FF,
            0x724072FF, 0x551937FF, 0x010101FF, 0x010101FF,
            0xBF3FBFFF, 0xA01982FF, 0x410062FF, 0x280A1EFF,
            0xFC3A8CFF, 0xC80078FF, 0x7F007FFF, 0x7F0000FF,
            0xFD81FFFF, 0xFF50BFFF, 0xFC3A8CFF, 0xE61E78FF,
            0xFD81FFFF, 0xFF6AC5FF, 0xFC3A8CFF, 0xE61E78FF,
            0xF8D2DAFF, 0xFAA0B9FF, 0xFF6AC5FF, 0xC87DA0FF,
            0xFF50BFFF, 0xFC3A8CFF, 0xC80078FF, 0x911437FF,
            0xFF50BFFF, 0xE61E78FF, 0x911437FF, 0x7F0000FF,
            0xE61E78FF, 0xBD1039FF, 0x7F0000FF, 0x280A1EFF,
            0xC35A91FF, 0x98344DFF, 0x551937FF, 0x551414FF,
            0xBF3F3FFF, 0x911437FF, 0x7F0000FF, 0x280A1EFF,
    };
//    /**
//     * Bytes that correspond to palette indices to use when shading an Aurora-palette model and ramps should consider
//     * the aesthetic warmth or coolness of a color, brightening to a warmer color and darkening to a cooler one.
//     * The color in index 1 of each 4-element sub-array is the "dimmer" color and the one that will match the voxel
//     * color used in a model. The color at index 0 is "bright", index 2 is "dim", and index 3 is "dark". Normal usage
//     * with a renderer will use {@link #AURORA_WARMTH_RAMP_VALUES}; this array would be used to figure out what indices are
//     * related to another color for the purpose of procedurally using similar colors with different lightness.
//     * To visualize this, <a href="https://i.imgur.com/zeJpyQ4.png">use this image</a>, with the first 32 items in
//     * AURORA_WARMTH_RAMPS corresponding to the first column from top to bottom, the next 32 items in the second column, etc.
//     */
//    public static final byte[][] AURORA_WARMTH_RAMPS = new byte[][]{
//            {0, 0, 0, 0},
//            {2, 1, 1, 1},
//            {87, 2, 1, 1},
//            {103, 3, 87, 2},
//            {73, 4, 103, 118},
//            {62, 5, 119, 4},
//            {63, 6, 5, 119},
//            {52, 7, 6, 116},
//            {64, 8, 51, 7},
//            {49, 9, 50, 8},
//            {48, 10, 49, 9},
//            {66, 11, 10, 113},
//            {-116, 12, 11, 112},
//            {60, 13, 110, 12},
//            {15, 14, 127, 13},
//            {15, 15, 97, 109},
//            {105, 16, -55, -52},
//            {106, 17, -65, -56},
//            {-59, 18, -57, -42},
//            {97, 19, -60, -59},
//            {-25, 20, -39, -40},
//            {-27, 21, -34, 21},
//            {-29, 22, -53, 23},
//            {-33, 23, 1, 1},
//            {-12, 24, 1, 1},
//            {-10, 25, -14, -13},
//            {-111, 26, -29, 22},
//            {-19, 27, -17, -16},
//            {-6, 28, -21, -26},
//            {67, 29, -115, 47},
//            {57, 30, 55, 54},
//            {-102, 31, -105, -1},
//            {-103, 32, -2, 62},
//            {-105, 33, 34, -108},
//            {-106, 34, -107, -108},
//            {-94, 35, -93, 72},
//            {-97, 36, 70, 52},
//            {-98, 37, -96, 36},
//            {59, 38, 57, 48},
//            {15, 39, 80, 81},
//            {-85, 40, -86, -82},
//            {-87, 41, 83, 76},
//            {-89, 42, -91, -79},
//            {-75, 43, -76, -55},
//            {83, 44, -69, -66},
//            {-73, 45, -74, -70},
//            {80, 46, -72, -61},
//            {11, 47, 10, 113},
//            {57, 48, 69, 49},
//            {48, 49, 9, 50},
//            {9, 50, 51, 115},
//            {8, 51, 115, 105},
//            {70, 52, 7, 6},
//            {64, 53, 63, 6},
//            {55, 54, 64, 53},
//            {56, 55, 54, 64},
//            {57, 56, 69, 55},
//            {58, 57, 56, 48},
//            {59, 58, 57, 48},
//            {60, 59, 68, 12},
//            {80, 60, 13, 12},
//            {62, 61, 4, -67},
//            {-2, 62, 61, -123},
//            {53, 63, 71, 62},
//            {54, 64, 53, 52},
//            {56, 65, 9, 64},
//            {58, 66, -114, -113},
//            {60, 67, -116, 12},
//            {59, 68, 78, 11},
//            {56, 69, 76, 75},
//            {53, 70, 52, 74},
//            {63, 71, 86, 5},
//            {-93, 72, 3, 87},
//            {-92, 73, 4, 72},
//            {70, 74, 85, 90},
//            {76, 75, 84, 92},
//            {69, 76, 75, 84},
//            {48, 77, 100, 76},
//            {68, 78, 82, 77},
//            {60, 79, 81, 78},
//            {39, 80, 81, 99},
//            {79, 81, 96, 107},
//            {78, 82, 95, 100},
//            {77, 83, 93, 92},
//            {75, 84, 92, 85},
//            {74, 85, 91, 102},
//            {71, 86, 73, 4},
//            {-107, 87, 2, 1},
//            {89, 88, 104, -67},
//            {86, 89, 88, 104},
//            {74, 90, 89, 88},
//            {85, 91, 102, -55},
//            {84, 92, 101, 91},
//            {100, 93, 92, -66},
//            {100, 94, 106, -66},
//            {82, 95, 100, 94},
//            {81, 96, 95, 94},
//            {15, 97, 109, 108},
//            {80, 98, 99, -58},
//            {98, 99, 107, -58},
//            {95, 100, 93, 106},
//            {92, 101, 91, 105},
//            {91, 102, -55, -53},
//            {72, 103, 87, 117},
//            {88, 104, -67, -54},
//            {101, 105, 116, 102},
//            {100, 106, 114, -66},
//            {111, 107, 106, 17},
//            {98, 108, -58, -44},
//            {97, 109, 108, -60},
//            {126, 110, 111, -58},
//            {110, 111, 112, 107},
//            {111, 112, 113, -45},
//            {47, 113, -47, 114},
//            {-47, 114, -40, -48},
//            {51, 115, 105, -51},
//            {6, 116, 104, -53},
//            {118, 117, 1, 1},
//            {-124, 118, 117, 24},
//            {5, 119, 104, -54},
//            {-122, 120, 119, -53},
//            {-126, 121, 116, 120},
//            {-119, 122, 115, 121},
//            {124, 123, -40, -51},
//            {-128, 124, 123, 114},
//            {112, 125, 113, 124},
//            {127, 126, 110, 111},
//            {14, 127, 126, 110},
//            {-113, -128, 124, 123},
//            {-120, -127, -126, 121},
//            {-121, -126, 121, 120},
//            {-122, -125, 119, -54},
//            {-110, -124, 3, -109},
//            {61, -123, 4, -124},
//            {63, -122, -125, 119},
//            {-111, -121, -126, 121},
//            {-111, -120, -121, -126},
//            {-112, -119, 122, -121},
//            {29, -118, -24, -114},
//            {15, -117, -22, -116},
//            {67, -116, 12, -43},
//            {29, -115, 47, 125},
//            {66, -114, -113, -128},
//            {-114, -113, -128, -119},
//            {65, -112, -119, 122},
//            {54, -111, -120, -121},
//            {61, -110, -124, -109},
//            {-107, -109, -108, 2},
//            {-109, -108, 2, 1},
//            {34, -107, -109, -108},
//            {-105, -106, 34, -107},
//            {-104, -105, -106, 33},
//            {-102, -104, -105, -1},
//            {-101, -103, 32, 63},
//            {-101, -102, -104, 32},
//            {-100, -101, -103, 32},
//            {30, -100, -103, -111},
//            {38, -99, 41, -97},
//            {-99, -98, -97, 36},
//            {-98, -97, -89, 75},
//            {37, -96, -95, 74},
//            {-96, -95, -94, 71},
//            {-95, -94, 35, -92},
//            {35, -93, 72, 3},
//            {-91, -92, 73, -77},
//            {42, -91, -92, 73},
//            {75, -90, 52, 7},
//            {-97, -89, 42, 74},
//            {-97, -88, -89, -80},
//            {-85, -87, 41, 77},
//            {-85, -86, -99, -88},
//            {39, -85, -87, -83},
//            {-85, -84, -83, -72},
//            {-84, -83, -71, 94},
//            {41, -82, -81, 44},
//            {-82, -81, -80, 44},
//            {-88, -80, 85, -75},
//            {-91, -79, -76, 88},
//            {-93, -78, 87, 2},
//            {-92, -77, -78, 103},
//            {-79, -76, -67, -32},
//            {-80, -75, 43, 102},
//            {-73, -74, -70, -69},
//            {-81, -73, -74, -70},
//            {46, -72, -71, -63},
//            {-72, -71, -64, -65},
//            {-74, -70, -69, -75},
//            {44, -69, -66, -68},
//            {92, -68, 102, -55},
//            {4, -67, 117, -32},
//            {92, -66, 16, -52},
//            {17, -65, -66, 16},
//            {44, -64, -65, -66},
//            {-62, -63, -57, -65},
//            {-61, -62, -63, -57},
//            {98, -61, -62, -63},
//            {108, -60, -58, -59},
//            {-58, -59, -57, -42},
//            {108, -58, -44, -42},
//            {-63, -57, -42, -56},
//            {114, -56, -41, -49},
//            {102, -55, -53, -54},
//            {119, -54, -32, 24},
//            {119, -53, -32, 24},
//            {22, -52, -53, 23},
//            {115, -51, -55, -52},
//            {-36, -50, -52, -34},
//            {-51, -49, -52, -53},
//            {-40, -48, -49, -52},
//            {124, -47, 114, -40},
//            {-45, -46, -56, -41},
//            {125, -45, -46, -56},
//            {-58, -44, -46, -42},
//            {-23, -43, 125, -45},
//            {-46, -42, -56, -41},
//            {-48, -41, -50, -52},
//            {114, -40, -48, -49},
//            {123, -39, -40, -38},
//            {-39, -38, -36, -50},
//            {-28, -37, -36, 22},
//            {-37, -36, 22, -35},
//            {-30, -35, -34, 23},
//            {-35, -34, 23, 1},
//            {-31, -33, 23, 1},
//            {118, -32, 24, 1},
//            {-30, -31, -33, 23},
//            {-16, -30, -31, -33},
//            {26, -29, 22, -53},
//            {-26, -28, -37, -36},
//            {-17, -27, -30, -31},
//            {-21, -26, -39, -28},
//            {-24, -25, 20, -39},
//            {-118, -24, -25, 20},
//            {-116, -23, -43, -45},
//            {-117, -22, -23, -43},
//            {28, -21, -26, -39},
//            {-7, -20, -26, -28},
//            {-8, -19, -17, -16},
//            {-19, -18, -29, -37},
//            {-19, -17, -16, -31},
//            {-17, -16, -31, -33},
//            {-14, -15, -13, -12},
//            {-10, -14, -12, 24},
//            {-14, -13, -12, 24},
//            {-11, -12, 24, 1},
//            {-1, -11, -109, 118},
//            {-4, -10, -14, -15},
//            {-4, -9, -10, 25},
//            {-7, -8, -111, 26},
//            {30, -7, -8, -111},
//            {58, -6, -114, -113},
//            {-100, -5, -4, 26},
//            {-5, -4, -10, -14},
//            {-104, -3, -1, -11},
//            {32, -2, 62, 61},
//            {-3, -1, -11, -12},
//    };
//    /**
//     * Bytes that correspond to palette indices to use when shading an Aurora-palette model and ramps should consider
//     * the aesthetic warmth or coolness of a color, brightening to a warmer color and darkening to a cooler one.
//     * The color in index 1 of each 4-element sub-array is the "dimmer" color and the one that will match the voxel
//     * color used in a model. The color at index 0 is "bright", index 2 is "dim", and index 3 is "dark".
//     * To visualize this, <a href="https://i.imgur.com/zeJpyQ4.png">use this image</a>, with the first 32 items in
//     * AURORA_WARMTH_RAMPS corresponding to the first column from top to bottom, the next 32 items in the second column, etc.
//     * Color values as RGBA8888 ints to use when shading an Aurora-palette model.
//     */
//    public static final int[][] AURORA_WARMTH_RAMP_VALUES = {
//            {0x00000000, 0x00000000, 0x00000000, 0x00000000},
//            {0x131313FF, 0x010101FF, 0x010101FF, 0x010101FF},
//            {0x191E0FFF, 0x131313FF, 0x010101FF, 0x010101FF},
//            {0x1E2D23FF, 0x252525FF, 0x191E0FFF, 0x131313FF},
//            {0x414123FF, 0x373737FF, 0x1E2D23FF, 0x1F1F3BFF},
//            {0x73413CFF, 0x494949FF, 0x3B3B57FF, 0x373737FF},
//            {0x8E5555FF, 0x5B5B5BFF, 0x494949FF, 0x3B3B57FF},
//            {0x7E6E60FF, 0x6E6E6EFF, 0x5B5B5BFF, 0x3B5773FF},
//            {0xAB7373FF, 0x808080FF, 0x6E8287FF, 0x6E6E6EFF},
//            {0xA6A090FF, 0x929292FF, 0x7E9494FF, 0x808080FF},
//            {0xCBAA89FF, 0xA4A4A4FF, 0xA6A090FF, 0x929292FF},
//            {0xE3ABABFF, 0xB6B6B6FF, 0xA4A4A4FF, 0x8FABC7FF},
//            {0xE3C7E3FF, 0xC9C9C9FF, 0xB6B6B6FF, 0xA8B9DCFF},
//            {0xF5E1D2FF, 0xDBDBDBFF, 0xBED2F0FF, 0xC9C9C9FF},
//            {0xFFFFFFFF, 0xEDEDEDFF, 0xE3E3FFFF, 0xDBDBDBFF},
//            {0xFFFFFFFF, 0xFFFFFFFF, 0xE1F8FAFF, 0xC7F1F1FF},
//            {0x3B7373FF, 0x007F7FFF, 0x055A5CFF, 0x004A9CFF},
//            {0x64ABABFF, 0x3FBFBFFF, 0x06C491FF, 0x109CDEFF},
//            {0x55E6FFFF, 0x00FFFFFF, 0x08DED5FF, 0x00BFFFFF},
//            {0xE1F8FAFF, 0xBFFFFFFF, 0x91EBFFFF, 0x55E6FFFF},
//            {0xB991FFFF, 0x8181FFFF, 0x786EF0FF, 0x4B7DC8FF},
//            {0x7F00FFFF, 0x0000FFFF, 0x0010BDFF, 0x0000FFFF},
//            {0x8732D2FF, 0x3F3FBFFF, 0x0F377DFF, 0x00007FFF},
//            {0x231094FF, 0x00007FFF, 0x010101FF, 0x010101FF},
//            {0x320A46FF, 0x0F0F50FF, 0x010101FF, 0x010101FF},
//            {0xA01982FF, 0x7F007FFF, 0x641464FF, 0x410062FF},
//            {0xC35A91FF, 0xBF3FBFFF, 0x8732D2FF, 0x3F3FBFFF},
//            {0xDA20E0FF, 0xF500F5FF, 0xBD10C5FF, 0x8C14BEFF},
//            {0xFAA0B9FF, 0xFD81FFFF, 0xE673FFFF, 0xBD62FFFF},
//            {0xF8D2DAFF, 0xFFC0CBFF, 0xE1B9D2FF, 0xBCAFC0FF},
//            {0xEBAA8CFF, 0xFF8181FF, 0xD08A74FF, 0xC07872FF},
//            {0xFF3C0AFF, 0xFF0000FF, 0xA5140AFF, 0x911437FF},
//            {0xD5524AFF, 0xBF3F3FFF, 0x98344DFF, 0x73413CFF},
//            {0xA5140AFF, 0x7F0000FF, 0x551414FF, 0x280A1EFF},
//            {0x621800FF, 0x551414FF, 0x401811FF, 0x280A1EFF},
//            {0xA04B05FF, 0x7F3F00FF, 0x5F3214FF, 0x3B2D1FFF},
//            {0xD79B0FFF, 0xBF7F3FFF, 0x8F7357FF, 0x7E6E60FF},
//            {0xFFA53CFF, 0xFF7F00FF, 0xDA6E0AFF, 0xBF7F3FFF},
//            {0xF6C8AFFF, 0xFFBF81FF, 0xEBAA8CFF, 0xCBAA89FF},
//            {0xFFFFFFFF, 0xFFFFBFFF, 0xEDEDC7FF, 0xC7E3ABFF},
//            {0xFFEA4AFF, 0xFFFF00FF, 0xFFD510FF, 0x96DC19FF},
//            {0xE6D55AFF, 0xBFBF3FFF, 0x8EBE55FF, 0xA2A255FF},
//            {0xAC9400FF, 0x7F7F00FF, 0x626200FF, 0x3C6E14FF},
//            {0x149605FF, 0x007F00FF, 0x0C5C0CFF, 0x055A5CFF},
//            {0x8EBE55FF, 0x3FBF3FFF, 0x05B450FF, 0x129880FF},
//            {0x14E60AFF, 0x00FF00FF, 0x0AD70AFF, 0x00C514FF},
//            {0xEDEDC7FF, 0xAFFFAFFF, 0x7DFF73FF, 0x6AFFCDFF},
//            {0xB6B6B6FF, 0xBCAFC0FF, 0xA4A4A4FF, 0x8FABC7FF},
//            {0xEBAA8CFF, 0xCBAA89FF, 0xC49E73FF, 0xA6A090FF},
//            {0xCBAA89FF, 0xA6A090FF, 0x929292FF, 0x7E9494FF},
//            {0x929292FF, 0x7E9494FF, 0x6E8287FF, 0x57738FFF},
//            {0x808080FF, 0x6E8287FF, 0x57738FFF, 0x3B7373FF},
//            {0x8F7357FF, 0x7E6E60FF, 0x6E6E6EFF, 0x5B5B5BFF},
//            {0xAB7373FF, 0xA0695FFF, 0x8E5555FF, 0x5B5B5BFF},
//            {0xD08A74FF, 0xC07872FF, 0xAB7373FF, 0xA0695FFF},
//            {0xE19B7DFF, 0xD08A74FF, 0xC07872FF, 0xAB7373FF},
//            {0xEBAA8CFF, 0xE19B7DFF, 0xC49E73FF, 0xD08A74FF},
//            {0xF5B99BFF, 0xEBAA8CFF, 0xE19B7DFF, 0xCBAA89FF},
//            {0xF6C8AFFF, 0xF5B99BFF, 0xEBAA8CFF, 0xCBAA89FF},
//            {0xF5E1D2FF, 0xF6C8AFFF, 0xE3C7ABFF, 0xC9C9C9FF},
//            {0xEDEDC7FF, 0xF5E1D2FF, 0xDBDBDBFF, 0xC9C9C9FF},
//            {0x73413CFF, 0x573B3BFF, 0x373737FF, 0x123832FF},
//            {0x98344DFF, 0x73413CFF, 0x573B3BFF, 0x463246FF},
//            {0xA0695FFF, 0x8E5555FF, 0x73573BFF, 0x73413CFF},
//            {0xC07872FF, 0xAB7373FF, 0xA0695FFF, 0x7E6E60FF},
//            {0xE19B7DFF, 0xC78F8FFF, 0x929292FF, 0xAB7373FF},
//            {0xF5B99BFF, 0xE3ABABFF, 0xD7A0BEFF, 0xC78FB9FF},
//            {0xF5E1D2FF, 0xF8D2DAFF, 0xE3C7E3FF, 0xC9C9C9FF},
//            {0xF6C8AFFF, 0xE3C7ABFF, 0xC7C78FFF, 0xB6B6B6FF},
//            {0xE19B7DFF, 0xC49E73FF, 0xA2A255FF, 0x8F8F57FF},
//            {0xA0695FFF, 0x8F7357FF, 0x7E6E60FF, 0x73733BFF},
//            {0x8E5555FF, 0x73573BFF, 0x465032FF, 0x494949FF},
//            {0x5F3214FF, 0x3B2D1FFF, 0x252525FF, 0x191E0FFF},
//            {0x53500AFF, 0x414123FF, 0x373737FF, 0x3B2D1FFF},
//            {0x8F7357FF, 0x73733BFF, 0x587D3EFF, 0x506450FF},
//            {0xA2A255FF, 0x8F8F57FF, 0x738F57FF, 0x578F57FF},
//            {0xC49E73FF, 0xA2A255FF, 0x8F8F57FF, 0x738F57FF},
//            {0xCBAA89FF, 0xB5B572FF, 0x87B48EFF, 0xA2A255FF},
//            {0xE3C7ABFF, 0xC7C78FFF, 0xABC78FFF, 0xB5B572FF},
//            {0xF5E1D2FF, 0xDADAABFF, 0xC7E3ABFF, 0xC7C78FFF},
//            {0xFFFFBFFF, 0xEDEDC7FF, 0xC7E3ABFF, 0xABE3C5FF},
//            {0xDADAABFF, 0xC7E3ABFF, 0xA2D8A2FF, 0x8FC7C7FF},
//            {0xC7C78FFF, 0xABC78FFF, 0x8FC78FFF, 0x87B48EFF},
//            {0xB5B572FF, 0x8EBE55FF, 0x73AB73FF, 0x578F57FF},
//            {0x8F8F57FF, 0x738F57FF, 0x578F57FF, 0x587D3EFF},
//            {0x73733BFF, 0x587D3EFF, 0x3B7349FF, 0x0F6946FF},
//            {0x73573BFF, 0x465032FF, 0x414123FF, 0x373737FF},
//            {0x401811FF, 0x191E0FFF, 0x131313FF, 0x010101FF},
//            {0x3B573BFF, 0x235037FF, 0x234146FF, 0x123832FF},
//            {0x465032FF, 0x3B573BFF, 0x235037FF, 0x234146FF},
//            {0x73733BFF, 0x506450FF, 0x3B573BFF, 0x235037FF},
//            {0x587D3EFF, 0x3B7349FF, 0x0F6946FF, 0x055A5CFF},
//            {0x738F57FF, 0x578F57FF, 0x507D5FFF, 0x3B7349FF},
//            {0x87B48EFF, 0x73AB73FF, 0x578F57FF, 0x129880FF},
//            {0x87B48EFF, 0x64C082FF, 0x64ABABFF, 0x129880FF},
//            {0xABC78FFF, 0x8FC78FFF, 0x87B48EFF, 0x64C082FF},
//            {0xC7E3ABFF, 0xA2D8A2FF, 0x8FC78FFF, 0x64C082FF},
//            {0xFFFFFFFF, 0xE1F8FAFF, 0xC7F1F1FF, 0xABE3E3FF},
//            {0xEDEDC7FF, 0xB4EECAFF, 0xABE3C5FF, 0x7DD7F0FF},
//            {0xB4EECAFF, 0xABE3C5FF, 0x8FC7C7FF, 0x7DD7F0FF},
//            {0x8FC78FFF, 0x87B48EFF, 0x73AB73FF, 0x64ABABFF},
//            {0x578F57FF, 0x507D5FFF, 0x3B7349FF, 0x3B7373FF},
//            {0x3B7349FF, 0x0F6946FF, 0x055A5CFF, 0x0F377DFF},
//            {0x3B2D1FFF, 0x1E2D23FF, 0x191E0FFF, 0x0F192DFF},
//            {0x235037FF, 0x234146FF, 0x123832FF, 0x162C52FF},
//            {0x507D5FFF, 0x3B7373FF, 0x3B5773FF, 0x0F6946FF},
//            {0x87B48EFF, 0x64ABABFF, 0x578FC7FF, 0x129880FF},
//            {0xABC7E3FF, 0x8FC7C7FF, 0x64ABABFF, 0x3FBFBFFF},
//            {0xB4EECAFF, 0xABE3E3FF, 0x7DD7F0FF, 0x5AC5FFFF},
//            {0xE1F8FAFF, 0xC7F1F1FF, 0xABE3E3FF, 0x91EBFFFF},
//            {0xD0DAF8FF, 0xBED2F0FF, 0xABC7E3FF, 0x7DD7F0FF},
//            {0xBED2F0FF, 0xABC7E3FF, 0xA8B9DCFF, 0x8FC7C7FF},
//            {0xABC7E3FF, 0xA8B9DCFF, 0x8FABC7FF, 0x90B0FFFF},
//            {0xBCAFC0FF, 0x8FABC7FF, 0x699DC3FF, 0x578FC7FF},
//            {0x699DC3FF, 0x578FC7FF, 0x4B7DC8FF, 0x2378DCFF},
//            {0x6E8287FF, 0x57738FFF, 0x3B7373FF, 0x326496FF},
//            {0x5B5B5BFF, 0x3B5773FF, 0x234146FF, 0x0F377DFF},
//            {0x1F1F3BFF, 0x0F192DFF, 0x010101FF, 0x010101FF},
//            {0x3C233CFF, 0x1F1F3BFF, 0x0F192DFF, 0x0F0F50FF},
//            {0x494949FF, 0x3B3B57FF, 0x234146FF, 0x162C52FF},
//            {0x724072FF, 0x494973FF, 0x3B3B57FF, 0x0F377DFF},
//            {0x73578FFF, 0x57578FFF, 0x3B5773FF, 0x494973FF},
//            {0xAB73ABFF, 0x736EAAFF, 0x57738FFF, 0x57578FFF},
//            {0x8F8FC7FF, 0x7676CAFF, 0x4B7DC8FF, 0x326496FF},
//            {0xAB8FC7FF, 0x8F8FC7FF, 0x7676CAFF, 0x578FC7FF},
//            {0xA8B9DCFF, 0xABABE3FF, 0x8FABC7FF, 0x8F8FC7FF},
//            {0xE3E3FFFF, 0xD0DAF8FF, 0xBED2F0FF, 0xABC7E3FF},
//            {0xEDEDEDFF, 0xE3E3FFFF, 0xD0DAF8FF, 0xBED2F0FF},
//            {0xC78FB9FF, 0xAB8FC7FF, 0x8F8FC7FF, 0x7676CAFF},
//            {0xAB57ABFF, 0x8F57C7FF, 0x73578FFF, 0x57578FFF},
//            {0x8F578FFF, 0x73578FFF, 0x57578FFF, 0x494973FF},
//            {0x724072FF, 0x573B73FF, 0x3B3B57FF, 0x162C52FF},
//            {0x4B2837FF, 0x3C233CFF, 0x252525FF, 0x321623FF},
//            {0x573B3BFF, 0x463246FF, 0x373737FF, 0x3C233CFF},
//            {0x8E5555FF, 0x724072FF, 0x573B73FF, 0x3B3B57FF},
//            {0xC35A91FF, 0x8F578FFF, 0x73578FFF, 0x57578FFF},
//            {0xC35A91FF, 0xAB57ABFF, 0x8F578FFF, 0x73578FFF},
//            {0xC87DA0FF, 0xAB73ABFF, 0x736EAAFF, 0x8F578FFF},
//            {0xFFC0CBFF, 0xEBACE1FF, 0xD7A5FFFF, 0xD7A0BEFF},
//            {0xFFFFFFFF, 0xFFDCF5FF, 0xF8C6FCFF, 0xE3C7E3FF},
//            {0xF8D2DAFF, 0xE3C7E3FF, 0xC9C9C9FF, 0xBEB9FAFF},
//            {0xFFC0CBFF, 0xE1B9D2FF, 0xBCAFC0FF, 0xABABE3FF},
//            {0xE3ABABFF, 0xD7A0BEFF, 0xC78FB9FF, 0xAB8FC7FF},
//            {0xD7A0BEFF, 0xC78FB9FF, 0xAB8FC7FF, 0xAB73ABFF},
//            {0xC78F8FFF, 0xC87DA0FF, 0xAB73ABFF, 0x736EAAFF},
//            {0xC07872FF, 0xC35A91FF, 0xAB57ABFF, 0x8F578FFF},
//            {0x573B3BFF, 0x4B2837FF, 0x3C233CFF, 0x321623FF},
//            {0x401811FF, 0x321623FF, 0x280A1EFF, 0x131313FF},
//            {0x321623FF, 0x280A1EFF, 0x131313FF, 0x010101FF},
//            {0x551414FF, 0x401811FF, 0x321623FF, 0x280A1EFF},
//            {0xA5140AFF, 0x621800FF, 0x551414FF, 0x401811FF},
//            {0xDA2010FF, 0xA5140AFF, 0x621800FF, 0x7F0000FF},
//            {0xFF3C0AFF, 0xDA2010FF, 0xA5140AFF, 0x911437FF},
//            {0xF55A32FF, 0xD5524AFF, 0xBF3F3FFF, 0x8E5555FF},
//            {0xF55A32FF, 0xFF3C0AFF, 0xDA2010FF, 0xBF3F3FFF},
//            {0xFF6262FF, 0xF55A32FF, 0xD5524AFF, 0xBF3F3FFF},
//            {0xFF8181FF, 0xFF6262FF, 0xD5524AFF, 0xC35A91FF},
//            {0xFFBF81FF, 0xF6BD31FF, 0xBFBF3FFF, 0xD79B0FFF},
//            {0xF6BD31FF, 0xFFA53CFF, 0xD79B0FFF, 0xBF7F3FFF},
//            {0xFFA53CFF, 0xD79B0FFF, 0xAC9400FF, 0x8F8F57FF},
//            {0xFF7F00FF, 0xDA6E0AFF, 0xB45A00FF, 0x73733BFF},
//            {0xDA6E0AFF, 0xB45A00FF, 0xA04B05FF, 0x73573BFF},
//            {0xB45A00FF, 0xA04B05FF, 0x7F3F00FF, 0x53500AFF},
//            {0x7F3F00FF, 0x5F3214FF, 0x3B2D1FFF, 0x252525FF},
//            {0x626200FF, 0x53500AFF, 0x414123FF, 0x204608FF},
//            {0x7F7F00FF, 0x626200FF, 0x53500AFF, 0x414123FF},
//            {0x8F8F57FF, 0x8C805AFF, 0x7E6E60FF, 0x6E6E6EFF},
//            {0xD79B0FFF, 0xAC9400FF, 0x7F7F00FF, 0x73733BFF},
//            {0xD79B0FFF, 0xB1B10AFF, 0xAC9400FF, 0x6AA805FF},
//            {0xFFEA4AFF, 0xE6D55AFF, 0xBFBF3FFF, 0xB5B572FF},
//            {0xFFEA4AFF, 0xFFD510FF, 0xF6BD31FF, 0xB1B10AFF},
//            {0xFFFFBFFF, 0xFFEA4AFF, 0xE6D55AFF, 0x9BF046FF},
//            {0xFFEA4AFF, 0xC8FF41FF, 0x9BF046FF, 0x7DFF73FF},
//            {0xC8FF41FF, 0x9BF046FF, 0x4BF05AFF, 0x64C082FF},
//            {0xBFBF3FFF, 0x96DC19FF, 0x73C805FF, 0x3FBF3FFF},
//            {0x96DC19FF, 0x73C805FF, 0x6AA805FF, 0x3FBF3FFF},
//            {0xB1B10AFF, 0x6AA805FF, 0x587D3EFF, 0x149605FF},
//            {0x626200FF, 0x3C6E14FF, 0x0C5C0CFF, 0x235037FF},
//            {0x5F3214FF, 0x283405FF, 0x191E0FFF, 0x131313FF},
//            {0x53500AFF, 0x204608FF, 0x283405FF, 0x1E2D23FF},
//            {0x3C6E14FF, 0x0C5C0CFF, 0x123832FF, 0x0C2148FF},
//            {0x6AA805FF, 0x149605FF, 0x007F00FF, 0x0F6946FF},
//            {0x14E60AFF, 0x0AD70AFF, 0x00C514FF, 0x05B450FF},
//            {0x73C805FF, 0x14E60AFF, 0x0AD70AFF, 0x00C514FF},
//            {0xAFFFAFFF, 0x7DFF73FF, 0x4BF05AFF, 0x2DEBA8FF},
//            {0x7DFF73FF, 0x4BF05AFF, 0x00DE6AFF, 0x06C491FF},
//            {0x0AD70AFF, 0x00C514FF, 0x05B450FF, 0x149605FF},
//            {0x3FBF3FFF, 0x05B450FF, 0x129880FF, 0x1C8C4EFF},
//            {0x578F57FF, 0x1C8C4EFF, 0x0F6946FF, 0x055A5CFF},
//            {0x373737FF, 0x123832FF, 0x0F192DFF, 0x0C2148FF},
//            {0x578F57FF, 0x129880FF, 0x007F7FFF, 0x004A9CFF},
//            {0x3FBFBFFF, 0x06C491FF, 0x129880FF, 0x007F7FFF},
//            {0x3FBF3FFF, 0x00DE6AFF, 0x06C491FF, 0x129880FF},
//            {0x3CFEA5FF, 0x2DEBA8FF, 0x08DED5FF, 0x06C491FF},
//            {0x6AFFCDFF, 0x3CFEA5FF, 0x2DEBA8FF, 0x08DED5FF},
//            {0xB4EECAFF, 0x6AFFCDFF, 0x3CFEA5FF, 0x2DEBA8FF},
//            {0xABE3E3FF, 0x91EBFFFF, 0x7DD7F0FF, 0x55E6FFFF},
//            {0x7DD7F0FF, 0x55E6FFFF, 0x08DED5FF, 0x00BFFFFF},
//            {0xABE3E3FF, 0x7DD7F0FF, 0x5AC5FFFF, 0x00BFFFFF},
//            {0x2DEBA8FF, 0x08DED5FF, 0x00BFFFFF, 0x109CDEFF},
//            {0x578FC7FF, 0x109CDEFF, 0x007FFFFF, 0x186ABDFF},
//            {0x0F6946FF, 0x055A5CFF, 0x0F377DFF, 0x162C52FF},
//            {0x3B3B57FF, 0x162C52FF, 0x0C2148FF, 0x0F0F50FF},
//            {0x3B3B57FF, 0x0F377DFF, 0x0C2148FF, 0x0F0F50FF},
//            {0x3F3FBFFF, 0x004A9CFF, 0x0F377DFF, 0x00007FFF},
//            {0x57738FFF, 0x326496FF, 0x055A5CFF, 0x004A9CFF},
//            {0x3C3CF5FF, 0x0052F6FF, 0x004A9CFF, 0x0010BDFF},
//            {0x326496FF, 0x186ABDFF, 0x004A9CFF, 0x0F377DFF},
//            {0x4B7DC8FF, 0x2378DCFF, 0x186ABDFF, 0x004A9CFF},
//            {0x8F8FC7FF, 0x699DC3FF, 0x578FC7FF, 0x4B7DC8FF},
//            {0x90B0FFFF, 0x4AA4FFFF, 0x109CDEFF, 0x007FFFFF},
//            {0xABABE3FF, 0x90B0FFFF, 0x4AA4FFFF, 0x109CDEFF},
//            {0x7DD7F0FF, 0x5AC5FFFF, 0x4AA4FFFF, 0x00BFFFFF},
//            {0xD7C3FAFF, 0xBEB9FAFF, 0xABABE3FF, 0x90B0FFFF},
//            {0x4AA4FFFF, 0x00BFFFFF, 0x109CDEFF, 0x007FFFFF},
//            {0x2378DCFF, 0x007FFFFF, 0x0052F6FF, 0x004A9CFF},
//            {0x578FC7FF, 0x4B7DC8FF, 0x2378DCFF, 0x186ABDFF},
//            {0x7676CAFF, 0x786EF0FF, 0x4B7DC8FF, 0x4A5AFFFF},
//            {0x786EF0FF, 0x4A5AFFFF, 0x3C3CF5FF, 0x0052F6FF},
//            {0x9C41FFFF, 0x6241F6FF, 0x3C3CF5FF, 0x3F3FBFFF},
//            {0x6241F6FF, 0x3C3CF5FF, 0x3F3FBFFF, 0x101CDAFF},
//            {0x6010D0FF, 0x101CDAFF, 0x0010BDFF, 0x00007FFF},
//            {0x101CDAFF, 0x0010BDFF, 0x00007FFF, 0x010101FF},
//            {0x5010B0FF, 0x231094FF, 0x00007FFF, 0x010101FF},
//            {0x1F1F3BFF, 0x0C2148FF, 0x0F0F50FF, 0x010101FF},
//            {0x6010D0FF, 0x5010B0FF, 0x231094FF, 0x00007FFF},
//            {0x8C14BEFF, 0x6010D0FF, 0x5010B0FF, 0x231094FF},
//            {0xBF3FBFFF, 0x8732D2FF, 0x3F3FBFFF, 0x0F377DFF},
//            {0xBD62FFFF, 0x9C41FFFF, 0x6241F6FF, 0x3C3CF5FF},
//            {0xBD10C5FF, 0x7F00FFFF, 0x6010D0FF, 0x5010B0FF},
//            {0xE673FFFF, 0xBD62FFFF, 0x786EF0FF, 0x9C41FFFF},
//            {0xD7A5FFFF, 0xB991FFFF, 0x8181FFFF, 0x786EF0FF},
//            {0xEBACE1FF, 0xD7A5FFFF, 0xB991FFFF, 0x8181FFFF},
//            {0xE3C7E3FF, 0xD7C3FAFF, 0xBEB9FAFF, 0x90B0FFFF},
//            {0xFFDCF5FF, 0xF8C6FCFF, 0xD7C3FAFF, 0xBEB9FAFF},
//            {0xFD81FFFF, 0xE673FFFF, 0xBD62FFFF, 0x786EF0FF},
//            {0xFF6AC5FF, 0xFF52FFFF, 0xBD62FFFF, 0x9C41FFFF},
//            {0xFF50BFFF, 0xDA20E0FF, 0xBD10C5FF, 0x8C14BEFF},
//            {0xDA20E0FF, 0xBD29FFFF, 0x8732D2FF, 0x6241F6FF},
//            {0xDA20E0FF, 0xBD10C5FF, 0x8C14BEFF, 0x5010B0FF},
//            {0xBD10C5FF, 0x8C14BEFF, 0x5010B0FF, 0x231094FF},
//            {0x641464FF, 0x5A187BFF, 0x410062FF, 0x320A46FF},
//            {0xA01982FF, 0x641464FF, 0x320A46FF, 0x0F0F50FF},
//            {0x641464FF, 0x410062FF, 0x320A46FF, 0x0F0F50FF},
//            {0x551937FF, 0x320A46FF, 0x0F0F50FF, 0x010101FF},
//            {0x911437FF, 0x551937FF, 0x321623FF, 0x1F1F3BFF},
//            {0xE61E78FF, 0xA01982FF, 0x641464FF, 0x5A187BFF},
//            {0xE61E78FF, 0xC80078FF, 0xA01982FF, 0x7F007FFF},
//            {0xFF6AC5FF, 0xFF50BFFF, 0xC35A91FF, 0xBF3FBFFF},
//            {0xFF8181FF, 0xFF6AC5FF, 0xFF50BFFF, 0xC35A91FF},
//            {0xF5B99BFF, 0xFAA0B9FF, 0xD7A0BEFF, 0xC78FB9FF},
//            {0xFF6262FF, 0xFC3A8CFF, 0xE61E78FF, 0xBF3FBFFF},
//            {0xFC3A8CFF, 0xE61E78FF, 0xA01982FF, 0x641464FF},
//            {0xDA2010FF, 0xBD1039FF, 0x911437FF, 0x551937FF},
//            {0xBF3F3FFF, 0x98344DFF, 0x73413CFF, 0x573B3BFF},
//            {0xBD1039FF, 0x911437FF, 0x551937FF, 0x320A46FF},
//    };

//    public static final byte[][] RINSED_RAMPS = {
//            { 0, 0, 0, 0 },
//            { 23, 23, 1, -65 },
//            { 23, 23, 2, -65 },
//            { 23, 23, 3, -65 },
//            { 23, 23, 4, -65 },
//            { 23, 23, 5, -65 },
//            { 23, 23, 6, -65 },
//            { 23, 23, 7, -65 },
//            { 23, 23, 8, -65 },
//            { 23, 23, 9, -65 },
//            { 23, 23, 10, -65 },
//            { 23, 23, 11, -65 },
//            { 23, 23, 12, -65 },
//            { 23, 23, 13, -65 },
//            { 23, 23, 14, -65 },
//            { 23, 23, 15, -65 },
//            { -48, -8, 16, 16 },
//            { 18, 91, 17, -32 },
//            { 36, 69, 18, 91 },
//            { -42, -59, 19, -108 },
//            { -122, 71, 20, 19 },
//            { -57, 38, 21, -59 },
//            { 23, 55, 22, -122 },
//            { 23, 23, 23, 55 },
//            { 27, 25, 24, 33 },
//            { -12, -13, 25, 24 },
//            { 70, 50, 26, 68 },
//            { 29, 101, 27, -30 },
//            { -27, 29, 28, -29 },
//            { -34, -27, 29, 28 },
//            { -25, 31, 30, 29 },
//            { 23, 55, 31, -27 },
//            { -15, 90, 32, 88 },
//            { 93, 34, 33, 90 },
//            { 50, 93, 34, -15 },
//            { -59, 36, 35, -61 },
//            { -58, 37, 36, 69 },
//            { -57, 38, 37, -43 },
//            { -65, -49, 38, 71 },
//            { 23, 55, 39, -41 },
//            { 93, 34, 40, 80 },
//            { 27, 26, 41, 33 },
//            { 70, 50, 42, 41 },
//            { 29, 101, 43, 26 },
//            { -27, 29, 44, 27 },
//            { -34, -27, 45, 28 },
//            { -25, 31, 46, 29 },
//            { 23, -25, 47, 38 },
//            { 93, 18, 48, 80 },
//            { 50, 93, 49, 67 },
//            { 37, 70, 50, 93 },
//            { 38, 21, 51, 94 },
//            { -25, 31, 52, 29 },
//            { 23, -25, 53, -10 },
//            { 23, 55, 54, 86 },
//            { 23, 23, 55, -26 },
//            { 48, 80, 56, -16 },
//            { 93, 34, 57, -7 },
//            { 27, 26, 58, 33 },
//            { -11, 43, 59, 25 },
//            { -10, 29, 60, 27 },
//            { 31, 46, 61, 28 },
//            { 55, -33, 62, 61 },
//            { 23, 23, 63, 62 },
//            { 66, 56, 64, 88 },
//            { -15, 90, 65, -16 },
//            { 34, 67, 66, 32 },
//            { 93, -14, 67, 17 },
//            { 50, 93, 68, -31 },
//            { -59, 36, 69, -61 },
//            { -58, 37, 70, -60 },
//            { 39, -41, 71, -59 },
//            { 98, 57, 72, 64 },
//            { 25, 98, 73, 72 },
//            { 27, 42, 74, 57 },
//            { -11, 60, 75, -5 },
//            { -10, -11, 76, 59 },
//            { -18, 61, 77, -4 },
//            { -17, 62, 78, -11 },
//            { 23, -17, 79, 78 },
//            { 34, 67, 80, -7 },
//            { 50, 93, 81, 80 },
//            { 70, 50, 82, 34 },
//            { 21, -11, 83, -13 },
//            { 38, 21, 84, 36 },
//            { -33, 86, 85, -11 },
//            { 23, -25, 86, -10 },
//            { 23, 55, 87, 86 },
//            { -32, 89, 88, 16 },
//            { 91, 17, 89, 88 },
//            { 67, -15, 90, -32 },
//            { -61, 18, 91, 17 },
//            { 35, 93, 92, -15 },
//            { 70, 50, 93, -14 },
//            { 21, 51, 94, 93 },
//            { 38, 21, 95, 36 },
//            { 90, 56, 96, 88 },
//            { 92, 48, 97, 56 },
//            { 27, 99, 98, 33 },
//            { -12, -13, 99, 81 },
//            { 29, 101, 100, 99 },
//            { -27, 29, 101, 94 },
//            { -33, 86, 102, 51 },
//            { 23, -17, 103, -27 },
//            { 33, 97, 104, 112 },
//            { 34, 48, 105, 72 },
//            { 43, 42, 106, 57 },
//            { 108, 83, 107, 99 },
//            { -10, 29, 108, 43 },
//            { 31, -18, 109, 77 },
//            { -25, 31, 110, 61 },
//            { 23, -1, 111, 30 },
//            { 90, 32, 112, 16 },
//            { 106, 120, 113, 104 },
//            { 99, 106, 114, 113 },
//            { 116, 99, 115, 73 },
//            { -11, 43, 116, -5 },
//            { -10, 29, 117, 59 },
//            { -25, 31, 118, -19 },
//            { 23, 55, 119, 79 },
//            { 99, 121, 120, 97 },
//            { 100, 82, 121, 120 },
//            { -125, 83, 122, 121 },
//            { -124, -125, 123, -119 },
//            { 126, 52, 124, -126 },
//            { 127, 103, 125, -124 },
//            { -113, 22, 126, -123 },
//            { 23, 23, 127, -97 },
//            { -119, -108, -128, -110 },
//            { -125, -126, -127, -128 },
//            { 21, 95, -126, -119 },
//            { 38, 21, -125, 94 },
//            { 31, 52, -124, 36 },
//            { -73, -74, -123, -106 },
//            { -65, -49, -122, 71 },
//            { 23, -65, -121, -51 },
//            { -119, 99, -120, 105 },
//            { -118, 19, -119, -128 },
//            { -123, -124, -118, -127 },
//            { -121, -98, -117, -101 },
//            { -113, -97, -116, -124 },
//            { -113, 22, -115, -105 },
//            { 23, 23, -114, -74 },
//            { 23, 23, -113, -121 },
//            { 17, -48, -112, 16 },
//            { -110, 91, -111, -63 },
//            { -61, 18, -110, -104 },
//            { -44, -61, -109, 91 },
//            { -43, -60, -108, 18 },
//            { 37, 20, -107, 35 },
//            { -122, -105, -106, -107 },
//            { -73, -74, -105, -106 },
//            { -103, -109, -104, -63 },
//            { 70, 19, -103, -109 },
//            { 37, 20, -102, -108 },
//            { -122, -105, -101, 19 },
//            { -49, -122, -100, 20 },
//            { -73, -74, -99, 37 },
//            { -65, -121, -98, 71 },
//            { 23, -113, -97, -58 },
//            { -86, -95, -96, -79 },
//            { -93, -94, -95, -96 },
//            { -92, -93, -94, -95 },
//            { -90, -91, -93, -94 },
//            { -81, -83, -92, -76 },
//            { 23, -89, -91, -92 },
//            { 23, -65, -90, -75 },
//            { 23, 23, -89, -50 },
//            { -46, -62, -88, -80 },
//            { -69, -77, -87, -71 },
//            { -52, -76, -86, -70 },
//            { -90, -83, -85, -69 },
//            { -82, -122, -84, -76 },
//            { 23, -89, -83, -84 },
//            { 23, -65, -82, -51 },
//            { 23, 23, -81, -50 },
//            { -72, -88, -80, -64 },
//            { -87, -71, -79, -88 },
//            { -54, -55, -78, -79 },
//            { -52, -53, -77, -87 },
//            { -51, -52, -76, -54 },
//            { -73, -74, -75, -76 },
//            { 23, -73, -74, -58 },
//            { 23, 23, -73, -74 },
//            { -78, -46, -72, -80 },
//            { -54, -55, -71, -79 },
//            { -53, -54, -70, -71 },
//            { -67, -68, -69, -70 },
//            { -90, -67, -68, -69 },
//            { -81, -66, -67, -68 },
//            { 23, 23, -66, -67 },
//            { 23, 23, -65, -66 },
//            { 91, 17, -64, -112 },
//            { -47, 17, -63, 16 },
//            { -55, -45, -62, -88 },
//            { -43, -44, -61, -46 },
//            { 37, 70, -60, -61 },
//            { 38, 71, -59, -60 },
//            { -73, -74, -58, -59 },
//            { 23, -65, -57, -51 },
//            { -54, -55, -56, -62 },
//            { -53, -54, -55, -45 },
//            { -52, -53, -54, -55 },
//            { -58, -42, -53, -54 },
//            { -65, -50, -52, -53 },
//            { -65, -57, -51, -52 },
//            { 23, -73, -50, -58 },
//            { 23, -65, -49, -51 },
//            { 91, 17, -48, 16 },
//            { -45, -46, -47, -88 },
//            { -55, -56, -46, -62 },
//            { -54, -55, -45, -62 },
//            { -42, -53, -44, -56 },
//            { -58, -42, -43, -44 },
//            { -57, -51, -42, -53 },
//            { 23, 39, -41, -58 },
//            { -43, -44, -40, -46 },
//            { -37, -38, -39, -46 },
//            { -52, -43, -38, -39 },
//            { -35, -36, -37, -38 },
//            { -49, -35, -36, -37 },
//            { -65, -57, -35, -36 },
//            { 23, -73, -34, -58 },
//            { 23, 55, -33, 38 },
//            { 91, 17, -32, -8 },
//            { -30, 68, -31, -23 },
//            { -28, -29, -30, -31 },
//            { -11, -28, -29, -30 },
//            { -27, 29, -28, -29 },
//            { -65, -34, -27, 37 },
//            { 23, -25, -26, -27 },
//            { 23, 23, -25, -34 },
//            { -15, 17, -24, 16 },
//            { 68, -31, -23, -7 },
//            { -29, -30, -22, -6 },
//            { -19, -20, -21, -5 },
//            { 78, -19, -20, -21 },
//            { -34, -27, -19, -28 },
//            { -25, 31, -18, 61 },
//            { 23, 23, -17, -26 },
//            { 17, -32, -16, 16 },
//            { -61, 18, -15, 17 },
//            { 50, 93, -14, 67 },
//            { -59, 36, -13, -30 },
//            { 21, -11, -12, 69 },
//            { 38, 21, -11, -12 },
//            { -33, 86, -10, -59 },
//            { 23, 55, -9, -27 },
//            { 17, -32, -8, 16 },
//            { -31, -15, -7, -24 },
//            { -30, 68, -6, -7 },
//            { 43, 59, -5, -6 },
//            { 61, 77, -4, -5 },
//            { 31, -18, -3, 77 },
//            { 23, -17, -2, 78 },
//            { 23, 23, -1, -2 },
//    };

    private Colorizer() {

    }

    protected Colorizer(PaletteReducer reducer) {
        this.reducer = reducer;
    }

    protected PaletteReducer reducer;

    /**
     * @param voxel      A color index
     * @param brightness An integer representing how many shades brighter (if positive) or darker (if negative) the result should be
     * @return A different shade of the same color
     */
    @Override
    public byte colorize(byte voxel, int brightness) {
        if (brightness > 0) {
            for (int i = 0; i < brightness; i++) {
                voxel = brighten(voxel);
            }
        } else if (brightness < 0) {
            for (int i = 0; i > brightness; i--) {
                voxel = darken(voxel);
            }
        }
        return voxel;
    }

    /**
     * @param color An RGBA8888 color
     * @return The nearest available color index in the palette
     */
    @Override
    public byte reduce(int color) {
        return reducer.reduceIndex(color);
    }

    /**
     * Uses {@link #colorize(byte, int)} to figure out what index has the correct brightness, then looks that index up
     * in the {@link #reducer}'s stored palette array to get an RGBA8888 int.
     *
     * @param brightness 0 for dark, 1 for dim, 2 for medium and 3 for bright. Negative numbers are expected to normally be interpreted as black and numbers higher than 3 as white.
     * @param voxel      The color index of a voxel
     * @return An rgba8888 color
     */
    @Override
    public int dimmer(int brightness, byte voxel) {
        return reducer.paletteArray[colorize(voxel, brightness - 2) & 0xFF];
    }

    /**
     * Gets a PaletteReducer that contains the RGBA8888 int colors that the byte indices these deals with correspond to.
     * This PaletteReducer can be queried for random colors with {@link PaletteReducer#randomColor(Random)} (for an int
     * color) or {@link PaletteReducer#randomColorIndex(Random)} (for a byte this can use again).
     *
     * @return the PaletteReducer this uses to store the corresponding RGBA8888 colors for the palette
     */
    public PaletteReducer getReducer() {
        return reducer;
    }

    /**
     * Sets the PaletteReducer this uses.
     *
     * @param reducer a PaletteReducer that should not be null
     */
    protected void setReducer(PaletteReducer reducer) {
        this.reducer = reducer;
    }

    private static int luma(final int r, final int g, final int b) {
        return r * 0x9C + g * 0xF6 + b * 0x65 + 0x18 - (Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b))) * 0x19;
//        return color.r * 0x8.Ap-5f + color.g * 0xF.Fp-5f + color.b * 0x6.1p-5f
//                + 0x1.6p-5f - (Math.max(color.r, Math.max(color.g, color.b))
//                - Math.min(color.r, Math.min(color.g, color.b))) * 0x1.6p-5f;
        // r * 0x8A + g * 0xFF + b * 0x61 + 0x15 - (Math.max(r, Math.max(g, b)) - Math.min(r, Math.min(g, b))) * 0x16;
        // 0x8A + 0xFF + 0x61 + 0x16 - 0x16
    }

    /**
     * Approximates the "color distance" between two colors defined by their YCoCg values. The luma in parameters y1 and
     * y2 should be calculated with {@link #luma(int, int, int)}, which is not the standard luminance value for YCoCg;
     * it will range from 0 to 63 typically. The chrominance orange values co1 and co2, and the chrominance green values
     * cg1 and cg2, should range from 0 to 31 typically by taking the standard range of -0.5 to 0.5, adding 0.5,
     * multiplying by 31 and rounding to an int.
     *
     * @param y1  luma for color 1; from 0 to 63, calculated by {@link #luma(int, int, int)}
     * @param co1 chrominance orange for color 1; from 0 to 31, usually related to {@code red - blue}
     * @param cg1 chrominance green for color 1; from 0 to 31, usually related to {@code green - (red + blue) * 0.5}
     * @param y2  luma for color 2; from 0 to 63, calculated by {@link #luma(int, int, int)}
     * @param co2 chrominance orange for color 2; from 0 to 31, usually related to {@code red - blue}
     * @param cg2 chrominance green for color 2; from 0 to 31, usually related to {@code green - (red + blue) * 0.5}
     * @return a non-negative int that is larger for more-different colors; typically somewhat large
     */
    private static int difference(int y1, int co1, int cg1, int y2, int co2, int cg2) {
        return ((y1 - y2) * (y1 - y2) << 2) + (((co1 - co2) * (co1 - co2) + (cg1 - cg2) * (cg1 - cg2)) * 3);
    }


    /**
     * Bytes that correspond to palette indices to use when shading an Aurora-palette model.
     * The color in index 1 of each 4-element sub-array is the "dimmer" color and the one that will match the voxel
     * color used in a model. The color at index 0 is "bright", index 2 is "dim", and index 3 is "dark". Normal usage
     * with a renderer will use {@link #AURORA_RAMP_VALUES}; this array would be used to figure out what indices are related to
     * another color for the purpose of procedurally using similar colors with different lightness.
     * To visualize this, <a href="https://i.imgur.com/rQDrPE7.png">use this image</a>, with the first 32 items in
     * AURORA_RAMPS corresponding to the first column from top to bottom, the next 32 items in the second column, etc.
     */
    public static final byte[][] AURORA_RAMPS = new byte[][]{
            { 0, 0, 0, 0 },
            { 3, 1, 1, 1 },
            { -124, 2, 1, 1 },
            { 119, 3, 1, 1 },
            { 6, 4, 117, 2 },
            { 7, 5, -124, 3 },
            { 51, 6, 119, 4 },
            { 50, 7, 5, 119 },
            { 10, 8, 6, 5 },
            { 47, 9, 7, 6 },
            { 12, 10, 8, 51 },
            { -116, 11, 9, 50 },
            { 127, 12, 10, 9 },
            { 15, 13, 11, 47 },
            { 15, 14, 12, 11 },
            { 15, 15, 13, -116 },
            { -65, 16, -53, -32 },
            { -44, 17, -65, -66 },
            { -59, 18, -42, -56 },
            { 15, 19, 111, 107 },
            { -25, 20, -38, -37 },
            { -36, 21, 23, 1 },
            { -38, 22, -31, -33 },
            { -35, 23, 1, 1 },
            { -53, 24, 1, 1 },
            { -16, 25, -12, 1 },
            { -20, 26, -17, -16 },
            { -20, 27, -16, 25 },
            { -22, 28, -20, 26 },
            { 15, 29, -114, -113 },
            { -6, 30, -103, 32 },
            { -102, 31, -105, 33 },
            { -100, 32, -3, -1 },
            { -1, 33, -108, 1 },
            { 61, 34, 1, 1 },
            { 32, 35, -106, -107 },
            { 56, 36, 32, -94 },
            { -98, 37, -95, -104 },
            { 39, 38, 56, 55 },
            { 15, 39, 79, 68 },
            { 39, 40, -88, -89 },
            { -87, 41, -89, 42 },
            { -88, 42, -92, -93 },
            { -70, 43, 87, 1 },
            { -71, 44, -68, -75 },
            { -71, 45, -70, -75 },
            { 97, 46, 95, 100 },
            { -116, 47, 9, -119 },
            { 68, 48, 54, 64 },
            { 12, 49, 8, 7 },
            { 11, 50, 7, -126 },
            { 10, 51, 6, 120 },
            { 9, 52, 5, 61 },
            { 65, 53, -2, 62 },
            { 56, 54, 63, -2 },
            { 57, 55, 53, 63 },
            { 59, 56, 54, 53 },
            { 29, 57, 55, 54 },
            { 67, 58, 55, 54 },
            { 39, 59, 48, 65 },
            { 15, 60, 12, -115 },
            { 63, 61, -109, -108 },
            { 52, 62, -11, -107 },
            { 64, 63, 61, -11 },
            { 65, 64, 63, 62 },
            { 66, 65, 64, 53 },
            { 67, 66, 65, -112 },
            { 15, 67, -114, -113 },
            { 14, 68, 48, 65 },
            { 66, 69, 64, 53 },
            { 9, 70, 71, 62 },
            { 70, 71, 73, 72 },
            { 5, 72, 1, 1 },
            { 71, 73, 87, 2 },
            { 75, 74, 86, 73 },
            { 49, 75, 74, 71 },
            { 48, 76, 70, 74 },
            { 68, 77, 75, -90 },
            { 80, 78, 69, 76 },
            { 14, 79, 48, 49 },
            { 15, 80, 12, 11 },
            { 14, 81, 82, 95 },
            { 81, 82, 9, 75 },
            { 82, 83, 84, 85 },
            { 100, 84, 90, 86 },
            { 93, 85, 89, 86 },
            { 101, 86, 103, 87 },
            { 4, 87, 1, 1 },
            { 91, 88, 117, 2 },
            { 101, 89, 103, 3 },
            { 51, 90, 4, 103 },
            { 51, 91, 88, -67 },
            { 93, 92, 89, 88 },
            { 95, 93, 92, 101 },
            { 107, 94, 92, 101 },
            { 99, 95, 93, 51 },
            { 109, 96, 100, 93 },
            { 15, 97, 110, 111 },
            { 97, 98, 107, 95 },
            { 97, 99, 100, 106 },
            { 96, 100, 8, 51 },
            { 50, 101, 89, 88 },
            { 105, 102, -67, -32 },
            { 5, 103, 1, 1 },
            { 105, 104, 117, 1 },
            { 51, 105, 119, 104 },
            { 107, 106, 115, 105 },
            { 108, 107, 106, 114 },
            { 97, 108, 107, 113 },
            { 15, 109, 111, 112 },
            { 97, 110, 125, 113 },
            { 127, 111, 113, 124 },
            { 126, 112, 124, 123 },
            { 111, 113, 50, 122 },
            { -45, 114, -51, -53 },
            { 50, 115, 116, 120 },
            { 115, 116, -54, -32 },
            { 104, 117, 1, 1 },
            { 119, 118, 1, 1 },
            { 121, 119, 118, 117 },
            { 115, 120, -54, 118 },
            { 123, 121, 119, -54 },
            { 124, 122, 120, -125 },
            { -45, 123, 121, 22 },
            { 125, 124, 122, -126 },
            { 110, 125, 124, 123 },
            { 15, 126, 112, 125 },
            { 15, 127, -43, 112 },
            { -43, -128, 122, -126 },
            { -26, -127, -16, -15 },
            { -119, -126, -125, -15 },
            { -126, -125, -13, -12 },
            { 5, -124, 1, 1 },
            { -122, -123, -108, 1 },
            { -120, -122, -14, -124 },
            { -119, -121, -125, -14 },
            { -128, -120, -122, -10 },
            { -114, -119, -121, -126 },
            { -117, -118, -113, -112 },
            { 15, -117, -115, -118 },
            { 15, -116, 47, -113 },
            { -117, -115, -113, -119 },
            { -116, -114, -112, -119 },
            { -118, -113, -120, -121 },
            { -114, -112, -120, -121 },
            { -113, -111, 63, -10 },
            { -122, -110, -108, 1 },
            { -123, -109, 1, 1 },
            { -110, -108, 1, 1 },
            { 61, -107, 1, 1 },
            { 35, -106, -108, 1 },
            { 32, -105, -108, 1 },
            { -101, -104, 33, -108 },
            { 30, -103, -3, -1 },
            { -101, -102, -105, 33 },
            { 30, -101, -102, -104 },
            { 57, -100, 32, -3 },
            { -85, -99, -97, -89 },
            { 38, -98, -97, -96 },
            { -98, -97, -95, -94 },
            { -98, -96, -94, -105 },
            { 36, -95, 35, -105 },
            { -96, -94, -105, -106 },
            { 71, -93, -108, 1 },
            { 74, -92, -78, 87 },
            { 74, -91, -78, -107 },
            { 9, -90, 71, 86 },
            { 41, -89, -94, 35 },
            { -87, -88, 42, -91 },
            { 39, -87, 41, -88 },
            { -85, -86, -97, -89 },
            { 39, -85, -99, -97 },
            { 39, -84, -82, -81 },
            { 46, -83, 83, -81 },
            { -84, -82, -80, 42 },
            { -83, -81, 42, -79 },
            { 83, -80, -91, -92 },
            { 92, -79, -77, -78 },
            { 86, -78, 1, 1 },
            { 86, -77, 2, 1 },
            { 89, -76, 87, 2 },
            { 44, -75, -76, -77 },
            { -71, -74, -75, 43 },
            { -71, -73, -75, 43 },
            { 46, -72, -71, 44 },
            { -72, -71, 44, -69 },
            { -71, -70, 43, -76 },
            { -65, -69, 102, -76 },
            { 92, -68, -67, 117 },
            { 116, -67, 1, 1 },
            { 17, -66, -55, -32 },
            { 17, -65, 16, -55 },
            { -63, -64, 16, 102 },
            { -61, -63, -65, -66 },
            { 19, -62, -64, -65 },
            { 19, -61, -63, 17 },
            { 19, -60, -44, 17 },
            { 19, -59, -57, -46 },
            { 19, -58, 17, -47 },
            { 18, -57, -56, 16 },
            { -46, -56, -49, -52 },
            { -66, -55, -32, 24 },
            { 116, -54, 1, 1 },
            { 22, -53, 24, 23 },
            { -49, -52, 23, 1 },
            { -40, -51, -53, -54 },
            { -41, -50, -35, -34 },
            { -40, -49, -52, -53 },
            { -46, -48, -52, -34 },
            { -45, -47, -40, 115 },
            { -59, -46, -56, -48 },
            { -60, -45, 20, -39 },
            { -60, -44, -56, -48 },
            { 127, -43, 124, 123 },
            { 18, -42, -41, -50 },
            { -42, -41, -50, -52 },
            { 20, -40, -49, -51 },
            { -25, -39, -37, 22 },
            { -39, -38, -35, -34 },
            { -39, -37, -30, -35 },
            { 20, -36, -35, -34 },
            { -36, -35, 23, 1 },
            { -36, -34, 1, 1 },
            { 22, -33, 1, 1 },
            { 119, -32, 1, 1 },
            { -29, -31, 23, 1 },
            { -37, -30, -33, 23 },
            { -26, -29, -31, -13 },
            { -26, -28, -27, -30 },
            { -18, -27, -33, 23 },
            { -25, -26, -28, -29 },
            { -23, -25, -26, -127 },
            { -22, -24, -26, -127 },
            { 15, -23, 125, -128 },
            { 15, -22, -24, -113 },
            { -24, -21, 26, -29 },
            { 28, -20, -19, -17 },
            { -20, -19, -16, 25 },
            { -26, -18, -16, -27 },
            { -20, -17, 25, -13 },
            { -18, -16, -31, 25 },
            { -122, -15, 24, 1 },
            { -10, -14, -108, 1 },
            { -125, -13, 1, 1 },
            { -15, -12, 1, 1 },
            { -122, -11, 1, 1 },
            { 26, -10, -13, -108 },
            { -5, -9, 25, 33 },
            { 28, -8, -5, -4 },
            { 28, -7, -5, -4 },
            { 67, -6, -7, -112 },
            { -8, -5, -9, -1 },
            { -8, -4, -1, 33 },
            { -4, -3, 33, -108 },
            { -111, -2, -11, 34 },
            { 32, -1, 33, -108 },
    };
    public static final Colorizer AuroraColorizer = new Colorizer(new PaletteReducer()) {
        private final byte[] primary = {
                -104,
                62,
                -98,
                40,
                -73,
                17,
                -52,
                -127
        }, grays = {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
        };

        @Override
        public byte[] mainColors() {
            return primary;
        }

        /**
         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
         */
        @Override
        public byte[] grayscale() {
            return grays;
        }

        @Override
        public byte brighten(byte voxel) {
            return AURORA_RAMPS[voxel & 0xFF][0];
        }

        @Override
        public byte darken(byte voxel) {
            return AURORA_RAMPS[voxel & 0xFF][2];
        }
    };
//    /**
//     * An experiment with the 64-color Flesurrect palette and ramps (this has the RGBA color values).
//     * <a href="https://i.imgur.com/HV1NDhx.png">View this here</a>.
//     */
//    public static final int[][] FLESURRECT_RAMP_VALUES = new int[][]{
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x1F1833FF, 0x1F1833FF, 0x1F1833FF, 0x3E3546FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x2B2E42FF, 0x68717AFF },
//            { 0x1F1833FF, 0x1F1833FF, 0x3E3546FF, 0x715A56FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x414859FF, 0x68717AFF },
//            { 0x3E3546FF, 0x414859FF, 0x68717AFF, 0x90A1A8FF },
//            { 0x715A56FF, 0x68717AFF, 0x90A1A8FF, 0xB6CBCFFF },
//            { 0x5C76BFFF, 0x90A1A8FF, 0xB6CBCFFF, 0xFFFFFFFF },
//            { 0x5C76BFFF, 0x90A1A8FF, 0xD3E5EDFF, 0xFFFFFFFF },
//            { 0xC8E4BEFF, 0xD3E5EDFF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x1F1833FF, 0x1F1833FF, 0x5C3A41FF, 0x826481FF },
//            { 0x3E3546FF, 0x414859FF, 0x826481FF, 0xAB947AFF },
//            { 0x5C3A41FF, 0x7A3045FF, 0x966C6CFF, 0xAB947AFF },
//            { 0x2B2E42FF, 0x3E3546FF, 0x715A56FF, 0xAB947AFF },
//            { 0x715A56FF, 0x966C6CFF, 0xAB947AFF, 0xDDBBA4FF },
//            { 0xAE4539FF, 0xF04F78FF, 0xF68181FF, 0xDDBBA4FF },
//            { 0x1F1833FF, 0x5A0A07FF, 0xF53333FF, 0xF04F78FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x5A0A07FF, 0x7A3045FF },
//            { 0x5A0A07FF, 0x7A3045FF, 0xAE4539FF, 0xCD683DFF },
//            { 0x1F1833FF, 0x5A0A07FF, 0x8A503EFF, 0x966C6CFF },
//            { 0x7A3045FF, 0xAE4539FF, 0xCD683DFF, 0xF68181FF },
//            { 0xCD683DFF, 0xC29162FF, 0xFBA458FF, 0xDDBBA4FF },
//            { 0xC93038FF, 0xF53333FF, 0xFB6B1DFF, 0xFFA514FF },
//            { 0x966C6CFF, 0xAB947AFF, 0xDDBBA4FF, 0xD3E5EDFF },
//            { 0xC29162FF, 0xF68181FF, 0xFDD7AAFF, 0xFFFFFFFF },
//            { 0xF53333FF, 0xFB6B1DFF, 0xFFA514FF, 0xFBE626FF },
//            { 0x8A503EFF, 0x966C6CFF, 0xC29162FF, 0xDDBBA4FF },
//            { 0xAE4539FF, 0xFB6B1DFF, 0xE8B710FF, 0xFBE626FF },
//            { 0xC0B510FF, 0xE8B710FF, 0xFBE626FF, 0xFBFF86FF },
//            { 0xAE4539FF, 0xCD683DFF, 0xC0B510FF, 0xB4D645FF },
//            { 0xC0B510FF, 0xB4D645FF, 0xFBFF86FF, 0xFFFFFFFF },
//            { 0x729446FF, 0xC0B510FF, 0xB4D645FF, 0xDDBBA4FF },
//            { 0x5C3A41FF, 0x715A56FF, 0x729446FF, 0xAB947AFF },
//            { 0xAB947AFF, 0x90A1A8FF, 0xC8E4BEFF, 0xFFFFFFFF },
//            { 0x0E4904FF, 0x039F78FF, 0x45F520FF, 0x55F084FF },
//            { 0x414859FF, 0x715A56FF, 0x51C43FFF, 0x55F084FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x0E4904FF, 0x414859FF },
//            { 0x039F78FF, 0x1EBC73FF, 0x55F084FF, 0xC8E4BEFF },
//            { 0x0E4904FF, 0x216981FF, 0x1EBC73FF, 0x30E1B9FF },
//            { 0x039F78FF, 0x1EBC73FF, 0x30E1B9FF, 0x7FE8F2FF },
//            { 0x039F78FF, 0x1EBC73FF, 0x7FE0C2FF, 0xB8FDFFFF },
//            { 0x7FE0C2FF, 0x7FE8F2FF, 0xB8FDFFFF, 0xFFFFFFFF },
//            { 0x0E4904FF, 0x28306FFF, 0x039F78FF, 0x30E1B9FF },
//            { 0x5C76BFFF, 0x4D9BE6FF, 0x63C2C9FF, 0x7FE8F2FF },
//            { 0x1F1833FF, 0x28306FFF, 0x216981FF, 0x5C76BFFF },
//            { 0x4D9BE6FF, 0x63C2C9FF, 0x7FE8F2FF, 0xB8FDFFFF },
//            { 0x180FCFFF, 0x4D44C0FF, 0x5369EFFF, 0x4D9BE6FF },
//            { 0x4D44C0FF, 0x5369EFFF, 0x4D9BE6FF, 0x63C2C9FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x28306FFF, 0x4D44C0FF },
//            { 0x28306FFF, 0x4D44C0FF, 0x5C76BFFF, 0xA884F3FF },
//            { 0x180FCFFF, 0x28306FFF, 0x4D44C0FF, 0x5369EFFF },
//            { 0x1F1833FF, 0x1F1833FF, 0x180FCFFF, 0x4D44C0FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x53207DFF, 0x4D44C0FF },
//            { 0x881AC4FF, 0x4D44C0FF, 0x8657CCFF, 0xA884F3FF },
//            { 0x4D44C0FF, 0x8657CCFF, 0xA884F3FF, 0xE4A8FAFF },
//            { 0x1F1833FF, 0x1F1833FF, 0x630867FF, 0x881AC4FF },
//            { 0x53207DFF, 0x881AC4FF, 0xA03EB2FF, 0xF34FE9FF },
//            { 0x630867FF, 0x53207DFF, 0x881AC4FF, 0xA03EB2FF },
//            { 0xF34FE9FF, 0xA884F3FF, 0xE4A8FAFF, 0xD3E5EDFF },
//            { 0x630867FF, 0x7A3045FF, 0xB53D86FF, 0xF04F78FF },
//            { 0x881AC4FF, 0xA03EB2FF, 0xF34FE9FF, 0xE4A8FAFF },
//            { 0x5A0A07FF, 0x630867FF, 0x7A3045FF, 0x715A56FF },
//            { 0xC93038FF, 0xB53D86FF, 0xF04F78FF, 0xF68181FF },
//            { 0x5A0A07FF, 0x7A3045FF, 0xC93038FF, 0xF04F78FF },
//    
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x1F1833FF, 0x1F1833FF, 0x1F1833FF, 0x1F1833FF },
//            { 0x1F1833FF, 0x2B2E42FF, 0x2B2E42FF, 0x2B2E42FF },
//            { 0x1F1833FF, 0x3E3546FF, 0x3E3546FF, 0x3E3546FF },
//            { 0x1F1833FF, 0x414859FF, 0x414859FF, 0x414859FF },
//            { 0x414859FF, 0x68717AFF, 0x68717AFF, 0x68717AFF },
//            { 0x68717AFF, 0x90A1A8FF, 0x90A1A8FF, 0x90A1A8FF },
//            { 0x90A1A8FF, 0xB6CBCFFF, 0xB6CBCFFF, 0xB6CBCFFF },
//            { 0x90A1A8FF, 0xD3E5EDFF, 0xD3E5EDFF, 0xD3E5EDFF },
//            { 0xD3E5EDFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x1F1833FF, 0x5C3A41FF, 0x5C3A41FF, 0x5C3A41FF },
//            { 0x414859FF, 0x826481FF, 0x826481FF, 0x826481FF },
//            { 0x7A3045FF, 0x966C6CFF, 0x966C6CFF, 0x966C6CFF },
//            { 0x3E3546FF, 0x715A56FF, 0x715A56FF, 0x715A56FF },
//            { 0x966C6CFF, 0xAB947AFF, 0xAB947AFF, 0xAB947AFF },
//            { 0xF04F78FF, 0xF68181FF, 0xF68181FF, 0xF68181FF },
//            { 0x5A0A07FF, 0xF53333FF, 0xF53333FF, 0xF53333FF },
//            { 0x1F1833FF, 0x5A0A07FF, 0x5A0A07FF, 0x5A0A07FF },
//            { 0x7A3045FF, 0xAE4539FF, 0xAE4539FF, 0xAE4539FF },
//            { 0x5A0A07FF, 0x8A503EFF, 0x8A503EFF, 0x8A503EFF },
//            { 0xAE4539FF, 0xCD683DFF, 0xCD683DFF, 0xCD683DFF },
//            { 0xC29162FF, 0xFBA458FF, 0xFBA458FF, 0xFBA458FF },
//            { 0xF53333FF, 0xFB6B1DFF, 0xFB6B1DFF, 0xFB6B1DFF },
//            { 0xAB947AFF, 0xDDBBA4FF, 0xDDBBA4FF, 0xDDBBA4FF },
//            { 0xF68181FF, 0xFDD7AAFF, 0xFDD7AAFF, 0xFDD7AAFF },
//            { 0xFB6B1DFF, 0xFFA514FF, 0xFFA514FF, 0xFFA514FF },
//            { 0x966C6CFF, 0xC29162FF, 0xC29162FF, 0xC29162FF },
//            { 0xFB6B1DFF, 0xE8B710FF, 0xE8B710FF, 0xE8B710FF },
//            { 0xE8B710FF, 0xFBE626FF, 0xFBE626FF, 0xFBE626FF },
//            { 0xCD683DFF, 0xC0B510FF, 0xC0B510FF, 0xC0B510FF },
//            { 0xB4D645FF, 0xFBFF86FF, 0xFBFF86FF, 0xFBFF86FF },
//            { 0xC0B510FF, 0xB4D645FF, 0xB4D645FF, 0xB4D645FF },
//            { 0x715A56FF, 0x729446FF, 0x729446FF, 0x729446FF },
//            { 0x90A1A8FF, 0xC8E4BEFF, 0xC8E4BEFF, 0xC8E4BEFF },
//            { 0x039F78FF, 0x45F520FF, 0x45F520FF, 0x45F520FF },
//            { 0x715A56FF, 0x51C43FFF, 0x51C43FFF, 0x51C43FFF },
//            { 0x1F1833FF, 0x0E4904FF, 0x0E4904FF, 0x0E4904FF },
//            { 0x1EBC73FF, 0x55F084FF, 0x55F084FF, 0x55F084FF },
//            { 0x216981FF, 0x1EBC73FF, 0x1EBC73FF, 0x1EBC73FF },
//            { 0x1EBC73FF, 0x30E1B9FF, 0x30E1B9FF, 0x30E1B9FF },
//            { 0x1EBC73FF, 0x7FE0C2FF, 0x7FE0C2FF, 0x7FE0C2FF },
//            { 0x7FE8F2FF, 0xB8FDFFFF, 0xB8FDFFFF, 0xB8FDFFFF },
//            { 0x28306FFF, 0x039F78FF, 0x039F78FF, 0x039F78FF },
//            { 0x4D9BE6FF, 0x63C2C9FF, 0x63C2C9FF, 0x63C2C9FF },
//            { 0x28306FFF, 0x216981FF, 0x216981FF, 0x216981FF },
//            { 0x63C2C9FF, 0x7FE8F2FF, 0x7FE8F2FF, 0x7FE8F2FF },
//            { 0x4D44C0FF, 0x5369EFFF, 0x5369EFFF, 0x5369EFFF },
//            { 0x5369EFFF, 0x4D9BE6FF, 0x4D9BE6FF, 0x4D9BE6FF },
//            { 0x1F1833FF, 0x28306FFF, 0x28306FFF, 0x28306FFF },
//            { 0x4D44C0FF, 0x5C76BFFF, 0x5C76BFFF, 0x5C76BFFF },
//            { 0x28306FFF, 0x4D44C0FF, 0x4D44C0FF, 0x4D44C0FF },
//            { 0x1F1833FF, 0x180FCFFF, 0x180FCFFF, 0x180FCFFF },
//            { 0x1F1833FF, 0x53207DFF, 0x53207DFF, 0x53207DFF },
//            { 0x4D44C0FF, 0x8657CCFF, 0x8657CCFF, 0x8657CCFF },
//            { 0x8657CCFF, 0xA884F3FF, 0xA884F3FF, 0xA884F3FF },
//            { 0x1F1833FF, 0x630867FF, 0x630867FF, 0x630867FF },
//            { 0x881AC4FF, 0xA03EB2FF, 0xA03EB2FF, 0xA03EB2FF },
//            { 0x53207DFF, 0x881AC4FF, 0x881AC4FF, 0x881AC4FF },
//            { 0xA884F3FF, 0xE4A8FAFF, 0xE4A8FAFF, 0xE4A8FAFF },
//            { 0x7A3045FF, 0xB53D86FF, 0xB53D86FF, 0xB53D86FF },
//            { 0xA03EB2FF, 0xF34FE9FF, 0xF34FE9FF, 0xF34FE9FF },
//            { 0x630867FF, 0x7A3045FF, 0x7A3045FF, 0x7A3045FF },
//            { 0xB53D86FF, 0xF04F78FF, 0xF04F78FF, 0xF04F78FF },
//            { 0x7A3045FF, 0xC93038FF, 0xC93038FF, 0xC93038FF },
//
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x1F1833FF, 0x1F1833FF, 0x1F1833FF, 0x3E3546FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x2B2E42FF, 0x68717AFF },
//            { 0x1F1833FF, 0x1F1833FF, 0x3E3546FF, 0x715A56FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x414859FF, 0x68717AFF },
//            { 0x3E3546FF, 0x414859FF, 0x68717AFF, 0x90A1A8FF },
//            { 0x715A56FF, 0x68717AFF, 0x90A1A8FF, 0xB6CBCFFF },
//            { 0x5C76BFFF, 0x90A1A8FF, 0xB6CBCFFF, 0xFFFFFFFF },
//            { 0x5C76BFFF, 0x90A1A8FF, 0xD3E5EDFF, 0xFFFFFFFF },
//            { 0xC8E4BEFF, 0xD3E5EDFF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x1F1833FF, 0x1F1833FF, 0x5C3A41FF, 0x826481FF },
//            { 0x3E3546FF, 0x414859FF, 0x826481FF, 0xAB947AFF },
//            { 0x5C3A41FF, 0x7A3045FF, 0x966C6CFF, 0xAB947AFF },
//            { 0x2B2E42FF, 0x3E3546FF, 0x715A56FF, 0xAB947AFF },
//            { 0x715A56FF, 0x966C6CFF, 0xAB947AFF, 0xDDBBA4FF },
//            { 0xAE4539FF, 0xF04F78FF, 0xF68181FF, 0xDDBBA4FF },
//            { 0x1F1833FF, 0x5A0A07FF, 0xF53333FF, 0xF04F78FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x5A0A07FF, 0x7A3045FF },
//            { 0x5A0A07FF, 0x7A3045FF, 0xAE4539FF, 0xCD683DFF },
//            { 0x1F1833FF, 0x5A0A07FF, 0x8A503EFF, 0x966C6CFF },
//            { 0x7A3045FF, 0xAE4539FF, 0xCD683DFF, 0xF68181FF },
//            { 0xCD683DFF, 0xC29162FF, 0xFBA458FF, 0xDDBBA4FF },
//            { 0xC93038FF, 0xF53333FF, 0xFB6B1DFF, 0xFFA514FF },
//            { 0x966C6CFF, 0xAB947AFF, 0xDDBBA4FF, 0xD3E5EDFF },
//            { 0xC29162FF, 0xF68181FF, 0xFDD7AAFF, 0xFFFFFFFF },
//            { 0xF53333FF, 0xFB6B1DFF, 0xFFA514FF, 0xFBE626FF },
//            { 0x8A503EFF, 0x966C6CFF, 0xC29162FF, 0xDDBBA4FF },
//            { 0xAE4539FF, 0xFB6B1DFF, 0xE8B710FF, 0xFBE626FF },
//            { 0xC0B510FF, 0xE8B710FF, 0xFBE626FF, 0xFBFF86FF },
//            { 0xAE4539FF, 0xCD683DFF, 0xC0B510FF, 0xB4D645FF },
//            { 0xC0B510FF, 0xB4D645FF, 0xFBFF86FF, 0xFFFFFFFF },
//            { 0x729446FF, 0xC0B510FF, 0xB4D645FF, 0xDDBBA4FF },
//            { 0x5C3A41FF, 0x715A56FF, 0x729446FF, 0xAB947AFF },
//            { 0xAB947AFF, 0x90A1A8FF, 0xC8E4BEFF, 0xFFFFFFFF },
//            { 0x0E4904FF, 0x039F78FF, 0x45F520FF, 0x55F084FF },
//            { 0x414859FF, 0x715A56FF, 0x51C43FFF, 0x55F084FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x0E4904FF, 0x414859FF },
//            { 0x039F78FF, 0x1EBC73FF, 0x55F084FF, 0xC8E4BEFF },
//            { 0x0E4904FF, 0x216981FF, 0x1EBC73FF, 0x30E1B9FF },
//            { 0x039F78FF, 0x1EBC73FF, 0x30E1B9FF, 0x7FE8F2FF },
//            { 0x039F78FF, 0x1EBC73FF, 0x7FE0C2FF, 0xB8FDFFFF },
//            { 0x7FE0C2FF, 0x7FE8F2FF, 0xB8FDFFFF, 0xFFFFFFFF },
//            { 0x0E4904FF, 0x28306FFF, 0x039F78FF, 0x30E1B9FF },
//            { 0x5C76BFFF, 0x4D9BE6FF, 0x63C2C9FF, 0x7FE8F2FF },
//            { 0x1F1833FF, 0x28306FFF, 0x216981FF, 0x5C76BFFF },
//            { 0x4D9BE6FF, 0x63C2C9FF, 0x7FE8F2FF, 0xB8FDFFFF },
//            { 0x180FCFFF, 0x4D44C0FF, 0x5369EFFF, 0x4D9BE6FF },
//            { 0x4D44C0FF, 0x5369EFFF, 0x4D9BE6FF, 0x63C2C9FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x28306FFF, 0x4D44C0FF },
//            { 0x28306FFF, 0x4D44C0FF, 0x5C76BFFF, 0xA884F3FF },
//            { 0x180FCFFF, 0x28306FFF, 0x4D44C0FF, 0x5369EFFF },
//            { 0x1F1833FF, 0x1F1833FF, 0x180FCFFF, 0x4D44C0FF },
//            { 0x1F1833FF, 0x1F1833FF, 0x53207DFF, 0x4D44C0FF },
//            { 0x881AC4FF, 0x4D44C0FF, 0x8657CCFF, 0xA884F3FF },
//            { 0x4D44C0FF, 0x8657CCFF, 0xA884F3FF, 0xE4A8FAFF },
//            { 0x1F1833FF, 0x1F1833FF, 0x630867FF, 0x881AC4FF },
//            { 0x53207DFF, 0x881AC4FF, 0xA03EB2FF, 0xF34FE9FF },
//            { 0x630867FF, 0x53207DFF, 0x881AC4FF, 0xA03EB2FF },
//            { 0xF34FE9FF, 0xA884F3FF, 0xE4A8FAFF, 0xD3E5EDFF },
//            { 0x630867FF, 0x7A3045FF, 0xB53D86FF, 0xF04F78FF },
//            { 0x881AC4FF, 0xA03EB2FF, 0xF34FE9FF, 0xE4A8FAFF },
//            { 0x5A0A07FF, 0x630867FF, 0x7A3045FF, 0x715A56FF },
//            { 0xC93038FF, 0xB53D86FF, 0xF04F78FF, 0xF68181FF },
//            { 0x5A0A07FF, 0x7A3045FF, 0xC93038FF, 0xF04F78FF },
//
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x1F1833FF, 0x1F1833FF, 0x1F1833FF, 0x3E3546FF },
//            { 0x2B2E42FF, 0x1F1833FF, 0x2B2E42FF, 0x68717AFF },
//            { 0x3E3546FF, 0x1F1833FF, 0x3E3546FF, 0x715A56FF },
//            { 0x414859FF, 0x1F1833FF, 0x414859FF, 0x68717AFF },
//            { 0x68717AFF, 0x414859FF, 0x68717AFF, 0x90A1A8FF },
//            { 0x90A1A8FF, 0x68717AFF, 0x90A1A8FF, 0xB6CBCFFF },
//            { 0xB6CBCFFF, 0x90A1A8FF, 0xB6CBCFFF, 0xFFFFFFFF },
//            { 0xD3E5EDFF, 0x90A1A8FF, 0xD3E5EDFF, 0xFFFFFFFF },
//            { 0xFFFFFFFF, 0xD3E5EDFF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x5C3A41FF, 0x1F1833FF, 0x5C3A41FF, 0x826481FF },
//            { 0x826481FF, 0x414859FF, 0x826481FF, 0xAB947AFF },
//            { 0x966C6CFF, 0x7A3045FF, 0x966C6CFF, 0xAB947AFF },
//            { 0x715A56FF, 0x3E3546FF, 0x715A56FF, 0xAB947AFF },
//            { 0xAB947AFF, 0x966C6CFF, 0xAB947AFF, 0xDDBBA4FF },
//            { 0xF68181FF, 0xF04F78FF, 0xF68181FF, 0xDDBBA4FF },
//            { 0xF53333FF, 0x5A0A07FF, 0xF53333FF, 0xF04F78FF },
//            { 0x5A0A07FF, 0x1F1833FF, 0x5A0A07FF, 0x7A3045FF },
//            { 0xAE4539FF, 0x7A3045FF, 0xAE4539FF, 0xCD683DFF },
//            { 0x8A503EFF, 0x5A0A07FF, 0x8A503EFF, 0x966C6CFF },
//            { 0xCD683DFF, 0xAE4539FF, 0xCD683DFF, 0xF68181FF },
//            { 0xFBA458FF, 0xC29162FF, 0xFBA458FF, 0xDDBBA4FF },
//            { 0xFB6B1DFF, 0xF53333FF, 0xFB6B1DFF, 0xFFA514FF },
//            { 0xDDBBA4FF, 0xAB947AFF, 0xDDBBA4FF, 0xD3E5EDFF },
//            { 0xFDD7AAFF, 0xF68181FF, 0xFDD7AAFF, 0xFFFFFFFF },
//            { 0xFFA514FF, 0xFB6B1DFF, 0xFFA514FF, 0xFBE626FF },
//            { 0xC29162FF, 0x966C6CFF, 0xC29162FF, 0xDDBBA4FF },
//            { 0xE8B710FF, 0xFB6B1DFF, 0xE8B710FF, 0xFBE626FF },
//            { 0xFBE626FF, 0xE8B710FF, 0xFBE626FF, 0xFBFF86FF },
//            { 0xC0B510FF, 0xCD683DFF, 0xC0B510FF, 0xB4D645FF },
//            { 0xFBFF86FF, 0xB4D645FF, 0xFBFF86FF, 0xFFFFFFFF },
//            { 0xB4D645FF, 0xC0B510FF, 0xB4D645FF, 0xDDBBA4FF },
//            { 0x729446FF, 0x715A56FF, 0x729446FF, 0xAB947AFF },
//            { 0xC8E4BEFF, 0x90A1A8FF, 0xC8E4BEFF, 0xFFFFFFFF },
//            { 0x45F520FF, 0x039F78FF, 0x45F520FF, 0x55F084FF },
//            { 0x51C43FFF, 0x715A56FF, 0x51C43FFF, 0x55F084FF },
//            { 0x0E4904FF, 0x1F1833FF, 0x0E4904FF, 0x414859FF },
//            { 0x55F084FF, 0x1EBC73FF, 0x55F084FF, 0xC8E4BEFF },
//            { 0x1EBC73FF, 0x216981FF, 0x1EBC73FF, 0x30E1B9FF },
//            { 0x30E1B9FF, 0x1EBC73FF, 0x30E1B9FF, 0x7FE8F2FF },
//            { 0x7FE0C2FF, 0x1EBC73FF, 0x7FE0C2FF, 0xB8FDFFFF },
//            { 0xB8FDFFFF, 0x7FE8F2FF, 0xB8FDFFFF, 0xFFFFFFFF },
//            { 0x039F78FF, 0x28306FFF, 0x039F78FF, 0x30E1B9FF },
//            { 0x63C2C9FF, 0x4D9BE6FF, 0x63C2C9FF, 0x7FE8F2FF },
//            { 0x216981FF, 0x28306FFF, 0x216981FF, 0x5C76BFFF },
//            { 0x7FE8F2FF, 0x63C2C9FF, 0x7FE8F2FF, 0xB8FDFFFF },
//            { 0x5369EFFF, 0x4D44C0FF, 0x5369EFFF, 0x4D9BE6FF },
//            { 0x4D9BE6FF, 0x5369EFFF, 0x4D9BE6FF, 0x63C2C9FF },
//            { 0x28306FFF, 0x1F1833FF, 0x28306FFF, 0x4D44C0FF },
//            { 0x5C76BFFF, 0x4D44C0FF, 0x5C76BFFF, 0xA884F3FF },
//            { 0x4D44C0FF, 0x28306FFF, 0x4D44C0FF, 0x5369EFFF },
//            { 0x180FCFFF, 0x1F1833FF, 0x180FCFFF, 0x4D44C0FF },
//            { 0x53207DFF, 0x1F1833FF, 0x53207DFF, 0x4D44C0FF },
//            { 0x8657CCFF, 0x4D44C0FF, 0x8657CCFF, 0xA884F3FF },
//            { 0xA884F3FF, 0x8657CCFF, 0xA884F3FF, 0xE4A8FAFF },
//            { 0x630867FF, 0x1F1833FF, 0x630867FF, 0x881AC4FF },
//            { 0xA03EB2FF, 0x881AC4FF, 0xA03EB2FF, 0xF34FE9FF },
//            { 0x881AC4FF, 0x53207DFF, 0x881AC4FF, 0xA03EB2FF },
//            { 0xE4A8FAFF, 0xA884F3FF, 0xE4A8FAFF, 0xD3E5EDFF },
//            { 0xB53D86FF, 0x7A3045FF, 0xB53D86FF, 0xF04F78FF },
//            { 0xF34FE9FF, 0xA03EB2FF, 0xF34FE9FF, 0xE4A8FAFF },
//            { 0x7A3045FF, 0x630867FF, 0x7A3045FF, 0x715A56FF },
//            { 0xF04F78FF, 0xB53D86FF, 0xF04F78FF, 0xF68181FF },
//            { 0xC93038FF, 0x7A3045FF, 0xC93038FF, 0xF04F78FF },
//    };
//    /**
//     * An experiment with the 64-color Flesurrect palette and ramps. There are 128 elements in this ramp array, but the
//     * largest element is 63, so it can be used to look up RGBA8888 colors in a 64-element array. The second half of
//     * items is used for voxels that should not be shaded or have outlines, such as light sources or dust/smoke.
//     * <a href="https://i.imgur.com/HV1NDhx.png">View this here</a>.
//     */
//    public static final byte[][] FLESURRECT_RAMPS = new byte[][]{
//            { 0, 0, 0, 0 },
//            { 1, 1, 1, 3 },
//            { 1, 1, 2, 5 },
//            { 1, 1, 3, 13 },
//            { 1, 1, 4, 5 },
//            { 3, 4, 5, 6 },
//            { 13, 5, 6, 7 },
//            { 49, 6, 7, 9 },
//            { 49, 6, 8, 9 },
//            { 33, 8, 9, 9 },
//            { 1, 1, 10, 11 },
//            { 3, 4, 11, 14 },
//            { 10, 61, 12, 14 },
//            { 2, 3, 13, 14 },
//            { 13, 12, 14, 23 },
//            { 18, 62, 15, 23 },
//            { 1, 17, 16, 62 },
//            { 1, 1, 17, 61 },
//            { 17, 61, 18, 20 },
//            { 1, 17, 19, 12 },
//            { 61, 18, 20, 15 },
//            { 20, 26, 21, 23 },
//            { 63, 16, 22, 25 },
//            { 12, 14, 23, 8 },
//            { 26, 15, 24, 9 },
//            { 16, 22, 25, 28 },
//            { 19, 12, 26, 23 },
//            { 18, 22, 27, 28 },
//            { 29, 27, 28, 30 },
//            { 18, 20, 29, 31 },
//            { 29, 31, 30, 9 },
//            { 32, 29, 31, 23 },
//            { 10, 13, 32, 14 },
//            { 14, 6, 33, 9 },
//            { 36, 42, 34, 37 },
//            { 4, 13, 35, 37 },
//            { 1, 1, 36, 4 },
//            { 42, 38, 37, 33 },
//            { 36, 44, 38, 39 },
//            { 42, 38, 39, 45 },
//            { 42, 38, 40, 41 },
//            { 40, 45, 41, 9 },
//            { 36, 48, 42, 39 },
//            { 49, 47, 43, 45 },
//            { 1, 48, 44, 49 },
//            { 47, 43, 45, 41 },
//            { 51, 50, 46, 47 },
//            { 50, 46, 47, 43 },
//            { 1, 1, 48, 50 },
//            { 48, 50, 49, 54 },
//            { 51, 48, 50, 46 },
//            { 1, 1, 51, 50 },
//            { 1, 1, 52, 50 },
//            { 57, 50, 53, 54 },
//            { 50, 53, 54, 58 },
//            { 1, 1, 55, 57 },
//            { 52, 57, 56, 60 },
//            { 55, 52, 57, 56 },
//            { 60, 54, 58, 8 },
//            { 55, 61, 59, 62 },
//            { 57, 56, 60, 58 },
//            { 17, 55, 61, 13 },
//            { 63, 59, 62, 15 },
//            { 17, 61, 63, 62 },
//    };
//    public static final Colorizer FlesurrectColorizer = new Colorizer(Coloring.FLESURRECT_REDUCER) {
//        private final byte[] primary = {
//                63, 24, 27, 34, 42, 49, 55
//        }, grays = {
//                1, 2, 3, 4, 5, 6, 7, 8, 9
//        };
//
//        @Override
//        public byte[] mainColors() {
//            return primary;
//        }
//
//        /**
//         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
//         */
//        @Override
//        public byte[] grayscale() {
//            return grays;
//        }
//
//        @Override
//        public byte brighten(byte voxel) {
//            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
//            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
//            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
//            return (byte) (FLESURRECT_RAMPS[voxel & 0x3F][3] | (voxel & 0xC0));
//        }
//
//        @Override
//        public byte darken(byte voxel) {
//            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
//            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
//            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
//            return (byte) (FLESURRECT_RAMPS[voxel & 0x3F][1] | (voxel & 0xC0));
//        }
//
//        @Override
//        public int dimmer(int brightness, byte voxel) {
//            return FLESURRECT_RAMP_VALUES[voxel & 0xFF][
//                    brightness <= 0
//                            ? 0
//                            : brightness >= 3
//                            ? 3
//                            : brightness
//                    ];
//        }
//
//        @Override
//        public int getShadeBit() {
//            return 0x40;
//        }
//
//        @Override
//        public int getWaveBit() {
//            return 0x80;
//        }
//    };
    public static final Colorizer AuroraBonusColorizer = new Colorizer(new PaletteReducer()) {
        private final byte[] primary = {
                -104,
                62,
                -98,
                40,
                -73,
                17,
                -52,
                -127
        }, grays = {
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15
        };

        @Override
        public byte[] mainColors() {
            return primary;
        }

        /**
         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
         */
        @Override
        public byte[] grayscale() {
            return grays;
        }

        @Override
        public byte brighten(byte voxel) {
            return AURORA_RAMPS[voxel & 0xFF][0];
        }

        @Override
        public byte darken(byte voxel) {
            return AURORA_RAMPS[voxel & 0xFF][2];
        }

        private int[][] RAMP_VALUES = new int[][]{
                {0x00000000, 0x00000000, 0x00000000, 0x00000000},
                {0x000000FF, 0x000000FF, 0x010101FF, 0x010101FF},
                {0x070707FF, 0x0D0D0DFF, 0x131313FF, 0x1B1B1BFF},
                {0x0D0D0DFF, 0x191919FF, 0x252525FF, 0x353535FF},
                {0x141414FF, 0x252525FF, 0x373737FF, 0x4F4F4FFF},
                {0x1B1B1BFF, 0x323232FF, 0x494949FF, 0x686868FF},
                {0x222222FF, 0x3E3E3EFF, 0x5B5B5BFF, 0x828282FF},
                {0x292929FF, 0x4B4B4BFF, 0x6E6E6EFF, 0x9E9E9EFF},
                {0x303030FF, 0x585858FF, 0x808080FF, 0xB8B8B8FF},
                {0x363636FF, 0x646464FF, 0x929292FF, 0xD1D1D1FF},
                {0x3D3D3DFF, 0x707070FF, 0xA4A4A4FF, 0xEBEBEBFF},
                {0x444444FF, 0x7D7D7DFF, 0xB6B6B6FF, 0xFFFFFFFF},
                {0x4B4B4BFF, 0x8A8A8AFF, 0xC9C9C9FF, 0xFFFFFFFF},
                {0x525252FF, 0x969696FF, 0xDBDBDBFF, 0xFFFFFFFF},
                {0x585858FF, 0xA2A2A2FF, 0xEDEDEDFF, 0xFFFFFFFF},
                {0x5F5F5FFF, 0xAFAFAFFF, 0xFFFFFFFF, 0xFFFFFFFF},
                {0x005728FF, 0x005661FF, 0x007F7FFF, 0x3EA29EFF},
                {0x006945FF, 0x297D91FF, 0x3FBFBFFF, 0x9AFEFAFF},
                {0x009E61FF, 0x009ED0FF, 0x00FFFFFF, 0x7EFFFFFF},
                {0x2A6D62FF, 0x85A9B9FF, 0xBFFFFFFF, 0xFFFFFFFF},
                {0x330FA2FF, 0x4B5EB2FF, 0x8181FFFF, 0xD1CCFFFF},
                {0x1400F4FF, 0x0000C2FF, 0x0000FFFF, 0x2E26EEFF},
                {0x1F008FFF, 0x222C8AFF, 0x3F3FBFFF, 0x726ED2FF},
                {0x0C007CFF, 0x000063FF, 0x00007FFF, 0x161276FF},
                {0x0C0045FF, 0x08083DFF, 0x0F0F50FF, 0x211F52FF},
                {0x840084FF, 0x5A005AFF, 0x7F007FFF, 0x8E268EFF},
                {0x910091FF, 0x7C327CFF, 0xBF3FBFFF, 0xEA82EAFF},
                {0xEB00EBFF, 0x9C0B9CFF, 0xF500F5FF, 0xFF4BFFFF},
                {0x9B009DFF, 0x9D689FFF, 0xFD81FFFF, 0xFFDFFFFF},
                {0x7E3651FF, 0xAD8983FF, 0xFFC0CBFF, 0xFFFFFFFF},
                {0xA10F33FF, 0xB15E4BFF, 0xFF8181FF, 0xFFCCD1FF},
                {0xF40015FF, 0xC20000FF, 0xFF0000FF, 0xEE262FFF},
                {0x8F001FFF, 0x8A2C22FF, 0xBF3F3FFF, 0xD26E72FF},
                {0x7C000DFF, 0x630000FF, 0x7F0000FF, 0x761217FF},
                {0x46000EFF, 0x3F0B0BFF, 0x551414FF, 0x582628FF},
                {0x4F1700FF, 0x5F2B00FF, 0x7F3F00FF, 0x8A5A2BFF},
                {0x672F00FF, 0x8B5723FF, 0xBF7F3FFF, 0xE6B686FF},
                {0x9F2F00FF, 0xBF5700FF, 0xFF7F00FF, 0xFFB657FF},
                {0x7F4611FF, 0xB78351FF, 0xFFBF81FF, 0xFFFFE4FF},
                {0x626D2AFF, 0xB9A985FF, 0xFFFFBFFF, 0xFFFFFFFF},
                {0x619E00FF, 0xD09E01FF, 0xFFFF00FF, 0xFFFF7FFF},
                {0x456900FF, 0x917D29FF, 0xBFBF3FFF, 0xFAFE9AFF},
                {0x285700FF, 0x615600FF, 0x7F7F00FF, 0x9EA23FFF},
                {0x008300FF, 0x005900FF, 0x007F00FF, 0x278E27FF},
                {0x009100FF, 0x337C33FF, 0x3FBF3FFF, 0x82EA82FF},
                {0x00F200FF, 0x0EA10EFF, 0x00FF00FF, 0x4FFF4FFF},
                {0x1D841DFF, 0x85A285FF, 0xAFFFAFFF, 0xFFFFFFFF},
                {0x4D3A51FF, 0x7F7A83FF, 0xBCAFC0FF, 0xFFFFFFFF},
                {0x5C3F23FF, 0x8F745AFF, 0xCBAA89FF, 0xFFF4DCFF},
                {0x413E2EFF, 0x736C62FF, 0xA6A090FF, 0xE7E3D7FF},
                {0x233D37FF, 0x566468FF, 0x7E9494FF, 0xC0D0D1FF},
                {0x1E3534FF, 0x4A585FFF, 0x6E8287FF, 0xA9B8BCFF},
                {0x37281DFF, 0x584B40FF, 0x7E6E60FF, 0xAA9E94FF},
                {0x591A21FF, 0x70483CFF, 0xA0695FFF, 0xC79D97FF},
                {0x6E1A2AFF, 0x875448FF, 0xC07872FF, 0xEEB6B4FF},
                {0x722622FF, 0x936149FF, 0xD08A74FF, 0xFFCDBFFF},
                {0x782F21FF, 0x9F6C4EFF, 0xE19B7DFF, 0xFFE5D1FF},
                {0x783625FF, 0xA57658FF, 0xEBAA8CFF, 0xFFF8E3FF},
                {0x7A3E2CFF, 0xAD8164FF, 0xF5B99BFF, 0xFFFFFAFF},
                {0x724534FF, 0xAB8A72FF, 0xF6C8AFFF, 0xFFFFFFFF},
                {0x655347FF, 0xAA9B8EFF, 0xF5E1D2FF, 0xFFFFFFFF},
                {0x300C18FF, 0x3E2828FF, 0x573B3BFF, 0x6F585AFF},
                {0x470A17FF, 0x522C26FF, 0x73413CFF, 0x8B6462FF},
                {0x550F24FF, 0x643B36FF, 0x8E5555FF, 0xAF8385FF},
                {0x5F1B2EFF, 0x78504BFF, 0xAB7373FF, 0xDAAEB0FF},
                {0x682637FF, 0x8A645DFF, 0xC78F8FFF, 0xFFD6D8FF},
                {0x723241FF, 0x9D7970FF, 0xE3ABABFF, 0xFFFEFFFF},
                {0x6E4354FF, 0xA89390FF, 0xF8D2DAFF, 0xFFFFFFFF},
                {0x634A32FF, 0x9F8872FF, 0xE3C7ABFF, 0xFFFFFFFF},
                {0x5B3C15FF, 0x8C6C4BFF, 0xC49E73FF, 0xFDE1C1FF},
                {0x442B13FF, 0x664F39FF, 0x8F7357FF, 0xBAA590FF},
                {0x392008FF, 0x523B25FF, 0x73573BFF, 0x927D68FF},
                {0x1C1004FF, 0x291E13FF, 0x3B2D1FFF, 0x4B4036FF},
                {0x162200FF, 0x2E2D16FF, 0x414123FF, 0x565740FF},
                {0x283B00FF, 0x544E27FF, 0x73733BFF, 0x9B9C71FF},
                {0x344503FF, 0x67603AFF, 0x8F8F57FF, 0xC3C499FF},
                {0x3B5200FF, 0x766C38FF, 0xA2A255FF, 0xDADCA1FF},
                {0x425508FF, 0x83784DFF, 0xB5B572FF, 0xF7F9C5FF},
                {0x4B581AFF, 0x908563FF, 0xC7C78FFF, 0xFFFFE9FF},
                {0x535D2AFF, 0x9C9276FF, 0xDADAABFF, 0xFFFFFFFF},
                {0x5B613AFF, 0xA89F8AFF, 0xEDEDC7FF, 0xFFFFFFFF},
                {0x3E6926FF, 0x90957AFF, 0xC7E3ABFF, 0xFFFFFFFF},
                {0x315F19FF, 0x7C8466FF, 0xABC78FFF, 0xF9FFE4FF},
                {0x197000FF, 0x6A7B3CFF, 0x8EBE55FF, 0xCFF8A5FF},
                {0x194D01FF, 0x525F3CFF, 0x738F57FF, 0xA9C094FF},
                {0x084D00FF, 0x3F542AFF, 0x587D3EFF, 0x85A372FF},
                {0x132B02FF, 0x313721FF, 0x465032FF, 0x646C55FF},
                {0x041200FF, 0x111509FF, 0x191E0FFF, 0x23271CFF},
                {0x003503FF, 0x163727FF, 0x235037FF, 0x446753FF},
                {0x043204FF, 0x293C29FF, 0x3B573BFF, 0x5D735DFF},
                {0x113111FF, 0x374437FF, 0x506450FF, 0x798979FF},
                {0x004900FF, 0x274D33FF, 0x3B7349FF, 0x699574FF},
                {0x005700FF, 0x3F603FFF, 0x578F57FF, 0x8FBC8FFF},
                {0x0C5F0CFF, 0x537153FF, 0x73AB73FF, 0xB7E4B7FF},
                {0x00710FFF, 0x4A7D63FF, 0x64C082FF, 0xB1FAC8FF},
                {0x196719FF, 0x688268FF, 0x8FC78FFF, 0xDFFFDFFF},
                {0x226B22FF, 0x768D76FF, 0xA2D8A2FF, 0xFAFFFAFF},
                {0x49615FFF, 0x9BA8B0FF, 0xE1F8FAFF, 0xFFFFFFFF},
                {0x286E3CFF, 0x829C94FF, 0xB4EECAFF, 0xFFFFFFFF},
                {0x25683CFF, 0x79958FFF, 0xABE3C5FF, 0xFFFFFFFF},
                {0x1A5921FF, 0x607866FF, 0x87B48EFF, 0xD0F5D6FF},
                {0x024510FF, 0x375444FF, 0x507D5FFF, 0x83A78FFF},
                {0x005000FF, 0x064833FF, 0x0F6946FF, 0x3A8264FF},
                {0x011A06FF, 0x131F18FF, 0x1E2D23FF, 0x2F3B33FF},
                {0x00201AFF, 0x152C32FF, 0x234146FF, 0x41585CFF},
                {0x003B28FF, 0x264E54FF, 0x3B7373FF, 0x719C9BFF},
                {0x00533FFF, 0x43727DFF, 0x64ABABFF, 0xB2EAE8FF},
                {0x1A584BFF, 0x628590FF, 0x8FC7C7FF, 0xE9FFFFFF},
                {0x256156FF, 0x7697A4FF, 0xABE3E3FF, 0xFFFFFFFF},
                {0x37635CFF, 0x8AA2ADFF, 0xC7F1F1FF, 0xFFFFFFFF},
                {0x3D4C69FF, 0x7E90A7FF, 0xBED2F0FF, 0xFFFFFFFF},
                {0x324A63FF, 0x71889FFF, 0xABC7E3FF, 0xFFFFFFFF},
                {0x364064FF, 0x6E7F99FF, 0xA8B9DCFF, 0xFFFFFFFF},
                {0x284059FF, 0x5E758CFF, 0x8FABC7FF, 0xE0F5FFFF},
                {0x043566FF, 0x356290FF, 0x578FC7FF, 0xA3CDF7FF},
                {0x132B44FF, 0x384F66FF, 0x57738FFF, 0x90A5BAFF},
                {0x082039FF, 0x243B52FF, 0x3B5773FF, 0x687D92FF},
                {0x02051DFF, 0x091022FF, 0x0F192DFF, 0x1E2435FF},
                {0x0F0228FF, 0x15142CFF, 0x1F1F3BFF, 0x323047FF},
                {0x180C31FF, 0x28283FFF, 0x3B3B57FF, 0x5A586FFF},
                {0x1F0E44FF, 0x303253FF, 0x494973FF, 0x706E90FF},
                {0x240F55FF, 0x373C65FF, 0x57578FFF, 0x8886B2FF},
                {0x301761FF, 0x4A4D77FF, 0x736EAAFF, 0xADA8D7FF},
                {0x2F1579FF, 0x49538EFF, 0x7676CAFF, 0xBAB6F9FF},
                {0x372668FF, 0x5C648AFF, 0x8F8FC7FF, 0xD8D6FFFF},
                {0x413272FF, 0x6F799DFF, 0xABABE3FF, 0xFFFEFFFF},
                {0x494D6CFF, 0x8B97ACFF, 0xD0DAF8FF, 0xFFFFFFFF},
                {0x544E6DFF, 0x989EAFFF, 0xE3E3FFFF, 0xFFFFFFFF},
                {0x4F1F68FF, 0x706687FF, 0xAB8FC7FF, 0xF3DAFFFF},
                {0x580089FF, 0x5A4088FF, 0x8F57C7FF, 0xC797F1FF},
                {0x3E0757FF, 0x4D3D64FF, 0x73578FFF, 0xA38AB8FF},
                {0x35004EFF, 0x3B2952FF, 0x573B73FF, 0x7B6290FF},
                {0x270027FF, 0x2A162AFF, 0x3C233CFF, 0x4E394EFF},
                {0x280528FF, 0x312231FF, 0x463246FF, 0x5F4E5FFF},
                {0x4A004AFF, 0x4E2C4EFF, 0x724072FF, 0x946B94FF},
                {0x570057FF, 0x603E60FF, 0x8F578FFF, 0xBC8EBCFF},
                {0x700070FF, 0x714071FF, 0xAB57ABFF, 0xDC97DCFF},
                {0x5F0B5FFF, 0x725372FF, 0xAB73ABFF, 0xE4B6E4FF},
                {0x76256EFF, 0x9A7E92FF, 0xEBACE1FF, 0xFFFFFFFF},
                {0x6F4667FF, 0xAA9CA2FF, 0xFFDCF5FF, 0xFFFFFFFF},
                {0x623D62FF, 0x988D98FF, 0xE3C7E3FF, 0xFFFFFFFF},
                {0x68365BFF, 0x97848BFF, 0xE1B9D2FF, 0xFFFFFFFF},
                {0x6C2657FF, 0x90737CFF, 0xD7A0BEFF, 0xFFF3FFFF},
                {0x671B5BFF, 0x846679FF, 0xC78FB9FF, 0xFFDCFFFF},
                {0x741151FF, 0x875A67FF, 0xC87DA0FF, 0xFFC4E2FF},
                {0x830058FF, 0x84425CFF, 0xC35A91FF, 0xEE9AC9FF},
                {0x320021FF, 0x351A25FF, 0x4B2837FF, 0x5D404EFF},
                {0x250018FF, 0x240E18FF, 0x321623FF, 0x3D2632FF},
                {0x23001BFF, 0x1E0516FF, 0x280A1EFF, 0x2E1527FF},
                {0x300007FF, 0x30100AFF, 0x401811FF, 0x472824FF},
                {0x4F0000FF, 0x4A0E00FF, 0x621800FF, 0x63291AFF},
                {0x92000BFF, 0x7D0B00FF, 0xA5140AFF, 0xA2312EFF},
                {0xBB000BFF, 0xA21400FF, 0xDA2010FF, 0xD94742FF},
                {0x97001EFF, 0x983A28FF, 0xD5524AFF, 0xEE8886FF},
                {0xCA0000FF, 0xBE2B00FF, 0xFF3C0AFF, 0xFF6D4DFF},
                {0xAE0004FF, 0xB24014FF, 0xF55A32FF, 0xFF9278FF},
                {0xB3002AFF, 0xB44835FF, 0xFF6262FF, 0xFFA4AAFF},
                {0x785D00FF, 0xB97D19FF, 0xF6BD31FF, 0xFFFF97FF},
                {0x8C4200FF, 0xBC701EFF, 0xFFA53CFF, 0xFFEA9BFF},
                {0x6C5000FF, 0xA26700FF, 0xD79B0FFF, 0xFDD267FF},
                {0x892700FF, 0xA44C00FF, 0xDA6E0AFF, 0xF19F55FF},
                {0x702100FF, 0x863D00FF, 0xB45A00FF, 0xC5813EFF},
                {0x671700FF, 0x773300FF, 0xA04B05FF, 0xAD6D39FF},
                {0x3B0D00FF, 0x452109FF, 0x5F3214FF, 0x6B4933FF},
                {0x1A3400FF, 0x3D3702FF, 0x53500AFF, 0x686832FF},
                {0x1D4400FF, 0x494300FF, 0x626200FF, 0x7A7C31FF},
                {0x39370EFF, 0x64573CFF, 0x8C805AFF, 0xBBB296FF},
                {0x465B00FF, 0x846300FF, 0xAC9400FF, 0xD0C14FFF},
                {0x3C7100FF, 0x897402FF, 0xB1B10AFF, 0xDFE462FF},
                {0x5E6900FF, 0xAD8B3CFF, 0xE6D55AFF, 0xFFFFC2FF},
                {0x747800FF, 0xC68904FF, 0xFFD510FF, 0xFFFF82FF},
                {0x6B7900FF, 0xC49631FF, 0xFFEA4AFF, 0xFFFFBFFF},
                {0x319B00FF, 0xA19E34FF, 0xC8FF41FF, 0xFFFFB2FF},
                {0x0E9B00FF, 0x7F973AFF, 0x9BF046FF, 0xE9FFAAFF},
                {0x109A00FF, 0x7A8B15FF, 0x96DC19FF, 0xD4FF77FF},
                {0x009B00FF, 0x5F8106FF, 0x73C805FF, 0xABF359FF},
                {0x008200FF, 0x536F01FF, 0x6AA805FF, 0x98CD4DFF},
                {0x005600FF, 0x2B4C0BFF, 0x3C6E14FF, 0x5E8740FF},
                {0x022800FF, 0x1C2500FF, 0x283405FF, 0x36411CFF},
                {0x003C00FF, 0x153102FF, 0x204608FF, 0x355423FF},
                {0x005800FF, 0x054105FF, 0x0C5C0CFF, 0x2A6B2AFF},
                {0x009000FF, 0x0E6602FF, 0x149605FF, 0x42AC37FF},
                {0x00CB00FF, 0x0F8C0FFF, 0x0AD70AFF, 0x4EF44EFF},
                {0x00D300FF, 0x189310FF, 0x14E60AFF, 0x5CFF55FF},
                {0x00A200FF, 0x679E5FFF, 0x7DFF73FF, 0xDAFFD3FF},
                {0x00AD00FF, 0x41954EFF, 0x4BF05AFF, 0xA1FFADFF},
                {0x00BB00FF, 0x038114FF, 0x00C514FF, 0x42E051FF},
                {0x009600FF, 0x047641FF, 0x05B450FF, 0x4BD784FF},
                {0x006A00FF, 0x125E3BFF, 0x1C8C4EFF, 0x54AD7AFF},
                {0x00230BFF, 0x092623FF, 0x123832FF, 0x2C4944FF},
                {0x00681EFF, 0x086562FF, 0x129880FF, 0x58C1ABFF},
                {0x008D14FF, 0x037E74FF, 0x06C491FF, 0x5DF3C6FF},
                {0x00B200FF, 0x058D5CFF, 0x00DE6AFF, 0x58FFA8FF},
                {0x009A1AFF, 0x249388FF, 0x2DEBA8FF, 0x92FFEFFF},
                {0x00A611FF, 0x339D89FF, 0x3CFEA5FF, 0xA7FFF6FF},
                {0x008E37FF, 0x51A0A2FF, 0x6AFFCDFF, 0xD9FFFFFF},
                {0x0B676CFF, 0x629BBCFF, 0x91EBFFFF, 0xFFFFFFFF},
                {0x00736FFF, 0x3895C3FF, 0x55E6FFFF, 0xC7FFFFFF},
                {0x036068FF, 0x538FB1FF, 0x7DD7F0FF, 0xE4FFFFFF},
                {0x008E49FF, 0x048DABFF, 0x08DED5FF, 0x74FFFFFF},
                {0x004F73FF, 0x0068A8FF, 0x109CDEFF, 0x68D3FFFF},
                {0x003D1DFF, 0x003E44FF, 0x055A5CFF, 0x317473FF},
                {0x000B34FF, 0x0D1E3EFF, 0x162C52FF, 0x334260FF},
                {0x000A55FF, 0x03255DFF, 0x0F377DFF, 0x37538AFF},
                {0x001965FF, 0x003376FF, 0x004A9CFF, 0x356BAAFF},
                {0x002551FF, 0x1B446DFF, 0x326496FF, 0x6A8FB5FF},
                {0x0005B3FF, 0x0039B8FF, 0x0052F6FF, 0x4781FFFF},
                {0x002770FF, 0x05488CFF, 0x186ABDFF, 0x5A98D6FF},
                {0x002985FF, 0x0C52A3FF, 0x2378DCFF, 0x6EADF9FF},
                {0x0D3E5CFF, 0x436B8DFF, 0x699DC3FF, 0xB7DEFBFF},
                {0x003D8DFF, 0x2670BAFF, 0x4AA4FFFF, 0xA7EBFFFF},
                {0x253687FF, 0x597BB4FF, 0x90B0FFFF, 0xECFFFFFF},
                {0x00567DFF, 0x3684BDFF, 0x5AC5FFFF, 0xC0FFFFFF},
                {0x49347EFF, 0x7A83ABFF, 0xBEB9FAFF, 0xFFFFFFFF},
                {0x00697DFF, 0x007DC5FF, 0x00BFFFFF, 0x6AFEFFFF},
                {0x002F9FFF, 0x0057BFFF, 0x007FFFFF, 0x56B6FFFF},
                {0x042872FF, 0x2B5791FF, 0x4B7DC8FF, 0x92B7F0FF},
                {0x36029FFF, 0x4550A7FF, 0x786EF0FF, 0xC0B4FFFF},
                {0x1900B8FF, 0x2342B7FF, 0x4A5AFFFF, 0x9198FFFF},
                {0x3F00C1FF, 0x3631AFFF, 0x6241F6FF, 0x9E7EFFFF},
                {0x2000C2FF, 0x1A2BB1FF, 0x3C3CF5FF, 0x7973FFFF},
                {0x0E00BFFF, 0x0012A4FF, 0x101CDAFF, 0x4043D8FF},
                {0x0500ABFF, 0x000890FF, 0x0010BDFF, 0x282FB6FF},
                {0x240087FF, 0x13086FFF, 0x231094FF, 0x412E96FF},
                {0x000531FF, 0x051536FF, 0x0C2148FF, 0x233150FF},
                {0x4F00A3FF, 0x320980FF, 0x5010B0FF, 0x713AB9FF},
                {0x5F00C1FF, 0x3B0B96FF, 0x6010D0FF, 0x8641DAFF},
                {0x6900ABFF, 0x542691FF, 0x8732D2FF, 0xB56DEEFF},
                {0x7100C8FF, 0x5C35ADFF, 0x9C41FFFF, 0xD689FFFF},
                {0x8600F6FF, 0x4C03B4FF, 0x7F00FFFF, 0xA63AFFFF},
                {0x7900B3FF, 0x724FA8FF, 0xBD62FFFF, 0xFFB4FFFF},
                {0x591097FF, 0x726CABFF, 0xB991FFFF, 0xFFE7FFFF},
                {0x691D8CFF, 0x887BA9FF, 0xD7A5FFFF, 0xFFFFFFFF},
                {0x583777FF, 0x8B8BA8FF, 0xD7C3FAFF, 0xFFFFFFFF},
                {0x733377FF, 0xA291A6FF, 0xF8C6FCFF, 0xFFFFFFFF},
                {0x9000A6FF, 0x8E5DA3FF, 0xE673FFFF, 0xFFCBFFFF},
                {0xBA00BAFF, 0x9D499DFF, 0xFF52FFFF, 0xFFABFFFF},
                {0xBD00C3FF, 0x8B1F90FF, 0xDA20E0FF, 0xFF68FFFF},
                {0x9E00D8FF, 0x7327A9FF, 0xBD29FFFF, 0xED72FFFF},
                {0xB100B8FF, 0x7C0F83FF, 0xBD10C5FF, 0xDA4DE0FF},
                {0x8200AEFF, 0x5B0F84FF, 0x8C14BEFF, 0xAC49D2FF},
                {0x50006DFF, 0x3D0E58FF, 0x5A187BFF, 0x733C8CFF},
                {0x5B005BFF, 0x470C47FF, 0x641464FF, 0x773677FF},
                {0x490066FF, 0x2E0049FF, 0x410062FF, 0x4E1867FF},
                {0x310043FF, 0x240435FF, 0x320A46FF, 0x401E4FFF},
                {0x46002CFF, 0x3E0F26FF, 0x551937FF, 0x61314BFF},
                {0x8F0075FF, 0x6F1157FF, 0xA01982FF, 0xB649A0FF},
                {0xC60080FF, 0x8F004EFF, 0xC80078FF, 0xD23296FF},
                {0xBD0085FF, 0xA74273FF, 0xFF50BFFF, 0xFF9FFCFF},
                {0xAD007BFF, 0xA85579FF, 0xFF6AC5FF, 0xFFBDFFFF},
                {0x891F51FF, 0xA87574FF, 0xFAA0B9FF, 0xFFF7FFFF},
                {0xCA0068FF, 0xAD2F52FF, 0xFC3A8CFF, 0xFF7EC5FF},
                {0xCA006AFF, 0xA21849FF, 0xE61E78FF, 0xF657A4FF},
                {0xAE003BFF, 0x8C0921FF, 0xBD1039FF, 0xC1385EFF},
                {0x740033FF, 0x6D2431FF, 0x98344DFF, 0xAD5E75FF},
                {0x820034FF, 0x6C0C23FF, 0x911437FF, 0x993656FF},
        };

//        {
//            StringBuilder sb = new StringBuilder(1024).append("{\n{ 0x00000000, 0x00000000, 0x00000000, 0x00000000 },\n");
//            for (int i = 1; i < 256; i++) {
//                int color = RAMP_VALUES[i][2] = Coloring.AURORA[i],
//                        r = (color >>> 24),
//                        g = (color >>> 16 & 0xFF),
//                        b = (color >>> 8 & 0xFF);
//                int co = r - b, t = b + (co >> 1), cg = g - t, y = t + (cg >> 1),
//                        yBright = y * 23 >> 4, yDim = y * 11 >> 4, yDark = y * 6 >> 4, chromO, chromG;
//                chromO = (co * 13) >> 4;
//                chromG = (cg * (256 - yDim) * 14) >> 12;
//                t = yDim - (chromG >> 1);
//                g = chromG + t;
//                b = t - (chromO >> 1);
//                r = b + chromO;
//                RAMP_VALUES[i][1] =
//                        MathUtils.clamp(r, 0, 255) << 24 |
//                                MathUtils.clamp(g, 0, 255) << 16 |
//                                MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//                chromO = (co * 12) >> 4;
//                chromG = (cg * 13) >> 4;
//                t = yBright - (chromG >> 1);
//                g = chromG + t;
//                b = t - (chromO >> 1);
//                r = b + chromO;
//                RAMP_VALUES[i][3] =
//                        MathUtils.clamp(r, 0, 255) << 24 |
//                                MathUtils.clamp(g, 0, 255) << 16 |
//                                MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//                chromO = (co * 14) >> 4;
//                chromG = (cg * (256 - yDark) * 15) >> 11;
//                t = yDark - (chromG >> 1);
//                g = chromG + t;
//                b = t - (chromO >> 1);
//                r = b + chromO;
//                RAMP_VALUES[i][0] =
//                        MathUtils.clamp(r, 0, 255) << 24 |
//                                MathUtils.clamp(g, 0, 255) << 16 |
//                                MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//                sb.append("{ 0x");
//                StringKit.appendHex(sb, RAMP_VALUES[i][0]);
//                StringKit.appendHex(sb.append(", 0x"), RAMP_VALUES[i][1]);
//                StringKit.appendHex(sb.append(", 0x"), RAMP_VALUES[i][2]);
//                StringKit.appendHex(sb.append(", 0x"), RAMP_VALUES[i][3]);
//                sb.append(" },\n");
//            }
//            System.out.println(sb.append("};"));
//        }

        @Override
        public int dimmer(int brightness, byte voxel) {
            return RAMP_VALUES[voxel & 255][
                    brightness <= 0
                            ? 0
                            : Math.min(brightness, 3)
                    ];
        }

    };

    public static final int[] FlesurrectBonusPalette = new int[256];
    private static final int[][] FLESURRECT_BONUS_RAMP_VALUES = new int[][]{
            {0x00000000, 0x00000000, 0x00000000, 0x00000000},
            {0x0A0A23FF, 0x11112AFF, 0x1F1833FF, 0x1F1F38FF},
            {0x12122BFF, 0x1D1D36FF, 0x2B2E42FF, 0x33334CFF},
            {0x23122BFF, 0x312039FF, 0x3E3546FF, 0x4C3C55FF},
            {0x1E1E37FF, 0x2F2F48FF, 0x414859FF, 0x51516AFF},
            {0x2A3A43FF, 0x45555EFF, 0x68717AFF, 0x7B8B93FF},
            {0x405059FF, 0x66767EFF, 0x90A1A8FF, 0xB1C2CAFF},
            {0x54646CFF, 0x84949CFF, 0xB6CBCFFF, 0xE3F4FCFF},
            {0x607078FF, 0x96A6AEFF, 0xD3E5EDFF, 0xFFFFFFFF},
            {0x7A7A82FF, 0xB8B8C0FF, 0xFFFFFFFF, 0xFFFFFFFF},
            {0x33121AFF, 0x43222AFF, 0x5C3A41FF, 0x63424AFF},
            {0x47263FFF, 0x624159FF, 0x826481FF, 0x97778FFF},
            {0x57262EFF, 0x74434BFF, 0x966C6CFF, 0xAE7C85FF},
            {0x392820FF, 0x503F37FF, 0x715A56FF, 0x7D6D65FF},
            {0x5F3E25FF, 0x836249FF, 0xAB947AFF, 0xCAAA91FF},
            {0x9E1B23FF, 0xC5424AFF, 0xF68181FF, 0xFF8F98FF},
            {0xB40000FF, 0xCC0000FF, 0xF53333FF, 0xFC262EFF},
            {0x420000FF, 0x490000FF, 0x5A0A07FF, 0x570500FF},
            {0x740100FF, 0x8A170FFF, 0xAE4539FF, 0xB6433BFF},
            {0x4D1C03FF, 0x633219FF, 0x8A503EFF, 0x8F5E45FF},
            {0x7C1900FF, 0x99360DFF, 0xCD683DFF, 0xD37047FF},
            {0x983500FF, 0xC15E14FF, 0xFBA458FF, 0xFFB066FF},
            {0xA10D00FF, 0xBF2B00FF, 0xFB6B1DFF, 0xFB671DFF},
            {0x735239FF, 0xA18067FF, 0xDDBBA4FF, 0xFCDBC3FF},
            {0x8B5A31FF, 0xBF8E64FF, 0xFDD7AAFF, 0xFFF5CCFF},
            {0x903E00FF, 0xB56300FF, 0xFFA514FF, 0xFFAC10FF},
            {0x69380FFF, 0x8C5B32FF, 0xC29162FF, 0xD2A077FF},
            {0x7E4C00FF, 0xA37100FF, 0xE8B710FF, 0xECBB0EFF},
            {0x876600FF, 0xB59400FF, 0xFBE626FF, 0xFFF033FF},
            {0x5B5B00FF, 0x7E7E00FF, 0xC0B510FF, 0xC3C317FF},
            {0x7B7B00FF, 0xB0B035FF, 0xFBFF86FF, 0xFFFFA0FF},
            {0x467700FF, 0x6FA004FF, 0xB4D645FF, 0xC0F255FF},
            {0x2C4D03FF, 0x48691FFF, 0x729446FF, 0x80A157FF},
            {0x56774DFF, 0x88A980FF, 0xC8E4BEFF, 0xEEFFE6FF},
            {0x00A100FF, 0x0DC200FF, 0x45F520FF, 0x4FFF25FF},
            {0x077A00FF, 0x26990DFF, 0x51C43FFF, 0x63D74BFF},
            {0x003300FF, 0x003C00FF, 0x0E4904FF, 0x0C4E04FF},
            {0x009227FF, 0x25B94EFF, 0x55F084FF, 0x73FF9CFF},
            {0x007E34FF, 0x009A50FF, 0x1EBC73FF, 0x2DD288FF},
            {0x009269FF, 0x02B78EFF, 0x30E1B9FF, 0x4CFFD8FF},
            {0x1D7F67FF, 0x48AB92FF, 0x7FE0C2FF, 0xA0FFEAFF},
            {0x45878FFF, 0x7DBEC7FF, 0xB8FDFFFF, 0xECFFFFFF},
            {0x007047FF, 0x00875EFF, 0x039F78FF, 0x10B58CFF},
            {0x0E7179FF, 0x3598A0FF, 0x63C2C9FF, 0x83E6EEFF},
            {0x00435CFF, 0x055770FF, 0x216981FF, 0x2D7F98FF},
            {0x168992FF, 0x46B9C1FF, 0x7FE8F2FF, 0xA6FFFFFF},
            {0x1C2CB8FF, 0x3748D4FF, 0x5369EFFF, 0x6F80FFFF},
            {0x0557A1FF, 0x2578C2FF, 0x4D9BE6FF, 0x67B9FFFF},
            {0x081852FF, 0x15255FFF, 0x28306FFF, 0x2F3F79FF},
            {0x1B3C86FF, 0x3859A3FF, 0x5C76BFFF, 0x7293DDFF},
            {0x1C1C97FF, 0x3232ADFF, 0x4D44C0FF, 0x5E5ED9FF},
            {0x0600C3FF, 0x1100CEFF, 0x180FCFFF, 0x2716E4FF},
            {0x31005AFF, 0x400F69FF, 0x53207DFF, 0x5E2C87FF},
            {0x491793FF, 0x6634B0FF, 0x8657CCFF, 0x9F6EE9FF},
            {0x54339EFF, 0x7B5AC5FF, 0xA884F3FF, 0xC9A8FFFF},
            {0x400048FF, 0x4B0054FF, 0x630867FF, 0x63116CFF},
            {0x64017CFF, 0x7E1C97FF, 0xA03EB2FF, 0xB452CDFF},
            {0x5C0095FF, 0x7100AAFF, 0x881AC4FF, 0x9A27D4FF},
            {0x7D3B96FF, 0xAD6BC5FF, 0xE4A8FAFF, 0xFFCBFFFF},
            {0x720049FF, 0x8C1963FF, 0xB53D86FF, 0xC04D97FF},
            {0x9C0094FF, 0xC11DB9FF, 0xF34FE9FF, 0xFF67FFFF},
            {0x520018FF, 0x631029FF, 0x7A3045FF, 0x84324BFF},
            {0x9F0024FF, 0xBE1942FF, 0xF04F78FF, 0xFC5780FF},
            {0x8F0000FF, 0xA40008FF, 0xC93038FF, 0xCE2932FF},

            {0x00000000, 0x00000000, 0x00000000, 0x00000000},
            {0x11112AFF, 0x1F1833FF, 0x1F1833FF, 0x1F1833FF},
            {0x1D1D36FF, 0x2B2E42FF, 0x2B2E42FF, 0x2B2E42FF},
            {0x312039FF, 0x3E3546FF, 0x3E3546FF, 0x3E3546FF},
            {0x2F2F48FF, 0x414859FF, 0x414859FF, 0x414859FF},
            {0x45555EFF, 0x68717AFF, 0x68717AFF, 0x68717AFF},
            {0x66767EFF, 0x90A1A8FF, 0x90A1A8FF, 0x90A1A8FF},
            {0x84949CFF, 0xB6CBCFFF, 0xB6CBCFFF, 0xB6CBCFFF},
            {0x96A6AEFF, 0xD3E5EDFF, 0xD3E5EDFF, 0xD3E5EDFF},
            {0xB8B8C0FF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF},
            {0x43222AFF, 0x5C3A41FF, 0x5C3A41FF, 0x5C3A41FF},
            {0x624159FF, 0x826481FF, 0x826481FF, 0x826481FF},
            {0x74434BFF, 0x966C6CFF, 0x966C6CFF, 0x966C6CFF},
            {0x503F37FF, 0x715A56FF, 0x715A56FF, 0x715A56FF},
            {0x836249FF, 0xAB947AFF, 0xAB947AFF, 0xAB947AFF},
            {0xC5424AFF, 0xF68181FF, 0xF68181FF, 0xF68181FF},
            {0xCC0000FF, 0xF53333FF, 0xF53333FF, 0xF53333FF},
            {0x490000FF, 0x5A0A07FF, 0x5A0A07FF, 0x5A0A07FF},
            {0x8A170FFF, 0xAE4539FF, 0xAE4539FF, 0xAE4539FF},
            {0x633219FF, 0x8A503EFF, 0x8A503EFF, 0x8A503EFF},
            {0x99360DFF, 0xCD683DFF, 0xCD683DFF, 0xCD683DFF},
            {0xC15E14FF, 0xFBA458FF, 0xFBA458FF, 0xFBA458FF},
            {0xBF2B00FF, 0xFB6B1DFF, 0xFB6B1DFF, 0xFB6B1DFF},
            {0xA18067FF, 0xDDBBA4FF, 0xDDBBA4FF, 0xDDBBA4FF},
            {0xBF8E64FF, 0xFDD7AAFF, 0xFDD7AAFF, 0xFDD7AAFF},
            {0xB56300FF, 0xFFA514FF, 0xFFA514FF, 0xFFA514FF},
            {0x8C5B32FF, 0xC29162FF, 0xC29162FF, 0xC29162FF},
            {0xA37100FF, 0xE8B710FF, 0xE8B710FF, 0xE8B710FF},
            {0xB59400FF, 0xFBE626FF, 0xFBE626FF, 0xFBE626FF},
            {0x7E7E00FF, 0xC0B510FF, 0xC0B510FF, 0xC0B510FF},
            {0xB0B035FF, 0xFBFF86FF, 0xFBFF86FF, 0xFBFF86FF},
            {0x6FA004FF, 0xB4D645FF, 0xB4D645FF, 0xB4D645FF},
            {0x48691FFF, 0x729446FF, 0x729446FF, 0x729446FF},
            {0x88A980FF, 0xC8E4BEFF, 0xC8E4BEFF, 0xC8E4BEFF},
            {0x0DC200FF, 0x45F520FF, 0x45F520FF, 0x45F520FF},
            {0x26990DFF, 0x51C43FFF, 0x51C43FFF, 0x51C43FFF},
            {0x003C00FF, 0x0E4904FF, 0x0E4904FF, 0x0E4904FF},
            {0x25B94EFF, 0x55F084FF, 0x55F084FF, 0x55F084FF},
            {0x009A50FF, 0x1EBC73FF, 0x1EBC73FF, 0x1EBC73FF},
            {0x02B78EFF, 0x30E1B9FF, 0x30E1B9FF, 0x30E1B9FF},
            {0x48AB92FF, 0x7FE0C2FF, 0x7FE0C2FF, 0x7FE0C2FF},
            {0x7DBEC7FF, 0xB8FDFFFF, 0xB8FDFFFF, 0xB8FDFFFF},
            {0x00875EFF, 0x039F78FF, 0x039F78FF, 0x039F78FF},
            {0x3598A0FF, 0x63C2C9FF, 0x63C2C9FF, 0x63C2C9FF},
            {0x055770FF, 0x216981FF, 0x216981FF, 0x216981FF},
            {0x46B9C1FF, 0x7FE8F2FF, 0x7FE8F2FF, 0x7FE8F2FF},
            {0x3748D4FF, 0x5369EFFF, 0x5369EFFF, 0x5369EFFF},
            {0x2578C2FF, 0x4D9BE6FF, 0x4D9BE6FF, 0x4D9BE6FF},
            {0x15255FFF, 0x28306FFF, 0x28306FFF, 0x28306FFF},
            {0x3859A3FF, 0x5C76BFFF, 0x5C76BFFF, 0x5C76BFFF},
            {0x3232ADFF, 0x4D44C0FF, 0x4D44C0FF, 0x4D44C0FF},
            {0x1100CEFF, 0x180FCFFF, 0x180FCFFF, 0x180FCFFF},
            {0x400F69FF, 0x53207DFF, 0x53207DFF, 0x53207DFF},
            {0x6634B0FF, 0x8657CCFF, 0x8657CCFF, 0x8657CCFF},
            {0x7B5AC5FF, 0xA884F3FF, 0xA884F3FF, 0xA884F3FF},
            {0x4B0054FF, 0x630867FF, 0x630867FF, 0x630867FF},
            {0x7E1C97FF, 0xA03EB2FF, 0xA03EB2FF, 0xA03EB2FF},
            {0x7100AAFF, 0x881AC4FF, 0x881AC4FF, 0x881AC4FF},
            {0xAD6BC5FF, 0xE4A8FAFF, 0xE4A8FAFF, 0xE4A8FAFF},
            {0x8C1963FF, 0xB53D86FF, 0xB53D86FF, 0xB53D86FF},
            {0xC11DB9FF, 0xF34FE9FF, 0xF34FE9FF, 0xF34FE9FF},
            {0x631029FF, 0x7A3045FF, 0x7A3045FF, 0x7A3045FF},
            {0xBE1942FF, 0xF04F78FF, 0xF04F78FF, 0xF04F78FF},
            {0xA40008FF, 0xC93038FF, 0xC93038FF, 0xC93038FF},

            {0x00000000, 0x00000000, 0x00000000, 0x00000000},
            {0x0A0A23FF, 0x11112AFF, 0x1F1833FF, 0x1F1F38FF},
            {0x12122BFF, 0x1D1D36FF, 0x2B2E42FF, 0x33334CFF},
            {0x23122BFF, 0x312039FF, 0x3E3546FF, 0x4C3C55FF},
            {0x1E1E37FF, 0x2F2F48FF, 0x414859FF, 0x51516AFF},
            {0x2A3A43FF, 0x45555EFF, 0x68717AFF, 0x7B8B93FF},
            {0x405059FF, 0x66767EFF, 0x90A1A8FF, 0xB1C2CAFF},
            {0x54646CFF, 0x84949CFF, 0xB6CBCFFF, 0xE3F4FCFF},
            {0x607078FF, 0x96A6AEFF, 0xD3E5EDFF, 0xFFFFFFFF},
            {0x7A7A82FF, 0xB8B8C0FF, 0xFFFFFFFF, 0xFFFFFFFF},
            {0x33121AFF, 0x43222AFF, 0x5C3A41FF, 0x63424AFF},
            {0x47263FFF, 0x624159FF, 0x826481FF, 0x97778FFF},
            {0x57262EFF, 0x74434BFF, 0x966C6CFF, 0xAE7C85FF},
            {0x392820FF, 0x503F37FF, 0x715A56FF, 0x7D6D65FF},
            {0x5F3E25FF, 0x836249FF, 0xAB947AFF, 0xCAAA91FF},
            {0x9E1B23FF, 0xC5424AFF, 0xF68181FF, 0xFF8F98FF},
            {0xB40000FF, 0xCC0000FF, 0xF53333FF, 0xFC262EFF},
            {0x420000FF, 0x490000FF, 0x5A0A07FF, 0x570500FF},
            {0x740100FF, 0x8A170FFF, 0xAE4539FF, 0xB6433BFF},
            {0x4D1C03FF, 0x633219FF, 0x8A503EFF, 0x8F5E45FF},
            {0x7C1900FF, 0x99360DFF, 0xCD683DFF, 0xD37047FF},
            {0x983500FF, 0xC15E14FF, 0xFBA458FF, 0xFFB066FF},
            {0xA10D00FF, 0xBF2B00FF, 0xFB6B1DFF, 0xFB671DFF},
            {0x735239FF, 0xA18067FF, 0xDDBBA4FF, 0xFCDBC3FF},
            {0x8B5A31FF, 0xBF8E64FF, 0xFDD7AAFF, 0xFFF5CCFF},
            {0x903E00FF, 0xB56300FF, 0xFFA514FF, 0xFFAC10FF},
            {0x69380FFF, 0x8C5B32FF, 0xC29162FF, 0xD2A077FF},
            {0x7E4C00FF, 0xA37100FF, 0xE8B710FF, 0xECBB0EFF},
            {0x876600FF, 0xB59400FF, 0xFBE626FF, 0xFFF033FF},
            {0x5B5B00FF, 0x7E7E00FF, 0xC0B510FF, 0xC3C317FF},
            {0x7B7B00FF, 0xB0B035FF, 0xFBFF86FF, 0xFFFFA0FF},
            {0x467700FF, 0x6FA004FF, 0xB4D645FF, 0xC0F255FF},
            {0x2C4D03FF, 0x48691FFF, 0x729446FF, 0x80A157FF},
            {0x56774DFF, 0x88A980FF, 0xC8E4BEFF, 0xEEFFE6FF},
            {0x00A100FF, 0x0DC200FF, 0x45F520FF, 0x4FFF25FF},
            {0x077A00FF, 0x26990DFF, 0x51C43FFF, 0x63D74BFF},
            {0x003300FF, 0x003C00FF, 0x0E4904FF, 0x0C4E04FF},
            {0x009227FF, 0x25B94EFF, 0x55F084FF, 0x73FF9CFF},
            {0x007E34FF, 0x009A50FF, 0x1EBC73FF, 0x2DD288FF},
            {0x009269FF, 0x02B78EFF, 0x30E1B9FF, 0x4CFFD8FF},
            {0x1D7F67FF, 0x48AB92FF, 0x7FE0C2FF, 0xA0FFEAFF},
            {0x45878FFF, 0x7DBEC7FF, 0xB8FDFFFF, 0xECFFFFFF},
            {0x007047FF, 0x00875EFF, 0x039F78FF, 0x10B58CFF},
            {0x0E7179FF, 0x3598A0FF, 0x63C2C9FF, 0x83E6EEFF},
            {0x00435CFF, 0x055770FF, 0x216981FF, 0x2D7F98FF},
            {0x168992FF, 0x46B9C1FF, 0x7FE8F2FF, 0xA6FFFFFF},
            {0x1C2CB8FF, 0x3748D4FF, 0x5369EFFF, 0x6F80FFFF},
            {0x0557A1FF, 0x2578C2FF, 0x4D9BE6FF, 0x67B9FFFF},
            {0x081852FF, 0x15255FFF, 0x28306FFF, 0x2F3F79FF},
            {0x1B3C86FF, 0x3859A3FF, 0x5C76BFFF, 0x7293DDFF},
            {0x1C1C97FF, 0x3232ADFF, 0x4D44C0FF, 0x5E5ED9FF},
            {0x0600C3FF, 0x1100CEFF, 0x180FCFFF, 0x2716E4FF},
            {0x31005AFF, 0x400F69FF, 0x53207DFF, 0x5E2C87FF},
            {0x491793FF, 0x6634B0FF, 0x8657CCFF, 0x9F6EE9FF},
            {0x54339EFF, 0x7B5AC5FF, 0xA884F3FF, 0xC9A8FFFF},
            {0x400048FF, 0x4B0054FF, 0x630867FF, 0x63116CFF},
            {0x64017CFF, 0x7E1C97FF, 0xA03EB2FF, 0xB452CDFF},
            {0x5C0095FF, 0x7100AAFF, 0x881AC4FF, 0x9A27D4FF},
            {0x7D3B96FF, 0xAD6BC5FF, 0xE4A8FAFF, 0xFFCBFFFF},
            {0x720049FF, 0x8C1963FF, 0xB53D86FF, 0xC04D97FF},
            {0x9C0094FF, 0xC11DB9FF, 0xF34FE9FF, 0xFF67FFFF},
            {0x520018FF, 0x631029FF, 0x7A3045FF, 0x84324BFF},
            {0x9F0024FF, 0xBE1942FF, 0xF04F78FF, 0xFC5780FF},
            {0x8F0000FF, 0xA40008FF, 0xC93038FF, 0xCE2932FF},

            {0x00000000, 0x00000000, 0x00000000, 0x00000000},
            {0x1F1833FF, 0x11112AFF, 0x1F1833FF, 0x1F1F38FF},
            {0x2B2E42FF, 0x1D1D36FF, 0x2B2E42FF, 0x33334CFF},
            {0x3E3546FF, 0x312039FF, 0x3E3546FF, 0x4C3C55FF},
            {0x414859FF, 0x2F2F48FF, 0x414859FF, 0x51516AFF},
            {0x68717AFF, 0x45555EFF, 0x68717AFF, 0x7B8B93FF},
            {0x90A1A8FF, 0x66767EFF, 0x90A1A8FF, 0xB1C2CAFF},
            {0xB6CBCFFF, 0x84949CFF, 0xB6CBCFFF, 0xE3F4FCFF},
            {0xD3E5EDFF, 0x96A6AEFF, 0xD3E5EDFF, 0xFFFFFFFF},
            {0xFFFFFFFF, 0xB8B8C0FF, 0xFFFFFFFF, 0xFFFFFFFF},
            {0x5C3A41FF, 0x43222AFF, 0x5C3A41FF, 0x63424AFF},
            {0x826481FF, 0x624159FF, 0x826481FF, 0x97778FFF},
            {0x966C6CFF, 0x74434BFF, 0x966C6CFF, 0xAE7C85FF},
            {0x715A56FF, 0x503F37FF, 0x715A56FF, 0x7D6D65FF},
            {0xAB947AFF, 0x836249FF, 0xAB947AFF, 0xCAAA91FF},
            {0xF68181FF, 0xC5424AFF, 0xF68181FF, 0xFF8F98FF},
            {0xF53333FF, 0xCC0000FF, 0xF53333FF, 0xFC262EFF},
            {0x5A0A07FF, 0x490000FF, 0x5A0A07FF, 0x570500FF},
            {0xAE4539FF, 0x8A170FFF, 0xAE4539FF, 0xB6433BFF},
            {0x8A503EFF, 0x633219FF, 0x8A503EFF, 0x8F5E45FF},
            {0xCD683DFF, 0x99360DFF, 0xCD683DFF, 0xD37047FF},
            {0xFBA458FF, 0xC15E14FF, 0xFBA458FF, 0xFFB066FF},
            {0xFB6B1DFF, 0xBF2B00FF, 0xFB6B1DFF, 0xFB671DFF},
            {0xDDBBA4FF, 0xA18067FF, 0xDDBBA4FF, 0xFCDBC3FF},
            {0xFDD7AAFF, 0xBF8E64FF, 0xFDD7AAFF, 0xFFF5CCFF},
            {0xFFA514FF, 0xB56300FF, 0xFFA514FF, 0xFFAC10FF},
            {0xC29162FF, 0x8C5B32FF, 0xC29162FF, 0xD2A077FF},
            {0xE8B710FF, 0xA37100FF, 0xE8B710FF, 0xECBB0EFF},
            {0xFBE626FF, 0xB59400FF, 0xFBE626FF, 0xFFF033FF},
            {0xC0B510FF, 0x7E7E00FF, 0xC0B510FF, 0xC3C317FF},
            {0xFBFF86FF, 0xB0B035FF, 0xFBFF86FF, 0xFFFFA0FF},
            {0xB4D645FF, 0x6FA004FF, 0xB4D645FF, 0xC0F255FF},
            {0x729446FF, 0x48691FFF, 0x729446FF, 0x80A157FF},
            {0xC8E4BEFF, 0x88A980FF, 0xC8E4BEFF, 0xEEFFE6FF},
            {0x45F520FF, 0x0DC200FF, 0x45F520FF, 0x4FFF25FF},
            {0x51C43FFF, 0x26990DFF, 0x51C43FFF, 0x63D74BFF},
            {0x0E4904FF, 0x003C00FF, 0x0E4904FF, 0x0C4E04FF},
            {0x55F084FF, 0x25B94EFF, 0x55F084FF, 0x73FF9CFF},
            {0x1EBC73FF, 0x009A50FF, 0x1EBC73FF, 0x2DD288FF},
            {0x30E1B9FF, 0x02B78EFF, 0x30E1B9FF, 0x4CFFD8FF},
            {0x7FE0C2FF, 0x48AB92FF, 0x7FE0C2FF, 0xA0FFEAFF},
            {0xB8FDFFFF, 0x7DBEC7FF, 0xB8FDFFFF, 0xECFFFFFF},
            {0x039F78FF, 0x00875EFF, 0x039F78FF, 0x10B58CFF},
            {0x63C2C9FF, 0x3598A0FF, 0x63C2C9FF, 0x83E6EEFF},
            {0x216981FF, 0x055770FF, 0x216981FF, 0x2D7F98FF},
            {0x7FE8F2FF, 0x46B9C1FF, 0x7FE8F2FF, 0xA6FFFFFF},
            {0x5369EFFF, 0x3748D4FF, 0x5369EFFF, 0x6F80FFFF},
            {0x4D9BE6FF, 0x2578C2FF, 0x4D9BE6FF, 0x67B9FFFF},
            {0x28306FFF, 0x15255FFF, 0x28306FFF, 0x2F3F79FF},
            {0x5C76BFFF, 0x3859A3FF, 0x5C76BFFF, 0x7293DDFF},
            {0x4D44C0FF, 0x3232ADFF, 0x4D44C0FF, 0x5E5ED9FF},
            {0x180FCFFF, 0x1100CEFF, 0x180FCFFF, 0x2716E4FF},
            {0x53207DFF, 0x400F69FF, 0x53207DFF, 0x5E2C87FF},
            {0x8657CCFF, 0x6634B0FF, 0x8657CCFF, 0x9F6EE9FF},
            {0xA884F3FF, 0x7B5AC5FF, 0xA884F3FF, 0xC9A8FFFF},
            {0x630867FF, 0x4B0054FF, 0x630867FF, 0x63116CFF},
            {0xA03EB2FF, 0x7E1C97FF, 0xA03EB2FF, 0xB452CDFF},
            {0x881AC4FF, 0x7100AAFF, 0x881AC4FF, 0x9A27D4FF},
            {0xE4A8FAFF, 0xAD6BC5FF, 0xE4A8FAFF, 0xFFCBFFFF},
            {0xB53D86FF, 0x8C1963FF, 0xB53D86FF, 0xC04D97FF},
            {0xF34FE9FF, 0xC11DB9FF, 0xF34FE9FF, 0xFF67FFFF},
            {0x7A3045FF, 0x631029FF, 0x7A3045FF, 0x84324BFF},
            {0xF04F78FF, 0xBE1942FF, 0xF04F78FF, 0xFC5780FF},
            {0xC93038FF, 0xA40008FF, 0xC93038FF, 0xCE2932FF},
    };
    public static final int[] WardBonusPalette = new int[256];
    public static final byte[][] WARD_RAMPS = new byte[][]{
            { 0, 0, 0, 0 },
            { 1, 1, 1, 4 },
            { 1, 1, 2, 10 },
            { 1, 1, 3, 4 },
            { 55, 3, 4, 11 },
            { 55, 48, 5, 59 },
            { 59, 12, 6, 54 },
            { 53, 54, 7, 58 },
            { 7, 58, 8, 9 },
            { 14, 23, 9, 9 },
            { 1, 2, 10, 44 },
            { 48, 4, 11, 53 },
            { 44, 42, 12, 14 },
            { 36, 44, 13, 32 },
            { 13, 12, 14, 23 },
            { 39, 14, 15, 23 },
            { 36, 42, 16, 38 },
            { 1, 2, 17, 3 },
            { 36, 44, 18, 42 },
            { 2, 36, 19, 32 },
            { 13, 32, 20, 14 },
            { 14, 23, 21, 23 },
            { 38, 35, 22, 29 },
            { 12, 14, 23, 33 },
            { 29, 21, 24, 9 },
            { 32, 29, 25, 29 },
            { 32, 14, 26, 14 },
            { 26, 29, 27, 24 },
            { 21, 24, 28, 24 },
            { 12, 14, 29, 14 },
            { 21, 23, 30, 24 },
            { 26, 14, 31, 21 },
            { 19, 13, 32, 12 },
            { 15, 23, 33, 8 },
            { 20, 12, 34, 15 },
            { 18, 12, 35, 12 },
            { 1, 17, 36, 61 },
            { 12, 15, 37, 15 },
            { 13, 12, 38, 12 },
            { 62, 12, 39, 15 },
            { 60, 54, 40, 58 },
            { 54, 58, 41, 8 },
            { 17, 61, 42, 12 },
            { 59, 54, 43, 54 },
            { 3, 10, 44, 61 },
            { 60, 54, 45, 58 },
            { 50, 11, 46, 53 },
            { 11, 53, 47, 54 },
            { 1, 3, 48, 11 },
            { 50, 11, 49, 11 },
            { 52, 48, 50, 11 },
            { 55, 48, 51, 48 },
            { 1, 55, 52, 50 },
            { 49, 11, 53, 6 },
            { 5, 6, 54, 7 },
            { 1, 1, 55, 3 },
            { 4, 5, 56, 5 },
            { 52, 48, 57, 11 },
            { 6, 7, 58, 41 },
            { 44, 5, 59, 5 },
            { 5, 6, 60, 6 },
            { 36, 44, 61, 44 },
            { 42, 38, 62, 39 },
            { 44, 42, 63, 42 },
    };
    public static final int[][] WARD_RAMP_VALUES = new int[][]{
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x181029ff, 0x181029ff, 0x181029ff, 0x294a6bff },
            { 0x181029ff, 0x181029ff, 0x183121ff, 0x5a3929ff },
            { 0x181029ff, 0x181029ff, 0x293139ff, 0x294a6bff },
            { 0x311063ff, 0x293139ff, 0x294a6bff, 0x525a8cff },
            { 0x311063ff, 0x313173ff, 0x397b84ff, 0xb52984ff },
            { 0xb52984ff, 0x9c6b6bff, 0x4aa5a5ff, 0xb57bd6ff },
            { 0x845ad6ff, 0xb57bd6ff, 0x84c6d6ff, 0xd6a5deff },
            { 0x84c6d6ff, 0xd6a5deff, 0xded6deff, 0xe7efd6ff },
            { 0xa5947bff, 0xd6b5a5ff, 0xe7efd6ff, 0xe7efd6ff },
            { 0x181029ff, 0x183121ff, 0x5a3929ff, 0x296b42ff },
            { 0x313173ff, 0x294a6bff, 0x525a8cff, 0x845ad6ff },
            { 0x296b42ff, 0x219442ff, 0x9c6b6bff, 0xa5947bff },
            { 0x215221ff, 0x296b42ff, 0x635a31ff, 0x638c29ff },
            { 0x635a31ff, 0x9c6b6bff, 0xa5947bff, 0xd6b5a5ff },
            { 0x42d694ff, 0xa5947bff, 0xe78494ff, 0xd6b5a5ff },
            { 0x215221ff, 0x219442ff, 0xef2929ff, 0x39b54aff },
            { 0x181029ff, 0x183121ff, 0x5a1021ff, 0x293139ff },
            { 0x215221ff, 0x296b42ff, 0xad3129ff, 0x219442ff },
            { 0x183121ff, 0x215221ff, 0x8c5229ff, 0x638c29ff },
            { 0x635a31ff, 0x638c29ff, 0xc66b31ff, 0xa5947bff },
            { 0xa5947bff, 0xd6b5a5ff, 0xe7a56bff, 0xd6b5a5ff },
            { 0x39b54aff, 0x39c631ff, 0xef7331ff, 0x9cb542ff },
            { 0x9c6b6bff, 0xa5947bff, 0xd6b5a5ff, 0xc6e7a5ff },
            { 0x9cb542ff, 0xe7a56bff, 0xe7d68cff, 0xe7efd6ff },
            { 0x638c29ff, 0x9cb542ff, 0xef9c29ff, 0x9cb542ff },
            { 0x638c29ff, 0xa5947bff, 0xb59431ff, 0xa5947bff },
            { 0xb59431ff, 0x9cb542ff, 0xdebd31ff, 0xe7d68cff },
            { 0xe7a56bff, 0xe7d68cff, 0xe7e731ff, 0xe7d68cff },
            { 0x9c6b6bff, 0xa5947bff, 0x9cb542ff, 0xa5947bff },
            { 0xe7a56bff, 0xd6b5a5ff, 0xbdef52ff, 0xe7d68cff },
            { 0xb59431ff, 0xa5947bff, 0xa5d639ff, 0xe7a56bff },
            { 0x8c5229ff, 0x635a31ff, 0x638c29ff, 0x9c6b6bff },
            { 0xe78494ff, 0xd6b5a5ff, 0xc6e7a5ff, 0xded6deff },
            { 0xc66b31ff, 0x9c6b6bff, 0x42ef31ff, 0xe78494ff },
            { 0xad3129ff, 0x9c6b6bff, 0x39c631ff, 0x9c6b6bff },
            { 0x181029ff, 0x5a1021ff, 0x215221ff, 0x842142ff },
            { 0x9c6b6bff, 0xe78494ff, 0x4aef84ff, 0xe78494ff },
            { 0x635a31ff, 0x9c6b6bff, 0x39b54aff, 0x9c6b6bff },
            { 0xe73984ff, 0x9c6b6bff, 0x42d694ff, 0xe78494ff },
            { 0xde31deff, 0xb57bd6ff, 0x52e7bdff, 0xd6a5deff },
            { 0xb57bd6ff, 0xd6a5deff, 0x9cefdeff, 0xded6deff },
            { 0x5a1021ff, 0x842142ff, 0x219442ff, 0x9c6b6bff },
            { 0xb52984ff, 0xb57bd6ff, 0x42bdbdff, 0xb57bd6ff },
            { 0x293139ff, 0x5a3929ff, 0x296b42ff, 0x842142ff },
            { 0xde31deff, 0xb57bd6ff, 0x42e7e7ff, 0xd6a5deff },
            { 0x4242b5ff, 0x525a8cff, 0x426be7ff, 0x845ad6ff },
            { 0x525a8cff, 0x845ad6ff, 0x4a9cdeff, 0xb57bd6ff },
            { 0x181029ff, 0x293139ff, 0x313173ff, 0x525a8cff },
            { 0x4242b5ff, 0x525a8cff, 0x4273c6ff, 0x525a8cff },
            { 0x391894ff, 0x313173ff, 0x4242b5ff, 0x525a8cff },
            { 0x311063ff, 0x313173ff, 0x3118c6ff, 0x313173ff },
            { 0x181029ff, 0x311063ff, 0x391894ff, 0x4242b5ff },
            { 0x4273c6ff, 0x525a8cff, 0x845ad6ff, 0x4aa5a5ff },
            { 0x397b84ff, 0x4aa5a5ff, 0xb57bd6ff, 0x84c6d6ff },
            { 0x181029ff, 0x181029ff, 0x311063ff, 0x293139ff },
            { 0x294a6bff, 0x397b84ff, 0xa521d6ff, 0x397b84ff },
            { 0x391894ff, 0x313173ff, 0x5a21deff, 0x525a8cff },
            { 0x4aa5a5ff, 0x84c6d6ff, 0xd6a5deff, 0x9cefdeff },
            { 0x296b42ff, 0x397b84ff, 0xb52984ff, 0x397b84ff },
            { 0x397b84ff, 0x4aa5a5ff, 0xde31deff, 0x4aa5a5ff },
            { 0x215221ff, 0x296b42ff, 0x842142ff, 0x296b42ff },
            { 0x219442ff, 0x39b54aff, 0xe73984ff, 0x42d694ff },
            { 0x296b42ff, 0x219442ff, 0xc62131ff, 0x219442ff },
    };     
    private static final int[][] WARD_BONUS_RAMP_VALUES = new int[][] {
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x1D3131FF, 0x2A3C3CFF, 0x314A4AFF, 0x4D5D5DFF },
            { 0x6975A4FF, 0x8B96BFFF, 0xA5B5EFFF, 0xEBF6FFFF },
            { 0x140D50FF, 0x1C1550FF, 0x21186BFF, 0x302A60FF },
            { 0x0F8609FF, 0x2B9326FF, 0x29BD21FF, 0x6ACB65FF },
            { 0x2F2192FF, 0x3E3295FF, 0x4A39C6FF, 0x695EBAFF },
            { 0xA33F11FF, 0xB55E35FF, 0xE76B31FF, 0xFDAC86FF },
            { 0x82045BFF, 0x8B1D69FF, 0xB51884FF, 0xB95399FF },
            { 0x0072ADFF, 0x2083B7FF, 0x18A5EFFF, 0x64C0F1FF },
            { 0x3A61A4FF, 0x577AB4FF, 0x6394E7FF, 0xA2C2F9FF },
            { 0x2633B1FF, 0x3A46B4FF, 0x4252EFFF, 0x6E78DFFF },
            { 0xA0788CFF, 0xC3A0B1FF, 0xEFBDD6FF, 0xFFFFFFFF },
            { 0x08A793FF, 0x31BDABFF, 0x29EFD6FF, 0x8CFFFDFF },
            { 0x609581FF, 0x86B5A4FF, 0x9CDEC6FF, 0xEEFFFFFF },
            { 0x9D684EFF, 0xBA8C74FF, 0xE7A584FF, 0xFFEED8FF },
            { 0x102B6DFF, 0x1F3670FF, 0x214294FF, 0x42578DFF },
            { 0x83413AFF, 0x965D57FF, 0xBD6B63FF, 0xDBA5A0FF },
            { 0x267C62FF, 0x459179FF, 0x4AB594FF, 0x90D6C0FF },
            { 0x570765FF, 0x601A6CFF, 0x7B188CFF, 0x854490FF },
            { 0x486927FF, 0x617E45FF, 0x739C4AFF, 0xA6C18BFF },
            { 0x4F2E0DFF, 0x5B3E22FF, 0x734A21FF, 0x866B50FF },
            { 0x879B00FF, 0xAABC30FF, 0xCEE721FF, 0xFFFF9FFF },
            { 0x7A2B10FF, 0x87412AFF, 0xAD4A29FF, 0xBC7B65FF },
            { 0x264E3AFF, 0x3A5D4BFF, 0x42735AFF, 0x6C8C7CFF },
            { 0xA82A94FF, 0xBC4EAAFF, 0xEF52D6FF, 0xFFA4FAFF },
            { 0xB40F08FF, 0xBC2C26FF, 0xF72921FF, 0xF36D67FF },
            { 0x9F009FFF, 0xAC20ACFF, 0xDE18DEFF, 0xE766E7FF },
            { 0x4C385AFF, 0x5E4C6AFF, 0x735A84FF, 0x94849FFF },
            { 0x1A5C06FF, 0x2D681CFF, 0x318418FF, 0x5D934DFF },
            { 0x670BA3FF, 0x7524A9FF, 0x9421DEFF, 0xA65BD6FF },
            { 0x140684FF, 0x1C107FFF, 0x2110ADFF, 0x33278EFF },
            { 0x0C555CFF, 0x226268FF, 0x217B84FF, 0x558F95FF },
            { 0xA65D01FF, 0xBD7D2DFF, 0xEF9421FF, 0xFFD68BFF },
            { 0x653079FF, 0x774988FF, 0x9452ADFF, 0xB388C3FF },
            { 0x4FA513FF, 0x73BE3FFF, 0x84EF39FF, 0xD1FFA0FF },
            { 0xA2810AFF, 0xC1A43BFF, 0xEFC631FF, 0xFFFFA8FF },
            { 0x4B8816FF, 0x6A9F3BFF, 0x7BC639FF, 0xBBEC90FF },
            { 0x090F17FF, 0x0E131AFF, 0x101821FF, 0x191F25FF },
            { 0x271A06FF, 0x2D2210FF, 0x392910FF, 0x443929FF },
            { 0x113F0AFF, 0x1F4718FF, 0x215A18FF, 0x3F653AFF },
            { 0xA49741FF, 0xCABF73FF, 0xF7E77BFF, 0xFFFFEDFF },
            { 0x9D5490FF, 0xBA7AAEFF, 0xE78CD6FF, 0xFFDCFFFF },
            { 0x524B16FF, 0x645F30FF, 0x7B7331FF, 0x9D986DFF },
            { 0xAE015EFF, 0xB72071FF, 0xEF188CFF, 0xEF63AFFF },
            { 0x6E40AAFF, 0x865EBAFF, 0xA56BEFFF, 0xD1ABFFFF },
            { 0x3AA475FF, 0x62BF96FF, 0x6BEFB5FF, 0xC5FFF6FF },
            { 0x180AB7FF, 0x2418AFFF, 0x2918EFFF, 0x4237C4FF },
            { 0x893975FF, 0x9E588CFF, 0xC663ADFF, 0xE7A6D7FF },
            { 0x737309FF, 0x8D8D31FF, 0xADAD29FF, 0xDEDE88FF },
            { 0x068A83FF, 0x289C96FF, 0x21C6BDFF, 0x74E0DAFF },
            { 0x11538FFF, 0x2A6398FF, 0x297BC6FF, 0x6297C8FF },
            { 0x2F0627FF, 0x33102DFF, 0x421039FF, 0x472641FF },
            { 0x8F9D8FFF, 0xBAC6BAFF, 0xDEEFDEFF, 0xFFFFFFFF },
            { 0x0BAB05FF, 0x2EBA29FF, 0x29EF21FF, 0x7CFD77FF },
            { 0x05A446FF, 0x29B463FF, 0x21E773FF, 0x79FAAFFF },
            { 0x7C550BFF, 0x916E2EFF, 0xB58429FF, 0xD7B77CFF },
            { 0x7B825AFF, 0x9DA380FF, 0xBDC694FF, 0xFFFFE4FF },
            { 0x78A64AFF, 0xA0C978FF, 0xBDF784FF, 0xFFFFEAFF },
            { 0x4D0A24FF, 0x52182FFF, 0x6B1839FF, 0x6F394EFF },
            { 0x037538FF, 0x1E814CFF, 0x18A55AFF, 0x57B382FF },
            { 0xA9314CFF, 0xBB526AFF, 0xEF5A7BFF, 0xFFA4B9FF },
            { 0x9C0A1EFF, 0xA32435FF, 0xD62139FF, 0xD45E6EFF },
            { 0x780715FF, 0x7E1B27FF, 0xA51829FF, 0xA34752FF },
            { 0x5F6666FF, 0x7B8181FF, 0x949C9CFF, 0xCCD1D1FF },
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x2A3C3CFF, 0x314A4AFF, 0x314A4AFF, 0x314A4AFF },
            { 0x8B96BFFF, 0xA5B5EFFF, 0xA5B5EFFF, 0xA5B5EFFF },
            { 0x1C1550FF, 0x21186BFF, 0x21186BFF, 0x21186BFF },
            { 0x2B9326FF, 0x29BD21FF, 0x29BD21FF, 0x29BD21FF },
            { 0x3E3295FF, 0x4A39C6FF, 0x4A39C6FF, 0x4A39C6FF },
            { 0xB55E35FF, 0xE76B31FF, 0xE76B31FF, 0xE76B31FF },
            { 0x8B1D69FF, 0xB51884FF, 0xB51884FF, 0xB51884FF },
            { 0x2083B7FF, 0x18A5EFFF, 0x18A5EFFF, 0x18A5EFFF },
            { 0x577AB4FF, 0x6394E7FF, 0x6394E7FF, 0x6394E7FF },
            { 0x3A46B4FF, 0x4252EFFF, 0x4252EFFF, 0x4252EFFF },
            { 0xC3A0B1FF, 0xEFBDD6FF, 0xEFBDD6FF, 0xEFBDD6FF },
            { 0x31BDABFF, 0x29EFD6FF, 0x29EFD6FF, 0x29EFD6FF },
            { 0x86B5A4FF, 0x9CDEC6FF, 0x9CDEC6FF, 0x9CDEC6FF },
            { 0xBA8C74FF, 0xE7A584FF, 0xE7A584FF, 0xE7A584FF },
            { 0x1F3670FF, 0x214294FF, 0x214294FF, 0x214294FF },
            { 0x965D57FF, 0xBD6B63FF, 0xBD6B63FF, 0xBD6B63FF },
            { 0x459179FF, 0x4AB594FF, 0x4AB594FF, 0x4AB594FF },
            { 0x601A6CFF, 0x7B188CFF, 0x7B188CFF, 0x7B188CFF },
            { 0x617E45FF, 0x739C4AFF, 0x739C4AFF, 0x739C4AFF },
            { 0x5B3E22FF, 0x734A21FF, 0x734A21FF, 0x734A21FF },
            { 0xAABC30FF, 0xCEE721FF, 0xCEE721FF, 0xCEE721FF },
            { 0x87412AFF, 0xAD4A29FF, 0xAD4A29FF, 0xAD4A29FF },
            { 0x3A5D4BFF, 0x42735AFF, 0x42735AFF, 0x42735AFF },
            { 0xBC4EAAFF, 0xEF52D6FF, 0xEF52D6FF, 0xEF52D6FF },
            { 0xBC2C26FF, 0xF72921FF, 0xF72921FF, 0xF72921FF },
            { 0xAC20ACFF, 0xDE18DEFF, 0xDE18DEFF, 0xDE18DEFF },
            { 0x5E4C6AFF, 0x735A84FF, 0x735A84FF, 0x735A84FF },
            { 0x2D681CFF, 0x318418FF, 0x318418FF, 0x318418FF },
            { 0x7524A9FF, 0x9421DEFF, 0x9421DEFF, 0x9421DEFF },
            { 0x1C107FFF, 0x2110ADFF, 0x2110ADFF, 0x2110ADFF },
            { 0x226268FF, 0x217B84FF, 0x217B84FF, 0x217B84FF },
            { 0xBD7D2DFF, 0xEF9421FF, 0xEF9421FF, 0xEF9421FF },
            { 0x774988FF, 0x9452ADFF, 0x9452ADFF, 0x9452ADFF },
            { 0x73BE3FFF, 0x84EF39FF, 0x84EF39FF, 0x84EF39FF },
            { 0xC1A43BFF, 0xEFC631FF, 0xEFC631FF, 0xEFC631FF },
            { 0x6A9F3BFF, 0x7BC639FF, 0x7BC639FF, 0x7BC639FF },
            { 0x0E131AFF, 0x101821FF, 0x101821FF, 0x101821FF },
            { 0x2D2210FF, 0x392910FF, 0x392910FF, 0x392910FF },
            { 0x1F4718FF, 0x215A18FF, 0x215A18FF, 0x215A18FF },
            { 0xCABF73FF, 0xF7E77BFF, 0xF7E77BFF, 0xF7E77BFF },
            { 0xBA7AAEFF, 0xE78CD6FF, 0xE78CD6FF, 0xE78CD6FF },
            { 0x645F30FF, 0x7B7331FF, 0x7B7331FF, 0x7B7331FF },
            { 0xB72071FF, 0xEF188CFF, 0xEF188CFF, 0xEF188CFF },
            { 0x865EBAFF, 0xA56BEFFF, 0xA56BEFFF, 0xA56BEFFF },
            { 0x62BF96FF, 0x6BEFB5FF, 0x6BEFB5FF, 0x6BEFB5FF },
            { 0x2418AFFF, 0x2918EFFF, 0x2918EFFF, 0x2918EFFF },
            { 0x9E588CFF, 0xC663ADFF, 0xC663ADFF, 0xC663ADFF },
            { 0x8D8D31FF, 0xADAD29FF, 0xADAD29FF, 0xADAD29FF },
            { 0x289C96FF, 0x21C6BDFF, 0x21C6BDFF, 0x21C6BDFF },
            { 0x2A6398FF, 0x297BC6FF, 0x297BC6FF, 0x297BC6FF },
            { 0x33102DFF, 0x421039FF, 0x421039FF, 0x421039FF },
            { 0xBAC6BAFF, 0xDEEFDEFF, 0xDEEFDEFF, 0xDEEFDEFF },
            { 0x2EBA29FF, 0x29EF21FF, 0x29EF21FF, 0x29EF21FF },
            { 0x29B463FF, 0x21E773FF, 0x21E773FF, 0x21E773FF },
            { 0x916E2EFF, 0xB58429FF, 0xB58429FF, 0xB58429FF },
            { 0x9DA380FF, 0xBDC694FF, 0xBDC694FF, 0xBDC694FF },
            { 0xA0C978FF, 0xBDF784FF, 0xBDF784FF, 0xBDF784FF },
            { 0x52182FFF, 0x6B1839FF, 0x6B1839FF, 0x6B1839FF },
            { 0x1E814CFF, 0x18A55AFF, 0x18A55AFF, 0x18A55AFF },
            { 0xBB526AFF, 0xEF5A7BFF, 0xEF5A7BFF, 0xEF5A7BFF },
            { 0xA32435FF, 0xD62139FF, 0xD62139FF, 0xD62139FF },
            { 0x7E1B27FF, 0xA51829FF, 0xA51829FF, 0xA51829FF },
            { 0x7B8181FF, 0x949C9CFF, 0x949C9CFF, 0x949C9CFF },
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x1D3131FF, 0x2A3C3CFF, 0x314A4AFF, 0x4D5D5DFF },
            { 0x6975A4FF, 0x8B96BFFF, 0xA5B5EFFF, 0xEBF6FFFF },
            { 0x140D50FF, 0x1C1550FF, 0x21186BFF, 0x302A60FF },
            { 0x0F8609FF, 0x2B9326FF, 0x29BD21FF, 0x6ACB65FF },
            { 0x2F2192FF, 0x3E3295FF, 0x4A39C6FF, 0x695EBAFF },
            { 0xA33F11FF, 0xB55E35FF, 0xE76B31FF, 0xFDAC86FF },
            { 0x82045BFF, 0x8B1D69FF, 0xB51884FF, 0xB95399FF },
            { 0x0072ADFF, 0x2083B7FF, 0x18A5EFFF, 0x64C0F1FF },
            { 0x3A61A4FF, 0x577AB4FF, 0x6394E7FF, 0xA2C2F9FF },
            { 0x2633B1FF, 0x3A46B4FF, 0x4252EFFF, 0x6E78DFFF },
            { 0xA0788CFF, 0xC3A0B1FF, 0xEFBDD6FF, 0xFFFFFFFF },
            { 0x08A793FF, 0x31BDABFF, 0x29EFD6FF, 0x8CFFFDFF },
            { 0x609581FF, 0x86B5A4FF, 0x9CDEC6FF, 0xEEFFFFFF },
            { 0x9D684EFF, 0xBA8C74FF, 0xE7A584FF, 0xFFEED8FF },
            { 0x102B6DFF, 0x1F3670FF, 0x214294FF, 0x42578DFF },
            { 0x83413AFF, 0x965D57FF, 0xBD6B63FF, 0xDBA5A0FF },
            { 0x267C62FF, 0x459179FF, 0x4AB594FF, 0x90D6C0FF },
            { 0x570765FF, 0x601A6CFF, 0x7B188CFF, 0x854490FF },
            { 0x486927FF, 0x617E45FF, 0x739C4AFF, 0xA6C18BFF },
            { 0x4F2E0DFF, 0x5B3E22FF, 0x734A21FF, 0x866B50FF },
            { 0x879B00FF, 0xAABC30FF, 0xCEE721FF, 0xFFFF9FFF },
            { 0x7A2B10FF, 0x87412AFF, 0xAD4A29FF, 0xBC7B65FF },
            { 0x264E3AFF, 0x3A5D4BFF, 0x42735AFF, 0x6C8C7CFF },
            { 0xA82A94FF, 0xBC4EAAFF, 0xEF52D6FF, 0xFFA4FAFF },
            { 0xB40F08FF, 0xBC2C26FF, 0xF72921FF, 0xF36D67FF },
            { 0x9F009FFF, 0xAC20ACFF, 0xDE18DEFF, 0xE766E7FF },
            { 0x4C385AFF, 0x5E4C6AFF, 0x735A84FF, 0x94849FFF },
            { 0x1A5C06FF, 0x2D681CFF, 0x318418FF, 0x5D934DFF },
            { 0x670BA3FF, 0x7524A9FF, 0x9421DEFF, 0xA65BD6FF },
            { 0x140684FF, 0x1C107FFF, 0x2110ADFF, 0x33278EFF },
            { 0x0C555CFF, 0x226268FF, 0x217B84FF, 0x558F95FF },
            { 0xA65D01FF, 0xBD7D2DFF, 0xEF9421FF, 0xFFD68BFF },
            { 0x653079FF, 0x774988FF, 0x9452ADFF, 0xB388C3FF },
            { 0x4FA513FF, 0x73BE3FFF, 0x84EF39FF, 0xD1FFA0FF },
            { 0xA2810AFF, 0xC1A43BFF, 0xEFC631FF, 0xFFFFA8FF },
            { 0x4B8816FF, 0x6A9F3BFF, 0x7BC639FF, 0xBBEC90FF },
            { 0x090F17FF, 0x0E131AFF, 0x101821FF, 0x191F25FF },
            { 0x271A06FF, 0x2D2210FF, 0x392910FF, 0x443929FF },
            { 0x113F0AFF, 0x1F4718FF, 0x215A18FF, 0x3F653AFF },
            { 0xA49741FF, 0xCABF73FF, 0xF7E77BFF, 0xFFFFEDFF },
            { 0x9D5490FF, 0xBA7AAEFF, 0xE78CD6FF, 0xFFDCFFFF },
            { 0x524B16FF, 0x645F30FF, 0x7B7331FF, 0x9D986DFF },
            { 0xAE015EFF, 0xB72071FF, 0xEF188CFF, 0xEF63AFFF },
            { 0x6E40AAFF, 0x865EBAFF, 0xA56BEFFF, 0xD1ABFFFF },
            { 0x3AA475FF, 0x62BF96FF, 0x6BEFB5FF, 0xC5FFF6FF },
            { 0x180AB7FF, 0x2418AFFF, 0x2918EFFF, 0x4237C4FF },
            { 0x893975FF, 0x9E588CFF, 0xC663ADFF, 0xE7A6D7FF },
            { 0x737309FF, 0x8D8D31FF, 0xADAD29FF, 0xDEDE88FF },
            { 0x068A83FF, 0x289C96FF, 0x21C6BDFF, 0x74E0DAFF },
            { 0x11538FFF, 0x2A6398FF, 0x297BC6FF, 0x6297C8FF },
            { 0x2F0627FF, 0x33102DFF, 0x421039FF, 0x472641FF },
            { 0x8F9D8FFF, 0xBAC6BAFF, 0xDEEFDEFF, 0xFFFFFFFF },
            { 0x0BAB05FF, 0x2EBA29FF, 0x29EF21FF, 0x7CFD77FF },
            { 0x05A446FF, 0x29B463FF, 0x21E773FF, 0x79FAAFFF },
            { 0x7C550BFF, 0x916E2EFF, 0xB58429FF, 0xD7B77CFF },
            { 0x7B825AFF, 0x9DA380FF, 0xBDC694FF, 0xFFFFE4FF },
            { 0x78A64AFF, 0xA0C978FF, 0xBDF784FF, 0xFFFFEAFF },
            { 0x4D0A24FF, 0x52182FFF, 0x6B1839FF, 0x6F394EFF },
            { 0x037538FF, 0x1E814CFF, 0x18A55AFF, 0x57B382FF },
            { 0xA9314CFF, 0xBB526AFF, 0xEF5A7BFF, 0xFFA4B9FF },
            { 0x9C0A1EFF, 0xA32435FF, 0xD62139FF, 0xD45E6EFF },
            { 0x780715FF, 0x7E1B27FF, 0xA51829FF, 0xA34752FF },
            { 0x5F6666FF, 0x7B8181FF, 0x949C9CFF, 0xCCD1D1FF },
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x314A4AFF, 0x2A3C3CFF, 0x314A4AFF, 0x4D5D5DFF },
            { 0xA5B5EFFF, 0x8B96BFFF, 0xA5B5EFFF, 0xEBF6FFFF },
            { 0x21186BFF, 0x1C1550FF, 0x21186BFF, 0x302A60FF },
            { 0x29BD21FF, 0x2B9326FF, 0x29BD21FF, 0x6ACB65FF },
            { 0x4A39C6FF, 0x3E3295FF, 0x4A39C6FF, 0x695EBAFF },
            { 0xE76B31FF, 0xB55E35FF, 0xE76B31FF, 0xFDAC86FF },
            { 0xB51884FF, 0x8B1D69FF, 0xB51884FF, 0xB95399FF },
            { 0x18A5EFFF, 0x2083B7FF, 0x18A5EFFF, 0x64C0F1FF },
            { 0x6394E7FF, 0x577AB4FF, 0x6394E7FF, 0xA2C2F9FF },
            { 0x4252EFFF, 0x3A46B4FF, 0x4252EFFF, 0x6E78DFFF },
            { 0xEFBDD6FF, 0xC3A0B1FF, 0xEFBDD6FF, 0xFFFFFFFF },
            { 0x29EFD6FF, 0x31BDABFF, 0x29EFD6FF, 0x8CFFFDFF },
            { 0x9CDEC6FF, 0x86B5A4FF, 0x9CDEC6FF, 0xEEFFFFFF },
            { 0xE7A584FF, 0xBA8C74FF, 0xE7A584FF, 0xFFEED8FF },
            { 0x214294FF, 0x1F3670FF, 0x214294FF, 0x42578DFF },
            { 0xBD6B63FF, 0x965D57FF, 0xBD6B63FF, 0xDBA5A0FF },
            { 0x4AB594FF, 0x459179FF, 0x4AB594FF, 0x90D6C0FF },
            { 0x7B188CFF, 0x601A6CFF, 0x7B188CFF, 0x854490FF },
            { 0x739C4AFF, 0x617E45FF, 0x739C4AFF, 0xA6C18BFF },
            { 0x734A21FF, 0x5B3E22FF, 0x734A21FF, 0x866B50FF },
            { 0xCEE721FF, 0xAABC30FF, 0xCEE721FF, 0xFFFF9FFF },
            { 0xAD4A29FF, 0x87412AFF, 0xAD4A29FF, 0xBC7B65FF },
            { 0x42735AFF, 0x3A5D4BFF, 0x42735AFF, 0x6C8C7CFF },
            { 0xEF52D6FF, 0xBC4EAAFF, 0xEF52D6FF, 0xFFA4FAFF },
            { 0xF72921FF, 0xBC2C26FF, 0xF72921FF, 0xF36D67FF },
            { 0xDE18DEFF, 0xAC20ACFF, 0xDE18DEFF, 0xE766E7FF },
            { 0x735A84FF, 0x5E4C6AFF, 0x735A84FF, 0x94849FFF },
            { 0x318418FF, 0x2D681CFF, 0x318418FF, 0x5D934DFF },
            { 0x9421DEFF, 0x7524A9FF, 0x9421DEFF, 0xA65BD6FF },
            { 0x2110ADFF, 0x1C107FFF, 0x2110ADFF, 0x33278EFF },
            { 0x217B84FF, 0x226268FF, 0x217B84FF, 0x558F95FF },
            { 0xEF9421FF, 0xBD7D2DFF, 0xEF9421FF, 0xFFD68BFF },
            { 0x9452ADFF, 0x774988FF, 0x9452ADFF, 0xB388C3FF },
            { 0x84EF39FF, 0x73BE3FFF, 0x84EF39FF, 0xD1FFA0FF },
            { 0xEFC631FF, 0xC1A43BFF, 0xEFC631FF, 0xFFFFA8FF },
            { 0x7BC639FF, 0x6A9F3BFF, 0x7BC639FF, 0xBBEC90FF },
            { 0x101821FF, 0x0E131AFF, 0x101821FF, 0x191F25FF },
            { 0x392910FF, 0x2D2210FF, 0x392910FF, 0x443929FF },
            { 0x215A18FF, 0x1F4718FF, 0x215A18FF, 0x3F653AFF },
            { 0xF7E77BFF, 0xCABF73FF, 0xF7E77BFF, 0xFFFFEDFF },
            { 0xE78CD6FF, 0xBA7AAEFF, 0xE78CD6FF, 0xFFDCFFFF },
            { 0x7B7331FF, 0x645F30FF, 0x7B7331FF, 0x9D986DFF },
            { 0xEF188CFF, 0xB72071FF, 0xEF188CFF, 0xEF63AFFF },
            { 0xA56BEFFF, 0x865EBAFF, 0xA56BEFFF, 0xD1ABFFFF },
            { 0x6BEFB5FF, 0x62BF96FF, 0x6BEFB5FF, 0xC5FFF6FF },
            { 0x2918EFFF, 0x2418AFFF, 0x2918EFFF, 0x4237C4FF },
            { 0xC663ADFF, 0x9E588CFF, 0xC663ADFF, 0xE7A6D7FF },
            { 0xADAD29FF, 0x8D8D31FF, 0xADAD29FF, 0xDEDE88FF },
            { 0x21C6BDFF, 0x289C96FF, 0x21C6BDFF, 0x74E0DAFF },
            { 0x297BC6FF, 0x2A6398FF, 0x297BC6FF, 0x6297C8FF },
            { 0x421039FF, 0x33102DFF, 0x421039FF, 0x472641FF },
            { 0xDEEFDEFF, 0xBAC6BAFF, 0xDEEFDEFF, 0xFFFFFFFF },
            { 0x29EF21FF, 0x2EBA29FF, 0x29EF21FF, 0x7CFD77FF },
            { 0x21E773FF, 0x29B463FF, 0x21E773FF, 0x79FAAFFF },
            { 0xB58429FF, 0x916E2EFF, 0xB58429FF, 0xD7B77CFF },
            { 0xBDC694FF, 0x9DA380FF, 0xBDC694FF, 0xFFFFE4FF },
            { 0xBDF784FF, 0xA0C978FF, 0xBDF784FF, 0xFFFFEAFF },
            { 0x6B1839FF, 0x52182FFF, 0x6B1839FF, 0x6F394EFF },
            { 0x18A55AFF, 0x1E814CFF, 0x18A55AFF, 0x57B382FF },
            { 0xEF5A7BFF, 0xBB526AFF, 0xEF5A7BFF, 0xFFA4B9FF },
            { 0xD62139FF, 0xA32435FF, 0xD62139FF, 0xD45E6EFF },
            { 0xA51829FF, 0x7E1B27FF, 0xA51829FF, 0xA34752FF },
            { 0x949C9CFF, 0x7B8181FF, 0x949C9CFF, 0xCCD1D1FF },
    };
    // Toasty shares ramps with Twirl
    public static final int[] ToastyBonusPalette = new int[256];
    private static final int[][] TOASTY_BONUS_RAMP_VALUES = new int[256][4];

//    public static final int[] CubicleBonusPalette = new int[256];
//    public static final byte[][] CUBICLE64_RAMPS = new byte[][]{
//            { 0, 0, 0, 0 },
//            { 1, 1, 1, 37 },
//            { 37, 19, 2, 3 },
//            { 37, 2, 3, 4 },
//            { 2, 3, 4, 5 },
//            { 3, 4, 5, 6 },
//            { 4, 5, 6, 8 },
//            { 4, 5, 7, 9 },
//            { 5, 6, 8, 9 },
//            { 5, 6, 9, 9 },
//            { 1, 1, 10, 28 },
//            { 1, 28, 11, 2 },
//            { 28, 29, 12, 29 },
//            { 29, 3, 13, 3 },
//            { 30, 31, 14, 31 },
//            { 31, 4, 15, 34 },
//            { 4, 5, 16, 5 },
//            { 33, 5, 17, 6 },
//            { 4, 5, 18, 9 },
//            { 1, 37, 19, 37 },
//            { 37, 38, 20, 39 },
//            { 38, 39, 21, 3 },
//            { 40, 41, 22, 41 },
//            { 22, 41, 23, 41 },
//            { 41, 43, 24, 43 },
//            { 43, 44, 25, 45 },
//            { 43, 44, 26, 45 },
//            { 43, 44, 27, 9 },
//            { 1, 10, 28, 10 },
//            { 10, 2, 29, 2 },
//            { 12, 11, 30, 13 },
//            { 13, 3, 31, 3 },
//            { 13, 15, 32, 15 },
//            { 15, 16, 33, 17 },
//            { 16, 17, 34, 17 },
//            { 16, 5, 35, 18 },
//            { 5, 35, 36, 9 },
//            { 1, 1, 37, 19 },
//            { 46, 19, 38, 2 },
//            { 2, 21, 39, 21 },
//            { 20, 21, 40, 21 },
//            { 21, 4, 41, 23 },
//            { 22, 23, 42, 23 },
//            { 5, 6, 43, 26 },
//            { 23, 25, 44, 26 },
//            { 5, 43, 45, 9 },
//            { 1, 1, 46, 19 },
//            { 1, 46, 47, 37 },
//            { 1, 47, 48, 47 },
//            { 38, 20, 49, 39 },
//            { 47, 49, 50, 49 },
//            { 47, 49, 51, 52 },
//            { 49, 51, 52, 53 },
//            { 51, 52, 53, 54 },
//            { 53, 5, 54, 9 },
//            { 10, 28, 55, 2 },
//            { 10, 55, 56, 3 },
//            { 56, 3, 57, 59 },
//            { 57, 59, 58, 59 },
//            { 57, 5, 59, 17 },
//            { 59, 61, 60, 61 },
//            { 59, 18, 61, 9 },
//            { 18, 36, 62, 9 },
//            { 5, 6, 63, 9 },
//    };
//    public static final int[][] CUBICLE64_RAMP_VALUES = new int[][]{
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x000000FF, 0x000000FF, 0x000000FF, 0x380038FF },
//            { 0x380038FF, 0x003838FF, 0x3B3B3BFF, 0x6F6F6FFF },
//            { 0x380038FF, 0x3B3B3BFF, 0x6F6F6FFF, 0x9B9B9BFF },
//            { 0x3B3B3BFF, 0x6F6F6FFF, 0x9B9B9BFF, 0xBFBFBFFF },
//            { 0x6F6F6FFF, 0x9B9B9BFF, 0xBFBFBFFF, 0xDBDBDBFF },
//            { 0x9B9B9BFF, 0xBFBFBFFF, 0xDBDBDBFF, 0xFBFBFBFF },
//            { 0x9B9B9BFF, 0xBFBFBFFF, 0xEFEFEFFF, 0xFFFFFFFF },
//            { 0xBFBFBFFF, 0xDBDBDBFF, 0xFBFBFBFF, 0xFFFFFFFF },
//            { 0xBFBFBFFF, 0xDBDBDBFF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x000000FF, 0x000000FF, 0x320000FF, 0x003200FF },
//            { 0x000000FF, 0x003200FF, 0x640A0AFF, 0x3B3B3BFF },
//            { 0x003200FF, 0x0A640AFF, 0xAA0000FF, 0x0A640AFF },
//            { 0x0A640AFF, 0x6F6F6FFF, 0xBC3E3EFF, 0x6F6F6FFF },
//            { 0x00AA00FF, 0x3EBC3EFF, 0xFF3C3CFF, 0x3EBC3EFF },
//            { 0x3EBC3EFF, 0x9B9B9BFF, 0xFF7272FF, 0x8CFF8CFF },
//            { 0x9B9B9BFF, 0xBFBFBFFF, 0xFF8C8CFF, 0xBFBFBFFF },
//            { 0x72FF72FF, 0xBFBFBFFF, 0xFFA6A6FF, 0xDBDBDBFF },
//            { 0x9B9B9BFF, 0xBFBFBFFF, 0xFFDCDCFF, 0xFFFFFFFF },
//            { 0x000000FF, 0x380038FF, 0x003838FF, 0x380038FF },
//            { 0x380038FF, 0x6A006AFF, 0x006A6AFF, 0x843084FF },
//            { 0x6A006AFF, 0x843084FF, 0x308484FF, 0x6F6F6FFF },
//            { 0xDA1ADAFF, 0xD070D0FF, 0x1ADADAFF, 0xD070D0FF },
//            { 0x1ADADAFF, 0xD070D0FF, 0x70D0D0FF, 0xD070D0FF },
//            { 0xD070D0FF, 0xFFB0FFFF, 0x6EFFFFFF, 0xFFB0FFFF },
//            { 0xFFB0FFFF, 0xFFC2FFFF, 0xB0FFFFFF, 0xFFDCFFFF },
//            { 0xFFB0FFFF, 0xFFC2FFFF, 0xC2FFFFFF, 0xFFDCFFFF },
//            { 0xFFB0FFFF, 0xFFC2FFFF, 0xDCFFFFFF, 0xFFFFFFFF },
//            { 0x000000FF, 0x320000FF, 0x003200FF, 0x320000FF },
//            { 0x320000FF, 0x3B3B3BFF, 0x0A640AFF, 0x3B3B3BFF },
//            { 0xAA0000FF, 0x640A0AFF, 0x00AA00FF, 0xBC3E3EFF },
//            { 0xBC3E3EFF, 0x6F6F6FFF, 0x3EBC3EFF, 0x6F6F6FFF },
//            { 0xBC3E3EFF, 0xFF7272FF, 0x3CFF3CFF, 0xFF7272FF },
//            { 0xFF7272FF, 0xFF8C8CFF, 0x72FF72FF, 0xFFA6A6FF },
//            { 0xFF8C8CFF, 0xFFA6A6FF, 0x8CFF8CFF, 0xFFA6A6FF },
//            { 0xFF8C8CFF, 0xBFBFBFFF, 0xA6FFA6FF, 0xFFDCDCFF },
//            { 0xBFBFBFFF, 0xA6FFA6FF, 0xDCFFDCFF, 0xFFFFFFFF },
//            { 0x000000FF, 0x000000FF, 0x380038FF, 0x003838FF },
//            { 0x000032FF, 0x003838FF, 0x6A006AFF, 0x3B3B3BFF },
//            { 0x3B3B3BFF, 0x308484FF, 0x843084FF, 0x308484FF },
//            { 0x006A6AFF, 0x308484FF, 0xDA1ADAFF, 0x308484FF },
//            { 0x308484FF, 0x9B9B9BFF, 0xD070D0FF, 0x70D0D0FF },
//            { 0x1ADADAFF, 0x70D0D0FF, 0xFF6EFFFF, 0x70D0D0FF },
//            { 0xBFBFBFFF, 0xDBDBDBFF, 0xFFB0FFFF, 0xC2FFFFFF },
//            { 0x70D0D0FF, 0xB0FFFFFF, 0xFFC2FFFF, 0xC2FFFFFF },
//            { 0xBFBFBFFF, 0xFFB0FFFF, 0xFFDCFFFF, 0xFFFFFFFF },
//            { 0x000000FF, 0x000000FF, 0x000032FF, 0x003838FF },
//            { 0x000000FF, 0x000032FF, 0x0A0A64FF, 0x380038FF },
//            { 0x000000FF, 0x0A0A64FF, 0x0000AAFF, 0x0A0A64FF },
//            { 0x6A006AFF, 0x006A6AFF, 0x3E3EBCFF, 0x843084FF },
//            { 0x0A0A64FF, 0x3E3EBCFF, 0x3C3CFFFF, 0x3E3EBCFF },
//            { 0x0A0A64FF, 0x3E3EBCFF, 0x7272FFFF, 0x8C8CFFFF },
//            { 0x3E3EBCFF, 0x7272FFFF, 0x8C8CFFFF, 0xA6A6FFFF },
//            { 0x7272FFFF, 0x8C8CFFFF, 0xA6A6FFFF, 0xDCDCFFFF },
//            { 0xA6A6FFFF, 0xBFBFBFFF, 0xDCDCFFFF, 0xFFFFFFFF },
//            { 0x320000FF, 0x003200FF, 0x383800FF, 0x3B3B3BFF },
//            { 0x320000FF, 0x383800FF, 0x6A6A00FF, 0x6F6F6FFF },
//            { 0x6A6A00FF, 0x6F6F6FFF, 0x848430FF, 0xD0D070FF },
//            { 0x848430FF, 0xD0D070FF, 0xDADA1AFF, 0xD0D070FF },
//            { 0x848430FF, 0xBFBFBFFF, 0xD0D070FF, 0xFFA6A6FF },
//            { 0xD0D070FF, 0xFFFFB0FF, 0xFFFF6EFF, 0xFFFFB0FF },
//            { 0xD0D070FF, 0xFFDCDCFF, 0xFFFFB0FF, 0xFFFFFFFF },
//            { 0xFFDCDCFF, 0xDCFFDCFF, 0xFFFFC2FF, 0xFFFFFFFF },
//            { 0xBFBFBFFF, 0xDBDBDBFF, 0xFFFFDCFF, 0xFFFFFFFF },
//    };
//    private static final int[][] CUBICLE_BONUS_RAMP_VALUES = new int[][] {
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF },
//            { 0x262626FF, 0x313131FF, 0x3B3B3BFF, 0x464646FF },
//            { 0x484848FF, 0x5C5C5CFF, 0x6F6F6FFF, 0x858585FF },
//            { 0x646464FF, 0x808080FF, 0x9B9B9BFF, 0xBABABAFF },
//            { 0x7C7C7CFF, 0x9E9E9EFF, 0xBFBFBFFF, 0xE5E5E5FF },
//            { 0x8E8E8EFF, 0xB6B6B6FF, 0xDBDBDBFF, 0xFFFFFFFF },
//            { 0x9B9B9BFF, 0xC6C6C6FF, 0xEFEFEFFF, 0xFFFFFFFF },
//            { 0xA3A3A3FF, 0xD0D0D0FF, 0xFBFBFBFF, 0xFFFFFFFF },
//            { 0xA6A6A6FF, 0xD4D4D4FF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x250C00FF, 0x2D0F00FF, 0x320000FF, 0x2C160BFF },
//            { 0x491C05FF, 0x5A2409FF, 0x640A0AFF, 0x5C3420FF },
//            { 0x7E2900FF, 0x9B3501FF, 0xAA0000FF, 0x974C27FF },
//            { 0x864727FF, 0xA65A34FF, 0xBC3E3EFF, 0xBA8367FF },
//            { 0xB85625FF, 0xE36E34FF, 0xFF3C3CFF, 0xF6A075FF },
//            { 0xB36C49FF, 0xDF8A60FF, 0xFF7272FF, 0xFFC8A9FF },
//            { 0xB0775AFF, 0xDD9875FF, 0xFF8C8CFF, 0xFFDCC2FF },
//            { 0xAE816BFF, 0xDBA58BFF, 0xFFA6A6FF, 0xFFEFDCFF },
//            { 0xA9978FFF, 0xD6C1B7FF, 0xFFDCDCFF, 0xFFFFFFFF },
//            { 0x001624FF, 0x001D2DFF, 0x003838FF, 0x112A36FF },
//            { 0x002B45FF, 0x003756FF, 0x006A6AFF, 0x204F67FF },
//            { 0x174156FF, 0x21536CFF, 0x308484FF, 0x53788BFF },
//            { 0x005F8FFF, 0x0579B3FF, 0x1ADADAFF, 0x5AAFD9FF },
//            { 0x3F7088FF, 0x558FABFF, 0x70D0D0FF, 0xA4CEE3FF },
//            { 0x3A82A6FF, 0x4FA6D2FF, 0x6EFFFFFF, 0xB1F1FFFF },
//            { 0x6B92A6FF, 0x8BBBD3FF, 0xB0FFFFFF, 0xECFFFFFF },
//            { 0x7897A6FF, 0x9CC1D3FF, 0xC2FFFFFF, 0xFCFFFFFF },
//            { 0x8B9DA6FF, 0xB4C9D3FF, 0xDCFFFFFF, 0xFFFFFFFF },
//            { 0x102903FF, 0x143205FF, 0x003200FF, 0x1E3413FF },
//            { 0x23500DFF, 0x2D6312FF, 0x0A640AFF, 0x42692EFF },
//            { 0x378C0CFF, 0x46AC13FF, 0x00AA00FF, 0x66B140FF },
//            { 0x519031FF, 0x67B342FF, 0x3EBC3EFF, 0x96CD7AFF },
//            { 0x66C835FF, 0x82F848FF, 0x3CFF3CFF, 0xBDFF92FF },
//            { 0x78BE54FF, 0x99EE6FFF, 0x72FF72FF, 0xDDFFBEFF },
//            { 0x80BA63FF, 0xA4E981FF, 0x8CFF8CFF, 0xEDFFD4FF },
//            { 0x89B572FF, 0xAFE494FF, 0xA6FFA6FF, 0xFDFFE9FF },
//            { 0x9AAC91FF, 0xC5DABAFF, 0xDCFFDCFF, 0xFFFFFFFF },
//            { 0x120020FF, 0x170028FF, 0x380038FF, 0x21082EFF },
//            { 0x22003DFF, 0x2C004BFF, 0x6A006AFF, 0x3F1057FF },
//            { 0x3A104FFF, 0x4A1864FF, 0x843084FF, 0x6C477EFF },
//            { 0x4F007FFF, 0x65009FFF, 0xDA1ADAFF, 0x923EBDFF },
//            { 0x683880FF, 0x854BA1FF, 0xD070D0FF, 0xC096D5FF },
//            { 0x762E9BFF, 0x9740C3FF, 0xFF6EFFFF, 0xDB9BFBFF },
//            { 0x8C64A0FF, 0xB383CAFF, 0xFFB0FFFF, 0xFFE0FFFF },
//            { 0x9273A1FF, 0xBA96CDFF, 0xFFC2FFFF, 0xFFF3FFFF },
//            { 0x9A89A3FF, 0xC5B0D0FF, 0xFFDCFFFF, 0xFFFFFFFF },
//            { 0x00001DFF, 0x000023FF, 0x000032FF, 0x00001DFF },
//            { 0x00003AFF, 0x000047FF, 0x0A0A64FF, 0x000041FF },
//            { 0x000063FF, 0x000077FF, 0x0000AAFF, 0x000064FF },
//            { 0x000071FF, 0x00008CFF, 0x3E3EBCFF, 0x252595FF },
//            { 0x000098FF, 0x0000BBFF, 0x3C3CFFFF, 0x0F0FBBFF },
//            { 0x0F0F9CFF, 0x1818C2FF, 0x7272FFFF, 0x6060DCFF },
//            { 0x2A2A9EFF, 0x3B3BC5FF, 0x8C8CFFFF, 0x8686ECFF },
//            { 0x46469FFF, 0x5D5DC8FF, 0xA6A6FFFF, 0xADADFCFF },
//            { 0x8080A3FF, 0xA5A5CFFF, 0xDCDCFFFF, 0xFEFEFFFF },
//            { 0x3B3B03FF, 0x4A4A07FF, 0x383800FF, 0x535322FF },
//            { 0x717107FF, 0x8C8C0DFF, 0x6A6A00FF, 0x9E9E40FF },
//            { 0x797925FF, 0x979732FF, 0x848430FF, 0xB7B76DFF },
//            { 0xDEDE1EFF, 0xFFFF2DFF, 0xDADA1AFF, 0xFFFF94FF },
//            { 0xAFAF4FFF, 0xDCDC69FF, 0xD0D070FF, 0xFFFFC1FF },
//            { 0xE2E251FF, 0xFFFF6DFF, 0xFFFF6EFF, 0xFFFFDCFF },
//            { 0xC7C778FF, 0xFBFB9CFF, 0xFFFFB0FF, 0xFFFFFFFF },
//            { 0xBFBF82FF, 0xF2F2A9FF, 0xFFFFC2FF, 0xFFFFFFFF },
//            { 0xB4B491FF, 0xE5E5BBFF, 0xFFFFDCFF, 0xFFFFFFFF },
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF },
//            { 0x313131FF, 0x3B3B3BFF, 0x3B3B3BFF, 0x3B3B3BFF },
//            { 0x5C5C5CFF, 0x6F6F6FFF, 0x6F6F6FFF, 0x6F6F6FFF },
//            { 0x808080FF, 0x9B9B9BFF, 0x9B9B9BFF, 0x9B9B9BFF },
//            { 0x9E9E9EFF, 0xBFBFBFFF, 0xBFBFBFFF, 0xBFBFBFFF },
//            { 0xB6B6B6FF, 0xDBDBDBFF, 0xDBDBDBFF, 0xDBDBDBFF },
//            { 0xC6C6C6FF, 0xEFEFEFFF, 0xEFEFEFFF, 0xEFEFEFFF },
//            { 0xD0D0D0FF, 0xFBFBFBFF, 0xFBFBFBFF, 0xFBFBFBFF },
//            { 0xD4D4D4FF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x2D0F00FF, 0x320000FF, 0x320000FF, 0x320000FF },
//            { 0x5A2409FF, 0x640A0AFF, 0x640A0AFF, 0x640A0AFF },
//            { 0x9B3501FF, 0xAA0000FF, 0xAA0000FF, 0xAA0000FF },
//            { 0xA65A34FF, 0xBC3E3EFF, 0xBC3E3EFF, 0xBC3E3EFF },
//            { 0xE36E34FF, 0xFF3C3CFF, 0xFF3C3CFF, 0xFF3C3CFF },
//            { 0xDF8A60FF, 0xFF7272FF, 0xFF7272FF, 0xFF7272FF },
//            { 0xDD9875FF, 0xFF8C8CFF, 0xFF8C8CFF, 0xFF8C8CFF },
//            { 0xDBA58BFF, 0xFFA6A6FF, 0xFFA6A6FF, 0xFFA6A6FF },
//            { 0xD6C1B7FF, 0xFFDCDCFF, 0xFFDCDCFF, 0xFFDCDCFF },
//            { 0x001D2DFF, 0x003838FF, 0x003838FF, 0x003838FF },
//            { 0x003756FF, 0x006A6AFF, 0x006A6AFF, 0x006A6AFF },
//            { 0x21536CFF, 0x308484FF, 0x308484FF, 0x308484FF },
//            { 0x0579B3FF, 0x1ADADAFF, 0x1ADADAFF, 0x1ADADAFF },
//            { 0x558FABFF, 0x70D0D0FF, 0x70D0D0FF, 0x70D0D0FF },
//            { 0x4FA6D2FF, 0x6EFFFFFF, 0x6EFFFFFF, 0x6EFFFFFF },
//            { 0x8BBBD3FF, 0xB0FFFFFF, 0xB0FFFFFF, 0xB0FFFFFF },
//            { 0x9CC1D3FF, 0xC2FFFFFF, 0xC2FFFFFF, 0xC2FFFFFF },
//            { 0xB4C9D3FF, 0xDCFFFFFF, 0xDCFFFFFF, 0xDCFFFFFF },
//            { 0x143205FF, 0x003200FF, 0x003200FF, 0x003200FF },
//            { 0x2D6312FF, 0x0A640AFF, 0x0A640AFF, 0x0A640AFF },
//            { 0x46AC13FF, 0x00AA00FF, 0x00AA00FF, 0x00AA00FF },
//            { 0x67B342FF, 0x3EBC3EFF, 0x3EBC3EFF, 0x3EBC3EFF },
//            { 0x82F848FF, 0x3CFF3CFF, 0x3CFF3CFF, 0x3CFF3CFF },
//            { 0x99EE6FFF, 0x72FF72FF, 0x72FF72FF, 0x72FF72FF },
//            { 0xA4E981FF, 0x8CFF8CFF, 0x8CFF8CFF, 0x8CFF8CFF },
//            { 0xAFE494FF, 0xA6FFA6FF, 0xA6FFA6FF, 0xA6FFA6FF },
//            { 0xC5DABAFF, 0xDCFFDCFF, 0xDCFFDCFF, 0xDCFFDCFF },
//            { 0x170028FF, 0x380038FF, 0x380038FF, 0x380038FF },
//            { 0x2C004BFF, 0x6A006AFF, 0x6A006AFF, 0x6A006AFF },
//            { 0x4A1864FF, 0x843084FF, 0x843084FF, 0x843084FF },
//            { 0x65009FFF, 0xDA1ADAFF, 0xDA1ADAFF, 0xDA1ADAFF },
//            { 0x854BA1FF, 0xD070D0FF, 0xD070D0FF, 0xD070D0FF },
//            { 0x9740C3FF, 0xFF6EFFFF, 0xFF6EFFFF, 0xFF6EFFFF },
//            { 0xB383CAFF, 0xFFB0FFFF, 0xFFB0FFFF, 0xFFB0FFFF },
//            { 0xBA96CDFF, 0xFFC2FFFF, 0xFFC2FFFF, 0xFFC2FFFF },
//            { 0xC5B0D0FF, 0xFFDCFFFF, 0xFFDCFFFF, 0xFFDCFFFF },
//            { 0x000023FF, 0x000032FF, 0x000032FF, 0x000032FF },
//            { 0x000047FF, 0x0A0A64FF, 0x0A0A64FF, 0x0A0A64FF },
//            { 0x000077FF, 0x0000AAFF, 0x0000AAFF, 0x0000AAFF },
//            { 0x00008CFF, 0x3E3EBCFF, 0x3E3EBCFF, 0x3E3EBCFF },
//            { 0x0000BBFF, 0x3C3CFFFF, 0x3C3CFFFF, 0x3C3CFFFF },
//            { 0x1818C2FF, 0x7272FFFF, 0x7272FFFF, 0x7272FFFF },
//            { 0x3B3BC5FF, 0x8C8CFFFF, 0x8C8CFFFF, 0x8C8CFFFF },
//            { 0x5D5DC8FF, 0xA6A6FFFF, 0xA6A6FFFF, 0xA6A6FFFF },
//            { 0xA5A5CFFF, 0xDCDCFFFF, 0xDCDCFFFF, 0xDCDCFFFF },
//            { 0x4A4A07FF, 0x383800FF, 0x383800FF, 0x383800FF },
//            { 0x8C8C0DFF, 0x6A6A00FF, 0x6A6A00FF, 0x6A6A00FF },
//            { 0x979732FF, 0x848430FF, 0x848430FF, 0x848430FF },
//            { 0xFFFF2DFF, 0xDADA1AFF, 0xDADA1AFF, 0xDADA1AFF },
//            { 0xDCDC69FF, 0xD0D070FF, 0xD0D070FF, 0xD0D070FF },
//            { 0xFFFF6DFF, 0xFFFF6EFF, 0xFFFF6EFF, 0xFFFF6EFF },
//            { 0xFBFB9CFF, 0xFFFFB0FF, 0xFFFFB0FF, 0xFFFFB0FF },
//            { 0xF2F2A9FF, 0xFFFFC2FF, 0xFFFFC2FF, 0xFFFFC2FF },
//            { 0xE5E5BBFF, 0xFFFFDCFF, 0xFFFFDCFF, 0xFFFFDCFF },
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF },
//            { 0x262626FF, 0x313131FF, 0x3B3B3BFF, 0x464646FF },
//            { 0x484848FF, 0x5C5C5CFF, 0x6F6F6FFF, 0x858585FF },
//            { 0x646464FF, 0x808080FF, 0x9B9B9BFF, 0xBABABAFF },
//            { 0x7C7C7CFF, 0x9E9E9EFF, 0xBFBFBFFF, 0xE5E5E5FF },
//            { 0x8E8E8EFF, 0xB6B6B6FF, 0xDBDBDBFF, 0xFFFFFFFF },
//            { 0x9B9B9BFF, 0xC6C6C6FF, 0xEFEFEFFF, 0xFFFFFFFF },
//            { 0xA3A3A3FF, 0xD0D0D0FF, 0xFBFBFBFF, 0xFFFFFFFF },
//            { 0xA6A6A6FF, 0xD4D4D4FF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x250C00FF, 0x2D0F00FF, 0x320000FF, 0x2C160BFF },
//            { 0x491C05FF, 0x5A2409FF, 0x640A0AFF, 0x5C3420FF },
//            { 0x7E2900FF, 0x9B3501FF, 0xAA0000FF, 0x974C27FF },
//            { 0x864727FF, 0xA65A34FF, 0xBC3E3EFF, 0xBA8367FF },
//            { 0xB85625FF, 0xE36E34FF, 0xFF3C3CFF, 0xF6A075FF },
//            { 0xB36C49FF, 0xDF8A60FF, 0xFF7272FF, 0xFFC8A9FF },
//            { 0xB0775AFF, 0xDD9875FF, 0xFF8C8CFF, 0xFFDCC2FF },
//            { 0xAE816BFF, 0xDBA58BFF, 0xFFA6A6FF, 0xFFEFDCFF },
//            { 0xA9978FFF, 0xD6C1B7FF, 0xFFDCDCFF, 0xFFFFFFFF },
//            { 0x001624FF, 0x001D2DFF, 0x003838FF, 0x112A36FF },
//            { 0x002B45FF, 0x003756FF, 0x006A6AFF, 0x204F67FF },
//            { 0x174156FF, 0x21536CFF, 0x308484FF, 0x53788BFF },
//            { 0x005F8FFF, 0x0579B3FF, 0x1ADADAFF, 0x5AAFD9FF },
//            { 0x3F7088FF, 0x558FABFF, 0x70D0D0FF, 0xA4CEE3FF },
//            { 0x3A82A6FF, 0x4FA6D2FF, 0x6EFFFFFF, 0xB1F1FFFF },
//            { 0x6B92A6FF, 0x8BBBD3FF, 0xB0FFFFFF, 0xECFFFFFF },
//            { 0x7897A6FF, 0x9CC1D3FF, 0xC2FFFFFF, 0xFCFFFFFF },
//            { 0x8B9DA6FF, 0xB4C9D3FF, 0xDCFFFFFF, 0xFFFFFFFF },
//            { 0x102903FF, 0x143205FF, 0x003200FF, 0x1E3413FF },
//            { 0x23500DFF, 0x2D6312FF, 0x0A640AFF, 0x42692EFF },
//            { 0x378C0CFF, 0x46AC13FF, 0x00AA00FF, 0x66B140FF },
//            { 0x519031FF, 0x67B342FF, 0x3EBC3EFF, 0x96CD7AFF },
//            { 0x66C835FF, 0x82F848FF, 0x3CFF3CFF, 0xBDFF92FF },
//            { 0x78BE54FF, 0x99EE6FFF, 0x72FF72FF, 0xDDFFBEFF },
//            { 0x80BA63FF, 0xA4E981FF, 0x8CFF8CFF, 0xEDFFD4FF },
//            { 0x89B572FF, 0xAFE494FF, 0xA6FFA6FF, 0xFDFFE9FF },
//            { 0x9AAC91FF, 0xC5DABAFF, 0xDCFFDCFF, 0xFFFFFFFF },
//            { 0x120020FF, 0x170028FF, 0x380038FF, 0x21082EFF },
//            { 0x22003DFF, 0x2C004BFF, 0x6A006AFF, 0x3F1057FF },
//            { 0x3A104FFF, 0x4A1864FF, 0x843084FF, 0x6C477EFF },
//            { 0x4F007FFF, 0x65009FFF, 0xDA1ADAFF, 0x923EBDFF },
//            { 0x683880FF, 0x854BA1FF, 0xD070D0FF, 0xC096D5FF },
//            { 0x762E9BFF, 0x9740C3FF, 0xFF6EFFFF, 0xDB9BFBFF },
//            { 0x8C64A0FF, 0xB383CAFF, 0xFFB0FFFF, 0xFFE0FFFF },
//            { 0x9273A1FF, 0xBA96CDFF, 0xFFC2FFFF, 0xFFF3FFFF },
//            { 0x9A89A3FF, 0xC5B0D0FF, 0xFFDCFFFF, 0xFFFFFFFF },
//            { 0x00001DFF, 0x000023FF, 0x000032FF, 0x00001DFF },
//            { 0x00003AFF, 0x000047FF, 0x0A0A64FF, 0x000041FF },
//            { 0x000063FF, 0x000077FF, 0x0000AAFF, 0x000064FF },
//            { 0x000071FF, 0x00008CFF, 0x3E3EBCFF, 0x252595FF },
//            { 0x000098FF, 0x0000BBFF, 0x3C3CFFFF, 0x0F0FBBFF },
//            { 0x0F0F9CFF, 0x1818C2FF, 0x7272FFFF, 0x6060DCFF },
//            { 0x2A2A9EFF, 0x3B3BC5FF, 0x8C8CFFFF, 0x8686ECFF },
//            { 0x46469FFF, 0x5D5DC8FF, 0xA6A6FFFF, 0xADADFCFF },
//            { 0x8080A3FF, 0xA5A5CFFF, 0xDCDCFFFF, 0xFEFEFFFF },
//            { 0x3B3B03FF, 0x4A4A07FF, 0x383800FF, 0x535322FF },
//            { 0x717107FF, 0x8C8C0DFF, 0x6A6A00FF, 0x9E9E40FF },
//            { 0x797925FF, 0x979732FF, 0x848430FF, 0xB7B76DFF },
//            { 0xDEDE1EFF, 0xFFFF2DFF, 0xDADA1AFF, 0xFFFF94FF },
//            { 0xAFAF4FFF, 0xDCDC69FF, 0xD0D070FF, 0xFFFFC1FF },
//            { 0xE2E251FF, 0xFFFF6DFF, 0xFFFF6EFF, 0xFFFFDCFF },
//            { 0xC7C778FF, 0xFBFB9CFF, 0xFFFFB0FF, 0xFFFFFFFF },
//            { 0xBFBF82FF, 0xF2F2A9FF, 0xFFFFC2FF, 0xFFFFFFFF },
//            { 0xB4B491FF, 0xE5E5BBFF, 0xFFFFDCFF, 0xFFFFFFFF },
//            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
//            { 0x000000FF, 0x000000FF, 0x000000FF, 0x000000FF },
//            { 0x3B3B3BFF, 0x313131FF, 0x3B3B3BFF, 0x464646FF },
//            { 0x6F6F6FFF, 0x5C5C5CFF, 0x6F6F6FFF, 0x858585FF },
//            { 0x9B9B9BFF, 0x808080FF, 0x9B9B9BFF, 0xBABABAFF },
//            { 0xBFBFBFFF, 0x9E9E9EFF, 0xBFBFBFFF, 0xE5E5E5FF },
//            { 0xDBDBDBFF, 0xB6B6B6FF, 0xDBDBDBFF, 0xFFFFFFFF },
//            { 0xEFEFEFFF, 0xC6C6C6FF, 0xEFEFEFFF, 0xFFFFFFFF },
//            { 0xFBFBFBFF, 0xD0D0D0FF, 0xFBFBFBFF, 0xFFFFFFFF },
//            { 0xFFFFFFFF, 0xD4D4D4FF, 0xFFFFFFFF, 0xFFFFFFFF },
//            { 0x320000FF, 0x2D0F00FF, 0x320000FF, 0x2C160BFF },
//            { 0x640A0AFF, 0x5A2409FF, 0x640A0AFF, 0x5C3420FF },
//            { 0xAA0000FF, 0x9B3501FF, 0xAA0000FF, 0x974C27FF },
//            { 0xBC3E3EFF, 0xA65A34FF, 0xBC3E3EFF, 0xBA8367FF },
//            { 0xFF3C3CFF, 0xE36E34FF, 0xFF3C3CFF, 0xF6A075FF },
//            { 0xFF7272FF, 0xDF8A60FF, 0xFF7272FF, 0xFFC8A9FF },
//            { 0xFF8C8CFF, 0xDD9875FF, 0xFF8C8CFF, 0xFFDCC2FF },
//            { 0xFFA6A6FF, 0xDBA58BFF, 0xFFA6A6FF, 0xFFEFDCFF },
//            { 0xFFDCDCFF, 0xD6C1B7FF, 0xFFDCDCFF, 0xFFFFFFFF },
//            { 0x003838FF, 0x001D2DFF, 0x003838FF, 0x112A36FF },
//            { 0x006A6AFF, 0x003756FF, 0x006A6AFF, 0x204F67FF },
//            { 0x308484FF, 0x21536CFF, 0x308484FF, 0x53788BFF },
//            { 0x1ADADAFF, 0x0579B3FF, 0x1ADADAFF, 0x5AAFD9FF },
//            { 0x70D0D0FF, 0x558FABFF, 0x70D0D0FF, 0xA4CEE3FF },
//            { 0x6EFFFFFF, 0x4FA6D2FF, 0x6EFFFFFF, 0xB1F1FFFF },
//            { 0xB0FFFFFF, 0x8BBBD3FF, 0xB0FFFFFF, 0xECFFFFFF },
//            { 0xC2FFFFFF, 0x9CC1D3FF, 0xC2FFFFFF, 0xFCFFFFFF },
//            { 0xDCFFFFFF, 0xB4C9D3FF, 0xDCFFFFFF, 0xFFFFFFFF },
//            { 0x003200FF, 0x143205FF, 0x003200FF, 0x1E3413FF },
//            { 0x0A640AFF, 0x2D6312FF, 0x0A640AFF, 0x42692EFF },
//            { 0x00AA00FF, 0x46AC13FF, 0x00AA00FF, 0x66B140FF },
//            { 0x3EBC3EFF, 0x67B342FF, 0x3EBC3EFF, 0x96CD7AFF },
//            { 0x3CFF3CFF, 0x82F848FF, 0x3CFF3CFF, 0xBDFF92FF },
//            { 0x72FF72FF, 0x99EE6FFF, 0x72FF72FF, 0xDDFFBEFF },
//            { 0x8CFF8CFF, 0xA4E981FF, 0x8CFF8CFF, 0xEDFFD4FF },
//            { 0xA6FFA6FF, 0xAFE494FF, 0xA6FFA6FF, 0xFDFFE9FF },
//            { 0xDCFFDCFF, 0xC5DABAFF, 0xDCFFDCFF, 0xFFFFFFFF },
//            { 0x380038FF, 0x170028FF, 0x380038FF, 0x21082EFF },
//            { 0x6A006AFF, 0x2C004BFF, 0x6A006AFF, 0x3F1057FF },
//            { 0x843084FF, 0x4A1864FF, 0x843084FF, 0x6C477EFF },
//            { 0xDA1ADAFF, 0x65009FFF, 0xDA1ADAFF, 0x923EBDFF },
//            { 0xD070D0FF, 0x854BA1FF, 0xD070D0FF, 0xC096D5FF },
//            { 0xFF6EFFFF, 0x9740C3FF, 0xFF6EFFFF, 0xDB9BFBFF },
//            { 0xFFB0FFFF, 0xB383CAFF, 0xFFB0FFFF, 0xFFE0FFFF },
//            { 0xFFC2FFFF, 0xBA96CDFF, 0xFFC2FFFF, 0xFFF3FFFF },
//            { 0xFFDCFFFF, 0xC5B0D0FF, 0xFFDCFFFF, 0xFFFFFFFF },
//            { 0x000032FF, 0x000023FF, 0x000032FF, 0x00001DFF },
//            { 0x0A0A64FF, 0x000047FF, 0x0A0A64FF, 0x000041FF },
//            { 0x0000AAFF, 0x000077FF, 0x0000AAFF, 0x000064FF },
//            { 0x3E3EBCFF, 0x00008CFF, 0x3E3EBCFF, 0x252595FF },
//            { 0x3C3CFFFF, 0x0000BBFF, 0x3C3CFFFF, 0x0F0FBBFF },
//            { 0x7272FFFF, 0x1818C2FF, 0x7272FFFF, 0x6060DCFF },
//            { 0x8C8CFFFF, 0x3B3BC5FF, 0x8C8CFFFF, 0x8686ECFF },
//            { 0xA6A6FFFF, 0x5D5DC8FF, 0xA6A6FFFF, 0xADADFCFF },
//            { 0xDCDCFFFF, 0xA5A5CFFF, 0xDCDCFFFF, 0xFEFEFFFF },
//            { 0x383800FF, 0x4A4A07FF, 0x383800FF, 0x535322FF },
//            { 0x6A6A00FF, 0x8C8C0DFF, 0x6A6A00FF, 0x9E9E40FF },
//            { 0x848430FF, 0x979732FF, 0x848430FF, 0xB7B76DFF },
//            { 0xDADA1AFF, 0xFFFF2DFF, 0xDADA1AFF, 0xFFFF94FF },
//            { 0xD0D070FF, 0xDCDC69FF, 0xD0D070FF, 0xFFFFC1FF },
//            { 0xFFFF6EFF, 0xFFFF6DFF, 0xFFFF6EFF, 0xFFFFDCFF },
//            { 0xFFFFB0FF, 0xFBFB9CFF, 0xFFFFB0FF, 0xFFFFFFFF },
//            { 0xFFFFC2FF, 0xF2F2A9FF, 0xFFFFC2FF, 0xFFFFFFFF },
//            { 0xFFFFDCFF, 0xE5E5BBFF, 0xFFFFDCFF, 0xFFFFFFFF },
//    };

    public static final int[] TwirlBonusPalette = new int[256];
    public static final byte[][] TWIRL_RAMPS = new byte[][]{
            { 0, 0, 0, 0 },
            { 1, 1, 1, 3 },
            { 1, 1, 2, 37 },
            { 1, 2, 3, 6 },
            { 1, 36, 4, 3 },
            { 1, 36, 5, 37 },
            { 1, 3, 6, 38 },
            { 1, 3, 7, 15 },
            { 1, 3, 8, 38 },
            { 4, 37, 9, 49 },
            { 5, 37, 10, 9 },
            { 6, 9, 11, 51 },
            { 36, 5, 12, 45 },
            { 3, 38, 13, 58 },
            { 36, 9, 14, 9 },
            { 7, 11, 15, 53 },
            { 37, 14, 16, 44 },
            { 58, 51, 17, 18 },
            { 11, 49, 18, 24 },
            { 16, 45, 19, 63 },
            { 51, 18, 20, 34 },
            { 49, 18, 21, 26 },
            { 15, 53, 22, 20 },
            { 15, 53, 23, 25 },
            { 51, 18, 24, 25 },
            { 41, 53, 25, 35 },
            { 46, 29, 26, 35 },
            { 22, 25, 27, 34 },
            { 44, 24, 28, 29 },
            { 49, 18, 29, 31 },
            { 53, 17, 30, 20 },
            { 24, 26, 31, 35 },
            { 24, 29, 32, 26 },
            { 25, 29, 33, 35 },
            { 25, 29, 34, 29 },
            { 29, 31, 35, 9 },
            { 1, 1, 36, 10 },
            { 1, 2, 37, 9 },
            { 8, 6, 38, 6 },
            { 38, 58, 39, 58 },
            { 37, 9, 40, 16 },
            { 9, 51, 41, 51 },
            { 9, 16, 42, 16 },
            { 58, 15, 43, 22 },
            { 9, 16, 44, 18 },
            { 40, 49, 45, 44 },
            { 10, 42, 46, 26 },
            { 2, 3, 47, 9 },
            { 6, 11, 48, 41 },
            { 37, 9, 49, 18 },
            { 16, 21, 50, 63 },
            { 11, 41, 51, 41 },
            { 63, 32, 52, 32 },
            { 7, 11, 53, 18 },
            { 14, 41, 54, 44 },
            { 57, 27, 55, 34 },
            { 10, 42, 56, 42 },
            { 59, 23, 57, 27 },
            { 6, 11, 58, 11 },
            { 13, 15, 59, 15 },
            { 59, 22, 60, 22 },
            { 23, 27, 61, 27 },
            { 34, 33, 62, 33 },
            { 40, 44, 63, 24 },
    };
    public static final int[][] TWIRL_RAMP_VALUES = new int[][]{
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x13071dff, 0x13071dff, 0x13071dff, 0x304024ff },
            { 0x13071dff, 0x13071dff, 0x2e1d3fff, 0x4e3982ff },
            { 0x13071dff, 0x2e1d3fff, 0x304024ff, 0x6d3e41ff },
            { 0x13071dff, 0x213366ff, 0x5f1255ff, 0x304024ff },
            { 0x13071dff, 0x213366ff, 0x491aa3ff, 0x4e3982ff },
            { 0x13071dff, 0x304024ff, 0x6d3e41ff, 0x337c28ff },
            { 0x13071dff, 0x304024ff, 0x535d26ff, 0x8d7e3cff },
            { 0x13071dff, 0x304024ff, 0x93150cff, 0x337c28ff },
            { 0x5f1255ff, 0x4e3982ff, 0x4e6d80ff, 0x9474a7ff },
            { 0x491aa3ff, 0x4e3982ff, 0x5e4dbbff, 0x4e6d80ff },
            { 0x6d3e41ff, 0x4e6d80ff, 0x895769ff, 0x4cb082ff },
            { 0x213366ff, 0x491aa3ff, 0x733ee4ff, 0x4db9dcff },
            { 0x304024ff, 0x337c28ff, 0xae4726ff, 0x2fa632ff },
            { 0x213366ff, 0x4e6d80ff, 0x9a3390ff, 0x4e6d80ff },
            { 0x535d26ff, 0x895769ff, 0x8d7e3cff, 0xa8887bff },
            { 0x4e3982ff, 0x9a3390ff, 0x648c9dff, 0xcd66c6ff },
            { 0x2fa632ff, 0x4cb082ff, 0xe46c7aff, 0x8ba5a9ff },
            { 0x895769ff, 0x9474a7ff, 0x8ba5a9ff, 0xdf8fc3ff },
            { 0x648c9dff, 0x4db9dcff, 0xfd3dedff, 0x6fd4cbff },
            { 0x4cb082ff, 0x8ba5a9ff, 0xcf9894ff, 0xbdef9cff },
            { 0x9474a7ff, 0x8ba5a9ff, 0x99a1deff, 0xe7bdf4ff },
            { 0x8d7e3cff, 0xa8887bff, 0x88c262ff, 0xcf9894ff },
            { 0x8d7e3cff, 0xa8887bff, 0xbaa150ff, 0xb2bea1ff },
            { 0x4cb082ff, 0x8ba5a9ff, 0xdf8fc3ff, 0xb2bea1ff },
            { 0xaf597dff, 0xa8887bff, 0xb2bea1ff, 0xfaf3f7ff },
            { 0x98c2eeff, 0xcdc8c5ff, 0xe7bdf4ff, 0xfaf3f7ff },
            { 0x88c262ff, 0xb2bea1ff, 0xf2b57dff, 0xbdef9cff },
            { 0xcd66c6ff, 0xdf8fc3ff, 0x7befb4ff, 0xcdc8c5ff },
            { 0x9474a7ff, 0x8ba5a9ff, 0xcdc8c5ff, 0xc6ecfaff },
            { 0xa8887bff, 0xe46c7aff, 0x6eea3cff, 0xcf9894ff },
            { 0xdf8fc3ff, 0xe7bdf4ff, 0xc6ecfaff, 0xfaf3f7ff },
            { 0xdf8fc3ff, 0xcdc8c5ff, 0x9fede3ff, 0xe7bdf4ff },
            { 0xb2bea1ff, 0xcdc8c5ff, 0xf3e8b4ff, 0xfaf3f7ff },
            { 0xb2bea1ff, 0xcdc8c5ff, 0xbdef9cff, 0xcdc8c5ff },
            { 0xcdc8c5ff, 0xc6ecfaff, 0xfaf3f7ff, 0x4e6d80ff },
            { 0x13071dff, 0x13071dff, 0x213366ff, 0x5e4dbbff },
            { 0x13071dff, 0x2e1d3fff, 0x4e3982ff, 0x4e6d80ff },
            { 0x93150cff, 0x6d3e41ff, 0x337c28ff, 0x6d3e41ff },
            { 0x337c28ff, 0x2fa632ff, 0xdf3527ff, 0x2fa632ff },
            { 0x4e3982ff, 0x4e6d80ff, 0xb435c0ff, 0x648c9dff },
            { 0x4e6d80ff, 0x4cb082ff, 0xaf597dff, 0x4cb082ff },
            { 0x4e6d80ff, 0x648c9dff, 0x9064ceff, 0x648c9dff },
            { 0x2fa632ff, 0x8d7e3cff, 0xc77e37ff, 0x88c262ff },
            { 0x4e6d80ff, 0x648c9dff, 0xcd66c6ff, 0x8ba5a9ff },
            { 0xb435c0ff, 0x9474a7ff, 0x4db9dcff, 0xcd66c6ff },
            { 0x5e4dbbff, 0x9064ceff, 0x98c2eeff, 0xe7bdf4ff },
            { 0x2e1d3fff, 0x304024ff, 0x8e0f5cff, 0x4e6d80ff },
            { 0x6d3e41ff, 0x895769ff, 0x23bf27ff, 0xaf597dff },
            { 0x4e3982ff, 0x4e6d80ff, 0x9474a7ff, 0x8ba5a9ff },
            { 0x648c9dff, 0x99a1deff, 0xd368faff, 0x6fd4cbff },
            { 0x895769ff, 0xaf597dff, 0x4cb082ff, 0xaf597dff },
            { 0x6fd4cbff, 0x9fede3ff, 0xfa99e6ff, 0x9fede3ff },
            { 0x535d26ff, 0x895769ff, 0xa8887bff, 0x8ba5a9ff },
            { 0x9a3390ff, 0xaf597dff, 0x32dc97ff, 0xcd66c6ff },
            { 0xc0c934ff, 0xf2b57dff, 0xeee037ff, 0xbdef9cff },
            { 0x5e4dbbff, 0x9064ceff, 0x4c7fdeff, 0x9064ceff },
            { 0x9c9504ff, 0xbaa150ff, 0xc0c934ff, 0xf2b57dff },
            { 0x6d3e41ff, 0x895769ff, 0x2fa632ff, 0x895769ff },
            { 0xae4726ff, 0x8d7e3cff, 0x9c9504ff, 0x8d7e3cff },
            { 0x9c9504ff, 0x88c262ff, 0xf19318ff, 0x88c262ff },
            { 0xbaa150ff, 0xf2b57dff, 0xaafb1aff, 0xf2b57dff },
            { 0xbdef9cff, 0xf3e8b4ff, 0xfafc56ff, 0xf3e8b4ff },
            { 0xb435c0ff, 0xcd66c6ff, 0x6fd4cbff, 0xdf8fc3ff },
    };
    private static final int[][] TWIRL_BONUS_RAMP_VALUES = new int[][] {
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x0D0315FF, 0x0F0616FF, 0x13071DFF, 0x160E1CFF },
            { 0x1E112CFF, 0x251931FF, 0x2E1D3FFF, 0x392E44FF },
            { 0x1E2B14FF, 0x283420FF, 0x304024FF, 0x45503DFF },
            { 0x43063BFF, 0x491342FF, 0x5F1255FF, 0x64325EFF },
            { 0x310B79FF, 0x3B1A7AFF, 0x491AA3FF, 0x5A3B95FF },
            { 0x4B2528FF, 0x573638FF, 0x6D3E41FF, 0x7F6062FF },
            { 0x363E12FF, 0x454C25FF, 0x535D26FF, 0x707652FF },
            { 0x6B0600FF, 0x701711FF, 0x93150CFF, 0x8F3C37FF },
            { 0x2F4857FF, 0x435966FF, 0x4E6D80FF, 0x788D99FF },
            { 0x3C2F87FF, 0x4E4290FF, 0x5E4DBBFF, 0x8176BEFF },
            { 0x5D3544FF, 0x6E4B57FF, 0x895769FF, 0xA58490FF },
            { 0x4C22A7FF, 0x5E38ADFF, 0x733EE4FF, 0x9370DCFF },
            { 0x7B290EFF, 0x883F28FF, 0xAE4726FF, 0xBB7862FF },
            { 0x6C1964FF, 0x793172FF, 0x9A3390FF, 0xAB68A5FF },
            { 0x5E521DFF, 0x73683AFF, 0x8D7E3CFF, 0xB2A87DFF },
            { 0x3D5D6AFF, 0x56727EFF, 0x648C9DFF, 0x9AB4BFFF },
            { 0x9F3F4AFF, 0xB46069FF, 0xE46C7AFF, 0xFFB3BCFF },
            { 0x586D70FF, 0x76888BFF, 0x8BA5A9FF, 0xC8D9DBFF },
            { 0xB319A7FF, 0xC63FBAFF, 0xFD3DEDFF, 0xFF94FFFF },
            { 0x8C605CFF, 0xA7817EFF, 0xCF9894FF, 0xFFDCD9FF },
            { 0x616899FF, 0x8186B1FF, 0x99A1DEFF, 0xD8DDFFFF },
            { 0x558336FF, 0x749D59FF, 0x88C262FF, 0xC9EFB0FF },
            { 0x7D6928FF, 0x97864DFF, 0xBAA150FF, 0xE9D9A4FF },
            { 0x985781FF, 0xB47BA0FF, 0xDF8FC3FF, 0xFFDBFDFF },
            { 0x737D65FF, 0x949D88FF, 0xB2BEA1FF, 0xF3FBE8FF },
            { 0x9977A4FF, 0xBDA0C7FF, 0xE7BDF4FF, 0xFFFFFFFF },
            { 0xA47346FF, 0xC39871FF, 0xF2B57DFF, 0xFFFFDBFF },
            { 0x46A374FF, 0x6EC097FF, 0x7BEFB4FF, 0xD4FFF9FF },
            { 0x86827FFF, 0xAAA6A4FF, 0xCDC8C5FF, 0xFFFFFFFF },
            { 0x3FA217FF, 0x62BA3FFF, 0x6EEA3CFF, 0xBCFF9BFF },
            { 0x7D9BA7FF, 0xA8C3CCFF, 0xC6ECFAFF, 0xFFFFFFFF },
            { 0x609F97FF, 0x8AC1BAFF, 0x9FEDE3FF, 0xF8FFFFFF },
            { 0xA0976DFF, 0xC8C19CFF, 0xF3E8B4FF, 0xFFFFFFFF },
            { 0x78A05DFF, 0xA0C389FF, 0xBDEF9CFF, 0xFFFFF9FF },
            { 0xA39EA1FF, 0xCFCACDFF, 0xFAF3F7FF, 0xFFFFFFFF },
            { 0x12214AFF, 0x1D2A4EFF, 0x213366FF, 0x394466FF },
            { 0x33225DFF, 0x403165FF, 0x4E3982FF, 0x665988FF },
            { 0x1B5613FF, 0x2F6227FF, 0x337C28FF, 0x5D8D56FF },
            { 0xA1190EFF, 0xAB342AFF, 0xDF3527FF, 0xE2736AFF },
            { 0x7E1888FF, 0x8D3496FF, 0xB435C0FF, 0xC875D0FF },
            { 0x793451FF, 0x8B4E68FF, 0xAF597DFF, 0xCA92A9FF },
            { 0x603C91FF, 0x7657A1FF, 0x9064CEFF, 0xB99CE1FF },
            { 0x8A4F16FF, 0x9E6B39FF, 0xC77E37FF, 0xE7B789FF },
            { 0x8D3A88FF, 0xA35B9FFF, 0xCD66C6FF, 0xF1ADECFF },
            { 0x277E9AFF, 0x4995AEFF, 0x4DB9DCFF, 0x9AE1F8FF },
            { 0x5E80A3FF, 0x82A0BFFF, 0x98C2EEFF, 0xE4FFFFFF },
            { 0x67013FFF, 0x6D134AFF, 0x8E0F5CFF, 0x8F3C6EFF },
            { 0x0B880EFF, 0x27952AFF, 0x23BF27FF, 0x66CC69FF },
            { 0x624871FF, 0x796386FF, 0x9474A7FF, 0xBFAACBFF },
            { 0x913BB0FF, 0xA95EC4FF, 0xD368FAFF, 0xFBB5FFFF },
            { 0x297954FF, 0x468D6CFF, 0x4CB082FF, 0x8ED0B2FF },
            { 0xAA5C9AFF, 0xC985BBFF, 0xFA99E6FF, 0xFFEFFFFF },
            { 0x70574CFF, 0x897269FF, 0xA8887BFF, 0xD4BFB7FF },
            { 0x119A63FF, 0x36AD7DFF, 0x32DC97FF, 0x88F7CAFF },
            { 0x9F940CFF, 0xC2B841FF, 0xEEE037FF, 0xFFFFB6FF },
            { 0x2B53A0FF, 0x4568ABFF, 0x4C7FDEFF, 0x85A7E5FF },
            { 0x7F860EFF, 0x9EA43BFF, 0xC0C934FF, 0xFBFF9FFF },
            { 0x157518FF, 0x2E8231FF, 0x2FA632FF, 0x69B76BFF },
            { 0x696300FF, 0x7F7A14FF, 0x9C9504FF, 0xC3BF60FF },
            { 0xA85C00FF, 0xBF7D26FF, 0xF19318FF, 0xFFD584FF },
            { 0x6BAC00FF, 0x90C92BFF, 0xAAFB1AFF, 0xF6FF98FF },
            { 0xA6A722FF, 0xCDCF5AFF, 0xFAFC56FF, 0xFFFFDAFF },
            { 0x3F9088FF, 0x64ABA5FF, 0x6FD4CBFF, 0xC2FFFEFF },
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x0F0616FF, 0x13071DFF, 0x13071DFF, 0x13071DFF },
            { 0x251931FF, 0x2E1D3FFF, 0x2E1D3FFF, 0x2E1D3FFF },
            { 0x283420FF, 0x304024FF, 0x304024FF, 0x304024FF },
            { 0x491342FF, 0x5F1255FF, 0x5F1255FF, 0x5F1255FF },
            { 0x3B1A7AFF, 0x491AA3FF, 0x491AA3FF, 0x491AA3FF },
            { 0x573638FF, 0x6D3E41FF, 0x6D3E41FF, 0x6D3E41FF },
            { 0x454C25FF, 0x535D26FF, 0x535D26FF, 0x535D26FF },
            { 0x701711FF, 0x93150CFF, 0x93150CFF, 0x93150CFF },
            { 0x435966FF, 0x4E6D80FF, 0x4E6D80FF, 0x4E6D80FF },
            { 0x4E4290FF, 0x5E4DBBFF, 0x5E4DBBFF, 0x5E4DBBFF },
            { 0x6E4B57FF, 0x895769FF, 0x895769FF, 0x895769FF },
            { 0x5E38ADFF, 0x733EE4FF, 0x733EE4FF, 0x733EE4FF },
            { 0x883F28FF, 0xAE4726FF, 0xAE4726FF, 0xAE4726FF },
            { 0x793172FF, 0x9A3390FF, 0x9A3390FF, 0x9A3390FF },
            { 0x73683AFF, 0x8D7E3CFF, 0x8D7E3CFF, 0x8D7E3CFF },
            { 0x56727EFF, 0x648C9DFF, 0x648C9DFF, 0x648C9DFF },
            { 0xB46069FF, 0xE46C7AFF, 0xE46C7AFF, 0xE46C7AFF },
            { 0x76888BFF, 0x8BA5A9FF, 0x8BA5A9FF, 0x8BA5A9FF },
            { 0xC63FBAFF, 0xFD3DEDFF, 0xFD3DEDFF, 0xFD3DEDFF },
            { 0xA7817EFF, 0xCF9894FF, 0xCF9894FF, 0xCF9894FF },
            { 0x8186B1FF, 0x99A1DEFF, 0x99A1DEFF, 0x99A1DEFF },
            { 0x749D59FF, 0x88C262FF, 0x88C262FF, 0x88C262FF },
            { 0x97864DFF, 0xBAA150FF, 0xBAA150FF, 0xBAA150FF },
            { 0xB47BA0FF, 0xDF8FC3FF, 0xDF8FC3FF, 0xDF8FC3FF },
            { 0x949D88FF, 0xB2BEA1FF, 0xB2BEA1FF, 0xB2BEA1FF },
            { 0xBDA0C7FF, 0xE7BDF4FF, 0xE7BDF4FF, 0xE7BDF4FF },
            { 0xC39871FF, 0xF2B57DFF, 0xF2B57DFF, 0xF2B57DFF },
            { 0x6EC097FF, 0x7BEFB4FF, 0x7BEFB4FF, 0x7BEFB4FF },
            { 0xAAA6A4FF, 0xCDC8C5FF, 0xCDC8C5FF, 0xCDC8C5FF },
            { 0x62BA3FFF, 0x6EEA3CFF, 0x6EEA3CFF, 0x6EEA3CFF },
            { 0xA8C3CCFF, 0xC6ECFAFF, 0xC6ECFAFF, 0xC6ECFAFF },
            { 0x8AC1BAFF, 0x9FEDE3FF, 0x9FEDE3FF, 0x9FEDE3FF },
            { 0xC8C19CFF, 0xF3E8B4FF, 0xF3E8B4FF, 0xF3E8B4FF },
            { 0xA0C389FF, 0xBDEF9CFF, 0xBDEF9CFF, 0xBDEF9CFF },
            { 0xCFCACDFF, 0xFAF3F7FF, 0xFAF3F7FF, 0xFAF3F7FF },
            { 0x1D2A4EFF, 0x213366FF, 0x213366FF, 0x213366FF },
            { 0x403165FF, 0x4E3982FF, 0x4E3982FF, 0x4E3982FF },
            { 0x2F6227FF, 0x337C28FF, 0x337C28FF, 0x337C28FF },
            { 0xAB342AFF, 0xDF3527FF, 0xDF3527FF, 0xDF3527FF },
            { 0x8D3496FF, 0xB435C0FF, 0xB435C0FF, 0xB435C0FF },
            { 0x8B4E68FF, 0xAF597DFF, 0xAF597DFF, 0xAF597DFF },
            { 0x7657A1FF, 0x9064CEFF, 0x9064CEFF, 0x9064CEFF },
            { 0x9E6B39FF, 0xC77E37FF, 0xC77E37FF, 0xC77E37FF },
            { 0xA35B9FFF, 0xCD66C6FF, 0xCD66C6FF, 0xCD66C6FF },
            { 0x4995AEFF, 0x4DB9DCFF, 0x4DB9DCFF, 0x4DB9DCFF },
            { 0x82A0BFFF, 0x98C2EEFF, 0x98C2EEFF, 0x98C2EEFF },
            { 0x6D134AFF, 0x8E0F5CFF, 0x8E0F5CFF, 0x8E0F5CFF },
            { 0x27952AFF, 0x23BF27FF, 0x23BF27FF, 0x23BF27FF },
            { 0x796386FF, 0x9474A7FF, 0x9474A7FF, 0x9474A7FF },
            { 0xA95EC4FF, 0xD368FAFF, 0xD368FAFF, 0xD368FAFF },
            { 0x468D6CFF, 0x4CB082FF, 0x4CB082FF, 0x4CB082FF },
            { 0xC985BBFF, 0xFA99E6FF, 0xFA99E6FF, 0xFA99E6FF },
            { 0x897269FF, 0xA8887BFF, 0xA8887BFF, 0xA8887BFF },
            { 0x36AD7DFF, 0x32DC97FF, 0x32DC97FF, 0x32DC97FF },
            { 0xC2B841FF, 0xEEE037FF, 0xEEE037FF, 0xEEE037FF },
            { 0x4568ABFF, 0x4C7FDEFF, 0x4C7FDEFF, 0x4C7FDEFF },
            { 0x9EA43BFF, 0xC0C934FF, 0xC0C934FF, 0xC0C934FF },
            { 0x2E8231FF, 0x2FA632FF, 0x2FA632FF, 0x2FA632FF },
            { 0x7F7A14FF, 0x9C9504FF, 0x9C9504FF, 0x9C9504FF },
            { 0xBF7D26FF, 0xF19318FF, 0xF19318FF, 0xF19318FF },
            { 0x90C92BFF, 0xAAFB1AFF, 0xAAFB1AFF, 0xAAFB1AFF },
            { 0xCDCF5AFF, 0xFAFC56FF, 0xFAFC56FF, 0xFAFC56FF },
            { 0x64ABA5FF, 0x6FD4CBFF, 0x6FD4CBFF, 0x6FD4CBFF },
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x0D0315FF, 0x0F0616FF, 0x13071DFF, 0x160E1CFF },
            { 0x1E112CFF, 0x251931FF, 0x2E1D3FFF, 0x392E44FF },
            { 0x1E2B14FF, 0x283420FF, 0x304024FF, 0x45503DFF },
            { 0x43063BFF, 0x491342FF, 0x5F1255FF, 0x64325EFF },
            { 0x310B79FF, 0x3B1A7AFF, 0x491AA3FF, 0x5A3B95FF },
            { 0x4B2528FF, 0x573638FF, 0x6D3E41FF, 0x7F6062FF },
            { 0x363E12FF, 0x454C25FF, 0x535D26FF, 0x707652FF },
            { 0x6B0600FF, 0x701711FF, 0x93150CFF, 0x8F3C37FF },
            { 0x2F4857FF, 0x435966FF, 0x4E6D80FF, 0x788D99FF },
            { 0x3C2F87FF, 0x4E4290FF, 0x5E4DBBFF, 0x8176BEFF },
            { 0x5D3544FF, 0x6E4B57FF, 0x895769FF, 0xA58490FF },
            { 0x4C22A7FF, 0x5E38ADFF, 0x733EE4FF, 0x9370DCFF },
            { 0x7B290EFF, 0x883F28FF, 0xAE4726FF, 0xBB7862FF },
            { 0x6C1964FF, 0x793172FF, 0x9A3390FF, 0xAB68A5FF },
            { 0x5E521DFF, 0x73683AFF, 0x8D7E3CFF, 0xB2A87DFF },
            { 0x3D5D6AFF, 0x56727EFF, 0x648C9DFF, 0x9AB4BFFF },
            { 0x9F3F4AFF, 0xB46069FF, 0xE46C7AFF, 0xFFB3BCFF },
            { 0x586D70FF, 0x76888BFF, 0x8BA5A9FF, 0xC8D9DBFF },
            { 0xB319A7FF, 0xC63FBAFF, 0xFD3DEDFF, 0xFF94FFFF },
            { 0x8C605CFF, 0xA7817EFF, 0xCF9894FF, 0xFFDCD9FF },
            { 0x616899FF, 0x8186B1FF, 0x99A1DEFF, 0xD8DDFFFF },
            { 0x558336FF, 0x749D59FF, 0x88C262FF, 0xC9EFB0FF },
            { 0x7D6928FF, 0x97864DFF, 0xBAA150FF, 0xE9D9A4FF },
            { 0x985781FF, 0xB47BA0FF, 0xDF8FC3FF, 0xFFDBFDFF },
            { 0x737D65FF, 0x949D88FF, 0xB2BEA1FF, 0xF3FBE8FF },
            { 0x9977A4FF, 0xBDA0C7FF, 0xE7BDF4FF, 0xFFFFFFFF },
            { 0xA47346FF, 0xC39871FF, 0xF2B57DFF, 0xFFFFDBFF },
            { 0x46A374FF, 0x6EC097FF, 0x7BEFB4FF, 0xD4FFF9FF },
            { 0x86827FFF, 0xAAA6A4FF, 0xCDC8C5FF, 0xFFFFFFFF },
            { 0x3FA217FF, 0x62BA3FFF, 0x6EEA3CFF, 0xBCFF9BFF },
            { 0x7D9BA7FF, 0xA8C3CCFF, 0xC6ECFAFF, 0xFFFFFFFF },
            { 0x609F97FF, 0x8AC1BAFF, 0x9FEDE3FF, 0xF8FFFFFF },
            { 0xA0976DFF, 0xC8C19CFF, 0xF3E8B4FF, 0xFFFFFFFF },
            { 0x78A05DFF, 0xA0C389FF, 0xBDEF9CFF, 0xFFFFF9FF },
            { 0xA39EA1FF, 0xCFCACDFF, 0xFAF3F7FF, 0xFFFFFFFF },
            { 0x12214AFF, 0x1D2A4EFF, 0x213366FF, 0x394466FF },
            { 0x33225DFF, 0x403165FF, 0x4E3982FF, 0x665988FF },
            { 0x1B5613FF, 0x2F6227FF, 0x337C28FF, 0x5D8D56FF },
            { 0xA1190EFF, 0xAB342AFF, 0xDF3527FF, 0xE2736AFF },
            { 0x7E1888FF, 0x8D3496FF, 0xB435C0FF, 0xC875D0FF },
            { 0x793451FF, 0x8B4E68FF, 0xAF597DFF, 0xCA92A9FF },
            { 0x603C91FF, 0x7657A1FF, 0x9064CEFF, 0xB99CE1FF },
            { 0x8A4F16FF, 0x9E6B39FF, 0xC77E37FF, 0xE7B789FF },
            { 0x8D3A88FF, 0xA35B9FFF, 0xCD66C6FF, 0xF1ADECFF },
            { 0x277E9AFF, 0x4995AEFF, 0x4DB9DCFF, 0x9AE1F8FF },
            { 0x5E80A3FF, 0x82A0BFFF, 0x98C2EEFF, 0xE4FFFFFF },
            { 0x67013FFF, 0x6D134AFF, 0x8E0F5CFF, 0x8F3C6EFF },
            { 0x0B880EFF, 0x27952AFF, 0x23BF27FF, 0x66CC69FF },
            { 0x624871FF, 0x796386FF, 0x9474A7FF, 0xBFAACBFF },
            { 0x913BB0FF, 0xA95EC4FF, 0xD368FAFF, 0xFBB5FFFF },
            { 0x297954FF, 0x468D6CFF, 0x4CB082FF, 0x8ED0B2FF },
            { 0xAA5C9AFF, 0xC985BBFF, 0xFA99E6FF, 0xFFEFFFFF },
            { 0x70574CFF, 0x897269FF, 0xA8887BFF, 0xD4BFB7FF },
            { 0x119A63FF, 0x36AD7DFF, 0x32DC97FF, 0x88F7CAFF },
            { 0x9F940CFF, 0xC2B841FF, 0xEEE037FF, 0xFFFFB6FF },
            { 0x2B53A0FF, 0x4568ABFF, 0x4C7FDEFF, 0x85A7E5FF },
            { 0x7F860EFF, 0x9EA43BFF, 0xC0C934FF, 0xFBFF9FFF },
            { 0x157518FF, 0x2E8231FF, 0x2FA632FF, 0x69B76BFF },
            { 0x696300FF, 0x7F7A14FF, 0x9C9504FF, 0xC3BF60FF },
            { 0xA85C00FF, 0xBF7D26FF, 0xF19318FF, 0xFFD584FF },
            { 0x6BAC00FF, 0x90C92BFF, 0xAAFB1AFF, 0xF6FF98FF },
            { 0xA6A722FF, 0xCDCF5AFF, 0xFAFC56FF, 0xFFFFDAFF },
            { 0x3F9088FF, 0x64ABA5FF, 0x6FD4CBFF, 0xC2FFFEFF },
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x13071DFF, 0x0F0616FF, 0x13071DFF, 0x160E1CFF },
            { 0x2E1D3FFF, 0x251931FF, 0x2E1D3FFF, 0x392E44FF },
            { 0x304024FF, 0x283420FF, 0x304024FF, 0x45503DFF },
            { 0x5F1255FF, 0x491342FF, 0x5F1255FF, 0x64325EFF },
            { 0x491AA3FF, 0x3B1A7AFF, 0x491AA3FF, 0x5A3B95FF },
            { 0x6D3E41FF, 0x573638FF, 0x6D3E41FF, 0x7F6062FF },
            { 0x535D26FF, 0x454C25FF, 0x535D26FF, 0x707652FF },
            { 0x93150CFF, 0x701711FF, 0x93150CFF, 0x8F3C37FF },
            { 0x4E6D80FF, 0x435966FF, 0x4E6D80FF, 0x788D99FF },
            { 0x5E4DBBFF, 0x4E4290FF, 0x5E4DBBFF, 0x8176BEFF },
            { 0x895769FF, 0x6E4B57FF, 0x895769FF, 0xA58490FF },
            { 0x733EE4FF, 0x5E38ADFF, 0x733EE4FF, 0x9370DCFF },
            { 0xAE4726FF, 0x883F28FF, 0xAE4726FF, 0xBB7862FF },
            { 0x9A3390FF, 0x793172FF, 0x9A3390FF, 0xAB68A5FF },
            { 0x8D7E3CFF, 0x73683AFF, 0x8D7E3CFF, 0xB2A87DFF },
            { 0x648C9DFF, 0x56727EFF, 0x648C9DFF, 0x9AB4BFFF },
            { 0xE46C7AFF, 0xB46069FF, 0xE46C7AFF, 0xFFB3BCFF },
            { 0x8BA5A9FF, 0x76888BFF, 0x8BA5A9FF, 0xC8D9DBFF },
            { 0xFD3DEDFF, 0xC63FBAFF, 0xFD3DEDFF, 0xFF94FFFF },
            { 0xCF9894FF, 0xA7817EFF, 0xCF9894FF, 0xFFDCD9FF },
            { 0x99A1DEFF, 0x8186B1FF, 0x99A1DEFF, 0xD8DDFFFF },
            { 0x88C262FF, 0x749D59FF, 0x88C262FF, 0xC9EFB0FF },
            { 0xBAA150FF, 0x97864DFF, 0xBAA150FF, 0xE9D9A4FF },
            { 0xDF8FC3FF, 0xB47BA0FF, 0xDF8FC3FF, 0xFFDBFDFF },
            { 0xB2BEA1FF, 0x949D88FF, 0xB2BEA1FF, 0xF3FBE8FF },
            { 0xE7BDF4FF, 0xBDA0C7FF, 0xE7BDF4FF, 0xFFFFFFFF },
            { 0xF2B57DFF, 0xC39871FF, 0xF2B57DFF, 0xFFFFDBFF },
            { 0x7BEFB4FF, 0x6EC097FF, 0x7BEFB4FF, 0xD4FFF9FF },
            { 0xCDC8C5FF, 0xAAA6A4FF, 0xCDC8C5FF, 0xFFFFFFFF },
            { 0x6EEA3CFF, 0x62BA3FFF, 0x6EEA3CFF, 0xBCFF9BFF },
            { 0xC6ECFAFF, 0xA8C3CCFF, 0xC6ECFAFF, 0xFFFFFFFF },
            { 0x9FEDE3FF, 0x8AC1BAFF, 0x9FEDE3FF, 0xF8FFFFFF },
            { 0xF3E8B4FF, 0xC8C19CFF, 0xF3E8B4FF, 0xFFFFFFFF },
            { 0xBDEF9CFF, 0xA0C389FF, 0xBDEF9CFF, 0xFFFFF9FF },
            { 0xFAF3F7FF, 0xCFCACDFF, 0xFAF3F7FF, 0xFFFFFFFF },
            { 0x213366FF, 0x1D2A4EFF, 0x213366FF, 0x394466FF },
            { 0x4E3982FF, 0x403165FF, 0x4E3982FF, 0x665988FF },
            { 0x337C28FF, 0x2F6227FF, 0x337C28FF, 0x5D8D56FF },
            { 0xDF3527FF, 0xAB342AFF, 0xDF3527FF, 0xE2736AFF },
            { 0xB435C0FF, 0x8D3496FF, 0xB435C0FF, 0xC875D0FF },
            { 0xAF597DFF, 0x8B4E68FF, 0xAF597DFF, 0xCA92A9FF },
            { 0x9064CEFF, 0x7657A1FF, 0x9064CEFF, 0xB99CE1FF },
            { 0xC77E37FF, 0x9E6B39FF, 0xC77E37FF, 0xE7B789FF },
            { 0xCD66C6FF, 0xA35B9FFF, 0xCD66C6FF, 0xF1ADECFF },
            { 0x4DB9DCFF, 0x4995AEFF, 0x4DB9DCFF, 0x9AE1F8FF },
            { 0x98C2EEFF, 0x82A0BFFF, 0x98C2EEFF, 0xE4FFFFFF },
            { 0x8E0F5CFF, 0x6D134AFF, 0x8E0F5CFF, 0x8F3C6EFF },
            { 0x23BF27FF, 0x27952AFF, 0x23BF27FF, 0x66CC69FF },
            { 0x9474A7FF, 0x796386FF, 0x9474A7FF, 0xBFAACBFF },
            { 0xD368FAFF, 0xA95EC4FF, 0xD368FAFF, 0xFBB5FFFF },
            { 0x4CB082FF, 0x468D6CFF, 0x4CB082FF, 0x8ED0B2FF },
            { 0xFA99E6FF, 0xC985BBFF, 0xFA99E6FF, 0xFFEFFFFF },
            { 0xA8887BFF, 0x897269FF, 0xA8887BFF, 0xD4BFB7FF },
            { 0x32DC97FF, 0x36AD7DFF, 0x32DC97FF, 0x88F7CAFF },
            { 0xEEE037FF, 0xC2B841FF, 0xEEE037FF, 0xFFFFB6FF },
            { 0x4C7FDEFF, 0x4568ABFF, 0x4C7FDEFF, 0x85A7E5FF },
            { 0xC0C934FF, 0x9EA43BFF, 0xC0C934FF, 0xFBFF9FFF },
            { 0x2FA632FF, 0x2E8231FF, 0x2FA632FF, 0x69B76BFF },
            { 0x9C9504FF, 0x7F7A14FF, 0x9C9504FF, 0xC3BF60FF },
            { 0xF19318FF, 0xBF7D26FF, 0xF19318FF, 0xFFD584FF },
            { 0xAAFB1AFF, 0x90C92BFF, 0xAAFB1AFF, 0xF6FF98FF },
            { 0xFAFC56FF, 0xCDCF5AFF, 0xFAFC56FF, 0xFFFFDAFF },
            { 0x6FD4CBFF, 0x64ABA5FF, 0x6FD4CBFF, 0xC2FFFEFF },
    };

    public static final byte[][] SMASH_RAMPS = new byte[][]{
            { 0, 0, 0, 0 },
            { 70, -3, 1, 59 },
            { -13, -10, 2, -81 },
            { -102, -9, 3, 11 },
            { 36, 39, 4, 121 },
            { 1, -88, 5, -59 },
            { 10, -49, 6, -49 },
            { -47, 109, 7, 10 },
            { -117, -12, 8, -32 },
            { -89, 55, 9, -61 },
            { -47, 3, 10, -66 },
            { -88, -102, 11, -97 },
            { -55, 67, 12, -70 },
            { -37, -80, 13, 36 },
            { 2, -90, 14, -90 },
            { -41, -32, 15, -82 },
            { 22, -19, 16, 38 },
            { 86, 121, 17, -30 },
            { 126, -108, 18, -121 },
            { -66, -49, 19, 50 },
            { -3, 59, 20, -18 },
            { -51, -49, 21, -50 },
            { 5, -93, 22, -117 },
            { -19, 89, 23, -116 },
            { 22, -19, 24, 38 },
            { -50, -17, 25, -17 },
            { 2, 11, 26, -26 },
            { -65, -116, 27, -4 },
            { 95, 38, 28, 38 },
            { 27, -4, 29, 9 },
            { 44, -52, 30, -111 },
            { 1, 1, 31, 127 },
            { -47, 122, 32, 122 },
            { 84, 48, 33, -85 },
            { 102, 3, 34, 3 },
            { -85, 85, 35, -48 },
            { 8, 89, 36, -52 },
            { 77, -82, 37, 91 },
            { 72, 126, 38, -32 },
            { -52, 70, 39, -56 },
            { -66, 50, 40, 50 },
            { 112, -17, 41, 105 },
            { 54, -37, 42, -80 },
            { 87, 17, 43, 81 },
            { 23, 36, 44, 27 },
            { 27, -55, 45, 67 },
            { -128, -12, 46, 8 },
            { -124, -116, 47, -111 },
            { 106, -121, 48, 77 },
            { 44, -111, 49, 45 },
            { 83, -50, 50, -17 },
            { 62, -83, 51, -66 },
            { -3, 59, 52, 17 },
            { -52, -3, 53, 59 },
            { 22, 82, 54, 8 },
            { 1, -13, 55, -93 },
            { -2, 126, 56, -108 },
            { -49, 15, 57, -17 },
            { 48, -20, 58, -20 },
            { 39, 86, 59, 81 },
            { 80, 8, 60, -116 },
            { -61, 82, 61, 8 },
            { -102, 102, 62, 3 },
            { -55, 67, 63, 91 },
            { -120, -97, 64, 126 },
            { -19, 42, 65, 13 },
            { -49, -50, 66, -57 },
            { -14, 48, 67, -103 },
            { 1, -29, 68, 122 },
            { -92, -121, 69, 27 },
            { -80, 36, 70, 27 },
            { 83, 79, 71, 79 },
            { 127, 11, 72, -54 },
            { 49, 121, 73, -126 },
            { -85, -20, 74, 12 },
            { 42, 13, 75, 70 },
            { 60, 115, 76, 115 },
            { 15, 84, 77, 48 },
            { 10, -36, 78, 21 },
            { -53, 15, 79, 84 },
            { 5, -59, 80, -80 },
            { -18, 29, 81, -103 },
            { 5, -117, 82, 72 },
            { -66, -53, 83, 15 },
            { -41, -50, 84, 77 },
            { -17, -82, 85, 105 },
            { -80, -116, 86, -18 },
            { -52, -3, 87, -3 },
            { -41, 79, 88, 101 },
            { -12, 61, 89, -32 },
            { -125, -61, 90, 96 },
            { -85, 37, 91, -48 },
            { 12, 29, 92, -48 },
            { 69, 48, 93, -20 },
            { 8, 89, 94, 89 },
            { 9, -117, 95, 61 },
            { -128, -12, 96, -37 },
            { 84, 48, 97, 48 },
            { 34, -58, 98, 10 },
            { -35, 114, 99, 114 },
            { 113, 111, 100, -41 },
            { -54, 84, 101, 84 },
            { 1, 1, 102, 122 },
            { -119, -110, 103, 117 },
            { -98, 5, 104, -59 },
            { -17, -85, 105, -95 },
            { -90, -51, 106, 48 },
            { 117, -15, 107, -15 },
            { 122, 10, 108, -112 },
            { 102, -47, 109, -49 },
            { 38, -65, 110, 69 },
            { 2, -90, 111, -90 },
            { 66, -17, 112, -85 },
            { 62, 11, 113, -90 },
            { 82, 80, 114, 89 },
            { 89, -116, 115, -116 },
            { 62, -24, 116, -53 },
            { 121, -18, 117, -18 },
            { 89, -116, 118, -116 },
            { -1, 101, 119, 97 },
            { -71, 97, 120, 58 },
            { -52, -111, 121, -20 },
            { 102, -9, 122, 11 },
            { 79, 101, 123, -57 },
            { -9, 11, 124, 11 },
            { 1, 1, 125, -12 },
            { -59, 64, 126, -54 },
            { 1, -88, 127, -90 },
            { 31, -125, -128, -37 },
            { 1, -47, -127, 34 },
            { -111, 45, -126, 63 },
            { 1, 1, -125, -93 },
            { 114, -92, -124, 118 },
            { 55, -61, -123, 82 },
            { 115, 87, -122, -110 },
            { -54, -65, -121, 48 },
            { -88, -104, -120, -90 },
            { -116, -52, -119, -3 },
            { -76, 114, -118, -21 },
            { 55, -61, -117, 82 },
            { 80, -37, -116, -14 },
            { -121, 97, -115, 97 },
            { 84, -85, -114, -85 },
            { -89, -26, -113, 64 },
            { 62, -83, -112, -66 },
            { 36, 27, -111, 121 },
            { 70, 86, -110, -18 },
            { -89, 2, -109, -26 },
            { 64, -54, -108, -65 },
            { -117, 61, -107, 114 },
            { 64, -65, -106, -65 },
            { 8, 89, -105, -116 },
            { 1, 1, -104, -120 },
            { -4, 67, -103, 9 },
            { 1, 1, -102, 122 },
            { -47, 3, -101, 113 },
            { -111, -20, -100, 29 },
            { 14, -51, -99, 79 },
            { 1, -125, -98, 55 },
            { 2, 11, -97, 64 },
            { 55, -89, -96, 82 },
            { -82, 105, -95, 92 },
            { 23, -63, -94, 39 },
            { 1, -125, -93, -59 },
            { 64, -54, -92, -65 },
            { -38, 115, -91, 115 },
            { -81, -120, -90, 72 },
            { -13, 5, -89, 5 },
            { 1, 1, -88, 127 },
            { 87, -56, -87, -15 },
            { -99, 84, -86, 50 },
            { 106, -14, -85, 105 },
            { 42, 13, -84, 70 },
            { 127, -120, -83, -51 },
            { -14, 48, -82, 37 },
            { -104, 11, -81, 11 },
            { 80, -32, -80, -52 },
            { -39, -49, -79, -50 },
            { -121, 97, -78, 97 },
            { -78, -85, -77, 37 },
            { 22, 82, -76, 38 },
            { 83, -49, -75, -50 },
            { 64, -68, -74, -65 },
            { 79, 101, -73, 33 },
            { 104, 22, -72, 82 },
            { 56, -65, -71, 48 },
            { -4, 12, -70, 29 },
            { 110, -121, -69, 93 },
            { 26, -97, -68, 126 },
            { 90, 61, -67, -37 },
            { 3, -101, -66, -49 },
            { 126, -32, -65, -92 },
            { 50, 84, -64, -85 },
            { 89, -116, -63, -52 },
            { -116, -52, -62, 70 },
            { 31, 5, -61, -59 },
            { -67, 42, -60, 13 },
            { -88, 31, -59, 80 },
            { -47, 3, -58, 113 },
            { 50, 15, -57, 48 },
            { 39, 86, -56, 121 },
            { 48, -4, -55, -4 },
            { -120, -97, -54, -14 },
            { -120, -97, -53, -17 },
            { -80, 36, -52, 27 },
            { -83, -90, -51, 83 },
            { -53, -54, -50, 84 },
            { -66, -53, -49, -53 },
            { -70, 91, -48, 92 },
            { 1, -102, -47, -29 },
            { -42, 115, -46, 39 },
            { -18, -126, -45, -30 },
            { 53, 117, -44, 117 },
            { -28, 70, -43, -56 },
            { -37, 8, -42, -80 },
            { 11, -90, -41, 15 },
            { 83, 79, -40, 101 },
            { 34, 113, -39, -49 },
            { 96, -37, -38, -80 },
            { -128, -12, -37, -116 },
            { 113, -51, -36, -51 },
            { 22, 82, -35, 8 },
            { -51, -41, -34, 79 },
            { -119, 39, -33, -110 },
            { 11, -59, -32, -65 },
            { 82, 8, -31, 89 },
            { 121, -18, -30, 9 },
            { 1, 1, -29, 113 },
            { -67, -80, -28, -52 },
            { 8, 89, -27, -116 },
            { -81, -120, -26, -97 },
            { -47, 10, -25, 10 },
            { 124, -81, -24, -97 },
            { -61, 82, -23, 82 },
            { -42, 60, -22, 36 },
            { 89, -65, -21, -116 },
            { 27, -82, -20, 67 },
            { -96, -117, -19, 61 },
            { -52, 86, -18, 81 },
            { -66, -53, -17, -85 },
            { -102, 102, -16, -29 },
            { -56, -18, -15, 81 },
            { -53, -32, -14, -82 },
            { 1, -88, -13, 11 },
            { -125, 125, -12, 8 },
            { -7, 105, -11, 91 },
            { -88, -104, -10, 11 },
            { 1, 1, -9, -83 },
            { -85, -82, -8, 37 },
            { -85, 37, -7, 37 },
            { -49, 83, -6, 79 },
            { 48, -85, -5, 85 },
            { -14, 77, -4, 29 },
            { 36, 115, -3, -18 },
            { -26, 64, -2, -68 },
            { -24, 64, -1, -54 },
    };
    
    public static final int[][] SMASH_RAMP_VALUES = new int[][]{
            { 0x00000000, 0x00000000, 0x00000000, 0x00000000 },
            { 0x85bf60ff, 0xa8bd77ff, 0xe3b72eff, 0xbbcf7dff },
            { 0x34081aff, 0x4e0536ff, 0x054d41ff, 0x58174cff },
            { 0x0c0635ff, 0x131a38ff, 0x3f136bff, 0x1e3d45ff },
            { 0xaf7a67ff, 0xd39270ff, 0x80e856ff, 0xd5ac9bff },
            { 0xe3b72eff, 0x000612ff, 0x391e19ff, 0x4a4140ff },
            { 0x2c3ba1ff, 0x6f4faeff, 0x3b5cfbff, 0x6f4faeff },
            { 0x12077bff, 0x1c3884ff, 0x233bd4ff, 0x2c3ba1ff },
            { 0x6b3020ff, 0x4e5431ff, 0x627846ff, 0x806176ff },
            { 0x025806ff, 0x093c06ff, 0x810208ff, 0x244b07ff },
            { 0x12077bff, 0x3f136bff, 0x2c3ba1ff, 0x3c5889ff },
            { 0x000612ff, 0x0c0635ff, 0x1e3d45ff, 0x643654ff },
            { 0xda8bb8ff, 0xd4bac2ff, 0x9de5caff, 0xf9a5ddff },
            { 0x726941ff, 0x878260ff, 0x71a928ff, 0xaf7a67ff },
            { 0x054d41ff, 0x2a606bff, 0x980f99ff, 0x2a606bff },
            { 0x77517eff, 0x806176ff, 0x60879bff, 0xb29fc1ff },
            { 0x107a05ff, 0x209415ff, 0xe30c1bff, 0x2ba050ff },
            { 0xaaae89ff, 0xd5ac9bff, 0xcad985ff, 0xffffd1ff },
            { 0x883e6eff, 0xab4380ff, 0x08c881ff, 0xbb5e9aff },
            { 0x3c5889ff, 0x6f4faeff, 0x9242efff, 0x499ec9ff },
            { 0xa8bd77ff, 0xbbcf7dff, 0xfdb56dff, 0xcfcfaaff },
            { 0x654786ff, 0x6f4faeff, 0x3979d4ff, 0x8664aeff },
            { 0x391e19ff, 0x49320bff, 0x107a05ff, 0x6b3020ff },
            { 0x209415ff, 0x61884fff, 0xc56045ff, 0x839c6bff },
            { 0x107a05ff, 0x209415ff, 0xfd0c0cff, 0x2ba050ff },
            { 0x8664aeff, 0x8e7fb9ff, 0x7894f2ff, 0x8e7fb9ff },
            { 0x054d41ff, 0x1e3d45ff, 0x72154eff, 0x0a7740ff },
            { 0x4f9c76ff, 0x839c6bff, 0xbd8d96ff, 0xa2c1bbff },
            { 0x01a601ff, 0x2ba050ff, 0xf7144dff, 0x2ba050ff },
            { 0xbd8d96ff, 0xa2c1bbff, 0xd3dbceff, 0x810208ff },
            { 0x75cd50ff, 0x8bad68ff, 0xf97d52ff, 0x8aca81ff },
            { 0xe3b72eff, 0xe3b72eff, 0x1a140eff, 0x2f2a3eff },
            { 0x12077bff, 0x1e2b76ff, 0x0c2cc5ff, 0x1e2b76ff },
            { 0x5a959fff, 0x79a7a7ff, 0xf750cdff, 0x7ab1bcff },
            { 0x170c4aff, 0x3f136bff, 0x082e9dff, 0x3f136bff },
            { 0x7ab1bcff, 0x8bbaecff, 0xcdbeffff, 0xb9fafaff },
            { 0x627846ff, 0x61884fff, 0xaf7a67ff, 0x8bad68ff },
            { 0xbc72a8ff, 0xb29fc1ff, 0x9bd5e1ff, 0xfcbbf9ff },
            { 0x644d59ff, 0x883e6eff, 0x2ba050ff, 0x806176ff },
            { 0x8bad68ff, 0x85bf60ff, 0xd39270ff, 0xa4db68ff },
            { 0x3c5889ff, 0x499ec9ff, 0xb833f7ff, 0x499ec9ff },
            { 0x9b7afdff, 0x8e7fb9ff, 0x86aaf8ff, 0xc2a9ddff },
            { 0x96540cff, 0x726941ff, 0x689e1aff, 0x878260ff },
            { 0xb3c03fff, 0xcad985ff, 0xf7e95fff, 0xe5eda8ff },
            { 0xc56045ff, 0xaf7a67ff, 0x75cd50ff, 0xbd8d96ff },
            { 0xbd8d96ff, 0xda8bb8ff, 0x8de89bff, 0xd4bac2ff },
            { 0x404213ff, 0x4e5431ff, 0x745909ff, 0x627846ff },
            { 0x34df4dff, 0x839c6bff, 0xfc5f68ff, 0x8aca81ff },
            { 0x78779cff, 0xbb5e9aff, 0x79a7a7ff, 0xbc72a8ff },
            { 0x75cd50ff, 0x8aca81ff, 0xff907fff, 0x8de89bff },
            { 0x8a3fc4ff, 0x8664aeff, 0x499ec9ff, 0x8e7fb9ff },
            { 0x00406bff, 0x245674ff, 0x8c03d4ff, 0x3c5889ff },
            { 0xa8bd77ff, 0xbbcf7dff, 0xe8cc1fff, 0xcad985ff },
            { 0x8bad68ff, 0xa8bd77ff, 0xeba55fff, 0xbbcf7dff },
            { 0x107a05ff, 0x386330ff, 0x96540cff, 0x627846ff },
            { 0xe3b72eff, 0x34081aff, 0x093c06ff, 0x49320bff },
            { 0xbd0b81ff, 0x883e6eff, 0x09b583ff, 0xab4380ff },
            { 0x6f4faeff, 0x60879bff, 0x935af8ff, 0x8e7fb9ff },
            { 0x79a7a7ff, 0x91d1b0ff, 0xf870c9ff, 0x91d1b0ff },
            { 0xd39270ff, 0xaaae89ff, 0xbbcf7dff, 0xe5eda8ff },
            { 0x665e50ff, 0x627846ff, 0x9e6644ff, 0x839c6bff },
            { 0x244b07ff, 0x386330ff, 0x814b33ff, 0x627846ff },
            { 0x0c0635ff, 0x170c4aff, 0x00406bff, 0x3f136bff },
            { 0xda8bb8ff, 0xd4bac2ff, 0xa9fbc5ff, 0xfcbbf9ff },
            { 0x4d2d45ff, 0x643654ff, 0x188257ff, 0x883e6eff },
            { 0x209415ff, 0x689e1aff, 0xfd4a08ff, 0x71a928ff },
            { 0x6f4faeff, 0x8664aeff, 0x5981f9ff, 0xa06cd3ff },
            { 0x908c97ff, 0x79a7a7ff, 0xd4bac2ff, 0xf2ece7ff },
            { 0xe3b72eff, 0x092359ff, 0x2a05beff, 0x1e2b76ff },
            { 0xa2607dff, 0xbb5e9aff, 0x48da89ff, 0xbd8d96ff },
            { 0x878260ff, 0xaf7a67ff, 0x85bf60ff, 0xbd8d96ff },
            { 0x8a3fc4ff, 0x9f50bcff, 0x0eb0f7ff, 0x9f50bcff },
            { 0x2f2a3eff, 0x1e3d45ff, 0x644d59ff, 0x527b78ff },
            { 0xff907fff, 0xd5ac9bff, 0x94fe8aff, 0xf3c1b3ff },
            { 0x7ab1bcff, 0x91d1b0ff, 0xfc89cfff, 0x9de5caff },
            { 0x689e1aff, 0x71a928ff, 0xf16b14ff, 0x85bf60ff },
            { 0x9e6644ff, 0xab8950ff, 0xa0a10cff, 0xab8950ff },
            { 0x60879bff, 0x5a959fff, 0xbc72a8ff, 0x79a7a7ff },
            { 0x2c3ba1ff, 0x2073b5ff, 0x9b17ffff, 0x3979d4ff },
            { 0x516d7eff, 0x60879bff, 0x9f50bcff, 0x5a959fff },
            { 0x391e19ff, 0x4a4140ff, 0x665e50ff, 0x878260ff },
            { 0xcfcfaaff, 0xd3dbceff, 0xe5eda8ff, 0xf2ece7ff },
            { 0x391e19ff, 0x6b3020ff, 0x386330ff, 0x644d59ff },
            { 0x3c5889ff, 0x516d7eff, 0x8a3fc4ff, 0x60879bff },
            { 0x77517eff, 0x8664aeff, 0x5a959fff, 0xbc72a8ff },
            { 0x8e7fb9ff, 0xb29fc1ff, 0x8bbaecff, 0xc2a9ddff },
            { 0x878260ff, 0x839c6bff, 0xaaae89ff, 0xcfcfaaff },
            { 0x8bad68ff, 0xa8bd77ff, 0xb3c03fff, 0xa8bd77ff },
            { 0x77517eff, 0x9f50bcff, 0x06c6baff, 0xc542c3ff },
            { 0x4e5431ff, 0x814b33ff, 0x61884fff, 0x806176ff },
            { 0x0f2b00ff, 0x244b07ff, 0x61410dff, 0x5a6f05ff },
            { 0x7ab1bcff, 0x9bd5e1ff, 0xfcbbf9ff, 0xb9fafaff },
            { 0x9de5caff, 0xd3dbceff, 0xfcd7ffff, 0xb9fafaff },
            { 0x48da89ff, 0x79a7a7ff, 0xeb75a4ff, 0x91d1b0ff },
            { 0x627846ff, 0x61884fff, 0xc54749ff, 0x61884fff },
            { 0x810208ff, 0x6b3020ff, 0x01a601ff, 0x814b33ff },
            { 0x404213ff, 0x4e5431ff, 0x5a6f05ff, 0x726941ff },
            { 0x5a959fff, 0x79a7a7ff, 0xd25ab6ff, 0x79a7a7ff },
            { 0x082e9dff, 0x054b95ff, 0x420cffff, 0x2c3ba1ff },
            { 0x9d332aff, 0xa34a4aff, 0x10cf0eff, 0xa34a4aff },
            { 0x681c85ff, 0x891878ff, 0x048d93ff, 0x77517eff },
            { 0x527b78ff, 0x5a959fff, 0xc542c3ff, 0x5a959fff },
            { 0xe3b72eff, 0xe3b72eff, 0x170c4aff, 0x1e2b76ff },
            { 0xc1982dff, 0xd1ba7bff, 0xbfea12ff, 0xe8ce7eff },
            { 0x5a0705ff, 0x391e19ff, 0x066c03ff, 0x4a4140ff },
            { 0x8e7fb9ff, 0x7ab1bcff, 0xc2a9ddff, 0xc7d1faff },
            { 0x2a606bff, 0x654786ff, 0x78779cff, 0x79a7a7ff },
            { 0xe8ce7eff, 0xfbdd7dff, 0xfff923ff, 0xfbdd7dff },
            { 0x1e2b76ff, 0x2c3ba1ff, 0x0955e1ff, 0x7614aeff },
            { 0x170c4aff, 0x12077bff, 0x1c3884ff, 0x6f4faeff },
            { 0x2ba050ff, 0x4f9c76ff, 0xfb3e86ff, 0x48da89ff },
            { 0x054d41ff, 0x2a606bff, 0x891878ff, 0x2a606bff },
            { 0x5981f9ff, 0x8e7fb9ff, 0x9b7afdff, 0x7ab1bcff },
            { 0x00406bff, 0x1e3d45ff, 0x681c85ff, 0x2a606bff },
            { 0x386330ff, 0x665e50ff, 0xa34a4aff, 0x61884fff },
            { 0x61884fff, 0x839c6bff, 0xab8950ff, 0x839c6bff },
            { 0x00406bff, 0x097472ff, 0xb50cc6ff, 0x516d7eff },
            { 0xd5ac9bff, 0xcfcfaaff, 0xe8ce7eff, 0xcfcfaaff },
            { 0x61884fff, 0x839c6bff, 0xcf6971ff, 0x839c6bff },
            { 0xd3019dff, 0xc542c3ff, 0x06d3bfff, 0xd25ab6ff },
            { 0xef34b5ff, 0xd25ab6ff, 0x21fddaff, 0xf870c9ff },
            { 0x8bad68ff, 0x8aca81ff, 0xd5ac9bff, 0x91d1b0ff },
            { 0x170c4aff, 0x131a38ff, 0x1e2b76ff, 0x1e3d45ff },
            { 0x9f50bcff, 0xc542c3ff, 0x00ddf8ff, 0xa06cd3ff },
            { 0x131a38ff, 0x1e3d45ff, 0x5b0576ff, 0x1e3d45ff },
            { 0xe3b72eff, 0xe3b72eff, 0x31290dff, 0x4e5431ff },
            { 0x4a4140ff, 0x188257ff, 0x883e6eff, 0x527b78ff },
            { 0xe3b72eff, 0x000612ff, 0x2f2a3eff, 0x2a606bff },
            { 0x1a140eff, 0x0f2b00ff, 0x404213ff, 0x726941ff },
            { 0xe3b72eff, 0x12077bff, 0x1204e4ff, 0x082e9dff },
            { 0x8aca81ff, 0x8de89bff, 0xf3c1b3ff, 0xa9fbc5ff },
            { 0xe3b72eff, 0xe3b72eff, 0x0f2b00ff, 0x49320bff },
            { 0xa34a4aff, 0xa2607dff, 0x34df4dff, 0xcf6971ff },
            { 0x093c06ff, 0x244b07ff, 0x672501ff, 0x386330ff },
            { 0xab8950ff, 0xb3c03fff, 0xb4d002ff, 0xd1ba7bff },
            { 0x527b78ff, 0x4f9c76ff, 0xbb5e9aff, 0x79a7a7ff },
            { 0x000612ff, 0x082126ff, 0x4d2d45ff, 0x2a606bff },
            { 0x839c6bff, 0x8bad68ff, 0xc1982dff, 0xa8bd77ff },
            { 0xc21b43ff, 0xa34a4aff, 0x07ed15ff, 0xe64c63ff },
            { 0x093c06ff, 0x244b07ff, 0x6b3020ff, 0x386330ff },
            { 0x665e50ff, 0x726941ff, 0x839c6bff, 0x908c97ff },
            { 0xbb5e9aff, 0xd25ab6ff, 0x0af7abff, 0xd25ab6ff },
            { 0x5a959fff, 0x7ab1bcff, 0xf93bffff, 0x7ab1bcff },
            { 0x025806ff, 0x0a7740ff, 0xaa104fff, 0x188257ff },
            { 0x00406bff, 0x245674ff, 0x7614aeff, 0x3c5889ff },
            { 0xaf7a67ff, 0xbd8d96ff, 0x8aca81ff, 0xd5ac9bff },
            { 0x85bf60ff, 0xaaae89ff, 0xd1ba7bff, 0xcfcfaaff },
            { 0x025806ff, 0x054d41ff, 0x960542ff, 0x0a7740ff },
            { 0x188257ff, 0x527b78ff, 0xab4380ff, 0x4f9c76ff },
            { 0x6b3020ff, 0x814b33ff, 0x17b103ff, 0xa34a4aff },
            { 0x188257ff, 0x4f9c76ff, 0xcc387dff, 0x4f9c76ff },
            { 0x627846ff, 0x61884fff, 0xb5740bff, 0x839c6bff },
            { 0xe3b72eff, 0xe3b72eff, 0x082126ff, 0x4d2d45ff },
            { 0xa2c1bbff, 0xd4bac2ff, 0xf2ece7ff, 0x810208ff },
            { 0xe3b72eff, 0xe3b72eff, 0x0c0635ff, 0x1e2b76ff },
            { 0x12077bff, 0x3f136bff, 0x1754b0ff, 0x681c85ff },
            { 0x8aca81ff, 0x91d1b0ff, 0xfbaba9ff, 0xd3dbceff },
            { 0x980f99ff, 0x654786ff, 0x03a6afff, 0x9f50bcff },
            { 0xe3b72eff, 0x0f2b00ff, 0x5a0705ff, 0x093c06ff },
            { 0x054d41ff, 0x1e3d45ff, 0x643654ff, 0x188257ff },
            { 0x093c06ff, 0x025806ff, 0x9c0612ff, 0x386330ff },
            { 0xb29fc1ff, 0xc2a9ddff, 0xc7d1faff, 0xfcd7ffff },
            { 0xc56045ff, 0xd57949ff, 0x4efd0eff, 0xd39270ff },
            { 0xe3b72eff, 0x0f2b00ff, 0x49320bff, 0x4a4140ff },
            { 0x188257ff, 0x527b78ff, 0xa2607dff, 0x4f9c76ff },
            { 0x858700ff, 0xab8950ff, 0xacb101ff, 0xab8950ff },
            { 0x58174cff, 0x4d2d45ff, 0x2a606bff, 0x644d59ff },
            { 0x34081aff, 0x391e19ff, 0x025806ff, 0x391e19ff },
            { 0xe3b72eff, 0xe3b72eff, 0x000612ff, 0x2f2a3eff },
            { 0xb3c03fff, 0xa4db68ff, 0xffd800ff, 0xfbdd7dff },
            { 0x03a6afff, 0x5a959fff, 0xe226f0ff, 0x499ec9ff },
            { 0x78779cff, 0x908c97ff, 0x7ab1bcff, 0xc2a9ddff },
            { 0x689e1aff, 0x71a928ff, 0xc48202ff, 0x85bf60ff },
            { 0x2f2a3eff, 0x4d2d45ff, 0x245674ff, 0x654786ff },
            { 0x908c97ff, 0x79a7a7ff, 0xb29fc1ff, 0x9bd5e1ff },
            { 0x082126ff, 0x1e3d45ff, 0x58174cff, 0x1e3d45ff },
            { 0x665e50ff, 0x806176ff, 0x878260ff, 0x8bad68ff },
            { 0x2561cdff, 0x6f4faeff, 0x4e6eedff, 0x8664aeff },
            { 0xbb5e9aff, 0xd25ab6ff, 0x23e3c7ff, 0xd25ab6ff },
            { 0x23e3c7ff, 0x7ab1bcff, 0xff66fdff, 0x9bd5e1ff },
            { 0x107a05ff, 0x386330ff, 0xc21b43ff, 0x2ba050ff },
            { 0x8a3fc4ff, 0x6f4faeff, 0x3b8bdeff, 0x8664aeff },
            { 0x188257ff, 0x10915eff, 0xed0778ff, 0x4f9c76ff },
            { 0x9f50bcff, 0xc542c3ff, 0x07eff6ff, 0xf750cdff },
            { 0x066c03ff, 0x107a05ff, 0xbe0f02ff, 0x386330ff },
            { 0x09b583ff, 0x4f9c76ff, 0xef34b5ff, 0x79a7a7ff },
            { 0xa2c1bbff, 0x9de5caff, 0xf9a5ddff, 0xd3dbceff },
            { 0xfb3e86ff, 0xbb5e9aff, 0x2dfa78ff, 0xeb75a4ff },
            { 0x72154eff, 0x643654ff, 0x10915eff, 0x883e6eff },
            { 0x61410dff, 0x814b33ff, 0x548204ff, 0x726941ff },
            { 0x3f136bff, 0x1754b0ff, 0x3c5889ff, 0x6f4faeff },
            { 0x883e6eff, 0x806176ff, 0x4f9c76ff, 0xa2607dff },
            { 0x499ec9ff, 0x5a959fff, 0xc05ef8ff, 0x7ab1bcff },
            { 0x61884fff, 0x839c6bff, 0xd57949ff, 0x8bad68ff },
            { 0x839c6bff, 0x8bad68ff, 0xd88830ff, 0x85bf60ff },
            { 0x1a140eff, 0x391e19ff, 0x244b07ff, 0x4a4140ff },
            { 0x548204ff, 0x689e1aff, 0xd96401ff, 0x71a928ff },
            { 0x000612ff, 0x1a140eff, 0x4a4140ff, 0x665e50ff },
            { 0x12077bff, 0x3f136bff, 0x054b95ff, 0x681c85ff },
            { 0x499ec9ff, 0x60879bff, 0xa06cd3ff, 0x79a7a7ff },
            { 0xd39270ff, 0xaaae89ff, 0xa4db68ff, 0xd5ac9bff },
            { 0x79a7a7ff, 0xa2c1bbff, 0xda8bb8ff, 0xa2c1bbff },
            { 0x4d2d45ff, 0x643654ff, 0x527b78ff, 0x908c97ff },
            { 0x4d2d45ff, 0x643654ff, 0x516d7eff, 0x8e7fb9ff },
            { 0x878260ff, 0xaf7a67ff, 0x8bad68ff, 0xbd8d96ff },
            { 0x245674ff, 0x2a606bff, 0x654786ff, 0x8a3fc4ff },
            { 0x516d7eff, 0x527b78ff, 0x8664aeff, 0x5a959fff },
            { 0x3c5889ff, 0x516d7eff, 0x6f4faeff, 0x516d7eff },
            { 0xf9a5ddff, 0xfcbbf9ff, 0xb9fafaff, 0xfcd7ffff },
            { 0xe3b72eff, 0x0c0635ff, 0x12077bff, 0x092359ff },
            { 0x917610ff, 0xab8950ff, 0x86de18ff, 0xd39270ff },
            { 0xcfcfaaff, 0xf3c1b3ff, 0xd1fe8eff, 0xffffd1ff },
            { 0xeba55fff, 0xe8ce7eff, 0xcafa14ff, 0xe8ce7eff },
            { 0x9e9623ff, 0x85bf60ff, 0xfd8f25ff, 0xa4db68ff },
            { 0x726941ff, 0x627846ff, 0x917610ff, 0x878260ff },
            { 0x1e3d45ff, 0x2a606bff, 0x77517eff, 0x60879bff },
            { 0x8a3fc4ff, 0x9f50bcff, 0x00cbeeff, 0xc542c3ff },
            { 0x082e9dff, 0x681c85ff, 0x2561cdff, 0x6f4faeff },
            { 0x5a6f05ff, 0x726941ff, 0x858700ff, 0x878260ff },
            { 0x404213ff, 0x4e5431ff, 0x726941ff, 0x839c6bff },
            { 0x681c85ff, 0x654786ff, 0x2073b5ff, 0x654786ff },
            { 0x107a05ff, 0x386330ff, 0x9d332aff, 0x627846ff },
            { 0x654786ff, 0x77517eff, 0x0eb2b6ff, 0x9f50bcff },
            { 0xc1982dff, 0xd39270ff, 0x92f935ff, 0xd1ba7bff },
            { 0x1e3d45ff, 0x4a4140ff, 0x806176ff, 0x4f9c76ff },
            { 0x386330ff, 0x627846ff, 0xb14f1dff, 0x61884fff },
            { 0xd5ac9bff, 0xcfcfaaff, 0xffffd1ff, 0x810208ff },
            { 0xe3b72eff, 0xe3b72eff, 0x092359ff, 0x681c85ff },
            { 0x548204ff, 0x878260ff, 0x9e9623ff, 0x8bad68ff },
            { 0x627846ff, 0x61884fff, 0xd84d27ff, 0x839c6bff },
            { 0x58174cff, 0x4d2d45ff, 0x0a7740ff, 0x643654ff },
            { 0x12077bff, 0x2c3ba1ff, 0x093dffff, 0x2c3ba1ff },
            { 0x5b0576ff, 0x58174cff, 0x097472ff, 0x643654ff },
            { 0x244b07ff, 0x386330ff, 0x823b05ff, 0x386330ff },
            { 0x917610ff, 0x9e6644ff, 0x72bd1dff, 0xaf7a67ff },
            { 0x61884fff, 0x4f9c76ff, 0xe64c63ff, 0x839c6bff },
            { 0xbd8d96ff, 0xb29fc1ff, 0x91d1b0ff, 0xd4bac2ff },
            { 0x9c0612ff, 0x6b3020ff, 0x209415ff, 0x814b33ff },
            { 0x8bad68ff, 0xaaae89ff, 0xcfcfaaff, 0xe5eda8ff },
            { 0x3c5889ff, 0x516d7eff, 0x8e7fb9ff, 0x7ab1bcff },
            { 0x0c0635ff, 0x170c4aff, 0x0c069fff, 0x092359ff },
            { 0xa4db68ff, 0xcfcfaaff, 0xfbdd7dff, 0xe5eda8ff },
            { 0x516d7eff, 0x806176ff, 0x908c97ff, 0xb29fc1ff },
            { 0xe3b72eff, 0x000612ff, 0x34081aff, 0x1e3d45ff },
            { 0x0f2b00ff, 0x31290dff, 0x4e5431ff, 0x627846ff },
            { 0xfe84feff, 0xc2a9ddff, 0x89f7f6ff, 0xfcbbf9ff },
            { 0x000612ff, 0x082126ff, 0x4e0536ff, 0x1e3d45ff },
            { 0xe3b72eff, 0xe3b72eff, 0x131a38ff, 0x245674ff },
            { 0x7ab1bcff, 0xb29fc1ff, 0xc69ff5ff, 0x9bd5e1ff },
            { 0x7ab1bcff, 0x9bd5e1ff, 0xfe84feff, 0x9bd5e1ff },
            { 0x6f4faeff, 0x8a3fc4ff, 0x14a0ffff, 0x9f50bcff },
            { 0x79a7a7ff, 0x7ab1bcff, 0xcc7bf6ff, 0x8bbaecff },
            { 0x908c97ff, 0xbc72a8ff, 0xa2c1bbff, 0xd3dbceff },
            { 0xaf7a67ff, 0xab8950ff, 0xa8bd77ff, 0xcfcfaaff },
            { 0x0a7740ff, 0x188257ff, 0xbd0b81ff, 0x10915eff },
            { 0x097472ff, 0x188257ff, 0xd3019dff, 0x527b78ff },
    };
    
    //        public final int[] ALL_COLORS = new int[256];     
    static {
//        for (int i = 1; i < 64; i++) {
//            int color = LAWN_BONUS_RAMP_VALUES[i | 128][2] = LAWN_BONUS_RAMP_VALUES[i][2] =
//                    Coloring.LAWN64[i],
//                    r = (color >>> 24),
//                    g = (color >>> 16 & 0xFF),
//                    b = (color >>> 8 & 0xFF);
//            LAWN_BONUS_RAMP_VALUES[i | 64][1] = LAWN_BONUS_RAMP_VALUES[i | 64][2] =
//                    LAWN_BONUS_RAMP_VALUES[i | 64][3] = color;
//            LAWN_BONUS_RAMP_VALUES[i | 192][0] = LAWN_BONUS_RAMP_VALUES[i | 192][2] = color;
//            int co = r - b, t = b + (co >> 1), cg = g - t, y = t + (cg >> 1),
//                    yBright = y * 21 >> 4, yDim = y * 11 >> 4, yDark = y * 6 >> 4, chromO, chromG;
//            chromO = (co * 3) >> 2;
//            chromG = (cg * 3) >> 2;
//            t = yDim - (chromG >> 1);
//            g = chromG + t;
//            b = t - (chromO >> 1);
//            r = b + chromO;
//            LAWN_BONUS_RAMP_VALUES[i | 192][1] = LAWN_BONUS_RAMP_VALUES[i | 128][1] =
//                    LAWN_BONUS_RAMP_VALUES[i | 64][0] = LAWN_BONUS_RAMP_VALUES[i][1] =
//                            MathUtils.clamp(r, 0, 255) << 24 |
//                                    MathUtils.clamp(g, 0, 255) << 16 |
//                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//            chromO = (co * 3) >> 2;
//            chromG = (cg * (256 - yBright) * 3) >> 9;
//            t = yBright - (chromG >> 1);
//            g = chromG + t;
//            b = t - (chromO >> 1);
//            r = b + chromO;
//            LAWN_BONUS_RAMP_VALUES[i | 192][3] = LAWN_BONUS_RAMP_VALUES[i | 128][3] =
//                    LAWN_BONUS_RAMP_VALUES[i][3] =
//                            MathUtils.clamp(r, 0, 255) << 24 |
//                                    MathUtils.clamp(g, 0, 255) << 16 |
//                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//            chromO = (co * 13) >> 4;
//            chromG = (cg * (256 - yDark) * 13) >> 11;
//            t = yDark - (chromG >> 1);
//            g = chromG + t;
//            b = t - (chromO >> 1);
//            r = b + chromO;
//            LAWN_BONUS_RAMP_VALUES[i | 128][0] = LAWN_BONUS_RAMP_VALUES[i][0] =
//                    MathUtils.clamp(r, 0, 255) << 24 |
//                            MathUtils.clamp(g, 0, 255) << 16 |
//                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//        }
//        StringBuilder sb = new StringBuilder(1024).append("private static final int[][] LAWN_BONUS_RAMP_VALUES = new int[][] {\n");
//        for (int i = 0; i < 256; i++) {
//            sb.append("{ 0x");
//            StringKit.appendHex(sb, LAWN_BONUS_RAMP_VALUES[i][0]);
//            StringKit.appendHex(sb.append(", 0x"), LAWN_BONUS_RAMP_VALUES[i][1]);
//            StringKit.appendHex(sb.append(", 0x"), LAWN_BONUS_RAMP_VALUES[i][2]);
//            StringKit.appendHex(sb.append(", 0x"), LAWN_BONUS_RAMP_VALUES[i][3]);
//            sb.append(" },\n");
//
//        }
//        System.out.println(sb.append("};"));
//        for (int i = 0; i < 64; i++) {
//            System.arraycopy(LAWN_BONUS_RAMP_VALUES[i], 0, LawnBonusPalette, i << 2, 4);
//        }


//        {
//            for (int i = 1; i < 64; i++) {
//                int color = FLESURRECT_BONUS_RAMP_VALUES[i | 128][2] = FLESURRECT_BONUS_RAMP_VALUES[i][2] =
//                        Coloring.FLESURRECT[i],
//                        r = (color >>> 24),
//                        g = (color >>> 16 & 0xFF),
//                        b = (color >>> 8 & 0xFF);
//                FLESURRECT_BONUS_RAMP_VALUES[i | 64][1] =
//                        FLESURRECT_BONUS_RAMP_VALUES[i | 64][2] = FLESURRECT_BONUS_RAMP_VALUES[i | 64][3] = color;
//                FLESURRECT_BONUS_RAMP_VALUES[i | 192][0] = FLESURRECT_BONUS_RAMP_VALUES[i | 192][2] = color;
//                int y = luma(r, g, b) >>> 11,
//                        yBright = y * 5, yDim = y * 3, yDark = y << 1;
//                float luma, warm, mild;
//                float cw = r - b + 255 >>> 4;
//                float cm = g - b + 255 >>> 4;
//
//                float cwf = (cw - 15.5f) * 0x1.08421p-4f;
//                float cmf = (cm - 15.5f) * 0x1.08421p-4f;
//
//                warm = cwf;
//                mild = cmf;
//                luma = yDim * 0x1p-8f;
//                g = (int) ((luma + mild * 0.5f - warm * 0.375f) * 255);
//                b = (int) ((luma - warm * 0.375f - mild * 0.5f) * 255);
//                r = (int) ((luma + warm * 0.625f - mild * 0.5f) * 255);
//                FLESURRECT_BONUS_RAMP_VALUES[i|192][1] = FLESURRECT_BONUS_RAMP_VALUES[i|128][1] =
//                        FLESURRECT_BONUS_RAMP_VALUES[i|64][0] = FLESURRECT_BONUS_RAMP_VALUES[i][1] =
//                        MathUtils.clamp(r, 0, 255) << 24 |
//                                MathUtils.clamp(g, 0, 255) << 16 |
//                                MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//                warm = cwf;
//                mild = cmf;
//                luma = yBright * 0x1p-8f;
//                g = (int) ((luma + mild * 0.5f - warm * 0.375f) * 255);
//                b = (int) ((luma - warm * 0.375f - mild * 0.5f) * 255);
//                r = (int) ((luma + warm * 0.625f - mild * 0.5f) * 255);
//                FLESURRECT_BONUS_RAMP_VALUES[i|192][3] = FLESURRECT_BONUS_RAMP_VALUES[i|128][3] =
//                        FLESURRECT_BONUS_RAMP_VALUES[i][3] =
//                        MathUtils.clamp(r, 0, 255) << 24 |
//                                MathUtils.clamp(g, 0, 255) << 16 |
//                                MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//                warm = cwf;
//                mild = cmf;
//                luma = yDark * 0x1p-8f;
//                g = (int) ((luma + mild * 0.5f - warm * 0.375f) * 255);
//                b = (int) ((luma - warm * 0.375f - mild * 0.5f) * 255);
//                r = (int) ((luma + warm * 0.625f - mild * 0.5f) * 255);
//                FLESURRECT_BONUS_RAMP_VALUES[i|128][0] = FLESURRECT_BONUS_RAMP_VALUES[i][0] =
//                        MathUtils.clamp(r, 0, 255) << 24 |
//                                MathUtils.clamp(g, 0, 255) << 16 |
//                                MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
//
//            }
//            StringBuilder sb = new StringBuilder(1024).append("private static final int[][] FLESURRECT_BONUS_RAMP_VALUES = new int[][] {\n");
//            for (int i = 0; i < 256; i++) {
//                sb.append("{ 0x");
//                StringKit.appendHex(sb, FLESURRECT_BONUS_RAMP_VALUES[i][0]);
//                StringKit.appendHex(sb.append(", 0x"), FLESURRECT_BONUS_RAMP_VALUES[i][1]);
//                StringKit.appendHex(sb.append(", 0x"), FLESURRECT_BONUS_RAMP_VALUES[i][2]);
//                StringKit.appendHex(sb.append(", 0x"), FLESURRECT_BONUS_RAMP_VALUES[i][3]);
//                sb.append(" },\n");
//
//            }
//            System.out.println(sb.append("};"));
//
//        }
        for (int i = 0; i < 256; i++) {
            System.arraycopy(TWIRL_BONUS_RAMP_VALUES[i], 0, TOASTY_BONUS_RAMP_VALUES[i], 0, 4);
            PaletteReducer.hueShiftPalette(TOASTY_BONUS_RAMP_VALUES[i]);
        }
        for (int i = 0; i < 64; i++) {
            System.arraycopy(FLESURRECT_BONUS_RAMP_VALUES[i], 0, FlesurrectBonusPalette, i << 2, 4);
            System.arraycopy(WARD_BONUS_RAMP_VALUES[i], 0, WardBonusPalette, i << 2, 4);
            System.arraycopy(TOASTY_BONUS_RAMP_VALUES[i], 0, ToastyBonusPalette, i << 2, 4);
//            System.arraycopy(CUBICLE_BONUS_RAMP_VALUES[i], 0, CubicleBonusPalette, i << 2, 4);
            System.arraycopy(TWIRL_BONUS_RAMP_VALUES[i], 0, TwirlBonusPalette, i << 2, 4);
        }
    }
//    public static final Colorizer FlesurrectBonusColorizer = new Colorizer(Coloring.FLESURRECT_REDUCER) {
//        private final byte[] primary = {
//                63, 24, 27, 34, 42, 49, 55
//        }, grays = {
//                1, 2, 3, 4, 5, 6, 7, 8, 9
//        };
//
//        @Override
//        public byte[] mainColors() {
//            return primary;
//        }
//
//        /**
//         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
//         */
//        @Override
//        public byte[] grayscale() {
//            return grays;
//        }
//
//        @Override
//        public byte brighten(byte voxel) {
//            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
//            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
//            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
//            return (byte) (FLESURRECT_RAMPS[voxel & 0x3F][3] | (voxel & 0xC0));
//        }
//
//        @Override
//        public byte darken(byte voxel) {
//            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
//            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
//            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
//            return (byte) (FLESURRECT_RAMPS[voxel & 0x3F][1] | (voxel & 0xC0));
//        }
//
//        @Override
//        public int dimmer(int brightness, byte voxel) {
//            return FLESURRECT_BONUS_RAMP_VALUES[voxel & 0xFF][
//                    brightness <= 0
//                            ? 0
//                            : brightness >= 3
//                            ? 3
//                            : brightness
//                    ];
//        }
//
//        @Override
//        public int getShadeBit() {
//            return 0x40;
//        }
//        @Override
//        public int getWaveBit() {
//            return 0x80;
//        }
//    };

    public static final Colorizer WardBonusColorizer = new Colorizer(new PaletteReducer(Coloring.WARD)) {
        private final byte[] primary = {
                reducer.reduceIndex(0xFF0000FF),reducer.reduceIndex(0xFFFF00FF),
                reducer.reduceIndex(0x00FF00FF),reducer.reduceIndex(0x00FFFFFF),
                reducer.reduceIndex(0x0000FFFF),reducer.reduceIndex(0xFF00FFFF),
        }, grays = {
                reducer.reduceIndex(0x000000FF),reducer.reduceIndex(0x333333FF),
                reducer.reduceIndex(0x666666FF),reducer.reduceIndex(0x999999FF),
                reducer.reduceIndex(0xCCCCCCFF),reducer.reduceIndex(0xFFFFFFFF),
        };

        @Override
        public byte[] mainColors() {
            return primary;
        }

        /**
         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
         */
        @Override
        public byte[] grayscale() {
            return grays;
        }

        @Override
        public byte brighten(byte voxel) {
            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
            return (byte) (WARD_RAMPS[voxel & 0x3F][3] | (voxel & 0xC0));
        }

        @Override
        public byte darken(byte voxel) {
            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
            return (byte) (WARD_RAMPS[voxel & 0x3F][1] | (voxel & 0xC0));
        }

        @Override
        public int dimmer(int brightness, byte voxel) {
            return WARD_BONUS_RAMP_VALUES[voxel & 0xFF][
                    brightness <= 0
                            ? 0
                            : Math.min(brightness, 3)
                    ];
        }

        @Override
        public int getShadeBit() {
            return 0x40;
        }
        @Override
        public int getWaveBit() {
            return 0x80;
        }
    };

    public static final Colorizer ToastyBonusColorizer = new Colorizer(new PaletteReducer(Coloring.TWIRL64)) {
        private final byte[] primary = {
                reducer.reduceIndex(0xFF0000FF),reducer.reduceIndex(0xFFFF00FF),
                reducer.reduceIndex(0x00FF00FF),reducer.reduceIndex(0x00FFFFFF),
                reducer.reduceIndex(0x0000FFFF),reducer.reduceIndex(0xFF00FFFF),
        }, grays = {
                reducer.reduceIndex(0x000000FF),reducer.reduceIndex(0x333333FF),
                reducer.reduceIndex(0x666666FF),reducer.reduceIndex(0x999999FF),
                reducer.reduceIndex(0xCCCCCCFF),reducer.reduceIndex(0xFFFFFFFF),
        };
        {
            reducer.hueShift();
        }

        @Override
        public byte[] mainColors() {
            return primary;
        }

        /**
         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
         */
        @Override
        public byte[] grayscale() {
            return grays;
        }

        @Override
        public byte brighten(byte voxel) {
            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
            return (byte) (TWIRL_RAMPS[voxel & 0x3F][3] | (voxel & 0xC0));
        }

        @Override
        public byte darken(byte voxel) {
            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
            return (byte) (TWIRL_RAMPS[voxel & 0x3F][1] | (voxel & 0xC0));
        }

        @Override
        public int dimmer(int brightness, byte voxel) {
            return TOASTY_BONUS_RAMP_VALUES[voxel & 0xFF][
                    brightness <= 0
                            ? 0
                            : Math.min(brightness, 3)
                    ];
        }

        @Override
        public int getShadeBit() {
            return 0x40;
        }
        @Override
        public int getWaveBit() {
            return 0x80;
        }
    };
    
    public static final Colorizer TwirlBonusColorizer = new Colorizer(new PaletteReducer(Coloring.TWIRL64)) {
        private final byte[] primary = {
                reducer.reduceIndex(0xFF0000FF),reducer.reduceIndex(0xFFFF00FF),
                reducer.reduceIndex(0x00FF00FF),reducer.reduceIndex(0x00FFFFFF),
                reducer.reduceIndex(0x0000FFFF),reducer.reduceIndex(0xFF00FFFF),
        }, grays = {
                reducer.reduceIndex(0x000000FF),reducer.reduceIndex(0x333333FF),
                reducer.reduceIndex(0x666666FF),reducer.reduceIndex(0x999999FF),
                reducer.reduceIndex(0xCCCCCCFF),reducer.reduceIndex(0xFFFFFFFF),
        };

        @Override
        public byte[] mainColors() {
            return primary;
        }

        /**
         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
         */
        @Override
        public byte[] grayscale() {
            return grays;
        }

        @Override
        public byte brighten(byte voxel) {
            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
            return (byte) (TWIRL_RAMPS[voxel & 0x3F][3] | (voxel & 0xC0));
        }

        @Override
        public byte darken(byte voxel) {
            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
            return (byte) (TWIRL_RAMPS[voxel & 0x3F][1] | (voxel & 0xC0));
        }

        @Override
        public int dimmer(int brightness, byte voxel) {
            return TWIRL_BONUS_RAMP_VALUES[voxel & 0xFF][
                    brightness <= 0
                            ? 0
                            : Math.min(brightness, 3)
                    ];
        }

        @Override
        public int getShadeBit() {
            return 0x40;
        }
        @Override
        public int getWaveBit() {
            return 0x80;
        }
    };

//    public static final Colorizer CubicleBonusColorizer = new Colorizer(new PaletteReducer(Coloring.CUBICLE64)) {
//        private final byte[] primary = {
//                7, 13, 22, 31, 40, 49, 58
//        }, grays = {
//                1, 2, 3, 4, 6, 5, 7, 8, 9
//        };
//
//        @Override
//        public byte[] mainColors() {
//            return primary;
//        }
//
//        /**
//         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
//         */
//        @Override
//        public byte[] grayscale() {
//            return grays;
//        }
//
//        @Override
//        public byte brighten(byte voxel) {
//            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
//            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
//            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
//            return (byte) (CUBICLE64_RAMPS[voxel & 0x3F][3] | (voxel & 0xC0));
//        }
//
//        @Override
//        public byte darken(byte voxel) {
//            // the second half of voxels (with bit 0x40 set) don't shade visually, but Colorizer uses this method to
//            // denote a structural change to the voxel's makeup, so this uses the first 64 voxel colors to shade both
//            // halves, then marks voxels from the second half back to being an unshaded voxel as the last step.
//            return (byte) (CUBICLE64_RAMPS[voxel & 0x3F][1] | (voxel & 0xC0));
//        }
//
//        @Override
//        public int dimmer(int brightness, byte voxel) {
//            return CUBICLE_BONUS_RAMP_VALUES[voxel & 0xFF][Math.max(0, Math.min(brightness, 3))];
//        }
//
//        @Override
//        public int getShadeBit() {
//            return 0x40;
//        }
//        @Override
//        public int getWaveBit() {
//            return 0x80;
//        }
//    };

    public static Colorizer arbitraryColorizer(final int[] palette) {
        final int COUNT = palette.length;
        PaletteReducer reducer = new PaletteReducer(palette);

        final byte[] primary = {
                reducer.reduceIndex(0xFF0000FF), reducer.reduceIndex(0xFFFF00FF), reducer.reduceIndex(0x00FF00FF),
                reducer.reduceIndex(0x00FFFFFF), reducer.reduceIndex(0x0000FFFF), reducer.reduceIndex(0xFF00FFFF)
        }, grays = {
                reducer.reduceIndex(0x000000FF), reducer.reduceIndex(0x444444FF), reducer.reduceIndex(0x888888FF),
                reducer.reduceIndex(0xCCCCCCFF), reducer.reduceIndex(0xFFFFFFFF)
        };
        final int THRESHOLD = 64;//0.011; // threshold controls the "stark-ness" of color changes; must not be negative.
        final byte[] paletteMapping = new byte[1 << 16];
        final int[] reverse = new int[COUNT];
        final byte[][] ramps = new byte[COUNT][4];
        final int[] lumas = new int[COUNT], cos = new int[COUNT], cgs = new int[COUNT];
        final int yLim = 63, coLim = 31, cgLim = 31, shift1 = 6, shift2 = 11;
        int color, r, g, b, co, cg, t;
        for (int i = 1; i < COUNT; i++) {
            color = palette[i];
            if((color & 0x80) == 0)
            {
                lumas[i] = -0x70000000; // very very negative, blocks transparent colors from mixing into opaque ones
                continue;
            }
            r = (color >>> 24);
            g = (color >>> 16 & 0xFF);
            b = (color >>> 8 & 0xFF);
            co = r - b;
            t = b + (co >> 1);
            cg = g - t;
            paletteMapping[
                    reverse[i] = 
                              (lumas[i] = luma(r, g, b) >>> 11)
                            | (cos[i] = co + 255 >>> 4) << shift1
                            | (cgs[i] = cg + 255 >>> 4) << shift2] = (byte) i;
        }

        for (int icg = 0; icg <= cgLim; icg++) {
            for (int ico = 0; ico <= coLim; ico++) {
                for (int iy = 0; iy <= yLim; iy++) {
                    final int c2 = icg << shift2 | ico << shift1 | iy;
                    if (paletteMapping[c2] == 0) {
                        int dist = 0x7FFFFFFF;
                        for (int i = 1; i < COUNT; i++) {
                            if (Math.abs(lumas[i] - iy) < 28 && dist > (dist = Math.min(dist, difference(lumas[i], cos[i], cgs[i], iy, ico, icg))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }

        float adj, cof, cgf;
        int idx2;
//        System.out.println("{\n{ 0, 0, 0, 0 },");
        for (int i = 1; i < COUNT; i++) {
            int rev = reverse[i], y = rev & yLim, match = i;
            cof = ((co = cos[i]) - 16) * 0x1.111112p-5f;
            cgf = ((cg = cgs[i]) - 16) * 0x1.111112p-5f;
            ramps[i][2] = (byte)i;
            ramps[i][3] = grays[4];//15;  //0xFFFFFFFF, white
            ramps[i][1] = grays[0];//0x010101FF, black
            ramps[i][0] = grays[0];//0x010101FF, black
            for (int yy = y + 2, rr = rev + 2; yy <= yLim; yy++, rr++) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], cos[idx2], cgs[idx2], y, co, cg) > THRESHOLD) {
                    ramps[i][3] = paletteMapping[rr];
                    break;
                }
                adj = 1f + ((yLim + 1 >>> 1) - yy) * 0x1p-10f;
                cof = MathUtils.clamp(cof * adj, -0.5f, 0.5f);
                cgf = MathUtils.clamp(cgf * adj + 0x1.8p-10f, -0.5f, 0.5f);

                rr = yy
                        | (co = (int) ((cof + 0.5f) * coLim)) << shift1
                        | (cg = (int) ((cgf + 0.5f) * cgLim)) << shift2;
            }
            cof = ((co = cos[i]) - 16) * 0x0.Bp-5f;
            cgf = ((cg = cgs[i]) - 16) * 0x0.Bp-5f;
            for (int yy = y - 2, rr = rev - 2; yy > 0; rr--) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], cos[idx2], cgs[idx2], y, co, cg) > THRESHOLD) {
                    ramps[i][1] = paletteMapping[rr];
                    rev = rr;
                    y = yy;
                    match = paletteMapping[rr] & 255;
                    break;
                }
                adj = 1f + (yy - (yLim + 1 >>> 1)) * 0x1p-10f;
                cof = MathUtils.clamp(cof * adj, -0.5f, 0.5f);
                cgf = MathUtils.clamp(cgf * adj - 0x1.8p-10f, -0.5f, 0.5f);

//                cof = (cof - 0.5f) * 0.984375f + 0.5f;
//                cgf = (cgf + 0.5f) * 0.984375f - 0.5f;
                rr = yy
                        | (co = (int) ((cof + 0.5f) * coLim)) << shift1
                        | (cg = (int) ((cgf + 0.5f) * cgLim)) << shift2;

//                cof = MathUtils.clamp(cof * 0.9375f, -0.5f, 0.5f);
//                cgf = MathUtils.clamp(cgf * 0.9375f, -0.5f, 0.5f);
//                rr = yy
//                        | (int) ((cof + 0.5f) * 63) << 7
//                        | (int) ((cgf + 0.5f) * 63) << 13;
                if (--yy == 0) {
                    match = -1;
                }
            }
            if (match >= 0) {
                cof = ((co = cos[match]) - 16) * 0x1.111112p-5f;
                cgf = ((cg = cgs[match]) - 16) * 0x1.111112p-5f;
                for (int yy = y - 3, rr = rev - 3; yy > 0; yy--, rr--) {
                    if ((idx2 = paletteMapping[rr] & 255) != match && difference(lumas[idx2], cos[idx2], cgs[idx2], y, co, cg) > THRESHOLD) {
                        ramps[i][0] = paletteMapping[rr];
                        break;
                    }
                    adj = 1f + (yy - (yLim + 1 >>> 1)) * 0x1p-10f;
                    cof = MathUtils.clamp(cof * adj, -0.5f, 0.5f);
                    cgf = MathUtils.clamp(cgf * adj - 0x1.8p-10f, -0.5f, 0.5f);

//                    cof = (cof - 0.5f) * 0.96875f + 0.5f;
//                    cgf = (cgf + 0.5f) * 0.96875f - 0.5f;
                    rr = yy
                            | (co = (int) ((cof + 0.5f) * coLim)) << shift1
                            | (cg = (int) ((cgf + 0.5f) * cgLim)) << shift2;

//                    cof = MathUtils.clamp(cof * 0.9375f, -0.5f, 0.5f);
//                    cgf = MathUtils.clamp(cgf * 0.9375f, -0.5f, 0.5f);
//                    rr = yy
//                            | (int) ((cof + 0.5f) * 63) << 7
//                            | (int) ((cgf + 0.5f) * 63) << 13;
                }
            }
//            System.out.println("{ " + ramps[i][0] + ", " + ramps[i][1] + ", " + ramps[i][2] + ", " + ramps[i][3] + " },");
        }
//        System.out.println("};");


        return new Colorizer(reducer) {

            @Override
            public byte[] mainColors() {
                return primary;
            }

            /**
             * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
             */
            @Override
            public byte[] grayscale() {
                return grays;
            }

            @Override
            public byte brighten(byte voxel) {
                return ramps[(voxel & 0xFF) % COUNT][3];
            }

            @Override
            public byte darken(byte voxel) {
                return ramps[(voxel & 0xFF) % COUNT][1];
            }

            @Override
            public int dimmer(int brightness, byte voxel) {
                return palette[ramps[(voxel & 0xFF) % COUNT][
                        brightness <= 0
                                ? 0
                                : brightness >= 3
                                ? 3
                                : brightness
                        ] & 0xFF];
            }

            @Override
            public int getShadeBit() {
                return 0;
            }

            @Override
            public int getWaveBit() {
                return 0;
            }
        };
    }
    public static Colorizer arbitraryLABColorizer(final int[] palette) {
        final int COUNT = palette.length;
        PaletteReducer reducer = new PaletteReducer(palette);

        final byte[] primary = {
                reducer.reduceIndex(0xFF0000FF), reducer.reduceIndex(0xFFFF00FF), reducer.reduceIndex(0x00FF00FF),
                reducer.reduceIndex(0x00FFFFFF), reducer.reduceIndex(0x0000FFFF), reducer.reduceIndex(0xFF00FFFF)
        }, grays = {
                reducer.reduceIndex(0x000000FF), reducer.reduceIndex(0x444444FF), reducer.reduceIndex(0x888888FF),
                reducer.reduceIndex(0xCCCCCCFF), reducer.reduceIndex(0xFFFFFFFF)
        };
        final CIELABConverter.Lab[] labs = new CIELABConverter.Lab[COUNT];
        final byte[][] ramps = new byte[COUNT][4];
        for (int i = 0; i < COUNT; i++) {
            labs[i] = new CIELABConverter.Lab(palette[i]);
            ramps[i][2] = (byte)i;
        }
        CIELABConverter.Lab lab, lab2;
        for (int i = 0; i < COUNT; i++) {
            if((lab = labs[i]).alpha == 0.0) {
                ramps[i][0] = ramps[i][1] = ramps[i][3] = (byte)i;
                continue;
            }
            ramps[i][0] = grays[0];
            ramps[i][1] = grays[0];
            ramps[i][3] = grays[4];
            int dimIndex = ramps[i][1] & 0xFF;
            double lighter = Double.POSITIVE_INFINITY, dimmer = Double.POSITIVE_INFINITY, temp;
            for (int j = 0; j < COUNT; j++) {
                if(i == j || (lab2 = labs[j]).alpha == 0.0) continue;
                if(lab2.L >= Math.min(lab.L + 3, lab.L * 1.06) && lighter > (temp = CIELABConverter.delta(lab, lab2, 1.0, 13.0, 18.0)))
                {
                    lighter = temp;
                    ramps[i][3] = (byte)j;
                } 
                else if(lab2.L <= Math.max(lab.L - 8, lab.L * 0.84) && dimmer > (temp = CIELABConverter.delta(lab, lab2, 1.0, 11.0, 16.0)))
                {
                    dimmer = temp;
                    ramps[i][1] = (byte)j;
                    dimIndex = j;
                }
            }
            lighter = Math.max(labs[dimIndex].L - 10, labs[dimIndex].L * 0.8);
            dimmer = Double.POSITIVE_INFINITY;
            for (int j = 0; j < COUNT; j++) {
                if (i == j || j == dimIndex || (lab2 = labs[j]).alpha == 0.0)
                    continue;
                if (lab2.L <= lighter && dimmer > (temp = CIELABConverter.delta(lab, lab2, 1.0, 12.0, 17.0))) {
                    dimmer = temp;
                    ramps[i][0] = (byte)j;
                }
            }
        }

        return new Colorizer(reducer) {

            @Override
            public byte[] mainColors() {
                return primary;
            }

            /**
             * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
             */
            @Override
            public byte[] grayscale() {
                return grays;
            }

            @Override
            public byte brighten(byte voxel) {
                return ramps[(voxel & 0xFF) % COUNT][3];
            }

            @Override
            public byte darken(byte voxel) {
                return ramps[(voxel & 0xFF) % COUNT][1];
            }

            @Override
            public int dimmer(int brightness, byte voxel) {
                return palette[ramps[(voxel & 0xFF) % COUNT][
                        Math.max(0, Math.min(brightness, 3))] & 0xFF];
            }

            @Override
            public int getShadeBit() {
                return 0;
            }

            @Override
            public int getWaveBit() {
                return 0;
            }
        };
    }
    public static Colorizer arbitraryBonusColorizer(final int[] palette) {
        final int COUNT = palette.length;
        PaletteReducer reducer = new PaletteReducer(palette);

        final byte[] primary = {
                reducer.reduceIndex(0xFF0000FF), reducer.reduceIndex(0xFFFF00FF), reducer.reduceIndex(0x00FF00FF),
                reducer.reduceIndex(0x00FFFFFF), reducer.reduceIndex(0x0000FFFF), reducer.reduceIndex(0xFF00FFFF)
        }, grays = {
                reducer.reduceIndex(0x000000FF), reducer.reduceIndex(0x444444FF), reducer.reduceIndex(0x888888FF),
                reducer.reduceIndex(0xCCCCCCFF), reducer.reduceIndex(0xFFFFFFFF)
        };
        final int THRESHOLD = 64;//0.011; // threshold controls the "stark-ness" of color changes; must not be negative.
        final byte[] paletteMapping = new byte[1 << 16];
        final int[] reverse = new int[COUNT];
        final byte[][] ramps = new byte[COUNT][4];
        final int[][] values = new int[COUNT][4];
        
        final int[] lumas = new int[COUNT], cos = new int[COUNT], cgs = new int[COUNT];
        final int yLim = 63, coLim = 31, cgLim = 31, shift1 = 6, shift2 = 11;
        int color, r, g, b, co, cg, t;
        for (int i = 1; i < COUNT; i++) {
            color = palette[i];
            if((color & 0x80) == 0)
            {
                lumas[i] = -0x70000000; // very very negative, blocks transparent colors from mixing into opaque ones
                continue;
            }
            r = (color >>> 24);
            g = (color >>> 16 & 0xFF);
            b = (color >>> 8 & 0xFF);
            co = r - b;
            t = b + (co >> 1);
            cg = g - t;
            paletteMapping[
                    reverse[i] = 
                              (lumas[i] = luma(r, g, b) >>> 11)
                            | (cos[i] = co + 255 >>> 4) << shift1
                            | (cgs[i] = cg + 255 >>> 4) << shift2] = (byte) i;
        }

        for (int icg = 0; icg <= cgLim; icg++) {
            for (int ico = 0; ico <= coLim; ico++) {
                for (int iy = 0; iy <= yLim; iy++) {
                    final int c2 = icg << shift2 | ico << shift1 | iy;
                    if (paletteMapping[c2] == 0) {
                        int dist = 0x7FFFFFFF;
                        for (int i = 1; i < COUNT; i++) {
                            if (Math.abs(lumas[i] - iy) < 28 && dist > (dist = Math.min(dist, difference(lumas[i], cos[i], cgs[i], iy, ico, icg))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }

        float adj, cof, cgf;
        int idx2;
        for (int i = 1; i < COUNT; i++) {
            int rev = reverse[i], y = rev & yLim, match = i,
                    yBright = y * 5, yDim = y * 3, yDark = y << 1, chromO, chromG;

            cof = ((co = cos[i]) - 16) * 0x1.111112p-5f;
            cgf = ((cg = cgs[i]) - 16) * 0x1.111112p-5f;

            //values[i][0] = values[i][1] = values[i][3] = 
            values[i][2] = palette[i];             

            chromO = (co * 395 + 31 >> 5) - 192;
            chromG = (cg * 395 + 31 >> 5) - 192;
            t = yDim - (chromG >> 1);
            g = chromG + t;
            b = t - (chromO >> 1);
            r = b + chromO;

            values[i][1] =
                    MathUtils.clamp(r, 0, 255) << 24 |
                            MathUtils.clamp(g, 0, 255) << 16 |
                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
            chromO = (co * 333 + 31 >> 5) - 162;
            chromG = (cg * 333 + 31 >> 5) - 162;//(cg * (256 - yBright) * 395 + 4095 >> 12) - 192;
            t = yBright - (chromG >> 1);
            g = chromG + t;
            b = t - (chromO >> 1);
            r = b + chromO;
            values[i][3] =
                    MathUtils.clamp(r, 0, 255) << 24 |
                            MathUtils.clamp(g, 0, 255) << 16 |
                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
            chromO = (co * 215 >> 4) - 208;
            chromG = (cg * 215 >> 4) - 208;//(cg * (256 - yDark) * 215 >> 11) - 208;
            t = yDark - (chromG >> 1);
            g = chromG + t;
            b = t - (chromO >> 1);
            r = b + chromO;
            values[i][0] =
                    MathUtils.clamp(r, 0, 255) << 24 |
                            MathUtils.clamp(g, 0, 255) << 16 |
                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;

            ramps[i][2] = (byte)i;
            ramps[i][3] = grays[4];//15;  //0xFFFFFFFF, white
            ramps[i][1] = grays[0];//0x010101FF, black
            ramps[i][0] = grays[0];//0x010101FF, black
            for (int yy = y + 2, rr = rev + 2; yy <= yLim; yy++, rr++) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], cos[idx2], cgs[idx2], y, co, cg) > THRESHOLD) {
                    ramps[i][3] = paletteMapping[rr];
                    break;
                }
                adj = 1f + ((yLim + 1 >>> 1) - yy) * 0x1p-10f;
                cof = MathUtils.clamp(cof * adj, -0.5f, 0.5f);
                cgf = MathUtils.clamp(cgf * adj + 0x1.8p-10f, -0.5f, 0.5f);

                rr = yy
                        | (co = (int) ((cof + 0.5f) * coLim)) << shift1
                        | (cg = (int) ((cgf + 0.5f) * cgLim)) << shift2;
            }
            cof = ((co = cos[i]) - 16) * 0x0.Bp-5f;
            cgf = ((cg = cgs[i]) - 16) * 0x0.Bp-5f;
            for (int yy = y - 2, rr = rev - 2; yy > 0; rr--) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], cos[idx2], cgs[idx2], y, co, cg) > THRESHOLD) {
                    ramps[i][1] = paletteMapping[rr];
                    rev = rr;
                    y = yy;
                    match = paletteMapping[rr] & 255;
                    break;
                }
                adj = 1f + (yy - (yLim + 1 >>> 1)) * 0x1p-10f;
                cof = MathUtils.clamp(cof * adj, -0.5f, 0.5f);
                cgf = MathUtils.clamp(cgf * adj - 0x1.8p-10f, -0.5f, 0.5f);

                rr = yy
                        | (co = (int) ((cof + 0.5f) * coLim)) << shift1
                        | (cg = (int) ((cgf + 0.5f) * cgLim)) << shift2;

                if (--yy == 0) {
                    match = -1;
                }
            }
            if (match >= 0) {
                cof = ((co = cos[match]) - 16) * 0x1.111112p-5f;
                cgf = ((cg = cgs[match]) - 16) * 0x1.111112p-5f;
                for (int yy = y - 3, rr = rev - 3; yy > 0; yy--, rr--) {
                    if ((idx2 = paletteMapping[rr] & 255) != match && difference(lumas[idx2], cos[idx2], cgs[idx2], y, co, cg) > THRESHOLD) {
                        ramps[i][0] = paletteMapping[rr];
                        break;
                    }
                    adj = 1f + (yy - (yLim + 1 >>> 1)) * 0x1p-10f;
                    cof = MathUtils.clamp(cof * adj, -0.5f, 0.5f);
                    cgf = MathUtils.clamp(cgf * adj - 0x1.8p-10f, -0.5f, 0.5f);
                    
                    rr = yy
                            | (co = (int) ((cof + 0.5f) * coLim)) << shift1
                            | (cg = (int) ((cgf + 0.5f) * cgLim)) << shift2;
                }
            }
        }


        return new Colorizer(reducer) {

            @Override
            public byte[] mainColors() {
                return primary;
            }

            /**
             * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
             */
            @Override
            public byte[] grayscale() {
                return grays;
            }

            @Override
            public byte brighten(byte voxel) {
                return ramps[(voxel & 0xFF) % COUNT][3];
            }

            @Override
            public byte darken(byte voxel) {
                return ramps[(voxel & 0xFF) % COUNT][1];
            }

            @Override
            public int dimmer(int brightness, byte voxel) {
                return values[voxel & 0xFF][
                        brightness <= 0
                                ? 0
                                : Math.min(brightness, 3)
                        ];
            }

            @Override
            public int getShadeBit() {
                return 0;
            }

            @Override
            public int getWaveBit() {
                return 0;
            }
        };
    }

    /**
     * Gets a Colorizer that can produce all of the colors in {@code palette} as well as an equal amount of lighter
     * colors that usually tend to have warmer hues, and twice as many cooler colors that usually tend to have cooler
     * hues (half of these are somewhat darker and half are much darker). The {@code heat} parameter is usually
     * {@code 1.0f} to make lighter colors warmer (matching the artistic effect of a yellow, orange, or red light
     * source), {@link 0.0f} to keep the warmth as-is, {@code -1.0f} to make lighter colors cooler (matching the effect
     * of a very blue-green light source such as exaggerated fluorescent lighting), or some other values between or just
     * outside that range. The {@code heat} should usually not be greater than {@code 2.0f} or less than {@code -2.0f}.
     * @param palette an int array of RGBA8888 colors, as found in {@link Coloring} 
     * @param heat typically between -1.0f and 1.0f, with positive values making lighter colors use warmer hues
     * @return a new Colorizer that will use a palette 4 times the size of the given {@code palette}
     */
    public static Colorizer arbitraryWarmingColorizer(final int[] palette, float heat) {
        final int COUNT = palette.length;
        PaletteReducer reducer = new PaletteReducer(palette);

        final byte[] primary = {
                reducer.reduceIndex(0xFF0000FF), reducer.reduceIndex(0xFFFF00FF), reducer.reduceIndex(0x00FF00FF),
                reducer.reduceIndex(0x00FFFFFF), reducer.reduceIndex(0x0000FFFF), reducer.reduceIndex(0xFF00FFFF)
        }, grays = {
                reducer.reduceIndex(0x000000FF), reducer.reduceIndex(0x444444FF), reducer.reduceIndex(0x888888FF),
                reducer.reduceIndex(0xCCCCCCFF), reducer.reduceIndex(0xFFFFFFFF)
        };
        final int THRESHOLD = 64;//0.011; // threshold controls the "stark-ness" of color changes; must not be negative.
        final byte[] paletteMapping = new byte[1 << 16];
        final int[] reverse = new int[COUNT];
        final byte[][] ramps = new byte[COUNT][4];
        final int[][] values = new int[COUNT][4];

        final int[] lumas = new int[COUNT], cws = new int[COUNT], cms = new int[COUNT];
        final int yLim = 63, cwLim = 31, cmLim = 31, shift1 = 6, shift2 = 11;
        int color, r, g, b, cw, cm, t;
        for (int i = 1; i < COUNT; i++) {
            color = palette[i];
            if((color & 0x80) == 0)
            {
                lumas[i] = -0x70000000; // very very negative, blocks transparent colors from mixing into opaque ones
                continue;
            }
            r = (color >>> 24);
            g = (color >>> 16 & 0xFF);
            b = (color >>> 8 & 0xFF);
            cw = r - b;
            cm = g - b;
            paletteMapping[
                    reverse[i] =
                            (lumas[i] = luma(r, g, b) >>> 11)
                                    | (cws[i] = cw + 255 >>> 4) << shift1
                                    | (cms[i] = cm + 255 >>> 4) << shift2] = (byte) i;
        }

        for (int icw = 0; icw <= cmLim; icw++) {
            for (int icm = 0; icm <= cwLim; icm++) {
                for (int iy = 0; iy <= yLim; iy++) {
                    final int c2 = iy | icw << shift1 | icm << shift2;
                    if (paletteMapping[c2] == 0) {
                        int dist = 0x7FFFFFFF;
                        for (int i = 1; i < COUNT; i++) {
                            if (dist > (dist = Math.min(dist, difference(lumas[i], cws[i], cms[i], iy, icw, icm))))
                                paletteMapping[c2] = (byte) i;
                        }
                    }
                }
            }
        }

        float adj, cwf, cmf;
        int idx2;
        for (int i = 1; i < COUNT; i++) {
            int rev = reverse[i], y = rev & yLim, match = i,
                    yBright = y * 5, yDim = y * 3, yDark = y << 1;
            float luma, warm, mild;

            cwf = ((cw = cws[i]) - 15.5f) * 0x1.08421p-4f;
            cmf = ((cm = cms[i]) - 15.5f) * 0x1.08421p-4f;

            //values[i][0] = values[i][1] = values[i][3] = 
            values[i][2] = palette[i];

            warm = cwf - 0.175f * heat;
            mild = cmf;
            luma = yDim * 0x1p-8f;
            g = (int)((luma + mild * 0.5f - warm * 0.375f) * 255);
            b = (int)((luma - warm * 0.375f - mild * 0.5f) * 255);
            r = (int)((luma + warm * 0.625f - mild * 0.5f) * 255);
            values[i][1] =
                    MathUtils.clamp(r, 0, 255) << 24 |
                            MathUtils.clamp(g, 0, 255) << 16 |
                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
            warm = cwf + 0.375f * heat;
            mild = cmf;
            luma = yBright * 0x1p-8f;
            g = (int)((luma + mild * 0.5f - warm * 0.375f) * 255);
            b = (int)((luma - warm * 0.375f - mild * 0.5f) * 255);
            r = (int)((luma + warm * 0.625f - mild * 0.5f) * 255);
            values[i][3] =
                    MathUtils.clamp(r, 0, 255) << 24 |
                            MathUtils.clamp(g, 0, 255) << 16 |
                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
            warm = cwf - 0.375f * heat;
            mild = cmf;
            luma = yDark * 0x1p-8f;
            g = (int)((luma + mild * 0.5f - warm * 0.375f) * 255);
            b = (int)((luma - warm * 0.375f - mild * 0.5f) * 255);
            r = (int)((luma + warm * 0.625f - mild * 0.5f) * 255);
            values[i][0] =
                    MathUtils.clamp(r, 0, 255) << 24 |
                            MathUtils.clamp(g, 0, 255) << 16 |
                            MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
            
            heat = (heat + 2f) * 0x1p-9f;
            ramps[i][2] = (byte)i;
            ramps[i][3] = grays[4];//15;  //0xFFFFFFFF, white
            ramps[i][1] = grays[0];//0x010101FF, black
            ramps[i][0] = grays[0];//0x010101FF, black
            for (int yy = y + 2, rr = rev + 2; yy <= yLim; yy++, rr++) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], cws[idx2], cms[idx2], y, cw, cm) > THRESHOLD) {
                    ramps[i][3] = paletteMapping[rr];
                    break;
                }
                adj = 1f + ((yLim + 1 >>> 1) - yy) * 0x1p-10f;
                cwf = MathUtils.clamp(cwf * adj + heat, -1f, 1f);
                cmf = MathUtils.clamp(cmf * adj, -1f, 1f);

                rr = yy
                        | (cw = (int) ((cwf * 0.5f + 0.5f) * cwLim)) << shift1
                        | (cm = (int) ((cmf * 0.5f + 0.5f) * cmLim)) << shift2;
            }
            cwf = (cw - 15.5f) * 0x1.08421p-4f;
            cmf = (cm - 15.5f) * 0x1.08421p-4f;
            for (int yy = y - 2, rr = rev - 2; yy > 0; rr--) {
                if ((idx2 = paletteMapping[rr] & 255) != i && difference(lumas[idx2], cws[idx2], cms[idx2], y, cw, cm) > THRESHOLD) {
                    ramps[i][1] = paletteMapping[rr];
                    rev = rr;
                    y = yy;
                    match = paletteMapping[rr] & 255;
                    break;
                }
                adj = 1f + (yy - (yLim + 1 >>> 1)) * 0x1p-10f;
                cwf = MathUtils.clamp(cwf * adj - heat, -1f, 1f);
                cmf = MathUtils.clamp(cmf * adj, -1f, 1f);

                rr = yy
                        | (cw = (int) ((cwf * 0.5f + 0.5f) * cwLim)) << shift1
                        | (cm = (int) ((cmf * 0.5f + 0.5f) * cmLim)) << shift2;

                if (--yy == 0) {
                    match = -1;
                }
            }
            if (match >= 0) {
                cwf = ((cw = cws[match]) - 15.5f) * 0x1.08421p-4f;
                cmf = ((cm = cms[match]) - 15.5f) * 0x1.08421p-4f;
                for (int yy = y - 3, rr = rev - 3; yy > 0; yy--, rr--) {
                    if ((idx2 = paletteMapping[rr] & 255) != match && difference(lumas[idx2], cws[idx2], cms[idx2], y, cw, cm) > THRESHOLD) {
                        ramps[i][0] = paletteMapping[rr];
                        break;
                    }
                    adj = 1f + (yy - (yLim + 1 >>> 1)) * 0x1p-10f;
                    cwf = MathUtils.clamp(cwf * adj - heat, -1f, 1f);
                    cmf = MathUtils.clamp(cmf * adj, -1f, 1f);

                    rr = yy
                            | (cw = (int) ((cwf * 0.5f + 0.5f) * cwLim)) << shift1
                            | (cm = (int) ((cmf * 0.5f + 0.5f) * cmLim)) << shift2;
                }
            }
        }


        return new Colorizer(reducer) {

            @Override
            public byte[] mainColors() {
                return primary;
            }

            /**
             * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
             */
            @Override
            public byte[] grayscale() {
                return grays;
            }

            @Override
            public byte brighten(byte voxel) {
                return ramps[(voxel & 0xFF) % COUNT][3];
            }

            @Override
            public byte darken(byte voxel) {
                return ramps[(voxel & 0xFF) % COUNT][1];
            }

            @Override
            public int dimmer(int brightness, byte voxel) {
                return values[voxel & 0xFF][
                        brightness <= 0
                                ? 0
                                : brightness >= 3
                                ? 3
                                : brightness
                        ];
            }

            @Override
            public int getShadeBit() {
                return 0;
            }

            @Override
            public int getWaveBit() {
                return 0;
            }
        };
    }
    public static final Colorizer RinsedColorizer = new Colorizer(new PaletteReducer(Coloring.RINSED, (
            "\027\027\027\02777·¿¿¿¿¿¾¾ÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ\027\027\027777·¿¿¿¿¿¾¾¾½ÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ\027\027\02777··¿¿¿¿¿¾¾¾½ÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ7777¯¿¿¿¿¾¾¾¾½½ÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ"+
                    "¯¯¿¿¾¾¾¾½½½½ÝÝÝÝÜÜÜÜÜÜÜÛÛÛ¯¯§§¾¾¾¾½½½½ÝÝÝÝÜÜÜÜÜÜÜÛÛÛ¯¯§®¾¾¾¾½½½½½¼¼¼¼ÜÜÜÜÜÜÛÛÛ§§®¦¾¾½½½½½¼¼¼¼¼ÜÜÜÜÜÜÛÛÛ"+
                    "®®¦¦¾­½½½½½¼¼¼¼¼¼»ÜÜÜÛÛÛÛ¦¦¦¦¦­­­½½½¼¼¼¼¼¼»»»»»»ÛÛÛ¦¦¦¥¥­­½½½¼¼¼¼¼¼»»»»»»ÛÛÛ¥¥¥¥¥¥­½½«¼¼¼¼¼»»»»»»»ºÛÛ"+
                    "¥¥¥¥¥¥¤¤««¼¼¼¼¼»»»»»»»ººÛ¥¥¥¤¤¤¤¤«««¼¼¼»»»»»»»»ººº¤¤¤¤¤¤¤¤«««¼¼¼»»»»»»»ºººº¤¤¤¤¤¤¤««««£¼¼»»»»»»»ºººº"+
                    "¤£££££££££££»»»»»»»»ºººº£££££££££££££»»»»»»»ººººº£££££££££££££¢¢»»»»»»ººººº£££££££££¢¢¢¢¢¢¢»»»©©©ºººº"+
                    "££¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¡©©©©ºººº¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¡¡¡©©©©ººº¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¡¡¡¡¡©©©   ¢¢¢¢¢¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡©    "+
                    "¢¢¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡     ¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡      ¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡       ¢¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡         "+
                    "¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡            ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡              ¡¡¡¡¡¡¡¡¡¡¡¡¡                ¡¡¡¡¡¡¡¡¡¡¡¡                  "+
                    "\027\027\027\0277çç¿¿¿¿¿ÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ\027\027\027777·¿¿¿¿¿¾ÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ\027\027\02777··¿¿¿¿¾¾¾¾½ÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ77777··¿¿¿¿¾¾¾¾½½ÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ"+
                    "\1777¯¯¿¿¾¾¾¾½½½ÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ\177¯¯§§¾¾¾¾½½½½ÝÝÝÝÜÜÜÜÜÜÜÛÛÛ\177¯¯§®¾¾¾¾½½½½¼¼¼¼ÜÜÜÜÜÜÜÛÛÛ§§®¾¾¾½½½½½¼¼¼¼¼ÜÜÜÜÜÛÛÛÛ"+
                    "®®¦¦¾­½½½½½¼¼¼¼¼¼ÜÜÜÜÛÛÛÛ¦¦¦¦­­½½½½¼¼¼¼¼¼»»»»»ÛÛÛÛ¦¦¦¥­­­½½½¼¼¼¼¼¼»»»»»»ÛÛÛ¥¥¥¥¥­­¬½«¼¼¼¼¼»»»»»»»ºÛÛ"+
                    "¥¥¥¥¥¥¤¤««¼¼¼¼¼»»»»»»»ººÛ¥¥¤¤¤¤¤««¼¼¼¼»»»»»»»ºººº¤¤¤¤¤¤¤«««¼¼¼»»»»»»»ºººº¤¤¤¤¤¤««««¼¼»»»»»»»»ºººº"+
                    "¤£££££££££££»»»»»»»ººººº£££££££££££££»»»»»»»ººººº££££££££££££¢¢»»»»»©ººººº££££££££¢¢¢¢¢¢¢»»»©©ººººº"+
                    "£¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢©©©©©ºººº¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¡¡©©©©©ºº¹¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¡¡¡¡©©©©   ¢¢¢¢¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡©©    "+
                    "¢¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡     ¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡      ¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡       ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡         "+
                    "¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡            ¡¡¡¡¡¡¡¡¡¡¡¡¡¡              ¡¡¡¡¡¡¡¡¡¡¡¡                ¡¡¡¡¡¡¡¡¡¡                  "+
                    "\027\027\02777çç·¿¿¿ÏÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ\027\027\02777ç··¿¿¿ÏÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ\027\027777···¿¿¿Ï¾ÞÞÍÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ77777···¿¿Ï¾¾¾ÍÍÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ"+
                    "\177777\026\026··¿¿Ç¾¾¾Í½½ÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ\177\177¯¯§§¾¾¾¾½½½½ÝÝÝÝÜÜÜÜÜÜÜÛÛÛ\177\177¯¯§®¾¾¾¾½½½½ÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177®®¾¾¾½½½½½¼¼¼¼¼ÜÜÜÜÜÛÛÛÛ"+
                    "®®®¦¾­½½½½½¼¼¼¼¼ÜÜÜÜÜÛÛÛÛ¦¦¦¦­­½½½½¼¼¼¼¼¼»»»»ÜÛÛÛÛ~¦¦¦­­­½½½¼¼¼¼¼¼»»»»»ÛÛÛÛ~¥¥¥¥­¬¬½½¼¼¼¼¼»»»»»»»ÛÛÛ"+
                    "¥¥¥¥¥¬¬««¼¼¼¼¼»»»»»»ºººÛ¥¤¤¤¤¤««¼¼¼¼»»»»»»»ºººº¤¤¤¤¤¤¤«««¼¼¼»»»»»»»ºººº¤¤¤¤¤¤««««¼¼»»»»»»»ººººº"+
                    "¤£££££££«££ª»»»»»»»ººººº£££££££££££ªª»»»»»»ººººº££££££££££££ªª»»»»»ºººººº£££££££¢¢¢¢¢¢»»»»©©ººººº"+
                    "¢¢¢¢¢¢¢¢¢¢¢¢¢¢»©©©©©ºººº¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¡©©©©©©ºº¹¢¢¢¢¢¢¢¢¢¢¢¢¢¢¢¡¡¡©©©©©º¹¹¢¢¢¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡©©©©   "+
                    "¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡©     ¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡      ¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡       ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡         "+
                    "¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡            ¡¡¡¡¡¡¡¡¡¡¡¡¡              ¡¡¡¡¡¡¡¡¡¡¡                ¡¡¡¡¡¡¡¡                   "+
                    "\027\027777ççç¿¿ÏÏÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ\0277777çç·¿¿ÏÏÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ77777ç··¿ÏÏÏÞÞÞÍÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ77777···'ÏÏÇÎÞÍÍÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ"+
                    "\177\17777\026\026··'ÏÇ¾ÎÎÍÍÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ\177\177\177\026\026\026··ÇÇ¾ÎÍÍ½½ÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177®¶¾¾Í½½½½ÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177®®¾¾¾Í½½½½¼¼¼¼ÜÜÜÜÜÜÛÛÛÛ"+
                    "~®®¦¾µ½½½½½¼¼¼¼¼ÜÜÜÜÜÛÛÛÛ~®¦¦¦­­µ½½½¼¼¼¼¼¼»»ÜÜÜÛÛÛÛ~~¦¦¦­­µ½½½¼¼¼¼¼¼»»»»»ÛÛÛÛ~~¥¥­­¬¬¬¼¼¼¼¼¼»»»»»»ºÛÛÛ"+
                    "¥­¬¬¬¬«¼¼¼¼¼»»»»»»ºººÛ¤¬¬¬¬««¼¼¼¼»»»»»»»ºººº¤¤¤¤¤¤«««¼¼¼»»»»»»ººººº¤¤¤¤«««««¼¼»»»»»»»ººººº"+
                    "£££££££«££ªª»»»»»»ººººº££££££££££ªªª»»»»»ºººººº££££££££££ªªª»»»»»ºººººº£££££££¢¢¢¢ªªª»»©©©ººººº"+
                    "¢¢¢¢¢¢¢¢¢¢¢¢ª»©©©©©ºººº¢¢¢¢¢¢¢¢¢¢¢¢¢¢©©©©©©ºº¹¹¢¢¢¢¢¢¢¢¢¢¢¢¢¡¡¡©©©©©©¹¹¹¢¢¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡©©©©©¹¹¹"+
                    "¢¢¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡©©©    ¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡©     ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡       ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡         "+
                    "¡¡¡¡¡¡¡¡¡¡¡¡¡¡            ¡¡¡¡¡¡¡¡¡¡¡¡              ¡¡¡¡¡¡¡¡¡¡                ¡¡¡¡¡¡¡                   "+
                    "77777çççïÏÏÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛ77777çç·'ÏÏÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ77777çç·'ÏÏÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ77777\026··'ÏÏÇÞÞÞÍÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ"+
                    "\177\1777\026\026\026··'ÏÇÎÎÎÍÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177\026\026\026·'ÇÇÎÎÍÍÍ½ÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177\177¶¶ÎÎÍÍÍ½ÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177¶¶ÎÎÍÍ½½ÌÝÝ¼¼ÜÜÜÜÜÜÛÛÛÛ"+
                    "~~Îµµ½½½Ì¼¼¼¼ÜÜÜÜÜÜÛÛÛÛ~~µµµ½½½¼¼¼¼¼¼ÜÜÜÜÜÛÛÛÛ~~~­µµ¬½Ì¼¼¼¼¼»»»»»»ÛÛÛÛ~~~­µ¬¬¬´¼¼¼¼¼»»»»»»ºÛÛÛ"+
                    "¬¬¬¬¬´¼¼¼¼»»»»»»»ººÛÛ¬¬¬¬¬«¼¼¼¼»»»»»»ººººº¤¤¬¬¬««¼¼¼¼»»»»»»ººººº¤¤«««««¼¼»»»»»»»ººººº"+
                    "££££««£ªªª»»»»»ºººººº££££££££ªªª»»»»»ºººººº££££££ªªªªª»»»©ºººººº££££¢¢ªªªªª»»©©©ººººº"+
                    "¢¢¢¢¢¢¢¢¢ªªª©©©©©ºººº¹¢¢¢¢¢¢¢¢¢¢¢¢¢ª©©©©©©ºº¹¹¢¢¢¢¢¢¢¢¢¢¢¢¡©©©©©©©©¹¹¹¢¢¢¢¢¢¢¢¢¢¡¡¡¡¡¡©©©©©©¹¹¹"+
                    "¢¢¢¢¢¢¡¡¡¡¡¡¡¡¡¡¡©©©©   ±¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡©©    ±¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡       ¡¡¡¡¡¡¡¡¡¡¡¡¡¡¡         "+
                    "¡¡¡¡¡¡¡¡¡¡¡¡            ¡¡¡¡¡¡¡¡¡¡              ¡¡¡¡¡¡¡¡¡                ¡¡¡¡¡¡                   "+
                    "77ççççççïÏÏÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ77ççççççßÏÏÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ777ççççß'ÏÏÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ\177\1777ççç·ß'ÏÏÞÞÞÞÍÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ"+
                    "\177\177\177\0266\026Wß'ÏÇÎÞÞÍÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177\026\026\026WßÇÇÎÎÍÍÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\17766¶¶ÎÎÍÍÍÌÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177¶ÎÎÍÍÍÌÌÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "~~~ÎµÍÍ½ÌÌÌ¼¼¼ÜÜÜÜÜÜÛÛÛÛ~~~µµµ½½ÌÌ¼¼¼¼ËÜÜÜÜÜÛÛÛÛ~~~~µµµ¬¬ÌÌ¼¼¼¼»»»»ÜÜÛÛÛÛ~~~~µ¬¬¬´¼¼¼¼¼»»»»»»ÛÛÛÛ"+
                    "¬¬¬¬´¼¼¼¼»»»»»»ºººÛÛ¬¬¬¬¬´´¼¼¼»»»»»»ººººº¬¬¬«´´¼¼»»»»»»»ººººº«««´´ªªª»»»»»ºººººº"+
                    "£«««ªªªª»»»»»ºººººº££££ªªªª»»»»»ºººººº££ªªªªªª»»»ººººººº£ªªªªªªª»©©©ºººººº"+
                    "¢¢¢¢¢¢ªªªª©©©©©ºººº¹¢¢¢¢¢¢¢¢¢¢¢ªª©©©©©©º¹¹¹¢¢¢¢¢¢¢¢¢¢¢¢©©©©©©©©¹¹¹¹¢¢¢¢¢¢¢¢¢¡¡¡¡¡©©©©©©¹¹¹¹"+
                    "¡¡¡¡¡¡¡¡¡©©©©©¹¹¹±¡¡¡¡¡¡¡¡¡¡¡¡©©©©  ±±¡¡¡¡¡¡¡¡¡¡¡¡¡¡©©    ±¡¡¡¡¡¡¡¡¡¡¡¡¡        ±"+
                    "¡¡¡¡¡¡¡¡¡¡¡           ±¡¡¡¡¡¡¡¡¡              ¡¡¡¡¡¡¡¡                ¡¡¡¡¡                   "+
                    "ççççççççïÏÏÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛçççççççïïÏÏÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ\177ççççççß'ÏÏÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛ\177\177ççççWß'ÏÏÞÞÞÞÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ"+
                    "\177\177\17766WWß'ÏÏÞÞÞÍÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177666Wß'ÏÇ×ÎÍÍÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177666¶××ÎÍÍÍÌÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177&×ÎÍÍÍÌÌÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "~~~~ÆµÍÍÌÌÌÌ¼¼ËÜÜÜÜÜÜÛÛÛÛ~~~~µµµÍÌÌÌÌ¼¼ËËÜÜÜÜÜÛÛÛÛ~~~~~µµ¬ÌÌÌ¼¼¼ËË»ÊÜÜÜÛÛÛÛ~~~~µ¬¬¬´´¼¼¼Ë»»»»ÊÊÛÛÛÛ"+
                    "}}¬¬´´´¼¼¼Ë»»»»»ºººÛÛ}¬¬¬´´´¼¼¼»»»»»»ººººÛ¬¬´´´¼¼»»»»»»ºººººº´´´´ªªª»»»»»ºººººº"+
                    "´´ªªªª»»»»»ººººººªªªªª³»»»ºººººººªªªªªª»»©ºººººººªªªªªªª³©©©ººººº¹"+
                    "¢¢¢ªªªªªª©©©©©ººº¹¹¢¢¢¢¢¢¢ªªªª©©©©©©º¹¹¹¢¢¢¢¢¢¢¢¢¢¢©©©©©©©©¹¹¹¹¡¡¡©©©©©©©©¹¹¹¹"+
                    "¡¡¡¡¡¡©©©©©©¹¹¹±¡¡¡¡¡¡¡¡©©©©©¹¹±±¡¡¡¡¡¡¡¡¡¡¡©©©   ±±¡¡¡¡¡¡¡¡¡¡¡       ±±"+
                    "¡¡¡¡¡¡¡¡¡          ±±¡¡¡¡¡¡¡¡             ±¡¡¡¡¡¡               ±                  "+
                    "ççççççïïï?ÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛççççïïïïïÏÏÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛçççïïïïïïÏÏÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177ïïïïïß'ÏÏÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ"+
                    "\177\1776////ßæÏÏÞÞÞÞÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\1776////ßæÏ××ÞÞÍÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ\177\177\177///555&&×ÎÍÍÍÌÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛ\177\177\17755555&×ÆÍÍÍÌÌÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "~~~g55ÆÆÍÍÌÌÌÌÌËËÜÜÜÜÜÜÛÛÛÛ~~~~~ÆÆµÍÌÌÌÌËËËËÜÜÜÜÜÛÛÛÛ~~~~~µµÖÌÌÌÌËËËËÊÊÊÜÜÛÛÛÛ~~~~~¬¬´´Ì¼ËËËËÊÊÊÊÊÛÛÛÛ"+
                    "}}}¬¬´´´¼ËËË»»ÊÊÊººÛÛÛ}}¬´´´´ËË»»»»ÊÊººººÚ¬´´´´ËË»»»»»ºººººº´´´´ªª³»»»»»ºººººº"+
                    "´´ªªªª³»»»ºººººººªªªªª³³»»ºººººººªªªªª³³»©ºººººººªªªªªª³³©©©ººººº¹"+
                    "ªªªªªªª©©©©©ººº¹¹¢¢¢ªªªªªª©©©©©º¹¹¹¹ª©©©©©©©©¹¹¹¹©©©©©©©©¹¹¹±"+
                    "¡¡¡©©©©©©©¹¹±±¡¡¡¡¡©©©©©©¹¹±±¡¡¡¡¡¡¡©©©©²¹±±±¡¡¡¡¡¡¡¡      ±±±"+
                    "¡¡¡¡¡¡¡         ±±±¡¡¡¡¡¡            ±±              ±±               ±±"+
                    "ÿÿÿÿïïïïï??ÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÿÿÿïïïïïïæÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛÿÿïïïïïïïæÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛÿ÷÷÷÷÷÷ïææÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ"+
                    "o\037\037\037\037÷÷æææÞÞÞÞÞÍÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛo\037\037\037\037ggæææ&ÞÞÞÍÍÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛoggggg55VV&×ÆÍÍÍÌÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛogggg5555&&&ÆÍÍÖÌÌÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "~~gg555\025&ÆÆÆÍÖÌÌÌËËËÜÜÜÜÜÜÛÛÛÛ~~~~~444\025\025\025ÆÆÆÖÖÌÌÌËËËËÜÜÜÜÜÛÛÛÛ~~~~~%%ÖÖÌÌÌËËËËÊÊÊÜÜÛÛÛÛ}}}~~ÖÖÌÌËËËËËÊÊÊÊÊÛÛÛÛ"+
                    "}}}}´´´´´ËËËÊÊÊÊÊÊºÛÛÚ}}}}´´´´´ËËËÊÊÊÊÊºººÚÚ´´´´´ËË³»ÊÊÊºººººÚ´´´´ªª³³»»ÊÊºººººº"+
                    "´ªªª³³³»»ºººººººªªª³³³»»ºººººººªªªªª³³³©ºººººººªªªªª³³©©ººººº¹¹"+
                    "ªªªªªª³³©©©ººº¹¹¹ªªªªª³©©©©©²¹¹¹¹©©©©©©©²¹¹¹¹©©©©©©©²¹¹¹±"+
                    "©©©©©©©²¹¹±±©©©©©©©²¹±±±¡¡¡©©©©©²±±±±¡¡¡¡¡¡     ±±±±"+
                    "¡¡¡¡        ±±±±         ±±±±          ±±±±          ±±±±"+
                    "ÿÿÿÿ???????ÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛÿÿÿÿÿ?????ÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛÿÿÿÿÿïïïææÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛooÿ\037\037÷÷æææÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛ"+
                    "ooo\037\037\037\037ææææÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛooo\037\037ggVææ&ÞÞÞÍÍÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛoooggggVVV&&ÆGÍÖÌÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛooogggg4V.&&GGÍÖÌÌÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "~~~gg4444\025\025ÆGGÖÖÌÌÌËËËÜÜÜÜÜÜÛÛÛÛ~~~~4444\025\025\025ÆG%ÖÖÌÌÌËËËËÜÜÜÜÜÛÛÛÛ~~~~~\025%%%ÖÖÌÌÌËËËËÊÊÊÊÛÛÛÛÛ}}}}}ÖÖÌÌËËËËËÊÊÊÊÊÛÛÛÛ"+
                    "}}}}}Å´´´ËËËËÊÊÊÊÊÊºÚÚÚ}}}}}´´´´ËËËËÊÊÊÊÊºººÚÚ}´´´´ËË³ÊÊÊÊºººººÚ´´ªª³³ÊÊÊÊºººººº"+
                    "|ªªª³³³ÊÊººººººº||ªªª³³³³ººººººººªªªª³³³³ººººººº¹ªªªªª³³©©ººººº¹¹"+
                    "ªªªªªª³³©©©ºº¹¹¹¹ªªª³³©©©©²²¹¹¹¹©©©©©²²¹¹¹¹©©©©©²²¹¹¹±"+
                    "©©©©©²²¹¹±±©©©©©²²¹±±±©©©²²²²±±±±¡¡©²²²²²±±±±"+
                    "    ±±±±±±     ±±±±±±      ±±±±±±      ±±±±±±"+
                    "ÿÿÿÿ???????ÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛÿÿÿÿÿ?????ÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛÿÿÿÿÿ????æÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛoooÿÿ\037\037æææÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "ooooo\037æææææÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛoooooggVVæ&ÞÞÞÞÍÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛoooogg....öååGGÖÌÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛoooog....öööGGGÖÌÌÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "oooo44444\025ööGGÖÖÌÌÌËËËÜÜÜÜÜÜÛÛÛÛ~~~~4444\025\025\025U%%ÖÖÖÌÌËËËÜÜÜÜÜÛÛÛÛÛ}}~~444\025\025_%%ÖÖÖÌÕËËËËÊÊÊÊÛÛÛÛÛ}}}}}___%ÅÅÅÌÕËËËËÊÊÊÊÊÛÛÛÚ"+
                    "}}}}}ÅÅ´´ËËËËÊÊÊÊÊÉÚÚÚÚ}}}}}Å´´´ËËËËÊÊÊÊÊÉººÚÚ|||||´´ËËËËÊÊÊÊÉººººÚ|||||´ª³³³ÊÊÊÉºººººº"+
                    "|||||ª³³³³ÊÊÉºººººº||ªª³³³³Éºººººººªªª³³³³ººººººº¹ªªª³³³©©ºººº¹¹¹"+
                    "ªªª³³³©©²²º¹¹¹¹³³³©©©²²²¹¹¹¹©©©©²²²¹¹¹¹©©©©²²²¹¹¹±"+
                    "©©©©²²²¹¹±±©©©²²²²¹±±±©²²²²²²±±±±²²²²²²±±±±"+
                    " ±±±±±±±  ±±±±±±±   ±±±±±±±   ±±±±±±±"+
                    "ÿÿÿÿ???????ÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛwÿÿÿÿ?????ÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛwwÿÿÿ???>>ÞÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛwwooOOOO>>ÞÞÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "ooooOOOO>îååÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛoooooOO\036\036åååååGÖÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛoooo\036\036\036\036\036öåååGGÖÖÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛoooo\036\036\036\036ööööGGGÖÖÌÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "nnnnfffföUUUGGÖÖÖÌÌÕËËÜÜÜÜÜÛÛÛÛÛnnnnffffUUUU%%ÖÖÖÌÕÕËËÜÜÜÜÜÛÛÛÛÛ}}}}ffffUUU_%%ÅÅÅÌÕÕËËËÊÊÊÊÛÛÛÛÛ}}}}}___\024\024ÅÅÅÅÕËËËÊÊÊÊÊÊÛÚÚÚ"+
                    "}}}}}__\024\024\024ÅÅÅ´ÕËËËÊÊÊÊÊÉÚÚÚÚ}}}||\024\024ÅÅ´´ÕËËËÊÊÊÊÉÉºÚÚÚ|||||´ÕËËÊÊÊÊÊÉÉººÚÚ||||||Ä³³³ÊÊÊÉÉººººÙ"+
                    "|||||ª³³³³ÊÉÉºººººº||ª³³³³ÊÉÉººººº¹ª³³³³Éººººº¹¹ª³³³³²ºººº¹¹¹"+
                    "³³³³©²²²²¹¹¹¹³³©©²²²²¹¹¹¹©©²²²²¹¹¹¹©©²²²²¹¹¹±"+
                    "©©²²²¹¹¹±±²²²²²¹¹±±±²²²²²¹±±±±²²²²±±±±±"+
                    "±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±±"+
                    "wwww?????>>ÞÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛwwwww?>>>>>îÞÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛwwwwwO>>>>îîÞÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwwwOO>>>îîååÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "wwwwOOO>îîåååååÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwoooOOO>îååååååÖÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛnnnn\036\036\036\036\036åååååGÖÖÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛnnnnn\036\036\036\036ååååGGÖÖÌÝÝÝÜÜÜÜÜÜÛÛÛÛÛ"+
                    "nnnnnffffUUUG%%ÖÖÖÕÕÕËÜÜÜÜÜÛÛÛÛÛnnnnnffffUUU%%%ÅÅÅÕÕÕËÜÜÜÜÜÛÛÛÛÛ}nnnfffffU__%%ÅÅÅÅÕÕËËËÊÊÊÊÛÛÛÚÚ}}}}____\024\024ÅÅÅÅÕÕËËÊÊÊÊÊÉÚÚÚÚ"+
                    "}}}}__\024\024\024$ÅÅÕÕÕËËÊÊÊÊÉÉÚÚÚÚ}||||_\024\024\024$ÅÅÄÕÕËÔÊÊÊÊÉÉÉÚÚÚ|||||\023\023\023ÄÄÕËÔÊÊÊÉÉÉºÙÙÙ||||||ÄÄ³ÔÊÊÉÉÉÉººÙÙ"+
                    "||||Ä³³³ÊÊÉÉÉººººÙ||³³³³ÉÉÉººººº¹³³³ÈÉÉººº¹¹¹³³³ÈÈ²ºº¹¹¹¹"+
                    "³³³È²²²¹¹¹¹¹³³³©²²²¹¹¹¹¹©©²²²¹¹¹¹¹©²²²²¹¹¹¹±"+
                    "²²²²²¹¹¹±±²²²²²¹¹±±±²²²²²¹±±±±²²²±±±±±"+
                    "±±±±±±±±±±±±±±±±±±±±±±°°°°°°±±"+
                    "wwwwwþþþ>>>îîÞÞÝÝÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛwwwwþþþ>>>îîîÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwwwþþþ>>îîîîÞÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwwwþþ>>îîîîåååÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "wwwwþþ>îîîîåååååÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwnnOO>îîîåååååÖÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛnnnnn\036\036îîååååååÖÖÝÝÝÝÜÜÜÜÜÜÛÛÛÛÛnnnnnn\036\036-åååååGÖÖÖÝÕÕÜÜÜÜÜÜÛÛÛÛÛ"+
                    "nnnnnnff---õõ%%ÖÖÖÕÕÕÕÜÜÜÜÜÛÛÛÛÛnnnnnfff--õõõ%FÅÅÅÕÕÕÕÜÜÜÜÜÛÛÛÛÛnnnnmfffTTTTõõFFÅÅÕÕÕËÔÊÊÊÊÛÚÚÚÚ}}}mmmf3333TT$$FFÕÕÕÕËÔÊÊÊÊÉÚÚÚÚ"+
                    "}}|||3333\024$$FFÕÕÕÕÔÔÊÊÊÉÉÚÚÚÚ|||||33\024\024$$\023FÄÄÕÕÔÔÊÊÉÉÉÉÙÙÙ||||||S\023\023\023\023ÄÄÄÔÔÔÊÊÉÉÉÉÙÙÙ||||||S\023ÄÄÄÔÔÔÊÉÉÉÉººÙÙ"+
                    "||||ÄÄ³ÔÔÉÉÉÉÉººÙÙ||{³ÃÃÈÉÉÉººº¹¹{{{³ÃÈÈÈÉººº¹¹¹³ÃÈÈÈ²²º¹¹¹¹"+
                    "ÃÈÈ²²²¹¹¹¹¹ÈÈ²²²¹¹¹¹¹²²²²¹¹¹¹¸²²²²¹¹¹¹¸"+
                    "²²²²¹¹¹¸¸²²²²¹¹¸±±²²²²¹±±±±²²±±±±±"+
                    "±±±±±±±±±±±±±±°°±±±±±°°°°°°°°"+
                    "wwwwþþþþþþîîîîÞÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwwþþþþþþîîîîîîÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwwþþþþþþîîîîîåÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwwþþþþþîîîîîåååÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "wwþþþþþþîîîîååååÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwnnþþþþNNNNåååååÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛÛnnnnnþNNNNN\035åååÖäÝÝÝÝÜÜÜÜÜÜÛÛÛÛÛnnnnnnNN\035\035\035\035\035\035õFÖääÕÕÜÜÜÜÜÜÛÛÛÛÛ"+
                    "nnnnmm----\035\035õõõFFäÕÕÕÕÜÜÜÜÜÛÛÛÛÛnnnmmmm----õõõFFFFÕÕÕÕÜÜÜÜÜÛÛÚÚÚnmmmmmm---õõõõFFFFÕÕÕÕÔÔÊÊÊÚÚÚÚÚmmmmmmmee333$$$FFFÕÕÕÔÔÔÊÊÉÉÚÚÚÚ"+
                    "||||mmeeee33$$$FFÄÕÕÕÔÔÔÊÉÉÉÚÚÚÚ||||||eeeS$$$\023FÄÄÄÕÔÔÔÊÉÉÉÙÙÙÙ||||||SS\023\023\023\023\023ÄÄÄÔÔÔÔÉÉÉÉÓÙÙÙ||||||SSS\023\023\023\023\023ÄÄÄÔÔÔÔÉÉÉÉÓÙÙÙ"+
                    "||||{ÄÄÄÃÃÔÉÉÉÉÉÓÒÙÙ|{{{{{ÃÃÃÈÉÉÉÓÓÒ¹Ø{{{{{{ÃÃÈÈÈÉÉº¹¹¹¹{{ÃÃÈÈÈÈ²¹¹¹¹¹"+
                    "ÃÈÈÈ²²¹¹¹¹¹ÈÈ²²²¹¹¹¹¹²²²²¹¹¹¹¸²²²²¹¹¹¸¸"+
                    "²²²¹¹¸¸¸²²²¹¸¸¸¸²²²¹¸¸¸±¸¸±±±"+
                    "±±±±±±±±±±±±±°°°°°°°°°°°°°°°"+
                    "wwþþþþþþþþþþîîîÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwþþþþþþþþþîîîîÝÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwwþþþþþþþþNNNNNíÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwþþþþþþþþNNNNNNíÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛ"+
                    "wþþþþþþNNNNNNNííÝÝÝÝÝÜÜÜÜÜÜÛÛÛÛÛwþþþþþNNNNNN====äÝÝÝÝÜÜÜÜÜÜÛÛÛÛÛnnnvvNNNNNN====ääääÝÜÜÜÜÜÜÜÛÛÛÛÛnnnmmNNN======\035FäääÕÕÜÜÜÜÜÜÛÛÛÛÛ"+
                    "nmmmmmm==\035\035\035\035\035FFäääÕÕÕÜÜÜÜÜÛÛÛÛÚmmmmmmm\035\035\035\035\035õFFFFäÕÕÕÕÔÜÜÜÜÚÚÚÚÚmmmmmmmmeeeõõFFFFFÕÕÕÕÔÔÊÊÉÚÚÚÚÚmmmmmmmeeeee$$FFFFÕÕÕÔÔÔÊÊÉÚÚÚÚÚ"+
                    "|mmmmmeeeeeSS$$FFÄÄÄÕÔÔÔÊÉÉÉÙÙÙÙ||||||eeeeSSS^$\023FÄÄÄÔÔÔÔÉÉÉÉÙÙÙÙ||||||eeSSS^^\023\023\023ÄÄÄÔÔÔÔÉÉÉÓÓÙÙÙ||||||{{SSSS^\023\023\023\023ÄÄÄÔÔÔÔÉÉÉÓÓÙÙÙ"+
                    "|||{{{{{^222\023##ÄÄÃÃÃÈÉÉÉÓÓÒØØ{{{{{{{{{222ÃÃÃÈÈÉÓÓÓÒÒØ{{{{{{{{{ÃÃÃÈÈÈÓÓÒÒÒ¹{{{ÃÃÈÈÈÈÓÓ¹¹¹¹"+
                    "\022ÃÈÈÈÈ²¹¹¹¹¹ÈÈÈ²²¹¹¹¹¸È²²²¹¹¹¸¸²²²¹¹¹¸¸"+
                    "²²²¹¹¸¸¸²²¹¸¸¸¸¸¸¸¸¸¸¸¸¸¸"+
                    "¸¸¸¸±±°°°°°°°°°°°°°°°°°°°°°°"+
                    "wþþþþþþþþþþþííííÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛwþþþþþþþþþNNííííÝÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛþþþþþþþþþNNNíííííÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛþþþþvvvvNNNííííííÝÝÝÝÜÜÜÜÜÜÛÛÛÛÛ"+
                    "vvvvvvvvNNNííííííäÝÝÝÜÜÜÜÜÜÛÛÛÛÛvvvvvvvNNNNíííííääääÜÜÜÜÜÜÜÛÛÛÛÛvvvvvvvNNN==íííääääääÜÜÜÜÜÜÛÛÛÛÛmmvvvvvN======ääääääÕÜÜÜÜÜÜÛÛÛÛÛ"+
                    "mmmmmm=======äääääääÕÕÜÜÜÜÜÛÚÚÚÚmmmmmmm=====\034ääääääÕÕÕÔÔÜÜÜÚÚÚÚÚmmmmmmmmeee\034ôôôôFääÕÕÔÔÔÔÊÉÚÚÚÚÚmmmmmmmeeee,ôôôôFôÄÕÕÔÔÔÔÉÉÚÚÚÚÚ"+
                    "mmmmllleeeSSSôôôFÄÄÄÔÔÔÔÔÉÉÓÙÙÙÙ||lllllleSSS^^^^#ÄÄÄÔÔÔÔÉÉÉÓÙÙÙÙ|||lllllSSSS^^22##ÄÄÔÔÔÔÉÉÓÓÓÙÙÙ|||{{{{{{SS^2222##ÄÄÃÔÔÔÉÉÓÓÓÙÙÙ"+
                    "|{{{{{{{{{d2222###ÄÄÃÃÃÈÈÉÓÓÓÒØØ{{{{{{{{{{dd22####ÃÃÃÃÈÈÈÓÓÒÒÒØ{{{{{{{{{{zd\022ÃÃÃÈÈÈÓÓÒÒÒØ{{{zzzzzzzz\022ÃÃÃÈÈÈÓÒÒÒÒÒ"+
                    "\022\022\022ÃÈÈÈÈÂÂÒ¹¹¹ÈÈÈÂÂÂ¹¹¹¸ÈÂÂÂÂ¹¹¸¸ÂÂÂÂ¹¸¸¸"+
                    "ÂÂÂ¸¸¸¸¸¸¸¸¸¸¸¸¸¨¸¸¸"+
                    "¨¨¨¸¸°°°°°°°°°°°°°°°°°°°°°"+
                    "þþþþþþþvvvíííííííÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛvvvvvvvvvvíííííííÝÝÝÝÜÜÜÜÜÜÜÛÛÛÛvvvvvvvvvíííííííííÝÝÝÜÜÜÜÜÜÛÛÛÛÛvvvvvvvvvíííííííííÝÝÝÜÜÜÜÜÜÛÛÛÛÛ"+
                    "vvvvvvvvvííííííííäääÜÜÜÜÜÜÜÛÛÛÛÛvvvvvvvvvíííííííäääääÜÜÜÜÜÜÛÛÛÛÛvvvvvvvvíííííííääääääÜÜÜÜÜÜÛÛÛÛÛvvvvvvvv=íííííäääääääÜÜÜÜÜÜÛÛÛÚÚ"+
                    "mmmmmvv====íääääääääÕÕÜÜÜÜÜÚÚÚÚÚmmmmmmm===\034\034\034äääääääÕÔÔÔÔÜÚÚÚÚÚÚmmmmmmll,,,\034\034\034äääääÕÕÔÔÔÔÔÉÚÚÚÚÚmmmlllll,,,,\034ôôôôôEÄÕÔÔÔÔÉÓÙÙÙÙÙ"+
                    "lllllllll,,,ôôôôEEEÄÔÔÔÔÔÉÓÓÙÙÙÙlllllllll,,,2222EEEÄÔÔÔÔÔÉÓÓÙÙÙÙlllllllllddd222###EÄÔÔÔÔÉÓÓÓÓÙÙÙ{{{{{{{{dddd222###EÄÃÃÔÔÉÓÓÓÓØØØ"+
                    "{{{{{{{{{ddd22#####ÄÃÃÃÈÈÓÓÓÒÒØØ{{{{{{{{{{ddd]]###\022\022ÃÃÃÈÈÓÓÓÒÒÒØ{{{{{{{{zzzdR]]]\022\022ÃÃÃÈÈÓÓÒÒÒÒØ{zzzzzzzzzzzRR\022\022\022\022ÃÃÈÈÈÓÒÒÒÒÑ"+
                    "z\022\022\022\022ÃÈÈÈÂÂÂÒÒÒÑÈÂÂÂÂÂÒÑ¸ÂÂÂÂÂ¸¸¸ÂÂÂÂÂ¸¸¸"+
                    "ÂÂÂ¨¸¸¸¨¨¸¸¨¨¨¨¨¨¨¨"+
                    "¨¨¨¨¨°°°°°°°°°°°°°°°°°°°°°°"+
                    "vvvvvvvvvíííííííííÝÝÝÜÜÜÜÜÜÜÛÛÛÛvvvvvvvvvííííííííííÝÝÜÜÜÜÜÜÛÛÛÛÛvvvvvvvvýííííííííííÝÝÜÜÜÜÜÜÛÛÛÛÛvvvvvvvvýííííííííííäÜÜÜÜÜÜÜÛÛÛÛÛ"+
                    "vvvvvvvvýííííííííääääÜÜÜÜÜÜÛÛÛÛÛvvvvvvvýýíííííííäääääÜÜÜÜÜÜÛÛÛÛÛvvvvvvvýýííííííääääääÜÜÜÜÜÜÛÛÛÛÚvvvvvvýýííííííäääääääãÜÜÜÜÜÛÚÚÚÚ"+
                    "mvvvvvýýMíííääääääääãããÜÜÜÜÚÚÚÚÚmmmmllMMM<<\034ääääääääãããÔÔÔÚÚÚÚÚÚmlllllll<<<\034\034\034ääääääããÔÔÔÔÓÚÚÚÚÚllllllll<<<,\034\034ôääEEEãÔÔÔÔÓÓÙÙÙÙÙ"+
                    "lllllllll<,,,ôôEEEEEãÔÔÔÔÓÓÓÙÙÙÙllllllllldddd2EEEEEEÔÔÔÔÔÓÓÓÙÙÙÙlllllllldddddd##EEEEÔÔÔÔÓÓÓÓÓØØØlllllllldddddd###EEEÃÃÃÔÓÓÓÓÓØØØ"+
                    "{{{{{{{{ddddd]]]]]#\022ÃÃÃÈÈÓÓÓÒÒØØ{{{{{{{{zdddR]]]]]\022\022ÃÃÃÈÈÓÓÓÒÒÒØ{{{{{zzzzzzRR]]]]\022\022\022\022ÃÃÈÈÓÓÒÒÒÒØzzzzzzzzzzzzRRR]\022\022\022\022\022ÃÃÈÈÓÓÒÒÒÒÑ"+
                    "zzzzzzzzzzzzRR\\\\\\\\\022\022\022\022ÃÈÈÂÂÂÒÒÑÑÈÂÂÂÂÂÒÑÑÂÂÂÂÂÂ¸¸ÂÂÂÂÂ¨¨¸"+
                    "ÂÂÂ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨"+
                    "¨¨¨¨°°°°°°°°°°°°°°°°°°°°°°"+
                    "vvvvvvýýýýýííííííííÝÝÜÜÜÜÜÜÛÛÛÛÛvvvvvýýýýýýíííííííííÝÜÜÜÜÜÜÛÛÛÛÛvvvvvýýýýýííííííííííÜÜÜÜÜÜÜÛÛÛÛÛvvvvýýýýýýíííííííííääÜÜÜÜÜÜÛÛÛÛÛ"+
                    "vvvvýýýýýýíííííííääääÜÜÜÜÜÜÛÛÛÛÛvvvýýýýýýýííííííäääääÜÜÜÜÜÜÛÛÛÛÛvvvýýýýýýýíííííääääääãÜÜÜÜÜÛÛÚÚÚvvýýýýýýýýííííäääääääããÜÜÜÜÚÚÚÚÚ"+
                    "vvýýýýMMMMMMääääääääããããâÜÚÚÚÚÚÚllllMMMMMMM<<äääääääããããââÚÚÚÚÚÚllllllMMM<<<<ääääääããããâââÓÙÙÙÙÙllllllll<<<<<<\033ääEEEãããâââÓÙÙÙÙÙ"+
                    "llllllll<<<+++\033\033EEEEããÔââÓÓÓÙÙÙÙlllllllll+++++\033óóEEEãÔÔââÓÓÓØØØØlllllllldddd+\033óóóóEEóÔÔâÓÓÓÓÓØØØlllllllldddddRóóóóóóÃÃÃÈÓÓÓÓÓØØØ"+
                    "{{{{{zzzddddRRR]]]]\022ÃÃÃÈÓÓÓÓÒÒØØ{zzzzzzzzzdRRRR]]]\022\022\022ÃÃÈÈÓÓÒÒÒÒØzzzzzzzzzzzRRRR]]\022\022\022\022ÃÃÈÈÓÓÒÒÒÒÑzzzzzzzzzzzzRRR]\022\022\022\022\022\022ÃÈÈÂÒÒÒÒÑÑ"+
                    "zzzzzzzzzzzzcc\\\\\\\\\\\022\022\022[ÈÂÂÂÂÒÒÑÑzzzzzzzzyyyyy\\\\[[ÂÂÂÂÂÑÑÑyyyyyy[[ÂÂÂÂÂÑÑÑyyÂÂÂÂÂ¨¨¨"+
                    "\021ÂÂÂ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨"+
                    "¨¨¨¨°°°°°°°°°°°°°°°°°°°°"+
                    "vvýýýýýýýýýýíííííííííÜÜÜÜÜÜÛÛÛÛÛvýýýýýýýýýýýíííííííííÜÜÜÜÜÜÛÛÛÛÛvýýýýýýýýýýýííííííííäÜÜÜÜÜÜÛÛÛÛÛýýýýýýýýýýýýíííííííääÜÜÜÜÜÜÛÛÛÛÛ"+
                    "ýýýýýýýýýýýýííííííäääìÜÜÜÜÜÛÛÛÛÛýýýýýýýýýýýýííííäääääãÜÜÜÜÜÛÛÛÚÚýýýýýýýýýýýMíííääääääããÜÜÜÜÚÚÚÚÚýýýýýýýýMMMMMMääääääããããâÜÚÚÚÚÚÚ"+
                    "ýýýýýMMMMMMMMMääääããããããââÚÚÚÚÚÚýýMMMMMMMMMMMäääãããããããââââÙÙÙÙÙlllMMMMMMMM<<<\033ããããããããâââÓÙÙÙÙÙllllluuuu<<<+\033\033\033ããããããââââÓÙÙÙÙÙ"+
                    "lllllllu<++++\033\033\033\033ãããããââââÓÓØØØØllllllll+++++\033\033\033\033óãããââââÓÓÓØØØØllllllkk+++++\033\033\033óóóóãââââÓÓÓØØØØlllkkkkkkk+++R\033óóóóóòòââÓÓÓÓÒØØØ"+
                    "kkkkkkkkkkkRRRRR]]òòòòòÈÓÓÓÓÒÒØØzzzzkkkkkkkRRRRRR11\022\022òòÈÓÓÓÒÒÒÒØzzzzzzzzzzzccRRR11\022\022\022òòÈÈÓÒÒÒÒÒÑzzzzzzzzzzzcccc\\\\\\\\\022\022\\[[ÂÂÂÒÒÒÑÑ"+
                    "zzzzzzzzzzzycc\\\\\\\\\\\\\022\\[[ÂÂÂÂÒÑÑÑzzzzzzyyyyyyyy\\\\\\\\\\\\\\[[[ÂÂÂÂÂÑÑÑzzyyyyyyyyyyyy[[[[ÂÂÂÂÂÑÑÑyyyyyyyyyyyy[[[\021ÂÂÂÂÑÑÑ"+
                    "\021\021ÂÂÂ¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨¨"+
                    "¨¨¨¨°°°°°°°°°°°°°°°°°°°°"+
                    "ýýýýýýýýýýýýýýííììììììÜÜÜÜÜÛÛÛÛÛýýýýýýýýýýýýýýíìììììììÜÜÜÜÜÛÛÛÛÛýýýýýýýýýýýýýíìììììììììÜÜÜÜÛÛÛÛÛýýýýýýýýýýýýýììììììììììÜÜÜÜÛÛÛÛÛ"+
                    "ýýýýýýýýýýýýýììììììììììÜÜÜÜÛÛÛÛÚýýýýýýýýýýýýìììììììììãããÜÜÜÛÚÚÚÚýýýýýýýýýýMMMììììììãããããâÜÚÚÚÚÚÚýýýýýýýMMMMMMìììììããããããââÚÚÚÚÚÚ"+
                    "ýýýMMMMMMMMMMMììãããããããââââÚÚÚÚÚuuuuuuuuuuuuuLãããããããããââââÙÙÙÙÙuuuuuuuuuuuuLLãããããããããââââÙÙÙÙÙuuuuuuuuuuuLL\033\033ãããããããââââÓÙÙÙÙÙ"+
                    "lluuuuuuuuL++\033\033ãããããããââââÓÓØØØØlllkuuuuu+++\033\033\033\033ãããããâââââÓÓØØØØkkkkkkkkkk+;;;\032\032\032ãããâââââÓÓÓØØØØkkkkkkkkkkk;;\032\032\032\032\032DDDââââÓÓÓÒØØØ"+
                    "kkkkkkkkkkkccc\032\032\032òòòòòDâÓÓÓÒÒÒØØkkkkkkkkkkkccccc11òòòòòDÓÓÓÒÒÒØØkkkkkkkkkkccccc111\"\"òòòòÓÓÒÒÒÒÑÑzzzzzkkkkkccccc111\\\\\\\\[[ÂÂÂÒÒÒÑÑ"+
                    "zzzzzzzyyyyyccc\\\\\\\\\\\\[[[ÂÂÂÂÒÑÑÑzzzzyyyyyyyyyy\\\\\\\\\\\\[[[[ÂÂÂÂÂÑÑÑyyyyyyyyyyyyyy000[[[[[\021ÂÂÂÂÑÑÑyyyyyyyyyyyyyyb00000[[[[\021ÂÂÂÂÑÑÑ"+
                    "b\021\021\021\021ÂÂÂ¨¨¨\021¨¨¨¨¨¨¨¨¨¨¨¨"+
                    "¨¨¨°°°°°°°°°°qqqqq°°°°°°°°"+
                    "ýýýýýýýýýýýýìììììììììììÜÜÜÜÛÛÛÛÛýýýýýýýýýýýýììììììììììììÜÜÜÛÛÛÛÛýýýýýýýýýýýìììììììììììììÜÜÜÛÛÛÛÛýýýýýýýýýýýìììììììììììììÜÜÜÛÛÛÛÛ"+
                    "ýýýýýýýýýýìììììììììììììììÜÜÛÛÚÚÚýýýýýýýýýýìììììììììììììììÜÚÚÚÚÚÚýýýýýýýýýMìììììììììììãããââÚÚÚÚÚÚýýýýuuuuuuììììììììììããããâââÚÚÚÚÚ"+
                    "uuuuuuuuuuuLììììììãããããââââÙÙÙÙÙuuuuuuuuuuLLLìììãããããããââââÙÙÙÙÙuuuuuuuuuLLLLüüãããããããâââââÙÙÙÙÙuuuuuuuuLLLLLüãããããããââââââØØØØØ"+
                    "uuuuuuuLLLLL;;;ãããããââââââÓÓØØØØkkkkuuLLLLL;;;;;ãããâââââââÓÓØØØØkkkkkkkkkk;;;;;\032\032\032ââââââââÓÓØØØØkkkkkkkkkk****\032\032\032\032DDâââââÓÓÓÒØØØ"+
                    "kkkkkkkkkkk***\032\032\032DDDDDDââÓÓÒÒÒØØkkkkkkkkkkkccccc1òòòòòòDáÓÒÒÒÒÑØkkkkkkkkkkccccc111\"òòòòòááÒÒÒÒÑÑkkkkkkkkkkccccc11\"\"\"\"[[[[ÂÂÒÒÑÑÑ"+
                    "kkkkyyyyyyyyccc1\"\"\"0[[[[[ÂÂÂÒÑÑÑyyyyyyyyyyyyyyQQQ000[[[[[ÂÂÂÑÑÑÑyyyyyyyyyyyyyyb0000[[[[[\021ÂÂÂÑÑÑÑyyyyyyyyyyyyybbb0000[[[\021\021\021ÂÂÂÑÑÑ"+
                    "bbb000\021\021\021\021\021\021ÂÂÑÑÁZZ\021\021\021\021¨¨ÁÁ¨ÁÁ¨ÁÁ"+
                    "ÀÀÀÀÀÀÀÀrriÀÀÀÀÀÀqqqqqqqqqqqHHhÀÀÀÀÀÀÀÀ"+
                    "ýýýýýýýýýýìììììììììììììììÜÜÛÛÛÛÛýýýýýýýýýììììììììììììììììÜÜÛÛÛÛÛýýýýýýýýýìììììììììììììììììÜÛÛÛÛÛýýýýýýýýììììììììììììììììììÜÛÛÛÛÚ"+
                    "ýýýýýýýýììììììììììììììììììëÚÚÚÚÚýýýýýýýìììììììììììììììììììëÚÚÚÚÚýýýuuuuuìììììììììììììììãâââÚÚÚÚÚuuuuuuuuLììììììììììììããââââÙÙÙÙÙ"+
                    "uuuuuuuLLLìììììììììãããâââââÙÙÙÙÙuuuuuuuLLLLüüüììììãããââââââÙÙÙÙÙuuuuuuLLLLLüüüüüãããããââââââØØØØØuuuuuLLLLLLüüüüüããããâââââââØØØØØ"+
                    "uuuuLLLLLLLLüü;;ãããââââââââÓØØØØkKKKKKKKKKK;;;;;\032ãââââââââÓÓØØØØkkkkkkKKKK***;;;\032\032ââââââââÓÓØØØØkkkkkkkkkk*****\032\032DDDâââââááÒÒØØØ"+
                    "kkkkkkkkkk****\031\031\031DDDDDDâáááÒÒÒØØkkkkkkkkkkkcc\031\031\031\031DDDDDDDáááÒÒÒÑÑkkkkkkkkkkkccc\031\031\031\"DDDDDDáááÒÒÑÑÑkkkkkkkkkkcccccQQQQQC[[[ñááÒÒÑÑÑ"+
                    "kkkkyyyyyyyybbbQQQQ0[[[[[ÂÂÂÑÑÑÑyyyyyyyyyyyybbbbQ000[[[[\021\021ÂÂÑÑÑÑyyyyyyyyyyyyybbb0000[[[[\021\021ÂÂÑÑÑÑyyyyyyyyyyyyybbb0000[[[\021\021\021\021ÂÑÑÑÑ"+
                    "xxxxxxxbbbb000Z\021\021\021\021\021\021\021ÑÁÁÁxxxxxxxx!!!ZZZZ\021\021\021\021ÁÁÁÁxxxxxxxaaZZÁÁÁÁxxxxaaaÁÁÁÁ"+
                    "rxiaÀÀÀÀrrriiiÀÀÀÀÀrrqqqqqqiiiiiÀÀÀÀÀÀÀqqqqqqqqqqqHHhhhh@@pÀÀÀÀÀÀÀÀ"+
                    "ýýýýýýýììììììììììììììììììììÛÛÛÛÛýýýýýýììììììììììììììììììììëÛÛÛÛÛýýýýýýììììììììììììììììììììëÛÛÛÛÛýýýýýìììììììììììììììììììììëÛÚÚÚÚ"+
                    "ýýýýýìììììììììììììììììììììëÚÚÚÚÚýuuuuììììììììììììììììììììëëÚÚÚÚÚuuuuuLììììììììììììììììììââëÚÚÚÚÚuuuuLLLììììììììììììììììââââÙÙÙÙÙ"+
                    "uuuuLLLLLììììììììììììââââââÙÙÙÙÙuuuLLLLLLüüüüüüüììììâââââââØØØØØuuLLLLLLLüüüüüüüüüüââââââââØØØØØKKKKLLLLLüüüüüüüüüâââââââââØØØØØ"+
                    "KKKKKKKKKKKüüüüüüââââââââââáØØØØKKKKKKKKKKKKKü;;\031âââââââââááØØØØtKKKKKKKKKKKK*\031\031\031\031âââââââáááØØØØttttKKKKKKKK*\031\031\031\031\031\031ââââââááááØØØ"+
                    "tttttttKKKK\031\031\031\031\031\031\031\031DDDâááááááÒØØttttttttttK\031\031\031\031\031\031\031)CCCCáááááÒÑÑÑtttttttttjj\031\031:::::CCCCCáááááÒÑÑÑttttttjjjjjjj::::QCCCCCñááááÑÑÑÑ"+
                    "ttjjjjjjjjjjjbbbQQQCCCññññááÑÑÑÑjjjjjjjjjjjjjbbbbQ00!ññññññÂÑÑÑÑyyjjjjjjjjjjjbbbb0!!![\021\021\021\021\021ÂÑÑÑÑyyyjjjjjjjjjjbbbb!!!!Z\021\021\021\021\021ZÑÑÑÑ"+
                    "xxxxxxxxxxxxxbbbb!!!ZZ\021\021\021\021\021ZÁÁÁÁxxxxxxxxxxxxxxxx!!!ZZZZZZ\021ZZÁÁÁÁxxxxxxxxxxxxxxxaaaaZZZZZZZZÁÁÁÁrxxxxxxxxxxxxxaaaaaÁÁÁÁ"+
                    "rrrxxxxxxxxiiiaaaa8YYÀÀÀÀrrrrrxxxiiiiiiiaa888YYÀÀÀÀÀÀrrqqqqqqqqiiiiihh@8888YYÀÀÀÀÀÀÀqqqqqqqqqqqHHhhhh@@@@ppÀÀÀÀÀ"+
                    "ýýýýììììììììììììììììììììììëëÛÛÛÛýýýìììììììììììììììììììììììëëÛÛÛÛýýýìììììììììììììììììììììììëëÛÚÚÚýýììììììììììììììììììììììììëëÚÚÚÚ"+
                    "uuììììììììììììììììììììììëëëëÚÚÚÚuuuLìììììììììììììììììììëëëëëÚÚÚÚuuLLLìììììììììììììììììëëëëëëÙÙÙÙuLLLLLìììììììììììììììëëëââëëÙÙÙÙ"+
                    "LLLLLLLüüüüìììììììììëëâââââØØØØØLLLLLLLüüüüüüüüüüììëëââââââØØØØØKKKKKKKKüüüüüüüüüüüââââââââØØØØØKKKKKKKKKKüüüüüüüüüââââââââáØØØØ"+
                    "KKKKKKKKKKKKüüüüüüââââââââááØØØØtKKKKKKKKKKKKKüü\031\031ââââââââááØØØØttttKKKKKKKKKK\031\031\031\031\031âââââáááááØØØtttttttKKKKKK\031\031\031\031\031))ââáááááááØØØ"+
                    "tttttttttttK\031\031\031)))))CáááááááááØØtttttttttttt)))))))CCááááááááÑÑÑttttttttttjj::::)))CCCáááááááÑÑÑtttttttjjjjjj:::::CCCCññáááááÑÑÑ"+
                    "ttttjjjjjjjjjbbbbCCCCñññññááÑÑÑÑtjjjjjjjjjjjjbbbb!!!!ññññññáÑÑÑÑsjjjjjjjjjjjjbbbb!!!!ñññZ\021ZZÑÑÑÑsjjjjjjjjjjjjbbb!!!!!ZZZZ\021ZZÑÑÑÑ"+
                    "xxxxxxxxxxxxxxbb!!!!ZZZZZZZZÁÁÁÁxxxxxxxxxxxxxxxaa!!ZZZZZZZZYYÁÁÁxxxxxxxxxxxxxxxaaaaZZZZZZZZYYÁÁÁrrxxxxxxxxxxxxiaaaaa8ZZZYYYYYÁÁÁ"+
                    "rrrrrxxxxxiiiiiaaaa888YYYYYYYÀÀÀrrrrrqqqiiiiiiiia88888YYYYYÀÀÀÀÀrrqqqqqqqqqiHHhhh@@@@ppYYÀÀÀÀÀÀqqqqqqqqqqqHhhhhh@@@@pppÀÀÀ"+
                    "ýììììììììììììììììììììììëëëëëëÛÛÛììììììììììììììììììììììëëëëëëëÛÛÚìììììììììììììììììììììëëëëëëëëÚÚÚììììììììììììììììììììëëëëëëëëëÚÚÚ"+
                    "LììììììììììììììììììëëëëëëëëëëÚÚÚLLìììììììììììììììëëëëëëëëëëëëÚÚÚLLLìììììììììììììëëëëëëëëëëëëÙÙÙÙLLLLLììììììììììëëëëëëëëëëëëëÙÙÙÙ"+
                    "KKKKKüüüüüüüììëëëëëëëëëëëëâëØØØØKKKKKKKüüüüüüüëëëëëëëëëëââââØØØØKKKKKKKKüüüüüüüëëëëëëëëââââáØØØØKKKKKKKKKKüüüüüëëëëëëââââââáØØØØ"+
                    "ttKKKKKKKKKKüüüüëëëëâââââáááØØØØttttKKKKKKKKKKüü\031\031âââââááááááØØØtttttttKKKKKKK\031)))))âááááááááØØØttttttttttKKK))))))\030ááááááááááØØ"+
                    "tttttttttttt))))))\030\030ááááááááááÑØtttttttttttt)))))\030\030\030\030áááááááááÑÑtttttttttttt))))\030\030\030\030\030ááááááááÑÑÑtttttttttjjjj:\030\030\030\030\030\030ññáááááááÑÑÑ"+
                    "sttttjjjjjjjjbb\030\030\030\030!ñññññáááÑÑÑÑssssjjjjjjjjjbbb!!!!ñññññññáÑÑÑÑssssjjjjjjjjjbbb!!!!PññññZZZàÑÑÑssssjjjjjjjjjbbb!!PPPPZZZZZZàÐÐÐ"+
                    "sssxxxxxxxxxxxaaaaPPPZZZZZZZYÐÐÐsxxxxxxxxxxxxxaaaaaPZZZZZZZYYÐÐÐrxxxxxxxxxxxxxaaaaaaZZZZZZYYYÁÁÁrrrrxxxxxxxxiiiaaaaa88YYYYYYYÁÁÁ"+
                    "rrrrrrxxiiiiiiiiaa8888YYYYYYYÀÀÀrrrrrqqqiiiiiiiia88888YYYYYYÀÀÀÀrrqqqqqqqqqHHHhhh@@@@pppYÀÀÀqqqqqqqqqqqHhhhhh@@@@pppÀ"+
                    "ìììììììììììììììììëëëëëëëëëëëëÛÛÛììììììììììììììììëëëëëëëëëëëëëëÚÚìììììììììììììììëëëëëëëëëëëëëëÚÚÚìììììììììììììëëëëëëëëëëëëëëëëÚÚÚ"+
                    "ììììììììììììëëëëëëëëëëëëëëëëëÚÚÚLììììììììììëëëëëëëëëëëëëëëëëëÙÙÙLLìììììììëëëëëëëëëëëëëëëëëëëëÙÙÙKKKKüüììëëëëëëëëëëëëëëëëëëëëëØØØ"+
                    "KKKKKKüüëëëëëëëëëëëëëëëëëëëëêØØØKKKKKKKüüëëëëëëëëëëëëëëëëëëêêØØØKKKKKKKKKüëëëëëëëëëëëëëëëêêêêØØØtKKKKKKKKKKëëëëëëëëëëëëêêêêáêØØØ"+
                    "tttttKKKKKKKëëëëëëëëëêêêáááááØØØttttttttKKKKKûûûûûûûêêáááááááØØØtttttttttttKKûûûûûûûááááááááááØØttttttttttttt)))\030\030\030\030ááááááááááØØ"+
                    "ttttttttttttt)\030\030\030\030\030\030ááááááááááÑÑttttttttttttJ\030\030\030\030\030\030\030\030áááááááááÑÑtttttttttttJJ\030\030\030\030\030\030\030\030ááááááááÑÑÑsstttttttJJJJ\030\030\030\030\030\030\030\030ááááááááÑÑÑ"+
                    "sssssstjjjjjJ\030\030\030\030\030((ññññáááááÑÑÑsssssssjjjjjj999999PPPññññááààÑÑsssssssjjjjjj99999PPPPPPPñZZàààÐsssssssxjjjjiaa999PPPPPPZZZZààÐÐ"+
                    "sssssxxxxxxiiaaaaaaPPPPZZZZYàÐÐÐrrrrxxxxxxiiiiaaaaaaPPPZZYYYYÐÐÐrrrrrxxxxxiiiiaaaaaa888YYYYYYÐÐÐrrrrrxxxxiiiiiiaaaa888YYYYYYYÐÐÐ"+
                    "rrrrrrriiiiiiiiiaa8888YYYYYYYÀÀÀrrrrrqqqqiiiiiii888888YYYYYYYÀÀÀrrqqqqqqqqqHHHhhh@@@`ppppÀqqqqqqqqqqqHhhhhh@@@@pppp"+
                    "ììììììììììëëëëëëëëëëëëëëëëëëëëÚÚììììììììëëëëëëëëëëëëëëëëëëëëëëÚÚìììììììëëëëëëëëëëëëëëëëëëëëëëëÚÚìììììëëëëëëëëëëëëëëëëëëëëëëëëëÚÚ"+
                    "ììììëëëëëëëëëëëëëëëëëëëëëëëëëëÙÙììëëëëëëëëëëëëëëëëëëëëëëëëëëëëÙÙKKëëëëëëëëëëëëëëëëëëëëëëëëëëëêØØKKKëëëëëëëëëëëëëëëëëëëëëëëëëêêØØ"+
                    "KKKKKëëëëëëëëëëëëëëëëëëëëëêêêêØØKKKKKKëëëëëëëëëëëëëëëëëëëêêêêêØØttKKKKKKëëëëëëëëëëëëëëëêêêêêêêØØtttttKKKKëëëëëëëëëëëëêêêêêêêêêØØ"+
                    "ttttttttKKûûûûûûûûûûêêêêêêêêêêØØttttttttttûûûûûûûûûûêêêêêêêááêØØtttttttttttûûûûûûûûûêêêêêáááááØØtttttttttttJûûûûûûûûêêêáááááááéØ"+
                    "ttttttttttJJJJûû\030\030\030((áááááááááéétttttttttJJJJJJ((((((ááááááááééésssttttJJJJJJJJ(((((((áááááááééÑsssssssJJJJJJJ((((((((áááááááééÑ"+
                    "ssssssssJJJJJJ(((((((BBBááááààààssssssssJJJJJ9999999BBBBBùààààààsssssssssJJJ9999999BBBBBBBàààààèsssssssssiiiia99999BBBBBBààààààÐ"+
                    "ssssssssiiiiiiaaaaaBBBBBYYYàààÐÐrrrrrrriiiiiiiaaaaa8888YYYYYYÐÐÐrrrrrrriiiiiiiiaaa88888YYYYYYÐÐÐrrrrrrriiiiiiiiaaa88888YYYYYYYÐÐ"+
                    "rrrrrrriiiiiiiiia888888YYYYYYY\020ÀrrrrrqqqqqiiiHHHh88888 YYYYYY\020\020\020rrqqqqqqqqqHHHhhh@@@`ppppXqqqqqqqqqqqHhhhhh@@@@pppp"+
                    "ììëëëëëëëëëëëëëëëëëëëëëëëëëëëëëÚëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëÚëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëÚëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëÚ"+
                    "ëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëêÙëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëêØëëëëëëëëëëëëëëëëëëëëëëëëëëëëêêêØKëëëëëëëëëëëëëëëëëëëëëëëëëëêêêêØ"+
                    "KKKëëëëëëëëëëëëëëëëëëëëëëêêêêêêØttKKëëëëëëëëëëëëëëëëëëëêêêêêêêêØtttttKëëëëëëëëëëëëëëëêêêêêêêêêêØtttttttûûûûûûûûëëûûêêêêêêêêêêêêØ"+
                    "ttttttttûûûûûûûûûûûêêêêêêêêêêêêØtttttttttûûûûûûûûûûêêêêêêêêêêêêØtttttttttûûûûûûûûûûêêêêêêêêêêêêéttttttttJJJûûûûûûûûêêêêêêêêêááéé"+
                    "sttttttJJJJJJJûûûûûêêêêêééééééééssssttJJJJJJJJJ(((((úúúúééééééééssssssJJJJJJJJJ(((((úúúééééééééésssssssJJJJJJJJ((((((úéééééééééà"+
                    "ssssssssJJJJJJ(((((((úééééééààààsssssssssJJJJJ999999BBBùùùùàààààsssssssssIIIII99999BBBBBùùàààààèsssssssssIIIII9999BBBBBBùùàààààè"+
                    "sssssssssIiiiiaaaaBBBBBBùàààààèèrrrrrrrriiiiiiiaaa88888 YYYàààÐÐrrrrrrrriiiiiiiaa888888 YYYYYøøÐrrrrrrrriiiiiiii888888  YYYYYøøø"+
                    "rrrrrrrrqiiiiiii888888  YYYYYYøørrrrrqqqqqHHHHHHh88```` YYYY\020\020\020\020rqqqqqqqqqqHHHhhh@@@`ppppXXX\020\020\020qqqqqqqqqqqHhhhhh@@@@pppppX\020"+
                    "ëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëê"+
                    "ëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëêêëëëëëëëëëëëëëëëëëëëëëëëëëëëëêêêêëëëëëëëëëëëëëëëëëëëëëëëëëëëêêêêêëëëëëëëëëëëëëëëëëëëëëëëëëêêêêêêê"+
                    "tëëëëëëëëëëëëëëëëëëëëëëêêêêêêêêêtttëëëëëëëëëëëëëëëëëëêêêêêêêêêêêtttttëëëëëëëëëëëëëëêêêêêêêêêêêêêttttttûûûûûûûûûûûûûêêêêêêêêêêêêê"+
                    "ttttttûûûûûûûûûûûûûêêêêêêêêêêêêêtttttttûûûûûûûûûûûûêêêêêêêêêêêêêtttttttJJûûûûûûûûûêêêêêêêêêêêêêésttttJJJJJJûûûûûûûêêêêêêêêêêéééé"+
                    "sssssJJJJJJJJûûûûûêêêêêêéééééééésssssJJJJJJJJJJJ(úúúúúúúééééééééssssssJJJJJJJJJ(((úúúúúééééééééésssssssJJJJJJJJ(((úúúúúééééééééé"+
                    "ssssssssJJJJJJJ((((úúúéééééééàààsssssssssIIIIII(((((úùùùùùùààààèsssssssssIIIIIII999BBùùùùùùààààèssssssssIIIIIIII99BBBBùùùùàààààè"+
                    "rrrrrrrrIIIIIIIII88AAAAùùùààààèèrrrrrrrrrIIIIIII888AAA   ððààèèèrrrrrrrrrriiiiii888AAA   ððððøøørrrrrrrrriiHHHHH8888A    ððððøøø"+
                    "rrrrrrrqqqHHHHHHH````    ððððøøørrrrqqqqqqHHHHHHh@`````ppXXXX\020\020\020rqqqqqqqqqqHHHhhh@@@``pppXXXX\020\020\020qqqqqqqqqqqHhhhhh@@@@pppppXXX\020\020\020"+
                    "ëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëëêê"+
                    "ëëëëëëëëëëëëëëëëëëëëëëëëëëëëêêêêëëëëëëëëëëëëëëëëëëëëëëëëëëêêêêêêëëëëëëëëëëëëëëëëëëëëëëëëëêêêêêêêëëëëëëëëëëëëëëëëëëëëëëëêêêêêêêêê"+
                    "ëëëëëëëëëëëëëëëëëëëëëêêêêêêêêêêêttëëëëëëëëëëëëëëëëëêêêêêêêêêêêêêtttûûûûûûûûûûûûûûûêêêêêêêêêêêêêêttttûûûûûûûûûûûûûûêêêêêêêêêêêêêê"+
                    "tttttûûûûûûûûûûûûûêêêêêêêêêêêêêêtttttJûûûûûûûûûûûûêêêêêêêêêêêêêêssttJJJJûûûûûûûûûûêêêêêêêêêêêêêéssssJJJJJJûûûûûûûûêêêêêêêêêêéééé"+
                    "sssssJJJJJJJJûûûûêúúúúúúééééééééssssssJJJJJJJJJûúúúúúúúúéééééééésssssssJJJJJJJJJúúúúúúúéééééééééssssssssJJJJJJJ(úúúúúúúééééééééé"+
                    "sssssssssJJJIIIIúúúúúúééééééééàèsssssssssIIIIIIIIúúúúùùùùùùùàààèssssssssIIIIIIIIIIBùùùùùùùùùààèèssssssssIIIIIIIIIIAAùùùùùùùàààèè"+
                    "rrrrrrrrIIIIIIIIIAAAAAAùùùàààèèèrrrrrrrrrIIIIIIIAAAAAAAA ðððèèèèrrrrrrrrrrIIIIIHAAAAAAA  ððððøøørrrrrrrrrHHHHHHHHAAAAA   ððððøøø"+
                    "rrrrrrrqqqHHHHHHH``````  ððððøøørrrrqqqqqqHHHHHhh@`````ppXXXXX\020\020rqqqqqqqqqqHHHhhh@@@``pppXXXXX\020\020qqqqqqqqqqqHhhhhh@@@@pppppXXX\020\020\020"
    ).getBytes(StandardCharsets.ISO_8859_1)
    )) {
        private final byte[] primary = {
                28, 36, 44, 52, 60, 68, 76, 84, 92, 100, 108, 116, 124, -124, -116, -108, -100, -92, -84, -76, -68, -52, -44, -36, -28, -20, -12, -4
        }, grays = {
                23, 22, 21, 20, 19, 18, 17, 16
        };

        @Override
        public byte[] mainColors() {
            return primary;
        }

        /**
         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
         */
        @Override
        public byte[] grayscale() {
            return grays;
        }

        @Override
        public byte brighten(byte voxel) {
            if((voxel & 7) == 0)
                return 16;
            return (byte) ((voxel & 248) + (voxel & 7) - 1);
        }

        @Override
        public byte darken(byte voxel) {
            if((voxel & 7) >= 6)
                return 23;
            return (byte)((voxel & 248) + (voxel & 7) + 2);
        }

        @Override
        public int bright(byte voxel) {
            if((voxel & 7) == 0)
                return Coloring.RINSED[16];
            return Coloring.RINSED[(voxel & 248) + (voxel & 7) - 1];
        }

        @Override
        public int medium(byte voxel) {
            return Coloring.RINSED[(voxel & 255)];
        }

        @Override
        public int dim(byte voxel) {
            if((voxel & 7) == 7)
                return Coloring.RINSED[23];
            return Coloring.RINSED[(voxel & 248) + (voxel & 7) + 1];
        }

        @Override
        public int dark(byte voxel)
        {
            if((voxel & 7) >= 6)
                return Coloring.RINSED[23];
            return Coloring.RINSED[(voxel & 248) + (voxel & 7) + 2];
        }
    };

    public static final Colorizer SmashColorizer = new Colorizer(new PaletteReducer(Coloring.SMASH256)) {
        private final byte[] primary = {
                reducer.reduceIndex(0xFF0000FF),reducer.reduceIndex(0xFFFF00FF),
                reducer.reduceIndex(0x00FF00FF),reducer.reduceIndex(0x00FFFFFF),
                reducer.reduceIndex(0x0000FFFF),reducer.reduceIndex(0xFF00FFFF),
        }, grays = {
                reducer.reduceIndex(0x000000FF),reducer.reduceIndex(0x333333FF),
                reducer.reduceIndex(0x666666FF),reducer.reduceIndex(0x999999FF),
                reducer.reduceIndex(0xCCCCCCFF),reducer.reduceIndex(0xFFFFFFFF),
        };

        @Override
        public byte[] mainColors() {
            return primary;
        }

        /**
         * @return An array of grayscale or close-to-grayscale color indices, with the darkest first and lightest last.
         */
        @Override
        public byte[] grayscale() {
            return grays;
        }

        @Override
        public byte brighten(byte voxel) {
            return SMASH_RAMPS[voxel & 0xFF][0];
        }

        @Override
        public byte darken(byte voxel) {
            return SMASH_RAMPS[voxel & 0xFF][2];
        }
    };

}
