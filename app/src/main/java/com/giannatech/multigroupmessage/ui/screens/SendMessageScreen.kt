package com.giannatech.multigroupmessage.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity
import com.giannatech.multigroupmessage.data.preferences.DispatchMode
import com.giannatech.multigroupmessage.data.preferences.TargetApp
import com.giannatech.multigroupmessage.ui.MainViewModel
import com.giannatech.multigroupmessage.ui.components.Flip7Badge
import com.giannatech.multigroupmessage.ui.components.Flip7Button
import com.giannatech.multigroupmessage.ui.components.Flip7Card
import com.giannatech.multigroupmessage.ui.components.Flip7OutlinedButton
import com.giannatech.multigroupmessage.ui.theme.Flip7Background
import com.giannatech.multigroupmessage.ui.theme.Flip7Border
import com.giannatech.multigroupmessage.ui.theme.Flip7CyberGold
import com.giannatech.multigroupmessage.ui.theme.Flip7ElectricCyan
import com.giannatech.multigroupmessage.ui.theme.Flip7NeonGreen
import com.giannatech.multigroupmessage.ui.theme.Flip7Surface
import com.giannatech.multigroupmessage.ui.theme.Flip7SurfaceVariant
import com.giannatech.multigroupmessage.ui.theme.Flip7TextMuted
import com.giannatech.multigroupmessage.ui.theme.Flip7TextPrimary
import com.giannatech.multigroupmessage.ui.theme.Flip7TextSecondary
import com.giannatech.multigroupmessage.ui.theme.Flip7VividCrimson
import com.giannatech.multigroupmessage.ui.theme.Flip7WhatsAppGreen

