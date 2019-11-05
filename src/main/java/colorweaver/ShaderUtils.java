package colorweaver;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by Tommy Ettinger on 4/23/2019.
 */
public class ShaderUtils {
    /**
     * This is the default vertex shader from libGDX.
     */
    public static final String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "uniform mat4 u_projTrans;\n"
            + "varying vec4 v_color;\n"
            + "varying vec2 v_texCoords;\n"
            + "\n"
            + "void main()\n"
            + "{\n"
            + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
            + "   v_color.a = v_color.a * (255.0/254.0);\n"
            + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
            + "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
            + "}\n";
    
    public static final String vertexShaderImmediate = vertexShader.replace("u_projTrans", "u_projModelView");
    
    /**
     * This fragment shader substitutes colors with ones from a palette, dithering as needed using interleaved gradient
     * noise by Jorge Jimenez (modified to incorporate the brightness of a color in dithering calculations). It is very
     * hard to find repeating patterns in this form of dithering, though they can happen in small palettes.
     */
    public static final String fragmentShader =
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "const vec3 bright = vec3(0.375, 0.5, 0.125);\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
                    "   float len = dot(tgt.rgb, bright * 0.0625) + 1.0;\n" +
                    "   float adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy + len))) * len - len * 0.5;\n" +
                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
                    "   gl_FragColor.rgb = v_color.rgb * texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";

    /**
     * This fragment shader substitutes colors with ones from a palette, acting like {@link #fragmentShader} but also
     * allowing color space adjustments to be done after the palette swap (this won't change the color count). The
     * uniforms {@code u_mul} and {@code u_add} are each YCwCm adjustments. The first multiplies the Y (brightness), Cw
     * (Chroma warm, with values greater than 1 making warm colors warmer and cool colors cooler) and Cm (Chroma mild,
     * with values greater than 1 making green/yellow colors closer to those and red/blue colors closer to that) by the
     * image's YCwCm values after palette-substitution. After that, {@code u_add} is added to Y (which can have an
     * internal value between 0 and 1, and all are clamped), Cw (which ranges between -1 for blue/green and 1 for
     * red/yellow), and Cm (which ranges between -1 for red/blue and 1 for yellow/green). You can use this to desaturate
     * colors by setting {@code u_mul} to {@code vec3(1.0, 0.5, 0.5)} or any other small fractions for Cw and Cm. You
     * can make colors warmer by setting {@code u_add} to {@code vec3(0.0, 0.6, 0.0)}; while warmth is added, randomly
     * setting Cm to a value between -0.5 and 0.5 can simulate a fiery color effect over the screen. You can make an icy
     * effect by setting {@code u_add} to {@code vec3(0.3, -0.4, 0.0)}.
     */
    public static final String fragmentShaderWarmMild =
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "uniform vec3 u_add;\n" +
                    "uniform vec3 u_mul;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "const vec3 bright = vec3(0.375, 0.5, 0.125);\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
                    "   float len = dot(tgt.rgb, bright * 0.0625) + 1.5;\n" +
                    "   float adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy + len))) * len - len * 0.5;\n" +
                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
                    "   tgt.rgb = v_color.rgb * texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, bright), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
                    //// this is an alternate way but it messes up the colors on the blue to yellow axis
//                    "   gl_FragColor.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, 0.0)), dot(tgt.rgb, vec3(1.0, 0.0, 0.5)), dot(tgt.rgb, vec3(1.0, -0.5, -0.375))), 0.0, 1.0);\n" +
                    //// this is the documented "correct" way, and it seems to cover the full gamut
                    "   gl_FragColor.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +
                    //// this is an alternative that seems to cover more colors, at least at medium luma levels, but has serious issues with blue and yellow tinting
//                    "   gl_FragColor.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.5, 0.0)), dot(tgt.rgb, vec3(1.0, 0.0, 0.5)), dot(tgt.rgb, vec3(1.0, -0.25, -0.25))), 0.0, 1.0);\n" +
                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";
    /**
     * This fragment shader substitutes colors with ones from a palette, acting like {@link #fragmentShader} but also
     * allowing color space adjustments to be done after the palette swap (this won't change the color count). The
     * uniforms {@code u_mul} and {@code u_add} are each YCwCm adjustments. The first multiplies the Y (brightness), Cw
     * (Chroma warm, with values greater than 1 making warm colors warmer and cool colors cooler) and Cm (Chroma mild,
     * with values greater than 1 making green/yellow colors closer to those and red/blue colors closer to that) by the
     * image's YCwCm values after palette-substitution. After that, {@code u_add} is added to Y (which can have an
     * internal value between 0 and 1, and all are clamped), Cw (which ranges between -1 for blue/green and 1 for
     * red/yellow), and Cm (which ranges between -1 for red/blue and 1 for yellow/green). You can use this to desaturate
     * colors by setting {@code u_mul} to {@code vec3(1.0, 0.5, 0.5)} or any other small fractions for Cw and Cm. You
     * can make colors warmer by setting {@code u_add} to {@code vec3(0.0, 0.6, 0.0)}; while warmth is added, randomly
     * setting Cm to a value between -0.5 and 0.5 can simulate a fiery color effect over the screen. You can make an icy
     * effect by setting {@code u_add} to {@code vec3(0.3, -0.4, 0.0)}.
     * <br>
     * This is a variant on {@link #fragmentShaderWarmMild} that will only produce colors from one palette, with a max
     * of 255 colors plus transparent.
     */
    public static final String fragmentShaderWarmMildLimited =
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "uniform vec3 u_add;\n" +
                    "uniform vec3 u_mul;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "const vec3 bright = vec3(0.375, 0.5, 0.125);\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = v_color * texture2D( u_texture, v_texCoords );\n" +
                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, bright), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
                    "   tgt.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +
                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
                    "   float len = dot(used.rgb, bright) + 1.5;\n" +
                    "   float adj = (fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) - 0.5) * len;\n" +
                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
                    "   gl_FragColor.rgb = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   gl_FragColor.a = tgt.a;\n" +

