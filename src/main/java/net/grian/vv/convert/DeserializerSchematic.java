package net.grian.vv.convert;

import net.grian.vv.core.BlockArray;
import net.grian.vv.io.*;
import net.grian.vv.nbt.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class DeserializerSchematic implements Deserializer<BlockArray> {

    @Override
    public BlockArray deserialize(InputStream stream) throws ParseException {
        BlockArray result;
        try {
            NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));

            // Schematic tag
            NamedTag rootTag = nbtStream.readNamedTag();
            nbtStream.close();
            if (!rootTag.getName().equals("Schematic"))
                throw new FileFormatException("Tag 'Schematic' does not exist or is not first");

            CompoundTag schematicTag = (CompoundTag) rootTag.getTag();

            Map<String, Tag> schematic = schematicTag.getValue();
            if (!schematic.containsKey("Blocks"))
                throw new FileSyntaxException("Schematic file is missing a 'Blocks' tag");

            final short
                    sizeX = getChildTag(schematic, "Width",  ShortTag.class).getValue(),
                    sizeY = getChildTag(schematic, "Height", ShortTag.class).getValue(),
                    sizeZ = getChildTag(schematic, "Length", ShortTag.class).getValue();
            result = new BlockArray(sizeX, sizeY, sizeZ);

            String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
            if (!materials.equals("Alpha"))
                throw new FileVersionException("Schematic file is not an Alpha schematic");

            short[] blocks;
            byte[] datas = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();
            {
                byte[] baseBlocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
                byte[] addBlocks = schematic.containsKey("AddBlocks")?
                        getChildTag(schematic, "AddBlocks", ByteArrayTag.class).getValue() : new byte[0];
                blocks = combine(baseBlocks, addBlocks);
            }

            for (int x = 0; x < sizeX; ++x) for (int y = 0; y < sizeY; ++y) for (int z = 0; z < sizeZ; ++z) {
                final int index = y*sizeX*sizeZ + z*sizeX + x;
                result.setBlock(x, y, z, blocks[index], datas[index]);
            }

        } catch (Throwable ex) {
            throw ex instanceof ParseException? (ParseException) ex : new ParseException(ex);
        }

        return result;
    }

    /**
     * Long story short, Mojang didn't think ahead and used a single byte array to store block id's. Surprise, surprise,
     * they added more blocks than expected and now we need a second array to store extra bits for up to 4096 blocks.
     *
     * @param baseBlocks the base array of block id's
     * @param addBlocks the array of additional block id's
     * @return a new array containing the full block id
     */
    private static short[] combine(byte[] baseBlocks, byte[] addBlocks) {
        short[] blocks = new short[baseBlocks.length]; // Have to later combine IDs

        for (int index = 0; index < baseBlocks.length; index++) {
            if ((index >> 1) >= addBlocks.length) { // No corresponding AddBlocks index
                blocks[index] = (short) (baseBlocks[index] & 0xFF);
            } else {
                if ((index & 1) == 0) {
                    blocks[index] = (short) (((addBlocks[index >> 1] & 0x0F) << 8) + (baseBlocks[index] & 0xFF));
                } else {
                    blocks[index] = (short) (((addBlocks[index >> 1] & 0xF0) << 4) + (baseBlocks[index] & 0xFF));
                }
            }
        }

        return blocks;
    }

    /**
     * Get child tag of a NBT structure.
     *
     * @param items The parent tag map
     * @param key The name of the tag to get
     * @param expected The expected type of the tag
     * @return child tag casted to the expected type
     * @throws FileSyntaxException if the tag does not exist or the tag is not of the expected type
     */
    private static <T extends Tag> T getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws FileSyntaxException {

        if (!items.containsKey(key)) {
            throw new FileSyntaxException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new FileSyntaxException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }

    public static class LocalBlock {

        private final int x, y, z, material;
        private final byte data;

        LocalBlock(int x, int y, int z, int material, byte data) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.material = material;
            this.data = data;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public int getMaterial() {
            return material;
        }

        public byte getData() {
            return data;
        }
    }

    public static class LocalChunk {

        private final ArrayList<LocalBlock> blocks;
        private final int chunkX;
        private final int chunkZ;

        public LocalChunk(int chunkX, int chunkZ) {
            blocks = new ArrayList<>();
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        public void addBlock(LocalBlock block) {
            if (block.y >= 0 && block.y < 256)
                blocks.add(block);
        }

        public ArrayList<LocalBlock> getBlocks() {
            return blocks;
        }

        public int getSize() {
            return blocks.size();
        }

        public int getChunkX() {
            return chunkX;
        }

        public int getChunkZ() {
            return chunkZ;
        }
    }

    private static void putBlockInLocalChunk(HashMap<String, LocalChunk> chunks, LocalBlock block) {
        int chunkX, chunkZ;
        chunkX = (block.x) >> 4;
        chunkZ = (block.z) >> 4;
        String chunkString = chunkX + "," + chunkZ;
        LocalChunk chunk;
        if (chunks.containsKey(chunkString))
            chunk = chunks.get(chunkString);
        else {
            chunk = new LocalChunk(chunkX, chunkZ);
            chunks.put(chunkString, chunk);
        }
        chunk.addBlock(block);
    }

}
