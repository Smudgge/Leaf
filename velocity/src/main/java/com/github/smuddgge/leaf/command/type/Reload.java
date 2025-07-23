package com.github.smuddgge.leaf.command.type;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.LeafException;
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

        // Reload the plugin.
        // When aborted it will already log the message in console.
        boolean success = this.reload();

        // Check if the reloading was aborted.
        if (!success) return new CommandStatus();

        // Get the message and log it in console.
        String message = section.getAdaptedString("message", "\n", "{message} Reloaded all configs.");
        user.sendMessage(message);
        return new CommandStatus();
    }

    @Override
    public @NotNull CommandStatus onPlayer(@NotNull ConfigurationSection section, @NotNull PlayerUser user, @NotNull String[] arguments) {

        // Attempt to reload the plugin.
        boolean success = this.reload();

        // Check if the reloading was aborted.
        if (!success) {
            user.sendMessage(section.getAdaptedString("error", "\n",
                    "{error_colour}An error occurred while reloading. Changes have been aborted."
            ));
            return new CommandStatus();
        }

        // Get the message and send it to the user.
        String message = section.getAdaptedString("message", "\n", "{message} Reloaded all configs.");
        user.sendMessage(message);
        return new CommandStatus();
    }

    private boolean reload() {
        new ConsoleUser().sendMessage("&f&lReloading");

        // Reload config.
        try {
            Leaf.get().getConfig().load();
        } catch (Exception exception) {
            throw new LeafException(
                    exception,
                    "Reload.reload() config",
                    "Unable to reload the config.yml",
                    "- Make sure the config file is formatted correctly."
            );
        }

        // Reload messages config.
        try {
            Leaf.get().getMessagesConfig().load();
        } catch (Exception exception) {
            throw new LeafException(
                    exception,
                    "Reload.reload() messages config",
                    "Unable to reload the messages.yml",
                    "- Make sure the config file is formatted correctly."
            );
        }

        // Reload placeholders.
        try {
            Leaf.get().getPlaceholdersDirectory().load(false);
        } catch (Exception exception) {
            throw new LeafException(
                    exception,
                    "Reload.reload() placeholder directory",
                    "Unable to load configuration in the placeholder directory.",
                    "- Make sure the config files are formatted correctly."
            );
        }

        // Register new placeholders.
        try {
            Leaf.get().unregisterCustomPlaceholders();
            Leaf.get().registerCustomPlaceholders();
        } catch (Exception exception) {
            throw new LeafException(
                    exception,
                    "Reload.reload() placeholder unregister and register",
                    "Unable to register new placeholders.",
                    "- One of your placeholders may be formatted incorrectly."
            );
        }

        // Reload command config.
        try {
            Leaf.get().getCommandsDirectory().load(false);
        } catch (Exception exception) {
            throw new LeafException(
                    exception,
                    "Reload.reload() command directory",
                    "Unable to load configuration in the command directory.",
                    "- Make sure the config files are formatted correctly."
            );
        }

        // Reload commands.
        try {
            Leaf.get().reloadCommands();
        } catch (Exception exception) {
            throw new LeafException(
                    exception,
                    "Reload.reload() command unregister and register",
                    "Unable to register new commands.",
                    "- One of your commands may be formatted incorrectly."
            );
        }

        new ConsoleUser().sendMessage("&f&lReloaded successfully.");
        return true;
    }
}
