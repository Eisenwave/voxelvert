package eisenwave.vv.bukkit.user;

import eisenwave.inv.util.LegacyUtil;
import eisenwave.torrens.object.Vertex3i;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WorldEditEmergencyListener implements Listener {
    
    // for 1.12 compatibility, Material#WOODEN_AXE can not be directly referenced
    // because it is called Material#WOOD_AXE
    // LegacyUtil always uses either the old Material or the new material on 1.13+
    private final static Material WOODEN_AXE = LegacyUtil.getByMinecraftKey13("wooden_axe").getMaterial();
    
    private final BukkitVoxelVert voxelVert;
    
    public WorldEditEmergencyListener(BukkitVoxelVert voxelVert) {
        this.voxelVert = voxelVert;
    }
    
    private void setPos(Player player, boolean pos2, int x, int y, int z) {
        PlayerVVUser user = (PlayerVVUser) voxelVert.getUser(player);
        if (pos2)
            user.setPos2(new Vertex3i(x, y, z));
        else
            user.setPos1(new Vertex3i(x, y, z));
        
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        switch (event.getMessage()) {
            case "//pos1": {
                Player player = event.getPlayer();
                Location loc = player.getLocation();
                PlayerVVUser user = (PlayerVVUser) voxelVert.getUser(player);
                Vertex3i pos = new Vertex3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                user.setPos1(pos);
                user.print("Set position 1 to " + pos);
                event.setCancelled(true);
                break;
            }
        
            case "//pos2": {
                Player player = event.getPlayer();
                Location loc = player.getLocation();
                PlayerVVUser user = (PlayerVVUser) voxelVert.getUser(player);
                Vertex3i pos = new Vertex3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                user.setPos2(pos);
                user.print("Set position 2 to " + pos);
                event.setCancelled(true);
                break;
            }
        }
    }
    
    //@EventHandler
    public void onBlockClick(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getItemOnCursor();
        Block block = e.getBlock();
        
        if (item != null && item.getType() == WOODEN_AXE) {
            PlayerVVUser user = (PlayerVVUser) voxelVert.getUser(player);
            Vertex3i pos = new Vertex3i(block.getX(), block.getY(), block.getZ());
            user.setPos1(pos);
            user.print("Set position 1 to " + pos);
            //UserManager.getInstance().setFirstPosition(player, vector);
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        //System.out.println(WOODEN_AXE);
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Block block = e.getClickedBlock();
        Action action = e.getAction();
        //System.out.println(action + " " + block + " " + item.getType() + " " + WOODEN_AXE);
        if ((action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK) &&
            item != null && item.getType() == WOODEN_AXE) {
        
            PlayerVVUser user = (PlayerVVUser) voxelVert.getUser(player);
            Vertex3i pos = new Vertex3i(block.getX(), block.getY(), block.getZ());
        
            if (action == Action.LEFT_CLICK_BLOCK) {
                if (user.setPos2(pos))
                    user.print("Set position 2 to " + pos);
            }
            else {
                if (user.setPos1(pos))
                    user.print("Set position 1 to " + pos);
            }
            e.setCancelled(true);
        }
    }
    
}
