package seng302.group2.workspace.project.story.tasks;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.conversion.ColorUtils;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.tag.Tag;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * The task class.
 * Created by cvs20 on 27/07/15.
 */
public class Task extends SaharaItem implements Serializable {

    /**
     * A comparator that returns the comparison of two story's short names
     */
    public static Comparator<Task> TaskNameComparator = (task1, task2) -> {
        return task1.getShortName().compareTo(task2.getShortName());
    };

    private String shortName = "Untitled Task";
    private String description = "";
    private String impediments = "";
    private TASKSTATE state = TASKSTATE.NOT_STARTED;
    private TASKSTATE lane = TASKSTATE.NOT_STARTED;
    private Story story = null;
    private Person assignee = null;

    private double effortLeft = 0;
    private double effortSpent = 0;
    private Log initialLog;

    /**
     * Returns the items held by the Task
     * @return set of logs associated with the task
     */
    @Override
    public Set<SaharaItem> getItemsSet() {
//        Set<SaharaItem> items = new HashSet<>();
//        for (Log log : this.logs) {
//            items.add(log);
//        }
        return new HashSet<>();
    }

    /**
     * Basic Task constructor
     */
    public Task() {
        super("Untitled Task");
        this.shortName = "Untitled Task";
        this.description = "";
        this.impediments = "";
        this.story = null;
        this.state = TASKSTATE.NOT_STARTED;
        this.effortLeft = 0;
        this.effortSpent = 0;
        this.initialLog = new Log(this, "initial log (this should be hidden)", null, null, 0,
                LocalDateTime.now(), 0 - effortLeft);
//        initListeners();
    }


    /**
     * Returns if a task has been completed
     * @return If the task's lane is the done/completed task state, and the task isn't blocked. (Deferred is fine)
     */
    public boolean completed() {
        return getLane().equals(TASKSTATE.DONE) && !getState().equals(TASKSTATE.BLOCKED);
    }


    /**
     * Basic Task constructor
     * @param shortName The shortname of the Task
     * @param description The description of the task
     * @param story The story of the task
     * @param person The person assigned to the task
     * @param effortLeft The base effort left on the task
     */
    public Task(String shortName, String description, Story story, Person person, double effortLeft) {
        super(shortName);
        this.shortName = shortName;
        this.description = description;
        this.impediments = "";
        this.state = TASKSTATE.NOT_STARTED;
        this.story = story;
        this.effortLeft = effortLeft;
        this.effortSpent = (float) 0;
        this.assignee = person;
        this.initialLog = new Log(this, "initial log (this should be hidden)", null, null, 0,
                LocalDateTime.now(), 0 - effortLeft);
//        initListeners();
    }


//    void initListeners() {
//        this.getLogs().addListener((ListChangeListener<Log>) c -> {
//                Set<Log> logsToRemove = new HashSet<>();
//                for (Log log : getLogsWithoutGhostLogs()) {
//                    if (log.isGhostLog()) {
//                        logsToRemove.add(log);
//                    }
//                }
//                logsWithoutGhosts.removeAll(logsToRemove);
//            });
//    }

    /**
     * Gets the Person assigned to the Task
     * @return the Person assigned
     */
    public Person getPerson() {
        return this.assignee;
    }

    /**
     * Gets the short name of the task
     *
     * @return the short name
     */
    public String getShortName() {
        return this.shortName;
    }

    /**
     * Sets the short name of the task
     *
     * @param shortName short name to be set
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the description of the task
     *
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description of the task
     *
     * @param description description to be set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the state of the current task
     *
     * @return the state as one of the Enum values
     */
    public TASKSTATE getState() {
        return this.state;
    }

    /**
     * Gets the initial effort left on the current task.
     * @return the initial effort left
     */
    public Log getInitialLog() {
        return this.initialLog;
    }

    /**
     * Gets the lane of the current task
     *
     * @return the lane as one of the state Enum values
     */
    public TASKSTATE getLane() {
        return this.lane;
    }

    
    /**
     * Gets the story of the current task
     *
     * @return the story the Task is on
     */
    public Story getStory() {
        return this.story;
    }


