package net.grian.vv.core;

import java.util.*;

public class RectangleArrangement implements Iterable<RectangleArrangement.Entry>, BaseRectangle {

    private final Collection<Entry> content = new ArrayList<>();

    private final int width, height;

    public RectangleArrangement(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Collection<RectangleArrangement.Entry> getContent() {
        return content;
    }

    /**
     * Returns the width of the arrangement bounding box. No rectangle exceeds these boundaries in width.
     *
     * @return the width of the arrangement bounds
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the arrangement bounding box. No rectangle exceeds these boundaries in height.
     *
     * @return the height of the arrangement bounds
     */
    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public Iterator<Entry> iterator() {
        return content.iterator();
    }

    public static class Entry {

        private final int u, v;
        private final BaseRectangle rectangle;

        public Entry(int u, int v, BaseRectangle rectangle) {
            this.u = u;
            this.v = v;
            this.rectangle = rectangle;
        }

        public int getU() {
            return u;
        }

        public int getV() {
            return v;
        }

        public BaseRectangle getRectangle() {
            return rectangle;
        }

    }

}
