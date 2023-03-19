package com.github.smuddgge.leaf.configuration;

import com.github.smuddgge.leaf.configuration.squishyyaml.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a configuration handler.
 */
public abstract class ConfigurationHandler {

    private final File pluginFolder;
    private final String directoryName;

    protected List<YamlConfiguration> configFileList = new ArrayList<>();

    /**
     * Used to create a configuration handler.
     *
     * @param pluginFolder  The instance of the plugin's folder.
     * @param directoryName The directory name it will use.
     */
    public ConfigurationHandler(File pluginFolder, String directoryName) {
        this.pluginFolder = pluginFolder;
        this.directoryName = directoryName;

        this.createDirectory();
        this.addDefaultFiles();
        this.registerFiles();
    }

    /**
     * Used to create a new instance of the default configuration.
     *
     * @param directory The command handler's directory.
     */
    public abstract YamlConfiguration createDefaultConfiguration(File directory);

    /**
     * Used to reload the configuration files.
     */
    public abstract void reload();

    /**
     * Used to get the configuration handlers directory.
     *
     * @return The instance of the directory as a file.
     */
    public File getDirectory() {
        return new File(this.pluginFolder.getAbsolutePath() + File.separator + this.directoryName);
    }

    /**
     * Used to get all the files in a directory.
     * This will also search depth.
     *
     * @param folder The instance of a directory.
     * @return The list of files.
     */
    public List<File> getFiles(File folder) {
        File[] fileList = folder.listFiles();
        if (fileList == null) return new ArrayList<>();

        List<File> finalFileList = new ArrayList<>();
        for (File file : fileList) {
            List<File> filesInFile = this.getFiles(file);
            if (filesInFile.isEmpty()) {
                finalFileList.add(file);
                continue;
            }

            finalFileList.addAll(filesInFile);
        }

        return finalFileList;
    }

    /**
     * Get the names of the files in the command's directory.
     *
     * @return The file names in the command's directory.
     */
    public List<String> getFileNames(File folder) {
        File[] fileList = folder.listFiles();
        if (fileList == null) return new ArrayList<>();

        List<String> fileNameList = new ArrayList<>();
        for (File file : fileList) {
            List<String> filesInFile = this.getFileNames(file);
            if (filesInFile.isEmpty()) {
                fileNameList.add(file.getName());
                continue;
            }

            fileNameList.addAll(filesInFile);
        }

        return fileNameList;
    }

    /**
     * Used to create the directory.
     * If the directory already exists, nothing will happen.
     */
    protected boolean createDirectory() {
        return this.getDirectory().mkdir();
    }

    /**
     * Used to add a configuration file instance to the list.
     *
     * @param configuration The configuration file.
     */
    protected void addConfiguration(YamlConfiguration configuration) {
        this.configFileList.add(configuration);
    }

    /**
     * Used to add the default files if needed.
     */
    protected void addDefaultFiles() {
        if (this.getFileNames(this.getDirectory()).size() == 0) {
            YamlConfiguration defaultConfiguration = this.createDefaultConfiguration(this.getDirectory());
            defaultConfiguration.reload();
        }
    }

    /**
     * Used to register all the files.
     */
    protected void registerFiles() {
        for (File file : this.getFiles(this.getDirectory())) {
            YamlConfiguration yamlConfiguration = new YamlConfiguration(file);
            yamlConfiguration.reload();
            this.addConfiguration(yamlConfiguration);
        }
    }
}
