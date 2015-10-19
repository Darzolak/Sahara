package seng302.group2.scenes.control.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by jml168 on 11/08/15.
 */
public class SearchableTable<T> extends TableView<T> implements SearchableControl {

    public ObservableList<T> matchingItems = FXCollections.observableArrayList();

    /**
     * Basic constructor.
     */
    public SearchableTable() {
        super();
        setFactory();
    }


    /**
     * Basic constructor. Sets the data of the table to that provided in the parameter.
     * @param data the new data of the table
     */
    public SearchableTable(ObservableList<T> data) {
        super();
        setFactory();
        setItems(data);
    }


    /**
     * Basic constructor. Sets the data of the table to that provided in the parameter.
     * @param data the new data of the table
     * @param rowFactory the table row factory that is used for the table. Default is SearchableTableRow
     */
    public SearchableTable(ObservableList<T> data, SearchableTableRow<T> rowFactory) {
        super();
        setFactory();
        setItems(data);
    }


    /**
     * Basic constructor. Sets the data of the table to that provided in the parameter.
     * @param data the new data of the table
     * @param searchableControls The collection of searchable controls to add this control to
     */
    public SearchableTable(ObservableList<T> data, Collection<SearchableControl> searchableControls) {
        super();
        searchableControls.add(this);
        setFactory();
        setItems(data);
    }


    /**
     * Basic constructor. Sets the data of the table to that provided in the parameter.
     * @param data the new data of the table
     * @param rowFactory the table row factory that is used for the table. Default is SearchableTableRow
     * @param searchableControls The collection of searchable controls to add this control to
     */
    public SearchableTable(ObservableList<T> data, SearchableTableRow<T> rowFactory,
                           Collection<SearchableControl> searchableControls) {
        super();
        searchableControls.add(this);
        setFactory();
        setItems(data);
    }


    /**
     * Checks all the elements in the table to see if they have a matching string.
     * @param query the string to be queried
     * @return whether or not a matching string was found
     */
    @Override
    public boolean query(String query) {
        //Set<T> tableItems = new HashSet<>();
        matchingItems.clear();
        if (!query.isEmpty()) {
            ObservableList<TableColumn<T, ?>> cols = this.getColumns();

            for (T aData : this.getItems()) {

                for (TableColumn<T, ?> col : cols) {
                    if (col.getCellData(aData) != null) {
                        String cellValue = col.getCellData(aData).toString();
                        cellValue = cellValue.toLowerCase();
                        if (cellValue.contains(query.trim().toLowerCase())) {
                            matchingItems.add(aData);
                        }
                    }

                }
            }
        }

        setFactory();
        return !matchingItems.isEmpty();
    }

    /**
     * Refreshes the table
     * @param table The table to refresh
     * @param tableList The items of the table
     * @param <T> Anonymous type
     */
    public static <T> void refresh(final TableView<T> table, final List<T> tableList) {
        //Wierd JavaFX bug
        table.setItems(null);
        table.layout();
        table.setItems(FXCollections.observableList(tableList));
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


    /**
     * Highlights a row if a matching query is found within that row. If there is no matching query,
     * the row's style is set to null (default).
     */
    void setFactory() {
        this.setRowFactory(tv -> new SearchableTableRow<>(this));
    }

    /**
     * Searches the table and finds any matching items.
     */
    @Override
    public int advancedQuery(String query, SearchType searchType) {
        matchingItems.clear();
        int count = 0;
        if (!query.isEmpty()) {
            ObservableList<TableColumn<T, ?>> cols = this.getColumns();

            for (T aData : this.getItems()) {

                for (TableColumn<T, ?> col : cols) {
                    if (col.getCellData(aData) != null) {
                        String cellValue = col.getCellData(aData).toString();

                        cellValue = cellValue.toLowerCase();
                        if (searchType == SearchType.NORMAL) {
                            if (cellValue.equals(query.trim().toLowerCase())) {
                                matchingItems.add(aData);
                                count = 2;
                            }
                            if (cellValue.contains(query.trim().toLowerCase())) {
                                matchingItems.add(aData);
                                count = 1;
                            }
                        }
                        else if (searchType == SearchType.REGEX) {
                            if (Pattern.matches(query.trim(), cellValue)) {
                                matchingItems.add(aData);
                            }
                        }
                    }
                }
            }
        }

        setFactory();
        return count;
    }
}
