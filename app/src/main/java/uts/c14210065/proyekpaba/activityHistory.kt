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

    lateinit var _rvHistory : RecyclerView
    lateinit var idLogin : String
    lateinit var dayDate: Date
    val db = Firebase.firestore
    private var arHistory= arrayListOf<History>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        idLogin = intent.getStringExtra(utama.userId).toString()
        _rvHistory = findViewById(R.id.rvHistoryP)

        var currentDate = Calendar.getInstance().time
        dayDate = currentDate

        val _btnOGym = findViewById<Button>(R.id.btnOGym)
        val _btnOClass = findViewById<Button>(R.id.btnOClass)
        val _btnOTrainer = findViewById<Button>(R.id.btnOTrainer)

        _btnOGym.setOnClickListener {
            ReadData(dayDate, "GymSesi")
        }

        _btnOClass.setOnClickListener {
            ReadData(dayDate, "Class")
        }

        _btnOTrainer.setOnClickListener {
            ReadDataTrainer(dayDate)
        }



        _rvHistory.layoutManager = LinearLayoutManager(this)
        val adapterP = adapterHistory(arHistory)
        _rvHistory.adapter = adapterP


    }

    private fun ReadData(date: Date, kategori: String){
        db.collection(kategori)
            .whereLessThanOrEqualTo("tanggal", date)
            .get()
            .addOnSuccessListener { result ->
                arHistory.clear()
                for (document in result) {
                    // Handle each document here
                    val data = document.data
                    val userId =
                        (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()
                    if(idLogin in userId){

                        val id = document.id
                        var title : String
                        if(kategori =="GymSesi"){
                            title = "RESERVED"
                        }else if(kategori =="Class"){
                            title = document.get("nama").toString() + " with " + document.get("pelatih").toString()
                        }else{
                            title = document.get("nama").toString()

                        }

                        val selectedDate = document.getTimestamp("tanggal")?.toDate()
                        Log.d("MyApp", "Selected Date: $selectedDate")

                        // Format time
                        val timeFormat = android.icu.text.SimpleDateFormat("HH:mm", Locale("id", "ID"))
                        timeFormat.timeZone = android.icu.util.TimeZone.getTimeZone("Asia/Jakarta")
                        val formattedTime = timeFormat.format(selectedDate)
                        Log.d("MyApp", "Formatted Time: $formattedTime")

//                        // Format date
                        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                        val formattedDate = dateFormat.format(selectedDate)

                        // Combine date and time
                        val formattedDateTime = "$formattedDate $formattedTime"

//                        val formattedDateTime = document.getTimestamp("waktu").toString()
                        Log.d("forma date time", formattedDateTime)

                        arHistory.add(History(id, title, formattedDateTime) )
                    }
                    // Access data fields as needed, e.g., data["fieldName"]

                    Log.d("FirestoreData", "Document ID: ${document.id}, Data: $data")
                }
                Log.d("arHistory", arHistory.toString())
                arHistory.sortByDescending { it.timestamp }
                _rvHistory.adapter?.notifyDataSetChanged()
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
    fun cekDate(timestamp: Date?, btnDate: Date): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM", Locale("id", "ID"))
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

        val formattedDate = dateFormat.format(timestamp)
        val date = dateFormat.format(btnDate)

        Log.d("cektanggal", "format" + formattedDate)
        Log.d("cektanggal", "button " + date)

        return formattedDate == date
    }
    private lateinit var idTrainer: String

    private fun ReadDataTrainer(btnDate: Date) {
        arHistory.clear()
        db.collection("UserTrainer").get().addOnSuccessListener { userTrainerResult ->
            for (userTrainerDocument in userTrainerResult) {
                var userTrainerId = userTrainerDocument.id
                Log.d("rrr", "UserTrainerId : $userTrainerId")
                var idUser = userTrainerDocument.getString("idUser") ?: ""
                var durasi = userTrainerDocument.getLong("durasiPaket")?.toInt() ?: 0

                val tanggalMulai =
                    (userTrainerDocument["tanggalMulai"] as? Timestamp)?.toDate()?.time ?: 0

                val tanggalBerakhir = addMonthsToTimestamp(tanggalMulai, durasi)

                idTrainer = userTrainerDocument.getString("idTrainer") ?: ""

                if (idUser == idLogin && tanggalBerakhir < btnDate) {
                    Log.d("sesiTrainerr", "masuk")
                    userTrainerId = userTrainerDocument.id

                    fetchJadwalTrainer(userTrainerId, idTrainer)

                } else {
                    Log.d("sesiTrainerr", "tidak masuk: idUser=$idUser, idLogin=$idLogin, tanggalBerakhir=$tanggalBerakhir, currentDate=$dayDate")
                }
            }

        }
    }

    private fun fetchJadwalTrainer(userTrainerId: String, idTrainer:String) {
        db.collection("JadwalTrainer")
            .get().addOnSuccessListener { document ->
                for (jadwalTrainer in document) {
                    val resultUserTrainerId = jadwalTrainer.get("userTrainerId").toString()
                    if (resultUserTrainerId == userTrainerId){
                        var jadwalTrainerId = jadwalTrainer.id

                        val trainerId = jadwalTrainer.getString("trainerId") ?: ""
                        val userTrainerIdd = jadwalTrainer.getString("userTrainerId") ?: ""

                        if (trainerId == idTrainer && userTrainerId == userTrainerIdd) {
                            Log.d("bbb", "masuk")
                            val selectedDate = jadwalTrainer.getTimestamp("tanggal")?.toDate()
                            Log.d("MyApp", "Selected Date: $selectedDate")

                            // Format time
                            val timeFormat = android.icu.text.SimpleDateFormat("HH:mm", Locale("id", "ID"))
                            timeFormat.timeZone = android.icu.util.TimeZone.getTimeZone("Asia/Jakarta")
                            val formattedTime = timeFormat.format(selectedDate)
                            Log.d("MyApp", "Formatted Time: $formattedTime")

//                        // Format date
                            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                            dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                            val formattedDate = dateFormat.format(selectedDate)

                            // Combine date and time
                            val formattedDateTime = "$formattedDate $formattedTime"
                            getTrainer(idTrainer, jadwalTrainerId, formattedDateTime)

                        }
                    }

                }
            }.addOnFailureListener { e ->
                Log.e("haes", "Error fetching data from Firebase", e)
            }
    }

    private fun getTrainer(idTrainer:String,jadwalTrainerId:String, tanggal:String){
        db.collection("Trainer").document(idTrainer).get()
            .addOnSuccessListener { result ->
                val nama = result.getString("nama").toString()
                arHistory.add(
                    History(
                        jadwalTrainerId,
                        nama,
                        tanggal
                    )
                )
                arHistory.sortBy { it.timestamp }
                _rvHistory.adapter?.notifyDataSetChanged()
                Log.d("arHis", arHistory.toString())
            }
    }
}