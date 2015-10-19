package seng302.group2.workspace.project.sprint;


import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;

public class SprintTest {

    /**
     * Test for the Sprint constructors
     */
    @Test
    public void testSprintConstructors() {
        Sprint sprint = new Sprint();
        Project project = new Project();
        Team team = new Team();
        Release release = new Release();

        Assert.assertEquals("Untitled Sprint/Goal", sprint.getGoal());
        Assert.assertEquals("Untitled Sprint/Goal", sprint.getLongName());
        Assert.assertEquals("", sprint.getDescription());
        Assert.assertEquals(null, sprint.getProject());
        Assert.assertEquals(null, sprint.getTeam());
        Assert.assertEquals(null, sprint.getRelease());
        Assert.assertEquals(LocalDate.now(), sprint.getStartDate());
        Assert.assertEquals(LocalDate.now().plusWeeks(2), sprint.getEndDate());

        Sprint testSprint = new Sprint("Goal", "LongName", "Description", LocalDate.now(), LocalDate.now().plusDays(5),
                project, team, release);

        Assert.assertEquals("Goal", testSprint.getGoal());
        Assert.assertEquals("LongName", testSprint.getLongName());
        Assert.assertEquals("Description", testSprint.getDescription());
        Assert.assertEquals(LocalDate.now(), testSprint.getStartDate());
        Assert.assertEquals(LocalDate.now().plusDays(5), testSprint.getEndDate());
        Assert.assertEquals(project, testSprint.getProject());
        Assert.assertEquals(team, testSprint.getTeam());
        Assert.assertEquals(release, testSprint.getRelease());
    }

    /**
     * Testing Creating tasks without a Story.
     */
    @Test
    public void testCreateTasksWithoutStory() {
        Sprint sprint = new Sprint();
        Assert.assertTrue(sprint.getUnallocatedTasksStory() != null);
    }

    /**
     * Tests the prepSerialization method of sprint
     */
    @Test
    public void testPrepSerialization() {
        Sprint sprint = new Sprint();

        Story story = new Story();
        sprint.getStories().add(story);
        sprint.prepSerialization();
        Assert.assertTrue(sprint.getSerializableStories().contains(story));

    }

    /**
     * Tests the postDeserialization method of sprint
     */
    @Test
    public void testPostDeserialization() {
        Sprint sprint  = new Sprint();

        Story story = new Story();
        sprint.getSerializableStories().add(story);
        sprint.postDeserialization();
        Assert.assertTrue(sprint.getStories().contains(story));
        Assert.assertEquals(1, sprint.getStories().size());
    }


    @Test
    /**
     * Test for Sprint's add story command
     */
    public void testAddStory() {
        Sprint sprint = new Sprint();
        Story story = new Story();

        sprint.add(story);
        Assert.assertTrue(sprint.getStories().contains(story));

        Assert.assertEquals("the addition of Story \"" + story.getShortName() + "\" to Sprint \""
                        + sprint.getGoal() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertFalse(sprint.getStories().contains(story));

        Global.commandManager.redo();
        Assert.assertTrue(sprint.getStories().contains(story));
    }

    /**
     * Test for the Sprint's get duration method
     */
    @Test
    public void testGetDuration() {
        Sprint sprint = new Sprint("", "", "", LocalDate.of(2015, 9, 1), LocalDate.of(2015, 9, 10), null, null, null);
        Assert.assertEquals(10, sprint.getDuration(), 0.01);
        Sprint sprint2 = new Sprint("", "", "", LocalDate.of(2015, 9, 1), null, null, null, null);
        long duration = ChronoUnit.DAYS.between(LocalDate.of(2015, 9, 1), LocalDate.now()) + 1;
        Assert.assertEquals(duration, sprint2.getDuration(), 0.05);
    }


    @Test
    /**
     * Test for Sprint's generateXml method
     */
    public void testGenerateXML() {
        new ReportGenerator();
        Sprint sprint = new Sprint("goal", "longname", "description", LocalDate.now(), LocalDate.now().plusDays(5), new Project(), new Team(), new Release());

        Element sprintElement = sprint.generateXML();
        Assert.assertEquals("[#text: goal]", sprintElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: longname]", sprintElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", sprintElement.getChildNodes().item(3).getChildNodes().item(0).toString());


        Assert.assertEquals("[#text: Untitled Project]", sprintElement.getChildNodes().item(6).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Untitled Team]", sprintElement.getChildNodes().item(7).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Untitled Release]", sprintElement.getChildNodes().item(8).getChildNodes().item(0).toString());

