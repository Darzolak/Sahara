package seng302.group2.scenes.information.project.sprint;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import org.controlsfx.control.PopOver;
import seng302.group2.scenes.control.Tooltip;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.information.project.story.task.LoggingEffortPane;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.team.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The contents of the scrumboard task cell, also used on the sprint info task to display information of each stories'
 * tasks.
 * Created by jml168 on 27/08/15.
 */
public class ScrumBoardTaskCellNode extends HBox implements SearchableControl {

    private Task task = null;
    private Set<SearchableControl> searchControls = new HashSet<>();
    private SprintTaskStatusTab statusTab = null;

    /**
     * The constructor that creates a scrumboard task cell for the given task
     * @param task The task the cell should reflect the information of
     */
    public ScrumBoardTaskCellNode(Task task) {
        this.task = task;
        this.getChildren().clear();
        Platform.runLater(() -> {
                HBox content = construct();
                this.getChildren().add(content);
            });

    }

    /**
     * The constructor that creates a scrumboard task cell for the given task
     * @param task The task the cell should reflect the information of
     * @param statusTab The tab that applies to this cell node
     */
    public ScrumBoardTaskCellNode(Task task, SprintTaskStatusTab statusTab) {
        this.statusTab = statusTab;
        this.task = task;
        this.getChildren().clear();
        Platform.runLater(() -> {
                HBox content = construct();
                this.getChildren().add(content);
            });
    }


    /**
     * Construct the content of the task cell with information from the task.
     * @return A cellular representation of the task with a visual representation of the task's information
     */
    private HBox construct() {
        HBox content = new HBox();
        content.setPrefHeight(48);

        // The cell's coloured rectangle
        Rectangle rect = new Rectangle(5, 48);
        rect.setFill(Color.web(task.getState().getColourString())); //(Color.web(item.getColour()));


        // The text content of the cell
        VBox textContent = new VBox();
        textContent.setPadding(new Insets(2, 2, 2, 6));
        textContent.setAlignment(Pos.CENTER_LEFT);

        SearchableText titleLabel = new SearchableText(task.getShortName(), searchControls);
        titleLabel.injectStyle("-fx-font-weight: bold");

        SearchableText descLabel = new SearchableText("(No Description)", searchControls);
        descLabel.injectStyle("-fx-font-size: 85%");
        if (!task.getDescription().isEmpty()) {
            descLabel.setText(task.getDescription());
        }
        textContent.getChildren().addAll(titleLabel, descLabel);


        // The cell's 'iconic' information
        VBox rightContent = new VBox(1);
        rightContent.setPrefHeight(48);

        rightContent.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(rightContent, Priority.ALWAYS);

        // Create the right sides content's information nodes
        Node remainingTime = createRemainingTime();
        Node assigneeNode = createAssigneeNode();
        Node impedimentsNode = createImpedimentsNode();
        rightContent.getChildren().addAll(remainingTime, assigneeNode, impedimentsNode);

        // Bring cell parts together
        content.getChildren().addAll(rect, textContent, rightContent);

        HBox.setHgrow(content, Priority.ALWAYS);

        return content;
    }


