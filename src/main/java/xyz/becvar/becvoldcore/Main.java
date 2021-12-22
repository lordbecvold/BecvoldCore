package xyz.becvar.becvoldcore;

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
import xyz.becvar.becvoldcore.coordinateshud.Coordinates;
import xyz.becvar.becvoldcore.coordinateshud.CoordinatesTimer;
import xyz.becvar.becvoldcore.coordinateshud.Utils;
import xyz.becvar.becvoldcore.deathcounter.TabDeathCountCommandExecutor;
import xyz.becvar.becvoldcore.setHome.SetHomeEvents;
import xyz.becvar.becvoldcore.setHome.SetHomeUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Main extends JavaPlugin implements Listener {

    /*Death counter variabiles*/
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

        //Save config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        //Hud cords
        Utils.readConfig(this.getConfig(), (Plugin)this);
        this.getCommand("coordinates").setExecutor((CommandExecutor)new Coordinates());
        CoordinatesTimer.run((Plugin)this);
        //End of Hud cords

        //Timer util
        final int evening = this.getConfig().getInt("times.evening");
        final int night = this.getConfig().getInt("times.night");
        final int day = this.getConfig().getInt("times.day");
        final int morning = this.getConfig().getInt("times.morning");
        System.out.print("[LongerTime] Morning:" + morning);
        System.out.print("[LongerTime] Day:" + day);
        System.out.print("[LongerTime] Evening:" + evening);
        System.out.print("[LongerTime] Night:" + night);
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
        this.getCommand("core").setExecutor((CommandExecutor)new CommandExecutor() {
            public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                command_core(sender);
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
        if (!file.exists()) {
            saveHomesFile();
        }
        cooldownTimeHome = new HashMap<>();
        cooldownTaskHome = new HashMap<>();
        cooldownTimeSetHome = new HashMap<>();
        cooldownTaskSetHome = new HashMap<>();
        getLogger().info("Enabled!");
        //End of setHome

        super.onEnable();
    }

    @Override
    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            resetPlayerNameOriginal(p);
        }
        Bukkit.getConsoleSender().sendMessage("Becvold core plugin is stoped...");
        super.onDisable();
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


        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Sorry, You must be a player to execute this.");
            return false;
        }
        final Player p = (Player)sender;
        if (!command.getName().equalsIgnoreCase("ui")) {
            return false;
        }
        if (!p.hasPermission("ui.view")) {
            p.sendMessage(ChatColor.RED + "Sorry, you dont have permission to execute this.");
            return false;
        }
        if (args.length != 1) {
            p.sendMessage(ChatColor.RED + "Usage: /ui <player>");
            return false;
        }
        final OfflinePlayer targetOff = Bukkit.getOfflinePlayer(args[0]);
        if (!targetOff.hasPlayedBefore()) {
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "User, " + ChatColor.GREEN + p.getName() + ChatColor.RED + " tried to lookup a user that hasnt joined: " + ChatColor.GREEN + args[0] + ChatColor.GRAY + ".");
            p.sendMessage(ChatColor.DARK_GRAY + "---------------- " + ChatColor.GREEN + "User Info" + ChatColor.DARK_GRAY + " ----------------");
            p.sendMessage(ChatColor.GRAY + "Sorry the player: " + ChatColor.GREEN + args[0] + ChatColor.GRAY + " has not joined before.");
            p.sendMessage(ChatColor.DARK_GRAY + "-----------------------------------------");
            return false;
        }
        if (!targetOff.isOnline()) {
            this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "User, " + ChatColor.GREEN + p.getName() + ChatColor.RED + " Looked up the offline user: " + ChatColor.GREEN + targetOff.getName() + ChatColor.RED + ".");
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
        final Player targetP = this.getServer().getPlayer(args[0]);
        this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "User, " + ChatColor.GREEN + p.getName() + ChatColor.RED + " Looked up the online user: " + ChatColor.GREEN + getName(targetP) + ChatColor.RED + ".");
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

    public void command_core(final CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_GRAY + "-------------------------------------------------");
        sender.sendMessage(ChatColor.DARK_GRAY + "----------------BECVOLD CORE INFO----------------");
        sender.sendMessage(ChatColor.GRAY + "Features");
        sender.sendMessage(ChatColor.GRAY + "1) /sethome /home");
        sender.sendMessage(ChatColor.GRAY + "2) OnePlayer sleep");
        sender.sendMessage(ChatColor.GRAY + "3) Tab Death counter");
        sender.sendMessage(ChatColor.GRAY + "4) Coordinates in hud");
        sender.sendMessage(ChatColor.GRAY + "5) Ram monitor /ram");
        sender.sendMessage(ChatColor.GRAY + "6) Timer util in config");
        sender.sendMessage(ChatColor.GRAY + "7) User info /ui + [player name]");
        sender.sendMessage(ChatColor.DARK_GRAY + "-------------------------------------------------");

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
        event.setJoinMessage(null);
        event.setJoinMessage(ChatColor.GRAY + event.getPlayer().getDisplayName() + " joined the game");
        updateNameWithDeathCount(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage(null);
        event.setQuitMessage(ChatColor.GRAY + event.getPlayer().getDisplayName() + " left the game");
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

    public static String getOnline(final Player p) {
        if (p.isOnline()) {
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