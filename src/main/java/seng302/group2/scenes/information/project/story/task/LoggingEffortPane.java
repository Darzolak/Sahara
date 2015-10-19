package seng302.group2.scenes.information.project.story.task;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import seng302.group2.Global;
import seng302.group2.scenes.control.*;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableTable;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.information.tag.TaggingPane;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.util.conversion.DurationConverter;
import seng302.group2.util.validation.DateValidator;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;
import seng302.group2.workspace.tag.Tag;
import seng302.group2.workspace.team.Team;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Pane for logging on a given Task.
 * Created by jml168 on 25/08/15.
 */
public class LoggingEffortPane extends Pane implements SearchableControl {
    private List<SearchableControl> searchControls = new ArrayList<>();

    private PopOver popOver = null;
    private Boolean correctEffortLeft = Boolean.FALSE;
    private Boolean correctDuration = Boolean.FALSE;
    private Boolean loggerSelected = Boolean.FALSE;
    private TableView table = null;
    private Person nullPerson = new Person("", "", "", "", "", null);
    private ObservableList<Person> availablePeople = FXCollections.observableArrayList();
    private ObservableList<Person> availablePartners = observableArrayList(availablePeople);
    private ObservableList<Person> availableLoggers = observableArrayList(availablePeople);
    private  ObservableList<Log> data = FXCollections.observableArrayList();
    Map<Log, PopOver> tagPopOvers = new HashMap<>();

    /**
     * Constructor for the logging effort pane.
     * @param task The parent task
     */
    public LoggingEffortPane(Task task) {
        try {
            String css = this.getClass().getResource("/styles/tableHeader.css").toExternalForm();
            this.getStylesheets().add(css);
        }
        catch (Exception ex) {
            System.err.println("Cannot acquire stylesheet: " + ex.toString());
        }
        construct(task);
    }

    /**
     * Constructor for the logging effort pane
     * @param task The parent task
     * @param popOver The popover to place the pane into
     */
    public LoggingEffortPane(Task task, PopOver popOver) {
        this.popOver = popOver;
        try {
            String css = this.getClass().getResource("/styles/tableHeader.css").toExternalForm();
            this.getStylesheets().add(css);
        }
        catch (Exception ex) {
            System.err.println("Cannot acquire stylesheet: " + ex.toString());
        }
        construct(task);
    }


    /**
     * Constructor for the logging effort pane
     * @param task The parent task
     * @param popOver The popover to place the pane into
     * @param table The table the logs are held in
     */
    public LoggingEffortPane(Task task, PopOver popOver, TableView table) {
        this.popOver = popOver;
        this.table = table;
        try {
            String css = this.getClass().getResource("/styles/tableHeader.css").toExternalForm();
            this.getStylesheets().add(css);
        }
        catch (Exception ex) {
            System.err.println("Cannot acquire stylesheet: " + ex.toString());
        }
        construct(task);
    }


