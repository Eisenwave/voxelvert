package net.grian.vv.core;

import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.geo.BlockVector;
import net.grian.spatium.voxel.VoxelArray;
import org.junit.Test;

import static org.junit.Assert.*;

public class VoxelMesh$ElementTest {

    @Test
    public void getPosition() throws Exception {
        VoxelArray array = new VoxelArray(3, 5, 7);
        VoxelMesh.Element element = new VoxelMesh.Element(1, 2, 3, array);

        assertEquals(BlockVector.fromXYZ(1, 2, 3), element.getPosition());
    }

    @Test
    public void getBoundaries() throws Exception {
        VoxelArray array = new VoxelArray(3, 5, 7);
        VoxelMesh.Element element = new VoxelMesh.Element(1, 2, 3, array);

        assertEquals(BlockSelection.fromPoints(1, 2, 3, 3, 6, 9), element.getBoundaries());
    }

}