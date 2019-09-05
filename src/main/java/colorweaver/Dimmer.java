package colorweaver;

import com.badlogic.gdx.math.MathUtils;

/**
 * Dimmer provides default implementations of bright, dimmer, dim and dark which refer to the light method, and a default implementation of the light method which refers to the other four. Extension classes can overload just the light method or alternatively can overload the other four. Either way will work.
 * <p>
 * Also contained here are various extensions as member classes.
 *
 * @author Ben McLean
 */
public abstract class Dimmer implements IDimmer {
    /**
     * Refers to the light method to get the answer.
     */
    @Override
    public int bright(byte voxel) {
        return dimmer(3, voxel);
    }

    /**
     * Refers to the light method to get the answer.
     */
    @Override
    public int medium(byte voxel) {
        return dimmer(2, voxel);
    }

    /**
     * Refers to the light method to get the answer.
     */
    @Override
    public int dim(byte voxel) {
        return dimmer(1, voxel);
    }

    /**
     * Refers to the light method to get the answer.
     */
    @Override
    public int dark(byte voxel) {
        return dimmer(0, voxel);
    }

    /**
     * Refers to the dark, dim, dimmer and bright methods to get the answer.
     *
     * @param brightness 0 for dark, 1 for dim, 2 for medium and 3 for bright. Negative numbers are expected to normally be interpreted as black and numbers higher than 3 as white.
     * @param voxel      The color index of a voxel
     * @return An rgba8888 color
     */
    @Override
    public int dimmer(int brightness, byte voxel) {
        if (voxel == 0) return 0; // 0 is equivalent to Color.rgba8888(Color.CLEAR)
        switch (brightness) {
            case 0:
                return dark(voxel);
            case 1:
                return dim(voxel);
            case 2:
                return medium(voxel);
            case 3:
                return bright(voxel);
        }
        return brightness > 3
                ? 0xffffffff //rgba8888 value of Color.WHITE
                : 0xff;      //rgba8888 value of Color.BLACK
    }

    /**
     * Allows implementors to mark whether an IDimmer allows shading and/or outlining to be disabled for each color
     * index, determined by the status of a specific shade bit. If this returns 0, the palette does not support custom
     * shading rules. If this returns a power of two between 1 and 128, when {@code (voxel & getShadeBit()) != 0}, an
     * alternate set of shading rules will be used, which usually disables shading and outlining for color indices with
     * that bit set.
     *
     * @return 0 if this does not have configurable shading, or a power of two between 1 and 128 when that bit marks special voxels with different shading rules
     */
    @Override
    public int getShadeBit() {
        return 0;
    }

    /**
     * Allows implementors to mark whether an IDimmer allows the shading of a voxel to vary depending on that voxel's
     * position in 3D space plus time, determined by the status of a specific wave bit. If this returns 0, the palette
     * does not support custom shading rules. If this returns a power of two between 1 and 128, when
     * {@code (voxel & getWaveBit()) != 0}, an alternate set of shading rules will be used. This has different behavior
     * if the bit that can be specified by {@link #getShadeBit()} is set at the same time the wave bit is specified.
     * If the wave bit is set and the shade bit (if any) is not set, then this is expected to use some form of 4D
     * continuous noise or seamless 3D noise to change the shading of a voxel, but the outline should be drawn with the
     * same color (when using {@link #dimmer(int, byte)}, brightness 0 stays the same, while the other brightnesses
     * should change using the noise). If both the wave bit and the shade bit are set, then the "wave" this refers to is
     * a pulsing light wave with a wavelength, and as with the shade bit on its own there should be no outline. The
     * pulsing effect for non-outline colors is suggested to use
     * {@code int brightness = (x + y + z + time & 3); brightness += 1 - (brightness & (brightness << 1));}, which will
     * cause the brightness to zigzag between 1 and 3, spending more time at brightness 2.
     *
     * @return 0 if this does not have wave shading, or a power of two between 1 and 128 when that bit marks special voxels that change shading over time and across space
     */
    @Override
    public int getWaveBit() {
        return 0;
    }

    /**
     * Renders arbitrarily brighter or darker using the colors available in another Dimmer.
     *
     * @author Ben McLean
     */
    public static class OffsetDimmer extends Dimmer {
        public OffsetDimmer(IDimmer dimmer) {
            super();
            set(dimmer);
        }

        protected int offset = 0;

        public int offset() {
            return offset;
        }

        public OffsetDimmer set(int offset) {
            this.offset = offset;
            return this;
        }

        public OffsetDimmer add(int offset) {
            this.offset += offset;
            return this;
        }

        protected IDimmer dimmer;

        public IDimmer dimmer() {
            return dimmer;
        }

        public OffsetDimmer set(IDimmer dimmer) {
            this.dimmer = dimmer;
            return this;
        }

