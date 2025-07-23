package com.github.smuddgge.leaf.command.type;

import com.github.smuddgge.leaf.command.BaseCommandType;
import com.github.smuddgge.leaf.command.CommandStatus;
import com.github.smuddgge.leaf.command.CommandSuggestions;
import com.github.smuddgge.leaf.user.ConsoleUser;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.squishylib.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Reload extends BaseCommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "reload";
    }

    @Override
    public @NotNull String getSyntax() {
        return "/[name]";
    }

    @Override
    public @Nullable CommandSuggestions getSuggestions(@NotNull ConfigurationSection section, @NotNull PlayerUser user) {
        return null;
    }

    @Override
    public @NotNull CommandStatus onConsole(@NotNull ConfigurationSection section, @NotNull ConsoleUser user, @NotNull String[] arguments) {

        

        return new CommandStatus();
    }
}
