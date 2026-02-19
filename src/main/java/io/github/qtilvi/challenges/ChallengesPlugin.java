package io.github.qtilvi.challenges;

import io.github.qtilvi.challenges.command.ChallengeCommand;
import io.github.qtilvi.challenges.manager.ChallengeManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ChallengesPlugin extends JavaPlugin implements Listener {

    private ChallengeManager challengeManager;
    private ChallengeCommand challengeCommand;

    @Override
    public void onEnable() {
        this.challengeManager = new ChallengeManager(this);
        this.challengeCommand = new ChallengeCommand(challengeManager, this);
    }
}