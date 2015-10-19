package seng302.group2.scenes;

//import com.sun.org.apache.xpath.internal.SourceTree;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import seng302.group2.App;
import seng302.group2.Global;
import seng302.group2.scenes.control.TrackedTabPane;
import seng302.group2.scenes.control.search.SearchResultCellNode;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableTab;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.dialog.CreateSearchPopOver;
import seng302.group2.workspace.SaharaItem;

import java.util.List;

/**
 * A window for displaying the results of a search.
 * Created by drm127 on 4/09/15.
 */
public class SearchResultPane extends PopOver {


    /**
     * Constructor
     *
     * @param results    The list of Search Results.
     * @param searchText The text being searched for
     * @param searchType The type of the search
     */
    public SearchResultPane(List<SearchResultCellNode> results, String searchText, SearchType searchType) {
        Global.advancedSearchExists = true;
        this.setDetachedTitle("Search Results");
        this.setMinWidth(500);
        this.setMinHeight(500);
        VBox content = new VBox();

        SearchableText resultsFound;
        if (results.size() != 1) {
            resultsFound = new SearchableText("Found " + results.size() + " Occurrences of " + searchText);
        }
        else {
            resultsFound = new SearchableText("Found " + results.size() + " Occurrence of " + searchText);
        }

        Insets insets = new Insets(10, 20, 10, 20);
        resultsFound.setPadding(insets);
        ListView<SearchResultCellNode> resultView = new ListView<>();
        resultView.setPrefHeight(490);
        resultView.setPrefWidth(490);
        for (SearchResultCellNode result : results) {
            resultView.getItems().add(result);
        }

        resultView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {

                SaharaItem selectedItem = (SaharaItem) resultView.getSelectionModel().getSelectedItem().getItem();
                App.mainPane.selectItem(selectedItem);
                Tab selectedTab = (SearchableTab) resultView.getSelectionModel().getSelectedItem().getTab();
                ((TrackedTabPane) resultView.getSelectionModel().getSelectedItem().getSearchableScene())
                        .select(selectedTab);
                selectedItem.switchToInfoScene();
                if (searchType == SearchType.NORMAL) {
                    //System.out.println("Into the Highlighting");
                    MainPane.getToolBar().search(searchText);
                }
            }
            event.consume();
        });

        HBox buttons = new HBox();
        Button btnClose = new Button("Close");
        Button btnBack = new Button("Back");
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.spacingProperty().setValue(10);
        buttons.setPadding(insets);
        buttons.getChildren().addAll(btnBack, btnClose);

        content.getChildren().addAll(resultsFound, resultView, buttons);

        btnClose.setOnAction(event -> {
            this.hide();
            Global.advancedSearchExists = false;
        });

        btnBack.setOnAction(event -> {
            this.hide();
            PopOver searchPopOver = new CreateSearchPopOver();
            searchPopOver.show(App.mainStage);
        });

//        this.setOnHiding(event -> {
//                MainPane.getToolBar().search("");
//                Global.advancedSearchExists = false;
//
//            });

        this.setContentNode(content);
        this.setDetached(true);

    }


}
