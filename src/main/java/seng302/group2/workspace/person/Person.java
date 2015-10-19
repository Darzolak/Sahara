/*
 * SENG302 Group 2
 */
package seng302.group2.workspace.person;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.PersonInformationSwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A basic class to represent a Person in the real world.
 *
 * @author crw73
 */
public class Person extends SaharaItem implements Serializable, Comparable<Person> {
    private String shortName;
    private String firstName;
    private String lastName;
    private String email;
    private String description;
    private LocalDate birthDate = null;
    private transient ObservableList<Skill> skills = observableArrayList();
    private List<Skill> serializableSkills = new ArrayList<>();
    private Team team;
    private Role role;
    private final SimpleBooleanProperty selected = new SimpleBooleanProperty(false);

    /**
     * Basic Person constructor
     */
    public Person() {
        super("Untitled Person");
        this.shortName = "Untitled Person";
        this.firstName = "firstName";
        this.lastName = "lastName";
        this.email = "";
        this.description = "";

        for (SaharaItem team : Global.currentWorkspace.getTeams()) {
            Team castedTeam = (Team) team;
            if (castedTeam.isUnassignedTeam()) {
                this.team = castedTeam;
                break; // Only one unassigned team
            }
        }

        this.birthDate = null;

        setInformationSwitchStrategy(new PersonInformationSwitchStrategy());
    }


    /**
     * Basic person constructor with all fields
     *
     * @param shortName   A unique short name to identify a Person
     * @param firstName   The first name of the Person
     * @param lastName    The last name of the Person
     * @param email       The email of the Person
     * @param birthDate   The date of birth of the Person
     * @param description A description of the Person
     */
    public Person(String shortName, String firstName, String lastName, String email,
                  String description, LocalDate birthDate) {
        // Initialize as a SaharaItem
        super(shortName);

        this.setShortName(shortName);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setDescription(description);
        this.setBirthDate(birthDate);
        for (SaharaItem team : Global.currentWorkspace.getTeams()) {
            Team castedTeam = (Team) team;
            if (castedTeam.isUnassignedTeam()) {
                this.team = castedTeam;
                break; // Only one unassigned team
            }
        }
        setInformationSwitchStrategy(new PersonInformationSwitchStrategy());
    }


    /**
     * Returns a set of all children items inside a person
     * @return A set of all children items inside this person
     */
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();

        for (Skill skill : skills) {
            items.add(skill);
            items.addAll(skill.getItemsSet());
        }

        return items;
    }


    // <editor-fold defaultstate="collapsed" desc="Getters"> 

    /**
     * Gets the person's short name
     *
     * @return The short name of the person
     */
    public String getShortName() {
        return this.shortName;
    }


    /**
     * Gets the person's full name
     * @return The person's first name and last name stringed together
     */
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    /**
     * Sets the person's short name
     *
     * @param shortName the short name to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the person's first name
     *
     * @return The first name of the person
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the person's first name
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the person's last name
     *
     * @return The last name of the person
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the person's last name
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the person's email
     *
     * @return The email of the person
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the person's email
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the person's description
     *
     * @return The description of the person
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the person's description
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the person's birth date.
     *
     * @return The birth date of the person
     */
    public LocalDate getBirthDate() {
        return this.birthDate;
    }


    /**
     * Gets a list of the Persons Logs
     *
     * @return The list of logs
     */
    public ObservableList<Log> getLogs() {
        ObservableList<Project> projects = Global.currentWorkspace.getProjects();
        ObservableList<Log> allLogs = observableArrayList();
        ObservableList<Log> personLogs = observableArrayList();
        for (Project proj : projects) {
            allLogs.addAll(proj.getLogs());
        }
        for (Log log : allLogs) {
            if (log.getLogger() == this) {
                personLogs.add(log);
            }
            else if (log.getPartner() == this) {
                personLogs.add(log);
            }
        }
        return personLogs;
    }

    //</editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Setters">

    /**
     * Sets the person's birth date
     *
     * @param birthDate the birth date to set
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Gets the person's current team's name.
     *
     * @return The name of the team the person is currently in
     */
    public String getTeamName() {
        if (this.team == null) {
            return "Unassigned";
        }
        else {
            return this.team.getShortName();
        }
    }

    /**
     * Gets the ArrayList of serializable skills
     *
     * @return the serializable skills
     */
    public List<Skill> getSerializableSkills() {
        return serializableSkills;
    }

    /**
     * Gets the person's team
     *
     * @return The person's team
     */
    public Team getTeam() {
        return this.team;
    }

    /**
     * Sets the person's current team
     *
     * @param team the team the person has been added too
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Gets the person's current role.
     *
     * @return The current role
     */
    public Role getRole() {
        return this.role;
    }

    /**
     * Sets the person's current team
     *
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the person's list of Skills.
     *
     * @return The skills associated with a person
     */
    public ObservableList<Skill> getSkills() {
        /*this.serializableSkills.clear();
        for (Object item : this.skills)
        {
            this.serializableSkills.add((Skill)item);
        }*/
        return this.skills;
    }

    //</editor-fold>

    /**
     * Prepares a person to be serialized.
     */
    public void prepSerialization() {
        serializableSkills.clear();
        for (Skill skill : skills) {
            serializableSkills.add(skill);
        }

//        serializableLogs.clear();
//        for (Log log : logs) {
//            serializableLogs.add(log);
//        }
        prepTagSerialization();
    }


    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
        skills.clear();
        for (Skill skill : serializableSkills) {
            skills.add(skill);
        }

