package eisenwave.vv.bukkit.async;

import eisenwave.torrens.object.BoundingBox6i;
import eisenwave.torrens.schematic.legacy.ArrayBlockStructure;
import eisenwave.torrens.schematic.legacy.LegacyBlockStructure;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A block scanner which caches the entire block selection of a player.
 */
public class CachedBlockScanner implements BlockScanner {
    
    private final Plugin plugin;
    
    public CachedBlockScanner(Plugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public synchronized LegacyBlockStructure getBlocks(@NotNull World world, @NotNull BoundingBox6i box) {
        if (Bukkit.isPrimaryThread()) {
            return new WorldBlockStructure(world, box);
        }
    
        //Bukkit.broadcastMessage(box.toString());
        Callable<LegacyBlockStructure> callable = () -> {
            LegacyBlockStructure worldStruct = new WorldBlockStructure(world, box);
            LegacyBlockStructure resultStruct = new ArrayBlockStructure(box.getSizeX(), box.getSizeY(), box.getSizeZ());
    
            worldStruct.forEachPos(pos -> resultStruct.setBlock(pos, worldStruct.getBlock(pos)));
            
            return resultStruct;
        };
    
        Future<LegacyBlockStructure> future = Bukkit.getScheduler().callSyncMethod(plugin, callable);
        //noinspection EmptyCatchBlock
        try {
            return future.get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            throw new AssertionError(e);
        }
    }
    
}
