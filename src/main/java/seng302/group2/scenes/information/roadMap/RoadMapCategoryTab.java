package seng302.group2.scenes.information.roadMap;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.control.search.SearchableTitle;
import seng302.group2.scenes.dialog.CreateReleaseDialog;
import seng302.group2.scenes.dialog.CreateSprintDialog;
import seng302.group2.scenes.dialog.CreateStoryDialog;
import seng302.group2.scenes.dialog.CustomDialog;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.roadMap.RoadMap;
import seng302.group2.workspace.workspace.Workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by cvs20 on 11/09/15.
 */
public class RoadMapCategoryTab extends SearchableTab {

    private List<SearchableControl> searchControls = new ArrayList<>();
    private Workspace currentWorkspace;
    private RoadMapNode interactiveRoadMap;
    private Release draggedRelease;
    private Sprint interactiveSprint;
    private Story interactiveStory;
    Boolean dragging = false;
    private Integer viewportWidth = 0;

    /**
     * Constructor for RoadMapCategoryTab class.
     * @param currentWorkspace The current workspace
     */
    public RoadMapCategoryTab(Workspace currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
        this.construct();

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
        // Tab Settings
        this.setText("RoadMaps");
        Pane categoryPane = new VBox(10);
        categoryPane.setBorder(null);
        categoryPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(categoryPane);
        this.setContent(wrapper);

        // Create Controls
        SearchableText title = new SearchableTitle("RoadMaps in " + currentWorkspace.getShortName());

        HBox roadMaps = new HBox();

        for (RoadMap roadMap : Global.currentWorkspace.getRoadMaps()) {
            RoadMapNode roadMapNode = new RoadMapNode(roadMap);

//            roadMapNode.setOnDragDropped(new EventHandler<DragEvent>() {
//                public void handle(DragEvent event) {
//                /* data dropped */
//                    System.out.println("onDragDropped");
//                /* if there is a string data on dragboard, read it and use it */
//                    Dragboard db = event.getDragboard();
//                    boolean success = false;
//                    if (db.hasString()) {
//                        for (RoadMap roadMap : currentWorkspace.getRoadMaps()) {
//                            if (db.getString() == roadMap.getShortName()) {
//                                roadMap.add(roadMapNode.getSelectedRelease());
//                            }
//
//                        }
//                        success = true;
//                    }
//                /* let the source know whether the string was successfully
//                 * transferred and used */
//                    event.setDropCompleted(success);
//
//                    event.consume();
//                }
//            });

            roadMaps.getChildren().add(roadMapNode);
            initRoadMapListeners(roadMapNode, roadMap);
            searchControls.addAll(roadMapNode.getSearchableControls());

        }


        HBox roadmapKeyBox = new HBox(8);
        Rectangle yellow = new Rectangle(250,25,20,20);
        yellow.setFill(Color.color(255 / 255, 0.5, 10 / 255, 0.62));
        yellow.setStrokeWidth(3);
        yellow.setArcWidth(10);
        yellow.setArcHeight(10);
        SearchableText yellowKeyLabel = new SearchableText("= Road Map");
        roadmapKeyBox.getChildren().addAll(yellow, yellowKeyLabel);

        HBox releaseKeyBox = new HBox(8);
        Rectangle blue = new Rectangle(250,25,20,20);
        blue.setFill(Color.color(11 / 255, 0, 255 / 255, 0.62));
        blue.setStrokeWidth(3);
        blue.setArcWidth(10);
        blue.setArcHeight(10);
        SearchableText blueKeyLabel = new SearchableText("= Releases");
        releaseKeyBox.getChildren().addAll(blue, blueKeyLabel);

        HBox sprintKeyBox = new HBox(8);
        Rectangle green = new Rectangle(250,25,20,20);
        green.setFill(Color.color(100 / 255, 255 / 255, 124 / 255, 0.83));
        green.setStrokeWidth(3);
        green.setArcWidth(10);
        green.setArcHeight(10);
        SearchableText greenKeyLabel = new SearchableText("= Sprints");
        sprintKeyBox.getChildren().addAll(green, greenKeyLabel);

        HBox storyKeyBox = new HBox(8);
        Rectangle red = new Rectangle(250,25,20,20);
        red.setFill(Color.color(255 / 255, 7 / 255, 0, 0.56));
        red.setStrokeWidth(3);
        red.setArcWidth(10);
        red.setArcHeight(10);
        SearchableText redKeyLabel = new SearchableText("= Stories");
        storyKeyBox.getChildren().addAll(red, redKeyLabel);

        Pane keyBox = new VBox(4);
        keyBox.getChildren().addAll(roadmapKeyBox, releaseKeyBox, sprintKeyBox, storyKeyBox);


        HBox btnBox = new HBox();
        btnBox.spacingProperty().setValue(10);
        Button btnCreateRelease = new Button("Create New Release");
        Button btnCreateSprint = new Button("Create New Sprint");
        Button btnCreateStory = new Button("Create New Story");


        btnCreateRelease.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateReleaseDialog(null);
            creationDialog.show();
        });
        btnCreateSprint.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateSprintDialog(null);
            creationDialog.show();
        });
        btnCreateStory.setOnAction((event) -> {
            javafx.scene.control.Dialog creationDialog = new CreateStoryDialog(null);
            creationDialog.show();
        });

        btnBox.getChildren().addAll(btnCreateRelease, btnCreateSprint, btnCreateStory);


        categoryPane.setOnDragOver(event -> {
                if (wrapper.getViewportBounds().getWidth() - event.getSceneX()
                        + App.mainPane.getTree().getWidth() <= 100) {
                    wrapper.setHvalue(wrapper.getHvalue() + 0.01);
                    viewportWidth = new Integer((int) wrapper.getHvalue());
                }
                else if (event.getSceneX() - App.mainPane.getTree().getWidth() <= 100) {
                    wrapper.setHvalue(wrapper.getHvalue() - 0.01);
                    viewportWidth = new Integer((int) wrapper.getHvalue());
                }
            });


        categoryPane.setOnDragDropped(event -> {
            wrapper.setHvalue(viewportWidth);
        });
