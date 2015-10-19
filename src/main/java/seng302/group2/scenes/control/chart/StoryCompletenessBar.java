package seng302.group2.scenes.control.chart;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.controlsfx.control.PopOver;
import seng302.group2.scenes.control.PopOverTip;
import seng302.group2.util.conversion.ColorUtils;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Task;

import java.text.DecimalFormat;

/**
 * A class for creating and controlling the Story completeness bar.
 * Created by cvs20 on 27/08/15.
 */
public class StoryCompletenessBar extends Pane {

    Story story = null;
    ObservableList<Task> tasks = FXCollections.observableArrayList();
    
    double width = 340;
    double height = 25;
    
    public StoryCompletenessBar(Story story) {
        this.story = story;
        this.tasks =  story.getTasks();
        construct();
    }

    public StoryCompletenessBar(Story story, double width, double height) {
        this.story = story;
        this.tasks =  story.getTasks();
        this.width = width;
        this.height = height;
        construct();
    }
    
    
    void construct() {
        ObservableList<Task> done = FXCollections.observableArrayList();
        ObservableList<Task> inProgress = FXCollections.observableArrayList();
        ObservableList<Task> rest = FXCollections.observableArrayList();

        for (Task task : tasks) {
            if (task.getLane() == Task.TASKSTATE.DONE || task.getState() == Task.TASKSTATE.DEFERRED) {
                done.add(task);
            }
            else if (task.getLane() == Task.TASKSTATE.NOT_STARTED) {
                rest.add(task);
            }
            else {
                inProgress.add(task);
            }
        }


        double lengthGreen = getEffortSpent(done);
        double lengthBlue = getEffortSpent(inProgress);
        double lengthRed = getEffortLeft(inProgress) + getEffortLeft(rest);

        double overallLength = lengthBlue + lengthGreen + lengthRed;

        double percentageGreen = 0;
        double percentageBlue = 0;
        double percentageRed = 1;

        if (overallLength != 0.0) {
            percentageGreen = (lengthGreen / overallLength);
            percentageBlue = (lengthBlue / overallLength);
            percentageRed = (lengthRed / overallLength);
        }

        double maxWidth = this.width;
        double maxGreen = maxWidth * percentageGreen;
        double maxBlue = (maxWidth * percentageBlue) + maxGreen;


        //Creates visualisation bar
        DecimalFormat df = new DecimalFormat("##");

        GridPane visualGrid = new GridPane();


        Rectangle green = new Rectangle(maxGreen, this.height);
        Rectangle greenOff = new Rectangle(7.5, this.height);
        greenOff.setFill(Color.TRANSPARENT);
        Rectangle greenSquare = new Rectangle(maxGreen - 7.5, this.height);
        HBox greenBg = new HBox();
        greenBg.getChildren().addAll(greenOff, greenSquare);


        green.setArcWidth(15);
        green.setArcHeight(15);
        VBox greenVBox = new VBox();
        Text greenPercentText = new Text(" Percentage of \"" + this.story.toString() + "\" done: "
                + (df.format(percentageGreen * 100)) + "% ");
        Text greenTaskNum = new Text(done.size() + " Tasks completed ");
        int greenHours = (int)lengthGreen / 60;
        int greenMinutes = (int)lengthGreen % 60;
        Text greenTimeSpent;
        if (greenHours == 0 && greenMinutes == 0) {
            greenTimeSpent = new Text(" No time spent on completed tasks ");
        }
        else if (greenHours == 0 ) {
            greenTimeSpent = new Text(" Spent " + greenMinutes + " minutes on completed tasks ");
        }
        else if (greenMinutes == 0) {
            greenTimeSpent = new Text(" Spent " + greenHours + " hours on completed tasks ");
        }
        else {
            greenTimeSpent = new Text(" Spent " + greenHours + " hours and " + greenMinutes
                    + " minutes on completed tasks ");
        }
        greenVBox.setAlignment(Pos.CENTER);
        greenVBox.setSpacing(5);
        greenVBox.getChildren().addAll(greenPercentText, greenTaskNum, greenTimeSpent);
        PopOverTip greenPO = new PopOverTip(green, greenVBox);
        //PopOverTip greenPO = new PopOverTip(green, new Text(" Percentage of Story Done: "
        //        + (df.format(percentageGreen * 100)) + "% "));
        greenPO.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        Rectangle blue = new Rectangle(maxBlue - maxGreen + 0.5, this.height);
        VBox blueVBox = new VBox();
        Text bluePercentText = new Text(" Percentage of \"" + this.story.toString() + "\" in progress: "
                + (df.format(percentageBlue * 100)) + "% ");
        Text blueTaskNum = new Text(inProgress.size() + " Tasks in progress ");
        int blueHoursSpent = (int)lengthBlue / 60;
        int blueMinutesSpent = (int)lengthBlue % 60;
        Text blueTimeSpent;
        if (blueHoursSpent == 0 && blueMinutesSpent == 0) {
            blueTimeSpent = new Text(" No time spent on in progress tasks ");
        }
        else if (blueHoursSpent == 0) {
            blueTimeSpent = new Text(blueMinutesSpent + " minutes spent on in progress tasks ");
        }
        else if (blueMinutesSpent == 0) {
            blueTimeSpent = new Text(" Spent " + blueHoursSpent + " hours on in progress tasks ");
        }
        else {
            blueTimeSpent = new Text(" Spent " + blueHoursSpent + " hours and " + blueMinutesSpent
                    + " minutes on in progress tasks");
        }
        int blueHoursLeft = (int)getEffortLeft(inProgress) / 60;
        int blueMinutesLeft = (int)getEffortLeft(inProgress) % 60;
        Text blueTimeLeft;
        if (blueHoursLeft == 0 && blueMinutesLeft == 0) {
            blueTimeLeft = new Text(" No time left on in progress tasks ");
        }
        else if (blueHoursLeft == 0) {
            blueTimeLeft = new Text(blueMinutesLeft + " minutes left on in progress tasks ");
        }
        else if (blueMinutesLeft == 0) {
            blueTimeLeft = new Text(blueHoursLeft + " hours left on in progress tasks ");
        }
        else {
            blueTimeLeft = new Text(blueHoursLeft + " hours and " + blueMinutesLeft
                    + " minutes left on in progress tasks");
        }
        blueVBox.setAlignment(Pos.CENTER);
        blueVBox.setSpacing(5);
        blueVBox.getChildren().addAll(bluePercentText, blueTaskNum, blueTimeSpent, blueTimeLeft);
        PopOverTip bluePO = new PopOverTip(blue, blueVBox);
        //PopOverTip bluePO = new PopOverTip(blue, new Text(" Percentage of Story In Progress: "
        //        + (df.format(percentageBlue * 100)) + "% "));
        bluePO.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        Rectangle red = new Rectangle(maxWidth - maxBlue, this.height);
        Rectangle redSquare = new Rectangle(red.getWidth() - red.getWidth() / 2.0, red.getHeight());
        red.setArcWidth(15);
        red.setArcHeight(15);
        VBox redVBox = new VBox();
        Text redPercentage = new Text(" Remaining Work to do on \"" + this.story.toString() + "\": "
                + (df.format(percentageRed * 100)) + "% ");
        Text redTaskNum = new Text(rest.size() + " Tasks not yet started ");
        int redHoursLeft = (int)getEffortLeft(rest) / 60;
        int redMinutesLeft = (int)getEffortLeft(rest) % 60;
        Text redTimeLeft;
        if (redHoursLeft == 0 && redMinutesLeft == 0) {
            redTimeLeft = new Text(" No time left on tasks not yet started ");
        }
        else if (redHoursLeft == 0) {
            redTimeLeft = new Text(redMinutesLeft + " minutes left on tasks not yet started ");
        }
        else if (redMinutesLeft == 0) {
            redTimeLeft = new Text(redHoursLeft + " hours left on tasks not yet started ");
        }
        else {
            redTimeLeft = new Text(redHoursLeft + " hours and " + redMinutesLeft
                    + " minutes left on tasks not yet started");
        }
        redVBox.setAlignment(Pos.CENTER);
        redVBox.setSpacing(5);
        redVBox.getChildren().addAll(redPercentage, redTaskNum, redTimeLeft);
        PopOverTip redPO = new PopOverTip(red, redVBox);
        //PopOverTip redPO = new PopOverTip(red, new Text(" Remaining work to do: "
        //        + (df.format(percentageRed * 100)) + "% "));
        redPO.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);

