package com.giannatech.multigroupmessage.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.giannatech.multigroupmessage.data.local.AppDatabase
import com.giannatech.multigroupmessage.data.local.entity.CampaignEntity
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity
import com.giannatech.multigroupmessage.data.local.entity.DispatchLogEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupWithContacts
import com.giannatech.multigroupmessage.data.preferences.AppSettings
import com.giannatech.multigroupmessage.data.preferences.DispatchMode
import com.giannatech.multigroupmessage.data.preferences.TargetApp
import com.giannatech.multigroupmessage.data.repository.AppRepository
import com.giannatech.multigroupmessage.util.DeviceContactsHelper
import com.giannatech.multigroupmessage.util.WhatsAppDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AppAnalytics(
    val totalCampaigns: Int = 0,
    val totalMessagesSent: Int = 0,
    val uniquePeopleReached: Int = 0,
    val avgPeoplePerCampaign: Float = 0f
)

class MainViewModel(
    private val repository: AppRepository,
    private val appSettings: AppSettings
) : ViewModel() {

    val targetApp: StateFlow<TargetApp> = appSettings.targetApp
    val defaultCountryCode: StateFlow<String> = appSettings.defaultCountryCode
    val dispatchMode: StateFlow<DispatchMode> = appSettings.dispatchMode
    val cloudApiPhoneNumberId: StateFlow<String> = appSettings.cloudApiPhoneNumberId
    val cloudApiAccessToken: StateFlow<String> = appSettings.cloudApiAccessToken

    val groupsWithContacts: StateFlow<List<GroupWithContacts>> =
        repository.getAllGroupsWithContacts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContacts: StateFlow<List<ContactEntity>> =
        repository.getAllContacts()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val campaigns: StateFlow<List<CampaignEntity>> =
        repository.getAllCampaigns()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analytics: StateFlow<AppAnalytics> = combine(
        repository.getTotalCampaignsCount(),
        repository.getTotalMessagesSentCount(),
        repository.getTotalUniquePeopleReachedCount()
    ) { campaignsCount, totalSent, uniquePeople ->
        val sent = totalSent ?: 0
        val avg = if (campaignsCount > 0) sent.toFloat() / campaignsCount else 0f
        AppAnalytics(
            totalCampaigns = campaignsCount,
            totalMessagesSent = sent,
            uniquePeopleReached = uniquePeople,
            avgPeoplePerCampaign = avg
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppAnalytics()
    )

    // Current active campaign dispatch state
    private val _activeCampaign = MutableStateFlow<CampaignEntity?>(null)
    val activeCampaign: StateFlow<CampaignEntity?> = _activeCampaign.asStateFlow()

    private val _dispatchLogs = MutableStateFlow<List<DispatchLogEntity>>(emptyList())
    val dispatchLogs: StateFlow<List<DispatchLogEntity>> = _dispatchLogs.asStateFlow()

    private val _isAutoDispatching = MutableStateFlow(false)
    val isAutoDispatching: StateFlow<Boolean> = _isAutoDispatching.asStateFlow()

    fun setTargetApp(app: TargetApp) {
        appSettings.setTargetApp(app)
    }

    fun setDefaultCountryCode(code: String) {
        appSettings.setDefaultCountryCode(code)
    }

    fun setDispatchMode(mode: DispatchMode) {
        appSettings.setDispatchMode(mode)
    }

    fun setCloudApiPhoneNumberId(id: String) {
        appSettings.setCloudApiPhoneNumberId(id)
    }

    fun setCloudApiAccessToken(token: String) {
        appSettings.setCloudApiAccessToken(token)
    }

    fun sendTestMessage(
        context: Context,
        targetPhone: String,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            val response = WhatsAppDispatcher.dispatchViaCloudApi(
                phoneNumberId = cloudApiPhoneNumberId.value,
                accessToken = cloudApiAccessToken.value,
                phoneNumber = targetPhone,
                message = "Envio de Teste MultiGroupMessage via Meta Graph API Oficial!",
                defaultCountryCode = defaultCountryCode.value
            )
            if (response.success) {
                onResult(true, "Mensagem de teste enviada com sucesso! ID: ${response.messageId}")
            } else {
                onResult(false, response.errorMessage ?: "Erro desconhecido na API Meta.")
            }
        }
    }

    // Contacts
    fun importDeviceContacts(context: Context) {
        viewModelScope.launch {
            val deviceContacts = DeviceContactsHelper.fetchDeviceContacts(
                context,
                appSettings.defaultCountryCode.value
            )
            if (deviceContacts.isNotEmpty()) {
                repository.insertContactsBatch(deviceContacts)
            }
        }
    }

    fun addManualContact(name: String, phone: String, notes: String = "") {
        viewModelScope.launch {
            val formattedPhone = WhatsAppDispatcher.formatPhoneNumber(
                phone,
                appSettings.defaultCountryCode.value
            )
            val contact = ContactEntity(
                name = name.ifBlank { "Sem nome" },
                phoneNumber = formattedPhone,
                notes = notes,
                isFromDevice = false
            )
            repository.insertContact(contact)
        }
    }

    fun deleteContact(contact: ContactEntity) {
        viewModelScope.launch {
            repository.deleteContact(contact)
        }
    }

    // Groups
    fun createGroup(name: String, description: String, colorHex: String, contactIds: List<Long>) {
        viewModelScope.launch {
            val group = GroupEntity(
                name = name.ifBlank { "Novo Grupo" },
                description = description,
                colorHex = colorHex
            )
            repository.createGroup(group, contactIds)
        }
    }

    fun updateGroup(group: GroupEntity, contactIds: List<Long>) {
        viewModelScope.launch {
            repository.updateGroup(group, contactIds)
        }
    }

    fun deleteGroup(group: GroupEntity) {
        viewModelScope.launch {
            repository.deleteGroup(group)
        }
    }

    // Campaign Dispatch Flow
    fun startCampaign(
        title: String,
        message: String,
        recipients: List<ContactEntity>,
        targetAppOverride: TargetApp? = null
    ) {
        viewModelScope.launch {
            val appToUse = targetAppOverride ?: appSettings.targetApp.value
            val campaign = CampaignEntity(
                title = title.ifBlank { "Disparo sem título" },
                messageText = message,
                targetApp = appToUse.name,
                totalRecipients = recipients.size,
                sentCount = 0
            )
            val campaignId = repository.createCampaign(campaign)
            val savedCampaign = campaign.copy(id = campaignId)

            val logs = recipients.map { contact ->
                DispatchLogEntity(
                    campaignId = campaignId,
                    contactName = contact.name,
                    phoneNumber = contact.phoneNumber,
                    status = "PENDING"
                )
            }

            _activeCampaign.value = savedCampaign
            _dispatchLogs.value = logs
        }
    }

    fun dispatchNextRecipient(
        context: Context,
        logIndex: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val currentLogs = _dispatchLogs.value
            if (logIndex !in currentLogs.indices) return@launch

            val targetLog = currentLogs[logIndex]
            val messageText = _activeCampaign.value?.messageText ?: ""

            if (dispatchMode.value == DispatchMode.CLOUD_API) {
                val cloudResponse = WhatsAppDispatcher.dispatchViaCloudApi(
                    phoneNumberId = cloudApiPhoneNumberId.value,
                    accessToken = cloudApiAccessToken.value,
                    phoneNumber = targetLog.phoneNumber,
                    message = messageText,
                    defaultCountryCode = defaultCountryCode.value
                )

                val newStatus = if (cloudResponse.success) "SENT" else "FAILED"
                val logId = repository.recordDispatchLog(
                    targetLog.copy(status = newStatus, timestamp = System.currentTimeMillis())
                )

                val updatedLogs = currentLogs.toMutableList()
                updatedLogs[logIndex] = targetLog.copy(id = logId, status = newStatus)
                _dispatchLogs.value = updatedLogs

                val sentCount = updatedLogs.count { it.status == "SENT" }
                val camp = _activeCampaign.value
                if (camp != null) {
                    repository.updateCampaignSentCount(camp.id, sentCount)
                    _activeCampaign.value = camp.copy(sentCount = sentCount)
                }

                if (cloudResponse.success) {
                    onSuccess()
                } else {
                    onError(cloudResponse.errorMessage ?: "Falha no envio via Meta API.")
                }
            } else {
                val appToUse = try {
                    TargetApp.valueOf(_activeCampaign.value?.targetApp ?: appSettings.targetApp.value.name)
                } catch (e: Exception) {
                    appSettings.targetApp.value
                }

                val launched = WhatsAppDispatcher.dispatchMessage(
                    context = context,
                    phoneNumber = targetLog.phoneNumber,
                    message = messageText,
                    targetApp = appToUse,
                    defaultCountryCode = appSettings.defaultCountryCode.value
                )

                val newStatus = if (launched) "SENT" else "FAILED"
                val logId = repository.recordDispatchLog(
                    targetLog.copy(status = newStatus, timestamp = System.currentTimeMillis())
                )

                val updatedLogs = currentLogs.toMutableList()
                updatedLogs[logIndex] = targetLog.copy(id = logId, status = newStatus)
                _dispatchLogs.value = updatedLogs

                val sentCount = updatedLogs.count { it.status == "SENT" }
                val camp = _activeCampaign.value
                if (camp != null) {
                    repository.updateCampaignSentCount(camp.id, sentCount)
                    _activeCampaign.value = camp.copy(sentCount = sentCount)
                }

                if (launched) onSuccess() else onError("WhatsApp não respondeu ao Intent.")
            }
        }
    }

    fun startAutoBatchDispatch(
        context: Context,
        onComplete: () -> Unit
    ) {
        if (_isAutoDispatching.value) return
        _isAutoDispatching.value = true

        viewModelScope.launch {
            val currentLogs = _dispatchLogs.value
            for (i in currentLogs.indices) {
                if (_dispatchLogs.value[i].status == "PENDING") {
                    dispatchNextRecipient(
                        context = context,
                        logIndex = i,
                        onSuccess = {},
                        onError = {}
                    )
                    delay(500) // Delay to throttle API calls smoothly
                }
            }
            _isAutoDispatching.value = false
            onComplete()
        }
    }

    fun markRecipientSkipped(logIndex: Int) {
        val currentLogs = _dispatchLogs.value
        if (logIndex !in currentLogs.indices) return

        val targetLog = currentLogs[logIndex]
        viewModelScope.launch {
            val logId = repository.recordDispatchLog(
                targetLog.copy(status = "SKIPPED", timestamp = System.currentTimeMillis())
            )
            val updatedLogs = currentLogs.toMutableList()
            updatedLogs[logIndex] = targetLog.copy(id = logId, status = "SKIPPED")
            _dispatchLogs.value = updatedLogs
        }
    }

    fun finishCampaignSession() {
        _activeCampaign.value = null
        _dispatchLogs.value = emptyList()
        _isAutoDispatching.value = false
    }

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getInstance(context)
            val repo = AppRepository(db)
            val settings = AppSettings(context)
            return MainViewModel(repo, settings) as T
        }
    }
}
