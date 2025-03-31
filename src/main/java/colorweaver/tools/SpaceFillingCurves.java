package colorweaver.tools;

public final class SpaceFillingCurves {

    public static final byte[] hilbert3X = new byte[0x1000];
    public static final byte[] hilbert3Y = new byte[0x1000];
    public static final byte[] hilbert3Z = new byte[0x1000];
    public static final char[] hilbert3Distances = new char[0x1000];
    private static boolean initialized3D;

    public static void init3D() {
        if (initialized3D) return;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    computeHilbert3D(x, y, z);
                }
            }
        }
        initialized3D = true;
    }

    private SpaceFillingCurves() {
    }

    /**
     * Given three 5-bit index parameters, encodes them all into a 15-bit Morton code.
     * Source: <a href="http://and-what-happened.blogspot.com/2011/08/fast-2d-and-3d-hilbert-curves-and.html">and-what-happened blog post</a>.
     *
     * @param index1 an int between 0 and 31, both inclusive
     * @param index2 an int between 0 and 31, both inclusive
     * @param index3 an int between 0 and 31, both inclusive
     * @return a 15-bit int encoding all three index parameters
     */
    public static int mortonEncode3D(int index1, int index2, int index3) {
        // pack 3 5-bit indices into a 15-bit Morton code
        index1 &= 0x0000001f;
        index2 &= 0x0000001f;
        index3 &= 0x0000001f;
        index1 *= 0x01041041;
        index2 *= 0x01041041;
        index3 *= 0x01041041;
        index1 &= 0x10204081;
        index2 &= 0x10204081;
        index3 &= 0x10204081;
        index1 *= 0x00011111;
        index2 *= 0x00011111;
        index3 *= 0x00011111;
        index1 &= 0x12490000;
        index2 &= 0x12490000;
        index3 &= 0x12490000;
        return index1 >>> 16 | index2 >>> 15 | index3 >>> 14;
    }

    /**
     * Used to initialize the 3D tables here with the 16x16x16 Hilbert curve.
     * This does not return a result, but instead assigns values to
     * {@link #hilbert3X}, {@link #hilbert3Y}, {@link #hilbert3Z}, and {@link #hilbert3Distances}.
     *
     * @param x between 0 and 15, both inclusive
     * @param y between 0 and 15, both inclusive
     * @param z between 0 and 15, both inclusive
     */
    private static void computeHilbert3D(int x, int y, int z) {
        int hilbert = mortonEncode3D(x, y, z);
        int block = 9;
        int hcode = hilbert >> block & 7;
        int mcode, shift, signs;
        shift = signs = 0;
        while (block > 0) {
            block -= 3;
            hcode <<= 2;
            mcode = 0x20212021 >> hcode & 3;
            shift = 0x48 >> 7 - shift - mcode & 3;
            signs = (signs | signs << 3) >> mcode;
            signs = (signs ^ 0x53560300 >> hcode) & 7;
            mcode = hilbert >> block & 7;
            hcode = mcode;
            hcode = (hcode | hcode << 3) >> shift & 7;
            hcode ^= signs;
            hilbert ^= (mcode ^ hcode) << block;
        }

        hilbert ^= hilbert >> 1 & 0x92492492;
        hilbert ^= (hilbert & 0x92492492) >> 1;

        hilbert3X[hilbert] = (byte) x;
        hilbert3Y[hilbert] = (byte) y;
        hilbert3Z[hilbert] = (byte) z;
        hilbert3Distances[x | y << 4 | z << 8] = (char) hilbert;
    }

    /**
     * Gets the x-position on the 32x32x32 "Pealbert Curve" at the given distance.
     * The Pealbert Curve travels from [0,0,0] to [31,31,31] after touching 32768 vertices.
     * It uses eight Hilbert Curves, each 16x16x16, to move the way a Peano Curve does.
     * @param distance between 0 and 32767, inclusive, but higher values wrap
     * @return the x-position of the vertex at the given distance
     */
    public static int getPealbertX(int distance) {
        final int section = distance >>> 12 & 7;
        switch (section) {
            case 0:
            case 5: return hilbert3Z[distance & 0xFFF];
            case 1:
            case 2: return hilbert3X[distance & 0xFFF] + 16;
            case 3: return 15 - hilbert3Z[distance & 0xFFF];
            case 4: return hilbert3X[distance & 0xFFF];
            case 6: return hilbert3Z[distance & 0xFFF] + 16;
            default: return 31 - hilbert3X[distance & 0xFFF];
        }
    }

    public static void main(String[] args) {
        init3D();
        int prevX = 0, nextX = 0;
        for (int i = 0; i < 0x4000; i++) {
            if(Math.abs((prevX) - (nextX = getPealbertX(i))) > 1)
                System.out.println("X PROBLEM AT " + prevX + " into " + nextX + " with distance " + i);
            prevX = nextX;
        }
    }
}
