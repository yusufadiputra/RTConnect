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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.data.model.StatusIuran
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.data.repository.MockData
import com.example.rtconnect.ui.components.RTButton
import com.example.rtconnect.ui.components.RTStatusBadge
import com.example.rtconnect.ui.components.RTTopAppBar
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.rtconnect.ui.theme.*
import com.example.rtconnect.util.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
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
    val currentUser by AppRepository.currentUser.collectAsState()
    val wargaList by AppRepository.wargaList.collectAsState()
    val currentWarga = wargaList.find {
        it.noHp.replace(" ", "") == currentUser.phoneNumber.replace(" ", "") ||
                it.namaLengkap.equals(currentUser.fullName, ignoreCase = true)
    }
    val tagihanList by AppRepository.tagihanList.collectAsState()
    val rawTagihan = tagihanList.find { it.status == StatusIuran.BELUM_DIBAYAR } ?: tagihanList.firstOrNull() ?: MockData.currentTagihan
    val tagihan = if (currentWarga?.customTarifNominal != null) rawTagihan.copy(nominal = currentWarga.customTarifNominal) else rawTagihan
    val pengumumanList by AppRepository.pengumumanList.collectAsState()
    val user = currentWarga ?: MockData.currentUser
    val context = LocalContext.current
    val isLoading by AppRepository.isLoading.collectAsState()
    var showKuitansiDialog by remember { mutableStateOf(false) }

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        AppRepository.refreshAllData()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "RT CONNECT",
                subtitle = "Lingkungan RT 06",
                showBackButton = false,
                onNotificationClick = onNavigateToNotifikasi
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { AppRepository.refreshAllData() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                    if (tagihan.status != StatusIuran.LUNAS) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Jatuh tempo: ${tagihan.batasWaktu}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (tagihan.status == StatusIuran.BELUM_LUNAS) RTRed else RTMuted,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val isLunas = tagihan.status == StatusIuran.LUNAS
                    RTButton(
                        text = if (isLunas) "Lihat Bukti Pembayaran" else "Bayar sekarang",
                        onClick = {
                            if (isLunas) {
                                showKuitansiDialog = true
                            } else {
                                onNavigateToBayar()
                            }
                        },
                        icon = if (isLunas) Icons.Outlined.ReceiptLong else Icons.Outlined.Payment
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

            Spacer(modifier = Modifier.height(14.dp))

            // Carousel Pengumuman yang Lebih Besar dan Bisa Digeser-geser (Swipeable)
            if (pengumumanList.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(pengumumanList) { item ->
                        Card(
                            modifier = Modifier
                                .width(310.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (item.penting) Color(0xFFFCA5A5) else RTBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { onNavigateToDetailPengumuman(item.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (item.penting) Color(0xFFFFF8F8) else RTWhite
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Baris Atas: Badge Status & Tanggal
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (item.penting) {
                                        Surface(
                                            color = RTRed.copy(alpha = 0.12f),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "PENTING",
                                                color = RTRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = RTMint,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "INFO WARGA",
                                                color = RTTeal,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.CalendarToday,
                                            contentDescription = null,
                                            tint = RTMuted,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = item.tanggal,
                                            color = RTMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Judul Pengumuman
                                Text(
                                    text = item.judul,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = RTNavy,
                                        fontSize = 16.sp,
                                        lineHeight = 21.sp
                                    ),
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Isi Pengumuman Langsung Tampil di Dashboard
                                Text(
                                    text = item.isi,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = RTMuted,
                                        fontSize = 12.sp,
                                        lineHeight = 17.sp
                                    ),
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                HorizontalDivider(color = RTBorder.copy(alpha = 0.6f))

                                Spacer(modifier = Modifier.height(10.dp))

                                // Lokasi & Aksi Baca Selengkapnya
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Place,
                                            contentDescription = null,
                                            tint = RTTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = item.lokasi,
                                            color = RTText,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Baca Detail",
                                            color = RTTeal,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                            contentDescription = null,
                                            tint = RTTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showKuitansiDialog) {
            KuitansiPembayaranDialog(
                tagihan = tagihan,
                onDismiss = { showKuitansiDialog = false }
            )
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
