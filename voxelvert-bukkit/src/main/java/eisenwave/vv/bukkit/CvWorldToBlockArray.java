package eisenwave.vv.bukkit;

import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.schematic.ArrayBlockStructure;
import eisenwave.vv.clsvert.Classverter;
import eisenwave.vv.object.BlockSet;
import eisenwave.vv.util.Arguments;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class CvWorldToBlockArray implements Classverter<World, ArrayBlockStructure> {
    
    @Override
    public Class<World> getFrom() {
        return World.class;
    }
    
    @Override
    public Class<ArrayBlockStructure> getTo() {
        return ArrayBlockStructure.class;
    }
    
    @Override
    public ArrayBlockStructure invoke(@NotNull World world, @NotNull Object... args) {
        Arguments.requireMin(args, 1);
        Arguments.requireType(args, BlockSet.class);
        
        return invoke(world, (BlockSet) args[0]);
    }
    
    @SuppressWarnings("deprecation")
    public ArrayBlockStructure invoke(World world, BlockSet blocks) {
        if (blocks.isEmpty())
            throw new IllegalArgumentException("cannot convert empty block set");
        BoundingBox6i bounds = blocks.getBoundaries();
        ArrayBlockStructure result = new ArrayBlockStructure(bounds.getSizeX(), bounds.getSizeY(), bounds.getSizeZ());
        
        final int offX = bounds.getMinX(), offY = bounds.getMinY(), offZ = bounds.getMinZ();
        
        bounds.forEach((x, y, z) -> {
            Block block = world.getBlockAt(x, y, z);
            result.setBlock(x - offX, y - offY, z - offZ, (short) block.getTypeId(), block.getData());
        });
        
        return result;
    }
    
}
