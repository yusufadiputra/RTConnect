package com.example.rtconnect.navigation

sealed class Screen(val route: String) {
    // Intro & Onboarding
    object Splash : Screen("splash")
    object TermsOfService : Screen("terms_of_service")

    // Auth (Unified Login)
    object Login : Screen("login")
    object LoginWarga : Screen("login_warga")
    object LoginAdmin : Screen("login_admin")

    // Warga Bottom Bar Tabs
    object Beranda : Screen("warga_beranda")
    object Riwayat : Screen("warga_riwayat")
    object Laporan : Screen("warga_laporan")
    object Profil : Screen("warga_profil")

    // Warga Sub-Screens
    object BayarIuran : Screen("warga_bayar_iuran")
    object PembayaranBerhasil : Screen("warga_pembayaran_berhasil")
    object LaporMasalah : Screen("warga_lapor_masalah")
    object LaporanTerkirim : Screen("warga_laporan_terkirim")
    object DetailLaporan : Screen("warga_detail_laporan/{laporanId}") {
        fun createRoute(laporanId: String) = "warga_detail_laporan/$laporanId"
    }
    object Pengumuman : Screen("warga_pengumuman")
    object DetailPengumuman : Screen("warga_detail_pengumuman/{pengumumanId}") {
        fun createRoute(pengumumanId: String) = "warga_detail_pengumuman/$pengumumanId"
    }
    object BantuanSosial : Screen("warga_bantuan_sosial")
    object Notifikasi : Screen("warga_notifikasi")

    // Admin Mobile Bottom Bar Tabs
    object AdminDashboard : Screen("admin_dashboard")
    object AdminWarga : Screen("admin_warga")
    object AdminPembayaran : Screen("admin_pembayaran")
    object AdminLaporan : Screen("admin_laporan")
    object AdminProfil : Screen("admin_profil")

    // Admin Sub-Screens
    object AdminKategori : Screen("admin_kategori")
    object AdminPengumuman : Screen("admin_pengumuman")
    object AdminBansos : Screen("admin_bansos")
    object AdminNotifikasi : Screen("admin_notifikasi")
    object AdminStatistik : Screen("admin_statistik")
}
