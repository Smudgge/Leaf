package com.github.smuddgge.leaf.utility;

import com.github.smuddgge.leaf.Leaf;
import com.velocitypowered.api.command.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents the console command utility.
 */
public class CommandUtility {

    /**
     * Used to execute a command in the console.
     * This will also remove any special args in the command.
     *
     * @param command The command to execute.
     */
    public static void executeCommandInConsole(@NotNull String command) {
        // Get the command manager.
        CommandManager manager = Leaf.getServer().getCommandManager();

        // Execute the command after removing special args.
        manager.executeAsync(Leaf.getServer().getConsoleCommandSource(), command
                .replace(" -c", "")
                .replace(" -o", "")
        );
    }

    /**
     * Used to execute a command list in the console.
     * This will also remove any special args in the commands.
     *
     * @param commandList The command list to execute.
     */
    public static void executeCommandInConsole(@NotNull List<String> commandList) {

        // Loop though all commands.
        for (String command : commandList) {
            CommandUtility.executeCommandInConsole(command);
        }
    }
}
