package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;

/**
 * Represents a chat command.
 * <ul>
 *     <li>Used to send a message in the chat.</li>
 * </ul>
 */
public class ChatCommand extends Command {

    private final String identifier;

    private final ConfigurationSection section;

    /**
     * Used to get the chat commands configuration section.
     *
     * @param identifier The command's identifier.
     * @param section    The configuration section.
     */
    public ChatCommand(String identifier, ConfigurationSection section) {
        this.identifier = identifier;
        this.section = section;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getName() {
        return this.section.getString("name");
    }

    @Override
    public CommandAliases getAliases() {
        return new CommandAliases().append(this.section.getListString("aliases"));
    }

    @Override
    public String getSyntax() {
        return "/[name] [message]";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return null;
    }

    @Override
    public String getPermission() {
        return this.section.getString("permission");
    }

    @Override
    public boolean isEnabled() {
        return this.section.getBoolean("enabled", false);
    }

    @Override
    public CommandStatus onConsoleRun(String[] arguments) {

        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        String message = this.section.getString("format")
                .replace("%player%", "Console")
                .replace("%message%", String.join(" ", arguments));

        for (Player player : Leaf.getServer().getAllPlayers()) {

            if (!player.hasPermission(this.getPermission())) continue;

            new User(player).sendMessage(message);
        }

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(String[] arguments, User user) {

        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        String message = this.section.getString("format")
                .replace("%player%", user.getName())
                .replace("%message%", String.join(" ", arguments));

        for (Player player : Leaf.getServer().getAllPlayers()) {

            if (!player.hasPermission(this.getPermission())) continue;

            new User(player).sendMessage(message);
        }

        return new CommandStatus();
    }
}
