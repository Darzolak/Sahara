package seng302.group2.scenes.control.search;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Collection;

/**
 * A class to display a searchable check box.
 * Created by btm38 on 19/08/15.
 */
public class SearchableCheckBox extends VBox implements SearchableControl {
    private SearchableText cbLabel = new SearchableText();
    private CheckBox cb = new CheckBox();

    /**
     * Constructor for a SearchableCheckBox that takes a label (as a String).
     * @param label string used for the checkbox label
     */
    public SearchableCheckBox(String label) {
        HBox box = new HBox();
        box.setSpacing(2);
        box.setPrefWidth(165);
        box.setAlignment(Pos.CENTER_LEFT);
        box.spacingProperty().setValue(0);

        cbLabel.setPadding(new Insets(0, 85, 0, 0));
        cbLabel.setText(label);
        cbLabel.setStyle("-fx-font-weight: bold");


        box.getChildren().addAll(cbLabel, cb);

        this.getChildren().add(box);

    }

    /**
     * Constructor for a SearchableCheckBox that takes a label (as a String) and the searchableControls.
     * @param label string used for the checkbox label
     * @param searchableControls A collection of searchable controls to add this control to
     */
    public SearchableCheckBox(String label, Collection<SearchableControl> searchableControls) {
        HBox box = new HBox();
        box.setPrefWidth(165);
        box.setAlignment(Pos.CENTER_LEFT);
        box.spacingProperty().setValue(0);

        cbLabel.setText(label);
        cbLabel.setStyle("-fx-font-weight: bold");
        box.getChildren().addAll(cbLabel, cb);

        this.getChildren().add(box);
        searchableControls.add(this);
    }

    /**
     * Gets the CheckBox out of this class.
     * @return the CheckBox.
     */
    public CheckBox getCheckBox() {
        return this.cb;
    }

    /**
     * Queries the contents of the checkbox label.
     *
     * @param query The search string to query the list contents with
     * @return If a match was found for the query
     */
    @Override
    public boolean query(String query) {
        return cbLabel.query(query);
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        return 0;
    }

}


