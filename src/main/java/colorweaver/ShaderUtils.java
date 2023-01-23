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
     * A snippet to be used from other shader code, this provides a blue() method that takes a position in pixels and
     * returns an approximation of blue noise, from -1 to 1.
     */
    public static final String blueNoiseSnippet =
            "float hash(vec2 p) {\n" +
            "    return fract(dot(vec2(476.39, 687.12), p) * sin(p.x * 3.1 + p.y * 4.3 + 13.1));\n" +
            "}\n" +
            "float blue(vec2 p) {\n" +
            "    float v =  hash(p + vec2(-1, 0))\n" +
            "             + hash(p + vec2( 1, 0))\n" +
            "             + hash(p + vec2( 0, 1))\n" +
            "             + hash(p + vec2( 0,-1)); \n" +
            "    return  hash(p) - v * 0.25;\n" +
            "}\n";

//                    "   float len = dot(tgt.rgb, bright * 0.0625) + 1.0;\n" +
//                    "   float adj = (fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) - step(fract(dot(vec2(0.75487, 0.56984), gl_FragCoord.xy)), 0.5)));\n" +
//                    "   float adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy)));\n" +
//                    "   float roberts = fract(dot(vec2(0.75487, 0.56984), gl_FragCoord.yx));\n" +
//                    "   float checker = step(fract(dot(vec2(0.5), gl_FragCoord.yx)), 0.499);\n" +
//                    "   float rob = fract(dot(vec4(0.8566748838545029, 0.733891856627126, 0.6287067210378087, 0.5385972572236101), gl_FragCoord.xyyx));\n" +
//                    "   float vlachos = fract(dot(vec2(171.0, 231.0), gl_FragCoord.xy) / 71.0);\n" +

//                    "   adj = (adj - rob);\n" +
//                    "   adj -= fract(adj * 61.803399);\n" +
//                    "   adj = fract(vlachos - adj) * 2.0 - 1.0;\n" +
//                    "   float adj = asin(fract(dot(vec2(171.0, 231.0), gl_FragCoord.xy) / 71.0) * 1.68 - 0.84);\n" +
//                    "   float adj = asin(mix(vlachos, jimenez, checker) * 1.68 - 0.84);\n" + //1.68 - 0.84
//                    "   float adj = asin(mix(roberts, jimenez, checker) - 0.25);\n" + //1.68 - 0.84
//                    "   float adj = asin(jimenez * 1.68 - 0.84);\n" +
//                    "   float adj = jimenez * jimenez - 0.25;\n" +

    /**
     * This fragment shader substitutes colors with ones from a palette, dithering as needed using interleaved gradient
     * noise by Jorge Jimenez (modified to incorporate the brightness of a color in dithering calculations). It is very
     * hard to find repeating patterns in this form of dithering, though they can happen in small palettes.
     */
    public static final String fragmentShaderOld =
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
                    //// white noise, to compare with gradient interleaved noise
                    //"   float adj = fract(4768.1232345456 * sin((gl_FragCoord.x+gl_FragCoord.y*43.0+137.0))) - 0.4;\n" +
                    "   float adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) - 0.4;\n" +
                    "   tgt.rgb = clamp(tgt.rgb + (tgt.rgb - used.rgb) * adj, 0.0, 1.0);\n" +
                    "   gl_FragColor.rgb = v_color.rgb * texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";

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
//                    "const vec3 xBumps = vec3(0.0);\n" +
//                    "const vec3 yBumps = vec3(0.0);\n" +
                    "const vec3 xBumps = vec3(-1., 0., 3.);\n" +
                    "const vec3 yBumps = vec3(-3., 1., 0.);\n" +
