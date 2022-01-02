package xyz.becvar.becvoldcore.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import xyz.becvar.becvoldcore.Main;

public class PunchTreeEvent implements Listener {

    Main main;


    @EventHandler
    public void onPunchTree(BlockBreakEvent e) {

        if (e.getBlock().getType().equals(Material.OAK_LOG) ||
                e.getBlock().getType().equals(Material.BIRCH_LOG) ||
                e.getBlock().getType().equals(Material.ACACIA_LOG) ||
                e.getBlock().getType().equals(Material.SPRUCE_LOG) ||
                e.getBlock().getType().equals(Material.JUNGLE_LOG) ||
                e.getBlock().getType().equals(Material.DARK_OAK_LOG) ||
                e.getBlock().getType().equals(Material.STRIPPED_OAK_LOG) ||
                e.getBlock().getType().equals(Material.STRIPPED_BIRCH_LOG) ||
                e.getBlock().getType().equals(Material.STRIPPED_ACACIA_LOG) ||
                e.getBlock().getType().equals(Material.STRIPPED_SPRUCE_LOG) ||
                e.getBlock().getType().equals(Material.STRIPPED_JUNGLE_LOG) ||
                e.getBlock().getType().equals(Material.STRIPPED_DARK_OAK_LOG) ||
                e.getBlock().getType().equals(Material.OAK_WOOD) ||
                e.getBlock().getType().equals(Material.BIRCH_WOOD) ||
                e.getBlock().getType().equals(Material.ACACIA_WOOD) ||
                e.getBlock().getType().equals(Material.SPRUCE_WOOD) ||
                e.getBlock().getType().equals(Material.JUNGLE_WOOD) ||
                e.getBlock().getType().equals(Material.DARK_OAK_WOOD) ||
                e.getBlock().getType().equals(Material.STRIPPED_OAK_WOOD) ||
                e.getBlock().getType().equals(Material.STRIPPED_BIRCH_WOOD) ||
                e.getBlock().getType().equals(Material.STRIPPED_ACACIA_WOOD) ||
                e.getBlock().getType().equals(Material.STRIPPED_SPRUCE_WOOD) ||
                e.getBlock().getType().equals(Material.STRIPPED_JUNGLE_WOOD) ||
                e.getBlock().getType().equals(Material.STRIPPED_DARK_OAK_WOOD)) {

            Player p = (Player) e.getPlayer();
            ItemStack droppedItem = new ItemStack(e.getBlock().getType());


            if (p.getInventory().getItemInMainHand().getType().equals(Material.WOODEN_AXE) ||
                    p.getInventory().getItemInMainHand().getType().equals(Material.STONE_AXE) ||
                    p.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE) ||
                    p.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_AXE) ||
                    p.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_AXE)) {

                Location broke = e.getBlock().getLocation();

                for (int j = 0; j < 256; j++) {
                    int Yt = (int) broke.getY() + j;

                    for (int i = -(1); i < (1 + 1); i++) {
                        int Xt = (int) broke.getX() + i;

                        for (int k = -(1); k < (1+ 1); k++) {
                            int Zt = (int) broke.getZ() + k;

                            Location l = new Location(p.getWorld(), Xt, Yt, Zt);

                            if (l != broke) {
                                if (l.getBlock().getType() == droppedItem.getType()) {
                                    l.getBlock().setType(Material.AIR);
                                    l.getWorld().dropItem(l, droppedItem);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
