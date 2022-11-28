package com.github.smuddgge.leaf.commands.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.Suggestions;
import com.github.smuddgge.leaf.configuration.ConfigCommands;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class AlertRaw extends Command {

    @Override
    public String getIdentifier() {
        return "alertraw";
    }

    @Override
    public String getSyntax() {
        return "/[name] [json]";
    }

    @Override
    public Suggestions getSuggestions(User user) {
        return null;
    }

    @Override
    public CommandStatus onConsoleRun(String[] arguments) {
        if (arguments.length == 0) return new CommandStatus().incorrectArguments();

        try {

            GsonComponentSerializer gsonComponentSerializer = GsonComponentSerializer.gson();
            Component component = gsonComponentSerializer.deserialize(String.join(" ", arguments));

            for (Player temp : Leaf.getServer().getAllPlayers()) {
                temp.sendMessage(component);
            }

            MessageManager.log(component);

        } catch (Exception exception) {
            player.sendMessage(ConfigManager.getMessages().getMessages().getInvalidArgument());
        }

        MessageManager.log(message);

        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(String[] arguments, User user) {
        return this.onConsoleRun(arguments);
    }
}