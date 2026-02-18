package io.github.qtilvi.challenges;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/*
TODO:   - use PlayerArmorChangeEvent for `noArmor` challenge
        - switch `boolean enable = (wolf == null)` for something better, say `getActiveChallenges()`
        - switch `wolf.setHealth(0);` for something better, say `wolf.remove()`
        - look in to better alternatives for `EntityDeathEvent`cal

wolf Field Design Problem
private Wolf wolf = null;


This limits you to exactly one wolf globally.

That may be intended — but structurally:

If the wolf gets unloaded (chunk unload) → your reference becomes stale.

If the server restarts → reference is gone.

If the wolf is removed externally → you still hold the reference.
 */

public class Challenges extends JavaPlugin implements Listener {
    private enum Challenge {
        NO_CRAFTING_TABLE,
        NO_FALL_DAMAGE,
        NO_ARMOR,
        THREE_HEARTS,
        WOLFI
    }

    private Wolf wolf = null;

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

                                        double maxHealth = enable ? 6 : 20;
                                        setAllPlayersMaxHealth(maxHealth);

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("wolfi")
                            .then(Commands.argument("player", ArgumentTypes.player())
                                    .executes(ctx -> {
                                        boolean enable = (wolf == null);
                                        setActiveChallenges(Challenge.WOLFI, enable);

                                        if (enable) {
                                            PlayerSelectorArgumentResolver playerSelectorArgumentResolver = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                            Player player = playerSelectorArgumentResolver.resolve(ctx.getSource()).getFirst();

                                            wolfi_init(player);
                                        } else {
                                            wolf.setHealth(0);
                                            wolf = null;
                                        }

                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    );
            commands.registrar().register(challengesCommandRoot.build());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent entityDamageEvent) {
        if (!getActiveChallenge(Challenge.NO_FALL_DAMAGE)) return;

        noFallDamage(entityDamageEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent entityDeathEvent) {
        if (!getActiveChallenge(Challenge.WOLFI)) return;

        Entity entity = entityDeathEvent.getEntity();
        wolfi(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityEquipmentChange(EntityEquipmentChangedEvent entityEquipmentChangedEvent) {
        if (!getActiveChallenge(Challenge.NO_ARMOR)) return;

        noArmor(entityEquipmentChangedEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        if (!getActiveChallenge(Challenge.NO_CRAFTING_TABLE)) return;

        noCraftingTable(playerInteractEvent);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        updateCommands(player);
        threeHearts(player);
    }


    private void updateCommands(Player player) {
        player.updateCommands();
    }

    private void noCraftingTable(PlayerInteractEvent playerInteractEvent) {
        Block clickedBlock = playerInteractEvent.getClickedBlock();
        if ((clickedBlock == null) || (clickedBlock.getType() != Material.CRAFTING_TABLE)) return;

        Action action = playerInteractEvent.getAction();
        if (action != Action.RIGHT_CLICK_BLOCK) return;

        playerInteractEvent.setCancelled(true);
    }

    private void noFallDamage(EntityDamageEvent entityDamageEvent) {
        DamageType damageType = entityDamageEvent.getDamageSource().getDamageType();
        if (damageType != DamageType.FALL) return;

        Entity entity = entityDamageEvent.getEntity();
        if (!(entity instanceof Player player)) return;

        player.setGameMode(GameMode.SPECTATOR);
    }

    private void noArmor(EntityEquipmentChangedEvent entityEquipmentChangedEvent) {
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

        if (getActiveChallenge(Challenge.THREE_HEARTS)) {
            attributeInstance.setBaseValue(6);
        } else {
            attributeInstance.setBaseValue(20);
        }

    }

    private void setAllPlayersMaxHealth(double maxHealth) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            AttributeInstance attributeInstance = player.getAttribute(Attribute.MAX_HEALTH);
            if (attributeInstance != null) attributeInstance.setBaseValue(maxHealth);
        }
    }

    private void wolfi_init(Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();

        wolf = (Wolf) world.spawnEntity(location, EntityType.WOLF);

        wolf.setOwner(player);
        wolf.setTamed(true);
        wolf.setAdult();
        wolf.customName(Component.text("Wolfi"));
        wolf.setCustomNameVisible(true);
        wolf.setVariant(Wolf.Variant.PALE);
        wolf.setCollarColor(DyeColor.RED);
    }

    private void wolfi(Entity entity) {
        if (wolf != entity) return;

        AnimalTamer animalTamer = wolf.getOwner();
        if (animalTamer == null) return;

        UUID uuid = animalTamer.getUniqueId();
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;

        player.setGameMode(GameMode.SPECTATOR);
        setActiveChallenges(Challenge.WOLFI, false);
        wolf = null;
    }
}
