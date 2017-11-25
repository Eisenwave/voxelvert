package eisenwave.vv.bukkit.gui;

import org.jetbrains.annotations.NotNull;

public class FileBrowserEntry implements Comparable<FileBrowserEntry> {
    
    private final String name;
    private final FileType type;
    
    public FileBrowserEntry(@NotNull String name) {
        this.name = name;
        this.type = FileType.fromPath(name);
    }
    
    /**
     * Returns the name of this entry.
     *
     * @return the name
     */
    @NotNull
    public String getName() {
        return name;
    }
    
    /**
     * Returns the full, absolute path of this browser entry.
     *
     * @return the path
     */
    @NotNull
    public String getPath() {
        return name;
    }
    
    public String getDisplayName(boolean colors) {
        String clearName;
        if (name.endsWith("/")) clearName = name.substring(0, name.length() - 1);
        else clearName = name;
        
        return (colors? type.getPrefix() : type.getPrefixNoColors()) + clearName;
    }
    
    @NotNull
    public FileType getType() {
        return type;
    }
    
    public boolean isHidden() {
        return name.startsWith(".");
    }
    
    public boolean isVariable() {
        return type == FileType.VARIABLE;
    }
    
    public boolean isDirectory() {
        return type == FileType.DIRECTORY;
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
