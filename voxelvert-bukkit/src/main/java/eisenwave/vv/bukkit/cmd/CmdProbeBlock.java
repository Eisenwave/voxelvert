package eisenwave.vv.bukkit.cmd;

import eisenwave.inv.util.LegacyUtil;
import eisenwave.inv.util.MinecraftObject;
import eisenwave.torrens.schematic.BlockKey;
import eisenwave.torrens.schematic.legacy.*;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.rp.BlockColor;
import eisenwave.vv.rp.BlockColorTable;
import eisenwave.vv.ui.fmtvert.FormatverterFactory;
import eisenwave.vv.ui.user.VVUser;
import eisenwave.vv.ui.util.Sets;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOError;
import java.io.IOException;
import java.util.*;

public class CmdProbeBlock extends VoxelVertCommand {
    
    //private final static String
    //    USAGE = CommandUtil.chatColors("&cUsage: /probe-block");
    
    public CmdProbeBlock(@NotNull VoxelVertPlugin plugin) {
        super(plugin);
    }
    
    @Override
    public String getName() {
        return "probe-block";
    }
    
    @Override
    public String getUsage() {
        return "[transparent ids...]";
    }
    
    @SuppressWarnings({"deprecation"})
    @Override
    public boolean onCommand(CommandSender sender, VVUser user, String[] args) {
        if (!(sender instanceof Player)) {
            user.errorLocalized("error.not_a_player");
            return true;
        }
        
        Set<Material> transparent;
        if (args.length == 0) {
            transparent = Sets.ofArray(Material.AIR);
        }
        else {
            transparent = new HashSet<>(args.length + 1);
            for (String arg : args) {
                BlockKey key = BlockKey.parse(arg);
                MinecraftObject obj = LegacyUtil.getByMinecraftKey13(key.getId());
                if (obj == null) {
                    user.errorLocalized("cmd.probe_block.err.unknown_key", key);
                    return true;
                }
                transparent.add(obj.getMaterial());
            }
            transparent.add(Material.AIR);
        }
        
        Player player = (Player) sender;
        Block block = player.getTargetBlock(transparent, 6);
        BlockColorTable defaultTable;
        try {
            defaultTable = FormatverterFactory.defaultBCT();
        } catch (IOException e) {
            throw new IOError(e);
        }
        
        if (LegacyUtil.isApi13()) {
            String blockDataStr = block.getBlockData().getAsString();
            BlockKey key = BlockKey.parse(blockDataStr);
            user.print(key.toString());
            BlockColor color = defaultTable.get(key);
            String rgb = color == null?
                ChatColor.RED + "no color" + ChatColor.RESET :
                "#" + Integer.toUnsignedString(color.getRGB(), 16).toUpperCase();
            user.printLocalized("cmd.probe_block.13", key, rgb);
        }
        else {
            int id = block.getType().getId();
            byte data = block.getData();
            LegacyBlockKey legacyKey = new LegacyBlockKey(id, data);
            BlockKey key = MicroLegacyUtil.getByLegacyKey(legacyKey);
            assert key != null;
            BlockColor color = defaultTable.get(key);
            String rgb = color == null?
                ChatColor.RED + "no color" + ChatColor.RESET :
                "#" + Integer.toUnsignedString(color.getRGB(), 16).toUpperCase();
            user.printLocalized("cmd.probe_block.12", legacyKey, key, rgb);
        }
        return true;
    }
    
}
