package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.placeholders.PlaceholderCondition;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the condition manager.
 */
public class ConditionManager {

    private static List<PlaceholderCondition> placeholderConditionList = new ArrayList<>();

    /**
     * Used to register a condition in the manager.
     *
     * @param placeholderCondition An instance of a placeholder condition.
     */
    public static void register(PlaceholderCondition placeholderCondition) {
        ConditionManager.placeholderConditionList.add(placeholderCondition);
    }
}
