package colorweaver.tools;

/**
 * Created by Tommy Ettinger on 8/6/2019.
 */
public class TrigTools {

    /**
     * A fairly-close approximation of {@link Math#sin(double)} that can be significantly faster (between 8x and 80x
     * faster sin() calls in benchmarking; if you have access to libGDX you should consider its sometimes-more-precise
     * and sometimes-faster MathUtils.sin() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes the same arguments Math.sin() does, so one angle in radians,
     * which may technically be any double (but this will lose precision on fairly large doubles, such as those that are
     * larger than {@link Long#MAX_VALUE}, because those doubles themselves will lose precision at that scale). This
     * is closely related to a cubic sway, but the shape of the output when graphed is almost identical to
     * sin(). The difference between the result of this method and {@link Math#sin(double)} should be under 0.0011 at
     * all points between -pi and pi, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * The error for this double version is extremely close to the float version, {@link #sin(float)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param radians an angle in radians as a double, often from 0 to pi * 2, though not required to be.
     * @return the sine of the given angle, as a double between -1.0 and 1.0 (both inclusive)
     */

    public static double sin(double radians)
    {
        radians *= 0.6366197723675814;
        final long floor = (radians >= 0.0 ? (long) radians : (long) radians - 1L) & -2L;
        radians -= floor;
        radians *= 2.0 - radians;
        return radians * (-0.775 - 0.225 * radians) * ((floor & 2L) - 1L);
    }

    /**
     * A fairly-close approximation of {@link Math#cos(double)} that can be significantly faster (between 8x and 80x
     * faster cos() calls in benchmarking; if you have access to libGDX you should consider its sometimes-more-precise
     * and sometimes-faster MathUtils.cos() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes the same arguments Math.cos() does, so one angle in radians,
     * which may technically be any double (but this will lose precision on fairly large doubles, such as those that are
     * larger than {@link Long#MAX_VALUE}, because those doubles themselves will lose precision at that scale). This
     * is closely related to a cubic sway, but the shape of the output when graphed is almost identical to
     * cos(). The difference between the result of this method and {@link Math#cos(double)} should be under 0.0011 at
     * all points between -pi and pi, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * The error for this double version is extremely close to the float version, {@link #cos(float)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param radians an angle in radians as a double, often from 0 to pi * 2, though not required to be.
     * @return the cosine of the given angle, as a double between -1.0 and 1.0 (both inclusive)
     */
    public static double cos(double radians)
    {
        radians = radians * 0.6366197723675814 + 1.0;
        final long floor = (radians >= 0.0 ? (long) radians : (long) radians - 1L) & -2L;
        radians -= floor;
        radians *= 2.0 - radians;
        return radians * (-0.775 - 0.225 * radians) * ((floor & 2L) - 1L);
    }

    /**
     * A fairly-close approximation of {@link Math#sin(double)} that can be significantly faster (between 8x and 80x
     * faster sin() calls in benchmarking, and both takes and returns floats; if you have access to libGDX you should
     * consider its more-precise and sometimes-faster MathUtils.sin() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes the same arguments Math.sin() does, so one angle in radians,
     * which may technically be any float (but this will lose precision on fairly large floats, such as those that are
     * larger than {@link Integer#MAX_VALUE}, because those floats themselves will lose precision at that scale). This
     * is closely related to a cubic sway, but the shape of the output when graphed is almost identical to
     * sin(). The difference between the result of this method and {@link Math#sin(double)} should be under 0.0011 at
     * all points between -pi and pi, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * The error for this float version is extremely close to the double version, {@link #sin(double)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any float to the valid input range.
     * @param radians an angle in radians as a float, often from 0 to pi * 2, though not required to be.
     * @return the sine of the given angle, as a float between -1f and 1f (both inclusive)
     */
    public static float sin(float radians)
    {
        radians *= 0.6366197723675814f;
        final int floor = (radians >= 0.0 ? (int) radians : (int) radians - 1) & -2;
        radians -= floor;
        radians *= 2f - radians;
        return radians * (-0.775f - 0.225f * radians) * ((floor & 2) - 1);
    }

