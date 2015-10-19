package seng302.group2.scenes.information.project.backlog;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.categories.subCategory.project.BacklogCategory;

import java.util.Collections;

/**
 * A class for displaying the backlog category. Contains information
 * about all the backlogs in the project.
 *
 * Created by btm38 on 14/07/15.
 */
public class BacklogCategoryScene extends TrackedTabPane {

    private BacklogCategory selectedCategory;
    private SearchableTab categoryTab;
    private SearchableTab scaleInfoTab;

    /**
     * Constructor for the BacklogCategoryScene class. Creates a tab
     * of BacklogCategoryTab and displays it.
     * @param selectedCategory the current workspace
     */
    public BacklogCategoryScene(BacklogCategory selectedCategory) {
        super(ContentScene.BACKLOG_CATEGORY, selectedCategory.getProject());

        this.selectedCategory = selectedCategory;

        // Define and add the tabs
        categoryTab = new BacklogCategoryTab(selectedCategory);
        scaleInfoTab = new StoryEstimationScaleInfoTab();

        Collections.addAll(getSearchableTabs(), categoryTab, scaleInfoTab);

        this.getTabs().addAll(getSearchableTabs());  // Add the tabs to the pane
    }
    public void done() {}
    public void edit() {}
    public void cancel() {}
}
