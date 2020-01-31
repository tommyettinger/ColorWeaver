package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Created by Tommy Ettinger on 1/30/2020.
 */
public class ColorizerPreview extends ApplicationAdapter {
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
	private Pixmap cubePix;
	private Texture[] cubeTextures;
	private Colorizer colorizer;
	private int[] palette;
	private MutantBatch batch;

	public void create () {
		palette = new int[] {
			0x000000ff, 0x111111ff, 0x222222ff, 0x333333ff, 0x444444ff, 0x555555ff, 0x666666ff, 0x777777ff,
			0x888888ff, 0x999999ff, 0xaaaaaaff, 0xbbbbbbff, 0xccccccff, 0xddddddff, 0xeeeeeeff, 0xffffffff,
			0x007f7fff, 0x3fbfbfff, 0x00ffffff, 0xbfffffff, 0x8181ffff, 0x0000ffff, 0x3f3fbfff, 0x00007fff,
			0x0f0f50ff, 0x7f007fff, 0xbf3fbfff, 0xf500f5ff, 0xfd81ffff, 0xffc0cbff, 0xff8181ff, 0xff0000ff,
			0xbf3f3fff, 0x7f0000ff, 0x551414ff, 0x7f3f00ff, 0xbf7f3fff, 0xff7f00ff, 0xffbf81ff, 0xffffbfff,
			0xffff00ff, 0xbfbf3fff, 0x7f7f00ff, 0x007f00ff, 0x3fbf3fff, 0x00ff00ff, 0xafffafff, 0x00bfffff,
			0x007fffff, 0x4b7dc8ff, 0xbcafc0ff, 0xcbaa89ff, 0xa6a090ff, 0x7e9494ff, 0x6e8287ff, 0x7e6e60ff,
			0xa0695fff, 0xc07872ff, 0xd08a74ff, 0xe19b7dff, 0xebaa8cff, 0xf5b99bff, 0xf6c8afff, 0xf5e1d2ff,
			0x7f00ffff, 0x573b3bff, 0x73413cff, 0x8e5555ff, 0xab7373ff, 0xc78f8fff, 0xe3ababff, 0xf8d2daff,
			0xe3c7abff, 0xc49e73ff, 0x8f7357ff, 0x73573bff, 0x3b2d1fff, 0x414123ff, 0x73733bff, 0x8f8f57ff,
			0xa2a255ff, 0xb5b572ff, 0xc7c78fff, 0xdadaabff, 0xededc7ff, 0xc7e3abff, 0xabc78fff, 0x8ebe55ff,
			0x738f57ff, 0x587d3eff, 0x465032ff, 0x191e0fff, 0x235037ff, 0x3b573bff, 0x506450ff, 0x3b7349ff,
			0x578f57ff, 0x73ab73ff, 0x64c082ff, 0x8fc78fff, 0xa2d8a2ff, 0xe1f8faff, 0xb4eecaff, 0xabe3c5ff,
			0x87b48eff, 0x507d5fff, 0x0f6946ff, 0x1e2d23ff, 0x234146ff, 0x3b7373ff, 0x64ababff, 0x8fc7c7ff,
			0xabe3e3ff, 0xc7f1f1ff, 0xbed2f0ff, 0xabc7e3ff, 0xa8b9dcff, 0x8fabc7ff, 0x578fc7ff, 0x57738fff,
			0x3b5773ff, 0x0f192dff, 0x1f1f3bff, 0x3b3b57ff, 0x494973ff, 0x57578fff, 0x736eaaff, 0x7676caff,
			0x8f8fc7ff, 0xababe3ff, 0xd0daf8ff, 0xe3e3ffff, 0xab8fc7ff, 0x8f57c7ff, 0x73578fff, 0x573b73ff,
			0x3c233cff, 0x463246ff, 0x724072ff, 0x8f578fff, 0xab57abff, 0xab73abff, 0xebace1ff, 0xffdcf5ff,
			0xe3c7e3ff, 0xe1b9d2ff, 0xd7a0beff, 0xc78fb9ff, 0xc87da0ff, 0xc35a91ff, 0x4b2837ff, 0x321623ff,
			0x280a1eff, 0x401811ff, 0x621800ff, 0xa5140aff, 0xda2010ff, 0xd5524aff, 0xff3c0aff, 0xf55a32ff,
			0xff6262ff, 0xf6bd31ff, 0xffa53cff, 0xd79b0fff, 0xda6e0aff, 0xb45a00ff, 0xa04b05ff, 0x5f3214ff,
			0x53500aff, 0x626200ff, 0x8c805aff, 0xac9400ff, 0xb1b10aff, 0xe6d55aff, 0xffd510ff, 0xffea4aff,
			0xc8ff41ff, 0x9bf046ff, 0x96dc19ff, 0x73c805ff, 0x6aa805ff, 0x3c6e14ff, 0x283405ff, 0x204608ff,
			0x0c5c0cff, 0x149605ff, 0x0ad70aff, 0x14e60aff, 0x7dff73ff, 0x4bf05aff, 0x00c514ff, 0x05b450ff,
			0x1c8c4eff, 0x123832ff, 0x129880ff, 0x06c491ff, 0x00de6aff, 0x2deba8ff, 0x3cfea5ff, 0x6affcdff,
			0x91ebffff, 0x55e6ffff, 0x7dd7f0ff, 0x08ded5ff, 0x109cdeff, 0x055a5cff, 0x162c52ff, 0x0f377dff,
			0x004a9cff, 0x326496ff, 0x0052f6ff, 0x186abdff, 0x2378dcff, 0x699dc3ff, 0x4aa4ffff, 0x90b0ffff,
			0x5ac5ffff, 0xbeb9faff, 0x786ef0ff, 0x4a5affff, 0x6241f6ff, 0x3c3cf5ff, 0x101cdaff, 0x0010bdff,
			0x231094ff, 0x0c2148ff, 0x5010b0ff, 0x6010d0ff, 0x8732d2ff, 0x9c41ffff, 0xbd62ffff, 0xb991ffff,
			0xd7a5ffff, 0xd7c3faff, 0xf8c6fcff, 0xe673ffff, 0xff52ffff, 0xda20e0ff, 0xbd29ffff, 0xbd10c5ff,
			0x8c14beff, 0x5a187bff, 0x641464ff, 0x410062ff, 0x320a46ff, 0x551937ff, 0xa01982ff, 0xc80078ff,
			0xff50bfff, 0xff6ac5ff, 0xfaa0b9ff, 0xfc3a8cff, 0xe61e78ff, 0xbd1039ff, 0x98344dff, 0x911437ff,
		};
		colorizer = Colorizer.arbitraryLABColorizer(palette);
		cubePix = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
		cubeTextures = new Texture[palette.length];
		for (int i = 0; i < palette.length; i++) {
			cubeTextures[i] = new Texture(16, 16, Pixmap.Format.RGBA8888);
		}
		batch = new MutantBatch();
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
	}

	public void render () {
		batch.begin();
		for (int i = 0; i < palette.length; i++) {
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {						
					cubePix.drawPixel(x, y, colorizer.dimmer(CUBE[y][x] & 3, (byte)i));
				}
			}
			cubeTextures[i].draw(cubePix, 0, 0);
			batch.draw(cubeTextures[i], (i & 15) << 4, (i & -16), 16, 16);
		}
		batch.end();
	}

	public void resize (int width, int height) {
	}

	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Palette Reducer");
		config.setWindowedMode(16 * 16, 16 * 16);
		config.setIdleFPS(10);
		config.useVsync(true);
		
		new Lwjgl3Application(new ColorizerPreview(), config);
	}

}
