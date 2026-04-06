import java.util.Map;
import java.util.TreeMap;

import graph.util.Heap;
import graph.util.Position;

public class GradientMap {
    private static int SIZE = 1000;
    private static final int[][] OPTIONS = {new int[] {0, -1}, new int[] {1, 0}, new int[] {0,1}, new int[] {-1,0}};
    private static final String[] DIRECTIONS = { "north", "east", "south", "west"};
    public class Entry {
        int x;
        int y;
        int distance;

        public Entry(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }

        public int priority() {
            // int px = x-model.x;
            // int py = y-model.y;

            // return (int) Math.sqrt((double) px*px+py*py) - distance;
            return distance;
        }

        public String toString() {
            return "(" + x + "," + y + " / " + distance + ")";
        }
    }

    int[][] model = new int[SIZE][SIZE];
    int[][] map;
    int max_x;
    int max_y;

    public void setMax_x(int x) {
        max_x = grid_x(x);
    }

    public void setMax_y(int y) {
        max_y = grid_y(y);
    }

    public int getMax_x() {
        return normal_x(max_x);
    }
    
    public int getMax_y() {
        return normal_y(max_y);
    }

    private int grid_x(int x) {
        return x+1;
    }

    private int grid_y(int y) {
        return y+1;
    }
    
    private int normal_x(int x) {
        return x-1;
    }

    private int normal_y(int y) {
        return y-1;
    }

    public void generateGradient(int x, int y) {
        map = new int[SIZE][SIZE];

        Heap<Integer, Entry> heap = new Heap<>();
        Map<String, Position<Entry>> positionMap = new TreeMap<>();
        
        Entry entry = new Entry(x, y, 1);
        String key = key(x, y);
        positionMap.put(key, heap.insert(entry.priority(), entry));
        while (!heap.isEmpty()) {
            entry = heap.remove();
            if (isClear(entry.x, entry.y)) {
                map[grid_x(entry.x)][grid_y(entry.y)] = entry.distance;

                for (int[] coord : OPTIONS) {
                    int cx = entry.x+coord[0];
                    int cy = entry.y+coord[1];

                    if (inBounds(cx, cy)) {
                        String k = key(cx, cy);
                        if (positionMap.containsKey(k)) {
                            Position<Entry> position = positionMap.get(k);
                            if (position.element().distance > entry.distance+1) {
                                //better path has been found...
                                position.element().distance = entry.distance+1;
                                heap.replaceKey(position, entry.distance+1);
                            }
                        } else {
                            Entry newEntry = new Entry(cx, cy, entry.distance+1);
                            positionMap.put(k, heap.insert(newEntry.priority(), newEntry));
                        }
                    }
                }
            } else {
                map[grid_x(entry.x)][grid_y(entry.y)] = 999;
            }

        }
    }

    boolean inBounds(int cx, int cy) {
        return (grid_x(cx) >=0 && grid_x(cx)<=max_x) && (grid_y(cy)>=0 && grid_y(cy) <=max_y);
    }

    private String key(int x, int y) {
        return x + ":" + y;
    }
    
    private boolean isClear(int x, int y) {
        return map[grid_x(x)][grid_y(y)] == 0 && !hasObstacle(x,y);
    }

    public void setObstacle(int x, int y) {
        model[grid_x(x)][grid_y(y)] = 1;
    }

    public boolean hasObstacle(int x, int y) {
        if (x<0 || y<0) return true;
        return model[grid_x(x)][grid_y(y)] == 1;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("OBSTACLES:\n");
        for (int j=0; j<=max_y;j++) {
            if (j==0) {
                buf.append("      ");
                for (int i=0; i<=max_x;i++) {
                    buf.append(" | ");
                    padLabel(buf, i-1);
                }
                buf.append(" |\n");
            }

            padLabel(buf, j-1);
            buf.append(" ");
            for (int i=0; i<=max_x;i++) {
                buf.append(" | ");
                padLabel(buf, model[i][j]);
            }
            buf.append(" |\n");
        }

        buf.append("\n\nGRADIENT:\n");
        for (int j=0; j<=max_y;j++) {
            if (j==0) {
                buf.append("      ");
                for (int i=0; i<=max_x;i++) {
                    buf.append(" | ");
                    padLabel(buf, i-1);
                }
                buf.append(" |\n");
            }

            padLabel(buf, j-1);
            buf.append(" ");
            for (int i=0; i<=max_x;i++) {
                buf.append(" | ");
                padLabel(buf, map[i][j]);
            }
            buf.append(" |\n");
        }
        return buf.toString();
    }

    private void padLabel(StringBuilder buf, int label) {
        if (label < 0) {
            if (label > -1000) buf.append(" ");
            if (label > -100) buf.append(" ");
            if (label > -10) buf.append(" ");
            
        } else {
            buf.append(" ");
            if (label < 1000) buf.append(" ");
            if (label < 100) buf.append(" ");
            if (label < 10) buf.append(" ");
        }
        buf.append(label);
    }

    private int getDistance(int x, int y) {
        if (x<0 || y<0) return 999;
        return map[grid_x(x)][grid_y(y)];
    }

    public int getDistance(int[] coords) {
        return map[grid_x(coords[0])][grid_y(coords[1])];
    }

    public int chooseOption(int[] coords) {
        int max = getDistance(coords);

        int option = -1;
        for (int i=0; i<OPTIONS.length; i++) {
            int distance = getDistance(coords[0]+OPTIONS[i][0], coords[1]+OPTIONS[i][1]);
            if (distance < max) {
                max = distance;
                option = i;
            }
        }
        return option;
    }

    public int[] optionCoords(int[] coords, int option) {
        coords[0] += OPTIONS[option][0];
        coords[1] += OPTIONS[option][1];
        return coords;
    }

    public String optionDirection(int option) {
        return DIRECTIONS[option];
    }
}