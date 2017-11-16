package org.eisenwave.vv.bukkit.inv;

import org.jetbrains.annotations.NotNull;

public class FileBrowserEntry implements Comparable<FileBrowserEntry> {
    
    private final String name;
    private final FileBrowserEntryType type;
    
    public FileBrowserEntry(@NotNull String name) {
        this.name = name;
        this.type = FileBrowserEntryType.fromPath(name);
    }
    
    @NotNull
    public String getName() {
        return name;
    }
    
    @NotNull
    public FileBrowserEntryType getType() {
        return type;
    }
    
    @Override
    public int compareTo(@NotNull FileBrowserEntry entry) {
        int result;
    
        if (this.name.startsWith("#"))
            result = entry.name.endsWith("#")? 0 : -1;
        else
            result = entry.name.endsWith("#")? 1 : 0;
        
        if (result != 0) return result;
    
        if (this.name.endsWith("/"))
            result = entry.name.endsWith("/")? 0 : -1;
        else
            result = entry.name.endsWith("/")? 1 : 0;
    
        if (result != 0) return result;
        
        return this.name.toLowerCase().compareTo(entry.name.toLowerCase());
    }
    
}
