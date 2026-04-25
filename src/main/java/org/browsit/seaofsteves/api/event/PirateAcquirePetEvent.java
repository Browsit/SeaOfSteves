package org.browsit.seaofsteves.api.event;

import org.browsit.seaofsteves.player.Pirate;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PirateAcquirePetEvent extends PlayerEvent {
    private final Entity tamer;
    private final ItemStack cost;

    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a pirate gets a new pet
     *
     * @param pirate The now-richer pirate
     * @param tamer The satisfied tamer
     * @param cost The item cost
     */
    public PirateAcquirePetEvent(Pirate pirate, @Nullable Entity tamer, ItemStack cost) {
        super(pirate.getPlayer());

        this.tamer = tamer;
        this.cost = cost;
    }

    @Nullable
    public Entity getTamer() {
        return tamer;
    }

    public ItemStack getCost() {
        return cost;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
