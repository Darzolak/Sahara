package seng302.group2.scenes.information.person;

import javafx.scene.control.Tab;
import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A class for displaying the person category. Contains information
 * about all the persons in the workspace.
 *
 * Created by btm38 on 14/07/15.
 */
public class PersonCategoryScene extends TrackedTabPane {

    private SearchableTab categoryTab;
    private Workspace currentWorkspace;

    /**
     * Constructor for the PersonCategoryScene class. Creates a tab
     * of PersonCategoryTab and displays it.
     * @param currentWorkspace the current workspace
     */
    public PersonCategoryScene(Workspace currentWorkspace) {
        super(ContentScene.PERSON_CATEGORY, currentWorkspace);

        this.currentWorkspace = currentWorkspace;

        // Define and add the tabs
        categoryTab = new PersonCategoryTab(currentWorkspace);

        Collections.addAll(getSearchableTabs(), categoryTab);
        this.getTabs().addAll(getSearchableTabs());  // Add the tabs to the pane
    }

    public void done() {}
    public void edit() {}
    public void cancel() {}

    @Override
    public void updateTabs() {
        Tab selectedTab = this.getSelectionModel().getSelectedItem();
        if (categoryTab != selectedTab) {
            categoryTab = new PersonCategoryTab(currentWorkspace);
        }

        Set<Tab> tabs = new HashSet<>();
        tabs.addAll(this.getTabs());
        tabs.clear();
        this.getTabs().addAll(tabs);
    }

    @Override
    public void updateAllTabs() {
        categoryTab = new PersonCategoryTab(currentWorkspace);
    }
}
