package xyz.becvar.becvoldcore.deathcounter;

import java.util.*;
import org.bukkit.command.*;
import org.bukkit.*;
import xyz.becvar.becvoldcore.Main;

public final class TabDeathCountCommandExecutor implements CommandExecutor {

    public static final String MAIN_COMMAND = "tabDeathCount";
    private final Map<String, Executor> COMMANDS;
    private Main plugin;

    public TabDeathCountCommandExecutor(final Main plugin) {
        this.COMMANDS = new TreeMap<String, Executor>(String.CASE_INSENSITIVE_ORDER);
        this.plugin = plugin;
        plugin.getCommand("tabDeathCount").setExecutor((CommandExecutor)this);
        this.COMMANDS.put("reloadConfig", new ExecutorReloadConfig());
        this.COMMANDS.put("updateDeathCounts", new ExecutorUpdateDeathCounts());
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (!this.checkPermission(sender, "command")) {
            return true;
        }
        final Executor executor = this.COMMANDS.get(args[0]);
        return executor != null && executor.execute(sender, args);
    }

    private boolean checkPermission(final CommandSender sender, String permission) {
        permission = "tabdeathcount." + permission;
        if (sender.hasPermission(permission)) {
            return true;
        }
        sender.sendMessage(ChatColor.RED + "You do not have permission to" + " perform this command.");
        return false;
    }

    private class ExecutorReloadConfig implements Executor
    {
        @Override
        public boolean execute(final CommandSender sender, final String[] args) {
            if (!TabDeathCountCommandExecutor.this.checkPermission(sender, "command.reloadconfig")) {
                return true;
            }
            TabDeathCountCommandExecutor.this.plugin.reloadConfig();
            sender.sendMessage("Reloaded configuration.");
            return true;
        }
    }

    private class ExecutorUpdateDeathCounts implements Executor
    {
        @Override
        public boolean execute(final CommandSender sender, final String[] args) {
            if (!TabDeathCountCommandExecutor.this.checkPermission(sender, "command.updatedeathcounts")) {
                return true;
            }
            TabDeathCountCommandExecutor.this.plugin.updateNames();
            return true;
        }
    }

    private interface Executor
    {
        boolean execute(final CommandSender p0, final String[] p1);
    }
}