//                    "const vec3 xBumps = vec3(-1., 3., 2.);\n" +
//                    "const vec3 yBumps = vec3(1., -1., 3.);\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   vec3 adj = (fract((xBumps + gl_FragCoord.x) * 0.75488 + (yBumps + gl_FragCoord.y) * 0.56984) - 0.5) * 3.0f;\n" + // * 3.0 makes this in the -1.5 to 1.5 range
                    "   adj *= 0.55 / (1.875 + abs(adj));\n" + // sigmoid function; 0.55 affects adjustment range, 1.875 makes the change more gradual as it gets higher
                    "   tgt.rgb = clamp(tgt.rgb + adj, 0.0, 1.0);\n" +
                    "   gl_FragColor.rgb = v_color.rgb * texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g)).rgb;\n" +
                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";

    public static final String fragmentShaderBlue =
            "varying vec2 v_texCoords;\n" +
                    "varying vec4 v_color;\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform sampler2D u_palette;\n" +
                    "uniform sampler2D u_blue;\n" +
                    "const float b_adj = 31.0 / 32.0;\n" +
                    "const float rb_adj = 32.0 / 1023.0;\n" +
                    "void main()\n" +
                    "{\n" +
                    "   float adj = (texture2D(u_blue, gl_FragCoord.xy * (1.0 / 64.0)).r * 0.14 +" +
                    "   0.06 * fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy)))) - 0.1;\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   tgt.rgb = clamp(tgt.rgb + adj, 0.0, 1.0);\n" +
                    "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
                    "   gl_FragColor.rgb = v_color.rgb * used.rgb;\n" +
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
                    "vec3 pq(vec3 color) {\n" +
                    "   color = pow(color, 0.1593017578125);\n" +
                    "   return pow((0.8359375 + 18.8515625 * color) / (1.0 + 18.6875 * color), 78.84375);\n" +
                    "}\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = texture2D( u_texture, v_texCoords );\n" +
                    "   tgt.rgb = pq(tgt.rgb);\n" +
                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, vec3(0.375, 0.5, 0.125)), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
                    "   gl_FragColor.rgb = v_color.rgb * clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +
//                    "   gl_FragColor.rgb = v_color.rgb * clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.5, 0.0)), dot(tgt.rgb, vec3(1.0, 0.0, 0.5)), dot(tgt.rgb, vec3(1.0, -0.25, -0.25))), 0.0, 1.0);\n" +
                    "   gl_FragColor.a = v_color.a * tgt.a;\n" +
                    "}";

    // older YCwCm to RGB conversion used previously in fragmentShaderOnlyWarmMild
    //                    "   gl_FragColor.rgb = v_color.rgb * clamp(vec3(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), dot(tgt.rgb, vec3(1.0, -0.375, -0.5))), 0.0, 1.0);\n" +

    public static final String fragmentShaderColorblind =
       "varying vec2 v_texCoords;\n"
          + "varying vec4 v_color;\n"
          + "uniform sampler2D u_texture;\n" 
          + "void main()\n" 
          + "{\n" 
          + "    vec4 tgt = v_color * texture2D( u_texture, v_texCoords );\n"
          + "    vec3 RGB = tgt.rgb;\n"
          + "    mat3 RGBtoLMS = mat3(17.8824, 3.45570, 0.0300,\n"
          + "                         43.5161, 27.1554, 0.1843,\n"
          + "                         4.11940,  3.8671, 1.4671);\n"
          + "    vec3 LMS = RGBtoLMS * RGB;\n"
          + "    float alpha_d = 1.0;\n"
          + "    float alpha_p = 0.0;\n"
          + "    mat3 hybrid = mat3((1.0-alpha_p),   0.4942*alpha_d, 0.0,\n"
          + "                        2.0234*alpha_p, 1.0-alpha_d,    0.0,\n"
          + "                       -2.5258*alpha_p, 1.2483*alpha_d, 1.0);\n"
          + "    LMS = hybrid * LMS;\n"
          + "    mat3 LMStoRGB = mat3(0.0809446, -0.0102483, -0.000367778,\n"
          + "                        -0.1305040,  0.0540190, -0.004117350,\n"
          + "                         0.1167140, -0.1136120,  0.693502000);\n"
          + "    RGB = LMStoRGB * LMS;\n" 
          + "    gl_FragColor = vec4(RGB,tgt.a);\n"
          + "}";


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
     * This fragment shader substitutes colors with ones from a palette, dithering as needed by mixing gradient interleaved noise,
     * by Jorge Jimenez, with the R2 point sequence dithering technique suggested by Martin Robert. This particular set of changes
     * seems especially good at avoiding obvious linear patterns, due to how it calculates the degree of adjustment towards (or,
     * less frequently, away from) the target color using the gradient interleaved noise result (a large fraction of it), minus
     * the R2 result (half of it), and finally running that through asin() to get a value that is positive more often than it is
     * negative, with competing patterns that resolve into soft, winding noise.
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
          "const vec3 pow64 = vec3(64.0, 64.0, 64.0);\n" + 
          "vec3 emphasize(vec3 color) { return pow((7.0625 * color) / (0.0625 + 7.0 * color), pow64); }\n" +
          "void main()\n" +
          "{\n" +
          "   vec4 tgt = v_color * texture2D( u_texture, v_texCoords );\n" +
          "   tgt.rgb = u_add + u_mul * tgt.rgb;\n" +
