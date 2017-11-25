package eisenwave.vv.object;

/**
 * Minecraft model uv coordinates with a texture reference.
 */
public class MCUV {
    
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
    
    /**
     * <p>
     * Returns the minimum x-coordinate.
     * <p>
     * If this value is larger than the {@link #getMaxX()}, the uv is mirrored on the x-axis.
     *
     * @return the minimum x-coordinate
     */
    public int getMinX() {
        return xmin;
    }
    
    /**
     * <p>
     * Returns the minimum y-coordinate.
     * <p>
     * If this value is larger than the {@link #getMaxY()}, the uv is mirrored on the y-axis.
     *
     * @return the minimum y-coordinate
     */
    public int getMinY() {
        return ymin;
    }
    
    /**
     * <p>
     * Returns the maximum x-coordinate.
     * <p>
     * If this value is smaller than the {@link #getMinX()}, the uv is mirrored on the x-axis.
     *
     * @return the maximum x-coordinate
     */
    public int getMaxX() {
        return xmax;
    }
    
    /**
     * <p>
     * Returns the maximum y-coordinate.
     * <p>
     * If this value is smaller than the {@link #getMinY()}, the uv is mirrored on the y-axis.
     *
     * @return the maximum y-coordinate
     */
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
    
    public int getWidth() {
        return Math.abs(xmax - xmin) + 1;
    }
    
    public int getHeight() {
        return Math.abs(ymax - ymin) + 1;
    }
    
    public int getArea() {
        return getWidth() * getHeight();
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
