package net.grian.torrens.img;

import net.grian.spatium.function.Int2Predicate;
import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.TestUtil;
import net.grian.vv.VVTest;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextureCanvasTest {
    
    @Test
    public void floodFill() throws Exception {
        BufferedImage image = new DeserializerImage().fromResource(getClass(), "floodtest.png");
        TextureCanvas canvas = new TextureCanvas(new Texture(image));
        
        Int2Predicate fillCondition = (x,y) -> canvas.getRGB(x,y) == ColorMath.SOLID_WHITE;
        canvas.floodFill(0, 0, fillCondition, ColorMath.SOLID_RED);
        
        BufferedImage result = canvas.getContent().toImage();
        
        File out = new File(VVTest.DIR_FILES, "TextureCanvasTest_floodFill.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException();
        
        new SerializerPNG().toFile(result, out);
    }
    
    @Test
    public void edgeFloodFill() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        BufferedImage image = new DeserializerImage().fromResource(getClass(), "floodtest.png");
        TextureCanvas canvas = new TextureCanvas(new Texture(image));
    
        Int2Predicate fillCondition = (x,y) -> canvas.getRGB(x,y) == ColorMath.SOLID_WHITE;
        long time = TestUtil.millisOf(() -> {
            canvas.edgeFloodFill(fillCondition, ColorMath.SOLID_RED);
        });
        logger.fine("max stack size of fill algorithm = "+TextureCanvas.max_stack);
        logger.fine(time+" ms to fill a "+canvas.getWidth()+"x"+canvas.getHeight()+" image");
        
        BufferedImage result = canvas.getContent().toImage();
        
        File out = new File(VVTest.DIR_FILES, "TextureCanvasTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException();
        
        new SerializerPNG().toFile(result, out);
    }
    
}
