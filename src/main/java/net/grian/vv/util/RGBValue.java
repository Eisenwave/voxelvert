package net.grian.vv.util;

import java.awt.*;

public interface RGBValue {

    //GETTERS

    /**
     * Returns this object's RGB value.
     *
     * @return this object's RGB value
     */
    public abstract int getRGB();

    /**
     * Returns this object's RGB value wrapped in a {@link Color}.
     *
     * @return this object's color
     */
    public default Color getColor() {
        return new Color(getRGB(), true);
    }

    /**
     * Returns the rgb's alpha value.
     *
     * @return the alpha value
     */
    public default int getAlpha() {
        return getRGB() >> 24;
    }

    /**
     * Returns the rgb's alpha value.
     *
     * @return the alpha value
     */
    public default int getRed() {
        return getRGB() >> 16 & 0xFF;
    }

    /**
     * Returns the rgb's green value.
     *
     * @return the green value
     */
    public default int getGreen() {
        return getRGB() >> 8 & 0xFF;
    }

    /**
     * Returns the rgb's blue value.
     *
     * @return the blue value
     */
    public default int getBlue() {
        return getRGB() & 0xFF;
    }

    //CHECKERS

    /**
     * Checks whether the rgb value is visible.
     * This condition is true if the rgb value has an alpha value of at least 1.
     *
     * @return whether the rgb value is visible
     */
    public default boolean isVisible() {
        return (getRGB() & 0xFF_00_00_00) != 0;
    }

    /**
     * Checks whether the rgb value is invisible.
     * This condition is true if the rgb value has an alpha value of exactly 0.
     *
     * @return whether the rgb value is visible
     */
    public default boolean isInvisible() {
        return (getRGB() & 0xFF_00_00_00) == 0;
    }

    /**
     * Checks whether the rgb value is solid.
     * This condition is true if the color is fully opaque with no transparency.
     *
     * @return whether the rgb value is solid
     */
    public default boolean isSolid() {
        return (getRGB() & 0xFF_00_00_00) == 0xFF_00_00_00;
    }

    /**
     * Checks whether the rgb value is transparent.
     * This condition is true if the color's alpha value is not <code>0xFF</code>.
     *
     * @return whether the rgb value is transparent
     */
    public default boolean isTransparent() {
        return (getRGB() & 0xFF_00_00_00) != 0xFF_00_00_00;
    }

}
