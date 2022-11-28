package com.github.smuddgge.leaf.commands;

import com.github.smuddgge.leaf.Leaf;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the tab suggestions for a command.
 */
public class Suggestions {

    private final List<List<String>> data = new ArrayList<>();

    /**
     * Used to append the next suggestion list.
     *
     * @param list Instance of a list.
     */
    public void append(List<String> list) {
        this.data.add(list);
    }

    /**
     * Used to append the next suggestion list.
     *
     * @param list Instance of a string array.
     */
    public void append(String[] list) {
        this.data.add(new ArrayList<>(Arrays.stream(list).toList()));
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
    public Suggestions appendPlayers() {
        List<String> players = new ArrayList<>();

        for (Player player : Leaf.getServer().getAllPlayers()) {
            players.add(player.getGameProfile().getName());
        }

        this.data.add(players);

        return this;
    }
}
