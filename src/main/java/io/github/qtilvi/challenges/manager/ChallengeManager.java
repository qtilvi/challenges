package io.github.qtilvi.challenges.manager;

import io.github.qtilvi.challenges.challenge.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChallengeManager {
    private final Map<String, Challenge> challenges = new HashMap<>();

    public ChallengeManager(JavaPlugin javaPlugin) {
        register(new NoCraftingTableChallenge(javaPlugin));
        register(new NoFallDamageChallenge(javaPlugin));
        register(new NoArmorChallenge(javaPlugin));
        register(new ThreeHeartsChallenge(javaPlugin));
        register(new WolfiChallenge(javaPlugin));
    }

    private void register(Challenge challenge) {
        challenges.put(challenge.getName().toLowerCase(), challenge);
    }

    public Challenge get(String name) {
        return challenges.get(name.toLowerCase());
    }

    public Collection<Challenge> getAll() {
        return challenges.values();
    }
}
