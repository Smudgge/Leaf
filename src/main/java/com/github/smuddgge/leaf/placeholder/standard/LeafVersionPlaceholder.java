package com.github.smuddgge.leaf.placeholder.standard;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.placeholder.Placeholder;
import com.github.smuddgge.leaf.user.User;
import com.velocitypowered.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LeafVersionPlaceholder implements Placeholder {

    @Override
    public @NotNull String getIdentifier() {
        return "leaf_version";
    }

    @Override
    public @NotNull List<String> getAliases() {
        return List.of("version");
    }

    @Override
    public @NotNull Type getType() {
        return Type.STANDARD;
    }

    @Override
    public @Nullable String getValue(@Nullable User user) {
        return Leaf.class.getAnnotation(Plugin.class).version();
    }
}
