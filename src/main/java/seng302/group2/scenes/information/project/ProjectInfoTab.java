package seng302.group2.scenes.information.project;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.CustomInfoLabel;
import seng302.group2.scenes.control.FilteredListView;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.team.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;


/**
 * The person information tab.
 * Created by swi67 on 10/05/15.
 */
public class ProjectInfoTab extends SearchableTab {
    private ObservableList<Team> currentTeams = observableArrayList();

    private List<SearchableControl> searchControls = new ArrayList<>();

    private Project currentProject;

    /**
     * Constructor for Project info tab
     * 
     * @param currentProject currently selected project
     */
    public ProjectInfoTab(Project currentProject) {
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
        this.setText("Basic Information");
        Pane basicInfoPane = new VBox(10);
        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

        // Create controls
        SearchableText title = new SearchableTitle(currentProject.getLongName(), searchControls);

        Separator separator = new Separator();


        this.setOnSelectionChanged(event -> {
            currentTeams.clear();
            for (Team team : currentProject.getCurrentTeams()) {
                currentTeams.add(team);
            }
        });

        CustomInfoLabel shortNameField = new CustomInfoLabel("Short Name: ", currentProject.getShortName());
        TagLabel projectTags = new TagLabel(currentProject.getTags());
        CustomInfoLabel description = new CustomInfoLabel("Project Description: ", currentProject.getDescription());
        CustomInfoLabel currentTeamsLabel = new CustomInfoLabel("Current Teams:", "");
        CustomInfoLabel releasesLabel = new CustomInfoLabel("Releases:", "");


        FilteredListView<Team> teamFilteredListView = new FilteredListView<>(currentTeams, "teams");
        teamFilteredListView.setPrefHeight(192);
        teamFilteredListView.setMaxHeight(150);

        FilteredListView<Release> releaseFilteredListView = new FilteredListView<>(currentProject.getReleases(),
                "releases");
        releaseFilteredListView.setPrefHeight(192);
        releaseFilteredListView.setMaxHeight(150);

        HBox listBoxes = new HBox(12);
        VBox teamsBox = new VBox();
        VBox releaseBox = new VBox();
        teamsBox.getChildren().addAll(currentTeamsLabel, teamFilteredListView);
        releaseBox.getChildren().addAll(releasesLabel, releaseFilteredListView);
        listBoxes.getChildren().addAll(teamsBox, releaseBox);

        // Add items to pane & search collection
        basicInfoPane.getChildren().addAll(
                title,
                shortNameField,
                projectTags,
                description,
                separator,
                listBoxes
        );

        Collections.addAll(searchControls,
                title,
                shortNameField,
                projectTags,
                description,
                currentTeamsLabel,
                teamFilteredListView,
                releasesLabel,
                releaseFilteredListView
        );
    }

    @Override
    public String toString() {
        return "Project Info Tab";
    }

    /**
     * Switches to the edit scene
     */
    public void edit() {
        currentProject.switchToInfoScene(true);
    }

}
