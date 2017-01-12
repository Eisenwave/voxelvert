package net.grian.vv.core;

import net.grian.spatium.enums.Direction;
import net.grian.spatium.geo.AxisAlignedBB;

import javax.annotation.Nullable;

/**
 * A Minecraft model element.
 */
public class MCElement {

    private AxisAlignedBB shape;
    private final MCUV[] uv = new MCUV[Direction.values().length];

    public MCElement(AxisAlignedBB shape) {
        setShape(shape);
    }

    /**
     * Returns the uv of the bounding of a certain side.
     *
     * @param d the direction
     * @return the tile or null if the element has no texture on that side
     */
    @Nullable
    public MCUV getUV(Direction d) {
        return uv[d.ordinal()];
    }

    public AxisAlignedBB getShape() {
        return shape;
    }

    //CHECKERS

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

    //SETTERS

    /**
     * Changes the shape of this element.
     *
     * @param shape the shape
     */
    public void setShape(AxisAlignedBB shape) {
        this.shape = shape.clone();
    }

    /**
     * Removes the UV on one side of the box.
     *
     * @param side the the side to be enabled
     */
    @Deprecated
    public void removeUV(Direction side) {
        uv[side.ordinal()] = null;
    }

    public void setUV(Direction side, MCUV uv) {
        this.uv[side.ordinal()] = uv;
    }

}
