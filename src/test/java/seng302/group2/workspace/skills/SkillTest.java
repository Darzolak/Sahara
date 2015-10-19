package seng302.group2.workspace.skills;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.workspace.Workspace;

import java.util.ArrayList;


/**
 * A series of tests relating to Skills
 *
 * @author Cameron Williams (crw73)
 * @author Bronson McNaughton (btm38)
 */
public class SkillTest {
    /**
     * A simple test for the Skill constructors.
     */
    @Test
    public void testSkillConstructors() {
        Skill skill = new Skill();
        Assert.assertEquals("Untitled Skill", skill.getShortName());
        Assert.assertEquals("no description", skill.getDescription());
        Assert.assertEquals("Untitled Skill", skill.toString());
        Assert.assertEquals(null, skill.getChildren());

        Skill skill2 = new Skill("C#", "A better language than Java");
        Assert.assertEquals("C#", skill2.getShortName());
        Assert.assertEquals("A better language than Java", skill2.getDescription());
        Assert.assertEquals("C#", skill2.toString());

    }

    /**
     * Tests for Skills' setter methods.
     */
    @Test
    public void testSkillsSetters() {
        Skill skill = new Skill();
        skill.setShortName("C#");
        skill.setDescription("A better language than Java");

        Assert.assertEquals("C#", skill.getShortName());
        Assert.assertEquals("A better language than Java", skill.getDescription());
        Assert.assertEquals("C#", skill.toString());
    }

    /**
     * Tests the compareTo method of Skill to ensure it correctly returns an int representing if a
     * shortName is larger or not.
     */
    @Test
    public void testCompareTo() {
        Skill skill1 = new Skill();
        Skill skill2 = new Skill();
        skill1.setShortName("A");
        skill2.setShortName("Z");

        Assert.assertTrue(skill1.compareTo(skill2) <= 0);
        Assert.assertTrue(skill2.compareTo(skill1) >= 0);
        Assert.assertTrue(skill1.compareTo(skill1) == 0);
    }

    @Test
    public void testEdit() {
        Global.currentWorkspace = new Workspace();

        Skill skill = new Skill("C#", "A better language than Java");
        Tag tag = new Tag("Tag");
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(tag);
        skill.edit("Java", "A better language than C#", tags);

        Assert.assertEquals("Java", skill.getShortName());
        Assert.assertEquals("A better language than C#", skill.getDescription());
        Assert.assertEquals(1, skill.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", skill.getTags().get(0).getName());

        Assert.assertEquals("the edit of Skill \"" + skill.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertEquals("C#", skill.getShortName());
        Assert.assertEquals("A better language than Java", skill.getDescription());
        Assert.assertEquals(0, skill.getTags().size());
        Assert.assertEquals(0, Global.currentWorkspace.getAllTags().size());

        Global.commandManager.redo();

        Assert.assertEquals("Java", skill.getShortName());
        Assert.assertEquals("A better language than C#", skill.getDescription());
        Assert.assertEquals(1, skill.getTags().size());
        Assert.assertEquals(1, Global.currentWorkspace.getAllTags().size());
        Assert.assertEquals("Tag", skill.getTags().get(0).getName());
    }

    @Test
    public void testDeleteSkill() {
        Skill skill = new Skill("C#", "A better language than Java");

        skill.deleteSkill();
        Assert.assertFalse(Global.currentWorkspace.getSkills().contains(skill));

        Assert.assertEquals("the deletion of Skill \"" + skill.getShortName() + "\"",
                Global.commandManager.getUndoCloneStack().peek().getString());
        Global.commandManager.undo();

        Assert.assertTrue(Global.currentWorkspace.getSkills().contains(skill));
    }

    /**
     * Tests for Skills' XML generator method.
     */
    @Test
    public void testGenerateXML() {
        new ReportGenerator();
        Skill skill = new Skill("C#", "A better language than Java");

        Element skillElement = skill.generateXML();
        Assert.assertEquals("[#text: C#]", skillElement.getChildNodes().item(1).getChildNodes().item(0).toString());
        Assert.assertEquals("[#text: A better language than Java]", skillElement.getChildNodes().item(2).getChildNodes().item(0).toString());
        Assert.assertEquals(4, skillElement.getChildNodes().getLength());
    }
}
