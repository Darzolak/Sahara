/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.workspace.team;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.allocation.Allocation;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.workspace.Workspace;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;

/**
 * A series of tests relating to Teams
 *
 * @author Cameron Williams (crw73)
 */
public class TeamTest {

    /**
     * A simple test for the Team constructors
     */
    @Test
    public void testTeamConstructors() {
        Team team = new Team();
        Assert.assertEquals("Untitled Team", team.getShortName());
        Assert.assertEquals("", team.getDescription());
        Assert.assertEquals("Untitled Team", team.toString());
        Assert.assertEquals(null, team.getChildren());
        Assert.assertEquals(null, team.getScrumMaster());
        Assert.assertEquals(null, team.getProductOwner());

        Team team2 = new Team("Arctic Falcon", "An awesome team name");
        Assert.assertEquals("Arctic Falcon", team2.getShortName());
        Assert.assertEquals("An awesome team name", team2.getDescription());
        Assert.assertEquals("Arctic Falcon", team2.toString());

    }

    /**
     * Tests for Teams' setter methods.
     */
    @Test
    public void testTeamSetters() {
        Team team = new Team();
        Person po = new Person();
        po.setShortName("Chardonnay");
        team.setShortName("Arctic Falcon");
        team.setDescription("An awesome team name");
        team.setScrumMaster(new Person());
        team.setProductOwner(po);

        Assert.assertEquals("Arctic Falcon", team.getShortName());
        Assert.assertEquals("An awesome team name", team.getDescription());
        Assert.assertEquals("Arctic Falcon", team.toString());

        Assert.assertEquals("Chardonnay", team.getProductOwner().getShortName());
        Assert.assertEquals("Untitled Person", team.getScrumMaster().getShortName());
        //Setting Team and People Roles should be seperate
        //Assert.assertEquals(RoleType.PRODUCT_OWNER, team.getProductOwner().getRole().getType());
        //Assert.assertEquals(RoleType.SCRUM_MASTER, team.getScrumMaster().getRole().getType());
    }

    /**
     * Tests the addition and removal of people in teams
     */
    @Test
    public void testAddPerson() {
        Team team = new Team();
        Person person = new Person();

        team.add(person);
        Assert.assertTrue(team.getPeople().contains(person));

        team.getPeople().remove(person);
        Assert.assertFalse(team.getPeople().contains(person));

        team.add(person);
        Assert.assertTrue(team.getPeople().contains(person));

        team.getPeople().remove(person);
        Assert.assertFalse(team.getPeople().contains(person));
    }

