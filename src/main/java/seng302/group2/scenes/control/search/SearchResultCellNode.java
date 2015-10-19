package seng302.group2.scenes.control.search;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.workspace.SaharaItem;

/**
 * The content of a search result cell.
 * Created by drm127 on 4/09/15.
 */
public class SearchResultCellNode extends VBox {

    private SaharaItem item = null;
    private String searchedString = "";
    private int noOfResults = 0;
    private String matchingString = "";
    private SearchableTab searchResult = null;
    private TrackedTabPane searchableScene = null;

    /**
     * Constructor
     * @param item The item the search string was found in
     * @param searchString the string being searched for
     * @param searchResult the tab the search string was found in
     * @param noOfResults the number of instances the search string is present in a tab
     * @param scene The tracked tab pane to navigate to when clicked
     */
    public SearchResultCellNode(SaharaItem item, String searchString, SearchableTab searchResult,
                                int noOfResults, TrackedTabPane scene) {
        this.item = item;
        this.searchedString = searchString;
        this.noOfResults = noOfResults;
        this.matchingString = searchResult.toString();
        this.searchResult = searchResult;
        this.searchableScene = scene;

        VBox content = construct();
        this.getChildren().add(content);
    }

    /**
     * Gets the SaharaItem the search string was found in
     * @return The SaharaItemt the query was found in
     */
    public SaharaItem getItem() {
        return item;
    }


    /**
     * Gets the tab that should be displayed when the cell is double clicked
     * @return the tab
     */
    public SearchableTab getTab() {
        return searchResult;
    }

    /**
     * Gets the scene the tab belongs to
     * @return The scene the tab belongs to
     */
    public TrackedTabPane getSearchableScene() {
        return searchableScene;
    }



    /**
     * Constructs a VBox with the result information
     * @return returns a VBox containing the information
     */
    private VBox construct() {
        VBox content = new VBox();
        content.setPrefHeight(48);

        content.setPadding(new Insets(2, 2, 2, 6));
        content.setAlignment(Pos.CENTER_LEFT);

        SearchableText titleLabel = new SearchableText(item.toString() + " - " + item.getClass().getSimpleName());
        titleLabel.injectStyle("-fx-font-weight: bold");

        SearchableText matchLabel;
//        if (noOfResults > 1) {
//            matchLabel = new SearchableText("Found " + noOfResults + " instances of " + searchedString
//                    + " in the " + matchingString);
//        }
//        else {
//            matchLabel = new SearchableText("Found " + noOfResults + " instance of " + searchedString
//                    + " in the " + matchingString);
//        }

        matchLabel = new SearchableText(matchingString);
        matchLabel.injectStyle("-fx-font-size: 85%");

        content.getChildren().addAll(titleLabel, matchLabel);

        HBox.setHgrow(content, Priority.ALWAYS);

        return content;
    }
}
