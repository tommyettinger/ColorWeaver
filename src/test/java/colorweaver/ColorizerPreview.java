package colorweaver;

import colorweaver.tools.StringKit;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import static colorweaver.PaletteReducer.oklabMetric;

/**
 * Created by Tommy Ettinger on 1/30/2020.
 */
public class ColorizerPreview extends ApplicationAdapter {
	public static final int[][] CUBE = {
		{4,4,4,4,4,4,4,0,0,4,4,4,4,4,4,4,},
		{4,4,4,4,4,0,0,3,3,0,0,4,4,4,4,4,},
		{4,4,4,0,0,3,3,3,3,3,3,0,0,4,4,4,},
		{4,0,0,3,3,3,3,3,3,3,3,3,3,0,0,4,},
		{0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,},
		{0,2,2,3,3,3,3,3,3,3,3,3,3,1,1,0,},
		{0,2,2,2,2,3,3,3,3,3,3,1,1,1,1,0,},
		{0,2,2,2,2,2,2,3,3,1,1,1,1,1,1,0,},
		{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,},
		{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,},
		{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,},
		{0,2,2,2,2,2,2,2,1,1,1,1,1,1,1,0,},
		{4,0,0,2,2,2,2,2,1,1,1,1,1,0,0,4,},
		{4,4,4,0,0,2,2,2,1,1,1,0,0,4,4,4,},
		{4,4,4,4,4,0,0,2,1,0,0,4,4,4,4,4,},
		{4,4,4,4,4,4,4,0,0,4,4,4,4,4,4,4,},
	};
	public static final int[][] BALL = {
		{4,4,4,4,4,4,0,0,0,0,4,4,4,4,4,4,},
		{4,4,4,4,0,0,0,0,0,0,0,0,4,4,4,4,},
		{4,4,4,0,0,0,2,2,2,2,0,0,0,4,4,4,},
		{4,4,0,0,2,2,3,3,2,2,2,2,0,0,4,4,},
		{4,0,0,2,2,3,3,3,3,2,2,2,2,0,0,4,},
		{4,0,0,2,2,3,3,3,3,2,2,2,2,0,0,4,},
		{0,0,2,2,2,2,3,3,2,2,2,2,2,2,0,0,},
		{0,0,2,2,2,2,2,2,2,2,2,2,2,1,0,0,},
		{0,0,2,2,2,2,2,2,2,2,2,2,1,1,0,0,},
		{0,0,1,1,2,2,2,2,2,2,2,1,1,1,0,0,},
		{4,0,0,1,1,2,2,2,2,1,1,1,1,0,0,4,},
		{4,0,0,1,1,1,1,1,1,1,1,1,1,0,0,4,},
		{4,4,0,0,1,1,1,1,1,1,1,1,0,0,4,4,},
		{4,4,4,0,0,0,1,1,1,1,0,0,0,4,4,4,},
		{4,4,4,4,0,0,0,0,0,0,0,0,4,4,4,4,},
		{4,4,4,4,4,4,0,0,0,0,4,4,4,4,4,4,},
	};
	public static int[][] SHAPE = BALL;
	private Pixmap cubePix;
	private Texture[] cubeTextures;
	private Colorizer colorizer;
	private ArrayList<Integer> mixingPalette;
	private int[] palette;
	private MutantBatch batch;
	private long state = 1234567898765431L;
	private float nextCurvedFloat ()
	{
		return (((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L >> 42)
		+ ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L >> 42)
		+ ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L >> 42)
		+ ((state = (state << 29 | state >>> 35) * 0xAC564B05L) * 0x818102004182A025L >> 42)) * 0x1p-24f;
	}


	private static float hue(final int color) {
		final float r = (color >>> 24       ) * 0x1.010102p-8f;
		final float g = (color >>> 16 & 0xFF) * 0x1.010102p-8f;
		final float b = (color >>>  8 & 0xFF) * 0x1.010102p-8f;
		final float min = Math.min(Math.min(r, g), b);   //Min. value of RGB
		final float max = Math.max(Math.max(r, g), b);   //Max value of RGB
		final float delta = max - min;                   //Delta RGB value

		if ( delta < 0.1f )                     //This is mostly gray, not much chroma...
		{
			return -100 + max * 0.01f;
		}
		else                                    //Chromatic data...
		{
			final float rDelta = (((max - r) / 6f) + (delta * 0.5f)) / delta;
			final float gDelta = (((max - g) / 6f) + (delta * 0.5f)) / delta;
			final float bDelta = (((max - b) / 6f) + (delta * 0.5f)) / delta;

			if      (r == max) return (1f + bDelta - gDelta)             - (int)(1f + bDelta - gDelta)            ;
			else if (g == max) return (1f + (1f / 3f) + rDelta - bDelta) - (int)(1f + (1f / 3f) + rDelta - bDelta);
			else               return (1f + (2f / 3f) + gDelta - rDelta) - (int)(1f + (2f / 3f) + gDelta - rDelta);
		}
	}
	
	private static int roughBrightness(int color)
	{
		return (
			(color >>> 24) * 3 +
			(color >>> 14 & 0x3FC) +
			(color >>>  8 & 0xFF));
	}

	public static final Comparator<Integer> hueComparator = new Comparator<Integer>() {
		public int compare (Integer o1, Integer o2) {
			return Float.compare(hue(o1), hue(o2));
		}
	};

	private static final Comparator<Integer> lightnessComparator = new Comparator<Integer>() {
		public int compare (Integer o1, Integer o2) {
			if(o1 == 0) return -1;
			if(o2 == 0) return 1;
//			return (int)Math.signum(CIELABConverter.differenceLAB(o1, o2) - 50);
			return Integer.compare(roughBrightness(o1), roughBrightness(o2));
		}
	};

	public int[] lloyd(int[] palette) {
		CIELABConverter.makeLAB15();
		PaletteReducer pr = new PaletteReducer(palette, oklabMetric);
		float[][] centroids = new float[4][palette.length];
		byte[] pm = pr.paletteMapping;
		int index, mix;
		float count;
		for (int i = 0; i < 0x8000; i++) {
			index = pm[i] & 0xFF;
			centroids[0][index] += (i >>> 10);
			centroids[1][index] += (i >>> 5 & 0x1F);
			centroids[2][index] += (i & 0x1F);
			centroids[3][index]++;
		}
		mixingPalette.clear();
		for (int i = 0; i < palette.length; i++) {
			if (palette[i] == 0)
			{
				mixingPalette.add(0, 0);
				continue;
			}
			count = centroids[3][i];
			if(count == 0) continue;
			mix = MathUtils.clamp((int)(centroids[0][i] / count + 0.5f), 0, 31) << 10
					| MathUtils.clamp((int)(centroids[1][i] / count + 0.5f), 0, 31) << 5
					| MathUtils.clamp((int)(centroids[2][i] / count + 0.5f), 0, 31);
			mixingPalette.add(CIELABConverter.puff(mix));
		}
		mixPalette(true);
		/*
		Collections.sort(mixingPalette, hueComparator);
		palette = new int[mixingPalette.size()];
		for (int i = 0; i < palette.length; i++) {
			palette[i] = mixingPalette.get(i);
		}
		 */
		return this.palette;
	}

	public int[] lloydCentral(int[] palette) {
		PaletteReducer pr = new PaletteReducer(palette, oklabMetric);
		float[][] centroids = new float[4][palette.length];
		byte[] pm = pr.paletteMapping;
		int index, mix;
		float count, count2;
		for (int i = 0; i < 0x8000; i++) {
			index = pm[i] & 0xFF;
			mix = CIELABConverter.shrink(palette[index]);
			centroids[0][index] += PaletteReducer.labs[0][mix];
			centroids[1][index] += PaletteReducer.labs[1][mix];
			centroids[2][index] += PaletteReducer.labs[2][mix];
			centroids[3][index]++;
		}
		state = Arrays.hashCode(palette);
		mixingPalette.clear();
		for (int i = 0; i < palette.length; i++) {
			if (palette[i] == 0)
			{
				mixingPalette.add(0, 0);
				continue;
			}
			count = centroids[3][i];
			if(count == 0) continue;
			count2 = count * 0.9375f;
//			mix = MathUtils.clamp((int)(centroids[0][i] / count + 0.5f), 0, 31) << 10
//					| MathUtils.clamp((int)(centroids[1][i] / count + 0.5f), 0, 31) << 5
//					| MathUtils.clamp((int)(centroids[2][i] / count + 0.5f), 0, 31);
//			mixingPalette.add(CIELABConverter.puff(mix));
//			double l = PaletteReducer.lab15[0][mix], 
//					a = PaletteReducer.lab15[1][mix] * 0.8, 
//					b = PaletteReducer.lab15[2][mix] * 0.8;
			mixingPalette.add(CIELABConverter.rgba8888(centroids[0][i] / count, 
					centroids[1][i] / count2,
					centroids[2][i] / count2));
		}
		Collections.sort(mixingPalette, hueComparator);
		palette = new int[mixingPalette.size()];
		for (int i = 0; i < palette.length; i++) {
			//mixingPalette.set(i, FloatColorTools.floatToInt(FloatColorTools.toEditedFloat(FloatColorTools.floatGet(mixingPalette.get(i)), 0f, -0.25f, 0f, 0f)));
			palette[i] = mixingPalette.get(i);
		}
		return palette;
	}

	private int[] emphasize (int[] palette) {
		int rd = 1, gd = 1, bd = 1;
		for (int i = 0; i < palette.length; i++) {
			int color = palette[i];
			if((color & 0x80) == 0)
				continue;
			rd = Math.max(Math.abs((color >>> 23 & 0x1FE) - 255), rd);
			gd = Math.max(Math.abs((color >>> 15 & 0x1FE) - 255), gd);
			bd = Math.max(Math.abs((color >>> 7  & 0x1FE) - 255), bd);
		}
		for (int i = 0; i < palette.length; i++) {
			int color = palette[i];
			if((color & 0x80) == 0)
				continue;
			int r = ((color >>> 23 & 0x1FE) - 255) * 255 / rd + 255 >>> 1;
			int g = ((color >>> 15 & 0x1FE) - 255) * 255 / gd + 255 >>> 1;
			int b = ((color >>> 7  & 0x1FE) - 255) * 255 / bd + 255 >>> 1;
			palette[i] = r << 24 | g << 16 | b << 8 | 0xFF;
		}
		return palette;
	}
	
	public int[] modify(int[] palette, float saturation, float brightness, float jiggle) {
		mixingPalette.clear();
		for (int i = 0; i < palette.length; i++) {
			if (palette[i] == 0)
			{
				mixingPalette.add(0, 0);
				continue;
			}
			float c = FloatColorTools.floatGet(palette[i]);
			int ic = FloatColorTools.floatToInt(FloatColorTools.toEditedFloat(c, nextCurvedFloat() * jiggle, saturation + nextCurvedFloat() * jiggle,
				brightness + nextCurvedFloat() * jiggle, 0f));
			//System.out.printf("0x%08X -> 0x%08X\n", palette[i], ic);
			mixingPalette.add(ic);
		}
		mixPalette(true);
//		Collections.sort(mixingPalette, hueComparator);
//		for (int i = 0; i < palette.length; i++) {
//			palette[i] = mixingPalette.get(i);
//		}
		return palette;
	}

	public int[] distribute(int[] palette) {
		mixingPalette.clear();
		int count = palette.length - 1;
		for (int i = 0; i < palette.length; i++) {
			if (palette[i] == 0)
			{
				mixingPalette.add(0, 0);
				count--;
				continue;
			}
			mixingPalette.add(palette[i]);
		}
		Collections.sort(mixingPalette, lightnessComparator);
		float lightness = 0f, inc = 1f / count;
		int rgba;
		Color color = new Color();
		for (int i = 0; i < palette.length; i++) {
			if((rgba = mixingPalette.get(i)) == 0)
			{
				continue;
			}
			color.set(rgba);
			float hue = NamedColor.hue(color);
			float sat = NamedColor.saturation(color);
			color.fromHsv(hue * 360f, sat, (float)Math.cbrt(lightness));
			lightness += inc;
			mixingPalette.set(i, Color.rgba8888(color));
		}
		Collections.sort(mixingPalette, hueComparator);
		for (int i = 0; i < palette.length; i++) {
			palette[i] = mixingPalette.get(i);
		}
		return palette;
	}

	public void loadPalette(String name) {
		try {
			String text = Gdx.files.absolute(name).readString();
			int start = 0, end = 6, len = text.length();
			int gap = (text.charAt(7) == '\n') ? 8 : 7;
			int sz = ((len + 2) / gap);
			for (int i = 0; i < sz; i++) {
				mixingPalette.add(StringKit.intFromHex(text, start, end) << 8 | 0xFF);
				start += gap;
				end += gap;
			}
		} catch (GdxRuntimeException e) {
			e.printStackTrace();
		}
	}
	
	public void mixPalette (boolean doRemove){
		ArrayList<CIELABConverter.Lab> labs = new ArrayList<>(mixingPalette.size());
		IntSet removalSet = new IntSet(16);
		for (int i = 0; i < mixingPalette.size(); i++) {
			labs.add(new CIELABConverter.Lab(mixingPalette.get(i)));
		}
		for (int i = 0; i < labs.size(); i++) {
			for (int j = i + 1; j < labs.size(); j++) {
				if((labs.get(i).alpha > 0.0 && labs.get(j).alpha > 0.0) && CIELABConverter.delta(labs.get(i), labs.get(j), 1.0, 1.5, 1.5) <= 100) {
					if (doRemove) {
						removalSet.add(i);
						removalSet.add(j);
						System.out.printf("Combined 0x%08X and 0x%08X\n", mixingPalette.get(i), mixingPalette.get(j));
						mixingPalette.add(Coloring.mixEvenly(mixingPalette.get(i), mixingPalette.get(j)));
					}
					else {
						System.out.printf("0x%08X and 0x%08X are very close!\n", mixingPalette.get(i), mixingPalette.get(j));
					}
				}
			}
		}
		if(doRemove) {
			IntArray removalIndices = removalSet.iterator().toArray();
			removalIndices.sort();
			for (int i = removalIndices.size - 1; i >= 0; i--) {
				mixingPalette.remove(removalIndices.get(i));
			}
		}
		
		mixingPalette.sort(hueComparator);
		
		palette = new int[mixingPalette.size()];
		for (int i = 0; i < palette.length; i++) {
			palette[i] = mixingPalette.get(i);
		}
		mixingPalette.clear();
		StringBuilder sb = new StringBuilder(palette.length * 12);
		StringKit.appendHex(sb.append("0x"), palette[0]);
		for (int i = 1; i < palette.length; i++) {
			if((i & 15) == 0)
				StringKit.appendHex(sb.append(",\n0x"), palette[i]);
			else
				StringKit.appendHex(sb.append(", 0x"), palette[i]);
		}
		System.out.println(sb);
		System.out.println(palette.length + " colors used.");
		colorizer = Colorizer.arbitraryLABColorizer(palette);
		cubePix = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
		cubeTextures = new Texture[palette.length];
		for (int i = 0; i < palette.length; i++) {
			cubeTextures[i] = new Texture(16, 16, Pixmap.Format.RGBA8888);
		}
		int idx = 0;
		if(palette[0] == 0)
		{
			System.out.println("\n0x00, 0x00, 0x00, 0x00,");
			idx++;
		}
		for (; idx < palette.length; idx++) {
			System.out.printf("0x%02X, 0x%02X, 0x%02X, 0x%02X,\n", colorizer.colorize((byte)idx, -2), colorizer.colorize((byte)idx, -1), idx, colorizer.colorize((byte)idx, 1));
		}

	}
	
	public void create () {
		int[] haltonite = new int[]{
				0x00000000,
				0x000000FF, 0x141414FF, 0x282828FF, 0x291710FF, 0x1E222AFF, 0x3B3B3BFF, 0x434531FF, 0x4F4F4FFF,
				0x5C5C5CFF, 0x6A6A6AFF, 0x56626FFF, 0x787878FF, 0x878787FF, 0x999999FF, 0xAAAAAAFF, 0xBBBBBBFF,
				0xC2BBA9FF, 0xCCCCCCFF, 0xDDDDDDFF, 0xEEEEEEFF, 0xFFFFFFFF, 0xE31515FF, 0x5B0606FF, 0xF23C37FF,
				0xDA938FFF, 0xB41808FF, 0xFE1D02FF, 0xFFBBB2FF, 0x3F1C16FF, 0xF4A293FF, 0xC0422AFF, 0x5B2E24FF,
				0xB46B58FF, 0xF2825BFF, 0xC2836DFF, 0xA7593AFF, 0xE06635FF, 0xD35521FF, 0x793210FF, 0xA44210FF,
				0xCF794CFF, 0xFF9A60FF, 0xF8B891FF, 0x7E5030FF, 0xAE9784FF, 0x86694EFF, 0x502B06FF, 0xEA7B0BFF,
				0xEDD4B9FF, 0xDFAC73FF, 0x794811FF, 0xBB7213FF, 0x7B603CFF, 0xDFA24BFF, 0xE3971BFF, 0xFFC24FFF,
				0xF2CF80FF, 0x906D1DFF, 0xB0871FFF, 0xFDE5A7FF, 0xEDB71AFF, 0xA19150FF, 0xEDE389FF, 0xF4E54FFF,
				0x83804EFF, 0xBEBB4EFF, 0xE6E21DFF, 0xA4A222FF, 0xABAC7CFF, 0xF5FE22FF, 0xA5A94CFF, 0xF6FF95FF,
				0xBFD22EFF, 0xECFE65FF, 0xF2FAC2FF, 0x9BBD25FF, 0xB7C87DFF, 0xC1E367FF, 0x7A9633FF, 0xB8FA35FF,
				0x8FD111FF, 0x64852CFF, 0x223308FF, 0x496B1CFF, 0x88ED12FF, 0x42582CFF, 0x264509FF, 0x397E0EFF,
				0xB1FB85FF, 0xC5F8ABFF, 0x3DBB14FF, 0x68915EFF, 0xA0DE92FF, 0x47723EFF, 0x6AEF59FF, 0x71FE60FF,
				0x11560BFF, 0x78AE77FF, 0x1E9922FF, 0x1FA92AFF, 0x61C56DFF, 0x16D72EFF, 0x0AE930FF, 0x48A75CFF,
				0x3EDB6AFF, 0x24BC57FF, 0x71CB92FF, 0x4FF990FF, 0x35E986FF, 0x169355FF, 0x9BE8C3FF, 0x9EFFD1FF,
				0x68B69CFF, 0x10EBAFFF, 0x549787FF, 0x14D7ADFF, 0x1FAE9AFF, 0x117F76FF, 0x4DF7ECFF, 0xB1FEFAFF,
				0x124D4AFF, 0x69D4D3FF, 0x25E5F4FF, 0x75BFC7FF, 0x2DB2CEFF, 0x1B90B4FF, 0x1C5C7DFF, 0x469AC6FF,
				0x244E65FF, 0xAADAF7FF, 0x1E9CFBFF, 0x3D79ADFF, 0x8899AAFF, 0x83A3C3FF, 0x76B6F8FF, 0x5AA7FEFF,
				0x406697FF, 0x4282E6FF, 0x2C69D4FF, 0x1662FCFF, 0x184BB9FF, 0x1F439BFF, 0x081236FF, 0xB2C1FAFF,
				0x031050FF, 0x1127ABFF, 0x7C82C2FF, 0x090B23FF, 0x8E94E8FF, 0x3538F9FF, 0x4F50C5FF, 0x7B7CFCFF,
				0x2424EDFF, 0x1D16D5FF, 0x3B3962FF, 0x201693FF, 0x6D63C8FF, 0x3E3486FF, 0xB6B1D2FF, 0x2E1E6EFF,
				0x7E64E4FF, 0x7147FEFF, 0xB39CF5FF, 0x4413BFFF, 0x4F15DAFF, 0x260E5DFF, 0x5D3BAAFF, 0x6625E1FF,
				0x5A4382FF, 0x7A42DBFF, 0x480C8BFF, 0xAC6BF1FF, 0x8C6BA9FF, 0x680AA7FF, 0x8420C6FF, 0xAC29F6FF,
				0xB380C7FF, 0x650C83FF, 0x55096CFF, 0x875695FF, 0xCA4EECFF, 0xE994FDFF, 0x7E3E8CFF, 0xD086E0FF,
				0x9D36B1FF, 0xB53FC6FF, 0xD769E6FF, 0x7B1A88FF, 0xD308EDFF, 0x9604A4FF, 0x500153FF, 0xFBBAFAFF,
				0xA284A1FF, 0xB767B3FF, 0xFE43F2FF, 0xC221B4FF, 0xB00D9FFF, 0x32042DFF, 0xF925DFFF, 0xFC73E9FF,
				0xE90FCAFF, 0xD44ABEFF, 0xE25EC4FF, 0xF8ACE4FF, 0xA31479FF, 0xAB5692FF, 0xE31BA2FF, 0x9D3F7CFF,
				0x8C135FFF, 0x6B3858FF, 0x4B0932FF, 0xF372C1FF, 0xCC0E7EFF, 0x71174CFF, 0xF645A3FF, 0xC03E83FF,
				0xD55193FF, 0xF095C2FF, 0x893A5FFF, 0xAB134FFF, 0xE83175FF, 0xF93E83FF, 0xB46D86FF, 0xA05B73FF,
				0xE71A58FF, 0x22040DFF, 0xD27E96FF, 0xBC3E61FF, 0xD51144FF, 0xFE7B9AFF, 0xFE6885FF, 0xDF6A7FFF,
				0x8C525CFF, 0xA83F4BFF, 0x76121CFF, 0xD34B57FF, 0xC21A26FF, 0x87353AFF, 0x981C20FF, 0xEA5A5EFF,

//				0x48384840, 0x50405040, 0x58485840, 0x60506040, 0x68586840, 0x70607040, 0x78687840,
//				0x80708040, 0x88788840, 0x90809040, 0x98889840, 0xA070A040, 0xA860A840, 0xB050B040, 0xB840B840,
		};

				int[] haltoniteBad = new int[]{
				0x00000000, /*0x48384840, 0x50405040, 0x58485840, 0x60506040, 0x68586840, 0x70607040, 0x78687840,
				0x80708040, 0x88788840, 0x90809040, 0x98889840, 0xA070A040, 0xA860A840, 0xB050B040, 0xB840B840,*/
				0x080008FF, 0x080818FF, 0x211010FF, 0x183118FF, 0x102131FF, 0x080831FF, 0x21424AFF, 0x5A525AFF,
				0x6B7B7BFF, 0x948C84FF, 0xA5A59CFF, 0xADADBDFF, 0xADC6ADFF, 0xC6D6CEFF, 0xD6B5CEFF, 0xF72129FF,
				0xEFDED6FF, 0xEFEFEFFF, 0xDEEFCEFF, 0xD6CEEFFF, 0xD6E7F7FF, 0xF7CEEFFF, 0xF7F7F7FF, 0xDEFFF7FF,
				0xFFFFF7FF, 0xEFFFDEFF, 0x9C1010FF, 0xA56B6BFF, 0x631010FF, 0xAD1818FF, 0xE71818FF, 0x7B1818FF,
				0xBD2110FF, 0xC63118FF, 0xCE7B6BFF, 0xD64A21FF, 0xE7947BFF, 0xF7734AFF, 0xEF5221FF, 0xDEAD9CFF,
				0x8C3918FF, 0xA54A21FF, 0x4A2918FF, 0xEF6B18FF, 0xC66321FF, 0xE7C6ADFF, 0xEF9C5AFF, 0xE78429FF,
				0xC67B29FF, 0xEFBD84FF, 0xEF9421FF, 0xA57331FF, 0xF7DEB5FF, 0x5A4218FF, 0xEFBD63FF, 0xF7B521FF,
				0x7B6321FF, 0xB5A573FF, 0xB59421FF, 0xDEB521FF, 0xEFD67BFF, 0xC6A529FF, 0xEFCE29FF, 0xF7E729FF,
				0x7B7B31FF, 0xBDBD42FF, 0xF7F7B5FF, 0xF7FF21FF, 0xDEE75AFF, 0xEFF77BFF, 0xF7FF94FF, 0x8C9439FF,
				0xC6CE73FF, 0xCEDE29FF, 0xEFFF5AFF, 0xE7FF18FF, 0xD6EF21FF, 0xCEFF18FF, 0x8CB529FF, 0x9CCE29FF,
				0xC6FF5AFF, 0xA5E729FF, 0xCEF784FF, 0xCEE7A5FF, 0xADFF29FF, 0x639C21FF, 0xCEF7A5FF, 0x73E721FF,
				0x7BFF21FF, 0x8CC673FF, 0xD6FFC6FF, 0x94F773FF, 0x39C610FF, 0x215218FF, 0x31DE18FF, 0x296B21FF,
				0xA5F79CFF, 0x94DE8CFF, 0x398431FF, 0x219418FF, 0x21AD18FF, 0x29B521FF, 0x29CE21FF, 0x29F721FF,
				0x187B18FF, 0x4AF75AFF, 0x31C642FF, 0x29E74AFF, 0x42DE6BFF, 0x31AD5AFF, 0x29EF6BFF, 0xADF7C6FF,
				0x39C66BFF, 0x399C63FF, 0x31F78CFF, 0x5AF7A5FF, 0x29EFADFF, 0x73DEBDFF, 0x31D6A5FF, 0x31BD94FF,
				0x6BBDA5FF, 0x7BF7D6FF, 0x297363FF, 0x29F7D6FF, 0x299C8CFF, 0xB5F7EFFF, 0x39F7F7FF, 0x215A5AFF,
				0x29D6E7FF, 0x9CE7EFFF, 0x29B5C6FF, 0x73D6E7FF, 0x6B9CA5FF, 0x2994ADFF, 0x39B5EFFF, 0x297BA5FF,
				0x319CD6FF, 0xA5CEE7FF, 0x21638CFF, 0x3194EFFF, 0x2984DEFF, 0x84B5E7FF, 0x426BADFF, 0x294A84FF,
				0x103984FF, 0x1852BDFF, 0x2163EFFF, 0x7B8CBDFF, 0x7B8CEFFF, 0x2942DEFF, 0xB5BDEFFF, 0x1829A5FF,
				0x08108CFF, 0x636BE7FF, 0x5A5AC6FF, 0x18185AFF, 0x1818E7FF, 0x08084AFF, 0x08086BFF, 0x101073FF,
				0x1010BDFF, 0x31316BFF, 0x18109CFF, 0x6B4AEFFF, 0xAD9CEFFF, 0x3918B5FF, 0x291084FF, 0x4A18E7FF,
				0x52319CFF, 0x6B4A9CFF, 0x6B18DEFF, 0x9C5AEFFF, 0x39106BFF, 0x521094FF, 0x946BBDFF, 0x210839FF,
				0x8418E7FF, 0x6B18B5FF, 0xAD84CEFF, 0xBD73E7FF, 0xA518EFFF, 0x52186BFF, 0x6B108CFF, 0xAD39D6FF,
				0x9442ADFF, 0xD69CE7FF, 0xC621EFFF, 0xA510C6FF, 0xD65AEFFF, 0x9410ADFF, 0x42104AFF, 0x841894FF,
				0xE784EFFF, 0x6B186BFF, 0xC618C6FF, 0xEFB5EFFF, 0xF794F7FF, 0xF763F7FF, 0xEF21EFFF, 0xD642C6FF,
				0x8C5284FF, 0xB5189CFF, 0xD618B5FF, 0xEF18C6FF, 0xCE5AB5FF, 0xB5429CFF, 0xF74ACEFF, 0xA5187BFF,
				0xEF6BC6FF, 0xCE108CFF, 0x84105AFF, 0xEF189CFF, 0x941863FF, 0xF794CEFF, 0x631842FF, 0xD62184FF,
				0xBD106BFF, 0xC684A5FF, 0xC66B94FF, 0xEF4A94FF, 0xAD396BFF, 0xEF186BFF, 0x9C1042FF, 0xF76B9CFF,
				0xD6185AFF, 0x421021FF, 0xC64A73FF, 0xC6104AFF, 0xEF84A5FF, 0x73394AFF, 0xB51039FF, 0xDE2142FF,
				0x9C394AFF, 0xF7BDC6FF, 0xEF4A63FF, 0xEF9CA5FF, 0xBD424AFF, 0x8C1018FF, 0xEF6B73FF, 0xCE1018FF,
	};
		int[] haltfire = new int[]{
				0x00000000, 0x48384840, 0x50405040, 0x58485840, 0x60506040, 0x68586840, 0x70607040, 0x78687840,
				0x80708040, 0x88788840, 0x90809040, 0x98889840, 0xA070A040, 0xA860A840, 0xB050B040, 0xB840B840,
				0x000000FF, 0x071314FF, 0x141E09FF, 0x081021FF, 0x102929FF, 0x291919FF, 0x292136FF, 0x233539FF,
				0x393939FF, 0x423131FF, 0x422931FF, 0x52525AFF, 0x6B6B63FF, 0x5A6373FF, 0x8C9484FF, 0x9C848CFF,
				0xA89C9CFF, 0xA5ADBDFF, 0xADBDC6FF, 0xD6CECEFF, 0xD6BDCEFF, 0xDEE7EFFF, 0xEFEFEFFF, 0xE7FFE7FF,
				0xFFFFFFFF, 0x422121FF, 0xBD6363FF, 0x390808FF, 0xC63129FF, 0xF7948CFF, 0xF73121FF, 0x73524AFF,
				0xE75A31FF, 0xF76B42FF, 0xD64A21FF, 0xA53918FF, 0xD68C73FF, 0xF7845AFF, 0x843110FF, 0xF7CEBDFF,
				0xEF5210FF, 0x632910FF, 0xC67B5AFF, 0xE79C7BFF, 0xB59484FF, 0xF76310FF, 0xF7BD9CFF, 0xAD5A29FF,
				0x9C735AFF, 0xEFB58CFF, 0xF79C5AFF, 0xDE7B31FF, 0xF77B18FF, 0x8C6342FF, 0x845229FF, 0xBD6318FF,
				0xEF8429FF, 0xC67321FF, 0x4A2908FF, 0xDE9C52FF, 0xE7AD6BFF, 0xFF9418FF, 0xF7B563FF, 0x945A10FF,
				0x734A10FF, 0xCE8418FF, 0xC6B59CFF, 0xEFAD4AFF, 0xEFCE9CFF, 0xBD9452FF, 0xE7A539FF, 0xDE9418FF,
				0xFFAD21FF, 0x946B18FF, 0x7B6B4AFF, 0xB58418FF, 0xE7AD18FF, 0xEFCE73FF, 0xCEBD8CFF, 0xEFC64AFF,
				0x312910FF, 0x6B634AFF, 0x7B6318FF, 0xEFBD18FF, 0xE7DEBDFF, 0x947B18FF, 0xF7E7A5FF, 0xCEAD21FF,
				0xEFC618FF, 0xA5944AFF, 0xA58C18FF, 0x4A4218FF, 0x847B4AFF, 0xEFD621FF, 0x524A10FF, 0xF7E763FF,
				0xBDB56BFF, 0xC6B510FF, 0xF7E731FF, 0xADA518FF, 0xEFEF21FF, 0xADAD5AFF, 0xFEFE0CFF, 0x9C9C7BFF,
				0xEFEFCEFF, 0xBDBD39FF, 0xE7E78CFF, 0x6B7310FF, 0xD6E721FF, 0xEFFF52FF, 0xE7FF21FF, 0xDEEF6BFF,
				0x5A6321FF, 0xEFFF8CFF, 0xB5D621FF, 0xA5AD84FF, 0xC6D684FF, 0x9CB542FF, 0xCEFF29FF, 0x848C6BFF,
				0x739418FF, 0xBDE75AFF, 0x8CBD21FF, 0x8CA55AFF, 0xADCE6BFF, 0x7BAD18FF, 0xE7FFBDFF, 0xADFF21FF,
				0x5A8418FF, 0x739442FF, 0x9CF721FF, 0x426B10FF, 0x8CDE29FF, 0x73D621FF, 0x5A7B42FF, 0x73F718FF,
				0xA5F773FF, 0x5AB539FF, 0x102908FF, 0x7BF75AFF, 0xADEF9CFF, 0x295221FF, 0x31BD18FF, 0x104208FF,
				0x219410FF, 0x186310FF, 0x29EF18FF, 0x187B10FF, 0x21AD18FF, 0x21D618FF, 0x29FF21FF, 0x0C300CFF,
				0x739C73FF, 0x29C629FF, 0xB5FFB5FF, 0x4AF74AFF, 0x085210FF, 0x6BDE7BFF, 0x219431FF, 0x184A21FF,
				0x217331FF, 0x29E75AFF, 0x183921FF, 0x18BD4AFF, 0x298C4AFF, 0x21AD52FF, 0x39C66BFF, 0x29FF7BFF,
				0xADD6BDFF, 0x29A55AFF, 0x31EF94FF, 0x217352FF, 0x42B58CFF, 0x218463FF, 0x84B5A5FF, 0x216352FF,
				0x31947BFF, 0x63E7C6FF, 0x31F7C6FF, 0x29D6B5FF, 0x8CF7E7FF, 0x39C6BDFF, 0x297B7BFF, 0x31A5B5FF,
				0x4AD6EFFF, 0x104252FF, 0xC6DEE7FF, 0x216B8CFF, 0x6B8C9CFF, 0x29526BFF, 0xADD6F7FF, 0x4AA5EFFF,
				0x102131FF, 0x637384FF, 0x8C9CADFF, 0x397BD6FF, 0x31426BFF, 0x315ACEFF, 0x102152FF, 0x080829FF,
				0x18187BFF, 0x2118A5FF, 0x3121D6FF, 0x100842FF, 0xB5A5EFFF, 0x7B39D6FF, 0xA58CC6FF, 0x0E001CFF,
				0x39105AFF, 0x8C6B9CFF, 0x8418B5FF, 0xC639E7FF, 0x631873FF, 0xEFC6F7FF, 0x5A3152FF, 0xEF73D6FF,
				0xC6189CFF, 0xEF29B5FF, 0x420831FF, 0xA54284FF, 0x941863FF, 0x6B1842FF, 0xF7B5D6FF, 0x210010FF,
				0xD64A8CFF, 0xE79CBDFF, 0xF7A5C6FF, 0xDE1863FF, 0x94526BFF, 0xCE7394FF, 0xF72163FF, 0xDE8CA5FF,
				0xC6184AFF, 0x6B424AFF, 0x841021FF, 0xEF6373FF, 0xAD1021FF, 0x5A0810FF, 0xFF1021FF, 0xE71821FF,
		};
		palette = haltonite;

//				new int[]{
//				0x00000000, 0x000000FF, 0x071314FF, 0x141E09FF, 0x081021FF, 0x102929FF, 0x291919FF, 0x292136FF, 0x233539FF, 0x393939FF, 0x423131FF, 0x422931FF, 0x52525AFF, 0x6B6B63FF, 0x5A6373FF, 0x8C9484FF,
//				0x9C848CFF, 0xA89C9CFF, 0xA5ADBDFF, 0xADBDC6FF, 0xD6CECEFF, 0xD6BDCEFF, 0xDEE7EFFF, 0xEFEFEFFF, 0xE7FFE7FF, 0xFFFFFFFF, 0x422121FF, 0xBD6363FF, 0x390808FF, 0xC63129FF, 0xF7948CFF, 0xF73121FF,
//				0x73524AFF, 0xE75A31FF, 0xF76B42FF, 0xD64A21FF, 0xA53918FF, 0xD68C73FF, 0xF7845AFF, 0x843110FF, 0xF7CEBDFF, 0xEF5210FF, 0x632910FF, 0xC67B5AFF, 0xE79C7BFF, 0xB59484FF, 0xF76310FF, 0xF7BD9CFF,
//				0xAD5A29FF, 0x9C735AFF, 0xEFB58CFF, 0xF79C5AFF, 0xDE7B31FF, 0xF77B18FF, 0x8C6342FF, 0x845229FF, 0xBD6318FF, 0xEF8429FF, 0xC67321FF, 0x4A2908FF, 0xDE9C52FF, 0xE7AD6BFF, 0xFF9418FF, 0xF7B563FF,
//				0x945A10FF, 0x734A10FF, 0xCE8418FF, 0xC6B59CFF, 0xEFAD4AFF, 0xEFCE9CFF, 0xBD9452FF, 0xE7A539FF, 0xDE9418FF, 0xFFAD21FF, 0x946B18FF, 0x7B6B4AFF, 0xB58418FF, 0xE7AD18FF, 0xEFCE73FF, 0xCEBD8CFF,
//				0xEFC64AFF, 0x312910FF, 0x6B634AFF, 0x7B6318FF, 0xEFBD18FF, 0xE7DEBDFF, 0x947B18FF, 0xF7E7A5FF, 0xCEAD21FF, 0xEFC618FF, 0xA5944AFF, 0xA58C18FF, 0x4A4218FF, 0x847B4AFF, 0xEFD621FF, 0x524A10FF,
//				0xF7E763FF, 0xBDB56BFF, 0xC6B510FF, 0xF7E731FF, 0xADA518FF, 0xEFEF21FF, 0xADAD5AFF, 0xFEFE0CFF, 0x9C9C7BFF, 0xEFEFCEFF, 0xBDBD39FF, 0xE7E78CFF, 0x6B7310FF, 0xD6E721FF, 0xEFFF52FF, 0xE7FF21FF,
//				0xDEEF6BFF, 0x5A6321FF, 0xEFFF8CFF, 0xB5D621FF, 0xA5AD84FF, 0xC6D684FF, 0x9CB542FF, 0xCEFF29FF, 0x848C6BFF, 0x739418FF, 0xBDE75AFF, 0x8CBD21FF, 0x8CA55AFF, 0xADCE6BFF, 0x7BAD18FF, 0xE7FFBDFF,
//				0xADFF21FF, 0x5A8418FF, 0x739442FF, 0x9CF721FF, 0x426B10FF, 0x8CDE29FF, 0x73D621FF, 0x5A7B42FF, 0x73F718FF, 0xA5F773FF, 0x5AB539FF, 0x102908FF, 0x7BF75AFF, 0xADEF9CFF, 0x295221FF, 0x31BD18FF,
//				0x104208FF, 0x219410FF, 0x186310FF, 0x29EF18FF, 0x187B10FF, 0x21AD18FF, 0x21D618FF, 0x29FF21FF, 0x0C300CFF, 0x739C73FF, 0x29C629FF, 0xB5FFB5FF, 0x4AF74AFF, 0x085210FF, 0x6BDE7BFF, 0x219431FF,
//				0x184A21FF, 0x217331FF, 0x29E75AFF, 0x183921FF, 0x18BD4AFF, 0x298C4AFF, 0x21AD52FF, 0x39C66BFF, 0x29FF7BFF, 0xADD6BDFF, 0x29A55AFF, 0x31EF94FF, 0x217352FF, 0x42B58CFF, 0x218463FF, 0x84B5A5FF,
//				0x216352FF, 0x31947BFF, 0x63E7C6FF, 0x31F7C6FF, 0x29D6B5FF, 0x8CF7E7FF, 0x39C6BDFF, 0x297B7BFF, 0x31A5B5FF, 0x4AD6EFFF, 0x104252FF, 0xC6DEE7FF, 0x216B8CFF, 0x6B8C9CFF, 0x29526BFF, 0xADD6F7FF,
//				0x4AA5EFFF, 0x102131FF, 0x637384FF, 0x8C9CADFF, 0x397BD6FF, 0x31426BFF, 0x315ACEFF, 0x102152FF, 0x080829FF, 0x18187BFF, 0x2118A5FF, 0x3121D6FF, 0x100842FF, 0xB5A5EFFF, 0x7B39D6FF, 0xA58CC6FF,
//				0x0E001CFF, 0x39105AFF, 0x8C6B9CFF, 0x8418B5FF, 0xC639E7FF, 0x631873FF, 0xEFC6F7FF, 0x5A3152FF, 0xEF73D6FF, 0xC6189CFF, 0xEF29B5FF, 0x420831FF, 0xA54284FF, 0x941863FF, 0x6B1842FF, 0xF7B5D6FF,
//				0x210010FF, 0xD64A8CFF, 0xE79CBDFF, 0xF7A5C6FF, 0xDE1863FF, 0x94526BFF, 0xCE7394FF, 0xF72163FF, 0xDE8CA5FF, 0xC6184AFF, 0x6B424AFF, 0x841021FF, 0xEF6373FF, 0xAD1021FF, 0x5A0810FF, 0xFF1021FF,
//				0xE71821FF
//		};
//		palette = Coloring.MANOSSUS256;
//		palette = new int[]{
//				0x00000000, 0x1A1621FE, 0x202920FE, 0x38391AFE, 0x2F4A4AFE, 0x3B455AFE, 0x335A33FE, 0x636363FE, 0x734F6DFE, 0x94837FFE, 0x9C9C9CFE, 0xAD85A8FE, 0xD6D6D6FE, 0xDEC7C0FE, 0xE7E7E7FE, 0xE1DBE7FE,
//				0xF7F7F7FE, 0x632A2AFE, 0xEF554FFE, 0xBD4B45FE, 0xE79888FE, 0xE78546FE, 0x84583BFE, 0xAD7B43FE, 0xE7A857FE, 0xD6C695FE, 0xDECB55FE, 0x9C964FFE, 0xDCEF5FFE, 0x9CB54DFE, 0xD9EFBAFE, 0xA2DE65FE,
//				0x4E7337FE, 0x49943DFE, 0x53BD4DFE, 0x65EF5FFE, 0x5BAD79FE, 0x70E7A4FE, 0x497B64FE, 0x80C6A6FE, 0x78EFDCFE, 0x66B5A9FE, 0x558E94FE, 0x78D4E7FE, 0x74B9D6FE, 0x41697BFE, 0x80ADE7FE, 0x6689BDFE,
//				0x4F619CFE, 0x767BDEFE, 0x2C2673FE, 0x503CBDFE, 0x6646E7FE, 0x563D94FE, 0xB557E7FE, 0x3D1E4AFE, 0xD7A1E7FE, 0x9841A5FE, 0xE757D4FE, 0x8C3466FE, 0xBD4584FE, 0xE7578FFE, 0xE7A9C1FE, 0x9C363DFE
//		};
		mixingPalette = new ArrayList<>(256);
		for (int i = 0; i < palette.length; i++) {
			mixingPalette.add(palette[i]);
		}
		mixPalette(false);
//		mixingPalette = IntArray.with(
//			0x060608ff, 0x141013ff, 0x3b1725ff, 0x73172dff, 0xb4202aff, 0xdf3e23ff, 0xfa6a0aff, 0xf9a31bff,
//			0xffd541ff, 0xfffc40ff, 0xd6f264ff, 0x9cdb43ff, 0x59c135ff, 0x14a02eff, 0x1a7a3eff, 0x24523bff,
//			0x122020ff, 0x143464ff, 0x285cc4ff, 0x249fdeff, 0x20d6c7ff, 0xa6fcdbff, 0xffffffff, 0xfef3c0ff,
//			0xfad6b8ff, 0xf5a097ff, 0xe86a73ff, 0xbc4a9bff, 0x793a80ff, 0x403353ff, 0x242234ff, 0x221c1aff,
//			0x322b28ff, 0x71413bff, 0xbb7547ff, 0xdba463ff, 0xf4d29cff, 0xdae0eaff, 0xb3b9d1ff, 0x8b93afff,
//			0x6d758dff, 0x4a5462ff, 0x333941ff, 0x422433ff, 0x5b3138ff, 0x8e5252ff, 0xba756aff, 0xe9b5a3ff,
//			0xe3e6ffff, 0xb9bffbff, 0x849be4ff, 0x588dbeff, 0x477d85ff, 0x23674eff, 0x328464ff, 0x5daf8dff,
//			0x92dcbaff, 0xcdf7e2ff, 0xe4d2aaff, 0xc7b08bff, 0xa08662ff, 0x796755ff, 0x5a4e44ff, 0x423934ff,
//			0x141414ff, 0x383838ff, 0x67615fff, 0xa69f96ff, 0xf0eeecff, 0x611732ff, 0xae2633ff, 0xe86c18ff,
//			0xf1bb3bff, 0xf26864ff, 0x4a2426ff, 0x844239ff, 0xc47540ff, 0xefb681ff, 0xd4705cff, 0x206348ff,
//			0x42ad37ff, 0xb4e357ff, 0x1a363fff, 0x20646cff, 0x2bad96ff, 0xa1e4a0ff, 0x222664ff, 0x264fa4ff,
//			0x1f95e1ff, 0x6de0e5ff, 0x431c58ff, 0x8d2f7cff, 0xe4669bff, 0xf0b4adff, 0x32384aff, 0x4f6872ff,
//			0x88a7a0ff);
		
//		palette = new int[] {
//			0x000000ff, 0x111111ff, 0x222222ff, 0x333333ff, 0x444444ff, 0x555555ff, 0x666666ff, 0x777777ff,
//			0x888888ff, 0x999999ff, 0xaaaaaaff, 0xbbbbbbff, 0xccccccff, 0xddddddff, 0xeeeeeeff, 0xffffffff,
//			0x007f7fff, 0x3fbfbfff, 0x00ffffff, 0xbfffffff, 0x8181ffff, 0x0000ffff, 0x3f3fbfff, 0x00007fff,
//			0x0f0f50ff, 0x7f007fff, 0xbf3fbfff, 0xf500f5ff, 0xfd81ffff, 0xffc0cbff, 0xff8181ff, 0xff0000ff,
//			0xbf3f3fff, 0x7f0000ff, 0x551414ff, 0x7f3f00ff, 0xbf7f3fff, 0xff7f00ff, 0xffbf81ff, 0xffffbfff,
//			0xffff00ff, 0xbfbf3fff, 0x7f7f00ff, 0x007f00ff, 0x3fbf3fff, 0x00ff00ff, 0xafffafff, 0x00bfffff,
//			0x007fffff, 0x4b7dc8ff, 0xbcafc0ff, 0xcbaa89ff, 0xa6a090ff, 0x7e9494ff, 0x6e8287ff, 0x7e6e60ff,
//			0xa0695fff, 0xc07872ff, 0xd08a74ff, 0xe19b7dff, 0xebaa8cff, 0xf5b99bff, 0xf6c8afff, 0xf5e1d2ff,
//			0x7f00ffff, 0x573b3bff, 0x73413cff, 0x8e5555ff, 0xab7373ff, 0xc78f8fff, 0xe3ababff, 0xf8d2daff,
//			0xe3c7abff, 0xc49e73ff, 0x8f7357ff, 0x73573bff, 0x3b2d1fff, 0x414123ff, 0x73733bff, 0x8f8f57ff,
//			0xa2a255ff, 0xb5b572ff, 0xc7c78fff, 0xdadaabff, 0xededc7ff, 0xc7e3abff, 0xabc78fff, 0x8ebe55ff,
//			0x738f57ff, 0x587d3eff, 0x465032ff, 0x191e0fff, 0x235037ff, 0x3b573bff, 0x506450ff, 0x3b7349ff,
//			0x578f57ff, 0x73ab73ff, 0x64c082ff, 0x8fc78fff, 0xa2d8a2ff, 0xe1f8faff, 0xb4eecaff, 0xabe3c5ff,
//			0x87b48eff, 0x507d5fff, 0x0f6946ff, 0x1e2d23ff, 0x234146ff, 0x3b7373ff, 0x64ababff, 0x8fc7c7ff,
//			0xabe3e3ff, 0xc7f1f1ff, 0xbed2f0ff, 0xabc7e3ff, 0xa8b9dcff, 0x8fabc7ff, 0x578fc7ff, 0x57738fff,
//			0x3b5773ff, 0x0f192dff, 0x1f1f3bff, 0x3b3b57ff, 0x494973ff, 0x57578fff, 0x736eaaff, 0x7676caff,
//			0x8f8fc7ff, 0xababe3ff, 0xd0daf8ff, 0xe3e3ffff, 0xab8fc7ff, 0x8f57c7ff, 0x73578fff, 0x573b73ff,
//			0x3c233cff, 0x463246ff, 0x724072ff, 0x8f578fff, 0xab57abff, 0xab73abff, 0xebace1ff, 0xffdcf5ff,
//			0xe3c7e3ff, 0xe1b9d2ff, 0xd7a0beff, 0xc78fb9ff, 0xc87da0ff, 0xc35a91ff, 0x4b2837ff, 0x321623ff,
//			0x280a1eff, 0x401811ff, 0x621800ff, 0xa5140aff, 0xda2010ff, 0xd5524aff, 0xff3c0aff, 0xf55a32ff,
//			0xff6262ff, 0xf6bd31ff, 0xffa53cff, 0xd79b0fff, 0xda6e0aff, 0xb45a00ff, 0xa04b05ff, 0x5f3214ff,
//			0x53500aff, 0x626200ff, 0x8c805aff, 0xac9400ff, 0xb1b10aff, 0xe6d55aff, 0xffd510ff, 0xffea4aff,
//			0xc8ff41ff, 0x9bf046ff, 0x96dc19ff, 0x73c805ff, 0x6aa805ff, 0x3c6e14ff, 0x283405ff, 0x204608ff,
//			0x0c5c0cff, 0x149605ff, 0x0ad70aff, 0x14e60aff, 0x7dff73ff, 0x4bf05aff, 0x00c514ff, 0x05b450ff,
//			0x1c8c4eff, 0x123832ff, 0x129880ff, 0x06c491ff, 0x00de6aff, 0x2deba8ff, 0x3cfea5ff, 0x6affcdff,
//			0x91ebffff, 0x55e6ffff, 0x7dd7f0ff, 0x08ded5ff, 0x109cdeff, 0x055a5cff, 0x162c52ff, 0x0f377dff,
//			0x004a9cff, 0x326496ff, 0x0052f6ff, 0x186abdff, 0x2378dcff, 0x699dc3ff, 0x4aa4ffff, 0x90b0ffff,
//			0x5ac5ffff, 0xbeb9faff, 0x786ef0ff, 0x4a5affff, 0x6241f6ff, 0x3c3cf5ff, 0x101cdaff, 0x0010bdff,
//			0x231094ff, 0x0c2148ff, 0x5010b0ff, 0x6010d0ff, 0x8732d2ff, 0x9c41ffff, 0xbd62ffff, 0xb991ffff,
//			0xd7a5ffff, 0xd7c3faff, 0xf8c6fcff, 0xe673ffff, 0xff52ffff, 0xda20e0ff, 0xbd29ffff, 0xbd10c5ff,
//			0x8c14beff, 0x5a187bff, 0x641464ff, 0x410062ff, 0x320a46ff, 0x551937ff, 0xa01982ff, 0xc80078ff,
//			0xff50bfff, 0xff6ac5ff, 0xfaa0b9ff, 0xfc3a8cff, 0xe61e78ff, 0xbd1039ff, 0x98344dff, 0x911437ff,
//		};
		batch = new MutantBatch();
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean keyUp(int keycode) {
				switch (keycode) {
					case Input.Keys.UNKNOWN: // to avoid PrintScreen triggering an event
					case Input.Keys.SHIFT_LEFT:
					case Input.Keys.SHIFT_RIGHT:
					case Input.Keys.CONTROL_LEFT:
					case Input.Keys.CONTROL_RIGHT:
					case Input.Keys.ALT_LEFT:
					case Input.Keys.ALT_RIGHT:
						break;
					case Input.Keys.Q:
					case Input.Keys.ESCAPE:
						Gdx.app.exit();
						break;
					case Input.Keys.S:
						SHAPE = (SHAPE == CUBE) ? BALL : CUBE;
						break;
					case Input.Keys.LEFT:
						modify(palette, -0.1f, 0f, 0f);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					case Input.Keys.RIGHT:
						modify(palette, 0.1f, 0f, 0f);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					case Input.Keys.UP:
						modify(palette, 0f, 0.1f, 0f);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					case Input.Keys.DOWN:
						modify(palette, 0f, -0.1f, 0f);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					case Input.Keys.J:
						modify(palette, 0f, 0f, 0.1f);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					case Input.Keys.D:
						palette = distribute(palette);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					case Input.Keys.L:
						palette = lloyd(palette);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					case Input.Keys.C:
						palette = lloydCentral(palette);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					case Input.Keys.E:
						emphasize(palette);
						colorizer = Colorizer.arbitraryLABColorizer(palette);
						break;
					default:
						StringBuilder sb = new StringBuilder(palette.length * 12);
						StringKit.appendHex(sb.append("0x"), palette[0]);
						for (int i = 1; i < palette.length; i++) {
							if ((i & 15) == 0)
								StringKit.appendHex(sb.append(",\n0x"), palette[i]);
							else
								StringKit.appendHex(sb.append(", 0x"), palette[i]);
						}
						System.out.println(sb);
						System.out.println(palette.length + " colors used.");
				}
				Gdx.graphics.requestRendering();
				return true;
			}
		});
		Gdx.graphics.setContinuousRendering(false);
		Gdx.graphics.requestRendering();
	}

	public void render () {
		if(cubePix == null) return;
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for (int i = 0; i < palette.length; i++) {
			if(palette[i] == 0) continue;
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {						
					cubePix.drawPixel(x, y, colorizer.dimmer(SHAPE[y][x] & 3, (byte)i));
				}
			}
			cubeTextures[i].draw(cubePix, 0, 0);
			batch.draw(cubeTextures[i], (i - 1 & 15) << 4, 240 - (i - 1 & -16), 16, 16);
		}
		batch.end();
	}

	public void resize (int width, int height) {
	}

	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Palette Reducer");
		config.setWindowedMode(16 * 16, 16 * 16);
		config.setIdleFPS(10);
		config.useVsync(true);
		final ColorizerPreview app = new ColorizerPreview();
		config.setWindowListener(new Lwjgl3WindowAdapter() {
			@Override
			public void filesDropped(String[] files) {
				if (files != null && files.length > 0) {
					app.mixingPalette.add(0);
					for (int i = 0; i < files.length; i++) {
						if (files[i].endsWith(".hex"))
							app.loadPalette(files[i]);
					}
					app.mixPalette(true);
					Gdx.graphics.requestRendering();
				}
			}
		});

		new Lwjgl3Application(app, config);
	}

}
