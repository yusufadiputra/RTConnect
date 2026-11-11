# LAPORAN KEGIATAN MINGGUAN (SHORT REPORT)
**Minggu ke:** 12  
**Tanggal:** 11 November 2026  
**Proyek:** RTConnect (Sistem Layanan Lingkungan & Warga RT Terpadu)

---

### **Nama Kegiatan:**
Implementasi antarmuka Android RTConnect: Splash Screen, Ketentuan Layanan (One-Time Terms of Service), Unified Single Login dengan Smart Role Detection, Navigasi Bottom Bar & Sidebar Drawer Admin, Fitur Beranda & Layanan Warga, serta Pembaruan Aset Ikon Aplikasi 3D.

---

### **Penjelasan Kegiatan:**
Pada minggu ke-12 ini dilakukan implementasi antarmuka pengguna (UI/UX) dan sistem navigasi menyeluruh pada aplikasi Android RTConnect berbasis Jetpack Compose. Kegiatan diawali dengan pembuatan Splash Screen bertema smart neighborhood yang menampilkan logo 3D resmi RTConnect dengan transisi otomatis. Selanjutnya diimplementasikan halaman Ketentuan Layanan (Terms of Service) yang hanya muncul satu kali saat aplikasi pertama kali dipasang dengan penyimpanan lokal persisten menggunakan SharedPreferences, serta penyesuaian window insets (navigationBarsPadding) agar tombol konfirmasi tidak tertutup oleh tombol navigasi sistem Android.

Setelah itu, dilakukan implementasi formulir Login tunggal (Unified Login) yang bersih dan modern tanpa toggle peran yang kaku, dilengkapi fitur Smart Role Detection yang dapat mendeteksi identitas akun secara otomatis: mengarahkan pengguna ke Dasbor Admin RT jika menggunakan akun pengurus (contoh: ketua.rt10) dan mengarahkan ke Beranda Warga jika menggunakan nomor ponsel atau akun warga terdaftar. Pada sisi Admin RT, dibangun panel Dasbor dengan ringkasan kepala keluarga, jumlah warga, grafik kepatuhan iuran sampah bulanan (MonthlyPaymentBarChart), pengelolaan data warga RT 10, pencatatan kasir pembayaran iuran, manajemen tanggapan laporan warga, profil pengurus RT, serta sidebar drawer untuk menu tambahan. Sedangkan pada sisi Warga, diimplementasikan Beranda Warga dengan status tagihan iuran sampah, menu layanan warga (Profil Keluarga, Lapor Masalah, Pengumuman, Bantuan Sosial), riwayat pembayaran iuran lengkap dengan metode transaksi (QRIS/Transfer/Tunai), status dan tindak lanjut laporan lingkungan, serta halaman Profil Keluarga. Seluruh perbaikan bug tata letak (label bottom bar satu baris dan proteksi teks badge status) telah diterapkan, dan aplikasi berhasil diuji serta berjalan stabil pada smartphone Android 15 fisik.

---

### **Hasil Kegiatan:**
- Splash Screen berhasil dibuat dengan logo 3D RTConnect dan animasi transisi otomatis.
- Halaman Ketentuan Layanan (ToS) berhasil diimplementasikan dengan SharedPreferences dan proteksi insets navigasi HP.
- Halaman Single Login terpadu berhasil dibuat lengkap dengan validasi dan smart role detection untuk membedakan hak akses Admin RT dan Warga.
- Panel Dasbor Admin RT berhasil menampilkan ringkasan demografi warga (KK dan jiwa), grafik kepatuhan iuran, dan status laporan lingkungan.
- Halaman Data Warga RT 10 berhasil menampilkan daftar keluarga terdaftar lengkap dengan filter status ekonomi dan badge status iuran.
- Halaman Pembayaran Sampah Admin berhasil memuat rekap kas iuran bulanan dan tombol pencatatan tunai kasir.
- Halaman Manajemen Laporan Admin berhasil menampilkan daftar keluhan warga beserta fitur tindak lanjut dan tanggapan RT.
- Halaman Profil Pengurus RT dan Sidebar Drawer navigasi admin berhasil diimplementasikan dan tertutup secara default saat login.
- Halaman Beranda Warga berhasil menampilkan status iuran sampah aktif, shortcut menu layanan warga, dan kabar lingkungan terkini.
- Halaman Riwayat Iuran Warga berhasil menampilkan daftar catatan pembayaran bulanan (QRIS, Transfer Bank, dan Tunai).
- Halaman Status Laporan Warga berhasil menampilkan progres pengaduan warga dengan badge status (Diproses, Selesai, Baru).
- Halaman Profil Keluarga berhasil memuat informasi kartu keluarga (No. KK, alamat blok, jumlah anggota) dan fitur Logout.
- Seluruh komponen antarmuka responsif tanpa pemotongan teks pada berbagai resolusi layar.
- Ikon aplikasi 3D RTConnect berhasil dipasang pada launcher homescreen perangkat smartphone.
- Seluruh modul aplikasi berhasil dikompilasi (assembleDebug) dan berjalan lancar tanpa error pada perangkat Infinix Android 15.

---

### **Link GitHub:**
https://github.com/yusufadiputra/RTConnect

---

### **Rencana Selanjutnya:**
Pembuatan fitur utama, Implementasi CRUD, database lokal (Room DB) / Supabase REST API, dan integrasi fitur unggah foto bukti laporan warga.

---

### **Lampiran Dokumentasi:**

#### **A. Dokumentasi Antarmuka Aplikasi (Mobile UI Real Device):**
1. *Gambar 1. Tampilan Splash Screen Aplikasi RTConnect pada Perangkat Fisik*
2. *Gambar 2. Tampilan Ketentuan Layanan (ToS) dengan Proteksi Insets Navigasi*
3. *Gambar 3. Tampilan Halaman Single Login dengan Smart Role Detection*
4. *Gambar 4. Tampilan Dasbor Utama Admin RT 10 & Grafik Kepatuhan Iuran*
5. *Gambar 5. Tampilan Modul Data Warga RT 10 dan Status Kependudukan*
6. *Gambar 6. Tampilan Modul Pembayaran Sampah & Kasir Pencatatan Tunai*
7. *Gambar 7. Tampilan Manajemen Laporan & Aduan Warga pada Panel Admin*
8. *Gambar 8. Tampilan Profil Pengurus RT 10 (Ketua RT)*
9. *Gambar 9. Tampilan Sidebar Navigation Drawer Admin RT*
10. *Gambar 10. Tampilan Beranda Warga RT 10 & Layanan Pengaduan Lingkungan*
11. *Gambar 11. Tampilan Riwayat Pembayaran Iuran Sampah & Keamanan Warga*
12. *Gambar 12. Tampilan Status Tindak Lanjut Pengaduan Lingkungan oleh Warga*
13. *Gambar 13. Tampilan Profil Keluarga Warga dan Informasi Kependudukan*

#### **B. Dokumentasi Pengerjaan & Koordinasi Tim Pengembang:**
14. *Gambar 14. Dokumentasi Kolaborasi & Pengerjaan Kode Bersama Tim (Sudut Depan)*
15. *Gambar 15. Dokumentasi Koordinasi dan Implementasi Arsitektur Jetpack Compose*
16. *Gambar 16. Dokumentasi Evaluasi Desain UI dan Penataan Komponen (Sudut Belakang)*
17. *Gambar 17. Dokumentasi Pengujian Langsung Aplikasi pada Real Device*
