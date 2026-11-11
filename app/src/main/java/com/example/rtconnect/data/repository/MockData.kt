package com.example.rtconnect.data.repository

import com.example.rtconnect.data.model.*

object MockData {
    val currentUser = Warga(
        id = "w1",
        namaKepalaKeluarga = "Budi Santoso",
        noKk = "3273010101900001",
        blokRumah = "Blok B No. 12",
        noHp = "081234567890",
        jumlahAnggota = 4,
        statusEkonomi = StatusEkonomi.REGULER,
        statusIuran = StatusIuran.BELUM_LUNAS
    )

    val currentTagihan = TagihanIuran(
        id = "t1",
        judul = "IURAN SAMPAH • NOVEMBER",
        periodeNama = "November 2026",
        nominal = 20000,
        batasWaktu = "15 November 2026",
        status = StatusIuran.BELUM_LUNAS
    )

    val riwayatTagihan = listOf(
        TagihanIuran(
            id = "t-okt",
            judul = "Iuran Sampah & Keamanan",
            periodeNama = "Oktober 2026",
            nominal = 20000,
            batasWaktu = "15 Okt 2026",
            status = StatusIuran.LUNAS,
            tanggalBayar = "10 Okt 2026",
            metodeBayar = "QRIS"
        ),
        TagihanIuran(
            id = "t-sep",
            judul = "Iuran Sampah & Keamanan",
            periodeNama = "September 2026",
            nominal = 20000,
            batasWaktu = "15 Sep 2026",
            status = StatusIuran.LUNAS,
            tanggalBayar = "12 Sep 2026",
            metodeBayar = "Transfer Bank"
        ),
        TagihanIuran(
            id = "t-agu",
            judul = "Iuran Sampah & Keamanan",
            periodeNama = "Agustus 2026",
            nominal = 20000,
            batasWaktu = "15 Agu 2026",
            status = StatusIuran.LUNAS,
            tanggalBayar = "08 Agu 2026",
            metodeBayar = "Tunai ke Bendahara"
        ),
        TagihanIuran(
            id = "t-jul",
            judul = "Iuran Sampah & Keamanan",
            periodeNama = "Juli 2026",
            nominal = 20000,
            batasWaktu = "15 Jul 2026",
            status = StatusIuran.LUNAS,
            tanggalBayar = "11 Jul 2026",
            metodeBayar = "QRIS"
        )
    )

    val pengumumanList = listOf(
        Pengumuman(
            id = "p1",
            judul = "Kerja bakti bersama",
            isi = "Dihimbau kepada seluruh bapak/ibu warga RT 10 untuk mengikuti kegiatan kerja bakti pembersihan saluran air menjelang musim hujan.",
            tanggal = "15 Nov 2026",
            waktu = "07.00 WIB",
            lokasi = "Pos RT 10",
            penting = true
        ),
        Pengumuman(
            id = "p2",
            judul = "Pertemuan Bulanan & Arisan RT",
            isi = "Pertemuan rutin bulanan warga RT 10 membahas laporan kas dan program posyandu lansia.",
            tanggal = "20 Nov 2026",
            waktu = "19.30 WIB",
            lokasi = "Balai Warga RT 10"
        ),
        Pengumuman(
            id = "p3",
            judul = "Jadwal Fogging Nyamuk DBD",
            isi = "Puskesmas akan melaksanakan pengasapan (fogging) nyamuk DBD. Mohon tutup makanan dan tempat penampungan air.",
            tanggal = "25 Nov 2026",
            waktu = "08.30 WIB",
            lokasi = "Seluruh Area RT 10"
        )
    )

