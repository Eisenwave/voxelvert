package eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.util.LegacyUtil;
import eisenwave.inv.widget.RadioButton;
import eisenwave.vv.bukkit.gui.*;
import eisenwave.vv.bukkit.gui.menu.FileBrowserMenu;
import eisenwave.vv.object.Language;
import org.bukkit.*;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.inv.util.ItemUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class FileButton extends RadioButton {
    
    private final static ChatColor ATTR_TEXT_COLOR = ChatColor.DARK_GRAY;
    
    private final static Material
        END_CRYSTAL = LegacyUtil.getByMinecraftKey13("end_crystal").getMaterial();
    
    private final FileBrowserEntry entry;
    
    private int index;
    
    public FileButton(@NotNull FileBrowserMenu menu, @NotNull FileBrowserEntry entry, int index) {
        super(menu, null);
        this.entry = entry;
        this.index = index;
        FileType type = entry.getType();
    
        Language lang = menu.getLanguage();
        String mediaName = lang.get(type.getLanguageName());
        String catName = lang.get(type.getCategory().getLanguageName());
        
        List<String> lore = new ArrayList<>(3);
        lore.add(ChatColor.GRAY + mediaName);
        if (type.isFile()) {
            BasicFileAttributes attr = menu.getFileSystem().getBasicAttributes(entry.getPath());
            assert attr != null;
            
            long size = attr.size();
            long age = System.currentTimeMillis() - attr.creationTime().toMillis();
            
            lore.add(ATTR_TEXT_COLOR + CommandUtil.printFileSize(size) + " (" + catName + ")");
            lore.add(lang.get("menu.files.attribute.age", ATTR_TEXT_COLOR + CommandUtil.localizeTime(age, lang)));
        }
        else lore.add(ATTR_TEXT_COLOR + catName);
        
        String name = ChatColor.RESET + entry.getDisplayName(true);
        
        this.setUncheckedItem(ItemUtil.create(type.getIcon(), 1, name, lore));
        this.setCheckedItem(ItemUtil.create(END_CRYSTAL, 1, name, lore));
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
