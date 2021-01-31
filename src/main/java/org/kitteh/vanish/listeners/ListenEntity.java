/*
 * VanishNoPacket
 * Copyright (C) 2011-2021 Matt Baxter
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.kitteh.vanish.listeners;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import com.destroystokyo.paper.event.entity.ProjectileCollideEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;
import org.spigotmc.event.entity.EntityMountEvent;

public final class ListenEntity implements Listener {
    private final VanishPlugin plugin;

    public ListenEntity(@NonNull VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(@NonNull EntityDamageEvent event) {
        final Entity smacked = event.getEntity();
        if (this.plugin.getManager().getBats().contains(smacked.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
        if (smacked instanceof Player) {
            final Player player = (Player) smacked;
            if (this.plugin.getManager().isVanished(player) && VanishPerms.blockIncomingDamage(player)) {
                event.setCancelled(true);
            }
        }
        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
            final Entity damager = ev.getDamager();
            Player player = null;
            if (damager instanceof Player) {
                player = (Player) damager;
            } else if (damager instanceof Projectile) {
                final Projectile projectile = (Projectile) damager;
                if ((projectile.getShooter() != null) && (projectile.getShooter() instanceof Player)) {
                    player = (Player) projectile.getShooter();
                }
            }
            if ((player != null) && this.plugin.getManager().isVanished(player) && VanishPerms.blockOutgoingDamage(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityMount(@NonNull EntityMountEvent event) {
        if ((event.getMount() instanceof Player) && this.plugin.getManager().isVanished((Player) event.getMount()) && VanishPerms.canNotPickUp(((Player) event.getMount()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityTarget(@NonNull EntityTargetEvent event) {
        if ((event.getTarget() instanceof Player) && this.plugin.getManager().isVanished((Player) event.getTarget()) && VanishPerms.canNotFollow((Player) event.getTarget())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onProjectileCollide(@NonNull ProjectileCollideEvent event) {
        Entity with = event.getCollidedWith();
        if (!(with instanceof Player)) {
            return;
        }

        Player player = (Player) with;
        if (this.plugin.getManager().isVanished(player) && VanishPerms.canNotFollow(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(@NonNull VehicleDestroyEvent event) {
        final Entity entity = event.getAttacker();
        if ((entity instanceof Player) && this.plugin.getManager().isVanished((Player) event.getAttacker())) {
            if (VanishPerms.canNotInteract((Player) entity)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleEntityCollision(@NonNull VehicleEntityCollisionEvent event) {
        if ((event.getEntity() instanceof Player) && this.plugin.getManager().isVanished((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPhantomPreSpawn(@NonNull PhantomPreSpawnEvent event) {
        Entity spawner = event.getSpawningEntity();
        if (!(spawner instanceof Player)) {
            return;
        }

        Player player = (Player) spawner;
        if (this.plugin.getManager().isVanished(player) && VanishPerms.canNotInteract(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityBlockForm(@NonNull EntityBlockFormEvent event) {
        if ((event.getEntity() instanceof Player) && this.plugin.getManager().isVanished((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
