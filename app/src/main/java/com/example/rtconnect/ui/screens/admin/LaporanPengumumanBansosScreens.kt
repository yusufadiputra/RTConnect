package com.example.rtconnect.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rtconnect.data.model.*
import com.example.rtconnect.data.repository.AppRepository
import com.example.rtconnect.ui.components.AdminTopAppBar
import com.example.rtconnect.ui.components.ConfirmationDialog
import com.example.rtconnect.ui.components.EmptyStateView
import com.example.rtconnect.ui.components.RTStatusBadge
import com.example.rtconnect.ui.theme.*
import java.util.UUID

// -----------------------------------------------------------------------------
// 1. KELOLA LAPORAN ADMIN SCREEN
// -----------------------------------------------------------------------------
@Composable
fun KelolaLaporanAdminScreen(
    onBackClick: () -> Unit
) {
    val laporanList by AppRepository.laporanList.collectAsState()

    var selectedStatus by remember { mutableStateOf<StatusLaporan?>(null) }
    var selectedKategori by remember { mutableStateOf<KategoriLaporan?>(null) }
    var handlingLaporan by remember { mutableStateOf<Laporan?>(null) }
    var deletingLaporan by remember { mutableStateOf<Laporan?>(null) }

    val filtered = laporanList.filter { l ->
        (selectedStatus == null || l.status == selectedStatus) &&
                (selectedKategori == null || l.kategori == selectedKategori)
    }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Manajemen Laporan",
                subtitle = "Tindak lanjut keluhan warga RT 10",
                showBack = false
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            // Filter Status Row
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedStatus == null,
                        onClick = { selectedStatus = null },
                        label = { Text("Semua (${laporanList.size})") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatus == StatusLaporan.BARU,
                        onClick = { selectedStatus = if (selectedStatus == StatusLaporan.BARU) null else StatusLaporan.BARU },
                        label = { Text("Baru") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatus == StatusLaporan.DIPROSES,
                        onClick = { selectedStatus = if (selectedStatus == StatusLaporan.DIPROSES) null else StatusLaporan.DIPROSES },
                        label = { Text("Diproses") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedStatus == StatusLaporan.SELESAI,
                        onClick = { selectedStatus = if (selectedStatus == StatusLaporan.SELESAI) null else StatusLaporan.SELESAI },
                        label = { Text("Selesai") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (filtered.isEmpty()) {
                EmptyStateView(
                    title = "Tidak Ada Laporan",
                    message = "Semua laporan warga telah ditangani dengan baik.",
                    icon = Icons.Outlined.ThumbUp
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        LaporanAdminCard(
                            laporan = item,
                            onHandleClick = { handlingLaporan = item },
                            onDeleteClick = { deletingLaporan = item }
                        )
                    }
                }
            }
        }
    }

    // Modal Tindak Lanjut & Tanggapan
    if (handlingLaporan != null) {
        TindakLanjutLaporanDialog(
            laporan = handlingLaporan!!,
            onDismiss = { handlingLaporan = null },
            onSave = { newStatus, tanggapan ->
                AppRepository.updateStatusLaporan(handlingLaporan!!.id, newStatus, tanggapan)
                handlingLaporan = null
            }
        )
    }

    // Dialog Konfirmasi Hapus
    if (deletingLaporan != null) {
        ConfirmationDialog(
            title = "Hapus Laporan?",
            message = "Apakah Anda yakin ingin menghapus laporan \"${deletingLaporan?.judul}\"? Tindakan ini permanen.",
            confirmText = "Hapus Laporan",
            isDestructive = true,
            onConfirm = {
                deletingLaporan?.let { AppRepository.deleteLaporan(it.id) }
                deletingLaporan = null
            },
            onDismiss = { deletingLaporan = null }
        )
    }
}

@Composable
fun LaporanAdminCard(
    laporan: Laporan,
    onHandleClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.dp, RTBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = RTWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = laporan.judul,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 15.sp),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                RTStatusBadge(statusLaporan = laporan.status)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = laporan.deskripsi, style = MaterialTheme.typography.bodyMedium.copy(color = RTText, fontSize = 13.sp))

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = RTMuted, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${laporan.lokasi} • Oleh ${laporan.pelapor} (${laporan.tanggal})", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp))
            }

            if (laporan.tanggapanRt != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(RTMint).padding(10.dp)
                ) {
                    Text(
                        text = "Tanggapan RT: ${laporan.tanggapanRt}",
                        style = MaterialTheme.typography.bodySmall.copy(color = RTTeal, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = RTBorder)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Hapus", tint = RTRed, modifier = Modifier.size(18.dp))
                }

                Button(
                    onClick = onHandleClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RTNavy),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Tindak Lanjut & Tanggapan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TindakLanjutLaporanDialog(
    laporan: Laporan,
    onDismiss: () -> Unit,
    onSave: (StatusLaporan, String) -> Unit
) {
    var status by remember { mutableStateOf(laporan.status) }
    var tanggapan by remember { mutableStateOf(laporan.tanggapanRt ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), colors = CardDefaults.cardColors(containerColor = RTWhite)) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(text = "Tindak Lanjut Laporan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy))
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = laporan.judul, style = MaterialTheme.typography.bodySmall.copy(color = RTMuted))

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Ubah Status Penanganan:")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusLaporan.values().forEach { st ->
                        FilterChip(
                            selected = status == st,
                            onClick = { status = st },
                            label = { Text(st.name, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Tanggapan Resmi Pengurus RT (Muncul di Warga):")
                OutlinedTextField(
                    value = tanggapan,
                    onValueChange = { tanggapan = it },
                    placeholder = { Text("Tuliskan tindakan yang telah atau akan diambil...", color = RTMuted) },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(10.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) { Text("Batal") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onSave(status, tanggapan) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal)
                    ) {
                        Text("Simpan & Kirim", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. KELOLA PENGUMUMAN ADMIN SCREEN (CRUD)
// -----------------------------------------------------------------------------
@Composable
fun KelolaPengumumanAdminScreen(
    onBackClick: () -> Unit
) {
    val pengumumanList by AppRepository.pengumumanList.collectAsState()
    var editingPengumuman by remember { mutableStateOf<Pengumuman?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }
    var deletingPengumuman by remember { mutableStateOf<Pengumuman?>(null) }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Manajemen Pengumuman",
                subtitle = "Publikasi kabar kegiatan RT 10",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingPengumuman = null
                    showFormDialog = true
                },
                containerColor = RTNavy,
                contentColor = RTWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Buat Pengumuman", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            items(pengumumanList, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.dp, RTBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = RTWhite)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.judul, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 15.sp), modifier = Modifier.weight(1f))
                            if (!item.isPublished) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(RTBorder).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text(text = "DRAFT", style = MaterialTheme.typography.labelSmall.copy(color = RTMuted, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                                }
                            } else if (item.penting) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(RTDanger).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text(text = "PENTING", style = MaterialTheme.typography.labelSmall.copy(color = RTRed, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = item.isi, style = MaterialTheme.typography.bodyMedium.copy(color = RTText, fontSize = 13.sp), maxLines = 3)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "${item.tanggal} • ${item.waktu} • ${item.lokasi}", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp))

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = RTBorder)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            IconButton(onClick = { deletingPengumuman = item }, modifier = Modifier.size(32.dp)) {
                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = null, tint = RTRed, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    editingPengumuman = item
                                    showFormDialog = true
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = RTNavy)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Edit", fontSize = 12.sp, color = RTNavy)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        PengumumanFormDialog(
            initial = editingPengumuman,
            onDismiss = { showFormDialog = false },
            onSave = { p ->
                if (editingPengumuman == null) {
                    AppRepository.addPengumuman(p)
                } else {
                    AppRepository.updatePengumuman(p)
                }
                showFormDialog = false
            }
        )
    }

    if (deletingPengumuman != null) {
        ConfirmationDialog(
            title = "Hapus Pengumuman?",
            message = "Apakah Anda yakin ingin menghapus pengumuman \"${deletingPengumuman?.judul}\"?",
            confirmText = "Hapus",
            isDestructive = true,
            onConfirm = {
                deletingPengumuman?.let { AppRepository.deletePengumuman(it.id) }
                deletingPengumuman = null
            },
            onDismiss = { deletingPengumuman = null }
        )
    }
}

@Composable
fun PengumumanFormDialog(
    initial: Pengumuman?,
    onDismiss: () -> Unit,
    onSave: (Pengumuman) -> Unit
) {
    var judul by remember { mutableStateOf(initial?.judul ?: "") }
    var isi by remember { mutableStateOf(initial?.isi ?: "") }
    var tanggal by remember { mutableStateOf(initial?.tanggal ?: "15 Nov 2026") }
    var waktu by remember { mutableStateOf(initial?.waktu ?: "07.00 WIB") }
    var lokasi by remember { mutableStateOf(initial?.lokasi ?: "Pos RT 10") }
    var penting by remember { mutableStateOf(initial?.penting ?: false) }
    var isPublished by remember { mutableStateOf(initial?.isPublished ?: true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)), colors = CardDefaults.cardColors(containerColor = RTWhite)) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(text = if (initial == null) "Buat Pengumuman Baru" else "Edit Pengumuman", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy))
                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Judul Kegiatan / Berita *")
                OutlinedTextField(value = judul, onValueChange = { judul = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Isi Pengumuman Lengkap *")
                OutlinedTextField(value = isi, onValueChange = { isi = it }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(10.dp), maxLines = 4)

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Tanggal Pelaksanaan:")
                OutlinedTextField(value = tanggal, onValueChange = { tanggal = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), singleLine = true)

                Spacer(modifier = Modifier.height(10.dp))

                FormLabel("Waktu & Lokasi:")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = waktu, onValueChange = { waktu = it }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), singleLine = true)
                    OutlinedTextField(value = lokasi, onValueChange = { lokasi = it }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp), singleLine = true)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Tandai Penting (Darurat / Mendesak)", style = MaterialTheme.typography.bodySmall.copy(color = RTText))
                    Switch(checked = penting, onCheckedChange = { penting = it })
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Publikasikan Sekarang", style = MaterialTheme.typography.bodySmall.copy(color = RTText))
                    Switch(checked = isPublished, onCheckedChange = { isPublished = it })
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) { Text("Batal") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val p = Pengumuman(
                                id = initial?.id ?: UUID.randomUUID().toString(),
                                judul = judul,
                                isi = isi,
                                tanggal = tanggal,
                                waktu = waktu,
                                lokasi = lokasi,
                                penting = penting,
                                isPublished = isPublished
                            )
                            onSave(p)
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTNavy)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 3. KELOLA BANTUAN SOSIAL ADMIN SCREEN (Privat & Transparan)
// -----------------------------------------------------------------------------
@Composable
fun KelolaBansosAdminScreen(
    onBackClick: () -> Unit
) {
    val programList by AppRepository.programBansosList.collectAsState()
    val penerimaList by AppRepository.penerimaBansosList.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Program, 1: Penerima

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Manajemen Bantuan Sosial",
                subtitle = "Data tertutup & penyaluran bansos RT 10",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        containerColor = RTCanvas
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Tab Switcher
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = RTWhite,
                contentColor = RTNavy
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Program Bansos (${programList.size})", fontWeight = FontWeight.Bold) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Daftar Penerima (${penerimaList.size})", fontWeight = FontWeight.Bold) })
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(programList, key = { it.id }) { item ->
                        Card(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.dp, RTBorder, RoundedCornerShape(14.dp)), colors = CardDefaults.cardColors(containerColor = RTWhite)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = item.namaProgram, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 15.sp), modifier = Modifier.weight(1f))
                                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(RTMint).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text(text = item.status, style = MaterialTheme.typography.labelSmall.copy(color = RTTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Sasaran: ${item.kriteria}", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Periode: ${item.periode} • Kuota: ${item.kuota} KK", style = MaterialTheme.typography.bodySmall.copy(color = RTText, fontWeight = FontWeight.SemiBold))
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(RTWarning).padding(12.dp)) {
                            Text(text = "Data penerima bansos bersifat rahasia dan hanya dapat diakses oleh Pengurus RT 10.", fontSize = 11.sp, color = RTAmber, fontWeight = FontWeight.Bold)
                        }
                    }

                    items(penerimaList, key = { it.id }) { item ->
                        Card(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.dp, RTBorder, RoundedCornerShape(14.dp)), colors = CardDefaults.cardColors(containerColor = RTWhite)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = item.namaPenerima, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 15.sp))
                                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(if (item.statusPenyaluran == "Disalurkan") RTMint else RTWarning).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                        Text(text = item.statusPenyaluran, style = MaterialTheme.typography.labelSmall.copy(color = if (item.statusPenyaluran == "Disalurkan") RTTeal else RTAmber, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Program: ${item.programNama}", style = MaterialTheme.typography.bodySmall.copy(color = RTTeal, fontSize = 12.sp, fontWeight = FontWeight.SemiBold))
                                Text(text = "${item.blokRumah} • No KK: ${item.noKk}", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 11.sp))

                                if (item.statusPenyaluran != "Disalurkan") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { AppRepository.updateStatusPenyaluran(item.id, "Disalurkan") },
                                        modifier = Modifier.fillMaxWidth().height(34.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal)
                                    ) {
                                        Text("Tandai Telah Disalurkan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
