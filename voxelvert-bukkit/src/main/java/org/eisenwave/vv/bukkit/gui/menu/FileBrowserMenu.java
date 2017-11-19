package org.eisenwave.vv.bukkit.gui.menu;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.view.ViewSize;
import eisenwave.inv.widget.MenuPane;
import eisenwave.inv.widget.Pane;
import org.bukkit.ChatColor;
import org.eisenwave.vv.bukkit.gui.FileBrowserEntry;
import org.eisenwave.vv.bukkit.gui.widget.FileList;
import org.eisenwave.vv.bukkit.gui.widget.FileButton;
import org.eisenwave.vv.bukkit.gui.widget.FileOptionsCompound;
import org.eisenwave.vv.bukkit.gui.widget.PageNavigatorCompound;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVInventoryVariable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FileBrowserMenu extends Menu {
    
    //private final static Button BACKGROUND = new Button(Material.STAINED_GLASS_PANE, 1, (short) 15, " ");
    
    private final VVInventory handle;
    
    private final FileBrowserEntry[] entries;
    
    private FileList fileList;
    
    //private final static int pageSize = 45;
    //private final int pages = 1;
    //private int page;
    
    public FileBrowserMenu(@NotNull VVInventory handle) {
        super(54, ChatColor.BOLD+"File Browser");
        this.handle = handle;
        this.entries = entriesOf(handle);
        //this.page = 0;
        
        initWidgets();
    }
    
    private FileBrowserEntry[] entriesOf(VVInventory inventory) {
        List<FileBrowserEntry> entries = new ArrayList<>();
        for (String name : handle.list()) {
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
    
    private void initWidgets() {
        MenuPane contentPane = getContentPane();
        
        this.fileList = new FileList(this, new ViewSize(ViewSize.MATCH_PARENT, 5), entries);
        contentPane.addChild(fileList);
    
        initFileOptions();
        initNavigator();
    }
    
    private void initNavigator() {
        Pane separatorPane = new Pane(this, null);
        separatorPane.setPosition(5, 5);
        getContentPane().addChild(separatorPane);
    
        if (entries.length > fileList.getArea()) {
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
        FileOptionsCompound options = new FileOptionsCompound(this);
        options.setPosition(ViewSize.MIN_POS, ViewSize.MAX_POS); // bottom-left corner
        getContentPane().addChild(options);
        options.setEnabled(true);
    }
    
    // DRAW
    
}
