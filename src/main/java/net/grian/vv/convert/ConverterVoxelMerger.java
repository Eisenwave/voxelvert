package net.grian.vv.convert;

import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.voxel.BitArray3;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.vv.core.VoxelMesh;
import net.grian.vv.util.ConvertUtil;

public class ConverterVoxelMerger implements Converter<VoxelArray, VoxelMesh> {

    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }

    @Override
    public Class<VoxelMesh> getTo() {
        return VoxelMesh.class;
    }

    @Override
    public VoxelMesh invoke(VoxelArray array, Object... args) {
        BlockSelection[] boxes = ConvertUtil.convert(array, BitArray3.class, BlockSelection[].class, args);

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
