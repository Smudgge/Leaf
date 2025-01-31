package com.github.smuddgge.leaf.discord;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.LeafException;
import com.github.smuddgge.leaf.command.Command;
import com.github.smuddgge.leaf.logger.Logger;
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

public class DiscordBot extends ListenerAdapter {

    private final @Nullable JDA bot;
    private final @NotNull List<DiscordBotCommandAdapter> discordCommandList;

    public DiscordBot(@NotNull String token) {

        this.discordCommandList = new ArrayList<>();

        try {

            // Start bot with token.
            this.bot = JDABuilder.createDefault(token)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableIntents(GatewayIntent.GUILD_PRESENCES)
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(this)
                    .build()
                    .awaitReady();

            Leaf.get().getLogger().optional(Logger.Opt.DISCORD_BOT_LOAD, "[DiscordBot] Bot loaded");

        } catch (Exception exception) {
            throw new LeafException(
                    exception,
                    "new DiscordBot()",
                    "Unable to start the discord bot.",
                    "Please ensure the following:",
                    "- The discord bot token is correct.",
                    "- The discord bot has the correct &fPrivileged Gateway Intents.",
                    "- The discord bot has the correct permissions on the server."
            );
        }
    }

    public @NotNull JDA.Status getStatus() {
        if (this.bot == null) return JDA.Status.SHUTDOWN;
        return this.bot.getStatus();
    }

    public @NotNull DiscordBot registerCommand(@NotNull Command command) {
        if (this.bot == null) return this;
        if (!command.isDiscordEnabled()) return this;

        try {

            Logger logger = Leaf.get().getLogger().extend(" [DiscordBot]");
            logger.optional(Logger.Opt.DISCORD_BOT_LOAD, "[DiscordBot] &fLoading &7command " + command.getName());

            // Register the command with the bot.
            CommandCreateAction action = this.bot.upsertCommand(command.getName(), command.getDescription());

            // Add command options.
            command.getCommandType().onDiscordRegister(
                    command.getSection(),
                    action
            );

            // Create command and get snowflake.
            long snowflake = action.complete().getIdLong();

            // Create new discord command.
            DiscordBotCommandAdapter discordCommand = new DiscordBotCommandAdapter(command, snowflake);

            // Add to the listener list.
            this.discordCommandList.add(discordCommand);

            logger.optional(Logger.Opt.DISCORD_BOT_LOAD, "[DiscordBot] &Registered &7command " + command.getName());

        } catch (Exception exception) {
            throw new LeafException(
                    exception,
                    "DiscordBot.registerCommand()",
                    "Unable to register command with identifier &a" + command.getIdentifier(),
                    "Please ensure the following:",
                    "- The discord bot token is correct.",
                    "- The discord bot has the correct &fPrivileged Gateway Intents.",
                    "- The discord bot has the correct permissions on the server."
            );
        }
        return this;
    }

    public @NotNull DiscordBot removeCommands() {
        if (this.bot == null) return this;

        for (DiscordBotCommandAdapter commandAdapter : this.discordCommandList) {
            Leaf.get().getLogger().optional(Logger.Opt.DISCORD_BOT_LOAD, "[DiscordBot] Removing command " + commandAdapter.getCommand().getName());
            this.bot.deleteCommandById(commandAdapter.getSnowflake()).complete();
        }

        this.discordCommandList.clear();
        return this;
    }

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
//
//    @Override
//    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
//
//        // Check if the author is a bot.
//        if (event.getAuthor().isBot()) return;
//
//        // Loop though commands.
//        for (DiscordBotCommandAdapter discordCommand : this.discordCommandList) {
//            discordCommand.onMessage(event);
//        }
//
//        EventManager.runEvent(event);
//    }
}
