package eu.lukasbecvar.becvold_core;

/* Becvold core plugin desn
* Working on mincraft 1.16.4
* Features
* 1)SetHome /sethome /home
* 2)OnePlayer sleep
* 3)Tab Death counter
* 4)Timer util (Can set day time etc)
* 5)Coordinates in hud
*/

import eu.lukasbecvar.becvold_core.coordinateshud.Coordinates;
import eu.lukasbecvar.becvold_core.coordinateshud.CoordinatesTimer;
import eu.lukasbecvar.becvold_core.coordinateshud.Utils;
import eu.lukasbecvar.becvold_core.deathcounter.TabDeathCountCommandExecutor;
import eu.lukasbecvar.becvold_core.setHome.SetHomeEvents;
import eu.lukasbecvar.becvold_core.setHome.SetHomeUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.logging.Level;

public class Main extends JavaPlugin implements Listener {

    /*Death counter variabiles*/
    private static final int NAME_LENGTH_MAX = 16;
    private String playerNameFormat;
    private String deathCountFormat;
    private DeathCountPosition deathCountPosition;

    /*SetHome variabiles*/
    private File file = new File(getDataFolder(), "Homes.yml");
    public YamlConfiguration homes = YamlConfiguration.loadConfiguration(file);
    private FileConfiguration config = getConfig();
    private HashMap<Player, Integer> cooldownTimeHome;
    private HashMap<Player, BukkitRunnable> cooldownTaskHome;
    private HashMap<Player, Integer> cooldownTimeSetHome;
    private HashMap<Player, BukkitRunnable> cooldownTaskSetHome;
    private static final String prefixError = ChatColor.DARK_RED + "[" + ChatColor.RED + "*" + ChatColor.DARK_RED + "] " + ChatColor.GRAY;
    private SetHomeUtils utils = new SetHomeUtils(this);

    @Override
    public void onEnable() {
        //Hud cords
        this.saveDefaultConfig();
        Utils.readConfig(this.getConfig(), (Plugin)this);
        this.getCommand("coordinates").setExecutor((CommandExecutor)new Coordinates());
        CoordinatesTimer.run((Plugin)this);
        //End of Hud cords
        //Timer util
        this.getConfig().options().copyDefaults(true);
        final int evening = this.getConfig().getInt("times.evening");
        final int night = this.getConfig().getInt("times.night");
        final int day = this.getConfig().getInt("times.day");
        final int morning = this.getConfig().getInt("times.morning");
        System.out.print("[LongerTime] Morning:" + morning);
        System.out.print("[LongerTime] Day:" + day);
        System.out.print("[LongerTime] Evening:" + evening);
        System.out.print("[LongerTime] Night:" + night);
        this.saveConfig();
        new BukkitRunnable() {
            public void run() {
                for (final World ww : Bukkit.getWorlds()) {
                    long nachher = 1L;
                    final long vorher = ww.getTime();
                    if (vorher > 11615L && vorher < 13805L) {
                        final int Faktor = evening;
                        nachher = vorher - Faktor;
                        ww.setTime(nachher);
                    }
                    if (vorher > 13805L && vorher < 22550L) {
                        final int Faktor = night;
                        nachher = vorher - Faktor;
                        ww.setTime(nachher);
                    }
                    if (vorher > 22550L && vorher < 450L) {
                        final int Faktor = morning;
                        nachher = vorher - Faktor;
                        ww.setTime(nachher);
                    }
                    if (vorher < 450L) {
                        final int Faktor = morning;
                        nachher = vorher - Faktor;
                        ww.setTime(nachher);
                    }
                    if (vorher > 450L && vorher < 11615L) {
                        final int Faktor = day;
                        nachher = vorher - Faktor;
                        ww.setTime(nachher);
                    }
                }
            }
        }.runTaskTimer((Plugin)this, 20L, 20L);
        //End of Timer utils
        //DeathCounter util
        getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        saveDefaultConfig();
        if (!this.loadVerifyConfig()) {
            return;
        }
        new TabDeathCountCommandExecutor(this);
        for (Player p : getServer().getOnlinePlayers()) {
            if (!updateNameWithDeathCount(p)) {
                return;
            }
        }
        //End of DeathCounter util
        //Ram utils
        this.getCommand("ram").setExecutor((CommandExecutor)new CommandExecutor() {
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                command_logic(sender);
                return true;
            }
        });
        //End of ram util
        //SetHome util
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getConsoleSender().sendMessage("Becvold core plugin starting...");
        getCommand("sethome").setExecutor(this);
        getCommand("home").setExecutor(this);
        getServer().getPluginManager().registerEvents(new SetHomeEvents(this), this);
        config.options().copyDefaults(true);
        saveDefaultConfig();
        try {
            config.save(getDataFolder() + File.separator + "config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!file.exists()) {
            saveHomesFile();
        }
        cooldownTimeHome = new HashMap<>();
        cooldownTaskHome = new HashMap<>();
        cooldownTimeSetHome = new HashMap<>();
        cooldownTaskSetHome = new HashMap<>();
        getLogger().info("Enabled!");
        //End of setHome
    }

