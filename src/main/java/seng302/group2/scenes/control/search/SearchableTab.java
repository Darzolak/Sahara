package seng302.group2.scenes.control.search;

import javafx.scene.control.Tab;
import org.controlsfx.control.PopOver;
import seng302.group2.scenes.information.project.story.task.LoggingEffortPane;
import seng302.group2.scenes.information.tag.TaggingPane;

import java.util.Collection;

/**
 * An abstract class for searchableTabs. Includes a base implementation of the query method.
 * Created by jml168 on 31/07/15.
 */
public abstract class SearchableTab extends Tab {

    /**
     * Abstract method.
     * Each inheriting class should implement this method to return a collection of their searchable controls.
     * @return A collection of a tabs searchable controls.
     */
    public abstract Collection<SearchableControl> getSearchableControls();




    /**
     * Constructs the contents of the tab
     */
    public abstract void construct();

    /**
     * Checks to see if the query matches any string in the searchable controls on this tab.
     * @param query The string to be matched within the tab.
     * @return if the query was found
     */
    public boolean query(String query) {
        boolean found = false;
        for (SearchableControl control : getSearchableControls()) {
            if (control.query(query)) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Checks to see if the query matches any string in the searchable controls on this tab.
     * @param query The string to be matched within the tab.
     * @param searchType The type of search
     * @return if the query was found
     */
    public int advancedQuery(String query, SearchType searchType) {
        int count = 0;

        if (searchType == SearchType.COMPLEX) {
            Integer orCount = null;
            String and = "\\&\\&";
            String or = "\\|\\|";
            String[] orList = query.split(or);
            for (String item : orList) {
                Integer andCount = null;
                String[] andList = item.split(and);
                for (String element : andList) {
                    for (SearchableControl control : getSearchableControls()) {
                        int elementCount = control.advancedQuery(element.trim(), SearchType.NORMAL);
                        if (andCount == null) {
                            andCount = elementCount;
                        }
                        else if (elementCount > 0 && andCount > 0) {
                            if (elementCount > andCount) {
                                andCount = elementCount;
                            }
                        }
                    }
                }
                if (orCount == null) {
                    orCount = andCount;
                }
                else if (orCount > 0 || andCount > 0) {
                    if (andCount > orCount) {
                        orCount = andCount;
                    }
                }
            }
            if (orCount != null) {
                count = orCount;
            }
        }
        else {
            for (SearchableControl control : getSearchableControls()) {
                if (searchType == SearchType.TAGS) {
                    if (control instanceof TagLabel || control instanceof TagField
                            || control instanceof TaggingPane || control instanceof LoggingEffortPane) {
                        if (control.advancedQuery(query, searchType) > 0) {
                            if (count < control.advancedQuery(query, searchType)) {
                                count = control.advancedQuery(query, searchType);
                            }
                        }
                    }
                }
                else {
                    if (control.advancedQuery(query, searchType) > 0) {
                        if (count < control.advancedQuery(query, searchType)) {
                            count = control.advancedQuery(query, searchType);
                        }
                    }
                }
            }
        }

        return count;
    }


    /**
     * Selects the Tab based on the tab index
     * @param index The index of the tab
     * @return whether the index is selected
     */
    public boolean select(int index) {
        try {
            getTabPane().getSelectionModel().select(index);
            return true;
        }
        catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }


    /**
     * Selects a tab based on a tab
     */
    public void select() {
        getTabPane().getSelectionModel().select(this);
    }
}
