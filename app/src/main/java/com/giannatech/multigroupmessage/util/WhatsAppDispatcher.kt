package com.giannatech.multigroupmessage.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.giannatech.multigroupmessage.data.preferences.TargetApp
import java.net.URLEncoder

object WhatsAppDispatcher {

    fun formatPhoneNumber(phone: String, defaultCountryCode: String = "55"): String {
        var digits = phone.filter { it.isDigit() }
        if (digits.isEmpty()) return ""

        digits = digits.trimStart('0')

        if ((digits.length == 10 || digits.length == 11) && !digits.startsWith(defaultCountryCode)) {
            digits = "$defaultCountryCode$digits"
        }

        return digits
    }

    suspend fun dispatchViaCloudApi(
        phoneNumberId: String,
        accessToken: String,
        phoneNumber: String,
        message: String,
        defaultCountryCode: String = "55"
    ): CloudApiResponse {
        val cleanPhone = formatPhoneNumber(phoneNumber, defaultCountryCode)
        if (cleanPhone.isEmpty()) {
            return CloudApiResponse(success = false, errorMessage = "Número de telefone inválido: $phoneNumber")
        }

        return WhatsAppCloudApiClient.sendTextMessage(
            phoneNumberId = phoneNumberId,
            accessToken = accessToken,
            toPhoneNumber = cleanPhone,
            messageText = message
        )
    }

    fun dispatchMessage(
        context: Context,
        phoneNumber: String,
        message: String,
        targetApp: TargetApp,
        defaultCountryCode: String = "55"
    ): Boolean {
        val cleanPhone = formatPhoneNumber(phoneNumber, defaultCountryCode)
        if (cleanPhone.isEmpty()) {
            Toast.makeText(context, "Número inválido: $phoneNumber", Toast.LENGTH_SHORT).show()
            return false
        }

        return try {
            val encodedMsg = URLEncoder.encode(message, "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=$encodedMsg")

            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage(targetApp.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val packageManager = context.packageManager
            if (intent.resolveActivity(packageManager) != null) {
                context.startActivity(intent)
                true
            } else {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (fallbackIntent.resolveActivity(packageManager) != null) {
                    Toast.makeText(
                        context,
                        "${targetApp.displayName} não encontrado. Abrindo WhatsApp disponível.",
                        Toast.LENGTH_SHORT
                    ).show()
                    context.startActivity(fallbackIntent)
                    true
                } else {
                    Toast.makeText(
                        context,
                        "Nenhum aplicativo do WhatsApp está instalado no dispositivo.",
                        Toast.LENGTH_LONG
                    ).show()
                    false
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao abrir WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