    /**
     * Test the addition and removal of team's project allocations
     */
    @Test
    public void testAddRemoveAllocation() {
        Team team = new Team();
        Project proj = new Project();
        LocalDate startDate = LocalDate.of(2014, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(2014, Month.JANUARY, 10);
        Allocation alloc = new Allocation(proj, team, startDate, endDate);

        team.add(alloc);
        Assert.assertTrue(team.getProjectAllocations().contains(alloc));

        team.remove(alloc);
        Assert.assertTrue(team.getProjectAllocations().isEmpty());
    }


    /**
     * Tests whether or not the unassigned team getter works correctly
     */
    @Test
    public void testIsUnassignedTeam() {
        Team unass = Team.createUnassignedTeam();
        Team ateam = new Team();

        Assert.assertTrue(unass.isUnassignedTeam());
        Assert.assertFalse(ateam.isUnassignedTeam());
    }

    /**
     * Test the method which returns the current allocations
     */
    @Test
    public void testGetCurrentAllocations() {
        Team team = new Team();
        Project proj = new Project();
        Allocation allocToday = new Allocation(proj, team, LocalDate.now(), LocalDate.now());
        proj.add(allocToday);

        Assert.assertEquals(allocToday, team.getCurrentAllocation());
    }


    /**
     * Tests that a team properly prepares for serialization
     */
    @Test
    public void testPrepSerialization() {
        Team testTeam = new Team();
        Person testPerson = new Person();

        testTeam.getSerializablePeople().clear();
        Assert.assertTrue(testTeam.getSerializablePeople().isEmpty());

        testTeam.add(testPerson);
        testTeam.prepSerialization();

        Assert.assertTrue(testTeam.getSerializablePeople().contains(testPerson));
    }

    /**
     * Tests that a team properly post-pares after deserialization
     */
    @Test
    public void testPostSerialization() {
        Team testTeam = new Team();
        Person testPerson = new Person();

        testTeam.getPeople().clear();
        Assert.assertTrue(testTeam.getPeople().isEmpty());

        testTeam.getSerializablePeople().add(testPerson);
        testTeam.postDeserialization();

        Assert.assertTrue(testTeam.getPeople().contains(testPerson));
    }

    /**
     * Tests the compareTo method of Team to ensure it correctly returns an int representing if a
     * shortName is larger or not.
     */
    @Test
    public void testCompareTo() {
        Team team1 = new Team();
        Team team2 = new Team();
        team1.setShortName("A");
        team2.setShortName("Z");

        Assert.assertTrue(team1.compareTo(team2) <= 0);
        Assert.assertTrue(team2.compareTo(team1) >= 0);
        Assert.assertTrue(team1.compareTo(team1) == 0);
    }


//    @Test
//    public void testBasicEdit() {
//        Team team = new Team("Arctic Falcon", "An awesome team name");
//        ArrayList<Tag> tags = new ArrayList<>();
//        team.edit("Antarctic Eagle", "An even awesomer team name", tags);
//        Assert.assertEquals("Antarctic Eagle", team.getShortName());
//        Assert.assertEquals("An even awesomer team name", team.getDescription());
//
//        Global.commandManager.undo();
//
//        Assert.assertEquals("Arctic Falcon", team.getShortName());
//        Assert.assertEquals("An awesome team name", team.getDescription());
//    }


    @Test
    public void testDelete() {
        Global.currentWorkspace = new Workspace();
        Team team = new Team();
        Person p1 = new Person();
        Project project = new Project();
        Release release = new Release();
        Sprint sprint = new Sprint("goal", "longname", "desc", LocalDate.now(), LocalDate.now().plusDays(1), project,
                team, release);
        Allocation allocation = new Allocation(project, team, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5));

        project.getSprints().add(sprint);
        project.getTeamAllocations().add(allocation);
        team.getPeople().add(p1);
        p1.setTeam(team);

        Global.currentWorkspace.add(project);
        Global.currentWorkspace.add(p1);
        Global.currentWorkspace.add(team);

        Assert.assertTrue(Global.currentWorkspace.getTeams().contains(team));
        Assert.assertTrue(Global.currentWorkspace.getPeople().contains(p1));
        Assert.assertEquals(team, p1.getTeam());
        Assert.assertEquals(1, project.getTeamAllocations().size());
        Assert.assertEquals(1, project.getSprints().size());

        team.deleteTeam();

        Assert.assertFalse(Global.currentWorkspace.getTeams().contains(team));
        Assert.assertTrue(Global.currentWorkspace.getPeople().contains(p1));
        Assert.assertEquals(Global.getUnassignedTeam(), p1.getTeam());
        Assert.assertEquals(0, project.getTeamAllocations().size());
        Assert.assertEquals(0, project.getSprints().size());

        Assert.assertEquals("the deletion of Team \"" + team.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertTrue(Global.currentWorkspace.getTeams().contains(team));
        Assert.assertTrue(Global.currentWorkspace.getPeople().contains(p1));
        Assert.assertEquals(team, p1.getTeam());
        Assert.assertEquals(1, project.getTeamAllocations().size());
        Assert.assertEquals(1, project.getSprints().size());
    }


    @Test
    public void testDeleteCascading() {
        Team team = new Team();
        Person p1 = new Person();
        Person p2 = new Person();
        Project project = new Project();
        Release release = new Release();
        Sprint sprint = new Sprint("goal", "longname", "desc", LocalDate.now(), LocalDate.now().plusDays(1), project,
                team, release);
        Allocation allocation = new Allocation(project, team, LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5));
        team.getPeople().addAll(p1, p2);
        project.getSprints().add(sprint);
        project.getTeamAllocations().add(allocation);

        Global.currentWorkspace.add(project);
        Global.currentWorkspace.add(team);
        Global.currentWorkspace.add(p1);
        Global.currentWorkspace.add(p2);

        Assert.assertTrue(Global.currentWorkspace.getTeams().contains(team));
        Assert.assertTrue(Global.currentWorkspace.getPeople().contains(p1));
        Assert.assertTrue(Global.currentWorkspace.getPeople().contains(p2));
        Assert.assertEquals(1, project.getTeamAllocations().size());
        Assert.assertEquals(1, project.getSprints().size());

        team.deleteTeamCascading();

        Assert.assertFalse(Global.currentWorkspace.getTeams().contains(team));
        Assert.assertFalse(Global.currentWorkspace.getPeople().contains(p1));
        Assert.assertFalse(Global.currentWorkspace.getPeople().contains(p2));
        Assert.assertEquals(0, project.getTeamAllocations().size());
        Assert.assertEquals(0, project.getSprints().size());

        Assert.assertEquals("the cascading deletion of Team \"" + team.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertTrue(Global.currentWorkspace.getTeams().contains(team));
        Assert.assertTrue(Global.currentWorkspace.getPeople().contains(p1));
        Assert.assertTrue(Global.currentWorkspace.getPeople().contains(p2));
        Assert.assertEquals(1, project.getTeamAllocations().size());
        Assert.assertEquals(1, project.getSprints().size());
    }


    @Test
    public void testExtendedEdit() {
        Global.currentWorkspace = new Workspace();

        Team team = new Team("Arctic Falcon", "An awesome team name");
        Person bronson = new Person();
        Person moffat = new Person();
        Person andrew = new Person();
        Person shinobu = new Person();
        ArrayList<Person> members = new ArrayList<>();
        members.add(bronson);
        members.add(shinobu);
        members.add(moffat);
        members.add(andrew);
        ArrayList<Person> devs = new ArrayList<>();
        devs.add(bronson);

        ArrayList<Tag> tags = new ArrayList<>();
        Tag tag = new Tag("Tag");
        tags.add(tag);

        team.edit("Antarctic Eagle", "An even awesomer team name", members,
                moffat, andrew, devs, tags);

        Assert.assertEquals("Antarctic Eagle", team.getShortName());
        Assert.assertEquals("An even awesomer team name", team.getDescription());
        Assert.assertTrue(team.getPeople().containsAll(members));
        Assert.assertTrue(team.getDevs().contains(bronson));
        Assert.assertEquals(andrew, team.getScrumMaster());
        Assert.assertEquals(moffat, team.getProductOwner());
        Assert.assertEquals(1, team.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", team.getTags().get(0).getName());

        Assert.assertEquals("the edit of Team \"" + team.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertEquals("Arctic Falcon", team.getShortName());
        Assert.assertEquals("An awesome team name", team.getDescription());
        Assert.assertFalse(team.getPeople().containsAll(members));
        Assert.assertFalse(team.getDevs().contains(bronson));
        Assert.assertNull(team.getScrumMaster());
        Assert.assertNull(team.getProductOwner());
        Assert.assertEquals(0, team.getTags().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Global.commandManager.redo();

        Assert.assertEquals("Antarctic Eagle", team.getShortName());
        Assert.assertEquals("An even awesomer team name", team.getDescription());
        Assert.assertTrue(team.getPeople().containsAll(members));
        Assert.assertTrue(team.getDevs().contains(bronson));
        Assert.assertEquals(andrew, team.getScrumMaster());
        Assert.assertEquals(moffat, team.getProductOwner());
        Assert.assertEquals(1, team.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", team.getTags().get(0).getName());
    }

    /**
     * Tests for Teams' XML generator method for when the team doesn't have allocations or People.
     */
    @Test
    public void testGenerateXML() {
        new ReportGenerator();
        Team team = new Team("shortname", "description");

        Element teamElement = team.generateXML();
        Assert.assertEquals("[#text: shortname]", teamElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", teamElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals(0, teamElement.getChildNodes().item(3).getChildNodes().getLength());
        Assert.assertEquals(0, teamElement.getChildNodes().item(4).getChildNodes().getLength());
        Assert.assertEquals(0, teamElement.getChildNodes().item(5).getChildNodes().getLength());
        Assert.assertEquals("[product-owner: null]", teamElement.getChildNodes().item(6).toString());
        Assert.assertEquals("[scrum-master: null]", teamElement.getChildNodes().item(7).toString());
        Assert.assertEquals(0, teamElement.getChildNodes().item(8).getChildNodes().getLength());
        Assert.assertEquals(0, teamElement.getChildNodes().item(9).getChildNodes().getLength());
        Assert.assertEquals(11, teamElement.getChildNodes().getLength());
    }

    /**
     * Tests for Teams' XML generator method for when the team doesn't have allocations.
     */
    @Test
    public void testGenerateXMLPeople() {
        new ReportGenerator();
        Team team = new Team("shortname", "description");
        Person productOwner = new Person();
        Person scrumMaster = new Person();
        Person dev1 = new Person();
        dev1.setRole(Role.getRoleFromType(Role.RoleType.DEVELOPMENT_TEAM_MEMBER));
        Person dev2 = new Person();
        dev2.setRole(Role.getRoleFromType(Role.RoleType.DEVELOPMENT_TEAM_MEMBER));
        Person other1 = new Person();
        Person other2 = new Person();
        Person other3 = new Person();
        Person other4 = new Person();

        team.setProductOwner(productOwner);
        team.setScrumMaster(scrumMaster);
        team.add(dev1);
        team.add(dev2);
        team.add(other1);
        team.add(other2);
        team.add(other3);
        team.add(other4);

        Element teamElement = team.generateXML();
        Assert.assertEquals("[#text: shortname]", teamElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", teamElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals(0, teamElement.getChildNodes().item(3).getChildNodes().getLength());
        Assert.assertEquals(0, teamElement.getChildNodes().item(4).getChildNodes().getLength());
        Assert.assertEquals(0, teamElement.getChildNodes().item(5).getChildNodes().getLength());
        Assert.assertEquals(1, teamElement.getChildNodes().item(6).getChildNodes().getLength());
        Assert.assertEquals(1, teamElement.getChildNodes().item(7).getChildNodes().getLength());
        Assert.assertEquals(2, teamElement.getChildNodes().item(8).getChildNodes().getLength());
        Assert.assertEquals(4, teamElement.getChildNodes().item(9).getChildNodes().getLength());
        Assert.assertEquals(11, teamElement.getChildNodes().getLength());
    }

    /**
     * Tests for Teams' XML generator method for when the team doesn't have People.
     */
    @Test
    public void testGenerateXMLAllocations() {
        new ReportGenerator();
        Team team = new Team("shortname", "description");

        Allocation pastAllocation1 = new Allocation(new Project(), team, LocalDate.of(1994, Month.DECEMBER, 1), LocalDate.of(1994, Month.DECEMBER, 2));
        Allocation pastAllocation2 = new Allocation(new Project(), team, LocalDate.of(1994, Month.DECEMBER, 1), LocalDate.of(1994, Month.DECEMBER, 2));
        Allocation currentAllocation = new Allocation(new Project(), team, LocalDate.of(2000, Month.JANUARY, 1), LocalDate.of(3000, Month.JANUARY, 1));
        Allocation futureAllocation1 = new Allocation(new Project(), team, LocalDate.of(3000, Month.DECEMBER, 31), LocalDate.of(3001, Month.DECEMBER, 31));
        Allocation futureAllocation2 = new Allocation(new Project(), team, LocalDate.of(3002, Month.DECEMBER, 31), LocalDate.of(3911, Month.DECEMBER, 31));

        team.add(pastAllocation1);
        team.add(pastAllocation2);
        team.add(currentAllocation);
        team.add(futureAllocation1);
        team.add(futureAllocation2);

        Element teamElement = team.generateXML();
        Assert.assertEquals("[#text: shortname]", teamElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", teamElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals(1, teamElement.getChildNodes().item(3).getChildNodes().getLength());
        Assert.assertEquals(2, teamElement.getChildNodes().item(4).getChildNodes().getLength());
        Assert.assertEquals(2, teamElement.getChildNodes().item(5).getChildNodes().getLength());
        Assert.assertEquals("[product-owner: null]", teamElement.getChildNodes().item(6).toString());
        Assert.assertEquals("[scrum-master: null]", teamElement.getChildNodes().item(7).toString());
        Assert.assertEquals(0, teamElement.getChildNodes().item(8).getChildNodes().getLength());
        Assert.assertEquals(0, teamElement.getChildNodes().item(9).getChildNodes().getLength());
        Assert.assertEquals(11, teamElement.getChildNodes().getLength());
    }

    /**
     * Tests for Teams' XML generator method.
     */
    @Test
    public void testGenerateXMLAll() {
        new ReportGenerator();
        Team team = new Team("shortname", "description");

        Person productOwner = new Person();
        Person scrumMaster = new Person();
        Person dev1 = new Person();
        dev1.setRole(Role.getRoleFromType(Role.RoleType.DEVELOPMENT_TEAM_MEMBER));
        Person dev2 = new Person();
        dev2.setRole(Role.getRoleFromType(Role.RoleType.DEVELOPMENT_TEAM_MEMBER));
        Person other1 = new Person();
        Person other2 = new Person();
        Person other3 = new Person();
        Person other4 = new Person();

        Allocation pastAllocation1 = new Allocation(new Project(), team, LocalDate.of(1994, Month.DECEMBER, 1), LocalDate.of(1994, Month.DECEMBER, 2));
        Allocation pastAllocation2 = new Allocation(new Project(), team, LocalDate.of(1994, Month.DECEMBER, 1), LocalDate.of(1994, Month.DECEMBER, 2));
        Allocation currentAllocation = new Allocation(new Project(), team, LocalDate.of(2000, Month.JANUARY, 1), LocalDate.of(3000, Month.JANUARY, 1));
        Allocation futureAllocation1 = new Allocation(new Project(), team, LocalDate.of(3000, Month.DECEMBER, 31), LocalDate.of(3001, Month.DECEMBER, 31));
        Allocation futureAllocation2 = new Allocation(new Project(), team, LocalDate.of(3002, Month.DECEMBER, 31), LocalDate.of(3911, Month.DECEMBER, 31));

        team.setProductOwner(productOwner);
        team.setScrumMaster(scrumMaster);
        team.add(dev1);
        team.add(dev2);
        team.add(other1);
        team.add(other2);
        team.add(other3);
        team.add(other4);

        team.add(pastAllocation1);
        team.add(pastAllocation2);
        team.add(currentAllocation);
        team.add(futureAllocation1);
        team.add(futureAllocation2);

        Element teamElement = team.generateXML();
        Assert.assertEquals("[#text: shortname]", teamElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: description]", teamElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals(1, teamElement.getChildNodes().item(3).getChildNodes().getLength());
        Assert.assertEquals(2, teamElement.getChildNodes().item(4).getChildNodes().getLength());
        Assert.assertEquals(2, teamElement.getChildNodes().item(5).getChildNodes().getLength());
        Assert.assertEquals(1, teamElement.getChildNodes().item(6).getChildNodes().getLength());
        Assert.assertEquals(1, teamElement.getChildNodes().item(7).getChildNodes().getLength());
        Assert.assertEquals(2, teamElement.getChildNodes().item(8).getChildNodes().getLength());
        Assert.assertEquals(4, teamElement.getChildNodes().item(9).getChildNodes().getLength());
        Assert.assertEquals(11, teamElement.getChildNodes().getLength());
    }


}
