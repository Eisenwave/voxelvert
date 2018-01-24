package eisenwave.vv.bukkit.cmd;

import com.google.common.net.MediaType;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.bukkit.http.DownloadManager;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.bukkit.util.HttpUtil;
import eisenwave.vv.ui.user.VVInventory;
import eisenwave.vv.ui.user.VVUser;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;

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
        return "player <source> <target> OR worldedit <source>";
    }
    
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
        if (args.length < 2
            || !args[0].equals("player") && !args[0].equals("worldedit") && !args[0].equals("download"))
            return false;
        
        String source = args[1];
        if (source.startsWith("/")) {
            user.errorLocalized("error.path_absolute");
            return true;
        }
        if (source.startsWith(".")) {
            user.errorLocalized("error.path_hidden");
            return true;
        }
        if (source.startsWith("#")) {
            user.errorLocalized("cmd.share.err.var");
            return true;
        }
        
        VVInventory inventory = user.getInventory();
        
        if (!inventory.contains(source)) {
            user.errorLocalized("cmd.share.err.missing", source);
            return true;
        }
        
        switch (args[0]) {
            case "player":
                if (args.length < 3) return false;
                user.printLocalized("cmd.share.wip");
                return true;
            
            case "download": {
                if (!plugin.isHttpServerStarted()) {
                    user.printLocalized("cmd.share.err.no_download_server");
                    return true;
                }
                File file = user.getInventory().getFile(source);
                if (!file.isFile()) {
                    user.errorLocalized("cmd.share.err.not_a_file");
                    return true;
                }
                String ext = CommandUtil.extensionOf(source);
                MediaType mediaType = ext == null? MediaType.APPLICATION_BINARY : HttpUtil.getMediaType(ext, false);
                
                DownloadManager downloadManager = plugin.getDownloadManager();
                String id = downloadManager.put(mediaType, file);
                String url = downloadManager.urlOfId(id);
                user.printLocalized("cmd.share.download", url);
                return true;
            }
            
            default:
                assert args[1].equals("worldedit");
                if (!source.endsWith(".schem") && !source.endsWith(".schematic")) {
                    user.errorLocalized("cmd.share.err.we_no_schematic", source);
                    return true;
                }
                user.printLocalized("cmd.share.wip");
                return true;
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
    
}
