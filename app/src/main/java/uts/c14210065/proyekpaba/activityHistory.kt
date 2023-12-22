package uts.c14210065.proyekpaba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class activityHistory : AppCompatActivity() {

    companion object {
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    private var arHistoryG = arrayListOf<GymClass>()
    private var lastClickedButton: Button? = null
    lateinit var dayDate: Date
    var idLogin: String = ""
    private lateinit var buttons: Array<Button>
    private lateinit var buttonsCategory: Array<Button>
    lateinit var idTrainer : String
    lateinit var _rvHistory : RecyclerView
    lateinit var userTrainerId : String
    //    lateinit var _tvHari : TextView
    var state : Int = 0
    private var selectedCategoryButton: Button? = null
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        idLogin = intent.getStringExtra(utama.userId).toString()

        Log.d("trainer", idLogin)

        _rvHistory = findViewById<RecyclerView>(R.id.rvHistory)
        val _btnOGym = findViewById<Button>(R.id.btnOGym)
        val _btnOClass = findViewById<Button>(R.id.btnOClass)
        val _btnOTrainer = findViewById<Button>(R.id.btnOTrainer)
//        _tvHari.visibility = View.GONE


        // Get the current date
        var currentDate = Calendar.getInstance().time
        dayDate = currentDate

        buttonsCategory = arrayOf(_btnOGym, _btnOClass, _btnOTrainer)

//        _btnOGym.setOnClickListener {
//            state = 1
//            changeButtonColor(_btnOGym)
//            Log.d("mmm", "state1 : $state")
//            setupRecyclerView()
//        }
//
//        _btnOClass.setOnClickListener {
//            state = 2
//            changeButtonColor(_btnOClass)
//            Log.d("mmm", "state2 : $state")
//            setupRecyclerView()
//        }
//
//        _btnOTrainer.setOnClickListener {
//            state = 3
//            changeButtonColor(_btnOTrainer)
//            Log.d("mmm", "state3 : $state")
//            setupRecyclerView()
//        }
        val currentTimestamp: Long = System.currentTimeMillis()
        TampilkanDataGym(currentTimestamp, "Class")

        Log.d("error1", "iya")
        _rvHistory.layoutManager = LinearLayoutManager(this)
        Log.d("error2", "iya")
        val adapterP = adapterHistory(arHistoryG)
        Log.d("error3", "iya")
        _rvHistory.adapter = adapterP
        Log.d("erro4", "iya")

    }

    private fun TampilkanDataGym(date: Long, kategori:String) {
        db.collection(kategori).get().addOnSuccessListener { result ->
//            arHistoryG.clear()
            for (document in result) {
                var name = document.getString("nama").toString()
                val tanggal = (document["waktu"] as? Timestamp)?.toDate()
                val timestamp = document.getTimestamp("waktu")
//                val kuotaSisa = document.getLong("kuotaSisa")?.toInt() ?: 0

//                if (cekDate(tanggal, date)) {
                val userId =
                    (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()

                if (idLogin in userId) {
                    val calendar = Calendar.getInstance()
                    calendar.time = tanggal

                    // ambil jam dan menit dari timestamp tanggal di database

                    val jam = calendar.get(Calendar.HOUR_OF_DAY)
                    val menit = calendar.get(Calendar.MINUTE)

                    //agar jadi 08.00 atau 17.00
                    val formatJam = String.format("%02d", jam)
                    val formatMenit = String.format("%02d", menit)

                    val sesi = "$formatJam:$formatMenit"

                    //jam sekarang
                    val currentTime = Calendar.getInstance()
                    val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                    val currentMinute = currentTime.get(Calendar.MINUTE)

                    //tanggal diubah dengan format
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                    dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                    val formattedDate = dateFormat.format(tanggal)
                    val currentDate = dateFormat.format(Date())

                    //jam ditambah 2 jam
                    calendar.add(Calendar.HOUR_OF_DAY, 2)
                    val jamm = calendar.get(Calendar.HOUR_OF_DAY)

                    // Check if the booking date has passed the current date
                    if (timestamp != null && timestamp.seconds < date / 1000) {
                        Log.d("ppp", "masuk1")
//                        arHistoryG.add(
//                            GymClass(
//                            document.id, name, document.get("kapasitas").toString().toInt(),
//                            document.get("durasi").toString().toInt(), document.getString("pelatih").toString(),
//                            document.getTimestamp("waktu"), document.getString("level").toString(), emptyList()
//                            ))
                        if (jamm > currentHour) {
                            Log.d("ppp", "masuk2")
//                            arHistoryG.add(
//                                GymClass(
//                                    document.id, name, document.get("kapasitas").toString().toInt(),
//                                    document.get("durasi").toString().toInt(), document.getString("pelatih").toString(),
//                                    document.getTimestamp("waktu"), document.getString("level").toString(), emptyList()
//                                )
//                            )
                            Log.d("haes", "Document data: ${document.data}")
                            Log.d("haes", formattedDate)
                        }
                        if(jamm == currentHour){
                            if (menit > currentMinute) {
                                Log.d("ppp", "masuk3")
//                                arHistoryG.add(
//                                    GymClass(
//                                        document.id, name, document.get("kapasitas").toString().toInt(),
//                                        document.get("durasi").toString().toInt(), document.getString("pelatih").toString(),
//                                        document.getTimestamp("waktu"), document.getString("level").toString(), emptyList()
//                                    )
//                                )
                                Log.d("haes", "Document data: ${document.data}")
                                Log.d("haes", formattedDate)
                            }
                        }
                    } else {
//                        arHistoryG.add(
//                            GymClass(
//                                document.id, name, document.get("kapasitas").toString().toInt(),
//                                document.get("durasi").toString().toInt(), document.getString("pelatih").toString(),
//                                document.getTimestamp("waktu"), document.getString("level").toString(), emptyList()
//                            )
//                        )
                        Log.d("haes", "Document data: ${document.data}")
                        Log.d("haes", formattedDate)
                    }
                }
//                }
            }
            Log.d("haes", "arHistoryG: ${arHistoryG}")
            arHistoryG.sortBy { it.timestamp }
            _rvHistory.adapter?.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e("haes", "Error fetching data from Firebase", e)
        }
    }
}