package org.browsit.seaofsteves.boss.type;

import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import net.md_5.bungee.api.ChatMessageType;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.boss.PirateBoss;
import org.browsit.seaofsteves.depend.Dependencies;
import org.browsit.seaofsteves.settings.BossSettings;
import org.browsit.seaofsteves.util.IO;
import org.browsit.seaofsteves.util.MythicUtil;
import org.browsit.seaofsteves.util.NBTAPI;
import org.browsit.seaofsteves.util.TerraUtil;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.Random;

public class EnemyShip implements PirateBoss {
    private final SeaOfSteves plugin;
    private final BossSettings bosses;
    private final Dependencies depends;
    private final LivingEntity target;
    private final LivingEntity boss;
    private final Random random = new Random();

    public EnemyShip(final SeaOfSteves plugin, LivingEntity target) {
        this.plugin = plugin;
        this.bosses = plugin.getBossSettings();
        this.depends = plugin.getDependencies();
        this.target = target;
        this.boss = this.spawn();
    }

    private LivingEntity spawn() {
        final LivingEntity[] le = new LivingEntity[1];
        final Location loc = target.getLocation();
        final Vector standardInverseDirectionVec = loc.getDirection().normalize().multiply(-12);
        loc.add(standardInverseDirectionVec);
        loc.setY(63.0); // Above ocean surface
        if (depends.getMythicMobs() != null
                && !bosses.getKingBlazeMythic().equals("NAME_OF_MOB_TO_USE_INSTEAD")) {
            return MythicUtil.spawnMythicMobAtLocation(bosses.getKingBlazeMythic(), loc);
        } else {
            plugin.getFoliaLib().getScheduler().runAtEntityLater(target, () -> {
                if (depends.getMythicMobs() != null && plugin.getConfigSettings().isVotsEnabled()) {
                    final Vector votsInverseDirectionVec = loc.getDirection().normalize().multiply(-20);
                    loc.add(votsInverseDirectionVec);
                    loc.setY(60.0); // Below ocean surface
                    if (loc.getBlock().isLiquid()) {
                        // Spawn sound
                        final Optional<Skill> opt = depends.getMythicMobs().getSkillManager().getSkill("VOTSNPCGalleonShipDeathSound");
                        if (opt.isPresent()) {
                            final BukkitEntity bukkitEntity = new BukkitEntity(target);
                            final SkillCaster c = depends.getMythicMobs().getSkillManager().getCaster(bukkitEntity);
                            final SkillMetadataImpl meta = new SkillMetadataImpl(SkillTriggers.API, c, bukkitEntity);
                            opt.get().execute(meta);
                        }
                        //depends.getMythicMobs().getMobManager().spawnMob("VOTSNPCGalleon", loc);
                        le[0] = MythicUtil.spawnMythicMobAtLocation("VOTSNPCGalleon", loc);
                        if (target instanceof Player) {
                            IO.sendMessage((Player)target, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("enemyVesselChase"));
                        }
                    }
                } else {
                    final Entity boat = target.getWorld().spawnEntity(loc, EntityType.CHEST_BOAT);
                    NBTAPI.addNBT(boat, "sos_owner", "sos_zombie_captain");
                    boat.setInvulnerable(true);

                    final Zombie zombie = (Zombie) target.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
                    final double health = 20.0D;
                    zombie.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
                    zombie.setHealth(health);
                    zombie.setCustomName(bosses.getEnemyShipName());
                    zombie.setTarget(target);
                    zombie.getAttribute(Attribute.GENERIC_SCALE).setBaseValue(bosses.getEnemyShipScale());
                    boat.addPassenger(zombie);

                    final double speed = 0.75; // Blocks per second
                    plugin.getFoliaLib().getScheduler().runAtEntityTimer(boat, task -> {
                        if (!boat.isValid() || zombie.isDead() || target.isDead() || boat.getPassengers().isEmpty()
                                || boat.getLocation().getWorld() == null
                                || !boat.getLocation().getWorld().getUID().equals(target.getWorld().getUID())
                                || !WorldUtil.isOceanBiome(TerraUtil.getBukkitBiome(boat.getLocation()))
                                || !boat.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
                                || boat.getLocation().distance(target.getLocation()) > 75) {

                            if (target instanceof Player) {
                                if (!zombie.isDead()) {
                                    IO.sendMessage((Player) target, ChatMessageType.ACTION_BAR, ChatColor.YELLOW + IO.getLang("enemyGaveUp"));
                                }
                            }
                            zombie.remove();
                            boat.remove();
                            task.cancel();
                            return;
                        }
                        final Location preTele = boat.getLocation();
                        if (preTele.getY() < 62 || preTele.getY() > 64) {
                            boat.teleport(new Location(boat.getWorld(), preTele.getX(), 63.0, preTele.getZ()));
                        }

                        Vector boatPos = boat.getLocation().toVector();
                        Vector playerPos = target.getLocation().toVector();
                        Vector zombiePos = zombie.getLocation().toVector();

                        if (random.nextInt(8) == 1) {
                            Vector aim = playerPos.clone().subtract(zombiePos).normalize();
                            aim.setY(0);
                            Fireball fireball = zombie.launchProjectile(Fireball.class);
                            fireball.setVelocity(aim.multiply(2.0));
                            fireball.setYield(0.75f);
                        }

                        Vector direction = playerPos.subtract(boatPos);
                        direction.setY(0);

                        double distance = direction.length();
                        if (distance < 1.5) {
                            boat.setVelocity(new Vector(0, boat.getVelocity().getY(), 0));
                            return;
                        }

                        Vector velocity = direction.normalize().multiply(speed);
                        velocity.setY(boat.getVelocity().getY());
                        boat.setVelocity(velocity);

                        float yaw = (float) Math.toDegrees(
                                Math.atan2(-direction.getX(), direction.getZ())
                        );
                        boat.setRotation(yaw, 0);
                    }, 8L, 20L);
                    if (target instanceof Player) {
                        final Player player = (Player)target;
                        player.playSound(boat, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1f, 1f);
                        player.playSound(boat, Sound.ENTITY_HOSTILE_SPLASH, 1f, 1f);
                        IO.sendMessage(player, ChatMessageType.ACTION_BAR, ChatColor.RED + IO.getLang("enemyVesselChase"));
                    }
                    le[0] = zombie;
                }
            }, 40L);
        }
        return le[0];
    }

    @Override
    public LivingEntity getEntity() {
        return boss;
    }

    @Override
    public Location getSpawnLocation() {
        return null;
    }
}
