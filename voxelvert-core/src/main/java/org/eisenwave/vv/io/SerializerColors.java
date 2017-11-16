package org.eisenwave.vv.io;

import eisenwave.commons.io.Serializer;
import net.grian.torrens.schematic.BlockKey;
import org.eisenwave.vv.object.BlockColor;
import org.eisenwave.vv.object.ColorMap;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class SerializerColors implements Serializer<ColorMap> {
    
    public final static int VERSION = 1;
    
    @Override
    public void toStream(ColorMap map, OutputStream stream) throws IOException {
        DataOutputStream dataStream = new DataOutputStream(stream);
        dataStream.writeByte('B');
        dataStream.writeByte('C');
        dataStream.writeByte('T');
        dataStream.write(VERSION);
        dataStream.write(map.size());
        
        for (Map.Entry<BlockKey, BlockColor> entry : map.entrySet()) {
            BlockKey block = entry.getKey();
            BlockColor color = entry.getValue();
            dataStream.writeByte(block.getId());
            dataStream.writeByte(block.getData());
            dataStream.writeInt(color.getRGB());
            dataStream.writeShort(color.getVoxelVolume());
        }
    }
    
    /*
    @Override
    public void toStream(ColorMap colors, OutputStream stream) throws IOException {
        toStream(colors, new NBTOutputStream(stream));
    }
    
    public void toStream(ColorMap colors, NBTOutputStream stream) throws IOException {
        List<NBTCompound> compounds = new LinkedList<>();
        
        for (Map.Entry<BlockKey, BlockColor> entry : colors.entrySet()) {
            BlockKey block = entry.getKey();
            BlockColor color = entry.getValue();
            
            NBTCompound compound = new NBTCompound();
            compound.putInt("id", block.getId());
            compound.putByte("data", block.getData());
            compound.putInt("rgb", color.getRGB());
            compound.putFloat("volume", color.getRelativeVolume());
            compound.putByte("tint", (byte) color.getTint());
            
            compounds.add(compound);
        }
        
        NBTList list = new NBTList(NBTType.COMPOUND);
        list.addAll(compounds);
        
        stream.writeNamedTag(new NBTNamedTag("Colors", list));
    }
    */
    
}
