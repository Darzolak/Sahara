package seng302.group2.scenes.information.project.backlog;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import seng302.group2.Global;
import seng302.group2.scenes.control.CustomComboBox;
import seng302.group2.scenes.control.FilteredListView;
import seng302.group2.scenes.control.search.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javafx.collections.FXCollections.observableArrayList;

/**
 * A tab for displaying the values of the various story estimation scales available in the workspace.
 * Created by drm127 on 13/07/15.
 */
public class StoryEstimationScaleInfoTab extends SearchableTab {

    List<SearchableControl> searchControls = new ArrayList<>();

    /**
     * Constructor for the StoryEstimationInfoTab class
     */
    public StoryEstimationScaleInfoTab() {
        construct();
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
        this.setText("Available Scales");
        Pane scaleInfoPane = new VBox(10);
        scaleInfoPane.setBorder(null);
        scaleInfoPane.setPadding(new Insets(25, 25, 25, 25));
        ScrollPane wrapper = new ScrollPane(scaleInfoPane);
        this.setContent(wrapper);

        SearchableText title = new SearchableTitle("Story Estimation Scales in "
                + Global.currentWorkspace.getShortName());
        SearchableText instructions = new SearchableText("Please select an estimation scale to view its values.");

        ObservableList<String> scaleOptions = observableArrayList();
        CustomComboBox<String> scaleComboBox = new CustomComboBox<>("Estimation Scales:");
        scaleComboBox.getComboBox().setItems(scaleOptions);

        scaleComboBox.setStyle("-fx-pref-width: 135;");
        //Label scaleComboLabel = new Label("Estimation Scales: ");
        //HBox scaleComboHBox = new HBox(scaleComboLabel);
        //HBox.setHgrow(scaleComboHBox, Priority.ALWAYS);
        //scaleComboHBox.setAlignment(Pos.CENTER_LEFT);
        //scaleComboHBox.getChildren().add(scaleComboBox);

        for (String scaleName : Global.currentWorkspace.getEstimationScales().getEstimationScaleDict().keySet()) {
            scaleOptions.add(scaleName);
        }

        ObservableList<String> scaleValues = observableArrayList();
        FilteredListView<String> scaleFilteredListView = new FilteredListView<>(scaleValues, "scales");
        SearchableListView<String> scaleValuesList = scaleFilteredListView.getListView();
        scaleValuesList.setPrefHeight(300);

        scaleComboBox.getComboBox().valueProperty().addListener((observable, oldValue, newValue) -> {
            scaleValues.clear();
            ArrayList<String> valueList = Global.currentWorkspace.getEstimationScales().
                    getEstimationScaleDict().get(newValue);

            for (String value : valueList) {
                scaleValues.add(value);
            }
        });

        scaleInfoPane.getChildren().add(title);
        scaleInfoPane.getChildren().add(instructions);
        scaleInfoPane.getChildren().add(scaleComboBox);
        scaleInfoPane.getChildren().add(scaleFilteredListView);

        Collections.addAll(searchControls, instructions, scaleComboBox, title, scaleFilteredListView);
    }
}
