package eisenwave.vv.bukkit.async;

import net.grian.bulkEdit.bukkit.wrapper.AsyncWorld;
import net.grian.torrens.object.BoundingBox6i;
import net.grian.torrens.schematic.BlockStructure;
import org.bukkit.World;

public class BulkEditBlockScanner implements BlockScanner {
    
    @Override
    public BlockStructure getBlocks(World world, BoundingBox6i box) {
        return new WorldBlockStructure(AsyncWorld.wrap(world), box);
    }
    
}
