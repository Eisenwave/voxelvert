package net.grian.vv.clsvert;

import net.grian.torrens.img.BaseRectangle;
import net.grian.vv.core.RectangleArrangement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

public class ClassverterRectangleArranger implements Classverter<BaseRectangle[], RectangleArrangement> {

    @Override
    public Class<BaseRectangle[]> getFrom() {
        return BaseRectangle[].class;
    }

    @Override
    public Class<RectangleArrangement> getTo() {
        return RectangleArrangement.class;
    }

    private final Logger logger = Logger.getLogger("voxelvert.debug");

    @Override
    public RectangleArrangement invoke(BaseRectangle[] from, Object[] args) {
        return invoke(from);
    }

    public RectangleArrangement invoke(BaseRectangle[] rectangles) {
        if (rectangles.length == 0) throw new IllegalArgumentException("can not sort empty rectangle array");
        logger.fine("arranging "+rectangles.length+" rectangles");

        Arrays.sort(rectangles, (a, b) -> {
            int result = b.getHeight() - a.getHeight();
            return result == 0? b.getWidth() - a.getWidth() : result;
        });

        RectangleBucket[] buckets = sortInBuckets(rectangles);
        logger.fine("sorted into "+buckets.length+" buckets");

        RectangleArrangement result = render(buckets);
        logger.fine("rendered as "+result);

        return result;
    }

    private RectangleBucket[] sortInBuckets(BaseRectangle[] rectangles) {
        if (rectangles.length == 1) {
            logger.fine("special case (single rectangle)");
            BaseRectangle rectangle = rectangles[0];
            RectangleBucket bucket = new RectangleBucket(rectangle.getWidth(), rectangle.getHeight());
            bucket.add(rectangle);

            return new RectangleBucket[] {bucket};
        }

        int maxW = getWidth(rectangles), maxH = Math.max(maxW, rectangles[0].getHeight());

        //special case: only one bucket can be created due to tall rectangles
        if (maxH > maxW) {
            logger.fine("special case (maxH >= maxW)");
            RectangleBucket[] buckets = {new RectangleBucket(maxH, maxH)};
            for (BaseRectangle rectangle : rectangles)
                buckets[0].add(rectangle);
            return buckets;
        }

        //regular case: increase bucket count until buckets exceed bounds in height
        logger.fine("regular case (multiple buckets optimal)");
        int[] simulation = findArrangement(rectangles, maxW);
        final int bucketCount = simulation[0], dims = simulation[1];
        logger.fine("conclusion: "+bucketCount+" buckets in "+dims+"x"+dims+" square");

        int i = 0;
        RectangleBucket[] buckets = new RectangleBucket[bucketCount];
        buckets[0] = new RectangleBucket(dims, rectangles[0].getHeight());

        for (BaseRectangle rectangle : rectangles) {
            if (!buckets[i].add(rectangle)) {
                //System.out.println(buckets[i]+" can not hold "+rectangle.getWidth()+"x"+rectangle.getHeight());
                RectangleBucket newBucket = buckets[++i] = new RectangleBucket(dims, rectangle.getHeight());
                newBucket.add(rectangle);
            }
        }

        return buckets;
    }

    /**
     * <p>
     *     Simulates arranging rectangles in buckets and returns the best fit.
     * </p>
     * <p>
     *     The result will be an int array containing the amount of buckets and the rectangle dimensions in order.
     *     (length = 2)
     * </p>
     *
     * @param rectangles the rectangles
     * @param totalWidth the total width of the rectangles
     * @return the amount of buckets and rectangle dims
     */
    private int[] findArrangement(BaseRectangle[] rectangles, int totalWidth) {
        int outBucketCount = -1, outDims = -1;
        
        for (int div = 2 ;; div++) {
            final int dims = totalWidth / div;
            final int attempt = tryArrangement(rectangles, dims);
            
            if (attempt == -1) break;
            
            //simulation succeeded, set outputs to this loop's temporary vars
            outBucketCount = attempt;
            outDims = dims;
        }

        return new int[] {outBucketCount, outDims};
    }
    
    /**
     * Tries out a rectangle arrangement.
     *
     * @param rectangles the rectangles to arrange
     * @param dims the bounding rectangle dimensions
     * @return -1 if the rectangles don't fit, else the bucket count
     */
    private int tryArrangement(BaseRectangle[] rectangles, int dims) {
        int
            bucketCount = 1,
            w = 0,
            h = rectangles[0].getHeight();
        
        for (BaseRectangle rectangle : rectangles) {
            final int recWidth = rectangle.getWidth(), recHeight = rectangle.getHeight();
        
            if ((w += recWidth) > dims) {
                bucketCount++;
                w = recWidth;
            
                if ((h += recHeight) > dims) {
                    //simulation failed
                    return -1;
                }
            }
        }
    
        return bucketCount;
    }

    /**
     * Returns the total width of an array of rectangles.
     *
     * @param rectangles the rectangle array
     * @return the total width of the rectangles
     */
    private static int getWidth(BaseRectangle[] rectangles) {
        int width = 0;
        for (BaseRectangle rectangle : rectangles)
            width += rectangle.getWidth();

        return width;
    }

    private static RectangleArrangement render(RectangleBucket[] buckets) {
        final int dims = buckets[0].getWidth();
        RectangleArrangement arrangement = new RectangleArrangement(dims, dims);

        int v = 0;
        for (RectangleBucket bucket : buckets) {
            int u = 0;
            for (BaseRectangle rectangle : bucket) {
                arrangement.add(u, v, rectangle);
                u += rectangle.getWidth();
            }
            v += bucket.getHeight();
        }

        return arrangement;
    }

    private static int[] getDimensions(RectangleBucket[] buckets) {
        int width = buckets[0].getWidth(), height = buckets[0].getHeight();
        for (int i = 1; i<buckets.length; i++) {
            if (buckets[i].getWidth() != width) throw new IllegalArgumentException();
            height += buckets[i].getHeight();
        }

        return new int[] {width, height};
    }

    private static class RectangleBucket extends ArrayList<BaseRectangle> {

        private final int width, height;
        private int avWidth;

        public RectangleBucket(int width, int height) {
            if (width < 1) throw new IllegalArgumentException("width < 1");
            if (height < 1) throw new IllegalArgumentException("height < 1");
            this.width = width;
            this.height = height;
            this.avWidth = width;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getAvailableWidth() {
            return avWidth;
        }

        public int getAvailableHeight() {
            return height;
        }

        @Override
        public boolean add(BaseRectangle rectangle) {
            final int recWidth = rectangle.getWidth(), recHeight = rectangle.getHeight();
            if (recHeight > getAvailableHeight() || recWidth > getAvailableWidth())
                return false;

            boolean result = super.add(rectangle);
            if (result) avWidth -= recWidth;
            return result;
        }

        @Override
        public String toString() {
            return RectangleBucket.class.getSimpleName()+"{width="+width+",height="+height+"}";
        }
    }

}