package seng302.group2.scenes.information.project.sprint;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.search.*;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.project.story.tasks.Task;

import java.util.*;

/**
 * The information tab for a backlog
 * Created by cvs20 on 19/05/15.
 */
public class ScrumboardTab extends SearchableTab {

    public List<SearchableControl> searchControls = new ArrayList<>();
    public int hoverIndex;
    Map<SearchableListView<Task>, Task.TASKSTATE> laneStateDict = new HashMap<>();
    Task interactiveTask = null;
    Pane basicInfoPane = new VBox(10);
    Sprint currentSprint;

    /**
     * Constructor for the Backlog Info tab
     *
     * @param currentSprint The currently selected backlog
     */
    public ScrumboardTab(Sprint currentSprint) {
        this.currentSprint = currentSprint;

        this.setText("Scrumboard");
        basicInfoPane = new VBox(10);

        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

        construct();


    }


    /**
     * Creates a collapsable title pane to display the scrumboard information of stories
     * @param story The story to generate the pane for
     * @return A pane showing the information of tasks in the given story
     */
    private Node getCollapsableStoryPane(Story story) {
        GridPane taskLanesGrid = new GridPane();
        taskLanesGrid.setPadding(new Insets(8));

        SearchableListView<Task> todoLane = new SearchableListView<>(story.todoTasks);
        SearchableListView<Task> inProgressLane = new SearchableListView<>(story.inProgTasks);
        SearchableListView<Task> verifyLane = new SearchableListView<>(story.verifyTasks);
        SearchableListView<Task> completedLane = new SearchableListView<>(story.completedTasks);

        laneStateDict.put(todoLane, Task.TASKSTATE.NOT_STARTED);
        laneStateDict.put(inProgressLane, Task.TASKSTATE.IN_PROGRESS);
        laneStateDict.put(verifyLane, Task.TASKSTATE.VERIFY);
        laneStateDict.put(completedLane, Task.TASKSTATE.DONE);

        Set<SearchableListView<Task>> lanes = new HashSet<>();
        Collections.addAll(lanes, todoLane, inProgressLane, verifyLane, completedLane);
        //searchControls.addAll(lanes);  // The cells are added directly through the tab, the list views don't need to
        // be searched.
        initLaneListeners(lanes, story);

        // Set cell factory
        todoLane.setCellFactory(list -> new ScrumBoardTaskCell(todoLane, this));
        inProgressLane.setCellFactory(list -> new ScrumBoardTaskCell(inProgressLane, this));
        verifyLane.setCellFactory(list -> new ScrumBoardTaskCell(verifyLane, this));
        completedLane.setCellFactory(list -> new ScrumBoardTaskCell(completedLane, this));

        // Add labels to grid
        taskLanesGrid.add(new SearchableText("Todo", searchControls), 0, 0);
        taskLanesGrid.add(new SearchableText("In Progress", searchControls), 1, 0);
        taskLanesGrid.add(new SearchableText("Verify", searchControls), 2, 0);
        taskLanesGrid.add(new SearchableText("Done", searchControls), 3, 0);

        // Add task list views
        taskLanesGrid.add(todoLane, 0, 1);
        taskLanesGrid.add(inProgressLane, 1, 1);
        taskLanesGrid.add(verifyLane, 2, 1);
        taskLanesGrid.add(completedLane, 3, 1);

        return new TitledPane("[" + story.getEstimate() + "] "
                + story.getShortName(), taskLanesGrid);
    }


    private void initLaneListeners(Collection<SearchableListView<Task>> lanes, Story story) {
        for (ListView<Task> lane : lanes) {
            lane.setOnDragDetected(event -> {
                    interactiveTask = lane.getSelectionModel().getSelectedItem();
                });

            lane.setOnDragOver(dragEvent -> {
                    dragEvent.acceptTransferModes(TransferMode.MOVE);
                });

            lane.setOnDragDropped(dragEvent -> {
                    if (interactiveTask == null) {
                        return;
                    }
                    if (interactiveTask.getStory() != story) {
                        Alert differentStoryAlert = new Alert(Alert.AlertType.WARNING);
                        differentStoryAlert.setTitle("Unable to Move Task");
                        differentStoryAlert.setHeaderText(null);
                        differentStoryAlert.setContentText("Can't move a task between different stories");
                        differentStoryAlert.showAndWait();
                        return;
                    }



                    if (laneStateDict.get(lane).equals(Task.TASKSTATE.DONE)
                            && story.allTasksCompletedExcept(interactiveTask)) {
                        Alert markStoryDone = new Alert(Alert.AlertType.INFORMATION);

                        ButtonType yesButtonType = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                        ButtonType noButtonType = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
                        markStoryDone.getDialogPane().getButtonTypes().clear();
                        markStoryDone.getDialogPane().getButtonTypes().addAll(noButtonType, yesButtonType);

                        markStoryDone.setTitle("Completed Story");
                        markStoryDone.setHeaderText(null);
                        markStoryDone.setContentText("All tasks in the story have been completed, mark the story as "
                            + "completed too?");

                        Optional<ButtonType> result = markStoryDone.showAndWait();
                        if (result.get() == yesButtonType) {
                            // User chose yes
                            interactiveTask.editLane(laneStateDict.get(lane), hoverIndex, true);
                        }
                        else {
                            interactiveTask.editLane(laneStateDict.get(lane), hoverIndex, false);
                        }
                    }
                    else {
                        interactiveTask.editLane(laneStateDict.get(lane), hoverIndex, false);
                    }
                });
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
        Platform.runLater(() -> {
            SearchableText title = new SearchableTitle(currentSprint.getLongName());
            basicInfoPane.getChildren().add(title);


            // Create story title pane regions

            for (Story story : currentSprint.getStories().sorted(Story.StoryPriorityComparator)) {
                basicInfoPane.getChildren().add(getCollapsableStoryPane(story));
            }
            basicInfoPane.getChildren().add(getCollapsableStoryPane(currentSprint.getUnallocatedTasksStory()));


            // Add the searchable controls to the collection for searching
            Collections.addAll(searchControls, title);
        });
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "Scrumboard Tab";
    }
}
