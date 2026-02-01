package com.upisafety.scanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upisafety.scanner.BuildConfig
import com.upisafety.scanner.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Language setting
            SettingsItem(
                icon = Icons.Default.Language,
                title = stringResource(R.string.language),
                subtitle = if (selectedLanguage == SettingsViewModel.LANG_HINDI) 
                    stringResource(R.string.hindi) else stringResource(R.string.english),
                onClick = { showLanguageDialog = true }
            )
            
            Divider()
            
            // Privacy Policy
            SettingsItem(
                icon = Icons.Default.PrivacyTip,
                title = stringResource(R.string.privacy_policy),
                onClick = { /* Open privacy policy URL */ }
            )
            
            Divider()
            
            // Terms of Service
            SettingsItem(
                icon = Icons.Default.Description,
                title = stringResource(R.string.terms_service),
                onClick = { /* Open terms URL */ }
            )
            
            Divider()
            
            // About
            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.about),
                subtitle = stringResource(R.string.version, BuildConfig.VERSION_NAME),
                onClick = { }
            )
        }
    }
    
    // Language selection dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.language)) },
            text = {
                Column {
                    RadioButtonItem(
                        text = stringResource(R.string.english),
                        selected = selectedLanguage == SettingsViewModel.LANG_ENGLISH,
                        onClick = {
                            viewModel.setLanguage(SettingsViewModel.LANG_ENGLISH)
                            showLanguageDialog = false
                        }
                    )
                    RadioButtonItem(
                        text = stringResource(R.string.hindi),
                        selected = selectedLanguage == SettingsViewModel.LANG_HINDI,
                        onClick = {
                            viewModel.setLanguage(SettingsViewModel.LANG_HINDI)
                            showLanguageDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RadioButtonItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}
