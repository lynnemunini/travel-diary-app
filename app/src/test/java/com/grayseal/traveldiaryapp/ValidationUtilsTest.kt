package com.grayseal.traveldiaryapp

import com.grayseal.traveldiaryapp.utils.isEmailValid
import com.grayseal.traveldiaryapp.utils.isPasswordValid
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun testValidEmail() {
        val validEmail = "test@example.com"
        assertTrue(isEmailValid(validEmail))
    }

    @Test
    fun testInvalidEmail() {
        val invalidEmail = "invalid.email"
        assertFalse(isEmailValid(invalidEmail))
    }

    @Test
    fun testShortPassword() {
        val shortPassword = "abc12" // Less than 6 characters
        assertFalse(isPasswordValid(shortPassword))
    }

    @Test
    fun testValidPassword() {
        val validPassword = "secure123"
        assertTrue(isPasswordValid(validPassword))
    }
}
