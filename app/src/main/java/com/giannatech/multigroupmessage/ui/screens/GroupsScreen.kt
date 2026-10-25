package com.giannatech.multigroupmessage.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupWithContacts
import com.giannatech.multigroupmessage.ui.MainViewModel
import com.giannatech.multigroupmessage.ui.components.Flip7Badge
import com.giannatech.multigroupmessage.ui.components.Flip7Button
import com.giannatech.multigroupmessage.ui.components.Flip7Card
import com.giannatech.multigroupmessage.ui.components.Flip7OutlinedButton
import com.giannatech.multigroupmessage.ui.components.Flip7SectionHeader
import com.giannatech.multigroupmessage.ui.theme.Flip7Background
import com.giannatech.multigroupmessage.ui.theme.Flip7Border
import com.giannatech.multigroupmessage.ui.theme.Flip7ElectricCyan
import com.giannatech.multigroupmessage.ui.theme.Flip7NeonGreen
import com.giannatech.multigroupmessage.ui.theme.Flip7SurfaceVariant
import com.giannatech.multigroupmessage.ui.theme.Flip7TextMuted
import com.giannatech.multigroupmessage.ui.theme.Flip7TextPrimary
import com.giannatech.multigroupmessage.ui.theme.Flip7TextSecondary
import com.giannatech.multigroupmessage.ui.theme.Flip7VividCrimson