    /**
     * Sets the state of the current task
     *
     * @param state state to set the task to
     */
    public void setState(TASKSTATE state) {
        this.state = state;
        if (getStory() != null && getLaneStates().contains(lane)) {
            getStory().addTaskToLane(this);
        }
    }

    /**
     * Gets the impediments of the current task
     *
     * @return the impediments of the current tasks
     */
    public String getImpediments() {
        return this.impediments;
    }

    /**
     * Sets the impediments of the current task
     *
     * @param impediments impediments to add to the task
     */
    public void setImpediments(String impediments) {
        this.impediments = impediments;
    }

    /**
     * Gets the effortLeft of the current task
     *
     * @return the effortLeft of the current task
     */
    public double getEffortLeft() {
        return this.effortLeft;
    }

    /**
     * Sets the impediments of the current task. This should not be used by the user, as it will not effect the graph.
     * Use the TaskEditCommand instead.
     *
     * @param effortLeft effortLeft of the current task
     */
    public void setEffortLeft(double effortLeft) {
        this.effortLeft = effortLeft;
    }

    /**
     * gets the effortleft as a string in hours and minutes
     * @return the effortLeft as a String
     */
    public String getEffortLeftString() {
        return (int) Math.floor(effortLeft / 60) + "h " + (int) Math.floor(effortLeft % 60) + "min";
    }

    /**
     * gets the effortSpent as a string in hours and minutes
     * @return the effortSpent as a String
     */
    public String getEffortSpentString() {
        return (int) Math.floor(effortSpent / 60) + "h " + (int) Math.floor(effortSpent % 60) + "min";
    }

    /**
     * Gets the effortLeft of the current task
     *
     * @return the effortLeft of the current task
     */
    public double getEffortSpent() {
        return this.effortSpent;
    }

    /**
     * Sets the impediments of the current task
     *
     * @param effortSpent effortLeft of the current task
     */
    public void setEffortSpent(double effortSpent) {
        this.effortSpent = effortSpent;
    }

    /**
     * Gets the responsible of a Task
     *
     * @return The Person responsible of the task
     */
    public Person getAssignee() {
        return this.assignee;
    }

    /**
     * Gets the logs associated to this task
     * @return list of logs
     */
    public ObservableList<Log> getLogs() {

        ObservableList<Project> projects = Global.currentWorkspace.getProjects();
        ObservableList<Log> allLogs = observableArrayList();
        ObservableList<Log> taskLogs = observableArrayList();
        for (Project proj : projects) {
            allLogs.addAll(proj.getLogs());
        }
        for (Log log : allLogs) {
            if (log.getTask() == this) {
                taskLogs.add(log);
            }
        }
        return taskLogs;

    }

    /**
     * Gets a list of logs without ghost logs. Ghost logs are logs that
     * are created when the effort left field of a task is manually adjusted.
     * Use this function when displaying logs, the ghost logs should not be visible.
     * @return list of logs without ghostlogs
     */
    public ObservableList<Log> getLogsWithoutGhostLogs() {
        ObservableList<Project> projects = Global.currentWorkspace.getProjects();
        ObservableList<Log> allLogs = observableArrayList();
        ObservableList<Log> taskLogs = observableArrayList();
        for (Project proj : projects) {
            allLogs.addAll(proj.getLogs());
        }
        for (Log log : allLogs) {
            if (log.getTask() == this && !taskLogs.contains(log) && !log.isGhostLog()) {
                taskLogs.add(log);
            }
        }
        return taskLogs;
    }

    /**
     * Prepares a Task to be serialized.
     */
    public void prepSerialization() {
//        serializableLogs.clear();
//        for (Log log : logs) {
//            this.serializableLogs.add(log);
//        }

        prepTagSerialization();
    }


    /**
     * Deserialization post-processing.
     */
    public void postDeserialization() {
//        logs.clear();
//        logs.addAll(serializableLogs);

        postTagDeserialization();
//        initListeners();
    }

