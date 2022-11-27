package com.github.smuddgge.leaf.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the tab suggestions for a command.
 */
public class Suggestions {

    List<List<String>> data = new ArrayList<>();

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
}
