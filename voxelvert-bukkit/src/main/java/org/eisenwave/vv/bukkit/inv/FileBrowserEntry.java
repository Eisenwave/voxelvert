package org.eisenwave.vv.bukkit.inv;

import org.jetbrains.annotations.NotNull;

public class FileBrowserEntry implements Comparable<FileBrowserEntry> {
    
    private final String name;
    private final FileBrowserType type;
    
    public FileBrowserEntry(@NotNull String name) {
        this.name = name;
        this.type = FileBrowserType.fromPath(name);
    }
    
    @NotNull
    public String getName() {
        return name;
    }
    
    @NotNull
    public FileBrowserType getType() {
        return type;
    }
    
    public boolean isVariable() {
        return type == FileBrowserType.VARIABLE;
    }
    
    public boolean isDirectory() {
        return type == FileBrowserType.DIRECTORY;
    }
    
    @Override
    public int compareTo(@NotNull FileBrowserEntry entry) {
        int result;
    
        if (this.isVariable())
            result = entry.isVariable()? 0 : -1;
        else
            result = entry.isVariable()? 1 : 0;
        
        if (result != 0) return result;
    
        if (this.isDirectory())
            result = entry.isDirectory()? 0 : -1;
        else
            result = entry.isDirectory()? 1 : 0;
    
        if (result != 0) return result;
        
        return this.name.toLowerCase().compareTo(entry.name.toLowerCase());
    }
    
}
