package seng302.group2.scenes.information.person;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.*;
import seng302.group2.scenes.control.Tooltip;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.util.validation.NameValidator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;
import static seng302.group2.Global.currentWorkspace;
import static seng302.group2.util.validation.ShortNameValidator.validateShortName;

/**
 * A class for displaying a tab used to edit people.
 * Created by btm38 on 30/07/15.
 */
public class PersonEditTab extends SearchableTab {

    private List<SearchableControl> searchControls = new ArrayList<>();
    private Person currentPerson;

    private Person tempPerson = new Person();
    private RequiredField shortNameCustomField = new RequiredField("Short Name:");
    private RequiredField firstNameCustomField = new RequiredField("First Name:");
    private RequiredField lastNameCustomField = new RequiredField("Last Name:");
    private CustomTextField emailTextField = new CustomTextField("Email:");
    private CustomDatePicker birthDatePicker = new CustomDatePicker("Birth Date:", false);
    private CustomComboBox<Team> teamBox = new CustomComboBox<>("Team: ");
    private CustomTextArea descriptionTextArea = new CustomTextArea("Person Description:", 300);
    private FilteredListView personSkillsBox = new FilteredListView<>(tempPerson.getSkills(), "skills");
    private SearchableListView personSkillsList = personSkillsBox.getListView();
    private TagField tagField;

    /**
     * Constructor for the PersonEditTab class. This constructor creates a JavaFX ScrollPane
     * which is populated with relevant controls then shown.
     *
     * @param currentPerson The person being edited
     */
    public PersonEditTab(Person currentPerson) {
        this.currentPerson = currentPerson;
        construct();
    }

    /**
     * Gets all the searchable controls on this tab.
     * @return a collection of all the searchable controls on this tab.
     */
    @Override
    public Collection<SearchableControl> getSearchableControls() {
        return searchControls;
    }

    @Override
    public void construct() {
        // Tab settings
        this.setText("Edit Person");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);

        // Create controls
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


        for (Skill skill : currentPerson.getSkills()) {
            tempPerson.getSkills().add(skill);
        }

        personSkillsBox.setPrefHeight(192);
        personSkillsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        personSkillsBox.setMaxWidth(275);

        ObservableList<Skill> dialogSkills = observableArrayList();
        ObservableList<Skill> dialogSkillsCopy = observableArrayList();

        FilteredListView<Skill> skillsBox = new FilteredListView<>(dialogSkills, "skills");
        SearchableListView skillsList = skillsBox.getListView();
        skillsBox.setPrefHeight(192);
        skillsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        skillsBox.setMaxWidth(275);

        teamBox.getComboBox().setItems(Global.currentWorkspace.getTeams());
        teamBox.setValue(currentPerson.getTeam());

        Button btnAdd = new Button("<");
        Button btnDelete = new Button(">");

        VBox skillsButtons = new VBox();
        skillsButtons.spacingProperty().setValue(10);
        skillsButtons.setAlignment(Pos.CENTER);
        skillsButtons.getChildren().addAll(btnAdd, btnDelete);

