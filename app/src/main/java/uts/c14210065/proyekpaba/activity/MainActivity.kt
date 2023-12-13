package uts.c14210065.proyekpaba.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import uts.c14210065.proyekpaba.R


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    companion object{
        const val login = "GETDATA"
        const val userId = "GETID"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val _btnLewati = findViewById<TextView>(R.id.btnLewati)
        val _btnLogin = findViewById<TextView>(R.id.btnLogin)
        val _btnRegister = findViewById<TextView>(R.id.btnRegister)

        _btnLewati.setOnClickListener{
            val intentWithData = Intent(this@MainActivity, utama::class.java).apply {
                putExtra(utama.login, false)
                putExtra(utama.userId, "0")
//                putExtra(login, true)
            }
            startActivity(intentWithData)
        }

        _btnLogin.setOnClickListener{
            val intentWithData = Intent(this@MainActivity, Login::class.java).apply {
                putExtra(utama.login, false)
                putExtra(utama.userId, "0")
//                putExtra(login, true)
            }
            startActivity(intentWithData)
        }


    }
}