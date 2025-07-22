package com.github.smuddgge.leaf.placeholder.standard;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.LeafException;
import com.github.smuddgge.leaf.placeholder.Placeholder;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.smuddgge.leaf.user.User;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public class DisplaySubTitlePlaceholder implements Placeholder {

    @Override
    public @NotNull List<String> getNameList() {
        return List.of("subtitle");
    }

    @Override
    public @NotNull Type getType() {
        return Type.STANDARD;
    }

    @Override
    public @Nullable String getValue(@Nullable User user, @NotNull String string) {
        DisplayTitlePlaceholder.sendTitle(user, string, TitlePart.SUBTITLE);
        return "";
    }
}
