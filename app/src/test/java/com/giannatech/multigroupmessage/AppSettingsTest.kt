package com.giannatech.multigroupmessage

import android.content.ContextWrapper
import android.content.SharedPreferences
import com.giannatech.multigroupmessage.data.preferences.AppSettings
import com.giannatech.multigroupmessage.data.preferences.DispatchMode
import com.giannatech.multigroupmessage.data.preferences.TargetApp
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AppSettingsTest {

    private class FakeSharedPreferences : SharedPreferences {
        private val map = mutableMapOf<String, Any?>()

        override fun getAll(): MutableMap<String, *> = map
        override fun getString(key: String?, defValue: String?): String? = (map[key] as? String) ?: defValue
        override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? = defValues
        override fun getInt(key: String?, defValue: Int): Int = (map[key] as? Int) ?: defValue
        override fun getLong(key: String?, defValue: Long): Long = (map[key] as? Long) ?: defValue
        override fun getFloat(key: String?, defValue: Float): Float = (map[key] as? Float) ?: defValue
        override fun getBoolean(key: String?, defValue: Boolean): Boolean = (map[key] as? Boolean) ?: defValue
        override fun contains(key: String?): Boolean = map.containsKey(key)
        override fun edit(): SharedPreferences.Editor = FakeEditor(map)
        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

        private class FakeEditor(private val map: MutableMap<String, Any?>) : SharedPreferences.Editor {
            override fun putString(key: String?, value: String?): SharedPreferences.Editor { map[key ?: ""] = value; return this }
            override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor = this
            override fun putInt(key: String?, value: Int): SharedPreferences.Editor = this
            override fun putLong(key: String?, value: Long): SharedPreferences.Editor = this
            override fun putFloat(key: String?, value: Float): SharedPreferences.Editor = this
            override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor = this
            override fun remove(key: String?): SharedPreferences.Editor = this
            override fun clear(): SharedPreferences.Editor = this
            override fun commit(): Boolean = true
            override fun apply() {}
        }
    }

    private class FakeContext(private val prefs: SharedPreferences) : ContextWrapper(null) {
        override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences = prefs
    }

    private lateinit var appSettings: AppSettings

    @Before
    fun setup() {
        val fakePrefs = FakeSharedPreferences()
        val context = FakeContext(fakePrefs)
        appSettings = AppSettings(context)
    }

    @Test
    fun testTargetAppEnumValues() {
        assertEquals("com.whatsapp", TargetApp.WHATSAPP.packageName)
        assertEquals("WhatsApp", TargetApp.WHATSAPP.displayName)
        assertEquals("com.whatsapp.w4b", TargetApp.WHATSAPP_BUSINESS.packageName)
        assertEquals("WhatsApp Business", TargetApp.WHATSAPP_BUSINESS.displayName)
    }

    @Test
    fun testSetTargetApp() {
        appSettings.setTargetApp(TargetApp.WHATSAPP)
        assertEquals(TargetApp.WHATSAPP, appSettings.targetApp.value)

        appSettings.setTargetApp(TargetApp.WHATSAPP_BUSINESS)
        assertEquals(TargetApp.WHATSAPP_BUSINESS, appSettings.targetApp.value)
    }

    @Test
    fun testSetDefaultCountryCode() {
        appSettings.setDefaultCountryCode("+55")
        assertEquals("55", appSettings.defaultCountryCode.value)

        appSettings.setDefaultCountryCode("1")
        assertEquals("1", appSettings.defaultCountryCode.value)
    }

    @Test
    fun testCloudApiPreferencesAndDispatchMode() {
        assertEquals(DispatchMode.CLOUD_API, appSettings.dispatchMode.value)

        appSettings.setDispatchMode(DispatchMode.INTENT_NATIVE)
        assertEquals(DispatchMode.INTENT_NATIVE, appSettings.dispatchMode.value)

        appSettings.setCloudApiPhoneNumberId(" 123456789 ")
        assertEquals("123456789", appSettings.cloudApiPhoneNumberId.value)

        appSettings.setCloudApiAccessToken(" EAA123 ")
        assertEquals("EAA123", appSettings.cloudApiAccessToken.value)
    }
}