@Composable
fun GroupsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val groups by viewModel.groupsWithContacts.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog States
    var showGroupDialog by remember { mutableStateOf(false) }
    var editingGroupWithContacts by remember { mutableStateOf<GroupWithContacts?>(null) }

    var showDeleteConfirmGroup by remember { mutableStateOf<GroupEntity?>(null) }
    var showAddContactDialog by remember { mutableStateOf(false) }

    // Permission launcher for READ_CONTACTS
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.importDeviceContacts(context)
            Toast.makeText(context, "Lendo contatos da agenda...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permissão para contatos negada.", Toast.LENGTH_SHORT).show()
        }
    }

    val filteredGroups = remember(groups, searchQuery) {
        if (searchQuery.isBlank()) groups
        else groups.filter { it.group.name.contains(searchQuery, ignoreCase = true) }
    }

    val filteredContacts = remember(allContacts, searchQuery) {
        if (searchQuery.isBlank()) allContacts
        else allContacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumber.contains(searchQuery)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Screen Top Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GERENCIADOR DE GRUPOS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Flip7ElectricCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Crie, edite e organize seus contatos",
                    fontSize = 12.sp,
                    color = Flip7TextSecondary
                )
            }

            Flip7Button(
                onClick = {
                    editingGroupWithContacts = null
                    showGroupDialog = true
                },
                containerColor = Flip7ElectricCyan,
                contentColor = Color.Black,
                modifier = Modifier.height(40.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("CRIAR GRUPO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs: Grupos vs Contatos
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Flip7Background,
            contentColor = Flip7ElectricCyan,
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Flip7ElectricCyan
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Text(
                        text = "GRUPOS (${groups.size})",
                        fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Text(
                        text = "CONTATOS (${allContacts.size})",
                        fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (selectedTabIndex == 0) "Buscar grupo..." else "Buscar contato...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Flip7TextMuted) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Flip7SurfaceVariant,
                unfocusedContainerColor = Flip7SurfaceVariant,
                focusedBorderColor = Flip7ElectricCyan,
                unfocusedBorderColor = Flip7Border,
                focusedTextColor = Flip7TextPrimary,
                unfocusedTextColor = Flip7TextPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTabIndex == 0) {
            // Groups List Tab
            if (filteredGroups.isEmpty()) {
                Flip7Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GroupAdd,
                            contentDescription = null,
                            tint = Flip7TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "Nenhum grupo encontrado" else "Nenhum grupo criado",
                            color = Flip7TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredGroups) { groupWithContacts ->
                        val groupColor = try {
                            Color(android.graphics.Color.parseColor(groupWithContacts.group.colorHex))
                        } catch (e: Exception) {
                            Flip7ElectricCyan
                        }

                        Flip7Card(
                            borderColor = groupColor,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = groupWithContacts.group.name,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Flip7TextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Flip7Badge(
                                            text = "${groupWithContacts.contacts.size} pessoas",
                                            color = groupColor
                                        )
                                    }
                                    if (groupWithContacts.group.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = groupWithContacts.group.description,
                                            fontSize = 12.sp,
                                            color = Flip7TextMuted
                                        )
                                    }
                                }

                                Row {
                                    IconButton(
                                        onClick = {
                                            editingGroupWithContacts = groupWithContacts
                                            showGroupDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = Flip7ElectricCyan
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            showDeleteConfirmGroup = groupWithContacts.group
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Excluir",
                                            tint = Flip7VividCrimson
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Contacts Tab
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Flip7Button(
                    onClick = {
                        val check = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_CONTACTS
                        )
                        if (check == PackageManager.PERMISSION_GRANTED) {
                            viewModel.importDeviceContacts(context)
                            Toast.makeText(context, "Importando contatos...", Toast.LENGTH_SHORT).show()
                        } else {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    },
                    containerColor = Flip7NeonGreen,
                    contentColor = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.Contacts, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("IMPORTAR AGENDA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Flip7OutlinedButton(
                    onClick = { showAddContactDialog = true },
                    borderColor = Flip7ElectricCyan,
                    contentColor = Flip7ElectricCyan,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("NOVO CONTATO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredContacts.isEmpty()) {
                Flip7Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Nenhum contato encontrado.",
                        color = Flip7TextMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredContacts) { contact ->
                        Flip7Card(
                            borderColor = Flip7Border,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = contact.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Flip7TextPrimary
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = null,
                                            tint = Flip7NeonGreen,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = contact.phoneNumber,
                                            fontSize = 13.sp,
                                            color = Flip7TextSecondary
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.deleteContact(contact) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Excluir",
                                        tint = Flip7TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Group Detail / Edit Dialog
    if (showGroupDialog) {
        GroupDetailDialog(
            initialGroup = editingGroupWithContacts?.group,
            initialContactIds = editingGroupWithContacts?.contacts?.map { it.id } ?: emptyList(),
            allContacts = allContacts,
            onDismiss = { showGroupDialog = false },
            onSave = { name, desc, colorHex, contactIds ->
                val currentGroup = editingGroupWithContacts?.group
                if (currentGroup != null) {
                    viewModel.updateGroup(
                        currentGroup.copy(name = name, description = desc, colorHex = colorHex),
                        contactIds
                    )
                } else {
                    viewModel.createGroup(name, desc, colorHex, contactIds)
                }
                showGroupDialog = false
            }
        )
    }

    // Group Delete Confirm Dialog
    showDeleteConfirmGroup?.let { groupToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmGroup = null },
            title = { Text("Excluir Grupo", color = Flip7TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Tem certeza que deseja excluir o grupo \"${groupToDelete.name}\"? Esta ação não pode ser desfeita.",
                    color = Flip7TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGroup(groupToDelete)
                        showDeleteConfirmGroup = null
                    }
                ) {
                    Text("EXCLUIR", color = Flip7VividCrimson, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmGroup = null }) {
                    Text("CANCELAR", color = Flip7TextMuted)
                }
            },
            containerColor = Flip7Background
        )
    }

    // Add Manual Contact Dialog
    if (showAddContactDialog) {
        var manualName by remember { mutableStateOf("") }
        var manualPhone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("Novo Contato", color = Flip7TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Flip7SurfaceVariant,
                            unfocusedContainerColor = Flip7SurfaceVariant,
                            focusedBorderColor = Flip7ElectricCyan,
                            unfocusedBorderColor = Flip7Border,
                            focusedTextColor = Flip7TextPrimary,
                            unfocusedTextColor = Flip7TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = manualPhone,
                        onValueChange = { manualPhone = it },
                        label = { Text("Telefone / Celular") },
                        placeholder = { Text("Ex: 11999998888") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Flip7SurfaceVariant,
                            unfocusedContainerColor = Flip7SurfaceVariant,
                            focusedBorderColor = Flip7ElectricCyan,
                            unfocusedBorderColor = Flip7Border,
                            focusedTextColor = Flip7TextPrimary,
                            unfocusedTextColor = Flip7TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (manualPhone.isNotBlank()) {
                            viewModel.addManualContact(manualName, manualPhone)
                            showAddContactDialog = false
                        }
                    },
                    enabled = manualPhone.isNotBlank()
                ) {
                    Text("ADICIONAR", color = Flip7ElectricCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("CANCELAR", color = Flip7TextMuted)
                }
            },
            containerColor = Flip7Background
        )
    }
}
