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

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.wrapper.task.WrappedTask;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Silverfish;
import org.bukkit.inventory.ItemStack;
import org.joml.Matrix4f;

public class Tephra {
    private static FoliaLib foliaLib;
    private final ConfigSettings config;
    private final Location target;
    private final Entity vehicle;
    private final Display tephra;
    private final WrappedTask effects;

    public Tephra(final SeaOfSteves plugin, final Location target) {
        foliaLib = plugin.getFoliaLib();
        this.config = plugin.getConfigSettings();
        this.target = target;
        this.vehicle = this.initVehicle();
        this.tephra = this.initTephra();
        effects = foliaLib.getScheduler().runAtEntityTimer(tephra, new EffectTask(plugin, vehicle, tephra, target), 4L, 4L);
    }

    private Entity initVehicle() {
        if (target.getWorld() == null) {
            return null;
        }
        final Silverfish vehicle = target.getWorld().spawn(target.clone().add(0,
                config.getEruptionStartHeight(), 0), Silverfish.class);
        vehicle.setInvisible(true);
        vehicle.setInvulnerable(true);
        return vehicle;
    }

    private Display initTephra() {
        if (target.getWorld() == null || vehicle == null) {
            return null;
        }
        final ItemDisplay display = target.getWorld().spawn(target.clone().add(0,
                config.getEruptionStartHeight(), 0), ItemDisplay.class);
        display.setPersistent(false);
        display.setItemStack(new ItemStack(Material.valueOf(config.getEruptionMaterial())));
        display.setInvulnerable(true);
        display.setBrightness(new Display.Brightness(15, 15));
        beginRotation(display);
        vehicle.addPassenger(display);
        return display;
    }

    private void beginRotation(final Display display) {
        final int duration = 3 * 20; // duration of half a revolution (5 * 20 ticks = 5 seconds)
        final Matrix4f mat = new Matrix4f().scale(3F);
        foliaLib.getScheduler().runAtEntityTimer(display, task -> {
            if (!display.isValid()) {
                effects.cancel();
                task.cancel();
                return;
            }
            display.setTransformationMatrix(mat.rotateY(((float) Math.toRadians(180)) + 0.1F));
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(duration);
        }, 2L, duration);
    }

    public Location getTarget() {
        return target;
    }

    public Display getTephra() {
        return tephra;
    }

    public Entity getVehicle() {
        return vehicle;
    }

    public WrappedTask getEffectTask() {
        return effects;
    }
}
