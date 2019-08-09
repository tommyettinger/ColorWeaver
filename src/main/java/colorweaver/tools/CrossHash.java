package colorweaver.tools;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.badlogic.gdx.utils.NumberUtils.doubleToLongBits;
import static com.badlogic.gdx.utils.NumberUtils.floatToIntBits;

/**
 * 32-bit and 64-bit hash code functions for arrays and some other types, with cross-platform equivalent results.
 * A fairly fast hashing algorithm in general, Water performs especially well on large arrays, and passes SMHasher's
 * newest and most stringent version of tests. The int-hashing {@link #hash(int[])} method is faster than
 * {@link Arrays#hashCode(int[])}, and all hashes are higher-quality than Arrays.hashCode(). Based on
 * <a href="https://github.com/wangyi-fudan/wyhash">wyhash</a>, specifically
 * <a href="https://github.com/tommyettinger/waterhash">the waterhash variant</a>. This version passes SMHasher for
 * both the 32-bit output hash() methods and the 64-bit output hash64() methods (which use the slightly tweaked
 * wheathash variant in the waterhash Git repo, or woothash for hashing long arrays). While an earlier version
 * passed rurban/smhasher, it failed demerphq/smhasher (Yves' more stringent fork), so some minor tweaks allowed the
 * latest code to pass Yves' test. Uses 64-bit math, so it won't be as fast on GWT. Currently, the methods that hash
 * types other than int arrays aren't as fast as the int array hash, but they are usually fast enough and pass SMHasher.
 * <br>
 * These hash functions are so fast because they operate in bulk on 4 items at a time, such as 4 ints (which is the
 * optimal case), 4 bytes, or 4 longs (which uses a different algorithm). This bulk operation usually entails 3
 * multiplications and some other, cheaper operations per 4 items hashed. For long arrays, it requires many more
 * multiplications, but modern CPUs can pipeline the operations on unrelated longs to run in parallel on one core.
 * If any items are left over after the bulk segment, Water uses the least effort possible to hash the remaining 1,
 * 2, or 3 items left. Most of these operations use the method {@link #mum(long, long)}, which helps take two inputs
 * and multiply them once, getting a more-random result after another small step. The long array code uses
 * {@link #wow(long, long)} (similar to mum upside-down), which mixes up its arguments with each other before
 * multplying. It finishes with either code similar to mum() for 32-bit output hash() methods, or a somewhat more
 * rigorous method for 64-bit output hash64() methods (still similar to mum).
 */
@SuppressWarnings("NumericOverflow")
public final class CrossHash {
    /**
     * Big constant 0.
     */
    public static final long b0 = 0xA0761D6478BD642FL;
    /**
     * Big constant 1.
     */
    public static final long b1 = 0xE7037ED1A0B428DBL;
    /**
     * Big constant 2.
     */
    public static final long b2 = 0x8EBC6AF09C88C6E3L;
    /**
     * Big constant 3.
     */
    public static final long b3 = 0x589965CC75374CC3L;
    /**
     * Big constant 4.
     */
    public static final long b4 = 0x1D8E4E27C47D124FL;
    /**
     * Big constant 5.
     */
    public static final long b5 = 0xEB44ACCAB455D165L;

    /**
     * Takes two arguments that are technically longs, and should be very different, and uses them to get a result
     * that is technically a long and mixes the bits of the inputs. The arguments and result are only technically
     * longs because their lower 32 bits matter much more than their upper 32, and giving just any long won't work.
     * <br>
     * This is very similar to wyhash's mum function, but doesn't use 128-bit math because it expects that its
     * arguments are only relevant in their lower 32 bits (allowing their product to fit in 64 bits).
     * @param a a long that should probably only hold an int's worth of data
     * @param b a long that should probably only hold an int's worth of data
     * @return a sort-of randomized output dependent on both inputs
     */
    public static long mum(final long a, final long b) {
        final long n = a * b;
        return n - (n >>> 32);
    }

