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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.NumberUtils;
import com.github.tommyettinger.digital.Base;
import com.github.tommyettinger.digital.MathTools;
import com.github.tommyettinger.ds.IntList;
import com.github.tommyettinger.ds.support.sort.IntComparator;

import java.util.Arrays;

/*
// 15 colors
{
0x00000000, 0x2D3EA5FF, 0x536EE1FF, 0xAD3CDCFF, 0x000000FF, 0x4B4B4BFF, 0x787878FF, 0x9E9E9EFF,
0xC1C1C1FF, 0xE1E1E1FF, 0xFFFFFEFF, 0x8B1547FF, 0xC74674FF, 0xF76B9AFF, 0x74E41CFF, 0xF0BA16FF,
}

// 39 colors
{
0x00000000, 0x210CC0FF, 0x3647F8FF, 0x7A17F4FF, 0x28367CFF, 0x485DACFF, 0x657DD3FF, 0x7F9AF5FF,
0x5D1279FF, 0x883CA9FF, 0xAB5CCFFF, 0xCB77F1FF, 0xE216CBFF, 0x24BAA2FF, 0x45D5BCFF, 0x5EEFD5FF,
0x000000FF, 0x3D3D3DFF, 0x636363FF, 0x828282FF, 0x9E9E9EFF, 0xB9B9B9FF, 0xD1D1D1FF, 0xE8E8E8FF,
0xFFFFFEFF, 0x6C1D3BFF, 0x9B4260FF, 0xC2607FFF, 0xE37B9BFF, 0xF61A7CFF, 0x50991AFF, 0x6AB839FF,
0x82D352FF, 0x99ED69FF, 0xA17E17FF, 0xBF9A37FF, 0xDBB550FF, 0xF5CD66FF, 0xDB5713FF, 0xFF7334FF,
}

// 63 colors
{
0x00000000, 0x1B1B97FF, 0x3143C6FF, 0x4661EBFF, 0x6627C3FF, 0x8146E8FF, 0xAC0BE4FF, 0x29C7FFFF,
0x243064FF, 0x40518DFF, 0x596CADFF, 0x7085CAFF, 0x859CE3FF, 0x99B1FBFF, 0x4C1862FF, 0x71398AFF,
0x8F54AAFF, 0xAA6BC7FF, 0xC281E0FF, 0xD995F8FF, 0xBA2BA7FF, 0xD845C4FF, 0xF45CDDFF, 0x178372FF,
0x369D8AFF, 0x4CB5A1FF, 0x61CBB6FF, 0x74E0CAFF, 0x86F4DDFF, 0x000000FF, 0x353535FF, 0x555555FF,
0x707070FF, 0x888888FF, 0x9E9E9EFF, 0xB4B4B3FF, 0xC7C7C7FF, 0xDBDBDAFF, 0xEDEDEDFF, 0x591D33FF,
0x803C52FF, 0xA1566DFF, 0xBD6D85FF, 0xD7839CFF, 0xEF97B1FF, 0xCA2C6BFF, 0xEA4683FF, 0x4A8227FF,
0x619C3EFF, 0x76B353FF, 0x89C966FF, 0x9CDE78FF, 0xAEF289FF, 0x876D26FF, 0xA1853DFF, 0xB99C51FF,
0xCFB164FF, 0xE4C576FF, 0xF8D887FF, 0xB45124FF, 0xD2683BFF, 0xED7E4FFF, 0xDB1B22FF, 0xFD3B39FF,
}
// 112 colors
{
0x00000000, 0x2727D5FF, 0x3647F8FF, 0x682DF5FF, 0x181D7CFF, 0x2D3EA5FF, 0x4158C5FF, 0x536EE1FF,
0x6583FAFF, 0x582AA2FF, 0x7044C2FF, 0x865ADEFF, 0x9A6DF7FF, 0x9322C0FF, 0xAD3CDCFF, 0xC351F5FF,
0xE81CF2FF, 0x249BC6FF, 0x3AAFDBFF, 0x4CC1EEFF, 0x212B55FF, 0x3A4877FF, 0x516094FF, 0x6576ADFF,
0x788AC3FF, 0x899CD8FF, 0x9AAEECFF, 0xAABFFEFF, 0x421953FF, 0x623675FF, 0x7C4D91FF, 0x9361AAFF,
0xA975C1FF, 0xBC87D6FF, 0xCF98E9FF, 0xE1A8FCFF, 0x801373FF, 0x9E308FFF, 0xB946A8FF, 0xD159BEFF,
0xE76BD3FF, 0xFC7CE7FF, 0xF52BBCFF, 0x237264FF, 0x3A897AFF, 0x4D9E8EFF, 0x5FB1A1FF, 0x6FC4B2FF,
0x7FD5C3FF, 0x8EE6D3FF, 0x9DF6E3FF, 0x000000FF, 0x2E2E2EFF, 0x4B4B4BFF, 0x636363FF, 0x787878FF,
0x8C8C8CFF, 0x9E9E9EFF, 0xB0B0B0FF, 0xC1C1C1FF, 0xD1D1D1FF, 0xE1E1E1FF, 0xF0F0F0FF, 0xFFFFFEFF,
0x4B1D2DFF, 0x6E3849FF, 0x8A4F61FF, 0xA36376FF, 0xB9768AFF, 0xCE889CFF, 0xE299AEFF, 0xF4A9BFFF,
0x8B1547FF, 0xAB315FFF, 0xC74674FF, 0xE05988FF, 0xF76B9AFF, 0x2CD473FF, 0x41E783FF, 0x53F891FF,
0x2F5713FF, 0x45712BFF, 0x59883FFF, 0x6C9D51FF, 0x7DB061FF, 0x8EC371FF, 0x9ED481FF, 0xADE58FFF,
0xBCF59EFF, 0x5C4812FF, 0x75602AFF, 0x8C763DFF, 0xA18A4FFF, 0xB49D60FF, 0xC7AE6FFF, 0xD8BF7FFF,
0xE9CF8DFF, 0xF9DF9BFF, 0x7C3310FF, 0x9A4B28FF, 0xB4603BFF, 0xCB734DFF, 0xE1855EFF, 0xF5966EFF,
0xBA2727FF, 0xD73F3AFF, 0xF1544BFF, 0x74E41CFF, 0x83F533FF, 0xBAD019FF, 0xCAE131FF, 0xD9F243FF,
0xF0BA16FF,
}

// 178 colors
{
0x00000000, 0x232AB5FF, 0x3244D4FF, 0x415AEFFF, 0x4812B2FF, 0x5C32D2FF, 0x6E48EDFF, 0x7A09CFFF,
0x8F2DEAFF, 0x1997F0FF, 0x171E6AFF, 0x2A3A8EFF, 0x3C51AAFF, 0x4D65C3FF, 0x5D77D9FF, 0x6C88EEFF,
0x340868FF, 0x4E2A8CFF, 0x6340A8FF, 0x7754C1FF, 0x8966D7FF, 0x9A76EBFF, 0xAA86FFFF, 0x68068AFF,
0x8128A6FF, 0x983DBEFF, 0xAC4FD5FF, 0xBF60E9FF, 0xD16FFCFF, 0xB510BCFF, 0xCB2DD2FF, 0xE041E7FF,
0xF352FAFF, 0x187899FF, 0x2E8BAEFF, 0x3F9CC0FF, 0x4FADD2FF, 0x5DBDE3FF, 0x6BCCF3FF, 0x1E2749FF,
0x364168FF, 0x4A5781FF, 0x5C6A97FF, 0x6D7CACFF, 0x7D8DBEFF, 0x8C9DD0FF, 0x9BACE1FF, 0xA9BBF1FF,
0x3A1948FF, 0x563266FF, 0x6E4780FF, 0x835A96FF, 0x966BAAFF, 0xA87BBCFF, 0xB88BCEFF, 0xC899DEFF,
0xD8A7EEFF, 0xE6B5FEFF, 0x701A65FF, 0x8B317EFF, 0xA24494FF, 0xB855A8FF, 0xCB65BAFF, 0xDE74CCFF,
0xEF83DCFF, 0xBF1E92FF, 0xD635A6FF, 0xEC46B8FF, 0x23DDC0FF, 0x38ECCEFF, 0x49FADCFF, 0x0E4F45FF,
0x27665AFF, 0x3A7A6EFF, 0x4B8D80FF, 0x5B9E90FF, 0x6AAFA0FF, 0x78BEAFFF, 0x86CDBEFF, 0x93DCCCFF,
0x9FEADAFF, 0xACF8E7FF, 0x000000FF, 0x2A2A2AFF, 0x434343FF, 0x595959FF, 0x6C6C6CFF, 0x7E7E7EFF,
0x8F8F8FFF, 0x9E9E9EFF, 0xAEAEAEFF, 0xBCBCBCFF, 0xCACACAFF, 0xD8D8D8FF, 0xE5E5E5FF, 0xF2F2F2FF,
0x421B28FF, 0x613442FF, 0x7A4857FF, 0x905B6AFF, 0xA46C7CFF, 0xB67C8DFF, 0xC88B9DFF, 0xD89AACFF,
0xE8A8BAFF, 0xF8B6C8FF, 0x791A40FF, 0x963156FF, 0xAE4469FF, 0xC4557AFF, 0xD9658BFF, 0xEC749BFF,
0xFE83AAFF, 0xCA1D67FF, 0xE23379FF, 0xF84689FF, 0x03994FFF, 0x27AC5EFF, 0x3ABD6CFF, 0x4ACD7AFF,
0x59DD88FF, 0x67EC95FF, 0x74FAA1FF, 0x2C4E17FF, 0x40652CFF, 0x53793DFF, 0x638C4DFF, 0x739D5CFF,
0x82AE6BFF, 0x90BE79FF, 0x9ECD86FF, 0xACDB93FF, 0xB9E9A0FF, 0xC5F7ACFF, 0x514116FF, 0x68572BFF,
0x7D6B3CFF, 0x8F7C4CFF, 0xA18D5BFF, 0xB19D69FF, 0xC1AC77FF, 0xD0BB84FF, 0xDEC991FF, 0xEDD79EFF,
0xFAE4AAFF, 0x6C3015FF, 0x874529FF, 0x9E583BFF, 0xB36A4AFF, 0xC67A59FF, 0xD88968FF, 0xE99875FF,
0xFAA683FF, 0x841014FF, 0xA22B28FF, 0xBB3F39FF, 0xD25149FF, 0xE76158FF, 0xFB7166FF, 0xD60738FF,
0xEF2A48FF, 0x13F950FF, 0x61BA1FFF, 0x6FCB31FF, 0x7CDA41FF, 0x89E94EFF, 0x96F85BFF, 0x99AB1DFF,
0xA8BB30FF, 0xB6CA3FFF, 0xC4D94DFF, 0xD1E75AFF, 0xDEF466FF, 0xC4991BFF, 0xD4A82EFF, 0xE4B73EFF,
0xF3C54BFF, 0xE98419FF, 0xFB932DFF, }

// 216 colors
{
0x00000000, 0x2917EFFF, 0x1F1EA8FF, 0x2A37C4FF, 0x374BDCFF, 0x435CF2FF, 0x5223C2FF, 0x6139DAFF,
0x704AF0FF, 0x8018D8FF, 0x9130EDFF, 0xC00FFFFF, 0x1193EBFF, 0x27A1FCFF, 0x131762FF, 0x233182FF,
0x33459CFF, 0x4257B2FF, 0x4F67C6FF, 0x5C76D8FF, 0x6984E9FF, 0x7592F9FF, 0x452180FF, 0x58359AFF,
0x6946B0FF, 0x7956C3FF, 0x8865D6FF, 0x9672E7FF, 0xA380F7FF, 0x741B97FF, 0x882FADFF, 0x9A3FC1FF,
0xAB4ED3FF, 0xBB5CE4FF, 0xCA69F4FF, 0xB715BFFF, 0xCA2CD1FF, 0xDB3CE2FF, 0xEB4BF2FF, 0x1B7A9CFF,
0x2D8AACFF, 0x3C98BCFF, 0x49A6CBFF, 0x55B3D9FF, 0x61C0E7FF, 0x6CCDF4FF, 0x1A2243FF, 0x2E395EFF,
0x3F4C75FF, 0x4F5D88FF, 0x5E6D9AFF, 0x6C7BAAFF, 0x7989BAFF, 0x8697C9FF, 0x92A3D7FF, 0x9EB0E4FF,
0xAABCF1FF, 0xB5C7FEFF, 0x341441FF, 0x4D2A5DFF, 0x623C73FF, 0x744D86FF, 0x855C98FF, 0x956AA8FF,
0xA377B8FF, 0xB284C7FF, 0xBF91D5FF, 0xCC9DE2FF, 0xD9A8EFFF, 0xE5B3FCFF, 0x650E5BFF, 0x7D2571FF,
0x923784FF, 0xA54696FF, 0xB654A6FF, 0xC761B6FF, 0xD66EC5FF, 0xE57AD3FF, 0xF386E0FF, 0xC22194FF,
0xD533A4FF, 0xE742B4FF, 0xF850C2FF, 0x25DEC1FF, 0x36EACDFF, 0x44F6D8FF, 0x1B5A4FFF, 0x2D6C60FF,
0x3C7C70FF, 0x4A8C7FFF, 0x579A8CFF, 0x64A89AFF, 0x70B5A7FF, 0x7BC2B3FF, 0x86CEBFFF, 0x91DACBFF,
0x9CE6D6FF, 0xA6F1E1FF, 0xB0FCECFF, 0x000000FF, 0x242424FF, 0x3B3B3BFF, 0x4E4E4EFF, 0x5F5F5FFF,
0x6E6E6EFF, 0x7D7D7DFF, 0x8B8B8BFF, 0x989898FF, 0xA5A5A5FF, 0xB1B1B1FF, 0xBDBDBDFF, 0xC9C9C9FF,
0xD4D4D4FF, 0xDFDFDFFF, 0xEAEAEAFF, 0xF4F4F4FF, 0xFFFFFEFF, 0x3B1623FF, 0x572C39FF, 0x6D3E4CFF,
0x804E5DFF, 0x925D6DFF, 0xA36B7BFF, 0xB27889FF, 0xC18596FF, 0xCF92A3FF, 0xDC9EAFFF, 0xE9A9BBFF,
0xF6B4C7FF, 0x6E1038FF, 0x87264BFF, 0x9D375BFF, 0xB1466BFF, 0xC35479FF, 0xD46287FF, 0xE46E94FF,
0xF47AA1FF, 0xCD2069FF, 0xE13278FF, 0xF34285FF, 0x22A75AFF, 0x33B667FF, 0x41C472FF, 0x4ED17EFF,
0x5ADE89FF, 0x65EA93FF, 0x70F69EFF, 0x25450FFF, 0x365922FF, 0x466B31FF, 0x557C3FFF, 0x628B4CFF,
0x6F9959FF, 0x7CA765FF, 0x88B571FF, 0x94C17CFF, 0x9FCE87FF, 0xAADA91FF, 0xB5E59CFF, 0xC0F1A6FF,
0xCAFCB0FF, 0x48390DFF, 0x5C4C20FF, 0x6E5D30FF, 0x7F6D3EFF, 0x8E7B4BFF, 0x9D8958FF, 0xAA9763FF,
0xB8A36FFF, 0xC4B07AFF, 0xD1BC85FF, 0xDDC790FF, 0xE8D39AFF, 0xF4DEA4FF, 0xFFE8AEFF, 0x62280CFF,
0x793B1FFF, 0x8E4B2FFF, 0xA05A3DFF, 0xB2694AFF, 0xC27656FF, 0xD18362FF, 0xDF8F6DFF, 0xED9B79FF,
0xFBA783FF, 0x931F1EFF, 0xA9322DFF, 0xBE423BFF, 0xD15048FF, 0xE25E55FF, 0xF36B60FF, 0xD90F3AFF,
0xEE2947FF, 0x0CF74EFF, 0x5BB314FF, 0x67C127FF, 0x72CF35FF, 0x7DDB41FF, 0x88E84DFF, 0x92F458FF,
0x93A412FF, 0x9FB225FF, 0xABBE34FF, 0xB7CB40FF, 0xC2D74BFF, 0xCDE356FF, 0xD8EE60FF, 0xE3F96AFF,
0xBD930FFF, 0xCBA024FF, 0xD8AC32FF, 0xE5B83EFF, 0xF1C44AFF, 0xFDCF54FF, 0xE17E0CFF, 0xF08A22FF,
0xFF9630FF, }

// 250 colors
{
0x00000000, 0x000000FF, 0x282828FF, 0x404040FF, 0x555555FF, 0x676767FF, 0x787878FF, 0x888888FF,
0x979797FF, 0xA6A6A6FF, 0xB4B4B3FF, 0xC1C1C1FF, 0xCECECEFF, 0xDBDBDAFF, 0xE7E7E7FF, 0xF3F3F3FF,
0xFFFFFEFF, 0x791A19FF, 0x94302BFF, 0xAC423CFF, 0xC2534BFF, 0xD56259FF, 0xE87167FF, 0xF97F74FF,
0xFD3F1AFF, 0x7D442DFF, 0xD99376FF, 0x92563DFF, 0xF8AE8FFF, 0x64301AFF, 0xC98568FF, 0xA6674CFF,
0xE9A182FF, 0xB8775BFF, 0xF56C2DFF, 0xE25D1CFF, 0xF99D4AFF, 0xE98F3CFF, 0xD8812EFF, 0xC6721DFF,
0x62532EFF, 0x98875CFF, 0xF2CA63FF, 0xE0CD9CFF, 0xA7966AFF, 0xC5B384FF, 0xE4BD57FF, 0x87774DFF,
0xFFD76EFF, 0xEDDAA8FF, 0xD6B04BFF, 0x75663EFF, 0xD2C090FF, 0xF9E6B3FF, 0xC7A23EFF, 0xB6A577FF,
0x4C3F1BFF, 0xB7932FFF, 0xA7841FFF, 0xF5F11EFF, 0x839320FF, 0x92A231FF, 0xA0B13FFF, 0xAEC04CFF,
0xC8DB64FF, 0xBBCE59FF, 0xE1F57BFF, 0xD4E870FF, 0x468E04FF, 0x559F21FF, 0x63B032FF, 0x70BF40FF,
0x2D491CFF, 0x7DCE4DFF, 0x8ADC5AFF, 0xA2F771FF, 0x96EA66FF, 0x405F2FFF, 0x8DB478FF, 0x51733FFF,
0x7FA56BFF, 0x9AC285FF, 0x61844FFF, 0x70955DFF, 0xA7D092FF, 0xBFEAA9FF, 0xCBF7B5FF, 0xB3DD9EFF,
0x4BF867FF, 0x3CEA5BFF, 0x2BDB4FFF, 0x10CC42FF, 0x87F9ABFF, 0x7BEC9FFF, 0x6EDE93FF, 0x62D087FF,
0x54C17AFF, 0x46B26CFF, 0x36A15FFF, 0x249050FF, 0x037E40FF, 0xAAEBDCFF, 0x85C3B5FF, 0x9EDED0FF,
0x78B4A7FF, 0xB6F7E9FF, 0x92D0C3FF, 0x3E7368FF, 0x5C968AFF, 0x6BA599FF, 0x4E857AFF, 0x2C6056FF,
0x5BECD1FF, 0x68F9DEFF, 0x4EDEC4FF, 0x174A41FF, 0x3FD0B7FF, 0x2EC1A9FF, 0x17B29AFF, 0x19DDF3FF,
0x0E5E79FF, 0x26728EFF, 0x3783A1FF, 0x63B3D3FF, 0x4794B3FF, 0x55A4C4FF, 0x7CCFF1FF, 0x88DCFFFF,
0x70C1E3FF, 0x1A7FC9FF, 0x2D90DCFF, 0x3DA0EEFF, 0x4BAFFFFF, 0x95A5D2FF, 0xBCCDFDFF, 0x8796C2FF,
0xB0C0EFFF, 0x7987B1FF, 0x59668DFF, 0xA3B2E1FF, 0x6A77A0FF, 0x485378FF, 0x343E60FF, 0x86A1FDFF,
0x7892ECFF, 0x6A83DAFF, 0x1E2643FF, 0x5C73C7FF, 0x4C61B2FF, 0x3C4F9BFF, 0x4F6BEEFF, 0x2A3980FF,
0x4159D8FF, 0x3245BFFF, 0x161F5FFF, 0x394CFFFF, 0x232EA1FF, 0x2D35E4FF, 0x2214C4FF, 0x15087DFF,
0x785CECFF, 0x613BFDFF, 0x674AD6FF, 0x5636BDFF, 0x5221E1FF, 0xAF91FBFF, 0x431E9FFF, 0xA183EAFF,
0x8164C5FF, 0x9274D8FF, 0x7053B0FF, 0x5E4199FF, 0x492C7EFF, 0x30105DFF, 0x7F1FFAFF, 0xA859FFFF,
0x9749EAFF, 0x8536D4FF, 0x7120BBFF, 0xCBA2DFFF, 0xD9AFEDFF, 0xE7BCFBFF, 0xBD94D0FF, 0x8D689EFF,
0x7B588BFF, 0x674676FF, 0xAE86C0FF, 0xA052C3FF, 0x9E78AFFF, 0xD37EF9FF, 0xC370E8FF, 0xB261D6FF,
0x8D41AEFF, 0x361A42FF, 0x51325EFF, 0x782E97FF, 0xC640FDFF, 0xB32CE8FF, 0x60167DFF, 0x9F0FD2FF,
0xA625ACFF, 0xBB38C1FF, 0xCF49D4FF, 0xE158E6FF, 0xF267F7FF, 0xEA21D2FF, 0xFD36E4FF, 0x671F5DFF,
0x803475FF, 0x964689FF, 0xAA569CFF, 0xBD65AEFF, 0xCE73BEFF, 0xEE8FDDFF, 0xDF81CEFF, 0xFE9BEBFF,
0x971373FF, 0xAF2B88FF, 0xC53D9AFF, 0xD94DACFF, 0xEC5CBCFF, 0xFE6ACCFF, 0xF427AAFF, 0xA01151FF,
0xFE2482FF, 0xB92A63FF, 0x6F203EFF, 0xD03C74FF, 0xE44D83FF, 0xF85B92FF, 0x8A3452FF, 0x3D1C27FF,
0xA14664FF, 0xB65675FF, 0xC96585FF, 0xDB7494FF, 0xEC81A2FF, 0x5A333FFF, 0x865866FF, 0xFC8FB0FF,
0x996977FF, 0x714753FF, 0xDAA2B2FF, 0xCB95A4FF, 0xF6BCCCFF, 0xAB7886FF, 0xBB8795FF, 0xE8B0BFFF,
0xC4233BFF, 0xDB374AFF, 0xF04858FF,
0xFF0000FF, 0x00FF00FF, 0x0000FFFF, 0xFFFF00FF, 0xE84200FF,
}

 */
