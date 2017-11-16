package org.eisenwave.vv;

import java.io.File;
import java.util.logging.Logger;

public class VVTest {

    private static VVTest instance = null;

    public synchronized static VVTest getInstance() {
        return instance==null? instance = new VVTest() : instance;
    }
    
    public final static Logger LOGGER = Logger.getLogger("voxelvert.debug");
    static {
        LOGGER.setUseParentHandlers(false);
        LOGGER.addHandler(new VVLoggingHandler());
    }
    
    public final static File
        DIRECTORY = new File("/home/user/Files/"),
        DIR_FILES = new File("/home/user/Files/"),
        DIR_IMAGE_SCALE = new File(DIRECTORY, "img_scale");
    
}
