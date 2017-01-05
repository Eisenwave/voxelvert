package net.grian.vv.convert;

import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.geo.BlockVector;
import net.grian.vv.cache.ColorMap;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.util.Arguments;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.awt.*;

public class ConverterBlocksVoxels implements Converter<BlockSelection, VoxelArray> {

    @Override
    public Class<BlockSelection> getFrom() {
        return BlockSelection.class;
    }

    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }

    @Override
    public VoxelArray invoke(BlockSelection blocks, Object... args) {
        Arguments.requireNonnull(blocks, "blocks must not be null");
        Arguments.requireNonnull(args, "args must not be null");
        Arguments.requireMin(args, 2);
        Arguments.requireType(args[0], World.class);
        Arguments.requireType(args[1], ColorMap.class);

        return invoke(blocks, (World) args[0], (ColorMap) args[1]);
    }

    public VoxelArray invoke(BlockSelection blocks, World world, ColorMap colors) {
        VoxelArray voxels = new VoxelArray(blocks.getSizeX(), blocks.getSizeY(), blocks.getSizeZ());
        final int x = blocks.getMinX(), y = blocks.getMinY(), z = blocks.getMinZ();

        for (BlockVector coords : blocks) {
            Block block = world.getBlockAt(coords.getX(), coords.getY(), coords.getZ());
            Color color = colors.getColor(block.getType());
            if (color == null || color.getAlpha() == 0) continue;
            voxels.setRGB(coords.getX()-x, coords.getY()-y, coords.getZ()-z, color.getRGB());
        }

        return voxels;
    }

}
