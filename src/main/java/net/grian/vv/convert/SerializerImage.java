package net.grian.vv.convert;

import net.grian.vv.io.Serializer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class SerializerImage implements Serializer<BufferedImage> {

    @Override
    public void serialize(BufferedImage image, OutputStream stream) throws IOException {
        ImageIO.write(image, "png", stream);
    }

}
