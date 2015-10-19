package seng302.group2.scenes.information.project.release;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.scenes.control.CustomDatePicker;
import seng302.group2.scenes.control.CustomTextArea;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.control.search.TagField;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.tag.Tag;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A class for displaying a tab used to edit releases.
 * Created by btm38 on 30/07/15.
 */
public class ReleaseEditTab extends SearchableTab {

    private Release currentRelease;
    private List<SearchableControl> searchControls = new ArrayList<>();

    // Create controls
    private RequiredField shortNameCustomField = new RequiredField("Short Name:");
    private CustomTextArea descriptionTextArea = new CustomTextArea("Release Description:", 300);
    private CustomDatePicker releaseDatePicker = new CustomDatePicker("Estimated Release Date:", false);
    private TagField tagField;

    /**
     * Constructor for the ReleaseEditTab class. This constructor creates a JavaFX ScrollPane
     * which is populated with relevant controls then shown.
     *
     * @param currentRelease the release being edited
     */
    public ReleaseEditTab(Release currentRelease) {
        this.currentRelease = currentRelease;
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
        this.setText("Edit Release");
        Pane editPane = new VBox(10);
        editPane.setBorder(null);
        editPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(editPane);
        this.setContent(wrapper);

        // Set up the tagging field
        SearchableText tagLabel = new SearchableText("Tags:", "-fx-font-weight: bold;", searchControls);
        tagLabel.setMinWidth(60);
        tagField = new TagField(currentRelease.getTags(), searchControls);
        HBox.setHgrow(tagField, Priority.ALWAYS);

        HBox tagBox = new HBox();
        tagBox.getChildren().addAll(tagLabel, tagField);

        shortNameCustomField.setPrefWidth(370);
        descriptionTextArea.setPrefWidth(370);
        releaseDatePicker.setPrefWidth(370);

        shortNameCustomField.setText(currentRelease.getShortName());
        descriptionTextArea.setText(currentRelease.getDescription());
        releaseDatePicker.setValue(currentRelease.getEstimatedDate());

        releaseDatePicker.getDatePicker().valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable,
                                LocalDate oldValue, LocalDate newValue) {
                LocalDate lastSprintEnd = null;
                for (Sprint sprint : currentRelease.getProject().getSprints()) {
                    if (sprint.getRelease() == currentRelease && lastSprintEnd == null) {
                        lastSprintEnd = sprint.getEndDate();
                    }
                    else if (sprint.getRelease() == currentRelease && sprint.getEndDate().isAfter(lastSprintEnd)) {
                        lastSprintEnd = sprint.getEndDate();
                    }
                }
                if (newValue != null && lastSprintEnd != null && newValue.isBefore(lastSprintEnd)) {
                    ValidationStyle.borderGlowRed(releaseDatePicker.getDatePicker());
                    ValidationStyle.showMessage("The estimated date of release cannot be before the end"
                            + " date of any sprint that exists for this release", releaseDatePicker.getDatePicker());
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDITDISABLED);
                }
                else {
                    ValidationStyle.borderGlowNone(releaseDatePicker.getDatePicker());
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDIT);
                }
            }
        });

        // Add items to pane & search collection
        editPane.getChildren().addAll(
                shortNameCustomField,
                tagBox,
                descriptionTextArea,
                releaseDatePicker
        );

        Collections.addAll(searchControls,
                shortNameCustomField,
                descriptionTextArea,
                releaseDatePicker
        );
    }

    /**
     * Cancels the edit
     */
    public void cancel() {
         currentRelease.switchToInfoScene();
    }

    /**
     * Changes the values depending on what the user edits
     */
    public void done() {
        boolean shortNameUnchanged = shortNameCustomField.getText().equals(
                currentRelease.getShortName());
        boolean descriptionUnchanged = descriptionTextArea.getText().equals(
                currentRelease.getDescription());
        boolean dateUnchanged = releaseDatePicker.getValue() == currentRelease.getEstimatedDate();
        boolean tagsUnchanged = tagField.getTags().equals(currentRelease.getTags());
        if (shortNameUnchanged && descriptionUnchanged && dateUnchanged && tagsUnchanged) {
            // No fields have been changed
            currentRelease.switchToInfoScene();
            return;
        }

        LocalDate releaseDate = releaseDatePicker.getValue();

        boolean correctShortName = ShortNameValidator.validateShortName(shortNameCustomField,
                currentRelease.getShortName());
        // The short name is the same or valid
        if (correctShortName) {
            ArrayList<Tag> tags = new ArrayList<>(tagField.getTags());
            currentRelease.edit(shortNameCustomField.getText(),
                    descriptionTextArea.getText(),
                    releaseDate,
                    tags
            );

            Collections.sort(currentRelease.getProject().getReleases());
            currentRelease.switchToInfoScene();
            App.mainPane.refreshTree();
        }
    }

}

