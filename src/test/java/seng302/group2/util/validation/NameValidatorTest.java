/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.util.validation;

import org.junit.Assert;
import org.junit.Test;

/**
 * A test class for NameValidator
 *
 * @author Jordane
 */
public class NameValidatorTest {
    /**
     * Test of validateName method, of class NameValidator.
     */
    @Test
    public void testValidateName() {
        Assert.assertEquals(ValidationStatus.NULL, NameValidator.validateName(""));
        Assert.assertEquals(ValidationStatus.VALID, NameValidator.validateName("Greg"));
    }

}
