package uts.c14210065.proyekpaba

import android.content.Intent
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
        _rvHistory = findViewById(R.id.rvHistory)

        var currentDate = Calendar.getInstance().time
        dayDate = currentDate

        val _btnOGym = findViewById<Button>(R.id.btnOGym)
        val _btnOClass = findViewById<Button>(R.id.btnOClass)
        val _btnOTrainer = findViewById<Button>(R.id.btnOTrainer)

        _btnOGym.setOnClickListener {
            ReadData(dayDate, "Gym")
        }

        _btnOClass.setOnClickListener {
            ReadData(dayDate, "Class")
        }

        _btnOTrainer.setOnClickListener {
//            ReadData(dayDate, "Trainer")
        }



        _rvHistory.layoutManager = LinearLayoutManager(this)
        val adapterP = adapterHistory(arHistory)
        _rvHistory.adapter = adapterP


    }

    fun ReadData(date: Date, kategori: String){
        db.collection(kategori)
            .whereLessThanOrEqualTo("waktu", date)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // Handle each document here
                    val data = document.data
                    val userId =
                        (document["userId"] as? List<*>)?.map { it.toString() } ?: emptyList()
                    if(idLogin in userId){
                        arHistory.add(History(document.id.toString(),kategori + " " + document.get("nama").toString(), document.getTimestamp("waktu")) )
                    }
                    // Access data fields as needed, e.g., data["fieldName"]

                    Log.d("FirestoreData", "Document ID: ${document.id}, Data: $data")
                }
                Log.d("arHistory", arHistory.toString())
                arHistory.sortBy { it.timestamp }
                _rvHistory.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting documents: $exception")
            }
    }
}