package seng302.group2.workspace;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.categories.PeopleCategory;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.tag.Tag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SaharaItemTest {
    /**
     * A test to check the functionality of the ID functions
     */
    @Test
    public void testSetStartId() {
        SaharaItem.setStartId(0, true);
        Person person = new Person();
        Assert.assertEquals(0, person.getId());
        Assert.assertNotEquals(0, SaharaItem.NEXT_ID);

        // No force, takes the max
        SaharaItem.setStartId(0, false);
        Assert.assertNotEquals(0, SaharaItem.NEXT_ID);

        // Forcing to reset to 0, even if it's not the max
        SaharaItem.setStartId(0, true);
        Assert.assertNotEquals(0, SaharaItem.NEXT_ID);

        SaharaItem.setStartId(9001, false);
        Assert.assertNotEquals(9001, SaharaItem.NEXT_ID);
    }

    @Test
    public void testId() {
        SaharaItem.setStartId(0, true);
        Person person = new Person();
        SaharaItem.setStartId(0, true);
        Person person2 = new Person();
        Assert.assertEquals(person.getId(), person2.getId());

        Person person3 = new Person();
        Assert.assertNotEquals(person.getId(), person3.getId());

        Set<Long> ids = new HashSet<>();
        int i = 0;
        while (i < 100) {
            SaharaItem item = new Person();
            Assert.assertTrue(ids.add(item.getId()));
            i++;
        }
    }

    @Test
    public void testEquivalentTo() {
        Person person1 = new Person();
        Person person2 = new Person();

        PeopleCategory peopleCategory1 = new PeopleCategory();

        Assert.assertTrue(person1.equivalentTo(person1));
        Assert.assertFalse(person1.equivalentTo(person2));

        Assert.assertFalse(person1.equivalentTo(peopleCategory1));
    }

    @Test
    public void testEditTags() {
        SaharaItem person1 = new Person();

        Global.currentWorkspace.getAllTags().clear();

        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals(0, person1.getTags().size());

        Tag tag = new Tag("Hey");
        Tag tag2 = new Tag("Ya!");

        ObservableList<Tag> tags = FXCollections.observableArrayList();

        tags.add(tag);
        tags.add(tag2);
        person1.editTags(tags);

        Assert.assertEquals(2, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals(2, person1.getTags().size());

        tags.remove(tag2);
        person1.editTags(tags);

        Assert.assertEquals(2, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals(1, person1.getTags().size());
        Assert.assertEquals("Hey", person1.getTags().get(0).toString());

        Global.commandManager.undo();

        Assert.assertEquals(2, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals(2, person1.getTags().size());

        Global.commandManager.redo();

        Assert.assertEquals(2, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals(1, person1.getTags().size());
        Assert.assertEquals("Hey", person1.getTags().get(0).toString());





    }
}