package xyz.becvar.becvoldcore.coordinateshud;

import org.bukkit.configuration.file.*;
import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import net.md_5.bungee.api.*;

public class CoordinatesUtils {

    private static List<String> Players;
    private static FileConfiguration cfg;
    private static Plugin plugin;
    private static String ticks;
    private static boolean defaulton;

    public static void readConfig(final FileConfiguration cfg, final Plugin plugin) {
        CoordinatesUtils.cfg = cfg;
        CoordinatesUtils.plugin = plugin;
        CoordinatesUtils.ticks = cfg.getString("ticks");
        CoordinatesUtils.defaulton = (boolean)cfg.get("default-on");
        CoordinatesUtils.Players = (List<String>)cfg.getStringList("Players");
    }

    public static int getTicks() {
        return Integer.parseInt(CoordinatesUtils.ticks);
    }

    public static boolean getDefaultOn() {
        return CoordinatesUtils.defaulton;
    }

    private static List<String> getPlayers() {
        if (CoordinatesUtils.Players == null) {
            return new ArrayList<String>();
        }
        return CoordinatesUtils.Players;
    }

    public static boolean checkPlayerList(final Player player) {
        return getPlayers().contains(player.getName());
    }

    public static void savePlayer(final Player player) {
        if (!CoordinatesUtils.Players.contains(player.getName())) {
            CoordinatesUtils.Players.add(player.getName());
        }
        CoordinatesUtils.cfg.set("Players", (Object) CoordinatesUtils.Players);
        CoordinatesUtils.plugin.saveConfig();
    }

    public static void removePlayer(final Player player) {
        CoordinatesUtils.Players.remove(player.getName());
        CoordinatesUtils.cfg.set("Players", (Object) CoordinatesUtils.Players);
        CoordinatesUtils.plugin.saveConfig();
    }

    public static void sendMsg(final CommandSender player, final String msg) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
    }

    public static String rpGetPlayerDirection(final Player playerSelf) {
        String dir = "";
        float y = playerSelf.getLocation().getYaw();
        if (y < 0.0f) {
            y += 360.0f;
        }
        y %= 360.0f;
        final int i = (int)((y + 8.0f) / 22.5);
        if (i == 4) {
            dir = "W";
        }
        else if (i == 5) {
            dir = "WNW";
        }
        else if (i == 6) {
            dir = "NW";
        }
        else if (i == 7) {
            dir = "NNW";
        }
        else if (i == 8) {
            dir = "N";
        }
        else if (i == 9) {
            dir = "NNE";
        }
        else if (i == 10) {
            dir = "NE";
        }
        else if (i == 11) {
            dir = "ENE";
        }
        else if (i == 12) {
            dir = "E";
        }
        else if (i == 13) {
            dir = "ESE";
        }
        else if (i == 14) {
            dir = "SE";
        }
        else if (i == 15) {
            dir = "SSE";
        }
        else if (i == 0) {
            dir = "S";
        }
        else if (i == 1) {
            dir = "SSW";
        }
        else if (i == 2) {
            dir = "SW";
        }
        else if (i == 3) {
            dir = "WSW";
        }
        else {
            dir = "S";
        }
        return dir;
    }
}

