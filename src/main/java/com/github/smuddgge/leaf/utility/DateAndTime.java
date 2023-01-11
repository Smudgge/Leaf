package com.github.smuddgge.leaf.utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a date and time utility class.
 */
public class DateAndTime {

    /**
     * Used to get the date and time formatted.
     *
     * @return Requested string.
     */
    public static String getNow() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH-mm-ss");
        LocalDateTime localDateTime = LocalDateTime.now();
        return dateTimeFormatter.format(localDateTime);
    }

    /**
     * Used to convert a date into its correct formatting.
     *
     * @param date The date to convert
     * @return The requested string.
     */
    public static String convert(String date) {
        return date.replace("-", ":");
    }
}
