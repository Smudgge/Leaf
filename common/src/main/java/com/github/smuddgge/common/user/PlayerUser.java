package com.github.smuddgge.common.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * <h1>Represents a player</h1>
 * Uses methods defined in the user interface.
 */
public class PlayerUser implements User {

    private final @NotNull Player player;

    /**
     * Used to create a new player user.
     *
     * @param player The instance of a player.
     */
    public PlayerUser(@NotNull Player player) {
        this.player = player;
    }

    @Override
    public @NotNull UUID getUuid() {
        return this.player.getUniqueId();
    }

    @Override
    public @NotNull String getName() {
        return this.player.getName();
    }

    @Override
    public void sendMessage(@NotNull String message) {
        this.player.sendMessage(MessageManager.parse(message));
    }

    @Override
    public void sendMessage(@NotNull List<String> messageList) {
        this.player.sendMessage(MessageManager.parse(messageList));
    }

    @Override
    public boolean isVanished() {
        for (MetadataValue meta : this.player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return this.player.hasPermission(permission);
    }

    /**
     * <h1>Used to get the player instance</h1>
     *
     * @return The instance of the player.
     */
    public @NotNull Player getPlayer() {
        return this.player;
    }

    /**
     * <h1>Used to get a item from the players inventory</h1>
     *
     * @param index The slot index.
     * @return The requested item.
     */
    public @NotNull CozyItem getInventoryItem(int index) {
        if (this.player.getInventory().getItem(index) == null) return new CozyItem();
        return new CozyItem(this.player.getInventory().getItem(index));
    }

    /**
     * Used to run a command as operator.
     * <li>Replaces {player} with this players name.</li>
     *
     * @param command The command to run.
     * @return This instance.
     */
    public @NotNull PlayerUser runCommandsAsOp(@NotNull String command) {
        if (this.player.isOp()) {
            Bukkit.dispatchCommand(player, command.replace("{player}", this.getName()));
            return this;
        }

        try {
            player.setOp(true);
            Bukkit.dispatchCommand(player, command.replace("{player}", this.getName()));
        } finally {
            player.setOp(false);
        }

        return this;
    }

    /**
     * Used to run commands as operator.
     *
     * @param command1      The first command.
     * @param otherCommands Other commands to run.
     * @return This instance.
     */
    public @NotNull PlayerUser runCommandsAsOp(@NotNull String command1, String... otherCommands) {
        this.runCommandsAsOp(command1);
        for (String command : otherCommands) {
            this.runCommandsAsOp(command);
        }

        return this;
    }

    /**
     * Used to run a list of commands as operator.
     *
     * @param commandList The list of commands.
     * @return This instance.
     */
    public @NotNull PlayerUser runCommandsAsOp(@NotNull List<String> commandList) {
        for (String command : commandList) {
            this.runCommandsAsOp(command);
        }

        return this;
    }

    /**
     * Used to get how much money this player has.
     * If the vault api is not installed it will return 0.
     *
     * @return The amount of money this player has.
     */
    public double getMoney() {
        if (!VaultAPIDependency.isEnabled()) return 0;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.getUuid());
        return VaultAPIDependency.get().getBalance(offlinePlayer);
    }

    /**
     * Used to give this user money.
     *
     * @param amount The amount of money to give.
     * @return True if the money was given successfully.
     */
    public boolean giveMoney(double amount) {
        if (!VaultAPIDependency.isEnabled()) return false;
        return VaultAPIDependency.giveMoney(this, amount);
    }

    /**
     * Used to remove money from this player.
     *
     * @param amount The amount to remove.
     * @return True if the money was removed successfully.
     */
    public boolean removeMoney(double amount) {
        if (!VaultAPIDependency.isEnabled()) return false;
        return VaultAPIDependency.removeMoney(this, amount);
    }

    /**
     * Used to change the amount of money the
     * player has by a certain amount.
     * If the amount is positive the money will be given.
     * If its negative the money will be taken away.
     *
     * @param amount The amount of money to change by.
     * @return True if the money was changed successfully.
     */
    public boolean changeMoney(double amount) {
        if (amount > 0) return this.giveMoney(amount);
        if (amount < 0) return this.removeMoney(Math.abs(amount));
        return false;
    }

    /**
     * Used to set the user's scoreboard instance.
     *
     * @param scoreboard The instance of the score board to set.
     * @return This instance.
     */
    public @NotNull PlayerUser setScoreboard(@Nullable Scoreboard scoreboard) {
        ScoreboardManager.setScoreboard(this, scoreboard);
        return this;
    }

    /**
     * Used to set the user's scoreboard as an animated scoreboard.
     *
     * @param scoreboard The instance of the scoreboard.
     * @return This instance.
     */
    public @NotNull PlayerUser setScoreboard(@Nullable AnimatedScoreboard scoreboard) {
        ScoreboardManager.setScoreboard(this, scoreboard);
        return this;
    }

    /**
     * Used to remove the player's scoreboard.
     *
     * @return This instance.
     */
    public @NotNull PlayerUser removeScoreboard() {
        ScoreboardManager.setScoreboard(this, (@Nullable Scoreboard) null);
        return this;
    }

    /**
     * Used to force teleport a player.
     * This will continue trying to teleport the player
     * every 4 ticks until one of the following reasons:
     * <li>The player was teleported successfully.</li>
     * <li>The player left the server.</li>
     *
     * @param location The location to teleport the player to.
     * @return This instance.
     */
    public @NotNull PlayerUser forceTeleport(@NotNull Location location) {
        this.runTaskLoop(PlayerUser.FORCE_TELEPORT_TASK_IDENTIFIER, () -> {

            // Check if the player is no longer on the server.
            if (Bukkit.getPlayer(this.player.getUniqueId()) == null) {

                // Stop the task if successful.
                this.stopTask(PlayerUser.FORCE_TELEPORT_TASK_IDENTIFIER);
            }

            // Attempt to teleport the player.
            if (this.getPlayer().teleport(location)) {

                // Stop the task if successful.
                this.stopTask(PlayerUser.FORCE_TELEPORT_TASK_IDENTIFIER);
            }

        }, 4);
        return this;
    }

    /**
     * Used to force set this player's game mode.
     * This will continue trying to set the player's game mode
     * every 4 ticks until one of the following reasons:
     * <li>The player's game mode was set successfully.</li>
     * <li>The player left the server.</li>
     *
     * @param mode The game mode to set.
     * @return This instance.
     */
    public @NotNull PlayerUser forceGameMode(@NotNull GameMode mode) {
        this.runTaskLoop(PlayerUser.FORCE_GAME_MODE_TASK_IDENTIFIER, () -> {

            // Check if the player is no longer on the server.
            if (Bukkit.getPlayer(this.player.getUniqueId()) == null) {

                // Stop the task if successful.
                this.stopTask(PlayerUser.FORCE_GAME_MODE_TASK_IDENTIFIER);
            }

            // Attempt to set the player's game mode.
            this.getPlayer().setGameMode(mode);

            if (this.getPlayer().getGameMode().equals(mode)) {

                // Stop the task if successful.
                this.stopTask(PlayerUser.FORCE_GAME_MODE_TASK_IDENTIFIER);
            }

        }, 4);
        return this;
    }
}
