package eisenwave.vv.bukkit.cmd;

import eisenwave.inv.menu.Menu;
import eisenwave.inv.menu.MenuManager;
import eisenwave.vv.bukkit.gui.menu.ConvertMenu;
import eisenwave.vv.bukkit.gui.menu.FileBrowserMenu;
import eisenwave.vv.bukkit.http.FileTransferManager;
import eisenwave.vv.ui.fmtvert.Format;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVUser;
import org.jetbrains.annotations.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CmdVoxelvert extends VoxelVertCommand implements TabCompleter {
    
    private final static List<String> TAB_COMPLETE_OPTIONS = Arrays.asList(
        "convert",
        "converter",
        "dev",
        "files",
        "reload",
        "status",
        "upload",
        "version"
    );
    private final static long MAX_UPLOAD_SIZE = 4 * 1024 * 1024;
    
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
        return "(convert|converter|files|reload|status|upload|version)";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
    
        String subcommand = args.length == 0? "files" : args[0];
    
        switch (subcommand) {
        
            // vv files
            case "files": {
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
            case "convert": {
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
            
                Menu menu = new ConvertMenu(user, source, sourceFormat);
                MenuManager.getInstance().startSession(player, menu);
                return true;
            }
        
            case "dev": {
                if (!sender.isOp()) {
                    user.error("You must be an operator to run this command.");
                }
            
                return true;
            }
        
            case "status": {
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
                sender.sendMessage(CommandUtil.chatColors(String.format(
                    format, "HTTP-Init-Status",
                    plugin.getHttpThread().getState().toString()
                )));
                return true;
            }
        
            case "reload": {
                if (!requirePermission(sender, user, "vv.admin.reload")) return true;
            
                user.printLocalized("cmd.voxelvert.reload");
                plugin.onDisable();
                plugin.onEnable();
                user.printLocalized("cmd.voxelvert.reloaded");
                return true;
            }
        
            case "version": {
                if (!requirePermission(sender, user, "vv.admin.version")) return true;
            
                user.printLocalized("cmd.voxelvert.version", plugin.getDescription().getVersion());
                return true;
            }
        
            case "converter": {
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
        
            case "upload": {
                if (!requirePermission(sender, user, "vv.upload")) return true;
                if (!plugin.isHttpServerStarted()) {
                    user.printLocalized("cmd.share.err.no_upload_server");
                    return true;
                }
            
                FileTransferManager transferManager = plugin.getFileTransferManager();
                String id = transferManager.makeUploadable(user, MAX_UPLOAD_SIZE, user.getInventory().getDirectory());
                String url = transferManager.getUploadUrl(id);
                user.printLocalized("upload.url", url);
                return true;
            }
        
            default: return false;
        }
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
    */
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1)
            return null;
    
        return TAB_COMPLETE_OPTIONS
            .stream()
            .filter(option -> option.startsWith(args[0]))
            .collect(Collectors.toList());
    }
    
}
