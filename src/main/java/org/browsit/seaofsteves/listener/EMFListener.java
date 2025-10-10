package org.browsit.seaofsteves.listener;

import com.oheers.fish.api.EMFFishSellEvent;
import org.browsit.seaofsteves.SeaOfSteves;
import org.browsit.seaofsteves.settings.ConfigSettings;
import org.browsit.seaofsteves.util.PlayerUtil;
import org.browsit.seaofsteves.util.WorldUtil;
import org.bukkit.event.EventHandler;

public class EMFListener {
    private final SeaOfSteves plugin;
    private final ConfigSettings config;

    public EMFListener(final SeaOfSteves plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigSettings();
    }

    @EventHandler
    public void onEMFFishSell(final EMFFishSellEvent event) {
        if (!WorldUtil.isAllowedWorld(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (config.isFishmongerEnabled()) {
            event.setCancelled(true);
            PlayerUtil.sellFish(event.getPlayer(), null);
        }
    }
}
