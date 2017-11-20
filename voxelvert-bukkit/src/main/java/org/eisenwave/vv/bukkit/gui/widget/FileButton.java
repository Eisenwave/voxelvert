package org.eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.widget.RadioButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eisenwave.vv.bukkit.gui.FileBrowserEntry;
import org.jetbrains.annotations.NotNull;

public class FileButton extends RadioButton {
    
    private final FileBrowserEntry entry;
    private boolean highlight = false;
    
    private int index;
    
    public FileButton(@NotNull Menu menu, @NotNull FileBrowserEntry entry, int index) {
        super(menu, null);
        this.entry = entry;
        this.index = index;
        
        ItemStack item = new ItemStack(entry.getType().getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + entry.getDisplayName());
        item.setItemMeta(meta);
        this.setUncheckedItem(item);
        
        ItemStack checked = item.clone();
        checked.setType(Material.END_CRYSTAL);
        
        this.setCheckedItem(checked);
    }
    
    public FileBrowserEntry getEntry() {
        return entry;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    /*
    public void highlight() {
        if (!highlight) {
            Bukkit.broadcastMessage("highlighting "+entry.getName());
            highlight = true;
            ItemStack item = getItem();
            item.setType(Material.END_CRYSTAL);
            //ItemMeta meta = item.getItemMeta();
            //if (!meta.addEnchant(, true)) Bukkit.broadcastMessage("fail");
            //meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            //item.setItemMeta(meta);
            setItem(item);
        }
    }
    */
    
}
