package com.example.rtconnect.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.ui.components.AdminTopAppBar
import com.example.rtconnect.ui.components.LocalAdminMenuAction
import com.example.rtconnect.ui.components.MonthlyPaymentBarChart
import com.example.rtconnect.ui.components.StatCard
import com.example.rtconnect.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onNavigateToWarga: () -> Unit,
    onNavigateToPembayaran: () -> Unit,
    onNavigateToKategori: () -> Unit,
    onNavigateToLaporan: () -> Unit,
    onNavigateToPengumuman: () -> Unit,
    onNavigateToBansos: () -> Unit,
    onNavigateToStatistik: () -> Unit = {},
    onNavigateToNotifikasi: () -> Unit
) {
    val wargaList by AppRepository.wargaList.collectAsState()
    val laporanList by AppRepository.laporanList.collectAsState()
    val notifications by AppRepository.notifications.collectAsState()
    val openDrawer = LocalAdminMenuAction.current

    val unreadNotifs = notifications.count { !it.isRead && it.forAdmin }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "RT CONNECT",
                subtitle = "ADMIN • RT 10",
                unreadNotifCount = unreadNotifs,
                onNotificationClick = onNavigateToNotifikasi,
                onMenuClick = { openDrawer?.invoke() }
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

            // Greeting Section (Figma Dashboard Screen 1)
            Text(
                text = "Selamat pagi, Pak Ketua",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTNavy,
                    fontSize = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pantau lingkungan, hadirkan pelayanan yang lebih baik.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RTMuted,
                    fontSize = 13.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button: Kelola data warga (Filled Teal Button)
            Button(
                onClick = onNavigateToWarga,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RTTeal)
            ) {
                Icon(
                    imageVector = Icons.Outlined.People,
                    contentDescription = null,
                    tint = RTWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kelola data warga",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = RTWhite,
                        fontSize = 14.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4-Stat Grid (Matching Figma Screen 1)
            // Row 1: Kepala Keluarga & Jumlah Warga
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Kepala keluarga",
                    value = "53 KK",
                    subtitle = "Terdaftar di lingkungan RT 10",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToWarga
                )
                StatCard(
                    title = "Jumlah warga",
                    value = "186 orang",
                    subtitle = "94 laki-laki • 92 perempuan",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToWarga
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Row 2: Iuran Bulan Ini & Laporan Aktif
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Iuran bulan ini",
                    value = "45 / 53",
                    subtitle = "85% keluarga sudah membayar",
                    badgeText = "85%",
                    badgeColor = RTMint,
                    badgeTextColor = RTTeal,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPembayaran
                )
                StatCard(
                    title = "Laporan aktif",
                    value = "3 laporan",
                    subtitle = "3 diproses • 2 menunggu",
                    badgeText = "3 AKTIF",
                    badgeColor = RTWarning,
                    badgeTextColor = RTAmber,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLaporan
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pembayaran Iuran Sampah Bar Chart
            MonthlyPaymentBarChart(
                months = listOf("Jun", "Jul", "Agu", "Sep", "Okt", "Nov"),
                values = listOf(0.70f, 0.78f, 0.85f, 0.90f, 0.88f, 0.85f),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Ringkasan November Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Ringkasan November",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Iuran terkumpul",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                    )
                    Text(
                        text = "Rp1.075.000",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RTTeal))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "45 lunas", style = MaterialTheme.typography.bodySmall.copy(color = RTText, fontWeight = FontWeight.Medium, fontSize = 12.sp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(RTRed))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "8 belum bayar", style = MaterialTheme.typography.bodySmall.copy(color = RTText, fontWeight = FontWeight.Medium, fontSize = 12.sp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = onNavigateToPembayaran,
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RTTeal),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(RTTeal))
                    ) {
                        Text(text = "Lihat pembayaran", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Laporan Warga Terbaru Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Laporan warga terbaru",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = RTNavy,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "Lihat semua →",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = RTTeal,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.clickable { onNavigateToLaporan() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Item 1
                    LaporanItemRow(
                        judul = "Sampah belum terangkut",
                        pelapor = "Budi Santoso • 10 Nov, 08.15",
                        status = "Diproses",
                        statusBg = RTMint,
                        statusColor = RTTeal,
                        onClick = onNavigateToLaporan
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = RTBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Item 2
                    LaporanItemRow(
                        judul = "Lampu pos ronda padam",
                        pelapor = "Dewi Lestari • 09 Nov, 19.40",
                        status = "Baru",
                        statusBg = RTWarning,
                        statusColor = RTAmber,
                        onClick = onNavigateToLaporan
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Agenda Lingkungan Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "AGENDA LINGKUNGAN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = RTMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Kerja bakti bersama",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTNavy,
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = "Minggu, 15 November • 07.00 WIB\nTitik kumpul: Pos RT 10",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = RTMuted,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onNavigateToPengumuman,
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal)
                    ) {
                        Text(text = "Kelola pengumuman", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RTWhite))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sidebar Drawer Banner Guide
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTTeal.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .clickable { openDrawer?.invoke() },
                colors = CardDefaults.cardColors(containerColor = RTMint)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(RTTeal),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Outlined.Menu, contentDescription = null, tint = RTWhite, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Akses Menu Lengkap di Sidebar ☰",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 13.sp)
                        )
                        Text(
                            text = "Kategori Iuran, Bansos, Pengumuman, dan Statistik",
                            style = MaterialTheme.typography.bodySmall.copy(color = RTTeal, fontSize = 11.sp)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = RTTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LaporanItemRow(
    judul: String,
    pelapor: String,
    status: String,
    statusBg: androidx.compose.ui.graphics.Color,
    statusColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = judul,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = RTText, fontSize = 13.sp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = pelapor,
                style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = status,
                maxLines = 1,
                softWrap = false,
                style = MaterialTheme.typography.labelSmall.copy(color = statusColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            )
        }
    }
}
