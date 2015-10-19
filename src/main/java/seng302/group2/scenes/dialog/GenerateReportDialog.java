/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.scenes.dialog;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;
import seng302.group2.Global;
import seng302.group2.util.reporting.ReportGenerator;
import seng302.group2.workspace.SaharaItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to create a pop up dialog for creating a skill.
 *
 * @author drm127
 */

public class GenerateReportDialog extends Dialog<Map<String, String>> {
    /**
     * Displays the Dialog box for creating a skill.
     */
    public GenerateReportDialog() {
        this.setTitle("Generate Report");
        this.getDialogPane().setStyle(" -fx-max-width:450px; -fx-max-height: 480; -fx-pref-width: 450px; "
                + "-fx-pref-height: 480;");

        VBox grid = new VBox();
        grid.spacingProperty().setValue(10);
        Insets insets = new Insets(20, 20, 20, 20);
        grid.setPadding(insets);

        ButtonType btnTypeCreate = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(btnTypeCreate, ButtonType.CANCEL);

        CheckBoxTreeItem<SaharaItem> root = new CheckBoxTreeItem<>(Global.currentWorkspace);
        addChildren(root);
        root.setExpanded(true);
        root.setSelected(true);

        TreeView<SaharaItem> treeView = new TreeView<>(root);
        treeView.setCellFactory(CheckBoxTreeCell.<SaharaItem>forTreeView());

        grid.getChildren().addAll(treeView);

        //Add grid of controls to dialog
        this.getDialogPane().setContent(grid);

        this.setResultConverter(b -> {
            if (b == btnTypeCreate) {
                List<SaharaItem> checkedItems = getCheckedItems(root);
                if (!checkedItems.isEmpty()) {
                    ReportGenerator.generateReport(checkedItems);
                    this.close();
                }
            }
            return null;
        });
        // This bug still exsists but the window is no-longer resizable. May have
        // to look into in the future if we want resizable windows.
        this.setResizable(false);
    }


    /**
     * Adds children of the SaharaItem to its TreeItem wrapper recursively
     *
     * @param root The root tree item
     */
    protected static void addChildren(TreeItem<SaharaItem> root) {
        if (root.getValue() == null || root.getValue().getChildren() == null) {
            return;
        }
        for (SaharaItem item : root.getValue().getChildren()) {
            TreeItem<SaharaItem> child = new CheckBoxTreeItem<>(item);
            root.getChildren().add(child);
            addChildren(child);
        }
    }

    /**
     * Gets a list of items that have been checked.
     *
     * @param root The root tree item
     * @return A list of the checked items
     */
    protected static List<SaharaItem> getCheckedItems(TreeItem<SaharaItem> root) {

        List<SaharaItem> checkedItems = new ArrayList<>();

        if (((CheckBoxTreeItem) root).isSelected() || ((CheckBoxTreeItem) root).isIndeterminate()) {
            checkedItems.add(root.getValue());
        }

        for (TreeItem item : root.getChildren()) {
            checkedItems.addAll(getCheckedItems(item));
        }

        return checkedItems;
    }
}
