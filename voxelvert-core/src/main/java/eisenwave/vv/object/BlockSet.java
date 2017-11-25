package eisenwave.vv.object;

import net.grian.torrens.object.BoundingBox6i;
import net.grian.torrens.object.Vertex3i;

import java.util.HashSet;

public class BlockSet extends HashSet<Vertex3i> {
    
    public BlockSet() {}
    
    public BoundingBox6i getBoundaries() {
        int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;
        
        for (Vertex3i block : this) {
            int x = block.getX(), y = block.getY(), z = block.getZ();
            
            if (x < minX) minX = x;
            if (y < minY) minY = y;
            if (z < minZ) minZ = z;
            if (x > maxX) maxX = x;
            if (y > maxY) maxY = y;
            if (z > maxZ) maxZ = z;
        }
        
        return new BoundingBox6i(minX, minY, minZ, maxX, maxY, maxZ);
    }
    
    public boolean contains(int x, int y, int z) {
        return this.contains(new Vertex3i(x, y, z));
    }
    
    public void add(int x, int y, int z) {
        this.add(new Vertex3i(x, y, z));
    }
    
    public void remove(int x, int y, int z) {
        this.remove(new Vertex3i(x, y, z));
    }
    
}
