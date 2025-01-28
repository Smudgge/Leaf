package com.github.smuddgge.leaf.command.type;

import com.github.smuddgge.leaf.command.BaseCommandType;
import com.github.smuddgge.leaf.command.CommandStatus;
import com.github.smuddgge.leaf.command.CommandSuggestions;
import com.github.smuddgge.leaf.command.CommandType;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Info extends BaseCommandType {

    @Override
    public @NotNull String getIdentifier() {
        return "info";
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
    public @NotNull CommandStatus onUser(@NotNull ConfigurationSection section, @NotNull User user, @NotNull String[] arguments) {

        // Get the message to send back to the user.
        final String message = section.getAdaptedString("message", "\n", "&cMessage not defined.");

        // Send the message back to the user.
        user.sendMessage(message);
        return new CommandStatus();
    }
}
