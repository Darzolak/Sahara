package seng302.group2.util.revert;

import com.google.gson.Gson;
import javafx.application.Platform;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.util.serialization.SerialBuilder;
import seng302.group2.util.undoredo.Command;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.workspace.Workspace;

import java.util.Stack;

/**
 * Allows {@link seng302.group2.workspace.workspace.Workspace}s to be reverted to the last given state (as a
 * json serialised workspace).
 * Created by crw73 on 12/05/15.
 */
public class RevertManager {
    private static String revertWorkspace;
    private static Stack<Command> revertUndos;
    private static Gson gson = SerialBuilder.getBuilder();

    /**
     * Reverts to the last revert state by deserialising the last workspace state and making it the
     * current workspace
     */
    public static void revertWorkspace() {
        if (revertWorkspace != null) {
            Stack<Command> undos = (Stack<Command>) revertUndos.clone();

            Workspace currentWorkspace = SerialBuilder.getBuilder().fromJson(revertWorkspace,
                    Workspace.class);
            Workspace.postDeserialization(currentWorkspace);

            Global.currentWorkspace = currentWorkspace;

            Global.commandManager.clear();
            Global.commandManager.setUndos(undos);

            for (Command command : undos) {
                command.map(currentWorkspace.getItemsSet());
            }

            SaharaItem.refreshIDs();

            App.refreshMainScene();
            if (App.mainPane != null) {
                App.mainPane.refreshStatusBar("Workspace has been Reverted.");
            }

            Platform.runLater(() -> {
                if (App.mainPane != null && App.mainPane.getTree() != null) {
                    if (Global.currentWorkspace != null) {
                        App.mainPane.getTree().selectItem(Global.currentWorkspace);
                    }
                    App.mainPane.stickyBar.construct(StickyBar.STICKYTYPE.INFO);
                    if (App.mainPane.getTree().getSelectionModel().getSelectedItem() != null) {
                        App.mainPane.getTree().getSelectionModel().getSelectedItem().setExpanded(true);
                    }
                }
            });
        }
    }

    /**
     * Updates the reverted state to the given json string
     *
     * @param json The json serialization of the state to update to
     */
    public static void updateRevertState(String json) {
        revertWorkspace = json;
        if (Global.commandManager != null) {
            revertUndos = Global.commandManager.getUndoCloneStack();
        }
    }

    /**
     * Updates the reverted state to the current workspace
     */
    public static void updateRevertState() {
        Workspace.prepSerialization(Global.currentWorkspace);
        String json = gson.toJson(Global.currentWorkspace);
        updateRevertState(json);
    }

    /**
     * Clears the revert state including the serialised workspace and undo/redo history
     */
    public static void clear() {
        revertWorkspace = null;
        try {
            revertUndos.clear();
        }
        catch (NullPointerException ex) {
            revertUndos = new Stack<>();
        }
    }
}
