package eisenwave.vv.object;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents an arrangement of rectangles within an outer, rectangular bounding box.
 */
public class RectangleArrangement<E extends BaseRectangle> implements Iterable<RectangleArrangement.Entry<E>> {
    
    private final Collection<Entry<E>> content = new ArrayList<>();
    
    private final int width, height;
    
    public RectangleArrangement(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public Collection<RectangleArrangement.Entry<E>> getContent() {
        return content;
    }
    
    /**
     * Returns the width of the arrangement bounding box. No rectangle exceeds these boundaries in width.
     *
     * @return the width of the arrangement bounds
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Returns the height of the arrangement bounding box. No rectangle exceeds these boundaries in height.
     *
     * @return the height of the arrangement bounds
     */
    public int getHeight() {
        return height;
    }
    
    public boolean contains(int x, int y) {
        for (Entry entry : content)
            if (entry.contains(x, y))
                return true;
        return false;
    }
    
    /**
     * Returns the efficiency factor of this arrangement.
     * <p>
     * This will be a value between {@code 0.0} (no pixels covered) and {@code 1.0} (every pixel in the arrangement is
     * covered by some rectangle).
     * <p>
     * An arrangement with a factor of 1.0 is seen as the mathematically optimal solution.
     *
     * @return the arrangement efficiency
     */
    public double getEfficiency() {
        int has = 0;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                if (contains(x, y)) has++;
        
        return (double) has / (width * height);
    }
    
    public int size() {
        return content.size();
    }
    
    public void clear() {
        content.clear();
    }
    
    public void add(Entry<E> entry) {
        if (entry.getU() + entry.getRectangle().getWidth() > this.width)
            throw new IllegalArgumentException("entry exceeds boundaries (0-" + width + ")in width");
        if (entry.getV() + entry.getRectangle().getHeight() > this.height)
            throw new IllegalArgumentException("entry exceeds boundaries (0-" + height + ")in height");
        content.add(entry);
    }
    
    public void add(int u, int v, E rectangle) {
        add(new Entry<>(u, v, rectangle));
    }
    
    // MISC
    
    @Override
    public String toString() {
        return RectangleArrangement.class.getSimpleName() +
            "{width=" + getWidth() +
            ",height=" + getHeight() + "}";
    }
    
    @NotNull
    @Override
    public Iterator<Entry<E>> iterator() {
        return content.iterator();
    }
    
    public static class Entry<T extends BaseRectangle> {
        
        private final int u, v;
        private final T rectangle;
        
        public Entry(int u, int v, T rectangle) {
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
        
        public T getRectangle() {
            return rectangle;
        }
        
        public boolean contains(int x, int y) {
            return x >= u && x <= u + rectangle.getWidth()
                && y >= v && y <= v + rectangle.getHeight();
        }
        
    }
    
}
