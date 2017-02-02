package net.grian.vv.clsvert;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.PrimMath;
import net.grian.torrens.img.BaseRectangle;
import net.grian.torrens.img.Texture;
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

public class ClassverterRectangleArrangerTest {

    @Test
    public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        long now = System.currentTimeMillis();
        BaseRectangle[] rectangles = new BaseRectangle[512];
        for (int i = 0; i<rectangles.length; i++) {
            Texture texture = new Texture(i+1, i+1);
            float hue = PrimMath.randomFloat(1);

            texture.getGraphics().drawRaster(
                    ColorMath.fromHSB(hue, 0.5F, 0.5F),
                    ColorMath.fromHSB(hue, 0.5F, 0.75F), (i+1)/2);
            rectangles[i] = texture;
        }
        logger.info("created "+rectangles.length+" rectangles in "+(System.currentTimeMillis()-now)+"ms");

        now = System.currentTimeMillis();
        RectangleArrangement arrangement = ConvertUtil.convert(rectangles, RectangleArrangement.class);
        logger.info("arranged "+rectangles.length+" rectangles in "+(System.currentTimeMillis()-now)+"ms");
    
        now = System.currentTimeMillis();
        Texture render = new Texture(arrangement.getWidth(), arrangement.getHeight());
        for (RectangleArrangement.Entry entry : arrangement)
            render.paste((Texture) entry.getRectangle(), entry.getU(), entry.getV());
        logger.info("rendered "+rectangles.length+" rectangles in "+(System.currentTimeMillis()-now)+"ms");

        String path = "D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ClassverterRectangleArrangerTest.png";
        File out = new File(path);
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints "+out);

        now = System.currentTimeMillis();
        ImageIO.write(render.toImage(), "png", out);
        logger.info("exported image in "+(System.currentTimeMillis()-now)+"ms");
    }

}