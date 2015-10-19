package seng302.group2.scenes.information.project.backlog;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.*;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.dialog.CustomDialog;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.tag.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * The backlog edit tab.
 * Created by btm38 on 30/07/15.
 */
public class BacklogEditTab extends SearchableTab {
    private List<SearchableControl> searchControls = new ArrayList<>();
    private Backlog currentBacklog;
    private RequiredField shortNameField = new RequiredField("Short Name:", searchControls);
    private CustomComboBox<String> scaleComboBox = new CustomComboBox<>("Scale", true, searchControls);
    private CustomTextField longNameField = new CustomTextField("Long Name:");
    private CustomTextArea descriptionField = new CustomTextArea("Backlog Description:", 300);
    private ObservableList<Story> backlogStoryList = observableArrayList();
    private TagField tagField;

    /**
     * Constructor for the BacklogEditTab class. This constructor creates a JavaFX ScrollPane
     * which is populated with relevant controls then shown.
     *
     * @param currentBacklog the backlog being edited
     */
    public BacklogEditTab(Backlog currentBacklog) {
        // Init
        this.currentBacklog = currentBacklog;
        construct();
    }


    /**
     * Checks if the changes in the scene are valid
     *
     * @return If the changes in the scene are valid
     */
    private boolean isValidState() {

        ButtonType confirm;
        if (!scaleComboBox.getValue().equals(currentBacklog.getScale())) {
            confirm = CustomDialog.showConfirmation("Estimation Scale", "All existing estimations "
                    + "will be lost if the scale is changed. Continue?");
            if (confirm == ButtonType.CANCEL) {
                return false;
            }

        }
        return (shortNameField.getText().equals(currentBacklog.getShortName())  // Is the same,
                || ShortNameValidator.validateShortName(shortNameField, null)); // new name validate
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
        // Setup basic GUI
        this.setText("Edit Backlog");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);

