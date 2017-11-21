package org.eisenwave.vv.bukkit.cmd;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.menu.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eisenwave.vv.bukkit.VoxelVertPlugin;
import org.eisenwave.vv.bukkit.gui.menu.ConvertMenu;
import org.eisenwave.vv.bukkit.gui.old_menu.ConversionFormatChooserMenu;
import org.eisenwave.vv.bukkit.gui.menu.FileBrowserMenu;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.fmtvert.Format;
import org.eisenwave.vv.ui.user.VVInventory;
import org.eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.Nullable;

public class CmdVoxelvert implements CommandExecutor {
    
    /*
    @Override
    public Parser[] formatOf(String[] args) {
        return new Parser[0];
    }
    */
    
    private final static String USAGE = "&cUsage: /vv files OR /vv convert <file> [format]";
    
    private final VoxelVertPlugin plugin;
    
    public CmdVoxelvert(VoxelVertPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        command.setUsage(USAGE);
    
        BukkitVoxelVert vv = plugin.getVoxelVert();
        VVUser user = CommandUtil.userOf(vv, sender);
        Language lang = vv.getLanguage();
        
        // vv files
        if (args.length == 0 || args[0].equals("files")) {
            if (!(sender instanceof Player)) {
                user.error(lang.get("err.not_a_player"));
                return true;
            }
            Player player = (Player) sender;
            
            FileBrowserMenu menu = new FileBrowserMenu(user.getInventory());
            MenuManager.getInstance().startSession(player, menu);
            return true;
        }
        
        // vv convert <sourceFile> [format]
        else if (args[0].equals("convert")) {
            if (!(sender instanceof Player)) {
                user.error(lang.get("err.not_a_player"));
                return true;
            }
            Player player = (Player) sender;
            
            if (args.length < 2) return false;
            
            final String source = args[1];
            if (source.startsWith("/")) {
                user.error(lang.get("err.path_absolute"));
                return true;
            }
            if (source.startsWith(".")) {
                user.error(lang.get("err.path_hidden"));
                return true;
            }
    
            final Format sourceFormat;
            if (args.length < 3) {
                sourceFormat = formatOf(user.getInventory(), source);
                if (sourceFormat == null) {
                    user.error("cmd.voxelvert.err.bad_extension");
                    return true;
                }
            }
            else {
                sourceFormat = Format.getById(args[2]);
                if (sourceFormat == null) {
                    user.error("cmd.voxelvert.err.bad_format");
                    return true;
                }
            }
            
            Menu menu = new ConvertMenu(source, sourceFormat); //TODO create convert menu
            MenuManager.getInstance().startSession(player, menu);
            return true;
        }
        
        else if (args[0].equals("daemon")) {
            sender.sendMessage(plugin.getConverterThread().getState().toString());
            return true;
        }
        
        else return false;
    }
    
    @Nullable
    private Format formatOf(VVInventory inventory, String path) {
        if (inventory.hasVariable(path)) {
            //noinspection ConstantConditions
            return inventory.getVariable(path).getFormat();
        }
        else {
            String ext = CommandUtil.extensionOf(path);
            return ext == null? null : Format.getByExtension(ext);
        }
    }
    
    /*private void printUser(CommandSender sender, VVUser user) {
        StringBuilder builder = new StringBuilder();
        
        builder
            .append("Name: ")
            .append(user.getName())
            .append("\n");
        
        builder.append("Data: ");
        for (String key : user.listData()) {
            builder
                .append("\n \u2022")
                .append(key)
                .append(": ")
                .append(user.getData(key));
        }
        builder.append("\nDirectory: ");
    
        File dir;
        try {
            dir = user.getDirectory();
        } catch (IOException e) {
            dir = null;
        }
        builder
            .append("Directory")
            .append(dir);
        
        sender.sendMessage(builder.toString());
    }
    
    @Override
    public String[] onTabComplete(CommandSender sender, Command command, String label, Object[] args) {
        return new String[0];
    }
    */
    
}
