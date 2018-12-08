package eisenwave.vv.clsvert;

import eisenwave.torrens.object.Rectangle4i;
import eisenwave.torrens.voxel.BitArray2;
import org.jetbrains.annotations.*;

import eisenwave.vv.clsvert.CvBitArrayMerger_XYZ.LineList;

import java.util.*;

public class CvBitImageMerger_XY implements Classverter<BitArray2, Rectangle4i[]> {
    
    @Override
    public Class<BitArray2> getFrom() {
        return BitArray2.class;
    }
    
    @Override
    public Class<Rectangle4i[]> getTo() {
        return Rectangle4i[].class;
    }
    
    @Override
    public Rectangle4i[] invoke(@NotNull BitArray2 from, @NotNull Object... args) {
        final int w = from.getSizeX(), h = from.getSizeY();
        
        LineList[] lines = mergeX(from, w, h);
        //return resultFromLines(lines, h);
        List<Rectangle4i> planes = mergeY(lines, h);
        
        return planes.toArray(new Rectangle4i[planes.size()]);
    }
    
    @NotNull
    static Rectangle4i[] resultFromLines(LineList[] lines, int h) {
        List<Rectangle4i> result = new ArrayList<>();
        
        for (int y = 0; y < h; y++) {
            LineList yLines = lines[y];
            for (LineList.Entry line : yLines)
                result.add(new Rectangle4i(line.min, y, line.max, y));
        }
        
        return result.toArray(new Rectangle4i[result.size()]);
    }
    
    /**
     * Merges bits into lines of bits on the x-axis.
     *
     * @param bitmap the initial bitmap
     * @return a 2D array of voxel arrays lists representing a YxZ array of lines
     */
    private static LineList[] mergeX(BitArray2 bitmap, int limX, int limY) {
        LineList[] result = new LineList[limY];
        
        for (int y = 0; y < limY; y++) {
            LineList list = result[y] = new LineList();
            
            x_loop:
            for (int x = 0; x < limX; x++) {
                
                // there is a pixel
                if (bitmap.contains(x, y)) {
                    // loop until no pixel is present and create the rectangle
                    for (int i = x + 1; i < limX; i++) {
                        if (!bitmap.contains(i, y)) {
                            list.add(new LineList.Entry(x, i - 1));
                            x = i;
                            continue x_loop;
                        }
                    }
                    // if the end of the line has been reached, finish the rectangle
                    list.add(new LineList.Entry(x, limX - 1));
                    break;
                }
                
            }
        }
        
        return result;
    }
    
    /**
     * Merges lines of voxels into planes on the y-axis.
     *
     * @param lines a 2D array of voxel arrays representing a YxZ array of lines
     * @return a 1D array of voxel arrays representing a z-axis array of planes
     */
    private static List<Rectangle4i> mergeY(LineList[] lines, int limY) {
        List<Rectangle4i> result = new LinkedList<>();
        
        // loop through every line list
        for (int y = 0; y < limY; y++) {
            for (LineList.Entry line : lines[y]) {
                int maxY = y;
                
                //loop through all lines further in y-direction than the current one
                mergeLoop:
                for (int i = y + 1; i < limY; i++) {
                    
                    Iterator<LineList.Entry> iter = lines[i].iterator();
                    //loop through all entries on the line
                    while (iter.hasNext()) {
                        LineList.Entry line2 = iter.next();
                        //early merge break since all following lines will start further in x than this one
                        if (line2.min > line.min) break mergeLoop;
                        if (line2.equals(line)) {
                            //line can be merged into current plane, thus stretch the current plane and remove the line
                            maxY = i;
                            iter.remove();
                            //continue merging on next y
                            continue mergeLoop;
                        }
                    }
                    
                    break;
                }
                
                result.add(new Rectangle4i(line.min, y, line.max, maxY));
            }
        }
        
        return result;
    }
    
}
