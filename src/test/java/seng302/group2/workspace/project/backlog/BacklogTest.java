package seng302.group2.workspace.project.backlog;


import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.workspace.Workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A series if tests relating to Backlog
 * Created by cvs20 on 22/05/15.
 */
public class BacklogTest {

    /**
     * Test for the backlog constructors
     */
    @Test
    public void testBacklogConstructors() {
        Backlog backlog = new Backlog();
        Project project = new Project();
        Person codie = new Person();

        Assert.assertEquals("Untitled Backlog", backlog.getShortName());
        Assert.assertEquals("Untitled Backlog", backlog.getLongName());
        Assert.assertEquals("", backlog.getDescription());
        Assert.assertEquals(null, backlog.getProductOwner());
        Assert.assertEquals(null, backlog.getProject());

        Backlog testBacklog = new Backlog("Test Story", "A long name",
                "Test description", codie, project, "ScaleName");
        Assert.assertEquals("Test Story", testBacklog.getShortName());
        Assert.assertEquals("A long name", testBacklog.getLongName());
        Assert.assertEquals("Test description", testBacklog.getDescription());
        Assert.assertEquals(codie, testBacklog.getProductOwner());
        Assert.assertEquals(project, testBacklog.getProject());
        Assert.assertEquals("ScaleName", testBacklog.getScale());
    }

    /**
     * Test backlog setters
     */
    @Test
    public void testBacklogSetters() {
        Backlog backlog = new Backlog();
        Person codie = new Person();
        Project project = new Project();

        backlog.setShortName("Test Backlog");
        backlog.setLongName("Test Long Name");
        backlog.setDescription("Description");
        backlog.setProductOwner(codie);
        backlog.setProject(project);

        Assert.assertEquals("Test Backlog", backlog.getShortName());
        Assert.assertEquals("Test Long Name", backlog.getLongName());
        Assert.assertEquals("Description", backlog.getDescription());
        Assert.assertEquals(codie, backlog.getProductOwner());
        Assert.assertEquals(project, backlog.getProject());
    }

