package seng302.group2.scenes.sceneswitch.switchStrategies.workspace.project;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.project.story.StoryScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.InformationSwitchStrategy;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.story.Story;

/**
 * An switch strategy for people information and edit scenes
 * Created by Jordane on 8/06/2015.
 */
public class StoryInformationSwitchStrategy implements InformationSwitchStrategy {

    /**
     * Sets the main pane to be an instance of the Story Scene. 
     * @param item The SaharaItem for the scene to be constructed with. 
     */
    @Override
    public void switchScene(SaharaItem item) {
        if (item instanceof Story) {
            App.mainPane.setContent(new StoryScene((Story) item));
            App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);

        }
        else {
            // Bad call
        }
    }

    /**
     * Sets the main pane to be an instance of the Story Edit Scene.
     * @param item The SaharaItem for the scene to be constructed with.
     * @param editScene Whether the edit scene is to be shown.
     */
    @Override
    public void switchScene(SaharaItem item, boolean editScene) {
        if (item instanceof Story) {
            if (editScene) {
                App.mainPane.setContent(new StoryScene((Story) item, true));
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDIT);
            }
            else {
                switchScene(item);
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);
            }
        }
        else {
            // Bad call
        }
    }
}
