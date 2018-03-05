package eisenwave.vv.bukkit.http;

import com.google.common.net.MediaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultipartFormEntry {
    
    private final byte[] data;
    private MediaType type;
    private String name, filename;
    
    public MultipartFormEntry(@NotNull byte[] data, @Nullable String name, @Nullable MediaType type, @Nullable String filename) {
        this.data = data;
        setType(type);
        this.name = name;
        this.filename = filename;
    }
    
    public MultipartFormEntry(byte[] data) {
        this(data, null, null, null);
    }
    
    @NotNull
    public byte[] getData() {
        return data;
    }
    
    @NotNull
    public MediaType getType() {
        return type;
    }
    
    @Nullable
    public String getName() {
        return name;
    }
    
    @Nullable
    public String getFilename() {
        return filename;
    }
    
    // SETTERS
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setType(@Nullable MediaType type) {
        this.type = type == null? MediaType.PLAIN_TEXT_UTF_8 : type;
    }
    
}
