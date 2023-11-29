package com.github.smuddgge.leaf.discord;

import com.github.smuddgge.leaf.task.TaskContainer;
import com.github.smuddgge.squishyconfiguration.interfaces.ConfigurationSection;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class DiscordBotRemoveMessageHandler extends TaskContainer {

    /**
     * Used to create a discord bot remove message handler.
     *
     * @param discordBotSection The discord bot config section.
     * @param event             The slash event.
     */
    public DiscordBotRemoveMessageHandler(@NotNull ConfigurationSection discordBotSection,
                                          @NotNull SlashCommandInteractionEvent event) {

        String messageID = event.getChannel().getLatestMessageId();

        // Get the amount of time to wait before deleting the message.
        int deleteAfter = discordBotSection.getInteger("delete_after_seconds", -1);
        if (deleteAfter == -1) return;

        // Delete the message after the specified time.
        this.runTask(
                () -> event.getChannel().deleteMessageById(messageID).complete(),
                Duration.ofSeconds(deleteAfter),
                "delete_after"
        );
    }
}
