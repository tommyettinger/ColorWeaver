package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;

import java.nio.ByteBuffer;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class BlueNoiseOther extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Blue Noise Builder");
        config.setWindowedMode(320, 320);
        config.setIdleFPS(1);
        config.setResizable(false);
        new Lwjgl3Application(new BlueNoiseOther(), config);
    }

    public void create() {
//        for (int n = 0; n < 64; n++) {
//            Pixmap pix = new Pixmap(Gdx.files.internal("BlueOmniTri_" + n + ".png"));
//            ByteBuffer buf = pix.getPixels();
//            final int len = pix.getWidth() * pix.getHeight();
//            byte[] brights = new byte[len];
//            for (int i = 0; i < len; i++) {
//                brights[i] = buf.get(i);
//                brights[i] += -128;
//            }
//            generatePreloadCode(brights, "BlueNoiseOmniTri.txt");
//        }
//        Pixmap pix = new Pixmap(Gdx.files.internal("LDR_LLL1_0.png"));
        for (int idx = 0; idx < 64; idx++) {
            Pixmap pix = new Pixmap(Gdx.files.local("blueNoise/blueTri64_"+idx+".png"));
            ByteBuffer buf = pix.getPixels();
            final int len = pix.getWidth() * pix.getHeight();
            byte[] brights = new byte[len];
            for (int i = 0; i < len; i++) {
                brights[i] = buf.get(i);
                brights[i] += -128;
            }
            generatePreloadCode(brights, "BlueNoise.txt");
//            generatePreloadCode(brights, "BlueNoise"+idx+".txt");
        }
        Gdx.app.exit();
    }
    /**
     * Given a byte array, this appends to a file called {@code filename} containing a code snippet that can be pasted
     * into Java code as the preload data used by {@link PaletteReducer#exact(int[], byte[])}; this is almost never
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
//                        if(Character.isISOControl(b))
//                            sb.append(String.format("\\%03o", b));
//                        else
                            sb.append((char) (b&0xFF));
                        break;
                }
            }
        }
        sb.append("\".getBytes(\"ISO-8859-1\");\n");
//        sb.append("\".getBytes(StandardCharsets.ISO_8859_1),\n");
        Gdx.files.local(filename).writeString(sb.toString(), true, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
    }

}
