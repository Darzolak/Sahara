package seng302.group2.scenes.information.project;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collections;

/**
 * A class for displaying the project category. Contains information
 * about all the projects in the workspace.
 *
 * Created by btm38 on 14/07/15.
 */
public class ProjectCategoryScene extends TrackedTabPane {

    Workspace currentWorkspace;
    SearchableTab categoryTab;

    /**
     * Constructor for the ProjectCategoryScene class. Creates a tab
     * of ProjectCategoryTab and displays it.
     * @param currentWorkspace the current workspace
     */
    public ProjectCategoryScene(Workspace currentWorkspace) {
        super(TrackedTabPane.ContentScene.PROJECT_CATEGORY, currentWorkspace);

        this.currentWorkspace = currentWorkspace;

        // Define and add the tabs
        categoryTab = new ProjectCategoryTab(currentWorkspace);
        //updateAllTabs();

        Collections.addAll(getSearchableTabs(), categoryTab);
        this.getTabs().addAll(getSearchableTabs());
    }
    public void done() {}
    public void edit() {}
    public void cancel() {}

}
