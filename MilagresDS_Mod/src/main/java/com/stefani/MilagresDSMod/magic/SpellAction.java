package com.stefani.MilagresDSMod.magic;

@FunctionalInterface
public interface SpellAction {
    void execute(SpellContext context);
}
