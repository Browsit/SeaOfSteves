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
