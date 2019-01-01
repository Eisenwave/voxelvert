package eisenwave.vv.bukkit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.*;
import eisenwave.inv.EisenInventoriesPluginStartup;
import eisenwave.vv.bukkit.async.*;
import eisenwave.vv.bukkit.cmd.*;
import eisenwave.vv.bukkit.http.*;
import eisenwave.vv.bukkit.user.WorldEditEmergencyListener;
import org.bukkit.*;
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
    
    private EisenInventoriesPluginStartup eisenInventoriesStarter = new EisenInventoriesPluginStartup(this);
    private WorldEditPlugin worldEditPlugin;
    private BukkitVoxelVert voxelVert;
    private VoxelVertConfig config;
    private Language lang;
    private FileTransferManager downloadManager;
    
    private VVConverterThread conversionThread;
    private VVHttpThread httpThread;
    
    private boolean eventsRegistered = false;
    
    // ENABLE & DISABLE
    
    @Override
    public void onLoad() {
        instance = this;
    }
    
    @Override
    public void onEnable() {
        eisenInventoriesStarter.onEnable();
        
        config = new VoxelVertConfig(this, getConfig());
        boolean verbose = config.hasVerbosityOnEnable();
        String langName = config.getLanguage();
        
        initWorldEdit(verbose);
        
        if (!initLanguage(langName, verbose) ||
            !initVoxelVert(verbose) ||
            !initCommands()) {
            getLogger().severe("FAILED TO ENABLE PLUGIN");
            setEnabled(false);
            return;
        }
        
        FormatverterInjector.inject(FormatverterFactory.getInstance());
        
        startConverterThread();
        if (config.isHttpEnabled()) {
            startHttpServer();
        }
        
        if (!eventsRegistered) {
            if (!voxelVert.isWorldEditAvailable()) {
                WorldEditEmergencyListener listener = new WorldEditEmergencyListener(voxelVert);
                getServer().getPluginManager().registerEvents(listener, this);
                eventsRegistered = true;
            }
        }
        
        this.saveDefaultConfig();
    }
    
    @Override
    public void onDisable() {
        eisenInventoriesStarter.onDisable();
        
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
    
    private void initWorldEdit(boolean verbose) {
        WorldEditPlugin we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        String worldEditName = "WorldEdit";
        
        if (we == null)
            getLogger().warning("Failed to load " + worldEditName +
                ", using emergency replacements for WE functionality");
        else {
            worldEditName += " (" + we.getDescription().getVersion() + ")";
            if (we.isEnabled()) {
                try {
                    if (we.getWorldEdit().getPlatformManager().queryCapability(Capability.WORLD_EDITING) == null)
                        throw new NoCapablePlatformException();
                    this.worldEditPlugin = we;
                    if (verbose)
                        getLogger().info("Loaded " + worldEditName);
                } catch (NoCapablePlatformException ex) {
                    getLogger().warning(worldEditName + " is not capable of world editing");
                    getLogger().warning("Failed to load " + worldEditName +
                        ", using emergency replacements for WE functionality");
                }
            }
            else {
                if (verbose)
                    getLogger().warning(worldEditName + " was found but is not enabled");
                getLogger().warning("Failed to load " + worldEditName +
                    ", using emergency replacements for WE functionality");
            }
        }
        
    }
    
    /* private void initEisenInventories(boolean verbose) {
        this.eisenInventoriesPlugin = (EisenInventoriesPlugin) Bukkit.getPluginManager().getPlugin("EisenInventories");
        if (eisenInventoriesPlugin == null) {
            this.eisenInventoriesPlugin = new EisenInventoriesPlugin(this);
            getLogger().warning("Failed to load EisenInventories, using shaded copy instead");
        }
        else if (verbose)
            getLogger().info("Loaded EisenInventories " + eisenInventoriesPlugin.getDescription().getVersion());
    } */
    
    private boolean initVoxelVert(boolean verbose) {
        BlockScanner scanner = new WorldBlockScanner();
        //BlockScanner scanner = new SimpleBlockScanner();
        
        if (verbose)
            getLogger().info("Using block scanner: " + scanner.getClass().getSimpleName());
        
        this.voxelVert = new BukkitVoxelVert(this, worldEditPlugin, scanner);
        return true;
    }
    
    private boolean initCommands() {
        VoxelVertCommand[] commands = {
            new CmdConvert(this),
            new CmdProbeBlock(this),
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
    
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
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
