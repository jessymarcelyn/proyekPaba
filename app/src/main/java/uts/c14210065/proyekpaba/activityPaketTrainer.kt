package uts.c14210065.proyekpaba

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import uts.c14210065.proyekpaba.model.TrainerModel

class activityPaketTrainer : AppCompatActivity() {

    lateinit var btnBack: ImageView

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paket_trainer)

        btnBack = findViewById(R.id.btnDetTrainerBack)
        btnBack.setOnClickListener {
            val fragment = fTrainer()
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, fragment)
            transaction.commit()
        }

        val documentId = intent.getStringExtra("documentId")
        Log.d("PaketTrainer", "documentId: $documentId")

    }
}