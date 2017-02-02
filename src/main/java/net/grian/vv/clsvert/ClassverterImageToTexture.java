package net.grian.vv.clsvert;

import net.grian.torrens.img.Texture;

import java.awt.image.BufferedImage;

public class ClassverterImageToTexture implements Classverter<BufferedImage, Texture> {

    @Override
    public Class<BufferedImage> getFrom() {
        return BufferedImage.class;
    }

    @Override
    public Class<Texture> getTo() {
        return Texture.class;
    }

    @Override
    public Texture invoke(BufferedImage image, Object... args) {
        final int width = image.getWidth(), height = image.getHeight();
        Texture texture = new Texture(width, height);

        for (int u = 0; u<width; u++) for (int v = 0; v<height; v++)
            texture.set(u, v, image.getRGB(u, v));

        return texture;
    }

}
