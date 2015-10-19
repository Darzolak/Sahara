/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.util.validation;

import seng302.group2.Global;
import seng302.group2.workspace.allocation.Allocation;
import seng302.group2.workspace.project.Project;
import seng302.group2.workspace.team.Team;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * A class used to check the validity of inputted Dates.
 *
 * @author Jordane
 */
public class DateValidator {
    /**
     * Checks is the given string is a valid birth date
     *
     * @param birthDate the string representation of a birth date
     * @return The validation status of the check
     */
    public static ValidationStatus isValidBirthdate(String birthDate) {
        if (birthDate.isEmpty()) {
            return ValidationStatus.NULL;
        }

        //Global.datePattern.setLenient(false);
        try {
            String[] date = birthDate.split("/"); //returns an array with the day, month and year
            String year = date[date.length - 1];
            if (year.length() != 4) {
                return ValidationStatus.PATTERN_MISMATCH;
            }

            LocalDate parsedBirthDate = LocalDate.parse(birthDate, Global.dateFormatter);

            if (parsedBirthDate.isAfter(LocalDate.now())) {
                return ValidationStatus.OUT_OF_RANGE;
            }
            else {
                return ValidationStatus.VALID;
            }
        }
        catch (DateTimeParseException ex) {
            return ValidationStatus.PATTERN_MISMATCH;

        }
    }

    /**
     * Checks if the given allocation parameters are valid
     *
     * @param project    the project to allocate team to
     * @param team       the team to allocate
     * @param startDate  to validate
     * @param endDate    to validate
     * @param allocation allocation to be edited if method used for validating allocation
     * @return validation status
     */
    public static ValidationStatus validateAllocation(Project project, Team team,
                                                      LocalDate startDate, LocalDate endDate,
                                                      Allocation allocation) {
        if (allocation != null) {
            project = allocation.getProject();
            team = allocation.getTeam();
        }

        if (endDate != null) {
            if (dateAfter(startDate, endDate)) {
                return ValidationStatus.ALLOCATION_DATES_WRONG_ORDER;
            }
        }
        //Go through project's teams
        for (Allocation alloc : project.getTeamAllocations()) {
            if (alloc != allocation && alloc.getTeam() == team) { //&& alloc.getProject() == project
                ValidationStatus currentStatus = validateAllocationDate(alloc.getStartDate(),
                        alloc.getEndDate(),
                        startDate, endDate);
                if (currentStatus != ValidationStatus.VALID) {
                    return currentStatus;
                }
            }
        }

        Allocation currentAlloc = team.getCurrentAllocation();
        ValidationStatus projectComparison = ValidationStatus.VALID;
        if (currentAlloc != null && currentAlloc != allocation) {
            //Check that the team's currently allocated project doesn't overlap with
            // the new allocation
            LocalDate currentAllocStartDate = currentAlloc.getStartDate();
            LocalDate currentAllocEndDate = currentAlloc.getEndDate();
            projectComparison = validateAllocationDate(currentAllocStartDate, currentAllocEndDate,
                    startDate, endDate);
        }

        return projectComparison;
    }


    /**
     * Checks for clashing allocations?
     *
     * @param endDate   The end date of the allocation
     * @param project   The project that the allocation is for
     * @param startDate The start date of the allocation
     * @param team      The team that the allocation is for
     * @return clashAllocation The first allocation instance it clashes with
     */
    public static ValidationStatus validateAllocation(Project project, Team team,
                                                      LocalDate startDate, LocalDate endDate) {
        return validateAllocation(project, team, startDate, endDate, null);
    }

