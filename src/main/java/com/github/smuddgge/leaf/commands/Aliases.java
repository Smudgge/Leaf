package com.github.smuddgge.leaf.commands;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of command aliases.
 */
public class Aliases {

    private final List<String> aliases = new ArrayList<>();

    /**
     * Used to append an alias to the list.
     *
     * @param alias The name of the command alias.
     * @return The instance of the aliases.
     */
    public Aliases append(String alias) {
        this.aliases.add(alias);

        return this;
    }

    /**
     * Used to append multiple aliases to the list.
     *
     * @param aliases A list of aliases.
     * @return The instance of the aliases.
     */
    public Aliases append(List<String> aliases) {
        this.aliases.addAll(aliases);

        return this;
    }

    /**
     * Used to get the aliases as a list.
     *
     * @return The list of aliases.
     */
    public List<String> get() {
        return this.aliases;
    }
}
