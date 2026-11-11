package com.example.rtconnect.data.model

import java.util.UUID

// Role Pengguna
enum class UserRole {
    ADMIN, WARGA
}

// Data Profil Pengguna
data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val role: UserRole = UserRole.WARGA,
    val avatarUrl: String? = null,
    val address: String = "RT 10 / RW 04"
)

// Data Warga / Keluarga
data class Warga(
    val id: String = UUID.randomUUID().toString(),
    val noKk: String = "",
    val nikKepalaKeluarga: String = "3273010101900001",
    val namaKepalaKeluarga: String = "",
    val blokRumah: String = "",
    val noHp: String = "",
    val jumlahAnggota: Int = 1,
    val kategoriIuranId: String = "kat-reguler",
    val kategoriIuranNama: String = "Reguler",
    val statusEkonomi: StatusEkonomi = StatusEkonomi.REGULER,
    val statusAkun: StatusAkun = StatusAkun.AKTIF,
    val statusIuran: StatusIuran = StatusIuran.LUNAS
)

enum class StatusEkonomi {
    REGULER, SUBSIDI, PENGUSAHA
}

enum class StatusAkun {
    AKTIF, NONAKTIF, PENDING
}

// Master Kategori Iuran
data class KategoriIuran(
    val id: String = UUID.randomUUID().toString(),
    val nama: String,
    val nominal: Long,
    val deskripsi: String,
    val isActive: Boolean = true
)

// Status Iuran & Pembayaran
enum class StatusIuran {
    BELUM_DIBAYAR,
    BELUM_LUNAS,
    MENUNGGU_PEMBAYARAN,
    MENUNGGU_KONFIRMASI,
    LUNAS,
    GAGAL
}

// Tagihan Iuran Sampah Bulanan
data class TagihanIuran(
    val id: String = UUID.randomUUID().toString(),
    val wargaId: String = "w1",
    val namaWarga: String = "Budi Santoso",
    val blokRumah: String = "Blok B No. 12",
    val kategoriNama: String = "Reguler",
    val judul: String,
    val periodeBulan: Int = 11,
    val periodeTahun: Int = 2026,
    val periodeNama: String = "November 2026",
    val nominal: Long,
    val batasWaktu: String,
    val status: StatusIuran = StatusIuran.BELUM_DIBAYAR,
    val tanggalBayar: String? = null,
    val metodeBayar: String? = null,
    val namaPetugasPencatat: String? = null
) {
    val periode: String get() = periodeNama
}

// Laporan & Pengaduan Warga
data class Laporan(
    val id: String = UUID.randomUUID().toString(),
    val judul: String,
    val kategori: KategoriLaporan,
    val deskripsi: String,
    val lokasi: String,
    val tanggal: String,
    val status: StatusLaporan = StatusLaporan.BARU,
    val pelapor: String,
    val pelaporPhone: String = "081234567890",
    val fotoRes: Int? = null,
    val fotoUrl: String? = null,
    val tanggapanRt: String? = null,
    val tanggalTanggapan: String? = null
)

enum class KategoriLaporan {
    SAMPAH, KEKERINGAN, KEAMANAN, KEBERSIHAN, LINGKUNGAN, FASILITAS, UMUM, LAINNYA
}

enum class StatusLaporan {
    BARU, DIPROSES, SELESAI, DITOLAK
}

// Pengumuman RT
data class Pengumuman(
    val id: String = UUID.randomUUID().toString(),
    val judul: String,
    val isi: String,
    val tanggal: String,
    val waktu: String = "07.00 WIB",
    val lokasi: String = "Pos RT 10",
    val penulis: String = "Ketua RT 10",
    val penting: Boolean = false,
    val isPublished: Boolean = true,
    val fotoUrl: String? = null
)

// Bantuan Sosial (Privat)
data class ProgramBansos(
    val id: String = UUID.randomUUID().toString(),
    val namaProgram: String,
    val periode: String,
    val kriteria: String,
    val kuota: Int,
    val status: String = "Aktif",
    val isActive: Boolean = true
)

data class BantuanSosial(
    val id: String = UUID.randomUUID().toString(),
    val namaProgram: String,
    val periode: String,
    val kriteria: String,
    val status: String = "Aktif",
    val jumlahPenerima: Int = 10
)

data class PenerimaBansos(
    val id: String = UUID.randomUUID().toString(),
    val programId: String,
    val programNama: String,
    val namaPenerima: String,
    val noKk: String,
    val blokRumah: String,
    val statusPenyaluran: String = "Terdaftar", // Terdaftar, Diverifikasi, Disalurkan
    val tanggalPenyaluran: String? = null,
    val catatan: String = ""
)

// Notifikasi
data class AppNotification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val timestamp: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val forAdmin: Boolean = false
)

enum class NotificationType {
    TAGIHAN, LAPORAN, PENGUMUMAN, PEMBAYARAN, BANSOS
}

// Audit Aktivitas Admin
data class ActivityLog(
    val id: String = UUID.randomUUID().toString(),
    val action: String,
    val description: String,
    val timeAgo: String
)
