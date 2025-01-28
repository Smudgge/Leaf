package com.github.smuddgge.leaf.placeholder;

import com.github.smuddgge.leaf.placeholder.condition.MatchCondition;
import com.github.smuddgge.leaf.placeholder.condition.PermissionCondition;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Condition {

    @NotNull String getConditionIdentifier();

    @Nullable String getValue(@NotNull ConfigurationSection section,
                              @Nullable User user,
                              @NotNull String identifier
    );

    static @Nullable Condition of(final @Nullable String condition) {
        if (condition == null) return null;
        if (condition.contains("MATCH")) return new MatchCondition();
        if (condition.contains("PERMISSION")) return new PermissionCondition();
        return null;
    }
}
