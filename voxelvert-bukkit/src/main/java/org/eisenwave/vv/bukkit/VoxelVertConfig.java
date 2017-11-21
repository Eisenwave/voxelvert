package org.eisenwave.vv.bukkit;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Read-only wrapper for VoxelVert plugin config
 */
public class VoxelVertConfig {
    
    private final String lang;
    
    private final boolean
    vEnable,
    vDisable,
    vRuntime;
    
    public VoxelVertConfig(Reader reader) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(reader);
        
        this.lang = (String) yml.get("language", "en_us.lang");
        this.vEnable = yml.getBoolean("verbosity.enable", false);
        this.vDisable = yml.getBoolean("verbosity.disable", false);
        this.vRuntime = yml.getBoolean("verbosity.runtime", false);
        //this.syntaxHighlighting = yml.getBoolean("syntax_highlighting", true);
    }
    
    public VoxelVertConfig(InputStream stream) {
        this(new InputStreamReader(stream));
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
    
}
