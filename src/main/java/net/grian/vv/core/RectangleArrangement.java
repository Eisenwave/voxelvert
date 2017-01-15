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

    public int size() {
        return content.size();
    }

    public void clear() {
        content.clear();
    }

    public void add(Entry entry) {
        if (entry.getU()+entry.getRectangle().getWidth() > this.width)
            throw new IllegalArgumentException("entry exceeds boundaries (0-"+width+")in width");
        if (entry.getV()+entry.getRectangle().getHeight() > this.height)
            throw new IllegalArgumentException("entry exceeds boundaries (0-"+height+")in height");
        content.add(entry);
    }

    public void add(int u, int v, BaseRectangle rectangle) {
        add(new Entry(u, v, rectangle));
    }

    @Override
    public String toString() {
        return RectangleArrangement.class.getSimpleName()+
                "{width="+getWidth()+
                ",height="+getHeight()+"}";
    }

    @Override
    public Iterator<Entry> iterator() {
        return content.iterator();
    }

    public static class Entry {

        private final int u, v;
        private final BaseRectangle rectangle;

        public Entry(int u, int v, BaseRectangle rectangle) {
            if (u < 0) throw new IllegalArgumentException("u must be positive");
            if (v < 0) throw new IllegalArgumentException("v must be positive");
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
