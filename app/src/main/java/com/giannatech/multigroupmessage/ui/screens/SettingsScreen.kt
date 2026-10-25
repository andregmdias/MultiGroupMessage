package com.giannatech.multigroupmessage.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.giannatech.multigroupmessage.ui.MainViewModel
import com.giannatech.multigroupmessage.ui.components.Flip7Button
import com.giannatech.multigroupmessage.ui.components.Flip7Card
import com.giannatech.multigroupmessage.ui.theme.Flip7Background
import com.giannatech.multigroupmessage.ui.theme.Flip7Border
import com.giannatech.multigroupmessage.ui.theme.Flip7CyberGold
import com.giannatech.multigroupmessage.ui.theme.Flip7ElectricCyan
import com.giannatech.multigroupmessage.ui.theme.Flip7NeonGreen
import com.giannatech.multigroupmessage.ui.theme.Flip7SurfaceVariant
import com.giannatech.multigroupmessage.ui.theme.Flip7TextMuted
import com.giannatech.multigroupmessage.ui.theme.Flip7TextPrimary
import com.giannatech.multigroupmessage.ui.theme.Flip7TextSecondary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToFaq: () -> Unit = {}
) {
    val context = LocalContext.current
    val defaultCountryCode by viewModel.defaultCountryCode.collectAsState()
    val phoneNumberIdState by viewModel.cloudApiPhoneNumberId.collectAsState()
    val accessTokenState by viewModel.cloudApiAccessToken.collectAsState()

    var countryCodeInput by remember(defaultCountryCode) { mutableStateOf(defaultCountryCode) }
    var phoneIdInput by remember(phoneNumberIdState) { mutableStateOf(phoneNumberIdState) }
    var tokenInput by remember(accessTokenState) { mutableStateOf(accessTokenState) }
    var testPhoneInput by remember { mutableStateOf("") }
    var isSendingTest by remember { mutableStateOf(false) }

    var showCredentialsHelpDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.importDeviceContacts(context)
            Toast.makeText(context, "Lendo contatos da agenda...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permissão negada para leitura de contatos.", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "CONFIGURAÇÕES",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Flip7NeonGreen,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Ajustes do aplicativo e credenciais WhatsApp Business Cloud API",
                    fontSize = 12.sp,
                    color = Flip7TextSecondary
                )
            }
        }

        // Meta WhatsApp Cloud API Credentials
        item {
            Flip7Card(
                borderColor = Flip7CyberGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = null,
                                tint = Flip7CyberGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Credenciais Meta Graph API (Oficial)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Flip7TextPrimary
                            )
                        }

                        IconButton(onClick = { showCredentialsHelpDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Onde obter os dados?",
                                tint = Flip7CyberGold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Obtenha gratuitamente no portal developers.facebook.com para disparo automático.",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phoneIdInput,
                        onValueChange = { phoneIdInput = it },
                        label = { Text("ID do Número de Telefone (Phone Number ID)") },
                        placeholder = { Text("Ex: 105938472610928") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Flip7SurfaceVariant,
                            unfocusedContainerColor = Flip7SurfaceVariant,
                            focusedBorderColor = Flip7CyberGold,
                            unfocusedBorderColor = Flip7Border,
                            focusedTextColor = Flip7TextPrimary,
                            unfocusedTextColor = Flip7TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tokenInput,
                        onValueChange = { tokenInput = it },
                        label = { Text("Token de Acesso (Access Token)") },
                        placeholder = { Text("EAAG...") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Flip7SurfaceVariant,
                            unfocusedContainerColor = Flip7SurfaceVariant,
                            focusedBorderColor = Flip7CyberGold,
                            unfocusedBorderColor = Flip7Border,
                            focusedTextColor = Flip7TextPrimary,
                            unfocusedTextColor = Flip7TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Flip7Button(
                        onClick = {
                            viewModel.setCloudApiPhoneNumberId(phoneIdInput)
                            viewModel.setCloudApiAccessToken(tokenInput)
                            Toast.makeText(context, "Credenciais salvas com sucesso!", Toast.LENGTH_SHORT).show()
                        },
                        containerColor = Flip7CyberGold,
                        contentColor = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SALVAR CREDENCIAIS META", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Test Message Section
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
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Testar Conexão de Envio (Cloud API)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Digite seu número para disparar uma mensagem de teste e validar a API.",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = testPhoneInput,
                            onValueChange = { testPhoneInput = it },
                            placeholder = { Text("Ex: 11999998888") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Flip7SurfaceVariant,
                                unfocusedContainerColor = Flip7SurfaceVariant,
                                focusedBorderColor = Flip7NeonGreen,
                                unfocusedBorderColor = Flip7Border,
                                focusedTextColor = Flip7TextPrimary,
                                unfocusedTextColor = Flip7TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Flip7Button(
                            onClick = {
                                if (testPhoneInput.isNotBlank()) {
                                    isSendingTest = true
                                    viewModel.sendTestMessage(
                                        context = context,
                                        targetPhone = testPhoneInput,
                                        onResult = { _, msg ->
                                            isSendingTest = false
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(context, "Digite um número para o teste.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = !isSendingTest && testPhoneInput.isNotBlank(),
                            containerColor = Flip7NeonGreen,
                            contentColor = Color.Black
                        ) {
                            Text(if (isSendingTest) "ENVIANDO..." else "TESTAR ENVIO", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Default Country Code
        item {
            Flip7Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = Flip7ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Código de País Padrão (DDI)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Utilizado para formatar números que não possuam código de país (Ex: 55 para Brasil).",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = countryCodeInput,
                            onValueChange = { countryCodeInput = it },
                            placeholder = { Text("55") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Flip7SurfaceVariant,
                                unfocusedContainerColor = Flip7SurfaceVariant,
                                focusedBorderColor = Flip7ElectricCyan,
                                unfocusedBorderColor = Flip7Border,
                                focusedTextColor = Flip7TextPrimary,
                                unfocusedTextColor = Flip7TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Flip7Button(
                            onClick = {
                                viewModel.setDefaultCountryCode(countryCodeInput)
                                Toast.makeText(context, "DDI atualizado para +$countryCodeInput", Toast.LENGTH_SHORT).show()
                            },
                            containerColor = Flip7ElectricCyan,
                            contentColor = Color.Black
                        ) {
                            Text("Salvar DDI", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Contact Sync Option
        item {
            Flip7Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Contacts,
                            contentDescription = null,
                            tint = Flip7CyberGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sincronização de Agenda",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Importe ou re-sincronize todos os contatos salvos no telefone para montagem de grupos.",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Flip7Button(
                        onClick = {
                            val check = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_CONTACTS
                            )
                            if (check == PackageManager.PERMISSION_GRANTED) {
                                viewModel.importDeviceContacts(context)
                                Toast.makeText(context, "Lendo contatos da agenda...", Toast.LENGTH_SHORT).show()
                            } else {
                                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                            }
                        },
                        containerColor = Flip7CyberGold,
                        contentColor = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SINCRONIZAR CONTATOS DA AGENDA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // FAQ Card
        item {
            Flip7Card(
                borderColor = Flip7CyberGold,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = Flip7CyberGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ajuda e Perguntas Frequentes (FAQ)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Saiba como configurar a Meta Cloud API, como funciona a cobrança e como conectar com o WhatsApp Web.",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Flip7Button(
                        onClick = onNavigateToFaq,
                        containerColor = Flip7CyberGold,
                        contentColor = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("VER RESPOSTAS E FAQ", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // App Information Card
        item {
            Flip7Card(
                borderColor = Flip7Border,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Flip7TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sobre o App",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flip7TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("MultiGroupMessage v1.0", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Flip7ElectricCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Design de alta performance inspirado na temática 'Flip 7 Card Game'. Desenvolvido exclusivamente para disparo e gestão de grupos em escala via WhatsApp Business Cloud API Oficial da Meta.",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                }
            }
        }
    }

    // Modal Explicativo de Coleta de Credenciais da Meta
    if (showCredentialsHelpDialog) {
        AlertDialog(
            onDismissRequest = { showCredentialsHelpDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Flip7CyberGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Como Obter os Dados na Meta", color = Flip7TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Para enviar mensagens em lote de forma 100% oficial e automática sem risco de banimento:",
                        fontSize = 13.sp,
                        color = Flip7TextSecondary
                    )

                    Text(
                        text = "1. ID do Número de Telefone (Phone Number ID):",
                        fontWeight = FontWeight.Bold,
                        color = Flip7CyberGold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "• Acesse developers.facebook.com e vá em 'Meus Aplicativos'.\n" +
                                "• É necessário vincular o aplicativo a uma Meta Business Account (Portfólio de Negócios) caso seja solicitado.\n" +
                                "• Selecione seu app -> vá na barra lateral em 'WhatsApp' -> 'Configuração da API (API Setup)'.\n" +
                                "• Role a tela até a seção 'Enviar e receber mensagens' e copie a sequência exibida em 'ID do número de telefone'.",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )

                    Text(
                        text = "2. Token de Acesso (Access Token):",
                        fontWeight = FontWeight.Bold,
                        color = Flip7CyberGold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "• Na mesma tela de 'Configuração da API', encontre a caixa azul no topo da página e copie o 'Token de acesso temporário' (válido por 24h para testes).\n" +
                                "• Para uso contínuo em produção, gere um Token Permanente no Meta Business Suite em 'Configurações do Negócio' -> 'Usuários do Sistema'.",
                        fontSize = 12.sp,
                        color = Flip7TextMuted
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showCredentialsHelpDialog = false }) {
                    Text("ENTENDI", color = Flip7CyberGold, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Flip7Background
        )
    }
}
