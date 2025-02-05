package com.github.smuddgge.leaf.discord;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.LeafException;
import com.github.smuddgge.leaf.command.Command;
import com.github.smuddgge.leaf.command.CommandStatus;
import com.github.smuddgge.leaf.database.CommandCooldownRecord;
import com.github.smuddgge.leaf.database.CommandCooldownTable;
import com.github.smuddgge.leaf.database.CommandLimitRecord;
import com.github.smuddgge.leaf.database.CommandLimitTable;
import com.github.smuddgge.leaf.helper.DiscordHelper;
import com.github.smuddgge.leaf.user.DiscordBotUser;
import com.github.squishylib.database.Query;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DiscordBotCommandAdapter {

    private final @NotNull Command command;
    private final long snowflake;

    public DiscordBotCommandAdapter(@NotNull Command command, long snowflake) {
        this.command = command;
        this.snowflake = snowflake;
    }

    public @NotNull Command getCommand() {
        return this.command;
    }

    /**
     * Used to get the commands snowflake id.
     * This is what discord uses as a unique id.
     *
     * @return The snowflake id.
     */
    public long getSnowflake() {
        return this.snowflake;
    }

    public @NotNull List<String> getAllowedChannels() {
        return this.getCommand().getSection().getListString("discord_bot.allowed_channels", new ArrayList<>());
    }

    public @NotNull List<Permission> getDiscordPermissions() {
        List<Permission> permissionList = new ArrayList<>();

        for (String permission : this.getCommand().getSection().getListString("discord_bot.permissions", new ArrayList<>())) {
            try {
                permissionList.add(Permission.valueOf(permission.toUpperCase()));
            } catch (Exception exception) {
                throw new LeafException(exception,
                    "DiscordBotCommandAdapter.getDiscordPermissions()",
                    "Invalid permission &f" + permission,
                    "The list of discord permissions are here: https://discord.com/developers/docs/topics/permissions"
                );
            }
        }

        return permissionList;
    }

    public @NotNull List<String> getDiscordRoles() {
        return this.getCommand().getSection().getListString("discord_bot.roles", new ArrayList<>());
    }

    public boolean isAllowed(@NotNull String channelSnowflake) {
        List<String> allowedChannels = this.getAllowedChannels();
        if (allowedChannels.isEmpty()) return true;
        return this.getAllowedChannels().contains(channelSnowflake);
    }

    public boolean hasPermission(@NotNull Member member) {
        List<Permission> permissionList = this.getDiscordPermissions();
        if (permissionList.isEmpty()) return true;
        return member.hasPermission(permissionList);
    }

    public boolean hasRoleFromList(@NotNull Member member) {
        return DiscordHelper.hasRoleFromList(this.getDiscordRoles(), member);
    }

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
        if (Leaf.get().isDatabaseDisabled()) return true;

        CommandLimitRecord record = Leaf.get().getDatabase()
                .getTable(CommandLimitTable.class)
                .getFirstRecord(new Query().match(
                    CommandLimitRecord.ID_FIELD,
                    CommandLimitRecord.createId(member, command.getIdentifier())
                ))
                .waitAndGet();

        if (record == null) return false;

        // Check if the amount of times the command
        // has been executed is bigger or equal to the limit.
        return record.getAmountExecuted() >= limit;
    }

    public boolean isOnCooldown(@NotNull Member member) {

        long cooldown = this.command.getSection()
                .getSection("discord_bot")
                .getLong("cooldown", -1);

        if (cooldown == -1) return false;
        if (Leaf.get().isDatabaseDisabled()) return true;

        final CommandCooldownRecord record = Leaf.get().getDatabase()
                .getTable(CommandCooldownTable.class)
                .getFirstRecord(new Query().match(CommandCooldownRecord.ID_FIELD, "Discord" + member.getUser().getName() + command.getIdentifier()))
                .waitAndGet();

        if (record == null) return false;
        return (record.getLastExecutedTimestamp() + cooldown) > System.currentTimeMillis();
    }

    public @NotNull Duration getCooldown(@NotNull Member member) {

        long cooldown = this.command.getSection()
                .getSection("discord_bot")
                .getLong("cooldown", -1);

        if (cooldown == -1) return Duration.ofMillis(0);

        final CommandCooldownRecord record = Leaf.get().getDatabase()
            .getTable(CommandCooldownTable.class)
            .getFirstRecord(new Query().match(CommandCooldownRecord.ID_FIELD, "Discord" + member.getUser().getName() + command.getIdentifier()))
            .waitAndGet();

        if (record == null) return Duration.ofMillis(0);
        return Duration.ofMillis((record.getLastExecutedTimestamp() + cooldown) - System.currentTimeMillis());
    }

    public void execute(@NotNull SlashCommandInteractionEvent event, @NotNull DiscordBotUser user) {

        // Check if the channel is allowed.
        if (!this.isAllowed(event.getChannel().getId())) {
            user.sendMessage(
                this.command.getSection(),
                "discord_bot.channel_not_allowed",
                "You cannot run this command in this channel."
            );
            return;
        }

        // Check if the member is null.
        if (event.getMember() == null) {
            user.sendMessage(
                    this.command.getSection(),
                    "discord_bot.member_error",
                    "An error occurred while trying to get the member instance."
            );
            return;
        }

        // Check if the user has permission to run the command.
        if (!this.hasPermission(event.getMember())) {
            user.sendMessage(
                    this.command.getSection(),
                    "discord_bot.no_permission",
                    "You do not have permission to run this command."
            );
            return;
        }

        // Check if the user has the correct roles to run the command.
        if (!this.hasRoleFromList(event.getMember())) {
            user.sendMessage(
                    this.command.getSection(),
                    "discord_bot.no_roles",
                    "You do not have the correct roles to run this command."
            );
            return;
        }

        // Check if the user is in the list.
        List<String> memberIds = this.getCommand().getSection().getListString("discord_bot.discord_members");
        if (!memberIds.isEmpty() && !memberIds.contains(event.getMember().getId())) {
            user.sendMessage(
                    this.command.getSection(),
                    "discord_bot.no_discord_id",
                    "You are not allowed to execute this command"
            );
            return;
        }

        // Check if the member is limited.
        if (this.isLimited(event.getMember())) {
            user.sendMessage(
                    this.command.getSection(),
                    "discord_bot.limited",
                    "You can no longer execute this command because " +
                            "you have reached the commands execute limit."
            );
            return;
        }

        // Check if the member is on cooldown.
        if (this.isOnCooldown(event.getMember())) {
            user.sendMessage(
                this.command.getSection(),
                "discord_bot.on_cooldown",
                "Please wait %cooldown%s seconds before executing this command.",
                (string) -> string.replace(
                    "%cooldown%",
                    String.valueOf(DiscordBotCommandAdapter.this.getCooldown(event.getMember()).toSecondsPart())
                )
            );

            return;
        }

        // Execute as discord command.
        CommandStatus status = this.getCommand().getCommandType().onDiscordRun(
                this.getCommand().getSection(),
                event,
                user
        );

        // Remove last message if specified.
        user.removeLastMessage(this.getCommand().getSection());

        // Check if the status is null.
        if (status == null) {
            event.reply("This command is not supported on discord.").queue();
            return;
        }

        // Increase the command executions.
        status.increaseExecutions(event.getMember(), this.command);
        status.applyCooldownIfNeeded(event.getMember(), this.command);

        // Send a status message if given.
        String message = status.getFirstMessage();
        if (message != null) event.reply(message).queue();
    }

//    /**
//     * Called when a message is sent on a discord server.
//     *
//     * @param event The instance of the event.
//     */
//    public void onMessage(@NotNull MessageReceivedEvent event) {
//
//        // Check if the message is in the correct channel.
//        if (!this.isAllowed(event.getChannel().getId())) return;
//
//        // Check if the user has permission for the command to register the message.
//        if (event.getMember() != null && !this.hasPermission(event.getMember())) return;
//
//        // Run event.
//        this.getCommand()
//                .getBaseCommandType()
//                .onDiscordMessage(this.getCommand().getSection(), event);
//    }
}
