package colorweaver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.github.tommyettinger.digital.Base;

public class SnorglyBulkPaletteGenerator {
    public static void main(String[] args) {
        GdxNativesLoader.load();
        Gdx.files = new Lwjgl3Files();

        String[] a = new String[1];
        for (int i = 8; i <= 256; i <<= 1) {
//        for (int i = 8; i < 15; i++) {
//        for (int i = 7; i < 256; i+= 8) {
            a[0] = Base.BASE10.signed(i);
            SnorglyPaletteGenerator.reset();
            SnorglyPaletteGenerator.main(a);
        }
    }
}
