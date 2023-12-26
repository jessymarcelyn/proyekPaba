package uts.c14210065.proyekpaba

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaksi(
    var idTransaksi: String,
    var tanggalBeliTimeStamp: Long,
    var status: Int,
    var durasi: Int,
    var harga: Int,
    var idPaket: String,
    var idTrainer:String,
    var idUser:String,
    var jenisPembayaran: String,
    var jenisMember: String,
    var pilihan: String,
    var tanggalBeli: String,
    var tanggalMulai: String,
    var tanggalBerakhir: String,
    var totalSesi:Int
) : Parcelable
