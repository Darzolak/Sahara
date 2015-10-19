package seng302.group2.scenes.control.search;

import javafx.scene.paint.Color;
import seng302.group2.util.conversion.ColorUtils;

/**
 * A interface for all the searchable controls in the program.
 * Created by jml168 on 31/07/15.
 */
public interface SearchableControl {
    public static String highlightColourString = ColorUtils.toRGBCode(Color.rgb(255, 255, 0, 0.5));

    public static Color highlightColour = Color.rgb(255, 255, 0, 0.5);

    /**
     * Abstract Method.
     * Subclasses will implement this to query their contents for a string
     * @param query the string to be queried
     * @return whether or not a matching string was found
     */
    boolean query(String query);

    /**
     * Query to be used for advanced search, only searches through values (not GUI elements)
     * @param query the string to be searched
     * @param searchType SearchType for the Advanced Search
     * @return whether the query string was found
     */
    int advancedQuery(String query, SearchType searchType);
}
