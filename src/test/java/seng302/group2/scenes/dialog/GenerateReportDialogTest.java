package seng302.group2.scenes.dialog;

import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import org.junit.Assert;
import org.junit.Test;
import seng302.group2.Global;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.workspace.Workspace;

import java.util.List;

public class GenerateReportDialogTest {

    @Test
    public void testAddChildren() throws Exception {
        Workspace ws = new Workspace();
        TreeItem<SaharaItem> root = new CheckBoxTreeItem<>(ws);
        GenerateReportDialog.addChildren(root);

        // Contains all the items
        for (TreeItem<SaharaItem> item : root.getChildren()) {
            Assert.assertTrue(ws.getChildren().contains(item.getValue()));
        }

        // Is the same size
        Assert.assertEquals(root.getChildren().size(), ws.getChildren().size());

        // Thus, they are added correctly with no added nasties.
    }

    @Test
    public void testGetCheckedItems() throws Exception {
        Workspace ws = new Workspace();
        Global.currentWorkspace = ws;

        TreeItem<SaharaItem> root = new CheckBoxTreeItem<>(ws);
        GenerateReportDialog.addChildren(root);

        // Select the Project and Team tree items
        for (TreeItem<SaharaItem> item : root.getChildren()) {
            ((CheckBoxTreeItem)item).setSelected(true);
        }

        List<SaharaItem> itemsSet = GenerateReportDialog.getCheckedItems(root);
        recursiveCheckItems(ws, itemsSet);
    }


    private void recursiveCheckItems(SaharaItem root, List<SaharaItem> list) {
        //System.out.println(root + " in " + set);

        boolean mapped = false;
        for (SaharaItem item : list) {
            if (item.equivalentTo(root)) {
                mapped = true;
                break;
            }
        }
        Assert.assertTrue(mapped);

        if (root.getChildren() != null) {
            for (SaharaItem child : root.getChildren()) {
                recursiveCheckItems(child, list);
            }
        }
    }
}