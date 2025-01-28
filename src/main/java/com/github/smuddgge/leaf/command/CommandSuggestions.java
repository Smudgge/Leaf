package com.github.smuddgge.leaf.command;

import com.github.smuddgge.leaf.helper.PlayerHelper;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.squishylib.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Represents the tab suggestions for a command.
 */
public class CommandSuggestions {

    private final List<List<String>> data = new ArrayList<>();
    private boolean isContinuous = false;

    public CommandSuggestions append(List<String> list) {
        this.data.add(list);
        return this;
    }

    public CommandSuggestions append(String[] list) {
        this.data.add(new ArrayList<>(Arrays.stream(list).toList()));
        return this;
    }

    public void appendBase(String string) {
        if (this.data.isEmpty()) {
            this.data.add(new ArrayList<>(Arrays.stream(new String[]{string}).toList()));
        }

        this.data.get(0).add(string);
    }

    public void appendBase(List<String> strings) {
        if (this.data.isEmpty()) {
            this.data.add(strings);
        }

        this.data.get(0).addAll(strings);
    }

    public List<List<String>> get() {
        return this.data;
    }

    /**
     * Used to add the list of online players to the list.
     *
     * @return This instance.
     */
    public CommandSuggestions appendPlayers() {
        this.data.add(PlayerHelper.getPlayers());
        return this;
    }

    /**
     * Used to add the list of online players to the list.
     * If vanishable players are able to see vanishable players this
     * will also be checked in this method.
     *
     * @param user The user to check if they are able to vanish.
     * @return This instance
     */
    public CommandSuggestions appendPlayers(PlayerUser user) {
        this.data.add(PlayerHelper.getPlayers(user));
        return this;
    }

    /**
     * Used to append every player on the server.
     * Even if the player is in vanish.
     *
     * @return This instance.
     */
    public CommandSuggestions appendPlayersRaw() {
        this.data.add(PlayerHelper.getPlayersRaw());
        return this;
    }

    /**
     * Used to append all the players that are registered in the database.
     *
     * @return This instance
     */
    public CommandSuggestions appendDatabasePlayers() {
        this.data.add(PlayerHelper.getDatabasePlayers());
        return this;
    }

    /**
     * Used to append a user's list of friends.
     *
     * @param user The instance of the user.
     * @return This instance.
     */
    public CommandSuggestions appendFriends(PlayerUser user) {
        this.data.add(PlayerHelper.getFriends(user));
        return this;
    }

    /**
     * Used to combine a sub command types suggestions.
     *
     * @param suggestions Suggestions to combine
     */
    public void combineSubType(CommandSuggestions suggestions) {
        if (suggestions == null) return;

        int index = 1;
        for (List<String> list : suggestions.get()) {
            if (this.data.size() >= index + 1) {
                this.data.get(index).addAll(list);
            } else {
                this.data.add(list);
            }

            index++;
        }
    }

    /**
     * Used to add a subcommands names and suggestions to
     * these commands suggestions.
     *
     * @param subCommandTypes The subcommand types of the command.
     * @param section         The configuration section of the command.
     * @param arguments       The arguments suggested.
     * @param user            The instance of the user executing the command.
     */
    public void appendSubCommandTypes(List<CommandType> subCommandTypes, ConfigurationSection section, String[] arguments, PlayerUser user) {
        for (CommandType commandType : subCommandTypes) {

            ConfigurationSection commandSection = section.getSection(commandType.getIdentifier());

            // Get the commands name.
            String name = commandSection.getString("name", commandType.getIdentifier());

            // Add all the aliases.
            List<String> commandNames = new ArrayList<>(commandSection.getListString("aliases", new ArrayList<>()));

            // Add the commands main name.
            commandNames.add(name);

            // Add the command names to the base.
            this.appendBase(commandNames);

            if (arguments.length == 0) continue;

            // If the first argument references this command, add the sub commands suggestions.
            for (String commandName : commandNames) {
                if (!commandName.toLowerCase(Locale.ROOT).equals(arguments[0].toLowerCase(Locale.ROOT))) continue;
                this.combineSubType(commandType.getSuggestions(section, user));
            }
        }
    }

    /**
     * Used to set the suggestions to continuous.
     * This makes the last suggestion repeat forever.
     *
     * @return This instance.
     */
    public CommandSuggestions setContinuous() {
        this.isContinuous = true;
        return this;
    }

    /**
     * Used to check if the suggestions are continuous.
     *
     * @return True if the suggestions are continuous.
     */
    public boolean isContinuous() {
        return this.isContinuous;
    }
}
