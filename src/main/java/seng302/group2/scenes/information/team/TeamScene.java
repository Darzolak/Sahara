package seng302.group2.scenes.information.team;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.team.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A class for displaying the Team Scene
 *
 * @author crw73
 */
public class TeamScene extends TrackedTabPane {

    Collection<SearchableTab> searchableTabs = new ArrayList<>();

    Team currentTeam;
    boolean editScene = false;

    TeamInfoTab informationTab;
    TeamEditTab editTab;

    /**
     * Constructor for the Team scene. Creates an instance of the TeamInfoTab and displays it. If the team is not
     * the unassigned team, also creates an instance of the TeamHistoryTab to display.
     * 
     * @param currentTeam The currently selected Team.
     */
    public TeamScene(Team currentTeam) {
        super(ContentScene.TEAM, currentTeam);

        this.currentTeam = currentTeam;

        // Define and add the tabs
        informationTab = new TeamInfoTab(currentTeam);
        editTab = new TeamEditTab(currentTeam);
        updateAllTabs();

        Collections.addAll(searchableTabs, informationTab);
        this.getTabs().addAll(searchableTabs);

        if (!currentTeam.isUnassignedTeam()) {
            SearchableTab historyTab = new TeamHistoryTab(currentTeam);
            SearchableTab velocityTab = new TeamVelocityTab(currentTeam);

            this.getTabs().addAll(velocityTab, historyTab);  // Add the tabs to the pane
            searchableTabs.add(historyTab);
            searchableTabs.add(velocityTab);

        }
    }

    /**
     * Constructor for the PersonScene. This should only used to display an edit tab.
     * @param currentTeam the team who will be edited
     * @param editScene a boolean - if the scene is an edit scene
     */
    public TeamScene(Team currentTeam, boolean editScene) {
        super(ContentScene.TEAM_EDIT, currentTeam);

        this.currentTeam = currentTeam;
        this.editScene = editScene;

        // Define and add the tabs
        informationTab = new TeamInfoTab(currentTeam);
        editTab = new TeamEditTab(currentTeam);
        updateAllTabs();
        Collections.addAll(searchableTabs, editTab);

        this.getTabs().addAll(editTab);

    }

    /**
     * Calls the done functionality behind the done button on the edit tab
     */
    @Override
    public void done() {
        if (getSelectionModel().getSelectedItem() == editTab) {
            editTab.done();
        }
    }

    /**
     * Calls the functionality behind the edit button on the info tab
     */
    @Override
    public void edit() {
        if (getSelectionModel().getSelectedItem() == informationTab) {
            informationTab.edit();
        }
    }

    /**
     * Calls the functionality behind the edit button on the edit tab
     */
    @Override
    public void cancel() {
        if (getSelectionModel().getSelectedItem() == editTab) {
            editTab.cancel();
        }
    }


    /**
     * Gets all the SearchableTabs on this scene
     * @return collection of SearchableTabs
     */
    @Override
    public Collection<SearchableTab> getSearchableTabs() {
        return searchableTabs;
    }

}