package net.grian.vv.convert;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.PrimMath;
import net.grian.torrens.object.BaseRectangle;
import net.grian.torrens.object.Texture;
import net.grian.vv.core.RectangleArrangement;
import net.grian.vv.util.ConvertUtil;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConverterRectangleArrangerTest {

    @Test
    public void invoke() throws Exception {
        BaseRectangle[] rectangles = new BaseRectangle[512];
        for (int i = 0; i<rectangles.length; i++) {
            Texture texture = new Texture(i+1, i+1);
            float hue = PrimMath.randomFloat(1);

            texture.getGraphics().drawRaster(
                    ColorMath.fromHSB(hue, 0.5F, 0.5F),
                    ColorMath.fromHSB(hue, 0.5F, 0.75F), (i+1)/2);
            rectangles[i] = texture;
        }

        long now = System.currentTimeMillis();
        RectangleArrangement arrangement = ConvertUtil.convert(rectangles, RectangleArrangement.class);
        System.out.println("arranged "+rectangles.length+" rectangles in "+(System.currentTimeMillis()-now)+"ms");

        Texture render = new Texture(arrangement.getWidth(), arrangement.getHeight());
        for (RectangleArrangement.Entry entry : arrangement)
            render.paste((Texture) entry.getRectangle(), entry.getU(), entry.getV());

        BufferedImage image = ConvertUtil.convert(render, BufferedImage.class);

        String path = "D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\ConverterRectangleArrangerTest.png";
        File out = new File(path);
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints "+out);

        ImageIO.write(image, "png", out);
    }

}