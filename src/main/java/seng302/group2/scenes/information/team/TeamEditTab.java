package seng302.group2.scenes.information.team;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.*;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.dialog.CustomDialog;
import seng302.group2.scenes.information.person.PersonScene;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.observableList;

/**
 * A class for displaying a tab used to edit teams.
 * Created by btm38 on 30/07/15.
 */
public class TeamEditTab extends SearchableTab {

    Set<SearchableControl> searchControls = new HashSet<>();
    String poPlaceholder = "No Product Owner Assigned";
    String smPlaceholder = "No Scrum Master Assigned";
    private Team currentTeam;
    private RequiredField shortNameField;
    private CustomTextArea descriptionField;
    private Person allocatedProductOwner;
    private Person allocatedScrumMaster;
    private Set<Person> allocatedDevelopers = new HashSet<>();
    private ObservableList<Role> roleList = observableArrayList();
    private CustomComboBox<Role> roleComboBox;
    private Role noneRole = new Role("", Role.RoleType.NONE);
    ObservableList<Person> teamMembersList = observableArrayList();
    TagField tagField;


    /**
     * Constructor for the Team Edit Tab class. This constructor creates a JavaFX ScrollPane
     * which is populated with relevant controls and then shown.
     *
     * @param currentTeam The team being edited
     */
    public TeamEditTab(Team currentTeam) {
        // Initialise Variables
        this.currentTeam = currentTeam;
        construct();
    }


    /**
     * Updates the roles in the role allocation combo box with those that the given person can fill
     *
     * @param person The person whose roles to check
     */
    private void updateRoles(Person person) {
        roleList.clear();
        roleList.add(noneRole);
        for (Role role : Global.currentWorkspace.getRoles()) {
            if (person != null && person.getSkills().containsAll(role.getRequiredSkills())) {
                roleList.add(role);
            }
        }
    }


    /**
     * Checks if the changes in the scene are valid
     *
     * @return If the changes in the scene are valid
     */
    private boolean isValidState() {
        return (shortNameField.getText().equals(currentTeam.getShortName())  // Is the same,
                || ShortNameValidator.validateShortName(shortNameField, null))// new name validates
                && areRolesValid();
    }


    /**
     * Checks if the current role changes are valid, and displays a dialog with the first found
     * violation
     *
     * @return If the role changes are valid
     */
    private boolean areRolesValid() {
        // Find and store errors in validation
        String errorMessage = null;
        if (allocatedScrumMaster == allocatedProductOwner && allocatedScrumMaster != null) {
            errorMessage = "Cannot have the same person assigned to Product Owner and Scrum Master.";
        }
        else if (allocatedDevelopers.contains(allocatedScrumMaster) && allocatedScrumMaster != null) {
            errorMessage = "The Scrum Master cannot also be assigned as a Developer.";
        }
        else if (allocatedDevelopers.contains(allocatedProductOwner)
                && allocatedProductOwner != null) {
            errorMessage = "The Product Owner cannot also be assigned as a Developer.";
        }

        // Display the first found error, or pass if valid
        if (errorMessage == null) {
            return true;
        }
        else {
            CustomDialog.showDialog("Invalid Role Assignment", errorMessage,
                    Alert.AlertType.WARNING);
            return false;
        }
    }

    /**
     * Gets all the searchable controls on this tab.
     *
     * @return a collection of all the searchable controls on this tab.
     */
    @Override
    public Collection<SearchableControl> getSearchableControls() {
        return searchControls;
    }

    @Override
    public void construct() {
        allocatedDevelopers.addAll(currentTeam.getDevs());
        CustomInfoLabel poText;
        CustomInfoLabel smText;

        if (currentTeam.getProductOwner() != null) {
            allocatedProductOwner = currentTeam.getProductOwner();
            poText = new CustomInfoLabel("Product Owner: ", allocatedProductOwner.toString());
        }
        else {
            poText = new CustomInfoLabel("Product Owner: ", poPlaceholder.toString());
        }

        if (currentTeam.getScrumMaster() != null) {
            allocatedScrumMaster = currentTeam.getScrumMaster();
            smText = new CustomInfoLabel("Scrum Master: ", allocatedScrumMaster.toString());
        }
        else {
            smText = new CustomInfoLabel("Scrum Master: ", smPlaceholder.toString());
        }


        // Tab settings
        this.setText("Edit Team");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentTeam.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);


