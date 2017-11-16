package org.eisenwave.vv.bukkit.inv.widget;

import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.menus.Menu;
import org.bukkit.Material;

public class ProgressBarWidget extends Widget {
    
    private final static Button BUTTON_OFF = new Button(Material.CONCRETE, 1, (short) 7, null);
    private final static Button BUTTON_ON = new Button(Material.CONCRETE, 1, (short) 5, null);
    
    private int width, height;
    private float progress;
    
    public ProgressBarWidget(Menu menu, int width, int height, int x, int y) {
        super(menu, x, y);
        setWidth(width);
        setHeight(height);
    }
    
    public ProgressBarWidget(Menu menu, int width, int height) {
        this(menu, width, height, 0, 0);
    }
    
    @Override
    protected void doDraw() {
        final int lim = (int) (progress * width); // in range(0,width)
    
        for (int y = 0; y < height; y++) {
            
            for (int x = 0; x < lim; x++)
                drawButton(BUTTON_OFF, x, y);
            
            for (int x = lim; x < width; x++)
                drawButton(BUTTON_ON, x, y);
        }
    }
    
    // GETTERS
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return height;
    }
    
    public float getProgress() {
        return progress;
    }
    
    // MUTATORS
    
    public void setWidth(int width) {
        if (width < 1 || width > 9)
            throw new IllegalArgumentException("width must be in range(1,9)");
        this.width = width;
    }
    
    public void setHeight(int height) {
        if (height < 1 || height > 6)
            throw new IllegalArgumentException("height must be in range(1,6)");
        this.height = height;
    }
    
    public void setProgress(float progress) {
        if (progress < 0 || progress > 1)
            throw new IllegalArgumentException("progress must be in range (0,1)");
        this.progress = progress;
    }
    
}
