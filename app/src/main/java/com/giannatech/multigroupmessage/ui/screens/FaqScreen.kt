package com.giannatech.multigroupmessage.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.giannatech.multigroupmessage.ui.components.Flip7Card
import com.giannatech.multigroupmessage.ui.theme.Flip7Border
import com.giannatech.multigroupmessage.ui.theme.Flip7CyberGold
import com.giannatech.multigroupmessage.ui.theme.Flip7ElectricCyan
import com.giannatech.multigroupmessage.ui.theme.Flip7NeonGreen
import com.giannatech.multigroupmessage.ui.theme.Flip7TextMuted
import com.giannatech.multigroupmessage.ui.theme.Flip7TextPrimary
import com.giannatech.multigroupmessage.ui.theme.Flip7TextSecondary
import com.giannatech.multigroupmessage.ui.theme.MultiGroupMessageTheme

data class FaqItem(
    val question: String,
    val answer: String
)

private val FAQ_ITEMS = listOf(
    FaqItem(
        question = "Como funciona o Disparo via API Oficial da Meta?",
        answer = "Para enviar todas as mensagens em lote diretamente sem risco de bloqueio, usamos a Cloud API da Meta:\n\n" +
                "1. Acesse developers.facebook.com e crie um aplicativo Empresa.\n\n" +
                "2. Associe-o a uma Meta Business Account (Portfólio de Negócios).\n\n" +
                "3. Em Configuração da API (API Setup), copie o 'Phone Number ID' e o 'Access Token'.\n\n" +
                "4. Salve-os em Ajustes e utilize o botão 'DISPARAR TUDO AUTOMATICAMENTE'."
    ),
    FaqItem(
        question = "Como realizar o teste de envio?",
        answer = "1. Na página do seu app no developers.facebook.com, vá em Configuração da API.\n\n" +
                "2. Adicione seu próprio número de celular na lista de números autorizados para teste.\n\n" +
                "3. No app MultiGroupMessage, em Ajustes, informe suas credenciais e clique em 'TESTAR CONEXÃO / ENVIAR MENSAGEM DE TESTE'. O app enviará uma mensagem instantânea diretamente para seu celular!"
    ),
    FaqItem(
        question = "Por que o envio por Intent/Manual foi removido?",
        answer = "Para proteger sua conta pessoal e comercial do WhatsApp contra banimentos.\n\n" +
                "O envio manual automatizado violava as diretrizes anti-spam. Focamos exclusivamente na WhatsApp Business Cloud API Oficial da Meta para garantir 100% de conformidade e segurança nos seus disparos em lote."
    ),
    FaqItem(
        question = "Como funciona a cobrança da API da Meta?",
        answer = "A Meta disponibiliza 1.000 conversas por mês gratuitamente no ambiente de teste para desenvolvedores.\n\n" +
                "Para volumes comerciais maiores em produção, a Meta cobra uma tarifa por conversa iniciada, dependendo do país do destinatário (tarifas B2C)."
    ),
    FaqItem(
        question = "Como conectar e usar com o WhatsApp Web no computador?",
        answer = "1. No celular, abra o WhatsApp ou WhatsApp Business.\n\n" +
                "2. Toque no menu de três pontos e selecione 'Dispositivos Conectados'.\n\n" +
                "3. No computador, acesse web.whatsapp.com e escaneie o QR Code com a câmera do celular.\n\n" +
                "4. Todas as mensagens disparadas pelo aplicativo serão sincronizadas instantaneamente no seu WhatsApp Web."
    ),
    FaqItem(
        question = "Como funciona o contador e a relação Mensagens x Pessoas?",
        answer = "Quando você cria 1 Campanha e seleciona N destinatários (ex: 10 pessoas num grupo), o aplicativo registra:\n\n" +
                "• Total de Disparos / Campanhas = 1\n" +
                "• Mensagens Enviadas = 10\n" +
                "• Relação Média = 10 pessoas por envio."
    )
)

@Composable
fun FaqScreen(
    onNavigateBack: () -> Unit = {}
) {
    val expandedStates = remember {
        mutableStateListOf<Boolean>().apply {
            addAll(List(FAQ_ITEMS.size) { index -> index == 0 || index == 1 })
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = Flip7CyberGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(
                        text = "PERGUNTAS FREQUENTES (FAQ)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Flip7CyberGold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dúvidas sobre envio automático Meta API, teste de envio, custos e WhatsApp Web",
                    fontSize = 12.sp,
                    color = Flip7TextSecondary
                )
            }
        }

        itemsIndexed(FAQ_ITEMS) { index, item ->
            val isExpanded = expandedStates.getOrElse(index) { false }
            val accentColor = when (index % 3) {
                0 -> Flip7NeonGreen
                1 -> Flip7CyberGold
                else -> Flip7ElectricCyan
            }

            Flip7Card(
                borderColor = if (isExpanded) accentColor else Flip7Border,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        expandedStates[index] = !isExpanded
                    }
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.question,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isExpanded) accentColor else Flip7TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Recolher" else "Expandir",
                            tint = if (isExpanded) accentColor else Flip7TextMuted
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = item.answer,
                                fontSize = 13.sp,
                                color = Flip7TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FaqScreenPreview() {
    MultiGroupMessageTheme {
        FaqScreen()
    }
}
