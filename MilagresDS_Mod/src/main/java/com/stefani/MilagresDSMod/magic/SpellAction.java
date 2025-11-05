package com.stefani.MilagresDSMod.magic;

/**
 * Functional interface representing a discrete action that is executed when a spell
 * is cast.  Implementations can perform arbitrary logic using the provided
 * {@link SpellContext}.  Lambdas may be used to define actions inline.
 */
@FunctionalInterface
public interface SpellAction {
    /**
     * Executes this spell action using the given context.  The context provides
     * access to the player casting the spell, the level in which the spell is
     * cast, as well as hit results and other properties.
     *
     * @param context contextual information about the current spell cast
     */
    void execute(SpellContext context);
}