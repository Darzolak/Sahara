package seng302.group2.scenes.information.roadMap;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import seng302.group2.App;
import seng302.group2.scenes.control.CustomInfoLabel;
import seng302.group2.scenes.control.search.*;
import seng302.group2.scenes.dialog.CreateReleaseDialog;
import seng302.group2.scenes.dialog.CreateSprintDialog;
import seng302.group2.scenes.dialog.CreateStoryDialog;
import seng302.group2.workspace.roadMap.RoadMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The RoadMap information tab
 * Created by cvs20 on 11/09/15.
 */
public class RoadMapInfoTab extends SearchableTab {
    private List<SearchableControl> searchControls = new ArrayList<>();
    private RoadMap currentRoadMap;
    int viewportWidth = 0;

    /**
     * Constructor for the RoadMapInfoTab class.
     * @param currentRoadMap the current roadmap for which information will be displayed
     */
    public RoadMapInfoTab(RoadMap currentRoadMap) {
        this.currentRoadMap = currentRoadMap;
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
        this.setText("Basic Information");
        Pane basicInfoPane = new VBox(10);
        basicInfoPane.setBorder(null);
        basicInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(basicInfoPane);
        this.setContent(wrapper);

        // Create controls
        SearchableText title = new SearchableTitle(currentRoadMap.getShortName(), searchControls);
        TagLabel roadMapTags = new TagLabel(currentRoadMap.getTags());

        Separator separator = new Separator();

        CustomInfoLabel currentTeamsLabel = new CustomInfoLabel("Current Teams:", "");
        CustomInfoLabel releasesLabel = new CustomInfoLabel("Releases:", "");

        RoadMapNode roadMapNode = new RoadMapNode(currentRoadMap);


        HBox roadmapKeyBox = new HBox(8);
        Rectangle yellow = new Rectangle(250,25,20,20);
        yellow.setFill(Color.color(1, 0.5, 10 / 255, 0.62));
        yellow.setStrokeWidth(3);
        yellow.setArcWidth(10);
        yellow.setArcHeight(10);
        SearchableText yellowKeyLabel = new SearchableText("= Road Map");
        roadmapKeyBox.getChildren().addAll(yellow, yellowKeyLabel);

        HBox releaseKeyBox = new HBox(8);
        Rectangle blue = new Rectangle(250,25,20,20);
        blue.setFill(Color.color(11 / 255, 0, 1, 0.62));
        blue.setStrokeWidth(3);
        blue.setArcWidth(10);
        blue.setArcHeight(10);
        SearchableText blueKeyLabel = new SearchableText("= Releases");
        releaseKeyBox.getChildren().addAll(blue, blueKeyLabel);

        HBox sprintKeyBox = new HBox(8);
        Rectangle green = new Rectangle(250,25,20,20);
        green.setFill(Color.color(100 / 255, 1, 124 / 255, 0.83));
        green.setStrokeWidth(3);
        green.setArcWidth(10);
        green.setArcHeight(10);
        SearchableText greenKeyLabel = new SearchableText("= Sprints");
        sprintKeyBox.getChildren().addAll(green, greenKeyLabel);

        HBox storyKeyBox = new HBox(8);
        Rectangle red = new Rectangle(250,25,20,20);
        red.setFill(Color.color(1, 7 / 255, 0, 0.56));
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

        basicInfoPane.setOnDragOver(event -> {
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

        btnBox.getChildren().addAll(btnCreateRelease, btnCreateSprint, btnCreateStory);

        // Add items to pane & search collection
        basicInfoPane.getChildren().addAll(
                title,
                roadMapTags,
                roadMapNode,
                keyBox,
                separator

        );

        Collections.addAll(searchControls,
                title,
                roadMapTags,
                roadMapNode,
                currentTeamsLabel,
                releasesLabel
        );
    }

    /**
     * Gets the string representation of the current Tab
     * @return The String value
     */
    @Override
    public String toString() {
        return "RoadMap Info Tab";
    }

    /**
     * Switches to the edit scene
     */
    public void edit() {
        currentRoadMap.switchToInfoScene(true);
    }

}
