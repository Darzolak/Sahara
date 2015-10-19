package seng302.group2.scenes.dialog;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.team.Team;

import java.util.Map;

import static javafx.collections.FXCollections.observableArrayList;
import static seng302.group2.util.validation.NameValidator.validateName;
import static seng302.group2.util.validation.ShortNameValidator.validateShortName;

/**
 * Class to create a pop up dialog for creating a backlog
 * Created by cvs20 on 19/05/15.
 */
public class CreateBacklogDialog extends Dialog<Map<String, String>> {
    private Boolean correctShortName = Boolean.FALSE;
    private Boolean correctLongName = Boolean.FALSE;
    private Boolean required = true;
    private CustomComboBox<Project> projComboBox = new CustomComboBox<>("Project: ", required);
    private CustomComboBox<Person> productOwnerComboBox = new CustomComboBox<>("Product Owner: ", required);
    private CustomComboBox<String> scaleComboBox = new CustomComboBox<>("Scale: ", required);

    /**
     * Shows a backlog creation dialog. A default project is selected in the project combo box.
     *
     * @param defaultProject The project to be used as the default project.
     */
    public CreateBacklogDialog(Project defaultProject) {
        correctShortName = Boolean.FALSE;
        correctLongName = Boolean.FALSE;

        this.setTitle("New Backlog");
        this.getDialogPane().setStyle(" -fx-max-width:600px; -fx-max-height: 350px; -fx-pref-width: 600px; "
                + "-fx-pref-height: 350px;");

        VBox grid = new VBox();
        grid.spacingProperty().setValue(10);
        Insets insets = new Insets(20, 20, 20, 20);
        grid.setPadding(insets);

        ButtonType btnTypeCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnTypeCreate, ButtonType.CANCEL);

        RequiredField shortNameCustomField = new RequiredField("Short Name:");
        RequiredField longNameCustomField = new RequiredField("Long Name:");
        CustomTextArea descriptionTextArea = new CustomTextArea("Description:");

        // Create project combo box.
        ObservableList<Project> projectOptions = observableArrayList();
        projComboBox.getComboBox().getItems().addAll(projectOptions);

        for (Project project : Global.currentWorkspace.getProjects()) {
            projComboBox.getComboBox().getItems().add(project);
        }
        if (defaultProject != null) {
            projComboBox.getComboBox().setValue(defaultProject);
        }


        for (String scaleName : Global.currentWorkspace.getEstimationScales().getEstimationScaleDict().keySet()) {
            scaleComboBox.getComboBox().getItems().add(scaleName);
        }

        for (Team team : Global.currentWorkspace.getTeams()) {
            if (team.getProductOwner() != null) {
                productOwnerComboBox.getComboBox().getItems().add(team.getProductOwner());
            }
        }

        grid.getChildren().addAll(shortNameCustomField, longNameCustomField,
                projComboBox, productOwnerComboBox, scaleComboBox, descriptionTextArea);

        // Request focus on the short name field by default.
        Platform.runLater(() -> shortNameCustomField.getTextField().requestFocus());

        //Add grid of controls to dialog
        this.getDialogPane().setContent(grid);

        Node createButton = this.getDialogPane().lookupButton(btnTypeCreate);
        createButton.setDisable(true);

        // Validation.
        shortNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctShortName = validateShortName(shortNameCustomField, null);
            createButton.setDisable(!(correctShortName && correctLongName
                    && projectSelected() & poSelected() & scaleSelected()));
        });

        longNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctLongName = validateName(longNameCustomField);
            createButton.setDisable(!(correctShortName && correctLongName
                    && projectSelected() & poSelected() & scaleSelected()));
        });

        projComboBox.getComboBox().valueProperty().addListener(new ChangeListener<Project>() {
            @Override
            public void changed(ObservableValue<? extends Project> observable, Project oldValue, Project newValue) {
                createButton.setDisable(!(correctShortName && correctLongName
                        && projectSelected() && poSelected() && scaleSelected()));
            }
        });

        productOwnerComboBox.getComboBox().valueProperty().addListener(new ChangeListener<Person>() {
            @Override
            public void changed(ObservableValue<? extends Person> observable, Person oldValue, Person newValue) {
                createButton.setDisable(!(correctShortName && correctLongName
                        && projectSelected() && poSelected() && scaleSelected()));
            }
        });

        scaleComboBox.getComboBox().valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                createButton.setDisable(!(correctShortName && correctLongName
                        && projectSelected() && poSelected() && scaleSelected()));
            }
        });

        // Create button event
        this.setResultConverter(b -> {
            if (b == btnTypeCreate) {
                String shortName = shortNameCustomField.getText();
                String longName = longNameCustomField.getText();
                String description = descriptionTextArea.getText();

                Project project = projComboBox.getComboBox().getValue();
                Person productOwner = productOwnerComboBox.getValue();
                String scale = scaleComboBox.getValue();

                Backlog backlog = new Backlog(shortName, longName, description, productOwner,
                        project, scale);

                project.add(backlog);
                App.mainPane.selectItem(backlog);
                this.close();

            }
            return null;
        });

        this.setResizable(false);
        this.show();
    }

    private Boolean projectSelected() {
        return !(projComboBox.getComboBox().getValue() == null);
    }

    private Boolean poSelected() {
        return !(productOwnerComboBox.getComboBox().getValue() == null);
    }

    private Boolean scaleSelected() {
        return !(scaleComboBox.getComboBox().getValue() == null);
    }
}