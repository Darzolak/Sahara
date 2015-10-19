package seng302.group2.scenes.control;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.scenes.MainPane;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableListView;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.SaharaItem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Class for Filtering a listview via a Textbox.
 *
 * Created by crw73 on 20/09/15.
 */
public class FilteredListView<T> extends VBox implements SearchableControl {

    private Set<SearchableControl> searchControls = new HashSet<>();
    private ObservableList<T> originalData = observableArrayList();
    private ObservableList<T> sortedData = observableArrayList();
    private TextField inputText = new TextField();
    private SearchableListView<T> listView;


    /**
     * Constructor for the VBox for Filtered Listviews
     * @param data The data to be displayed in the filtered list view
     */
    public FilteredListView(ObservableList<T> data) {
        this.setMaxWidth(275);
        this.setPrefWidth(275);
        originalData = data;
        inputText.setPromptText("Filter list...");
        listView = new SearchableListView<>(data);
        inputText.setOnKeyReleased(event -> {
            sortedData.clear();
            for (T item : originalData) {
                if (item.toString().toLowerCase().contains(inputText.getText().toLowerCase())) {
                    sortedData.add(item);
                }
            }
            listView.setItems(sortedData);
        });
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (listView.getSelectionModel().getSelectedItem() != null && listView.getSelectionModel()
                        .getSelectedItem() instanceof SaharaItem) {
                    SaharaItem selectedItem = ((SaharaItem) listView.getSelectionModel().getSelectedItem());
                    App.mainPane.selectItem(selectedItem);
                }


            }
            event.consume();
        });


        this.getChildren().addAll(
                inputText,
                listView
        );

        Collections.addAll(searchControls, listView);
    }

    /**
     * Constructor for the VBox for Filtered Listviews
     * @param data The data to be displayed in the filtered list view
     * @param promptText Default text in search box
     */
    public FilteredListView(ObservableList<T> data, String promptText) {
        this.setMaxWidth(275);
        this.setPrefWidth(275);
        originalData = data;
        inputText.setPromptText("Filter " + promptText + "...");
        listView = new SearchableListView<>(data);
        inputText.setOnKeyReleased(event -> {
            sortedData.clear();
            for (T item : originalData) {
                if (item.toString().toLowerCase().contains(inputText.getText().toLowerCase())) {
                    sortedData.add(item);
                }
            }
            listView.setItems(sortedData);
        });


        this.getChildren().addAll(
                inputText,
                listView
        );

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (listView.getSelectionModel().getSelectedItem() != null && listView.getSelectionModel()
                        .getSelectedItem() instanceof SaharaItem) {
                    SaharaItem selectedItem = ((SaharaItem) listView.getSelectionModel().getSelectedItem());
                    App.mainPane.selectItem(selectedItem);
                }


            }
            event.consume();
        });

        Collections.addAll(searchControls, listView);
    }

    /**
     * Gets the Searchable Listview
     * @return the Searchable Listview
     */
    public SearchableListView<T> getListView() {
        return listView;
    }

    /**
     * Resets the data of the Listview based on the current input string.
     */
    public void resetInputText() {
        sortedData.clear();
        for (T item : originalData) {
            if (item.toString().toLowerCase().contains(inputText.getText().toLowerCase())) {
                sortedData.add(item);
            }
        }
        listView.setItems(sortedData);
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
        return 0;
    }
}
