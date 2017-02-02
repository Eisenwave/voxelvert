package net.grian.vv;

import net.grian.vv.cache.Registry;

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
