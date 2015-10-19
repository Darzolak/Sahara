/*
 * SENG302 Group 2
 */
package seng302.group2.workspace.team;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.TeamInformationSwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.allocation.Allocation;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.workspace.Workspace;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A basic class to represent a Team in the real world.
 *
 * @author crw73
 */
public class Team extends SaharaItem implements Serializable, Comparable<Team> {
    private String shortName;
    private String description;
    private Person scrumMaster;
    private Person productOwner;
    private transient ObservableList<Person> people = observableArrayList();
    private List<Person> serializablePeople = new ArrayList<>();
    private transient ObservableList<Person> devs = observableArrayList();
    private List<Person> serializableDevs = new ArrayList<>();
    private transient ObservableList<Allocation> projectAllocations = observableArrayList();
    private List<Allocation> serializableProjectAllocations = new ArrayList<>();
    private boolean unassigned = false;


    /**
     * Basic Team constructor
     */
    public Team() {
        super("Untitled Team");
        this.shortName = "Untitled Team";
        this.description = "";

        setInformationSwitchStrategy(new TeamInformationSwitchStrategy());
    }

    /** Basic team constructor with all fields
     *
     * @param shortName   A unique short name to identify a Team
     * @param description of a Team
     */
    public Team(String shortName, String description) {
        // Initialize as a SaharaItem
        super(shortName);
        this.shortName = shortName;
        this.description = description;

        setInformationSwitchStrategy(new TeamInformationSwitchStrategy());
    }


    /**
     * Adds the 'Unassigned' team to the workspace.
     *
     * @return the unassigned team
     */
    public static Team createUnassignedTeam() {
        Team temp = new Team("Unassigned",
                "All the people unassigned to a team");
        temp.unassigned = true;
        return temp;
    }

    /// <editor-fold defaultstate="collapsed" desc="Getters">

    /**

/**
     * Gets the set of SaharaItems 'belonging' to the Team: People, those in Dev roles, and its allocations
     * @return A set of SaharaItems belonging to the team
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();

        items.addAll(people);
        items.addAll(devs);
        items.addAll(projectAllocations);

        return items;
    }


    /**
     * Gets the Team's short name
     *
     * @return The short name of the team
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Sets the team's short name
     *
     * @param shortName the short name to set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the team's description
     *
     * @return The description of the team
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Gets the team's description
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the project the Team is assigned to
     *
     * @return The teams project.
     */
    public Project getProject() {
        if (this.getCurrentAllocation() != null) {
            return this.getCurrentAllocation().getProject();
        }
        return null;
    }

    /**
     * Gets the team's list of members
     *
     * @return The ObservableList of Persons
     */
    public ObservableList<Person> getPeople() {
        /*this.serializablePeople.clear();
        for (Object item : this.people)
        {
            this.serializablePeople.add((Person) item);
        }*/
        return this.people;
    }

    /**
     * Gets the team's list of roles
     *
     * @return The observable list of roles
     */
    public ObservableList<Person> getDevs() {
        /*this.serializableDevs.clear();
        for (Object item : this.devs)
        {
            this.serializableDevs.add((Person) item);
        }*/
        return this.devs;
    }

    /**
     * Get the team's Product Owner
     *
     * @return The Product Owner
     */
    public Person getProductOwner() {
        return this.productOwner;
    }

    /**
     * Sets the team's Product Owner
     *
     * @param person the person to set
     */
    public void setProductOwner(Person person) {
        this.productOwner = person;
        //person.setRole(new Role("Product Owner", RoleType.PRODUCT_OWNER));
    }

    //</editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Setters">

    /**
     * Get the team's Scrum Master
     *
     * @return The Scrum Master
     */
    public Person getScrumMaster() {
        return this.scrumMaster;
    }

    /**
     * Sets the team's Scrum Master
     *
     * @param person the person to set
     */
    public void setScrumMaster(Person person) {
        this.scrumMaster = person;
        //person.setRole(new Role("Scrum Master", RoleType.SCRUM_MASTER));
    }

    /**
     * Gets the serializable people of a team
     *
     * @return the serializable people
     */
    public List<Person> getSerializablePeople() {
        return serializablePeople;
    }

