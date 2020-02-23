package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

/**
 * Created by Tommy Ettinger on 1/30/2020.
 */
public class AuroraNamedPreview extends ApplicationAdapter {
	public static final int[][] CUBE = {
		{4,4,4,4,4,4,4,0,0,4,4,4,4,4,4,4,},
		{4,4,4,4,4,0,0,3,3,0,0,4,4,4,4,4,},
		{4,4,4,0,0,3,3,3,3,3,3,0,0,4,4,4,},
		{4,0,0,3,3,3,3,3,3,3,3,3,3,0,0,4,},
		{0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,},
		{0,2,2,3,3,3,3,3,3,3,3,3,3,1,1,0,},
		{0,2,2,2,2,3,3,3,3,3,3,1,1,1,1,0,},
		{0,2,2,2,2,2,2,3,3,1,1,1,1,1,1,0,},
		{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,},
		{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,},
		{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,},
		{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,},
		{4,0,0,2,2,2,2,2,1,1,1,1,1,0,0,4,},
		{4,4,4,0,0,2,2,2,1,1,1,0,0,4,4,4,},
		{4,4,4,4,4,0,0,2,1,0,0,4,4,4,4,4,},
		{4,4,4,4,4,4,4,0,0,4,4,4,4,4,4,4,},
	};
	public static final int[][] SHAPE = ColorizerPreview.BALL;
	private Pixmap cubePix;
	private Texture[] cubeTextures;
	private Colorizer colorizer;
	private NamedColor[] names;
	private int[] palette;
	private MutantBatch batch;
	private BitmapFont font;

	public void create () {
		palette = Coloring.AURORA;
		names = NamedColor.DAWNBRINGER_AURORA;
		colorizer = Colorizer.arbitraryLABColorizer(palette);
		cubePix = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
		cubeTextures = new Texture[palette.length - 1];
		for (int i = 1; i < palette.length; i++) {
			cubeTextures[i-1] = new Texture(16, 16, Pixmap.Format.RGBA8888);
		}
		batch = new MutantBatch();
		font = new BitmapFont(Gdx.files.classpath("font.fnt"), Gdx.files.classpath("font.png"), false, false);
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
	}

	public void render () {
		batch.begin();
		for (int i = 1; i < palette.length; i++) {
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					if(SHAPE[y][x] != 4)
						cubePix.drawPixel(x, y, colorizer.dimmer(SHAPE[y][x], (byte)i));
				}
			}
			cubeTextures[i-1].draw(cubePix, 0, 0);
			batch.draw(cubeTextures[i-1], ((i-1) % 12) * 130, (22 - (i - 1) / 12) * 20 + 2, 16, 16);
			font.draw(batch, names[i].name.substring(7), ((i-1) % 12) * 130 + 20, (22 - (i - 1) / 12) * 20 + 16);
		}
		batch.end();
	}

	public void resize (int width, int height) {
	}

	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Palette Reducer");
		config.setWindowedMode(12 * 130, 23 * 20);
		config.setIdleFPS(10);
		config.useVsync(true);
		
		new Lwjgl3Application(new AuroraNamedPreview(), config);
	}

}
