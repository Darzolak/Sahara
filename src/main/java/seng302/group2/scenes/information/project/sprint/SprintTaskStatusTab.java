package seng302.group2.scenes.information.project.sprint;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.CustomComboBox;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Task;

import java.util.*;


/**
 * Task Visualisation Tab
 * Created by cvs20 on 6/09/15.
 */

public class SprintTaskStatusTab extends SearchableTab {

    public ToggleGroup group = new ToggleGroup();
    public CustomComboBox<String> filterBox;
    List<SearchableControl> searchControls = new ArrayList<>();
    public SearchableRadioButton statusToggle = new SearchableRadioButton("Status", searchControls);
    public SearchableRadioButton storyToggle = new SearchableRadioButton("Story", searchControls);
    Pane basicInfoPane = new VBox();
    VBox listWrapper = new VBox();
    String unassignedFilter = "Unassigned";
    String uncompletedFilter = "Uncompleted";
    String allFilter = "All Tasks";

    Sprint sprint = null;


    /**
     * Constructor for Task Visualisation tab
     *
     *@param currentSprint the currently selected sprint
     */
    public SprintTaskStatusTab(Sprint currentSprint) {
        this.sprint = currentSprint;
        construct();
    }


    public void createVisualisation() {
        // Remove what's already in the box
        listWrapper.getChildren().clear();

        // Collect the tasks based on filters
        List<Task> tasks = collectTasks();

        // Create lists based on grouping radio
        if (statusToggle.getRadioButton().isSelected()) {
            listWrapper.getChildren().add(createStatusTitlePanes(tasks));
        }
        else if (storyToggle.getRadioButton().isSelected()) {
            listWrapper.getChildren().add(createStoryTitlePanes(tasks));
        }
        // No other case (at the moment)

        if (basicInfoPane.getChildren().contains(listWrapper)) {
            basicInfoPane.getChildren().remove(listWrapper);
        }
        basicInfoPane.getChildren().add(listWrapper);
    }


    public List<Task> collectTasks() {
        // Collect all the tasks in the sprint
        List<Task> tasks = new ArrayList<>();
        tasks.addAll(sprint.getAllTasks());

        // Filter the tasks based on the combo selection
        String filter = filterBox.getComboBox().getSelectionModel().getSelectedItem();
        Set<Task> tasksToRemove = new HashSet<>();

        if (filter.equals(unassignedFilter)) {
            // Add all the tasks that are assigned to the removal list
            for (Task task : tasks) {
                if (task.getAssignee() != null) {
                    tasksToRemove.add(task);
                }
            }
        }
        else if (filter.equals(uncompletedFilter)) {
            // Add all the tasks that are completed to the removal list
            for (Task task : tasks) {
                if (task.getState() == Task.TASKSTATE.DONE) {
                    tasksToRemove.add(task);
                }
            }
        }
        else if (filter.equals(allFilter)) {
            // Don't need to do anything here
        }

        // Remove the tasks from the final list
        tasks.removeAll(tasksToRemove);

        return tasks;
    }


    // Modified from SprintInfoTab
    VBox createStoryTitlePanes(Collection<Task> tasks) {
        final VBox stackedStoryTitlePanes = new VBox();

        List<Story> stories = new ArrayList<>();
        stories.addAll(sprint.getStories().sorted(Story.StoryPriorityComparator));
        stories.add(sprint.getUnallocatedTasksStory());

        for (Story story : stories) {
            VBox VtaskBox = new VBox(30);
            VBox taskBox = new VBox(4);
            if (story.getTasks().size() != 0) {
                for (Task task : story.getTasks().sorted(Task.TaskNameComparator)) {
                    if (tasks.contains(task)) {  // only add it if it is contained in the filtered collection of tasks
                        Node cellNode = new ScrumBoardTaskCellNode(task, this);
                        taskBox.getChildren().add(cellNode);
                    }
                }
            }
            else {
                taskBox.getChildren().add(new SearchableText("This story currently has no tasks.", searchControls));
            }
            VtaskBox.getChildren().add(taskBox);
            TitledPane storyPane = new TitledPane("[" + story.getEstimate() + "] "
                    + story.getShortName() + " - " + story.getReadyString(), VtaskBox);

            storyPane.setPrefHeight(30);
            storyPane.setExpanded(true);
            storyPane.setAnimated(true);
            stackedStoryTitlePanes.getChildren().add(storyPane);
        }
        return stackedStoryTitlePanes;
    }


    // Modified from SprintInfoTab
    VBox createStatusTitlePanes(List<Task> tasks) {
        final VBox stackedStoryTitlePanes = new VBox();

        List<Story> stories = new ArrayList<>();
        stories.addAll(sprint.getStories().sorted(Story.StoryPriorityComparator));
        stories.add(sprint.getUnallocatedTasksStory());

        for (Task.TASKSTATE state : Task.TASKSTATE.values()) {
            VBox VtaskBox = new VBox(30);
            VBox taskBox = new VBox(4);

            tasks.sort(Task.TaskNameComparator);
            for (Task task : tasks) {
                if (task.getState() == state) {  // only add it if it is contained in the filtered collection of tasks
                    taskBox.getChildren().add(new ScrumBoardTaskCellNode(task, this));
                }
            }
            if (taskBox.getChildren().isEmpty()) {
                taskBox.getChildren().add(new SearchableText("There are currently no tasks with this status.",
                        searchControls));
            }
            VtaskBox.getChildren().add(taskBox);
            TitledPane storyPane = new TitledPane(state.toString(), VtaskBox);

            storyPane.setPrefHeight(30);
            storyPane.setExpanded(true);
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

        // Tab settings
        this.setText("Task Visualisation");
        basicInfoPane = new VBox(10);
        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);


        SearchableText title = new SearchableTitle("Task Visualisation", searchControls);
        SearchableText groupBy = new SearchableText("Group By: ", searchControls);
        groupBy.setStyle("-fx-font-weight: bold");

        statusToggle.getRadioButton().setToggleGroup(group);
        statusToggle.getRadioButton().setSelected(true);
        storyToggle.getRadioButton().setToggleGroup(group);

        group.selectedToggleProperty().addListener(event -> {
            createVisualisation();
        });

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(groupBy, statusToggle, storyToggle);

        filterBox = new CustomComboBox<>("Filter by: ", searchControls);
        filterBox.getComboBox().getSelectionModel().selectedItemProperty().addListener(event -> {
            createVisualisation();
        });


        filterBox.addToComboBox(unassignedFilter);
        filterBox.addToComboBox(uncompletedFilter);
        filterBox.addToComboBox(allFilter);

        filterBox.setValue(unassignedFilter);



        Platform.runLater(() -> {
            try {
                basicInfoPane.getChildren().addAll(
                        title,
                        buttonBox,
                        filterBox
                );
            }
            catch (IllegalArgumentException exception) {

            }

            createVisualisation();

            Collections.addAll(searchControls,
                    title,
                    statusToggle,
                    storyToggle,
                    filterBox
            );
        });
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Sprint Task Status Tab";
    }

}
