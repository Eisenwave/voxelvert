package eisenwave.vv.bukkit.user;

import com.sk89q.worldedit.bukkit.selections.Selection;
import eisenwave.vv.object.Language;
import net.grian.torrens.object.BoundingBox6i;
import net.grian.torrens.schematic.BlockStructure;
import org.bukkit.Location;
import org.bukkit.World;
import eisenwave.vv.bukkit.util.CommandUtil;
import eisenwave.vv.ui.VoxelVert;
import eisenwave.vv.ui.user.VVUser;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.logging.Logger;

public class PlayerVVUser implements VVUser {
    
    private final BukkitVoxelVert vv;
    private final Player player;
    private final PlayerVVInventory inv;
    
    private final String msgFormat, errFormat;
    
    public PlayerVVUser(@NotNull BukkitVoxelVert vv, @NotNull Player player) {
        this.vv = Objects.requireNonNull(vv);
        this.player = Objects.requireNonNull(player);
        
        File userDir = new File(vv.getDirectory(), "users");
        File dir = new File(userDir, player.getUniqueId().toString());
        this.inv = new PlayerVVInventory(this, dir);
    
        Language lang = vv.getLanguage();
        this.msgFormat = lang.get("user.msg");
        this.errFormat = lang.get("user.err");
    }
    
    // IMPLEMENTED METHODS
    
    @NotNull
    @Override
    public VoxelVert getVoxelVert() {
        return vv;
    }
    
    @Override
    public String getName() {
        return player.getName();
    }
    
    @Override
    public boolean acceptsUpdates() {
        return false;
    }
    
    @Override
    public void print(String msg) {
        player.sendMessage( CommandUtil.chatColors(String.format(msgFormat, msg)) );
    }
    
    @Override
    public void error(String err) {
        player.sendMessage( CommandUtil.chatColors(String.format(errFormat, err)) );
    }
    
    @Override
    public Logger getLogger() {
        return null;
    }
    
    @Override
    public PlayerVVInventory getInventory() {
        return inv;
    }
    
    // PROVIDED METHODS
    
    @NotNull
    public Player getPlayer() {
        return player;
    }
    
    /**
     * Returns a block array containing all of the player's blocks or <code>null</code> if the player has no block
     * selection.
     *
     * @return the blocks in the player selection
     * @see #getBlockSelection()
     */
    @Nullable
    public BlockStructure getBlocks() {
        World world = player.getWorld();
        BoundingBox6i box = getBlockSelection();
        if (box == null)
            return null;
        
        return vv.getBlockScanner().getBlocks(world, box);
    }
    
    /**
     * Returns the player block selection or <code>null</code> if they have none.
     *
     * @return the player block selection
     * @see #getBlocks()
     */
    @Nullable
    public BoundingBox6i getBlockSelection() {
        Selection selection = vv.getWorldEdit().getSelection(player);
        if (selection == null)
            return null;
        
        Location min = selection.getMinimumPoint(), max = selection.getMaximumPoint();
    
        return new BoundingBox6i(
            min.getBlockX(),
            min.getBlockY(),
            min.getBlockZ(),
            max.getBlockX(),
            max.getBlockY(),
            max.getBlockZ());
    }
    
}
