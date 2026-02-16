package io.github.qtilvi.challenges;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class Challenges extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void noCraftingTable(PlayerInteractEvent playerInteractEvent) {
        Block clickedBlock = playerInteractEvent.getClickedBlock();
        if ((clickedBlock == null) || (clickedBlock.getType() != Material.CRAFTING_TABLE)) return;

        Action action = playerInteractEvent.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK) {
            playerInteractEvent.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void noFallDamage(EntityDamageEvent entityDamageEvent) {
        DamageType damageType = entityDamageEvent.getDamageSource().getDamageType();
        if (damageType != DamageType.FALL) return;

        Entity entity = entityDamageEvent.getEntity();
        if (!(entity instanceof Player player)) return;

        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler(ignoreCancelled = true)
    public void noArmor(EntityEquipmentChangedEvent entityEquipmentChangedEvent) {
        LivingEntity livingEntity = entityEquipmentChangedEvent.getEntity();
        if (!(livingEntity instanceof Player player)) return;

        for (Map.Entry<EquipmentSlot, EntityEquipmentChangedEvent.EquipmentChange> entry
                : entityEquipmentChangedEvent.getEquipmentChanges().entrySet()) {
            EquipmentSlot equipmentSlot = entry.getKey();

            if (!(isArmorSlot(equipmentSlot))) continue;

            switch (equipmentSlot) {
                case HEAD -> player.getEquipment().setHelmet(new ItemStack(Material.AIR));
                case CHEST -> player.getEquipment().setChestplate(new ItemStack(Material.AIR));
                case LEGS -> player.getEquipment().setLeggings(new ItemStack(Material.AIR));
                case FEET -> player.getEquipment().setBoots(new ItemStack(Material.AIR));
                default -> {}
            }

            player.sendEquipmentChange(player, equipmentSlot, new ItemStack(Material.AIR));
        }
    }

    private static boolean isArmorSlot(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.HEAD ||
                equipmentSlot == EquipmentSlot.CHEST ||
                equipmentSlot == EquipmentSlot.LEGS ||
                equipmentSlot == EquipmentSlot.FEET;
    }
}
