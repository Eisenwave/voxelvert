package org.eisenwave.vv.clsvert;

public class ClassverterBlocksToVoxelsTest {
    
    /* public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        long now = System.currentTimeMillis();
        BlockArray blocks = new DeserializerSchematicBlocks().fromResource(getClass(), "bunny.schematic");
        logger.fine((System.currentTimeMillis()-now)+": "+blocks);
        
        ExtractableColor[] extractableColors = VVTest.getInstance().getRegistry().getColors("default");
        logger.fine((System.currentTimeMillis()-now)+": got colors");
        
        ZipFile pack = Resources.getZipFile(getClass(), "resourcepacks/default.zip");
        logger.fine((System.currentTimeMillis()-now)+": "+pack);
        
        ColorMap colors = ConvertUtil.convert(pack, ColorMap.class, new Object[] {extractableColors});
        logger.fine((System.currentTimeMillis()-now)+": "+colors);
    
        now = System.currentTimeMillis();
        final int flags = ClassverterBlocksToVoxels.IGNORE_ALPHA | ClassverterBlocksToVoxels.SHOW_MISSING;
        VoxelArray voxels = ConvertUtil.convert(blocks, VoxelArray.class, colors, flags);
        logger.fine((System.currentTimeMillis()-now)+": "+voxels);
    
        now = System.currentTimeMillis();
        Texture texture = ConvertUtil.convert(voxels, Texture.class, Direction.NEGATIVE_Z, true, true);
        BufferedImage image = texture.getImageWrapper();
        File out = new File(VVTest.DIR_FILES, "ClassverterBlocksToVoxelsTest.png");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create file");
        ImageIO.write(image, "png", out);
        logger.fine((System.currentTimeMillis()-now)+": done");
    } */

}