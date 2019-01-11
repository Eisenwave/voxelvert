package eisenwave.vv.rp;

import eisenwave.torrens.schematic.BlockKey;
import eisenwave.torrens.schematic.BlockKeyMap;
import eisenwave.torrens.util.ColorMath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class BlockColorTable {
    
    /** Accept no blocks with a smaller space occupation */
    public final static int
        INSIST_OCCUPATION = 1,
    /** Do not use tint */
    UNTINTED = 1 << 1,
    /** Completely ignore alpha channel */
    IGNORE_ALPHA = 1 << 2,
    /** Accept no blocks with lower alpha */
    INSIST_ALPHA = 1 << 3;
    
    private final Map<BlockKey, BlockColor> map = new BlockKeyMap<>();
    
    public BlockColorTable() {}
    
    public int size() {
        return map.size();
    }
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Nullable
    public BlockColor get(@NotNull String key) {
        return get(BlockKey.minecraft(key));
    }
    
    @Nullable
    public BlockColor get(@NotNull BlockKey key) {
        return map.get(key);
    }
    
    @Nullable
    public BlockColor put(@NotNull BlockKey key, @NotNull BlockColor color) {
        return map.put(key, color);
    }
    
    @NotNull
    public Set<Map.Entry<BlockKey, BlockColor>> entrySet() {
        return map.entrySet();
    }
    
    @Nullable
    public BlockKey get(int rgb, boolean safetyFilter) {
        Stream<Map.Entry<BlockKey, BlockColor>> stream = map.entrySet().stream();
        
        // filters
        if (safetyFilter) stream = stream.filter(entry -> {
            //if (entry.getKey().getId() == 78) System.out.println(entry.getKey()+" "+entry.getValue().isWhole());
            //System.out.println(entry.getKey()+" "+entry.getValue().isWhole());
            BlockColor value = entry.getValue();
            return value.isWhole()
                && !value.isPhysicsAffected()
                && !value.isTransparent()
                && !value.isVolatile();
        });
        if (ColorMath.isSolid(rgb))
            stream = stream.filter(entry -> entry.getValue().isSolid());
        
        Map.Entry<BlockKey, BlockColor> entry = stream.min((entryA, entryB) -> {
            int diffA = ColorMath.visualDiff(rgb, entryA.getValue().getRGB());
            int diffB = ColorMath.visualDiff(rgb, entryB.getValue().getRGB());
            return diffA - diffB;
        }).orElse(null);
        
        return entry == null? null : entry.getKey();
    }
    
    /*
     * Attempts to find the {@link BlockKey} which is most accurately described by a given {@link BlockColor}.
     *
     * @param color the color of the block
     * @param flags query settings
     * @return the block most accurately described by the color
     *
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
    */
    
    // MISC
    
    @Override
    public String toString() {
        return BlockColorTable.class.getSimpleName() + "{size=" + map.size() + "}";
    }
    
}
