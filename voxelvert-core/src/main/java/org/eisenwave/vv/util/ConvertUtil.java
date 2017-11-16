package org.eisenwave.vv.util;

import org.eisenwave.vv.clsvert.*;

@Deprecated
public final class ConvertUtil {

    private ConvertUtil() {}

    private final static ConversionManager manager = new ConversionManager();

    static {
        manager.add(new CvBitArrayMerger());
        manager.add(new CvBlocksToVoxelArray());
        manager.add(new CvImageToTexture());
        manager.add(new CvVoxelMeshToVoxelArray());
        manager.add(new CvVoxelMeshToQB());
        manager.add(new CvQBToVoxelMesh());
        manager.add(new CvRectangleArranger());
        manager.add(new CvTextureStackToVoxelArray());
        manager.add(new CvTextureToVoxelArray());
        manager.add(new CvVoxelArrayToVoxelMesh());
        manager.add(new CvVoxelMeshToMC());
        manager.add(new CvVoxelArrayToOBJ());
        manager.add(new CvVoxelArrayToQB());
        manager.add(new CvVoxelArrayToSTL());
        manager.add(new CvVoxelArrayToTexture());
        //manager.add(new ClassverterWorldToBlockArray());
    }

    public static <A,B> B convert(A from, Class<A> fromClass, Class<B> toClass, Object... args) {
        return manager.convert(from, fromClass, toClass, args);
    }

    @SuppressWarnings("unchecked")
    public static <A,B> B convert(A from, Class<B> toClass, Object... args) {
        return convert(from, (Class<A>) from.getClass(), toClass, args);
    }

}
