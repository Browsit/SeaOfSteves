/*
 * Copyright (c) Browsit, LLC
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.browsit.seaofsteves.boss.lavablock;

import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EffectTask implements Runnable {
    private final ConfigSettings config;
    private final Entity vehicle;
    private final Display tephra;
    private final Location target;

    public EffectTask(final SeaOfSteves plugin, final Entity vehicle, final Display tephra, final Location target) {
        this.config = plugin.getConfigSettings();
        this.vehicle = vehicle;
        this.tephra = tephra;
        this.target = target;
    }

    public void run() {
        if (!tephra.isValid()) {
            return;
        }
        final World world = vehicle.getWorld();
        if (vehicle.getLocation().getY() <= this.target.getY() || vehicle.isOnGround()) {
            // Tephra has landed
            world.spawnParticle(Particle.EXPLOSION_EMITTER, tephra.getLocation(), 10);
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getWorld() == tephra.getWorld() && player.getLocation().distance(tephra.getLocation()) < 4.0D/*(double)Meteors.instance.config.getInt("range")*/) {
                    Vector velocity = player.getLocation().toVector().subtract(tephra.getLocation().toVector());
                    player.setVelocity(new Vector(velocity.getX(), 1.2, velocity.getZ()));
                }

            });
            final Location l = new Location(tephra.getLocation().getWorld(), tephra.getLocation().getX(), tephra.getLocation().getY() - 1.0, this.tephra.getLocation().getZ());
            if (l.getWorld() == null) {
                return;
            }
            final List<FallingBlock> blocks = new ArrayList<>(Arrays.asList(
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l).getType().createBlockData()),
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l.add(1.0, 0.0, 0.0)).getType().createBlockData()),
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l.add(0.0, 0.0, 1.0)).getType().createBlockData()),
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l.add(-1.0, 0.0, 0.0)).getType().createBlockData()),
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l.add(-1.0, 0.0, 0.0)).getType().createBlockData()),
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l.add(0.0, 0.0, -1.0)).getType().createBlockData()),
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l.add(0.0, 0.0, -1.0)).getType().createBlockData()),
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l.add(1.0, 0.0, 0.0)).getType().createBlockData()),
                    this.tephra.getWorld().spawnFallingBlock(tephra.getLocation(), l.getWorld().getBlockAt(l.add(1.0, 0.0, 0.0)).getType().createBlockData())));
            blocks.get(0).setVelocity(new Vector(-0.3, 0.5, -0.6));
            blocks.get(1).setVelocity(new Vector(-0.5, 0.4, 0.2));
            blocks.get(2).setVelocity(new Vector(0.5, 0.3, 0.5));
            blocks.get(3).setVelocity(new Vector(0.3, 0.4, 0.4));
            blocks.get(4).setVelocity(new Vector(0.2, 0.3, -0.5));
            blocks.get(5).setVelocity(new Vector(0.4, 0.3, -0.3));
            blocks.get(6).setVelocity(new Vector(-0.6, 0.4, -0.5));
            blocks.get(7).setVelocity(new Vector(-0.4, 0.5, -0.2));
            blocks.get(8).setVelocity(new Vector(0.2, 0.6, -0.1));
            if (config.getEruptionCancelDrop()) {
                blocks.get(0).setCancelDrop(true);
                blocks.get(1).setCancelDrop(true);
                blocks.get(2).setCancelDrop(true);
                blocks.get(3).setCancelDrop(true);
                blocks.get(4).setCancelDrop(true);
                blocks.get(5).setCancelDrop(true);
                blocks.get(6).setCancelDrop(true);
                blocks.get(7).setCancelDrop(true);
                blocks.get(8).setCancelDrop(true);
            }
            blocks.forEach((b) -> {
                b.setHurtEntities(true);
                b.setDropItem(false);
            });
            world.playSound(tephra.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);
            tephra.remove();
            vehicle.remove();
            return;
        }

        world.spawnParticle(Particle.LARGE_SMOKE, tephra.getLocation().add(2.0, -1.0, 4.0), 1);
        world.playSound(tephra.getLocation(), Sound.ENTITY_HORSE_BREATHE, 2.0f, 1.0f);
    }
}