    private void construct(Task task) {
        VBox content = new VBox(8);
        content.setPadding(new Insets(8));

        SearchableTable<Log> logTable = new SearchableTable<>();
        searchControls.add(logTable);
        logTable.setEditable(true);
        logTable.setPrefWidth(400);
        logTable.setPrefHeight(200);
        logTable.setPlaceholder(new SearchableText("There are currently no logs in this task."));
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        data = task.getLogsWithoutGhostLogs();
        sortLogTableData();

        TableColumn loggerCol = new TableColumn("Logger");
        loggerCol.setCellValueFactory(new PropertyValueFactory<Log, Person>("logger"));
        loggerCol.setEditable(true);
        loggerCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        Set<Team> availableTeams = task.getStory().getProject().getCurrentTeams();
        for (Team team : availableTeams) {
            availablePeople.addAll(team.getPeople());
        }


        Callback<TableColumn, TableCell> loggerCellFactory = col -> new LogPersonEditTableCell(availableLoggers, this);
        loggerCol.setCellFactory(loggerCellFactory);

        loggerCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Log, String>,
                        ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Log,
                            String> log) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        property.setValue(log.getValue().getLogger().toString());
                        return property;
                    }
                });


        loggerCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Log, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Log,
                            String> event) {
                        Log currentLog = event.getTableView().getItems().get(
                                event.getTablePosition().getRow());
                        ArrayList<Tag> tags = new ArrayList<>();
                        Person newPerson = null;
                        for (Person person : availablePeople) {
                            if (person.getShortName() == event.getNewValue()) {
                                newPerson = person;
                            }
                        }
                        currentLog.editLogger(newPerson);
                    }
                }
        );


        Callback<TableColumn, TableCell> partnerCellFactory = col -> new LogPersonEditTableCell(availablePartners,
                this);

        TableColumn partnerCol = new TableColumn("Partner");
        partnerCol.setCellValueFactory(new PropertyValueFactory<Log, Person>("partner"));
        partnerCol.setEditable(true);

        partnerCol.setCellFactory(partnerCellFactory);


        partnerCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Log, String>,
                        ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Log,
                            String> log) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        if (log.getValue().getPartner() != null) {
                            property.setValue(log.getValue().getPartner().toString());
                        }
                        return property;
                    }
                });


        partnerCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Log, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Log,
                            String> event) {
                        Log currentLog = event.getTableView().getItems().get(
                                event.getTablePosition().getRow());

                        Person newPerson = null;
                        for (Person person : availablePeople) {
                            if (person.getShortName() == event.getNewValue()) {
                                newPerson = person;
                            }
                        }
                        currentLog.editPartner(newPerson);
                    }
                }
        );

        partnerCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        Callback<TableColumn, TableCell> cellFactory = col -> new DatePickerEditCell();
        Callback<TableColumn, TableCell> startTimeCellFactory = col -> new TimeTextFieldEditCell();

        TableColumn startDateTimeCol = new TableColumn("Start Time");
        TableColumn startDateCol = new TableColumn("Date");
        startDateCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Log, String>,
                        ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Log,
                            String> log) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        property.setValue(log.getValue().getStartDate().format(Global.dateFormatter));
                        return property;
                    }
                });

        startDateCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Log, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Log, String> event) {
                        if (!event.getNewValue().isEmpty()) {
                            Log currentLog = event.getTableView().getItems()
                                    .get(event.getTablePosition().getRow());

                            int hour = currentLog.getStartDate().getHour();
                            int min = currentLog.getStartDate().getMinute();

                            LocalDate startDate = LocalDate.parse(event.getNewValue(),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                            Month month = startDate.getMonth();
                            int day = startDate.getDayOfMonth();
                            int year = startDate.getYear();

                            LocalDateTime newStartDateTime = LocalDateTime.of(year, month, day, hour, min);
                            currentLog.editStartTime(newStartDateTime);
                            sortLogTableData();
                        }
                    }
                });
        startDateCol.setCellFactory(cellFactory);
        startDateCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));
        startDateCol.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn startTimeCol = new TableColumn("Time");
        startTimeCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Log, String>,
                        ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Log,
                            String> log) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        property.setValue(log.getValue().getStartDate().format(formatter));
                        return property;
                    }
                });

        startTimeCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Log, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Log, String> event) {
                        if (!event.getNewValue().isEmpty()) {
                            Log currentLog = event.getTableView().getItems()
                                    .get(event.getTablePosition().getRow());

                            int year = currentLog.getStartDate().getYear();
                            int month = currentLog.getStartDate().getMonthValue();
                            int day = currentLog.getStartDate().getDayOfMonth();
                            int hours = Integer.parseInt(event.getNewValue().substring(0, 2));
                            int minutes = Integer.parseInt(event.getNewValue().substring(3,5));

                            LocalDateTime newDateTime = LocalDateTime.of(year, month, day, hours, minutes);
                            currentLog.editStartTime(newDateTime);
                        }
                    }
                });

        startTimeCol.setCellFactory(startTimeCellFactory);

        startDateTimeCol.getColumns().addAll(startDateCol, startTimeCol);

        startTimeCol.setSortType(TableColumn.SortType.DESCENDING);
        startDateCol.setSortType(TableColumn.SortType.DESCENDING);



        TableColumn descriptionCol = new TableColumn("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Log, String>("description"));
        descriptionCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        descriptionCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Log, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Log, String> event) {
                        Log selectedLog = event.getTableView().getItems().get(
                                event.getTablePosition().getRow());
                        if (!event.getNewValue().isEmpty() && event.getNewValue() != null) {
                            ArrayList<Tag> tags = new ArrayList<>();
                        selectedLog.editDescription(event.getNewValue());
                        }
                    }
                }
        );


        TableColumn durationCol = new TableColumn("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<Log, String>("durationString"));
        durationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        durationCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Log, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Log, String> event) {
                        Log selectedLog = event.getTableView().getItems().get(
                                event.getTablePosition().getRow());
                        if (!event.getNewValue().isEmpty() && event.getNewValue() != null) {
                            ArrayList<Tag> tags = new ArrayList<>();
                        }
                        if (DateValidator.validDuration(event.getNewValue())
                                && !event.getNewValue().isEmpty()) {
                            double duration = DurationConverter.readDurationToMinutes(event.getNewValue());
                            selectedLog.editDuration(duration);
                            //TODO:Shinobu figure out how to display error
                        }
                        logTable.refresh(logTable, logTable.getItems());
                        if (table != null) {
                            SearchableTable.refresh(table, table.getItems());
                        }
                    }
                }
        );
        durationCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));


        logTable.setItems(data);
        TableColumn[] columns = {loggerCol, partnerCol, startDateTimeCol, durationCol, descriptionCol};
        logTable.getColumns().setAll(columns);
        logTable.getSortOrder().add(startDateCol);


        // Listener to disable columns being movable
        /*taskTable.getColumns().addListener(new ListChangeListener() {
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
        });*/

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        Button tagButton = new Button("View Tags");
        buttons.getChildren().addAll(addButton, deleteButton);

        HBox newLogFields = new HBox(10);
        VBox newLogFieldFirstRow = new VBox(10);
        final CustomComboBox<Person> personComboBox = new CustomComboBox<>("Logger:", true);

        CustomComboBox<Person> partnerComboBox = new CustomComboBox<>("Partner");
        partnerComboBox.getComboBox().getItems().add(nullPerson);

        addButton.setDisable(true);

        personComboBox.getComboBox().valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null) {
                    loggerSelected = false;
                    ValidationStyle.borderGlowRed(personComboBox.getComboBox());
                    ValidationStyle.showMessage("Logger required", personComboBox.getComboBox());
                }
                else {
                    if (newValue == partnerComboBox.getValue()) {
                        partnerComboBox.setValue(nullPerson);
                    }
                    partnerComboBox.getComboBox().getItems().remove(newValue);
                    if (oldValue != null) {
                        partnerComboBox.getComboBox().getItems().add(oldValue);
                    }
                    ValidationStyle.borderGlowNone(personComboBox.getComboBox());
                    loggerSelected = true;
                }
                if (partnerComboBox.getComboBox().getItems().size() == 0) {
                    partnerComboBox.getComboBox().setDisable(true);
                    ValidationStyle.showMessage("There is only one Person in the Team",
                            partnerComboBox.getComboBox());
                }
                else {
                    partnerComboBox.getComboBox().setDisable(false);
                }


                addButton.setDisable(!(loggerSelected && correctDuration && correctEffortLeft));
            });


        partnerComboBox.getComboBox().valueProperty().addListener((observable, oldValue, newValue) -> {
                addButton.setDisable(!(loggerSelected && correctDuration && correctEffortLeft));
            });

        CustomDatePicker startDatePicker = new CustomDatePicker("Date:", true);

