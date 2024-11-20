package com.github.smuddgge.leaf.placeholder.standard;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.placeholder.Placeholder;
import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VelocityVersionPlaceholder implements Placeholder {

    @Override
    public @NotNull String getIdentifier() {
        return "velocity_version";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return List.of();
    }

    @Override
    public @NotNull Type getType() {
        return Type.STANDARD;
    }

    @Override
    public @Nullable String getValue(@Nullable User user) {
        return Leaf.get().getProxyServer().getVersion().getVersion();
    }
}
