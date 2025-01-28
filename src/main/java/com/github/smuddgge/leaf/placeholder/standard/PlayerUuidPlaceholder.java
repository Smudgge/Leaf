package com.github.smuddgge.leaf.placeholder.standard;

import com.github.smuddgge.leaf.placeholder.Placeholder;
import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PlayerUuidPlaceholder implements Placeholder {

    @Override
    public @NotNull List<String> getNameList() {
        return List.of("uuid", "player_uuid");
    }

    @Override
    public @NotNull Type getType() {
        return Type.STANDARD;
    }

    @Override
    public @Nullable String getValue(@Nullable User user) {
        return user != null ? user.getUuid().toString() : null;
    }
}
