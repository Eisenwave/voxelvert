package net.grian.vv.core;

public interface BaseRectangle extends Comparable<BaseRectangle> {

    /**
     * Returns the rectangle width.
     *
     * @return the rectangle width
     */
    public abstract int getWidth();

    /**
     * Returns the rectangle height.
     *
     * @return the rectangle height
     */
    public abstract int getHeight();

    /**
     * Returns the area <b>A</b> of the rectangle: <code>A = width * height</code>
     *
     * @return the area
     */
    public default int getArea() {
        return getWidth() * getHeight();
    }

    @Override
    public default int compareTo(BaseRectangle rectangle) {
        int result = this.getHeight() - rectangle.getHeight();
        return result!=0? result : this.getWidth() - rectangle.getWidth();
    }

}
