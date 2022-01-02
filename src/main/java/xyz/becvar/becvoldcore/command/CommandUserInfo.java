package xyz.becvar.becvoldcore.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.becvar.becvoldcore.util.Logger;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.regex.Pattern;

public class CommandUserInfo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final Player p = (Player)sender;
        if (sender instanceof Player) {
            if (!p.hasPermission("ui.view")) {
                p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Sorry, you dont have permission to execute this.");
                return false;
            }
            if (args.length != 1) {
                p.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + "Usage: /ui <player>");
                return false;
            }
            final OfflinePlayer targetOff = Bukkit.getOfflinePlayer(args[0]);
            if (!targetOff.hasPlayedBefore()) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "User, " + ChatColor.GREEN + p.getName() + ChatColor.RED + " tried to lookup a user that hasnt joined: " + ChatColor.GREEN + args[0] + ChatColor.GRAY + ".");
                p.sendMessage(ChatColor.DARK_GRAY + "---------------- " + ChatColor.GREEN + "User Info" + ChatColor.DARK_GRAY + " ----------------");
                p.sendMessage(ChatColor.GRAY + "Sorry the player: " + ChatColor.GREEN + args[0] + ChatColor.GRAY + " has not joined before.");
                p.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------------");
                return false;
            }
            if (!targetOff.isOnline()) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "User, " + ChatColor.GREEN + p.getName() + ChatColor.RED + " Looked up the offline user: " + ChatColor.GREEN + targetOff.getName() + ChatColor.RED + ".");
                p.sendMessage(ChatColor.DARK_GRAY + "---------------- " + ChatColor.GREEN + "User Info" + ChatColor.DARK_GRAY + " ----------------");
                p.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + targetOff.getName());
                p.sendMessage(ChatColor.GRAY + "UUID: " + ChatColor.GREEN + targetOff.getUniqueId());
                p.sendMessage(ChatColor.GRAY + "Online: " + ChatColor.GREEN + "False");
                p.sendMessage(ChatColor.GRAY + "OP: " + ChatColor.GREEN + getOffOp(targetOff));
                p.sendMessage(ChatColor.GRAY + "Banned: " + ChatColor.GREEN + getOffBanned(targetOff));
                p.sendMessage(ChatColor.GRAY + "Whitelisted: " + ChatColor.GREEN + getOffWhitelist(targetOff));
                p.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------------");
                return false;
            }
            final Player targetP = Bukkit.getServer().getPlayer(args[0]);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "User, " + ChatColor.GREEN + p.getName() + ChatColor.RED + " Looked up the online user: " + ChatColor.GREEN + getName(targetP) + ChatColor.RED + ".");
            p.sendMessage(ChatColor.DARK_GRAY + "---------------- " + ChatColor.GREEN + "User Info" + ChatColor.DARK_GRAY + " ----------------");
            p.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + getName(targetP));
            p.sendMessage(ChatColor.GRAY + "UUID: " + ChatColor.GREEN + getUUID(targetP));
            p.sendMessage(ChatColor.GRAY + "Online: " + ChatColor.GREEN + "True");
            p.sendMessage(ChatColor.GRAY + "OP: " + ChatColor.GREEN + getOp(targetP));
            p.sendMessage(ChatColor.GRAY + "Banned: " + ChatColor.GREEN + getBanned(targetP));
            p.sendMessage(ChatColor.GRAY + "Whitelisted: " + ChatColor.GREEN + getWhitelist(targetP));
            p.sendMessage(ChatColor.GRAY + "Gamemode: " + ChatColor.GREEN + getGamemode(targetP));
            p.sendMessage(ChatColor.GRAY + "Location: " + ChatColor.GREEN + "{" + getLocX(targetP) + ", " + getLocY(targetP) + ", " + getLocZ(targetP) + "} in world '" + getWorld(targetP) + "'");
            p.sendMessage(ChatColor.GRAY + "Health: " + ChatColor.GREEN + getHealth(targetP));
            p.sendMessage(ChatColor.GRAY + "Hunger: " + ChatColor.GREEN + getHunger(targetP));
            p.sendMessage(ChatColor.GRAY + "IP: " + ChatColor.GREEN + getIP(targetP));
            p.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------------");
        } else {
            Logger.INSTANCE.printErrorToConsole("You can use this command only form game as player");
        }
        return true;
    }


    public static String getServerVersion() {
        final Pattern brand = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");
        String version = null;
        final String pkg = Bukkit.getServer().getClass().getPackage().getName();
        String version2 = pkg.substring(pkg.lastIndexOf(46) + 1);
        if (!brand.matcher(version2).matches()) {
            version2 = "";
        }
        version = version2;
        return "".equals(version) ? "" : (String.valueOf(version) + ".");
    }

    public static String getIP(final Player p) {
        return p.getAddress().getAddress().getHostAddress();
    }

    public static UUID getUUID(final Player p) {
        return p.getUniqueId();
    }

    public static String getName(final Player p) {
        return p.getName();
    }

    public static String getGamemode(final Player p) {
        if (p.getGameMode() == GameMode.ADVENTURE) {
            return "Adventure";
        }
        if (p.getGameMode() == GameMode.CREATIVE) {
            return "Creative";
        }
        if (p.getGameMode() == GameMode.SPECTATOR) {
            return "Spectator";
        }
        if (p.getGameMode() == GameMode.SURVIVAL) {
            return "Survival";
        }
        return "Error";
    }

    public static String getOp(final Player p) {
        if (p.isOp()) {
            return "True";
        }
        return "False";
    }

    public static String getWhitelist(final Player p) {
        if (p.isWhitelisted()) {
            return "True";
        }
        return "False";
    }

    public static int getHunger(final Player p) {
        return p.getFoodLevel();
    }

    public static String getBanned(final Player p) {
        if (p.isBanned()) {
            return "True";
        }
        return "False";
    }

    public static int getHealth(final Player p) {
        return (int)p.getHealth();
    }

    public static String getWorld(final Player p) {
        return p.getWorld().getName();
    }

    public static int getLocX(final Player p) {
        return (int)p.getLocation().getX();
    }

    public static int getLocY(final Player p) {
        return (int)p.getLocation().getY();
    }

    public static int getLocZ(final Player p) {
        return (int)p.getLocation().getZ();
    }

    public static String getOffOp(final OfflinePlayer p) {
        if (p.isOp()) {
            return "True";
        }
        return "False";
    }

    public static String getOffWhitelist(final OfflinePlayer p) {
        if (p.isWhitelisted()) {
            return "True";
        }
        return "False";
    }

    public static String getOffBanned(final OfflinePlayer p) {
        if (p.isBanned()) {
            return "True";
        }
        return "False";
    }
}
