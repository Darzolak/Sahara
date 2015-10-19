package seng302.group2;

import org.junit.Assert;
import org.junit.Test;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

/**
 * A class for testing the GlobalClass.
 * Created by Jordane on 27/04/2015.
 */
public class GlobalTest {

    /**
     * Tests that the workspace gets the unassigned team correctly
     */
    @Test
    public void testGetUnassignedTeam() {
        Workspace workspace = new Workspace();
        Global.currentWorkspace = workspace;

        Team unassignedTeam = null;
        for (Team team : workspace.getTeams()) {
            if (team.isUnassignedTeam()) {
                unassignedTeam = team;
            }
        }

        Assert.assertNotNull(unassignedTeam);
        Assert.assertEquals(Global.getUnassignedTeam(), unassignedTeam);
    }

    /**
     * Tests that setting the current workspace as changed works correctly
     */
    @Test
    public void testSetCurrentWorkspaceChanged() {
        Workspace workspace = new Workspace();
        Global.currentWorkspace = workspace;

        Global.setCurrentWorkspaceChanged();

        Assert.assertTrue(workspace.getHasUnsavedChanges());
    }

    /**
     * Tests that setting the current workspace as unchanged works correctly
     */
    @Test
    public void testSetCurrentWorkspaceUnchanged() {
        Workspace workspace = new Workspace();
        Global.currentWorkspace = workspace;

        Global.setCurrentWorkspaceUnchanged();

        Assert.assertFalse(workspace.getHasUnsavedChanges());
    }
}