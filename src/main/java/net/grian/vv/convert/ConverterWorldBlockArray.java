package net.grian.vv.convert;

import net.grian.spatium.geo.BlockSelection;
import net.grian.vv.core.BlockArray;
import net.grian.vv.util.Arguments;
import org.bukkit.World;
import org.bukkit.block.Block;

public class ConverterWorldBlockArray implements Converter<World, BlockArray> {

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
        Arguments.requireType(args, BlockSelection.class);

        return invoke(world, (BlockSelection) args[0]);
    }

    @SuppressWarnings("deprecation")
    public BlockArray invoke(World world, BlockSelection selection) {
        final int
                minX = selection.getMinX(), minY = selection.getMinY(), minZ = selection.getMinZ(),
                maxX = selection.getMaxX(), maxY = selection.getMaxY(), maxZ = selection.getMaxZ();
        BlockArray result = new BlockArray(maxX-minX+1, maxY-minY+1, maxZ-minZ+1, BlockArray.FLAG_BIOMES);

        for (int x = minX; x<=maxX; x++) for (int y = minY; y<=maxY; y++) for (int z = minZ; z<=maxZ; z++) {
            Block block = world.getBlockAt(x, y, z);
            result.setBlock(x, y, z, (short) block.getTypeId(), block.getData());
            result.setBiome(x, y, z, block.getBiome());
        }

        return result;
    }

}
