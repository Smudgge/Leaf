package com.github.smuddgge.leaf.configurationold;

import com.github.smuddgge.squishyconfiguration.implementation.YamlConfiguration;

import java.io.File;

/**
 * Represents the main config.
 */
public class ConfigMain extends YamlConfiguration {

    private static ConfigMain config;

    /**
     * Used to create an instance of the database's
     * configuration file.
     *
     * @param folder The plugin's folder.
     */
    public ConfigMain(File folder) {
        super(folder, "config.yml");
        this.setDefaultPath("config.yml");
        this.load();
    }

    /**
     * Used to initialise the database's configuration
     * file instance.
     *
     * @param folder The plugin's folder.
     */
    public static void initialise(File folder) {
        ConfigMain.config = new ConfigMain(folder);
    }

    /**
     * Used to get the instance of the database's
     * configuration file.
     *
     * @return The database's configuration file.
     */
    public static ConfigMain get() {
        return ConfigMain.config;
    }

    /**
     * Used to get the vanishable permission.
     *
     * @return The permission to check if a
     * player can vanish.
     */
    public static String getVanishablePermission() {
        return ConfigMain.get().getString("vanish_permission", "leaf.vanishable");
    }

    /**
     * Used to get if a vanishable player can
     * always see a vanishable player.
     *
     * @return True if a player can always see
     * a vanishable player.
     */
    public static boolean getVanishableCanSeeVanishable() {
        return ConfigMain.get().getBoolean("vanishable_can_see_vanishable", false);
    }
}
