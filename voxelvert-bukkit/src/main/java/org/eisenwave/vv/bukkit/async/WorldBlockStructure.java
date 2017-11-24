package org.eisenwave.vv.bukkit.async;

import net.grian.torrens.object.BoundingBox6i;
import net.grian.torrens.schematic.BlockKey;
import net.grian.torrens.schematic.BlockStructure;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("deprecation")
public class WorldBlockStructure implements BlockStructure {
    
    private final World world;
    private final int offX, offY, offZ;
    private final BoundingBox6i box;
    
    public WorldBlockStructure(@NotNull World world, @NotNull BoundingBox6i box) {
        this.world = world;
        this.box = box;
        
        this.offX = box.getMinX();
        this.offY = box.getMinY();
        this.offZ = box.getMinZ();
    }
    
    private Block getBlockAt(int x, int y, int z) {
        return world.getBlockAt(x + offX, y + offY, z + offZ);
    }
    
    @Override
    public int getId(int x, int y, int z) {
        return getBlockAt(x, y, z).getTypeId();
    }
    
    @Override
    public byte getData(int x, int y, int z) {
        return getBlockAt(x, y, z).getData();
    }
    
    @Override
    public void setId(int x, int y, int z, int id) {
        getBlockAt(x, y, z).setTypeId(id);
    }
    
    @Override
    public void setData(int x, int y, int z, byte data) {
        getBlockAt(x, y, z).setData(data);
    }
    
    @Override
    public void setBlock(int x, int y, int z, int id, byte data) {
        getBlockAt(x, y, z).setTypeIdAndData(id, data, false);
    }
    
    @Override
    public BlockKey getBlock(int x, int y, int z) {
        Block block = getBlockAt(x, y, z);
        return new BlockKey(block.getTypeId(), block.getData());
    }
    
    @Override
    public int getSizeX() {
        return box.getSizeX();
    }
    
    @Override
    public int getSizeY() {
        return box.getSizeY();
    }
    
    @Override
    public int getSizeZ() {
        return box.getSizeZ();
    }
    
    // DEFAULT RE-IMPLEMENTATIONS
    
    @Override
    public BoundingBox6i getBoundaries() {
        return box;
    }
    
    @Override
    public int getVolume() {
        return box.getVolume();
    }
    
}
