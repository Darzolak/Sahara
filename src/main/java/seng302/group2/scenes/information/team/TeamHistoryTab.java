package seng302.group2.scenes.information.team;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import seng302.group2.Global;
import seng302.group2.scenes.control.CustomComboBox;
import seng302.group2.scenes.control.CustomDatePicker;
import seng302.group2.scenes.control.DatePickerEditCell;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.util.validation.ValidationStatus;
import seng302.group2.workspace.allocation.Allocation;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.team.Team;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static seng302.group2.util.validation.DateValidator.validateAllocation;

/**
 * A class for history allocation tab to display a table of the selected team's allocation
 * history
 * Created by swi67 on 15/05/15.
 */
public class TeamHistoryTab extends SearchableTab {

    Set<SearchableControl> searchControls = new HashSet<>();
    Team currentTeam;

    Boolean isValidEdit;

    /**
     * Constructor for team allocation tab
     *
     * @param currentTeam currently selected team
     */
    public TeamHistoryTab(Team currentTeam) {
        this.currentTeam = currentTeam;
        construct();
    }

    /**
     * Displays the appropriate error dialog according to the validation status
     *
     * @param status the validation status
     */
    private void showErrorDialog(ValidationStatus status) {
        switch (status) {
            case VALID:
                break;
            case ALLOCATION_DATES_WRONG_ORDER:
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.getDialogPane().setStyle(" -fx-max-width:550px; -fx-max-height: 100px; -fx-pref-width: 550px; "
                        + "-fx-pref-height: 100px;");
                alert.setTitle("Error");
                alert.setHeaderText("Allocation Date Error");
                alert.setContentText("The end date of your new allocation cannot be before the start date.");
                alert.showAndWait();
                break;
            case ALLOCATION_DATES_EQUAL:
                Alert alertDatesEquals = new Alert(Alert.AlertType.ERROR);
                alertDatesEquals.getDialogPane().setStyle(" -fx-max-width:550px; -fx-max-height: 100px; "
                        + "-fx-pref-width: 550px; -fx-pref-height: 100px;");
                alertDatesEquals.setTitle("Error");
                alertDatesEquals.setHeaderText("Allocation Date Error");
                alertDatesEquals.setContentText("An allocation with those start and end dates already exists.");
                alertDatesEquals.showAndWait();
                break;
            case START_OVERLAP:
                Alert alertOverlap = new Alert(Alert.AlertType.ERROR);
                alertOverlap.getDialogPane().setStyle(" -fx-max-width:550px; -fx-max-height: 100px; "
                        + "-fx-pref-width: 550px; -fx-pref-height: 100px;");
                alertOverlap.setTitle("Error");
                alertOverlap.setHeaderText("Allocation Date Error");
                alertOverlap.setContentText("Start date overlaps with an already existing allocation for that team.");
                alertOverlap.showAndWait();
                break;
            case END_OVERLAP:
                Alert alertEndOverlap = new Alert(Alert.AlertType.ERROR);
                alertEndOverlap.getDialogPane().setStyle(" -fx-max-width:550px; -fx-max-height: 100px; "
                        + "-fx-pref-width: 550px; -fx-pref-height: 100px;");
                alertEndOverlap.setTitle("Error");
                alertEndOverlap.setHeaderText("Allocation Date Error");
                alertEndOverlap.setContentText("End date overlaps with an already existing allocation for that team.");
                alertEndOverlap.showAndWait();
                break;
            case SUPER_OVERLAP:
                Alert alertSuperOverlap = new Alert(Alert.AlertType.ERROR);
                alertSuperOverlap.getDialogPane().setStyle(" -fx-max-width:550px; -fx-max-height: 100px; "
                        + "-fx-pref-width: 550px; -fx-pref-height: 100px;");
                alertSuperOverlap.setTitle("Error");
                alertSuperOverlap.setHeaderText("Allocation Date Error");
                alertSuperOverlap.setContentText("Start and end dates encompass an existing allocation for that team.");
                alertSuperOverlap.showAndWait();
                break;
            case SUB_OVERLAP:
                Alert alertSubOverlap = new Alert(Alert.AlertType.ERROR);
                alertSubOverlap.getDialogPane().setStyle(" -fx-max-width:550px; -fx-max-height: 100px; "
                        + "-fx-pref-width: 550px; -fx-pref-height: 100px;");
                alertSubOverlap.setTitle("Error");
                alertSubOverlap.setHeaderText("Allocation Date Error");
                alertSubOverlap.setContentText("Start and end dates are encompassed by an existing allocation"
                        + " for that team.");
                alertSubOverlap.showAndWait();
                break;
            default:
                break;
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
        // Tab settings
        this.setText("Allocation History");
        Pane historyPane = new VBox(10);
        historyPane.setBorder(null);
        historyPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(historyPane);
        this.setContent(wrapper);

        // Create Table
        SearchableTable<Allocation> historyTable = new SearchableTable<>(currentTeam.getProjectAllocations(),
                searchControls);
        SearchableText tablePlaceholder = new SearchableText("This team has no project allocations.");
        historyTable.setEditable(true);
        historyTable.setPrefWidth(700);
        historyTable.setPrefHeight(400);
        historyTable.setPlaceholder(tablePlaceholder);
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Callback<TableColumn, TableCell> cellFactory = col -> new DatePickerEditCell();

        TableColumn<Allocation, String> teamCol = new TableColumn<>("Project");
        teamCol.setCellValueFactory(new PropertyValueFactory<>("Project"));
        teamCol.prefWidthProperty().bind(historyTable.widthProperty()
                .subtract(3).divide(100).multiply(40));
        teamCol.setResizable(false);

        TableColumn startDateCol = new TableColumn("Start Date");


        startDateCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Allocation, String>,
                        ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Allocation,
                            String> alloc) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        property.setValue(alloc.getValue().getStartDate().format(Global.dateFormatter));
                        return property;
                    }
                });

        startDateCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Allocation, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Allocation, String> event) {
                        isValidEdit = false;
                        if (!event.getNewValue().isEmpty()) {
                            Allocation currentAlloc = event.getTableView().getItems()
                                    .get(event.getTablePosition().getRow());

                            LocalDate newStartDate = LocalDate.parse(event.getNewValue(),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            ValidationStatus editValidationStatus = validateAllocation(
                                    currentAlloc.getProject(), currentAlloc.getTeam(),
                                    newStartDate, currentAlloc.getEndDate(),
                                    currentAlloc);

                            if (editValidationStatus == ValidationStatus.VALID) {
                                currentAlloc.editStartDate(newStartDate);
                                isValidEdit = true;
                            }
                            else {
                                showErrorDialog(editValidationStatus);
                                isValidEdit = false;
                            }
                        }
                    }
                });

        startDateCol.setCellFactory(cellFactory);
        startDateCol.prefWidthProperty().bind(historyTable.widthProperty()
                .subtract(3).divide(100).multiply(30));

        TableColumn endDateCol = new TableColumn("End Date");

        endDateCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Allocation, String>,
                        ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Allocation,
                            String> alloc) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        DateTimeFormatter formatter = Global.dateFormatter;
                        if (alloc.getValue().getEndDate() != null) {
                            property.setValue(alloc.getValue().getEndDate().format(formatter));
                        }
                        else {
                            property.setValue("");
                        }
                        return property;
                    }
                });

        endDateCol.prefWidthProperty().bind(historyTable.widthProperty()
                .subtract(3).divide(100).multiply(30));
        endDateCol.setCellFactory(cellFactory);
        endDateCol.setEditable(true);
        endDateCol.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Allocation, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Allocation, String> event) {
                        isValidEdit = false;
                        if (!event.getNewValue().isEmpty()) {
                            Allocation currentAlloc = event.getTableView().
                                    getItems().get(event.getTablePosition().getRow());

                            LocalDate newEndDate = LocalDate.parse(event.getNewValue(),
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                            ValidationStatus editValidationStatus =
                                    validateAllocation(currentAlloc.getProject(),
                                            currentAlloc.getTeam(),
                                            currentAlloc.getStartDate(), newEndDate,
                                            currentAlloc);

                            if (editValidationStatus == ValidationStatus.VALID) {
                                currentAlloc.editEndDate(newEndDate);
                                isValidEdit = true;
                            }
                            else {
                                showErrorDialog(editValidationStatus);
                                isValidEdit = false;
                            }
                        }
                    }
                });

        // Create controls
        SearchableText title = new SearchableTitle(currentTeam.getShortName() + " Allocation History");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.BOTTOM_RIGHT);
        Button addButton = new Button("Add");
        Button deleteButton = new Button("Delete");
        buttons.getChildren().addAll(addButton, deleteButton);

        HBox newAllocationFields = new HBox(35);
        CustomComboBox<Project> projectComboBox = new CustomComboBox<>("Project", true);
        CustomDatePicker startDatePicker = new CustomDatePicker("Start Date", true);
        CustomDatePicker endDatePicker = new CustomDatePicker("End Date", false);
        startDatePicker.getDatePicker().setStyle("-fx-pref-width: 200;");
        endDatePicker.getDatePicker().setStyle("-fx-pref-width: 200;");
        projectComboBox.getComboBox().setStyle("-fx-pref-width: 250;");

        projectComboBox.prefWidthProperty().bind(historyTable.widthProperty().subtract(3).divide(100).multiply(30));
        startDatePicker.prefWidthProperty().bind(historyTable.widthProperty().subtract(3).divide(100).multiply(30));
        endDatePicker.prefWidthProperty().bind(historyTable.widthProperty().subtract(3).divide(100).multiply(30));
        newAllocationFields.getChildren().addAll(projectComboBox, startDatePicker, endDatePicker);

        startDatePicker.getDatePicker().valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
                                LocalDate newValue) {
                ValidationStyle.borderGlowNone(startDatePicker.getDatePicker());
            }
        });

        // Events
        projectComboBox.getComboBox().setOnMouseClicked(event -> {
            projectComboBox.getComboBox().getItems().clear();
            for (Project proj : Global.currentWorkspace.getProjects()) {
                projectComboBox.getComboBox().getItems().add(proj);
            }
        });

        addButton.setOnAction((event) -> {
            ValidationStyle.borderGlowNone(projectComboBox.getComboBox());
            ValidationStyle.borderGlowNone(startDatePicker.getDatePicker());
            if (projectComboBox.getValue() != null && startDatePicker.getValue() != null) {
                LocalDate endDate = endDatePicker.getValue();
                LocalDate startDate = startDatePicker.getValue();
                Project selectedProject = null;

                for (Project proj : Global.currentWorkspace.getProjects()) {
                    if (proj.equals(projectComboBox.getValue())) {
                        selectedProject = proj;
                    }
                }

                if (validateAllocation(selectedProject, currentTeam, startDate, endDate)
                        == ValidationStatus.VALID) {
                    Allocation alloc = new Allocation(selectedProject, currentTeam,
                            startDate, endDate);
                    currentTeam.add(alloc);
                }
                else {
                    showErrorDialog(validateAllocation(selectedProject,
                            currentTeam, startDate, endDate));
                    event.consume();
                }
            }
            else {
                if (projectComboBox.getValue() == null) {
                    ValidationStyle.borderGlowRed(projectComboBox.getComboBox());
                    ValidationStyle.showMessage("Please select a project", projectComboBox.getComboBox());
                    event.consume();
                }
                if (startDatePicker.getValue() == null) {
                    ValidationStyle.borderGlowRed(startDatePicker.getDatePicker());
                    ValidationStyle.showMessage("Please select a date", startDatePicker.getDatePicker());
                    event.consume();
                }
            }
        });

        deleteButton.setOnAction((event) -> {
            Allocation selectedAlloc = historyTable.getSelectionModel().getSelectedItem();
            if (selectedAlloc != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Delete");
                alert.setHeaderText("Delete Allocation?");
                alert.setContentText("Do you really want to delete this allocation?");
                alert.getDialogPane().setStyle(" -fx-max-width:450; -fx-max-height: 100px; -fx-pref-width: 450px; "
                        + "-fx-pref-height: 100px;");

                ButtonType buttonTypeYes = new ButtonType("Yes");
                ButtonType buttonTypeNo = new ButtonType("No");

                alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
                Optional<ButtonType> result  = alert.showAndWait();

                if (result.get() == buttonTypeYes) {
                    selectedAlloc.delete();
                }
                else if (result.get() == buttonTypeNo) {
                    event.consume();
                }
            }
        });

        TableColumn[] columns = {teamCol, startDateCol, endDateCol};
        historyTable.getColumns().setAll(columns);

        // Listener to disable columns being movable
        historyTable.getColumns().addListener(new ListChangeListener() {
            public boolean suspended;

            @Override
            public void onChanged(Change change) {
                change.next();
                if (change.wasReplaced() && !suspended) {
                    this.suspended = true;
                    historyTable.getColumns().setAll(columns);
                    this.suspended = false;
                }
            }
        });

        // Add items to pane & search collection
        historyPane.getChildren().addAll(
                title,
                historyTable,
                newAllocationFields,
                buttons
        );

        Collections.addAll(searchControls,
                title,
                tablePlaceholder,
                projectComboBox,
                startDatePicker,
                endDatePicker
        );
    }


    /**
     * Returns whether the allocation edit was valid
     * @return true if valid
     */
    public Boolean getIsValidEdit() {
        return this.isValidEdit;
    }


    /**
     * Gets the striresentation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Team History Tab";
    }
}