//                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
//                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
//                    "   float len = dot(tgt.rgb, bright * 0.0625) + 1.5;\n" +
//                    "   float adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy + len))) * len - len * 0.5;\n" +
//                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
//                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, bright), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
//                    //// this is an alternate way but it messes up the colors on the blue to yellow axis
////                    "   tgt.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, 0.0)), dot(tgt.rgb, vec3(1.0, 0.0, 0.5)), dot(tgt.rgb, vec3(1.0, -0.5, -0.375))), 0.0, 1.0);\n" +
//                    //// this is the documented "correct" way, and it seems to cover the full gamut
//                    "   tgt.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +
//                    //// this is an alternative that seems to cover more colors, at least at medium luma levels, but has serious issues with blue and yellow tinting
////                    "   tgt.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.5, 0.0)), dot(tgt.rgb, vec3(1.0, 0.0, 0.5)), dot(tgt.rgb, vec3(1.0, -0.25, -0.25))), 0.0, 1.0);\n" +
//                    "   gl_FragColor.rgb = v_color.rgb * texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
//                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";
    /**
     * This fragment shader allows color space adjustments to be done and does not do any color reduction. The uniforms
     * {@code u_mul} and {@code u_add} are each YCwCm adjustments. The first multiplies the Y (brightness), Cw (Chroma
     * warm, with values greater than 1 making warm colors warmer and cool colors cooler) and Cm (Chroma mild, with
     * values greater than 1 making green/yellow colors closer to those and red/blue colors closer to that) by the
     * image's YCwCm values after palette-substitution. After that, {@code u_add} is added to Y (which can have an
     * internal value between 0 and 1, and all are clamped), Cw (which ranges between -1 for blue/green and 1 for
     * red/yellow), and Cm (which ranges between -1 for red/blue and 1 for yellow/green). You can use this to desaturate
     * colors by setting {@code u_mul} to {@code vec3(1.0, 0.5, 0.5)} or any other small fractions for Cw and Cm. You
     * can make colors warmer by setting {@code u_add} to {@code vec3(0.0, 0.6, 0.0)}; while warmth is added, randomly
     * setting Cm to a value between -0.5 and 0.5 can simulate a fiery color effect over the screen. You can make an icy
     * effect by setting {@code u_add} to {@code vec3(0.3, -0.4, 0.0)}. You can simulate the desaturation and yellowing
     * that happens to old paintings by setting {@code u_mul} to {@code vec3(0.9, 0.7, 0.75)} and {@code u_add} to
     * {@code vec3(0.05, 0.14, 0.16)}.
     */
    public static final String fragmentShaderOnlyWarmMild =
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform vec3 u_add;\n" +
                    "uniform vec3 u_mul;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, vec3(0.375, 0.5, 0.125)), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
                    "   gl_FragColor.rgb = v_color.rgb * clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +
