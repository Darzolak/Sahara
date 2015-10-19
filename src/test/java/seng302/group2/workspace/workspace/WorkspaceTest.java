/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.workspace.workspace;

import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.ProjectCategory;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.roadMap.RoadMap;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.time.LocalDate;
import java.util.ArrayList;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A series of tests relating to Workspaces
 *
 * @author Jordane Lew (jml168)
 * @author Bronson McNaughton (btm38)
 */
public class WorkspaceTest {

    /**
     * A simple test for the Workspace constructors and getters.
     */
    @Test
    public void testWorkspaceConstructors() {
        Workspace work = new Workspace();
        ObservableList<SaharaItem> people = observableArrayList();
        Assert.assertEquals("Untitled Workspace", work.getShortName());
        Assert.assertEquals("Untitled Workspace", work.getLongName());
        Assert.assertEquals("A blank workspace.", work.getDescription());
        Assert.assertEquals("Untitled Workspace", work.toString());

        Workspace work2 = new Workspace("aShortName", "aLongName", "aDescription");
        Assert.assertEquals("aShortName", work2.getShortName());
        Assert.assertEquals("aLongName", work2.getLongName());
        Assert.assertEquals("aDescription", work2.getDescription());
        Assert.assertEquals("aShortName", work2.toString());

    }


    /**
     * Tests Projects' setter methods.
     */
    @Test
    public void testWorkspaceSetters() {
        Workspace work = new Workspace();
        work.setShortName("aShortName");
        work.setLongName("aLongName");
        work.setDescription("aDescription");

        Assert.assertEquals("aShortName", work.getShortName());
        Assert.assertEquals("aLongName", work.getLongName());
        Assert.assertEquals("aDescription", work.getDescription());
        Assert.assertEquals("aShortName", work.toString());
    }


    /**
     * Tests that people are correctly added to the workspace through the add() method.
     */
    @Test
    public void testAddPerson() {
        Workspace work = new Workspace();
        Global.currentWorkspace = work;
        Person pers = new Person();
        work.add(pers);

        Assert.assertTrue(work.getPeople().contains(pers));

        work.add(pers);

        Assert.assertEquals(2, work.getPeople().size());
        Assert.assertEquals("the creation of Person \"" + pers.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());

        Global.commandManager.undo();
        Assert.assertEquals(1, work.getPeople().size());

        Global.commandManager.undo();
        Assert.assertEquals(0, work.getPeople().size());
    }


