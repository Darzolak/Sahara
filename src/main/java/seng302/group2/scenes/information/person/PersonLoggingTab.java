package seng302.group2.scenes.information.person;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import seng302.group2.scenes.control.CustomDatePicker;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Tab for displaying the Persons Logs
 * Created by crw73 on 10/09/15.
 */
public class PersonLoggingTab extends SearchableTab {
    private List<SearchableControl> searchControls = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private ObservableList<Log> data = observableArrayList();
    private Person currentPerson;

    /**
     * Constructor for the Person Logging Tab
     *
     * @param currentPerson The currently selected Person
     */
    public PersonLoggingTab(Person currentPerson) {
        this.currentPerson = currentPerson;
        construct();


    }


    private void updateFilteredLogs(Person currentPerson) {
        data.clear();
        for (Log log : currentPerson.getLogs()) {
            if (log.getLocalStartDate().isAfter(startDate.minusDays(1))
                    && log.getLocalStartDate().isBefore(endDate.plusDays(1))) {
                data.add(log);
            }
        }
        sortLogTableData();
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
        this.setText("Logging Effort");
        Pane basicInfoPane = new VBox(10);

        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

        SearchableText title = new SearchableTitle("Logging Effort", searchControls);

        HBox datePickers = new HBox();

        endDate = LocalDate.now();
        startDate = LocalDate.now().minusYears(1);

        CustomDatePicker startDatePicker = new CustomDatePicker("Start Date", true);
        CustomDatePicker endDatePicker = new CustomDatePicker("End Date", true);

        startDatePicker.setValue(startDate);
        endDatePicker.setValue(endDate);

        datePickers.getChildren().addAll(startDatePicker, endDatePicker);

        SearchableTable<Log> taskTable = new SearchableTable<>();
        taskTable.setEditable(false);
        taskTable.setPrefWidth(700);
        taskTable.setPrefHeight(200);
        taskTable.setPlaceholder(new SearchableText("There are currently no "
                + "logs between the specified dates.", searchControls));
        taskTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        startDatePicker.prefWidthProperty().bind(taskTable.widthProperty()
                .divide(2));
        endDatePicker.prefWidthProperty().bind(taskTable.widthProperty()
                .divide(2));

        updateFilteredLogs(currentPerson);

        TableColumn partnerCol = new TableColumn("Partner");
//        partnerCol.setCellValueFactory(new PropertyValueFactory<Log, Person>("partner"));
        partnerCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Log, Person>,
                ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Log, Person> log) {
                // p.getValue() returns the Person instance for a particular TableView row
                SimpleStringProperty prop = new SimpleStringProperty();
                if (log.getValue().getLogger() != null && log.getValue().getPartner() != null) {
                    if (log.getValue().getLogger() == currentPerson) {
                        prop.set(log.getValue().getPartner().toString());
                    }
                    else {
                        prop.set(log.getValue().getLogger().toString());
                    }
                }

                return prop;
            }
        });


        partnerCol.setEditable(true);
        partnerCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        TableColumn taskCol = new TableColumn("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<Log, Task>("task"));
        taskCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        TableColumn descriptionCol = new TableColumn("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Log, String>("description"));
        descriptionCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));



        TableColumn startDateTimeCol = new TableColumn("Start Time");

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

        TableColumn startDateCol = new TableColumn("Date");
        startDateCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Log, String>,
                        ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Log,
                            String> log) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        property.setValue(log.getValue().getStartDate().format(formatter));
                        return property;
                    }
                });


        startDateTimeCol.getColumns().addAll(startDateCol, startTimeCol);

        TableColumn durationCol = new TableColumn("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<Log, Long>("durationString"));
        durationCol.prefWidthProperty().bind(taskTable.widthProperty()
                .subtract(2).divide(100).multiply(60));


        taskTable.setItems(data);
        TableColumn[] columns = {partnerCol, taskCol, descriptionCol, startDateTimeCol, durationCol};
        taskTable.getColumns().setAll(columns);

        final Callback<DatePicker, DateCell> endDateCellFactory =
                new Callback<DatePicker, DateCell>() {
                    @Override
                    public DateCell call(final DatePicker datePicker) {
                        return new DateCell() {
                            @Override
                            public void updateItem(LocalDate item, boolean empty) {
                                super.updateItem(item, empty);
                                if (startDatePicker.getValue() != null && (item.isBefore(startDatePicker.getValue()))) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                                }
                            }
                        };
                    }
                };
        endDatePicker.getDatePicker().setDayCellFactory(endDateCellFactory);

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

        //TODO restrict date selection so can't pick startdate past end date and visa-versa
        startDatePicker.getDatePicker().valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable,
                                LocalDate oldValue, LocalDate newValue) {

                if (newValue != null && endDatePicker.getValue() != null
                        && newValue.isAfter(endDatePicker.getValue())) {
                    ValidationStyle.borderGlowRed(endDatePicker.getDatePicker());
                    ValidationStyle.showMessage("The end date cannot be before the start date!",
                            endDatePicker.getDatePicker());
                    endDatePicker.setTooltip(new seng302.group2.scenes.control.Tooltip("The end date cannot be "
                            + "before the start date!"));
                }

                //TODO Actually restrict date usage
                startDate = newValue;
                updateFilteredLogs(currentPerson);
            }
        });

        endDatePicker.getDatePicker().valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable,
                                LocalDate oldValue, LocalDate newValue) {

                ValidationStyle.borderGlowNone(endDatePicker.getDatePicker());
                endDatePicker.removeTooltip();

                if (newValue != null && startDatePicker.getValue() != null
                        && newValue.isBefore(startDatePicker.getValue())) {
                    ValidationStyle.borderGlowRed(endDatePicker.getDatePicker());
                    ValidationStyle.showMessage("The end date cannot be before the start date!",
                            endDatePicker.getDatePicker());
                    endDatePicker.setTooltip(new seng302.group2.scenes.control.Tooltip("The end date cannot be before "
                            + "the start date!"));
                }

                //TODO Actually restrict date usage
                endDate = newValue;
                updateFilteredLogs(currentPerson);
            }
        });

        basicInfoPane.getChildren().add(title);
        basicInfoPane.getChildren().add(datePickers);
        basicInfoPane.getChildren().add(taskTable);


        Collections.addAll(searchControls,
                title,
                startDatePicker,
                endDatePicker,
                taskTable
        );
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Person Logs Tab";
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