public class OkgridPaletteGenerator {
//    public static int LIMIT = 256;
    public static float STEP_L = 1f / 15f;
    public static float STEP_AB = 1f / 16f;
    private static final boolean SORT = true;
    private static final IntList rgba = new IntList(256);

    private static final IntList mixingPalette = new IntList(256);
    private static int idx;
    private static float LL = 0f;
    private static float AA = 0f;
    private static float BB = 0f;
    private static boolean ended = false;

    public static void reset() {
        rgba.clear();
        mixingPalette.clear();
        idx = 1;
        LL = 0f;
        AA = 0f;
        BB = 0f;
        ended = false;
    }

    private static void addGray(float lightness){
        float oklab = oklab(lightness, 0.5f, 0.5f, 1f);
        int rgb = toRGBA8888(oklab);
        rgba.add(rgb);
    }
    private static void add(){
        ++idx;

        LL += STEP_L;
        if(LL > 1f) {
            LL = 0f;
            AA += STEP_AB;
            if(AA >= 1f) {
                AA = 0f;
                BB += STEP_AB;
                if(BB >= 1f)
                    ended = true;
            }
        }

        double L = reverseLight(LL);
        double A = AA - 0.5;
        double B = BB - 0.5;

        double l = (L + +0.3963377774 * A + +0.2158037573 * B);
        l *= l * l;
        double m = (L + -0.1055613458 * A + -0.0638541728 * B);
        m *= m * m;
        double s = (L + -0.0894841775 * A + -1.2914855480 * B);
        s *= s * s;

        double dr = Math.sqrt(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s)*255.0;
        int r = (int)dr;
        if(Double.isNaN(dr) || r < 0 || r > 255) return;
        double dg = Math.sqrt(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s)*255.0;
        int g = (int)dg;
        if(Double.isNaN(dg) || g < 0 || g > 255) return;
        double db = Math.sqrt(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s)*255.0;
        int b = (int)db;
        if(Double.isNaN(db) || b < 0 || b > 255) return;

        int rgb = r << 24 | g << 16 | b << 8 | 0xFF;
        for (int i = 1; i < rgba.size(); i++) {
            int e = rgba.get(i);
//            int er = e >>> 24;
//            int eg = e >>> 16 & 255;
//            int eb = e >>> 8  & 255;
//            if(Math.abs(r - er) + Math.abs(g - eg) + Math.abs(b - eb) <= 6) return;
            if(HexGenerator.METRIC.difference(e, r, g, b) <= 30) System.out.printf("%08X is very close to existing %08X !", rgb, e);
        }
        rgba.add(rgb);
    }
    public static void main(String[] args) {
        if(args != null && args.length > 0)
        {
            STEP_L = 1f / (Base.BASE10.readInt(args[0]) + 1);
            STEP_L = 1f / Base.BASE10.readInt(args[0]);
        }
        rgba.add(0);

        while (!ended) {
            add();
        }
        System.out.println(idx + " attempts.");
//        rgba.items = lloydCompletely(rgba.toArray());

        if(SORT)
            rgba.sort(hueComparator);

        int size = rgba.size();

        StringBuilder sb = new StringBuilder(12 * size + 35).append("{\n");
        for (int i = 0; i < size; i++) {
            appendHex(sb.append("0x"), rgba.get(i)).append(", ");
            if(7 == (i & 7)) sb.append('\n');
        }
        sb.append('}');
        System.out.println(sb);

        sb = new StringBuilder(size * 7);
        for (int i = 1; i < size; i++) {
            sb.append(String.format("%06x\n", rgba.get(i) >>> 8));
        }

        GdxNativesLoader.load();
        Gdx.files = new Lwjgl3Files();
        FileHandle fh = Gdx.files.local("palettes/hex/okgrid-"+ (size-1) +".hex");
        fh.writeString(sb.toString(), false);
        System.out.println("Wrote to " + fh.name());
//        System.out.println("new int[] {");
//        for (int i = 0; i < rgba.size(); i++) {
//            System.out.print("0x" + StringKit.hex(rgba.get(i)) + ", ");
//            if((i & 7) == 7)
//                System.out.println();
//        }
//        System.out.println("};");

    }

