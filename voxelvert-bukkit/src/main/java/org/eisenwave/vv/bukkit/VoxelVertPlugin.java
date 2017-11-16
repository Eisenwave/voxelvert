package org.eisenwave.vv.bukkit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.eisenwave.vv.bukkit.cmd.*;
import org.eisenwave.vv.bukkit.inject.FormatverterInjector;
import org.eisenwave.vv.bukkit.async.CachedBlockScanner;
import org.eisenwave.vv.bukkit.async.BlockScanner;
import org.eisenwave.vv.bukkit.async.BulkEditBlockScanner;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.io.DeserializerLanguage;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.fmtvert.FormatverterFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class VoxelVertPlugin extends JavaPlugin {
    
    private WorldEditPlugin worldEditPlugin;
    private BukkitVoxelVert voxelVert;
    private VoxelVertConfig config;
    private Language lang;
    
    private Thread conversionThread;
    
    // ENABLE & DISABLE
    
    @Override
    public void onEnable() {
        config = new VoxelVertConfig(getResource("config.yml"));
        boolean verbose = config.hasVerbosityOnEnable();
        String langName = config.getLanguage();
        
        if (!initCommands()
            || !initWorldEdit(verbose)
            || !initLanguage(langName, verbose)
            || !initVoxelVert(verbose)) {
            getLogger().severe("FAILED TO ENABLE PLUGIN");
            setEnabled(false);
            return;
        }
        
        this.conversionThread = this.voxelVert.initConversion();
        this.saveDefaultConfig();
    
        FormatverterInjector.inject(FormatverterFactory.getInstance());
    }
    
    @Override
    public void onDisable() {
        boolean verbose = config.hasVerbosityOnDisable();
        this.conversionThread.interrupt();
        
        if (verbose) getLogger().info("goodbye");
    }
    
    private boolean initLanguage(String name, boolean verbose) {
        try {
            File langDir = new File(getDataFolder(), "lang");
            File langFile = new File(langDir, name);
            this.lang = new DeserializerLanguage(name).fromFile(langFile);
            if (verbose) getLogger().info(String.format("[VoxelVert] using language \"%s\"", lang));
        } catch (IOException ex) {
            getLogger().warning(String.format("Failed to load language \"%s\", using default language", name));
        }
    
        try {
            this.lang = new DeserializerLanguage("default").fromResource(getClass(), "bukkit_lang/en_us.lang");
            return true;
        } catch (IOException ex) {
            getLogger().severe("Failed to load default language");
            return false;
        }
    }
    
    private boolean initWorldEdit(boolean verbose) {
        this.worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null) {
            getLogger().severe("Failed to load WorldEdit");
            return false;
        }
        if (verbose)
            System.out.println("[VoxelVert] loaded WorldEdit "+worldEditPlugin.getDescription().getVersion());
        return true;
    }
    
    private boolean initVoxelVert(boolean verbose) {
        BlockScanner scanner = Bukkit.getPluginManager().isPluginEnabled("BulkEdit")?
            new BulkEditBlockScanner() : new CachedBlockScanner(this);
        
        if (verbose) getLogger().info("using block scanner: " + scanner.getClass().getSimpleName());
        
        this.voxelVert = new BukkitVoxelVert(this, worldEditPlugin, scanner);
        return true;
    }
    
    private boolean initCommands() {
        getCommand("convert").setExecutor(new CmdConvert(this));
        getCommand("voxelvert").setExecutor(new CmdVoxelvert(this));
        getCommand("voxelvert-list").setExecutor(new CmdList(this));
        getCommand("voxelvert-remove").setExecutor(new CmdRemove(this));
        return true;
    }
    
    // GETTERS
    
    @NotNull
    public BukkitVoxelVert getVoxelVert() {
        return voxelVert;
    }
    
    @NotNull
    public VoxelVertConfig getVVConfig() {
        return config;
    }
    
    @NotNull
    public WorldEditPlugin getWorldEdit() {
        return worldEditPlugin;
    }
    
    @NotNull
    public Language getLanguage() {
        return lang;
    }
    
}