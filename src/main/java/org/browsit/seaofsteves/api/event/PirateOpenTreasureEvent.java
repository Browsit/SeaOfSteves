/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
