package xyz.becvar.becvoldcore.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerSendMessageEvent implements Listener {

    @EventHandler
    public void onSend(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + e.getPlayer().getDisplayName() + ChatColor.DARK_GRAY + "]: " + ChatColor.WHITE + e.getMessage());
    }
}
