package com.giannatech.multigroupmessage.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giannatech.multigroupmessage.data.preferences.TargetApp
import com.giannatech.multigroupmessage.ui.MainViewModel
import com.giannatech.multigroupmessage.ui.components.Flip7Badge
import com.giannatech.multigroupmessage.ui.components.Flip7Button
import com.giannatech.multigroupmessage.ui.components.Flip7Card
import com.giannatech.multigroupmessage.ui.components.Flip7OutlinedButton
import com.giannatech.multigroupmessage.ui.components.Flip7SectionHeader
import com.giannatech.multigroupmessage.ui.components.Flip7StatCard
import com.giannatech.multigroupmessage.ui.theme.Flip7CyberGold
import com.giannatech.multigroupmessage.ui.theme.Flip7ElectricCyan
import com.giannatech.multigroupmessage.ui.theme.Flip7NeonGreen
import com.giannatech.multigroupmessage.ui.theme.Flip7TextMuted
import com.giannatech.multigroupmessage.ui.theme.Flip7TextPrimary
import com.giannatech.multigroupmessage.ui.theme.Flip7TextSecondary
import com.giannatech.multigroupmessage.ui.theme.Flip7VividCrimson
import com.giannatech.multigroupmessage.ui.theme.Flip7WhatsAppGreen

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToSendMessage: () -> Unit,
    onNavigateToGroups: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val analytics by viewModel.analytics.collectAsState()
    val targetApp by viewModel.targetApp.collectAsState()
    val groups by viewModel.groupsWithContacts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MULTIGROUP",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Flip7NeonGreen,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Disparo inteligente de mensagens",
                        fontSize = 13.sp,
                        color = Flip7TextSecondary
                    )
                }

                val appBadgeColor = if (targetApp == TargetApp.WHATSAPP_BUSINESS) Flip7VividCrimson else Flip7WhatsAppGreen
                Flip7Badge(
                    text = targetApp.displayName,
                    color = appBadgeColor
                )
            }
        }

        // Hero CTA Card
        item {
            Flip7Card(
                borderColor = Flip7NeonGreen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Flip7NeonGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nova Campanha de Mensagens",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Envie uma mensagem simultânea para seus grupos ou contatos selecionados via ${targetApp.displayName}.",
                        fontSize = 13.sp,
                        color = Flip7TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Flip7Button(
                        onClick = onNavigateToSendMessage,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CRIAR MENSAGEM",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Flip7SectionHeader(
                title = "Métricas Rápidas",
                subtitle = "Estatísticas de envio e impacto"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Flip7StatCard(
                    title = "Campanhas",
                    value = "${analytics.totalCampaigns}",
                    subtext = "1 envio p/ N pessoas = 1",
                    icon = Icons.Default.Analytics,
                    accentColor = Flip7CyberGold,
                    modifier = Modifier.weight(1f)
                )
                Flip7StatCard(
                    title = "Impacto",
                    value = "${analytics.uniquePeopleReached}",
                    subtext = "${analytics.totalMessagesSent} msgs enviadas",
                    icon = Icons.Default.People,
                    accentColor = Flip7ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Relation Banner
        item {
            Flip7Card(
                borderColor = Flip7CyberGold.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "RELAÇÃO MENSAGENS X PESSOAS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7CyberGold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Média de %.1f pessoas/envio".format(analytics.avgPeoplePerCampaign),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7TextPrimary
                        )
                        Text(
                            text = "Total de ${analytics.totalMessagesSent} mensagens para ${analytics.uniquePeopleReached} pessoas únicas",
                            fontSize = 12.sp,
                            color = Flip7TextMuted
                        )
                    }
                    Flip7OutlinedButton(
                        onClick = onNavigateToAnalytics,
                        borderColor = Flip7CyberGold,
                        contentColor = Flip7CyberGold,
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Detalhes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Groups Quick Access Section
        item {
            Flip7SectionHeader(
                title = "Meus Grupos (${groups.size})",
                subtitle = "Grupos configurados para envio rápido",
                action = {
                    Flip7OutlinedButton(
                        onClick = onNavigateToGroups,
                        borderColor = Flip7ElectricCyan,
                        contentColor = Flip7ElectricCyan,
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Gerenciar", fontSize = 12.sp)
                    }
                }
            )

            if (groups.isEmpty()) {
                Flip7Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = null,
                            tint = Flip7TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nenhum grupo cadastrado ainda",
                            color = Flip7TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Flip7Button(
                            onClick = onNavigateToGroups,
                            containerColor = Flip7ElectricCyan,
                            contentColor = Color.Black
                        ) {
                            Text("Criar Primeiro Grupo", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(groups) { groupWithContacts ->
                        val groupColor = try {
                            Color(android.graphics.Color.parseColor(groupWithContacts.group.colorHex))
                        } catch (e: Exception) {
                            Flip7ElectricCyan
                        }
                        Flip7Card(
                            borderColor = groupColor,
                            modifier = Modifier.width(160.dp),
                            onClick = onNavigateToSendMessage
                        ) {
                            Column {
                                Flip7Badge(
                                    text = "${groupWithContacts.contacts.size} membros",
                                    color = groupColor
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = groupWithContacts.group.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Flip7TextPrimary,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = groupWithContacts.group.description.ifBlank { "Sem descrição" },
                                    fontSize = 11.sp,
                                    color = Flip7TextMuted,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.giannatech.multigroupmessage.ui.theme.Flip7Background)
            .padding(16.dp)
    ) {
        Text(
            text = "MULTIGROUP",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Flip7NeonGreen,
            letterSpacing = 2.sp
        )
        Text(
            text = "Disparo inteligente de mensagens",
            fontSize = 13.sp,
            color = Flip7TextSecondary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Flip7Card(
            borderColor = Flip7NeonGreen,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Flip7NeonGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Nova Campanha de Mensagens",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Flip7TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Envie uma mensagem simultânea para seus grupos ou contatos selecionados via WhatsApp Business.",
                    fontSize = 13.sp,
                    color = Flip7TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Flip7Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CRIAR MENSAGEM",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Flip7SectionHeader(
            title = "Métricas Rápidas",
            subtitle = "Estatísticas de envio e impacto"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Flip7StatCard(
                title = "Campanhas",
                value = "5",
                subtext = "1 envio p/ N pessoas = 1",
                icon = Icons.Default.Analytics,
                accentColor = Flip7CyberGold,
                modifier = Modifier.weight(1f)
            )
            Flip7StatCard(
                title = "Impacto",
                value = "48",
                subtext = "48 msgs enviadas",
                icon = Icons.Default.People,
                accentColor = Flip7ElectricCyan,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