    /**
     * A fairly-close approximation of {@link Math#cos(double)} that can be significantly faster (between 8x and 80x
     * faster cos() calls in benchmarking, and both takes and returns floats; if you have access to libGDX you should
     * consider its more-precise and sometimes-faster MathUtils.cos() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes the same arguments Math.cos() does, so one angle in radians,
     * which may technically be any float (but this will lose precision on fairly large floats, such as those that are
     * larger than {@link Integer#MAX_VALUE}, because those floats themselves will lose precision at that scale). This
     * is closely related to a cubic sway, but the shape of the output when graphed is almost identical to
     * cos(). The difference between the result of this method and {@link Math#cos(double)} should be under 0.0011 at
     * all points between -pi and pi, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * The error for this float version is extremely close to the double version, {@link #cos(double)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any float to the valid input range.
     * @param radians an angle in radians as a float, often from 0 to pi * 2, though not required to be.
     * @return the cosine of the given angle, as a float between -1f and 1f (both inclusive)
     */
    public static float cos(float radians)
    {
        radians = radians * 0.6366197723675814f + 1f;
        final int floor = (radians >= 0.0 ? (int) radians : (int) radians - 1) & -2;
        radians -= floor;
        radians *= 2f - radians;
        return radians * (-0.775f - 0.225f * radians) * ((floor & 2) - 1);
    }
    /**
     * A fairly-close approximation of {@link Math#sin(double)} that can be significantly faster (between 8x and 80x
     * faster sin() calls in benchmarking, and both takes and returns floats; if you have access to libGDX, you should
     * consider its more-precise and sometimes-faster MathUtils.sinDeg() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes one angle in degrees,
     * which may technically be any float (but this will lose precision on fairly large floats, such as those that are
     * larger than {@link Integer#MAX_VALUE}, because those floats themselves will lose precision at that scale). This
     * is closely related to a cubic sway, but the shape of the output when graphed is almost identical to
     * sin(). The difference between the result of this method and {@link Math#sin(double)} should be under 0.0011 at
     * all points between -360 and 360, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * The error for this float version is extremely close to the double version, {@link #sin(double)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any float to the valid input range.
     * @param degrees an angle in degrees as a float, often from 0 to 360, though not required to be.
     * @return the sine of the given angle, as a float between -1f and 1f (both inclusive)
     */
    public static float sinDegrees(float degrees)
    {
        degrees = degrees * 0.011111111111111112f;
        final int floor = (degrees >= 0.0 ? (int) degrees : (int) degrees - 1) & -2;
        degrees -= floor;
        degrees *= 2f - degrees;
        return degrees * (-0.775f - 0.225f * degrees) * ((floor & 2) - 1);
    }

    /**
     * A fairly-close approximation of {@link Math#cos(double)} that can be significantly faster (between 8x and 80x
     * faster cos() calls in benchmarking, and both takes and returns floats; if you have access to libGDX, you should
     * consider its more-precise and sometimes-faster MathUtils.cosDeg() method. Because this method doesn't rely on a
     * lookup table, where libGDX's MathUtils does, applications that have a bottleneck on memory may perform better
     * with this method than with MathUtils. Takes one angle in degrees,
     * which may technically be any float (but this will lose precision on fairly large floats, such as those that are
     * larger than {@link Integer#MAX_VALUE}, because those floats themselves will lose precision at that scale). This
     * is closely related to a cubic sway, but the shape of the output when graphed is almost identical to
     * cos(). The difference between the result of this method and {@link Math#cos(double)} should be under 0.0011 at
     * all points between -360 and 360, with an average difference of about 0.0005; not all points have been checked for
     * potentially higher errors, though.
     * <br>
     * The error for this float version is extremely close to the double version, {@link #cos(double)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * Unlike in previous versions of this method, the sign of the input doesn't affect performance here, at least not
     * by a measurable amount.
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any float to the valid input range.
     * @param degrees an angle in degrees as a float, often from 0 to pi * 2, though not required to be.
     * @return the cosine of the given angle, as a float between -1f and 1f (both inclusive)
     */
    public static float cosDegrees(float degrees)
    {
        degrees = degrees * 0.011111111111111112f + 1f;
        final int floor = (degrees >= 0.0 ? (int) degrees : (int) degrees - 1) & -2;
        degrees -= floor;
        degrees *= 2f - degrees;
        return degrees * (-0.775f - 0.225f * degrees) * ((floor & 2) - 1);
    }

