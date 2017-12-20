package eisenwave.vv.bukkit.cmd;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.menu.MenuManager;
import eisenwave.vv.bukkit.gui.menu.ConvertMenu;
import eisenwave.vv.bukkit.gui.menu.FileBrowserMenu;
import eisenwave.vv.ui.fmtvert.Format;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.Nullable;

public class CmdVoxelvert extends VoxelVertCommand {
    
    /*
    @Override
    public Parser[] formatOf(String[] args) {
        return new Parser[0];
    }
    */
    
    public CmdVoxelvert(VoxelVertPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "voxelvert";
    }
    
    @Override
    public String getUsage() {
        return "(convert|converter|files|reload|status|version)";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
        
        // vv files
        if (args.length == 0 || args[0].equals("files")) {
            if (!(sender instanceof Player)) {
                user.errorLocalized("error.not_a_player");
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
                user.errorLocalized("error.not_a_player");
                return true;
            }
            Player player = (Player) sender;
            
            if (args.length < 2) return false;
            
            final String source = args[1];
            if (source.startsWith("/")) {
                user.errorLocalized("error.path_absolute");
                return true;
            }
            if (source.startsWith(".")) {
                user.errorLocalized("error.path_hidden");
                return true;
            }
    
            final Format sourceFormat;
            if (args.length < 3) {
                sourceFormat = formatOf(user.getInventory(), source);
                if (sourceFormat == null) {
                    user.errorLocalized("cmd.voxelvert.err.bad_extension");
                    return true;
                }
            }
            else {
                sourceFormat = Format.getById(args[2]);
                if (sourceFormat == null) {
                    user.errorLocalized("cmd.voxelvert.err.bad_format");
                    return true;
                }
            }
            
            Menu menu = new ConvertMenu(user, source, sourceFormat); //TODO create convert menu
            MenuManager.getInstance().startSession(player, menu);
            return true;
        }

        else if (args[0].equals("status")) {
            if (!requirePermission(sender, user, "vv.admin.status")) return true;
    
            String format = user.getLanguage().get("user.keyval");
    
            user.printLocalized("cmd.voxelvert.status");
            sender.sendMessage(CommandUtil.chatColors(String.format(
                format, "Language",
                plugin.getLanguage().getName()
            )));
            sender.sendMessage(CommandUtil.chatColors(String.format(
                format, "Converter-Status",
                plugin.getConverterThread().getState().toString()
            )));
            return true;
        }

        else if (args[0].equals("reload")) {
            if (!requirePermission(sender, user, "vv.admin.reload")) return true;
    
            user.printLocalized("cmd.voxelvert.reload");
            plugin.onDisable();
            plugin.onEnable();
            user.printLocalized("cmd.voxelvert.reloaded");
            return true;
        }

        else if (args[0].equals("version")) {
            if (!requirePermission(sender, user, "vv.admin.version")) return true;
    
            user.printLocalized("cmd.voxelvert.version", plugin.getDescription().getVersion());
            return true;
        }

        else if (args[0].equals("converter")) {
            if (!requirePermission(sender, user, "vv.admin.converter")) return true;
            if (args.length < 2) return false;
    
            Thread thread = plugin.getConverterThread();
    
            switch (args[1]) {
                case "state":
                    user.print(thread.getState().toString());
                    break;
                case "priority":
                    user.print(Integer.toString(thread.getPriority()));
                    break;
                case "name":
                    user.print(thread.getName());
                    break;
                case "restart":
                    voxelVert.startConversionThread();
                    user.print("restarted");
                    break;
                default: return false;
            }
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