//        final Callback<DatePicker, DateCell> endDateCellFactory =
//            new Callback<DatePicker, DateCell>() {
//                @Override
//                public DateCell call(final DatePicker datePicker) {
//                    return new DateCell() {
//                        @Override
//                        public void updateItem(LocalDate item, boolean empty) {
//                            super.updateItem(item, empty);
//                            if (item.isBefore(task.getStory().getSprint().getStartDate())) {
//                                setDisable(true);
//                                setStyle("-fx-background-color: #ffc0cb;");
//                            }
//                        }
//                    };
//                }
//            };
//        startDatePicker.getDatePicker().setDayCellFactory(endDateCellFactory);
        startDatePicker.getDatePicker().setValue(LocalDate.now());

        HBox startTimeHBox = new HBox(10);
        SearchableText startTimeLabel = new SearchableText("Start Time:");
        startTimeLabel.setStyle("-fx-font-weight: bold");

        startTimeLabel.setPrefWidth(150);
        TimeTextField timeTextField = new TimeTextField();
        HBox.setHgrow(timeTextField, Priority.ALWAYS);
        startTimeHBox.getChildren().addAll(startTimeLabel, timeTextField);

        RequiredField durationTextField = new RequiredField("Duration:");
        durationTextField.getTextField().setPromptText("eg. 1h 20min");
        durationTextField.getTextField().setPrefWidth(175);
        
        CustomTextField effortLeftField = new CustomTextField("Task Effort Left:");
        effortLeftField.getTextField().setText(task.getEffortLeftString());
        effortLeftField.getTextField().setPrefWidth(175);

        personComboBox.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(3).divide(100).multiply(30));
        startDatePicker.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(3).divide(100).multiply(30));
        newLogFieldFirstRow.getChildren().addAll(personComboBox, partnerComboBox,
                startDatePicker, startTimeHBox, durationTextField, effortLeftField);

        startDatePicker.getDatePicker().setMinWidth(175);
        timeTextField.setPrefWidth(175);

        VBox newLogFieldSecondRow = new VBox(10);
        SearchableText descriptionLabel = new SearchableText("Description");
        TextArea descriptionTextArea = new TextArea();
        descriptionTextArea.setPrefSize(300, 100);


        
        durationTextField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
                correctDuration = DateValidator.validDuration(newValue) && !newValue.isEmpty();
                correctEffortLeft = DateValidator.validDuration(effortLeftField.getText()) 
                        && !effortLeftField.getText().isEmpty();
                if (correctDuration) {
                    ValidationStyle.borderGlowNone(durationTextField.getTextField());
                    double effortLeft = task.getEffortLeft()
                            - DurationConverter.readDurationToMinutes(durationTextField.getText());
                    String effortLeftString = (int) Math.floor(effortLeft / 60) + "h " 
                            + (int) Math.floor(effortLeft % 60) + "min";
                    if (effortLeft <= 0) {
                        effortLeftString = "0h 00min";
                    }
                    
                    effortLeftField.setText(effortLeftString);
                }
                else {
                    effortLeftField.setText(task.getEffortLeftString());
                    if (newValue.isEmpty()) {
                        ValidationStyle.borderGlowRed(durationTextField.getTextField());
                        ValidationStyle.showMessage("This field must be filled", durationTextField.getTextField());
                    }
                    else {
                        ValidationStyle.borderGlowRed(durationTextField.getTextField());
                        ValidationStyle.showMessage("Please input in valid format", durationTextField.getTextField());
                    }
                }
                addButton.setDisable(!(loggerSelected && correctDuration && correctEffortLeft));
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
                //addButton.setDisable(!(loggerSelected && correctDuration && correctEffortLeft));
            });


        newLogFieldSecondRow.getChildren().addAll(descriptionLabel, descriptionTextArea);

        newLogFields.getChildren().addAll(newLogFieldFirstRow, newLogFieldSecondRow);

