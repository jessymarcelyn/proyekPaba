package uts.c14210065.proyekpaba

data class GymClass(
    var idClass: String,
    var name:String,
    var capacity:Int,
    var duration:Int,
    var coach:String,
    var timestamp: com.google.firebase.Timestamp?,
    var level:String
)
