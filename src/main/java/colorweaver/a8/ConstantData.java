/*
 * Copyright (c) 2022  Tommy Ettinger
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package colorweaver.a8;

import colorweaver.BlueNoise;

import java.io.UnsupportedEncodingException;

/**
 * Meant to store large constant arrays as Strings that get converted to byte arrays, for internal use.
 * Right now it stores a large palette preload code, {@link #ENCODED_SNUGGLY}, as well as the blue noise data used by
 * BLUE_NOISE, CHAOTIC_NOISE, NEUE, SCATTER, DODGY, and WREN dithers.
 */
public class ConstantData {

    private ConstantData() {}
    /**
     * The encoded (many) bytes of the palette mapping for Snuggly255, a quasi-random-then-optimized palette.
     * Note: Uses the OklabCareful metric from ColorWeaver, not the simple RGB one used in PaletteReducer.
     */
    public static byte[] ENCODED_SNUGGLY;
    /**
     * A 16384-element byte array as a 64x64 grid of bytes. When arranged into a grid, the bytes will follow a blue noise
     * frequency (in this case, they will have a triangular distribution for its bytes, so values near 0 are much more
     * common). This is used to create {@link #TRI_BLUE_NOISE_MULTIPLIERS_A}.
     * <br>
     * While, for some reason, you could change the contents to some other distribution of bytes, I don't know why this
     * would be needed.
     */
    public static final byte[] TRI_BLUE_NOISE_A = BlueNoise.TRIANGULAR_BLUE_NOISE[0];
    /**
     * Like {@link #TRI_BLUE_NOISE_A}, this is a 16384-element byte array as a 64x64 grid of bytes. When arranged into a
     * grid, the bytes will follow a blue noise frequency (in this case, they will have a triangular distribution for
     * its bytes, so values near 0 are much more common). The possible byte values appear with the same frequency as in
     * {@link #TRI_BLUE_NOISE_A} and {@link #TRI_BLUE_NOISE_C}, but in a different order.
     * <br>
     * While, for some reason, you could change the contents to some other distribution of bytes, I don't know why this
     * would be needed.
     */
    public static final byte[] TRI_BLUE_NOISE_B = BlueNoise.TRIANGULAR_BLUE_NOISE[1];
    /**
     * Like {@link #TRI_BLUE_NOISE_A}, this is a 16384-element byte array as a 64x64 grid of bytes. When arranged into a
     * grid, the bytes will follow a blue noise frequency (in this case, they will have a triangular distribution for
     * its bytes, so values near 0 are much more common). The possible byte values appear with the same frequency as in
     * {@link #TRI_BLUE_NOISE_A} and {@link #TRI_BLUE_NOISE_B}, but in a different order.
     * <br>
     * While, for some reason, you could change the contents to some other distribution of bytes, I don't know why this
     * would be needed.
     */
    public static final byte[] TRI_BLUE_NOISE_C = BlueNoise.TRIANGULAR_BLUE_NOISE[2];

