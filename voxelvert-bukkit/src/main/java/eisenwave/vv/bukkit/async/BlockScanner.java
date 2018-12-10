package eisenwave.vv.bukkit.async;

import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.schematic.BlockStructureStream;
import eisenwave.torrens.schematic.legacy.LegacyBlockStructure;
import org.bukkit.World;

public interface BlockScanner {
    
    abstract BlockStructureStream getBlocks(World world, BoundingBox6i box);
    
}
