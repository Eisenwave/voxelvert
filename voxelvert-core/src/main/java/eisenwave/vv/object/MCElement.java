package eisenwave.vv.object;

import eisenwave.spatium.enums.Direction;
import eisenwave.torrens.object.BoundingBox6f;

/**
 * A Minecraft model element.
 */
public class MCElement {
    
    private BoundingBox6f shape;
    private final MCUV[] uv = new MCUV[Direction.values().length];
    
    /**
     * Constructs a new element with a given shape.
     *
     * @param shape the element shape
     */
    public MCElement(BoundingBox6f shape) {
        setShape(shape);
    }
    
    /**
     * Returns the uv of the bounding of a certain side.
     *
     * @param d the direction
     * @return the tile or null if the element has no texture on that side
     */
    public MCUV getUV(Direction d) {
        return uv[d.ordinal()];
    }
    
    /**
     * Returns the amount of faces which have UV mapping.
     *
     * @return the amount of faces with UV mapping
     */
    public int getFaceCount() {
        int result = 0;
        
        for (MCUV anUv : uv)
            if (anUv != null)
                result++;
        
        return result;
    }
    
    /**
     * Returns the shape of the element (without rotation).
     *
     * @return the element shape
     */
    public BoundingBox6f getShape() {
        return shape;
    }
    
    // PREDICATES
    
    /**
     * Returns whether the element has any visible side and is thus visible.
     *
     * @return whether the element is visible
     */
    public boolean isVisible() {
        for (MCUV entry : uv)
            if (entry != null) return true;
        return false;
    }
    
    /**
     * Returns whether the box has uv on a given side.
     *
     * @param side the side of the element
     * @return whether a side of the element is enabled
     */
    public boolean hasUV(Direction side) {
        return uv[side.ordinal()] != null;
    }
    
    // MUTATORS
    
    /**
     * Changes the shape of this element.
     *
     * @param shape the shape
     */
    public void setShape(BoundingBox6f shape) {
        this.shape = shape;
    }
    
    /**
     * Removes the UV on one side of the element.
     *
     * @param side the the side to be enabled
     */
    @Deprecated
    public void removeUV(Direction side) {
        uv[side.ordinal()] = null;
    }
    
    /**
     * Sets the uv of a side of the element.
     *
     * @param side the side
     * @param uv the uv
     */
    public void setUV(Direction side, MCUV uv) {
        this.uv[side.ordinal()] = uv;
    }
    
    // MISC
    
    @Override
    public String toString() {
        return MCElement.class.getSimpleName() +
            "{shape=" + shape + "}";
    }
    
}
