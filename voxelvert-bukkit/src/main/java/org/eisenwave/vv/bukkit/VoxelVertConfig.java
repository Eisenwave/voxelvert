package org.eisenwave.vv.bukkit;

import org.bukkit.configuration.file.YamlConfiguration;

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
    vRuntime,
    syntaxHighlighting;
    
    public VoxelVertConfig(Reader reader) {
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(reader);
        
        this.lang = (String) yml.get("language", "en_us.lang");
        this.vEnable = yml.getBoolean("verbosity.enable", false);
        this.vDisable = yml.getBoolean("verbosity.disable", false);
        this.vRuntime = yml.getBoolean("verbosity.runtime", false);
        this.syntaxHighlighting = yml.getBoolean("syntax_highlighting", true);
    }
    
    public VoxelVertConfig(InputStream stream) {
        this(new InputStreamReader(stream));
    }
    
    // GETTERS
    
    public String getLanguage() {
        return lang;
    }
    
    public boolean hasVerbosityOnEnable() {
        return vEnable;
    }
    
    public boolean hasVerbosityOnDisable() {
        return vDisable;
    }
    
    public boolean hasVerbosityOnRuntime() {
        return vRuntime;
    }
    
    public boolean hasSyntaxHighlighting() {
        return syntaxHighlighting;
    }
    
}
