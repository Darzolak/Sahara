package seng302.group2.scenes.information.project.story;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.categories.subCategory.project.StoryCategory;

import java.util.Collections;

/**
 * A class for displaying the story category. Contains information
 * about all the stories in the project.
 *
 * Created by btm38 on 14/07/15.
 */
public class StoryCategoryScene extends TrackedTabPane {

    StoryCategory selectedCategory;
    SearchableTab categoryTab;

    /**
     * Constructor for the StoryCategoryScene class. Creates a tab
     * of StoryCategoryTab and displays it.
     * @param selectedCategory the current workspace
     */
    public StoryCategoryScene(StoryCategory selectedCategory) {
        super(ContentScene.RELEASE_CATEGORY, selectedCategory.getProject());

        this.selectedCategory = selectedCategory;

        // Define and add the tabs
        categoryTab = new StoryCategoryTab(selectedCategory);

        Collections.addAll(getSearchableTabs(), categoryTab);

        this.getTabs().addAll(getSearchableTabs());  // Add the tabs to the pane
    }
    public void done() {}
    public void edit() {}
    public void cancel() {}
}