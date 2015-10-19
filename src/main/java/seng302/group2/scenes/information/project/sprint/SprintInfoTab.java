package seng302.group2.scenes.information.project.sprint;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.CustomInfoLabel;
import seng302.group2.scenes.control.chart.StoryCompletenessBar;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Task;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The sprint information tab.
 * Created by drm127 on 29/07/15.
 */
public class SprintInfoTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();
    Sprint currentSprint;

    /**
     * Constructor for the sprint information tab.
     * @param currentSprint the currently selected sprint
     */
    public SprintInfoTab(Sprint currentSprint) {
        this.currentSprint = currentSprint;
        construct();
    }


    /**
     * Creates a stacked series of TitledPanes to display each story in the sprint and its tasks
     * @param currentSprint The current sprint
     * @return A VBox containing the stacked TitledPanes
     */
    private VBox createStoryTitlePanes(Sprint currentSprint) {
        final VBox stackedStoryTitlePanes = new VBox();

        List<Story> stories = new ArrayList<>();
        stories.addAll(currentSprint.getStories().sorted(Story.StoryPriorityComparator));
        stories.add(currentSprint.getUnallocatedTasksStory());

        for (Story story : stories) {
            VBox VtaskBox = new VBox(30);
            VBox taskBox = new VBox(4);
            if (story.getTasks().size() != 0) {
                VtaskBox.getChildren().add(new StoryCompletenessBar(story));
                for (Task task : story.getTasks().sorted(Task.TaskNameComparator)) {
                    taskBox.getChildren().add(new ScrumBoardTaskCellNode(task));
                }
            }
            else {
                taskBox.getChildren().add(new SearchableText("This story currently has no tasks.", searchControls));
            }
            VtaskBox.getChildren().add(taskBox);
            TitledPane storyPane = new TitledPane("[" + story.getEstimate() + "] "
                    + story.getShortName() + " - " + story.getReadyString(), VtaskBox);

            storyPane.setPrefHeight(30);
            storyPane.setExpanded(false);
            storyPane.setAnimated(true);
            stackedStoryTitlePanes.getChildren().add(storyPane);
        }

        return stackedStoryTitlePanes;
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
        this.setText("Basic Information");
        Pane basicInfoPane = new VBox(10);
        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

//        Platform.runLater(() -> {
        SearchableText title = new SearchableTitle(currentSprint.getLongName());

        /*//SUBJECT TO CHANGE BASED ON FUTURE STORIES
        ObservableList<Story> data = observableArrayList();
        data.addAll(currentSprint.getStories());
        ListView sprintStoryBox = new ListView(data);
        sprintStoryBox.setPrefHeight(192);
        sprintStoryBox.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);*/



        final Separator separator = new Separator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        CustomInfoLabel sprintName = new CustomInfoLabel("Sprint Name: ", currentSprint.getLongName());
        TagLabel sprintTags = new TagLabel(currentSprint.getTags());
        CustomInfoLabel sprintGoal = new CustomInfoLabel("Sprint Goal: ", currentSprint.getGoal());
        String startDateString = currentSprint.getStartDate().format(formatter);
        CustomInfoLabel sprintStart = new CustomInfoLabel("Start Date: ", startDateString);
        String endDateString = currentSprint.getEndDate().format(formatter);
        CustomInfoLabel sprintEnd = new CustomInfoLabel("End Date: ", endDateString);
        CustomInfoLabel desc = new CustomInfoLabel("Description: ", currentSprint.getDescription());
        CustomInfoLabel team = new CustomInfoLabel("Team: ", currentSprint.getTeam().toString());
        CustomInfoLabel project = new CustomInfoLabel("Project: ", currentSprint.getProject().toString());
        CustomInfoLabel release = new CustomInfoLabel("Release: ", currentSprint.getRelease().toString());
        CustomInfoLabel stories = new CustomInfoLabel("Stories: ", "");

        basicInfoPane.getChildren().addAll(
                title,
                sprintName,
                sprintGoal,
                sprintTags,
                sprintStart,
                sprintEnd,
                desc,
                separator,
                team,
                project,
                release,
                stories

        );

        Collections.addAll(searchControls,
                title,
                sprintName,
                sprintGoal,
                sprintTags,
                sprintStart,
                sprintEnd,
                desc,
                team,
                project,
                release,
                stories
        );

        basicInfoPane.getChildren().addAll(createStoryTitlePanes(currentSprint));
//        });
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Sprint Info Tab";
    }

    /**
     * Switches to the edit scene
     */
    public void edit() {
        currentSprint.switchToInfoScene(true);
    }

}
