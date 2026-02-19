package io.github.qtilvi.challenges.challenge;

import org.bukkit.GameMode;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoFallDamageChallenge extends AbstractChallenge {
    public NoFallDamageChallenge(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public String getName() {
        return "nofalldamage";
    }

    @EventHandler(ignoreCancelled = true)
    public void noFallDamage(EntityDamageEvent entityDamageEvent) {
        DamageType damageType = entityDamageEvent.getDamageSource().getDamageType();
        if (damageType != DamageType.FALL) return;

        Entity entity = entityDamageEvent.getEntity();
        if (!(entity instanceof Player player)) return;

        player.setGameMode(GameMode.SPECTATOR);
    }
}
