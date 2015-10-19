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
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Map;

import static seng302.group2.util.validation.NameValidator.validateName;
import static seng302.group2.util.validation.ShortNameValidator.validateShortName;

/**
 * Class to create a pop up dialog for creating a workspace.
 *
 * @author David Moseley drm127
 */
@SuppressWarnings("deprecation")
public class CreateWorkspaceDialog extends Dialog<Map<String, String>> {

    private Boolean correctShortName = Boolean.FALSE;
    private Boolean correctLongName = Boolean.FALSE;

    /**
     * Displays the Dialog box for creating a workspace.
     */
    public CreateWorkspaceDialog() {
        correctShortName = Boolean.FALSE;
        correctLongName = Boolean.FALSE;
        this.setTitle("New Workspace");
        this.getDialogPane().setStyle(" -fx-max-width:600px; -fx-max-height: 300px; -fx-pref-width: 600px; "
                + "-fx-pref-height: 300px;");

        VBox grid = new VBox();
        grid.spacingProperty().setValue(10);
        Insets insets = new Insets(20, 20, 20, 20);
        grid.setPadding(insets);

        ButtonType btnTypeCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnTypeCreate, ButtonType.CANCEL);

        RequiredField shortNameCustomField = new RequiredField("Short Name:");
        RequiredField longNameCustomField = new RequiredField("Long Name:");
        CustomTextArea descriptionTextArea = new CustomTextArea("Workspace Description:");

        grid.getChildren().add(shortNameCustomField);
        grid.getChildren().add(longNameCustomField);
        grid.getChildren().add(descriptionTextArea);

        // Request focus on the short name field by default.
        Platform.runLater(() -> shortNameCustomField.getTextField().requestFocus());

        //Add grid of controls to dialog
        this.getDialogPane().setContent(grid);

        Node createButton = this.getDialogPane().lookupButton(btnTypeCreate);
        createButton.setDisable(true);

        shortNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctShortName = validateShortName(shortNameCustomField, null);
            createButton.setDisable(!(correctShortName && correctLongName));
        });

        longNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctLongName = validateName(longNameCustomField);
            createButton.setDisable(!(correctShortName && correctLongName));
        });


        this.setResultConverter(b -> {
            if (b == btnTypeCreate) {
                String shortName = shortNameCustomField.getText();
                String longName = longNameCustomField.getText();
                String description = descriptionTextArea.getText();

                Workspace workspace = new Workspace(shortName, longName, description);
                Global.currentWorkspace = workspace;
                App.mainPane.selectItem(Global.currentWorkspace);
                App.mainPane.refreshAll();
                Global.setCurrentWorkspaceChanged();

                Platform.runLater(() -> {
                    App.mainPane.getTree().selectItem(Global.currentWorkspace);
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);
                    App.mainPane.getTree().getSelectionModel().getSelectedItem().setExpanded(true);
                });

                this.close();
            }
            return null;
        });

        this.setResizable(false);
        this.show();
    }
}
