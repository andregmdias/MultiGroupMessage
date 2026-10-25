package com.giannatech.multigroupmessage.util

import android.content.Context
import android.provider.ContactsContract
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity

object DeviceContactsHelper {
    fun fetchDeviceContacts(context: Context, defaultCountryCode: String = "55"): List<ContactEntity> {
        val contactsList = mutableListOf<ContactEntity>()
        val seenNumbers = mutableSetOf<String>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = if (nameIndex >= 0) it.getString(nameIndex) ?: "Sem nome" else "Sem nome"
                val rawNumber = if (numberIndex >= 0) it.getString(numberIndex) ?: "" else ""

                val sanitized = WhatsAppDispatcher.formatPhoneNumber(rawNumber, defaultCountryCode)
                if (sanitized.isNotEmpty() && !seenNumbers.contains(sanitized)) {
                    seenNumbers.add(sanitized)
                    contactsList.add(
                        ContactEntity(
                            name = name,
                            phoneNumber = sanitized,
                            notes = "Importado da agenda",
                            isFromDevice = true
                        )
                    )
                }
            }
        }

        return contactsList
    }
}