    /**
     * Method for creating an XML element for the Task within report generation
     * @return element for XML generation
     */
    @Override
    public Element generateXML() {
//        Element taskElement = ReportGenerator.doc.createElement("task");
//
//        //WorkSpace Elements
//        Element taskID = ReportGenerator.doc.createElement("ID");
//        taskID.appendChild(ReportGenerator.doc.createTextNode(String.valueOf(id)));
//        taskElement.appendChild(taskID);
//
//        Element taskShortName = ReportGenerator.doc.createElement("shortname");
//        taskShortName.appendChild(ReportGenerator.doc.createTextNode(shortName));
//        taskElement.appendChild(taskShortName);
//
//        Element taskDescription = ReportGenerator.doc.createElement("description");
//        taskDescription.appendChild(ReportGenerator.doc.createTextNode(description));
//        taskElement.appendChild(taskDescription);
//
//        Element taskState = ReportGenerator.doc.createElement("state");
//        taskState.appendChild(ReportGenerator.doc.createTextNode(state.toString()));
//        taskElement.appendChild(taskState);
//
//        Element taskAssignee = ReportGenerator.doc.createElement("assignee");
//        if (assignee != null) {
//            taskAssignee.appendChild(ReportGenerator.doc.createTextNode(assignee.toString()));
//        }
//        taskElement.appendChild(taskAssignee);
//
//        Element effortLeftElement = ReportGenerator.doc.createElement("effort-left");
//        effortLeftElement.appendChild(ReportGenerator.doc.createTextNode(Double.toString(effortLeft)));
//        taskElement.appendChild(effortLeftElement);
//
//        Element effortSpentElement = ReportGenerator.doc.createElement("effort-spent");
//        effortSpentElement.appendChild(ReportGenerator.doc.createTextNode(Double.toString(effortSpent)));
//        taskElement.appendChild(effortSpentElement);
//
//        Element taskTagElement = ReportGenerator.doc.createElement("tags");
//        for (Tag tag : this.getTags()) {
//            Element tagElement = tag.generateXML();
//            taskTagElement.appendChild(tagElement);
//        }
//        taskElement.appendChild(taskTagElement);
//
//        return taskElement;
        return null;
    }

    /**
     * An enum for the states of the Task. Also includes a toString method for GUI application of TaskStates
     */
    public enum TASKSTATE {
        // String value, RBGA colour
        NOT_STARTED("Not Started", ColorUtils.toRGBCode(Color.color(0.75, 0.75, 0.75, 1))),
        IN_PROGRESS("In Progress", ColorUtils.toRGBCode(Color.color(0.75, 0.5, 0, 1))),
        VERIFY("Verify", ColorUtils.toRGBCode(Color.color(0.2, 0.2, 0.75, 1))),
        DONE("Done", ColorUtils.toRGBCode(Color.color(0.25, 0.8, 0, 1))),
        BLOCKED("Blocked", ColorUtils.toRGBCode(Color.color(0.8, 0.0, 0, 1))),
        DEFERRED("Deferred", ColorUtils.toRGBCode(Color.color(0.2, 0.2, 0.2, 1)));

        private String value;
        private String colour;

        TASKSTATE(String value, String colour) {
            this.value = value;
            this.colour = colour;
        }

        /**
         * Gets the String value of the Enum.
         * @return The String equivalent of the Enum
         */
        public String getValue() {
            return value;
        }

        /**
         * Overriding toString method
         * @return The String equivalent of the Enum
         */
        @Override
        public String toString() {
            return this.getValue();
        }

        /**
         * Gets the colour string of the status, used mainly on the scrumboard
         * @return The statuses colour
         */
        public String getColourString() {
            return this.colour;
        }
    }


    private static Set<TASKSTATE> laneStates = new HashSet<>();
    public static Collection<TASKSTATE> getLaneStates() {
        Collections.addAll(laneStates, TASKSTATE.NOT_STARTED, TASKSTATE.IN_PROGRESS, TASKSTATE.VERIFY, TASKSTATE.DONE);
        return laneStates;
    }


