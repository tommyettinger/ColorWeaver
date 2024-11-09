/*
 * Copyright (c) 2023 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package colorweaver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.github.tommyettinger.digital.Base;
import com.github.tommyettinger.digital.MathTools;
import com.github.tommyettinger.ds.IntList;
import com.github.tommyettinger.ds.support.sort.IntComparator;

import java.util.Arrays;

/*
{
0x00000000, 0x000000FF, 0xFFFFFFFF, 0x111111FF, 0x222222FF, 0x333333FF, 0x444444FF, 0x555555FF,
0x666666FF, 0x777777FF, 0x888888FF, 0x999999FF, 0xAAAAAAFF, 0xBBBBBBFF, 0xCCCCCCFF, 0xDDDDDDFF,
0xEEEEEEFF, 0xC8A884FF, 0x8B591BFF, 0x6B138AFF, 0x1EAF1CFF, 0x1C4EBFFF, 0xE01750FF, 0xB3AFECFF,
0x8E5467FF, 0x571AF0FF, 0x2CB279FF, 0x1A5619FF, 0xD017A1FF, 0x97BF23FF, 0x6B67B9FF, 0x460B3FFF,
0x28B4C9FF, 0xEF595CFF, 0xB41AECFF, 0x77BE73FF, 0x545DECFF, 0x2F1082FF, 0x31BA1FFF, 0xC96BADFF,
0x9B1231FF, 0x6DC4C2FF, 0x266B46FF, 0x100FCBFF, 0xE9C165FF, 0xB466ECFF, 0x8C137EFF, 0x27D11BFF,
0x2A7394FF, 0x0C2831FF, 0xC5D1A4FF, 0x9F7725FF, 0x8017CCFF, 0x2DD254FF, 0x2373ECFF, 0xF51C6BFF,
0xAED0F1FF, 0x9D7390FF, 0x592E12FF, 0x32D39FFF, 0x1D8614FF, 0xE01CC6FF, 0xA1E230FF, 0x7785E9FF,
0x4C3167FF, 0x2FDDF0FF, 0xEE876DFF, 0xD01C16FF, 0x84DD9AFF, 0x568E1CFF, 0x2138A6FF, 0x44DB2BFF,
0xD28ED0FF, 0xA53654FF, 0x72E4EDFF, 0x26936DFF, 0x1B37F0FF, 0xEBF37CFF, 0xB79320FF, 0x8E42A7FF,
0x2DFA20FF, 0x269EBCFF, 0x144540FF, 0xC1F8DFFF, 0x99A364FF, 0x7B47EBFF, 0x39F87DFF, 0x199713FF,
0xEF498EFF, 0xB6F628FF, 0x76ADB0FF, 0x6F4B38FF, 0x47F8C7FF, 0x2D9F54FF, 0xE951EEFF, 0xB01468FF,
0x77A7F0FF, 0x535585FF, 0x340B12FF, 0xEEB196FF, 0xC3571DFF, 0x9E17C0FF, 0x66B251FF, 0x2462D0FF,
0x0E0C64FF, 0xE8ABEEFF, 0xB45B81FF, 0x7F1117FF, 0x37BC94FF, 0x1D6618FF, 0x0D0F9AFF, 0xCABD2AFF,
0x9E67C0FF, 0x6C0F55FF, 0x29C6E5FF, 0x1C656BFF, 0xF21BE8FF, 0xB6C380FF, 0x7E731BFF, 0x4F129CFF,
0x20C624FF, 0x2066A6FF, 0xC51443FF, 0x8FCED1FF, 0x6E7450FF, 0x4215D7FF, 0x29C369FF, 0xED8020FF,
0xB81A8FFF, 0x85D327FF, 0x5F7FACFF, 0x382244FF, 0x29C7B6FF, 0xCF7E54FF, 0x991AEBFF, 0x81D368FF,
0x3285EEFF, 0x162C83FF, 0xE5DE25FF, 0xC2859BFF, 0x8D351AFF, 0x34E3BCFF, 0x268B3CFF, 0x1531C9FF,
0xDDDD69FF, 0xAC8BE9FF, 0x79377BFF, 0x23E917FF, 0x25878FFF, 0xEF3E21FF, 0xB0F1ACFF, 0x929528FF,
0x673DBDFF, 0x2CEB46FF, 0x278ECAFF, 0xD64569FF, 0x90F4E9FF, 0x749D77FF, 0x494F1AFF, 0x34E791FF,
0xEFA122FF, 0xC44AB3FF, 0x82F82CFF, 0x689BC5FF, 0x1D5560FF, 0x2FF5ECFF, 0xEBA561FF, 0xAB4C17FF,
0x87F785FF, 0x5EA31FFF, 0x1D5294FF, 0xF01723FF, 0xD8A0B3FF, 0x9B5E4CFF, 0x6114C1FF, 0x24B153FF,
0x1E55F1FF, 0xD31B79FF, 0xBBAB1FFF, 0x835E94FF, 0x590E18FF, 0x2DACA4FF, 0xF46427FF, 0xBE1AC2FF,
0xA7B558FF, 0x7B68E9FF, 0x460E68FF, 0x2DB3EFFF, 0xEC6B8DFF, 0xB01816FF, 0x8AC198FF, 0x596D1CFF,
0x2F12B2FF, 0xEFC326FF, 0xE46FD9FF, 0x8F1556FF, 0x72C2EEFF, 0x2B7572FF, 0x162A0CFF, 0xEFD297FF,
0xC0791CFF, 0x8217A7FF, 0x65C426FF, 0x237AC0FF, 0x132A58FF, 0xEDC8E8FF, 0xA77E61FF, 0x771BEFFF,
0x2ED47FFF, 0x207719FF, 0xF11AA1FF, 0xC1D228FF, 0x9285BCFF, 0x6A2B41FF, 0x31D7CCFF, 0x258059FF,
0xCD28EEFF, 0xACDC72FF, 0x7B891CFF, 0x4D3793FF, 0x1ADE18FF, 0xF287AAFF, 0xBD4039FF, 0x91E2C1FF,
0x6A8C55FF, 0x453FDFFF, 0x31E369FF, 0xEC88EDFF, 0xAB4183FF, 0x72E720FF, 0x369596FF, 0x194113FF,
0xEAF3B3FF, 0xD19734FF, 0x9F4ADAFF, 0x3DF659FF, 0x2D9CEDFF, 0x1A4271FF, 0xEBF62BFF, 0xB8967BFF,
0x704F13FF, 0x36F6A6FF, 0x24A022FF, 0xF049C0FF, 0xBFF76CFF, 0x9EA1CFFF, 0x744661FF, 0x1813F0FF,
0x28A482FF, 0xDA601FFF, 0xA4159CFF, 0x85AC1EFF, 0x5754B3FF, 0x150A39FF, 0xEFAFC5FF, 0xC5625DFF,
}

Or, with a different starting point,

{
0x00000000, 0x000000FF, 0xFFFFFFFF, 0x111111FF, 0x222222FF, 0x333333FF, 0x444444FF, 0x555555FF,
0x666666FF, 0x777777FF, 0x888888FF, 0x999999FF, 0xAAAAAAFF, 0xBBBBBBFF, 0xCCCCCCFF, 0xDDDDDDFF,
0xEEEEEEFF, 0x8C591BFF, 0x6C138BFF, 0x1EAF1DFF, 0x1C4FC0FF, 0xE11750FF, 0xB4AFEBFF, 0x8E5468FF,
0x5318F1FF, 0x2CB279FF, 0x1A5619FF, 0xD017A1FF, 0x98BF22FF, 0x6C66B9FF, 0x470B3FFF, 0x28B4CAFF,
0xEF5A5CFF, 0xB618F1FF, 0x78BE74FF, 0x545EEDFF, 0x301082FF, 0x31BA20FF, 0xC96BACFF, 0x9B1331FF,
0x6EC4C2FF, 0x266C46FF, 0x100FCCFF, 0xE8C063FF, 0xB467ECFF, 0x8C127DFF, 0x28D11BFF, 0x2A7495FF,
0x0B2831FF, 0xC6D1A5FF, 0x9F7725FF, 0x7E15C8FF, 0x2ED255FF, 0x2373ECFF, 0xF51C6CFF, 0xAED0F1FF,
0x9C7390FF, 0x5A2E12FF, 0x33D39FFF, 0x1D8614FF, 0xE11CC6FF, 0xA2E230FF, 0x7785E9FF, 0x4C3167FF,
0x30DDF0FF, 0xEF886DFF, 0xD01C15FF, 0x85DD9AFF, 0x568E1CFF, 0x2238A6FF, 0x44DC2CFF, 0xD38FCEFF,
0xA53654FF, 0x73E4EDFF, 0x27936EFF, 0x1B38F0FF, 0xEBF37DFF, 0xB79420FF, 0x8F42A6FF, 0x2EFA21FF,
0x269EBCFF, 0x144640FF, 0xC2F9DFFF, 0x9AA264FF, 0x7C47ECFF, 0x39F97DFF, 0x1A9813FF, 0xEF498DFF,
0xB7F728FF, 0x77ADAFFF, 0x6F4C38FF, 0x48F8C7FF, 0x2DA054FF, 0xE951EEFF, 0xB01468FF, 0x78A7F0FF,
0x535585FF, 0x340B12FF, 0xF0B192FF, 0xC3571DFF, 0x9D16BEFF, 0x66B351FF, 0x2562D1FF, 0x0E0C64FF,
0xE7ACEFFF, 0xB45C82FF, 0x7F1117FF, 0x39BC94FF, 0x1E6618FF, 0x0D0F9BFF, 0xCBBD2AFF, 0x9F67C0FF,
0x6D0F55FF, 0x29C6E5FF, 0x1C656BFF, 0xF21BE8FF, 0xB4C482FF, 0x7F731BFF, 0x50129BFF, 0x1FC624FF,
0x2067A6FF, 0xC51443FF, 0x90CED0FF, 0x6F7550FF, 0x4015D6FF, 0x28C469FF, 0xED8020FF, 0xB81A8FFF,
0x85D327FF, 0x5F7FACFF, 0x382343FF, 0x2AC7B6FF, 0xCE7F56FF, 0xA421DEFF, 0x81D368FF, 0x3285EEFF,
0x162D83FF, 0xE5DE25FF, 0xC1859BFF, 0x8E351AFF, 0x34E3BBFF, 0x278B3BFF, 0x1532C9FF, 0xDCDE68FF,
0xAC8BE8FF, 0x79387AFF, 0x24E917FF, 0x25878FFF, 0xEF3E22FF, 0xB1F1ACFF, 0x929527FF, 0x683EBBFF,
0x2CEB46FF, 0x278ECBFF, 0xD64569FF, 0x91F4E9FF, 0x749D77FF, 0x494F1AFF, 0x34E891FF, 0xF0A127FF,
0xC44AB3FF, 0x84F82CFF, 0x689BC6FF, 0x1E5560FF, 0x2FF5ECFF, 0xDDA76FFF, 0xAC4C17FF, 0x88F786FF,
0x5EA31FFF, 0x1D5295FF, 0xF01723FF, 0xD7A0ACFF, 0x9B5E4CFF, 0x5E16C2FF, 0x24B154FF, 0x1E55F1FF,
0xD31B79FF, 0xBDAB1FFF, 0x835E94FF, 0x590E18FF, 0x2EADA5FF, 0xF46427FF, 0xBE1AC3FF, 0xA7B558FF,
0x7B69E9FF, 0x460E69FF, 0x2DB3EFFF, 0xED6C8EFF, 0xB01816FF, 0x8CC099FF, 0x596D1CFF, 0x2F13B2FF,
0xF0C425FF, 0xE36FD9FF, 0x901556FF, 0x72C3EFFF, 0x2B7572FF, 0x162A0CFF, 0xEFD296FF, 0xC0791CFF,
0x8517A4FF, 0x65C426FF, 0x237AC0FF, 0x132A57FF, 0xEDC8E8FF, 0xA77E61FF, 0x6F1FEEFF, 0x2FD57FFF,
0x217719FF, 0xF11BA1FF, 0xC1D328FF, 0x9185BCFF, 0x6A2B41FF, 0x31D8CDFF, 0x258159FF, 0xCD2AEEFF,
0xACDC72FF, 0x7B891CFF, 0x4E3792FF, 0x1ADE17FF, 0xF386AAFF, 0xBD4039FF, 0x91E2C1FF, 0x6A8C55FF,
0x453FDFFF, 0x31E46AFF, 0xEB89EDFF, 0xAB4283FF, 0x73E720FF, 0x379596FF, 0x194113FF, 0xEBF3B3FF,
0xD19733FF, 0x9E4BD9FF, 0x3FF659FF, 0x2E9DEDFF, 0x1B4271FF, 0xEBF62BFF, 0xBC9C7DFF, 0x704F13FF,
0x37F6A6FF, 0x24A023FF, 0xF14AC0FF, 0xBFF76CFF, 0x9EA2CFFF, 0x744761FF, 0x1913F0FF, 0x28A482FF,
0xDA601EFF, 0xA5159CFF, 0x86AC1EFF, 0x5654B3FF, 0x160A39FF, 0xEFADC5FF, 0xC5625CFF, 0x8F17EEFF,
}
 */
