package seng302.group2.workspace.project.story;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.scenes.sceneswitch.switchStrategies.workspace.project.StoryInformationSwitchStrategy;
import seng302.group2.util.conversion.ColorUtils;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.acceptanceCriteria.AcceptanceCriteria;
import seng302.group2.workspace.project.story.estimation.EstimationScalesDictionary;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.tag.Tag;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * An instance of a user story that is used to describe high-level requirements of a project
 * Created by swi67 on 6/05/15.
 */
public class Story extends SaharaItem implements Serializable {
    /**
     * A comparator that returns the comparison of two story's priorities
     */
    public static Comparator<Story> StoryPriorityComparator = (story1, story2) -> {
        return story2.getPriority().compareTo(story1.getPriority());
    };

    /**
     * A comparator that returns the comparison of two story's short names
     */
    public static Comparator<Story> StoryNameComparator = (story1, story2) -> {
        return story1.getShortName().compareTo(story2.getShortName());
    };

    private String shortName;
    private String longName;
    private String description = "";
    private String creator = "";
    private STORYCOLOUR colour = STORYCOLOUR.DEFAULT;
    private Integer priority = null;
    private Backlog backlog = null;
    private Project project = null;
    private Sprint sprint = null;

    private LocalDate startDate = null;
    private LocalDate endDate = null;

    private boolean done = false;

    private String estimate = EstimationScalesDictionary.getScaleValue(EstimationScalesDictionary.DefaultValues.NONE);
    private boolean ready = false;
    private transient ObservableList<AcceptanceCriteria> acceptanceCriteria = observableArrayList();
    private List<AcceptanceCriteria> serializableAcceptanceCriteria = new ArrayList<>();
    //private Set<Story> dependentOnThis = new HashSet<>();
    private Set<Story> dependentOn = new HashSet<>();
    private transient ObservableList<Task> tasks = observableArrayList();
    private List<Task> serializableTasks = new ArrayList<>();


    // Observable lists for use on the scrumboard
    public transient ObservableList<Task> todoTasks = FXCollections.observableArrayList();
    public transient ObservableList<Task> inProgTasks = FXCollections.observableArrayList();
    public transient ObservableList<Task> verifyTasks = FXCollections.observableArrayList();
    public transient ObservableList<Task> completedTasks = FXCollections.observableArrayList();


    public static String stateReady = "Ready";
    public static String stateNotReady = "Not Ready";

    public static Color greenHighlight = Color.color(0.4, 1, 0, 0.4);
    public static Color orangeHighlight = Color.color(1, 0.6, 0, 0.4);
    public static Color redHighlight = Color.color(1, 0, 0, 0.4);

    // If the story is the story of a sprint representing tasks without a story
    public boolean tasksWithoutStory = false;


    /**
     * Basic Story constructor
     */
    public Story() {
        super("Untitled Story");
        this.shortName = "Untitled Story";
        this.longName = "Untitled Story";
        this.description = "";
        this.creator = null;
        this.priority = 0;
        this.project = null;
        this.colour = STORYCOLOUR.DEFAULT;

        setInformationSwitchStrategy(new StoryInformationSwitchStrategy());
    }


    /**
     * Story Constructor with all fields. Ready state set to false
     *
     * @param shortName   short name to identify the story
     * @param longName    long name
     * @param description description
     * @param creator     creator of the story
     * @param project     project the story belongs to
     * @param priority    the projects priority
     *
     */
    public Story(String shortName, String longName, String description, String creator,
                 Project project, Integer priority) {
        super(shortName);
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.creator = creator;
        this.priority = priority;
        this.project = project;
        this.ready = false;
        this.colour = STORYCOLOUR.DEFAULT;

        setInformationSwitchStrategy(new StoryInformationSwitchStrategy());
    }


