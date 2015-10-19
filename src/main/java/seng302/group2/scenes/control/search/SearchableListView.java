package seng302.group2.scenes.control.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

import java.util.Collection;
import java.util.regex.Pattern;

/**
 * A custom searchable list view that highlights matching items when queried
 * Created by btm38 on 3/08/15.
 */
public class SearchableListView<T> extends ListView<T> implements SearchableControl {
    ObservableList<T> matchingItems = FXCollections.observableArrayList();

    /**
     * Basic constructor for a SearchableListView
     */
    public SearchableListView() {
        super();
        this.setPrefHeight(240);
    }

    /**
     * Constructor for a SearchableListView that takes an initial set of items
     * @param listItems The initial items to add to the list
     */
    public SearchableListView(ObservableList<T> listItems) {
        super(listItems);
        this.setPrefHeight(240);
    }


    /**
     * Constructor for a SearchableListView that takes an initial set of items and
     * @param listItems The initial items to add to the list
     * @param searchableControls A collection of searchable controls to add this control to
     */
    public SearchableListView(ObservableList<T> listItems, Collection<SearchableControl> searchableControls) {
        super(listItems);
        searchableControls.add(this);
        this.setPrefHeight(240);
    }

    /**
     * Queries the contents of the list view, highlighting any contents that match
     * @param query The search string to query the list contents with
     * @return If a match was found for the query
     */
    @Override
    public boolean query(String query) {
        boolean foundList = false;
        for (T item : this.getItems()) {
            if (item.toString().toLowerCase().contains(query.toLowerCase())) {
                foundList = true;
                matchingItems.add(item);
            }
        }

        this.setCellFactory(param -> new ListCell<T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                }
                else {
                    if (query.trim().isEmpty()) {
                        setText(item.toString());
                        setStyle("-fx-background-color: inherit");
                    }
                    else if (queryCell(query, item.toString())) {
                        setText(item.toString());
                        setStyle("-fx-background-color:" + SearchableControl.highlightColourString + ";");
                    }
                    else {
                        setText(item.toString());
                        setStyle("-fx-background-color: inherit");
                    }
                }
            }
        });

        return foundList;
    }


    /**
     * Sets a consistent, well-placed placeholder with the given text
     * @param placeholderText The text of the placeholder
     */
    public void setPlaceholder(String placeholderText) {
        HBox box = new HBox();
        SearchableText searchableText = new SearchableText(placeholderText);
        box.setFillHeight(true);
        box.setAlignment(Pos.CENTER);
        searchableText.setTextAlignment(TextAlignment.CENTER);
        box.getChildren().add(searchableText);
        setPlaceholder(box);
    }

    /**
     * Sets a consistent, well-placed placeholder with the given searchable text
     * @param placeholderText The searchable text of the placeholder
     */
    public void setPlaceholder(SearchableText placeholderText) {
        HBox box = new HBox();
        box.setFillHeight(true);
        box.setAlignment(Pos.CENTER);
        placeholderText.setTextAlignment(TextAlignment.CENTER);
        box.getChildren().add(placeholderText);
        setPlaceholder(box);
    }

    private boolean queryCell(String query, String string) {
        return !query.trim().isEmpty() && string.toLowerCase().contains(query.toLowerCase());
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        int count = 0;
        if (searchType == SearchType.NORMAL) {
            for (T item : this.getItems()) {
                if (item.toString().toLowerCase().equals(query.toLowerCase())) {
                    count = 3;
                    matchingItems.add(item);
                }
                else if (item.toString().toLowerCase().contains(query.toLowerCase())) {
                    count = 1;
                    matchingItems.add(item);
                }
            }
        }
        else if (searchType == SearchType.REGEX) {
            for (T item : this.getItems()) {
                if (Pattern.matches(query, item.toString().toLowerCase())) {
                    count = 1;
                    matchingItems.add(item);
                }
            }

        }


        this.setCellFactory(param -> new ListCell<T>() {
            @Override
            public void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                }
                else {
                    if (query.trim().isEmpty()) {
                        setText(item.toString());
                        setStyle("-fx-background-color: inherit");
                    }
                    else if (queryCell(query, item.toString())) {
                        setText(item.toString());
                        setStyle("-fx-background-color:" + SearchableControl.highlightColourString + ";");
                    }
                    else {
                        setText(item.toString());
                        setStyle("-fx-background-color: inherit");
                    }
                }
            }
        });

        return count;
    }
}