    /**
     * Tests that projects are correctly added to the workspace through the add() method.
     */
    @Test
    public void testAddProject() {
        Workspace work = new Workspace();
        Global.currentWorkspace = work;
        Project proj = new Project();
        work.add(proj);

        Assert.assertTrue(work.getProjects().contains(proj));

        work.add(proj);
        Assert.assertEquals("the creation of Project \"" + proj.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Assert.assertEquals(2, work.getProjects().size());
        Global.commandManager.undo();
        Assert.assertEquals(1, work.getProjects().size());
        Global.commandManager.undo();
        Assert.assertEquals(0, work.getProjects().size());
    }

    /**
     * Tests that roadmaps are correctly added to the workspace through the add() method.
     */
    @Test
    public void testAddRoadMap() {
        Workspace work = new Workspace();
        Global.currentWorkspace = work;
        RoadMap roadMap = new RoadMap();
        work.add(roadMap);

        Assert.assertTrue(work.getRoadMaps().contains(roadMap));

        Assert.assertEquals("the creation of Road map \"" + roadMap.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        work.add(roadMap);
        Assert.assertEquals(2, work.getRoadMaps().size());
        Global.commandManager.undo();
        Assert.assertEquals(1, work.getRoadMaps().size());
        Global.commandManager.undo();
        Assert.assertEquals(0, work.getRoadMaps().size());
    }


    /**
     * Tests that skills are correctly added to the workspace through the add() method.
     */
    @Test
    public void testAddSkill() {
        Workspace work = new Workspace();
        Global.currentWorkspace = work;
        Skill skill = new Skill();
        work.add(skill);

        Assert.assertTrue(work.getSkills().contains(skill));

        work.add(skill);

        Assert.assertEquals("the creation of Skill \"" + skill.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        // Also account for the two default skills in workspaces, SM, PO
        Assert.assertEquals(4, work.getSkills().size());
        Global.commandManager.undo();
        Assert.assertEquals(3, work.getSkills().size());
        Global.commandManager.undo();
        Assert.assertEquals(2, work.getSkills().size());
        Global.commandManager.undo();  // Can't remove the default skills
        Assert.assertEquals(2, work.getSkills().size());
    }


    /**
     * Tests that teams are correctly added to the workspace through the add() method.
     */
    @Test
    public void testAddTeam() {
        Workspace work = new Workspace();
        Global.currentWorkspace = work;
        Team team = new Team();

        work.add(team);
        Assert.assertTrue(work.getTeams().contains(team));

        work.add(team);
        Assert.assertEquals(3, work.getTeams().size());  // Including the unassigned team

        Assert.assertEquals("the creation of Team \"" + team.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();
        Assert.assertEquals(2, work.getTeams().size());
        Global.commandManager.undo();
        Assert.assertEquals(1, work.getTeams().size());
        Global.commandManager.undo();
        Assert.assertEquals(1, work.getTeams().size());  // Can't remove the unassigned team
    }

    /**
     * Test the addition of tags to the workspace
     */
    @Deprecated
    @Test
    public  void testAddGlobalTag() {
        Workspace work = new Workspace();
        Global.currentWorkspace = work;
        Tag tag = new Tag("Tag");

        Assert.assertFalse(work.getAllTags().contains(tag));
        work.add(tag);
        Assert.assertTrue(work.getAllTags().contains(tag));

        work.add(tag);
        Assert.assertEquals("the creation of Tag \"" + tag.getName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Assert.assertEquals(2, work.getAllTags().size());

        Global.commandManager.undo();
        Assert.assertEquals(1, work.getAllTags().size());

        Global.commandManager.undo();
        Assert.assertEquals(0, work.getAllTags().size());
        Assert.assertFalse(work.getAllTags().contains(tag));

        Global.commandManager.redo();
        Assert.assertEquals(1, work.getAllTags().size());
        Assert.assertTrue(work.getAllTags().contains(tag));
    }

    /**
     * Tests the workspace's edit command
     */
    @Test
    public void testEdit() {
        Workspace ws = new Workspace("Name", "Long Name", "Description");

        Tag tag = new Tag("Tag");
        ArrayList<Tag> newTags = new ArrayList<>();
        newTags.add(tag);
        ws.edit("New Name", "New Long Name", "New Description", newTags);

        Assert.assertEquals("New Name", ws.getShortName());
        Assert.assertEquals("New Long Name", ws.getLongName());
        Assert.assertEquals("New Description", ws.getDescription());
        Assert.assertEquals(1, ws.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", ws.getTags().get(0).getName());

        Assert.assertEquals("the edit of Workspace \"" + ws.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertEquals("Name", ws.getShortName());
        Assert.assertEquals("Long Name", ws.getLongName());
        Assert.assertEquals("Description", ws.getDescription());
        Assert.assertEquals(0, ws.getTags().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Global.commandManager.redo();

        Assert.assertEquals("New Name", ws.getShortName());
        Assert.assertEquals("New Long Name", ws.getLongName());
        Assert.assertEquals("New Description", ws.getDescription());
        Assert.assertEquals(1, ws.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", ws.getTags().get(0).getName());
    }

    /**
     * Checks to see if getAllTags() returns all the tags in the workspace
     */
    @Test
    public void testGetAllTags() {
        Tag tag1 = new Tag("Tag1");
        Tag tag2 = new Tag("Tag2");
        Tag tag3 = new Tag("Tag3");

        ArrayList<Tag> projectTags = new ArrayList<>();
        projectTags.add(tag2);
        ArrayList<Tag> skillTags = new ArrayList<>();
        skillTags.add(tag3);
        skillTags.add(tag2);

        Project project1 = new Project();
        project1.edit("", "", "", null, projectTags);

        Skill skill1 = new Skill();
        skill1.edit("", "", skillTags);


        Global.currentWorkspace.add(tag1);
        project1.getTags().add(tag2);
        Global.currentWorkspace.add(project1);
        skill1.getTags().add(tag3);
        Global.currentWorkspace.add(skill1);

        ObservableList<Tag> tagList = Global.currentWorkspace.getAllTags();

        Assert.assertTrue(tagList.contains(tag1));
        Assert.assertTrue(tagList.contains(tag2));
        Assert.assertTrue(tagList.contains(tag3));

    }

    /**
     * Tests the Workspace's generateXml() method
     */
    @Test
    public void testGenerateXml() {
        new ReportGenerator();
        Workspace ws = new Workspace("SS", "Shinobi Solutions", "We're better than Google");
        Global.currentWorkspace = ws;
        Person unassignedPerson = new Person("Unssigned Person", "first name", "last name", "email", "description", LocalDate.now());
        Person unassignedPerson2 = new Person("Unssigned Person", "first name", "last name", "email", "description", LocalDate.now());
        Skill unassignedSkill = new Skill("unassigned Skill", "description");
        Project proj = new Project();
        Team unassignedTeam = new Team();

        Global.currentWorkspace.add(proj);
        Global.currentWorkspace.add(unassignedPerson);
        Global.currentWorkspace.add(unassignedPerson2);
        Global.currentWorkspace.add(unassignedSkill);
        Global.currentWorkspace.add(proj);
        Global.currentWorkspace.add(unassignedTeam);

        ReportGenerator.generatedItems = ws.getChildren();
        ReportGenerator.generatedItems.add(new ProjectCategory());
        ReportGenerator.generatedItems.add(ws);
        ReportGenerator.generatedItems.add(proj);
        ReportGenerator.generatedItems.add(unassignedTeam);
        ReportGenerator.generatedItems.add(unassignedPerson);
        ReportGenerator.generatedItems.add(unassignedPerson2);
        ReportGenerator.generatedItems.add(unassignedSkill);

        for(Role role: Global.currentWorkspace.getRoles()) {
            ReportGenerator.generatedItems.add(role);
        }

        Element wsElement  = Global.currentWorkspace.generateXML();

        Assert.assertEquals("[#text: SS]", wsElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Shinobi Solutions]", wsElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: We're better than Google]", wsElement.getChildNodes().item(3).getChildNodes().item(0).toString());

        //<projects>
        Assert.assertEquals(1, wsElement.getChildNodes().item(4).getChildNodes().getLength());
        //<unassigned-teams>
        Assert.assertEquals(1, wsElement.getChildNodes().item(5).getChildNodes().getLength());
        //<unassigned-people>
        Assert.assertEquals(2, wsElement.getChildNodes().item(6).getChildNodes().getLength());
        //<unassigned-skills>
        Assert.assertEquals(1, wsElement.getChildNodes().item(7).getChildNodes().getLength());
        //<Roles>
        Assert.assertEquals(3, wsElement.getChildNodes().item(8).getChildNodes().getLength());

    }
}