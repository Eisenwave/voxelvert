package net.grian.vv.util;

import net.grian.vv.clsvert.ClassverterImageToTexture;
import net.grian.vv.clsvert.ClassverterTextureToImage;
import net.grian.vv.clsvert.*;

public final class ConvertUtil {

    private ConvertUtil() {}

    private final static ConversionManager manager = new ConversionManager();

    static {
        manager.add(new ClassverterBitFieldMerger());
        manager.add(new ClassverterBlocksToVoxels());
        manager.add(new ClassverterColorExtractor());
        manager.add(new ClassverterImageToTexture());
        manager.add(new ClassverterMeshToArray());
        manager.add(new ClassverterMeshToQB());
        manager.add(new ClassverterQBToMesh());
        manager.add(new ClassverterRectangleArranger());
        manager.add(new ClassverterTextureToImage());
        manager.add(new ClassverterTextureStackVoxelizer());
        manager.add(new ClassverterTextureVoxelizer());
        manager.add(new ClassverterVoxelMerger());
        manager.add(new ClassverterVoxelsToMC());
        manager.add(new ClassverterVoxelsToOBJ());
        manager.add(new ClassverterVoxelsToQB());
        manager.add(new ClassverterVoxelsToSTL());
        manager.add(new ClassverterVoxelsToBlocks());
        manager.add(new ClassverterVoxelsToTexture());
        manager.add(new ClassverterWorldToBlockArray());
    }

    public static <A,B> B convert(A from, Class<A> fromClass, Class<B> toClass, Object... args) {
        return manager.convert(from, fromClass, toClass, args);
    }

    @SuppressWarnings("unchecked")
    public static <A,B> B convert(A from, Class<B> toClass, Object... args) {
        return convert(from, (Class<A>) from.getClass(), toClass, args);
    }

}
