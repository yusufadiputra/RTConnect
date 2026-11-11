package com.example.rtconnect.data.repository

import com.example.rtconnect.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object AppRepository {

    // 1. Current Session
    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "admin-1",
            email = "admin.rt10@rtconnect.id",
            fullName = "H. Supriyadi (Ketua RT 10)",
            phoneNumber = "081234567890",
            role = UserRole.ADMIN,
            address = "Jl. Merpati Blok A No. 01, RT 10 / RW 04"
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    fun switchUserRole(role: UserRole) {
        if (role == UserRole.ADMIN) {
            _currentUser.value = UserProfile(
                id = "admin-1",
                email = "admin.rt10@rtconnect.id",
                fullName = "H. Supriyadi (Ketua RT 10)",
                phoneNumber = "081234567890",
                role = UserRole.ADMIN,
                address = "Jl. Merpati Blok A No. 01, RT 10 / RW 04"
            )
        } else {
            _currentUser.value = UserProfile(
                id = "warga-budi",
                email = "budi.santoso@warga.id",
                fullName = "Budi Santoso",
                phoneNumber = "081234567891",
                role = UserRole.WARGA,
                address = "Jl. Merpati Blok B No. 12, RT 10 / RW 04"
            )
        }
    }

    // 2. Data Warga (CRUD)
    private val _wargaList = MutableStateFlow(
        listOf(
            Warga("w1", "3273010101900001", "3273010101900011", "Budi Santoso", "Blok B No. 12", "081234567891", 4, "kat-1", "Reguler", StatusEkonomi.REGULER, StatusAkun.AKTIF, StatusIuran.BELUM_DIBAYAR),
            Warga("w2", "3273010101900002", "3273010101900012", "Ahmad Fauzi", "Blok A No. 04", "081298765432", 3, "kat-1", "Reguler", StatusEkonomi.REGULER, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w3", "3273010101900003", "3273010101900013", "Siti Rahmawati", "Blok C No. 08", "081311223344", 2, "kat-3", "Subsidi", StatusEkonomi.SUBSIDI, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w4", "3273010101900004", "3273010101900014", "Hendra Gunawan", "Blok B No. 01", "081399887766", 5, "kat-2", "Pengusaha", StatusEkonomi.PENGUSAHA, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w5", "3273010101900005", "3273010101900015", "Dedi Supardi", "Blok D No. 15", "081255443322", 4, "kat-1", "Reguler", StatusEkonomi.REGULER, StatusAkun.AKTIF, StatusIuran.BELUM_DIBAYAR),
            Warga("w6", "3273010101900006", "3273010101900016", "Nurhasanah", "Blok C No. 02", "081277665544", 1, "kat-3", "Subsidi", StatusEkonomi.SUBSIDI, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w7", "3273010101900007", "3273010101900017", "Rahmat Hidayat", "Blok A No. 09", "081288990011", 4, "kat-1", "Reguler", StatusEkonomi.REGULER, StatusAkun.AKTIF, StatusIuran.LUNAS),
            Warga("w8", "3273010101900008", "3273010101900018", "Bambang Wijaya", "Blok D No. 03", "081344556677", 6, "kat-2", "Pengusaha", StatusEkonomi.PENGUSAHA, StatusAkun.AKTIF, StatusIuran.LUNAS)
        )
    )
    val wargaList: StateFlow<List<Warga>> = _wargaList.asStateFlow()

    fun addWarga(warga: Warga) {
        _wargaList.value = listOf(warga) + _wargaList.value
        addActivityLog("Tambah Warga", "Menambahkan data keluarga baru: ${warga.namaKepalaKeluarga} (${warga.blokRumah})")
    }

    fun updateWarga(updated: Warga) {
        _wargaList.value = _wargaList.value.map { if (it.id == updated.id) updated else it }
        addActivityLog("Update Warga", "Memperbarui data keluarga: ${updated.namaKepalaKeluarga}")
    }

    fun deleteWarga(wargaId: String) {
        val target = _wargaList.value.find { it.id == wargaId }
        _wargaList.value = _wargaList.value.filterNot { it.id == wargaId }
        if (target != null) {
            addActivityLog("Hapus Warga", "Menghapus data keluarga: ${target.namaKepalaKeluarga}")
        }
    }

    // 3. Kategori Iuran (CRUD)
    private val _kategoriList = MutableStateFlow(
        listOf(
            KategoriIuran("kat-1", "Reguler", 20000, "Iuran standar pengelolaan sampah keluarga rumah tangga reguler."),
            KategoriIuran("kat-2", "Pengusaha", 50000, "Iuran khusus warga yang memiliki usaha rumahan / toko kelontong di area RT."),
            KategoriIuran("kat-3", "Subsidi", 10000, "Iuran bersubsidi untuk lansia prasejahtera dan keluarga penerima bansos.")
        )
    )
    val kategoriList: StateFlow<List<KategoriIuran>> = _kategoriList.asStateFlow()

    fun addKategori(kategori: KategoriIuran) {
        _kategoriList.value = _kategoriList.value + kategori
        addActivityLog("Tambah Kategori", "Membuat kategori iuran: ${kategori.nama}")
    }

    fun updateKategori(kategori: KategoriIuran) {
        _kategoriList.value = _kategoriList.value.map { if (it.id == kategori.id) kategori else it }
        addActivityLog("Update Kategori", "Memperbarui tarif iuran kategori ${kategori.nama}: Rp${kategori.nominal}")
    }

    fun deleteKategori(id: String) {
        _kategoriList.value = _kategoriList.value.filterNot { it.id == id }
    }

    // 4. Pembayaran Sampah & Tagihan (CRUD)
    private val _tagihanList = MutableStateFlow(
        listOf(
            TagihanIuran("t1", "w1", "Budi Santoso", "Blok B No. 12", "Reguler", "Iuran Sampah November 2026", 11, 2026, "November 2026", 20000, "15 Nov 2026", StatusIuran.BELUM_DIBAYAR),
            TagihanIuran("t2", "w2", "Ahmad Fauzi", "Blok A No. 04", "Reguler", "Iuran Sampah November 2026", 11, 2026, "November 2026", 20000, "15 Nov 2026", StatusIuran.LUNAS, "05 Nov 2026", "QRIS Duitku"),
            TagihanIuran("t3", "w3", "Siti Rahmawati", "Blok C No. 08", "Subsidi", "Iuran Sampah November 2026", 11, 2026, "November 2026", 10000, "15 Nov 2026", StatusIuran.LUNAS, "04 Nov 2026", "Tunai ke RT", "Bpk. Supriyadi"),
            TagihanIuran("t4", "w4", "Hendra Gunawan", "Blok B No. 01", "Pengusaha", "Iuran Sampah November 2026", 11, 2026, "November 2026", 50000, "15 Nov 2026", StatusIuran.LUNAS, "02 Nov 2026", "Transfer Bank"),
            TagihanIuran("t5", "w5", "Dedi Supardi", "Blok D No. 15", "Reguler", "Iuran Sampah November 2026", 11, 2026, "November 2026", 20000, "15 Nov 2026", StatusIuran.BELUM_DIBAYAR),
            TagihanIuran("t6", "w6", "Nurhasanah", "Blok C No. 02", "Subsidi", "Iuran Sampah November 2026", 11, 2026, "November 2026", 10000, "15 Nov 2026", StatusIuran.LUNAS, "08 Nov 2026", "Tunai ke RT", "Bpk. Supriyadi"),
            TagihanIuran("t7", "w7", "Rahmat Hidayat", "Blok A No. 09", "Reguler", "Iuran Sampah Oktober 2026", 10, 2026, "Oktober 2026", 20000, "15 Okt 2026", StatusIuran.LUNAS, "10 Okt 2026", "QRIS Duitku")
        )
    )
    val tagihanList: StateFlow<List<TagihanIuran>> = _tagihanList.asStateFlow()

    fun catatPembayaranTunai(tagihanId: String, petugas: String) {
        _tagihanList.value = _tagihanList.value.map {
            if (it.id == tagihanId) {
                it.copy(
                    status = StatusIuran.LUNAS,
                    tanggalBayar = "11 Nov 2026 (Hari ini)",
                    metodeBayar = "Tunai ke Bendahara",
                    namaPetugasPencatat = petugas
                )
            } else it
        }
        addActivityLog("Pembayaran Tunai", "Mencatat pembayaran tunai untuk tagihan $tagihanId oleh $petugas")
    }

    fun updateStatusTagihan(tagihanId: String, newStatus: StatusIuran) {
        _tagihanList.value = _tagihanList.value.map {
            if (it.id == tagihanId) it.copy(status = newStatus) else it
        }
    }

    // 5. Laporan & Pengaduan (CRUD)
    private val _laporanList = MutableStateFlow(
        listOf(
            Laporan("l1", "Lampu Penerangan Jalan Mati", KategoriLaporan.KEAMANAN, "Lampu jalan di pertigaan Blok B mati sejak 2 hari lalu, kondisi jalan cukup gelap dan rawan.", "Pertigaan Blok B No. 10-14", "09 Nov 2026", StatusLaporan.DIPROSES, "Budi Santoso", "081234567891", tanggapanRt = "Sudah dilaporkan ke PLN, petugas akan periksa besok pagi."),
            Laporan("l2", "Saluran Air Tersumbat Sampah", KategoriLaporan.LINGKUNGAN, "Saluran air depan gang C tersumbat daun dan lumpur, air meluap saat hujan deras kemarin sore.", "Depan Gang C RT 10", "07 Nov 2026", StatusLaporan.SELESAI, "Joko Widodo", "081299887766", tanggapanRt = "Saluran telah dibersihkan bersama tim kebersihan."),
            Laporan("l3", "Pohon Tumbang Sebagian", KategoriLaporan.FASILITAS, "Dahan pohon rindang dekat lapangan hampir patah dan menghalangi kabel listrik.", "Dekat Lapangan RT 10", "10 Nov 2026", StatusLaporan.BARU, "Hendra Setiawan", "081399887766"),
            Laporan("l4", "Kekurangan Pasokan Air Bersih", KategoriLaporan.KEKERINGAN, "Tekanan air sumur bor menurun selama musim kemarau di Blok D.", "Blok D RT 10", "11 Nov 2026", StatusLaporan.BARU, "Dedi Supardi", "081255443322")
        )
    )
    val laporanList: StateFlow<List<Laporan>> = _laporanList.asStateFlow()

    fun addLaporan(laporan: Laporan) {
        _laporanList.value = listOf(laporan) + _laporanList.value
        addNotification(
            AppNotification(
                title = "Laporan Baru Diterima",
                message = "${laporan.pelapor}: ${laporan.judul}",
                timestamp = "Baru saja",
                type = NotificationType.LAPORAN,
                forAdmin = true
            )
        )
    }

    fun updateStatusLaporan(laporanId: String, status: StatusLaporan, tanggapan: String? = null) {
        _laporanList.value = _laporanList.value.map {
            if (it.id == laporanId) {
                it.copy(
                    status = status,
                    tanggapanRt = tanggapan ?: it.tanggapanRt,
                    tanggalTanggapan = "11 Nov 2026"
                )
            } else it
        }
        addActivityLog("Tindak Lanjut Laporan", "Mengubah status laporan $laporanId menjadi ${status.name}")
    }

    fun deleteLaporan(laporanId: String) {
        _laporanList.value = _laporanList.value.filterNot { it.id == laporanId }
    }

    // 6. Pengumuman RT (CRUD)
    private val _pengumumanList = MutableStateFlow(
        listOf(
            Pengumuman("p1", "Kerja bakti bersama RT 10", "Dihimbau kepada seluruh bapak/ibu warga RT 10 untuk mengikuti kegiatan kerja bakti pembersihan saluran air menjelang musim hujan.", "15 Nov 2026", "07.00 WIB", "Pos RT 10", "Ketua RT 10", penting = true, isPublished = true),
            Pengumuman("p2", "Pertemuan Rutin Bulanan Warga", "Pertemuan bulanan membahas laporan keuangan kas RT 10 dan evaluasi pos ronda malam.", "20 Nov 2026", "19.30 WIB", "Balai Warga RT 10", "Ketua RT 10", penting = false, isPublished = true),
            Pengumuman("p3", "Jadwal Fogging Nyamuk DBD", "Petugas Puskesmas akan melakukan fogging di seluruh lorong RT 10. Mohon tutup makanan dan tempat penampungan air.", "25 Nov 2026", "08.30 WIB", "Area RT 10", "Ketua RT 10", penting = true, isPublished = true),
            Pengumuman("p4", "Draft: Pemilihan Pengurus Baru", "Pemberitahuan awal mengenai rencana regenerasi kepengurusan RT.", "01 Des 2026", "19.00 WIB", "Balai Warga", "Sekretaris RT", penting = false, isPublished = false)
        )
    )
    val pengumumanList: StateFlow<List<Pengumuman>> = _pengumumanList.asStateFlow()

    fun addPengumuman(p: Pengumuman) {
        _pengumumanList.value = listOf(p) + _pengumumanList.value
        addActivityLog("Publikasi Pengumuman", "Membuat pengumuman baru: ${p.judul}")
    }

    fun updatePengumuman(p: Pengumuman) {
        _pengumumanList.value = _pengumumanList.value.map { if (it.id == p.id) p else it }
    }

    fun deletePengumuman(id: String) {
        _pengumumanList.value = _pengumumanList.value.filterNot { it.id == id }
    }

    // 7. Bantuan Sosial (CRUD)
    private val _programBansosList = MutableStateFlow(
        listOf(
            ProgramBansos("b1", "Bansos Beras & Sembako Bulanan", "November 2026", "Warga Lansia & Subsidi RT 10", 15, "Aktif"),
            ProgramBansos("b2", "Bantuan Posyandu Lansia & Balita", "November 2026", "Balita dan Lansia > 60 th", 20, "Aktif"),
            ProgramBansos("b3", "Santunan Dana Pendidikan Warga", "Semester Genap 2026", "Keluarga Prasejahtera", 8, "Persiapan")
        )
    )
    val programBansosList: StateFlow<List<ProgramBansos>> = _programBansosList.asStateFlow()

    private val _penerimaBansosList = MutableStateFlow(
        listOf(
            PenerimaBansos("pb1", "b1", "Bansos Beras & Sembako", "Siti Rahmawati", "3273010101900003", "Blok C No. 08", "Disalurkan", "08 Nov 2026", "Sudah diserahkan langsung."),
            PenerimaBansos("pb2", "b1", "Bansos Beras & Sembako", "Nurhasanah", "3273010101900006", "Blok C No. 02", "Disalurkan", "08 Nov 2026", "Diterima oleh kepala keluarga."),
            PenerimaBansos("pb3", "b1", "Bansos Beras & Sembako", "Dedi Supardi", "3273010101900005", "Blok D No. 15", "Diverifikasi", catatan = "Menunggu kuota tahap 2.")
        )
    )
    val penerimaBansosList: StateFlow<List<PenerimaBansos>> = _penerimaBansosList.asStateFlow()

    fun updateStatusPenyaluran(penerimaId: String, newStatus: String) {
        _penerimaBansosList.value = _penerimaBansosList.value.map {
            if (it.id == penerimaId) it.copy(statusPenyaluran = newStatus, tanggalPenyaluran = "11 Nov 2026") else it
        }
    }

    // 8. Notifikasi
    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification("n1", "Pembayaran Iuran Masuk", "Ahmad Fauzi (Blok A No. 04) membayar iuran Rp20.000 via QRIS.", "10 menit lalu", NotificationType.PEMBAYARAN, false, forAdmin = true),
            AppNotification("n2", "Laporan Masuk Baru", "Dedi Supardi melaporkan pasokan air bersih di Blok D.", "1 jam lalu", NotificationType.LAPORAN, false, forAdmin = true),
            AppNotification("n3", "Laporan Ditanggapi", "Pengurus RT menanggapi laporan lampu jalan di Blok B.", "2 jam lalu", NotificationType.LAPORAN, true, forAdmin = false),
            AppNotification("n4", "Tagihan Iuran Baru", "Tagihan iuran sampah bulan November telah diterbitkan.", "1 hari lalu", NotificationType.TAGIHAN, true, forAdmin = false)
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map { if (it.id == id) it.copy(isRead = true) else it }
    }

    private fun addNotification(n: AppNotification) {
        _notifications.value = listOf(n) + _notifications.value
    }

    // 9. Activity Logs
    private val _activityLogs = MutableStateFlow(
        listOf(
            ActivityLog("a1", "Verifikasi Pembayaran", "Konfirmasi pembayaran iuran Siti Rahmawati (Blok C No. 08)", "1 jam lalu"),
            ActivityLog("a2", "Tindak Lanjut Aduan", "Mengubah status aduan Lampu Jalan menjadi DIPROSES", "3 jam lalu"),
            ActivityLog("a3", "Publikasi Pengumuman", "Mempublikasikan jadwal Kerja Bakti Minggu 15 Nov", "Kemarin")
        )
    )
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private fun addActivityLog(action: String, description: String) {
        val log = ActivityLog(UUID.randomUUID().toString(), action, description, "Baru saja")
        _activityLogs.value = listOf(log) + _activityLogs.value
    }
}
