package eisenwave.vv.bukkit.cmd;

import com.google.common.net.MediaType;
import eisenwave.spatium.util.Strings;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.http.FileTransferManager;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.bukkit.util.HttpUtil;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVUser;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

public class CmdShare extends VoxelVertCommand {
    
    public CmdShare(@NotNull VoxelVertPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "voxelvert-share";
    }
    
    @Override
    public String getUsage() {
        return "(download|player|worldedit) <source> [target]";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
        if (args.length < 2) return false;
    
        //final String path = args[1];
        
        
        switch (args[0]) {
            case "player":
                if (args.length < 3) return false;
                if (!requirePermission(sender, user, "vv.share.player")) return true;
                
                user.printLocalized("cmd.share.wip");
                return true;
            
            case "download": {
                if (!requirePermission(sender, user, "vv.download")) return true;
                String path = Strings.join(" ", Arrays.copyOfRange(args, 1, args.length));
                
                if (!plugin.isHttpServerStarted()) {
                    user.printLocalized("cmd.share.err.no_download_server");
                    return true;
                }
                File file = user.getInventory().getFile(path);
                if (!file.isFile()) {
                    user.errorLocalized("cmd.share.err.not_a_file");
                    return true;
                }
                String ext = CommandUtil.extensionOf(path);
                MediaType mediaType = ext == null? MediaType.APPLICATION_BINARY : HttpUtil.getMediaType(ext, false);
    
                FileTransferManager downloadManager = plugin.getFileTransferManager();
                String id = downloadManager.makeDownloadable(user, mediaType, file);
                String url = downloadManager.getDownloadUrl(id);
                user.printLocalized("download.url", url);
                return true;
            }
    
            case "worldedit": {
                if (!requirePermission(sender, user, "vv.share.worldedit")) return true;
                String path = Strings.join(" ", Arrays.copyOfRange(args, 1, args.length));
        
                assert args[0].equals("worldedit");
                if (!path.endsWith(".schem") && !path.endsWith(".schematic")) {
                    user.errorLocalized("cmd.share.err.we_no_schematic", path);
                    return true;
                }
                user.printLocalized("cmd.share.wip");
                return true;
            }
    
            default: return false;
        }
        
        /*
        try {
            if (inventory.move(source, target))
                user.printLocalized("cmd.move.success", source, target);
            else
                user.errorLocalized("cmd.move.failure", source, target);
            return true;
        } catch (IOException ex) {
            user.errorLocalized("cmd.move.exception", ex.getMessage());
        }
        */
    }
    
    private static boolean validateInputPath(VVUser user, String path) {
        if (path.startsWith("/")) {
            user.errorLocalized("error.path_absolute");
            return false;
        }
        if (path.startsWith(".")) {
            user.errorLocalized("error.path_hidden");
            return false;
        }
        if (path.startsWith("#")) {
            user.errorLocalized("cmd.share.err.var");
            return false;
        }
        VVInventory inventory = user.getInventory();
        if (!inventory.contains(path)) {
            user.errorLocalized("cmd.share.err.missing", path);
            return true;
        }
        
        return true;
    }
    
}
