package org.eisenwave.vv.bukkit.gui.menu;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.menu.MenuResponse;
import eisenwave.inv.view.ViewSize;
import eisenwave.inv.widget.MenuPane;
import eisenwave.inv.widget.Pane;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.eisenwave.vv.bukkit.gui.FileBrowserEntry;
import org.eisenwave.vv.bukkit.gui.FileOptionsMode;
import org.eisenwave.vv.bukkit.gui.widget.*;
import org.eisenwave.vv.bukkit.util.ItemInitUtil;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVInventoryVariable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserMenu extends Menu {
    
    //private final static Button BACKGROUND = new Button(Material.STAINED_GLASS_PANE, 1, (short) 15, " ");
    
    private final VVInventory handle;
    
    private FileOptionsCompound fileOptions;
    private FileList fileList;
    
    //private final static int pageSize = 45;
    //private final int pages = 1;
    //private int page;
    
    public FileBrowserMenu(@NotNull VVInventory handle) {
        super(54, ChatColor.BOLD + "File Browser");
        this.handle = handle;
        
        initWidgets();
    }
    
    private void initWidgets() {
        MenuPane contentPane = getContentPane();
        
        this.fileList = new FileList(this, new ViewSize(ViewSize.MATCH_PARENT, 5));
        this.fileList.addAll(entriesOf(handle));
        contentPane.addChild(fileList);
        
        initFileOptions();
        initNavigator();
    }
    
    private void refreshFileList() {
        int offset = this.fileList.getOffset();
        this.fileList.clearChildren();
        this.fileList.addAll(entriesOf(handle));
        this.fileList.setOffset(offset);
        
        setOptionsMode(FileOptionsMode.EMPTY);
    }
    
    private void initNavigator() {
        Pane separatorPane = new Pane(this, null);
        separatorPane.setItem(ItemInitUtil.item(Material.STAINED_GLASS_PANE, 1, (short) 15, " "));
        separatorPane.setPosition(5, 5);
        getContentPane().addChild(separatorPane);
        
        if (fileList.getLength() > fileList.getArea()) {
            PageNavigatorCompound navigator = new PageNavigatorCompound(this, null, fileList);
            navigator.setPosition(6, ViewSize.MAX_POS);
            getContentPane().addChild(navigator);
            separatorPane.setSize(1, 1);
        }
        else {
            separatorPane.setPosition(5, ViewSize.MAX_POS);
            separatorPane.setSize(1, ViewSize.MATCH_PARENT);
        }
    }
    
    private void initFileOptions() {
        this.fileOptions = new FileOptionsCompound(this);
        fileOptions.setPosition(ViewSize.MIN_POS, ViewSize.MAX_POS); // bottom-left corner
        getContentPane().addChild(fileOptions);
        //fileOptions.setEnabled(true);
    }
    
    // GET & SET
    
    /**
     * Returns the file system of this browser.
     *
     * @return the file system
     */
    public VVInventory getFileSystem() {
        return handle;
    }
    
    /*
     * Returns the selected entry or {@code null} if there is no selected entry.
     *
     * @return the selected entry
     *
    public FileBrowserEntry getSelected() {
        return fileList.getSelEntry();
    }
    */
    
    public void setOptionsMode(@NotNull FileOptionsMode mode) {
        this.fileOptions.setMode(mode);
    }
    
    /**
     * Deletes the currently selected item.
     */
    public void performDelete(Player player) {
        if (!fileList.hasSelection()) return;
        
        String path = fileList.getSelEntry().getPath();
        player.performCommand("vv-rm "+path);
        refreshFileList();
    }
    
    /**
     * Renames the currently selected item.
     */
    public void performRename(Player player, String target) {
        if (!fileList.hasSelection()) return;
        
        String source = fileList.getSelEntry().getPath();
        player.performCommand("vv-mv "+source+" "+target);
        refreshFileList();
    }
    
    /**
     * Renames the currently selected item.
     */
    public void performCopy(Player player, String target) {
        if (!fileList.hasSelection()) return;
        
        String source = fileList.getSelEntry().getPath();
        player.performCommand("vv-cp "+source+" "+target);
        refreshFileList();
    }
    
    /*
    public void performRename() {
        int index = fileList.getSelIndex();
        if (index > -1) {
            FileButton button = fileList.removeFile(index);
            String path = button.getEntry().getPath();
            getFileSystem().delete(path);
        }
    }
    */
    
    // MENU IMPL
    
    @Override
    public MenuResponse performClick(Player player, int x, int y, ClickType click) {
        MenuResponse response = super.performClick(player, x, y, click);
        if (response == MenuResponse.EMPTY) {
            fileList.select(-1);
            setOptionsMode(FileOptionsMode.EMPTY);
        }
        return response;
    }
    
    // UTIL
    
    private static FileBrowserEntry[] entriesOf(VVInventory inventory) {
        List<FileBrowserEntry> entries = new ArrayList<>();
        for (String name : inventory.list()) {
            FileBrowserEntry entry = new FileBrowserEntry(name);
            if (entry.isHidden()) continue;
            if (entry.isVariable()) {
                VVInventoryVariable var = inventory.getVariable(entry.getName());
                assert var != null;
                if (!var.isSet()) continue;
            }
            entries.add(entry);
        }
        
        entries.sort(null);
        return entries.toArray(new FileBrowserEntry[entries.size()]);
    }
    
}
