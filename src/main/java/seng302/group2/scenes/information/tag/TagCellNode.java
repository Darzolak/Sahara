package seng302.group2.scenes.information.tag;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import seng302.group2.scenes.control.Tooltip;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.control.search.TagField;
import seng302.group2.workspace.tag.Tag;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The content of a Tag cell as shown on the Tag management pane.
 * Created by drm127 on 14/09/15.
 */
public class TagCellNode extends VBox implements SearchableControl {

    private Tag tag = null;
    private String tagName = "";
    private Color tagColor = Color.ROYALBLUE;
    private boolean removable;
    private TagField tagField = null;
    SearchableText titleLabel;

    private Set<SearchableControl> searchControls = new HashSet<>();


    /**
     * Constructor for a tag cell node.
     * @param tag The tag to display.
     * @param removable if the cell node displays the x icon
     */
    public TagCellNode(Tag tag, boolean removable) {
        this.tag = tag;
        this.tagName = tag.getName();
        this.tagColor = tag.getColor();
        this.removable = removable;

        construct();
    }

    /**
     * Constructor for a tag cell node. Adds to the given list of searchable controls
     * @param tag The tag to display.
     * @param removable if the cell node displays the x icon
     * @param searchableControls Collection of searchable controls
     */
    public TagCellNode(Tag tag, boolean removable, Collection<SearchableControl> searchableControls) {
        this.tag = tag;
        this.tagName = tag.getName();
        this.tagColor = tag.getColor();
        this.removable = removable;

        searchableControls.add(this);

        construct();
    }


    /**
     * Constructor for a tag cell node. Adds to the given list of searchable controls
     *
     * @param tag The tag to display.
     * @param removable if the cell node displays the x icon
     * @param tagField A containing tag field
     * @param searchableControls Collection of searchable controls
     */
    public TagCellNode(Tag tag, boolean removable,
                       TagField tagField, Collection<SearchableControl> searchableControls) {
        this.tag = tag;
        this.tagName = tag.getName();
        this.tagColor = tag.getColor();
        this.removable = removable;
        this.tagField = tagField;

        searchableControls.add(this);

        construct();
    }


    /**
     * Creates the cell node, including text, size and background colour.
     */
    void construct() {
        this.getChildren().clear();

        HBox content = new HBox();
        Insets insetsContent = new Insets(2, 2, 2, 2);
        content.setPadding(insetsContent);
        content.setBackground(new Background(new BackgroundFill(tagColor, new CornerRadii(4), Insets.EMPTY)));

        VBox textContent = new VBox();
        textContent.setPadding(new Insets(2, 4, 2, 4));
        textContent.setAlignment(Pos.CENTER_LEFT);

        titleLabel = new SearchableText(" " + tagName, "-fx-font-weight: bold;", searchControls);
        // A transparent text to keep the width of the tag equal to the text
        Text titleLabelShadow = new Text(" " + tagName + " ");
        titleLabelShadow.setFill(Color.TRANSPARENT);
        titleLabelShadow.setStyle("-fx-font-weight: bold;");
        GridPane titleGrid = new GridPane();
        titleGrid.add(titleLabel, 0, 0);
        titleGrid.add(titleLabelShadow, 0, 0);

        if (tagColor.getBrightness() < 0.9) {
            for (Text text : titleLabel.getTexts()) {
                text.setFill(Color.WHITE);
            }
            titleLabel.injectStyle("-fx-text-fill: white;");
        }

        textContent.getChildren().addAll(titleGrid);

        if (removable) {
            content.getChildren().addAll(textContent, createDeletionNode(tag));
        }
        else {
            content.getChildren().addAll(textContent);
        }

        this.getChildren().add(content);
    }


    /**
     * Sets the name of the cell.
     * @param newName The new name to display
     */
    public void setName(String newName) {
        this.tagName = newName;
        construct();
    }

    /**
     * Sets the color of the cell.
     * @param newColor The new color to show
     */
    public void setColor(Color newColor) {
        this.tagColor = newColor;
        construct();
    }

    /**
     * Gets the tag that the cell node is based on
     * @return The cell node's associated tag
     */
    public Tag getTag() {
        return this.tag;
    }

    /**
     * Creates a deletion node
     * @param tag The tag to be deleted
     */
    public Node createDeletionNode(Tag tag) {

        VBox deletionNode = new VBox();

        ImageView deletionImage = new ImageView("icons/tag_remove_black.png");

        if (tagColor.getBrightness() < 0.9) {
            deletionImage = new ImageView("icons/tag_remove_white.png");
        }

        Tooltip.create("Remove", deletionImage, 50);

        deletionNode.setOnMouseEntered(me -> {
            this.getScene().setCursor(Cursor.HAND); //Change cursor to hand
        });
        deletionNode.setOnMouseExited(me -> {
            this.getScene().setCursor(Cursor.DEFAULT); //Change cursor to hand
        });

        deletionImage.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (this.tagField != null) {
                this.tagField.getTags().remove(tag);
                this.tagField.update();
            }

            event.consume();
        });


        deletionNode.getChildren().add(deletionImage);
        VBox.setVgrow(deletionNode, Priority.ALWAYS);
        deletionNode.setAlignment(Pos.CENTER);

        return deletionNode;
    }

    @Override
    public boolean query(String query) {
        boolean found = false;
        for (SearchableControl control : searchControls) {
            found = control.query(query) || found;
        }

        if (titleLabel != null) {
            if (tagColor.getBrightness() < 0.9) {
                for (Text text : titleLabel.getTexts()) {
                    text.setFill(Color.WHITE);
                }
            }
        }

        return found;
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        if (tagName.trim().toLowerCase().contains(query.trim().toLowerCase())) {
            return 1;
        }
        return 0;
    }
}