package com.giannatech.multigroupmessage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "campaigns")
data class CampaignEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val messageText: String,
    val targetApp: String,
    val totalRecipients: Int,
    val sentCount: Int,
    val timestamp: Long = System.currentTimeMillis()
)
