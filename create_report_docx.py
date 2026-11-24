import os
import docx
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

def create_report():
    doc = docx.Document()

    # 1. Page Setup (A4, 1 inch margins)
    for section in doc.sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)
        section.page_width = Inches(8.27)
        section.page_height = Inches(11.69)

    # Colors
    NAVY = RGBColor(16, 45, 69)      # #102D45 (RTNavy)
    TEAL = RGBColor(8, 127, 122)     # #087F7A (RTTeal)
    GRAY = RGBColor(100, 116, 139)   # #64748B (RTMuted)
    DARK = RGBColor(30, 41, 59)      # #1E293B (RTText)

    def set_font(run, name="Calibri", size_pt=11, bold=False, italic=False, color=DARK):
        run.font.name = name
        run.font.size = Pt(size_pt)
        run.bold = bold
        run.italic = italic
        run.font.color.rgb = color

    def add_heading_styled(text, level=1):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(14)
        p.paragraph_format.space_after = Pt(4)
        p.paragraph_format.keep_with_next = True
        run = p.add_run(text)
        if level == 1:
            set_font(run, "Calibri", 13, bold=True, color=NAVY)
        elif level == 2:
            set_font(run, "Calibri", 12, bold=True, color=TEAL)
        else:
            set_font(run, "Calibri", 11, bold=True, color=NAVY)
        return p

    def add_body_p(text, space_after=6, bold=False, italic=False, color=DARK):
        p = doc.add_paragraph()
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = 1.15
        run = p.add_run(text)
        set_font(run, "Calibri", 11, bold=bold, italic=italic, color=color)
        return p

    def add_bullet_p(text, bold_prefix="", space_after=4):
        p = doc.add_paragraph(style='List Bullet')
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(space_after)
        p.paragraph_format.line_spacing = 1.15
        if bold_prefix:
            r_pre = p.add_run(bold_prefix)
            set_font(r_pre, "Calibri", 11, bold=True, color=DARK)
        r_text = p.add_run(text)
        set_font(r_text, "Calibri", 11, bold=False, color=DARK)
        return p

    # --- Header with Logo ---
    logo_path = os.path.join("app", "src", "main", "res", "drawable", "logo_rtconnect.png")
    if os.path.exists(logo_path):
        p_logo = doc.add_paragraph()
        p_logo.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_logo.paragraph_format.space_after = Pt(4)
        r_logo = p_logo.add_run()
        r_logo.add_picture(logo_path, width=Inches(1.2))

    # Title
    p_title = doc.add_paragraph()
    p_title.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_title.paragraph_format.space_after = Pt(2)
    r_title = p_title.add_run("LAPORAN KEGIATAN MINGGUAN (SHORT REPORT)")
    set_font(r_title, "Calibri", 15, bold=True, color=NAVY)

    # Subtitle / Metadata Card
    meta_table = doc.add_table(rows=3, cols=2)
    meta_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    meta_table.autofit = False

    meta_data = [
        ("Minggu ke:", "13"),
        ("Tanggal:", "18 November 2026"),
        ("Proyek:", "RTConnect (Sistem Layanan Lingkungan & Warga RT Terpadu)")
    ]

    for i, (label, val) in enumerate(meta_data):
        row = meta_table.rows[i]
        # label cell
        cell_lbl = row.cells[0]
        cell_lbl.width = Inches(1.5)
        p_l = cell_lbl.paragraphs[0]
        p_l.paragraph_format.space_after = Pt(2)
        r_l = p_l.add_run(label)
        set_font(r_l, "Calibri", 10.5, bold=True, color=TEAL)

        # val cell
        cell_val = row.cells[1]
        cell_val.width = Inches(4.7)
        p_v = cell_val.paragraphs[0]
        p_v.paragraph_format.space_after = Pt(2)
        r_v = p_v.add_run(val)
        set_font(r_v, "Calibri", 10.5, bold=False, color=DARK)

    # Shading and borders on table
    for row in meta_table.rows:
        for cell in row.cells:
            tcPr = cell._element.get_or_add_tcPr()
            shd = parse_xml(r'<w:shd {} w:fill="F1F5F9"/>'.format(nsdecls('w')))
            tcPr.append(shd)
            borders = parse_xml(
                r'<w:tcBorders {}><w:top w:val="none"/><w:left w:val="none"/><w:right w:val="none"/><w:bottom w:val="none"/></w:tcBorders>'.format(nsdecls('w'))
            )
            tcPr.append(borders)

    # Divider line
    p_div = doc.add_paragraph()
    p_div.paragraph_format.space_before = Pt(8)
    p_div.paragraph_format.space_after = Pt(8)
    r_div = p_div.add_run("―" * 45)
    r_div.font.color.rgb = RGBColor(203, 213, 225)
    p_div.alignment = WD_ALIGN_PARAGRAPH.CENTER

    # --- 1. Nama Kegiatan ---
    add_heading_styled("Nama Kegiatan:", level=1)
    p_nama = add_body_p(
        "Pembuatan Fitur Utama, Implementasi Penuh Operasi CRUD (Create, Read, Update, Delete), "
        "Integrasi Cloud Database & REST API Supabase, Sistem Autentikasi & Reset Sandi, "
        "Fitur Kamera Bukti Laporan, Standardisasi Wilayah RT 06, serta Peningkatan Antarmuka "
        "(Kuitansi Digital, Shortcut WhatsApp, dan Skeleton Loading)."
    )

    # --- 2. Penjelasan Kegiatan ---
    add_heading_styled("Penjelasan Kegiatan:", level=1)
    add_body_p(
        "Pada minggu ke-13 ini dilakukan implementasi fungsional backend menyeluruh, integrasi jaringan REST API, "
        "serta peningkatan fitur antarmuka pada aplikasi Android RTConnect berbasis Jetpack Compose. "
        "Fokus utama kegiatan adalah menghubungkan aplikasi ke backend cloud database Supabase menggunakan Retrofit "
        "dan OkHttp client dengan arsitektur StateFlow dan prinsip Optimistic Update, sehingga setiap data tersimpan "
        "secara persisten di cloud."
    )
    add_body_p(
        "Kegiatan diawali dengan implementasi modul REST API untuk seluruh entitas sistem: data keluarga/warga (families), "
        "kategori tarif iuran sampah (waste_categories), tagihan iuran bulanan (waste_bills), laporan pengaduan lingkungan (reports), "
        "dan pengumuman resmi RT (announcements). Seluruh operasi CRUD (Create, Read, Update, Delete) berhasil dihubungkan dua arah "
        "antara antarmuka Compose dan Supabase REST API."
    )
    add_body_p(
        "Selanjutnya, diimplementasikan sistem autentikasi terpadu menggunakan Supabase Auth API (/auth/v1/token dan /auth/v1/signup) "
        "yang menyimpan JWT Access Token secara dinamis pada interceptor jaringan. Ditambahkan pula fitur keamanan akun berupa "
        "Ganti Kata Sandi pada profil akun, fitur Lupa/Reset Kata Sandi langsung dari halaman Login, serta hak akses bagi Admin RT "
        "untuk mendaftarkan akun login warga baru secara langsung di dalam aplikasi. Pada modul Laporan Warga, akses kamera dan "
        "galeri perangkat telah diperbaiki sehingga warga dapat langsung melampirkan foto bukti kondisi fisik saat melapor. "
        "Selain itu, dilakukan standardisasi penamaan lingkungan menjadi RT 06 / RW 04, perbaikan kontras tema visual Material 3 "
        "agar seluruh teks terbaca jelas, integrasi tombol pintas WhatsApp untuk mempermudah koordinasi pengurus dengan warga/pelapor, "
        "penerbitan Kuitansi Sah Digital RT 06 berstempel sistem yang dapat dibagikan, serta implementasi Shimmer Skeleton Loading "
        "untuk transisi pemuatan data yang lebih halus."
    )

    # --- 3. Hasil Kegiatan ---
    add_heading_styled("Hasil Kegiatan:", level=1)
    results = [
        ("Integrasi REST API Supabase Berhasil: ", "Seluruh endpoint REST API Supabase (families, waste_categories, waste_bills, reports, announcements) berhasil terhubung menggunakan Retrofit & OkHttp."),
        ("Operasi CRUD Menyeluruh: ", "Operasi Create, Read, Update, dan Delete untuk Data Warga, Kategori Iuran, Tagihan Sampah, Laporan Aduan, Pengumuman, dan Program Bansos berjalan lancar dan data tersimpan persisten di cloud."),
        ("Autentikasi Supabase & Manajemen Sesi: ", "Login Supabase Auth berhasil menghasilkan JWT Access Token dinamis untuk autorisasi request berikutnya."),
        ("Fitur Ganti Sandi & Reset Sandi: ", "Fitur Ganti Kata Sandi pada profil pengguna dan Reset Kata Sandi langsung dari halaman Login berhasil diimplementasikan."),
        ("Pendaftaran Akun Warga oleh Admin: ", "Pengurus RT dapat mendaftarkan akun login warga langsung dari panel Data Warga dengan sinkronisasi otomatis ke Supabase Auth."),
        ("Integrasi Kamera & Lampiran Foto: ", "Formulir laporan aduan warga berhasil membuka kamera dan galeri ponsel untuk melampirkan foto bukti aduan."),
        ("Standardisasi Identitas Wilayah RT 06: ", "Seluruh penomoran dan label lingkungan diperbarui secara konsisten menjadi RT 06 / RW 04."),
        ("Perbaikan Kontras Warna & Aksesibilitas Tema: ", "Masalah teks yang tidak terbaca karena kesamaan warna latar telah diperbaiki pada seluruh dialog, tombol, dan badge status."),
        ("Integrasi WhatsApp Pengurus & Warga: ", "Tombol 'Hubungi via WhatsApp' berhasil disematkan pada kartu data warga dan kartu laporan pelapor dengan template pesan otomatis."),
        ("Penerbitan Kuitansi Sah Digital RT 06: ", "Tersedia dialog kuitansi sah berstempel sistem lengkap dengan rincian pembayaran, nomor referensi, dan tombol bagikan via WhatsApp."),
        ("Animasi Shimmer Skeleton Loading: ", "Komponen pemuatan data berbasis skeleton shimmer berhasil diterapkan pada saat sinkronisasi data dari server."),
        ("Kompilasi Sukses (Build Successful): ", "Seluruh berkas kode Kotlin dan konfigurasi Gradle berhasil dikompilasi tanpa error (BUILD SUCCESSFUL).")
    ]
    for bold_pre, desc in results:
        add_bullet_p(desc, bold_prefix=bold_pre)

    # --- 4. Link GitHub ---
    add_heading_styled("Link GitHub:", level=1)
    p_git = add_body_p("https://github.com/yusufadiputra/RTConnect", bold=True, color=TEAL)

    # --- 5. Rencana Selanjutnya ---
    add_heading_styled("Rencana Selanjutnya:", level=1)
    add_body_p(
        "Pengujian alur transaksi pembayaran payment gateway / QRIS secara end-to-end, "
        "pengujian mekanisme offline caching saat perangkat tidak memiliki koneksi internet, "
        "serta pembuatan berkas APK rilis untuk pengujian langsung oleh pengurus dan warga RT 06."
    )

    # --- 6. Lampiran Dokumentasi ---
    add_heading_styled("Lampiran Dokumentasi:", level=1)

    # Sub A
    add_heading_styled("A. Dokumentasi Antarmuka Aplikasi (Mobile UI Real Device):", level=2)
    
    screenshots_a = [
        "Gambar 1. Tampilan Dialog Reset Kata Sandi Akun pada Halaman Login",
        "Gambar 2. Tampilan Fitur Ganti Kata Sandi pada Menu Profil Akun",
        "Gambar 3. Tampilan Form Registrasi Akun Warga oleh Admin RT",
        "Gambar 4. Tampilan Formulir Tambah dan Edit Data Warga (CRUD Data Warga)",
        "Gambar 5. Tampilan Kartu Data Warga RT 06 Lengkap dengan Tombol Hubungi via WhatsApp",
        "Gambar 6. Tampilan Form Laporan Aduan Warga dengan Fitur Lampirkan Foto dari Kamera",
        "Gambar 7. Tampilan Detail Laporan pada Panel Admin dengan Opsi Hubungi Pelapor via WhatsApp",
        "Gambar 8. Tampilan Dialog Kuitansi Sah Digital RT 06 dengan Tombol Bagikan ke WhatsApp",
        "Gambar 9. Tampilan Kelola Kategori dan Tarif Iuran Sampah Bulanan (CRUD Kategori)",
        "Gambar 10. Tampilan Kelola Program dan Penerima Bantuan Sosial Warga (CRUD Bansos)",
        "Gambar 11. Tampilan Animasi Shimmer Skeleton Loading saat Pemuatan Data dari Cloud",
        "Gambar 12. Tampilan Pembaruan Kontras Warna dan Teks Tema pada Seluruh Komponen Antarmuka RT 06"
    ]

    for item in screenshots_a:
        # Placeholder frame / table for screenshot
        frame_tbl = doc.add_table(rows=1, cols=1)
        frame_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        frame_tbl.autofit = False
        cell = frame_tbl.rows[0].cells[0]
        cell.width = Inches(5.8)
        
        tcPr = cell._element.get_or_add_tcPr()
        # Light gray background
        shd = parse_xml(r'<w:shd {} w:fill="F8FAFC"/>'.format(nsdecls('w')))
        tcPr.append(shd)
        # Subtle dashed border
        borders = parse_xml(
            r'<w:tcBorders {}><w:top w:val="dashed" w:sz="4" w:space="0" w:color="CBD5E1"/><w:left w:val="dashed" w:sz="4" w:space="0" w:color="CBD5E1"/><w:right w:val="dashed" w:sz="4" w:space="0" w:color="CBD5E1"/><w:bottom w:val="dashed" w:sz="4" w:space="0" w:color="CBD5E1"/></w:tcBorders>'.format(nsdecls('w'))
        )
        tcPr.append(borders)

        cp = cell.paragraphs[0]
        cp.alignment = WD_ALIGN_PARAGRAPH.CENTER
        cp.paragraph_format.space_before = Pt(28)
        cp.paragraph_format.space_after = Pt(28)
        r_ph = cp.add_run("[ Tempatkan Tangkapan Layar / Screenshot HP di sini ]")
        set_font(r_ph, "Calibri", 10, italic=True, color=GRAY)

        # Caption
        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(4)
        p_cap.paragraph_format.space_after = Pt(14)
        p_cap.paragraph_format.keep_with_next = False
        r_cap = p_cap.add_run(item)
        set_font(r_cap, "Calibri", 10, bold=True, italic=True, color=DARK)

    # Sub B
    add_heading_styled("B. Dokumentasi Pengerjaan & Koordinasi Tim Pengembang:", level=2)

    screenshots_b = [
        "Gambar 13. Dokumentasi Diskusi Arsitektur Jaringan REST API dan Skema Database Supabase",
        "Gambar 14. Dokumentasi Implementasi Kode Retrofit, CRUD Repository, dan Jetpack Compose",
        "Gambar 15. Dokumentasi Pengujian Fitur Kamera, Reset Kata Sandi, dan WhatsApp pada Perangkat Android",
        "Gambar 16. Dokumentasi Evaluasi Hasil Kompilasi Akhir (Build Successful) Bersama Tim Pengembang"
    ]

    for item in screenshots_b:
        frame_tbl = doc.add_table(rows=1, cols=1)
        frame_tbl.alignment = WD_TABLE_ALIGNMENT.CENTER
        frame_tbl.autofit = False
        cell = frame_tbl.rows[0].cells[0]
        cell.width = Inches(5.8)
        
        tcPr = cell._element.get_or_add_tcPr()
        shd = parse_xml(r'<w:shd {} w:fill="F8FAFC"/>'.format(nsdecls('w')))
        tcPr.append(shd)
        borders = parse_xml(
            r'<w:tcBorders {}><w:top w:val="dashed" w:sz="4" w:space="0" w:color="CBD5E1"/><w:left w:val="dashed" w:sz="4" w:space="0" w:color="CBD5E1"/><w:right w:val="dashed" w:sz="4" w:space="0" w:color="CBD5E1"/><w:bottom w:val="dashed" w:sz="4" w:space="0" w:color="CBD5E1"/></w:tcBorders>'.format(nsdecls('w'))
        )
        tcPr.append(borders)

        cp = cell.paragraphs[0]
        cp.alignment = WD_ALIGN_PARAGRAPH.CENTER
        cp.paragraph_format.space_before = Pt(28)
        cp.paragraph_format.space_after = Pt(28)
        r_ph = cp.add_run("[ Tempatkan Foto Dokumentasi Tim di sini ]")
        set_font(r_ph, "Calibri", 10, italic=True, color=GRAY)

        p_cap = doc.add_paragraph()
        p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(4)
        p_cap.paragraph_format.space_after = Pt(14)
        r_cap = p_cap.add_run(item)
        set_font(r_cap, "Calibri", 10, bold=True, italic=True, color=DARK)

    output_filename = "LAPORAN_KEGIATAN_MINGGU_13.docx"
    doc.save(output_filename)
    print(f"File berhasil dibuat: {output_filename}")

if __name__ == "__main__":
    create_report()
