package org.eisenwave.vv.rp;

import net.grian.torrens.schematic.BlockKey;

import java.util.*;

public class BlockColorTable extends LinkedHashMap<BlockKey, BlockColor> {
    
    /** Accept no blocks with a smaller space occupation */
    public final static int
        INSIST_OCCUPATION = 1,
    /** Do not use tint */
    UNTINTED = 1 << 1,
    /** Completely ignore alpha channel */
    IGNORE_ALPHA = 1 << 2,
    /** Accept no blocks with lower alpha */
    INSIST_ALPHA = 1 << 3;
    
    public BlockColor get(BlockKey key) {
        BlockColor result = super.get(key);
        return (result == null && key.getData() != 0)?
            super.get(new BlockKey(key.getId(), 0)) : result;
    }
    
    /**
     * Attempts to find the {@link BlockKey} which is most accurately described by a given {@link BlockColor}.
     *
     * @param color the color of the block
     * @param flags query settings
     * @return the block most accurately described by the color
     */
    public BlockKey get(BlockColor color, int flags) {
        boolean
            insistOcc = (flags & INSIST_OCCUPATION) != 0,
            untinted = (flags & UNTINTED) != 0,
            ignoreA = (flags & IGNORE_ALPHA) != 0;
        
        Map.Entry<BlockKey, BlockColor> bestFit = null;
        for (Map.Entry<BlockKey, BlockColor> entry : entrySet()) {
        
        }
        
        return bestFit == null? null : bestFit.getKey();
    }
    
    
    // MISC
    
    @Override
    public String toString() {
        return BlockColorTable.class.getSimpleName() + "{size=" + size() + "}";
    }
    
}
