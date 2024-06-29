package com.github.smuddgge.common.user;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public interface User {

    /**
     * <h1>Used to get the users uuid</h1>
     * Every user should have a unique uuid.
     *
     * @return The users uuid.
     */
    @NotNull UUID getUuid();

    /**
     * <h1>Used to get the users name</h1>
     * If the user doesnt have a set name,
     * the string will output "null".
     *
     * @return The users name.
     */
    @NotNull String getName();

    /**
     * <h1>Used to send a message to the user</h1>
     * This method will also parse placeholders and colours.
     *
     * @param message The message to send.
     */
    void sendMessage(@NotNull String message);

    /**
     * <h1>Used to send a message to the user</h1>
     * The message will add line breaks and send as a complete message.
     * This method will also parse placeholders and colours.
     *
     * @param messageList The instance of the message list.
     */
    void sendMessage(@NotNull List<String> messageList);

    /**
     * <h1>Used to check if a user is vanished</h1>
     *
     * @return True if the user is vanished.
     */
    boolean isVanished();

    /**
     * <h1>Used to check if a user has a permission</h1>
     *
     * @param permission The permission to check.
     * @return True if the user has permission.
     */
    boolean hasPermission(@NotNull String permission);

    /**
     * <h1>Used to check if a user has all the permissions in a list</h1>
     *
     * @param permissionList The list of permissions.
     * @return True if all permissions return true.
     */
    default boolean hasPermissionList(@NotNull List<String> permissionList) {
        for (String permission : permissionList) {
            if (!this.hasPermission(permission)) return false;
        }
        return true;
    }
}
