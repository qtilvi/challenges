package io.github.qtilvi.challenges.challenge;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class WolfiChallenge extends AbstractChallenge {
    private Wolf wolf;

    public WolfiChallenge(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public String getName() {
        return "wolfi";
    }

    @Override
    public void enable() {
        super.enable();

        Player player = Bukkit.getOnlinePlayers().iterator().next();
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

    @Override
    public void disable() {
        super.disable();

        if (wolf != null && !wolf.isDead()) {
            wolf.remove();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void wolfiChallenge(EntityDeathEvent entityDeathEvent) {
        if (!isEnabled()) return;

        Entity entity = entityDeathEvent.getEntity();
        if (wolf != entity) return;
        AnimalTamer animalTamer = wolf.getOwner();
        if (animalTamer == null) return;
        UUID uuid = animalTamer.getUniqueId();
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return;
        player.setGameMode(GameMode.SPECTATOR);
    }
}
