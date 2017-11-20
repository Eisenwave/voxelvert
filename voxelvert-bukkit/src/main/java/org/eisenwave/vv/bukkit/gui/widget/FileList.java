package org.eisenwave.vv.bukkit.gui.widget;

import eisenwave.inv.view.ViewSize;
import eisenwave.inv.widget.SimpleList;
import org.eisenwave.vv.bukkit.gui.FileBrowserEntry;
import org.eisenwave.vv.bukkit.gui.FileOptionsMode;
import org.eisenwave.vv.bukkit.gui.FileType;
import org.eisenwave.vv.bukkit.gui.menu.FileBrowserMenu;
import org.jetbrains.annotations.NotNull;

public class FileList extends SimpleList<FileButton> {
    
    private int selected = -1;
    
    public FileList(@NotNull FileBrowserMenu menu, @NotNull ViewSize size, @NotNull FileBrowserEntry[] entries) {
        super(menu, size);
        
        for (int i = 0; i < entries.length; i++) {
            FileBrowserEntry entry = entries[i];
            FileButton button = new FileButton(menu, entry, i);
            addChild(button);
            
            button.addCheckListener(event -> {
                if (event.isChecked())
                    select(button.getIndex());
            });
        }
    }
    
    /**
     * Returns a button by index.
     *
     * @param index the index
     * @return the button
     */
    public FileButton getFile(int index) {
        return children.get(index);
    }
    
    /**
     * Removes a button by index.
     *
     * @return the removed button
     */
    public FileButton removeFile(int index) {
        FileButton result = children.remove(index);
        fixIndices();
        this.invalidate();
        return result;
    }
    
    // SELECTION
    
    public int getSelIndex() {
        return selected;
    }
    
    public FileBrowserEntry getSelEntry() {
        return selected < 0? null : getFile(selected).getEntry();
    }
    
    /**
     * Selects a given file in the list.
     * <p>
     * If the provided index is negative, nothing will be selected but the previously selected item (if such an item
     * exists) will be unselected.
     *
     * @param index the index of the file
     */
    public void select(int index) {
        //System.out.println("picking: picked = " + picked + ", index = " + index);
        assert selected != index;
        final int size = children.size();
        if (index >= size) throw new IndexOutOfBoundsException(Integer.toString(index));
        
        if (selected > -1) {
            FileButton previous = getFile(selected);
            previous.setChecked(false);
            /* this is necessary because file lists are being scrolled through and the previous button may now be on a
            different page, but still visible and inside the visible area of this list */
            int offset = getOffset();
            if (selected < offset || selected > offset + getArea())
                previous.revalidate();
        }
        
        if (index > -1) {
            FileButton next = getFile(index);
            next.setChecked(true);
            FileType type = next.getEntry().getType();
            getMenu().setOptionsMode(modeOf(type));
        }
        else {
            getMenu().setOptionsMode(FileOptionsMode.EMPTY);
        }
    
        this.selected = index;
    }
    
    private void fixIndices() {
        int lim = children.size();
        
        for (int i = 0; i < lim; i++)
            children.get(i).setIndex(i);
    }
    
    @NotNull
    @Override
    public FileBrowserMenu getMenu() {
        return (FileBrowserMenu) super.getMenu();
    }
    
    private static FileOptionsMode modeOf(FileType type) {
        if (type.isDirectory()) return FileOptionsMode.FOLDER;
        if (type.isVariable()) return FileOptionsMode.VARIABLE;
        if (type.isFile()) return FileOptionsMode.FILE;
        else return FileOptionsMode.EMPTY;
    }
    
    /*
    public class FileButton extends CheckBox {
    
        private final FileBrowserEntry entry;
        private boolean highlight = false;
        
        private int index;
    
        public FileButton(@NotNull FileBrowserEntry entry, int index) {
            super(FileList.this.getMenu(), null);
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
        
    }
    */
    
}
