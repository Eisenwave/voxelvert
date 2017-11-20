package org.eisenwave.vv.bukkit.cmd;

import eisenwave.inv.menu.MenuManager;
import nl.klikenklaar.util.gui.menus.Menu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eisenwave.vv.bukkit.VoxelVertPlugin;
import org.eisenwave.vv.bukkit.gui.old_menu.ConversionFormatChooserMenu;
import org.eisenwave.vv.bukkit.gui.menu.FileBrowserMenu;
import org.eisenwave.vv.bukkit.user.BukkitVoxelVert;
import org.eisenwave.vv.bukkit.util.CommandUtil;
import org.eisenwave.vv.object.Language;
import org.eisenwave.vv.ui.fmtvert.Format;
import org.eisenwave.vv.ui.user.VVUser;

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
        
        if (!(sender instanceof Player)) {
            user.error("Only players can use the VoxelVert command!");
            return true;
        }
        
        if (args.length == 0 || args[0].equals("files")) {
            Player player = (Player) sender;
            FileBrowserMenu menu = new FileBrowserMenu(user.getInventory()); // TODO how the fuck do I show this shit to players
            MenuManager.getInstance().startSession(player, menu);
            return true;
        }
        
        else if (args[0].equals("convert")) {
            if (args.length < 2) return false;
            
            String in = args[0];
    
            if (in.startsWith("/")) {
                user.error(lang.get("err.path_absolute"));
                return true;
            }
            if (in.startsWith(".")) {
                user.error(lang.get("err.path_hidden"));
                return true;
            }
    
            final Format inFormat;
            if (user.getInventory().hasVariable(in)) {
                //noinspection ConstantConditions
                inFormat = user.getInventory().getVariable(in).getFormat();
            } else {
                String ext = CommandUtil.extensionOf(in);
                if (ext == null) {
                    user.error(lang.get("cmd.voxelvert.err.no_ext"), in);
                    return true;
                }
                inFormat = Format.getByExtension(ext);
            }
            
            // vv convert file
            if (args.length < 3) {
                Menu menu = new ConversionFormatChooserMenu(inFormat, in);
                // TODO show to player
            }
    
            // vv convert file format
            else {
            
            }
            
            return true;
        }
        
        else return false;
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
