/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.scenes.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import seng302.group2.scenes.control.search.SearchType;
import seng302.group2.scenes.control.search.SearchableControl;
import seng302.group2.scenes.control.search.SearchableText;
import seng302.group2.scenes.validation.ValidationStyle;

import java.util.Collection;

/**
 * @author Shinobu
 */
public class CustomComboBox<T> extends VBox implements SearchableControl {
    private boolean required = false;
    private ObservableList<T> options = FXCollections.observableArrayList();
    private ComboBox<T> comboBox = new ComboBox<>(options);
    private Label astrLabel = new Label(" * ");
    private SearchableText titleLabel = new SearchableText();

    /**
     * Creates a ComboBox and Label inside a single HBox.
     *
     * @param name The label of the combo box
     */
    public CustomComboBox(String name) {
        titleLabel.setStyle("-fx-font-weight: bold");

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);

        titleLabel.setText(name);
        labelBox.getChildren().addAll(titleLabel);

        if (required) {
            Label aster = astrLabel;
            aster.setTextFill(Color.web("#ff0000"));
            labelBox.getChildren().add(aster);
        }

        HBox entry = new HBox();
        entry.setPrefWidth(275);
        entry.getChildren().addAll(labelBox, this.comboBox);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        this.comboBox.setMinWidth(135);
        this.comboBox.setPrefWidth(175);
        this.getChildren().add(entry);
    }

    /**
     * Creates a ComboBox and Label inside a single HBox.
     *
     * @param name     The label of the combo box
     * @param required Whether or not the field is required
     */
    public CustomComboBox(String name, boolean required) {
        this.required = required;
        titleLabel.setStyle("-fx-font-weight: bold");

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);

        titleLabel.setText(name);
        labelBox.getChildren().addAll(titleLabel);

        if (required) {
            Label aster = astrLabel;
            aster.setTextFill(Color.web("#ff0000"));
            labelBox.getChildren().add(aster);
        }

        HBox entry = new HBox();
        entry.setPrefWidth(275);
        entry.getChildren().addAll(labelBox, this.comboBox);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        this.comboBox.setMinWidth(135);
        this.comboBox.setPrefWidth(175);
        this.getChildren().add(entry);
    }

    /**
     * Creates a ComboBox and Label inside a single HBox.
     *
     * @param name               The label of the combo box
     * @param searchableControls The collection of searchable controls to add this to
     */
    public CustomComboBox(String name, Collection<SearchableControl> searchableControls) {
        searchableControls.add(this);
        titleLabel.setStyle("-fx-font-weight: bold");

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);

        titleLabel.setText(name);
        labelBox.getChildren().addAll(titleLabel);

        if (required) {
            Label aster = astrLabel;
            aster.setTextFill(Color.web("#ff0000"));
            labelBox.getChildren().add(aster);
        }

        HBox entry = new HBox();
        entry.setPrefWidth(275);
        entry.getChildren().addAll(labelBox, this.comboBox);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        this.comboBox.setMinWidth(135);
        this.comboBox.setPrefWidth(175);
        this.getChildren().add(entry);
    }

    /**
     * Creates a ComboBox and Label inside a single HBox.
     *
     * @param name               The label of the combo box
     * @param required           Whether or not the field is required
     * @param searchableControls The collection of searchable controls to add this to
     */
    public CustomComboBox(String name, boolean required, Collection<SearchableControl> searchableControls) {
        searchableControls.add(this);
        this.required = required;
        titleLabel.setStyle("-fx-font-weight: bold");

        HBox labelBox = new HBox();
        labelBox.setPrefWidth(175);

        titleLabel.setText(name);
        labelBox.getChildren().addAll(titleLabel);

        if (required) {
            Label aster = astrLabel;
            aster.setTextFill(Color.web("#ff0000"));
            labelBox.getChildren().add(aster);
        }

        HBox entry = new HBox();
        entry.setPrefWidth(275);
        entry.getChildren().addAll(labelBox, this.comboBox);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        this.comboBox.setMinWidth(135);
        this.comboBox.setPrefWidth(175);
        this.getChildren().add(entry);
    }


    /**
     * Disables and enables the combobox. Does not affect the labels.
     *
     * @param disable boolean which either disables or enables depending on its value
     */
    public void disable(boolean disable) {
        if (disable) {
            this.comboBox.setDisable(true);
        }
        else {
            this.comboBox.setDisable(false);
        }
    }

    /**
     * Sets the tooltip of the combobox.
     *
     * @param tool The tooltip
     */
    public void setTooltip(javafx.scene.control.Tooltip tool) {
        this.comboBox.setTooltip(tool);
    }

    /**
     * Removes the tooltip of the combobox.
     */
    public void removeTooltip() {
        this.comboBox.setTooltip(null);
    }


    /**
     * Add option item to Combo Box
     *
     * @param item String item to add
     */
    public void addToComboBox(T item) {
        this.options.add(item);
    }


    /**
     * Removes all options inside the combo box
     */
    public void clear() {
        options.clear();
    }

    /**
     * Gets the inner combo box
     *
     * @return The combo box
     */
    public ComboBox<T> getComboBox() {
        return this.comboBox;
    }

    /**
     * Gets the value of the chosen item in combo box
     *
     * @return The string of the chosen item
     */
    public T getValue() {
        if (this.comboBox.getValue() != null) {
            return this.comboBox.getValue();
        }
        else {
            return null;
        }
    }

    /**
     * Sets the value of the selected item of combo box
     *
     * @param value value to set to
     */
    public void setValue(T value) {
        this.comboBox.setValue(value);
    }


    /**
     * Queries the combo box to find any elements containing the given query string. If found inside the combo box, the
     * combo box border will be highlighted, and if found in the label text, the matching text will be highlighted.
     *
     * @param query The query string to search
     * @return Whether any elements inside of the combobox or label were found to contain the query string
     */
    @Override
    public boolean query(String query) {
        if (query.trim().isEmpty()) {
            ValidationStyle.borderGlowNone(comboBox);
            //comboBox.setStyle("-fx-border-color: inherit");
        }
        boolean foundCombo = false;
        for (T item : comboBox.getItems()) {
            if (item.toString().toLowerCase().contains(query.toLowerCase()) && !query.trim().isEmpty()) {
                foundCombo = true;
            }
        }
        if (foundCombo) {
            ValidationStyle.borderGlowSearch(comboBox);
            //comboBox.setStyle("-fx-border-color: " + SearchableControl.highlightColourString + ";");
        }
        else {
            ValidationStyle.borderGlowNone(comboBox);
            //comboBox.setStyle("-fx-border-color: inherit");
        }

        boolean foundText = titleLabel.query(query);

        return foundCombo || foundText;
    }

    @Override
    public int advancedQuery(String query, SearchType searchType) {
        return 0;
    }
}
