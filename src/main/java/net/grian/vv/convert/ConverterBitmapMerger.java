package net.grian.vv.convert;

import net.grian.spatium.geo.BlockSelection;
import net.grian.vv.core.Bitmap3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

public class ConverterBitmapMerger implements Converter<Bitmap3D, BlockSelection[]> {

    @Override
    public Class<Bitmap3D> getFrom() {
        return Bitmap3D.class;
    }

    @Override
    public Class<BlockSelection[]> getTo() {
        return BlockSelection[].class;
    }

    private final Logger logger;

    public ConverterBitmapMerger() {
        this.logger = Logger.getGlobal();
    }

    @Override
    public BlockSelection[] invoke(Bitmap3D map, Object... args) {
        logger.info("merging "+map+" ...");
        final int
                limX = map.getSizeX(),
                limY = map.getSizeY(),
                limZ = map.getSizeZ();

        LineList[][] lines = mergeX(map, limX, limY, limZ);
        {
            int count = 0;
            for (int y = 0; y<limY; y++) for (int z = 0; z<limZ; z++)
                count += lines[y][z].size();
            logger.info("1: merged array into "+count+" lines");
        }

        PlaneList[] planes = mergeY(lines, limY, limZ);
        {
            int count = 0;
            for (PlaneList planeList : planes) count += planeList.size();
            logger.info("2: merged lines into "+count+" planes");
        }

        BoxList boxes = mergeZ(planes, limZ);
        logger.info("3: merged planes into "+boxes.size()+" boxes");

        BlockSelection[] result = new BlockSelection[boxes.size()];
        int index = 0;
        for (BoxList.Entry box : boxes)
            result[index++] = BlockSelection.fromPoints(box.xmin, box.ymin, box.zmin, box.xmax, box.ymax, box.zmax);

        return result;
    }

    /**
     * Merges bits into lines of bits on the x-axis.
     *
     * @param bitmap the initial bitmap
     * @return a 2D array of voxel arrays lists representing a YxZ array of lines
     */
    private static LineList[][] mergeX(Bitmap3D bitmap, int limX, int limY, int limZ) {
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
                for (int i = y+1; i<limY; i++) {
                    LineList lineList = lines[i][z];
                    Iterator<LineList.Entry> iter = lineList.iterator();
                    //loop through all entries on the line
                    while (iter.hasNext()) {
                        LineList.Entry line2 = iter.next();
                        //early break since all following lines will start further in x than this one
                        if (line2.min > line.min) break;
                        if (line2.equals(line)) {
                            //line can be merged into current plane, thus stretch the current plane and remove the line
                            plane.ymax = i;
                            iter.remove();
                        }
                    }
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
            for (int i = z+1; i<limZ; i++) {

                Iterator<PlaneList.Entry> iter = planes[i].iterator();
                //loop through all entries on further planes
                while (iter.hasNext()) {
                    PlaneList.Entry plane2 = iter.next();
                    //early break since all following planes will start further in y than this one
                    if (plane2.ymin > plane.ymin) break;
                    else if (plane2.ymin == plane.ymin) {
                        //early break since all following planes will start further in x or y than current one
                        if (plane2.xmin > plane.xmin) break;
                        else if (plane2.xmin == plane.xmin) {
                            //plane can be merged into current box, thus stretch the current box and remove the plane
                            box.zmax = i;
                            iter.remove();
                        }
                    }
                }
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
