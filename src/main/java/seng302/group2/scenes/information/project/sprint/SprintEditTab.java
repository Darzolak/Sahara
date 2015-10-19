package seng302.group2.scenes.information.project.sprint;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.allocation.Allocation;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static seng302.group2.util.validation.NameValidator.validateName;
import static seng302.group2.util.validation.ShortNameValidator.validateShortName;

/**
 * A class for displaying a tab used to edit people.
 * Created by btm38 on 30/07/15.
 */
public class SprintEditTab extends SearchableTab {

    static Boolean correctGoal = Boolean.FALSE;
    static Boolean correctLongName = Boolean.FALSE;
    Set<SearchableControl> searchControls = new HashSet<>();
    Sprint currentSprint;
    Button btnDone;

    RequiredField goalCustomField;
    RequiredField longNameCustomField;
    CustomComboBox<Team> teamComboBox;
    CustomComboBox<Release> releaseComboBox;
    String currentGoal;

    CustomDatePicker sprintStartDatePicker;
    CustomDatePicker sprintEndDatePicker;
    CustomTextArea descriptionTextArea = new CustomTextArea("Sprint Description:", 300, searchControls);
    ObservableList<Story> storiesInSprint = FXCollections.observableArrayList();
    TagField tagField;

    /**
     * Constructor for the SprintEditTab class. This constructor creates a JavaFX ScrollPane
     * which is populated with relevant controls then shown.
     *
     * @param currentSprint the sprint being edited
     */
    public SprintEditTab(Sprint currentSprint) {
        this.currentSprint = currentSprint;
        construct();

    }

    private String getTooltipStr(long dur) {
        String returnStr;
        if (Math.floorDiv(dur, 7) == 0) {
            if (dur == 0) {
                returnStr = "Sprint duration: less than a day.";
            }
            else if (dur % 7 == 1) {
                returnStr = "Sprint duration: 1 day.";
            }
            else {
                returnStr = "Sprint duration: " + (dur % 7) + " days.";
            }
        }
        else if (dur % 7 == 0) {
            if (Math.floorDiv(dur, 7 ) == 1 && (dur % 7) == 0) {
                returnStr = "Sprint duration: 1 week.";
            }
            else {
                returnStr = "Sprint duration: " + Math.floorDiv(dur, 7) + " weeks.";
            }
        }
        else {
            if (Math.floorDiv(dur, 7) == 1 && (dur % 7) == 1) {
                returnStr = "Sprint duration: 1 week and 1 day.";
            }
            else if (Math.floorDiv(dur, 7) == 1) {
                returnStr = "Sprint duration: 1 week and " + (dur % 7) + " days.";
            }
            else if (dur % 7 == 1) {
                returnStr = "Sprint duration: " + Math.floorDiv(dur, 7) + " weeks and 1 day.";
            }
            else {
                returnStr = "Sprint duration: " + Math.floorDiv(dur, 7) + " weeks and " + (dur % 7) + " days.";
            }
        }
        return returnStr;
    }

    private Boolean teamSelected() {
        return !(teamComboBox.getValue() == null);
    }

    private Boolean releaseSelected() {
        return !(releaseComboBox.getValue() == null);
    }

    private Boolean startDateSelected() {
        return !(sprintStartDatePicker.getValue() == null)
                && (releaseComboBox.getValue().getEstimatedDate() == null
                || sprintStartDatePicker.getValue().isBefore(releaseComboBox.getValue().
                getEstimatedDate().plusDays(1)));
    }

    private Boolean endDateSelected() {
        return !(sprintEndDatePicker.getValue() == null)
                && (sprintEndDatePicker.getValue().isAfter(sprintStartDatePicker.getValue().minusDays(1)))
                && (releaseComboBox.getValue().getEstimatedDate() == null
                || sprintEndDatePicker.getValue().isBefore(releaseComboBox.getValue().getEstimatedDate().plusDays(1)));
    }

    private void toggleDone() {

        correctGoal = validateShortName(goalCustomField, currentGoal);
        correctLongName = validateName(longNameCustomField);

        btnDone.setDisable(!(correctGoal && correctLongName && teamSelected() && releaseSelected()
                && startDateSelected() && endDateSelected()));
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
        this.currentGoal = currentSprint.getGoal();
        this.setText("Edit Sprint");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);

        goalCustomField = new RequiredField("Goal:", searchControls);
        longNameCustomField = new RequiredField("Long Name:", searchControls);

        teamComboBox = new CustomComboBox<>("Team:", true, searchControls);
        releaseComboBox = new CustomComboBox<>("Release:", true, searchControls);

