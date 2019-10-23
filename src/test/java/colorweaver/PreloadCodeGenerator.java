package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.TimeUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
//    private List<byte[]> bytes = new ArrayList<>();
//    private int counter = 0;
//    private int a = 3127, b = 31111;
//    private ImmediateModeRenderer20 render;
//    private Viewport view;
//    public void create() {
//        bytes.add(BlueNoise.RAW_NOISE);
//        render = new ImmediateModeRenderer20(320 * 320, false, true, 0);
//        view = new ScreenViewport();
//    }
//    @Override
//    public void resize(int width, int height) {
//        super.resize(width, height);
//        view.update(width, height, true);
//        view.apply(true);
//    }
//
//    @Override
//    public void render() {
//        int i = 0;
//        byte[] bt;
//        render.begin(view.getCamera().combined, GL20.GL_POINTS);
//        for (int bx = 0; bx < 5; bx++) {
//            for (int by = 0; by < 5; by++) {
//                bt = bytes.get(i++ % bytes.size());
//                for (int n = 0; n < 4096; n++) {
//                    render.color(Float.intBitsToFloat((bt[n] + 128) * 0x010101 | 0xFE000000));
//                    render.vertex(bx << 6 | n >>> 6, by << 6 | (n & 63), 0);
//                }
//            }
//        }
//        render.end();
//        if(counter++ <= 0)
//            return;
//        a = (a << 13 | a >>> 19) * 0x89A7;
//        b = (b << 17 | b >>> 15) * 0xBCFD;
//        bt = BlueNoise.generateMetropolis(a, b);
//        bytes.add(bt);
//        generatePreloadCode(bt, "blue_" + StringKit.hex(a) + "_" + StringKit.hex(b) + ".txt");
//    }
    
    public void create() {
        Pixmap pix;// = new Pixmap(Gdx.files.internal("BlueNoise64x64.png"));
//        System.out.println("Original image has format " + pix.getFormat());
        for (int idx = 0; idx < 16; idx++) {
            pix = new Pixmap(Gdx.files.internal("LDR_LLL1_" + idx + ".png"));
            ByteBuffer l3a1 = pix.getPixels();
            final int len = pix.getWidth() * pix.getHeight();
            System.out.println("Original image has format " + pix.getFormat() + " and contains " + len + " pixels.");
            byte[] brights =  new byte[len];
            for (int i = 0; i < len; i++) {
                brights[i] = l3a1.get(i);
                brights[i] += -128;
            }
            //System.out.println(brights[0]);
            generatePreloadCode(brights, "BlueNoise16.txt");
        }
        //check(pix);
        System.out.println("Succeeded!");
    }
    
    public void check(Pixmap pix){
        byte[] blueNoise = ("ÁwK1¶\025à\007ú¾íNY\030çzÎúdÓi ­rì¨ýÝI£g;~O\023×\006vE1`»Ü\004)±7\fº%LÓD\0377ÜE*\fÿí\177£RÏA2\r(Å\0026\023¯?*Â;ÌE!Â\022,è\006ºá6h\"ó¢Én\"<sZÅAt×\022\002x,aèkZõ"+
                "l±×\033dÅ&k°Ö÷nCÚ]%é\177ø\022S\001Øl´uÉ\036þ«À>Zß\000O®ñ\021Õæe÷¨ê^Â±\030þ®\021¹?èUªE6è\023_|¼¢!­t½P\005ÙG¥¸u.\030ò>Tÿ3nXCvíp*³\033ìÑyC¼/\031P1;òSÝÈ2KÒ\""+
                "È3r Óø·V\000\034ä4\bVê\020õgÇ\0331êÞ`¯ÅeãÓ­ò×\rÈ\034KÏ\013h5\tÃ\037T\002~Í´ kÐq@~ïc\003x\023ó»\005OxÛÃJÎeIÒ7´p]\013#J\006 $`F¿¡*³`åôS½F¤bùÝl¦Há\rû¡æ\013%º\005\035à©"+
                "G[âc\020§=,mñµ=þÃ-\034å\ròM¿?Ïöq9¹\017xæ\032eù2¦\026:~Ùå-:¶ð'Ww¿KcªÕ\\¢OÀ-Ð³:¥+Éî!\\Ñ\f$qß}¦WB*«Õýz¨\025ìPÌ\0027|ÞRq\001Ä¬%ÿr¯\030Ò\016_Ç3Ö=\0260úè8\roø"+
                "a\007Ù}ýAs¼áû¬Tè\024²_\007øÊxe\036µ1VØ(ª@ÚUÊ\007»Óaî\021WÆM{B\033s\005®óÉyiÍ¯\032ê%M\030±Nh\0267{Â¢K9Ö¹\026à:\tjæ¿~]÷h.µ\024J\"óC-\032KkÏ=ò\003é«Ûö»b\"ßU\b·B#ÞT"+
                "pÀhèÔ2\tÊFÙ\003+Íñ lGa\000ÁQìË¢\033D\004\035Ãð¤pé®\\ Ýµ2º¡b)¿6kNëFl§\035Mÿ|1È?úª\017GZ÷£ì¶\037p[\017ä1¤&s-`û7±Òt\rYÑ9z\016Éêvü\tã\034pÖJ\007£*\017Å6×íÂ\023óµ"+
                "\026.]Ì$q¹\034x-bVãø¼«wÃî³\020ÙH¸vÞP\022é3MÞ>\000Á*úeA\"ZD®û\037ÉYÔ\177µ\002t.f«\\JÖuÝ\003¡òß>Ô\f¨\0223B\002RÐ?[÷©\013#Æo[ü¹\"¬d\030á¸Q\0344ÂªÕ}Ç\017ç`xñ2¬ü`è\026XÑ9å\t"+
                "ïR´e4U­\003l¿<NÑhÝ#ù}\030Æm;ÐWô«)¢Í}ñoG¦Ó\003hð'V<µà\024D!Ë=÷º\037jÃ9\036AÁw\020ÈLúé\177\036´_\r¨ÜO,æ|\016?ÛjE\0076×S\nôxâT\022I6»\003Ò\031Oq¿Ûn Mà\020zFþ)§"+
                "~â\013ùÖ*ë Ü7(Æ¡õ.ê¾i7·\004ë\036fF»\000Àï\027^µ&Ê1?¾,´ùÜn¦v+Å¢\0008õ±(Ã^¢Ø³VÎ¹Ni¨]z¶d\030F\005WuËL)åt¬ÂüØ4b\035T/¯æÄÿvê®b\036\brÍ\033éFöa\016ée\031W\nv\0020eô\024"+
                "\001-Ë\031G»õ¢\nÐ«r×:\025\000Ñ\\B\fU\024±Ñ ygM\033\023Øí[©:dP\n±>Õ'¸Ðå;ªïÊ\034çpCÚaït2ÿn>RäZð%¸â£°y\034ò¢1Îz&îqãGû\017Ø,§BYý\177LBà\000½$Íjá»TªsI/f\026R@½4"+
                "Å£\020²ãØÆ\027-ýÁ5\fHh÷V?ÄláH§[8\n·È;óÏlãÂ5Ï¹&\024{ò0\025}\005ÇòþÀØw\016\\­Py\036=X\t$¯Ak^Æ#\016¼Û(\022ù¹\005Á÷f!Uqº\t1³ ¡\006pöÇXÚ=] ù\"8Þb\035|L&µâúÒ$\006"+
                "öÒ¿J}9dívÛ\022°ç\000ÔrìcI­X;n\032ÚO.¬ß¤\031_÷R^Ü/²E\013s­\003ÆêL\020C¯ê\tY£-gßl/`ýç§ÌW\004¹IÍ\037}Q/<¥\004~Õë$wÌ\023ë|\004JíyØA\025ë¨gSé\032&m³Òv½Ö3Ípð9ÄA·"+
                "ì«'\024ö\032(¦ù2¾\032ß_¶Ì\0310Æ^³\000>ZÑ)Á8Ë&­iÇ:\036Ìü5¾ÔAW/¤[\002m\025¨@\003y\031M\017UÉFsÕf3ÁàQp[ïC«\007õPåüi\rIåg¼õC°ýNå\013ÿu¶\râw£cPàö\t\031äò%O_¼!Ú¯èÏ"+
                "\177\034\007¹L°x;\bÅÚ\017iÏx$s8»B¢ò,\027¨4\034k`\022t£\\¾.ÖóN?)´\016{Ài>±Åúæ\016Hkaþ4Ýøá;ï\t\"Îèb®%7KÂ\021ÓY¬\037Ý|ÁPÕs\fãÙ¹ ñGV#Â]\001ð 4¬FÌ\177Ü/uÓô(<½¤"+
                "%²_-Ziý\027G{¹æý+°î\006nÎ\004büÈM+ó?Ð4Ý{\027¥l®Þ\024ÏmIÇÚ]þ\035\bg;¥R¶ÇX\023gÅApÌ\023¼ÚA¥·1ò\003V\035`ÜoDeâN÷1W±à&E\177¬\007oX\003²çÍû2~;§çr\023é+Qºï\"ü\026|\006ãNð"+
                "\r¡Oÿ|)ÈTuÜÒÇ§\0265'Å\017\033<£\022·í\\Á\030§Æi=c\bDí!W»*\004=¶¡Òc¨\021ÊCÝ2ªÓvýÙë\035­äù\036Kk\021<tPôÎ½\001®y@¹évôk2Ò\0369âJú-\021½&M·Õù\fgRöyHâvW²j\\ëG\036¸/"+
                "©Tk1Hc9ê\006Á¬'ì¶/\f\177\\Úñ¤gÓÆHþfÔzëUóÚ\034È_r Ë­×\0360Ä\005>(ô\004¡Àqb<\024½Ò\boµ\025Ò\\ãbþAä L9\024Qÿ,[\bÝr\017´V$¯E Îw©k\020æ-M7\030ãnXì\027Ö½5\032Î+\017ôßË"+
                "']@óÄ%¨}0DÌ\032m×¬f\bÌo$ß¯\035½«N5Çö\fÞ`3\0048Hþ°ÀyðCÿ¸jª[oOzÛ=S\006}çù°x6ßY\001@ºõ\nJ¼)XÄí´úÀCc7æzñ'çt@Àm\026¶\"áÃ¢#Ú\006(Å\0166IÎü\"\016è§ûi±Â "+
                "\020Ú ¤MíhÜ\037uïÞ\0027\031w0^\rÊ\005T\023Ñ^¦.üïQù]}îVeµ\\¥~Ý+ä²Ç@_¸Jí\"6FnQcÀ\fs\031Ê,¡T±3[¦z\020MÔS¬ê~öiÖ?ÃlE\005\034ÖYÊuB¬Ô+\017<Í\026õKÑ\037øTwh\006(\024ÊuÓ"+
                "µ*þÔ²Iq\004ÕÅ\025?Íõ£#à\0265¸'¨\033ú°ßº~K®9'£\t\032¸kã¨v2æ=d¼\007:\032ñ1Ód©Ý\032\0024ÉâDïf8û¾\022ê<gû%¶ç_¿r\001FÏK\n]5$òä\020j½êÛb5È\003R¼\np¯ë\024Ë¦ÙQ¾\177ãøVNò"+
                "¥_\023~«&á^O©áSl,\f=´f YÜîoÉçwV\reÈ\002OzðF\"ùBÛ\034ÄWHmaÿ®F :\b-¿x >º\007Y\027¡xÏ.\035~ÁD\002ª×íÅûv¼,E\003À£<*X\033qÏ,²\026^rÐ+b¢\001$Ø/ûå(\016!oÆ²jè"+
                "ÜøÏ.GÙÆö?\b·ó\rÑu\0338øR|1\017®9\025¦Ù\037MÒý¬Ùõ¶A\022üT×ç¬\r³|ð;xªÃCsÐì\027×D\f®Shêu±hÞI^5ëYÈi\025§ÕåcÑSeµ3ðmEz3â§m¿(9ÄQäHÎ^·\013\033Yº0MZþ%aÄ"+
                "s\033\000¥Q7\032T)Én­ú#·¢á»%@L\006$êù\017t\031â\013Â `LÉ;\nyîd\002\030.¿\020÷Lï7Üùfâ|ï6\021ã´È(\013¼å\000î\025ÙfA\021/K\001Zôm¶Ä\177>Ê¨Y¸d#ñ\005\037ë[øI\034¤×g«s ÙjÊ{\025©\b@Î¸¦L"+
                "+B_ñcÐ©{:»,RÄ\004wÐîo¯Ë\035Û¤G0^Þ)\0018Îp·~.Û²Ì2ºtú@æ£=+\004³I'Às4\035\002Òõ|Ø2r!H\\\007rçª\\7\ny.ÿ\025ä¯\t¾PéI­0TÕ¤\024f\004àY;ÇS\007(ZýÀRâaÔé¡÷oX"+
                "h\006»¢\020NßøÃ\021Óã¢\034Jõ'¾\033ÝÀëU:qÑg ÷{Õi\021üà\030CôÂO!n\016$®îÎ´z\022§p ôRe\fDÞÂ«8\"WÍ²> j4°ý=Í\177\rØlRúG\026a¸NïD\030¥Ä\\s\n:©ëEØ\177\0274fÞEìÌ9º\021,È³%ë"+
                "\023Häuÿ\032í&Yh·5>³gªÓø\016Æ)¦6Í¶1ò@y»7ËaäþÓx·öa¾Jö\0350]\013C}«Û8wOýÇmó.¼]}ØGÂð-\000`¤îÎ\0060æ>#|ß[ÿu\013WmÛ&\004ì\035²X)\027É0O\001åp\tÄÔ°lÞûX\004\031òÑ`"+
                "Ü\027­aB\nË§\001\035\020«ßÈo\022%{¾m J°\031¾ã!\000°ÐU¤h0H¿d\t¤Ñ)­9wS\001KÄ&èg¾¥3\177¸Q(ÖåS6¶kæuVC\036JàU\032Ê\005ì<×cFî Kd\024>Üó}\021áAí8v³Z@\024dè$½\027/u²Ï=o\013!"+
                "@\002¡Ãyú\024NÏ )ø¿[­þBñÚ^2Ãj-\025³~ÇætÁ\013Ð¥\003¯nÝ\037ðÊûÙ¡óc¥î\017P\036ù×Vèdð0j\016\"FfÛ,õ<\005²×y\013.Ôq´*¦v\017õN©ûÔ'\n6øª*E\034lQ#ÆU\022¼fR¸3NË?Ø6`¹G­É"+
                "\025Òá±ïº©wÅ\\\027ëH£ê5b\023Oü¶!Ìm=^ºPÖ`þ´5õÕ,üI\b}'j\003\023z¯\bl\000Þ{+\005K·Z<qË4]\036\fáo½4dËj\033»\006Èå:ÕUáz\b¾àñq\030yÞÃèbCz ç«;ÁäªÕ äEÉ(§ñÂ8k"+
                "$ôþ\027QÔûB#ý©:öP}#nÁfB3ìW\037J¥/\005É9ïY'\n¸\031_Î!\026G÷8^¾T÷t\021Zä£\n|Å+¨ç\007|¡V°ðLÝ\021'á°AÜô®\f\033¥\001±\022Êÿ´ÚgS!¬\023Ih©Û\0013MtôÙ[rÇén¤0\031µLÍ?\035rÖ"+
                "W@ÚdJ*Æ6kÐ~UÁs]\rÍ+ZEé+bÙq¡e4xé¼Ô\002 Ì:nîÄ²\013.\007µ)\006Øeé\003³ü½3ì°\020tÀó³\021å\001\032¹/Ô\000\037í¹tÿÐ{KÆö%?æÃ&D\016r>{ø\036X&âj½<¦Q\027Dñ\177Ã:Û-cH"+
                "j\037Sú#ÖD[a<\bóD¤m8\030¨5»\026·Ð\tQ\031]ü¦ñaä/¿PÓ\177B\030LûÑ{ßÌ¯Z\020#pS©î\027Ï§âË_;oª!wÊß©i´*L¿àU\007ðPà\n_2X{ô±ÖË*µU\025®ç\016÷¥Écì\037Xýi5ã¦öº\tÇ{\005"
        ).getBytes(StandardCharsets.ISO_8859_1);
        byte[] brighterNegative = ("A÷Ë±6`z>mÎ\033ÙgúNzä\003Sé -òl(}]É#ç»þÏ\rWöÅ±à;\\©1·\003:¥\033Ì\fSÄ\004·\\Åª\030\177mÿ#ÒOÁ²¨\013E¶\033/¿ªB\r»L\000Å¡B¬h:a¶è¢s\"I\002î¢¼\006óÚ\037EÁôWø¬áhë\025Úu"+
                "ì1WäE\017¦ë\0300VwîÃZ\006Ý¥i\024ÿxÓXì\0254õ\021I\030\007~+@¾Ú_\027Ï.qUfåw(jÞB1\037~.9\n¿hÕ\003*Å¶h\001ßü<\"¡-ô=ÐYÇ%8õ\035®r¾Ô\177³îØÃöm\032ðª3lQù\034Ã<¯\023Ð±\017»rÓ]H²ËR¢"+
                "\034H³ò Sx7Öd´Öju\036çG±j^à/EåcS\004-rWHËO\rèµCÔ\nþM4 ëP\005ñÀ\022þoãøs;Ï\032ø[C\037Ê\rNå\020ÉR·4ð\022Ý\007£Ê\f ¤àÆ?!ª3àe\023tÓ=Æ$ây]ì&Èa{!f¥:\004`)"+
                "Ç\rÛb\020ã'½¬íq5½~C­\004erÍ?¿Ovñ¹9øfåy\000²&ºþY\004e­\021º6p§\000×÷?Ëã*UÜ\"Ï@­P3º%«In¡\tÜQ¤ñ\031_ý&×Âª+U}ú\025(lÐL\025\f·ü^ÒñD,¥\177ò/RßG\034³V½\030°zh¸ïx"+
                "áYý}Áó<a{,\005Ôh2ßxJø\035å5±ÖX\005¨*ÀZÕJ\034;S\tán\033×FÍû\026Âó.\bsIù\016éM/\027j¥Í\0021\030Îè·ûB\"Ë¹\024V9\n`º\003êf?þÝwè®5Ê¢sÃ­ËëO½r\006i+[v;â¢_Õ7Â£^Ô\n"+
                "ð\034@èhT²J\021ÆY«Mq ìÇáAÑlK\"Ä\035Cp$ði\023.Ü ]5\n²:!â©?¶ëÎ\022kÆì'Í\001\177ü±H¿z*Ç\fÚw#l6\027ðÛ\000d±$¦ó\017­à{\023·1R\017ô\003ÙQ¹úIjö|cðVÊ\n#ªE¶\033WmB\036s5"+
                "®ÝL¤ñ9ø­âÖcx<+÷C\020n3YÈ8ö^Ði³Í^¾Aª\007zå\026Á¢Ú\017Ä.{\035IÙTÿ\r5ô®æ+ÜÊVõ\026]!r_¾\002T(\t³ÂÒP¿Û\031w)£\013FïÛ|9¢,äa8Ñ´B*UýGgàøq²,|àhØ\027Q¹e\006"+
                "oÒ4å´\021Õ-ì?¼ÎQè]\034£yýFí»P×t+©\006\"Mý\fq\035ïÇ&S\003èp\033§Ö\001¼5`\004Ä¡K½\007w:\021êC¹\013ÁA÷HÌ\032ziÿ4\fß(\006\\Ï¬\001\037fü¿[êÅ¶WÓ\020tøbÔÉ¶;R\021Ïñ?[î Í`úÆ~©'"+
                "þbyVªk \\·¨\017F!u®j>é·7kæÆ;\027@o\025Þ5¦J±¿>\036¬4y\\î&ö«E\"¸\027u1¨CÞ\"X3ÖN\0359Îé(\025Ý\007ú6äÆ×õKÌ©e\026ô,B|X´âÔ¯/fD\177öj.âòM\t\023iÆváiå×ö\021°å\003t"+
                "­\006KÇ;u\"P+òW\024º\tQÜÂÕ\0201\bQ ù\000çÍ\033\004X\fmÛ)ºäÐ1\025¾U§\0168Pe»*oJgðÃZáoô²\177î¾ÒdÚ\002p¥8b#0ùr\"±Nú¦nñcÇ{X¬'ÂÙ}ÿÌ\030Â`=¤Mêa;Ô*óÉ¯\000æÒÀ\031=´\020"+
                "E#2c\034XF­}AµÈèwÖ¿\022Dì\007aÈ'Û¸7H»\020sOìcBµO9¦û\037r\007°ý\tGr\037~@X\f÷Ü-Ðù½Ø\017¤/\n\026Áë\037Þ\016F£<[¨y9\023Aw\ræ¡Õñ:±3 !ðv\001GØZ½Ý y¢¸^âüÌ¦5bzR¤"+
                "v\000R?Êý¹ämö[0gTò\001lãÉ-Ø»îZÏ®,\031_$ß\006wÒ\020Þ\\¯2Åó-FjÌ\026Ã/j\026Ù#­ç\034_ì¯à}g'L×9ÉMýÑ\034¯¼%þ\032Uk¤\036÷LküÊm\026ùXÁk(çÓ\027i\022¦í3Rö=\006V³Mðp¹D\nÁ7"+
                "l+\027§\bv\033¨&y²\006>_ß6L°\004FÞ3¾\002ÚQ©A¸K¦-éGº\013L|µ>TÁ\002×¯$Ûí(À\003ùÍÕIÆóUæ³A`Ñð\023ÛoÃ+uÐ\fe|éÉ\017eç<uÃ0\f}Îe\031\177õ6b÷#ãÐ`vdr¥Ï\017ß<¡Z/hO\022"+
                "ÿ9\037Ì0ø\001»EZéOø\025¤ó¸;Â\"r¬(´\027ëàô#Ü>®VsÎ¿\005©4û\035@é\t¾1EzfÈë\035á~´]\004xa»o¢Nhâ.¥\036·\nËBSÙ,]üAÐUóc\036Y9\001 qÇ\007Ö£\024BÝp\013 ´,ÆLÿ\\\033¯õS\023t¨¼=$"+
                "¥2ß­\fÚ\025é}\016Çû9f}«0n\037\020îN\bâ\023|HÍ«s¿P´]û%ì.^OíÉGZÝ~\022ç»%Ò6GØ\tçEÁðL<ZÁ%7±rÖà\\ïÄåbÎw±×1`¦Å\005ÿ,ïØ\0272gM{²þ\032»'g\024òi«Ñ:o\013¢|ücÎp"+
                "\021!Ï\177ü©HÔõ\\\032R\003G'\022µ\001§E\030¼#7mÜA\023'Fé½\017ãÄm¡×;ª½6!Rã(JÃ]\000²*S\031ö}Yk-d\035y\005Ëë¼ôÐtN=.ùÀ9iötë²R¹bÊz­=¦Í\0347\000UyçÒ\003vùÈ\bbö×2êÜkÇ8¯"+
                ")Ôë±\007Èã¹jA,§l6¯ÿ\027ÜZq$çS\002FÈ\017\037~æ\tTúk\nÕsZHßò\016 K-W°D¾¨t\024!@ñ\râ¼\002=Rï5RÜ\021câ\037~\bÁd Ì¹\020Ñ\177¬Û]ò4Ö¤/Å N÷)ëf­Í·cîØ\021l\034V=µN«t_K"+
                "§ÝÀ\024sD¥\r(ý°ÄLíW,æ\036Lï¤_/\026=+ÎµGv\031^à³\007¸È~0@ùpÃ\032\1778ê*Ûï\005Ïú[½\033Óýgy0ø¶_ÙÀ:u\026Ê<©ØDm4z\005@Ãã·fúq§\006gôÀ@í6¢aC\024\"£Z\006¨E¶ÉN|¢h'{\fé1B "+
                "\016Z $Í\030mè\\\000õo^\020·÷°Þ\037J\nÔ\034QÞ&®|\002o\025ÑyÝýnÖ\021å5Ü%þ]\001«d2GÀß8Êm¢¶ÆîÑã@óJ¬!Ô1³Û&úÍ\tTÓ,jþvéV¿CìÅ\017VÙJõÂ,T«¼MuËQx\036Ô÷\016è\032¨Jõ\bS"+
                "5\032ª~\003T2É\tñUE\031¿Mu#£`\024µ8§(z0_:þË.¹§#\n8ëc(ö²f\r½ä<ºq±S\006ä)]´IbÄoæ¸{>j¼ç{¥6gß?òÆO\bË\031Ý\000µ¤\030rdê=j[âµH\001Ò<\026ð/kK&YÑ>ÿcxÖ\024Îr"+
                "%ßþ+\023¦aÞ\024Ï)\naÓ\016ì¬½4æ Ù\\nïIg÷ÖåH\b\034Ï\rúpÆ\036¢yÂ[D×\tÈí\023á\177.Æ º­?ø ¾\013:Ù!øO®þAÄ*W\032\001mE\036{ö<¬Å\024@#¼ªØñO¬\0322ÞòP«â\"¤X¯{e¨\002¡ïF2êh\000"+
                "\\xO®ÇYFv¿7s\036Qõ¸xÒü±.¹\020&YÍR},Yu6Á|ÔWg,\0213üp»\033ø*CÃóPl\016W\035Ä.Óè\031jõ\r1è\005^ÉÞµ\004kÙHé'Ueã\004QÓå5³pí\022Åú³\003b'í?¨\006¹DÑdÈ\002NÞ7Ù\037:°ÍÚ~¥áD"+
                "\022ó%Ñ·Ô©Iî-z£7\"\021a;¥À\026Ì¤jyô\004bB àÌI»\027ùnä®?wÌ\bo·\\yæ\027b\006üo¶c4H¨<e\034n\024YæÁ¯Ë\013Útí6Dÿ\034¾J(Ù8ä£qk\013ÛxÉ$\fWç+ó YêJ\030û)ÀN8&Ì"+
                "«\006Âßq\002ãP)ûº;¬ÒD÷Pnï/K[$Ç°\013Þ^©¸\013Nð\0237þ®[2L²:ôz\030Àf#\022½«3É§@ó´R\034uüX²\027ò¡È\013Üò\007g*\032Ü\003·ù®\016\177d/>Ð\030iÉ-°ÔU$æ\032`Ù»GÓ¨Ú}@Òb\016áTi!w\013ïØ"+
                "è;\"Î_xCSc\"Êu§>]\036@kÕºñQç wûUé|`ÃtBÏ¡î\002¤.n\bN4ú'ð t\004ÒåÄ^B+¸¢×M2¾ ê´0}½MÿXìÒzÇá\0308\001Îo\032Ä\005%DÜ\000\035ó\bº)kÅ\036Xÿ´æ^Å\005lL¹:¬H\0263¥k"+
                "È\bdõ\177\tm¦\026Ùè\0167µ\023¾3\rç*SxF©&¶M6±rÀù;·Kád~Sø7vá\022>Ê\037v°Ý\035Ãý+[¸÷Ï\016}Gís\025®<ÝýXÇBp­à$nN°f¾£ü_Û\177õ×í[\020¦l2\020Ø©I°ÏeðD\025T0ì^{\021ØrQà"+
                "\033\\-áÂK'\004+_Hï¥û>\005í Ê0\t>c\r¡0PÕ$è°È?\031ä\007$Q©\000-¹÷Ó\nËD¦hç\001>%³ÿ8Ñ¨V\003eÓ¶6ëfõÖÃ\000Ê`\033ÕJ\020l¼WãÆn Ëä¾\007\\sýaÁm¸ö3ÚÀäh¤=¯õ2O\037½ï¡"+
                "\007À!Cùz\017\031ÎO ©\024x?Û-~ÂqZÞ²Cê\033­3þG\024fôA\032P%/î\016]pJ{Y!\017sã%nÐyWÖhäp°ê¢Ææ[¬u¼2Wù®Tñ\0134ª&öuÎ){T§¶x*ªÅìÑ£FÕ<æ\031\013Ò8³ÎK¿X\005¶à9\013Ç-I"+
                "R\022a1\033o:)÷E\nÜkÈ\f#jµâÏ|\0026¡L\rí½Þ\002:ÐVà~4\001µu\025U¬|Éý§êú/ì\024^û«\031Ë7Ú¼ñK´Ýaï=´äKê;H\023eºUÕaú>\031`qñ\036\016ù^ChâÃú g+»Ad*\003U\027 dÅI¨'qB¸ë"+
                "¤t\000~Ñ\022T{\003Â£\022})\030ºvÐý\037£îAæ\026Â³l×Ê%¯I¹oÙ§\0228\004ßN¡\035Çw¸Þ>Ôw\035ôÚ\021d#üE«(gü!Ö0pÌ]§a0\005Á\\t.%1\005J\1774ZçÓ¡,Éè)[³ÍôtYÛòGiî$°5ÌM¿òV"+
                "×\027ÀZä\fÊªF¶ëP\036þÕAóÝM«ÚÅ\007i«âYñ!å´ø\025i\004<T LºînD2\030®\b5©\rX\001åi\b3|=³l0ô@s3e9¯\002Tm\0209ô\177PûËFv¥¿f\017C¦Äò¾\nûx\tØ¦\016bê=¼&ÑÄqÿCº\030[­ãÈ\f"+
                "êÓz\036£VÄÛ\026\tá¼sÄ\023$í¸(\033µ;\017\0317PÑÝ|&qád¯?ÐS\035ÿÂÌ{Qû_\025L/Ú£ðÓ)nO'b\bKß»\005ï*¡÷J_)é4ªÌ?`ÕpÐ`ß²Øû\007t1V\032Kª5Õ\025.gw%Iã\021lØ}éµc&v:Gû\035"
        ).getBytes(StandardCharsets.ISO_8859_1);
        int i = 0;
        for (int y = 0; y < pix.getHeight(); y++) {
            for (int x = 0; x < pix.getWidth(); x++) {
                if((pix.getPixel(x, y) + 128 & 0xFF) != (blueNoise[i++] & 0xFF))
                    System.out.println(blueNoise[i-1] + " and " + pix.getPixel(x, y) + " are not equal at x=" + x + ",y=" + y);
            }
        }

    }



    /**
     * Given a byte array, this writes a file containing a code snippet that can be pasted into Java code as the preload
     * data used by {@link PaletteReducer#exact(int[], String)}; this is almost never needed by external code. When 
     * using this for preload data, the byte array should be {@link PaletteReducer#paletteMapping}.
     * @param data the bytes to use as preload data, usually {@link PaletteReducer#paletteMapping}
     */
    public static void generatePreloadCode(final byte[] data) {
        generatePreloadCode(data, "bytes_" + TimeUtils.millis() + ".txt");
    }
    /**
     * Given a byte array, this writes a file containing a code snippet that can be pasted into Java code as the preload
     * data used by {@link PaletteReducer#exact(int[], String)}; this is almost never needed by external code. When 
     * using this for preload data, the byte array should be {@link PaletteReducer#paletteMapping}.
     * @param data the bytes to use as preload data, usually {@link PaletteReducer#paletteMapping}
     */
    public static void generatePreloadCode(final byte[] data, String filename){
        StringBuilder sb = new StringBuilder(data.length + 400);
        sb.append("(");
        for (int i = 0; i < data.length;) {
            sb.append('"');
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
                        if(Character.isISOControl(b))
                            sb.append(String.format("\\%03o", b));
                        else
                            sb.append((char) (b&0xFF));
                        break;
                }
            }
            sb.append('"');
            if(i != data.length)
                sb.append('+');
            sb.append('\n');
        }
        sb.append(").getBytes(StandardCharsets.ISO_8859_1),\n");
        Gdx.files.local(filename).writeString(sb.toString(), true, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
    }
}
