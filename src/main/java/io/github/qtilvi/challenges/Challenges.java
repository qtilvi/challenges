package io.github.qtilvi.challenges;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
import org.jspecify.annotations.NonNull;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/*
TODO:   - find a cleaner solution to `boolean noX, noY, noZ`
 */

public class Challenges extends JavaPlugin implements Listener {
    private enum Challenge {
        NO_CRAFTING_TABLE,
        NO_FALL_DAMAGE,
        NO_ARMOR,
        THREE_HEARTS
    }

    private final Set<Challenge> activeChallenges = EnumSet.noneOf(Challenge.class);

    private void setActiveChallenges(Challenge challenge, boolean action) {
        if (action) activeChallenges.add(challenge);
        else activeChallenges.remove(challenge);
    }

    private boolean getActiveChallenge(Challenge challenge) {
        return activeChallenges.contains(challenge);
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(this, this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> challengesCommandRoot = Commands.literal("challenges")
                    .requires(sender -> sender.getSender().isOp())
                    .then(Commands.literal("noCraftingTable")
                            .then(Commands.argument("enable", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean enable = ctx.getArgument("enable", boolean.class);
                                        setActiveChallenges(Challenge.NO_CRAFTING_TABLE, enable);

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("noFallDamage")
                            .then(Commands.argument("enable", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean enable = ctx.getArgument("enable", boolean.class);
                                        setActiveChallenges(Challenge.NO_FALL_DAMAGE, enable);

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("noArmor")
                            .then(Commands.argument("enable", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean enable = ctx.getArgument("enable", boolean.class);
                                        setActiveChallenges(Challenge.NO_ARMOR, enable);

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("threeHearts")
                            .then(Commands.argument("enable", BoolArgumentType.bool())
                                    .executes(ctx -> {
                                        boolean enable = ctx.getArgument("enable", boolean.class);
                                        setActiveChallenges(Challenge.THREE_HEARTS, enable);

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    );
            commands.registrar().register(challengesCommandRoot.build());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        updateCommands(player);
        threeHearts(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        if (getActiveChallenge(Challenge.NO_CRAFTING_TABLE)) noCraftingTable(playerInteractEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        if (getActiveChallenge(Challenge.NO_FALL_DAMAGE)) noFallDamage(entityDamageEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityEquipmentChange(EntityEquipmentChangedEvent entityEquipmentChangedEvent) {
        if (getActiveChallenge(Challenge.NO_ARMOR)) noArmor(entityEquipmentChangedEvent);
    }

    private void updateCommands(@NonNull Player player) {
        player.updateCommands();
    }

    private void noCraftingTable(@NonNull PlayerInteractEvent playerInteractEvent) {
        Block clickedBlock = playerInteractEvent.getClickedBlock();
        if ((clickedBlock == null) || (clickedBlock.getType() != Material.CRAFTING_TABLE)) return;

        Action action = playerInteractEvent.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;

        playerInteractEvent.setCancelled(true);
    }

    private void noFallDamage(@NonNull EntityDamageEvent entityDamageEvent) {
        DamageType damageType = entityDamageEvent.getDamageSource().getDamageType();
        if (damageType != DamageType.FALL) return;

        Entity entity = entityDamageEvent.getEntity();
        if (!(entity instanceof Player player)) return;

        player.setGameMode(GameMode.SPECTATOR);
    }

    private void noArmor(@NonNull EntityEquipmentChangedEvent entityEquipmentChangedEvent) {
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
        }
    }

    private boolean isArmorSlot(EquipmentSlot equipmentSlot) {
        return equipmentSlot == EquipmentSlot.HEAD ||
                equipmentSlot == EquipmentSlot.CHEST ||
                equipmentSlot == EquipmentSlot.LEGS ||
                equipmentSlot == EquipmentSlot.FEET;
    }

    private void threeHearts(Player player) {
        AttributeInstance attributeInstance = player.getAttribute(Attribute.MAX_HEALTH);
        if (attributeInstance == null) return;

        double targetHealth = getActiveChallenge(Challenge.THREE_HEARTS) ? 6 : 20;
        if (attributeInstance.getBaseValue() != targetHealth) attributeInstance.setBaseValue(targetHealth);

        if (getActiveChallenge(Challenge.THREE_HEARTS)) {
            attributeInstance.setBaseValue(6);
        } else {
            attributeInstance.setBaseValue(20);
        }

    }
}
