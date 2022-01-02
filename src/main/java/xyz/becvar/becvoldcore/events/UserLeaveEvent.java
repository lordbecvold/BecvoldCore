package xyz.becvar.becvoldcore.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserLeaveEvent implements Listener {

    @EventHandler
    public void onPlayerQuit (PlayerQuitEvent e) {
        e.setQuitMessage(null);
        e.setQuitMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "]:" + ChatColor.GRAY + e.getPlayer().getDisplayName() + " left the game");
    }
}
