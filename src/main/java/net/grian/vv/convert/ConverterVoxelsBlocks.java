package net.grian.vv.convert;

import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.geo.BlockVector;
import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.VoxelArray.Voxel;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.util.Arguments;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ConverterVoxelsBlocks implements Converter<VoxelArray, BlockSelection> {

    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }

    @Override
    public Class<BlockSelection> getTo() {
        return BlockSelection.class;
    }

    @Override
    public BlockSelection invoke(VoxelArray from, Object[] args) {
        Arguments.requireMin(args, 3);
        Arguments.requireType(args[0], BlockSelection.class);
        Arguments.requireType(args[1], ColorMap.class);
        Arguments.requireType(args[2], World.class);

        BlockSelection selection = (BlockSelection) args[0];
        ColorMap colors = (ColorMap) args[1];
        World world = (World) args[2];

        BlockVector
                min = selection.getMin(),
                max = min.clone().add(from.getSizeX()-1, from.getSizeY()-1, from.getSizeZ()-1);

        BlockSelection target = BlockSelection.between(min, max);


        int x = target.getMinX(), y = target.getMinY(), z = target.getMinZ();

        /*
        for (Voxel v : from) {
            Material material = colors.getMaterial(v.getColor());
            if (material == null) continue;
            Block block = world.getBlockAt(v.getX()+x, v.getY()+y, v.getZ()+z);
            block.setType(material);
        }
        */

        return target;
    }
}
