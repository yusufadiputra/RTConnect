package com.example.rtconnect.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.ui.components.AdminTopAppBar
import com.example.rtconnect.ui.components.EmptyStateView
import com.example.rtconnect.ui.screens.warga.ProfileItemRow
import com.example.rtconnect.ui.theme.*

@Composable
fun AdminProfileScreen(
    onLogout: () -> Unit
) {
    val currentUser by AppRepository.currentUser.collectAsState()
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Profil Pengurus",
                subtitle = "Ketua RT 10 / RW 04",
                showBack = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Ketua RT Avatar & Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(RTNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "RT",
                            style = MaterialTheme.typography.titleLarge.copy(color = RTWhite, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentUser.fullName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 19.sp)
                    )
                    Text(
                        text = "Ketua RT 10 • Lingkungan Siaga",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 13.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Detail Akun Pengurus
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Informasi Akun & Jabatan",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 15.sp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileItemRow("Jabatan", "Ketua RT 10 / RW 04")
                    ProfileItemRow("Nama Lengkap", currentUser.fullName)
                    ProfileItemRow("Nomor WhatsApp", currentUser.phoneNumber)
                    ProfileItemRow("Email Resmi", currentUser.email)
                    ProfileItemRow("Alamat Pos RT", currentUser.address)
                    ProfileItemRow("Status Autentikasi", "Super Admin (Supabase)")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showChangePasswordDialog = true }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Outlined.Lock, contentDescription = null, tint = RTNavy)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Ganti Kata Sandi Akun", style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.SemiBold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = RTRed),
                border = androidx.compose.foundation.BorderStroke(1.dp, RTRed)
            ) {
                Icon(imageVector = Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Keluar dari Panel Admin", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(onDismiss = { showChangePasswordDialog = false })
    }
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }
    var successMsg by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), colors = CardDefaults.cardColors(containerColor = RTWhite)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Ganti Kata Sandi", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy))
                Spacer(modifier = Modifier.height(14.dp))

                if (successMsg) {
                    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(RTMint).padding(10.dp)) {
                        Text(text = "Kata sandi berhasil diperbarui!", color = RTTeal, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                FormLabel("Kata Sandi Lama:")
                OutlinedTextField(value = oldPass, onValueChange = { oldPass = it }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Kata Sandi Baru:")
                OutlinedTextField(value = newPass, onValueChange = { newPass = it }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Konfirmasi Kata Sandi Baru:")
                OutlinedTextField(value = confirmPass, onValueChange = { confirmPass = it }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) { Text("Tutup") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { successMsg = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTNavy),
                        enabled = newPass.isNotEmpty() && newPass == confirmPass
                    ) {
                        Text("Simpan Sandi", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// NOTIFIKASI ADMIN SCREEN
// -----------------------------------------------------------------------------
@Composable
fun AdminNotificationScreen(
    onBackClick: () -> Unit
) {
    val notifications by AppRepository.notifications.collectAsState()
    val adminNotifs = notifications.filter { it.forAdmin }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Notifikasi Pengurus",
                subtitle = "${adminNotifs.count { !it.isRead }} pesan baru",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        if (adminNotifs.isEmpty()) {
            EmptyStateView(
                title = "Tidak Ada Notifikasi",
                message = "Belum ada laporan atau aktivitas baru dari warga RT 10.",
                icon = Icons.Outlined.NotificationsNone
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(adminNotifs, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, if (!item.isRead) RTTeal else RTBorder, RoundedCornerShape(12.dp))
                            .clickable { AppRepository.markNotificationAsRead(item.id) },
                        colors = CardDefaults.cardColors(containerColor = if (!item.isRead) RTMint.copy(alpha = 0.25f) else RTWhite)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier.size(38.dp).clip(RoundedCornerShape(8.dp)).background(if (!item.isRead) RTMint else RTCanvas),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Outlined.NotificationsActive, contentDescription = null, tint = if (!item.isRead) RTTeal else RTMuted, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = item.title, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 14.sp))
                                    Text(text = item.timestamp, style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 10.sp))
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = item.message, style = MaterialTheme.typography.bodySmall.copy(color = RTText, fontSize = 12.sp))
                            }
                        }
                    }
                }
            }
        }
    }
}
