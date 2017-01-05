package net.grian.vv.util;

import java.awt.Color;

/**
 * A class for dealing with java.awt.Color or an ARGB representation of colors.
 * Contains methods for comparing colors, stacking them, initializing them and more.
 * @author Jan "Headaxe" Schultke
 *
 */
public final class Colors {

    public final static int
    INVISIBLE_WHITE = 0x00_FF_FF_FF,
    INVISIBLE_BLACK = 0,
    SOLID_WHITE = 0xFF_FF_FF_FF,
    SOLID_BLACK = 0xFF_FF_FF_FF,
    SOLID_RED = 0xFF_FF_00_00;

    private Colors() {}

    public static Color asColorRGB(int red, int green, int blue, int alpha) {
        return new Color(red, green, blue, alpha);
    }

    public static Color asColorRGB(int red, int green, int blue) {
        return new Color(red, green, blue, 255);
    }

    public static Color asColorHSB(int hue, int saturation, int brightness, int alpha) {
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        color = (color & 0x00FFFFFF) | (alpha<<24);

        return new Color (color, true);
    }

    public static Color asColorHSB(int hue, int saturation, int brightness) {
        int color = Color.HSBtoRGB(hue, saturation, brightness);

        return new Color (color, true);
    }

    public static int fromRGB(int r, int g, int b, int a) {
        if (r > 0xFF || g > 0xFF || b > 0xFF || a > 0xFF)
            throw new IllegalArgumentException("channel out of range (> 0xFF");
        if (r < 0 || g < 0 || b < 0 || a < 0)
            throw new IllegalArgumentException("channel out of range (< 0");

        return a<<24 | r<<16 | g<<8 | b;
    }

    private static int fromRGB(float r, float g, float b, float a) {
        return fromRGB((int) (r*255), (int) (g*255), (int) (b*255), (int) (a*255));
    }

    public static int fromRGB(int r, int g, int b) {
        return fromRGB(r, g, b, 0xFF);
    }

    public static int fromHSB(int h, int s, int b, int a) {
        return (fromHSB(h, s, b) & 0x00FFFFFF) | (a<<24);
    }

    public static int fromHSB(int r, int g, int b) {
        return Color.HSBtoRGB(r, g, b);
    }

    private static Color blendColorsUnchecked(int c1, int c2, float weight1, float weight2) {
        int[] argb1 = split(c1), argb2 = split(c2);
        final int[] argb = new int[4];

        for (int i = 0; i < 4; i++) {
            argb[i] = (int) (argb1[i]*weight1 + argb2[i]*weight2);
        }

        return new Color(argb[1], argb[2], argb[3], argb[0]);
    }

    /**
     * Blends two colors based on their weighting.
     *
     * @param c1 the first color
     * @param c2 the second color
     * @param weight1 the weight of the first color
     * @param weight2 the weight of the second color
     * @return the new blended color
     * @throws IllegalArgumentException if any weight is smaller than 0, NaN, positive infinity or negative infinity
     */
    public static Color blendColors(int c1, int c2, float weight1, float weight2) {
        if (weight1 < 0 || Float.isInfinite(weight1) || Float.isNaN(weight1))
            throw new IllegalArgumentException("invalid weight1");
        if (weight2 < 0 || Float.isInfinite(weight2) || Float.isNaN(weight2))
            throw new IllegalArgumentException("invalid weight2");

        float
                weight_n1 = weight1 / (weight1+weight2),
                weight_n2 = weight2 / (weight1+weight2);

        return blendColorsUnchecked(c1, c2, weight_n1, weight_n2);
    }

    /**
     * Blends two colors based on a ratio of importance.
     *
     * @param c1 the first color
     * @param c2 the second color
     * @param ratio the ratio between the two colors. Must be greater than 0 and smaller than infinity
     * @return the new blended color
     * @throws IllegalArgumentException if ratio is 0 or smaller; or NaN, positive infinity or negative infinity
     */
    public static Color blendColors(int c1, int c2, float ratio) {
        if (ratio < 0 || Float.isInfinite(ratio) || Float.isNaN(ratio))
            throw new IllegalArgumentException("invalid ratio");

        float
                weight1 = (ratio >= 1) ? ratio : 1,
                weight2 = (ratio >= 1) ? 1 : 1/ratio,
                weight_n1 = weight1 / (weight1+weight2),
                weight_n2 = weight2 / (weight1+weight2);

        return blendColorsUnchecked(c1, c2, weight_n1, weight_n2);
    }

    public static Color blendColors(int c1, int c2) {
        return blendColorsUnchecked(c1,c2,0.5f,0.5f);
    }

    /**
     * Blends several colors with equal weighting.
     *
     * @param colors an array of argb integers.
     * @return the new blended color
     */
    public static Color blendColors(int...colors) {
        int[] argb = new int[4];

        for (int c : colors) {
            int[] comp = split(c);
            for (int i = 0; i < 4; i++) {
                argb[i] += comp[i];
            }
        }

        for (int i = 0; i < 4; i++) {
            argb[0] /= colors.length;
        }

        return new Color(argb[1], argb[2], argb[3], argb[0]);
    }

