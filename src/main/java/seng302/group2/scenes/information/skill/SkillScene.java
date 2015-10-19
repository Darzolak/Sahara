package seng302.group2.scenes.information.skill;

import javafx.scene.control.Tab;
import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.skills.Skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A class for displaying the skill scene
 *
 * @author jml168
 */
public class SkillScene extends TrackedTabPane {

    Skill currentSkill;
    boolean editScene = false;

    SkillInfoTab informationTab;
    SkillEditTab editTab;

    Collection<SearchableTab> searchableTabs = new ArrayList<>();

    /**
     * Constructor for the SkillScene class. Displays an instance of SkillInfoTab.
     * @param currentSkill the current skill for which information will be displayed
     */
    public SkillScene(Skill currentSkill) {
        super(ContentScene.SKILL, currentSkill);

        this.currentSkill = currentSkill;

        // Define and add the tabs
        informationTab = new SkillInfoTab(currentSkill);

        //updateAllTabs();

        Collections.addAll(searchableTabs, informationTab);
        this.getTabs().addAll(searchableTabs);  // Add the tabs to the pane
    }


    /**
     * Constructor for the SkillScene class. This creates an instance of the SkillEditTab tab and displays it.
     * @param currentSkill the skill which will be edited
     * @param editScene a boolean - if the scene is an edit scene
     */
    public SkillScene(Skill currentSkill, boolean editScene) {
        super(ContentScene.SKILL_EDIT, currentSkill);

        this.currentSkill = currentSkill;
        this.editScene = editScene;

        // Define and add the tabs
        editTab = new SkillEditTab(currentSkill);

        Collections.addAll(searchableTabs, editTab);

        this.getTabs().addAll(searchableTabs);  // Add the tabs to the pane
    }

    public Tab getCurrentTab() {
        return this.getSelectionModel().getSelectedItem();
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
        if (getSelectionModel().getSelectedItem() == editTab) {
            editTab.done();
        }
    }

    /**
     * Calls the functionality behind the edit button on the info tab
     */
    @Override
    public void edit() {
        if (getSelectionModel().getSelectedItem() == informationTab) {
            informationTab.edit();
        }
    }

    /**
     * Calls the functionality behind the edit button on the edit tab
     */
    @Override
    public void cancel() {
        if (getSelectionModel().getSelectedItem() == editTab) {
            editTab.cancel();
        }
    }

}
