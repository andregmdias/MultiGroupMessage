package com.giannatech.multigroupmessage

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.giannatech.multigroupmessage.data.local.AppDatabase
import com.giannatech.multigroupmessage.data.local.entity.CampaignEntity
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity
import com.giannatech.multigroupmessage.data.local.entity.DispatchLogEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupContactCrossRef
import com.giannatech.multigroupmessage.data.local.entity.GroupEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndQueryContacts() = runBlocking {
        val contact = ContactEntity(name = "João Silva", phoneNumber = "5511987654321")
        val contactId = db.contactDao().insertContact(contact)

        val retrieved = db.contactDao().getContactById(contactId)
        assertNotNull(retrieved)
        assertEquals("João Silva", retrieved?.name)
        assertEquals("5511987654321", retrieved?.phoneNumber)
    }

    @Test
    fun createGroupWithContacts() = runBlocking {
        val c1Id = db.contactDao().insertContact(ContactEntity(name = "Maria", phoneNumber = "5511911112222"))
        val c2Id = db.contactDao().insertContact(ContactEntity(name = "Pedro", phoneNumber = "5511933334444"))

        val groupId = db.groupDao().insertGroup(GroupEntity(name = "Clientes VIP", colorHex = "#00E5FF"))
        db.groupDao().insertGroupContactCrossRef(GroupContactCrossRef(groupId, c1Id))
        db.groupDao().insertGroupContactCrossRef(GroupContactCrossRef(groupId, c2Id))

        val groupWithContacts = db.groupDao().getGroupWithContacts(groupId)
        assertNotNull(groupWithContacts)
        assertEquals("Clientes VIP", groupWithContacts?.group?.name)
        assertEquals(2, groupWithContacts?.contacts?.size)
    }

    @Test
    fun createCampaignAndRecordDispatchLogs() = runBlocking {
        val campaignId = db.campaignDao().insertCampaign(
            CampaignEntity(
                title = "Promoção de Sexta",
                messageText = "Olá! Confira nossas ofertas.",
                targetApp = "WHATSAPP_BUSINESS",
                totalRecipients = 2,
                sentCount = 2
            )
        )

        db.campaignDao().insertDispatchLog(
            DispatchLogEntity(campaignId = campaignId, contactName = "Maria", phoneNumber = "5511911112222", status = "SENT")
        )
        db.campaignDao().insertDispatchLog(
            DispatchLogEntity(campaignId = campaignId, contactName = "Pedro", phoneNumber = "5511933334444", status = "SENT")
        )

        val totalCampaigns = db.campaignDao().getTotalCampaignsCount().first()
        val totalSent = db.campaignDao().getTotalMessagesSentCount().first()
        val uniquePeople = db.campaignDao().getTotalUniquePeopleReachedCount().first()

        assertEquals(1, totalCampaigns)
        assertEquals(2, totalSent)
        assertEquals(2, uniquePeople)
    }
}
