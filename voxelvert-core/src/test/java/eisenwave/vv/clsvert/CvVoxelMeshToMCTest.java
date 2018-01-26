package eisenwave.vv.clsvert;

import eisenwave.vv.VVTest;
import eisenwave.vv.io.SerializerMCModelGeometry;
import eisenwave.vv.util.ConvertUtil;
import eisenwave.spatium.enums.Direction;
import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.voxel.DeserializerQB;
import eisenwave.torrens.voxel.DeserializerQEF;
import eisenwave.torrens.voxel.VoxelArray;
import eisenwave.torrens.img.SerializerPNG;
import eisenwave.vv.object.MCModel;
import eisenwave.torrens.voxel.QBModel;
import eisenwave.torrens.voxel.VoxelMesh;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class CvVoxelMeshToMCTest {
    
    @Test
    public void getSurface() {
        final BoundingBox6i
            box = new BoundingBox6i(1, 2, 3, 2, 8, 18),
            surfaceNX = CvVoxelMeshToMC.getSurface(box, Direction.NEGATIVE_X),
            surfacePX = CvVoxelMeshToMC.getSurface(box, Direction.POSITIVE_X),
            surfaceNY = CvVoxelMeshToMC.getSurface(box, Direction.NEGATIVE_Y),
            surfacePY = CvVoxelMeshToMC.getSurface(box, Direction.POSITIVE_Y),
            surfaceNZ = CvVoxelMeshToMC.getSurface(box, Direction.NEGATIVE_Z),
            surfacePZ = CvVoxelMeshToMC.getSurface(box, Direction.POSITIVE_Z);

        assertEquals(surfaceNX, new BoundingBox6i(0, 2, 3, 0, 8, 18));
        assertEquals(surfacePX, new BoundingBox6i(3, 2, 3, 3, 8, 18));
        assertEquals(surfaceNY, new BoundingBox6i(1, 1, 3, 2, 1, 18));
        assertEquals(surfacePY, new BoundingBox6i(1, 9, 3, 2, 9, 18));
        assertEquals(surfaceNZ, new BoundingBox6i(1, 2, 2, 2, 8, 2));
        assertEquals(surfacePZ, new BoundingBox6i(1, 2, 19, 2, 8, 19));
    }
    
    @Test
    public void faceOptimization_X() {
        VoxelMesh mesh = new VoxelMesh();
        mesh.add(1, 0, 0, new VoxelArray(1, 3, 7));
        mesh.add(2, 0, 0, new VoxelArray(1, 2, 6)); // -x & +x faces should be removed
        mesh.add(3, 0, 0, new VoxelArray(1, 3, 7));
        
        MCModel model = new CvVoxelMeshToMC().invoke(mesh);
        assertEquals(16, model.getVisibleFaceCount());
    }
    
    @Test
    public void faceOptimization_Y() {
        VoxelMesh mesh = new VoxelMesh();
        mesh.add(0, 1, 0, new VoxelArray(3, 1, 7));
        mesh.add(0, 2, 0, new VoxelArray(2, 1, 6)); // -y & +y faces should be removed
        mesh.add(0, 3, 0, new VoxelArray(3, 1, 7));
        
        MCModel model = new CvVoxelMeshToMC().invoke(mesh);
        assertEquals(16, model.getVisibleFaceCount());
    }
    
    @Test
    public void faceOptimization_Z() {
        VoxelMesh mesh = new VoxelMesh();
        mesh.add(0, 0, 1, new VoxelArray(7, 3, 1));
        mesh.add(0, 0, 2, new VoxelArray(2, 2, 1)); // -z & +z faces should be removed
        mesh.add(0, 0, 3, new VoxelArray(7, 3, 1));
        
        MCModel model = new CvVoxelMeshToMC().invoke(mesh);
        assertEquals(16, model.getVisibleFaceCount());
    }

    @Test
    public void invoke() throws Exception {
        Logger logger = VVTest.LOGGER;
        logger.setLevel(Level.FINE);
        
        VoxelArray voxels = new DeserializerQEF(logger).fromResource(getClass(), "debug.qef");
        VoxelMesh mesh = ConvertUtil.convert(voxels, VoxelMesh.class);
        MCModel model = new CvVoxelMeshToMC().invoke(mesh);
        BufferedImage image = model.getTexture("texture").getImageWrapper();

        File jsonOut = new File(VVTest.DIR_FILES, "ClassverterVoxelsToMCTest.json");
        if (!jsonOut.exists() && !jsonOut.createNewFile()) throw new IOException("failed to fromPoints json");
        new SerializerMCModelGeometry().toFile(model, jsonOut);

        File imgOut = new File(VVTest.DIR_FILES, "ClassverterVoxelsToMCTest.png");
        if (!imgOut.exists() && !imgOut.createNewFile()) throw new IOException("failed to fromPoints texture");
        new SerializerPNG().toFile(image, imgOut);
    }
    
    /* public void testVolumePreserve() throws Exception {
        VoxelArray array = new VoxelArray(32, 32, 32);
        VoxelMesh mesh = new VoxelMesh(array);
        MCModel mc = ConvertUtil.convert(mesh, MCModel.class);

        assertEquals(mesh.size(), mc.size());
        assertEquals(mesh.getCombinedVolume(), (int) mc.getCombinedVolume());
    } */

    @Test
    public void testElementsPreserve() throws Exception {
        QBModel model = new DeserializerQB().fromResource(getClass(), "sniper.qb");
        VoxelMesh mesh = ConvertUtil.convert(model, VoxelMesh.class);
        MCModel mc = ConvertUtil.convert(mesh, MCModel.class);

        assertEquals(mesh.size(), mc.size());
    }

}