    public static float oklab(float l, float a, float b, float alpha) {
        return NumberUtils.intBitsToFloat(((int) (alpha * 255) << 24 & 0xFE000000) | ((int) (b * 255) << 16 & 0xFF0000)
                | ((int) (a * 255) << 8 & 0xFF00) | ((int) (l * 255) & 0xFF));
    }
    private static double cube(final double x) {
        return x * x * x;
    }

    public static int toRGBA8888(final float packed)
    {
        final int decoded = NumberUtils.floatToRawIntBits(packed);
        final double L = reverseLight((decoded & 0xff) / 255f);
        final double A = ((decoded >>> 8 & 0xff) - 127f) / 127f;
        final double B = ((decoded >>> 16 & 255) - 127f) / 127f;
        final double l = cube(L + 0.3963377774f * A + 0.2158037573f * B);
        final double m = cube(L - 0.1055613458f * A - 0.0638541728f * B);
        final double s = cube(L - 0.0894841775f * A - 1.2914855480f * B);
        final int r = (int)(Math.sqrt(Math.min(Math.max(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s, 0.0), 1.0)) * 255.999999);
        final int g = (int)(Math.sqrt(Math.min(Math.max(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s, 0.0), 1.0)) * 255.999999);
        final int b = (int)(Math.sqrt(Math.min(Math.max(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s, 0.0), 1.0)) * 255.999999);
        return r << 24 | g << 16 | b << 8 | (decoded & 0xfe000000) >>> 24 | decoded >>> 31;
    }

