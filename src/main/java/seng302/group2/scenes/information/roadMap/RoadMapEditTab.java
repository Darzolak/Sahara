package seng302.group2.scenes.information.roadMap;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.CustomComboBox;
import seng302.group2.scenes.control.FilteredListView;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.control.search.*;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.roadMap.RoadMap;
import seng302.group2.workspace.tag.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;
import static seng302.group2.util.validation.PriorityFieldValidator.validatePriorityField;

/**
 * The RoadMap edit tab
 * Created by cvs20 on 11/09/15.
 */
public class RoadMapEditTab extends SearchableTab {

    private List<SearchableControl> searchControls = new ArrayList<>();
    private RequiredField shortNameField;
    private CustomComboBox<String> scaleComboBox;

    private RoadMap currentRoadMap;
    private RequiredField priorityNumberField = new RequiredField("Story Priority:");
    private ObservableList<Release> roadMapList = observableArrayList();
    private TagField tagField;

    /**
     * Constructor for the RoadMapEditTab class. This constructor creates a JavaFX ScrollPane
     * which is populated with relevant controls and then shown.
     *
     * @param currentRoadMap The skill being edited
     */
    public RoadMapEditTab(RoadMap currentRoadMap) {
        this.currentRoadMap = currentRoadMap;
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
        this.setText("Edit RoadMap");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);
        SearchableText errorField = new SearchableText("");
        shortNameField = new RequiredField("Short Name:");
        shortNameField.setText(currentRoadMap.getShortName());
        shortNameField.setMaxWidth(275);

        priorityNumberField.setText(currentRoadMap.getPriority().toString());
        priorityNumberField.setMaxWidth(275);


        Button btnAssign = new Button("<");
        Button btnUnassign = new Button(">");
        VBox assignmentButtons = new VBox();
        assignmentButtons.spacingProperty().setValue(10);
        assignmentButtons.getChildren().addAll(btnAssign, btnUnassign);
        assignmentButtons.setAlignment(Pos.CENTER);

        // Draft member and available people lists
        roadMapList.addAll(currentRoadMap.getReleases());

        ObservableList<Release> availableReleases = observableArrayList();
        for (Project proj : Global.currentWorkspace.getProjects()) {
            for (Release release : proj.getReleases()) {
                if (!roadMapList.contains(release)) {
                    availableReleases.add(release);
                }
            }
        }
        availableReleases.removeAll(roadMapList);

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentRoadMap.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);


        // List views
        FilteredListView<Release> roadMapReleaseFiltered = new FilteredListView<>(roadMapList,
                "road map releases");
        SearchableListView<Release> roadMapReleaseListView = roadMapReleaseFiltered.getListView();
        roadMapReleaseListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        roadMapReleaseListView.getSelectionModel().select(0);

        FilteredListView<Release> availableReleaseFiltered = new FilteredListView<Release>(availableReleases,
                "available releases");
        SearchableListView<Release> availableReleaseListView = availableReleaseFiltered.getListView();
        availableReleaseListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableReleaseListView.getSelectionModel().select(0);

        VBox roadMapReleaseBox = new VBox(10);
        SearchableText backlogStoriesLabel = new SearchableText("RoadMap Releases: ");
        backlogStoriesLabel.setStyle("-fx-font-weight: bold");
        roadMapReleaseBox.getChildren().addAll(backlogStoriesLabel, roadMapReleaseFiltered);

        VBox availableReleaseBox = new VBox(10);
        SearchableText availableStoriesLabel = new SearchableText("Available Releases: ");
        availableStoriesLabel.setStyle("-fx-font-weight: bold");
        availableReleaseBox.getChildren().addAll(availableStoriesLabel, availableReleaseFiltered);

        HBox storyListViews = new HBox(10);
        storyListViews.getChildren().addAll(roadMapReleaseBox, assignmentButtons, availableReleaseBox);
        storyListViews.setPrefHeight(192);

        // Events
        btnAssign.setOnAction((event) -> {
            errorField.setText("");
            roadMapList.addAll(
                    availableReleaseListView.getSelectionModel().getSelectedItems());
            availableReleases.removeAll(
                    availableReleaseListView.getSelectionModel().getSelectedItems());
            roadMapReleaseFiltered.resetInputText();
            availableReleaseFiltered.resetInputText();
        });

        btnUnassign.setOnAction((event) -> {
            errorField.setText("");
            availableReleases.addAll(
                    roadMapReleaseListView.getSelectionModel().getSelectedItems());
            roadMapList.removeAll(
                    roadMapReleaseListView.getSelectionModel().getSelectedItems());
            roadMapReleaseFiltered.resetInputText();
            availableReleaseFiltered.resetInputText();
        });

        editPane.getChildren().addAll(
                shortNameField,
                tagBox,
                priorityNumberField,
                storyListViews,
                errorField
        );

        // Add items to pane & search collection
        Collections.addAll(searchControls,
                shortNameField,
                priorityNumberField,
                availableReleaseFiltered,
                availableStoriesLabel,
                roadMapReleaseFiltered,
                backlogStoriesLabel,
                errorField
        );
    }

    /**
     * Cancels the edit
     */
    public void cancel() {
        currentRoadMap.switchToInfoScene();
    }

    /**
     * Changes the values depending on what the user edits
     */
    public void done() {
        boolean shortNameUnchanged = priorityNumberField.getText().equals(
                currentRoadMap.getShortName().toString());
        boolean priorityUnchanged = priorityNumberField.getText().equals(
                currentRoadMap.getPriority().toString());
        boolean tagsUnchanged = tagField.getTags().equals(currentRoadMap.getTags());

        if (shortNameUnchanged && priorityUnchanged && tagsUnchanged) {
            currentRoadMap.switchToInfoScene();
            return;
        }

        boolean correctShortName = ShortNameValidator.validateShortName(shortNameField,
                currentRoadMap.getShortName());
        boolean correctPriority = validatePriorityField(priorityNumberField, null, null);

        if (correctPriority && correctShortName) { // validation
            // Edit Command.
            ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());
            currentRoadMap.edit(shortNameField.getText(), new Integer(priorityNumberField.getText()),
                    roadMapList, tags);
            currentRoadMap.switchToInfoScene();
            App.mainPane.refreshTree();
        }

    }
}
