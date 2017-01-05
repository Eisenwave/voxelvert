package net.grian.vv.core;

import net.grian.spatium.geo.AxisAlignedBB;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ElementSet implements Iterable<TexturedBox> {

    private final Texture texture;
    private final Set<TexturedBox> elements = new HashSet<>();

    public ElementSet(int width, int height) {
        this.texture = new Texture(width, height);
    }

    public ElementSet() {
        this.texture = new Texture();
    }

    public Texture getTexture() {
        return texture;
    }

    public int size() {
        return elements.size();
    }

    public void clear() {
        elements.clear();
    }

    public TexturedBox addElement(AxisAlignedBB bounds) {
        TexturedBox element = new TexturedBox(bounds);
        elements.add(element);
        return element;
    }

    @Override
    public Iterator<TexturedBox> iterator() {
        return elements.iterator();
    }



}
