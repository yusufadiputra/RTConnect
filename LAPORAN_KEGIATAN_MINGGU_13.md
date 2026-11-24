# LAPORAN KEGIATAN MINGGUAN (SHORT REPORT)
**Minggu ke:** 13  
**Tanggal:** 18 November 2026  
**Proyek:** RTConnect (Sistem Layanan Lingkungan & Warga RT Terpadu)

---

### **Nama Kegiatan:**
Pembuatan Fitur Utama, Implementasi Penuh Operasi CRUD (Create, Read, Update, Delete), Integrasi Cloud Database & REST API Supabase, Sistem Autentikasi & Reset Sandi, Fitur Kamera Bukti Laporan, Standardisasi Wilayah RT 06, serta Peningkatan Antarmuka (Kuitansi Digital, Shortcut WhatsApp, dan Skeleton Loading).

---

### **Penjelasan Kegiatan:**
Pada minggu ke-13 ini dilakukan implementasi fungsional backend menyeluruh, integrasi jaringan REST API, serta peningkatan fitur antarmuka pada aplikasi Android RTConnect berbasis Jetpack Compose. Fokus utama kegiatan adalah menghubungkan aplikasi ke backend cloud database Supabase menggunakan Retrofit dan OkHttp client dengan arsitektur StateFlow dan prinsip Optimistic Update, sehingga setiap data tersimpan secara persisten di cloud.

Kegiatan diawali dengan implementasi modul REST API untuk seluruh entitas sistem: data keluarga/warga (`families`), kategori tarif iuran sampah (`waste_categories`), tagihan iuran bulanan (`waste_bills`), laporan pengaduan lingkungan (`reports`), dan pengumuman resmi RT (`announcements`). Seluruh operasi CRUD (Create, Read, Update, Delete) berhasil dihubungkan dua arah antara antarmuka Compose dan Supabase REST API.

Selanjutnya, diimplementasikan sistem autentikasi terpadu menggunakan Supabase Auth API (`/auth/v1/token` dan `/auth/v1/signup`) yang menyimpan JWT Access Token secara dinamis pada interceptor jaringan. Ditambahkan pula fitur keamanan akun berupa Ganti Kata Sandi pada profil akun, fitur Lupa/Reset Kata Sandi langsung dari halaman Login, serta hak akses bagi Admin RT untuk mendaftarkan akun login warga baru secara langsung di dalam aplikasi. Pada modul Laporan Warga, akses kamera dan galeri perangkat telah diperbaiki sehingga warga dapat langsung melampirkan foto bukti kondisi fisik saat melapor. Selain itu, dilakukan standardisasi penamaan lingkungan menjadi RT 06 / RW 04, perbaikan kontras tema visual Material 3 agar seluruh teks terbaca jelas, integrasi tombol pintas WhatsApp untuk mempermudah koordinasi pengurus dengan warga/pelapor, penerbitan Kuitansi Sah Digital RT 06 berstempel sistem yang dapat dibagikan, serta implementasi Shimmer Skeleton Loading untuk transisi pemuatan data yang lebih halus.

---

