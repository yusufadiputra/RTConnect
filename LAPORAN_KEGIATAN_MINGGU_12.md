# LAPORAN KEGIATAN MINGGUAN (SHORT REPORT)
**Minggu ke:** 12  
**Tanggal:** 11 November 2026  
**Proyek:** RTConnect (Sistem Layanan Lingkungan & Warga RT Terpadu)

---

### **Nama Kegiatan:**
Implementasi Antarmuka Android RTConnect: Splash Screen, Ketentuan Layanan (One-Time Terms of Service), Unified Login dengan Smart Role Detection, Navigasi Bottom Bar Responsif, Penataan Komponen Dasbor, dan Pembaruan Aset Ikon Aplikasi.

---

### **Penjelasan Kegiatan:**
Pada minggu ke-12 ini, kegiatan berfokus pada implementasi menyeluruh antarmuka pengguna (UI/UX) pada platform Android menggunakan Jetpack Compose, perbaikan arsitektur navigasi, serta pengujian langsung pada perangkat fisik (*real device*).

Rangkaian kegiatan yang dilaksanakan meliputi:
1. **Pembuatan Splash Screen:** Merancang tampilan awal (*splash screen*) bertema pemukiman pintar (*smart neighborhood*) dengan latar gradien warna brand RTConnect (`RTNavy` dan `RTTeal`), animasi *pulsing* logo, dan transisi otomatis menuju layar berikutnya.
2. **Implementasi One-Time Terms of Service (ToS):** Membangun halaman persetujuan Ketentuan Layanan & Kebijakan Privasi yang hanya muncul satu kali saat aplikasi pertama kali dipasang. Status persetujuan disimpan secara persisten pada `SharedPreferences` sehingga pada pembukaan aplikasi selanjutnya pengguna langsung diarahkan ke layar Login. Penyesuaian *window insets* (`navigationBarsPadding`) juga diterapkan agar tombol aksi tidak tertutup oleh bilah navigasi bawaan Android.
3. **Pengembangan Unified Single Login:** Merombak alur autentikasi dari sebelumnya menggunakan toggle manual yang kaku menjadi satu form login yang bersih, modern, dan dilengkapi *Smart Role Detection*. Sistem dapat secara otomatis mendeteksi apakah akun yang dimasukkan merupakan pengurus RT (diarahkan ke Panel Admin RT) atau warga (diarahkan ke Beranda Warga). Tampilan login dipercantik dengan kartu *elevated*, ikon bertema rumah, serta chip preset pengujian.
4. **Optimalisasi Navigasi & Drawer Admin:** Memperbaiki sistem *ModalNavigationDrawer* admin agar tidak terbuka otomatis saat pertama kali login dan hanya dapat diakses melalui tombol menu (☰). Selain itu, tata letak *Bottom Navigation Bar* admin dirapikan dengan pembatasan teks satu baris (`maxLines = 1`) agar seluruh 5 tab menu sejajar presisi.
5. **Perbaikan Komponen Responsif (Bug Fixing Layout):** Memperbaiki kartu grafik bulanan (*MonthlyPaymentBarChart*) dan badge status (*RTStatusBadge*) yang sebelumnya mengalami penyusutan teks vertikal akibat ketiadaan pembagian *weight* pada komponen baris (*Row*).
6. **Integrasi Aset App Icon 3D:** Mengonversi desain logo 3D rumah pintar RTConnect ke seluruh tingkat resolusi *mipmap* Android (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) beserta *adaptive icon* dengan latar putih bersih.
7. **Pengujian pada Perangkat Fisik:** Melakukan kompilasi Gradle (`assembleDebug`) dan *deployment* aplikasi secara langsung ke smartphone Android 15 (Infinix X6871) untuk memastikan performa yang mulus dan bebas error.

---

### **Hasil Kegiatan:**
1. **Splash Screen & Onboarding ToS Berfungsi Sempurna:** Transisi splash berjalan lancar, dan halaman ketentuan layanan hanya muncul satu kali sesuai rancangan.
2. **Form Login Terpadu yang Estetik:** Berhasil memvalidasi kredensial dan mengarahkan pengguna secara presisi ke Dasbor Admin RT maupun Beranda Warga berdasarkan peran akun.
3. **Navigasi Dasbor & Bottom Bar yang Stabil:** Menu bar bawah tampil sejajar rapi 1 baris tanpa pemotongan kata, serta drawer samping tertutup secara default saat pengguna masuk.
4. **Tata Letak Bebas Glitch:** Komponen grafik, badge persentase kepatuhan warga ("Rata-rata 90%"), dan daftar warga telah responsif di berbagai resolusi layar.
5. **Aplikasi Terpasang dengan Ikon 3D Resmi:** Ikon peluncur (*launcher icon*) aplikasi di layar beranda ponsel tampil modern dan tajam.
6. **Build Sukses & Lolos Pengujian:** Aplikasi berjalan stabil pada perangkat fisik Android 15 tanpa kendala crash maupun kompatibilitas AGP.

---

### **Link Repository:**
- **GitHub:** *(Masukkan link repository GitHub RTConnect Anda)*

---

### **Rencana Selanjutnya:**
1. Pembuatan dan integrasi arsitektur database lokal (Room Database / Supabase REST API) untuk sinkronisasi data real-time.
2. Implementasi penuh operasi CRUD (Create, Read, Update, Delete) pada modul Data Warga dan Kategori Iuran Sampah.
3. Pengembangan fungsionalitas pengajuan formulir Lapor Masalah berbasis unggah foto bukti dan integrasi alur verifikasi berkas oleh Ketua RT.

---

### **Lampiran Dokumentasi:**

#### **A. Dokumentasi Antarmuka Aplikasi (Mobile UI):**
1. **Splash Screen & Launcher Icon:** Tampilan splash screen bertema smart community dengan logo 3D resmi RTConnect.
2. **Ketentuan Layanan (ToS):** Tampilan persetujuan layanan dengan proteksi sistem insets navigasi bawah.
3. **Unified Login Screen:** Form login terpadu dengan deteksi peran otomatis (*Admin / Warga*).
4. **Dasbor Admin RT:** Tampilan utama admin dengan grafik kepatuhan iuran, ringkasan kas, dan bottom bar 5 menu yang sejajar.
5. **Manajemen Data Warga & Laporan:** Daftar keluarga dengan status iuran yang responsif dan rapi.

#### **B. Dokumentasi Pengerjaan Tim:**
- Foto koordinasi bersama tim pengembang dalam proses pemrograman, *code review*, dan pengujian layout antarmuka di laboratorium/ruang kerja.

