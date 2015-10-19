package seng302.group2.scenes.control;

/**
 * Custom Table Cell for editing with a date picker
 * Created by swi67 on 20/09/15.
 */

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import seng302.group2.Global;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.information.project.ProjectHistoryTab;
import seng302.group2.scenes.information.team.TeamHistoryTab;
import seng302.group2.workspace.SaharaItem;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatePickerEditCell extends TableCell<SaharaItem, String> {

    private DatePicker datePicker;
    private boolean validEdit = false;
    private SearchableTab historyTab;

    /**
     * Blank constructor for EditingCell
     */
    public DatePickerEditCell() {
        validEdit = true;
    }

    /**
     * Constructor to be used for allocation history table
     * @param currentTab the tab which contains the cell's table view
     */
    public DatePickerEditCell(SearchableTab currentTab) {
        historyTab = currentTab;
    }

    /**
     * Start editing the date picker cell
     */
    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createDateField();
            setGraphic(datePicker);

            if (!getText().isEmpty()) {
                datePicker.setValue(LocalDate.parse(getText(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            else {
                datePicker.setValue(null);
            }
            Platform.runLater(() -> {
                datePicker.requestFocus();
            });

        }
    }

    /**
     * Cancel the editing of the cell.
     */
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(null);
    }

    /**
     * Updates the item in the cell
     * @param item The item to update
     * @param empty Whether the cell is empty or not
     */
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        }
        else {
            if (isEditing()) {
                if (!getItem().isEmpty()) {
                    datePicker.setValue(LocalDate.parse(getItem(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                setGraphic(datePicker);
            }
            else {
                if (datePicker == null) {
                    setText(getString());
                }
                else {
                    if (historyTab instanceof ProjectHistoryTab) {
                        this.validEdit = ((ProjectHistoryTab) historyTab).getIsValidEdit();
                    }
                    else if (historyTab instanceof TeamHistoryTab) {
                        this.validEdit = ((TeamHistoryTab) historyTab).getIsValidEdit();
                    }
                    if (datePicker.getValue() == null) {
                        setText(getString());
                    }
                    else if (validEdit) {
                        setText(datePicker.getValue().format(Global.dateFormatter));
                    }
                }

                setGraphic(null);
            }
        }
    }

    /**
     * Creates a text field for use in the editing cell
     */
    private void createDateField() {
        datePicker = new DatePicker();
        datePicker.setMaxWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        datePicker.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean arg1, Boolean arg2) {
                if (!arg2) {
                    if (datePicker.getValue() != null) {
                        commitEdit(datePicker.getValue().toString());
                    }
                    else {
                        commitEdit("");
                    }
                }
                else {
                    updateItem(getItem(), false);
                }
            }
        });
    }

    /**
     * Returns a string in a Date format for use in the cell
     * @return The string in Date format
     */
    private String getString() {
        LocalDate date;

        if (getItem().isEmpty()) {
            return getItem();
        }
        else {
            if (getItem().matches("([0-9]{2})/([0-9]{2})/([0-9]{4})")) {
                date = LocalDate.parse(getItem(),
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            else {
                date = LocalDate.parse(getItem(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            String dateString = date.format(Global.dateFormatter);
            return getItem() == null ? "" : dateString;
        }
    }
}