    /**
     * A slower but higher-quality variant on {@link #mum(long, long)} that can take two arbitrary longs (with any
     * of their 64 bits containing relevant data) instead of mum's 32-bit sections of its inputs, and outputs a
     * 64-bit result that can have any of its bits used.
     * <br>
     * This was changed so it distributes bits from both inputs a little better on July 6, 2019.
     * @param a any long
     * @param b any long
     * @return a sort-of randomized output dependent on both inputs
     */
    public static long wow(final long a, final long b) {
        final long n = (a ^ (b << 39 | b >>> 25)) * (b ^ (a << 39 | a >>> 25));
        return n ^ (n >>> 32);
    }

    public static long hash64(final boolean[] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;//seed = b1 ^ b1 >>> 29 ^ b1 >>> 43 ^ b1 << 7 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum((data[i-3] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b1, (data[i-2] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b2) + seed,
                    mum((data[i-1] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b3, (data[i] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ (data[len-1] ? 0x9E37L : 0x7F4AL), b3 ^ (data[len-1]  ? 0x79B9L : 0x7C15L)); break;
            case 2: seed = mum(seed ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L), b0 ^ (data[len-1] ? 0x9E3779B9L : 0x7F4A7C15L)); break;
            case 3: seed = mum(seed ^ (data[len-3] ? 0x9E3779B9L : 0x7F4A7C15L), b2 ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L)) ^ mum(seed ^ (data[len-1] ? 0x9E3779B9 : 0x7F4A7C15), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }
    public static long hash64(final byte[] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b2, b1 ^ data[len-1]); break;
            case 2: seed = mum(seed ^ b3, data[len-2] ^ data[len-1] << 8 ^ b4); break;
            case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 8, b2 ^ data[len-1]); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final short[] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
            case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
            case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final char[] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
            case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
            case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final CharSequence data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length();
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                    mum(data.charAt(i-1) ^ b3, data.charAt(i  ) ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
            case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
            case 3: seed = mum(seed ^ data.charAt(len-3) ^ data.charAt(len-2) << 16, b1 ^ data.charAt(len-1)); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final int[] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ (data[len-1] >>> 16), b3 ^ (data[len-1] & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ data[len-2], b0 ^ data[len-1]); break;
            case 3: seed = mum(seed ^ data[len-3], b2 ^ data[len-2]) ^ mum(seed ^ data[len-1], b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }
    
    public static long hash64(final int[] data, final int length) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        for (int i = 3; i < length; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (length & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ (data[length-1] >>> 16), b3 ^ (data[length-1] & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ data[length-2], b0 ^ data[length-1]); break;
            case 3: seed = mum(seed ^ data[length-3], b2 ^ data[length-2]) ^ mum(seed ^ data[length-1], b4); break;
        }
        seed = (seed ^ seed << 16) * (length ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final long[] data) {
        if (data == null) return 0;
        long seed = 0x1E98AE18CA351B28L,// seed = b0 ^ b0 >>> 23 ^ b0 >>> 48 ^ b0 << 7 ^ b0 << 53, 
                a = seed ^ b4, b = (seed << 17 | seed >>> 47) ^ b3,
                c = (seed << 31 | seed >>> 33) ^ b2, d = (seed << 47 | seed >>> 17) ^ b1;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            a = (data[i-3] ^ a) * b1; a = (a << 23 | a >>> 41) * b3;
            b = (data[i-2] ^ b) * b2; b = (b << 25 | b >>> 39) * b4;
            c = (data[i-1] ^ c) * b3; c = (c << 29 | c >>> 35) * b5;
            d = (data[i  ] ^ d) * b4; d = (d << 31 | d >>> 33) * b1;
            seed += a + b + c + d;
        }
        seed += b5;
        switch (len & 3) {
            case 1: seed = wow(seed, b1 ^ data[len-1]); break;
            case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
            case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
        return seed - (seed >>> 31) + (seed << 33);
    }
    public static long hash64(final float[] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(floatToIntBits(data[i-3]) ^ b1, floatToIntBits(data[i-2]) ^ b2) + seed,
                    mum(floatToIntBits(data[i-1]) ^ b3, floatToIntBits(data[i]) ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ (floatToIntBits(data[len-1]) >>> 16), b3 ^ (floatToIntBits(data[len-1]) & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ floatToIntBits(data[len-2]), b0 ^ floatToIntBits(data[len-1])); break;
            case 3: seed = mum(seed ^ floatToIntBits(data[len-3]), b2 ^ floatToIntBits(data[len-2])) ^ mum(seed ^ floatToIntBits(data[len-1]), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }
    public static long hash64(final double[] data) {
        if (data == null) return 0;
        long seed = 0x1E98AE18CA351B28L,// seed = b0 ^ b0 >>> 23 ^ b0 >>> 48 ^ b0 << 7 ^ b0 << 53, 
                a = seed ^ b4, b = (seed << 17 | seed >>> 47) ^ b3,
                c = (seed << 31 | seed >>> 33) ^ b2, d = (seed << 47 | seed >>> 17) ^ b1;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            a = (doubleToLongBits(data[i-3]) ^ a) * b1; a = (a << 23 | a >>> 41) * b3;
            b = (doubleToLongBits(data[i-2]) ^ b) * b2; b = (b << 25 | b >>> 39) * b4;
            c = (doubleToLongBits(data[i-1]) ^ c) * b3; c = (c << 29 | c >>> 35) * b5;
            d = (doubleToLongBits(data[i  ]) ^ d) * b4; d = (d << 31 | d >>> 33) * b1;
            seed += a + b + c + d;
        }
        seed += b5;
        switch (len & 3) {
            case 1: seed = wow(seed, b1 ^ doubleToLongBits(data[len-1])); break;
            case 2: seed = wow(seed + doubleToLongBits(data[len-2]), b2 + doubleToLongBits(data[len-1])); break;
            case 3: seed = wow(seed + doubleToLongBits(data[len-3]), b2 + doubleToLongBits(data[len-2])) ^ wow(seed + doubleToLongBits(data[len-1]), seed ^ b3); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
        return seed - (seed >>> 31) + (seed << 33);
    }

    /**
     * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
     *
     * @param data  the char array to hash
     * @param start the start of the section to hash (inclusive)
     * @param end   the end of the section to hash (exclusive)
     * @return a 32-bit hash code for the requested section of data
     */
    public static long hash64(final char[] data, final int start, final int end) {
        if (data == null || start >= end)
            return 0;
        long seed = 9069147967908697017L;
        final int len = Math.min(end, data.length);
        for (int i = start + 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len - start & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
            case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
            case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
        }
        return (int) mum(seed ^ seed << 16, len - start ^ b0);
    }

    /**
     * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
     *
     * @param data  the String or other CharSequence to hash
     * @param start the start of the section to hash (inclusive)
     * @param end   the end of the section to hash (exclusive)
     * @return a 32-bit hash code for the requested section of data
     */
    public static long hash64(final CharSequence data, final int start, final int end) {
        if (data == null || start >= end)
            return 0;
        long seed = 9069147967908697017L;
        final int len = Math.min(end, data.length());
        for (int i = start + 3; i < len; i+=4) {
            seed = mum(
                    mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                    mum(data.charAt(i-1) ^ b3, data.charAt(i) ^ b4));
        }
        switch (len - start & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
            case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
            case 3: seed = mum(seed ^ data.charAt(len-3) ^ data.charAt(len-2) << 16, b1 ^ data.charAt(len-1)); break;
        }
        return (int) mum(seed ^ seed << 16, len - start ^ b0);
    }


    public static long hash64(final char[][] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final int[][] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final long[][] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final CharSequence[] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final CharSequence[]... data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final Iterable<? extends CharSequence> data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final Iterator<? extends CharSequence> it = data.iterator();
        int len = 0;
        while (it.hasNext())
        {
            ++len;
            seed = mum(
                    mum(hash(it.next()) ^ b1, (it.hasNext() ? hash(it.next()) ^ b2 ^ ++len : b2)) + seed,
                    mum((it.hasNext() ? hash(it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(it.next()) ^ b4 ^ ++len : b4)));
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final List<? extends CharSequence> data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.size();
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data.get(i-3)) ^ b1, hash(data.get(i-2)) ^ b2) + seed,
                    mum(hash(data.get(i-1)) ^ b3, hash(data.get(i  )) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data.get(len-1))) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data.get(len-2)), b0 ^ hash(data.get(len-1))); break;
            case 3: seed = mum(seed ^ hash(data.get(len-3)), b2 ^ hash(data.get(len-2))) ^ mum(seed ^ hash(data.get(len-1)), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);

    }

    public static long hash64(final Object[] data) {
        if (data == null) return 0;
        long seed = 9069147967908697017L;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0);
        return seed - (seed >>> 31) + (seed << 33);
    }

