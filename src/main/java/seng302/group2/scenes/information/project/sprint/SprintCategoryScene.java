package seng302.group2.scenes.information.project.sprint;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.categories.subCategory.project.SprintCategory;

import java.util.Collections;

/**
 * A class for displaying the sprint category. Contains information
 * about sprints in a project.
 *
 * Created by drm127 on 29/07/15.
 */
public class SprintCategoryScene extends TrackedTabPane {

    SprintCategory selectedCategory;
    SearchableTab informationTab;

    public SprintCategoryScene(SprintCategory selectedCategory) {
        super(ContentScene.SPRINT_CATEGORY, selectedCategory.getProject());

        this.selectedCategory = selectedCategory;

        // Define and add the tabs
        informationTab = new SprintCategoryTab(selectedCategory);

        Collections.addAll(getSearchableTabs(), informationTab);
        this.getTabs().addAll(getSearchableTabs());  // Add the tabs to the pane
    }
    public void done() {}
    public void edit() {}
    public void cancel() {}
}
