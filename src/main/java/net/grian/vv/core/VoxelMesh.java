package net.grian.vv.core;

import net.grian.spatium.geo.BlockVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class VoxelMesh implements Serializable, Iterable<VoxelMesh.Element> {

    private final List<Element> list = new ArrayList<>();

    public VoxelMesh() {}

    public VoxelMesh(VoxelMesh copyOf) {
        for (Element e : copyOf)
            list.add(e.clone());
    }

    public void add(int x, int y, int z, VoxelArray array) {
        this.list.add(new Element(x, y, z, array));
    }

    public int size() {
        return list.size();
    }

    public VoxelMesh.Element[] getElements() {
        return list.toArray(new Element[list.size()]);
    }

    @Override
    public String toString() {
        return VoxelMesh.class.getSimpleName() + "{size="+size()+"}";
    }

    @Override
    public VoxelMesh clone() {
        return new VoxelMesh(this);
    }

    @Override
    public Iterator<Element> iterator() {
        return list.iterator();
    }

    public static class Element {

        private final int x, y, z;
        private final VoxelArray array;

        private Element(int x, int y, int z, VoxelArray array) {
            Objects.requireNonNull(array, "array must not be null");
            this.x = x;
            this.y = y;
            this.z = z;
            this.array = array;
        }

        private Element(Element copyOf) {
            this(copyOf.x, copyOf.y, copyOf.z, copyOf.array);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }

        public BlockVector getPosition() {
            return BlockVector.fromXYZ(x, y, z);
        }

        public VoxelArray getArray() {
            return array;
        }

        @Override
        public Element clone() {
            return new Element(this);
        }

    }

}
