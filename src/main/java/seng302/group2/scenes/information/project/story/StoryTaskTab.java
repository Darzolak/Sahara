package seng302.group2.scenes.information.project.story;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import seng302.group2.App;
import seng302.group2.scenes.control.*;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.information.project.story.task.AssigneeTableCell;
import seng302.group2.scenes.information.project.story.task.ImpedimentsTableCell;
import seng302.group2.scenes.information.project.story.task.LoggingEffortPane;
import seng302.group2.scenes.information.tag.TaggingPane;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.util.conversion.DurationConverter;
import seng302.group2.util.validation.DateValidator;
import seng302.group2.util.validation.ShortNameValidator;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.acceptanceCriteria.AcceptanceCriteria;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A tab on the story scene which shows the all the stories tasks and their status.
 * Created by cvs20 on 5/08/15.
 */
public class StoryTaskTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    static Boolean correctShortName = Boolean.FALSE;
    static Boolean correctEffortLeft = Boolean.FALSE;
    Map<Task, PopOver> tagPopOvers = new HashMap<>();
    Map<Task, PopOver> taskPopOvers = new HashMap<>();


    Story currentStory;

    /**
     * Constructor for the Story Task Tab
     *
     * @param currentStory The currently selected Story
     */
    public StoryTaskTab(Story currentStory) {
        this.currentStory = currentStory;
        construct();
    }


    /**
     * Gets all the searchable controls on this tab.
     * @return a collection of all the searchable controls on this tab.
     */
    @Override
    public Collection<SearchableControl> getSearchableControls() {
        return searchControls;
    }

    @Override
    public void construct() {
        this.setText("Tasks");
        correctShortName = false;
        correctEffortLeft = false;

        Pane basicInfoPane = new VBox(10);

        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

        SearchableTable<Task> taskTable = new SearchableTable<>(currentStory.getTasks());
        taskTable.setEditable(true);
        taskTable.setPrefWidth(800);
        taskTable.setPrefHeight(200);
        taskTable.setPlaceholder(new SearchableText("There are currently no tasks in this story.", searchControls));
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        PopOver acPopover = new PopOver();
        acPopover.setDetachedTitle(currentStory.getShortName() + " - Acceptance Criteria");
        VBox acContent = new VBox();
        acContent.setAlignment(Pos.CENTER);
        acContent.setPadding(new Insets(8, 8, 8, 8));

        FilteredListView<AcceptanceCriteria> acceptanceCriteriaFilteredListView = new
                FilteredListView<>(currentStory.getAcceptanceCriteria(),
                "acceptance criteria");
        SearchableListView<AcceptanceCriteria> acListView = acceptanceCriteriaFilteredListView.getListView();
        ScrollPane acWrapper = new ScrollPane();
        acListView.setPrefSize(640, 250);
        acListView.setPlaceholder(new SearchableText("No Acceptance Criteria", searchControls));
        acWrapper.setContent(acceptanceCriteriaFilteredListView);
        acContent.getChildren().add(acWrapper);

        acPopover.setContentNode(acContent);


        Button acButton = new Button("View Acceptance Criteria");

        acButton.setOnAction((event) -> acPopover.show(acButton));


        SearchableTitle tasksTitle = new SearchableTitle("Tasks Table");

        TableColumn nameCol = new TableColumn("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<Task, String>("shortName"));
        nameCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Task, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Task, String> event) {
                        Task selectedTask = event.getTableView().getItems().get(
                                event.getTablePosition().getRow());
                        if (!event.getNewValue().isEmpty() && event.getNewValue() != null) {
                            ArrayList<Tag> tags = new ArrayList<>();
                            selectedTask.edit(event.getNewValue(), selectedTask.getDescription(),
                                    selectedTask.getImpediments(), selectedTask.getState(), selectedTask.getAssignee(),
                                    selectedTask.getLogs(),
                                    selectedTask.getEffortLeft(),
                                    selectedTask.getEffortSpent(),
                                    tags);
                        }
                    }
                }
        );

        TableColumn stateCol = new TableColumn("State");
        stateCol.setCellValueFactory(new PropertyValueFactory<Task, String>("state"));
        stateCol.setEditable(true);
        stateCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        ObservableList<Task.TASKSTATE> states = observableArrayList();
        states.addAll(Task.TASKSTATE.values());
        stateCol.setCellFactory(ComboBoxTableCell.forTableColumn(
                states
        ));

        stateCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Task, Task.TASKSTATE>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Task,
                            Task.TASKSTATE> event) {
                        Task currentTask = event.getTableView().getItems().get(
                                event.getTablePosition().getRow());
                        currentTask.editState(event.getNewValue());

                        SearchableTable.refresh(taskTable, taskTable.getItems());

                        /*event.getTablePosition().getTableColumn().setVisible(false);
                        event.getTablePosition().getTableColumn().setVisible(true);*/
                    }
                }
        );




        TableColumn assigneesCol = new TableColumn("Assignee");
        assigneesCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        ObservableList<Person> availablePeople = FXCollections.observableArrayList();
        Set<Team> availableTeam = currentStory.getProject().getCurrentTeams();
        for (Team team : availableTeam) {
            availablePeople.addAll(team.getPeople());
        }

        assigneesCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Task, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Task, String> task) {
                SimpleStringProperty prop = new SimpleStringProperty();
                prop.set(task.getValue().getShortName());
                return prop;
            }
        });

        assigneesCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Task, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Task, String> event) {
                        Task selectedTask = event.getTableView().getItems().get(
                                event.getTablePosition().getRow());
                        Person selectedPerson = null;

                        for (Person person : availablePeople) {
                            if (event.getNewValue().equals(person.getShortName())) {
                                selectedPerson = person;
                            }
                        }
                        if (event.getNewValue() != null && event.getNewValue() != null) {
                            selectedTask.editAssignee(selectedPerson);
                            taskTable.refresh(taskTable, taskTable.getItems());
                        }
                    }
                });

        Separator seperator = new Separator();

        Callback<TableColumn, TableCell> assigneeCellFactory = col -> new AssigneeTableCell(currentStory,
                availablePeople);
        assigneesCol.setCellFactory(assigneeCellFactory);

        TableColumn leftCol = new TableColumn("Effort Left");
        leftCol.setCellValueFactory(new PropertyValueFactory<Task, String>("effortLeftString"));
        leftCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));
        leftCol.setCellFactory(TextFieldTableCell.forTableColumn());
        leftCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Task, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Task, String> event) {
                        Task selectedTask = event.getTableView().getItems().get(
                                event.getTablePosition().getRow());
                        if (!event.getNewValue().isEmpty() && event.getNewValue() != null) {
                            selectedTask.editEffortLeft(DurationConverter.readDurationToMinutes(event.getNewValue()));
                            taskTable.refresh(taskTable, taskTable.getItems());
                        }
                    }
                }
        );

        TableColumn spentCol = new TableColumn("Effort Spent");
        spentCol.setCellValueFactory(new PropertyValueFactory<Task, String>("effortSpentString"));
        spentCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        spentCol.setCellFactory(
                column -> {
                    return new TableCell<Task, String>()
                    {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            setText(item);
                            setTooltip(new Tooltip("Effort spent is not editable"));
                        }
                    };
                });

        TableColumn impedimentsCol = new TableColumn("Impediments");
        impedimentsCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        impedimentsCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Task, String>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Task, String> task) {
                SimpleStringProperty prop = new SimpleStringProperty();
                prop.set(task.getValue().getShortName());
                return prop;
            }
        });
        Callback<TableColumn, TableCell> impedimentsCellFactory = col -> new ImpedimentsTableCell(currentStory);
        impedimentsCol.setCellFactory(impedimentsCellFactory);

        TableColumn descriptionCol = new TableColumn("Description");
        descriptionCol.setEditable(true);
        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Task, String>("description"));
        descriptionCol.setPrefWidth(250);

        descriptionCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Task, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Task, String> event) {
                        (event.getTableView().getItems().get(
                                event.getTablePosition().getRow())
                        ).editDescription(event.getNewValue());
                        taskTable.refresh(taskTable, taskTable.getItems());
                    }
                }
        );


        TableColumn[] columns = {nameCol, stateCol, assigneesCol, leftCol, spentCol, impedimentsCol, descriptionCol};
        taskTable.getColumns().setAll(columns);


        // Listener to disable columns being movable
        taskTable.getColumns().addListener(new ListChangeListener() {
            public boolean suspended;

            @Override
            public void onChanged(ListChangeListener.Change change) {
                change.next();
                if (change.wasReplaced() && !suspended) {
                    this.suspended = true;
                    taskTable.getColumns().setAll(columns);
                    this.suspended = false;
                }
            }
        });

        Button btnView = new Button("View");
        Button btnDelete = new Button("Delete Task");
        Button tagButton = new Button("View Tags");

        HBox buttons = new HBox();
        buttons.spacingProperty().setValue(10);
        buttons.getChildren().addAll(btnView, btnDelete, tagButton);
        buttons.setAlignment(Pos.TOP_LEFT);

        btnView.setOnAction((event) -> {
            if (taskTable.getSelectionModel().getSelectedItem() == null) {
                PopOver taskPopover = new PopOver();
                VBox content = new VBox();
                content.setMinWidth(200);
                content.setPadding(new Insets(8, 8, 8, 8));
                SearchableText noTaskLabel = new SearchableText("No tasks selected.", searchControls);
                content.getChildren().add(noTaskLabel);
                taskPopover.setContentNode(content);
                taskPopover.show(btnView);
            }
            else {
                PopOver taskPopover = taskPopOvers.get(taskTable.getSelectionModel().getSelectedItem());
                taskPopover.show(btnView);
            }
        });

        tagButton.setOnAction((event) -> {
            if (taskTable.getSelectionModel().getSelectedItem() == null) {
                PopOver tagPopover = new PopOver();
                VBox content = new VBox();
                content.setMinWidth(200);
                content.setPadding(new Insets(8, 8, 8, 8));
                SearchableText noTaskLabel = new SearchableText("No tasks selected.", searchControls);
                content.getChildren().add(noTaskLabel);
                tagPopover.setContentNode(content);
                tagPopover.show(tagButton);
            }
            else {
                PopOver tagPopover = tagPopOvers.get(taskTable.getSelectionModel().getSelectedItem());
                tagPopover.show(tagButton);
            }
        });


        for (Task task : currentStory.getTasks()) {
            PopOver tagPopover = new PopOver();
            VBox content = new VBox();
            content.setMinWidth(600);
            content.setPadding(new Insets(8, 8, 8, 8));
            tagPopover.setDetachedTitle(task.toString());

            TaggingPane taggingPane = new TaggingPane(task);
            taggingPane.setStyle(null);
            searchControls.add(taggingPane);

            content.getChildren().add(taggingPane);
            tagPopover.setContentNode(content);
            tagPopOvers.put(task, tagPopover);



            PopOver taskPopover = new PopOver();
            VBox taskContent = new VBox();
            taskContent.setPadding(new Insets(8, 8, 8, 8));

            taskPopover.setDetachedTitle(task.toString());

            ScrollPane taskWrapper = new ScrollPane();
            LoggingEffortPane loggingPane = new LoggingEffortPane(task,
                    taskPopover, taskTable);
            loggingPane.setStyle(null);
            searchControls.add(loggingPane);

            taskWrapper.setContent(loggingPane);

            TitledPane collapsableLoggingPane = new TitledPane("Task Logging", taskWrapper);
            collapsableLoggingPane.setExpanded(true);
            collapsableLoggingPane.setAnimated(true);

            taskContent.getChildren().addAll(collapsableLoggingPane);

            taskPopover.setContentNode(taskContent);
            taskPopOvers.put(task, taskPopover);
        }

        btnDelete.setOnAction((event) -> {
            if (taskTable.getSelectionModel().getSelectedItem() != null) {
                Task currentTask = taskTable.getSelectionModel().getSelectedItem();
                if (currentTask != null) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete");
                    alert.setHeaderText("Delete Task?");
                    alert.setContentText("Do you really want to delete this Task?");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.get() == buttonTypeYes) {
                        currentTask.deleteTask();
                    }
                    else if (result.get() == buttonTypeNo) {
                        event.consume();
                    }
                }
            }

        });

        VBox addTaskBox = new VBox(10);
        SearchableText task = new SearchableText("Add Quick Tasks:", "-fx-font-weight: bold;");

        RequiredField shortNameCustomField = new RequiredField("Task Name:");
        RequiredField effortLeftField = new RequiredField("Effort left:");
        CustomComboBox<Person> assigneeComboBox = new CustomComboBox<>("Assignee:", false);

        //assigneeComboBox.setDisable(false);

        if (currentStory.getProject().getCurrentTeams() != null
                && !currentStory.getProject().getCurrentTeams().isEmpty()) {

            if (availablePeople.size() != 0) {
                assigneeComboBox.getComboBox().getItems().addAll(availablePeople);
                assigneeComboBox.setDisable(false);
            }
            else {
                PopOverTip pot = new PopOverTip(assigneeComboBox,
                        "There are no team members in the currently allocated teams");
                assigneeComboBox.getComboBox().setDisable(true);
            }
        }
        else {
            PopOverTip pot = new PopOverTip(assigneeComboBox,
                    "There are currently no teams allocated on this project");
            assigneeComboBox.getComboBox().setDisable(true);
        }


        Button btnAdd = new Button("Add");
        btnAdd.setDisable(true);
        addTaskBox.getChildren().addAll(task, shortNameCustomField, effortLeftField, assigneeComboBox, btnAdd);


        shortNameCustomField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationStyle.borderGlowNone(shortNameCustomField.getTextField());
            correctShortName = !shortNameCustomField.getText().isEmpty();
            if (!correctShortName) {
                ValidationStyle.borderGlowRed(shortNameCustomField.getTextField());
                ValidationStyle.showMessage("Short Name required",
                        shortNameCustomField.getTextField());
            }
            btnAdd.setDisable(!(correctShortName && correctEffortLeft));
        });

        effortLeftField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            correctEffortLeft = DateValidator.validDuration(newValue) && !newValue.isEmpty();
            if (correctEffortLeft) {
                ValidationStyle.borderGlowNone(effortLeftField.getTextField());
            }
            else {
                if (newValue.isEmpty()) {
                    ValidationStyle.borderGlowRed(effortLeftField.getTextField());
                    ValidationStyle.showMessage("This field must be filled", effortLeftField.getTextField());
                }
                else {
                    ValidationStyle.borderGlowRed(effortLeftField.getTextField());
                    ValidationStyle.showMessage("Please input in valid format", effortLeftField.getTextField());
                }
            }
            btnAdd.setDisable(!(correctShortName && correctEffortLeft));
        });

        btnAdd.setOnAction((event) -> {
            //get user input
            String shortName = shortNameCustomField.getText();
            Task newTask;
            if (effortLeftField.getText().isEmpty()) {
                newTask = new Task(shortName, " ", currentStory, assigneeComboBox.getValue(), 0);
            }
            else {
                newTask = new Task(shortName, " ", currentStory, assigneeComboBox.getValue(),
                        DurationConverter.readDurationToMinutes(effortLeftField.getText()));
            }
            currentStory.add(newTask);
            App.refreshMainScene();
        });

        basicInfoPane.getChildren().addAll(
                tasksTitle,
                taskTable,
                buttons,
                acButton,
                seperator,
                addTaskBox
        );

        Collections.addAll(searchControls,
                task,
                tasksTitle,
                shortNameCustomField,
                effortLeftField,
                assigneeComboBox,
                taskTable
        );
    }

    private void taskInfoPane(Task currentTask, VBox taskContent) {
        if (!taskContent.getChildren().isEmpty()) {
            taskContent.getChildren().remove(0);
        }
        VBox taskInfo = new VBox();

        taskInfo.setBorder(null);
        taskInfo.setPadding(new Insets(25, 25, 25, 25));

        SearchableText title = new SearchableTitle(currentTask.getShortName());
        SearchableText description = new SearchableText("Task Description: "
                + currentTask.getDescription());
        SearchableText impediments = new SearchableText("Impediments: " + currentTask.getImpediments());
        SearchableText effortLeft = new SearchableText("Effort Left: " + currentTask.getEffortLeftString());
        SearchableText effortSpent = new SearchableText("Effort Spent: "
                + currentTask.getEffortSpentString());
        SearchableText taskState = new SearchableText("Task State: " + currentTask.getState());
        SearchableText assignedPerson;
        if (currentTask.getAssignee() == null) {
            assignedPerson = new SearchableText("Assigned Person: ");
        }
        else {
            assignedPerson = new SearchableText("Assigned Person: " + currentTask.getAssignee());
        }

        Button btnEdit = new Button("Edit");



        taskInfo.getChildren().addAll(
                title,
                description,
                impediments,
                effortLeft,
                effortSpent,
                taskState,
                assignedPerson,
                btnEdit
        );

        taskInfo.setStyle(" -fx-background: -fx-control-inner-background ;\n"
                + "  -fx-background-color: -fx-table-cell-border-color, -fx-background ;\n");



        TitledPane collapsableInfoPane = new TitledPane("Task Info", taskInfo);
        collapsableInfoPane.setPrefHeight(30);
        collapsableInfoPane.setExpanded(true);
        collapsableInfoPane.setAnimated(true);


        taskContent.getChildren().add(0, collapsableInfoPane);
        btnEdit.setOnAction((event) -> {
                taskEditPane(currentTask, taskContent);
            });
    }

    private void taskEditPane(Task currentTask, VBox taskContent) {
        taskContent.getChildren().remove(0);

        VBox taskInfo = new VBox();
        taskInfo.setBorder(null);
        taskInfo.setPadding(new Insets(25, 25, 25, 25));


        Button btnCancel = new Button("Cancel");
        Button btnDone = new Button("Done");

        HBox buttons = new HBox();
        buttons.spacingProperty().setValue(10);
        buttons.alignmentProperty().set(Pos.TOP_LEFT);
        buttons.getChildren().addAll(btnDone, btnCancel);

        RequiredField shortNameCustomField = new RequiredField("Task Name:");
        CustomTextArea descriptionTextArea = new CustomTextArea("Task Description:", 300);
        CustomTextArea impedimentsTextArea = new CustomTextArea("Task Impediments:", 300);
        CustomTextField effortLeftField = new CustomTextField("Effort Left:");

        effortLeftField.setPrefWidth(300);

        HBox taskHbox = new HBox();
        SearchableText taskStateText = new SearchableText("Task State: ");
        ObservableList<Task.TASKSTATE> taskstateObservableList = observableArrayList();
        taskstateObservableList.addAll(Task.TASKSTATE.values());


        ComboBox<Task.TASKSTATE> taskStateComboBox = new ComboBox<>(taskstateObservableList);
        taskStateComboBox.setValue(currentTask.getState());
        taskHbox.getChildren().addAll(taskStateText, taskStateComboBox);

        shortNameCustomField.setMaxWidth(275);
        descriptionTextArea.setMaxWidth(275);
        impedimentsTextArea.setMaxWidth(275);
        effortLeftField.setMaxWidth(275);

        shortNameCustomField.setText(currentTask.getShortName());
        descriptionTextArea.setText(currentTask.getDescription());
        impedimentsTextArea.setText(currentTask.getImpediments());
        effortLeftField.setText(Double.toString(currentTask.getEffortLeft()));


        CustomComboBox<Person> taskAssigneesList = new CustomComboBox<>("Assignee: ");
        taskAssigneesList.addToComboBox(null);
        if (currentTask.getStory() != null && currentTask.getStory().getSprint() != null) {
            taskAssigneesList.getComboBox().setItems(currentTask.getStory().getSprint().getTeam().getPeople());
        }
        taskAssigneesList.setValue(currentTask.getAssignee());

        //Adding to MainPane
        taskInfo.getChildren().addAll(shortNameCustomField,
                descriptionTextArea,
                impedimentsTextArea,
                effortLeftField,
                taskHbox,
                taskAssigneesList,
                buttons
        );



        btnDone.setOnAction((event) -> {
                boolean shortNameUnchanged = shortNameCustomField.getText().equals(
                        currentTask.getShortName());

                boolean descriptionUnchanged = descriptionTextArea.getText().equals(
                        currentTask.getDescription());

                boolean impedimentsUnchanged = impedimentsTextArea.getText().equals(
                        currentTask.getImpediments());

                boolean taskstateUnchanged = taskStateComboBox.getValue().equals(
                        currentTask.getState());

                boolean effortLeftUnchanged = effortLeftField.getText().equals(
                        Double.toString(currentTask.getEffortLeft()));
                //TODO deal with null assignment in the combobox
    //                boolean assigneesUnchanged = taskAssigneesList.getValue().equals((currentTask.getAssignee()));


                if (shortNameUnchanged && descriptionUnchanged
                        && impedimentsUnchanged && taskstateUnchanged && effortLeftUnchanged) {
                    // No changes
                    taskInfoPane(currentTask, taskContent);
                    return;
                }
                boolean correctShortName = ShortNameValidator.validateShortName(shortNameCustomField,
                        currentTask.getShortName());

                if (correctShortName) {
                    //                    Valid short name, make the edit
                    ArrayList<Tag> tags = new ArrayList<>();

                    currentTask.edit(shortNameCustomField.getText(),
                            descriptionTextArea.getText(),
                            impedimentsTextArea.getText(),
                            taskStateComboBox.getValue(),
                            taskAssigneesList.getValue(), currentTask.getLogs(),
                            Double.parseDouble(effortLeftField.getText()), currentTask.getEffortSpent(),
                            tags);


                }
                else {
                    event.consume();
                }

            });

        taskInfo.setStyle(" -fx-background: -fx-control-inner-background ;\n"
                + "  -fx-background-color: -fx-table-cell-border-color, -fx-background ;\n");

        btnCancel.setOnAction((event) -> {
                taskInfoPane(currentTask, taskContent);
            });

        TitledPane collapsableInfoPane = new TitledPane("Task Info", taskInfo);
        collapsableInfoPane.setPrefHeight(30);
        collapsableInfoPane.setExpanded(true);
        collapsableInfoPane.setAnimated(true);

        taskContent.getChildren().add(0, collapsableInfoPane);
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Task Tab";
    }
}
