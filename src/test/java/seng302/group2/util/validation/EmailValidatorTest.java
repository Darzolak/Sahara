/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seng302.group2.util.validation;

import org.junit.Assert;
import org.junit.Test;


/**
 * A test for the Email validation class
 *
 * @author Jordane
 */
public class EmailValidatorTest {
    /**
     * Test of validEmail method, of class EmailValidator.
     */
    @Test
    public void testValidEmail() {
        Assert.assertEquals(ValidationStatus.INVALID, EmailValidator.validEmail(""));
        Assert.assertEquals(ValidationStatus.VALID, EmailValidator.validEmail("abc@gmail.com"));
    }

}
