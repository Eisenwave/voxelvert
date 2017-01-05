package net.grian.vv.core;

public class Rectangle {

    private int xmin, ymin, xmax, ymax;

    public Rectangle(int xmin, int ymin, int xmax, int ymax) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.xmax = xmax;
        this.ymax = ymax;
    }

    public void setMinX(int x) {
        if (x < 0) throw new IllegalArgumentException("x must be positive");
        this.xmin = x;
    }

    public void setMaxX(int x) {
        if (x < 0) throw new IllegalArgumentException("x must be positive");
        this.xmax = x;
    }

    public void setMinY(int y) {
        if (y < 0) throw new IllegalArgumentException("y must be positive");
        this.ymin = y;
    }

    public void setMaxY(int y) {
        if (y < 0) throw new IllegalArgumentException("y must be positive");
        this.ymax = y;
    }

    public int getMinX() {
        return xmin;
    }

    public int getMinY() {
        return ymin;
    }

    public int getMaxX() {
        return xmax;
    }

    public int getMaxY() {
        return ymax;
    }

}
