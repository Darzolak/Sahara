package seng302.group2.scenes;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.*;
import org.controlsfx.control.StatusBar;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.information.StickyBar;
import seng302.group2.scenes.information.workspace.WorkspaceScene;
import seng302.group2.scenes.menu.MainMenuBar;
import seng302.group2.scenes.menu.MainToolbar;
import seng302.group2.scenes.treeView.HierarchyTracker;
import seng302.group2.scenes.treeView.TreeViewWithItems;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.Category;
import seng302.group2.workspace.categories.subCategory.SubCategory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

import static seng302.group2.App.content;
import static seng302.group2.App.refreshWindowTitle;


/**
 * A class for holding JavaFX scenes used in the workspace
 *
 * @author Jordane Lew (jml168)
 */
public class MainPane extends BorderPane {
    public static MainToolbar toolBar = null;
    Preferences userPrefs = Preferences.userNodeForPackage(getClass());
    private TreeViewWithItems<SaharaItem> treeView;
    private Pane informationPane = new Pane();
    private HBox statusBar;
    public ScrollPane contentPane;
    private boolean menuHidden = false;
    private double dividerPositions;

    public StickyBar stickyBar = new StickyBar();


    /**
     * The default constructor of the main pane that performs basic initialisation
     */
    public MainPane() {
        this.init();

        if (contentPane == null) {
            contentPane = new ScrollPane();
        }
        content.getItems().clear();
        menuHidden = userPrefs.getBoolean("tree.hidden", false);
        if (!menuHidden) {
            content.getItems().add(treeView);
        }

        VBox.setVgrow(contentPane, Priority.ALWAYS);

        VBox stickyBarBox = new VBox();
        VBox.setVgrow(stickyBarBox, Priority.SOMETIMES);
        stickyBarBox.setAlignment(Pos.BOTTOM_LEFT);
        stickyBarBox.getChildren().addAll(new Separator(), stickyBar);

        VBox contentVBox = new VBox();
        contentVBox.getChildren().addAll(contentPane, stickyBarBox);

        content.getItems().add(contentVBox);
        statusBar = statusBar("Opened the Project");

        this.setCenter(content);

//        statusBar = statusBar(Global.commandManager.lastAction());

        this.setBottom(statusBar);

        double split = userPrefs.getDouble("pane.split", 0.225);

        Platform.runLater(() -> content.setDividerPositions(split));
    }

    /**
     * Gets the Toolbar of the App
     *
     * @return The toolbar object of the app.
     */
    public static MainToolbar getToolBar() {
        return toolBar;
    }

    public TreeViewWithItems<SaharaItem> getTree() {
        return treeView;
    }

    /**
     * Refreshes the tree menu of the main pane
     */
    public void refreshTree() {
        if (treeView == null) {
            // Create the tree view
            treeView = new TreeViewWithItems<SaharaItem>(new TreeItem<>());
            treeView.setShowRoot(false);
        }

        HierarchyTracker.refreshMap(treeView);

        /*SaharaItem selectedItem = null;
        if (treeView.getSelectionModel().getSelectedItem() != null) {
            selectedItem = treeView.getSelectionModel().getSelectedItem().getValue();
        }*/
        TreeItem<SaharaItem> selectedItem = treeView.getSelectionModel().getSelectedItem();

        treeView.setItems(FXCollections.observableArrayList(Global.currentWorkspace));

        HierarchyTracker.restoreMap(treeView);

        // Select the selected tree item in the tree
        if (selectedItem != null) {
            treeView.selectItem(selectedItem);
        }
        else if (Global.selectedTreeItem != null) {
            treeView.selectItem(Global.selectedTreeItem);
        }
        else {
            Platform.runLater(() -> {
                treeView.selectItem(Global.currentWorkspace);
                stickyBar.construct(StickyBar.STICKYTYPE.INFO);
                treeView.getSelectionModel().getSelectedItem().setExpanded(true);
            });

        }
    }

    /**
     * Expands all of the items inside of the tree recursively
     */
    public void expandTree() {
        HierarchyTracker.expandAll(treeView);
    }

    /**
     * Toggles if the tree menu is present in the pane
     */
    public void toggleTree() {
        if (content.getItems().contains(treeView)) {
            dividerPositions = content.getDividerPositions()[0];
            content.getItems().remove(treeView);
        }
        else {
            content.getItems().add(0, treeView);
            content.setDividerPositions(dividerPositions);
        }

        userPrefs.putBoolean("tree.hidden", !content.getItems().contains(treeView));
    }

    public static HBox statusBar(String input) {
        HBox statusBarBox = new HBox();
        StatusBar statusBar = new StatusBar();
        if (input != null) {
            statusBar.setText(input + " at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm" + ".")));
        }
        else {
            statusBar.setText("");
        }
        HBox.setHgrow(statusBarBox, Priority.ALWAYS);
        HBox.setHgrow(statusBar, Priority.ALWAYS);
        statusBarBox.getChildren().add(statusBar);
        return statusBarBox;
    }
    
    public void refreshStatusBar(String input) {
        this.getChildren().remove(statusBar);
        statusBar = statusBar(input);
        this.setBottom(statusBar);
    }

    /**
     * Gets the main content within the scene
     *
     * @return The information panes.
     */
    public Node getContent() {
        return contentPane.getContent();
    }

    /**
     * Sets the content pane child to the given node
     *
     * @param node The node to replace the current content pane
     */
    public void setContent(Node node) {
//        Platform.runLater(() -> {
        contentPane.setContent(node);
//            });

    }


    /**
     * Refreshes the content child of the pane
     */
    public void refreshContent() {
        SaharaItem selected = null;

        try {
            selected = (SaharaItem) Global.selectedTreeItem.getValue();
            if (selected == null) {
                return;
            }
        }
        catch (NullPointerException ex) {
            return;
        }

        // Refresh based on the selected item's type
        if (selected instanceof SubCategory) {
            selected.switchToCategoryScene((Category) selected);
        }
        else if (selected instanceof Category) {
            selected.switchToCategoryScene();
        }
        else {
            selected.switchToInfoScene();
        }
    }

    /**
     * Refreshes each of the child elements in the pane
     */
    public void refreshAll() {
        refreshTree();
        refreshContent();
        refreshWindowTitle();
    }

    /**
     * Performs the initialisation tasks of the main pane including adding children elements and styling
     */
    private void init() {
        MenuBar menuBar = new MainMenuBar();
        toolBar = new MainToolbar();
        VBox topBar = new VBox();
        topBar.getChildren().addAll(menuBar, toolBar);
        this.setTop(topBar);

        this.refreshTree();

        contentPane = new ScrollPane(informationPane);
        contentPane.setFitToHeight(true);
        contentPane.setFitToWidth(true);
        contentPane.setStyle("-fx-background-color:transparent;");
        this.setContent(new WorkspaceScene(Global.currentWorkspace));

        this.heightProperty().addListener((arg0, arg1, arg2) -> {
            App.content.setPrefHeight(arg2.doubleValue());
        });

        treeView.widthProperty().addListener(event -> {
            userPrefs.putDouble("pane.split", content.getDividerPositions()[0]);
        });
    }

    /**
     * Selects the given item inside the pane (by selecting it through the tree as if it were clicked)
     *
     * @param item The item to select
     */
    public void selectItem(SaharaItem item) {
        treeView.selectItem(item);
    }

    /**
     * Sets the focus on the toolbar's search field
     */
    public void focusSearch() {
        toolBar.focusSearch();
    }
}
