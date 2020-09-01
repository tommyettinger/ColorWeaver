package colorweaver;

import colorweaver.tools.StringKit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import java.util.Arrays;

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
//    private static RandomXS128 random = new RandomXS128(12345678, 901234567);
    
    // Based off Alan Wolfe's work at https://github.com/Atrix256/TriangularBlueNoise
    // adapted from https://www.shadertoy.com/view/4t2SDh
    public static byte reshape(byte b)
    {
        double rnd = (b + 0.5) / 256.0 + 1.0;
        rnd -= (int)rnd;
        double orig = rnd * 2.0 - 1.0;
        rnd = (orig == 0.0) ? 0.0 : (orig / Math.sqrt(Math.abs(orig)));
        rnd = rnd - Math.signum(orig);
        return (byte) (rnd * 128 - 0.5);
    }
    public byte[][] bytes;
    public void create() {
        bytes = BlueNoise.ALT_NOISE;
        int[] counts = new int[256], bestCounts = new int[256];
        byte[] tri = new byte[0x1000], choice = new byte[0x1000];
        int bestOff = Integer.MAX_VALUE;
//        for (int trial = 0; trial < 200; trial++) { // when using random aspect

            for (int i = 0; i < 64; i++) {
                Arrays.fill(counts, 0);
                for (int k = 0; k < 0x1000; k++) {
                    counts[128 + (tri[k] = reshape(bytes[i][k]))]++;
                }
                int off = 0;
                for (int k = 0; k < 128; k++) {
                    off += Math.abs(counts[k] - counts[255 - k]);
                }
                if (off < bestOff) {
                    System.arraycopy(tri, 0, choice, 0, 0x1000);
                    System.arraycopy(counts, 0, bestCounts, 0, 256);
                    bestOff = off;
                }
            }
//        }
//        for (int i = 0; i < 63; i++) {
//            for (int j = i+1; j < 64; j++) {
//                Arrays.fill(counts, 0);
//                for (int k = 0; k < 0x1000; k++) {
//                    counts[128 + (tri[k] = (byte) (bytes[i][k] + bytes[j][k] >> 1))]++;
//                }
//                int off = 0;
//                for (int k = 0; k < 128; k++) {
//                    off += Math.abs(counts[k] - counts[255 - k]);
//                }
//                if(off < bestOff)
//                {
//                    System.arraycopy(tri, 0, choice, 0, 0x1000);
//                    bestOff = off;
//                }
//            }
//        }
        System.out.println("Best offset was " + bestOff);
        System.out.println(StringKit.join(", ", bestCounts));
        BlueNoiseTiler.generatePreloadCode(choice, "TriangularBlue.txt");
        Gdx.app.exit();
    }
}
