package xyz.becvar.becvoldcore.command.sethome;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.configuration.file.*;
import xyz.becvar.becvoldcore.Main;
import xyz.becvar.becvoldcore.util.Logger;

public class CommandSetHome implements CommandExecutor {

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Location location = ((Player) sender).getLocation();
            final FileConfiguration configuration = ((Main) Main.getPlugin((Class) Main.class)).getConfig();
            configuration.set(((Player) sender).getUniqueId().toString(), (Object) location);
            ((Main) Main.getPlugin((Class) Main.class)).saveConfig();
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "]: " + ChatColor.GRAY + "Your home saved!");
        } else {
            Logger.INSTANCE.printErrorToConsole("You can use this command only form game as player");
        }
        return true;
    }
}
