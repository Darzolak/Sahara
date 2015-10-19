package seng302.group2.scenes.dialog;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.MainPane;
import seng302.group2.scenes.SearchResultPane;
import seng302.group2.scenes.control.CustomTextField;
import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchResultCellNode;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.information.person.PersonScene;
import seng302.group2.scenes.information.project.ProjectScene;
import seng302.group2.scenes.information.project.backlog.BacklogScene;
import seng302.group2.scenes.information.project.release.ReleaseScene;
import seng302.group2.scenes.information.project.sprint.SprintScene;
import seng302.group2.scenes.information.project.story.StoryScene;
import seng302.group2.scenes.information.role.RoleScene;
import seng302.group2.scenes.information.skill.SkillScene;
import seng302.group2.scenes.information.team.TeamScene;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.team.Team;

import java.util.*;

/**
 * Class to create a popup dialog for advanced search
 * Created by btm38 on 17/08/15.
 */
public class CreateSearchPopOver extends PopOver {


    /**
     * Displays the dialog box for advanced search
     */
    public CreateSearchPopOver() {
        this.setDetachedTitle("Search Workspace");
        this.setMinWidth(400);
        this.setMinHeight(250);
        this.setDetached(true);

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button btnSearch = new Button("Search");
        Button btnCancel = new Button("Cancel");
        buttons.spacingProperty().setValue(10);
        Insets insets = new Insets(0, 20, 0, 20);
        buttons.setPadding(insets);
        buttons.getChildren().addAll(btnCancel, btnSearch);
        btnSearch.setDisable(true);

        HBox searchByBox = new HBox();
        ToggleGroup searchGroup = new ToggleGroup();
        RadioButton normalBtn = new RadioButton("Normal");
        normalBtn.setToggleGroup(searchGroup);
        normalBtn.setSelected(true);
        RadioButton tagBtn = new RadioButton("Tags");
        tagBtn.setToggleGroup(searchGroup);
        RadioButton regexBtn = new RadioButton("Regular Expression");
        regexBtn.setToggleGroup(searchGroup);
        RadioButton complexBtn = new RadioButton("Complex Expression");
        complexBtn.setToggleGroup(searchGroup);


        searchByBox.getChildren().addAll(normalBtn, tagBtn, regexBtn, complexBtn);

        HBox sortByBox = new HBox();
        SearchableText sortByString = new SearchableText("Sort By: ");
        ToggleGroup sortGroup = new ToggleGroup();
        RadioButton treeViewButton = new RadioButton("TreeView");
        treeViewButton.setToggleGroup(sortGroup);
        treeViewButton.setSelected(true);
        RadioButton relevanceBtn = new RadioButton("Relevance");
        relevanceBtn.setToggleGroup(sortGroup);

        sortByBox.getChildren().addAll(treeViewButton, relevanceBtn);

        VBox grid = new VBox();
        Insets insets1 = new Insets(20, 20, 20, 20);
        grid.spacingProperty().setValue(10);
        grid.setPadding(insets1);

        CustomTextField searchField = new CustomTextField("Search for:");

        CheckBox workspaceSearchCheck = new CheckBox("Search entire workspace");
        workspaceSearchCheck.setSelected(true);

        SearchableText onlySearchLabel = new SearchableText("Only Search:");
        GridPane checkBoxPane = new GridPane();
        checkBoxPane.setHgap(10);
        checkBoxPane.setVgap(10);

        List<CheckBox> modelCheckBoxes = new ArrayList<>();

        CheckBox projectSearchCheck = new CheckBox("Projects");
        checkBoxPane.add(projectSearchCheck, 0, 0);

        modelCheckBoxes.add(projectSearchCheck);

        CheckBox releaseSearchCheck = new CheckBox("Releases");
        checkBoxPane.add(releaseSearchCheck, 1, 0);
        modelCheckBoxes.add(releaseSearchCheck);

        CheckBox backlogSearchCheck = new CheckBox("Backlogs");
        checkBoxPane.add(backlogSearchCheck, 2, 0);
        modelCheckBoxes.add(backlogSearchCheck);

        CheckBox storySearchCheck = new CheckBox("Stories");
        checkBoxPane.add(storySearchCheck, 0, 1);
        modelCheckBoxes.add(storySearchCheck);

        CheckBox sprintSearchCheck = new CheckBox("Sprints");
        checkBoxPane.add(sprintSearchCheck, 1, 1);
        modelCheckBoxes.add(sprintSearchCheck);

        CheckBox teamSearchCheck = new CheckBox("Teams");
        checkBoxPane.add(teamSearchCheck, 2, 1);
        modelCheckBoxes.add(teamSearchCheck);

        CheckBox personSearchCheck = new CheckBox("People");
        checkBoxPane.add(personSearchCheck, 0, 2);
        modelCheckBoxes.add(personSearchCheck);

        CheckBox roleSearchCheck = new CheckBox("Roles");
        checkBoxPane.add(roleSearchCheck, 1, 2);
        modelCheckBoxes.add(roleSearchCheck);

        CheckBox skillSearchCheck = new CheckBox("Skills");
        checkBoxPane.add(skillSearchCheck, 2, 2);
        modelCheckBoxes.add(skillSearchCheck);

        grid.getChildren().addAll(searchField, searchByBox, new Separator(), workspaceSearchCheck, onlySearchLabel,
                checkBoxPane, new Separator(), sortByString, sortByBox, buttons);

        //Add grid of controls to dialog
        this.setContentNode(grid);

        EventHandler eh = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (event.getSource() instanceof CheckBox) {
                    CheckBox chk = (CheckBox) event.getSource();
                    if ("Search entire workspace".equals(chk.getText())
                            && chk.isSelected()) {
                        for (CheckBox box : modelCheckBoxes) {
                            box.setSelected(false);
                        }
                    }
                    else if (chk.isSelected()) {
                        workspaceSearchCheck.setSelected(false);
                    }
                    else if (!chk.isSelected()) {
                        boolean anyTicked = false;
                        for (CheckBox box : modelCheckBoxes) {
                            if (box.isSelected()) {
                                anyTicked = true;
                            }
                        }
                        if (!anyTicked) {
                            workspaceSearchCheck.setSelected(true);
                        }
                    }
                }
            }
        };

        workspaceSearchCheck.setOnAction(eh);
        for (CheckBox box : modelCheckBoxes) {
            box.setOnAction(eh);
        }


        searchField.getTextField().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals("")) {
                    btnSearch.setDisable(true);
                }
                else {
                    btnSearch.setDisable(false);
                }
            }
        });

        btnSearch.setOnAction(event -> {
            if (normalBtn.isSelected()) {
                this.hide();
                String searchText = searchField.getText();
                List<String> checkedItems = getCheckedItems(workspaceSearchCheck,
                        modelCheckBoxes);
                PopOver resultsPopOver = new SearchResultPane(runSearch(checkedItems, searchText,
                        SearchType.NORMAL, treeViewButton.isSelected()), searchText, SearchType.NORMAL);

                resultsPopOver.show(App.mainStage);
            }
            else if (regexBtn.isSelected()) {
                this.hide();
                String searchText = searchField.getText();
                List<String> checkedItems = getCheckedItems(workspaceSearchCheck,
                        modelCheckBoxes);
                PopOver resultsPopOver = new SearchResultPane(runSearch(checkedItems, searchText,
                        SearchType.REGEX, treeViewButton.isSelected()), searchText, SearchType.REGEX);
                resultsPopOver.show(App.mainStage);
            }
            else if (complexBtn.isSelected()) {
                this.hide();
                String searchText = searchField.getText();
                List<String> checkedItems = getCheckedItems(workspaceSearchCheck,
                        modelCheckBoxes);
                PopOver resultsPopOver = new SearchResultPane(runSearch(checkedItems, searchText,
                        SearchType.COMPLEX, treeViewButton.isSelected()), searchText, SearchType.COMPLEX);
                resultsPopOver.show(App.mainStage);
            }
            else if (tagBtn.isSelected()) {
                this.hide();
                String searchText = searchField.getText();
                List<String> checkedItems = getCheckedItems(workspaceSearchCheck,
                        modelCheckBoxes);
                PopOver resultsPopOver = new SearchResultPane(runSearch(checkedItems, searchText,
                        SearchType.TAGS, treeViewButton.isSelected()), searchText, SearchType.TAGS);
                resultsPopOver.show(App.mainStage);

            }

        });

        btnCancel.setOnAction(event -> {
            Global.advancedSearchExists = false;
            this.hide();
        });

        this.setOnHiding(event -> {
            MainPane.getToolBar().search("");
            Global.advancedSearchExists = false;
        });


    }


    /**
     * Gets the checked item.
     *
     * @param workspaceSearchCheck The check box for searching through the whole workspace
     * @param modelCheckBoxes      List of check boxes in the search dialog
     * @return a list of checked items
     */
    private static List<String> getCheckedItems(CheckBox workspaceSearchCheck,
                                                  List<CheckBox> modelCheckBoxes) {
        List<String> checkedItems = new ArrayList<>();

        if (workspaceSearchCheck.isSelected()) {
            checkedItems.add("Projects");
            checkedItems.add("Releases");
            checkedItems.add("Backlogs");
            checkedItems.add("Stories");
            checkedItems.add("Sprints");
            checkedItems.add("Teams");
            checkedItems.add("People");
            checkedItems.add("Roles");
            checkedItems.add("Skills");

        }
        else {
            for (CheckBox checkBox : modelCheckBoxes) {
                if (checkBox.isSelected()) {
                    checkedItems.add(checkBox.getText());
                }
            }
        }
        return checkedItems;
    }

    private static Map sortByValue(Map map) {
        List list = new ArrayList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        Map result = new HashMap<>();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Runs the search through the checked items and constructs the SearchResultCellNode to be added to the search
     * result pane
     *
     * @param checkedItems items that are being searched through
     * @param searchText   the text to be searched
     * @param searchType   the type of the search
     * @param sortByTree   wether to sort via the tree or not
     * @return a list of SearchResultCellNode
     */
    public List<SearchResultCellNode> runSearch(List<String> checkedItems, String searchText, SearchType searchType,
                                                boolean sortByTree) {

        Map<SearchResultCellNode, Integer> results = new HashMap<>();

        for (String item : checkedItems) {
            if (item.equals("Projects")) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    TrackedTabPane scene = new ProjectScene(proj);
                    Map<SearchableTab, Integer> map = scene.advancedQuery(searchText, searchType);

                    for (SearchableTab tab : map.keySet()) {
                        SearchResultCellNode searchResult = new SearchResultCellNode(proj, searchText, tab,
                                map.get(tab), scene);
                        if (!(searchResult == null)) {
                            results.put(searchResult, map.get(tab));
                        }
                    }
                }
            }
            else if (item.equals("Releases")) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    for (Release release : proj.getReleases()) {
                        TrackedTabPane scene = new ReleaseScene(release);
                        Map<SearchableTab, Integer> searchResults = scene.advancedQuery(searchText, searchType);
                        for (SearchableTab tab : searchResults.keySet()) {
                            SearchResultCellNode searchResult = new SearchResultCellNode(release, searchText,
                                    tab, searchResults.get(tab), scene);
                            if (!(searchResult == null)) {
                                results.put(searchResult, searchResults.get(tab));
                            }
                        }
                    }
                }
            }
            else if (item.equals("Backlogs")) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    for (Backlog backlog : proj.getBacklogs()) {
                        TrackedTabPane scene = new BacklogScene(backlog);
                        Map<SearchableTab, Integer> searchResults = scene.advancedQuery(searchText, searchType);
                        for (SearchableTab tab : searchResults.keySet()) {
                            SearchResultCellNode searchResult = new SearchResultCellNode(backlog, searchText,
                                    tab, searchResults.get(tab), scene);
                            if (!(searchResult == null)) {
                                results.put(searchResult, searchResults.get(tab));
                            }
                        }
                    }
                }
            }
            else if (item.equals("Stories")) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    for (Backlog backlog : proj.getBacklogs()) {
                        for (Story story : backlog.getStories()) {
                            TrackedTabPane scene = new StoryScene(story);
                            Map<SearchableTab, Integer> searchResults = scene.advancedQuery(searchText, searchType);
                            for (SearchableTab tab : searchResults.keySet()) {
                                SearchResultCellNode searchResult = new SearchResultCellNode(story, searchText,
                                        tab, searchResults.get(tab), scene);
                                if (!(searchResult == null)) {
                                    results.put(searchResult, searchResults.get(tab));
                                }
                            }
                        }
                    }
                }
            }
            else if (item.equals("Sprints")) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    for (Sprint sprint : proj.getSprints()) {
                        TrackedTabPane scene = new SprintScene(sprint);
                        Map<SearchableTab, Integer> searchResults = scene.advancedQuery(searchText, searchType);
                        for (SearchableTab tab : searchResults.keySet()) {
                            SearchResultCellNode searchResult = new SearchResultCellNode(sprint, searchText,
                                    tab, searchResults.get(tab), scene);
                            if (!(searchResult == null)) {
                                results.put(searchResult, searchResults.get(tab));
                            }
                        }
                    }
                }
            }
            else if (item.equals("Teams")) {
                for (Team team : Global.currentWorkspace.getTeams()) {
                    TrackedTabPane scene = new TeamScene(team);
                    Map<SearchableTab, Integer> searchResults = scene.advancedQuery(searchText, searchType);
                    for (SearchableTab tab : searchResults.keySet()) {
                        SearchResultCellNode searchResult = new SearchResultCellNode(team, searchText, tab,
                                searchResults.get(tab), scene);
                        if (!(searchResult == null)) {
                            results.put(searchResult, searchResults.get(tab));
                        }
                    }
                }
            }
            else if (item.equals("People")) {
                for (Person person : Global.currentWorkspace.getPeople()) {
                    TrackedTabPane scene = new PersonScene(person);
                    Map<SearchableTab, Integer> searchResults = scene.advancedQuery(searchText, searchType);
                    for (SearchableTab tab : searchResults.keySet()) {
                        SearchResultCellNode searchResult = new SearchResultCellNode(person, searchText,
                                tab, searchResults.get(tab), scene);
                        if (!(searchResult == null)) {
                            results.put(searchResult, searchResults.get(tab));
                        }
                    }
                }
            }
            else if (item.equals("Roles")) {
                for (Role role : Global.currentWorkspace.getRoles()) {
                    TrackedTabPane scene = new RoleScene(role);
                    Map<SearchableTab, Integer> searchResults = scene.advancedQuery(searchText, searchType);
                    for (SearchableTab tab : searchResults.keySet()) {
                        SearchResultCellNode searchResult = new SearchResultCellNode(role, searchText,
                                tab, searchResults.get(tab), scene);
                        if (!(searchResult == null)) {
                            results.put(searchResult, searchResults.get(tab));
                        }
                    }
                }
            }
            else if (item.equals("Skills")) {
                for (Skill skill : Global.currentWorkspace.getSkills()) {
                    TrackedTabPane scene = new SkillScene(skill);
                    Map<SearchableTab, Integer> searchResults = scene.advancedQuery(searchText, searchType);
                    for (SearchableTab tab : searchResults.keySet()) {
                        SearchResultCellNode searchResult = new SearchResultCellNode(skill, searchText,
                                tab, searchResults.get(tab), scene);
                        if (!(searchResult == null)) {
                            results.put(searchResult, searchResults.get(tab));
                        }
                    }
                }
            }
        }

        List<SearchResultCellNode> returnedList;
        if (sortByTree) {
            returnedList = new ArrayList<>();
            returnedList.addAll(results.keySet());

        }
        else {
            Map<SearchResultCellNode, Integer> searchResults = sortByValue(results);
            returnedList = new ArrayList<>();
            returnedList.addAll(searchResults.keySet());

        }

        return returnedList;
    }

}
