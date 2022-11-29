package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.commands.CommandType;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

/**
 * Represents the alert raw command type.
 */
public class AlertRaw implements CommandType {

    @Override
    public String getName() {
        return "alertraw";
    }

    @Override
    public String getSyntax() {
        return "/[name] [json]";
    }

    @Override
    public CommandSuggestions getSuggestions(User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        try {

            GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
            Component component = gsonComponentSerializer.deserialize(String.join(" ", arguments));

            for (Player temp : Leaf.getServer().getAllPlayers()) {
                temp.sendMessage(component);
            }

            MessageManager.log(component);

        } catch (Exception exception) {
            return new CommandStatus().incorrectArguments();
        }

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        return this.onConsoleRun(section, arguments);
    }
}
