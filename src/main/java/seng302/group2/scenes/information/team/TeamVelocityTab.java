package seng302.group2.scenes.information.team;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.CustomInfoLabel;
import seng302.group2.scenes.control.chart.VelocityChart;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.team.Team;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by cvs20 on 24/09/15.
 */
public class TeamVelocityTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    private CategoryAxis xAxis = new CategoryAxis();
    private  NumberAxis yAxis = new NumberAxis();
    private Team currentTeam;

    /**
     * Constructor for team basic information tab
     *
     * @param currentTeam currently selected team
     */
    public TeamVelocityTab(Team currentTeam) {
        this.currentTeam = currentTeam;
        construct();
    }

    /**
     * Sets the axis configuration.
     */
    private void configureAxis() {
        xAxis.setLabel("Sprints");
        yAxis.setLabel("Average Points Per Week");
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
        this.setText("Team Velocity");

        Pane basicInfoPane = new VBox(10);
        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

        SearchableText title = new SearchableTitle(currentTeam.getShortName() + " Velocity");

        configureAxis();
        VelocityChart velocityChart = new VelocityChart(xAxis, yAxis);
        velocityChart.setPrefSize(800, 600);
        velocityChart.setMaxSize(800, 600);
        velocityChart.populateGraph(currentTeam);
        basicInfoPane.getChildren().addAll(title, velocityChart);
        Collections.addAll(searchControls, title);

    }


    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Team Velocity Tab";
    }

}
