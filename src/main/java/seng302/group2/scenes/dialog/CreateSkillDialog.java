/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.scenes.dialog;

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
import seng302.group2.workspace.skills.Skill;

import java.util.Map;

import static seng302.group2.util.validation.ShortNameValidator.validateShortName;

/**
 * Class to create a pop up dialog for creating a skill.
 *
 * @author drm127
 */
public class CreateSkillDialog extends Dialog<Map<String, String>> {
    /**
     * Displays the Dialog box for creating a skill.
     */
    public CreateSkillDialog() {
        this.setTitle("New Skill");

        VBox grid = new VBox();
        grid.spacingProperty().setValue(10);
        Insets insets = new Insets(20, 20, 20, 20);
        grid.setPadding(insets);
        this.getDialogPane().setStyle(" -fx-max-width:600px; -fx-max-height: 250px; -fx-pref-width: 600px; "
                + "-fx-pref-height: 250px;");

        ButtonType btnTypeCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnTypeCreate, ButtonType.CANCEL);

        RequiredField shortNameCustomField = new RequiredField("Short Name:");
        CustomTextArea descriptionTextArea = new CustomTextArea("Skill Description:");

        grid.getChildren().add(shortNameCustomField);
        grid.getChildren().add(descriptionTextArea);

        this.getDialogPane().setContent(grid);

        Node createButton = this.getDialogPane().lookupButton(btnTypeCreate);
        createButton.setDisable(true);

        //Validation
        shortNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            Boolean correctShortName = validateShortName(shortNameCustomField, null);
            createButton.setDisable(!correctShortName);
        });

        this.setResultConverter(b -> {
            if (b == btnTypeCreate) {
                String shortName = shortNameCustomField.getText();
                String description = descriptionTextArea.getText();
                Skill skill = new Skill(shortName, description);
                Global.currentWorkspace.add(skill);
                App.mainPane.selectItem(skill);
                this.close();
            }
            return null;
        });

        this.setResizable(false);
        this.show();
    }
}