    public static double forwardLight(final double L) {
//        return L;
        return Math.pow(L, 2.2);
//        return Math.pow(L, 1.5);
    }

    public static double reverseLight(double L) {
//        return L;
        return Math.pow(L, 1.0/2.2);
//        return Math.pow(L, 2.0/3.0);
    }

    public static int oklabToRGB(double L, double A, double B)
    {
        L = reverseLight(L);
        double l = (L + 0.3963377774 * A + 0.2158037573 * B);
        double m = (L - 0.1055613458 * A - 0.0638541728 * B);
        double s = (L - 0.0894841775 * A - 1.2914855480 * B);
        l *= l * l;
        m *= m * m;
        s *= s * s;
        final int r = (int)(Math.sqrt(Math.min(Math.max(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s, 0.0), 1.0)) * 255.9999);
        final int g = (int)(Math.sqrt(Math.min(Math.max(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s, 0.0), 1.0)) * 255.9999);
        final int b = (int)(Math.sqrt(Math.min(Math.max(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s, 0.0), 1.0)) * 255.9999);
        return r << 24 | g << 16 | b << 8 | 255;
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
        double[] centroids = new double[basePalette.length << 2];
        for (int it = 1; it <= 1024; it++) {
            pr.exact(basePalette, HexGenerator.METRIC);
            byte[] pm = pr.paletteMapping;
            int index;
            double count;
            for (int i = 0; i < 0x8000; i++) {
                index = (pm[i] & 0xFF) << 2;
                double r = (i >>> 10) / 31.0;
                double g = (i >>> 5 & 0x1F) / 31.0;
                double b = (i & 0x1F) / 31.0;

                r *= r;
                g *= g;
                b *= b;

                double l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
                double m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
                double s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

                centroids[0+index] += forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
                centroids[1+index] += 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
                centroids[2+index] += 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;
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
                    mixingPalette.add(oklabToRGB(centroids[i<<2] / count,
                            centroids[i<<2|1] / count,
                            centroids[i<<2|2] / count));
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
                    double r, g, b, l, m, s, L, A, B;

                    r = (mixingPalette.get(i) >>> 24) / 255.0;
                    g = (mixingPalette.get(i) >>> 16 & 255) / 255.0;
                    b = (mixingPalette.get(i) >>> 8 & 255) / 255.0;
                    r *= r;
                    g *= g;
                    b *= b;

                    l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
                    m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
                    s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

                    L = forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
                    A = 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
                    B = 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;

                    r = (mixingPalette.get(j) >>> 24) / 255.0;
                    g = (mixingPalette.get(j) >>> 16 & 255) / 255.0;
                    b = (mixingPalette.get(j) >>> 8 & 255) / 255.0;
                    r *= r;
                    g *= g;
                    b *= b;

                    l = Math.cbrt(0.4121656120 * r + 0.5362752080 * g + 0.0514575653 * b);
                    m = Math.cbrt(0.2118591070 * r + 0.6807189584 * g + 0.1074065790 * b);
                    s = Math.cbrt(0.0883097947 * r + 0.2818474174 * g + 0.6302613616 * b);

                    L += forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
                    A += 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
                    B += 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;

                    removalSet.add(mixingPalette.get(i));
                    removalSet.add(mixingPalette.get(j));

                    int fusion = oklabToRGB(L * 0.5, A * 0.5, B * 0.5);
                    mixingPalette.add(fusion);
                    System.out.printf("Replacing close colors with their blend, %08X.\n", fusion);
                }
            }
        }
        mixingPalette.removeAll(removalSet);

        rgba.clear();
        rgba.addAll(mixingPalette);
        mixingPalette.clear();
        while (!ended)
            add();
    }
}
