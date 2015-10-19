package seng302.group2.scenes.contextmenu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import seng302.group2.Global;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.team.Team;
import seng302.group2.workspace.workspace.Workspace;

import static seng302.group2.scenes.dialog.DeleteDialog.showDeleteDialog;


/**
 * ContextMenu class for instances of Person, Skill and Team
 *
 * @author swi67, jml168, btm38
 */
@SuppressWarnings("deprecation")
public class ElementTreeContextMenu extends ContextMenu {

    /**
     * Constructor. Sets the event for contextMenu buttons
     */
    public ElementTreeContextMenu() {
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem editItem = new MenuItem("Edit");

        /*Categories selectedCategory = getSelectedCategory();

        final Categories finalSelectedCategory = selectedCategory;  // for use in lamda expr.*/

        SaharaItem selected = (SaharaItem) Global.selectedTreeItem.getValue();

        //"Edit" button event
        editItem.setOnAction(e -> selected.switchToInfoScene(true));

        //"Delete" button event
        deleteItem.setOnAction(e -> showDeleteDialog(selected));

        this.getItems().addAll(editItem, deleteItem);


        /* Specific enable/disables based on items */
        if (selected instanceof Team) {
            Team selectedTeam = (Team) Global.selectedTreeItem.getValue();
            if (selectedTeam.isUnassignedTeam()) {
                editItem.setDisable(true);
                deleteItem.setDisable(true);
            }
            else {
                editItem.setDisable(false);
                deleteItem.setDisable(false);
            }
        }

        if (selected instanceof Role) {
            Role selectedRole = (Role) Global.selectedTreeItem.getValue();
            if (!selectedRole.isDefault()) {
                editItem.setDisable(true);
                deleteItem.setDisable(true);
            }
            else {
                editItem.setDisable(false);
                deleteItem.setDisable(false);
            }
        }

        if (selected instanceof Skill) {

            editItem.setDisable(false);
            deleteItem.setDisable(false);

            for (Role role : Global.currentWorkspace.getRoles()) {
                if (role.getType() == Role.RoleType.PRODUCT_OWNER
                        || role.getType() == Role.RoleType.SCRUM_MASTER) {
                    if (role.getRequiredSkills().contains(selected)) {
                        editItem.setDisable(true);
                        deleteItem.setDisable(true);
                    }
                }
            }
        }

        if (selected instanceof Workspace) {
            deleteItem.setDisable(true);
        }
    }
}
