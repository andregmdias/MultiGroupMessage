package com.giannatech.multigroupmessage.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.giannatech.multigroupmessage.data.local.entity.GroupContactCrossRef
import com.giannatech.multigroupmessage.data.local.entity.GroupEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupWithContacts
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    @Transaction
    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun getAllGroupsWithContacts(): Flow<List<GroupWithContacts>>

    @Transaction
    @Query("SELECT * FROM groups WHERE id = :groupId")
    suspend fun getGroupWithContacts(groupId: Long): GroupWithContacts?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity): Long

    @Update
    suspend fun updateGroup(group: GroupEntity)

    @Delete
    suspend fun deleteGroup(group: GroupEntity)

    @Query("DELETE FROM groups WHERE id = :groupId")
    suspend fun deleteGroupById(groupId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupContactCrossRef(crossRef: GroupContactCrossRef)

    @Query("DELETE FROM group_contact_cross_ref WHERE groupId = :groupId AND contactId = :contactId")
    suspend fun removeContactFromGroup(groupId: Long, contactId: Long)

    @Query("DELETE FROM group_contact_cross_ref WHERE groupId = :groupId")
    suspend fun clearGroupContacts(groupId: Long)
}
