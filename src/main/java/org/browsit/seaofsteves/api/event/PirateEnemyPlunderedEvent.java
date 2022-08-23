package org.browsit.seaofsteves.api.event;

import org.browsit.seaofsteves.player.Pirate;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PirateEnemyPlunderedEvent extends PlayerEvent {
    private final Pirate plundered;

    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a pirate plunders an enemy pirate
     *
     * @param pirate The responsible pirate
     * @param plundered The enemy pirate
     */
    public PirateEnemyPlunderedEvent(Pirate pirate, Pirate plundered) {
        super(pirate.getPlayer());

        this.plundered = plundered;
    }

    public Pirate getPlundered() {
        return plundered;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