//                    "   gl_FragColor.rgb = v_color.rgb * clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.5, 0.0)), dot(tgt.rgb, vec3(1.0, 0.0, 0.5)), dot(tgt.rgb, vec3(1.0, -0.25, -0.25))), 0.0, 1.0);\n" +
                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";

    // older YCwCm to RGB conversion used previously in fragmentShaderOnlyWarmMild
    //                    "   gl_FragColor.rgb = v_color.rgb * clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +

    /**
     * This fragment shader substitutes colors with ones from a palette, acting like {@link #fragmentShader} but also
     * allowing color space adjustments to be done after the palette swap (this won't change the color count). This
     * shader also "softens" colors that are warmer, making dithering affect them less strongly (useful for skin tones
     * and some other types of material). The uniforms {@code u_mul} and {@code u_add} are each YCwCm adjustments. The
     * first multiplies the Y (brightness), Cw (Chroma warm, with values greater than 1 making warm colors warmer and
     * cool colors cooler) and Cm (Chroma mild, with values greater than 1 making green/yellow colors closer to those
     * and red/blue colors closer to that) by the image's YCwCm values after palette-substitution. After that,
     * {@code u_add} is added to Y (which can have an internal value between 0 and 1, and all are clamped), Cw (which
     * ranges between -1 for blue/green and 1 for red/yellow), and Cm (which ranges between -1 for red/blue and 1 for
     * yellow/green). You can use this to desaturate colors by setting {@code u_mul} to {@code vec3(1.0, 0.5, 0.5)} or
     * any other small fractions for Cw and Cm. You can make colors warmer by setting {@code u_add} to
     * {@code vec3(0.0, 0.6, 0.0)}; while warmth is added, randomly setting Cm to a value between -0.5 and 0.5 can
     * simulate a fiery color effect over the screen. You can make an icy effect by setting {@code u_add} to
     * {@code vec3(0.3, -0.4, 0.0)}.
     * <br>
     * This is a variant on {@link #fragmentShaderWarmMildLimited} that will also only produce colors from one palette,
     * with a max of 255 colors plus transparent, but can change the lightness of warm-color areas and reduce their
     * dithering intensity.
     */
    public static final String fragmentShaderWarmMildSoft = 
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "uniform vec3 u_add;\n" +
                    "uniform vec3 u_mul;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
                    "   float len = length(tgt.rgb) + 1.0;\n" +
                    "   float adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) * len - len * 0.5;\n" +
                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj + (tgt.r * 0.0625 - tgt.b * 0.0375), 0.0, 1.0);\n" +
                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, vec3(0.375, 0.5, 0.125)), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
                    "   tgt.rgb = clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +
                    "   gl_FragColor.rgb = v_color.rgb * texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";
    
    /**
     * Modeled after {@link #fragmentShaderRobertsLimited}, but this doesn't try to use an ordered dither and instead tries to
     * use a noisy dither with a slight bias toward keeping close-enough matches as the same color.
     */
    public static final String fragmentShaderRandom =
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
                    "   float len = fract(length(tgt.rgb) * dot(sin(gl_FragCoord.xy * 5.6789), vec2(14.743036261279236, 13.580412143837574)));\n" +
                    "   float adj = asin(len * 1.8 - 1.0) * 0.6;\n" +
                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
                    "   gl_FragColor.rgb = v_color.rgb * texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";
    /**
     * This fragment shader substitutes colors with ones from a palette, without dithering.
     */
    public static final String fragmentShaderNoDither =
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
//            "   gl_FragColor = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" + //solid shading
                    "   gl_FragColor = v_color * vec4(texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb, tgt.a);\n" +
                    "}";

    /**
     * This fragment shader substitutes colors with ones from a palette, dithering as needed using a variant on the R2
     * point sequence dithering technique suggested by Martin Roberts. This particular set of changes seems especially
     * good at avoiding obvious linear patterns, due to how it calculates the degree of adjustment towards (or, less
     * frequently, away from) the target color using both R2 and asin().
     */
    public static final String fragmentShaderRobertsLimited =
       "varying vec2 v_texCoords;\n" +
          "varying vec4 v_color;\n" +
          "uniform sampler2D u_texture;\n" +
          "uniform sampler2D u_palette;\n" +
          "uniform vec3 u_add;\n" +
          "uniform vec3 u_mul;\n" +
          "const float b_adj = 31.0 / 32.0;\n" +
          "const float rb_adj = 32.0 / 1023.0;\n" +
          "void main()\n" +
          "{\n" +
          "   vec4 tgt = v_color * texture2D( u_texture, v_texCoords );\n" +
          "   tgt.rgb = u_add + u_mul * tgt.rgb;\n" +
          "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
