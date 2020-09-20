package colorweaver;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * A more perceptually-uniform color space than CIELAB while maybe being easier to compute.
 * This is a stripped-down and simplified version of IPT, a color space discovered by Ebner and Fairchild and described
 * <a href="https://www.researchgate.net/publication/221677980_Development_and_Testing_of_a_Color_Space_IPT_with_Improved_Hue_Uniformity">in this dissertation</a>.
 * <br>
 * Massive credit to CypherCove for doing all of the code work here; formulas are taken from his Compressed LMS code: 
 * https://github.com/CypherCove/gdx-tween/blob/b9a14c3e1b7508d69912dc0e26ad94db768ac3e8/gdxtween/src/main/java/com/cyphercove/gdxtween/graphics/GtColor.java#L641
 * Although there seems to be a patent filed by Kodak and related to IPT, none of the constants are the same between
 * this technique and the patent, so they probably describe unrelated methods.
 */
public class SemiIPTConverter {
	public static class SemiIPT {
		/**
		 * Intensity; very accurate form of luminance.
		 */
		public double i;
		/**
		 * Protan; red when high, green when low.
		 */
		public double p;
		/**
		 * Tritan; yellow when high, blue when low.
		 */
		public double t;
		/**
		 * Alpha, or opacity.
		 */
		public double a;

		public SemiIPT()
		{
			this(0.0, 0.0, 0.0, 1.0);
		}
		public SemiIPT(double i, double p, double t) {
			this(i, p, t, 1.0);
		}

		public SemiIPT(double i, double p, double t, double a) {
			this.i = i;
			this.p = p;
			this.t = t;
			this.a = a;
		}

		public SemiIPT(Color color){
			double l = 0.313921 * color.r + 0.639468 * color.g + 0.0465970 * color.b;
			double m = 0.151693 * color.r + 0.748209 * color.g + 0.1000044 * color.b;
			double s = 0.017753 * color.r + 0.109468 * color.g + 0.8729690 * color.b;
			i = Math.pow(l, 0.43);
			p = Math.pow(m, 0.43);
			t = Math.pow(s, 0.43);
			a = color.a;
		}

		public SemiIPT(int rgba){
			double r = (rgba >>> 24) * 0x1.010101010101p-8,
					g = (rgba >>> 16 & 0xFF) * 0x1.010101010101p-8,
					b = (rgba >>> 8 & 0xFF) * 0x1.010101010101p-8;
			a = (rgba & 0xFF) * 0x1.010101010101p-8;
			double l = 0.313921 * r + 0.639468 * g + 0.0465970 * b;
			double m = 0.151693 * r + 0.748209 * g + 0.1000044 * b;
			double s = 0.017753 * r + 0.109468 * g + 0.8729690 * b;
			i = Math.pow(l, 0.43);
			p = Math.pow(m, 0.43);
			t = Math.pow(s, 0.43); 
		}
		public Color intoColor(Color color){

			float l = (float) Math.pow(i, 2.3256);
			float m = (float) Math.pow(p, 2.3256);
			float s = (float) Math.pow(t, 2.3256);
			color.r = 5.432622f * l - 4.679100f * m + 0.246257f * s;
			color.g = -1.10517f * l + 2.311198f * m - 0.205880f * s;
			color.b = 0.028104f * l - 0.194660f * m + 1.166325f * s;
			color.a = (float) a;
			return color.clamp();
		}
		public int rgba8888(){

			double l = Math.pow(i, 2.3256);
			double m = Math.pow(p, 2.3256);
			double s = Math.pow(t, 2.3256);
			int r = MathUtils.clamp((int) ((5.432622f * l - 4.679100f * m + 0.246257f * s) * 256.0 - 0.5), 0, 255);
			int g = MathUtils.clamp((int) ((-1.10517f * l + 2.311198f * m - 0.205880f * s) * 256.0 - 0.5), 0, 255);
			int b = MathUtils.clamp((int) ((0.028104f * l - 0.194660f * m + 1.166325f * s) * 256.0 - 0.5), 0, 255);
			int a = (int) (this.a * 256.0 -0.5);
			return r << 24 | g << 16 | b << 8 | a;
		}
	}
	public static double[][] makeIPT15()
	{
		double[] minP = new double[20], maxP = new double[20], minT = new double[20], maxT = new double[20];


		final double[][] ipts = new double[3][0x8000];
		double r, g, b, i, p, t;
		for (int ri = 0; ri < 32; ri++) {
			r = ri / 31.0;
			for (int gi = 0; gi < 32; gi++) {
				g = gi / 31.0;
				for (int bi = 0; bi < 32; bi++) {
					b = bi / 31.0;
					int idx = ri << 10 | gi << 5 | bi;
					double l = 0.313921 * r + 0.639468 * g + 0.0465970 * b;
					double m = 0.151693 * r + 0.748209 * g + 0.1000044 * b;
					double s = 0.017753 * r + 0.109468 * g + 0.8729690 * b;
					ipts[0][idx] = i = Math.pow(l, 0.43);
					ipts[1][idx] = p = Math.pow(m, 0.43);
					ipts[2][idx] = t = Math.pow(s, 0.43);
					int y = (int)(i * 20);
					minP[y] = Math.min(minP[y], p);
					maxP[y] = Math.max(maxP[y], p);
					minT[y] = Math.min(minT[y], t);
					maxT[y] = Math.max(maxT[y], t);
		
				}
			}
		}

		for (int y = 0; y < 20; y++) {
			System.out.println("At I " + (int)(y / 19.0) + ", P ranges from " + minP[y] + " to " + maxP[y]);
			System.out.println("At I " + (int)(y / 19.0) + ", T ranges from " + minT[y] + " to " + maxT[y]);
		}

		return ipts;
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
	
	public static double difference15(final double[][] ipt15, final int indexA, final int indexB)
	{
		final double
				i = ipt15[0][indexA] - ipt15[0][indexB],
				p = ipt15[1][indexA] - ipt15[1][indexB],
				t = ipt15[2][indexA] - ipt15[2][indexB];
		return i * i * 7.0 + p * p + t * t;
//		return L * L * 50.0 + A * A * 50.0 + B * B * 50.0;
	}

}
