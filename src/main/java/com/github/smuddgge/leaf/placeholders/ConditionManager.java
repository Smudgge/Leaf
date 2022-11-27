package com.github.smuddgge.leaf.placeholders;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents the condition manager.
 */
public class ConditionManager {

    private static final List<PlaceholderCondition> placeholderConditionList = new ArrayList<>();

    /**
     * Used to register a condition in the manager.
     *
     * @param placeholderCondition An instance of a placeholder condition.
     */
    public static void register(PlaceholderCondition placeholderCondition) {
        ConditionManager.placeholderConditionList.add(placeholderCondition);
    }

    /**
     * Used to get the placeholder condition list.
     *
     * @return The list of placeholder conditions.
     */
    public static List<PlaceholderCondition> getAll() {
        return ConditionManager.placeholderConditionList;
    }

    /**
     * Used to get a condition.
     * If the condition doesn't exist it will return null.
     *
     * @param conditionIdentifier The condition's identifier.
     * @return The placeholder condition requested.
     */
    public static PlaceholderCondition getCondition(String conditionIdentifier) {
        for (PlaceholderCondition condition : ConditionManager.placeholderConditionList) {
            if (Objects.equals(condition.getIdentifier(), conditionIdentifier.toUpperCase(Locale.ROOT)))
                return condition;
        }

        return null;
    }
}
