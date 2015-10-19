package seng302.group2.scenes.treeView;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.contextmenu.CategoryTreeContextMenu;
import seng302.group2.scenes.contextmenu.ElementTreeContextMenu;
import seng302.group2.workspace.HierarchyData;
import seng302.group2.workspace.SaharaItem;
import seng302.group2.workspace.categories.Category;
import seng302.group2.workspace.categories.RolesCategory;
import seng302.group2.workspace.categories.subCategory.SubCategory;
import seng302.group2.workspace.categories.subCategory.project.BacklogCategory;
import seng302.group2.workspace.categories.subCategory.project.SprintCategory;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.role.Role;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * This class extends the {@link TreeView} to use items as a data source.
 * This allows you to treat a {@link TreeView} in a similar way as a
 * {@link javafx.scene.control.ListView} or {@link javafx.scene.control.TableView}.
 * Each item in the list must implement the {@link seng302.group2.workspace.HierarchyData} interface, in order to map
 * the recursive nature of the tree data to the tree view.
 * Each change in the underlying data (adding, removing, sorting) will then be automatically
 * reflected in the UI.
 *
 * @param <T> The type of treeview items
 * @author Christian Schudt (modified by Jordane Lew)
 */
public class TreeViewWithItems<T extends HierarchyData<T>> extends TreeView<T> {

    /**
     * Keep hard references for each listener, so that they don't get garbage collected too soon.
     */
    private final Map<TreeItem<T>, ListChangeListener<T>> hardReferences = new HashMap<TreeItem<T>,
            ListChangeListener<T>>();

    /**
     * Also store a reference from each tree item to its weak listeners, so that the listener can be
     * removed, when the tree item gets removed.
     */
    private final Map<TreeItem<T>, WeakListChangeListener<T>> weakListeners =
            new HashMap<TreeItem<T>, WeakListChangeListener<T>>();

    private ObjectProperty<ObservableList<? extends T>> items =
            new SimpleObjectProperty<ObservableList<? extends T>>(this, "items");

    /**
     * Creates the tree view.
     */
    public TreeViewWithItems() {
        super();
        init();
    }


    /**
     * Creates the tree view with a given root.
     *
     * @param root The root tree item.
     * @see TreeView#TreeView(javafx.scene.control.TreeItem)
     */
    public TreeViewWithItems(TreeItem<T> root) {
        super(root);
        init();
    }


    /**
     * Refreshes the tree by clearing the root item and updating.
     */
    public void refresh() {
        T currentSelection = null;
        if (Global.selectedTreeItem != null) {
            currentSelection = (T) Global.selectedTreeItem.getValue();
        }

        clear(getRoot());
        updateItems();

        if (currentSelection != null) {
            selectItem(currentSelection);
        }
    }


