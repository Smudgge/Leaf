package com.github.smuddgge.leaf.configuration.squishyyaml;

public enum YamlConfigurationExceptionType {
    WRONG_TYPE("This path contains the wrong type: <var>");

    /**
     * Represents the exception message
     */
    private final String message;

    /**
     * Used to create a new yaml configuration exception enum
     *
     * @param message The error message
     */
    YamlConfigurationExceptionType(String message) {
        this.message = message;
    }

    /**
     * Used to get the exception message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Used to get the message and replace the placeholder with a variable
     *
     * @param variable To replace with the placeholder
     * @return The message formatted with the variable
     */
    public String getMessage(String variable) {
        return this.message.replace("<var>", variable);
    }
}