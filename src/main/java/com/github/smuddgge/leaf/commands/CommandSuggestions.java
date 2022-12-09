package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.Leaf;
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
            players.add(player.getGameProfile().getName());
        }

        this.data.add(players);
        return this;
    }
}
