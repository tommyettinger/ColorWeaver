package colorweaver;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

/**
 * Created by Tommy Ettinger on 8/2/2019.
 */
public class ColorEqualizer {
    private final float[] lumas = new float[4081];
    public ColorEqualizer()
    {
    }
    public Pixmap process(Pixmap pm)
    {
        final int w = pm.getWidth();
        final int h = pm.getHeight();
        float area = (w * h - 1f);
        if((w == 1 && h == 1) || w == 0 || h == 0)
            return pm;
        Arrays.fill(lumas, 0);
        int c, t;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                c = pm.getPixel(x, y);
                if((c & 0x80) != 0)
                    lumas[(c >>> 23 & 0x1FE) + (c >>> 24) + (c >>> 14 & 0x3FC) + (c >>> 8 & 0xFF)]++;
                else
                    area--;
            }
        }
        final float invArea = 255f / 4f / area;

        c = 0;
//        int minLuma = 0, maxLuma = 2040;
//        for (int i = 0; i < 2041; i++) {
//            if(lumas[i] != 0)
//            {
//                minLuma = i + 8 >>> 4;
//                break;
//            }
//        }
        for (int i = 0; i < 2041; i++) {
            if(c != (c += lumas[i])) // hoo boy. if this luma showed up at least once, add its frequency to c and run.
            {
                lumas[i] = c * invArea;

//                lumas[i] = (float)(Math.pow(2.0, c * invArea) - 1.0) * 255f;

//                lumas[i] = TrigTools.sin_(c * invArea) * 255f;
//                lumas[i] = (float)Math.expm1(c * invArea) * 148.40406025167826f;
//                maxLuma = i;
            }
        }
//        maxLuma = 2048 + maxLuma >>> 4;
        float luma, warm, mild;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                c = pm.getPixel(x, y);
                t = (c >>> 23 & 0x1FE) + (c >>> 24) + (c >>> 14 & 0x3FC) + (c >>> 8 & 0xFF);
//                luma = lumas[t];
                luma = (lumas[t] + (t >>> 4) + (t >>> 5)); // 3/4 from actual lightness, 1/4 equalized
//                luma = (lumas[t] + (t >>> 4)); // 1/2 from actual lightness, 1/2 equalized
                warm = (c >>> 24) - (c >>> 8 & 0xFF);
                mild = ((c >>> 16 & 0xFF) - (c >>> 8 & 0xFF)) * 0.5f;
                pm.drawPixel(x, y,
                        MathUtils.clamp((int) (luma + 0.625f * warm - mild), 0, 255)<<24|
                                MathUtils.clamp((int) (luma - 0.375f * warm + mild), 0, 255)<<16|
                                MathUtils.clamp((int) (luma - 0.375f * warm - mild), 0, 255)<<8|
                                (c & 0xFF));

            }
        }
        return pm;
    }

    /**
     * Color equalizer that uses an alternate YCwCm system, with a lightness approximation close to one employed by
     * DawnBringer. It values red at a rate of 0.25, green at a rate of 0.5625, and blue at a rate of 0.1875.
     * @param pm a Pixmap that will be modified in place
     * @return the given Pixmap, after modifications
     */
    public Pixmap processDB(Pixmap pm)
    {
        final int w = pm.getWidth();
        final int h = pm.getHeight();
        float area = (w * h - 1f), work;
        if((w == 1 && h == 1) || w == 0 || h == 0)
            return pm;
        Arrays.fill(lumas, 0);
        int c, t;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                c = pm.getPixel(x, y);
                if((c & 0x80) != 0)
                    lumas[(c >>> 22 & 0x3FC) + (c >>> 13 & 0x7F8) + (c >>> 16 & 0xFF) + (c >>> 7 & 0x1FE) + (c >>> 8 & 0xFF)]++;
                else
                    area--;
            }
        }
        final float invArea = 1f / area;

        c = 0;
//        int minLuma = 0, maxLuma = 2040;
//        for (int i = 0; i < 2041; i++) {
//            if(lumas[i] != 0)
//            {
//                minLuma = i + 8 >>> 4;
//                break;
//            }
//        }
        for (int i = 0; i < 4081; i++) {
            if(c != (c += lumas[i])) // hoo boy. if this luma showed up at least once, add its frequency to c and run.
            {
                work = (c * invArea);
                lumas[i] = (255f * 0.25f) * (work * work);

//                lumas[i] = (float)(Math.pow(2.0, c * invArea) - 1.0) * 255f;

//                lumas[i] = TrigTools.sin_(c * invArea) * 255f;
//                lumas[i] = (float)Math.expm1(c * invArea) * 148.40406025167826f;
//                maxLuma = i;
            }
        }
//        maxLuma = 2048 + maxLuma >>> 4;
        float luma, warm, mild;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                c = pm.getPixel(x, y);
                t = (c >>> 22 & 0x3FC) + (c >>> 13 & 0x7F8) + (c >>> 16 & 0xFF) + (c >>> 7 & 0x1FE) + (c >>> 8 & 0xFF);
//                luma = lumas[t];
                luma = (lumas[t] + (t >>> 5) + (t >>> 6)); // 3/4 from actual lightness, 1/4 equalized
//                luma = (lumas[t] + (t >>> 5)); // 1/2 from actual lightness, 1/2 equalized
                warm = (c >>> 24) - (c >>> 8 & 0xFF);
                mild = ((c >>> 16 & 0xFF) - (c >>> 8 & 0xFF));
                //+0.2500 +0.5625 +0.1875
                //+1.0000 +0.0000 -1.0000
                //+0.0000 +1.0000 -1.0000
                
                //luma + 0.7500 * warm - 0.5625 * mild
                //luma - 0.2500 * warm + 0.4375 * mild
                //luma - 0.2500 * warm - 0.5625 * mild
                pm.drawPixel(x, y,
                                MathUtils.clamp((int) (luma + 0.75f * warm - 0.5625f * mild), 0, 255)<<24|
                                MathUtils.clamp((int) (luma - 0.25f * warm + 0.4375f * mild), 0, 255)<<16|
                                MathUtils.clamp((int) (luma - 0.25f * warm - 0.5625f * mild), 0, 255)<<8|
                                (c & 0xFF));

                //old
                //+0.3750 +0.5000 +0.1250
                //+1.0000 +0.0000 -1.0000
                //+0.0000 +1.0000 -1.0000
                
                //red
                //+0.3750 +0.6250 +0.0000
                //+0.5000 +0.0000 -0.5000
                //+0.1250 -0.6250 +0.5000
            }
        }
        return pm;
    }
}
