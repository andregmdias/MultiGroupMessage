package com.giannatech.multigroupmessage.data.repository

import com.giannatech.multigroupmessage.data.local.AppDatabase
import com.giannatech.multigroupmessage.data.local.entity.CampaignEntity
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity
import com.giannatech.multigroupmessage.data.local.entity.DispatchLogEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupContactCrossRef
import com.giannatech.multigroupmessage.data.local.entity.GroupEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupWithContacts
import kotlinx.coroutines.flow.Flow

class AppRepository(private val db: AppDatabase) {
    private val contactDao = db.contactDao()
    private val groupDao = db.groupDao()
    private val campaignDao = db.campaignDao()

    // Contacts
    fun getAllContacts(): Flow<List<ContactEntity>> = contactDao.getAllContacts()

    suspend fun insertContact(contact: ContactEntity): Long = contactDao.insertContact(contact)

    suspend fun insertContactsBatch(contacts: List<ContactEntity>): List<Long> =
        contactDao.insertContacts(contacts)

    suspend fun updateContact(contact: ContactEntity) = contactDao.updateContact(contact)

    suspend fun deleteContact(contact: ContactEntity) = contactDao.deleteContact(contact)

    suspend fun deleteContactById(id: Long) = contactDao.deleteContactById(id)

    // Groups
    fun getAllGroupsWithContacts(): Flow<List<GroupWithContacts>> =
        groupDao.getAllGroupsWithContacts()

    suspend fun getGroupWithContacts(groupId: Long): GroupWithContacts? =
        groupDao.getGroupWithContacts(groupId)

    suspend fun createGroup(group: GroupEntity, contactIds: List<Long>): Long {
        val groupId = groupDao.insertGroup(group)
        contactIds.forEach { contactId ->
            groupDao.insertGroupContactCrossRef(GroupContactCrossRef(groupId, contactId))
        }
        return groupId
    }

    suspend fun updateGroup(group: GroupEntity, contactIds: List<Long>) {
        groupDao.updateGroup(group)
        groupDao.clearGroupContacts(group.id)
        contactIds.forEach { contactId ->
            groupDao.insertGroupContactCrossRef(GroupContactCrossRef(group.id, contactId))
        }
    }

    suspend fun deleteGroup(group: GroupEntity) {
        groupDao.clearGroupContacts(group.id)
        groupDao.deleteGroup(group)
    }

    suspend fun deleteGroupById(groupId: Long) {
        groupDao.clearGroupContacts(groupId)
        groupDao.deleteGroupById(groupId)
    }

    // Campaigns & Analytics
    fun getAllCampaigns(): Flow<List<CampaignEntity>> = campaignDao.getAllCampaigns()

    suspend fun createCampaign(campaign: CampaignEntity): Long =
        campaignDao.insertCampaign(campaign)

    suspend fun updateCampaignSentCount(campaignId: Long, sentCount: Int) =
        campaignDao.updateSentCount(campaignId, sentCount)

    suspend fun recordDispatchLog(log: DispatchLogEntity): Long =
        campaignDao.insertDispatchLog(log)

    fun getDispatchLogs(campaignId: Long): Flow<List<DispatchLogEntity>> =
        campaignDao.getDispatchLogsForCampaign(campaignId)

    fun getTotalCampaignsCount(): Flow<Int> = campaignDao.getTotalCampaignsCount()

    fun getTotalMessagesSentCount(): Flow<Int?> = campaignDao.getTotalMessagesSentCount()

    fun getTotalUniquePeopleReachedCount(): Flow<Int> =
        campaignDao.getTotalUniquePeopleReachedCount()
}
