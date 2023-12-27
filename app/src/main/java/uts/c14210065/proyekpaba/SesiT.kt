package uts.c14210065.proyekpaba

import com.google.firebase.Timestamp

data class SesiT(
    var idJadwal: String,
    var tanggal: String,
    var trainerId:String,
    var sesi:String,
    var timestamp: Timestamp,
    var userTrainerIdSesi: String,
    var userTrainerId: String,

)
