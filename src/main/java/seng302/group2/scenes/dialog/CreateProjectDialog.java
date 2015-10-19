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
import seng302.group2.scenes.control.CustomTextArea;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.util.validation.NameValidator;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.project.Project;

import java.util.Map;


/**
 * Class to create a pop up dialog for creating a workspace.
 *
 * @author Jordane Lew jml168
 */

public class CreateProjectDialog extends Dialog<Map<String, String>> {
    /**
     * Displays the Dialog box for creating a workspace.
     */
    private Boolean correctShortName = Boolean.FALSE;
    private Boolean correctLongName = Boolean.FALSE;

    public CreateProjectDialog() {
        correctShortName = false;
        correctLongName = false;
        this.setTitle("New Project");
        this.getDialogPane().setStyle(" -fx-max-width:600px; -fx-max-height: 300px; -fx-pref-width: 600px; "
                + "-fx-pref-height: 300px;");

        VBox grid = new VBox();
        grid.spacingProperty().setValue(10);
        Insets insets = new Insets(20, 20, 20, 20);
        grid.setPadding(insets);

        ButtonType btnTypeCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnTypeCreate, ButtonType.CANCEL);

        //Add grid of controls to dialog
        this.getDialogPane().setContent(grid);

        RequiredField shortNameCustomField = new RequiredField("Short Name:");
        RequiredField longNameCustomField = new RequiredField("Long Name:");
        CustomTextArea descriptionTextArea = new CustomTextArea("Project Description:");

        grid.getChildren().add(shortNameCustomField);
        grid.getChildren().add(longNameCustomField);
        grid.getChildren().add(descriptionTextArea);

        // Request focus on the username field by default.
        Platform.runLater(() -> shortNameCustomField.getTextField().requestFocus());

        Node createButton = this.getDialogPane().lookupButton(btnTypeCreate);
        createButton.setDisable(true);

        shortNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctShortName = ShortNameValidator.validateShortName(shortNameCustomField, null);
            createButton.setDisable(!(correctShortName && correctLongName));
        });

        longNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctLongName = NameValidator.validateName(longNameCustomField);
            createButton.setDisable(!(correctShortName && correctLongName));
        });

        this.setResultConverter(b -> {
            if (b == btnTypeCreate) {
                String shortName = shortNameCustomField.getText();
                String longName = longNameCustomField.getText();
                String description = descriptionTextArea.getText();

                if (correctShortName && correctLongName) {
                    Project project = new Project(shortName, longName, description);
                    Global.currentWorkspace.add(project);
                    App.mainPane.selectItem(project);
                    this.close();
                }
            }
            return null;
        });
        this.setResizable(false);
        this.show();
    }
}
