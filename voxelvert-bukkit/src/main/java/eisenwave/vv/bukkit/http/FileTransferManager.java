package eisenwave.vv.bukkit.http;

import com.google.common.net.MediaType;
import eisenwave.vv.bukkit.VoxelVertConfig;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.util.HttpUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class FileTransferManager {
    
    private final VoxelVertConfig config;
    private final String host;
    
    private final Map<String, DownloadEntry> downloads = new HashMap<>();
    private final Map<String, UploadEntry> uploads = new HashMap<>();
    
    public FileTransferManager(VoxelVertPlugin plugin, String host) {
        this.config = plugin.getVVConfig();
        this.host = host;
    }
    
    public String getDownloadUrl(String id) {
        return host + config.getHttpDownloadPath() + "?id=" + id;
    }
    
    public String getUploadUrl(String id) {
        return host + config.getHttpUploadPath() + "?id=" + id;
    }
    
    public String makeDownloadable(@NotNull MediaType mediaType, @NotNull File file) {
        long random = ThreadLocalRandom.current().nextLong();
        String id = HttpUtil.longToBase64(random);
        downloads.put(id, new DownloadEntry(id, mediaType, file));
        return id;
    }
    
    public String makeUploadable(long maxSize) {
        if (maxSize < 0) throw new IllegalArgumentException("max size must be positive");
        
        long random = ThreadLocalRandom.current().nextLong();
        String id = HttpUtil.longToBase64(random);
        uploads.put(id, new UploadEntry(id, maxSize));
        return id;
    }
    
    @Nullable
    public DownloadEntry getDownload(String id) {
        return downloads.get(id);
    }
    
    @Nullable
    public UploadEntry getUpload(String id) {
        return uploads.get(id);
    }
    
    public boolean hasDownload(@NotNull String id) {
        return downloads.containsKey(id);
    }
    
    public boolean hasUpload(@NotNull String id) {
        return uploads.containsKey(id);
    }
    
    @Nullable
    public DownloadEntry removeDownload(String id) {
        return downloads.remove(id);
    }
    
    @Nullable
    public UploadEntry removeUpload(String id) {
        return uploads.remove(id);
    }
    
    public void clear() {
        downloads.clear();
        uploads.clear();
    }
    
    // UTIL
    
    public static class DownloadEntry {
        
        private final String id;
        private final MediaType type;
        private final File file;
        
        public DownloadEntry(String id, MediaType type, File file) {
            this.id = id;
            this.type = type;
            this.file = file;
        }
        
        public String getId() {
            return id;
        }
        
        public MediaType getMediaType() {
            return type;
        }
        
        public File getFile() {
            return file;
        }
        
    }
    
    public static class UploadEntry {
        
        private final String id;
        private final long maxSize;
        
        public UploadEntry(String id, long size) {
            this.id = id;
            this.maxSize = size;
        }
        
        public String getId() {
            return id;
        }
        
        public long getMaxSize() {
            return maxSize;
        }
        
    }
    
}
