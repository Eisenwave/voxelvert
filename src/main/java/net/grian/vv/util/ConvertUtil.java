package net.grian.vv.util;

import net.grian.vv.convert.*;

public final class ConvertUtil {

    private ConvertUtil() {}

    private final static ConversionManager manager = new ConversionManager();

    static {
        manager.add(new ConverterBitFieldMerger());
        manager.add(new ConverterBlocksToVoxels());
        manager.add(new ConverterColorExtractor());
        manager.add(new ConverterImageToTexture());
        manager.add(new ConverterMeshToArray());
        manager.add(new ConverterMeshToQB());
        manager.add(new ConverterQBToMesh());
        manager.add(new ConverterRectangleArranger());
        manager.add(new ConverterTextureToImage());
        manager.add(new ConverterTextureStackVoxelizer());
        manager.add(new ConverterTextureVoxelizer());
        manager.add(new ConverterVoxelMerger());
        manager.add(new ConverterVoxelsToMC());
        manager.add(new ConverterVoxelsToOBJModel());
        manager.add(new ConverterVoxelsToQB());
        manager.add(new ConverterVoxelsToSTL());
        manager.add(new ConverterVoxelsToBlocks());
        manager.add(new ConverterVoxelsToTexture());
        manager.add(new ConverterWorldToBlockArray());
    }

    public static <A,B> B convert(A from, Class<A> fromClass, Class<B> toClass, Object... args) {
        return manager.convert(from, fromClass, toClass, args);
    }

    @SuppressWarnings("unchecked")
    public static <A,B> B convert(A from, Class<B> toClass, Object... args) {
        return convert(from, (Class<A>) from.getClass(), toClass, args);
    }

}