    @Override
    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            resetPlayerNameOriginal(p);
        }
        Bukkit.getConsoleSender().sendMessage("Becvold core plugin is stoped...");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("sethome")) {
            if (sender instanceof ConsoleCommandSender) {
                getLogger().log(Level.WARNING, "Only players can use this command.");
            } else if (sender instanceof Player) {
                Player player = (Player) sender;
                if (config.getBoolean("sethome-command-delay")) {
                    int coolDown = config.getInt("sethome-time-delay");
                    if (cooldownTimeSetHome.containsKey(player)) {
                        player.sendMessage(prefixError + "You must wait for " + ChatColor.RED + cooldownTimeSetHome.get(player) + ChatColor.GRAY + " seconds.");
                    } else {
                        setPlayerHome(player);
                        setCoolDownTimeSetHome(player, coolDown);
                    }
                } else {
                    setPlayerHome(player);
                }
            } else {
                sender.sendMessage(prefixError + "There was an error performing this command.");
            }
            return true;
        } else if (command.getName().equals("home")) {
            if (sender instanceof ConsoleCommandSender) {
                getLogger().log(Level.WARNING, "Only players can use this command.");
            } else if (sender instanceof Player) {
                Player player = (Player) sender;
                if (utils.homeIsNull(player)) {
                    player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "*" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "You must first use /sethome");
                } else {
                    if (config.getBoolean("home-command-delay")) {
                        int coolDown = config.getInt("home-time-delay");
                        if (cooldownTimeHome.containsKey(player)) {
                            player.sendMessage(prefixError + "You must wait for " + ChatColor.RED + cooldownTimeHome.get(player) + ChatColor.GRAY + " seconds.");
                        } else {
                            sendPlayerHome(player);
                            setCoolDownTimeHome(player, coolDown);
                        }
                    } else {
                        sendPlayerHome(player);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void saveHomesFile() {
        try {
            homes.save(file);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save homes file.\nHere is the stack trace:");
            e.printStackTrace();
        }
    }

    void sendPlayerHome(Player player) {
        utils.sendHome(player);
        if (config.getBoolean("play-warp-sound")) {
            player.playSound(utils.getHomeLocation(player), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        }
        String strFormatted = config.getString("teleport-message").replace("%player%", player.getDisplayName());
        if (config.getBoolean("show-teleport-message")) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', strFormatted));
        }
    }

    void setPlayerHome(Player player) {
        utils.setHome(player);
        if (config.getBoolean("show-sethome-message")) {
            String strFormatted = config.getString("sethome-message").replace("%player%", player.getDisplayName());
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', strFormatted));
        }
    }

    void setCoolDownTimeHome(Player player, int coolDown) {
        cooldownTimeHome.put(player, coolDown);
        cooldownTaskHome.put(player, new BukkitRunnable() {
            public void run() {
                cooldownTimeHome.put(player, cooldownTimeHome.get(player) - 1);
                if (cooldownTimeHome.get(player) == 0) {
                    cooldownTimeHome.remove(player);
                    cooldownTaskHome.remove(player);
                    cancel();
                }
            }
        });
        cooldownTaskHome.get(player).runTaskTimer(this, 20, 20);
    }

    void setCoolDownTimeSetHome(Player player, int coolDown) {
        cooldownTimeSetHome.put(player, coolDown);
        cooldownTaskSetHome.put(player, new BukkitRunnable() {
            public void run() {
                cooldownTimeSetHome.put(player, cooldownTimeSetHome.get(player) - 1);
                if (cooldownTimeSetHome.get(player) == 0) {
                    cooldownTimeSetHome.remove(player);
                    cooldownTaskSetHome.remove(player);
                    cancel();
                }
            }
        });
        cooldownTaskSetHome.get(player).runTaskTimer(this, 20, 20);
    }

    public void command_logic(final CommandSender sender) {
        final Runtime runtime = Runtime.getRuntime();
        System.gc();
        if (sender.isOp() || sender.hasPermission("ram.command")) {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Server" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "[Used / Total / Free]  " + ChatColor.DARK_GREEN + (runtime.totalMemory() - runtime.freeMemory()) / 1048576L + " MB / " + runtime.totalMemory() / 1048576L + " MB / " + runtime.freeMemory() / 1048576L + " MB");
        }
        else {
            sender.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Server" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "You Do Not Have Permission to Execute This Command");
        }
    }

    public void reloadConfig() {
        super.reloadConfig();
        if (!this.loadVerifyConfig()) {
            return;
        }
        for (final Player p : this.getServer().getOnlinePlayers()) {
            if (!this.updateNameWithDeathCount(p)) {
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        updateNameWithDeathCount(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        resetPlayerNameOriginal(event.getPlayer());
    }

    @EventHandler
    public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (event.getStatistic() == Statistic.DEATHS) {
            new BukkitRunnable() {
                public void run() {
                    updateNameWithDeathCount(event.getPlayer());
                }
            }.runTask((Plugin)this);
        }
    }

    private boolean loadVerifyConfig() {
        deathCountFormat = (String)getConfig().get("deathCount.format");
        playerNameFormat = (String)getConfig().get("playerName.format");
        String pos = (String)getConfig().get("deathCount.position");
        String lowerCase = pos.trim().toLowerCase();
        switch (lowerCase) {
            case "start": {
                this.deathCountPosition = DeathCountPosition.START;
                break;
            }
            case "end": {
                this.deathCountPosition = DeathCountPosition.END;
                break;
            }
            default: {
                this.getLogger().severe("config 'deathCount.position' must be \"before\" or \"after\"");
                this.getServer().getPluginManager().disablePlugin((Plugin)this);
                return false;
            }
        }
        return true;
    }

    public void updateNames() {
        for (final Player p : this.getServer().getOnlinePlayers()) {
            if (!this.updateNameWithDeathCount(p)) {
                return;
            }
        }
    }

    private boolean updateNameWithDeathCount(final Player player) {
        if (!player.hasPermission("tabdeathcount.showdeathcount")) {
            this.resetPlayerNameOriginal(player);
            return true;
        }
        final int deaths = player.getStatistic(Statistic.DEATHS);
        String deathCount;
        try {
            deathCount = String.format(this.deathCountFormat, deaths);
        }
        catch (IllegalFormatException e) {
            this.getLogger().severe("config 'deathCount.format' is invalid: " + e.getMessage());
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return false;
        }
        final int deathCountLength = deathCount.length();
        if (deathCountLength > 16) {
            this.getLogger().severe("config 'deathCount.format' produces a string that is too long");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return false;
        }
        String name = null;
        for (final MetadataValue v : player.getMetadata("originalPlayerListName")) {
            if (v.getOwningPlugin() == this) {
                name = v.asString();
            }
        }
        if (name == null) {
            name = player.getPlayerListName();
            player.setMetadata("originalPlayerListName", (MetadataValue)new FixedMetadataValue((Plugin)this, (Object)name));
        }
        try {
            name = String.format(this.playerNameFormat, name);
        }
        catch (IllegalFormatException e2) {
            this.getLogger().severe("config 'playerName.format' is invalid: " + e2.getMessage());
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return false;
        }
        final int nameLength = 16 - deathCountLength;
        if (name.length() > nameLength) {
            name = name.substring(0, nameLength);
        }
        switch (this.deathCountPosition) {
            case START: {
                name = deathCount + name;
                break;
            }
            case END: {
                name += deathCount;
                break;
            }
        }
        player.setPlayerListName(name);
        return true;
    }

    private void resetPlayerNameOriginal(final Player player) {
        String name = null;
        for (final MetadataValue v : player.getMetadata("originalPlayerListName")) {
            if (v.getOwningPlugin() == this) {
                name = v.asString();
            }
        }
        if (name != null) {
            player.setPlayerListName(name);
            player.removeMetadata("originalPlayerListName", (Plugin)this);
        }
    }

    private enum DeathCountPosition
    {
        START,
        END;
    }

    public int getNight() {
        final FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        return this.getConfig().getInt("times.night");
    }

    public int getmorning() {
        final FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        return this.getConfig().getInt("times.morning");
    }

    public int getday() {
        final FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        return this.getConfig().getInt("times.day");
    }

    public int getevening() {
        final FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);
        return this.getConfig().getInt("times.evening");
    }

    public int[] lesen() throws FileNotFoundException {
        final int[] rueckgabe = new int[10];
        final char[] test = new char[10];
        this.getConfig().options().copyDefaults(true);
        rueckgabe[1] = this.getConfig().getInt("morning");
        rueckgabe[2] = this.getConfig().getInt("day");
        rueckgabe[3] = this.getConfig().getInt("evening");
        rueckgabe[4] = this.getConfig().getInt("night");
        return rueckgabe;
    }
}