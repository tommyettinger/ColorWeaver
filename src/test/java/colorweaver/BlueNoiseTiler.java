package colorweaver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectIntMap;
import com.badlogic.gdx.utils.TimeUtils;

import java.nio.ByteBuffer;

/**
 * Created by Tommy Ettinger on 1/21/2018.
 */
public class BlueNoiseTiler extends ApplicationAdapter {

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Blue Noise Tiling Assembly Tool");
        config.setWindowedMode(320, 320);
        config.setIdleFPS(1);
        config.setResizable(false);
        new Lwjgl3Application(new BlueNoiseTiler(), config);
    }
//    private List<byte[]> bytes = new ArrayList<>();
//    private int counter = 0;
//    private int a = 3127, b = 31111;
//    private ImmediateModeRenderer20 render;
//    private Viewport view;
//    public void create() {
//        bytes.add(BlueNoise.RAW_NOISE);
//        render = new ImmediateModeRenderer20(320 * 320, false, true, 0);
//        view = new ScreenViewport();
//    }
//    @Override
//    public void resize(int width, int height) {
//        super.resize(width, height);
//        view.update(width, height, true);
//        view.apply(true);
//    }
//
//    @Override
//    public void render() {
//        int i = 0;
//        byte[] bt;
//        render.begin(view.getCamera().combined, GL20.GL_POINTS);
//        for (int bx = 0; bx < 5; bx++) {
//            for (int by = 0; by < 5; by++) {
//                bt = bytes.get(i++ % bytes.size());
//                for (int n = 0; n < 4096; n++) {
//                    render.color(Float.intBitsToFloat((bt[n] + 128) * 0x010101 | 0xFE000000));
//                    render.vertex(bx << 6 | n >>> 6, by << 6 | (n & 63), 0);
//                }
//            }
//        }
//        render.end();
//        if(counter++ <= 0)
//            return;
//        a = (a << 13 | a >>> 19) * 0x89A7;
//        b = (b << 17 | b >>> 15) * 0xBCFD;
//        bt = BlueNoise.generateMetropolis(a, b);
//        bytes.add(bt);
//        generatePreloadCode(bt, "blue_" + StringKit.hex(a) + "_" + StringKit.hex(b) + ".txt");
//    }
    public byte[][] bytes;
    public void create() {
        Pixmap pix;// = new Pixmap(Gdx.files.internal("BlueNoise64x64.png"));
//        System.out.println("Original image has format " + pix.getFormat());
        bytes = new byte[64][];
        for (int idx = 0; idx < 64; idx++) {
            pix = new Pixmap(Gdx.files.internal("LDR_LLL1_" + idx + ".png"));
//            pix = new Pixmap(Gdx.files.internal("blueN_" + idx + ".png"));
            ByteBuffer l3a1 = pix.getPixels();
            final int len = pix.getWidth() * pix.getHeight();
//            System.out.println("Original image has format " + pix.getFormat() + " and contains " + len + " pixels.");
            byte[] brights =  new byte[len];
            for (int i = 0; i < len; i++) {
                brights[i] = l3a1.get(i);
                brights[i] += -128;
            }
            bytes[idx] = brights;
            //System.out.println(brights[0]);
//            generatePreloadCode(brights, "BlueNoiseTiling.txt");
        }
        for (int i : new int[]{0, 1, 3, 2, 6, 7, 5, 4, 12, 13, 15, 14, 10, 11, 9, 8}) {
            for (int e = 0; e < 4; e++) {
                int choice = 63 - ((i & 1 << e) == 0 ? 1 : 0);
                GridGraph gg = new GridGraph(i, choice);
                IndexedAStarPathFinder<GridPoint2> astar = new IndexedAStarPathFinder<>(gg, false);
                GraphPath<GridPoint2> dgp = new DefaultGraphPath<>(1024);
                switch (e)
                {
                    case 0:
                        if(!astar.searchNodePath(gg.grid[63][0], gg.grid[63][63], gg.heu, dgp))
                            System.out.println("failed to find path");
                        else {
                            System.out.println("found path for e="+e);
                            for (GridPoint2 p : dgp) {
                                for (int j = p.x; j < 64; j++) {
                                    bytes[i][p.y << 6 | j] = bytes[choice][p.y << 6 | j];
                                }
                            }
                        }
                        break;
                    case 1:
                        if(!astar.searchNodePath(gg.grid[0][0], gg.grid[0][63], gg.heu, dgp))
                            System.out.println("failed to find path");
                        else {
                            System.out.println("found path for e="+e);
                            for (GridPoint2 p : dgp) {
                                for (int j = p.x; j >= 0; j--) {
                                    bytes[i][p.y << 6 | j] = bytes[choice][p.y << 6 | j];
                                }
                            }
                        }
                        break;
                    case 2:
                        if(!astar.searchNodePath(gg.grid[0][63], gg.grid[63][63], gg.heu, dgp))
                            System.out.println("failed to find path");
                        else {
                            System.out.println("found path for e="+e);
                            for (GridPoint2 p : dgp) {
                                for (int j = p.y; j < 64; j++) {
                                    bytes[i][j << 6 | p.x] = bytes[choice][j << 6 | p.x];
                                }
                            }
                        }
                        break;
                    default:
                        if(!astar.searchNodePath(gg.grid[0][0], gg.grid[63][0], gg.heu, dgp))
                            System.out.println("failed to find path");
                        else {
                            System.out.println("found path for e="+e);
                            for (GridPoint2 p : dgp) {
                                for (int j = p.y; j >= 0; j--) {
                                    bytes[i][j << 6 | p.x] = bytes[choice][j << 6 | p.x];
                                }
                            }
                        }
                        break;
                }
            }
            generatePreloadCode(bytes[i], "BlueNoiseTiling.txt");
        }
        System.out.println("Succeeded!");
    }
    /*
    First I ran MultiTileBlueNoise to get blue.png .
    
    Then I ran this ImageMagick script:
    
convert blue.png -crop 64x64 blueTiling_%d.png
    
    Then I ran this Clojure code to get the mv commands with the correct numbers:
    
(println (clojure.string/join "\r\n" (for [n (range 16) :let [x (bit-and n 3) y (bit-shift-right n 2)]] (format "mv blueTiling_%d.png blueN_%d.png" n (+ (bit-xor x (bit-shift-right x 1)) (* 4 (bit-xor y (bit-shift-right y 1))))))))
    
    Then I ran those mv commands, which are:
    
mv blueTiling_0.png  blueN_0.png
mv blueTiling_1.png  blueN_1.png
mv blueTiling_2.png  blueN_3.png
mv blueTiling_3.png  blueN_2.png
mv blueTiling_4.png  blueN_4.png
mv blueTiling_5.png  blueN_5.png
mv blueTiling_6.png  blueN_7.png
mv blueTiling_7.png  blueN_6.png
mv blueTiling_8.png  blueN_12.png
mv blueTiling_9.png  blueN_13.png
mv blueTiling_10.png blueN_15.png
mv blueTiling_11.png blueN_14.png
mv blueTiling_12.png blueN_8.png
mv blueTiling_13.png blueN_9.png
mv blueTiling_14.png blueN_11.png
mv blueTiling_15.png blueN_10.png
     
    the bit == 1 determines what the right edge is.
    the bit == 2 determines what the left edge is.
    the bit == 4 determines what the bottom edge is (using y-down, so higher y).
    the bit == 8 determines what the top edge is (using y-down, so lower y).
     */
    /**
     * Given a byte array, this writes a file containing a code snippet that can be pasted into Java code as the preload
     * data used by {@link PaletteReducer#exact(int[], byte[])}; this is almost never needed by external code. When 
     * using this for preload data, the byte array should be {@link PaletteReducer#paletteMapping}.
     * @param data the bytes to use as preload data, usually {@link PaletteReducer#paletteMapping}
     */
    public static void generatePreloadCode(final byte[] data) {
        generatePreloadCode(data, "bytes_" + TimeUtils.millis() + ".txt");
    }
    /**
     * Given a byte array, this appends to a file called {@code filename} containing a code snippet that can be pasted
     * into Java code as the preload data used by {@link PaletteReducer#exact(int[], byte[])}; this is almost never
     * needed by external code. When using this for preload data, the byte array should be
     * {@link PaletteReducer#paletteMapping}.
     * @param data the bytes to use as preload data, usually {@link PaletteReducer#paletteMapping}
     * @param filename the name of the text file to append to
     */
    public static void generatePreloadCode(final byte[] data, String filename){
        StringBuilder sb = new StringBuilder(data.length + 400);
        sb.append('"');
        for (int i = 0; i < data.length;) {
            for (int j = 0; j < 0x80 && i < data.length; j++) {
                byte b = data[i++];
                switch (b)
                {
                    case '\t': sb.append("\\t");
                        break;
                    case '\b': sb.append("\\b");
                        break;
                    case '\n': sb.append("\\n");
                        break;
                    case '\r': sb.append("\\r");
                        break;
                    case '\f': sb.append("\\f");
                        break;
                    case '\"': sb.append("\\\"");
                        break;
                    case '\\': sb.append("\\\\");
                        break;
                    default:
                        if(Character.isISOControl(b))
                            sb.append(String.format("\\%03o", b));
                        else
                            sb.append((char) (b&0xFF));
                        break;
                }
            }
//            sb.append('"');
//            if(i != data.length)
//                sb.append('+');
//            sb.append('\n');
        }
        sb.append("\".getBytes(StandardCharsets.ISO_8859_1),\n");
        Gdx.files.local(filename).writeString(sb.toString(), true, "ISO-8859-1");
        System.out.println("Wrote code snippet to " + filename);
    }

    class GridGraph implements IndexedGraph<GridPoint2>
    {
        public ObjectIntMap<GridPoint2> points = new ObjectIntMap<>(128 * 128);
        public byte[] base, edge;
        public GridPoint2[][] grid = new GridPoint2[64][64];
        public Heuristic<GridPoint2> heu = new Heuristic<GridPoint2>() {
            @Override
            public float estimate(GridPoint2 node, GridPoint2 endNode) {
                return (Math.abs(node.x - endNode.x) + Math.abs(node.y - endNode.y)) * 42;
            }
        };

        public GridGraph(int baseIndex, int edgeIndex)
        {
            base = bytes[baseIndex];
            edge = bytes[edgeIndex];
            final int floorCount = 4096;
            for (int i = 0; i < floorCount; i++) {
                final int x =  i & 63, y = i >>> 6;
                points.put(grid[x][y] = new GridPoint2(x, y), i);
            }
        }
        @Override
        public int getIndex(GridPoint2 node) {
            return points.get(node, -1);
        }

        @Override
        public int getNodeCount() {
            return points.size;
        }

        @Override
        public Array<Connection<GridPoint2>> getConnections(GridPoint2 fromNode) {
            Array<Connection<GridPoint2>> conn = new Array<>(false, 4);
            int index;
            GridPoint2 t;
            final int x = fromNode.x, y = fromNode.y;
            if(x < 63) {
                t = grid[x + 1][y];// new GridPoint2(fromNode.cpy().add(1, 0));
                index = y << 6 | x + 1;
                conn.add(new DijkstraConnection(fromNode, t, Math.abs(base[index] - edge[index])));
            }
            if(x > 0) {
                t = grid[x - 1][y];// new GridPoint2(fromNode.cpy().add(-1, 0));
                index = y << 6 | x - 1;
                conn.add(new DijkstraConnection(fromNode, t, Math.abs(base[index] - edge[index])));
            }
            if(y < 63) {
                t = grid[x][y + 1];// new GridPoint2(fromNode.cpy().add(0, 1));
                index = y + 1 << 6 | x;
                conn.add(new DijkstraConnection(fromNode, t, Math.abs(base[index] - edge[index])));
            }
            if(y > 0) {
                t = grid[x][y - 1];// new GridPoint2(fromNode.cpy().add(0, -1));
                index = y - 1 << 6 | x;
                conn.add(new DijkstraConnection(fromNode, t, Math.abs(base[index] - edge[index])));
            }
            return conn;
        }
    }
    static class DijkstraConnection implements Connection<GridPoint2>
    {
        public float cost;
        public GridPoint2 from, to;

        public DijkstraConnection(GridPoint2 fromNode, GridPoint2 toNode, float travelCost)
        {
            cost = travelCost;
            from = fromNode;
            to = toNode;
        }

        @Override
        public float getCost() {
            return cost;
        }

        @Override
        public GridPoint2 getFromNode() {
            return from;
        }

        @Override
        public GridPoint2 getToNode() {
            return to;
        }
    }
}
