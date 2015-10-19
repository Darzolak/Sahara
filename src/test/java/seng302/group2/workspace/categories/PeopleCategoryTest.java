package seng302.group2.workspace.categories;

import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Jordane on 7/06/2015.
 */
public class PeopleCategoryTest {

    @Test
    public void testGetChildren() throws Exception {
        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;
        Collection<Person> people = new HashSet<>();
        Person p1 = new Person();
        Person p2 = new Person();
        people.add(p1);
        people.add(p2);
        ws.getPeople().addAll(people);

        Category peopleCategory = new PeopleCategory();
        Assert.assertTrue(peopleCategory.getChildren().containsAll(people));
    }

    @Test
    public void testToString() {
        Category peopleCategory = new PeopleCategory();
        Assert.assertEquals("People", peopleCategory.toString());
    }

    @Test
    public void testEquals() {
        Category p1 = new PeopleCategory();
        Category p2 = new PeopleCategory();
        Assert.assertEquals(p1, p2);
    }
}