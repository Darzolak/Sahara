/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.scenes.sceneswitch.switchStrategies.category;

import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.roadMap.RoadMapCategoryScene;
import seng302.group2.scenes.sceneswitch.switchStrategies.CategorySwitchStrategy;

/**
 * RoadMap category Switch Strategy
 * @author crw73
 */
public class RoadMapCategoryCategorySwitchStrategy implements CategorySwitchStrategy {
    /**
     * Sets the Main Pane to be an instance of the RoadMapCategoryScene.
     */
    @Override
    public void switchScene() {
        App.mainPane.setContent(new RoadMapCategoryScene(Global.currentWorkspace));
        App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.OTHER);

    }
}
