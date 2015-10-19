package seng302.group2.workspace.categories;

import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Jordane on 7/06/2015.
 */
public class ProjectCategoryTest {

    @Test
    public void testGetChildren() throws Exception {
        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;
        Collection<Project> projects = new HashSet<>();
        Project p1 = new Project();
        Project p2 = new Project();
        projects.add(p1);
        projects.add(p2);
        ws.getProjects().addAll(projects);

        Category projCat = new ProjectCategory();
        Assert.assertTrue(projCat.getChildren().containsAll(projects));
    }

    @Test
    public void testToString() {
        Category projectCategory = new ProjectCategory();
        Assert.assertEquals("Projects", projectCategory.toString());
    }

    @Test
    public void testEquals() {
        Category p1 = new ProjectCategory();
        Category p2 = new ProjectCategory();
        Assert.assertEquals(p1, p2);
    }
}