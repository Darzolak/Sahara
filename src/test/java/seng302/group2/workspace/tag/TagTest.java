package seng302.group2.workspace.tag;

import javafx.scene.paint.Color;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.person.Person;

import java.util.HashSet;

/**
 * A series of tests relating to Workspaces
 *
 * @author Bronson McNaughton (btm38)
 */
public class TagTest {

    @Test
    public void testSetGetColor() throws Exception {
        Tag tag = new Tag("hash");
        Assert.assertEquals(Color.ROYALBLUE, tag.getColor());
    }

    @Test
    public void testGetItemsSet() throws Exception {
        Tag tag = new Tag("hash");
        Assert.assertEquals(new HashSet<SaharaItem>(), tag.getItemsSet());
    }

    @Test
    public void testGenerateXML() throws Exception {
        new ReportGenerator();
        Tag tag = new Tag("hash");

        Element tagElement = tag.generateXML();
        Assert.assertEquals("[hash: null]", tagElement.toString());
    }

    @Test
    public void testToString() throws Exception {
        Tag tag = new Tag("hash");
        Tag tag2 = new Tag("tag");
        Assert.assertEquals("hash", tag.toString());
        Assert.assertEquals("tag", tag2.toString());
    }

    //TODO Bronson
    @Test
    public void testGetTaggedItems() {
        Tag tag1 = new Tag("Tag1");
        Person peter = new Person();

        Global.currentWorkspace.add(tag1);
        peter.getTags().addAll(tag1);

        peter.getTags().get(0).setName("peter tags");
    }

    @Test
    public void testGetNewTag() {
        Tag tag1 = new Tag("Tag1");

        Global.currentWorkspace.add(tag1);
        Tag tag1Alt = Tag.getNewTag("Tag1");
        Tag newTag = Tag.getNewTag("Another Tag");


        Assert.assertEquals(tag1, tag1Alt);
        Assert.assertEquals(newTag.getName(), "Another Tag");
    }

    /**
     * Tests the deletion of a tag globally.
     */
    @Test
    public void testDeleteGlobalTag() {
        Tag tag = new Tag("New tag");
        Person person1 = new Person();
        person1.getTags().add(tag);

        Assert.assertEquals(person1.getTags().get(0).getName(), "New tag");

        tag.deleteGlobalTag();

        Assert.assertFalse(person1.getTags().contains(tag));
        Assert.assertFalse(Global.currentWorkspace.getAllTags().contains(tag));

        Global.commandManager.undo();

        Assert.assertTrue(person1.getTags().contains(tag));
        Assert.assertTrue(Global.currentWorkspace.getAllTags().contains(tag));

        Global.commandManager.redo();

        Assert.assertFalse(person1.getTags().contains(tag));
        Assert.assertFalse(Global.currentWorkspace.getAllTags().contains(tag));
    }

    /**
     * Tests the edit of a tag.
     */
    @Test
    public void testEdit() {
        Tag tag = new Tag("Tag");

        tag.edit("New Tag", Color.WHEAT);

        Assert.assertEquals("New Tag", tag.getName());
        Assert.assertEquals(Color.WHEAT, tag.getColor());

        Global.commandManager.undo();

        Assert.assertEquals("Tag", tag.getName());
        Assert.assertEquals(Color.ROYALBLUE, tag.getColor());

        Global.commandManager.redo();

        Assert.assertEquals("New Tag", tag.getName());
        Assert.assertEquals(Color.WHEAT, tag.getColor());
    }
}