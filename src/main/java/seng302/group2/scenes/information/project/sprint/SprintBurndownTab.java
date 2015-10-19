package seng302.group2.scenes.information.project.sprint;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.chart.BurndownChart;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.control.search.SearchableTitle;
import seng302.group2.workspace.project.sprint.Sprint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * A class for displaying a tab showing a sprints burndown graph.
 * Created by btm38 on 31/07/15.
 */
public class SprintBurndownTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    private Sprint currentSprint;
    private CategoryAxis xAxis;
    private NumberAxis yAxis;

    /**
     * Constructor for SprintBurndownTab.
     *
     * @param currentSprint the corresponding sprint
     */
    public SprintBurndownTab(Sprint currentSprint) {
        this.currentSprint = currentSprint;
        construct();
    }

    /**
     * Sets the axis configuration.
     */
    private void configureAxis() {
        ObservableList<String> xLabels = FXCollections.observableArrayList();


        boolean sprintEndReached = false;
        LocalDate startDate = this.currentSprint.getStartDate();
        while (!sprintEndReached) {
            xLabels.add(startDate.getMonth().toString().substring(0, 3) + " " + startDate.getDayOfMonth());
            startDate = startDate.plusDays(1);
            if (startDate.isAfter(currentSprint.getEndDate())) {
                sprintEndReached = true;
            }
        }
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();

        xAxis.setAutoRanging(false);
        xAxis.setCategories(xLabels);
        xAxis.invalidateRange(xLabels);
        xAxis.setLabel("Date");
        yAxis.setLabel("Hours");

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
        this.setText("Burndown Chart");
        Pane burndownPane = new VBox(10);
        burndownPane.setBorder(null);
        burndownPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(burndownPane);
        this.setContent(wrapper);

        SearchableText title = new SearchableTitle(currentSprint.getGoal() + " Burndown");
        burndownPane.getChildren().add(title);

        //defining the axes


        configureAxis();
        BurndownChart burndown = new BurndownChart(xAxis, yAxis);
        burndown.setPrefSize(800, 600);
        burndown.setMaxSize(800, 600);
        burndown.populateGraph(currentSprint);

        burndownPane.getChildren().add(burndown);


        // Add all our searchable controls on the page to the collection of searchable items
        Collections.addAll(searchControls, title);
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Sprint Burndown Tab";
    }
}
