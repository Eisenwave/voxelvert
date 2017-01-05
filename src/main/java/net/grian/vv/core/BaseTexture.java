package net.grian.vv.core;

public interface BaseTexture extends Comparable<BaseTexture> {

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract int get(int u, int v);

    public default int getPixels() {
        return getWidth() * getHeight();
    }

    public abstract void set(int u, int v, int rgb);

    @Override
    public default int compareTo(BaseTexture texture) {
        int result = this.getHeight() - texture.getHeight();
        return result!=0? result : this.getWidth() - texture.getWidth();
    }

}
