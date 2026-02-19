package io.github.qtilvi.challenges.challenge;

public interface Challenge {
    /**
     * @return the unique name of the challenge
     */
    String getName();

    /**
     * Enables the challenge
     * Called when the challenge is activated.
     */
    void enable();

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
