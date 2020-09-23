package colorweaver;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Tommy Ettinger on 9/22/2020.
 */
public class CheckIPT {
	public static void main(String[] args){
		Color t = new Color();
		for(NamedColor color : NamedColor.FULL_PALETTE)
		{
			IPTConverter.IPT ipt = new IPTConverter.IPT(color);
			ipt.intoColor(t);
			System.out.printf("%30s: R=%+1.3f,G=%+1.3f,B=%+1.3f -> I=%+1.3f,P=%+1.3f,T=%+1.3f -> R=%+1.3f,G=%+1.3f,B=%+1.3f\n",
					color.name, color.r, color.g, color.b, ipt.i, ipt.p, ipt.t, t.r, t.g, t.b);
		}
	}
}
