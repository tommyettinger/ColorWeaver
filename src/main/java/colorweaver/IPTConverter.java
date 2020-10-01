package colorweaver;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * A more perceptually-uniform color space than CIELAB while maybe being easier to compute.
 * This is IPT, a color space discovered by Ebner and Fairchild and described
 * <a href="https://www.researchgate.net/publication/221677980_Development_and_Testing_of_a_Color_Space_IPT_with_Improved_Hue_Uniformity">in this dissertation</a>.
 * <br>
 * Massive credit to CypherCove for doing all of the code work here; formulas are taken from 
 * <a href="https://github.com/CypherCove/gdx-tween/blob/b9a14c3e1b7508d69912dc0e26ad94db768ac3e8/gdxtween/src/main/java/com/cyphercove/gdxtween/graphics/GtColor.java#L569-L647">his IPT code</a>.
 * Although there seems to be a patent filed by Kodak and related to IPT, none of the constants are the same between
 * this technique and the patent, so they probably describe unrelated methods.
 */
public class IPTConverter {
	public static class IPT {
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

		public IPT()
		{
			this(0.0, 0.0, 0.0, 1.0);
		}
		public IPT(double i, double p, double t) {
			this(i, p, t, 1.0);
		}

		public IPT(double i, double p, double t, double a) {
			this.i = i;
			this.p = p;
			this.t = t;
			this.a = a;
		}

		public IPT(Color color){
			double l = Math.pow(0.313921 * color.r + 0.639468 * color.g + 0.0465970 * color.b, 0.43);
			double m = Math.pow(0.151693 * color.r + 0.748209 * color.g + 0.1000044 * color.b, 0.43);
			double s = Math.pow(0.017700 * color.r + 0.109400 * color.g + 0.8729000 * color.b, 0.43);

			i = 0.4000 * l + 0.4000 * m + 0.2000 * s;
			p = 6.6825 * l - 7.2765 * m + 0.5940 * s;
			t = 1.0741 * l + 0.4763 * m - 1.5504 * s;
			a = color.a;
		}

		public IPT(int rgba){
			double r = (rgba >>> 24) * 0x1.010101010101p-8,
					g = (rgba >>> 16 & 0xFF) * 0x1.010101010101p-8,
					b = (rgba >>> 8 & 0xFF) * 0x1.010101010101p-8;
			a = (rgba & 0xFF) * 0x1.010101010101p-8;
			double l = Math.pow(0.313921 * r + 0.639468 * g + 0.0465970 * b, 0.43);
			double m = Math.pow(0.151693 * r + 0.748209 * g + 0.1000044 * b, 0.43);
			double s = Math.pow(0.017700 * r + 0.109400 * g + 0.8729000 * b, 0.43);

			i = 0.4000f * l + 0.4000f * m + 0.2000f * s;
			p = 6.6825 * l - 7.2765 * m + 0.5940 * s;
			t = 1.0741 * l + 0.4763 * m - 1.5504 * s;
		}
		// vec3 ipt = mat3(0.4000, 4.4550, 0.8056, 0.4000, 4.8510, 0.3572, 0.2000, 0.3960, 1.1628) * 
		//      pow(mat3(0.313921, 0.151693, 0.017700, 0.639468, 0.748209, 0.109400, 0.0465970, 0.1000044, 0.8729000) * tgt.rgb, vec3(0.43));
		// vec3 back = mat3(1.0, 1.0, 1.0, 0.06503950, -0.07591241, 0.02174116, 0.15391950, 0.09991275, -0.50766750) * ipt;
		// tgt.rgb = mat3(5.432622, -1.10517, 0.028104, -4.67910, 2.311198, -0.19466, 0.246257, -0.20588, 1.166325) * 
		//     (pow(abs(back), vec3(2.3256)) * sign(back));
		public Color intoColor(Color color) {
			double lPrime = i + 0.06503950 * p + 0.15391950 * t;
			double mPrime = i - 0.07591241 * p + 0.09991275 * t;
			double sPrime = i + 0.02174116 * p - 0.50766750 * t;
			float l = (float) Math.copySign(Math.pow(Math.abs(lPrime), 2.3256), lPrime);
			float m = (float) Math.copySign(Math.pow(Math.abs(mPrime), 2.3256), mPrime);
			float s = (float) Math.copySign(Math.pow(Math.abs(sPrime), 2.3256), sPrime);
			color.r = 5.432622f * l + -4.67910f * m + 0.246257f * s;
			color.g = -1.10517f * l + 2.311198f * m + -0.20588f * s;
			color.b = 0.028104f * l + -0.19466f * m + 1.166325f * s;
			color.a = (float) a;
			return color.clamp();
		}
		public int rgba8888(){
			double lPrime = i + 0.06503950 * p + 0.15391950 * t;
			double mPrime = i - 0.07591241 * p + 0.09991275 * t;
			double sPrime = i + 0.02174116 * p - 0.50766750 * t;
			double l = Math.copySign(Math.pow(Math.abs(lPrime), 2.3256), lPrime);
			double m = Math.copySign(Math.pow(Math.abs(mPrime), 2.3256), mPrime);
			double s = Math.copySign(Math.pow(Math.abs(sPrime), 2.3256), sPrime);
			int r = MathUtils.clamp((int) ((5.432622 * l - 4.679100 * m + 0.246257 * s) * 255.99999), 0, 255);
			int g = MathUtils.clamp((int) ((-1.10517 * l + 2.311198 * m - 0.205880 * s) * 255.99999), 0, 255);
			int b = MathUtils.clamp((int) ((0.028104 * l - 0.194660 * m + 1.166325 * s) * 255.99999), 0, 255);
			int a = (int) (this.a * 255.99999);
			return r << 24 | g << 16 | b << 8 | a;
		}
	}
	public static double[][] makeIPT15()
	{
		final double[][] ipts = new double[3][0x8000];
		double r, g, b, i, p, t;
		double[] minP = new double[20], maxP = new double[20], minT = new double[20], maxT = new double[20];
		double minI = Double.MAX_VALUE, maxI = -Double.MAX_VALUE;
		for (int ri = 0; ri < 32; ri++) {
			r = ri / 31.0;
			for (int gi = 0; gi < 32; gi++) {
				g = gi / 31.0;
				for (int bi = 0; bi < 32; bi++) {
					b = bi / 31.0;
					int idx = ri << 10 | gi << 5 | bi;
					double l = Math.pow(0.313921 * r + 0.639468 * g + 0.0465970 * b, 0.43);
					double m = Math.pow(0.151693 * r + 0.748209 * g + 0.1000044 * b, 0.43);
					double s = Math.pow(0.017753 * r + 0.109468 * g + 0.8729690 * b, 0.43);

					ipts[0][idx] = i = 0.4000 * l + 0.4000 * m + 0.2000 * s;
					ipts[1][idx] = p = 6.6825 * l - 7.2765 * m + 0.5940 * s;
					ipts[2][idx] = t = 1.0741 * l + 0.4763 * m - 1.5504 * s;

					minI = Math.min(minI, i);
					maxI = Math.max(maxI, i);
					int y = (int)(i * 20);
					minP[y] = Math.min(minP[y], p);
					maxP[y] = Math.max(maxP[y], p);
					minT[y] = Math.min(minT[y], t);
					maxT[y] = Math.max(maxT[y], t);		
				}
			}
		}
		System.out.println("I ranges from " + minI + " to " + maxI);
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
		return i * i * 4.0 + p * p * 1.5 + t * t;

//		return i * i * 25.0 + p * p * 4.0 + t * t;
//		return L * L * 50.0 + A * A * 50.0 + B * B * 50.0;
	}

}
