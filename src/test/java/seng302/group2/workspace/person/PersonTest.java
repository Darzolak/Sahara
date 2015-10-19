package seng302.group2.workspace.person;

import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Unit test for Person
 * Created by Jordane on 27/04/2015.
 */
public class PersonTest {

    // Build some test objects for test operations
    Team unassignedTeam = null;

    Team testTeam = new Team();
    Skill testSkill = new Skill();
    Role testRole = new Role();

    LocalDate testDate;
    LocalDate testDate2;

    Person basicPerson = new Person();
    Person extendedPerson = new Person("btm38", "Bronson", "McNaughton", "btm38@gmail.com",
            "A really cool dude", testDate2);


    /**
     * Prepares test objects before executing the Person tests
     */
    @Before
    public void setUp() {
        // Find the unassigned team
        for (SaharaItem team : Global.currentWorkspace.getTeams()) {
            Team castedTeam = (Team) team;
            if (castedTeam.isUnassignedTeam()) {
                unassignedTeam = castedTeam;
                break; // Only one unassigned team
            }
        }

        // Set the test date
        try {
            testDate = LocalDate.parse("06/03/1995", Global.dateFormatter);
            testDate2 = LocalDate.parse("19/12/1994", Global.dateFormatter);
        }
        catch (DateTimeParseException e) {
            Assert.fail("The date was not parsed correctly, please review:\n" + e.toString());
        }
    }

    /**
     * Tests the getter and setter for a persons short name member
     */
    @Test
    public void testGetSetShortName() {
        Assert.assertEquals("Untitled Person", basicPerson.getShortName());
        Assert.assertEquals("btm38", extendedPerson.getShortName());
        basicPerson.setShortName("newShortName");
        Assert.assertEquals("newShortName", basicPerson.getShortName());
    }

    /**
     * Tests the getter and setter for a persons first name member
     */
    @Test
    public void testGetSetFirstName() {
        Assert.assertEquals("firstName", basicPerson.getFirstName());
        Assert.assertEquals("Bronson", extendedPerson.getFirstName());
        basicPerson.setFirstName("newFirstName");
        Assert.assertEquals("newFirstName", basicPerson.getFirstName());
    }

    /**
     * Tests the getter and setter for a persons last name member
     */
    @Test
    public void testGetSetLastName() {
        Assert.assertEquals("lastName", basicPerson.getLastName());
        Assert.assertEquals("McNaughton", extendedPerson.getLastName());
        basicPerson.setLastName("newLastName");
        Assert.assertEquals("newLastName", basicPerson.getLastName());
    }

    /**
     * Tests the getter and setter for a persons email member
     */
    @Test
    public void testGetSetEmail() {
        Assert.assertEquals("", basicPerson.getEmail());
        Assert.assertEquals("btm38@gmail.com", extendedPerson.getEmail());
        basicPerson.setEmail("newEmail");
        Assert.assertEquals("newEmail", basicPerson.getEmail());
    }

    /**
     * Tests the getter and setter for a persons description member
     */
    @Test
    public void testGetSetDescription() {
        Assert.assertEquals("", basicPerson.getDescription());
        Assert.assertEquals("A really cool dude", extendedPerson.getDescription());
        basicPerson.setDescription("newDesc");
        Assert.assertEquals("newDesc", basicPerson.getDescription());
    }

    /**
     * Tests the getter and setter for a persons birth date member
     */
    @Test
    public void testGetSetBirthDate() {
        extendedPerson.setBirthDate(testDate2);
        Assert.assertEquals(null, basicPerson.getBirthDate());
        Assert.assertEquals("", basicPerson.getDateString());
        Assert.assertEquals(testDate2, extendedPerson.getBirthDate());

        basicPerson.setBirthDate(testDate);
        Assert.assertEquals(testDate, basicPerson.getBirthDate());
        Assert.assertEquals("06/03/1995", basicPerson.getDateString());
    }

    /**
     * Tests the getter for a persons team name
     */
    @Test
    public void testGetSetTeamName() {
        basicPerson.setTeam(unassignedTeam);
        Assert.assertEquals("Unassigned", basicPerson.getTeamName());
        Assert.assertEquals("Unassigned", extendedPerson.getTeamName());
        basicPerson.setTeam(testTeam);
        Assert.assertEquals(testTeam.getShortName(), basicPerson.getTeamName());
    }

