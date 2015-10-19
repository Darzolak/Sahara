package seng302.group2.scenes.sceneswitch.switchStrategies.workspace;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.role.RoleScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.InformationSwitchStrategy;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.role.Role;

/**
 * An switch strategy for people information and edit scenes
 * Created by Jordane on 8/06/2015.
 */
public class RoleInformationSwitchStrategy implements InformationSwitchStrategy {

    /**
     * Sets the main pane to be an instance of the Role Scene. 
     * @param item The SaharaItem for the scene to be constructed with. 
     */
    @Override
    public void switchScene(SaharaItem item) {
        if (item instanceof Role) {
            App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);
            App.mainPane.setContent(new RoleScene((Role) item));

        }
    }

    /**
     * Sets the main pane to be an instance of the Role Edit Scene.
     * @param item The SaharaItem for the scene to be constructed with.
     * @param editScene Whether the edit scene is to be shown.
     */
    @Override
    public void switchScene(SaharaItem item, boolean editScene) {
        if (item instanceof Role) {
            if (!editScene) {
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);
                switchScene(item);
            }
        }
    }
}
