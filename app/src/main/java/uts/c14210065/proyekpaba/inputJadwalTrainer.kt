package uts.c14210065.proyekpaba

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class inputJadwalTrainer : AppCompatActivity() {
    var dateSelect: String = ""
    lateinit var jamSelect: String
    lateinit var btnSave: Button
    lateinit var btnBack: ImageView
    lateinit var warningTgl: TextView
    lateinit var warningJam: TextView

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

        warningTgl = findViewById(R.id.warningTgl)
        warningJam = findViewById(R.id.warningJam)

        jamSelect = ""

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
                when (checkedId) {
                    R.id.jam1 -> jamSelect = "08:00:00"
                    R.id.jam2 -> jamSelect = "10:00:00"
                    R.id.jam3 -> jamSelect = "14:00:00"
                    R.id.jam4 -> jamSelect = "16:00:00"
                }
                Log.d("inputJam", "jamselect: $jamSelect")
        }
        Log.d("inputJam", "jamselect: $jamSelect")

        btnSave.setOnClickListener {
            dateSelect?.let { date ->
                SimpanTanggalJam(date, jamSelect)
            }
        }
    }

    private fun SimpanTanggalJam(tgl: String, jam: String) {

        val selectedCalendar = Calendar.getInstance()
        if (tgl.isNotEmpty() && jam.isNotEmpty()) {
            selectedCalendar.time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse("$tgl $jam")!!

            val currDate = Calendar.getInstance().time
            if (selectedCalendar.time < currDate) {
                Toast.makeText(this, "Tanggal dan jam harus lebih dari sekarang.", Toast.LENGTH_SHORT).show()
                Log.e("inputJadwal", "Tanggal harus lebih dari hari ini. tgl: $tgl jam: $jam")
                return
            }
            val monthDate = Calendar.getInstance()
            monthDate.add(Calendar.MONTH, 6)

            if (selectedCalendar.after(monthDate)) {
                Toast.makeText(this, "Tanggal tidak boleh lebih dari 6 bulan dari sekarang.", Toast.LENGTH_SHORT).show()
                Log.e("inputJadwal", "Tanggal tidak boleh lebih dari 6 bulan dari sekarang. tgl: $tgl jam: $jam")
                return
            }
        } else {
            if (tgl.isEmpty()) {
                warningTgl.visibility = View.VISIBLE
            }
            else if (jam.isEmpty()) {
                warningJam.visibility = View.VISIBLE
            }
            Log.e("inputJadwal", "tgl or jam is empty. tgl: $tgl jam: $jam")
            return
        }

        val timestamp = Timestamp(selectedCalendar.time)

        db.collection("JadwalTrainer").get().addOnSuccessListener { result ->
            var docId: String? = null

            for (document in result) {
                var trainerId = document.getString("trainerId")
                var timestmp = document.getTimestamp("tanggal")

                if (trainerId == idLogin && timestmp == timestamp) {
                    docId = document.id
                    break
                }
            }

            var utid = ""
            val addData = hashMapOf(
                "trainerId" to idLogin,
                "tanggal" to timestamp,
                "userTrainerId" to utid
            )
            if (docId == null) {
                db.collection("JadwalTrainer").add(addData).addOnSuccessListener {
                    Toast.makeText(this, "Jadwal berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                    Log.d("inputJadwal", "berhasil add data.")
                }
            }
            else {
                Toast.makeText(this, "Jadwal sudah ada", Toast.LENGTH_SHORT).show()
                Log.d("inputJadwal", "jadwal sdh ada. docid: $docId")
            }
        }
    }
}