        // Basic information fields
        shortNameField.setText(currentBacklog.getShortName());
        shortNameField.setMaxWidth(275);
        longNameField.setText(currentBacklog.getLongName());
        longNameField.setMaxWidth(275);
        descriptionField.setText(currentBacklog.getDescription());
        descriptionField.setMaxWidth(275);
        SearchableText errorField = new SearchableText("");

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentBacklog.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);

        scaleComboBox = new CustomComboBox<>("Estimation Scale:", true);

        for (String scaleName : Global.currentWorkspace.getEstimationScales().getEstimationScaleDict().keySet()) {
            scaleComboBox.addToComboBox(scaleName);
        }

        scaleComboBox.setValue(currentBacklog.getScale());

        HBox scaleHBox = new HBox();
        HBox.setHgrow(scaleHBox, Priority.ALWAYS);
        scaleHBox.getChildren().add(scaleComboBox);

        // Story assignment buttons
        Button btnAssign = new Button("<");
        Button btnUnassign = new Button(">");
        VBox assignmentButtons = new VBox();
        assignmentButtons.spacingProperty().setValue(10);
        assignmentButtons.getChildren().addAll(btnAssign, btnUnassign);
        assignmentButtons.setAlignment(Pos.CENTER);

        // Draft member and available people lists
        backlogStoryList.addAll(currentBacklog.getStories());

        ObservableList<Story> availableStoryList = observableArrayList();
        for (Story story : currentBacklog.getProject().getUnallocatedStories()) {
            if (story.getBacklog() == null) {
                availableStoryList.add(story);
            }
        }
        availableStoryList.removeAll(backlogStoryList);


        // List views
        FilteredListView<Story> backlogStoryListBox = new FilteredListView<>(backlogStoryList, "stories");
        SearchableListView<Story> backlogStoryListView = backlogStoryListBox.getListView();
        backlogStoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        backlogStoryListView.getSelectionModel().select(0);

        FilteredListView<Story> availableStoryListBox = new FilteredListView<>(availableStoryList, "stories");
        SearchableListView<Story> availableStoryListView = availableStoryListBox.getListView();
        availableStoryListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableStoryListView.getSelectionModel().select(0);

        VBox backlogStoryBox = new VBox(10);
        SearchableText backlogStoriesLabel = new SearchableText("Backlog Stories: ");
        backlogStoriesLabel.setStyle("-fx-font-weight: bold");
        backlogStoryBox.getChildren().addAll(backlogStoriesLabel, backlogStoryListBox);

        VBox availableStoryBox = new VBox(10);
        SearchableText availableStoriesLabel = new SearchableText("Available Stories: ");
        availableStoriesLabel.setStyle("-fx-font-weight: bold");
        availableStoryBox.getChildren().addAll(availableStoriesLabel, availableStoryListBox);

        HBox storyListViews = new HBox(10);
        storyListViews.getChildren().addAll(backlogStoryBox, assignmentButtons, availableStoryBox);
        storyListViews.setPrefHeight(192);

        // Events
        btnAssign.setOnAction((event) -> {
            boolean uniquePriority = true;
            Story errorStory = null;
            errorField.setText("");
            outerloop:
            for (Story story : backlogStoryList) {
                for (Story addedStory : availableStoryListView.getSelectionModel().getSelectedItems()) {
                    if (story.getPriority().equals(addedStory.getPriority())) {
                        uniquePriority = false;
                        errorStory = addedStory;
                        break outerloop;
                    }
                }
            }

            if (uniquePriority) {
                backlogStoryList.addAll(
                        availableStoryListView.getSelectionModel().getSelectedItems());
                availableStoryList.removeAll(
                        availableStoryListView.getSelectionModel().getSelectedItems());
            }
            else {
                errorField.setStyle("-fx-text-fill: #ff0000;");
                errorField.setText("* Story \"" + errorStory.getShortName() + "\" must have a unique priority.");
            }
            availableStoryListBox.resetInputText();
            backlogStoryListBox.resetInputText();

        });

        btnUnassign.setOnAction((event) -> {
            errorField.setText("");
            availableStoryList.addAll(
                    backlogStoryListView.getSelectionModel().getSelectedItems());
            backlogStoryList.removeAll(
                    backlogStoryListView.getSelectionModel().getSelectedItems());
            availableStoryListBox.resetInputText();
            backlogStoryListBox.resetInputText();
        });


        editPane.getChildren().addAll(
                shortNameField,
                tagBox,
                longNameField,
                descriptionField,
                scaleHBox,
                storyListViews,
                errorField
        );

        // Add items to pane & search collection
        Collections.addAll(searchControls,
                shortNameField,
                longNameField,
                descriptionField,
                scaleComboBox,
                availableStoryListView,
                availableStoriesLabel,
                backlogStoryListView,
                backlogStoriesLabel,
                errorField
        );
    }

    /**
     * Cancels the edit
     */
    public void cancel() {
        currentBacklog.switchToInfoScene();
    }

    /**
     * Changes the values depending on what the user edits
     */
    public void done() {

        boolean shortNameUnchanged = shortNameField.getText().equals(currentBacklog.getShortName());
        boolean tagsUnchanged = tagField.getTags().equals(currentBacklog.getTags());
        boolean longNameUnchanged = longNameField.getText().equals(currentBacklog.getLongName());
        boolean descriptionUnchanged = descriptionField.getText().equals(currentBacklog.getDescription());
        boolean scaleUnchanged = scaleComboBox.getValue().equals(currentBacklog.getScale());
        boolean storiesUnchanged = backlogStoryList.equals(currentBacklog.getStories());

        if (shortNameUnchanged && tagsUnchanged && longNameUnchanged && descriptionUnchanged && scaleUnchanged
                && storiesUnchanged) {
            currentBacklog.switchToInfoScene();
            return;
        }

        if (isValidState()) { // validation
            // Edit Command.
            ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());
            currentBacklog.edit(shortNameField.getText(),
                    longNameField.getText(),
                    descriptionField.getText(),
                    currentBacklog.getProductOwner(),
                    currentBacklog.getProject(),
                    scaleComboBox.getValue(),
                    backlogStoryList,
                    tags
            );

            Collections.sort(currentBacklog.getProject().getBacklogs());
            currentBacklog.switchToInfoScene();
            App.mainPane.refreshTree();
        }
    }
}

