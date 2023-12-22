package uts.c14210065.proyekpaba

data class GymClass(
    var idClass: String,
    var name:String,
    var capacity:Int,
    var durasi:Int,
    var coach:String,
//    var waktu:String,
    var timestamp: com.google.firebase.Timestamp?,
    var level:String,
    val arrUser: List<String>
)