    /**
     * Gets the serializable project allocations
     *
     * @return list of project allocations
     */
    public List<Allocation> getSerializableProject() {
        return serializableProjectAllocations;
    }

    /**
     * Gets the team's list of project allocations
     *
     * @return list of allocations
     */
    public ObservableList<Allocation> getProjectAllocations() {
        return projectAllocations;
    }

    //</editor-fold>

    /**
     * gets the current allocation of the team. Returns a type of Allocation (NOT project).
     * @return the teams current allocation
     */
    public Allocation getCurrentAllocation() {
        Allocation currentAllocation = null;
        LocalDate now = LocalDate.now();
        for (Allocation allocation : this.getProjectAllocations()) {
            if ((allocation.getStartDate().isBefore(now) || allocation.getStartDate().isEqual(LocalDate.now()))
                    && (allocation.getEndDate() == null
                    || allocation.getEndDate().isAfter(now) || allocation.getEndDate().isEqual(LocalDate.now()))) {
                currentAllocation = allocation;
            }
        }
        return currentAllocation;
    }

    /**
     * Adds a Person to the Teams list of Members
     *  @param person The person to add
     *
     */
    public void add(Person person) {
        this.people.add(person);
    }

    /**
     * Adds a project allocation to the team's list of allocations
     *
     * @param allocation Allocation to add
     */
    @Deprecated
    public void add(Allocation allocation) {
        if (!this.equals(allocation.getTeam())) {
            return;
        }

        Command addAlloc = new AddAllocationCommand(allocation.getProject(), this, allocation);
        Global.commandManager.executeCommand(addAlloc);
    }

    /**
     * Returns if the team is the team of unassigned people
     *
     * @return if the team is the unassigned team
     */
    public boolean isUnassignedTeam() {
        return this.unassigned;
    }


    /**
     * //this.getTeamAllocations().add(allocation);
     * Removes the given allocation from the team's list of allocations
     *
     * @param allocation allocation to be removed
     */
    public void remove(Allocation allocation) {
        this.projectAllocations.remove(allocation);
    }


    /**
     * Prepares a Team to be serialized.
     */
    public void prepSerialization() {
        serializablePeople.clear();
        for (Person person : people) {
            this.serializablePeople.add(person);
        }

        serializableProjectAllocations.clear();
        for (Allocation item : projectAllocations) {
            this.serializableProjectAllocations.add(item);
        }

        prepTagSerialization();
    }


    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
        people.clear();
        for (Person person : serializablePeople) {
            this.people.add(person);
        }

        projectAllocations.clear();
        for (Allocation alloc : serializableProjectAllocations) {
            this.projectAllocations.add(alloc);
        }

