package com.github.smuddgge.leaf.command;

import com.github.smuddgge.leaf.Leaf;
import com.github.smuddgge.leaf.logger.Logger;
import com.github.squishylib.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The first word typed after a slash.
 * <p>
 * This is what is registered with velocity.
 * Any subcommands are simulated with this class.
 */
public abstract class BaseCommandType implements CommandType {

    private final @NotNull List<CommandType> subCommandTypes = new ArrayList<>();

    public @NotNull List<CommandType> getSubCommandTypes() {
        return this.subCommandTypes;
    }

    public @NotNull BaseCommandType addSubCommandType(@NotNull CommandType subCommandType) {
        this.subCommandTypes.add(subCommandType);
        return this;
    }

    /**
     * Checks which subcommands should be removed from the command type.
     *
     * @param section The command type's configuration section.
     */
    public void initialiseSubCommands(@NotNull ConfigurationSection section) {

        final Logger logger = Leaf.get().getLogger().extend(" [Commands]");
        final List<CommandType> toRemove = new ArrayList<>();

        for (CommandType commandType : this.subCommandTypes) {

            // Does the configuration not exist?
            if (!section.getKeys().contains(commandType.getName())) {
                logger.info("&7↳ &cDisabling &7subcommand (No configuration) &f" + commandType.getName());
                toRemove.add(commandType);
                continue;
            }

            // Is the command disabled?
            if (!section.getSection(commandType.getName()).getBoolean("enabled", true)) {
                logger.info("&7↳ &eDisabling &7subcommand (Configuration disabled) &f" + commandType.getName());
                toRemove.add(commandType);
                continue;
            }

            logger.info("&7↳ &aEnabling &7subcommand &f" + commandType.getName());
        }

        // Remove the subcommands that were disabled.
        toRemove.forEach(this.subCommandTypes::remove);
    }

    public void removeAllSubCommands() {
        this.subCommandTypes.clear();
    }
}
