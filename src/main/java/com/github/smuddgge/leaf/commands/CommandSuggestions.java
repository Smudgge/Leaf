package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.database.Record;
import com.github.smuddgge.leaf.database.records.PlayerRecord;
import com.github.smuddgge.leaf.datatype.User;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * @param string The string
     */
    public void appendBase(String string) {
        if (this.data.isEmpty()) {
            this.data.add(new ArrayList<>(Arrays.stream(new String[]{string}).toList()));
        }

        this.data.get(0).add(string);
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
        int index = 1;
        for (List<String> list : suggestions.get()) {
            if (this.data.size() >= index) {
                this.data.get(index).addAll(list);
            } else {
                this.data.add(list);
            }

            index++;
        }
    }
}
