package seng302.group2.scenes.control;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import seng302.group2.workspace.SaharaItem;

/**
 * Custom table cell with a time text field
 * Created by swi67 on 20/09/15.
 */
public class TimeTextFieldEditCell extends TableCell<SaharaItem, String> {
    private TimeTextField timeTextField;



    public TimeTextFieldEditCell() {

    }

    /**
     * Start editing in the time text field
     */
    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createTimeTextField();
            setGraphic(timeTextField);

            if (!getText().isEmpty()) {
                timeTextField.setText(getText());
            }
            else {
                timeTextField.setText(null);
            }
            Platform.runLater(() -> timeTextField.requestFocus());
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
                    timeTextField.setText(getItem());
                }
                setGraphic(timeTextField);
            }
            else {
                if (timeTextField == null) {
                    setText(getString());
                }
                else {
                    if (timeTextField.getText().isEmpty()) {
                        setText(getString());
                    }
                    else {
                        setText(timeTextField.getText());
                    }
                }
                setGraphic(null);
            }
        }
    }


    /**
     * Creates a time textfield for use in the editing cell
     */
    private void createTimeTextField() {
        timeTextField = new TimeTextField();
        timeTextField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        timeTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0,
                                Boolean arg1, Boolean arg2) {
                if (!arg2) {
                    if (timeTextField.getText() != null) {
                        commitEdit(timeTextField.getText());
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
        if (getItem().isEmpty()) {
            return getItem();
        }
        else {
            return getItem() == null ? "" : getItem();
        }
    }

}