    /**
     * A variation on {@link Math#sin(double)} that takes its input as a fraction of a turn instead of in radians; one
     * turn is equal to 360 degrees or two*PI radians. This can be useful as a building block for other measurements;
     * to make a sine method that takes its input in grad (with 400 grad equal to 360 degrees), you would just divide
     * the grad value by 400.0 (or multiply it by 0.0025) and pass it to this method. Similarly for binary degrees, also
     * called brad (with 256 brad equal to 360 degrees), you would divide by 256.0 or multiply by 0.00390625 before
     * passing that value here. The brad case is especially useful because you can use a byte for any brad values, and
     * adding up those brad values will wrap correctly (256 brad goes back to 0) while keeping perfect precision for the
     * results (you still divide by 256.0 when you pass the brad value to this method).
     * <br>
     * The error for this double version is extremely close to the float version, {@link #sin_(float)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param turns an angle as a fraction of a turn as a double, with 0.5 here equivalent to PI radians in {@link #cos(double)}
     * @return the sine of the given angle, as a double between -1.0 and 1.0 (both inclusive)
     */
    public static double sin_(double turns)
    {
        turns *= 4.0;
        final long floor = (turns >= 0.0 ? (long) turns : (long) turns - 1L) & -2L;
        turns -= floor;
        turns *= 2.0 - turns;
        return turns * (-0.775 - 0.225 * turns) * ((floor & 2L) - 1L);
    }

    /**
     * A variation on {@link Math#cos(double)} that takes its input as a fraction of a turn instead of in radians; one
     * turn is equal to 360 degrees or two*PI radians. This can be useful as a building block for other measurements;
     * to make a cosine method that takes its input in grad (with 400 grad equal to 360 degrees), you would just divide
     * the grad value by 400.0 (or multiply it by 0.0025) and pass it to this method. Similarly for binary degrees, also
     * called brad (with 256 brad equal to 360 degrees), you would divide by 256.0 or multiply by 0.00390625 before
     * passing that value here. The brad case is especially useful because you can use a byte for any brad values, and
     * adding up those brad values will wrap correctly (256 brad goes back to 0) while keeping perfect precision for the
     * results (you still divide by 256.0 when you pass the brad value to this method).
     * <br>
     * The error for this double version is extremely close to the float version, {@link #cos_(float)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param turns an angle as a fraction of a turn as a double, with 0.5 here equivalent to PI radians in {@link #cos(double)}
     * @return the cosine of the given angle, as a double between -1.0 and 1.0 (both inclusive)
     */
    public static double cos_(double turns)
    {
        turns = turns * 4.0 + 1.0;
        final long floor = (turns >= 0.0 ? (long) turns : (long) turns - 1L) & -2L;
        turns -= floor;
        turns *= 2.0 - turns;
        return turns * (-0.775 - 0.225 * turns) * ((floor & 2L) - 1L);
    }

    /**
     * A variation on {@link Math#sin(double)} that takes its input as a fraction of a turn instead of in radians (it
     * also takes and returns a float); one turn is equal to 360 degrees or two*PI radians. This can be useful as a
     * building block for other measurements; to make a sine method that takes its input in grad (with 400 grad equal to
     * 360 degrees), you would just divide the grad value by 400.0 (or multiply it by 0.0025) and pass it to this
     * method. Similarly for binary degrees, also called brad (with 256 brad equal to 360 degrees), you would divide by
     * 256.0 or multiply by 0.00390625 before passing that value here. The brad case is especially useful because you
     * can use a byte for any brad values, and adding up those brad values will wrap correctly (256 brad goes back to 0)
     * while keeping perfect precision for the results (you still divide by 256.0 when you pass the brad value to this
     * method).
     * <br>
     * The error for this float version is extremely close to the double version, {@link #sin_(double)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * The technique for sine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param turns an angle as a fraction of a turn as a float, with 0.5 here equivalent to PI radians in {@link #cos(double)}
     * @return the sine of the given angle, as a float between -1.0 and 1.0 (both inclusive)
     */
    public static float sin_(float turns)
    {
        turns *= 4f;
        final long floor = (turns >= 0.0 ? (long) turns : (long) turns - 1L) & -2L;
        turns -= floor;
        turns *= 2f - turns;
        return turns * (-0.775f - 0.225f * turns) * ((floor & 2L) - 1L);
    }

