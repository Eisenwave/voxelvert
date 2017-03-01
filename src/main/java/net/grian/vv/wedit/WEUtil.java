package net.grian.vv.wedit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;
import net.grian.spatium.geo3.BlockVector;
import net.grian.vv.core.BlockSet;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import javax.annotation.Nonnull;

public final class WEUtil {
    
    public final static WorldEditPlugin PLUGIN = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    
    private WEUtil() {}
    
    @Contract("_ -> !null")
    public static BlockSet blockSetOf(@Nonnull Region region) {
        BlockSet result = new BlockSet();
        for (com.sk89q.worldedit.BlockVector vector : region)
            result.add(vectorOf(vector));
        
        return result;
    }
    
    @Contract("_ -> !null")
    public static BlockVector vectorOf(@Nonnull com.sk89q.worldedit.BlockVector vector) {
        return BlockVector.fromXYZ(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }
    
    public static Selection selectionOf(Player player) {
        return PLUGIN.getSelection(player);
    }
    
}
