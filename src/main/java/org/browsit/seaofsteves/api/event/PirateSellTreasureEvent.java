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
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PirateSellTreasureEvent extends PlayerEvent {
    private final Entity merchant;
    private final int totalValue;

    private static final HandlerList handlers = new HandlerList();

    /**
     * Called when a pirate sells treasure
     *
     * @param pirate The now-richer pirate
     * @param merchant The satisfied merchant
     * @param totalValue The total point or monetary value
     */
    public PirateSellTreasureEvent(Pirate pirate, Entity merchant, int totalValue) {
        super(pirate.getPlayer());

        this.merchant = merchant;
        this.totalValue = totalValue;
    }

    public Entity getMerchant() {
        return merchant;
    }

    public int getTotalValue() {
        return totalValue;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
