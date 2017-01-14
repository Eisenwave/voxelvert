package net.grian.vv.convert;

import net.grian.vv.core.BaseRectangle;
import net.grian.vv.core.RectangleArrangement;
import net.grian.vv.core.Texture;
import net.grian.vv.util.tuple.Pair;

import java.util.*;

public class ConverterRectangleArranger implements Converter<BaseRectangle[], RectangleArrangement> {

    @Override
    public Class<BaseRectangle[]> getFrom() {
        return BaseRectangle[].class;
    }

    @Override
    public Class<RectangleArrangement> getTo() {
        return RectangleArrangement.class;
    }

    @Override
    public RectangleArrangement invoke(BaseRectangle[] from, Object[] args) {
        return invoke(from);
    }

    public RectangleArrangement invoke(BaseRectangle[] rectangles) {
        if (rectangles.length == 0) throw new IllegalArgumentException("can not sort empty rectangle array");
        Arrays.sort(rectangles);

        RectangleBucket bucket = new RectangleBucket(rectangles[0].getWidth(), rectangles[1].getHeight());
        List<RectangleBucket> buckets = new ArrayList<>();
        buckets.add(bucket);

        dump(buckets, rectangles, 1);


        return render(buckets);
    }

    private static void dump(List<RectangleBucket> buckets, BaseRectangle[] rectangles, int start) {
        for (int i = start; i<rectangles.length; i++)
            dump(buckets, rectangles[i]);
    }

    private static void dump(List<RectangleBucket> buckets, BaseRectangle rectangle) {
        //TODO implement this
    }

    private static RectangleArrangement render(List<RectangleBucket> buckets) {
        int[] dims = getDimensions(buckets);
        RectangleArrangement arrangement = new RectangleArrangement(dims[0], dims[1]);

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

    private static int[] getDimensions(List<RectangleBucket> buckets) {
        int width = buckets.get(0).getWidth(), height = buckets.get(0).getHeight();
        for (int i = 1; i<buckets.size(); i++) {
            if (buckets.get(i).getWidth() != width) throw new IllegalArgumentException();
            height += buckets.get(i).getHeight();
        }

        return new int[] {width, height};
    }

    private static class RectangleBucket extends ArrayList<BaseRectangle> {

        private int width, height;

        public RectangleBucket(int width, int height) {
            if (width < 1) throw new IllegalArgumentException("width < 1");
            if (height < 1) throw new IllegalArgumentException("height < 1");
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getContentWidth() {
            int result = 0;
            for (BaseRectangle rectangle : this)
                result += rectangle.getWidth();
            return result;
        }

        public int getContentHeight() {
            int result = 0;
            for (BaseRectangle rectangle : this)
                result += rectangle.getHeight();
            return result;
        }

        public int getAvailableWidth() {
            return width - getContentWidth();
        }

        public int getAvailableHeight() {
            return height - getContentWidth();
        }

        @Override
        public boolean add(BaseRectangle rectangle) {
            return
                    rectangle.getHeight() <= getAvailableHeight() &&
                    rectangle.getWidth() <= getAvailableWidth() &&
                    super.add(rectangle);
        }

    }

}
