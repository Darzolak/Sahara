/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.workspace.role;

import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.workspace.Workspace;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A series of tests relating to Role
 *
 * @author swi67
 */
public class RoleTest {
    /**
     * A simple test for the Role constructors
     */
    @Test
    public void testRoleConstructors() {
        Role role = new Role();
        Assert.assertEquals("Untitled Role", role.getShortName());
        Assert.assertEquals("", role.getDescription());
        Assert.assertEquals("Untitled Role", role.toString());

        Role role2 = new Role("QA", Role.RoleType.NONE);
        Assert.assertEquals("QA", role2.getShortName());
        Assert.assertEquals(Role.RoleType.NONE, role2.getType());

        Role role3 = new Role("Dev2", Role.RoleType.DEVELOPMENT_TEAM_MEMBER, "Developer");
        Assert.assertEquals("Dev2", role3.getShortName());
        Assert.assertEquals(Role.RoleType.DEVELOPMENT_TEAM_MEMBER, role3.getType());
        Assert.assertEquals("Developer", role3.getDescription());

        ObservableList<Skill> skills = observableArrayList();
        Role role4 = new Role("Role", Role.RoleType.NONE, skills);
        Assert.assertEquals("Role", role4.getShortName());
        Assert.assertEquals(Role.RoleType.NONE, role4.getType());
        Assert.assertEquals(skills, role4.getRequiredSkills());
    }


    @Test
    public void testToString() {
        Role role = new Role();
        Role nullRole = null;
        Assert.assertEquals("Untitled Role", role.toString());
    }


    @Test
    public void testGetChildren() {
        Role role = new Role();
        Assert.assertNull(role.getChildren());
    }


    @Test
    public void testGetRoleType() {
        Global.currentWorkspace = new Workspace();
        Role poRole = null;
        for (Role role : Global.currentWorkspace.getRoles()) {
            if (role.getType() == Role.RoleType.PRODUCT_OWNER) {
                poRole = role;
            }
        }
        Assert.assertTrue(Role.getRoleFromType(Role.RoleType.PRODUCT_OWNER) == poRole);
    }


    @Test
    public void testPostSerialization() {
        Role role = new Role();
        Skill skill = new Skill();
        role.getSerializableRequiredSkills().add(skill);

        role.getRequiredSkills().clear();
        Assert.assertTrue(role.getRequiredSkills().isEmpty());

        role.getSerializableRequiredSkills().add(skill);
        role.postDeserialization();
        Assert.assertTrue(role.getRequiredSkills().contains(skill));
    }


    @Test
    public void testPrepSerialization() {
        Role role = new Role();
        Skill skill = new Skill();
        role.getRequiredSkills().add(skill);

        role.getSerializableRequiredSkills().clear();
        Assert.assertTrue(role.getSerializableRequiredSkills().isEmpty());

        role.getRequiredSkills().add(skill);
        role.prepSerialization();
        Assert.assertTrue(role.getSerializableRequiredSkills().contains(skill));
    }


    /**
     * Tests for Role setter methods.
     */
    @Test
    public void testRoleSetters() {
        Role role = new Role();
        role.setShortName("Tester");
        role.setDescription("Manual Testing");
        role.setType(Role.RoleType.NONE);

        Assert.assertEquals("Tester", role.getShortName());
        Assert.assertEquals("Manual Testing", role.getDescription());
        Assert.assertEquals(Role.RoleType.NONE, role.getType());
    }


    /**
     * Tests for Roles' XML generator method.
     */
    @Test
    public void testGenerateXML() {
        new ReportGenerator();
        Role role = new Role();
        role.setShortName("Tester");
        role.setDescription("Manual Testing");
        role.setType(Role.RoleType.NONE);

        Element roleElement = role.generateXML();
        Assert.assertEquals("[#text: Tester]", roleElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: Manual Testing]", roleElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals(5, roleElement.getChildNodes().getLength());
    }
}
