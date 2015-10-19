package seng302.group2.scenes.information.project.release;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.CustomInfoLabel;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.control.search.SearchableTitle;
import seng302.group2.scenes.control.search.TagLabel;
import seng302.group2.workspace.project.release.Release;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * The release information tab.
 * Created by jml168 on 11/05/15.
 */
public class ReleaseInfoTab extends SearchableTab {

    private Release currentRelease;
    private List<SearchableControl> searchControls = new ArrayList<>();
    
    /**
     * Constructor for the Release Info Tab
     * 
     * @param currentRelease The currently selected Release
     */
    public ReleaseInfoTab(Release currentRelease) {
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
        this.setText("Basic Information");
        Pane basicInfoPane = new VBox(10);
        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

        // Create controls
        SearchableTitle title = new SearchableTitle(currentRelease.getShortName());
        TagLabel releaseTags  = new TagLabel(currentRelease.getTags());
        CustomInfoLabel description = new CustomInfoLabel("Release Description: ", currentRelease.getDescription());
        String releaseDateString = "";
        if (currentRelease.getEstimatedDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            releaseDateString = currentRelease.getEstimatedDate().format(formatter);
        }
        CustomInfoLabel releaseDate = new CustomInfoLabel("Estimated Release Date: ", releaseDateString);
        CustomInfoLabel projectLabel = new CustomInfoLabel("Project: ", currentRelease.getProject().toString());

        // Add items to pane & search collection
        basicInfoPane.getChildren().addAll(
                title,
                releaseTags,
                description,
                releaseDate,
                projectLabel
        );

        Collections.addAll(searchControls,
                title,
                releaseTags,
                description,
                releaseDate,
                projectLabel
        );
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Release Info Tab";
    }

    /**
     * Switches to the edit scene
     */
    public void edit() {
        currentRelease.switchToInfoScene(true);
    }

}
