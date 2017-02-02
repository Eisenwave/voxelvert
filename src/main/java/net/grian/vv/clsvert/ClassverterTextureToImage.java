package net.grian.vv.clsvert;

import net.grian.torrens.img.Texture;

import java.awt.image.BufferedImage;

public class ClassverterTextureToImage implements Classverter<Texture, BufferedImage> {

    @Override
    public Class<Texture> getFrom() {
        return Texture.class;
    }

    @Override
    public Class<BufferedImage> getTo() {
        return BufferedImage.class;
    }

    @Override
    public BufferedImage invoke(Texture from, Object... args) {
        int width = from.getWidth(), height = from.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int u = 0; u<width; u++) for (int v = 0; v<height; v++)
            image.setRGB(u, v, from.get(u, v));

        return image;
    }
}