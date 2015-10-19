package seng302.group2.scenes.information.tag;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.PopOver;
import seng302.group2.App;
import seng302.group2.scenes.control.Tooltip;
import seng302.group2.scenes.menu.MainMenuBar;
import seng302.group2.workspace.tag.Tag;

import static seng302.group2.scenes.dialog.DeleteDialog.showDeleteDialog;

/**
 * A custom list cell for the tags
 * Created by Bronson Laptop on 20/09/2015.
 */
public class TagListCell extends ListCell<Tag> {
    HBox cell = new HBox(10);
    HBox deleteCell = new HBox(0);
    PopOver popOver;

    public TagListCell(PopOver popover) {
        this.popOver = popover;
    }

    @Override
    protected void updateItem(Tag tagItem, boolean bln) {
        super.updateItem(tagItem, bln);
        if (tagItem != null) {
            cell = new HBox(10);
            deleteCell.setAlignment(Pos.BASELINE_RIGHT);
            Node deletionNode = createDeletionNode(tagItem);
            Label tagName = new Label(tagItem.getName());
            deleteCell.getChildren().clear();
            deleteCell.getChildren().add(deletionNode);
            HBox.setHgrow(deleteCell, Priority.ALWAYS);
            cell.getChildren().addAll(tagName, deleteCell);
            setGraphic(cell);
        }
        else {
            setGraphic(null);
        }
    }

    /**
     * Creates a deletion node, which is used to delete a tag.
     * @param tag The tag to be deleted
     * return A deletion node
     */
    public Node createDeletionNode(Tag tag) {
        ImageView deletionImage = new ImageView("icons/trashcan.png");
        Tooltip.create("Delete Tag", deletionImage, 50);

        deletionImage.setOnMouseEntered(me -> {
            this.getScene().setCursor(Cursor.HAND); //Change cursor to hand
        });
        deletionImage.setOnMouseExited(me -> {
            this.getScene().setCursor(Cursor.DEFAULT); //Change cursor to hand
        });

        deletionImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (popOver != null) {
                popOver.hide();
                showDeleteDialog(tag);
                popOver.setDetached(true);
                Platform.runLater(() -> {
                    MainMenuBar.showTaggingPopOver(tag);
                    /*PopOver taggingPopOver = new PopOver();
                    taggingPopOver.setDetachedTitle("Tag Management");
                    taggingPopOver.setDetached(true);
                    TagManagementPane tagManagementPane = new TagManagementPane(taggingPopOver);
                    taggingPopOver.setContentNode(tagManagementPane);
                    Platform.runLater(() -> taggingPopOver.show(App.content, App.mainStage.getX()
                                    + App.mainStage.getWidth() / 2 - 300,
                            App.mainStage.getY() + App.mainStage.getHeight() / 2 - 200));
                        tagManagementPane.newTagField.requestFocus();
                        if (tag != null) {
                            tagManagementPane.tagListView.getSelectionModel().select(tag);
                        }*/
                    });
            }
            else {
                showDeleteDialog(tag);
            }
        });
        return deletionImage;
    }

}
