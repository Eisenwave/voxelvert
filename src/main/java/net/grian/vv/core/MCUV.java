package net.grian.vv.core;

/**
 * Minecraft model uv coordinates with a texture reference.
 */
public class MCUV implements BaseRectangle {

    private String texture;
    private int xmin, ymin, xmax, ymax, rotation;

    public MCUV(String texture, int xmin, int ymin, int xmax, int ymax, int rotation) {
        setTexture(texture);
        setMinX(xmin);
        setMinY(ymin);
        setMaxX(xmax);
        setMaxY(ymax);
        setRotation(rotation);
    }

    public MCUV(String texture, int xmin, int ymin, int xmax, int ymax) {
        this(texture, xmin, ymin, xmax, ymax, 0);
    }

    //GETTERS

    /**
     * Returns the name of the texture.
     *
     * @return the name of the texture
     */
    public String getTexture() {
        return texture;
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

    /**
     * Returns the rotation of the uv in degrees. The returned value can be either 0, 90, 180 or 270.
     *
     * @return the uv rotation
     */
    public int getRotation() {
        return rotation;
    }

    @Override
    public int getWidth() {
        return Math.abs(xmax - xmin) + 1;
    }

    @Override
    public int getHeight() {
        return Math.abs(ymax - ymin) + 1;
    }

    //SETTERS

    public void setTexture(String texture) {
        this.texture = texture;
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

    public void setRotation(int rotation) {
        if (rotation % 90 != 0) throw new IllegalArgumentException("rotation must be multiple of 90");
        if (rotation < 0 || rotation >= 360) throw new IllegalArgumentException("rotation out of range (0-359)");
        this.rotation = rotation;
    }
}
