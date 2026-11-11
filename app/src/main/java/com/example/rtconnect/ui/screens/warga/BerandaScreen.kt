package com.example.rtconnect.ui.screens.warga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.data.model.StatusIuran
import com.example.rtconnect.data.repository.MockData
import com.example.rtconnect.ui.components.RTButton
import com.example.rtconnect.ui.components.RTStatusBadge
import com.example.rtconnect.ui.components.RTTopAppBar
import com.example.rtconnect.ui.theme.*

@Composable
fun BerandaScreen(
    onNavigateToBayar: () -> Unit,
    onNavigateToProfil: () -> Unit,
    onNavigateToLapor: () -> Unit,
    onNavigateToPengumuman: () -> Unit,
    onNavigateToDetailPengumuman: (String) -> Unit,
    onNavigateToBansos: () -> Unit,
    onNavigateToNotifikasi: () -> Unit
) {
    val user = MockData.currentUser
    val tagihan = MockData.currentTagihan
    val pengumumanTerbaru = MockData.pengumumanList.firstOrNull()

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "RT CONNECT",
                subtitle = "Lingkungan RT 10",
                showBackButton = false,
                onNotificationClick = onNavigateToNotifikasi
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

            // Greeting Section
            Text(
                text = "Selamat pagi, Pak ${user.namaKepalaKeluarga.split(" ").first()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTText,
                    fontSize = 22.sp
                )
            )
            Text(
                text = "Keluarga ${user.namaKepalaKeluarga} • ${user.blokRumah}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RTMuted,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Hero Card Tagihan Keluarga
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tagihan.judul,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RTMuted,
                                letterSpacing = 0.5.sp
                            )
                        )
                        RTStatusBadge(statusIuran = tagihan.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Rp${String.format("%,d", tagihan.nominal).replace(',', '.')}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Jatuh tempo: ${tagihan.batasWaktu}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (tagihan.status == StatusIuran.BELUM_LUNAS) RTRed else RTMuted,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RTButton(
                        text = if (tagihan.status == StatusIuran.BELUM_LUNAS) "Bayar sekarang" else "Lihat Bukti Pembayaran",
                        onClick = onNavigateToBayar,
                        icon = Icons.Outlined.Payment
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Layanan Warga Section Title
            Text(
                text = "Layanan warga",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTText,
                    fontSize = 18.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 2x2 Grid of Quick Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title = "Profil Keluarga",
                    icon = Icons.Outlined.People,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToProfil
                )
                QuickActionCard(
                    title = "Lapor Masalah",
                    icon = Icons.Outlined.Campaign,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLapor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionCard(
                    title = "Pengumuman",
                    icon = Icons.Outlined.Announcement,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPengumuman
                )
                QuickActionCard(
                    title = "Bantuan Sosial",
                    icon = Icons.Outlined.VolunteerActivism,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBansos
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Kabar Lingkungan Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kabar lingkungan",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = RTText,
                        fontSize = 18.sp
                    )
                )
                Text(
                    text = "Lihat semua",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = RTTeal,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.clickable { onNavigateToPengumuman() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (pengumumanTerbaru != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, RTBorder, RoundedCornerShape(14.dp))
                        .clickable { onNavigateToDetailPengumuman(pengumumanTerbaru.id) },
                    colors = CardDefaults.cardColors(containerColor = RTWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(RTMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Event,
                                contentDescription = null,
                                tint = RTTeal
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pengumumanTerbaru.judul,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RTText,
                                    fontSize = 15.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${pengumumanTerbaru.tanggal} • ${pengumumanTerbaru.waktu} • ${pengumumanTerbaru.lokasi}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = RTMuted,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = RTMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, RTBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = RTWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(RTMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = RTTeal,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = RTText,
                    fontSize = 13.sp
                ),
                maxLines = 2
            )
        }
    }
}
