package com.github.smuddgge.leaf.placeholder.standard;

import com.github.smuddgge.leaf.placeholder.Placeholder;
import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerServerPlaceholder implements Placeholder {

    @Override
    public @NotNull String getIdentifier() {
        return "server";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return List.of("player_server");
    }

    @Override
    public @NotNull Type getType() {
        return Type.STANDARD;
    }

    @Override
    public @Nullable String getValue(@Nullable User user) {
        // Is user and server present?
        return user != null && user.getServer() != null
                ? user.getServer().getServerInfo().getName()
                : null;
    }
}
