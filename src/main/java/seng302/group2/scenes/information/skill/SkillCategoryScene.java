package seng302.group2.scenes.information.skill;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collections;

/**
 * A class for displaying the skill category. Contains information
 * about all the skills in the workspace.
 *
 * Created by btm38 on 14/07/15.
 */
public class SkillCategoryScene extends TrackedTabPane {

    Workspace currentWorkspace;
    SearchableTab categoryTab;

    /**
     * Constructor for the SkillCategoryScene class. Creates a tab
     * of SkillCategoryTab and displays it.
     * @param currentWorkspace the current workspace
     */
    public SkillCategoryScene(Workspace currentWorkspace) {
        super(TrackedTabPane.ContentScene.SKILL_CATEGORY, currentWorkspace);

        this.currentWorkspace = currentWorkspace;

        // Define and add the tabs
        categoryTab = new SkillCategoryTab(currentWorkspace);
        updateAllTabs();
        Collections.addAll(getSearchableTabs(), categoryTab);

        this.getTabs().addAll(getSearchableTabs());
    }

    public void done() {}
    public void edit() {}
    public void cancel() {}
}