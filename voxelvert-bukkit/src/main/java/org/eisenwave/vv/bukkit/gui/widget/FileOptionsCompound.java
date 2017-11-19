package org.eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.view.Icon;
import eisenwave.inv.view.IconBuffer;
import eisenwave.inv.widget.Button;
import eisenwave.inv.widget.CompoundGroup;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FileOptionsCompound extends CompoundGroup {
    
    private final ItemStack backgroundItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
    private boolean enabled = false;
    
    // INIT
    
    public FileOptionsCompound(@NotNull Menu menu) {
        super(menu);
        
        initOpen();
        initShare();
        initCopy();
        initRename();
        initDelete();
    }
    
    private void initOpen() {
        Button button = new Button(getMenu(), null);
        button.setItem(new ItemStack(Material.DIAMOND_PICKAXE));
        button.setPosition(0, 0);
        addChild(button);
    }
    
    private void initShare() {
        Button button = new Button(getMenu(), null);
        button.setItem(new ItemStack(Material.ENDER_PEARL));
        button.setPosition(1, 0);
        addChild(button);
    }
    
    private void initCopy() {
        Button button = new Button(getMenu(), null);
        button.setItem(new ItemStack(Material.MINECART));
        button.setPosition(2, 0);
        addChild(button);
    }
    
    private void initRename() {
        Button button = new Button(getMenu(), null);
        button.setItem(new ItemStack(Material.NAME_TAG));
        button.setPosition(3, 0);
        addChild(button);
    }
    
    private void initDelete() {
        Button button = new Button(getMenu(), null);
        button.setItem(new ItemStack(Material.LAVA_BUCKET));
        button.setPosition(4, 0);
        addChild(button);
    }
    
    // GETTERS
    
    /**
     * Returns whether this view is enabled.
     *
     * @return whether this view is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Returns a copy of the background item.
     *
     * @return a copy of the background item
     */
    public ItemStack getBackgroundItem() {
        return backgroundItem.clone();
    }
    
    /**
     * Enables or disables this view.
     *
     * @param enabled whether the view should be enabled
     * @see #isEnabled()
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.invalidate();
    }
    
    // DRAW
    
    @Override
    protected void drawContent(IconBuffer buffer) {
        if (enabled)
            super.drawContent(buffer);
        else
            buffer.fill(new Icon(this, backgroundItem));
    }
    
}
