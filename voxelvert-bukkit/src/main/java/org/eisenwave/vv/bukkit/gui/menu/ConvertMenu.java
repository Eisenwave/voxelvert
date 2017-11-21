package org.eisenwave.vv.bukkit.gui.menu;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.menu.MenuManager;
import eisenwave.inv.view.ViewSize;
import eisenwave.inv.widget.ButtonPane;
import eisenwave.inv.widget.Pane;
import eisenwave.inv.widget.RadioButton;
import eisenwave.inv.widget.RadioList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.eisenwave.vv.bukkit.gui.FileType;
import org.eisenwave.vv.bukkit.util.ItemInitUtil;
import org.eisenwave.vv.ui.fmtvert.Format;
import org.eisenwave.vv.ui.fmtvert.FormatverterFactory;
import org.jetbrains.annotations.NotNull;

public class ConvertMenu extends Menu {
    
    private final static ItemStack
        ITEM_CANCEL = ItemInitUtil.item(Material.WOOL, 1, (short) 14, ChatColor.RED+"Cancel"),
        ITEM_CONFIRM = ItemInitUtil.item(Material.WOOL, 1, (short) 5, ChatColor.GREEN+"Confirm"),
        ITEM_PANE = ItemInitUtil.item(Material.STAINED_GLASS_PANE, 1, (short) 15, " ");
    
    // INIT
    
    public ConvertMenu(@NotNull String name, @NotNull Format format) {
        super(Menu.MAX_SIZE, ChatColor.BOLD + "Convert: "+ChatColor.RESET +continuedName(name, 16));
    
        initFormatPicker(format);
        initCancel();
        initConfirm();
        initSeparator();
    }
    
    private void initFormatPicker(Format sourceFormat) {
        RadioList list = new RadioList(this, new ViewSize(ViewSize.MATCH_PARENT, 1));
        Format[] options = FormatverterFactory.getInstance().getOutputFormats(sourceFormat);
        //boolean first = true;
        for (Format format : options) {
            RadioButton child = new RadioButton(this, null);
            final ItemStack unchecked, checked;
            {
                FileType type = FileType.fromFormat(format);
                Material material = type == null? Material.WHITE_SHULKER_BOX : type.getIcon();
                String prefix = type == null? ChatColor.RESET.toString() : type.getPrefix();
                unchecked = ItemInitUtil.item(material, prefix + format.getId());
            }
            checked = unchecked.clone();
            checked.setType(Material.END_CRYSTAL);
            
            child.setCheckedItem(checked);
            child.setUncheckedItem(unchecked);
            
            child.addCheckListener(event -> {
                if (event.isChecked())
                    this.performFormatPick(event.getPlayer(), format);
            });
            
            /* if (first) {
                child.setChecked(true);
                first = false;
            } */
    
            list.addChild(child);
        }
        
        getContentPane().addChild(list);
        
        int length = list.getLength();
        if (length < 9) {
            Pane pane = new Pane(this, new ViewSize(ViewSize.MAX_POS, 0, 9 - length, 1), null);
            pane.setItem(ITEM_PANE);
            getContentPane().addChild(pane);
        }
    }
    
    private void initCancel() {
        ViewSize size = new ViewSize(ViewSize.MIN_POS, ViewSize.MAX_POS, 2, 1);
        ButtonPane button = new ButtonPane(this, size, null);
        button.setItem(ITEM_CANCEL);
        getContentPane().addChild(button);
        
        button.addClickListener(event -> MenuManager.getInstance().endSession(event.getPlayer()));
    }
    
    private void initConfirm() {
        ViewSize size = new ViewSize(ViewSize.MAX_POS, ViewSize.MAX_POS, 2, 1);
        ButtonPane button = new ButtonPane(this, size, null);
        button.setItem(ITEM_CONFIRM);
        getContentPane().addChild(button);
    }
    
    private void initSeparator() {
        ViewSize size = new ViewSize(2, ViewSize.MAX_POS, 5, 1);
        Pane pane = new Pane(this, size, null);
        pane.setItem(ITEM_PANE);
        getContentPane().addChild(pane);
    }
    
    // ACTIONS
    
    public void performFormatPick(Player player, Format sourceFormat) {
        player.sendMessage(sourceFormat.getId());
    }
    
    // UTIL
    
    private static String continuedName(String name, int lim) {
        return (name.length() > lim)? name.substring(0, lim) + "..." : name;
    }
    
}
