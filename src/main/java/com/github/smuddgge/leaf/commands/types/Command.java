package com.github.smuddgge.leaf.commands.types;

import com.github.smuddgge.leaf.commands.BaseCommandType;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.commands.CommandSuggestions;
import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.utility.CommandUtility;
import com.github.smuddgge.leaf.utility.PlayerUtility;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Command extends BaseCommandType {

    @Override
    public String getName() {
        return "command";
    }

    @Override
    public String getSyntax() {
        return "/[name]";
    }

    @Override
    public CommandSuggestions getSuggestions(ConfigurationSection section, User user) {
        CommandSuggestions suggestions = new CommandSuggestions();
        ConfigurationSection suggestionSection = section.getSection("suggestions");

        for (String key : suggestionSection.getKeys()) {
            List<String> argumentList = new ArrayList<>();

            // Loop though all suggestions to add.
            for (String value : suggestionSection.getAdaptedString(key, ",").split(",")) {

                if (value.contains("%players%")) {
                    argumentList.addAll(PlayerUtility.getPlayers(user));
                    continue;
                }

                if (value.contains("%all_players%")) {
                    argumentList.addAll(PlayerUtility.getPlayersRaw());
                    continue;
                }

                if (value.contains("%database_players%")) {
                    argumentList.addAll(PlayerUtility.getDatabasePlayers());
                    continue;
                }

                argumentList.add(value);
            }

            // Add the argument list to the next set of suggestions.
            suggestions.append(argumentList);
        }

        return suggestions;
    }

    @Override
    public CommandStatus onConsoleRun(ConfigurationSection section, String[] arguments) {

        // Get the final command list.
        List<String> commandList = this.getFinalCommandList(section, arguments);

        // Execute the commands in console.
        CommandUtility.executeCommandInConsole(commandList);
        return new CommandStatus();
    }

    @Override
    public CommandStatus onPlayerRun(ConfigurationSection section, String[] arguments, User user) {

        // Get the final command list.
        List<String> commandList = this.getFinalCommandList(section, arguments);

        // Execute the commands as the user.
        user.executeCommand(commandList);
        return new CommandStatus();
    }

    @Override
    public void onDiscordRegister(ConfigurationSection section, @NotNull CommandCreateAction action) {
        action.addOption(OptionType.STRING, "player", "The players name.").complete();
    }

    @Override
    public CommandStatus onDiscordRun(ConfigurationSection section, SlashCommandEvent event) {
        return super.onDiscordRun(section, event);
    }

    /**
     * Used to get the list of commands
     * to execute.
     *
     * @param section The instance of the configuration section.
     * @param arguments The instance of the command arguments.
     * @return The list of commands to execute.
     */
    private @NotNull List<String> getFinalCommandList(ConfigurationSection section, String[] arguments) {
        List<String> finalCommandList = new ArrayList<>();

        // Loop though each command.
        for (String command : section.getListString("commands", new ArrayList<>())) {
            String finalCommand = command;

            // Loop though each argument.
            // Parse the argument placeholder.
            int argumentNumber = 1;
            for (String argument : arguments) {
                finalCommand = finalCommand.replace("%argument_" + argumentNumber + "%", argument);
                argumentNumber ++;
            }

            finalCommandList.add(finalCommand);
        }

        return finalCommandList;
    }
}