    /**
     * Returns the total difference between the components of two colors.
     * @param c1 first color
     * @param c2 second color
     * @param transparency false if transparency is ignored
     * @return difference between colors
     */
    public static int colorDifference(int c1, int c2, boolean transparency) {
        int a1= (c1>>24) &255;
        int r1= (c1>>16) &255;
        int g1= (c1>>8)  &255;
        int b1= c1       &255;

        int a2= (c2>>24) &255;
        int r2= (c2>>16) &255;
        int g2= (c2>>8)  &255;
        int b2= c2       &255;

        if (transparency) {
            return Math.abs(b2-b1) + Math.abs(g2-g1) + Math.abs(r2-r1) + Math.abs(a2-a1);
        }
        else {
            return Math.abs(b2-b1) + Math.abs(g2-g1) + Math.abs(r2-r1);
        }
    }

    /**
     * Returns the total difference between the components of two colors.
     * @param c1 first color
     * @param c2 second color
     * @param transparency false if transparency is ignored
     * @return difference between colors
     */
    public static int colorDifference(Color c1, Color c2, boolean transparency) {
        return colorDifference(c1.getRGB(), c2.getRGB(), transparency);
    }

    /**
     * Adds the A, R, G and B values of an array of colors together.
     *
     * @param colors the array of colors
     * @return a new color
     */
    public static Color add(int...colors) {
        int r=0, g=0, b=0, a=0;
        for (int color : colors) {
            int[] array = split(color);

            r += array[1];
            g += array[2];
            b += array[3];
            a += array[0];
        }

        return new Color(Math.min(255, r), Math.min(255, g), Math.min(255, b), Math.min(255, a));
    }

    public static Color multiply(int rgb, float factor) {
        if (factor < 0 ) throw new IllegalArgumentException("invalid factor "+factor);

        int[] argb = split(rgb);

        for (int i = 0; i<4; i++) {
            argb[i] = (int) Math.min(255, argb[i]*factor);
        }

        return new Color(argb[1], argb[2], argb[3], argb[0]);
    }

    /**
     * Checks whether two colors are equal.
     *
     * @param c1 the RGB of the first color
     * @param c2 the RGB of the second color
     * @param transparency true if transparency is taken into consideration, else false
     * @return true if the colors are equal, false if not
     */
    public static boolean colorsAreEqual(int c1, int c2, boolean transparency) {
        if ( ((c1>>24) &0xFF) != (c2>>24) && transparency) return false;
        if ( ((c1>>16) &0xFF) != ((c2>>16) &0xFF))         return false;
        if ( ((c1>>8) &0xFF)  != ((c2>>8) &0xFF))          return false;
        if ( (c1 &0xFF)       != (c2 &0xFF))               return false;
        return true;
    }

    public static int stack(float btmR, float btmG, float btmB, float btmA, float topR, float topG, float topB, float topA) {
        final float
                deficit = (1 - topA),
                outA = topA + btmA*deficit;

        return fromRGB(
                (topR*topA + btmR*btmA*deficit) / outA,
                (topG*topA + btmG*btmA*deficit) / outA,
                (topB*topA + btmB*btmA*deficit) / outA,
                outA);
    }

    public static int stack(int btmR, int btmG, int btmB, int btmA, int topR, int topG, int topB, int topA) {
        return stack(btmR/255F, btmG/255F, btmB/255F, btmA/255F, topR/255F, topG/255F, topB/255F, topA/255F);
    }

    public static int stack(int bottom, int top) {
        final int topAlpha = alpha(top);
        return topAlpha == 0? bottom : stack(
                red(bottom), green(bottom), blue(bottom), alpha(bottom),
                red(top),    green(top),    blue(top),    topAlpha);
    }

    public static int alpha(int rgb) {
        return rgb >> 24 & 0xFF;
    }

    public static int red(int rgb) {
        return rgb >> 16 & 0xFF;
    }

    public static int green(int rgb) {
        return rgb >> 8 & 0xFF;
    }

    public static int blue(int rgb) {
        return rgb & 0xFF;
    }

    /**
     * Splits up an ARGB int into its 4 components (alpha, red, green blue)
     *
     * @param rgb the argb value
     * @return an array of bytes in ARGB order
     */
    public static byte[] asByteArrayARGB(int rgb) {
        return new byte[] {(byte) alpha(rgb), (byte) red(rgb), (byte) green(rgb), (byte) blue(rgb)};
    }

    /**
     * Splits up an ARGB int into its 4 components (alpha, red, green blue)
     *
     * @param rgb the argb value
     * @return an array of ints in ARGB order
     */
    public static int[] split(int rgb) {
        return new int[] {alpha(rgb), red(rgb), green(rgb), blue(rgb)};
    }

    //PREDICATES

    public static boolean isTransparent(int rgb) {
        return (rgb & 0xFF_000000) != 0xFF_000000;
    }

    public static boolean isSolid(int rgb) {
        return (rgb & 0xFF_000000) == 0xFF_000000;
    }

    public static boolean isVisible(int rgb) {
        return (rgb & 0xFF_000000) != 0;
    }

}