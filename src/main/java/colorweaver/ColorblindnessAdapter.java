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
    private final float[] crs = new float[1025];
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
        Arrays.fill(crs, 0);
        int c, u, b, r;
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
                    crs[(int)((c >>> 24) * (0x1.010102p0f * 0.5f) +
                       (c & 0x00ff0000) * (0x1.010102p-16f * -0.418688f) +
                       (c & 0x0000ff00) * (0x1.010102p-8f * -0.081312f) + 512.5f)]++;
                }
                else
                    area--;
            }
        }
        final float invArea = 63.75f / area;

        u = 0;
        b = 0;
        r = 0;

        for (int i = 0; i < 1025; i++) {
            if(u != (u += lumas[i])) // hoo boy. if this luma showed up at least once, add its frequency to u and run.
            {
                lumas[i] = u * invArea;
            }
            if(b != (b += cbs[i])) // hoo boy. if this luma showed up at least once, add its frequency to b and run.
            {
                cbs[i] = b * invArea;
            }
            if(r != (r += crs[i])) // hoo boy. if this luma showed up at least once, add its frequency to r and run.
            {
                crs[i] = r * invArea;
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
                r = (int)((c >>> 24) * (0x1.010102p0f * 0.5f) +
                   (c & 0x00ff0000) * (0x1.010102p-16f * -0.418688f) +
                   (c & 0x0000ff00) * (0x1.010102p-8f * -0.081312f) + 512.5f);
                cb = (cbs[b] + (b >>> 3) + (b >>> 4)) - 127.5f;
                cr = (crs[r] + (r >>> 3) + (r >>> 4)) - 127.5f;
                luma = (lumas[u] + (u >>> 3) + (u >>> 4)); // 3/4 from actual lightness, 1/4 equalized
                pm.drawPixel(x, y,
                   MathUtils.clamp((int)(luma + cr * 1.402f + 0.5f), 0, 255)<<24|
                      MathUtils.clamp((int)(luma - cb * 0.344136f - cr * 0.714136f + 0.5f), 0, 255)<<16|
                      MathUtils.clamp((int)(luma + cb * 1.772f + 0.5f), 0, 255)<<8|
                      (c & 0xFF));
            }
        }
        return pm;
    }
    public Pixmap processRoughness(Pixmap pm)
    {
        final int w = pm.getWidth();
        final int h = pm.getHeight();
        int area = (w * h - 1);
        if(area <= 0)
            return pm;
        Arrays.fill(lumas, 0);
        Arrays.fill(cbs, 0);
        Arrays.fill(crs, 0);
        int c, u, b, r;
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
                    crs[(int)((c >>> 24) * (0x1.010102p0f * 0.5f) +
                       (c & 0x00ff0000) * (0x1.010102p-16f * -0.418688f) +
                       (c & 0x0000ff00) * (0x1.010102p-8f * -0.081312f) + 512.5f)]++;
                }
                else
                    area--;
            }
        }
        final float invArea = 63.75f / area;

        u = 0;
        b = 0;
        r = 0;

        for (int i = 0; i < 1025; i++) {
            if(u != (u += lumas[i])) // hoo boy. if this luma showed up at least once, add its frequency to u and run.
            {
                lumas[i] = u * invArea;
            }
            if(b != (b += cbs[i])) // hoo boy. if this luma showed up at least once, add its frequency to b and run.
            {
                cbs[i] = b * invArea;
            }
            if(r != (r += crs[i])) // hoo boy. if this luma showed up at least once, add its frequency to r and run.
            {
                crs[i] = r * invArea;
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
                r = (int)((c >>> 24) * (0x1.010102p0f * 0.5f) +
                   (c & 0x00ff0000) * (0x1.010102p-16f * -0.418688f) +
                   (c & 0x0000ff00) * (0x1.010102p-8f * -0.081312f) + 512.5f);
                cb = (cbs[b] + (b >>> 3) + (b >>> 4)) - 127.5f; // 3/4 from actual value, 1/4 equalized
                cr = (crs[r] + (r >>> 3) + (r >>> 4)) - 127.5f;
                luma = Math.max(0f, -0.1f + cr) * 1.5f; // temporary
                luma = (lumas[u] + (u >>> 3) + (u >>> 4)) + (((x ^ y >>> 1) & 1) - 0.5f) * luma;
                pm.drawPixel(x, y,
                   MathUtils.clamp((int)(luma + cr * 1.402f + 0.5f), 0, 255)<<24|
                      MathUtils.clamp((int)(luma - cb * 0.344136f - cr * 0.714136f + 0.5f), 0, 255)<<16|
                      MathUtils.clamp((int)(luma + cb * 1.772f + 0.5f), 0, 255)<<8|
                      (c & 0xFF));
            }
        }
        return pm;
    }
}