    static {
        try {
            ENCODED_SNUGGLY = "RRRRqqqRRRRRRqqqqqRRRRRRRqqqqUUUUUUUoooooUUUUUUUUooooo}}}}UUUUUUUUjjjjj}}}}}}VVVVVVVVjjjjjj}}}}}VVVVVVVVhhhhhhhzzzzzVVVVVVVVVhhhhhnnzzzzzz]]]]]]]]]ffffnnnnzzzzz]]]]]]]]]ffffffnvvvvv]]]]]]]]]fffffffvvvvvvWWWWWWWWWWWgggggvvvvvvv|||||YYYYYYYWWWWggggggguuuuu||||||YYYYYYYYYYYgggggggssssss||||YYYYYYYYYSSSeeeeeesssssssxxxXXXXXXXSSSSSeeeeeeesssssxxxxxxxXXXXXXXXXXXXeeeeeekkkkkkxxxxxyyyXXXXXXXXXXXXbbbbbkkkkkkkkyyyyyyy\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbkkkmmmmmyyyyy\\\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbmmmmmmmmwwww\\\\\\\\\\\\\\\\\\\\\\\\\\ccccccccmmmmmttttttZZZZZZZZZZZZZccccccccciittttttttZZZZZZZZZZZZZ`````cciiiiiiittrrrZZZZZZZZZZZZZ```````iiiiiiirrrrrRRRRqqqRRRRRRqqqqqRRRRRRRqqqqUUUUUUUoooooUUUUUUUUooooo}}}}UUUUUUUUjjjjj}}}}}}VVVVVVVVjjjjjj}}}}}VVVVVVVVhhhhhhhzzzzzVVVVVVVVVhhhhhnnzzzzzz]]]]]]]]]ffffnnnnzzzzz]]]]]]]]]ffffffnvvvvv]]]]]]]]]ffffffvvvvvvvWWWWWWWWWWWgggggvvvvvvv|||||YYYYYYYWWWWggggggguuuuu||||||YYYYYYYYYYYgggggggssssss||||YYYYYYYYSSSSeeeeeesssssssxxxXXXXXXXSSSSSeeeeeeesssssxxxxxxxXXXXXXXXXXXXeeeeeekkkkkkxxxxxyyyXXXXXXXXXXXXbbbbbkkkkkkkkyyyyyyy\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbkkkmmmmmyyyyy\\\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbmmmmmmmmwwww\\\\\\\\\\\\\\\\\\\\\\\\\\ccccccccmmmmmttttttZZZZZZZZZZZZZccccccccciittttttttZZZZZZZZZZZZZ`````cciiiiiiittrrrZZZZZZZZZZZZZ```````iiiiiiirrrrrRRRRRqqqqRRRRRRqqqqqRRRRRRRqqqqUUUUUUUoooooUUUUUUUUooooo}}}}UUUUUUUUjjjjj}}}}}}VVVVVVVjjjjjjj}}}}}VVVVVVVVhhhhhhhzzzzzVVVVVVVVVhhhhhnnzzzzzz]]]]]]]]]ffffnnnnzzzzz]]]]]]]]]ffffffnvvvvv]]]]]]]]]ffffffvvvvvvvWWWWWWWWWWWgggggvvvvvvv||||||YYYYYYYWWWWggggggguuuuu||||||YYYYYYYYYYYgggggggssssss||||YYYYYYYYSSSSeeeeeesssssssxxxXXXXXXXSSSSSeeeeeeesssssxxxxxxxXXXXXXXXXXXXeeeeeekkkkkkxxxxxyyyXXXXXXXXXXXXbbbbbkkkkkkkkyyyyyyy\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbkkkmmmmmyyyyy\\\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbmmmmmmmmwwww\\\\\\\\\\\\\\\\\\\\\\\\\\ccccccccmmmmmttttttZZZZZZZZZZZZZccccccccciittttttttZZZZZZZZZZZZZ`````cciiiiiiitrrrrZZZZZZZZZZZZZ```````iiiiiiirrrrrÂÂ§RRRRRqqqqRRRRRRqqqqqRRRRRRqqqqqUUUUUUUoooooUUUUUUUUooooo}}}}UUUUUUUUjjjjj}}}}}}VVVVVVVjjjjjjj}}}}}VVVVVVVVhhhhhhnzzzzzVVVVVVVVhhhhhhnnzzzzzz]]]]]]]]]ffffnnnnzzzz]]]]]]]]]ffffffnvvvvv]]]]]]]]]ffffffvvvvvvvWWWWWWWWWWWgggggvvvvvvv||||||YYYYYYYWWWWggggggguuuuu||||||YYYYYYYYYYYgggggggssssss||||YYYYYYYYSSSSeeeeeesssssssxxxXXXXXXXSSSSSeeeeeeesssssxxxxxxxXXXXXXXXXXXXeeeeeekkkkkkxxxxxyyyXXXXXXXXXXXXbbbbbkkkkkkkkyyyyyyy\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbkkkmmmmmyyyyy\\\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbmmmmmmmmwwww\\\\\\\\\\\\\\\\\\\\\\\\\\ccccccccmmmmmttttttZZZZZZZZZZZZZccccccccciittttttttZZZZZZZZZZZZZ`````cciiiiiiitrrrrZZZZZZZZZZZZZ```````iiiiiiirrrrrÂÂÂÂ³³³³ÂÂÂÂ³³³³ÂÂÂÂ³³³³§ÂÂÂ³³³§§§§§§§§¢¢RRRRRqqqqRRRRRRqqqqqRRRRRRqqqqqUUUUUUUoooooUUUUUUUoooooo}}}}UUUUUUUjjjjjj}}}}}}VVVVVVVjjjjjjj}}}}VVVVVVVVhhhhhhnzzzzzVVVVVVVVhhhhhnnnzzzzzz]]]]]]]]]ffffnnnnzzzz]]]]]]]]]ffffffnvvvvv]]]]]]]WWffffffvvvvvvvWWWWWWWWWWWgggggvvvvvvu||||||YYYYYYYWWWgggggggguuuuu||||||YYYYYYYYYYYgggggggssssss||||YYYYYYYSSSSSeeeeeesssssssxxxXXXXXXXSSSSSeeeeeeesssssxxxxxxxXXXXXXXXXXXXeeeeeekkkkkkxxxxyyyyXXXXXXXXXXXXbbbbbkkkkkkkkyyyyyyy\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbkkkmmmmmyyyyy\\\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbmmmmmmmmwwww\\\\\\\\\\\\\\\\\\\\\\\\\\ccccccccmmmmmttttttZZZZZZZZZZZZZccccccccciittttttttZZZZZZZZZZZZZ`````cciiiiiiitrrrrZZZZZZZZZZZZ````````iiiiiiirrrrrÂÂÂÂ³³³³³³§§§ÂÂÂÂ³³³³³³§§§§ÂÂÂÂÂ³³³³§§§§§ÂÂÂÂ³³³³§§§§§¢--³§§§§§¢¢§§§§¢¢¢¢¢¢¢RRRRRRRRRRRqqqqqRRRRRRqqqqqUUUUUUooooooUUUUUUUoooooo}}}}UUUUUUUjjjjjj}}}}}}VVVVVVVjjjjjjj}}}}VVVVVVVVhhhhhhnzzzzzVVVVVVVVhhhhhnnnzzzzzz]]]]]]]]fffffnnnnnzzz]]]]]]]]]ffffffvvvvvvWWWWWWWWWffffffvvvvvvvWWWWWWWWWWWgggggvvvvvuu||||||YYYYYYYWWWggggggguuuuuu||||||YYYYYYYYYYYgggggggssssss|||YYYYYYSSSSSSeeeeeessssssxxxxXXXXXXXSSSSSeeeeeeesssssxxxxxxxXXXXXXXXXXXXeeeeeekkkkkkxxxxyyyyXXXXXXXXXXXbbbbbbkkkkkkkkyyyyyyy\\\\\\\\\\\\\\\\\\\\bbbbbbbbbkkkmmmmmyyyyy\\\\\\\\\\\\\\\\\\\\\\\\bbbbbbbbmmmmmmmmwwww\\\\\\\\\\\\\\\\\\\\\\\\\\ccccccccmmmmtttttttZZZZZZZZZZZZZccccccccciittttttttZZZZZZZZZZZZZ``````ciiiiiiirrrrrZZZZZZZZZZZZ````````iiiiiiirrrrrúúúúÂÂÂÂÂ³³³³³§§§§§¬¬¬¢¢¢úúúúÂÂÂÂÂ³³³³³§§§§§¬¬¬¢¢¢¢úúúúÂÂÂÂÂ³³³³³§§§§§¬¬¬¢¢¢¢úúúúúÂÂÂÂ³³³³§§§§§§¬¬¬¢¢¢¢¢---Â³³³§§§§§§¬¬¬¢¢¢¢¢-----­­­­­§§§§§¢¢¢¢¢----­­­­­­¢¢¢¢RRRR­­­­RRRRRRqqqRRRR@@@qUUUUU@@oooooUUUUUUUoooooo}}}}UUUUUUUjjjjjj}}}}}VVVVVVVjjjjjjj}}}}VVVVVVVVhhhhhhnzzzzzVVVVVVVVhhhhhnnnzzzzzz]]]]]]]]fffffnnnnnzzz]]]]]]]]fffffffvvvvvvWWWWWWWWWffffffvvvvvvvWWWWWWWWWWWgggggvvvuuuu||||||YYYYYYYYWWggggggguuuuuu|||||YYYYYYYYYYSgggggggssssss|||YYYYYSSSSSSSeeeeeessssssxxxxXXXXXXXSSSSSeeeeeeesssssxxxxxxxXXXXXXXXXXXXeeeeeekkkkkkxxxxyyyyXXXXXXXXXXXbbbbbbkkkkkkkkyyyyyyy\\\\\\\\\\\\\\\\\\\\bbbbbbbbbkkmmmmmmyyyyy\\\\\\\\\\\\\\\\\\\\\\\\bbbbbbbmmmmmmmmwwwww\\\\\\\\\\\\\\\\\\\\\\\\\\cccccccccmmmtttttttZZZZZZZZZZZZZccccccccciiitttttttZZZZZZZZZZZZZ``````ciiiiiiirrrrrZZZZZZZZZZZZ````````iiiiiiirrrrrúúúúúÂÂÂ×³³³³³§§§§¬¬¬¬¬¢¢¢¢¢úúúúúÂÂ××³³³³³§§§§¬¬¬¬¬¢¢¢¢¢úúúúúÂ×××³³³³³§§§§¬¬¬¬¬¢¢¢¢¢úúúúúú××××³³³§§§§§¬¬¬¬¬¢¢¢¢¢-----×××­­­³§§§§§¬¬¬¬¢¢¢¢¢¢-----­­­­­­§§§§¬¬¬¬¢¢¢¢¢¢-----­­­­­­±±±¥¥¢¢¢¢RRRR­­­­­¥¥¥¥RRRR@@@@@@@@@UUU@@@@oooooUUUUUUUooooo\b}}}}UUUUUUjjjjjjj}}}}}VVVVVVGGjjjjj\t}}}VVVVVVVVhhhhhnnzzzzVVVVVVVVhhhhnnnnzzzzzz]]]]]]]Kfffffnnnnnz~~]]]]]]]]fffffffvvvvv~WWWWWWWWWffffffvvvvvvvWWWWWWWWWWWgggggvvuuuuu||||||YYYYYYYYWWggggggguuuuuu|||||YYYYYYYYYSSggggggsssssss|||YYYSSSSSSSSSeeeeeessssssxxxxXXXXXXXSSSSSeeeeeeessssxxxxxxxxXXXXXXXXXXXXeeeeekkkkkkkxxxyyyyyXXXXXXXXXXXbbbbbbkkkkkkkkyyyyyyy\\\\\\\\\\\\\\\\\\\\bbbbbbbbbkkmmmmmmyyyyy\\\\\\\\\\\\\\\\\\\\\\\\bbbbbbbmmmmmmmmwwwww\\\\\\\\\\\\\\\\\\\\\\\\ccccccccccmmmtttttttZZZZZZZZZZZZZcccccccciiiitttttttZZZZZZZZZZZZ```````iiiiiiiirrrrrZZZZZZZZZZZZ````````iiiiiiirrrrrúúúúú×××××ºººººº§¬¬¬¬¬¬¢¢¢¢¢ªªªªúúúúú×××××ºººººº§¬¬¬¬¬¬¢¢¢¢¢ªªªªúúúúú××××××ººººº§¬¬¬¬¬¬¢¢¢¢¢ªªªªúúúúúú×××××ºººº§§¬¬¬¬¬¬¢¢¢¢¢ªªªª-----ú××××­­ººº§§¬¬¬¬¬¬¢¢¢¢¢ªªªª-----è­­­­­­±±±±¬¬¬¬¥¢¢¢¢ªªªªª-----­­­­­±±±±±¥¥¥¥¥¥¢¢ªªª'''''­­­­±±±±¥¥¥¥¥¥@@@@@@¥¥@@@@@@@@@@@@@@@o\b\b\b\bUUUUU@@@j\b\b\b\b\b}}}GGGGGGGjjjjj\t\t}}}VVVGGGGGjjjj\t\t\t}}VVVVVVGGhhhhhnnnzzzVVVVVVVKhhhhnnnnnzzzz]]]]]KKKKfffnnnnn~~~~~]]]]]]]Kfffffffvvvv~~~WWWWWWWWWfffff[vvvvvvvWWWWWWWWWWgggggguuuuuuu||||||YYYYYYYYWgggggggguuuuuu|||||YYYYYYYYSSSggggggsssssss|||SSSSSSSSSSSSeeeeeessssssxxxxxXXXXXXXSSSSSeeeeeeessssxxxxxxxXXXXXXXXXXXXeeeeekkkkkkkxxyyyyyyXXXXXXXXXXbbbbbbbkkkkkkkmyyyyyyy\\\\\\\\\\\\\\\\\\\\bbbbbbbbbkmmmmmmmwwwww\\\\\\\\\\\\\\\\\\\\\\\\bbbbbb^mmmmmmmmwwwww\\\\\\\\\\\\\\\\\\\\\\\\ccccccccccmmmtttttttZZZZZZZZZZZZ`cccccccciiiitttttttZZZZZZZZZZZZ```````iiiiiiirrrrrrZZZZZZZZZZZZ````````iiiiiiirrrrrúúúúú×××××ººººººº¬¬¬¬¬¬¬¢¢¢ªªªªªúúúúú×××××ººººººº¬¬¬¬¬¬¬¢¢¢ªªªªªúúúúú××××××ºººººº¬¬¬¬¬¬¢¢¢¢ªªªªªúúúúú××××××ºººººº¬¬¬¬¬¬¢¢¢¢ªªªªª----ùùè××××ººººº±¶¬¬¬¬¥¥¢¢¢ªªªªª-----èèèè­­­­±±±±±±¬¥¥¥¥¥¢ªªªªªª''''''èèèè­­­±±±±±±¥¥¥¥¥¥¥ªªªªª''''''­­­­±±±±¥¥¥¥¥¥@@@@@@¥¥¥@@@@@@@@@@@@@@@\b\b\b\b\b¨UUU@@@@@\b\b\b\b\b\b}}¨¨¨¨¨GGGGGGGGjjj\t\t\t\t}}GGGGGGGGGjj\t\t\t\t\tVVVGGGGGhhhhnnnnzzzKKKKKKKKhhhnnnnnnzzz~KKKKKKKKKfffnnnnn~~~~~]]]]KKKKKfffff[vvv~~~~WWWWWWWWWfff[[[[vvvvv~WWWWWWWWWWgggggguuuuuu||||YYYYYYYYJJggggggguuuuuu|||||YYYYYYSSSSSSgggg_sssssss||SSSSSSSSSSSSeeeeesssssssxxxxxXXXXXXXSSSSSeeeeeeessssxxxxxxxXXXXXXXXXXXLeeeeekkkkkkkxyyyyyyyXXXXXXXXXXbbbbbbbkkkkkkkmyyyyyyy\\\\\\\\\\\\\\\\\\OObbbbbbbbkmmmmmmwwwwww\\\\\\\\\\\\\\\\\\\\\\\\bbbbb^^^mmmmmmmwwwww\\\\\\\\\\\\\\\\\\\\\\\\ccccccccccmmttttttttZZZZZZZZZZZZ``ccccccciiiitttttttZZZZZZZZZZZZ```````iiiiiiirrrrrrZZZZZZZZZZZ`````````iiiiiirrrrrrùùùùù×××××ººººººº¶¶¶¬¬¬¬¢¢ªªªªªªùùùùù×××××Ýºººººº¶¶¶¬¬¬¬¢¢ªªªªªªùùùùùù××××Ýºººººº¶¶¶¬¬¬¬¢¢ªªªªªªùùùùùù×××ÝÝººººº¶¶¶¶¬¬¬¥¢¢ªªªªªª--ùùùùèèèÝÝºººº±±¶¶¶¶¥¥¥¥¥ªªªªªª'''''èèèèèè­±±±±±±¶¶¥¥¥¥¥¥ªªªªªª''''''èèèèè­­±±±±±±¥¥¥¥¥¥¥ªªªª'''''''èèè­±±±±±¥¥¥¥¥¥@@@@@@¥¥¥@@@@@@@\b\b@@@@@@@@\b\b\b\b\b¨¨¨¨44444444\b\b\b\b\b\b\t¨¨¨¨¨¨¦¦¦GGGGGGGGjj\t\t\t\t\t\tGGGGGGGGGG\t\t\t\t\t\t\tGGGGGGGGGhhnnnnnzzzKKKKKKKKKhhnnnnnn~~~~~KKKKKKKKKfffnnnn~~~~~~~KKKKKKKKKfff[[[[vv~~~~~WWWWWWWWWW[[[[[[vvvvuWWWWWWWWJJggggg[uuuuuu|YYYYYYYJJJgggggguuuuuuu||||YYYYSSSSSSSSgg____ssssss||SSSSSSSSSSSSeeeee_ssssssxxxxXXXXXXXSSSSeeeeeeekkspppxxxxxXXXXXXXXXXLLLeeekkkkkkkppyyyyyyyXXXXXXOOOObbbbbbbkkkkkkkmyyyyyyy\\\\\\\\\\\\\\\\OOObbbbbbb^mmmmmmmwwwwww\\\\\\\\\\\\\\\\\\\\\\\\bbb^^^^^mmmmmmwwwwww\\\\\\\\\\\\\\\\\\\\MMccccccccccmmttttttttZZZZZZZZZZZZ``cccccciiiiittttttrZZZZZZZZZZZ````````iiiiiiirrrrrrZZZZZZZZZZZ`````````iiiiiirrrrrrùùùùùùÝÝÝÝÝÝºººº¶¶¶¶¶¶µµµµªªªªªªùùùùùùÝÝÝÝÝÝºººº¶¶¶¶¶¶µµµµªªªªªªùùùùùùÝÝÝÝÝÝºººº¶¶¶¶¶¶µµµµªªªªªªùùùùùùùÝÝÝÝÝºººº¶¶¶¶¶¶µµµµªªªªªªùùùùùùèèÝÝÝÝººº±¶¶¶¶¶¶µµµµªªªªªª'''''èèèèèèÝ±±±±±±¶¶¶¥¥¥¥¥ªªªªªª''''''èèèèèèÖ±±±±±±¥¥¥¥¥¥¥ªªª'''''''èèèèÖÖÖ±±±±¥¥¥¥¥¥@@@@''¯¯¯¯@@@@@@@\b\b\b\b\b¨¨¯4444444\b\b\b\b\b\b\b¨¨¨¨¨¨¦¦¦¦44444444\b\b\b\b\b\t\t¨¨¨¨¨¨¨¦¦¦¦GGGGGGGGG\t\t\t\t\t\t\t\t¤¤¤GGGGGGGGGG\n\n\n\n\n\n\n    ¤¤¤GGGGGGGGG9hnn\n\n\n     KKKKKKKKK9nnnnnn~~~~~KKKKKKKKKKff[nnn~~~~~~~KKKKKKKKKK[[[[[[v~~~~~~WWWWWWWWJ[[[[[[[[uuuuWWWJJJJJJJJgg[[[uuuuuuYYYYYJJJJJJggggguuuuuuu|||SSSSSSSSSSSSe_____ssssssxxSSSSSSSSSSSSeeee__sssssxxxxXXXXXXXSSLLLeeeeeekkppppxxxxXXXXXXXXXLLLLbeekkkkkkkppyyyyyyyOOOOOOOOOOObbbbbbkkkkkkmmyyyyyyy\\\\\\\\\\\\OOOOObbbbb^^^mmmmmmmwwwwww\\\\\\\\\\\\\\\\\\\\\\Ob^^^^^^^mmmmmmwwwwww\\\\\\\\\\\\\\MMMMMccccccccccmtttttttttZZZZZZZZZZZM```ccccciiiiitttttrrZZZZZZZZZZZ````````iiiiiiirrrrrrZZZZZZZZZNN`````````iiiiiirrrrrrùùùùùùÝÝÝÝÝÝÈÈÈÈ¶¶¶¶¶¶µµµµµµªªªªùùùùùùÝÝÝÝÝÝÈÈÈÈ¶¶¶¶¶¶µµµµµµªªªªùùùùùùÝÝÝÝÝÝÈÈÈÈ¶¶¶¶¶¶µµµµµµªªªªùùùùùùùÝÝÝÝÝÈÈÈÈ¶¶¶¶¶¶µµµµµµªªªªùùùùùùèèÝÝÝÝÈÈÈÈ¶¶¶¶¶µµµµµµªªªªª'''''èèèèèèÝÈÈÈ±±¶¶¶¶µµµµµµ°°°°°''''''øèèèèÖÖÖ±±»»»»»¥¥µµµ°°°°''''''øøøøÖÖÖÖÖ»»»»»»¯¯¯¯¯¯%%%%%%%øøøÖÖÖÖÖÖ»»»»¯¯¯¯¯¯¯44444%%\b\b\b\b\b¨¨¨¨¨¨¯¯¯¯¯¦¦¦¦¦44444444\b\b\b\b\b\b¨¨¨¨¨¨¨¨¦¦¦¦¦44444444\b\b\b\t\t\t\t¨¨¨¨¨¨¤¤¤¤¦¦¦¦¦GGGGGGGG&\t\t\t\n\n\n\n     ¤¤¤¤¤¤GGGGGGGGG\n\n\n\n\n\n\n      ¤¤¤¤GGGGG999999\n\n\n     KKKKKKK9999nn~~~~KKKKKKKKKK[[[[~~~~~~KKKKKKKKK[[[[[[[[~~~~~JJJJJJJJJJ[[[[[[[uuuJJJJJJJJJJJg[[[[uuuuuJJJJJJJJJJJJg____uuuuuSSSSSSSSSSSS_______sssssxxSSSSSSSSSSSeee_____sspppxxXXXXXLLLLLLLeeeeekkkpppppxxXXXXXXXLLLLLLLbkkkkkkkpppyyyyyyyOOOOOOOOOOObbbbbbkkkkkmmmyyyywww\\\\\\OOOOOOOOObb^^^^^^mmmmmwwwwwww\\\\\\\\\\\\\\\\\\OOO^^^^^^^^mmmmmmwwwwwwMMMMMMMMMMMMMccccccccidtttttttttZZZZZZZZZMMM````ccciiiiiitttrrrrZZZZZZZZZZ`````````iiiiiiirrrrrrZZZZNNNNNNNN````````iiiiiirrrrrrùùùùùùÝÝÝÝÝÝÈÈÈÈÈ¶¶¶¶µµµµµµµ°°°°ùùùùùùÝÝÝÝÝÝÈÈÈÈÈ¶¶¶¶µµµµµµµ°°°°ùùùùùùÝÝÝÝÝÝÈÈÈÈÈ¶¶¶¶µµµµµµµ°°°°ùùùùùùùÝÝÝÝÝÈÈÈÈÈ¶¶¶¶µµµµµµ°°°°°ÿÿÿÿÿÿÿèÝÝÝÝÈÈÈÈÈ¶¶¶¶µµµµµµ°°°°°'''ÿÿøøøèèáÖÖÈÈÈ»»»»¿µµµµµ°°°°°°''''øøøøøøÖÖÖÖÖ»»»»»»¯¯¯¯¯°°°°°°%%%%%øøøøøÖÖÖÖÖ»»»»»»¯¯¯¯¯¯°°°°°%%%%%%%øøøÖÖÖÖÖÖ»»»»¯¯¯¯¯¯¯¦¦¦¦¦4444%%%%\b\bìììÖÖ¨¨¨¨¨¯¯¯¯¯¯¦¦¦¦¦¦44444444\b\b\bìììì¨¨¨¨¨¨¤¤¤¤¦¦¦¦¦¦44444444&&\t\t\t\t\t¨¨¨¨¨¨¤¤¤¤¤¤¦¦¦¦¦GGGGGGG&&&\n\n\n\n\n\n     ¤¤¤¤¤¤GGGGG99999\n\n\n\n\n\n      ¤¤¤¤99999999999     KKKKKK99999~~~KKKKKKKKK[[[[[~~~~~~KKKKKK>>>[[[[[[[\f\f~~~JJJJJJJJJJ[[[[[[[uuuJJJJJJJJJJJJ[[[[uuuuuJJJJJJJJJJJJ______uuuuSSSSSSSSSSF________sssspSSSSSSSSSLLLe______ppppppLLLLLLLLLLLLLeee_kkpppppppOOOOOOLLLLLLLLbPkkkkkpppppyyyyOOOOOOOOOOOObbb^^^kkkmmmmwwwwwwwOOOOOOOOOOOO^^^^^^^^ddddmwwwwwww\\\\\\\\\\\\\\\\OOOM^^^^^^^^dddddtwwwwwwMMMMMMMMMMMMMccccccccidtttttttttZZZZZZZZMMMM`````cciiiiiitrrrrrrZZZZZZZZZN`````````iiiiiirrrrrrrNNNNNNNNNNNN```````iiiiiirrrrrrrÿÿÿÿÿÿÿÝÝáááÈÈÈÈÈÈ¿¿¿¿µµµµµ°°°°°ÿÿÿÿÿÿÿÝÝáááÈÈÈÈÈÈ¿¿¿¿µµµµµ°°°°°ÿÿÿÿÿÿÿÝááááÈÈÈÈÈÈ¿¿¿¿µµµµµ°°°°°ÿÿÿÿÿÿÿáááááÈÈÈÈÈÈ¿¿¿¿µµµµµ°°°°°ÿÿÿÿÿÿÿøáááááÈÈÈÈÈ¿¿¿¿µµµµ°°°°°°ÿÿÿÿÿøøøøááááÈÈÈ»»»¿¿¿µµµµ°°°°°°%%%%øøøøøøÖÖÖÖÖ»»»»»»¯¯¯¯¯°°°°°°%%%%%%øøøøÖÖÖÖÖ»»»»»»¯¯¯¯¯¯°°°°°%%%%%%%øøøìÖÖÖÖÖ»»»»¯¯¯¯¯¯¯¦¦¦¦¦44%%%%%%ììììììì¨¨¨¨¨¯¯¯¯¯¯¦¦¦¦¦¦4444444&&&ììììì¨¨¨¨¨¨¤¤¤¤¤¦¦¦¦¦¦444444&&&&&\nìììÁÁÁÁÁ¤¤¤¤¤¤¤¦¦¦¦¦GGGG99&&&&&\n\n\n\n\n     ¤¤¤¤¤¤9999999999\n\n\n\n\n      ¤¤¤¤99999999999     KKKKK999999~~~KKKKK>>>>[[[[[\f\f~~¡¡¡>>>>>>>>>[[[[[[[\f\f\f\f¡¡¡JJJJJJJJJJ[[[[[[\f\f\f\fJJJJJJJJJJJJ[[[[uuuuJJJJJJJJJJJ________u\r\rSSSSSSFFFFF_________ppppLLLLLLLLLLLL_______ppppppLLLLLLLLLLLLLLPPPPppppppppOOOOOOOLLLLLLLPPPPPkppppppyOOOOOOOOOOOO^^^^^^^kdddddwwwwwwwOOOOOOOOOOOO^^^^^^^ddddddwwwwwwwMMMMMMMMMMMM^^^^^^^^ddddddwwwwwwMMMMMMMMMMMMMMccccccidddtttttt{{ZZZZZZMMMMMM`````QQiiiiiirrrrrrrZZZZNNNNNNN````````iiiiiirrrrrrrNNNNNNNNNNNNN``````TTiiiirrrrrrrÿÿÿÿÿÿÿáááááÈÈÈÈÈ¿¿¿¿¿¿µµµµ°°°°°ÿÿÿÿÿÿÿááááááÈÈÈÈ¿¿¿¿¿¿µµµµ°°°°°ÿÿÿÿÿÿÿááááááÈÈÈÈ¿¿¿¿¿¿µµµµ°°°°°ÿÿÿÿÿÿÿïáááááÈÈÈÈ¿¿¿¿¿¿¿µµ°°°°°°ÿÿÿÿÿÿÿïáááááÈÈÈÈ¿¿¿¿¿¿¿µµ°°°°°°ÿÿÿÿÿÿøøøáááááÈ»»»»¿¿¿¿¿µ°°°°°°°%%%%øøøøøøÖÖÖÖÖ»»»»»¿¿¯¯¯¯°°°°°°%%%%%%øøøøÖÖÖÖÖ»»»»»»¯¯¯¯¯¯°°°°¦%%%%%%%%øììììÖÖÖ»»»»¯¯¯¯¯¯¯¦¦¦¦¦%%%%%%%%ìììììììÁÁÁÁÁÁ¯¸¸¸¸¦¦¦¦¦¦44444&&&&&ìììììÁÁÁÁÁÁ¤¤¤¤¸¦¦¦¦¦¦44444&&&&&&&ìììÁÁÁÁÁÁ¤¤¤¤¤¤¦¦¦¦¦999999&&&&&\n\n\nôô     ¤¤¤¤¤¤9999999999&\nôôô      ¤¤²²²9999999999.    ²KK99999999..¡¡¡©©©©>>>>>>>>>>[[[[\f\f\f\f\f¡¡¡¡¡¡>>>>>>>>>>[[[[[\f\f\f\f\f¡¡¡¡¡JJJJJJJJJJ[[[[[[\f\f\f\fJJJJJJJJJJJ======\r\r\r\rFFFFFFFFFFF________\r\r\rFFFFFFFFFFF_________ppppLLLLLLLLLLLL______pppppppLLLLLLLLLLLLLPPPPPPpppppppOOOOOOOLLLLLLPPPPPPPppppppOOOOOOOOOOOO^^^^^^^ddddddwwwwwwOOOOOOOOOOO^^^^^^^^ddddddwwwwwwwMMMMMMMMMMMM^^^^^^^dddddddwwwww{MMMMMMMMMMMMMMQQQQQQQddaatttt{{{ZZZMMMMMMMMM````QQQQiiiiarrrrrrrNNNNNNNNNNNN```````iiiiiirrrrrrrNNNNNNNNNNNNNN````TTTTTTTrrrrrrlÿÿÿÿÿÿïïáááááÈÈÓÓÓ¿¿¿¿¿¿½½½°°°°°ÿÿÿÿÿÿïïáááááÈÈÓÓÓ¿¿¿¿¿¿½½½°°°°°ÿÿÿÿÿÿïïááááááÓÓÓÓ¿¿¿¿¿¿½½½°°°°°ÿÿÿÿÿÿïïïáááááÓÓÓÓ¿¿¿¿¿½½½½°°°°°ÿÿÿÿÿÿïïïáááááÓÓÓÓ¿¿¿¿¿½½½½°°°°°ÿÿÿÿÿÿøïïïáááâÓÓÓÓ¿¿¿¿¿½½½½°°°°·%%%øøøøïââââââ»»»Ê¿¿½½½½½·····%%%%øøøââââââ»ÊÊÊÊÊ¸¸¸¸¸·····%%%%%ììììâââÁÁÊÊÊ¸¸¸¸¸¸¸¦¦¦¦%%%%%ìììììììÁÁÁÁÁÁ¸¸¸¸¸¸¦¦¦¦¦,,,,,&&&&&ìììììÁÁÁÁÁÁ¤¸¸¸¸¸¦´´´´,,,,,,&&&&&&ìôôÁÁÁÁÁÁ¤¤¤¤¤¤´´´´´99,,,,,&&&&ôôôôôô    ¤¤²²²²²´´´´999999999..ôôôôôô   ÆÆ²²²²²²999999999...ÆÆÆÆÆ²²²²²©©©©>>>>>>>9.....¡¡¡¡¡¡©©©©©©>>>>>>>>>>...\f\f\f\f\f\f¡¡¡¡¡¡®®®©©©©>>>>>>>>>>[[[[\f\f\f\f\f\f¡¡¡¡¡®®JJJJJJJJJ=======\f\f\f\r\rJJJJJJJJJJ======\r\r\r\r\r\rFFFFFFFFFFF_______\r\r\r\rFFFFFFFFFFF________pppppLLLLLLLLLLLL______pppppppLLLLLLLLLLLLLPPPPPPpppppppOOOOOOOOLLLLLPPPPPPPpppppOOOOOOOOOOO^^^^^^^dddddddwwwwwOOOOOOOOOOO^^^^^^^^dddddddwwwwwwMMMMMMMMMMMM^^^^^^^dddddddww{{{{MMMMMMMMMMMMMQQQQQQQQaaaaaaa{{{{MMMMMMMMMMMM``QQQQQQiiiaaarrrrrrNNNNNNNNNNNNN````TTTTTiiirrrrrrrNNNNNNNNNNNNNNN`TTTTTTTTTlllllllÿÿÿÿÿïïïïááááÓÓÓÓÓÓ¿¿¿¿½½½½½····ÿÿÿÿÿïïïïááááÓÓÓÓÓÓ¿¿¿¿½½½½½····ÿÿÿÿÿïïïïááááÓÓÓÓÓÓ¿¿¿¿½½½½½····ÿÿÿÿÿïïïïïáááÓÓÓÓÓÓ¿¿¿¿½½½½½····ïïïïïïááÓÓÓÓÓÓ¿¿¿½½½½½½····ïïïïïââââÓÓÓÓÊ¿¿½½½½½·····ïïïââââââÊÊÊÊÊÊ½½½½½·····âââââââÊÊÊÊÊÊ¸¸¸¸¸·····ìììââââÁÊÊÊÊ¸¸¸¸¸¸¸·´´´,,,ìììììÁÁÁÁÁÁÁ¸¸¸¸¸¸¸´´´´,,,,,,&&&&ììììÁÁÁÁÁÁÁ¸¸¸¸¸¸´´´´´,,,,,,,&&&&ôôôôæÁÁÁÁÁÏ¤¤²²´´´´´´,,,,,,,,&&ôôôôôôôÆÆÆÆ²²²²²²²´´´´99999999...ôôôôôôÆÆÆÆÆ²²²²²²²©©©9999999......ÆÆÆÆÆÆ²²²²©©©©©>>>>>>>>......\f\f\f¡¡¡¡¡¡¡®©©©©©©>>>>>>>>>>...\f\f\f\f\f\f¡¡¡¡¡¡®®®©©©©>>>>>>>>>=====\f\f\f\f\f\f¡¡¡¡®®®®®JJJJJJJJ========\r\r\r\r\r\rFFFFFFFFF=======\r\r\r\r\r\rFFFFFFFFFFF_______\r\r\r\rFFFFFFFFFFFF_______ppLLLLLLLLLLLLPPPPPPpppppppLLLLLLLLLLLLPPPPPPPppppppOOOOOOOOEEEEEPPPPPPPPpppOOOOOOOOOOO^^^^^^^dddddddwwwwOOOOOOOOOO^^^^^^^^ddddddddwww{{{MMMMMMMMMMMM^^^^^^Qddddddd{{{{{{MMMMMMMMMMMMMQQQQQQQQaaaaaaa{{{{NNNNNNNMMMMM`QQQQQQQQaaaaaarrrrrNNNNNNNNNNNNN```TTTTTTTTarllllllNNNNNNNNNNNNNNNTTTTTTTTTTlllllllïïïïïááàÓÓÓÓÓÓÓ¿¿½½½½½½····ïïïïïááàÓÓÓÓÓÓÓ¿¿½½½½½½····ïïïïïïáàÓÓÓÓÓÓÓ¿¿½½½½½½····ïïïïïáààÓÓÓÓÓÓ¿¿½½½½½½····ïïïïïâààÓÓÓÓÓÓ¿½½½½½½·····ïïïïââââÓÓÓÊÊÊÊ½½½½½·····ïïââââââÊÊÊÊÊÊ½½½½½·····õââââââÊÊÊÊÊÊ¸¸¸¸¸·····õõâââââÊÊÊÊÊ¸¸¸¸¸¸¸´´´´,,,,ììììææÁÁÁÁÏÏ¸¸¸¸¸¸´´´´´,,,,,,,&&ìææææÁÁÁÏÏÏÏ¸¸¸´´´´´´,,,,,,,,&ôôôæææÁÁÏÏÏÏ²²²´´´´´´,,,,,,,,,ôôôôôôôÆÆÆÆÆ²²²²²²´´´´///////.....ôôôôÆÆÆÆÆÆ²²²²²²©©©©///////.......ôôÆÆÆÆÆÆÆ²²²²©©©©©>>>>>///......\f\f\f\f¡¡¡¡¡¡®®®©©©©©>>>>>>>>>>...\f\f\f\f\f\f¡¡¡¡¡®®®®®©©©>>>>>>>>======\f\f\f\f\f\f¡¡¡¡®®®®®JJJ33333========\r\r\r\r\r\rFFFFFFFFF=======\r\r\r\r\r\rFFFFFFFFFFF_____6\r\r\r\rFFFFFFFFFFFF____66«LLLLLLLLLLLPPPPPPPPppppLLLLLLLLLLEEPPPPPPPPppppOOOOEEEEEEEEEPPPPPPPdddOOOOOOOOOEE^^^^^^^dddddddwwwMMMMMMMMDDD^^^^^^^dddddddd{{{{{{MMMMMMMMMMMMQQQQQQQdddddaa{{{{{{MMMMMMMMMMMMQQQQQQQQQaaaaaaa{{{{NNNNNNNNNNNNQQQQQQQQQaaaaaaallllNNNNNNNNNNNNNNHTTTTTTTTTalllllllNNNNNNNNNNNNNNHTTTTTTTTTTlllllllïïïïïàààÓÓÓÓÓÓÅÅ½½½½½½····ïïïïïààààÓÓÓÓÓÅÅ½½½½½½····ïïïïïààààÓÓÓÓÓÅÅÅ½½½½½····ïïïïïààààÓÓÓÓÓÅÅÅ½½½½·····ïïïïàààààÓÓÓÊÅÅÅ½½½½·····ïïâââàààÊÊÊÊÊÅÅÅ½½½·····õõââââââÊÊÊÊÊÊÅÅ½½······õõõâââââÊÊÊÊÊÊ¸¸¸¸¸·····õõõõõâââÊÊÊÊÊÊ¸¸¸¸¸´´´´´,,,,õõõæææææÏÏÏÏÏ¸¸¸¸¸´´´´´,,,,,,,ææææææÏÏÏÏÏÏ¸À´´´´´´,,,,,,,ôæææææÏÏÏÏÏ²²²´´´´´´,,,,,,,,ôôôôôÆÆÆÆÆÆ²²²²²²´´´´///////.....ôôôôÆÆÆÆÆÆ²²²²²²©©©©////////......îîîîÆÆÆÆÆ²²²©©©©©©////////......\f\fîîî¡¡¡¡®®®®©©©©©>>>>>>>>>===.\f\f\f\f\f\f¡¡¡¡®®®®®®®©©333333333=====  \f\f\r\rÚÚÚ®®®®®®333333333====== \r\r\r\r\r\rFFFFFFFFF======\r\r\r\r\r\r\rFFFFFFFFFFF6666666\r«««FFFFFFFF8888666666««««LLLLLLLL888PPPPPPPPEEEEEEEEEEEEPPPPPPPPppEEEEEEEEEEEEEPPPPPPPddOOOOOODDDDDD^^^^^ddddddddd{{{{{{MMMMMMDDDDDD^^^^^dddddddd{{{{{{{MMMMMMMMMMMMQQQQQQQddaaaaa{{{{{{MMMMMMMMMMMQQQQQQQQQaaaaaaaa{{{{NNNNNNNNNNNNQQQQQQQTTaaaaaalllllNNNNNNNNNNNNHHHTTTTTTTTTllllllllNNNNNNNNNNNNHHHTTTTTTTTTTlllllllïíííàààààÓÓÓÅÅÅÅÅÅ½½·····ïíííàààààÓÓÓÅÅÅÅÅÅ½½·····ííííàààààÓÓÓÅÅÅÅÅÅ½½·····ííííààààààÓÙÅÅÅÅÅÅÅ½·····ííííààààààÙÙÙÅÅÅÅÅÅ½¾····íííííàààààÊÊÊÅÅÅÅÅÅ¾¾¾¾··õõõõõââààÊÊÊÊÊÅÅÅÅÅ¾¾¾¾¾¾õõõõõõâââÊÊÊÊÊÊÅÅÀÀ¾¾¾¾¾¾õõõõõæææåÏÏÏÏÏÀÀÀÀÀÀ´´´´õõõæææææÏÏÏÏÏÏÀÀÀÀ´´´´´,,,,,,æææææÏÏÏÏÏÏÀÀÀÀ´´´´´,,,,,,æææææÏÏÏÏÏÏ²ÀÀ´´´´´////!!!!ôôôôÆÆÆÆÆÆ²²²²²²¼¼¼¼////////....ôôîîîÆÆÆÆÆÃÃÃÃÃ©©©©©////////.....îîîîîîÆÆÆÃÃÃÃÃ©©©©©/////////....îîîîîî¡¡®®®®®®©©©©333333333==      \fÚÚÚÚÚ®®®®®®®¹¹333333333====     \rÚÚÚÚÚ®®®®333333333=====  \r\r\r\r\rÚÚFFFFFFFF3====666\r\r\r\r\r\rFFFFFFF88886666666«««««888888888888666666Ô«««««EEEEEEE88888PPPPPPPEEEEEEEEEEEEPPPPPPPPEEEEEEEEEEEEEPPPPPPdd£DDDDDDDDDDDDD^^BBBBddddd{{{{{{DDDDDDDDDDDDDD^BBBBdddddd{{{{{{{MMMMMMMMMMDQQQQQQQQQaaaaaa{{{{{{MMMMMMMMMMMQQQQQQQQQaaaaaaaa{{{{NNNNNNNNNHHHHQQQQQTTTTaaaallllllNNNNNNNNNHHHHHHTTTTTTTTTllllllllNNNNNNNNNNHHHHHTTTTTTTTTllllllllíííííààààààÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾íííííààààààÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾íííííààààààÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾ííííííààààÙÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾ííííííààààÙÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾þþþþíííííààààÙÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾þõõõõõõàààÙÙÙÙÙÅÅÅÅÅ¾¾¾¾¾¾õõõõõõååååÙÙÙÙÅÀÀÀÀÀ¾¾¾¾¾õõõõõæååååÏÏÏÏÀÀÀÀÀÀ¾¾´´õõææææåÏÏÏÏÏÀÀÀÀÀÀ´´´´!!!!!!æææææÏÏÏÏÏÏÀÀÀÀÀ¼¼¼¼!!!!!!!æææææÏÏÏÏÏÀÀÀÀ¼¼¼¼¼!!!!!!!!!îîîîÆÆÆÆÃÃÃÃÃÃ¼¼¼¼¼////////îîîîîîÆÆÃÃÃÃÃÃÃ©©©¼////////îîîîîîîÃÃÃÃÃÃÃ©©©©/////////îîîîîîÚÚ®®®®®®¹¹¹¹3333333333        ÚÚÚÚÚ®®®®®®¹¹¹3333333333==      ÚÚÚÚÚÚÚ®®®3333333333===    \r\r\rÚÚÚÚFFFFFF88336666666\r\r\r\r«««««888888888886666666ÔÔ««««««888888888888666666ÔÔ«««««EEEEEEE88888PPPPPP££EEEEEEEEEEEEPPPPPPP£££EEEEEEEEEEEEEBBBBBBB££££DDDDDDDDDDDDDBBBBBBBddd{{{{{DDDDDDDDDDDDDDQBBBBBddaa{{{{{{MMMMMMMMDDDQQQQQQQQaaaaaaa{{{{{{MMMMMMMMH?QQQQQQQQQQaaaaaaaa{{{{NNNNNHHHHHHHHHQQTTTTTTaaalllllllNNNNNNHHHHHHHHHTTTTTTTTTllllllllNNNNNNNHHHHHHHHTTTTTTTTTllllllllþþþþþþííííííààààÙÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾þþþþþþííííííààààÙÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾þþþþþþííííííààààÙÙÙÙÅÅÅÅÅÅ¾¾¾¾¾¾þþþþþþþíííííààààÙÙÙÙÙÅÅÅÅÅ¾¾¾¾¾¾þþþþþþþíííííààààÙÙÙÙÙÅÅÅÅÅ¾¾¾¾¾¾þþþþþþþþíííííäääÙÙÙÙÙÅÅÅÅ¾¾¾¾¾¾¾þþõõõõõõåååÙÙÙÙÙÙÅÅÀÀ¾¾¾¾¾¾õõõõõõåååååÙÙÙÀÀÀÀÀÀ¾¾¾¾¾õõõõõåååååÏÏÏÀÀÀÀÀÀÀÀ¾¾¼õææååååÏÏÏÏÀÀÀÀÀÀ¼¼¼¼!!!!!!!öææææåÏÏÏÏÀÀÀÀÀ¼¼¼¼¼!!!!!!!!öööæææÞÞÞÞÞÃÃÀ¼¼¼¼¼¼!!!!!!!!!!ööîîîîÆÆÃÃÃÃÃÃÃ¼¼¼¼¼///////îîîîîîîÃÃÃÃÃÃÃÃ¼¼¼¼///////îîîîîîîÃÃÃÃÃÃÃ¹¹¹¹//////)) îîîîÚÚÚÚ®®®®¹¹¹¹¹3333333333       ÚÚÚÚÚÚÚ®®®¹¹¹¹¹3333333333        ÚÚÚÚÚÚÚÇÇÇ¹¹¹¹3333333333==     \r\r\rÚÚÚÚÇÇÇÇ«888888882266666666ÔÔÔÔ««««««888888888886666666ÔÔÔÔ«««««888888888888666666ÔÔÔ«««««EEEEEEEE8888PPPPP###£££££EEEEEEEEEEEEPPPPPPP£££££EEEEEEEDDDDDBBBBBBBB£££££DDDDDDDDDDDDDBBBBBBBBd{{{{DDDDDDDDDDDDDQBBBBBBBaaa{{{{{{????????????QQQQQQQaaaaaaa{{{{{{HHHHHHHHHH?QQQQQQQQaaaaaaaaa{{HHHHHHHHHHHHHHHTTTTTTTaallllllllHHHHHHHHHHHHHHHTTTTTTTTTllllllllHHHHHHHHHHHHHHHTTTTTTTTTllllllllþþþþþþþíííííääääÙÙÙÙÙÅÅÅÅÌ¾¾¾¾¾¾þþþþþþþíííííääääÙÙÙÙÙÅÅÅÅÌ¾¾¾¾¾¾þþþþþþþíííííääääÙÙÙÙÙÅÅÅÌÌ¾¾¾¾¾¾þþþþþþþíííííääääÙÙÙÙÙÅÅÅÌÌ¾¾¾¾¾¾þþþþþþþþííííääääÙÙÙÙÙÙÅÌÌÌ¾¾¾¾¾¾þþþþþþþþííííääääÙÙÙÙÙÙÌÌÌÌÌ¾¾¾¾¾þþþþõõõõåååååÙÙÙÙÙÌÌÌÌÌ¾¾¾¾¾õõõõõåååååååÙÙÀÀÀÀÀÀÀ¾¾¾¾õõõõåååååååÞÞÀÀÀÀÀÀÀÀ¼¼¼!!!!!öööåååååÞÞÞÞÀÀÀÀÀÀ¼¼¼¼!!!!!!!!öööööååÞÞÞÞÞÞÀÀÀÀ¼¼¼¼¼!!!!!!!!!öööööööÞÞÞÞÞÞÃÃÃ¼¼¼¼¼¼!!!!!!!!!ööööîîîîÞÞÃÃÃÃÃÃ¼¼¼¼¼//////îîîîîîîÃÃÃÃÃÃÃÃ¼¼¼¼)))))))îîîîîîÜÜÃÃÃÃÃ¹¹¹¹¹))))))))) îîîÚÚÚÚÜÜÜ®¹¹¹¹¹¹33333333))       ÚÚÚÚÚÚÚÇÇÇ¹¹¹¹¹3333333333        ÚÚÚÚÚÚÇÇÇÇÇ¹¹¹22222222222\"\"\"   ÷÷÷÷ÚÚÇÇÇÇÇÇ«««888882222266666666ÔÔÔÔÔ««««««88888888888666666#ÔÔÔÔÔ«««««8888888888880066####ÔÔÔÔ«««««EEEEEE7777770000####££££££EEEEEEEEE7777BBBBBB£££££DDDDDDDDDDDDBBBBBBBB££££DDDDDDDDDDDDDBBBBBBBB{{{DDDDDDDDDDDDDQBBBBBBaaa{{{{{?????????????QQQQQ;aaaaaaa{{{{{{HHHHHHHHHHH?QQQQQQ;aaaaaaaHHHHHHHHHHHHHHHTTTTTTTalllllllllHHHHHHHHHHHHHHHTTTTTTTTlllllllllHHHHHHHHHHHHHHHTTTTTTTTIIlllllllþþþþþþþþíííäääääÙÙÙÙÙÌÌÌÌÌÌ¾¾¾¾¾þþþþþþþþíííääääääÙÙÙÙÌÌÌÌÌÌ¾¾¾¾¾þþþþþþþþíííääääääÙÙÙÙÌÌÌÌÌÌÌ¾¾¾¾þþþþþþþþòòòääääääÙÙÙÙÌÌÌÌÌÌÌ¾¾¾¾þþþþþþþþòòòòäääääÙÙÙÙÌÌÌÌÌÌÌ¾¾¾¾þþþþþþþþòòòòäääääÙÙÙÛÛÌÌÌÌÌÌÄÄÄÄþþþþþþþòòòòååååååÛÛÛÛÌÌÌÌÌÌÄÄÄÄòòòòåååååååÛÛÛÌÌÌÌÌÄÄÄÄÄööååååååÞÞÞÞÀÀÀÀÀÀ¼¼¼¼!öööööåååÞÞÞÞÞÞÐÐÐÐ¼¼¼¼¼!!!!!!!öööööööêÞÞÞÞÞÞÐÐÐÐ¼¼¼¼¼!!!!!!!!!öööööööêêÞÞÞÞÞÃÐÐÐ¼¼¼¼¼!!!!!!!ööööîîêêÞÞÃÃÃÃÃÃ¼¼¼¼¼))))))ûûîîîîÜÜÜÜÃÃÃÃ¹¹¹¹¹))))))))ûûûûûîÜÜÜÜÜÜÜ¹¹¹¹¹¹))))))))))ûûûûûÚÚÚÜÜÜÜÜ¹¹¹¹¹¹3333))))))\"      ÷ÚÚÚÚÚÇÇÇÇÇ¹¹¹¹222222222\"\"\"\"\"\" ÷÷÷÷÷ÚÚÇÇÇÇÇÇÇ¹¹22222222222\"\"\"\"\"÷÷÷÷÷÷ÔÇÇÇÇÇÇ«««22222222222666666#÷÷÷ÔÔÔÔÔÔ«««««8888888888006666#####ÔÔÔÔÔÔ«««««8888888888000000######ÔÔÔÔ££«««7777777777770000####££££££777777777777BBBBBBB££££££DDDDDDDDDDDBBBBBBBBB££££DDDDDDDDDDDDBBBBBBBBB{{??????????????BBBBBBaa{{{{??????????????;;;;;;aaaaaHHHHHHHHHHHH?Q;;;;;;aaaaaHHHHHHHHHHHHHHHTTTTTT<<<lllllllHHHHHHHHHHHHHHHTTTTTTTTllllllllAAAAAAAAAAAAAAACCCCCCCIIIIIllllþþþþþþþòòòòääääääÙÛÛÛÛÌÌÌÌÌÌÄÄÄÄþþþþþþþòòòòääääääÙÛÛÛÛÌÌÌÌÌÌÄÄÄÄþþþþþþþòòòòòäääääÛÛÛÛÛÌÌÌÌÌÌÄÄÄÄþþþþþþþòòòòòäääääÛÛÛÛÛÌÌÌÌÌÌÄÄÄÄþþþþþþþòòòòòäääääÛÛÛÛÛÌÌÌÌÌÄÄÄÄÄýýýýýýýòòòòòòääääÛÛÛÛÛÌÌÌÌÌÄÄÄÄÄýýòòòòòååååÛÛÛÛÛÛÌÌÌÌÄÄÄÄÄòòòòåååååÛÛÛÛÛÌÌÌÌÄÄÄÄÄöööååååÞÞÞÞÞÐÐÐÐÐÐÄÄÄÄööööööêêÞÞÞÞÞÞÐÐÐÐÐ¼¼¼¼!!!!!ööööööêêêÞÞÞÞÞÐÐÐÐÐ¼¼¼¼ööööööêêêêÞÞÞÞÐÐÐÐÐ¼¼¼¼ööûûêêêêÜÜÜÃÃÃÃÉÉÉÉÉ)))))))ûûûûûûÜÜÜÜÜÜÜÜÃ¹¹¹¹¹)))))))))ûûûûûûûÜÜÜÜÜÜÜ¹¹¹¹¹¹)))))))))))ûûûûûûûÚÜÜÜÜÜÜ¹¹¹¹¹¹222))))))\"\"\"\"\"\"÷÷÷÷÷ÚÚÇÇÇÇÇÇ¹¹¹¹222222222\"\"\"\"\"\"\"÷÷÷÷÷÷ÇÇÇÇÇÇÇÇ¹¹2222222222\"\"\"\"\"\"÷÷÷÷÷÷ÔÔÇÇÇÇÇ«««222222222226666####÷ÔÔÔÔÔÔÔ«««««888888880000000######ÔÔÔÔÔÔÔ««««7777777770000000######ÔÔÔÔ£££££7777777777700000####££££££777777777777BBBBBBB££££££DDDDDDDDDDDBBBBBBBBB££££DDDDDDDDDDD5BBBBBBBB*ÑÑ?????????????;;;;;;;***Õ?????????????;;;;;;;;aaaHHHHHHHHHHHH::;;;;;;<<<<<HHHHHHHHHHHHHHHTTTTT<<<<<llllHHHHHHHHHHHHHHCCCCCCCIIIIIlllAAAAAAAAAAAAAACCCCCCCIIIIIIIIýýýýýýýòòòòòäääääÛÛÛÛÛÌÌÌÌÌÄÄÄÄÄýýýýýýýòòòòòäääääÛÛÛÛÛÌÌÌÌÌÄÄÄÄÄýýýýýýýòòòòòäääääÛÛÛÛÛÌÌÌÌÌÄÄÄÄÄýýýýýýýòòòòòäääääÛÛÛÛÛÌÌÌÌÌÄÄÄÄÄýýýýýýýòòòòòòççççÛÛÛÛÛÛÌÌÌÌÄÄÄÄÄýýýýýýýýòòòòòççççÛÛÛÛÛÛÌÌÌÄÄÄÄÄÄýýýòòòòòççççÛÛÛÛÛÛÌÌÌÄÄÄÄÄÄòòòòçççççÛÛÛÛÛÐÐÐÐÄÄÄÄÄöööööêêêÞÞÞÞÞÐÐÐÐÐÐÄÄÄÄöööööêêêêÞÞÞÞÐÐÐÐÐÐÐ¼¼¼öööööêêêêêÞÞÞÐÐÐÐÐÐÉÉÉÉööööêêêêêêÞÞÞÐÐÐÐÉÉÉÉÉûûûûûêêêÜÜÜÜÜÜÜÉÉÉÉÉÉ))))))))ûûûûûûûÜÜÜÜÜÜÜÜÜ¹ÉÉÉÉ))))))))))ûûûûûûûûÜÜÜÜÜÜÜ¹¹¹¹¹¹))))))))))\"\"ûûûûûûûÜÜÜÜÜÜÇÇ¹¹¹¹¹2222222\"\"\"\"\"\"\"\"÷÷÷÷÷÷÷ÇÇÇÇÇÇÇ¹¹¹222222222\"\"\"\"\"\"÷÷÷÷÷÷÷ÇÇÇÇÇÇÇÍÍÍ2222222222\"\"\"\"\"\"÷÷÷÷÷÷ÔÔÇÇÇÇÍÍÍÍ22222222220000######ÔÔÔÔÔÔÔ«««««777777700000000######ÔÔÔÔÔÔËËËËË7777777770000000######óóóó£££ËËË7777777777700000((##ó££££££777777777777BBBBB(((££££££DDDDDDD55555BBBBBBB**ÑÑÑÑ?????????5555BBBBBB***ÑÑÑ????????????;;;;;;;;****ÕÕ????????????;;;;;;;;;<<<ÕHHHHHHHH::::::;;;;;<<<<<<HHHHHHHHHHHH:::CCCC<<<<<<<lAAAAAAAAAAAAACCCCCCCCIIIIIIIAAAAAAAAAAAAAACCCCCCCIIIIIIIIýýýýýýýòòòòòçççççÛÛÛÛÛÛÌÌÌÄÄÄÄÄÄýýýýýýýòòòòòçççççÛÛÛÛÛÛÌÌÌÄÄÄÄÄÄýýýýýýýòòòòòçççççÛÛÛÛÛÛÌÌÌÄÄÄÄÄÄýýýýýýýýòòòòçççççÛÛÛÛÛÛÌÌÌÄÄÄÄÄÄýýýýýýýýòòòòçççççÛÛÛÛÛÛÌÌÌÄÄÄÄÄÄýýýýýýýýòòòòçççççÛÛÛÛÛÛØØØÄÄÄÄÄÄýýýýýòòòççççççÛÛÛÛÛØØØÄÄÄÄÄÄòòññçççççãÛÛÛÐÐÐÐÐÄÄÄÄÄüüüöêêêêêÞÞÞÞÐÐÐÐÐÐÐÎÎÎüüüüüêêêêêêÞÞÞÐÐÐÐÐÐÉÉÉÉüüüüêêêêêêêÞÞÐÐÐÐÐÉÉÉÉÉüüüüêêêêêêêßßßÐÐÉÉÉÉÉÉûûûûûûêêÜÜÜÜÜÜßÉÉÉÉÉÉ))))))))ûûûûûûûûÜÜÜÜÜÜÜÜÒÒÒÉÉÉ))))))))))ûûûûûûûûÜÜÜÜÜÜÜÒÒÒÒÒÒ))))))$$$$\"\"ûûûûëëëëÜÜÇÇÇÒÒÒÒÒ22222$$$\"\"\"\"\"\"\"÷÷÷÷÷÷ëÇÇÇÇÇÇÍÍÍÍ22222222\"\"\"\"\"\"\"÷÷÷÷÷÷÷éÇÇÇÇÍÍÍÍÍ222222222+\"\"\"\"\"÷÷÷÷éééééÇÍÍÍÍÍ22222+++++0000######ÔÔÔÔÔÔÔËËËËË777777700000000######óÔÔÔÔËËËËËË7777777770000000#####óóóóóóËËËËË77777777771100((((((óóóóó£££££7777777771111BB(((((ÑÑÑÑÑÑ5555555555555BBBBB****ÑÑÑÑÑ???????5555555;;;*******ÑÑÑÑ????????????;;;;;;;;****ÕÕÕ??????????::;;;;;;;;<<<<ÕÕHHH:::::::::::;;;;;<<<<<<<HHHHHHHH::::::CCCCC<<<<<<<AAAAAAAAAAAAACCCCCCCCIIIIIIIAAAAAAAAAAAAAACCCCCCCIIIIIIIIýýýýýýýýòòòççççççÛÛÛÛÛØØØØØÄÄÄÄÄýýýýýýýýòòòççççççÛÛÛÛÛØØØØØÄÄÄÄÄýýýýýýýýòòòççççççÛÛÛÛÛØØØØØÄÄÄÄÄýýýýýýýýòòòççççççãÛÛÛÛØØØØØÄÄÄÄÄýýýýýýýýýòññçççççããÛÛÛØØØØØÄÄÄÄÄýýýýýýýýýñññçççççãããÛØØØØØØÎÎÎÎÎññññççççããããØØØØØØÎÎÎÎÎüññññççãããããØØØØØØÎÎÎÎÎüüüüüñêêêêãããßÐÐÐÐÐÐÎÎÎÎüüüüüüüêêêêêêßßßßÐÐÐÉÉÉÉÉüüüüüüêêêêêêßßßßßßÉÉÉÉÉÉüüüüüðððððêßßßßßßÉÉÉÉÉÉûûûûððððÜÜÜÜßßßÉÉÉÉÉÉ)))))$ûûûûûûëëÜÜÜÜÜÒÒÒÒÒÒÒ$$$$$$$$$$ûûëëëëëëÜÜÒÒÒÒÒÒÒ$$$$$$$$$$$ëëëëëëëÇÒÒÒÒÒÒÒ$$$$$$$$$\"\"\"\"\"÷÷÷÷÷ëëééÇÇÍÍÍÍÍÍ22222++++\"\"\"\"\"÷÷÷÷ééééééÍÍÍÍÍÍ++++++++++++÷éééééééÍÍÍÍÍ++++++++++++0ééééééËËËËËË777771100000000#####óóóóóóËËËËËË77777111111000((((((óóóóóóóËËËËË7777111111111((((((((óóóóóóÑÑÑÑÑ5555555551111((((((((*óóÑÑÑÑÑÑÑ555555555555555B********ÑÑÑÑÑÑ????555555555;;;;********ÕÕÕÕÕ???????????;;;;;;;;*****ÕÕÕÕ::::::::::::;;;;;;;<<<<<ÕÕÕ:::::::::::::::;;;<<<<<<<<AAAAAAAAA::::CCCCCC<<<<<IIAAAAAAAAAAAACCCCCCCCIIIIIIIAAAAAAAAAAAAACCCCCCCCIIIIIIIIýýýýýýýýññññççççããããÛØØØØØØÎÎÎÎÎýýýýýýýýññññççççããããÛØØØØØØÎÎÎÎÎýýýýýýýýññññççççãããããØØØØØØÎÎÎÎÎñññññçççãããããØØØØØØÎÎÎÎÎñññññçççãããããØØØØØØÎÎÎÎÎññññññççãããããØØØØØØÎÎÎÎÎñññññççãããããØØØØØØÎÎÎÎÎüñññññãããããããØØØØØÎÎÎÎÎüüüüüüñêêêãããßßßßßÐÎÎÎÎÎÎüüüüüüüüððððêßßßßßßßÉÉÉÉÉÉüüüüüüðððððßßßßßßßÉÉÉÉÉÉüüüðððððððßßßßßßÉÉÉÉÉÉûðððððëëßßßßÒÒÒÒÉÉÉ$$$$$$$ëëëëëëëëÒÒÒÒÒÒÒÒ$$$$$$$$$$ëëëëëëëÒÒÒÒÒÒÒÒ$$$$$$$$$$$ëëëëëëëëÒÒÒÒÒÒÒ$$$$$$$$$$\"\"\"÷÷÷ëéééééÍÍÍÍÍÍÍ+++++++++++\"éééééééÍÍÍÍÍÍ++++++++++++éééééééÍÍÍÍÍ+++++++++++++ééééééËËËËËË11111111100000((((#óóóóóóóËËËËËË111111111111((((((((óóóóóóóËËËËË1111111111111((((((((óóóóóÑÑÑÑÑÑ5555555555551(((((((***óÑÑÑÑÑÑÑÑ555555555555555**********ÑÑÑÑÑÑÑ5555555555555;;;;********ÕÕÕÕÕÕ????:::::::;;;;;;;;****ÕÕÕÕÕ:::::::::::::;;;;;<<<<<<ÕÕÕÕ:::::::::::::::;;<<<<<<<<AAAAAAAAAA::CCCCCCCC<<IIIIAAAAAAAAAAAACCCCCCCCIIIIIIIAAAAAAAAAAAAACCCCCCCIIIIIIIIñññññççããããããØØØØØØÎÎÎÎÎñññññççããããããØØØØØØÎÎÎÎÎññññññçããããããØØØØØØÎÎÎÎÎññññññçããããããØØØØØØÎÎÎÎÎññññññçããããããØØØØØØÎÎÎÎÎññññññããããããØØØØØØÎÎÎÎÎññññññããããããØØØØØÎÎÎÎÎÎüüüññññããããããßßØØØÎÎÎÎÎÎüüüüüüüðððððããßßßßßßÉÎÎÎÎÎüüüüüüððððððßßßßßßßÉÉÉÉÉÉüüüüðððððððßßßßßßÉÉÉÉÉÉüüðððððððßßßßßßÉÉÉÉÉÉððððëëëëßßßÒÒÒÒÒÒÒ$$$$$$$$ëëëëëëëëÒÒÒÒÒÒÒÒ$$$$$$$$$ëëëëëëëÒÒÒÒÒÒÒÒ$$$$$$$$$$ëëëëëëëëÍÍÍÍÍÍÍ++$$$$$$$$$éééééééÍÍÍÍÍÍÍ+++++++++++éééééééÍÍÍÍÍÍ++++++++++++éééééééËÍÍÍÍ++++++++++++óóéééËËËËËËË111111111111(((((((óóóóóóóËËËËËË111111111111((((((((óóóóóóóËËËËË111111111111(((((((((óóóóóÑÑÑÑÑÑ5555555555555(((((******ÑÑÑÑÑÑÑÑ555555555555555**********ÑÑÑÑÑÑÑ555555555555;;;;;********ÕÕÕÕÕÕÕ:::::::::::;;;;;;;;<<<<ÕÕÕÕÕÕ:::::::::::::;;;;<<<<<<<<ÕÕÕÕÕ::::::::::::::CCC<<<<<<<<AAAAAAAAAAACCCCCCCCCIIIIIIAAAAAAAAAAAACCCCCCCCIIIIIIIAAAAAAAAAAAACCCCCCCCIIIIIIIññññññãããããããØØØØØØÎÎÎÎÎññññññãããããããØØØØØØÎÎÎÎÎññññññãããããããØØØØØØÎÎÎÎÎñññññññããããããØØØØØØÎÎÎÎÎñññññññããããããØØØØØÎÎÎÎÎÎññññññããããããØØØØØÎÎÎÎÎÎññññññããããããßØØØØÎÎÎÎÎÎüüüüññððããããßßßßßØÎÎÎÎÎÎüüüüüüððððððßßßßßßßßÎÎÎÎÎüüüüðððððððßßßßßßßßÉÉÉÉÉüüððððððððßßßßßßßÉÉÉÉÉððððððððßßßßßßÒÒÒÒÉÉððëëëëëëßÒÒÒÒÒÒÒÒ$$$$$$$$ëëëëëëëëÒÒÒÒÒÒÒÒ$$$$$$$$$ëëëëëëëÒÒÒÒÒÒÒÒ$$$$$$$$$$ëëëëëëëéÍÍÍÍÍÍÍ+++++++++$éééééééÍÍÍÍÍÍÍ+++++++++++éééééééÍÍÍÍÍÍ+++++++++++ééééééËËËËËË++++++++++++óóóóóóËËËËËËË111111111111(((((((óóóóóóóËËËËËË111111111111((((((((óóóóóóóËËËËË111111111111(((((((((óóóóÑÑÑÑÑÑÑ5555555555555((((*******ÑÑÑÑÑÑÑÑ55555555555555;**********ÕÑÑÑÑÑÑ555555555555;;;;;*******ÕÕÕÕÕÕÕÕ::::::::::::;;;;;;<<<<<ÕÕÕÕÕÕÕ::::::::::::::;;<<<<<<<<<ÕÕÕÕÕ:::::::::::::CCCC<<<<<<<AAAAAAAAAACCCCCCCCCIIIIIIIAAAAAAAAAAACCCCCCCCCIIIIIIIAAAAAAAAAAAACCCCCCCCIIIIIII".getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException ignored) {
            ENCODED_SNUGGLY = new byte[0x8000];
        }
    }
    /**
     * A 64x64 grid of floats, with a median value of about 1.0, generated using the triangular-distributed blue noise
     * from {@link #TRI_BLUE_NOISE_A}. If you randomly selected two floats from this and multiplied them, the average
     * result should be 1.0; half of the items in this should be between 1 and {@code 4.232422}, and the other half should
     * be the inverses of the first half (between {@code 0.23625374}, which is {@code 1.0/4.232422}, and 1).
     * <br>
     * While, for some reason, you could change the contents to some other distribution of floats, I don't know why this
     * would be needed.
     */
    public static final float[] TRI_BLUE_NOISE_MULTIPLIERS_A = new float[16384];
    /**
     * A 64x64 grid of floats, with a median value of about 1.0, generated using the triangular-distributed blue noise
     * from {@link #TRI_BLUE_NOISE_B}. If you randomly selected two floats from this and multiplied them, the average
     * result should be 1.0; half of the items in this should be between 1 and {@code 4.232422}, and the other half should
     * be the inverses of the first half (between {@code 0.23625374}, which is {@code 1.0/4.232422}, and 1).
     * <br>
     * While, for some reason, you could change the contents to some other distribution of floats, I don't know why this
     * would be needed.
     */
    public static final float[] TRI_BLUE_NOISE_MULTIPLIERS_B = new float[16384];
    /**
     * A 64x64 grid of floats, with a median value of about 1.0, generated using the triangular-distributed blue noise
     * from {@link #TRI_BLUE_NOISE_C}. If you randomly selected two floats from this and multiplied them, the average
     * result should be 1.0; half of the items in this should be between 1 and {@code 4.232422}, and the other half should
     * be the inverses of the first half (between {@code 0.23625374}, which is {@code 1.0/4.232422}, and 1).
     * <br>
     * While, for some reason, you could change the contents to some other distribution of floats, I don't know why this
     * would be needed.
     */
    public static final float[] TRI_BLUE_NOISE_MULTIPLIERS_C = new float[16384];
    static {
        for (int i = 0; i < 16384; i++) {
            TRI_BLUE_NOISE_MULTIPLIERS_A[i] = OtherMath.expRough(OtherMath.probitF((TRI_BLUE_NOISE_A[i] + 128.5f) * 0x1p-8f) * 0.5f);
            TRI_BLUE_NOISE_MULTIPLIERS_B[i] = OtherMath.expRough(OtherMath.probitF((TRI_BLUE_NOISE_B[i] + 128.5f) * 0x1p-8f) * 0.5f);
            TRI_BLUE_NOISE_MULTIPLIERS_C[i] = OtherMath.expRough(OtherMath.probitF((TRI_BLUE_NOISE_C[i] + 128.5f) * 0x1p-8f) * 0.5f);
        }
    }
}
