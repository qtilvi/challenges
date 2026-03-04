package io.github.qtilvi.challenges.challenge;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface Challenge {
    /**
     * @return the unique name of the challenge
     */
    String getName();

    /**
     * Enables the challenge
     * Called when the challenge is activated.
     * @param ctx CommandSourcceStack, useful for getting the command sender, their world, etc.
     * @return true if enabling succeeded, false otherwise
     */
    boolean enable(CommandContext<CommandSourceStack> ctx);

    /**
     * Registers the challenge
     * Called shortly after the challenge is activated.
     */
    void register();

    /**
     * Disables the challenge
     * Called when the challenge is deactivated.
     */
    void disable();

    /**
     * @return true if challenge is enabled, false if challenge is disabled
     */
    boolean isEnabled();
}
