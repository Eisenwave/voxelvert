package net.grian.vv.wedit;

import com.sk89q.worldedit.regions.Region;
import net.grian.spatium.geo3.BlockVector;
import net.grian.vv.core.BlockSet;

public final class WEUtil {
    
    private WEUtil() {}
    
    public static BlockSet blockSetOf(Region region) {
        BlockSet result = new BlockSet();
        for (com.sk89q.worldedit.BlockVector vector : region)
            result.add(vectorOf(vector));
        
        return result;
    }
    
    public static BlockVector vectorOf(com.sk89q.worldedit.BlockVector vector) {
        return BlockVector.fromXYZ(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }
    
    
}