    /**
     * A variation on {@link Math#cos(double)} that takes its input as a fraction of a turn instead of in radians (it
     * also takes and returns a float); one turn is equal to 360 degrees or two*PI radians. This can be useful as a
     * building block for other measurements; to make a cosine method that takes its input in grad (with 400 grad equal
     * to 360 degrees), you would just divide the grad value by 400.0 (or multiply it by 0.0025) and pass it to this
     * method. Similarly for binary degrees, also called brad (with 256 brad equal to 360 degrees), you would divide by
     * 256.0 or multiply by 0.00390625 before passing that value here. The brad case is especially useful because you
     * can use a byte for any brad values, and adding up those brad values will wrap correctly (256 brad goes back to 0)
     * while keeping perfect precision for the results (you still divide by 256.0 when you pass the brad value to this
     * method).
     * <br>
     * The error for this float version is extremely close to the float version, {@link #cos_(double)}, so you should
     * choose based on what type you have as input and/or want to return rather than on quality concerns. Coercion
     * between float and double takes about as long as this method normally takes to run (or longer), so if you have
     * floats you should usually use methods that take floats (or return floats, if assigning the result to a float),
     * and likewise for doubles.
     * <br>
     * The technique for cosine approximation is mostly from
     * <a href="https://web.archive.org/web/20080228213915/http://devmaster.net/forums/showthread.php?t=5784">this archived DevMaster thread</a>,
     * with credit to "Nick". Changes have been made to accelerate wrapping from any double to the valid input range.
     * @param turns an angle as a fraction of a turn as a float, with 0.5 here equivalent to PI radians in {@link #cos(double)}
     * @return the cosine of the given angle, as a float between -1.0 and 1.0 (both inclusive)
     */
    public static float cos_(float turns)
    {
        turns = turns * 4f + 1f;
        final long floor = (turns >= 0.0 ? (long) turns : (long) turns - 1L) & -2L;
        turns -= floor;
        turns *= 2f - turns;
        return turns * (-0.775f - 0.225f * turns) * ((floor & 2L) - 1L);
    }

