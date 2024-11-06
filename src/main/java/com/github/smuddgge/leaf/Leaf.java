package com.github.smuddgge.leaf;

import com.github.smuddgge.leaf.configuration.*;
import com.github.smuddgge.leaf.logger.Logger;
import com.github.smuddgge.leaf.logger.SquishyLoggerAdapter;
import com.github.squishylib.configuration.Configuration;
import com.github.squishylib.database.Database;
import com.github.squishylib.database.DatabaseBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

@Plugin(
        id = "leaf",
        name = "Leaf",
        version = "6.0.0.dev",
        description = "A velocity utility plugin.",
        authors = {"Smudge"}
)
public class Leaf {

    private static Leaf instance;

    private final @NotNull ProxyServer proxyServer;
    private final @NotNull File folder;
    private final @NotNull Logger logger;
    private final @NotNull Metrics.Factory metricsFactory;

    private Config config;
    private DatabaseConfig databaseConfig;
    private MessagesConfig messagesConfig;
    private WhitelistConfig whitelistConfig;
    private CommandDirectory commandsDirectory;
    private PlaceholderDirectory placeholdersDirectory;
    private VariableDirectory variableDirectory;
    private EventDirectory eventDirectory;

    private Database database;

    @Inject
    public Leaf(ProxyServer proxyServer, @DataDirectory final Path folder, ComponentLogger componentLogger, Metrics.Factory metricsFactory) {

        // Assign the pointer to this instance.
        Leaf.instance = this;

        try {

            // Set up basic variables.
            this.proxyServer = proxyServer;
            this.folder = folder.toFile();
            this.logger = new Logger(componentLogger);
            this.metricsFactory = metricsFactory;

        } catch (Exception exception) {
            throw new LeafException(exception, "Leaf", "Failed to initialise the plugin.");
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        // Set up the methods debug logger.
        Logger tempLogger = this.logger.extend(" &b.onProxyInitialization() &7Leaf.java:105");

        // Display the plugin's name in console.
        this.logHeader();

        // Set up b stats.
        tempLogger.debug("Setting up b-stats.");
        this.setupBStats();

        // Set up config files and directories.
        tempLogger.debug("Setting up config files and directories.");
        this.setUpConfigurationAndDirectories();

        // Set up the database.
        this.setupDatabase();
    }

    private void logHeader() {
        try {
            final String message = """
                &7
                &a __         ______     ______     ______
                &a/\\ \\       /\\  ___\\   /\\  __ \\   /\\  ___\\
                &a\\ \\ \\____  \\ \\  __\\   \\ \\  __ \\  \\ \\  __\\
                &a \\ \\_____\\  \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_\\
                &a  \\/_____/   \\/_____/   \\/_/\\/_/   \\/_/
                &7
                      &7By Smudge    Version &b%s
                &7
                """.formatted(Leaf.class.getAnnotation(Plugin.class).version());

            this.logger.info(message);
        } catch (Exception exception) {
            throw new LeafException(exception, "logHeader", "Failed to initialise the plugin.");
        }
    }

    private void setupBStats() {
        try {
            this.metricsFactory.make(this, 17381);
            if (this.config.shouldLogBStats()) this.logger.info(" &7[b-stats] Enabled");
        } catch (Exception exception) {
            throw new LeafException(exception, "setupBStats", "Failed to initialise b stats.");
        }
    }

    private void setUpConfigurationAndDirectories() {

        // Set up the methods debug logger.
        Logger tempLogger = this.logger.extend(" &b.setUpConfigurationAndDirectories() &7Leaf.java:130");

        try {
            tempLogger.debug("Initializing&b config.yml");
            this.config = new Config(this.folder, "config.yml");
            this.config.setResourcePath("config.yml");
            this.config.load();

            tempLogger.debug("Initializing&b database.yml");
            this.databaseConfig = new DatabaseConfig(this.folder, "database.yml");
            this.databaseConfig.setResourcePath("database.yml");
            this.databaseConfig.load();

            tempLogger.debug("Initializing&b messages.yml");
            this.messagesConfig = new MessagesConfig(this.folder, "messages.yml");
            this.messagesConfig.setResourcePath("messages.yml");
            this.messagesConfig.load();

            tempLogger.debug("Initializing&b whitelist.yml");
            this.whitelistConfig = new WhitelistConfig(this.folder, "whitelist.yml");
            this.whitelistConfig.setResourcePath("whitelist.yml");
            this.whitelistConfig.load();

            tempLogger.debug("Initializing&b Command Directory");
            this.commandsDirectory = new CommandDirectory(this.folder);
            this.commandsDirectory.addResourcePath("commands.yml");
            this.commandsDirectory.load(false);

            tempLogger.debug("Initializing&b Placeholder Directory");
            this.placeholdersDirectory = new PlaceholderDirectory(this.folder);
            this.placeholdersDirectory.addResourcePath("placeholders.yml");
            this.placeholdersDirectory.load(false);

            tempLogger.debug("Initializing&b Variable Directory");
            this.variableDirectory = new VariableDirectory(this.folder);
            this.variableDirectory.addResourcePath("variables.yml");
            this.variableDirectory.load(false);

            tempLogger.debug("Initializing&b Event Directory");
            this.eventDirectory = new EventDirectory(this.folder);
            this.eventDirectory.addResourcePath("events.yml");
            this.eventDirectory.load(false);

        } catch (Exception exception) {
            throw new LeafException(exception, "setupConfigurationAndDirectories", "Failed to initialise config files and directories.");
        }
    }

    private void setupDatabase() {

        // Set up the methods debug logger.
        Logger tempLogger = this.logger.extend(" &b.setupDatabase() &7Leaf.java:173");

        try {

            DatabaseBuilder builder = new DatabaseBuilder(this.databaseConfig);
            builder.setLogger(new SquishyLoggerAdapter(this.logger));
            builder.setDebugMode(this.inDebugMode());

        } catch (Exception exception) {
            throw new LeafException(exception, "setupDatabase", "Failed to initialise the database.");
        }
    }

    public @NotNull ProxyServer getProxyServer() {
        return this.proxyServer;
    }

    public @NotNull File getFolder() {
        return this.folder;
    }

    public @NotNull Logger getLogger() {
        return this.logger;
    }

    public @NotNull Config getConfig() {
        return this.config;
    }

    public @NotNull DatabaseConfig getDatabaseConfig() {
        return this.databaseConfig;
    }

    public @NotNull MessagesConfig getMessagesConfig() {
        return this.messagesConfig;
    }

    public @NotNull WhitelistConfig getWhitelistConfig() {
        return this.whitelistConfig;
    }

    public @NotNull CommandDirectory getCommandsDirectory() {
        return this.commandsDirectory;
    }

    public @NotNull PlaceholderDirectory getPlaceholdersDirectory() {
        return this.placeholdersDirectory;
    }

    public @NotNull VariableDirectory getVariableDirectory() {
        return this.variableDirectory;
    }

    public @NotNull EventDirectory getEventDirectory() {
        return this.eventDirectory;
    }

    public boolean inDebugMode() {
        return this.config.inDebugMode();
    }

    /**
     * Gets the instance of the loaded leaf plugin.
     *
     * @return The instance of the leaf plugin.
     */
    public static @NotNull Leaf get() {
        return Leaf.instance;
    }
}
