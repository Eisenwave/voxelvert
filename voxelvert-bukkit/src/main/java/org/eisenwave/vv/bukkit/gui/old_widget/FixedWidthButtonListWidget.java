package org.eisenwave.vv.bukkit.gui.old_widget;

import net.grian.spatium.util.PrimMath;
import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.menus.Menu;

import java.util.ArrayList;
import java.util.List;

public class FixedWidthButtonListWidget extends Widget {
    
    private final List<Button> buttons = new ArrayList<>();
    private int width;
    
    public FixedWidthButtonListWidget(Menu menu, List<? extends Button> buttons, int width, int x, int y) {
        super(menu, x, y);
        setWidth(width);
        this.buttons.addAll(buttons);
    }
    
    public FixedWidthButtonListWidget(Menu menu, List<? extends Button> buttons, int width) {
        this(menu, buttons, width, 0, 0);
    }
    
    // DRAW
    
    @Override
    protected void doDraw() {
        int height = getHeight();
        
        for (int i = 0; i < buttons.size(); i++)
            drawButton(buttons.get(i), (i % width), (i / height));
    }
    
    // GETTERS
    
    @Override
    public int getWidth() {
        return width;
    }
    
    @Override
    public int getHeight() {
        return Math.min(1, PrimMath.ceil(buttons.size() / (float) width));
    }
    
    public Button getButton(int index) {
        return buttons.get(index);
    }
    
    // MUTATORS
    
    public void setWidth(int width) {
        if (width < 1 || width > 9)
            throw new IllegalArgumentException("width must be in range(1,9)");
        this.width = width;
    }
    
}
