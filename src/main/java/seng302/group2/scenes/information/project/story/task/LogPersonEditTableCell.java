package seng302.group2.scenes.information.project.story.task;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.story.tasks.Log;


/**
 * Customised table cell used for editing logger and partner in the log table.
 * Created by swi67 on 31/08/15.
 */
public class LogPersonEditTableCell extends TableCell<Object, String> {
    /**
     * *A subclass of TableCell to bind combo box to the cell
     * to allow for editing
     */
    private ComboBox<Person> comboBox;
    private ObservableList items;
    private LoggingEffortPane pane;

    /**
     * Constructor
     * @param itemList items to populate the combo box with
     * @param pane The pane the table cell is on
     */
    public LogPersonEditTableCell(ObservableList itemList, LoggingEffortPane pane) {
        this.items = itemList;
        this.pane = pane;
    }

    /**
     * Sets the cell to a combo box when focused on.
     */
    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createCombo();
            setGraphic(comboBox);

            if (getText() != null && !getText().isEmpty()) {
                comboBox.setValue((Person)getType());
            }
            else {
                comboBox.setValue(null);
            }
            Platform.runLater(() -> {
                comboBox.requestFocus();
            });
        }
    }

    /**
     * Resets the cell to a label on cancel
     */
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(null);
    }

    /**
     * Updates the item
     * @param item the item to update to
     * @param empty if the cell is empty
     */
//    @Override
//    public void updateItem(String item, boolean empty) {
//        super.updateItem(item, empty);
//
//        if (empty) {
//            setText(null);
//            setGraphic(null);
//        }
//        else {
//            if (isEditing()) {
//                if (!getItem().isEmpty()) {
//                    comboBox.setValue(getItem());
//                }
//
////                setText(getItem());
//                setGraphic(comboBox);
//            }
//            else {
//                if (comboBox == null) {
//                    setText(getItem());
//                }
//
//                setGraphic(null);
//            }
//        }
//    }
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        }
        else {
            if (isEditing()) {
                if (comboBox != null) {
                    comboBox.setValue((Person)getType());
                }
                setText(getItem());
                setGraphic(comboBox);
            }
            else {
                setText(getItem());
                setGraphic(null);
            }
        }
    }


    /**
     * Gets the selected item
     * @return the selected item as a class instance
     */
    private Object getType() {
        Object selected = null;
        for (Object saharaItem : items) {
            if (saharaItem.toString() == getItem()) {
                selected = saharaItem;
            }
        }
        return selected;
    }

    /**
     * Creates the combo box and populates it with the itemList. Updates the value in the cell.
     */
    private void createCombo() {
        comboBox = new ComboBox<Person>(this.items);
        comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        Log selectedLog = (Log) getTableRow().getItem();
        Boolean isPartnerList = true;
        pane.updateObservablePeopleList(pane.getAvailableLoggerList(), selectedLog.getPartner(),
                !isPartnerList);
        pane.updateObservablePeopleList(pane.getAvailablePartnerList(), selectedLog.getLogger(),
                isPartnerList);
//        comboBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable,
//                                Boolean oldValue, Boolean newValue) {
//                System.out.println("new Value" + newValue);
//                System.out.println("oldValue" + oldValue);
//                if (!newValue) {
//                    System.out.println("out");
//                    if (comboBox.getValue() != null) {
//                        System.out.println(comboBox.getValue().toString());
//                        commitEdit(comboBox.getValue().toString());
//                    }
//                    else {
//                        commitEdit("");
//                        System.out.println("committing");
//                    }
//                }
//                else {
//                    System.out.println("in");
//                    updateItem(getItem(), false);
//                    Log selectedLog = (Log) getTableRow().getItem();
//                    Boolean isPartnerList = true;
//                    pane.updateObservablePeopleList(pane.getAvailableLoggerList(), selectedLog.getPartner(),
//                            !isPartnerList);
//                    pane.updateObservablePeopleList(pane.getAvailablePartnerList(), selectedLog.getLogger(),
//                            isPartnerList);
//                }
//            }
//        });
        comboBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                                Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    if (comboBox.getValue() != null) {
                        commitEdit(comboBox.getValue().toString());
                        System.out.println("out");
//                        Log selectedLog = (Log) getTableRow().getItem();
//                        Boolean isPartnerList = true;
//                        pane.updateObservablePeopleList(pane.getAvailableLoggerList(), selectedLog.getPartner(),
//                                !isPartnerList);
//                        pane.updateObservablePeopleList(pane.getAvailablePartnerList(), selectedLog.getLogger(),
//                                isPartnerList);
                    }
                    else {
                        commitEdit("");
                    }
                }
                else {
                    System.out.println("in");
//                    comboBox.setValue((Person)getType());
                    updateItem(getItem(), false);
                }
            }
        });



//        comboBox.valueProperty().addListener(new ChangeListener<Object>() {
//            @Override
//            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//                if (newValue != oldValue) {
//                    System.out.println("in");
//                    if (newValue != null) {
//                        commitEdit(newValue.toString());
//                    }
//                    updateItem(getItem(), false);
//                    Log selectedLog = (Log) getTableRow().getItem();
//                    Boolean isPartnerList = true;
//                    pane.updateObservablePeopleList(pane.getAvailableLoggerList(), selectedLog.getPartner(),
//                            !isPartnerList);
//                    pane.updateObservablePeopleList(pane.getAvailablePartnerList(), selectedLog.getLogger(),
//                            isPartnerList);
//                }
//            }
//        });
    }
}