    /**
     * Close approximation of the frequently-used trigonometric method atan2, with higher precision than LibGDX's atan2
     * approximation. Maximum error is below 0.001 radians.
     * Takes y and x (in that unusual order) as doubles, and returns the angle from the origin to that point in radians.
     * It is about 5 times faster than {@link Math#atan2(double, double)} (roughly 17 ns instead of roughly 88 ns for
     * Math, though the computer was under some load during testing). It is almost identical in speed to LibGDX'
     * MathUtils approximation of the same method; MathUtils seems to have worse average error, though.
     * Credit to StackExchange user njuffa, who gave
     * <a href="https://math.stackexchange.com/a/1105038">this useful answer</a>. This method changed from an earlier
     * technique that was twice as fast but had very poor quality, enough to be visually noticeable. See also
     * {@link #atan2_(double, double)} if you don't want a mess converting to degrees or some other measurement, since
     * that method returns an angle from 0.0 (equal to 0 degrees) to 1.0 (equal to 360 degrees).
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, in radians as a double
     */
    public static double atan2(final double y, final double x)
    {
        /*
a := min (|x|, |y|) / max (|x|, |y|)
s := a * a
r := ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a
if |y| > |x| then r := 1.57079637 - r
if x < 0 then r := 3.14159274 - r
if y < 0 then r := -r
         */
        if(y == 0.0 && x >= 0.0) return 0.0;
        final double ax = Math.abs(x), ay = Math.abs(y);
        if(ax < ay)
        {
            final double a = ax / ay, s = a * a,
                    r = 1.57079637 - (((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a);
            return (x < 0.0) ? (y < 0.0) ? -3.14159274 + r : 3.14159274 - r : (y < 0.0) ? -r : r;
        }
        else {
            final double a = ay / ax, s = a * a,
                    r = (((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a);
            return (x < 0.0) ? (y < 0.0) ? -3.14159274 + r : 3.14159274 - r : (y < 0.0) ? -r : r;
        }
    }

    /**
     * Close approximation of the frequently-used trigonometric method atan2, with higher precision than LibGDX's atan2
     * approximation. Maximum error is below 0.001 radians.
     * Takes y and x (in that unusual order) as floats, and returns the angle from the origin to that point in radians.
     * It is about 5 times faster than {@link Math#atan2(double, double)} (roughly 17 ns instead of roughly 88 ns for
     * Math, though the computer was under some load during testing). It is almost identical in speed to LibGDX'
     * MathUtils approximation of the same method; MathUtils seems to have worse average error, though.
     * Credit to StackExchange user njuffa, who gave
     * <a href="https://math.stackexchange.com/a/1105038">this useful answer</a>. This method changed from an earlier
     * technique that was twice as fast but had very poor quality, enough to be visually noticeable. See also
     * {@link #atan2_(float, float)} if you don't want a mess converting to degrees or some other measurement, since
     * that method returns an angle from 0f (equal to 0 degrees) to 1f (equal to 360 degrees).
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, in radians as a float
     */
    public static float atan2(final float y, final float x)
    {
        /*
a := min (|x|, |y|) / max (|x|, |y|)
s := a * a
r := ((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a
if |y| > |x| then r := 1.57079637 - r
if x < 0 then r := 3.14159274 - r
if y < 0 then r := -r
         */
        if(y == 0f && x >= 0f) return 0f;
        final float ax = Math.abs(x), ay = Math.abs(y);
        if(ax < ay)
        {
            final float a = ax / ay, s = a * a,
                    r = 1.57079637f - (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a);
            return (x < 0f) ? (y < 0f) ? -3.14159274f + r : 3.14159274f - r : (y < 0f) ? -r : r;
        }
        else {
            final float a = ay / ax, s = a * a,
                    r = (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a);
            return (x < 0f) ? (y < 0f) ? -3.14159274f + r : 3.14159274f - r : (y < 0f) ? -r : r;
        }
    }

    /**
     * Altered-range approximation of the frequently-used trigonometric method atan2, taking y and x positions as 
     * doubles and returning an angle measured in turns from 0.0 to 1.0 (inclusive), with one cycle over the range
     * equivalent to 360 degrees or 2PI radians. You can multiply the angle by {@code 6.2831855f} to change to radians,
     * or by {@code 360f} to change to degrees. Takes y and x (in that unusual order) as doubles. Will never return a
     * negative number, which may help avoid costly floating-point modulus when you actually want a positive number.
     * Credit to StackExchange user njuffa, who gave
     * <a href="https://math.stackexchange.com/a/1105038">this useful answer</a>. Note that
     * {@link #atan2(double, double)} returns an angle in radians and can return negative results, which may be fine for
     * many tasks; these two methods are extremely close in implementation and speed.
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, as a double from 0.0 to 1.0, inclusive
     */
    public static double atan2_(final double y, final double x)
    {
        if(y == 0.0 && x >= 0.0) return 0.0;
        final double ax = Math.abs(x), ay = Math.abs(y);
        if(ax < ay)
        {
            final double a = ax / ay, s = a * a,
                    r = 0.25 - (((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a) * 0.15915494309189535;
            return (x < 0.0) ? (y < 0.0) ? 0.5 + r : 0.5 - r : (y < 0.0) ? 1.0 - r : r;
        }
        else {
            final double a = ay / ax, s = a * a,
                    r = (((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a) * 0.15915494309189535;
            return (x < 0.0) ? (y < 0.0) ? 0.5 + r : 0.5 - r : (y < 0.0) ? 1.0 - r : r;
        }
    }
    /**
     * Altered-range approximation of the frequently-used trigonometric method atan2, taking y and x positions as floats
     * and returning an angle measured in turns from 0.0f to 1.0f, with one cycle over the range equivalent to 360
     * degrees or 2PI radians. You can multiply the angle by {@code 6.2831855f} to change to radians, or by {@code 360f}
     * to change to degrees. Takes y and x (in that unusual order) as floats. Will never return a negative number, which
     * may help avoid costly floating-point modulus when you actually want a positive number.
     * Credit to StackExchange user njuffa, who gave
     * <a href="https://math.stackexchange.com/a/1105038">this useful answer</a>. Note that
     * {@link #atan2(float, float)} returns an angle in radians and can return negative results, which may be fine for
     * many tasks; these two methods are extremely close in implementation and speed.
     * @param y y-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @param x x-component of the point to find the angle towards; note the parameter order is unusual by convention
     * @return the angle to the given point, as a float from 0.0f to 1.0f, inclusive
     */
    public static float atan2_(final float y, final float x)
    {
        if(y == 0.0 && x >= 0.0) return 0f;
        final float ax = Math.abs(x), ay = Math.abs(y);
        if(ax < ay)
        {
            final float a = ax / ay, s = a * a,
                    r = 0.25f - (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a) * 0.15915494309189535f;
            return (x < 0.0f) ? (y < 0.0f) ? 0.5f + r : 0.5f - r : (y < 0.0f) ? 1f - r : r;
        }
        else {
            final float a = ay / ax, s = a * a,
                    r = (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a) * 0.15915494309189535f;
            return (x < 0.0f) ? (y < 0.0f) ? 0.5f + r : 0.5f - r : (y < 0.0f) ? 1f - r : r;
        }
    }

    /**
     * Arc sine approximation with fairly low error while still being faster than {@link #sin(double)}.
     * This formula is number 201 in <a href=">http://www.fastcode.dk/fastcodeproject/articles/index.htm">Dennis
     * Kjaer Christensen's unfinished math work on arc sine approximation</a>. This method is about 40 times faster
     * than {@link Math#asin(double)}.
     * @param a an input to the inverse sine function, from -1 to 1 inclusive (error is higher approaching -1 or 1)
     * @return an output from the inverse sine function, from -PI/2 to PI/2 inclusive.
     */
    public static double asin(double a) {
        return (a * (1.0 + (a *= a) * (-0.141514171442891431 + a * -0.719110791477959357))) /
                (1.0 + a * (-0.439110389941411144 + a * -0.471306172023844527));
    }
    /**
     * Arc sine approximation with fairly low error while still being faster than {@link #sin(float)}.
     * This formula is number 201 in <a href=">http://www.fastcode.dk/fastcodeproject/articles/index.htm">Dennis
     * Kjaer Christensen's unfinished math work on arc sine approximation</a>. This method is about 40 times faster
     * than {@link Math#asin(double)}, and takes and returns a float.
     * @param a an input to the inverse sine function, from -1 to 1 inclusive (error is higher approaching -1 or 1)
     * @return an output from the inverse sine function, from -PI/2 to PI/2 inclusive.
     */
    public static float asin(float a) {
        return (a * (1f + (a *= a) * (-0.141514171442891431f + a * -0.719110791477959357f))) /
                (1f + a * (-0.439110389941411144f + a * -0.471306172023844527f));
    }
    /**
     * Arc cosine approximation with fairly low error while still being faster than {@link #cos(double)}.
     * This formula is number 201 in <a href=">http://www.fastcode.dk/fastcodeproject/articles/index.htm">Dennis
     * Kjaer Christensen's unfinished math work on arc sine approximation</a>, with a basic change to go from arc sine
     * to arc cosine. This method is faster than {@link Math#acos(double)}.
     * @param a an input to the inverse cosine function, from -1 to 1 inclusive (error is higher approaching -1 or 1)
     * @return an output from the inverse cosine function, from 0 to PI inclusive.
     */
    public static double acos(double a) {
        return 1.5707963267948966 - (a * (1.0 + (a *= a) * (-0.141514171442891431 + a * -0.719110791477959357))) /
                (1.0 + a * (-0.439110389941411144 + a * -0.471306172023844527));
    }
    /**
     * Arc cosine approximation with fairly low error while still being faster than {@link #cos(float)}.
     * This formula is number 201 in <a href=">http://www.fastcode.dk/fastcodeproject/articles/index.htm">Dennis
     * Kjaer Christensen's unfinished math work on arc sine approximation</a>, with a basic change to go from arc sine
     * to arc cosine. This method is faster than {@link Math#acos(double)}, and takes and returns a float.
     * @param a an input to the inverse cosine function, from -1 to 1 inclusive (error is higher approaching -1 or 1)
     * @return an output from the inverse cosine function, from 0 to PI inclusive.
     */
    public static float acos(float a) {
        return 1.5707963267948966f - (a * (1f + (a *= a) * (-0.141514171442891431f + a * -0.719110791477959357f))) /
                (1f + a * (-0.439110389941411144f + a * -0.471306172023844527f));
    }

    /**
     * Inverse sine function (arcsine) but with output measured in turns instead of radians. Possible results for this
     * range from 0.75 (inclusive) to 1.0 (exclusive), and continuing past that to 0.0 (inclusive) to 0.25 (inclusive).
     * <br>
     * This method is much more precise than the non-turn approximation, but is about 3x slower.
     * @param n a double from -1.0 to 1.0 (both inclusive), usually the output of sin() or cos()
     * @return one of the values that would produce {@code n} if it were passed to {@link #sin_(double)}
     */
    public static double asin_(final double n)
    {
        if(n == 0.0) return 0.0;
        final double ax = Math.sqrt(1.0 - n * n), ay = Math.abs(n);
        if(ax < ay)
        {
            final double a = ax / ay, s = a * a,
                    r = 0.25 - (((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a) * 0.15915494309189535;
            return (n < 0.0) ? 1.0 - r : r;
        }
        else {
            final double a = ay / ax, s = a * a,
                    r = (((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a) * 0.15915494309189535;
            return (n < 0.0) ? 1.0 - r : r;
        }
    }
    /**
     * Inverse cosine function (arccos) but with output measured in turns instead of radians. Possible results for this
     * range from 0.0 (inclusive) to 0.5 (inclusive).
     * <br>
     * This method is much more precise than the non-turn approximation, but is about 3x slower.
     * @param n a double from -1.0 to 1.0 (both inclusive), usually the output of sin() or cos()
     * @return one of the values that would produce {@code n} if it were passed to {@link #cos_(double)}
     */
    public static double acos_(final double n)
    {
        if(n == 1.0 || n == -1.0) return 0.0;
        final double ax = Math.abs(n), ay = Math.sqrt((1.0 + n) * (1.0 - n));
        if(ax < ay)
        {
            final double a = ax / ay, s = a * a,
                    r = 0.25 - (((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a) * 0.15915494309189535;
            return (n < 0.0) ? 0.5 - r : r;
        }
        else {
            final double a = ax / ay, s = a * a,
                    r = 0.25 - (((-0.0464964749 * s + 0.15931422) * s - 0.327622764) * s * a + a) * 0.15915494309189535;
            return (n < 0.0) ? 0.5 - r : r;
        }
    }


    /**
     * Inverse sine function (arcsine) but with output measured in turns instead of radians. Possible results for this
     * range from 0.75f (inclusive) to 1.0f (exclusive), and continuing past that to 0.0f (inclusive) to 0.25f
     * (inclusive).
     * <br>
     * This method is much more precise than the non-turn approximation, but is about 3x slower.
     * @param n a float from -1.0f to 1.0f (both inclusive), usually the output of sin() or cos()
     * @return one of the values that would produce {@code n} if it were passed to {@link #sin_(float)}
     */
    public static float asin_(final float n)
    {
        if(n == 0.0f) return 0.0f;
        final float ax = (float) Math.sqrt(1f - n * n), ay = Math.abs(n);
        if(ax < ay)
        {
            final float a = ax / ay, s = a * a,
                    r = 0.25f - (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a) * 0.15915494309189535f;
            return (n < 0.0f) ? 1.0f - r : r;
        }
        else {
            final float a = ay / ax, s = a * a,
                    r = (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a) * 0.15915494309189535f;
            return (n < 0.0f) ? 1.0f - r : r;
        }
    }
    /**
     * Inverse cosine function (arccos) but with output measured in turns instead of radians. Possible results for this
     * range from 0.0f (inclusive) to 0.5f (inclusive).
     * <br>
     * This method is much more precise than the non-turn approximation, but is about 3x slower.
     * @param n a float from -1.0f to 1.0f (both inclusive), usually the output of sin() or cos()
     * @return one of the values that would produce {@code n} if it were passed to {@link #cos_(float)}
     */
    public static float acos_(final float n)
    {
        if(n == 1.0f || n == -1.0f) return 0.0f;
        final float ax = Math.abs(n), ay = (float) Math.sqrt((1f + n) * (1f - n));
        if(ax < ay)
        {
            final float a = ax / ay, s = a * a,
                    r = 0.25f - (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a) * 0.15915494309189535f;
            return (n < 0.0f) ? 0.5f - r : r;
        }
        else {
            final float a = ax / ay, s = a * a,
                    r = (((-0.0464964749f * s + 0.15931422f) * s - 0.327622764f) * s * a + a) * 0.15915494309189535f;
            return (n < 0.0f) ? 0.5f - r : r;
        }
    }
}
