package eisenwave.vv.bukkit.gui;

public enum MediaCategory {
    DIRECTORY,
    BINARY,
    TEXT,
    COMPRESSED,
    NBT,
    TEMPORARY;
    
    private final String langName;
    
    MediaCategory(String langName) {
        this.langName = langName;
    }
    
    MediaCategory() {
        this.langName = "menu.files.media_category." + this.name().toLowerCase();
    }
    
    public String getLanguageName() {
        return langName;
    }
    
}
