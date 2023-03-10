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

    /**
     * Used to convert a string to a time stamp.
     *
     * <p>
     * Example string: 100d
     * Example conversion: (now - 100days)in time stamp
     * </p>
     *
     * @param fromNow The amount of time backwards from this moment.
     * @return The converted time stamp.
     */
    public static Long convertToTimeStamp(String fromNow) {
        String amountAsString = fromNow.substring(0, fromNow.length() - 1);
        long amount = Long.parseLong(amountAsString);

        if (fromNow.endsWith("d")) {
            Long milliseconds = amount * 24 * 60 * 60 * 1000;
            Long current = System.currentTimeMillis();

            return current - milliseconds;
        }

        if (fromNow.endsWith("h")) {
            Long milliseconds = amount * 60 * 60 * 1000;
            Long current = System.currentTimeMillis();

            return current - milliseconds;
        }

        return 0L;
    }

    /**
     * Used to get the value of from now for message query.
     *
     * @param time The amount of time as a string to go back in time.
     * @return The time stamp.
     */
    public static Long getFrom(String time) {
        String[] timeList = time.split("-");
        return DateAndTime.convertToTimeStamp(timeList[0]);
    }

    /**
     * Used to get the value of to now for message query.
     *
     * @param time The amount of time as a string to go back in time.
     * @return The time stamp.
     */
    public static Long getTo(String time) {
        String[] timeList = time.split("-");
        if (timeList.length < 2) return System.currentTimeMillis();

        return DateAndTime.convertToTimeStamp(timeList[1]);
    }
}
