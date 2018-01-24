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

public class DownloadManager {
    
    private final VoxelVertConfig config;
    private final String downloadHost;
    
    private final Map<String, DownloadEntry> map = new HashMap<>();
    
    public DownloadManager(VoxelVertPlugin plugin, String downloadHost) {
        this.config = plugin.getVVConfig();
        this.downloadHost = downloadHost;
    }
    
    public String urlOfId(String id) {
        return downloadHost + config.getHttpDownloadPath() + "?id=" + id;
    }
    
    public String put(@NotNull MediaType mediaType, @NotNull File file) {
        long random = ThreadLocalRandom.current().nextLong();
        String id = HttpUtil.longToBase64(random);
        map.put(id, new DownloadEntry(id, mediaType, file));
        return id;
    }
    
    @Nullable
    public DownloadManager.DownloadEntry get(String id) {
        return map.get(id);
    }
    
    @Nullable
    public DownloadManager.DownloadEntry remove(String id) {
        return map.remove(id);
    }
    
    public void clear() {
        map.clear();
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
    
}
