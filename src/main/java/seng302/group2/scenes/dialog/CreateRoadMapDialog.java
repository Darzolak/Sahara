/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.scenes.dialog;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.workspace.roadMap.RoadMap;

import java.util.Map;

import static seng302.group2.util.validation.PriorityFieldValidator.validatePriorityField;
import static seng302.group2.util.validation.ShortNameValidator.validateShortName;

/**
 * @author crw73
 */
public class CreateRoadMapDialog extends Dialog<Map<String, String>> {

    private Boolean correctShortName = Boolean.FALSE;
    private Boolean correctPriority = Boolean.FALSE;

    /**
     * Displays the Dialog box for creating a workspace.
     */
    public CreateRoadMapDialog() {
        correctShortName = Boolean.FALSE;
        this.setTitle("New RoadMap");
        this.getDialogPane().setStyle(" -fx-max-width:600px; -fx-max-height: 300px; -fx-pref-width: 600px; "
                + "-fx-pref-height: 300px;");

        VBox grid = new VBox();
        grid.spacingProperty().setValue(10);
        Insets insets = new Insets(20, 20, 20, 20);
        grid.setPadding(insets);

        ButtonType btnTypeCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnTypeCreate, ButtonType.CANCEL);

        RequiredField shortNameCustomField = new RequiredField("Short Name:");
        RequiredField priorityNumberField = new RequiredField("Priority:");

        grid.getChildren().addAll(shortNameCustomField, priorityNumberField);

        // Request focus on the short name field by default.
        Platform.runLater(() -> shortNameCustomField.getTextField().requestFocus());

        //Add grid of controls to dialog
        this.getDialogPane().setContent(grid);

        Node createButton = this.getDialogPane().lookupButton(btnTypeCreate);
        createButton.setDisable(true);

        shortNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctShortName = validateShortName(shortNameCustomField, null);
            createButton.setDisable(!(correctShortName && correctPriority));
        });

        priorityNumberField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctPriority = validatePriorityField(priorityNumberField, null, null);
            createButton.setDisable(!(correctShortName && correctPriority));
        });


        this.setResultConverter(b -> {
            if (b == btnTypeCreate) {
                String shortName = shortNameCustomField.getText();


                if (correctShortName && correctPriority) {
                    Integer priority = new Integer(priorityNumberField.getText());
                    RoadMap roadMap = new RoadMap(shortName, priority);
                    Global.currentWorkspace.add(roadMap);
                    App.mainPane.selectItem(roadMap);
                    this.close();
                }
            }
            return null;
        });

        this.setResizable(false);
        this.show();
    }
}
