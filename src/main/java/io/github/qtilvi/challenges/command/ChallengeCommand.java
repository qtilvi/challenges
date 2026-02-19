package io.github.qtilvi.challenges.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.qtilvi.challenges.challenge.Challenge;
import io.github.qtilvi.challenges.manager.ChallengeManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public class ChallengeCommand implements Listener {
    private final ChallengeManager challengeManager;
    private final JavaPlugin javaPlugin;

    public ChallengeCommand(ChallengeManager challengeManager, JavaPlugin javaPlugin) {
        this.challengeManager = challengeManager;
        this.javaPlugin = javaPlugin;

        registerCommands();
    }

    private void registerCommands() {
        Collection<Challenge> challenges = challengeManager.getAll();

        javaPlugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> challengesCommandRoot = Commands.literal("challenges")
                    .requires(sender -> sender.getSender().isOp())
                    .then(Commands.literal("enable")
                            .then(Commands.argument("challenge", StringArgumentType.string())
                                    .executes(ctx -> {
                                        String challengeString = ctx.getArgument("challenge", String.class);
                                        Challenge challenge = challengeManager.get(challengeString);

                                        if (challenges.contains(challenge)) {
                                            challenge.enable();

                                            CommandSender commandSender = ctx.getSource().getSender();
                                            commandSender.sendMessage("Enabled " + challenge.getName());
                                        }
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    )
                    .then(Commands.literal("disable")
                            .then(Commands.argument("challenge", StringArgumentType.string())
                                    .executes(ctx -> {
                                        String challengeString = ctx.getArgument("challenge", String.class);
                                        Challenge challenge = challengeManager.get(challengeString);

                                        if (challenges.contains(challenge)) {
                                            challenge.disable();

                                            CommandSender commandSender = ctx.getSource().getSender();
                                            commandSender.sendMessage("Disabled " + challenge.getName());
                                        }
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                    );
            commands.registrar().register(challengesCommandRoot.build());
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void syncCommands(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        player.updateCommands();
    }
}
