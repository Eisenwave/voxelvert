package eisenwave.vv.bukkit.cmd;

import eisenwave.inv.util.LegacyUtil;
import eisenwave.inv.util.MinecraftObject;
import eisenwave.torrens.schematic.BlockKey;
import eisenwave.torrens.schematic.legacy.LegacyBlockKey;
import eisenwave.torrens.schematic.legacy.MicroLegacyUtil;
import eisenwave.vv.bukkit.VoxelVertPlugin;
import eisenwave.vv.ui.user.VVUser;
import eisenwave.vv.ui.util.Sets;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
    
    @SuppressWarnings("deprecation")
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
                    user.error("Unknown id %s", key);
                    return true;
                }
                transparent.add(obj.getMaterial());
            }
            transparent.add(Material.AIR);
        }
        
        Player player = (Player) sender;
        Block block = player.getTargetBlock(transparent, 6);
        
        if (LegacyUtil.isApi13())
            user.print(block.getBlockData().getAsString());
        else {
            int id = block.getType().getId();
            byte data = block.getData();
            LegacyBlockKey legacyKey = new LegacyBlockKey(id, data);
            BlockKey key = MicroLegacyUtil.getByLegacyKey(legacyKey);
            user.print("%s = %s", legacyKey, key);
        }
        return true;
    }
    
}
