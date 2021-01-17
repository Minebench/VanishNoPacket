package org.kitteh.vanish.hooks.plugins;

import com.earth2me.essentials.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPlugin;
import org.kitteh.vanish.hooks.Hook;

public final class EssentialsHook extends Hook {
    private final VanishPlugin plugin;
    private IEssentials essentials;

    public EssentialsHook(@NonNull VanishPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void onDisable() {
        for (final Player player : this.plugin.getServer().getOnlinePlayers()) {
            if ((player != null) && this.plugin.getManager().isVanished(player)) {
                this.onUnvanish(player);
            }
        }
        this.essentials = null;
    }

    @Override
    public void onEnable() {
        final Plugin grab = this.plugin.getServer().getPluginManager().getPlugin("Essentials");
        if (grab != null) {
            this.essentials = ((IEssentials) grab);
            this.plugin.getLogger().info("Now hooking into Essentials");
        } else {
            this.plugin.getLogger().info("You wanted Essentials support. I could not find Essentials.");
            this.essentials = null;
        }
    }

    @Override
    public void onUnvanish(@NonNull Player player) {
        if (player.hasPermission("vanish.hooks.essentials.hide")) {
            this.setHidden(player, false);
        }
    }

    @Override
    public void onVanish(@NonNull Player player) {
        if (player.hasPermission("vanish.hooks.essentials.hide")) {
            this.setHidden(player, true);
        }
    }

    private void setHidden(@NonNull Player player, boolean hide) {
        if (this.essentials != null) {
            this.essentials.getUser(player).setHidden(hide);
        }
    }
}