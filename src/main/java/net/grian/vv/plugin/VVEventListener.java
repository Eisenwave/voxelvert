package net.grian.vv.plugin;

import net.grian.spatium.geo.BlockVector;
import net.grian.vv.cache.UserManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VVEventListener implements Listener {

    @EventHandler
    public void onBlockClick(BlockBreakEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getItemOnCursor();

        if (item != null && item.getType().equals(Material.DIAMOND_AXE)) {
            Block block = e.getBlock();
            BlockVector vector = BlockVector.fromXYZ(block.getX(), block.getY(), block.getZ());
            UserManager.getInstance().setFirstPosition(player, vector);
        }
    }

    @EventHandler
    public void onBlockPunch(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getItemOnCursor();

        if (item != null && item.getType().equals(Material.DIAMOND_AXE)) {
            Block block = e.getClickedBlock();
            BlockVector vector = BlockVector.fromXYZ(block.getX(), block.getY(), block.getZ());
            UserManager.getInstance().setSecondPosition(player, vector);
        }
    }

}
