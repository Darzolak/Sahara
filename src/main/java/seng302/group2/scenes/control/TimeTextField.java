package seng302.group2.scenes.control;

import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.scene.control.IndexRange;
import seng302.group2.scenes.control.search.SearchableTextField;

import java.util.regex.Pattern;

/**
 * spCustomised text field for time input
 * Created by swi67 on 11/08/15.
 */
public class TimeTextField extends SearchableTextField {

    private final Pattern timePattern;
    private final ReadOnlyIntegerWrapper hours;
    private final ReadOnlyIntegerWrapper minutes;
    public TimeTextField() {
        this("00:00");
    }

    public TimeTextField(String time) {
        super(time);
        this.setPrefWidth(175);
        timePattern = Pattern.compile("\\d\\d:\\d\\d");
        if (!validate(time)) {
            throw new IllegalArgumentException("Invalid time: " + time);
        }
        hours = new ReadOnlyIntegerWrapper(this, "hours");
        minutes = new ReadOnlyIntegerWrapper(this, "minutes");

        hours.bind(new TimeTextField.TimeUnitBinding(Unit.HOURS));
        minutes.bind(new TimeTextField.TimeUnitBinding(Unit.MINUTES));
    }

    /**
     * Gets the inputted hours
     *
     * @return hours
     */
    public int getHours() {
        return hours.get();
    }


    /**
     * Gets the inputted minutes
     *
     * @return minutes
     */
    public int getMinutes() {
        return minutes.get();
    }

    /**
     * Customised deleteNextChar method which replaces the character that follows the current caret position from the
     * text with "0" if there is no selection. Replaces the selection with 0's if there is selection. Colons are not
     * fixed and cannot be deleted.
     *
     * @return true if the deletion is successful, false otherwise.
     */
    @Override
    public boolean deleteNextChar() {
        boolean success = false;

        // If there's a selection, delete it:
        final IndexRange selection = getSelection();
        if (selection.getLength() > 0) {
            int selectionEnd = selection.getEnd();
            this.deleteText(selection);
            this.positionCaret(selectionEnd);
            success = true;
        }
        else {
            // If the caret preceeds a digit, replace that digit with a zero and move the caret forward. Else just move
            // the caret forward.
            int caret = this.getCaretPosition();
            if (caret % 3 != 2) { // not preceeding a colon
                String currentText = this.getText();
                setText(currentText.substring(0, caret) + "0" + currentText.substring(caret + 1));
                success = true;
            }
            this.positionCaret(Math.min(caret + 1, this.getText().length()));
        }
        return success;
    }

    /**
     * Customised deleteNextChar method which replaces the character that precedes the current caret position from the
     * text with "0" if there is no selection. Replaces the selection with 0's if there is selection. Colons are not
     * fixed and cannot be deleted.
     *
     * @return true if the deletion is successful, false otherwise.
     */
    @Override
    public boolean deletePreviousChar() {
        boolean success = false;
        // If there's a selection, delete it:
        final IndexRange selection = getSelection();
        if (selection.getLength() > 0) {
            int selectionStart = selection.getStart();
            this.deleteText(selection);
            this.positionCaret(selectionStart);
            success = true;
        }
        else {
            // If the caret is after a digit, replace that digit with a zero and move the caret backward. Else just
            // move the caret back.
            int caret = this.getCaretPosition();
            if (caret % 3 != 0) { // not following a colon
                String currentText = this.getText();
                setText(currentText.substring(0, caret - 1) + "0" + currentText.substring(caret));
                success = true;
            }
            this.positionCaret(Math.max(caret - 1, 0));
        }
        return success;
    }

    /**
     * Removes a range of characters from the content.
     *
     * @param range The range of text to be removed
     */
    @Override
    public void deleteText(IndexRange range) {
        this.deleteText(range.getStart(), range.getEnd());
    }