        // Basic information fields
        shortNameField = new RequiredField("Short Name:");
        shortNameField.setText(currentTeam.getShortName());
        shortNameField.setMaxWidth(275);
        descriptionField = new CustomTextArea("Team Description:", 300);
        descriptionField.setText(currentTeam.getDescription());
        descriptionField.setMaxWidth(275);

        // Team assignment buttons
        Button btnAssign = new Button("<");
        Button btnUnassign = new Button(">");
        VBox assignmentButtons = new VBox();
        assignmentButtons.spacingProperty().setValue(10);
        assignmentButtons.getChildren().addAll(btnAssign, btnUnassign);
        assignmentButtons.setAlignment(Pos.CENTER);

        // Role assignment
        Button btnRoleAssign = new Button("Assign");
        btnRoleAssign.setDisable(true);
        roleList = observableArrayList();
        roleComboBox = new CustomComboBox<>("Role:");
        roleComboBox.getComboBox().setItems(roleList);

        HBox roleAssignmentBox = new HBox(10);

        roleAssignmentBox.getChildren().addAll(roleComboBox, btnRoleAssign);
        for (Role role : Global.currentWorkspace.getRoles()) {
            roleComboBox.addToComboBox(role);
        }


        // Draft member and available people lists
        teamMembersList.addAll(currentTeam.getPeople());

        ObservableList<Person> availablePeopleList = observableArrayList();
        for (Person person : Global.currentWorkspace.getPeople()) {
            if (person.getTeam().isUnassignedTeam()) {
                availablePeopleList.add(person);
            }
        }
        availablePeopleList.removeAll(teamMembersList);


        // List views
        FilteredListView<Person> teamMembersListBox = new FilteredListView<>(teamMembersList, "team members");
        SearchableListView<Person> teamMembersListView = teamMembersListBox.getListView();
        teamMembersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        teamMembersListView.getSelectionModel().select(0);

        FilteredListView<Person> availablePeopleListBox = new FilteredListView<>(availablePeopleList,
                "available people");
        SearchableListView<Person> availablePeopleListView = availablePeopleListBox.getListView();
        availablePeopleListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availablePeopleListView.getSelectionModel().select(0);

        VBox teamMembersBox = new VBox(10);
        SearchableText teamMemberLabel = new SearchableText("Team Members: ");
        teamMemberLabel.setStyle("-fx-font-weight: bold");
        teamMembersBox.getChildren().addAll(teamMemberLabel, teamMembersListBox);


        VBox availablePeopleBox = new VBox(10);
        SearchableText availablePeopleLabel = new SearchableText("Available People: ");
        availablePeopleLabel.setStyle("-fx-font-weight: bold");
        availablePeopleBox.getChildren().addAll(availablePeopleLabel, availablePeopleListBox);

        HBox memberListViews = new HBox(10);
        memberListViews.getChildren().addAll(teamMembersBox, assignmentButtons, availablePeopleBox);
        memberListViews.setPrefHeight(192);

        ObservableList<Person> devList = observableArrayList();

        for (Person person : allocatedDevelopers) {
            devList.add(person);
        }

