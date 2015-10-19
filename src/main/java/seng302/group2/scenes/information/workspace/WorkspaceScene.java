package seng302.group2.scenes.information.workspace;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collections;

/**
 * A class for displaying the project scene
 *
 * @author jml168
 */
public class WorkspaceScene extends TrackedTabPane {

    Workspace currentWorkspace;
    boolean editScene = false;

    WorkspaceInfoTab informationTab;
    WorkspaceEditTab editTab;

    /**
     * Constructor for the Workspace Scene. Creates an instance of the WorkspaceInfoTab and displays it.
     * 
     * @param currentWorkspace The currently selected Workspace. 
     */
    public WorkspaceScene(Workspace currentWorkspace) {
        super(ContentScene.WORKSPACE, currentWorkspace);

        this.currentWorkspace = currentWorkspace;

        // Define and add the tabs
        informationTab = new WorkspaceInfoTab(currentWorkspace);
        updateAllTabs();

        Collections.addAll(getSearchableTabs(), informationTab);
        this.getTabs().addAll(getSearchableTabs());  // Add the tabs to the pane
    }

    /**
     * Constructor for the Workspace Scene. This creates an instance of the WorkspaceEditTab tab and displays it.
     *
     * @param currentWorkspace the person who will be edited
     * @param editScene boolean - if the scene if an edit scene
     */
    public WorkspaceScene(Workspace currentWorkspace, boolean editScene) {
        super(ContentScene.WORKSPACE_EDIT, currentWorkspace);

        this.editScene = editScene;
        this.currentWorkspace = currentWorkspace;

        // Define and add the tabs
        informationTab = new WorkspaceInfoTab(currentWorkspace);
        editTab = new WorkspaceEditTab(currentWorkspace);
        updateAllTabs();
        Collections.addAll(getSearchableTabs(), editTab);

        this.getTabs().addAll(editTab);  // Add the tabs to the pane
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
