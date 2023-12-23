package uts.c14210065.proyekpaba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        idLogin = intent.getStringExtra(utama.userId).toString()
        _rvHistory = findViewById(R.id.rvHistory)

        var currentDate = Calendar.getInstance().time
        dayDate = currentDate

        Log.d("dayDate", dayDate.toString())


    }

}