package eisenwave.vv.io;

import eisenwave.torrens.schematic.legacy.LegacyBlockKey;

public class DeserializerSchematicTest {
    
    private final static LegacyBlockKey
        COAL_BLOCK = new LegacyBlockKey(173, 0);
    
    /* public void deserialize() throws Exception {
        BlockArray blocks = new DeserializerSchematicBlocks().fromResource(getClass(), "bunny.schematic");

        assertEquals(COAL_BLOCK, blocks.getBlock(50, 30, 36));

        ExtractableColor[] extractableColors = VVTest.getInstance().getRegistry().getColors("default");
        ZipFile pack = Resources.getZipFile(getClass(), "resourcepacks/default.zip");
        ColorMap colors = ConvertUtil.convert(pack, ColorMap.class, new Object[] {extractableColors});
        final int flags = ClassverterBlocksToVoxels.SHOW_MISSING;

        VoxelArray voxels = ConvertUtil.convert(blocks, VoxelArray.class, colors, flags);
        Texture front = ConvertUtil.convert(voxels, Texture.class, Direction.NEGATIVE_Z, true, false);
        BufferedImage image = front.getImageWrapper();

        File out = new File(VVTest.DIR_FILES, "DeserializerSchematicTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to fromPoints "+out);

        new SerializerPNG().toFile(image, out);
    } */

}
