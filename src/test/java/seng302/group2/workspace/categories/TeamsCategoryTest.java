package seng302.group2.workspace.categories;

import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Jordane on 7/06/2015.
 */
public class TeamsCategoryTest {

    @Test
    public void testGetChildren() throws Exception {
        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;
        Collection<Team> teams = new HashSet<>();
        Team t1 = new Team();
        Team t2 = new Team();
        teams.add(t1);
        teams.add(t2);
        ws.getTeams().addAll(teams);

        Category teamsCat = new TeamsCategory();
        Assert.assertTrue(teamsCat.getChildren().containsAll(teams));
    }

    @Test
    public void testToString() {
        Category teamsCat = new TeamsCategory();
        Assert.assertEquals("Teams", teamsCat.toString());
    }

    @Test
    public void testEquals() {
        Category t1 = new TeamsCategory();
        Category t2 = new TeamsCategory();
        Assert.assertEquals(t1, t2);
    }
}