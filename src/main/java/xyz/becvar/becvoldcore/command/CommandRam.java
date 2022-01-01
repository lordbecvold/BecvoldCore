package xyz.becvar.becvoldcore.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.becvar.becvoldcore.util.Logger;

public class CommandRam implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            final Runtime runtime = Runtime.getRuntime();
            System.gc();
            if (sender.isOp() || sender.hasPermission("ram.command")) {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "[Used / Total / Free]  " + ChatColor.DARK_GREEN + (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + " MB / " + runtime.totalMemory() / 1048576L + " MB / " + runtime.freeMemory() / 1048576L + " MB");
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "You Do Not Have Permission to Execute This Command");
            }
        } else {
            Logger.INSTANCE.printErrorToConsole("You can use this command only form game as player");
        }
        return true;
    }
}
