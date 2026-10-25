package com.giannatech.multigroupmessage

import com.giannatech.multigroupmessage.data.local.dao.CampaignDao
import com.giannatech.multigroupmessage.data.local.dao.ContactDao
import com.giannatech.multigroupmessage.data.local.dao.GroupDao
import com.giannatech.multigroupmessage.data.local.entity.CampaignEntity
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity
import com.giannatech.multigroupmessage.data.local.entity.DispatchLogEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupContactCrossRef
import com.giannatech.multigroupmessage.data.local.entity.GroupEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupWithContacts
import com.giannatech.multigroupmessage.data.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class AppRepositoryTest {

    private class FakeContactDao : ContactDao {
        val contacts = mutableListOf<ContactEntity>()
        private var nextId = 1L

        override fun getAllContacts(): Flow<List<ContactEntity>> = flowOf(contacts)
        override suspend fun getContactById(id: Long): ContactEntity? = contacts.find { it.id == id }
        override suspend fun getContactByPhone(phoneNumber: String): ContactEntity? = contacts.find { it.phoneNumber == phoneNumber }
        override suspend fun insertContact(contact: ContactEntity): Long {
            val id = if (contact.id == 0L) nextId++ else contact.id
            val saved = contact.copy(id = id)
            contacts.add(saved)
            return id
        }
        override suspend fun insertContacts(contactsList: List<ContactEntity>): List<Long> {
            return contactsList.map { insertContact(it) }
        }
        override suspend fun updateContact(contact: ContactEntity) {
            val index = contacts.indexOfFirst { it.id == contact.id }
            if (index != -1) contacts[index] = contact
        }
        override suspend fun deleteContact(contact: ContactEntity) {
            contacts.removeAll { it.id == contact.id }
        }
        override suspend fun deleteContactById(id: Long) {
            contacts.removeAll { it.id == id }
        }
    }

    private class FakeGroupDao : GroupDao {
        val groups = mutableListOf<GroupEntity>()
        val crossRefs = mutableListOf<GroupContactCrossRef>()
        private var nextId = 1L

        override fun getAllGroupsWithContacts(): Flow<List<GroupWithContacts>> = flowOf(
            groups.map { group ->
                GroupWithContacts(group = group, contacts = emptyList())
            }
        )
        override suspend fun getGroupWithContacts(groupId: Long): GroupWithContacts? {
            val g = groups.find { it.id == groupId } ?: return null
            return GroupWithContacts(group = g, contacts = emptyList())
        }
        override suspend fun insertGroup(group: GroupEntity): Long {
            val id = if (group.id == 0L) nextId++ else group.id
            val saved = group.copy(id = id)
            groups.add(saved)
            return id
        }
        override suspend fun updateGroup(group: GroupEntity) {
            val index = groups.indexOfFirst { it.id == group.id }
            if (index != -1) groups[index] = group
        }
        override suspend fun deleteGroup(group: GroupEntity) {
            groups.removeAll { it.id == group.id }
        }
        override suspend fun deleteGroupById(groupId: Long) {
            groups.removeAll { it.id == groupId }
        }
        override suspend fun insertGroupContactCrossRef(crossRef: GroupContactCrossRef) {
            crossRefs.add(crossRef)
        }
        override suspend fun removeContactFromGroup(groupId: Long, contactId: Long) {
            crossRefs.removeAll { it.groupId == groupId && it.contactId == contactId }
        }
        override suspend fun clearGroupContacts(groupId: Long) {
            crossRefs.removeAll { it.groupId == groupId }
        }
    }

    private class FakeCampaignDao : CampaignDao {
        val campaigns = mutableListOf<CampaignEntity>()
        val logs = mutableListOf<DispatchLogEntity>()
        private var nextCampaignId = 1L
        private var nextLogId = 1L

        override fun getAllCampaigns(): Flow<List<CampaignEntity>> = flowOf(campaigns)
        override suspend fun getCampaignById(campaignId: Long): CampaignEntity? = campaigns.find { it.id == campaignId }
        override suspend fun insertCampaign(campaign: CampaignEntity): Long {
            val id = if (campaign.id == 0L) nextCampaignId++ else campaign.id
            val saved = campaign.copy(id = id)
            campaigns.add(saved)
            return id
        }
        override suspend fun updateSentCount(campaignId: Long, sentCount: Int) {
            val index = campaigns.indexOfFirst { it.id == campaignId }
            if (index != -1) campaigns[index] = campaigns[index].copy(sentCount = sentCount)
        }
        override suspend fun insertDispatchLog(log: DispatchLogEntity): Long {
            val id = if (log.id == 0L) nextLogId++ else log.id
            val saved = log.copy(id = id)
            logs.add(saved)
            return id
        }
        override fun getDispatchLogsForCampaign(campaignId: Long): Flow<List<DispatchLogEntity>> =
            flowOf(logs.filter { it.campaignId == campaignId })
        override fun getTotalCampaignsCount(): Flow<Int> = flowOf(campaigns.size)
        override fun getTotalMessagesSentCount(): Flow<Int?> = flowOf(campaigns.sumOf { it.sentCount })
        override fun getTotalUniquePeopleReachedCount(): Flow<Int> =
            flowOf(logs.filter { it.status == "SENT" }.map { it.phoneNumber }.distinct().size)
    }

    private class FakeAppDatabase(
        val cDao: FakeContactDao,
        val gDao: FakeGroupDao,
        val campDao: FakeCampaignDao
    ) : com.giannatech.multigroupmessage.data.local.AppDatabase() {
        override fun contactDao(): ContactDao = cDao
        override fun groupDao(): GroupDao = gDao
        override fun campaignDao(): CampaignDao = campDao
        override fun clearAllTables() {}
        override fun createInvalidationTracker(): androidx.room.InvalidationTracker {
            return androidx.room.InvalidationTracker(this, "contacts", "groups", "group_contact_cross_ref", "campaigns", "dispatch_logs")
        }
    }

    private lateinit var repository: AppRepository
    private lateinit var fakeContactDao: FakeContactDao
    private lateinit var fakeGroupDao: FakeGroupDao
    private lateinit var fakeCampaignDao: FakeCampaignDao

    @Before
    fun setup() {
        fakeContactDao = FakeContactDao()
        fakeGroupDao = FakeGroupDao()
        fakeCampaignDao = FakeCampaignDao()
        val fakeDb = FakeAppDatabase(fakeContactDao, fakeGroupDao, fakeCampaignDao)
        repository = AppRepository(fakeDb)
    }

    @Test
    fun testContactRepositoryOperations() = runBlocking {
        val contact = ContactEntity(name = "Ana", phoneNumber = "5511999990000")
        val id = repository.insertContact(contact)
        assertEquals(1L, id)

        val all = repository.getAllContacts().first()
        assertEquals(1, all.size)
        assertEquals("Ana", all.first().name)

        val updated = all.first().copy(name = "Ana Maria")
        repository.updateContact(updated)

        val list2 = repository.insertContactsBatch(listOf(ContactEntity(name = "Beto", phoneNumber = "5511888880000")))
        assertEquals(1, list2.size)

        repository.deleteContact(updated)
        assertEquals(1, repository.getAllContacts().first().size)

        repository.deleteContactById(2L)
        assertEquals(0, repository.getAllContacts().first().size)
    }

    @Test
    fun testGroupRepositoryOperations() = runBlocking {
        val group = GroupEntity(name = "Vendas", description = "Equipe Vendas", colorHex = "#00E5FF")
        val groupId = repository.createGroup(group, listOf(10L, 20L))
        assertEquals(1L, groupId)

        val retrievedGroup = repository.getGroupWithContacts(groupId)
        assertNotNull(retrievedGroup)
        assertEquals("Vendas", retrievedGroup?.group?.name)

        repository.updateGroup(group.copy(id = groupId, name = "Vendas SP"), listOf(10L))
        val groupsAll = repository.getAllGroupsWithContacts().first()
        assertEquals(1, groupsAll.size)

        repository.deleteGroupById(groupId)
        assertEquals(0, repository.getAllGroupsWithContacts().first().size)
    }

    @Test
    fun testCampaignRepositoryOperations() = runBlocking {
        val campaign = CampaignEntity(title = "Campanha 1", messageText = "Olá!", targetApp = "WHATSAPP", totalRecipients = 5, sentCount = 0)
        val campId = repository.createCampaign(campaign)
        assertEquals(1L, campId)

        repository.updateCampaignSentCount(campId, 5)

        val log = DispatchLogEntity(campaignId = campId, contactName = "Carlos", phoneNumber = "5511777770000", status = "SENT")
        val logId = repository.recordDispatchLog(log)
        assertEquals(1L, logId)

        val logs = repository.getDispatchLogs(campId).first()
        assertEquals(1, logs.size)

        val campaignsAll = repository.getAllCampaigns().first()
        assertEquals(1, campaignsAll.size)

        val totalCampaigns = repository.getTotalCampaignsCount().first()
        assertEquals(1, totalCampaigns)

        val totalSent = repository.getTotalMessagesSentCount().first()
        assertEquals(5, totalSent)

        val uniquePeople = repository.getTotalUniquePeopleReachedCount().first()
        assertEquals(1, uniquePeople)
    }
}
