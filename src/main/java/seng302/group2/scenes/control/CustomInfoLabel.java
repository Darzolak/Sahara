package seng302.group2.scenes.control;

import javafx.scene.layout.HBox;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableText;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Class to provide basic functionality so that a label is bolded.
 * Created by btm38 on 1/09/15.
 */
public class CustomInfoLabel extends HBox implements SearchableControl {
    private SearchableText label;
    private SearchableText value;
    private Set<SearchableControl> searchControls = new HashSet<>();

    /**
     * Basic constructor. Given a label string and value string, will return an HBox containing the two.
     * The label string will be bolded.
     *
     * @param labelText The label string
     * @param valueText The value string
     */
    public CustomInfoLabel(String labelText, String valueText) {
        label = new SearchableText(labelText, searchControls);
        if (label.getText().length() < 30) {
            label.setMinWidth(220);
            label.setPrefWidth(230);
        }
        value = new SearchableText(valueText, searchControls);
        label.setStyle("-fx-font-weight: bold");

        this.getChildren().addAll(label, value);
    }

    /**
     * Basic constructor. Given a label string and value string, will return an HBox containing the two.
     * The label string will be bolded.
     *
     * @param labelText          The label string
     * @param valueText          The value string
     * @param searchableControls The collection of searchable controls to add this to
     */
    public CustomInfoLabel(String labelText, String valueText, Collection<SearchableControl> searchableControls) {
        searchableControls.add(this);
        label = new SearchableText(labelText, searchControls);
        value = new SearchableText(valueText, searchControls);
        label.setStyle("-fx-font-weight: bold");

        this.getChildren().addAll(label, value);
    }

    public String getLabel() {
        return label.getText();
    }

    public void setLabel(String labelText) {
        label.setText(labelText);
    }

    public String getValue() {
        return value.getText();
    }

    public void setValue(String valueText) {
        value.setText(valueText);
    }

    @Override
    /**
     * Queries the HBox to find any elements containing the given query string. If found inside
     * the HBox, the matching text will be highlighted.
     * @param query The query string to search
     * @return Whether any elements inside the HBox were found to contain the query string
     */
    public boolean query(String query) {
        boolean found = false;

        for (SearchableControl control : searchControls) {
            if (control.query(query)) {
                found = true;
            }
        }
        return found;
    }

    @Override
    /**
     * Queries the HBox to find any elements containing the given query string. If found inside
     * the HBox, the matching text will be highlighted.
     * @param query The query string to search
     * @return Whether any elements inside the HBox were found to contain the query string
     */
    public int advancedQuery(String query, SearchType searchType) {
        int count = 0;
        if (searchType == SearchType.NORMAL) {
            if (this.value.query(query)) {
                if (this.label.getText().contains("Short")) {
                    if (this.value.getText().trim().equals(query)) {
                        count = 4;
                    }
                    else {
                        count = 2;
                    }
                }
                else {
                    if (this.value.getText().equals(query)) {
                        count = 2;
                    }
                    else {
                        count = 1;
                    }
                }

            }
        }
        else if (searchType == SearchType.REGEX) {
            if (Pattern.matches(query, value.getText())) {
                count = 1;
            }
        }
//        else if (searchType == SearchType.WILDCARD) {
//
//        }
        return count;
    }

}
