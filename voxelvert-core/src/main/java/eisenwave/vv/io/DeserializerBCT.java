package eisenwave.vv.io;

import eisenwave.torrens.error.FileSyntaxException;
import eisenwave.torrens.schematic.BlockKey;
import eisenwave.torrens.schematic.legacy.MicroLegacyUtil;
import eisenwave.vv.rp.BlockColor;
import eisenwave.vv.rp.BlockColorTable;
import eisenwave.torrens.error.*;
import eisenwave.torrens.io.Deserializer;
import eisenwave.torrens.schematic.legacy.LegacyBlockKey;
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
    
        int version = dataStream.readInt();
        if (version != 1 && version != 2 && version != 3)
            throw new FileVersionException("version " + version + " is not supported");
    
        final int count = dataStream.readInt();
        final boolean
            hasFlags = version >= 2,
            legacyIds = version <= 2;
        
        BlockColorTable result = new BlockColorTable();
        for (int i = 0; i < count; i++) {
            BlockKey key;
            boolean legacy;
            if (legacyIds) {
                LegacyBlockKey legacyKey = new LegacyBlockKey(dataStream.readUnsignedByte(), dataStream.readByte());
                key = MicroLegacyUtil.getByLegacyKey(legacyKey);
                if (key == null)
                    throw new FileSyntaxException("No translation for " + legacyKey);
                legacy = true;
            }
            else {
                String keyStr = dataStream.readUTF();
                legacy = dataStream.readBoolean();
        
                key = BlockKey.parse(keyStr);
            }
            
            int argb = dataStream.readInt();
            short flags = hasFlags? dataStream.readShort() : 0;
            short volume = dataStream.readShort();
    
            result.put(key, new BlockColor(argb, flags, volume, legacy));
        }
        
        return result;
    }
    
    private static void verifyHeader(DataInputStream stream) throws IOException {
        if (stream.readByte() != 'B' || stream.readByte() != 'C' || stream.readByte() != 'T')
            throw new FileFormatException("file is not a block colors table, bct's must start with ASCII \"BCT\"");
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
