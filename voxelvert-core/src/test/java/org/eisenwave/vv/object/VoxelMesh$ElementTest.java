package org.eisenwave.vv.object;

import net.grian.torrens.object.BoundingBox6i;
import net.grian.torrens.object.Vertex3i;
import net.grian.torrens.voxel.VoxelArray;
import net.grian.torrens.voxel.VoxelMesh;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VoxelMesh$ElementTest {

    @Test
    public void getPosition() throws Exception {
        VoxelArray array = new VoxelArray(3, 5, 7);
        VoxelMesh.Element element = new VoxelMesh.Element(1, 2, 3, array);

        assertEquals(new Vertex3i(1, 2, 3), element.getPosition());
    }

    @Test
    public void getBoundaries() throws Exception {
        VoxelArray array = new VoxelArray(3, 5, 7);
        VoxelMesh.Element element = new VoxelMesh.Element(1, 2, 3, array);

        assertEquals(new BoundingBox6i(1, 2, 3, 3, 6, 9), element.getBoundaries());
    }

}