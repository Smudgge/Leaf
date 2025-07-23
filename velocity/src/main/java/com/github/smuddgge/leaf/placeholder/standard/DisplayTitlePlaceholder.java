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

public class DisplayTitlePlaceholder implements Placeholder {

    @Override
    public @NotNull List<String> getNameList() {
        return List.of("title");
    }

    @Override
    public @NotNull Type getType() {
        return Type.STANDARD;
    }

    @Override
    public @Nullable String getValue(@Nullable User user, @NotNull String string) {
        DisplayTitlePlaceholder.sendTitle(user, string, TitlePart.TITLE);
        return "";
    }

    public static void sendTitle(@Nullable User user, @NotNull String string, @NotNull TitlePart titlePart) {
        try {

            if (!(user instanceof PlayerUser player)) return;

            final int messageStart = string.indexOf("'");
            final int messageEnd = string.lastIndexOf("'");

            final String chopped = string.substring(messageEnd);
            final int durationStart = chopped.indexOf(" ");
            final int durationEnd = chopped.indexOf("s");
            final String durationString = chopped.substring(durationStart + 1, durationEnd);

            final String message = string.substring(messageStart + 1, messageEnd);

            Duration duration;
            try {
                duration = Duration.ofSeconds(Long.parseLong(durationString));
            } catch (Exception exception) {
                throw new LeafException(exception,
                        "DisplayTitlePlaceholder.sendTitle()",
                        "Failed to convert duration string into seconds.",
                        "Placeholder that threw an error: " + string,
                        "Example of a correct title placeholder: <title: 'hi' 4s>",
                        "&7",
                        "message_start: " + messageStart,
                        "message_end: " + messageEnd,
                        "chopped: " + chopped,
                        "duration_start: " + durationStart,
                        "duration_end: " + durationEnd,
                        "duration_string: " + durationString
                );
            }

            final Component parsedMessage = Leaf.get().getPlaceholderManager().parse(message, user);

            final Title title = Title.title(
                    parsedMessage,
                    Leaf.get().getPlaceholderManager().parse("", user),
                    Title.Times.times(
                            Duration.ofMillis(500),
                            duration,
                            Duration.ofMillis(500)
                    )
            );

            player.getPlayer().sendTitlePart(TitlePart.TIMES, Objects.requireNonNull(title.times()));
            player.getPlayer().sendTitlePart(titlePart, title.title());

        } catch (final Exception exception) {
            throw new LeafException(exception,
                    "DisplayTitlePlaceholder.sendTitle()",
                    "Failed to parse a title placeholder.",
                    "Placeholder that threw an error: " + string,
                    "Example of a correct title placeholder: &6<title: 'hi' 4s>"
            );
        }
    }
}
