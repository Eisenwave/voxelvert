package eisenwave.vv.clsvert;

import eisenwave.torrens.schematic.BlockStructureStream;
import eisenwave.torrens.schematic.legacy.*;
import org.jetbrains.annotations.NotNull;

public class CvBlockStreamToBlockArray implements Classverter<BlockStructureStream, ArrayBlockStructure> {
    
    @Override
    public Class<BlockStructureStream> getFrom() {
        return null;
    }
    
    @Override
    public Class<ArrayBlockStructure> getTo() {
        return null;
    }
    
    @Override
    public ArrayBlockStructure invoke(@NotNull BlockStructureStream stream, @NotNull Object... args) {
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