        // Set values
        for (SaharaItem projectSkill : currentWorkspace.getSkills()) {
            if (!currentPerson.getSkills().contains(projectSkill)) {
                dialogSkills.add((Skill) projectSkill);
                dialogSkillsCopy.add((Skill) projectSkill);
            }
        }

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentPerson.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);

        shortNameCustomField.setText(currentPerson.getShortName());
        firstNameCustomField.setText(currentPerson.getFirstName());
        lastNameCustomField.setText(currentPerson.getLastName());
        emailTextField.setText(currentPerson.getEmail());
        birthDatePicker.setValue(currentPerson.getBirthDate());
        descriptionTextArea.setText(currentPerson.getDescription());

        shortNameCustomField.setPrefWidth(275);
        firstNameCustomField.setPrefWidth(275);
        lastNameCustomField.setPrefWidth(275);
        emailTextField.setPrefWidth(275);
        birthDatePicker.setPrefWidth(275);
        descriptionTextArea.setPrefWidth(275);
        teamBox.setPrefWidth(275);

        VBox v1 = new VBox(10);
        SearchableText v1Label = new SearchableText("Skills: ");
        v1Label.setStyle("-fx-font-weight: bold");
        v1.getChildren().addAll(v1Label, personSkillsBox);

        VBox v2 = new VBox(10);
        SearchableText v2Label = new SearchableText("Available Skills: ");
        v2Label.setStyle("-fx-font-weight: bold");
        v2.getChildren().addAll(v2Label, skillsBox);

        HBox h1 = new HBox(10);
        h1.getChildren().addAll(v1, skillsButtons, v2);

        teamBox.getComboBox().valueProperty().addListener(new ChangeListener<Team>() {
            @Override
            public void changed(ObservableValue<? extends Team> observable, Team oldValue, Team newValue) {
                ValidationStyle.borderGlowNone(teamBox.getComboBox());
                teamBox.removeTooltip();

                if (newValue != currentPerson.getTeam() && currentPerson.getRole() != null
                        && currentPerson.getRole().toString().equals("Product Owner")) {
                    ValidationStyle.borderGlowRed(teamBox.getComboBox());
                    ValidationStyle.showMessage("This person is currently the Product Owner of the team "
                                    + currentPerson.getTeamName() + "! \n"
                                    + "You must put someone else into the role before you can change this"
                                    + "person's team.",
                            teamBox.getComboBox());
                    teamBox.setTooltip(new Tooltip("This person is currently the Product Owner of the team "
                            + currentPerson.getTeamName() + "! \n"
                            + "You must put someone else into the role before you can change this persons team."));
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDITDISABLED);

                }
                else if (newValue != currentPerson.getTeam() && currentPerson.getRole() != null
                        && currentPerson.getRole().toString().equals("Scrum Master")) {
                    ValidationStyle.borderGlowRed(teamBox.getComboBox());
                    ValidationStyle.showMessage("This person is currently the Scrum Master of the team "
                                    + currentPerson.getTeamName() + "! \n"
                                    + "You must put someone else into the role before you can change this person's"
                                    + " team.",
                            teamBox.getComboBox());
                    teamBox.setTooltip(new Tooltip("This person is currently the Scrum Master of the team "
                            + currentPerson.getTeamName() + "! \n"
                            + "You must put someone else into the role before you can change this persons team."));
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDITDISABLED);

                }
                else {
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDIT);

                }
            }
        });

        birthDatePicker.getDatePicker().valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable,
                                LocalDate oldValue, LocalDate newValue) {
                if (newValue != null && newValue.isAfter(LocalDate.now())) {
                    ValidationStyle.borderGlowRed(birthDatePicker.getDatePicker());
                    ValidationStyle.showMessage("A Persons birth date must be in the past",
                            birthDatePicker.getDatePicker());
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDITDISABLED);

                }
                else {
                    ValidationStyle.borderGlowNone(birthDatePicker.getDatePicker());
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDIT);

                }
            }
        });

        // Fires if the focus is changed. then sets the value of the date picker field.
        birthDatePicker.getDatePicker().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    birthDatePicker.setValue(birthDatePicker.getDatePicker().getConverter().fromString(
                            birthDatePicker.getDatePicker().getEditor().getText()));
                }
            }
        });

        // Events
        btnAdd.setOnAction((event) -> {
            ObservableList<Skill> selectedSkills =
                    skillsList.getSelectionModel().getSelectedItems();
            for (Skill item : selectedSkills) {
                tempPerson.getSkills().add(item);
            }

            dialogSkills.clear();
            for (SaharaItem projectSkill : currentWorkspace.getSkills()) {
                if (!tempPerson.getSkills().contains(projectSkill)) {
                    dialogSkills.add((Skill) projectSkill);
                }
            }
            skillsBox.resetInputText();
            personSkillsBox.resetInputText();
        });

        btnDelete.setOnAction((event) -> {
            ObservableList<Skill> selectedSkills = personSkillsList.getSelectionModel().getSelectedItems();
            for (Skill item : selectedSkills) {
                tempPerson.getSkills().remove(item);
            }

            dialogSkills.clear();
            for (SaharaItem projectSkill : currentWorkspace.getSkills()) {
                if (!tempPerson.getSkills().contains(projectSkill)) {
                    dialogSkills.add((Skill) projectSkill);
                }
            }
            skillsBox.resetInputText();
            personSkillsBox.resetInputText();

        });

        // Add items to pane & search collection
        editPane.getChildren().addAll(
                shortNameCustomField,
                tagBox,
                firstNameCustomField,
                lastNameCustomField,
                emailTextField,
                birthDatePicker,
                descriptionTextArea,
                teamBox,
                h1
        );

        Collections.addAll(searchControls,
                shortNameCustomField,
                firstNameCustomField,
                lastNameCustomField,
                emailTextField,
                birthDatePicker,
                descriptionTextArea,
                teamBox,
                v1Label,
                personSkillsList,
                v2Label,
                skillsList
        );
    }

    /**
     * Cancels the edit
     */
    public void cancel() {
        currentPerson.switchToInfoScene();
    }

    /**
     * Changes the values depending on what the user edits
     */
    public void done() {
        System.out.println("yeah yolo");
        Team selectedTeam = teamBox.getValue();

        boolean shortNameUnchanged = shortNameCustomField.getText().equals(
                currentPerson.getShortName());
        boolean firstNameUnchanged = firstNameCustomField.getText().equals(
                currentPerson.getFirstName());
        boolean lastNameUnchanged = lastNameCustomField.getText().equals(
                currentPerson.getLastName());
        boolean descriptionUnchanged = descriptionTextArea.getText().equals(
                currentPerson.getDescription());
        boolean birthdayUnchanged = birthDatePicker.getValue() == currentPerson.getBirthDate();
        boolean emailUnchanged = emailTextField.getText().equals(
                currentPerson.getEmail());
        boolean teamUnchanged = selectedTeam.getShortName().equals(
                currentPerson.getTeamName());
        boolean skillsUnchanged = true;

        for (Object skill : personSkillsList.getItems()) {
            System.out.println(skill);
            if (!currentPerson.getSkills().contains(skill)) {
                skillsUnchanged = false;
                break;
            }
        }

        for (Object skill : currentPerson.getSkills()) {
            System.out.println(skill);
            if (!personSkillsList.getItems().contains(skill)) {
                skillsUnchanged = false;
                break;
            }
        }
        boolean tagsUnchanged = tagField.getTags().equals(currentPerson.getTags());

        if (shortNameUnchanged && firstNameUnchanged && lastNameUnchanged
                && descriptionUnchanged && birthdayUnchanged && emailUnchanged
                && teamUnchanged && skillsUnchanged && tagsUnchanged) {
            // No fields have been changed
            currentPerson.switchToInfoScene();
            return;
        }

        boolean correctShortName = validateShortName(shortNameCustomField,
                currentPerson.getShortName());
        boolean firstNameValidated = NameValidator.validateName(firstNameCustomField);
        boolean lastNameValidated = NameValidator.validateName(lastNameCustomField);

        ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());

        // The short name is the same or valid
        if (correctShortName && firstNameValidated && lastNameValidated) {
            LocalDate birthDate = birthDatePicker.getValue();


            currentPerson.edit(shortNameCustomField.getText(),
                    firstNameCustomField.getText(),
                    lastNameCustomField.getText(),
                    emailTextField.getText(),
                    birthDate,
                    descriptionTextArea.getText(),
                    selectedTeam,
                    personSkillsList.getItems(),
                    tags
            );

            Collections.sort(Global.currentWorkspace.getPeople());
            currentPerson.switchToInfoScene();
            App.mainPane.refreshTree();
        }
    }
}
