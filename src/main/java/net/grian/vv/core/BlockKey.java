package net.grian.vv.core;

import org.bukkit.Material;

@SuppressWarnings("deprecation")
public class BlockKey {

    public final static BlockKey
    AIR = new BlockKey(0, 0),
    STONE = new BlockKey(1, 0);

    private final short id;
    private final byte data;

    public BlockKey(short id, byte data) {
        this.id = id;
        this.data = data;
    }

    public BlockKey(int id, int data) {
        this((short) id, (byte) data);
    }

    public BlockKey(Material material, int data) {
        this(material.getId(), data);
    }

    public BlockKey(Material material) {
        this(material, 0);
    }

    public short getId() {
        return id;
    }

    public byte getData() {
        return data;
    }

    public Material getMaterial() {
        return Material.getMaterial(id);
    }

    @Override
    public int hashCode() {
        return id | data << 16;
    }

    @Override
    public String toString() {
        return id+":"+data;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BlockKey && equals((BlockKey) obj);
    }

    public boolean equals(BlockKey key) {
        return this.id == key.id && this.data == key.data;
    }

}
