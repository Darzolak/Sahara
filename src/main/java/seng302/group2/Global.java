package seng302.group2;

import javafx.scene.control.TreeItem;
import seng302.group2.util.undoredo.CommandManager;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import java.time.format.DateTimeFormatter;

/**
 * A class that keeps static local variables for the runtime of the application. Useful for tracking
 * the current workspace, command manager, and other items.
 *
 * @author Jordane
 */
public final class Global {
    public static Workspace currentWorkspace = new Workspace();
    public static TreeItem selectedTreeItem = new TreeItem();
    public static String lastSaveLocation = "/";
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static CommandManager commandManager = new CommandManager();
    public static boolean advancedSearchExists = false;


    /**
     * Gets the unassigned team
     *
     * @return the unassigned team
     */
    public static Team getUnassignedTeam() {
        if (currentWorkspace.getTeams() == null) {
            Team.createUnassignedTeam();
        }
        for (Team team : currentWorkspace.getTeams()) {
            if (team.isUnassignedTeam()) {
                return team;
            }
        }
        return Team.createUnassignedTeam();
    }


    /**
     * Mark the current workspace as changed.
     */
    public static void setCurrentWorkspaceChanged() {
        currentWorkspace.setChanged();
        App.refreshWindowTitle();
    }


    /**
     * Mark the current workspace as unchanged.
     */
    public static void setCurrentWorkspaceUnchanged() {
        currentWorkspace.setUnchanged();
        App.refreshWindowTitle();
    }

}