    /**
     * Determines the Validation status of a new allocation, checking if there are any illegal
     * overlaps with the start or end dates of other allocations.
     *
     * @param startDate1 the start date to check
     * @param endDate1   the end date to check
     * @param startDate2 the start date to compare to
     * @param endDate2   the end date to compare to
     * @return The validation status of the check
     */
    private static ValidationStatus validateAllocationDate(LocalDate startDate1, LocalDate endDate1,
                                                           LocalDate startDate2, LocalDate endDate2) {
        if (datesEqual(startDate1, startDate2)
                && datesEqual(endDate1, endDate2)) {
            return ValidationStatus.ALLOCATION_DATES_EQUAL;
        }
        else if (dateAfter(startDate1, startDate2)
                && dateBefore(endDate1, endDate2)) {
            return ValidationStatus.SUPER_OVERLAP; // teamAlloc is between startDate and endDate
        }
        else if (dateAfter(startDate1, startDate2)
                && datesEqual(endDate1, endDate2)) {
            return ValidationStatus.SUPER_OVERLAP; //when bigger
        }
        else if (datesEqual(startDate1, startDate2)
                && dateBefore(endDate1, endDate2)) {
            return ValidationStatus.SUPER_OVERLAP;
        }
        else if (dateBefore(startDate1, startDate2)
                && dateAfter(endDate1, endDate2)) {
            return ValidationStatus.SUB_OVERLAP; // new Allocation between start date and endDate
        }
        else if (dateBefore(startDate1, startDate2)
                && datesEqual(endDate1, endDate2)) {
            return ValidationStatus.SUB_OVERLAP;
        }
        else if (datesEqual(startDate1, startDate2)
                && dateAfter(endDate1, endDate2)) {
            return ValidationStatus.SUB_OVERLAP;
        }
        else if (dateBefore(startDate1, startDate2)
                && dateAfter(endDate1, startDate2)) {
            return ValidationStatus.START_OVERLAP;
        }
        else if (dateAfter(endDate1, startDate2)
                && dateBefore(startDate1, endDate2)) {
            return ValidationStatus.END_OVERLAP;
        }
        return ValidationStatus.VALID;
    }

    /**
     * Checks if date1 and date2 are equal
     *
     * @param date1 the first date
     * @param date2 the second date
     * @return if date1 and date2 are equal
     */
    private static boolean datesEqual(LocalDate date1, LocalDate date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
        else if (date1 == null) {
            return false;
        }
        else if (date2 == null) {
            return false;
        }
        else {
            return date1.equals(date2);

        }
    }

    /**
     * Checks if date1 comes before date2 in the case of 'infinite' nulls
     *
     * @param date1 the date to check
     * @param date2 the date to compare to
     * @return if date1 comes before date2
     */
    public static boolean dateBefore(LocalDate date1, LocalDate date2) {
        if (date1 == null) {
            return false;
        }
        else if (date2 == null) {
            return true;
        }
        else {
            return date1.isBefore(date2);
        }
    }

    /**
     * Checks if date1 comes after date2 in the case of 'infinite' nulls
     *
     * @param date1 the date to check
     * @param date2 the date to compare to
     * @return if date1 comes after date2
     */
    public static boolean dateAfter(LocalDate date1, LocalDate date2) {
        if (date1 == null) {
            return true;
        }
        else if (date2 == null) {
            return false;
        }
        else {
            return date1.isAfter(date2);
        }
    }


    /**
     * Determines if a LocalDate is in the future.
     *
     * @param date The date to check against the current time
     * @return A boolean showing if the date is in the future (true) or not (false).
     */
    public static boolean isFutureDate(LocalDate date) {
        return date.isAfter(LocalDate.now());
    }

    /**
     * Converts string to date
     *
     * @param releaseDateString String to be converted
     * @return date
     */
    public static LocalDate stringToDate(String releaseDateString) {
        LocalDate releaseDate = null;
        try {
            releaseDate = LocalDate.parse(releaseDateString, Global.dateFormatter);
        }
        catch (DateTimeParseException e) {
            System.out.println("Error parsing date");
        }
        return releaseDate;
    }


    /**
     * Validates a string to see if the string is formatted correctly i.e 2h30min where h can be replaced with "hour,
     * "h", "hrs", "hr", "hours" and "min" can be placed with "m", "minute", "minutes" or "mins"
     * "min" can also be replaced with "m"
     *
     * @param inputString time string to validate
     * @return true if input string is in the right format
     */
    public static boolean validDuration(String inputString) {
        String pattern = "(( *)((\\d*)( *)((h)|(hour)|(hours)|(hrs)|(hr)))?( *)"  // normal hours
                + "((([0-5][0-9])|([0-9]))( *)((min)|(m)|(mins)|(minutes)|(minute)))?( *))"  // normal minutes
                + "|((\\d*(\\.\\d*)?)( *)(h|hour|hours|hrs|hr)?( *))";  // or, base unit (default hours), eg. '2'.


        return Pattern.matches(pattern, inputString);
    }
}
