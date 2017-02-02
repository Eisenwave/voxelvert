package net.grian.vv.clsvert;

import net.grian.spatium.geo3.BlockSelection;
import net.grian.spatium.voxel.BitArray3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class ClassverterBitFieldMerger implements Classverter<BitArray3, BlockSelection[]> {

    @Override
    public Class<BitArray3> getFrom() {
        return BitArray3.class;
    }

    @Override
    public Class<BlockSelection[]> getTo() {
        return BlockSelection[].class;
    }

    @Nullable
    private final Logger logger;
    
    public ClassverterBitFieldMerger(Logger logger) {
        this.logger = logger;
    }

    public ClassverterBitFieldMerger() {
        logger = null;
    }

    @Override
    public BlockSelection[] invoke(BitArray3 map, Object... args) {
        int cancel = args.length>0? (Integer) args[0] : 3;
        if (cancel < 0) throw new IllegalArgumentException("cancel point must be positive");
    
        log("merging "+map+" ...");
        final int
            limX = map.getSizeX(),
            limY = map.getSizeY(),
            limZ = map.getSizeZ();
        if (cancel == 0) return resultFromBits(map, limX, limY, limZ);

        LineList[][] lines = mergeX(map, limX, limY, limZ);
        log(lines, limY, limZ);
        if (cancel <= 1) return resultFromLines(lines, limY, limZ);

        PlaneList[] planes = mergeY(lines, limY, limZ);
        log(planes);
        if (cancel <= 2) return resultFromPlanes(planes, limZ);
        
        BoxList boxes = mergeZ(planes, limZ);
        log(boxes);

        return resultFromBoxes(boxes);
    }
    
    private void log(String msg) {
        if (logger != null) logger.fine(msg);
    }
    
    private void log(LineList[][] lines, int limY, int limZ) {
        if (logger == null) return;
        
        int count = 0;
        for (int y = 0; y<limY; y++) for (int z = 0; z<limZ; z++)
            count += lines[y][z].size();
        logger.fine("1: merged array into "+count+" lines");
    }
    
    private void log(PlaneList[] planes) {
        if (logger == null) return;
    
        int count = 0;
        for (PlaneList planeList : planes) count += planeList.size();
        logger.fine("2: merged lines into "+count+" planes");
    }
    
    private void log(BoxList boxes) {
        if (logger == null) return;
    
        logger.fine("3: merged planes into "+boxes.size()+" boxes");
    }

    /**
     * Creates block selections from boxes.
     *
     * @param boxes the boxes
     * @return an array of block selections
     */
    private static BlockSelection[] resultFromBoxes(BoxList boxes) {
        BlockSelection[] result = new BlockSelection[boxes.size()];
        int index = 0;
        for (BoxList.Entry box : boxes)
            result[index++] = BlockSelection.fromPoints(box.xmin, box.ymin, box.zmin, box.xmax, box.ymax, box.zmax);

        return result;
    }

    private static BlockSelection[] resultFromPlanes(PlaneList[] planes, int limZ) {
        List<BlockSelection> result = new ArrayList<>();

        for (int z = 0; z<limZ; z++) {
            PlaneList zPlanes = planes[z];
            for (PlaneList.Entry plane : zPlanes)
                result.add(BlockSelection.fromPoints(plane.xmin, plane.ymin, z, plane.xmax, plane.ymax, z));
        }

        return result.toArray(new BlockSelection[result.size()]);
    }

    private static BlockSelection[] resultFromLines(LineList[][] lines, int limY, int limZ) {
        List<BlockSelection> result = new ArrayList<>();

        for (int y = 0; y<limY; y++) for (int z = 0; z<limZ; z++) {
            LineList yzLines = lines[y][z];
            for (LineList.Entry line : yzLines)
                result.add(BlockSelection.fromPoints(line.min, y, z, line.max, y, z));
        }

        return result.toArray(new BlockSelection[result.size()]);
    }

    private static BlockSelection[] resultFromBits(BitArray3 bitmap, int limX, int limY, int limZ) {
        BlockSelection[] result = new BlockSelection[limX*limY*limZ];

        for (int x = 0; x<limX; x++) for (int y = 0; y<limY; y++) for (int z = 0; z<limZ; z++) {
            if (bitmap.contains(x, y, z))
                result[x + y * limX + z * limX * limY] = BlockSelection.fromPoints(x, y, z, x, y, z);
        }

        return result;
    }

    /**
     * Merges bits into lines of bits on the x-axis.
     *
     * @param bitmap the initial bitmap
     * @return a 2D array of voxel arrays lists representing a YxZ array of lines
     */
    private static LineList[][] mergeX(BitArray3 bitmap, int limX, int limY, int limZ) {
        LineList[][] result = new LineList[limY][limZ];

        for (int y = 0; y<limY; y++) for (int z = 0; z<limZ; z++) {
            LineList list = result[y][z] = new LineList();

            int lineOrigin = -1;
            for (int x = 0; x<limX; x++) {

                //there is a voxel
                if (bitmap.contains(x, y, z)) {
                    //no line has been drawn yet, so the line begins at the current voxel
                    if (lineOrigin == -1) lineOrigin = x;
                }

                //there is no voxel, but line has begun and can be ended
                else if (lineOrigin > -1) {
                    //create line from previously set minimum to previous existing voxel
                    list.add(new LineList.Entry(lineOrigin, x-1));
                    lineOrigin = -1;
                }

            }

            //finish a line if it ended at the last x-coordinate
            if (lineOrigin != -1)
                list.add(new LineList.Entry(lineOrigin, limX-1));
        }

        return result;
    }

    /**
     * Merges lines of voxels into planes on the y-axis.
     *
     * @param lines a 2D array of voxel arrays representing a YxZ array of lines
     * @return a 1D array of voxel arrays representing a z-axis array of planes
     */
    private static PlaneList[] mergeY(LineList[][] lines, int limY, int limZ) {
        PlaneList[] result = new PlaneList[limZ];

        //loop through all z-axis planes
        for (int z = 0; z<limZ; z++) {
            PlaneList planes = result[z] = new PlaneList();

            //loop through every line list
            for (int y = 0; y<limY; y++) for (LineList.Entry line : lines[y][z]) {
                //turn line into plane on current y-coordinate, then proceed to stretch it into y as far as possible
                PlaneList.Entry plane = new PlaneList.Entry(line.min, y, line.max, y);
                planes.add(plane);

                //loop through all lines further in y-direction than the current one
                mergeLoop: for (int i = y+1; i<limY; i++) {

                    Iterator<LineList.Entry> iter = lines[i][z].iterator();
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
        }

        return result;
    }

    /**
     * Merges planes of voxels into boxes on the z-axis.
     *
     * @param planes a 1D array of voxel arrays representing a z-axis array of planes
     * @return an unordered array representing chunks/boxes/cuboids of voxels
     */
    private static BoxList mergeZ(PlaneList[] planes, int limZ) {
        BoxList result = new BoxList();

        //loop through all plane lists
        for (int z = 0; z<limZ; z++) for (PlaneList.Entry plane : planes[z]) {
            //turn plane into box on current z-coordinate, then proceed to stretch it into z as far as possible
            BoxList.Entry box = new BoxList.Entry(plane.xmin, plane.ymin, z, plane.xmax, plane.ymax, z);
            result.add(box);

            //loop through all planes further in z than this one
            mergeLoop: for (int i = z+1; i<limZ; i++) {

                Iterator<PlaneList.Entry> iter = planes[i].iterator();
                //loop through all entries on further planes
                while (iter.hasNext()) {
                    PlaneList.Entry plane2 = iter.next();
                    //early merge break since all following planes will start further in y than this one
                    if (plane2.ymin > plane.ymin) break mergeLoop;
                    if (plane2.ymin == plane.ymin) {
                        //early merge break since all following planes will start further in x or y than current one
                        if (plane2.xmin > plane.xmin) break mergeLoop;
                        if (plane2.xmin == plane.xmin && plane2.xmax == plane.xmax && plane2.ymax == plane.ymax) {
                            //plane can be merged into current box, thus stretch the current box and remove the plane
                            box.zmax = i;
                            iter.remove();
                            //continue merging on next z
                            continue mergeLoop;
                        }
                    }
                }

                break;
            }
        }

        return result;
    }

    private static class LineList extends ArrayList<LineList.Entry> {

        static class Entry {

            private int min, max;

            private Entry(int min, int max) {
                this.min = min;
                this.max = max;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Entry && equals((Entry) obj);
            }

            private boolean equals(Entry line) {
                return this.min == line.min && this.max == line.max;
            }

        }

    }

    private static class PlaneList extends ArrayList<PlaneList.Entry> {

        static class Entry {

            private int xmin, ymin, xmax, ymax;

            private Entry(int xmin, int ymin, int xmax, int ymax) {
                this.xmin = xmin;
                this.ymin = ymin;
                this.xmax = xmax;
                this.ymax = ymax;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Entry && equals((Entry) obj);
            }

            private boolean equals(Entry plane) {
                return
                        this.xmin == plane.xmin && this.ymin == plane.ymin &&
                        this.xmax == plane.xmax && this.ymax == plane.ymax;
            }

        }

    }

    private static class BoxList extends ArrayList<BoxList.Entry> {

        static class Entry {

            private int xmin, ymin, zmin, xmax, ymax, zmax;

            private Entry(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax) {
                this.xmin = xmin;
                this.ymin = ymin;
                this.zmin = zmin;
                this.xmax = xmax;
                this.ymax = ymax;
                this.zmax = zmax;
            }

            @Override
            public boolean equals(Object obj) {
                return obj instanceof Entry && equals((Entry) obj);
            }

            private boolean equals(Entry box) {
                return
                        this.xmin == box.xmin && this.ymin == box.ymin && this.zmin == box.zmin &&
                        this.xmax == box.xmax && this.ymax == box.ymax && this.zmax == box.zmax;
            }

        }

    }

}
