package uts.c14210065.proyekpaba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class activityJadwalTrainer : AppCompatActivity() {
    lateinit var _rvJadwalT : RecyclerView
    private var arJadwalT = arrayListOf<SesiT>()
    lateinit var idLogin: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jadwal_trainer)
        idLogin = intent.getStringExtra(activityTrainer.userId).toString()
        _rvJadwalT = findViewById(R.id.rvJadwalTrainer)

        Log.d("idLogin", idLogin)
        ReadData(idLogin)

        _rvJadwalT.layoutManager = LinearLayoutManager(this)
        val adapterP = adapterJadwalTrainer(arJadwalT)
        _rvJadwalT.adapter = adapterP
    }

    private fun ReadData(id:String){
        val db = Firebase.firestore
        db.collection("JadwalTrainer")
            .whereEqualTo("trainerId", id)
            .get().addOnSuccessListener { result ->
            arJadwalT.clear()
            for (document in result) {
                var idJadwal = document.id
                val idTrainer = document.getString("trainerId") ?: ""

//                if(idTrainer == idLogin) {

                    val tanggal = (document["tanggal"] as? Timestamp)?.toDate()
                    val calendar = Calendar.getInstance()
                    calendar.time = tanggal
                    val timestamp = document.getTimestamp("tanggal")!!

                    // ambil jam dan menit dari timestamp tanggal di database
                    val jam = calendar.get(Calendar.HOUR_OF_DAY)
                    val menit = calendar.get(Calendar.MINUTE)

                        //agar jadi 08.00 atau 17.00
                    val formatJam = String.format("%02d", jam)
                    val formatMenit = String.format("%02d", menit)

                    val sesi = "$formatJam:$formatMenit"

                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val formattedDate = dateFormat.format(tanggal)

                    val userTrainerId = document.get("userTrainerId").toString()
                    arJadwalT.add(
                        SesiT(
                            idJadwal,
                            formattedDate,
                            idTrainer,
                            sesi, timestamp,
                            "apakah ini?", userTrainerId
                        )
                    )

                    Log.d("xxxx", "arHistory : " + arJadwalT.toString())
//                }

            }

            arJadwalT.sortBy{ it.timestamp }
            _rvJadwalT.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
    }

}