package net.grian.vv.clsvert;

import net.grian.spatium.geo3.BlockSelection;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.vv.core.VoxelMesh;

import javax.annotation.Nullable;
import java.util.logging.Logger;

public class ClassverterVoxelMerger implements Classverter<VoxelArray, VoxelMesh> {

    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }

    @Override
    public Class<VoxelMesh> getTo() {
        return VoxelMesh.class;
    }
    
    @Nullable
    private final Logger logger;
    
    public ClassverterVoxelMerger(Logger logger) {
        this.logger = logger;
    }
    
    public ClassverterVoxelMerger() {
        this(null);
    }

    @Override
    public VoxelMesh invoke(VoxelArray array, Object... args) {
        BlockSelection[] boxes = new ClassverterBitFieldMerger(logger)
            .invoke(array, args);

        return cut(array, boxes); //cut arrays out of array using zones
    }

    private static VoxelMesh cut(VoxelArray array, BlockSelection[] boxes) {
        VoxelMesh result = new VoxelMesh();

        for (BlockSelection box : boxes) {
            final int x = box.getMinX(), y = box.getMinY(), z = box.getMinZ();
            VoxelArray copy = array.copy(x, y, z, box.getMaxX(), box.getMaxY(), box.getMaxZ());
            result.add(x, y, z, copy);
        }

        return result;
    }

}
