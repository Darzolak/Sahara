package seng302.group2.scenes.information.project.story.task;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Task;

/**
 * A Table cell which displays a person icon and the assignee's name. Combo box is displayed on edit.
 * Created by swi67 on 4/09/15.
 */
public class AssigneeTableCell extends TableCell<Task, String> {

    private Node assigneeHBox;
    private Story story;
    private ComboBox<Person> comboBox;
    private ObservableList<Person> items;
    private HBox cell = new HBox();

    /**
     * Constructor
     * @param story The currently selected story
     * @param itemList A list of the available assignees for selection in the editing assignee combo
     */
    public AssigneeTableCell(Story story, ObservableList<Person> itemList) {
        this.story = story;
        this.items = itemList;
    }

    /**
     * Updates the item
     *
     * @param item  the item to update to
     * @param empty if the cell is empty
     */
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        }
        else {
            cell.getChildren().clear();
            if (isEditing()) {
                if (comboBox != null) {
                    comboBox.setValue(getTask().getAssignee());
                }
                cell.getChildren().add(comboBox);
            }
            else {
                if (getTask() != null) {
                    this.assigneeHBox = createAssigneeNode(getTask(), this);
                }
                else if (getTableView().getSelectionModel().getSelectedItem() != null) {
                    this.assigneeHBox =
                            createAssigneeNode(getTableView().getSelectionModel().getSelectedItem(), this);

                }
                cell.getChildren().add(assigneeHBox);
            }
            setGraphic(cell);
        }
    }

    /**
     * Gets the task of the current cell by checking the short name against each of the tasks in the story
     * @return The task whose name matches the cell item
     */
    public Task getTask() {
        Task result = null;
        for (Task task : this.story.getTasks()) {
            if (task.getShortName().equals(getItem())) {
                result = task;
            }
        }
        return result;
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            createCombo();

            if (getTask().getAssignee() != null) {
                comboBox.setValue(getTask().getAssignee());
            }
            cell.getChildren().clear();
            cell.getChildren().add(comboBox);
            setGraphic(cell);
            Platform.runLater(comboBox::requestFocus);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        cell.getChildren().clear();
        cell.getChildren().add(assigneeHBox);
        setGraphic(cell);
    }

    private void createCombo() {
        comboBox = new ComboBox<>(this.items);
        comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        comboBox.focusedProperty().addListener((arg0, arg1, arg2) -> {
                if (!arg2) {
                    if (comboBox.getValue() != null) {
                        commitEdit(comboBox.getValue().toString());
                    }
                    else {
                        commitEdit("");
                    }
                }
                else {
                    updateItem(getItem(), false);
                }
            });
    }


    /**
     * Creates a
     * @param task currently selected task
     * @param tableCell the cell the assignee node is set on
     * @return A node containing the UI for displaying and setting the assignee of a task
     */
    private Node createAssigneeNode(Task task, TableCell tableCell) {
        HBox assigneeHBox = new HBox(10);
        ImageView assigneeImage;
        if (task.getAssignee() != null) {
            assigneeImage = new ImageView("icons/person.png");
            seng302.group2.scenes.control.Tooltip.create(task.getAssignee().getFullName(), assigneeImage, 50);
        }
        else {
            assigneeImage = new ImageView("icons/person_empty.png");
            seng302.group2.scenes.control.Tooltip.create("This task is unassigned", assigneeImage, 50);
        }

        tableCell.setOnMouseEntered(me -> {
                tableCell.setCursor(Cursor.HAND); //Change cursor to hand
            });
        tableCell.setOnMouseExited(me -> {
                tableCell.setCursor(Cursor.DEFAULT); //Change cursor to hand
            });

        assigneeHBox.getChildren().addAll(assigneeImage);
        if (task.getAssignee() != null) {
            assigneeHBox.getChildren().add(new Label(task.getAssignee().toString()));
        }
        return assigneeHBox;
    }
}
