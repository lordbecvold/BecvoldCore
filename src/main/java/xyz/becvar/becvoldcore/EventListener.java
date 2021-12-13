package xyz.becvar.becvoldcore;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import static org.bukkit.Bukkit.getServer;

public class EventListener implements Listener {

    public boolean day() {
        Server server = getServer();
        long time = server.getWorld("world").getTime();

        return time < 12300 || time > 23850;
    }

    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent e) {
        if (!day()) {
            Player p = e.getPlayer();
            getServer().getWorld("world").setTime(1000);
            getServer().broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Server" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + p.getDisplayName() + ChatColor.GRAY + " has slept!");
        }
    }
}