        Assert.assertEquals(11, sprintElement.getChildNodes().getLength());

    }


    @Test
    /**
     * Test for Sprint's edit command
     */
    public void testEdit() {
        Global.currentWorkspace = new Workspace();
        Project project = new Project();
        Sprint sprint = new Sprint();
        sprint.setProject(project);
        Collection<Story> oldStories = new ArrayList<>();
        Story oldStory = new Story();
        oldStories.add(oldStory);
        sprint.add(oldStory);

        Team team = new Team();
        Release release = new Release();
        Collection<Story> stories = new ArrayList<>();
        Story story = new Story();
        stories.add(story);

        sprint.edit("goal", "long", "desc", LocalDate.now(), LocalDate.now().plusDays(1), team, release, stories, null);

        Assert.assertEquals("goal", sprint.getGoal());
        Assert.assertEquals("long", sprint.getLongName());
        Assert.assertEquals("desc", sprint.getDescription());
        Assert.assertEquals(LocalDate.now(), sprint.getStartDate());
        Assert.assertEquals(LocalDate.now().plusDays(1), sprint.getEndDate());
        Assert.assertEquals(team, sprint.getTeam());
        Assert.assertEquals(release, sprint.getRelease());
        Assert.assertEquals(stories, sprint.getStories());

        Assert.assertEquals("the edit of Sprint \"" + sprint.getGoal() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertEquals("Untitled Sprint/Goal", sprint.getGoal());
        Assert.assertEquals("Untitled Sprint/Goal", sprint.getLongName());
        Assert.assertEquals("", sprint.getDescription());
        Assert.assertEquals(LocalDate.now(), sprint.getStartDate());
        Assert.assertEquals(LocalDate.now().plusWeeks(2), sprint.getEndDate());
        Assert.assertEquals(null, sprint.getTeam());
        Assert.assertEquals(null, sprint.getRelease());
        Assert.assertEquals(oldStories, sprint.getStories());

        Global.commandManager.redo();

        Assert.assertEquals("goal", sprint.getGoal());
        Assert.assertEquals("long", sprint.getLongName());
        Assert.assertEquals("desc", sprint.getDescription());
        Assert.assertEquals(LocalDate.now(), sprint.getStartDate());
        Assert.assertEquals(LocalDate.now().plusDays(1), sprint.getEndDate());
        Assert.assertEquals(team, sprint.getTeam());
        Assert.assertEquals(release, sprint.getRelease());
        Assert.assertEquals(stories, sprint.getStories());
    }

    @Test
    /**
     * Test for Sprint's delete command
     */
    public void testDeleteSprint() {
        Project project = new Project();
        Sprint sprint = new Sprint("goal", "long", "desc", LocalDate.now(), LocalDate.now().plusDays(1), project, null, null);
        project.add(sprint);
        Global.currentWorkspace.add(project);

        sprint.deleteSprint();
        Assert.assertFalse(project.getSprints().contains(sprint));

        Assert.assertEquals("the deletion of Sprint \"" + sprint.getGoal() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertTrue(project.getSprints().contains(sprint));
    }


    @Test
    /**
     * Tests Sprint's method which returns all the logs associated to it
     */
    public void testGetAllLogs() {
        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;
        Project proj = new Project();
        Sprint sprint = new Sprint();
        Story story = new Story();
        Task task = new Task("", "", story, new Person(), 0);
        Log log = new Log(task, "", new Person(), new Person(), 0, LocalDateTime.now(), 0);
        Log log2 = new Log(task, "", new Person(), new Person(), 0, LocalDateTime.now(), 0);
        log2.setGhostLog();
        Log log3 = new Log(task, "", new Person(), new Person(), 0, LocalDateTime.now(), 0);

        ws.add(proj);
        proj.add(sprint);
        sprint.add(story);
        story.add(task);
        proj.add(log);
        proj.add(log2);
        proj.add(log3);

        Assert.assertEquals(3, sprint.getAllLogs().size());
    }

    @Test
    /**
     * Tests Sprint's method which returns all logs that are not ghost logs.
     */
    public void testGetAllLogsWithInitialLogs() {
        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;
        Project proj = new Project();
        Sprint sprint = new Sprint();
        Story story = new Story();
        Task task = new Task("", "", story, new Person(), 0);
        Log log = new Log(task, "", new Person(), null, 0, LocalDateTime.now(), 0);
        Log log2 = new Log(task, "", new Person(), null, 0, LocalDateTime.now(), 0);
        log2.setGhostLog();
        Log log3 = new Log(task, "", new Person(), new Person(), 0, LocalDateTime.now(), 0);

        ws.add(proj);
        proj.add(sprint);
        sprint.add(story);
        story.add(task);
        proj.add(log);
        proj.add(log2);
        proj.add(log3);

        Assert.assertEquals(2, sprint.getAllLogsWithInitialLogs().size());
    }

    @Test
    /**
     * Tests Sprint's method which returns points per day for a sprint.
     */
    public void testGetPointsPerDay() {
        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;
        Project proj = new Project();
        Team team = new Team();
        Release release = new Release();
        Sprint sprint = new Sprint("", "", "", LocalDate.now().minusWeeks(2), LocalDate.now(), proj, team, release);
        Person codie = new Person();
        Backlog backlog = new Backlog("", "", "", codie, proj, "T-Shirt Sizes");
        Story story = new Story();
        Story story2 = new Story();

        backlog.add(story);
        backlog.add(story2);
        story.setBacklog(backlog);
        story2.setBacklog(backlog);

        story.setEstimate("S");
        story2.setEstimate("4XL");
        story.setDone(true);
        story2.setDone(true);

        ws.add(proj);
        proj.add(sprint);
        sprint.add(story);
        sprint.add(story2);

        int storyPoint = Global.currentWorkspace.getEstimationScales().getFibScaleEquivalent(
                Global.currentWorkspace.getEstimationScales().getEstimationScaleDict().get("T-Shirt Sizes"),
                story.getEstimate());
        int storyPoint2 = Global.currentWorkspace.getEstimationScales().getFibScaleEquivalent(
                Global.currentWorkspace.getEstimationScales().getEstimationScaleDict().get("T-Shirt Sizes"),
                story2.getEstimate());
        double expectedEstimate = (storyPoint + storyPoint2) / sprint.getDuration();

        Assert.assertEquals(expectedEstimate, sprint.getPointsPerDay(), 0.005);
    }

}