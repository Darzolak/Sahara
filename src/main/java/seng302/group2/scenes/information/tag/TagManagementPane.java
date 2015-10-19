package seng302.group2.scenes.information.tag;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;
import org.controlsfx.control.PopOver;
import seng302.group2.Global;
import seng302.group2.scenes.control.FilteredListView;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.control.search.SearchableTitle;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.workspace.tag.Tag;

import java.util.HashSet;
import java.util.Set;

/**
 * A Pane for the creation and management of Tags.
 * Created by drm127 on 14/09/15.
 */
public class TagManagementPane extends SplitPane {

    private PopOver popOver;
    Set<SearchableControl> searchControls = new HashSet<>();

    public ListView<Tag> tagListView;
    ObservableList<Tag> tagList = Global.currentWorkspace.getAllTags();

    VBox detailsPane = new VBox(8);
    VBox listPane = new VBox(8);

    public TextField newTagField = new TextField();


    /**
     * Public constructor for the Tag management pane.
     * @param po The pop over to contain the pane.
     */
    public TagManagementPane(PopOver po) {
        this.setPrefSize(665, 400);
        this.setDividerPositions(0.5);
        this.popOver = po;
        construct();
        Platform.runLater(newTagField::requestFocus);
    }

    /**
     * Calls the constructor for the left side list view, and the right side detail or placeholder panes.
     */
    private void construct() {
        this.getChildren().clear();
        this.constructList();
        if (tagListView.getItems().size() > 0) {
            this.constructInfo();
            tagListView.getSelectionModel().select(0);
        }
        else {
            this.constructPlaceholder();
        }
    }

    /**
     * If no tag is selected, shows a blank pane with placeholder text.
     */
    private void constructPlaceholder() {
        this.getItems().remove(detailsPane);
        detailsPane = new VBox(8);
        detailsPane.setPadding(new Insets(8));
        detailsPane.setMaxWidth(357);

        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(messageBox, Priority.ALWAYS);
        VBox.setVgrow(detailsPane, Priority.ALWAYS);

        Text message = new Text("No tag selected");
        message.setStyle("-fx-font-weight: bold");
        message.setTextAlignment(TextAlignment.CENTER);
        messageBox.getChildren().add(message);
        detailsPane.getChildren().add(messageBox);

        this.getItems().add(detailsPane);
    }

    /**
     * Creates the list view for the tags in the workspace, and the text field and button for easy creation.
     */
    private void constructList() {
        this.getItems().remove(listPane);
        listPane = new VBox(8);
        listPane.setPadding(new Insets(8));
        listPane.setMaxWidth(292);

        FilteredListView<Tag> tagFilteredListView = new FilteredListView<>(tagList, "tags");
        tagListView = tagFilteredListView.getListView();
        tagListView.setPrefHeight(584);

        this.tagListView.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {
            @Override
            public ListCell<Tag> call(ListView<Tag> param) {
                return new TagListCell(popOver);
            }
        });

        tagListView.getSelectionModel().getSelectedItems().addListener(
                (ListChangeListener<Tag>) change -> {
                    if (tagListView.getItems().size() > 0) {
                        constructInfo();
                    }
                });