    /**
     * Initializes the tree view.
     */
    private void init() {
        setMinWidth(160);
        setMaxWidth(400);

        setContextMenu(new CategoryTreeContextMenu(true));

        rootProperty().addListener((observableValue, oldRoot, newRoot) -> {
            clear(oldRoot);
            updateItems();
        });

        setItems(FXCollections.<T>observableArrayList());

        /* Do not use ChangeListener, because it won't trigger if old list equals new list (but in
        fact different references). */
        items.addListener(observable -> {
            clear(getRoot());
            updateItems();
        });

        /* Sets the App.selectedTreeItem when a new selection is made, and sets the information
         * shown in the main pane to the selected item's details */
        this.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {

                    Global.selectedTreeItem = newValue;

                    SaharaItem selected = null;

                    //Updates the display pane to be pane for the selectItem
                    if (Global.selectedTreeItem == null
                            || Global.selectedTreeItem.getValue() == null) {
                        // Nothing is selected, make a default selection?
                        Global.currentWorkspace.switchToInfoScene();
                        return;
                    }

                    selected = (SaharaItem) Global.selectedTreeItem.getValue();

                    // Make a switch based on the type
                    if (selected instanceof SubCategory) {
                        selected.switchToCategoryScene((Category) selected);
                        setContextMenu(new CategoryTreeContextMenu());
                        if (selected instanceof BacklogCategory) {
                            boolean PoExists = false;
                            for (Person person : Global.currentWorkspace.getPeople()) {
                                if (person.getSkills().containsAll(Role.getRoleFromType(Role.RoleType.PRODUCT_OWNER)
                                        .getRequiredSkills())) {

                                    PoExists = true;
                                }
                            }
                            setContextMenu(new CategoryTreeContextMenu(PoExists));
                        }
                        if (selected instanceof SprintCategory) {
                            boolean releasesExists = (!((Project) Global.selectedTreeItem.getParent().getValue())
                                    .getReleases().isEmpty());

                            setContextMenu(new CategoryTreeContextMenu(releasesExists));
                        }
                    }
                    else if (selected instanceof Category) {
                        selected.switchToCategoryScene();
                        setContextMenu(new CategoryTreeContextMenu());
                        if (selected instanceof RolesCategory) {
                            setContextMenu(new CategoryTreeContextMenu(false));
                        }
                    }
                    else {
                        // Assumed workspace item
                        selected.switchToInfoScene();
                        setContextMenu(new ElementTreeContextMenu());
                    }
                });
    }


    /**
     * Removes all listener from a root.
     *
     * @param root The root.
     */
    private void clear(TreeItem<T> root) {
        if (root != null) {
            for (TreeItem<T> treeItem : root.getChildren()) {
                removeRecursively(treeItem);
            }

            removeRecursively(root);
            root.getChildren().clear();
        }
    }


    /**
     * Updates the items
     */
    private void updateItems() {
        if (getItems() != null) {
            for (T value : getItems()) {
                getRoot().getChildren().add(addRecursively(value));
            }

            ListChangeListener<T> rootListener = getListChangeListener(getRoot().getChildren());
            WeakListChangeListener<T> weakListChangeListener =
                    new WeakListChangeListener<T>(rootListener);
            hardReferences.put(getRoot(), rootListener);
            weakListeners.put(getRoot(), weakListChangeListener);
            getItems().addListener(weakListChangeListener);
        }
    }


    /**
     * Gets a {@link javafx.collections.ListChangeListener} for a {@link TreeItem}. It listens to
     * changes on the underlying list and updates the UI accordingly.
     *
     * @param treeItemChildren The associated tree item's children list.
     * @return The listener.
     */
    private ListChangeListener<T> getListChangeListener(
            final ObservableList<TreeItem<T>> treeItemChildren) {
        return change -> {
            while (change.next()) {
                if (change.wasUpdated()) {
                    // http://javafx-jira.kenai.com/browse/RT-23434
                    continue;
                }
                if (change.wasRemoved()) {
                    for (int i = change.getRemovedSize() - 1; i >= 0; i--) {
                        removeRecursively(treeItemChildren.remove(change.getFrom() + i));
                    }
                }
                // If items have been added
                if (change.wasAdded()) {
                    // Get the new items
                    for (int i = change.getFrom(); i < change.getTo(); i++) {
                        treeItemChildren.add(i, addRecursively(change.getList().get(i)));
                    }
                }
                // If the list was sorted.
                if (change.wasPermutated()) {
                    App.mainPane.refreshTree();
                    /*// Store the new order.
                    Map<Integer, TreeItem<T>> tempMap = new HashMap<Integer, TreeItem<T>>();

                    for (int i = change.getTo() - 1; i >= change.getFrom(); i--)
                    {
                        int a = change.getPermutation(i);
                        tempMap.put(a, treeItemChildren.remove(i));
                    }

                    getSelectionModel().clearSelection();

                    // Add the items in the new order.
                    for (int i = change.getFrom(); i < change.getTo(); i++)
                    {
                        treeItemChildren.add(tempMap.remove(i));
                    }*/
                }
            }
        };
    }


    /**
     * Removes the listener recursively.
     *
     * @param item The tree item.
     * @return the item removed
     */
    private TreeItem<T> removeRecursively(TreeItem<T> item) {
        if (item.getValue() != null && item.getValue().getChildren() != null) {

            if (weakListeners.containsKey(item)) {
                item.getValue().getChildren().removeListener(weakListeners.remove(item));
                hardReferences.remove(item);
            }
            for (TreeItem<T> treeItem : item.getChildren()) {
                removeRecursively(treeItem);
            }
        }
        return item;
    }


    /**
     * Adds the children to the tree recursively.
     *
     * @param value The initial value.
     * @return The tree item.
     */
    private TreeItem<T> addRecursively(T value) {
        TreeItem<T> treeItem = new TreeItem<T>();
        treeItem.setValue(value);
        treeItem.setExpanded(true);

        if (value != null && value.getChildren() != null) {
            ListChangeListener<T> listChangeListener =
                    getListChangeListener(treeItem.getChildren());
            WeakListChangeListener<T> weakListener =
                    new WeakListChangeListener<T>(listChangeListener);
            value.getChildren().addListener(weakListener);

            hardReferences.put(treeItem, listChangeListener);
            weakListeners.put(treeItem, weakListener);
            for (T child : value.getChildren()) {
                treeItem.getChildren().add(addRecursively(child));
            }
        }
        return treeItem;
    }


    /**
     * Gets the observable list of items
     *
     * @return The observable list of items
     */
    public ObservableList<? extends T> getItems() {
        return items.get();
    }


    /**
     * Sets items for the tree.
     *
     * @param items The list.
     */
    public void setItems(ObservableList<? extends T> items) {
        this.items.set(items);
    }


    /**
     * Scans the entire tree from the root and selects the item if it is found.
     * @param item The (SaharaItem) item to select
     */
    public void selectItem(T item) {
        selectItem(item, this.getRoot());
    }

    /**
     * Scans the entire tree from the root and selects the item if it is found.
     * @param item The (SaharaItem) item to select
     */
    public void selectItem(TreeItem<T> item) {
        if (item.getParent() == null) {
            selectItem(item.getValue(), null, this.getRoot());
        }
        else {
            selectItem(item.getValue(), item.getParent().getValue(), this.getRoot());
        }
    }


    /**
     * Scans the tree and compares the item to the root TreeItem, if they match, select the TreeItem
     * If not, recursively check the children of the TreeItem. If the item exists in the tree, it
     * will eventually be selected through the depth-first search.
     *
     * @param item The (SaharaItem) item to select
     * @param root The root node to start checking, usually this.getRoot()
     */
    public void selectItem(T item, TreeItem<T> root) {
        for (TreeItem<T> treeItem : root.getChildren()) {
            if (treeItem.getValue() == item) {
                getSelectionModel().select(treeItem);
            }
            else {
                selectItem(item, treeItem);
            }
        }
    }


    /**
     * Scans the tree and compares the item to the root TreeItem, if they match, select the TreeItem
     * If not, recursively check the children of the TreeItem. If the item exists in the tree, it
     * will eventually be selected through the depth-first search.
     *
     * @param item The tree item to select
     * @param root The root node to start checking, usually this.getRoot()
     */
    public void selectItem(T item, T parent, TreeItem<T> root) {
        if (item == root) {
            getSelectionModel().select(root);
        }
        for (TreeItem<T> treeItem : root.getChildren()) {
            if (treeItem.getParent() == null && parent == null) {
                getSelectionModel().select(treeItem);
            }
            else if (treeItem.getValue().equals(item) && treeItem.getParent() != null
                    && treeItem.getParent().getValue() != null && treeItem.getParent().getValue().equals(parent)) {
                getSelectionModel().select(treeItem);
            }
            else {
                selectItem(item, parent, treeItem);
            }
        }
    }


    /**
     * Returns a set of all treeItems inside of the tree.
     *
     * @return A set of all treeItems inside of the tree
     */
    public Set<TreeItem<T>> getTreeItems() {
        Set<TreeItem<T>> items = new HashSet<>();
        items.add(this.getRoot());
        items.addAll(getTreeItemChildren(this.getRoot()));

        return items;
    }

    /**
     * Returns a set of children of a treeItem that already exists within the tree.
     *
     * @param treeItem The tree item to explore the children of
     * @return The children tree items of the given tree item
     */
    private Set<TreeItem<T>> getTreeItemChildren(TreeItem<T> treeItem) {
        Set<TreeItem<T>> items = new HashSet<>();

        for (TreeItem<T> item : treeItem.getChildren()) {
            items.add(item);
            items.addAll(getTreeItemChildren(item));
        }

        return items;
    }

}