    /**
     * Story Constructor for a Sprints' Tasks without a Story, story
     * @param sprint The sprint that this tasks without a story story belongs to
     */
    public Story(Sprint sprint) {
        super("Tasks without a Story");
        this.shortName = "Tasks without a Story";
        this.longName = "Tasks without a Story";
        this.description = "A collection of tasks that do not belong to a story";
        this.creator = "";
        this.priority = 0;
        this.project = sprint.getProject();
        this.sprint = sprint;
        this.ready = true;
        this.colour = STORYCOLOUR.DEFAULT;
        this.tasksWithoutStory = true;
        //System.out.println("story " + this);

        setInformationSwitchStrategy(new StoryInformationSwitchStrategy());
    }


    /**
     * Gets the set of SaharaItems 'belonging' to the Story (It's Acceptance Criteria).
     * @return A set of SaharaItems belonging to the story
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
        Set<SaharaItem> items = new HashSet<>();
        items.addAll(acceptanceCriteria);
        for (AcceptanceCriteria ac : acceptanceCriteria) {
            items.addAll(ac.getItemsSet());
        }
        items.addAll(tasks);
        for (Task task : tasks) {
            items.addAll(task.getItemsSet());
        }
        return items;
    }


    /**
     * Gets the short name of the story
     *
     * @return the short name
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Sets the short name of the story
     *
     * @param shortName short name to be set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the long name of the story
     *
     * @return the long name
     */
    public String getLongName() {
        return this.longName;
    }

    /**
     * Sets the long name of the story
     *
     * @param longName long name to be set
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     * Gets the end date of the story
     *
     * @return end date
     */
    public LocalDate getEndDate() {
        return this.endDate;
    }

    /**
     * Gets the start date of the story
     *
     * @return start date
     */
    public LocalDate getStartDate() {
        return this.startDate;
    }

    /**
     * Sets the end date of the story
     * @param date The stories new end date
     */
    public void setEndDate(LocalDate date) {
        this.endDate = date;
    }

    /**
     * Sets the start date of the story
     * @param date The new LocalStart date of the story
     */
    public void setStartDate(LocalDate date) {
        this.startDate = date;
    }

    /**
     * Gets the description of the story
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the story
     *
     * @param description description to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the person who created the story
     *
     * @return name of the person
     */
    public String getCreator() {
        return this.creator;
    }

    /**
     * Sets the person who created the story
     *
     * @param creator name of the person to be set.
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Gets the priority of the project
     *
     * @return an integer representing the priority
     */
    public Integer getPriority() {
        return this.priority;
    }


    /**
     * Returns whether or not the story has been marked as done
     * @return Whether or not the story has been marked as done
     */
    public boolean isDone() {
        return done;
    }


    /**
     * Sets the story's completed state
     * @param done Whether or not the story should be marked as done
     */
    public void setDone(boolean done) {
        this.done = done;
    }


    /**
     * Sets the priority of the story
     *
     * @param priority the priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Gets the project this story belongs to.
     *
     * @return the project
     */
    public Project getProject() {
        return this.project;
    }

    /**
     * Sets the project of the story
     *
     * @param project the project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Gets the backlog this story belongs to.
     *
     * @return the backlog
     */
    public Backlog getBacklog() {
        return this.backlog;
    }

    /**
     * Gets String representation of the stories ready state.
     *
     * @return the readyString
     */
    public String getReadyString() {
        if (ready) {
            return "Ready";
        }
        return "Not Ready";
    }

    /**
     * Gets the sprint this story belongs to.
     *
     * @return the project
     */
    public Sprint getSprint() {
        return this.sprint;
    }

    /**
     * Returns whether or not all tasks in the story have been marked as being completed
     * @return True if all tasks have been marked as being completed, false otherwise
     */
    public boolean allTasksCompleted() {
        for (Task task : tasks) {
            if (!task.completed()) {
                return false;
            }
        }
        return true;
    }


