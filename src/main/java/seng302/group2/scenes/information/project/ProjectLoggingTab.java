package seng302.group2.scenes.information.project;

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
import seng302.group2.scenes.control.Tooltip;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Class for Displaying all Logs under each project based on a date range.
 * Created by crw73 on 14/08/15.
 */
public class ProjectLoggingTab extends SearchableTab {
    private List<SearchableControl> searchControls = new ArrayList<>();
    private LocalDate startDate;
    private LocalDate endDate;
    private ObservableList<Log> data = observableArrayList();
    private Project currentProject;

    /**
     * Constructor for the Project Logging Tab
     *
     * @param currentProject The currently selected Project
     */
    public ProjectLoggingTab(Project currentProject) {
        this.currentProject = currentProject;
        construct();
    }


    private void updateFilteredLogs(Project currentProject) {
        data.clear();
        for (Log log : currentProject.getLogs()) {
            if (log.getLocalStartDate().isAfter(startDate.minusDays(1))
                    && log.getLocalStartDate().isBefore(endDate.plusDays(1))) {
                data.add(log);
            }
        }
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

        SearchableTable<Log> logTable = new SearchableTable<>();
        logTable.setEditable(false);
        logTable.setPrefWidth(700);
        logTable.setPrefHeight(200);
        logTable.setPlaceholder(new SearchableText("There are currently no "
                + "logs between the specified dates.", searchControls));
        logTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        startDatePicker.prefWidthProperty().bind(logTable.widthProperty()
                .divide(2));
        endDatePicker.prefWidthProperty().bind(logTable.widthProperty()
                .divide(2));

        updateFilteredLogs(currentProject);

        TableColumn loggerCol = new TableColumn("Logger");
        loggerCol.setCellValueFactory(new PropertyValueFactory<Log, Person>("logger"));
        loggerCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        TableColumn partnerCol = new TableColumn("Partner");
        partnerCol.setCellValueFactory(new PropertyValueFactory<Log, Person>("partner"));
        partnerCol.setEditable(true);
        partnerCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        TableColumn taskCol = new TableColumn("Task");
        taskCol.setCellValueFactory(new PropertyValueFactory<Log, Task>("task"));
        taskCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        TableColumn descriptionCol = new TableColumn("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<Log, String>("description"));
        descriptionCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));

        TableColumn startTimeCol = new TableColumn("Start Time");
        startTimeCol.setCellValueFactory(
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
        startTimeCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));
        startTimeCol.setSortType(TableColumn.SortType.ASCENDING);

        TableColumn durationCol = new TableColumn("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<Log, Long>("durationString"));
        durationCol.prefWidthProperty().bind(logTable.widthProperty()
                .subtract(2).divide(100).multiply(60));


        logTable.setItems(data);
        TableColumn[] columns = {loggerCol, partnerCol, taskCol, descriptionCol, startTimeCol, durationCol};
        logTable.getColumns().setAll(columns);

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
        logTable.getColumns().addListener(new ListChangeListener() {
            public boolean suspended;

            @Override
            public void onChanged(ListChangeListener.Change change) {
                change.next();
                if (change.wasReplaced() && !suspended) {
                    this.suspended = true;
                    logTable.getColumns().setAll(columns);
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
                    endDatePicker.setTooltip(new Tooltip("The end date cannot be before the start date!"));
                }

                //TODO Actually restrict date usage
                startDate = newValue;
                updateFilteredLogs(currentProject);
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
                    endDatePicker.setTooltip(new Tooltip("The end date cannot be before the start date!"));
                }

                //TODO Actually restrict date usage
                endDate = newValue;
                updateFilteredLogs(currentProject);
            }
        });

        basicInfoPane.getChildren().add(title);
        basicInfoPane.getChildren().add(datePickers);
        basicInfoPane.getChildren().add(logTable);


        Collections.addAll(searchControls,
                title,
                startDatePicker,
                endDatePicker,
                logTable
        );
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Project Logs Tab";
    }
}