//        logs.clear();
//        for (Log log : serializableLogs) {
//            logs.add(log);
//        }
        postTagDeserialization();
    }

    /**
     * Gets the persons birth date as a string
     *
     * @return The persons birth date as a string
     */
    public String getDateString() {
        if (birthDate == null) {
            return "";
        }
        else {
            try {
                return this.getBirthDate().format(Global.dateFormatter);
                //return Global.datePattern.format(this.getBirthDate());
            }
            catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * Method for creating an XML element for the Person within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element personElement = ReportGenerator.doc.createElement("person");

        //WorkSpace Elements
        Element personID = ReportGenerator.doc.createElement("ID");
        personID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        personElement.appendChild(personID);

        Element personShortName = ReportGenerator.doc.createElement("identifier");
        personShortName.appendChild(ReportGenerator.doc.createTextNode(shortName));
        personElement.appendChild(personShortName);

        Element personFirstName = ReportGenerator.doc.createElement("first-name");
        personFirstName.appendChild(ReportGenerator.doc.createTextNode(firstName));
        personElement.appendChild(personFirstName);

        Element personLastName = ReportGenerator.doc.createElement("last-name");
        personLastName.appendChild(ReportGenerator.doc.createTextNode(lastName));
        personElement.appendChild(personLastName);

        Element personEmail = ReportGenerator.doc.createElement("email");
        personEmail.appendChild(ReportGenerator.doc.createTextNode(email));
        personElement.appendChild(personEmail);

        Element personBirthDate = ReportGenerator.doc.createElement("birth-date");
        personBirthDate.appendChild(ReportGenerator.doc.createTextNode(getDateString()));
        personElement.appendChild(personBirthDate);

        Element personDescription = ReportGenerator.doc.createElement("description");
        personDescription.appendChild(ReportGenerator.doc.createTextNode(description));
        personElement.appendChild(personDescription);

        Element skillsElement = ReportGenerator.doc.createElement("skills");
        for (Skill skill : skills) {
            Element skillElement = skill.generateXML();
            skillsElement.appendChild(skillElement);
        }
        personElement.appendChild(skillsElement);

        Element personTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            personTagElement.appendChild(tagElement);
        }
        personElement.appendChild(personTagElement);

        return personElement;
    }


    /**
     * Gets the children of the SaharaItem
     *
     * @return The items of the SaharaItem
     */
    @Override
    public ObservableList<SaharaItem> getChildren() {
        return null;
    }


    /**
     * Compares the person to another person based on their short names
     *
     * @param comparePerson The person to compare to
     * @return The string comparison result of the peoples' short names
     */
    @Override
    public int compareTo(Person comparePerson) {
        String pers1ShortName = this.getShortName();
        String pers2ShortName = comparePerson.getShortName();
        return pers1ShortName.compareTo(pers2ShortName);
    }


    /**
     * Deletes the person and removes them from team if they are in one.
     */
    public void deletePerson() {
        Command deletePers = new DeletePersonCommand(this);
        Global.commandManager.executeCommand(deletePers);
    }


    /**
     * Creates a Person edit command and executes it with the Global Command Manager, updating
     * the person with the new parameter values.
     *
     * @param newShortName   The persons new shortName
     * @param newFirstName   The persons new First name
     * @param newLastName    The perosns new Last name
     * @param newEmail       The persons new email address
     * @param newBirthDate   The persons new birth date
     * @param newDescription The persons new description
     * @param newSkills      the persons new list of skills
     * @param newTeam        the persons new team
     * @param newTags        The persons new tags
     */
    public void edit(String newShortName, String newFirstName, String newLastName,
                     String newEmail, LocalDate newBirthDate, String newDescription,
                     Team newTeam, ObservableList newSkills, ArrayList newTags) {
        Command persEdit = new PersonEditCommand(this, newShortName, newFirstName, newLastName,
                newEmail, newBirthDate, newDescription, newTeam, newSkills, newTags);
        Global.commandManager.executeCommand(persEdit);
    }




    /**
     * An overridden version for the String representation of a Person
     *
     * @return The short name of the Person
     */
    @Override
    public String toString() {
        return this.shortName;
    }

    /**
     * A command class that allows the executing and undoing of project edits
     */
    private class PersonEditCommand implements Command {
        private Person person;
        private String shortName;
        private String firstName;
        private String lastName;
        private String email;
        private LocalDate birthDate;
        private String description;
        private Team team;
        private ObservableList<Skill> skills;
        private Set<Tag> personTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private String oldShortName;
        private String oldFirstName;
        private String oldLastName;
        private String oldNewEmail;
        private LocalDate oldBirthDate;
        private String oldDescription;
        private Team oldTeam;
        private ObservableList<Skill> oldSkills;
        private Set<Tag> oldPersonTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        protected PersonEditCommand(Person person, String newShortName, String newFirstName,
                                    String newLastName, String newEmail, LocalDate newBirthDate,
                                    String newDescription, Team newTeam, ObservableList<Skill> newSkills,
                                    ArrayList<Tag> newTags) {
            this.person = person;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }


            this.shortName = newShortName;
            this.firstName = newFirstName;
            this.lastName = newLastName;
            this.email = newEmail;
            this.birthDate = newBirthDate;
            this.description = newDescription;
            this.team = newTeam;
            this.skills = newSkills;
            this.personTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldShortName = person.shortName;
            this.oldFirstName = person.firstName;
            this.oldLastName = person.lastName;
            this.oldNewEmail = person.email;
            this.oldBirthDate = person.birthDate;
            this.oldDescription = person.description;
            this.oldTeam = person.team;
            this.oldSkills = person.skills;
            this.oldPersonTags.addAll(person.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes/Redoes the changes of the person edit
         */
        public void execute() {
            person.shortName = shortName;
            person.firstName = firstName;
            person.lastName = lastName;
            person.email = email;
            person.birthDate = birthDate;
            person.description = description;
            person.team = team;
            team.addPerson(person);
            oldTeam.removePerson(person);
            person.skills = skills;

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a person has to their list of tags
            person.getTags().clear();
            person.getTags().addAll(personTags);

            Collections.sort(Global.currentWorkspace.getPeople());
        }

        /**
         * Undoes the changes of the person edit
         */
        public void undo() {
            person.shortName = oldShortName;
            person.firstName = oldFirstName;
            person.lastName = oldLastName;
            person.email = oldNewEmail;
            person.birthDate = oldBirthDate;
            person.description = oldDescription;
            person.team = oldTeam;
            oldTeam.addPerson(person);
            team.removePerson(person);
            person.skills = oldSkills;
            Collections.sort(Global.currentWorkspace.getPeople());

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the persons list of tags to what they used to be
            person.getTags().clear();
            person.getTags().addAll(oldPersonTags);
        }

        /**
         * Gets the String value of the Command for Editing a person.
         */
        public String getString() {
            return "the edit of Person \"" + person.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(person)) {
                    this.person = (Person) item;
                    mapped = true;
                }
            }




            boolean mapped_team = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped_team = true;
                }
            }
            boolean mapped_old_team = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldTeam)) {
                    this.oldTeam = (Team) item;
                    mapped_old_team = true;
                }
            }

            // Skill collections
            if (skills == null) {
                skills = FXCollections.observableArrayList();
            }
            for (Skill skill : skills) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(skill)) {
                        skills.remove(skill);
                        skills.add((Skill)item);
                        break;
                    }
                }
            }
            for (Skill skill : oldSkills) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(skill)) {
                        oldSkills.remove(skill);
                        oldSkills.add((Skill)item);
                        break;
                    }
                }
            }

            for (Tag tag : personTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        personTags.remove(tag);
                        personTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldPersonTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldPersonTags.remove(tag);
                        oldPersonTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : globalTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        globalTags.remove(tag);
                        globalTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldGlobalTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldGlobalTags.remove(tag);
                        oldGlobalTags.add((Tag)item);
                        break;
                    }
                }
            }

            return mapped && mapped_old_team && mapped_team;
        }
    }

    /**
     * A command class for allowing the deletion of people.
     */
    private class DeletePersonCommand implements Command {
        private Person person;
        private Team team;

        /**
         * Constructor for the person deletion command.
         * @param person The person to be deleted.
         */
        DeletePersonCommand(Person person) {
            this.person = person;
            this.team = person.getTeam();
        }

        /**
         * Executes the person deletion command.
         */
        public void execute() {
            team.getPeople().remove(person);
            person.setTeam(null);
            Global.currentWorkspace.getPeople().remove(person);

        }

        /**
         * Undoes the person deletion command.
         */
        public void undo() {
            team.getPeople().add(person);
            person.setTeam(team);
            Global.currentWorkspace.getPeople().add(person);
        }

        /**
         * Gets the String value of the Command for deleting a Person.
         */
        public String getString() {
            return "the deletion of Person \"" + person.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_person = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(person)) {
                    this.person = (Person) item;
                    mapped_person = true;
                }
            }
            boolean mapped_team = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped_team = true;
                }
            }
            return mapped_person && mapped_team;
        }
    }
}
