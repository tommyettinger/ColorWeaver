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
import com.badlogic.gdx.utils.*;
import com.github.tommyettinger.digital.MathTools;
import com.github.tommyettinger.ds.IntList;
import com.github.tommyettinger.ds.IntSet;
import com.github.tommyettinger.ds.support.sort.IntComparator;

import java.lang.StringBuilder;
import java.util.Arrays;
/*
First complete result:
{
0x00000000, 0x000000FF, 0xFFFFFFFF, 0x080808FF, 0x101010FF, 0x181818FF, 0x202020FF, 0x292929FF,
0x313131FF, 0x393939FF, 0x414141FF, 0x4A4A4AFF, 0x525252FF, 0x5A5A5AFF, 0x626262FF, 0x6A6A6AFF,
0x737373FF, 0x7B7B7BFF, 0x838383FF, 0x8B8B8BFF, 0x949494FF, 0x9C9C9CFF, 0xA4A4A4FF, 0xACACACFF,
0xB4B4B4FF, 0xBDBDBDFF, 0xC5C5C5FF, 0xCDCDCDFF, 0xD5D5D5FF, 0xDEDEDEFF, 0xE6E6E6FF, 0xEEEEEEFF,
0xF6F6F6FF, 0xF523BBFF, 0xDBDF35FF, 0xAB8B67FF, 0x72397AFF, 0x35D6C3FF, 0x3277C8FF, 0xF5B03DFF,
0xC35A6DFF, 0x3B9B30FF, 0xF737EEFF, 0xB0F8A5FF, 0x18443FFF, 0x929ECDFF, 0x703FC2FF, 0xCE1870FF,
0x79722FFF, 0xF2C8A6FF, 0x471A39FF, 0xB86CC3FF, 0xCE9427FF, 0x903E32FF, 0xCB1FC7FF, 0x7BD878FF,
0x538794FF, 0x38217EFF, 0xDD5A26FF, 0xE8F83DFF, 0xB4A572FF, 0x260A12FF, 0x865387FF, 0x39EDE0FF,
0xE1162BFF, 0x3E90F2FF, 0x491BE0FF, 0xD6737CFF, 0x921686FF, 0x6AB634FF, 0x296752FF, 0xA6BDEFFF,
0x0E0E33FF, 0x8160D3FF, 0xF12384FF, 0x898A31FF, 0xEFE1ACFF, 0x5E344EFF, 0xCA8BD4FF, 0x931CC9FF,
0xD8AE2EFF, 0xA85843FF, 0xD94BCEFF, 0x6BFA84FF, 0x5CA1A0FF, 0x50469AFF, 0xF2772CFF, 0xB21846FF,
0xCAC686FF, 0x391F0EFF, 0x9C6C97FF, 0xF72A27FF, 0x45ACF2FF, 0x5042EEFF, 0x783D19FF, 0xF39284FF,
0xB82493FF, 0x73D037FF, 0x467F65FF, 0xA8D9EFFF, 0x172F58FF, 0x887FEEFF, 0xF76189FF, 0x95A637FF,
0x754D5EFF, 0xE99CEEFF, 0xB326EEFF, 0x192A9AFF, 0xBA734DFF, 0x7B1953FF, 0xF472E7FF, 0x215B1DFF,
0x58C0ACFF, 0x5865A9FF, 0xCE3250FF, 0xD7DD87FF, 0x1D1BEAFF, 0x214115FF, 0xB388ABFF, 0x7C1FA0FF,
0x40D1EFFF, 0x3370F0FF, 0x8E591BFF, 0xF3B18BFF, 0x490E18FF, 0xC453A2FF, 0x4AF63AFF, 0x34A173FF,
0xADF8E8FF, 0x1C496FFF, 0x8D98F3FF, 0x6E21EEFF, 0x931918FF, 0x9FBE39FF, 0x8E646AFF, 0xE8BBEEFF,
0x431163FF, 0xB05FF2FF, 0x2148B9FF, 0xD1905AFF, 0x903965FF, 0x277422FF, 0x80D6B2FF, 0x647EBBFF,
0x4216A7FF, 0xE5535AFF, 0xE9F88BFF, 0x484D1FFF, 0xCB9EBBFF, 0x894BB5FF, 0xA2721FFF, 0x6B151BFF,
0xDA72B0FF, 0x3DB677FF, 0x296481FF, 0x8350F1FF, 0xAC3D1DFF, 0xDC22A3FF, 0xA0E03AFF, 0x768F68FF,
0xF1D0EFFF, 0x543073FF, 0xC685F5FF, 0x2267BEFF, 0xDDAA65FF, 0xA55371FF, 0x278B26FF, 0xDC25EDFF,
0x5AF9C1FF, 0x6B96CAFF, 0x4E34B7FF, 0xF27769FF, 0xA71B6DFF, 0x586926FF, 0xE0B6C0FF, 0x2D103DFF,
0x9469C1FF, 0xF4275DFF, 0xAC8C2AFF, 0x733338FF, 0xF185B8FF, 0xAC1EB5FF, 0x39D184FF, 0x2A7B94FF,
0x181482FF, 0xBE5721FF, 0xF554B9FF, 0xAAF83CFF, 0x83A879FF, 0x65538BFF, 0xBE1B1FFF, 0x278AD0FF,
0xEFC744FF, 0x1B19BBFF, 0xB37380FF, 0x711574FF, 0x30AD2CFF, 0x1F5651FF, 0x7BB5DEFF, 0x5858CFFF,
0xC33D7CFF, 0x638329FF, 0xB3E2BFFF, 0x3A3158FF, 0xA683D3FF, 0x6D17C0FF, 0xB7A82DFF, 0x815945FF,
0xF3A4C5FF, 0xB34ACBFF, 0x3CED92FF, 0x2A96A4FF, 0x233E8CFF, 0xD07627FF, 0x8F1841FF, 0x9BBE80FF,
0x746A96FF, 0xD73A21FF, 0x2DA3D5FF, 0xF1E260FF, 0x223DE6FF, 0x563117FF, 0xCC8C8BFF, 0x8F3694FF,
0x30C634FF, 0x23775FFF, 0x78D5E6FF, 0x13135BFF, 0x6972ECFF, 0xE44F8CFF, 0x749D33FF, 0xE6F9C6FF,
0x484A70FF, 0xB5A6ECFF, 0x8F27EFFF, 0xC4C736FF, 0x957353FF, 0x5E1249FF, 0xD666EBFF, 0x34B2B0FF,
0x27599DFF, 0xF39332FF, 0xA83D52FF, 0xA7DE8BFF, 0x162D13FF, 0x8A83AFFF, 0x5C1790FF, 0xF75731FF,
0x31BEE2FF, 0x2459EBFF, 0x67541EFF, 0xD4A892FF, 0xA44E9DFF, 0x38DF37FF, 0x2A8D64FF, 0x6CF5F0FF,
0x0E385EFF, 0x798EEFFF, 0x5C1AD9FF, 0x711E1AFF, 0xF07095FF, 0x85B557FF, 0x5C6365FF, 0xC7B7EBFF,
0x2C054DFF, 0x9C59E5FF, 0xFC2091FF, 0xDAD818FF, 0xA9885BFF, 0x70365EFF, 0xE682E6FF, 0x60C8B0FF,
0x4A71AEFF, 0xFAA737FF, 0xBD565BFF, 0xF93CDFFF, 0xBEEF9DFF, 0x31432FFF, 0x989BADFF, 0x6D41A3FF,
0x3079FDFF, 0x7C681BFF, 0xE9BFA0FF, 0x3F1A28FF, 0xB569A8FF, 0x33FB52FF, 0x25A775FF, 0x165271FF,
0x84A9FFFF, 0x6943F0FF, 0x8A3923FF, 0xC426A0FF, 0x90CF5DFF, 0x6B7D72FF, 0xD5D2F8FF, 0x3F2864FF,
0xAC76F7FF, 0xE9F211FF, 0xBAA163FF, 0x854F6DFF, 0xF89DF4FF, 0xC232EDFF, 0x68E3BBFF, 0x548CBFFF,
0x3D29A9FF, 0xD27066FF, 0x8E0765FF, 0x3F5C3BFF, 0xA6B5BAFF, 0x130B27FF, 0x7D5DB6FF, 0xDE2E65FF,
0x8D8120FF, 0xF9D9A8FF, 0x3C17F7FF, 0x553336FF, 0xC884B6FF, 0x8F1CABFF, 0x2AC27FFF, 0x1D6D82FF,
0x170562FF, 0xA1532BFF, 0xDB49B0FF, 0x9CE962FF, 0x78967EFF, 0x4F4278FF, 0x9013F8FF, 0xA7082EFF,
0xCABB6BFF, 0x98697BFF, 0x6FFEC5FF, 0x5CA7CFFF, 0x4849C0FF, 0xE6896FFF, 0xA63175FF, 0x4C7645FF,
0xB3CFC5FF, 0x24253DFF, 0x8C78C7FF, 0xF74E70FF, 0x9D9B24FF, 0x694C43FF, 0xD99EC3FF, 0xA440BEFF,
0x2FDC88FF, 0x248791FF, 0x1C2C7CFF, 0xB66C31FF, 0x71173DFF, 0xF066BFFF, 0x85B089FF, 0x5F5C89FF,
0xC13337FF, 0xD9D571FF, 0x1028C5FF, 0x3B2C0CFF, 0xA98288FF, 0x72297FFF, 0x64C1DDFF, 0x5166D4FF,
0xFAA377FF, 0xBD4E83FF, 0x578F4FFF, 0xBFEAD0FF, 0x323E4FFF, 0x9B92D7FF, 0x712DC7FF, 0xACB426FF,
0x7B654FFF, 0xE9B8CFFF, 0x401042FF, 0xB75ED0FF, 0x33F790FF, 0x2AA29FFF, 0x224992FF, 0xC98637FF,
0x8A344BFF, 0x90CA93FF, 0x041D1AFF, 0x6D7699FF, 0x431382FF, 0xD9503FFF, 0xE8EF76FF, 0x4E4515FF,
0xBA9C93FF, 0x864691FF, 0x6CDCEAFF, 0x5A81E7FF, 0x561817FF, 0xD26991FF, 0x62A957FF, 0x40575FFF,
0xA8ADE6FF, 0x814EDCFF, 0xDE1D8BFF, 0xBACE27FF, 0x8C7E59FF, 0xF9D3DBFF, 0x562C55FF, 0xC979E0FF,
0x2FBCACFF, 0x2864A6FF, 0xDC9F3CFF, 0xA04E58FF, 0x338607FF, 0xDC35D8FF, 0x9BE59CFF, 0x123629FF,
0x7A90A8FF, 0x533599FF, 0xF06B46FF, 0x605E1DFF, 0xCAB69EFF, 0x26111FFF, 0x9960A1FF, 0x72F7F6FF,
0xFB1A4AFF, 0x639DF9FF, 0x5133E5FF, 0x6E3221FF, 0xE6839DFF, 0xA71F98FF, 0x6CC45EFF, 0x4C716DFF,
0xB5C8F4FF, 0x281A58FF, 0x906BEFFF, 0xF74499FF, 0xC8E926FF, 0x9C9762FF, 0x694666FF, 0xDB94EFFF,
0xA726E4FF, 0x33D7B8FF, 0x2D7FB8FF, 0xEEB93FFF, 0x28149CFF, 0xB56863FF, 0x3CA00CFF, 0xF257E8FF,
0xA5FFA4FF, 0x1E5036FF, 0x86AAB6FF, 0x6251ADFF, 0xC12B60FF, 0x707723FF, 0xD9D0A7FF, 0x3B292FFF,
0xAB7BB1FF, 0x750BA0FF, 0x5A55FBFF, 0x844B2AFF, 0xF99DA8FF, 0xBE42A9FF, 0x76DE64FF, 0x588B7AFF,
0x36366EFF, 0x8A032BFF, 0xABB16BFF, 0x7C5F75FF, 0xEBAFFDFF, 0xBA4CF7FF, 0x36F2C3FF, 0x339AC8FF,
0xFFD341FF, 0x2E3AB4FF, 0xC9816DFF, 0x8A2A6DFF, 0x44BB0CFF, 0x286942FF, 0x92C5C2FF, 0x0B1731FF,
0x706CC0FF, 0xD9496CFF, 0x7F9029FF, 0xE7EAB0FF, 0x4E413EFF, 0xBB95BFFF, 0x8936B5FF, 0x996432FF,
0x561036FF, 0xD35FB9FF, 0x7FF96AFF, 0x63A586FF, 0x435081FF, 0xA42E35FF, 0xB9CB72FF, 0x232109FF,
0x8D7983FF, 0x581F74FF, 0x37B5D8FF, 0x3458CBFF, 0xDB9B76FF, 0xA0477DFF, 0x4BD509FF, 0x32834CFF,
0x9CDFCEFF, 0x163146FF, 0x7D87D1FF, 0x591CBBFF, 0xF06577FF, 0x8CAA2DFF, 0x5F5A4BFF, 0xCBAFCCFF,
0x9B54C8FF, 0xAC7D39FF, 0x6E2D45FF, 0xE77AC8FF, 0x6DBF91FF, 0x4F6A92FF, 0xBC4A3EFF, 0xF832C1FF,
0xC7E578FF, 0x343914FF, 0x9C9290FF, 0x6C3C88FF, 0x3BD0E6FF, 0x3974DFFF, 0xEDB57EFF, 0x3B1012FF,
0xB5618CFF, 0x3B9E56FF, 0xA6FAD8FF, 0x214B57FF, 0x89A2E0FF, 0x6841D2FF, 0xC11985FF, 0x9AC430FF,
0x6F7456FF, 0xDAC9D8FF, 0x3D224CFF, 0xAD70D9FF, 0xBE963FFF, 0x844653FF, 0xFA95D5FF, 0xC02CCFFF,
0x76DA9BFF, 0x5B85A2FF, 0x3C278DFF, 0xD36446FF, 0xD3FF7EFF, 0x44521DFF, 0xABAC9BFF, 0x0F0513FF,
0x7D569AFF, 0x3DEBF3FF, 0xDC1A48FF, 0x3E90F1FF, 0xFECF86FF, 0x3B1CD8FF, 0x53291EFF, 0xC97B99FF,
0x8C158FFF, 0x42B85EFF, 0x2B6567FF, 0x95BDEFFF, 0x140649FF, 0x755FE7FF, 0xD93F94FF, 0xA6DE31FF,
0x7E8D60FF, 0xE8E4E3FF, 0x4F3B5EFF, 0xBD8BE9FF, 0x8D14D9FF, 0xCFB043FF, 0x98605FFF, 0xD54FE1FF,
0x7EF4A4FF, 0x659FB1FF, 0x4945A3FF, 0xE87E4DFF, 0xA4265BFF, 0x526C25FF, 0xB9C6A5FF, 0x231E27FF,
0x8E71AAFF, 0xF64051FF, 0x4145F0FF, 0x694228FF, 0xDB95A5FF, 0xA23AA2FF, 0x4AD265FF, 0x347F75FF,
0x9FD7FDFF, 0x1D2862FF, 0x827BF9FF, 0xF05CA2FF, 0xB2F931FF, 0x8CA76AFF, 0xF5FEEDFF, 0x60556FFF,
0xCDA6F8FF, 0x9F40EEFF, 0xBF210EFF, 0xDFCA47FF, 0x1428A8FF, 0xAC796AFF, 0x6F2265FF, 0xE96DF2FF,
0x6FB9BEFF, 0x5460B7FF, 0xFC9853FF, 0xBC4468FF, 0x60852BFF, 0xC6E0AFFF, 0x0A0FF5FF, 0x343637FF,
0x9E8BB9FF, 0x6F2AABFF, 0x7D5B31FF, 0xEDAFB0FF, 0x3B062CFF, 0xB757B3FF, 0x50ED6CFF, 0x3C9982FF,
0x274377FF, 0x6F22F8FF, 0x872932FF, 0x99C172FF, 0x0B1405FF, 0x706E7EFF, 0x401068FF, 0xD84213FF,
0xEEE44AFF, 0x0B49C0FF, 0xBD9274FF, 0x853E76FF, 0x78D4CAFF, 0x5F7BC9FF, 0xD25F74FF, 0x6C9F31FF,
0xD3FBB8FF, 0x444F45FF, 0xADA5C7FF, 0x8149BFFF, 0x8F7439FF, 0xFEC9BAFF, 0x53243EFF, 0xCA72C2FF,
0x44B48EFF, 0x305E8AFF, 0x9F433CFF, 0xDA2CBAFF, 0xA5DB79FF, 0x192D11FF, 0x7F888BFF, 0x52307EFF,
0xF05E16FF, 0xFDFE4BFF, 0xCEAC7EFF, 0x21070CFF, 0x995886FF, 0x80EFD6FF, 0x6A96DAFF, 0x5032C7FF,
0xE7797FFF, 0xA4127DFF, 0x78B935FF, 0x526852FF, 0xBABFD4FF, 0x251640FF, 0x9165D1FF, 0xF5367DFF,
0xA08D40FF, 0x693D4DFF, 0xDC8DD0FF, 0xA422C6FF, 0x4ACE99FF, 0x39789BFF, 0x271580FF, 0xB55D45FF,
0xF14FCBFF, 0xB0F580FF, 0x27461BFF, 0x8CA298FF, 0x634C92FF, 0xBE1845FF, 0xDEC686FF, 0x392019FF,
0xAC7294FF, 0x73B1EAFF, 0x5B52DDFF, 0xFB9388FF, 0xBC398EFF, 0x83D438FF, 0x5F825EFF, 0xC8DAE0FF,
0x363055FF, 0xA181E3FF, 0xB0A746FF, 0x7D565BFF, 0xEEA7DEFF, 0xB946D9FF, 0x50E9A2FF, 0x4093ABFF,
0x2F3798FF, 0xCA774DFF, 0x872054FF, 0x336024FF, 0x99BCA3FF, 0x0C111CFF, 0x7266A3FF, 0xD83C4FFF,
0xEEE08DFF, 0x2832E4FF, 0x4E3825FF, 0xBE8CA1FF, 0x873199FF, 0x7CCCF8FF, 0x666FF1FF, 0xD2569CFF,
0x8DEE3AFF, 0x6C9C68FF, 0xD4F4EBFF, 0x454967FF, 0xAF9CF2FF, 0x8533E4FF, 0xA11E12FF, 0xC0C04BFF,
0x8F7067FF, 0xFEC2EAFF, 0x55195BFF, 0xCC65EBFF, 0x48ADB9FF, 0x3853AEFF, 0xDE9054FF, 0x9F3D63FF,
0x3E7A2CFF, 0xA5D6ADFF, 0x1A2A2FFF, 0x8180B3FF, 0x571B9FFF, 0xEF5958FF, 0xFCFA93FF, 0x2655FBFF,
0x61512FFF, 0xCFA6ADFF, 0x9B4EABFF, 0x6B222DFF, 0xE871AAFF, 0x77B671FF, 0x536377FF, 0x9655F9FF,
0xBB3D18FF, 0xF524A5FF, 0xCEDA4FFF, 0xA08972FF, 0x6A356EFF, 0xDF81FBFF, 0x4EC8C6FF, 0x406FC1FF,
0xF0AA5AFF, 0xB55870FF, 0x499433FF, 0xF33AF3FF, 0xB0F0B7FF, 0x27433FFF, 0x8E9BC2FF, 0x673DB5FF,
0x736A38FF, 0xDFC0B8FF, 0x3A1B35FF, 0xAE69BCFF, 0x833C39FF, 0xFC8CB6FF, 0xBE25B3FF, 0x82D079FF,
0x617D86FF, 0x3A2473FF, 0xD3581DFF, 0xDCF552FF, 0xB0A37CFF, 0x7E4F7FFF, 0x54E3D3FF, 0x478AD3FF,
0x3A1FBAFF, 0xCA727BFF, 0x880575FF, 0x53AE39FF, 0x335C4DFF, 0x9BB5D0FF, 0x110431FF, 0x765AC9FF,
0xD73378FF, 0x838340FF, 0xEEDAC2FF, 0x4F3446FF, 0xBF84CBFF, 0x8A12BCFF, 0x995543FF, 0xD448C4FF,
0x8CEA81FF, 0x6D9793FF, 0x494088FF, 0xE97221FF, 0xA11541FF, 0xBFBC85FF, 0x211613FF, 0x90698EFF,
0x59FEDEFF, 0xF42E2FFF, 0x4EA5E3FF, 0x4243D2FF, 0xDD8B86FF, 0xA03286FF, 0x5CC83DFF, 0x3F765AFF,
0xA7CFDDFF, 0x1E234AFF, 0x8476DBFF, 0xEF5185FF, 0x929D48FF, 0xFCF5CBFF, 0x614C55FF, 0xD09ED9FF,
0x9E3CD1FF, 0x16268BFF, 0xAD6F4CFF, 0x6B194CFF, 0xE966D4FF, 0x78B1A0FF, 0x575A9BFF, 0xFE8C23FF,
0xBA384CFF, 0xCDD68DFF, 0x0E18D6FF, 0x352D20FF, 0xA1839CFF, 0x6C268FFF, 0x54C0F3FF, 0x4961E7FF,
0xF0A590FF, 0xB64F96FF, 0x64E341FF, 0x499065FF, 0xB2EAE8FF, 0x2A3D5EFF, 0x9291ECFF, 0x6D23D9FF,
0x841913FF, 0xA0B64EFF, 0x736662FF, 0xE0B9E6FF, 0x3C0C4FFF, 0xB05BE3FF, 0x1745A3FF, 0xC08854FF,
0x83365CFF, 0xFD82E2FF, 0x0E6D2BFF, 0x82CBABFF, 0x6475ACFF, 0xD25357FF, 0xDBF194FF, 0x46462CFF,
0xB19DA9FF, 0x8044A3FF, 0x517EFBFF, 0x501B27FF, 0xCA6AA5FF, 0x6BFD43FF, 0x52AA6FFF, 0x365770FF,
0x9EACFCFF, 0x7C48EFFF, 0x9E371BFF, 0xD81F9EFF, 0xADD053FF, 0x837F6EFF, 0xEFD3F3FF, 0x502B64FF,
0xC278F5FF, 0x1861B8FF, 0xD2A25BFF, 0x99506BFF, 0x188733FF, 0xD631EBFF, 0x8CE6B5FF, 0x6F8FBCFF,
0x4F30AAFF, 0xE86E60FF, 0x565F36FF, 0xC0B7B5FF, 0x22102AFF, 0x925FB4FF, 0xF32460FF, 0xA48203FF,
0x4D26F7FF, 0x683434FF, 0xDE84B2FF, 0xA21BAAFF, 0x5BC578FF, 0x417180FF, 0x241365FF, 0xB55221FF,
0xF045ADFF, 0xBAEB57FF, 0x929979FF, 0xFDEEFEFF, 0x634577FF, 0xA21AF7FF, 0x197DCBFF, 0xE3BB62FF,
0xAD6977FF, 0x20A23AFF, 0xEB55FDFF, 0x7BAACBFF, 0x5C4EBFFF, 0xFD8868FF, 0xBA2E72FF, 0x657840FF,
0xCED1C0FF, 0x35293DFF, 0xA37AC5FF, 0xB59C00FF, 0x7D4D40FF, 0xF09EBFFF, 0xB740BCFF, 0x63DF81FF,
0x4B8B8EFF, 0x30337DFF, 0xCB6B26FF, 0x84103CFF, 0xA0B283FF, 0x745F88FF, 0xD62C30FF, 0x1998DDFF,
0xF3D567FF, 0x2932C6FF, 0xC08383FF, 0x842A7EFF, 0x27BC40FF, 0x116A55FF, 0x85C4D8FF, 0x04143CFF,
0x686AD3FF, 0xD24C80FF, 0x739248FF, 0xDBEBCAFF, 0x47424EFF, 0xB294D4FF, 0x8331C7FF, 0x90664AFF,
0x511043FF, 0xCC5ECDFF, 0x6AFA88FF, 0x54A59BFF, 0x3B4E92FF, 0xE0852AFF, 0x9D3248FF, 0xADCC8CFF,
0x1C221AFF, 0x847997FF, 0x541984FF, 0xEF4B36FF, 0x18B4EDFF, 0x2B53DDFF, 0x624707FF, 0xD29D8EFF,
0x9A468FFF, 0x2CD745FF, 0x1A8461FF, 0x8EDFE5FF, 0x092F53FF, 0x7485E5FF, 0x681412FF, 0xE8678CFF,
0x80AC4FFF, 0x575B5DFF, 0xC1AFE2FF, 0x9551DBFF, 0xF30888FF, 0xA37F53FF, 0x682D55FF, 0xDF7ADDFF,
0x5DC0A8FF, 0x4669A5FF, 0xF49F2DFF, 0xB54D54FF, 0xF131D5FF, 0xB9E795FF, 0x2B3A27FF, 0x9392A4FF,
0x663999FF, 0x16CFFCFF, 0x2D70F2FF, 0x755F0FFF, 0xE3B798FF, 0x361220FF, 0xAE619FFF, 0x31F249FF,
0x219E6DFF, 0x97FAF1FF, 0x114A67FF, 0x7FA0F6FF, 0x633AE4FF, 0x81311BFF, 0xFD8298FF, 0xBB1997FF,
0x8CC655FF, 0x65746AFF, 0xCFC9EFFF, 0x381F59FF, 0xA66DEDFF, 0xB4995BFF, 0x7D4764FF, 0xF295EBFF,
0xBA26E3FF, 0x64DAB3FF, 0x4F83B6FF, 0x381F9DFF, 0xCA675EFF, 0x3A5333FF, 0xA1ADB1FF, 0x7755ACFF,
0xD5225DFF, 0x877914FF, 0xF3D1A0FF, 0x4D2B2EFF, 0xC17CADFF, 0x860CA0FF, 0x27B977FF, 0x196479FF,
}
 */
