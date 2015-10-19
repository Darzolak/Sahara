package seng302.group2.workspace.project.sprint;

import javafx.collections.ObservableList;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.project.SprintInformationSwitchStrategy;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A class to hold information around scrum sprints
 */
public class Sprint extends SaharaItem implements Serializable, Comparable<Sprint> {

    private Project project = null;
    private Team team = null;
    private Release release = null;
    private transient ObservableList<Story> stories = observableArrayList();
    private List<Story> serializableStories = new ArrayList<>();

    private String goal = "Untitled Sprint/Goal";
    private String longName = "Untitled Sprint/Goal";
    private String description = "";
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now().plusWeeks(2);
    //private transient ObservableList<Task> unallocatedTasks = observableArrayList();
    //private List<Task> serializableTasks = new ArrayList<>();

    private Story tasksWithoutStory = new Story(this);



    /**
     * Basic constructor
     */
    public Sprint() {
        super("Untitled Sprint");
        setInformationSwitchStrategy(new SprintInformationSwitchStrategy());
        tasksWithoutStory = new Story(this);
    }

    /**
     * Complete Constructor
     *
     * @param longName The name of the sprint
     * @param goal The goal of the sprint
     * @param description The description of the sprint
     * @param startDate The start date of the sprint
     * @param endDate The end date of the sprint
     * @param project The project to which the sprint belongs
     * @param team The team working on the sprint
     * @param release The release to which the sprint is dedicated
     */
    public Sprint(String goal, String longName, String description, LocalDate startDate, LocalDate endDate,
                  Project project, Team team, Release release) {

        this.goal = goal;
        this.longName = longName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
        this.team = team;
        this.release = release;
        tasksWithoutStory = new Story(this);

        setInformationSwitchStrategy(new SprintInformationSwitchStrategy());
    }


    /**
     * Gets the team allocated to the sprint
     * @return The team allocated to the sprint
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Gets the project the sprint belongs to
     * @return The project the sprint belongs to
     */
    public Project getProject() {
        return project;
    }

    /**
     * Gets the Release with which the sprint is associated.
     * @return The release the sprint is assigned
     */
    public Release getRelease() {
        return release;
    }

    /**
     * Gets the story of tasks without a story
     * @return The story of tasks without a story
     */
    public Story getUnallocatedTasksStory() {
        return this.tasksWithoutStory;
    }

    public void setProject(Project proj) {
        this.project = proj;
    }

    /**
     * Gets the sprints list of stories
     *
     * @return The ObservableList of stories
     */
    public ObservableList<Story> getStories() {
        this.serializableStories.clear();
        for (Object item : this.stories) {
            this.serializableStories.add((Story) item);
        }
        return this.stories;
    }

    /**
     * Gets the short name/label of the sprint
     * @return The sprint goal
     */
    public String getGoal() {
        return goal;
    }

    /**
     * Gets the full name of the sprint
     * @return The long name of the sprint
     */
    public String getLongName() {
        return longName;
    }