        @Override
        public int dimmer(int brightness, byte voxel) {
            return dimmer.dimmer(brightness + offset, voxel);
        }
    }

    public static final IDimmer RinsedDimmer = new Dimmer() {
        protected int[] palette = Coloring.RINSED;

        @Override
        public int bright(byte voxel) {
            return palette[(voxel & 248) + Math.max((voxel & 7) - 1, 0)];
        }

        @Override
        public int medium(byte voxel) {
            return palette[(voxel & 255)];
        }

        @Override
        public int dim(byte voxel) {
            return palette[(voxel & 248) + Math.min((voxel & 7) + 1, 7)];
        }

        @Override
        public int dark(byte voxel) {
            return palette[(voxel & 248) + Math.min((voxel & 7) + 2, 7)];
        }
    };

    public static final IDimmer AuroraDimmer = new Dimmer() {
        @Override
        public int dimmer(int brightness, byte voxel) {
            return Colorizer.AURORA_RAMP_VALUES[(voxel & 255) << 2 | (
                    brightness <= 0
                            ? 3
                            : brightness >= 3
                            ? 0
                            : 3 - brightness
            )];
        }
    };

//    public static final IDimmer AuroraWarmthDimmer = new Dimmer() {
//        @Override
//        public int dark(byte voxel) {
//            return Colorizer.AURORA_WARMTH_RAMP_VALUES[voxel & 255][3];
//        }
//
//        @Override
//        public int dim(byte voxel) {
//            return Colorizer.AURORA_WARMTH_RAMP_VALUES[voxel & 255][2];
//        }
//
//        @Override
//        public int medium(byte voxel) {
//            return Colorizer.AURORA_WARMTH_RAMP_VALUES[voxel & 255][1];
//        }
//
//        @Override
//        public int bright(byte voxel) {
//            return Colorizer.AURORA_WARMTH_RAMP_VALUES[voxel & 255][0];
//        }
//    };
//
    public static IDimmer arbitraryDimmer(final int[] rgbaPalette) {
        return new Dimmer() {
            private int[][] RAMP_VALUES = new int[256][4];

            {
                for (int i = 1; i < 256 && i < rgbaPalette.length; i++) {
                    int color = RAMP_VALUES[i][2] = rgbaPalette[i],
                            r = (color >>> 24),
                            g = (color >>> 16 & 0xFF),
                            b = (color >>> 8 & 0xFF);
                    int co = r - b, t = b + (co >> 1), cg = g - t, y = t + (cg >> 1),
                            yBright = y * 21 >> 4, yDim = y * 5 >> 3, yDark = y >> 1, chromO, chromG;
                    chromO = (co * 3) >> 2;
                    chromG = (cg * 3) >> 2;
                    t = yDim - (chromG >> 1);
                    g = chromG + t;
                    b = t - (chromO >> 1);
                    r = b + chromO;
                    RAMP_VALUES[i][1] =
                            MathUtils.clamp(r, 0, 255) << 24 |
                                    MathUtils.clamp(g, 0, 255) << 16 |
                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
                    chromO = (co * 3) >> 2;
                    chromG = (cg * (256 - yBright) * 3) >> 9;
                    t = yBright - (chromG >> 1);
                    g = chromG + t;
                    b = t - (chromO >> 1);
                    r = b + chromO;
                    RAMP_VALUES[i][3] =
                            MathUtils.clamp(r, 0, 255) << 24 |
                                    MathUtils.clamp(g, 0, 255) << 16 |
                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
                    chromO = (co * 13) >> 4;
                    chromG = (cg * (256 - yDark) * 13) >> 11;
                    t = yDark - (chromG >> 1);
                    g = chromG + t;
                    b = t - (chromO >> 1);
                    r = b + chromO;
                    RAMP_VALUES[i][0] =
                            MathUtils.clamp(r, 0, 255) << 24 |
                                    MathUtils.clamp(g, 0, 255) << 16 |
                                    MathUtils.clamp(b, 0, 255) << 8 | 0xFF;
                }
            }

            @Override
            public int dimmer(int brightness, byte voxel) {
                return RAMP_VALUES[voxel & 255][
                        brightness <= 0
                                ? 0
                                : brightness >= 3
                                ? 3
                                : brightness
                        ];
            }
        };
    }

//    public static final IDimmer AuroraToFlesurrectDimmer = new Dimmer() {
//        @Override
//        public int dimmer(int brightness, byte voxel) {
//            if(voxel == 0) return 0;
//            final int color = Colorizer.AURORA_RAMP_VALUES[(voxel & 255) << 2 | 1];
//            return Colorizer.FLESURRECT_RAMP_VALUES[Coloring.FLESURRECT_REDUCER.reduceIndex(color) & 0x3F][
//                    brightness <= 0
//                            ? 0
//                            : brightness >= 3
//                            ? 3
//                            : brightness
//                    ];
//        }
//    };


}
