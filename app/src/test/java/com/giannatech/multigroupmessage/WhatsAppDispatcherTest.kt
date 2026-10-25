package com.giannatech.multigroupmessage

import com.giannatech.multigroupmessage.util.WhatsAppDispatcher
import org.junit.Assert.assertEquals
import org.junit.Test

class WhatsAppDispatcherTest {

    @Test
    fun formatPhoneNumber_addsDefaultCountryCode_when10or11Digits() {
        val input = "(11) 98765-4321"
        val result = WhatsAppDispatcher.formatPhoneNumber(input, defaultCountryCode = "55")
        assertEquals("5511987654321", result)
    }

    @Test
    fun formatPhoneNumber_keepsExistingCountryCode_whenAlreadyPresent() {
        val input = "+55 11 98765-4321"
        val result = WhatsAppDispatcher.formatPhoneNumber(input, defaultCountryCode = "55")
        assertEquals("5511987654321", result)
    }

    @Test
    fun formatPhoneNumber_removesLeadingZeroes() {
        val input = "011987654321"
        val result = WhatsAppDispatcher.formatPhoneNumber(input, defaultCountryCode = "55")
        assertEquals("5511987654321", result)
    }

    @Test
    fun formatPhoneNumber_returnsEmpty_forInvalidInput() {
        val input = "abc---"
        val result = WhatsAppDispatcher.formatPhoneNumber(input, defaultCountryCode = "55")
        assertEquals("", result)
    }
}
