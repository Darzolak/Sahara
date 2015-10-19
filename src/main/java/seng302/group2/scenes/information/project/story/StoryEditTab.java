package seng302.group2.scenes.information.project.story;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.*;
import seng302.group2.scenes.control.search.*;
import seng302.group2.util.validation.PriorityFieldValidator;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.estimation.EstimationScalesDictionary;
import seng302.group2.workspace.tag.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A class for displaying a tab used to edit stories.
 * Created by btm38 on 30/07/15.
 */
public class StoryEditTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    Story currentStory;

    RequiredField shortNameCustomField = new RequiredField("Short Name:");
    CustomTextField longNameTextField = new CustomTextField("Long Name:");
    CustomTextArea descriptionTextArea = new CustomTextArea("Story Description:", 300);
    RequiredField priorityNumberField = new RequiredField("Story Priority:");
    CustomComboBox estimateComboBox = new CustomComboBox("Estimate:", false);

    SearchableCheckBox readyStateCheck = new SearchableCheckBox("Ready?");

    ObservableList<Story> dependentOnList = FXCollections.observableArrayList();
    TagField tagField;

    /**
     * Constructor for the StoryEditTab class. This constructor creates a JavaFX ScrollPane
     * which is populated with relevant controls then shown.
     *
     * @param currentStory The story being edited
     */
    public StoryEditTab(Story currentStory) {
        this.currentStory = currentStory;
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
        this.setText("Edit Story");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);



        Button btnAssign = new Button("<");
        Button btnUnassign = new Button(">");
        VBox assignmentButtons = new VBox();
        assignmentButtons.spacingProperty().setValue(10);
        assignmentButtons.getChildren().addAll(btnAssign, btnUnassign);
        assignmentButtons.setAlignment(Pos.CENTER);

        HBox estimateHBox = new HBox();

        boolean unassigned = false;
        if (currentStory.getBacklog() == null) {
            unassigned = true;
        }
        if (unassigned) {
            estimateComboBox.disable(true);
            Tooltip estimateTT = new Tooltip("Stories cannot be estimated without belonging to a backlog");
            Tooltip.install(estimateHBox, estimateTT);
        }
        else {
            String key = currentStory.getBacklog().getScale();
            ArrayList<String> valueList = Global.currentWorkspace.getEstimationScales()
                    .getEstimationScaleDict().get(key);
            for (String value : valueList) {
                estimateComboBox.addToComboBox(value);
            }
        }

        estimateComboBox.setValue(currentStory.getEstimate());

        readyStateCheck.getCheckBox().setSelected(currentStory.getReady());
        if (currentStory.getBacklog() == null
                || currentStory.getEstimate().equals(EstimationScalesDictionary.getScaleValue(
                EstimationScalesDictionary.DefaultValues.NONE))
                || currentStory.getAcceptanceCriteria().isEmpty()) {
            readyStateCheck.getCheckBox().setSelected(false);
            readyStateCheck.getCheckBox().setDisable(true);
        }
        else {
            readyStateCheck.getCheckBox().setDisable(false);
            readyStateCheck.getCheckBox().setTooltip(
                    new Tooltip("Stories cannot be estimated without belonging to a backlog"));
        }

        estimateComboBox.getComboBox().valueProperty().addListener((observable, oldValue, newValue) -> {
            if (currentStory.getBacklog() == null
                    || (newValue != null && newValue.equals(EstimationScalesDictionary.getScaleValue(
                    EstimationScalesDictionary.DefaultValues.NONE)))
                    || currentStory.getAcceptanceCriteria().isEmpty()) {
                readyStateCheck.getCheckBox().setSelected(false);
                readyStateCheck.getCheckBox().setDisable(true);
            }
            else {
                readyStateCheck.getCheckBox().setDisable(false);
            }
        });


        HBox.setHgrow(estimateHBox, Priority.ALWAYS);
        estimateHBox.getChildren().add(estimateComboBox);

        if (currentStory.getAcceptanceCriteria().isEmpty() || currentStory.getBacklog() == null) {
            estimateComboBox.disable(true);
            estimateComboBox.setValue(EstimationScalesDictionary.getScaleValue(
                    EstimationScalesDictionary.DefaultValues.NONE));
            Tooltip tool = new seng302.group2.scenes.control.Tooltip(
                    "A Story must be assigned to a backlog and have at least one Acceptance "
                            + "criteria before an estimate can be given.");
            Tooltip.install(estimateHBox, tool);
            estimateComboBox.setTooltip(tool);
        }

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentStory.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);

        shortNameCustomField.setMaxWidth(275);
        longNameTextField.setMaxWidth(275);
        descriptionTextArea.setMaxWidth(275);
        priorityNumberField.setMaxWidth(275);
        readyStateCheck.setMaxWidth(165);

        shortNameCustomField.setText(currentStory.getShortName());
        longNameTextField.setText(currentStory.getLongName());
        descriptionTextArea.setText(currentStory.getDescription());
        priorityNumberField.setText(currentStory.getPriority().toString());

        dependentOnList.addAll(currentStory.getDependentOn());

        Backlog currentBacklog = currentStory.getBacklog();
        ObservableList<Story> availableStoryList = FXCollections.observableArrayList();
        if (!unassigned) {
            availableStoryList.addAll(currentBacklog.getProject().getAllStories());
        }

        availableStoryList.removeAll(dependentOnList);
        availableStoryList.remove(currentStory);

        FilteredListView<Story> dependantStoriesFilteredList = new FilteredListView<>(dependentOnList,
                "dependents");
        SearchableListView<Story> dependantStoriesListView = dependantStoriesFilteredList.getListView();
        dependantStoriesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        dependantStoriesListView.getSelectionModel().select(0);

        FilteredListView<Story> availableStoryFilteredList = new FilteredListView<>(availableStoryList,
                "dependents");
        SearchableListView<Story> availableStoryListView = availableStoryFilteredList.getListView();
        availableStoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableStoryListView.getSelectionModel().select(0);

        VBox backlogStoryBox = new VBox(10);
        VBox availableStoryBox = new VBox(10);
        HBox storyListViews = new HBox(10);

        SearchableText dependantStoryLabel = new SearchableText("Dependant On: ");
        dependantStoryLabel.setStyle("-fx-font-weight: bold");
        backlogStoryBox.getChildren().addAll(dependantStoryLabel, dependantStoriesFilteredList);

        SearchableText availableStoryLabel = new SearchableText("Available Stories: ");
        availableStoryLabel.setStyle("-fx-font-weight: bold");
        availableStoryBox.getChildren().addAll(availableStoryLabel, availableStoryFilteredList);


        storyListViews.getChildren().addAll(backlogStoryBox, assignmentButtons, availableStoryBox);
        storyListViews.setPrefHeight(192);

        if (unassigned) {
            for (Node child : storyListViews.getChildren()) {
                child.setDisable(true);
            }
            Tooltip estimateTT = new Tooltip("Stories cannot be estimated without belonging to a backlog");
            Tooltip.install(storyListViews, estimateTT);
        }

        btnAssign.setOnAction((event) -> {
            Story adding = availableStoryListView.getSelectionModel().getSelectedItem();
            if (adding != null) {
                if (currentStory.checkAddCycle(adding)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Conflicting Dependencies");
                    alert.setHeaderText("Unable to add '" + adding + "'");
                    alert.setContentText(
                            "Adding this story would create a cycle in " + currentStory + "'s dependencies");

                    alert.showAndWait();
                }
                else {
                    dependentOnList.addAll(
                            availableStoryListView.getSelectionModel().getSelectedItem());
                    availableStoryList.removeAll(
                            availableStoryListView.getSelectionModel().getSelectedItem());
                }
            }
            availableStoryFilteredList.resetInputText();
            dependantStoriesFilteredList.resetInputText();
        });

        btnUnassign.setOnAction((event) -> {
            availableStoryList.addAll(
                    dependantStoriesListView.getSelectionModel().getSelectedItem());
            dependentOnList.removeAll(
                    dependantStoriesListView.getSelectionModel().getSelectedItem());
            availableStoryFilteredList.resetInputText();
            dependantStoriesFilteredList.resetInputText();
        });

        // Add items to pane & search collection
        editPane.getChildren().addAll(
                shortNameCustomField,
                tagBox,
                longNameTextField,
                descriptionTextArea,
                priorityNumberField,
                estimateHBox,
                readyStateCheck,
                storyListViews);

        Collections.addAll(searchControls,
                shortNameCustomField,
                longNameTextField,
                descriptionTextArea,
                priorityNumberField,
                estimateComboBox,
                readyStateCheck,
                dependantStoryLabel,
                dependantStoriesListView,
                availableStoryLabel,
                availableStoryListView
        );
    }

    /**
     * Cancels the edit
     */
    public void cancel() {
        currentStory.switchToInfoScene();
    }

    /**
     * Changes the values depending on what the user edits
     */
    public void done() {
        boolean shortNameUnchanged = shortNameCustomField.getText().equals(
                currentStory.getShortName());
        boolean longNameUnchanged = longNameTextField.getText().equals(
                currentStory.getLongName());
        boolean descriptionUnchanged = descriptionTextArea.getText().equals(
                currentStory.getDescription());
        boolean priorityUnchanged = priorityNumberField.getText().equals(
                currentStory.getPriority().toString());
        boolean readyUnchanged = readyStateCheck.getCheckBox().isSelected() == currentStory.getReady();
        boolean estimateUnchanged = estimateComboBox.getValue().equals(currentStory.getEstimate());
        boolean tagsUnchanged = tagField.getTags().equals(currentStory.getTags());

        boolean dependentChanged = true;
        if (currentStory.getDependentOn().containsAll(dependentOnList)
                && dependentOnList.containsAll(currentStory.getDependentOn())) {
            dependentChanged = false;
        }


        if (shortNameUnchanged && longNameUnchanged && descriptionUnchanged
                && priorityUnchanged && readyUnchanged && estimateUnchanged && !dependentChanged && tagsUnchanged) {
            // No changes
            currentStory.switchToInfoScene();
            return;
        }

        boolean correctShortName = ShortNameValidator.validateShortName(shortNameCustomField,
                currentStory.getShortName());
        boolean correctPriority = PriorityFieldValidator.validatePriorityField(priorityNumberField,
                currentStory.getBacklog(), currentStory.getPriority());

        if (correctShortName && correctPriority) {
            // Valid short name, make the edit
            ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());

            currentStory.edit(shortNameCustomField.getText(),
                    longNameTextField.getText(),
                    descriptionTextArea.getText(),
                    currentStory.getProject(),
                    Integer.parseInt(priorityNumberField.getText()),
                    currentStory.getBacklog(),
                    estimateComboBox.getValue().toString(),
                    readyStateCheck.getCheckBox().selectedProperty().get(),
                    dependentOnList,
                    tags
            );

            currentStory.switchToInfoScene();
            App.mainPane.refreshTree();
        }
    }
}
