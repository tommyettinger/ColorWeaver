package colorweaver;

import colorweaver.tools.StringKit;
import colorweaver.tools.TrigTools;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;

import java.util.ArrayList;

import static colorweaver.tools.TrigTools.cos_;
import static colorweaver.tools.TrigTools.sin_;

public class HexGenerator extends ApplicationAdapter {
    private int[] palette;
    public static final String NAME = "surin-255";
    
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(".hex Palette Generator");
        config.setWindowedMode(640, 480);
        config.setIdleFPS(10);
        config.useVsync(true);
        config.setResizable(false);
        new Lwjgl3Application(new HexGenerator(), config);
        AutomaticPaletteTransformer.main(arg);
        AutomaticPalettizer.main(arg); 
    }

    @Override
    public void create() {
        palette =
//                Coloring.HALTONIC255;
                new int[]
//                { // halturvy
//                        0x00000000, 0x010101FF, 0xFEFEFEFF, 0x777777FF, 0x555555FF, 0xAAAAAAFF, 0x333333FF, 0xE0E0E0FF,
//                        0xC8C8C8FF, 0x563C18FF, 0x5076DDFF, 0x79303DFF, 0xAA8ECEFF, 0x1B295FFF, 0xD6F77EFF, 0x20C93AFF,
//                        0x9F6BD9FF, 0x5A6862FF, 0x6CA557FF, 0x2F0921FF, 0xC42784FF, 0x68208FFF, 0xFC8962FF, 0x3A1F29FF,
//                        0xF86FB2FF, 0x70E2B5FF, 0x4B1849FF, 0xA96A38FF, 0x91574BFF, 0xFA95E9FF, 0x1B8577FF, 0xF7441FFF,
//                        0xC5D04DFF, 0x0B151EFF, 0x9B867DFF, 0x171C3EFF, 0xAA27D8FF, 0x106839FF, 0x105346FF, 0xA2A84EFF,
//                        0x89CC48FF, 0x6E59B2FF, 0x1E53C7FF, 0x372FD0FF, 0x90B9F9FF, 0x6F3E97FF, 0x25BAD1FF, 0xDF3447FF,
//                        0x5339FFFF, 0x259ADDFF, 0xFB6A52FF, 0x591F16FF, 0x353C83FF, 0x48881AFF, 0xEC37B4FF, 0xF873F9FF,
//                        0xD01412FF, 0x6823DEFF, 0xEED17AFF, 0x9D3649FF, 0xA544FFFF, 0xF3AE55FF, 0xD2608EFF, 0x74FF6CFF,
//                        0x661A4FFF, 0x8B4719FF, 0x3A13A6FF, 0xDC24FFFF, 0x1EADA5FF, 0x080804FF, 0x7353F4FF, 0x66671AFF,
//                        0x599159FF, 0x152D1EFF, 0x8D1E78FF, 0x131F14FF, 0x27D6AAFF, 0xB85488FF, 0x31F9EAFF, 0x007D28FF,
//                        0xD310D1FF, 0x174353FF, 0xC36EFEFF, 0x8E32AFFF, 0xC395EFFF, 0xCC7F3FFF, 0x9A1F17FF, 0x3573AAFF,
//                        0x2A4212FF, 0x37EB57FF, 0xE4A4A6FF, 0x382771FF, 0x1B050AFF, 0x820B16FF, 0x508AB4FF, 0xF32D76FF,
//                        0x0A0B45FF, 0xA5C4A0FF, 0xEEB3FFFF, 0x47540AFF, 0xD1A828FF, 0xB84E1CFF, 0xF853ECFF, 0xA1F2BBFF,
//                        0xAB197DFF, 0xD7FBDAFF, 0x1C547DFF, 0x6C87FFFF, 0xCA8187FF, 0x52D5EDFF, 0x824E7AFF, 0x300B5EFF,
//                        0x0B6876FF, 0x19C28DFF, 0x925EBEFF, 0x00A648FF, 0x001074FF, 0x2660EBFF, 0x9C852CFF, 0x090121FF,
//                        0x51436FFF, 0x5EA7F8FF, 0x822EF3FF, 0x828AC5FF, 0x92A093FF, 0xFAFF23FF, 0x130FA2FF, 0x95759DFF,
//                        0x856B2EFF, 0x112FB8FF, 0x0C30FEFF, 0x26B71DFF, 0x820CB6FF, 0x00DD34FF, 0x8B5F84FF, 0xCE6BC2FF,
//                        0xD4E22FFF, 0x4A3952FF, 0x9CE0FCFF, 0xFA51A3FF, 0x500ABDFF, 0x6E4416FF, 0x495D99FF, 0xB7445AFF,
//                        0xF08112FF, 0x5A0B89FF, 0x7F0F67FF, 0x0E08F6FF, 0xCE6957FF, 0x301D06FF, 0x003914FF, 0x3E0533FF,
//                        0xC14CC0FF, 0x783F62FF, 0x300A81FF, 0xBE1B54FF, 0xF8E19DFF, 0xDC259AFF, 0x9A48A5FF, 0xFC4966FF,
//                        0xFDB29CFF, 0x6DC1C6FF, 0x009D96FF, 0x005415FF, 0x00000DFF, 0xC79976FF, 0x00FE2AFF, 0x87FFFFFF,
//                        0x3E47A9FF, 0xB4190DFF, 0x584ACDFF, 0xBDFE20FF, 0x78C188FF, 0xF7FF9AFF, 0x2CF79DFF, 0xD38FACFF,
//                        0x240F06FF, 0x00358AFF, 0x200424FF, 0x530C14FF, 0x585C2DFF, 0x867C1EFF, 0xC7B5E9FF, 0xFBC8B8FF,
//                        0x007960FF, 0x961B4BFF, 0xC159EEFF, 0x7866FFFF, 0xA89617FF, 0xFA2CE3FF, 0x4A4B44FF, 0xF48AC0FF,
//                        0xD25311FF, 0x342C11FF, 0xFCC21FFF, 0x00100AFF, 0x070832FF, 0xB219ADFF, 0x98E521FF, 0x0E87FFFF,
//                        0x18C1FFFF, 0xD5CFFCFF, 0x602A31FF, 0x132942FF, 0xC4AE7DFF, 0x75B124FF, 0x6D9A19FF, 0xD71259FF,
//                        0x6998A2FF, 0x620A37FF, 0x4F7A41FF, 0xA9420DFF, 0x0E5E5EFF, 0x6770B2FF, 0xBAD19AFF, 0xCFBB17FF,
//                        0x0085B3FF, 0x58066BFF, 0x240545FF, 0x631AC1FF, 0x5AA6C9FF, 0x4C0CFBFF, 0x888063FF, 0xF9EBFFFF,
//                        0x90BB25FF, 0x00EAFFFF, 0x48330EFF, 0x9EE78FFF, 0xD67BE9FF, 0x63DD86FF, 0xB95E5CFF, 0x92AAE5FF,
//                        0x4B5083FF, 0xF6E15BFF, 0x000E5EFF, 0x1100C7FF, 0x775451FF, 0x3F8C8BFF, 0xA97CA8FF, 0x452D50FF,
//                        0x004BFBFF, 0x184B69FF, 0x156EFAFF, 0x16152FFF, 0xF99428FF, 0x6F7DD9FF, 0x00395EFF, 0xB73DB9FF,
//                        0x460A07FF, 0x592A73FF, 0x20D271FF, 0x004039FF, 0x95CDFFFF, 0x7B413DFF, 0x9B0AD3FF, 0xEC6433FF,
//                };
//                { // haltok
//                        0x00000000, 0x010101FF, 0xFEFEFEFF, 0x777777FF, 0x555555FF, 0xAAAAAAFF, 0x333333FF, 0xE0E0E0FF,
//                        0xC8C8C8FF, 0x92072EFF, 0xBA81E2FF, 0x1B2076FF, 0xCEFE27FF, 0xAE54FCFF, 0x576A5EFF, 0x0A5D9BFF,
//                        0x5CAB26FF, 0x538E5DFF, 0x461425FF, 0x24EEA3FF, 0xAB4633FF, 0x0A1420FF, 0x55358FFF, 0xE7D05EFF,
//                        0xC210D3FF, 0xAB8071FF, 0x18174BFF, 0xE060B7FF, 0xB99CFEFF, 0xFD4A3FFF, 0x7947D2FF, 0xA1D363FF,
//                        0x598BB6FF, 0x581F61FF, 0x7D26B0FF, 0xB96079FF, 0x2A5F34FF, 0xC62765FF, 0x81F981FF, 0xD15D19FF,
//                        0xFA757FFF, 0x19A4EDFF, 0x2E4F6BFF, 0x4966A2FF, 0x3D31CDFF, 0x31D2E8FF, 0x9A3D74FF, 0x5972F5FF,
//                        0x411AA7FF, 0xEBA239FF, 0xF908DBFF, 0xEF9FA0FF, 0xFEA1E0FF, 0x627D15FF, 0xE18823FF, 0xEFBFFDFF,
//                        0xDD303FFF, 0x280C29FF, 0x69BA94FF, 0x923824FF, 0x7E975DFF, 0x4C4220FF, 0x120B0CFF, 0x132A47FF,
//                        0x6EC03BFF, 0x151F06FF, 0xF073F4FF, 0x8965AFFF, 0x053685FF, 0xD5348BFF, 0x3C63ECFF, 0x682825FF,
//                        0x8D2863FF, 0x4CB9F3FF, 0x2842F5FF, 0x7283F3FF, 0xBE8C97FF, 0x7C6311FF, 0x74F7E1FF, 0x080107FF,
//                        0x6DA1A5FF, 0x232D0EFF, 0x089D06FF, 0x1E83A7FF, 0x603657FF, 0xDCF7AEFF, 0x4CEC3AFF, 0x9A10FDFF,
//                        0x9F6A3BFF, 0x361209FF, 0x692BEBFF, 0x5D0E36FF, 0xF64BFAFF, 0x297538FF, 0xA843A9FF, 0x380652FF,
//                        0x0B4624FF, 0xE64980FF, 0x704995FF, 0x9052E5FF, 0xB8A838FF, 0xC8BB8DFF, 0x674A0CFF, 0xAA70BDFF,
//                        0x1B4450FF, 0x0EA984FF, 0xC87B1CFF, 0xB8B2DDFF, 0x27D596FF, 0x879ED8FF, 0xDC89BDFF, 0x12550DFF,
//                        0x0C0476FF, 0x258063FF, 0x65E2DAFF, 0xA45571FF, 0x270B05FF, 0x112AC8FF, 0xAE0D40FF, 0xA31BAAFF,
//                        0x19D931FF, 0x7D0B6AFF, 0x987F1AFF, 0x0B034DFF, 0x480E5FFF, 0xFEDF6EFF, 0xA69A1BFF, 0xD346BAFF,
//                        0x055ECCFF, 0xC3FDFAFF, 0x1949A3FF, 0x06021BFF, 0xC463FDFF, 0xF4B623FF, 0x200CADFF, 0x3A1A83FF,
//                        0x6D03ADFF, 0x815795FF, 0xF3CFA5FF, 0xC0E110FF, 0x0C26FEFF, 0x4D2301FF, 0x7F5258FF, 0xD00CFCFF,
//                        0xAFBE05FF, 0xFE6518FF, 0xFEB591FF, 0xDC7065FF, 0xDE957AFF, 0xFF87E8FF, 0x7E3010FF, 0xD21226FF,
//                        0xA6DEF6FF, 0x1BBB6DFF, 0x60FD1EFF, 0xFEFC65FF, 0x4B49BBFF, 0x12355CFF, 0x650BD9FF, 0xFF32A5FF,
//                        0x5E5D22FF, 0xA4DC9DFF, 0x27685AFF, 0x3D3667FF, 0xFE577EFF, 0xA90274FF, 0x1B2427FF, 0x098EF4FF,
//                        0x4C4C85FF, 0x089998FF, 0x958BB9FF, 0x721546FF, 0x443701FF, 0x65098AFF, 0x753D4CFF, 0x093B0FFF,
//                        0x99C079FF, 0x9AA171FF, 0x09B8C7FF, 0xC4039EFF, 0x99C8FBFF, 0xFF1D42FF, 0x105346FF, 0x0AC217FF,
//                        0xFB67BBFF, 0xBCEAA5FF, 0x060630FF, 0x8F7A98FF, 0x6D9522FF, 0x390D2EFF, 0x16830AFF, 0x87C2B8FF,
//                        0x83645FFF, 0x0A7AD8FF, 0x1B0220FF, 0x524843FF, 0x050A02FF, 0x8F08D8FF, 0x870B8AFF, 0x087583FF,
//                        0xC44A13FF, 0x4E46FDFF, 0xB355A8FF, 0x091C2FFF, 0x690B0AFF, 0x898264FF, 0x595A7BFF, 0x4B3240FF,
//                        0x274185FF, 0x7BD5A5FF, 0x051601FF, 0xFEFDC0FF, 0x914B14FF, 0x372246FF, 0x09CAC5FF, 0x8F69DEFF,
//                        0x099037FF, 0x576FB4FF, 0x597184FF, 0x09A64FFF, 0x4D10FDFF, 0xFDE7FEFF, 0xA6FF9AFF, 0x5107C5FF,
//                        0xD85C5AFF, 0x266B0FFF, 0x8A8DFCFF, 0xD73DFCFF, 0x6C51FDFF, 0x713A84FF, 0xB81E12FF, 0xC86990FF,
//                        0x78918EFF, 0xF90873FF, 0xFF849BFF, 0xCFEC50FF, 0xD91BB9FF, 0x56DC5FFF, 0x73794DFF, 0xFC8A3AFF,
//                        0xFFA169FF, 0x26A1BFFF, 0xB643DAFF, 0x7BBFFAFF, 0x3B1F0BFF, 0xAA0FD0FF, 0x5656C5FF, 0x870802FF,
//                        0x3F2958FF, 0x85E966FF, 0xD4B6FCFF, 0xBF8B58FF, 0xB06E56FF, 0xA35A0BFF, 0x0EFDB6FF, 0x26C87EFF,
//                };
//                { // haltoyo
//                        0x00000000, 0x010101FF, 0xFEFEFEFF, 0x777777FF, 0x555555FF, 0xAAAAAAFF, 0x333333FF, 0xE0E0E0FF,
//                        0xC8C8C8FF, 0x4972F1FF, 0x822638FF, 0xB189D3FF, 0x192767FF, 0xCFFB6EFF, 0xA464E3FF, 0x556A62FF,
//                        0x64A84AFF, 0x6C1397FF, 0x3F1B29FF, 0x56E8AFFF, 0x4E1050FF, 0xB06728FF, 0x3F4511FF, 0x995245FF,
//                        0xC2D22BFF, 0x0B151EFF, 0x4E3E83FF, 0xB33AC1FF, 0x9F847DFF, 0x161B41FF, 0xA4A934FF, 0xE46254FF,
//                        0x82CF34FF, 0x7253C0FF, 0x004FDBFF, 0x638AACFF, 0x3B1EE2FF, 0xFB47E7FF, 0xA52D96FF, 0x74389FFF,
//                        0xED1740FF, 0xE493D3FF, 0x601708FF, 0x3B1EADFF, 0xF81AB8FF, 0x93F29DFF, 0xE3838BFF, 0x43A3DCFF,
//                        0xF5CF62FF, 0x9E5FA0FF, 0x5D7B1EFF, 0xA82A44FF, 0x5BCEE2FF, 0xDAA95CFF, 0x68EF3FFF, 0x5E2141FF,
//                        0x209462FF, 0x912AF2FF, 0xD18F49FF, 0x426217FF, 0x333870FF, 0x0F2E1EFF, 0x0F4A51FF, 0x151F10FF,
//                        0x06041DFF, 0x4D89FDFF, 0xCB223AFF, 0x196BB7FF, 0x7E4D21FF, 0x7D8930FF, 0x87C486FF, 0x521186FF,
//                        0xA4B0FCFF, 0x4BBBA3FF, 0x070307FF, 0x5DAFF8FF, 0xEF4A8AFF, 0x1F618BFF, 0x6C16FFFF, 0xD037EFFF,
//                        0x2E0731FF, 0x2800A5FF, 0x735177FF, 0x3C57FBFF, 0xD5258CFF, 0x59EFF8FF, 0xBB506CFF, 0x00421BFF,
//                        0xE36CB3FF, 0x5B4531FF, 0x50C01CFF, 0x8B1379FF, 0xF7A3B2FF, 0xD3FDD3FF, 0xF06EF8FF, 0x0082C1FF,
//                        0x137F82FF, 0x009F1DFF, 0xE0AEFFFF, 0x91A288FF, 0x6B2067FF, 0x2DA190FF, 0xFAB46BFF, 0x240607FF,
//                        0x0F0B05FF, 0x005B2EFF, 0xA18E2AFF, 0x007548FF, 0xB6EB30FF, 0x0A08E3FF, 0x924CF5FF, 0x120177FF,
//                        0x090742FF, 0x84FCFCFF, 0xBD6A86FF, 0x0E42B2FF, 0x331359FF, 0xFDC3FEFF, 0xDA522CFF, 0x6312CBFF,
//                        0xBC68FDFF, 0x656EB1FF, 0x095179FF, 0x00DF8FFF, 0x59331BFF, 0x380911FF, 0x7B0E14FF, 0x8727C9FF,
//                        0xFC703FFF, 0x7A3D66FF, 0x8683D3FF, 0x8A6825FF, 0x972A19FF, 0xBF491DFF, 0x8E5488FF, 0xA6D09BFF,
//                        0xC67315FF, 0xCA4BB3FF, 0xF89179FF, 0xF9FE92FF, 0x4E53B3FF, 0x00CE7BFF, 0x372A09FF, 0xDCE7A2FF,
//                        0x3C1079FF, 0xBE0D66FF, 0x5E6A1AFF, 0x00005EFF, 0xB0BCBDFF, 0x523B52FF, 0x16071FFF, 0xF9384CFF,
//                        0xFA8A21FF, 0xBE8FFFFF, 0x00E41DFF, 0x00675CFF, 0xBB15F7FF, 0xF8C7BDFF, 0x00B112FF, 0x4940B1FF,
//                        0xBB8597FF, 0xFAFF23FF, 0xEFE429FF, 0xA911CAFF, 0x930EA3FF, 0x3D2CFEFF, 0x00909CFF, 0xFB88FFFF,
//                        0xB972C4FF, 0x2D1F0DFF, 0x052949FF, 0xB7B72CFF, 0x7AD7FFFF, 0x080695FF, 0x668D76FF, 0x9D2374FF,
//                        0xDB54CCFF, 0xB2DFC4FF, 0xF02178FF, 0x008E25FF, 0x0A395FFF, 0xE5B51BFF, 0xFA6376FF, 0x7A73CDFF,
//                        0xD616CEFF, 0xF977BDFF, 0x7597AAFF, 0xC0D4FFFF, 0xB03707FF, 0x7696F9FF, 0x59669AFF, 0x0098D9FF,
//                        0x9A174CFF, 0x11BD75FF, 0xFCEAF2FF, 0x003197FF, 0x3C2B5DFF, 0x8AA1C3FF, 0x0DAFB3FF, 0x18CFB7FF,
//                        0x2D5650FF, 0x4AFF80FF, 0x4E4E87FF, 0xEF18FFFF, 0x3D043AFF, 0x41350DFF, 0x675BE9FF, 0x99645BFF,
//                        0x008017FF, 0x00FFD2FF, 0xB14085FF, 0x648155FF, 0x5645E3FF, 0x7DDD6CFF, 0x3A3B45FF, 0x609C17FF,
//                        0x7D0C41FF, 0x00D41BFF, 0x495316FF, 0x967D58FF, 0xC1835BFF, 0x1C0C2DFF, 0x0008C7FF, 0xB3A86EFF,
//                        0x9BFFC5FF, 0x00C1DCFF, 0x732F14FF, 0xC215A4FF, 0x001506FF, 0x8D75A0FF, 0x5B1131FF, 0xAAC1F9FF,
//                        0xFAB5DFFF, 0x006EE5FF, 0x4B2CACFF, 0x372545FF, 0xD8175FFF, 0x7D5D31FF, 0x7A9B73FF, 0x526242FF,
//                        0x7968FCFF, 0x5F0C62FF, 0x0087E6FF, 0x9847AFFF, 0x68BD68FF, 0xB5DC8CFF, 0x5D455CFF, 0x0056B0FF,
//                        0x5E30DAFF, 0xDC7914FF, 0x944567FF, 0x502B85FF, 0x53090DFF, 0x9DA0E6FF, 0xD077EAFF, 0xFD49B3FF,
//                };
//                        { // haltesque
//                                0x00000000, 0x000000FF, 0xFFFFFFFF, 0x888888FF, 0x444444FF, 0xCCCCCCFF, 0x222222FF, 0xAAAAAAFF,
//                                0x666666FF, 0xEEEEEEFF, 0x111111FF, 0x999999FF, 0x555555FF, 0xDDDDDDFF, 0x333333FF, 0xBBBBBBFF,
//                                0x777777FF, 0x9F4026FF, 0x5578CEFF, 0x73353DFF, 0xA21DCFFF, 0xD4F695FF, 0x47C453FF, 0x9773CBFF,
//                                0x26D0D8FF, 0x2A2877FF, 0x73A264FF, 0xB93980FF, 0x622D83FF, 0xED9170FF, 0xE97BB2FF, 0xDD33BDFF,
//                                0x176AE1FF, 0x98234AFF, 0x8095E8FF, 0xEE9DE4FF, 0xE5573BFF, 0xC3CF67FF, 0x12569AFF, 0xBE7BA9FF,
//                                0xD5841AFF, 0xBC24FCFF, 0x94C860FF, 0xDA3D19FF, 0x6E5DA4FF, 0x10050FFF, 0x3A3AB9FF, 0x100D6DFF,
//                                0x49304FFF, 0x09A64FFF, 0x68478AFF, 0x355219FF, 0x5247E9FF, 0x711943FF, 0xEB7466FF, 0xF2AB4AFF,
//                                0xEE4281FF, 0x4C8635FF, 0xAA7357FF, 0xF962FFFF, 0xC752F9FF, 0x0B1146FF, 0x00E7B7FF, 0x40105FFF,
//                                0xB0A836FF, 0x147997FF, 0x4D1324FF, 0x9CEE3BFF, 0xB49AE8FF, 0x9D396EFF, 0x816126FF, 0xA3554AFF,
//                                0x931CA1FF, 0x2B0E14FF, 0x261FC3FF, 0x0698F2FF, 0x006D3CFF, 0x7EBAFCFF, 0x33094AFF, 0x1A3E0BFF,
//                                0x6EF3BBFF, 0xAF5C84FF, 0x4D1A87FF, 0xC5FD22FF, 0x704D13FF, 0x45E8FFFF, 0xBC79EAFF, 0x6F12BEFF,
//                                0xF7CE82FF, 0x0691AEFF, 0x40D732FF, 0x002F0CFF, 0x2EB2C9FF, 0x7747F9FF, 0x2F4283FF, 0xE2285DFF,
//                                0x0045E4FF, 0x56390EFF, 0x7564FEFF, 0xC6153BFF, 0x2762B4FF, 0x621B13FF, 0xEB52C0FF, 0x157860FF,
//                                0x1B7F17FF, 0xA74EBBFF, 0x79FFF0FF, 0xF71AEFFF, 0x20C8A4FF, 0x938D25FF, 0x020302FF, 0x1E2448FF,
//                                0x40ED4FFF, 0x4B06F9FF, 0x851E74FF, 0xF7B39BFF, 0xFDB7FCFF, 0x001D0CFF, 0x000534FF, 0x00687BFF,
//                                0x8B7223FF, 0x530CB9FF, 0x618F65FF, 0xEED415FF, 0x590D5FFF, 0xC920B1FF, 0x143F62FF, 0xB6856CFF,
//                                0x352807FF, 0x793AC7FF, 0x140FABFF, 0x391025FF, 0x30A3A4FF, 0x220786FF, 0x004D0EFF, 0x922F0FFF,
//                                0x1000FEFF, 0xC1BBFFFF, 0x76AE0BFF, 0x105569FF, 0x75B188FF, 0xFBFBAFFF, 0x1B0B2CFF, 0x830D17FF,
//                                0xC06B82FF, 0xF9FD43FF, 0xB40E95FF, 0xBEB06FFF, 0x5C7FFFFF, 0x20897AFF, 0x7C4C58FF, 0xEA85EBFF,
//                                0x6D00FCFF, 0xF390B1FF, 0xE59520FF, 0xF95E91FF, 0x5AADFCFF, 0x159918FF, 0x74D0FBFF, 0x54466DFF,
//                                0x8168A9FF, 0x474BBBFF, 0x95D8B2FF, 0x32FF19FF, 0xFD192AFF, 0x00B977FF, 0xA358ECFF, 0x5D6314FF,
//                                0xFCE43DFF, 0x113496FF, 0xBEE887FF, 0xC27615FF, 0x5D8BC3FF, 0x891048FF, 0x6A106FFF, 0x00FF8CFF,
//                                0x0C453AFF, 0x9FD625FF, 0x9544A6FF, 0xBC996DFF, 0x5D7759FF, 0xB21420FF, 0x0B305BFF, 0x00D98BFF,
//                                0x82C6A7FF, 0xC55EB5FF, 0xB35D18FF, 0x00B417FF, 0x9A6FFFFF, 0x610836FF, 0x91557EFF, 0xFB5F22FF,
//                                0x003235FF, 0x0A6047FF, 0x5B4626FF, 0xBD3B14FF, 0x861BFBFF, 0xEC1A8AFF, 0x75E882FF, 0xFABC2BFF,
//                                0x000904FF, 0xF8E0ABFF, 0xA30D76FF, 0x3E5E12FF, 0xEA4FFFFF, 0x2C0433FF, 0x0075FFFF, 0x412544FF,
//                                0x58355DFF, 0x6162D5FF, 0x4D5E8AFF, 0xA29927FF, 0x00260FFF, 0x44340DFF, 0xC593A4FF, 0x240607FF,
//                                0xFC9D89FF, 0x9E29F9FF, 0x62BBCCFF, 0x94749CFF, 0xFE4D5CFF, 0xFEC6BEFF, 0xB9A8FFFF, 0x7DFF86FF,
//                                0xC7B622FF, 0xFB5FCEFF, 0x5E7519FF, 0x678F1AFF, 0x17031EFF, 0xC55A58FF, 0x9DE1FEFF, 0x0655FFFF,
//                                0x8A8DB8FF, 0x001D2CFF, 0xFB7A25FF, 0xDD142BFF, 0xFC28ACFF, 0x89C118FF, 0xD90CFFFF, 0x3D04E1FF,
//                                0x0087EDFF, 0x421509FF, 0x3605ADFF, 0x753E65FF, 0xFBCEFFFF, 0x001E49FF, 0xF6B0D3FF, 0xC7469DFF,
//                                0x5D3239FF, 0x000018FF, 0xBF445DFF, 0x393377FF, 0x457EA7FF, 0x9E7A18FF, 0xBAFFD8FF, 0xBF116CFF,
//                        };
                        { // surin
                                0x00000000, 0x000000FF, 0x141414FF, 0xFFFFFFFF, 0x878787FF, 0xCCCCCCFF, 0x4F4F4FFF, 0xEEEEEEFF,
                                0x282828FF, 0x999999FF, 0x757575FF, 0xDDDDDDFF, 0x3B3B3BFF, 0xBBBBBBFF, 0x626262FF, 0xAAAAAAFF,
                                0x9B9783FF, 0x514E3DFF, 0xE10788FF, 0xDC8EA1FF, 0xA55F72FF, 0x5E2637FF, 0xF7419BFF, 0x9E0C5BFF,
                                0x7C706BFF, 0x39302DFF, 0x943907FF, 0xAA755EFF, 0x794B37FF, 0x3A1706FF, 0xAA512AFF, 0x602400FF,
                                0xE09A8BFF, 0x884F44FF, 0xDC6D00FF, 0xFDAA7CFF, 0xE06719FF, 0x7C3603FF, 0xFF811DFF, 0xA15013FF,
                                0xECA992FF, 0x945D4AFF, 0xFF6B03FF, 0xFFBE9EFF, 0xEE763DFF, 0x913C07FF, 0xFF9664FF, 0xC15000FF,
                                0xC09C84FF, 0x6F523EFF, 0xFF3107FF, 0xFF9779FF, 0xCD633FFF, 0x7E2400FF, 0xFF6B4EFF, 0xB52800FF,
                                0xE9BB63FF, 0x916A13FF, 0xFF6B8EFF, 0xFFC8A5FF, 0xD78C59FF, 0x894A1BFF, 0xFE9C96FF, 0xC74F4EFF,
                                0xCAC0B2FF, 0x797165FF, 0xFF6F84FF, 0xFFC4C4FF, 0xDA878AFF, 0x8B464AFF, 0xFF9BA3FF, 0xCC475EFF,
                                0xEEBC98FF, 0x956C4DFF, 0xFF7C64FF, 0xFFCEBDFF, 0xDB9071FF, 0x8D4E33FF, 0xFFA68FFF, 0xC35E47FF,
                                0xE6CAB3FF, 0x917965FF, 0xF88D7EFF, 0xFEDBCCFF, 0xD79F8CFF, 0x8A5B4BFF, 0xFEB5A5FF, 0xBC6E60FF,
                                0xE7D7A1FF, 0x928355FF, 0xE79BBAFF, 0xFEE5DAFF, 0xD3AA99FF, 0x876556FF, 0xFCBBC3FF, 0xB27A81FF,
                                0xBCD68FFF, 0x6C8145FF, 0xF767FDFF, 0xE5DBCCFF, 0xAEA698FF, 0x696156FF, 0xEDA7E5FF, 0xA5689EFF,
                                0x6A945EFF, 0x294A1FFF, 0x9D00E1FF, 0xAC80D6FF, 0x7B53A0FF, 0x3E1B5BFF, 0xB335FFFF, 0x690B99FF,
                                0x7AAA6AFF, 0x365E28FF, 0xB725FFFF, 0xD08BFFFF, 0x995ACFFF, 0x561A81FF, 0xC261FFFF, 0x830CBDFF,
                                0x507A5EFF, 0x133722FF, 0x6410D3FF, 0x6E78BCFF, 0x464C88FF, 0x181747FF, 0x6D45DEFF, 0x3D008CFF,
                                0x739B83FF, 0x2F513EFF, 0x7B44FFFF, 0x849EE5FF, 0x586EADFF, 0x233266FF, 0x866EFFFF, 0x5203DCFF,
                                0x7FB98AFF, 0x386A43FF, 0xA162FFFF, 0xA5B9EAFF, 0x7688B5FF, 0x3A486DFF, 0xAB8BFFFF, 0x6D40D3FF,
                                0x90CCAFFF, 0x457961FF, 0x958AFDFF, 0xB6D2FDFF, 0x7B9AD8FF, 0x3D578BFF, 0xA6ACFFFF, 0x6264DDFF,
                                0x508B6BFF, 0x0C442CFF, 0x6A10FDFF, 0x6191B8FF, 0x386487FF, 0x002B46FF, 0x635EEBFF, 0x3622A1FF,
                                0x659FADFF, 0x21535FFF, 0x0080BAFF, 0x4FB3DBFF, 0x1B81A5FF, 0x103E53FF, 0x139AD5FF, 0x065A7DFF,
                                0x94BAD4FF, 0x4B6B81FF, 0x00AAC5FF, 0x5EDBFFFF, 0x25A5C9FF, 0x055D70FF, 0x26C3E6FF, 0x057D95FF,
                                0x9DC1EFFF, 0x527198FF, 0x1CB6B9FF, 0x6EE6FFFF, 0x10B3CFFF, 0x026674FF, 0x1FD1E0FF, 0x00898FFF,
                                0x707DD4FF, 0x32387EFF, 0x197A62FF, 0x6898D8FF, 0x3E6AA3FF, 0x072E5DFF, 0x278DA1FF, 0x114F5AFF,
                                0x869BEFFF, 0x425196FF, 0x179982FF, 0x5CC1E9FF, 0x2B8FB4FF, 0x114A5FFF, 0x00B3B0FF, 0x116D6BFF,
                                0xB898E7FF, 0x694D8FFF, 0x00AA3BFF, 0x92C7C5FF, 0x629492FF, 0x275250FF, 0x14C560FF, 0x127A3BFF,
                                0xBD87D6FF, 0x6C3F80FF, 0x4F961CFF, 0x9CBA97FF, 0x6D8868FF, 0x32482EFF, 0x55B31FFF, 0x386C13FF,
                                0xD3B0E3FF, 0x7F628CFF, 0x5FBA18FF, 0xA6E790FF, 0x75B060FF, 0x376923FF, 0x6DD819FF, 0x468C0DFF,
                                0x98738BFF, 0x4D3144FF, 0x65690FFF, 0x9D8E71FF, 0x6E6147FF, 0x342915FF, 0x827C31FF, 0x4A4406FF,
                                0xD47AA4FF, 0x7D3458FF, 0x8E7C13FF, 0xDC9B71FF, 0xA56B45FF, 0x5F300CFF, 0xB88C14FF, 0x6E530AFF,
                                0xDBA4AAFF, 0x86595EFF, 0xC98800FF, 0xF8B690FF, 0xC08562FF, 0x764527FF, 0xF79617FF, 0x9D600EFF,
                                0xEAB0CAFF, 0x926278FF, 0xBAA300FF, 0xFDD07DFF, 0xC7993CFF, 0x7B5800FF, 0xDCBA1CFF, 0x917911FF,
                        };
//        palette = new int[64];
//        float[] outer = {
//            0.96f,//NamedColor.AURORA_CARMINE.hue(),
//            0.015f,//NamedColor.AURORA_LIGHT_SKIN_6.hue(),
//            0.013f,//NamedColor.ORANGE.hue(),
//            NamedColor.CW_LIGHT_YELLOW.hue(),
//            NamedColor.CW_GREEN.hue(),
//            NamedColor.CW_LIGHT_CYAN.hue(),
//            NamedColor.CW_SAPPHIRE.hue(),
//            NamedColor.CW_PURPLE.hue()
//        };
//
//        for (int i = 0; i < 7; i++) {
//            palette[i+1] = getInt(floatGetHSV(0.078f, 0.05f, (i+0.5f)/7f, 1f));
//        }
//        for (int i = 0; i < 8; i++) {
//            float sm = 1.0625f, vm = (9.5f + ((i & 3))) * 0.1f;
//            if(i <= 1 || i == 4) sm -= 0.09375f;
//            if(i == 7 || i == 5) sm -= 0.125f;
//            if(i == 6) vm = 0.9875f;
//            if(i == 7) vm = 0.9375f;
//            palette[i+8]    = getInt(floatGetHSV(outer[i], sm * 0.375f, vm * 0.25f, 1f));
//            palette[i+8+8]  = getInt(floatGetHSV(outer[i], sm * 0.3f, vm * 0.4f, 1f));
//            palette[i+8+16] = getInt(floatGetHSV(outer[i], sm * 0.45f, vm * 0.55f, 1f));
//            palette[i+8+24] = getInt(floatGetHSV(outer[i], sm * 0.55f, vm * 0.75f, 1f));
//            palette[i+8+32] = getInt(floatGetHSV(outer[i], sm * 0.4f, vm * 0.9f, 1f));
//            palette[i+8+40] = getInt(floatGetHSV(lerpHue(outer[i], outer[i+1 & 7], 0.4f), sm * 0.5f, vm * 0.475f, 1f));
//            palette[i+8+48] = getInt(floatGetHSV(lerpHue(outer[i], outer[i-1 & 7], 0.4f), sm * 0.35f, vm * 0.65f, 1f));
//        }
        
//        palette = new int[217];
//        for (int r = 0, i = 1; r < 6; r++) {
//            for (int g = 0; g < 6; g++) {
//                for (int b = 0; b < 6; b++) {
//                    palette[i++] = (r * 0x330000 | g * 0x3300 | b * 0x33) << 8 | 0xFF;
//                }
//            }
//        }
//        palette = new int[] {0x080000FF,0x201A0BFF,0x432817FF,0x492910FF,
//                0x234309FF,0x5D4F1EFF,0x9C6B20FF,0xA9220FFF,
//                0x2B347CFF,0x2B7409FF,0xD0CA40FF,0xE8A077FF,
//                0x6A94ABFF,0xD5C4B3FF,0xFCE76EFF,0xFCFAE2FF };

//        palette = Coloring.MANOSSUS256;
//        palette = new int[256];
//        System.arraycopy(Coloring.MANOS64, 0, palette, 0, 64);
//        PaletteReducer reducer = new PaletteReducer(Coloring.MANOS64);
//        long state = 98765432123456789L;
//        IntArray colors = new IntArray(256);
//        colors.addAll(Coloring.MANOS64);
//        int[] items = colors.items;
//
//        for (int i = 1; i < 14; i++) {
//            int color = i * 0x12121200 + 0x111111FF;
//            int found = reducer.reduceSingle(color);
//            if (CIELABConverter.differenceLAB(color, found, 1.0, 1.5, 1.5) > 300)
//            {
//                colors.add(color);
//                reducer.exact(items, colors.size);
//            }
//        }
//        for (int i = 1; i < 5000; i++) {
//            int r = (int)(i * 0xD1B54A32D192ED03L >>> 56), g = (int)(i * 0xABC98388FB8FAC03L >>> 56), b = (int)(i * 0x8CB92BA72F3D8DD7L >>> 56),
//                    color = r << 24 | g << 16 | b << 8 | 0xFF;
//                            int found = reducer.reduceSingle(color);
//                            if (CIELABConverter.differenceLAB(color, found, 1.0, 1.5, 1.5) > 300)
//                            {
//                                colors.add(color);
//                                reducer.exact(items, colors.size);
//                            }
//                            if(colors.size >= 256)
//                                break;
//        }
//        if(colors.size < 256)
//            System.out.println("UH-OH, colors.size is " + colors.size);
//
//        System.arraycopy(items, 64, palette, 64, 192);
//        
        ArrayList<Integer> mixingPalette = new ArrayList<>(256);
        for (int i = 0; i < palette.length; i++) {
            mixingPalette.add(palette[i]);
        }
//
//        mixingPalette.subList(64, 256).sort(hueComparator);
//        ArrayList<Integer> mixingPalette = new ArrayList<>(256);
//        for (int i = 0; i < 256; i++) {
//            mixingPalette.add(i * 0x01010100 | 0xFF);
//        }

//        float hueAngle = 0.1f, sat;
//        //0.7548776662466927, 0.5698402909980532,   0.6180339887498949
//        for (int i = 0; i < 6; i++) {
//            sat = sin_((i / 5f) * 0.5f) * 0.05f * 2f;
////            sat = TrigTools.sin_((i / 5.0) * 0.5) * 12.0;
////            palette[1 + i] = CIELABConverter.rgba8888((i / 5.0) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
////            palette[1 + i] = Color.rgba8888(NamedColor.ycwcm((i / 5f), TrigTools.zigzag(hueAngle) * sat, TrigTools.zigzag(0.5f + hueAngle) * sat, 1f));
//            palette[1 + i] = Color.rgba8888(NamedColor.ycwcm((i / 5f), cosMaybe(hueAngle) * sat, sinMaybe(hueAngle) * sat, 1f));
//            hueAngle += 0.6180339887498949;
//        }
//        for (int i = 1; i < 7; i++) {
//            sat = sin_(((i + 3f) / 13f) * 0.5f) * 0.125f * 2f;
////            sat = TrigTools.sin_(((i + 2.0) / 11.0) * 0.5) * 28.0;
////            palette[6 + i] = CIELABConverter.rgba8888((i / 7.0) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
////            palette[6 + i] = Color.rgba8888(NamedColor.ycwcm(((i+2f) / 11f), TrigTools.zigzag(hueAngle) * sat, TrigTools.zigzag(0.5f + hueAngle) * sat, 1f));
//            palette[6 + i] = Color.rgba8888(NamedColor.ycwcm(((i+2f) / 11f), cosMaybe(hueAngle) * sat, sinMaybe(hueAngle) * sat, 1f));
//            hueAngle += 0.6180339887498949;
//        }
//        for (int i = 1; i < 20; i++) {
//            sat = sin_(((i + 6f) / 32f) * 0.5f) * 0.2f * 2f;
////            sat = TrigTools.sin_(((i + 5) / 30.0) * 0.5) * (44.0 + 10.0 * TrigTools.cos(i * Math.E));
////            palette[12 + i] = CIELABConverter.rgba8888(Math.pow((i+5) / 30.0, 0.75) * 100.0, TrigTools.cos_(hueAngle) * sat, TrigTools.sin_(hueAngle) * sat);
////            palette[12 + i] = Color.rgba8888(NamedColor.ycwcm((float)Math.pow((i+4f) / 28.0f, 0.75), TrigTools.zigzag(hueAngle) * sat, TrigTools.zigzag(0.5f + hueAngle) * sat, 1f));
//            palette[12 + i] = Color.rgba8888(NamedColor.ycwcm((float)Math.pow((i+4f) / 28.0f, 0.625), cosMaybe(hueAngle) * sat, sinMaybe(hueAngle) * sat, 1f));
//            hueAngle += 0.6180339887498949;
//        }
        StringBuilder sb = new StringBuilder(mixingPalette.size() * 7);
        for (int i = 1; i < mixingPalette.size(); i++) {
            sb.append(String.format("%06x\n", mixingPalette.get(i) >>> 8));
        }
        Gdx.files.local("palettes/hex/"+HexGenerator.NAME+".hex").writeString(sb.toString(), false);
        System.out.println("new int[] {");
        for (int i = 0; i < mixingPalette.size(); i++) {
            System.out.print("0x" + StringKit.hex(mixingPalette.get(i)) + ", ");
            if((i & 7) == 7)
                System.out.println();
        }
        System.out.println("};");
        Gdx.app.exit();
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private static float cosMaybe(float angle){
        return cos_(angle * 0.25f) * 0.375f + TrigTools.cosq(angle - 0.5f) * 0.625f;
    }
    //3.141592653589793f
    private static float sinMaybe(float angle){
        return sin_(angle * 0.25f) * 0.375f + TrigTools.sinq(angle - 0.5f) * 0.625f;
    }

    private static int getInt(float color){
        final int c = FloatColorTools.floatToInt(color);
        return c | (c >>> 7 & 1);
    }
    public static float lerpHue (float from, float to, float progress) {
        to += 1.5f - from;
        to -= 0.5f + (int)to;
        from += to * progress + 1f;
        return from - (int)from;
        /*
        fract((fract(to + 0.5 - from) - 0.5) * progress + from)
         */
    }

}
