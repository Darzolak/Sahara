package seng302.group2.scenes.control;

import com.sun.javafx.scene.control.behavior.TextAreaBehavior;
import com.sun.javafx.scene.control.skin.TextAreaSkin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.validation.ValidationStyle;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Creates a custom text area which displays appropriate error messages when required.
 * Created by Codie on 02/04/2015
 */
public class CustomTextArea extends VBox implements SearchableControl {
    private TextArea inputText = new TextArea();
    private Set<SearchableControl> searchControls = new HashSet<>();

    /**
     * Creates a required label HBox inside of the VBox containing a Label with an appended red
     * asterisk.
     *
     * @param name The node field that is required
     */
    public CustomTextArea(String name) {
        inputText.setWrapText(true);
        inputText.setPrefRowCount(5);


        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);
        labelBox.spacingProperty().setValue(0);
        labelBox.setAlignment(Pos.CENTER_LEFT);

        SearchableText label = new SearchableText(name.trim(), searchControls);
        label.setStyle("-fx-font-weight: bold");
        labelBox.getChildren().addAll(label);

        Insets insets = new Insets(0, 0, 5, 0);
        labelBox.setPadding(insets);

        VBox entry = new VBox();
        entry.setPrefWidth(175);
        entry.getChildren().addAll(labelBox, inputText);

        this.getChildren().add(entry);

        inputText.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) inputText.getSkin();
                if (skin.getBehavior() != null) {
                    TextAreaBehavior behavior = skin.getBehavior();
                    if (event.isControlDown()) {
                        behavior.callAction("InsertTab");
                    }
                    else {
                        behavior.callAction("TraverseNext");
                    }
                    event.consume();
                }

            }
        });
    }

    /**
     * Creates a required label HBox inside of the VBox containing a Label with an appended red
     * asterisk.
     *
     * @param name               The node field that is required
     * @param searchableControls The collection of searchable controls to add this control too
     */
    public CustomTextArea(String name, Collection<SearchableControl> searchableControls) {
        searchableControls.add(this);
        inputText.setWrapText(true);
        inputText.setPrefRowCount(5);


        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);
        labelBox.spacingProperty().setValue(0);
        labelBox.setAlignment(Pos.CENTER_LEFT);

        SearchableText label = new SearchableText(name.trim(), searchControls);
        label.setStyle("-fx-font-weight: bold");
        labelBox.getChildren().addAll(label);

        Insets insets = new Insets(0, 0, 5, 0);
        labelBox.setPadding(insets);

        VBox entry = new VBox();
        entry.setPrefWidth(175);
        entry.getChildren().addAll(labelBox, inputText);

        this.getChildren().add(entry);

        inputText.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) inputText.getSkin();
                if (skin.getBehavior() != null) {
                    TextAreaBehavior behavior = skin.getBehavior();
                    if (event.isControlDown()) {
                        behavior.callAction("InsertTab");
                    }
                    else {
                        behavior.callAction("TraverseNext");
                    }
                    event.consume();
                }

            }
        });
    }

    /**
     * Creates a required label HBox inside of the VBox containing a Label with an appended red
     * asterisk.
     *
     * @param name  The node field that is required
     * @param width The width of the area
     */
    public CustomTextArea(String name, int width) {
        inputText.setWrapText(true);
        inputText.setPrefRowCount(5);

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);
        labelBox.spacingProperty().setValue(0);

        SearchableText label = new SearchableText(name.trim(), searchControls);
        label.setStyle("-fx-font-weight: bold");
        labelBox.getChildren().addAll(label);

        Insets insets = new Insets(0, 0, 5, 0);
        labelBox.setPadding(insets);

        VBox entry = new VBox();
        entry.setPrefWidth(width);
        entry.getChildren().addAll(labelBox, inputText);

        this.getChildren().add(entry);

        inputText.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) inputText.getSkin();
                if (skin.getBehavior() != null) {
                    TextAreaBehavior behavior = skin.getBehavior();
                    if (event.isControlDown()) {
                        behavior.callAction("InsertTab");
                    }
                    else {
                        behavior.callAction("TraverseNext");
                    }
                    event.consume();
                }

            }
        });
    }


    /**
     * Creates a required label HBox inside of the VBox containing a Label with an appended red
     * asterisk.
     *
     * @param name               The node field that is required
     * @param width              The width of the area
     * @param searchableControls The collection of searchable controls to add this control too
     */
    public CustomTextArea(String name, int width, Collection<SearchableControl> searchableControls) {
        searchableControls.add(this);
        inputText.setWrapText(true);
        inputText.setPrefRowCount(5);

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);
        labelBox.spacingProperty().setValue(0);

        SearchableText label = new SearchableText(name.trim(), searchControls);
        label.setStyle("-fx-font-weight: bold");
        labelBox.getChildren().addAll(label);

        Insets insets = new Insets(0, 0, 5, 0);
        labelBox.setPadding(insets);

        VBox entry = new VBox();
        entry.setPrefWidth(width);
        entry.getChildren().addAll(labelBox, inputText);

        this.getChildren().add(entry);

        inputText.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                TextAreaSkin skin = (TextAreaSkin) inputText.getSkin();
                if (skin.getBehavior() != null) {
                    TextAreaBehavior behavior = skin.getBehavior();
                    if (event.isControlDown()) {
                        behavior.callAction("InsertTab");
                    }
                    else {
                        behavior.callAction("TraverseNext");
                    }
                    event.consume();
                }

            }
        });
    }

    /**
     * Returns the text inside the text field of the CustomTextArea.
     *
     * @return The text of the text field
     */
    public String getText() {
        return this.inputText.getText().trim();
    }


    /**
     * Sets the text inside the text field of the CustomTextArea.
     *
     * @param text the text to be inserted into the text field
     */
    public void setText(String text) {
        this.inputText.setText(text);
    }


    /**
     * Returns the inner text area
     *
     * @return The inner text area
     */
    public TextArea getTextArea() {
        return this.inputText;
    }


    @Override
    public boolean query(String query) {
        boolean found = false;

        if (query.isEmpty()) {
            ValidationStyle.borderGlowNone(inputText);
        }

        for (SearchableControl control : searchControls) {
            found = found || control.query(query);
        }

        if (inputText.getText().contains(query) && !query.trim().isEmpty()) {
            found = true;
            ValidationStyle.borderGlowSearch(inputText);
        }
        else {
            ValidationStyle.borderGlowNone(this);
        }

        return found;
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        return 0;
    }
}

