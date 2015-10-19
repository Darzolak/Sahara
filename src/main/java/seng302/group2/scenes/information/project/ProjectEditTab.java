package seng302.group2.scenes.information.project;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.CustomTextArea;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.control.search.TagField;
import seng302.group2.util.validation.NameValidator;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.tag.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A class for displaying a tab showing data on all the projects in the workspace.
 * Created by btm38 on 30/07/15.
 */
public class ProjectEditTab extends SearchableTab {

    private List<SearchableControl> searchControls = new ArrayList<>();
    private Project currentProject;
    private RequiredField shortNameCustomField = new RequiredField("Short Name:");
    private RequiredField longNameCustomField = new RequiredField("Long Name:");
    private CustomTextArea descriptionTextArea = new CustomTextArea("Project Description:", 300);
    private TagField tagField;

    /**
     * Gets the workspace edit information scene.
     *
     * @param currentProject The project to display the editable information of
     */
    public ProjectEditTab(Project currentProject) {
        this.currentProject = currentProject;
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
        this.setText("Edit Project");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentProject.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);

        // Create controls

        shortNameCustomField.setMaxWidth(275);
        longNameCustomField.setMaxWidth(275);
        descriptionTextArea.setMaxWidth(275);


        // Set values
        shortNameCustomField.setText(currentProject.getShortName());
        longNameCustomField.setText(currentProject.getLongName());
        descriptionTextArea.setText(currentProject.getDescription());


        // Add items to pane & search collection
        editPane.getChildren().addAll(
                shortNameCustomField,
                tagBox,
                longNameCustomField,
                descriptionTextArea
        );

        Collections.addAll(searchControls,
                shortNameCustomField,
                longNameCustomField,
                descriptionTextArea
        );
    }

    /**
     * Cancels the edit
     */
    public void cancel() {
        currentProject.switchToInfoScene();
    }

    /**
     * Changes the values depending on what the user edits
     */
    public void done() {
        boolean shortNameUnchanged = shortNameCustomField.getText().equals(
                currentProject.getShortName());
        boolean longNameUnchanged = longNameCustomField.getText().equals(
                currentProject.getLongName());
        boolean descriptionUnchanged = descriptionTextArea.getText().equals(
                currentProject.getDescription());
        boolean tagsUnchanged = tagField.getTags().equals(currentProject.getTags());

        // If no fields have been changed
        if (shortNameUnchanged && longNameUnchanged && descriptionUnchanged && tagsUnchanged) {
            currentProject.switchToInfoScene();
            return;
        }

        boolean correctShortName = ShortNameValidator.validateShortName(shortNameCustomField,
                currentProject.getShortName());
        boolean correctLongName = NameValidator.validateName(longNameCustomField);

        if (correctShortName && correctLongName) {
            ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());
            currentProject.edit(shortNameCustomField.getText(),
                    longNameCustomField.getText(), descriptionTextArea.getText(),
                    FXCollections.observableArrayList(), tags
            );

            Collections.sort(Global.currentWorkspace.getProjects());
            currentProject.switchToInfoScene();
            App.mainPane.refreshTree();
        }
    }
}

