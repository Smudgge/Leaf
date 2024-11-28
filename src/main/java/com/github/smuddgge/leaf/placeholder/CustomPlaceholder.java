package com.github.smuddgge.leaf.placeholder;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.logger.Logger;
import com.github.smuddgge.leaf.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a placeholder within the placeholder directory.
 */
public class CustomPlaceholder implements Placeholder {

    private final @NotNull String identifier;
    private final @Nullable Condition condition;

    /**
     * @param identifier The configuration key within the placeholder directory.
     */
    public CustomPlaceholder(@NotNull String identifier) {
        this.identifier = identifier;
        this.condition = Condition.of(Leaf.get().getPlaceholdersDirectory()
                .getSection(this.identifier)
                .getString("condition", null)
        );
    }

    private void errorIfConditionIsNull() {
        if (this.condition == null) {
            final String conditionIdentifier = Leaf.get().getPlaceholdersDirectory()
                    .getSection(this.identifier)
                    .getString("condition", "null")
                    .split(":")[0];

            Logger logger = Leaf.get().getLogger().extend(" &7[Placeholders]");
            logger.warn("Could not find a condition type that matches " + conditionIdentifier
                    + " for placeholder with identifier " + this.identifier);
        }
    }

    public @NotNull String getIdentifier() {
        return this.identifier;
    }

    @Override
    public @NotNull List<String> getNameList() {
        List<String> nameList = new ArrayList<>();
        nameList.add(this.identifier);

        final List<String> aliases = Leaf.get().getPlaceholdersDirectory()
                .getSection(this.identifier)
                .getListString("aliases", new ArrayList<>());

        if (aliases.isEmpty()) return nameList;
        nameList.addAll(aliases);
        return nameList;
    }

    @Override
    public @NotNull Type getType() {
        return Type.CUSTOM;
    }

    @Override
    public @Nullable String getValue(@Nullable User user) {

        // Is the placeholder hard coded to a value?
        final String value = Leaf.get().getPlaceholdersDirectory()
                .getString(this.identifier, null);
        if (value != null) return value;

        // Is there a condition?
        if (this.condition == null) {
            this.errorIfConditionIsNull();
            return null;
        }

        return this.condition.getValue(
                Leaf.get().getPlaceholdersDirectory().getSection(identifier),
                user,
                this.identifier
        );
    }
}
