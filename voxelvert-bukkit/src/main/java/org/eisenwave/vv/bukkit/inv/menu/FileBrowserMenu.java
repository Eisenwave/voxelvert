package org.eisenwave.vv.bukkit.inv.menu;

import nl.klikenklaar.util.gui.buttons.Button;
import nl.klikenklaar.util.gui.menus.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.eisenwave.vv.bukkit.inv.FileBrowserEntry;
import org.eisenwave.vv.ui.user.VVInventory;

import java.util.ArrayList;
import java.util.List;

public class FileBrowserMenu extends Menu {
    
    private final static Button BACKGROUND = new Button(Material.STAINED_GLASS_PANE, 1, (short) 15, " ");
    
    private final VVInventory handle;
    
    private final FileBrowserEntry[] entries;
    
    private final static int pageSize = 45;
    private final int pages = 1;
    
    private int page;
    
    public FileBrowserMenu(VVInventory handle, int page) {
        super(54, "File Browser");
        this.handle = handle;
    
        List<FileBrowserEntry> entries = new ArrayList<>();
        for (String name : handle.list(false)) {
            entries.add(new FileBrowserEntry(name));
        }
        entries.sort(null);
        this.entries = entries.toArray(new FileBrowserEntry[entries.size()]);
        this.page = page;
        
        draw(page);
    }
    
    public FileBrowserMenu(VVInventory handle) {
        this(handle, 0);
    }
    
    public void goToPage(int page) {
        if (page < 0 || page > pages)
            throw new IndexOutOfBoundsException("page must be in range(0,"+pages+")");
        draw(page);
        this.page = page;
    }
    
    // DRAW
    
    private void draw(int page) {
        drawActionBar(page);
        drawEntries(page);
    }
    
    protected void drawEntries(int page) {
        final int off = page * pageSize;
        final int lim = Math.min(entries.length - off, pageSize);
        
        for (int i = 0; i < lim; i++) {
            setButton(new EntryButton(entries[off+i]), i);
        }
    }
    
    protected void drawActionBar(int page) {
        for (int x = 1; x <= 9; x++)
            setButton(BACKGROUND, x, 6);
        
        if (page > 0)
            setButton(new PageNavigatorButton(page - 1), 1, 6);
        if (page+1 < pages)
            setButton(new PageNavigatorButton(page + 1), 9, 6);
    }
    
    
    // BUTTONS
    
    private class EntryButton extends Button {
        
        private final FileBrowserEntry entry;
        
        public EntryButton(FileBrowserEntry entry) {
            super(entry.getType().getIcon(), entry.getName());
            this.entry = entry;
        }
        
    }
    
    private class PageNavigatorButton extends Button {
        
        private final int targetPage;
    
        public PageNavigatorButton(int targetPage) {
            super(Material.ARROW, String.format("To Page %d", targetPage+1));
            this.targetPage = targetPage;
        }
    
        @Override
        public boolean[] onClick(Player player) {
            FileBrowserMenu.this.goToPage(targetPage);
            return new boolean[] {true, false};
        }
        
    }
    
}
