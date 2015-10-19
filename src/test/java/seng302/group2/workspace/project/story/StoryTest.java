/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.workspace.project.story;


import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.conversion.ColorUtils;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.story.acceptanceCriteria.AcceptanceCriteria;
import seng302.group2.workspace.project.story.estimation.EstimationScalesDictionary;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.workspace.Workspace;

import java.util.ArrayList;

/**
 * A series of tests relating to Story
 *
 * @author swi67
 */
public class StoryTest {
    Story story = new Story();
    AcceptanceCriteria ac = new AcceptanceCriteria("requirement", story);
    ArrayList<AcceptanceCriteria> acList = new ArrayList<>();

    /**
     * Test for the story constructors
     */
    @Test
    public void testStoryConstructors() {
        Project project = new Project();
        Assert.assertEquals("Untitled Story", story.getShortName());
        Assert.assertEquals("", story.getDescription());
        Assert.assertEquals("Untitled Story", story.toString());
        Assert.assertEquals(null, story.getCreator());

        Story testStory = new Story("Test Story", "A long Name", "test description",
                "Tyler the Creator", project, 1);
        Assert.assertEquals("Test Story", testStory.getShortName());
        Assert.assertEquals("A long Name", testStory.getLongName());
        Assert.assertEquals("test description", testStory.getDescription());
        Assert.assertEquals(1, testStory.getPriority(), 0);
        Assert.assertEquals("Tyler the Creator", testStory.getCreator());
        Assert.assertEquals("Test Story", testStory.toString());
        Assert.assertEquals(project, testStory.getProject());

    }

    /**
     * Test Story setters
     */
    @Test
    public void testStorySetters() {
        story.setShortName("Test Story");
        story.setLongName("Test Long Name");
        story.setDescription("description");
        story.setPriority(5);
        story.setCreator("Shinobu");

        Assert.assertEquals("Test Story", story.getShortName());
        Assert.assertEquals("Test Long Name", story.getLongName());
        Assert.assertEquals("description", story.getDescription());
        Assert.assertEquals(5, story.getPriority(), 0);
        Assert.assertEquals("Shinobu", story.getCreator());
    }


    @Test
    public void testComparators() {
        Story defaultStory = new Story();

        // Short name comparator
        story.setShortName("a story");
        Assert.assertTrue(0 < Story.StoryNameComparator.compare(story, defaultStory));

        // Priority comparator
        story.setPriority(1);
        defaultStory.setPriority(5);
        Assert.assertTrue(0 < Story.StoryPriorityComparator.compare(story, defaultStory));
    }