@Composable
fun SendMessageScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val defaultTargetApp by viewModel.targetApp.collectAsState()
    val dispatchMode by viewModel.dispatchMode.collectAsState()
    val isAutoDispatching by viewModel.isAutoDispatching.collectAsState()
    val groups by viewModel.groupsWithContacts.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()

    val activeCampaign by viewModel.activeCampaign.collectAsState()
    val dispatchLogs by viewModel.dispatchLogs.collectAsState()

    var title by remember { mutableStateOf("") }
    var messageText by remember { mutableStateOf("") }
    var selectedTargetApp by remember(defaultTargetApp) { mutableStateOf(defaultTargetApp) }

    val selectedGroupIds = remember { mutableStateListOf<Long>() }
    val selectedContactIds = remember { mutableStateListOf<Long>() }

    val finalRecipients = remember(selectedGroupIds.toList(), selectedContactIds.toList(), groups, allContacts) {
        val recipientMap = mutableMapOf<String, ContactEntity>()

        groups.filter { selectedGroupIds.contains(it.group.id) }.forEach { groupWithContacts ->
            groupWithContacts.contacts.forEach { contact ->
                if (contact.phoneNumber.isNotBlank()) {
                    recipientMap[contact.phoneNumber] = contact
                }
            }
        }

        allContacts.filter { selectedContactIds.contains(it.id) }.forEach { contact ->
            if (contact.phoneNumber.isNotBlank()) {
                recipientMap[contact.phoneNumber] = contact
            }
        }

        recipientMap.values.toList()
    }

    // Active Campaign Dispatch Modal state
    if (activeCampaign != null) {
        val logs = dispatchLogs
        val campaign = activeCampaign!!

        var currentLogIndex by remember { mutableIntStateOf(0) }

        val pendingIndex = logs.indexOfFirst { it.status == "PENDING" }
        val activeIndex = if (pendingIndex != -1) pendingIndex else currentLogIndex.coerceIn(0, (logs.size - 1).coerceAtLeast(0))

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Flip7Background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Dispatch Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DISPARO DE CAMPANHA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Flip7NeonGreen,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = campaign.title,
                            fontSize = 14.sp,
                            color = Flip7TextSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    val badgeText = if (dispatchMode == DispatchMode.CLOUD_API) "API Meta (Automático)" else selectedTargetApp.displayName
                    val badgeColor = if (dispatchMode == DispatchMode.CLOUD_API) Flip7NeonGreen else if (selectedTargetApp == TargetApp.WHATSAPP_BUSINESS) Flip7VividCrimson else Flip7WhatsAppGreen

                    Flip7Badge(
                        text = badgeText,
                        color = badgeColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                val completedCount = logs.count { it.status == "SENT" || it.status == "SKIPPED" || it.status == "FAILED" }
                val progressRatio = if (logs.isNotEmpty()) completedCount.toFloat() / logs.size else 0f

                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Flip7NeonGreen,
                    trackColor = Flip7SurfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progresso: $completedCount de ${logs.size} enviados",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                    Text(
                        text = "${(progressRatio * 100).toInt()}%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Flip7NeonGreen
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Auto Batch Dispatch Banner if Cloud API mode is active
                if (dispatchMode == DispatchMode.CLOUD_API && completedCount < logs.size) {
                    Flip7Card(
                        borderColor = Flip7NeonGreen,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Cloud, contentDescription = null, tint = Flip7NeonGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Disparo Automático Meta Graph API", fontWeight = FontWeight.Bold, color = Flip7TextPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("As mensagens serão enviadas sequencialmente em segundo plano sem abrir o app.", fontSize = 12.sp, color = Flip7TextMuted)
                            Spacer(modifier = Modifier.height(12.dp))

                            Flip7Button(
                                onClick = {
                                    viewModel.startAutoBatchDispatch(context) {
                                        Toast.makeText(context, "Disparo em lote concluído com sucesso!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                enabled = !isAutoDispatching,
                                containerColor = Flip7NeonGreen,
                                contentColor = Color.Black,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isAutoDispatching) "DISPARANDO EM LOTE..." else "DISPARAR TUDO AUTOMATICAMENTE", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Current Target Recipient Spotlight Card
                if (activeIndex in logs.indices && completedCount < logs.size) {
                    val currentLog = logs[activeIndex]
                    Flip7Card(
                        borderColor = Flip7ElectricCyan,
                        backgroundColor = Flip7SurfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "DESTINATÁRIO ATUAL (${activeIndex + 1}/${logs.size})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Flip7TextMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentLog.contactName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Flip7TextPrimary
                            )
                            Text(
                                text = currentLog.phoneNumber,
                                fontSize = 15.sp,
                                color = Flip7ElectricCyan,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Flip7Button(
                                    onClick = {
                                        viewModel.dispatchNextRecipient(
                                            context = context,
                                            logIndex = activeIndex,
                                            onSuccess = {
                                                val msg = if (dispatchMode == DispatchMode.CLOUD_API) "Enviado via Meta Cloud API!" else "Abrindo ${selectedTargetApp.displayName}..."
                                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { err ->
                                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    },
                                    containerColor = Flip7ElectricCyan,
                                    contentColor = Color.Black,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("ENVIAR UNIDADE", fontWeight = FontWeight.Bold)
                                }

                                Flip7OutlinedButton(
                                    onClick = {
                                        viewModel.markRecipientSkipped(activeIndex)
                                    },
                                    borderColor = Flip7CyberGold,
                                    contentColor = Flip7CyberGold,
                                    modifier = Modifier.width(110.dp)
                                ) {
                                    Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PULAR", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // All Completed Card
                    Flip7Card(
                        borderColor = Flip7CyberGold,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Flip7CyberGold,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Campanha Concluída!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Flip7TextPrimary
                            )
                            Text(
                                text = "Total de $completedCount mensagens disparadas com sucesso.",
                                fontSize = 13.sp,
                                color = Flip7TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dispatch Logs List
                Text(
                    text = "Fila de Envio",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Flip7TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(logs) { log ->
                        val statusColor = when (log.status) {
                            "SENT" -> Flip7NeonGreen
                            "SKIPPED" -> Flip7CyberGold
                            "FAILED" -> Flip7VividCrimson
                            else -> Flip7TextMuted
                        }
                        val statusText = when (log.status) {
                            "SENT" -> "Enviado"
                            "SKIPPED" -> "Pulado"
                            "FAILED" -> "Falha"
                            else -> "Pendente"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Flip7Surface)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = log.contactName, fontSize = 14.sp, color = Flip7TextPrimary, fontWeight = FontWeight.Medium)
                                Text(text = log.phoneNumber, fontSize = 12.sp, color = Flip7TextMuted)
                            }
                            Flip7Badge(text = statusText, color = statusColor)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Flip7Button(
                    onClick = { viewModel.finishCampaignSession() },
                    containerColor = Flip7CyberGold,
                    contentColor = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CONCLUIR SESSÃO", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    // Main Send Message Form View
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "NOVA MENSAGEM EM LOTE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Flip7NeonGreen,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Monte sua mensagem e selecione os destinatários",
                    fontSize = 12.sp,
                    color = Flip7TextSecondary
                )
            }
        }

        // Title Input
        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título da Campanha / Identificador") },
                placeholder = { Text("Ex: Promoção de Sexta") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Flip7SurfaceVariant,
                    unfocusedContainerColor = Flip7SurfaceVariant,
                    focusedBorderColor = Flip7NeonGreen,
                    unfocusedBorderColor = Flip7Border,
                    focusedTextColor = Flip7TextPrimary,
                    unfocusedTextColor = Flip7TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Message Content Input Area
        item {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                label = { Text("Conteúdo da Mensagem") },
                placeholder = { Text("Digite aqui o texto da mensagem que será enviada para o WhatsApp de cada contato...") },
                minLines = 4,
                maxLines = 8,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Flip7SurfaceVariant,
                    unfocusedContainerColor = Flip7SurfaceVariant,
                    focusedBorderColor = Flip7NeonGreen,
                    unfocusedBorderColor = Flip7Border,
                    focusedTextColor = Flip7TextPrimary,
                    unfocusedTextColor = Flip7TextPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Target App Selector
        item {
            Flip7Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "APLICATIVO ALVO DE DISPARO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Flip7TextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedTargetApp = TargetApp.WHATSAPP_BUSINESS }
                        ) {
                            RadioButton(
                                selected = selectedTargetApp == TargetApp.WHATSAPP_BUSINESS,
                                onClick = { selectedTargetApp = TargetApp.WHATSAPP_BUSINESS },
                                colors = RadioButtonDefaults.colors(selectedColor = Flip7VividCrimson)
                            )
                            Text("WhatsApp Business", fontSize = 13.sp, color = Flip7TextPrimary, fontWeight = FontWeight.Medium)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { selectedTargetApp = TargetApp.WHATSAPP }
                        ) {
                            RadioButton(
                                selected = selectedTargetApp == TargetApp.WHATSAPP,
                                onClick = { selectedTargetApp = TargetApp.WHATSAPP },
                                colors = RadioButtonDefaults.colors(selectedColor = Flip7WhatsAppGreen)
                            )
                            Text("WhatsApp", fontSize = 13.sp, color = Flip7TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Select Groups Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SELECIONAR GRUPOS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Flip7ElectricCyan,
                    letterSpacing = 1.sp
                )
                if (groups.isNotEmpty()) {
                    Text(
                        text = "${selectedGroupIds.size} selecionados",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (groups.isEmpty()) {
                Text("Nenhum grupo cadastrado. Crie grupos na aba Grupos.", fontSize = 12.sp, color = Flip7TextMuted)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    groups.forEach { groupWithContacts ->
                        val isChecked = selectedGroupIds.contains(groupWithContacts.group.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isChecked) Flip7SurfaceVariant else Flip7Surface)
                                .clickable {
                                    if (isChecked) selectedGroupIds.remove(groupWithContacts.group.id)
                                    else selectedGroupIds.add(groupWithContacts.group.id)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked == true) selectedGroupIds.add(groupWithContacts.group.id)
                                    else selectedGroupIds.remove(groupWithContacts.group.id)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = Flip7ElectricCyan)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = groupWithContacts.group.name, fontWeight = FontWeight.Bold, color = Flip7TextPrimary, fontSize = 14.sp)
                                Text(text = "${groupWithContacts.contacts.size} contatos", fontSize = 11.sp, color = Flip7TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Summary & Launch CTA Button
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Flip7Card(
                borderColor = Flip7NeonGreen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "TOTAL DE DESTINATÁRIOS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Flip7TextMuted)
                            Text(
                                text = "${finalRecipients.size} pessoas únicas",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Flip7NeonGreen
                            )
                        }

                        Flip7Badge(
                            text = "1 Envio = ${finalRecipients.size} Mensagens",
                            color = Flip7CyberGold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Flip7Button(
                        onClick = {
                            if (messageText.isNotBlank() && finalRecipients.isNotEmpty()) {
                                viewModel.startCampaign(
                                    title = title,
                                    message = messageText,
                                    recipients = finalRecipients,
                                    targetAppOverride = selectedTargetApp
                                )
                            } else {
                                Toast.makeText(context, "Preencha a mensagem e selecione ao menos 1 destinatário.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = messageText.isNotBlank() && finalRecipients.isNotEmpty(),
                        containerColor = Flip7NeonGreen,
                        contentColor = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("INICIAR DISPARO EM LOTE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}
