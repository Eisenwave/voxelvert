package net.grian.vv.core;

public interface BaseTexture extends BaseRectangle {

    public abstract int get(int u, int v);

    public abstract void set(int u, int v, int rgb);

}
