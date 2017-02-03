package net.grian.vv;

import net.grian.vv.cache.Registry;

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
    
    public final static File DIRECTORY = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert");
    public final static File DIR_FILES = new File(DIRECTORY, "files");
    public final static File DIR_USERS = new File(DIRECTORY, "users");
    
    private Registry registry;
    
    private VVTest() {
        initRegistry();
    }

    private void initRegistry() {
        registry = new Registry();
        registry.loadResources();
    }

    public Registry getRegistry() {
        return registry;
    }
}
