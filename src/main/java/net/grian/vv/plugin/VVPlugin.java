package net.grian.vv.plugin;

import net.grian.vv.cache.Registry;
import net.grian.vv.cmd.CmdConvert;
import org.bukkit.plugin.java.JavaPlugin;

import static net.grian.vv.cache.Language.define;

public class VVPlugin extends JavaPlugin {

    private static VVPlugin instance;
    private static Registry registry;

    public void onEnable() {
        initPlugin();
        initData();
        initCommands();
    }

    private void initPlugin() {
        instance = this;
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
        return registry==null? registry=new Registry() : registry;
    }
    
    public static void initLanguage() {
        define(
            "vv:cmd.convert.usage.main",
            "<fromType> <from> <toType> <to> ['-'<argType> <arg>+]*");
        define(
            "vv:cmd.convert.error.invalid_types",
            "Can not convert from %s to %s");
        define(
            "vv:cmd.convert.msg.finish",
            "Finished converting after %dms (%fs)");
        define(
            "vv:cmd.convert.error.interrupt",
            "Converting was interrupted :/");
        define(
            "vv:cmd.convert.error.timeout",
            "Converting took too long (> %sms) and was cancelled :/");
    }
    
}
