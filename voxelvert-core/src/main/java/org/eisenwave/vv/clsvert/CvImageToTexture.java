package org.eisenwave.vv.clsvert;

import net.grian.torrens.img.Texture;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class CvImageToTexture implements Classverter<BufferedImage, Texture> {
    
    @Override
    public Class<BufferedImage> getFrom() {
        return BufferedImage.class;
    }
    
    @Override
    public Class<Texture> getTo() {
        return Texture.class;
    }
    
    @Override
    public Texture invoke(@NotNull BufferedImage image, @NotNull Object... args) {
        return Texture.copy(image);
    }
    
}
