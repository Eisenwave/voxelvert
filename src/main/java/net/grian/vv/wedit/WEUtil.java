package net.grian.vv.wedit;

import com.sk89q.worldedit.regions.Region;
import net.grian.spatium.geo3.BlockVector;
import net.grian.vv.core.BlockSet;

import javax.annotation.Nonnull;

public final class WEUtil {
    
    private WEUtil() {}
    
    public static BlockSet blockSetOf(@Nonnull Region region) {
        BlockSet result = new BlockSet();
        for (com.sk89q.worldedit.BlockVector vector : region)
            result.add(vectorOf(vector));
        
        return result;
    }
    
    public static BlockVector vectorOf(@Nonnull com.sk89q.worldedit.BlockVector vector) {
        return BlockVector.fromXYZ(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }
    
    
}