//          "   float adj = fract(dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy));\n" + // Roberts
//          "   float adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy)));\n" + // Jimenez
          "   float adj = asin(fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) * 0.875 - fract(dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy)) * 0.5);\n" +
          "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
          "   gl_FragColor.rgb = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
          "   gl_FragColor.a = tgt.a;\n" +
          "}";

    public static final String fragmentShaderRobertsWarmMild =
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "uniform vec3 u_add;\n" +
                    "uniform vec3 u_mul;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "const vec3 bright = vec3(0.375, 0.5, 0.125);\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = v_color * texture2D( u_texture, v_texCoords );\n" +
                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, bright), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
                    "   vec4 used = texture2D(u_palette, vec2((clamp(dot(tgt.rgb, vec3(1.0, -0.375, -0.5)), 0.0, 1.0) * b_adj + floor(clamp(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), 0.0, 1.0) * 31.999)) * rb_adj, 1.0 - clamp(dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), 0.0, 1.0)));\n" +
                    "   used.rgb = vec3(dot(used.rgb, bright), used.r - used.b, used.g - used.b);\n" +
                    "   float adj = fract(dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy)) * 1.421 - 0.654;\n" +
                    "   tgt.rgb += (tgt.rgb - used.rgb) * adj;\n" +
                    "   gl_FragColor.rgb = texture2D(u_palette, vec2((clamp(dot(tgt.rgb, vec3(1.0, -0.375, -0.5)), 0.0, 1.0) * b_adj + floor(clamp(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), 0.0, 1.0) * 31.999)) * rb_adj, 1.0 - clamp(dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), 0.0, 1.0))).rgb;\n" +
                    "   gl_FragColor.a = tgt.a;\n" +
                    "}";
//"   vec3 adj = fract(vec3(0.8986537126286993, 0.8075784952213448, 0.6521830259439717) * dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy)) - 0.25;\n" +

}
