package seng302.group2.scenes.sceneswitch.switchStrategies.category.subCategory.project;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.project.release.ReleaseCategoryScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.SubCategorySwitchStrategy;
import seng302.group2.workspace.categories.Category;
import seng302.group2.workspace.categories.subCategory.project.ReleaseCategory;

/**
 * A switch strategy for project release categories
 * Created by Jordane on 8/06/2015.
 */
public class ReleaseCategorySwitchStrategy implements SubCategorySwitchStrategy {

    /**
     * Sets the Main Pane to be an instance of the ReleaseCategoryScene.
     * 
     * @param releaseCategory The Release Category to display.
     */
    @Override
    public void switchScene(Category releaseCategory) {
        if (releaseCategory instanceof ReleaseCategory) {
            App.mainPane.setContent(new ReleaseCategoryScene((ReleaseCategory) releaseCategory));
            App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);

        }
    }
}
