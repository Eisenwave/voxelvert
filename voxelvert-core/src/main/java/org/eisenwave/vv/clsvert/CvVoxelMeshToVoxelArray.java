package org.eisenwave.vv.clsvert;

import net.grian.torrens.object.BoundingBox6i;
import net.grian.torrens.voxel.VoxelArray;
import net.grian.torrens.voxel.VoxelMesh;
import org.jetbrains.annotations.NotNull;

public class CvVoxelMeshToVoxelArray implements Classverter<VoxelMesh, VoxelArray> {
    
    @Override
    public Class<VoxelMesh> getFrom() {
        return VoxelMesh.class;
    }
    
    @Override
    public Class<VoxelArray> getTo() {
        return VoxelArray.class;
    }
    
    @Override
    public VoxelArray invoke(@NotNull VoxelMesh mesh, @NotNull Object... args) {
        return invoke(mesh);
    }
    
    public VoxelArray invoke(VoxelMesh mesh) {
        BoundingBox6i bounds = mesh.getBoundaries();
        VoxelArray result = new VoxelArray(bounds.getSizeX(), bounds.getSizeY(), bounds.getSizeZ());
        
        final int
            bxmin = bounds.getMinX(),
            bymin = bounds.getMinY(),
            bzmin = bounds.getMinZ();
        
        for (VoxelMesh.Element element : mesh) {
            VoxelArray array = element.getArray();
            final int
                xmin = element.getMinX() - bxmin,
                ymin = element.getMinY() - bymin,
                zmin = element.getMinZ() - bzmin,
                limX = array.getSizeX(),
                limY = array.getSizeY(),
                limZ = array.getSizeZ();
            
            for (int x = 0; x < limX; x++)
                for (int y = 0; y < limY; y++)
                    for (int z = 0; z < limZ; z++)
                        result.setRGB(xmin + x, ymin + y, zmin + z, array.getRGB(x, y, z));
        }
        
        return result;
    }
    
}
