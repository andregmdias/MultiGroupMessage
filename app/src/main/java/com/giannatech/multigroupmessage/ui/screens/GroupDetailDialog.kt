package com.giannatech.multigroupmessage.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.giannatech.multigroupmessage.data.local.entity.ContactEntity
import com.giannatech.multigroupmessage.data.local.entity.GroupEntity
import com.giannatech.multigroupmessage.ui.components.Flip7Button
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

private val COLOR_PRESETS = listOf(
    "#00E5FF", // Electric Cyan
    "#00E676", // Neon Green
    "#FFD600", // Cyber Gold
    "#FF3D00", // Vivid Crimson
    "#E040FB", // Neon Violet
    "#FF4081"  // Hot Pink
)

@Composable
fun GroupDetailDialog(
    initialGroup: GroupEntity? = null,
    initialContactIds: List<Long> = emptyList(),
    allContacts: List<ContactEntity>,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, colorHex: String, selectedContactIds: List<Long>) -> Unit
) {
    var name by remember { mutableStateOf(initialGroup?.name ?: "") }
    var description by remember { mutableStateOf(initialGroup?.description ?: "") }
    var selectedColorHex by remember { mutableStateOf(initialGroup?.colorHex ?: COLOR_PRESETS.first()) }
    var searchQuery by remember { mutableStateOf("") }

    val selectedIds = remember { mutableStateListOf<Long>().apply { addAll(initialContactIds) } }

    val filteredContacts = remember(allContacts, searchQuery) {
        if (searchQuery.isBlank()) allContacts
        else allContacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.phoneNumber.contains(searchQuery)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Flip7Surface,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Flip7Border),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(20.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialGroup == null) "Novo Grupo" else "Editar Grupo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Flip7TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Flip7TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Group Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome do Grupo") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Flip7SurfaceVariant,
                        unfocusedContainerColor = Flip7SurfaceVariant,
                        focusedBorderColor = Flip7ElectricCyan,
                        unfocusedBorderColor = Flip7Border,
                        focusedLabelColor = Flip7ElectricCyan,
                        unfocusedLabelColor = Flip7TextMuted,
                        focusedTextColor = Flip7TextPrimary,
                        unfocusedTextColor = Flip7TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Group Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (Opcional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Flip7SurfaceVariant,
                        unfocusedContainerColor = Flip7SurfaceVariant,
                        focusedBorderColor = Flip7ElectricCyan,
                        unfocusedBorderColor = Flip7Border,
                        focusedLabelColor = Flip7ElectricCyan,
                        unfocusedLabelColor = Flip7TextMuted,
                        focusedTextColor = Flip7TextPrimary,
                        unfocusedTextColor = Flip7TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Color Presets Selector
                Text(
                    text = "Cor da Tag",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Flip7TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    COLOR_PRESETS.forEach { colorHex ->
                        val color = try {
                            Color(android.graphics.Color.parseColor(colorHex))
                        } catch (e: Exception) {
                            Flip7ElectricCyan
                        }
                        val isSelected = selectedColorHex.equals(colorHex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorHex = colorHex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Member Selection Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Membros (${selectedIds.size} selecionados)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Flip7TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar contato...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Flip7TextMuted
                        )
                    },
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

                Spacer(modifier = Modifier.height(8.dp))

                // Contacts list with Checkboxes
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredContacts) { contact ->
                        val isChecked = selectedIds.contains(contact.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isChecked) Flip7SurfaceVariant else Color.Transparent)
                                .clickable {
                                    if (isChecked) selectedIds.remove(contact.id)
                                    else selectedIds.add(contact.id)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    if (checked == true) selectedIds.add(contact.id)
                                    else selectedIds.remove(contact.id)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Flip7ElectricCyan,
                                    uncheckedColor = Flip7Border
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = contact.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Flip7TextPrimary
                                )
                                Text(
                                    text = contact.phoneNumber,
                                    fontSize = 12.sp,
                                    color = Flip7TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footer Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Flip7OutlinedButton(
                        onClick = onDismiss,
                        borderColor = Flip7Border,
                        contentColor = Flip7TextMuted,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCELAR")
                    }
                    Flip7Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onSave(name, description, selectedColorHex, selectedIds.toList())
                            }
                        },
                        enabled = name.isNotBlank(),
                        containerColor = Flip7ElectricCyan,
                        contentColor = Color.Black,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("SALVAR", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
