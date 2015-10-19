package seng302.group2.workspace.project;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.project.ProjectInformationSwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.allocation.Allocation;
import seng302.group2.workspace.categories.subCategory.project.BacklogCategory;
import seng302.group2.workspace.categories.subCategory.project.ReleaseCategory;
import seng302.group2.workspace.categories.subCategory.project.SprintCategory;
import seng302.group2.workspace.categories.subCategory.project.StoryCategory;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A class representing real-world projects
 * Created by Jordane on 18/04/2015.
 */
public class Project extends SaharaItem implements Serializable, Comparable<Project> {
    private String shortName;
    private String longName;
    private String description;

    private transient ObservableList<Release> releases = observableArrayList();
    private List<Release> serializableReleases = new ArrayList<>();
    private transient ObservableList<Allocation> teamAllocations = observableArrayList();
    private List<Allocation> serializableTeamAllocations = new ArrayList<>();
    private transient ObservableList<Story> unallocatedStories = observableArrayList();
    private List<Story> serializableStories = new ArrayList<>();
    private transient ObservableList<Backlog> backlogs = observableArrayList();
    private List<Backlog> serializableBacklogs = new ArrayList<>();
    private transient ObservableList<Sprint> sprints = observableArrayList();
    private List<Sprint> serializableSprints = new ArrayList<>();
    private transient ObservableList<Log> logs = observableArrayList();
    private List<Log> serializableLogs = new ArrayList<>();

    private transient ReleaseCategory releasesCategory = new ReleaseCategory(this);
    private transient BacklogCategory backlogCategory = new BacklogCategory(this);
    private transient StoryCategory storiesCategory = new StoryCategory(this);
    private transient SprintCategory sprintsCategory = new SprintCategory(this);


    /**
     * Basic Workspace constructor.
     */
    public Project() {
        super("Untitled Project");
        this.shortName = "Untitled Project";
        this.longName = "Untitled Project";
        this.description = "A blank project.";
        addListeners();

        setInformationSwitchStrategy(new ProjectInformationSwitchStrategy());
    }

    /**
     * Gets the set of SaharaItems 'belonging' to the Project: Backlogs, Releases, Unallocated Stories, Allocations,
     * Sprints
     * @return A set of SaharaItems belonging to the project
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();
        items.addAll(releases);
        items.addAll(teamAllocations);

        for (Story story : unallocatedStories) {
            items.addAll(story.getItemsSet());
        }
        items.addAll(unallocatedStories);

        for (Backlog bl : backlogs) {
            items.addAll(bl.getItemsSet());
        }
        items.addAll(backlogs);
        for (Release release : releases) {
            items.addAll(release.getItemsSet());
        }
        items.addAll(releases);
        for (Sprint sprint : sprints) {
            items.addAll(sprint.getItemsSet());
        }

        return items;
    }


    /**
     * Basic project constructor with input.
     *
     * @param shortName   A unique short name to identify the Project
     * @param fullName    The full Project name
     * @param description A description of the Project
     */
    public Project(String shortName, String fullName, String description) {
        super(shortName);
        this.shortName = shortName;
        this.longName = fullName;
        this.description = description;
        addListeners();

        setInformationSwitchStrategy(new ProjectInformationSwitchStrategy());
    }


    /**
     * Adds listeners to the project properties (lists), primarily for sorting
     */
    public void addListeners() {
        releases.addListener((ListChangeListener<Release>) change -> {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(releases);
                }
            });

