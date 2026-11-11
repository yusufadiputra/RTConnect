package com.example.rtconnect.ui.screens.warga

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.data.repository.MockData
import com.example.rtconnect.ui.components.RTButton
import com.example.rtconnect.ui.components.RTTopAppBar
import com.example.rtconnect.ui.theme.*

@Composable
fun PengumumanScreen(
    onDetailClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val list = MockData.pengumumanList

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Pengumuman RT",
                subtitle = "Informasi kegiatan warga RT 10",
                showBackButton = true,
                onBackClick = onBackClick,
                showNotification = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(list) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, RTBorder, RoundedCornerShape(14.dp))
                        .clickable { onDetailClick(item.id) },
                    colors = CardDefaults.cardColors(containerColor = RTWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.judul,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = RTText,
                                    fontSize = 16.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (item.penting) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(RTDanger)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PENTING",
                                        style = MaterialTheme.typography.labelSmall.copy(color = RTRed, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.isi,
                            style = MaterialTheme.typography.bodyMedium.copy(color = RTMuted, fontSize = 13.sp),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null, tint = RTTeal, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${item.tanggal} • ${item.waktu}", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp))
                            }
                            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = RTMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DetailPengumumanScreen(
    pengumumanId: String,
    onBackClick: () -> Unit
) {
    val item = MockData.pengumumanList.find { it.id == pengumumanId } ?: MockData.pengumumanList.first()

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Detail Pengumuman",
                showBackButton = true,
                onBackClick = onBackClick,
                showNotification = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = item.judul,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTText,
                            fontSize = 22.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Dipublikasikan oleh ${item.penulis} • ${item.tanggal}",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = RTBorder)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = null, tint = RTTeal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Waktu: ${item.waktu}", style = MaterialTheme.typography.bodyMedium.copy(color = RTText, fontWeight = FontWeight.SemiBold))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Outlined.Place, contentDescription = null, tint = RTTeal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Lokasi: ${item.lokasi}", style = MaterialTheme.typography.bodyMedium.copy(color = RTText, fontWeight = FontWeight.SemiBold))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Isi Pengumuman",
                        style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = item.isi,
                        style = MaterialTheme.typography.bodyLarge.copy(color = RTText, lineHeight = 22.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun BantuanSosialScreen(
    onBackClick: () -> Unit
) {
    val list = MockData.bantuanSosialList

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Bantuan Sosial",
                subtitle = "Program bantuan untuk warga RT 10",
                showBackButton = true,
                onBackClick = onBackClick,
                showNotification = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Info banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(RTMint)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Program bansos disalurkan secara transparan dan diverifikasi langsung oleh Pengurus RT 10.",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTNavy, fontSize = 12.sp)
                    )
                }
            }

            items(list) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, RTBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = RTWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.namaProgram,
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTText, fontSize = 15.sp),
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(RTMint)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = item.status, style = MaterialTheme.typography.labelSmall.copy(color = RTTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Sasaran: ${item.kriteria}", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Periode: ${item.periode} • Kuota: ${item.jumlahPenerima} Keluarga", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun NotifikasiScreen(
    onBackClick: () -> Unit
) {
    val notifs = listOf(
        Pair("Tagihan Iuran Baru", "Iuran sampah bulan November 2026 telah terbit. Silakan bayar sebelum 15 Nov."),
        Pair("Kerja Bakti Minggu Ini", "Jangan lupa besok Minggu pkl 07.00 WIB kerja bakti di Pos RT 10."),
        Pair("Laporan Diproses", "Laporan lampu jalan mati di Blok B sedang ditangani petugas.")
    )

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Notifikasi",
                showBackButton = true,
                onBackClick = onBackClick,
                showNotification = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            items(notifs) { notif ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, RTBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = RTWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RTMint),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Outlined.Notifications, contentDescription = null, tint = RTTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = notif.first, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTText, fontSize = 14.sp))
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = notif.second, style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp))
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ProfilKeluargaScreen(
    onLogout: () -> Unit
) {
    val user = MockData.currentUser

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Profil Keluarga",
                subtitle = "Data kependudukan RT 10",
                showBackButton = false,
                showNotification = false
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

            // User Info Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(35.dp))
                            .background(RTNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.namaKepalaKeluarga.split(" ").map { it.take(1) }.joinToString(""),
                            style = MaterialTheme.typography.titleLarge.copy(color = RTWhite, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = user.namaKepalaKeluarga,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTText, fontSize = 18.sp)
                    )
                    Text(
                        text = "Kepala Keluarga • ${user.blokRumah}",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 13.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Detail Data KK
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RTWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Informasi Kependudukan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTText, fontSize = 15.sp))
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileItemRow("Nomor Kartu Keluarga", user.noKk)
                    ProfileItemRow("Alamat / Blok", "${user.blokRumah}, RT 10 / RW 04")
                    ProfileItemRow("Nomor Ponsel", user.noHp)
                    ProfileItemRow("Jumlah Anggota", "${user.jumlahAnggota} Jiwa")
                    ProfileItemRow("Status Iuran", "Aktif (Iuran Bulanan)")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                Text(text = "Keluar dari Akun", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = RTMuted, fontSize = 13.sp))
        Text(text = value, style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.SemiBold, fontSize = 13.sp))
    }
}
