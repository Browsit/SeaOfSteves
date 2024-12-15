/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.player;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Pirate implements Comparable<Pirate> {
    private final UUID uniqueId;
    private int goldEarned;
    private int fishCaught;
    private int enemiesPlundered;

    public Pirate(final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public int compareTo(final Pirate pirate) {
        return uniqueId.compareTo(pirate.getUniqueId());
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    public String getName() {
        return getPlayer().getName();
    }

    public int getGoldEarned() {
        return goldEarned;
    }

    public void setGoldEarned(final int goldEarned) {
        this.goldEarned = goldEarned;
    }

    public int getFishCaught() {
        return fishCaught;
    }

    public void setFishCaught(final int fishCaught) {
        this.fishCaught = fishCaught;
    }

    public int getEnemiesPlundered() {
        return enemiesPlundered;
    }

    public void setEnemiesPlundered(final int enemiesPlundered) {
        this.enemiesPlundered = enemiesPlundered;
    }

    public int getTotalDaysAtSea() {
        // Note that PLAY_ONE_MINUTE is actually number of ticks played
        return getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60 / 60 / 24;
    }

    public int getTotalNauticalMilesSailed() {
        return (int) Math.floor(getPlayer().getStatistic(Statistic.BOAT_ONE_CM) * 185200);
    }
}