    /**
     * Gets the description of the sprint
     * @return The description of the sprint
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the start date of the sprint
     * @return The sprint's start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Gets the start date string
     * @return the start date string of sprints
     */
    public String getStartDateString() {
        if (this.getStartDate() == null) {
            return "";
        }
        else {
            try {
                return this.getStartDate().format(Global.dateFormatter);
            }
            catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * Gets the end date of the sprint
     * @return The sprint's end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }


    /**
     * Gets the start date string
     * @return the start date string of sprints
     */
    public String getEndDateString() {
        if (this.getEndDate() == null) {
            return "";
        }
        else {
            try {
                return this.getEndDate().format(Global.dateFormatter);
            }
            catch (Exception e) {
                return "";
            }
        }
    }

    /**
     * Gets the unallocatedTasks of the Sprint
     *
     * @return list of unallocatedTasks
     */
    public ObservableList<Task> getUnallocatedTasks() {
        return this.tasksWithoutStory.getTasks();
        //return this.unallocatedTasks;
    }

    /**
     * Gets all of the tasks within the sprint (unassigned and assigned)
     *
     * @return all of the tasks within the sprint (unassigned and assigned)
     */
    public Set<Task> getAllTasks() {
        Set<Task> tasks = new HashSet<>();
        tasks.addAll(getUnallocatedTasks());
        for (Story story : stories) {
            tasks.addAll(story.getTasks());
        }
        return tasks;
    }

    /**
     * Gets all the logs current within the sprint.
     *
     * @return A list of logs of the tasks within a sprint.
     */
    public List<Log> getAllLogs() {
        ObservableList<Project> projects = Global.currentWorkspace.getProjects();
        ObservableList<Log> allLogs = observableArrayList();
        ObservableList<Log> logList = observableArrayList();
        for (Project proj : projects) {
            allLogs.addAll(proj.getLogs());
        }
        for (Log log : allLogs) {
            if (log.getTask().getStory().getSprint() == this) {
                logList.add(log);
            }
        }

        Collections.sort(logList, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        return logList;
    }

    /**
     * Gets all the logs current within the sprint. The log list includes the initial logs
     * created when a new task is made.
     *
     * @return A list of logs of the tasks within a sprint.
     */
    public ObservableList<Log> getAllLogsWithInitialLogs() {
        ObservableList<Project> projects = Global.currentWorkspace.getProjects();
        ObservableList<Log> allLogs = observableArrayList();
        ObservableList<Log> logList = observableArrayList();
        for (Project proj : projects) {
            allLogs.addAll(proj.getLogs());
        }
        for (Log log : allLogs) {
            if (log.getTask().getStory().getSprint() == this && !log.isGhostLog()) {
                logList.add(log);
            }
        }

        Collections.sort(logList, new Comparator<Log>() {
            @Override
            public int compare(Log o1, Log o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        return logList;
    }

    public double totalEffortLeft() {
        double totalEffort = 0;
        for (Task task : this.getAllTasks()) {
            totalEffort += Math.abs(task.getInitialLog().getEffortLeftDifferenceInMinutes());
        }
        return totalEffort;
    }

    /**
     * Gets the serializable stories belonging to the sprint
     * @return A List of Stories
     */
    public List<Story> getSerializableStories() {
        return serializableStories;
    }

    /**
     * Gets the children of the SaharaItem
     *
     * @return The items of the SaharaItem
     */
    @Override
    public ObservableList<SaharaItem> getChildren() {
        ObservableList<SaharaItem> children = observableArrayList();

        //children.addAll(tasksCategory);
        children.addAll(stories);
        children.add(tasksWithoutStory);

        return children;
    }


    /**
     * Gets the duration of the sprint
     * @return the duration
     */
    public double getDuration() {
        if (endDate != null && endDate.isBefore(LocalDate.now())) {
            return ChronoUnit.DAYS.between(startDate, endDate) + 1;
        }
        else {
            return ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1;
        }

    }

    public double getPointsPerDay() {
        int totalPoints = 0;
        for (Story story : this.getStories()) {
            if (story.isDone()) {
                if (story.getBacklog() != null) {
                    int point = Global.currentWorkspace.getEstimationScales().getFibScaleEquivalent(
                            Global.currentWorkspace.getEstimationScales().getEstimationScaleDict().get(
                                    story.getBacklog().getScale()), story.getEstimate());
                    totalPoints += point;
                }
            }
        }
        double duration = getDuration();
        return totalPoints / duration;
    }

    /**
     * Compares the sprint to another sprint based on their short names
     *
     * @param compareSprint The sprint to compare to
     * @return The string comparison result of the sprint' short names
     */
    @Override
    public int compareTo(Sprint compareSprint) {
        String sprint1ShortName = this.getGoal();
        String sprint2ShortName = compareSprint.getGoal();
        return sprint1ShortName.compareTo(sprint2ShortName);
    }

    /**
     * Adds listeners to the Sprint tasks for sorting
     */
    /*public void addListeners() {
        unallocatedTasks.addListener((ListChangeListener<Task>) change ->
            {
                if (change.next() && !change.wasPermutated()) {
                    Collections.sort(unallocatedTasks, Task.TaskNameComparator);
                }
            });
    }*/

    /**
     * Prepares the backlog to be serialized.
     */
    public void prepSerialization() {
        tasksWithoutStory.prepSerialization();

        serializableStories.clear();
        for (Story story : stories) {
            story.prepSerialization();
            this.serializableStories.add(story);
        }

        /*serializableTasks.clear();
        for (Task item : unallocatedTasks) {
            item.prepSerialization();
            this.serializableTasks.add(item);
        }*/

        prepTagSerialization();
    }


    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
        /*unallocatedTasks.clear();
        for (Task task : serializableTasks) {
            task.postDeserialization();
            this.unallocatedTasks.add(task);
        }*/

        tasksWithoutStory.postDeserialization();
        stories.clear();
        for (Story story : serializableStories) {
            story.postDeserialization();
            this.stories.add(story);
        }

        postTagDeserialization();
        Collections.sort(this.stories, Story.StoryPriorityComparator);
    }


    /**
     * Gets the underlying item set in the hierarchy of sprints
     * @return A blank set (as no <b>new</b> items appear under the sprint hierarchy)
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();

        /*for (Task task : getUnallocatedTasks()) {
            items.addAll(task.getItemsSet());
        }*/
        items.addAll(getUnallocatedTasks());

        return items;
    }

    /**
     * Adds a story to the sprints list of stories
     *
     * @param story Story to add
     */
    public void add(Story story) {
        Command addStory = new AddStoryCommand(this, story);
        Global.commandManager.executeCommand(addStory);
    }


    /**
     * Adds a story to the sprints list of stories and Removes from another sprint.

     * @param newSprint The new sprint for the story to be placed into
     * @param oldSprint The old sprint to remove the story from
     * @param story Story to add
     */
    public void addRemove(Sprint newSprint, Sprint oldSprint, Story story) {
        Command addStory = new AddRemoveStoryCommand(newSprint, oldSprint, story);
        Global.commandManager.executeCommand(addStory);
    }


    /**
     * Method for creating an XML element for the Sprint within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element sprintElement = ReportGenerator.doc.createElement("sprint");

        //WorkSpace Elements
        Element sprintID = ReportGenerator.doc.createElement("ID");
        sprintID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        sprintElement.appendChild(sprintID);

        Element sprintGoal = ReportGenerator.doc.createElement("goal");
        sprintGoal.appendChild(ReportGenerator.doc.createTextNode(goal));
        sprintElement.appendChild(sprintGoal);

        Element sprintLongName = ReportGenerator.doc.createElement("long-name");
        sprintLongName.appendChild(ReportGenerator.doc.createTextNode(longName));
        sprintElement.appendChild(sprintLongName);

        Element sprintDescription = ReportGenerator.doc.createElement("description");
        sprintDescription.appendChild(ReportGenerator.doc.createTextNode(description));
        sprintElement.appendChild(sprintDescription);

        Element sprintStartDate = ReportGenerator.doc.createElement("start-date");
        sprintStartDate.appendChild(ReportGenerator.doc.createTextNode(startDate.toString()));
        sprintElement.appendChild(sprintStartDate);

        Element sprintEndDate = ReportGenerator.doc.createElement("end-date");
        sprintEndDate.appendChild(ReportGenerator.doc.createTextNode(endDate.toString()));
        sprintElement.appendChild(sprintEndDate);

        Element sprintProject = ReportGenerator.doc.createElement("project");
        sprintProject.appendChild(ReportGenerator.doc.createTextNode(project.toString()));
        sprintElement.appendChild(sprintProject);

        Element sprintTeam = ReportGenerator.doc.createElement("team");
        sprintTeam.appendChild(ReportGenerator.doc.createTextNode(team.toString()));
        sprintElement.appendChild(sprintTeam);

        Element sprintRelease = ReportGenerator.doc.createElement("release");
        sprintRelease.appendChild(ReportGenerator.doc.createTextNode(release.toString()));
        sprintElement.appendChild(sprintRelease);

        Element sprintStories = ReportGenerator.doc.createElement("stories");
        for (Story story : getStories()) {
            Element storyElement = story.generateXML();
            sprintStories.appendChild(storyElement);
        }
        sprintElement.appendChild(sprintStories);

        Element sprintTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            sprintTagElement.appendChild(tagElement);
        }
        sprintElement.appendChild(sprintTagElement);

        return sprintElement;
    }


    /**
     * Gets the string representation of a sprint
     * @return The sprint goal/label
     */
    @Override
    public String toString() {
        return goal;
    }

    /**
     * Creates a Sprint edit command and executes it with the Global Command Manager, updating
     * the Backlog with the new parameter values.
     *
     * @param newGoal         The new goal
     * @param newLongName     The new long name
     * @param newDescription  The new description
     * @param newStartDate    The new start date
     * @param newEndDate      The new end date
     * @param newTeam         The new team
     * @param newRelease      The new release
     * @param newStories      The new stories in the backlog
     * @param newTags         The new tags

     */
    public void edit(String newGoal, String newLongName, String newDescription, LocalDate newStartDate,
                     LocalDate newEndDate, Team newTeam, Release newRelease, Collection<Story> newStories,
                    Collection<Tag> newTags) {
        Command relEdit = new SprintEditCommand(this, newGoal, newLongName, newDescription, newStartDate, newEndDate,
                newTeam, newRelease, newStories, newTags);
        Global.commandManager.executeCommand(relEdit);
    }

    /**
     * Deletes a sprint from the given project.
     */
    public void deleteSprint() {
        Command command = new DeleteSprintCommand(this);
        Global.commandManager.executeCommand(command);
    }

    /**
     * A command class that allows the executing and undoing of sprint edits
     */
    private class SprintEditCommand implements Command {
        private Sprint sprint;

        private String goal;
        private String longName;
        private String description;
        private LocalDate startDate;
        private LocalDate endDate;
        private Team team;
        private Release release;
        private Collection<Story> stories = new HashSet<>();
        private Set<Tag> sprintTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private String oldGoal;
        private String oldLongName;
        private String oldDescription;
        private LocalDate oldStartDate;
        private LocalDate oldEndDate;
        private Team oldTeam;
        private Release oldRelease;
        private Collection<Story> oldStories = new HashSet<>();
        private Set<Tag> oldSprintTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();

        private Map<Story, String> oldEstimateDict = new HashMap<>();
        private Map<Story, Boolean> oldReadyStateDict = new HashMap<>();

        private SprintEditCommand(Sprint sprint, String newGoal, String newLongName, String newDescription,
                                  LocalDate newStartDate, LocalDate newEndDate, Team newTeam, Release newRelease,
                                  Collection<Story> newStories, Collection<Tag> newTags) {
            this.sprint = sprint;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.goal = newGoal;
            this.longName = newLongName;
            this.description = newDescription;
            this.startDate = newStartDate;
            this.endDate = newEndDate;
            this.team = newTeam;
            this.release = newRelease;
            this.stories.addAll(newStories);
            this.sprintTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());


            this.oldGoal = sprint.goal;
            this.oldLongName = sprint.longName;
            this.oldDescription = sprint.description;
            this.oldStartDate = sprint.startDate;
            this.oldEndDate = sprint.endDate;
            this.oldTeam = sprint.team;
            this.oldRelease = sprint.release;
            this.oldStories.addAll(sprint.stories);
            this.oldSprintTags.addAll(sprint.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());

            for (Story story : oldStories) {
                oldEstimateDict.put(story, story.getEstimate());
                oldReadyStateDict.put(story, story.getReady());
            }

        }

        /**
         * Executes/Redoes the changes of the sprint edit
         */
        public void execute() {
            sprint.goal = goal;
            sprint.longName = longName;
            sprint.description = description;
            sprint.startDate = startDate;
            sprint.endDate = endDate;
            sprint.team = team;
            sprint.release = release;

            sprint.stories.clear();
            sprint.stories.addAll(stories);

            for (Story story : oldStories) {
                story.setSprint(null);
            }

            for (Story newStory : stories) {
                newStory.setSprint(sprint);
            }

            //sprint.stories.addAll(stories);

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a sprint has to their list of tags
            sprint.getTags().clear();
            sprint.getTags().addAll(sprintTags);

            //Are stories sorted in sprint?
            //Collections.sort(sprint.stories, Story.StoryPriorityComparator);
            Collections.sort(sprint.getProject().getSprints());
        }

        /**
         * Undoes the changes of the backlog edit
         */
        public void undo() {
            sprint.goal = oldGoal;
            sprint.longName = oldLongName;
            sprint.description = oldDescription;
            sprint.startDate = oldStartDate;
            sprint.endDate = oldEndDate;
            sprint.team = oldTeam;
            sprint.release = oldRelease;

            for (Story story : stories) {
                story.setSprint(null);
            }

            sprint.stories.clear();

            for (Story story : oldStories) {
                story.setSprint(sprint);
            }

            System.out.println(oldStories);
            sprint.stories.addAll(oldStories);

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the persons list of tags to what they used to be
            sprint.getTags().clear();
            sprint.getTags().addAll(oldSprintTags);
            //Are stories sorted in sprint?
            //Collections.sort(sprint.stories, Story.StoryPriorityComparator);
            Collections.sort(sprint.getProject().getSprints());
        }

        /**
         * Gets the String value of the Command for editting sprints.
         */
        public String getString() {
            return "the edit of Sprint \"" + sprint.getGoal() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override //
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_sp = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(sprint)) {
                    this.sprint = (Sprint) item;
                    mapped_sp = true;
                }
            }

            boolean mapped_tm = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(team)) {
                    this.team = (Team) item;
                    mapped_tm = true;
                }
            }

            boolean mapped_old_tm = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldTeam)) {
                    this.oldTeam = (Team) item;
                    mapped_old_tm = true;
                }
            }

            boolean mapped_rl = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(release)) {
                    this.release = (Release) item;
                    mapped_rl = true;
                }
            }

            boolean mapped_old_rl = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldRelease)) {
                    this.oldRelease = (Release) item;
                    mapped_old_rl = true;
                }
            }

            // Story collections
            for (Story story : stories) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(story)) {
                        stories.remove(story);
                        stories.add((Story)item);
                        break;
                    }
                }
            }

            for (Story story : oldStories) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(story)) {
                        oldStories.remove(story);
                        oldStories.add((Story)item);
                        break;
                    }
                }
            }

            return mapped_sp &&  mapped_tm && mapped_rl && mapped_old_tm && mapped_old_rl;
        }
    }

    /**
     * A command class for allowing the deletion of Sprints.
     */
    private class DeleteSprintCommand implements Command {
        private Sprint sprint;
        private Project proj;

        /**
         * Constructor for the sprint deletion command.
         * @param sprint The sprint to be deleted.
         */
        DeleteSprintCommand(Sprint sprint) {
            this.sprint = sprint;
            this.proj = sprint.getProject();
        }

        /**
         * Executes the sprint deletion command.
         */
        public void execute() {
            //System.out.println("Exec Sprint Delete");
            proj.getSprints().remove(sprint);
            //release.setProject(null);
        }

        /**
         * Undoes the sprint deletion command.
         */
        public void undo() {
            //System.out.println("Undone Sprint Delete");
            proj.getSprints().add(sprint);
            Collections.sort(sprint.getProject().getSprints());
            //release.setProject(proj);
        }

        /**
         * Gets the String value of the Command for deleting sprints.
         */
        public String getString() {
            return "the deletion of Sprint \"" + sprint.getGoal() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override //
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_sp = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(sprint)) {
                    this.sprint = (Sprint) item;
                    mapped_sp = true;
                }
            }
            return mapped_sp;
        }
    }

    /**
     * A command class for allowing the addition of Stories to Sprints.
     */
    private class AddStoryCommand implements Command {
        private Sprint sprint;
        private Story story;

        /**
         * Constructor for the story addition command.
         * @param sprint The sprint to which the story is to be added.
         * @param story The story to be added.
         */
        AddStoryCommand(Sprint sprint, Story story) {
            //this.proj = proj;
            this.sprint = sprint;
            this.story = story;
        }

        /**
         * Executes the Story addition command
         */
        public void execute() {
            sprint.stories.add(story);
            story.setSprint(sprint);
        }

        /**
         * Undoes the story addition command.
         */
        public void undo() {
            sprint.stories.remove(story);
            story.setSprint(null);
        }

        /**
         * Gets the String value of the Command for adding stories.
         */
        public String getString() {
            return "the addition of Story \"" + story.getShortName() + "\" to Sprint \""
                    + sprint.getGoal() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override //
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_sp = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(sprint)) {
                    this.sprint = (Sprint) item;
                    mapped_sp = true;
                }
            }
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            return mapped_sp && mapped_story;
        }
    }

    /**
     * A command class for moving a Story from one sprint to another.
     */
    private class AddRemoveStoryCommand implements Command {
        private Sprint newSprint;
        private Sprint oldSprint;
        private Story story;

        /**
         * Constructor for the story add/remove command.
         * @param newSprint The sprint to which the story is to be added.
         * @param oldSprint The sprint to which the story is to be added.
         * @param story The story to be added.
         */
        AddRemoveStoryCommand(Sprint newSprint, Sprint oldSprint, Story story) {
            this.oldSprint = oldSprint;
            this.newSprint = newSprint;
            this.story = story;
        }

        /**
         * Executes the Story add/remove command
         */
        public void execute() {
            newSprint.stories.add(story);
            story.setSprint(newSprint);
            oldSprint.stories.remove(story);
        }

        /**
         * Undoes the story add/remove command.
         */
        public void undo() {
            newSprint.stories.remove(story);
            story.setSprint(oldSprint);
            oldSprint.stories.add(story);
        }

        /**
         * Gets the String value of the Command for adding/removing stories.
         */
        public String getString() {
            return "the addition of Story \"" + story.getShortName() + "\" to Sprint \""
                    + newSprint.getGoal() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override //
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_new = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(newSprint)) {
                    this.newSprint = (Sprint) item;
                    mapped_new = true;
                }
            }
            boolean mapped_old = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldSprint)) {
                    this.oldSprint = (Sprint) item;
                    mapped_old = true;
                }
            }
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            return mapped_new && mapped_old && mapped_story;
        }
    }
}
