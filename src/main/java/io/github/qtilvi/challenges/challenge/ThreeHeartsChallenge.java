package io.github.qtilvi.challenges.challenge;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class ThreeHeartsChallenge extends AbstractChallenge {
    public ThreeHeartsChallenge(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public String getName() {
        return "threehearts";
    }

    @Override
    public void enable() {
        super.enable();
        double maxHealth = super.isEnabled() ? 6.0 : 20.0;
        setAllPlayersMaxHealth(maxHealth);
    }

    @Override
    public void disable() {
        super.disable();
        double maxHealth = super.isEnabled() ? 6.0 : 20.0;
        setAllPlayersMaxHealth(maxHealth);
    }

    @EventHandler(ignoreCancelled = true)
    public void threeHeartsChallenge(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        AttributeInstance attributeInstance = player.getAttribute(Attribute.MAX_HEALTH);
        if (attributeInstance == null) return;

        if (super.isEnabled()) {
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
}
