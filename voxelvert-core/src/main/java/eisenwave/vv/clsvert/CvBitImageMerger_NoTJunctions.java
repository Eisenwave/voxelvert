package eisenwave.vv.clsvert;

import eisenwave.spatium.array.BooleanArray2;
import eisenwave.torrens.object.Rectangle4i;
import eisenwave.torrens.voxel.*;
import eisenwave.vv.clsvert.CvBitArrayMerger_XYZ.LineList;
import eisenwave.vv.clsvert.CvBitArrayMerger_XYZ.PlaneList;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.logging.Logger;

public class CvBitImageMerger_NoTJunctions implements Classverter<BitArray2, Rectangle4i[]> {
    
    @Nullable
    private final Logger logger;
    
    public CvBitImageMerger_NoTJunctions(@Nullable Logger logger) {
        this.logger = logger;
    }
    
    public CvBitImageMerger_NoTJunctions() {
        logger = null;
    }
    
    @Deprecated
    @Override
    public Rectangle4i[] invoke(@NotNull BitArray2 from, @NotNull Object... args) {
        return invoke(from);
    }
    
    public Rectangle4i[] invoke(@NotNull BitArray2 from) {
        final int w = from.getSizeX(), h = from.getSizeY();
        
        BooleanArray2[] edges = drawEdges(from, w, h);
        
        /* {
            Texture out = Texture.alloc(w * 5, h * 5);
            for (int x = 0; x < w; x++)
                for (int y = 0; y < h; y++)
                    out.fill(x * 5, y * 5, 4, 4, from.contains(x, y)? ColorMath.SOLID_WHITE : ColorMath.SOLID_BLACK);
            
            for (int x = 0; x < w; x++)
                for (int y = 1; y < h; y++)
                    if (horizontEdges.get(x, y - 1))
                        out.fill(x * 5, y * 5 - 1, 4, 1, ColorMath.SOLID_RED);
            
            for (int y = 0; y < h; y++)
                for (int x = 1; x < w; x++)
                    if (verticalEdges.get(x - 1, y))
                        out.fill(x * 5 - 1, y * 5, 1, 4, ColorMath.SOLID_BLUE);
            
            try {
                new SerializerPNG().toFile(out.getImageWrapper(), new File("/home/user/Files/debug_borders.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } */
        
        LineList[] lines = mergeX(from, edges[1], w, h);
        PlaneList planes = mergeY(lines, edges[0], h);
        
        Rectangle4i[] result = new Rectangle4i[planes.size()];
        int index = 0;
        for (PlaneList.Entry plane : planes)
            result[index++] = new Rectangle4i(plane.xmin, plane.ymin, plane.xmax, plane.ymax);
        
        return result;
        //return resultFromLines(lines, h);
    }
    
    private static LineList[] mergeX(final BitArray2 from, final BooleanArray2 verticalEdges,
                                     final int w, final int h) {
        //System.err.println("WH " + w + " " + h);
        final LineList[] result = new LineList[h];
        
        for (int y = 0; y < h; y++) {
            LineList listHere = result[y] = new LineList();
            boolean[] verEdgesHere = verticalEdges.getRow(y);
            
            int min = -1;
            for (int x = 0; x < w; x++) {
                if (min == -1) {
                    if (from.contains(x, y)) {
                        min = x;
                    }
                }
                else if (verEdgesHere[x - 1]) {
                    listHere.add(new LineList.Entry(min, x - 1));
                    min = from.contains(x, y)? x : -1;
                }
            }
            
            if (min != -1)
                listHere.add(new LineList.Entry(min, w - 1));
        }
        
        return result;
    }
    
    @SuppressWarnings("Duplicates")
    private static PlaneList mergeY(final LineList[] lines, final BooleanArray2 horizontEdges, final int h) {
        final PlaneList planes = new PlaneList();
        
        //loop through every line list
        for (int y = 0; y < h; y++)
            for (LineList.Entry line : lines[y]) {
                //turn line into plane on current y-coordinate, then proceed to stretch it into y as far as possible
                PlaneList.Entry plane = new PlaneList.Entry(line.min, y, line.max, y);
                planes.add(plane);
                
                //loop through all lines further in y-direction than the current one
                mergeLoop:
                for (int i = y + 1; i < h && !horizontEdges.get(line.min, i - 1); i++) {
                    
                    Iterator<LineList.Entry> iter = lines[i].iterator();
                    //loop through all entries on the line
                    while (iter.hasNext()) {
                        LineList.Entry line2 = iter.next();
                        //early merge break since all following lines will start further in x than this one
                        if (line2.min > line.min) break mergeLoop;
                        if (line2.equals(line)) {
                            //line can be merged into current plane, thus stretch the current plane and remove the line
                            plane.ymax = i;
                            iter.remove();
                            //continue merging on next y
                            continue mergeLoop;
                        }
                    }
                    
                    break;
                }
            }
        
        return planes;
    }
    
