package com.giannatech.multigroupmessage.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "group_contact_cross_ref",
    primaryKeys = ["groupId", "contactId"]
)
data class GroupContactCrossRef(
    val groupId: Long,
    val contactId: Long
)
