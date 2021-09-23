package colorweaver;

import colorweaver.tools.StringKit;
import colorweaver.tools.TrigTools;

import java.util.ArrayList;
import java.util.Comparator;

public class PaletteGuarantee {
    public static void main(String[] args){
        ArrayList<Integer> edit = new ArrayList<Integer>(256);
        int[] outer = Coloring.YAMOG255;
        for (int i = 0; i < outer.length; i++) {
            edit.add(outer[i]);
        }
        int[] guarantee = Coloring.BETTS64;
        for(int want : guarantee){
            int idx = 0;
            double dist = Double.MAX_VALUE, diff;
            for (int i = 0; i < edit.size(); i++) {
                diff = PaletteReducer.oklabCarefulMetric.difference(want, edit.get(i));
                if(diff < dist){
                    dist = diff;
                    idx = i;
                }
            }
            edit.remove(idx);
        }
        edit.sort(Comparator.comparing(i -> {
            int s = PaletteReducer.shrink(i);
            double L = PaletteReducer.OKLAB[0][s],  A = PaletteReducer.OKLAB[1][s], B = PaletteReducer.OKLAB[2][s];
            return (A * A + B * B < 0.00325 ? L : TrigTools.atan2_(A, B) + 2f);
        }));
        for (int i = 0; i < guarantee.length; i++) {
            edit.add(i, guarantee[i]);
        }
        System.out.println("new int[] {");
        for (int i = 0; i < edit.size(); i++) {
            System.out.print("0x" + StringKit.hex(edit.get(i)) + ", ");
            if((i & 7) == 7)
                System.out.println();
        }
        System.out.println("};");

    }
}
