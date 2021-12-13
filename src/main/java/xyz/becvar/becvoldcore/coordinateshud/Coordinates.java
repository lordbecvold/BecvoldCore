package xyz.becvar.becvoldcore.coordinateshud;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;
import java.util.*;

public class Coordinates implements CommandExecutor, TabCompleter {

    private static final List<String> COMMANDS;

    static {
        COMMANDS = Arrays.asList("toggle");
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        final Player player = (Player)sender;
        if (args.length == 0 || !args[0].toLowerCase().equals("toggle")) {
            player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "*" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "Usage: /coordinates toggle");

            return true;
        }
        if (!Utils.checkPlayerList(player)) {
            Utils.savePlayer(player);
        }
        else {
            Utils.removePlayer(player);
        }
        player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "*" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "Coordinates HUD is now " + (((Utils.checkPlayerList(player) && !Utils.getDefaultOn()) || (!Utils.checkPlayerList(player) && Utils.getDefaultOn())) ? "enabled" : "disabled") + ".");
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        return (List<String>)((args.length > 0) ? ((List)StringUtil.copyPartialMatches(args[0], (Iterable)Coordinates.COMMANDS, (Collection)new ArrayList())) : null);
    }
}

