package xyz.becvar.becvoldcore.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.becvar.becvoldcore.util.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class CommandUptime implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        System.gc();
        if (sender.isOp() || sender.hasPermission("uptime.command")) {

            //Init MXBean
            RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();

            //Get values
            long millis = mxBean.getUptime() % 1000;
            long seconds = (mxBean.getUptime() / 1000) % 60;
            long minutes = (mxBean.getUptime() / (1000 * 60)) % 60;
            long hours = (mxBean.getUptime() / (1000 * 60 * 60)) % 24;
            long days = (mxBean.getUptime() / 1000 / 60 / 60 / 24);


            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "]: " + ChatColor.GRAY + "Uptime: Days: " + days + ", hours: " + hours + ", minutes: " + minutes + ", seconds: " + seconds);
        }
        else {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "BecvoldCore" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "You Do Not Have Permission to Execute This Command");
        }
        return true;
    }
}