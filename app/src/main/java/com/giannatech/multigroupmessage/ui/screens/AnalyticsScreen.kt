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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giannatech.multigroupmessage.data.preferences.TargetApp
import com.giannatech.multigroupmessage.ui.MainViewModel
import com.giannatech.multigroupmessage.ui.components.Flip7Badge
import com.giannatech.multigroupmessage.ui.components.Flip7Card
import com.giannatech.multigroupmessage.ui.components.Flip7SectionHeader
import com.giannatech.multigroupmessage.ui.components.Flip7StatCard
import com.giannatech.multigroupmessage.ui.theme.Flip7Border
import com.giannatech.multigroupmessage.ui.theme.Flip7CyberGold
import com.giannatech.multigroupmessage.ui.theme.Flip7ElectricCyan
import com.giannatech.multigroupmessage.ui.theme.Flip7NeonGreen
import com.giannatech.multigroupmessage.ui.theme.Flip7TextMuted
import com.giannatech.multigroupmessage.ui.theme.Flip7TextPrimary
import com.giannatech.multigroupmessage.ui.theme.Flip7TextSecondary
import com.giannatech.multigroupmessage.ui.theme.Flip7VividCrimson
import com.giannatech.multigroupmessage.ui.theme.Flip7WhatsAppGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val analytics by viewModel.analytics.collectAsState()
    val campaigns by viewModel.campaigns.collectAsState()

    val dateFormat = rememberDateFormat()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "PAINEL DE MÉTRICAS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Flip7CyberGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Acompanhamento da relação Mensagens x Destinatários Impactados",
                    fontSize = 12.sp,
                    color = Flip7TextSecondary
                )
            }
        }

        // Stat Grid Row 1
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Flip7StatCard(
                    title = "Total de Disparos",
                    value = "${analytics.totalCampaigns}",
                    subtext = "Campanhas criadas",
                    icon = Icons.Default.Analytics,
                    accentColor = Flip7CyberGold,
                    modifier = Modifier.weight(1f)
                )
                Flip7StatCard(
                    title = "Msgs Enviadas",
                    value = "${analytics.totalMessagesSent}",
                    subtext = "Disparos individuais",
                    icon = Icons.Default.Send,
                    accentColor = Flip7NeonGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Stat Grid Row 2
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Flip7StatCard(
                    title = "Pessoas Únicas",
                    value = "${analytics.uniquePeopleReached}",
                    subtext = "Impactadas pelo app",
                    icon = Icons.Default.People,
                    accentColor = Flip7ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
                Flip7StatCard(
                    title = "Média por Envio",
                    value = "%.1f".format(analytics.avgPeoplePerCampaign),
                    subtext = "Pessoas por disparo",
                    icon = Icons.Default.BarChart,
                    accentColor = Flip7VividCrimson,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // High Contrast Summary Card
        item {
            Flip7Card(
                borderColor = Flip7CyberGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "RESUMO DE IMPACTO E RELAÇÃO DE ENVIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Flip7CyberGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Quando você cria 1 Campanha e envia para N pessoas, o aplicativo registra como 1 disparo com alcance de N contatos.",
                        fontSize = 13.sp,
                        color = Flip7TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("1 Envio", fontSize = 11.sp, color = Flip7TextMuted)
                            Text("↓", fontSize = 14.sp, color = Flip7CyberGold, fontWeight = FontWeight.Bold)
                            Text("Média de %.1f pessoas".format(analytics.avgPeoplePerCampaign), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Flip7NeonGreen)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Multiplicador de Alcance", fontSize = 11.sp, color = Flip7TextMuted)
                            Text(
                                text = "${analytics.totalMessagesSent} msgs / ${analytics.totalCampaigns} envios",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Flip7ElectricCyan
                            )
                        }
                    }
                }
            }
        }

        // Campaign History Section
        item {
            Flip7SectionHeader(
                title = "Histórico de Campanhas (${campaigns.size})",
                subtitle = "Registro detalhado de mensagens enviadas"
            )

            if (campaigns.isEmpty()) {
                Flip7Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nenhuma campanha realizada ainda.",
                        color = Flip7TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        items(campaigns) { campaign ->
            val isBusiness = campaign.targetApp == TargetApp.WHATSAPP_BUSINESS.name
            val badgeColor = if (isBusiness) Flip7VividCrimson else Flip7WhatsAppGreen
            val formattedDate = dateFormat.format(Date(campaign.timestamp))

            Flip7Card(
                borderColor = Flip7Border,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = campaign.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7TextPrimary
                        )

                        Flip7Badge(
                            text = if (isBusiness) "WhatsApp Business" else "WhatsApp",
                            color = badgeColor
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = formattedDate,
                        fontSize = 11.sp,
                        color = Flip7TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "\"${campaign.messageText}\"",
                        fontSize = 13.sp,
                        color = Flip7TextSecondary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enviados: ${campaign.sentCount} de ${campaign.totalRecipients}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7NeonGreen
                        )

                        Text(
                            text = "1 envio p/ ${campaign.totalRecipients} contatos",
                            fontSize = 11.sp,
                            color = Flip7CyberGold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberDateFormat(): SimpleDateFormat {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
}

@Preview(showBackground = true)
@Composable
fun AnalyticsScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(com.giannatech.multigroupmessage.ui.theme.Flip7Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = "PAINEL DE MÉTRICAS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Flip7CyberGold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Acompanhamento da relação Mensagens x Destinatários Impactados",
                fontSize = 12.sp,
                color = Flip7TextSecondary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Flip7StatCard(
                title = "Total de Disparos",
                value = "5",
                subtext = "Campanhas criadas",
                icon = Icons.Default.Analytics,
                accentColor = Flip7CyberGold,
                modifier = Modifier.weight(1f)
            )
            Flip7StatCard(
                title = "Msgs Enviadas",
                value = "48",
                subtext = "Disparos individuais",
                icon = Icons.Default.Send,
                accentColor = Flip7NeonGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Flip7StatCard(
                title = "Pessoas Únicas",
                value = "32",
                subtext = "Impactadas pelo app",
                icon = Icons.Default.People,
                accentColor = Flip7ElectricCyan,
                modifier = Modifier.weight(1f)
            )
            Flip7StatCard(
                title = "Média por Envio",
                value = "9.6",
                subtext = "Pessoas por disparo",
                icon = Icons.Default.BarChart,
                accentColor = Flip7VividCrimson,
                modifier = Modifier.weight(1f)
            )
        }

        Flip7Card(
            borderColor = Flip7CyberGold,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = "RESUMO DE IMPACTO E RELAÇÃO DE ENVIO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Flip7CyberGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Quando você cria 1 Campanha e envia para N pessoas, o aplicativo registra como 1 disparo com alcance de N contatos.",
                    fontSize = 13.sp,
                    color = Flip7TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("1 Envio", fontSize = 11.sp, color = Flip7TextMuted)
                        Text("↓", fontSize = 14.sp, color = Flip7CyberGold, fontWeight = FontWeight.Bold)
                        Text("Média de 9.6 pessoas", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Flip7NeonGreen)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Multiplicador de Alcance", fontSize = 11.sp, color = Flip7TextMuted)
                        Text(
                            text = "48 msgs / 5 envios",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7ElectricCyan
                        )
                    }
                }
            }
        }
    }
}
