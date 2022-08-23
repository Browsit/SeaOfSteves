package org.browsit.seaofsteves.api.event;

import org.browsit.seaofsteves.player.Pirate;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PirateOpenTreasureEvent extends PlayerEvent {
    private final Block clickedBlock;
    private final String lootName;

    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a pirate opens treasure
     *
     * @param pirate The lucky pirate
     * @param clickedBlock The treasure opened
     * @param lootName The associated PhatLoot name
     */
    public PirateOpenTreasureEvent(Pirate pirate, Block clickedBlock, String lootName) {
        super(pirate.getPlayer());

        this.clickedBlock = clickedBlock;
        this.lootName = lootName;
    }

    public Block getClickedBlock() {
        return clickedBlock;
    }

    public String getLootName() {
        return lootName;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
