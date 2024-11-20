package com.github.smuddgge.leaf.placeholder.standard;

import com.github.smuddgge.leaf.placeholder.Placeholder;
import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerPingPlaceholder implements Placeholder {

    @Override
    public @NotNull List<String> getIdentifierList() {
        return List.of("player_ping", "ping");
    }

    @Override
    public @NotNull Type getType() {
        return Type.STANDARD;
    }

    @Override
    public @Nullable String getValue(@Nullable User user) {
        return user != null ? String.valueOf(user.getPing()) : null;
    }
}