        postTagDeserialization();
    }

    /**
     * Method for creating an XML element for the Team within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element teamElement = ReportGenerator.doc.createElement("team");

        //WorkSpace Elements
        Element teamID = ReportGenerator.doc.createElement("ID");
        teamID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        teamElement.appendChild(teamID);

        Element teamShortName = ReportGenerator.doc.createElement("identifier");
        teamShortName.appendChild(ReportGenerator.doc.createTextNode(shortName));
        teamElement.appendChild(teamShortName);

        Element teamDescription = ReportGenerator.doc.createElement("description");
        teamDescription.appendChild(ReportGenerator.doc.createTextNode(description));
        teamElement.appendChild(teamDescription);

        Element currentAllocations = ReportGenerator.doc.createElement("current-allocation");
        if (getCurrentAllocation() != null) {
            Element currentAllocation = getCurrentAllocation().generateXML();
            currentAllocations.appendChild(currentAllocation);
        }
        teamElement.appendChild(currentAllocations);

        Element projectPreviousElements = ReportGenerator.doc.createElement("previous-allocations");
        for (Allocation allocation : this.getPastAllocations()) {
            Element projectElement = allocation.generateXML();
            projectPreviousElements.appendChild(projectElement);
        }
        teamElement.appendChild(projectPreviousElements);

        Element projectFutureElements = ReportGenerator.doc.createElement("future-allocations");
        for (Allocation allocation : this.getFutureAllocations()) {
            Element projectElement = allocation.generateXML();
            projectFutureElements.appendChild(projectElement);
        }
        teamElement.appendChild(projectFutureElements);

        Element productOwnerElement = ReportGenerator.doc.createElement("product-owner");
        if (productOwner != null) {
            Element teamProductOwner = productOwner.generateXML();
            productOwnerElement.appendChild(teamProductOwner);
        }
        teamElement.appendChild(productOwnerElement);

        Element scrumMasterElement = ReportGenerator.doc.createElement("scrum-master");
        if (scrumMaster != null) {
            Element teamScrumMaster = scrumMaster.generateXML();
            scrumMasterElement.appendChild(teamScrumMaster);
        }
        teamElement.appendChild(scrumMasterElement);

        Element devElement = ReportGenerator.doc.createElement("devs");
        for (Person person : people) {
            if (person.getRole() != null) {
                if (person.getRole().getType() == Role.RoleType.DEVELOPMENT_TEAM_MEMBER) {
                    Element personElement = person.generateXML();
                    devElement.appendChild(personElement);
                }
            }
        }
        teamElement.appendChild(devElement);

        Element othersElement = ReportGenerator.doc.createElement("others");
        for (Person person : people) {
            if (person.getRole() != null) {
                if (person.getRole().getType() == Role.RoleType.NONE) {
                    Element personElement = person.generateXML();
                    othersElement.appendChild(personElement);
                }
            }
            if (person.getRole() == null) {
                Element personElement = person.generateXML();
                othersElement.appendChild(personElement);
            }
        }
        teamElement.appendChild(othersElement);

        Element teamTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            teamTagElement.appendChild(tagElement);
        }
        teamElement.appendChild(teamTagElement);

        return teamElement;
    }

    /**
     * Gets a set of the teams that have been previously assigned
     * to the project through the team allocations
     *
     * @return a set of the teams that have been assigned to the project
     */
    public Set<Allocation> getPastAllocations() {
        Set<Allocation> allocations = new HashSet<>();
        LocalDate now = LocalDate.now();
        for (Allocation alloc : projectAllocations) {
            if (alloc.getStartDate().isBefore(now)
                    && alloc.getEndDate().isBefore(now)) {
                allocations.add(alloc);
            }
        }
        return allocations;
    }

    /**
     * Gets a set of the allocations the team has for the future
     *
     * @return a set of the allocations
     */
    public Set<Allocation> getFutureAllocations() {
        Set<Allocation> allocations = new HashSet<>();
        LocalDate now = LocalDate.now();
        for (Allocation alloc : projectAllocations) {
            if (alloc.getEndDate() != null) {
                if (alloc.getStartDate().isAfter(now)
                        && alloc.getEndDate().isAfter(now)) {
                    allocations.add(alloc);
                }
            }
            else {
                if (alloc.getStartDate().isAfter(now)) {
                    allocations.add(alloc);
                }
            }
        }
        return allocations;
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
     * Compares the team to another team based on their short names
     *
     * @param compareTeam The team to compare to
     * @return The result of running the string comparator on the teams' short names
     */
    @Override
    public int compareTo(Team compareTeam) {
        String team1ShortName = this.getShortName();
        String team2ShortName = compareTeam.getShortName();
        return team1ShortName.compareTo(team2ShortName);
    }


    /**
     * Deletes the team without cascading deletion on the assigned people
     */
    public void deleteTeam() {
        Command teamDel = new DeleteTeamCommand(this);
        Global.commandManager.executeCommand(teamDel);
    }


    /**
     * Deletes the team and all the people currently assigned to it in a cascading manner.
     */
    public void deleteTeamCascading() {
        Command teamCasDelete = new DeleteTeamCascadingCommand(this, Global.currentWorkspace);
        Global.commandManager.executeCommand(teamCasDelete);
    }

    /**
     * Adds a person to the team.
     * @param person The person to be added.
     */
    public void addPerson(Person person) {
        Command addPersonCommand = new AddPersonCommand(this, person);
        Global.commandManager.executeCommand(addPersonCommand);
    }

    /**
     * Removes a person from the team.
     * @param person The person to be removed.
     */
    public void removePerson(Person person) {
        Command removePersonCommand = new RemovePersonCommand(this, person);
        Global.commandManager.executeCommand(removePersonCommand);
    }

    /**
     * An overridden version for the String representation of a Team
     *
     * @return The short name of the Team
     */
    @Override
    public String toString() {
        return this.shortName;
    }

//    /**
//     * Creates a team edit command and executes it with the Global Command Manager, updating
//     * the team with the new parameter values.
//     *
//     * @param newShortName   The new short name
//     * @param newDescription The new description
//     */
//    public void edit(String newShortName, String newDescription, ArrayList<Tag> newTags) {
//        Command teamEdit = new BasicTeamEditCommand(this, newShortName, newDescription, newTags);
//        Global.commandManager.executeCommand(teamEdit);
//    }


    /**
     * Creates a team edit command and executes it with the Global Command Manager, updating
     * the team with the new parameter values.
     *
     * @param newShortName   The new short name
     * @param newDescription The new description
     * @param newDevelopers  The new developers
     * @param newMembers     The new team members
     * @param newPO          The new product owner
     * @param newSM          The new scrum master
     * @param newTags        The new tags
     */
    public void edit(String newShortName, String newDescription, Collection<Person> newMembers,
                     Person newPO, Person newSM, Collection<Person> newDevelopers, ArrayList<Tag> newTags) {
        Command teamEdit = new ExtendedTeamEditCommand(this, newShortName, newDescription,
                newMembers, newPO, newSM, newDevelopers, newTags);
        Global.commandManager.executeCommand(teamEdit);
    }


    /**
     * A command class that allows the executing and undoing of basic team edits
     */
    private class BasicTeamEditCommand implements Command {
        private Team team;
        private String shortName;
        private String description;
        private String oldShortName;
        private String oldDescription;

        private BasicTeamEditCommand(Team team, String newShortName, String newDescription) {
            this.team = team;
            this.shortName = newShortName;
            this.description = newDescription;
            this.oldShortName = team.shortName;
            this.oldDescription = team.description;
        }

        /**
         * Executes/Redoes the changes of the team edit
         */
        public void execute() {
            team.shortName = shortName;
            team.description = description;
        }

        /**
         * Undoes the changes of the team edit
         */
        public void undo() {
            team.shortName = oldShortName;
            team.description = oldDescription;
        }

        /**
         * Gets the String value of the Command for editting a Team.
         */
        public String getString() {
            return "the edit of Team \"" + shortName + "\"";
        }

        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }


    /**
     * A command class that allows the executing and undoing of team edits
     */
    private class ExtendedTeamEditCommand implements Command {
        private Team team;

        private String shortName;
        private String description;
        private Collection<Person> members = new HashSet<>();
        private Person productOwner;
        private Person scrumMaster;
        private Collection<Person> developers = new HashSet<>();
        private Set<Tag> teamTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private String oldShortName;
        private String oldDescription;
        private Collection<Person> oldMembers = new HashSet<>();
        private Person oldProductOwner;
        private Person oldScrumMaster;
        private Collection<Person> oldDevelopers = new HashSet<>();
        private Set<Tag> oldTeamTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        private ExtendedTeamEditCommand(Team team, String newShortName, String newDescription,
                                        Collection<Person> newMembers, Person newPO, Person newSM,
                                        Collection<Person> newDevelopers, ArrayList<Tag> newTags) {
            this.team = team;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.shortName = newShortName;
            this.description = newDescription;
            this.members.addAll(newMembers);
            this.productOwner = newPO;
            this.scrumMaster = newSM;
            this.developers.addAll(newDevelopers);
            this.teamTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldShortName = team.shortName;
            this.oldDescription = team.description;
            this.oldMembers.addAll(team.people);
            this.oldProductOwner = team.productOwner;
            this.oldScrumMaster = team.scrumMaster;
            this.oldDevelopers.addAll(team.devs);
            this.oldTeamTags.addAll(team.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes/Redoes the changes of the team edit
         */
        public void execute() {
            Global.getUnassignedTeam().getPeople().removeAll(oldMembers);
            for (Person member : oldMembers) {
                member.setTeam(Global.getUnassignedTeam());
            }

            Global.getUnassignedTeam().getPeople().removeAll(members);
            for (Person member : members) {
                member.setTeam(team);
            }

            team.shortName = shortName;
            team.description = description;
            team.people.clear();
            team.people.addAll(members);
            team.productOwner = productOwner;
            team.scrumMaster = scrumMaster;
            team.devs.clear();
            team.devs.addAll(developers);

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a team has to their list of tags
            team.getTags().clear();
            team.getTags().addAll(teamTags);
        }

        /**
         * Undoes the changes of the team edit
         */
        public void undo() {
            team.shortName = oldShortName;
            team.description = oldDescription;
            team.people.clear();
            team.people.addAll(oldMembers);
            team.productOwner = oldProductOwner;
            team.scrumMaster = oldScrumMaster;
            team.devs.clear();
            team.devs.addAll(oldDevelopers);

            Global.getUnassignedTeam().getPeople().addAll(members);
            for (Person member : members) {
                member.setTeam(Global.getUnassignedTeam());
            }
            for (Person member : oldMembers) {
                member.setTeam(team);
            }

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the team list of tags to what they used to be
            team.getTags().clear();
            team.getTags().addAll(oldTeamTags);

        }

        /**
         * Gets the String value of the Command for editting Teams.
         */
        public String getString() {
            return "the edit of Team \"" + team.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_team = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped_team = true;
                }
            }

            boolean mapped_po = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(productOwner)) {
                    this.productOwner = (Person) item;
                    mapped_po = true;
                }
            }
            boolean mapped_old_po = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldProductOwner)) {
                    this.oldProductOwner = (Person) item;
                    mapped_old_po = true;
                }
            }

            boolean mapped_sm = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(scrumMaster)) {
                    this.scrumMaster = (Person) item;
                    mapped_sm = true;
                }
            }
            boolean mapped_old_sm = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldScrumMaster)) {
                    this.oldScrumMaster = (Person) item;
                    mapped_old_sm = true;
                }
            }

            // Members collections
            for (Person member : members) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(member)) {
                        members.remove(member);
                        members.add((Person)item);
                        break;
                    }
                }
            }
            for (Person member : oldMembers) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(member)) {
                        oldMembers.remove(member);
                        oldMembers.add((Person)item);
                        break;
                    }
                }
            }

            // Developers collections
            for (Person dev : developers) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(dev)) {
                        developers.remove(dev);
                        developers.add((Person)item);
                        break;
                    }
                }
            }

            for (Person dev : oldDevelopers) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(dev)) {
                        oldDevelopers.remove(dev);
                        oldDevelopers.add((Person)item);
                        break;
                    }
                }
            }

            //Tag collections
            for (Tag tag : teamTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        teamTags.remove(tag);
                        teamTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldTeamTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldTeamTags.remove(tag);
                        oldTeamTags.add((Tag)item);
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

            return mapped_sm && mapped_po && mapped_team && mapped_old_po && mapped_old_sm;
        }
    }

    /**
     * A command class for allowing the deletion of Teams
     */
    private class DeleteTeamCommand implements Command {
        private Team team;
        private List<Person> members = new ArrayList<>();
        private List<Sprint> sprints = new ArrayList<>();
        private List<Allocation> teamAllocs = new ArrayList<>();
        private List<Allocation> futureAllocs = new ArrayList<>();
        private List<Allocation> pastAllocs = new ArrayList<>();

        /**
         * Constructor of the team deletion command
         * @param team The team to be deleted
         */
        DeleteTeamCommand(Team team) {
            this.team = team;
            for (Person p : team.getPeople()) {
                members.add(p);
            }

            for (Project proj : Global.currentWorkspace.getProjects()) {
                for (Sprint sprint : proj.getSprints()) {
                    if (sprint.getTeam() == team) {
                        sprints.add(sprint);
                    }
                }
                for (Allocation alloc : proj.getTeamAllocations()) {
                    if (alloc.getTeam() == team) {
                        teamAllocs.add(alloc);
                    }
                }
                for (Allocation alloc : proj.getFutureAllocations()) {
                    if (alloc.getTeam() == team) {
                        futureAllocs.add(alloc);
                    }
                }
                for (Allocation alloc : proj.getPastAllocations()) {
                    if (alloc.getTeam() == team) {
                        pastAllocs.add(alloc);
                    }
                }
            }
        }

        /**
         * Executes the team deletion command
         */
        public void execute() {
            Global.currentWorkspace.getTeams().remove(team);
            for (Person member : members) {
                member.setTeam(Global.getUnassignedTeam());
                Global.getUnassignedTeam().getPeople().add(member);
            }

            for (Sprint sprint : sprints) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    if (proj.getSprints().contains(sprint)) {
                        proj.getSprints().remove(sprint);
                        break;
                    }
                }
            }
            for (Project proj : Global.currentWorkspace.getProjects()) {
                for (Allocation teamAlloc : teamAllocs) {
                    if (proj.getTeamAllocations().contains(teamAlloc)) {
                        proj.getTeamAllocations().remove(teamAlloc);
                    }
                }
                for (Allocation futureAlloc : futureAllocs) {
                    if (proj.getFutureAllocations().contains(futureAlloc)) {
                        proj.getFutureAllocations().remove(futureAlloc);
                    }
                }
                for (Allocation pastAlloc : pastAllocs) {
                    if (proj.getPastAllocations().contains(pastAlloc)) {
                        proj.getPastAllocations().remove(pastAlloc);
                    }
                }
            }
            Global.currentWorkspace.getTeams().remove(team);
        }

        /**
         * Undoes the team deletion command
         */
        public void undo() {
            for (Person member : members) {
                member.setTeam(team);
                Global.getUnassignedTeam().getPeople().remove(member);
            }
            for (Sprint sprint : sprints) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    if (sprint.getProject() == proj) {
                        proj.getSprints().add(sprint);
                        break;
                    }
                }
            }
            for (Project proj : Global.currentWorkspace.getProjects()) {
                for (Allocation teamAlloc : teamAllocs) {
                    if (teamAlloc.getProject() == proj) {
                        proj.getTeamAllocations().add(teamAlloc);
                    }
                }
                for (Allocation futureAlloc : futureAllocs) {
                    if (futureAlloc.getProject() == proj) {
                        proj.getFutureAllocations().add(futureAlloc);
                    }
                }
                for (Allocation pastAlloc : pastAllocs) {
                    if (pastAlloc.getProject() == proj) {
                        proj.getPastAllocations().add(pastAlloc);
                    }
                }
            }
            Global.currentWorkspace.getTeams().add(team);
        }

        /**
         * Gets the String value of the Command for deleting Teams.
         */
        public String getString() {
            return "the deletion of Team \"" + team.getShortName() + "\"";
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
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped = true;
                }
            }

            // Member collection
            for (Person member : members) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(member)) {
                        members.remove(member);
                        members.add((Person)item);
                        break;
                    }
                }
            }

            for (Sprint sprint : sprints) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(sprint)) {
                        sprints.remove(sprint);
                        sprints.add((Sprint)item);
                        break;
                    }
                }
            }

            for (Allocation teamAlloc : teamAllocs) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(teamAlloc)) {
                        teamAllocs.remove(teamAlloc);
                        teamAllocs.add((Allocation)item);
                        break;
                    }
                }
            }

            for (Allocation futureAlloc : futureAllocs) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(futureAlloc)) {
                        futureAllocs.remove(futureAlloc);
                        futureAllocs.add((Allocation)item);
                        break;
                    }
                }
            }

            for (Allocation pastAlloc : pastAllocs) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(pastAlloc)) {
                        pastAllocs.remove(pastAlloc);
                        pastAllocs.add((Allocation)item);
                        break;
                    }
                }
            }

            return mapped;
        }
    }

    /**
     * A class for allowing the cascading deletion of the members of a team upon the teams deletion, as well as any
     * sprints and allocations associated.
     */
    private class DeleteTeamCascadingCommand implements Command {
        private Team team;
        private List<Person> members = new ArrayList<>();
        private List<Sprint> sprints = new ArrayList<>();
        private List<Allocation> teamAllocs = new ArrayList<>();
        private List<Allocation> futureAllocs = new ArrayList<>();
        private List<Allocation> pastAllocs = new ArrayList<>();

        /**
         * Constructor for the cascading team deletion command
         * @param team The team to be deleted
         * @param ws The workspace to which the team belonged
         */
        DeleteTeamCascadingCommand(Team team, Workspace ws) {
            this.team = team;
            for (Person p : team.getPeople()) {
                members.add(p);
            }
            for (Project proj : Global.currentWorkspace.getProjects()) {
                for (Sprint sprint : proj.getSprints()) {
                    if (sprint.getTeam() == team) {
                        sprints.add(sprint);
                    }
                }
                for (Allocation alloc : proj.getTeamAllocations()) {
                    if (alloc.getTeam() == team) {
                        teamAllocs.add(alloc);
                    }
                }
                for (Allocation alloc : proj.getFutureAllocations()) {
                    if (alloc.getTeam() == team) {
                        futureAllocs.add(alloc);
                    }
                }
                for (Allocation alloc : proj.getPastAllocations()) {
                    if (alloc.getTeam() == team) {
                        pastAllocs.add(alloc);
                    }
                }
            }

        }

        /**
         * Executes the cascading team deletion command
         */
        public void execute() {
            for (Person member : members) {
                Global.currentWorkspace.getPeople().remove(member);
            }
            for (Sprint sprint : sprints) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    if (proj.getSprints().contains(sprint)) {
                        proj.getSprints().remove(sprint);
                        break;
                    }
                }
            }
            for (Project proj : Global.currentWorkspace.getProjects()) {
                for (Allocation teamAlloc : teamAllocs) {
                    if (proj.getTeamAllocations().contains(teamAlloc)) {
                        proj.getTeamAllocations().remove(teamAlloc);
                    }
                }
                for (Allocation futureAlloc : futureAllocs) {
                    if (proj.getFutureAllocations().contains(futureAlloc)) {
                        proj.getFutureAllocations().remove(futureAlloc);
                    }
                }
                for (Allocation pastAlloc : pastAllocs) {
                    if (proj.getPastAllocations().contains(pastAlloc)) {
                        proj.getPastAllocations().remove(pastAlloc);
                    }
                }
            }
            Global.currentWorkspace.getTeams().remove(team);
        }

        /**
         * Undoes the team deletion command
         */
        public void undo() {
            for (Person member : members) {
                Global.currentWorkspace.getPeople().add(member);
            }
            for (Sprint sprint : sprints) {
                for (Project proj : Global.currentWorkspace.getProjects()) {
                    if (sprint.getProject() == proj) {
                        proj.getSprints().add(sprint);
                        break;
                    }
                }
            }
            for (Project proj : Global.currentWorkspace.getProjects()) {
                for (Allocation teamAlloc : teamAllocs) {
                    if (teamAlloc.getProject() == proj) {
                        proj.getTeamAllocations().add(teamAlloc);
                    }
                }
                for (Allocation futureAlloc : futureAllocs) {
                    if (futureAlloc.getProject() == proj) {
                        proj.getFutureAllocations().add(futureAlloc);
                    }
                }
                for (Allocation pastAlloc : pastAllocs) {
                    if (pastAlloc.getProject() == proj) {
                        proj.getPastAllocations().add(pastAlloc);
                    }
                }
            }
            Global.currentWorkspace.getTeams().add(team);
        }

        /**
         * Gets the String value of the Command for cascading team deletion.
         */
        public String getString() {
            return "the cascading deletion of Team \"" + team.getShortName() + "\"";
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
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped = true;
                }
            }

            // Member collection
            for (Person member : members) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(member)) {
                        members.remove(member);
                        members.add((Person)item);
                        break;
                    }
                }
            }

            for (Sprint sprint : sprints) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(sprint)) {
                        sprints.remove(sprint);
                        sprints.add((Sprint)item);
                        break;
                    }
                }
            }

            for (Allocation teamAlloc : teamAllocs) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(teamAlloc)) {
                        teamAllocs.remove(teamAlloc);
                        teamAllocs.add((Allocation)item);
                        break;
                    }
                }
            }

            for (Allocation futureAlloc : futureAllocs) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(futureAlloc)) {
                        futureAllocs.remove(futureAlloc);
                        futureAllocs.add((Allocation)item);
                        break;
                    }
                }
            }

            for (Allocation pastAlloc : pastAllocs) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(pastAlloc)) {
                        pastAllocs.remove(pastAlloc);
                        pastAllocs.add((Allocation)item);
                        break;
                    }
                }
            }

            return mapped;
        }
    }

    /**
     * A command class for allowing the addition of People to Teams
     */
    private class AddPersonCommand implements Command {
        private Person person;
        private Team team;

        /**
         * Constructor for the person addition command
         * @param team The team to which the person is added
         * @param person The person to be added
         */
        AddPersonCommand(Team team, Person person) {
            this.person = person;
            this.team = team;
        }

        /**
         * Executes the person addition command
         */
        public void execute() {
            team.getPeople().add(person);
            //person.setTeam(team);
        }

        /**
         * Undoes the person addition command
         */
        public void undo() {
            team.getPeople().remove(person);
            //person.setTeam(team);
        }

        /**
         * Gets the String value of the Command for adding a Person.
         */
        public String getString() {
            return "the addition of Person \"" + person.getShortName() + "\" to Team \""
                    + team.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_team = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped_team = true;
                }
            }
            boolean mapped_person = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(person)) {
                    this.person = (Person) item;
                    mapped_person = true;
                }
            }
            return mapped_team && mapped_person;
        }
    }

    /**
     * A command class for allowing the removal of People from Teams
     */
    private class RemovePersonCommand implements Command {
        private Person person;
        private Team team;

        /**
         * Constructor for the person removal command
         * @param team The team from which the person is removed
         * @param person The person to be removed
         */
        RemovePersonCommand(Team team, Person person) {
            this.person = person;
            this.team = team;
        }

        /**
         * Executes the person removed command
         */
        public void execute() {
            team.getPeople().remove(person);
            //person.setTeam(team);
        }

        /**
         * Undoes the person removal command
         */
        public void undo() {
            team.getPeople().add(person);
            //person.setTeam(team);
        }

        /**
         * Gets the String value of the Command for removing a Person off a team.
         */
        public String getString() {
            return "the removal of Person \"" + person.getShortName() + "\" off Team \""
                    + team.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_team = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped_team = true;
                }
            }
            boolean mapped_person = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(person)) {
                    this.person = (Person) item;
                    mapped_person = true;
                }
            }
            return mapped_team && mapped_person;
        }
    }

    /**
     * A command class for allowing the addition of Allocations to Teams
     */
    private class AddAllocationCommand implements Command {
        private Project proj;
        private Team team;
        private Allocation allocation;

        /**
         * Constructor for the allocation addition command
         * @param proj The project to which the team is allocated
         * @param team The team to be allocated
         * @param allocation The allocation to be added
         */
        AddAllocationCommand(Project proj, Team team, Allocation allocation) {
            this.proj = proj;
            this.team = team;
            this.allocation = allocation;
        }

        /**
         * Executes the allocation addition command
         */
        public void execute() {
            proj.getTeamAllocations().add(allocation);
            team.getProjectAllocations().add(allocation);
        }

        /**
         * Undoes the allocation addition command
         */
        public void undo() {
            proj.getTeamAllocations().remove(allocation);
            team.getProjectAllocations().remove(allocation);
        }

        /**
         * Gets the String value of the Command for adding an allocation.
         */
        public String getString() {
            return "the creation of an Allocation for \"" + team.getShortName() + "\" on \""
                    + proj.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_team = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped_team = true;
                }
            }
            boolean mapped_project = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped_project = true;
                }
            }
            boolean mapped_alloc = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(allocation)) {
                    this.allocation = (Allocation) item;
                    mapped_alloc = true;
                }
            }
            return mapped_team && mapped_project && mapped_alloc;
        }
    }
}
