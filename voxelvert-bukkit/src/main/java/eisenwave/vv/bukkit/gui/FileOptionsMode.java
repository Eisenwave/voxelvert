package eisenwave.vv.bukkit.gui;

public enum FileOptionsMode {
    EMPTY,
    DEFAULT,
    VARIABLE,
    KNOWN_FILE,
    FILE,
    FOLDER,
    DELETE,
    SHARE_FILE;
    
    public static FileOptionsMode fromType(FileType type) {
        if (type.isDirectory())
            return FOLDER;
        if (type.isVariable())
            return VARIABLE;
        if (type.isFile())
            return type.isFormat()? KNOWN_FILE : FILE;
        else return EMPTY;
    }
    
}
