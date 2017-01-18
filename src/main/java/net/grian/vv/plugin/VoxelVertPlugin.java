package net.grian.vv.plugin;

import net.grian.vv.cache.Language;
import net.grian.vv.cache.Registry;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

public class VoxelVertPlugin extends JavaPlugin {

    private static VoxelVertPlugin instance;
    private static Registry registry;
    private static Language language;

    public void onEnable() {
        initPlugin();
        initData();
        initCommands();
    }

    private void initPlugin() {
        instance = this;
        language = new Language();
    }

    private void initData() {
        registry = new Registry();
    }

    private void initCommands() {
        Command[] commands = {

        };
        for (Command command : commands);
    }

    public static VoxelVertPlugin getInstance() {
        return instance;
    }

    public static Registry getRegistry() {
        return registry;
    }

    public static Language getLanguage() {
        return language;
    }
}