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
import seng302.group2.scenes.control.CustomComboBox;
import seng302.group2.scenes.control.CustomTextArea;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.story.Story;

import java.util.Map;

import static seng302.group2.util.validation.NameValidator.validateName;
import static seng302.group2.util.validation.PriorityFieldValidator.validatePriorityField;
import static seng302.group2.util.validation.ShortNameValidator.validateShortName;

/**
 * Class to create a pop up dialog for creating a story
 *
 * @author swi67
 */
public class CreateStoryDialog extends Dialog<Map<String, String>> {
    private static Boolean correctShortName = Boolean.FALSE;
    private static Boolean correctCreator = Boolean.FALSE;
    private static Boolean correctLongName = Boolean.FALSE;
    private static Boolean correctPriority = Boolean.FALSE;

    /**
     * Constructor for the CreateStoryDialog class. Creates and displays a JavaFX
     * dialog used to create new sprints. A default project is selected in the project combo box.
     *
     * @param defaultProject the selected project combobox option. set to null for no default option.
     */
    public CreateStoryDialog(Project defaultProject) {
        correctCreator = false;
        correctLongName = false;
        correctShortName = false;
        correctPriority = false;

        // Initialise Dialog
        this.setTitle("New Story");
        this.getDialogPane().setStyle(" -fx-max-width:600px; -fx-max-height: 400px; -fx-pref-width: 600px; "
                + "-fx-pref-height: 400px;");

        VBox grid = new VBox();
        grid.spacingProperty().setValue(10);
        Insets insets = new Insets(20, 20, 20, 20);
        grid.setPadding(insets);

        ButtonType btnTypeCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnTypeCreate, ButtonType.CANCEL);


        // Add elements to grid
        RequiredField shortNameCustomField = new RequiredField("Short Name:");
        RequiredField longNameCustomField = new RequiredField("Long Name:");
        RequiredField creatorCustomField = new RequiredField("Creator:");
        CustomTextArea descriptionTextArea = new CustomTextArea("Description:");
        CustomComboBox<Project> projectComboBox = new CustomComboBox("Project:", true);
        projectComboBox.getComboBox().setItems(Global.currentWorkspace.getProjects());
        if (defaultProject == null) {
            projectComboBox.getComboBox().setValue(Global.currentWorkspace.getProjects().get(0));
        }
        else {
            projectComboBox.setValue(defaultProject);

        }
        RequiredField priorityNumberField = new RequiredField("Priority:");

        grid.getChildren().addAll(shortNameCustomField, longNameCustomField, creatorCustomField,
                priorityNumberField, projectComboBox, descriptionTextArea);

        //Add grid of controls to dialog
        this.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> shortNameCustomField.getTextField().requestFocus());

        Node createButton = this.getDialogPane().lookupButton(btnTypeCreate);
        createButton.setDisable(true);

        //Validation
        shortNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctShortName = validateShortName(shortNameCustomField, null);
            createButton.setDisable(!(correctShortName && correctCreator && correctPriority && correctLongName));
        });

        creatorCustomField.getTextField().textProperty().addListener((observable, oldValue, newvalue) -> {
            correctCreator = validateName(creatorCustomField);
            createButton.setDisable(!(correctShortName && correctCreator && correctPriority && correctLongName));
        });

        longNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctLongName = validateName(longNameCustomField);
            createButton.setDisable(!(correctShortName && correctCreator && correctPriority && correctLongName));
        });

        priorityNumberField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctPriority = validatePriorityField(priorityNumberField, null, null);
            createButton.setDisable(!(correctShortName && correctCreator && correctPriority && correctLongName));
        });

        this.setResultConverter(b -> {
            if (b == btnTypeCreate) {

                if (correctShortName && correctLongName && correctCreator && correctPriority) {

                    //get user input
                    String shortName = shortNameCustomField.getText();
                    String longName = longNameCustomField.getText();
                    String creator = creatorCustomField.getText();
                    String description = descriptionTextArea.getText();
                    Integer priority = Integer.parseInt(priorityNumberField.getText());

                    Project project = projectComboBox.getValue();
                    Story story = new Story(shortName, longName, description, creator, project,
                            priority);
                    project.add(story);
                    App.mainPane.selectItem(story);
                    this.close();
                }
            }
            return null;
        });
        this.setResizable(false);
        this.show();
    }

}