    public static long hash64(final Object data) {
        if (data == null)
            return 0;
        final long h = data.hashCode() * 0x9E3779B97F4A7C15L;
        return h - (h >>> 31) + (h << 33);
    }


    public static int hash(final boolean[] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum((data[i-3] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b1, (data[i-2] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b2) + seed,
                    mum((data[i-1] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b3, (data[i] ? 0x9E3779B9L : 0x7F4A7C15L) ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ (data[len-1] ? 0x9E37L : 0x7F4AL), b3 ^ (data[len-1]  ? 0x79B9L : 0x7C15L)); break;
            case 2: seed = mum(seed ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L), b0 ^ (data[len-1] ? 0x9E3779B9L : 0x7F4A7C15L)); break;
            case 3: seed = mum(seed ^ (data[len-3] ? 0x9E3779B9L : 0x7F4A7C15L), b2 ^ (data[len-2] ? 0x9E3779B9L : 0x7F4A7C15L)) ^ mum(seed ^ (data[len-1] ? 0x9E3779B9 : 0x7F4A7C15), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    public static int hash(final byte[] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b2, b1 ^ data[len-1]); break;
            case 2: seed = mum(seed ^ b3, data[len-2] ^ data[len-1] << 8 ^ b4); break;
            case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 8, b2 ^ data[len-1]); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final short[] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
            case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
            case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final char[] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
            case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
            case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final CharSequence data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length();
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                    mum(data.charAt(i-1) ^ b3, data.charAt(i  ) ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
            case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
            case 3: seed = mum(seed ^ data.charAt(len-3) ^ data.charAt(len-2) << 16, b1 ^ data.charAt(len-1)); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    public static int hash(final int[] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ (data[len-1] >>> 16), b3 ^ (data[len-1] & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ data[len-2], b0 ^ data[len-1]); break;
            case 3: seed = mum(seed ^ data[len-3], b2 ^ data[len-2]) ^ mum(seed ^ data[len-1], b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    public static int hash(final int[] data, final int length) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        for (int i = 3; i < length; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (length & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ (data[length-1] >>> 16), b3 ^ (data[length-1] & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ data[length-2], b0 ^ data[length-1]); break;
            case 3: seed = mum(seed ^ data[length-3], b2 ^ data[length-2]) ^ mum(seed ^ data[length-1], b4); break;
        }
        return (int) mum(seed ^ seed << 16, length ^ b0);
    }
    
    public static int hash(final long[] data) {
        if (data == null) return 0;
        long seed = 0x1E98AE18CA351B28L,// seed = b0 ^ b0 >>> 23 ^ b0 >>> 48 ^ b0 << 7 ^ b0 << 53, 
                a = seed ^ b4, b = (seed << 17 | seed >>> 47) ^ b3,
                c = (seed << 31 | seed >>> 33) ^ b2, d = (seed << 47 | seed >>> 17) ^ b1;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            a = (data[i-3] ^ a) * b1; a = (a << 23 | a >>> 41) * b3;
            b = (data[i-2] ^ b) * b2; b = (b << 25 | b >>> 39) * b4;
            c = (data[i-1] ^ c) * b3; c = (c << 29 | c >>> 35) * b5;
            d = (data[i  ] ^ d) * b4; d = (d << 31 | d >>> 33) * b1;
            seed += a + b + c + d;
        }
        seed += b5;
        switch (len & 3) {
            case 1: seed = wow(seed, b1 ^ data[len-1]); break;
            case 2: seed = wow(seed + data[len-2], b2 + data[len-1]); break;
            case 3: seed = wow(seed + data[len-3], b2 + data[len-2]) ^ wow(seed + data[len-1], seed ^ b3); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
        return (int)(seed - (seed >>> 32));
    }

    public static int hash(final float[] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(floatToIntBits(data[i-3]) ^ b1, floatToIntBits(data[i-2]) ^ b2) + seed,
                    mum(floatToIntBits(data[i-1]) ^ b3, floatToIntBits(data[i]) ^ b4));
        }
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ (floatToIntBits(data[len-1]) >>> 16), b3 ^ (floatToIntBits(data[len-1]) & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ floatToIntBits(data[len-2]), b0 ^ floatToIntBits(data[len-1])); break;
            case 3: seed = mum(seed ^ floatToIntBits(data[len-3]), b2 ^ floatToIntBits(data[len-2])) ^ mum(seed ^ floatToIntBits(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    public static int hash(final double[] data) {
        if (data == null) return 0;
        long seed = 0x1E98AE18CA351B28L,// seed = b0 ^ b0 >>> 23 ^ b0 >>> 48 ^ b0 << 7 ^ b0 << 53, 
                a = seed ^ b4, b = (seed << 17 | seed >>> 47) ^ b3,
                c = (seed << 31 | seed >>> 33) ^ b2, d = (seed << 47 | seed >>> 17) ^ b1;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            a = (doubleToLongBits(data[i-3]) ^ a) * b1; a = (a << 23 | a >>> 41) * b3;
            b = (doubleToLongBits(data[i-2]) ^ b) * b2; b = (b << 25 | b >>> 39) * b4;
            c = (doubleToLongBits(data[i-1]) ^ c) * b3; c = (c << 29 | c >>> 35) * b5;
            d = (doubleToLongBits(data[i  ]) ^ d) * b4; d = (d << 31 | d >>> 33) * b1;
            seed += a + b + c + d;
        }
        seed += b5;
        switch (len & 3) {
            case 1: seed = wow(seed, b1 ^ doubleToLongBits(data[len-1])); break;
            case 2: seed = wow(seed + doubleToLongBits(data[len-2]), b2 + doubleToLongBits(data[len-1])); break;
            case 3: seed = wow(seed + doubleToLongBits(data[len-3]), b2 + doubleToLongBits(data[len-2])) ^ wow(seed + doubleToLongBits(data[len-1]), seed ^ b3); break;
        }
        seed = (seed ^ seed << 16) * (len ^ b0 ^ seed >>> 32);
        return (int)(seed - (seed >>> 32));
    }
    
    /**
     * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
     *
     * @param data  the char array to hash
     * @param start the start of the section to hash (inclusive)
     * @param end   the end of the section to hash (exclusive)
     * @return a 32-bit hash code for the requested section of data
     */
    public static int hash(final char[] data, final int start, final int end) {
        if (data == null || start >= end)
            return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = Math.min(end, data.length);
        for (int i = start + 3; i < len; i+=4) {
            seed = mum(
                    mum(data[i-3] ^ b1, data[i-2] ^ b2) + seed,
                    mum(data[i-1] ^ b3, data[i] ^ b4));
        }
        switch (len - start & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data[len-1]); break;
            case 2: seed = mum(seed ^ data[len-2], b3 ^ data[len-1]); break;
            case 3: seed = mum(seed ^ data[len-3] ^ data[len-2] << 16, b1 ^ data[len-1]); break;
        }
        return (int) mum(seed ^ seed << 16, len - start ^ b0);
    }
    
    /**
     * Hashes only a subsection of the given data, starting at start (inclusive) and ending before end (exclusive).
     *
     * @param data  the String or other CharSequence to hash
     * @param start the start of the section to hash (inclusive)
     * @param end   the end of the section to hash (exclusive)
     * @return a 32-bit hash code for the requested section of data
     */
    public static int hash(final CharSequence data, final int start, final int end) {
        if (data == null || start >= end)
            return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = Math.min(end, data.length());
        for (int i = start + 3; i < len; i+=4) {
            seed = mum(
                    mum(data.charAt(i-3) ^ b1, data.charAt(i-2) ^ b2) + seed,
                    mum(data.charAt(i-1) ^ b3, data.charAt(i) ^ b4));
        }
        switch (len - start & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^ b3, b4 ^ data.charAt(len-1)); break;
            case 2: seed = mum(seed ^ data.charAt(len-2), b3 ^ data.charAt(len-1)); break;
            case 3: seed = mum(seed ^ data.charAt(len-3) ^ data.charAt(len-2) << 16, b1 ^ data.charAt(len-1)); break;
        }
        return (int) mum(seed ^ seed << 16, len - start ^ b0);
    }
    
    
    public static int hash(final char[][] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final int[][] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final long[][] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final CharSequence[] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final CharSequence[]... data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }

    public static int hash(final Iterable<? extends CharSequence> data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final Iterator<? extends CharSequence> it = data.iterator();
        int len = 0;
        while (it.hasNext())
        {
            ++len;
            seed = mum(
                    mum(hash(it.next()) ^ b1, (it.hasNext() ? hash(it.next()) ^ b2 ^ ++len : b2)) + seed,
                    mum((it.hasNext() ? hash(it.next()) ^ b3 ^ ++len : b3), (it.hasNext() ? hash(it.next()) ^ b4 ^ ++len : b4)));
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }

    public static int hash(final List<? extends CharSequence> data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.size();
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data.get(i-3)) ^ b1, hash(data.get(i-2)) ^ b2) + seed,
                    mum(hash(data.get(i-1)) ^ b3, hash(data.get(i  )) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data.get(len-1))) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data.get(len-2)), b0 ^ hash(data.get(len-1))); break;
            case 3: seed = mum(seed ^ hash(data.get(len-3)), b2 ^ hash(data.get(len-2))) ^ mum(seed ^ hash(data.get(len-1)), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final Object[] data) {
        if (data == null) return 0;
        long seed = -260224914646652572L;//b1 ^ b1 >>> 41 ^ b1 << 53;
        final int len = data.length;
        for (int i = 3; i < len; i+=4) {
            seed = mum(
                    mum(hash(data[i-3]) ^ b1, hash(data[i-2]) ^ b2) + seed,
                    mum(hash(data[i-1]) ^ b3, hash(data[i  ]) ^ b4));
        }
        int t;
        switch (len & 3) {
            case 0: seed = mum(b1 ^ seed, b4 + seed); break;
            case 1: seed = mum(seed ^((t = hash(data[len-1])) >>> 16), b3 ^ (t & 0xFFFFL)); break;
            case 2: seed = mum(seed ^ hash(data[len-2]), b0 ^ hash(data[len-1])); break;
            case 3: seed = mum(seed ^ hash(data[len-3]), b2 ^ hash(data[len-2])) ^ mum(seed ^ hash(data[len-1]), b4); break;
        }
        return (int) mum(seed ^ seed << 16, len ^ b0);
    }
    
    public static int hash(final Object data) {
        if (data == null)
            return 0;
        final int h = data.hashCode() * 0x9E375;
        return h ^ (h >>> 16);
    }
}
