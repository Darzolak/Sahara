package seng302.group2.scenes.control.search;

import javafx.scene.control.TableRow;

/**
 * Created by jml168 on 12/08/15.
 */
public class SearchableTableRow<T> extends TableRow<T> {

    public SearchableTable<T> parentTable;

    public SearchableTableRow(SearchableTable<T> table) {
        parentTable = table;
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (item != null) {
            if (parentTable.matchingItems.contains(item)) {
                setStyle("-fx-background-color: " + SearchableControl.highlightColourString + "; ");
            }
            else {
                setStyle(null);
                //setStyle("-fx-background-color: " + Color.TRANSPARENT + ";");
            }
        }
    }
}
