package seng302.group2.workspace.project.story.acceptanceCriteria;

import javafx.util.StringConverter;
import org.apache.commons.lang.WordUtils;

/**
 * A class for converting the state of an acceptance criteria from an enum value to a string, and also the reverse.
 * Created by jml168 on 29/06/15.
 */
public class AcEnumStringConverter extends StringConverter<AcceptanceCriteria.AcState> {

    /**
     * Converts a string value into an enum value in the AcState enum.
     * @param value The string value to convert
     * @return The enum value converted to
     */
    public AcceptanceCriteria.AcState fromString(String value) {
        String result = value.replace(' ', '_');
        result = result.toUpperCase();
        return AcceptanceCriteria.AcState.valueOf(result);
    }

    /**
     * Converts an enum value from the AcState enum into a string value.
     * @param value The enum value to convert
     * @return The string value converted to
     */
    @Override
    public String toString(AcceptanceCriteria.AcState value) {
        String result = value.name().replace("_", " ");
        return WordUtils.capitalizeFully(result);
    }
}
