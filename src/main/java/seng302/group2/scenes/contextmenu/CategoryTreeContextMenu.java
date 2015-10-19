package seng302.group2.scenes.contextmenu;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import seng302.group2.Global;
import seng302.group2.workspace.categories.Category;

/**
 * Created by Jordane on 14/04/2015.
 * Edited by swi67 14/04/2015
 */
public class CategoryTreeContextMenu extends ContextMenu {

    /**
     * Constructor for context menu for categories
     */
    public CategoryTreeContextMenu() {
        MenuItem createItem = new MenuItem("Create new...");
        createItem.setDisable(false);
        createItem.setOnAction(e -> {
            ((Category) Global.selectedTreeItem.getValue()).showCreationDialog();
            //showCreateNewDialog(Global.selectedTreeItem.getValue().toString());
        });

        this.getItems().addAll(createItem);
    }


    /**
     * Constructor for context menu for categories
     *
     * @param enabled the visibility of the context menu
     */
    public CategoryTreeContextMenu(Boolean enabled) {
        MenuItem createItem = new MenuItem("Create new...");
        createItem.setDisable(!enabled);
        createItem.setOnAction(e -> {
            ((Category) Global.selectedTreeItem.getValue()).showCreationDialog();
            //showCreateNewDialog(Global.selectedTreeItem.getValue().toString());
        });

        this.getItems().addAll(createItem);
    }
}
