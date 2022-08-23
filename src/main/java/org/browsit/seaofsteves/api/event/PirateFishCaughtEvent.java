package org.browsit.seaofsteves.api.event;

import org.browsit.seaofsteves.player.Pirate;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PirateFishCaughtEvent extends PlayerEvent {
    private final Entity caught;

    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a pirate has a successful catch
     *
     * @param pirate The fishing pirate
     * @param caught The entity caught (which may not be fish)
     */
    public PirateFishCaughtEvent(Pirate pirate, Entity caught) {
        super(pirate.getPlayer());

        this.caught = caught;
    }

    public Entity getCaught() {
        return caught;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
