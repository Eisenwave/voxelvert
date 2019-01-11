package eisenwave.vv.clsvert;

import eisenwave.torrens.img.Texture;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;

public class CvImageToTexture implements Classverter<BufferedImage, Texture> {
    
    @Deprecated
    @Override
    public Texture invoke(@NotNull BufferedImage image, @NotNull Object... args) {
        return Texture.copy(image);
    }
    
    public static Texture invoke(@NotNull BufferedImage image) {
        return Texture.copy(image);
    }
    
}
