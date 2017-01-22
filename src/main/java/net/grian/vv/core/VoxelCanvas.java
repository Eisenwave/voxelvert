package net.grian.vv.core;

import net.grian.spatium.array.BooleanArray3;
import net.grian.spatium.function.Int3Consumer;
import net.grian.spatium.function.Int3IntFunction;
import net.grian.spatium.function.Int3Predicate;
import net.grian.spatium.voxel.VoxelArray;

import java.util.function.Consumer;

public class VoxelCanvas {

    private final VoxelArray content;
    private final BooleanArray3 selection;

    private final int sizeX, sizeY, sizeZ;

    public VoxelCanvas(int x, int y, int z) {
        this.content = new VoxelArray(x, y, z);
        this.selection = new BooleanArray3(x, y, z);
        this.sizeX = x;
        this.sizeY = y;
        this.sizeZ = z;

        forEachPosition(selection::enable);
    }

    public VoxelCanvas(VoxelArray content) {
        this(content.getSizeX(), content.getSizeY(), content.getSizeZ());
        drawVoxels(content);
    }

    /**
     * Returns a copy of the current canvas content.
     *
     * @return a copy of the current canvas content
     */
    public VoxelArray getContent() {
        return content.clone();
    }

    /**
     * Returns a copy of the current canvas selection.
     *
     * @return a copy of the current canvas selection
     */
    public BooleanArray3 getSelection() {
        return selection;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public int getVolume() {
        return sizeX * sizeY * sizeZ;
    }

    public int contentSize() {
        return content.size();
    }

    public int selectionSize() {
        return selection.size();
    }

    // DRAW

    public void draw(int x, int y, int z, int rgb) {
        if (
                x >= 0 && y >= 0 && z >= 0 &&
                x < sizeX && y < sizeY && z < sizeZ &&
                selection.get(x, y, z))
            content.setRGB(x, y, z, rgb);
    }

    public void drawVoxel(VoxelArray.Voxel voxel) {
        draw(voxel.getX(), voxel.getY(), voxel.getZ(), voxel.getRGB());
    }

    public void drawVoxels(VoxelArray array, int x, int y, int z) {
        array.forEach( voxel -> draw(voxel.getX()+x, voxel.getY()+y, voxel.getZ()+z, voxel.getRGB()) );
    }

    public void drawVoxels(VoxelArray array) {
        array.forEach(this::drawVoxel);
    }

    public void drawRaw(Int3IntFunction function) {
        forEachPosition( (x, y, z) -> draw(x, y, z, function.apply(x, y, z)) );
    }

    // SELECT

    public void setSelected(int x, int y, int z, boolean selected) {
        if (x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ)
            selection.set(x, y, z, selected);
    }

    public void select(int x, int y, int z) {
        if (x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ)
            selection.enable(x, y, z);
    }

    public void unselect(int x, int y, int z) {
        if (x >= 0 && y >= 0 && z >= 0 && x < sizeX && y < sizeY && z < sizeZ)
            selection.disable(x, y, z);
    }

    public void selectContent(boolean append) {
        selectRaw(content::contains, append);
    }

    public void selectRaw(Int3Predicate predicate, boolean append) {
        forEachPosition(((x, y, z) -> {
            if (predicate.test(x, y, z))
                select(x, y, z);
            else if (!append)
                unselect(x, y, z);
        }));
    }

    // IS SELECTED

    public boolean contains(int x, int y, int z) {
        return content.contains(x, y, z);
    }

    public boolean isSelected(int x, int y, int z) {
        return selection.contains(x, y, z);
    }

    // ITERATION

    public void forEachVoxel(Consumer<? super VoxelArray.Voxel> action) {
        forEachPosition((x, y, z) -> {
            if (content.contains(x, y, z) && selection.contains(x, y, z))
                action.accept(content.getVoxel(x, y, z));
        });
    }

    public void forEachPosition(Int3Consumer action) {
        for (int x = 0; x<sizeX; x++)
            for (int y = 0; y<sizeY; y++)
                for (int z = 0; z<sizeZ; z++)
                    action.accept(x, y, z);
    }

    // MISC


    @Override
    public String toString() {
        return VoxelCanvas.class.getSimpleName()+
                "{content="+content+
                ",selected="+getSelection().size()+"}";
    }
}
