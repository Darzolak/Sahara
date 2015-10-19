package seng302.group2.scenes.sceneswitch.switchStrategies.category.subCategory.project;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.project.story.StoryCategoryScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.SubCategorySwitchStrategy;
import seng302.group2.workspace.categories.Category;
import seng302.group2.workspace.categories.subCategory.project.StoryCategory;

/**
 * A switch strategy for project story categories
 * Created by Jordane on 8/06/2015.
 */
public class StoryCategorySwitchStrategy implements SubCategorySwitchStrategy {

    /**
     * Sets the Main Pane to be an instance of the StoryCategoryScene.
     * 
     * @param storyCategory The Story Category to display.
     */
    @Override
    public void switchScene(Category storyCategory) {
        if (storyCategory instanceof StoryCategory) {
            App.mainPane.setContent(new StoryCategoryScene((StoryCategory) storyCategory));
            App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);

        }
    }
}
