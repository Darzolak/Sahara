package seng302.group2.scenes.information.workspace;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.scenes.control.CustomTextArea;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.control.search.TagField;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.workspace.Workspace;

import java.util.*;

/**
 * A class for displaying a tab used to edit the workspaces.
 * Created by btm38 on 30/07/15.
 */
public class WorkspaceEditTab extends SearchableTab {

    Set<SearchableControl> searchControls = new HashSet<>();
    Workspace currentWorkspace;

    // Create Controls
    RequiredField shortNameCustomField = new RequiredField("Short Name:");
    RequiredField longNameCustomField = new RequiredField("Long Name:");
    CustomTextArea descriptionTextArea = new CustomTextArea("Workspace Description:", 300);
    TagField tagField;

    /**
     * Constructor for the WorkspaceEditTab class. This constructor creates a JavaFX ScrollPane
     * which is populated with relevant controls and then shown.
     * @param currentWorkspace The workspace being edited
     */
    public WorkspaceEditTab(Workspace currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
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
        this.setText("Edit Workspace");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);

        // Set fields
        shortNameCustomField.setText(currentWorkspace.getShortName());
        longNameCustomField.setText(currentWorkspace.getLongName());
        descriptionTextArea.setText(currentWorkspace.getDescription());

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentWorkspace.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);

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
        currentWorkspace.switchToInfoScene();
    }

    /**
     * Changes the values depending on what the user edits
     */
    public void done() {
        boolean shortNameUnchanged = shortNameCustomField.getText().equals(currentWorkspace.getShortName());
        boolean longNameUnchanged = longNameCustomField.getText().equals(currentWorkspace.getLongName());
        boolean descriptionUnchanged = descriptionTextArea.getText().equals(currentWorkspace.getDescription());
        boolean tagsUnchanged = tagField.getTags().equals(currentWorkspace.getTags());

        if (shortNameUnchanged && longNameUnchanged && descriptionUnchanged && tagsUnchanged) {
            currentWorkspace.switchToInfoScene();
            return;
        }

        boolean correctShortName = ShortNameValidator.validateShortName(shortNameCustomField,
                currentWorkspace.getShortName());


        if (correctShortName) {
            ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());
            currentWorkspace.edit(shortNameCustomField.getText(), longNameCustomField.getText(),
                    descriptionTextArea.getText(), tags);
            currentWorkspace.switchToInfoScene();
            App.mainPane.refreshTree();
        }
    }
}
