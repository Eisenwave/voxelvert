package net.grian.vv.convert;

import net.grian.spatium.array.Incrementer2;
import net.grian.spatium.util.PrimMath;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.object.OBJMaterial;
import net.grian.torrens.object.Texture;

public class ConverterVoxelsToOBJMaterial implements Converter<VoxelArray, OBJMaterial> {

    @Override
    public Class<VoxelArray> getFrom() {
        return VoxelArray.class;
    }

    @Override
    public Class<OBJMaterial> getTo() {
        return OBJMaterial.class;
    }

    @Override
    public OBJMaterial invoke(VoxelArray voxels, Object... args) {
        final int
                pixels = voxels.size(),
                textureDims = PrimMath.ceilAsInt(Math.sqrt(pixels));

        OBJMaterial material = new OBJMaterial("VoxelArray");
        material.setDiffuseMap(createDiffuseMap(voxels, textureDims, textureDims));

        return null;
    }

    private static Texture createDiffuseMap(VoxelArray voxels, int width, int height) {
        Texture texture = new Texture(width, height);
        Incrementer2 increment = new Incrementer2(width, height);
        voxels.forEach(voxel -> {
            int[] xy = increment.current();
            texture.set(xy[0], xy[1], voxel.getRGB());
            increment.increment();
        });

        return texture;
    }

}
