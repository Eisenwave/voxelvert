package eisenwave.vv.clsvert;

import eisenwave.spatium.util.PrimMath;
import eisenwave.vv.object.BaseRectangle;
import eisenwave.torrens.util.ANSI;
import eisenwave.torrens.util.ColorMath;
import eisenwave.spatium.util.TestUtil;
import eisenwave.torrens.img.Texture;
import eisenwave.vv.VVTest;
import eisenwave.vv.object.RectangleArrangement;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class CvRectangleArrangerTest {

    @Test
    public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.INFO);
        
        long now = System.currentTimeMillis();
        BaseRectangle[] rectangles = new BaseRectangle[32];
        for (int i = 0; i < rectangles.length; i++) {
            Texture texture = Texture.alloc(i+1, i+1);
            float hue = PrimMath.randomFloat(1);

            texture.getGraphics().drawRaster(
                    ColorMath.fromHSB(hue, 0.5F, 0.5F),
                    ColorMath.fromHSB(hue, 0.5F, 0.75F), (i+1)/2);
            rectangles[i] = new TextureRectangle(texture);
        }
        logger.info("created "+rectangles.length+" rectangles "+ timeSinceStr(now));

        now = System.currentTimeMillis();
        RectangleArrangement arrangement = new CvRectangleArranger().invoke(rectangles);
        logger.info("arranged "+rectangles.length+" rectangles "+ timeSinceStr(now));
    
        now = System.currentTimeMillis();
        Texture render = Texture.alloc(arrangement.getWidth(), arrangement.getHeight());
        for (RectangleArrangement.Entry entry : arrangement) {
            Texture t = ((TextureRectangle) entry.getRectangle()).getTexture();
            render.paste(t, entry.getU(), entry.getV());
        }
        logger.info("rendered "+rectangles.length+" rectangles "+ timeSinceStr(now));
        
        {
            File out = new File(VVTest.DIR_FILES, "ClassverterRectangleArrangerTest.png");
            if (!out.canWrite()) return;
    
            now = System.currentTimeMillis();
            assertTrue(ImageIO.write(render.getImageWrapper(), "png", out));
            logger.info("exported image "+ timeSinceStr(now));
        }
    }
    
    @SuppressWarnings("ConstantConditions")
    @Test
    public void performance() {
        final boolean printEff = false;
        final int count = 300, times = 10;
        
        BaseRectangle[] rectangles = new BaseRectangle[count];
        for (int i = 0; i < rectangles.length; i++) {
            int width = PrimMath.randomInt(1, count);
            int height = PrimMath.randomInt(1, count);
            rectangles[i] = new EmptyRectangle(width, height);
        }
    
        CvRectangleArranger clsVerter = new CvRectangleArranger();
        
        long time = TestUtil.millisOf(() -> clsVerter.invoke(rectangles), times);
        
        System.out.println("arranging "+count+" rectangles "+times+" times took "+timeTotalStr(time));
        
        RectangleArrangement result = clsVerter.invoke(rectangles);
        
        System.out.println("result: "+ ANSI.FG_YELLOW+result+ANSI.RESET);
        if (printEff)
            System.out.println("efficiency: "+ ANSI.FG_YELLOW+result.getEfficiency()+ANSI.RESET);
    }
    
    private static class EmptyRectangle implements BaseRectangle {
        
        private final int width, height;
        
        public EmptyRectangle(int w, int h) {
            this.width = w;
            this.height = h;
        }
    
        @Override
        public int getWidth() {
            return width;
        }
    
        @Override
        public int getHeight() {
            return height;
        }
        
    }
    
    private static class TextureRectangle implements BaseRectangle {
        private final Texture texture;
        
        public TextureRectangle(Texture t) {
            this.texture = t;
        }
    
        public Texture getTexture() {
            return texture;
        }
    
        @Override
        public int getWidth() {
            return texture.getWidth();
        }
    
        @Override
        public int getHeight() {
            return texture.getHeight();
        }
        
    }
    
    private static String timeSinceStr(long before) {
        return timeTotalStr(System.currentTimeMillis()-before);
    }
    
    private static String timeTotalStr(long time) {
        return "in "+ANSI.FG_RED+time+" ms"+ANSI.RESET;
    }

}
