package com.github.smuddgge.leaf.discord;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.commands.Command;
import com.github.smuddgge.leaf.commands.CommandStatus;
import com.github.smuddgge.leaf.database.tables.CommandCooldownTable;
import com.github.smuddgge.leaf.database.tables.CommandLimitTable;
import com.github.smuddgge.leaf.utility.DiscordUtility;
import com.github.smuddgge.squishydatabase.console.Console;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapts a leaf command to a discord command.
 */
public class DiscordBotCommandAdapter {

    private final long snowflake;
    private final @NotNull Command command;

    /**
     * Used to create the discord command adapter.
     *
     * @param command The instance of the command.
     */
    public DiscordBotCommandAdapter(@NotNull Command command, @NotNull CommandCreateAction action) {
        this.command = command;

        // Add options with the command type.
        this.getCommand().getBaseCommandType().onDiscordRegister(
                this.getCommand().getSection(),
                action
        );

        this.snowflake = action.complete().getIdLong();
    }

    /**
     * Used to get the instance of the command.
     *
     * @return The instance of the command.
     */
    public @NotNull Command getCommand() {
        return this.command;
    }

    /**
     * Used to get the commands snowflake id.
     *
     * @return The snowflake id.
     */
    public long getSnowflake() {
        return this.snowflake;
    }

    /**
     * Used to get the allowed channels.
     * If the list is empty all channels are allowed.
     *
     * @return The list of allowed channels.
     */
    public @NotNull List<String> getAllowedChannels() {
        return this.getCommand().getSection().getListString("discord_bot.allowed_channels");
    }

    /**
     * Used to get the list of discord permission
     * required to execute this command.
     *
     * @return The list of permissions.
     */
    public @NotNull List<Permission> getDiscordPermissions() {
        List<Permission> permissionList = new ArrayList<>();

        for (String permission : this.getCommand().getSection().getListString("discord_bot.permissions")) {
            try {
                permissionList.add(Permission.valueOf(permission.toUpperCase()));
            } catch (Exception exception) {
                Console.warn(permission + " was removed. It is not a discord permission.");
            }
        }

        return permissionList;
    }

    /**
     * Used to get the list of discord roles.
     *
     * @return The list of discord roles.
     */
    public @NotNull List<String> getDiscordRoles() {
        return this.getCommand().getSection().getListString("discord_bot.roles", new ArrayList<>());
    }

    /**
     * Used to check if a channel is allowed
     * for this command.
     *
     * @param channelSnowflake The channel's snowflake id.
     * @return True if the channel is allowed.
     */
    public boolean isAllowed(@NotNull String channelSnowflake) {
        List<String> allowedChannels = this.getAllowedChannels();
        if (allowedChannels.isEmpty()) return true;
        return this.getAllowedChannels().contains(channelSnowflake);
    }

    /**
     * Used to check if a member has the discord permissions.
     *
     * @param member The instance of the member to check permissions.
     * @return True if the member has permission to run this command.
     */
    public boolean hasPermission(@NotNull Member member) {
        List<Permission> permissionList = this.getDiscordPermissions();
        if (permissionList.isEmpty()) return true;
        return member.hasPermission(permissionList);
    }

    /**
     * Used to check if a member has a role from the list
     * specified in this command.
     *
     * @param member The instance of the member.
     * @return True if the member has a role from the list
     * or if there are no roles to check for.
     */
    public boolean hasRoleFromList(@NotNull Member member) {
        return DiscordUtility.hasRoleFromList(this.getDiscordRoles(), member);
    }

    /**
     * Used to check if the member is limited by the
     * number of times they can execute the command.
     *
     * @param member The instance of the member.
     * @return This instance.
     */
    public boolean isLimited(@NotNull Member member) {

        // Get the command limit for this command.
        int limit = this.command.getSection()
                .getSection("discord_bot")
                .getInteger("limit", -1);

        // Check if there is no limit.
        if (limit == -1) return false;

        // Check if the database is disabled.
        // We return true because the database may have disabled its self
        // and the admin may still want commands to be limited.
        if (Leaf.isDatabaseDisabled()) return true;

        int amountExecuted = Leaf.getDatabase()
                .getTable(CommandLimitTable.class)
                .getAmountExecuted(member, this.command.getIdentifier());

        // Check if the amount of times the command
        // has been executed is bigger or equal to the limit.
        return amountExecuted >= limit;
    }

    public boolean isOnCooldown(@NotNull Member member) {

        long cooldown = this.command.getSection()
                .getSection("discord_bot")
                .getLong("cooldown", -1);

        if (cooldown == -1) return false;
        if (Leaf.isDatabaseDisabled()) return true;

        long lastCooldownTimeStamp = Leaf.getDatabase()
                .getTable(CommandCooldownTable.class)
                .getExecutedTimeStamp(member, this.getCommand().getIdentifier());

        return (lastCooldownTimeStamp + cooldown) > System.currentTimeMillis();
    }

