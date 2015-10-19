/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.scenes.information.role;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.role.Role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A class for displaying the role scene
 *
 * @author jml168
 */
public class RoleScene extends TrackedTabPane {

    Collection<SearchableTab> searchableTabs = new
            ArrayList<>();

    Role currentRole;
    RoleInfoTab informationTab;
    
    /**
     * Constructor for the Role Scene. Creates an instance of the Role Info Tab and displays it.
     * 
     * @param currentRole The currently selected Role. 
     */
    public RoleScene(Role currentRole) {
        super(ContentScene.ROLE, currentRole);

        this.currentRole = currentRole;

        // Define and add the tabs
        informationTab = new RoleInfoTab(currentRole);
        updateAllTabs();

        Collections.addAll(searchableTabs, informationTab);
        this.getTabs().addAll(searchableTabs);  // Add the tabs to the pane
    }


    /**
     * Gets all the SearchableTabs on this scene
     * @return collection of SearchableTabs
     */
    @Override
    public Collection<SearchableTab> getSearchableTabs() {
        return searchableTabs;
    }

    /**
     * Calls the done functionality behind the done button on the edit tab
     */
    @Override
    public void done() {
    }

    /**
     * Calls the functionality behind the edit button on the info tab
     */
    @Override
    public void edit() {
    }

    /**
     * Calls the functionality behind the edit button on the edit tab
     */
    @Override
    public void cancel() {
    }

}