    /**
     * Removes a range of characters from the content.Colons are dismissed.
     *
     * @param begin the start index (inclusive)
     * @param end   the end index (exclusive)
     */
    @Override
    public void deleteText(int begin, int end) {
        // Replace all digits in the given range with zero:
        StringBuilder builder = new StringBuilder(this.getText());
        for (int c = begin; c < end; c++) {
            if (c % 3 != 2) { // Not at a colon:
                builder.replace(c, c + 1, "0");
            }
        }
        this.setText(builder.toString());
    }

    /**
     * Inserts a sequence of characters into the content. Colons are fixed and cannot be replaced.
     *
     * @param index the location to insert the text.
     * @param text  the text to insert.
     */
    @Override
    public void insertText(int index, String text) {
        // Handle an insert by replacing the range from index to index+text.length() with text, if that results in a
        // valid string:
        StringBuilder builder = new StringBuilder(this.getText());
        builder.replace(index, index + text.length(), text);
        final String testText = builder.toString();
        if (validate(testText)) {
            this.setText(testText);
        }
        this.positionCaret(index + text.length());
        if (this.getCaretPosition() % 3 == 2) {
            this.positionCaret(index + text.length() + 1);
        }
    }

    /**
     * Replaces the selection with the given replacement String. If there is no selection, then the replacement text is
     * simply inserted at the current caret position. If there was a selection, then the selection is cleared and the
     * given replacement text inserted.Colons are fixed in place and cannot be replaced. Texts are inserted around the
     * colon.
     *
     * @param replacement the String to replace the selection with
     */
    @Override
    public void replaceSelection(String replacement) {
        final IndexRange selection = this.getSelection();
        if (selection.getLength() == 0) {
            this.insertText(selection.getStart(), replacement);
        }
        else {
            this.replaceText(selection.getStart(), selection.getEnd(), replacement);
        }
    }

    /**
     * Replaces a range of characters with the given text.
     *
     * @param range The range of text to replace. The range object must not be null.
     * @param text  The text that is to replace the range. This must not be null.
     */
    @Override
    public void replaceText(IndexRange range, String text) {
        this.replaceText(range.getStart(), range.getEnd(), text);
    }

    /**
     * Replaces a range of characters with the given text. String must b 22222
     *
     * @param begin The starting index in the range, inclusive. This must be &gt;= 0 and &lt; the end.
     * @param end   The ending index in the range, exclusive. This is one-past the last character to delete (consistent
     *              with the String manipulation methods). This must be &gt; the start, and &lt;= the length of the
     *              text.
     * @param text  The text that is to replace the range. This must not be null.
     */
    @Override
    public void replaceText(int begin, int end, String text) {
        if (begin == end) {
            this.insertText(begin, text);
        }
        else {
            // only handle this if text.length() is equal to the number of characters being replaced, and if the
            // replacement results in a valid string:
            if (text.length() == end - begin) {
                StringBuilder builder = new StringBuilder(this.getText());
                builder.replace(begin, end, text);
                String testText = builder.toString();
                if (validate(testText)) {
                    this.setText(testText);
                }
                this.positionCaret(end);
            }
        }
    }

    /**
     * Validates the given string to ensure that invalid time cannot be inputted.
     *
     * @param time the string being validated
     * @return return if the string passed the validation
     */
    private boolean validate(String time) {
        if (!timePattern.matcher(time).matches()) {
            return false;
        }
        String[] tokens = time.split(":");
        try {
            int hours = Integer.parseInt(tokens[0]);
            int mins = Integer.parseInt(tokens[1]);

            if (hours < 0 || hours > 23) {
                return false;
            }
            if (mins < 0 || mins > 59) {
                return false;
            }

            return true;
        }
        catch (NumberFormatException nfe) {
            // regex matching should assure we never reach this catch block
            assert false;
            return false;
        }
    }


    enum Unit { HOURS, MINUTES }

    /**
     * Simple invalidation schema
     */
    private final class TimeUnitBinding extends IntegerBinding {
        final Unit unit;

        TimeUnitBinding(Unit unit) {
            this.bind(textProperty());
            this.unit = unit;
        }

        /**
         * Calculates the value of this binding
         *
         * @return the current value
         */
        @Override
        protected int computeValue() {
            String token = getText().split(":")[unit.ordinal()];
            return Integer.parseInt(token);
        }

    }

}
