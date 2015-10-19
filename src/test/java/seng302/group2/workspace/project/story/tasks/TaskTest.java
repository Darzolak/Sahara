package seng302.group2.workspace.project.story.tasks;

import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskTest {
    Task task = new Task();

    /**
     * Test for the task constructors
     */
    @Test
    public  void testTaskConstructors() {
        Story story_ = new Story();
        Assert.assertEquals("Untitled Task", task.getShortName());
        Assert.assertEquals("", task.getDescription());
        Assert.assertEquals("", task.getImpediments());
        Assert.assertEquals("Untitled Task", task.toString());

        Task testTask = new Task("Test Task", "test description", story_, null, 0);
        Assert.assertEquals("Test Task", testTask.getShortName());
        Assert.assertEquals("test description", testTask.getDescription());
        Assert.assertEquals("", testTask.getImpediments());
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, testTask.getState());
        Assert.assertEquals(story_, testTask.getStory());
    }

    /**
     * Test Task setters
     */
    @Test
    public void testTaskSetters() {
        task.setShortName("Test Task");
        task.setDescription("Test Description");
        task.setImpediments("Test Impediments");
        task.setState(Task.TASKSTATE.IN_PROGRESS);
        task.setEffortLeft(604);
        task.setEffortSpent(567);

        Assert.assertEquals("Test Task", task.getShortName());
        Assert.assertEquals("Test Description", task.getDescription());
        Assert.assertEquals("Test Impediments", task.getImpediments());
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getState());
        Assert.assertEquals(604, task.getEffortLeft(), 0);
        Assert.assertEquals("10h 4min", task.getEffortLeftString());
        Assert.assertEquals(567, task.getEffortSpent(), 0);
        Assert.assertEquals("9h 27min", task.getEffortSpentString());
    }

    /**
     * Test for Task's compare method
     */
    @Test
    public void testComparators() {
        Task defaultTask = new Task();

        //Short name comparator
        task.setShortName("a task");
        Assert.assertTrue(0 < Task.TaskNameComparator.compare(task, defaultTask));
    }

    /**
     * Test for task's delete command
     */
    @Test
    public void testDeleteTask() {
        Project project = new Project();
        Backlog backlog = new Backlog();
        Sprint sprint = new Sprint();
        Story story = new Story();
        project.add(backlog);
        backlog.add(story);

        Task task = new Task("short", "desc", story, null, 0);
        Task task2 = new Task("short2", "desc",  null, null, 0);

        story.add(task);
        sprint.getUnallocatedTasks().add(task2);

        Assert.assertTrue(story.getTasks().contains(task));
        Assert.assertTrue(sprint.getUnallocatedTasks().contains(task2));

        task.deleteTask();
        task2.deleteTask();

        Assert.assertFalse(story.getTasks().contains(task));
        Assert.assertFalse(sprint.getUnallocatedTasks().contains(task));
        Assert.assertEquals("the deletion of Task \"" + task2.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertEquals("the deletion of Task \"" + task.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertTrue(story.getTasks().contains(task));
        Assert.assertTrue(sprint.getUnallocatedTasks().contains(task2));
    }


    /**
     * Test Task state methods
     */
    @Test
    public void testTaskStates() {
        Task task = new Task();

        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getState());

        task.setState(Task.TASKSTATE.IN_PROGRESS);
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getState());

        task.setState(Task.TASKSTATE.BLOCKED);
        Assert.assertEquals(Task.TASKSTATE.BLOCKED, task.getState());

        task.setState(Task.TASKSTATE.DONE);
        Assert.assertEquals(Task.TASKSTATE.DONE, task.getState());

        task.setState(Task.TASKSTATE.DEFERRED);
        Assert.assertEquals(Task.TASKSTATE.DEFERRED, task.getState());

    }


    /**
     * Test the lane edit method of tasks
     */
    @Test
    public void testEditLane() {
        Person person = new Person();
        Story story = new Story();

        Task task = new Task("test task", "a task for testing", story, person, 0);
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getState());
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getLane());

        // Make a valid change
        task.editLane(Task.TASKSTATE.IN_PROGRESS, -1, false);
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getState());
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getLane());
        Assert.assertEquals("the edit of Lane on Task \"" + task.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getState());
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getLane());
        Global.commandManager.redo();
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getState());
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getLane());

        // Make an invalid change
        task.editLane(Task.TASKSTATE.BLOCKED, -1, false);
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getState());
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getLane());
        Global.commandManager.undo();
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getState());
        Assert.assertEquals(Task.TASKSTATE.IN_PROGRESS, task.getLane());
        Global.commandManager.undo();
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getState());
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getLane());
    }
    //TODO: shnob
    /**
     * Tests the effort left edit method of tasks
     */
    @Test
    public void testEditEffortLeft() {
        Person person = new Person();
        Story story = new Story();
        Project proj = new Project();
        proj.add(story);
        story.add(task);
        Log log = new Log(task, "", new Person(), new Person(), 60, LocalDateTime.now(), 0);

        Task task = new Task("test task", "a task for testing", story, person, 600);
        Assert.assertEquals(600, task.getEffortLeft(), 0);
        Assert.assertEquals(0, task.getLogs().size());
        Assert.assertEquals(0, task.getLogsWithoutGhostLogs().size());

        proj.add(log);
        task.editEffortLeft(540);
        Assert.assertEquals(540, task.getEffortLeft(), 0);
        Assert.assertEquals(0, task.getLogsWithoutGhostLogs().size());

        Global.commandManager.undo();
        Assert.assertEquals(600, task.getEffortLeft(), 0);
        Assert.assertEquals(0, task.getLogs().size());
        Assert.assertEquals(0, task.getLogsWithoutGhostLogs().size());

        Global.commandManager.redo();
        Assert.assertEquals(540, task.getEffortLeft(), 0);
        Assert.assertEquals(0, task.getLogsWithoutGhostLogs().size());

    }

    /**
     * Test the impediment state edit method of tasks
     */
    @Test
    public void testEditImpedimentState() {
        Person person = new Person();
        Story story = new Story();

        Task task = new Task("test task", "a task for testing", story, person, 0);
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getState());
        Assert.assertEquals("", task.getImpediments());

        task.editImpedimentState(Task.TASKSTATE.BLOCKED, "An impediment");
        Assert.assertEquals(Task.TASKSTATE.BLOCKED, task.getState());
        Assert.assertEquals("An impediment", task.getImpediments());
        Assert.assertEquals("the edit of Impediments on Task \"" + task.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertEquals(Task.TASKSTATE.NOT_STARTED, task.getState());
        Assert.assertEquals("", task.getImpediments());
        Global.commandManager.redo();
        Assert.assertEquals(Task.TASKSTATE.BLOCKED, task.getState());
        Assert.assertEquals("An impediment", task.getImpediments());

        task.editImpedimentState(Task.TASKSTATE.NOT_STARTED, "Another impediment");
        Assert.assertEquals(Task.TASKSTATE.BLOCKED, task.getState());
        Assert.assertEquals("Another impediment", task.getImpediments());
        Global.commandManager.undo();
        Assert.assertEquals(Task.TASKSTATE.BLOCKED, task.getState());
        Assert.assertEquals("An impediment", task.getImpediments());

        task.editImpedimentState(Task.TASKSTATE.DEFERRED, "");
        Assert.assertEquals(Task.TASKSTATE.DEFERRED, task.getState());
        Assert.assertEquals("", task.getImpediments());
    }

    /**
     * Test the Description edit method of tasks
     */
    @Test
    public void testEditDescription() {
        Person person = new Person();
        Story story = new Story();

        Task task = new Task("test task", "", story, person, 0);
        Assert.assertEquals("", task.getDescription());

        task.editDescription("a task for testing");
        Assert.assertEquals("a task for testing", task.getDescription());
        Assert.assertEquals("the edit of Description on Task \"" + task.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertEquals("", task.getDescription());
        Global.commandManager.redo();
        Assert.assertEquals("a task for testing", task.getDescription());

        task.editDescription("A new Description");
        Assert.assertEquals("A new Description", task.getDescription());
        Global.commandManager.undo();
        Assert.assertEquals("a task for testing", task.getDescription());

        task.editDescription("");
        Assert.assertEquals("", task.getDescription());
    }


    /**
     * Test the assignee edit method of tasks
     */
    @Test
    public void testEditAssignee() {
        Person person = new Person();
        Story story = new Story();

        Task task = new Task("test task", "a task for testing", story, person, 0);
        Assert.assertEquals(person, task.getAssignee());

        task.editAssignee(null);
        Assert.assertEquals(null, task.getAssignee());

        task.editAssignee(person);
        Assert.assertEquals(person, task.getAssignee());

        Assert.assertEquals("the edit of Assignee on Task \"" + task.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertEquals(null, task.getAssignee());

        Global.commandManager.redo();
        Assert.assertEquals(person, task.getAssignee());
    }

    //TODO: shnob

    /**
     * Test the general edit method of tasks (Edit scene)
     */
    @Test
    public void testEdit() {
        Person person = new Person();
        Task.TASKSTATE state = Task.TASKSTATE.IN_PROGRESS;
        String impediments = "An impediment";
        List<Log> logs = new ArrayList<>();
        Log log = new Log();
        logs.add(log);
        double effortSpent = 1;
        double effortLeft = 1;

        Person newPerson = new Person();
        Task.TASKSTATE newState = Task.TASKSTATE.VERIFY;
        String newImpediments = "Another impediment";
        List<Log> newLogs = new ArrayList<>();
        Log newLog = new Log();
        newLogs.add(newLog);
        double newEffortSpent = 1.5;
        double newEffortLeft = 0.5;

        Task task = new Task();
        task.edit("test task", "a task for testing", impediments, state, person, logs, effortLeft, effortSpent, null);
        Assert.assertEquals(person, task.getAssignee());
        Assert.assertEquals(impediments, task.getImpediments());
        Assert.assertEquals("test task", task.getShortName());
        Assert.assertEquals("a task for testing", task.getDescription());
        Assert.assertEquals(state, task.getState());
        Assert.assertEquals(effortLeft, task.getEffortLeft(), 0.0001);
        Assert.assertEquals(effortSpent, task.getEffortSpent(), 0.0001);


        task.edit("edited test task", "an edited task for testing", newImpediments, newState, newPerson, newLogs,
                newEffortLeft, newEffortSpent, null);
        Assert.assertEquals(newPerson, task.getAssignee());
        Assert.assertEquals(newImpediments, task.getImpediments());
        Assert.assertEquals("edited test task", task.getShortName());
        Assert.assertEquals("an edited task for testing", task.getDescription());
        Assert.assertEquals(newState, task.getState());
        Assert.assertEquals(newEffortLeft, task.getEffortLeft(), 0.0001);
        Assert.assertEquals(newEffortSpent, task.getEffortSpent(), 0.0001);

        Assert.assertEquals("the edit of Task \"" + task.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertEquals(person, task.getAssignee());
        Assert.assertEquals(impediments, task.getImpediments());
        Assert.assertEquals("test task", task.getShortName());
        Assert.assertEquals("a task for testing", task.getDescription());
        Assert.assertEquals(state, task.getState());
        Assert.assertEquals(effortLeft, task.getEffortLeft(), 0.0001);
        Assert.assertEquals(effortSpent, task.getEffortSpent(), 0.0001);

    }


    /**
     * Test for Task's getLogs method
     */
    @Test
    public void testGetLogs() {
        Workspace workspace = new Workspace();
        Global.currentWorkspace = workspace;
        Person person = new Person();
        Project proj = new Project("", "", "");
        workspace.add(proj);
        Sprint sprint = new Sprint("", "", "", LocalDate.now(), LocalDate.now(), proj, new Team(), new Release());
        Story story = new Story("", "", "", "", proj, 5);
        Task task = new Task("", "", story, person, 0);
        proj.add(sprint);
        sprint.add(story);

        Log log1 = new Log(task, "", person, new Person(), 5, LocalDateTime.now(), 0);
        Log log2 = new Log(task, "", person, new Person(), 5, LocalDateTime.now(), 0);

        proj.add(log1);
        proj.add(log2);

        Assert.assertEquals(2, task.getLogs().size());
        Assert.assertTrue(task.getLogs().contains(log1));
        Assert.assertTrue(task.getLogs().contains(log2));
    }

}