    val laporanList = listOf(
        Laporan(
            id = "l1",
            judul = "Lampu Penerangan Jalan Mati",
            kategori = KategoriLaporan.KEAMANAN,
            deskripsi = "Lampu jalan di pertigaan Blok B mati sejak 2 hari lalu, kondisi jalan cukup gelap dan rawan.",
            lokasi = "Pertigaan Blok B No. 10-14",
            tanggal = "09 Nov 2026",
            status = StatusLaporan.DIPROSES,
            pelapor = "Budi Santoso"
        ),
        Laporan(
            id = "l2",
            judul = "Saluran Air Tersumbat Sampah",
            kategori = KategoriLaporan.LINGKUNGAN,
            deskripsi = "Saluran air depan gang C tersumbat daun dan lumpur, air meluap saat hujan deras kemarin sore.",
            lokasi = "Depan Gang C RT 10",
            tanggal = "07 Nov 2026",
            status = StatusLaporan.SELESAI,
            pelapor = "Joko Widodo"
        ),
        Laporan(
            id = "l3",
            judul = "Pohon Tumbang Sebagian",
            kategori = KategoriLaporan.FASILITAS,
            deskripsi = "Dahan pohon rindang dekat lapangan hampir patah dan menghalangi kabel listrik.",
            lokasi = "Dekat Lapangan RT 10",
            tanggal = "10 Nov 2026",
            status = StatusLaporan.BARU,
            pelapor = "Hendra Setiawan"
        )
    )

    val masterWargaList = listOf(
        Warga(
            id = "w1",
            namaKepalaKeluarga = "Budi Santoso",
            noKk = "3273010101900001",
            blokRumah = "Blok B No. 12",
            noHp = "081234567890",
            jumlahAnggota = 4,
            statusEkonomi = StatusEkonomi.REGULER,
            statusIuran = StatusIuran.BELUM_LUNAS
        ),
        Warga(
            id = "w2",
            namaKepalaKeluarga = "Ahmad Fauzi",
            noKk = "3273010101900002",
            blokRumah = "Blok A No. 04",
            noHp = "081298765432",
            jumlahAnggota = 3,
            statusEkonomi = StatusEkonomi.REGULER,
            statusIuran = StatusIuran.LUNAS
        ),
        Warga(
            id = "w3",
            namaKepalaKeluarga = "Siti Rahmawati",
            noKk = "3273010101900003",
            blokRumah = "Blok C No. 08",
            noHp = "081311223344",
            jumlahAnggota = 2,
            statusEkonomi = StatusEkonomi.SUBSIDI,
            statusIuran = StatusIuran.LUNAS
        ),
        Warga(
            id = "w4",
            namaKepalaKeluarga = "Hendra Gunawan",
            noKk = "3273010101900004",
            blokRumah = "Blok B No. 01",
            noHp = "081399887766",
            jumlahAnggota = 5,
            statusEkonomi = StatusEkonomi.PENGUSAHA,
            statusIuran = StatusIuran.LUNAS
        ),
        Warga(
            id = "w5",
            namaKepalaKeluarga = "Dedi Supardi",
            noKk = "3273010101900005",
            blokRumah = "Blok D No. 15",
            noHp = "081255443322",
            jumlahAnggota = 4,
            statusEkonomi = StatusEkonomi.REGULER,
            statusIuran = StatusIuran.BELUM_LUNAS
        ),
        Warga(
            id = "w6",
            namaKepalaKeluarga = "Nurhasanah",
            noKk = "3273010101900006",
            blokRumah = "Blok C No. 02",
            noHp = "081277665544",
            jumlahAnggota = 1,
            statusEkonomi = StatusEkonomi.SUBSIDI,
            statusIuran = StatusIuran.LUNAS
        )
    )

    val bantuanSosialList = listOf(
        BantuanSosial("b1", "Bansos Beras & Sembako Bulanan", "November 2026", "Warga Lansia & Subsidi RT 10", "Tersalurkan", 12),
        BantuanSosial("b2", "Bantuan Kesehatan & Posyandu Balita", "Oktober 2026", "Balita & Ibu Menyusui", "Selesai", 18),
        BantuanSosial("b3", "Santunan Dana Pendidikan Warga", "Semester Genap 2026", "Keluarga Prasejahtera", "Pendaftaran", 6)
    )
}
