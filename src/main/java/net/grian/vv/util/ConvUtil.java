package net.grian.vv.util;

import net.grian.vv.convert.*;

public final class ConvUtil {

    private ConvUtil() {}

    private final static ConvManager manager = new ConvManager();

    static {
        manager.add(new ConverterBlocksVoxels());
        manager.add(new ConverterColorExtractor());
        manager.add(new ConverterImageTexture());
        manager.add(new ConverterTextureArranger());
        manager.add(new ConverterTextureImage());
        manager.add(new ConverterTextureStackVoxelizer());
        manager.add(new ConverterTextureVoxelizer());
        manager.add(new ConverterVoxelMerger());
        manager.add(new ConverterVoxelMeshElements());
        manager.add(new ConverterVoxelsBlocks());
        manager.add(new ConverterVoxelsTexture());
        manager.add(new ConverterWorldBlockArray());
    }

    public static <A,B> B convert(A from, Class<A> fromClass, Class<B> toClass, Object... args) {
        return manager.convert(from, fromClass, toClass, args);
    }

    @SuppressWarnings("unchecked")
    public static <A,B> B convert(A from, Class<B> toClass, Object... args) {
        return convert(from, (Class<A>) from.getClass(), toClass, args);
    }

}
