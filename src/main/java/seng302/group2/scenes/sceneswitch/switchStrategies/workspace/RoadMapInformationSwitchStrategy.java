package seng302.group2.scenes.sceneswitch.switchStrategies.workspace;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.roadMap.RoadMapScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.InformationSwitchStrategy;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.roadMap.RoadMap;

/**
 * A switch strategy for road map information and edit scenes
 * Created by cvs20 on 11/09/15.
 */
public class RoadMapInformationSwitchStrategy implements InformationSwitchStrategy {

    /**
     * Sets the main pane to be an instance of the RoadMap Scene.
     * @param item The SaharaItem for the scene to be constructed with.
     */
    @Override
    public void switchScene(SaharaItem item) {
        if (item instanceof RoadMap) {
            App.mainPane.setContent(new RoadMapScene((RoadMap) item));
            App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);

        }
        else {
            // Bad call
        }
    }

    /**
     * Sets the main pane to be an instance of the RoadMap Scene.
     * @param item The SaharaItem for the scene to be constructed with.
     * @param editScene Whether the edit scene is to be shown.
     */
    @Override
    public void switchScene(SaharaItem item, boolean editScene) {
        if (item instanceof RoadMap) {
            if (editScene) {
                App.mainPane.setContent(new RoadMapScene((RoadMap) item, true));
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
