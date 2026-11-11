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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rtconnect.data.model.KategoriLaporan
import com.example.rtconnect.data.model.Laporan
import com.example.rtconnect.data.repository.MockData
import com.example.rtconnect.ui.components.RTButton
import com.example.rtconnect.ui.components.RTStatusBadge
import com.example.rtconnect.ui.components.RTTopAppBar
import com.example.rtconnect.ui.theme.*

@Composable
fun LaporMasalahScreen(
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var selectedKategori by remember { mutableStateOf(KategoriLaporan.KEAMANAN) }
    var judul by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Lapor Masalah",
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
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pilih Kategori Laporan",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTText,
                    fontSize = 16.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                KategoriChip(
                    text = "Keamanan",
                    icon = Icons.Outlined.Security,
                    isSelected = selectedKategori == KategoriLaporan.KEAMANAN,
                    onClick = { selectedKategori = KategoriLaporan.KEAMANAN },
                    modifier = Modifier.weight(1f)
                )
                KategoriChip(
                    text = "Lingkungan",
                    icon = Icons.Outlined.Nature,
                    isSelected = selectedKategori == KategoriLaporan.LINGKUNGAN,
                    onClick = { selectedKategori = KategoriLaporan.LINGKUNGAN },
                    modifier = Modifier.weight(1f)
                )
                KategoriChip(
                    text = "Kekeringan",
                    icon = Icons.Outlined.WaterDrop,
                    isSelected = selectedKategori == KategoriLaporan.KEKERINGAN,
                    onClick = { selectedKategori = KategoriLaporan.KEKERINGAN },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Judul Laporan",
                style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = judul,
                onValueChange = { judul = it },
                placeholder = { Text("Contoh: Lampu jalan Blok B mati", color = RTMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RTWhite,
                    unfocusedContainerColor = RTWhite,
                    focusedBorderColor = RTTeal,
                    unfocusedBorderColor = RTBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Lokasi Kejadian",
                style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lokasi,
                onValueChange = { lokasi = it },
                placeholder = { Text("Contoh: Depan Pos RT / Pertigaan Blok B", color = RTMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RTWhite,
                    unfocusedContainerColor = RTWhite,
                    focusedBorderColor = RTTeal,
                    unfocusedBorderColor = RTBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Deskripsi Masalah",
                style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = deskripsi,
                onValueChange = { deskripsi = it },
                placeholder = { Text("Jelaskan detail situasi yang perlu ditangani pengurus...", color = RTMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = RTWhite,
                    unfocusedContainerColor = RTWhite,
                    focusedBorderColor = RTTeal,
                    unfocusedBorderColor = RTBorder
                ),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Upload Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, RTBorder, RoundedCornerShape(12.dp))
                    .background(RTWhite)
                    .clickable { /* Upload photo */ },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Outlined.PhotoCamera, contentDescription = null, tint = RTTeal)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Lampirkan Foto Bukti (Opsional)",
                        style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            RTButton(
                text = "Kirim Laporan ke Pengurus RT",
                onClick = onSubmitSuccess,
                enabled = judul.isNotBlank() || deskripsi.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun KategoriChip(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) RTTeal else RTBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .background(if (isSelected) RTMint else RTWhite)
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (isSelected) RTTeal else RTMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) RTTeal else RTText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
fun LaporanTerkirimScreen(
    onDoneClick: () -> Unit
) {
    Scaffold(containerColor = RTCanvas) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(45.dp))
                    .background(RTMint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = RTTeal,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Laporan Terkirim!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = RTText,
                    fontSize = 24.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Laporan Anda telah diteruskan ke pengurus RT 10 dan akan segera ditindaklanjuti. Anda dapat memantau status penanganannya.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RTMuted,
                    fontSize = 14.sp
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            RTButton(
                text = "Lihat Status Laporan",
                onClick = onDoneClick
            )
        }
    }
}

@Composable
fun StatusLaporanScreen(
    onLaporBaruClick: () -> Unit,
    onDetailClick: (String) -> Unit
) {
    val laporanList = MockData.laporanList

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Status Laporan",
                subtitle = "Keluhan lingkungan RT 10",
                showBackButton = false,
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Laporan Warga (${laporanList.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTText,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "+ Buat Laporan",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = RTTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.clickable { onLaporBaruClick() }
                    )
                }
            }

            items(laporanList) { item ->
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
                                    fontSize = 15.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            RTStatusBadge(statusLaporan = item.status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.deskripsi,
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
                                Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = RTMuted, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = item.lokasi, style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp))
                            }
                            Text(text = item.tanggal, style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp))
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
fun DetailLaporanScreen(
    laporanId: String,
    onBackClick: () -> Unit
) {
    val laporan = MockData.laporanList.find { it.id == laporanId } ?: MockData.laporanList.first()

    Scaffold(
        topBar = {
            RTTopAppBar(
                title = "Detail Laporan",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kategori: ${laporan.kategori.name}",
                            style = MaterialTheme.typography.labelSmall.copy(color = RTMuted, fontWeight = FontWeight.SemiBold)
                        )
                        RTStatusBadge(statusLaporan = laporan.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = laporan.judul,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = RTText,
                            fontSize = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Dilaporkan oleh ${laporan.pelapor} pada ${laporan.tanggal}",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = RTBorder)

                    Text(
                        text = "Lokasi Kejadian",
                        style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = laporan.lokasi, style = MaterialTheme.typography.bodyMedium.copy(color = RTMuted))

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Keterangan Lengkap",
                        style = MaterialTheme.typography.labelLarge.copy(color = RTText, fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = laporan.deskripsi, style = MaterialTheme.typography.bodyLarge.copy(color = RTText, lineHeight = 22.sp))

                    Spacer(modifier = Modifier.height(20.dp))

                    // Catatan Pengurus RT
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(RTMint)
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "Tindak Lanjut Pengurus RT:",
                                style = MaterialTheme.typography.labelSmall.copy(color = RTTeal, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Petugas keamanan dan seksi pemeliharaan lingkungan sudah dijadwalkan untuk penanganan ke lokasi.",
                                style = MaterialTheme.typography.bodySmall.copy(color = RTNavy, fontSize = 12.sp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
