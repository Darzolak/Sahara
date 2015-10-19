package seng302.group2.scenes.information.project.sprint;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import seng302.group2.App;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.information.project.story.StoryScene;
import seng302.group2.workspace.project.story.tasks.Task;

import java.util.HashSet;
import java.util.Set;

/**
 * A ListCell extension for the neat displaying of tasks on the scrum board view
 * Created by Jordane on 23/07/2015.
 */
public class ScrumBoardTaskCell extends ListCell<Task> implements SearchableControl {

    ListView<Task> parentTable = null;
    Task interactiveTask = null;
    ScrumboardTab tab = null;

    Set<SearchableControl> searchControls = new HashSet<>();

    /**
     * Creates a scrumboard task cell for the given parent table for sharing drag events, and the tab to make the fields
     * searchable.
     * @param parentTable The table that the cell belongs to
     * @param tab The scrumboard tab containing the table and cell
     */
    public ScrumBoardTaskCell(ListView<Task> parentTable, ScrumboardTab tab) {
        this.parentTable = parentTable;
        this.tab = tab;
        tab.searchControls.add(this);
    }


    /**
     * Updates the item, resetting it's graphic node, and reinitialising it's listeners for dragging and dropping
     * @param task The cell's task
     * @param empty Whether the cell is empty of not
     */
    @Override
    public void updateItem(Task task, boolean empty) {
        super.updateItem(task, empty);
        if (task != null) {
            // Setup the cell graphic (HBox)
            setGraphic(new ScrumBoardTaskCellNode(task));
        }
        else {
            //System.out.println("Item is null");
            setGraphic(null);
        }

        this.setOnDragDetected(event -> {
                interactiveTask = this.getItem();
                Dragboard dragBoard = this.startDragAndDrop(TransferMode.MOVE);
                dragBoard.setDragView(this.snapshot(null, null));
                ClipboardContent content = new ClipboardContent();
                content.putString("");
                dragBoard.setContent(content);
            });

        this.setOnDragOver(event -> {
                int dropIndex;

                if (this.isEmpty()) {
                    dropIndex = this.parentTable.getItems().size() - 1;
                }
                else {
                    dropIndex = this.getIndex();
                }

                //parentTable.getItems().add(dropIndex, interactiveTask);
                tab.hoverIndex = dropIndex;
            });

        this.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                App.mainPane.selectItem(task.getStory());

                ((StoryScene) App.mainPane.getContent()).select(((StoryScene) App.mainPane.getContent()).getTaskTab());
                task.getStory().switchToInfoScene();
            }
            event.consume();
        });
        setTextOverrun(OverrunStyle.CLIP);
    }

    @Override
    public boolean query(String query) {
        boolean result = false;
        for (SearchableControl control : searchControls) {
            if (control.query(query)) {
                result = true;
            }
        }
        return result;
    }

    //TODO Advanced search query for this class.
    @Override
    public int advancedQuery(String query, SearchType searchType) {
        return 0;
    }

}