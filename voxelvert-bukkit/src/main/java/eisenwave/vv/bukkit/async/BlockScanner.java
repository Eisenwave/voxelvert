package eisenwave.vv.bukkit.async;

import net.grian.torrens.object.BoundingBox6i;
import net.grian.torrens.object.Vertex3i;
import net.grian.torrens.schematic.ArrayBlockStructure;
import net.grian.torrens.schematic.BlockStructure;
import org.bukkit.World;

public interface BlockScanner {
    
    abstract BlockStructure getBlocks(World world, BoundingBox6i box);
    
}
