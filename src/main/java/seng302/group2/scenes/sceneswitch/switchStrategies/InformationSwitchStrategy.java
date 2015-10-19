package seng302.group2.scenes.sceneswitch.switchStrategies;

import seng302.group2.workspace.SaharaItem;

/**
 * An interface for information switching strategies
 * Created by jml168 on 8/06/15.
 */
public interface InformationSwitchStrategy {
    void switchScene(SaharaItem item);
    void switchScene(SaharaItem item, boolean editScene);
}
