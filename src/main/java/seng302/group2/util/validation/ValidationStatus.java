/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.util.validation;

/**
 * An enumeration of validation statuses
 *
 * @author Jordane
 */
public enum ValidationStatus {
    // Common statuses
    VALID,
    INVALID,
    NON_UNIQUE,
    NULL,

    // Pattern/RegEx status
    PATTERN_MISMATCH,

    // Birth date statuses
    OUT_OF_RANGE,

    // Allocation date statuses
    ALLOCATION_DATES_WRONG_ORDER,
    START_OVERLAP,
    END_OVERLAP,
    SUPER_OVERLAP,
    SUB_OVERLAP,
    ALLOCATION_DATES_EQUAL
}