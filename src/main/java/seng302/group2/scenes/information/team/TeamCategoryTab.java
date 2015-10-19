package seng302.group2.scenes.information.team;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import seng302.group2.scenes.dialog.CreateTeamDialog;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static seng302.group2.scenes.dialog.DeleteDialog.showDeleteDialog;

/**
 * A class for displaying a tab showing data on all the team in the workspace.
 * Created by btm38 on 13/07/15.
 */
public class TeamCategoryTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    Workspace currentWorkspace;

    /**
     * Constructor for TeamCategoryTab class.
     * @param currentWorkspace The current workspace
     */
    public TeamCategoryTab(Workspace currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
        this.construct();
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
        // Tab Settings
        this.setText("Teams");

        Pane categoryPane = new VBox(10);
        categoryPane.setBorder(null);
        categoryPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(categoryPane);
        this.setContent(wrapper);


        // Create Controls
        SearchableText title = new SearchableTitle("Teams in " + currentWorkspace.getShortName());
        FilteredListView<Team> teamFilteredListView = new FilteredListView<>(currentWorkspace.getTeams(), "teams");
        SearchableListView<Team> teamBox = teamFilteredListView.getListView();
        teamBox.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        teamBox.setMaxWidth(275);

        Button btnView = new Button("View");
        Button btnDelete = new Button("Delete");
        Button btnCreate = new Button("Create New Team");

        HBox selectionButtons = new HBox();
        selectionButtons.spacingProperty().setValue(10);
        selectionButtons.getChildren().addAll(btnView, btnDelete, btnCreate);
        selectionButtons.setAlignment(Pos.TOP_LEFT);

        // Events
        teamBox.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Team>() {
                    @Override
                    public void changed(ObservableValue<? extends Team> observable, Team oldValue, Team newValue) {
                        if (newValue == Global.getUnassignedTeam()) {
                            btnDelete.setDisable(true);
                        }
                        else {
                            btnDelete.setDisable(false);
                        }
                    }
                }
        );
        btnView.setOnAction((event) -> {
            if (teamBox.getSelectionModel().getSelectedItem() != null) {
                App.mainPane.selectItem(teamBox.getSelectionModel().getSelectedItem());
            }
        });

        btnDelete.setOnAction((event) -> {
            if (teamBox.getSelectionModel().getSelectedItem() != null) {
                showDeleteDialog((SaharaItem)
                        teamBox.getSelectionModel().getSelectedItem());
            }
        });

        btnCreate.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateTeamDialog();
            creationDialog.show();
        });

        // Add items to pane & search collection
        categoryPane.getChildren().addAll(
                title,
                teamFilteredListView,
                selectionButtons
        );

        Collections.addAll(searchControls,
                title,
                teamFilteredListView
        );
    }
}

