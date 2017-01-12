package net.grian.vv.io;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;

public class SerializerImage implements Serializer<RenderedImage> {

    @Override
    public void serialize(RenderedImage image, OutputStream stream) throws IOException {
        ImageIO.write(image, "png", stream);
    }

}
