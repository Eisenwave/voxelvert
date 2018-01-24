package eisenwave.vv.bukkit;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.net.InetSocketAddress;

/**
 * Read-only wrapper for VoxelVert plugin config
 */
public class VoxelVertConfig {
    
    private final String lang;
    
    private final boolean vEnable, vDisable, vRuntime;
    
    private final InetSocketAddress httpPort;
    private final String httpHost, httpDownloadPath, httpUploadPath;
    private final boolean httpEnable;
    
    public VoxelVertConfig(FileConfiguration yml) {
        //YamlConfiguration yml = YamlConfiguration.loadConfiguration(reader);
        
        this.lang = (String) yml.get("language", "en_us.lang");
        this.vEnable = yml.getBoolean("verbosity.enable", false);
        this.vDisable = yml.getBoolean("verbosity.disable", false);
        this.vRuntime = yml.getBoolean("verbosity.runtime", false);
        
        this.httpEnable = yml.getBoolean("http.enable", false);
        this.httpPort = new InetSocketAddress(yml.getInt("http.port", 26000));
        this.httpHost = yml.getString("http.host", "$localhost:$port");
        this.httpDownloadPath = yml.getString("http.download_path", "/vv/dl");
        this.httpUploadPath = yml.getString("http.upload_path", "/vv/up");
        //this.syntaxHighlighting = yml.getBoolean("syntax_highlighting", true);
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
    
}