    private static Set<TASKSTATE> impedingStates = new HashSet<>();
    public static Collection<TASKSTATE> getImpedingStates() {
        Collections.addAll(impedingStates, TASKSTATE.BLOCKED, TASKSTATE.DEFERRED);
        return impedingStates;
    }
    

    /**
     * Deletes the task using the delete command
     */
    public void deleteTask() {
        Command command = new DeleteTaskCommand(this);
        Global.commandManager.executeCommand(command);
    }


    /**
     * Creates a Task edit command and executes it with the Global Command Manager, updating
     * the task with the new parameter values.
     *
     * @param newShortName   The new short name
     * @param newDescription The new description
     * @param newImpediments The new Impediments
     * @param newState       The new state
     * @param newAssignee    The new Assignee
     * @param newLogs        The new Logs
     * @param newEffortLeft  The new effort left
     * @param newEffortSpent The new effort spent
     * @param newTags        The new tags
     */
    public void edit(String newShortName, String newDescription, String newImpediments, TASKSTATE newState,
                     Person newAssignee,  List<Log> newLogs, double newEffortLeft, double newEffortSpent,
                     ArrayList<Tag> newTags) {
        Command taskEdit = new TaskEditCommand(this, newShortName, newDescription, newImpediments,
                newState, newAssignee, newLogs, newEffortLeft, newEffortSpent, newTags);

        Global.commandManager.executeCommand(taskEdit);
    }


    public void editEffortLeft(double newEffortLeft) {
        Log manualEffortLeftLog = new Log(this, "Manual Edit Log", null, null, 0,
                LocalDateTime.now(), this.getEffortLeft() - newEffortLeft);
        manualEffortLeftLog.setGhostLog();
        Command effortLeftEdit = new TaskEditEffortLeftCommand(this, newEffortLeft, manualEffortLeftLog);
        Global.commandManager.executeCommand(effortLeftEdit);
    }
    /**
     * Creates a TaskEditDescription command and executes it with the globa command manager, updating the task
     * with the new parameter values.
     * @param newDescription The new description
     */
    public void editDescription(String newDescription) {
        Command desEdit = new TaskEditDescriptionCommand(this, newDescription);
        Global.commandManager.executeCommand(desEdit);
    }

    /**
     * Creates a Task edit lane state command and executes it with the Global Command Manager, updating
     * the task with the new parameter values.
     *
     * @param newState    The new state
     * @param index The index to add at
     * @param markStoryDone Wether to mark the story done or not
     */
    public void editLane(TASKSTATE newState, int index, boolean markStoryDone) {

        Command relEdit = new TaskEditLaneCommand(this, newState, index, markStoryDone);
        Global.commandManager.executeCommand(relEdit);
    }


    /**
     * Creates a Task edit impediment state command and executes it with the Global Command Manager, updating
     * the task with the new parameter values.
     *
     * @param newState    The new state
     * @param impediments The new impediments
     */
    public void editImpedimentState(TASKSTATE newState, String impediments) {
        Command relEdit = new TaskEditImpedimentStatusCommand(this, newState, impediments);
        Global.commandManager.executeCommand(relEdit);
    }


    /**
     * Creates a Task edit state (lane and impeding state) command and executes it with the Global Command Manager,
     * updating the task with the new parameter values.
     *
     * @param newState    The new state
     */
    public void editState(TASKSTATE newState) {
        Command relEdit = new TaskEditStateCommand(this, newState);
        Global.commandManager.executeCommand(relEdit);
    }


    /**
     * Creates a Task assignee edit command and executes it with the Global Command Manager, updating the task with the
     * new assignee
     *
     * @param newAssignee The new assignee
     */
    public void editAssignee(Person newAssignee) {
        Command relEdit = new TaskEditAssigneeCommand(this, newAssignee);
        Global.commandManager.executeCommand(relEdit);
    }


    /**
     * A command class that allows the executing and undoing of task edits
     */
    private class TaskEditCommand implements Command {
        private Task task;

        private String shortName;
        private String description;
        private String impediments;
        private Person assignee;
        private Collection<Log> logs;
        private TASKSTATE state;
        private TASKSTATE lane;
        private double effortLeft;
        private double effortSpent;
        private Set<Tag> taskTags = new HashSet<>();
        private Set<Tag> globalTags = new HashSet<>();


