package seng302.group2.scenes.information.person;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.Global;
import seng302.group2.scenes.control.CustomComboBox;
import seng302.group2.scenes.control.CustomInfoLabel;
import seng302.group2.scenes.control.FilteredListView;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.skills.Skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;


/**
 * The person information tab
 * Created by jml168 on 11/05/15.
 */
public class PersonInfoTab extends SearchableTab {

    private List<SearchableControl> searchControls = new ArrayList<>();
    private Person currentPerson;

    /**
     * A tab to display information on a selected tasks.
     * @param currentPerson The person to display the information about
     */
    public PersonInfoTab(Person currentPerson) {
        this.currentPerson = currentPerson;
        construct();
    }

    /**
     * Gets all the searchable controls on this tab.
     * @return a collection of all the searchable controls on this tab.
     */
    @Override
    public Collection<SearchableControl> getSearchableControls() {
        return searchControls;
    }

    @Override
    public void construct() {
        // Tab settings
        this.setText("Basic Information");
        Pane basicInfoPane = new VBox(10);
        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

        // Create controls
        HBox listViewHBox = new HBox(10);

        SearchableText title = new SearchableTitle(currentPerson.getFirstName() + " " + currentPerson.getLastName());

        VBox skillVBox = new VBox(20);
        CustomInfoLabel skill  = new CustomInfoLabel("Skills: ", "");
        FilteredListView<Skill> personFilteredListView = new FilteredListView<>(currentPerson.getSkills(), "skills");
        SearchableListView personSkillsBox = personFilteredListView.getListView();
        personFilteredListView.setPrefHeight(192);
        personSkillsBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        skillVBox.getChildren().addAll(skill, personFilteredListView);

        VBox taskVBox = new VBox(10);
        HBox titleAndCombo = new HBox(10);
        ObservableList<Task.TASKSTATE> taskStates = observableArrayList(Task.TASKSTATE.values());

        CustomComboBox<Object> filterComboBox = new CustomComboBox<>("Tasks: ");
        filterComboBox.addToComboBox("All");

        for (Task.TASKSTATE state : taskStates) {
            filterComboBox.addToComboBox(state);
        }


        ArrayList<Story> storyList = new ArrayList<>();
        if (currentPerson.getTeam() != Global.getUnassignedTeam() && currentPerson.getTeam().getProject() != null) {
            storyList.addAll(currentPerson.getTeam().getProject().getAllStories());
        }

        ArrayList<Task> taskList = new ArrayList<>();
        ObservableList<Task> filteredList = observableArrayList();
        for (Story story : storyList) {
            for (Task task : story.getTasks()) {
                if (task.getAssignee() == currentPerson) {
                    taskList.add(task);
                }
            }
        }
        FilteredListView<Task> taskFilteredListView = new FilteredListView<>(filteredList, "tasks");
        SearchableListView taskBox = taskFilteredListView.getListView();
        taskFilteredListView.setPrefHeight(192);
        taskFilteredListView.setPrefWidth(275);
        taskBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        titleAndCombo.getChildren().addAll(filterComboBox);
        taskVBox.getChildren().addAll(titleAndCombo, taskFilteredListView);
        listViewHBox.getChildren().addAll(skillVBox, taskVBox);

        CustomInfoLabel shortName = new CustomInfoLabel("Short Name: ", currentPerson.getShortName());
        TagLabel personTags = new TagLabel(currentPerson.getTags());
        CustomInfoLabel emailAddress = new CustomInfoLabel("Email Address: ", currentPerson.getEmail());
        CustomInfoLabel birthDate = new CustomInfoLabel("Birth Date: ", currentPerson.getDateString());
        CustomInfoLabel desc = new CustomInfoLabel("Person Description: ", currentPerson.getDescription());
        CustomInfoLabel team = new CustomInfoLabel("Team: ", currentPerson.getTeamName());
        String roleString = currentPerson.getRole() == null ? "" : currentPerson.getRole().toString();
        CustomInfoLabel role = new CustomInfoLabel("Role: ", roleString);

        final Separator separator = new Separator();

        filterComboBox.getComboBox().valueProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
                filteredList.clear();
                if (newValue == "All") {
                    for (Task task : taskList) {
                        filteredList.add(task);
                    }
                }
                else {
                    for (Task task : taskList) {
                        if (task.getState() == newValue) {
                            filteredList.add(task);
                        }
                    }
                }
            }
        });

        filterComboBox.getComboBox().setValue(filterComboBox.getComboBox().getItems().get(0));


        // Add items to pane & search collection
        basicInfoPane.getChildren().addAll(
                title,
                shortName,
                personTags,
                emailAddress,
                birthDate,
                desc,
                team,
                role,
                separator,
                listViewHBox);

        Collections.addAll(searchControls,
                title,
                personTags,
                shortName,
                emailAddress,
                birthDate,
                desc,
                team,
                role,
                skill,
                personFilteredListView,
                taskFilteredListView
        );
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Person Info Tab";
    }

    /**
     * Switches to the edit scene
     */
    public void edit() {
        currentPerson.switchToInfoScene(true);
    }

}
