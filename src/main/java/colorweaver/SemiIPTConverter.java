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
}
