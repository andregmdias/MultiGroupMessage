package com.giannatech.multigroupmessage.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class GroupWithContacts(
    @Embedded val group: GroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GroupContactCrossRef::class,
            parentColumn = "groupId",
            entityColumn = "contactId"
        )
    )
    val contacts: List<ContactEntity>
)
