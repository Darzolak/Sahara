package seng302.group2.util.conversion;

import seng302.group2.util.validation.DateValidator;

/**
 * Created by Shinobu on 26/08/2015.
 */
public class DurationConverter {
    /**
     * Converts string formatted duration to duration in minutes
     *
     * @param inputDuration the string duration to be converted
     * @return duration in minutes
     */
    public static Double readDurationToMinutes(String inputDuration) {
        if (!DateValidator.validDuration(inputDuration)) {
            return null;
        }

        String[] hourKeys = {"h", "hour", "hours", "hrs", "hr"};
        String[] minKeys = {"min", "mins", "m", "minutes", "minute"};

        int hourPos = -1;
        int minPos = -1;
        String hourWord = "";

        // Find the hour position
        for (String hour : hourKeys) {
            if (inputDuration.contains(hour)) {
                hourPos = inputDuration.indexOf(hour);
                hourWord = hour;
            }
        }

        // Find the minute position
        for (String min : minKeys) {
            if (inputDuration.contains(min)) {
                minPos = inputDuration.indexOf(min);
            }
        }

        if (hourPos > 0 && minPos > 0) {
            // Have both
            String hourString = inputDuration.substring(0, hourPos);
            String minString = inputDuration.substring(hourPos + hourWord.length(), minPos);
            return Double.parseDouble(hourString.trim()) * 60 + Float.valueOf(minString.trim());

        }
        else if (hourPos > 0) {
            // Have just hours
            String hourString = inputDuration.substring(0, hourPos);
            return Double.parseDouble(hourString.trim()) * 60;
        }
        else if (minPos > 0) {
            // Have just mins
            String minString = inputDuration.substring(0, minPos);
            return Double.parseDouble(minString.trim());
        }
        else {
            // Have just a base number (hours default)
            return Double.parseDouble(inputDuration.trim()) * 60;
        }
    }

    /**
     * Converts string formatted duration to duration in hour
     *
     * @param inputDuration the string duration to be converted
     * @return duration in hours
     */
    public static Double readDurationToHours(String inputDuration) {
        return readDurationToMinutes(inputDuration) / 60;
    }

}
