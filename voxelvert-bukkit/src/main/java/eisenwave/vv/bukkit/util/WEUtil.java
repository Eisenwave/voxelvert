package eisenwave.vv.bukkit.util;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.Region;
import net.grian.torrens.object.Vertex3i;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import eisenwave.vv.object.BlockSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WEUtil {
    
    public final static WorldEditPlugin PLUGIN = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    
    private WEUtil() {}
    
    @Contract("_ -> !null")
    public static BlockSet blockSetOf(@NotNull Region region) {
        BlockSet result = new BlockSet();
        for (com.sk89q.worldedit.BlockVector vector : region)
            result.add(vectorOf(vector));
        
        return result;
    }
    
    @NotNull
    @Contract("_ -> !null")
    public static Vertex3i vectorOf(@NotNull com.sk89q.worldedit.BlockVector vector) {
        return new Vertex3i(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }
    
    @Nullable
    public static Selection selectionOf(Player player) {
        return PLUGIN.getSelection(player);
    }
    
}