/*        durationTextField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (Log.readDurationToMinutes(newValue) == null) {
                durationTextField.getTextField().setTooltip(new Tooltip("Wrong format"));
            }
            else {
                durationTextField.getTextField().setTooltip(null);
            }
        });*/

        personComboBox.getComboBox().getItems().clear();
        try {
            Set<Team> teams = ((task.getStory().getBacklog() == null)
                    ? task.getStory().getProject().getCurrentTeams() :
                    task.getStory().getBacklog().getProject().getCurrentTeams());
            if (teams.isEmpty()) {
                personComboBox.setDisable(true);
            }
            else {
                boolean teamPopulated = false;
                for (Team team : teams) {
                    teamPopulated = teamPopulated || !team.getPeople().isEmpty();
                }
                if (teamPopulated) {
                    personComboBox.setDisable(false);
                    for (Team team : teams) {
                        personComboBox.getComboBox().getItems().addAll(team.getPeople());
                        partnerComboBox.getComboBox().getItems().addAll(team.getPeople());
                    }
                }
                else {
                    personComboBox.setDisable(true);
                }
            }
        }
        catch (NullPointerException ex) {
            // Somewhere getting to teams hit null
            personComboBox.setDisable(true);
        }

        tagButton.setOnAction((event) -> {
            if (logTable.getSelectionModel().getSelectedItem() == null) {
                PopOver tagPopover = new PopOver();
                VBox popoverContent = new VBox();
                popoverContent.setMinWidth(200);
                popoverContent.setPadding(new Insets(8, 8, 8, 8));
                SearchableText noTaskLabel = new SearchableText("No tasks selected.", searchControls);
                popoverContent.getChildren().add(noTaskLabel);
                tagPopover.setContentNode(popoverContent);
                tagPopover.show(tagButton);
            }
            else {
                PopOver tagPopover = tagPopOvers.get(logTable.getSelectionModel().getSelectedItem());
                tagPopover.show(tagButton);
            }


        });

        for (Log log : task.getLogs()) {
            PopOver tagPopover = new PopOver();
            VBox popoverContent = new VBox();
            popoverContent.setMinWidth(600);
            popoverContent.setPadding(new Insets(8, 8, 8, 8));
            tagPopover.setDetachedTitle(task.toString());

            TaggingPane taggingPane = new TaggingPane(log);
            taggingPane.setStyle(null);
            searchControls.add(taggingPane);

            popoverContent.getChildren().add(taggingPane);

            tagPopover.setContentNode(popoverContent);
            tagPopOvers.put(log, tagPopover);
        }

        addButton.setOnAction((event) -> {
                ValidationStyle.borderGlowNone(startDatePicker.getDatePicker());

                if (personComboBox.getValue() != null && startDatePicker.getValue() != null) {
                    LocalDate startDate = startDatePicker.getValue();
                    Person selectedPerson = personComboBox.getValue();
                    double duration = DurationConverter.readDurationToMinutes(durationTextField.getText());
                    double effortLeft = 0;
                    if (DurationConverter.readDurationToMinutes(effortLeftField.getText()) != 0) {
                        effortLeft = DurationConverter.readDurationToMinutes(effortLeftField.getText());
                    }

                    LocalDateTime dateTime = startDate.atTime(timeTextField.getHours(), timeTextField.getMinutes());
                    double effortLeftDifference = task.getEffortLeft() - effortLeft;

                    Person partner = partnerComboBox.getComboBox().getSelectionModel().getSelectedItem();
                    partner = partner == nullPerson ? null : partner;
                    Log newLog = new Log(task, descriptionTextArea.getText(),
                            selectedPerson, partner, duration, dateTime, effortLeftDifference);

                    task.getStory().getProject().add(newLog);

                    String effortLeftString = "";
                    if (task.getEffortLeft() == 0
                            || task.getEffortLeft()
                            - DurationConverter.readDurationToMinutes(durationTextField.getText()) <= 0) {
                        effortLeftString = "0h 0min";
                    }
                    else {
                        double newEffortLeft = task.getEffortLeft()
                                - DurationConverter.readDurationToMinutes(durationTextField.getText());
                        effortLeftString = (int) Math.floor(newEffortLeft / 60) + "h "
                                + (int) Math.floor(newEffortLeft % 60) + "min";
                    }

                    effortLeftField.getTextField().setText(effortLeftString);
                    data.removeAll(task.getLogsWithoutGhostLogs());
                    data.addAll(task.getLogsWithoutGhostLogs());
                    SearchableTable.refresh(logTable, logTable.getItems());
                    // Refresh the table if used, so that the logged effort updates after adding
                    if (table != null) {
                        SearchableTable.refresh(table, table.getItems());
                    }
                    sortLogTableData();
                    event.consume();
                }
                else {
                    event.consume();
                }
            });

        deleteButton.setOnAction((event) -> {
                Log selectedLog = logTable.getSelectionModel().getSelectedItem();
                if (selectedLog != null) {

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete");
                    alert.setHeaderText("Delete Log?");
                    alert.setContentText("Do you really want to delete this log?");

                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                    Optional<ButtonType> result  = alert.showAndWait();

                    if (result.get() == buttonTypeYes) {
                        logTable.getSelectionModel().getSelectedItem().deleteLog();
                    }
                    else if (result.get() == buttonTypeNo) {
                        event.consume();
                    }
                }
            });

        content.getChildren().add(logTable);
        content.getChildren().add(tagButton);
        content.getChildren().add(new Separator());
        content.getChildren().add(newLogFields);
        content.getChildren().add(buttons);

        this.getChildren().add(content);
    }

    /**
     * Updates the list of people bound to the combo boxes so that the partner/logger is remove from them
     * @param peopleList the list to update
     * @param removePerson the person to remove
     * @param isPartnerList whether the list is the one bound to the partner combo box
     */
    public void updateObservablePeopleList(ObservableList<Person> peopleList, Person removePerson,
                                            Boolean isPartnerList) {
        peopleList.clear();
        if (isPartnerList) {
            peopleList.add(nullPerson);
        }
        peopleList.addAll(availablePeople);
        peopleList.remove(removePerson);
    }

    /**
     * Gets the list of available loggers. Available loggers is the list of loggers minus
     * any currently set person in the partner combo box.
     * @return List of available loggers
     */
    public ObservableList<Person> getAvailableLoggerList() {
        return this.availableLoggers;
    }

    /**
     * Gets the list of available partners. Available partners is the list of loggers minus
     * any currently set person in the logger combo box.
     * @return List of available partners
     */
    public ObservableList<Person> getAvailablePartnerList() {
        return this.availablePartners;
    }


    /**
     * Queries all the searchable controls within the pane to see if they have a matching string
     * @param query the string to be queried
     * @return if a matching query was found
     */
    @Override
    public boolean query(String query) {
        boolean found = false;
        for (SearchableControl control : searchControls) {
            found = control.query(query) || found;
        }
        return found;
    }

    /**
     * Iterates over all the controls within this control and calls their advanced search method.
     * @param query the string to be searched
     * @param searchType SearchType for the Advanced Search
     * @return int the priority value
     */
    @Override
    public int advancedQuery(String query, SearchType searchType) {
        int found = 0;
        for (SearchableControl control : searchControls) {
            if (control.advancedQuery(query, searchType) > 0) {
                return control.advancedQuery(query, searchType);
            }
        }
        return found;
    }


    /**
     * Sorts the list of logs by its start date and time
     */
    private void sortLogTableData() {
        Collections.sort(this.data, new Comparator<Log>() {
            @Override
            public int compare(Log log1, Log log2) {
                if (log1.getStartDate().isBefore(log2.getStartDate())) {
                    return 1;
                }
                else if (log1.getStartDate() == log2.getStartDate()) {
                    return 0;
                }
                else {
                    return -1;
                }
            }
        });
    }

}