    private static BooleanArray2[] drawEdges(final BitArray2 from, final int w, final int h) {
        //long beforeBorders = System.currentTimeMillis();
        final int maxX = w - 1, maxY = h - 1;
        
        final BooleanArray2
            horizontEdges = new BooleanArray2(w, maxY),
            verticalEdges = new BooleanArray2(maxX, h);
        
        final boolean[] current = new boolean[w];
        final boolean[] next = new boolean[w];
        
        // special case block for y = 0
        // * no NORTH-* vertices can be created on the last row
        {
            readRow(from, current, 0);
            readRow(from, next, 1);
            
            boolean prevVal = current[0];
            if (next[0] != prevVal) {
                horizontEdges.enable(0, 0);
            }
            for (int x = 1; x < w; x++) {
                boolean nextVal = current[x];
                
                final boolean horSouth, verWest;
                if (verWest = prevVal != nextVal)
                    verticalEdges.enable(x - 1, 0);
                if (horSouth = next[x] != nextVal)
                    horizontEdges.enable(x, 0);
                
                if (!nextVal) {
                    if (x != maxX && horSouth && verWest)
                        drawCorner(from, horizontEdges, verticalEdges, x, 0, CornerDirection.SE);
                    if (horSouth && verWest)
                        drawCorner(from, horizontEdges, verticalEdges, x, 0, CornerDirection.SW);
                }
                prevVal = nextVal;
            }
        }
        
        // regular case loop for 0 < y < height - 1
        for (int y = 1; y < h - 1; y++) {
            System.arraycopy(next, 0, current, 0, w);
            readRow(from, next, y + 1);
            
            boolean prevVal = current[0];
            if (next[0] != prevVal) {
                horizontEdges.enable(0, y);
            }
            
            for (int x = 1; x < w; x++) {
                boolean nextVal = current[x];
                
                final boolean horSouth, verWest;
                if (verWest = prevVal != nextVal) {
                    verticalEdges.enable(x - 1, y);
                }
                
                if (horSouth = next[x] != nextVal)
                    horizontEdges.enable(x, y);
                
                if (!nextVal) {
                    if (x != maxX && current[x + 1]) {
                        if (horizontEdges.get(x, y - 1))
                            drawCorner(from, horizontEdges, verticalEdges, x, y, CornerDirection.NE);
                        if (horSouth)
                            drawCorner(from, horizontEdges, verticalEdges, x, y, CornerDirection.SE);
                    }
                    if (verWest) {
                        if (horSouth)
                            drawCorner(from, horizontEdges, verticalEdges, x, y, CornerDirection.SW);
                        if (horizontEdges.get(x, y - 1))
                            drawCorner(from, horizontEdges, verticalEdges, x, y, CornerDirection.NW);
                    }
                }
                prevVal = nextVal;
            }
        }
        
        // special case block for y = height - 1:
        // * no horizontal edges can be created on the last row
        // * no SOUTH-* vertices can be created on the last row
        {
            boolean prevVal = next[0];
            for (int x = 1; x < w; x++) {
                boolean nextVal = next[x];
                
                final boolean verWest;
                if (verWest = prevVal != nextVal)
                    verticalEdges.enable(x - 1, maxY);
                
                if (!nextVal) {
                    if (x != maxX && next[x + 1] && verWest)
                        drawCorner(from, horizontEdges, verticalEdges, x, maxY, CornerDirection.NE);
                    if (verWest && horizontEdges.get(x, maxY - 1))
                        drawCorner(from, horizontEdges, verticalEdges, x, maxY, CornerDirection.NW);
                }
                prevVal = nextVal;
            }
        }
        
        // System.out.println("drawn borders in " + (System.currentTimeMillis() - beforeBorders) + " ms");
        
        return new BooleanArray2[] {horizontEdges, verticalEdges};
    }
    
    @SuppressWarnings("Duplicates")
    private static void drawCorner(BitArray2 arr, BooleanArray2 hor, BooleanArray2 ver,
                                   int x, int y, CornerDirection dir) {
        //System.err.println("drawCorner(" + x + ", " + y + ", " + dir + ")");
        final boolean s = dir.isSouth(), e = dir.isEast();
        //if (true) return;
        
        final int w = hor.getSizeX(), h = ver.getSizeY(),
            x2, y2, xstr, ystr, xdir, ydir, xlim, ylim;
        
        if (e) {
            x2 = x;
            xstr = x + 1;
            xdir = 1;
            xlim = w;
        }
        else {
            x2 = xstr = x - 1;
            xdir = xlim = -1;
        }
        if (s) {
            y2 = y;
            ystr = y + 1;
            ydir = 1;
            ylim = h;
        }
        else {
            y2 = ystr = y - 1;
            ydir = ylim = -1;
        }
        
        // horizontal draw
        for (int i = xstr; i != xlim
            && !hor.get(i, y2)
            && (arr.contains(i, y2) || arr.contains(i, y2 + 1)); i += xdir) {
            //System.err.println("    enableH(" + i + ", " + y2 + ")");
            hor.enable(i, y2);
        }
        
        // vertical draw
        for (int i = ystr; i != ylim
            && !ver.get(x2, i)
            && (arr.contains(x2, i) || arr.contains(x2 + 1, i)); i += ydir) {
            //System.err.println("    enableV(" + x2 + ", " + i + ")");
            ver.enable(x2, i);
        }
    }
    
    private static void readRow(BitArray2 source, boolean[] target, int row) {
        for (int x = 0; x < target.length; x++)
            target[x] = source.contains(x, row);
    }
    
    // SUBCLASSES
    
    private static enum CornerDirection {
        NE, SE, SW, NW;
        
        private boolean isNorth() {
            return this == NE || this == NW;
        }
        
        private boolean isSouth() {
            return this == SE || this == SW;
        }
        
        private boolean isEast() {
            return this == NE || this == SE;
        }
    }
    
}