        sprintStartDatePicker = new CustomDatePicker("Start Date:", true, searchControls);
        sprintEndDatePicker  = new CustomDatePicker("End Date:", true, searchControls);

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentSprint.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);

        goalCustomField.setMaxWidth(275);
        longNameCustomField.setMaxWidth(275);
        descriptionTextArea.setMaxWidth(275);
        teamComboBox.setMaxWidth(275);
        releaseComboBox.setMaxWidth(275);
        sprintStartDatePicker.setMaxWidth(275);
        sprintEndDatePicker.setMaxWidth(275);

        goalCustomField.setText(currentSprint.getGoal());
        longNameCustomField.setText(currentSprint.getLongName());
        descriptionTextArea.setText(currentSprint.getDescription());

        for (Team team : currentSprint.getProject().getCurrentTeams()) {
            teamComboBox.addToComboBox(team);
        }
        teamComboBox.setValue(currentSprint.getTeam());

        for (Release release : currentSprint.getProject().getReleases()) {
            releaseComboBox.addToComboBox(release);
        }
        releaseComboBox.setValue(currentSprint.getRelease());

        sprintStartDatePicker.setValue(currentSprint.getStartDate());
        sprintEndDatePicker.setValue(currentSprint.getEndDate());



        // Story assignment buttons
        Button btnAssign = new Button("<");
        Button btnUnassign = new Button(">");
        VBox assignmentButtons = new VBox();
        assignmentButtons.spacingProperty().setValue(10);
        assignmentButtons.getChildren().addAll(btnAssign, btnUnassign);
        assignmentButtons.setAlignment(Pos.CENTER);

        // Story list view setup
        ObservableList<Story> availableStories = FXCollections.observableArrayList();
        FilteredListView<Story> storiesInSprintFilteredList = new FilteredListView<>(storiesInSprint,
                "stories in the sprint");
        SearchableListView<Story> storiesInSprintView = storiesInSprintFilteredList.getListView();
        FilteredListView<Story> availableStoriesFilteredList = new FilteredListView<>(availableStories,
                "available stories");
        SearchableListView<Story> availableStoriesView = availableStoriesFilteredList.getListView();


        storiesInSprintView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableStoriesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        VBox inSprintVBox = new VBox();
        VBox availableVBox = new VBox();
        SearchableText sprintStoriesLabel = new SearchableText("Sprint Stories: ", searchControls);
        sprintStoriesLabel.setStyle("-fx-font-weight: bold");
        SearchableText availableStoriesLabel = new SearchableText("Available Stories: ", searchControls);
        availableStoriesLabel.setStyle("-fx-font-weight: bold");
        inSprintVBox.getChildren().addAll(sprintStoriesLabel, storiesInSprintFilteredList);
        availableVBox.getChildren().addAll(availableStoriesLabel, availableStoriesFilteredList);
        HBox storyHBox = new HBox(10);
        storyHBox.setPrefHeight(192);
        storyHBox.getChildren().addAll(inSprintVBox, assignmentButtons, availableVBox);


        storiesInSprint.addAll(currentSprint.getStories());
        //availableStories.addAll(Global.currentWorkspace.getAllStories());
        for (Story story : Global.currentWorkspace.getAllStories()) {
            if (story.getProject() == currentSprint.getProject()) {
                if (story.getSprint() == null) {
                    availableStories.add(story);
                }
            }
        }
        availableStories.removeAll(currentSprint.getStories());



        editPane.getChildren().addAll(goalCustomField, tagBox, longNameCustomField, descriptionTextArea,
                releaseComboBox, sprintStartDatePicker, sprintEndDatePicker, teamComboBox, storyHBox);

        Collections.addAll(searchControls, storiesInSprintView, availableStoriesView);






        // Actions and events

        final Callback<DatePicker, DateCell> startDateCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (releaseComboBox.getValue().getEstimatedDate() != null
                                        && item.isAfter(releaseComboBox.getValue().getEstimatedDate())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };

        sprintStartDatePicker.getDatePicker().setDayCellFactory(startDateCellFactory);

        final Callback<DatePicker, DateCell> endDateCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (item.isBefore(sprintStartDatePicker.getValue())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                                if (releaseComboBox.getValue().getEstimatedDate() != null
                                        && item.isAfter(releaseComboBox.getValue().getEstimatedDate())) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                                long p = ChronoUnit.DAYS.between(sprintStartDatePicker.getValue(), item);
                                setTooltip(new Tooltip(getTooltipStr(p)));
                            }
                        };
                    }
                };
        sprintEndDatePicker.getDatePicker().setDayCellFactory(endDateCellFactory);

        goalCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctGoal = validateShortName(goalCustomField, null);
            toggleDone();
        });

        longNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctLongName = validateName(longNameCustomField);
            toggleDone();
        });

        teamComboBox.getComboBox().valueProperty().addListener((observable, oldValue, newValue) -> {
            toggleDone();
        });

        releaseComboBox.getComboBox().valueProperty().addListener(new ChangeListener<Release>() {
            @Override
            public void changed(ObservableValue<? extends Release> observable,
                                Release oldValue, Release newValue) {

                sprintEndDatePicker.setDisable(true);
                sprintStartDatePicker.setDisable(true);
                teamComboBox.setDisable(true);

                if (newValue != null) {
                    if (newValue.getEstimatedDate() != null
                            && newValue.getEstimatedDate().isBefore(sprintStartDatePicker.getValue())) {
                        ValidationStyle.borderGlowRed(sprintStartDatePicker.getDatePicker());
                        ValidationStyle.showMessage("The start date of the Sprint must be before the estimated"
                                        + " release date of the Release: " + newValue.getEstimatedDate().toString(),
                                sprintStartDatePicker.getDatePicker());
                        sprintStartDatePicker.setTooltip(new Tooltip("The start date of"
                                + " the Sprint must be before the estimated release date of the Release: "
                                + newValue.getEstimatedDate().toString()));
                        sprintStartDatePicker.setDisable(false);
                        teamComboBox.setDisable(true);
                    }
                    else {
                        ValidationStyle.borderGlowNone(sprintStartDatePicker.getDatePicker());
                        sprintStartDatePicker.removeTooltip();
                        sprintStartDatePicker.setDisable(false);
                        teamComboBox.setDisable(false);
                    }
                    if (newValue.getEstimatedDate() != null
                            && newValue.getEstimatedDate().isBefore(sprintEndDatePicker.getValue())) {
                        ValidationStyle.borderGlowRed(sprintEndDatePicker.getDatePicker());
                        ValidationStyle.showMessage("The end date of the Sprint must be before the estimated"
                                        + " release date of the Release: " + newValue.getEstimatedDate().toString(),
                                sprintEndDatePicker.getDatePicker());
                        sprintEndDatePicker.setTooltip(new Tooltip("The end date of"
                                + " the Sprint must be before the estimated release date of the Release: "
                                + newValue.getEstimatedDate().toString()));
                        sprintEndDatePicker.setDisable(false);
                        teamComboBox.setDisable(true);
                    }
                    else {
                        ValidationStyle.borderGlowNone(sprintEndDatePicker.getDatePicker());
                        sprintEndDatePicker.removeTooltip();
                        sprintEndDatePicker.setDisable(false);
                        teamComboBox.setDisable(false);
                    }
                    toggleDone();
                }

            }
        });

        sprintStartDatePicker.getDatePicker().valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable,
                                LocalDate oldValue, LocalDate newValue) {


                Team prevTeam = teamComboBox.getValue();

                ValidationStyle.borderGlowNone(sprintStartDatePicker.getDatePicker());
                sprintStartDatePicker.removeTooltip();


                if (newValue != null && releaseComboBox.getValue().getEstimatedDate() != null
                        && releaseComboBox.getValue().getEstimatedDate().isBefore(newValue)) {
                    ValidationStyle.borderGlowRed(sprintStartDatePicker.getDatePicker());
                    ValidationStyle.showMessage("The start date of the Sprint must be before the estimated"
                                    + " release date of the Release: "
                                    + releaseComboBox.getValue().getEstimatedDate().toString(),
                            sprintStartDatePicker.getDatePicker());
                    sprintStartDatePicker.setTooltip(new Tooltip("The start date of"
                            + " the Sprint must be before the estimated release date of the Release: "
                            + releaseComboBox.getValue().getEstimatedDate().toString()));
                    sprintStartDatePicker.setDisable(false);
                    teamComboBox.setDisable(true);
                }
                if ((sprintEndDatePicker.getValue() != null) && (newValue != null)
                        && newValue.isAfter(sprintEndDatePicker.getValue())) {
                    sprintEndDatePicker.setDisable(false);
                    ValidationStyle.borderGlowRed(sprintEndDatePicker.getDatePicker());
                    ValidationStyle.showMessage("The end date of the Sprint must be after the start date",
                            sprintEndDatePicker.getDatePicker());
                    sprintEndDatePicker.setTooltip(new Tooltip("The end date of the Sprint must"
                            + " be after the start date"));
                    //sprintEndDatePicker.setValue(null);
                    teamComboBox.setValue(null);
                    teamComboBox.setDisable(true);
                }
                else if (newValue != null) {

                    if (releaseComboBox.getValue().getEstimatedDate() != null
                            && releaseComboBox.getValue().getEstimatedDate().isBefore(sprintEndDatePicker.getValue())) {
                        ValidationStyle.borderGlowRed(sprintEndDatePicker.getDatePicker());
                        ValidationStyle.showMessage("The end date of the Sprint must be before the estimated"
                                        + " release date of the Release: "
                                        + releaseComboBox.getValue().getEstimatedDate().toString(),
                                sprintEndDatePicker.getDatePicker());
                        sprintEndDatePicker.setTooltip(new Tooltip("The end date of"
                                + " the Sprint must be before the estimated release date of the Release: "
                                + releaseComboBox.getValue().getEstimatedDate().toString()));
                        sprintEndDatePicker.setDisable(false);
                        teamComboBox.setDisable(true);
                    }
                    else {
                        ValidationStyle.borderGlowNone(sprintEndDatePicker.getDatePicker());
                        sprintEndDatePicker.removeTooltip();
                        sprintEndDatePicker.setDisable(false);
                        teamComboBox.clear();
                        outer:
                        for (Team team : currentSprint.getProject().getAllTeams()) {
                            for (Allocation alloc : team.getProjectAllocations()) {

                                if (alloc.getStartDate().isBefore((sprintStartDatePicker.getValue().plusDays(1)))
                                        && alloc.getEndDate().isAfter(sprintStartDatePicker.getValue())) {
                                    teamComboBox.addToComboBox(team);
                                    continue outer;

                                }
                            }
                        }
                        teamComboBox.setDisable(false);
                        teamComboBox.setValue(null);
                    }
                }
                if (teamComboBox.getComboBox().getItems().contains(prevTeam)) {
                    ValidationStyle.borderGlowNone(teamComboBox.getComboBox());
                    teamComboBox.removeTooltip();
                    teamComboBox.setValue(prevTeam);
                }
                else if (teamComboBox.getComboBox().getItems().size() == 0) {
                    ValidationStyle.borderGlowRed(teamComboBox.getComboBox());
                    ValidationStyle.showMessage("There are no Teams allocated to this project at the start date"
                            + " specified", teamComboBox.getComboBox());
                    teamComboBox.setTooltip(new Tooltip("There are no Teams allocated to this project at the"
                            + " start date specified"));
                }
                else {
                    ValidationStyle.borderGlowNone(teamComboBox.getComboBox());
                    teamComboBox.removeTooltip();
                }
                toggleDone();

            }
        });


        sprintEndDatePicker.getDatePicker().valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable,
                                LocalDate oldValue, LocalDate newValue) {


                Team prevTeam = teamComboBox.getValue();

                ValidationStyle.borderGlowNone(sprintEndDatePicker.getDatePicker());
                sprintEndDatePicker.getDatePicker().setTooltip(null);
                if (newValue != null) {
                    sprintEndDatePicker.setStyle(null);
                    sprintEndDatePicker.getDatePicker().setTooltip(null);

                    if (releaseComboBox.getValue().getEstimatedDate() != null
                            && releaseComboBox.getValue().getEstimatedDate().isBefore(sprintEndDatePicker.getValue())) {
                        ValidationStyle.borderGlowRed(sprintEndDatePicker.getDatePicker());
                        ValidationStyle.showMessage("The end date of the Sprint must be before the estimated"
                                        + " release date of the Release: "
                                        + releaseComboBox.getValue().getEstimatedDate().toString(),
                                sprintEndDatePicker.getDatePicker());
                        sprintEndDatePicker.setTooltip(new Tooltip("The end date of"
                                + " the Sprint must be before the estimated release date of the Release: "
                                + releaseComboBox.getValue().getEstimatedDate().toString()));
                        sprintEndDatePicker.setDisable(false);
                        teamComboBox.setDisable(true);
                    }
                    else if (newValue.isBefore(sprintStartDatePicker.getValue())) {
                        ValidationStyle.borderGlowRed(sprintEndDatePicker.getDatePicker());
                        ValidationStyle.showMessage("The end date of the Sprint must be after the start date",
                                sprintEndDatePicker.getDatePicker());
                        sprintEndDatePicker.setTooltip(new Tooltip("The end date of the Sprint must be after"
                                + " the start date"));
                        sprintEndDatePicker.setDisable(false);
                        teamComboBox.setDisable(true);
                    }
                    else {
                        teamComboBox.clear();
                        outer:
                        for (Team team : currentSprint.getProject().getAllTeams()) {
                            for (Allocation alloc : team.getProjectAllocations()) {

                                if (alloc.getStartDate().isBefore((sprintStartDatePicker.getValue().plusDays(1)))
                                        && alloc.getEndDate().isAfter(sprintStartDatePicker.getValue())) {
                                    teamComboBox.addToComboBox(team);
                                    continue outer;

                                }
                            }
                        }
                        teamComboBox.setDisable(false);
                        teamComboBox.setValue(null);

                        if (teamComboBox.getComboBox().getItems().contains(prevTeam)) {
                            ValidationStyle.borderGlowNone(teamComboBox.getComboBox());
                            teamComboBox.removeTooltip();
                            teamComboBox.setValue(prevTeam);
                        }
                        else if (teamComboBox.getComboBox().getItems().size() == 0) {
                            ValidationStyle.borderGlowRed(teamComboBox.getComboBox());
                            ValidationStyle.showMessage("There are no Teams allocated to this project at the start"
                                    + " date specified", teamComboBox.getComboBox());
                            teamComboBox.setTooltip(new Tooltip("There are no Teams allocated to this project at the"
                                    + " start date specified"));
                        }
                        else {
                            ValidationStyle.borderGlowNone(teamComboBox.getComboBox());
                            teamComboBox.removeTooltip();
                        }
                    }
                    toggleDone();
                }
            }
        });

        // Button events
        btnAssign.setOnAction((event) -> {
            Collection<Story> selectedStories = new ArrayList<>();
            selectedStories.addAll(availableStoriesView.getSelectionModel().
                    getSelectedItems());

            storiesInSprint.addAll(
                    availableStoriesView.getSelectionModel().getSelectedItems());
            availableStories.removeAll(
                    availableStoriesView.getSelectionModel().getSelectedItems());
            storiesInSprintFilteredList.resetInputText();
            availableStoriesFilteredList.resetInputText();
        });

        btnUnassign.setOnAction((event) -> {
            Collection<Story> selectedStories = new ArrayList<>();
            selectedStories.addAll(storiesInSprintView.getSelectionModel().
                    getSelectedItems());
            availableStories.addAll(selectedStories);
            storiesInSprint.removeAll(selectedStories);
            storiesInSprintFilteredList.resetInputText();
            availableStoriesFilteredList.resetInputText();
        });

    }

    /**
     * Cancels the edit
     */
    public void cancel() {
        currentSprint.switchToInfoScene();
    }
    /**
     * Changes the values depending on what the user edits
     */
    public void done() {
        boolean goalUnchanged = goalCustomField.getText().equals(
                currentSprint.getGoal());
        boolean longNameUnchanged = longNameCustomField.getText().equals(
                currentSprint.getLongName());
        boolean descriptionUnchanged = descriptionTextArea.getText().equals(
                currentSprint.getDescription());
        boolean teamUnchanged = teamComboBox.getValue().equals(
                currentSprint.getTeam());
        boolean releaseUnchanged = releaseComboBox.getValue().equals(
                currentSprint.getRelease());
        boolean startDateUnchanged = sprintStartDatePicker.getValue().equals(
                currentSprint.getStartDate());
        boolean endDateUnchanged = sprintEndDatePicker.getValue().equals(
                currentSprint.getEndDate());
        boolean storiesUnchanged = storiesInSprint.equals(currentSprint.getStories());
        boolean tagsUnchanged = tagField.getTags().equals(currentSprint.getTags());
        if (goalUnchanged && longNameUnchanged && descriptionUnchanged
                && teamUnchanged && releaseUnchanged && startDateUnchanged && endDateUnchanged
                && storiesUnchanged && tagsUnchanged) {
            // No fields have been changed
            currentSprint.switchToInfoScene();
            return;
        }

        boolean correctGoal = ShortNameValidator.validateShortName(goalCustomField,
                currentSprint.getGoal());
        // The short name is the same or valid
        if (correctGoal) {
            ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());

            currentSprint.edit(goalCustomField.getText(),
                    longNameCustomField.getText(),
                    descriptionTextArea.getText(),
                    sprintStartDatePicker.getValue(),
                    sprintEndDatePicker.getValue(),
                    teamComboBox.getValue(),
                    releaseComboBox.getValue(),
                    storiesInSprint, //This line just a placeholder for now
                    tags
            );

            currentSprint.switchToInfoScene();
            App.mainPane.refreshTree();
        }
    }
}
