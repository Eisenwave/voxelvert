package eisenwave.vv.bukkit.async;

import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.schematic.BlockStructure;
import org.bukkit.World;

public class SimpleBlockScanner implements BlockScanner {
    
    @Override
    public BlockStructure getBlocks(World world, BoundingBox6i box) {
        return new WorldBlockStructure(world, box);
    }
    
}
