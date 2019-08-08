package colorweaver;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HueShifter extends ApplicationAdapter {
    //public static final int backgroundColor = Color.rgba8888(Color.DARK_GRAY);
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    protected MutantBatch batch;
    protected Viewport screenView;
    protected Texture screenTexture;
    protected BitmapFont font;

    private Texture palette;

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Hue Shifter");
        config.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
        config.setIdleFPS(10);
        config.useVsync(true);
        config.setResizable(false);
        final HueShifter app = new HueShifter();
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public void filesDropped(String[] files) {
                if (files != null && files.length > 0) {
                    if (files[0].endsWith(".png"))
                        PNG8.hueShiftPalette(Gdx.files.absolute(files[0]), Gdx.files.absolute(files[0].substring(0, files[0].length() - 4) + "_HS.png"));
                }
            }
        });

        new Lwjgl3Application(app, config);
    }

    @Override
    public void create() {
        batch = new MutantBatch();
        font = new BitmapFont();
        Gdx.input.setInputProcessor(inputProcessor());
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            batch.begin();
            font.draw(batch, "Drag and drop an image file onto this window;", 20, 150);
            font.draw(batch, "a palette-reduced version will be shown here.", 20, 120);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        
    }

    public InputProcessor inputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.Q:
                    case Input.Keys.ESCAPE:
                        Gdx.app.exit();
                        break;
                }
                palette.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                return true;
            }
        };
    }
}
