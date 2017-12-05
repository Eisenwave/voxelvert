package eisenwave.vv.io;

import eisenwave.torrens.io.Serializer;
import eisenwave.torrens.schematic.BlockKey;
import eisenwave.vv.rp.BlockColor;
import eisenwave.vv.rp.BlockColorTable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class SerializerBCT implements Serializer<BlockColorTable> {
    
    public final static int VERSION = 1;
    
    @Override
    public void toStream(BlockColorTable map, OutputStream stream) throws IOException {
        DataOutputStream dataStream = new DataOutputStream(stream);
        dataStream.writeByte('B');
        dataStream.writeByte('C');
        dataStream.writeByte('T');
        dataStream.writeInt(VERSION);
        dataStream.writeInt(map.size());
        
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
