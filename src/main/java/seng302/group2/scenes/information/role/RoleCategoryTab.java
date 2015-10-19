package seng302.group2.scenes.information.role;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.scenes.control.FilteredListView;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.workspace.Workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A class for displaying a tab showing data on all the roles in the workspace.
 * Created by btm38 on 13/07/15.
 */
public class RoleCategoryTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    Workspace currentWorkspace;

    /**
     * Constructor for RoleCategoryTab class.
     * @param currentWorkspace The current workspace
     */
    public RoleCategoryTab(Workspace currentWorkspace) {
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
        this.setText("Roles");
        Pane categoryPane = new VBox(10);
        categoryPane.setBorder(null);
        categoryPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(categoryPane);
        this.setContent(wrapper);

        // Create controls
        SearchableText title = new SearchableTitle("Roles in " + currentWorkspace.getShortName(), searchControls);
        FilteredListView<Role> roleFilteredListView = new FilteredListView<>(currentWorkspace.getRoles(), "roles");
        SearchableListView roleBox = roleFilteredListView.getListView();
        roleBox.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        roleBox.setMaxWidth(450);

        Button btnView = new Button("View");

        HBox selectionButtons = new HBox();
        selectionButtons.spacingProperty().setValue(10);
        selectionButtons.getChildren().addAll(btnView);
        selectionButtons.setAlignment(Pos.TOP_LEFT);

        // Events
        btnView.setOnAction((event) -> {
            if (roleBox.getSelectionModel().getSelectedItem() != null) {
                App.mainPane.selectItem((SaharaItem)
                        roleBox.getSelectionModel().getSelectedItem());
            }
        });

        // Add items to pane & search collection
        categoryPane.getChildren().addAll(
                title,
                roleFilteredListView,
                selectionButtons
        );

        Collections.addAll(searchControls,
                title,
                roleFilteredListView
        );
    }
}