    public @NotNull Duration getCooldown(@NotNull Member member) {

        long cooldown = this.command.getSection()
                .getSection("discord_bot")
                .getLong("cooldown", -1);

        if (cooldown == -1) return Duration.ofMillis(0);

        long lastCooldownTimeStamp = Leaf.getDatabase()
                .getTable(CommandCooldownTable.class)
                .getExecutedTimeStamp(member, this.getCommand().getIdentifier());

        return Duration.ofMillis((lastCooldownTimeStamp + cooldown) - System.currentTimeMillis());
    }

    /**
     * Used to execute the discord command.
     *
     * @param event The instance of the discord event.
     */
    public void execute(@NotNull SlashCommandInteractionEvent event) {

        // Check if the channel is allowed.
        if (!this.isAllowed(event.getChannel().getId())) {
            event.reply(new DiscordBotMessageAdapter(
                    this.command.getSection(),
                    "discord_bot.channel_not_allowed",
                    "You cannot run this command in this channel."
            ).buildMessage()).queue();
            return;
        }

        // Check if the member is null.
        if (event.getMember() == null) {
            event.reply(new DiscordBotMessageAdapter(
                    this.command.getSection(),
                    "discord_bot.member_error",
                    "An error occurred while trying to get the member instance."
            ).buildMessage()).queue();
            return;
        }

        // Check if the user has permission to run the command.
        if (!this.hasPermission(event.getMember())) {
            event.reply(new DiscordBotMessageAdapter(
                    this.command.getSection(),
                    "discord_bot.no_permission",
                    "You do not have permission to run this command."
            ).buildMessage()).queue();
            return;
        }

        // Check if the user has the correct roles to run the command.
        if (!this.hasRoleFromList(event.getMember())) {
            event.reply(new DiscordBotMessageAdapter(
                    this.command.getSection(),
                    "discord_bot.no_roles",
                    "You do not have the correct roles to run this command."
            ).buildMessage()).queue();
            return;
        }

        // Check if the user is in the list.
        List<String> memberIds = this.getCommand().getSection().getListString("discord_bot.discord_members");
        if (!memberIds.isEmpty() && !memberIds.contains(event.getMember().getId())) {
            event.reply(new DiscordBotMessageAdapter(
                    this.command.getSection(),
                    "discord_bot.no_discord_id",
                    "You are not allowed to execute this command"
            ).buildMessage()).queue();
            return;
        }

        // Check if the member is limited.
        if (this.isLimited(event.getMember())) {
            event.reply(new DiscordBotMessageAdapter(
                    this.command.getSection(),
                    "discord_bot.limited",
                    "You can no longer execute this command because " +
                            "you have reached the commands execute limit."
            ).buildMessage()).queue();
            return;
        }

        // Check if the member is on cooldown.
        if (this.isOnCooldown(event.getMember())) {
            event.reply(new DiscordBotMessageAdapter(
                    this.command.getSection(),
                    "discord_bot.on_cooldown",
                    "Please wait %cooldown%s seconds before executing this command."
            ).setParser(new DiscordBotMessageAdapter.PlaceholderParser() {
                @Override
                public @NotNull String parsePlaceholders(@NotNull String string) {
                    return string.replace("%cooldown%", String.valueOf(DiscordBotCommandAdapter.this.getCooldown(event.getMember()).toSecondsPart()));
                }
            }).buildMessage()).queue();
            return;
        }

        // Execute as discord command.
        CommandStatus status = this.getCommand().getBaseCommandType().onDiscordRun(
                this.getCommand().getSection(),
                event
        );

        // Check if the status is null.
        if (status == null) {
            event.reply("This command is not supported on discord.").queue();
            return;
        }

        // Increase the command executions.
        status.increaseExecutions(event.getMember(), this.command);
        status.updateCooldownTimeStamp(event.getMember(), this.command);

        // Send a status message if given.
        String message = status.getMessage();
        if (message != null) event.reply(message).queue();
    }

    /**
     * Called when a message is sent on a discord server.
     *
     * @param event The instance of the event.
     */
    public void onMessage(@NotNull MessageReceivedEvent event) {

        // Check if the message is in the correct channel.
        if (!this.isAllowed(event.getChannel().getId())) return;

        // Check if the user has permission for the command to register the message.
        if (event.getMember() != null && !this.hasPermission(event.getMember())) return;

        // Run event.
        this.getCommand()
                .getBaseCommandType()
                .onDiscordMessage(this.getCommand().getSection(), event);
    }
}
