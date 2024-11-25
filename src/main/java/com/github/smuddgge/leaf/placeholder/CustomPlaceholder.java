package com.github.smuddgge.leaf.placeholder;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.user.User;
import com.github.squishylib.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Represents a placeholder within the placeholder directory.
 */
public class CustomPlaceholder implements Placeholder {

    public static class SectionOrString {

        public final String string;
        public final ConfigurationSection section;

        public SectionOrString(final String string) {
            this.string = string;
            this.section = null;
        }

        public SectionOrString(final ConfigurationSection section) {
            this.section = section;
            this.string = null;
        }
    }

    private final @NotNull String identifier;
    /**
     * @param identifier The configuration key within the placeholder directory.
     */
    public CustomPlaceholder(@NotNull String identifier) {
        this.identifier = identifier;
    }

    /**
     * Gets the custom placeholder from the configuration.
     * This could ether be a string or a configuration section.
     *
     * @return The section or string.
     */
    public @NotNull SectionOrString getSectionOrString() {
        final String string = Leaf.get().getPlaceholdersDirectory().getString(this.identifier, null);

        if (string == null) {
            return new SectionOrString(Leaf.get().getPlaceholdersDirectory().getSection(this.identifier));
        }

        return new SectionOrString(string);
    }

    @Override
    public @NotNull List<String> getNameList() {
        List<String> nameList = new ArrayList<>();
        nameList.add(this.identifier);

        final SectionOrString sectionOrString = this.getSectionOrString();
        if (sectionOrString.section != null) nameList.addAll(
                sectionOrString.section.getListString("aliases", new ArrayList<>())
        );
        
        return nameList;
    }

    @Override
    public @NotNull Type getType() {
        return Type.CUSTOM;
    }

    @Override
    public @Nullable String getValue(@Nullable User user) {
        return "";
    }
}