### **Hasil Kegiatan:**
- **Integrasi REST API Supabase Berhasil:** Seluruh endpoint REST API Supabase (`families`, `waste_categories`, `waste_bills`, `reports`, `announcements`) berhasil terhubung menggunakan Retrofit & OkHttp.
- **Operasi CRUD Menyeluruh:** Operasi Create, Read, Update, dan Delete untuk Data Warga, Kategori Iuran, Tagihan Sampah, Laporan Aduan, Pengumuman, dan Program Bansos berjalan lancar dan data tersimpan persisten di cloud.
- **Autentikasi Supabase & Manajemen Sesi:** Login Supabase Auth berhasil menghasilkan JWT Access Token dinamis untuk autorisasi request berikutnya.
- **Fitur Ganti Sandi & Reset Sandi:** Fitur Ganti Kata Sandi pada profil pengguna dan Reset Kata Sandi langsung dari halaman Login berhasil diimplementasikan.
- **Pendaftaran Akun Warga oleh Admin:** Pengurus RT dapat mendaftarkan akun login warga langsung dari panel Data Warga dengan sinkronisasi otomatis ke Supabase Auth.
- **Integrasi Kamera & Lampiran Foto:** Formulir laporan aduan warga berhasil membuka kamera dan galeri ponsel untuk melampirkan foto bukti aduan.
- **Standardisasi Identitas Wilayah RT 06:** Seluruh penomoran dan label lingkungan diperbarui secara konsisten menjadi RT 06 / RW 04.
- **Perbaikan Kontras Warna & Aksesibilitas Tema:** Masalah teks yang tidak terbaca karena kesamaan warna latar telah diperbaiki pada seluruh dialog, tombol, dan badge status.
- **Integrasi WhatsApp Pengurus & Warga:** Tombol "Hubungi via WhatsApp" berhasil disematkan pada kartu data warga dan kartu laporan pelapor dengan template pesan otomatis.
- **Penerbitan Kuitansi Sah Digital RT 06:** Tersedia dialog kuitansi sah berstempel sistem lengkap dengan rincian pembayaran, nomor referensi, dan tombol bagikan via WhatsApp.
- **Animasi Shimmer Skeleton Loading:** Komponen pemuatan data berbasis skeleton shimmer berhasil diterapkan pada saat sinkronisasi data dari server.
- **Kompilasi Sukses (Build Successful):** Seluruh berkas kode Kotlin dan konfigurasi Gradle berhasil dikompilasi tanpa error (BUILD SUCCESSFUL).

---

### **Link GitHub:**
https://github.com/yusufadiputra/RTConnect

---

### **Rencana Selanjutnya:**
Pengujian alur transaksi pembayaran payment gateway / QRIS secara end-to-end, pengujian mekanisme offline caching saat perangkat tidak memiliki koneksi internet, serta pembuatan berkas APK rilis untuk pengujian langsung oleh pengurus dan warga RT 06.

---

### **Lampiran Dokumentasi:**

#### **A. Dokumentasi Antarmuka Aplikasi (Mobile UI Real Device):**
1. *Gambar 1. Tampilan Dialog Reset Kata Sandi Akun pada Halaman Login*
2. *Gambar 2. Tampilan Fitur Ganti Kata Sandi pada Menu Profil Akun*
3. *Gambar 3. Tampilan Form Registrasi Akun Warga oleh Admin RT*
4. *Gambar 4. Tampilan Formulir Tambah dan Edit Data Warga (CRUD Data Warga)*
5. *Gambar 5. Tampilan Kartu Data Warga RT 06 Lengkap dengan Tombol Hubungi via WhatsApp*
6. *Gambar 6. Tampilan Form Laporan Aduan Warga dengan Fitur Lampirkan Foto dari Kamera*
7. *Gambar 7. Tampilan Detail Laporan pada Panel Admin dengan Opsi Hubungi Pelapor via WhatsApp*
8. *Gambar 8. Tampilan Dialog Kuitansi Sah Digital RT 06 dengan Tombol Bagikan ke WhatsApp*
9. *Gambar 9. Tampilan Kelola Kategori dan Tarif Iuran Sampah Bulanan (CRUD Kategori)*
10. *Gambar 10. Tampilan Kelola Program dan Penerima Bantuan Sosial Warga (CRUD Bansos)*
11. *Gambar 11. Tampilan Animasi Shimmer Skeleton Loading saat Pemuatan Data dari Cloud*
12. *Gambar 12. Tampilan Pembaruan Kontras Warna dan Teks Tema pada Seluruh Komponen Antarmuka RT 06*

#### **B. Dokumentasi Pengerjaan & Koordinasi Tim Pengembang:**
13. *Gambar 13. Dokumentasi Diskusi Arsitektur Jaringan REST API dan Skema Database Supabase*
14. *Gambar 14. Dokumentasi Implementasi Kode Retrofit, CRUD Repository, dan Jetpack Compose*
15. *Gambar 15. Dokumentasi Pengujian Fitur Kamera, Reset Kata Sandi, dan WhatsApp pada Perangkat Android*
16. *Gambar 16. Dokumentasi Evaluasi Hasil Kompilasi Akhir (Build Successful) Bersama Tim Pengembang*
