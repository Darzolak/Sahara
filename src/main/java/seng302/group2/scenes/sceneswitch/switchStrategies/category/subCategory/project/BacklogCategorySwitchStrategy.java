package seng302.group2.scenes.sceneswitch.switchStrategies.category.subCategory.project;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.project.backlog.BacklogCategoryScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.SubCategorySwitchStrategy;
import seng302.group2.workspace.categories.Category;
import seng302.group2.workspace.categories.subCategory.project.BacklogCategory;

/**
 * A switch strategy for project backlog categories
 * Created by Jordane on 8/06/2015.
 */
public class BacklogCategorySwitchStrategy implements SubCategorySwitchStrategy {

    /**
     * Sets the Main Pane to be an instance of the BacklogCategoryScene.
     * 
     * @param backlogCategory The backlog Category to display.
     */
    @Override
    public void switchScene(Category backlogCategory) {
        if (backlogCategory instanceof BacklogCategory) {
            App.mainPane.setContent(new BacklogCategoryScene((BacklogCategory) backlogCategory));
            App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);

        }
    }
}
