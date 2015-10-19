package seng302.group2.scenes.menu;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.controlsfx.control.PopOver;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.dialog.*;
import seng302.group2.scenes.information.tag.TagManagementPane;
import seng302.group2.util.revert.RevertManager;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.Category;
import seng302.group2.workspace.categories.subCategory.SubCategory;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;
import seng302.group2.workspace.workspace.Workspace.SaveLoadResult;

import java.util.Optional;

import static seng302.group2.scenes.dialog.DeleteDialog.showDeleteDialog;


/**
 * The main menu bar of the workspace window(s).
 *
 * @author Jordane Lew (jml168)
 */
@SuppressWarnings("deprecation")
public class MainMenuBar extends MenuBar {

    static PopOver taggingPopOver = new PopOver();

    /**
     * Constructor for the Menu Bar
     */
    public MainMenuBar() {
        // The menus and menu bar creation
        //MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        this.getMenus().add(fileMenu);

        // Create 'New >' sub-menu
        Menu newBranch = new Menu("New");

        // Create MenuItems for New submenu
        MenuItem newWorkspaceItem = createWorkspaceItem();
        MenuItem newProjectItem = createProjectItem();
        MenuItem newReleaseItem = createReleaseItem();
        MenuItem newBacklogItem = createBacklogItem();
        MenuItem newStoryItem = createStoryItem();
        MenuItem newSprintItem = createSprintItem();
        MenuItem newTeamItem = createTeamItem();
        MenuItem newPersonItem = createPersonItem();
        MenuItem newRoadMapItem = createRoadMapItem();
        MenuItem newSkillItem = createSkillItem();

        // Create other items for file menu
        MenuItem openItem = createOpenItem();
        MenuItem saveItem = createSaveItem();
        MenuItem saveAsItem = createSaveAsItem();

        MenuItem quitProgramItem = createQuitItem();

        newBranch.getItems().add(newWorkspaceItem);
        newBranch.getItems().add(newProjectItem);
        newBranch.getItems().add(newReleaseItem);
        newBranch.getItems().add(newBacklogItem);
        newBranch.getItems().add(newStoryItem);
        newBranch.getItems().add(newSprintItem);
        newBranch.getItems().add(newTeamItem);
        newBranch.getItems().add(newPersonItem);
        newBranch.getItems().add(newRoadMapItem);
        newBranch.getItems().add(newSkillItem);


        // Create 'Edit >' sub-menu
        Menu editMenu = new Menu("Edit");
        this.getMenus().add(editMenu);


        //Create MenuItems for Edit submenu
        MenuItem searchItem = createSearchItem();
        MenuItem advancedSearchItem = createAdvancedSearchItem();
        MenuItem undoItem = createUndoItem();
        MenuItem redoItem = createRedoItem();
        MenuItem deleteTreeItem = createDeleteTreeItem();
        MenuItem revertItem = createRevertItem();

        // Create 'Display >' sub-menu
        Menu displayMenu = new Menu("Display");
        this.getMenus().add(displayMenu);
        // Create MenuItems for Display submenu
        MenuItem toggleTree = createToggleTreeItem();
        MenuItem refreshItem = createRefreshItem();
        MenuItem taggingItem = createTagMaintenanceItem();
        displayMenu.getItems().addAll(toggleTree, refreshItem, taggingItem);


        // Create 'Generate >' menu
        Menu generateMenu = new Menu("Generate");
        this.getMenus().add(generateMenu);
        MenuItem reportItem = createReportItem();
        generateMenu.getItems().add(reportItem);


        // Add MenuItems to Menu
        fileMenu.getItems().addAll(newBranch, openItem,
                saveItem, saveAsItem, revertItem, new SeparatorMenuItem(),
                quitProgramItem);

        editMenu.getItems().addAll(searchItem, advancedSearchItem, undoItem, redoItem, deleteTreeItem);

        fileMenu.setOnShowing((event) -> {
            if (Global.currentWorkspace.getHasUnsavedChanges()) {
                revertItem.setDisable(false);
            }
            else {
                revertItem.setDisable(true);
            }
        });

        newBranch.setOnShowing((event) -> {
            newBacklogItem.setDisable(true);
            boolean PoExists = false;
            for (Person person : Global.currentWorkspace.getPeople()) {
                if (person.getSkills().containsAll(Role.getRoleFromType(Role.RoleType.PRODUCT_OWNER)
                        .getRequiredSkills())) {
                    PoExists = true;
                }
            }
            if (PoExists && !Global.currentWorkspace.getProjects().isEmpty()) {
                newBacklogItem.setDisable(false);  // Enable
            }

            if (Global.currentWorkspace.getProjects().isEmpty()) {
                newReleaseItem.setDisable(true);
                newStoryItem.setDisable(true);
                newSprintItem.setDisable(true);
            }
            else {
                newReleaseItem.setDisable(false);
                newStoryItem.setDisable(false);
                newSprintItem.setDisable(false);
            }
        });

        editMenu.setOnShowing((event) -> {
            redoItem.setDisable(!Global.commandManager.isRedoAvailable());
            undoItem.setDisable(!Global.commandManager.isUndoAvailable());

            if (Global.selectedTreeItem == null || Global.selectedTreeItem.getValue() == null) {
                deleteTreeItem.setDisable(true);
            }
            else if (Global.selectedTreeItem.getValue().getClass().getSuperclass() == Category.class
                    || Global.selectedTreeItem.getValue().getClass().getSuperclass() == SubCategory.class) {
                deleteTreeItem.setDisable(true);
            }
            else if (Global.selectedTreeItem.getValue().getClass() == Workspace.class) {
                deleteTreeItem.setDisable(true);
            }
            else if (Global.selectedTreeItem.getValue().getClass() == Skill.class) {
                Skill selectedSkill = (Skill) Global.selectedTreeItem.getValue();
                if (selectedSkill.getShortName().equals("Product Owner")
                        || selectedSkill.getShortName().equals("Scrum Master")) {
                    deleteTreeItem.setDisable(true);
                }
                else {
                    deleteTreeItem.setDisable(false);
                }
            }
            else if (Global.selectedTreeItem.getValue().getClass() == Role.class) {
                deleteTreeItem.setDisable(true);
            }
            else if (Global.selectedTreeItem.getValue().getClass() == Team.class) {
                Team selectedTeam = (Team) Global.selectedTreeItem.getValue();
                deleteTreeItem.setDisable(selectedTeam.isUnassignedTeam());
            }
            else {
                deleteTreeItem.setDisable(false);
            }
        });
    }

