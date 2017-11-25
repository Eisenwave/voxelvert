package eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.widget.RadioButton;
import eisenwave.vv.bukkit.gui.FileBrowserEntry;
import eisenwave.vv.bukkit.gui.FileType;
import eisenwave.vv.bukkit.gui.menu.FileBrowserMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.inv.util.ItemInitUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileButton extends RadioButton {
    
    private final FileBrowserEntry entry;
    private boolean highlight = false;
    
    private int index;
    
    public FileButton(@NotNull FileBrowserMenu menu, @NotNull FileBrowserEntry entry, int index) {
        super(menu, null);
        this.entry = entry;
        this.index = index;
        FileType type = entry.getType();
        
        List<String> lore = new ArrayList<>(2);
        lore.add(ChatColor.GRAY + menu.getLanguage().get(type.getLanguageName()));
        if (type.isFile()) {
            BasicFileAttributes attr = menu.getFileSystem().getBasicAttributes(entry.getPath());
            if (attr != null) {
                lore.add(ChatColor.DARK_GRAY + CommandUtil.printFileSize(attr.size()));
            }
        }
        
        ItemStack unchecked = ItemInitUtil.create(type.getIcon(), 1, (short) 0,
            ChatColor.RESET + entry.getDisplayName(true),
            lore);
        this.setUncheckedItem(unchecked);
        
        ItemStack checked = unchecked.clone();
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
