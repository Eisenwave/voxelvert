package net.grian.vv.clsvert;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.PrimMath;
import net.grian.torrens.img.BaseRectangle;
import net.grian.torrens.img.Texture;
import net.grian.torrens.util.ANSI;
import net.grian.vv.VVTest;
import net.grian.vv.core.RectangleArrangement;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class ClassverterRectangleArrangerTest {

    @Test
    public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        long now = System.currentTimeMillis();
        BaseRectangle[] rectangles = new BaseRectangle[32];
        for (int i = 0; i<rectangles.length; i++) {
            Texture texture = new Texture(i+1, i+1);
            float hue = PrimMath.randomFloat(1);

            texture.getGraphics().drawRaster(
                    ColorMath.fromHSB(hue, 0.5F, 0.5F),
                    ColorMath.fromHSB(hue, 0.5F, 0.75F), (i+1)/2);
            rectangles[i] = texture;
        }
        logger.info("created "+rectangles.length+" rectangles "+time(now));

        now = System.currentTimeMillis();
        RectangleArrangement arrangement = ConvertUtil.convert(rectangles, RectangleArrangement.class);
        logger.info("arranged "+rectangles.length+" rectangles "+time(now));
    
        now = System.currentTimeMillis();
        Texture render = new Texture(arrangement.getWidth(), arrangement.getHeight());
        for (RectangleArrangement.Entry entry : arrangement)
            render.paste((Texture) entry.getRectangle(), entry.getU(), entry.getV());
        logger.info("rendered "+rectangles.length+" rectangles "+time(now));
    
        now = System.currentTimeMillis();
        BufferedImage image = render.toImage(false);
        logger.info("conv. to image "+time(now));
        
        String imgFormat = "png";
        {
            File out = new File(VVTest.DIR_FILES, "ClassverterRectangleArrangerTest."+imgFormat);
            if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);
    
            now = System.currentTimeMillis();
            assertTrue(ImageIO.write(image, imgFormat, out));
            logger.info("exported image "+time(now));
        }
    }
    
    private static String time(long before) {
        long time = System.currentTimeMillis()-before;
        return "in "+ANSI.RED+time+"ms"+ANSI.RESET;
    }

}