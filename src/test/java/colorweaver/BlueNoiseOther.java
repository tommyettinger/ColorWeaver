package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
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
    public byte[][] bytes;
    public void create() {
        bytes = BlueNoise.ALT_NOISE;
        int[] counts = new int[256];
        byte[] tri = new byte[0x1000], choice = new byte[0x1000];
        int bestOff = Integer.MAX_VALUE;
        for (int i = 0; i < 63; i++) {
            for (int j = i+1; j < 64; j++) {
                Arrays.fill(counts, 0);
                for (int k = 0; k < 0x1000; k++) {
                    counts[128 + (tri[k] = (byte) (bytes[i][k] + bytes[j][k] >> 1))]++;
                }
                int off = 0;
                for (int k = 0; k < 128; k++) {
                    off += Math.abs(counts[k] - counts[255 - k]);
                }
                if(off < bestOff)
                {
                    System.arraycopy(tri, 0, choice, 0, 0x1000);
                    bestOff = off;
                }
            }
        }
        System.out.println("Best offset was " + bestOff);
        BlueNoiseTiler.generatePreloadCode(choice, "TriangularBlue.txt");
    }
}