/*

            //System.out.println(event.getX() + " " + event.getY() + " " + categoryPane.getBoundsInParent()
             //       .getMinX() + " " + categoryPane.getParent().getParent().getBoundsInParent().getMaxX());
            if (dragging) {
             //   System.out.println(event.getX() + " " + event.getY() + " " + categoryPane.getBoundsInParent()
               //         .getMinX() + " " + categoryPane.getParent().getParent().getBoundsInParent().getMaxX());
                if (categoryPane.getParent().getParent().getBoundsInParent().getMaxX() - event.getX() < 100) {
                    for (Node node : wrapper.getChildrenUnmodifiable()) {
                        if (node instanceof ScrollBar) {
                            ScrollBar scrollBar = (ScrollBar) node;
                            if (scrollBar.getOrientation().equals(Orientation.HORIZONTAL)) {
                                scrollBar.increment();
                            }
                        }
                    }
                }
            }
        });
*/

//        categoryPane.setOnDragOver(event -> {
//            System.out.println(event.getX() + " " + event.getY() + " " + categoryPane.getBoundsInParent()
//                    .getMinX() + " " + categoryPane.getParent().getParent().getBoundsInParent().getMaxX());
//            if (dragging) {
//                System.out.println(event.getX() + " " + event.getY() + " " + categoryPane.getBoundsInParent()
//                        .getMinX() + " " + categoryPane.getParent().getParent().getBoundsInParent().getMaxX());
//                if (categoryPane.getParent().getParent().getBoundsInParent().getMaxX() - event.getX() < 100) {
//                    for (Node node : wrapper.getChildrenUnmodifiable()) {
//                        if (node instanceof ScrollBar) {
//                            ScrollBar scrollBar = (ScrollBar) node;
//                            if (scrollBar.getOrientation().equals(Orientation.HORIZONTAL)) {
//                                Platform.runLater();
//                                scrollBar.increment();
//                            }
//                        }
//                    }
//                }
//            }
//        });



        // Add items to pane & search collection
        categoryPane.getChildren().addAll(
                title,
                roadMaps,
                keyBox,
                btnBox
        );

        Collections.addAll(
                searchControls,
                title
        );
    }

    private void initRoadMapListeners(RoadMapNode roadMapNode, RoadMap currentRoadMap) {
        roadMapNode.setOnDragDetected(event -> {
            interactiveRoadMap = roadMapNode;
            draggedRelease = roadMapNode.getSelectedRelease();
            dragging = true;

        });

        roadMapNode.setOnDragOver(dragEvent -> {
            dragEvent.acceptTransferModes(TransferMode.MOVE);
            roadMapNode.setSelectedSprint(interactiveRoadMap.getSelectedSprint());
            roadMapNode.setSelectedStory(interactiveRoadMap.getSelectedStory());
        });

        roadMapNode.setOnDragDropped(dragEvent -> {
            dragging = false;
            Boolean confirm = true;
            if (dragEvent.getDragboard().getString() == "release") {
                for (Release release : currentRoadMap.getReleases() ) {
                    if (release == draggedRelease && !currentRoadMap.equals(
                            interactiveRoadMap.getRoadmap())) {
                        dragEvent.consume();
                        confirm = false;
                        CustomDialog.showDialog("Cannot Move Release"
                                , "You cannot have double up releases in a RoadMap", Alert.AlertType.WARNING);
                    }
                }
                if (confirm) {
                    interactiveRoadMap.getRoadmap().addRemove(currentRoadMap,
                            interactiveRoadMap.getRoadmap(), draggedRelease);
                }

                App.mainPane.refreshAll();
            }
        });


    }

    public Release getSelectedRelease() {
        return draggedRelease;
    }

    public void setSelectedRelease(Release release) {
        draggedRelease = release;
    }

    public Sprint getSelectedSprint() {
        return interactiveSprint;
    }

    public void setSelectedSprint(Sprint sprint) {
        interactiveSprint = sprint;
    }

    public Story getSelectedStory() {
        return interactiveStory;
    }

    public void setSelectedStory(Story story) {
        interactiveStory = story;
    }
}

