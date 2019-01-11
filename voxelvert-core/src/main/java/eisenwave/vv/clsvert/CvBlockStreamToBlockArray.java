package eisenwave.vv.clsvert;

import eisenwave.torrens.schematic.BlockStructureStream;
import eisenwave.torrens.schematic.legacy.*;
import org.jetbrains.annotations.NotNull;

public class CvBlockStreamToBlockArray implements Classverter<BlockStructureStream, ArrayBlockStructure> {
    
    @Deprecated
    @Override
    public ArrayBlockStructure invoke(@NotNull BlockStructureStream stream, @NotNull Object... args) {
        return invoke(stream);
    }
    
    public static ArrayBlockStructure invoke(@NotNull BlockStructureStream stream) {
        ArrayBlockStructure blocks = new ArrayBlockStructure(stream.getSizeX(), stream.getSizeY(), stream.getSizeZ());
        stream.forEach(block -> {
            LegacyBlockKey legacyKey = MicroLegacyUtil.getByMinecraftKey13(block.getKey());
            //System.err.println(block.getKey() + " -> " + legacyKey);
            if (legacyKey != null)
                blocks.setBlock(block.getX(), block.getY(), block.getZ(), legacyKey.getId(), legacyKey.getData());
        });
        
        return blocks;
    }
    
}