        FilteredListView<Person> devListBox = new FilteredListView<>(devList, "team members");
        SearchableListView<Person> devListView = devListBox.getListView();
        devListView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
            }
        });

        VBox devBox = new VBox(10);
        SearchableText devLabel = new SearchableText("Developers: ");
        devLabel.setStyle("-fx-font-weight: bold");
        devBox.getChildren().addAll(devLabel, devListBox);
        devBox.setPrefHeight(192);

        // Load the initial role assignment options
        if (teamMembersListView.getSelectionModel().getSelectedItems().size() == 1) {
            roleComboBox.setDisable(false);
            btnRoleAssign.setDisable(false);
            updateRoles(teamMembersListView.getSelectionModel().getSelectedItems().get(0));
        }
        else {
            roleComboBox.setDisable(true);
            btnRoleAssign.setDisable(true);
        }

        // Listeners
        // Update the roles combo when the selected person changes
        teamMembersListView.getSelectionModel().selectedItemProperty().addListener((event) -> {
            roleList.clear();
            if (teamMembersListView.getSelectionModel().getSelectedItems().size() == 1) {
                roleComboBox.setDisable(false);
                btnRoleAssign.setDisable(false);
                updateRoles(teamMembersListView.getSelectionModel().getSelectedItems().get(0));
            }
            else {
                // No person, or more than one person, selected
                roleComboBox.setDisable(true);
                btnRoleAssign.setDisable(true);
            }
        });


        // Events
        btnAssign.setOnAction((event) -> {
            Collection<Person> selectedPeople = new ArrayList<>();
            selectedPeople.addAll(availablePeopleListView.getSelectionModel().
                    getSelectedItems());
            for (Person person : selectedPeople) {
                if (person.getRole() == Role.getRoleFromType(Role.RoleType.PRODUCT_OWNER)) {
                    person.setRole(Role.getRoleFromType(Role.RoleType.NONE));
                }
                if (person.getRole() == Role.getRoleFromType(Role.RoleType.SCRUM_MASTER)) {
                    person.setRole(Role.getRoleFromType(Role.RoleType.NONE));
                }
            }

            teamMembersList.addAll(
                    availablePeopleListView.getSelectionModel().getSelectedItems());
            availablePeopleList.removeAll(
                    availablePeopleListView.getSelectionModel().getSelectedItems());
            availablePeopleListBox.resetInputText();
            teamMembersListBox.resetInputText();
        });

        btnUnassign.setOnAction((event) -> {
            Collection<Person> selectedPeople = new ArrayList<>();
            selectedPeople.addAll(teamMembersListView.getSelectionModel().
                    getSelectedItems());

            for (Person person : selectedPeople) {
                if (allocatedProductOwner == person) {
                    String errorMessage = "Cannot move " + person.toString() + " out of the team, as they are currently"
                            + " the teams Product Owner.";
                    CustomDialog.showDialog("Cannot Remove Team Member", errorMessage,
                            Alert.AlertType.WARNING);
                    return;
                }
                if (allocatedScrumMaster == person) {
                    String errorMessage = "Cannot move " + person.toString() + " out of the team, as they are currently"
                            + " the teams Scrum Master.";
                    CustomDialog.showDialog("Cannot Remove Team Member", errorMessage,
                            Alert.AlertType.WARNING);
                    return;
                }
                if (allocatedDevelopers.contains(person)) {
                    allocatedDevelopers.remove(person);
                }
                devList.clear();
                for (Person pers : allocatedDevelopers) {
                    devList.add(pers);
                }
            }

            availablePeopleList.addAll(selectedPeople);
            teamMembersList.removeAll(selectedPeople);

            teamMembersListBox.resetInputText();
            availablePeopleListBox.resetInputText();
        });

        btnRoleAssign.setOnAction((event) -> {
            Person selectedPerson =
                    teamMembersListView.getSelectionModel().getSelectedItems().get(0);
            Role selectedRole = roleComboBox.getComboBox().getSelectionModel().getSelectedItem();
            if (selectedRole == null) {
                return;
            }
            switch (selectedRole.getType()) {
                case PRODUCT_OWNER:
                    if (selectedPerson == allocatedScrumMaster) {
                        String errorMessage = "Cannot have the same person assigned to Product Owner and Scrum Master.";
                        CustomDialog.showDialog("Invalid Role Assignment", errorMessage,
                                Alert.AlertType.WARNING);
                    }
                    else {
                        if (allocatedProductOwner != null) {
                            allocatedProductOwner.setRole(Role.getRoleFromType(Role.RoleType.NONE));
                        }
                        allocatedProductOwner = selectedPerson;
                        allocatedDevelopers.remove(selectedPerson);
                        devList.clear();
                        for (Person pers : allocatedDevelopers) {
                            devList.add(pers);
                        }
                        poText.setValue(allocatedProductOwner.toString());
                        selectedPerson.setRole(Role.getRoleFromType(Role.RoleType.PRODUCT_OWNER));
                    }
                    break;
                case SCRUM_MASTER:
                    if (selectedPerson == allocatedProductOwner) {
                        String errorMessage = "Cannot have the same person assigned to Product Owner and Scrum Master.";
                        CustomDialog.showDialog("Invalid Role Assignment", errorMessage,
                                Alert.AlertType.WARNING);
                    }
                    else {
                        if (allocatedScrumMaster != null) {
                            allocatedScrumMaster.setRole(Role.getRoleFromType(Role.RoleType.NONE));
                        }
                        allocatedScrumMaster = selectedPerson;
                        allocatedDevelopers.remove(selectedPerson);
                        devList.clear();
                        for (Person pers : allocatedDevelopers) {
                            devList.add(pers);
                        }
                        smText.setValue(allocatedScrumMaster.toString());
                        selectedPerson.setRole(Role.getRoleFromType(Role.RoleType.SCRUM_MASTER));
                    }
                    break;
                case DEVELOPMENT_TEAM_MEMBER:
                    if (selectedPerson == allocatedProductOwner) {
                        String errorMessage = "The Product Owner cannot also be assigned as a Developer.";
                        CustomDialog.showDialog("Invalid Role Assignment", errorMessage,
                                Alert.AlertType.WARNING);
                    }
                    else if (selectedPerson == allocatedScrumMaster) {
                        String errorMessage = "The Scrum Master cannot also be assigned as a Developer.";
                        CustomDialog.showDialog("Invalid Role Assignment", errorMessage,
                                Alert.AlertType.WARNING);
                    }
                    else {
                        allocatedDevelopers.add(selectedPerson);
                        selectedPerson.setRole(Role.getRoleFromType(Role.RoleType.DEVELOPMENT_TEAM_MEMBER));
                    }
                    break;
                case NONE:
                    if (allocatedProductOwner == selectedPerson) {
                        String errorMessage = "Someone else must be moved into the Product Owner role before"
                                + " this person can be removed from a role.";
                        CustomDialog.showDialog("Invalid Role Assignment", errorMessage,
                                Alert.AlertType.WARNING);
                        return;
                    }
                    if (allocatedScrumMaster == selectedPerson) {
                        String errorMessage = "Someone else must be moved into the Scrum Master role before"
                                + " this person can be removed from a role.";
                        CustomDialog.showDialog("Invalid Role Assignment", errorMessage,
                                Alert.AlertType.WARNING);
                        return;
                    }
                    allocatedDevelopers.remove(selectedPerson);
                    selectedPerson.setRole(Role.getRoleFromType(Role.RoleType.NONE));
                    break;
                default:
                    break;
            }
            devList.clear();
            for (Person person : allocatedDevelopers) {
                devList.add(person);
            }

        });

        // Add items to pane & search collection
        editPane.getChildren().addAll(
                shortNameField,
                tagBox,
                descriptionField,
                memberListViews,
                roleAssignmentBox,
                poText,
                smText,
                devBox
        );

        Collections.addAll(searchControls,
                shortNameField,
                descriptionField,
                roleComboBox,
                poText,
                smText,
                teamMembersListView,
                availablePeopleListView,
                devListView,
                devLabel,
                teamMemberLabel,
                availablePeopleLabel
        );
    }

    /**
     * Cancels the edit
     */
    public void cancel() {
        currentTeam.switchToInfoScene();
    }

    /**
     * Changes the values depending on what the user edits
     */
    public void done() {


        boolean shortNameUnchanged = shortNameField.getText().equals(currentTeam.getShortName());
        boolean tagUnchanged = tagField.getTags().equals(currentTeam.getTags());
        boolean descriptionUnchanged = descriptionField.getText().equals(currentTeam.getDescription());
        boolean membersUnchanged = teamMembersList.equals(currentTeam.getPeople());
        boolean developersUnchanged = allocatedDevelopers.equals(currentTeam.getDevs());
        boolean smUnchanged = allocatedScrumMaster == currentTeam.getScrumMaster();
        boolean poUnchanged = allocatedProductOwner == currentTeam.getProductOwner();

        if (shortNameUnchanged && tagUnchanged && descriptionUnchanged && membersUnchanged && developersUnchanged
                && smUnchanged && poUnchanged) {
            currentTeam.switchToInfoScene();
            return;
        }

        if (isValidState()) { // validation
            ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());

            currentTeam.edit(shortNameField.getText(),
                    descriptionField.getText(),
                    teamMembersList,
                    allocatedProductOwner,
                    allocatedScrumMaster,
                    allocatedDevelopers,
                    tags
            );

            Collections.sort(Global.currentWorkspace.getTeams());
            currentTeam.switchToInfoScene();
            App.mainPane.refreshTree();
        }
    }
}