/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.util.validation;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


/**
 * Tests that the short name validator performs correctly.
 *
 * @author Jordane
 */
public class ShortNameValidatorTest {
    /**
     * Performs the initialization state before tests are run
     */
    @BeforeClass
    public static void setUp() {
        LocalDate dob = LocalDate.now();
        try {
            dob = LocalDate.parse("19/12/1994", Global.dateFormatter);
        }
        catch (DateTimeParseException e) {
            Assert.fail("Date parsing error, needs fixing");
        }

        Workspace ws = new Workspace("WS", "Workspace", "Desc");
        Global.currentWorkspace = ws;

        Person pers = new Person("btm38", "McNaughton", "Bronson", "btm38@gmail.com",
                "A really cool dude", dob);
        Global.currentWorkspace.add(pers);

        Project proj = new Project("PROJ", "Project", "Desc");
        Release rel = new Release();
        rel.setShortName("RELEASE");
        Team team = new Team("TEAM", "Desc");
        Person apers = new Person();
        apers.setShortName("PERS");
        Skill skill = new Skill("SKILL", "Desc");
        Role role = new Role("ROLE", Role.RoleType.NONE);

        proj.addWithoutUndo(rel);
        ws.getProjects().add(proj);
        ws.getTeams().add(team);
        ws.getPeople().add(apers);
        ws.getSkills().add(skill);
        ws.getRoles().add(role);
    }


    /**
     * Test of validateShortName method, of class ShortNameValidator.
     */
    @Test
    public void testValidateShortName() {
        // People
        Assert.assertEquals(ValidationStatus.NULL, ShortNameValidator.validateShortName("", null));
        Assert.assertEquals(ValidationStatus.NON_UNIQUE,
                ShortNameValidator.validateShortName("btm38", null));
        Assert.assertEquals(ValidationStatus.VALID, ShortNameValidator.validateShortName("new", null));
        Assert.assertEquals(ValidationStatus.OUT_OF_RANGE, ShortNameValidator.validateShortName(
                "this is much more than 20 characters long", null));

        Assert.assertEquals(ValidationStatus.NON_UNIQUE, ShortNameValidator.validateShortName("WS", null));
        Assert.assertEquals(ValidationStatus.NON_UNIQUE, ShortNameValidator.validateShortName("PROJ", null));
        Assert.assertEquals(ValidationStatus.NON_UNIQUE, ShortNameValidator.validateShortName("TEAM", null));
        Assert.assertEquals(ValidationStatus.NON_UNIQUE, ShortNameValidator.validateShortName("PERS", null));
        Assert.assertEquals(ValidationStatus.NON_UNIQUE, ShortNameValidator.validateShortName("SKILL", null));
        Assert.assertEquals(ValidationStatus.NON_UNIQUE, ShortNameValidator.validateShortName("ROLE", null));
        Assert.assertEquals(ValidationStatus.NON_UNIQUE, ShortNameValidator.validateShortName("RELEASE", null));
    }
}
