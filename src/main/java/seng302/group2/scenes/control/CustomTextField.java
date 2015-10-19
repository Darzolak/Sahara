package seng302.group2.scenes.control;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableText;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates a custom text field which displays appropriate error messages when required.
 * Created by Codie on 02/04/2015
 */
public class CustomTextField extends VBox implements SearchableControl {
    private TextField inputText = new TextField();
    private Set<SearchableControl> searchControls = new HashSet<>();

    /**
     * Creates a required label HBox inside of the VBox containing a Label with an appended red
     * asterisk.
     *
     * @param name The node field that is required
     */
    public CustomTextField(String name) {
        inputText.setPrefWidth(175);

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);

        SearchableText label = new SearchableText(name.trim(), searchControls);
        label.setStyle("-fx-font-weight: bold");
        labelBox.getChildren().addAll(label);

        HBox entry = new HBox();
        entry.setPrefWidth(175);
        entry.getChildren().addAll(labelBox, inputText);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        this.getChildren().add(entry);
    }

    /**
     * Creates a required label HBox inside of the VBox containing a Label with an appended red
     * asterisk.
     *
     * @param name              The node field that is required
     * @param controlCollection The search control collection to add this control to
     */
    public CustomTextField(String name, Collection<SearchableControl> controlCollection) {
        controlCollection.add(this);
        this.inputText.setPrefWidth(175);

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);
        labelBox.spacingProperty().setValue(0);
        labelBox.setAlignment(Pos.CENTER_LEFT);

        SearchableText label = new SearchableText(name.trim(), searchControls);
        label.setStyle("-fx-font-weight: bold");
        labelBox.getChildren().addAll(label);

        HBox entry = new HBox();
        entry.setPrefWidth(175);
        entry.getChildren().addAll(labelBox, inputText);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        this.getChildren().add(entry);
    }

    /**
     * Returns the text inside the text field of the CustomTextField.
     *
     * @return The text of the text field
     */
    public String getText() {
        return this.inputText.getText().trim();
    }


    /**
     * Sets the text inside the text field of the CustomTextField.
     *
     * @param text the text to be inserted into the text field
     */
    public void setText(String text) {
        this.inputText.setText(text);
    }


    /**
     * Gets the input text field
     *
     * @return textfield
     */
    public TextField getTextField() {
        return inputText;
    }

    @Override
    public boolean query(String query) {
        query = query.toLowerCase();
        if (query.isEmpty()) {
            inputText.setStyle("-fx-border-color: inherit");
        }

        boolean found = false;
        for (SearchableControl control : searchControls) {
            found = found || control.query(query);
        }

        if (inputText.getText().toLowerCase().contains(query) && !query.trim().isEmpty()) {
            found = true;
            inputText.setStyle("-fx-border-color: " + SearchableControl.highlightColourString);
        }
        else {
            inputText.setStyle("-fx-border-color: inherit");
        }

        return found;
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        return 0;
    }
}