    @Test
    public void testDeleteStory() {
        Project proj = new Project();
        Backlog back = new Backlog();
        proj.add(back);

        Story story1 = new Story("short", "long", "desc", "creator", null, 5);
        Story story2 = new Story("short2", "long", "desc", "creator", null, 5);
        story1.setProject(proj);
        story2.setProject(proj);
        story2.setBacklog(back);

        proj.add(story1);
        back.add(story2);

        Assert.assertTrue(back.getStories().contains(story2));
        Assert.assertTrue(proj.getUnallocatedStories().contains(story1));

        story1.deleteStory();
        story2.deleteStory();

        Assert.assertFalse(back.getStories().contains(story2));
        Assert.assertFalse(proj.getUnallocatedStories().contains(story1));

        Assert.assertEquals("the deletion of Story \"" + story2.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Global.commandManager.undo();

        Assert.assertTrue(back.getStories().contains(story2));
        Assert.assertTrue(proj.getUnallocatedStories().contains(story1));
    }


    /**
     * Tests that a story properly prepares for serialization
     */
    @Test
    public void testPrepSerialization() {
        story.getSerializableAc().clear();
        story.getAcceptanceCriteria().clear();

        story.getAcceptanceCriteria().add(ac);
        story.prepSerialization();

        acList.add(ac);

        Assert.assertEquals(acList, story.getSerializableAc());

    }

    /**
     * Tests that a story properly post-pares after deserialization
     */
    @Test
    public void testPostSerialization() {
        story.getSerializableAc().clear();
        story.getAcceptanceCriteria().clear();
        story.getSerializableAc().add(ac);
        story.postDeserialization();

        acList.add(ac);
        Assert.assertEquals(acList, story.getAcceptanceCriteria());
    }


    /**
     * Test undo/redo for adding Acceptance Criteria
     */
    @Test
    public void testAddACs() {
        story.add(ac);
        Assert.assertEquals("the creation of Acceptance Criteria \"" + ac.toString() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Assert.assertTrue(story.getAcceptanceCriteria().contains(ac));
        Global.commandManager.undo();
        Assert.assertTrue(story.getAcceptanceCriteria().size() == 0);
        Global.commandManager.redo();
        Assert.assertTrue(story.getAcceptanceCriteria().contains(ac));
    }


    /**
     * Test Ready and Ready State methods
     */
    @Test
    public void testReadyState() {
        Story story = new Story();
        Assert.assertFalse(story.getReady());
        Assert.assertEquals(Story.stateNotReady, story.getReadyState());

        story.setReady(true);
        Assert.assertTrue(story.getReady());
        Assert.assertEquals(Story.stateReady, story.getReadyState());

        story.setReady(false);
        Assert.assertFalse(story.getReady());
        Assert.assertEquals(Story.stateNotReady, story.getReadyState());
    }


    /**
     * Test AC interaction
     */
    @Test
    public void testAcInteraction() {
        Story story = new Story();
        Assert.assertTrue(story.getAcceptanceCriteria().size() == 0);

        AcceptanceCriteria ac1 = new AcceptanceCriteria("AC1", story);
        AcceptanceCriteria ac2 = new AcceptanceCriteria("AC2", story);

        story.add(ac1);
        story.add(ac2);
        Assert.assertTrue(story.getAcceptanceCriteria().size() == 2);
        Assert.assertTrue(story.getAcceptanceCriteria().contains(ac1) && story.getAcceptanceCriteria().contains(ac2));

        story.delete(ac2);
        Assert.assertTrue(story.getAcceptanceCriteria().size() == 1);
        Assert.assertTrue(story.getAcceptanceCriteria().contains(ac1) && !story.getAcceptanceCriteria().contains(ac2));
    }

    /**
     * Tests for Story XML generator method.
     */
    @Test
    public void testGenerateXML() {
        new ReportGenerator();
        Story story = new Story("short", "long", "desc", "creator", null, 5);
        story.setEstimate("10");

        Element storyElement = story.generateXML();
        Assert.assertEquals("[#text: short]", storyElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: long]", storyElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: desc]", storyElement.getChildNodes().item(3).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: creator]", storyElement.getChildNodes().item(4).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: 5]", storyElement.getChildNodes().item(5).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: 10]", storyElement.getChildNodes().item(6).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Not Ready]", storyElement.getChildNodes().item(7).getChildNodes().item(0).toString());
        Assert.assertEquals(0, storyElement.getChildNodes().item(8).getChildNodes().getLength());
        Assert.assertEquals(11, storyElement.getChildNodes().getLength());

    }

    /**
     * Tests the highlightColourString and getColour methods
     */
    @Test
    public void testColourMethods() {
        Assert.assertEquals("transparent", story.getColour());

        story.setPriority(200);
        Story lowPriorityStory = new Story();
        lowPriorityStory.setPriority(100);
        story.getDependentOnThis().add(lowPriorityStory);
        Assert.assertEquals("transparent", story.getColour()); //red

        story.setPriority(3);
        story.getAcceptanceCriteria().add(ac);
        story.setEstimate(EstimationScalesDictionary.getScaleValue(EstimationScalesDictionary.DefaultValues.NONE));
        Assert.assertEquals(ColorUtils.toRGBCode(Story.orangeHighlight), story.getColour()); // orange

        story.setReady(true);
        Assert.assertEquals(ColorUtils.toRGBCode(Story.greenHighlight), story.getColour()); //green
    }

    @Test
    public void testToRGBCode() {
        Assert.assertEquals("#FF0000FF", ColorUtils.toRGBCode(Color.RED));
        Assert.assertEquals("#008000FF", ColorUtils.toRGBCode(Color.GREEN));
        Assert.assertEquals("#0000FFFF", ColorUtils.toRGBCode(Color.BLUE));
        Assert.assertEquals("#80808080", ColorUtils.toRGBCode(Color.color(128 / 255.0, 128 / 255.0, 128 / 255.0, 128 / 255.0)));
    }


    /**
     * Checks whether cyclic dependency check returns true if a story depends on itself
     * A -&gt; A
     */
    @Test
    public void testHasCyclicDependencies_Single() {
        Story storyA = new Story("A", "Story A", "", "", null, 0);
        storyA.getDependentOn().add(storyA);

        Assert.assertTrue(storyA.hasDependencyCycle());
    }


    /**
     * Checks whether cyclic dependency check returns false for a simple story scenario without cycles
     * A -&gt; B
     */
    @Test
    public void testHasCyclicDependencies_None_Simple() {
        Story storyA, storyB;
        storyA = new Story("A", "Story A", "", "", null, 0);
        storyB = new Story("B", "Story B", "", "", null, 0);
        storyA.getDependentOn().add(storyB);

        Assert.assertFalse(storyA.hasDependencyCycle());
        Assert.assertFalse(storyB.hasDependencyCycle());
    }


    /**
     * Checks whether cyclic dependency check returns false for a story scenario without cycles
     * A -&gt; B -&gt; C -&gt; D
     * A -&gt; C
     * B -&gt; D
     */
    @Test
    public void testHasCyclicDependencies_None_Adv() {
        Story storyA, storyB, storyC, storyD;
        storyA = new Story("A", "Story A", "", "", null, 0);
        storyB = new Story("B", "Story B", "", "", null, 0);
        storyC = new Story("C", "Story C", "", "", null, 0);
        storyD = new Story("D", "Story D", "", "", null, 0);
        storyA.getDependentOn().add(storyB);
        storyB.getDependentOn().add(storyC);
        storyC.getDependentOn().add(storyD);
        storyA.getDependentOn().add(storyC);
        storyB.getDependentOn().add(storyD);

        Assert.assertFalse(storyA.hasDependencyCycle());
        Assert.assertFalse(storyB.hasDependencyCycle());
        Assert.assertFalse(storyC.hasDependencyCycle());
        Assert.assertFalse(storyD.hasDependencyCycle());
    }


    /**
     * Checks whether cyclic dependency check returns expected values for a simple story scenario with cycles
     * A -&gt; B
     * B -&gt; A
     */
    @Test
    public void testHasCyclicDependencies_Simple() {
        Story storyA, storyB;
        storyA = new Story("A", "Story A", "", "", null, 0);
        storyB = new Story("B", "Story B", "", "", null, 0);
        storyA.getDependentOn().add(storyB);
        storyB.getDependentOn().add(storyA);

        Assert.assertTrue(storyA.hasDependencyCycle());
        Assert.assertTrue(storyB.hasDependencyCycle());
    }


    /**
     * Checks whether cyclic dependency check returns expected values for a story scenario with cycles
     * A -&gt; B -&gt; C -&gt; D
     * B -&gt; A
     */
    @Test
    public void testHasCyclicDependencies_Adv() {
        Story storyA, storyB, storyC, storyD;
        storyA = new Story("A", "Story A", "", "", null, 0);
        storyB = new Story("B", "Story B", "", "", null, 0);
        storyC = new Story("C", "Story C", "", "", null, 0);
        storyD = new Story("D", "Story D", "", "", null, 0);
        storyA.getDependentOn().add(storyB);
        storyB.getDependentOn().add(storyC);
        storyC.getDependentOn().add(storyD);
        storyB.getDependentOn().add(storyA);

        Assert.assertTrue(storyA.hasDependencyCycle());
        Assert.assertTrue(storyB.hasDependencyCycle());
        Assert.assertFalse(storyC.hasDependencyCycle());
        Assert.assertFalse(storyD.hasDependencyCycle());
    }

    /**
     * Testing Adding Tasks to a Story.
     */
    @Test
    public void testAddTask() {
        Person aPerson = new Person();
        Project proj = new Project("A new Project", "Proj", "Proj");
        Story story = new Story();

        proj.add(story);
        story.setProject(proj);

        Task task1 = new Task("test task", "", story, aPerson, 0);
        Task task2 = new Task("test task", "", story, aPerson, 0);
        Task task3 = new Task("test task", "", story, aPerson, 0);

        story.add(task1);
        Assert.assertEquals(1, story.getTasks().size());
        Assert.assertEquals("the creation of Task \"" + task1.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertEquals(0, story.getTasks().size());
        Global.commandManager.redo();
        Assert.assertEquals(1, story.getTasks().size());

        story.add(task2);
        Assert.assertEquals(2, story.getTasks().size());
        Global.commandManager.undo();

        story.add(task3);
        Assert.assertEquals(2, story.getTasks().size());
    }

    @Test
    public void testEdit() {
        Global.currentWorkspace = new Workspace();

        Project project = new Project();
        project.setShortName("proj1");
        Project project2 = new Project();
        project2.setShortName("proj2");
        Backlog newBacklog = new Backlog();
        newBacklog.setShortName("a");
        Story story = new Story("short name", "long name", "description", "creator", project, 5);

        Tag tag = new Tag("Tag");
        ArrayList<Tag> newTags = new ArrayList<>();
        newTags.add(tag);

        story.edit("short", "long", "desc", project2, 10, newBacklog, "Estimate", true, null, newTags);

        Assert.assertEquals("short", story.getShortName());
        Assert.assertEquals("long", story.getLongName());
        Assert.assertEquals("desc", story.getDescription());
        Assert.assertEquals("proj2", story.getProject().getShortName());
        Assert.assertEquals(new Integer(10), story.getPriority());
        Assert.assertEquals("a",story.getBacklog().toString());
        Assert.assertEquals("Estimate", story.getEstimate());
        Assert.assertEquals(true, story.getReady());
        Assert.assertEquals(1, story.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", story.getTags().get(0).getName());

        Assert.assertEquals("the edit of Story \"" + story.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertEquals("short name", story.getShortName());
        Assert.assertEquals("long name", story.getLongName());
        Assert.assertEquals("description", story.getDescription());
        Assert.assertEquals("proj1", story.getProject().getShortName());
        Assert.assertEquals(new Integer(5), story.getPriority());
        Assert.assertEquals(null, story.getBacklog());
        Assert.assertEquals(false, story.getReady());
        Assert.assertEquals(0, story.getTags().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Global.commandManager.redo();

        Assert.assertEquals("short", story.getShortName());
        Assert.assertEquals("long", story.getLongName());
        Assert.assertEquals("desc", story.getDescription());
        Assert.assertEquals("proj2", story.getProject().getShortName());
        Assert.assertEquals(new Integer(10), story.getPriority());
        Assert.assertEquals("a",story.getBacklog().toString());
        Assert.assertEquals("Estimate", story.getEstimate());
        Assert.assertEquals(true, story.getReady());
        Assert.assertEquals(1, story.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", story.getTags().get(0).getName());

    }
}



