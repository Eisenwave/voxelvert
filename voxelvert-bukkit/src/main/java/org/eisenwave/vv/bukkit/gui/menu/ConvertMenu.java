package org.eisenwave.vv.bukkit.gui.menu;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.menu.MenuManager;
import eisenwave.inv.view.ViewGroup;
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
import org.eisenwave.vv.bukkit.gui.widget.ConvertOptionWidget;
import org.eisenwave.vv.bukkit.util.ItemInitUtil;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.fmtvert.Format;
import org.eisenwave.vv.ui.fmtvert.Formatverter;
import org.eisenwave.vv.ui.fmtvert.FormatverterFactory;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertMenu extends Menu {
    
    private final static ItemStack
        ITEM_CANCEL = ItemInitUtil.item(Material.WOOL, 1, (short) 14, ChatColor.RED + "Cancel", "&7Back to Inventory"),
        ITEM_CONFIRM_OFF = ItemInitUtil.item(Material.WOOL, 1, (short) 7, ChatColor.DARK_GRAY + "Confirm", "&7Convert"),
        ITEM_CONFIRM_ON = ItemInitUtil.item(Material.WOOL, 1, (short) 5, ChatColor.GREEN + "Confirm", "&7Convert"),
        ITEM_PANE = ItemInitUtil.item(Material.STAINED_GLASS_PANE, 1, (short) 15, " ");
    
    // INIT
    
    private ViewGroup<ConvertOptionWidget> optionsGroup;
    private ButtonPane confirmButton;
    
    private boolean ready = false;
    
    private final VVUser user;
    private final String sourcePath;
    private Format sourceFormat, targetFormat;
    
    public ConvertMenu(VVUser user, @NotNull String name, @NotNull Format format) {
        super(Menu.MAX_SIZE, ChatColor.BOLD + "Convert: " + ChatColor.RESET + continuedName(name, 16));
        this.user = user;
        this.sourcePath = name;
        this.sourceFormat = format;
        
        initFormatPicker(format);
        initCancel();
        initConfirm();
        initSeparator();
        initOptionsGroup();
    }
    
    private void initFormatPicker(Format sourceFormat) {
        RadioList list = new RadioList(this, new ViewSize(ViewSize.MATCH_PARENT, 1));
        Format[] options = FormatverterFactory.getInstance().getOutputFormats(sourceFormat);
        Language lang = getUser().getVoxelVert().getLanguage();
        //boolean first = true;
        for (Format format : options) {
            RadioButton child = new RadioButton(this, null);
            final ItemStack unchecked, checked;
            {
                FileType type = FileType.fromFormat(format);
                Material material = type == null? Material.WHITE_SHULKER_BOX : type.getIcon();
                String prefix = type == null? ChatColor.RESET.toString() : type.getPrefix();
                String name = lang.get("format." + format.getId());
                unchecked = ItemInitUtil.item(material, prefix + name);
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
        
        button.addClickListener(event -> {
            Menu menu = new FileBrowserMenu(user.getInventory());
            MenuManager.getInstance().startSession(event.getPlayer(), menu);
        });
    }
    
    private void initConfirm() {
        ViewSize size = new ViewSize(ViewSize.MAX_POS, ViewSize.MAX_POS, 2, 1);
        confirmButton = new ButtonPane(this, size, null);
        confirmButton.setItem(ITEM_CONFIRM_OFF);
        getContentPane().addChild(confirmButton);
    }
    
    private void initSeparator() {
        ViewSize size = new ViewSize(2, ViewSize.MAX_POS, 5, 1);
        Pane pane = new Pane(this, size, null);
        pane.setItem(ITEM_PANE);
        getContentPane().addChild(pane);
    }
    
    private void initOptionsGroup() {
        this.optionsGroup = new ViewGroup<>(this, new ViewSize(0, 1, ViewSize.MATCH_PARENT, 4));
        getContentPane().addChild(optionsGroup);
    }
    
    // ACTIONS
    
    public void performFormatPick(Player player, Format targetFormat) {
        optionsGroup.clearChildren();
        
        Formatverter fv = FormatverterFactory.getInstance().fromFormats(sourceFormat, targetFormat);
        List<ConvertOptionWidget> widgets = Arrays
            .stream(fv.getAllOptions())
            .map(option -> new ConvertOptionWidget(ConvertMenu.this, option.getId()))
            .collect(Collectors.toList());
        int size = widgets.size();
        
        int lim = Math.min(4, size);
        for (int i = 0; i < lim; i++) {
            ConvertOptionWidget widget = widgets.get(i);
            widget.setPosition(ViewSize.MIN_POS, i);
            optionsGroup.addChild(widget);
        }
        
        if (!this.ready) {
            this.ready = true;
            confirmButton.setItem(ITEM_CONFIRM_ON);
            confirmButton.addClickListener(event -> performConvert(event.getPlayer()));
        }
        this.targetFormat = targetFormat;
    }
    
    public void performConvert(Player player) {
        player.sendMessage(sourceFormat + "<" + sourcePath + "> -> " + targetFormat);
    }
    
    // GETTERS
    
    
    public VVUser getUser() {
        return user;
    }
    
    public String getSourcePath() {
        return sourcePath;
    }
    
    public Format getSourceFormat() {
        return sourceFormat;
    }
    
    // UTIL
    
    private static String continuedName(String name, int lim) {
        return (name.length() > lim)? name.substring(0, lim) + "..." : name;
    }
    
}
