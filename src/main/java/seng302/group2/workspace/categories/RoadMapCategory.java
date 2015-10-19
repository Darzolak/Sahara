/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.workspace.categories;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.dialog.CreateRoadMapDialog;
import seng302.group2.scenes.sceneswitch.switchStrategies.category.RoadMapCategoryCategorySwitchStrategy;
import seng302.group2.workspace.SaharaItem;

import java.util.HashSet;
import java.util.Set;

/**
 * Category model for RoadMaps
 * @author crw73
 */
public class RoadMapCategory extends Category {
    
    /**
     * Constructor for the RoadMapCategory class.
     */
    public RoadMapCategory() {
        super("RoadMaps");
        setCategorySwitchStrategy(new RoadMapCategoryCategorySwitchStrategy());
    }

    /**
     * Method for creating an XML element for the RoadMap within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        return null;
    }

    /**
     * Returns the items held by the RoadMapCategory, blank as the roadMap category has no child items.
     * @return a blank hash set
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        return new HashSet<>();
    }

    /**
     * Returns a list of all the RoadMap in the workspace.
     * @return An ObservableList of the RoadMap in the workspace.
     */
    @Override
    public ObservableList getChildren() {
        return Global.currentWorkspace.getRoadMaps();
    }

    /**
     * Shows the RoadMap creation dialog.
     */
    @Override
    public void showCreationDialog() {
        javafx.scene.control.Dialog creationDialog = new CreateRoadMapDialog();
        creationDialog.show();
    }
}
