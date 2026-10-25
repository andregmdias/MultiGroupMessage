package com.giannatech.multigroupmessage.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class TargetApp(val packageName: String, val displayName: String) {
    WHATSAPP("com.whatsapp", "WhatsApp"),
    WHATSAPP_BUSINESS("com.whatsapp.w4b", "WhatsApp Business")
}

enum class DispatchMode(val displayName: String) {
    CLOUD_API("API Oficial Meta (Automático)"),
    INTENT_NATIVE("Intent Nativo WhatsApp (Manual)")
}

class AppSettings(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("multigroupmessage_settings", Context.MODE_PRIVATE)

    private val _targetApp = MutableStateFlow(loadTargetApp())
    val targetApp: StateFlow<TargetApp> = _targetApp.asStateFlow()

    private val _defaultCountryCode = MutableStateFlow(loadCountryCode())
    val defaultCountryCode: StateFlow<String> = _defaultCountryCode.asStateFlow()

    private val _dispatchMode = MutableStateFlow(loadDispatchMode())
    val dispatchMode: StateFlow<DispatchMode> = _dispatchMode.asStateFlow()

    private val _cloudApiPhoneNumberId = MutableStateFlow(loadCloudApiPhoneNumberId())
    val cloudApiPhoneNumberId: StateFlow<String> = _cloudApiPhoneNumberId.asStateFlow()

    private val _cloudApiAccessToken = MutableStateFlow(loadCloudApiAccessToken())
    val cloudApiAccessToken: StateFlow<String> = _cloudApiAccessToken.asStateFlow()

    private fun loadTargetApp(): TargetApp {
        val name = prefs.getString("KEY_TARGET_APP", TargetApp.WHATSAPP_BUSINESS.name)
        return try {
            TargetApp.valueOf(name ?: TargetApp.WHATSAPP_BUSINESS.name)
        } catch (e: Exception) {
            TargetApp.WHATSAPP_BUSINESS
        }
    }

    private fun loadCountryCode(): String {
        return prefs.getString("KEY_COUNTRY_CODE", "55") ?: "55"
    }

    private fun loadDispatchMode(): DispatchMode {
        val name = prefs.getString("KEY_DISPATCH_MODE", DispatchMode.CLOUD_API.name)
        return try {
            DispatchMode.valueOf(name ?: DispatchMode.CLOUD_API.name)
        } catch (e: Exception) {
            DispatchMode.CLOUD_API
        }
    }

    private fun loadCloudApiPhoneNumberId(): String {
        return prefs.getString("KEY_CLOUD_PHONE_NUMBER_ID", "") ?: ""
    }

    private fun loadCloudApiAccessToken(): String {
        return prefs.getString("KEY_CLOUD_ACCESS_TOKEN", "") ?: ""
    }

    fun setTargetApp(targetApp: TargetApp) {
        prefs.edit().putString("KEY_TARGET_APP", targetApp.name).apply()
        _targetApp.value = targetApp
    }

    fun setDefaultCountryCode(code: String) {
        val cleanCode = code.replace("+", "").trim()
        prefs.edit().putString("KEY_COUNTRY_CODE", cleanCode).apply()
        _defaultCountryCode.value = cleanCode
    }

    fun setDispatchMode(mode: DispatchMode) {
        prefs.edit().putString("KEY_DISPATCH_MODE", mode.name).apply()
        _dispatchMode.value = mode
    }

    fun setCloudApiPhoneNumberId(id: String) {
        val trimmed = id.trim()
        prefs.edit().putString("KEY_CLOUD_PHONE_NUMBER_ID", trimmed).apply()
        _cloudApiPhoneNumberId.value = trimmed
    }

    fun setCloudApiAccessToken(token: String) {
        val trimmed = token.trim()
        prefs.edit().putString("KEY_CLOUD_ACCESS_TOKEN", trimmed).apply()
        _cloudApiAccessToken.value = trimmed
    }
}
