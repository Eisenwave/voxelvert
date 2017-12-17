package net.grian.torrens.util.img;

import eisenwave.spatium.function.Int2Predicate;
import eisenwave.torrens.img.*;
import eisenwave.torrens.util.ColorMath;
import eisenwave.spatium.util.TestUtil;
import eisenwave.vv.VVTest;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextureCanvasTest {
    
    @Test
    public void floodFill() throws Exception {
        BufferedImage image = new DeserializerImage().fromResource(getClass(), "floodtest.png");
        Texture texture = Texture.wrapOrCopy(image);
        PixelCanvas canvas = texture.getGraphics();
        
        Int2Predicate fillCondition = (x,y) -> canvas.getRGB(x,y) == ColorMath.SOLID_WHITE;
        canvas.floodFill(0, 0, fillCondition, ColorMath.SOLID_RED);
        
        File out = new File(VVTest.DIR_FILES, "TextureCanvasTest_floodFill.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException();
        
        new SerializerPNG().toFile(texture.getImageWrapper(), out);
    }
    
    @Test
    public void edgeFloodFill() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        BufferedImage image = new DeserializerImage().fromResource(getClass(), "floodtest.png");
        Texture texture = Texture.wrapOrCopy(image);
        PixelCanvas canvas = texture.getGraphics();
    
        Int2Predicate fillCondition = (x,y) -> canvas.getRGB(x,y) == ColorMath.SOLID_WHITE;
        long time = TestUtil.millisOf(() -> canvas.edgeFloodFill(fillCondition, ColorMath.SOLID_RED));
        logger.fine(time+" ms to paste a "+canvas.getWidth()+"x"+canvas.getHeight()+" image");
        
        File out = new File(VVTest.DIR_FILES, "TextureCanvasTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException();
        
        new SerializerPNG().toFile(texture.getImageWrapper(), out);
    }
    
    @Test
    public void graphicsTest() throws Exception {
        BufferedImage image = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(new Color(32, 32, 32));
        graphics.fillRect(0, 0, 1024, 1024);
        graphics.translate(512, 512);
        graphics.setColor(Color.ORANGE);
        graphics.drawLine(-512, -512, 512, 512);
    
        File out = new File(VVTest.DIR_FILES, "GraphicsTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException();
    
        new SerializerPNG().toFile(image, out);
    }
    
}
