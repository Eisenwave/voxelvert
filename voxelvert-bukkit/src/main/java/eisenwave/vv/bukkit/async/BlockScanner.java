package eisenwave.vv.bukkit.async;

import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.schematic.BlockStructure;
import org.bukkit.World;

public interface BlockScanner {
    
    abstract BlockStructure getBlocks(World world, BoundingBox6i box);
    
}
