package com.github.smuddgge.leaf.placeholder.standard;

import com.github.smuddgge.leaf.placeholder.Placeholder;
import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerVanishedPlaceholder implements Placeholder {

    @Override
    public @NotNull String getIdentifier() {
        return "player_vanished";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return List.of("vanished");
    }

    @Override
    public @NotNull Type getType() {
        return Type.STANDARD;
    }

    @Override
    public @Nullable String getValue(@Nullable User user) {
        return user != null ? (user.isVanished() ? "true" : "false") : null;
    }
}
