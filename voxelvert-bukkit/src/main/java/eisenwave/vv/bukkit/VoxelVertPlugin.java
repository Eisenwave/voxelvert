package eisenwave.vv.bukkit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import eisenwave.vv.bukkit.async.*;
import eisenwave.vv.bukkit.cmd.*;
import eisenwave.vv.bukkit.http.FileTransferManager;
import eisenwave.vv.bukkit.http.VVHttpThread;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import eisenwave.vv.bukkit.inject.FormatverterInjector;
import eisenwave.vv.bukkit.user.BukkitVoxelVert;
import eisenwave.vv.io.DeserializerLanguage;
import eisenwave.vv.object.Language;
import eisenwave.vv.ui.fmtvert.FormatverterFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class VoxelVertPlugin extends JavaPlugin {
    
    private static VoxelVertPlugin instance;
    
    private WorldEditPlugin worldEditPlugin;
    private BukkitVoxelVert voxelVert;
    private VoxelVertConfig config;
    private Language lang;
    private FileTransferManager downloadManager;
    
    private VVConverterThread conversionThread;
    private VVHttpThread httpThread;
    
    // ENABLE & DISABLE
    
    @Override
    public void onLoad() {
        instance = this;
    }
    
    @Override
    public void onEnable() {
        config = new VoxelVertConfig(getConfig());
        boolean verbose = config.hasVerbosityOnEnable();
        String langName = config.getLanguage();
    
        if (!initWorldEdit(verbose)
            || !initLanguage(langName, verbose)
            || !initVoxelVert(verbose)
            || !initCommands()) {
            getLogger().severe("FAILED TO ENABLE PLUGIN");
            setEnabled(false);
            return;
        }
    
        startConverterThread();
        if (config.isHttpEnabled()) {
            startHttpServer();
        }
        
        this.saveDefaultConfig();
    
        FormatverterInjector.inject(FormatverterFactory.getInstance());
    }
    
    @Override
    public void onDisable() {
        boolean verbose = config.hasVerbosityOnDisable();
        this.conversionThread.interrupt();
        if (httpThread != null)
            this.httpThread.interrupt();
        
        if (verbose) getLogger().info("goodbye");
    }
    
    private boolean initLanguage(String name, boolean verbose) {
        try {
            File langDir = new File(getDataFolder(), "lang");
            File langFile = new File(langDir, name);
            this.lang = new DeserializerLanguage(name).fromFile(langFile);
            if (verbose) getLogger().info(String.format("using language \"%s\"", lang));
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
        if (verbose) getLogger().info("Loaded WorldEdit " + worldEditPlugin.getDescription().getVersion());
        return true;
    }
    
    private boolean initVoxelVert(boolean verbose) {
        BlockScanner scanner = new CachedBlockScanner(this);
        //BlockScanner scanner = new SimpleBlockScanner();
        
        if (verbose) getLogger().info("Using block scanner: " + scanner.getClass().getSimpleName());
        
        this.voxelVert = new BukkitVoxelVert(this, worldEditPlugin, scanner);
        return true;
    }
    
    private boolean initCommands() {
        VoxelVertCommand[] commands = {
            new CmdConvert(this),
            new CmdVoxelvert(this),
            new CmdList(this),
            new CmdRemove(this),
            new CmdMove(this),
            new CmdCopy(this),
            new CmdShare(this)
        };
    
        Arrays.stream(commands).forEach(cmd -> getCommand(cmd.getName()).setExecutor(cmd));
        return true;
    }
    
    // GETTERS
    
    public static VoxelVertPlugin getInstance() {
        return instance;
    }
    
    @NotNull
    public FileTransferManager getFileTransferManager() {
        return downloadManager;
    }
    
    @NotNull
    public Thread getConverterThread() {
        return conversionThread;
    }
    
    public Thread getHttpThread() {
        return httpThread;
    }
    
    public boolean isHttpServerStarted() {
        return httpThread != null && httpThread.hasStartupSuccess();
    }
    
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
    
    // SETTERS
    
    public void setFileTransferManager(@NotNull FileTransferManager manager) {
        this.downloadManager = manager;
    }
    
    // THREAD STARTERS
    
    public void startConverterThread() {
        if (conversionThread != null && conversionThread.isAlive())
            conversionThread.interrupt();
        this.conversionThread = this.voxelVert.startConversionThread();
    }
    
    public void startHttpServer() {
        if (httpThread != null && httpThread.isAlive())
            httpThread.interrupt();
        this.httpThread = new VVHttpThread(this);
        this.httpThread.start();
    }
    
}
