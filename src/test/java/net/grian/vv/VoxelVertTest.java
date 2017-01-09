package net.grian.vv;

import net.grian.vv.cache.Registry;

public class VoxelVertTest {

    private final static VoxelVertTest instance = new VoxelVertTest();

    public synchronized static VoxelVertTest getInstance() {
        return instance;
    }

    private Registry registry;

    private VoxelVertTest() {
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
