package xyz.becvar.becvoldcore.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public enum Logger {

    INSTANCE;

    public void consoleLogRed(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + " " + msg);
    }

    public void consoleLogGreen(String msg) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " " + msg);
    }

    public void logSpacerToConsole() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "==============================================================");
    }

    public void printErrorToConsole(String errorMSG) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "BecvoldCore" + ChatColor.GRAY + "]" + ChatColor.RED + ": " + ChatColor.RED + errorMSG);
    }
}