public class SnorglyPaletteGenerator {
    public static int LIMIT = 256;
    private static final boolean SORT = false;
    private static final IntList rgba = new IntList(256);

    private static final IntList mixingPalette = new IntList(256);
    private static int idx;
    private static long RR = 0xD1B54A32D192ED03L;
    private static long GG = 0xABC98388FB8FAC03L;
    private static long BB = 0x8CB92BA72F3D8DD7L;

    public static void reset() {
        rgba.clear();
        mixingPalette.clear();
        idx = 1;
        RR = 0xD1B54A32D192ED03L;
        GG = 0xABC98388FB8FAC03L;
        BB = 0x8CB92BA72F3D8DD7L;
    }

    private static void addGray(float lightness){
        int rgb = Color.rgba8888(lightness, lightness, lightness, 1f);
        rgba.add(rgb);
    }
    private static void add(){
        ++idx;

        RR += 0xD1B54A32D192ED03L;
        GG += 0xABC98388FB8FAC03L;
        BB += 0x8CB92BA72F3D8DD7L;

        int r = (int)(RR >>> 56);
        int g = (int)(GG >>> 56);
        int b = (int)(BB >>> 56);

        int rgb = r << 24 | g << 16 | b << 8 | 0xFF;
        for (int i = 1; i < rgba.size(); i++) {
            int e = rgba.get(i);
            if(HexGenerator.METRIC.difference(e, r, g, b) <= 30) return;
        }
        rgba.add(rgb);
    }
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        if(args != null && args.length > 0)
            LIMIT = Base.BASE10.readInt(args[0]);
        rgba.add(0);

