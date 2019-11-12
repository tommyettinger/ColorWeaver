package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.TimeUtils;

import java.nio.ByteBuffer;

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
        for (int idx = 0; idx < 64; idx++) {
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
            generatePreloadCode(brights, "BlueNoise64.txt");
        }

        System.out.println("Succeeded!");
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
     * Given a byte array, this appends to a file called {@code filename} containing a code snippet that can be pasted
     * into Java code as the preload data used by {@link PaletteReducer#exact(int[], String)}; this is almost never
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
                        if(Character.isISOControl(b))
                            sb.append(String.format("\\%03o", b));
                        else
                            sb.append((char) (b&0xFF));
                        break;
                }
            }
//            sb.append('"');
//            if(i != data.length)
//                sb.append('+');
//            sb.append('\n');
        }
        sb.append("\".getBytes(StandardCharsets.ISO_8859_1),\n");
        Gdx.files.local(filename).writeString(sb.toString(), true, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
    }
}
