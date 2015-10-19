package seng302.group2.scenes.information.project.sprint;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.project.sprint.Sprint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A class for displaying the sprint scene.
 *
 * Created by drm127 on 29/07/15.
 */
public class SprintScene extends TrackedTabPane {

    Collection<SearchableTab> searchableTabs = new ArrayList<>();


    SprintInfoTab informationTab;
    SprintBurndownTab burndownTab;
    ScrumboardTab scrumboardTab;
    SprintTaskStatusTab taskStatusTab;
    SprintLogTab loggingEffortTab;
    SprintEditTab editTab;


    Sprint currentSprint;
    boolean editScene = false;


    /**
     * Basic constructor for the sprint scene.
     * @param currentSprint the sprint for which information will be displayed.
     */
    public SprintScene(Sprint currentSprint) {
        super(ContentScene.SPRINT, currentSprint);

        this.currentSprint = currentSprint;

        //Define and add the tabs
        informationTab = new SprintInfoTab(currentSprint);
        burndownTab = new SprintBurndownTab(currentSprint);
        scrumboardTab = new ScrumboardTab(currentSprint);
        taskStatusTab = new SprintTaskStatusTab(currentSprint);
        loggingEffortTab = new SprintLogTab(currentSprint);


        Collections.addAll(searchableTabs, informationTab, scrumboardTab, burndownTab, taskStatusTab, loggingEffortTab);
        this.getTabs().addAll(searchableTabs);  // Add the tabs to the pane
    }


    /**
     * Constructor for the SprintScene. This should only be used to display an edit tab.
     * @param currentSprint the sprint for which information will be displayed.
     * @param editScene a boolean - if the scene is an edit scene
     */
    public SprintScene(Sprint currentSprint, boolean editScene) {
        super(ContentScene.SPRINT_EDIT, currentSprint);

        this.currentSprint = currentSprint;
        this.editScene = editScene;

        //Define and add the tabs
        editTab = new SprintEditTab(currentSprint);

        Collections.addAll(searchableTabs, editTab);
        this.getTabs().addAll(searchableTabs);  // Add the tabs to the pane
    }


    /**
     * Gets all the SearchableTabs on this scene
     * @return collection of SearchableTabs
     */
    @Override
    public Collection<SearchableTab> getSearchableTabs() {
        return searchableTabs;
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

}
