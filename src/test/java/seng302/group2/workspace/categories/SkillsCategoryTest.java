package seng302.group2.workspace.categories;

import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Jordane on 7/06/2015.
 */
public class SkillsCategoryTest {

    @Test
    public void testGetChildren() throws Exception {
        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;
        Collection<Skill> skills = new HashSet<>();
        Skill s1 = new Skill();
        Skill s2 = new Skill();
        skills.add(s1);
        skills.add(s2);
        ws.getSkills().addAll(skills);

        Category skillsCat = new SkillsCategory();
        Assert.assertTrue(skillsCat.getChildren().containsAll(skills));
    }

    @Test
    public void testToString() {
        Category skillsCat = new SkillsCategory();
        Assert.assertEquals("Skills", skillsCat.toString());
    }

    @Test
    public void testEquals() {
        Category s1 = new SkillsCategory();
        Category s2 = new SkillsCategory();
        Assert.assertEquals(s1, s2);
    }
}