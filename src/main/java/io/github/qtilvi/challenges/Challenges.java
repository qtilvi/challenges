package io.github.qtilvi.challenges;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;

/*
TODO:   - change `void three_hearts()` to update on en-/disable
        - find a cleaner solution to `boolean noX, noY, noZ`
 */

public class Challenges extends JavaPlugin implements Listener {
    private boolean noCraftingTableBool = false;
    private boolean noFallDamageBool = false;
    private boolean noArmorBool = false;
    private boolean threeHeartsBool = false;

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> challengesCommandRoot = Commands.literal("challenges")
                    .requires(sender -> sender.getSender().isOp())
                    .then(Commands.literal("enable")
                            .then(Commands.literal("noCraftingTable")
                                    .executes(ctx -> {
                                        noCraftingTableBool = true;
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("noFallDamage")
                                    .executes(ctx -> {
                                        noFallDamageBool = true;
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("noArmor")
                                    .executes(ctx -> {
                                        noArmorBool = true;
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("threeHearts")
                                    .executes(ctx -> {
                                        threeHeartsBool = true;
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("disable")
                            .then(Commands.literal("noCraftingTable")
                                    .executes(ctx -> {
                                        noCraftingTableBool = false;
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("noFallDamage")
                                    .executes(ctx -> {
                                        noFallDamageBool = false;
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("noArmor")
                                    .executes(ctx -> {
                                        noArmorBool = false;
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .then(Commands.literal("threeHearts")
                                    .executes(ctx -> {
                                        threeHeartsBool = false;
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    );
            commands.registrar().register(challengesCommandRoot.build());
        });
    }

    @EventHandler
    public void updateCommands(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        player.updateCommands();
    }

    @EventHandler(ignoreCancelled = true)
    public void noCraftingTable(PlayerInteractEvent playerInteractEvent) {
        if (!noCraftingTableBool) return;

        Block clickedBlock = playerInteractEvent.getClickedBlock();
        if ((clickedBlock == null) || (clickedBlock.getType() != Material.CRAFTING_TABLE)) return;

        Action action = playerInteractEvent.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;

        playerInteractEvent.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void noFallDamage(EntityDamageEvent entityDamageEvent) {
        if (!noFallDamageBool) return;

        DamageType damageType = entityDamageEvent.getDamageSource().getDamageType();
        if (damageType != DamageType.FALL) return;

        Entity entity = entityDamageEvent.getEntity();
        if (!(entity instanceof Player player)) return;

        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler(ignoreCancelled = true)
    public void noArmor(EntityEquipmentChangedEvent entityEquipmentChangedEvent) {
        if (!noArmorBool) return;

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

    @EventHandler(ignoreCancelled = true)
    public void threeHearts(PlayerJoinEvent playerJoinEvent) {
        if (!threeHeartsBool) return;

        Player player = playerJoinEvent.getPlayer();
        AttributeInstance attributeInstance = player.getAttribute(Attribute.MAX_HEALTH);
        Objects.requireNonNull(attributeInstance).setBaseValue(6);
    }

    private boolean isArmorSlot(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.HEAD ||
                equipmentSlot == EquipmentSlot.CHEST ||
                equipmentSlot == EquipmentSlot.LEGS ||
                equipmentSlot == EquipmentSlot.FEET;
    }
}
