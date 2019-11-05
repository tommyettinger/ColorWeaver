package colorweaver;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

/**
 * Meant to help applications adapt their visuals to be perceptible by users with either type of red-green colorblindness.
 * Works by finding the YCbCr version of colors in the image, equalizing Y (perceptual lightness) and Cb to try to ensure they are
 * different enough, but leaving Cr alone because it is an axis from green to red.
 * Created by Tommy Ettinger on 11/4/2019.
 */
public class ColorblindnessAdapter {
    private final float[] lumas = new float[1025];
    private final float[] cbs = new float[1025];
    public ColorblindnessAdapter ()
    {
    }
    public Pixmap process(Pixmap pm)
    {
        final int w = pm.getWidth();
        final int h = pm.getHeight();
        int area = (w * h - 1);
        if(area <= 0)
            return pm;
        Arrays.fill(lumas, 0);
        Arrays.fill(cbs, 0);
        int c, t, u, b;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                c = pm.getPixel(x, y);
                if((c & 0x80) != 0)
                {
                    lumas[(int)((c >>> 24) * (0x1.010102p+2f * 0.299f) +
                       (c & 0x00ff0000) * (0x1.010102p-14f * 0.587f) +
                       (c & 0x0000ff00) * (0x1.010102p-6f * 0.114f) + 0.5f)]++;
                    cbs[(int)((c >>> 24) * (0x1.010102p+2f * -0.168736f) +
                       (c & 0x00ff0000) * (0x1.010102p-14f * -0.331264f) +
                       (c & 0x0000ff00) * (0x1.010102p-6f * 0.5f) + 512.5f)]++;
                }
                else
                    area--;
            }
        }
        final float invArea = 63.75f / area;

        u = 0;
        b = 0;
//        int minLuma = 0, maxLuma = 2040;
//        for (int i = 0; i < 2041; i++) {
//            if(lumas[i] != 0)
//            {
//                minLuma = i + 8 >>> 4;
//                break;
//            }
//        }
        for (int i = 0; i < 1025; i++) {
            if(u != (u += lumas[i])) // hoo boy. if this luma showed up at least once, add its frequency to c and run.
            {
                lumas[i] = u * invArea;
            }
            if(b != (b += cbs[i])) // hoo boy. if this luma showed up at least once, add its frequency to c and run.
            {
                cbs[i] = b * invArea;
            }
        }
        float luma, cb, cr;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                c = pm.getPixel(x, y);
                u = (int)((c >>> 24) * (0x1.010102p+2f * 0.299f) +
                   (c & 0x00ff0000) * (0x1.010102p-14f * 0.587f) +
                   (c & 0x0000ff00) * (0x1.010102p-6f * 0.114f) + 0.5f);
                b = (int)((c >>> 24) * (0x1.010102p+2f * -0.168736f) +
                   (c & 0x00ff0000) * (0x1.010102p-14f * -0.331264f) +
                   (c & 0x0000ff00) * (0x1.010102p-6f * 0.5f) + 512.5f);
//                luma = lumas[t];
                luma = (lumas[u] + (u >>> 3) + (u >>> 4)); // 3/4 from actual lightness, 1/4 equalized
//                luma = (lumas[u] + (u >>> 3)); // 1/2 from actual lightness, 1/2 equalized
                cb = (cbs[b] + (b >>> 3) + (b >>> 4)) - 127.5f;
                cr = (c >>> 24) * (0x1.010102p0f * 0.5f) +
                   (c & 0x00ff0000) * (0x1.010102p-16f * -0.418688f) +
                   (c & 0x0000ff00) * (0x1.010102p-8f * -0.081312f);
                
                pm.drawPixel(x, y,
                   t = MathUtils.clamp((int)(luma + cr * 1.402f + 0.5f), 0, 255)<<24|
                      MathUtils.clamp((int)(luma - cb * 0.344136f - cr * 0.714136f + 0.5f), 0, 255)<<16|
                      MathUtils.clamp((int)(luma + cb * 1.772f + 0.5f), 0, 255)<<8|
                      (c & 0xFF));
//                System.out.println("c:"+ StringKit.hex(c)+" to shown:"+StringKit.hex(t)+ ", has Y:"+luma+",Cb:"+cb+",Cr"+cr);
            }
        }
        return pm;
    }
}
