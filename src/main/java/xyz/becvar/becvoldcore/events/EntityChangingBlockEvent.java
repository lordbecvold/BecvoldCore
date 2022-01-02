package xyz.becvar.becvoldcore.events;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangingBlockEvent implements Listener {

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if(event.getEntityType() == EntityType.ENDERMAN) {
            event.setCancelled(true);
        }
    }
}
