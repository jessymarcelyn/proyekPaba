package uts.c14210065.proyekpaba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    companion object{
        const val login = "GETDATA"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val _btnLewati = findViewById<TextView>(R.id.btnLewati)

        _btnLewati.setOnClickListener{
            val intentWithData = Intent(this@MainActivity, utama::class.java).apply {
                putExtra(login, false)
//                putExtra(login, true)

            }
            startActivity(intentWithData)
        }


    }
}