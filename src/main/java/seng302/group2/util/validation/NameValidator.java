/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.util.validation;

import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.validation.ValidationStyle;

/**
 * A class for checking the validity of inputted Names.
 *
 * @author Jordane
 */
public class NameValidator {
    /**
     * Checks whether the name is not null or an empty string
     *
     * @param name The name to check
     * @return An enum value showing whether the name is not null or an empty string
     */
    public static ValidationStatus validateName(String name) {
        if (name.isEmpty()) {
            return ValidationStatus.NULL;
        }
        else if (name != null) {
            return ValidationStatus.VALID;
        }
        else {
            return ValidationStatus.INVALID;
        }
    }

    /**
     * Checks whether the name is valid
     *
     * @param nameField the text field
     * @return If the name is valid
     */
    public static boolean validateName(RequiredField nameField) {
        switch (NameValidator.validateName(nameField.getText())) {
            case VALID:
                ValidationStyle.borderGlowNone(nameField.getTextField());
                return true;
            case INVALID:
                ValidationStyle.borderGlowRed(nameField.getTextField());
                ValidationStyle.showMessage("Please insert a name",
                        nameField.getTextField());
                return false;
            case NULL:
                ValidationStyle.borderGlowRed(nameField.getTextField());
                ValidationStyle.showMessage("Please insert a name",
                        nameField.getTextField());
                return false;
            default:
                ValidationStyle.borderGlowRed(nameField.getTextField());
                ValidationStyle.showMessage("* Not a valid name",
                        nameField.getTextField());
                return false;
        }
    }
}