        red.setFill(ColorUtils.toColor(Task.TASKSTATE.BLOCKED.getColourString()));  // Red
        redSquare.setFill(red.getFill());
        green.setFill(ColorUtils.toColor(Task.TASKSTATE.DONE.getColourString()));  // Green
        greenSquare.setFill(green.getFill());
        blue.setFill(ColorUtils.toColor(Task.TASKSTATE.VERIFY.getColourString()));  // Blue


        if (maxBlue - maxGreen > 0.01) {
            if (maxGreen > 7.5 && maxGreen < maxWidth - 7.5) {
                visualGrid.add(greenBg, 0, 0);
            }
            if (maxGreen < 0.1) {
                if (maxGreen + maxBlue > maxWidth - 0.01) {
                    // no red at all
                    // round both
                    blue.setArcHeight(15);
                    blue.setArcWidth(15);
                    visualGrid.add(blue, 1, 0);
                }
                else {
                    // round left, square right of blue box, no green
                    HBox blueBox = new HBox();
                    Rectangle blueSquare = new Rectangle(maxBlue - 7.5, blue.getHeight());
                    blueSquare.setFill(blue.getFill());
                    blue.setArcWidth(15);
                    blue.setArcHeight(15);
                    Rectangle transRec = new Rectangle(7.5, blue.getHeight());
                    transRec.setFill(Color.TRANSPARENT);
                    blueBox.getChildren().add(transRec);
                    blueBox.getChildren().add(blueSquare);

                    visualGrid.add(blueBox, 1, 0);
                    visualGrid.add(blue, 1, 0);
                    visualGrid.add(redSquare, 2, 0);
                }
            }
            else if (red.getWidth() < 1) {
                // no red, round right
                Rectangle blueSquare;
                if ((maxBlue + maxGreen) - maxWidth == 0) {
                    blueSquare = new Rectangle(maxBlue - maxGreen, blue.getHeight());
                }
                else {
                    blueSquare = new Rectangle(maxBlue - maxGreen - 7.5, blue.getHeight());
                }
                blueSquare.setFill(blue.getFill());
                blue.setArcWidth(15);
                blue.setArcHeight(15);
                visualGrid.add(blueSquare, 1, 0);
                visualGrid.add(blue, 1, 0);
            }
            else {
                // normal
                visualGrid.add(blue, 1, 0);
            }
        }

