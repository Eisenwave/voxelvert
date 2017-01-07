package net.grian.vv.core;

import net.grian.spatium.geo.BlockSelection;
import net.grian.spatium.geo.BlockVector;
import org.bukkit.block.Biome;

import java.io.Serializable;

public class BlockArray implements Serializable, Cloneable {

    private final static Biome[] valuesBiome = Biome.values();

    private final short[][][] arrayId;
    private final byte[][][] arrayData;
    private final byte[][][] arrayBiome;
    private final int sizeX, sizeY, sizeZ;

    public BlockArray(int x, int y, int z, boolean biomes) {
        if (x == 0 || y == 0 || z == 0) throw new IllegalArgumentException("size 0 voxel array");
        this.sizeX = x;
        this.sizeY = y;
        this.sizeZ = z;
        this.arrayId = new short[x][y][z];
        this.arrayData = new byte[x][y][z];
        this.arrayBiome = biomes? new byte[x][y][z] : null;
    }

    public BlockArray(int x, int y, int z) {
        this(x, y, z, false);
    }

    public void fill(short id, byte data) {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    setBlock(x, y, z, id, data);
    }

    /**
     * Returns a copy of a part of this array.
     *
     * @param xmin the min x
     * @param ymin the min y
     * @param zmin the min z
     * @param xmax the max x
     * @param ymax the max y
     * @param zmax the max z
     * @return a new sub array, copied out of this array
     */
    public BlockArray copy(int xmin, int ymin, int zmin, int xmax, int ymax, int zmax) {
        if (xmin < 0 || ymin < 0 || zmin < 0)
            throw new IllegalArgumentException("min ("+xmin+","+ymin+","+zmin+") out of boundaries");
        if (xmax >= getSizeX() || ymax >= getSizeY() || zmax >= getSizeZ())
            throw new IllegalArgumentException("max ("+xmax+","+ymax+","+zmax+") out of boundaries");

        BlockArray result = new BlockArray(xmax-xmin+1, ymax-ymin+1, zmax-zmin+1, hasBiomes());
        for (int x = xmin; x<=xmax; x++)
            for (int y = ymin; y<=ymax; y++)
                for (int z = zmin; z<=zmax; z++)
                    result.setBlock(x-xmin, y-ymin, z-zmin, getId(x, y, z), getData(x, y, z));

        return result;
    }

    /**
     * Returns the size of the array on the x-axis.
     *
     * @return the size on the x-axis
     */
    public int getSizeX() {
        return sizeX;
    }

    /**
     * Returns the size of the array on the y-axis.
     *
     * @return the size on the y-axis
     */
    public int getSizeY() {
        return sizeY;
    }

    /**
     * Returns the size of the array on the z-axis.
     *
     * @return the size on the z-axis
     */
    public int getSizeZ() {
        return sizeZ;
    }

    /**
     * Returns the volume of the voxel array.
     *
     * @return the volume
     */
    public int getVolume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    public BlockVector getDimensions() {
        return BlockVector.fromXYZ(getSizeX(), getSizeY(), getSizeZ());
    }

    /**
     * Returns the boundaries of this voxel array.
     *
     * @return the array boundaries
     */
    public BlockSelection getBoundaries() {
        return BlockSelection.fromPoints(0, 0, 0, getSizeX(), getSizeY(), getSizeZ());
    }

    /**
     * Returns the block at the specified position,
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the block at the specified position
     */
    public BlockKey getBlock(int x, int y, int z) {
        return new BlockKey(arrayId[x][y][z], arrayData[x][y][z]);
    }

    /**
     * Returns the block at the specified position,
     *
     * @param v the position
     * @return the block at the specified position
     */
    public BlockKey getBlock(BlockVector v) {
        return new BlockKey(
                arrayId[v.getX()][v.getY()][v.getZ()],
                arrayData[v.getX()][v.getY()][v.getZ()]);
    }

    /**
     * Returns the block id at the specified position.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return the block id at the specified position
     */
    public short getId(int x, int y, int z) {
        return arrayId[x][y][z];
    }

