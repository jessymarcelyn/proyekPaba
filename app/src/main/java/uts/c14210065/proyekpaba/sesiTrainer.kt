package uts.c14210065.proyekpaba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class sesiTrainer : AppCompatActivity() {
    companion object{
        const val login = "GETDATA"
        const val userId = "GETID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sesi_trainer)

        val idLogin = intent.getStringExtra(utama.userId).toString()
        Log.d("trainer", idLogin)


    }
}