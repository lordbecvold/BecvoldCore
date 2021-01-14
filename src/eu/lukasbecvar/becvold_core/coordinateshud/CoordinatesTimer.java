package eu.lukasbecvar.becvold_core.coordinateshud;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.*;
import java.util.*;
import org.bukkit.*;

public class CoordinatesTimer
{
    public static void run(final Plugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final Player p : plugin.getServer().getOnlinePlayers()) {
                    if ((Utils.checkPlayerList(p) && !Utils.getDefaultOn()) || (!Utils.checkPlayerList(p) && Utils.getDefaultOn())) {
                        final Location l = p.getLocation();
                        final long gameTime = p.getWorld().getTime();
                        long hours = gameTime / 1000L + 6L;
                        hours %= 24L;
                        if (hours == 24L) {
                            hours = 0L;
                        }
                        final long minutes = gameTime % 1000L * 60L / 1000L;
                        String mm = "0" + minutes;
                        mm = mm.substring(mm.length() - 2, mm.length());
                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(ChatColor.GOLD + "XYZ: " + ChatColor.WHITE + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + ChatColor.GOLD + String.format("%-10s", Utils.rpGetPlayerDirection(p)) + hours + ":" + mm).create());
                    }
                }
            }
        }, 0L, (long)Utils.getTicks());
    }
}

