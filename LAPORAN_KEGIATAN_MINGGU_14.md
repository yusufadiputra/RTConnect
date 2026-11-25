# LAPORAN KEGIATAN MINGGUAN (SHORT REPORT)
**Minggu ke:** 14  
**Tanggal:** 25 November 2026  
**Proyek:** RTConnect (Sistem Layanan Lingkungan & Warga RT Terpadu)

---

### **Nama Kegiatan:**
Integrasi Payment Gateway Duitku QRIS Sandbox, Implementasi Opsi Pembayaran Tunai dengan Bukti Kuitansi Fisik, Fitur Atur Tarif Khusus & Tanggal Jatuh Tempo Kustom, Penyempurnaan Tampilan Beranda & Kuitansi Digital Lunas, Implementasi Fitur Pull-to-Refresh Material 3, serta Perbaikan Respon Antarmuka Form Login (IME Padding).

---

### **Penjelasan Kegiatan:**
Pada minggu ke-14 ini dilakukan implementasi modul transaksi pembayaran digital, perbaikan interaksi antarmuka pengguna (UI/UX), serta sinkronisasi data tagihan dan kependudukan pada aplikasi Android RTConnect berbasis Jetpack Compose. Kegiatan diawali dengan integrasi sistem pembayaran payment gateway Duitku Sandbox untuk metode pembayaran QRIS resmi. Implementasi ini mencakup pembuatan permintaan transaksi (Inquiry API v2) secara dinamis menggunakan MD5 signature generator, penerimaan kode string QRIS (`qrString`), serta perenderan kode QR secara langsung pada antarmuka aplikasi. Bukti keberhasilan integrasi payment gateway diverifikasi langsung melalui simulator transaksi Duitku yang terhubung ke jaringan e-wallet (GoPay) secara nyata.

Di samping pembayaran digital, diimplementasikan pula opsi pembayaran tunai langsung kepada pengurus RT (Ketua/Bendahara RT 06). Opsi ini mewajibkan warga mengunggah foto fisik kuitansi serah terima pembayaran menggunakan integrasi kamera ponsel atau galeri perangkat, guna menjamin akuntabilitas pencatatan manual. Pada sisi pengurus (Admin RT), dikembangkan fitur penetapan tarif iuran khusus dan kustomisasi tanggal jatuh tempo pembayaran bulanan per warga pada modul Kategori & Tarif Iuran, yang secara otomatis menyinkronkan batas waktu pembayaran tagihan warga terkait.

Penyempurnaan antarmuka beranda warga juga diselesaikan, di mana tagihan yang telah lunas menampilkan tombol "Lihat Bukti Pembayaran" yang langsung membuka Kuitansi Digital berstempel sistem, serta menyembunyikan label tanggal jatuh tempo ketika status sudah LUNAS. Untuk meningkatkan keandalan pembaruan data, fitur Pull-to-Refresh berbasis Material 3 (`PullToRefreshBox`) diterapkan pada Beranda Warga agar warga dapat memuat ulang data tagihan dan kependudukan langsung dari cloud Supabase. Terakhir, masalah tumpang tindih papan ketik (keyboard overlap) pada form Login berhasil diperbaiki dengan penyesuaian *WindowInsets.ime* dan penambahan *dynamic spacer*. Seluruh pembaruan telah dikompilasi (assembleDebug) dan diverifikasi berjalan lancar pada perangkat smartphone fisik.

---

### **Hasil Kegiatan:**
- **Integrasi Duitku QRIS Sandbox:** Pembuatan transaksi QRIS dinamis melalui Duitku Inquiry API berhasil diterapkan lengkap dengan pembuatan signature MD5, dialog QRIS resmi, dan simulator pembayaran sandbox.
- **Verifikasi Nyata Payment Gateway:** Transaksi QRIS Duitku berhasil direview langsung oleh sistem e-wallet (GoPay / ShopeePay) dengan nominal tagihan yang sesuai.
- **Opsi Pembayaran Tunai & Upload Bukti:** Warga dapat memilih opsi pembayaran langsung ke pengurus RT dengan verifikasi lampiran foto kuitansi fisik via kamera/galeri ponsel.
- **Fitur Kustomisasi Tarif Khusus & Jatuh Tempo:** Admin RT dapat mengatur nominal iuran khusus dan tanggal jatuh tempo bulanan spesifik per kepala keluarga dengan pembaruan otomatis ke data tagihan.
- **Penyempurnaan Beranda & Kuitansi Lunas:** Tagihan berstatus LUNAS menyembunyikan teks jatuh tempo dan menyediakan tombol "Lihat Bukti Pembayaran" yang terhubung langsung ke dialog Kuitansi Sah Digital RT 06.
- **Fitur Pull-to-Refresh Material 3:** Halaman Beranda Warga dilengkapi gestur tarik layar ke bawah (*pull-to-refresh*) untuk sinkronisasi pembaruan data tagihan secara real-time.
- **Perbaikan Responsivitas Keyboard Form Login:** Pengaturan *contentWindowInsets* (IME Padding) pada halaman Login memastikan kolom kata sandi dan tombol masuk tidak terhalang oleh keyboard virtual.
- **Pemisahan Konteks Menu Warga dan Menu Iuran:** Menu Data Warga pada panel admin disederhanakan dengan memusatkan status pembayaran iuran secara rapi di Menu Pembayaran Iuran.
- **Kompilasi Sukses (Build Successful):** Seluruh modul berhasil dikompilasi tanpa galat (*assembleDebug*) dan berjalan stabil pada perangkat smartphone Android fisik.

