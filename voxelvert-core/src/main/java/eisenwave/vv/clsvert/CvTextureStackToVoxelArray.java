package eisenwave.vv.clsvert;

import eisenwave.torrens.img.Texture;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.vv.util.Arguments;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CvTextureStackToVoxelArray implements Classverter<Texture[], VoxelArray> {
    
    @Deprecated
    @Override
    public VoxelArray invoke(@NotNull Texture[] from, @NotNull Object... args) {
        return invoke(from);
    }
    
    public static VoxelArray invoke(@NotNull Texture[] from) {
        Arguments.requireMin(from, 1);
        
        List<Texture> list = new ArrayList<>();
        final int width = from[0].getWidth(), height = from[0].getHeight();
        for (int i = 0; i < from.length; i++) {
            if (from[i].getWidth() != width)
                throw new IllegalArgumentException("out of line image width at index " + i);
            if (from[i].getHeight() != height)
                throw new IllegalArgumentException("out of line image height at index " + i);
            list.add(from[i]);
        }
        
        VoxelArray array = new VoxelArray(width, from.length, height);
        list.parallelStream().forEach(texture -> {
            for (int u = 0; u < width; u++) for (int v = 0; v < height; v++) ;
        });
        
        return null;
    }
    
}
