package com.giannatech.multigroupmessage.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dispatch_logs")
data class DispatchLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val campaignId: Long,
    val contactName: String,
    val phoneNumber: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)