    public Node createImpedimentsNode() {
        // Impediments icon
        ImageView warningImage;
        if (Task.getImpedingStates().contains(task.getState()) || !task.getImpediments().isEmpty()) {
            warningImage = new ImageView("icons/dialog-cancel.png");
            if (task.getState() == Task.TASKSTATE.BLOCKED) {
                if (!task.getImpediments().isEmpty()) {
                    Tooltip.create("This task is currently blocked, with the following impediments:\n"
                            + task.getImpediments(), warningImage, 50);
                }
                else {
                    Tooltip.create("This task is currently blocked", warningImage, 50);
                }
            }
            else if (task.getState() == Task.TASKSTATE.DEFERRED) {
                if (!task.getImpediments().isEmpty()) {
                    //System.out.println(task.getImpediments());
                    Tooltip.create("This task has been deferred, and has the following impediments:\n"
                            + task.getImpediments(), warningImage, 50);
                }
                else {
                    Tooltip.create("This task has been deferred", warningImage, 50);
                }
            }
            else {
                Tooltip.create("This task has the following impediments:\n" + task.getImpediments(),
                        warningImage, 50);
            }
        }
        else {
            warningImage = new ImageView("icons/dialog-cancel-empty.png");
            Tooltip.create("This task has no impediments or blockages", warningImage, 50);
        }

        warningImage.setOnMouseEntered(me -> {
                this.getScene().setCursor(Cursor.HAND); //Change cursor to hand
            });
        warningImage.setOnMouseExited(me -> {
                this.getScene().setCursor(Cursor.DEFAULT); //Change cursor to hand
            });


        PopOver impedimentPopOver = new PopOver();
        impedimentPopOver.setDetachedTitle(task.getShortName() + "'s Impediments");
        SortedSet<Task.TASKSTATE> availableStatuses = new TreeSet<>();
        try {
            for (Task.TASKSTATE state : Task.getImpedingStates()) {
                availableStatuses.add(state);
            }
        }
        catch (NullPointerException ex) {
        }

        // Leave the combobox without a type so we can add a blank '(none)' without having to create a new task state
        ComboBox impedimentCombo = new ComboBox<>();
        impedimentCombo.getItems().add("(none)");
        impedimentCombo.getItems().addAll(availableStatuses);
        // Make default selection
        if (impedimentCombo.getItems().contains(task.getState())) {
            impedimentCombo.getSelectionModel().select(task.getState());
        }
        else {
            impedimentCombo.getSelectionModel().select("(none)");
        }
        warningImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (impedimentPopOver.isShowing()) {
                    impedimentPopOver.hide();
                }
                else {
                    impedimentPopOver.show(warningImage);
                }
                event.consume();
            });

        VBox impedimentsVBox = new VBox(4);
        SearchableText impedimentsLabel = new SearchableText("Impediments: ");
        TextArea impedimentsTextArea = new TextArea(task.getImpediments());
        impedimentsTextArea.setPrefSize(240, 80);
        impedimentsVBox.getChildren().addAll(impedimentsLabel, impedimentsTextArea);

        Button impedimentSaveButton = new Button("Save Impediments");
        impedimentSaveButton.setAlignment(Pos.CENTER_RIGHT);
        impedimentSaveButton.setOnAction(event -> {
                Task.TASKSTATE selectedState = null;
                Object selectedObject = impedimentCombo.getSelectionModel().getSelectedItem();
                if (selectedObject == null || !selectedObject.toString().equals("(none)")) {
                    selectedState = (Task.TASKSTATE) selectedObject;
                }
                task.editImpedimentState(selectedState, impedimentsTextArea.getText());
                impedimentPopOver.hide();

                this.getChildren().clear();
                this.getChildren().add(construct());
                if (statusTab != null) {
                    Platform.runLater(statusTab::createVisualisation);
                }
            });

        SearchableText statusLabel = new SearchableText("Status: ");
        statusLabel.setTextAlignment(TextAlignment.LEFT);
        HBox impedimentComboLabel = new HBox(8);
        impedimentComboLabel.setAlignment(Pos.CENTER_RIGHT);
        impedimentComboLabel.getChildren().addAll(
                statusLabel,
                impedimentCombo
        );


        VBox impedimentChangeNode = new VBox(8);
        impedimentChangeNode.setAlignment(Pos.CENTER_RIGHT);
        impedimentChangeNode.setPadding(new Insets(8,8,8,8));
        impedimentChangeNode.getChildren().addAll(
                impedimentComboLabel,
                impedimentsVBox,
                impedimentSaveButton
        );
        impedimentPopOver.setContentNode(impedimentChangeNode);

        return warningImage;
    }


    private Node createAssigneeNode() {
        // Assignee icon
        ImageView assigneeImage;
        if (task.getAssignee() != null) {
            assigneeImage = new ImageView("icons/person.png");
            Platform.runLater(() -> {
                    Tooltip.create(task.getAssignee().getFullName(), assigneeImage, 50);
                });
        }
        else {
            assigneeImage = new ImageView("icons/person_empty.png");
            Platform.runLater(() -> {
                    Tooltip.create("This task is unassigned", assigneeImage, 50);
                });
        }

        assigneeImage.setOnMouseEntered(me -> {
                this.getScene().setCursor(Cursor.HAND); //Change cursor to hand
            });
        assigneeImage.setOnMouseExited(me -> {
                this.getScene().setCursor(Cursor.DEFAULT); //Change cursor to hand
            });

        PopOver assignPopOver = new PopOver();
        assignPopOver.setDetachedTitle(task.getShortName() + " Assignment");
        SortedSet<Person> availableAssignees = new TreeSet<>();
        try {
            for (Team team : task.getStory().getProject().getCurrentTeams()) {
                availableAssignees.addAll(team.getPeople());
            }
        }
        catch (NullPointerException ex) {
        }
        Person nonePerson = new Person();
        nonePerson.setShortName("(none)");
        availableAssignees.add(nonePerson);

        ComboBox<Person> assigneeCombo = new ComboBox<>();
        assigneeCombo.getItems().addAll(availableAssignees);
        assigneeCombo.getSelectionModel().select(task.getAssignee());
        assigneeImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (assignPopOver.isShowing()) {
                    assignPopOver.hide();
                }
                else {
                    assignPopOver.show(assigneeImage);
                }
                event.consume();
            });

        Button assigneeSaveButton = new Button("Save Assignee");
        assigneeSaveButton.setAlignment(Pos.CENTER_RIGHT);
        assigneeSaveButton.setOnAction(event -> {
                Person selectedPerson = assigneeCombo.getSelectionModel().getSelectedItem();
                if (selectedPerson.equals(nonePerson)) {
                    selectedPerson = null;
                }
                task.editAssignee(selectedPerson);
                assignPopOver.hide();

                this.getChildren().clear();
                this.getChildren().add(construct());
                if (statusTab != null) {
                    Platform.runLater(statusTab::createVisualisation);
                }
            });

        SearchableText assigneeLabel = new SearchableText("Assignee: ");
        assigneeLabel.setTextAlignment(TextAlignment.LEFT);
        HBox assigneeComboLabel = new HBox(8);
        assigneeComboLabel.setAlignment(Pos.CENTER_RIGHT);
        assigneeComboLabel.getChildren().addAll(
                assigneeLabel,
                assigneeCombo
        );
        VBox assigneeChangeNode = new VBox(8);
        assigneeChangeNode.setAlignment(Pos.CENTER_RIGHT);
        assigneeChangeNode.setPadding(new Insets(8,8,8,8));
        assigneeChangeNode.getChildren().addAll(
                assigneeComboLabel,
                assigneeSaveButton
        );
        assignPopOver.setContentNode(assigneeChangeNode);

        return assigneeImage;
    }

    private Node createRemainingTime() {
        SearchableText remainingTime = new SearchableText(task.getEffortLeftString(), searchControls);
        Tooltip.create("Spent Effort: " + task.getEffortSpentString() + "\n"
                + "Remaining Effort: " + task.getEffortLeftString(), remainingTime, 50);
        remainingTime.injectStyle("-fx-font-size: 85%;");
        remainingTime.setTextAlignment(TextAlignment.RIGHT);

        // Remaining time PopOver
        PopOver loggingEffortPopOver = new PopOver();
        LoggingEffortPane loggingPane = new LoggingEffortPane(task, loggingEffortPopOver);
        loggingEffortPopOver.setContentNode(loggingPane);
        loggingEffortPopOver.setDetachedTitle(task.getShortName());

        remainingTime.setOnMouseEntered(me -> {
                this.getScene().setCursor(Cursor.HAND); //Change cursor to hand
            });
        remainingTime.setOnMouseExited(me -> {
                this.getScene().setCursor(Cursor.DEFAULT); //Change cursor to hand
            });

        remainingTime.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (loggingEffortPopOver.isShowing()) {
                    loggingEffortPopOver.hide();
                }
                else {
                    loggingEffortPopOver.show(remainingTime);
                }
                event.consume();
            });

        return remainingTime;
    }


    /**
     * Queries the cell for the given query string and returns whether or not the cell contains a match for the query
     * @param query the string to be queried
     * @return Whether or not the cell contains a match for the query
     */
    @Override
    public boolean query(String query) {
        boolean result = false;
        for (SearchableControl control : searchControls) {
            if (control.query(query)) {
                result = true;
            }
        }
        return result;
    }

    //TODO Advanced search query for this class.
    @Override
    public int advancedQuery(String query, SearchType searchType) {
        return 0;
    }
}
