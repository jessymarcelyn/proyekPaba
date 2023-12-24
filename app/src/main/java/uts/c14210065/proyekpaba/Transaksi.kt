package uts.c14210065.proyekpaba

data class Transaksi(
    var idTransaksi: String,
    var tanggal: String,
    var metode:String,
    var idPaket:String,
    var durasi: Int,
    var totalSesi: Int,
    var idUserTrainer: String
)
