package com.github.smuddgge.leaf.discord;

import com.github.smuddgge.leaf.datatype.User;
import com.github.smuddgge.leaf.placeholders.PlaceholderManager;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to adapt configuration into a discord message.
 */
public class DiscordBotMessageAdapter {

    private final @NotNull ConfigurationSection section;
    private final @NotNull String path;
    private final @NotNull String alternative;
    private @NotNull PlaceholderParser parser;

    /**
     * Used to create a discord message.
     *
     * @param section     The configuration section where the message key is located.
     *                    This is because the message may just be a string or list.
     * @param path        The path to the message within the section.
     * @param alternative The alternative value if the message does not exist.
     */
    public DiscordBotMessageAdapter(@NotNull ConfigurationSection section, @NotNull String path, @NotNull String alternative) {
        this.section = section;
        this.path = path;
        this.alternative = alternative;
        this.parser = string -> PlaceholderManager.parse(string, null, null);
    }


    /**
     * Represents a placeholder parser.
     * Used to parse placeholders in strings.
     */
    public interface PlaceholderParser {
        /**
         * Called when a string needs to be parsed.
         *
         * @param string The instance of the string to parse.
         * @return The parsed string.
         */
        @NotNull String parsePlaceholders(@NotNull String string);
    }

    /**
     * Used to get the embeds from an embed section.
     *
     * @param section The instance of the embeds section.
     * @return The list of embeds.
     */
    public @NotNull List<MessageEmbed> getEmbedList(@NotNull ConfigurationSection section) {
        List<MessageEmbed> messageEmbedList = new ArrayList<>();

        for (String key : section.getKeys()) {
            messageEmbedList.add(this.getEmbed(section.getSection(key)));
        }

        return messageEmbedList;
    }

    /**
     * Used to get an embed given the embed section.
     * <pre>
     * embed:
     *   color: 0x00FF00
     *   title:
     *     message: "Title"
     *     url: "url"
     *   description: "Description"
     *   footer:
     *     message: "Footer"
     *     icon: "url"
     *   author:
     *     name: "Smudge"
     *     iconUrl: "url"
     *     url: "url"
     *   imageUrl: "url"
     *   thumbnailUrl: "url"
     *   timeStamp: false
     * </pre>
     *
     * @param section The configuration section
     *                representing an embed.
     * @return The instance of the embed.
     */
    public @NotNull MessageEmbed getEmbed(@NotNull ConfigurationSection section) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(section.getInteger("color", 0x00FF00));

        if (section.getKeys().contains("title")) {
            embedBuilder.setTitle(
                    this.parse(section.getSection("title").getString("message", "")),
                    this.parse(section.getSection("title").getString("url", null))
            );
        }

        if (section.getKeys().contains("description")) {
            embedBuilder.setDescription(this.parse(section.getString("description", "")));
        }

        if (section.getKeys().contains("footer")) {
            embedBuilder.setFooter(
                    this.parse(section.getSection("footer").getString("message", "")),
                    this.parse(section.getSection("footer").getString("icon", null))
            );
        }

        if (section.getKeys().contains("author")) {
            embedBuilder.setAuthor(
                    this.parse(section.getSection("author").getString("name", "")),
                    this.parse(section.getSection("author").getString("url", null)),
                    this.parse(section.getSection("author").getString("iconUrl", null))
            );
        }

        if (section.getKeys().contains("imageUrl")) {
            embedBuilder.setImage(this.parse(section.getString("imageUrl", null)));
        }

        if (section.getKeys().contains("thumbnailUrl")) {
            embedBuilder.setThumbnail(this.parse(section.getString("thumbnailUrl", null)));
        }

        if (section.getBoolean("timeStamp", false)) {
            embedBuilder.setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis()));
        }

        return embedBuilder.build();
    }

    /**
     * Used to set the placeholder parser used
     * in parsing messages.
     *
     * @param parser The instance of the parser.
     * @return This instance.
     */
    public @NotNull DiscordBotMessageAdapter setParser(PlaceholderParser parser) {
        this.parser = parser;
        return this;
    }

    /**
     * Used to set the parser to a parser
     * in terms of a user.
     *
     * @param user The instance of a user.
     * @return This instance.
     */
    public @NotNull DiscordBotMessageAdapter setParser(@NotNull User user) {
        this.parser = string -> PlaceholderManager.parse(string, null, user);
        return this;
    }

    /**
     * Used to build a message from the section.
     * <pre>
     * section:
     *   path: "message"
     *
     * section:
     *   path:
     *     message: "message"
     *     embeds:
     *       1: ...
     *       2: ...
     * </pre>
     *
     * @return The instance of the message.
     */
    public @NotNull Message buildMessage() {
        MessageBuilder builder = new MessageBuilder();

        // Check if the message is not a section.
        if (this.section.getSection(this.path).getMap().isEmpty()) {

            // Adapt the message to a string.
            String string = this.section.getAdaptedString(this.path, "\n", this.alternative);

            // Set content and build.
            builder.setContent(this.parser.parsePlaceholders(string));
            return builder.build();
        }

        // Otherwise it is a message section.
        ConfigurationSection messageSection = this.section.getSection(this.path);

        // Check if the section contains a message.
        if (messageSection.getKeys().contains("message")) {
            builder.setContent(this.parser.parsePlaceholders(
                    messageSection.getAdaptedString("message", "\n", this.alternative)
            ));
        }

        // Check if the section contains embeds.
        if (messageSection.getKeys().contains("embeds")) {
            builder.setEmbeds(this.getEmbedList(messageSection.getSection("embeds")));
        }

        // Build message.
        return builder.build();
    }

    /**
     * Used to quickly parse a string.
     *
     * @param string The string to parse.
     * @return The parsed string.
     */
    private @Nullable String parse(@Nullable String string) {
        if (string == null) return null;
        return this.parser.parsePlaceholders(string);
    }
}
