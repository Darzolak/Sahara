package seng302.group2.scenes.sceneswitch.switchStrategies.workspace;

import seng302.group2.App;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.skill.SkillScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.InformationSwitchStrategy;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.skills.Skill;

/**
 * A switch strategy for skill information and edit scenes
 * Created by Jordane on 8/06/2015.
 */
public class SkillInformationSwitchStrategy implements InformationSwitchStrategy {

    /**
     * Sets the main pane to be an instance of the Skill Scene. 
     * @param item The SaharaItem for the scene to be constructed with. 
     */
    @Override
    public void switchScene(SaharaItem item) {
        if (item instanceof Skill) {
            App.mainPane.setContent(new SkillScene((Skill) item));
            if (((Skill) item).getShortName().equals("Product Owner")) {
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);
            }
            else if (((Skill) item).getShortName().equals("Scrum Master")) {
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);
            }
            else {
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);
            }

        }
        else {
            // Bad call
        }
    }

    /**
     * Sets the main pane to be an instance of the Skill Scene.
     * @param item The SaharaItem for the scene to be constructed with.
     * @param editScene Whether the edit scene is to be shown.
     */
    @Override
    public void switchScene(SaharaItem item, boolean editScene) {
        if (item instanceof Skill) {
            if (editScene) {
                App.mainPane.setContent(new SkillScene((Skill) item, true));
                App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.EDIT);
            }
            else {
                switchScene(item);
                if (((Skill) item).getShortName().equals("Product Owner")) {
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);
                }
                else if (((Skill) item).getShortName().equals("Scrum Master")) {
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);
                }
                else {
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);
                }
            }
        }
        else {
            // Bad call
        }
    }
}
