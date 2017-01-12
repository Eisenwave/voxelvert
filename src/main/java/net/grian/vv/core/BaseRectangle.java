package net.grian.vv.core;

public interface BaseRectangle extends Comparable<BaseRectangle> {

    public abstract int getWidth();

    public abstract int getHeight();

    public default int getArea() {
        return getWidth() * getHeight();
    }

    @Override
    public default int compareTo(BaseRectangle rectangle) {
        int result = this.getHeight() - rectangle.getHeight();
        return result!=0? result : this.getWidth() - rectangle.getWidth();
    }

}