    /**
     * Tests the getter and setter for a persons team member
     */
    @Test
    public void testGetSetTeam() {
        basicPerson.setTeam(unassignedTeam);
        Assert.assertEquals(unassignedTeam, basicPerson.getTeam());
        Assert.assertEquals(unassignedTeam, extendedPerson.getTeam());
        basicPerson.setTeam(testTeam);
        Assert.assertEquals(testTeam, basicPerson.getTeam());
    }

    /**
     * Tests the getter and setter for a persons role member
     */
    @Test
    public void testGetSetRole() {
        Assert.assertEquals(null, basicPerson.getRole());
        Assert.assertEquals(null, extendedPerson.getRole());
        basicPerson.setRole(testRole);
        Assert.assertEquals(testRole, basicPerson.getRole());
    }

    /**
     * Tests the getter and setter for a persons skill member
     */
    @Test
    public void testGetSetSkills() {
        Assert.assertEquals(observableArrayList(), basicPerson.getSkills());
        Assert.assertEquals(observableArrayList(), extendedPerson.getSkills());

        ObservableList<Skill> skills = observableArrayList();
        skills.add(testSkill);

        basicPerson.getSkills().add(testSkill);
        Assert.assertEquals(skills, basicPerson.getSkills());
    }

    /**
     * Tests that a person properly prepares for serialization
     */
    @Test
    public void testPrepSerialization() {
        basicPerson.getSerializableSkills().clear();
        basicPerson.getSkills().clear();
        Assert.assertEquals(new ArrayList<Skill>(), basicPerson.getSerializableSkills());
        Assert.assertEquals(new ArrayList<Skill>(), basicPerson.getSkills());

        basicPerson.getSkills().add(testSkill);
        basicPerson.prepSerialization();

        ArrayList<Skill> skills = new ArrayList<>();
        skills.add(testSkill);

        Assert.assertEquals(skills, basicPerson.getSerializableSkills());
    }

    /**
     * Tests that a person properly post-pares after deserialization
     */
    @Test
    public void testPostSerialization() {
        basicPerson.getSerializableSkills().clear();
        basicPerson.getSkills().clear();
        Assert.assertEquals(new ArrayList<Skill>(), basicPerson.getSerializableSkills());
        Assert.assertEquals(new ArrayList<Skill>(), basicPerson.getSkills());

        basicPerson.getSerializableSkills().add(testSkill);
        basicPerson.postDeserialization();

        ArrayList<Skill> skills = new ArrayList<>();
        skills.add(testSkill);

        Assert.assertEquals(skills, basicPerson.getSkills());
    }

    /**
     * Tests that a persons skills are removed correctly
     */
    @Test
    public void testRemoveSkill() {
        // Clear
        basicPerson.getSkills().clear();

        // Add
        basicPerson.getSkills().add(testSkill);
        ArrayList<Skill> skills = new ArrayList<>();
        skills.add(testSkill);
        Assert.assertEquals(skills, basicPerson.getSkills());

        // Remove
        basicPerson.getSkills().remove(testSkill);
        Assert.assertTrue(basicPerson.getSkills().isEmpty());
    }

    @Test
    public void testGetChildren() throws Exception {
        Assert.assertNull(basicPerson.getChildren());
    }

    @Test
    public void testToString() throws Exception {
        basicPerson.setShortName("unnamed");
        Assert.assertEquals("unnamed", basicPerson.toString());
        Assert.assertEquals("btm38", extendedPerson.toString());
    }

    /**
     * Tests the compareTo method of Person to ensure it correctly returns an int representing if a
     * shortName is larger or not.
     */
    @Test
    public void testCompareTo() {
        Person zedd = new Person("z", "first", "last", "", "", null);
        Assert.assertTrue(extendedPerson.compareTo(zedd) <= 0);
        Assert.assertTrue(zedd.compareTo(extendedPerson) >= 0);
        Assert.assertTrue(zedd.compareTo(zedd) == 0);
    }


