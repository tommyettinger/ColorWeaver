//port of https://github.com/mcychan/nQuant.j2se
//Apache 2.0 license
package colorweaver;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import static colorweaver.tools.TrigTools.*;

public class CIELABConverter {

	protected static double a1Prime, a2Prime, CPrime1, CPrime2, barCPrime, barhPrime;
	private CIELABConverter()
	{
	}

	public static class Lab {
		public double alpha;
		public double A;
		public double B;
		public double L;
		public Lab()
		{
			alpha = 1.0;
			A = 0.0;
			B = 0.0;
			L = 0.0;
		}
		public Lab(double L, double A, double B, double alpha)
		{
			this.L = L;
			this.A = A;
			this.B = B;
			this.alpha = alpha;
		}
		public Lab(Color color)
		{
			double r = color.r, g = color.g, b = color.b;
			alpha = color.a;
			double x, y, z;

			r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
			g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
			b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);

			x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489;
			y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000;
			z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840;

			x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
			y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
			z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

			L = (116.0 * y) - 16.0;
			A = 500.0 * (x - y);
			B = 200.0 * (y - z);
		}
		public Lab(int rgba)
		{
			double r = (rgba >>> 24) * 0x1.010101010101p-8,
				g = (rgba >>> 16 & 0xFF) * 0x1.010101010101p-8,
				b = (rgba >>> 8 & 0xFF) * 0x1.010101010101p-8;
			alpha = (rgba & 0xFF) * 0x1.010101010101p-8;
			double x, y, z;

			r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
			g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
			b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);

			x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489;
			y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000;
			z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840;

			x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
			y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
			z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

