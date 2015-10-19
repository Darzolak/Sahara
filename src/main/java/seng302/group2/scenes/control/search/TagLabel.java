package seng302.group2.scenes.control.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import seng302.group2.scenes.control.CustomInfoLabel;
import seng302.group2.scenes.information.tag.TagCellNode;
import seng302.group2.workspace.project.story.acceptanceCriteria.AcceptanceCriteria;
import seng302.group2.workspace.tag.Tag;

import java.util.HashSet;
import java.util.Set;

/**
 * A class for displaying the tags on an info scene.
 * Created by Bronson Laptop on 21/09/2015.
 */
public class TagLabel extends HBox implements SearchableControl {
    private Set<SearchableControl> searchControls = new HashSet<>();
    ObservableList<Tag> tags = FXCollections.observableArrayList();


    public TagLabel() {}

    public TagLabel(ObservableList<Tag> newTags) {
        this.tags = newTags;
        construct();
    }

    public void construct() {
        this.getChildren().clear();
        this.setSpacing(5);
        CustomInfoLabel label = new CustomInfoLabel("Tags: ", "", searchControls);
        this.getChildren().add(label);

        if (this.tags == null) {
            this.tags = FXCollections.observableArrayList();
        }

        for (Tag tag : this.tags) {
            TagCellNode node = new TagCellNode(tag, false, searchControls);
            searchControls.add(node);
            this.getChildren().add(node);
        }
    }


    @Override
    public boolean query(String query) {
        boolean found = false;
        for (SearchableControl control : searchControls) {
            found = control.query(query) || found;
        }
        return found;
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        int found = 0;
        for (SearchableControl control : searchControls) {
            found = control.advancedQuery(query, searchType);
            if (found > 0) {
                return found;
            }
        }
        return found;
    }
}
