package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.utils.TimeUtils;

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

    public void create() {
        PaletteReducer reducer = new PaletteReducer(Coloring.AURORA, PaletteReducer.oklabMetric);
        generatePreloadCode(reducer.paletteMapping, "AuroraPreload.txt");
//        generatePreloadCode(Gdx.files.local("haltonic.txt").readBytes(), "HaltonicPreload.txt");
        Gdx.app.exit();
    }
    /**
     * Given a byte array, this writes a file containing a code snippet that can be pasted into Java code as the preload
     * data used by {@link PaletteReducer#exact(int[], byte[])}; this is almost never needed by external code. When 
     * using this for preload data, the byte array should be {@link PaletteReducer#paletteMapping}.
     * @param data the bytes to use as preload data, usually {@link PaletteReducer#paletteMapping}
     */
    public static void generatePreloadCode(final byte[] data) {
        generatePreloadCode(data, "bytes_" + TimeUtils.millis() + ".txt");
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
                        if(Character.isISOControl(b))
                            sb.append(String.format("\\%03o", b));
                        else
                            sb.append((char) (b&0xFF));
                        break;
                }
            } 
        }
        sb.append("\".getBytes(StandardCharsets.ISO_8859_1);\n");
        Gdx.files.local(filename).writeString(sb.toString(), true, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
    }
}
