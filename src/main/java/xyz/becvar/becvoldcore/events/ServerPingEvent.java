package xyz.becvar.becvoldcore.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerPingEvent implements Listener {

    @EventHandler
    public void countDown(ServerListPingEvent event){
        event.setMotd("§8» §6§lBECVAR§7.§6§lXYZ §8× §7Private survival server");
        event.setMaxPlayers(20);
    }
}
