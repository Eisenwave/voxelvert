package net.grian.vv.core;

import net.grian.spatium.geo3.BlockSelection;
import net.grian.spatium.geo3.BlockVector;

import java.util.HashSet;

public class BlockSet extends HashSet<BlockVector> {
    
    public BlockSet() {}
    
    public BlockSelection getBoundaries() {
        int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;
        
        for (BlockVector block : this) {
            int x = block.getX(), y = block.getY(), z = block.getZ();
            
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }
        
        return BlockSelection.fromPoints(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    public boolean contains(int x, int y, int z) {
        return this.contains(BlockVector.fromXYZ(x, y, z));
    }
    
    public void add(int x, int y, int z) {
        this.add(BlockVector.fromXYZ(x, y, z));
    }
    
    public void remove(int x, int y, int z) {
        this.remove(BlockVector.fromXYZ(x, y, z));
    }
    
}