    @Test
    public void testEdit() {
        Person person = new Person("btm38", "Bronson", "McNaughton", "btm38@gmail.com",
                "A really cool dude", testDate2);
        Team team = new Team();
        Tag tag = new Tag("Tag");
        ArrayList<Tag> newTags = new ArrayList<>();
        newTags.add(tag);
        person.edit("shortName", "firstName",
                "lastName", "email", LocalDate.now(), "Desc", team, null, newTags);

        Assert.assertEquals("shortName", person.getShortName());
        Assert.assertEquals("firstName", person.getFirstName());
        Assert.assertEquals("lastName", person.getLastName());
        Assert.assertEquals("email", person.getEmail());
        Assert.assertEquals(LocalDate.now(), person.getBirthDate());
        Assert.assertEquals("Desc", person.getDescription());
        Assert.assertEquals(1, person.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", person.getTags().get(0).getName());

        Assert.assertEquals("the edit of Person \"" + person.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertEquals("btm38", person.getShortName());
        Assert.assertEquals("Bronson", person.getFirstName());
        Assert.assertEquals("McNaughton", person.getLastName());
        Assert.assertEquals("btm38@gmail.com", person.getEmail());
        Assert.assertEquals(testDate2, person.getBirthDate());
        Assert.assertEquals("A really cool dude", person.getDescription());
        Assert.assertEquals(0, person.getTags().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Global.commandManager.redo();

        Assert.assertEquals("shortName", person.getShortName());
        Assert.assertEquals("firstName", person.getFirstName());
        Assert.assertEquals("lastName", person.getLastName());
        Assert.assertEquals("email", person.getEmail());
        Assert.assertEquals(LocalDate.now(), person.getBirthDate());
        Assert.assertEquals("Desc", person.getDescription());
        Assert.assertEquals(1, person.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", person.getTags().get(0).getName());
    }


    @Test
    public void testDeletePerson() {
        Global.commandManager.clear();
        Person person = new Person("short", "firstName", "lastName", "email",
                "description", LocalDate.now());
        Global.currentWorkspace.add(person);

        person.deletePerson();
        Assert.assertFalse(Global.currentWorkspace.getPeople().contains(person));
        Global.commandManager.undo();
        Assert.assertTrue(Global.currentWorkspace.getPeople().contains(person));
    }

    /**
     * Tests for Persons' XML generator method for when the person doesn't have skills.
     */
    @Test
    public void testGenerateXML() {
        new ReportGenerator();
        Person person = new Person("short", "firstName", "lastName", "email",
                "description", LocalDate.of(2015, Month.APRIL, 12));

        Element personElement = person.generateXML();
        Assert.assertEquals("[#text: short]", personElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: firstName]", personElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: lastName]", personElement.getChildNodes().item(3).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: email]", personElement.getChildNodes().item(4).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: 12/04/2015]", personElement.getChildNodes().item(5).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", personElement.getChildNodes().item(6).getChildNodes().item(0).toString());
        Assert.assertEquals(0, personElement.getChildNodes().item(7).getChildNodes().getLength());
        Assert.assertEquals(9, personElement.getChildNodes().getLength());

    }

    /**
     * Tests for Persons' XML generator method when the Person has skills.
     */
    @Test
    public void testGenerateXMLSkills() {
        new ReportGenerator();
        Person person = new Person("short", "firstName", "lastName", "email",
                "description", LocalDate.of(2015, Month.APRIL, 12));
        person.getSkills().add(new Skill());
        person.getSkills().add(new Skill());

        Element personElement = person.generateXML();
        Assert.assertEquals("[#text: short]", personElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: firstName]", personElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: lastName]", personElement.getChildNodes().item(3).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: email]", personElement.getChildNodes().item(4).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: 12/04/2015]", personElement.getChildNodes().item(5).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", personElement.getChildNodes().item(6).getChildNodes().item(0).toString());
        Assert.assertEquals(2, personElement.getChildNodes().item(7).getChildNodes().getLength());
        Assert.assertEquals(9, personElement.getChildNodes().getLength());

    }


    /**
     * Test for Person's getLogs() method
     */
    @Test
    public void testGetLog() {
        Workspace ws = new Workspace();
        Person partner = new Person();
        Project proj = new Project();
        Story story = new Story("", "", "", "", proj, 0);
        Task task = new Task("", "", story, basicPerson, 0);
        Log log = new Log(task, "", basicPerson, partner, 10, LocalDateTime.now(), 0);
        Global.currentWorkspace = ws;
        ws.add(proj);
        proj.add(story);
        proj.add(log);
        story.add(task);

        Assert.assertEquals(1, basicPerson.getLogs().size());
    }
}