    /**
     * Creates a menu item "Workspace" and sets the on action event if "Workspace" is clicked.
     *
     * @return MenuItem Workspace
     */
    private static MenuItem createWorkspaceItem() {
        MenuItem newWorkspaceItem = new MenuItem("Workspace");
        newWorkspaceItem.setOnAction((ActionEvent event) ->
                        newWorkspaceAction()
        );

        newWorkspaceItem.setAccelerator(new KeyCodeCombination(KeyCode.N,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return newWorkspaceItem;
    }

    static void newWorkspaceAction() {
        if (Global.currentWorkspace == null
                || !Global.currentWorkspace.getHasUnsavedChanges()) {
            javafx.scene.control.Dialog creationDialog = new CreateWorkspaceDialog();
            creationDialog.show();
            App.refreshMainScene();
            Global.commandManager.clear();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save");
        alert.setHeaderText("Save Workspace?");
        alert.setContentText("Would you like to save your changes to the current workspace?");
        alert.getDialogPane().setStyle(" -fx-max-width:500px; -fx-max-height: 180px; -fx-pref-width: 500px; "
                + "-fx-pref-height: 180px;");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);


        Optional<ButtonType> result = alert.showAndWait();


        if (result.get() == buttonTypeYes) {
            SaveLoadResult saved = Workspace.saveWorkspace(Global.currentWorkspace, false);
            if (saved == SaveLoadResult.SUCCESS) {
                javafx.scene.control.Dialog creationDialog = new CreateWorkspaceDialog();
                creationDialog.show();
                App.refreshMainScene();
            }
            else {
                System.out.println("There was an error saving the file. Cancelling");
            }
        }
        else if (result.get() == buttonTypeNo) {
            javafx.scene.control.Dialog creationDialog = new CreateWorkspaceDialog();
            creationDialog.show();
            App.refreshMainScene();
        }
        if (result.get() != buttonTypeCancel) {
            Global.commandManager.clear();
            App.mainPane.expandTree();
        }
    }

    /**
     * Creates a menu item "Workspace" and sets the on action event if "Workspace" is clicked.
     *
     * @return MenuItem Workspace
     */
    private static MenuItem createProjectItem() {
        MenuItem newProjectItem = new MenuItem("Project");
        newProjectItem.setOnAction((ActionEvent event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateProjectDialog();
            creationDialog.show();
        });

        newProjectItem.setAccelerator(new KeyCodeCombination(KeyCode.P,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN,
                KeyCombination.SHORTCUT_DOWN));

        return newProjectItem;
    }

    /**
     * Creates a menu item for "Tag Management".
     *
     * @return MenuItem for tag management
     */
    private static MenuItem createTagMaintenanceItem() {
        MenuItem newProjectItem = new MenuItem("Tag Management");
        newProjectItem.setOnAction((ActionEvent event) -> showTaggingPopOver());

        newProjectItem.setAccelerator(new KeyCodeCombination(KeyCode.T,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN,
                KeyCombination.SHORTCUT_DOWN));

        return newProjectItem;
    }

    public static void showTaggingPopOver() {
        if (!taggingPopOver.isShowing()) {
            taggingPopOver = new PopOver();
            taggingPopOver.setDetachedTitle("Tag Management");
            taggingPopOver.setDetached(true);
            TagManagementPane tagManagementPane = new TagManagementPane(taggingPopOver);
            taggingPopOver.setContentNode(tagManagementPane);
            Platform.runLater(() -> {
                taggingPopOver.show(App.content, App.mainStage.getX()
                                + App.mainStage.getWidth() / 2 - 300,
                        App.mainStage.getY() + App.mainStage.getHeight() / 2 - 200);
                tagManagementPane.newTagField.requestFocus();
            });
        }
        else {
            taggingPopOver.requestFocus();
        }
    }

    public static void showTaggingPopOver(Tag tag) {
        showTaggingPopOver();
        try {
            TagManagementPane managementPane = ((TagManagementPane) taggingPopOver.getContentNode());
            managementPane.tagListView.getSelectionModel().select(tag);
            Platform.runLater(managementPane.tagListView::requestFocus);
        }
        catch (Exception ex) {

        }
    }

    /**
     * Creates a menu item "Person" and sets the on action event if "Person" is clicked.
     *
     * @return MenuItem Person
     */
    private static MenuItem createPersonItem() {
        MenuItem newPersonItem = new MenuItem("Person");
        newPersonItem.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreatePersonDialog();
            creationDialog.show();
        });
        newPersonItem.setAccelerator(new KeyCodeCombination(KeyCode.P,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return newPersonItem;
    }

    /**
     * Creates a menu item "Skill" and sets the on action event if "Skill" is clicked.
     *
     * @return MenuItem Skill
     */
    private static MenuItem createSkillItem() {
        MenuItem newSkillItem = new MenuItem("Skill");
        newSkillItem.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateSkillDialog();
            creationDialog.show();
        });
        newSkillItem.setAccelerator(new KeyCodeCombination(KeyCode.K,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return newSkillItem;
    }

    /**
     * Create a menu item "Release" and sets the action event if "Release" is clicked.
     *
     * @return MenuItem Release
     */
    private static MenuItem createReleaseItem() {

        MenuItem newReleaseItem = new MenuItem("Release");
        newReleaseItem.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateReleaseDialog(null);
            creationDialog.show();
        });
        newReleaseItem.setAccelerator(new KeyCodeCombination(KeyCode.R,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return newReleaseItem;
    }

    /**
     * Create a menu item "Sprint" and sets the action event if "Sprint" is clicked.
     *
     * @return MenuItem Sprint
     */
    private static MenuItem createSprintItem() {

        MenuItem newSprintItem = new MenuItem("Sprint");
        newSprintItem.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateSprintDialog(null);
            creationDialog.show();
        });

        return newSprintItem;
    }

