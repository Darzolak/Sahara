package seng302.group2.scenes.sceneswitch.switchStrategies.category;

import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.person.PersonCategoryScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.CategorySwitchStrategy;

/**
 * The switch strategy for the Person category
 * Created by jml168 on 7/06/15.
 */
public class PersonCategorySwitchStrategy implements CategorySwitchStrategy {
    
    /**
     * Sets the Main Pane to be an instance of the PersonCategoryScene.
     */
    @Override
    public void switchScene() {
        App.mainPane.setContent(new PersonCategoryScene(Global.currentWorkspace));
        App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);

    }
}