        private String oldShortName;
        private String oldDescription;
        private String oldImpediments;
        private Person oldAssignee;
        private Collection<Log> oldLogs;
        private TASKSTATE oldState;
        private TASKSTATE oldLane;
        private double oldEffortLeft;
        private double oldEffortSpent;
        private Set<Tag> oldTaskTags = new HashSet<>();
        private Set<Tag> oldGlobalTags = new HashSet<>();
        
        /**
         * Constructor for the Task Edit command.
         * @param task The story to be edited
         * @param newShortName   The new short name
         * @param newDescription The new description
         * @param newImpediments The new Impediments
         * @param newState    The new state
         * @param newAssignee The new Assignee
         * @param newLogs The new Logs
         * @param effortLeft The new effort left
         * @param effortSpent The new effort spent
         * @param newTags The new tags
         */
        private TaskEditCommand(Task task, String newShortName, String newDescription, 
                String newImpediments, TASKSTATE newState, Person newAssignee,  List<Log> newLogs,
                double effortLeft, double effortSpent, ArrayList<Tag> newTags) {

            this.task = task;

            if (newTags == null) {
                newTags = new ArrayList<>();
            }

            this.shortName = newShortName;
            this.description = newDescription;
            this.impediments = newImpediments;
            this.assignee = newAssignee;
            this.logs = new HashSet<>();
            this.logs.addAll(newLogs);
            this.state = newState;
            this.effortLeft = effortLeft;
            this.effortSpent = effortSpent;
            if (getLaneStates().contains(newState)) {
                this.lane = newState;
            }
            this.taskTags.addAll(newTags);
            this.globalTags.addAll(newTags);
            this.globalTags.addAll(Global.currentWorkspace.getAllTags());

            this.oldShortName = task.shortName;
            this.oldDescription = task.description;
            this.oldImpediments = task.impediments;
            this.oldAssignee = task.assignee;
            this.oldLogs = new HashSet<>();
            this.oldLogs.addAll(task.getLogs());
            this.oldState = task.state;
            this.oldEffortLeft = task.effortLeft;
            this.oldEffortSpent = task.effortSpent;
            if (getLaneStates().contains(state)) {
                this.oldLane = state;
            }
            this.oldTaskTags.addAll(task.getTags());
            this.oldGlobalTags.addAll(Global.currentWorkspace.getAllTags());
        }

        /**
         * Executes/Redoes the changes of the task edit
         */
        public void execute() {
            task.shortName = shortName;
            task.description = description;
            task.impediments = impediments;
            task.state = state;
            task.effortLeft = effortLeft;
            task.effortSpent = effortSpent;
            task.lane = lane;

            if (task.getStory() != null && getLaneStates().contains(lane)) {
                task.getStory().addTaskToLane(task);
            }
            
            task.assignee = assignee;

            //Add any created tags to the global collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(globalTags);
            //Add the tags a person has to their list of tags
            task.getTags().clear();
            task.getTags().addAll(taskTags);

//            task.logs.clear();
//            task.logs.addAll(logs);
        }

        /**
         * Undoes the changes of the task edit
         */
        public void undo() {
            task.shortName = oldShortName;
            task.description = oldDescription;
            task.impediments = oldImpediments;
            task.state = oldState;
            task.effortLeft = oldEffortLeft;
            task.effortSpent = oldEffortSpent;
            task.lane = oldLane;

            if (task.getStory() != null && getLaneStates().contains(lane)) {
                task.getStory().addTaskToLane(task);
            }

            task.assignee = oldAssignee;

            //Adds the old global tags to the overall collection
            Global.currentWorkspace.getAllTags().clear();
            Global.currentWorkspace.getAllTags().addAll(oldGlobalTags);

            //Changes the persons list of tags to what they used to be
            task.getTags().clear();
            task.getTags().addAll(oldTaskTags);
//
//            task.logs.clear();
//            task.logs.addAll(oldLogs);
        }

