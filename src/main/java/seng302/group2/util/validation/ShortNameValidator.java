/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.util.validation;

import seng302.group2.Global;
import seng302.group2.scenes.control.RequiredField;
import seng302.group2.scenes.validation.ValidationStyle;
import seng302.group2.workspace.person.Person;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.project.backlog.Backlog;
import seng302.group2.workspace.project.release.Release;
import seng302.group2.workspace.project.sprint.Sprint;
import seng302.group2.workspace.project.story.Story;
import seng302.group2.workspace.role.Role;
import seng302.group2.workspace.skills.Skill;
import seng302.group2.workspace.team.Team;


/**
 * A class for checking the validity of a short name.
 *
 * @author Jordane
 */
public class ShortNameValidator {

    /**
     * Checks whether a given short name is valid (unique and not null/empty)
     *
     * @param shortNameField   is a short name field
     * @param currentShortName If an element is being edited, the current short name of that element.
     * @return If the short name is valid
     */
    public static boolean validateShortName(RequiredField shortNameField, String currentShortName) {
        ValidationStatus status;
        status = validateShortName(shortNameField.getText(), currentShortName);

        switch (status) {
            case VALID:
                ValidationStyle.borderGlowNone(shortNameField.getTextField());
                return true;
            case NON_UNIQUE:
                ValidationStyle.borderGlowRed(shortNameField.getTextField());
                ValidationStyle.showMessage("Short name must be unique", shortNameField.getTextField());
                return false;
            case NULL:
                ValidationStyle.borderGlowRed(shortNameField.getTextField());
                ValidationStyle.showMessage("Short name required", shortNameField.getTextField());
                return false;
            case INVALID:
                ValidationStyle.borderGlowRed(shortNameField.getTextField());
                ValidationStyle.showMessage("Not a valid short name", shortNameField.getTextField());
                return false;
            case OUT_OF_RANGE:
                ValidationStyle.borderGlowRed(shortNameField.getTextField());
                ValidationStyle.showMessage("Short name must be less than 20 characters",
                        shortNameField.getTextField());
                return false;
            default:
                ValidationStyle.borderGlowRed(shortNameField.getTextField());
                ValidationStyle.showMessage("Not a valid short name", shortNameField.getTextField());
                return false;
        }
    }


    /**
     * Checks whether a short name is valid (unique and not null/empty).
     *
     * @param shortName       The short name to validate
     * @param editedShortName If an element is being edited, the current short name of that element.
     * @return Validation status representing if the short name is valid
     */
    public static ValidationStatus validateShortName(String shortName, String editedShortName) {
        //Test if the same
        if (shortName.equals(editedShortName)) {
            return ValidationStatus.VALID;
        }
        if (shortName.isEmpty()) {
            return ValidationStatus.NULL;
        }
        // Test if valid name
        if (NameValidator.validateName(shortName) == ValidationStatus.INVALID) {
            return ValidationStatus.INVALID;
        }
        // Test if short
        if (shortName.length() > 20) {
            return ValidationStatus.OUT_OF_RANGE;
        }

        // Test if unique
        if (Global.currentWorkspace.getShortName().equals(shortName)) {
            return ValidationStatus.NON_UNIQUE;
        }

        for (Person pers : Global.currentWorkspace.getPeople()) {
            if (pers.getShortName().toUpperCase().equals(shortName.toUpperCase())) {
                return ValidationStatus.NON_UNIQUE;
            }
        }

        for (Team team : Global.currentWorkspace.getTeams()) {
            if (team.getShortName().equals(shortName)) {
                return ValidationStatus.NON_UNIQUE;
            }
        }

        for (Skill skill : Global.currentWorkspace.getSkills()) {
            if (skill.getShortName().equals(shortName)) {
                return ValidationStatus.NON_UNIQUE;
            }
        }

        for (Role role : Global.currentWorkspace.getRoles()) {
            if (role.getShortName().equals(shortName)) {
                return ValidationStatus.NON_UNIQUE;
            }
        }

        for (Project proj : Global.currentWorkspace.getProjects()) {
            if (proj.getShortName().equals(shortName)) {
                return ValidationStatus.NON_UNIQUE;
            }
            for (Release rel : proj.getReleases()) {
                if (rel.getShortName().equals(shortName)) {
                    return ValidationStatus.NON_UNIQUE;
                }
            }
            for (Story story : proj.getUnallocatedStories()) {
                if (story.getShortName().equals(shortName)) {
                    return ValidationStatus.NON_UNIQUE;
                }
            }
            for (Backlog backlog : proj.getBacklogs()) {
                if (backlog.getShortName().equals(shortName)) {
                    return ValidationStatus.NON_UNIQUE;
                }
            }
            for (Sprint sprint : proj.getSprints()) {
                if (sprint.getGoal().equals(shortName)) {
                    return ValidationStatus.NON_UNIQUE;
                }
            }
        }

        return ValidationStatus.VALID;
    }
}