        // Sort here if needed
        if (tagListView.getItems().size() > 0) {
            tagListView.getSelectionModel().select(0);
        }

        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER_LEFT);
        labelBox.getChildren().add(new Text("New Tag"));

        newTagField = new TextField();

        newTagField.setPromptText("eg. Buggy");
        Button addNewTagButton = new Button("Create");
        addNewTagButton.setMinWidth(64);
        addNewTagButton.setPrefWidth(64);
        HBox newTagBox = new HBox(8);
        HBox.setHgrow(newTagField, Priority.ALWAYS);
        newTagBox.getChildren().addAll(labelBox, newTagField, addNewTagButton);

        addNewTagButton.setOnAction(event -> {
            newTagField.setText(newTagField.getText().trim());

            if (newTagField.getText().isEmpty()) {
                event.consume();
                return;
            }

            for (Tag tag : Global.currentWorkspace.getAllTags()) {
                if (tag.getName().equals(newTagField.getText())) {
                    tagListView.getSelectionModel().select(tag);
                    event.consume();
                    return;
                }
            }

            Tag newTag = new Tag(newTagField.getText());
            Global.currentWorkspace.add(newTag);

            newTagField.clear();
            this.constructList();
            tagListView.getSelectionModel().select(newTag);
        });

        newTagField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newTagField.getText().isEmpty() && newTagField.getText().length() >= 2
                        && (newTagField.getText().endsWith(" "))) {
                    newTagField.setText(newTagField.getText().substring(0, newTagField.getText().length() - 1) + "_");
                }
                if (newValue.length() > 20) {
                    newTagField.setText(oldValue);
                    ValidationStyle.borderGlowRed(newTagField);
                    ValidationStyle.showMessage("A tag must be 20 characters or fewer", newTagField);
                    addNewTagButton.setDisable(true);
                }
                else {
                    ValidationStyle.borderGlowNone(newTagField);
                    addNewTagButton.setDisable(false);
                }
            });

        listPane.getChildren().addAll(tagFilteredListView, newTagBox);

        this.getItems().add(0, listPane);

        Platform.runLater(newTagField::requestFocus);

    }

    /**
     * Creates an info pane for a tag, displays the tag, name and colour value.
     */
    private void constructInfo() {
        this.getItems().remove(detailsPane);
        detailsPane = new VBox(8);
        detailsPane.setPadding(new Insets(8));
        detailsPane.setMaxWidth(357);

        // Find the selected tag and create its tag node
        Tag selectedTag = tagListView.getSelectionModel().getSelectedItem();

        //HBox titleBox = new HBox();
        SearchableTitle title = new SearchableTitle("View Tag:", searchControls);
        detailsPane.getChildren().add(title);

        SearchableText preview = new SearchableText("Preview:", searchControls);
        preview.injectStyle("-fx-font-weight: bold");
        detailsPane.getChildren().add(preview);

        HBox cellBox = new HBox();
        cellBox.setPrefHeight(100);
        cellBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(cellBox, Priority.ALWAYS);

        if (selectedTag == null) {
            return;
        }
        TagCellNode cellNode = new TagCellNode(selectedTag, false);
        cellNode.setAlignment(Pos.CENTER);
        cellBox.getChildren().add(cellNode);
        detailsPane.getChildren().add(cellBox);

        Button editButton = new Button("Edit");

        HBox buttonsBox = new HBox(8);
        VBox.setVgrow(buttonsBox, Priority.ALWAYS);
        buttonsBox.getChildren().addAll(editButton);
        detailsPane.getChildren().addAll(buttonsBox);

        buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);

        editButton.setOnAction(event -> constructEdit());

        this.getItems().add(detailsPane);


    }


    /**
     * Creates the detail pane for editing tags in the workspace. Contains controls for editing the text and colour
     * of a tag, and deleting the tag.
     */
    private void constructEdit() {
        this.getItems().remove(detailsPane);
        detailsPane = new VBox(8);
        detailsPane.setPadding(new Insets(8));
        detailsPane.setMaxWidth(357);

        // Find the selected tag and create its tag node
        Tag selectedTag = tagListView.getSelectionModel().getSelectedItem();

        //HBox titleBox = new HBox();
        SearchableTitle title = new SearchableTitle("Edit Tag:", searchControls);
        detailsPane.getChildren().add(title);

        SearchableText preview = new SearchableText("Preview:", searchControls);
        preview.injectStyle("-fx-font-weight: bold");
        detailsPane.getChildren().add(preview);

        HBox cellBox = new HBox();
        cellBox.setPrefHeight(175);
        cellBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(cellBox, Priority.ALWAYS);

        if (selectedTag == null) {
            return;
        }
        TagCellNode cellNode = new TagCellNode(selectedTag, false);
        cellNode.setAlignment(Pos.CENTER);
        cellBox.getChildren().add(cellNode);
        detailsPane.getChildren().add(cellBox);

        // Create the editable option controls
        RequiredField tagNameField = new RequiredField("Tag Name:", searchControls);
        tagNameField.getTextField().setPromptText("fix");
        tagNameField.setText(selectedTag.getName());
        tagNameField.getTextField().addEventFilter(Event.ANY, event -> {
                // @Dave Update the displayed cell node with the name
                cellNode.setName(tagNameField.getText());
            });

        HBox colorBox = new HBox();
        HBox labelBox = new HBox();

        SearchableText tagColorLabel = new SearchableText("Tag Colour:", searchControls);
        tagColorLabel.injectStyle("-fx-font-weight: bold");
        labelBox.getChildren().add(tagColorLabel);
        labelBox.setAlignment(Pos.BOTTOM_LEFT);
        labelBox.spacingProperty().setValue(0);
        HBox.setHgrow(labelBox, Priority.ALWAYS);
        VBox.setVgrow(labelBox, Priority.ALWAYS);
        ColorPicker colorPicker = new ColorPicker(selectedTag.getColor());
        colorPicker.setPrefWidth(168);
        colorPicker.setOnAction(event -> {
                cellNode.setColor(colorPicker.getValue());
            });
        colorBox.getChildren().addAll(labelBox, colorPicker);

        // Group all of the controls into the details pane
        detailsPane.getChildren().addAll(tagNameField, colorBox);

        // Create buttons
        Button saveButton = new Button("Done");
        Button cancelButton = new Button("Cancel");

        tagNameField.getTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!tagNameField.getTextWithoutTrim().isEmpty() && tagNameField.getTextWithoutTrim().length() >= 2
                    && (tagNameField.getTextWithoutTrim().endsWith(" "))) {
                tagNameField.setText(tagNameField.getTextWithoutTrim().substring(
                        0, tagNameField.getTextWithoutTrim().length() - 1) + "_");
            }
            if (newValue.length() > 20) {
                tagNameField.getTextField().setText(oldValue);
                ValidationStyle.borderGlowRed(tagNameField.getTextField());
                ValidationStyle.showMessage("A tag must be 20 characters or fewer", tagNameField.getTextField());
                saveButton.setDisable(true);
            }
            else {
                ValidationStyle.borderGlowNone(tagNameField.getTextField());
                saveButton.setDisable(false);
            }
        });

        saveButton.setOnAction(event -> {
                // @Dave create and execute edit
                boolean nameUnchanged = tagNameField.getText().equals(selectedTag.getName());
                boolean colorUnchanged = colorPicker.getValue() == (selectedTag.getColor());

                if (nameUnchanged && colorUnchanged) {
                    event.consume();
                }

                selectedTag.edit(tagNameField.getText(), colorPicker.getValue());

                constructList();
                tagListView.getSelectionModel().select(selectedTag);
                constructInfo();
            });

        cancelButton.setOnAction(event -> constructInfo());


        // Commented as delete button no longer exists. Have kept the code for the popover logic involved,
        // in case we need to reuse somewhere else in the project.
