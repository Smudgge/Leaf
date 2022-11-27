package com.github.smuddgge.leaf.configuration.squishyyaml;

import com.github.smuddgge.leaf.Leaf;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

import static org.yaml.snakeyaml.DumperOptions.FlowStyle.BLOCK;

/**
 * Represents a yaml configuration file
 */
public class YamlConfiguration extends YamlConfigurationSection implements Configuration {

    /**
     * Represents the configuration file instance
     */
    private File file;

    /**
     * Represents the parent folder location
     */
    private final File folder;

    /**
     * The path within the parent folder
     */
    private final String path;

    /**
     * Used to create a representation of a configuration file
     *
     * @param file Instance of the configuration file
     */
    public YamlConfiguration(File file) {
        super(new HashMap<>());
        this.file = file;

        this.folder = file.getParentFile();
        this.path = file.getName();
    }

    /**
     * Used to create a representation of a configuration file
     *
     * @param folder The instance of the folder location
     * @param path   The path within the folder
     *               You should include the extension name
     */
    public YamlConfiguration(File folder, String path) {
        super(new HashMap<>());

        this.folder = folder;
        this.path = path;
    }

    /**
     * Used to get the absolute path
     * Combines the absolute path from the folder and path
     *
     * @return The absolute path
     */
    public String getAbsolutePath() {
        if (this.path == null || this.path.equals("")) {
            return this.folder.getAbsolutePath();
        }

        return this.folder.getAbsolutePath() + "/" + this.path;
    }

    @Override
    public boolean load() {
        this.file = new File(this.getAbsolutePath());

        // Create file if it doesn't exist
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) return false;
        }

        if (!file.exists()) {
            try (InputStream input = Leaf.class.getResourceAsStream("/" + file.getName())) {

                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }

            } catch (IOException exception) {
                exception.printStackTrace();
                return false;
            }
        }

        try (InputStream inputStream = new FileInputStream(this.file)) {

            Yaml yaml = new Yaml();
            this.data = yaml.load(inputStream);

            if (this.data == null) this.data = new HashMap<>();

        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Used to save the instance of the data to the configuration file
     *
     * @return True if successful
     */
    public boolean save() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setDefaultFlowStyle(BLOCK);

        Yaml yaml = new Yaml(dumperOptions);

        try {

            FileWriter writer = new FileWriter(this.file);
            yaml.dump(this.data, writer);

        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }
}
