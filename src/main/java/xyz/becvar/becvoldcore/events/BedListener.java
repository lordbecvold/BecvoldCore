package xyz.becvar.becvoldcore.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.Plugin;
import xyz.becvar.becvoldcore.Main;

public class BedListener implements Listener {

    Main main;

    public BedListener(Main instance) {
        instance.getServer().getPluginManager().registerEvents(this, (Plugin)instance);
        this.main = instance;
    }

    @EventHandler
    public void onPlayerViolationCommand(PlayerBedEnterEvent e) {
        Bukkit.getServer().getScheduler().runTaskLater(main, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (e.getPlayer().isSleeping()) {
                    e.getPlayer().getWorld().setTime(0L);
                    e.getPlayer().getWorld().setStorm(false);
                    e.getPlayer().getWorld().setThundering(false);
                }
            }
        }, 100L);
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "]:" + " " + ChatColor.GRAY + e.getPlayer().getDisplayName() + " sleeping!");
    }
}
