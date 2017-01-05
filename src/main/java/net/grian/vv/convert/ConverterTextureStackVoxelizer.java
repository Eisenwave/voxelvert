package net.grian.vv.convert;

import net.grian.vv.core.Texture;
import net.grian.vv.core.VoxelArray;
import net.grian.vv.util.Arguments;

import java.util.ArrayList;
import java.util.List;

public class ConverterTextureStackVoxelizer implements Converter<Texture[], VoxelArray> {

    @Override
    public Class<Texture[]> getFrom() {
        return Texture[].class;
    }

    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }

    @Override
    public VoxelArray invoke(Texture[] from, Object... args) {
        Arguments.requireMin(from, 1);

        List<Texture> list = new ArrayList<>();
        final int width = from[0].getWidth(), height = from[0].getHeight();
        for (int i = 0; i<from.length; i++) {
            if (from[i].getWidth() != width)
                throw new IllegalArgumentException("out of line image width at index "+i);
            if (from[i].getHeight() != height)
                throw new IllegalArgumentException("out of line image height at index "+i);
            list.add(from[i]);
        }

        VoxelArray array = new VoxelArray(width, from.length, height);
        list.parallelStream().forEach(texture -> {
            for (int u = 0; u<width; u++) for (int v = 0; v<height; v++);
        });

        return null;
    }
}
