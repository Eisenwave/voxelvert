package net.grian.vv.plugin;

import net.grian.vv.cache.Language;
import net.grian.vv.cache.Registry;
import net.grian.vv.cmd.CmdConvert;
import org.bukkit.plugin.java.JavaPlugin;

public class VVPlugin extends JavaPlugin {

    private static VVPlugin instance;
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
        getCommand("convert").setExecutor(new CmdConvert());
    }

    public static VVPlugin getInstance() {
        return instance;
    }

    public static Registry getRegistry() {
        return registry;
    }

    public static Language getLanguage() {
        return language;
    }
}
