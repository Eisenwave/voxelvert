package eisenwave.vv.bukkit.async;

import eisenwave.inv.util.LegacyUtil;
import eisenwave.spatium.util.Incrementer3;
import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.schematic.BlockKey;
import eisenwave.torrens.schematic.BlockStructureStream;
import eisenwave.torrens.schematic.StructureBlock;
import eisenwave.torrens.schematic.legacy.*;
import org.bukkit.World;
import org.bukkit.block.Block;

public class WorldBlockScanner implements BlockScanner {
    
    @Override
    public BlockStructureStream getBlocks(World world, BoundingBox6i box) {
        final int
            offX = box.getMinX(),
            offY = box.getMinY(),
            offZ = box.getMinZ(),
            limX = box.getSizeX(),
            limY = box.getSizeY(),
            limZ = box.getSizeZ();
        
        final Incrementer3 incrementer = new Incrementer3(limX, limY, limZ);
        
        return new BlockStructureStream() {
            @Override
            public int getSizeX() {
                return limX;
            }
            
            @Override
            public int getSizeY() {
                return limY;
            }
            
            @Override
            public int getSizeZ() {
                return limZ;
            }
            
            @Override
            public int getVolume() {
                return box.getVolume();
            }
            
            @Override
            public boolean hasNext() {
                return incrementer.canIncrement();
            }
            
            @SuppressWarnings("deprecation")
            @Override
            public StructureBlock next() {
                int[] pos = incrementer.getAndIncrement();
                
                Block block = world.getBlockAt(pos[0] + offX, pos[1] + offY, pos[2] + offZ);
                BlockKey key;
                if (LegacyUtil.isApi13()) {
                    key = BlockKey.parse(block.getBlockData().getAsString());
                }
                else {
                    int id = block.getType().getId();
                    byte data = block.getData();
                    LegacyBlockKey legacyKey = new LegacyBlockKey(id, data);
                    key = MicroLegacyUtil.getByLegacyKey(legacyKey);
                    if (key == null)
                        throw new IllegalStateException("no translation for " + legacyKey);
                }
                
                return new StructureBlock(pos[0], pos[1], pos[2], key);
            }
        };
    }
    
}
