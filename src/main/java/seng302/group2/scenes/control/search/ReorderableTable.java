package seng302.group2.scenes.control.search;

import javafx.collections.ObservableList;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.util.Collection;

/**
 * A searchable table that also allows the reordering of items in a list via drag and drop.
 * Created by Jordane on 29/08/2015.
 */
public class ReorderableTable<T> extends SearchableTable<T> {

    /**
     * Keep track of yourself (only for reference in setFactory method)
     */
    private TableView<T> table = this;


    /**
     * The constructor for a reorderable table
     * @param itemList The list of items to populate the table with and reorder
     */
    public ReorderableTable(ObservableList<T> itemList) {
        setItems(itemList);
    }


    /**
     * The constructor for a reorderable table that adds itself to a collection of searchable controls
     * @param itemList The list of items to populate the table with and reorder
     * @param searchableControls A collection of searchable controls to add this control to
     */
    public ReorderableTable(ObservableList<T> itemList, Collection<SearchableControl> searchableControls) {
        setItems(itemList);
        searchableControls.add(this);
    }


    /**
     * Highlights a row if a matching query is found within that row. If there is no matching query,
     * the row's style is set to null (default).
     */
    @Override
    void setFactory() {
        setRowFactory(tr -> {
                TableRow<T> row = new TableRow<T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            if (matchingItems.contains(item)) {
                                setStyle("-fx-background-color: " + SearchableControl.highlightColourString + "; ");
                            }
                            else {
                                setStyle(null);
                            }
                        }
                    }
                };

                row.setOnDragDetected(event -> {
                        if (!row.isEmpty()) {
                            Integer index = row.getIndex();
                            Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                            db.setDragView(row.snapshot(null, null));
                            ClipboardContent cc = new ClipboardContent();
                            cc.put(DataFormat.PLAIN_TEXT, index);
                            db.setContent(cc);
                            event.consume();
                        }
                    });

                row.setOnDragOver(event -> {
                        Dragboard db = event.getDragboard();
                        if (db.hasContent(DataFormat.PLAIN_TEXT)) {
                            if (row.getIndex() != (Integer) db.getContent(DataFormat.PLAIN_TEXT)) {
                                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                                event.consume();
                            }
                        }
                    });

                row.setOnDragDropped(event -> {
                        Dragboard db = event.getDragboard();
                        if (db.hasContent(DataFormat.PLAIN_TEXT)) {
                            int draggedIndex = (Integer) db.getContent(DataFormat.PLAIN_TEXT);
                            T draggedItem = table.getItems().remove(draggedIndex);

                            int dropIndex;

                            if (row.isEmpty()) {
                                dropIndex = table.getItems().size();
                            }
                            else {
                                dropIndex = row.getIndex();
                            }

                            table.getItems().add(dropIndex, draggedItem);

                            event.setDropCompleted(true);
                            table.getSelectionModel().select(dropIndex);
                            event.consume();
                        }
                    });

                return row;
            });
    }
}
