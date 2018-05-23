package eisenwave.vv.bukkit;

import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.ui.fmtvert.Format;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Read-only wrapper for VoxelVert plugin config
 */
public class VoxelVertConfig {
    
    private final static long
        DEFAULT_LIMIT_DEFAULT = 102400,
        DEFAULT_LIMIT_OP = 1024 * 1024;
    
    private final static Map<String, String> FORMAT_LIMIT_KEYS = new HashMap<>();
    
    static {
        for (Format format : Format.values()) {
            String id = format.getId();
            FORMAT_LIMIT_KEYS.put(id, "format." + id);
        }
        FORMAT_LIMIT_KEYS.put("upload", "upload");
        FORMAT_LIMIT_KEYS.put("download", "download");
    }
    
    private final static FileConfiguration DEFAULT_CONFIG;
    
    static {
        DEFAULT_CONFIG = new YamlConfiguration();
        try (InputStream stream = VoxelVertConfig.class.getClassLoader().getResourceAsStream("config.yml");
             Reader reader = new InputStreamReader(stream)) {
            DEFAULT_CONFIG.load(reader);
        } catch (InvalidConfigurationException | IOException ex) {
            throw new IOError(ex);
        }
    }
    
    private final VoxelVertPlugin plugin;
    
    private final String lang;
    
    private final boolean vEnable, vDisable, vRuntime;
    
    private final InetSocketAddress httpPort;
    private final String httpHost, httpDownloadPath, httpUploadPath;
    private final boolean httpEnable;
    
    private final Map<String, Long> defaultLimits = new HashMap<>();
    private final Map<String, Long> opLimits = new HashMap<>();
    
    public VoxelVertConfig(VoxelVertPlugin plugin, FileConfiguration yml) {
        Logger logger = plugin.getLogger();
        yml.setDefaults(DEFAULT_CONFIG);
        
        //YamlConfiguration yml = YamlConfiguration.loadConfiguration(reader);
        this.plugin = plugin;
        
        this.lang = (String) yml.get("language", "en_us.lang");
        this.vEnable = yml.getBoolean("verbosity.enable", false);
        this.vDisable = yml.getBoolean("verbosity.disable", false);
        this.vRuntime = yml.getBoolean("verbosity.runtime", false);
        
        this.httpEnable = yml.getBoolean("http.enable", false);
        this.httpPort = new InetSocketAddress(yml.getInt("http.port", 26000));
        this.httpHost = yml.getString("http.host", "$localhost:$port");
        this.httpDownloadPath = yml.getString("http.download_path", "/vv/dl");
        this.httpUploadPath = yml.getString("http.upload_path", "/vv/up");
        
        ConfigurationSection defaultLimitsSection = yml.getConfigurationSection("file_limits.default");
        ConfigurationSection opLimitsSection = yml.getConfigurationSection("file_limits.op");
        
        if (defaultLimitsSection != null)
            readLimits(defaultLimitsSection, defaultLimits);
        else
            logger.warning("missing section \"file_limits.default\" in config");
        if (opLimitsSection != null)
            readLimits(opLimitsSection, opLimits);
        else
            logger.warning("missing section \"file_limits.op\" in config");
        
        if (vEnable) {
            logger.info("configured " + defaultLimits.size() + " file limits for group \"default\" (default players)");
            logger.info("configured " + opLimits.size() + " file limits for group \"op\" (operators)");
        }
    }
    
    private void readLimits(ConfigurationSection section, Map<String, Long> map) {
        /* System.out.println(ANSI.FG_RED + section.getCurrentPath() + ANSI.RESET);
        for (String key : section.getKeys(true))
            System.out.print(key + ", "); */
        
        for (Map.Entry<String, String> entry : FORMAT_LIMIT_KEYS.entrySet()) {
            String keyInMap = entry.getKey();
            String keyInSection = entry.getValue();
            
            long size;
            try {
                String value = section.getString(keyInSection, null);
                if (value == null) {
                    continue;
                }
                size = CommandUtil.parseFileSize(value);
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("failed to parse " + section.getCurrentPath() + "." + keyInSection);
                continue;
            }
            if (size < 0) {
                plugin.getLogger().warning("negative value ignored: " + section.getCurrentPath() + "." + keyInSection);
                continue;
            }
            
            map.put(keyInMap, size);
        }
    }
    
    // GETTERS
    
    /**
     * Returns the language path of this config.
     *
     * @return the language path
     */
    public String getLanguage() {
        return lang;
    }
    
    /**
     * Returns whether the plugin is verbose when being enabled.
     *
     * @return enabling verbosity
     * @see Plugin#onEnable()
     */
    public boolean hasVerbosityOnEnable() {
        return vEnable;
    }
    
    /**
     * Returns whether the plugin is verbose when being disabled.
     *
     * @return disabling verbosity
     * @see Plugin#onDisable()
     */
    public boolean hasVerbosityOnDisable() {
        return vDisable;
    }
    
    /**
     * Returns whether the plugin is verbose during runtime.
     *
     * @return runtime verbosity
     */
    public boolean hasVerbosityOnRuntime() {
        return vRuntime;
    }
    
    public boolean isHttpEnabled() {
        return httpEnable;
    }
    
    public InetSocketAddress getHttpAddress() {
        return httpPort;
    }
    
    public String getHttpHost() {
        return httpHost;
    }
    
    public String getHttpDownloadPath() {
        return httpDownloadPath;
    }
    
    public String getHttpUploadPath() {
        return httpUploadPath;
    }
    
    public long getFileLimitOfDefault(String format) {
        return defaultLimits.getOrDefault(format, DEFAULT_LIMIT_DEFAULT);
    }
    
    public long getFileLimitOfOp(String format) {
        return opLimits.getOrDefault(format, DEFAULT_LIMIT_OP);
    }
    
}
