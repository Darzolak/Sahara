package seng302.group2.scenes.control;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import seng302.group2.scenes.control.search.SearchableScene;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.SaharaItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Tab Pane extension that keeps a tracked record of the last open tabs for each of its extending
 * classes.
 * Created by Jordane on 10/05/2015.
 */
public abstract class TrackedTabPane extends SearchableScene {


    /**
     * A map to keep of each of the last selected tabs in the scenes
     */
    private static Map<SaharaItem, Map<ContentScene, Integer>> contentTabs = new LinkedHashMap<>();
    Collection<SearchableTab> searchableTabs = new ArrayList<>();
    private ContentScene scene;
    private SaharaItem item;

    /**
     * Constructor that takes the content scene type to use for storing tracking information
     *
     * @param scene The scene of the tracked tab pane
     * @param item  The item that the tabs track to
     */
    public TrackedTabPane(ContentScene scene, SaharaItem item) {
        this.item = item;
        this.scene = scene;
        this.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        openLastTab();

        this.getSelectionModel().selectedItemProperty().addListener((event) -> {
            contentTabs.put(item, new LinkedHashMap<>());
            Map<ContentScene, Integer> itemMap = contentTabs.get(item);
            itemMap.put(scene, this.getSelectionModel().getSelectedIndex());
        });
    }

    /**
     * Clears the content tabs.
     */
    public static void clearHistory() {
        contentTabs.clear();
    }

    public Collection<SearchableTab> getSearchableTabs() {
        return searchableTabs;
    }

    /**
     * Opens the last tab stored for the type of scene
     */
    public void openLastTab() {
        Integer lastTab = 0;
        if (scene != null && item != null && contentTabs.get(item) != null) {
            //System.out.println("opening last tab from " + scene.toString());
            lastTab = contentTabs.get(item).get(scene);
        }
        if (lastTab != null) {
            this.getSelectionModel().select(lastTab);
        }
    }

    public void select(Tab tab) {
        for (int i = 0; i < this.getSearchableTabs().size(); i++) {
            if (((ArrayList) this.getSearchableTabs()).get(i) == tab) {
                contentTabs.put(item, new LinkedHashMap<>());
                Map<ContentScene, Integer> itemMap = contentTabs.get(item);
                itemMap.put(scene, i);
            }
        }

//        App.refreshMainScene();
    }

    /**
     * Updates each of the tabs in the scene, except for the current tab
     */
    public void updateTabs() {
        for (SearchableTab tab : getSearchableTabs()) {
            if (!tab.isSelected()) {
                tab.getSearchableControls().clear();  // These should be re-added on construction
                tab.construct();
            }
        }
    }

    /**
     * Updates each of the tabs in the scene, including the current tab
     */
    public void updateAllTabs() {
        for (SearchableTab tab : getSearchableTabs()) {
            tab.getSearchableControls().clear();  // These should be re-added on construction
            tab.construct();
        }
    }


    /**
     * An enumeration of scenes in the project
     */
    public enum ContentScene {
        PERSON,
        PERSON_EDIT,
        PERSON_CATEGORY,
        PROJECT,
        PROJECT_EDIT,
        PROJECT_CATEGORY,
        WORKSPACE,
        WORKSPACE_EDIT,
        TEAM,
        TEAM_EDIT,
        TEAM_CATEGORY,
        ROLE,
        ROLE_EDIT,
        ROLE_CATEGORY,
        SKILL,
        SKILL_EDIT,
        SKILL_CATEGORY,
        RELEASE,
        RELEASE_EDIT,
        RELEASE_CATEGORY,
        SPRINT,
        SPRINT_EDIT,
        SPRINT_CATEGORY,
        STORY,
        STORY_EDIT,
        STORY_CATEGORY,
        BACKLOG,
        BACKLOG_EDIT,
        BACKLOG_CATEGORY,
        TASK,
        TASK_EDIT,
        TASK_CATEGORY,
        ROADMAP,
        ROADMAP_EDIT,
        ROADMAP_CATEGORY
    }

}