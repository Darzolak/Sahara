package seng302.group2.scenes.information.skill;

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
import seng302.group2.scenes.dialog.CreateSkillDialog;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.workspace.Workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static seng302.group2.scenes.dialog.DeleteDialog.showDeleteDialog;

/**
 * A class for displaying a tab showing data on all the skills in the workspace.
 * Created by btm38 on 13/07/15.
 */
public class SkillCategoryTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    Workspace currentWorkspace;

    /**
     * Constructor for SkillCategoryTab class.
     * @param currentWorkspace The current workspace
     */
    public SkillCategoryTab(Workspace currentWorkspace) {
        // Tab Settings
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
        this.setText("Skills");
        Pane categoryPane = new VBox(10);
        categoryPane.setBorder(null);
        categoryPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(categoryPane);
        this.setContent(wrapper);

        // Create Controls
        SearchableText title = new SearchableTitle("Skills in " + currentWorkspace.getShortName());
        FilteredListView<Skill> skillFilteredListView = new FilteredListView<>(currentWorkspace.getSkills(),
                "skills");
        SearchableListView<Skill> skillBox = skillFilteredListView.getListView();
        skillBox.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        skillBox.setMaxWidth(275);

        Button btnView = new Button("View");
        Button btnDelete = new Button("Delete");
        Button btnCreate = new Button("Create New Skill");

        HBox selectionButtons = new HBox();
        selectionButtons.spacingProperty().setValue(10);
        selectionButtons.getChildren().addAll(btnView, btnDelete, btnCreate);
        selectionButtons.setAlignment(Pos.TOP_LEFT);

        // Events
        btnView.setOnAction((event) -> {
            if (skillBox.getSelectionModel().getSelectedItem() != null) {
                App.mainPane.selectItem(skillBox.getSelectionModel().getSelectedItem());
            }
        });

        btnDelete.setOnAction((event) -> {
            if (skillBox.getSelectionModel().getSelectedItem() != null) {
                showDeleteDialog(skillBox.getSelectionModel().getSelectedItem());
            }
        });

        btnCreate.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateSkillDialog();
            creationDialog.show();
        });

        // Add items to pane & search collection
        categoryPane.getChildren().addAll(
                title,
                skillFilteredListView,
                selectionButtons
        );

        Collections.addAll(
                searchControls,
                title,
                skillFilteredListView
        );
    }
}