			L = (116.0 * y) - 16.0;
			A = 500.0 * (x - y);
			B = 200.0 * (y - z);
		}
		public Lab set(Color color)
		{
			double r = color.r, g = color.g, b = color.b;
			alpha = color.a;
			double x, y, z;

			r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
			g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
			b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);

			x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489;
			y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000;
			z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840;

			x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
			y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
			z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

			L = (116.0 * y) - 16.0;
			A = 500.0 * (x - y);
			B = 200.0 * (y - z);
			
			return this;
		}
		
		public double lightnessLAB(Color color)
		{
			double r = color.r, g = color.g, b = color.b, y;
			r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
			g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
			b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);
			y = (r * 0.2126 + g * 0.7152 + b * 0.0722);
			y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
			return (116.0 * y) - 16.0;
		}
		public Lab fromRGBA(int rgba)
		{
			double r = (rgba >>> 24) / 255.0, g = (rgba >>> 16 & 0xFF) / 255.0, b = (rgba >>> 8 & 0xFF) / 255.0;
			alpha = (rgba & 0xFF) / 255.0;
			double x, y, z;

			r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
			g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
			b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);

			x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
			y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
			z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;
	

			x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
			y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
			z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

			L = (116.0 * y) - 16.0;
			A = 500.0 * (x - y);
			B = 200.0 * (y - z);
			return this;
		}
		public Color toColor(){
			double y = (L + 16.0) / 116.0;
			double x = A / 500.0 + y;
			double z = y - B / 200.0;
			double r, g, b;

			x = 0.95047 * ((x > 0.2068930344229638) ? x * x * x : (x - 16.0 / 116.0) / 7.787);
			y = 1.00000 * ((y > 0.2068930344229638) ? y * y * y : (y - 16.0 / 116.0) / 7.787);
			z = 1.08883 * ((z > 0.2068930344229638) ? z * z * z : (z - 16.0 / 116.0) / 7.787);

			r = x *  3.2406 + y * -1.5372 + z * -0.4986;
			g = x * -0.9689 + y *  1.8758 + z *  0.0415;
			b = x *  0.0557 + y * -0.2040 + z *  1.0570;

			r = (r > 0.0031308) ? (1.055 * Math.pow(r, 1.0 / 2.4) - 0.055) : 12.92 * r;
			g = (g > 0.0031308) ? (1.055 * Math.pow(g, 1.0 / 2.4) - 0.055) : 12.92 * g;
			b = (b > 0.0031308) ? (1.055 * Math.pow(b, 1.0 / 2.4) - 0.055) : 12.92 * b;

			return new Color((float)r, (float)g, (float)b, (float)alpha);
		}

		public Color intoColor(Color color){
			double y = (L + 16.0) / 116.0;
			double x = A / 500.0 + y;
			double z = y - B / 200.0;
			double r, g, b;

			x = 0.95047 * ((x > 0.2068930344229638) ? x * x * x : (x - 16.0 / 116.0) / 7.787);
			y = 1.00000 * ((y > 0.2068930344229638) ? y * y * y : (y - 16.0 / 116.0) / 7.787);
			z = 1.08883 * ((z > 0.2068930344229638) ? z * z * z : (z - 16.0 / 116.0) / 7.787);

			r = x *  3.2406 + y * -1.5372 + z * -0.4986;
			g = x * -0.9689 + y *  1.8758 + z *  0.0415;
			b = x *  0.0557 + y * -0.2040 + z *  1.0570;

			r = (r > 0.0031308) ? (1.055 * Math.pow(r, 1.0 / 2.4) - 0.055) : 12.92 * r;
			g = (g > 0.0031308) ? (1.055 * Math.pow(g, 1.0 / 2.4) - 0.055) : 12.92 * g;
			b = (b > 0.0031308) ? (1.055 * Math.pow(b, 1.0 / 2.4) - 0.055) : 12.92 * b;

			return color.set((float)r, (float)g, (float)b, (float)alpha);
		}

		public int toRGBA(){
			double y = (L + 16.0) / 116.0;
			double x = A / 500.0 + y;
			double z = y - B / 200.0;
			double r, g, b;

			x = 0.95047 * ((x > 0.2068930344229638) ? x * x * x : (x - 16.0 / 116.0) / 7.787);
			y = 1.00000 * ((y > 0.2068930344229638) ? y * y * y : (y - 16.0 / 116.0) / 7.787);
			z = 1.08883 * ((z > 0.2068930344229638) ? z * z * z : (z - 16.0 / 116.0) / 7.787);

			r = x *  3.2406 + y * -1.5372 + z * -0.4986;
			g = x * -0.9689 + y *  1.8758 + z *  0.0415;
			b = x *  0.0557 + y * -0.2040 + z *  1.0570;

			r = ((r > 0.0031308) ? (1.055 * Math.pow(r, 1.0 / 2.4) - 0.055) : 12.92 * r) * 255.5;
			g = ((g > 0.0031308) ? (1.055 * Math.pow(g, 1.0 / 2.4) - 0.055) : 12.92 * g) * 255.5;
			b = ((b > 0.0031308) ? (1.055 * Math.pow(b, 1.0 / 2.4) - 0.055) : 12.92 * b) * 255.5;

			return  Math.max(0, Math.min(255, (int) r)) << 24 |
					Math.max(0, Math.min(255, (int) g)) << 16 | 
					Math.max(0, Math.min(255, (int) b)) << 8 | 
					Math.max(0, Math.min(255, (int) (alpha * 255.5)));
		}

	}
	
	public static Lab RGB2LAB(final double[] c)
	{
		double r = c[0], g = c[1], b = c[3];
		double x, y, z;

		r = (r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92;
		g = (g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92;
		b = (b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92;

		x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.95047;
		y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.00000;
		z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.08883;

		x = (x > 0.008856) ? Math.cbrt(x) : (7.787 * x) + 16.0 / 116.0;
		y = (y > 0.008856) ? Math.cbrt(y) : (7.787 * y) + 16.0 / 116.0;
		z = (z > 0.008856) ? Math.cbrt(z) : (7.787 * z) + 16.0 / 116.0;

		Lab lab = new Lab();
		lab.alpha = c[3];
		lab.L = (116 * y) - 16;
		lab.A = 500 * (x - y);
		lab.B = 200 * (y - z);
		return lab;
	}

	public static double[] LAB2RGB(final Lab lab){
		double y = (lab.L + 16.0) / 116.0;
		double x = lab.A * 0.002 + y;
		double z = y - lab.B * 0.005;
		double r, g, b;

		x = 0.95047 * ((x > 0.2068930344229638) ? x * x * x : (x - 16.0 / 116.0) / 7.787);
		y = 1.00000 * ((y > 0.2068930344229638) ? y * y * y : (y - 16.0 / 116.0) / 7.787);
		z = 1.08883 * ((z > 0.2068930344229638) ? z * z * z : (z - 16.0 / 116.0) / 7.787);

		r = x *  3.2406 + y * -1.5372 + z * -0.4986;
		g = x * -0.9689 + y *  1.8758 + z *  0.0415;
		b = x *  0.0557 + y * -0.2040 + z *  1.0570;

		r = (r > 0.0031308) ? (1.055 * Math.pow(r, 1.0 / 2.4) - 0.055) : 12.92 * r;
		g = (g > 0.0031308) ? (1.055 * Math.pow(g, 1.0 / 2.4) - 0.055) : 12.92 * g;
		b = (b > 0.0031308) ? (1.055 * Math.pow(b, 1.0 / 2.4) - 0.055) : 12.92 * b;

		return new double[]{Math.max(0.0, Math.min(1.0, r)), Math.max(0.0, Math.min(1.0, g)), Math.max(0.0, Math.min(1.0, b)), Math.max(0.0, Math.min(1.0, lab.alpha))};
	}

	public static int rgba8888(final double L, final double A, final double B){
		double y = (L + 16.0) / 116.0;
		double x = A * 0.002 + y;
		double z = y - B * 0.005;
		double r, g, b;

		x = 0.95047 * ((x > 0.2068930344229638) ? x * x * x : (x - 16.0 / 116.0) / 7.787);
		y = 1.00000 * ((y > 0.2068930344229638) ? y * y * y : (y - 16.0 / 116.0) / 7.787);
		z = 1.08883 * ((z > 0.2068930344229638) ? z * z * z : (z - 16.0 / 116.0) / 7.787);

		r = x *  3.2406 + y * -1.5372 + z * -0.4986;
		g = x * -0.9689 + y *  1.8758 + z *  0.0415;
		b = x *  0.0557 + y * -0.2040 + z *  1.0570;

		r = (r > 0.0031308) ? (1.055 * Math.pow(r, 1.0 / 2.4) - 0.055) : 12.92 * r;
		g = (g > 0.0031308) ? (1.055 * Math.pow(g, 1.0 / 2.4) - 0.055) : 12.92 * g;
		b = (b > 0.0031308) ? (1.055 * Math.pow(b, 1.0 / 2.4) - 0.055) : 12.92 * b;

		return MathUtils.clamp((int) (r * 255 + 0.5), 0, 255) << 24
				| MathUtils.clamp((int) (g * 255 + 0.5), 0, 255) << 16
				| MathUtils.clamp((int) (b * 255 + 0.5), 0, 255) << 8 | 0xFF;

//		if(r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1) return 0;
//		return (int) (r * 255 + 0.5) << 24 | (int) (g * 255 + 0.5) << 16 | (int) (b * 255 + 0.5) << 8 | 0xFF;
	}

	/*******************************************************************************
	* Conversions.
	******************************************************************************/
	private static final double deg2Rad30  = 30 ;//(0.5235987755982988);
	private static final double deg2Rad6   = 6  ;//(0.10471975511965978);
	private static final double deg2Rad25  = 25 ;//(0.4363323129985824);
	private static final double deg2Rad275 = 275;//(4.799655442984406);
	private static final double deg2Rad63  = 63 ;//(1.0995574287564276);

	private static double L_prime_div_k_L_S_L(final Lab lab1, final Lab lab2)
	{
		final double k_L = 1.0;
		double deltaLPrime = lab2.L - lab1.L;	
		double barLPrime = (lab1.L + lab2.L) / 2.0;
		double S_L = 1 + ((0.015 * Math.pow(barLPrime - 50.0, 2.0)) / Math.sqrt(20 + Math.pow(barLPrime - 50.0, 2.0)));
		return deltaLPrime / (k_L * S_L);
	}

	private static double C_prime_div_k_C_S_C(final Lab lab1, final Lab lab2)
	{
		final double k_C = 1.0;
		final double pow25To7 = 6103515625.0; /* pow(25, 7) */
		double C1 = Math.sqrt((lab1.A * lab1.A) + (lab1.B * lab1.B));
		double C2 = Math.sqrt((lab2.A * lab2.A) + (lab2.B * lab2.B));
		double barC = (C1 + C2) * 0.5;
		final double barCTo7 = Math.pow(barC, 7);
		double G = 0.5 * (1 - Math.sqrt(barCTo7 / (barCTo7 + pow25To7)));
		a1Prime = ((1.0 + G) * lab1.A);
		a2Prime = ((1.0 + G) * lab2.A);

		CPrime1 = (Math.sqrt((a1Prime * a1Prime) + (lab1.B * lab1.B)));
		CPrime2 = (Math.sqrt((a2Prime * a2Prime) + (lab2.B * lab2.B)));
		double deltaCPrime = CPrime2 - CPrime1;
		double barCPrime =  (CPrime1 + CPrime2) * 0.5;
		
		double S_C = 1 + (0.045 * barCPrime);
		return deltaCPrime / (k_C * S_C);
	}

	private static double H_prime_div_k_H_S_H(final Lab lab1, final Lab lab2)
	{
		final double k_H = 1.0;
		final double deg360InRad = 360;//Math.PI * 2.0;
		final double deg180InRad = 180;//Math.PI;
		double CPrimeProduct = CPrime1 * CPrime2;
		double hPrime1;
		if (Math.abs(lab1.B) < 0x1p-32 && Math.abs(a1Prime) < 0x1p-32)
			hPrime1 = 0.0;
		else {
			hPrime1 = 360.0* atan2_(lab1.B, a1Prime);
		}
		double hPrime2;
		if (Math.abs(lab2.B) < 0x1p-32 && Math.abs(a2Prime) < 0x1p-32)
			hPrime2 = 0.0;
		else {
			hPrime2 = 360.0*atan2_(lab2.B, a2Prime);
		}
		double deltahPrime;
		if (Math.abs(CPrimeProduct) < 0x1p-32)
			deltahPrime = 0;
		else {
			deltahPrime = hPrime2 - hPrime1;
			if (deltahPrime < -deg180InRad)
				deltahPrime += deg360InRad;
			else if (deltahPrime > deg180InRad)
				deltahPrime -= deg360InRad;
		}

		double deltaHPrime = 2.0 * Math.sqrt(CPrimeProduct) * sin_(deltahPrime / 720.0);
		double hPrimeSum = hPrime1 + hPrime2;
		if (Math.abs(CPrimeProduct) < 0x1p-32) {
			barhPrime = hPrimeSum;
		}
		else {
			if (Math.abs(hPrime1 - hPrime2) <= deg180InRad)
				barhPrime = hPrimeSum * 0.5;
			else {
				if (hPrimeSum < deg360InRad)
					barhPrime = ((hPrimeSum + deg360InRad) * 0.5);
				else
					barhPrime = ((hPrimeSum - deg360InRad) * 0.5);
			}
		}

		barCPrime = ((CPrime1 + CPrime2) * 0.5);
		double T = 1.0 - 
				(0.17 * cos_((barhPrime - deg2Rad30) / 360.0)) + 
				(0.24 * cos_((2.0 * barhPrime)) / 360.0) + 
				(0.32 * cos_(((3.0 * barhPrime) + deg2Rad6)) / 360.0) - 
				(0.20 * cos_(((4.0 * barhPrime) - deg2Rad63)) / 360.0);
		double S_H = 1 + (0.015 * barCPrime * T);
		return deltaHPrime / (k_H * S_H);
	}

	private static double R_T()
	{
		final double pow25To7 = 6103515625.0; /* Math.pow(25, 7) */
		final double barCPrimeTo7 = Math.pow(barCPrime, 7.0);
		final double deltaTheta = deg2Rad30 * Math.exp(-Math.pow((barhPrime - deg2Rad275) / deg2Rad25, 2.0));
		final double R_C = -2.0 * Math.sqrt(barCPrimeTo7 / (barCPrimeTo7 + pow25To7));
		return sin_(deltaTheta / 180.0) * R_C;
	}

	/* From the paper "The CIEDE2000 Color-Difference Formula: Implementation Notes, */
	/* Supplementary Test Data, and Mathematical Observations", by */
	/* Gaurav Sharma, Wencheng Wu and Edul N. Dalal, */
	/* Color Res. Appl., vol. 30, no. 1, pp. 21-30, Feb. 2005. */
	/* Return the CIEDE2000 Delta E color difference measure squared, for two Lab values */
	public static double difference (final Lab lab1, final Lab lab2)
	{
		double deltaL_prime_div_k_L_S_L = L_prime_div_k_L_S_L(lab1, lab2);
		double deltaC_prime_div_k_C_S_C = C_prime_div_k_C_S_C(lab1, lab2);
		double deltaH_prime_div_k_H_S_H = H_prime_div_k_H_S_H(lab1, lab2);
		double deltaR_T = R_T() * deltaC_prime_div_k_C_S_C * deltaH_prime_div_k_H_S_H;
		return
			deltaL_prime_div_k_L_S_L * deltaL_prime_div_k_L_S_L +
			deltaC_prime_div_k_C_S_C * deltaC_prime_div_k_C_S_C +
			deltaH_prime_div_k_H_S_H * deltaH_prime_div_k_H_S_H +
			deltaR_T;
	}
	
	public double cmc(final Lab lab1, final Lab lab2)
	{
		final double a1 = lab1.A, b1 = lab1.B, l1 = lab1.A;
		final double a2 = lab2.A, b2 = lab2.B, l2 = lab2.A;
		final double weightL = 1.0, weightC = 1.0;
		double xC1 = Math.sqrt(a1 * a1 + b1 * b1);
		double xC2 = Math.sqrt(a2 * a2 + b2 * b2);
		double xff = xC1 * xC1;
		xff *= xC2 * xC2;
		xff = Math.sqrt(xff / (xff + 1900));
		double xH1 = atan2_(b1, a1) * 360.0;
		double xTT, xSL;
		if ( xH1 < 164 || xH1 > 345 ) xTT = 0.36 + Math.abs( 0.4 * cos_(( 35 + xH1) / 360.0));
		else                          xTT = 0.56 + Math.abs( 0.2 * cos_((168 + xH1) / 360.0));

		if ( l1 < 16 ) xSL = 0.511;
		else           xSL = (0.040975 * l1) / (1.0 + (0.01765 * l1));
		double xSC = ((0.0638 * xC1) / ( 1 + (0.0131 * xC1))) + 0.638;
		double xSH = ((xff * xTT) + 1 - xff) * xSC;
		double xDH = Math.sqrt((a2 - a1) * (a2 - a1) + (b2 - b1) * (b2 - b1) - (xC2 - xC1) * (xC2 - xC1));
		xSL = (l2 - l1) / (weightL * xSL);
		xSC = (xC2 - xC1) / (weightC * xSC);
		xSH = xDH / xSH;
		
		return xSL * xSL + xSC * xSC + xSH * xSH;
		
		/* //formulas found on https://www.easyrgb.com/en/math.php
CIE-L*1, CIE-a*1, CIE-b*1          //Color #1 CIE-L*ab values
CIE-L*2, CIE-a*2, CIE-b*2          //Color #2 CIE-L*ab values
WHT-L, WHT-C                       //Weight factors

xC1 = sqrt( ( CIE-a*1 ^ 2 ) + ( CIE-b*1 ^ 2 ) )
xC2 = sqrt( ( CIE-a*2 ^ 2 ) + ( CIE-b*2 ^ 2 ) )
xff = sqrt( ( xC1 ^ 4 ) / ( ( xC1 ^ 4 ) + 1900 ) )
xH1 = CieLab2Hue( CIE-a*1, CIE-b*1 )

if ( xH1 < 164 || xH1 > 345 ) xTT = 0.36 + abs( 0.4 * cos( dtor(  35 + xH1 ) ) )
else                          xTT = 0.56 + abs( 0.2 * cos( dtor( 168 + xH1 ) ) )

if ( CIE-L*1 < 16 ) xSL = 0.511
else                xSL = ( 0.040975 * CIE-L*1 ) / ( 1 + ( 0.01765 * CIE-L*1 ) )

xSC = ( ( 0.0638 * xC1 ) / ( 1 + ( 0.0131 * xC1 ) ) ) + 0.638
xSH = ( ( xff * xTT ) + 1 - xff ) * xSC
xDH = sqrt( ( CIE-a*2 - CIE-a*1 ) ^ 2 + ( CIE-b*2 - CIE-b*1 ) ^ 2 - ( xC2 - xC1 ) ^ 2 )
xSL = ( CIE-L*2 - CIE-L*1 ) / ( WHT-L * xSL )
xSC = ( xC2 - xC1 ) / ( WHT-C * xSC )
xSH = xDH / xSH

Delta CMC = sqrt( xSL ^ 2 + xSC ^ 2 + xSH ^ 2 )
		 */
	}

	public static double delta(final Lab lab1, final Lab lab2)
	{
		return (lab1.L - lab2.L) * (lab1.L - lab2.L) * 11.0 +
			(lab1.A - lab2.A) * (lab1.A - lab2.A) * 1.6 +
			(lab1.B - lab2.B) * (lab1.B - lab2.B);
	}

	public static double delta(final Lab lab1, final Lab lab2, final double biasL, final double biasA, final double biasB)
	{
//		final double purpleAdjustment = (Math.max(0.0, lab1.A - lab1.B * 2.5 - 50) * (lab1.A + lab1.B + lab1.L * 0.5)
//			- Math.max(0.0, lab2.A - lab2.B * 2.5 - 50) * (lab2.A + lab2.B + lab2.L * 0.5)) * 0.1;
		return (lab1.L - lab2.L) * (lab1.L - lab2.L) * 11.0 * biasL +
			(lab1.A - lab2.A) * (lab1.A - lab2.A) * biasA * 1.6 +
			(lab1.B - lab2.B) * (lab1.B - lab2.B) * biasB
//			+ purpleAdjustment * purpleAdjustment
			;
	}

	public static double differenceLAB(double L, double A, double B, double L2, double A2, double B2)
	{
		L -= L2;
		A -= A2;
		B -= B2;
		return L * L * 11.0 + A * A * 1.6 + B * B;
	}
	public static double differenceLAB(final int rgba1, final int rgba2)
	{
		return differenceLAB(rgba1, rgba2, 1.0, 1.0, 1.0);
	}
	public static double differenceLAB(final int rgba1, final int rgba2, final double biasL, final double biasA, final double biasB)
	{
		if(((rgba1 ^ rgba2) & 0x80) == 0x80) return Double.POSITIVE_INFINITY;
		double x, y, z, r, g, b;

		r = (rgba1 >>> 24) / 255.0;
		g = (rgba1 >>> 16 & 0xFF) / 255.0;
		b = (rgba1 >>> 8 & 0xFF) / 255.0;

		r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
		g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
		b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);

		x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
		y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
		z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;
		
		x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
		y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
		z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

		double L = (116.0 * y) - 16.0;
		double A = 500.0 * (x - y);
		double B = 200.0 * (y - z);

		r = (rgba2 >>> 24) / 255.0;
		g = (rgba2 >>> 16 & 0xFF) / 255.0;
		b = (rgba2 >>> 8 & 0xFF) / 255.0;

		r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
		g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
		b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);

		x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
		y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
		z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;

		x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
		y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
		z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

		L -= 116.0 * y - 16.0;
		A -= 500.0 * (x - y);
		B -= 200.0 * (y - z);
		
		return L * L * 11.0 * biasL + A * A * 1.6 * biasA + B * B * biasB;
	}
	public static double[][] makeLAB15()
	{
//		double[] minA = new double[20], maxA = new double[20], minB = new double[20], maxB = new double[20];
//		int[][][] grids = new int[20][5][5];


		final double[][] labs = new double[3][0x8000];
		double r, g, b, x, y, z;
		for (int ri = 0; ri < 32; ri++) {
			r = ri / 31.0;
			r = ((r > 0.04045) ? Math.pow((r + 0.055) / 1.055, 2.4) : r / 12.92);
			for (int gi = 0; gi < 32; gi++) {
				g = gi / 31.0;
				g = ((g > 0.04045) ? Math.pow((g + 0.055) / 1.055, 2.4) : g / 12.92);
				for (int bi = 0; bi < 32; bi++) {
					b = bi / 31.0;
					b = ((b > 0.04045) ? Math.pow((b + 0.055) / 1.055, 2.4) : b / 12.92);

					int idx = ri << 10 | gi << 5 | bi;

					x = (r * 0.4124 + g * 0.3576 + b * 0.1805) / 0.950489; // 0.96422;
					y = (r * 0.2126 + g * 0.7152 + b * 0.0722) / 1.000000; // 1.00000;
					z = (r * 0.0193 + g * 0.1192 + b * 0.9505) / 1.088840; // 0.82521;


//					x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
//					y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
//					z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

					x = (x > 0.008856) ? Math.cbrt(x) : (7.787037037037037 * x) + 0.13793103448275862;
					y = (y > 0.008856) ? Math.cbrt(y) : (7.787037037037037 * y) + 0.13793103448275862;
					z = (z > 0.008856) ? Math.cbrt(z) : (7.787037037037037 * z) + 0.13793103448275862;

					labs[0][idx] = (116.0 * y) - 16.0;
					labs[1][idx] = 500.0 * (x - y);
					labs[2][idx] = 200.0 * (y - z);
//					int l = (int)(L * 0.19 + 0.5); // was 1.0 / 11.111
//					minA[l] = Math.min(minA[l], A);
//					maxA[l] = Math.max(maxA[l], A);
//					minB[l] = Math.min(minB[l], B);
//					maxB[l] = Math.max(maxB[l], B);
//					if(A == minA[l])
//						grids[l][0][0] = idx;
//					if(B == minB[l])
//						grids[l][4][0] = idx;
//					if(B == maxB[l])
//						grids[l][0][4] = idx;
//					if(A == maxA[l])
//						grids[l][4][4] = idx;
				}
			}
		}

//		for (int l = 0; l < 20; l++) {
//			int lowLow = grids[l][0][0], highLow = grids[l][4][0], lowHigh = grids[l][0][4], highHigh = grids[l][4][4];
//			grids[l][1][0] = splitLeft15(lowLow, highLow);
//			grids[l][2][0] = split15(lowLow, highLow);
//			grids[l][3][0] = splitRight15(lowLow, highLow);
//
//			grids[l][1][4] = splitLeft15(lowHigh, highHigh);
//			grids[l][2][4] = split15(lowHigh, highHigh);
//			grids[l][3][4] = splitRight15(lowHigh, highHigh);
//
//			grids[l][0][1] = splitLeft15(lowLow, lowHigh);
//			grids[l][0][2] = split15(lowLow, lowHigh);
//			grids[l][0][3] = splitRight15(lowLow, lowHigh);
//
//			grids[l][4][1] = splitLeft15(highLow, highHigh);
//			grids[l][4][2] = split15(highLow, highHigh);
//			grids[l][4][3] = splitRight15(highLow, highHigh);
//
//			grids[l][1][1] = split15(splitLeft15(grids[l][1][0], grids[l][1][4]), splitLeft15(grids[l][0][1], grids[l][4][1]));
//			grids[l][2][1] = split15(splitLeft15(grids[l][2][0], grids[l][2][4]), split15(grids[l][0][1], grids[l][4][1]));
//			grids[l][3][1] = split15(splitLeft15(grids[l][3][0], grids[l][3][4]), splitRight15(grids[l][0][1], grids[l][4][1]));
//
//			grids[l][1][2] = split15(split15(grids[l][1][0], grids[l][1][4]), splitLeft15(grids[l][0][2], grids[l][4][2]));
//			grids[l][2][2] = split15(split15(grids[l][2][0], grids[l][2][4]), split15(grids[l][0][2], grids[l][4][2]));
//			grids[l][3][2] = split15(split15(grids[l][3][0], grids[l][3][4]), splitRight15(grids[l][0][2], grids[l][4][2]));
//			
//			grids[l][1][3] = split15(splitRight15(grids[l][1][0], grids[l][1][4]), splitLeft15(grids[l][0][3], grids[l][4][3]));
//			grids[l][2][3] = split15(splitRight15(grids[l][2][0], grids[l][2][4]), split15(grids[l][0][3], grids[l][4][3]));
//			grids[l][3][3] = split15(splitRight15(grids[l][3][0], grids[l][3][4]), splitRight15(grids[l][0][3], grids[l][4][3]));
//		}
//		StringBuilder sb = new StringBuilder(500).append("new int[] {\n");
//		for (int i = 0; i < 20; i++) {
//			System.out.println("At L " + (int)(i * 5.2631578947368425) + ", A ranges from " + minA[i] + " to " + maxA[i]);
//			System.out.println("At L " + (int)(i * 5.2631578947368425) + ", B ranges from " + minB[i] + " to " + maxB[i]);
//			sb.append("\n");
//			for (int bb = 0; bb < 5; bb++) {
////				sb.append("{ ");
//				for (int aa = 0; aa < 5; aa++) {
//					StringKit.appendHex(sb.append("0x"), puff(grids[i][aa][bb])).append(", ");
//				}
//				sb.append("\n");
//			}
//			sb.append("\n");
//		}
//		sb.append("};\n");
//		System.out.println(sb);
		
		return labs;
	}
	
	public static int puff(final int small)
	{
		return (small << 17 & 0xF8000000) | (small << 12 & 0x07000000) | (small << 14 & 0xF80000) | (small << 9 & 0x070000) | (small << 11 & 0xF800) | (small << 6 & 0x0700) | 0xFF;
	}

	public static int shrink(int r, int g, int b)
	{
		return (r & 0xF8) << 7 | (g & 0xF8) << 2 | (b >>> 3);
	}
	public static int shrink(int color)
	{
		return (color >>> 17 & 0x7C00) | (color >>> 14 & 0x3E0) | (color >>> 11 & 0x1F);
	}
	
	private static int split15(int left, int right)
	{
		return ((left >>> 10) + (right >>> 10) + 1 << 9 & 0x7C00) |
				((left >>> 5 & 0x3E0) + (right >>> 5 & 0x3E0) + 1 << 4 & 0x3E0) |
				((left & 0x1F) + (right & 0x1F) + 1 >>> 1 & 0x1F);
	}

	private static int splitRight15(int left, int right)
	{
		return ((left >>> 10) + (right >>> 10) * 3 + 3 << 8 & 0x7C00) |
				((left >>> 5 & 0x3E0) + (right >>> 5 & 0x3E0) * 3 + 3 << 3 & 0x3E0) |
				((left & 0x1F) + (right & 0x1F) * 3 + 3 >>> 2 & 0x1F);
	}

	private static int splitLeft15(int left, int right)
	{
		return ((left >>> 10) * 3 + (right >>> 10) + 3 << 8 & 0x7C00) |
				((left >>> 5 & 0x3E0) * 3 + (right >>> 5 & 0x3E0) + 3 << 3 & 0x3E0) |
				((left & 0x1F) * 3 + (right & 0x1F) + 3 >>> 2 & 0x1F);
	}
	
	public static double difference15(final double[][] lab15, final int indexA, final int indexB)
	{
		final double
				L = lab15[0][indexA] - lab15[0][indexB],
				A = lab15[1][indexA] - lab15[1][indexB],
				B = lab15[2][indexA] - lab15[2][indexB];
		return L * L * 400.0 + A * A * 25.0 + B * B * 10.0;
//		return L * L * 50.0 + A * A * 50.0 + B * B * 50.0;
	}
}
