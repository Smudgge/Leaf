package com.github.smuddgge.leaf.placeholder;

import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Condition {

    @NotNull String getIdentifier();

    @Nullable String getValue(@NotNull ConfigurationSection section, @Nullable User user);
}
