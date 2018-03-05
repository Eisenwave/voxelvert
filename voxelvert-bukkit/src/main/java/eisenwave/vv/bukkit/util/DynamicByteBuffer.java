package eisenwave.vv.bukkit.util;

import java.util.ArrayList;
import java.util.List;

public class DynamicByteBuffer {
    
    private final List<byte[]> blocks = new ArrayList<>();
    private final int blockSize;
    private int position = 0;
    private int limit = 0;
    
    public DynamicByteBuffer(int blockSize) {
        this.blockSize = blockSize;
    }
    
    public DynamicByteBuffer() {
        this(1024);
    }
    
    public byte[] getContent() {
        byte[] result = new byte[limit];
        for (int i = 0; i < limit; i++)
            result[i] = get(i);
        return result;
    }
    
    public byte get(int index) {
        int block = index / blockSize;
        int blockIndex = index % blockSize;
        return blocks.get(block)[blockIndex];
    }
    
    public void put(int index, byte b) {
        if (index >= limit)
            throw new IndexOutOfBoundsException(Integer.toString(index) + " >= " + limit);
        
        int block = index / blockSize;
        int blockIndex = index % blockSize;
        blocks.get(block)[blockIndex] = b;
    }
    
    public byte get() {
        return get(position++);
    }
    
    public void put(byte b) {
        if (position == getCapacity()) {
            byte[] block = new byte[blockSize];
            block[0] = b;
            blocks.add(block);
            limit++;
        }
        else if (position == limit) {
            limit++;
            put(position, b);
        }
        else {
            put(position, b);
        }
        position++;
    }
    
    public int getCapacity() {
        return blocks.size() * blockSize;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    @Override
    public String toString() {
        return "DynamicByteBuffer{pos: " + position + ", lim: " + limit + ", cap: " + getCapacity() + "}";
    }
}
