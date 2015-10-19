package seng302.group2.scenes.control.chart;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import seng302.group2.scenes.control.Tooltip;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.tasks.Log;
import seng302.group2.workspace.project.story.tasks.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * A class for displaying a burndown chart.
 * Created by btm38 on 31/07/15.
 */
public class BurndownChart extends LineChart {

    public BurndownChart(Axis axis, Axis axis2) {
        super(axis, axis2);
        try {
            String css = this.getClass().getResource("/styles/chart.css").toExternalForm();
            this.getStylesheets().add(css);
        }
        catch (Exception ex) {
            System.err.println("Cannot acquire stylesheet: " + ex.toString());
        }
    }


    /**
     * Populates the chart with the information passed through in the current sprint.
     * This function will define the series, css, titles, and any other graph functionality.
     * @param currentSprint the sprint the chart is displaying.
     */
    public void populateGraph(Sprint currentSprint) {
        this.setTitle(currentSprint.getGoal() + " Burndown");


        XYChart.Series effortSpentSeries = createEffortSpentSeries(currentSprint);
        XYChart.Series effortLeftSeries = createLEffortLeftSeries(currentSprint);
        XYChart.Series referenceVelocitySeries = createReferenceVelocity(currentSprint);

        if (!currentSprint.getAllLogsWithInitialLogs().isEmpty()) {
            this.getData().add(referenceVelocitySeries);
            this.getData().add(effortSpentSeries);
            this.getData().add(effortLeftSeries);
        }

        /**
         * Browsing through the Data and applying ToolTip
         * as well as the class on hover
         */
        for (Object s : this.getData()) {
            XYChart.Series series = (XYChart.Series) s;
            for (Object obj : series.getData()) {
                XYChart.Data d = (XYChart.Data)obj;
                String hoursStr = d.getYValue().toString();
                double hoursVal = Double.parseDouble(hoursStr);
                Tooltip.install(d.getNode(), new Tooltip(Math.round(hoursVal) + " hours."));

                //Adding class on hover
                d.getNode().setOnMouseEntered(new EventHandler<Event>() {

                    @Override
                    public void handle(Event event) {
                        d.getNode().getStyleClass().add("onHover");
                    }
                });

                //Removing class on exit
                d.getNode().setOnMouseExited(new EventHandler<Event>() {

                    @Override
                    public void handle(Event event) {
                        d.getNode().getStyleClass().remove("onHover");
                    }
                });
            }
        }


    }

    /**
     * Creates an effort spent series.
     * @param currentSprint the current sprint
     * @return an XYChart.Series series representing an effort spent series.
     */
    public XYChart.Series createEffortSpentSeries(Sprint currentSprint) {
        double effortSpent = 0;

        List<Log> logList;
        logList = currentSprint.getAllLogsWithInitialLogs();

        /* Map which stores a (key, value) pair of (date: hours), where hours is the total n
        umber of hours logged on the date.
         */
        Map<LocalDate, Double> dailyEffortSpentMap = new LinkedHashMap<>();

        boolean sprintEndReached = false;
        LocalDate currDate = currentSprint.getStartDate();
        while (!sprintEndReached) {
            if (currDate.isAfter(currentSprint.getEndDate()) || currDate.isAfter(LocalDate.now())) {
                sprintEndReached = true;
            }
            else {
                dailyEffortSpentMap.put(currDate, 0.0);
                currDate = currDate.plusDays(1);
            }
        }

        for (Log log : logList) {
            if (dailyEffortSpentMap.containsKey(log.getStartDate().toLocalDate())) {
                Double sum = dailyEffortSpentMap.get(log.getStartDate().toLocalDate());
                dailyEffortSpentMap.put(log.getStartDate().toLocalDate(), sum + log.getDurationInHours());
            }
        }

        XYChart.Series effortSpentSeries = new XYChart.Series();
        effortSpentSeries.setName("Effort Spent");

        for (LocalDate d : dailyEffortSpentMap.keySet()) {
            effortSpent += dailyEffortSpentMap.get(d);
            String monthStr = d.getMonth().toString().substring(0, 3);
            XYChart.Data chartData = new XYChart.Data<>(monthStr + " " + d.getDayOfMonth(), effortSpent);
            Tooltip.install(chartData.getNode(), new Tooltip(String.valueOf(effortSpent)));
            effortSpentSeries.getData().add(chartData);
        }



        return effortSpentSeries;
    }