//        deleteButton.setOnAction(event -> {
//            if (popOver != null) {
//                Node parent = popOver.getOwnerNode();
//                double x = popOver.getX();
//                double y = popOver.getY();
//                popOver.hide();
//                showDeleteDialog(selectedTag);
//                popOver.setDetached(true);
//                Platform.runLater(() -> {
//                    popOver.show(parent, x, y);
//                    TagManagementPane managementPane = (TagManagementPane) popOver.getContentNode();
//
//                    Tag tag = managementPane.tagListView.getSelectionModel().getSelectedItem();
//
//                    managementPane.construct();
//
//                    if ((managementPane).tagListView.getItems().contains(tag)) {
//                        (managementPane).tagListView.getSelectionModel().select(tag);
//                    }
//                });
//            }
//            else {
//                showDeleteDialog(selectedTag);
//            }
//        });

        HBox deleteBox = new HBox();
        deleteBox.setAlignment(Pos.CENTER_RIGHT);


        HBox buttonsBox = new HBox(8);
        VBox.setVgrow(buttonsBox, Priority.ALWAYS);
        buttonsBox.getChildren().addAll(saveButton, cancelButton);
        detailsPane.getChildren().addAll(deleteBox, buttonsBox);

        buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);


        this.getItems().add(detailsPane);
    }
}
