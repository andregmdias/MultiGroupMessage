package com.giannatech.multigroupmessage.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class CloudApiResponse(
    val success: Boolean,
    val messageId: String? = null,
    val errorMessage: String? = null
)

object WhatsAppCloudApiClient {

    private const val BASE_GRAPH_URL = "https://graph.facebook.com/v22.0"

    suspend fun sendTextMessage(
        phoneNumberId: String,
        accessToken: String,
        toPhoneNumber: String,
        messageText: String
    ): CloudApiResponse = withContext(Dispatchers.IO) {
        if (phoneNumberId.isBlank()) {
            return@withContext CloudApiResponse(
                success = false,
                errorMessage = "ID do Número de Telefone (Phone Number ID) não configurado."
            )
        }
        if (accessToken.isBlank()) {
            return@withContext CloudApiResponse(
                success = false,
                errorMessage = "Token de Acesso (Access Token) não configurado."
            )
        }

        val endpointUrl = "$BASE_GRAPH_URL/$phoneNumberId/messages"

        try {
            val url = URL(endpointUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $accessToken")
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                doInput = true
                connectTimeout = 15000
                readTimeout = 15000
            }

            // Escaping JSON body safely
            val escapedMessage = escapeJsonString(messageText)
            val jsonPayload = """
                {
                  "messaging_product": "whatsapp",
                  "recipient_type": "individual",
                  "to": "$toPhoneNumber",
                  "type": "text",
                  "text": {
                    "preview_url": false,
                    "body": "$escapedMessage"
                  }
                }
            """.trimIndent()

            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(jsonPayload)
                writer.flush()
            }

            val responseCode = connection.responseCode
            val isSuccess = responseCode in 200..299

            val inputStream = if (isSuccess) connection.inputStream else connection.errorStream
            val responseText = BufferedReader(InputStreamReader(inputStream, "UTF-8")).use { it.readText() }

            if (isSuccess) {
                val messageId = extractJsonValue(responseText, "id") ?: "wamid.success"
                CloudApiResponse(success = true, messageId = messageId)
            } else {
                val errorDetails = extractJsonValue(responseText, "message")
                    ?: extractJsonValue(responseText, "error_data")
                    ?: "Erro HTTP $responseCode ao se comunicar com a Meta Graph API."
                CloudApiResponse(success = false, errorMessage = errorDetails)
            }
        } catch (e: Exception) {
            CloudApiResponse(success = false, errorMessage = "Falha de conexão com a API Meta: ${e.message}")
        }
    }

    private fun escapeJsonString(input: String): String {
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    private fun extractJsonValue(json: String, key: String): String? {
        val searchPattern = "\"$key\":\\s*\"([^\"]+)\""
        val regex = Regex(searchPattern)
        val match = regex.find(json)
        return match?.groupValues?.get(1)
    }
}