//          "   tgt.rgb = u_add + u_mul * emphasize(tgt.rgb);\n" +
          "   vec4 used = texture2D(u_palette, vec2((tgt.b * b_adj + floor(tgt.r * 31.999)) * rb_adj, 1.0 - tgt.g));\n" +
//          "   float adj = fract(dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy));\n" + // Roberts
//          "   float adj = fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy)));\n" + // Jimenez
//          "   float adj = 2.0 * sin(fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) * 1.44 - 0.72 );\n" +
          "   float adj = sin(fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) * 2.0 - 1.0);\n" +
//          "   float adj = 1.5 * sin(fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy)))   - fract(dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy)) * 0.6 );\n" +
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
                    "const vec3 pow64 = vec3(64.0, 64.0, 64.0);\n" +
                    "vec3 emphasize(vec3 color) { return pow((7.0625 * color) / (0.0625 + 7.0 * color), pow64); }\n" +
                    "void main()\n" +
                    "{\n" +
                    "   vec4 tgt = v_color * texture2D( u_texture, v_texCoords );\n" +
//                    "   tgt.rgb = emphasize(tgt.rgb);\n" +
                    "   tgt.rgb = u_add + u_mul * vec3(dot(tgt.rgb, bright), tgt.r - tgt.b, tgt.g - tgt.b);\n" +
                    "   vec4 used = texture2D(u_palette, vec2((clamp(dot(tgt.rgb, vec3(1.0, -0.375, -0.5)), 0.0, 1.0) * b_adj + floor(clamp(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), 0.0, 1.0) * 31.999)) * rb_adj, 1.0 - clamp(dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), 0.0, 1.0)));\n" +
                    "   used.rgb = vec3(dot(used.rgb, bright), used.r - used.b, used.g - used.b);\n" +
                    "   float adj = (fract(52.9829189 * fract(dot(vec2(0.06711056, 0.00583715), gl_FragCoord.xy))) + 0.09375 - fract(dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy))) * 0.5125;\n" +
                    "   tgt.rgb += (tgt.rgb - used.rgb) * adj;\n" +
                    "   gl_FragColor.rgb = texture2D(u_palette, vec2((clamp(dot(tgt.rgb, vec3(1.0, -0.375, -0.5)), 0.0, 1.0) * b_adj + floor(clamp(dot(tgt.rgb, vec3(1.0, 0.625, -0.5)), 0.0, 1.0) * 31.999)) * rb_adj, 1.0 - clamp(dot(tgt.rgb, vec3(1.0, -0.375, 0.5)), 0.0, 1.0))).rgb;\n" +
                    "   gl_FragColor.a = tgt.a;\n" +
                    "}";
    
    
    //"   vec3 adj = fract(vec3(0.8986537126286993, 0.8075784952213448, 0.6521830259439717) * dot(vec2(0.7548776662466927, 0.5698402909980532), gl_FragCoord.xy)) - 0.25;\n" +
}
