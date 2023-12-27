package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class inputJadwalTrainer : AppCompatActivity() {
    lateinit var dateSelect: String
    lateinit var jamSelect: String
    lateinit var btnSave: Button
    lateinit var btnBack: ImageView

    var idLogin: String = ""
    var db = Firebase.firestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_jadwal_trainer)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupJam)
        btnSave = findViewById(R.id.btnSaveDateJadwal)
        btnBack = findViewById(R.id.btnInputBack)

        btnBack.setOnClickListener {
            val intent = Intent(this, activityTrainer::class.java)
            intent.putExtra(activityTrainer.login, true)
            intent.putExtra(activityTrainer.userId, idLogin)
            startActivity(intent)
        }

        idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("inputjadwal", "login: $idLogin")

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateSelect = dateFormat.format(calendar.time)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            jamSelect = radioButton.text.toString()
        }

        btnSave.setOnClickListener {
            SimpanTanggalJam(dateSelect, jamSelect)
        }
    }

    private fun SimpanTanggalJam(tgl: String, jam: String) {
        db.collection("JadwalTrainer").get().addOnSuccessListener { result ->
            var docId: String? = null

            val addData = hashMapOf(
                "trainerId" to idLogin,
                "tanggal" to tgl,
                "jam" to jam
            )

            val setData = hashMapOf(
                "tanggal" to tgl,
                "jam" to jam
            )

            for (document in result) {
                var trainerId = document.getString("trainerId")

                if (trainerId == idLogin) {
                    docId = document.id
                    break
                }
            }
//            if (docId == null) {
                db.collection("JadwalTrainer").add(addData).addOnSuccessListener {
                    Log.d("inputJadwal", "berhasil add data.")
                }
//            }
//            else {
//                db.collection("UserTrainer").document().set(setData).addOnSuccessListener {
//                    Log.d("inputJadwal", "berhasil set data. docid: $docId")
//                }
//            }
        }
    }
}