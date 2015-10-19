/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.util.validation;

/**
 * A class for checking the validity of inputted Email addresses.
 *
 * @author Jordane
 */
public class EmailValidator {
    /**
     * The defined email pattern to match against.
     */
    private static String emailPattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\."
            + "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    /**
     * Checks whether a given string matches the email pattern regular expression.
     *
     * @param email The email string to validate
     * @return Whether the email is valid
     */
    public static ValidationStatus validEmail(String email) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(emailPattern);
        java.util.regex.Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            return ValidationStatus.VALID;
        }
        else {
            return ValidationStatus.INVALID;
        }
    }
}
