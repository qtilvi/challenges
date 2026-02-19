package io.github.qtilvi.challenges.challenge;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractChallenge implements Challenge, Listener {
    protected final JavaPlugin javaPlugin;
    private boolean enabled = false;

    protected AbstractChallenge(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public void enable() {
        if (enabled) return;
        enabled = true;
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    @Override
    public void disable() {
        if (!enabled) return;
        enabled = false;
        HandlerList.unregisterAll(this);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
