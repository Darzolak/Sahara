package seng302.group2.scenes.control;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.validation.ValidationStyle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates a custom  text field where the box may not be empty
 * and appropriate error messages are displayed when required.
 * Created by Jordane on 24/03/2015.
 */
public class RequiredField extends VBox implements SearchableControl {
    private TextField inputText = new TextField();
    private Set<SearchableControl> searchControls = new HashSet<>();

    /**
     * Creates a required label HBox inside of the VBox containing a Label with an appended red
     * asterisk.
     *
     * @param name The label for the node required
     */
    public RequiredField(String name) {
        inputText.setPrefWidth(175);

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);
        Label aster = new Label(" * ");
        aster.setTextFill(Color.web("#ff0000"));

        SearchableText label = new SearchableText(name.trim(), searchControls);
        label.setStyle("-fx-font-weight: bold");
        labelBox.getChildren().addAll(label, aster);

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
     * @param controlCollection The search collection to add this control to
     */
    public RequiredField(String name, Collection<SearchableControl> controlCollection) {

        controlCollection.add(this);

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);

        Label aster = new Label(" * ");
        aster.setTextFill(Color.web("#ff0000"));

        SearchableText label = new SearchableText(name.trim(), searchControls);
        label.setStyle("-fx-font-weight: bold");
        labelBox.getChildren().addAll(label, aster);

        HBox entry = new HBox();
        entry.setPrefWidth(175);
        entry.getChildren().addAll(labelBox, inputText);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        this.getChildren().add(entry);
    }


    /**
     * Returns the text inside the text field of the RequiredField.
     *
     * @return The text of the text field
     */
    public String getText() {
        return this.inputText.getText().trim();
    }


    /**
     * Returns the text inside the text field of the RequiredField.
     *
     * @return The text of the text field
     */
    public String getTextWithoutTrim() {
        return this.inputText.getText();
    }


    /**
     * Sets the text inside the text field of the RequiredField.
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
            ValidationStyle.borderGlowNone(inputText);
        }

        boolean found = false;
        for (SearchableControl control : searchControls) {
            found = found || control.query(query);
        }

        if (inputText.getText().toLowerCase().contains(query) && !query.trim().isEmpty()) {
            found = true;
            ValidationStyle.borderGlowSearch(inputText);
        }
        else {
            ValidationStyle.borderGlowNone(inputText);
        }

        return found;
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        return 0;
    }
}
