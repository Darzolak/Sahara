package seng302.group2.scenes.sceneswitch.switchStrategies.workspace;

import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.team.TeamScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.InformationSwitchStrategy;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.team.Team;

/**
 * An switch strategy for people information and edit scenes
 * Created by Jordane on 8/06/2015.
 */
public class TeamInformationSwitchStrategy implements InformationSwitchStrategy {

    /**
     * Sets the main pane to be an instance of the Team Scene. 
     * @param item The SaharaItem for the scene to be constructed with. 
     */
    @Override
    public void switchScene(SaharaItem item) {
        if (item instanceof Team) {
            if (item == Global.getUnassignedTeam()) {
                App.mainPane.setContent(new TeamScene((Team) item));
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);
            }
            else {
                App.mainPane.setContent(new TeamScene((Team) item));
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);
            }
        }
        else {
            // Bad call
        }
    }

    /**
     * Sets the main pane to be an instance of the Team Scene.
     * @param item The SaharaItem for the scene to be constructed with.
     * @param editScene Whether the edit scene is to be shown.
     */
    @Override
    public void switchScene(SaharaItem item, boolean editScene) {
        if (item instanceof Team) {
            if (editScene) {
                App.mainPane.setContent(new TeamScene((Team) item, true));
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