---

### **Link GitHub:**
https://github.com/yusufadiputra/RTConnect

---

### **Rencana Selanjutnya:**
Pengujian komprehensif seluruh alur aplikasi (end-to-end testing), persiapan rilis APK produksi untuk demonstrasi dan evaluasi bersama pengurus RT 06, serta penyusunan buku panduan penggunaan aplikasi bagi warga dan pengurus.

---

### **Lampiran:**

#### **A. Dokumentasi Antarmuka Aplikasi (Mobile UI Real Device):**

![Gambar 1. Tampilan Halaman Pilih Metode Pembayaran Iuran Warga](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578187213_6e6a7623.jpg)  
**Gambar 1. Tampilan Halaman Pilih Metode Pembayaran Iuran Warga (QRIS & Tunai)**

![Gambar 2. Tampilan Dialog QRIS Duitku Sandbox Resmi](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578195592_7d6b3164.jpg)  
**Gambar 2. Tampilan Dialog QRIS Duitku Sandbox Resmi dengan Tombol Simulator dan Konfirmasi Sukses**

![Gambar 3. Bukti Transaksi Nyata Review Pembayaran Duitku - Airpay Shopee via E-Wallet (GoPay)](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578744359_8955d0a5.jpg)  
**Gambar 3. Bukti Transaksi Nyata Review Pembayaran Duitku - Airpay Shopee via E-Wallet (GoPay)**

![Gambar 4. Tampilan Opsi Pembayaran Tunai ke Pengurus RT](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578267180_40d25391.jpg)  
**Gambar 4. Tampilan Opsi Pembayaran Tunai ke Pengurus RT dengan Kewajiban Unggah Bukti Kuitansi**

![Gambar 5. Tampilan Beranda Warga dengan Tagihan Berstatus Lunas](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578287034_d67862a7.jpg)  
**Gambar 5. Tampilan Beranda Warga dengan Tagihan Berstatus LUNAS dan Tombol Lihat Bukti Pembayaran**

![Gambar 6. Tampilan Dialog Kuitansi Sah Digital RT 06](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578295328_38168f0c.jpg)  
**Gambar 6. Tampilan Dialog Kuitansi Sah Digital RT 06 Berstempel Sistem Lengkap Rincian Transaksi**

![Gambar 7. Tampilan Animasi Pull-to-Refresh pada Beranda Warga](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578304217_64f5c707.jpg)  
**Gambar 7. Tampilan Animasi Pull-to-Refresh pada Beranda Warga untuk Sinkronisasi Data Instan**

![Gambar 8. Tampilan Tab Tarif Khusus Warga pada Menu Kategori & Tarif Iuran](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578315992_87c476ac.jpg)  
**Gambar 8. Tampilan Tab Tarif Khusus Warga pada Menu Kategori & Tarif Iuran Panel Admin**

![Gambar 9. Tampilan Dialog Atur Tarif Khusus Warga](C:/Users/User/.gemini/antigravity/brain/abef5ef6-9da9-4d74-89f7-45ecb02d4659/.user_uploaded/media_1795578323403_314ed723.jpg)  
**Gambar 9. Tampilan Dialog Atur Tarif Khusus Warga Lengkap dengan Input Tanggal Jatuh Tempo**

---

#### **B. Dokumentasi Pengerjaan & Koordinasi Tim Pengembang:**
*(Dapat dilampirkan foto dokumentasi koordinasi dan pengerjaan kode bersama tim pengembang)*
