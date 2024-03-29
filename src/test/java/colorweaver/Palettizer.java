package colorweaver;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.IOException;

public class Palettizer extends ApplicationAdapter {
    //public static final int backgroundColor = Color.rgba8888(Color.DARK_GRAY);
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 720;
    protected SpriteBatch batch;
    protected Viewport screenView;
    protected Texture screenTexture;
    protected BitmapFont font;
    protected PaletteReducer reducer;
    protected PNG8 png8;
    protected ColorEqualizer eq;
    private int[] palette;

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Palette Reducer");
        config.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
        config.setIdleFPS(10);
        config.useVsync(true);
        config.setResizable(false);
        final Palettizer app = new Palettizer();
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public void filesDropped(String[] files) {
                if (files != null && files.length > 0) {
                    if (files[0].endsWith(".png") || files[0].endsWith(".jpg") || files[0].endsWith(".jpeg"))
                        app.load(files[0]);
                }
            }
        });

        new Lwjgl3Application(app, config);
    }

    public void load(String name) {
        try {
            final String suffix = "_Gray4";
            //// loads a file by its full path, which we get via drag+drop
            Pixmap pm;
//            reducer.analyze(pm, 1600, 32);
            String subname = name.substring(Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\')) + 1, name.lastIndexOf('.'));
//            pm = (reducer.reduceWithNoise(new Pixmap(Gdx.files.absolute(name))));
//            png8.writePrecisely(Gdx.files.local(subname + "_FloydSteinbergHu"+suffix+".png"), pm, false);
            
//            pm = reducer.reduceBurkes(new Pixmap(Gdx.files.absolute(name)));
//            png8.writePrecisely(Gdx.files.local(subname + "_Burkes"+suffix+".png"), pm, false);
            
//            pm = reducer.reduce(new Pixmap(Gdx.files.absolute(name)));
//            png8.writePrecisely(Gdx.files.local(subname + "_SierraLite"+suffix+".png"), pm, false);
            
            pm = reducer.reduceSolid(new Pixmap(Gdx.files.absolute(name)));
            png8.writePrecisely(Gdx.files.local(subname + "_Solid"+suffix+".png"), pm, palette, false, 1);
            
//            pm = reducer.reduceWithRoberts(new Pixmap(Gdx.files.absolute(name)));
//            png8.writePrecisely(Gdx.files.local(subname + "_Roberts"+suffix+".png"), pm, false);
            
//            pm = reducer.reduceRobertsMul(new Pixmap(Gdx.files.absolute(name)));
//            png8.writePrecisely(Gdx.files.local(subname + "_RobertsMul"+suffix+".png"), pm, false);
            
//            pm = reducer.reduceRobertsEdit(new Pixmap(Gdx.files.absolute(name)));
//            png8.writePrecisely(Gdx.files.local(subname + "_RobertsEdit"+suffix+".png"), pm, false);

            pm = reducer.reduceTrueBlue3(new Pixmap(Gdx.files.absolute(name)));
            png8.writePrecisely(Gdx.files.local(subname + "_Blue"+suffix+".png"), pm, palette, false, 1);

            pm = reducer.reduceBluish(new Pixmap(Gdx.files.absolute(name)));
            png8.writePrecisely(Gdx.files.local(subname + "_Bluish"+suffix+".png"), pm, palette, false, 1);

            pm = reducer.reduceIGN(new Pixmap(Gdx.files.absolute(name)));
            png8.writePrecisely(Gdx.files.local(subname + "_IGN"+suffix+".png"), pm, palette, false, 1);

            pm = reducer.reduceKnollRoberts(new Pixmap(Gdx.files.absolute(name)));
            png8.writePrecisely(Gdx.files.local(subname + "_KR_G"+suffix+".png"), pm, palette, false, 1);
            
//            FileHandle next = Gdx.files.local(subname + "_IGN"+suffix+".png");
//            png8.writePrecisely(next, pm, reducer.paletteArray, false, 0);
//            int[] hsp = Arrays.copyOf(reducer.paletteArray, 256);
//            PaletteReducer.hueShiftPalette(hsp);
//            PNG8.swapPalette(next, Gdx.files.local(subname + "_IGNHSP"+suffix+".png"), hsp);
            pm = (reducer.reduceFloydSteinberg(new Pixmap(Gdx.files.absolute(name))));
            FileHandle next = Gdx.files.local(subname + "_FloydSteinberg"+suffix+".png");
            png8.writePrecisely(next, pm, palette, false, 1);
            screenTexture = new Texture(next);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void create() {
        font = new BitmapFont();
        batch = new SpriteBatch();
        eq = new ColorEqualizer();
        palette = Coloring.GB;
        /*
                    0x000000FF, 0x6F6776FF, 0x9A9A97FF, 0xC5CCB8FF, 0x8B5580FF, 0xC38890FF, 0xA593A5FF, 0x666092FF,
            0x9A4F50FF, 0xC28D75FF, 0x7CA1C0FF, 0x416AA3FF, 0x8D6268FF, 0xBE955CFF, 0x68ACA9FF, 0x387080FF,
            0x6E6962FF, 0x93A167FF, 0x6EAA78FF, 0x557064FF, 0x9D9F7FFF, 0x7E9E99FF, 0x5D6872FF, 0x433455FF,

         */
            reducer = new PaletteReducer(palette, PaletteReducer.labMetric); 
            //Colorizer.SmashColorizer.getReducer(); 
//                new PaletteReducer(); 
                //new PaletteReducer(Colorizer.JudgeBonusPalette);
//                Coloring.FLESURRECT_REDUCER;
                //Colorizer.AuroraColorizer.getReducer();
                //Colorizer.RinsedColorizer.getReducer();
//                 new PaletteReducer(Coloring.RELAXED_ROLL);
//                 new PaletteReducer(Coloring.DB16, PaletteReducer.labMetric);
//                 new PaletteReducer(Coloring.DB32);
        reducer.setDitherStrength(0.5f);
        png8 = new PNG8();
        png8.palette = reducer;
        png8.setFlipY(false);
        screenView = new ScreenViewport();
        screenView.getCamera().position.set(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, 0);
        screenView.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.enableBlending();
        Gdx.input.setInputProcessor(inputProcessor());

        // if you don't have these files on this absolute path, that's fine, and they will be ignored
//        load("D:/Painting_by_Henri_Biva.jpg");
//        load("D:/Mona_Lisa.jpg");
//        load("D:/Among_the_Sierra_Nevada_by_Albert_Bierstadt.jpg");
        load("D:/Satchmo_baby_face.png");
        Gdx.app.exit();
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(screenView.getCamera().combined);
        batch.begin();
        if(screenTexture != null)
            batch.draw(screenTexture, 0, 0);
        else {
            font.draw(batch, "Drag and drop an image file onto this window;", 20, 150);
            font.draw(batch, "a palette-reduced copy will be written to this folder.", 20, 120);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        screenView.update(width, height);
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
                return true;
            }
        };
    }
}