    /**
     * Returns whether or not all tasks in the story have been marked as being completed
     * @param task The task to be checked
     * @return True if all tasks have been marked as being completed, false otherwise
     */
    public boolean allTasksCompletedExcept(Task task) {
        for (Task task_ : tasks) {
            if (!task_.completed() && !task_.equals(task)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Adds a task to the story's lanes based on it's lane and status
     * @param task The task to add to the lane
     */
    public void addTaskToLane(Task task) {
        //System.out.println("added to lane " + task.getState());
        todoTasks.remove(task);
        inProgTasks.remove(task);
        completedTasks.remove(task);
        verifyTasks.remove(task);

        switch (task.getLane()) {
            case NOT_STARTED:
                todoTasks.add(task);
                break;
            case IN_PROGRESS:
                inProgTasks.add(task);
                break;
            case VERIFY:
                verifyTasks.add(task);
                break;
            case DONE:
                completedTasks.add(task);
                break;
            default:
                break;
        }
    }


    /**
     * Adds a task to the story's lanes based on it's lane and status
     * @param task The task to add to the lane
     * @param index The index at which the task should be added (used for reordering)
     */
    public void addTaskToLane(Task task, int index) {
        todoTasks.remove(task);
        inProgTasks.remove(task);
        completedTasks.remove(task);
        verifyTasks.remove(task);

        if (index == -1) {
            index = 0;
        }

        switch (task.getLane()) {
            case NOT_STARTED:
                if (index <= todoTasks.size() - 1) {
                    todoTasks.add(index, task);
                }
                else {
                    todoTasks.add(task);
                }
                break;
            case IN_PROGRESS:
                if (index <= inProgTasks.size() - 1) {
                    inProgTasks.add(index, task);
                }
                else {
                    inProgTasks.add(task);
                }
                break;
            case VERIFY:
                if (index <= verifyTasks.size() - 1) {
                    verifyTasks.add(index, task);
                }
                else {
                    verifyTasks.add(task);
                }
                break;
            case DONE:
                if (index <= completedTasks.size() - 1) {
                    completedTasks.add(index, task);
                }
                else {
                    completedTasks.add(task);
                }
                break;
            default:
                break;
        }
    }


    /**
     * Tries to finds a task in each of the stories' lanes, and then returns the index of which it sits inside the lane
     * Returns -1 if not found
     * @param task The task to find the index of
     * @return the lane of the task
     */
    public int getTaskLaneIndex(Task task) {
        int index = -1;

        if (todoTasks.contains(task)) {
            index = todoTasks.indexOf(task);
        }
        else if (inProgTasks.contains(task)) {
            index = todoTasks.indexOf(task);
        }
        else if (verifyTasks.contains(task)) {
            index = todoTasks.indexOf(task);
        }
        else if (completedTasks.contains(task)) {
            index = todoTasks.indexOf(task);
        }

        return index;
    }

    /**
     * Sets the sprint the story belongs to
     *
     * @param sprint sprint to set to
     */
    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    /**
     * Gets the stories the current story is dependant of
     *
     * @return the stories, the current story is dependant of
     */
    public Set<Story> getDependentOn() {
        return this.dependentOn;
    }

    /**
     * Sets the highlight colour of the story to its appropriate colour according to the criteria
     */
    public void setHighlightColour() {
        Boolean red = false;
        for (Story story : this.dependentOn) {
            if (story.priority < this.priority) {
                red = true;
            }
        }
        if (red) {
            this.colour = STORYCOLOUR.RED;
        }
        else if (this.ready) {
            this.colour = STORYCOLOUR.GREEN;
        }
        else if (this.estimate.equals(EstimationScalesDictionary.getScaleValue(EstimationScalesDictionary
                .DefaultValues.NONE)) && this.acceptanceCriteria.size() > 0) {
            this.colour = STORYCOLOUR.ORANGE;
        }
        else {
            this.colour = STORYCOLOUR.DEFAULT;
        }
    }

    /**
     * Gets the string form of the story colour t
     * @return the colour of the story in highlight mode
     */
    public String getColour() {
        this.setHighlightColour();
        switch (colour) {
            case GREEN:
                return ColorUtils.toRGBCode(greenHighlight);//"#aaffaa";
            case ORANGE:
                return ColorUtils.toRGBCode(orangeHighlight);
            case RED:
                return ColorUtils.toRGBCode(redHighlight);//"#fd4949";
            case DEFAULT:
                return "transparent";
            default:
                return "transparent";
        }
    }

    /**
     * Gets the dependentOnThis this story has.
     *
     * @return the set of dependentOnThis
     */
    public Set<Story> getDependentOnThis() {
        Set<Story> stories = new HashSet<>();
        for (SaharaItem item : Global.currentWorkspace.getItemsSet()) {
            if (item instanceof Story && ((Story) item).dependentOn.contains(this)) {
                stories.add((Story)item);
            }
        }
        return stories;
    }

    /**
     * Sets the backlog the story belongs to
     *
     * @param backlog the backlog to set to
     */
    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    /**
     * Gets the estimate value of this story
     *
     * @return The estimate value of the story.
     */
    public String getEstimate() {
        return this.estimate;
    }

    /**
     * Sets the estimate value of this story
     *
     * @param estimate The new estimate value
     */
    public void setEstimate(String estimate) {
        this.estimate = estimate;
    }

    /**
     * Gets the ready state of the story
     * @return the ready state
     */
    public boolean getReady() {
        return ready;
    }


    /**
     * Traverses the dependencies of the story (DFS) and returns if there are any cyclic dependencies
     * @return Whether or not the story has cyclic dependencies
     */
    public boolean hasDependencyCycle() {
        // http://www.geeksforgeeks.org/detect-cycle-in-a-graph/
        Set<Story> visited = new HashSet<>();
        Set<Story> backTrack = new HashSet<>();

        // Call the recursive helper function to detect cycle in different DFS trees
        for (Story story : dependentOn) {
            if (recursiveCycleCheck(story, visited, backTrack)) {
                return true;
            }
        }

        return false;
    }


    private boolean recursiveCycleCheck(Story story, Set<Story> visited, Set<Story> backTrack) {
        if (!visited.contains(story)) {
            // Mark the current node as visited and part of recursion (backTrack) stack
            visited.add(story);
            backTrack.add(story);

            // Recursively traverse for all the vertices adjacent to this vertex
            for (Story adjStory : story.dependentOn) {
                if (!visited.contains(adjStory) && recursiveCycleCheck(adjStory, visited, backTrack)) {
                    return true;
                }
                else if (backTrack.contains(adjStory)) {
                    return true;
                }
            }
        }

        backTrack.remove(story);  // remove the vertex from recursion (backTrack) stack
        return false;
    }


    /**
     * Checks if a cycle will be added if a dependency is to be created with the given story added to the stories'
     * dependencies
     * @param story The story to check the addition of. Presumed to have no cycles to start with.
     * @return If the added story adds a cycle to the dependencies of this story.
     */
    public boolean checkAddCycle(Story story) {
        if (this.dependentOn.contains(story)) {
            return true; // Assumed that the story has no cycles to start
        }
        this.dependentOn.add(story);
        boolean cycle = hasDependencyCycle();
        this.dependentOn.remove(story);
        return cycle;
    }

    private boolean cycleCheckTraverse(Set<Story> traversedItems) {
        if (!traversedItems.add(this)) {
            // Item wasn't added to set (duplicate)
            //System.out.println("Can't add " + this + " to " + traversedItems);
            return true;  // There was a cycle
        }
        for (Story dependent : getDependentOn()) {
            dependent.cycleCheckTraverse(traversedItems);
        }
        return false;
    }


    /**
     * A method to return the ready state of a story as a string
     * @return The ready state of a story as a string
     */
    public String getReadyState() {
        if (ready) {
            return stateReady;
        }
        return stateNotReady;
    }

    /**
     * Gets the tasks of this story
     *
     * @return the tasks of the story
     */
    public ObservableList<Task> getTasks() {
        this.serializableTasks.clear();
        for (Object item : this.tasks) {
            this.serializableTasks.add((Task) item);
        }
        return this.tasks;
    }

    /**
     * Gets the serializable tasks
     *
     * @return the serializable tasks
     */
    public List<Task> getSerializableTasks() {
        return serializableTasks;
    }

    /**
     * Adds a task to the story
     *
     * @param task task to be added
     */
    public void add(Task task) {
        Command command = new AddTaskCommand(this, task);
        Global.commandManager.executeCommand(command);
    }
    /**
     * Sets the story's ready state to the given boolean
     * @param ready The boolean state to set
     */
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    /**
     * Gets the acceptance criteria of this story
     *
     * @return the acceptance criteria of the story
     */
    public ObservableList<AcceptanceCriteria> getAcceptanceCriteria() {
        return this.acceptanceCriteria;
    }

    /**
     * Gets the serializable AC's
     *
     * @return the serializable AC's
     */
    public List<AcceptanceCriteria> getSerializableAc() {
        return serializableAcceptanceCriteria;
    }

    /**
     * Adds an Acceptance Criteria to the story
     *
     * @param ac ac to be added
     */
    public void add(AcceptanceCriteria ac) {
        Command command = new AddAcceptanceCriteriaCommand(this, ac);
        Global.commandManager.executeCommand(command);
    }

    /**
     * Prepares a story to be serialized.
     */
    public void prepSerialization() {
        serializableAcceptanceCriteria.clear();
        for (AcceptanceCriteria ac : acceptanceCriteria) {
            this.serializableAcceptanceCriteria.add(ac);
        }
        serializableTasks.clear();
        for (Task task : tasks) {
            task.prepSerialization();
            this.serializableTasks.add(task);
        }

        prepTagSerialization();
    }

    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
        acceptanceCriteria.clear();
        for (Object item : serializableAcceptanceCriteria) {
            this.acceptanceCriteria.add((AcceptanceCriteria) item);
        }
        tasks.clear();
        for (Task task : serializableTasks) {
            task.postDeserialization();
            this.tasks.add(task);
            addTaskToLane(task);
        }

        postTagDeserialization();
    }

    /**
     * Method for creating an XML element for the Story within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
        Element storyElement = ReportGenerator.doc.createElement("story");

        //WorkSpace Elements
        Element storyID = ReportGenerator.doc.createElement("ID");
        storyID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
        storyElement.appendChild(storyID);

        Element storyShortName = ReportGenerator.doc.createElement("identifier");
        storyShortName.appendChild(ReportGenerator.doc.createTextNode(shortName));
        storyElement.appendChild(storyShortName);

        Element storyLongName = ReportGenerator.doc.createElement("long-name");
        storyLongName.appendChild(ReportGenerator.doc.createTextNode(longName));
        storyElement.appendChild(storyLongName);

        Element storyDescription = ReportGenerator.doc.createElement("description");
        storyDescription.appendChild(ReportGenerator.doc.createTextNode(description));
        storyElement.appendChild(storyDescription);

        Element storyCreator = ReportGenerator.doc.createElement("creator");
        storyCreator.appendChild(ReportGenerator.doc.createTextNode(creator));
        storyElement.appendChild(storyCreator);

        Element storyPriority = ReportGenerator.doc.createElement("priority");
        storyPriority.appendChild(ReportGenerator.doc.createTextNode(priority.toString()));
        storyElement.appendChild(storyPriority);

        Element storyEstimate = ReportGenerator.doc.createElement("estimate");
        storyEstimate.appendChild(ReportGenerator.doc.createTextNode(estimate));
        storyElement.appendChild(storyEstimate);

        Element storyReady = ReportGenerator.doc.createElement("ready_state");
        if (ready) {
            storyReady.appendChild(ReportGenerator.doc.createTextNode("Ready"));
        }
        else {
            storyReady.appendChild(ReportGenerator.doc.createTextNode("Not Ready"));
        }
        storyElement.appendChild(storyReady);

        Element acceptanceCriteriaElement = ReportGenerator.doc.createElement("story-acceptance-criteria");
        for (AcceptanceCriteria acceptanceCriteria : this.acceptanceCriteria) {
            Element acElement = acceptanceCriteria.generateXML();
            acceptanceCriteriaElement.appendChild(acElement);
        }
        storyElement.appendChild(acceptanceCriteriaElement);

        Element dependenciesElement = ReportGenerator.doc.createElement("story-dependencies");
        for (Story dependency : this.dependentOn) {
            Element dependencyElement = ReportGenerator.doc.createElement("dependency");
            dependencyElement.appendChild(ReportGenerator.doc.createTextNode(String
                    .valueOf(dependency.getShortName())));
            dependenciesElement.appendChild(dependencyElement);
        }
        storyElement.appendChild(dependenciesElement);

        Element storyTagElement = ReportGenerator.doc.createElement("tags");
        for (Tag tag : this.getTags()) {
            Element tagElement = tag.generateXML();
            storyTagElement.appendChild(tagElement);
        }
        storyElement.appendChild(storyTagElement);

//        Element tasksElement = ReportGenerator.doc.createElement("tasks");
//        for (Task task : this.tasks) {
//            Element taskElement = task.generateXML();
//            tasksElement.appendChild(taskElement);
//        }
//        storyElement.appendChild(tasksElement);

        return storyElement;
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
     * An overridden version for the String representation of a Story
     *
     * @return The short name of the Story
     */
    @Override
    public String toString() {
        return this.shortName;
    }

    /**
     * Deletes the given acceptance criteria from the story
     * @param ac The acceptance criteria to delete
     */
    public void delete(AcceptanceCriteria ac) {
        ac.delete();
    }


    /**
     * Creates a Story edit command and executes it with the Global Command Manager, updating
     * the story with the new parameter values.
     *
     * @param newShortName   The new short name
     * @param newLongName    The new long name
     * @param newDescription The new description
     * @param newProject     The new project
     * @param newPriority    The new priority
     * @param newBacklog     The new backlog
     * @param newEstimate    The new estimate
     * @param newReady       The new ready state
     * @param newDependentOn The new dependent ons
     * @param newTags        The new tags
     */
    public void edit(String newShortName, String newLongName, String newDescription,
                     Project newProject, Integer newPriority, Backlog newBacklog, String newEstimate,
                     boolean newReady, List<Story> newDependentOn, ArrayList<Tag> newTags) {
        Command relEdit = new StoryEditCommand(this, newShortName, newLongName,
                newDescription, newProject, newPriority, newBacklog, newEstimate, newReady, newDependentOn, newTags);

        Global.commandManager.executeCommand(relEdit);
    }

    /**
     * Deletes a story from the given project.
     */
    public void deleteStory() {
        Command command = new DeleteStoryCommand(this);
        Global.commandManager.executeCommand(command);
    }


    /**
     * A command class that allows the executing and undoing of story edits
     */
    private class StoryEditCommand implements Command {
        private Story story;

        private String shortName;
        private String longName;
        private String description;
        private Project project;
        private Integer priority;
        private Backlog backlog;
        private String estimate;
        private boolean ready;
        private Collection<Story> dependentOn;
        private Set<Tag> storyTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();

        private String oldShortName;
        private String oldLongName;
        private String oldDescription;
        private Project oldProject;
        private Integer oldPriority;
        private Backlog oldBacklog;
        private String oldEstimate;
        private boolean oldReady;
        private Collection<Story> oldDependentOn;
        private Set<Tag> oldStoryTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();
        
        /**
         * Constructor for the Story Edit command.
         * @param story The story to be edited
         * @param newShortName The new short name for the story
         * @param newLongName The new long name for the story
         * @param newDescription The new description of the story
         * @param newProject The new project to which the story belongs
         * @param newPriority The new priority value for the story
         * @param newBacklog The new backlog to which the story belongs
         * @param newEstimate The new estimate value for the story
         * @param newReady The story's new readiness state
         * @param newDependentOn The new list of stories the story is dependant on
         * @param newTags        The new tags

         */
        private StoryEditCommand(Story story, String newShortName, String newLongName, String newDescription,
                                 Project newProject, Integer newPriority, Backlog newBacklog, String newEstimate,
                                 boolean newReady, List<Story> newDependentOn, ArrayList<Tag> newTags) {
            this.story = story;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            if (newDependentOn == null) {
                newDependentOn = new ArrayList<>();
            }

            this.shortName = newShortName;
            this.longName = newLongName;
            this.description = newDescription;
            this.project = newProject;
            this.priority = newPriority;
            this.backlog = newBacklog;
            this.estimate = newEstimate;
            this.ready = newReady;
            this.dependentOn = new HashSet<>();
            this.dependentOn.addAll(newDependentOn);
            this.storyTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());


            this.oldShortName = story.shortName;
            this.oldLongName = story.longName;
            this.oldDescription = story.description;
            this.oldPriority = story.priority;
            this.oldProject = story.project;
            this.oldBacklog = story.backlog;
            this.oldEstimate = story.estimate;
            this.oldReady = story.ready;
            this.oldDependentOn = new HashSet<>();
            this.oldDependentOn.addAll(story.dependentOn);
            this.oldStoryTags.addAll(story.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());


            if (backlog == null) {
                estimate = EstimationScalesDictionary.getScaleValue(EstimationScalesDictionary.DefaultValues.NONE);
                ready = false;
            }
            if (estimate.equals(EstimationScalesDictionary.getScaleValue(
                    EstimationScalesDictionary.DefaultValues.NONE))) {
                ready = false;
            }
        }

        /**
         * Executes/Redoes the changes of the story edit
         */
        public void execute() {
            story.shortName = shortName;
            story.longName = longName;
            story.description = description;
            story.priority = priority;
            story.backlog = backlog;
            story.project = project;
            story.estimate = estimate;
            story.ready = ready;


            story.dependentOn.clear();
            story.dependentOn.addAll(dependentOn);

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a story has to their list of tags
            story.getTags().clear();
            story.getTags().addAll(storyTags);

            Collections.sort(project.getUnallocatedStories(), Story.StoryNameComparator);
            if (backlog != null) {
                Collections.sort(backlog.getStories(), Story.StoryPriorityComparator);
            }

            /* If the story if being added to a backlog in the project, remove it from the unassigned stories.*/
            if (backlog != null && project != null) {
                project.getUnallocatedStories().remove(story);
            }
        }

        /**
         * Undoes the changes of the story edit
         */
        public void undo() {
            story.shortName = oldShortName;
            story.longName = oldLongName;
            story.description = oldDescription;
            story.priority = oldPriority;
            story.backlog = oldBacklog;
            story.project = oldProject;
            story.estimate = oldEstimate;
            story.ready = oldReady;


            story.dependentOn.clear();
            story.dependentOn.addAll(oldDependentOn);

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the story's list of tags to what they used to be
            story.getTags().clear();
            story.getTags().addAll(oldStoryTags);

            Collections.sort(project.getUnallocatedStories(), Story.StoryNameComparator);
            Collections.sort(backlog.getStories(), Story.StoryPriorityComparator);

            /* If the story if being added back into a backlog in the project, remove it from the unassigned stories.*/
            if (oldBacklog != null && oldProject != null) {
                oldProject.getUnallocatedStories().remove(story);
            }
        }

        /**
         * Gets the String value of the Command for editting stories.
         */
        public String getString() {
            return "the edit of Story \"" + story.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }

            boolean mapped_project = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(project)) {
                    this.project = (Project) item;
                    mapped_project = true;
                }
            }
            boolean mapped_old_project = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldProject)) {
                    this.project = (Project) item;
                    mapped_old_project = true;
                }
            }

            boolean mapped_backlog = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(backlog)) {
                    this.backlog = (Backlog) item;
                    mapped_backlog = true;
                }
            }
            boolean mapped_old_backlog = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(oldBacklog)) {
                    this.backlog = (Backlog) item;
                    mapped_old_backlog = true;
                }
            }

            //Tag collections
            for (Tag tag : storyTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        storyTags.remove(tag);
                        storyTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldStoryTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldStoryTags.remove(tag);
                        oldStoryTags.add((Tag) item);
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

            return mapped_backlog && mapped_project && mapped_story && mapped_old_backlog
                    && mapped_old_project;
        }
    }

    /**
     * A class for implementing story deletion in the Command undo/redo structure.
     */
    private class DeleteStoryCommand implements Command {
        private Story story;
        private Project proj;
        private Backlog backlog;
        private Sprint sprint;

        /**
         * Contructor for a story deletion command.
         *
         * @param story The story to delete
         */
        DeleteStoryCommand(Story story) {
            this.story = story;
            this.proj = story.getProject();
            this.backlog = story.getBacklog();
            this.sprint = story.getSprint();
        }

        /**
         * Executes the deletion of a story.
         */
        public void execute() {
            if (backlog != null) {
                backlog.getStories().remove(story);
            }
            else if (proj != null) {
                proj.getUnallocatedStories().remove(story);
            }
            if (sprint != null) {
                sprint.getStories().remove(story);
            }
        }

        /**
         * Undoes the deletion of a story.
         */
        public void undo() {
            if (backlog != null) {
                backlog.getStories().add(story);
            }
            else if (proj != null) {
                proj.getUnallocatedStories().add(story);
            }
            if (sprint != null) {
                sprint.getStories().add(story);
            }
        }

        /**
         * Gets the String value of the Command for deleting stories.
         */
        public String getString() {
            return "the deletion of Story \"" + story.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            boolean mapped_project = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(proj)) {
                    this.proj = (Project) item;
                    mapped_project = true;
                }
            }
            boolean mapped_backlog = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(backlog)) {
                    this.backlog = (Backlog) item;
                    mapped_backlog = true;
                }
            }
            
            boolean mapped_sprint = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(sprint)) {
                    this.sprint = (Sprint) item;
                    mapped_sprint = true;
                }
            }
            return mapped_backlog && mapped_project && mapped_story && mapped_sprint;
        }
    }

    /**
     * Command to add and remove acceptance criteria
     */
    private class AddAcceptanceCriteriaCommand implements Command {
        private String commandString;
        private Story story;
        private AcceptanceCriteria ac;

        /**
         * Constructor for the acceptance criteria addition command
         * @param story The story to which the acceptance criteria is to be added
         * @param ac The acceptance criteria to be added
         */
        AddAcceptanceCriteriaCommand(Story story, AcceptanceCriteria ac) {
            this.story = story;
            this.ac = ac;
        }

        /**
         * Executes the acceptance criteria addition command
         */
        public void execute() {
            story.getAcceptanceCriteria().add(ac);
            ac.setStory(story);
        }

        /**
         * Undoes the acceptance criteria addition command
         */
        public void undo() {
            story.getAcceptanceCriteria().remove(ac);
            ac.setStory(null);
        }

        /**
         * Gets the String value of the Command for adding acceptance criteria.
         */
        public String getString() {
            return "the creation of Acceptance Criteria \"" + ac.toString() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            boolean mapped_ac = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(ac)) {
                    this.ac = (AcceptanceCriteria) item;
                    mapped_ac = true;
                }
            }
            return mapped_ac && mapped_story;
        }
    }

    /**
     * Command to add and remove acceptance criteria
     */
    private class AddTaskCommand implements Command {
        private Story story;
        private Task task;

        /**
         * Constructor for the acceptance criteria addition command
         * @param story The story to which the acceptance criteria is to be added
         * @param task The acceptance criteria to be added
         */
        AddTaskCommand(Story story, Task task) {
            this.story = story;
            this.task = task;
            story.addTaskToLane(task);
        }

        /**
         * Executes the acceptance criteria addition command
         */
        public void execute() {
            story.getTasks().add(task);
        }

        /**
         * Undoes the acceptance criteria addition command
         */
        public void undo() {
            story.getTasks().remove(task);
        }

        /**
         * Gets the String value of the Command for adding Tasks.
         */
        public String getString() {
            return "the creation of Task \"" + task.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            boolean mapped_ac = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(task)) {
                    this.task = (Task) item;
                    mapped_ac = true;
                }
            }
            return mapped_ac && mapped_story;
        }
    }

    /**
     * enum used for colour setter
     */
    public enum STORYCOLOUR {
        GREEN,
        ORANGE,
        RED,
        DEFAULT
    }
}
