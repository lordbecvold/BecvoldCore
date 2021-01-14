package eu.lukasbecvar.becvold_core.coordinateshud;

import org.bukkit.configuration.file.*;
import org.bukkit.plugin.*;
import java.util.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import net.md_5.bungee.api.*;

public class Utils
{
    private static List<String> Players;
    private static FileConfiguration cfg;
    private static Plugin plugin;
    private static String ticks;
    private static boolean defaulton;

    public static void readConfig(final FileConfiguration cfg, final Plugin plugin) {
        Utils.cfg = cfg;
        Utils.plugin = plugin;
        Utils.ticks = cfg.getString("ticks");
        Utils.defaulton = (boolean)cfg.get("default-on");
        Utils.Players = (List<String>)cfg.getStringList("Players");
    }

    public static int getTicks() {
        return Integer.parseInt(Utils.ticks);
    }

    public static boolean getDefaultOn() {
        return Utils.defaulton;
    }

    private static List<String> getPlayers() {
        if (Utils.Players == null) {
            return new ArrayList<String>();
        }
        return Utils.Players;
    }

    public static boolean checkPlayerList(final Player player) {
        return getPlayers().contains(player.getName());
    }

    public static void savePlayer(final Player player) {
        if (!Utils.Players.contains(player.getName())) {
            Utils.Players.add(player.getName());
        }
        Utils.cfg.set("Players", (Object)Utils.Players);
        Utils.plugin.saveConfig();
    }

    public static void removePlayer(final Player player) {
        Utils.Players.remove(player.getName());
        Utils.cfg.set("Players", (Object)Utils.Players);
        Utils.plugin.saveConfig();
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

