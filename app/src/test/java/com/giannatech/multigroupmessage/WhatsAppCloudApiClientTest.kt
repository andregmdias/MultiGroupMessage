package com.giannatech.multigroupmessage

import com.giannatech.multigroupmessage.util.CloudApiResponse
import com.giannatech.multigroupmessage.util.WhatsAppCloudApiClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatsAppCloudApiClientTest {

    @Test
    fun sendTextMessage_fails_whenPhoneNumberIdIsBlank() = runBlocking {
        val result = WhatsAppCloudApiClient.sendTextMessage(
            phoneNumberId = "",
            accessToken = "EAA...",
            toPhoneNumber = "5511999998888",
            messageText = "Teste"
        )

        assertFalse(result.success)
        assertEquals("ID do Número de Telefone (Phone Number ID) não configurado.", result.errorMessage)
    }

    @Test
    fun sendTextMessage_fails_whenAccessTokenIsBlank() = runBlocking {
        val result = WhatsAppCloudApiClient.sendTextMessage(
            phoneNumberId = "123456",
            accessToken = "",
            toPhoneNumber = "5511999998888",
            messageText = "Teste"
        )

        assertFalse(result.success)
        assertEquals("Token de Acesso (Access Token) não configurado.", result.errorMessage)
    }

    @Test
    fun cloudApiResponse_dataClass_behavesCorrectly() {
        val successResponse = CloudApiResponse(success = true, messageId = "wamid.123")
        assertTrue(successResponse.success)
        assertEquals("wamid.123", successResponse.messageId)
        assertNull(successResponse.errorMessage)

        val errorResponse = CloudApiResponse(success = false, errorMessage = "Erro 401")
        assertFalse(errorResponse.success)
        assertNull(errorResponse.messageId)
        assertEquals("Erro 401", errorResponse.errorMessage)
    }
}
