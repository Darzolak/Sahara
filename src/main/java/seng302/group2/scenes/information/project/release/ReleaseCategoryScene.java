package seng302.group2.scenes.information.project.release;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.categories.subCategory.project.ReleaseCategory;

import java.util.Collections;

/**
 * A class for displaying the release category. Contains information
 * about all the releases in the project.
 *
 * Created by btm38 on 14/07/15.
 */
public class ReleaseCategoryScene extends TrackedTabPane {

    ReleaseCategory selectedCategory;
    SearchableTab informationTab;

    /**
     * Constructor for the ReleaseCategoryScene class. Creates a tab
     * of ReleaseCategoryTab and displays it.
     * @param selectedCategory the current workspace
     */
    public ReleaseCategoryScene(ReleaseCategory selectedCategory) {
        super(ContentScene.RELEASE_CATEGORY, selectedCategory.getProject());

        this.selectedCategory = selectedCategory;

        // Define and add the tabs
        informationTab = new ReleaseCategoryTab(selectedCategory);


        Collections.addAll(getSearchableTabs(), informationTab);
        this.getTabs().addAll(getSearchableTabs());  // Add the tabs to the pane
    }
    public void done() {}
    public void edit() {}
    public void cancel() {}
}