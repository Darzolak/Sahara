package seng302.group2.scenes.sceneswitch.switchStrategies.category.subCategory.project;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.project.sprint.SprintCategoryScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.SubCategorySwitchStrategy;
import seng302.group2.workspace.categories.Category;
import seng302.group2.workspace.categories.subCategory.project.SprintCategory;

/**
 * A switch strategy for project sprint categories
 * Created by drm127 on 29/07/15.
 */
public class SprintCategorySwitchStrategy implements SubCategorySwitchStrategy {

    /**
     * Sets the Main Pane to be an instance of the SprintCategoryScene.
     *
     * @param sprintCategory The Sprint Category to display.
     */
    @Override
    public void switchScene(Category sprintCategory) {
        if (sprintCategory instanceof SprintCategory) {
            App.mainPane.setContent(new SprintCategoryScene((SprintCategory) sprintCategory));
            App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);

        }
    }
}