    /**
     * Creates a reference velocity series
     * @param currentSprint The current sprint
     * @return an XYChart.Series representing a reference velocity series
     */
    public XYChart.Series createReferenceVelocity(Sprint currentSprint) {
        LocalDate startDate = currentSprint.getStartDate();
        LocalDate endDate = currentSprint.getEndDate();
        double initialEffortLeft = currentSprint.totalEffortLeft() / 60;

        XYChart.Series referenceVelocitySeries = new XYChart.Series();
        referenceVelocitySeries.setName("Reference Velocity");

        String firstDayStr = startDate.getMonth().toString().substring(0, 3) + " " + startDate.getDayOfMonth();
        String lastDayStr = endDate.getMonth().toString().substring(0, 3) + " " + endDate.getDayOfMonth();
        referenceVelocitySeries.getData().add(new XYChart.Data<>(firstDayStr, initialEffortLeft));
        referenceVelocitySeries.getData().add(new XYChart.Data<>(lastDayStr, 0));

        return referenceVelocitySeries;

    }

    /**
     * Create effort left series.
     * @param currentSprint the sprint to generate the series from
     * @return an XYChart.Series representing an effort left series.
     */
    public XYChart.Series createLEffortLeftSeries(Sprint currentSprint) {
        List<Log> logList = currentSprint.getAllLogs();
        for (Task t : currentSprint.getAllTasks()) {
            if (t.getInitialLog().getStartDate().toLocalDate().isBefore(currentSprint.getStartDate())) {
                LocalDate date = currentSprint.getStartDate();
                int hourInt = t.getInitialLog().getStartDate().getHour();
                int minuteInt = t.getInitialLog().getStartDate().getMinute();
                LocalDateTime dateTime = date.atTime(hourInt, minuteInt);

                logList.add(new Log(t, "initial log (this should be hidden)", null, null, 0,
                        dateTime, t.getInitialLog().getEffortLeftDifferenceInMinutes()));
            }
            else {
                logList.add(t.getInitialLog());
            }
        }

        /* Map which stores a (key, value) pair of (date: hours), where hours is the total effort left
        for that date.
         */
        Map<LocalDate, Double> dailyEffortLeftMap = new LinkedHashMap<>();


        boolean sprintEndReached = false;
        LocalDate currDate = currentSprint.getStartDate();
        while (!sprintEndReached) {
            if (currDate.isAfter(currentSprint.getEndDate()) || currDate.isAfter(LocalDate.now())) {
                sprintEndReached = true;
            }
            else {
                dailyEffortLeftMap.put(currDate, 0.0);
                currDate = currDate.plusDays(1);
            }
        }

        for (Log log : logList) {
            if (dailyEffortLeftMap.containsKey(log.getStartDate().toLocalDate())) {
                Double sum = dailyEffortLeftMap.get(log.getStartDate().toLocalDate());
                dailyEffortLeftMap.put(log.getStartDate().toLocalDate(), sum + log.getEffortLeftDifferenceInHours());
            }
        }


        XYChart.Series effortLeftSeries = new XYChart.Series();
        effortLeftSeries.setName("Effort Left");

        double currentEffortLeft = 0;
        for (LocalDate d : dailyEffortLeftMap.keySet()) {
            currentEffortLeft -= dailyEffortLeftMap.get(d);
            String monthStr = d.getMonth().toString().substring(0, 3);
            effortLeftSeries.getData().add(new XYChart.Data<>(monthStr + " " + d.getDayOfMonth(), currentEffortLeft));
        }

        return effortLeftSeries;
    }
}


