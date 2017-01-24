package net.grian.vv.core;

import net.grian.spatium.util.ColorMath;
import net.grian.spatium.util.Flags;
import net.grian.spatium.voxel.VoxelArray;
import net.grian.torrens.io.DeserializerQEF;
import net.grian.torrens.io.SerializerQEF;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VoxelCanvasTest {

    @Test
    public void drawVisibility() throws Exception {
        VoxelArray voxels = new DeserializerQEF().fromResource(getClass(), "debug.qef");
        VoxelCanvas canvas = new VoxelCanvas(voxels);

        canvas.selectContent(false);
        canvas.drawRaw((x, y, z) -> {
            float brightness = Flags.bitSum(voxels.getVisibilityMask(x, y, z)) / 6F;
            return ColorMath.fromRGB(brightness, brightness, brightness);
        });

        VoxelArray content = canvas.getContent();
        System.out.println(voxels+" = "+content);
        assertEquals(voxels.size(), content.size());

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\VoxelCanvasTest.qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQEF().toFile(content, out);
    }

    @Test
    public void drawLine() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(64, 64, 64);
        canvas.drawLine(0, 7, 15, 63, 63, 63, ColorMath.DEBUG1);

        File out = new File("D:\\Users\\Jan\\Desktop\\SERVER\\SERVERS\\TEST\\plugins\\VoxelVert\\files\\VoxelCanvasTest_drawLine.qef");
        if (!out.exists() && !out.createNewFile()) throw new IOException("failed to create "+out);

        new SerializerQEF().toFile(canvas.getContent(), out);
    }

    @Test
    public void contentSelect() throws Exception {
        VoxelArray voxels = new DeserializerQEF().fromResource(getClass(), "debug.qef");
        VoxelCanvas canvas = new VoxelCanvas(voxels);
        assertEquals(voxels.size(), canvas.contentSize());

        canvas.selectContent(false);
        assertEquals(canvas.contentSize(), canvas.selectionSize());
    }

    @Test
    public void fullSelect() throws Exception {
        VoxelCanvas canvas = new VoxelCanvas(3, 3, 3);
        assertEquals(canvas.getVolume(), canvas.selectionSize());

        canvas.forEachPosition(canvas::unselect);
        /*
        canvas.forEachPosition((x,y,z) -> {
            if (canvas.isSelected(x,y,z))
                System.out.println(ANSI.GREEN+x+","+y+","+z+ANSI.RESET);
            else
                System.out.println(ANSI.RED+x+","+y+","+z+ANSI.RESET);
        });
        System.out.println(canvas);
        */
        assertEquals(0, canvas.selectionSize());

        canvas.forEachPosition(canvas::select);
        assertEquals(canvas.getVolume(), canvas.selectionSize());
    }

}