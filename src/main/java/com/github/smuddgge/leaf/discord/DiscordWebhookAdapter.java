package com.github.smuddgge.leaf.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.smuddgge.leaf.exception.LeafException;
import com.github.smuddgge.leaf.task.TaskContainer;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import com.github.smuddgge.squishydatabase.console.Console;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
 *      timeStamp: false
 * </pre>
 */
public class DiscordWebhookAdapter extends TaskContainer {

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
        @NotNull
        String parsePlaceholders(@NotNull String string);
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

            // Send the message.
            CompletableFuture<ReadonlyMessage> completableMessage = client.send(messageBuilder.build());

            // Check if the message should be deleted.
            if (this.section.getInteger("delete_after_seconds", -1) >= 0) {

                // Thread it.
                this.runTask(() -> {
                    try {

                        // Wait for complete.
                        while (!completableMessage.isDone()) {
                            Thread.sleep(200);
                        }

                        // Delete the message.
                        client.delete(completableMessage.get().getId());

                        // Close the client.
                        client.close();

                    } catch (InterruptedException | ExecutionException exception) {
                        throw new LeafException(exception);
                    }

                }, Duration.ofSeconds(this.section.getInteger("delete_after", -1)), "delete");
                return this;
            }

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
     *   timeStamp: false
     * </pre>
     */
    private void sendEmbed(@NotNull WebhookMessageBuilder messageBuilder) {
        ConfigurationSection embedSection = this.section.getSection("embed");

        // Build embed.
        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
        embedBuilder.setColor(embedSection.getInteger("color", 0x00FF00));

        if (embedSection.getKeys().contains("title")) {
            embedBuilder.setTitle(new WebhookEmbed.EmbedTitle(
                    this.parse(embedSection.getSection("title").getAdaptedString("message", "\n", "")),
                    this.parse(embedSection.getSection("title").getAdaptedString("url", "\n", null))
            ));
        }

        if (embedSection.getKeys().contains("description")) {
            embedBuilder.setDescription(this.parse(embedSection.getAdaptedString("description", "\n", "")));
        }

        if (embedSection.getKeys().contains("footer")) {
            embedBuilder.setFooter(new WebhookEmbed.EmbedFooter(
                    this.parse(embedSection.getSection("footer").getAdaptedString("message", "\n", "")),
                    this.parse(embedSection.getSection("footer").getAdaptedString("icon", "\n", null))
            ));
        }

        if (embedSection.getKeys().contains("author")) {
            embedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor(
                    this.parse(embedSection.getSection("author").getAdaptedString("name", "\n", "")),
                    this.parse(embedSection.getSection("author").getAdaptedString("iconUrl", "\n", null)),
                    this.parse(embedSection.getSection("author").getAdaptedString("url", "\n", null))
            ));
        }

        if (embedSection.getKeys().contains("imageUrl")) {
            embedBuilder.setImageUrl(this.parse(embedSection.getAdaptedString("imageUrl", "\n", null)));
        }

        if (embedSection.getKeys().contains("thumbnailUrl")) {
            embedBuilder.setThumbnailUrl(this.parse(embedSection.getAdaptedString("thumbnailUrl", "\n", null)));
        }

        if (embedSection.getBoolean("timeStamp", false)) {
            embedBuilder.setTimestamp(Instant.ofEpochMilli(System.currentTimeMillis()));
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
