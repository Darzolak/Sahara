package seng302.group2.scenes.information.project.story;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.App;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.project.story.Story;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * Created by cvs20 on 13/07/15.
 */
public class StoryDependenciesTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    Story currentStory;


    /**
     * Constructor for the Story Dependencies Tab.
     * 
     * @param currentStory The currently selected story
     */
    public StoryDependenciesTab(Story currentStory) {
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
        this.setText("Dependencies");
        Pane dependenciesPane = new VBox(10);  // The pane that holds the basic info
        dependenciesPane.setBorder(null);
        dependenciesPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(dependenciesPane);
        this.setContent(wrapper);

        SearchableText title = new SearchableTitle("Dependencies of " + currentStory.getShortName());

        ObservableList<Story> dataDependants = observableArrayList();
        dataDependants.addAll(currentStory.getDependentOn());

        SearchableTable<Story> dependsTable = new SearchableTable<>(dataDependants);
        SearchableText tablePlaceholder = new SearchableText("This story is not currently dependent on another");
        dependsTable.setEditable(true);
        dependsTable.setPrefWidth(500);
        dependsTable.setPrefHeight(200);
        dependsTable.setPlaceholder(tablePlaceholder);
        dependsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Story, String> storyCol2 = new TableColumn<>("Dependent On");
        storyCol2.setCellValueFactory(new PropertyValueFactory<>("shortName"));
        storyCol2.prefWidthProperty().bind(dependsTable.widthProperty()
                .subtract(2).divide(100).multiply(60));


        TableColumn<Story, Integer> priorityCol2 = new TableColumn<>("Priority");
        priorityCol2.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol2.prefWidthProperty().bind(dependsTable.widthProperty()
                .subtract(2).divide(100).multiply(20));

        dependsTable.getColumns().add(storyCol2);
        dependsTable.getColumns().add(priorityCol2);




        SearchableTable<Story> dependantsTable = new SearchableTable<>();
        dependantsTable.setEditable(true);
        dependantsTable.setPrefWidth(500);
        dependantsTable.setPrefHeight(200);
        dependantsTable.setPlaceholder(new SearchableText("There are currently no stories dependent on this story",
                searchControls));
        dependantsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<Story> dataDependencies = observableArrayList();
        dataDependencies.addAll(currentStory.getDependentOnThis());
        dependantsTable.setItems(dataDependencies);

        TableColumn<Story, String> storyCol = new TableColumn<>("Dependant On This");
        storyCol.setCellValueFactory(new PropertyValueFactory<>("shortName"));
        storyCol.prefWidthProperty().bind(dependantsTable.widthProperty().subtract(2).divide(100).multiply(60));

        TableColumn<Story, Integer> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.prefWidthProperty().bind(dependantsTable.widthProperty().subtract(2).divide(100).multiply(20));


        dependantsTable.getColumns().addAll(storyCol, priorityCol);


        Button btnView = new Button("View");

        btnView.setOnAction((event) -> {
            if (dependsTable.getSelectionModel().getSelectedItem() != null) {
                App.mainPane.selectItem(dependsTable.getSelectionModel().getSelectedItem());
            }
            if (dependantsTable.getSelectionModel().getSelectedItem() != null) {
                App.mainPane.selectItem(dependantsTable.getSelectionModel().getSelectedItem());
            }
        });

        dependenciesPane.getChildren().addAll(
                dependsTable,
                dependantsTable,
                btnView
        );

        // Add items to pane & search collection
        Collections.addAll(searchControls,
                title,
                tablePlaceholder,
                dependsTable,
                dependantsTable
        );
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Story Dependencies Tab";
    }
}