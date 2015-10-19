package seng302.group2.scenes.information.project.backlog;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.FilteredListView;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.dialog.CreateBacklogDialog;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.subCategory.project.BacklogCategory;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.role.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static seng302.group2.scenes.dialog.DeleteDialog.showDeleteDialog;


/**
 * A class for displaying a tab showing data on all the backlogs in the current project.
 * Created by btm38 on 13/07/15.
 */
public class BacklogCategoryTab extends SearchableTab {

    private List<SearchableControl> searchControls = new ArrayList<>();
    private BacklogCategory selectedCategory;

    /**
     * Constructor for BacklogCategoryTab class.
     * @param selectedCategory The current selected category
     */
    public BacklogCategoryTab(BacklogCategory selectedCategory) {
        this.selectedCategory = selectedCategory;
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
        this.setText("Backlogs");
        Pane categoryPane = new VBox(10);
        categoryPane.setBorder(null);
        categoryPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(categoryPane);
        this.setContent(wrapper);

        // Create controls
        SearchableText title = new SearchableTitle("Backlogs in " + selectedCategory.getProject().toString());

        Button btnView = new Button("View");
        Button btnDelete = new Button("Delete");
        Button btnCreate = new Button("Create New Backlog");

        HBox selectionButtons = new HBox();
        selectionButtons.spacingProperty().setValue(10);
        selectionButtons.getChildren().addAll(btnView, btnDelete, btnCreate);
        selectionButtons.setAlignment(Pos.TOP_LEFT);

        FilteredListView<Backlog> backlogFilteredListView = new FilteredListView<>(
                selectedCategory.getProject().getBacklogs(), "backlogs");
        SearchableListView backlogListView = backlogFilteredListView.getListView();
        backlogListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        backlogFilteredListView.setMaxWidth(450);

        boolean poExists = false;
        for (Person person : Global.currentWorkspace.getPeople()) {
            if (person.getSkills().containsAll(Role.getRoleFromType(Role.RoleType.PRODUCT_OWNER)
                    .getRequiredSkills())) {
                poExists = true;
            }
        }
        btnCreate.setDisable(!poExists);


        // Events
        btnView.setOnAction((event) -> {
            if (backlogListView.getSelectionModel().getSelectedItem() != null) {
                App.mainPane.selectItem((SaharaItem)
                        backlogListView.getSelectionModel().getSelectedItem());
            }
        });

        btnDelete.setOnAction((event) -> {
            if (backlogListView.getSelectionModel().getSelectedItem() != null) {
                showDeleteDialog((SaharaItem) backlogListView
                        .getSelectionModel().getSelectedItem());
            }
        });

        btnCreate.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateBacklogDialog(selectedCategory.getProject());
            creationDialog.show();
        });

        // Add items to pane & search collection
        categoryPane.getChildren().addAll(title, backlogFilteredListView, selectionButtons);
        Collections.addAll(searchControls, title, backlogFilteredListView);
    }
}
