package xyz.becvar.becvoldcore.command.sethome;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.configuration.file.*;
import xyz.becvar.becvoldcore.Main;
import xyz.becvar.becvoldcore.util.Logger;

public class CommandHome implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            final FileConfiguration configuration = ((Main)Main.getPlugin((Class)Main.class)).getConfig();
            if (configuration.contains(((Player)sender).getUniqueId().toString())) {
                ((Player)sender).teleport(configuration.getLocation(((Player)sender).getUniqueId().toString()));
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "]: " + ChatColor.GREEN + "Teleporting to home location...");
            } else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "]: " + ChatColor.RED + "Your home location not saved!");
            }
        } else {
            Logger.INSTANCE.printErrorToConsole("You can use this command only form game as player");
        }
        return true;
    }
}

