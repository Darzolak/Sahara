package seng302.group2.scenes.information.project.backlog;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.project.backlog.Backlog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A class for displaying the Backlog Scene.
 *
 * Created by cvs20 on 19/05/15.
 */
public class BacklogScene extends TrackedTabPane {

    private Collection<SearchableTab> searchableTabs = new ArrayList<>();

    private Backlog currentBacklog;
    private boolean editScene = false;

    private BacklogInfoTab informationTab;
    private BacklogEditTab editTab;
    
    /**
     * Constructor for the Backlog scene
     * 
     * @param currentBacklog The currently selected Backlog
     */
    public BacklogScene(Backlog currentBacklog) {
        super(ContentScene.BACKLOG, currentBacklog.getProject());

        this.currentBacklog = currentBacklog;

        // Define and add the tabs
        informationTab = new BacklogInfoTab(currentBacklog);

        Collections.addAll(searchableTabs, informationTab);
        this.getTabs().addAll(searchableTabs);  // Add the tabs to the pane
    }

    /**
     * Constructor for the BacklogScene. This creates an instance of the BacklogEditTab tab and displays it.
     *
     * @param currentBacklog The currently selected Backlog
     * @param editScene a boolean - if the scene is an edit scene
     */
    public BacklogScene(Backlog currentBacklog, boolean editScene) {
        super(ContentScene.BACKLOG_EDIT, currentBacklog.getProject());

        this.currentBacklog = currentBacklog;
        this.editScene = editScene;

        // Define and add the tabs
        informationTab = new BacklogInfoTab(currentBacklog);
        editTab = new BacklogEditTab(currentBacklog);

        Collections.addAll(searchableTabs, editTab);
        this.getTabs().addAll(searchableTabs);  // Add the tabs to the pane
    }

    /**
     * Gets all the SearchableTabs on this scene
     * @return collection of tabs
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
