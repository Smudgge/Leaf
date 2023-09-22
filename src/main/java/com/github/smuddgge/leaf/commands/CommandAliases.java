package com.github.smuddgge.leaf.commands;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of command aliases.
 */
public class CommandAliases {

    private final List<String> aliases = new ArrayList<>();

    /**
     * Used to append an alias to the list.
     *
     * @param alias The name of the command alias.
     * @return The instance of the aliases.
     */
    public CommandAliases append(String alias) {
        this.aliases.add(alias);

        return this;
    }

    /**
     * Used to append multiple aliases to the list.
     *
     * @param aliases A list of aliases.
     * @return The instance of the aliases.
     */
    public CommandAliases append(List<String> aliases) {
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

    /**
     * Used to check if a name is contained in this list.
     *
     * @param name The name of the command.
     * @return True if it is an alias.
     */
    public boolean contains(@NotNull String name) {
        return this.aliases.contains(name);
    }
}
