package seng302.group2.scenes.information.person;

import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.workspace.person.Person;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A class for displaying the Person Scene
 *
 * @author crw73
 */
public class PersonScene extends TrackedTabPane {

    private Collection<SearchableTab> searchableTabs = new ArrayList<>();

    Person currentPerson;
    boolean editScene = false;

    private PersonInfoTab informationTab;
    private PersonLoggingTab loggingTab;
    private PersonEditTab editTab;


    /**
     * Basic constructor for the PersonScene.
     * @param currentPerson the person for which the information will be displayed.
     */
    public PersonScene(Person currentPerson) {
        super(ContentScene.PERSON, currentPerson);

        this.currentPerson = currentPerson;

        // Define and add the tabs
        informationTab = new PersonInfoTab(currentPerson);
        loggingTab = new PersonLoggingTab(currentPerson);

        Collections.addAll(searchableTabs, informationTab, loggingTab);
        this.getTabs().addAll(searchableTabs);  // Add the tabs to the pane
    }

    /**
     * Constructor for the PersonScene. This creates an instance of the PersonEditTab tab and displays it.
     * @param currentPerson the person who will be edited
     * @param editScene a boolean - if the scene is an edit scene
     */
    public PersonScene(Person currentPerson, boolean editScene) {
        super(ContentScene.PERSON_EDIT, currentPerson);

        this.currentPerson = currentPerson;
        this.editScene = editScene;

        // Define and add the tabs
        editTab = new PersonEditTab(currentPerson);

        Collections.addAll(searchableTabs, editTab);
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