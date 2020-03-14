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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import static com.badlogic.gdx.Gdx.input;
import static colorweaver.ShaderUtils.*;

public class ShaderPalettizer extends ApplicationAdapter {
    //public static final int backgroundColor = Color.rgba8888(Color.DARK_GRAY);
//    public static final int SCREEN_WIDTH = 1531;
//    public static final int SCREEN_HEIGHT = 862;
    public static final int SCREEN_WIDTH = 808;
    public static final int SCREEN_HEIGHT =600;
    protected SpriteBatch batch;
    protected Viewport screenView;
    protected Texture screenTexture;
    protected BitmapFont font;

    private long startTime = 0L, lastProcessedTime = 0L;
    private ShaderProgram defaultShader;
    private ShaderProgram shader, shader2, shaderCB;
    private Texture palette;
    private FileHandle[] lospec;
    private int lospecIndex = 0;
    private Vector3 add, mul;

    private ColorEqualizer eq;
    private ColorblindnessAdapter cba;
    
    public static boolean equalize = true;
    public static boolean equalizeCB = true;

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Palette Reducer");
        config.setWindowedMode(SCREEN_WIDTH, SCREEN_HEIGHT);
        config.setIdleFPS(10);
        config.useVsync(true);
        config.setResizable(false);
        
        final ShaderPalettizer app = new ShaderPalettizer();
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public void filesDropped(String[] files) {
                if (files != null && files.length > 0) {
                    if (files[0].endsWith(".png") || files[0].endsWith(".jpg") || files[0].endsWith(".jpeg"))
                        app.load(files[0], equalize = !equalize);
                }
            }
        });

        new Lwjgl3Application(app, config);
    }

    public void load(String name, boolean equalize) {
        //// loads a file by its full path, which we get via drag+drop
        FileHandle file = Gdx.files.absolute(name);
        if(!file.exists())
            return;
        if(screenTexture != null) screenTexture.dispose();
//        ShaderPalettizer.equalize = equalize;
//        if(equalize)
//        {
//            if(equalizeCB = !equalizeCB)
//            {
//                screenTexture = new Texture(cba.processRoughness(new Pixmap(file)), Pixmap.Format.RGBA8888, false);
//                batch.setShader(shaderCB);
//            }
//            else
//            {
//                screenTexture = new Texture(cba.process(new Pixmap(file)), Pixmap.Format.RGBA8888, false);
//                batch.setShader(defaultShader);
//            }
//        }
//        else
            screenTexture = new Texture(file);
        screenTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Gdx.graphics.setWindowedMode(screenTexture.getWidth() * 2, screenTexture.getHeight());
        screenView.getCamera().position.set(screenTexture.getWidth(), screenTexture.getHeight() * 0.5f, 0);
        screenView.update(screenTexture.getWidth() * 2, screenTexture.getHeight());
    }

    @Override
    public void create() {
        startTime = TimeUtils.millis();
        eq = new ColorEqualizer();
        cba = new ColorblindnessAdapter();
        lospec = Gdx.files.local("palettes/gen/").list("_GLSL.png");
        palette = new Texture(Gdx.files.local("palettes/DB_Aurora_GLSL.png"), Pixmap.Format.RGBA8888, false);
        palette.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font = new BitmapFont();
        add = new Vector3(0, 0, 0);
        mul = new Vector3(1, 1, 1);
        defaultShader =
           //new ShaderProgram(vertexShader, fragmentShaderColorblind);
           SpriteBatch.createDefaultShader();
        shader = new ShaderProgram(vertexShader, fragmentShaderRobertsLimited);
        if (!shader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader.getLog());
        shader2 = new ShaderProgram(vertexShader, fragmentShaderRobertsWarmMild);
        if (!shader2.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shader2.getLog());
        shaderCB = 
           //defaultShader; 
           new ShaderProgram(vertexShader, fragmentShaderColorblind);
        if (!shaderCB.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + shaderCB.getLog());
        batch = new SpriteBatch(1000, defaultShader);
        screenView = new ScreenViewport();
        screenView.getCamera().position.set(SCREEN_WIDTH * 0.5f, SCREEN_HEIGHT * 0.5f, 0);
        screenView.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.enableBlending();
        Gdx.input.setInputProcessor(inputProcessor());

        // if you don't have these files on this absolute path, that's fine, and they will be ignored
//        load("samples/Painting_by_Henri_Biva.jpg", false);
//        load("samples/Among_the_Sierra_Nevada_by_Albert_Bierstadt.jpg", false);
        load("samples/Mona_Lisa.jpg", false);
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput();
        final ShaderProgram sh = batch.getShader();
        batch.setProjectionMatrix(screenView.getCamera().combined);
        if(screenTexture != null) {
            if(!sh.equals(defaultShader) && !sh.equals(shaderCB)) {
                batch.setPackedColor(-0x1.fffffep126f); // packed white
                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE1);
                palette.bind();
                batch.begin();
                sh.setUniformi("u_palette", 1);
                //if(!batch.getShader().equals(defaultShader)) 
                //{
//                    shader.setUniformf("u_mul", 0.9f, 0.7f, 0.75f);
//                    shader.setUniformf("u_add", 0.05f, 0.14f, 0.16f);
                    sh.setUniformf("u_mul", mul);
                    sh.setUniformf("u_add", add);
//                    shader.setUniformf("u_mul", 1f, 0.8f, 0.85f);
//                    shader.setUniformf("u_add", 0.1f, 0.95f, NumberTools.swayRandomized(12345, TimeUtils.timeSinceMillis(startTime) * 0x1p-9f) * 0.4f + 0.2f);
                //}
//                else batch.getShader().setUniformi("u_palette", 1);

                Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
            }
            else
            {
                batch.begin();
            }
            batch.draw(screenTexture, 0, 0);
            batch.setShader(defaultShader);
            batch.draw(screenTexture, screenTexture.getWidth(), 0);
            batch.setShader(sh);

        }
        else {
            batch.begin();
            font.draw(batch, "Drag and drop an image file onto this window;", 20, 150);
            font.draw(batch, "a palette-reduced version will be shown here.", 20, 120);
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
                    case Input.Keys.NUM_1:
                    case Input.Keys.NUMPAD_1:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/DB16_GLSL.png"), Pixmap.Format.RGBA8888, false)
//                                : new Texture(Gdx.files.local("DB22LAB_GLSL.png"), Pixmap.Format.RGBA8888, false);
                                : new Texture(Gdx.files.local("palettes/DB8_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Quorum64_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_2:
                    case Input.Keys.NUMPAD_2:
                        palette = (UIUtils.shift())
//                                ? new Texture(Gdx.files.local("palettes/Smash256_GLSL.png"), Pixmap.Format.RGBA8888, false)
                                ? new Texture(Gdx.files.local("palettes/DB32_GLSL.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/DB_Iso22_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Smash256_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Slab256_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Quorum128_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_3:
                    case Input.Keys.NUMPAD_3:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/UnsevenLloyd_GLSL.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/FlesurrectLloydC_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                                ? new Texture(Gdx.files.local("palettes/Smith256_GLSL_HS.png"), Pixmap.Format.RGBA8888, false)
//                                : new Texture(Gdx.files.local("palettes/Smith256_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/SlabBonus_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Quorum256_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_4:
                    case Input.Keys.NUMPAD_4:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/AuroraRelaxed_GLSL.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/DB_Aurora_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_5:
                    case Input.Keys.NUMPAD_5:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/Ward_GLSL.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/Flesurrect_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Flesurrect_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_6:
                    case Input.Keys.NUMPAD_6:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/WardBonus_GLSL.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/FlesurrectBonus_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/FlesurrectBonus_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_7:
                    case Input.Keys.NUMPAD_7:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/Sheltzy32_GLSL.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/Vinik24_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/RoughLAB_Aurora_GLSL_HS.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/JudgeBonus_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_8:
                    case Input.Keys.NUMPAD_8:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/Lawn64_GLSL.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/LawnBonus_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                                : new Texture(Gdx.files.local("palettes/RoughLAB_Aurora_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Uniform216_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Laser64_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/NonUniform256_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Mash256_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_9:
                    case Input.Keys.NUMPAD_9:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/Lawn256_GLSL_HS.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/Lawn256_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Laser64Bonus_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/PureBonus_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Uniform216_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.NUM_0:
                    case Input.Keys.NUMPAD_0:
                        palette = (UIUtils.shift())
                                ? new Texture(Gdx.files.local("palettes/Tincture256_GLSL_HS.png"), Pixmap.Format.RGBA8888, false)
                                : new Texture(Gdx.files.local("palettes/Tincture256_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/CIELAB_Aurora_GLSL.png"), Pixmap.Format.RGBA8888, false);
//                        palette = new Texture(Gdx.files.local("palettes/Uniform216_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.MINUS:
                        palette = (UIUtils.shift())
                            ? new Texture(Gdx.files.local("palettes/GBGreen16_GLSL.png"), Pixmap.Format.RGBA8888, false)
                            : new Texture(Gdx.files.local("palettes/GBGreen_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.EQUALS:
                        palette = (UIUtils.shift())
                            ? new Texture(Gdx.files.local("palettes/Uniform216_GLSL.png"), Pixmap.Format.RGBA8888, false)
                            : new Texture(Gdx.files.local("palettes/Quack64_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                    case Input.Keys.BACKSPACE:
                        palette = new Texture(Gdx.files.local("palettes/BW_GLSL.png"), Pixmap.Format.RGBA8888, false);
                        break;
                case Input.Keys.ENTER:
                    palette = new Texture(lospec[lospecIndex], Pixmap.Format.RGBA8888, false);
                    break;
                case Input.Keys.LEFT_BRACKET:
                    palette = new Texture(lospec[lospecIndex = (lospecIndex - 1 + lospec.length) % lospec.length], Pixmap.Format.RGBA8888, false);
                    break;
                case Input.Keys.RIGHT_BRACKET:
                    palette = new Texture(lospec[lospecIndex = (lospecIndex + 1) % lospec.length], Pixmap.Format.RGBA8888, false);
                    break;
                    case Input.Keys.M: // Mona Lisa
                        load("samples/Mona_Lisa.jpg", UIUtils.shift());
                        break;
                    case Input.Keys.S: //Sierra Nevada
                        load("samples/Among_the_Sierra_Nevada_by_Albert_Bierstadt.jpg", UIUtils.shift());
                        break;
                    case Input.Keys.B: // Biva
                        load("samples/Painting_by_Henri_Biva.jpg", UIUtils.shift());
                        break;
                    case Input.Keys.C: // Color Guard
                        load("samples/Color_Guard.png", UIUtils.shift());
                        break;
                    case Input.Keys.F: // lower-color palette
                        load("samples/Flesurrect_Spaceships.png", UIUtils.shift());
                        break;
                    case Input.Keys.A: // higher-color palette
                        load("samples/Aurora_Spaceships.png", UIUtils.shift());
                        break;
//                    case Input.Keys.D: // dither/disable
//                        if(!batch.getShader().equals(shader))
//                        {
//                            batch.setShader(shader);
//                            Gdx.graphics.setTitle("Softness OFF");
//                        }
//                        else
//                        {
//                            batch.setShader(shaderNoDither);
//                            Gdx.graphics.setTitle("Softness ON");
//                        }
//                        break;
                    case Input.Keys.SPACE:
                        if((UIUtils.shift()))
                        {
                            batch.setShader(shader2);
                        }
                        else if(!batch.getShader().equals(shader))
                        {
                            batch.setShader(shader);
                        }
//                        else if(batch.getShader().equals(shader))
//                        {
//                            batch.setShader(shaderNoDither);
//                            Gdx.graphics.setTitle("Softness ON");
//                        }
                        else 
                        {
                            batch.setShader(defaultShader);
                        }
                        break;
                }
                palette.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
                Gdx.graphics.setTitle(((batch.getShader().equals(defaultShader)) ? "Full Color" : "Palette")
                   + " with Equalize: " + (equalize ? (equalizeCB ? "COLORBLIND" : "STANDARD") : "OFF")
                   + (batch.getShader().equals(shader2) ? " on linear mode" : " on asin mode"));
                return true;
            }
        };
    }
    public void handleInput()
    {
        // only process once every 100 ms, or 10 times a second, at most
        if(TimeUtils.timeSinceMillis(lastProcessedTime) < 100)
            return;
        lastProcessedTime = TimeUtils.millis();
        Vector3 changing;
        // holding shift will change multipliers, otherwise it affects addends
        if(UIUtils.shift())
            changing = mul;
        else
            changing = add;
        if(input.isKeyPressed(Input.Keys.PERIOD) || input.isKeyPressed(Input.Keys.L)) //light
            changing.x = MathUtils.clamp(changing.x + 0.02f, -1f, 1f);
        else if(input.isKeyPressed(Input.Keys.COMMA) || input.isKeyPressed(Input.Keys.D)) //dark
            changing.x = MathUtils.clamp(changing.x - 0.02f, -1f, 1f);
        else if(input.isKeyPressed(Input.Keys.RIGHT)) //warm
            changing.y = MathUtils.clamp(changing.y + 0.02f, -2f, 2f);
        else if(input.isKeyPressed(Input.Keys.LEFT)) //cool
            changing.y = MathUtils.clamp(changing.y - 0.02f, -2f, 2f);
        else if(input.isKeyPressed(Input.Keys.UP)) //mild
            changing.z = MathUtils.clamp(changing.z + 0.02f, -2f, 2f);
        else if(input.isKeyPressed(Input.Keys.DOWN)) // bold
            changing.z = MathUtils.clamp(changing.z - 0.02f, -2f, 2f);
        else if(input.isKeyPressed(Input.Keys.R)) // reset
        {
            mul.set(1f, 1f, 1f);
            add.set(0f, 0f, 0f);
        }
        else if(input.isKeyPressed(Input.Keys.P)) // print
            System.out.println("Mul: Y="+mul.x+",Cw="+mul.y+",Cm="+mul.z+
                    "\nAdd: Y="+add.x+",Cw="+add.y+",Cm="+add.z);
        else if(input.isKeyPressed(Input.Keys.Q) || input.isKeyPressed(Input.Keys.ESCAPE)) //quit
            Gdx.app.exit();
    }

}
