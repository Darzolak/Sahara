package seng302.group2.util.conversion;

import javafx.util.StringConverter;
import org.apache.commons.lang.WordUtils;

/**
 * A class for converting the string value of enumerations to human readable format.
 */
public class GeneralEnumStringConverter extends StringConverter<String> {

    /**
     * Converts a human readable string value into an equivalent enum string value.
     *
     * @param value The string value to convert
     * @return The enum value converted to
     */
    public String fromString(String value) {
        String result = value.replace(' ', '_');
        return result.toUpperCase();
    }

    /**
     * Converts an enum string value into an equivalent human readable string value.
     *
     * @param value The enum value to convert
     * @return The string value converted to
     */
    @Override
    public String toString(String value) {
        String result = value.replace("_", " ");
        return WordUtils.capitalizeFully(result);
    }
}
