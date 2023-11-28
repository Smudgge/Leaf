package com.github.smuddgge.leaf.discord;

import com.github.smuddgge.leaf.MessageManager;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.events.EventManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a discord bot.
 * Used to send messages and register events
 * with a jda bot instance.
 */
public class DiscordBot extends ListenerAdapter {

    private @Nullable JDA bot;
    private @NotNull List<DiscordBotCommandAdapter> discordCommandList;

    /**
     * Used to create and connect to
     * the discord bot.
     *
     * @param token The bot token.
     */
    public DiscordBot(@Nullable String token) {
        this.discordCommandList = new ArrayList<>();

        // Check if the token is null.
        if (token == null || token.isEmpty()) {
            MessageManager.log("&7Discord bot disabled.");
            return;
        }

        try {

            // Start bot with token.
            this.bot = JDABuilder.createDefault(token)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .addEventListeners(this)
                    .build();

            MessageManager.log("&7[Discord Bot] &aEnabled");

        } catch (Exception exception) {
            MessageManager.warn("Unable to login to the discord bot. Please ensure the following:");
            MessageManager.warn("- The discord bot token is correct.");
            MessageManager.warn("- The discord bot has the correct &fPrivileged Gateway Intents.");
            MessageManager.warn("- The discord bot has the correct permissions on the server.");
            MessageManager.warn("Exception for debugging:");
            exception.printStackTrace();
        }
    }

    /**
     * Used to get the bot status.
     *
     * @return The bots status.
     */
    public @NotNull JDA.Status getStatus() {
        if (this.bot == null) return JDA.Status.SHUTDOWN;
        return this.bot.getStatus();
    }

    /**
     * Used to register a command with the discord bot.
     * Checks if the bot is enabled.
     * Checks if the command is a discord command.
     *
     * @param command The instance of the command.
     * @return This instance.
     */
    public @NotNull DiscordBot registerCommand(@NotNull Command command) {
        if (this.bot == null) return this;
        if (!command.isDiscordEnabled()) return this;

        try {
            // Register with bot.
            CommandCreateAction action = this.bot.upsertCommand(command.getName(), command.getDescription());

            // Create new discord command.
            DiscordBotCommandAdapter discordCommand = new DiscordBotCommandAdapter(command, action);
            MessageManager.log("&7[Discord Bot] &fLoading &7command : " + command.getName());
            action.complete();

            // Log.
            MessageManager.log("&7[Discord Bot] &aRegistered &7command : " + command.getName());

            // Add to the listener list.
            this.discordCommandList.add(discordCommand);

        } catch (Exception exception) {
            MessageManager.warn("Unable to load command " + command.getName() + ". Please ensure the following:");
            MessageManager.warn("- The discord bot token is correct.");
            MessageManager.warn("- The discord bot has the correct &fPrivileged Gateway Intents.");
            MessageManager.warn("- The discord bot has the correct permissions on the server.");
            MessageManager.warn("Exception for debugging:");
            exception.printStackTrace();
        }
        return this;
    }

    /**
     * Removes all the commands from the discord bot.
     *
     * @return This instance.
     */
    public @NotNull DiscordBot removeCommands() {
        if (this.bot == null) return this;

        for (DiscordBotCommandAdapter commandAdapter : this.discordCommandList) {
            MessageManager.log("&7[Discord Bot] &eRemoving &7command : " + commandAdapter.getCommand().getName());
            this.bot.deleteCommandById(commandAdapter.getSnowflake()).complete();
        }

        this.discordCommandList = new ArrayList<>();
        return this;
    }

    /**
     * Used to shut down the bot.
     *
     * @return The instance of the discord bot.
     */
    public @NotNull DiscordBot shutdown() {
        if (this.bot == null) return this;
        this.bot.shutdownNow();
        return this;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String name = event.getName();

        for (DiscordBotCommandAdapter discordCommand : this.discordCommandList) {
            if (!discordCommand.getCommand().getName().equals(name)) continue;
            discordCommand.execute(event);
            return;
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        for (DiscordBotCommandAdapter discordCommand : this.discordCommandList) {
            discordCommand.onMessage(event);
        }

        EventManager.runEvent(event);
    }
}
