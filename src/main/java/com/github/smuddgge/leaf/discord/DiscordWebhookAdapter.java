package com.github.smuddgge.leaf.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.console.Console;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a discord webhook.
 * Uses the webhook from {@link club.minnced.discord.webhook.WebhookClient}
 * to create a link to discord.
 * <p>
 * <h2>Example</h2>
 * <pre>
 * discord_webhook:
 *   url: "url"
 *   message:
 *     username: "<player>"
 *     avatar: "https://crafatar.com/avatars/<uuid>?overlay"
 *     message: "Hello there!"
 *   embed:
 *      color: 0x00FF00
 *      title:
 *        message: "Title"
 *        url: "url"
 *      description: "Description"
 *      footer:
 *        message: "Footer"
 *        icon: "url"
 *      author:
 *        name: "Smudge"
 *        iconUrl: "url"
 *        url: "url"
 *      imageUrl: "url"
 *      thumbnailUrl: "url"
 * </pre>
 */
public class DiscordWebhookAdapter {

    private final @NotNull ConfigurationSection section;
    private @NotNull PlaceholderParser placeholderParser;

    /**
     * Used to create a discord webhook adapter.
     *
     * @param section The instance of the configuration section.
     *                This section is marked with the key "discord_webhook"
     */
    public DiscordWebhookAdapter(@NotNull ConfigurationSection section) {
        this.section = section;
        this.placeholderParser = string -> string;
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
     * Used to get the webhook url from the configuration section.
     *
     * @return The webhook url.
     */
    public @Nullable String getUrl() {
        String url = this.section.getString("url", null);
        if (url == null) {
            Console.warn("Attempted to get discord webhook url, but was null.");
            return null;
        }
        return url;
    }

    /**
     * Used to set the placeholder parser.
     * Lets you replace placeholders in the strings used.
     *
     * @param placeholderParser The instance of the placeholder parser.
     * @return This instance.
     */
    public @NotNull DiscordWebhookAdapter setPlaceholderParser(@NotNull PlaceholderParser placeholderParser) {
        this.placeholderParser = placeholderParser;
        return this;
    }

    /**
     * Used to send the message contained in
     * the configuration section.
     */
    public @NotNull DiscordWebhookAdapter send() {
        try {
            if (this.getUrl() == null) return this;
            WebhookClientBuilder builder = new WebhookClientBuilder(this.getUrl());
            WebhookClient client = builder.build();
            WebhookMessageBuilder messageBuilder = new WebhookMessageBuilder();

            // Add message.
            if (this.section.getKeys().contains("message")) {
                this.sendMessage(messageBuilder);
            }

            // Add embed.
            if (this.section.getKeys().contains("embed")) {
                this.sendEmbed(messageBuilder);
            }

            client.send(messageBuilder.build());
            client.close();
            return this;

        } catch (Exception exception) {
            Console.warn("Unable to send web hook message to url = " + this.getUrl());
            exception.printStackTrace();
            return this;
        }
    }

    /**
     * Used to send the message though the webhook.
     *
     * <pre>
     * message:
     *   username: "<player>"
     *   avatar: "https://crafatar.com/avatars/<uuid>?overlay"
     *   message: "Hello there!"
     * </pre>
     */
    private void sendMessage(@NotNull WebhookMessageBuilder messageBuilder) {
        ConfigurationSection messageSection = this.section.getSection("message");

        // Build message.
        messageBuilder.setUsername(this.parse(messageSection.getString("username")));
        messageBuilder.setAvatarUrl(this.parse(messageSection.getString("avatar")));
        messageBuilder.setContent(this.parse(messageSection.getString("message")));
    }

    /**
     * Used to send the embed though the webhook.
     *
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
     * </pre>
     */
    private void sendEmbed(@NotNull WebhookMessageBuilder messageBuilder) {
        ConfigurationSection embedSection = this.section.getSection("embed");

        // Build embed.
        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
        embedBuilder.setColor(embedSection.getInteger("color", 0x00FF00));

        if (embedSection.getKeys().contains("title")) {
            embedBuilder.setTitle(new WebhookEmbed.EmbedTitle(
                    this.parse(embedSection.getSection("title").getString("message", "")),
                    this.parse(embedSection.getSection("title").getString("url", null))
            ));
        }

        if (embedSection.getKeys().contains("description")) {
            embedBuilder.setDescription(this.parse(embedSection.getString("description", "")));
        }

        if (embedSection.getKeys().contains("footer")) {
            embedBuilder.setFooter(new WebhookEmbed.EmbedFooter(
                    this.parse(embedSection.getSection("footer").getString("message", "")),
                    this.parse(embedSection.getSection("footer").getString("icon", null))
            ));
        }

        if (embedSection.getKeys().contains("author")) {
            embedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor(
                    this.parse(embedSection.getSection("author").getString("name", "")),
                    this.parse(embedSection.getSection("author").getString("iconUrl", null)),
                    this.parse(embedSection.getSection("author").getString("url", null))
            ));
        }

        if (embedSection.getKeys().contains("imageUrl")) {
            embedBuilder.setImageUrl(this.parse(embedSection.getString("imageUrl", null)));
        }

        if (embedSection.getKeys().contains("thumbnailUrl")) {
            embedBuilder.setThumbnailUrl(this.parse(embedSection.getString("thumbnailUrl", null)));
        }

        messageBuilder.addEmbeds(embedBuilder.build());
    }

    /**
     * Used to quickly parse a string.
     *
     * @param string The string to parse.
     * @return The parsed string.
     */
    private @Nullable String parse(@Nullable String string) {
        if (string == null) return null;
        return this.placeholderParser.parsePlaceholders(string);
    }
}
