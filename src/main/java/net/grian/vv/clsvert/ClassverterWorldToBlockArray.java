package net.grian.vv.clsvert;

import net.grian.spatium.geo3.BlockSelection;
import net.grian.spatium.voxel.BlockArray;
import net.grian.vv.core.BlockSet;
import net.grian.vv.util.Arguments;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ClassverterWorldToBlockArray implements Classverter<World, BlockArray> {

    @Override
    public Class<World> getFrom() {
        return World.class;
    }

    @Override
    public Class<BlockArray> getTo() {
        return BlockArray.class;
    }

    @Override
    public BlockArray invoke(World world, Object... args) {
        Arguments.requireMin(args, 1);
        Arguments.requireType(args, BlockSet.class);

        return invoke(world, (BlockSet) args[0]);
    }

    @SuppressWarnings("deprecation")
    public BlockArray invoke(World world, BlockSet blocks) {
        if (blocks.isEmpty())
            throw new IllegalArgumentException("cannot convert empty block set");
        BlockSelection bounds = blocks.getBoundaries();
        BlockArray result = new BlockArray(bounds.getSizeX(), bounds.getSizeY(), bounds.getSizeZ());

        int offX = bounds.getMinX(), offY = bounds.getMinY(), offZ = bounds.getMinZ();
        
        bounds.forEach((x, y, z) -> {
            Block block = world.getBlockAt(x, y, z);
            result.setBlock(x-offX, y-offY, z-offZ, (short) block.getTypeId(), block.getData());
        });

        return result;
    }

}
