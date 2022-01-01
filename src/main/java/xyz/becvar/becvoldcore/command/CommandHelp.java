package xyz.becvar.becvoldcore.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.becvar.becvoldcore.util.Logger;

public class CommandHelp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.DARK_GRAY + "====================================================");
            sender.sendMessage(ChatColor.GRAY + "BecvoldCore plugin help");
            sender.sendMessage(ChatColor.GRAY + "SetHome: /sethome, /home");
            sender.sendMessage(ChatColor.GRAY + "CoordinatesHud: /coordinates toggle");
            sender.sendMessage(ChatColor.GRAY + "Ram usage monitor: /ram");
            sender.sendMessage(ChatColor.GRAY + "User info: /ui + player name");
            sender.sendMessage(ChatColor.DARK_GRAY + "====================================================");
        } else {
            Logger.INSTANCE.printErrorToConsole("You can use this command only form game as player");
        }
        return true;
    }
}
