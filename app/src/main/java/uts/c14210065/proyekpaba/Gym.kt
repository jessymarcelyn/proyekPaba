package uts.c14210065.proyekpaba

data class Gym(
    var idGym: String,
    var tanggal: String,
    var sesi:String,
//    val endTime: String,
    var kuotaMax: Int,
    var kuotaSisa: Int,
    var userId: ArrayList<String>

)
