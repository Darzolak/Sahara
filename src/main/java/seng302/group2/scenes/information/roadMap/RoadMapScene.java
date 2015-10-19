package seng302.group2.scenes.information.roadMap;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.roadMap.RoadMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Scene for displaying all the road map information.
 * Created by cvs20 on 11/09/15.
 */
public class RoadMapScene extends TrackedTabPane {

    Collection<SearchableTab> searchableTabs = new ArrayList<>();


    RoadMap currentRoadMap;
    boolean editScene = false;

    RoadMapInfoTab informationTab;
    RoadMapEditTab editTab;

    /**
     * Constructor for the RoadMapScene class. Displays an instance of RoadMapInfoTab.
     * @param currentRoadMap the current RoadMap for which information will be displayed
     */
    public RoadMapScene(RoadMap currentRoadMap) {
        super(ContentScene.ROADMAP, currentRoadMap);

        this.currentRoadMap = currentRoadMap;

        //Define and add the tabs
        informationTab = new RoadMapInfoTab(currentRoadMap);
        //updateAllTabs();

        Collections.addAll(searchableTabs, informationTab);
        this.getTabs().addAll(searchableTabs);

    }

    /**
     * Constructor for the RoadMapScene class. This creates an instance of the RoadMapEditTab tab and displays it.
     * @param currentRoadMap the RoadMap which will be edited
     * @param editScene a boolean - if the scene is an edit scene
     */
    public RoadMapScene(RoadMap currentRoadMap, boolean editScene) {
        super(ContentScene.ROADMAP_EDIT, currentRoadMap);

        this.currentRoadMap = currentRoadMap;
        this.editScene = editScene;

        // Define and add the tabs
        informationTab = new RoadMapInfoTab(currentRoadMap);
        editTab = new RoadMapEditTab(currentRoadMap);
        //updateAllTabs();

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

