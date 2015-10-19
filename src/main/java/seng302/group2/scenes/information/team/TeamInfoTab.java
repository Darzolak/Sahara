package seng302.group2.scenes.information.team;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.Global;
import seng302.group2.scenes.control.CustomInfoLabel;
import seng302.group2.scenes.control.FilteredListView;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.team.Team;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;


/**
 * The workspace information tab
 * Created by jml168 on 11/05/15.
 */
public class TeamInfoTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();

    Team currentTeam;

    /**
     * Constructor for team basic information tab
     *
     * @param currentTeam currently selected team
     */
    public TeamInfoTab(Team currentTeam) {
        this.currentTeam = currentTeam;
        construct();
    }


    /**
    * @param currentTeam It is the team to be converted.
    * @return returns a List of the People converted to a List of their Shortnames
    */
    public static ObservableList<String> personRoleToString(ObservableList<Person> currentTeam) {
        ObservableList<String> currentStringTeam = observableArrayList();

        for (Person person : currentTeam) {
            if (person.getRole() != null) {
                if (person.getRole().toString().equals("Product Owner")) {
                    currentStringTeam.add(person.toString() + " (" + person.getRole() + ")");
                }
                else if (person.getRole().toString().equals("Scrum Master")) {
                    currentStringTeam.add(person.toString() + " (" + person.getRole() + ")");
                }
                else if (person.getRole().toString().equals("Development Team Member")) {
                    currentStringTeam.add(person.toString() + " (" + person.getRole() + ")");
                }
            }
            else {
                currentStringTeam.add(person.toString());
            }
        }

        return currentStringTeam;
    }


    /**
    * Sorts a list of people based on their roles on a team, PO first, SM second, Dev third
    * then lastly goes people without a role.
    * @param currentTeam The team to sort by.
    * @return Returns sorted list.
    */
    public static ObservableList<Person> sortListView(ObservableList<Person> currentTeam) {
        ObservableList<Person> teamList = observableArrayList();

        for (Person person : currentTeam) {
            if (person.getRole() != null) {
                if (person.getRole().toString().equals("Product Owner")) {
                    teamList.add(person);
                }
                else if (person.getRole().toString().equals("Scrum Master")) {
                    teamList.add(person);
                }
                else if (person.getRole().toString().equals("Development Team Member")) {
                    teamList.add(person);
                }
            }
            else {
                teamList.add(person);
            }
        }

        return teamList;
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

        // Create Controls
        SearchableTitle title = new SearchableTitle(currentTeam.getShortName());
        TagLabel teamTags = new TagLabel(currentTeam.getTags());
        CustomInfoLabel desc = new CustomInfoLabel("Team Description: ", currentTeam.getDescription());
        CustomInfoLabel listViewLabel = new CustomInfoLabel("", "");
        CustomInfoLabel devLabel = new CustomInfoLabel("Developers: ", "");

        ObservableList<Person> personList = currentTeam.getPeople();
        FilteredListView teamsPeopleBox = new FilteredListView<>(personList, "people");
        SearchableListView<Person> teamsPeoplelist = teamsPeopleBox.getListView();
        teamsPeopleBox.setPrefHeight(192);
        teamsPeoplelist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        teamsPeopleBox.setMaxWidth(275);

        Separator separator = new Separator();

        basicInfoPane.getChildren().addAll(
                title,
                teamTags,
                desc,
                separator
        );

        Collections.addAll(searchControls,
                title,
                teamTags,
                desc
        );


        if (!currentTeam.isUnassignedTeam()) {
            CustomInfoLabel sprintText = new CustomInfoLabel("Sprint Velocity Points Per Week: ", "");
            basicInfoPane.getChildren().add(sprintText);
            if (currentTeam.getProject() != null) {
                for (Sprint sprint : currentTeam.getProject().getSprints()) {
                    if (sprint.getTeam() == currentTeam) {
                        Double velocity = sprint.getPointsPerDay() * 7;
                        DecimalFormat df = new DecimalFormat("#.##");
                        String velocity_ = df.format(velocity);
                        CustomInfoLabel sprintLabel = new CustomInfoLabel(
                                "   " + sprint.getGoal() + ": ", velocity_);
                        basicInfoPane.getChildren().add(sprintLabel);
                        Collections.addAll(searchControls, sprintLabel, sprintText);
                    }
                }
            }
        }



        if (currentTeam.isUnassignedTeam()) {
            for (SaharaItem person : Global.currentWorkspace.getPeople()) {
                Person castedPerson = (Person) person;
                if (castedPerson.getTeam() == null) {
                    currentTeam.add(castedPerson);
                }
            }
        }

        // Add a PO, SM,and Team Member label if the team is not the unassigned team.
        if (!currentTeam.isUnassignedTeam()) {
            CustomInfoLabel poLabel = new CustomInfoLabel("Product Owner: ", "");
            CustomInfoLabel smLabel = new CustomInfoLabel("Scrum Master: ", "");
            listViewLabel.setLabel("Team Members: ");

            if (currentTeam.getProductOwner() != null) {
                poLabel.setValue(currentTeam.getProductOwner().toString());
            }

            if (currentTeam.getScrumMaster() != null) {
                smLabel.setValue(currentTeam.getScrumMaster().toString());
            }

            basicInfoPane.getChildren().addAll(poLabel, smLabel);
            Collections.addAll(searchControls, poLabel, smLabel);

            ObservableList<Person> devList = currentTeam.getDevs();
            FilteredListView devsListBox = new FilteredListView<>(devList, "devs");
            SearchableListView<Person> devsPeopleList = devsListBox.getListView();
            devsListBox.setPrefHeight(192);
            devsPeopleList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            devsListBox.setMaxWidth(275);

            basicInfoPane.getChildren().addAll(listViewLabel, teamsPeopleBox, devLabel, devsListBox);
            Collections.addAll(searchControls,listViewLabel, teamsPeoplelist, devLabel, devsListBox);
        }

        else {
            listViewLabel.setLabel("Unassigned People: ");
            basicInfoPane.getChildren().addAll(listViewLabel, teamsPeopleBox);
            Collections.addAll(searchControls,listViewLabel, teamsPeoplelist);
        }

    }

    /**
     * Switches to the edit scene
     */
    public void edit() {
        if (!currentTeam.isUnassignedTeam()) {
            currentTeam.switchToInfoScene(true);
        }
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Team Info Tab";
    }
}
