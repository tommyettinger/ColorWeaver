package colorweaver;

import colorweaver.tools.StringKit;
import colorweaver.tools.TrigTools;

import java.util.ArrayList;
import java.util.Comparator;

public class PaletteGuarantee {
    public static void main(String[] args){
        ArrayList<Integer> edit = new ArrayList<Integer>(256);
        int[] outer = Coloring.HALTONIC255;
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
            return TrigTools.atan2_(PaletteReducer.OKLAB[1][s], PaletteReducer.OKLAB[2][s]);
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