    /**
     * Returns the block id at the specified position.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return the block id at the specified position
     */
    public byte getData(int x, int y, int z) {
        return arrayData[x][y][z];
    }

    /**
     * Returns the biome at the specified position.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the biome at the specified position
     * @throws IllegalStateException if the array stores no biomes
     */
    public Biome getBiome(int x, int y, int z) {
        if (!hasBiomes()) throw new IllegalStateException("block array stores no biomes");
        return valuesBiome[arrayBiome[x][y][z]];
    }

    /**
     * Returns the total amount of visible blocks in this array.
     *
     * @return the amount of blocks
     */
    public int size() {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();
        int count = 0;

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    if (contains(x,y,z)) count++;

        return count;
    }

    //CHECKERS

    /**
     * Returns whether this block array stores biomes.
     *
     * @return whether this block array stores biomes
     */
    public boolean hasBiomes() {
        return arrayBiome != null;
    }

    /**
     * Returns whether the array contains a block at the given position.
     *
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @return whether the array contains a block
     */
    public boolean contains(int x, int y, int z) {
        return getId(x, y, z) != 0;
    }

    /**
     * Returns whether the array contains a voxel at the given position.
     * @param v the voxel position
     * @return whether the array contains a voxel
     */
    public boolean contains(BlockVector v) {
        return contains(v.getX(), v.getY(), v.getZ());
    }

    //SETTERS

    public void setId(int x, int y, int z, short id) {
        arrayId[x][y][z] = id;
    }

    public void setId(BlockVector pos, short id) {
        setId(pos.getX(), pos.getY(), pos.getZ(), id);
    }

    public void setData(int x, int y, int z, byte data) {
        arrayData[x][y][z] = data;
    }

    public void setData(BlockVector pos, byte data) {
        setData(pos.getX(), pos.getY(), pos.getZ(), data);
    }

    public void setBlock(int x, int y, int z, short id, byte data) {
        arrayId[x][y][z] = id;
        arrayData[x][y][z] = data;
    }

    public void setBlock(int x, int y, int z, BlockKey block) {
        setBlock(x, y, z, block.getId(), block.getData());
    }

    public void setBlock(BlockVector pos, BlockKey block) {
        setBlock(pos.getX(), pos.getY(), pos.getZ(), block);
    }

    public void setBiome(int x, int y, int z, Biome biome) {
        if (!hasBiomes()) throw new IllegalArgumentException("this block array has no biomes");
        arrayBiome[x][y][z] = (byte) biome.ordinal();
    }

    public void setBiome(BlockVector pos, Biome biome) {
        setBiome(pos.getX(), pos.getY(), pos.getZ(), biome);
    }

    public void remove(int x, int y, int z) {
        setBlock(x, y, z, BlockKey.AIR);
    }

    public void remove(BlockVector pos) {
        remove(pos.getX(), pos.getY(), pos.getZ());
    }

    //MISC

    @Override
    public String toString() {
        return BlockArray.class.getSimpleName()+
                "{dims="+getSizeX()+"x"+getSizeY()+"x"+getSizeZ()+
                ", volume="+getVolume()+
                ", size="+size()+"," +
                ", biomes="+hasBiomes()+"}";
    }

    @Override
    public BlockArray clone() {
        return copy(0, 0, 0, getSizeX()-1, getSizeY()-1, getSizeZ()-1);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockArray && equals((BlockArray) obj);
    }

    public boolean equals(BlockArray array) {
        final int limX = getSizeX(), limY = getSizeY(), limZ = getSizeZ();
        if (limX != array.getSizeX() || limY != array.getSizeY() || limZ != array.getSizeZ())
            return false;

        for (int x = 0; x<limX; x++)
            for (int y = 0; y<limY; y++)
                for (int z = 0; z<limZ; z++)
                    if (
                            this.getId(x,y,z) != array.getId(x,y,z) ||
                            this.getData(x,y,z) != array.getData(x,y,z))
                        return false;

        return true;
    }

}
