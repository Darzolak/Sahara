/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.scenes.dialog;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Callback;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.CustomDatePicker;
import seng302.group2.scenes.control.CustomTextArea;
import seng302.group2.scenes.control.CustomTextField;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.workspace.person.Person;

import java.io.File;
import java.time.LocalDate;
import java.util.Map;

import static seng302.group2.util.validation.NameValidator.validateName;
import static seng302.group2.util.validation.ShortNameValidator.validateShortName;


/**
 * Class to create a pop up dialog for creating a person.
 *
 * @author swi67
 */
public class CreatePersonDialog extends Dialog<Map<String, String>> {

    private Boolean correctDate = Boolean.TRUE;
    private Boolean correctShortName = Boolean.FALSE;
    private Boolean correctFirstName = Boolean.FALSE;
    private Boolean correctLastName = Boolean.FALSE;

    /**
     * Displays the Dialog box for creating a person.
     */
    public CreatePersonDialog() {
        correctDate = Boolean.TRUE;
        correctShortName = Boolean.FALSE;
        correctFirstName = Boolean.FALSE;
        correctLastName = Boolean.FALSE;


        this.setTitle("New Person");
        this.getDialogPane().setStyle(" -fx-max-width:600px; -fx-max-height: 400px; -fx-pref-width: 600px; "
                + "-fx-pref-height: 400px;");

        ButtonType btnTypeCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnTypeCreate, ButtonType.CANCEL);

        VBox grid = new VBox();
        grid.spacingProperty().setValue(10);
        Insets insets = new Insets(20, 20, 20, 20);
        grid.setPadding(insets);

        // Add elements to grid
        RequiredField shortNameCustomField = new RequiredField("Short Name:");
        RequiredField firstNameCustomField = new RequiredField("First Name:");
        RequiredField lastNameCustomField = new RequiredField("Last Name:");
        CustomTextField emailTextField = new CustomTextField("Email:");
        CustomDatePicker birthDatePicker = new CustomDatePicker("Birth Date:", false);
        CustomTextArea descriptionTextArea = new CustomTextArea("Description:");

        final Callback<DatePicker, DateCell> birthDateCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isAfter(LocalDate.now())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        birthDatePicker.getDatePicker().setDayCellFactory(birthDateCellFactory);

        grid.getChildren().addAll(shortNameCustomField, firstNameCustomField, lastNameCustomField,
                emailTextField, birthDatePicker, descriptionTextArea);

        //Add grid of controls to dialog
        this.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> shortNameCustomField.getTextField().requestFocus());

        Node createButton = this.getDialogPane().lookupButton(btnTypeCreate);
        createButton.setDisable(true);

        //Validation
        shortNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctShortName = validateShortName(shortNameCustomField, null);
            createButton.setDisable(!correctUserInput());
        });

        firstNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newvalue) -> {
            correctFirstName = validateName(firstNameCustomField);
            createButton.setDisable(!correctUserInput());
        });

        lastNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctLastName = validateName(lastNameCustomField);
            createButton.setDisable(!correctUserInput());
        });

        birthDatePicker.getDatePicker().valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable,
                                LocalDate oldValue, LocalDate newValue) {
                if (newValue != null && newValue.isAfter(LocalDate.now())) {
                    ValidationStyle.borderGlowRed(birthDatePicker.getDatePicker());
                    ValidationStyle.showMessage("A Persons birth date must be in the past",
                            birthDatePicker.getDatePicker());
                }
                else {
                    ValidationStyle.borderGlowNone(birthDatePicker.getDatePicker());
                }
                createButton.setDisable(!correctUserInput());
            }
        });

        this.setResultConverter(b -> {
            if (b == btnTypeCreate) {
                //get user input
                String firstName = firstNameCustomField.getText();
                String lastName = lastNameCustomField.getText();
                String shortName = shortNameCustomField.getText();
                String email = emailTextField.getText();
                String description = descriptionTextArea.getText();

                LocalDate birthDate = birthDatePicker.getValue();

                if (firstName.equals("John") && lastName.equals("Cena")) {
                    String path = ("resources/media/Cena.mp3");
                    Media hit = new Media(new File(path).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(hit);
                    mediaPlayer.play();
                }

                Person person = new Person(shortName, firstName, lastName, email, description,
                        birthDate);
                Global.currentWorkspace.add(person);
                App.mainPane.selectItem(person);
                this.close();
            }
            return null;
        });

        this.setResizable(false);
        this.show();
    }

    private boolean correctUserInput() {
        return correctShortName && correctFirstName && correctLastName && correctDate;
    }
}
