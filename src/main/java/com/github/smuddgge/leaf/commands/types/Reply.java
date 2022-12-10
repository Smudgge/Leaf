package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;

/**
 * Represents the reply command type.
 */
public class Reply implements CommandType {

    @Override
    public String getName() {
        return "reply";
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
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        MessageManager.log("{error_colour}The console cannot reply to messages.");
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        if (arguments.length < 1) return new CommandStatus().incorrectArguments();

        User recipient = user.getLastMessaged();

        if (recipient == null) {
            user.sendMessage(section.getString("not_found", "{error_colour}You have no conversation to reply to."));
            return new CommandStatus();
        }

        if (recipient.isVanished()) {
            user.sendMessage(section.getString("not_found", "{error_colour}You have no conversation to reply to."));
            return new CommandStatus();
        }

        String message = String.join(" ", arguments).trim();

        // Send messages
        recipient.sendMessage(PlaceholderManager.parse(section.getString("from")
                .replace("%message%", message), null, user));

        user.sendMessage(PlaceholderManager.parse(section.getString("to")
                .replace("%message%", message), null, recipient));

        // Log message interaction
        MessageManager.setLastMessaged(user.getUniqueId(), recipient.getUniqueId());

        return new CommandStatus();
    }
}
