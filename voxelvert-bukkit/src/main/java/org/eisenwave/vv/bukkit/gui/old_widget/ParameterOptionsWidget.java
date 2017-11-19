package org.eisenwave.vv.bukkit.gui.old_widget;

import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.menus.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ParameterOptionsWidget extends Widget {
    
    private final Set<ActionListener> listeners = new HashSet<>();
    
    private final static Material MATERIAL = Material.INK_SACK;
    private final static short on = 10, off = 8;
    
    private final Button param;
    private final String[] options;
    private final FixedWidthButtonListWidget listWidget;
    
    private int selected = -1;
    
    public ParameterOptionsWidget(@NotNull Menu menu, @NotNull Button param, int y, String... options) {
        super(menu, 0, y);
        this.param = param;
        this.options = options;
    
        List<ParameterChooseButton> list = new ArrayList<>();
        for (int i = 0; i < options.length; i++) {
            list.add( new ParameterChooseButton(i, options[i]) );
        }
        this.listWidget = new FixedWidthButtonListWidget(menu, list, 7, 2, y);
    }
    
    public ParameterOptionsWidget(@NotNull Menu menu, @NotNull Button param, String... options) {
        this(menu, param, 0, options);
    }
    
    @Override
    public void doDraw() {
        drawButton(param, 0, 0);
        
        listWidget.doDraw();
    }
    
    // GETTERS
    
    public int getSelectedIndex() {
        return selected;
    }
    
    public String getSelectedOption() {
        return options[selected];
    }
    
    @Override
    public int getWidth() {
        return 9;
    }
    
    @Override
    public int getHeight() {
        return listWidget.getHeight();
    }
    
    // MUTATORS
    
    @Override
    public void setPosX(int x) {
        super.setPosX(x);
        listWidget.setPosX(x + 2);
    }
    
    @Override
    public void setPosY(int y) {
        super.setPosY(y);
        listWidget.setPosY(y);
    }
    
    /**
     * <p>
     *     Selects an option with given index. If the index is negative, no option is selected.
     * </p>
     * <p>
     *     This action notifies all {@link ActionListener} objects which were registered via
     *     {@link #addActionListener(ActionListener)}.
     * </p>
     *
     * @param index the index
     * @throws IndexOutOfBoundsException if the index is >= the amount of options
     */
    public void setSelected(int index) {
        if (index >= options.length)
            throw new IndexOutOfBoundsException(Integer.toString(index));
        
        if (index != selected) {
            if (selected >= 0)
                this.listWidget.getButton(selected).setDurability(off);
            this.selected = index;
            this.listWidget.getButton(selected).setDurability(on);
        }
        
        for (ActionListener l : listeners)
            l.onSelect(selected, options[selected]);
    }
    
    // MISC
    
    public void addActionListener(ActionListener listener) {
        listeners.add(listener);
    }
    
    public void removeActionListener(ActionListener listener) {
        listeners.remove(listener);
    }
    
    // CLASSES
    
    private class ParameterChooseButton extends Button {
    
        private final int index;
        
        public ParameterChooseButton(int index, String name) {
            super(MATERIAL, name);
            this.index = index;
        }
    
        @Override
        public boolean[] onClick(Player player) {
            ParameterOptionsWidget.this.setSelected(index);
            return new boolean[] {true, false};
        }
        
    }
    
    @FunctionalInterface
    public static interface ActionListener extends EventListener {
        
        abstract void onSelect(int index, String option);
        
    }
    
}
