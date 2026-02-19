package io.github.qtilvi.challenges;

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

public class ChallengesPlugin extends JavaPlugin implements Listener {

    private ChallengeManager challengeManager;

    @Override
    public void onEnable() {
        this.challengeManager = new ChallengeManager(this);
        Collection<Challenge> challenges = challengeManager.getAll();

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
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
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();

        player.updateCommands();
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }
}
/*
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
 */