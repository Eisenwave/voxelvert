package org.eisenwave.vv.object;

import net.grian.torrens.img.Texture;

import java.util.*;

/**
 * Object representation of a Minecraft model.
 */
public class MCModel implements Iterable<MCElement> {
    
    private final Map<String, Texture> textures = new HashMap<>();
    private final Set<MCElement> elements = new HashSet<>();
    
    private boolean antiBleed = true;
    
    /**
     * Returns a texture in the model by its name.
     *
     * @param name the texture name
     * @return the texture
     */
    public Texture getTexture(String name) {
        return textures.get(name);
    }
    
    /**
     * Returns the combined volume of all elements.
     *
     * @return the combined volume of all elements
     */
    public float getCombinedVolume() {
        float v = 0;
        for (MCElement e : elements)
            v += e.getShape().getVolume();
        return v;
    }
    
    /**
     * Returns a set containing the names of all textures in this model.
     *
     * @return the names of all textures
     */
    public Set<String> getTextures() {
        return textures.keySet();
    }
    
    /**
     * Returns the amount of elements in this model.
     *
     * @return the amount of elements in this model
     */
    public int getElementCount() {
        return elements.size();
    }
    
    /**
     * Returns the amount of visible faces in this model.
     *
     * @return the amount of visible faces
     */
    public int getVisibleFaceCount() {
        int result = 0;
        
        for (MCElement element : elements)
            result += element.getFaceCount();
        
        return result;
    }
    
    /**
     * Returns the amount of textures in this model.
     *
     * @return the amount of textures in this model
     */
    public int getTextureCount() {
        return textures.size();
    }
    
    /**
     * Returns the size of this model in elements.
     *
     * @return the size of the model in elements
     */
    public int size() {
        return elements.size();
    }
    
    /**
     * Returns a set containing all elements in this model.
     *
     * @return all elements
     */
    public Set<MCElement> getElements() {
        return elements;
    }
    
    public boolean hasAntiBleed() {
        return antiBleed;
    }
    
    // MUTATORS
    
    /**
     * Adds a named texture to the model.
     *
     * @param name the texture name
     * @param texture the texture
     * @return the previous texture with the name or null if there has been none
     */
    public Texture addTexture(String name, Texture texture) {
        return textures.put(name, texture);
    }
    
    /**
     * Clears all elements in this model.
     */
    public void clearElements() {
        elements.clear();
    }
    
    /**
     * Adds an element to the model.
     *
     * @param element the element
     */
    public void addElement(MCElement element) {
        elements.add(element);
    }
    
    public void setAntiBleed(boolean antiBleed) {
        this.antiBleed = antiBleed;
    }
    
    // MISC
    
    @Override
    public Iterator<MCElement> iterator() {
        return elements.iterator();
    }
    
    
}
