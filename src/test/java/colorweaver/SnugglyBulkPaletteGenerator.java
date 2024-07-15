package colorweaver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.github.tommyettinger.digital.Base;

public class SnugglyBulkPaletteGenerator {
    public static void main(String[] args) {
        GdxNativesLoader.load();
        Gdx.files = new Lwjgl3Files();

        String[] a = new String[1];
        for (int i = 7; i < 256; i+= 8) {
            a[0] = Base.BASE10.signed(i);
            SnugglyPaletteGenerator.reset();
            SnugglyPaletteGenerator.main(a);
        }
    }
}
