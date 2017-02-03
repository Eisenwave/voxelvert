package net.grian.vv.plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import net.grian.vv.core.BlockSet;
import net.grian.vv.wedit.WEUtil;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class BukkitVVUser extends AbstractVVUser {
    
    private final static File
        PLUGIN_DIR = VVPlugin.getInstance().getDataFolder(),
        USER_DIR = new File(PLUGIN_DIR, "users");
    
    private final Player player;
    private final File dir;
    
    public BukkitVVUser(Player player) {
        this.player = player;
        this.dir = new File(USER_DIR, player.getUniqueId().toString());
    }
    
    public Player getPlayer() {
        return player;
    }
    
    @Override
    public File getFileDirectory() throws IOException {
        if (dir.exists()) {
            if (!dir.isDirectory()) throw new IOException(dir+" exists but is not directory");
            else return dir;
        }
        else if (!dir.mkdirs()) {
            throw new IOException("failed to make directory "+dir);
        }
        return dir;
    }
    
    @Override
    public String getName() {
        return player.getName();
    }
    
    @Override
    public World getWorld() {
        return player.getWorld();
    }
    
    //DATA
    
    @SuppressWarnings("deprecation")
    public BlockSet getSelection() {
        WorldEdit we = WorldEdit.getInstance();
        try {
            Region region = we.getSession(player.getName()).getRegion();
            return WEUtil.blockSetOf(region);
        } catch (IncompleteRegionException e) {
            return null;
        }
    }
    
}
