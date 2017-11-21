package org.eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.view.*;
import eisenwave.inv.widget.Widget;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.eisenwave.vv.bukkit.util.ItemInitUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConvertOptionWidget extends Widget {
    
    private final ItemStack optionDisplay;
    
    public ConvertOptionWidget(Menu menu, @NotNull String option) {
        super(menu, new ViewSize(ViewSize.MATCH_PARENT, 1, false, false), null);
        this.optionDisplay = displayOf(option);
    }
    
    @Override
    protected void drawContent(IconBuffer buffer) {
        buffer.set(0, 0, new Icon(this, optionDisplay));
    }
    
    // UTIL
    
    @Nullable
    private static ItemStack displayOf(String option) {
        switch (option) {
            case "d": return ItemInitUtil.item(Material.COMPASS, "Direction", "&8-d");
            case "v": return ItemInitUtil.item(Material.JUKEBOX, "Verbosity", "&8-v");
            default: return null;
        }
    }
    
}
