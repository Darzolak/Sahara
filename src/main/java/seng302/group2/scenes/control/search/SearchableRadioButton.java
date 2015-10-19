package seng302.group2.scenes.control.search;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Collection;

/**
 * A class to display a searchable radio button.
 * Created by cvs20 on 6/09/15.
 */
public class SearchableRadioButton extends VBox implements SearchableControl {
    private SearchableText rbLabel = new SearchableText();
    private RadioButton rb = new RadioButton();

    /**
     * Constructor for a SearchableRadioButton that takes a label (as a String).
     * @param label string used for the RadioButton label
     */
    public SearchableRadioButton(String label) {
        HBox box = new HBox();
        box.setSpacing(2);
        box.setPrefWidth(165);
        box.setAlignment(Pos.CENTER_LEFT);
        box.spacingProperty().setValue(0);

        rbLabel.setPadding(new Insets(0, 85, 0, 0));
        rbLabel.setText(label);
        rbLabel.setStyle("-fx-font-weight: bold");

        box.getChildren().addAll(rb, rbLabel);

        this.getChildren().add(box);

    }

    /**
     * Constructor for a SearchableRadioButton that takes a label (as a String) and the searchableControls.
     * @param label string used for the RadioButton label
     * @param searchableControls A collection of searchable controls to add this control to
     */
    public SearchableRadioButton(String label, Collection<SearchableControl> searchableControls) {
        HBox box = new HBox();
        box.setPrefWidth(165);
        box.setAlignment(Pos.CENTER_LEFT);
        box.spacingProperty().setValue(0);

        rbLabel.setPadding(new Insets(0, 85, 0, 0));
        rbLabel.setText(label);
        rbLabel.setStyle("-fx-font-weight: bold");
        box.getChildren().addAll(rb, rbLabel);

        this.getChildren().add(box);
        searchableControls.add(this);
    }

    /**
     * Gets the RadioButton out of this class.
     * @return the RadioButton.
     */
    public RadioButton getRadioButton() {
        return this.rb;
    }

    /**
     * Queries the contents of the RadioButton label.
     *
     * @param query The search string to query the list contents with
     * @return If a match was found for the query
     */
    @Override
    public boolean query(String query) {
        return rbLabel.query(query);
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        return 0;
    }
}
