package seng302.group2.scenes.sceneswitch.switchStrategies.workspace.project;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.project.sprint.SprintScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.InformationSwitchStrategy;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.sprint.Sprint;

/**
 * A switch strategy for sprint information and edit scenes
 * Created by drm127 on 29/07/15.
 */
public class SprintInformationSwitchStrategy implements InformationSwitchStrategy {

    /**
     * Sets the main pane to be an instance of the Sprint Scene.
     * @param item The SaharaItem for the scene to be constructed with.
     */
    @Override
    public void switchScene(SaharaItem item) {
        if (item instanceof Sprint) {
            App.mainPane.setContent(new SprintScene((Sprint) item));
            App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);

        }
        else {
            // Bad call
        }
    }

    /**
     * Sets the main pane to be an instance of the Sprint Edit Scene.
     * @param item The SaharaItem for the scene to be constructed with.
     * @param editScene Whether the edit scene is to be shown.
     */
    @Override
    public void switchScene(SaharaItem item, boolean editScene) {
        if (item instanceof Sprint) {
            if (editScene) {
                App.mainPane.setContent(new SprintScene((Sprint) item, true));
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
