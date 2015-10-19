package seng302.group2.util.validation;

import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.story.Story;

/**
 * A class for validating the input on number only text fields
 * Created by drm127 on 20/05/15.
 */
public class PriorityFieldValidator {

    /**
     * Checks whether the new input priority is valid
     *
     * @param input           The input priority
     * @param backlog         The current backlog to check
     * @param currentPriority The current priority
     * @return A status representing the state of validity
     */
    private static ValidationStatus validatePriorityField(String input, Backlog backlog, Integer currentPriority) {
        //System.out.print(input);
        if (input == null || input.equals("")) {
            return ValidationStatus.NULL;
        }
        else {
            try {
                Integer parsedInt = Integer.parseInt(input);
                if (currentPriority != null && currentPriority.equals(parsedInt)) {
                    return ValidationStatus.VALID;
                }
                if (backlog != null) {
                    for (Story currentStory : backlog.getStories()) {
                        if (currentStory.getPriority().equals(parsedInt)) {
                            return ValidationStatus.NON_UNIQUE;
                        }
                    }
                }
            }
            catch (NullPointerException ex) {
                return ValidationStatus.NULL;
            }
            catch (NumberFormatException ex) {
                return ValidationStatus.INVALID;
            }
            return ValidationStatus.VALID;
        }
    }


    /**
     * Checks whether the priority is valid
     *
     * @param numberField     The nominated priority
     * @param backlog         The current backlog of the story. null if a new story.
     * @param currentPriority the current priority. null if a new story.
     * @return If the priority is valid
     */
    public static boolean validatePriorityField(RequiredField numberField, Backlog backlog, Integer currentPriority) {
        switch (PriorityFieldValidator.validatePriorityField(numberField.getText(), backlog, currentPriority)) {
            case VALID:
                ValidationStyle.borderGlowNone(numberField.getTextField());
                return true;
            case INVALID:
                ValidationStyle.borderGlowRed(numberField.getTextField());
                ValidationStyle.showMessage("You must enter integer values only", numberField.getTextField());
                return false;
            case NULL:
                ValidationStyle.borderGlowRed(numberField.getTextField());
                ValidationStyle.showMessage("You must enter an integer priority for this story",
                        numberField.getTextField());
                return false;
            case NON_UNIQUE:
                ValidationStyle.borderGlowRed(numberField.getTextField());
                ValidationStyle.showMessage("Priorities must be unique within a backlog", numberField.getTextField());
                return false;
            default:
                ValidationStyle.borderGlowRed(numberField.getTextField());
                ValidationStyle.showMessage("Not a valid number", numberField.getTextField());
                return false;
        }
    }


}