        addGray(0f);
        addGray(1f);

        float grayLimit = (float)Math.ceil(Math.sqrt(LIMIT)) - 1;

        for (int i = 1; i < grayLimit; i++) {
            addGray(i / grayLimit);
        }

        while (rgba.size() < LIMIT) {
            add();
        }
        System.out.println(idx + " attempts.");
        rgba.items = lloydCompletely(rgba.toArray());

        if(SORT)
            rgba.sort(hueComparator);


        StringBuilder sb = new StringBuilder(12 * rgba.size() + 35).append("{\n");
        for (int i = 0; i < rgba.size(); i++) {
            appendHex(sb.append("0x"), rgba.get(i)).append(", ");
            if(7 == (i & 7)) sb.append('\n');
        }
        sb.append('}');
        System.out.println(sb);

        sb = new StringBuilder(rgba.size() * 7);
        for (int i = 1; i < rgba.size(); i++) {
            sb.append(String.format("%06x\n", rgba.get(i) >>> 8));
        }

        GdxNativesLoader.load();
        Gdx.files = new Lwjgl3Files();
        Gdx.files.local("palettes/hex/snorgly-"+ (LIMIT-1) +".hex").writeString(sb.toString(), false);

        System.out.println("\nFinished in " + (System.currentTimeMillis() - startTime) + " ms.");
    }

    /**
     * Constant storing the 16 hexadecimal digits, as char values, in order.
     */
    private static final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private static StringBuilder appendHex(StringBuilder builder, int number){
        for (int i = 28; i >= 0; i -= 4) {
            builder.append(hexDigits[(number >> i & 15)]);
        }
        return builder;
    }


    public static int[] lloydCompletely(int[] basePalette) {
        PaletteReducer pr = new PaletteReducer();
        float[] centroids = new float[basePalette.length << 2];
        for (int it = 1; it <= 1024; it++) {
            pr.exact(basePalette, HexGenerator.METRIC);
            byte[] pm = pr.paletteMapping;
            int index;
            float count;
            for (int i = 0; i < 0x8000; i++) {
                index = (pm[i] & 0xFF) << 2;
                float r = (i >>> 10) / 31f;
                float g = (i >>> 5 & 0x1F) / 31f;
                float b = (i & 0x1F) / 31f;

                centroids[0+index] += r;
                centroids[1+index] += g;
                centroids[2+index] += b;
                centroids[3+index]++;
            }
            mixingPalette.clear();
            mixingPalette.addAll(rgba.items, 0, 1);
            for (int i = 1; i < rgba.size(); i++) {
                count = centroids[i<<2|3];

                if(count == 0 || MathTools.isEqual(rgba.get(i) >>> 24, rgba.get(i) >>> 16 & 255, 3) &&
                        MathTools.isEqual(rgba.get(i) >>> 16 & 255, rgba.get(i) >>> 8 & 255, 3))
                    mixingPalette.add(rgba.get(i));
                else
                    mixingPalette.add(Color.rgba8888(centroids[i<<2] / count,
                            centroids[i<<2|1] / count,
                            centroids[i<<2|2] / count, 1f));
            }
            mixPalette(0, false);
            int[] palette = rgba.toArray();
            if(Arrays.equals(palette, basePalette))
            {
                System.out.println("Palette completely Lloyd-ed in " + it + " iterations");
                return palette;
            }
            System.arraycopy(palette, 0, basePalette, 0, basePalette.length);
        }
        System.out.println("Palette not completely Lloyd-ed...");
        return rgba.toArray();
    }
    private static float hue(final int color) {
        final float r = (color >>> 24       ) * 0x1.010102p-8f;
        final float g = (color >>> 16 & 0xFF) * 0x1.010102p-8f;
        final float b = (color >>>  8 & 0xFF) * 0x1.010102p-8f;
        final float min = Math.min(Math.min(r, g), b);   //Min. value of RGB
        final float max = Math.max(Math.max(r, g), b);   //Max value of RGB
        final float delta = max - min;                   //Delta RGB value

        if ( delta < 0.1f )                     //This is mostly gray, not much chroma...
        {
            return -100 + max * 0.01f;
        }
        else                                    //Chromatic data...
        {
            final float rDelta = (((max - r) / 6f) + (delta * 0.5f)) / delta;
            final float gDelta = (((max - g) / 6f) + (delta * 0.5f)) / delta;
            final float bDelta = (((max - b) / 6f) + (delta * 0.5f)) / delta;

            if      (r == max) return (1f + bDelta - gDelta)             - (int)(1f + bDelta - gDelta)            ;
            else if (g == max) return (1f + (1f / 3f) + rDelta - bDelta) - (int)(1f + (1f / 3f) + rDelta - bDelta);
            else               return (1f + (2f / 3f) + gDelta - rDelta) - (int)(1f + (2f / 3f) + gDelta - rDelta);
        }
    }
    public static final IntComparator hueComparator = (o1, o2) -> Float.compare(hue(o1), hue(o2));

    public static void mixPalette (int doRemove, boolean doSort){
        com.github.tommyettinger.ds.IntSet removalSet = new com.github.tommyettinger.ds.IntSet(16);
        int size = mixingPalette.size();
        double closest = Double.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                double diff = HexGenerator.METRIC.difference(mixingPalette.get(i), mixingPalette.get(j));
                if(((mixingPalette.get(i) & 255) > 0 && (mixingPalette.get(j) & 255) > 0) && diff <= 30) {
                    System.out.printf("0x%08X and 0x%08X are very close!!\n", mixingPalette.get(i), mixingPalette.get(j));
                    float r, g, b, R, G, B;

                    r = (mixingPalette.get(i) >>> 24) / 255f;
                    g = (mixingPalette.get(i) >>> 16 & 255) / 255f;
                    b = (mixingPalette.get(i) >>> 8 & 255) / 255f;
                    R = r;
                    G = g;
                    B = b;

                    r = (mixingPalette.get(j) >>> 24) / 255f;
                    g = (mixingPalette.get(j) >>> 16 & 255) / 255f;
                    b = (mixingPalette.get(j) >>> 8 & 255) / 255f;
                    R += r;
                    G += g;
                    B += b;

                    removalSet.add(mixingPalette.get(i));
                    removalSet.add(mixingPalette.get(j));

                    int fusion = Color.rgba8888(R * 0.5f, G * 0.5f, B * 0.5f, 1f);
                    mixingPalette.add(fusion);
                    System.out.printf("Replacing close colors with their blend, %08X.\n", fusion);
                }
            }
        }
        mixingPalette.removeAll(removalSet);

        rgba.clear();
        rgba.addAll(mixingPalette);
        mixingPalette.clear();
        while (rgba.size() < LIMIT)
            add();
    }
}
