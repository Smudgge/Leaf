package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.dependencys.ProtocolizeDependency;
import com.github.smuddgge.leaf.utility.Sounds;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

/**
 * <h1>Alert Raw Command Type</h1>
 * Used to alert all online players with a message.
 * This message will be formatted with JSON.
 */
public class AlertRaw extends BaseCommandType {

    @Override
    public String getName() {
        return "alertraw";
    }

    @Override
    public String getSyntax() {
        return "/[name] [json]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        // Attempt to send the JSON message.
        try {
            GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
            Component component = gsonComponentSerializer.deserialize(String.join(" ", arguments));

            // Send the message to all players
            for (Player player : Leaf.getServer().getAllPlayers()) {
                player.sendMessage(component);

                // Play sound if it exists.
                if (ProtocolizeDependency.isEnabled()) Sounds.play(section.getString("see_sound", null), player.getUniqueId());
            }

            // Log the message in console.
            MessageManager.log(component);

        } catch (Exception exception) {
            MessageManager.log("Incorrect arguments for alert raw command type : ");
            MessageManager.log(exception.getMessage());
            return new CommandStatus().incorrectArguments();
        }

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {
        return this.onConsoleRun(section, arguments);
    }
}
