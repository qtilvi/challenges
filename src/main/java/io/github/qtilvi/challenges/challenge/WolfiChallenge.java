package io.github.qtilvi.challenges.challenge;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
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
    public boolean enable(CommandContext<CommandSourceStack> ctx) {
        CommandSender commandSender = ctx.getSource().getSender();
        if (!(commandSender instanceof Player player)) return false;

        super.register();

        wolfCreate(player);

        return true;
    }

    @Override
    public void disable() {
        super.disable();

        if (wolf != null && !wolf.isDead()) {
            wolf.remove();
        }
    }

    private void wolfCreate(Player player) {
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
