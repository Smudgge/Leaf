package com.github.smuddgge.leaf.placeholder;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.LeafException;
import com.github.smuddgge.leaf.dependency.MiniPlaceholdersAdapter;
import com.github.smuddgge.leaf.dependency.MiniPlaceholdersDependency;
import com.github.smuddgge.leaf.logger.Logger;
import com.github.smuddgge.leaf.user.PlayerUser;
import com.github.smuddgge.leaf.user.User;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Acts as the entry point for leaf placeholders.
 */
public class PlaceholderManager {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#[0-9a-fA-F]{6}[^>]");

    private final List<Placeholder> placeholderList;

    public PlaceholderManager() {
        this.placeholderList = new ArrayList<>();
    }

    public @NotNull List<Placeholder> getPlaceholders() {
        return this.placeholderList;
    }

    public @NotNull PlaceholderManager register(@NotNull final Placeholder placeholder) {
        Logger logger = Leaf.get().getLogger().extend(" &7[Placeholders]");

        // Has the placeholder already been registered?
        if (this.contains(placeholder)) {
            logger.info("&eAttempted to register the placeholder &f%placeholder% &ebut it already exists! Make sure there are no identifier or alias duplicates."
                    .replace("%placeholder%", placeholder.asString())
            );
            return this;
        }

        this.placeholderList.add(placeholder);
        logger.info("&aRegistered &7" + placeholder.asString());
        return this;
    }

    public @NotNull PlaceholderManager unregister(@NotNull final Placeholder placeholder) {
        this.placeholderList.remove(placeholder);
        Logger logger = Leaf.get().getLogger().extend(" &7[Placeholders]");
        logger.info("&cUnregistered &7%placeholder%"
                .replace("%placeholder%", placeholder.asString())
        );
        return this;
    }

    /**
     * Is the placeholder already registered?
     * <p>
     * Checks all identifiers.
     *
     * @param placeholder The placeholder to check.
     * @return True if it is already registered.
     */
    private boolean contains(@NotNull final Placeholder placeholder) {
        // For each placeholder
        for (Placeholder temp : this.placeholderList) {
            if (temp.overlaps(placeholder)) return true;
        }
        return false;
    }

    private boolean stringContainsPlaceholder(@NotNull String string) {
        for (Placeholder placeholder : this.placeholderList) {
            if (placeholder.isIn(string)) return true;
        }
        return false;
    }

    public @NotNull String parseLeafPlaceholders(@NotNull String string, @Nullable User user) {
        return this.parseLeafPlaceholders(string, user, 20, 0);
    }

    private @NotNull String parseLeafPlaceholders(@NotNull String string, @Nullable User user, int maxDepth, int currentDepth) {

        // Check if a placeholder may be getting replaced with its self.
        if (maxDepth <= currentDepth) {
            Logger logger = Leaf.get().getLogger().extend(" &7[Placeholders]");
            logger.warn("&eCould not fully convert placeholders in a string." +
                    "A placeholder in the string kept getting replaced with its self. " +
                    "result_string: &f" + string + ", depth: " + currentDepth + ", max_depth: " + maxDepth);
            return string;
        }

        // Parse the placeholders.
        for (Placeholder placeholder : this.placeholderList) {

            // Does the string not contain one of the placeholder's identifiers?
            if (!placeholder.isIn(string)) continue;

            string = placeholder.parse(string, user);
        }

        // Check if there are still placeholders to parse.
        if (this.stringContainsPlaceholder(string))
            return this.parseLeafPlaceholders(string, user, maxDepth, currentDepth + 1);
        return string;
    }

    private @NotNull Component parseMiniMessage(String message, @Nullable Player player) {

        // Check if the mini placeholders dependency is disabled.
        if (!MiniPlaceholdersDependency.isEnabled()) {
            return MiniMessage.miniMessage().deserialize(message);
        }

        return MiniPlaceholdersAdapter.parseMiniPlaceholders(message, player);
    }

    private @NotNull String convertLegacyHexToMiniMessage(@NotNull String message) {
        final StringBuilder builder = new StringBuilder(
                message.replace("&#", "<#")
        );

        final Matcher matcher = HEX_PATTERN.matcher(builder);

        // Add the end bracket for a mini message.
        matcher.results().forEach(
                result -> builder.insert(result.start() + 8, ">")
        );

        return builder.toString();
    }

    private @NotNull String convertLegacyToMiniMessage(@NotNull String message) {
        return this.convertLegacyHexToMiniMessage(message)
                .replace("§", "&") // Ensure there are no legacy symbols.
                .replace("&0", "<reset><black>")
                .replace("&1", "<reset><dark_blue>")
                .replace("&2", "<reset><dark_green>")
                .replace("&3", "<reset><dark_aqua>")
                .replace("&4", "<reset><dark_red>")
                .replace("&5", "<reset><dark_purple>")
                .replace("&6", "<reset><gold>")
                .replace("&7", "<reset><gray>")
                .replace("&8", "<reset><dark_gray>")
                .replace("&9", "<reset><blue>")
                .replace("&a", "<reset><green>")
                .replace("&b", "<reset><aqua>")
                .replace("&c", "<reset><red>")
                .replace("&d", "<reset><light_purple>")
                .replace("&e", "<reset><yellow>")
                .replace("&f", "<reset><white>")
                .replace("&k", "<obf>")
                .replace("&l", "<b>")
                .replace("&m", "<st>")
                .replace("&n", "<u>")
                .replace("&o", "<i>")
                .replace("&r", "<reset>");
    }

    public @NotNull Component parse(@NotNull String string) {
        return this.parse(string, (User) null);
    }

    public @NotNull Component parse(@NotNull String string, @Nullable User user) {
        if (user instanceof PlayerUser player) return this.parse(string, player.getPlayer());
        return this.parse(string, (Player) null);
    }

    public @NotNull Component parse(@NotNull String string, @Nullable Player player) {
        try {

            String miniMessageString = this.convertLegacyToMiniMessage(string);
            String leafPlaceholderString = this.parseLeafPlaceholders(miniMessageString, player != null ? new PlayerUser(player) : null);
            String miniMessageString2 = this.convertLegacyToMiniMessage(leafPlaceholderString);
            return this.parseMiniMessage(miniMessageString2, player);

        } catch (Exception exception) {
            throw new LeafException(exception, "PlaceholderManager.parse(string, user)", "Failed to convert message &f\"" + string + "\"", null);
        }
    }

    public @NotNull Component parseColorsOnly(@NotNull String string) {
        try {

            String legacyString = this.convertLegacyToMiniMessage(string);
            return this.parseMiniMessage(legacyString, null);

        } catch (Exception exception) {
            throw new LeafException(exception, "PlaceholderManager.parse(string)", "Failed to convert message &f\"" + string + "\"", null);
        }
    }
}
