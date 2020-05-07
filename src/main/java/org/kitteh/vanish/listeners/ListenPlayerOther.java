package org.kitteh.vanish.listeners;

import com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.kitteh.vanish.Settings;
import org.kitteh.vanish.VanishPerms;
import org.kitteh.vanish.VanishPlugin;

public final class ListenPlayerOther implements Listener {
    private final VanishPlugin plugin;

    public ListenPlayerOther(VanishPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player) event.getEntity();
            if (this.plugin.getManager().isVanished(player) && VanishPerms.canNotHunger(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (!this.plugin.chestFakeInUse(player.getName()) && !player.isSneaking() && (event.getAction() == Action.RIGHT_CLICK_BLOCK) && this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canReadChestsSilently(event.getPlayer())) {
            final Block block = event.getClickedBlock();
            Inventory inventory = null;
            final BlockState blockState = block.getState();
            boolean fake = false;
            switch (block.getType()) {
                case TRAPPED_CHEST:
                case CHEST:
                    final Chest chest = (Chest) blockState;
                    inventory = this.plugin.getServer().createInventory(player, chest.getInventory().getSize());
                    inventory.setContents(chest.getInventory().getContents());
                    fake = true;
                    break;
                case ENDER_CHEST:
                    if (this.plugin.getServer().getPluginManager().isPluginEnabled("EnderChestPlus") && VanishPerms.canNotInteract(player)) {
                        event.setCancelled(true);
                        return;
                    }
                    inventory = player.getEnderChest();
                    break;
            }
            if (inventory == null && blockState instanceof InventoryHolder) {
                inventory = ((InventoryHolder) blockState).getInventory();
            }
            if (inventory != null) {
                event.setCancelled(true);
                if (fake) {
                    this.plugin.chestFakeOpen(player.getName());
                    player.sendMessage(ChatColor.AQUA + "[VNP] Opening chest silently. Can not edit.");
                }
                player.openInventory(inventory);
                return;
            }
        }
        if (this.plugin.getManager().isVanished(player) && VanishPerms.canNotInteract(player)) {
            event.setCancelled(true);
            return;
        }
        if ((event.getAction() == Action.PHYSICAL) && (event.getClickedBlock().getType() == Material.FARMLAND)) {
            if (this.plugin.getManager().isVanished(player) && VanishPerms.canNotTrample(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotPickUp(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player && this.plugin.getManager().isVanished((Player) event.getEntity()) && VanishPerms.canNotPickUp((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerPickupExperience(PlayerPickupExperienceEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotPickUp(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (this.plugin.getManager().isVanished(player)) {
            this.plugin.messageStatusUpdate(ChatColor.DARK_AQUA + event.getPlayer().getName() + " has quit vanished");
        }
        this.plugin.getManager().playerQuit(player);
        this.plugin.hooksQuit(player);
        this.plugin.getManager().getAnnounceManipulator().dropDelayedAnnounce(player.getName());
        if (!this.plugin.getManager().getAnnounceManipulator().playerHasQuit(player.getName()) || VanishPerms.silentQuit(player)) {
            event.setQuitMessage(null);
        }
        this.plugin.chestFakeClose(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer()) && VanishPerms.canNotInteract(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (Settings.getWorldChangeCheck()) {
            this.plugin.getManager().playerRefresh(event.getPlayer());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onNaturalSpawn(PlayerNaturallySpawnCreaturesEvent event) {
        if (this.plugin.getManager().isVanished(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}