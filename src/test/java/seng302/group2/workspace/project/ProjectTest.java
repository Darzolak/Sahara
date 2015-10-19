package seng302.group2.workspace.project;

import javafx.collections.ObservableList;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.allocation.Allocation;
import seng302.group2.workspace.categories.subCategory.project.BacklogCategory;
import seng302.group2.workspace.categories.subCategory.project.ReleaseCategory;
import seng302.group2.workspace.categories.subCategory.project.SprintCategory;
import seng302.group2.workspace.categories.subCategory.project.StoryCategory;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

import static javafx.collections.FXCollections.observableArrayList;


/**
 * A series of tests relating to Projects
 *
 * @author Jordane Lew (jml168)
 */
public class ProjectTest extends TestCase {

    /**
     * Tests that releases are added to projects properly
     */
    @Test
    public void testAddRelease() {
        Project proj = new Project();
        Release release = new Release("test release", proj);
        proj.add(release);

        Assert.assertEquals("the creation of Release \"" + release.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        assertTrue(proj.getReleases().contains(release));

        Global.commandManager.undo();

        assertFalse(proj.getReleases().contains(release));
    }

    /**
     * Tests that sprints are added to projects properly
     */
    @Test
    public void testAddSprint() {
        Project proj = new Project();
        Sprint sprint = new Sprint();
        sprint.setProject(proj);
        proj.add(sprint);

        Assert.assertEquals("the creation of Sprint \"" + sprint.getGoal() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        assertTrue(proj.getSprints().contains(sprint));

        Global.commandManager.undo();

        assertFalse(proj.getSprints().contains(sprint));
    }

    /**
     * Tests that backlogs are added to projects properly
     */
    @Test
    public void testAddBacklog() {
        Project proj = new Project();
        Backlog backlog = new Backlog();
        proj.add(backlog);

        Assert.assertEquals("the creation of Backlog \"" + backlog.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        assertTrue(proj.getBacklogs().contains(backlog));

        Global.commandManager.undo();

        assertFalse(proj.getBacklogs().contains(backlog));
    }

    /**
     * Tests the addition and removal of project's team allocation
     */
    @Test
    public void testAddRemoveTeamAllocation() {
        LocalDate startDate = LocalDate.of(2015, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(2015, Month.JANUARY, 2);
        Project proj = new Project();
        Team team = new Team();

        Allocation allocation = new Allocation(proj, team, startDate, endDate);
        proj.add(allocation);
        Assert.assertTrue(proj.getTeamAllocations().contains(allocation));

        Assert.assertEquals("the creation of an Allocation for \"" + team.getShortName() + "\" on \""
                        + proj.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertFalse(proj.getTeamAllocations().contains(allocation));
    }


    /**
     * Tests the addition and removal of project's backlogs
     */
    @Test
    public void testAddRemoveTeamBacklog() {
        Project proj = new Project();
        Backlog back = new Backlog();

        proj.add(back);
        Assert.assertTrue(proj.getBacklogs().contains(back));

        Global.commandManager.undo();
        Assert.assertFalse(proj.getBacklogs().contains(back));

        Global.commandManager.redo();
    }


    /**
     * Tests the addition and removal of project's team allocation
     */
    @Test
    public void testGetAllocationMethods() {
        LocalDate startDate = LocalDate.now().minusYears(1);
        LocalDate endDate = LocalDate.now().plusYears(1);
        Project proj = new Project("aproj", "a project", "some project");
        Team team = new Team("aTeam", "The A team");
        Allocation currentAllocation = new Allocation(proj, team, startDate, endDate);

        LocalDate startDate2 = LocalDate.now().plusYears(2);
        LocalDate endDate2 = LocalDate.now().plusYears(3);
        Team team2 = new Team("bTeam", "The B team");
        Allocation futureAllocation = new Allocation(proj, team2, startDate2, endDate2);

        LocalDate startDate3 = LocalDate.now().minusYears(3);
        LocalDate endDate3 = LocalDate.now().minusYears(2);
        Team team3 = new Team();
        Allocation pastAllocation = new Allocation(proj, team3, startDate3, endDate3);

        Project anotherProj = new Project("short", "full", "desc");
        Allocation anotherAllocation = new Allocation(anotherProj, team3, startDate3, endDate3);

        Global.currentWorkspace.getTeams().addAll(team, team2, team3);

        proj.add(currentAllocation);
        proj.add(futureAllocation);
        proj.add(pastAllocation);
        proj.add(anotherAllocation);  // Should not add correctly as it has another project as param

        Assert.assertFalse(proj.getTeamAllocations().contains(anotherAllocation));

        Assert.assertTrue(proj.getCurrentTeams().contains(team));
        Assert.assertFalse(proj.getCurrentTeams().contains(team2));

        Assert.assertTrue(proj.getAllTeams().contains(team));
        Assert.assertTrue(proj.getAllTeams().contains(team2));

        Assert.assertFalse(proj.getPastAllocations().contains(currentAllocation));
        Assert.assertFalse(proj.getPastAllocations().contains(futureAllocation));
        Assert.assertTrue(proj.getPastAllocations().contains(pastAllocation));
    }

    /**
     * Tests that a projects children are correct
     */
    @Test
    public void testGetChildren() {
        Project proj = new Project();
        ObservableList<SaharaItem> children = observableArrayList();
        ReleaseCategory releasesCategory = new ReleaseCategory(proj);
        children.add(releasesCategory);
        BacklogCategory backlogCategory = new BacklogCategory(proj);
        children.add(backlogCategory);
        StoryCategory storiesCategory = new StoryCategory(proj);
        children.add(storiesCategory);
        SprintCategory sprintCategory = new SprintCategory(proj);
        children.add(sprintCategory);

        Assert.assertEquals(children, proj.getChildren());

        Release release = new Release("test release", proj);
        proj.add(release);
        Backlog backlog = new Backlog();
        proj.add(backlog);
        Story story = new Story();
        proj.add(story);
        Sprint sprint = new Sprint();
        sprint.setProject(proj);
        proj.add(sprint);

        children.clear();
        releasesCategory = new ReleaseCategory(proj);
        children.add(releasesCategory);
        backlogCategory = new BacklogCategory(proj);
        children.add(backlogCategory);
        storiesCategory = new StoryCategory(proj);
        children.add(storiesCategory);
        sprintCategory = new SprintCategory(proj);
        children.add(sprintCategory);

        Assert.assertEquals(children, proj.getChildren());
    }


    @Test
    public void testDeleteProject() {
        Global.currentWorkspace = new Workspace();
        Project proj = new Project();
        Global.currentWorkspace.add(proj);
        proj.deleteProject();
        Assert.assertFalse(Global.currentWorkspace.getProjects().contains(proj));
        Assert.assertEquals("the deletion of Project \"" + proj.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());

        Global.commandManager.undo();

        Assert.assertTrue(Global.currentWorkspace.getProjects().contains(proj));
    }


    @Test
    public void testAddStory() {
        Project proj = new Project();
        Backlog back = new Backlog();
        Story loneStory = new Story("short", "long", "desc", "creator", null, 3);
        Story backStory = new Story();

        proj.add(back);
        back.add(backStory);
        proj.add(loneStory);

        Assert.assertEquals("the creation of Story \"" + loneStory.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Assert.assertTrue(proj.getUnallocatedStories().contains(loneStory));
        Assert.assertFalse(proj.getUnallocatedStories().contains(backStory));
        Assert.assertTrue(proj.getAllStories().contains(loneStory));
        Assert.assertTrue(proj.getAllStories().contains(backStory));

        Global.commandManager.undo();

        Assert.assertFalse(proj.getAllStories().contains(loneStory));
    }


    /**
     * Tests that a project properly prepares for serialization
     */
    @Test
    public void testPrepSerialization() {
        Project proj = new Project();
        Team testTeam = new Team();
        Release testRelease = new Release();
        Backlog testBacklog = new Backlog();
        Story testStory = new Story();
        Sprint testSprint = new Sprint();
        testSprint.setProject(proj);
        Allocation testAllocation = new Allocation(proj, testTeam, LocalDate.now(),
                LocalDate.now());

        proj.getSerializableReleases().clear();
        proj.getSerializableBacklogs().clear();
        proj.getSerializableStories().clear();
        proj.getSerializableSprints().clear();
        proj.getSerializableTeamAllocations();

        Assert.assertEquals(new ArrayList<Skill>(), proj.getSerializableStories());
        Assert.assertEquals(new ArrayList<Backlog>(), proj.getSerializableBacklogs());
        Assert.assertEquals(new ArrayList<Release>(), proj.getSerializableReleases());
        Assert.assertEquals(new ArrayList<Sprint>(), proj.getSerializableSprints());
        Assert.assertEquals(new ArrayList<Allocation>(), proj.getSerializableTeamAllocations());

        proj.add(testBacklog);
        proj.add(testStory);
        proj.add(testRelease);
        proj.add(testSprint);
        proj.add(testAllocation);

        proj.prepSerialization();

        ArrayList<Story> stories = new ArrayList<>();
        ArrayList<Release> releases = new ArrayList<>();
        ArrayList<Backlog> backlogs = new ArrayList<>();
        ArrayList<Sprint> sprints = new ArrayList<>();
        ArrayList<Allocation> allocations = new ArrayList<>();
        stories.add(testStory);
        releases.add(testRelease);
        backlogs.add(testBacklog);
        sprints.add(testSprint);
        allocations.add(testAllocation);

        Assert.assertEquals(backlogs, proj.getSerializableBacklogs());
        Assert.assertEquals(stories, proj.getSerializableStories());
        Assert.assertEquals(releases, proj.getSerializableReleases());
        Assert.assertEquals(sprints, proj.getSerializableSprints());
        Assert.assertEquals(allocations, proj.getSerializableTeamAllocations());
    }

    /**
     * Tests that a project properly post-pares after deserialization
     */
    @Test
    public void testPostDeserialization() {
        Project proj = new Project();
        Release testRelease = new Release();
        Backlog testBacklog = new Backlog();
        Sprint testSprint = new Sprint();
        Story testStory = new Story();
        Allocation testAllocation = new Allocation(proj, new Team(), LocalDate.now(),
                LocalDate.now());

        proj.getReleases().clear();
        proj.getUnallocatedStories().clear();
        proj.getBacklogs().clear();
        proj.getSprints().clear();
        proj.getTeamAllocations().clear();

        Assert.assertEquals(new ArrayList<Release>(), proj.getReleases());
        Assert.assertTrue(proj.getUnallocatedStories().isEmpty());
        Assert.assertTrue(proj.getBacklogs().isEmpty());
        Assert.assertTrue(proj.getSprints().isEmpty());
        Assert.assertTrue(proj.getTeamAllocations().isEmpty());

        proj.getSerializableReleases().add(testRelease);
        proj.getSerializableStories().add(testStory);
        proj.getSerializableBacklogs().add(testBacklog);
        proj.getSerializableSprints().add(testSprint);
        proj.getSerializableTeamAllocations().add(testAllocation);

        proj.postDeserialization();
        ArrayList<Release> releases = new ArrayList<>();
        releases.add(testRelease);

        Assert.assertEquals(releases, proj.getReleases());
        Assert.assertTrue(proj.getBacklogs().contains(testBacklog));
        Assert.assertTrue(proj.getSprints().contains(testSprint));
        Assert.assertTrue(proj.getUnallocatedStories().contains(testStory));
        Assert.assertTrue(proj.getTeamAllocations().contains(testAllocation));
    }


    /**
     * Tests the compareTo method of Project to ensure it correctly returns an int representing if a
     * shortName is larger or not.
     */
    @Test
    public void testCompareTo() {
        Project proj = new Project("aShortName", "aLongName", "aDescription");
        Project proj2 = new Project("zShortName", "Long Name", "Description");

        Assert.assertTrue(proj.compareTo(proj2) <= 0);
        Assert.assertTrue(proj2.compareTo(proj) >= 0);
        Assert.assertTrue(proj.compareTo(proj) == 0);
    }

    /**
     * Test for Project's edit command
     */
    @Test
    public void testEdit() {
        Global.currentWorkspace = new Workspace();


        Project project = new Project("aShortName", "aLongName", "aDescription");
        Tag tag = new Tag("Tag");
        ArrayList<Tag> newTags = new ArrayList<>();
        newTags.add(tag);
        project.edit("newShortname", "newLongName", "newDescription", null, newTags);

        assertEquals("newShortname", project.getShortName());
        assertEquals("newLongName", project.getLongName());
        assertEquals("newDescription", project.getDescription());
        Assert.assertEquals(1, project.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", project.getTags().get(0).getName());

        Assert.assertEquals("the edit of Project \"" + project.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());

        Global.commandManager.undo();

        assertEquals("aShortName", project.getShortName());
        assertEquals("aLongName", project.getLongName());
        assertEquals("aDescription", project.getDescription());
        Assert.assertEquals(0, project.getTags().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Global.commandManager.redo();

        assertEquals("newShortname", project.getShortName());
        assertEquals("newLongName", project.getLongName());
        assertEquals("newDescription", project.getDescription());
        Assert.assertEquals(1, project.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", project.getTags().get(0).getName());
    }


    /**
     * Test for project when it has all attributes i.e allocation, backlog, team, release and story
     */
    @Test
    public void testGenerateXml() {
        new ReportGenerator();
        Project project = new Project("Test", "Test project", "Used for testing");
        Story story = new Story();
        Team team = new Team("Test team", "test description");
        Person person = new Person();
        Global.currentWorkspace.add(team);
        Allocation allocationNow = new Allocation(project, team, LocalDate.now(), LocalDate.now());
        Allocation prevAllocation = new Allocation(project, team, LocalDate.now().minusDays(1), LocalDate.now().minusDays(1));
        Allocation prevAllocation2 = new Allocation(project, team, LocalDate.now().minusDays(2), LocalDate.now().minusDays(2));
        Allocation futureAllocation = new Allocation(project, team, LocalDate.now().plusDays(2), LocalDate.now().plusDays(2));
        Release release = new Release("Test Release", project);
        Release release2 = new Release("Release 2", project);
        Backlog backlog = new Backlog("Backlog", "Long Backlog name", "Backlog description", person, project, "-");
        project.add(story);
        project.add(release);
        project.add(release2);
        project.add(backlog);
        project.add(allocationNow);
        project.add(prevAllocation);
        project.add(prevAllocation2);
        project.add(futureAllocation);
        project.getChildren().add(release);

        ReportGenerator.generatedItems = project.getChildren();
        ReportGenerator.generatedItems.add(release); //Simulate checked items in generateXml dialog
        ReportGenerator.generatedItems.add(release2);
        ReportGenerator.generatedItems.add(backlog);
        ReportGenerator.generatedItems.add(story);
        ReportGenerator.generatedItems.add(team);
        Element projectElement = project.generateXML();
        Assert.assertEquals("[#text: Test]", projectElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Test project]", projectElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Used for testing]", projectElement.getChildNodes().item(3).getChildNodes().item(0).toString());

        //<current-allocations>
        Assert.assertEquals(1, projectElement.getChildNodes().item(4).getChildNodes().getLength());
        //<previous-allocations>
        Assert.assertEquals(2, projectElement.getChildNodes().item(5).getChildNodes().getLength());
        //<future-allocations>
        Assert.assertEquals(1, projectElement.getChildNodes().item(6).getChildNodes().getLength());
        //<releases>
        Assert.assertEquals(2, projectElement.getChildNodes().item(7).getChildNodes().getLength());
        //<backlog>
        Assert.assertEquals(1, projectElement.getChildNodes().item(8).getChildNodes().getLength());
        //<unassigned-stories>
        Assert.assertEquals(1, projectElement.getChildNodes().item(9).getChildNodes().getLength());
    }


    /**
     * Test for Project's add command
     */
    @Test
    public void testAddLogCommand() {
        Project proj = new Project();
        Story story = new Story();
        Task task = new Task("", "", story, new Person(), 0);
        proj.add(story);
        story.add(task);
        Log log = new Log(task, "", new Person(), new Person(), 10, LocalDateTime.now(), 0);
        proj.add(log);

        Assert.assertTrue(proj.getLogs().contains(log));
        Global.commandManager.undo();
        Assert.assertTrue(proj.getLogs().isEmpty());
    }
}