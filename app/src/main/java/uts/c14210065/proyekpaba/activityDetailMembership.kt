package uts.c14210065.proyekpaba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class activityDetailMembership : AppCompatActivity() {
    val db = Firebase.firestore
    lateinit var idLogin : String
    lateinit var _tvJenis : TextView
    lateinit var _tvTMulai : TextView
    lateinit var _tvTAkhir : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_membership)

        idLogin = intent.getStringExtra(utama.userId).toString()

        _tvJenis = findViewById<TextView>(R.id.tvJenis)
        _tvTMulai = findViewById<TextView>(R.id.tvTanggalMulai)
        _tvTAkhir = findViewById<TextView>(R.id.tvTanggalBerakhir)
        ReadData(idLogin)


    }

    private fun ReadData(userid : String){
        db.collection("Member").whereEqualTo("userId", userid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val jenis = document.getString("jenisMember").toString()
//                        val selectedDate = document.getTimestamp("tanggalMulai")?.toDate()

                        Log.d("jenis", jenis)

                    val tanggalMulai = (document["tanggalMulai"] as? Timestamp)?.toDate()?.time ?: 0
                    var durasi : Int
                    if (jenis == "bronze"){
                        durasi = 6
                    }else  if (jenis == "silver"){
                        durasi = 12
                    } else  if (jenis == "gold"){
                        durasi = 18
                    }else{
                        durasi = 24
                    }
                    val tanggalBerakhir = addMonthsToTimestamp(tanggalMulai, durasi)

                    // Format time
                    val timeFormat = android.icu.text.SimpleDateFormat("HH:mm", Locale("id", "ID"))
                    timeFormat.timeZone = android.icu.util.TimeZone.getTimeZone("Asia/Jakarta")
                    val formattedTimeS = timeFormat.format(tanggalMulai)
                    val formattedTimeE = timeFormat.format(tanggalBerakhir)
                    Log.d("MyApp", "Formatted Time: $formattedTimeS $formattedTimeE")

                    // Format date
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val formattedDateS = dateFormat.format(tanggalMulai)
                    val formattedDateE = dateFormat.format(tanggalBerakhir)
                    val formattedDateTimeStart = "$formattedDateS $formattedTimeS"
                    val formattedDateTimeEnd = "$formattedDateE $formattedTimeE"
                    Log.d("forma date time s", formattedDateTimeStart)
                    Log.d("forma date time e", formattedDateTimeEnd)

                    _tvJenis.text = jenis
                    _tvTAkhir.text = formattedDateTimeEnd
                    _tvTMulai.text = formattedDateTimeStart
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting documents: $exception")
            }
    }
    fun addMonthsToTimestamp(originalTimestamp: Long, monthsToAdd: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = originalTimestamp
        calendar.add(Calendar.MONTH, monthsToAdd)
        return calendar.time
    }

}