        /**
         * Gets the String value of the Command for Editting of Tasks.
         */
        public String getString() {
            return "the edit of Task \"" + shortName + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_task = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(task)) {
                    this.task = (Task) item;
                    mapped_task = true;
                }
            }

            //Tag collections
            for (Tag tag : taskTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        taskTags.remove(tag);
                        taskTags.add((Tag)item);
                        break;
                    }
                }
            }

            for (Tag tag : oldTaskTags) {
                for (SaharaItem item : stateObjects) {
                    if (item.equivalentTo(tag)) {
                        oldTaskTags.remove(tag);
                        oldTaskTags.add((Tag) item);
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

            return  mapped_task;
        }
    }


    /**
     * A command class that allows the executing and undoing of task edits
     */
    private class TaskEditAssigneeCommand implements Command {
        private Task task;
        private Person assignee;
        private Person oldAssignee;


        /**
         * Constructor for the Task Edit State command, used for changing lanes in the scrumboard
         * @param task The story to be edited
         * @param newAssignee The task's new assignee
         */
        private TaskEditAssigneeCommand(Task task, Person newAssignee) {
            this.task = task;
            this.assignee = newAssignee;
            this.oldAssignee = task.assignee;
        }

        /**
         * Executes/Redoes the changes of the task edit
         */
        public void execute() {
            task.assignee = assignee;
        }

        /**
         * Undoes the changes of the task edit
         */
        public void undo() {
            task.assignee = oldAssignee;
        }

        /**
         * Gets the String value of the Command for editting the assignee of a task.
         */
        public String getString() {
            return "the edit of Assignee on Task \"" + task.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_task = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(task)) {
                    this.task = (Task) item;
                    mapped_task = true;
                }
            }

            return  mapped_task;
        }
    }


    /**
     * A command class that allows the executing and undoing of task state edits
     */
    private class TaskEditStateCommand implements Command {
        private Task task;
        private TASKSTATE state;
        private TASKSTATE oldState;
        private TASKSTATE oldLane;

        /**
         * Constructor for the Task Edit State command, used for changing lanes in the scrumboard
         * @param task The story to be edited
         * @param newState The task's new assignee
         */
        private TaskEditStateCommand(Task task, TASKSTATE newState) {
            this.task = task;
            this.state = newState;
            this.oldState = task.state;
            this.oldLane = task.lane;
        }

        /**
         * Executes/Redoes the changes of the task edit
         */
        public void execute() {
            task.lane = this.state;
            task.state = this.state;
        }

        /**
         * Undoes the changes of the task edit
         */
        public void undo() {
            task.lane = this.oldLane;
            task.state = this.oldState;
        }

        /**
         * Gets the String value of the Command for editting the State of a task.
         */
        public String getString() {
            return "the edit of Task State on Task \"" + task.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_task = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(task)) {
                    this.task = (Task) item;
                    mapped_task = true;
                }
            }

            return  mapped_task;
        }
    }

    /**
     * A command class that allows the executing and undoing of task effort left edits
     */
    private class TaskEditEffortLeftCommand implements Command {
        private Task task;

        private Log effortLeftLog;

        private double effortLeft;

        private double oldEffortLeft;

        /**
         * Constructor for the task edit effort left command, used when manually editing the effort left
         * @param task The task being edited
         * @param effortLeft the new effort left
         * @param effortLeftLog The new effort left log (ghost log)
         */
        private TaskEditEffortLeftCommand(Task task, double effortLeft, Log effortLeftLog) {
            this.task = task;

            this.effortLeftLog = effortLeftLog;

            this.effortLeft = effortLeft;

            this.oldEffortLeft = task.effortLeft;
        }

        /**
         * Executes/Redoes the changes of the task edit
         */
        public void execute() {
            task.effortLeft = effortLeft;
            task.getLogs().add(effortLeftLog);
        }

        /**
         * Undoes the changes of the task edit
         */
        public void undo() {
            task.effortLeft = oldEffortLeft;
            task.getLogs().remove(effortLeftLog);
        }

        /**
         * Gets the String value of the Command for Editting the effortleft of a task.
         */
        public String getString() {
            return "the edit of Effort Left on Task \"" + task.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_task = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(task)) {
                    this.task = (Task) item;
                    mapped_task = true;
                }
            }

            return  mapped_task;
        }
    }

    /**
     * A command class that allows the executing and undoing of task description edits
     */
    private class TaskEditDescriptionCommand implements Command {
        private Task task;

        private String description;

        private String oldDescription;

        /**
         * Constructor for the TaskEditDescriptionCommand.
         * @param task The task to be edited
         * @param description The new description
         */
        private TaskEditDescriptionCommand(Task task, String description) {
            this.task = task;

            this.description = description;

            this.oldDescription = task.description;
        }

        /**
         * Executes / Redos the changes of task description edit
         */
        public void execute() {
            task.description = description;
        }

        /**
         * Undoes the changes of the task description edit
         */
        public void undo() {
            task.description = oldDescription;
        }

        /**
         * Gets the String value of the Command for editting the description of a task.
         */
        public String getString() {
            return "the edit of Description on Task \"" + task.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_task = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(task)) {
                    this.task = (Task) item;
                    mapped_task = true;
                }
            }

            return  mapped_task;
        }
    }



    /**
     * A command class that allows the executing and undoing of task lane edits
     */
    private class TaskEditLaneCommand implements Command {
        private Task task;
        private TASKSTATE lane;
        private TASKSTATE oldLane;
        private TASKSTATE oldState;
        private int index = -1;
        private int oldIndex = -1;
        private boolean storyDone = false;
        private boolean oldStoryDone = false;
        private LocalDate oldEndDate = null;

        /**
         * Constructor for the Task Edit State command, used for changing lanes in the scrumboard
         * @param task The story to be edited
         * @param newLane The new state
         * @param index The index of the lane
         * @param markStoryDone Whether or not the story is to be marked done
         */
        private TaskEditLaneCommand(Task task, TASKSTATE newLane, int index, boolean markStoryDone) {
            this.task = task;
            this.lane = newLane;
            this.oldState = task.state;
            this.oldLane = task.lane;
            this.oldEndDate = task.getStory().getEndDate();

            this.storyDone = markStoryDone;
            this.oldStoryDone = task.getStory().isDone();

            this.index = index;
            this.oldIndex = task.getStory().getTaskLaneIndex(task);
        }

        /**
         * Executes/Redoes the changes of the task edit
         */
        public void execute() {
            if (Task.getLaneStates().contains(lane)) {
                if (!Task.getImpedingStates().contains(task.state)) {
                    // Update the state if it was not an impediment state previously
                    task.state = lane;
                }
                task.lane = lane;
                if (task.getStory() != null) {
                    if (index != -1) {
                        task.getStory().addTaskToLane(task, index);
                    }
                    else {
                        task.getStory().addTaskToLane(task);
                    }
                }
            }
            task.getStory().setDone(storyDone);
            if (lane == TASKSTATE.IN_PROGRESS) {
                if (task.getStory().getStartDate() == null) {
                    task.getStory().setStartDate(LocalDate.now());
                }
            }
            if (storyDone) {
                task.getStory().setEndDate(LocalDate.now());
            }
            else {
                task.getStory().setEndDate(null);
            }
        }


        /**
         * Undoes the changes of the task edit
         */
        public void undo() {
            task.state = oldState;
            task.lane = oldLane;
            if (task.getStory() != null) {
                task.getStory().addTaskToLane(task, oldIndex);
            }
            task.getStory().setDone(oldStoryDone);
            if (storyDone) {
                task.getStory().setEndDate(null);
            }
            else {
                task.getStory().setEndDate(oldEndDate);
            }

            if (lane == TASKSTATE.IN_PROGRESS) {
                if (task.getStory().getStartDate() != null) {
                    task.getStory().setStartDate(null);
                }
            }
        }

        /**
         * Gets the String value of the Command for editting the Lane of a task.
         */
        public String getString() {
            return "the edit of Lane on Task \"" + task.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_task = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(task)) {
                    this.task = (Task) item;
                    mapped_task = true;
                }
            }

            return  mapped_task;
        }
    }


    /**
     * A command class that allows the executing and undoing of task lane edits
     */
    private class TaskEditImpedimentStatusCommand implements Command {
        private Task task;

        private TASKSTATE state;
        private String impediments;

        private TASKSTATE oldState;
        private String oldImpediments;

        /**
         * Constructor for the Task Edit State command, used for changing lanes in the scrumboard
         * @param task The story to be edited
         * @param state    The new state
         * @param impediments The new impediments
         */
        private TaskEditImpedimentStatusCommand(Task task, TASKSTATE state, String impediments) {
            this.task = task;

            this.state = state;
            this.impediments = impediments;

            this.oldState = task.state;
            this.oldImpediments = task.impediments;
        }

        /**
         * Executes/Redoes the changes of the task edit
         */
        public void execute() {
            if (state == null) {
                task.state = task.getLane();
            }
            else if (Task.getImpedingStates().contains(state)) {
                task.state = state;
            }
            task.impediments = impediments;
        }

        /**
         * Undoes the changes of the task edit
         */
        public void undo() {
            task.state = oldState;
            task.impediments = oldImpediments;
        }

        /**
         * Gets the String value of the Command for editting the impediements of a task.
         */
        public String getString() {
            return "the edit of Impediments on Task \"" + task.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_task = false;
            for (SaharaItem item : stateObjects) {
                if (item.equivalentTo(task)) {
                    this.task = (Task) item;
                    mapped_task = true;
                }
            }

            return  mapped_task;
        }
    }
    
    
    /**
     * A class for implementing task deletion in the Command undo/redo structure.
     */
    private class DeleteTaskCommand implements Command {
        private Task task;
        private Story story;
        private Object lane;
        /* If Story == null, assume it's a task without a story as Story is required when creating a task otherwise */
        
        /**
         * Contructor for a task deletion command.
         *
         * @param task The task to delete
         */
        DeleteTaskCommand(Task task) {
            this.task = task;
            this.story = task.getStory();
            if (story != null) {
                if (story.todoTasks.contains(task)) {
                    lane = story.todoTasks;
                }
                else if (story.inProgTasks.contains(task)) {
                    lane = story.inProgTasks;
                }
                else if (story.verifyTasks.contains(task)) {
                    lane = story.verifyTasks;
                }
                else {
                    lane = story.completedTasks;
                }
            }
        }

        /**
         * Executes the deletion of a task.
         */
        public void execute() {
            if (story != null) {
                story.getTasks().remove(task);
                ((ObservableList<Task>) lane).remove(task);
            }
        }

        /**
         * Undoes the deletion of a task.
         */
        public void undo() {
            if (story != null) {
                story.getTasks().add(task);
                ((ObservableList<Task>) lane).add(task);
            }
        }

        /**
         * Gets the String value of the Command for deleting a task.
         */
        public String getString() {
            return "the deletion of Task \"" + task.getShortName() + "\"";
        }

        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
        @Override
        public boolean map(Set<SaharaItem> stateObjects) {
            boolean mapped_task = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(task)) {
                    this.task = (Task) item;
                    mapped_task = true;
                }
            }
            
            boolean mapped_story = false;
            for (SaharaItem item : stateObjects) {
                if (item.equals(story)) {
                    this.story = (Story) item;
                    mapped_story = true;
                }
            }
            
            return mapped_task && mapped_story;
        }
    }


        /**
         * Searches the stateObjects to find an equal model class to map to
         * @param stateObjects A set of objects to search through
         * @return If the item was successfully mapped
         */
//        @Override
//        public boolean map(Set<SaharaItem> stateObjects) {
//            boolean mapped_task = false;
//            for (SaharaItem item : stateObjects) {
//                if (item.equals(task)) {
//                    this.task = (Task) item;
//                    mapped_task = true;
//                }
//            }
//            boolean mapped_log = false;
//            for (SaharaItem item : stateObjects) {
//                if (item.equals(log)) {
//                    this.log = (Log) item;
//                    mapped_log = true;
//                }
//            }
//            return mapped_task && mapped_log;
//        }
    
}
