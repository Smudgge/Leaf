package com.github.smuddgge.leaf.user;

import com.github.smuddgge.leaf.discord.DiscordBotMessageAdapter;
import com.github.squishylib.common.task.TaskContainer;
import com.github.squishylib.configuration.ConfigurationSection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class DiscordBotUser extends TaskContainer {

    final SlashCommandInteractionEvent event;

    public DiscordBotUser(@NotNull SlashCommandInteractionEvent event) {
        this.event = event;
    }

    public void sendMessage(@NotNull ConfigurationSection section, @NotNull String path, @NotNull String alternative) {
        event.reply(new DiscordBotMessageAdapter(section, path, alternative)
            .buildMessage()
        ).queue();
    }

    public void sendMessage(@NotNull ConfigurationSection section, @NotNull String path, @NotNull String alternative, @NotNull DiscordBotMessageAdapter.PlaceholderParser parser) {
        event.reply(new DiscordBotMessageAdapter(section, path, alternative)
            .setParser(parser)
            .buildMessage()
        ).queue();
    }

    public void removeLastMessage(@NotNull ConfigurationSection section) {

        String messageID = event.getChannel().getLatestMessageId();

        // Get the amount of time to wait before deleting the message.
        int deleteAfter = section.getInteger("discord_bot.delete_after_seconds", -1);
        if (deleteAfter == -1) return;

        // Delete the message after the specified time.
        this.runTask(
            () -> event.getChannel().deleteMessageById(messageID).complete(),
            Duration.ofSeconds(deleteAfter),
            "delete_after"
        );
    }
}
