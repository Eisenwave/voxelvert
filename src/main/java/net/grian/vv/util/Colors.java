package net.grian.vv.util;

import java.awt.Color;

@SuppressWarnings("unused")
public final class Colors {

    public final static int
    INVISIBLE_WHITE = 0x00_FF_FF_FF,
    INVISIBLE_BLACK = 0,
    SOLID_BLACK = 0xFF_00_00_00,
    SOLID_RED =   0xFF_FF_00_00,
    SOLID_GREEN = 0xFF_00_FF_00,
    SOLID_BLUE =  0xFF_00_00_FF,
    SOLID_YELLOW = SOLID_RED   | SOLID_GREEN,
    SOLID_CYAN =   SOLID_GREEN | SOLID_BLUE,
    SOLID_MAGENTA = SOLID_RED  | SOLID_BLUE,
    SOLID_WHITE = SOLID_RED | SOLID_GREEN | SOLID_BLUE,
    DEBUG1 = SOLID_MAGENTA,
    DEFAULT_TINT = 0xFF_8DB360;

    private Colors() {}

    //"CONSTRUCTORS"

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

    public static int fromTintedRGB(int rgb, int tint) {
        final int luma = luminance2(rgb);
        return fromRGB(
                (red(tint) * luma) / 255,
                (green(tint) * luma) / 255,
                (blue(tint) * luma) / 255
        );
    }

    /**
     * Returns the total difference between the components of two colors.
     *
     * @param c1 first color
     * @param c2 second color
     * @param transparency false if transparency is ignored
     * @return difference between colors
     */
    public static int componentDifference(int c1, int c2, boolean transparency) {
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
     * <p>
     *     Returns the visual difference between two colors.
     * </p>
     *
     * <p>
     *     This methods is solely to be used for comparison of differences, the returned value itself is mathematically
     *     useless, its sole purpose is to be compared with other values returned by this method.
     * </p>
     *
     * @return the visual difference between the colors
     */
    public static int visualDifference(int redA, int grnA, int bluA, int redB, int grnB, int bluB) {
        int redM = (redA + redB) >> 1,
            red = redA - redB,
            grn = grnA - grnB,
            blu = bluA - bluB;
        red = ((512 + redM) * red * red) >> 8;
        grn = 4 * grn * grn;
        blu = ((767 - redM) * blu * blu) >> 8;

        return red + grn + blu;
    }

    /**
     * <p>
     *     Returns the visual difference between two colors.
     * </p>
     *
     * <p>
     *     This methods is solely to be used for comparison of differences, the returned value itself is mathematically
     *     useless, its sole purpose is to be compared with other values returned by this method.
     * </p>
     *
     * @param a the first color
     * @param b the second color
     * @return the visual difference between the colors
     */
    public static int visualDifference(int a, int b) {
        return visualDifference(red(a), green(a), blue(a), red(b), green(b), blue(b));
    }

    public static Color multiply(int rgb, float factor) {
        if (factor < 0 ) throw new IllegalArgumentException("invalid factor "+factor);

        int[] argb = split(rgb);

        for (int i = 0; i<4; i++) {
            argb[i] = (int) Math.min(255, argb[i]*factor);
        }

        return new Color(argb[1], argb[2], argb[3], argb[0]);
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

    public static int anaglyph(int r, int g, int b, int a) {
        return fromRGB(
                (r * 30 + g * 59 + b * 11) / 100,
                (r * 30 + g * 70) / 100,
                (r * 30 + b * 70) / 100,
                a);
    }

    public static int anaglyph(int rgb) {
        return anaglyph(red(rgb), green(rgb), blue(rgb), alpha(rgb));
    }

    public static float luminance(float r, float g, float b) {
        //min to compensate for result > 1 due to imprecision
        return Math.min(1, 0.2126F*r + 0.7152F*g + 0.0722F*b);
    }

    public static float luminance(int r, int g, int b) {
        return luminance(r/255F, g/255F, b/255F);
    }

    public static float luminance(int rgb) {
        return luminance(red(rgb), green(rgb), blue(rgb));
    }

    public static int luminance2(int r, int b, int g) {
        return (r+r+r + b + g+g+g+g) >> 3;
    }

    public static int luminance2(int rgb) {
        return luminance2(red(rgb), green(rgb), blue(rgb));
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

    public static boolean isInvisible(int rgb) {
        return (rgb & 0xFF_000000) == 0;
    }

}