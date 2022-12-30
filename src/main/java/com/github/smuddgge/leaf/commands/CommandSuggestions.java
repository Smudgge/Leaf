package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.configuration.squishyyaml.ConfigurationSection;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;

import java.util.*;

/**
 * Represents the tab suggestions for a command.
 */
public class CommandSuggestions {

    private final List<List<String>> data = new ArrayList<>();

    /**
     * Used to append the next suggestion list.
     *
     * @param list Instance of a list.
     * @return This instance.
     */
    public CommandSuggestions append(List<String> list) {
        this.data.add(list);
        return this;
    }

    /**
     * Used to append the next suggestion list.
     *
     * @param list Instance of a string array.
     * @return This instance.
     */
    public CommandSuggestions append(String[] list) {
        this.data.add(new ArrayList<>(Arrays.stream(list).toList()));
        return this;
    }

    /**
     * Append something to the first tab item.
     *
     * @param string The string.
     */
    public void appendBase(String string) {
        if (this.data.isEmpty()) {
            this.data.add(new ArrayList<>(Arrays.stream(new String[]{string}).toList()));
        }

        this.data.get(0).add(string);
    }

    /**
     * Append a list of suggestions to the first tab item.
     *
     * @param strings A list of strings.
     */
    public void appendBase(List<String> strings) {
        if (this.data.isEmpty()) {
            this.data.add(strings);
        }

        this.data.get(0).addAll(strings);
    }

    /**
     * Used to get the suggestions.
     *
     * @return The suggestions as a 3D list.
     */
    public List<List<String>> get() {
        return this.data;
    }

    /**
     * Used to add the list of online players to the list.
     *
     * @return This instance.
     */
    public CommandSuggestions appendPlayers() {
        List<String> players = new ArrayList<>();

        for (Player player : Leaf.getServer().getAllPlayers()) {
            User user = new User(player);

            if (user.isVanished()) continue;

            players.add(player.getGameProfile().getName());
        }

        this.data.add(players);
        return this;
    }

    /**
     * Used to append all the players that are registered in the database.
     *
     * @return This instance
     */
    public CommandSuggestions appendDatabasePlayers() {
        List<String> players = new ArrayList<>();

        if (Leaf.getDatabase() == null || Leaf.getDatabase().isDisabled()) return this;

        for (Record record : Leaf.getDatabase().getTable("Player").getAllRecords()) {
            PlayerRecord playerRecord = (PlayerRecord) record;
            players.add(playerRecord.name);
        }

        this.data.add(players);
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

    public void appendSubCommandTypes(List<CommandType> subCommandTypes, ConfigurationSection section, String[] arguments, User user) {
        for (CommandType commandType : subCommandTypes) {

            ConfigurationSection commandSection = section.getSection(commandType.getName());

            // Get the commands name.
            String name = commandSection.getString("name", commandType.getName());

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
                this.combineSubType(commandType.getSuggestions(user));
            }
        }

    }
}
