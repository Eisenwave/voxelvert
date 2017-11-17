package org.eisenwave.vv.io;

import net.grian.torrens.error.FileFormatException;
import net.grian.torrens.error.FileVersionException;
import net.grian.torrens.io.Deserializer;
import net.grian.torrens.schematic.BlockKey;
import org.eisenwave.vv.rp.BlockColorTable;
import org.eisenwave.vv.rp.BlockColor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DeserializerBCT implements Deserializer<BlockColorTable> {
    
    @NotNull
    @Override
    public BlockColorTable fromStream(InputStream stream) throws IOException {
        DataInputStream dataStream = new DataInputStream(stream);
        verifyHeader(dataStream);
        int count = dataStream.readInt();
        
        BlockColorTable result = new BlockColorTable();
        for (int i = 0; i < count; i++) {
            int id = dataStream.readUnsignedByte();
            byte data = dataStream.readByte();
            int argb = dataStream.readInt();
            short volume = dataStream.readShort();
            
            result.put(new BlockKey(id, data), new BlockColor(argb, volume));
        }
        
        return result;
    }
    
    private static void verifyHeader(DataInputStream stream) throws IOException {
        if (stream.readByte() != 'B'
            || stream.readByte() != 'C'
            || stream.readByte() != 'T')
            throw new FileFormatException("file is not a block colors table, bct's must start with ASCII \"BCT\"");
        int version = stream.readInt();
        if (version != 1)
            throw new FileVersionException("version " + version + " is not supported");
    }
    
    /*
    @NotNull
    @Override
    public ColorMap fromStream(InputStream stream) throws IOException {
        NBTNamedTag root = new NBTDeserializer(false).fromStream(stream);
        if (!root.getName().equals("Colors"))
            throw new FileFormatException("provided NBT format is not a colors database (" + root.getName() + ")");
        
        ColorMap colors = new ColorMap();
        NBTList list = (NBTList) root.getTag();
        
        if (!list.getElementType().equals(NBTType.COMPOUND))
            throw new FileSyntaxException("color list must be a list of compounds (" + list.getElementType() + ")");
        
        for (NBTTag entry : list) {
            NBTCompound compound = (NBTCompound) entry;
            
            final short id = compound.getShort("id");
            final byte data = compound.getByte("data");
            final int rgb = compound.getInt("rgb");
            final byte tint = compound.getByte("tint");
            final float volume = compound.getFloat("volume");
            
            colors.put(new BlockKey(id, data), new BlockColor(rgb, volume, tint));
        }
        
        return colors;
    }
    */
    
}
