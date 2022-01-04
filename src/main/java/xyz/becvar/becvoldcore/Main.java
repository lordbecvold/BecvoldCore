package xyz.becvar.becvoldcore;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.becvar.becvoldcore.command.CommandHelp;
import xyz.becvar.becvoldcore.command.CommandRam;
import xyz.becvar.becvoldcore.command.CommandUserInfo;
import xyz.becvar.becvoldcore.command.sethome.CommandHome;
import xyz.becvar.becvoldcore.command.sethome.CommandSetHome;
import xyz.becvar.becvoldcore.coordinateshud.Coordinates;
import xyz.becvar.becvoldcore.coordinateshud.CoordinatesTimer;
import xyz.becvar.becvoldcore.coordinateshud.CoordinatesUtils;
import xyz.becvar.becvoldcore.events.*;
import xyz.becvar.becvoldcore.util.Logger;

public class Main extends JavaPlugin {

    public Main main;


    @Override
    public void onEnable() {

        //Init plugin
        main = this;

        Logger.INSTANCE.logSpacerToConsole();
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: plugin starting...");



        //Create config file
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();


        //Register basic events
        Bukkit.getServer().getPluginManager().registerEvents(new BedListener(this), this);
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: One player sleep initiated...");
        Bukkit.getServer().getPluginManager().registerEvents(new UserJoinEvent(), this);
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: User join event initiated...");
        Bukkit.getServer().getPluginManager().registerEvents(new UserLeaveEvent(), this);
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: User leave event initiated...");
        Bukkit.getServer().getPluginManager().registerEvents(new EntityChangingBlockEvent(), this);
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: Anti mob grief initiated...");
        Bukkit.getServer().getPluginManager().registerEvents(new PunchTreeEvent(), this);
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: Treecapitator initiated...");
        Bukkit.getServer().getPluginManager().registerEvents(new ServerPingEvent(), this);
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: server ping event initiated...");
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerSendMessageEvent(), this);
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: player message event initiated...");
        //End of events register


        //Register basic commands
        this.getCommand("help").setExecutor(new CommandHelp());
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: Help command registred...");
        this.getCommand("ram").setExecutor(new CommandRam());
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: ram command registred...");
        this.getCommand("ui").setExecutor(new CommandUserInfo());
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: ui command registred...");
        //End of command register


        //Init sethome commands
        this.getCommand("home").setExecutor((CommandExecutor)new CommandHome());
        this.getCommand("sethome").setExecutor((CommandExecutor)new CommandSetHome());
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: Sethome initiate...");
        //End of sethome



        //Hud cords
        CoordinatesUtils.readConfig(this.getConfig(), (Plugin)this);
        this.getCommand("coordinates").setExecutor((CommandExecutor)new Coordinates());
        CoordinatesTimer.run((Plugin)this);
        Logger.INSTANCE.consoleLogGreen("BecvoldCore: coordinates hud initiate...");
        //End of Hud cords




        //Timer util
        final int evening = this.getConfig().getInt("times.evening");
        final int night = this.getConfig().getInt("times.night");
        final int day = this.getConfig().getInt("times.day");
        final int morning = this.getConfig().getInt("times.morning");
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
        //End of timer util



        Logger.INSTANCE.logSpacerToConsole();
    }

    @Override
    public void onDisable() {
        Logger.INSTANCE.logSpacerToConsole();
        Logger.INSTANCE.consoleLogRed("BecvoldCore: plugin disabling...");
        Logger.INSTANCE.logSpacerToConsole();
    }

}