        unallocatedStories.addListener((ListChangeListener<Story>) change -> {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(unallocatedStories, Story.StoryNameComparator);
                }
            });

        backlogs.addListener((ListChangeListener<Backlog>) change -> {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(backlogs);
                }
            });

        sprints.addListener((ListChangeListener<Sprint>) change -> {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(sprints);
                }
            });

        teamAllocations.addListener((ListChangeListener<Allocation>) change -> {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(teamAllocations);
                }
            });

        for (Backlog bl : backlogs) {
            bl.addListeners();
        }
    }


    // <editor-fold defaultstate="collapsed" desc="Getters">

    /**
     * Gets the short name of the project
     *
     * @return Short name of the project
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name of the project
     *
     * @param shortName The short name to set to the project
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the long name of the project
     *
     * @return The long name of the project
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Sets the long name of the project
     *
     * @param longName The long name to set to the project
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     * Gets a set of the teams that have been assigned to the project through the team allocations
     *
     * @return a set of the teams that have been assigned to the project
     */
    public Set<Team> getAllTeams() {
        Set<Team> teams = new HashSet<>();
        for (Allocation alloc : teamAllocations) {
            Team projectTeam = alloc.getTeam();
            if (!projectTeam.isUnassignedTeam()) {
                teams.add(projectTeam);
            }
        }
        return teams;
    }

    /**
     * Gets a set of the teams currently assigned to the project through the team allocations
     *
     * @return a set of the teams currently assigned to the project
     */
    public Set<Team> getCurrentTeams() {
        Set<Team> teams = new HashSet<>();
        LocalDate now = LocalDate.now();
        for (Allocation alloc : teamAllocations) {
            Team projectTeam = alloc.getTeam();

            if (!projectTeam.isUnassignedTeam()
                    && !alloc.getStartDate().isAfter(now)
                    && (alloc.getEndDate() == null || alloc.getEndDate().isAfter(now.minusDays(1)))
                    && !teams.contains(projectTeam)
                    && Global.currentWorkspace.getTeams().contains(alloc.getTeam())) {
                teams.add(projectTeam);
            }
        }
        return teams;
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
        for (Allocation alloc : teamAllocations) {
            Team projectTeam = alloc.getTeam();

            if (!projectTeam.isUnassignedTeam()
                    && alloc.getStartDate().isBefore(now)
                    && alloc.getEndDate().isBefore(now)) {
                allocations.add(alloc);
            }
        }
        return allocations;
    }

    /**
     * Gets a set of the teams that will be assigned to the project
     *
     * @return a set of the teams that will be assigned to the project
     */
    public Set<Allocation> getFutureAllocations() {
        Set<Allocation> allocations = new HashSet<>();
        LocalDate now = LocalDate.now();
        for (Allocation alloc : teamAllocations) {
            Team projectTeam = alloc.getTeam();
            if (alloc.getEndDate() != null) {
                if (!projectTeam.isUnassignedTeam()
                        && alloc.getStartDate().isAfter(now)
                        && alloc.getEndDate().isAfter(now)) {
                    allocations.add(alloc);
                }
            }
            else {
                if (!projectTeam.isUnassignedTeam()
                        && alloc.getStartDate().isAfter(now)) {
                    allocations.add(alloc);
                }
            }
        }
        return allocations;
    }

    /**
     * Gets the releases of the project
     *
     * @return list of releases
     */
    public ObservableList<Release> getReleases() {
        return this.releases;
    }

    /**
     * Gets the unallocatedStories of the project
     *
     * @return list of unallocatedStories
     */
    public ObservableList<Story> getUnallocatedStories() {
        return this.unallocatedStories;
    }

    /**
     * Gets the backlogs of the project
     *
     * @return list of backlogs
     */
    public ObservableList<Backlog> getBacklogs() {
        return this.backlogs;
    }

    /**
     * Gets the Logs of the project
     *
     * @return list of Logs
     */
    public ObservableList<Log> getLogs() {
        return this.logs;
    }

    /**
     * Gets the sprints of the project
     *
     * @return list of sprints
     */
    public ObservableList<Sprint> getSprints() {
        return this.sprints;
    }

    /**
     * Gets the description of the project
     *
     * @return The description of the project
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the project
     *
     * @param description The description to set to the project
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the serializable releases
     *
     * @return the serializable releases
     */
    public List<Release> getSerializableReleases() {
        return serializableReleases;
    }

    /**
     * Gets the serializable unallocatedStories
     *
     * @return the serializable releases
     */
    public List<Story> getSerializableStories() {
        return serializableStories;
    }
    //</editor-fold>

    /**
     * Gets the serializable Backlogs
     *
     * @return the serializable Backlogs
     */
    public List<Backlog> getSerializableBacklogs() {
        return serializableBacklogs;
    }

    /**
     * Gets the serializable Sprints
     *
     * @return the serializable Sprints
     */
    public List<Sprint> getSerializableSprints() {
        return serializableSprints;
    }


    // <editor-fold defaultstate="collapsed" desc="Setters"> 

    /**
     * Gets the list of teams allocated to the project in the past
     *
     * @return list of teams
     */
    public ObservableList<Allocation> getTeamAllocations() {
        return teamAllocations;
    }

    /**
     * Gets the serializable teams allocated to the project in the past
     *
     * @return serializable teams
     */
    public List<Allocation> getSerializableTeamAllocations() {
        return serializableTeamAllocations;
    }

    //</editor-fold>


    /**
     * Adds a Project to the Project list of Stories
     *
     * @param story the story to add
     */
    public void add(Story story) {
        Command command = new AddStoryCommand(this, story);
        Global.commandManager.executeCommand(command);

    }

    /**
     * Adds a Project to the Project list of Stories and to the sprint
     *
     * @param story the story to add
     * @param sprint to add story to
     */
    public void add(Story story, Sprint sprint) {
        Command command = new AddStorySprintCommand(this, story, sprint);
        Global.commandManager.executeCommand(command);

    }


    /**
     * Adds a Backlog to the Backlog list of Backlogs
     *
     * @param backlog the Backlog to add
     */
    public void add(Backlog backlog) {
        Command command = new AddBacklogCommand(this, backlog);
        Global.commandManager.executeCommand(command);
    }


    /**
     * Removes the team allocation from the list of project's team allocation
     *
     * @param allocation allocation to remove from the list
     */
    @Deprecated
    public void remove(Allocation allocation) {
        this.teamAllocations.remove(allocation);
    }


    /**
     * Add Release to Project without an undoable command
     *
     * @param release release to be added
     */
    public void addWithoutUndo(Release release) {
        this.releases.add(release);
    }


    /**
     * Adds a Release to the Project's list of Releases.
     *
     * @param release The release to add
     */
    public void add(Release release) {
        Command command = new AddReleaseCommand(this, release);
        Global.commandManager.executeCommand(command);
    }

    /**
     * Adds a Sprint to the Project's list of Sprints.
     *
     * @param sprint The sprint to add
     */
    public void add(Sprint sprint) {
        Command command = new AddSprintCommand(this, sprint);
        Global.commandManager.executeCommand(command);
    }


    /**
     * Adds a team allocation to the list of project's team allocations
     *
     * @param allocation Allocation to add
     */
    public void add(Allocation allocation) {
        if (!this.equals(allocation.getProject())) {
            return;
        }
        Command addAlloc = new AddAllocationCommand(this, allocation.getTeam(), allocation);
        Global.commandManager.executeCommand(addAlloc);
    }


    /**
     * Adds a Log to the Project's list of logs. Does not create an Undo command.
     *
     * @param log The log to add
     */
    public void add(Log log) {
        Task task = log.getTask();
        AddLogsCommand addCommand = new AddLogsCommand(task, log, this);
        Global.commandManager.executeCommand(addCommand);
    }


    /**
     * Removes a Log from the Project's list of logs. Does not create an Undo command.
     *
     * @param log The log to delete
     */
    public void delete(Log log) {
        this.logs.remove(log);
    }


    /**
     * Prepares a project to be serialized.
     */
    public void prepSerialization() {
        serializableReleases.clear();
        for (Object item : releases) {
            this.serializableReleases.add((Release) item);
        }

        serializableTeamAllocations.clear();
        for (Allocation item : teamAllocations) {
            this.serializableTeamAllocations.add(item);
        }

        serializableStories.clear();
        for (Story item : unallocatedStories) {
            item.prepSerialization();
            this.serializableStories.add(item);
        }

        serializableBacklogs.clear();
        for (Backlog backlog : backlogs) {
            backlog.prepSerialization();
            this.serializableBacklogs.add(backlog);
        }

        serializableSprints.clear();
        for (Sprint sprint : sprints) {
            sprint.prepSerialization();
            this.serializableSprints.add(sprint);
        }

        serializableLogs.clear();
        for (Log log : logs) {
            this.serializableLogs.add(log);
        }

        prepTagSerialization();
    }


    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
        /*teams.clear();
        for (Object item : serializableTeams)
        {
            this.teams.add((Team) item);
        }*/

        releases.clear();
        for (Object item : serializableReleases) {
            this.releases.add((Release) item);
        }

        teamAllocations.clear();
        for (Allocation alloc : serializableTeamAllocations) {
            this.teamAllocations.add(alloc);
        }

        unallocatedStories.clear();
        for (Story story : serializableStories) {
            story.postDeserialization();
            this.unallocatedStories.add(story);
        }

        backlogs.clear();
        for (Backlog backlog : serializableBacklogs) {
            backlog.postDeserialization();
            this.backlogs.add(backlog);
        }

        sprints.clear();
        for (Sprint sprint : serializableSprints) {
            sprint.postDeserialization();
            this.sprints.add(sprint);
        }

        logs.clear();
        for (Log log : serializableLogs) {
            this.logs.add(log);
        }

        postTagDeserialization();
    }


    /**
     * Gets all of the stories within the project (unassigned and assigned)
     *
     * @return all of the stories within the project (unassigned and assigned)
     */
    public ArrayList<Story> getAllStories() {
        ArrayList<Story> stories = new ArrayList<>();
        stories.addAll(unallocatedStories);
        for (Backlog backlog : backlogs) {
            stories.addAll(backlog.getStories());
        }
        return stories;
    }

    /**
     * Method for creating an XML element for the Project within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element projectElement = ReportGenerator.doc.createElement("project");

        //WorkSpace Elements
        Element projectID = ReportGenerator.doc.createElement("ID");
        projectID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        projectElement.appendChild(projectID);

        Element projectShortName = ReportGenerator.doc.createElement("identifier");
        projectShortName.appendChild(ReportGenerator.doc.createTextNode(shortName));
        projectElement.appendChild(projectShortName);

        Element projectLongName = ReportGenerator.doc.createElement("long-name");
        projectLongName.appendChild(ReportGenerator.doc.createTextNode(longName));
        projectElement.appendChild(projectLongName);

        Element projectDescription = ReportGenerator.doc.createElement("description");
        projectDescription.appendChild(ReportGenerator.doc.createTextNode(description));
        projectElement.appendChild(projectDescription);

        Element teamElements = ReportGenerator.doc.createElement("current-teams");
        for (Team team : this.getCurrentTeams()) {
            if (ReportGenerator.generatedItems.contains(team)) {
                Element teamElement = team.generateXML();
                teamElements.appendChild(teamElement);
                ReportGenerator.generatedItems.remove(team);
            }
        }
        projectElement.appendChild(teamElements);

        Element teamPreviousElements = ReportGenerator.doc.createElement("previous-allocations");
        for (Allocation allocation : this.getPastAllocations()) {
            Element teamElement = allocation.generateXML();
            teamPreviousElements.appendChild(teamElement);
        }
        projectElement.appendChild(teamPreviousElements);

        Element teamFutureElements = ReportGenerator.doc.createElement("future-allocations");
        for (Allocation allocation : this.getFutureAllocations()) {
            Element teamElement = allocation.generateXML();
            teamFutureElements.appendChild(teamElement);
        }
        projectElement.appendChild(teamFutureElements);

        //Generate the children of the Project, ie Releases, Backlogs, Sprints and Unassigned Stories.
        for (SaharaItem item : this.getChildren()) {
            if (ReportGenerator.generatedItems.contains(item)) {
                Element xmlElement = item.generateXML();
                if (xmlElement != null) {
                    projectElement.appendChild(xmlElement);
                }
                ReportGenerator.generatedItems.remove(item);
            }
        }

        Element projectTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            projectTagElement.appendChild(tagElement);
        }
        projectElement.appendChild(projectTagElement);

        return projectElement;
    }


    /**
     * An overridden version for the String representation of a Workspace.
     *
     * @return The short name of the Workspace
     */
    @Override
    public String toString() {
        return this.shortName;
    }

    /**
     * Compares to another project based on their short names
     *
     * @param compareProject The project to compare to
     * @return The result of compareTo on the short names of the projects
     */
    @Override
    public int compareTo(Project compareProject) {
        String proj1ShortName = this.getShortName();
        String proj2ShortName = compareProject.getShortName();
        return proj1ShortName.compareTo(proj2ShortName);
    }


    /**
     * Deletes a project from the given workspace.
     */
    public void deleteProject() {
        Command command = new DeleteProjectCommand(this);
        Global.commandManager.executeCommand(command);
    }


    /**
     * Gets the children of the category
     *
     * @return the children of the category
     */
    @Override
    public ObservableList<SaharaItem> getChildren() {
        ObservableList<SaharaItem> children = observableArrayList();
        children.addAll(releasesCategory, backlogCategory, storiesCategory, sprintsCategory);
        return children;
    }


    /**
     * Creates a Project edit command and executes it with the Global Command Manager, updating
     * the project with the new parameter values.
     *
     * @param newShortName   The new short name
     * @param newLongName    The new long name
     * @param newDescription The new description
     * @param teams          The new project teams
     * @param newTags        The new tags
     */
    public void edit(String newShortName, String newLongName, String newDescription,
                     ObservableList<Team> teams, ArrayList<Tag> newTags) {
        Command projEdit = new ProjectEditCommand(this, newShortName, newLongName, newDescription,
                teams, newTags);
        Global.commandManager.executeCommand(projEdit);
    }


    /**
     * A command class that allows the executing and undoing of project edits
     */
    private class ProjectEditCommand implements Command {

        private Project proj;
        private String shortName;
        private String longName;
        private String description;
        private ObservableList<Team> teams = FXCollections.observableArrayList();
        private Set<Tag> projectTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();


        private String oldShortName;
        private String oldLongName;
        private String oldDescription;
        private ObservableList<Team> oldTeams = FXCollections.observableArrayList();
        private Set<Tag> oldProjectTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        private ProjectEditCommand(Project proj, String newShortName, String newLongName,
                                   String newDescription, ObservableList<Team> newTeams, ArrayList<Tag> newTags) {
            this.proj = proj;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.shortName = newShortName;
            this.longName = newLongName;
            this.description = newDescription;
            this.teams = newTeams;
            this.projectTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldShortName = proj.shortName;
            this.oldLongName = proj.longName;
            this.oldDescription = proj.description;
            this.oldProjectTags.addAll(proj.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes/Redoes the changes of the project edit
         */
        public void execute() {
            proj.shortName = shortName;
            proj.longName = longName;
            proj.description = description;

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a project has to their list of tags
            proj.getTags().clear();
            proj.getTags().addAll(projectTags);

            Collections.sort(Global.currentWorkspace.getProjects());
        }

        /**
         * Undoes the changes of the project edit
         */
        public void undo() {
            proj.shortName = oldShortName;
            proj.longName = oldLongName;
            proj.description = oldDescription;

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the projects list of tags to what they used to be
            proj.getTags().clear();
            proj.getTags().addAll(oldProjectTags);

            Collections.sort(Global.currentWorkspace.getProjects());
        }

        /**
         * Gets the String value of the Command for editting projects.
         */
        public String getString() {
            return "the edit of Project \"" + proj.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_project = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped_project = true;
                }
            }

            //Tag collections
            for (Tag tag : projectTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        projectTags.remove(tag);
                        projectTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldProjectTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldProjectTags.remove(tag);
                        oldProjectTags.add((Tag) item);
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

            return mapped_project;
        }
    }


    /**
     * A command class for allowing the deletion of Projects.
     */
    private class DeleteProjectCommand implements Command {
        private Project proj;

        /**
         * Constructor for the project deletion command.
         * @param proj the project to be deleted.
         */
        DeleteProjectCommand(Project proj) {
            this.proj = proj;
        }

        /**
         * Executes the project deletion command.
         */
        public void execute() {
            Global.currentWorkspace.getProjects().remove(proj);
        }

        /**
         * Undoes the project deletion command.
         */
        public void undo() {
            Global.currentWorkspace.getProjects().add(proj);
        }

        /**
         * Gets the String value of the Command for deleting projects.
         */
        public String getString() {
            return "the deletion of Project \"" + proj.getShortName() + "\"";
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
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped = true;
                }
            }
            return mapped;
        }
    }

    /**
     * A command class for allowing the adding of Releases to Projects.
     */
    private class AddReleaseCommand implements Command {
        private Release release;
        private Project proj;

        /**
         * Constructor for the release addition command.
         * @param proj The project to which the release is being added.
         * @param release The release to be added.
         */
        AddReleaseCommand(Project proj, Release release) {
            this.proj = proj;
            this.release = release;
        }

        /**
         * Executes the release addition command.
         */
        public void execute() {
            proj.getReleases().add(release);
            release.setProject(proj);
        }

        /**
         * Undoes the release addition command.
         */
        public void undo() {
            proj.getReleases().remove(release);
            release.setProject(null);
        }

        /**
         * Gets the String value of the Command for adding releases.
         */
        public String getString() {
            return "the creation of Release \"" + release.getShortName() + "\"";
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
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped = true;
                }
            }
            boolean mapped_release = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(release)) {
                    this.release = (Release) item;
                    mapped_release = true;
                }
            }
            return mapped && mapped_release;
        }
    }

    /**
     * A command class for allowing the addition of Stories to Projects.
     */
    private class AddStoryCommand implements Command {
        private Story story;
        private Project proj;

        /**
         * Constructor for the story addition command.
         * @param proj The project to which the story is to be added.
         * @param story The story to be added.
         */
        AddStoryCommand(Project proj, Story story) {
            this.proj = proj;
            this.story = story;
        }


        /**
         * Executes the story addition command.
         */
        public void execute() {
            proj.getUnallocatedStories().add(story);
            story.setProject(proj);
        }

        /**
         * Undoes the story addition command.
         */
        public void undo() {
            proj.getUnallocatedStories().remove(story);
            story.setProject(null);
        }

        /**
         * Gets the String value of the Command for adding stories.
         */
        public String getString() {
            return "the creation of Story \"" + story.getShortName() + "\"";
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
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped = true;
                }
            }
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            return mapped && mapped_story;
        }
    }

    /**
     * A command class for allowing the addition of Stories to Projects.
     * and stories to a sprint
     */
    private class AddStorySprintCommand implements Command {
        private Story story;
        private Project proj;
        private Sprint sprint;

        /**
         * Constructor for the story addition command.
         * @param proj The project to which the story is to be added.
         * @param story The story to be added.
         * @param sprint The sprint to add the story to
         */
        AddStorySprintCommand(Project proj, Story story, Sprint sprint) {
            this.proj = proj;
            this.story = story;
            this.sprint = sprint;
        }


        /**
         * Executes the story addition command.
         */
        public void execute() {
            proj.getUnallocatedStories().add(story);
            story.setProject(proj);
            sprint.getStories().add(story);
            story.setSprint(sprint);
        }

        /**
         * Undoes the story addition command.
         */
        public void undo() {
            proj.getUnallocatedStories().remove(story);
            story.setProject(null);
            sprint.getStories().remove(story);
            story.setSprint(null);
        }

        /**
         * Gets the String value of the Command for adding stories.
         */
        public String getString() {
            return "the creation of Story \"" + story.getShortName() + "\"";
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
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped = true;
                }
            }
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            boolean mapped_sprint = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(sprint)) {
                    this.sprint = (Sprint) item;
                    mapped_sprint = true;
                }
            }
            return mapped && mapped_story && mapped_sprint;
        }
    }


    /**
     * A command class for allowing the addition of Backlogs to Projects.
     */
    private class AddBacklogCommand implements Command {
        private Backlog backlog;
        private Project proj;

        /**
         * Constructor for the backlog addition command.
         * @param proj The project to which the backlog is to be added.
         * @param backlog The backlog to be added.
         */
        AddBacklogCommand(Project proj, Backlog backlog) {
            this.proj = proj;
            this.backlog = backlog;
        }

        /**
         * Executes the backlog addition command.
         */
        public void execute() {
            proj.getBacklogs().add(backlog);
            backlog.setProject(proj);
        }

        /**
         * Undoes the backlog addition command.
         */
        public void undo() {
            proj.getBacklogs().remove(backlog);
            backlog.setProject(null);
        }

        /**
         * Gets the String value of the Command for adding backlogs.
         */
        public String getString() {
            return "the creation of Backlog \"" + backlog.getShortName() + "\"";
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
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped = true;
                }
            }
            boolean mapped_bl = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(backlog)) {
                    this.backlog = (Backlog) item;
                    mapped_bl = true;
                }
            }
            return mapped && mapped_bl;
        }
    }

    /**
     * A command class for allowing the addition of Sprints to Projects.
     */
    private class AddSprintCommand implements Command {
        private Sprint sprint;
        private Project proj;

        /**
         * Constructor for the sprint addition command.
         * @param proj The project to which the sprint is to be added.
         * @param sprint The sprint to be added.
         */
        AddSprintCommand(Project proj, Sprint sprint) {
            this.proj = proj;
            this.sprint = sprint;
        }

        /**
         * Executes the sprint addition command.
         */
        public void execute() {
            proj.getSprints().add(sprint);
        }

        /**
         * Undoes the sprint addition command.
         */
        public void undo() {
            proj.getSprints().remove(sprint);
        }

        /**
         * Gets the String value of the Command for adding sprints.
         */
        public String getString() {
            return "the creation of Sprint \"" + sprint.getGoal() + "\"";
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
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
                    mapped = true;
                }
            }
            boolean mapped_sp = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(sprint)) {
                    this.sprint = (Sprint) item;
                    mapped_sp = true;
                }
            }
            return mapped && mapped_sp;
        }
    }

    /**
     * A command class for allowing the addition of Allocations to Projects.
     */
    private class AddAllocationCommand implements Command {
        private Project proj;
        private Team team;
        private Allocation allocation;

        /**
         * Constructor for the allocation addition command.
         * @param proj The project to which the team is allocated.
         * @param team The team allocated to the project.
         * @param allocation The allocation to be added.
         */
        AddAllocationCommand(Project proj, Team team, Allocation allocation) {
            this.proj = proj;
            this.team = team;
            this.allocation = allocation;
        }

        /**
         * Executes the allocation addition command.
         */
        public void execute() {
            proj.getTeamAllocations().add(allocation);
            team.getProjectAllocations().add(allocation);
        }

        /**
         * Undoes the allocation addition command.
         */
        public void undo() {
            proj.getTeamAllocations().remove(allocation);
            team.getProjectAllocations().remove(allocation);
        }

        /**
         * Gets the String value of the Command for adding allocations.
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
            boolean mapped = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(proj)) {
                    this.proj = (Project) item;
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
            boolean mapped_alloc = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(allocation)) {
                    this.allocation = (Allocation) item;
                    mapped_alloc = true;
                }
            }
            return mapped && mapped_alloc && mapped_team;
        }
    }

    /**
     * Command to add and remote logs from a project
     */
    private class AddLogsCommand implements Command {
        private Log log;
        private Task task;
        private Project proj;

        /**
         * Constructor for the log addition command
         *
         * @param task       The task to which the log is to be added
         * @param log        The log to be added
         * @param proj       The project of the task
         */
        AddLogsCommand(Task task, Log log, Project proj) {
            this.log = log;
            this.task = task;
            this.proj = proj;

        }

        /**
         * Executes the log addition command
         */
        public void execute() {
            proj.getLogs().add(log);
            task.setEffortSpent(task.getEffortSpent() + log.getDurationInMinutes());
            task.setEffortLeft(task.getEffortLeft() - log.getEffortLeftDifferenceInMinutes());

        }

        /**
         * Undoes the log addition command
         */
        public void undo() {
            task.setEffortSpent(task.getEffortSpent() - log.getDurationInMinutes());
            task.setEffortLeft(task.getEffortLeft() + log.getEffortLeftDifferenceInMinutes());
            proj.getLogs().remove(log);

        }

        /**
         * Gets the String value of the Command for adding logs.
         */
        public String getString() {
            String description = log.getDescription();
            String pairString = "";
            if (description.length() > 40) {
                description = description.substring(0, 40) + "...";
            }
            if (log.getPartner() != null) {
                pairString = " and " + log.getPartner();
            }
            return "creation of Log \"" + description + "\", by " + log.getLogger() + pairString;
        }

        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            //TODO: implement
            return false;
        }
    }

}
