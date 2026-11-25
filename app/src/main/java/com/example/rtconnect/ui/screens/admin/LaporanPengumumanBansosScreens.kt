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
import androidx.compose.ui.platform.LocalContext
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
import com.example.rtconnect.util.NotificationHelper
import com.example.rtconnect.util.WhatsAppHelper
import com.example.rtconnect.ui.theme.*
import java.util.UUID

// -----------------------------------------------------------------------------
// 1. KELOLA LAPORAN ADMIN SCREEN
// -----------------------------------------------------------------------------
@Composable
fun KelolaLaporanAdminScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
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
                subtitle = "Tindak lanjut keluhan warga RT 06",
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
                val currentLaporan = handlingLaporan!!
                AppRepository.updateStatusLaporan(currentLaporan.id, newStatus, tanggapan)
                NotificationHelper.showAnnouncementNotification(
                    context = context,
                    title = "Status Laporan: ${newStatus.name}",
                    message = "Laporan '${currentLaporan.judul}' telah diperbarui pengurus RT (${newStatus.name}).",
                    isPenting = false
                )
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
    val context = LocalContext.current
    val wargaList by AppRepository.wargaList.collectAsState()
    val pelaporWarga = remember(laporan.pelapor, wargaList) {
        wargaList.find { it.namaKepalaKeluarga.contains(laporan.pelapor, ignoreCase = true) || laporan.pelapor.contains(it.namaKepalaKeluarga, ignoreCase = true) }
    }

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

            if (pelaporWarga != null) {
                Button(
                    onClick = {
                        val message = "Halo Bapak/Ibu ${laporan.pelapor}, terkait laporan lingkungan Anda: '${laporan.judul}', kami dari Pengurus RT 06..."
                        WhatsAppHelper.openWhatsApp(context, pelaporWarga.noHp, message)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RTWhatsApp, contentColor = RTWhite),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(34.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Chat, contentDescription = "WhatsApp", modifier = Modifier.size(15.dp), tint = RTWhite)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Hubungi Pelapor via WhatsApp (${pelaporWarga.noHp})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RTWhite)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Hapus", tint = RTRed, modifier = Modifier.size(18.dp))
                }

                Button(
                    onClick = onHandleClick,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RTNavy, contentColor = RTWhite),
                    contentPadding = PaddingValues(horizontal = 14.dp)
                ) {
                    Icon(imageVector = Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp), tint = RTWhite)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Tindak Lanjut & Tanggapan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RTWhite)
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
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal, contentColor = RTWhite)
                    ) {
                        Text("Simpan & Kirim", fontWeight = FontWeight.Bold, color = RTWhite)
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
    val context = LocalContext.current
    val pengumumanList by AppRepository.pengumumanList.collectAsState()
    var editingPengumuman by remember { mutableStateOf<Pengumuman?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }
    var deletingPengumuman by remember { mutableStateOf<Pengumuman?>(null) }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Manajemen Pengumuman",
                subtitle = "Publikasi kabar kegiatan RT 06",
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
                                Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(RTMuted).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                    Text(text = "DRAFT", style = MaterialTheme.typography.labelSmall.copy(color = RTWhite, fontWeight = FontWeight.Bold, fontSize = 9.sp))
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
                    NotificationHelper.showAnnouncementNotification(
                        context = context,
                        title = p.judul,
                        message = p.isi,
                        isPenting = p.penting
                    )
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
    var lokasi by remember { mutableStateOf(initial?.lokasi ?: "Pos RT 06") }
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
                        colors = ButtonDefaults.buttonColors(containerColor = RTNavy, contentColor = RTWhite)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold, color = RTWhite)
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
    val context = LocalContext.current
    val programList by AppRepository.programBansosList.collectAsState()
    val penerimaList by AppRepository.penerimaBansosList.collectAsState()
    val wargaList by AppRepository.wargaList.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Program, 1: Penerima
    var showProgramDialog by remember { mutableStateOf(false) }
    var editingProgram by remember { mutableStateOf<ProgramBansos?>(null) }
    var deletingProgram by remember { mutableStateOf<ProgramBansos?>(null) }

    var showPenerimaDialog by remember { mutableStateOf(false) }
    var deletingPenerima by remember { mutableStateOf<PenerimaBansos?>(null) }

    LaunchedEffect(Unit) {
        AppRepository.refreshAllData()
    }

    Scaffold(
        topBar = {
            AdminTopAppBar(
                title = "Manajemen Bantuan Sosial",
                subtitle = "Data tertutup & penyaluran bansos RT 06",
                showBack = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) {
                        editingProgram = null
                        showProgramDialog = true
                    } else {
                        showPenerimaDialog = true
                    }
                },
                containerColor = RTNavy,
                contentColor = RTWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = RTWhite)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (selectedTab == 0) "Tambah Program" else "Tambah Penerima",
                        fontWeight = FontWeight.Bold,
                        color = RTWhite,
                        fontSize = 13.sp
                    )
                }
            }
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
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(programList, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(1.dp, RTBorder, RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = RTWhite)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = item.namaProgram, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = RTNavy, fontSize = 15.sp), modifier = Modifier.weight(1f))
                                    Box(modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(RTMint).padding(horizontal = 8.dp, vertical = 3.dp)) {
                                        Text(text = item.status, style = MaterialTheme.typography.labelSmall.copy(color = RTTeal, fontWeight = FontWeight.Bold, fontSize = 10.sp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Sasaran: ${item.kriteria}", style = MaterialTheme.typography.bodySmall.copy(color = RTMuted))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Periode: ${item.periode} • Kuota: ${item.kuota} KK", style = MaterialTheme.typography.bodySmall.copy(color = RTText, fontWeight = FontWeight.SemiBold))

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    IconButton(onClick = { deletingProgram = item }, modifier = Modifier.size(32.dp)) {
                                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Hapus", tint = RTRed, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            editingProgram = item
                                            showProgramDialog = true
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(32.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = RTNavy)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Edit Program", fontSize = 11.sp, color = RTNavy)
                                    }
                                }
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
                            Text(text = "Data penerima bansos bersifat rahasia dan hanya dapat diakses oleh Pengurus RT 06.", fontSize = 11.sp, color = RTAmber, fontWeight = FontWeight.Bold)
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

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = { deletingPenerima = item },
                                        modifier = Modifier.size(34.dp)
                                    ) {
                                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Hapus Penerima", tint = RTRed, modifier = Modifier.size(18.dp))
                                    }

                                    if (item.statusPenyaluran != "Disalurkan") {
                                        Button(
                                            onClick = { AppRepository.updateStatusPenyaluran(item.id, "Disalurkan") },
                                            modifier = Modifier.height(34.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = RTTeal, contentColor = RTWhite)
                                        ) {
                                            Text("Tandai Telah Disalurkan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RTWhite)
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

    // Dialog Konfirmasi Hapus Program
    if (deletingProgram != null) {
        AlertDialog(
            onDismissRequest = { deletingProgram = null },
            title = { Text("Hapus Program Bansos", fontWeight = FontWeight.Bold, color = RTNavy) },
            text = { Text("Apakah Anda yakin ingin menghapus program '${deletingProgram?.namaProgram}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        deletingProgram?.let { AppRepository.deleteProgramBansos(it.id) }
                        deletingProgram = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RTRed)
                ) {
                    Text("Hapus", color = RTWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingProgram = null }) {
                    Text("Batal", color = RTMuted)
                }
            }
        )
    }

    // Dialog Konfirmasi Hapus Penerima
    if (deletingPenerima != null) {
        AlertDialog(
            onDismissRequest = { deletingPenerima = null },
            title = { Text("Hapus Penerima Bansos", fontWeight = FontWeight.Bold, color = RTNavy) },
            text = { Text("Apakah Anda yakin ingin menghapus '${deletingPenerima?.namaPenerima}' dari daftar penerima bansos?") },
            confirmButton = {
                Button(
                    onClick = {
                        deletingPenerima?.let { AppRepository.deletePenerimaBansos(it.id) }
                        deletingPenerima = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RTRed)
                ) {
                    Text("Hapus", color = RTWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingPenerima = null }) {
                    Text("Batal", color = RTMuted)
                }
            }
        )
    }

    if (showProgramDialog) {
        ProgramBansosFormDialog(
            initial = editingProgram,
            onDismiss = { showProgramDialog = false },
            onSave = { p ->
                if (editingProgram == null) {
                    AppRepository.addProgramBansos(p)
                } else {
                    AppRepository.updateProgramBansos(p)
                }
                showProgramDialog = false
            }
        )
    }

    if (showPenerimaDialog) {
        PenerimaBansosFormDialog(
            programList = programList,
            wargaList = wargaList,
            onDismiss = { showPenerimaDialog = false },
            onSave = { p ->
                AppRepository.addPenerimaBansos(p)
                showPenerimaDialog = false
            }
        )
    }
}

// -----------------------------------------------------------------------------
// Dialog Form Program Bansos (Tambah / Edit)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramBansosFormDialog(
    initial: ProgramBansos?,
    onDismiss: () -> Unit,
    onSave: (ProgramBansos) -> Unit
) {
    var namaProgram by remember { mutableStateOf(initial?.namaProgram ?: "") }
    var periode by remember { mutableStateOf(initial?.periode ?: "November 2026") }
    var kriteria by remember { mutableStateOf(initial?.kriteria ?: "Keluarga Prasejahtera / Lansia") }
    var kuotaStr by remember { mutableStateOf(initial?.kuota?.toString() ?: "15") }
    var status by remember { mutableStateOf(initial?.status ?: "Aktif") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = RTWhite)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(
                    text = if (initial == null) "Tambah Program Bansos" else "Edit Program Bansos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy)
                )
                Spacer(modifier = Modifier.height(14.dp))

                FormLabel("Nama Program Bansos *")
                OutlinedTextField(
                    value = namaProgram,
                    onValueChange = { namaProgram = it },
                    placeholder = { Text("Contoh: Bansos Beras & Sembako Bulanan", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))
                FormLabel("Sasaran / Kriteria Penerima *")
                OutlinedTextField(
                    value = kriteria,
                    onValueChange = { kriteria = it },
                    placeholder = { Text("Contoh: Warga Lansia & Subsidi RT 06", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))
                FormLabel("Periode Program *")
                OutlinedTextField(
                    value = periode,
                    onValueChange = { periode = it },
                    placeholder = { Text("Contoh: November 2026", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Kuota (KK) *")
                        OutlinedTextField(
                            value = kuotaStr,
                            onValueChange = { kuotaStr = it },
                            placeholder = { Text("15", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        FormLabel("Status")
                        OutlinedTextField(
                            value = status,
                            onValueChange = { status = it },
                            placeholder = { Text("Aktif / Persiapan", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) { Text("Batal") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (namaProgram.isNotBlank()) {
                                onSave(
                                    ProgramBansos(
                                        id = initial?.id ?: UUID.randomUUID().toString(),
                                        namaProgram = namaProgram,
                                        periode = periode,
                                        kriteria = kriteria,
                                        kuota = kuotaStr.toIntOrNull() ?: 10,
                                        status = status
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal, contentColor = RTWhite),
                        enabled = namaProgram.isNotBlank()
                    ) {
                        Text(if (initial == null) "Tambah" else "Simpan", fontWeight = FontWeight.Bold, color = RTWhite)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Dialog Form Tambah Penerima Bansos (Pilih dari Warga RT)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenerimaBansosFormDialog(
    programList: List<ProgramBansos>,
    wargaList: List<Warga>,
    onDismiss: () -> Unit,
    onSave: (PenerimaBansos) -> Unit
) {
    var selectedProgramId by remember { mutableStateOf(programList.firstOrNull()?.id ?: "") }
    var selectedWargaId by remember { mutableStateOf(wargaList.firstOrNull()?.id ?: "") }

    var expandedProgram by remember { mutableStateOf(false) }
    var expandedWarga by remember { mutableStateOf(false) }

    val selectedProgram = programList.find { it.id == selectedProgramId } ?: programList.firstOrNull()
    val selectedWarga = wargaList.find { it.id == selectedWargaId } ?: wargaList.firstOrNull()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = RTWhite)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text(
                    text = "Tambah Penerima Bansos",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RTNavy)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Pilih keluarga warga RT 06 yang berhak menerima program bantuan ini:",
                    style = MaterialTheme.typography.bodySmall.copy(color = RTMuted, fontSize = 12.sp)
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Dropdown Program Bansos
                FormLabel("Pilih Program Bansos *")
                ExposedDropdownMenuBox(
                    expanded = expandedProgram,
                    onExpandedChange = { expandedProgram = !expandedProgram }
                ) {
                    OutlinedTextField(
                        value = selectedProgram?.namaProgram ?: "Pilih Program",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedProgram) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedProgram,
                        onDismissRequest = { expandedProgram = false }
                    ) {
                        programList.forEach { prog ->
                            DropdownMenuItem(
                                text = { Text(prog.namaProgram, fontSize = 13.sp) },
                                onClick = {
                                    selectedProgramId = prog.id
                                    expandedProgram = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dropdown Warga
                FormLabel("Pilih Warga / Kepala Keluarga *")
                ExposedDropdownMenuBox(
                    expanded = expandedWarga,
                    onExpandedChange = { expandedWarga = !expandedWarga }
                ) {
                    OutlinedTextField(
                        value = selectedWarga?.let { "${it.namaLengkap} (${it.blokRumah})" } ?: "Pilih Warga",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWarga) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedWarga,
                        onDismissRequest = { expandedWarga = false }
                    ) {
                        wargaList.forEach { w ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(w.namaLengkap, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${w.blokRumah} • KK: ${w.nomorKk}", fontSize = 11.sp, color = RTMuted)
                                    }
                                },
                                onClick = {
                                    selectedWargaId = w.id
                                    expandedWarga = false
                                }
                            )
                        }
                    }
                }

                if (selectedWarga != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(RTMint).padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Rincian Warga Terpilih:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RTNavy)
                            Text(text = "Kepala Keluarga: ${selectedWarga.namaLengkap}", fontSize = 12.sp, color = RTText)
                            Text(text = "No. KK: ${selectedWarga.nomorKk}", fontSize = 12.sp, color = RTText)
                            Text(text = "Alamat: ${selectedWarga.blokRumah}", fontSize = 12.sp, color = RTText)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) { Text("Batal") }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            if (selectedProgram != null && selectedWarga != null) {
                                onSave(
                                    PenerimaBansos(
                                        id = UUID.randomUUID().toString(),
                                        programId = selectedProgram.id,
                                        programNama = selectedProgram.namaProgram,
                                        namaPenerima = selectedWarga.namaLengkap,
                                        noKk = selectedWarga.nomorKk,
                                        blokRumah = selectedWarga.blokRumah,
                                        statusPenyaluran = "Diverifikasi",
                                        catatan = "Penerima KK: ${selectedWarga.noKk}"
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RTTeal, contentColor = RTWhite),
                        enabled = selectedProgram != null && selectedWarga != null
                    ) {
                        Text("Daftarkan Penerima", fontWeight = FontWeight.Bold, color = RTWhite)
                    }
                }
            }
        }
    }
}