public class SnugglyPaletteGenerator {
    private static final int limit = 1024;
    private static final IntList rgba = new IntList(limit);
    private static final IntList mixingPalette = new IntList(limit);
    private static int idx = 1;
    private static long LL = 0xD1B54A32D192ED03L, AA = 0xABC98388FB8FAC03L, BB = 0x8CB92BA72F3D8DD7L;

    private static void addGray(float lightness){
        float oklab = oklab(lightness, 0.5f, 0.5f, 1f);
        int rgb = toRGBA8888(oklab);
        rgba.add(rgb);
    }
    private static void add(){
        ++idx;

        LL += 0xD1B54A32D192ED03L;
        AA += 0xABC98388FB8FAC03L;
        BB += 0x8CB92BA72F3D8DD7L;

        double L = reverseLight((LL >>> 11) * 0x1p-53);
        double A = (AA >>> 11) * 0x1p-53 - 0.5;
        double B = (BB >>> 11) * 0x1p-53 - 0.5;

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
            int er = e >>> 24;
            int eg = e >>> 16 & 255;
            int eb = e >>> 8  & 255;
            if(Math.abs(r - er) + Math.abs(g - eg) + Math.abs(b - eb) <= 6) return;
        }
        rgba.add(rgb);
    }
    public static void main(String[] args) {
        rgba.add(0);

        addGray(0f);
        addGray(1f);

        float grayLimit = (float)Math.ceil(Math.sqrt(limit)) - 1;

        for (int i = 1; i < grayLimit; i++) {
            addGray(i / grayLimit);
        }

        while (rgba.size() < limit) {
            add();
        }
        System.out.println(idx + " attempts.");
        rgba.items = lloydCompletely(rgba.toArray());
        StringBuilder sb = new StringBuilder(12 * rgba.size() + 35).append("{\n");
        for (int i = 0; i < rgba.size(); i++) {
            appendHex(sb.append("0x"), rgba.get(i)).append(", ");
            if(7 == (i & 7)) sb.append('\n');
        }
        sb.append('}');
        System.out.println(sb);
        GdxNativesLoader.load();
        Gdx.files = new Lwjgl3Files();
        Gdx.files.local("snuggly-" + (limit-1) + ".txt").writeString(sb.toString(), false, "UTF-8");

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


    public static double reverseLight(double L) {
        return Math.pow(L, 2.0/3.0);
    }

    /**
     * Returns true if the given Oklab values are valid to convert losslessly back to RGBA.
     * @param L lightness channel, as a double from 0 to 1
     * @param A green-to-red chromatic channel, as a double from 0 to 1
     * @param B blue-to-yellow chromatic channel, as a double from 0 to 1
     * @return true if the given Oklab channels can be converted back and forth to RGBA
     */
    public static boolean inGamut(double L, double A, double B)
    {
        L = reverseLight(L);

        double l = (L + +0.3963377774 * A + +0.2158037573 * B);
        l *= l * l;
        double m = (L + -0.1055613458 * A + -0.0638541728 * B);
        m *= m * m;
        double s = (L + -0.0894841775 * A + -1.2914855480 * B);
        s *= s * s;

        double dr = Math.sqrt(+4.0767245293 * l - 3.3072168827 * m + 0.2307590544 * s)*255.0;
        final int r = (int)dr;
        if(Double.isNaN(dr) || r < 0 || r > 255) return false;
        double dg = Math.sqrt(-1.2681437731 * l + 2.6093323231 * m - 0.3411344290 * s)*255.0;
        final int g = (int)dg;
        if(Double.isNaN(dg) || g < 0 || g > 255) return false;
        double db = Math.sqrt(-0.0041119885 * l - 0.7034763098 * m + 1.7068625689 * s)*255.0;
        final int b = (int)db;
        return (!Double.isNaN(db) && b >= 0 && b <= 255);
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
        if(rgba.items.length != basePalette.length)
            rgba.setSize(basePalette.length);
        int[] palette = rgba.items;
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

                centroids[0+index] += PaletteReducer.forwardLight(0.2104542553 * l + 0.7936177850 * m - 0.0040720468 * s);
                centroids[1+index] += 1.9779984951 * l - 2.4285922050 * m + 0.4505937099 * s;
                centroids[2+index] += 0.0259040371 * l + 0.7827717662 * m - 0.8086757660 * s;
                centroids[3+index]++;
            }
            mixingPalette.clear();
            mixingPalette.addAll(palette, 0, 1);
            for (int i = 1; i < palette.length; i++) {
                count = centroids[i<<2|3];


                if(count == 0 || MathTools.isEqual(palette[i] >>> 24, palette[i] >>> 16 & 255, 3) &&
                        MathTools.isEqual(palette[i] >>> 16 & 255, palette[i] >>> 8 & 255, 3))
                    mixingPalette.add(palette[i]);
                else
                    mixingPalette.add(PaletteReducer.oklabToRGB(centroids[i<<2] / count,
                            centroids[i<<2|1] / count,
                            centroids[i<<2|2] / count));
            }
            mixPalette(0, false);
            if(Arrays.equals(palette, basePalette))
            {
                System.out.println("Palette completely Lloyd-ed in " + it + " iterations");
                return palette;
            }
            System.arraycopy(palette, 0, basePalette, 0, basePalette.length);
        }
        System.out.println("Palette not completely Lloyd-ed...");
        return palette;
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
                    if (doRemove < 0) {
                        removalSet.add(mixingPalette.get(i));
                        removalSet.add(mixingPalette.get(j));
                        System.out.printf("Combined 0x%08X and 0x%08X\n", mixingPalette.get(i), mixingPalette.get(j));
                        mixingPalette.add(Coloring.mixEvenly(mixingPalette.get(i), mixingPalette.get(j)));
                    }
                    else {
                        System.out.printf("0x%08X and 0x%08X are very close in size %d!\n", mixingPalette.get(i), mixingPalette.get(j), size);
                    }
                }
                if(doRemove > 0) {
                    if(closest > (closest = Math.min(closest, diff))) {
                        removalSet.clear();
                        removalSet.add(mixingPalette.get(i));
                        removalSet.add(mixingPalette.get(j));
                    }
                }
            }
        }
        if(doRemove > 0 && removalSet.size() >= 2) {
            IntSet.IntSetIterator it = removalSet.iterator();
            mixingPalette.add(Coloring.mixEvenly(it.nextInt(), it.nextInt()));
        }
        if(doRemove != 0) {
            mixingPalette.removeAll(removalSet);
        }

        if(doSort) {
            mixingPalette.sort(hueComparator);
        }
        rgba.clear();
        rgba.addAll(mixingPalette);
        mixingPalette.clear();
    }
}
