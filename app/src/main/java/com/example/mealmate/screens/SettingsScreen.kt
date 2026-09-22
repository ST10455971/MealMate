package com.example.mealmate.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.mealmate.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun SettingsScreen(
    user: FirebaseUser? = FirebaseAuth.getInstance().currentUser,
    onLogout: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val currentUser = user ?: FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: currentUser?.email ?: "User"

    var notificationsEnabled by remember {
        mutableStateOf(true)
    }

    var showDietaryDialog by remember { mutableStateOf(false) }
    var selectedPreferences by remember { 
        mutableStateOf(setOf("Vegetarian", "Halal", "Vegan")) 
    }

    if (showDietaryDialog) {
        DietaryPreferencesDialog(
            currentPreferences = selectedPreferences,
            onDismiss = { showDietaryDialog = false },
            onConfirm = { 
                selectedPreferences = it
                showDietaryDialog = false
            }
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentScreen = "settings",
                onNavigate = { onNavigate(it) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            item {

                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    color = ForestGreen
                )

                Spacer(modifier = Modifier.height(24.dp))

                ListItem(
                    headlineContent = { Text("Profile") },
                    supportingContent = { Text(userName) },
                    leadingContent = {
                        Icon(Icons.Default.Person, null, tint = ForestGreen)
                    }
                )

                HorizontalDivider(color = LightBeige)

                ListItem(
                    headlineContent = { Text("Notifications") },
                    leadingContent = {
                        Icon(Icons.Default.Notifications, null, tint = ForestGreen)
                    },
                    trailingContent = {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                notificationsEnabled = it
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = ForestGreen)
                        )
                    }
                )

                HorizontalDivider(color = LightBeige)

                ListItem(
                    headlineContent = { Text("Language") },
                    supportingContent = { Text("English") },
                    leadingContent = {
                        Icon(Icons.Default.Language, null, tint = ForestGreen)
                    },
                    modifier = Modifier.clickable {
                        onNavigate("language")
                    }
                )

                HorizontalDivider(color = LightBeige)

                ListItem(
                    headlineContent = { Text("Dietary Preferences") },
                    supportingContent = {
                        Text(if (selectedPreferences.isEmpty()) "None selected" else selectedPreferences.joinToString(" • "))
                    },
                    leadingContent = {
                        Icon(Icons.Default.Restaurant, null, tint = ForestGreen)
                    },
                    modifier = Modifier.clickable {
                        showDietaryDialog = true
                    }
                )

                HorizontalDivider(color = LightBeige)

                ListItem(
                    headlineContent = { Text("Sync Status") },
                    supportingContent = {
                        Text("Online")
                    },
                    leadingContent = {
                        Icon(Icons.Default.CloudDone, null, tint = ForestGreen)
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen
                    )
                ) {

                    Icon(Icons.Default.Logout, null, tint = White)

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Logout", color = White)

                }

            }

        }
    }
}

@Composable
fun DietaryPreferencesDialog(
    currentPreferences: Set<String>,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    val options = listOf("Vegetarian", "Halal", "Vegan", "Gluten-Free", "Keto", "Paleo")
    val selectedOptions = remember { mutableStateListOf<String>().apply { addAll(currentPreferences) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Dietary Preferences") },
        text = {
            Column(Modifier.selectableGroup()) {
                options.forEach { option ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .selectable(
                                selected = selectedOptions.contains(option),
                                onClick = {
                                    if (selectedOptions.contains(option)) {
                                        selectedOptions.remove(option)
                                    } else {
                                        selectedOptions.add(option)
                                    }
                                },
                                role = Role.Checkbox
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedOptions.contains(option),
                            onCheckedChange = null // null because Row handles it
                        )
                        Spacer(Modifier.width(16.dp))
                        Text(text = option, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedOptions.toSet()) }) {
                Text("Save", color = ForestGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = WarmGray)
            }
        }
    )
}
