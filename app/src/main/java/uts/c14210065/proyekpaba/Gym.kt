package uts.c14210065.proyekpaba

import com.google.firebase.Timestamp

data class Gym(
    var idGym: String,
    var tanggal: String,
    var sesi:String,
    val timestamp: Timestamp,
    var kuotaMax: Int,
    var kuotaSisa: Int,
    var userId: ArrayList<String>
)
