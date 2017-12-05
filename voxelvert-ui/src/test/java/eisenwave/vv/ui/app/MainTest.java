package eisenwave.vv.ui.app;

import eisenwave.vv.ui.Main;
import org.junit.Test;

public class MainTest {
    
    private final static String TEST_DIR = "/home/user/Files/";
    
    @Test
    public void help() throws Exception {
        String[] args = {"--help", "--verbose"};
        
        Main.main(args);
    }
    
    @Test
    public void formats() throws Exception {
        String[] args = {"--formats", "--verbose"};
        
        Main.main(args);
    }
    
    @Test
    public void empty() throws Exception {
        System.setProperty("user.dir", TEST_DIR);
        String[] args = {};
    
        Main.main(args);
    }
    
    @Test
    public void model() throws Exception {
        System.setProperty("user.dir", TEST_DIR);
        String[] args = {"debug.qef", "debug_model.zip", "-o", "model", "-r", "-v"};
        
        Main.main(args);
    }
    
    @Test
    public void voxelize() throws Exception {
        System.setProperty("user.dir", TEST_DIR);
        String[] args = {"sword.obj", "sword.qef", "-r", "-v", "-R", "8"};
        
        Main.main(args);
    }
    
    
    //@Test
    public void main() throws Exception {
        System.setProperty("user.dir", TEST_DIR);
        //String[] args = {"grian_small.png", "debug_from_img.qb", "-d", "e", "-r", "--verbose"};
        //String[] args = {"default.zip", "default.colors", "--verbose"};
        //String[] args = {"bunny.schematic", "bunny.qb", "--verbose"};
        String[] args = {"something.schematic", "something_up.png", "-d", "u", "--crop", "-r", "--verbose"};
        //String[] args = {"bunny.schematic", "bunny.stl", "-r", "--verbose"};
        
        Main.main(args);
    }
    
}