        // There is no blue, but there is green
        if (maxBlue - maxGreen == 0 && maxGreen != 0 && maxWidth - maxGreen != 0) {
            HBox greenBox = new HBox();
            greenSquare.setWidth(green.getWidth() - 7.5);
            Rectangle transRec = new Rectangle(7.5, green.getHeight());
            transRec.setFill(Color.TRANSPARENT);
            greenBox.getChildren().add(transRec);
            greenBox.getChildren().add(greenSquare);
            visualGrid.add(greenBox, 0, 0);
        }

        if (maxGreen + maxBlue > 7 && !visualGrid.getChildren().contains(redSquare)) {
            visualGrid.add(redSquare, 2, 0);
        }

        visualGrid.add(green, 0, 0);

        if (maxBlue == 0 && maxGreen == 0) {
            Boolean allDone = true;
            for (Task task : story.getTasks()) {
                if (!(task.getState() == Task.TASKSTATE.DONE)) {
                    allDone = false;
                }
            }
            if (allDone) {
                red.setFill(ColorUtils.toColor(Task.TASKSTATE.DONE.getColourString()));
            }
        }

        visualGrid.add(red, 2, 0);
        this.getChildren().add(visualGrid);
    }


    public double getEffortSpent(ObservableList<Task> taskList) {
        double totalEffortSpent = 0;
        for (Task task : taskList) {
            totalEffortSpent += task.getEffortSpent();
        }
        return totalEffortSpent;
    }

    public double getEffortLeft(ObservableList<Task> taskList) {
        double totalEffortLeft = 0;
        for (Task task : taskList) {
            totalEffortLeft += task.getEffortLeft();
        }
        return totalEffortLeft;
    }
}