    @Test
    public void testAddStory() {
        Backlog backlog = new Backlog();
        Story story = new Story();

        backlog.add(story);
        Assert.assertEquals("the addition of Story \"" + story.getShortName() + "\" to Backlog \""
                        + backlog.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Assert.assertTrue(backlog.getStories().contains(story));

        Global.commandManager.undo();
        Assert.assertFalse(backlog.getStories().contains(story));

        Global.commandManager.redo();
        Assert.assertTrue(backlog.getStories().contains(story));
    }


    @Test
    public void testGetChildren() {
        Sprint sprint = new Sprint();
        List<Story> exp = observableArrayList();
        exp.add(sprint.getUnallocatedTasksStory());
        Assert.assertEquals(exp, sprint.getChildren());
    }


    @Test
    public void testToString() {
        Backlog backlogDefault = new Backlog();
        Backlog backlog = new Backlog("short", "long", "desc", null, null, "scale");
        Assert.assertEquals("Untitled Backlog", backlogDefault.toString());
        Assert.assertEquals("short", backlog.toString());
    }


    @Test
    public void testCompareTo() {
        Backlog backlogDefault = new Backlog();
        Backlog backlog = new Backlog("short", "long", "desc", null, null, "scale");
        Assert.assertTrue(0 < backlog.compareTo(backlogDefault));
        Assert.assertEquals(30, backlog.compareTo(backlogDefault));
    }


    @Test
    public void testEdit() {
        Global.currentWorkspace = new Workspace();
        Backlog backlog = new Backlog();
        Story oldStory = new Story();
        backlog.add(oldStory);

        Person po = new Person();
        Project proj = new Project();
        Collection<Story> stories = new ArrayList<>();
        Story story = new Story();
        stories.add(story);

        Tag tag = new Tag("Tag");
        ArrayList<Tag> newTags = new ArrayList<>();
        newTags.add(tag);

        backlog.edit("short", "long", "desc", po, proj, "scale", stories, newTags);

        Assert.assertEquals("short", backlog.getShortName());
        Assert.assertEquals("long", backlog.getLongName());
        Assert.assertEquals("desc", backlog.getDescription());
        Assert.assertEquals(po, backlog.getProductOwner());
        Assert.assertEquals(proj, backlog.getProject());
        Assert.assertEquals("scale", backlog.getScale());
        Assert.assertEquals(stories, backlog.getStories());
        Assert.assertTrue(story.getBacklog() == backlog);
        Assert.assertFalse(oldStory.getBacklog() == backlog);
        Assert.assertEquals(1, backlog.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", backlog.getTags().get(0).getName());
        Assert.assertEquals("the edit of Backlog \"" + backlog.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());

        Global.commandManager.undo();

        Assert.assertEquals("Untitled Backlog", backlog.getShortName());
        Assert.assertEquals("Untitled Backlog", backlog.getLongName());
        Assert.assertEquals("", backlog.getDescription());
        Assert.assertEquals(null, backlog.getProductOwner());
        Assert.assertEquals(null, backlog.getProject());
        Assert.assertEquals("Fibonacci", backlog.getScale());
        Assert.assertTrue(backlog.getStories().contains(oldStory));
        Assert.assertEquals(0, backlog.getTags().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Assert.assertEquals(1, backlog.getStories().size());
        Assert.assertFalse(story.getBacklog() == backlog);
        Assert.assertTrue(oldStory.getBacklog() == backlog);

        Global.commandManager.redo();

        Assert.assertEquals("short", backlog.getShortName());
        Assert.assertEquals("long", backlog.getLongName());
        Assert.assertEquals("desc", backlog.getDescription());
        Assert.assertEquals(po, backlog.getProductOwner());
        Assert.assertEquals(proj, backlog.getProject());
        Assert.assertEquals("scale", backlog.getScale());
        Assert.assertEquals(stories, backlog.getStories());
        Assert.assertTrue(story.getBacklog() == backlog);
        Assert.assertFalse(oldStory.getBacklog() == backlog);
        Assert.assertEquals(1, backlog.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", backlog.getTags().get(0).getName());
    }


    @Test
    public void testDeleteBacklog() {
        Backlog backlog = new Backlog();
        Project project = new Project();
        project.add(backlog);
        Global.currentWorkspace.add(project);

        backlog.deleteBacklog();
        Assert.assertFalse(project.getBacklogs().contains(backlog));
        Assert.assertNotEquals(project, backlog.getProject());

        Assert.assertEquals("the deletion of Backlog \"" + backlog.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertTrue(project.getBacklogs().contains(backlog));
        Assert.assertEquals(project, backlog.getProject());
    }


    /**
     * Tests both the prepSerialization and postDeserialization methods
     */
    @Test
    public void testSerializationMethods() {
        Backlog backlog = new Backlog();

        Story story = new Story();
        backlog.getStories().add(story);
        backlog.prepSerialization();
        Assert.assertTrue(backlog.getSerializableStories().contains(story));

        Story story2 = new Story();
        backlog.getSerializableStories().add(story2);
        backlog.postDeserialization();
        Assert.assertTrue(backlog.getStories().contains(story2));
        Assert.assertEquals(2, backlog.getStories().size());
    }

    /**
     * Tests for Backlogs' XML generator method for when the backlog doesn't have any stories.
     */
    @Test
    public void testGenerateXML() {
        new ReportGenerator();
        Backlog backlog = new Backlog("shortname", "longname", "description", new Person(), new Project(), "thing");

        Element backlogElement = backlog.generateXML();
        Assert.assertEquals("[#text: shortname]", backlogElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: longname]", backlogElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", backlogElement.getChildNodes().item(3).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Untitled Person]", backlogElement.getChildNodes().item(4).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: thing]", backlogElement.getChildNodes().item(5).getChildNodes().item(0).toString());
        Assert.assertEquals(0, backlogElement.getChildNodes().item(6).getChildNodes().getLength());
        Assert.assertEquals(8, backlogElement.getChildNodes().getLength());

    }

    /**
     * Tests for Backlogs' XML generator method for when the backlog has stories.
     */
    @Test
    public void testGenerateXMLStories() {
        new ReportGenerator();
        Backlog backlog = new Backlog("shortname", "longname", "description", new Person(), new Project(), "thing");
        Story story1 = new Story();
        Story story2 = new Story();
        backlog.add(story1);
        backlog.add(story2);

        Element backlogElement = backlog.generateXML();
        Assert.assertEquals("[#text: shortname]", backlogElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: longname]", backlogElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", backlogElement.getChildNodes().item(3).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Untitled Person]", backlogElement.getChildNodes().item(4).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: thing]", backlogElement.getChildNodes().item(5).getChildNodes().item(0).toString());
        Assert.assertEquals(2, backlogElement.getChildNodes().item(6).getChildNodes().getLength());
        Assert.assertEquals(8, backlogElement.getChildNodes().getLength());
    }



}
