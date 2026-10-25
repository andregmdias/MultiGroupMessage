package com.giannatech.multigroupmessage

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import com.giannatech.multigroupmessage.data.local.dao.CampaignDao
import com.giannatech.multigroupmessage.data.local.dao.ContactDao
import com.giannatech.multigroupmessage.data.local.dao.GroupDao
import com.giannatech.multigroupmessage.data.local.entity.CampaignEntity
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity
import com.giannatech.multigroupmessage.data.local.entity.DispatchLogEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupContactCrossRef
import com.giannatech.multigroupmessage.data.local.entity.GroupEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupWithContacts
import com.giannatech.multigroupmessage.data.preferences.AppSettings
import com.giannatech.multigroupmessage.data.preferences.DispatchMode
import com.giannatech.multigroupmessage.data.preferences.TargetApp
import com.giannatech.multigroupmessage.data.repository.AppRepository
import com.giannatech.multigroupmessage.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

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

    private class FakeContext(prefs: SharedPreferences) : ContextWrapper(null) {
        private val p = prefs
        override fun getSharedPreferences(name: String?, mode: Int): SharedPreferences = p
        override fun getApplicationContext(): Context = this
    }

    private class FakeContactDao : ContactDao {
        val list = mutableListOf<ContactEntity>()
        private var idSeq = 1L
        override fun getAllContacts(): Flow<List<ContactEntity>> = flowOf(list)
        override suspend fun getContactById(id: Long): ContactEntity? = list.find { it.id == id }
        override suspend fun getContactByPhone(phoneNumber: String): ContactEntity? = list.find { it.phoneNumber == phoneNumber }
        override suspend fun insertContact(contact: ContactEntity): Long {
            val id = if (contact.id == 0L) idSeq++ else contact.id
            list.add(contact.copy(id = id))
            return id
        }
        override suspend fun insertContacts(contacts: List<ContactEntity>): List<Long> = contacts.map { insertContact(it) }
        override suspend fun updateContact(contact: ContactEntity) {}
        override suspend fun deleteContact(contact: ContactEntity) { list.removeAll { it.id == contact.id } }
        override suspend fun deleteContactById(id: Long) { list.removeAll { it.id == id } }
    }

    private class FakeGroupDao : GroupDao {
        val list = mutableListOf<GroupEntity>()
        private var idSeq = 1L
        override fun getAllGroupsWithContacts(): Flow<List<GroupWithContacts>> = flowOf(list.map { GroupWithContacts(it, emptyList()) })
        override suspend fun getGroupWithContacts(groupId: Long): GroupWithContacts? = list.find { it.id == groupId }?.let { GroupWithContacts(it, emptyList()) }
        override suspend fun insertGroup(group: GroupEntity): Long {
            val id = if (group.id == 0L) idSeq++ else group.id
            list.add(group.copy(id = id))
            return id
        }
        override suspend fun updateGroup(group: GroupEntity) {}
        override suspend fun deleteGroup(group: GroupEntity) { list.removeAll { it.id == group.id } }
        override suspend fun deleteGroupById(groupId: Long) { list.removeAll { it.id == groupId } }
        override suspend fun insertGroupContactCrossRef(crossRef: GroupContactCrossRef) {}
        override suspend fun removeContactFromGroup(groupId: Long, contactId: Long) {}
        override suspend fun clearGroupContacts(groupId: Long) {}
    }

    private class FakeCampaignDao : CampaignDao {
        val campaigns = mutableListOf<CampaignEntity>()
        val logs = mutableListOf<DispatchLogEntity>()
        private var campSeq = 1L
        private var logSeq = 1L

        override fun getAllCampaigns(): Flow<List<CampaignEntity>> = flowOf(campaigns)
        override suspend fun getCampaignById(campaignId: Long): CampaignEntity? = campaigns.find { it.id == campaignId }
        override suspend fun insertCampaign(campaign: CampaignEntity): Long {
            val id = if (campaign.id == 0L) campSeq++ else campaign.id
            campaigns.add(campaign.copy(id = id))
            return id
        }
        override suspend fun updateSentCount(campaignId: Long, sentCount: Int) {
            val idx = campaigns.indexOfFirst { it.id == campaignId }
            if (idx != -1) campaigns[idx] = campaigns[idx].copy(sentCount = sentCount)
        }
        override suspend fun insertDispatchLog(log: DispatchLogEntity): Long {
            val id = if (log.id == 0L) logSeq++ else log.id
            logs.add(log.copy(id = id))
            return id
        }
        override fun getDispatchLogsForCampaign(campaignId: Long): Flow<List<DispatchLogEntity>> = flowOf(logs.filter { it.campaignId == campaignId })
        override fun getTotalCampaignsCount(): Flow<Int> = flowOf(campaigns.size)
        override fun getTotalMessagesSentCount(): Flow<Int?> = flowOf(campaigns.sumOf { it.sentCount })
        override fun getTotalUniquePeopleReachedCount(): Flow<Int> = flowOf(logs.filter { it.status == "SENT" }.map { it.phoneNumber }.distinct().size)
    }

    private class FakeAppDatabase(
        val c: FakeContactDao, val g: FakeGroupDao, val camp: FakeCampaignDao
    ) : com.giannatech.multigroupmessage.data.local.AppDatabase() {
        override fun contactDao(): ContactDao = c
        override fun groupDao(): GroupDao = g
        override fun campaignDao(): CampaignDao = camp
        override fun clearAllTables() {}
        override fun createInvalidationTracker(): androidx.room.InvalidationTracker {
            return androidx.room.InvalidationTracker(this, "contacts", "groups", "group_contact_cross_ref", "campaigns", "dispatch_logs")
        }
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var appSettings: AppSettings
    private lateinit var repository: AppRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = FakeContext(FakeSharedPreferences())
        appSettings = AppSettings(context)
        val db = FakeAppDatabase(FakeContactDao(), FakeGroupDao(), FakeCampaignDao())
        repository = AppRepository(db)
        viewModel = MainViewModel(repository, appSettings)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testSettingsStateAndUpdates() {
        assertEquals(TargetApp.WHATSAPP_BUSINESS, viewModel.targetApp.value)
        assertEquals("55", viewModel.defaultCountryCode.value)

        viewModel.setTargetApp(TargetApp.WHATSAPP)
        assertEquals(TargetApp.WHATSAPP, viewModel.targetApp.value)

        viewModel.setDefaultCountryCode("1")
        assertEquals("1", viewModel.defaultCountryCode.value)

        viewModel.setDispatchMode(DispatchMode.CLOUD_API)
        assertEquals(DispatchMode.CLOUD_API, viewModel.dispatchMode.value)

        viewModel.setCloudApiPhoneNumberId("100200300")
        assertEquals("100200300", viewModel.cloudApiPhoneNumberId.value)

        viewModel.setCloudApiAccessToken("TOKEN123")
        assertEquals("TOKEN123", viewModel.cloudApiAccessToken.value)
    }

    @Test
    fun testContactOperations() = runTest(testDispatcher) {
        viewModel.addManualContact("Carlos", "11988887777", "VIP")
        testDispatcher.scheduler.advanceUntilIdle()

        val contacts = repository.getAllContacts().first()
        assertEquals(1, contacts.size)
        assertEquals("Carlos", contacts.first().name)
        assertEquals("5511988887777", contacts.first().phoneNumber)

        viewModel.deleteContact(contacts.first())
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, repository.getAllContacts().first().size)
    }

    @Test
    fun testGroupOperations() = runTest(testDispatcher) {
        viewModel.createGroup("Time Vendas", "Grupo de Vendas", "#00E5FF", listOf(1L, 2L))
        testDispatcher.scheduler.advanceUntilIdle()

        val groups = repository.getAllGroupsWithContacts().first()
        assertEquals(1, groups.size)
        assertEquals("Time Vendas", groups.first().group.name)

        viewModel.updateGroup(groups.first().group.copy(name = "Vendas SP"), listOf(1L))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteGroup(groups.first().group)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, repository.getAllGroupsWithContacts().first().size)
    }

    @Test
    fun testCampaignFlowAndDispatch() = runTest(testDispatcher) {
        val recipients = listOf(
            ContactEntity(id = 1L, name = "Mariana", phoneNumber = "5511911112222"),
            ContactEntity(id = 2L, name = "Lucas", phoneNumber = "5511933334444")
        )

        viewModel.startCampaign("Aviso Geral", "Olá pessoal!", recipients)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.activeCampaign.value)
        assertEquals(2, viewModel.dispatchLogs.value.size)

        viewModel.markRecipientSkipped(0)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("SKIPPED", viewModel.dispatchLogs.value[0].status)

        viewModel.finishCampaignSession()

        assertNull(viewModel.activeCampaign.value)
        assertEquals(0, viewModel.dispatchLogs.value.size)
    }
}