    /**
     * Create a menu item "Story" and sets the action event if "Story" is clicked.
     *
     * @return MenuItem Story
     */
    private static MenuItem createStoryItem() {

        MenuItem newStoryItem = new MenuItem("Story");
        newStoryItem.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateStoryDialog(null);
            //creationDialog.show();
        });
        return newStoryItem;
    }

    /**
     * Create a menu item "RoadMap" and sets the action event if "RoadMap" is clicked.
     *
     * @return MenuItem RoadMap
     */
    private static MenuItem createRoadMapItem() {

        MenuItem newBacklogItem = new MenuItem("Road Map");
        newBacklogItem.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateRoadMapDialog();
            creationDialog.show();
        });
        return newBacklogItem;
    }

    /**
     * Create a menu item "Backlog" and sets the action event if "Backlog" is clicked.
     *
     * @return MenuItem Backlog
     */
    private static MenuItem createBacklogItem() {

        MenuItem newBacklogItem = new MenuItem("Backlog");
        newBacklogItem.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateBacklogDialog(null);
            creationDialog.show();
        });
        return newBacklogItem;
    }

    /**
     * Creates a menu item "Team" and sets the on action event if "Team" is clicked.
     *
     * @return MenuItem Team
     */
    private static MenuItem createTeamItem() {
        MenuItem newTeamItem = new MenuItem("Team");
        newTeamItem.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateTeamDialog();
            creationDialog.show();
        });
        newTeamItem.setAccelerator(new KeyCodeCombination(KeyCode.T,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return newTeamItem;
    }

    /**
     * Creates a menu item "Open" and sets the on action event if "Open" is clicked.
     *
     * @return MenuItem Open
     */
    private static MenuItem createOpenItem() {
        // Create 'Open' MenuItem
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction((event) -> loadAction());
        openItem.setAccelerator(new KeyCodeCombination(KeyCode.O,
                KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));
        return openItem;
    }

    static void loadAction() {
        if (!Global.currentWorkspace.getHasUnsavedChanges()) {
            Workspace.loadWorkspace();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Save");
        alert.setHeaderText("Save Workspace?");
        alert.setContentText("Would you like to save your changes to the current workspace?");
        alert.getDialogPane().setStyle(" -fx-max-width:500px; -fx-max-height: 180px; -fx-pref-width: 500px; "
                + "-fx-pref-height: 180px;");

        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == buttonTypeYes) {
            SaveLoadResult saved = Workspace.saveWorkspace(Global.currentWorkspace, false);
            if (saved == SaveLoadResult.SUCCESS) {
                Workspace.loadWorkspace();
                //App.refreshMainScene();
            }
        }
        else if (result.get() == buttonTypeNo) {
            Workspace.loadWorkspace();
            //App.refreshMainScene();
        }
    }

    /**
     * Creates a menu item "Save" and sets the on action event if "Save" is clicked.
     *
     * @return MenuItem Save
     */
    private static MenuItem createSaveItem() {
        // Create 'Save' MenuItem
        MenuItem saveItem = new MenuItem("Save");
        saveItem.setOnAction((event) -> saveAction());

        saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return saveItem;
    }

    /**
     * Creates a menu item for advanced search item with short cut key (ctrl-shift-F) bound to it.
     *
     * @return the advanced search menu item
     */
    private static MenuItem createAdvancedSearchItem() {
        // Create 'Search' MenuItem
        MenuItem newSearchItem = new MenuItem("Advanced Search");
        newSearchItem.setOnAction((event) -> {
            PopOver searchPopOver = new CreateSearchPopOver();
            if (!Global.advancedSearchExists) {
                Global.advancedSearchExists = true;
                searchPopOver.show(App.mainStage);
            }
        });
        newSearchItem.setAccelerator(new KeyCodeCombination(KeyCode.F,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return newSearchItem;

    }

    static void saveAction() {
        Workspace.saveWorkspace(Global.currentWorkspace, false);
    }

    /**
     * Creates a menu item "SaveAs" and sets the on action event if "SaveAs" is clicked.
     *
     * @return MenuItem SaveAs
     */
    private static MenuItem createSaveAsItem() {
        // Create 'Save As' MenuItem
        MenuItem saveAsItem = new MenuItem("Save As...");
        saveAsItem.setOnAction((event) ->
                Workspace.saveWorkspace(Global.currentWorkspace, true));

        saveAsItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHIFT_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return saveAsItem;
    }

    /**
     * Creates menu item for basic search with short cut (ctrl-F) bound to it
     *
     * @return a MenuItem used to create a search item
     */
    private static MenuItem createSearchItem() {
        MenuItem findItem = new MenuItem("Find");
        findItem.setOnAction((event) -> App.mainPane.focusSearch());

        findItem.setAccelerator(new KeyCodeCombination(KeyCode.F,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return findItem;
    }

    /**
     * Creates a menu item "Export" and sets the on action event to export the file as an XML
     * document.
     *
     * @return MenuItem Export
     */
    private static MenuItem createReportItem() {
        // Create 'Save' MenuItem
        MenuItem reportItem = new MenuItem("Report");
        reportItem.setOnAction((event) ->
                //ReportGenerator.generateReport());
                reportAction());

        //ShortcutItem not implemented yet
//        exportItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
//                KeyCombination.CONTROL_DOWN,
//                KeyCombination.SHORTCUT_DOWN));
        return reportItem;
    }

    static void reportAction() {
        javafx.scene.control.Dialog creationDialog = new GenerateReportDialog();
        creationDialog.show();
    }

    /**
     * Creates a menu item "Revert" and sets the on action event to revert the current workspace to
     * the Revert state from when the program is last saved or last opened
     *
     * @return MenuItem Revert
     */
    private static MenuItem createRevertItem() {
        // Create 'Save' MenuItem
        MenuItem revertItem = new MenuItem("Revert");
        revertItem.setOnAction((event) -> {
            if (!Global.currentWorkspace.getHasUnsavedChanges()) {
                RevertManager.revertWorkspace();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Save");
            alert.setHeaderText("Save Workspace?");
            alert.setContentText("Would you like to save your changes to the current workspace?");
            alert.getDialogPane().setStyle(" -fx-max-width:500px; -fx-max-height: 180px; -fx-pref-width: 500px; "
                    + "-fx-pref-height: 180px;");

            ButtonType buttonTypeYes = new ButtonType("Yes");
            ButtonType buttonTypeNo = new ButtonType("No");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo, buttonTypeCancel);


            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == buttonTypeYes) {
                SaveLoadResult saved = Workspace.saveWorkspace(Global.currentWorkspace, false);
                if (saved == SaveLoadResult.SUCCESS) {
                    RevertManager.revertWorkspace();
                }
            }
            else if (result.get() == buttonTypeNo) {
                RevertManager.revertWorkspace();
            }
        });


        //ShortcutItem not implemented yet
//        exportItem.setAccelerator(new KeyCodeCombination(KeyCode.S,
//                KeyCombination.CONTROL_DOWN,
//                KeyCombination.SHORTCUT_DOWN));
        return revertItem;
    }

    /**
     * Creates a menu item "Quit" and sets the on action event if "Quit" is clicked.
     *
     * @return MenuItem Quit
     */
    private static MenuItem createQuitItem() {
        MenuItem quitProgramItem = new MenuItem("Quit");
        quitProgramItem.setOnAction((event) ->
                App.exitApp());

        return quitProgramItem;
    }

    /**
     * Creates a menu item "Undo" and sets the on action event if "Undo" is clicked.
     *
     * @return MenuItem Undo
     */
    private static MenuItem createUndoItem() {
        // Create 'Undo' MenuItem
        MenuItem undoItem = new MenuItem("Undo");
        undoItem.setOnAction((event) -> undoAction());

        undoItem.setAccelerator(new KeyCodeCombination(KeyCode.Z,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return undoItem;
    }

    static void undoAction() {
        Global.commandManager.undo();
        MainToolbar.undoRedoToggle();
    }

    /**
     * Creates a menu item "Redo" and sets the on action event if "Redo" is clicked.
     *
     * @return MenuItem Redo
     */
    private static MenuItem createRedoItem() {
        // Create 'Redo' MenuItem
        MenuItem redoItem = new MenuItem("Redo");
        redoItem.setOnAction((event) ->
                redoAction());

        redoItem.setAccelerator(new KeyCodeCombination(KeyCode.Y,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return redoItem;
    }

    static void redoAction() {
        Global.commandManager.redo();
        MainToolbar.undoRedoToggle();
    }

    /**
     * Creates a menu item "Delete" item and sets the on action event if "Delete" is clicked.
     * Piggy-backing off the context menu for now. Better they are synced than duplicate methods.
     *
     * @return MenuItem Delete
     */
    private static MenuItem createDeleteTreeItem() {
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction((event) -> {
            showDeleteDialog((SaharaItem) Global.selectedTreeItem
                    .getValue());
        });

        deleteItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE,
                KeyCombination.SHIFT_DOWN));
        return deleteItem;
    }

    /**
     * Creates a menu item "Toggle Tree" and sets the on action event if "Toggle Tree" is clicked.
     *
     * @return MenuItem ToggleTree
     */
    private static MenuItem createToggleTreeItem() {
        MenuItem toggleItem = new MenuItem("Toggle Menu");
        toggleItem.setOnAction((event) -> {
            App.mainPane.toggleTree();
            //MainScene.menuHidden = !MainScene.menuHidden;
            //App.refreshMainScene();
        });

        toggleItem.setAccelerator(new KeyCodeCombination(KeyCode.M,
                KeyCombination.CONTROL_DOWN,
                KeyCombination.SHORTCUT_DOWN));
        return toggleItem;
    }

    /**
     * Creates a menu item "Refresh" that refreshes the main scene when it is selected, or when F5 is pressed.
     *
     * @return A refresh menu item
     */
    private static MenuItem createRefreshItem() {
        MenuItem refreshItem = new MenuItem("Refresh");
        refreshItem.setOnAction((event) -> App.refreshMainScene());
        refreshItem.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        return refreshItem;
    }
}
