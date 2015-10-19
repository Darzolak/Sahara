package seng302.group2.scenes.information.role;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collections;

/**
 * A class for displaying the role category. Contains information
 * about all the roles in the workspace.
 *
 * Created by btm38 on 14/07/15.
 */
public class RoleCategoryScene extends TrackedTabPane {

    Workspace currentWorkspace;
    SearchableTab categoryTab;

    /**
     * Constructor for the RoleCategoryScene class. Creates a tab
     * of RoleCategoryTab and displays it.
     * @param currentWorkspace the current workspace
     */
    public RoleCategoryScene(Workspace currentWorkspace) {
        super(ContentScene.ROLE_CATEGORY, currentWorkspace);

        this.currentWorkspace = currentWorkspace;

        // Define and add the tabs
        categoryTab = new RoleCategoryTab(currentWorkspace);
        updateAllTabs();

        Collections.addAll(getSearchableTabs(), categoryTab);
        this.getTabs().addAll(getSearchableTabs());  // Add the tabs to the pane
    }

    public void done() {}
    public void edit() {}
    public void cancel() {}
}