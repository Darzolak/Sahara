package seng302.group2.scenes.information;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.search.SearchableScene;
import seng302.group2.workspace.SaharaItem;

/**
 * A class for creating sticky bar.
 * Created by cvs20 on 19/09/15.
 */
public class StickyBar extends HBox {

    Button editButton = new Button("Edit");
    Button doneButton = new Button("Done");
    Button cancelButton = new Button("Cancel");

    public StickyBar() {
        this.setSpacing(8);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new Insets(4));

        addEvents();
    }

    /**
     * Adds event handlers for the buttons on the sticky bar
     */
    void addEvents() {
        editButton.setOnAction(event -> {
            try {
                SaharaItem selected = (SaharaItem) Global.selectedTreeItem.getValue();
                selected.switchToInfoScene(true);
            }
            catch (NullPointerException ex) {
                System.out.println("nothing selected");
            }
        });

        doneButton.setOnAction(event -> {
            try {
                ((SearchableScene)App.mainPane.contentPane.getContent()).done();
            }
            catch (NullPointerException ex) {

            }
        });

        cancelButton.setOnAction(event -> {
            try {
                SaharaItem selected = (SaharaItem) Global.selectedTreeItem.getValue();
                selected.switchToInfoScene(false);
            }
            catch (NullPointerException ex) {

            }
        });
    }


    /**
     * Reconstruct the sticky bar with the given type of buttons
     * @param type The buttons to add to the bar
     */
    public void construct(STICKYTYPE type) {
        this.getChildren().clear();

        if (type == STICKYTYPE.INFO) {
            editButton.setDisable(false);
            this.getChildren().add(editButton);
        }
        if (type == STICKYTYPE.EDIT) {
            doneButton.setDisable(false);
            this.getChildren().add(doneButton);
            this.getChildren().add(cancelButton);
        }
        if (type == STICKYTYPE.OTHER) {
            editButton.setDisable(true);
            this.getChildren().add(editButton);
        }
        if (type == STICKYTYPE.EDITDISABLED) {
            doneButton.setDisable(true);
            this.getChildren().add(doneButton);
            this.getChildren().add(cancelButton);
        }

    }

    /**
     * An enum for the states of the Task. Also includes a toString method for GUI application of TaskStates
     */
    public enum STICKYTYPE {
        INFO,
        EDIT,
        EDITDISABLED,
        OTHER
    }
}

