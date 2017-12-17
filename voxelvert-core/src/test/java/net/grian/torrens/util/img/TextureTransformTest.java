package net.grian.torrens.util.img;

import eisenwave.spatium.util.TestUtil;
import eisenwave.torrens.img.DeserializerImage;
import eisenwave.torrens.img.SerializerPNG;
import eisenwave.torrens.img.Texture;
import eisenwave.torrens.img.scale.ScaleBox;
import eisenwave.torrens.img.scale.ScaleNearestNeighbour;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static eisenwave.vv.VVTest.DIR_IMAGE_SCALE;

public class TextureTransformTest {
    
    public final static String NAME = "test";
    
    @Test
    public void scale() throws Exception {
        File file = new File(DIR_IMAGE_SCALE, NAME+".png");
        Texture texture = Texture.wrapOrCopy( new DeserializerImage().fromFile(file) );
        printPNG(texture, NAME+"_org");
        
        for (int i = 0; i < 4; i++) {
            final int dims = (int) Math.pow(4, i);
            
            Texture nnb = new ScaleNearestNeighbour().apply(texture, dims, dims);
            Texture ssa = new ScaleBox().apply(texture, dims, dims);
    
            printPNG(nnb, NAME+"_nnb_"+i);
            printPNG(ssa, NAME+"_ssa_"+i);
        }
    }
    
    public void scale_performance() throws Exception {
        File file = new File(DIR_IMAGE_SCALE, NAME+".png");
        Texture texture = Texture.wrapOrCopy( new DeserializerImage().fromFile(file) );
        printPNG(texture, NAME+"_org");
        final int dims = 128;
        final int tests = 100;
    
        long nnb = TestUtil.millisOf(() -> {
            for (int i = 0; i < tests; i++)
                new ScaleNearestNeighbour().apply(texture, dims, dims);
        });
    
        long ssa = TestUtil.millisOf(() -> {
            for (int i = 0; i < tests; i++)
                new ScaleBox().apply(texture, dims, dims);
        });
    
        System.out.println("scaled "+texture+" to "+dims+"x"+dims+": ("+tests+" times)");
        System.out.println("nearest neighbour: "+nnb+" ms");
        System.out.println("subsect average: "+ssa+" ms");
    }
    
    private static void printPNG(Texture texture, String name) throws IOException {
        File file = new File(DIR_IMAGE_SCALE, name+".png");
        new SerializerPNG().toFile(texture.toImage(true), file);
    }
    
}
